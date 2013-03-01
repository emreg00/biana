#!/usr/env/python

from globals import * 

def get_human_interactome(include_string=False):
    from biana.biana_commands import available_sessions, create_new_session

    identifier_description_list = [("taxid", "9606")]
    relation_type_list = ["interaction"] #,"reaction"] # ["interaction","reaction","complex","pathway"]
    level = 0

    # Create a new BIANA session
    create_new_session(sessionID="biana_session",dbname=DB_NAME, dbhost=DB_HOST, dbuser=DB_USER, dbpassword=DB_PASS, unification_protocol=UNIFICATION_PROTOCOL)
    objSession = available_sessions["biana_session"]

    # Create a new User Entity Set (group of biomolecules) in this session
    uESet1 = objSession.create_new_user_entity_set( identifier_description_list=identifier_description_list, attribute_restriction_list=[], id_type="embedded", new_user_entity_set_id="User_Entity_Set_1")
    uESet1 = objSession.get_user_entity_set("User_Entity_Set_1")

    print "# of nodes: ", uESet1.getSize()

    if include_string:
	# Duplicate the User Entity Set (group of biomolecules) in this session
	uESet2 = objSession.duplicate_user_entity_set(user_entity_set_id="User_Entity_Set_1", new_user_entity_set_id="User_Entity_Set_2")

	# Fetch relations (except STRING) of biomolecules in 1st set
	objSession.create_network( user_entity_set_id = "User_Entity_Set_1" , level = level, relation_type_list=relation_type_list, relation_attribute_restriction_list=[], include_relations_last_level=True, use_self_relations=True)

	print "# of experimental edges: ", uESet1.getNumberEdges()

	# Fetch relations (only STRING with scores > 700) of biomolecules in 2nd set
	relation_type_list = ["functional_association"]
	#objSession.create_network( user_entity_set_id = "User_Entity_Set_2" , level = level, relation_type_list=relation_type_list, relation_attribute_restriction_list=[("stringscore",">700")], include_relations_last_level=True, use_self_relations=True)
	objSession.create_network( user_entity_set_id = "User_Entity_Set_2" , level = level, relation_type_list=relation_type_list, relation_attribute_restriction_list=[], include_relations_last_level=True, use_self_relations=True)
	print "# of string edges: ", uESet2.getNumberEdges()

	uESet3 = objSession.get_union_of_user_entity_set_list(user_entity_set_list=["User_Entity_Set_1", "User_Entity_Set_2"], include_relations=True, new_user_entity_set_id="User_Entity_Set_3") 
	objSession.remove_user_entity_set(user_entity_set_id="User_Entity_Set_1")
	objSession.remove_user_entity_set(user_entity_set_id="User_Entity_Set_2")

	# Export all the information of this set and its network to a file in a tab separated format
	#objSession.output_user_entity_set_network(user_entity_set_id = "User_Entity_Set_3", out_method=open("network.out", "w").write, node_attributes = ["uniprotaccession"], participant_attributes = [], relation_attributes=["stringscore"], include_relation_ids=False, include_participant_ids=False, include_relation_type=True, include_relation_sources=True, output_1_value_per_attribute=False, output_format="tabulated", include_participant_tags=False, include_relation_tags=False) 
	objSession.output_user_entity_set_network_in_sif_format(user_entity_set_id = "User_Entity_Set_3", output_path = "./", output_prefix = "human_with_string", node_attributes = [], participant_attributes = [], relation_attributes=["pubmed", "method_id","stringscore"], output_1_value_per_attribute=False, include_tags=False) 
    else:
	# Fetch relations (except STRING) of biomolecules in 1st set
	objSession.create_network( user_entity_set_id = "User_Entity_Set_1" , level = level, relation_type_list=relation_type_list, relation_attribute_restriction_list=[], include_relations_last_level=True, use_self_relations=True)
	print "# of experimental edges: ", uESet1.getNumberEdges()
	# Output set details - all nodes
	objSession.output_user_entity_set_details(user_entity_set_id = "User_Entity_Set_1", out_method = open("human_nodes.tsv", "w").write, attributes=["uniprotaccession", "geneid", "ensembl", "genesymbol", "go", "hgnc", "disease", "proteinsequence"], include_level_info=False, include_degree_info=True, level=None, only_selected=False, output_format="tabulated", include_tags_info = True, include_tags_linkage_degree_info=[], substitute_node_attribute_if_not_exists=False, output_1_value_per_attribute=False, include_command_in_rows=False)
	# Export sequences of entries in this set as a FASTA file
	#objSession.output_user_entity_set_sequences_in_fasta(user_entity_set_id="User_Entity_Set_1", only_selected = False, out_method=open("human_fasta.txt",'w').write, type="proteinsequence") #, attributes=["uniprotaccession"])
	# Export all the information of this set and its network to a file in a tab separated format
	objSession.output_user_entity_set_network(user_entity_set_id = "User_Entity_Set_1", out_method=open("human_network.out", "w").write, node_attributes = ["uniprotaccession"], participant_attributes = [], relation_attributes=[], include_relation_ids=False, include_participant_ids=False, include_relation_type=True, include_relation_sources=True, output_1_value_per_attribute=False, output_format="tabulated", include_participant_tags=False, include_relation_tags=False) 
	objSession.output_user_entity_set_network_in_sif_format(user_entity_set_id = "User_Entity_Set_1", output_path = "./", output_prefix = "human", node_attributes = [], participant_attributes = [], relation_attributes=["pubmed", "method_id"], output_1_value_per_attribute=False, include_tags=False) 


    return

if __name__ == "__main__":
    get_human_interactome() # True)

