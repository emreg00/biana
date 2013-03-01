
public class CreateNewBianaDBPanel extends BianaDBPanel {

	private static final long serialVersionUID = 1L;

	@Override
	protected String get_command() {
	    return "administration.create_biana_database( dbname = \""+this.jDBNameTextField.getText()+"\", dbhost = \""+this.jDBHostTextField1.getText()+"\", dbuser = \""+this.jDBUserTextField.getText()+"\", dbpassword = \""+new String(this.jPasswordField.getPassword())+"\", description = \""+this.jDescriptionTextField.getText()+"\" )";
	}

}
