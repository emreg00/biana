from database_info_parameters import parser
import biana.BianaObjects
import biana.BianaDB

parser.add_option("-i","--input-file", dest="input_file",
                  help = "CD HIT clusters file", default="")

(options, args) = parser.parse_args()

dbaccess = biana.BianaDB.BianaDBaccess( dbhost = options.dbhost,
                                        dbuser = options.dbuser,
                                        dbpassword = options.dbpass,
                                        dbname = options.dbname,
                                        lock_tables = True )

biana.BianaObjects.sequenceUtilities.insert_cd_hit_clusters_to_biana_database(cd_hit_clusters_file = options.input_file,
                                                                              dbaccess = dbaccess)

dbaccess.close()
