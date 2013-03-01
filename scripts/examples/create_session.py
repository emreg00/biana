#!python

from globals import * 
from time import clock

def create_session():
    from biana.biana_commands import available_sessions, create_new_session

    # Create a new BIANA session
    t1=clock()
    create_new_session(sessionID="biana_session",dbname=DB_NAME, dbhost=DB_HOST, dbuser=DB_USER, dbpassword=DB_PASS, unification_protocol=DEFAULT_UNIFICATION_PROTOCOL)
    objSession = available_sessions["biana_session"]
    t2=clock()
    print "Time: ", t2-t1

    return

if __name__ == "__main__":
    create_session()

