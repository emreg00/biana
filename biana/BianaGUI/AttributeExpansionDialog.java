import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

import javax.swing.JComboBox;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.util.HashSet;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JList;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.DefaultListModel;
import java.util.Iterator;

import java.util.Vector;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.JDialog;

public class AttributeExpansionDialog extends JDialog implements ActionListener, ListSelectionListener {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JTextField jNameTextField = null;
    
    private BianaDatabase biana_database = null;
    private JPanel jSelectAttributesPanel = null;
    private JLabel jCrossLabel = null;
    private JLabel jCrossLabel2 = null;
    private JPanel jTablePanel = null;
    private JScrollPane jScrollPane = null;
    private JTable jTable = null;
    
    private JScrollPane jAttributesScrollPane1 = null;
    private JScrollPane jRelationsScrollPane1 = null;
    private JButton jAddButton = null;
    private JPanel jAttributePanel = null;
    private JPanel jRelationTypePanel = null;
    private JList jAttributeList = null;
    private JList jRelationTypeList = null;
    private HashSet<String> expansionRelationTypesSet = null;
    private HashSet<String> expansionAttributesSet = null;
    private JPanel jButtonsPanel = null;

    /**
     * This method initializes 
     * 
     */
    public AttributeExpansionDialog(BianaDatabase bDB, HashSet<String> pExpansionRelationTypesSet, HashSet<String> pExpansionAttributesSet) {
	super();
	this.biana_database = bDB;
	this.expansionRelationTypesSet = pExpansionRelationTypesSet;
	this.expansionAttributesSet = pExpansionAttributesSet;
	initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
	JPanel t = new JPanel();
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        //t.setSize(new Dimension(432, 316));
	t.add(getJRelationTypePanel(),null);
        t.add(getJSelectAttributesPanel(), null);
	t.add(new JLabel("Expansion attributes:"));
        t.add(getJTablePanel(), null);
	t.add(getJButtonsPanel(), null);
	this.setSize(300,450);
	this.setResizable(false);
	this.setContentPane(t);
    }

    private JPanel getJButtonsPanel(){
	if (jButtonsPanel == null) {
	    FlowLayout flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.RIGHT);
	    jButtonsPanel = new JPanel();
	    jButtonsPanel.setLayout(flowLayout);
	    JButton okbutton = new JButton();
	    okbutton.setText("Accept");
	    okbutton.addActionListener(this);
	    okbutton.setActionCommand("accept");
	    jButtonsPanel.add(okbutton, null);
	    JButton resetbutton = new JButton();
	    resetbutton.setText("Reset");
	    resetbutton.setActionCommand("reset");
	    resetbutton.addActionListener(this);
	    jButtonsPanel.add(resetbutton, null);
	}
	return this.jButtonsPanel;
    }

    
    /**
     * This method initializes jNameTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextField() {
	if (jNameTextField == null) {
	    jNameTextField = new JTextField();
	    jNameTextField.setColumns(30);
	}
	return jNameTextField;
    }
    
    
    public void actionPerformed(ActionEvent e) {
	if( e.getSource()==this.getJAddButton() ){
	    if( this.getJAttributeList().getSelectedIndex() == -1 ){
		JOptionPane.showMessageDialog(this,
					      "You must select at least one relation type and one attribute expansion",
					      "BIANA ERROR",
					      JOptionPane.ERROR_MESSAGE);
		return;
            }
	    String attr_str = Utilities.join(this.getJAttributeList().getSelectedValues(),",");
	    this.expansionAttributesSet.add("\""+Utilities.join(this.getJAttributeList().getSelectedValues(),"\",\"")+"\"");
	    ((DefaultTableModel)this.jTable.getModel()).addRow(new Object[]{attr_str.toString()});
	}
	else if( "reset".equals(e.getActionCommand()) ){
	    this.getJAttributeList().clearSelection();
	    this.getJRelationTypeList().clearSelection();
	    this.expansionRelationTypesSet.clear();
	    this.expansionAttributesSet.clear();
	    while(true){
		try{
		    ((DefaultTableModel)this.jTable.getModel()).removeRow(0);
		}
		catch( Exception exc ){
		    break;
		}
	    }
	}
	else if( "accept".equals(e.getActionCommand() ) ){
	    this.setVisible(false);
	}
    }

    /**
     * This method initializes jSelectAttributesPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJSelectAttributesPanel() {
	if (jSelectAttributesPanel == null) {
	    jCrossLabel = new JLabel();
	    jCrossLabel.setText("Select attributes");
	    jSelectAttributesPanel = new JPanel();
	    jSelectAttributesPanel.setLayout(new BorderLayout());
	    jSelectAttributesPanel.add(getJAttributePanel(), BorderLayout.WEST);
	}
	return jSelectAttributesPanel;
    }

    /**
     * This method initializes jTablePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJTablePanel() {
	if (jTablePanel == null) {
	    jTablePanel = new JPanel();
	    jTablePanel.setLayout(new BorderLayout());
	    jTablePanel.add(getJScrollPane(), BorderLayout.NORTH);
	}
	return jTablePanel;
    }
    
    /**
     * This method initializes jScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane() {
	if (jScrollPane == null) {
	    jScrollPane = new JScrollPane();
	    jScrollPane.setPreferredSize(new Dimension(432, 100));
	    jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    jScrollPane.setViewportView(getJTable());
	}
	return jScrollPane;
    }
    
    /**
     * This method initializes jTable	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getJTable() {
	if (jTable == null) {
	    
	    DefaultTableModel model = new DefaultTableModel() {
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
			
	    model.addColumn("Attributes");
	    
	    jTable = new JTable(model){
		    /**
		     * 
		     */
		    private static final long serialVersionUID = 1L;
		    
		    @Override
			public boolean isCellEditable(int row, int column) {
			// TODO Auto-generated method stub
			//return false;
			return true;
		    }
		};    
	}
	return jTable;
    }
    
    private void update_fields(){
	
    }
    
    /**
     * This method initializes jAttributesScrollPane1	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJAttributesScrollPane() {
	if (jAttributesScrollPane1 == null) {
	    jAttributesScrollPane1 = new JScrollPane();
	    jAttributesScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    jAttributesScrollPane1.setViewportView(getJAttributeList());
	    jAttributesScrollPane1.setPreferredSize(new Dimension(120, 120));
	}
	return jAttributesScrollPane1;
    }


    private JScrollPane getJRelationsTypeScrollPane() {
	if( jRelationsScrollPane1 == null ){
	    jRelationsScrollPane1 = new JScrollPane();
	    jRelationsScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    jRelationsScrollPane1.setViewportView(getJRelationTypeList());
	    jRelationsScrollPane1.setPreferredSize(new Dimension(120, 120));
	}
	return jRelationsScrollPane1;
    }
    
    /**
     * This method initializes jAddButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJAddButton() {
	if (jAddButton == null) {
	    jAddButton = new JButton();
	    jAddButton.setText("Add");
	    jAddButton.addActionListener(this);
	}
	return jAddButton;
    }

    /**
     * This method initializes jCrossingPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJAttributePanel() {
	if (jAttributePanel == null) {
	    FlowLayout flowLayout2 = new FlowLayout();
	    flowLayout2.setAlignment(FlowLayout.LEFT);
	    jAttributePanel = new JPanel();
	    jAttributePanel.setLayout(flowLayout2);
	    jAttributePanel.add(jCrossLabel, null);
	    jAttributePanel.add(getJAttributesScrollPane(), null);
	    jAttributePanel.add(getJAddButton(), null);
	}
	return jAttributePanel;
    }

    /**
     * This method initializes jCrossingPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJRelationTypePanel() {
	if (jRelationTypePanel == null) {
	    FlowLayout flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    jRelationTypePanel = new JPanel();
	    jRelationTypePanel.setLayout(flowLayout);
	    jRelationTypePanel.add(new JLabel("Expand relation types"), null);
	    jRelationTypePanel.add(getJRelationsTypeScrollPane(), null);
	}
	return jRelationTypePanel;
    }

    /**
     * This method initializes jAttributeList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getJAttributeList() {
	if (jAttributeList == null) {
	    jAttributeList = new JList(ExternalDatabase.getAllAttributes("eE",this.biana_database.getAvailableExternalDatabases()));
            jAttributeList.addListSelectionListener(this);
	} 
	return jAttributeList;
    }

     private JList getJRelationTypeList() {
	if (jRelationTypeList == null) {
	    jRelationTypeList = new JList(ExternalDatabase.getAlleErTypes(this.biana_database.getAvailableExternalDatabases()));
	    jRelationTypeList.addListSelectionListener(this);
	}
	return jRelationTypeList;
    }


    public void valueChanged(ListSelectionEvent e){
        if(e.getSource() == getJRelationTypeList()) {
        	this.expansionRelationTypesSet.clear();
	        this.expansionRelationTypesSet.addAll((Collection)Arrays.asList(this.getJRelationTypeList().getSelectedValues()));
        } else if(e.getSource() == getJAttributeList()) {
                if(getJAttributeList().getModel().getElementAt(getJAttributeList().getLeadSelectionIndex()).equals("proteinsequence")) {
                    System.out.println("Sequence");
                }
        }
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
