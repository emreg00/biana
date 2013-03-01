"""
    BIANA: Biologic Interactions and Network Analysis
    Copyright (C) 2009  Javier Garcia-Garcia, Emre Guney, Baldo Oliva

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

"""

#########################################################################
# BIANA output modification Utility Library 
# Methods 
#   to get psuedo-representative uniprot accession ids from tsv formatted node output file
#   to map uniprots to genes (vice verca) in tsv node output
#   to filter interaction network based on method type
#   to select/convert sequence from BIANA tsv output to fasta
#
# eg 22/06/2009
#########################################################################

from biana.utilities import TsvReader
from biana.utilities import FastaWriter

def get_representative_uniprot_accessions_from_tsv_file(tsv_file_name, in_value_separator = ","):
    """
	! Word representative is contraversial here, selected id may not represent the user entity perfectly !
	! Highly recommended to use output_only_unique_values (most likely in combination with identifier_utilities.select_swissprot_accessions) !
	! in node info outputting instead of using this method !

	Returns a list of uniprot accession ids from tsv formatted node output file for each user entity
	Trys to select swissprot ids
    """
    import re
    swissprot_exp = re.compile("\w\d\d\d\d\d")
    reader = TsvReader.TsvReader(tsv_file_name, inner_delim=in_value_separator)
    #columns, id_to_vals = reader.process(fields_to_include = ["User Entity ID", "uniprotaccession"], overwrite_keys = True)
    columns, id_to_vals = reader.read(fields_to_include = ["User Entity ID", "uniprotaccession"], merge_inner_values = True)
    #print columns, id_to_vals.items()[0]
    ids = set()
    for ueid, vals in id_to_vals.iteritems():
	#words = vals[columns["uniprotaccession"]].split(in_value_separator)
	words = reduce(lambda x,y: x+y, vals)
	local_ids = []
	containedP = None
	containedO = None
	for id in words:
	    id = id.strip()
	    if re.match(swissprot_exp, id):
		#ids.add(id)
		if id.startswith("P"):
		    containedP = id
		if id.startswith("O"):
		    containedO = id
	    local_ids.append(id)
	if len(local_ids) > 1:
	    if containedP is not None:
		ids.add(containedP)
	    elif containedO is not None:
		ids.add(containedO)
	    else:
		#print local_ids
		ids.add(local_ids[0])
	#elif len(local_ids) == 0:
	#    print "no swissprot related primary accession found:", ueid
	else:
	    #print local_ids
	    ids.add(local_ids[0])
    return ids


def select_representative_sequence(seq_list):
    """
	From the list of sequences, selects the longest
	Before, was selecting the longest of the most frequently occuring sequences

	seq_list: list of sequences
    """
    max_len = -1 
    seq_selected = None
    for val in seq_list:
	val = val.strip()
	size = len(val)
	if size > max_len:
	    max_len = size
	    seq_selected = val
    return seq_selected

    # Since BIANA gives values for an attribute as a set, 
    # checking occurences is pointless!
    seqToCount = {}
    for val in seq_list:
	val = val.strip()
	seqToCount[val] = seqToCount.setdefault(val, 0) + 1
    seqCounts = seqToCount.items()
    seqCounts.sort(lambda x,y: cmp(x[1],y[1]))
    seqCounts.reverse()
    nCount = seqCounts[0][1]
    seq_selected = seqCounts[0][0]
    seq_len = 0
    for seq, n in seqCounts:
	if n < nCount:
	    break
	if len(seq) > seq_len:
	    seq_selected = seq
	    seq_len = len(seq)
    return seq_selected


def convert_sequence_in_tsv_to_fasta(tsv_file_name, fasta_file_name, representative_sequence_selection=False, in_value_separator = ","):
    """
	Converts sequence info contained in BIANA user entity output file to fasta formatted sequence file

	tsv_file_name: BIANA user entity output file
	fasta_file_name: Fasta formatted sequence file to be created
	representative_sequence_selection: if True uses select_representative_sequence to select from multiple sequences given for a user entity 
	in_value_separator: Character used in BIANA user entity outout file to seperate values
    """
    reader = TsvReader.TsvReader(tsv_file_name, inner_delim = in_value_separator)
    columns, id_to_vals = reader.read(fields_to_include = ["user entity id", "proteinsequence"], merge_inner_values = True)
    file_out = open(fasta_file_name, "w")
    fwriter = FastaWriter.FastaWriter(out_method=file_out.write, one_line_per_sequence=True) 
    for id, vals in id_to_vals.iteritems():
	if representative_sequence_selection:
	    words = reduce(lambda x,y: x+y, vals)
	    seq_selected = select_representative_sequence(words)
	else:
	    seq_selected = vals[0][columns["proteinsequence"]].split(in_value_seperator)[0].strip()
	if seq_selected == "-":
	    continue
	fwriter.output_sequence(id, seq_selected)
    file_out.close()
    #file_out = open(fasta_file_name+"2", "w")
    #reader.process(file_out.write, fields_to_include=["user entity id", "proteinsequence"])
    #file_out.close()
    return

def get_attribute_to_attribute_mapping(tsv_file_name, attribute, to_attribute, in_value_separator = ",", out_file_name=None, keys_to_include=None, include_inverse_mapping=True):
    """
	Returns mapping of two attributes contained in BIANA user entity output file as a dictionary where attribute => to_attribute

	tsv_file_name: BIANA user entity output file
	attribute: attribute to be mapped to to_attribute
	to_attribute: attribute mapped from attribute
	in_value_separator: Character used in BIANA user entity outout file to seperate values
	out_file_name: If not None, creates an output file with only attribute and to_attribute fields 
	keys_to_include: If None, all lines (rows) in the input file is used, if a list of keys as the first field in the file, 
			 only the lines that contains the value for the field are included in the mapping.
	include_inverse_mapping: If True returns a (to_attribute, attribute) mapping dictionary in addition to (attribute, to_attribute) mapping dictionary
    """
    reader = TsvReader.TsvReader(tsv_file_name, inner_delim = in_value_separator)
    columns, id_to_vals = reader.read(fields_to_include = [attribute, to_attribute], merge_inner_values = True, keys_to_include = keys_to_include) 

    if out_file_name is not None:
	file_out = open(out_file_name, "w")
	reader.process(file_out.write, fields_to_include=[attribute, to_attribute]) 
	file_out.close()
	return
    attribute_to_nodes = {}
    node_to_attributes = {}
    for id, vals in id_to_vals.iteritems():
	words = reduce(lambda x,y: x+y, vals)
	if include_inverse_mapping:
	    [ attribute_to_nodes.setdefault(val.strip(), set()).add(id) for val in words ] 
	[ node_to_attributes.setdefault(id, set()).add(val.strip()) for val in words ]  
    if include_inverse_mapping:
	return node_to_attributes, attribute_to_nodes
    else:
	return node_to_attributes, None


def get_user_entity_attribute_mapping(tsv_file_name, attribute, out_file_name=None):
    """
	Returns mapping between user entity id and another attribute based on BIANA user entity output file
    """
    return get_attribute_to_attribute_mapping(tsv_file_name, "user entity id", attribute, out_file_name)


def filter_network_by_interaction_attribute_value(network_attribute_file_name, network_out_file_name, accept_attribute_value):
    """
	Creates a new network sif file removing interactions based on the value of the interaction attribute such that value returns false with accept_attribute_value

	network_attribute_file_name: BIANA network output sif method_id attribute file 
	network_out_file_name: Network file in sif format to be created
	accept_attribute_value: A function that returns True or False based on the attribute value

	REMEMBER that comparisons inside accept_attribute_value must take into consideration that the compared value is a string (cast to number if necessary)
	Typical accept_attribute_value: lambda x: float(x) > 0
    """
    f = open(network_attribute_file_name)
    valid_interactions = set()
    for line in f:
	words = line.strip().split()
	if len(words) == 1:
	    continue
	if words[3] != "=":
	    raise Exception("Sif attribute file format error! %s" % line)
	if accept_attribute_value(words[4]):
	    valid_interactions.add(" ".join([words[0], words[1].rstrip(')').lstrip('('), words[2]]))
    f.close()
    f_out = open(network_out_file_name, 'w')
    for i in valid_interactions:
	f_out.write(i+"\n")
    f_out.close()
    return
 

def filter_network_by_interaction_type(network_attribute_file_name, network_out_file_name, interaction_type="y2h", reverse_selection=False):
    """
	Creates a new network sif file removing interactions based on method detection type

	network_attribute_file_name: BIANA network output sif method_id attribute file 
	network_out_file_name: Network file in sif format to be created
	interaction_type: "y2h" | "tap" 
	reverse_selection: True => no tap / no y2h
    """
    y2h = set([18,397,727,728,437,398,399])
    tap = set([4,96,676,729,19,6,7,858,59,109])
    #other = set([114, 441, 492, 493, 802]) # xray, sga, in vitro, in vivo, enhancement
    if interaction_type=="y2h":
	valid_ids = y2h
    elif interaction_type=="tap":
	valid_ids = tap
    else:
	print "Unsupported interaction type", interaction_type
	return
    if reverse_selection:
	f = lambda x: int(x) not in valid_ids
    else:
	f = lambda x: int(x) in valid_ids
    filter_network_by_interaction_attribute_value(network_attribute_file_name, network_out_file_name, accept_attribute_value = f)
    return
    f = open(network_attribute_file_name)
    valid_interactions = set()
    for line in f:
	words = line[:-1].split()
	if len(words) == 1 and line.startswith("method_id"):
	    continue
	if words[3] != "=":
	    print "format error", line
	    continue
	if reverse_selection:
	    if int(words[4]) not in valid_ids:
		valid_interactions.add(" ".join([words[0], words[1], words[2]]))
	else:
	    if int(words[4]) in valid_ids:
		valid_interactions.add(" ".join([words[0], words[1], words[2]]))
    f.close()
    f_out = open(network_out_file_name, 'w')
    for i in valid_interactions:
	f_out.write(i+"\n")
    f_out.close()
    return

def get_tap_method_ids(objSession):
    tap = set(map(int, objSession.get_ontology(ontology_name="psimiobo", root_attribute_values = [4]).get_all_linked_attributes()))
    tap.add(59)
    tap.add(109)
    return tap
    #return "select * from ExternalEntityOntology_isA A, externalEntitypsimi_name N, key_attribute_3 K, key_attribute_3 K2 where K.externalEntityID=A.is_a and A.externalEntityID=N.externalEntityID and N.externalEntityID=K2.externalEntityID and K.value IN (4,96,676,19,6,7,729,858,59,109)"
    # 59 (gst pull down)
    # 109 (tap tag coimmunoprecipitation)
    # considering 25 (copurification, obsolute because not specific, can be tap related or not..)
    # 46, 63 (interaction prediction) also 58, 101, 105

def get_y2h_method_ids(objSession):
    y2h = set(map(int, objSession.get_ontology(ontology_name="psimiobo", root_attribute_values = [18]).get_all_linked_attributes()))
    return y2h
    #return "select * from ExternalEntityOntology_isA A, externalEntitypsimi_name N, key_attribute_3 K, key_attribute_3 K2 where K.externalEntityID=A.is_a and A.externalEntityID=N.externalEntityID and N.externalEntityID=K2.externalEntityID and K.value IN (18,397,727,728,437,398,399)"

def get_experimental_method_ids(objSession):
    experimental = set(map(int, objSession.get_ontology(ontology_name="psimiobo", root_attribute_values = [45]).get_all_linked_attributes()))
    return experimental

def get_predicted_method_ids(objSession):
    predicted = set(map(int, objSession.get_ontology(ontology_name="psimiobo", root_attribute_values = [63]).get_all_linked_attributes()))
    return predicted 

if __name__ == "__main__":
    node_to_genes, gene_to_nodes = get_user_entity_gene_mapping(tsv_file_name="/home/emre/arastirma/data/human_interactome_biana/test_nodes.tsv", out_file_name="test.txt")
    print node_to_genes, gene_to_nodes

