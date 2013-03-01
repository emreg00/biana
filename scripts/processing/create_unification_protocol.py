import biana
from database_info_parameters import parser

(options, args) = parser.parse_args()

#databases_list = [2,3,4,5,6,7,8,9,10,11,12,13,25,24]
databases_list = [1,2,4,8,10,11,12,13,15,16,18,19,29,30,34,35,41]

attributes_list = [ ["uniprotaccession"],
                    ["uniprotentry"],
                    ["taxid","proteinsequence"] ]

#attributes_list = [["geneid"],["ypd"],["orfname","taxid"],["rgd"],["gi"],["gdb"],["mgi"],["cygd"],["uniprotaccession"],["uniprotentry"],["taxid","proteinsequence"],["hgnc"],["ipi"],["genesymbol","taxid"]]

list_unification_atom_elements = [ (databases_list,x) for x in attributes_list ]

biana.administration.create_unification_protocol( unification_protocol_name = "unify_uniprot_seqTax", 
                                                  #list_unification_atom_elements = list_unification_atom_elements, 
                                                  list_unification_atom_elements = list_unification_atom_elements,
                                                  dbname = options.dbname,
                                                  dbhost = options.dbhost,
                                                  dbuser = options.dbuser,
                                                  dbpassword = options.dbpass )

