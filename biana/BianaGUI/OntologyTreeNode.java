import javax.swing.tree.DefaultMutableTreeNode;


public class OntologyTreeNode extends DefaultMutableTreeNode{

    private String ontologyNodeID;

    public OntologyTreeNode(String showNodeID, String pOntologyNodeID){
	super(showNodeID);
	this.ontologyNodeID=pOntologyNodeID;
    }

    public void setOntologyNodeID(String id){
	this.ontologyNodeID=id;
    }

    public String getOntologyNodeID(){
	return ontologyNodeID;
    }

}