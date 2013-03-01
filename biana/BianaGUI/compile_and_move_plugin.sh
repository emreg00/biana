JAVAPATH=$HOME/lib/jdk1.5.0_14/bin/
$JAVAPATH/javac *.java
$JAVAPATH/jar cfm BianaCytoscapePlugin.jar manifest.txt *.class plugin.props BIANA.props img/ #> /dev/null
mv BianaCytoscapePlugin.jar ~/lib/Cytoscape_v2.6.0/plugins/ 


# OBSOLETE
##$JAVAPATH/javac -cp $CLASSPATH:JavaApplication1.jar *.java
##$JAVAPATH/jar cvf BIANA.jar JavaApplication1.jar lib/ *.class img/ > /dev/null
##$JAVAPATH/jar cvf BIANA.jar lib/ *.class img/ > /dev/null

