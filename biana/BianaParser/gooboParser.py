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


class GOOBOParser(BianaParser):
    """
    GO OBO 1.2 Parser Class
    """

    name = "go_obo"
    description = "This program fills up tables in database biana related with gene ontology"
    external_entity_definition = "A external entity represents an ontology element"
    external_entity_relations = ""


    def __init__(self):
        
        BianaParser.__init__(self, default_db_description = "GO obo",
                             default_script_name = "gooboParser.py",
                             default_script_description = "This program fills up tables in database biana related to OBO 1.2 formatted Ontologies")
        self.default_eE_attribute = "go"
        

    def parse_database(self):
        """
        Method that implements the specific operations of go obo parser
        """


        # Add the attributes specific to GO database
        self.biana_access.add_valid_external_entity_attribute_type( name = "GO_type",
                                                                    data_type = "ENUM(\"universal\",\"molecular_function\",\"cellular_component\",\"biological_process\")",
                                                                    category = "eE attribute")

        self.biana_access.add_valid_external_entity_attribute_type( name = "GO_name",
                                                                    data_type = "varchar(370)",
                                                                    category = "eE identifier attribute")


        # IMPORTANT: As we have added new types and attributes that are not in the default BIANA distribution, we must execute the follwing command:
        self.biana_access.refresh_database_information()

        

        # Add the possibility to transfer GO id and GO category using GO as a key
        self.biana_access._add_transfer_attribute( externalDatabaseID = self.database.get_id(), 
                                                   key_attribute = "GO",
                                                   transfer_attribute="GO_name" )

        ontology = Ontology( source_database = self.database, linkedAttribute="GO", name="GO", descriptionAttribute="GO_name" )

        specific_identifiers_and_parent = {}

        # Start variables
        term_id = None
        term_name = None
        term_def = None

        # GO OBO specific
        term_namespace = None 
        #term_synonyms = []
        term_is_a = []
        term_part_of = []
        term_exact_synonyms = []
        term_related_synonyms = []
        term_broad_synonyms = []
        term_narrow_synonyms = []
        term_alt_id = []

        self.initialize_input_file_descriptor()

        # Adding a dummy relation to let the database know that go_name attribute is also a possible relation attribute
        externalEntityRelation = ExternalEntityRelation( source_database = self.database, relation_type = "interaction" )
        externalEntityRelation.add_attribute( ExternalEntityRelationAttribute( attribute_identifier = "go_name", value = None) )

	def create_external_entity_from_go_term(database, term_id, term_name, 
		term_namespace, term_def, term_exact_synonyms, term_related_synonyms, term_broad_synonyms, term_narrow_synonyms, term_alt_id):
	    externalEntity = ExternalEntity( source_database = database, type = "GOElement" )
	    
	    externalEntity.add_attribute( ExternalEntityAttribute( attribute_identifier = "GO", value = term_id, type="unique") )

	    if term_name is not None:
		externalEntity.add_attribute( ExternalEntityAttribute( attribute_identifier = "GO_name", value = term_name, type="unique") )

	    externalEntity.add_attribute( ExternalEntityAttribute( attribute_identifier = "GO_type", value = term_namespace, type="unique" ) )

	    if term_def is not None:
		externalEntity.add_attribute( ExternalEntityAttribute( attribute_identifier = "description", value = term_def) )
	    
	    for current_synonym in term_exact_synonyms:
		if current_synonym is not None:
		    externalEntity.add_attribute( ExternalEntityAttribute( attribute_identifier = "GO_name",
									   value = current_synonym,
									   type = "exact_synonym" ) )
                    
	    for current_synonym in term_related_synonyms:
		if current_synonym is not None:
		    externalEntity.add_attribute( ExternalEntityAttribute( attribute_identifier = "GO_name",
		    						       value = current_synonym,
		    						       type = "related_synonym" ) )
                    
	    for current_synonym in term_broad_synonyms:
		if current_synonym is not None:
		    externalEntity.add_attribute( ExternalEntityAttribute( attribute_identifier = "GO_name",
		    						       value = current_synonym,
		    						       type = "broad_synonym" ) )
                    
	    for current_synonym in term_narrow_synonyms:
		if current_synonym is not None:
		    externalEntity.add_attribute( ExternalEntityAttribute( attribute_identifier = "GO_name",
		    						       value = current_synonym,
		    						       type = "narrow_synonym" ) )
                    
        # Quim Aguirre: Adding the alternative GO ids as "alias" in GO table
	    for current_alt_id in term_alt_id:
		if current_alt_id is not None:
		    externalEntity.add_attribute( ExternalEntityAttribute( attribute_identifier = "GO",
		    						       value = current_alt_id,
		    						       type = "alias" ) )
	    return externalEntity


        for line in self.input_file_fd:

            # Quim Aguirre: I have included to recognise [Typedef], so that the [Term] entries are recorded well when they are finished and there is a [Typedef] afterwards
            if re.search("\[Term\]",line) or re.search("\[Typedef\]",line):
                # New term
                if term_id is not None:

                    # insert previous
		    externalEntity = create_external_entity_from_go_term(self.database, term_id, term_name, 
			    term_namespace, term_def, term_exact_synonyms, 
			    term_related_synonyms, term_broad_synonyms, term_narrow_synonyms, term_alt_id)

                    self.biana_access.insert_new_external_entity( externalEntity )

                    specific_identifiers_and_parent[term_id] = (externalEntity.get_id(), term_is_a, term_part_of)

                # Restart variables
                term_id = None
                term_name = None
                term_def = None
                # GO OBO specific
                term_namespace = None 
                #term_synonyms = []
                term_is_a = []
                term_part_of = []
                term_exact_synonyms = []
                term_related_synonyms = []
                term_broad_synonyms = []
                term_narrow_synonyms = []
                term_alt_id = []

                if re.search("\[Typedef\]",line):
                    typedef = True
                else:
                    typedef = False


            elif re.search("^id\:",line):
                if typedef == True: # If typedef tag is true, we do not want to record anything
                    continue

                temp = re.search("GO\:(\d+)",line)
                
                if temp:
                    term_id = temp.group(1)

            elif re.search("^name\:",line):
                if typedef == True:
                    continue

                temp = re.search("name:\s+(.+)",line)
                term_name = temp.group(1)

            elif re.search("^namespace\:",line):
                if typedef == True:
                    continue

                temp = re.search("namespace:\s+(.+)",line)
                term_namespace = temp.group(1)

            elif re.search("^def\:",line):
                if typedef == True:
                    continue

                temp = re.search("\"(.+)\"",line)
                term_def = temp.group(1)

            elif re.search("synonym\:",line):
                if typedef == True:
                    continue

                temp = re.search("\"(.+)\"\s+(\w+)",line)
                if temp.group(2) == "EXACT":
                    term_exact_synonyms.append(temp.group(1))
                elif temp.group(2) == "RELATED":
                    term_related_synonyms.append(temp.group(1))
                # Quim Aguirre: I have added the broad and narrow synonyms
                elif temp.group(2) == "BROAD":
                    term_broad_synonyms.append(temp.group(1))
                elif temp.group(2) == "NARROW":
                    term_narrow_synonyms.append(temp.group(1))

            elif re.search("^alt_id\:",line):
            # Quim Aguirre: Recognison of the "alt_id" tags
            # Example --> alt_id: GO:0016425
                if typedef == True:
                    continue

                temp = re.search("GO\:(\d+)",line)
                
                if temp:
                    term_alt_id.append(temp.group(1))

            elif re.search("is_a\:",line):
                if typedef == True:
                    continue

                temp = re.search("GO\:(\d+)",line)
                if temp is not None:
                    #print "??:", line # malformation --> is_a: regulates ! regulates
                    term_is_a.append(temp.group(1))

            elif re.search("relationship\:",line):
                if typedef == True:
                    continue

                if( re.search("part_of",line) ):
                    temp = re.search("part_of\s+GO\:(\d+)",line)
                    if temp is not None:
                        term_part_of.append(temp.group(1))

	# Insert last term
	if term_id is not None:
	    externalEntity = create_external_entity_from_go_term(self.database, term_id, term_name, 
		    term_namespace, term_def, term_exact_synonyms, 
		    term_related_synonyms, term_broad_synonyms, term_narrow_synonyms, term_alt_id)
	    self.biana_access.insert_new_external_entity( externalEntity )
	    specific_identifiers_and_parent[term_id] = (externalEntity.get_id(), term_is_a, term_part_of)

        # Set the ontology hierarch and insert elements to ontology
        for current_method_id in specific_identifiers_and_parent:
            is_a_list = [ specific_identifiers_and_parent[x][0] for x in specific_identifiers_and_parent[current_method_id][1] ]
            is_part_of_list = [ specific_identifiers_and_parent[x][0] for x in specific_identifiers_and_parent[current_method_id][2]  ]
            ontology.add_element( ontologyElementID = specific_identifiers_and_parent[current_method_id][0],
                                  isA = is_a_list,
                                  isPartOf = is_part_of_list )

        self.biana_access.insert_new_external_entity(ontology)

