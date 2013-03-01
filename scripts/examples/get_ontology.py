#!python

from globals import * 

def get_ontology():
    from biana.biana_commands import available_sessions, create_new_session
    # Create a new BIANA session
    create_new_session(sessionID="biana_session",dbname=DB_NAME, dbhost=DB_HOST, dbuser=DB_USER, dbpassword=DB_PASS, unification_protocol=DEFAULT_UNIFICATION_PROTOCOL)
    objSession = available_sessions["biana_session"]
    ontology = objSession.get_ontology(ontology_name="psimiobo", root_attribute_values = [4])
    print map(int, ontology.get_all_linked_attributes())
    ontology = objSession.dbAccess.get_ontology(ontology_name="psimiobo", root_attribute_values = [45], load_external_entities=True)
    #ontology = objSession.dbAccess.get_ontology(ontology_name="psimiobo", root_attribute_values = [59, 109, 63], load_external_entities=True)
    print map(lambda x: (int(x[0]), x[1]), ontology.get_linked_attr_and_description_tuples())
    return 

if __name__ == "__main__":
    get_ontology()

