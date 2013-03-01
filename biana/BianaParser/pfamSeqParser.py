"""
File        : pfamParser.py
Author      : Javier Garcia
Creation    : 19 December 2007
Contents    : fills up tables in database biana with information from PFAM
Called from : 
=======================================================================================================

"""


## STEP 1: IMPORT NECESSARY MODULES

from bianaParser import *


class PFAMSeqParser(BianaParser):
    """
    PFAM Seq Parser Class
    """
    
    name = "pfamseq"
    description = "PFAM Sequence Parser. Collection of multiple sequence alignments and hidden markov models covering many common protein domains and families"
    external_entity_definition = "A external entity represents a pfam domain as a sequence"
    external_entity_relations = ""

    def __init__(self):

        # Start with the default values

        BianaParser.__init__(self, default_db_description = "Collection of multiple sequence alignments and hidden markov models covering many common protein domains and families",
                             default_script_name = "pfamSeqParser.py",
                             default_script_description = PFAMParser.description,
                             additional_optional_arguments = [("pfamSeq-file-name=","pfamseq.gz","Name of the file with pfam sequences")])
        self.default_eE_attribute = "proteinSequence"

    def parse_database(self):
        """
        Method that implements the specific operations of PFAM parser
        """

        if not self.input_file.endswith(os.sep):
            self.input_file += os.sep

        # Get the exact information of sequences used and inserts them to the database
        
        pfamseq_file = self.input_file+self.arguments_dic["pfamSeq-file-name"]

        if pfamseq_file.endswith(".gz"):
            pfamseq_input_file_fd = gzip.open(pfamseq_file,'r')
        else:
            pfamseq_input_file_fd = file(pfamseq_file, 'r')

        sequence = []
        protein_title_line = None

        protein_number = 0
        
        #self.title_regex = re.compile("^(\w+)\.(\d+)")

        uniprot_acc = None
        uniprot_acc_version = None
        uniprot_entry = None
        sequence = None

        self.title_regex = re.compile("^(\w+)\.(\d+)\s+(\S+)")

        for line in pfamseq_input_file_fd:

            if line[0]==">":
                protein_number += 1

                if self.time_control:
                    if protein_number%20000==0:
                        sys.stderr.write("%s proteins in %s seconds\n" %(protein_number,time.time()-self.initial_time))

                if sequence is not None:
                    self.parse_pfam_seq_record(header_line = protein_title_line, sequence = "".join(sequence))

                protein_title_line = line[1:]
                sequence = []
            else:
                sequence.append(line.strip())
    
        if len(sequence)>0:
            self.parse_pfam_seq_record(header_line = protein_title_line, sequence = "".join(sequence))

        pfamseq_input_file_fd.close()


    def parse_pfam_seq_record(self, header_line, sequence):

        pfamseq_object = ExternalEntity( source_database = self.seq_database, type="protein" )

        m = self.title_regex.match(header_line)

        if m:
            uniprot_acc = m.group(1)
            uniprot_acc_version = m.group(2)
            uniprot_entry = m.group(3)
        else:
            raise "ERROR in parsing line %s" %header_line

        pfamseq_object.add_attribute(ExternalEntityAttribute( attribute_identifier = "uniprotentry", value = uniprot_entry,type = "cross-reference" ))
        pfamseq_object.add_attribute(ExternalEntityAttribute( attribute_identifier = "uniprotaccession", value = uniprot_acc, version = uniprot_acc_version, type = "cross-reference"))

        sequenceObject = ProteinSequence(sequence.strip())

        pfamseq_object.add_attribute(ExternalEntityAttribute( attribute_identifier = "proteinSequence", value = sequenceObject, type="cross-reference"))

        self.biana_access.insert_new_external_entity( externalEntity = pfamseq_object )
            

