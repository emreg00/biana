
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JList;


import javax.swing.JOptionPane;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

public class DeleteBianaDatabasePanel extends CommandPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPane = null;
	private JList jList = null;
	
	private BianaDatabase[] available_databases = null;
	private JLabel jLabel = null;

	/**
	 * This method initializes 
	 * 
	 */
	public DeleteBianaDatabasePanel(BianaDatabase[] pDB) {
		super();
		this.available_databases = pDB;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        jLabel = new JLabel();
        jLabel.setText("Select BIANA databases to delete:");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setSize(new Dimension(310, 156));
        this.add(jLabel, null);
        this.add(getJScrollPane(), null);
			
	}

	@Override
	protected boolean check_parameters() {
		if( this.getJList().getSelectedIndex() == -1 ){
			return true;
		}
		int confirm = JOptionPane.showConfirmDialog(this,
	    		"Are you sure you want to delete selected BIANA databases? This action is not reversible",
	    		"Delete BIANA databases confirmation",
	    		JOptionPane.YES_NO_OPTION);
		if( confirm == JOptionPane.YES_OPTION ){
			return true;
		}
		return false;
	}

	@Override
	protected String get_command() {
	    StringBuffer str = new StringBuffer("");
	    int[] selected = this.getJList().getSelectedIndices();
	    for( int i=0; i<selected.length; i++ ) {
		str.append("administration.delete_biana_database(dbname=\""+this.available_databases[selected[i]].getDbname()+"\",dbhost=\""+this.available_databases[selected[i]].getDbhost()+"\",dbuser=\""+this.available_databases[selected[i]].getDbuser()+"\",dbpassword=\""+this.available_databases[selected[i]].getDbpass()+"\")\n");
	    }
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
			jList = new JList(this.available_databases);
		}
		return jList;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
