import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;

public class BianaDatabase{

    private String dbname = null;
    private String dbuser = null;
    private String dbpass = null;
    private String dbhost = null;
    private String description = null;
    
    private Vector<String> unification_protocols = null;
    private Vector<ExternalDatabase> available_external_databases = null;
    private Hashtable<String, String> attribute_ontology = null;
	
    public BianaDatabase(String dbname, String dbuser, String dbpass, String dbhost, String description, Vector<String> pUnificationProtocols, Vector<ExternalDatabase> pExternalDatabases, Hashtable<String,String> pAttributeOntologies){
	this.dbname = dbname;
	this.dbuser = dbuser;
	this.dbpass = dbpass;
	this.dbhost = dbhost;
	this.description = description;
	this.unification_protocols = pUnificationProtocols;
	this.available_external_databases = pExternalDatabases;
	this.attribute_ontology = pAttributeOntologies;
    }
    
    public String toString(){
	return this.dbname+" at "+this.dbhost;
    }
    
    public String getDbhost() {
	return dbhost;
    }
    public void setDbhost(String dbhost) {
	this.dbhost = dbhost;
    }
    public String getDbname() {
	return dbname;
    }
    public void setDbname(String dbname) {
	this.dbname = dbname;
    }
    public String getDbpass() {
	return dbpass;
    }
    public void setDbpass(String dbpass) {
	this.dbpass = dbpass;
    }
    public String getDbuser() {
	return dbuser;
    }
    public void setDbuser(String dbuser) {
	this.dbuser = dbuser;
    }
    
    public String getDescription() {
	return description;
    }
    
    public void setDescription(String description) {
	this.description = description;
    }
    
    public boolean equals(Object db2){
	if( this.dbname.compareTo(((BianaDatabase)db2).getDbname())==0 && this.dbhost.compareTo(((BianaDatabase)db2).getDbhost())==0 ){
	    return true;
	}
	return false;
    }
    
    public int hashCode() {
    	return this.getDbname().hashCode()+this.getDbhost().hashCode();
    }

    
    public String[] getUnification_protocols() {
	String[] t = new String[this.unification_protocols.size()];
	unification_protocols.toArray(t);
	return t;
    }
    

    public void addExternalDatabase(ExternalDatabase pED){
	this.available_external_databases.add(pED);
    }
    
    public Vector<ExternalDatabase> getAvailableExternalDatabases() {
	return available_external_databases;
    }

    public Iterator getAvailableExternalDatabasesIterator(){
	return this.available_external_databases.iterator();
    }

    public boolean hasOntology(String attributeName){
	return this.attribute_ontology.containsKey(attributeName);
    }

    public String getOntologyName(String attributeName){
	
	if( this.hasOntology(attributeName) ){
	    return this.attribute_ontology.get(attributeName);
	}
	System.err.println("Trying to get an ontology from an invalid attribute");
	return null;
    }


}
