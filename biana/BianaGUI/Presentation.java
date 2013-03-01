
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


//public class Presentation extends JFrame implements ActionListener{
public class Presentation extends JDialog implements ActionListener{

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;


	/**
	 * This is the default constructor
	 */
	public Presentation(JFrame owner) {
	    //super();
	    super(owner);
	    initialize();
	    this.setUndecorated(true);
	    //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo( owner );
	    this.setResizable(false);
	    this.setVisible(true);
	    this.setAlwaysOnTop(true);
	    
	    /*
	    try{
		//do what you want to do before sleeping
		Thread.currentThread().sleep(3000);//sleep for 1000 ms
		//do what you want to do after sleeptig
		this.dispose();
	    }
	    catch(InterruptedException ie){		    
		//If this thread was intrrupted by nother thread
		this.dispose();
		}*/
	    //this.dispose();*/
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
	    this.setSize(341, 331);
	    this.setContentPane(getJContentPane());
	    this.setTitle("BIANA");
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
			//jContentPane.add(new JButton(new ImageIcon("./img/logo_biana1.png")),BorderLayout.CENTER);
			JButton button = new JButton(new ImageIcon(this.getClass().getResource("/img/logo_biana1.png")));
			jContentPane.add(button, BorderLayout.CENTER);
			button.addActionListener(this);
		}
		return jContentPane;
	}

    public void actionPerformed(ActionEvent e) {
	this.dispose();
    }

    public void close(){
	this.dispose();
    }

}  //  @jve:decl-index=0:visual-constraint="176,20"
