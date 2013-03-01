
import java.awt.GridBagLayout;
import javax.swing.JPanel;

public abstract class CommandPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * This is the default constructor
	 */
	public CommandPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		//this.setSize(300, 200);
		this.setLayout(new GridBagLayout());
	}
	
	protected abstract String get_command();
	protected abstract boolean check_parameters();
	
	/**
	 * Checks if an string is empty or not.
	 * @param text
	 * @return True if the String is empty
	 */
	protected boolean is_empty(String text){
	    //System.out.println(text);
	    //System.out.println(text.matches(".*\\w+.*"));
	    return !text.matches(".*[\\*\\w+].*");
	}
}
