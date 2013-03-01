
# Database details
#dbhost=sefarad
dbhost=ben-yehuda
#dbhost=127.0.0.1
dbname=test_BIANA_JANUARY_2013 #test_biana #_small #new #emre
#dbname=pianav2 
#dbname=biana_beta
#dbuser=piana
dbuser="jgarcia" #emre
#dbuser=bianaweb
dbpass="" # for no password
#dbpass=piana
#dbpass=pianaBiana

# Get command line arguments
parser=$1 
db=$2 
ver=$3 
file=$4 
promiscuous=$5
default_attribute=$6

# External database directory
biana_databases_path="/sbi/users/interchange/DATA/BIANA" #BIANA_FILES/biana_databases

base_command="python parse_database.py ${parser} --input-identifier=${biana_databases_path}/${db}/${file} --biana-dbname=${dbname} --biana-dbhost=${dbhost} --database-name=${db} --database-version=${ver} --time-control --verbose"

if [ ! -z $default_attribute ]; then
    base_command=${base_command}" --default-attribute=${default_attribute}"
fi

if [ $promiscuous -eq 1 ]; then
    base_command=${base_command}" --promiscuous"
fi

if [ ! -z $dbuser ]; then
    base_command=${base_command}" --biana-dbuser=${dbuser}"
fi

if [ ! -z $dbpass ]; then
    base_command=${base_command}" --biana-dbpass=${dbpass}"
fi

# Comment if you do not want to optimize for parsing
base_command=${base_command}" --optimize-for-parsing"

# Uncomment if you want to use already created string alias table (saves a few mins)
#base_command=${base_command}" --use-existing-temp-alias-table"

base_command=${base_command}

echo ${base_command}
${base_command} > ${db}".out" 2> ${db}".err"

