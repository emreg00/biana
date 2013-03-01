import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Collection;
import javax.swing.JTree;
import java.util.Vector;

public class BianaSession {

    private String sessionID;
    private HashSet<String> userEntitySets;  //Available user entity sets
    private BianaDatabase bianaDatabase;
    private String unificationProtocol;
    private SessionTree sessionTree;
    private Hashtable<String, Integer> network_level = null;
    private Hashtable<String, JTree> ontologies = null;
    private HashSet<String> selected_uEs = null;
    private HashSet<String> hasNetwork = null;

    public BianaSession(BianaProcessController controller, String pSessionID, BianaDatabase pBianaDatabase, String pUnificationProtocol){
    	this.sessionID = pSessionID;
    	this.bianaDatabase = pBianaDatabase;
    	this.unificationProtocol = pUnificationProtocol;
    	this.userEntitySets = new HashSet<String>();
    	this.sessionTree = new SessionTree(this, controller);
    	this.network_level = new Hashtable<String, Integer>();
	this.ontologies = new Hashtable<String, JTree>();
	this.selected_uEs = new HashSet<String>();
	this.hasNetwork  = new HashSet<String>();
    }

    public String getID(){
	return this.sessionID;
    }
    
    public void addUserEntitySet(String pUES){
    	this.userEntitySets.add(pUES);
    	this.sessionTree.addUserEntitySet(pUES);
    	this.network_level.put(pUES, 0);

	/*this.sessionTree.addGroupNode(pUES, "test_group_name", "pathway", "0", null);
	this.sessionTree.addGroupNode(pUES, "test_group_name2", "cluster", "1", null);
	this.sessionTree.addGroupNode(pUES, "clusterGroup", "cluster", "2", null);
	this.sessionTree.addGroupNode(pUES, "subcluster", "cluster", "4", "2");*/
    }

    public void createGroup(String pUES, String pGroupName, String pGroupType, String pGroupID, String pParentGroupID, String pParentGroupName){
	this.sessionTree.addGroupNode(pUES, pGroupName, pGroupType, pGroupID, pParentGroupID, pParentGroupName);
    }
    
    public void removeUserEntitySet(String pUEs){
	System.err.println("Going to remove "+pUEs);
	if( this.userEntitySets.contains(pUEs) ){
	    this.userEntitySets.remove(pUEs);
	    this.sessionTree.removeUserEntitySet(pUEs);
	    this.hasNetwork.remove(pUEs);
	}
    }
    
    public void removeSelectedUserEntitySets(){
	String pUEs;
    	for( Iterator it = this.getSelectedUserEntitySetsIterator(); it.hasNext(); ){
	    //this.removeUserEntitySet((String)it.next());
	    pUEs = (String)it.next();
	    this.userEntitySets.remove(pUEs);
	    this.hasNetwork.remove(pUEs);
    	}
    	this.sessionTree.removeSelectedUserEntitySets(); //It is already removed in removeUserEntitySet
    }
    
    public String getUnificationProtocol(){
    	return this.unificationProtocol;
    }
    
    public BianaDatabase getBianaDatabase(){
    	return this.bianaDatabase;
    }
    
    public Iterator getUserEntitySetsIterator(){
    	return this.userEntitySets.iterator();
    }

    public int getNumUserEntitySets(){
	return this.userEntitySets.size();
    }

    public HashSet<String> getUserEntitySets(){
	return this.userEntitySets;
    }

    public boolean isUserEntitySetSelected(String pName){
	return this.selected_uEs.contains(pName);
    }

    public boolean hasNetwork(String pSetName){
	System.err.println(pSetName+" has network: "+this.hasNetwork.contains(pSetName));
	return this.hasNetwork.contains(pSetName);
	
    }

    public void setHasNetwork( String pSetName ){
	this.hasNetwork.add(pSetName);
	System.err.println(pSetName+" has network: "+this.hasNetwork.contains(pSetName));
    }

    /*public void selectUserEntitySet(Collection pNameSet){
	for( Iterator it = pNameSet.iterator(); it.hasNext(); ){
	    this.selectUserEntitySet((String)it.next());
	}
	}*/

    public void selectUserEntitySet(String pName){
	if( this.selected_uEs.contains(pName) ){
	    return;
	}
	this.selected_uEs.add(pName);
    	this.sessionTree.selectUserEntitySet(pName);
    }

    public void unselectUserEntitySet(String pName){
	if( this.selected_uEs.contains(pName) ){
	    System.err.println("Unselecting user entity "+pName+" set from session");
	    this.sessionTree.unselectUserEntitySet(pName);
	    this.selected_uEs.remove(pName);
	    System.err.println("Unselected correctly");
	}
    }

    public void selectAllUserEntitySet(){
	for( Iterator it = this.userEntitySets.iterator(); it.hasNext(); ){
	    String s = (String)it.next();
	    System.err.println("Selecting "+s);
	    this.selectUserEntitySet(s);
    	}
    }

    public void unselectAllUserEntitySet(){
	System.err.println("Unselecting all user entity sets from session");
	for( Iterator it = this.getSelectedUserEntitySetsIterator(); it.hasNext(); ){
	    this.unselectUserEntitySet((String)it.next());
    	}
	this.selected_uEs.clear();
    }

    public HashSet<String> getSelectedUserEntitySets(){
    	return this.sessionTree.getSelectedUserEntitySets();
    }
    
    public Iterator getSelectedUserEntitySetsIterator(){
    	return this.sessionTree.getSelectedUserEntitySets().iterator();
    }
    
    public String getSelectedUserEntitySet(){
    	if( this.userEntitySets.size() == 1 ){
    	        return (String)this.userEntitySets.iterator().next();
        }
    	if( this.getSelectedUserEntitySets().size() > 1 ){
    		System.err.println("There are more than one user entity set selected");
    	}
    	if( this.getSelectedUserEntitySets().size() == 1 ){
    		return (String)this.getSelectedUserEntitySetsIterator().next();
    	}
    	return null;
    }

    public void selectUserEntitySet(Collection c){
    	for( Iterator it = c.iterator(); it.hasNext(); ){
	    this.sessionTree.selectUserEntitySet((String)it.next());
	    this.selectUserEntitySet((String)it.next());
    	}
    }

    public String toString(){
    	return "BIANA Work session. Connected to "+this.bianaDatabase+" using unification protocol "+this.unificationProtocol;
    }

    public SessionTree getSessionTree() {
	return sessionTree;
    }
    
    public void setNerworkDepth(String pUserEntitySetID, int level){
	/*if( this.network_level.get(pUserEntitySetID) == -1 ){
	  this.sessionTree.new_network(pUserEntitySetID, level);
	  }
	  else{*/
	if( this.network_level.get(pUserEntitySetID) < level ){
	    this.sessionTree.update_network_depth(pUserEntitySetID, level);
	}
	//}
    }

    public void addTag( String pSetName, String pTagName, String tagType){
	this.sessionTree.addTagNode(pSetName,pTagName, tagType);
    }

    public Vector<String> getTags( String pSetName, String tagType){
	return this.sessionTree.getTags(pSetName, tagType);
    }

    public void addOntology( String ontologyName, JTree ontologyTree ){
	System.err.println("Trying to add new ontology "+ontologyName);
	this.ontologies.put(ontologyName, ontologyTree);
    }

    public JTree getOntology( String ontologyName ){
	return this.ontologies.get(ontologyName);
    }
}
