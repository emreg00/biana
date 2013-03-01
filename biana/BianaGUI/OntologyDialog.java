
import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.JLabel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import java.util.regex.*;
import java.util.Enumeration;
import java.awt.Dimension;
import javax.swing.JTextArea;

//public class OntologyDialog extends JDialog implements ActionListener, KeyListener {
public class OntologyDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel jButtonsPanel = null;

	private JPanel jSearchPanel = null;

	private JPanel jAddCancelPanel = null;

	private JLabel jSearchLabel = null;

	private JTextField jSearchTextField = null;

	private JButton jAddButton = null;
    private JButton jExpandButton = null;
    private JButton jCollapseButton = null;
    private JButton jSearchButton = null;

	private JButton jCancelButton = null;

	private JScrollPane jTreeScrollPane = null;
	
	private JTree tree = null;
	
	private int temp_row = -1;
	private JTextArea outText = null;

	/**
	 * @param owner
	 */
    public OntologyDialog(Frame owner, JTree tree, JTextArea out, String pOntologyName) {
	super(owner);
	this.tree = tree;
	this.outText = out;
	tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	initialize();
	this.setTitle("Navigate into "+pOntologyName);
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
	private void initialize() {
		this.setSize(300, 301);
		this.setContentPane(getJContentPane());
		this.setVisible(true);
	}


    private void expandAll(){
	int row = 0;
	while( row < this.tree.getRowCount()){
	    this.tree.expandRow(row);
	    row++;
	}
    }

    private void collapseAll(){
	int row = tree.getRowCount() - 1;
	while( row >= 0 ){
	    tree.collapseRow(row);
	    row--;
	}
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
	    jContentPane.add(getJButtonsPanel(), BorderLayout.SOUTH);
	    jContentPane.add(getJTreeScrollPane(), BorderLayout.CENTER);
	}
	return jContentPane;
    }

	/**
	 * This method initializes jButtonsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJButtonsPanel() {
		if (jButtonsPanel == null) {
			jButtonsPanel = new JPanel();
			jButtonsPanel.setLayout(new BoxLayout(getJButtonsPanel(), BoxLayout.Y_AXIS));
			jButtonsPanel.add(getJSearchPanel(), null);
			jButtonsPanel.add(getJAddCancelPanel(), null);
		}
		return jButtonsPanel;
	}

	/**
	 * This method initializes jSearchPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJSearchPanel() {
		if (jSearchPanel == null) {
			jSearchLabel = new JLabel();
			jSearchLabel.setText("Search");
			jSearchLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			jSearchPanel = new JPanel();
			jSearchPanel.setLayout(new FlowLayout());
			jSearchPanel.add(jSearchLabel, null);
			jSearchPanel.add(getJSearchTextField(), null);
			jSearchPanel.add(getSearchButton(), null );
		}
		return jSearchPanel;
	}

	/**
	 * This method initializes jAddCancelPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJAddCancelPanel() {
		if (jAddCancelPanel == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.RIGHT);
			jAddCancelPanel = new JPanel();
			jAddCancelPanel.setLayout(flowLayout);
			jAddCancelPanel.add(getExpandButton(), null);
			jAddCancelPanel.add(getCollapseButton(), null);
			jAddCancelPanel.add(getJAddButton(), null);
			jAddCancelPanel.add(getJCancelButton(), null);
		}
		return jAddCancelPanel;
	}

	/**
	 * This method initializes jSearchTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJSearchTextField() {
		if (jSearchTextField == null) {
			jSearchTextField = new JTextField();
			jSearchTextField.setColumns(20);
			//jSearchTextField.addActionListener(this);
			//jSearchTextField.addKeyListener(this);
		}
		return jSearchTextField;
	}

	/**
	 * This method initializes jAddButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJAddButton() {
		if (jAddButton == null) {
			jAddButton = new JButton();
			jAddButton.setText("Add");
			jAddButton.addActionListener(this);
		}
		return jAddButton;
	}

    private JButton getExpandButton() {
	if (jExpandButton == null) {
	    jExpandButton = new JButton();
	    jExpandButton.setText("Expand all tree");
	    jExpandButton.addActionListener(this);
	}
	return jExpandButton;
    }

    private JButton getCollapseButton() {
	if (jCollapseButton == null) {
	    jCollapseButton = new JButton();
	    jCollapseButton.setText("Collapse all tree");
	    jCollapseButton.addActionListener(this);
	}
	return jCollapseButton;
    }

    private JButton getSearchButton() {
	if (jSearchButton == null) {
	    jSearchButton = new JButton();
	    jSearchButton.setText("Search");
	    jSearchButton.addActionListener(this);
	}
	return jSearchButton;
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

	/**
	 * This method initializes jTreeScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJTreeScrollPane() {
		if (jTreeScrollPane == null) {
			jTreeScrollPane = new JScrollPane(this.tree);
			jTreeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			jTreeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		}
		return jTreeScrollPane;
	}
	
	private void runNodes(Pattern p, DefaultMutableTreeNode node){
		
	    if( node.isRoot() ){
		this.temp_row = 0;
	    }
	    else{
		this.temp_row++;
	    }
	    System.err.println("Checking "+node.getUserObject());
	    if( node.getUserObject() != null ){
		if( p.matcher((String)node.getUserObject()).find() ){ 
		    this.tree.addSelectionRow(this.temp_row);
		    //this.tree.addSelectionRow(row);
		    //System.err.println("I should select "+node.getUserObject());
		}
	    }
	    if( node.isLeaf()==false ){
		for( Enumeration en = node.children(); en.hasMoreElements(); ){
		    this.runNodes(p,(DefaultMutableTreeNode)en.nextElement());
		}
	    }
	} 
    
    public void actionPerformed(ActionEvent e) {
	if( e.getSource() == this.getJAddButton() ){
	    int[] a = this.tree.getSelectionRows();
	    TreePath[] t = this.tree.getSelectionPaths();
	    for( int i=0; i<t.length; i++){
		//this.outText.append(((DefaultMutableTreeNode)t[i].getLastPathComponent()).getUserObject().toString());
		this.outText.append(((OntologyTreeNode)t[i].getLastPathComponent()).getOntologyNodeID());
		this.outText.append("\n");
	    }
	    return;
	}
	else if( e.getSource() == this.getJCancelButton() ){
	    this.dispose();
	    return;
	}
	else if( e.getSource() == this.getExpandButton() ){
	    this.expandAll();
	}
	else if( e.getSource() == this.getCollapseButton() ){
	    this.collapseAll();
	}
	else if( e.getSource() == this.getSearchButton() ){
	    this.expandAll();
	    this.tree.setSelectionRows(new int[0]);
	    String text = this.getJSearchTextField().getText();
	    if( text.trim().length()>0 ){
		this.runNodes(Pattern.compile(Pattern.quote(text), Pattern.CASE_INSENSITIVE), (DefaultMutableTreeNode)this.tree.getModel().getRoot());
	    }
	}
    }

    /*
    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {
	this.tree.setSelectionRows(new int[0]);
	String text = this.getJSearchTextField().getText();
	if( text.trim().length()>0 ){
	    this.runNodes(Pattern.compile(Pattern.quote(text)), (DefaultMutableTreeNode)this.tree.getModel().getRoot());
	}
    }
    
    public void keyTyped(KeyEvent e) {
	
    }*/


}  //  @jve:decl-index=0:visual-constraint="239,10"
