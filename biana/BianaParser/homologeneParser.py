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

class HomologeneParser(BianaParser):
    """
    Homologene Parser Class
    """

    name = "homologene"
    description = "Automated system for detecting homologs among eukaryotic gene sets"
    external_entity_definition = "A gene that belongs to a homologous gene familiy in Homologene"
    external_entity_relations = "Homology between genes"

    def __init__(self):

        # Start with the default values

        BianaParser.__init__(self, default_db_description = "Homologene database",
                             default_script_name = "homologeneParser.py",
                             default_script_description = HomologeneParser.description,
                             additional_optional_arguments = [])
        self.default_eE_attribute = "homologene"
	#self.is_promiscuous = True


    def parse_database(self):

        def create_and_insert_eE(hid, taxid, geneid, genesymbol, proteingi, proteinaccession):
            eE_object = ExternalEntity( source_database = self.database, type="gene" )
	    eE_object.add_attribute(ExternalEntityAttribute( attribute_identifier = "homologene", 
                                                                 value = hid,
								 type = "unique")) 
	    eE_object.add_attribute(ExternalEntityAttribute( attribute_identifier = "taxID",
								 value = taxid,
								 type = "cross-reference"))
	    eE_object.add_attribute(ExternalEntityAttribute( attribute_identifier = "GeneID",
								 value = geneid,
								 type = "cross-reference"))
	    eE_object.add_attribute(ExternalEntityAttribute( attribute_identifier = "GeneSymbol",
								 value = genesymbol,
								 type = "cross-reference"))
	    eE_object.add_attribute( ExternalEntityAttribute( attribute_identifier = "GI",
							      value = proteingi,
							      type = "cross-reference") )
	    eE_object.add_attribute( ExternalEntityAttribute( attribute_identifier = "AccessionNumber",
							      value = proteinaccession,
							      type = "cross-reference") )

	    self.biana_access.insert_new_external_entity( externalEntity = eE_object )
	# fed: create_and_insert_eE()

        self.initialize_input_file_descriptor()
        
	# Format (tab seperated): HID	TaxId	GeneId	GeneSymbol  ProteinGi	ProteinAccession 
	#			    0	1	2	3	    4		5
        for line in self.input_file_fd:
	    # Parse the line and insert the external entity
	    (hid, taxid, geneid, genesymbol, proteingi, proteinaccession) = line.strip().split("\t")
	    create_and_insert_eE(hid, taxid, geneid, genesymbol, proteingi, proteinaccession)

        self.input_file_fd.close()

        # FINALLY, IT WOULD BE POSSIBLE TO INSERT THE Homologenes AS A RELATION... IS IT NECESSARY? In principle, it is not necessary, as attribute networks can be generated...
        # For the moment, not done

