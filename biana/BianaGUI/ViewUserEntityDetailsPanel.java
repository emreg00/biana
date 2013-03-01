

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JList;

import javax.swing.JOptionPane;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultListModel;

import javax.swing.JComboBox;

import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

import javax.swing.BorderFactory;

//import java.io.File;
//import javax.swing.filechooser.*;

public class ViewUserEntityDetailsPanel extends CommandPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JScrollPane jScrollPane = null;
    private JList jList = null;
    
    private Vector<String> available_attributes = null;
    private JLabel jLabel = null;
    private JPanel jPanel = null;
    private JLabel jLabel1 = null;

    private JLabel jLabelHidden = null;
    //private JLabel jLabelHidden2 = null;
    private JLabel jLabelExport = null;
    private JCheckBox jCheckBoxExport = null;
    private JTextField jTextField1 = null;
    private JLabel jLabelFile = null;
    private JPanel jPanelExport = null;
    private JButton jButton1 = null;

    private JCheckBox oneValuePerAttributeCheckBox = null;

    private JRadioButton JRadioButtonOneValuePerAttribute = null;
    private JRadioButton JRadioButtonOnlyDefaultsPerAttribute = null;
    private JRadioButton JRadioButtonAllValuesPerAttribute = null;
    private ButtonGroup group = new ButtonGroup();

    private String setID = null;

    private String method = null;
    private String selected_format_description = "";

    /**
     * This method initializes 
     * pMethod can be "all", to show the all the details of the user entity set, or "selected", to show the selected user entities
     */
    public ViewUserEntityDetailsPanel(String pSetID, Vector<String> pAvailableAttributes, String pMethod) {
	super();
	this.available_attributes = pAvailableAttributes;
	this.setID = pSetID;
	this.method = pMethod;
	initialize();
    }

	/**
	 * This method initializes this
	 * 
	 */
        private void initialize() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            //this.setSize(new Dimension(310, 365)); //310, 355)); //310, 156));
            this.add(getJPanel(), null);
            jLabel = new JLabel();
            jLabel.setText("Select attributes");
            //this.add(jLabel, null);
            this.add(getJScrollPane(), null);  //Attribute scrollpanel
	    this.add(getOneValuePerAttributePanel(), null);
	    this.add(getJPanelExport(), null); // Export panel
        }


    private JPanel getOneValuePerAttributePanel(){
	
	JPanel checkboxpanel = new JPanel();
	checkboxpanel.setLayout(new FlowLayout());
	checkboxpanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Display ... value(s) per attribute:"));

	/*
	oneValuePerAttributeCheckBox = new JCheckBox("Show a single value per attribute");
	oneValuePerAttributeCheckBox.setSelected(true);
	checkboxpanel.add(oneValuePerAttributeCheckBox);
	*/

	//jLabel = new JLabel();
	//jLabel.setText("Representation:");
	JRadioButtonOneValuePerAttribute = new JRadioButton("Single");
	JRadioButtonOnlyDefaultsPerAttribute = new JRadioButton("Only Native");
	JRadioButtonAllValuesPerAttribute = new JRadioButton("All");
	JRadioButtonOneValuePerAttribute.setSelected(true);
        group.add(JRadioButtonOneValuePerAttribute);
        group.add(JRadioButtonOnlyDefaultsPerAttribute);
        group.add(JRadioButtonAllValuesPerAttribute);
        //checkboxpanel.add(jLabel);
        checkboxpanel.add(JRadioButtonOneValuePerAttribute);
        checkboxpanel.add(JRadioButtonOnlyDefaultsPerAttribute);
        checkboxpanel.add(JRadioButtonAllValuesPerAttribute);
	//checkboxpanel.setToolTipText("<html>Display ... value(s) per attribute for all biomolecules inside the user entity set: <ul><li>Single: Shows the most frequent value for the attribute in concern</li><li>Only Native: Shows only the values coming from databases that provide the attribute in concern as the primary identifier</li><li>All: Shows all associated values for the attribute in concern</li></ul></html>");
	checkboxpanel.setToolTipText("<html>Display ... value(s) per attribute for all biomolecules inside the user entity set: <ul><li>Single: Shows the most frequent value for the attribute in concern</li><li>Only Native: Shows only the values given as primary identifiers by databases</li><li>All: Shows all associated values for the attribute in concern</li></ul></html>");
	
	return checkboxpanel;
    }


	@Override
	protected boolean check_parameters() {
	    /*if( this.getJList().getSelectedIndex() == -1 ){
		return false;
		}*/
	    return true;
	}

	@Override
	protected String get_command() {
	    System.err.println("Method: "+this.method);
	    String selected_attr = "", command_str = "";

	    StringBuffer parametersStringBuffer = new StringBuffer();
	    
	    if( this.getJList().getSelectedIndex() == -1 ){
		parametersStringBuffer.append(", attributes = []");
	    }
	    else{
		parametersStringBuffer.append(", attributes = [\""+Utilities.join(this.jList.getSelectedValues(),"\",\"")+"\"]");
	    }
	    
	    if( this.method.equals("all") ){
		parametersStringBuffer.append(", only_selected=False");
            } else if( this.method.equals("selected") ){
		parametersStringBuffer.append(", only_selected=True");
            }
            else{
               return "deliver_error_message(\"Confused with selection!\")";
            }

	    //if( oneValuePerAttributeCheckBox.isSelected() ){
	    if( JRadioButtonOneValuePerAttribute.isSelected() ) {
		parametersStringBuffer.append(", output_1_value_per_attribute=True");
		parametersStringBuffer.append(", output_only_native_values=False");
		parametersStringBuffer.append(", output_only_unique_values=False");
	    }
	    else if(JRadioButtonOnlyDefaultsPerAttribute.isSelected()) {
		parametersStringBuffer.append(", output_1_value_per_attribute=False");
		//parametersStringBuffer.append(", output_only_native_values=True");
		parametersStringBuffer.append(", output_only_native_values=False");
		parametersStringBuffer.append(", output_only_unique_values=True");
	    } else {
		parametersStringBuffer.append(", output_1_value_per_attribute=False");
		parametersStringBuffer.append(", output_only_native_values=False");
		parametersStringBuffer.append(", output_only_unique_values=False");
	    }
           
            if( this.getJCheckBoxExport().isSelected() ) {
                if(getJTextField1().getText() != "") {
                //if(getJLabelFile().getText() != "") {
                    String type = "";
                    if(selected_format_description.equals(SharedObjects.FASTA_PROTEIN_SEQUENCE_DESCRIPTION)) {
                            type = "proteinsequence";
                            command_str = "output_user_entity_set_sequences_in_fasta(user_entity_set_id=\""+this.setID+"\", out_method = open(\""+getJTextField1().getText()+"\", 'w').write, type=\""+type+"\", include_tags_info=True" + parametersStringBuffer.toString()+")";
                    } else if(selected_format_description.equals(SharedObjects.FASTA_NUCLEOTIDE_SEQUENCE_DESCRIPTION)) {
                            type = "nucleotidesequence";
                            command_str = "output_user_entity_set_sequences_in_fasta(user_entity_set_id=\""+this.setID+"\", out_method = open(\""+getJTextField1().getText()+"\", 'w').write, type=\""+type+"\", include_tags_info=True" + parametersStringBuffer.toString() + ")";
                    } else if(selected_format_description.equals(SharedObjects.TAB_SEPARATED_DESCRIPTION)) {
                            type = "tabulated";
                            command_str = "output_user_entity_set_details(user_entity_set_id=\""+this.setID+"\""+selected_attr+", output_format=\""+type+"\", out_method=open(\""+getJTextField1().getText()+"\", 'w').write" + parametersStringBuffer.toString() + ")";
                    } else { 
                        return "deliver_error_message(\"Unrecognized output format\")";
                    }
                } else {
                    return "deliver_error_message(\"Please specify a proper file name!\")";
                }
            } else {
		command_str = "output_user_entity_set_details(user_entity_set_id=\""+this.setID+"\", output_format=\"xml\", include_command_in_rows=True" + parametersStringBuffer.toString() + ")";
            }

            return command_str;
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
			jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.setPreferredSize(new Dimension(200, 130));
			jScrollPane.setViewportView(getJList());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJList() {
	    if (jList == null) {
		jList = new JList(this.available_attributes);
	    }
		return jList;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jLabel1 = new JLabel();
			//jLabel1.setText("From database");
			jLabel1.setText("Select attributes to view/export:");
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			jPanel = new JPanel();
			jPanel.setLayout(flowLayout);
			jPanel.add(jLabel1, null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanelExport
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelExport() {
		if (jPanelExport == null) {
                    FlowLayout flowLayout = new FlowLayout();
                    flowLayout.setAlignment(FlowLayout.LEFT);
                    jPanelExport = new JPanel();
                    jPanelExport.setLayout(flowLayout);
                    //jLabelHidden2 = new JLabel("                                          ");
                    //jPanelExport.add(jLabelHidden2, null);
                    jPanelExport.add(getJCheckBoxExport(), null);
                    jLabelHidden = new JLabel("                          ");
                    //jLabelHidden.setVisible(false);
                    jPanelExport.add(jLabelHidden, null);
                    jLabelExport = new JLabel();
                    jLabelExport.setText("File name:");
                    jPanelExport.add(jLabelExport, null);
                    jPanelExport.add(getJTextField1(), null);
                    //jPanelExport.add(getJLabelFile(), null);
                    jPanelExport.add(getJButton1(), null);
                    SharedObjects.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    SharedObjects.fileChooser.setDialogTitle("Select file name & format to export data");
                    jLabelExport.setEnabled(false);
                    getJTextField1().setEnabled(false);
                    //getJLabelFile().setEnabled(false);
                    getJButton1().setEnabled(false);
                    //jPanelExport.add(SharedObjects.fileChooser);
		    jPanelExport.setToolTipText("<html>Export user entity set data into a file in one of the following formats: <br><ul><li>TAB Seperated format</li><li>FASTA format (either amino acid or nucleotide sequences)</li></ul></html>");
		}
		return jPanelExport;
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
                jButton1.addActionListener(this);
                jButton1.setActionCommand("select_export_file");
            }
            return jButton1;
        }

	/**
	 * This method initializes jCheckBoxExport
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getJCheckBoxExport() {
		if (jCheckBoxExport == null) {
                    jCheckBoxExport = new JCheckBox("Export data into file");
                    //jCheckBoxExport.setText("Export data into file");
                    jCheckBoxExport.addActionListener(this);
                    jCheckBoxExport.setActionCommand("check_export");
		}
		return jCheckBoxExport;
	}

        /**
         * This method initializes jLabelFile	
         * 	
         * @return javax.swing.JLabel	
         */
        private JLabel getJLabelFile() {
            if (jLabelFile == null) {
                jLabelFile = new JLabel("                       ");
            }
            return jLabelFile;
        }

        /**
         * This method initializes jTextField1	
         * 	
         * @return javax.swing.JTextField	
         */
        private JTextField getJTextField1() {
            if (jTextField1 == null) {
                jTextField1 = new JTextField();
                jTextField1.setColumns(15);
                jTextField1.setText("");
            }
            return jTextField1;
        }

        public void actionPerformed(ActionEvent e) {
            if( e.getActionCommand()=="select_export_file" ){
                SharedObjects.fileChooser.setAcceptAllFileFilterUsed(false);
                //SharedObjects.fileChooser.addChoosableFileFilter(SharedObjects.NucleotideSequenceFilter);
                //SharedObjects.fileChooser.addChoosableFileFilter(SharedObjects.ProteinSequenceFilter);
                //SharedObjects.fileChooser.addChoosableFileFilter(SharedObjects.TabulatedFilter);
                SharedObjects.fileChooser.addChoosableFileFilter(SharedObjects.filterNuc);
                SharedObjects.fileChooser.addChoosableFileFilter(SharedObjects.filterPro);
                SharedObjects.fileChooser.addChoosableFileFilter(SharedObjects.filterTab);
                int returnVal = SharedObjects.fileChooser.showOpenDialog(this);
                selected_format_description = SharedObjects.fileChooser.getFileFilter().getDescription();
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    this.getJTextField1().setText(SharedObjects.fileChooser.getSelectedFile().getAbsolutePath());
                    //this.getJLabelFile().setText(SharedObjects.fileChooser.getSelectedFile().getAbsolutePath());
                }
                SharedObjects.fileChooser.resetChoosableFileFilters();
            }
            if( e.getActionCommand()=="check_export" ){
                if( this.getJCheckBoxExport().isSelected() ) {
                    jLabelExport.setEnabled(true);
                    getJTextField1().setEnabled(true);
                    //getJLabelFile().setEnabled(true);
                    getJButton1().setEnabled(true);
                } else {
                    jLabelExport.setEnabled(false);
                    getJTextField1().setEnabled(false);
                    //getJLabelFile().setEnabled(false);
                    getJButton1().setEnabled(false);
                }
            }
        }
		

}
