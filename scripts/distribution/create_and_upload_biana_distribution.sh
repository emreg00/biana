
# Parameters
	# 1- CVS TAG TO EXPORT
	# 2- BIANA VERSION TO SEND

if [ $# -ne 2 ]
then
    echo
    echo "This command needs two parameters: cvs_tag_to_export biana_version_to_send"
    echo
    exit
fi

# remove files
#rm -rf biana_distribution # moving this to the very end
mkdir biana_distribution

cd biana_distribution

cvs export -r $1 biana

# rm biana/biana/biana.bat
rm biana/biana/src/biana/BianaParser/tutorialParser.py 
rm biana/biana/src/biana/BianaParser/pdbParser.py
rm biana/distribution/*
rmdir biana/distribution
rm biana/biana/README.tags

cd biana
tar -cvf biana.v$2.tar biana
gzip biana.v$2.tar
cd ..
scp biana/biana.v$2.tar.gz emre@ben-yehuda:/usr/local/apache2/htdocs/data/biana

cd biana/biana/doc/tutorial
pdflatex tutorial.tex
pdflatex tutorial.tex
cd ../../../../
scp biana/biana/doc/tutorial/tutorial.pdf emre@ben-yehuda:/usr/local/apache2/htdocs/data/biana/biana_tutorial.pdf

cd biana/biana/doc/manual
pdflatex reference_manual.tex
pdflatex reference_manual.tex
cd ../../../../
scp biana/biana/doc/manual/reference_manual.pdf emre@ben-yehuda:/usr/local/apache2/htdocs/data/biana/biana_reference_manual.pdf

# Compile and copy the BianaCytoscapePlugin
cd biana/biana/src/biana/BianaGUI
#javac *.java -target 1.5 -cp /home/jgarcia/programs/Cytoscape/cytoscape-v2.6.0/cytoscape.jar
javac *.java -target 1.5 -cp $HOME/lib/Cytoscape/cytoscape.jar
jar cfm BianaCytoscapePlugin.jar manifest.txt *.class img/ *.props
scp BianaCytoscapePlugin.jar emre@ben-yehuda:/usr/local/apache2/htdocs/data/biana/
cd ../../../../../

# Generate html documentation
cd biana/biana/doc/
sh generate_html.sh

cd ../../../../


# CREATE THE FILES FOR CREATING THE BIANA INSTALLER
sh create_windows_release.sh biana_distribution

rm -rf biana_distribution

echo "Copied biana.v$2.tar.gz to /usr/local/apache2/htdocs/data/biana"
echo "Compiled & Copied tutorial.pdf to /usr/local/apache2/htdocs/data/biana/biana_tutorial.pdf"
echo "Compiled & Copied reference_manual.pdf to /usr/local/apache2/htdocs/data/biana/biana_reference_manual.pdf"
echo "Compiled & Copied BianaCytoscapePlugin.jar to /usr/local/apache2/htdocs/data/biana"
echo "Created windows_files_to_compile_with_innosetup.tar.gz"

