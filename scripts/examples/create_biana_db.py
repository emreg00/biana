#!python

from globals import *

def create_biana_db():
    from biana.biana_commands import administration
    administration.create_biana_database( dbname = DB_NAME, dbhost = DB_HOST, dbuser = DB_USER, dbpassword = DB_PASS, description = "BIANA Test Database" )
    return

if __name__ == "__main__":
    create_biana_db()

