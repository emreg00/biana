
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;

import javax.swing.JComboBox;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.util.HashSet;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JList;

import java.util.Comparator;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import java.util.Iterator;

import java.util.Vector;

import javax.swing.JOptionPane;

public class NewUnificationProtocolPanel extends CommandPanel implements ActionListener {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JPanel jNamePanel = null;
    private JLabel jNameLabel = null;
    private JTextField jNameTextField = null;
    private JPanel jBianaDBPanel = null;
    private JLabel jBianaDBLabel = null;
    private JComboBox jBianaDBComboBox = null;
    
    private BianaDatabase[] available_databases = null;
    private JPanel jSelectCrossingsPanel = null;
    private JLabel jCrossLabel = null;
    private JLabel jCrossLabel2 = null;
    private JPanel jTablePanel = null;
    private JScrollPane jScrollPane = null;
    private JTable jTable = null;
    
    /* Stores the databases that have every attribute */  
    //private Hashtable<String,HashSet<ExternalDatabase>> attribute_db = null;
    private JScrollPane jAttributesScrollPane1 = null;
    private JScrollPane jDatabasesScrollPane11 = null;
    private JButton jAddButton = null;
    private JPanel jCrossingPanel = null;
    private JList jAttributeList = null;
    private JList jExtDatabasesList = null;

    private Vector<String> unificationElements = null;
    
    /**
     * This method initializes 
     * 
     */
    public NewUnificationProtocolPanel(BianaDatabase[] pDB) {
	super();
	this.available_databases = pDB;
	this.unificationElements = new Vector<String>();
	initialize();
	updateJExtDatabaseList(null);
	updateJAttributeList(null);
    }

    private class ExternalDatabaseComparator implements Comparator<ExternalDatabase> {
        public int compare(ExternalDatabase e1, ExternalDatabase e2) {
            return e1.toString().compareTo(e2.toString());
        }

        /* // Safe not to override.
        public boolean equals(Object o) {
            if (this == o)
                return True;
            return False;
        }
        */
    }

    private void updateJExtDatabaseList(ArrayList<String> listAttribute) {
    	DefaultListModel m = (DefaultListModel)this.getJExtDatabasesList().getModel();
        m.removeAllElements();
        if(listAttribute == null) {
            //Object[] temp_list2 = get_selected_biana_db().getAvailableExternalDatabases().toArray();
            //java.util.Arrays.sort(temp_list2);
            //System.out.println(temp_list2);
            //for(int i=0; i<get_selected_biana_db().getAvailableExternalDatabases().size(); i++) {
            //    m.addElement(temp_list2[i]);
            //}

            ArrayList<ExternalDatabase> temp_list = new ArrayList<ExternalDatabase>();
            for( Iterator it = get_selected_biana_db().getAvailableExternalDatabases().iterator(); it.hasNext(); ){
                temp_list.add((ExternalDatabase)it.next());
            }
            java.util.Collections.sort(temp_list, new ExternalDatabaseComparator());
            for( Iterator it = temp_list.iterator(); it.hasNext(); ){
                m.addElement(it.next());
            }
        } else {
            /*
            Object[] selected_values = this.getJExtDatabasesList().getSelectedValues();
            ArrayList<String> t = new ArrayList<String>();
                    for( int i=0; i<selected_values.length; i++ ){
                        t.add((String)selected_values[i]);
                    }
            ArrayList<String> result = ExternalDatabase.getCommonDatabases("protein",get_selected_biana_db().getAvailableExternalDatabases(), t);
            */
            ArrayList<String> result = ExternalDatabase.getCommonDatabases("protein",get_selected_biana_db().getAvailableExternalDatabases(), listAttribute);
            java.util.Collections.sort(result);
            for( Iterator it = result.iterator(); it.hasNext(); ){
                        m.addElement(it.next());
            }
        }
    }

    private void updateJAttributeList(ArrayList<ExternalDatabase> listEDB) {
    	DefaultListModel m = (DefaultListModel)this.getJAttributeList().getModel();
	m.removeAllElements();
	if(listEDB == null) {
	    //if(selected_values == null) {
	    HashSet<String> temp_set = new HashSet<String>();
            ArrayList<String> temp_list = new ArrayList<String>();
	    for( Iterator it = get_selected_biana_db().getAvailableExternalDatabases().iterator(); it.hasNext(); ){
		ExternalDatabase eDB = (ExternalDatabase)it.next();
		for ( Iterator it2 = (eDB).getAvailableAttributes("eE").iterator(); it2.hasNext(); ){
		    String s = (String)it2.next();
		    temp_set.add(s);
		}
	    }
            for ( Iterator it = temp_set.iterator(); it.hasNext(); ){
                temp_list.add((String)it.next());
            }
            java.util.Collections.sort(temp_list);
	    for( Iterator it = temp_list.iterator(); it.hasNext(); ){
		m.addElement(it.next());
	    }
    	} else {
	    /*
	      Object[] selected_values = this.getJExtDatabasesList().getSelectedValues();
	      ArrayList<ExternalDatabase> t = new ArrayList<ExternalDatabase>();
	      for( int i=0; i<selected_values.length; i++ ){
	      t.add((ExternalDatabase)selected_values[i]);
	      }
	      for( Iterator it = ExternalDatabase.getCommonAttributes("protein",t).iterator(); it.hasNext(); ){
	    */
            ArrayList<String> temp_list = ExternalDatabase.getCommonAttributes("protein", listEDB);
            java.util.Collections.sort(temp_list);
	    for( Iterator it = temp_list.iterator(); it.hasNext(); ){
		m.addElement(it.next());
	    }
    	}
    }
    
    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setSize(new Dimension(432, 316));
        this.add(getJBianaDBPanel(), null);
        this.add(getJNamePanel(), null);
        this.add(getJSelectCrossingsPanel(), null);
        this.add(getJTablePanel(), null);
    }

    @Override
	protected boolean check_parameters() {
	if( this.getJTable().getRowCount() == 0 ){
	    JOptionPane.showMessageDialog(this,
					  "You must specify at least one unification criteria",
					  "BIANA ERROR",
					  JOptionPane.ERROR_MESSAGE);
	    return false;
	}
	else if( this.getJTextField().getText().trim().compareTo("")==0 ){
	    JOptionPane.showMessageDialog(this,
					  "You must specify a unique name to the unification protocol",
					  "BIANA ERROR",
					  JOptionPane.ERROR_MESSAGE);
	    return false;
	}  
	return true;
    }
    
    @Override
	protected String get_command() {
	
	StringBuffer command = new StringBuffer();

	BianaDatabase bdb = get_selected_biana_db();
	
	command.append("administration.create_unification_protocol( unification_protocol_name=\""+getJTextField().getText()+"\", list_unification_atom_elements = ["+Utilities.join(this.unificationElements,",")+"], dbname=\""+bdb.getDbname()+"\", dbuser = \""+bdb.getDbuser()+"\", dbhost = \""+bdb.getDbhost()+"\", dbpassword = \""+bdb.getDbpass()+"\")\n");
	
	command.append("administration.check_database(dbname = \""+bdb.getDbname()+"\", dbhost = \""+bdb.getDbhost()+"\", dbuser = \""+bdb.getDbuser()+"\", dbpassword = \""+bdb.getDbpass()+"\")");
	
	return command.toString();
    }

    /**
     * This method initializes jNamePanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJNamePanel() {
	if (jNamePanel == null) {
	    jNameLabel = new JLabel();
	    jNameLabel.setText("Protocol Name");
	    FlowLayout flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    jNamePanel = new JPanel();
	    jNamePanel.setLayout(flowLayout);
	    jNamePanel.add(jNameLabel, null);
	    jNamePanel.add(getJTextField(), null);
	}
	return jNamePanel;
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
    
    /**
     * This method initializes jBianaDBPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJBianaDBPanel() {
	if (jBianaDBPanel == null) {
	    FlowLayout flowLayout1 = new FlowLayout();
	    flowLayout1.setAlignment(FlowLayout.LEFT);
	    jBianaDBLabel = new JLabel();
	    jBianaDBLabel.setText("Use Biana Database");
	    jBianaDBPanel = new JPanel();
	    jBianaDBPanel.setLayout(flowLayout1);
	    jBianaDBPanel.add(jBianaDBLabel, null);
	    jBianaDBPanel.add(getJBianaDBComboBox(), null);
	}
	return jBianaDBPanel;
    }
    
    /**
     * This method initializes jBianaDBComboBox	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getJBianaDBComboBox() {
	if (jBianaDBComboBox == null) {
	    jBianaDBComboBox = new JComboBox(this.available_databases);
	    jBianaDBComboBox.addActionListener(this);
	}
	return jBianaDBComboBox;
    }
    
    public void actionPerformed(ActionEvent e) {
	if( e.getSource()==this.getJBianaDBComboBox()){
	    updateJExtDatabaseList(null);
	    updateJAttributeList(null);
	}
	else if( e.getSource()==this.getJAddButton() ){
	    //if( this.getJAttributeList().getSelectedIndex() == -1 || this.getJExtDatabasesList().getSelectedIndex() == -1 ){
	    if( this.getJExtDatabasesList().getSelectedIndex() == -1 ){
		JOptionPane.showMessageDialog(this,
					      "You must select at least one external database",
					      "BIANA ERROR",
					      JOptionPane.ERROR_MESSAGE);
		return;
	    }
	    String attr_str = Utilities.join(this.getJAttributeList().getSelectedValues(),",");
	    /*
	      StringBuffer attr_str = new StringBuffer();
	      Object t[] = this.getJAttributeList().getSelectedValues();
	      for( int i=0; i<t.length; i++ ){
	      attr_str.append(t[i]);
	      if( i+1!=t.length ){
	      attr_str.append(",");
	      }
	      }*/
	    StringBuffer eD_str = new StringBuffer();
	    StringBuffer eD_ids = new StringBuffer();
	    Object t[] = this.getJExtDatabasesList().getSelectedValues();
	    for( int i=0; i<t.length; i++ ){
		eD_str.append(t[i]);
		eD_ids.append(((ExternalDatabase)t[i]).getID());
		if( i+1!=t.length ){
		    eD_str.append(",");
		    eD_ids.append(",");
		}
	    }
	    if( this.getJAttributeList().getSelectedIndex() == -1 ){
		this.unificationElements.add("(["+eD_ids.toString()+"],[])");
	    }
	    else{
		this.unificationElements.add("(["+eD_ids.toString()+"],[\""+Utilities.join(this.getJAttributeList().getSelectedValues(),"\",\"")+"\"])");
	    }
	    ((DefaultTableModel)this.jTable.getModel()).addRow(new Object[]{
		    attr_str.toString(),
		    eD_str.toString()});
	    //this.updateJExtDatabaseList(); // to remove added attribute
	}
    }

    /**
     * This method initializes jSelectCrossingsPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJSelectCrossingsPanel() {
	if (jSelectCrossingsPanel == null) {
	    jCrossLabel = new JLabel();
	    jCrossLabel.setText("Cross attribute");
	    jCrossLabel2 = new JLabel();
	    jCrossLabel2.setText("on");
	    jSelectCrossingsPanel = new JPanel();
	    jSelectCrossingsPanel.setLayout(new BorderLayout());
	    jSelectCrossingsPanel.add(getJCrossingPanel(), BorderLayout.WEST);
	}
	return jSelectCrossingsPanel;
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
	    model.addColumn("Databases");
	    
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
    
    /**
     * This method initializes jDatabasesScrollPane11	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJDatabasesScrollPane() {
	if (jDatabasesScrollPane11 == null) {
	    jDatabasesScrollPane11 = new JScrollPane();
	    jDatabasesScrollPane11.setPreferredSize(new Dimension(120, 120));
	    jDatabasesScrollPane11.setViewportView(getJExtDatabasesList());
	    jDatabasesScrollPane11.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	return jDatabasesScrollPane11;
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
    private JPanel getJCrossingPanel() {
	if (jCrossingPanel == null) {
	    FlowLayout flowLayout2 = new FlowLayout();
	    flowLayout2.setAlignment(FlowLayout.LEFT);
	    jCrossingPanel = new JPanel();
	    jCrossingPanel.setLayout(flowLayout2);
	    jCrossingPanel.add(jCrossLabel, null);
	    jCrossingPanel.add(getJAttributesScrollPane(), null);
	    jCrossingPanel.add(jCrossLabel2, null);
	    jCrossingPanel.add(getJDatabasesScrollPane(), null);
	    jCrossingPanel.add(getJAddButton(), null);
	}
	return jCrossingPanel;
    }

    /**
     * This method initializes jAttributeList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getJAttributeList() {
	if (jAttributeList == null) {
	    jAttributeList = new JList(new DefaultListModel());
	}
	return jAttributeList;
    }
    
    /**
     * This method initializes jExtDatabasesList	
     * 	
     * @return javax.swing.JList	
     */
    private JList getJExtDatabasesList() {
	if (jExtDatabasesList == null) {
	    jExtDatabasesList = new JList(new DefaultListModel());
	}
	return jExtDatabasesList;
    }
    
    private BianaDatabase get_selected_biana_db(){
	return (BianaDatabase)this.getJBianaDBComboBox().getSelectedItem();
    }
    
}  //  @jve:decl-index=0:visual-constraint="10,10"
