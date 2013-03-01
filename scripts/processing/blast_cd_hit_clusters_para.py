import sys
import os
from database_info_parameters import parser

parser.add_option("-a","--prefix", dest="prefix",
                  help = "Cluster files prefix")


(options, args) = parser.parse_args()

import time

for current_file in os.popen("ls %s*" %options.prefix):
	current_file = current_file.strip()
	shf = open(current_file+".sh",'w')
	#shf.write("/bin/bash\n")
	#shf.write("source /sbi/users/jgarcia/.bashrc\n")
	shf.write("setenv PYTHONPATH \"/sbi/users/jgarcia/biana/src\"\n")
	shf.write("cd /sbi/users/jgarcia/biana/scripts/administration\n")
	shf.write("python blast_cd_hit_clusters.py -n %s -c %s -i %s -f %s.blast.gz -s 3729884308 -l 632558808656\n" %(options.dbname, options.dbhost, current_file, current_file))
	shf.close()
	#os.system("qsub -q sbi %s.sh" %current_file)

