
public class AddBianaDBPanel extends BianaDBPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected String get_command() {
		return "administration.check_database(dbname = \""+this.jDBNameTextField.getText()+"\", dbhost = \""+this.jDBHostTextField1.getText()+"\", dbuser = \""+this.jDBUserTextField.getText()+"\", dbpassword = \""+new String(this.jPasswordField.getPassword())+"\" )";
	}
}
