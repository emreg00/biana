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

from bianaParser import *

class PDBParser(BianaParser):
    """
    PDB Parser Class

    Parses PDB files
    """

    name = "pdb"
    description = "This file implements a program that fills up tables in database biana with information of PDB databsae"
    external_entity_definition = "A external entity represents a structure"
    external_entity_relations = ""

    def __init__(self):

        # Start with the default values

        BianaParser.__init__(self, default_db_description = "PDB database (Protein Data Bank)",
                             default_script_name = "pdbParser.py",
                             default_script_description = PDBParser.description,
                             additional_optional_arguments = [])
        self.default_eE_attribute = "pdb"

    def insert_pdb_2_database(self,pdb_file_fd):

        position = 0
        atom_site_dict = {}

        #pdbObject = pdb.PDB(name="100d",
        #                    resolution=10)
        pdbObject = None

        current_chain_name = None
        current_residue_num = None
        current_atom = None
        struct_title = None

        resolution_regex = re.compile("^_refine.ls_d_res_high\s+(\S+)")
        entry_ID_regex_v1 = re.compile("^_entry.id\s+(\w+)")
        entry_ID_regex_v2 = re.compile("^_pdbx_database_status.entry_id\s+(\w+)")
        atom_regex = re.compile("^ATOM")
        struct_title_regex = re.compile("^_struct.title\s+\'(.*)\'")
        
        atom_site_loop = 0

        for line in pdb_file_fd:
            
            if re.search("^loop_",line):
                atom_site_loop = 0

            # Search for entry id
            entry_ID_search = entry_ID_regex_v1.match(line)
            if entry_ID_search:
                if pdbObject is None:
                    entry_id = entry_ID_search.group(1)
                    pdbObject = PDB(name = entry_id)
                    continue

            entry_ID_search = entry_ID_regex_v2.match(line)
            if entry_ID_search:
                if pdbObject is None:
                    entry_id = entry_ID_search.group(1)
                    pdbObject = PDB(name = entry_id)
                    continue

            resolution_search = resolution_regex.match(line)
            if resolution_search:
                pdbObject.set_resolution(resolution = resolution_search.group(1))
                continue

            # Search for struct title
            struct_title_search = struct_title_regex.match(line)
            if struct_title_search:
                struct_title = struct_title_search.group(1)
                continue

            atom_site_search = re.search("_atom_site\.(\w+)",line)
            if atom_site_search:
                atom_site_loop=1
                atom_site_dict[atom_site_search.group(1)] = position
                position += 1
                continue

            atom_search = atom_regex.match(line)

            if atom_search and atom_site_loop:

                line_fields = re.split("\s+", line)

                # For the moment, we only take a model
                if line_fields[atom_site_dict["pdbx_PDB_model_num"]] != "1":
                    break

                if current_chain_name is None or line_fields[atom_site_dict["auth_asym_id"]] != current_chain_name:
                    current_chain_name = line_fields[atom_site_dict["auth_asym_id"]]

                # New residue
                if current_residue_num is None or line_fields[atom_site_dict["auth_seq_id"]] != current_residue_num:
                    current_residue_num = line_fields[atom_site_dict["auth_seq_id"]]
                    current_residue_type = line_fields[atom_site_dict["auth_comp_id"]]

                current_atom = PDBAtom( atom_num = line_fields[atom_site_dict["id"]],
                                        atom_type = line_fields[atom_site_dict["type_symbol"]],
                                        atom_name = line_fields[atom_site_dict["auth_atom_id"]],
                                        x = line_fields[atom_site_dict["Cartn_x"]],
                                        y = line_fields[atom_site_dict["Cartn_y"]],
                                        z = line_fields[atom_site_dict["Cartn_z"]])

                pdbObject.add_atom( atomObject = current_atom, chain_name = current_chain_name, residue_num = current_residue_num, residue_type = current_residue_type )

                continue

        self.biana_access.insert_pdb_object(PDBObject = pdbObject, source_database = self.database, description=struct_title )
        

    def parse_database(self):
        """
        """

        self.pdbs_done = 0

        def insert_pdb(arg, dirname, names):
            for name in names:
                if name.endswith('.cif.gz'):
                    sys.stderr.write("Going to parse file %s\n" %name)
                    try:
                        file_fd = gzip.open(os.path.join(dirname,name),'r')
                        self.insert_pdb_2_database(file_fd)
                    	#file_fd.close()
                    except:
                        print "Error parsing file %s: " %name
                        traceback.print_exc()
                    self.pdbs_done += 1
                    file_fd.close()

                    if self.time_control:
                        if self.pdbs_done%10==0:
                            sys.stderr.write("%s pdbs done in %s seconds\n" %(self.pdbs_done, time.time()-self.initial_time))

        # Run all the path to insert all the hssp files of the path
        os.path.walk(self.input_file,insert_pdb,None)

        return
        
