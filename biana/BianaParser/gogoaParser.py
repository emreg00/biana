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

import sys
import re
from bianaParser import *

class GOGOAParser(BianaParser):
    """
    GO GOA Parser Class
    """
    name = "go_goa"
    description = "This program fills up tables in database biana related with gene ontology gene/protein annotations"
    external_entity_definition = "A external entity represents a gene or protein"
    external_entity_relations = ""

    db_name_to_biana_name = { "UniProtKB/Swiss-Prot": 'UniprotAccession', 
			    "UniProtKB/TrEMBL": 'UniprotAccession', 
			    "ENSEMBL": 'ENSEMBL', 
			    "HINV": 'None', 
			    "TAIR": 'TAIR', 
			    "RefSeq": 'RefSeq', 
			    "VEGA": 'None',
			    "PDB": 'PDB',
			    'MGI': 'MGI'
			    }

    def __init__(self):
        
        BianaParser.__init__(self, default_db_description = "GO goa",
                             default_script_name = "gogoaParser.py",
                             default_script_description = "This program fills up tables in biana database related to GOA formatted annotation")
        self.default_eE_attribute = "genesymbol"
        
    def parse_database(self):
        """
        Method that implements the specific operations of go obo parser
        """
        if os.path.isdir(self.input_file):
            directory = os.path.abspath(self.input_file+os.sep)+os.sep
	    files = [ file for file in os.listdir(directory) if not file.startswith('.') ]
	    files = map(lambda x: directory + x, files)
        elif os.path.isfile(self.input_file):
	    files = [ os.path.abspath(self.input_file) ]
    
	for file in files:
	    self.input_file_fd = open(file)
	    for line in self.input_file_fd:
		if line.startswith("!"):
		    continue
		words = line.rstrip('\n').split("\t")
		db_name, db_id, gene_name, qualifier, go_id, evidence_ref, evidence_type, evidence_id, aspect, description, synonym, type, taxon_id = words[:13] #, date, curator, extension, form_id
		
		if any([ q=="NOT" for q in qualifier.split('|')]):
		    continue

		externalEntity = ExternalEntity( source_database = self.database, type="protein" )

		if db_name not in self.db_name_to_biana_name:
		    print "Warning: xref db name is not recognized:", db_name
		    db_name = None
		else:
		    db_name = self.db_name_to_biana_name[db_name]

		if db_name is not None:
		    externalEntity.add_attribute(ExternalEntityAttribute(attribute_identifier=db_name, value=db_id, type="cross-reference")) 

		externalEntity.add_attribute(ExternalEntityAttribute(attribute_identifier="GeneSymbol", value=gene_name, type="cross-reference")) 
		externalEntity.add_attribute(ExternalEntityAttribute(attribute_identifier="GO", value=int(go_id.lstrip("GO:")), type="unique")) 
		for taxon in taxon_id.split('|'):
		    externalEntity.add_attribute(ExternalEntityAttribute(attribute_identifier="TaxID", value=int(taxon.lstrip("taxon:")), type="cross-reference")) 
		if evidence_ref.startswith("PMID:"):
		    externalEntity.add_attribute(ExternalEntityAttribute(attribute_identifier="Pubmed", value=evidence_ref.lstrip("PMID:"), type="cross-reference")) 
		if len(description)>0:
		    externalEntity.add_attribute(ExternalEntityAttribute(attribute_identifier="description", value=description))
		self.biana_access.insert_new_external_entity( externalEntity )

	    self.input_file_fd.close()


