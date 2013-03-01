import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import java.awt.Component;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;

import java.awt.Font;

import java.util.Hashtable;
import java.util.HashSet;
import javax.swing.BoxLayout;

import java.util.Collection;
import java.util.Vector;

public class SessionTree extends JPanel implements TreeSelectionListener{
	
    private static int SESSION_NODE_TYPE = 0;
    private static int USER_ENTITY_SET_NODE_TYPE = 1;
    private static int NETWORK_NODE_TYPE = 2;
    private static int TAG_TYPE = 3;
    private static int TAG_NODE_TYPE = 4;
    private static int TAG_EDGE_TYPE = 5;
    private static int GROUP_NODE_TYPE = 6;
    private static int GROUP_TYPE_NODE_TYPE = 7;
    private static int SUB_USER_ENTITY_SET_NODE_TYPE = 8;
    private static int SUB_USER_ENTITY_RELATION_SET_NODE_TYPE = 9;

    private static int SUB_USER_ENTITY_SET_NODE_LEVEL_TYPE = 10;
    private static int SUB_USER_ENTITY_SET_NODE_TAG_TYPE = 11;
    private static int SUB_USER_ENTITY_SET_NODE_GROUP_TYPE = 12;
    
    private static ImageIcon USER_ENTITY_SET_ICON = null;
    private static ImageIcon SUB_USER_ENTITY_SET_ICON = null;
    private static ImageIcon NETWORK_ICON = null;
    private static ImageIcon NODE_TAG_ICON = null;
    private static ImageIcon EDGE_TAG_ICON = null;
    private static ImageIcon GROUP_ICON = null;
    private static ImageIcon LEVEL1_ICON = null;
    private static ImageIcon LEVEL2_ICON = null;
    private static ImageIcon LEVEL3_ICON = null;

    private static int NETWORK_NODE_INDEX = 0;
    private static int TAG_NODE_INDEX = 1;
    private static int GROUP_NODE_INDEX = 2;

    private static String SESSION_NODE_TOOLTIP = " <br>(Right-click for possible operations):<br>" + 
                                                 "<ul><li>Create a new user entity set from biomolecules of interest</li>" + 
                                                 "<li>Save this session for later use</li>" + 
                                                 "<li>Save commands used in this session</li>" + 
						 "<li>Reconnect to database if database server has gone away</li>" + 
                                                 "<li>Close the session</li></ul>";

    private static String USER_ENTITY_SET_NODE_TOOLTIP = " (Right-click for possible operations): <br>" + 
                                                 "<ul><li>Create/Expand network for this set of biomolecules</li>" + 
                                                 "<li>Delete this user entity set</li>" + 
                                                 "<li>Duplicate this user entiy set </li>" +
                                                 "<li>View/Export details of biomolecules and their connections in this set</li>" +
                                                 "<li>Randomize the network of this set</li>" + 
                                                 "<li>Select/unselect biomolecules/relations of the set</li>" +
						 "<li>View details of this user entity set</li></ul>";

    private static String NETWORK_NODE_TOOLTIP = " (Right-click for possible operations):<br>" + 
                                                 "<ul><li>Create/Expand network from biomolecules of interest</li>" + 
                                                 "<li>View/Export details of biomolecules and their connections</li>" + 
                                                 "<li>Randomize the network of this user entity set</li></ul>";

    private static String TAG_NODE_TOOLTIP = ": Click on the tag to select nodes with that tag";  
    private static String TAG_EDGE_TOOLTIP = ": Click on the tag to select edges with that tag";  
    private static String LEVEL_NODE_TOOLTIP = ": <br> Level i contains all the nodes that are included in the i^th expansion (nodes having a distance of i from the initial set of nodes).";  
    //private static String LEVEL_RELATION_NODE_TOOLTIP = ": <br> Level i contains all the nodes that are included in the ith expansion (nodes having a distance of i from the initial set of nodes).";
    private static String GROUP_NODE_TOOLTIP = ": Entities represented as groups";

    private Hashtable<String,Hashtable<String,DefaultMutableTreeNode>> userEntitySetAvailableGroupTypes = null;  //key: user entity set name. Value: <Group type, Node Object>
    private Hashtable<String,Hashtable<String,DefaultMutableTreeNode>> userEntityGroupNodes = null;

    private DefaultMutableTreeNode ROOT_NODE = null;

    private boolean block_tree_selection_changes = false;

    private TreePath[] selected_tree_paths = null;
    private boolean isTreeEnabled = true;
    
    BianaProcessController controller = null;

    public void valueChanged(TreeSelectionEvent e) {
	/*
	if( block_tree_selection_changes == false ){
	    System.err.println("Tree event. User entity Selected: "+Utilities.join(this.getSelectedUserEntitySets(),","));
	    this.controller.selectUserEntitySet((Collection)this.getSelectedUserEntitySets());
	    
	    if( this.getSelectedSubUserEntitySets().size()>0 ){
		System.err.println("Tree event. Sub User entity Selected: "+Utilities.join(this.getSelectedSubUserEntitySets(),","));
		this.controller.unselect_user_entities(this.getSelectedUserEntitySet());
		this.controller.select_user_entity(this.getSelectedUserEntitySet(), this.getSelectedSubUserEntitySets(), true);
	    }
	    }*/
    }

    private static final long serialVersionUID = 1L;
    private JTree tree;
    private TreeSelectionModel treeSelectionModel;
    
    private Hashtable<String,Integer> userEntitySetNodeIndices = null;  //  @jve:decl-index=0:
    private Hashtable<String, DefaultMutableTreeNode> userEntitySetNodes = null;

    private Hashtable<String, String> groupNameToID = null;
	
    private Hashtable<DefaultMutableTreeNode, Integer> nodeType = null;  //  @jve:decl-index=0:
    private Hashtable<DefaultMutableTreeNode, Integer> subUserEntitySetType = null;  //  @jve:decl-index=0:
    private Hashtable<DefaultMutableTreeNode, String> nodeDescription = null;
    private Hashtable<DefaultMutableTreeNode, DefaultMutableTreeNode> nodeUserEntitySetParentNode = null;
    
    private Hashtable<String,TreePath> userEntitySetPath = null;
	
    private JPopupMenu popUpMenu = null;

    private DefaultMutableTreeNode top = null;
    private DefaultMutableTreeNode currentSelecionParent = null;

    private BianaSession session = null;
	
    /**
     * This is the default constructor
     */
    public SessionTree(BianaSession pSession, BianaProcessController pController){
	super(); 
	this.session = pSession;
	this.controller = pController;
	initialize();
	initialize_icons();
    }
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.setSize(300, 110);
	
        //Create the nodes.
        top = new DefaultMutableTreeNode("BIANA SESSION");

	this.ROOT_NODE = top;
        
		//Create a tree that allows one selection at a time.
        this.tree = new JTree(top);
	this.treeSelectionModel = this.tree.getSelectionModel();
        
        ToolTipManager.sharedInstance().registerComponent(tree);
        
        tree.getSelectionModel().setSelectionMode
	   (TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	//tree.setSelectionModel(null);
        
        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);
        
        tree.addMouseListener(new MyMouseListener());
        
        //Create the root nodes        
        userEntitySetNodeIndices = new Hashtable<String,Integer>();
        userEntitySetNodes = new Hashtable<String, DefaultMutableTreeNode>();

	this.groupNameToID = new Hashtable<String, String>();
        
        //this.sessionNodesPath = new Hashtable<String,TreePath>();
        //this.sessionNodes = new Hashtable<String,DefaultMutableTreeNode>();
        this.nodeType = new Hashtable<DefaultMutableTreeNode,Integer>();
	this.subUserEntitySetType = new Hashtable<DefaultMutableTreeNode,Integer>();
        this.nodeDescription = new Hashtable<DefaultMutableTreeNode,String>();
        this.userEntitySetPath = new Hashtable<String,TreePath>();
	this.nodeUserEntitySetParentNode = new Hashtable<DefaultMutableTreeNode,DefaultMutableTreeNode>();
	this.userEntityGroupNodes = new Hashtable<String,Hashtable<String, DefaultMutableTreeNode>>();
	this.userEntitySetAvailableGroupTypes = new Hashtable<String, Hashtable<String, DefaultMutableTreeNode>>();
        
        this.nodeType.put(this.top,SessionTree.SESSION_NODE_TYPE);
        
        this.add(new JScrollPane((tree)));
        
        tree.setCellRenderer(new BianaTreeRenderer());
    }


    public void initialize_icons(){
	
	this.NETWORK_ICON = new ImageIcon(this.getClass().getResource("./img/network.gif"));
	this.USER_ENTITY_SET_ICON = new ImageIcon(this.getClass().getResource("./img/set.gif"));
	this.SUB_USER_ENTITY_SET_ICON = new ImageIcon(this.getClass().getResource("./img/set.gif"));
	this.NODE_TAG_ICON = new ImageIcon(this.getClass().getResource("./img/blue.gif"));
	this.EDGE_TAG_ICON = new ImageIcon(this.getClass().getResource("./img/red.gif"));
	this.GROUP_ICON = new ImageIcon(this.getClass().getResource("./img/network.gif"));
	this.LEVEL1_ICON = new ImageIcon(this.getClass().getResource("./img/set1.gif"));
	this.LEVEL2_ICON = new ImageIcon(this.getClass().getResource("./img/set2.gif"));
	this.LEVEL3_ICON = new ImageIcon(this.getClass().getResource("./img/set3.gif"));

    }

    public void disable(){
	//this.tree.setEditable(false);
	if( this.isTreeEnabled == false ){
	    return;
	}
	System.err.println("Disabling tree");
	this.selected_tree_paths = this.tree.getSelectionPaths();
	this.tree.setSelectionModel(null);
	this.isTreeEnabled = false;
	this.tree.removeTreeSelectionListener(this);
    }

    public void enable(){
	//this.tree.setEditable(true);
	System.err.println("Enabling tree");
	this.tree.setSelectionModel(this.treeSelectionModel);
	this.tree.setSelectionPaths(this.selected_tree_paths);
	this.isTreeEnabled = true;
	this.tree.addTreeSelectionListener(this);
    }

	
    public void removeSelectedUserEntitySets(){
	if( true ){ //For testing purposes
	    try{
		/*TreePath[] selected = this.tree.getSelectionPaths();
		System.err.println("Tree nodes to delete: "+selected.toString());
		for( int i=0; i<selected.length; i++ ){
		    ((DefaultTreeModel)this.tree.getModel()).removeNodeFromParent((DefaultMutableTreeNode)selected[i].getLastPathComponent());	
		}*/

		//New way to avoid exceptions...
		
		//TreePath[] selected = this.tree.getSelectionPaths();
		TreePath[] selected = this.getSelectionPaths();
		System.err.println("Tree nodes to delete: "+selected.toString());
		for( int i=0; i<selected.length; i++ ){
		    ((DefaultMutableTreeNode)selected[i].getLastPathComponent()).removeFromParent();
		}
		((DefaultTreeModel)this.tree.getModel()).nodeStructureChanged(this.ROOT_NODE);
		
	    }
	    catch( Exception e ){
		System.err.println("Error while trying to remove selected user entity sets...\n");
	    }
	}
	//AQUI TENGO QUE ELIMINAR DEL REGISTRO DE SETS DISPONIBLES...
    }
	
    public void removeUserEntitySet(String pName){
	
	if( this.userEntitySetNodeIndices.containsKey(pName) ){
	    //System.err.println("Clear from removeUserEntitySet");
	    this.tree.clearSelection();
	    this.selectUserEntitySet(pName);
	    this.removeSelectedUserEntitySets();
	    this.userEntitySetNodeIndices.remove(pName);	
	}
	else{
	    System.err.println("Trying to remove an unexisting user entity node...: "+pName);
	}
    }
	
    public void addUserEntitySet(String pSetName){
	if( this.userEntitySetNodeIndices.containsKey(pSetName)){
	    System.err.println("Trying to add an existing user entity set: "+pSetName);
	}
	else{
	    // Insert Set Node
	    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(pSetName);
	    this.nodeType.put(newNode, SessionTree.USER_ENTITY_SET_NODE_TYPE);
	    ((DefaultTreeModel)this.tree.getModel()).insertNodeInto(newNode, this.top, this.top.getChildCount());
	    this.userEntitySetNodes.put(pSetName, newNode);
	    this.userEntitySetNodeIndices.put(pSetName,this.top.getChildCount()+2);
	    this.userEntitySetPath.put(pSetName,new TreePath(newNode.getPath()));
	    this.userEntitySetAvailableGroupTypes.put(pSetName,new Hashtable<String,DefaultMutableTreeNode>());
	    this.userEntityGroupNodes.put(pSetName,new Hashtable<String,DefaultMutableTreeNode>());

	    // Insert Network Node
	    DefaultMutableTreeNode networkNode = new DefaultMutableTreeNode("Network");
	    this.nodeType.put(networkNode, SessionTree.NETWORK_NODE_TYPE);
	    ((DefaultTreeModel)this.tree.getModel()).insertNodeInto(networkNode, newNode, SessionTree.NETWORK_NODE_INDEX);
	    this.nodeUserEntitySetParentNode.put(networkNode,newNode);

	    // Insert Level 0 Node
	    DefaultMutableTreeNode levelNode = new DefaultMutableTreeNode("Level 0");
	    this.nodeType.put(levelNode, SessionTree.SUB_USER_ENTITY_SET_NODE_TYPE);
	    this.subUserEntitySetType.put(levelNode, SessionTree.SUB_USER_ENTITY_SET_NODE_LEVEL_TYPE);
	    ((DefaultTreeModel)this.tree.getModel()).insertNodeInto(levelNode, networkNode, 0);
	    this.nodeUserEntitySetParentNode.put(levelNode,newNode);
	    
	    // Insert Tags Node
	    DefaultMutableTreeNode tagNode = new DefaultMutableTreeNode("Tags");
	    this.nodeType.put(tagNode, SessionTree.TAG_NODE_TYPE);
	    ((DefaultTreeModel)this.tree.getModel()).insertNodeInto(tagNode, newNode, SessionTree.TAG_NODE_INDEX);
	    this.nodeUserEntitySetParentNode.put(tagNode,newNode);

	    // Insert Node Tags Node
	    DefaultMutableTreeNode nodeTagNode = new DefaultMutableTreeNode("Node Tags");
	    this.nodeType.put(nodeTagNode, SessionTree.TAG_NODE_TYPE);
	    ((DefaultTreeModel)this.tree.getModel()).insertNodeInto(nodeTagNode, tagNode, 0);
	    this.nodeUserEntitySetParentNode.put(nodeTagNode,newNode);

	    // Insert Edge Tags Node
	    DefaultMutableTreeNode edgeTagNode = new DefaultMutableTreeNode("Edge Tags");
	    this.nodeType.put(edgeTagNode, SessionTree.TAG_EDGE_TYPE);
	    ((DefaultTreeModel)this.tree.getModel()).insertNodeInto(edgeTagNode, tagNode, 1);
	    this.nodeUserEntitySetParentNode.put(edgeTagNode,newNode);

	    // Insert Groups Node
	    DefaultMutableTreeNode groupsNode = new DefaultMutableTreeNode("Groups");
	    this.nodeType.put(groupsNode, SessionTree.GROUP_NODE_TYPE);
	    ((DefaultTreeModel)this.tree.getModel()).insertNodeInto(groupsNode, newNode, SessionTree.GROUP_NODE_INDEX);
	    this.nodeUserEntitySetParentNode.put(groupsNode,newNode);

	    DefaultMutableTreeNode byTypeGroupsNode = new DefaultMutableTreeNode("By group type");
	    this.nodeType.put(byTypeGroupsNode, SessionTree.GROUP_NODE_TYPE);
	    ((DefaultTreeModel)this.tree.getModel()).insertNodeInto(byTypeGroupsNode, groupsNode, 0);
	    this.nodeUserEntitySetParentNode.put(byTypeGroupsNode,newNode);
	    
	    DefaultMutableTreeNode hierarchyGroupsNode = new DefaultMutableTreeNode("Groups hierarchy");
	    this.nodeType.put(hierarchyGroupsNode, SessionTree.GROUP_NODE_TYPE);
	    ((DefaultTreeModel)this.tree.getModel()).insertNodeInto(hierarchyGroupsNode, groupsNode, 1);
	    this.nodeUserEntitySetParentNode.put(hierarchyGroupsNode,newNode);
	}
    }

    /*
      groupType is something like "pathway", "cluster",...
      groupID is the internal group ID identifier
      parentGroupID is for if the group is subgroup of another
     */
    public void addGroupNode(String pSetName, String groupName, String groupType, String groupID, String parentGroupID, String parentGroupName){

	this.groupNameToID.put(groupName,groupID);

	System.err.println("Adding group node "+groupID+" with parent "+parentGroupID);
	/*if( this.userEntityGroupNodes.get(pSetName).containsKey(groupID) ){
	    System.err.println("Trying to insert a group with the same name twice...("+groupID+")");
	    return;
	    }*/
	

	DefaultMutableTreeNode userEntitySetNode = this.userEntitySetNodes.get(pSetName);
	DefaultMutableTreeNode parentNode = null;
	
	if( this.userEntitySetAvailableGroupTypes.get(pSetName).containsKey(groupType)==false ){
	    //Hashtable<String, DefaultMutableTreeNode> t = new Hashtable<String,DefaultMutableTreeNode>();
	    DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(groupType);
	    //t.put(groupType,newTreeNode);
	    //this.userEntitySetAvailableGroupTypes.put(pSetName,t);
	    this.userEntitySetAvailableGroupTypes.get(pSetName).put(groupType,newTreeNode);
	    parentNode = (DefaultMutableTreeNode)(userEntitySetNode.getChildAt(SessionTree.GROUP_NODE_INDEX).getChildAt(0));
	    this.nodeType.put(newTreeNode, SessionTree.GROUP_TYPE_NODE_TYPE);
	    this.nodeUserEntitySetParentNode.put(newTreeNode,userEntitySetNode);
	    ((DefaultTreeModel)this.tree.getModel()).insertNodeInto(newTreeNode,parentNode,parentNode.getChildCount());
	}
	//Add a new group node classified by type
	DefaultMutableTreeNode newGroupNode = new DefaultMutableTreeNode(groupName);
	this.nodeUserEntitySetParentNode.put(newGroupNode,userEntitySetNode);
	this.nodeType.put(newGroupNode, SessionTree.SUB_USER_ENTITY_SET_NODE_GROUP_TYPE);
	this.subUserEntitySetType.put(newGroupNode, SessionTree.SUB_USER_ENTITY_SET_NODE_GROUP_TYPE);
	parentNode = this.userEntitySetAvailableGroupTypes.get(pSetName).get(groupType);
	((DefaultTreeModel)this.tree.getModel()).insertNodeInto(newGroupNode,parentNode,parentNode.getChildCount());

	//Add a new group node in hierarchy
	if( this.userEntityGroupNodes.get(pSetName).containsKey(groupID) ){
	    //Node previously exited in tree. Don't add a new node, but move it into the correct place
	    newGroupNode = this.userEntityGroupNodes.get(pSetName).get(groupID);
	    //System.err.println("Group node "+groupID+" previously existed.");
	    //System.err.println("Going to remove node "+newGroupNode+" from tree");
	    ((DefaultTreeModel)this.tree.getModel()).removeNodeFromParent(newGroupNode);
	    
	    //Temp
	    //newGroupNode = new DefaultMutableTreeNode(groupName);
	    //this.nodeUserEntitySetParentNode.put(newGroupNode,userEntitySetNode);
	    //this.nodeType.put(newGroupNode, SessionTree.GROUP_NODE_TYPE);
	}
	else{
	    newGroupNode = new DefaultMutableTreeNode(groupName);
	    this.nodeUserEntitySetParentNode.put(newGroupNode,userEntitySetNode);
	    this.nodeType.put(newGroupNode, SessionTree.SUB_USER_ENTITY_SET_NODE_GROUP_TYPE);
	    this.subUserEntitySetType.put(newGroupNode, SessionTree.SUB_USER_ENTITY_SET_NODE_GROUP_TYPE);
	}
	
	if( parentGroupID == null ){
	    //Parent is null. So, add it to the roots of hierarchies
	    //System.err.println("Adding as a root as it has no parent");
	    parentNode = (DefaultMutableTreeNode)(userEntitySetNode.getChildAt(SessionTree.GROUP_NODE_INDEX).getChildAt(1));
	}
	else{
	    //Has parent. Search it.
	    parentNode = this.userEntityGroupNodes.get(pSetName).get(parentGroupID);
	    if( parentNode == null ){
		//System.err.println("It should have parent but it has not. Going to create it");
		//Parent node does exist in tree. Add it in the roots of hierarchies
		DefaultMutableTreeNode newParentNode = new DefaultMutableTreeNode(parentGroupName);
		this.nodeUserEntitySetParentNode.put(newParentNode,userEntitySetNode);
		this.nodeType.put(newParentNode, SessionTree.SUB_USER_ENTITY_SET_NODE_GROUP_TYPE);
		this.subUserEntitySetType.put(newParentNode, SessionTree.SUB_USER_ENTITY_SET_NODE_GROUP_TYPE);
		parentNode = (DefaultMutableTreeNode)(userEntitySetNode.getChildAt(SessionTree.GROUP_NODE_INDEX).getChildAt(1));
		this.userEntityGroupNodes.get(pSetName).put(parentGroupID,newParentNode);
		((DefaultTreeModel)this.tree.getModel()).insertNodeInto(newParentNode,parentNode,parentNode.getChildCount());
		parentNode = newParentNode;
	    }
	}
	
	((DefaultTreeModel)this.tree.getModel()).insertNodeInto(newGroupNode,parentNode,parentNode.getChildCount());
	this.userEntityGroupNodes.get(pSetName).put(groupID,newGroupNode);
    }
    

    /**
       tagType specifies if it is a node tag or an edge tag
    */
    public void addTagNode(String pSetName, String tagName, String tagType){
	DefaultMutableTreeNode newTagNode = new DefaultMutableTreeNode(tagName);	
	DefaultMutableTreeNode userEntitySetNode = this.userEntitySetNodes.get(pSetName);
	this.nodeUserEntitySetParentNode.put(newTagNode,userEntitySetNode);
	if( tagType.equals("nodeTag") ){
	    this.nodeType.put(newTagNode, SessionTree.SUB_USER_ENTITY_SET_NODE_TYPE);
	    this.subUserEntitySetType.put(newTagNode,SUB_USER_ENTITY_SET_NODE_TAG_TYPE);
	    DefaultMutableTreeNode parentTagNode = (DefaultMutableTreeNode)(userEntitySetNode.getChildAt(SessionTree.TAG_NODE_INDEX).getChildAt(0));
	    ((DefaultTreeModel)this.tree.getModel()).insertNodeInto(newTagNode,parentTagNode,parentTagNode.getChildCount());
	}
	else{
	    if( tagType.equals("edgeTag") ){
		this.nodeType.put(newTagNode, SessionTree.SUB_USER_ENTITY_RELATION_SET_NODE_TYPE);
		DefaultMutableTreeNode parentTagNode = (DefaultMutableTreeNode)(userEntitySetNode.getChildAt(SessionTree.TAG_NODE_INDEX).getChildAt(1));
		((DefaultTreeModel)this.tree.getModel()).insertNodeInto(newTagNode,parentTagNode,parentTagNode.getChildCount());
	    }
	    else{
		System.err.println(tagType+" not recognized");
	    }
	}
    }

    /**
       tagType specifies if it is a node tag or an edge tag
    */
    public Vector<String> getTags(String pSetName, String tagType){
	DefaultMutableTreeNode userEntitySetNode = this.userEntitySetNodes.get(pSetName);
	Vector<String> tags = new Vector<String>();
	if( tagType.equals("nodeTag") ){
	    DefaultMutableTreeNode parentTagNode = (DefaultMutableTreeNode)(userEntitySetNode.getChildAt(SessionTree.TAG_NODE_INDEX).getChildAt(0));
	    for(int i = 0; i < parentTagNode.getChildCount(); i++)
		tags.add(((DefaultTreeModel)this.tree.getModel()).getChild(parentTagNode,i).toString());
	}
	else{
	    if( tagType.equals("edgeTag") ){
		DefaultMutableTreeNode parentTagNode = (DefaultMutableTreeNode)(userEntitySetNode.getChildAt(SessionTree.TAG_NODE_INDEX).getChildAt(1));
		for(int i = 0; i < parentTagNode.getChildCount(); i++)
		    tags.add(((DefaultTreeModel)this.tree.getModel()).getChild(parentTagNode,i).toString());
	    }
	    else{
		System.err.println(tagType+" not recognized");
	    }
	}
	return tags;
    }


   
    public void update_network_depth(String pSetName, int levels){
	//System.err.println("Going to change level from session tree... "+levels);
	DefaultMutableTreeNode userEntitySetNode = this.userEntitySetNodes.get(pSetName);
    	DefaultMutableTreeNode networkNode = (DefaultMutableTreeNode)userEntitySetNode.getChildAt(SessionTree.NETWORK_NODE_INDEX);
    	int current_levels = networkNode.getChildCount();
    	for( int i=current_levels; i<levels+1; i++ ){
	    DefaultMutableTreeNode levelNode = new DefaultMutableTreeNode("Level "+i);
	    this.nodeType.put(levelNode, SessionTree.SUB_USER_ENTITY_SET_NODE_TYPE);
	    this.subUserEntitySetType.put(levelNode, SessionTree.SUB_USER_ENTITY_SET_NODE_LEVEL_TYPE);
	    ((DefaultTreeModel)this.tree.getModel()).insertNodeInto(levelNode, networkNode, i);
	    this.nodeUserEntitySetParentNode.put(levelNode,userEntitySetNode);
    	}
    }


    private int get_selected_sub_user_entity_set_type(){
	TreePath[] selecteds = this.tree.getSelectionPaths();
	return this.subUserEntitySetType.get((DefaultMutableTreeNode)selecteds[0].getLastPathComponent());
    }


    private void processTreeSelection(){
	
	//this.controller.selectUserEntitySet((Collection)this.getSelectedUserEntitySets());
	    
	if( this.getSelectedSubUserEntitySets().size()>0 ){
	    //System.err.println("Tree event. Sub User entity Selected: "+Utilities.join(this.getSelectedSubUserEntitySets(),","));
	    this.controller.unselect_user_entities(this.getSelectedUserEntitySet());
	    int selected_type = this.get_selected_sub_user_entity_set_type();
	    if( selected_type == SessionTree.SUB_USER_ENTITY_SET_NODE_LEVEL_TYPE){
		this.controller.select_user_entity(this.getSelectedUserEntitySet(), this.getSelectedSubUserEntitySets(), true, "level");
	    }
	    else if( selected_type == SessionTree.SUB_USER_ENTITY_SET_NODE_TAG_TYPE){
		this.controller.select_user_entity(this.getSelectedUserEntitySet(), this.getSelectedSubUserEntitySets(), true, "tag");
	    }
	    else if( selected_type == SessionTree.SUB_USER_ENTITY_SET_NODE_GROUP_TYPE){
		this.controller.select_user_entity(this.getSelectedUserEntitySet(), this.getSelectedSubUserEntitySets(), true, "group");
	    }
	}
	
	if( this.getSelectedSubUserEntityRelationSets().size()>0 ){
	    this.controller.select_user_entity_relations(this.getSelectedUserEntitySet(), this.getSelectedSubUserEntityRelationSets(), true);
	}

	this.controller.selectUserEntitySet((Collection)this.getSelectedUserEntitySets());
    }


    public HashSet<String> getSelectedGroups(){
	int selected_type = this.get_selected_sub_user_entity_set_type();
	System.err.println(selected_type);
	if( selected_type == SessionTree.SUB_USER_ENTITY_SET_NODE_GROUP_TYPE ){
	    return this.getSelectedSubUserEntitySets();
	}
	return null;
    }
	
    class MyMouseListener implements MouseListener
    {
	//	display popup menu
	public void mouseReleased(MouseEvent evt){

	    try{
	    
	    //	using the x,y co-ords is a bit buggy and
	    //	throws an exception when the circle
	    //	next to tree node is clicked
	    int xCoord = evt.getX();
	    int yCoord = evt.getY();
	    
	    TreePath sPath = tree.getPathForLocation(xCoord,yCoord);
	    if( sPath != null ){
		if( ((DefaultMutableTreeNode)sPath.getLastPathComponent()).getParent()!=currentSelecionParent){
		    currentSelecionParent = (DefaultMutableTreeNode)((DefaultMutableTreeNode)sPath.getLastPathComponent()).getParent();
		    //System.err.println("Clear tree selection by mouse action");
		    //block_tree_selection_changes = true;
		    tree.clearSelection();
		    tree.addSelectionPath(sPath);
		    processTreeSelection();
		    tree.clearSelection();
		    tree.addSelectionPath(sPath);
		    //tree.addSelectionPath(sPath);
		    //block_tree_selection_changes = false;
		}
		else{
		    processTreeSelection();
		}
	    }
	    
	    if (evt.isPopupTrigger()){
		try{
		    this.getPopUpMenu(sPath).show(tree, xCoord, yCoord);
		}
		catch(Exception e){
		    System.err.println(e.toString());
		    //controller.getPopUpMenu("general").show(tree,xCoord,yCoord);
		}
	    }

	    }
	    catch(Exception e){
		System.err.println("Exception produced while mouse released...\n");
	    }
	}
	
	public void mousePressed(MouseEvent evt){

	    try{
	    
	    if (evt.isPopupTrigger()){
		
	    	int xCoord = evt.getX();
	    	int yCoord = evt.getY();
		
	    	try{
	    		TreePath sPath = tree.getPathForLocation(xCoord,yCoord);
	    		this.getPopUpMenu(sPath).show(tree, xCoord, yCoord);
	    	}
	    	catch( Exception e ) {
		    System.err.println(e.toString());
	    		//controller.getPopUpMenu("general").show(tree,xCoord,yCoord);
	    	}
	    }
	    }
	    catch( Exception e ){
		System.err.println("Exception produced while mouse pressed...\n");
	    }
	} 


	public void mouseEntered(MouseEvent evt){}
	public void mouseExited(MouseEvent evt){}
	public void mouseClicked(MouseEvent evt){}
	
	
	
	
	private JPopupMenu getPopUpMenu(TreePath sPath){
	    int type = nodeType.get((DefaultMutableTreeNode)sPath.getLastPathComponent());

	    //System.err.println("NODE TREE TYPE: "+type);
	    
	    if( type==SessionTree.USER_ENTITY_SET_NODE_TYPE){
	    	if( tree.getSelectionCount()==1 ){
		    return BIANA.getUserEntitySetPopupMenu(controller);
	    	}
	    	else{
		    return BIANA.getMultipleUserEntitySetPopupMenu(controller);
	    	}
	    }
	    else if(type==SessionTree.NETWORK_NODE_TYPE ){
	    	return BIANA.getNetworkMenu(controller);
	    }
	    else if(type==SessionTree.SESSION_NODE_TYPE) {
	    	return BIANA.getSessionMenu(controller);
	    }
	    else if(type==SessionTree.SUB_USER_ENTITY_SET_NODE_TYPE){
	    	return BIANA.getUserEntitySubSetPopupMenu(controller);
	    }
	    else if(type==SessionTree.SUB_USER_ENTITY_SET_NODE_GROUP_TYPE){
		return BIANA.getGroupPopupMenu(controller);
	    }
	    return null;
	}
    }
    
    class BianaTreeRenderer extends DefaultTreeCellRenderer {
	
	private static final long serialVersionUID = 1L;
	
	public Component getTreeCellRendererComponent(
						      JTree tree,
						      Object value,
						      boolean sel,
						      boolean expanded,
						      boolean leaf,
						      int row,
						      boolean hasFocus) {
	    
	    super.getTreeCellRendererComponent(
					       tree, value, sel,
					       expanded, leaf, row,
					       hasFocus);
	    
	    int type = nodeType.get((DefaultMutableTreeNode)value);
	    
	    if( type == SessionTree.SESSION_NODE_TYPE ){
	    	//setToolTipText(nodeDescription.get((DefaultMutableTreeNode)value));
	    	setToolTipText("<html>BIANA_SESSION. Database "+session.getBianaDatabase().toString()+", using unification protocol \""+session.getUnificationProtocol()+"\".<br />"+SESSION_NODE_TOOLTIP+"</html>");
	    	//setFont(new Font("Serif", Font.BOLD, 16));
	    }
	    else if( type == SessionTree.USER_ENTITY_SET_NODE_TYPE ){
	    	setIcon(SessionTree.USER_ENTITY_SET_ICON);
	    	setToolTipText("<html>User Entity Set "+value+ USER_ENTITY_SET_NODE_TOOLTIP+"</html>");
	    }
	    else if( type == SessionTree.NETWORK_NODE_TYPE ){
	    	setIcon(SessionTree.NETWORK_ICON);
	    	setToolTipText("<html>Network"+NETWORK_NODE_TOOLTIP+"</html>");
	    }
	    else if( type == SessionTree.TAG_NODE_TYPE ){
		setIcon(SessionTree.NODE_TAG_ICON);
		setToolTipText("<html>Node tags"+TAG_NODE_TOOLTIP+"</html>");
	    }
	    else if( type == SessionTree.TAG_EDGE_TYPE ){
		setIcon(SessionTree.EDGE_TAG_ICON);
		setToolTipText("<html>Edge tags"+TAG_EDGE_TOOLTIP+"</html>");
	    }  
	    else if( type == SessionTree.SUB_USER_ENTITY_SET_NODE_TYPE ){
		setIcon(SessionTree.SUB_USER_ENTITY_SET_ICON);
		setToolTipText("<html>User Entities by level"+LEVEL_NODE_TOOLTIP+"<html>");
	    }
	    else if( type == SessionTree.SUB_USER_ENTITY_RELATION_SET_NODE_TYPE ){
		setIcon( SessionTree.NETWORK_ICON);
		//setToolTipText("<html>User Entity Relations by level"+LEVEL_RELATION_NODE_TOOLTIP+"</html>");
	    }
	    else if( type == SessionTree.GROUP_NODE_TYPE){
		setToolTipText("<html>Groups"+GROUP_NODE_TOOLTIP+"</html>");
	    }
	    else{
		setToolTipText("<html>You can use right-click on a tree element to specify a desired action</html>");
	    }
	    //this.setFont(new Font("Serif", Font.BOLD, 18));
	    
	    return this;
	}
    }

    private TreePath[] getSelectionPaths(){
	TreePath[] selecteds = null;
	if( isTreeEnabled ){
	    selecteds = this.tree.getSelectionPaths();
	}
	else{
	    selecteds = this.selected_tree_paths;
	}

	return selecteds;
    }

    /**
     * Returns the selected user entity sets
     * All user entity sets can belong to a single session (use getSelectedSessions to obtain it)
     *
     */
    public HashSet<String> getSelectedUserEntitySets(){
	HashSet<String> selection = new HashSet<String>();
	TreePath[] selecteds = this.getSelectionPaths();

	if( selecteds != null ){
	    DefaultMutableTreeNode temp;
	    for( int i=0; i<selecteds.length; i++ ){
		//for( int i=0; i<this.tree.getSelectionCount(); i++ ){
		temp = (DefaultMutableTreeNode)selecteds[i].getLastPathComponent();
		if( nodeType.get(temp)==SessionTree.USER_ENTITY_SET_NODE_TYPE){
		    selection.add((String)(temp.getUserObject()));
		}
		else if( this.nodeUserEntitySetParentNode.containsKey(temp) ){
		    selection.add((String)(this.nodeUserEntitySetParentNode.get(temp).getUserObject()));
		}
	    }
	}
	return selection;
    }

    private String getSelectedUserEntitySet(){
	HashSet<String> selection = this.getSelectedUserEntitySets();
	if( selection.size()== 1 ){
	    return selection.iterator().next();
	}
	return null;
    }


    public HashSet<String> getSelectedSubUserEntitySets(){
	HashSet<String> selection = new HashSet<String>();
	TreePath[] selecteds = this.getSelectionPaths();

	DefaultMutableTreeNode temp;
	//for( int i=0; i<this.tree.getSelectionCount(); i++ ){
	for( int i=0; i<selecteds.length; i++ ){
	    temp = (DefaultMutableTreeNode)selecteds[i].getLastPathComponent();
	    if( nodeType.get(temp)==SessionTree.SUB_USER_ENTITY_SET_NODE_TYPE){
		selection.add((String)(temp.getUserObject()));
	    }
	    else if( nodeType.get(temp)==SessionTree.SUB_USER_ENTITY_SET_NODE_GROUP_TYPE ){
		selection.add(this.groupNameToID.get((String)(temp.getUserObject())));
	    }
	}
	return selection;
    }

    public HashSet<String> getSelectedSubUserEntityRelationSets(){
	HashSet<String> selection = new HashSet<String>();
	TreePath[] selecteds = this.getSelectionPaths();
	DefaultMutableTreeNode temp;
	//for( int i=0; i<this.tree.getSelectionCount(); i++ ){
	for( int i=0; i<selecteds.length; i++ ){
	    temp = (DefaultMutableTreeNode)selecteds[i].getLastPathComponent();
	    if( nodeType.get(temp)==SessionTree.SUB_USER_ENTITY_RELATION_SET_NODE_TYPE){
		selection.add((String)(temp.getUserObject()));
	    }
	}
	return selection;
    }
	
    public HashSet<String> getSelectedSessions(){
	HashSet<String> selection = new HashSet<String>();
	TreePath[] selecteds = this.tree.getSelectionPaths();
	DefaultMutableTreeNode temp;
	for( int i=0; i<this.tree.getSelectionCount(); i++ ){
	    temp = (DefaultMutableTreeNode)selecteds[i].getLastPathComponent();
	    if( nodeType.get(temp)==SessionTree.SESSION_NODE_TYPE ){
		selection.add((String)temp.getUserObject());
	    }
	    else if( nodeType.get(temp)==SessionTree.USER_ENTITY_SET_NODE_TYPE){
		selection.add((String)((DefaultMutableTreeNode)temp.getParent()).getUserObject());
	    }
	}
	return selection;
    }
	
    public void selectUserEntitySet(String pName){

	if( this.isTreeEnabled == false ){
	    this.selected_tree_paths = new TreePath[1];
	    this.selected_tree_paths[0] = this.userEntitySetPath.get(pName);
	}
	else{
	    System.err.println("Selecting Tree User Entity Set "+pName);
	    if( ((DefaultMutableTreeNode)this.userEntitySetPath.get(pName).getLastPathComponent()).getParent()!=currentSelecionParent){
		currentSelecionParent = (DefaultMutableTreeNode)((DefaultMutableTreeNode)this.userEntitySetPath.get(pName).getLastPathComponent()).getParent();
		System.err.println("Clear tree selection from selectUserEntitySet in SessionTree");
		tree.clearSelection();
	    }
	    this.tree.addSelectionPath(this.userEntitySetPath.get(pName));
	    System.err.println("Selected!");
	}
    }
    
    public void unselectUserEntitySet(String pName){
	//System.err.println("Unselecting Tree User entity Set "+pName);
	try{
	    this.tree.removeSelectionPath(this.userEntitySetPath.get(pName));
	}
	catch(Exception e){
	    System.err.println("You cannot unselect "+pName+" as it does not exist");
	}
    }
    
    public static void main(String[] args) {
    	//System.err.println("Showing");
    	JFrame a = new JFrame();
    	a.setSize(100,100);
    	SessionTree s = new SessionTree(null, null);
    	a.add(s);
    	a.setVisible(true);
    	s.addUserEntitySet("set1");
    	s.addUserEntitySet("set2");
    	s.addUserEntitySet("set3");
    	s.addUserEntitySet("set4");
    	s.addUserEntitySet("set5");
    	try{
    		Thread.sleep(4000);
    	}
    	catch(Exception e){
    		
    	}
    	s.addUserEntitySet("set6");
    	//a.add(s);
    	//a.setVisible(true);
    	System.err.println("Done");
    }
}
