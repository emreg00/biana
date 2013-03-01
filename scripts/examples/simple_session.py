#!python

from globals import * 

def simple_session():
    from biana.biana_commands import administration, available_sessions, create_new_session, save_session, load_session
    from biana.BianaObjects import BianaSessionManager

    # Remove comment below to load data from previous session
    #load_session("/home/user/test.dat") 

    identifier_description_list = [("gi", "1019412")] #[("disease", "alzheimer")] 
    relation_type_list =  ["interaction"] # ["reaction","complex","pathway","interaction"]

    # Create a new BIANA session
    create_new_session(sessionID="biana_session",dbname=DB_NAME, dbhost=DB_HOST, dbuser=DB_USER, dbpassword=DB_PASS, unification_protocol=UNIFICATION_PROTOCOL)
    objSession = available_sessions["biana_session"]

    # Create a new User Entity Set (group of biomolecules) in this session
    uESet1 = objSession.create_new_user_entity_set( identifier_description_list=identifier_description_list, attribute_restriction_list=[], id_type="embedded", new_user_entity_set_id="User_Entity_Set_1")
    uESet1 = objSession.get_user_entity_set("User_Entity_Set_1")

    # Export sequences of entries in this set as a FASTA file
    objSession.output_user_entity_set_sequences_in_fasta(user_entity_set_id="User_Entity_Set_1", only_selected = False, out_method=open("test_fasta.txt",'w').write, type="proteinsequence", attributes=["uniprotAccession"])

    # Instead of exporting the set as a FASTA file, remove comment below to export it as a tab separated file
    #objSession.output_user_entity_set_details(user_entity_set_id = "User_Entity_Set_1", out_method = open("nodes.out", "w").write, attributes=["hgnc", "taxid"], include_level_info=True, include_degree_info=True, level=None, only_selected=False, output_format="tabulated", include_tags_info = True, include_tags_linkage_degree_info=[], substitute_node_attribute_if_not_exists=False, output_1_value_per_attribute=False, include_command_in_rows=False)

    # Fetch relations of biomolecules in this set
    objSession.create_network( user_entity_set_id = "User_Entity_Set_1" , level = 1, relation_type_list= relation_type_list, include_relations_last_level = True, use_self_relations = True)

    # Instead of creating network from existing relations, remove comment below make predictions based on shared attribute
    #objSession.create_network( user_entity_set_id = "User_Entity_Set_1" , level = 1 , include_relations_last_level = False , use_self_relations = False , expansion_relation_type_list = ["interaction"] , expansion_attribute_list = [ [("cog", [])] ], expansion_level = 2 )

    # Export all the information of this set and its network to a file in a tab separated format
    objSession.output_user_entity_set_network(user_entity_set_id = "User_Entity_Set_1", out_method=open("network.out", "w").write, node_attributes = ["uniprotaccession"], participant_attributes = [], relation_attributes=[], allowed_relation_types="all", include_relation_ids=False, include_participant_ids=False, include_relation_type=True, include_relation_sources=True, output_1_value_per_attribute=False, output_format="tabulated", only_selected=False, substitute_node_attribute_if_not_exists=False) 
    #objSession.output_user_entity_set_network(user_entity_set_id = "User_Entity_Set_1", out_method=open("network.out", "w").write, node_attributes = ["uniprotaccession"], relation_attributes=["psimi_name"], include_relation_sources=True, output_format="tabulated") 
    # Remove comment below to save this session for later use
    #save_session("biana_session", "/home/user/test.dat")
    return

if __name__ == "__main__":
    simple_session()

