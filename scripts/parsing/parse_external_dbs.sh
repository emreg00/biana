#./run_parser.sh <parser_name> <db_name> <version> <input_file_name>(default value: "") <is_promiscous>(default 0, 1:promiscous) <default_attribute>(if applicable) 
#./run_parser.sh uniprot uniprot_sprot Feb_5_09 uniprot_sprot.dat.gz 0 
#./run_parser.sh uniprot uniprot_trembl Feb_5_09 uniprot_trembl.dat.gz 0
#./run_parser.sh taxonomy taxonomy Feb_4_09 "" 0
#./run_parser.sh hgnc hgnc Feb_5_09 hgnc.txt 0 
#./run_parser.sh go_obo go Feb_12_09 gene_ontology_edit.obo 0
#./run_parser.sh psi_mi_obo psi_mi_obo Feb13 psi-mi25.obo 0
#./run_parser.sh psi_mi_2.5 intact Jan13 "" 1 intact
#./run_parser.sh psi_mi_2.5 biogrid Jan13 "" 1 accessionnumber
#./run_parser.sh psi_mi_2.5 hprd Apr10 "" 1 hprd
#./run_parser.sh psi_mi_2.5 mint Dec11 "" 1 mint
#./run_parser.sh psi_mi_2.5 dip Jan13 "" 1 dip
#./run_parser.sh psi_mi_2.5 mpact Oct08 "" 1 cygd
#./run_parser.sh psi_mi_2.5 bind Unk05 "" 0 name # not in psi_mi_2.5 format (in psi_mi_2 format)
./run_parser.sh biopax_level_2 reactome Dec12 biopax.zip 1 reactome
#./run_parser.sh string string v7.1 "" 1
#./run_parser.sh kegg_ko kegg_ko Feb_5_09 ko 1
#./run_parser.sh kegg_ligand kegg_ligand Feb_5_09 "" 0
#./run_parser.sh kegg_gene kegg_gene Feb_5_09 genes.tar.gz 0
#./run_parser.sh ipi ipi Feb_12_09 "" 0
#./run_parser.sh cog cog Mar_5_03 "" 1
#./run_parser.sh scop scop 1.73 "" 1
#./run_parser.sh pfam pfam Feb_12_09 "" 1
#./run_parser.sh ncbi_genpept genpept v1 "" 0
#./run_parser.sh nr nr v1 "" 0
#./run_parser.sh generic input_database 1 input_database4_v2.txt 0 name
