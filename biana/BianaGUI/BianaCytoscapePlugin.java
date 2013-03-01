import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanelImp;
import javax.swing.SwingConstants;
import cytoscape.*;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.data.CyAttributes;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.CyEdge;
import cytoscape.CytoscapeInit;
import cytoscape.util.IndeterminateProgressBar;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;

import cytoscape.layout.algorithms.GridNodeLayout;

import java.awt.Frame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.awt.event.ActionEvent;

import giny.view.NodeView;
import giny.view.EdgeView;
import ding.view.NodeContextMenuListener;
import ding.view.EdgeContextMenuListener;

import java.awt.event.ActionListener;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;

import java.util.HashSet;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JTree;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.JButton;

import java.io.File;
//import java.util.Set;

import java.net.URI;
//import java.awt.Desktop;   //Removed because not supported in java 1.6!!! (and 1.6 is not available for MacOs...)

public class BianaCytoscapePlugin extends CytoscapePlugin implements BianaListener, ActionListener, EdgeContextMenuListener, NodeContextMenuListener { //, CyNetworkListener {
	
    PipedInputStream  in = null;
    PipedOutputStream out = null;
    
    PipedInputStream  cytIn = null;
    PipedOutputStream cytOut = null;
    
    CyNetwork current_network = null;
    
    BianaProcessController controller = null;
    
    JPanel bianaPane = null;
    
    boolean started = false;
    boolean vizmapLoaded = false;
    boolean disable_cytoscape_events_listener = false;
    int num_processing_messages = 0;

    IndeterminateProgressBar progressBar = null;

    JDialog presentation = null;

    /* Socket to communicate with process */
    ServerSocket pluginSocket = null;
    
    BianaCytoscapePlugin self = this;
    private JPanel jBianaPanel = null;  //  @jve:decl-index=0:visual-constraint="192,46"
    private JPanel jButtonsPanel = null;
    private JButton jOptionSessionButton = null;
    private JButton jConfigurationButton = null;
    private JButton jHelpButton = null;
    private JTabbedPane jSessionPanel = null;
    private JScrollPane jSessionTreePanel = null;

    private int BianaPanelIndex = -1;
    private int BianaShellIndex = -1;

    private Hashtable<String,String> networkTitleToId = null;

    private boolean selecting_process = false; //Variable to avoid java.util.ConcurrentModificationException, when selecting while the program is still unselecting
    private boolean selecting_user_entity = false;

    private JPopupMenu jAdministrationPopupMenu = null;
    private JPopupMenu jHelpPopupMenu = null;

    private HashSet<String> BIANA_added_attributes = null;

    //private static final String VIZMAP_FILE = CytoscapeInit.getConfigVersionDirectory().getAbsolutePath() + "/plugins/BIANA.props";// = "/home/emre/biana2.props"; //"biana/BianaGUI/BIANA.props";
    private static final String VIZMAP_FILE = null;
    private static final String VIZMAP_NAME = "BIANA";

    private socketThread cytoscapeSocketThread = null;

    //private boolean applyLayout = false;
    private enum Layout { NONE, GRID, CIRCULAR }
    private Layout applyLayout = Layout.NONE;

    /* Menus */
    private JMenu eEnodeMenu = null;
    private JMenu eERnodeMenu = null;

    public BianaCytoscapePlugin () {
	
    	BianaPluginAction action = new BianaPluginAction();
        action.setPreferredMenu("Plugins");
        Cytoscape.getDesktop().getCyMenus().addAction(action);

        //VIZMAP_FILE = CytoscapeInit.getConfigVersionDirectory().getAbsolutePath() + "/plugins/BIANA.props"; //getConfigDirectory()

        //! to get BIANA working at Cytoscape startup but makes socket crash
        //action.actionPerformed(new ActionEvent(this, 0, ""));
    }
    
    /**
     * Gives a description of this plugin.
     */
    public String describe() {
        StringBuffer sb = new StringBuffer();
        sb.append("Links BIANA with Cytoscape");
        return sb.toString();
    }

    /* Start a process message dialog */
    public void initiateProcessingMessage(Frame owner, String message) {
        System.err.println("Intitiate Processing!");
        if(this.progressBar == null) {
                //this.progressBar = new IndeterminateProgressBar(Cytoscape.getDesktop(), "BIANA in Progress", message);
                this.progressBar = new IndeterminateProgressBar(owner, "BIANA in Progress", message);
        } else { 
                this.progressBar.setLabelText(message);
        }
        this.progressBar.setVisible(true);
        //this.progressBar.show();
        return;
    }

    public void endProcessingMessage() {
        if(this.progressBar != null) {
                this.progressBar.dispose(); //setVisible(false);
                this.progressBar = null;
                //this.progressBar.hide();
        } 
        return;
    }
    
    class BianaPluginAction extends CytoscapeAction{

	private static final long serialVersionUID = 1L;

	/**
         * The constructor sets the text that should appear on the menu item.
         */
        public BianaPluginAction() {
	    super("BIANA");
        }
        
        public void onCytoscapeExit() {
	    if( controller!=null ){
		controller.close();
	    }
        }
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {

	    /* If BIANA has been previously started, reset it */
	    // FOR THE MOMENT... IT DOES NOT WORK...
	    /*
	    if( started == true ){
		int confirm = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
							    "BIANA is currently running. Do you want to reset it?",
							    "BIANA process is running.",
							    JOptionPane.YES_NO_OPTION);
		if( confirm == JOptionPane.YES_OPTION ){
		    started = false;
		    controller.close_session_dialog();
		    controller.add_command("close()",true);
		    cytoscapeSocketThread.waitForClosedSocket();
		    controller = null;
		    //cytoscapeSocketThread.close();
		    jSessionPanel = null;
		    jSessionTreePanel = null;
		    //Remove the panels
		    ((CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)).remove(BianaPanelIndex);
		    ((CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)).remove(BianaShellIndex);
		    System.err.println("All is removed");
		}
		else{
		    System.err.println("Not closed");
		}
		}*/
	    
	    if( started == true ){
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					      "BIANA is currently running.",
					      "BIANA process is running.",
					      JOptionPane.ERROR_MESSAGE);
					      }
	 
	    /* If BIANA has not been started, it starts a instance of biana */
	    else{
		//if( started == false ){

		presentation = new Presentation(Cytoscape.getDesktop());
        	
		in = new PipedInputStream();
		out = new PipedOutputStream();

		try{
		    cytIn = new PipedInputStream(out);
		    cytOut = new PipedOutputStream(in);
		}
		catch( Exception e ){
		    System.err.println("ERROR");
		}	
        	
		controller = new BianaProcessController(Cytoscape.getDesktop(), CytoscapeInit.getConfigVersionDirectory().getAbsolutePath() + "/plugins/");
		controller.setCytoscapePlugin(self);

		cytoscapeSocketThread = new socketThread();
		cytoscapeSocketThread.add_listener(self);
		
		

		networkTitleToId = new Hashtable<String,String>();
        	
		JScrollPane shell = new JScrollPane(controller.getShell());
		((CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)).add("Biana command line", shell);
		//shell.requestFocus(true);
		BianaShellIndex = ((CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)).getCytoPanelComponentCount()-1;
		((CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)).add("Biana", getJBianaPanel());
		BianaPanelIndex = ((CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)).getCytoPanelComponentCount()-1;
		
		started = true;

		EventListenerClass cytoscapeListenerClass = new EventListenerClass();
                
		focusBianaElements();

                // not neccessary new attributes loaded by default
                //CyAttributes currentNodeAttributes = Cytoscape.getNodeAttributes();
                //currentNodeAttributes.setUserVisible("type", true);

                // load BIANA Vizmap file
		try{
		    Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null,this.getClass().getResource("BIANA.props") ); 
		}
		catch( Exception no_vizmap ){
		    System.err.println("Unable to open the vizmap file");
		}

		controller.ping_biana();

		//new Presentation();
		
		
		/*try{
		    Thread.sleep(3000);
		}
		catch( InterruptedException e ){
		    System.err.println("ERROR while waiting for closing presentation");
		    }*/
	    }


	    BIANA_added_attributes = new HashSet<String>();
	 
            //applyVIZMAP(VIZMAP_NAME);
            return; 
        }

    }

    private void focusBianaElements(){
	//Focus biana_panel
	((CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)).setSelectedIndex(BianaPanelIndex);
	//Focus shell
	((CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)).setSelectedIndex(BianaShellIndex);
    }

    private void setCurrentNetwork(String pName){
	if( this.networkTitleToId.containsKey(pName)==false ){
	    System.err.println("Trying to set "+pName+" as current network and it is not in networkTitleToId hash. Probably because this network is just recently creeated");
	}
	else{
	    if( current_network == null || current_network.getTitle().equals(pName)==false ){
		System.err.println("Setting as current network "+pName);		
		String id = this.networkTitleToId.get(pName);
		System.err.println("It has as ID: "+id);
		this.current_network = Cytoscape.getCurrentNetwork();
		Cytoscape.getDesktop().setFocus(id);
	    }
	}
    }

    /* BIANA POPUP MENU FOR EDGE SELECTION */
    public void addEdgeContextMenuItems(EdgeView edgeView, JPopupMenu menu){

	if( networkTitleToId.containsKey(this.current_network.getTitle())==false ){
	    System.err.println("Not showing biana menu because current network is "+this.current_network);
	    return;
    	}

	if( this.eERnodeMenu == null ){

	    JMenu bianaMenu = new JMenu("BIANA");
	    
	    JMenuItem tagSelectedEdgesMenuItem = new JMenuItem("Tag selected edges");
	    tagSelectedEdgesMenuItem.setActionCommand("tag_selected_edges");
	    tagSelectedEdgesMenuItem.addActionListener(self);
	    bianaMenu.add(tagSelectedEdgesMenuItem);
	    
	    JMenuItem removeSelectedEdgesMenuItem = new JMenuItem("Remove selected edges");
	    removeSelectedEdgesMenuItem.setActionCommand("remove_selected_edges");
	    removeSelectedEdgesMenuItem.addActionListener(self);
	    bianaMenu.add(removeSelectedEdgesMenuItem);

	    this.eERnodeMenu = bianaMenu;
	}

	if( menu == null ){
	    menu = new JPopupMenu();
	}

	menu.add(this.eERnodeMenu);
    }


    /* BIANA POPUP MENU FOR NODE SELECTION */
    public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu)
    {

	if( networkTitleToId.containsKey(this.current_network.getTitle())==false ){
	    System.err.println("Not showing biana menu because current network is "+this.current_network);
	    return;
    	}

	if( getSelectedNodes().size() == 0 ){
	    System.err.println("Not showing biana menu because ANY nodes selected");
	    return;
	}

	if( this.eEnodeMenu == null ){

	    JMenu bianaMenu = new JMenu("BIANA");
	
	    JMenuItem entityDetailsMenuItem = new JMenuItem("View entity details");
	    entityDetailsMenuItem.setActionCommand("view_uE_details");
	    entityDetailsMenuItem.addActionListener(self);
	    bianaMenu.add(entityDetailsMenuItem);
	    
	    JMenuItem newSetFromSubsetMenuItem = new JMenuItem("Create new set from selected nodes");
	    newSetFromSubsetMenuItem.setActionCommand("new_set_from_subset");
	    newSetFromSubsetMenuItem.addActionListener(self);
	    bianaMenu.add(newSetFromSubsetMenuItem);
	    
	    JMenuItem tagSelectedNodesMenuItem = new JMenuItem("Tag selected nodes");
	    tagSelectedNodesMenuItem.setActionCommand("tag_selected_nodes");
	    tagSelectedNodesMenuItem.addActionListener(self);
	    bianaMenu.add(tagSelectedNodesMenuItem);
	    
	    JMenuItem removeSelectedNodesMenuItem = new JMenuItem("Remove selected nodes");
	    removeSelectedNodesMenuItem.setActionCommand("remove_selected_nodes");
	    removeSelectedNodesMenuItem.addActionListener(self);
	    bianaMenu.add(removeSelectedNodesMenuItem);
	
	    this.eEnodeMenu = bianaMenu;
	    //entityDetailsMenuItem.addActionListener(new MyNodeAction(nodeView));

	}
	else{

	}
	
	if( menu == null ){
	    menu = new JPopupMenu();
	}
	
	menu.add(this.eEnodeMenu);

    }

    class MyNodeAction implements ActionListener {

	NodeView nodeView;
	
	public MyNodeAction(NodeView pNodeView){
	    nodeView = pNodeView;
	}
	
	
	public void actionPerformed(ActionEvent e){
	    //JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"MyNodeMenuItem on node "+ nodeView.getNode().getIdentifier() + " is clicked");
	}

    }

    //!
    /*
    public void onCyNetworkEvent(CyNetworkEvent e) {
        System.out.println("Network is touched: "+Cytoscape.getCurrentNetworkView().getNetwork().getTitle());
        if( e.getType() == CyNetworkEvent.BEGIN ) {
            String s = Cytoscape.getCurrentNetworkView().getNetwork().getTitle();
            if( current_network == null ){
                if( current_network.getTitle().equals(s)==false ){
                    controller.select_user_entity_set(s);
                }
            }
            return;
        } 
    }
    */

    private void applyVIZMAP(String vizmapName) {
                //System.out.println("ApplyVIZMAP");
                VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
                //Set<String> names = vmm.getCalculatorCatalog().getVisualStyleNames();
                VisualStyle vs = vmm.getCalculatorCatalog().getVisualStyle(vizmapName);
                if (vs == null) {
                    System.err.println("Visual style not found:" + vizmapName);
                    return;
                }
                Cytoscape.getCurrentNetworkView().setVisualStyle(vs.getName()); // not strictly necessary
                // actually apply the visual style
                vmm.setVisualStyle(vs);
                Cytoscape.getCurrentNetworkView().applyVizmapper(vs);
                Cytoscape.getCurrentNetworkView().redrawGraph(true,true);
                return;
    }


    ///* //!
    public class NetworkEventListenerClass implements CyNetworkListener { //extends CyNetworkAdapter { //implements CyNetworkListener {

        public NetworkEventListenerClass() {
            //Cytoscape.getCurrentNetwork().addCyNetworkListener(this);
            //Cytoscape.getCurrentNetworkView().getNetwork().addCyNetworkListener(this);
        }

	public void onCyNetworkEvent(CyNetworkEvent e) {
            System.out.println("Network is touched: "+Cytoscape.getCurrentNetworkView().getNetwork().getTitle());
            if( e.getType() == CyNetworkEvent.BEGIN ) {
		String s = Cytoscape.getCurrentNetworkView().getNetwork().getTitle();
		if( current_network == null ){
		    if( current_network.getTitle().equals(s)==false ){
		        controller.select_user_entity_set(s);
                    }
		}
                return;
            } 
        }
    }
    //*/

    public class EventListenerClass implements PropertyChangeListener{

	public EventListenerClass(){
	    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED,this);
	    Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_CREATED, this);
	    Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
	    Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_DESTROYED, this);
	    Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_TITLE_MODIFIED, this);
	}

        private void selectCurrentUserEntitySetInSessionTree() {
	    String s = Cytoscape.getCurrentNetworkView().getNetwork().getTitle();
                if( current_network == null ){
		    controller.select_user_entity_set(s);
		}
		else{
		    if( current_network.getTitle().equals(s)==false ){
			if( s.equals("0")==true ){
			    controller.unselect_all_user_entity_sets();
			}
			else{
			    controller.unselect_all_user_entity_sets();
			    controller.select_user_entity_set(s);
			}
		    }
		}
        }

	public void propertyChange(PropertyChangeEvent e){
            //if (vizmapLoaded == false) {
            //applyVIZMAP(VIZMAP_NAME);
            //    vizmapLoaded = true;
            //}

	    String s = Cytoscape.getCurrentNetworkView().getNetwork().getTitle();

	    if( networkTitleToId.containsKey(s)==false || disable_cytoscape_events_listener ){			
		return;
	    }

	    System.err.println(e.getPropertyName());
	    if( e.getPropertyName().equalsIgnoreCase(Cytoscape.VIZMAP_LOADED) || e.getPropertyName().equalsIgnoreCase(Cytoscape.VIZMAP_RESTORED) ) {
                //applyVIZMAP(VIZMAP_NAME);
                return;
            }
	    if( e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED)){
		return;
	    }
	    if( e.getPropertyName().equalsIgnoreCase(Cytoscape.NETWORK_CREATED)){
		System.out.println("NETWORK CREATED: " + Cytoscape.getCurrentNetwork().getTitle());
                //! following listener to focus the created network is not working
                NetworkEventListenerClass networkListenerClass = new NetworkEventListenerClass();
                //NetworkEventListenerClass networkListenerClass2 = new NetworkEventListenerClass();
                Cytoscape.getCurrentNetwork().addCyNetworkListener(networkListenerClass); //new NetworkEventListenerClass());
                //Cytoscape.getCurrentNetworkView().getNetwork().addCyNetworkListener(networkListenerClass2); 
                //System.out.println("are networks same: "+(Cytoscape.getCurrentNetwork() == Cytoscape.getCurrentNetworkView().getNetwork()));
		selectCurrentUserEntitySetInSessionTree();
                return; 
            } else if( e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)){
		//System.out.println("NETWORK_VIEW_FOCUSED: "+Cytoscape.getCurrentNetworkView().getNetwork().getTitle());
		System.out.println("NETWORK_VIEW_FOCUSED: "+s);
		selectCurrentUserEntitySetInSessionTree();
		//String s = Cytoscape.getCurrentNetworkView().getNetwork().getTitle();
		//System.err.println(s);
		/*if( s.equals("0")==true ){
		    controller.unselect_all_user_entity_sets();
		}
		else{*/
		//  System.err.println("IS SELECTED: "+controller.isUserEntitySetSelected(s));
		    //if( controller.isUserEntitySetSelected(s)==false ){
		//controller.unselect_all_user_entity_sets();
		//controller.select_user_entity_set(s,true);
		// }
		//}
		return;
	    }
	    else if( e.getPropertyName().equalsIgnoreCase(Cytoscape.NETWORK_DESTROYED)){
		//System.out.println("NETWORK DESTROYED. I have destroyed "+Cytoscape.getCurrentNetwork().getTitle()+"?");
		//System.err.println("Cytoscape plugin is requesting controller to destroy "+Cytoscape.getCurrentNetwork().getTitle());
		System.err.println("Cytoscape plugin is requesting controller to destroy "+s);
		controller.destroy_user_entity_set(Cytoscape.getCurrentNetwork().getTitle());
		//if( networkTitleToId.contains(s) ){
		System.err.println("Removing from cytoscape event");
		    networkTitleToId.remove(s);
		    //}
		//destroy_user_entity_set(Cytoscape.getCurrentNetwork().getTitle());
		return;
	    }
	    else if( e.getPropertyName().equalsIgnoreCase(Cytoscape.NETWORK_TITLE_MODIFIED)){
		JOptionPane.showMessageDialog( Cytoscape.getDesktop(), "Using BIANA you should not change network names... It won't work.");
		return;
	    }
	}
    }


    public void add_edge(String pName, String node1, String node2, String type, String relation_id) {

	setCurrentNetwork(pName);

	if( this.controller.edges_as_nodes.contains(type) ){
	    //System.err.println("Adding edge as a node");
	    relation_id = "r"+relation_id;
	    CyNode edgeNode = Cytoscape.getCyNode(relation_id, true);
	    Cytoscape.getNodeAttributes().setAttribute(relation_id, "type", type+"_edge");
	    CyEdge edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(node1,true),
					      edgeNode,
					      Semantics.INTERACTION, type+"_edge", true);
	    this.current_network.addEdge(edge);
	    Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), type, type);
	    edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(node2,true),
				       edgeNode,
				       Semantics.INTERACTION, type+"_edge", true);
	    this.current_network.addEdge(edge);
	    Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), type, type);
	}
	else{
	    CyEdge edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(node1,true),
					      Cytoscape.getCyNode(node2,true),
					      Semantics.INTERACTION, type, true);
	    //Semantics.INTERACTION,"pp", true));
	    this.current_network.addEdge(edge);
	    Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), "type", type);
	}
	//Cytoscape.firePropertyChange(Cytoscape.VIZMAP_RESTORED,null,null);
        //applyVIZMAP(VIZMAP_NAME);
    }
    
    public void update_network(String pName, int levels){}
    public void addTag(String pSetName, String tagName, String tagType){}

    public void select_user_entity(String pSetName, String node){
	//this.wait_for_selecting_process();
	//this.selecting_process = true;
	this.wait_for_selecting_process();
	this.setCurrentNetwork(pSetName);
	this.selecting_process = true;
	this.current_network.setSelectedNodeState(Cytoscape.getCyNode(node),true);
	this.selecting_process = false;
	Cytoscape.getCurrentNetworkView().updateView();
	//this.selecting_process = false;
    }

    public void select_user_entity(String pSetName, String[] nodes){
	this.wait_for_selecting_process();
	ArrayList<CyNode> d = new ArrayList<CyNode>();
	for( int i=0; i<nodes.length; i++ ){
	    d.add(Cytoscape.getCyNode(nodes[i],true));
	}
	this.selecting_process = true;
	this.current_network.setSelectedNodeState(d,true);
	this.selecting_process = false;
	Cytoscape.getCurrentNetworkView().updateView();
    }

    public void select_user_entity_relation(String pSetName, String node1, String node2, String type){
	this.wait_for_selecting_process();
	this.setCurrentNetwork(pSetName);
	this.selecting_process = true;
	this.current_network.setSelectedEdgeState(Cytoscape.getCyEdge(Cytoscape.getCyNode(node1,true),
								      Cytoscape.getCyNode(node2,true),
								      Semantics.INTERACTION, type, false),
						  true);
	this.selecting_process = false;
	Cytoscape.getCurrentNetworkView().updateView();
    }

    public void select_user_entity_relation(String pSetName, String[][] edges){
	this.wait_for_selecting_process();
	ArrayList<CyEdge> d = new ArrayList<CyEdge>();
	for( int i=0; i<edges.length; i++ ){
	    d.add(Cytoscape.getCyEdge(Cytoscape.getCyNode(edges[i][0],true),
				      Cytoscape.getCyNode(edges[i][1],true),
				      Semantics.INTERACTION, edges[i][2], false) );
	}
	this.selecting_process = true;
	this.current_network.setSelectedEdgeState(d,true);
	this.selecting_process = false;
	Cytoscape.getCurrentNetworkView().updateView();
    }

    public void remove_user_entities(String pSetName, String[] nodes){
	this.wait_for_selecting_process();
	setCurrentNetwork(pSetName);
	for( int i=0; i<nodes.length; i++ ){
	    this.current_network.removeNode(Cytoscape.getCyNode(nodes[i],false).getRootGraphIndex(),false);
	}
	Cytoscape.getCurrentNetworkView().updateView();
    }
    
    public void remove_user_entity_relations(String pSetName, String[][] edges){
	this.wait_for_selecting_process();
	setCurrentNetwork(pSetName);
	for( int i=0; i<edges.length; i++ ){
	    this.current_network.removeEdge(Cytoscape.getCyEdge(Cytoscape.getCyNode(edges[i][0],false),
								Cytoscape.getCyNode(edges[i][1],false),
								Semantics.INTERACTION, edges[i][2], false).getRootGraphIndex(), false );
	    System.err.println("Removing edge between "+edges[i][0]+" and "+edges[i][1]+"\n");
	}
	Cytoscape.getCurrentNetworkView().updateView();
    }

    public void addNodesToGroup(String pSetName, String pGroupName, String pGroupType, String pGroupID, String pParentGroupID, String pParentGroupName, String[] nodes){

	/*

	ArrayList<CyNode> nodeList = new ArrayList<CyNode>();
	for( int i=0; i<nodes.length; i++ ){
	    nodeList.add(Cytoscape.getCyNode(nodes[i],false));
	}
	setCurrentNetwork(pSetName);
	System.err.println("Creating group for "+nodeList.size()+" nodes");
	CyGroup test_group = CyGroupManager.createGroup(pGroupName, nodeList, null);
	if( test_group!=null ){
	    System.err.println("Group created. Assigning it to current view");
	    CyGroupManager.setGroupViewer(test_group,"test",Cytoscape.getCurrentNetworkView(),true);
	}
	*/
    }


    private void wait_for_selecting_process(){
	try{
	    while( this.selecting_process == true ){
		System.err.println("Wait operation");
		Thread.sleep(1000);
	    }
	}
	catch( InterruptedException e ){
	    System.err.println("ERROR while waiting for selecting process");
	}
    }

    public void unselect_user_entities(String pSetName){
	this.wait_for_selecting_process();
	this.selecting_process = true;
	System.err.println("Unselecting all nodes");
	this.setCurrentNetwork(pSetName);
	this.current_network.unselectAllNodes();
	//this.current_network.unselectAllEdges();
	Cytoscape.getCurrentNetworkView().updateView();
	this.selecting_process = false;
    }

    public void unselect_user_entity_relations(String pSetName){
	this.wait_for_selecting_process();
	this.selecting_process = true;
	this.setCurrentNetwork(pSetName);
	//System.err.println("Unselecting all edges");
	this.current_network.unselectAllEdges();
	Cytoscape.getCurrentNetworkView().updateView();
	this.selecting_process = false;
    }

    public void add_nodes(String pSetName, ArrayList<String[]> nodes_types){
	this.setCurrentNetwork(pSetName);
	CyNetwork net = this.current_network;
	for( Iterator it=nodes_types.iterator(); it.hasNext(); ){
	    String[] t = (String[])it.next();
	    net.addNode(Cytoscape.getCyNode(t[0], true));
	    Cytoscape.getNodeAttributes().setAttribute(t[0], "type", t[1]);
	}
    }


    public void add_edges(String pSetName, ArrayList<String[]> edges_types_and_id){
	setCurrentNetwork(pSetName);
	CyNetwork net = this.current_network;
	String relation_id;
	String node1;
	String node2;
	String type;
	for( Iterator it=edges_types_and_id.iterator(); it.hasNext(); ){
	    String[] t = (String[])it.next();
	    node1 = t[0];
	    node2 = t[1];
	    type = t[2];
	    relation_id = t[3];
	    if( this.controller.edges_as_nodes.contains(type) ){
		//System.err.println("Adding edge as a node");
		relation_id = "r"+relation_id;
		CyNode edgeNode = Cytoscape.getCyNode(relation_id, true);
		Cytoscape.getNodeAttributes().setAttribute(relation_id, "type", type+"_edge");
		CyEdge edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(node1,true),
						  edgeNode,
						  Semantics.INTERACTION, type+"_edge", true);
		//this.current_network.addEdge(edge);
		net.addEdge(edge);
		Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), type, type);
		edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(node2,true),
					   edgeNode,
					   Semantics.INTERACTION, type+"_edge", true);
		this.current_network.addEdge(edge);
		Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), type, type);
	    }
	    else{
		CyEdge edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(node1,true),
						  Cytoscape.getCyNode(node2,true),
						  Semantics.INTERACTION, type, true);
		//Semantics.INTERACTION,"pp", true));
		//this.current_network.addEdge(edge);
		net.addEdge(edge);
		Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), "type", type);
	    }
	}
    }
	
    public void add_node(String pSetName, String node, String type) {
	this.setCurrentNetwork(pSetName);
	this.current_network.addNode(Cytoscape.getCyNode(node, true));
        Cytoscape.getNodeAttributes().setAttribute(node, "type", type);
	//Cytoscape.firePropertyChange(Cytoscape.VIZMAP_RESTORED,null,null);
        //applyVIZMAP(VIZMAP_NAME);
    }

    public void new_user_entity_set(String pName) {
	//System.err.println("Going to create new user entity set "+pName);
	if( this.networkTitleToId.containsKey(pName) ){
	    //System.err.println("Trying to insert a network with the same name");
	    JOptionPane.showMessageDialog( Cytoscape.getDesktop(), "Trying to insert a network with the same name");
	}
	else{
	    //System.err.println("Really creating it...");
	    current_network = Cytoscape.createNetwork(pName);
	    networkTitleToId.put(pName,current_network.getIdentifier());
	    //System.err.println("Network "+pName+" created with identifier "+current_network.getIdentifier());
	    CyNetworkView view = Cytoscape.createNetworkView(current_network);
	    // no use to create a view with layout since nodes will be added later, instead update
	    //CyNetworkView view = Cytoscape.createNetworkView(current_network, pName, new GridNodeLayout());
	    //System.err.println("NetworkView "+pName+" created with identifier "+current_network.getIdentifier());
	    view.addNodeContextMenuListener(this);
	    view.addEdgeContextMenuListener(this);
	    focusBianaElements();
	    this.select_user_entity_set( pName );
	    setApplyLayout(Layout.GRID);
	}
    }


    public void select_user_entity_set(String pName){
	if( current_network == null ){
	    this.setCurrentNetwork(pName);
	}
	else if( current_network.getTitle().equals(pName)==false ){
	    this.setCurrentNetwork(pName);
	}
    }
	
    public void destroy_user_entity_set(String pName){
	if( this.networkTitleToId.containsKey(pName) ){
	    try{
		disable_cytoscape_events_listener = true;
		System.err.println("Destroying "+pName+" from destroy_user_entity_set");
		//if( Cytoscape.getNetworkView(this.networkTitleToId.get(pName)) != null ){
		Cytoscape.destroyNetworkView(this.networkTitleToId.get(pName));
		//}
		//if( Cytoscape.getNetwork(this.networkTitleToId.get(pName)) != null ){
		Cytoscape.destroyNetwork(this.networkTitleToId.get(pName));
		//}
		this.networkTitleToId.remove(pName);
		//controller.destroy_user_entity_set(Cytoscape.getCurrentNetwork().getTitle());

		disable_cytoscape_events_listener = false;
	    }
	    catch( Exception e ){
		System.err.println("ERROR HERE...");
		e.printStackTrace();
	    }
	}
    }
    

    /* UNIMPLEMENTED METHODS */

    public void unselect_all_user_entity_sets(){}

    public void end_user_entity_set_data(){
	focusBianaElements();
	
	//Update view
	Cytoscape.getCurrentNetworkView().updateView();
	switch(this.applyLayout) {
	    case GRID:
	    {
		Cytoscape.getCurrentNetworkView().applyLayout(new GridNodeLayout());
		setApplyLayout(Layout.NONE);
	    } break;
	    case CIRCULAR:
	    {
		Cytoscape.getCurrentNetworkView().applyLayout(new GridNodeLayout());
		setApplyLayout(Layout.NONE);
	    } break;
	}
        applyVIZMAP(VIZMAP_NAME);
    }

    public void setApplyLayout(Layout layout) {
	this.applyLayout = layout;
    }

    public void show_table_details(Vector<String> columns, Vector<Vector<String>> values, Vector<String> rowIdentifiers, String title, String command){
	if( title != null && title.compareTo("User Entity Set Details")==0 ){
	    for( int i=0; i<values.size(); i++ ){
		for( int j=0; j<columns.size(); j++ ){
		    if(values.get(i).get(j)!=null){
			Cytoscape.getNodeAttributes().setAttribute(rowIdentifiers.get(i),columns.get(j),values.get(i).get(j));
			BIANA_added_attributes.add(columns.get(j));
		    }
		}			   
	    }
	    Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED,null,null);
	}
	if( title != null && title.compareTo("User Entity Set Network Details")==0 ){
	    System.err.println("I should lad network details...\n");

	    int participant1_id_col = -1;
	    int participant2_id_col = -1;
	    int relation_type_col = -1;

	    for( int j=0; j<columns.size(); j++ ){
		if( columns.get(j).compareTo("Relation IDs")==0 || columns.get(j).compareTo("Relation Source Databases")==0 ){		    
		}
		else if( columns.get(j).compareTo("Relation Types")==0 ){
		    relation_type_col = j;
		}
		else if( columns.get(j).compareTo("Participant 2 User Entity")==0 ){
		    participant2_id_col = j;
		}
		else if( columns.get(j).compareTo("Participant 1 User Entity")==0 ){
		    participant1_id_col = j;
		}
		else if( participant1_id_col != -1 && participant2_id_col != -1 ){
		    for( int i=0; i<values.size(); i++ ){
			if(values.get(i).get(j)!=null){
			    CyEdge edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(values.get(i).get(participant1_id_col),true),
							      Cytoscape.getCyNode(values.get(i).get(participant2_id_col),true),
							      Semantics.INTERACTION, values.get(i).get(relation_type_col), true);  
			    Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),columns.get(j),values.get(i).get(j));
			    BIANA_added_attributes.add(columns.get(j));
			}
		    }
		}
            }
            Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED,null,null);
	}
	//else{
	//    new ShowTableDialog(this.controller,title,columns,values,rowIdentifiers, command).setVisible(true);
	//}
    }

    public void new_session(String dbname, String dbhost, String pUnificationProtocol, String pSessionID, String pSessionDescription){
    	this.getJSessionPanel().removeAll();
    	this.getJSessionPanel().add("BIANA Working Sets",new JScrollPane(this.controller.getSessionTree()));
    	jOptionSessionButton.setText("Close Session");
	jOptionSessionButton.setActionCommand("close_session");
    }


    public void close_session(String pSessionID){
	// It is necessary first to remove the tree... because if not an exception is produced
	this.getJSessionPanel().removeAll();
	jOptionSessionButton.setText("New Session");
	jOptionSessionButton.setActionCommand("new_session");
	jOptionSessionButton.setToolTipText("<html> A BIANA session is a container for all user generated sets of biomolecules and their networks:<br>All query/data retrieval operations are going to be handled over this session using any BIANA Database created/configured previously.<br>Start creating/analyzing new sets of biomolecules of interest and their interactions by<br><ul><li>Loading from a previously created and saved session</li><li>Creating a brand new session</li></ul></html>");

	ArrayList<String> t = new ArrayList<String>();
	for( Iterator it = this.networkTitleToId.keySet().iterator(); it.hasNext(); ){
	    t.add((String)it.next());
	}
	for( Iterator it = t.iterator(); it.hasNext(); ){
	    this.destroy_user_entity_set((String)it.next());
	}


	//It is necessary to remove all attributes added by BIANA, because if other sessions start with other unification protocols, attributes may be assigned incorrectly!
	for( Iterator it = BIANA_added_attributes.iterator(); it.hasNext(); ){
	    Cytoscape.getNodeAttributes().deleteAttribute((String)it.next());
	}
    }
    
    public void new_biana_database(String dbname, String dbhost, String dbuser, String dbpass, Vector<String> pAvailableAttributes, Vector<ExternalDatabase> pExternalDatabases, Hashtable<String,String> pAttributeOntologies){}
    public void delete_biana_database(String dbname, String dbhost){}
    public void error_message(String pMessageSummary, String errorMessage){}
    public void show_info_message(String message){}

    public void biana_ping_response(){
	if( this.presentation != null ){
	    this.presentation.dispose();
	    this.presentation = null;
	}
    }

    public void processing_message(String message){
	//Cytoscape.getDesktop().setEnabled(false);    // I SHOULD DISABLE IT...
	num_processing_messages++;
    }

    public void end_processing(){
	num_processing_messages--;
	if( num_processing_messages==0 ){
	    Cytoscape.getDesktop().setEnabled(true);
	}
    }

    public void set_command_completed() {} ;

    public void newOntologyTree(String name, JTree tree){}
    public void not_available_biana_database(String dbname, String dbhost ){}
    public void closedConnection(String pMessage) {}
    public void add_available_parser(String[] attributes){}
    public void add_available_external_entity_type(String name){}
    public void add_available_external_entity_attribute(String name){}
    public void add_available_external_entity_relation_type(String name){}
    public void reconnect(){}

    //public void add_unification_protocol_atom(String dbA, String dbB, String attributes){}

    /**
     * This method initializes jBianaPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJBianaPanel() {
	if (jBianaPanel == null) {
	    jBianaPanel = new JPanel();
	    jBianaPanel.setLayout(new BorderLayout());
	    //jBianaPanel.setSize(new Dimension(274, 183));
	    jBianaPanel.setSize(new Dimension(300,183));
	    jBianaPanel.add(getJButtonsPanel(), BorderLayout.NORTH);
	    jBianaPanel.add(getJSessionPanel(), BorderLayout.CENTER);
	}
	return jBianaPanel;
    }
    
    /**
     * This method initializes jButtonsPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJButtonsPanel() {
	if (jButtonsPanel == null) {
	    jButtonsPanel = new JPanel();
	    jButtonsPanel.setLayout(new FlowLayout());
	    jButtonsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(null, "BIANA Options", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), SystemColor.activeCaption), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	    jButtonsPanel.add(getJOptionSessionButton(), null);
	    jButtonsPanel.add(getJConfigurationButton(), null);
	    jButtonsPanel.add(getJHelpButton(), null);
	}
	return jButtonsPanel;
    }

    /**
     * This method initializes jOptionSessionButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJOptionSessionButton() {
	if (jOptionSessionButton == null) {
	    jOptionSessionButton = new JButton();
	    jOptionSessionButton.setText("New Session");
	    //jOptionSessionButton.addActionListener(this);
	    jOptionSessionButton.addActionListener(controller);
	    jOptionSessionButton.setActionCommand("new_session");
	    jOptionSessionButton.setToolTipText("<html>A BIANA session is a container for all user generated sets of biomolecules and their networks:<br> All query/data retrieval operations are going to be handled over this session using any BIANA Database created/configured previously.<br> Start creating/analyzing new sets of biomolecules of interest and their interactions by<br><ul><li>Loading from a previously created and saved session</li><li>Creating a brand new session</li></ul></html>");
	}
	return jOptionSessionButton;
    }

    /**
     * This method initializes jConfigurationButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJConfigurationButton() {
	if (jConfigurationButton == null) {
	    //jConfigurationButton = new JButton("Configuration", new ImageIcon(this.getClass().getResource("./img/stock_form-properties.png")));
	    jConfigurationButton = new JButton("Configuration");
	    jConfigurationButton.addActionListener(this);
	    jConfigurationButton.setToolTipText("<html>Configure <i>BIANA database</i>s, unification protocols and settings. Here you can:<br><ul><li>Create a new & empty <i>BIANA Database</i> (to be populated by parsers later)</li><li>Add a previously created & possibly populated <i>BIANA database</i></li><li>Parse one or more biological data source (<i> External Database</i>) to an existing <i>BIANA Database</i></li><li>Update information of <i>BIANA Database</i>s</li><li>Define a new <i>Unification Protocol</i>(how biomolecules coming from <i>External Database</i>s are going to be considered equivalent)</li><li>Display information of available <i>Unification Protocol</i>s in <i>BIANA Database</i>s</li><li>Remove an existing <i>Unification Protocol</i></li><li>Remove an existing <i>BIANA database</i></li><li>Decide BIANA preferences such as which Python interpreter to use or how the data represented in the network</li></ul></html>");
	}
	return jConfigurationButton;
    }

    private JButton getJHelpButton(){
	if (jHelpButton == null) {
	    jHelpButton = new JButton("Help");
	    jHelpButton.addActionListener(this);
	    jHelpButton.setToolTipText("<html>Information about BIANA software:<br><ul><li>Help will guide you to comprehensive documentation available online</li><li>About will give information about people involved in BIANA project</li></ul></html>");
	}
	return jHelpButton;
    }


    /**
     * This method initializes jSessionPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JTabbedPane getJSessionPanel() {
	if (jSessionPanel == null) {
	    jSessionPanel = new JTabbedPane();
	    jSessionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(null, "Current Session", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), SystemColor.activeCaption), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	}
	return jSessionPanel;
    }

    private java.util.Set getSelectedEdges(){
	java.util.Set selected_edges = Cytoscape.getCurrentNetwork().getSelectedEdges();
	HashSet <String>tuple_set = new HashSet<String>();
	for( Iterator it=selected_edges.iterator(); it.hasNext(); ){
	    CyEdge current_edge = (CyEdge)it.next();
	    if( current_edge.getSource().getIdentifier().toString().startsWith("r")==false && current_edge.getTarget().getIdentifier().toString().startsWith("r")==false ){
		tuple_set.add("("+current_edge.getSource().getIdentifier()+","+current_edge.getTarget().getIdentifier()+",\""+Cytoscape.getEdgeAttributes().getAttribute(current_edge.getIdentifier(),"type")+"\")");
	    }
	}
	return tuple_set;
    }

    //private java.util.Set getSelectedNodes() {
    public java.util.Set getSelectedNodes() {
	HashSet<String> nodes_to_select = new HashSet<String>();
	Iterator it = Cytoscape.getCurrentNetwork().getSelectedNodes().iterator();
	while( it.hasNext() ){
	    String t = it.next().toString();
	    if( t.startsWith("r")==false ){
		nodes_to_select.add(t);
	    }
	}
	return nodes_to_select;
    }

    private JPopupMenu getAdministrationPopupMenu(){

	if ( this.jAdministrationPopupMenu == null ){
	    this.jAdministrationPopupMenu = new JPopupMenu();
	    BIANA.addDBAdministrationMenuItems(controller, this.jAdministrationPopupMenu);
	    this.jAdministrationPopupMenu.addSeparator();
	    JMenuItem temp = new JMenuItem("Preferences");
	    temp.addActionListener(this.controller);
	    temp.setActionCommand("view_preferences");
            temp.setToolTipText("<html>Specify preferred settings:<br><ul><li>Change used Python interpreter</li><li>Specify relation types for which an additional node will be inserted in the network view (e.g. for an advanced visual representation of complexes)</li></ul></html>");
	    this.jAdministrationPopupMenu.add(temp);
	}
	return this.jAdministrationPopupMenu;
    }


    private JPopupMenu getJHelpPopupMenu(){

	if( this.jHelpPopupMenu == null ){
	    this.jHelpPopupMenu = new JPopupMenu();
	    BIANA.addHelpMenuItems(this, this.jHelpPopupMenu);
	}
	return this.jHelpPopupMenu;

    }
    
    public void actionPerformed(ActionEvent e) {
        //applyVIZMAP(VIZMAP_NAME);
	if( "about".equals(e.getActionCommand()) ){
	    new Credits();
	}
	else if( "help".equals(e.getActionCommand()) ){

	    BareBonesBrowserLaunch.openURL("http://sbi.imim.es/web/BIANA.php?page=biana.documentation");
	    
	    // Commented because not available in java 1.6 (and this is not available for MacOs)
	    /*
	    //System.err.println("I should open navigator...");
	    if( !java.awt.Desktop.isDesktopSupported() ){
		System.err.println( "Desktop is not supported (fatal)" );
	    }
	    else{
		java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

		if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ){
		    System.err.println( "Desktop doesn't support the browse action (fatal)" );
		}
		else{
		    try{
			java.net.URI uri = new java.net.URI( "http://sbi.imim.es/biana" );
			desktop.browse(uri);
		    }
		    catch( Exception exc ){
			System.err.println( exc.getMessage() );
		    }
		}
		}*/
	    
	}
	else if( e.getSource()==this.getJConfigurationButton()){
	    this.getAdministrationPopupMenu().show(this.jConfigurationButton, 0, this.jConfigurationButton.getHeight());
	}
	else if( e.getSource()==this.getJHelpButton() ){
	    this.getJHelpPopupMenu().show(this.getJHelpButton(), 0, this.getJHelpButton().getHeight());
	}
	else if( "view_uE_details".equals(e.getActionCommand() ) ){
	    //this.controller.selectUserEntities(Cytoscape.getCurrentNetwork().getSelectedNodes(), true);
	    this.controller.selectUserEntities(this.getSelectedNodes(), true);
	    this.controller.view_uE_details();
        }
        else if( "new_set_from_subset".equals(e.getActionCommand() ) ){
	    //this.controller.newSetFromSelected(Cytoscape.getCurrentNetwork().getSelectedNodes());
	    this.controller.newSetFromSelected(this.getSelectedNodes());
	}
	else if( "tag_selected_nodes".equals(e.getActionCommand() ) ){
	    /*HashSet<String> nodes_to_select = new HashSet<String>();
	    HashSet<String> edges_to_select = new HashSet<String>();  // TODO
	    Iterator it = Cytoscape.getCurrentNetwork().getSelectedNodes().iterator();
	    while( it.hasNext() ){
		String t = it.next().toString();
		if( t.startsWith("r")==false ){
		    nodes_to_select.add(t);
		}
	    }
	    //this.controller.tagSelectedNodes(Cytoscape.getCurrentNetwork().getSelectedNodes());
	    this.controller.tagSelectedNodes(nodes_to_select);
	    */
	    this.controller.tagSelectedNodes(this.getSelectedNodes());
	}
	else if( "tag_selected_edges".equals(e.getActionCommand() ) ){
	    this.controller.tagSelectedEdges(this.getSelectedEdges());
	}
	else if( "remove_selected_nodes".equals(e.getActionCommand() ) ){
	    int confirm = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
							"Are you sure you want to remove selected user entities? This action is not reversible.",
							"Delete confirmation",
							JOptionPane.YES_NO_CANCEL_OPTION);
	    if( confirm == JOptionPane.YES_OPTION ){
		//this.controller.selectUserEntities(Cytoscape.getCurrentNetwork().getSelectedNodes(), true);
		this.controller.selectUserEntities(this.getSelectedNodes(), true);
		this.controller.remove_selected_user_entities();
	    }
	}
	else if( "remove_selected_edges".equals(e.getActionCommand() ) ){
	    int confirm = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
							"Are you sure you want to remove selected relations? This action is not reversible",
							"Delete confirmation",
							JOptionPane.YES_NO_CANCEL_OPTION);
	    if( confirm == JOptionPane.YES_OPTION ){
		this.controller.selectUserEntityRelations(this.getSelectedEdges(), true);
		this.controller.remove_selected_user_entity_relations();
	    }
	}
   } 
}
