

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Toolkit;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Credits extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;


	/**
	 * This is the default constructor
	 */
	public Credits() {
		super();
		initialize();
		this.setUndecorated(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
	    //this.setSize(289, 297);
	    this.setSize(270, 423);
	    this.setContentPane(getJContentPane());
	    this.setTitle("About BIANA");
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
			//JButton button = new JButton(new ImageIcon("./img/credits.gif"));
			JButton button = new JButton(new ImageIcon(this.getClass().getResource("./img/credits.gif")));
			jContentPane.add(button,BorderLayout.CENTER);
			button.addActionListener(this);
		}
		return jContentPane;
	}

	public void actionPerformed(ActionEvent e) {
		this.dispose();
	}

}  //  @jve:decl-index=0:visual-constraint="176,20"
