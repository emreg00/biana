
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JButton;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.JPasswordField;

import java.io.File;
import java.util.Vector;;

public class NewSessionPanel extends CommandPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    //private static final String dirSession = "../../../saved_sessions/";
    private JLabel jLabelDummy = null;
    private JLabel jLabelDummy2 = null;
    private JRadioButton JRadioButtonExisting = null;
    private JTextField JTextFieldExisting = null;
    //private JFileChooser JFileChooserExisting = new JFileChooser();
    private JButton jInputButton = null;
    private String input = null; 
    private JRadioButton JRadioButtonNew = null;
    private ButtonGroup group = new ButtonGroup();
    private JLabel jLabel = null;
    private JComboBox jAvailableDBComboBox = null;
    private JLabel jLabel1 = null;
    private JTextField jTextField = null;
    private JLabel jLabel2 = null;
    private JPasswordField jPasswordField = null;
    private JLabel jLabel3 = null;
    private JComboBox jComboBox1 = null;
    private String sessionId = null;
    
    private BianaDatabase[] available_databases = null;
    
    /**
     * This method initializes 
     * 
     */
    public NewSessionPanel(BianaDatabase[] pDB, String sessionID) {
	super();
	if( pDB.length==0 ){
		//ERROR!!!! NO PUEDE SER 0!!!
	}
	this.available_databases = pDB;
	initialize();
	this.refresh_values();
	this.sessionId = sessionID;
    }
    
    /**
     * This method initializes this
     * 
     */
    private void initialize() {
	
    	jLabelDummy = new JLabel();
	jLabelDummy.setText("");
	jLabelDummy2 = new JLabel();
	jLabelDummy2.setText("");
    	JTextFieldExisting = new JTextField();
    	jInputButton = getJInputButton();
	jLabel2 = new JLabel();
	jLabel2.setText("Biana DB Password");
	jLabel1 = new JLabel();
	jLabel1.setText("Biana DB User");
	jLabel3 = new JLabel();
	jLabel3.setText("Unification Protocol");
	jLabel = new JLabel();
	jLabel.setText("Use database");
	
	SharedObjects.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	SharedObjects.fileChooser.setDialogTitle("Select input file/directory");
	SharedObjects.fileChooser.setToolTipText("<html>Choose previously saved <i>BIANA Session</i> data file</html>");
	//JFileChooserExisting.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    	//JFileChooserExisting.setDialogTitle("Select input file/directory");
    	
        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.setRows(8);
        gridLayout1.setHgap(5);
        gridLayout1.setVgap(5);
        gridLayout1.setColumns(2);
        this.setLayout(gridLayout1);
        //this.setSize(new Dimension(252, 122));
        this.setSize(new Dimension(504, 122));
        //super.setSize(new Dimension(554, 200));
        this.add(getJRadioButtonExisting(), null);
        this.add(jLabelDummy, null);
        this.add(getJTextFieldExisting(), null);
        this.add(getJInputButton(), null);
        this.add(getJRadioButtonNew(), null);
        this.add(jLabelDummy2, null);
        this.add(jLabel, null);
        this.add(getJAvailableDBComboBox(), null);
        this.add(jLabel3, null);
        this.add(getJComboBox1(), null);
        this.add(jLabel1, null);
        this.add(getJTextField(), null);
        this.add(jLabel2, null);
        this.add(getJPasswordField(), null);
	this.setToolTipText("<html>Either<ul><li>Load data from a previously created <i>BIANA Session</i></li></ul> or <ul><li>Create a new <i>BIANA Session</i> (a container of sets of biomolecules and their networks)</li></ul></html>");
        group.add(getJRadioButtonExisting());
        group.add(getJRadioButtonNew());
	}
    
	@Override
	protected boolean check_parameters(){
	    if(JRadioButtonExisting.isSelected()){
		File file = new File(JTextFieldExisting.getText());
		if (!file.exists() ){
		    return false;
		    //For the moment, it is not possible to show the dialog... as this is a panel...
		    /*JOptionPane.showMessageDialog(this.getParentFrame(), 
		      "There is not any BIANA database available. You must first add a new available BIANA database",
		      "BIANA ERROR",
		      JOptionPane.ERROR_MESSAGE);*/
		    //return false;
		}
	    }
	    return true;
	}

    @Override
	protected String get_command() {
	if(JRadioButtonExisting.isSelected())  {
	    /*if(input != null) {
		System.out.println(input);
		this.sessionId = input;
		return "load_session(\"" + input + "\")";
		} */
	    this.sessionId = JTextFieldExisting.getText();
	    return "load_session(\""+JTextFieldExisting.getText()+"\")";
	    //return "send_error_notification(\"No session to be loaded selected\", \"No session to be loaded selected\")";
	}
	if(JRadioButtonNew.isSelected())  {
	    BianaDatabase d = (BianaDatabase)this.getJAvailableDBComboBox().getSelectedItem();
	    this.sessionId = "biana_session";
	    return "create_new_session(sessionID=\""+this.sessionId+"\",dbname=\""+d.getDbname()+"\",dbhost=\""+d.getDbhost()+"\",dbuser=\""+d.getDbuser()+"\",dbpassword=\""+d.getDbpass()+"\",unification_protocol=\""+this.getJComboBox1().getSelectedItem()+"\")";
	}
	return ""; 
    }
    
    
    /**
	 * This method initializes JRadioButtonExisting	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButtonExisting() {
		if (JRadioButtonExisting == null) {
			JRadioButtonExisting = new JRadioButton("Load Existing Session");
	    	JRadioButtonExisting.setActionCommand("disable_new");
		JRadioButtonExisting.setToolTipText("<html>Load data from a previously created <i>BIANA Session</i></html>");
	    	//JRadioButtonExisting.setSelected(true);
	    	JRadioButtonExisting.setSelected(false);
	    	JRadioButtonExisting.addActionListener(this);
	    	//getJAvailableDBComboBox().setEnabled(false);
        	//getJComboBox1().setEnabled(false);
        	//getJTextField().setEnabled(false);
        	//getJPasswordField().setEnabled(false);
		}
		return JRadioButtonExisting;
	}
	
    /**
	 * This method initializes JRadioButtonExisting	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
    private JRadioButton getJRadioButtonNew() {
	if (JRadioButtonNew == null) {
	    JRadioButtonNew = new JRadioButton("Create Empty Session");
	    JRadioButtonNew.setActionCommand("disable_existing");
	    JRadioButtonNew.setSelected(true);
	    JRadioButtonNew.setToolTipText("<html>Create a new <i>BIANA Session</i> (a container of sets of biomolecules and their networks): <br> Provide <i>BIANA Database</i> and <i>Unification Protocol</i> information to start creating/analyzing new sets of biomolecules of interest and their relations</html>");
	    getJTextFieldExisting().setEnabled(false);
	    getJInputButton().setEnabled(false);
	    JRadioButtonNew.addActionListener(this);
	}
	return JRadioButtonNew;
	}
    
    /**
	 * This method initializes jInputButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJInputButton() {
		if (jInputButton == null) {
			jInputButton = new JButton();
			jInputButton.setText("Choose");
			jInputButton.setName("jInputButton");
			jInputButton.addActionListener(this);
			jInputButton.setToolTipText("<html>Choose previously saved <i>BIANA Session</i> data file</html>");
		}
		return jInputButton;
	}
    
	/**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextFieldExisting() {
	if (JTextFieldExisting == null) {
	    JTextFieldExisting = new JTextField();
	    JTextFieldExisting.setToolTipText("<html>Choose previously saved <i>BIANA Session</i> data file</html>");
	}
	return JTextFieldExisting;
    }
	

    /**
     * This method initializes jAvailableDBComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getJAvailableDBComboBox() {
	if (jAvailableDBComboBox == null) {
	    jAvailableDBComboBox = new JComboBox(this.available_databases);
	    jAvailableDBComboBox.addActionListener(this);
	    jAvailableDBComboBox.setToolTipText("<html>Select <i>Biana Database</i> to use</html>");
	}
	return jAvailableDBComboBox;
    }
    
    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextField() {
	if (jTextField == null) {
	    jTextField = new JTextField();
	}
	return jTextField;
    }
    
    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getJPasswordField() {
	if (jPasswordField == null) {
	    jPasswordField = new JPasswordField();
	}
	return jPasswordField;
    }
    
    /**
     * This method initializes jComboBox1	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getJComboBox1() {
	if (jComboBox1 == null) {
	    jComboBox1 = new JComboBox();
	    jComboBox1.setToolTipText("<html>Select <i>Unification Protocol</i> to use</html>");
	}
	return jComboBox1;
    }
    
    public void actionPerformed(ActionEvent e) {
		if( e.getSource()==this.getJAvailableDBComboBox()){
		    this.refresh_values();
		} else if (e.getSource() == this.jInputButton ) {
		    //int returnVal = JFileChooserExisting.showOpenDialog(this);
		    int returnVal = SharedObjects.fileChooser.showOpenDialog(this);
		    if (returnVal == JFileChooser.APPROVE_OPTION) {
        		//File inputPath = JFileChooserExisting.getSelectedFile();
			File inputPath = SharedObjects.fileChooser.getSelectedFile();
        		this.input = inputPath.getPath();
        		this.JTextFieldExisting.setText(this.input);
		    }
        } else if("disable_new".equals(e.getActionCommand())){
        	getJAvailableDBComboBox().setEnabled(false);
        	getJComboBox1().setEnabled(false);
        	getJTextField().setEnabled(false);
        	getJPasswordField().setEnabled(false);
        	getJTextFieldExisting().setEnabled(true);
			getJInputButton().setEnabled(true);
		} else if("disable_existing".equals(e.getActionCommand())){
			getJTextFieldExisting().setEnabled(false);
			getJInputButton().setEnabled(false);
			getJAvailableDBComboBox().setEnabled(true);
        	getJComboBox1().setEnabled(true);
        	getJTextField().setEnabled(true);
        	getJPasswordField().setEnabled(true);
		}
    }
    
    private void refresh_values(){
	BianaDatabase selected_db = (BianaDatabase)this.getJAvailableDBComboBox().getSelectedItem();
	this.getJComboBox1().removeAllItems();
	if( selected_db.getDbuser()!= null ){
	    this.getJTextField().setText(selected_db.getDbuser());
	    this.getJPasswordField().setText(selected_db.getDbpass());
	}
	String[] unification_protocols = selected_db.getUnification_protocols();
	for( int i=0; i<unification_protocols.length; i++ ){ 
	    this.getJComboBox1().addItem(unification_protocols[i]);
	}
    }
    
}  //  @jve:decl-index=0:visual-constraint="95,19"
