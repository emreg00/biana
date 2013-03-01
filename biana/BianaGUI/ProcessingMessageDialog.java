import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import java.awt.event.*;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import javax.swing.JProgressBar;

public class ProcessingMessageDialog extends JDialog implements WindowListener{

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JLabel jMessageLabel = null;
    private JPanel jMessagePanel = null;
    private JPanel jProgressBarPanel = null;
    private JProgressBar jProgressBar = null;
    private String message = null;
    private BianaProcessController controller = null;

    private final int CHARACTERS_X_LINE = 100;
    
    /**
     * @param owner
     */
    public ProcessingMessageDialog(BianaProcessController controller, Frame owner, String message) {
	super(owner);
	this.message = message;
	this.controller = controller;
	initialize();
	this.setVisible(true);
	this.setLocationRelativeTo(owner);
	this.addWindowListener(this);
	//this.setAlwaysOnTop(true);  // IT IS APPLIED TO ALL WINDOWS, NOT ONLY CYTOSCAPE!!!
	
	this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
	    //this.setSize(338, 113);//this.setSize(338, 113);
	    this.setResizable(true);
	    this.setTitle("BIANA processing...");
	    this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
	    if (jContentPane == null) {

		jMessagePanel = new JPanel();
		jMessagePanel.setLayout(new FlowLayout());

		//Determine how many lines are needed
		String[] words = this.message.split("\\s");
		StringBuffer new_line_text = new StringBuffer();
		int num_lines = 0;
		for( int i=0; i<words.length; i++ ){
		    if( (new_line_text.length() + words[i].length()) > CHARACTERS_X_LINE ){
			jMessageLabel = new JLabel();
			jMessageLabel.setText(new_line_text.toString());
			jMessagePanel.add(jMessageLabel, null);
			new_line_text.setLength(0);
			num_lines += 1;
		    }
		    new_line_text.append(words[i]);
		    new_line_text.append(" ");
		}
		
		this.setSize(430, 113+num_lines*20);
		
		//Add the last line
		jMessageLabel = new JLabel();
		jMessageLabel.setText(new_line_text.toString());
		jMessagePanel.add(jMessageLabel, null);

		jContentPane = new JPanel();
		jContentPane.setLayout(new BoxLayout(getJContentPane(), BoxLayout.Y_AXIS));
		jContentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		jContentPane.add(jMessagePanel, null);
		jContentPane.add(getJProgressBarPanel(), null);
		
	    }
	    return jContentPane;
	}

    
    public void setMessage(String message){
	this.message = message;
	this.jMessageLabel.setText(this.message);
    }


	/**
	 * This method initializes jProgressBarPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJProgressBarPanel() {
		if (jProgressBarPanel == null) {
			jProgressBarPanel = new JPanel();
			jProgressBarPanel.setLayout(new FlowLayout());
			jProgressBarPanel.add(getJProgressBar(), null);
		}
		return jProgressBarPanel;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setMaximum(110);
			jProgressBar.setPreferredSize(new Dimension(200, 20));
			jProgressBar.setIndeterminate(true);
		}
		return jProgressBar;
	}

    public void windowIconified(WindowEvent e){}
    public void windowClosed(WindowEvent e){
	//System.err.println("Window is closed...");
    }
    public void windowDeactivated(WindowEvent e){}
    public void windowActivated(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}

    public void windowClosing(WindowEvent e){
	System.err.println("Closing Biana processing message dialog...");
	/*JOptionPane.showMessageDialog(this.getOwner(),
				      "For the moment, it is not possible to stop a Biana Process from graphical interface",
				      "Cancel BIANA process",
				      JOptionPane.ERROR_MESSAGE);*/
	JOptionPane.showMessageDialog(this.getOwner(),
				      "Closing this window does not stop the current BIANA process.",
				      "Close Processing BIANA Dialog",
				      JOptionPane.INFORMATION_MESSAGE);
	
	this.dispose();
	controller.end_processing();

	//Tring to send the kill signal...
	//if( n==0 ){
	    //char c = 3;
	    //this.controller.add_command("\\x03",false);
	    //this.dispose();
	//}
    }
    public void windowOpened(WindowEvent e){}

}  //  @jve:decl-index=0:visual-constraint="159,27"
