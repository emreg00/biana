from database_info_parameters import parser
import biana


parser.add_option("-d","--description", dest="description",
                  help = "BIANA database description", default="")

(options, args) = parser.parse_args()

if options.dbname is None:
	parser.print_help()
else:
	biana.administration.create_biana_database( dbname = options.dbname,
						    dbuser = options.dbuser,
						    dbhost = options.dbhost,
						    dbpassword = options.dbpass,
						    description = options.description )

