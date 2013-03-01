#!/usr/env/python

from globals import * 

def create_interactome_using_biana(taxids = [9606], node_file_prefix="human_nodes", network_files_prefix="human_network", network_type="experimental", load_from_saved_session = False): 
    """
	Creates ppi specific to an organism network files using BIANA
	taxids: List of id(s) of taxonomy identifier of the organism(s) whose 
		interactome (and in case of more than one organism) interactions 
		between their interactome) will be created
	Node file: node_file_prefix.tsv (as tab seperated values)
	Edge files: network_files_prefix.sif (network in sif format), network_file_prefix_attribute.eda (Cytoscape edge attribute files for each attribute)
	Network type: "experimental" (type interaction, all ppi db but string) | "functional" (type functional_association, only string) | "all" (experimental + functional)
    """
    from biana.biana_commands import available_sessions, create_new_session, save_session, load_session
    from biana.BianaObjects import BianaSessionManager
    
    #DB_NAME = "test_biana" 
    #DB_HOST = "127.0.0.1" 
    #DB_USER = "biana_user" 
    #DB_PASS = "biana_pass" 
    #UNIFICATION_PROTOCOL = "(p2)(noself)(noprevious)uniprot_seqtax_geneid_scoppdb" 

    identifier_description_list = [ ("taxid", taxid) for taxid in taxids ]
    level = 0
    relation_type_list = []
    relation_attribute_restriction_list = []
    node_attributes=["uniprotaccession", "geneid", "ensembl", "genesymbol", "go", "hgnc", "disease", "proteinsequence"]
    relation_attributes=["pubmed", "method_id"]
    if network_type == "functional":
	relation_type_list = ["functional_association"] 
	relation_attributes.extend(["stringscore", "stringscore_neighborhood", "stringscore_fusion", "stringscore_cooccurence", "stringscore_experimental", "stringscore_db", "stringscore_textmining"])
	# Uncommet below for only relations from STRING with score > 700
	#relation_attribute_restriction_list=[("stringscore",">700")]
    elif network_type == "experimental":
	relation_type_list = ["interaction"] #,"reaction"] # ["interaction","reaction","complex","pathway"]
    elif network_type == "all":
	relation_type_list ["interaction", "functional_association"]
    else:
	raise ValueError("Unrecognized network_type!")

    if load_from_saved_session:
    	load_session(network_files_prefix + "_session.dat")
	objSession = available_sessions["biana_session"]
    else:
	# Create a new BIANA session
	create_new_session(sessionID="biana_session",dbname=DB_NAME, dbhost=DB_HOST, dbuser=DB_USER, dbpassword=DB_PASS, unification_protocol=UNIFICATION_PROTOCOL)
	objSession = available_sessions["biana_session"]

	# Create a new User Entity Set (group of biomolecules) in this session
	uESet1 = objSession.create_new_user_entity_set( identifier_description_list=identifier_description_list, attribute_restriction_list=[], id_type="embedded", new_user_entity_set_id="User_Entity_Set_1")
	print "# of nodes: ", objSession.get_user_entity_set("User_Entity_Set_1").getSize()

	# Fetch relations of biomolecules in the set 
	objSession.create_network( user_entity_set_id = "User_Entity_Set_1" , level = level, relation_type_list=relation_type_list, relation_attribute_restriction_list = relation_attribute_restriction_list, include_relations_last_level=True, use_self_relations=True)
	print "# of edges: ", objSession.get_user_entity_set("User_Entity_Set_1").getNumberEdges()

	# Save the session
	save_session("biana_session", network_files_prefix + "_session.dat")

    # Output set details - all nodes
    objSession.output_user_entity_set_details(user_entity_set_id = "User_Entity_Set_1", out_method = open(node_file_prefix + ".tsv", "w").write, attributes=node_attributes, include_level_info=False, include_degree_info=False, level=None, only_selected=False, output_format="tabulated", include_tags_info = False, include_tags_linkage_degree_info=[], substitute_node_attribute_if_not_exists=False, output_1_value_per_attribute=False, include_command_in_rows=False, output_only_native_values=False)

    objSession.output_user_entity_set_details(user_entity_set_id = "User_Entity_Set_1", out_method = open(node_file_prefix + "_only_native.tsv", "w").write, attributes=["uniprotaccession"], include_level_info=False, include_degree_info=False, level=None, only_selected=False, output_format="tabulated", include_tags_info = False, include_tags_linkage_degree_info=[], substitute_node_attribute_if_not_exists=False, output_1_value_per_attribute=False, include_command_in_rows=False, output_only_native_values=True)

    # Export all the information of this set and its network to a file in a tab separated format
    objSession.output_user_entity_set_network_in_sif_format(user_entity_set_id = "User_Entity_Set_1", output_path = "./", output_prefix = network_files_prefix, node_attributes = [], participant_attributes = [], relation_attributes = relation_attributes, output_1_value_per_attribute = False, include_tags = False) 

    ## Export sequences of entries in this set as a FASTA file
    #objSession.output_user_entity_set_sequences_in_fasta(user_entity_set_id="User_Entity_Set_1", only_selected = False, out_method=open("human_fasta.txt",'w').write, type="proteinsequence") #, attributes=["uniprotaccession"])

    ## Export all the information of this set and its network to a file in a tab separated format
    #available_sessions["biana_session"].output_user_entity_set_network(user_entity_set_id = "User_Entity_Set_1", out_method = open(network_files_prefix + ".tsv", "w").write, node_attributes = [], participant_attributes = [], relation_attributes = ["method_id"], allowed_relation_types = "all", include_relation_ids = False, include_participant_ids = True, include_relation_type = True, include_relation_sources = True, output_1_value_per_attribute = False, output_format = "tabulated", only_selected = False, substitute_node_attribute_if_not_exists = False, include_participant_tags = False, include_relation_tags = False, include_unconnected_nodes=False)
    return

if __name__ == "__main__":
    #create_interactome_using_biana(taxids = [9606], node_file_prefix="human_nodes", network_files_prefix="human_network", network_type="experimental", load_from_saved_session = False)
    create_interactome_using_biana(taxids = [9606, 1773], node_file_prefix="human_myco_nodes", network_files_prefix="human_myco_network", network_type="experimental", load_from_saved_session = False)
    #create_interactome_using_biana(taxids = [3702], node_file_prefix="arabidopsis_nodes", network_files_prefix="arabidopsis_network", network_type="experimental", load_from_saved_session = False)
    #create_interactome_using_biana(taxids = [9913], node_file_prefix="cow_nodes", network_files_prefix="cow_network", network_type="experimental", load_from_saved_session = False)
    #create_interactome_using_biana(taxids = [10090], node_file_prefix="mouse_nodes", network_files_prefix="mouse_network", network_type="experimental", load_from_saved_session = False)

