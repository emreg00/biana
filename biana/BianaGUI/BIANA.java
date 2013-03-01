

import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import java.awt.event.*;
import java.awt.*;

import java.awt.Toolkit;

public class BIANA extends JFrame implements ActionListener, WindowListener {
	
    private static final long serialVersionUID = 1L;
    JDesktopPane desktop;
    private BianaProcessController controller = null;

    static String title = "BIANA: Biological Interactions And Network Analysis";
	private JSplitPane jBianaSplitPane = null;

    public BIANA() {
        super(title);
        this.initialize();
    }
       
    private void initialize(){
    	
    	
    	this.setIconImage(Toolkit.getDefaultToolkit().getImage("./img/test_icon.gif"));
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                  screenSize.width  - inset*2,
                  screenSize.height - inset*2);
        this.setSize(800, 500);
        this.addWindowListener(this);
        
        	
        /* Start a new session */
	try{
	    this.controller = new BianaProcessController(this);
	}
	catch( Exception e ){
	    System.err.println(e);
	}

        this.setContentPane(getJBianaSplitPane());
        //Set up the GUI.
        setJMenuBar(createMenuBar());

        //Make dragging a little faster but perhaps uglier.
        //desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        this.setLocationRelativeTo(null);

        //Display the window.
        setVisible(true);
        
        
    }
    
    public static void addDBAdministrationMenuItems(ActionListener listener, JComponent menu){
    	
    	//JMenu menu = new JMenu("Configuration");
        //menu.setMnemonic(KeyEvent.VK_C);
        
        JMenuItem menuItem = new JMenuItem("Create new BIANA Database");
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("create_new_biana_database");
        menuItem.addActionListener(listener);
        menuItem.setToolTipText("<html>Create a new & empty <i>BIANA Database</i> (to be populated by parsers later): <br> BIANA stores data provided from biological data sources (referred as <i>External Database</i>s) in its own data repository.<br>Typically this is the first thing to do before you start parsing <i>External Database</i>s such as UNIPROT, INTACT, etc...</html>");
        menu.add(menuItem);

	menuItem = new JMenuItem("Add existing BIANA Database");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("add_biana_database");
        menuItem.addActionListener(listener);
        menuItem.setToolTipText("<html>Add a <i>BIANA Database</i> that has already been created: <br> Add a previously created & possibly populated BIANA database<html>");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Parse External Database");
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("parse_external_databases");
        menuItem.addActionListener(listener);
        menuItem.setToolTipText("<html>Parse one or more biological data source (<i>External Database</i>) to an existing BIANA Database: <br> Inserts the data provided by a biological data source (like HGNC, NR, MINT, etc...) into <i>BIANA Database</i></html>");
        menu.add(menuItem);

        menuItem = new JMenuItem("Update available BIANA Databases");
        menuItem.setMnemonic(KeyEvent.VK_U);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_U, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("update_available_databases");
        menuItem.addActionListener(listener);
        menuItem.setToolTipText("<html>Update information of <i>BIANA Database</i>s:<br> This is required after parsing a new <i>External Database</i> to be able to display newly inserted attributes.</html>");
        menu.add(menuItem);

        menuItem = new JMenuItem("Create new Unification Protocol");
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("new_unification_protocol");
        menuItem.addActionListener(listener);
        menuItem.setToolTipText("<html>Create a new <i>Unification Protocol</i>(how biomolecules coming from <i>External Database</i>s are going to be considered equivalent):<br>Unify biomolecules wtih respect to their common properties<br> BIANA will consider entries from <i>External Database</i>s equivalent if they share specified properties.<br> <i>Unification Protocol</i> refers to this consideration of equivalence.</html>");
        menu.add(menuItem);
        
         menuItem = new JMenuItem("View available Unification Protocols");
        menuItem.setMnemonic(KeyEvent.VK_V);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_V, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("view_available_unification_protocols");
        menuItem.addActionListener(listener);
        menuItem.setToolTipText("<html>Display information of available <i>Unification Protocol</i>s in <i>BIANA Database</i>s</html>");
        menu.add(menuItem);

        menuItem = new JMenuItem("Delete Unification Protocol");
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_E, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("delete_unification_protocol");
        menuItem.addActionListener(listener);
        menuItem.setToolTipText("<html>Remove an existing <i>Unification Protocol</i><html>");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Delete BIANA Database");
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_D, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("delete_databases");
        menuItem.addActionListener(listener);
        menuItem.setToolTipText("<html>Remove an existing <i>BIANA database</i></html>");
        menu.add(menuItem);
    	
        //return menu;	
    }
    
    public static JPopupMenu getUserEntitySetPopupMenu(ActionListener listener){
		
    	JPopupMenu popUp = new JPopupMenu("Actions");
		
    	JMenuItem temp;
    	
	temp = new JMenuItem("Create/Expand Network");
	temp.setActionCommand("create_network");
	temp.addActionListener(listener);
        temp.setToolTipText("Add relations between biomolecules of this user entity set");
	popUp.add(temp);
	
	temp = new JMenuItem("Delete");
	temp.setActionCommand("destroy_user_entity_set");
	temp.addActionListener(listener);
        temp.setToolTipText("Remove this user entity set from the session");
	popUp.add(temp);
	
	temp = new JMenuItem("Duplicate");
	temp.setActionCommand("duplicate_user_entity_set");
	temp.addActionListener(listener);
        temp.setToolTipText("Create a copy of this user entity set and its relations (asks name of the new set)");
	popUp.add(temp);
	
	temp = new JMenuItem("View/Export set details");
	temp.addActionListener(listener);
	temp.setActionCommand("view_details");
        temp.setToolTipText("View/Export details of biomolecules in this set");
	popUp.add(temp);

	temp = new JMenuItem("View/Export network details");
	temp.addActionListener(listener);
	temp.setActionCommand("view_network_details");
        temp.setToolTipText("View/Export details of relations of biomolecules in this set");
	popUp.add(temp);

	temp = new JMenuItem("Randomize network");
	temp.addActionListener(listener);
	temp.setActionCommand("randomize_network");
        temp.setToolTipText("<html>Randomize the relations of biomolecules in this user entity set (asks for randomization type):<ul><li>Random: Redistributes relations over the biomolecules in the set uniformly</li><li>Preserve Topology: Shifts biomolecules within themselves without modifying relations</li><li>Preserve Degree Distribution: Redistributes relations so that degree distribution of biomolecules are preserved</li><li>Preserve Individual Node Degrees: Redistributes relations so that individual degrees of all biomolecules are preserved</li><li>Preserve Both Topology And Individual Node Degrees: Combination of <i>Preserve Topology</i> and <i>Preserve Individual Node Degrees</i> approach</li><li>Erdos Renyi Model: Redistributes edges based on Erdos-Renyi model</li><li>Barabasi Albert Model: Redistributes edges based on Barabasi-Albert model (preferential attachment)</li></ul></html>");
	popUp.add(temp);
	
	temp = new JMenuItem("Select user entities by attribute/tag");
	temp.addActionListener(listener);
	temp.setActionCommand("select_user_entities_by");
        temp.setToolTipText("Select biomolecules based on their attributes or tags");
	popUp.add(temp);

	temp = new JMenuItem("Select user entities from existing set");
	temp.addActionListener(listener);
	temp.setActionCommand("select_user_entities_from");
        temp.setToolTipText("Select biomolecules that are already contained in another set");
	popUp.add(temp);


	//temp = new JMenuItem("Select relations by...");
	//temp.addActionListener(listener);
	//temp.setActionCommand("select_relations_by");
        //temp.setToolTipText("Select relations of biomolecules based on their attributes");
	//popUp.add(temp);
	
	temp = new JMenuItem("Select all user entities");
	temp.addActionListener(listener);
	temp.setActionCommand("select_all_user_entities");
        temp.setToolTipText("Select all biomolecules in this set");
	popUp.add(temp);
	

	temp = new JMenuItem("Unselect all user entities");
	temp.addActionListener(listener);
	temp.setActionCommand("unselect_user_entities");
        temp.setToolTipText("Unselect all selected biomolecules");
	popUp.add(temp);

	temp = new JMenuItem("View Set Properties");
	temp.addActionListener(listener);
	temp.setActionCommand("view_set_properties");
        temp.setToolTipText("View the summary details of the set");
	popUp.add(temp);

	// Remove selected is not recommended for tree, because it can induce errors when using cytoscape plugin
	// because from biana process controller is not possible to get selected nodes and edges
	/*
	temp = new JMenuItem("Remove selected user entities");
	temp.addActionListener(listener);
	temp.setActionCommand("remove_selected_user_entities");
	popUp.add(temp);

	temp = new JMenuItem("Remove selected relations");
	temp.addActionListener(listener);
	temp.setActionCommand("remove_selected_user_entity_relations");
	popUp.add(temp);*/
	
	/*
	temp = new JMenuItem("Clusterize");
	temp.addActionListener(listener);
	temp.setActionCommand("clusterize_network");
	popUp.add(temp);*/
	
    	return popUp;
    }

    public static JPopupMenu getUserEntitySubSetPopupMenu(ActionListener listener){
	
	JPopupMenu popUp = new JPopupMenu("Actions");
		
	JMenuItem temp;
	
	temp = new JMenuItem("Create new user entity set with selected entities");
	temp.setActionCommand("new_set_from_subset");
	temp.addActionListener(listener);
        temp.setToolTipText("Create a new user entity set from selected biomolecules (asks name of the new set)");
	popUp.add(temp);
	
	temp = new JMenuItem("View/export entities details");
	temp.addActionListener(listener);
	temp.setActionCommand("view_uE_details");
        temp.setToolTipText("View/Export details of biomolecules in this set");
	popUp.add(temp);
	
	return popUp;
    }


    public static JPopupMenu getGroupPopupMenu(ActionListener listener){

	
	JPopupMenu popUp = getUserEntitySubSetPopupMenu(listener);

	JMenuItem temp;

	temp = new JMenuItem("View Relation Details");
	temp.setActionCommand("view_group_details");
	temp.addActionListener(listener);
        temp.setToolTipText("View Relation Details");
	popUp.add(temp);

	return popUp;

    }
    
    public static JPopupMenu getUserEntityPopupMenu(ActionListener listener){
    	
    	JPopupMenu menu = new JPopupMenu();
	JMenuItem menuItem = new JMenuItem("View/Export details");
	menuItem.addActionListener(listener);
	menuItem.setActionCommand("view_user_entity_details");
        menuItem.setToolTipText("View/Export details of biomolecules in this set");
	menu.add(menuItem);
	/*
	  menuItem = new JMenuItem("Copy to clipboard");
	  menuItem.addActionListener(listener);
	  menuItem.setActionCommand("copy");
	  menu.add(menuItem);*/
	
	return menu;
    }
    
    public static JPopupMenu getMultipleUserEntitySetPopupMenu(ActionListener listener){
    	JMenuItem temp;
    	JPopupMenu menu = new JPopupMenu();
		temp = new JMenuItem("Union");
		temp.addActionListener(listener);
		temp.setActionCommand("union_uEs");
                temp.setToolTipText("Make a union of selected user entiy sets");
		menu.add(temp);
		temp = new JMenuItem("Intersection");
		temp.addActionListener(listener);
		temp.setActionCommand("intersection_uEs");
                temp.setToolTipText("Make an intersection of selected user entiy sets");
		menu.add(temp);
		temp = new JMenuItem("Remove");
		temp.setActionCommand("destroy_user_entity_set");
                temp.setToolTipText("Remove selected user entity sets from current session");
		temp.addActionListener(listener);
		menu.add(temp);
		
		return menu;
    }
  
    public static void addMainMenuItems(ActionListener listener, JComponent menu){
    	
		JMenuItem menuItem = new JMenuItem("New Session");
		menuItem.addActionListener(listener);
		menuItem.setActionCommand("new_session");
                menuItem.setToolTipText("<html>Create a new BIANA session:<br> Start querying available BIANA databases for a set of <br>biomolecules of interest and managing their relation networks</html>");
		menu.add(menuItem);
		menuItem = new JMenuItem("Close session");
		menuItem.addActionListener(listener);
		menuItem.setActionCommand("close_session");
		menu.add(menuItem);
    }
    
    public static JPopupMenu getNetworkMenu(ActionListener listener){

    	JPopupMenu menu = new JPopupMenu();
    	
    	JMenuItem menuItem = new JMenuItem("Create/Expand");
    	menuItem.addActionListener(listener);
    	menuItem.setActionCommand("create_network");
        menuItem.setToolTipText("Add relations between biomolecules of this user entity set");
    	menu.add(menuItem);
    	
    	//menuItem = new JMenuItem("Export");
    	//menuItem.addActionListener(listener);
    	//menuItem.setActionCommand("export_network");
    	//menu.add(menuItem);
    	
    	menuItem = new JMenuItem("View/Export details");
    	menuItem.addActionListener(listener);
    	menuItem.setActionCommand("view_network_details");
        menuItem.setToolTipText("View/Export details of relations of biomolecules in this set");
    	menu.add(menuItem);
    	
    	menuItem = new JMenuItem("Randomize");
    	menuItem.addActionListener(listener);
    	menuItem.setActionCommand("randomize_network");
        menuItem.setToolTipText("Randomize the relations of biomolecules in this user entity set (asks for randomization type)");
    	menu.add(menuItem);
    	
    	return menu;
    	
    }
    
    public static JPopupMenu getSessionMenu(ActionListener listener){
    	
    	JPopupMenu menu = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("<html>Create new set <i>(Query BIANA Database)</i></html>");
		menuItem.addActionListener(listener);
		menuItem.setActionCommand("new_uEs");
                menuItem.setToolTipText("<html>Create a new user entity set querying BIANA database for biomolecules of interest: <br> Here you can query biological data resources in BIANA using various kinds of attributes such as <ul> <li><i>" + "UniprotAccession </i></li><li><i> GeneSymbol </i></li><li><i> Keyword </i></li><li><i> GeneID </i><li><i> ..." + "</i></li> </ul></html>");
		// Utilities.join(this.controller.get_available_default_attributes(), "</i></li><li><i>")
		menu.add(menuItem);
		/*menuItem = new JMenuItem("Select all");
		menuItem.setActionCommand("select_all_uEs");
		menuItem.addActionListener(listener);
		menu.add(menuItem);
		menuItem = new JMenuItem("Unselect all");
		menuItem.setActionCommand("unselect_all_uEs");
		menuItem.addActionListener(listener);
		menu.add(menuItem);*/

		menuItem = new JMenuItem("Save session");
		menuItem.setActionCommand("save_session");
		menuItem.addActionListener(listener);
                menuItem.setToolTipText("Save this session for later use (asks for the file name)");
		menu.add(menuItem);

		menuItem = new JMenuItem("Save commands history");
		menuItem.setActionCommand("save_commands");
		menuItem.addActionListener(listener);
		menu.add(menuItem);
                menuItem.setToolTipText("Save commands used in this session into file (asks for the file name)");

		menuItem = new JMenuItem("Reconnect database");
		menuItem.setActionCommand("reconnect_database");
		menuItem.addActionListener(listener);
		menu.add(menuItem);
                menuItem.setToolTipText("Reconnects to database if database server has gone away");

		menuItem = new JMenuItem("Close");
		menuItem.setActionCommand("close_session");
		menuItem.addActionListener(listener);
                menuItem.setToolTipText("Close the session");
		menu.add(menuItem);
		
		return menu;
	}


    public static void addHelpMenuItems(ActionListener listener, JComponent menu){

	JMenuItem menuItem = new JMenuItem("Help");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_H, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("help");
        menuItem.addActionListener(listener);
        menu.add(menuItem);

	menuItem = new JMenuItem("About");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("about");
        menuItem.addActionListener(listener);
        menu.add(menuItem);
	
    }
	    
    public static JMenu getHelpMenu(ActionListener listener){
    	
    	JMenu menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);

	BIANA.addHelpMenuItems(listener, menu);

        return menu;
    }
    
    

    protected JMenuBar createMenuBar() {
    	
        JMenuBar menuBar = new JMenuBar();

        //Set up the lone menu.
        JMenu menu = new JMenu("Biana");
        menu.setMnemonic(KeyEvent.VK_B);
        menuBar.add(menu);

        //Set up the first menu item.
        JMenuItem menuItem = new JMenuItem("New Session");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("new_session");
        menuItem.addActionListener(this.controller);
        menuItem.setToolTipText("<html>Create a new BIANA session:<br> Start querying available BIANA databases for a set of <br>biomolecules of interest and managing their relation networks</html>");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Close");
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("close_session");
        menuItem.addActionListener(this.controller);
        menuItem.setToolTipText("Close the session");
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Close all sessions");
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("close_all_sessions");
        menuItem.addActionListener(this.controller);
        menuItem.setToolTipText("Close all sessions");
        menu.add(menuItem);

        //Set up the second menu item.
        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener(this.controller);
        menu.add(menuItem);
        
        //Set the configuration menu
        menu = new JMenu("Configuration");
        menu.setMnemonic(KeyEvent.VK_C);
        menuBar.add(menu);
        BIANA.addDBAdministrationMenuItems(this.controller, menu);
        
        //Set the help menu
        menuBar.add(BIANA.getHelpMenu(this.controller));
        
        
        return menuBar;
    }

    //React to menu selections.
    public void actionPerformed(ActionEvent e) {
        if ("about".equals(e.getActionCommand())) { //new
        	new Credits();
        	//System.err.println("I should not be here...");
        	//new NewSessionDialog(this).setVisible(true);
        	//this.controller.add_command("Starting new session", true);
        } else { //quit
        	System.err.println("Going to quit because action command not recognized");
            quit();
        }
    }

    //Quit the application.
    protected void quit() {
        System.exit(0);
    }

    /**
	 * This method initializes jBianaSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
    
	private JSplitPane getJBianaSplitPane() {
	    if (jBianaSplitPane == null) {
		jBianaSplitPane = new JSplitPane();
		jBianaSplitPane.setSize(446, 200);
		jBianaSplitPane.setDividerSize(4);
		jBianaSplitPane.setDividerLocation(180);
		jBianaSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		JScrollPane shellScrollPane = new JScrollPane(this.controller.getShell());
		shellScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jBianaSplitPane.setRightComponent(shellScrollPane);
		jBianaSplitPane.setLeftComponent(controller.getJSessionPanel());
	    }
	    return jBianaSplitPane;
	}
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        BIANA frame = new BIANA();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
	
	/*
	JFrame presentation = new Presentation();

	try{
	    //Thread.sleep(3000);
	}
	catch(Exception e){
	    e.printStackTrace();
	}
	presentation.dispose();*/
	
	//createAndShowGUI();
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
		public void run() {
		    createAndShowGUI();
		}
	    });
    }
    
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowClosing(WindowEvent e) { this.controller.close(); }
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}
