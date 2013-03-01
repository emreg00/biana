import biana.BianaDB
import sys
from database_info_parameters import parser

parser.add_option("-i","--input-file", dest="input_file",
                  help = "Sequence file", default="")

(options, args) = parser.parse_args()

dbaccess = biana.BianaDB.BianaDBaccess(dbname = options.dbname,
                                       dbuser = options.dbuser,
				       dbhost = options.dbhost,
                                       dbpassword = options.dbpass,
                                       lock_tables = True)

dbaccess._insert_blast_results_file( file_path=options.input_file )

dbaccess.close()

