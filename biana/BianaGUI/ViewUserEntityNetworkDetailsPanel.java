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

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.Component;
import javax.swing.Box;

public class ViewUserEntityNetworkDetailsPanel extends CommandPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JScrollPane jScrollPane = null;
    private JScrollPane jRelationAttributesScrollPane = null;
    private JList jList = null;
    private JList jRelationAttributesJList = null;
    
    private Vector<String> available_attributes = null;
    private Vector<String> available_relation_attributes = null;
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

    private String setID = null;
    private String method = null;
    private String selected_format_description = "";

    /**
     * This method initializes 
     * pMethod can be "all", to show the all the details of the user entity set, or "selected", to show the selected user entities
     */
    public ViewUserEntityNetworkDetailsPanel(String pSetID, Vector<String> pAvailableAttributes, Vector<String> pAvailableRelationAttributes, String pMethod) {
	super();
	this.available_attributes = pAvailableAttributes;
	this.available_relation_attributes = pAvailableRelationAttributes;
	this.setID = pSetID;
	this.method = pMethod;
	initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); //BoxLayout.PAGE_AXIS)); 
        this.setSize(new Dimension(310, 390)); //370 //(310, 300));
        this.add(getJPanel(), null);
        //JLabel jLabelPart = new JLabel("Select participant attributes to be included");
        //jLabelPart.setAlignmentX(Component.LEFT_ALIGNMENT);
        //this.add(jLabelPart, null);
        //this.add(Box.createHorizontalGlue());
        this.add(getJScrollPane(), null);
	//this.add(new JLabel("Select relation attributes to be included"), null);
        JLabel jLabelRel = new JLabel();
        jLabelRel.setText("Select relation attributes to be included:");
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        JPanel jPanelRel = new JPanel();
        jPanelRel.setLayout(flowLayout);
        jPanelRel.add(jLabelRel, null);
        this.add(jPanelRel);
	this.add(getJRelationAttributesScrollPane(),null);
	this.add(getOneValuePerAttributePanel(), null);
        this.add(getJPanelExport(), null);
    }
    
    @Override
	protected boolean check_parameters() {
	return true;
    }

    @Override
	protected String get_command() {
	//System.err.println("Method: "+this.method);
	String selected_attr = "";
	String selected_relation_attr = "";
	String all_or_selected = "";
        String command_str = "";
	StringBuffer parametersStringBuffer = new StringBuffer();

	if( this.getJList().getSelectedIndex() != -1 ){
	    selected_attr = ", node_attributes=[\""+Utilities.join(this.jList.getSelectedValues(),"\",\"")+"\"]";
	}
	if( this.getRelationAttributesJList().getSelectedIndex() != -1 ){
	    selected_relation_attr = ", relation_attributes=[\""+Utilities.join(this.getRelationAttributesJList().getSelectedValues(),"\",\"")+"\"]";
	}
	if( this.method.equals("selected") ){
	    all_or_selected= ", only_selected=True";
	}
	if( oneValuePerAttributeCheckBox.isSelected() ){
	    parametersStringBuffer.append(", output_1_value_per_attribute=True");
	}
	else{
	    parametersStringBuffer.append(", output_1_value_per_attribute=False");
	}
        if( this.getJCheckBoxExport().isSelected() ) {
            if(getJTextField1().getText() != "") {
                String type = "";
                if(selected_format_description.equals(SharedObjects.TAB_SEPARATED_DESCRIPTION)) {
                        type = "tabulated";
                        command_str = "output_user_entity_set_network(user_entity_set_id=\""+this.setID+"\", output_format=\""+type+"\""+selected_attr+selected_relation_attr+all_or_selected+", out_method=open(\""+getJTextField1().getText()+"\", 'w').write" + parametersStringBuffer.toString()+")";
                } else { 
                    return "deliver_error_message(\"Unrecognized output format\")";
                }
            } else {
                return "deliver_error_message(\"Please specify a proper file name!\")";
            }
        } else {
            command_str = "output_user_entity_set_network(user_entity_set_id=\""+this.setID+"\""+selected_attr+selected_relation_attr+all_or_selected+", output_format=\"xml\", include_command_in_rows=True" + parametersStringBuffer.toString()+")";
            }
        return command_str;
    }

    private JPanel getOneValuePerAttributePanel(){
	JPanel checkboxpanel = new JPanel();
	checkboxpanel.setLayout(new FlowLayout());
	oneValuePerAttributeCheckBox = new JCheckBox("Show a single value per attribute");
	oneValuePerAttributeCheckBox.setSelected(true);
	checkboxpanel.add(oneValuePerAttributeCheckBox);
	
	return checkboxpanel;
    }


    private JList getRelationAttributesJList() {
	if (jRelationAttributesJList == null) {
	    jRelationAttributesJList = new JList(this.available_relation_attributes);
	}
	return jRelationAttributesJList;
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

    private JScrollPane getJRelationAttributesScrollPane() {
	if (jRelationAttributesScrollPane == null) {
	    jRelationAttributesScrollPane = new JScrollPane();
	    jRelationAttributesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    jRelationAttributesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    jRelationAttributesScrollPane.setPreferredSize(new Dimension(200, 130));
	    jRelationAttributesScrollPane.setViewportView(getRelationAttributesJList());
	}
	return jRelationAttributesScrollPane;
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
	    jLabel1.setText("Select participant attributes to be included:");
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
                SharedObjects.fileChooser.setToolTipText("Select file name & format to export data");
                jLabelExport.setEnabled(false);
                getJTextField1().setEnabled(false);
                //getJLabelFile().setEnabled(false);
                getJButton1().setEnabled(false);
                //jPanelExport.add(SharedObjects.fileChooser);
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
