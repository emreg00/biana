
# Cygwin sh script for creating BIANA windows installer from windows_files_to_compile_with_innosetup.tar.gz (created by create_windows_release.sh)

# Remove old files
rm -rf windows_biana_release_files
rm windows_files_to_compile_with_innosetup.tar.gz

# Fetch the archieve from web - Make sure that wget is installed
wget http://sbi.imim.es/data/biana/windows_files_to_compile_with_innosetup.tar.gz

# Extract it
tar xvzf windows_files_to_compile_with_innosetup.tar.gz

# Go inside the folder
cd windows_biana_release_files

# Compile the setup - If you want to remove absoulte path, add Compil32.exe to Cygwin / windows path
"/cygdrive/c/Program Files/Inno Setup 5"/Compil32.exe /cc inno_setup_script.iss

