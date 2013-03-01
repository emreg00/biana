import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.*;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.util.Vector;
import java.awt.event.*;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
//import javax.swing.RowFilter;   //Commented because only avaible in java 1.6

public class ShowTableDialog extends JFrame implements ActionListener, MouseListener {

    private static final long serialVersionUID = 1L;
    
    private JPanel jContentPane = null;
    private JScrollPane jUserEntitySetDetailsScrollPane = null;
    private JTable jTable = null;
    private JPanel jButtonsPanel = null;
    private JButton jPrintButton = null;
    private JButton jExitButton = null;
    private JButton jDetailsButton = null;
    private JPanel jDataPanel = null;
    private BianaProcessController controller = null;
    private Vector<String> rowIdentifiers = null;
    private String command = null;
    
    //Commented because only avaible in java 1.6
    //private TableRowSorter sorter = null;
    //private JTextField jRegexFilter = null;

    /**
     * @param owner
     */
    public ShowTableDialog(BianaProcessController pController, String pTitle, Vector<String> pColumns, Vector<Vector<String>> pValues, Vector<String> pRowIdentifiers, String pCommand ){

	this.command = pCommand;
	this.controller = pController;
	this.setTitle(pTitle);
	initialize();
	this.setAlwaysOnTop(false);
	this.rowIdentifiers = pRowIdentifiers;

	((DefaultTableModel)jTable.getModel()).setDataVector(pValues, pColumns);

	// Set table widths
	for( int i=0; i< jTable.getModel().getColumnCount(); i++ ){
	    jTable.getColumnModel().getColumn(i).setPreferredWidth(pColumns.get(i).length()*9);
	}
    }
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
	this.setSize(531, 200);
	this.setContentPane(getJContentPane());
    }
    
    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
	if (jContentPane == null) {
	    jContentPane = new JPanel();
	    jContentPane.setLayout(new BorderLayout());
	    jContentPane.add(getJDataPanel(), BorderLayout.CENTER);
	    jContentPane.add(getJButtonsPanel(), BorderLayout.SOUTH);
	}
	return jContentPane;
    }
    
    /**
     * This method initializes jUserEntitySetDetailsScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJUserEntitySetDetailsScrollPane() {
	if (jUserEntitySetDetailsScrollPane == null) {
	    jUserEntitySetDetailsScrollPane = new JScrollPane();
	    jUserEntitySetDetailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    jUserEntitySetDetailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    jUserEntitySetDetailsScrollPane.setViewportView(getJTable());
	}
	return jUserEntitySetDetailsScrollPane;
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
		    private static final long serialVersionUID = 1L;
		    @Override
			public boolean isCellEditable(int row, int column) {
			return false;
		    }
		};
	    
	    jTable = new JTable(model){
		    private static final long serialVersionUID = 1L;
		    @Override
			public boolean isCellEditable(int row, int column) {
			    return false;
		    }	
		};
	    
	    // Commented because option only available in java 1.6
	    //sorter = new TableRowSorter<DefaultTableModel>(model);
	    //jTable.setRowSorter(sorter);
	    
	    jTable.addMouseListener(this);
	}
	
	jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	
	
	return jTable;
    }
    

    //Commented because only avaible in java 1.6

    /*
    private void newFilter() {
	
	RowFilter<DefaultTableModel, Object> rf = null;
	
	try{
	    rf = RowFilter.regexFilter(getJRegexFilter().getText());
	} catch ( java.util.regex.PatternSyntaxException e ){
	    System.err.println("REGEX ERROR");
	}
	
	sorter.setRowFilter(rf);
    }

    private JTextField getJRegexFilter(){
	if( jRegexFilter == null ){
	    jRegexFilter = new JTextField();
	    jRegexFilter.setColumns(10);
	    jRegexFilter.getDocument().addDocumentListener( new DocumentListener() {
		    public void changedUpdate(DocumentEvent e){
			newFilter();
		    }
		    public void insertUpdate(DocumentEvent e){
			newFilter();
		    }
		    public void removeUpdate(DocumentEvent e){
			newFilter();
		    }
		});
	    
	}
	return jRegexFilter;
	}*/

    /**
     * This method initializes jButtonsPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJButtonsPanel() {
	if (jButtonsPanel == null) {
	    FlowLayout flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.RIGHT);
	    jButtonsPanel = new JPanel();
	    jButtonsPanel.setLayout(flowLayout);
	    
	    //Commented because only avaible in java 1.6
	    /*
	    jButtonsPanel.add( new JLabel("Filter: "),null);
	    jButtonsPanel.add(getJRegexFilter(),null);*/
	    
	    //jButtonsPanel.add(getJPrintButton(), null);
	    if( this.command != null ){
		jButtonsPanel.add(getJDetailsButton(), null);
	    }
	    jButtonsPanel.add(getJExitButton(), null);
	}
	return jButtonsPanel;
    }
    
    /**
     * This method initializes jPrintButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJPrintButton() {
	if (jPrintButton == null) {
	    jPrintButton = new JButton();
	    jPrintButton.setText("Print");
	    jPrintButton.addActionListener(this);
	}
	return jPrintButton;
    }

    /**
     * This method initializes jPrintButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJDetailsButton() {
	if (jDetailsButton == null) {
	    jDetailsButton = new JButton();
	    jDetailsButton.setText("View Details");
	    jDetailsButton.addActionListener(this);
	}
	return jDetailsButton;
    }
    
    /**
     * This method initializes jExitButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJExitButton() {
	if (jExitButton == null) {
	    jExitButton = new JButton();
	    jExitButton.setText("Exit");
	    jExitButton.addActionListener(this);
	}
	return jExitButton;
    }
    
    /**
     * This method initializes jDataPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJDataPanel() {
	if (jDataPanel == null) {
	    GridBagConstraints gridBagConstraints = new GridBagConstraints();
	    gridBagConstraints.fill = GridBagConstraints.BOTH;
	    gridBagConstraints.weighty = 1.0;
	    gridBagConstraints.weightx = 1.0;
	    jDataPanel = new JPanel();
	    jDataPanel.setLayout(new GridBagLayout());
	    jDataPanel.add(getJUserEntitySetDetailsScrollPane(), gridBagConstraints);
	}
	return jDataPanel;
    }
    
    public void actionPerformed(ActionEvent e){
	if( e.getSource()==this.getJPrintButton() ){
	    System.err.println("Going to print...");
	    try{
		System.err.println("Going to print...");
		if( ! this.getJTable().print(JTable.PrintMode.NORMAL) ){
		    System.err.println("User cancelled printing");
		}
	    } catch (java.awt.print.PrinterException exc) {
		System.err.format("Cannot print %s%n", exc.getMessage());
	    }
	    return;
	}
	else if( e.getSource()==this.getJDetailsButton() ){
	    if( this.jTable.getSelectedRowCount()> 0 ){
		if( this.command != null ){
		    this.controller.add_command("available_sessions[\""+this.controller.getSessionID()+"\"]."+this.command.replace("?",Utilities.join(this.getSelectedRowIdentifiers(),",")),true);
		}
	    }
	}
	else if( e.getSource()==this.getJExitButton() ){
	    this.dispose();
	}
    }


    private Vector<String> getSelectedRowIdentifiers(){
	Vector<String> t = new Vector<String>();

	int[] sel = this.jTable.getSelectedRows();

	System.err.println(sel.length);
	
	for( int i=0; i<sel.length; i++ ){
	    
	    // Commented because only available in java 1.6
	    //t.add(this.rowIdentifiers.get(jTable.convertRowIndexToModel(sel[i])));
	    t.add(this.rowIdentifiers.get(sel[i]));
	    //System.err.println(this.rowIdentifiers.get(jTable.convertRowIndexToModel(sel[i])));
	}
	

	return t;
    }
    
    
    public void mouseReleased(MouseEvent evt){
	/*
    	int xCoord = evt.getX();
	int yCoord = evt.getY();
	
    	if( evt.isPopupTrigger() ){
	    //this.controller.getPopUpMenu("userEntitySetDetails").show(this,xCoord,yCoord);
	    this.controller.add_command("View details for ",true);
	    }*/
    }
    
    public void mousePressed(MouseEvent evt){
	
	/*
    	int xCoord = evt.getX();
    	int yCoord = evt.getY();
    	
    	if( evt.isPopupTrigger() ){
	    //this.controller.getPopUpMenu("userEntitySetDetails").show(this,xCoord,yCoord);
	    this.controller.add_command("View details for ",true);
	    }*/

    }
    
    public void mouseEntered(MouseEvent evt){}
    public void mouseExited(MouseEvent evt){}
    public void mouseClicked(MouseEvent evt){}
    

}  //  @jve:decl-index=0:visual-constraint="122,31"
