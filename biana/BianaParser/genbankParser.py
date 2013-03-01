from bianaParser import *
import re
import time


class GenBankParser(BianaParser):
    """
    GenBank Parser class
    """

    name = "GenBank"
    description = "This file implements the parser that fills up tables in BIANA database with information from GenBank"
    external_entity_definition = "sequence entries (rna, genes, proteins...)"
    external_entity_relations = ""


    version_re = re.compile("(\w+)\.(\d+)\s+GI:(\d+)") #AP008229.1  GI:84365597
    taxid_re = re.compile("db_xref=\"taxon:(\d+)")     #db_xref="taxon:367928
    mol_type_re = re.compile("mol_type=\"([\w\s]+)\"")      #mol_type="genomic DNA"

    feature_field_re = re.compile("\/(\w+)=(.+)\n")

    cds_gi_re = re.compile("db_xref=\"GI:(\d+)")                         # /db_xref="GI:84365712"
    cds_accession_number = re.compile("/protein_id=\"(\w+)\.(\d+)")      #BAE66870.1"
    cds_genesymbol = re.compile("/gene=\"(\w+)\"")
    cds_sequence = re.compile("translation=\"([\s+\w+])\"")                
    

    def __init__(self):

        # Start with the default values

        BianaParser.__init__(self, default_db_description = GenBankParser.name,
                             default_script_name = "genbankParser.py",
                             default_script_description = GenBankParser.description)

        self.default_eE_attribute = "accessionNumber"
        self.initialize_input_file_descriptor()


    def parse_database(self):

        self.biana_access.add_valid_external_entity_type( type = "genomic DNA" )
        self.biana_access.add_valid_external_entity_relation_type( type = "codifies" )
        self.biana_access.refresh_database_information()

        def insert_seq_files(arg,dirname,names):

            for name in names:
                #print
                #print "File: ",name
                #print
                if name.endswith('.seq'):
                    file_fd = open(os.path.join(dirname,name),'r')
                #elif name.endswith('.seq.gz'):
                #    file_fd = gzip.open(os.path.join(dirname,name),'r')
                else:
                    continue

                try:
                    self.parse_seq_file(file_fd)
                except:
                    sys.stderr.write("Error parsing file %s\n" %name)
                    traceback.print_exc()

                file_fd.close()

        os.path.walk(self.input_file,insert_seq_files,None)


    def parse_seq_file(self, file_fd):

        block = None
        current_field = None
        fields = {}
        sequence = []

        done = 0

        itime = time.time()

        for line in file_fd:

            if line.startswith("//"):
                
                #print "\n".join( [ "%s\t%s" %(x,y) for x, y in fields.iteritems() ] )
                #print "SEQUENCE: ",sequence
                #print fields["cds"]

                done += 1

                if done%10000==0:
                    print done, "done in %s seconds" %(time.time()-itime)
                    itime = time.time()

                #print fields

                # PROCESS FEATURES
                features = {}
                for x in fields["features"]:
                    t = x[5:12].strip()
                    if t != "":
                        current_field = t.lower()
                        #features[current_field] = {}
                        features.setdefault(current_field,[]).append({})
                        continue

                    if x[21]=="/":
                        m = GenBankParser.feature_field_re.match(x[21:])
                        if m:
                            current_subfield = m.group(1).lower()
                            #features[current_field][current_subfield] = [m.group(2).strip("\"")]
                            #features[current_field].append( {} )
                            features[current_field][-1].setdefault(current_subfield,[]).append(m.group(2).strip("\"\n"))
                    else:
                        #features[current_field][current_subfield].append(x[21:].strip("\""))
                        features[current_field][-1][current_subfield].append(x[21:].strip("\"\n"))

                
                mol_type = features["source"][0]["mol_type"][0]

                print mol_type


                for x in features["source"][0]["db_xref"]:
                    if x.startswith("taxon:"):
                        taxid = x[6:]

                genbank_entry = ExternalEntity( source_database = self.database, type = mol_type )

                genbank_entry.add_attribute( ExternalEntityAttribute( attribute_identifier = "taxid",
                                                                      value = taxid,
                                                                      type = "unique" ) )

                genbank_entry.add_attribute( ExternalEntityAttribute( attribute_identifier = "description",
                                                                      value = " ".join(fields["definition"])) )

                m = GenBankParser.version_re.search(" ".join(fields["version"]))
                if m:
                    genbank_entry.add_attribute( ExternalEntityAttribute( attribute_identifier = "accessionnumber",
                                                                          value = m.group(1),
                                                                          version = m.group(2),
                                                                          type = "unique") )
                    genbank_entry.add_attribute( ExternalEntityAttribute( attribute_identifier = "gi",
                                                                          value = m.group(3),
                                                                          type = "unique" ) )
                else:
                    raise ValueError("Version line does not match correctly")


                # print fields["origin"]
                if "origin" in fields:
                    sequence = []
                    for x in fields["origin"]:
                        sequence.append(x[10:].replace(" ","").strip())
                    sequence = "".join(sequence)
                    #print sequence
                    genbank_entry.add_attribute( ExternalEntityAttribute( attribute_identifier = "nucleotidesequence",
                                                                          value = DNASequence(sequence) ) )


                self.biana_access.insert_new_external_entity(genbank_entry)

                


                if "cds" in features:
                    for current_cds in features["cds"]:

                        # INSERT NEW EXTERNAL ENTITY CORRESPONDING TO A PROTEIN
                        genbank_protein_entry = ExternalEntity( source_database = self.database, type = "protein" )
                        
                        genbank_protein_entry.add_attribute( ExternalEntityAttribute( attribute_identifier = "taxid",
                                                                                      value = taxid,
                                                                                      type = "unique" ) )

                        if "ec_number" in current_cds:
                            for x in current_cds["ec_number"]:
                                genbank_protein_entry.add_attribute( ExternalEntityAttribute( attribute_identifier = "ec",
                                                                                              value = x,
                                                                                              type = "cross-reference" ) )


                        # print "GI:",current_cds["db_xref"][0][3:]
                        if "db_xref" in current_cds:
                            for x in current_cds["db_xref"]:
                                if x.startswith("GI:"):
                                    genbank_protein_entry.add_attribute( ExternalEntityAttribute( attribute_identifier = "gi",
                                                                                                  value = x[3:],
                                                                                                  type = "unique" ) )
                                    break
                    
                        if current_cds.has_key("gene"):
                            # print "gene:",current_cds["gene"][0]
                            genbank_protein_entry.add_attribute( ExternalEntityAttribute( attribute_identifier = "genesymbol",
                                                                                          value = current_cds["gene"][0],
                                                                                          type = "unique" ) )

                        if "protein_id" in current_cds:
                            acc_version = current_cds["protein_id"][0].split(".")
                            # print "Acc number",acc_version
                            genbank_protein_entry.add_attribute( ExternalEntityAttribute( attribute_identifier = "accessionnumber",
                                                                                          value = acc_version[0],
                                                                                          version = acc_version[1],
                                                                                          type = "unique" ) )

                        if "translation" in current_cds:
                            # print "Sequence", "".join(current_cds["translation"])
                            genbank_protein_entry.add_attribute( ExternalEntityAttribute( attribute_identifier = "proteinsequence",
                                                                                          value = ProteinSequence("".join(current_cds["translation"])) ) )

                        if "description" in current_cds:
                            # print "Description", " ".join(current_cds["product"])
                            genbank_protein_entry.add_attribute( ExternalEntityAttribute( attribute_identifier = "description",
                                                                                          value = " ".join(current_cds["product"]) ) )

                        self.biana_access.insert_new_external_entity(genbank_protein_entry)
                        
                        relation = ExternalEntityRelation( source_database=self.database, relation_type="codifies" )
                        relation.add_participant( externalEntityID = genbank_protein_entry.get_id() )
                        relation.add_participant( externalEntityID = genbank_entry.get_id() )

                        self.biana_access.insert_new_external_entity(relation)
                

                if "trna" in features:
                    print features["trna"]

                if "rrna" in features:
                    print features["rrna"]

                if "gene" in features:
                    print features["gene"]

                if "misc_fe" in features:
                    print features["misc_fe"]

                if "misc_rn" in features:
                    print features["misc_rn"]


                #print features.keys()

                # RESET
                current_field = None
                fields = {}
            
            if line.startswith(" "):
                fields[current_field].append(line)
            else:
                current_field = line[0:12].strip().lower()
                fields[current_field] = [line]




                
                
