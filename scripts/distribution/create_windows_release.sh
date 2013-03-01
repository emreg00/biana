# PARAMETERS
#       1. BIANA distribution directory (biana_distribution exported from CVS)

# Uncompress the files
tar -xzvf windows_biana_release_files.tar.gz

# Copy the bat file
cp $1/biana/biana/biana.bat windows_biana_release_files/release/
cp $1/biana/biana/README windows_biana_release_files/release/
cp inno_setup_script.iss windows_biana_release_files/

# Copy the BIANA files
cp -r $1/biana/biana/src/biana windows_biana_release_files/release/ext/python2.5/Lib/site-packages/

# Copy the networx module
cp -r $1/biana/biana/ext/networkx/networkx windows_biana_release_files/release/ext/python2.5/Lib/site-packages/biana/ext/

# Copy the Windows MySQLdb module
tar -xzvf MySQLdb_windows.tar.gz
mv MySQLdb windows_biana_release_files/release/ext/python2.5/Lib/site-packages/biana/ext/
mv _mysql* windows_biana_release_files/release/ext/python2.5/Lib/site-packages/biana/ext/

# Copy the documentation
cp -r $1/biana/biana/doc/tutorial/tutorial.pdf windows_biana_release_files/release/doc/
cp -r $1/biana/biana/doc/manual/reference_manual.pdf windows_biana_release_files/release/doc/

# Copy the scripts files
cp -r $1/biana/biana/scripts/* windows_biana_release_files/release/scripts/

# Copy the BIANA Cytoscape plugin file
cp -r $1/biana/biana/src/biana/BianaGUI/BianaCytoscapePlugin.jar windows_biana_release_files/release/

tar -czvf windows_files_to_compile_with_innosetup.tar.gz windows_biana_release_files/

# Remove all the files
rm -rf windows_biana_release_files
rm MySQLdb
rm _mysql*

#cp windows_files_to_compile_with_innosetup.tar.gz /sbi/users/emre
scp windows_files_to_compile_with_innosetup.tar.gz emre@ben-yehuda:/usr/local/apache2/htdocs/data/biana

