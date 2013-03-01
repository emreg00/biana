#!python

from globals import *

def select_and_create_subset_from_set():
    from biana.biana_commands import administration, available_sessions, create_new_session
    from biana.BianaObjects import BianaSessionManager
    import sets

    identifier_description_list = [("genesymbol", "rad2"), ("genesymbol", "rad1")] 
    identifier_description_list2 = [("genesymbol", "rad1")] 
    relation_type_list = ["interaction"]

    # Create a new BIANA session
    objSession = create_new_session(sessionID="biana_session",dbname=DB_NAME, dbhost=DB_HOST, dbuser=DB_USER, dbpassword=DB_PASS, unification_protocol=UNIFICATION_PROTOCOL)
    objSession = available_sessions["biana_session"]

    # Create a new User Entity Set (group of biomolecules) in this session
    uESet1 = objSession.create_new_user_entity_set( identifier_description_list = identifier_description_list, external_entity_attribute_restriction_list=[], id_type="embedded", new_user_entity_set_id="User_Entity_Set_1")

    # Create another User Entity Set (group of biomolecules) in this session
    uESet2 = objSession.create_new_user_entity_set( identifier_description_list = identifier_description_list2, external_entity_attribute_restriction_list=[], id_type="embedded", new_user_entity_set_id="User_Entity_Set_2")

    # Select all User Entities in the second User Entity Set
    user_entities_to_select = uESet2.get_user_entity_ids()
    objSession.select_user_entities_from_user_entity_set("User_Entity_Set_1", user_entities_to_select, clear_previous_selection=True)

    # Create a new User Entity Set from selected User Entities in the first User Entity Set
    objSession.get_sub_user_entity_set(user_entity_set_id = "User_Entity_Set_1", include_relations=False, new_user_entity_set_id="User_Entity_Set_3")

    # Output set details
    objSession.output_user_entity_set_details(user_entity_set_id = "User_Entity_Set_3", out_method = open("nodes.out", "w").write, attributes=["genesymbol", "uniprotaccession", "hgnc"], include_level_info=True, include_degree_info=True, level=None, only_selected=False, output_format="tabulated", include_tags_info = True, include_tags_linkage_degree_info=[], substitute_node_attribute_if_not_exists=True, output_1_value_per_attribute=False, include_command_in_rows=False)

if __name__ == "__main__":
    select_and_create_subset_from_set()

