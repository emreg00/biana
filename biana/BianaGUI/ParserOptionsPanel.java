

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.SystemColor;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.Box;
import javax.swing.JOptionPane;

import java.util.Hashtable;
import java.util.Vector;
//import java.util.SortedSet;
import java.util.Arrays;

public class ParserOptionsPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JLabel jInputLabel = null;
	private JTextField jInputTextField = null;
	private JButton jInputButton = null;
	
	private JFileChooser inputChooser = new JFileChooser();  //  @jve:decl-index=0:visual-constraint="18,163"
	
        private String input = null;
        private JLabel jExtDBNameLabel = null;
        private JTextField jExtDBNameTextField = null;
        private JPanel jInputPanel = null;
        private JPanel jDBNameVersionPanel = null;
        private JLabel jExtDBVersionLabel = null;
        private JTextField jExtDBVersionTextField = null;
        private JPanel jAdditionalOptionsPanel = null;
        private JCheckBox jTimeControlCheckBox = null;
        private JCheckBox jVerboseCheckBox = null;
        private JCheckBox jPromiscuousCheckBox = null;
        private JPanel jSelectParserPanel = null;
        private JLabel jSelectParserLabel = null;
        private JComboBox jSelectParserComboBox = null;
        private JPanel jSelectDefaultAttributePanel = null;
        private JLabel jSelectDefaultAttributeLabel = null;
        private JComboBox jSelectDefaultAttributeComboBox = null;
        private Hashtable<String,String[]> parserCommands = null;
        private int currentParser = 0;
        private JPanel jSpecificOptionsPanel = null;
        private Vector<String[]> parsers = null;
        private Vector<String> default_attributes = null;
	
	/**
	 * This is the default constructor
	 */
	public ParserOptionsPanel(Vector<String[]> parsers, Vector<String> default_attributes) {
	    super();
	    this.parsers = parsers;
            //System.err.println(default_attributes);
	    this.default_attributes = default_attributes;
	    if( parsers.size()==0 ){
		JOptionPane.showMessageDialog(this,
					      "There are not available parsers. Check it does not exist any error while loading databases.",
					      "BIANA PARSERS ERROR",
					      JOptionPane.ERROR_MESSAGE);
	    }
	    else{
		initialize();
	    }
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
	    this.load_CommandsHashTable();
	    
	    jExtDBNameLabel = new JLabel();
	    jExtDBNameLabel.setText("Database Name");
	    jInputLabel = new JLabel();
	    jInputLabel.setText("Input file/path");
	    jInputLabel.setName("jInputLabel");
	    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    this.setSize(533, 142);
	    this.setMaximumSize(new Dimension(533,142));
	    this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(null, "Parser Options", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), SystemColor.activeCaption), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
	    this.add(getJSelectParserPanel(), null);
	    this.add(Box.createRigidArea(new Dimension(0,5)));
	    this.add(getJInputPanel(), null);
	    this.add(Box.createRigidArea(new Dimension(0,5)));
	    this.add(getJDBNameVersionPanel(), null);
	    this.add(Box.createRigidArea(new Dimension(0,5)));
	    this.add(getJSelectDefaultAttributePanel(), null);
	    this.add(Box.createRigidArea(new Dimension(0,5)));
	    this.add(getJAdditionalOptionsPanel(), null);
	    this.add(getJSpecificOptionsPanel(), null);
	    if(this.jSelectParserComboBox.getSelectedItem().equals("biopax_level_2") || this.jSelectParserComboBox.getSelectedItem().equals("psi_mi_2.5") || this.jSelectParserComboBox.getSelectedItem().equals("generic") || this.jSelectParserComboBox.getSelectedItem().equals("string")) {
		getJSelectDefaultAttributePanel().setVisible(true);
		//this.getJPromiscuousCheckBox().setEnabled(true);
		if(this.jSelectParserComboBox.getSelectedItem().equals("string")) {
		    getJSelectDefaultAttributePanel().setVisible(false);
		}
	    } else {
		getJSelectDefaultAttributePanel().setVisible(false);
		//this.getJPromiscuousCheckBox().setEnabled(false);
	    }
	    if(this.jSelectParserComboBox.getSelectedItem().equals("cog") || this.jSelectParserComboBox.getSelectedItem().equals("scop") || this.jSelectParserComboBox.getSelectedItem().equals("pfam")) {
		this.getJPromiscuousCheckBox().setSelected(true);
	    } else {
		this.getJPromiscuousCheckBox().setSelected(false);
	    }
	    this.add(Box.createVerticalGlue());
	    
	    inputChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    inputChooser.setDialogTitle("Select input file/directory");
	}
	
    
    private void load_CommandsHashTable(){
		
	this.parserCommands = new Hashtable<String,String[]>();

	for( int i=0; i<this.parsers.size(); i++ ){
	    this.parserCommands.put(this.parsers.get(i)[0],this.parsers.get(i));
	}
    }

	/**
	 * This method initializes jInputTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJInputTextField() {
		if (jInputTextField == null) {
			jInputTextField = new JTextField();
			jInputTextField.setColumns(30);
			jInputTextField.setName("jInputTextField");
		}
		return jInputTextField;
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
		}
		return jInputButton;
	}
	
	public void actionPerformed(ActionEvent e) {

            //Handle open button action.
            if (e.getSource() == this.jInputButton ) {
            
                    int returnVal = inputChooser.showOpenDialog(this);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File inputPath = inputChooser.getSelectedFile();
                            this.input = inputPath.getPath();
                            this.jInputTextField.setText(this.input);
                    }
            }
            else if( e.getSource() == this.jSelectParserComboBox ){
                    
                    if( this.currentParser!= this.jSelectParserComboBox.getSelectedIndex() ){
			    this.currentParser = this.jSelectParserComboBox.getSelectedIndex();
                            this.getJInputTextField().setText("");
                            //this.getJExtDBNameTextField().setText(this.parserCommands.get(this.jSelectParserComboBox.getSelectedItem()));
                            this.getJExtDBNameTextField().setText((String)this.jSelectParserComboBox.getSelectedItem());
                            this.getJExtDBVersionTextField().setText("");
                            this.getJTimeControlCheckBox().setSelected(true);
                            this.getJVerboseCheckBox().setSelected(false);
                            //System.err.println((String)this.jSelectParserComboBox.getSelectedItem());
                            if(this.jSelectParserComboBox.getSelectedItem().equals("psi_mi_2.5") || this.jSelectParserComboBox.getSelectedItem().equals("biopax_level_2") || this.jSelectParserComboBox.getSelectedItem().equals("generic") || this.jSelectParserComboBox.getSelectedItem().equals("string")) {
				getJSelectDefaultAttributePanel().setVisible(true);
				//this.getJPromiscuousCheckBox().setEnabled(true);
				if(this.jSelectParserComboBox.getSelectedItem().equals("string")) {
				    getJSelectDefaultAttributePanel().setVisible(false);
				}
                            } else {
                                getJSelectDefaultAttributePanel().setVisible(false);
				//this.getJPromiscuousCheckBox().setEnabled(false);
                            }
                            if(this.jSelectParserComboBox.getSelectedItem().equals("cog") || this.jSelectParserComboBox.getSelectedItem().equals("scop") || this.jSelectParserComboBox.getSelectedItem().equals("pfam")) {
				this.getJPromiscuousCheckBox().setSelected(true);
			    } else {
				this.getJPromiscuousCheckBox().setSelected(false);
			    }

                    }
            } 
	}

	/**
	 * This method initializes jExtDBNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJExtDBNameTextField() {
		if (jExtDBNameTextField == null) {
			jExtDBNameTextField = new JTextField();
			jExtDBNameTextField.setColumns(13);
		}
		return jExtDBNameTextField;
	}

	/**
	 * This method initializes jInputPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJInputPanel() {
		if (jInputPanel == null) {
			jInputPanel = new JPanel();
			jInputPanel.setLayout(new BoxLayout(getJInputPanel(), BoxLayout.X_AXIS));
			jInputPanel.add(jInputLabel, null);
			jInputPanel.add(getJInputTextField(), null);
			jInputPanel.add(getJInputButton(), null);
		}
		return jInputPanel;
	}

	/**
	 * This method initializes jDBNameVersionPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJDBNameVersionPanel() {
		if (jDBNameVersionPanel == null) {
			jExtDBVersionLabel = new JLabel();
			jExtDBVersionLabel.setText("Database Version");
			jDBNameVersionPanel = new JPanel();
			jDBNameVersionPanel.setLayout(new BoxLayout(getJDBNameVersionPanel(), BoxLayout.X_AXIS));
			jDBNameVersionPanel.add(jExtDBNameLabel, null);
			jDBNameVersionPanel.add(getJExtDBNameTextField(), null);
			jDBNameVersionPanel.add(jExtDBVersionLabel, null);
			jDBNameVersionPanel.add(getJExtDBVersionTextField(), null);
		}
		return jDBNameVersionPanel;
	}

	/**
	 * This method initializes jExtDBVersionTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJExtDBVersionTextField() {
		if (jExtDBVersionTextField == null) {
			jExtDBVersionTextField = new JTextField();
			jExtDBVersionTextField.setColumns(13);
		}
		return jExtDBVersionTextField;
	}

	/**
	 * This method initializes jAdditionalOptionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJAdditionalOptionsPanel() {
		if (jAdditionalOptionsPanel == null) {
			jAdditionalOptionsPanel = new JPanel();
			jAdditionalOptionsPanel.setLayout(new FlowLayout());
			jAdditionalOptionsPanel.add(getJTimeControlCheckBox(), null);
			jAdditionalOptionsPanel.add(getJVerboseCheckBox(), null);
			jAdditionalOptionsPanel.add(getJPromiscuousCheckBox(), null);
		}
		return jAdditionalOptionsPanel;
	}

	/**
	 * This method initializes jTimeControlCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJTimeControlCheckBox() {
		if (jTimeControlCheckBox == null) {
			jTimeControlCheckBox = new JCheckBox();
			jTimeControlCheckBox.setSelected(true);
			jTimeControlCheckBox.setText("Time control");
		}
		return jTimeControlCheckBox;
	}

	/**
	 * This method initializes jVerboseCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJVerboseCheckBox() {
		if (jVerboseCheckBox == null) {
			jVerboseCheckBox = new JCheckBox();
			jVerboseCheckBox.setText("Verbose");
		}
		return jVerboseCheckBox;
	}

	/**
	 * This method initializes jPromiscuousCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJPromiscuousCheckBox() {
		if (jPromiscuousCheckBox == null) {
			jPromiscuousCheckBox = new JCheckBox();
			jPromiscuousCheckBox.setText("Promiscuous");
		}
		return jPromiscuousCheckBox;
	}

	/**
	 * This method initializes jSelectParserPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJSelectParserPanel() {
		if (jSelectParserPanel == null) {
			jSelectParserLabel = new JLabel();
			jSelectParserLabel.setText("Parser");
			jSelectParserPanel = new JPanel();
			jSelectParserPanel.setLayout(new BoxLayout(getJSelectParserPanel(), BoxLayout.X_AXIS));
			jSelectParserPanel.add(jSelectParserLabel, null);
			jSelectParserPanel.add(getJSelectParserComboBox(), null);
		}
		return jSelectParserPanel;
	}

	/**
	 * This method initializes jSelectParserComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJSelectParserComboBox() {
		if (jSelectParserComboBox == null) {
                    Object [] keys = this.parserCommands.keySet().toArray();
                    java.util.Arrays.sort(keys);
                    //java.util.Collections.sort(keys);
		    jSelectParserComboBox = new JComboBox(keys);
		    //jSelectParserComboBox = new JComboBox(this.parserCommands.keySet().toArray());
		    jSelectParserComboBox.addActionListener(this);
		    //System.err.println((String)jSelectParserComboBox.getItemAt(0));
		}
		return jSelectParserComboBox;
	}

	/**
	 * This method initializes jSelectDefaultAttributePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJSelectDefaultAttributePanel() {
		if (jSelectDefaultAttributePanel == null) {
			jSelectDefaultAttributeLabel = new JLabel();
			jSelectDefaultAttributeLabel.setText("Default attribute:");
			jSelectDefaultAttributePanel = new JPanel();
			jSelectDefaultAttributePanel.setLayout(new BoxLayout(getJSelectDefaultAttributePanel(), BoxLayout.X_AXIS));
			jSelectDefaultAttributePanel.add(jSelectDefaultAttributeLabel, null);
			jSelectDefaultAttributePanel.add(getJSelectDefaultAttributeComboBox(), null);
		}
		return jSelectDefaultAttributePanel;
	}

	/**
	 * This method initializes jSelectDefaultAttributeComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJSelectDefaultAttributeComboBox() {
		if (jSelectDefaultAttributeComboBox == null) {
                    java.util.Collections.sort(this.default_attributes);
		    this.default_attributes.add(0, "N/A");
		    jSelectDefaultAttributeComboBox = new JComboBox(this.default_attributes.toArray());
		    //jSelectDefaultAttributeComboBox.addActionListener(this);
		}
		return jSelectDefaultAttributeComboBox;
	}



    /**
     * This method initializes jSpecificOptionsPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJSpecificOptionsPanel() {
	if (jSpecificOptionsPanel == null) {
	    jSpecificOptionsPanel = new JPanel();
	    jSpecificOptionsPanel.setLayout(new FlowLayout());
	}
	return jSpecificOptionsPanel;
    }
    

    public String get_parser(){
	return this.getJSelectParserComboBox().getSelectedItem().toString();
    }

    public String getDatabaseName(){
	return this.getJExtDBNameTextField().getText();
    }
	
    public Vector<String> get_command() throws Exception{
	
	String t;
	
	Vector<String> commandVector = new Vector<String>();
	
	//commandVector.add(this.parserCommands.get(this.getJSelectParserComboBox().getSelectedItem()));
	commandVector.add((String)this.getJSelectParserComboBox().getSelectedItem());
	
	t = this.getJInputTextField().getText();
	if( t.trim().compareTo("")!=0 ){
	    commandVector.add("--input-identifier="+t);
	}
	else{
	    throw new Exception("You must specify the input path/file");
	}
	t=this.getJExtDBNameTextField().getText();
	if( t.trim().compareTo("")!=0 ){
	    commandVector.add("--database-name="+t);
	}
	else{
	    throw new Exception("You must specify the database name");
	}
	t=this.getJExtDBVersionTextField().getText();
	if( t.trim().compareTo("")!=0 ){
	    commandVector.add("--database-version="+t);
	}
	else{
	    throw new Exception("You must specify the database version");
	}
	
	if( this.getJVerboseCheckBox().isSelected() ){
	    commandVector.add("--verbose");
	}
	
	if( this.getJPromiscuousCheckBox().isSelected() ){
	    commandVector.add("--promiscuous");
	}

	if( this.getJTimeControlCheckBox().isSelected() ){
	    commandVector.add("--time-control");
	}

        if(this.jSelectParserComboBox.getSelectedItem().equals("psi_mi_2.5") || this.jSelectParserComboBox.getSelectedItem().equals("biopax_level_2") || this.jSelectParserComboBox.getSelectedItem().equals("generic")) {
	    if((String)this.getJSelectDefaultAttributeComboBox().getSelectedItem() != "N/A") 
		commandVector.add("--default-attribute="+(String)this.getJSelectDefaultAttributeComboBox().getSelectedItem());
        }
	
        commandVector.add("--optimize-for-parsing");

	return commandVector;
    }
	
}  //  @jve:decl-index=0:visual-constraint="10,10"
