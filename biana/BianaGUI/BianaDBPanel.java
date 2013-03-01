
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.GridLayout;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;


public abstract class BianaDBPanel extends CommandPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel jLabel1 = null;
	protected JTextField jDBNameTextField = null;
	private JLabel jLabel2 = null;
	protected JTextField jDBHostTextField1 = null;
	private JLabel jLabel3 = null;
	protected JTextField jDBUserTextField = null;
	private JLabel jLabel4 = null;
	protected JPasswordField jPasswordField = null;
	
	private JLabel jLabel = null;
	protected JTextField jDescriptionTextField = null;

	/**
	 * This method initializes 
	 * 
	 */
	public BianaDBPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		jLabel4 = new JLabel();
		jLabel4.setText("Password");
		jLabel3 = new JLabel();
		jLabel3.setText("Database User");
		jLabel2 = new JLabel();
		jLabel2.setText("Database Host");
		jLabel1 = new JLabel();
		jLabel1.setText("Database Name");
        jLabel = new JLabel();
        jLabel.setText("Database Description");
        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.setRows(5);
        gridLayout1.setHgap(5);
        gridLayout1.setVgap(5);
        gridLayout1.setColumns(2);
        this.setLayout(gridLayout1);
        this.setSize(new Dimension(319, 125));
        this.add(jLabel1, null);
        this.add(getJDBNameTextField(), null);
        this.add(jLabel2, null);
        this.add(getJDBHostTextField1(), null);
        this.add(jLabel3, null);
        this.add(getJDBUserTextField(), null);
        this.add(jLabel4, null);
        this.add(getJPasswordField(), null);
        this.add(jLabel, null);
        this.add(getJDescriptionTextField(), null);
	}

	@Override
	protected boolean check_parameters() {
		if( !this.is_empty(this.getJDBNameTextField().getText()) && !this.is_empty(this.getJDBHostTextField1().getText()) ){
			return true;
		}
		JOptionPane.showMessageDialog(this, 
				"It is mandatory to specify database name and host",
				"Database Parameters",
				JOptionPane.ERROR_MESSAGE);
		return false;
	}

	@Override
	protected abstract String get_command();
		

	/**
	 * This method initializes jDBNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	protected JTextField getJDBNameTextField() {
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
	protected JTextField getJDBHostTextField1() {
		if (jDBHostTextField1 == null) {
			jDBHostTextField1 = new JTextField();
			jDBHostTextField1.setText("127.0.0.1");
			jDBHostTextField1.setColumns(10);
		}
		return jDBHostTextField1;
	}

	/**
	 * This method initializes jDBUserTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	protected JTextField getJDBUserTextField() {
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
	protected JPasswordField getJPasswordField() {
		if (jPasswordField == null) {
			jPasswordField = new JPasswordField();
			jPasswordField.setColumns(10);
		}
		return jPasswordField;
	}

	/**
	 * This method initializes jDescriptionTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	protected JTextField getJDescriptionTextField() {
		if (jDescriptionTextField == null) {
			jDescriptionTextField = new JTextField();
		}
		return jDescriptionTextField;
	}

}  //  @jve:decl-index=0:visual-constraint="143,11"
