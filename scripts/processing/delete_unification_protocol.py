from database_info_parameters import parser
import biana

biana.administration.delete_unification_protocol( unification_protocol_name = "uniprot_seq_hgnc_ipi_unification", 
                                                  dbname = options.dbname,
                                                  dbhost = options.dbhost,
                                                  dbuser = options.dbuser,
                                                  dbpassword = options.dbpass )

