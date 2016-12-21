"""
    BIANA: Biologic Interactions and Network Analysis
    Copyright (C) 2009  Javier Garcia-Garcia, Emre Guney, Baldo Oliva

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

"""

from bianaParser import *
from obo_index import obo_name_to_MI

class iRefIndexParser(BianaParser):
    """
    Parser for iRefIndex MITAB 2.5 PPI files. Modified at April of 2016 
    by Quim Aguirre to adapt the parser to iRefIndex MITAB 2.6
    """

    name = "iRefIndex_2016"
    description = "This file implements the parser for iRefIndex"
    external_entity_definition = "A protein"
    external_entity_relations = "physical interaction"


    def __init__(self):

        # Start with the default values

        BianaParser.__init__(self, default_db_description = "iRefIndex Database",
                             default_script_name = "iRefIndexParser_2016.py",
                             default_script_description = iRefIndexParser.description,
                             additional_optional_arguments = [])
        self.default_eE_attribute = "iRefIndex_ROGID"
        self.initialize_input_file_descriptor()
        return


    def parse_database(self):
        """
        Parses the iRefIndex file

        iRefIndex format explained at http://irefindex.org/wiki/index.php?title=README_MITAB2.6_for_iRefIndex
        """

        #irefindex:NeQ0QcVrpNzyqWlmp/HvG5FIRHA9606       irefindex:xjgJ54UxwS5DwnuMgn6A8XxjvsY9606       uniprotkb:P24593|refseq:NP_000590|entrezgene/locuslink:3488     uniprotkb:P35858|refseq:NP_004961|entrezgene/locuslink:3483  uniprotkb:IBP5_HUMAN|entrezgene/locuslink:IGFBP5        uniprotkb:ALS_HUMAN|entrezgene/locuslink:IGFALS MI:0000(-)      -       pubmed:-        taxid:9606      taxid:9606  MI:0000(aggregation)     MI:0923(irefindex)|MI:0000:(ophid)      irefindex:++1ALo0GitoAXTTz5lyLFlYtoO8|ophid:-   lpr:-|hpr:-|np:-        3488    3483    MI:0326(protein)        MI:0326(protein)    ++1ALo0GitoAXTTz5lyLFlYtoO8      X       2       3196230 4869527
        
        fields_dict = {}

        external_entities_dict = {}   # Dictionary protein_id -> external Entity ID

        external_entity_relations_dict = {}

        mi_re = re.compile("MI:(\d+)\((.+)\)")

        uniprot_entry_pattern = re.compile('[a-zA-Z0-9]{,10}_[a-zA-Z0-9]{,5}')
        pdb_chain_pattern = re.compile('[a-zA-Z0-9]{4}_[a-zA-Z0-9]')
        refseq_pattern = re.compile('[a-zA-Z0-9]{2}_[a-zA-Z0-9]+')
        gbaccession_pattern = re.compile('[a-zA-Z]{3}[0-9]{5}')

        for line in self.input_file_fd:

            # PROCESS HEADER. Here I have an example of the current version header:
            #uidA   uidB    altA    altB    aliasA  aliasB  method  author  pmids   taxa    taxb    interactionType sourcedb    interactionIdentifier   confidence  expansion   biological_role_A   biological_role_B   experimental_role_A experimental_role_B interactor_type_A   interactor_type_B   xrefs_A xrefs_B xrefs_Interaction   Annotations_A   Annotations_B   Annotations_Interaction Host_organism_taxid parameters_Interaction  Creation_date   Update_date Checksum_A  Checksum_B  Checksum_Interaction    Negative    OriginalReferenceA  OriginalReferenceB  FinalReferenceA FinalReferenceB MappingScoreA   MappingScoreB   irogida irogidb irigid  crogida crogidbcrigid   icrogida    icrogidb    icrigid imex_id edgetype    numParticipants
            if line[0]=="#":
                fields = line[1:].strip().split("\t")
                for x in xrange(0, len(fields)):
                    fields_dict[fields[x].lower()] = x
            
                continue

            fields = line.strip().split("\t")

            eE1_id = None
            eE2_id = None

            # Create or get external entities
            if "0326" in fields[fields_dict["interactor_type_a"]]:

                if fields[0] not in external_entities_dict:

                    eE1 = ExternalEntity( source_database = self.database, type="protein" )

                    # Primary ids:
                    # In the previous versions, RogIDs were extracted from the columns "uida" and "uidb"
                    # Now, these columns contain identifiers from major databases (uniprot, refseq...)
                    # If we want the RogID, we need to take it from column column 33/34 (checksum_a/checksum_b)
                    primary_id_t = fields[fields_dict["checksum_a"]]
                    # Example --> rogid:6Y8uQAzJShSjVNH41+7FK8K1DXo9606
                    t = primary_id_t.split(":")
                    eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "irefindex_ROGID", value=t[1], type="unique") )


                    alternative_ids = fields[fields_dict["alta"]].split("|")
                    for current_id in alternative_ids:
                        if current_id == "-":
                            continue
                        t = current_id.split(":")
                        idtype = t[0].lower()
                        idvalue = t[1]
                        if idvalue=="-":
                            continue
                        if idtype=="uniprotkb":
                        # In "uniprotkb" ids, I have found both UniprotEntries and UniprotAccession entries. 
                        # For this reason, I have made one regex to identify the Entries
                            if uniprot_entry_pattern.match(idvalue) != None:
                                eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotentry", value=idvalue, type="cross-reference") )
                            else:
                                eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotaccession", value=idvalue, type="cross-reference") )
                        elif idtype=="refseq":
                        # In "refseq" ids, I have found both RefSeq and GenBank Accession entries. 
                        # For this reason, I have made two regex to identify them
                            if refseq_pattern.match(idvalue) != None:
                                eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "refseq", value=idvalue, type="cross-reference") )
                            elif gbaccession_pattern.match(idvalue) != None:
                                eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value=idvalue, type="cross-reference") )
                        elif idtype=="entrezgene/locuslink":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "geneID", value=idvalue, type="cross-reference") )
                        elif idtype=="cygd":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "cygd", value=idvalue, type="cross-reference") )
                        elif idtype=="ipi":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "ipi", value=idvalue, type="cross-reference") )
                        elif idtype=="flybase":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "flybase", value=idvalue, type="cross-reference") )
                        elif idtype=="pdb":
                        # There is the possibilty of having just the PDB of 4 letters, or the PDB plus the chain
                        # For this reason, I have made a regex to identify the PDBs with chain and annotate them
                            if pdb_chain_pattern.match(idvalue) != None:
                                eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "pdb", value=idvalue[0:4], type="cross-reference", additional_fields = {"chain": idvalue[-1]} ) )
                            else:
                                eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "pdb", value=idvalue[0:4], type="cross-reference" ) )
                        elif idtype=="gb":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value = idvalue, type="cross-reference" ) )
                        elif idtype=="dbj":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value = idvalue, type="cross-reference" ) )
                        elif idtype=="pir":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "pir", value = idvalue, type="cross-reference" ) )
                        elif idtype=="kegg":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "keggCode", value = idvalue, type="cross-reference" ) )
                        elif idtype=="emb":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value = idvalue, type="cross-reference" ) )
                        elif idtype=="uniprot":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotaccession", value = idvalue, type="cross-reference" ) )
                        elif idtype=="swiss-prot":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotaccession", value = idvalue, type="cross-reference" ) )
                        elif idtype=="intact":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "intact", value = idvalue.replace("EBI-",""), type="cross-reference" ) )
                        elif idtype=="genbank_protein_gi":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "GI", value = idvalue, type="cross-reference" ) )
                        elif idtype=="tigr":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "tigr", value = idvalue, type="cross-reference" ) )
                        elif idtype=="prf" or idtype=="pubmed" or idtype=="uniparc":
                            pass
                        else:
                            pass
                            #sys.stderr.write("Alternative id type %s not recognized\n" %idtype)

                    aliases_ids = fields[fields_dict["aliasa"]].split("|")
                    for current_id in aliases_ids:
                        if current_id == "-":
                            continue
                        t = current_id.split(":")
                        idtype = t[0].lower()
                        idvalue = t[1]
                        if idvalue=="-":
                            continue
                        if idtype=="uniprotkb":
                        # In "uniprotkb" ids, I have found both UniprotEntries and UniprotAccession entries. 
                        # For this reason, I have made one regex to identify the Entries
                            if uniprot_entry_pattern.match(idvalue) != None:
                                eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotentry", value=idvalue, type="cross-reference") )
                            else:
                                eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotaccession", value=idvalue, type="cross-reference") )
                        elif idtype=="refseq":
                        # In "refseq" ids, I have found both RefSeq and GenBank Accession entries. 
                        # For this reason, I have made two regex to identify them
                            if refseq_pattern.match(idvalue) != None:
                                eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "refseq", value=idvalue, type="cross-reference") )
                            elif gbaccession_pattern.match(idvalue) != None:
                                eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value=idvalue, type="cross-reference") )
                        elif idtype=="entrezgene/locuslink":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "geneID", value=idvalue, type="cross-reference") )
                        elif idtype=="cygd":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "cygd", value=idvalue, type="cross-reference") )
                        elif idtype=="ipi":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "ipi", value=idvalue, type="cross-reference") )
                        elif idtype=="flybase":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "flybase", value=idvalue, type="cross-reference") )
                        elif idtype=="pdb":
                        # There is the possibilty of having just the PDB of 4 letters, or the PDB plus the chain
                        # For this reason, I have made a regex to identify the PDBs with chain and annotate them
                            if pdb_chain_pattern.match(idvalue) != None:
                                eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "pdb", value=idvalue[0:4], type="cross-reference", additional_fields = {"chain": idvalue[-1]} ) )
                            else:
                                eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "pdb", value=idvalue[0:4], type="cross-reference" ) )
                        elif idtype=="gb":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value = idvalue, type="cross-reference" ) )
                        elif idtype=="dbj":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value = idvalue, type="cross-reference" ) )
                        elif idtype=="pir":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "pir", value = idvalue, type="cross-reference" ) )
                        elif idtype=="kegg":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "keggCode", value = idvalue, type="cross-reference" ) )
                        elif idtype=="emb":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value = idvalue, type="cross-reference" ) )
                        elif idtype=="uniprot":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotaccession", value = idvalue, type="cross-reference" ) )
                        elif idtype=="swiss-prot":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotaccession", value = idvalue, type="cross-reference" ) )
                        elif idtype=="intact":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "intact", value = idvalue.replace("EBI-",""), type="cross-reference" ) )
                        elif idtype=="genbank_protein_gi":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "GI", value = idvalue, type="cross-reference" ) )
                        elif idtype=="tigr":
                            eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "tigr", value = idvalue, type="cross-reference" ) )
                        elif idtype=="prf" or idtype=="pubmed" or idtype=="uniparc":
                            pass
                        else:
                            #sys.stderr.write("Alternative id type %s not recognized\n" %idtype)
                            pass


                    # Example --> taxid:83333(Escherichia coli K-12)
                    taxID = fields[fields_dict["taxa"]].replace("taxid:","").split("(")[0]
                    if taxID!="-":
                        eE1.add_attribute( ExternalEntityAttribute( attribute_identifier= "taxID", value=taxID, type = "cross-reference") )

                    external_entities_dict[fields[0]] = self.biana_access.insert_new_external_entity( externalEntity = eE1 )

                eE1_id = external_entities_dict[fields[0]]
                

            if "0326" in fields[fields_dict["interactor_type_b"]]:

                if fields[1] not in external_entities_dict:

                    eE2 = ExternalEntity( source_database = self.database, type="protein" )

                    # Primary ids
                    primary_id_t = fields[fields_dict["checksum_b"]]
                    # Example --> rogid:6Y8uQAzJShSjVNH41+7FK8K1DXo9606
                    t = primary_id_t.split(":")
                    eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "irefindex_ROGID", value=t[1], type="cross-reference") )

                    alternative_ids = fields[fields_dict["altb"]].split("|")
                    for current_id in alternative_ids:
                        if current_id == "-":
                            continue
                        t = current_id.split(":")
                        idtype = t[0].lower()
                        idvalue = t[1]
                        if idvalue=="-":
                            continue
                        if idtype=="uniprotkb":
                        # In "uniprotkb" ids, I have found both UniprotEntries and UniprotAccession entries. 
                        # For this reason, I have made one regex to identify the Entries
                            if uniprot_entry_pattern.match(idvalue) != None:
                                eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotentry", value=idvalue, type="cross-reference") )
                            else:
                                eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotaccession", value=idvalue, type="cross-reference") )
                        elif idtype=="refseq":
                        # In "refseq" ids, I have found both RefSeq and GenBank Accession entries. 
                        # For this reason, I have made two regex to identify them
                            if refseq_pattern.match(idvalue) != None:
                                eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "refseq", value=idvalue, type="cross-reference") )
                            elif gbaccession_pattern.match(idvalue) != None:
                                eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value=idvalue, type="cross-reference") )
                        elif idtype=="entrezgene/locuslink":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "geneID", value=idvalue, type="cross-reference") )
                        elif idtype=="cygd":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "cygd", value=idvalue, type="cross-reference") )
                        elif idtype=="ipi":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "ipi", value=idvalue, type="cross-reference") )
                        elif idtype=="flybase":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "flybase", value=idvalue, type="cross-reference") )
                        elif idtype=="pdb":
                        # There is the possibilty of having just the PDB of 4 letters, or the PDB plus the chain
                        # For this reason, I have made a regex to identify the PDBs with chain and annotate them
                            if pdb_chain_pattern.match(idvalue) != None:
                                eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "pdb", value=idvalue[0:4], type="cross-reference", additional_fields = {"chain": idvalue[-1]} ) )
                            else:
                                eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "pdb", value=idvalue[0:4], type="cross-reference" ) )
                        elif idtype=="gb":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value = idvalue, type="cross-reference" ) )
                        elif idtype=="dbj":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value = idvalue, type="cross-reference" ) )
                        elif idtype=="pir":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "pir", value = idvalue, type="cross-reference" ) )
                        elif idtype=="kegg":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "keggCode", value = idvalue, type="cross-reference" ) )
                        elif idtype=="emb":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value = idvalue, type="cross-reference" ) )
                        elif idtype=="uniprot":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotaccession", value = idvalue, type="cross-reference" ) )
                        elif idtype=="swiss-prot":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotaccession", value = idvalue, type="cross-reference" ) )
                        elif idtype=="intact":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "intact", value = idvalue.replace("EBI-",""), type="cross-reference" ) ) 
                        elif idtype=="genbank_protein_gi":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "GI", value = idvalue, type="cross-reference" ) )
                        elif idtype=="tigr":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "tigr", value = idvalue, type="cross-reference" ) )
                        elif idtype=="prf" or idtype=="pubmed" or idtype=="uniparc":
                            pass
                        else:
                            #sys.stderr.write("Alias id type %s not recognized\n" %idtype)
                            pass

                    aliases_ids = fields[fields_dict["aliasb"]].split("|")
                    for current_id in aliases_ids:
                        if current_id == "-":
                            continue
                        t = current_id.split(":")
                        idtype = t[0].lower()
                        idvalue = t[1]
                        if idvalue=="-":
                            continue
                        if idtype=="uniprotkb":
                        # In "uniprotkb" ids, I have found both UniprotEntries and UniprotAccession entries. 
                        # For this reason, I have made one regex to identify the Entries
                            if uniprot_entry_pattern.match(idvalue) != None:
                                eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotentry", value=idvalue, type="cross-reference") )
                            else:
                                eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotaccession", value=idvalue, type="cross-reference") )
                        elif idtype=="refseq":
                        # In "refseq" ids, I have found both RefSeq and GenBank Accession entries. 
                        # For this reason, I have made two regex to identify them
                            if refseq_pattern.match(idvalue) != None:
                                eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "refseq", value=idvalue, type="cross-reference") )
                            elif gbaccession_pattern.match(idvalue) != None:
                                eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value=idvalue, type="cross-reference") )
                        elif idtype=="entrezgene/locuslink":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "geneID", value=idvalue, type="cross-reference") )
                        elif idtype=="cygd":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "cygd", value=idvalue, type="cross-reference") )
                        elif idtype=="ipi":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "ipi", value=idvalue, type="cross-reference") )
                        elif idtype=="flybase":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "flybase", value=idvalue, type="cross-reference") )
                        elif idtype=="pdb":
                        # There is the possibilty of having just the PDB of 4 letters, or the PDB plus the chain
                        # For this reason, I have made a regex to identify the PDBs with chain and annotate them
                            if pdb_chain_pattern.match(idvalue) != None:
                                eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "pdb", value=idvalue[0:4], type="cross-reference", additional_fields = {"chain": idvalue[-1]} ) )
                            else:
                                eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "pdb", value=idvalue[0:4], type="cross-reference" ) )
                        elif idtype=="gb":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value = idvalue, type="cross-reference" ) )
                        elif idtype=="dbj":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value = idvalue, type="cross-reference" ) )
                        elif idtype=="pir":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "pir", value = idvalue, type="cross-reference" ) )
                        elif idtype=="kegg":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "keggCode", value = idvalue, type="cross-reference" ) )
                        elif idtype=="emb":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "accessionnumber", value = idvalue, type="cross-reference" ) )
                        elif idtype=="uniprot":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotaccession", value = idvalue, type="cross-reference" ) )
                        elif idtype=="swiss-prot":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "uniprotaccession", value = idvalue, type="cross-reference" ) )
                        elif idtype=="intact":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "intact", value = idvalue.replace("EBI-",""), type="cross-reference" ) )
                        elif idtype=="genbank_protein_gi":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "GI", value = idvalue, type="cross-reference" ) )
                        elif idtype=="tigr":
                            eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "tigr", value = idvalue, type="cross-reference" ) )
                        elif idtype=="prf" or idtype=="pubmed" or idtype=="uniparc":
                            pass
                        else:
                            #sys.stderr.write("Alias id type %s not recognized\n" %idtype)
                            pass

                    taxID = fields[fields_dict["taxb"]].replace("taxid:","").split("(")[0]
                    if taxID!="-":
                        eE2.add_attribute( ExternalEntityAttribute( attribute_identifier= "taxID", value=taxID, type = "cross-reference") )

                    external_entities_dict[fields[1]] = self.biana_access.insert_new_external_entity( externalEntity = eE2 )

                eE2_id = external_entities_dict[fields[1]]


            ######################################
            ## INTERACTION SPECIFIC INFORMATION ##
            ######################################

            rigid_id = fields[fields_dict["checksum_interaction"]]
            

            add_common_attributes = True

            # Does the edge represent a binary interaction (X), complex membership (C), or a multimer (Y)?
            edgetype = fields[fields_dict["edgetype"]]
            if edgetype=="X":
                eEr = ExternalEntityRelation( source_database = self.database, relation_type = "interaction" )
            # When edgetype is a complex, the row contains the interactor "A" which is the complex, and the interactor "B" which is one of the proteins of the complex
            # We only annotate the protein of the complex, and we relate this protein with the other proteins of the complex by the "rigid" of the complex
            elif edgetype=="C":
                if rigid_id in external_entity_relations_dict:
                    add_common_attributes = False
                eEr = external_entity_relations_dict.setdefault(rigid_id, ExternalEntityRelation( source_database = self.database, relation_type = "complex" ) )
            elif edgetype=="Y":
                eEr = ExternalEntityRelation( source_database = self.database, relation_type = "interaction" )

                
            if "0326" in fields[fields_dict["interactor_type_a"]]:
                eEr.add_participant( externalEntityID = eE1_id )
            
            if "0326" in fields[fields_dict["interactor_type_b"]]:
                eEr.add_participant( externalEntityID = eE2_id )


            if edgetype=="Y":
                eEr.add_participant_attribute(externalEntityID = eE1_id,
                                              participantAttribute = ExternalEntityRelationParticipantAttribute( attribute_identifier = "cardinality", 
                                                                                                                 value = fields[fields_dict["numparticipants"]] ))
            else:
                if "0326" in fields[fields_dict["interactor_type_b"]]:
                    eEr.add_participant( externalEntityID = eE2_id )
                                              

            
            if add_common_attributes:

                eEr.add_attribute( ExternalEntityAttribute( attribute_identifier = "iRefIndex_RIGID", value = rigid_id.replace("rigid:",""), type="unique" ) )

                # pubmed:9199353|pubmed:10413469|pubmed:14759368|pubmed:11805826
                pubmeds = fields[fields_dict["pmids"]].split("|")
                for current_pubmed in pubmeds:
                    pubmed_id = current_pubmed.replace("pubmed:","").strip()
                    if pubmed_id != "-" and not pubmed_id.startswith("unassigned") and pubmed_id!="-2" and pubmed_id!="null":
                        eEr.add_attribute( ExternalEntityAttribute( attribute_identifier= "pubmed", value=current_pubmed.replace("pubmed:",""), type="cross-reference") )


                # METHOD
                methods = fields[fields_dict["method"]].split("|")
                # Example of one method --> MI:0090(protein complementation assay)
                # Example of multiple methods --> MI:0090(protein complementation assay)|MI:0228(cytoplasmic complementation assay)|MI:0230(membrane bound complementation assay)
                for current_method in methods:
                    m = mi_re.match(current_method)
                    if m:
                        try:
	                        # I will take directly the MI of the method from the regex
	                        method_MI = m.group(1)
	                        method_name = m.group(2).lower()
	                        if method_name=="-" or method_name=="other" or method_name=="not-specified" or method_name=="na":
	                            continue
	                        #method_MI = obo_name_to_MI[method_name] # This was from the old version of the parser, but I think that it is not necessary
	                        if method_MI is not None:
	                            eEr.add_attribute( ExternalEntityAttribute( attribute_identifier= "method_id", value=method_MI, type="cross-reference" ) )
                        except:
                            sys.stderr.write("Method MI not found: %s\n" %m.group(2))

                
                # CONFIDENCE
                confidences = fields[fields_dict["confidence"]].split("|")
                # Example --> ['hpr:37298', 'lpr:37298', 'np:1']
 
                for current_confidence in confidences:
                    if current_confidence.startswith("lpr"):
                        eEr.add_attribute( ExternalEntityAttribute( attribute_identifier= "iRefIndex_lpr", value=current_confidence[4:] ) )
                    elif current_confidence.startswith("hpr"):
                        eEr.add_attribute( ExternalEntityAttribute( attribute_identifier= "iRefIndex_hpr", value=current_confidence[4:] ) )
                    elif current_confidence.startswith("np"):
                        eEr.add_attribute( ExternalEntityAttribute( attribute_identifier= "iRefIndex_np", value=current_confidence[3:] ) )

            if edgetype != "C":
                self.biana_access.insert_new_external_entity( externalEntity = eEr )


        # Insert all complexes
        for current_complex_eEr in external_entity_relations_dict.values():
            self.biana_access.insert_new_external_entity( externalEntity = current_complex_eEr )



            


                    

                    
            
            

