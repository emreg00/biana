import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class BianaErrorDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JScrollPane jErrorScrollPane = null;
    private JTextArea jErrorTextArea = null; 
    private String errorDetailsMessage = null;
    private String errorMessage = null;
    private JOptionPane jOptionPane = null;
    private JPanel jMessagePane = null;
    private JPanel jButtonsPane = null;

    /**
     * @param owner
     */
    public BianaErrorDialog(Frame owner, String errorMessage, String errorDetailsMessage) {
	super(owner);
	this.setLocationRelativeTo(owner);
	this.errorMessage = errorMessage;
	this.errorDetailsMessage = errorDetailsMessage;
	initialize();
    }
    
    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
	this.setSize(500,300);
	this.setResizable(true);
	this.setContentPane(getJContentPane());
	this.setTitle("BIANA Error Notification");
	this.setVisible(true);
    }
    
    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
	if (jContentPane == null) {
	    BorderLayout borderLayout = new BorderLayout();
	    jContentPane = new JPanel();
	    jContentPane.setLayout(borderLayout);
	    jContentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    jContentPane.add(getMessagePanel(), BorderLayout.NORTH);
	    jContentPane.add(getJErrorScrollPane(), BorderLayout.CENTER);
	    jContentPane.add(getButtonsPanel(), BorderLayout.SOUTH);
	}
	return jContentPane;
    }

    /**
     * This method initializes jErrorScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJErrorScrollPane() {
	if (jErrorScrollPane == null) {
	    jErrorScrollPane = new JScrollPane();
	    jErrorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    jErrorScrollPane.setViewportView(getJErrorTextArea());
	    jErrorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    jErrorScrollPane.setViewportView(getJErrorTextArea());
	}
	return jErrorScrollPane;
    }
    
    /**
     * This method initializes jErrorTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getJErrorTextArea() {
	if (jErrorTextArea == null) {
	    jErrorTextArea = new JTextArea();
	    jErrorTextArea.setText(this.errorDetailsMessage);
	    jErrorTextArea.setEditable(false);
	}
	return jErrorTextArea;
    }
    
    /**
     * This method initializes jOptionPane	
     * 	
     * @return javax.swing.JOptionPane	
     */
    private JOptionPane getJOptionPane() {
	if (jOptionPane == null) {
	    jOptionPane = new JOptionPane(this.errorMessage);
	    jOptionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
	}
	return jOptionPane;
    }

    private JPanel getMessagePanel(){
	if( jMessagePane == null ){
	    jMessagePane = new JPanel();
	    FlowLayout flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.LEFT);
	    jMessagePane.setLayout(flowLayout);
	    jMessagePane.add(new JLabel(UIManager.getIcon("OptionPane.errorIcon")));
	    jMessagePane.add(new JLabel(this.errorMessage));
	}
	return jMessagePane; 
    }

    private JPanel getButtonsPanel(){
	if( jButtonsPane == null ){
	    jButtonsPane = new JPanel();
	    FlowLayout flowLayout = new FlowLayout();
	    flowLayout.setAlignment(FlowLayout.RIGHT);
	    jButtonsPane.setLayout(flowLayout);
	    JButton button = new JButton();
	    button.addActionListener(this);
	    button.setText("OK");
	    jButtonsPane.add(button);
	}
	return jButtonsPane;
    }

    public void actionPerformed(ActionEvent e) {
	this.dispose();
    }

    public static void main(String[] args) {
	JFrame a = new JFrame();
	a.setVisible(true);
	new BianaErrorDialog(a,"test_error","error_details");
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
