#!python

from globals import * 

def check_biana_db():
    from biana.biana_commands import administration
    administration.check_database(dbname = DB_NAME, dbhost = DB_HOST, dbuser = DB_USER, dbpassword = DB_PASS)
    return

if __name__ == "__main__":
    check_biana_db()

