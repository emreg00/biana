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
                    

class TutorialParser(BianaParser):                                                        
    """
    Parser for BIANA Tutorial

    Parses data in the following format:

    EC A     EC B   Score   pValue  KEGG RA KEGG RB Dir A   Dir B   HARD    Common metabolites

    See Tutorial for more details about this data set
    """                 
                                                                                         
    name = "TutorialParser"
    description = "This file implements a program that fills up tables in BIANA database for the Tutorial sample"
    external_entity_definition = "An external entity represents a protein"
    external_entity_relations = ""

            
    def __init__(self):
	"""
        Start with the default values
	"""
        BianaParser.__init__(self, default_db_description = "Metabolical relations between enzymes",  
                             default_script_name = "TutorialParser.py",
                             default_script_description = TutorialParser.description,     
                             additional_compulsory_arguments = [])
        self.default_eE_attribute = "EC"
        
                    
    def parse_database(self):
        """                                                                              
        Method that implements the specific operations of a MyData formatted file
        """

        if self.input_file.endswith(".gz"):
            self.input_file_fd = gzip.open(self.input_file, 'r')
        else:
            self.input_file_fd = open(self.input_file, 'r')


        # First of all, we have to define our specific attributes that are not by default in BIANA
        self.biana_access.add_valid_external_entity_attribute_type( name = "difficulty",
                                                                    data_type = "ENUM(\"special\",\"normal\")",
                                                                    category = "eE numeric attribute")

        self.biana_access.add_valid_external_entity_attribute_type( name = "CommonMetaboliteNumber",
                                                                    data_type = "smallint unsigned",
                                                                    category = "eE numeric attribute")

        self.biana_access.add_valid_external_entity_relation_participant_attribute_type( name = "direction",
                                                                                         data_type = "enum(\"direct\",\"reverse\")" )

        
        self.biana_access.add_valid_external_entity_relation_participant_attribute_type( name = "keggCode",
                                                                                         data_type = "char(6)" )


        # IMPORTANT: As we have added new types and attributes that are not in the default BIANA distribution, we must execute the following command:
        self.biana_access.refresh_database_information()


        EC_to_external_entity_id = {}

        line_num = 0

	for line in self.input_file_fd:
            
            line_num += 1

            #Skip the first line
            if line_num == 1:
                continue


            # Print progress
            if self.time_control:
                if line_num%1000==0:
                    sys.stderr.write("%s lines done in %s seconds\n" %(line_num, time.time()-self.initial_time))


            
            fields = line.strip().split("\t")


            # First fields are the EC codes of the enzymes. So, we must create an external entity for these proteins.
            # In this example, an external Entity will be a protein, identified by its EC enzyme code. In order to not insert the same external entity multiple times,
            # we will store the EC code in a dictionary, to know if it has been previously inserted
            
            if not EC_to_external_entity_id.has_key(fields[0]):
                
                new_external_entity_object = ExternalEntity( source_database = self.database, type = "protein" )
                new_external_entity_object.add_attribute( ExternalEntityAttribute(attribute_identifier="EC", value=fields[0], type="unique") )

                EC_to_external_entity_id[fields[0]] = self.biana_access.insert_new_external_entity( externalEntity = new_external_entity_object )          

            if not EC_to_external_entity_id.has_key(fields[1]):

                new_external_entity_object = ExternalEntity( source_database = self.database, type = "protein" )
                new_external_entity_object.add_attribute( ExternalEntityAttribute(attribute_identifier="EC", value=fields[1], type="unique") )

                EC_to_external_entity_id[fields[1]] = self.biana_access.insert_new_external_entity( externalEntity = new_external_entity_object )


            # Next, we have to introduce a relation between these external entities            
            new_external_entity_relation = ExternalEntityRelation( source_database = self.database, relation_type="reaction" )


            # Next, we have to add the participants of the relation
            new_external_entity_relation.add_participant( externalEntityID = EC_to_external_entity_id[fields[0]] )
            new_external_entity_relation.add_participant( externalEntityID = EC_to_external_entity_id[fields[1]] )

            
            # Check if it's a self relation, to add the cardinality
            if EC_to_external_entity_id[fields[0]]==EC_to_external_entity_id[fields[1]]:
                new_external_entity_relation.add_participant_attribute( externalEntityID = EC_to_external_entity_id[fields[0]],
                                                                        participantAttribute = ExternalEntityRelationParticipantAttribute(attribute_identifier = "cardinality", 
                                                                                                                                          value = 2) )
            
            # Next, we add the attributes for the relation
            new_external_entity_relation.add_attribute( ExternalEntityRelationAttribute(attribute_identifier = "score", value = fields[2]) )
            new_external_entity_relation.add_attribute( ExternalEntityRelationAttribute(attribute_identifier = "pvalue", value = fields[3]) )

            if fields[8] == "False":
                new_external_entity_relation.add_attribute( ExternalEntityRelationAttribute(attribute_identifier = "difficulty", value = "hard") )
            else:
                new_external_entity_relation.add_attribute( ExternalEntityRelationAttribute(attribute_identifier = "difficulty", value = "easy") )
                
            new_external_entity_relation.add_attribute( ExternalEntityRelationAttribute(attribute_identifier = "CommonMetaboliteNumber", value = fields[9]) )

            # Finally, we add the external entity relation participant attributes
            new_external_entity_relation.add_participant_attribute( externalEntityID = EC_to_external_entity_id[fields[0]],
                                                                     participantAttribute = ExternalEntityRelationParticipantAttribute( attribute_identifier = "keggCode",
                                                                                                                                        value = fields[4]) )
            
            new_external_entity_relation.add_participant_attribute( externalEntityID = EC_to_external_entity_id[fields[1]],
                                                                     participantAttribute = ExternalEntityRelationParticipantAttribute( attribute_identifier = "keggCode",
                                                                                                                                        value = fields[5]) )

            if fields[6]=="d":
                direction = "direct"
            elif fields[6]=="r":
                direction = "reverse"
            else:
                sys.stderr.write("Direction not recognized: %s" %fields[6])
            new_external_entity_relation.add_participant_attribute( externalEntityID = EC_to_external_entity_id[fields[0]],
                                                                    participantAttribute = ExternalEntityRelationParticipantAttribute( attribute_identifier = "direction",
                                                                                                                                       value = direction ) )

            if fields[7]=="d":
                direction = "direct"
            elif fields[7]=="r":
                direction = "reverse"
            else:
                sys.stderr.write("Direction not recognized: %s" %fields[6])
            
            new_external_entity_relation.add_participant_attribute( externalEntityID = EC_to_external_entity_id[fields[1]],
                                                                    participantAttribute = ExternalEntityRelationParticipantAttribute( attribute_identifier = "direction",
                                                                                                                                       value = direction ) )
            
            # Insert this external entity realtion into database
            self.biana_access.insert_new_external_entity( externalEntity = new_external_entity_relation )

            
