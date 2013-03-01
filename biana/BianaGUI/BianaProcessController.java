import java.io.*;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.HashSet;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.util.Iterator;
import javax.swing.JOptionPane;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;

import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;

import java.util.ArrayList;

import java.util.Properties;
import java.util.Map;
import java.util.Properties;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.Collection;
import java.util.Stack;

//! for encryption of database user & password but requires installation of policy files - usage aborted
import javax.crypto.Cipher; 
import javax.crypto.spec.SecretKeySpec;

/**
 * Class that controls the BIANA process
 * This class starts a BIANA process using th PythonInterpreter, and manages its input and outputs
 * The standard input and output goes through a ShellPane
 * The objects representation goes to a tree
 * 
 * @author Javier Garcia
 *
 */
public class BianaProcessController implements ActionListener, BianaListener {
    	
    //private static String HOME_PATH = System.getenv().get("HOME");
    private static String BIANA_CONF_PATH = System.getProperty("user.home")+System.getProperty("file.separator")+".biana"+System.getProperty("file.separator")+"v1.0"+System.getProperty("file.separator");
    private static String CONFIGURATION_FILE = "biana_properties.config";
    
    public static Properties BIANA_PROPERTIES = new Properties();
	
    //Pipes to communicate with berit process Standard Input/Output
    private PipedInputStream  beritProcessIn = null;
    
    private PipedOutputStream streamToShell = null;
    private PipedOutputStream streamToInterpreter = null;
    
    private socketThread beritSocket = null;
    
    private PythonInterpreter interpreter = null;
    
    //Visual objects
    private ShellPane shell = null;
    private JFrame parentFrame = null;
    private JPanel sessionTreesPanel = null;
    
    //Objects management
    private BianaSession session = null;
    
    private Stack<String> processingMessagesStack = null;

    private JTabbedPane jSessionPanel = null;

    private JFileChooser jFileChooserSave = null;
    
    /* Stores the information about available biana databases */
    private HashSet<BianaDatabase> all_available_biana_databases = null;
    private HashSet<BianaDatabase> available_biana_databases = null;
    private Vector<String[]> available_parsers = null;
    //private Vector<String[]> unification_protocol_atoms = null;
    private Vector<String> available_external_entity_types = null;
    private Vector<String> available_external_entity_relation_types = null;
    private Vector<String> available_external_entity_attributes = null;
	
    private int next_user_entity_set_id = 1;

    private boolean new_message_added = false;

    private ProcessingMessageDialog processingMessageDialog = null; 

    private boolean enabled = true;

    private BianaCytoscapePlugin cytoscapePlugin = null;

    /* Preferences elements */
    private JDialog preferencesDialog = null;
    private JTextField python_exec_text_field = null;
    private JList jAvailableRelationTypesList = null;
    public HashSet edges_as_nodes = null;

    private boolean isCommandCompleted = true;

    /* Dialogs */
    private JDialog parseDatabasesDialog = null;



    public BianaProcessController(JFrame parent, String propertyFileDir) {
        CONFIGURATION_FILE = propertyFileDir + CONFIGURATION_FILE;
	this.parentFrame = parent;
        initiliazeBiana();
    }

    public BianaProcessController(JFrame parent)  {
        CONFIGURATION_FILE = BIANA_CONF_PATH + CONFIGURATION_FILE;
	this.parentFrame = parent;
        initiliazeBiana();
    }

    private void initiliazeBiana() {

	try{

	    this.processingMessagesStack = new Stack<String>();

            //System.err.println("here: " + encrypt("test") + " " + decrypt(encrypt("test"))); // en
	    //load properties
	    try{
		BIANA_PROPERTIES.load(new FileInputStream(BianaProcessController.CONFIGURATION_FILE));
	    }
	    catch( Exception e ){
		System.err.println("File "+BianaProcessController.CONFIGURATION_FILE+" not found (probably first execution)");
	    }
		
	    //Start pipes
	    this.beritProcessIn = new PipedInputStream(); 
	    this.streamToShell = new PipedOutputStream();
	    this.streamToInterpreter = new PipedOutputStream(this.beritProcessIn);
	    
	    //Start visual objects
	    this.shell = new ShellPane(new PipedInputStream(this.streamToShell),this.streamToInterpreter);
	    this.sessionTreesPanel = new JPanel();
	    
	    //Biana DB management
	    this.available_biana_databases = new HashSet<BianaDatabase>();
	    this.all_available_biana_databases = new HashSet<BianaDatabase>();
	    this.available_parsers = new Vector<String[]>();
	    this.available_external_entity_types = new Vector<String>();
	    this.available_external_entity_relation_types = new Vector<String>();
	    this.available_external_entity_attributes = new Vector<String>();
	    
	    beritSocket = new socketThread();
	    beritSocket.add_listener(this);
	    
	    //Start python interpreter with the biana module
	    System.err.println("Going to start biana process");
	    this.startBianaProcess();
	    
	    //Load available databases
	    System.err.println("Going to load available databases");
	    this.load_available_databases();

	    //Sleep to eliminate previous messages
	    //Thread.sleep(4000);
	    //this.shell.reset();

	    /* Preferences dialog elements */
	    this.python_exec_text_field = new JTextField();
	    this.edges_as_nodes =  new HashSet<String>();
	    this.load_edges_as_nodes();
	    
	    System.err.println("Loading default starting commands");
	    this.sendDefaultStartCommands();
	}
	catch( Exception e ){
	    System.err.println("Error");
	    e.printStackTrace();
	}
    }


    public void setEnabled(boolean value){
	this.enabled = value;
    }


    public void setCytoscapePlugin(BianaCytoscapePlugin cp) {
	this.cytoscapePlugin = cp;
    }

    public BianaCytoscapePlugin getCytoscapePlugin() {
	return this.cytoscapePlugin;
    }

    private void sendDefaultStartCommands() {
	this.add_command("administration.get_available_parsers()", false);
	this.add_command("administration.get_available_external_entity_types()", false);
	this.add_command("administration.get_available_external_entity_relation_types()", false);
	this.add_command("administration.get_available_external_entity_attributes()", false);
	//this.add_command("load_session(\"/home/emre/session.dat\")",true);
	//this.add_command("available_sessions[\"biana_session\"].create_network( user_entity_set_id = \"User_Entity_Set_2\" , level = 1 , include_relations_last_level = False , use_self_relations = False , expansion_relation_type_list = [\"interaction\"] , expansion_attribute_list = [ [(\"genesymbol\", [])] ], expansion_level = 2 )",true);
    }

    private void startBianaProcess() {
    	
    	System.err.println(BianaProcessController.BIANA_PROPERTIES.getProperty("python_exec"));
    	try {
	    BianaProcessController.BIANA_PROPERTIES.store(System.err, "test");
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	
	if( BianaProcessController.BIANA_PROPERTIES.getProperty("python_exec") == null ){
	}

    	while(true){
	    //System.err.println("1: "+BianaProcessController.BIANA_PROPERTIES.getProperty("python_exec"));
	    boolean isPythonSet = false;
	    if( BianaProcessController.BIANA_PROPERTIES.getProperty("python_exec") == null ){
		BianaProcessController.BIANA_PROPERTIES.setProperty("python_exec","python_unknown"); // required for getter method of the configuration file was used to avoid infinite loop 

		// Try to find the python interpreter
		String paths[] = System.getenv("PATH").split(System.getProperty("path.separator"));
		String python_execs[] = {"python","Python","python.exe"};

		String found_path = null;

		System.err.println("Python interpreter not found. Trying to locate it automatically");

		for( int i=0; i<paths.length; i++ ){
		    for( int j=0; j<python_execs.length; j++ ){
			File f = new File(paths[i]+System.getProperty("file.separator")+python_execs[j]);
			if( f.exists() ){
			    found_path = paths[i]+System.getProperty("file.separator")+python_execs[j];
			    BianaProcessController.BIANA_PROPERTIES.setProperty("python_exec",found_path);
			    System.err.println("Python interpreter found at "+ found_path);
			    isPythonSet = true;
			}
		    }
		}
	    } else {
		isPythonSet = true;
	    }

	    try{
		if(isPythonSet) {
		    interpreter = new PythonInterpreter(BianaProcessController.BIANA_PROPERTIES.getProperty("python_exec"),this.beritProcessIn,this.streamToShell, this);
		    break;
		} else {
		    this.getSetPythonInterpreterDialog();
		    isPythonSet = true;
		}
	    }
	    catch( Exception e ){

		//this.getSetPythonInterpreterDialog();
		/*}
		else{
		    this.close();
		    }*/
	    }
    	}
    	
    	//Import biana module
	//this.add_command("from biana import *",true);
    	this.add_command("import sys\ntry: from biana import *\nexcept: sys.exit(10)\n",true);
	this.reconnect();
    }

    public void reconnect(){
	this.add_command("OutBianaInterface.connect_to_socket(port="+beritSocket.getPort()+")", false);
    }
    
    /**G
     * 
     *
     */
    private void getSetPythonInterpreterDialog() {
    	JFileChooser fc = new JFileChooser(BianaProcessController.BIANA_PROPERTIES.getProperty("python_exec"));
	fc.setDialogTitle("Python interpreter not found! Select Python Interpreter manually");
	fc.setSelectedFile(new File(BianaProcessController.BIANA_PROPERTIES.getProperty("python_exec")));
	int returnVal = fc.showOpenDialog(this.parentFrame);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    BianaProcessController.BIANA_PROPERTIES.setProperty("python_exec", fc.getSelectedFile().getPath());
	    //System.err.println("Going to save python_exec...");
	    this.saveProperties(true);
	    python_exec_text_field.setText(BianaProcessController.BIANA_PROPERTIES.getProperty("python_exec"));
	}
    }


    private JDialog getPreferencesDialog(){

	if( this.preferencesDialog == null ){

	    // Create dialog
	    this.preferencesDialog = new JDialog(this.getParentFrame());
	    this.preferencesDialog.setSize(500,250);
	    this.preferencesDialog.setTitle("BIANA Graphical Interface preferences");

	    //create ContentPanel
	    JPanel contentPanel = new JPanel();
	    contentPanel.setLayout(new BorderLayout());
	    this.preferencesDialog.setContentPane(contentPanel);


	    jAvailableRelationTypesList = new JList(this.available_external_entity_relation_types);


	    //Create Buttons Panel
	    FlowLayout flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.RIGHT);
	    JPanel jButtonsPanel = new JPanel();
	    jButtonsPanel.setLayout(flowLayout);
	    JButton jAcceptButton = new JButton();
	    jAcceptButton.setText("Accept");
	    jAcceptButton.addActionListener( new ActionListener(){
		    public void actionPerformed( ActionEvent evt ){
			Object[] selected_values = jAvailableRelationTypesList.getSelectedValues();
			String[] t = new String[selected_values.length];
			for( int i=0; i<selected_values.length; i++ ){
			    t[i] = (String)selected_values[i];
			}
			setEdgesAsNodes(t);
			preferencesDialog.dispose();
		    }
		} );
	    jButtonsPanel.add(jAcceptButton, null);
	    this.preferencesDialog.add(jButtonsPanel,BorderLayout.SOUTH);

	    

	    //Create main panel
	    JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

	    //Create python exec panel
	    flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);	    
	    JPanel jPythonExecPanel = new JPanel();
	    jPythonExecPanel.setLayout(flowLayout);
	    jPythonExecPanel.add( new JLabel("Python interpreter") );
	    //JTextField python_exec_text_field = new JTextField();
	    python_exec_text_field.setColumns(25);
	    python_exec_text_field.setEnabled(false);
	    python_exec_text_field.setText(BianaProcessController.BIANA_PROPERTIES.getProperty("python_exec"));
	    jPythonExecPanel.add( python_exec_text_field );
	    jPythonExecPanel.setLayout(flowLayout);
	    JButton jChangePythonExecButton = new JButton();
	    jChangePythonExecButton.setText("Change");

	    jChangePythonExecButton.addActionListener( new ActionListener() {
		    public void actionPerformed( ActionEvent evt ){
			getSetPythonInterpreterDialog();
		    }
		});


	    jChangePythonExecButton.setActionCommand("change_python_exec");
	    jPythonExecPanel.add(jChangePythonExecButton, null);

	    mainPanel.add( jPythonExecPanel );
	    
	    //Create edges as nodes panel
	    flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    JPanel jEdgesAsNodesPanel = new JPanel();
	    jEdgesAsNodesPanel.setLayout(flowLayout);
	    jEdgesAsNodesPanel.add( new JLabel("Relations showed GRAPHICALLY as a nodes:") );
	    JScrollPane tempScrollPane = new JScrollPane();
	    tempScrollPane.setPreferredSize(new Dimension(150,100));
	    tempScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

	    

	    int[] selected_indices = new int[this.edges_as_nodes.size()];
	    int i = 0;
	    for( Iterator it = this.edges_as_nodes.iterator(); it.hasNext(); ){
		jAvailableRelationTypesList.setSelectedValue((String)it.next(),true);
		selected_indices[i++] = jAvailableRelationTypesList.getSelectedIndex();
	    }
	    jAvailableRelationTypesList.setSelectedIndices(selected_indices);


	    tempScrollPane.setViewportView(jAvailableRelationTypesList);
	    jEdgesAsNodesPanel.add( tempScrollPane );

	    mainPanel.add( jEdgesAsNodesPanel );

	    this.preferencesDialog.add(mainPanel, BorderLayout.CENTER);
	}

	return this.preferencesDialog;
    }

    
    /** GETTERS */
    public JPanel getSessionTree()  {
    	return this.session.getSessionTree();
    	//return this.sessionTreesPanel;
    }

    public String getSessionID() {
	return this.session.getID();
    }

    public ShellPane getShell()  { 
    	return shell;
    }

    public BianaSession getBianaSession() {
    	return this.session;
    }
    
    /**
     * This method initializes jSessionPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    public JTabbedPane getJSessionPanel()  {
	if (jSessionPanel == null) {
	    jSessionPanel = new JTabbedPane();
	    jSessionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(null, "Current Session", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), SystemColor.activeCaption), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	}
	return jSessionPanel;
    }
    
    /**
	 * This method initializes JFileChooserSave
	 * 	
	 * @return javax.swing.JFileChooser	
	 */
    private JFileChooser getJFileChooserSave()  {
    	if(jFileChooserSave == null) {
	    	jFileChooserSave = new JFileChooser();
		jFileChooserSave.setDialogType(JFileChooser.SAVE_DIALOG);
	        jFileChooserSave.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    	jFileChooserSave.setDialogTitle("Select input file/directory");
    	} 
    	return jFileChooserSave;
    }
	
    public void showNewSessionDialog() {
    	
	
    	BianaDatabase[] t = this.get_available_biana_databases();
    	if( t.length>0 ){
    		new CommandDialog(this, new NewSessionPanel(this.get_available_biana_databases(),""),"Start new Biana Working session",new Dimension(400,300));
    	}
    	else{
    		Object[] options = {"Add existing BIANA Database",
				    "Create new BIANA Database",
				    "Exit BIANA"};
    		int n = JOptionPane.showOptionDialog(this.parentFrame,
						     "There are not available BIANA databases. What do you want?",
						     "BIANA Databases Error",
						     JOptionPane.YES_NO_OPTION,
						     JOptionPane.ERROR_MESSAGE,
						     null,     //do not use a custom Icon
						     options,  //the titles of buttons
						     options[0]); //default button title
    		
    		if( n==0 ){
    			this.showAddBianaDatabaseDialog();
    		}
    		else if( n== 1){
    			this.showNewBianaDatabaseDialog();
    		}
    		else if( n==2 ){
    			this.close();
    		}
    	}
    }
    
    private void showNewBianaDatabaseDialog() {
    	new CommandDialog(this, new CreateNewBianaDBPanel(),"Create new BIANA database",new Dimension(330,270));
    }
    
    private void showAddBianaDatabaseDialog() {
    	new CommandDialog(this, new AddBianaDBPanel(),"Add existing BIANA database",new Dimension(330,270));
    }

    private void saveProperties(boolean mantain_databases) {

	new File(BIANA_CONF_PATH).mkdir();
    	try{
	    FileOutputStream of = new FileOutputStream(BianaProcessController.CONFIGURATION_FILE);
	    if( mantain_databases == false ){
		StringBuffer t = new StringBuffer();
		for( Iterator it = this.all_available_biana_databases.iterator(); it.hasNext(); ){
		    BianaDatabase pDB = (BianaDatabase)it.next();
		    t.append("|"+pDB.getDbname()+";"+pDB.getDbhost()+";"+pDB.getDbuser()+";"+pDB.getDbpass());
		}
		BianaProcessController.BIANA_PROPERTIES.setProperty("biana_databases", t.toString());
	    }
	    BianaProcessController.BIANA_PROPERTIES.store(of, "BIANA GUI PROPERTIES");
	    of.close();
	}
	catch( Exception e ){
	    e.printStackTrace();
	}
    }

    private void setEdgesAsNodes(String[] relation_types){

	this.edges_as_nodes.clear();
	for( int i=0; i<relation_types.length; i++ ){
	    this.edges_as_nodes.add(relation_types[i]);
	}

	//Save it
	StringBuffer t = new StringBuffer();
	for( Iterator it = this.edges_as_nodes.iterator(); it.hasNext(); ){
	    t.append(it.next()+";");
	}
	BianaProcessController.BIANA_PROPERTIES.setProperty("edges_as_nodes", t.toString());
	this.saveProperties(true);

    }

    
    private SecretKeySpec getKey()  { 
        byte[] keyBytes = new byte[] { 0x10, 0x18, 0x02, 0x32, 0x04, 0x05, 0x06, 0x27, 0x08, 0x59,
                0x0a, 0x41, 0x2c, 0x00, 0x0e, 0x7f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x55, 0x16, 0x17 };
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        return key;
    }

    private String encrypt(String clearText) throws java.security.NoSuchAlgorithmException, java.security.InvalidKeyException, javax.crypto.NoSuchPaddingException, javax.crypto.IllegalBlockSizeException, javax.crypto.BadPaddingException {
       Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
       cipher.init(Cipher.ENCRYPT_MODE, getKey());
       return cipher.doFinal(clearText.getBytes()).toString();
    }

    private String decrypt(String cipherText) throws java.security.NoSuchAlgorithmException, java.security.InvalidKeyException, javax.crypto.NoSuchPaddingException, javax.crypto.IllegalBlockSizeException,javax.crypto.BadPaddingException {
       Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
       cipher.init(Cipher.DECRYPT_MODE, getKey());
       return cipher.doFinal(cipherText.getBytes()).toString(); 
    }

    public void close() {
    	
		try {
		    this.streamToInterpreter.write("exit()\n".getBytes());
		} catch (IOException e) {
		    e.printStackTrace();
		}
		this.beritSocket.remove_listener(this);
		
		this.saveProperties(false);

		System.exit(0);
	
		return;
    }
	
    //public void add_command(String command, boolean show){ this.add_command(command, show, false ); }

    public void add_command(String command, boolean show) {
	System.err.println("Command: "+command);
	try {
	    /*
	    if( wait ){
		while(isCommandCompleted == false) {
		    Thread.sleep(100);
		    System.err.println("Waiting for: "+command);
		}
	    }
	    */
	    this.streamToInterpreter.write(command.getBytes());
	    this.streamToInterpreter.write(10);
	    if( show ){
		this.shell.add_command(command);
		this.shell.add_to_history(command);  //Only showed commands are saved into history
		//this.streamToShell.write(command.getBytes());
		//this.streamToShell.write(10);
	    }
	    isCommandCompleted = false;
	    //this.shell.add_to_history(command); // Only showed commands are saved into history
	} catch (IOException e) {
	    e.printStackTrace();
	} /*
	catch (InterruptedException e) {
	    e.printStackTrace();
	}*/
    }

    public void set_command_completed() {
	//System.err.println("setting command completed\n");
	isCommandCompleted = true;
    }


    // UNIMPLEMENTED CODES
    public void add_edge(String pSetName, String node1, String node2, String type, String relation_id)  {}
    public void add_edges(String pSetName, ArrayList<String[]> edges_types_and_id) {}
    public void add_node(String pSetName, String node, String type)  {}
    public void add_nodes(String pSetName, ArrayList<String[]> nodes_types)  {}


    /* BIANA LISTENER METHODS */
    public void processing_message(String message) {
	
	if( this.session != null ){
	    this.session.getSessionTree().disable();
	}
        //if( false && this.getCytoscapePlugin() != null) {
        //     this.getCytoscapePlugin().initiateProcessingMessage(this.parentFrame, message);   
        //} else {
	if( this.processingMessageDialog == null ){
	    this.processingMessageDialog = new ProcessingMessageDialog(this, this.parentFrame, message);
	    this.processingMessageDialog.setVisible(true);
	}
	else{
	    this.processingMessageDialog.setMessage(message);
	}
	//System.err.println("Added message "+message+" to Stack");
	this.processingMessagesStack.push(message);
	new_message_added=true;
    }

    public void end_processing() {
	if( new_message_added ){
	    this.processingMessagesStack.pop();
	}
	new_message_added=false;
	if( this.processingMessageDialog != null ){
	    if( this.processingMessagesStack.empty() == false ){
		this.processingMessageDialog.setMessage(this.processingMessagesStack.pop());
	    }
	    else{
		this.processingMessageDialog.dispose();
		this.processingMessageDialog = null;
		if( this.session != null ){
		    this.session.getSessionTree().enable();
		}
	    }
	}
    }

    private BianaDatabase getBianaDatabase(String dbname, String dbhost) {
    	BianaDatabase c = new BianaDatabase(dbname,"","",dbhost,"",null,null,null);
    	for( Iterator it = this.available_biana_databases.iterator(); it.hasNext(); ){
    		BianaDatabase t = (BianaDatabase)it.next();
    		if( c.equals(t) ){	return t;   }
    	}
    	return null;
    }

    public void selectUserEntitySet(Collection pNameSet) {
	if( this.session != null ){
	    //System.err.println("Starting method "+pNameSet);
	    boolean sendToCytoscape = pNameSet.size()<=1;
	    //System.err.println("Sending to Cytoscape: "+sendToCytoscape);
	    for( Iterator it = pNameSet.iterator(); it.hasNext(); ){
		String t = (String)it.next();
		System.err.println("Going to select "+t);
		//this.select_user_entity_set(t);
		//if( this.session.isUserEntitySetSelected(t) == false ){
		//System.err.println("...");
		this.session.selectUserEntitySet(t);
		if( sendToCytoscape ){
		    this.cytoscapePlugin.select_user_entity_set(t);
		}
		//}
	    }
	}
    }

    /**
     It adds a new user entity set, and selects it. It unselects all the rest
     */
    public void new_user_entity_set(String pName) {
	if( this.session != null ){
	    this.session.addUserEntitySet(pName);
	    this.session.unselectAllUserEntitySet();
	    this.session.selectUserEntitySet(pName);
	}
    }

    public void end_user_entity_set_data() {}

    public boolean isUserEntitySetSelected(String pName) {
	if( this.session != null ){
	    return this.session.isUserEntitySetSelected(pName);
	}
	return false;
    }

    public void select_user_entity_set(String pName) {
	if( this.session != null ) {
	    if( this.session.isUserEntitySetSelected(pName) ){
		return;
	    }
	    System.err.println("Going to select user entity set: "+pName);
	    this.session.selectUserEntitySet(pName);
	    if( this.cytoscapePlugin != null ){
		this.cytoscapePlugin.select_user_entity_set(pName);
	    }
	}
    }
	
    public void unselect_user_entity_set(String pName)  {
	System.err.println("Unselecting user entity set "+pName+" from BianaProcessController");
	if( this.session != null ){
	    this.session.unselectUserEntitySet(pName);
	}
    }

    public void unselect_all_user_entity_sets() {
	System.err.println("Asking BianaProcessController for unselect all user entity sets");
	if( session != null ){
	    this.session.unselectAllUserEntitySet();
	}
    }

    public void select_user_entity(String pSetName, String node) {}
    public void select_user_entity(String pSetName, String[] nodes) {}
    public void remove_user_entities(String pSetName, String[] node) {}
    public void remove_user_entity_relations(String pSetName, String[][] edges) {}
    public void select_user_entity_relation(String pSetName, String node1, String node2, String type) {}
    public void select_user_entity_relation(String pSetName, String[][] edges) {}
    public void unselect_user_entities(String pSetName) {}
    public void unselect_user_entity_relations(String pSetName) {}
    public void biana_ping_response() {}

    public void addNodesToGroup(String pSetName, String pGroupName, String pGroupType, String pGroupID, String pParentGroupID, String pParentGroupName, String[] nodes) {
	this.session.createGroup(pSetName,pGroupName,pGroupType,pGroupID,pParentGroupID, pParentGroupName);
    }

    public void select_user_entity(String pSetName, Collection subset_selection, boolean clear_previous_selection, String subset_selection_type) {
	if( this.session == null ){
	    return;
	}
	System.err.println("I should select user entities from "+pSetName+"... I am in process controller");
	String clearp = "";
	if( clear_previous_selection ){
	    clearp = ", clear_previous_selection = True";
	}

	for( Iterator it = subset_selection.iterator(); it.hasNext(); ){
	    String t = (String)it.next();
	    if( subset_selection_type.equals("level") ){
		if( t.startsWith("Level ") ){
		    System.err.println(t);
		    Pattern pattern = Pattern.compile("Level\\s+(\\d+)");
		    Matcher matcher = pattern.matcher(t);
		    if( matcher.find() ) {
			this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].select_user_entities_from_user_entity_set( user_entity_set_id = \""+pSetName+"\", user_entity_id_list =  available_sessions[\""+this.session.getID()+"\"].get_user_entity_set( user_entity_set_id=\""+pSetName+"\").get_user_entity_ids( level = "+matcher.group(1)+" )"+clearp+" )",true);
		    }
		    else{
			System.err.println("ERROR while trying to select nodes by level... Regular expression not matched");
		    }		
		}
		else{
		    System.err.println("How can be of level type and node tree does not start with \"Level \"?");
		}
	    }
	    else if( subset_selection_type.equals("tag") ){
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].select_user_entities_from_user_entity_set( user_entity_set_id = \""+pSetName+"\", user_entity_id_list =  available_sessions[\""+this.session.getID()+"\"].get_user_entity_set( user_entity_set_id=\""+pSetName+"\").get_user_entities_for_tag( tag = \""+t+"\" )"+clearp+" )",true);
	    }
	    else if( subset_selection_type.equals("group") ){
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].select_user_entities_from_user_entity_set( user_entity_set_id = \""+pSetName+"\", user_entity_id_list =  available_sessions[\""+this.session.getID()+"\"].get_user_entity_set( user_entity_set_id=\""+pSetName+"\").get_group_user_entities( group_id = "+t+" )"+clearp+" )",true);
	    }
	    //only clear previous selection in the first step
	    clearp = ", clear_previous_selection = False";
	}
    }

    public void select_user_entity_relations(String pSetName, Collection subset_selection, boolean clear_previous_selection) {
	if( this.session == null ){
	    return;
	}
	String clearp = "";
	if( clear_previous_selection ){
	    clearp = ", clear_previous_selection = True";
	}
	for( Iterator it = subset_selection.iterator(); it.hasNext(); ){
	    String t = (String)it.next();
	    //this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].select_user_entity_relations_from_user_entity_set( user_entity_set_id = \""+pSetName+"\", user_entity_relation_id_list =  available_sessions[\""+this.session.getID()+"\"].get_user_entity_set( user_entity_set_id=\""+pSetName+"\").get_user_entity_relations_for_tag( tag = \""+t+"\" )"+clearp+" )",true);
	    this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].select_user_entity_relations_from_user_entity_set( user_entity_set_id = \""+pSetName+"\", external_entity_relation_ids_list =  available_sessions[\""+this.session.getID()+"\"].get_user_entity_set( user_entity_set_id=\""+pSetName+"\").get_external_entity_relations_for_tag( tag = \""+t+"\" )"+clearp+" )",true);
	    clearp = ", clear_previous_selection = False";
	}
    }

    public void destroy_user_entity_set(String pName) {
	System.err.println("Destroying "+pName+" from BianaProcessController...");
	if( this.session != null ) {
	    this.session.removeUserEntitySet(pName);
	    //this.add_command("available_sessions[\""+this.session.getID()+"\"].remove_user_entity_set( user_entity_set_id = \""+pName+"\")",true);
	}
    }
    
    public void update_network(String pSetName, int levels) {
	if( this.session != null ){
	    this.session.setHasNetwork(pSetName);
	    System.err.println("Updating network depth from process controller for set "+pSetName);
	    this.session.setNerworkDepth(pSetName, levels);
	}
    }

    public void addTag(String pSetName, String tagName, String tagType) {
	if( this.session != null ){
	    this.session.addTag(pSetName, tagName, tagType);
	}
    }
    
    public JFrame getParentFrame()  {
    	return parentFrame;
    }

    public String get_next_uEset_name() {
	//First, if it is the first created set, check if it contains created User_Entity_Set_ sets
	if( this.next_user_entity_set_id==1 ){
	    Pattern pattern = Pattern.compile("User_Entity_Set_(\\d+)");
	    Matcher matcher;
	    for( Iterator it = this.session.getUserEntitySetsIterator(); it.hasNext(); ){
		matcher = pattern.matcher((String)it.next());
		if( matcher.find() ){
		    if( this.next_user_entity_set_id <= new Integer(matcher.group(1)) ){
			this.next_user_entity_set_id = new Integer(matcher.group(1))+1;
		    }
		}
	    }
	}
    	String value = "User_Entity_Set_"+this.next_user_entity_set_id;
    	this.next_user_entity_set_id += 1;
    	return value;
    }
    
    private HashSet<String> get_all_selected_uEs() {
	if( this.session != null ) {
	    return this.session.getSelectedUserEntitySets();
	}
	return null;
    }

    /*public void add_unification_protocol_atom(String eDbA, String eDbB, String attributes){
        //String[] str_arr = {eDbA, eDbB, attributes};
	this.unification_protocol_atoms.add(new String[] {eDbA, eDbB, attributes});//str_arr);
    }*/

    public void add_available_parser(String[] attributes) {
	this.available_parsers.add(attributes);
    }

    public void add_available_external_entity_type(String name){
	this.available_external_entity_types.add(name);
    }

    public void add_available_external_entity_attribute(String name){
	//System.err.println("Adding available external entity attribute");
	this.available_external_entity_attributes.add(name);
    }

    public void add_available_external_entity_relation_type(String name){
	this.available_external_entity_relation_types.add(name);
    }

    private Vector<String[]> get_available_parsers(){
	return this.available_parsers;
    }

    private Vector<String> get_available_default_attributes(){
	return this.available_external_entity_attributes;
    }

    private void add_available_biana_databases(BianaDatabase pDB) {
	if( this.available_biana_databases.contains(pDB) ){
	    System.err.println("Removing previously "+pDB);
	    this.available_biana_databases.remove(pDB); // Remove it if it existed previously
	}
    	this.available_biana_databases.add(pDB);
    	if( !this.all_available_biana_databases.contains(pDB)){
	    this.all_available_biana_databases.add(pDB);
	    BianaProcessController.BIANA_PROPERTIES.setProperty("biana_databases", BianaProcessController.BIANA_PROPERTIES.getProperty("biana_databases").concat("|"+pDB.getDbname()+";"+pDB.getDbhost()+";"+pDB.getDbuser()+";"+pDB.getDbpass()));
    	}
    	this.saveProperties(false);
    }
    
    public BianaDatabase[] get_available_biana_databases() {
    	BianaDatabase[] test = new BianaDatabase[this.available_biana_databases.size()];
    	this.available_biana_databases.toArray(test);
    	return test;
    }
	
    
    /*
      Loads information of all available databases
      It clears previous information
     */
    private void load_available_databases() {
	//this.processing_message("Loading information about BIANA Databases. Please wait...");
	this.all_available_biana_databases.clear();
    	String t = (String)BianaProcessController.BIANA_PROPERTIES.get("biana_databases");
    	if( t!= null ){
	    	String[] biana_databases = t.split("\\|",-1);
	    	for( int i=0; i<biana_databases.length; i++ ){
	    		String[] bdb_param = biana_databases[i].split(";",-1);
	    		if( bdb_param.length == 4 ){
			    System.err.println("administration.check_database(dbname = \""+bdb_param[0]+"\", dbhost = \""+bdb_param[1]+"\", dbuser = \""+bdb_param[2]+"\", dbpassword = \""+bdb_param[3]+"\")");
			    this.add_command("administration.check_database(dbname = \""+bdb_param[0]+"\", dbhost = \""+bdb_param[1]+"\", dbuser = \""+bdb_param[2]+"\", dbpassword = \""+bdb_param[3]+"\")",false);
			    this.all_available_biana_databases.add(new BianaDatabase(bdb_param[0],bdb_param[2],bdb_param[3],bdb_param[1],null,null,null,null));
	    		}
	    	}
    	}
    	else{
    		BianaProcessController.BIANA_PROPERTIES.setProperty("biana_databases","");
    	}
	//this.end_processing();
    }

    private void load_edges_as_nodes(){
	this.edges_as_nodes.clear();
	String t = (String)BianaProcessController.BIANA_PROPERTIES.get("edges_as_nodes");
	if( t!= null ){
	    	String[] temp = t.split(";",-1);
	    	for( int i=0; i<temp.length; i++ ){
		    this.edges_as_nodes.add(temp[i]);
	    	}
    	}
    	else{
    		BianaProcessController.BIANA_PROPERTIES.setProperty("edges_as_nodes","");		
    	}
    }

    public void remove_selected_user_entities() {
	this.add_command( "available_sessions[\""+this.session.getID()+"\"].remove_selected_user_entities(user_entity_set_id=\""+this.getBianaSession().getSelectedUserEntitySet()+"\")",true);
    }

    public void remove_selected_user_entity_relations() {
	this.add_command( "available_sessions[\""+this.session.getID()+"\"].remove_selected_relations(user_entity_set_id=\""+this.getBianaSession().getSelectedUserEntitySet()+"\")",true);
    }
    
    public void new_session(String dbname, String dbhost, String pUnificationProtocol, String pSessionID, String pSessionDescription) {
        String tempName, tempHost, dbuser="", dbpass="";
        for( Iterator it = this.all_available_biana_databases.iterator(); it.hasNext(); ){
            BianaDatabase pDB = (BianaDatabase)it.next();
            tempName = pDB.getDbname();
            tempHost = pDB.getDbhost();
            if(tempName.equals(dbname) && tempHost.equals(dbhost)) {
                dbuser = pDB.getDbuser();
                dbpass = pDB.getDbpass();
            }
        }
        //this.add_command("administration.check_database(dbname = \""+dbname+"\", dbhost = \""+dbhost+"\", dbuser = \""+dbuser+"\", dbpassword = \""+dbpass+"\")",false);
    	this.session = new BianaSession(this, pSessionID,this.getBianaDatabase(dbname,dbhost),pUnificationProtocol);
    	this.getJSessionPanel().removeAll();
    	this.getJSessionPanel().add("BIANA Working Sets",new JScrollPane(this.getSessionTree()));
    }

    public void close_session(String pSessionID) {
	this.session = null;
    }


    public void new_biana_database(String dbname, String dbhost,String dbuser, String dbpass, Vector<String> pAvailableUnificationProtocols, Vector<ExternalDatabase> pExternalDatabases, Hashtable<String, String> pAttributeOntologies) {
	//System.err.println("Adding new biana database from biana process controller");
    	this.add_available_biana_databases(new BianaDatabase(dbname,dbuser,dbpass,dbhost,"description",pAvailableUnificationProtocols,pExternalDatabases, pAttributeOntologies));
    }

    public void not_available_biana_database(String dbname, String dbhost ) {

	//Not ask for missing database, when it is available again, it will show it
	/*
	Object[] options = {"Delete it from available databases",
			    "Skip"};
	int i = JOptionPane.showOptionDialog(this.getParentFrame(),
					     "Database "+dbname+" at "+dbhost+" is not available, what do you want to do?",
					     "BIANA database error",
					     JOptionPane.YES_NO_OPTION,
					     JOptionPane.ERROR_MESSAGE,
					     null,
					     options,
					     options[0]);
	if( i==0 ){
	    this.all_available_biana_databases.remove(new BianaDatabase(dbname,null,null,dbhost,null,null,null,null));
	    this.saveProperties(false);
	    }*/

    }

    public void delete_biana_database(String dbname, String dbhost) {
	BianaDatabase pDB = new BianaDatabase(dbname,null,null,dbhost,null,null,null,null);
	if( this.available_biana_databases.contains(pDB) ){
	    this.available_biana_databases.remove(pDB);
	    if( this.all_available_biana_databases.contains(pDB)){
    		this.all_available_biana_databases.remove(pDB);
    		BianaProcessController.BIANA_PROPERTIES.setProperty("biana_databases", BianaProcessController.BIANA_PROPERTIES.getProperty("biana_databases").concat("|"+pDB.getDbname()+";"+pDB.getDbhost()+";"+pDB.getDbuser()+";"+pDB.getDbpass()));
	    }
	    this.saveProperties(false);
	}
    }
    
    public void error_message(String pMessageSummary, String errorMessage) {
    	new BianaErrorDialog(this.getParentFrame(), pMessageSummary, errorMessage);
	this.end_processing();
    }

    public void show_info_message(String message){
	StringBuffer processed_message  = new StringBuffer();
	int MAX_CHARACTERS = 80;
	
	processed_message.append("<html><body>");

	//First, split by lines
	String[] lines = message.split("\\n");

	    
	for( int line_index = 0; line_index<lines.length; line_index++ ){
	    String[] words = lines[line_index].split("\\s");
	    int accumulated = 0;
	    for( int i=0; i<words.length; i++ ){
		processed_message.append(words[i]);
		processed_message.append(" ");
		accumulated += words[i].length()+1;
		if( accumulated > MAX_CHARACTERS ){
		    processed_message.append("<br />");
		    accumulated = 0;
		}
	    }
	    processed_message.append("<br />");
	}
      
	processed_message.append("</body></html>");

	JOptionPane.showMessageDialog(this.getParentFrame(), 
				      //message,
				      processed_message.toString(),
				      "BIANA INFORMATION",
				      JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void show_table_details(Vector<String> columns, Vector<Vector<String>> values, Vector<String> rowIdentifiers, String title, String command){
    	new ShowTableDialog(this,title,columns,values,rowIdentifiers, command).setVisible(true);
    }

    public void newOntologyTree(String name, JTree tree) {
	if( this.session != null ){
	    this.session.addOntology(name, tree);
	}
    }

    public JTree getOntologyTree(String name) {
	
	//I HAVE TO WAIT UNTIL ONTOLOGY IS LOADED...!!!!

	if( this.session != null ){
	    JTree t = this.session.getOntology(name);
	    
	    if( t == null ){
		//this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].output_ontology( ontology_object = available_sessions[\""+this.session.getID()+"\"].get_ontology( ontology_name=\""+name+"\", root_attribute_values=[9443] ))",true);
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].output_ontology( ontology_object = available_sessions[\""+this.session.getID()+"\"].get_ontology( ontology_name=\""+name+"\" ))",true);
	    }
	    
	    return t;
	}
	return null;
    }

    //public void selectUserEntities(java.util.Set userEntityIds, boolean clear_previous){
    public void selectUserEntities(java.util.Collection userEntityIds, boolean clear_previous) {
	if( this.session != null ){
	    if( clear_previous == true ){
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].get_user_entity_set( user_entity_set_id=\""+this.getBianaSession().getSelectedUserEntitySet()+"\").clear_user_entity_selection()", true);
	    }
	    this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].get_user_entity_set( user_entity_set_id=\""+this.getBianaSession().getSelectedUserEntitySet()+"\").select_user_entities( user_entity_id_list=["+Utilities.join(userEntityIds,",")+"])", true);
	}
    }


    public void ping_biana(){
	this.add_command("ping()", false);
    }


    public void selectUserEntityRelations(java.util.Collection userEntityRelations, boolean clear_previous) {
	if( this.session != null ){
	    String clearPrevious;
	    if( clear_previous == true ){
		clearPrevious = "True";
	    }
	    else{
		clearPrevious = "False";
	    }
	    this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].select_user_entity_relations_from_user_entity_set( user_entity_set_id=\""+this.getBianaSession().getSelectedUserEntitySet()+"\", user_entity_relation_id_list = ["+Utilities.join(userEntityRelations,",")+"], clear_previous_selection="+clearPrevious+")", true);
	}
    }
	
    //public void newSetFromSelected(java.util.Collection userEntityIds){
    public void newSetFromSelected(java.util.Set userEntityIds) {
	/*
	String s = (String)JOptionPane.showInputDialog(
						       this.getParentFrame(),
						       "Specify new user entity set name:",
						       "Create a new set from existing...",
						       JOptionPane.PLAIN_MESSAGE,
						       null,
						       null,
						       this.get_next_uEset_name());	 
	if( s!= null ){
	    if( userEntityIds != null ){
		this.selectUserEntities(userEntityIds, true);
	    }

	    String include_relations_str = "False";
	    if( this.session.hasNetwork(this.session.getSelectedUserEntitySet()) ){
		int include_relations = JOptionPane.showConfirmDialog( this.getParentFrame(),
								   "Include current relations in new subset?",
								   "Create a new set from existing...",
								   JOptionPane.YES_NO_OPTION);		
		if( include_relations == JOptionPane.YES_OPTION ){
		    include_relations_str = "True";
		}
		else{
		    include_relations_str = "False";
		}
	    }

	    this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].get_sub_user_entity_set( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\", include_relations="+include_relations_str+", new_user_entity_set_id = \""+s+"\")", true);

	}
	*/
	String include_relations_str = "False";
	int include_relations = JOptionPane.showConfirmDialog( this.getParentFrame(),
							   "Include current RESTRICTIONS and RELATIONS in new subset?",
							   "Creating a new set from existing...",
							   JOptionPane.YES_NO_OPTION);		
	if( include_relations == JOptionPane.YES_OPTION ){
	    include_relations_str = "True";
	    String s = (String)JOptionPane.showInputDialog(
						       this.getParentFrame(),
						       "Specify new user entity set name:",
						       "Create a new set from existing...",
						       JOptionPane.PLAIN_MESSAGE,
						       null,
						       null,
						       this.get_next_uEset_name());	 
	    if( s!= null ){
		if( userEntityIds != null ){
		    this.selectUserEntities(userEntityIds, true);
		}
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].get_sub_user_entity_set( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\", include_relations="+include_relations_str+", new_user_entity_set_id = \""+s+"\")", true);
	    }
	}
	else{
	    include_relations_str = "False";
	    new CommandDialog(this,this.session.getID(),new CreateNewUserEntitySetPanel(ExternalDatabase.getAllAttributes("eE",this.getBianaSession().getBianaDatabase().getAvailableExternalDatabases()),this.get_next_uEset_name(), getCytoscapePlugin().getSelectedNodes(), this), "Create New User Entity Set", new Dimension(470,650));
	}
    }


    public void view_uE_details() {
	if( this.session != null ){
	    new CommandDialog(this,this.session.getID(),new ViewUserEntityDetailsPanel(this.getBianaSession().getSelectedUserEntitySet(),ExternalDatabase.getAllAttributes("eE",this.getBianaSession().getBianaDatabase().getAvailableExternalDatabases()), "selected"), "Select attributes to show", new Dimension(330, 415)); 
	}
    }


    public void close_session_dialog() {
	if( this.session != null ){
	    int confirm = JOptionPane.showConfirmDialog(this.getParentFrame(),
							"Do you want to save the session before closing it?",
							"Delete confirmation",
							JOptionPane.YES_NO_CANCEL_OPTION);
	    if( confirm == JOptionPane.YES_OPTION ){
		String input = null;
		jFileChooserSave = getJFileChooserSave();
		int returnVal = jFileChooserSave.showOpenDialog(new JPanel());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
		    File inputPath = jFileChooserSave.getSelectedFile();
		    input = inputPath.getPath();
		    //System.out.println(input);
		    this.add_command("save_session(\""+ session.getID() + "\", \"" + input+ "\")", true);
		}
	    }
	    else if( confirm == JOptionPane.CANCEL_OPTION ){
		return;
	    }
	    this.add_command("remove_session(\""+ session.getID() + "\")", true);
	}
    }

    public void tagSelectedNodes(java.util.Set userEntityIds) {
	String s = (String)JOptionPane.showInputDialog(
						       this.getParentFrame(),
						       "Specify tag name:",
						       "Tag selected nodes...",
						       JOptionPane.PLAIN_MESSAGE,
						       null,
						       null,
						       null);
	if( s!= null && s.equals("")==false ){
	    if( this.check_valid_string(s) ){
		this.selectUserEntities(userEntityIds, true);
		//this.add_command("available_sessions[\""+this.session.getID()+"\"].get_user_entity_set(user_entity_set_id=\""+this.getBianaSession().getSelectedUserEntitySet()+"\").addTagToSelectedUE(tag=\""+s+"\")", true);
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].tag_selected_user_entities( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\", tag = \""+s+"\")",true);
	    }
	}
    }

    public void tagSelectedEdges(java.util.Set userEntityRelationIds) {
	String s = (String)JOptionPane.showInputDialog(
						       this.getParentFrame(),
						       "Specify tag name:",
						       "Tag selected edges...",
						       JOptionPane.PLAIN_MESSAGE,
						       null,
						       null,
						       null);
	if( s!= null && s.equals("")==false ){
	    if( this.check_valid_string(s) ){
		this.selectUserEntityRelations(userEntityRelationIds, true);
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].tag_selected_user_entity_relations( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\", tag = \""+s+"\")",true);
	    }
	}
    }


    //Checks if a string is valid for communicating with biana process (it checks it does not contain reserved characters (",')
    private boolean check_valid_string(String s) {
	if( s.indexOf("\"")>=0 || s.indexOf("'")>=0 ){
	    JOptionPane.showMessageDialog(this.getParentFrame(), 
					  "There is not any BIANA database available. You must first add a new available BIANA database",
					  "BIANA ERROR",
					  JOptionPane.ERROR_MESSAGE);
	    return false;
	}
	return true;
    }


    /* ACTIONS */
    public void actionPerformed(ActionEvent e)  {

	if (this.enabled == false ){
	    System.err.println("BianaProcessController is disabled. Cannot perform any action");
	    return;
	}

	if ("new_uEs".equals(e.getActionCommand())) {
	    new CommandDialog(this,this.session.getID(),new CreateNewUserEntitySetPanel(ExternalDatabase.getAllAttributes("eE",this.getBianaSession().getBianaDatabase().getAvailableExternalDatabases()),this.get_next_uEset_name(), null, this), "Create New User Entity Set", new Dimension(470,650));
	}
	else if( "select_all_uEs".equals(e.getActionCommand())){ 
	    this.session.selectAllUserEntitySet();
	}
	else if( "unselect_all_uEs".equals(e.getActionCommand())){ 
	    this.session.unselectAllUserEntitySet();
	}
	else if( "create_network".equals(e.getActionCommand()) || "expand_network".equals(e.getActionCommand()) ){
	    if( this.session.hasNetwork(this.session.getSelectedUserEntitySet()) ){
		this.add_command( "temp = available_sessions[\""+this.session.getID()+"\"].expand_user_entity_set( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\")", true );
	    }
	    else{
		new CommandDialog(this, this.session.getID(), new CreateNetworkPanel(this.session.getSelectedUserEntitySet(),ExternalDatabase.getAlleErTypes(this.getBianaSession().getBianaDatabase().getAvailableExternalDatabases()),ExternalDatabase.getAllAttributes("relation",this.getBianaSession().getBianaDatabase().getAvailableExternalDatabases()),this.session.getBianaDatabase()),"Create relation network",new Dimension(400,350));
	    }
	}
	else if( "duplicate_user_entity_set".equals(e.getActionCommand()) ){
	    String s = (String)JOptionPane.showInputDialog(
							   this.getParentFrame(),
							   "Specify new user entity set name:",
							   "Duplicate set...",
							   JOptionPane.PLAIN_MESSAGE,
							   null,
							   null,
							   null);	 
	    if( s!= null ){
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].duplicate_user_entity_set( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\", new_user_entity_set_id = \""+s+"\")", true);
	    }
	}
	else if( "select_user_entities_by".equals(e.getActionCommand()) ){
	    //Object[] possibilities = {"From existing user entity set", "By some attributes..."};
	    //Object[] possibilities = {"By some attributes", "By tag / tag linkage"};
	    //Object[] possibilities = new Object[tags.size()];
	    //possibilities[0] = "By some attributes";
	    Vector<String> tags = this.getBianaSession().getTags(this.getBianaSession().getSelectedUserEntitySet(), "nodeTag");
	    Vector<String> possibilities = new Vector<String>();
	    possibilities.add("By some attributes (asks for the attributes)");
	    for(Iterator it = tags.iterator(); it.hasNext(); ) {
		String tag = it.next().toString();
		possibilities.add("By tag: " + tag);
		possibilities.add("By tag linkage (nodes connecting at least 2 tagged nodes): " + tag);
	    }
	    String s = (String)JOptionPane.showInputDialog(
							   this.getParentFrame(),
							   "Select an option of selection:",
							   "Selecting user entities by attributes / tags...",
							   JOptionPane.PLAIN_MESSAGE,
							   null,
							   possibilities.toArray(),
							   //"From existing user entity set");
							   "By some attributes (asks for the attributes)");
	    if( s!=null ){
		/*
		if( s.equals("From existing user entity set") ){
		    Object[] uEs = this.session.getUserEntitySets().toArray();
		    String t = (String)JOptionPane.showInputDialog(
								   this.getParentFrame(),
								   "Select User Entity Set",
								   "Selecting user entities by existing user entity set",
								   JOptionPane.PLAIN_MESSAGE,
								   null,
								   uEs,
								   "From existing user entity set");
		    if( t!=null ){
			this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].select_user_entities_from_user_entity_set( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\", user_entity_id_list =  available_sessions[\""+this.session.getID()+"\"].get_user_entity_set( user_entity_set_id=\""+t+"\").get_user_entity_ids(), clear_previous_selection = True)",true ) ;
		    }
		}
		else if( s.equals("By some attributes...") ){
		*/
		if( s.equals("By some attributes (asks for the attributes)") ){
		    new CommandDialog(this,this.session.getID(),new SelectUserEntityAttributesSelection(ExternalDatabase.getAllAttributes("eE",this.getBianaSession().getBianaDatabase().getAvailableExternalDatabases()),this.getBianaSession().getSelectedUserEntitySet(),this), "Select user entities by attributes...", new Dimension(470,650));
		}
		else {
		    for(Iterator it = tags.iterator(); it.hasNext(); ) {
			String tag = it.next().toString();
			if(s.equals("By tag: " + tag))
			    this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].select_user_entities_from_user_entity_set( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\", user_entity_id_list = available_sessions[\""+this.session.getID()+"\"].get_user_entity_set( user_entity_set_id=\""+this.getBianaSession().getSelectedUserEntitySet()+"\").get_user_entities_for_tag(\"" + tag + "\"), clear_previous_selection = True)",true ) ;
			else if(s.equals("By tag linkage (nodes connecting at least 2 tagged nodes): " + tag))
			    this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].select_user_entities_from_user_entity_set( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\", user_entity_id_list =  available_sessions[\""+this.session.getID()+"\"].get_user_entity_set( user_entity_set_id=\""+this.getBianaSession().getSelectedUserEntitySet()+"\").get_user_entity_ids_by_tag_linker_degree_cutoff(\"" + tag + "\", 2), clear_previous_selection = True)",true ) ;
		    }
		}
	    }
	}
	else if( "select_user_entities_from".equals(e.getActionCommand()) ){
	    Object[] uEs = this.session.getUserEntitySets().toArray();
	    String t = (String)JOptionPane.showInputDialog(
							   this.getParentFrame(),
							   "Select User Entity Set",
							   "Selecting user entities by existing user entity set",
							   JOptionPane.PLAIN_MESSAGE,
							   null,
							   uEs,
							   "From existing user entity set");
	    if( t!=null ){
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].select_user_entities_from_user_entity_set( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\", user_entity_id_list =  available_sessions[\""+this.session.getID()+"\"].get_user_entity_set( user_entity_set_id=\""+t+"\").get_user_entity_ids(), clear_previous_selection = True)",true ) ;
	    }
	}
	//else if( "select_relations_by".equals(e.getActionCommand()) ){
	//    /* TODO NEW WINDOW FOR NETWORK ATTRIBUTE SELECTION */
	//}
	else if( "select_all_user_entities".equals(e.getActionCommand()) ){
	    this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].select_all_user_entities( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\" )",true);
	}
	else if( "unselect_user_entities".equals(e.getActionCommand()) ){
	    this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].unselect_user_entities_from_user_entity_set( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\" )",true);
	}
	else if( "destroy_user_entity_set".equals(e.getActionCommand())){
	    int confirm = JOptionPane.showConfirmDialog(this.getParentFrame(),
							"Are you sure you want to delete selected user entity sets?",
							"Delete confirmation",
							JOptionPane.YES_NO_OPTION);
	    if( confirm == JOptionPane.YES_OPTION ){
	    	//this.session.removeSelectedUserEntitySets();
		for( Iterator it = this.session.getSelectedUserEntitySetsIterator(); it.hasNext(); ){
		    this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].remove_user_entity_set( user_entity_set_id = \""+it.next()+"\")",true);
		}
	    }
	}
	else if( "union_uEs".equals(e.getActionCommand())){
	    String s = (String)JOptionPane.showInputDialog(
							   this.getParentFrame(),
							   "Specify new user entity set name:",
							   "Union between sets...",
							   JOptionPane.PLAIN_MESSAGE,
							   null,
							   null,
							   Utilities.join(this.get_all_selected_uEs(),"_")+"_union");	 
	    if( s!= null ){
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].get_union_of_user_entity_set_list( user_entity_set_list = [\""+Utilities.join(this.get_all_selected_uEs(),"\" , \"")+"\"], new_user_entity_set_id = \""+s+"\", include_relations=True)", true);
	    }
	}
	else if( "intersection_uEs".equals(e.getActionCommand())){
	    String s = (String)JOptionPane.showInputDialog(
							   this.getParentFrame(),
							   "Specify new user entity set name:",
							   "Intersection between sets...",
							   JOptionPane.PLAIN_MESSAGE,
							   null,
							   null,
							   Utilities.join(this.get_all_selected_uEs(),"_")+"_intersection");
	    if( s!= null ){
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].get_intersection_of_user_entity_set_list( user_entity_set_list = [\""+Utilities.join(this.get_all_selected_uEs(),"\" , \"")+"\"], new_user_entity_set_id = \""+s+"\", include_relations=True)", true);
	    }
	}
	else if( "new_session".equals(e.getActionCommand())){
	    if( this.available_biana_databases.size() > 0 ){
	    	this.showNewSessionDialog();
		
	
	    	//new CommandDialog(this, new NewSessionPanel(this.get_available_biana_databases(),"biana_session"),"Start new Biana Working session",new Dimension(400,260));
	    }
	    else{
		JOptionPane.showMessageDialog(this.getParentFrame(), 
					      "There is not any BIANA database available. You must first add a new available BIANA database",
					      "BIANA ERROR",
					      JOptionPane.ERROR_MESSAGE);
	    }
	}
	else if( "save_session".equals(e.getActionCommand())){
		String input = null;
		jFileChooserSave = getJFileChooserSave();
		int returnVal = jFileChooserSave.showOpenDialog(new JPanel());
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
    		File inputPath = jFileChooserSave.getSelectedFile();
    		input = inputPath.getPath();
    		//System.out.println(input);
    		this.add_command("save_session(\""+ session.getID() + "\", \"" + input+ "\")", true);
    	}
	}
	else if( "close_session".equals(e.getActionCommand() )){
	    this.close_session_dialog();
	}
	else if( "delete_databases".equals((e.getActionCommand())) ){
	    new CommandDialog(this, new DeleteBianaDatabasePanel(this.get_available_biana_databases()),"Select BIANA databases to delete",new Dimension(330,270));
	}
	else if( "create_new_biana_database".equals(e.getActionCommand())){
	    this.showNewBianaDatabaseDialog();	    
	}
	else if( "add_biana_database".equals(e.getActionCommand())){
		this.showAddBianaDatabaseDialog();
	}
	else if( "new_set_from_subset".equals(e.getActionCommand()) ){
	    this.newSetFromSelected(null);
	}
	else if ( "view_details".equals(e.getActionCommand())){
	    new CommandDialog(this,this.session.getID(),new ViewUserEntityDetailsPanel(this.getBianaSession().getSelectedUserEntitySet(),ExternalDatabase.getAllAttributes("eE",this.getBianaSession().getBianaDatabase().getAvailableExternalDatabases()), "all"), "Select attributes to view/export", new Dimension(330,415)); 
	}
	else if( "view_uE_details".equals(e.getActionCommand())){
	    this.view_uE_details();
	}
	else if( "view_network_details".equals(e.getActionCommand())){
	    new CommandDialog(this,this.session.getID(),new ViewUserEntityNetworkDetailsPanel(this.getBianaSession().getSelectedUserEntitySet(), ExternalDatabase.getAllAttributes("eE",this.getBianaSession().getBianaDatabase().getAvailableExternalDatabases()), ExternalDatabase.getAllAttributes("relation",this.getBianaSession().getBianaDatabase().getAvailableExternalDatabases()),"all"), "Select attributes to view/export", new Dimension(330,585)); //(330, 400));
	}
	else if( "view_group_details".equals(e.getActionCommand())){
	    this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].output_user_entity_set_group(user_entity_set_id=\""+this.getBianaSession().getSelectedUserEntitySet()+"\", group_ids=["+Utilities.join(this.getBianaSession().getSessionTree().getSelectedGroups(),",")+"] )", true);
	}
        /*
	else if( "create_randomized_network".equals(e.getActionCommand()) ){
	    String s = (String)JOptionPane.showInputDialog(
							   this.getParentFrame(),
							   "Specify new user entity set name:",
							   "Randomize Network...",
							   JOptionPane.PLAIN_MESSAGE,
							   null,
							   null,
							   null);	 
	    if( s!= null ){
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].create_randomized_user_entity_set( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\", new_user_entity_set_id = \""+s+"\", type_randomization = " + "\"random\"" + ")", true);
	    }
	}
        */
	else if( "randomize_network".equals(e.getActionCommand()) ){
            Object [] possibleValues = { "Random (Redistribute Edges)", "Preserve Topology (Shuffle Nodes)", "Preserve Degree Distribution", "Preserve Individual Node Degrees", "Preserve Both Topology And Individual Node Degrees", "Erdos Renyi Model", "Barabasi Albert Model"};
	    String s = (String)JOptionPane.showInputDialog(
							   this.getParentFrame(),
							   "Specify randomization type:",
							   "Randomize network",
							   JOptionPane.PLAIN_MESSAGE,
							   null,
							   possibleValues,
							   possibleValues[0]);	 
	    if( s!= null ){
                if(s == "Random (Redistribute Edges)") {
                    s = "random";
                } else if(s == "Preserve Degree Distribution") {
                    s = "preserve_degree_distribution";
                } else if(s == "Preserve Topology (Shuffle Nodes)") {
                    s = "preserve_topology";
                } else if(s == "Preserve Individual Node Degrees") {
                    s = "preserve_degree_distribution_and_node_degree";
                } else if(s == "Preserve Both Topology And Individual Node Degrees") {
                    s = "preserve_topology_and_node_degree";
                } else if(s == "Erdos Renyi Model") {
                    s = "erdos_renyi";
                } else if(s == "Barabasi Albert Model") {
                    s = "barabasi_albert";
                } 
		this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].randomize_user_entity_set_network( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\", type_randomization = \"" + s + "\")", true);
	    }
	}
	else if( "tag_selected_nodes".equals(e.getActionCommand())){
	    //TODO (call tagSelectedNodes method)
	}
	else if ( "delete_unification_protocol".equals(e.getActionCommand() )){
	    if( this.available_biana_databases.size() > 0 ){
		if( this.getBianaSession() == null ){
		    new CommandDialog(this, new DeleteUnificationProtocolPanel(this.get_available_biana_databases(), null, null), "Delete Unification Protocols", new Dimension(420,270));
		}
		else{
		    new CommandDialog(this, new DeleteUnificationProtocolPanel(this.get_available_biana_databases(), this.getBianaSession().getBianaDatabase(), this.getBianaSession().getUnificationProtocol()), "Delete Unification Protocols", new Dimension(400,270));
		}
	    }
	    else{
		JOptionPane.showMessageDialog(this.getParentFrame(), 
					      "There is not any BIANA database available. You must first add a new available BIANA databse",
					      "BIANA ERROR",
					      JOptionPane.ERROR_MESSAGE);
	    }
	}
	else if( "new_unification_protocol".equals(e.getActionCommand() )){
	    if( this.available_biana_databases.size() > 0 ){
		new CommandDialog(this, new NewUnificationProtocolPanel(this.get_available_biana_databases()), "Create New Unification Protocol", new Dimension(510,420));
	    }
	    else{
		JOptionPane.showMessageDialog(this.getParentFrame(), 
					      "There is not any BIANA database available. You must first add a new available BIANA databse",
					      "BIANA ERROR",
					      JOptionPane.ERROR_MESSAGE);
	    }
	}
	else if( "parse_external_databases".equals(e.getActionCommand() )){
	    if( this.available_biana_databases.size() > 0 ){
		if( this.parseDatabasesDialog == null ){
		    this.parseDatabasesDialog = new JDialog(this.getParentFrame());
		    this.parseDatabasesDialog.setSize(new Dimension(600,500));
		    this.parseDatabasesDialog.setContentPane(new DatabaseAdministratorPane(this.get_available_biana_databases(), this.get_available_parsers(),  this.get_available_default_attributes()));
		}
		this.parseDatabasesDialog.setVisible(true);
		/*JFrame f = new JFrame();
		f.setContentPane(new DatabaseAdministratorPane(this.get_available_biana_databases(), this.get_available_parsers(),  this.get_available_default_attributes()));
		f.setSize(new Dimension(600,500));
		f.setVisible(true);*/
	    }
	    else{
		JOptionPane.showMessageDialog(this.getParentFrame(), 
					      "There is not any BIANA database available. You must first add a new available BIANA databse",
					      "BIANA ERROR",
					      JOptionPane.ERROR_MESSAGE);
	    }
	}
	else if( "update_available_databases".equals(e.getActionCommand() )){
	    this.load_available_databases();
	    //Resets the parse external databases panel
	    this.parseDatabasesDialog = null;
            JOptionPane.showMessageDialog(this.getParentFrame(), 
					      "Information in available BIANA Databases succesfully updated!",
					      "BIANA NOTIFICATION",
					      JOptionPane.INFORMATION_MESSAGE);
        }
	else if( "view_available_unification_protocols".equals(e.getActionCommand() )){
	    if( this.available_biana_databases.size() > 0 ){
		if( this.getBianaSession() == null ){
		    new CommandDialog(this, new ViewUnificationProtocolPanel(this.get_available_biana_databases(), null, null), "View Unification Protocols", new Dimension(330,270));
		}
		else{
		    new CommandDialog(this, new ViewUnificationProtocolPanel(this.get_available_biana_databases(), this.getBianaSession().getBianaDatabase(), this.getBianaSession().getUnificationProtocol()), "View Unification Protocols", new Dimension(330,270));
		}
	    }
	    else{
		JOptionPane.showMessageDialog(this.getParentFrame(), 
					      "There is not any BIANA database available. You must first add a new available BIANA databse",
					      "BIANA ERROR",
					      JOptionPane.ERROR_MESSAGE);
	    }
            //show_table_details(Vector<String> columns, Vector<Vector<String>> values, Vector<String> rowIdentifiers, String title, String command);
        }
	else if( "reconnect_database".equals(e.getActionCommand()) ){
	    this.add_command("available_sessions[\""+this.session.getID()+"\"].reconnect()",true);
	}
	else if( "save_commands".equals(e.getActionCommand() ) ){
	    //JFileChooser saveHistoryChooser = jFileChooserSave = new JFileChooser();
	    JFileChooser saveHistoryChooser = this.getJFileChooserSave();
	    //saveHistoryChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    saveHistoryChooser.setDialogTitle("Select output to save commands history");
	    int returnVal = jFileChooserSave.showOpenDialog(new JPanel());
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		try{
		    File outputPath = jFileChooserSave.getSelectedFile();
		    FileOutputStream of = new FileOutputStream(outputPath.getPath());
		    PrintStream p = new PrintStream(of);
		    p.println(this.shell.getHistoryText());
		    p.close();
		}
		catch(Exception exc){
		    this.error_message(exc.getMessage(),"Error saving commands history");
		}
	    }
	}
	else if( "view_preferences".equals(e.getActionCommand() ) ){
	    this.getPreferencesDialog().setVisible(true);
	}
	else if( "view_set_properties".equals(e.getActionCommand() ) ){
	    this.add_command("temp = available_sessions[\""+this.session.getID()+"\"].describe_user_entity_set( user_entity_set_id = \""+this.getBianaSession().getSelectedUserEntitySet()+"\")",true);
	}
	else if( "quit".equals(e.getActionCommand() )){
	    this.close();
	}
	else{
	    System.err.println("Action command "+e.getActionCommand()+" not recognized");
	}
    }

    public void closedConnection(String pMessage) {
	// TODO Auto-generated method stub
	this.error_message("CONNECTION TO BIANA CLOSED", pMessage);
    }


}
