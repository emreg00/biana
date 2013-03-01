
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

import javax.swing.DefaultListModel;

import javax.swing.JComboBox;

public class ViewUnificationProtocolPanel extends CommandPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JScrollPane jScrollPane = null;
    private JList jList = null;
    
    private BianaDatabase[] available_databases = null;
    private BianaDatabase currentBianaDatabase = null;
    private String currentUnificationProtocol = null;
    
    private JLabel jLabel = null;
    private JPanel jPanel = null;
    private JLabel jLabel1 = null;
    private JComboBox jComboBox = null;

	/**
	 * This method initializes 
	 * 
	 */
    public ViewUnificationProtocolPanel( BianaDatabase[] pDB, BianaDatabase currentBianaDatabase, String currentUnificationProtocol ) {
		super();
		this.available_databases = pDB;
		this.currentBianaDatabase = currentBianaDatabase;
		this.currentUnificationProtocol = currentUnificationProtocol;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        jLabel = new JLabel();
        jLabel.setText("Select Unification Protocol to display:");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setSize(new Dimension(400, 156));
        this.add(getJPanel(), null);
        this.add(jLabel, null);
        this.add(getJScrollPane(), null);
			
	}

	@Override
	protected boolean check_parameters() {
		if( this.getJList().getSelectedIndex() == -1 ){
			return false;
		}
		return true;
	}

	@Override
	protected String get_command() {
	    StringBuffer str = new StringBuffer("");
	    Object[] unification_protocol_selected_values = this.getJList().getSelectedValues();
	    BianaDatabase bdb = (BianaDatabase)this.getJComboBox().getSelectedItem();
	    for( int i = 0; i<unification_protocol_selected_values.length; i++ ){
		str.append("administration.get_unification_protocol_atoms(unification_protocol_name = \""+(String)unification_protocol_selected_values[i]+"\", dbname=\""+bdb.getDbname()+"\",dbhost=\""+bdb.getDbhost()+"\",dbuser=\""+bdb.getDbuser()+"\",dbpassword=\""+bdb.getDbpass()+"\")\n");
	    }
	    //Update the database
	    //str.append("administration.check_database(dbname = \""+bdb.getDbname()+"\", dbhost = \""+bdb.getDbhost()+"\", dbuser = \""+bdb.getDbuser()+"\", dbpassword = \""+bdb.getDbpass()+"\")");
	    return str.toString();
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
			jList = new JList(new DefaultListModel());
			String[] t = ((BianaDatabase)this.getJComboBox().getSelectedItem()).getUnification_protocols();
			for( int i=0; i<t.length; i++ ){
			    if( !t[i].toLowerCase().equals("no unification") ){
				((DefaultListModel)jList.getModel()).addElement(t[i]);
			    }
			}
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
			jLabel1.setText("From database");
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			jPanel = new JPanel();
			jPanel.setLayout(flowLayout);
			jPanel.add(jLabel1, null);
			jPanel.add(getJComboBox(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox(this.available_databases);
			jComboBox.addActionListener(this);
		}
		return jComboBox;
	}

	public void actionPerformed(ActionEvent e) {
	    if( e.getSource()==this.getJComboBox() ){
		DefaultListModel m = (DefaultListModel)this.getJList().getModel();
		m.removeAllElements();
		String[] t = ((BianaDatabase)this.getJComboBox().getSelectedItem()).getUnification_protocols();
		for( int i=0; i<t.length; i++ ){ 
                    if( !t[i].toLowerCase().equals("no unification") ){
                        m.addElement(t[i]); 
                    }
                } 
	    }
	} 
    
}  //  @jve:decl-index=0:visual-constraint="10,10"
