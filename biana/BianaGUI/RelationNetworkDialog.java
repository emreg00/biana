import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.awt.Font;
import javax.swing.JLabel;

public class RelationNetworkDialog extends JDialog implements ActionListener {

    private JList available_relations_JList = null;
    private JList relation_restrictions_JList = null;
    private JTable jTable = null;
    private JTextArea relation_restriction_JTextArea = null;
    private Vector<String> relation_restrictions = null;

    public RelationNetworkDialog(Container parent, JList available_relations_JList, JList relation_restrictions_JList, Vector<String> relation_restrictions_tuples) {

	super();
        this.setLocationRelativeTo(parent);
        
	this.setResizable(false);
	this.setSize(520,320);
        this.setTitle("Add Relations");
        this.setModal(true);
	
	this.available_relations_JList = available_relations_JList;
	this.relation_restrictions_JList = relation_restrictions_JList;
	this.relation_restrictions = relation_restrictions_tuples;

	JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
	//FlowLayout flowLayout = new FlowLayout();
	//flowLayout.setAlignment(FlowLayout.LEFT);
	GridLayout gridLayout = new GridLayout(2,3); //,-2,0);
	JPanel listPanel = new JPanel();
	//listPanel.setLayout(flowLayout);
	listPanel.setLayout(gridLayout);
	
	JScrollPane relationTypeScrollPane = new JScrollPane();
	relationTypeScrollPane.setPreferredSize(new Dimension(150, 150));
	relationTypeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	relationTypeScrollPane.setViewportView(available_relations_JList);

        JLabel tempLabel = new JLabel("1. Select relation type: ");
        tempLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
	listPanel.add(tempLabel);
	//listPanel.add(Box.createRigidArea(new Dimension(5,3)));
	//listPanel.add(Box.createRigidArea(new Dimension(10,3)));
	listPanel.add(Box.createHorizontalGlue());
	listPanel.add(Box.createHorizontalGlue());
	//listPanel.add(Box.createRigidArea(new Dimension(10,3)));
	listPanel.add(Box.createHorizontalGlue());
	listPanel.add(relationTypeScrollPane);
	//listPanel.add(Box.createRigidArea(new Dimension(10,3)));
	listPanel.add(Box.createHorizontalGlue());

	mainPanel.add(listPanel);
	mainPanel.add(getJRelationRestrictionsPanel());

	//mainPanel.add(getJTablePanel());

	mainPanel.add(Box.createRigidArea(new Dimension(10,20)));

	mainPanel.add(getJButtonsPanel());

	this.setContentPane(mainPanel);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /*
    public Vector<String> getRelationRestrictions() {
        if(this.relation_restrictions == null) {
                this.relation_restrictions = new Vector<String>(); 
        }
        return this.relation_restrictions;
    }
    */

    private JPanel getJButtonsPanel(){
	FlowLayout flowLayout = new FlowLayout();
	flowLayout.setAlignment(FlowLayout.RIGHT);
	JPanel jButtonsPanel = new JPanel();
	jButtonsPanel.setLayout(flowLayout);
	JButton okbutton = new JButton();
	okbutton.setText("Accept");
	okbutton.addActionListener(this);
	okbutton.setActionCommand("accept");
	jButtonsPanel.add(okbutton, null);
	JButton resetbutton = new JButton();
	resetbutton.setText("Reset");
	resetbutton.setActionCommand("reset");
	resetbutton.addActionListener(this);
	jButtonsPanel.add(resetbutton, null);
	JButton cancelbutton = new JButton();
	cancelbutton.setText("Cancel");
	cancelbutton.setActionCommand("cancel");
	cancelbutton.addActionListener(this);
	jButtonsPanel.add(cancelbutton, null);

	return jButtonsPanel;
    }

    private JPanel getJRelationRestrictionsPanel(){
	GridLayout gridLayout = new GridLayout(2,4); //,-2,0);
	///FlowLayout flowLayout = new FlowLayout();
	///flowLayout.setAlignment(FlowLayout.RIGHT);
	JPanel jRestrictionsPanel = new JPanel();
        jRestrictionsPanel.setLayout(gridLayout);
	//jRestrictionsPanel.setLayout(new GridBagLayout());
        //GridBagConstraints c = new GridBagConstraints();

        JLabel tempLabel = new JLabel("2.(Optional) Restrict", JLabel.LEFT);
        tempLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        //c.anchor = GridBagConstraints.LINE_START;
        //c.gridx = 0;
        //c.gridy = 0;
        //c.gridwidth = 3;
	jRestrictionsPanel.add(tempLabel); //,c);
        tempLabel = new JLabel("relations by:", JLabel.LEFT);
        tempLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
	jRestrictionsPanel.add(tempLabel); 
	//jRestrictionsPanel.add(new JLabel("Restrict by"));

	//jRestrictionsPanel.add(Box.createHorizontalGlue());
	jRestrictionsPanel.add(Box.createHorizontalGlue());
	jRestrictionsPanel.add(Box.createHorizontalGlue());
        //c.gridx = 0;
        //c.gridy = 1;
        //c.gridwidth = 1;
        //c.gridheight = 2;
        //tempLabel = new JLabel("Attribute", JLabel.CENTER);
	//jRestrictionsPanel.add(tempLabel); 
        //tempLabel = new JLabel("Value", JLabel.CENTER);
	//jRestrictionsPanel.add(tempLabel); 
	//jRestrictionsPanel.add(Box.createHorizontalGlue());
	//jRestrictionsPanel.add(Box.createHorizontalGlue());

	JScrollPane sp = new JScrollPane();
	sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	sp.setPreferredSize(new Dimension(80, 150));
	sp.setViewportView(this.relation_restrictions_JList);
	jRestrictionsPanel.add(sp); //, c);

	sp = new JScrollPane();
	sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	sp.setPreferredSize(new Dimension(80, 150));
	sp.setViewportView(getJTextArea());
        //c.gridx = 1;
        //c.gridy = 1;
        //c.gridheight = 2;
	jRestrictionsPanel.add(sp); //, c);
	
	JButton addrestrictionbutton = new JButton("Add Restriction ->");
        //c.gridx = 2;
        //c.gridy = 1;
	jRestrictionsPanel.add(addrestrictionbutton); //, c);
	addrestrictionbutton.addActionListener(this);
	addrestrictionbutton.setActionCommand("add_restriction");

        //c.gridx = 3;
        //c.gridy = 1;
        //c.gridheight = 2;
        jRestrictionsPanel.add(getJTableScrollPane()); //,c);

	return jRestrictionsPanel;
    }

    private JTextArea getJTextArea() {
        if(this.relation_restriction_JTextArea == null) {
                relation_restriction_JTextArea = new JTextArea();
        	relation_restriction_JTextArea.setColumns(5);
        }
        return relation_restriction_JTextArea;
    }

    private JPanel getJTablePanel(){
	JScrollPane sp = new JScrollPane();
	sp.setViewportView(getJTable());
	sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	sp.setPreferredSize(new Dimension(170, 150));
	JPanel p = new JPanel();
	p.add(sp);
	return p;
    }

    private JScrollPane getJTableScrollPane(){
	JScrollPane sp = new JScrollPane();
	sp.setViewportView(getJTable());
	sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	sp.setPreferredSize(new Dimension(170, 150));
	return sp;
    }
 

    private JTable getJTable() {
	if (jTable == null) {
	    //DefaultTableModel model = new DefaultTableModel();
	    DefaultTableModel model = new DefaultTableModel() {
		    /**
		     * 
		     */
		    private static final long serialVersionUID = 1L;
		    
		    @Override
			public boolean isCellEditable(int row, int column) {
			return false;
		    }
		};
	    
	    model.addColumn("Attribute");
	    model.addColumn("Values");
	    
	    jTable = new JTable(model){
		    /**
		     * 
		     */
		    private static final long serialVersionUID = 1L;
		    
		    @Override
			public boolean isCellEditable(int row, int column) {
			// TODO Auto-generated method stub
			return false;
		    }
		};	
	}
	return jTable;
    }


    public void actionPerformed(ActionEvent e) {
	if( "add_restriction".equals(e.getActionCommand() ) ) {
	    if( this.relation_restrictions_JList.getSelectedValue()==null ) {
		JOptionPane.showMessageDialog(this,
					      "You must select the attribute identifier",
					      "Attribute identider error",
					      JOptionPane.ERROR_MESSAGE);
	    }
	    else if( this.getJTextArea().getText().compareTo("")==0 ){
		JOptionPane.showMessageDialog(this,
					      "You must specify some attribute value",
					      "Attribute Values Error",
					      JOptionPane.ERROR_MESSAGE);
	    }
	    else{
		StringBuffer buffer = new StringBuffer();
		String[] lines = this.getJTextArea().getText().split("\n");
		for( int i=0; i<lines.length; i++ ){
		    if( !this.is_empty(lines[i]) ){
			if( buffer.length()>0){
			    buffer.append(",");
			}
			buffer.append(lines[i].trim());
			this.relation_restrictions.add("(\""+this.relation_restrictions_JList.getSelectedValue()+"\",\""+lines[i].trim()+"\")");
		    }
		}
		if( buffer.length()>0 ){
		    ((DefaultTableModel)this.getJTable().getModel()).addRow(new Object[]{
			    this.relation_restrictions_JList.getSelectedValue(),
			    Utilities.join(this.getJTextArea().getText().split("\n"),",")});
		}
	    }
	}
        else if( "reset".equals(e.getActionCommand()) ){
	    this.available_relations_JList.clearSelection();
	}
        else if( "cancel".equals(e.getActionCommand()) ){
	    this.available_relations_JList.clearSelection();
	    this.dispose();
	}
	else if( "accept".equals(e.getActionCommand() ) ){
	    this.setVisible(false);
	}
    }

	/**
	 * Checks if an string is empty or not.
	 * @param text
	 * @return True if the String is empty
	 */
	private boolean is_empty(String text){
		System.out.println(text);
		System.out.println(text.matches(".*\\w+.*"));
		return !text.matches(".*\\w+.*");
	}

}
