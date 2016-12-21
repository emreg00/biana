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

"""
File          : hgnc2piana.py
Author        : Javier Garcia
Creation      : 14 November 2007
Contents      : fills up tables in database piana with information from HGNC
Called from   : 
Modifications : at April of 2016 by Quim Aguirre in order to adapt the parser
                to the changes of the names of the different fields
Information   : http://www.genenames.org/help/statistics-downloads
=======================================================================================================

"""


## STEP 1: IMPORT NECESSARY MODULES

from bianaParser import *


class HGNCParser(BianaParser):
    """
    HGNC Parser Class
    """

    name = "hgnc_2016"
    description = "This file implements a program that fills up tables in BIANA database with information from HGNC"
    external_entity_definition = "A external entity represents a protein"
    external_entity_relations = ""

    def __init__(self):

        # Start with the default values

        BianaParser.__init__(self, default_db_description = "HUGO Gene Nomenclature Committee",
                             default_script_name = "hgncParser_2016.py",
                             default_script_description = HGNCParser.description,
                             additional_compulsory_arguments = [])
        self.default_eE_attribute = "hgnc"
        
    def parse_database(self):
        """
        Method that implements the specific operations of HGNC parser

        # Updated dict
        0 : HGNC ID
        1 : Approved Symbol
        2 : Approved Name
        3 : Status
        4 : Previous Symbols
        5 : Aliases
        6 : Chromosome
        7 : Accession Numbers
        8 : RefSeq IDs

        # Python generated dict
        0 :  HGNC ID
        1 :  Approved Symbol
        2 :  Approved Name
        3 :  Status
        4 :  Locus Type
        5 :  Previous Symbols
        6 :  Previous Names
        7 :  Aliases
        8 :  Name Aliases
        9 :  Chromosome
        10 :  Date Approved
        11 :  Date Modified
        12 :  Date Symbol Changed
        13 :  Date Name Changed
        14 :  Accession Numbers
        15 :  Enzyme IDs
        16 :  Entrez Gene ID
        17 :  Ensembl Gene ID
        18 :  Mouse Genome Database ID
        19 :  Specialist Database Links
        20 :  Specialist Database IDs
        21 :  Pubmed IDs
        22 :  RefSeq IDs
        23 :  Gene Family Name
        24 :  Record Type
        25 :  Primary IDs
        26 :  Secondary IDs
        27 :  CCDS IDs
        28 :  VEGA IDs
        29 :  Locus Specific Databases
        30 :  GDB ID (mapped data)
        31 :  Entrez Gene ID (mapped data supplied by NCBI)
        32 :  OMIM ID (mapped data supplied by NCBI)
        33 :  RefSeq (mapped data supplied by NCBI)
        34 :  UniProt ID (mapped data supplied by UniProt)
        35 :  Ensembl ID (mapped data supplied by Ensembl)
        36 :  UCSC ID (mapped data supplied by UCSC)
        37 :  Rat Genome Database ID (mapped data supplied by RGD)

        """
        

		
        # List of tables to lock. It is used to improve speed inserts, as the indices are not updated in each insert
        # Commented. Locking all tables for the moment
        # tables_to_lock = [PianaGlobals.crossReferenceSource_table,
        #                   PianaGlobals.crossReferences_table]


        # HGNC Fields are the following: (CD: Multiple values, comma delimited    QCD: Multiple Quoited values in a comma delimited list
        #  0: HGNC ID
        #  1: Approved Symbol (Oficial Gene Symbol)
        #  2: Approved Name (Oficial Gene Name)
        #  3: Status
        #  4: Locus Type
        #  5: Previous Symbols CD
        #  6: Previous Names QCD
        #  7: Aliases CD
        #  8: Name Aliases QCD         # EMPTY!!!!
        #  9: Chromosome
        # 10: Date Approved
        # 11: Date Modified
        # 12: Date Symbol changed      NOT EXISTS!!!!
        # 13: Date Name Changed
        # 14: Accession Numbers CD
        # 15: Enzyme ID CD
        # 16: Entrez Gene ID (Replaeced Locus Link)
        # 17: Ensembl Gene ID
        # 18: MGD ID
        # 19: Specialist Database Links (CD)
        # 20: Specialist Database IDs (CD)         NOT EXISTS!!!!
        # 21: Pubmed IDs (CD)
        # 22: RefSeq IDs (CD) Only One is selected!
        # 23: Gene Family Name (CD)
        # 24: Record Type
        # 25: Primary IDs
        # 26: Secondary IDs
        # 27: CCDS IDs
        # 28: VEGA IDs
        # 29: Locus Specific Databases
        # 30: GBD ID
        # 31: Entrez Gene ID
        # 32: OMIM ID
        # 33: RefSeq
        # 34: Uniprot ID
        # 35: EnsembL
        # 36: UCSC ID
        # 37: RGD ID
        

        self.initialize_input_file_descriptor()

        line_number=0
        header_columns = {}

        columns = 0

        for line in self.input_file_fd:

            line_number += 1
            

            # Read columns of header line into dictionary
            if line_number == 1:
                value_list = line.strip().split("\t")
                header_columns = dict([ (value_list[i], i) for i in xrange(len(value_list))])
                #sys.stderr.write("%s columns in header\n" %len(value_list))
                columns = len(value_list)

            # Example of header row:
            # hgnc_id	symbol	name	locus_group	locus_type	status	location	location_sortable	alias_symbol	alias_name	prev_symbol	prev_name	gene_family	gene_family_id	date_approved_reserved	date_symbol_changed	date_name_changed	date_modified	entrez_id	ensembl_gene_id	vega_id	ucsc_id	ena	refseq_accession	ccds_id	uniprot_ids	pubmed_id	mgd_id	rgd_id	lsdb	cosmic	omim_id	mirbase	homeodb	snornabase	bioparadigms_slc	orphanet	pseudogene.org	horde_id	merops	imgt	iuphar	kznf_gene_catalog	mamit-trnadb	cd	lncrnadb	enzyme_id	intermediate_filament_db                                

            # Example of simple row:
            # HGNC:18149	A4GALT	alpha 1,4-galactosyltransferase	protein-coding gene	gene with protein product	Approved	22q13.2	22q13.2	"A14GALT|Gb3S|P(k)|P1"	"Gb3 synthase|CD77 synthase|globotriaosylceramide synthase|lactosylceramide 4-alpha-galactosyltransferase"		alpha 1,4-galactosyltransferase (globotriaosylceramide synthase, P blood group)	Alpha 1,4-glycosyltransferases	442	2002-02-06		2008-07-31	2016-12-13	53947	ENSG00000128274	OTTHUMG00000150744	uc062ewl.1		NM_017436	CCDS14041	Q9NPC4	10854428	MGI:3512453	RGD:621583	LRG_795|http://www.lrg-sequence.org/LRG/LRG_795	A4GALT	607922															2.4.1.228	

            if line_number>1:
	
                try:
	            if line_number>1:

                        line.strip()
	            
	            	# Create a new external entity object
	            	hgnc_object = ExternalEntity( source_database = self.database, type="protein" )

                        # ADDING TAXID AS IT ONLY CONTAINS HUMAN GENES
                        hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier="taxid",
                                                                          value=9606,
                                                                          type="unique"))

	            	line_fields = line.split("\t")

                        
                        if len(line_fields) != columns:
                            sys.stderr.write("Incorrect fields number\n%s\n" %(line))

                        

	                column_index = header_columns["hgnc_id"]
                        column_value =  line_fields[column_index].strip()
                        if column_value.startswith("HGNC:"):
                            hgnc_id = column_value[5:]
                        else:
                            hgnc_id = column_value
	                # Take the values. Those that can be multiple values are stored in a list
                        hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "hgnc", 
                                                                          value = hgnc_id,
                                                                          type = "unique" ))
	                
	                column_index = header_columns["symbol"]
	                official_gene_symbol = line_fields[column_index].strip()
                        hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "geneSymbol", 
                                                                          value = official_gene_symbol,
                                                                          type = "unique" ))
	                
	                column_index = header_columns["name"]
	                official_gene_name = line_fields[column_index].strip()
	                # Oficial gene Name is entered as a description
                        hgnc_object.add_attribute( ExternalEntityAttribute(attribute_identifier = "description",
                                                                           value = official_gene_name, type="unique" ))

	                column_index = header_columns["alias_symbol"]
	                aliases_symbol = line_fields[column_index].strip()
	                if len(aliases_symbol)>0:
                            if aliases_symbol.startswith('"') and aliases_symbol.endswith('"'):
                                aliases_symbol = aliases_symbol[1:-1]
                            aliases_symbol = [ x.strip() for x in aliases_symbol.split("|") ]
                            [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "geneSymbol", 
                                                                                value = x,
                                                                                type = "alias" )) for x in aliases_symbol ]

                        keyword = "alias_name"
                        if keyword in header_columns:
	                    column_index = header_columns[keyword]
	                    aliases_name = line_fields[column_index].strip()
	                    if len(aliases_name)>0:
                                if aliases_name.startswith('"') and aliases_name.endswith('"'):
                                    aliases_name = aliases_name[1:-1]
                                aliases_name = [ x.strip() for x in aliases_name.split("|") ]
                                [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "description", 
                                                                                value = x,
                                                                                type = "alias")) for x in aliases_name ]
	                
	                column_index = header_columns["prev_symbol"]
	                previous_symbols = line_fields[column_index].strip()
	                if len(previous_symbols)>0:
                            if previous_symbols.startswith('"') and previous_symbols.endswith('"'):
                                previous_symbols = previous_symbols[1:-1]
                            previous_symbols = [ x.strip() for x in previous_symbols.split("|") ]
                            [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "geneSymbol", 
                                                                                value = x,
                                                                                type = "previous")) for x in previous_symbols ]

                        keyword = "prev_name"
                        if keyword in header_columns:
	                    column_index = header_columns[keyword]
	                    previous_names = line_fields[column_index].strip()
	                    if len(previous_names)>0:
				# some names have commas in between, so we will split based on ", " - fix by Laura Furlong
                                if previous_names.startswith('"') and previous_names.endswith('"'):
                                    previous_names = previous_names[1:-1]
                                previous_names = [ x.strip() for x in previous_names.split("|") ]
                                [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "description", 
                                                                                value = x, type="previous" )) for x in previous_names ]

                        keyword = "entrez_id"
                        if keyword in header_columns:
	                    column_index = header_columns[keyword]
	                    geneIDs = line_fields[column_index].strip()
	                    if len(geneIDs)>0:
                                if geneIDs.startswith('"') and geneIDs.endswith('"'):
                                    geneIDs = geneIDs[1:-1]
                                geneIDs = [ x.strip() for x in geneIDs.split("|") ]
                                [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "geneID", 
                                                                                    value = x,
                                                                                    type = "cross-reference")) for x in geneIDs ]

                        keyword = "ensembl_gene_id"
                        if keyword in header_columns:
	                    column_index = header_columns[keyword]
	                    ensemblIDs = line_fields[column_index].strip()
	                    if len(ensemblIDs)>0:
                                if ensemblIDs.startswith('"') and ensemblIDs.endswith('"'):
                                    ensemblIDs = ensemblIDs[1:-1]
                                ensemblIDs = [ x.strip() for x in ensemblIDs.split("|") ]
                                [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "ensembl", 
                                                                                    value = x,
                                                                                    type = "cross-reference")) for x in ensemblIDs ]
	                	
                        keyword = "ena"
                        if keyword in header_columns:
	                    column_index = header_columns[keyword]
	                    accessionIDs = line_fields[column_index].strip()
	                    if len(accessionIDs)>0:
                                if accessionIDs.startswith('"') and accessionIDs.endswith('"'):
                                    accessionIDs = accessionIDs[1:-1]
                                accessionIDs = [ x.strip() for x in accessionIDs.split("|") ]
                                [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "AccessionNumber", 
                                                                                    value = x,
                                                                                    type = "cross-reference")) for x in accessionIDs ]

	                column_index = header_columns["refseq_accession"]
	                refseqs = line_fields[column_index].strip()
	                if len(refseqs)>0: 
                            if refseqs.startswith('"') and refseqs.endswith('"'):
                                refseqs = refseqs[1:-1]
                            refseqs = [ x.strip() for x in refseqs.split("|") ]
                            [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "refseq", 
                                                                                value = x,
                                                                                type = "cross-reference")) for x in refseqs ]

                        keyword = "uniprot_ids"
                        if keyword in header_columns:
	                    column_index = header_columns[keyword]
	                    uniprotIDs = line_fields[column_index].strip()
	                    if len(uniprotIDs)>0:
                                if uniprotIDs.startswith('"') and uniprotIDs.endswith('"'):
                                    uniprotIDs = uniprotIDs[1:-1]
                                uniprotIDs = [ x.strip() for x in uniprotIDs.split("|") ]
                                [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "uniprotaccession", 
                                                                                    value = x,
                                                                                    type = "cross-reference")) for x in uniprotIDs ]
	                	
#                        keyword = "GDB ID (mapped data)"
#                        if keyword in header_columns:
#	                    column_index = header_columns[keyword]
#	                    column_value = line_fields[column_index].strip()
#	                    if len(column_value)>0:
#                                GDB_IDs = [ x.lstrip("GDB:") for x in column_value.split(",") ]
#                                [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "gdb", 
#                                                                                    value = x,
#                                                                                    type = "cross-reference" )) for x in GDB_IDs ]
	                	
                        keyword = "pubmed_id"
                        if keyword in header_columns:
	                    column_index = header_columns[keyword]
	                    pubmed_IDs = line_fields[column_index].strip()
	                    if len(pubmed_IDs)>0:
                                if pubmed_IDs.startswith('"') and pubmed_IDs.endswith('"'):
                                    pubmed_IDs = pubmed_IDs[1:-1]
                                pubmed_IDs = [ x.strip() for x in pubmed_IDs.split("|") ]
                                [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "pubmed", 
                                                                                    value = x,
                                                                                    type = "cross-reference")) for x in pubmed_IDs ]
	                	
                        keyword = "mgd_id"
                        if keyword in header_columns:
	                    column_index = header_columns[keyword]
	                    MGD_IDs = line_fields[column_index].strip()
	                    if len(MGD_IDs)>0:
                                if MGD_IDs.startswith('"') and MGD_IDs.endswith('"'):
                                    MGD_IDs = MGD_IDs[1:-1]
                                MGD_IDs = [ x.lstrip("MGI:") for x in MGD_IDs.split("|") ]
                                [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "mgi", 
                                                                                    value = x,
                                                                                    type = "cross-reference")) for x in MGD_IDs ]

                        keyword = "rgd_id"
                        if keyword in header_columns:
	                    column_index = header_columns[keyword]
	                    RGD_IDs = line_fields[column_index].strip()
	                    if len(RGD_IDs)>0:
                                if RGD_IDs.startswith('"') and RGD_IDs.endswith('"'):
                                    RGD_IDs = RGD_IDs[1:-1]
                                RGD_IDs = [ x.lstrip("RGD:") for x in RGD_IDs.split("|") ]
                                for current_rgd_id in RGD_IDs:
                                    if current_rgd_id != "":
                                        hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "rgd", 
                                                                                      value = current_rgd_id,
                                                                                      type = "cross-reference"))
	                	
                        keyword = "omim_id"
                        if keyword in header_columns:
	                    column_index = header_columns[keyword]
	                    omimIDs = line_fields[column_index].strip()
	                    if len(omimIDs)>0:
                                if omimIDs.startswith('"') and omimIDs.endswith('"'):
                                    omimIDs = omimIDs[1:-1]
                                omimIDs = [ x.strip() for x in omimIDs.split("|") ]
                                [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "mim",
                                                                                    value = x, 
			    							type="cross-reference")) for x in omimIDs ]
	                	
                        keyword = "imgt"
                        if keyword in header_columns:
	                    column_index = header_columns[keyword]
	                    imgtIDs = line_fields[column_index].strip()
	                    if len(imgtIDs)>0:
                                if imgtIDs.startswith('"') and imgtIDs.endswith('"'):
                                    imgtIDs = imgtIDs[1:-1]
                                imgtIDs = [ x.strip() for x in imgtIDs.split("|") ]
                                [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "IMGT", 
                                                                                    value = x,
                                                                                    type = "cross-reference")) for x in imgtIDs ]
	                	
                        keyword = "enzyme_id"
                        if keyword in header_columns:
	                    column_index = header_columns[keyword]
	                    enzyme_IDs = line_fields[column_index].strip()
	                    if len(enzyme_IDs)>0:
                                if enzyme_IDs.startswith('"') and enzyme_IDs.endswith('"'):
                                    enzyme_IDs = enzyme_IDs[1:-1]
                                enzyme_IDs = [ x.strip() for x in enzyme_IDs.split("|") ]
                                [ hgnc_object.add_attribute(ExternalEntityAttribute(attribute_identifier = "EC", 
                                                                                value = x,
                                                                                type = "cross-reference" ) ) for x in enzyme_IDs ]
	                	
	                # Save the object in the database            	
	                self.biana_access.insert_new_external_entity( externalEntity = hgnc_object )
	
	
                except:
                    traceback.print_exc()
                    sys.stderr.write("Error in parsing line %s\n" %(line_number))
                    raise Exception;


