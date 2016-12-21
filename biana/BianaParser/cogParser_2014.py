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

class CogParser(BianaParser):
    """
    COG Parser Class
    """

    name = "cog2014"
    description = "Clusters of Orthologous Groups of proteins (COGs)"
    external_entity_definition = "An element in a COG"
    external_entity_relations = "A COG"

    def __init__(self):

        # Start with the default values

        BianaParser.__init__(self, default_db_description = "COG database",
                             default_script_name = "cogParser_2014.py",
                             default_script_description = CogParser.description,
                             additional_optional_arguments = [])
        self.default_eE_attribute = "cog"
	#self.is_promiscuous = True


    def parse_database(self):

        # FIRST: Check that all the files exist
        if os.path.isdir(self.input_file):
            self.input_path = self.input_file
        else:
            raise ValueError("You must specify a path instead of a file")

        files = ["prot2003-2014.fa","prot2003-2014.tab","prot2003-2014.gi2gbk.tab","genomes2003-2014.tab","fun2003-2014.tab","cognames2003-2014.tab", "cog2003-2014.csv"] #"pa" for the moment it is not necessary

        for current_file in files:
            if os.path.exists(self.input_path+os.sep+current_file) is False:
                raise ValueError("File %s is missing in %s" %(current_file, self.input_path))


        
        # Read correspondence letters to TaxID for the external entities
        # Change with respect to the old COG (by Quim Aguirre)

        species_file_fd = open(self.input_path+os.sep+"genomes2003-2014.tab",'r')
        specie_taxid_dict = {}

        # We create a dict with FTP name (specie) as key, and TaxID as value
        for line in species_file_fd:
            if line[0] != "#":
                (code, tax_id, FTP_name) = line.strip().split("\t")
                ## Example --> Acamar   329726  Acaryochloris_marina_MBIC11017_uid58167
                specie_taxid_dict[FTP_name.lower()] = tax_id
        
        species_file_fd.close()


        # Read the functional information
        # Change with respect to the old COG (by Quim Aguirre)

        function_dict = {}
        function_file_fd = open(self.input_path+os.sep+"fun2003-2014.tab",'r')

        # We create a dict with the functional code as key, and the function name as value
        for line in function_file_fd:
            if line[0] != "#":
                (fun_code, fun_name) = line.strip().split("\t")
                ## Example --> H    Coenzyme transport and metabolism
                function_dict[fun_code] = fun_name

        function_file_fd.close()

        
        # Read the name (genbank GI), refseq and (NCBI protein) genbank accession correspondence
        # Change with respect to the old COG (by Quim Aguirre)

        name2refseq_dict = {}
        name2gb_dict = {}
        name2refseqgb_file_fd = open(self.input_path+os.sep+"prot2003-2014.gi2gbk.tab",'r')

        # We create two dictionaries
        ## One contains the GI as key, and the RefSeq as value
        ## The other contains the GI as key, and the GenBank accession as value
        for line in name2refseqgb_file_fd:
            (protein_name, refseq, genbank) = line.strip().split("\t")
            ## Example --> 103485499	YP_615060	ABF51727
            name2refseq_dict[protein_name.lower()] = refseq.lower()
            name2gb_dict[protein_name.lower()] = genbank.lower()

        name2refseqgb_file_fd.close()


        # Obtain, from the COGs file, to which specie belongs each protein
        # Obtain also the information for the COGs, description, functional_classification...
        # Change with respect to the old COG (by Quim Aguirre)

        cognames = open(self.input_path+os.sep+"cognames2003-2014.tab",'r')
        cog2003_2014 = open(self.input_path+os.sep+"cog2003-2014.csv",'r')

        name2species_dict = {}
        cogs_components_dict = {}
        cogs_funct_dict = {}
        cogs_description_dict = {}
        name2cogs_dict = {}

        ##--> Obtain, from "cognames2003-2014.tab", the COG number, COG functional annotation code and description
        ## We create three dicts
        ### cogs_description_dict --> Contains the COG as key, and the description as value
        ### cogs_funct_dict --> Contains the COG as key, and the function as value
        ### In the dict "cogs_components_dict", we create keys containing COGs, and we set empty lists as their default values
        for line in cognames:

            if line[0] != "#":
                (COG, fun_code, description) = line.strip().split("\t")
                ## Example --> COG0001  H   Glutamate-1-semialdehyde aminotransferase

                cogs_description_dict[COG] = description
                #print(COG, cogs_description_dict[COG])
                cogs_funct_dict[COG] = fun_code
                #print(COG, cogs_funct_dict[COG])        
                cogs_components_dict.setdefault(COG,[])

        cognames.close()

        ##--> Obtain, from "cog2003-2014.csv", the COG number, protein id and its corresponding genome
        for line in cog2003_2014:
            (domain_id, genome, protein, prot_len, dom_start, dom_end, COG, membership, nothing) = line.split(',')
            ## Example --> 333894695,Alteromonas_SN2_uid67349,333894695,427,1,427,COG0001,0,
    
            try:
                # Here, if the COG exists inside the dictionary "cogs_components_dict", we add the proteins that correspond to this COG
                cogs_components_dict[COG].append(protein)
            except:
                continue
            # Here, we create two dicts:
            ## name2cogs_dict --> containing the GI as key, and the COG as value
            ## name2species_dict --> containing the GI as key, and the FTP name as value
            ### If a protein has more than one COG/genome, they are appended/added inside the list/set
            name2cogs_dict.setdefault(protein,[]).append(COG)
            name2species_dict.setdefault(protein.lower(),set()).add(genome.lower())

        cog2003_2014.close()
        


        def create_and_insert_eE():
            # Create an External Entity of type "protein"
            eE_object = ExternalEntity( source_database = self.database, type="protein" )
            # Add an attribute to the External Entity --> the protein sequence
            eE_object.add_attribute(ExternalEntityAttribute( attribute_identifier = "proteinsequence", 
                                                                     value = ProteinSequence("".join(sequence)),
								     type = "cross-reference"))

            if name2refseq_dict.has_key(protein_name.lower()):
                # Add an attribute to the External Entity --> the RefSeq identifier
                eE_object.add_attribute(ExternalEntityAttribute( attribute_identifier = "RefSeq", 
                                                                 value = name2refseq_dict[protein_name.lower()],
								 type="cross-reference" ))

            if name_to_gb_dict.has_key(protein_name.lower()):
                # Add an attribute to the External Entity --> the GeneBank accession
                eE_object.add_attribute(ExternalEntityAttribute( attribute_identifier = "AccessionNumber", 
                                                                 value = name_to_gb_dict[protein_name.lower()],
								 type="cross-reference" ))
                
            if name2species_dict.has_key(protein_name.lower()):
                species = name2species_dict[protein_name.lower()]

                if len(species)>1:
                    print "Protein %s has more than a single specie assigned!" %protein_name

                for current_specie in species:
                    # Add an attribute to the External Entity --> the TaxIDs of the protein
                    # We use the dictionary "specie_taxid_dict" in order to insert the corresponding TaxID to the specie
                    eE_object.add_attribute(ExternalEntityAttribute( attribute_identifier = "taxID",
                                                                     value = specie_taxid_dict[current_specie.lower()],
		    					      type = "cross-reference") )

                for current_cog in name2cogs_dict[protein_name.lower()]:
                    # Add an attribute to the External Entity --> the corresponding COG identifiers of the protein
                    eE_object.add_attribute( ExternalEntityAttribute( attribute_identifier = "COG",
                                                                      value = current_cog,
								      type="cross-reference" ) )
                    for current_function in cogs_funct_dict[current_cog]:
                        # Add an attribute to the External Entity --> the COG function
                        # Here, we use the "function_dict" to add the function description corresponding to the function code (current_function)
                        eE_object.add_attribute( ExternalEntityAttribute( attribute_identifier = "function",
                                                                          value = function_dict[current_function],
									  type="cross-reference" ) )

                    # Add an attribute to the External Entity --> the protein name, in this case is classified as GenBank GI
                    eE_object.add_attribute( ExternalEntityAttribute( attribute_identifier = "GI",
                                                                      value = protein_name,
								      type="cross-reference" ) )
                

                self.biana_access.insert_new_external_entity( externalEntity = eE_object )
            

        # Read the sequences and insert the external entities
        # Change with respect to the old COG (by Quim Aguirre)

        fasta_file_fd = open(self.input_path+os.sep+"prot2003-2014.fa",'r')
        sequence = []
        protein_name_regex = re.compile(">gi\|(.+)\|ref.*")
        ## Example of FASTA header --> >gi|103485499|ref|YP_615060.1| chromosomal replication initiation protein [Sphingopyxis alaskensis RB2256]
        protein_name = None

        for line in fasta_file_fd:

            m = protein_name_regex.match(line)
            if m:
                if len(sequence)>0:
                    create_and_insert_eE()

                sequence = []
                protein_name = m.group(1)
            else:
                sequence.append(line.strip())

        fasta_file_fd.close()

        if len(sequence)>0:
            create_and_insert_eE()


        # FINALLY, IT WOULD BE POSSIBLE TO INSERT THE COGS AS A RELATION... IS IT NECESSARY? In principle, it is not necessary, as attribute networks can be generated...
        # For the moment, not done
