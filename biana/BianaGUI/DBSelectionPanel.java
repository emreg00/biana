
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.util.Vector;


public class DBSelectionPanel extends CommandPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel jSelectExistingDBPanel = null;
	private JLabel jLabel = null;
	private JComboBox jComboBox = null;
	private JPanel jDatabaseOptionsPanel = null;
	private JLabel jLabel1 = null;
	protected JTextField jDBNameTextField = null;
	private JLabel jLabel2 = null;
	protected JTextField jDBHostTextField1 = null;
	private JLabel jLabel3 = null;
	protected JTextField jDBUserTextField = null;
	private JLabel jLabel4 = null;
	protected JPasswordField jPasswordField = null;
	
	private BianaDatabase[] available_databases = null;

	/**
	 * This method initializes 
	 * 
	 */
	public DBSelectionPanel(BianaDatabase[] pDB) {
		super();
		this.available_databases = pDB;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setSize(new Dimension(343, 152));
        this.add(getJSelectExistingDBPanel(), null);
        this.add(getJDatabaseOptionsPanel(), null);	
        BianaDatabase selected = (BianaDatabase)this.getJComboBox().getSelectedObjects()[0];
		this.getJDBHostTextField1().setText(selected.getDbhost());
		this.getJDBNameTextField().setText(selected.getDbname());
		this.getJPasswordField().setText(selected.getDbpass());
		this.getJDBUserTextField().setText(selected.getDbuser());
	}

	@Override
	protected boolean check_parameters() {
		if( !this.is_empty(this.getJDBNameTextField().getText()) && !this.is_empty(this.getJDBHostTextField1().getText()) ){
			return true;
		}
		return false;
	}

	@Override
	protected String get_command(){
		return null;
	}
	

	public Vector<String> get_command_vector(){
		Vector<String> commandVector = new Vector<String>();
		commandVector.add("--biana-dbname="+this.getJDBNameTextField().getText());
		commandVector.add("--biana-dbhost="+this.getJDBHostTextField1().getText());
		if( !this.is_empty(this.getJDBUserTextField().getText())){
			commandVector.add("--biana-dbuser="+this.getJDBUserTextField().getText());
		}
		if( !this.is_empty(this.getJPasswordField().getText())){
			commandVector.add("--biana-dbpass="+this.getJPasswordField().getText());
		}
		return commandVector;
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if( e.getSource()==this.getJComboBox() ){
			BianaDatabase selected = (BianaDatabase)this.getJComboBox().getSelectedObjects()[0];
			this.getJDBHostTextField1().setText(selected.getDbhost());
			this.getJDBNameTextField().setText(selected.getDbname());
			this.getJPasswordField().setText(selected.getDbpass());
			this.getJDBUserTextField().setText(selected.getDbuser());
		}
	}

	/**
	 * This method initializes jSelectExistingDBPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJSelectExistingDBPanel() {
		if (jSelectExistingDBPanel == null) {
			jLabel = new JLabel();
			jLabel.setText("Select Database");
			jSelectExistingDBPanel = new JPanel();
			jSelectExistingDBPanel.setLayout(new FlowLayout());
			jSelectExistingDBPanel.add(jLabel, null);
			jSelectExistingDBPanel.add(getJComboBox(), null);
		}
		return jSelectExistingDBPanel;
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

	/**
	 * This method initializes jDatabaseOptionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJDatabaseOptionsPanel() {
		if (jDatabaseOptionsPanel == null) {
			jLabel4 = new JLabel();
			jLabel4.setText("Password");
			jLabel3 = new JLabel();
			jLabel3.setText("Database User");
			jLabel2 = new JLabel();
			jLabel2.setText("Database Host");
			jLabel1 = new JLabel();
			jLabel1.setText("Database Name");
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(4);
			gridLayout.setVgap(5);
			gridLayout.setHgap(5);
			gridLayout.setColumns(10);
			jDatabaseOptionsPanel = new JPanel();
			jDatabaseOptionsPanel.setLayout(gridLayout);
			jDatabaseOptionsPanel.add(jLabel1, null);
			jDatabaseOptionsPanel.add(getJDBNameTextField(), null);
			jDatabaseOptionsPanel.add(jLabel2, null);
			jDatabaseOptionsPanel.add(getJDBHostTextField1(), null);
			jDatabaseOptionsPanel.add(jLabel3, null);
			jDatabaseOptionsPanel.add(getJDBUserTextField(), null);
			jDatabaseOptionsPanel.add(jLabel4, null);
			jDatabaseOptionsPanel.add(getJPasswordField(), null);
		}
		return jDatabaseOptionsPanel;
	}

	/**
	 * This method initializes jDBNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJDBNameTextField() {
		if (jDBNameTextField == null) {
			jDBNameTextField = new JTextField();
			jDBNameTextField.setColumns(10);
		}
		return jDBNameTextField;
	}

	/**
	 * This method initializes jDBHostTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJDBHostTextField1() {
		if (jDBHostTextField1 == null) {
			jDBHostTextField1 = new JTextField();
			jDBHostTextField1.setColumns(10);
		}
		return jDBHostTextField1;
	}

	/**
	 * This method initializes jDBUserTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJDBUserTextField() {
		if (jDBUserTextField == null) {
			jDBUserTextField = new JTextField();
			jDBUserTextField.setColumns(10);
		}
		return jDBUserTextField;
	}

	/**
	 * This method initializes jPasswordField	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordField() {
		if (jPasswordField == null) {
			jPasswordField = new JPasswordField();
			jPasswordField.setColumns(10);
		}
		return jPasswordField;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
