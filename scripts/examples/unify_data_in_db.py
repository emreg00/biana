#!python

from globals import *

def unify_data_in_db():
    from biana.biana_commands import administration
    list_database_id = [1,2,4,8,10,11,12,13,15,16,18,19,29,30,34,41]
    #list_unification_atom_elements = [(list_database_id,["uniprotaccession"]),(list_database_id, ["uniprotentry"]),(list_database_id,["taxid","proteinsequence"]),(list_database_id,["geneid"]),(list_database_id,["ipi"])]
    list_unification_atom_elements = [(list_database_id,["uniprotaccession"]),(list_database_id,["taxid","proteinsequence"])]
    administration.create_unification_protocol( unification_protocol_name=UNIFICATION_PROTOCOL, list_unification_atom_elements = list_unification_atom_elements, dbname=DB_NAME, dbhost = DB_HOST, dbuser = DB_USER, dbpassword = DB_PASS)
    return

if __name__ == "__main__":
    unify_data_in_db()

