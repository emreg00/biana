# Database details
dbhost="127.0.0.1" #"ben-yehuda"
dbname="test_biana" #"test_biana_small"
dbuser="" #"BianaTutorial"
dbpass="" #"Bian4" 
identity=0.9
python_exc=/soft/bin/python

echo "python eliminate_duplicate_sequences.py --dbname=${dbname} --dbuser=${dbuser} --dbpass=${dbpass} --dbhost=${dbhost} --type=proteinsequence" " --conserve-temporary-files=yes"
$python_exc eliminate_duplicate_sequences.py --dbname=${dbname} --dbuser=${dbuser} --dbpass=${dbpass} --dbhost=${dbhost} --type=proteinsequence --conserve-temporary-files=yes

echo "python calculate_sequence_similarities.py --dbname=${dbname} --dbuser=${dbuser} --dbpass=${dbpass} --dbhost=${dbhost} --type=proteinsequence --cd-hit-identity-cluster=${identity}" " --conserve-temporary-files=yes"
$python_exc calculate_sequence_similarities.py --dbname=${dbname} --dbuser=${dbuser} --dbpass=${dbpass} --dbhost=${dbhost} --type=proteinsequence --cd-hit-identity-cluster=${identity} --conserve-temporary-files=yes

