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

class OMIMParser(BianaParser):
    """
    OMIM Parser Class
    """
    name = "omim"
    description = "This program fills up tables in database biana related with OMIM annotations"
    external_entity_definition = "A external entity represents a gene or protein"
    external_entity_relations = ""
    tax_id = 9606 # OMIM provides annotations for human

    def __init__(self):
        BianaParser.__init__(self, default_db_description = "OMIM",
                             default_script_name = "omimParser.py",
                             default_script_description = "This program fills up tables in biana database related to OMIM")
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
		if line.startswith("?"):
		    continue
		words = line.strip().split("|")
		description = words[0]
		for word in words[1].split(", "):
		    gene_name = word.strip()
		    externalEntity = ExternalEntity( source_database = self.database, type="gene" )
		    externalEntity.add_attribute(ExternalEntityAttribute(attribute_identifier="GeneSymbol", value=gene_name, type="unique")) 
		    externalEntity.add_attribute(ExternalEntityAttribute(attribute_identifier="TaxID", value=tax_id, type="cross-reference")) 
		    externalEntity.add_attribute(ExternalEntityAttribute(attribute_identifier="description", value=description))
		self.biana_access.insert_new_external_entity( externalEntity )

	    self.input_file_fd.close()

