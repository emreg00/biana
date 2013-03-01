#!python

from globals import *
import time

def get_seed_connected_nodes():
    from biana.biana_commands import administration, available_sessions, create_new_session
    from biana.BianaObjects import BianaSessionManager
    import sets
    import biana.utilities.identifier_utilities as id_utils

    t_init = time.clock()

    relation_type_list = ["interaction"] # ["reaction","complex","pathway","interaction"]

    # Create a new BIANA session
    create_new_session(sessionID="biana_session",dbname=DB_NAME, dbhost=DB_HOST, dbuser=DB_USER, dbpassword=DB_PASS, unification_protocol=UNIFICATION_PROTOCOL)
    objSession = available_sessions["biana_session"]
    #identifier_description_list = id_utils.read_identifier_list_from_file("/home/emre/arastirma/colloboration/seeds_intracranial_and_aneurysm_and_mesh_genetics.txt", "genesymbol")
    identifier_description_list = id_utils.read_identifier_list_from_file("/home/emre/arastirma/colloboration/subarachnoid_and_hemorrhage_or_haemorrhage_and_mesh_genetics.seeds", "genesymbol")

    # Create a new User Entity Set (group of biomolecules) in this session
    uESet1 = objSession.create_new_user_entity_set( identifier_description_list = identifier_description_list, external_entity_attribute_restriction_list=[("taxid","9606")], id_type="embedded", new_user_entity_set_id="User_Entity_Set_1")

    t_1 = time.clock()
    # Get relations of biomolecules in this set
    objSession.create_network( user_entity_set_id = "User_Entity_Set_1" , level = 1, relation_type_list= relation_type_list, include_relations_last_level = False, use_self_relations = False)

    t_2 = time.clock()
    print "t_network:", t_2-t_1
    # Select seeds and nodes connected by seeds in the network of this User Entity Set
    user_entities_to_print = sets.Set(uESet1.get_user_entity_ids(level=0))|uESet1.get_user_entity_ids_by_linker_degree_cutoff(2)
    objSession.select_user_entities_from_user_entity_set("User_Entity_Set_1", user_entities_to_print, clear_previous_selection=True)
    objSession.tag_selected_user_entities(user_entity_set_id="User_Entity_Set_1", tag="seed+connected")

    t_3 = time.clock()
    print "t_connected:", t_3-t_2

    # Output set details - only selected nodes
    #objSession.output_user_entity_set_details(user_entity_set_id = "User_Entity_Set_1", out_method = open("nodes_selected.out", "w").write, attributes=["genesymbol", "uniprotaccession", "hgnc"], include_level_info=True, include_degree_info=True, level=None, only_selected=True, output_format="tabulated", include_tags_info = True, include_tags_linkage_degree_info=[], substitute_node_attribute_if_not_exists=True, output_1_value_per_attribute=False, include_command_in_rows=False)
    # Output set details - all nodes
    objSession.output_user_entity_set_details(user_entity_set_id = "User_Entity_Set_1", out_method = open("nodes.out", "w").write, attributes=["genesymbol", "uniprotaccession", "hgnc"], include_level_info=True, include_degree_info=True, level=None, only_selected=False, output_format="tabulated", include_tags_info = True, include_tags_linkage_degree_info=[], substitute_node_attribute_if_not_exists=True, output_1_value_per_attribute=False, include_command_in_rows=False)

    t_4 = time.clock()
    print "t_nodes:", t_4-t_3

    # Output network details
    objSession.output_user_entity_set_network(user_entity_set_id = "User_Entity_Set_1", out_method=open("network.out", "w").write, node_attributes = [], participant_attributes = [], relation_attributes=["Method_id"], allowed_relation_types="all", include_relation_ids=False, include_participant_ids=True, include_relation_type=True, include_relation_sources=True, output_1_value_per_attribute=False, output_format="tabulated", only_selected=False, substitute_node_attribute_if_not_exists=True, include_participant_tags=False, include_relation_tags = False, include_unconnected_nodes=False) 

    t_5 = time.clock()
    print "t_network:", t_5-t_4

