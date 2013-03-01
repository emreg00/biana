import os
import gzip
import sys
import re

from database_info_parameters import parser
import biana.BianaObjects
import biana.BianaDB

parser.add_option("-t","--type", dest="type",
                  help = "proteinsequence or nucleotidesequence", default="")

parser.add_option("--conserve-temporary-files", dest="temporaryfiles",
                  help = "If \"yes\", it does not delete temporary files")

(options, args) = parser.parse_args()

if options.type is None or options.dbname is None:
        parser.print_help()
	sys.exit(1)

temporal_all = "all_sequences.temp"
temporal_removed = "unique_sequences.temp.gz"


def remove_duplicated_sequences_from_file(input_file,output_file):
        """
        Removes all duplicates in a sequence file with the format sequenceID    Sequence

        For duplicated sequences, it takes the smallest sequenceID
        """
        sorted_fd = os.popen("sort -k2 -k1n -T ./ %s" %input_file)

	# Below is alternative to above (above did not work due to broken pipe error but it was caused by sth else)
	#sorted_file = input_file+".sorted"
	#os.system("sort -k2 -k1n -T ./ %s > %s" % (input_file, sorted_file))
	#sorted_fd = open(sorted_file)

        out_file = gzip.open(output_file,'w')

        line_regex = re.compile("(\d+)\t(\w+)")

        previous = None
        for line in sorted_fd:
                m = line_regex.match(line)
                if m:
                        if m.group(2)!=previous:
                                previous = m.group(2)
                                out_file.write(line)
                else:
			print line
                        raise ValueError("File has incorrect format in line: %s" %line)

        #out_file.write(line)
	sorted_fd.close()
	out_file.close()



#First connect to database
dbaccess = biana.BianaDB.BianaDBaccess( dbhost = options.dbhost,
                                        dbuser = options.dbuser,
                                        dbpassword = options.dbpass,
                                        dbname = options.dbname,
                                        lock_tables = True )

#Get all protein sequences in sequence format
all_fd = open(temporal_all, 'w')
dbaccess.output_sequences(all_fd.write, type=options.type, format="seq")
all_fd.close()

#Remove duplicates
remove_duplicated_sequences_from_file(input_file = temporal_all, output_file = temporal_removed )

#Remove temporal files
os.unlink(temporal_all)

#Empty sequence table in database
dbaccess._empty_sequences_table( type = options.type )

#Insert new sequences
dbaccess._insert_sequence_file(input_fd = gzip.open(temporal_removed), type=options.type, format="seq", verbose=True)

#Remove temporal file
if options.temporaryfiles is None:
    os.unlink(temporal_removed)

dbaccess.close()
