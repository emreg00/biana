# This script implements the automated execution for similarity measures
# It creates a formatted blast database with all protein sequences in the database
# CD Hit results are also inserted in database

from database_info_parameters import parser
import biana.BianaDB
import biana.BianaObjects.sequenceUtilities
import sys
import gzip
import os

TEMP_FASTA_FILE = "temp_sequences_file.fasta"
TEMP_CLSTR_PREFIX = "./temp_sequence_clusters"
TEMP_CLSTR_FILE = TEMP_CLSTR_PREFIX+".clstr"
TEMP_BLAST_RESULTS_FILE = "./blast_results.txt.gz"

parser.add_option("-t","--type", dest="type",
                  help = "proteinsequence or nucleotidesequence", default="")

parser.add_option("--cd-hit-identity-cluster", dest="cdhit_threshold",
                  help = "See CD-HIT documentation", default="")

parser.add_option("--conserve-temporary-files", dest="temporaryfiles",
                  help = "If \"yes\", it does not delete temporary files")

(options, args) = parser.parse_args()

if options.type is None or options.dbname is None:
    parser.print_help()
    sys.exit(1)


# First, gets FASTA file
dbaccess = biana.BianaDB.BianaDBaccess( dbhost = options.dbhost,
                                        dbuser = options.dbuser,
                                        dbpassword = options.dbpass,
                                        dbname = options.dbname,
                                        lock_tables = False )


if not os.path.exists(TEMP_FASTA_FILE):
    out = open(TEMP_FASTA_FILE,'w')
    dbaccess.output_sequences(out.write, type=options.type, format="fasta")
    out.close()


# Second, execute CD-HIT
if not os.path.exists(TEMP_CLSTR_FILE):
    dbaccess.close()
    
    biana.BianaObjects.sequenceUtilities.get_cd_hit_clusters(TEMP_FASTA_FILE, output_path=TEMP_CLSTR_PREFIX, sequence_identity_threshold = float(options.cdhit_threshold))

    # To avoid MySQL server has gone away error
    dbaccess = biana.BianaDB.BianaDBaccess( dbhost = options.dbhost,
					    dbuser = options.dbuser,
					    dbpassword = options.dbpass,
					    dbname = options.dbname,
					    lock_tables = False )
	
# Then, insert clusters information into database
dbaccess.insert_cd_hit_clusters_to_biana_database(cd_hit_clusters_file=TEMP_CLSTR_FILE)


# Calculate similarities by using blast
if not os.path.exists(TEMP_BLAST_RESULTS_FILE):

    blast_out_fd = gzip.open(TEMP_BLAST_RESULTS_FILE, 'w')
    biana.BianaObjects.sequenceUtilities.blast_cd_hit_clusters( cd_hit_clusters_file = TEMP_CLSTR_FILE,
                                                                output_fd = blast_out_fd,
                                                                length_blast_db = 1000, #options.database_size,  #TODO
                                                                effective_length_space_search = 1000, #options.eff_length_size,   #TODO
                                                                dbaccess = dbaccess )
    blast_out_fd.close()


dbaccess._insert_blast_results_file( file_fd = gzip.open(TEMP_BLAST_RESULTS_FILE ) )

dbaccess.close()

if options.temporaryfiles is None:
    os.unlink("temp_sequences_file.fasta")
    os.unlink("./temp_sequence_clusters")
    os.unlink(TEMP_CLSTR_PREFIX+".clstr")
    os.unlink("./blast_results.txt.gz")

