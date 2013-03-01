
import java.util.Calendar;
import java.text.SimpleDateFormat;

import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.BoxLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.Box;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.*;

import java.util.Iterator;
import java.io.*;

public class DatabaseAdministratorPane extends JPanel implements ActionListener, MouseListener{

    private static final long serialVersionUID = 1L;
    private JPanel jDatabaseAdministratorCommandsPanel = null;
    private JSplitPane jDatabaseAdministratorOutSplitPane = null;
    private JScrollPane jDatabaseAdmCommandsTableScrollPane = null;
    private JTable jDatabaseAdmCommandsTable = null;
    private DBSelectionPanel databaseConnectionPanel = null;
    private JPanel jSubmitButtonPanel = null;
    private JButton jSubmittButton = null;
    private ParserOptionsPanel jOptionsPanel = null;
    private JScrollPane jOutScrollPane = null;
    
    private Vector<String> processes;  //  @jve:decl-index=0:
    private BlockingQueue<String[]> queue;  //  @jve:decl-index=0:
    private Hashtable<Integer,String> processes_out;
	
    private int current_process=-1;
    
    private ProcessController controller = null;
    
    private BianaDatabase[] available_databases = null; 
    private Vector<String[]> parsers = null;
    private Vector<String> default_attributes = null;
    
    /**
     * This is the default constructor
     */
    public DatabaseAdministratorPane(BianaDatabase[] pDB, Vector<String[]> pParsers, Vector<String> pAttributes) {
	super();
	this.available_databases = pDB;
	this.parsers = pParsers;
	this.default_attributes = pAttributes;
	initialize();
    }

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(509, 702);
		this.setLayout(new BorderLayout());
		this.setEnabled(true);
		this.add(getJDatabaseAdministratorCommandsPanel(), BorderLayout.NORTH);
		this.add(getJDatabaseAdmCommandsTableScrollPane(), BorderLayout.CENTER);
		this.add(getJDatabaseAdmCommandsTableScrollPane());
		this.processes = new Vector<String>();
		//this.processes_out = new Hashtable<Integer,JFrame>();
		this.processes_out = new Hashtable<Integer,String>();
		this.queue = new LinkedBlockingQueue<String[]>(1000);
		this.controller = new ProcessController(this,queue);
	}

	/**
	 * This method initializes jDatabaseAdministratorCommandsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJDatabaseAdministratorCommandsPanel() {
		if (jDatabaseAdministratorCommandsPanel == null) {
			jDatabaseAdministratorCommandsPanel = new JPanel();
			jDatabaseAdministratorCommandsPanel.setLayout(new BoxLayout(getJDatabaseAdministratorCommandsPanel(), BoxLayout.Y_AXIS));
			jDatabaseAdministratorCommandsPanel.add(getDatabaseConnectionPanel(), null);
			this.jOptionsPanel = new ParserOptionsPanel(this.parsers, this.default_attributes);
			jDatabaseAdministratorCommandsPanel.add(jOptionsPanel,null);
			jDatabaseAdministratorCommandsPanel.add(Box.createVerticalGlue());
			jDatabaseAdministratorCommandsPanel.add(getJSubmitButtonPanel(), null);
		}
		return jDatabaseAdministratorCommandsPanel;
	}

	/**
	 * This method initializes jDatabaseAdministratorOutSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getJDatabaseAdministratorOutSplitPane() {
		if (jDatabaseAdministratorOutSplitPane == null) {
			jDatabaseAdministratorOutSplitPane = new JSplitPane();
			jDatabaseAdministratorOutSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jDatabaseAdministratorOutSplitPane.setEnabled(false);
			jDatabaseAdministratorOutSplitPane.setDividerSize(1);
			jDatabaseAdministratorOutSplitPane.setEnabled(true);
			jDatabaseAdministratorOutSplitPane.setTopComponent(getJDatabaseAdmCommandsTableScrollPane());
			jDatabaseAdministratorOutSplitPane.setBottomComponent(new JPanel());
			jDatabaseAdministratorOutSplitPane.setOneTouchExpandable(false);
		}
		return jDatabaseAdministratorOutSplitPane;
	}

	/**
	 * This method initializes jDatabaseAdmCommandsTableScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJDatabaseAdmCommandsTableScrollPane() {
		if (jDatabaseAdmCommandsTableScrollPane == null) {
			jDatabaseAdmCommandsTableScrollPane = new JScrollPane();
			jDatabaseAdmCommandsTableScrollPane.setViewportView(getJDatabaseAdmCommandsTable());
		}
		return jDatabaseAdmCommandsTableScrollPane;
	}

    /**
     * This method initializes jDatabaseAdmCommandsTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getJDatabaseAdmCommandsTable() {
	if (jDatabaseAdmCommandsTable == null) {
	    DefaultTableModel model = new DefaultTableModel() {
		    /**
		     * 
		     */
		    private static final long serialVersionUID = 1L;
		    
		    @Override
			public boolean isCellEditable(int row, int column) {
			return false;
		    }
		};
	    
	    model.addColumn("Parser");
	    //model.addColumn("Command");
	    model.addColumn("Submitted at");
	    model.addColumn("Status");
	    
	    jDatabaseAdmCommandsTable = new JTable(model){
		    
		    private static final long serialVersionUID = 1L;
		    
		    @Override
			public boolean isCellEditable(int row, int column) {
			return false;
		    }
		    
		};
	    
	    jDatabaseAdmCommandsTable.addMouseListener(this);
	    
	    
	}
	return jDatabaseAdmCommandsTable;
    }

	/**
	 * This method initializes databaseConnectionPanel	
	 * 	
	 * @return DatabaseConnectionPanel	
	 */
	private DBSelectionPanel getDatabaseConnectionPanel() {
		if (databaseConnectionPanel == null) {
			databaseConnectionPanel = new DBSelectionPanel(this.available_databases);
			databaseConnectionPanel.setName(null);
		}
		return databaseConnectionPanel;
	}

	/**
	 * This method initializes jSubmitButtonPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJSubmitButtonPanel() {
		if (jSubmitButtonPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			jSubmitButtonPanel = new JPanel();
			jSubmitButtonPanel.setLayout(flowLayout);
			jSubmitButtonPanel.add(new JLabel("Double-click on table rows to view parsing process log."));
			jSubmitButtonPanel.add(getJSubmittButton(), null);
		}
		return jSubmitButtonPanel;
	}

	/**
	 * This method initializes jSubmittButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJSubmittButton() {
		if (jSubmittButton == null) {
			jSubmittButton = new JButton();
			jSubmittButton.setText("Execute");
			jSubmittButton.addActionListener(this);
		}
		return jSubmittButton;
	}
	
    public void actionPerformed(ActionEvent e) {
	
        if (e.getSource() == this.jSubmittButton ) {
	    try{
		Vector<String> commandList = new Vector<String>();
		//commandList.add(Executables.PYTHON_EXEC);
		commandList.add(BianaProcessController.BIANA_PROPERTIES.getProperty("python_exec"));
		
		try {
	        File temp = File.createTempFile("bianaParser", ".py");
	        temp.deleteOnExit();
	        BufferedWriter out = new BufferedWriter(new FileWriter(temp));
	        out.write("import biana.BianaParser\n");
		out.write("biana.BianaParser.run()\n");
	        out.close();
	        commandList.add(temp.getAbsolutePath());
		} catch (IOException err) {
			System.err.println("Error in creating parser temp file");
	    }
		commandList.addAll(this.jOptionsPanel.get_command());
		commandList.addAll(this.databaseConnectionPanel.get_command_vector());
		String[] commandPar = new String[commandList.size()];
		commandList.toArray(commandPar);
		StringBuffer commandStr = new StringBuffer();
		for( Iterator it = commandList.iterator(); it.hasNext(); ){
		    commandStr.append(it.next());
		    commandStr.append(" ");
		}
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy 'at' hh:mm");
		
		((DefaultTableModel)this.jDatabaseAdmCommandsTable.getModel()).addRow(new Object[]{
				this.jOptionsPanel.getDatabaseName(),
				sdf.format(cal.getTime()),
				"waiting"});
		    
		
		this.processes.add(commandStr.toString());
		this.queue.put(commandPar);

		

	    }
	    catch( Exception exc ){
	    	JOptionPane.showMessageDialog(this, exc.getMessage(), "Parameter errors", JOptionPane.ERROR_MESSAGE);
	    }
        }
    }
	
    public void setCurrentOutput(int pProcessId, String pOutFileName ){
	this.processes_out.put(pProcessId, pOutFileName);
    }

    public void change_process_status(int process_index, String status){
    	System.err.println("Trying to change row "+process_index+" to status "+status);
    	System.err.println(this.jDatabaseAdmCommandsTable.getModel().getRowCount());
    	this.jDatabaseAdmCommandsTable.getModel().setValueAt(status, process_index, 2);
    }
    
    public void mouseClicked(MouseEvent e) {
	if( e.getClickCount()== 2){
	    try{
		JFrame outFrame = new JFrame();
		outFrame.setSize(500,300);
		outFrame.setTitle("Parser out. ");
		System.err.println("Trying to open file "+this.processes_out.get(this.getJDatabaseAdmCommandsTable().getSelectedRow()));
		FileTextPane out_shell = new FileTextPane(this.processes_out.get(this.getJDatabaseAdmCommandsTable().getSelectedRow()));
		JScrollPane out_scroll_pane = new JScrollPane(out_shell);
		out_scroll_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		out_scroll_pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		outFrame.add(out_scroll_pane);
		outFrame.setVisible(true);
	    }
	    catch (Exception exc) {
		JOptionPane.showMessageDialog(this,
					      "This parser is still not running",
					      "BIANA ERROR",
					      JOptionPane.ERROR_MESSAGE);
		return;
	    }
	}	
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}

}
