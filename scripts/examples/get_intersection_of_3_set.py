#!python

from globals import * 

BONE_FILE = "/home/emre/arastirma/biana/ramon_paper/clin_exp_met_root_proteins/bone_root_proteins.txt"
LIVER_FILE = "/home/emre/arastirma/biana/ramon_paper/clin_exp_met_root_proteins/liver_root_proteins.txt"
LUNG_FILE = "/home/emre/arastirma/biana/ramon_paper/clin_exp_met_root_proteins/lung_root_proteins.txt"

def get_intersection_of_3_set():
    from biana.biana_commands import administration, available_sessions, create_new_session
    from biana.BianaObjects import BianaSessionManager
    import biana.utilities.identifier_utilities as id_utils
    create_new_session(sessionID="biana_session",dbname=DB_NAME, dbhost=DB_HOST, dbuser=DB_USER, dbpassword=DB_PASS, unification_protocol=UNIFICATION_PROTOCOL)
    objSession = available_sessions["biana_session"]
    objSession.create_new_user_entity_set( identifier_description_list = id_utils.read_identifier_list_from_file(BONE_FILE, id_type="uniprotaccession"), external_entity_attribute_restriction_list=[("taxid","9606")], id_type="embedded",new_user_entity_set_id="bone")
    objSession.create_new_user_entity_set( identifier_description_list = id_utils.read_identifier_list_from_file(LIVER_FILE, id_type="uniprotaccession"), external_entity_attribute_restriction_list=[("taxid","9606")], id_type="embedded",new_user_entity_set_id="liver")
    objSession.create_new_user_entity_set( identifier_description_list = id_utils.read_identifier_list_from_file(LUNG_FILE, id_type="uniprotaccession"), external_entity_attribute_restriction_list=[("taxid","9606")], id_type="embedded",new_user_entity_set_id="lung")

    print "Bone - Liver - Lung User Entity Sets are created..", objSession
    objSession.create_network( user_entity_set_id = "bone" , level = 1 , relation_type_list=["complex","interaction","pathway","reaction"] , include_relations_last_level = True , use_self_relations = False)
    objSession.create_network( user_entity_set_id = "liver" , level = 1 , relation_type_list=["complex","interaction","pathway","reaction"] , include_relations_last_level = True , use_self_relations = False)
    objSession.create_network( user_entity_set_id = "lung" , level = 1 , relation_type_list=["complex","interaction","pathway","reaction"] , include_relations_last_level = True , use_self_relations = False)

    print "Bone - Liver - Lung Networks are created..", objSession
    objSession.get_intersection_of_user_entity_set_list([objSession.get_user_entity_set("bone"), objSession.get_user_entity_set("liver"), objSession.get_user_entity_set("lung")], include_relations=True, new_user_entity_set_id="intersection")
    objSession.get_intersection_of_user_entity_set_list([objSession.get_user_entity_set("bone"), objSession.get_user_entity_set("liver")], include_relations=True, new_user_entity_set_id="bone_liver")
    objSession.get_intersection_of_user_entity_set_list([objSession.get_user_entity_set("bone"), objSession.get_user_entity_set("lung")], include_relations=True, new_user_entity_set_id="bone_lung")
    objSession.get_intersection_of_user_entity_set_list([objSession.get_user_entity_set("liver"), objSession.get_user_entity_set("lung")], include_relations=True, new_user_entity_set_id="liver_lung")

    print "Intersections are created..", objSession

    return

if __name__ == "__main__":
    get_intersection_of_3_set()

