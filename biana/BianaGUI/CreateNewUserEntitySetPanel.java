import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;

import javax.swing.event.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JList;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.util.Vector;
import javax.swing.JTree;
import java.io.*;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.SystemColor;
import java.awt.Font;

public class CreateNewUserEntitySetPanel extends CommandPanel implements ActionListener, ListSelectionListener {

    private static final long serialVersionUID = 1L;
    private JPanel jNamePanel = null;
    private JLabel jLabel = null;
    private JTextField jTextField = null;
    private JPanel jAttributeValuesPanel = null;
    private JPanel jPanel1 = null;
    private JPanel jPanel2 = null;
    private JLabel jLabel1 = null;
    private JScrollPane jScrollPane = null;
    private JLabel jLabel2 = null;
    private JButton jButton = null;
    private JButton jNavigateButton = null;
    private JPanel jCentralPanel = null;
    private JPanel jPanel5 = null;
    private JLabel jLabel3 = null;
    private JTextField jTextField1 = null;
    private JButton jButton1 = null;
    private JButton jAddAsRestrictionButton = null;
    private JButton jAddAsNegativeRestrictionButton = null;
    private JPanel jPanel6 = null;
    private JButton jButton2 = null;
    private JScrollPane jScrollPane1 = null;
    private JTable jTable = null;
    private JList jList = null;
    private JScrollPane jScrollPane2 = null;
    private JTextArea jTextArea = null;
    //private JFileChooser chooser = null;
    private Vector<String> available_attributes = null;
    private Vector<String> values = null;
    private Vector<String> restrictions = null;
    private Vector<String> negative_restrictions = null;
    private BianaProcessController controller = null;
	private JPanel jTablePanel = null;
	private JPanel jTableButtonsPanel = null;
	private JButton jDeleteRowsButton = null;
	private JButton jResetButton = null;
	private JPanel jAddButtonsPanel = null;
	private JPanel jRestrictionsTablePanel1 = null;
	private JScrollPane jRestrictionsScrollPane11 = null;
	private JTable jRestrictionsTable1 = null;
	private JPanel jRestrictionsTableButtonsPanel1 = null;
	private JButton jRestrictionsDeleteRowsButton1 = null;
	private JButton jRestrictionsResetButton1 = null;
	private JPanel jTablesPanel = null;  //  @jve:decl-index=0:visual-constraint="20,131"
	
    /**
     * This is the default constructor
     */
    //public CreateNewUserEntitySetPanel(String[] pAvailableAttributes, String pNewName) {
    public CreateNewUserEntitySetPanel(Vector<String> pAvailableAttributes, String pNewName, java.util.Collection selectedUserEntities, BianaProcessController controller) {

	super();
	this.available_attributes = pAvailableAttributes;
	this.setSize(677, 238);
	this.setLayout(new BorderLayout());
	this.add(getJCentralPanel(), BorderLayout.NORTH);
	//this.chooser = new JFileChooser();
	SharedObjects.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	SharedObjects.fileChooser.setDialogTitle("Select input identifier list file");
	SharedObjects.fileChooser.setToolTipText("Select input identifier list from a file which contains one identifier per line");
	this.add(getJTablesPanel(), BorderLayout.CENTER);
	this.controller = controller;
	this.setVisible(true);
	
	//Start default values
	this.getJTextField().setText(pNewName);
	this.values = new Vector<String>();
	this.restrictions = new Vector<String>();
	this.negative_restrictions = new Vector<String>();
	//this.read_from_files = new Vector<String>();
	addInitialUserEntities(selectedUserEntities);
    }

    private void addInitialUserEntities(java.util.Collection selectedUserEntities) {
	if(selectedUserEntities != null) {
	    this.values.add("(\"userEntityID\","+Utilities.join(selectedUserEntities, "),(\"userEntityID\",")+")");
	    DefaultTableModel model = (DefaultTableModel)this.getJTable().getModel();
	    model.addRow(new Object[]{ "User Entity ID", Utilities.join(selectedUserEntities,", ")});
	}
    }

    /**
     * This method initializes jNamePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJNamePanel() {
	if (jNamePanel == null) {
	    jLabel = new JLabel();
	    jLabel.setText("User Entity Set Name");
	    FlowLayout flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    jNamePanel = new JPanel();
	    jNamePanel.setLayout(flowLayout);
	    jNamePanel.add(jLabel, null);
	    jNamePanel.add(getJTextField(), null);
	}
	return jNamePanel;
    }
    
    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextField() {
	if (jTextField == null) {
	    jTextField = new JTextField();
	    jTextField.setColumns(20);
	}
	return jTextField;
    }
    
    /**
     * This method initializes jAttributeValuesPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJAttributeValuesPanel() {
	if (jAttributeValuesPanel == null) {
	    jAttributeValuesPanel = new JPanel();
	    jAttributeValuesPanel.setLayout(new BorderLayout());
	    jAttributeValuesPanel.add(getJPanel1(), BorderLayout.CENTER);
	    jAttributeValuesPanel.add(getJPanel2(), BorderLayout.EAST);
	}
	return jAttributeValuesPanel;
    }
    
    /**
     * This method initializes jPanel1	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanel1() {
	if (jPanel1 == null) {
	    jLabel2 = new JLabel();
	    jLabel2.setText("Values");
	    jLabel1 = new JLabel();
	    jLabel1.setText("Attribute");
	    FlowLayout flowLayout1 = new FlowLayout();
	    flowLayout1.setAlignment(FlowLayout.LEFT);
	    jPanel1 = new JPanel();
	    jPanel1.setLayout(flowLayout1);
	    jPanel1.add(jLabel1, null);
	    jPanel1.add(getJScrollPane(), null);
	    jPanel1.add(jLabel2, null);
	    jPanel1.add(getJScrollPane2(), null);
            jPanel1.setToolTipText("<html>Select an attribute and then enter a list of values<br> of biomolecules either from text field or from file <html>");
	}
	return jPanel1;
    }
    
    /**
     * This method initializes jPanel2	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanel2() {
	if (jPanel2 == null) {
	    //FlowLayout flowLayout2 = new FlowLayout();
	    //flowLayout2.setAlignment(FlowLayout.RIGHT);
	    jPanel2 = new JPanel();
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.setColumns(1);
	    gridLayout.setRows(3);
	    gridLayout.setVgap(5);	    
	    jPanel2.setLayout(gridLayout);
	    //jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.Y_AXIS));
	    //jPanel2.setLayout(flowLayout2);
	}
	return jPanel2;
    }
    
    /**
     * This method initializes jScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane() {
	if (jScrollPane == null) {
	    jScrollPane = new JScrollPane();
	    jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    jScrollPane.setPreferredSize(new Dimension(150, 200));
	    jScrollPane.setViewportView(getJList());
            jScrollPane.setToolTipText("<html>Select an attribute and then enter a list of values<br> of biomolecules either from text field or from file <html>");
	}
	return jScrollPane;
    }

    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButton() {
	if (jButton == null) {
	    jButton = new JButton();
	    jButton.setText("Add");
            jButton.setToolTipText("<html>INCLUDE biomolecules having the attribute and value pairs specified above<br><i>Behaves like [OR]:</i><br>e.g. (description, cdk) gets all biomolecules whose description field contain cdk</html>");
	    jButton.addActionListener(this);
	    jButton.setActionCommand("add_attribute_list");
	}
	return jButton;
    }

    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getNavigateJButton() {
	if (jNavigateButton == null) {
	    jNavigateButton = new JButton();
	    jNavigateButton.setText("Navigate");
            jNavigateButton.setToolTipText("<html><body>Navigate into the possible values for this attribute</body></html>");
	    jNavigateButton.addActionListener(this);
	    jNavigateButton.setActionCommand("navigate");
	    jNavigateButton.setEnabled(false);
	}
	return jNavigateButton;
    }

    /**
     * This method initializes jCentralPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJCentralPanel() {
	if (jCentralPanel == null) {
	    jCentralPanel = new JPanel();
	    jCentralPanel.setLayout(new BoxLayout(getJCentralPanel(), BoxLayout.Y_AXIS));
	    jCentralPanel.add(getJNamePanel(), null);
	    jCentralPanel.add(getJAttributeValuesPanel(), null);
	    jCentralPanel.add(getJPanel5(), null);
	    jCentralPanel.add(getJAddButtonsPanel(), null);
	}
	return jCentralPanel;
    }

    /**
     * This method initializes jPanel5	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanel5() {
	if (jPanel5 == null) {
	    FlowLayout flowLayout3 = new FlowLayout();
	    flowLayout3.setAlignment(FlowLayout.LEFT);
	    jLabel3 = new JLabel();
	    jLabel3.setText("From file");
	    jPanel5 = new JPanel();
	    jPanel5.setLayout(flowLayout3);
	    jPanel5.add(jLabel3, null);
	    jPanel5.add(getJTextField1(), null);
	    jPanel5.add(getJButton1(), null);
	    jPanel5.setToolTipText("Select input identifier list from a file which contains one identifier per line");
	}
	return jPanel5;
    }
    
    /**
     * This method initializes jTextField1	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextField1() {
	if (jTextField1 == null) {
	    jTextField1 = new JTextField();
	    jTextField1.setColumns(25);
	    jTextField1.setToolTipText("Select input identifier list from a file which contains one identifier per line");
	}
	return jTextField1;
    }
    
    /**
     * This method initializes jButton1	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButton1() {
	if (jButton1 == null) {
	    jButton1 = new JButton();
	    jButton1.setText("Select");
	    jButton1.setToolTipText("Select input identifier list from a file which contains one identifier per line");
	    jButton1.addActionListener(this);
	    jButton1.setActionCommand("select_attribute_file");
	}
	return jButton1;
    }
    
    /**
     * This method initializes jPanel6	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanel6() {
	if (jPanel6 == null) {
	    jPanel6 = new JPanel();
	    jPanel6.setLayout(new FlowLayout());
	    jPanel6.add(getJButton2(), null);
	}
	return jPanel6;
    }

    /**
     * This method initializes jButton2	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButton2() {
	if (jButton2 == null) {
	    jButton2 = new JButton();
	    jButton2.setText("Add");
	    jButton2.addActionListener(this);
	    jButton2.setActionCommand("add_attribute_file");
	}
	return jButton2;
    }
    
    private JButton getJAddAsRestrictionButton() {
	if (jAddAsRestrictionButton == null) {
	    jAddAsRestrictionButton = new JButton();
	    jAddAsRestrictionButton.setText("Add as restriction");
	    jAddAsRestrictionButton.addActionListener(this);
	    jAddAsRestrictionButton.setActionCommand("add_attribute_restriction");
            jAddAsRestrictionButton.setToolTipText("<html>ONLY INCLUDE biomolecules having the attribute and value pairs specified above<br><i>Behaves like [AND] or [IN]:</i><br>e.g. (taxid, 9606) gets all biomolecules associated to human</html>");
	}
	return jAddAsRestrictionButton;
    }

    private JButton getJAddAsNegativeRestrictionButton(){
	if (jAddAsNegativeRestrictionButton == null) {
	    jAddAsNegativeRestrictionButton = new JButton();
	    jAddAsNegativeRestrictionButton.setText("Add as negative restriction");
	    jAddAsNegativeRestrictionButton.addActionListener(this);
	    jAddAsNegativeRestrictionButton.setActionCommand("add_negative_attribute_restriction");
            jAddAsNegativeRestrictionButton.setToolTipText("<html>EXCLUDE biomolecules having the attribute and value pairs specified above<br><i>Behaves like [AND NOT] or [NOT IN]:</i><br>e.g. (disease, alzheimer) excludes all biomolecules that are associated with Alzheimer's Disease</html>");
	}
	return jAddAsNegativeRestrictionButton;
    }
    

    /**
     * This method initializes jScrollPane1	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane1() {
	if (jScrollPane1 == null) {
	    jScrollPane1 = new JScrollPane();
	    jScrollPane1.setViewportView(getJTable());
	    jScrollPane1.setPreferredSize(new Dimension(170, 200));
	    jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	return jScrollPane1;
    }
    
    /**
     * This method initializes jTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getJTable() {
	if (jTable == null) {
	    //DefaultTableModel model = new DefaultTableModel();
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
	    
	    model.addColumn("Attribute");
	    model.addColumn("Values");
	    
	    jTable = new JTable(model){
		    /**
		     * 
		     */
		    private static final long serialVersionUID = 1L;
		    
		    @Override
			public boolean isCellEditable(int row, int column) {
			// TODO Auto-generated method stub
			return false;
		    }
		};	
	}
	return jTable;
    }
    
    /**
     * This method initializes jList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getJList() {
	if (jList == null) {
	    jList = new JList(this.available_attributes);
	    jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    jList.addListSelectionListener(this);
	}
	return jList;
    }

    public void valueChanged(ListSelectionEvent e){
	if( this.controller.getBianaSession().getBianaDatabase().hasOntology((String)this.getJList().getSelectedValue()) ){
	    this.getNavigateJButton().setEnabled(true);
	}
	else{
	    this.getNavigateJButton().setEnabled(false);
	}
    }


    /**
     * This method initializes jScrollPane2	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane2() {
	if (jScrollPane2 == null) {
	    jScrollPane2 = new JScrollPane();
	    jScrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    jScrollPane2.setPreferredSize(new Dimension(170, 200));
	    jScrollPane2.setViewportView(getJTextArea());
            jScrollPane2.setToolTipText("<html>Select an attribute and then enter a list of values<br> of biomolecules either from text field or from file <html>");
	}
	return jScrollPane2;
    }
    
    /**
     * This method initializes jTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getJTextArea() {
	if (jTextArea == null) {
	    jTextArea = new JTextArea();
	    jTextArea.setColumns(5);
	}
	return jTextArea;
    }
    
    public void actionPerformed(ActionEvent e) {
	if( e.getActionCommand()=="add_attribute_list" || e.getActionCommand()=="add_attribute_restriction" || e.getActionCommand()=="add_negative_attribute_restriction" ){
	    if( this.getJList().getSelectedValue()==null ){
		JOptionPane.showMessageDialog(this,
					      "You must select the attribute identifier",
					      "Attribute identider error",
					      JOptionPane.ERROR_MESSAGE);
	    }
	    else if( this.getJTextArea().getText().compareTo("")==0 ){
		JOptionPane.showMessageDialog(this,
					      "You must specify some attribute value",
					      "Attribute Values Error",
					      JOptionPane.ERROR_MESSAGE);
	    }
	    else{
		StringBuffer buffer = new StringBuffer();
		String[] lines = this.getJTextArea().getText().split("\n");
		StringBuffer currentValue_buffer = new StringBuffer();
		for( int i=0; i<lines.length; i++ ){
		    if( !this.is_empty(lines[i]) ){
			if( buffer.length()>0){
			    buffer.append(",");
			    currentValue_buffer.append(",");
			}
			buffer.append(lines[i].trim());
			currentValue_buffer.append("(\""+this.getJList().getSelectedValue()+"\",\""+lines[i].trim()+"\")");
		    }
		}
		if( e.getActionCommand()=="add_attribute_list" ){
		    this.values.add(currentValue_buffer.toString());
		}
		else if( e.getActionCommand()=="add_attribute_restriction" ){
		    this.restrictions.add(currentValue_buffer.toString());
		}
		else if( e.getActionCommand()=="add_negative_attribute_restriction" ){
		    this.negative_restrictions.add(currentValue_buffer.toString());
		}
		if( buffer.length()>0 ){
		    String additional_comment = "";
		    DefaultTableModel model;
		    if( e.getActionCommand()=="add_attribute_restriction" ){
		    	additional_comment = " [Restriction]";
			model = (DefaultTableModel)this.getJRestrictionsTable1().getModel();
		    }
		    else if( e.getActionCommand()=="add_negative_attribute_restriction" ){
			additional_comment = " [Negative restriction]";
			model = (DefaultTableModel)this.getJRestrictionsTable1().getModel();
		    }
		    else{
		    	model = (DefaultTableModel)this.getJTable().getModel();
		    }
		    model.addRow(new Object[]{
			    this.getJList().getSelectedValue()+additional_comment,
			    Utilities.join(this.getJTextArea().getText().split("\n"),",")});
		}
	    }
	}
	else if( e.getSource() == this.jDeleteRowsButton ){
		int[] selected_rows = this.getJTable().getSelectedRows();
		for( int i=0; i<selected_rows.length; i++ ){
			((DefaultTableModel)this.getJTable().getModel()).removeRow(selected_rows[i]-i);
			this.values.remove(selected_rows[i]-i);
		}
	}
	else if( e.getSource() == this.jRestrictionsDeleteRowsButton1 ){
		int[] selected_rows = this.getJRestrictionsTable1().getSelectedRows();
		for( int i=0; i<selected_rows.length; i++ ){
			((DefaultTableModel)this.getJRestrictionsTable1().getModel()).removeRow(selected_rows[i]-i);
			this.restrictions.remove(selected_rows[i]-i);
		}
	}
	else if( e.getSource() == this.jResetButton ){
		int num_rows = this.getJTable().getRowCount();
		for( int i=0; i<num_rows; i++ ){
			((DefaultTableModel)this.getJTable().getModel()).removeRow(0);
		}
		this.values.clear();
	}
	else if( e.getSource() == this.jRestrictionsResetButton1 ){
		int num_rows = this.getJRestrictionsTable1().getRowCount();
		for( int i=0; i<num_rows; i++ ){
			((DefaultTableModel)this.getJRestrictionsTable1().getModel()).removeRow(0);
		}
		this.restrictions.clear();
	}
	else if( e.getActionCommand()=="select_attribute_file" ){
	    //int returnVal = chooser.showOpenDialog(this);
	    int returnVal = SharedObjects.fileChooser.showOpenDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
		//this.getJTextField1().setText(chooser.getSelectedFile().getAbsolutePath());
		this.getJTextField1().setText(SharedObjects.fileChooser.getSelectedFile().getAbsolutePath());
		this.getJTextArea().setText("");
		
		try{
		    BufferedReader reader = new BufferedReader(new FileReader(SharedObjects.fileChooser.getSelectedFile().getAbsolutePath()));
		    StringBuffer temp = new StringBuffer();
		    
		    if( reader == null ){
			System.err.println("File not found");
		    }
		    while( reader.ready() ){
			temp.append(reader.readLine());
			temp.append("\n");
		    }
		    
		    this.getJTextArea().setText(temp.toString());
		    reader.close();
		}
		catch( Exception exc ){
		    System.err.println("ERROR READING INPUT FILE");
		}
	    }
	}
	else if( e.getActionCommand()=="navigate" ){
	    //new OntologyDialog(this.controller.getParentFrame(), controller.getOntologyTree("PSI_MI_OBO"), this.getJTextArea());
	    String taxonomyName = this.controller.getBianaSession().getBianaDatabase().getOntologyName((String)this.getJList().getSelectedValue());
	    if( taxonomyName != null ){
		System.err.println("Trying to open ontology "+taxonomyName);
		JTree tree = controller.getOntologyTree(taxonomyName);
		int max_iterations = 10;
		int iter = 0;
		while( tree==null && iter<max_iterations ){
		    tree = controller.getBianaSession().getOntology(taxonomyName);
		    try{
			Thread.sleep(1000);
		    }
		    catch(  InterruptedException exc ){
			
		    }
		    iter++;
		}
		if( tree == null ){
		    JOptionPane.showMessageDialog(this,
						  "Time exceeded to get ontology",
						  "Time exceeded to get ontology",
						  JOptionPane.ERROR_MESSAGE);
		}
		else{
		    new OntologyDialog(this.controller.getParentFrame(), controller.getOntologyTree(taxonomyName), this.getJTextArea(), taxonomyName);
		}
	    }
	    else{
		JOptionPane.showMessageDialog(this,
					      "There is no available ontolgy for this attribute",
					      "Not found ontology for this attribute",
					      JOptionPane.ERROR_MESSAGE);
	    }
	}
    }

    public String get_command(){
	return "create_new_user_entity_set( identifier_description_list = ["+Utilities.join(this.values,",")+"], attribute_restriction_list=["+Utilities.join(this.restrictions,",")+"], id_type=\"embedded\", new_user_entity_set_id=\""+this.getJTextField().getText().trim()+"\", negative_attribute_restriction_list=["+Utilities.join(this.negative_restrictions,",")+"])";	
    }
    
    @Override
	protected boolean check_parameters() {
	if( this.values.size()==0 ){
	    return false;
	}
	return true;
    }

	/**
	 * This method initializes jTablePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJTablePanel() {
		if (jTablePanel == null) {
			jTablePanel = new JPanel();
			jTablePanel.setLayout(new BoxLayout(getJTablePanel(), BoxLayout.Y_AXIS));
			jTablePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(null, "Added Attributes", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), SystemColor.activeCaption), null));
			jTablePanel.add(getJScrollPane1(), null);
			jTablePanel.add(getJTableButtonsPanel(), null);
		}
		return jTablePanel;
	}

	/**
	 * This method initializes jTableButtonsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJTableButtonsPanel() {
		if (jTableButtonsPanel == null) {
			FlowLayout flowLayout2 = new FlowLayout();
			flowLayout2.setAlignment(FlowLayout.RIGHT);
			jTableButtonsPanel = new JPanel();
			jTableButtonsPanel.setLayout(flowLayout2);
			jTableButtonsPanel.add(getJDeleteRowsButton(), null);
			jTableButtonsPanel.add(getJResetButton(), null);
		}
		return jTableButtonsPanel;
	}

	/**
	 * This method initializes jDeleteRowsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJDeleteRowsButton() {
		if (jDeleteRowsButton == null) {
			jDeleteRowsButton = new JButton();
			jDeleteRowsButton.setText("Delete selected");
			jDeleteRowsButton.addActionListener(this);
		}
		return jDeleteRowsButton;
	}

	/**
	 * This method initializes jResetButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJResetButton() {
		if (jResetButton == null) {
			jResetButton = new JButton();
			jResetButton.setText("Reset");
			jResetButton.addActionListener(this);
		}
		return jResetButton;
	}

	/**
	 * This method initializes jAddButtonsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJAddButtonsPanel() {
		if (jAddButtonsPanel == null) {
			FlowLayout flowLayout4 = new FlowLayout();
			flowLayout4.setAlignment(FlowLayout.RIGHT);
			jAddButtonsPanel = new JPanel();
			jAddButtonsPanel.setLayout(flowLayout4);
			jAddButtonsPanel.add(getJButton(), null);
			jAddButtonsPanel.add(getJAddAsRestrictionButton(), null);
			jAddButtonsPanel.add(getJAddAsNegativeRestrictionButton(), null);
			jAddButtonsPanel.add(getNavigateJButton(), null);
		}
		return jAddButtonsPanel;
	}

	/**
	 * This method initializes jRestrictionsTablePanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJRestrictionsTablePanel1() {
		if (jRestrictionsTablePanel1 == null) {
			jRestrictionsTablePanel1 = new JPanel();
			jRestrictionsTablePanel1.setLayout(new BoxLayout(getJRestrictionsTablePanel1(), BoxLayout.Y_AXIS));
			jRestrictionsTablePanel1.setBorder(BorderFactory.createTitledBorder(null, "Add restrictions", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), SystemColor.activeCaption));
			jRestrictionsTablePanel1.add(getJRestrictionsScrollPane11(), null);
			jRestrictionsTablePanel1.add(getJRestrictionsTableButtonsPanel1(), null);
		}
		return jRestrictionsTablePanel1;
	}

	/**
	 * This method initializes jRestrictionsScrollPane11	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJRestrictionsScrollPane11() {
		if (jRestrictionsScrollPane11 == null) {
			jRestrictionsScrollPane11 = new JScrollPane();
			jRestrictionsScrollPane11.setPreferredSize(new Dimension(170, 200));
			jRestrictionsScrollPane11.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jRestrictionsScrollPane11.setViewportView(getJRestrictionsTable1());
		}
		return jRestrictionsScrollPane11;
	}

	/**
	 * This method initializes jRestrictionsTable1	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJRestrictionsTable1() {
		
//		DefaultTableModel model = new DefaultTableModel();
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
	    
	    model.addColumn("Attribute");
	    model.addColumn("Restricted to values (AND)");
		
		if (jRestrictionsTable1 == null) {
			jRestrictionsTable1 = new JTable(model) {
				/** 
				 */
				private static final long serialVersionUID = 1L;
	
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
		}
		return jRestrictionsTable1;
	}

	/**
	 * This method initializes jRestrictionsTableButtonsPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJRestrictionsTableButtonsPanel1() {
		if (jRestrictionsTableButtonsPanel1 == null) {
			FlowLayout RestrictionsflowLayout21 = new FlowLayout();
			RestrictionsflowLayout21.setAlignment(FlowLayout.RIGHT);
			jRestrictionsTableButtonsPanel1 = new JPanel();
			jRestrictionsTableButtonsPanel1.setLayout(RestrictionsflowLayout21);
			jRestrictionsTableButtonsPanel1.add(getJRestrictionsDeleteRowsButton1(), null);
			jRestrictionsTableButtonsPanel1.add(getJRestrictionsResetButton1(), null);
		}
		return jRestrictionsTableButtonsPanel1;
	}

	/**
	 * This method initializes jRestrictionsDeleteRowsButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJRestrictionsDeleteRowsButton1() {
		if (jRestrictionsDeleteRowsButton1 == null) {
			jRestrictionsDeleteRowsButton1 = new JButton();
			jRestrictionsDeleteRowsButton1.setText("Delete selected");
			jRestrictionsDeleteRowsButton1.addActionListener(this);
		}
		return jRestrictionsDeleteRowsButton1;
	}

	/**
	 * This method initializes jRestrictionsResetButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJRestrictionsResetButton1() {
		if (jRestrictionsResetButton1 == null) {
			jRestrictionsResetButton1 = new JButton();
			jRestrictionsResetButton1.setText("Reset");
			jRestrictionsResetButton1.addActionListener(this);
		}
		return jRestrictionsResetButton1;
	}

	/**
	 * This method initializes jTablesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJTablesPanel() {
		if (jTablesPanel == null) {
			jTablesPanel = new JPanel();
			jTablesPanel.setLayout(new BoxLayout(jTablesPanel, BoxLayout.Y_AXIS));
			jTablesPanel.add(getJTablePanel());
			jTablesPanel.add(getJRestrictionsTablePanel1());
		}
		return jTablesPanel;
	}
	
    public static void main(String[] args) {
	JFrame window = new JFrame();
	Vector<String> attributes = new Vector<String>();
	attributes.add("attr1");
	attributes.add("attr2");
	attributes.add("attr3");
	window.setContentPane(new CreateNewUserEntitySetPanel(attributes,"name",null,null));
	
	window.setSize(460,600);
	window.setVisible(true);
    }
}


