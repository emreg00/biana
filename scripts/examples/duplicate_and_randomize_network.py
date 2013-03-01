#!python

from globals import *

def duplicate_and_randomize_network():
    from biana.biana_commands import create_new_session, administration, available_sessions
    from biana.BianaObjects import BianaSessionManager
    import sets

    identifier_description_list = [("genesymbol", "rad2")] 
    identifier_description_list2 = [("genesymbol", "rad1")] 
    relation_type_list = ["interaction"]

    # Create a new BIANA session
    objSession = create_new_session(sessionID="biana_session",dbname=DB_NAME, dbhost=DB_HOST, dbuser=DB_USER, dbpassword=DB_PASS, unification_protocol=UNIFICATION_PROTOCOL)
    objSession = available_sessions["biana_session"]

    # Create a new User Entity Set (group of biomolecules) in this session
    uESet1 = objSession.create_new_user_entity_set( identifier_description_list = identifier_description_list, external_entity_attribute_restriction_list=[], id_type="embedded", new_user_entity_set_id="User_Entity_Set_1")

    # Get relations of biomolecules in this set
    objSession.create_network( user_entity_set_id = "User_Entity_Set_1" , level = 1, relation_type_list= relation_type_list, include_relations_last_level = False, use_self_relations = False)

    # Duplicate (could be used to keep a copy before randomization) the User Entity Set (group of biomolecules) in this session
    uESet2 = objSession.duplicate_user_entity_set(user_entity_set_id="User_Entity_Set_1", new_user_entity_set_id="User_Entity_Set_2")

    # Randomize the network of the User Entity Set based on the given randomization type
    objSession.randomize_user_entity_set_network(user_entity_set_id="User_Entity_Set_1", type_randomization="random")

    # Output network details
    objSession.output_user_entity_set_network(user_entity_set_id = "User_Entity_Set_1", out_method=open("network.out", "w").write, node_attributes = ["uniprotaccession"], participant_attributes = [], relation_attributes=[], allowed_relation_types="all", include_relation_ids=False, include_participant_ids=False, include_relation_type=True, include_relation_sources=True, output_1_value_per_attribute=False, output_format="tabulated", only_selected=True, substitute_node_attribute_if_not_exists=False) 

if __name__ == "__main__":
    duplicate_and_randomize_network()

