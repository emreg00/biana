
import java.util.HashSet;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;

public class ExternalDatabase {

    /* Available Attributes */
    /* Available External Entity Types, with the available attributes for it */
    private Hashtable<String,HashSet<String>> available_eE_type_attributes;
    private HashSet<String> available_eEr_types;
    private String name;
    private String version;
    private String description;
    private String id;
	

    public ExternalDatabase(String pName, String pVersion, String pDescription, String id) {
	this.available_eE_type_attributes = new Hashtable<String, HashSet<String>>();
	this.available_eEr_types = new HashSet<String>();
	this.name = pName;
	this.version = pVersion;
	this.id = id;
	this.description = pDescription;
	this.add_available_eE_type("eE");
	this.add_available_eE_type("relation");
    }

    public String getID(){
	return this.id;
    }
    
    public String toString(){
	return this.name+" ["+this.version+"]";
    }

    public void add_available_eE_type(String peEType){
	if( this.available_eE_type_attributes.containsKey(peEType) ){
	    return;
	}
	this.available_eE_type_attributes.put(peEType, new HashSet<String>());
    }
    
    public void add_available_eEr_type(String attribute){
	this.available_eEr_types.add(attribute);
    }

    
    public void add_available_eE_attribute(String peEType, String attribute){
	if( this.available_eE_type_attributes.containsKey(peEType) ){
	    this.available_eE_type_attributes.get(peEType).add(attribute);
	}
	else{
	    this.add_available_eE_type(peEType);
	    this.available_eE_type_attributes.get(peEType).add(attribute);
	}
    }
    
    public HashSet<String> getAvailable_eEType(){
	return (HashSet<String>)this.available_eE_type_attributes.keySet();
    }

    public HashSet<String> getAvailable_eErTypes(){
	return this.available_eEr_types;
    }
    
    public HashSet<String> getAvailableAttributes(String peEType){
	return this.available_eE_type_attributes.get(peEType);
    }

    public static ArrayList<String> getCommonDatabases(String peEType, Vector<ExternalDatabase> pExternalDatabases, ArrayList<String> listAttribute){
    	ArrayList<String> result = new ArrayList<String>();
    	boolean flagDBincluded;
    	for( Iterator it = pExternalDatabases.iterator(); it.hasNext(); ){
    		flagDBincluded = false;
    		ExternalDatabase eDB = (ExternalDatabase)it.next();
    		HashSet<String> attr = eDB.getAvailableAttributes(peEType);
    		for( Iterator itInner = listAttribute.iterator(); itInner.hasNext(); ){
    			if(attr.contains(itInner.next())) {
    				result.add(eDB.name);
    				flagDBincluded = true;
    				continue;
    			}
    		}
    		if(flagDBincluded) {
    			continue;
    		}
    	}
    	return result;
    }
    
    public static ArrayList<String> getCommonAttributes(String peEType, ArrayList<ExternalDatabase> eD){
	Hashtable<String,Integer> t = new Hashtable<String,Integer>();
	ArrayList<String> result = new ArrayList<String>();
	if( eD.size()>0 ){
	    HashSet<String> attr = (eD.get(0)).getAvailableAttributes(peEType);
	    for( Iterator it = attr.iterator(); it.hasNext(); ){
		String current_attribute = (String)it.next();
		t.put(current_attribute,1);
		for( int j=1; j<eD.size(); j++ ){
		    if( (eD.get(j)).getAvailableAttributes(peEType).contains(current_attribute) ){
			t.put(current_attribute,(int)t.get(current_attribute)+1);
		    }
		}
	    }
	}
	for( Enumeration e = t.keys(); e.hasMoreElements() ;) {
	    String ac = (String)e.nextElement();
	    if( t.get(ac) == eD.size() ){
		result.add(ac);
	    }
	}
	
	java.util.Collections.sort(result);

	return result;
    }


    public static Vector<String> getAlleErTypes(Vector<ExternalDatabase> eDv){
	Vector<String> result = new Vector<String>();
	HashSet<String> resultSet = new HashSet<String>();
	for( Iterator iteDv = eDv.iterator(); iteDv.hasNext(); ){
	    HashSet<String> types = ((ExternalDatabase)iteDv.next()).getAvailable_eErTypes();
	    for( Iterator it = types.iterator(); it.hasNext(); ){
		String current_type = (String)it.next();
		resultSet.add(current_type);
	    }
	}
	for( Iterator it = resultSet.iterator(); it.hasNext();) {
	    String ac = (String)it.next();
	    result.add(ac);
	}

	java.util.Collections.sort(result);

	return result;
    }


    public static Vector<String> getAllAttributes(String peEType, Vector<ExternalDatabase> eDv){
	Vector<String> result = new Vector<String>();
	HashSet<String> resultSet = new HashSet<String>();
	for( Iterator iteDv = eDv.iterator(); iteDv.hasNext(); ){
	    HashSet<String> types = ((ExternalDatabase)iteDv.next()).getAvailableAttributes(peEType);
	    for( Iterator it = types.iterator(); it.hasNext(); ){
		String current_type = (String)it.next();
		resultSet.add(current_type);
	    }
	}
	for( Iterator it = resultSet.iterator(); it.hasNext();) {
	    String ac = (String)it.next();
	    result.add(ac);
	}

	java.util.Collections.sort(result);

	return result;
    }
}

