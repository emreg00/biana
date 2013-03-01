
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.event.*;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.SystemColor;
import java.awt.Font;
import java.awt.Dimension;

import java.awt.event.WindowAdapter;

public class CommandDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	protected JPanel jOptionsPanel = null;
	private JPanel jButtonsPanel = null;
	private JButton jAcceptButton = null;
	private JButton jCancelButton = null;
	private BianaProcessController controller = null;
	private CommandPanel commandPanel = null;
	private String sessionID = null;

	/**
	 * @param owner
	 */
	public CommandDialog(BianaProcessController pOwner, CommandPanel pCommandPanel, String pTitle, Dimension pSize) {
		super(pOwner.getParentFrame());
		this.controller = pOwner;
		this.setTitle(pTitle);
		this.setSize(pSize);
		this.setContentPane(getJContentPane());
		this.setResizable(true);
		this.setOptionsPanel( pCommandPanel );
		this.commandPanel = pCommandPanel;
		this.setLocationRelativeTo(pOwner.getParentFrame());
		this.setVisible(true);
		this.controller.setEnabled(false);
                this.addWindowListener(new WindowAdapterWrapper());
                //this.setModal(true);
	}
	
	/**
	 * @param owner
	 */
	public CommandDialog(BianaProcessController pOwner, String pSessionID, CommandPanel pCommandPanel, String pTitle, Dimension pSize) {
		super(pOwner.getParentFrame());
		this.controller = pOwner;
		this.setTitle(pTitle);
		this.setSize(pSize);
		this.setContentPane(getJContentPane());
		this.setResizable(true);
		this.setOptionsPanel( pCommandPanel );
		this.commandPanel = pCommandPanel;
		this.setLocationRelativeTo(pOwner.getParentFrame());
		this.setVisible(true);
		this.sessionID=pSessionID;
		this.controller.setEnabled(false);
                this.addWindowListener(new WindowAdapterWrapper());
                //this.setModal(true);
	}

        private class WindowAdapterWrapper extends WindowAdapter {
            public void windowClosed(WindowEvent e) {
                //System.err.println("Window closed");
		controller.setEnabled(true);
            }
            public void windowClosing(WindowEvent e) {
                //System.err.println("Window closing");
		controller.setEnabled(true);
            }
            /*
            public void windowDeactivated(WindowEvent e) {
                //System.err.println("Window deactivated");
		controller.setEnabled(true);
            }
            public void windowDeiconified(WindowEvent e) {
                System.err.println("Window deiconified");
		controller.setEnabled(true);
            }
            */
        } 

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJOptionsPanel(), BorderLayout.CENTER);
			jContentPane.add(getJButtonsPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jOptionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJOptionsPanel() {
		if (jOptionsPanel == null) {
			jOptionsPanel = new JPanel();
			//jOptionsPanel.setLayout(new FlowLayout());
			jOptionsPanel.setLayout(new BorderLayout());
			jOptionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createTitledBorder(null, "Command Options", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), SystemColor.activeCaption)), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		}
		return jOptionsPanel;
	}

	/**
	 * This method initializes jButtonsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJButtonsPanel() {
		if (jButtonsPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			jButtonsPanel = new JPanel();
			jButtonsPanel.setLayout(flowLayout);
			jButtonsPanel.add(getJAcceptButton(), null);
			jButtonsPanel.add(getJCancelButton(), null);
		}
		return jButtonsPanel;
	}

	/**
	 * This method initializes jAcceptButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJAcceptButton() {
		if (jAcceptButton == null) {
			jAcceptButton = new JButton();
			jAcceptButton.setText("Accept");
			jAcceptButton.addActionListener(this);
		}
		return jAcceptButton;
	}

	/**
	 * This method initializes jCancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJCancelButton() {
		if (jCancelButton == null) {
			jCancelButton = new JButton();
			jCancelButton.setText("Cancel");
			jCancelButton.addActionListener(this);
		}
		return jCancelButton;
	}
	
	protected void setOptionsPanel(JPanel options){
	    this.jOptionsPanel.add(options,BorderLayout.CENTER);
	}
	
	public String get_command(){
		String prefix = "";
		if( this.sessionID != null ){
		    prefix = "temp = available_sessions[\""+this.sessionID+"\"].";
		}
		// System.err.println("PREFIX: "+prefix);
		return prefix.concat(this.commandPanel.get_command());
	}

	public void actionPerformed(ActionEvent e) {
		if( e.getSource()==jAcceptButton){
			if( this.commandPanel.check_parameters()==true ){
				this.controller.add_command(this.get_command(), true);
				this.controller.setEnabled(true);
				this.dispose();
			}
		}
		else if( e.getSource()==jCancelButton){
		    this.controller.setEnabled(true);
		    this.dispose();
		}
	}

}  //  @jve:decl-index=0:visual-constraint="76,23"
