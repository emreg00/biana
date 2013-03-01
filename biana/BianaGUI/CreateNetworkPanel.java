import javax.swing.JDialog;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.util.Vector;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
//import java.lang.Object;

public class CreateNetworkPanel extends CommandPanel implements ActionListener{

    private static final long serialVersionUID = 1L;
        
    private static final String ADD_RELATION_TOOLTIP = "<html>Add relations associated with biomolecules in this set and their partners:<br>" + 
                                                       "  You can:<ul>" + 
                                                       "<li> Specify relation types to be included (Click <i>Details</i>)</li>" +
                                                       "<li> Impose restrictions based on attributes while including relation partners (Click <i>Details</i>)</li>" +
                                                       "<li> Limit number of relations to look ahead for the biomolecules in the set <i>(depth)</i></li>" +
                                                       "<li> Include/exclude self relations</li>" +
                                                       "<li> Include/exclude relations at the last level</li>" +
                                                       "</ul><html>";

    private static final String ATTRIBUTE_NETWORK_TOOLTIP = "<html>Add attribute relations (relations between biomolecules sharing common attributes):<br>" + 
                                                       " You can:<ul>" + 
                                                       "<li> Select attributes for biomolecules to be connected (Click <i>Details</i>)</li>" +
                                                       "<li> Impose restrictions based on attributes while including relation partners (Click <i>Details</i>)</li>" +
                                                       "<li> Limit number of relations to look ahead for the biomolecules in the set <i>(depth)</i></li>" +
                                                       "<li> Include/exclude self relations</li>" +
                                                       "<li> Include/exclude relations at the last level</li>" +
                                                       "</ul><html>";

    private static final String RELATION_EXPANSION_TOOLTIP = "<html>Add inferred relations (a relation x-z is predicted if x=y under some consideration and y-z):<br>" + 
                                                       " You can:<ul>" + 
                                                       "<li> Specify relation types to be included (Click <i>Details</i>)</li>" +
                                                       "<li> Select attributes for biomolecules to be considered equivalent (Click <i>Details</i>)</li>" +
                                                       "<li> Limit number of relations to look ahead for the biomolecules in the set <i>(depth)</i>" +
                                                       "<li> Include/exclude self relations</li>" +
                                                       "<li> Include/exclude relations at the last level</li>" +
                                                       "</ul><html>";

    private static final String GROUP_RELATION_TOOLTIP = "<html>Add relations associated with biomolecules as groups:<br>" + 
                                                       "(This does not to display relations indivudally but rather insert relation partners as nodes and relations as a group)<br>" + 
                                                       "You can:<ul>" + 
                                                       "<li> Specify relation types to be included (Click <i>Details</i>)</li>" +
                                                       "<li> Impose restrictions based on attributes while including relation partners (Click <i>Details</i>)</li>" +
                                                       "<li> Limit number of relations to look ahead for the biomolecules in the set <i>(depth)</i></li>" +
                                                       "<li> Include/exclude self relations</li>" +
                                                       "<li> Include/exclude relations at the last level</li>" +
                                                       "</ul><html>";

    private static final String DEPTH_TOOLTIP = "Limit number of relations to look ahead for the biomolecules in the set <i>(depth)</i>";
    private static final String SELF_TOOLTIP = "Include/exclude self relations"; 
    private static final String LAST_TOOLTIP = "Include/exclude relations at the last level";

    private JPanel jPanel = null;
    private JLabel jLabel = null;
    private JScrollPane jScrollPane = null;
    private JList jList = null;
    private JList groupRelationTypesJList = null;
    private JList jRelationAttributesJlist = null;
    private JPanel jPanel1 = null;
    private JCheckBox jSelfRelationsCheckBox = null;
    private JCheckBox jLastLevelCheckBox = null;
    private JCheckBox jPredictionsCheckBox = null;
    private JCheckBox jAttributeRelationsCheckBox = null;
    private JCheckBox jRelationsCheckBox = null;
    private JCheckBox jGroupRelationsCheckBox = null;
    private JComboBox jComboBox = null;
    private JLabel jLabel1 = null;
    private String[] levels = { "0","1","2","3","4","5","6","7","8","9","10" };
    private Vector<String> available_relations = null;
    private Vector<String> available_relation_attributes = null;
    private Vector<String> relation_restrictions = null;

    private JDialog relationTypeDialog = null;
    private JDialog groupRelationsDialog = null;
    private JDialog attributeRelationsDialog = null;
    private JDialog expansionDialog = null;
    private String userEntitySetID= null;
    private HashSet<String> expansionRelationTypesSet = null;
    private HashMap<String, HashMap<String, HashMap<String, String>>> expansionAttributesMap = null;
    //private HashSet<String> attributeRelationSet = null;
    private HashMap<String, HashMap<String, HashMap<String, String>>> sharingAttributesMap = null;

    private BianaDatabase bDB = null;

    /**
     * This method initializes 
     * 
     */
    public CreateNetworkPanel(String pUserEntitySetID, Vector<String> pAvailableRelations, Vector<String> pAvailableRelationAttributes, BianaDatabase pBianaDB) {
	super();
	this.available_relations = pAvailableRelations;
	this.available_relation_attributes = pAvailableRelationAttributes;
	this.userEntitySetID = pUserEntitySetID;
	this.bDB = pBianaDB;
	this.expansionAttributesMap = new HashMap<String, HashMap<String, HashMap<String, String>>>();
	this.expansionRelationTypesSet = new HashSet<String>();
	this.sharingAttributesMap = new HashMap<String, HashMap<String, HashMap<String, String>>>();
        this.relation_restrictions = new Vector<String>();
	initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.setSize(new Dimension(244, 179));
	//this.add(getJPanel(), null);
	this.add(getJPanel1(), null);
    }
    
    @Override
	protected String get_command() {
	StringBuffer command = new StringBuffer();
	command.append("create_network( user_entity_set_id = \""+this.userEntitySetID+"\" , level = "+this.getJLevelsComboBox().getSelectedItem() );
	if( this.jRelationsCheckBox.isSelected() && this.getJList().getSelectedIndex()!=-1 ){
	    //if( this.getJList().getSelectedIndices().length<this.available_relations.size() ){
	    command.append(" , relation_type_list=[\""+Utilities.join(this.getJList().getSelectedValues(),"\",\"")+"\"]");
	    //}
	}
	//if( this.getRelationNetworkDialog().getRelationRestrictions().size() > 0 ) {
	//    command.append(" , externalEntityRelationAttributeRestrictions=[\""+Utilities.join(this.getRelationNetworkDialog().getRelationRestrictions(),"\",\"")+"\"]");
	if( this.jRelationsCheckBox.isSelected() && this.relation_restrictions.size() > 0 ) {
	    command.append(" , relation_attribute_restriction_list=["+Utilities.join(this.relation_restrictions,",")+"]");
        }
	command.append(" , include_relations_last_level = ");
	if( this.jLastLevelCheckBox.isSelected() ){
	    command.append("True");
	}
	else{
	    command.append("False");
	}
	command.append(" , use_self_relations = ");
	if( this.jSelfRelationsCheckBox.isSelected() ){
	    command.append("True");
	}
	else{
	    command.append("False");
	}

	if( this.jPredictionsCheckBox.isSelected() && this.expansionAttributesMap.size() > 0 ){
	    command.append(" , expansion_relation_type_list = [\""+Utilities.join(this.expansionRelationTypesSet,"\",\"")+"\"]");
            //command.append(" , expansionAttributesList = [["+Utilities.join(this.expansionAttributesMap,"],[")+"]], expansionLevel = 2 "); 
            String str = Utilities.convertAttributeParameterMapToList(this.expansionAttributesMap);
	    command.append(" , expansion_attribute_list = [ " + str + " ], expansion_level = 2 "); 
	}

	if( this.jAttributeRelationsCheckBox.isSelected() && this.sharingAttributesMap.size() > 0){
            //command.append(" , attributesNetworkList=[["+Utilities.join(this.attributeRelationSet,"],[")+"]] ");
            String str = Utilities.convertAttributeParameterMapToList(this.sharingAttributesMap);
            command.append(" , attribute_network_attribute_list=[ " + str + " ] ");
	}

	if( this.jGroupRelationsCheckBox.isSelected() && this.getGroupRelationTypesJList().getSelectedValues().length > 0){
	    command.append(" , group_relation_type_list=[\""+Utilities.join(this.getGroupRelationTypesJList().getSelectedValues(),"\",\"")+"\"]");
	}

	command.append(")");

	return command.toString();
    }

    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanel() {
	if (jPanel == null) {
	    FlowLayout flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    jLabel = new JLabel();
	    jLabel.setText("Use Relation Types");
	    jPanel = new JPanel();
	    jPanel.setLayout(flowLayout);
	    //jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
	    jPanel.add(jLabel, null);
	    jPanel.add(getJScrollPane(), null);
	}
	return jPanel;
    }

    /**
     * This method initializes jScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane() {
	if (jScrollPane == null) {
	    jScrollPane = new JScrollPane();
	    jScrollPane.setPreferredSize(new Dimension(150, 100));
	    jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
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
	    jList = new JList(this.available_relations);
	}
	return jList;
    }

    private JList getGroupRelationTypesJList(){
	if( groupRelationTypesJList == null ){
	    groupRelationTypesJList = new JList(this.available_relations);
	}
	return groupRelationTypesJList;
    }

    private JList getRelationAttributesJList() {
	if (jRelationAttributesJlist == null) {
	    jRelationAttributesJlist = new JList(this.available_relation_attributes);
	}
	return jRelationAttributesJlist;
    }

    /**
     * This method initializes jPanel1	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanel1() {
	if (jPanel1 == null) {
	    jPanel1 = new JPanel();
	    jPanel1.setLayout(new BoxLayout(jPanel1,BoxLayout.Y_AXIS));

	    //Relations network
	    FlowLayout flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    JPanel relationsJPanel = new JPanel();
	    relationsJPanel.setLayout(flowLayout);
	    this.jRelationsCheckBox = new JCheckBox();
	    jRelationsCheckBox.setText("Add relations");
	    jRelationsCheckBox.setSelected(false);
	    jRelationsCheckBox.addActionListener(this);
            jRelationsCheckBox.setToolTipText(ADD_RELATION_TOOLTIP);
	    relationsJPanel.add(jRelationsCheckBox,null);
	    JButton jb = new JButton("Details");
	    jb.setActionCommand("view_relations_details");
	    jb.addActionListener(this);
            jb.setToolTipText(ADD_RELATION_TOOLTIP);
	    relationsJPanel.add(jb);
            relationsJPanel.setToolTipText(ADD_RELATION_TOOLTIP);
	    jPanel1.add(relationsJPanel,null);

	    // Expansion predictions
	    flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    JPanel expansionJPanel = new JPanel();
	    expansionJPanel.setLayout(flowLayout);
	    this.jPredictionsCheckBox = new JCheckBox();
	    //jPredictionsCheckBox.setText("Add extrapolated relations based on shared attributes");
	    jPredictionsCheckBox.setText("Make predictions by sharing attributes");
	    jPredictionsCheckBox.setSelected(false);
	    jPredictionsCheckBox.addActionListener(this);
            jPredictionsCheckBox.setToolTipText(RELATION_EXPANSION_TOOLTIP);
	    expansionJPanel.add(jPredictionsCheckBox,null);
	    jb = new JButton("Details");
	    jb.setActionCommand("view_expansion_details");
	    jb.addActionListener(this);
            jb.setToolTipText(RELATION_EXPANSION_TOOLTIP);
	    expansionJPanel.add(jb);
            expansionJPanel.setToolTipText(RELATION_EXPANSION_TOOLTIP);
	    jPanel1.add(expansionJPanel,null);

	    // Add attribute relations
	    flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    JPanel attributeRelJPanel = new JPanel();
	    attributeRelJPanel.setLayout(flowLayout);
	    this.jAttributeRelationsCheckBox = new JCheckBox();
	    jAttributeRelationsCheckBox.setText("Add attribute relations");
	    jAttributeRelationsCheckBox.setSelected(false);
	    jAttributeRelationsCheckBox.addActionListener(this);
            jAttributeRelationsCheckBox.setToolTipText(ATTRIBUTE_NETWORK_TOOLTIP);
	    attributeRelJPanel.add(jAttributeRelationsCheckBox,null);
	    jb = new JButton("Details");
	    jb.setActionCommand("view_attribute_relations_details");
	    jb.addActionListener(this);
            jb.setToolTipText(ATTRIBUTE_NETWORK_TOOLTIP);
	    attributeRelJPanel.add(jb);
            attributeRelJPanel.setToolTipText(ATTRIBUTE_NETWORK_TOOLTIP);
	    jPanel1.add(attributeRelJPanel,null);

	    // Add relations as groups
	    flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    JPanel groupRelationsJPanel = new JPanel();
	    groupRelationsJPanel.setLayout(flowLayout);
	    this.jGroupRelationsCheckBox = new JCheckBox();
	    jGroupRelationsCheckBox.setText("Add relations as GROUPS");
	    jGroupRelationsCheckBox.setSelected(false);
	    jGroupRelationsCheckBox.addActionListener(this);
	    jGroupRelationsCheckBox.setToolTipText(GROUP_RELATION_TOOLTIP);
	    groupRelationsJPanel.add(jGroupRelationsCheckBox,null);
	    jb = new JButton("Details");
	    jb.setActionCommand("view_group_relations_details");
	    jb.addActionListener(this);
	    jb.setToolTipText(GROUP_RELATION_TOOLTIP);
	    groupRelationsJPanel.add(jb);
	    groupRelationsJPanel.setToolTipText(GROUP_RELATION_TOOLTIP);
	    jPanel1.add(groupRelationsJPanel,null);
	    
	    // Depth
	    flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    JPanel depthJPanel = new JPanel();
	    depthJPanel.setLayout(flowLayout);
	    jLabel1 = new JLabel();
	    jLabel1.setText("Depth");
	    depthJPanel.add(jLabel1,null);
            getJLevelsComboBox().setToolTipText(DEPTH_TOOLTIP);
	    depthJPanel.add(getJLevelsComboBox(),null);
            depthJPanel.setToolTipText(DEPTH_TOOLTIP);
	    jPanel1.add(depthJPanel);
	    
	    // Self relations
	    flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    JPanel selfrelationsJPanel = new JPanel();
	    selfrelationsJPanel.setLayout(flowLayout);
	    this.jSelfRelationsCheckBox = new JCheckBox();
	    this.jSelfRelationsCheckBox.setText("Use self-relations");
	    this.jSelfRelationsCheckBox.setSelected(false);
            this.jSelfRelationsCheckBox.setToolTipText(SELF_TOOLTIP);
            selfrelationsJPanel.setToolTipText(SELF_TOOLTIP);
	    selfrelationsJPanel.add(this.jSelfRelationsCheckBox, null);
	    jPanel1.add(selfrelationsJPanel,null);

	    // Last level relations
	    flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    JPanel lastLevelJPanel = new JPanel();
	    lastLevelJPanel.setLayout(flowLayout);
	    this.jLastLevelCheckBox = new JCheckBox();
	    this.jLastLevelCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
	    jLastLevelCheckBox.setText("Include relations between elements at last level");
	    jLastLevelCheckBox.setSelected(true);
            jLastLevelCheckBox.setToolTipText(LAST_TOOLTIP);
	    lastLevelJPanel.add(jLastLevelCheckBox,null);
            lastLevelJPanel.setToolTipText(LAST_TOOLTIP);
	    jPanel1.add(lastLevelJPanel,null);
	}
	return jPanel1;
    }
    
    /**
     * This method initializes jComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getJLevelsComboBox() {
	if (jComboBox == null) {
	    jComboBox = new JComboBox(this.levels);
	    jComboBox.setSelectedIndex(1);
	}
	return jComboBox;
    }
    
    @Override
	protected boolean check_parameters() {
	if( this.jRelationsCheckBox.isSelected()==false && this.jPredictionsCheckBox.isSelected()==false && this.jAttributeRelationsCheckBox.isSelected()==false && this.jGroupRelationsCheckBox.isSelected()==false ){
	    JOptionPane.showMessageDialog(this,
					  "You must select at least one type of network creation",
					  "Relation network error",
					  JOptionPane.ERROR_MESSAGE);
	    return false;
	}
	if( this.jRelationsCheckBox.isSelected() ){
	    if( this.getJList().getSelectedIndex()==-1 ){
		JOptionPane.showMessageDialog(this,
					      "You must select at least one type type of relation",
					      "Relation network error",
					      JOptionPane.ERROR_MESSAGE);
		return false;
	    }
	}
	if( this.jPredictionsCheckBox.isSelected() ){
	    if( this.expansionRelationTypesSet.size()==0 ){
		JOptionPane.showMessageDialog(this,
					      "You must select at least one type of relation for expansion",
					      "Relation expansion error",
					      JOptionPane.ERROR_MESSAGE);
		return false;
	    }
	    if( this.expansionAttributesMap.size() == 0 ){
		JOptionPane.showMessageDialog(this,
					      "You must select at least one attribute to expand  relations",
					      "Relation expansion error",
					      JOptionPane.ERROR_MESSAGE);
		return false;
	    }
	}
	if( this.jAttributeRelationsCheckBox.isSelected() ){
	    if( this.sharingAttributesMap.size()==0 ){
		JOptionPane.showMessageDialog(this,
					      "You must select at least one attribute to do an attribute network",
					      "Attribute Relation Network error",
					      JOptionPane.ERROR_MESSAGE);
		return false;
	    }
	}
	if( this.jGroupRelationsCheckBox.isSelected() ) {
	    if (this.getGroupRelationTypesJList().getSelectedValues().length == 0) {
		JOptionPane.showMessageDialog(this,
					      "You must select at least one attribute to create groups",
					      "Relation Group Network error",
					      JOptionPane.ERROR_MESSAGE);
		return false;
	    }
	}
	return true;
    }


    private JDialog getRelationNetworkDialog(){
	if( this.relationTypeDialog == null ){
	    this.relationTypeDialog = new RelationNetworkDialog(this, this.getJList(), this.getRelationAttributesJList(), this.relation_restrictions);
	}
	return this.relationTypeDialog;
    }

    private JDialog getGroupRelationsDialog(){
	if( this.groupRelationsDialog == null ){
	    this.groupRelationsDialog = new RelationNetworkDialog(this, this.getGroupRelationTypesJList(), this.getRelationAttributesJList(), this.relation_restrictions);
	}
	return this.groupRelationsDialog;
    }

    private JDialog getAttributeRelationsDialog(){
	if( this.attributeRelationsDialog == null ){
	    //this.attributeRelationsDialog = new AttributeNetworkDialog(this.bDB, this.attributeRelationSet);
            this.attributeRelationsDialog = new AttributeNetworkDialog2(this, this.bDB, this.sharingAttributesMap);
	}
	return this.attributeRelationsDialog;
    }
    
    private JDialog getExpansionDialog(){
	if( this.expansionDialog == null ){
	    //this.expansionDialog = new AttributeExpansionDialog(this.bDB, this.expansionRelationTypesSet, this.expansionAttributesSet);
	    //this.expansionDialog = new AttributeExpansionDialog2(new javax.swing.JFrame(), this.bDB, this.expansionRelationTypesSet, this.expansionAttributesMap); // JFrame("Predict Relations")
            this.expansionDialog = new AttributeExpansionDialog2(this, this.bDB, this.expansionRelationTypesSet, this.expansionAttributesMap);
	}
	return this.expansionDialog;
    }

    public void actionPerformed(ActionEvent e){
	if( "view_attribute_relations_details".equals(e.getActionCommand() ) ){
	    this.getAttributeRelationsDialog().setVisible(true);
	}
	else if( "view_expansion_details".equals(e.getActionCommand() ) ){
	    this.getExpansionDialog().setVisible(true);
	}
	else if( "view_relations_details".equals(e.getActionCommand() ) ){
	    this.getRelationNetworkDialog().setVisible(true);
        }
	else if( "view_group_relations_details".equals(e.getActionCommand() ) ){
	    this.getGroupRelationsDialog().setVisible(true);
        }
	else if( e.getSource()== jAttributeRelationsCheckBox ){
	    if( jAttributeRelationsCheckBox.isSelected() ){
		this.getAttributeRelationsDialog().setVisible(true);
	    }
	}
	else if( e.getSource()==jPredictionsCheckBox ){
	    if( jPredictionsCheckBox.isSelected() ){
		this.getExpansionDialog().setVisible(true);
	    }
	}
	else if( e.getSource()==jRelationsCheckBox ){
	    if( jRelationsCheckBox.isSelected() ){
		this.getRelationNetworkDialog().setVisible(true);
	    }
	}
	else if( e.getSource()==jGroupRelationsCheckBox ){
	    if( jGroupRelationsCheckBox.isSelected() ){
		this.getGroupRelationsDialog().setVisible(true);
	    }
	}
    }
}
