import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.Cursor;
import javax.swing.JTextPane;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;
import javax.swing.Action;
import javax.swing.text.*;
import javax.swing.JFrame;
import java.awt.Color;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

public class ShellPane extends JTextPane implements MouseListener, ActionListener, ClipboardOwner{

    private int MAX_CHAR = -1;                        //Max characters allowed to the textpane (-1 if not limited)
    private static final long serialVersionUID = 1L;
    
    private InputStream in = null;
    private OutputStream out = null;
    
    private int last_editable_position = 0;
    
    private ArrayList<String> history = null;
    private StringBuffer current_command = null;
    private int current_history_position;
    private int max_history_position;
    private JPopupMenu popupMenu;

    private Style user_typed_style = null;
    private Style input_stream_typed_style = null;
    private Style added_command_style = null;
    
    private StringBuffer inBuffer = null;
    private boolean wait_buffer = false;

    private StyledDocument document = null;

    Keymap newmap = addKeymap("KeymapExampleMap", this.getKeymap());
    
    private int charact_num = 0;
    
    public ShellPane( InputStream pIn, OutputStream pOut ){
	in = pIn;
	out = pOut;
	last_editable_position = 0;
	Thread rt = new Thread(new ReaderThread());
	Thread wt = new Thread(new WriterThread());
	inBuffer = new StringBuffer();
	rt.start();
	wt.start();
	this.addKeyListener(new keyListener());

	this.history = new ArrayList<String>();
	this.current_command = new StringBuffer();
	this.current_history_position = -1; 
	this.max_history_position = -1;

	this.document = (StyledDocument)getDocument();
	
	/* History related */
	Action history_up = new HistoryUp();
	Action history_down = new HistoryDown();
	Action send_command = new SendCommand();
	newmap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), history_up);
	newmap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0), history_up);
	newmap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), history_down);
	newmap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0), history_down);
	newmap.removeKeyStrokeBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
	newmap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), send_command);
	this.setKeymap(newmap);

	/* Mouse listener related */
	this.addMouseListener(this);

	/* Start popup menu */
	this.popupMenu = new JPopupMenu();
	JMenuItem temp = new JMenuItem("Reset shell content");
	this.popupMenu.add(temp);
	temp.setActionCommand("reset_shell");
	temp.addActionListener(this);

	temp = new JMenuItem("Copy all commands to clipboard");
	this.popupMenu.add(temp);
	temp.addActionListener(this);
	temp.setActionCommand("copy_all_to_clipboard");


	/* Text Style */
	user_typed_style = document.addStyle("user",null);
	StyleConstants.setForeground(user_typed_style, Color.red);

	input_stream_typed_style = document.addStyle("stream",null);
	StyleConstants.setForeground(input_stream_typed_style, Color.gray);
	StyleConstants.setItalic(input_stream_typed_style, true);
	
	added_command_style = document.addStyle("added_command",null);
	StyleConstants.setForeground(added_command_style, Color.black);	   
    }

    public void mousePressed(MouseEvent e){
	if(getCaretPosition()<last_editable_position){
	    setEditable(false);
	}
	else{
	    setEditable(true);
	}

	if( e.isPopupTrigger() ){
	    this.popupMenu.show(this,e.getX(),e.getY());
	}
    }

    public void mouseClicked(MouseEvent e){}
    public void mouseReleased(MouseEvent e){
	if(getCaretPosition()<last_editable_position){
	    setEditable(false);
	}
	else{
	    setEditable(true);
	}
	if( e.isPopupTrigger() ){
	    this.popupMenu.show(this,e.getX(),e.getY());
	}
    }

    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}

    
    public void setMaxChar(int pMaxChar){
	this.MAX_CHAR = pMaxChar;
    }
    
    
    protected void processKeyEvent(KeyEvent evt){
	if( last_editable_position >= getCaretPosition() && evt.getKeyChar()==8 ){
	    return;
	}
	super.processKeyEvent(evt);
    }

    public void reset(){
	//System.err.println("Reseting shell");
	try{
	    int length = document.getLength()-1;
	    document.remove(0,length);
	    this.last_editable_position = 0;
	    out.write(10);
	}
	catch(Exception e){
	    System.err.println("ERROR RESETING SHELL");
	}
    }
	
	
    final Thread mainthread = Thread.currentThread();
	
    class WriterThread implements Runnable {
    	
    	public void run() {
    	
    		while (true){
		    try{
			Thread.sleep(100);
			if( inBuffer.length()>0 ){
			    wait_buffer=true;
			    String content = inBuffer.toString();
			    inBuffer.setLength(0);
			    wait_buffer=false;
			    
			    if( content.charAt(content.length()-1)==' ' ){
				document.insertString(document.getLength(), content.substring(0,content.length()-1), input_stream_typed_style);
				document.insertString(document.getLength(), " ", user_typed_style);
			    }
			    else{
				document.insertString(document.getLength(), content, input_stream_typed_style);
			    }
			    
			    charact_num += content.length();
			    
			    if( MAX_CHAR != -1 ){
				if( charact_num > MAX_CHAR){
				    document.remove(0,document.getLength()-MAX_CHAR);
				}
			    }
			    last_editable_position = document.getLength();
			    setCaretPosition(document.getLength());
			}
		    }
		    catch(Exception e){
			System.err.println("Exception in shell writting thread...");
			//System.err.println(e.getStackTrace());
			e.printStackTrace();
		    }
    		}
    		
    	}
    	
    }
    
    
    class ReaderThread implements Runnable {
	
	public void run() {			
	    try {
		while (true) {
		    int c = in.read();
		    if (c == -1){
		    	System.err.println("Shell detected stream break...");
		    	break;
		    }
		    
		    while(wait_buffer){
		    	//Waiting
		    }

		    inBuffer.append((char)c);
		    /*
		    if( ((char)c)!=' ' ){
			document.insertString(document.getLength(), ""+((char)c), input_stream_typed_style);
		    }
		    else{
			document.insertString(document.getLength(), ""+((char)c), user_typed_style);
		    }
		    
		    charact_num++;
		    if( MAX_CHAR != -1 ){
			if( charact_num > MAX_CHAR){
			    document.remove(0,document.getLength()-MAX_CHAR);
			}
		    }
		    last_editable_position = document.getLength();
		    setCaretPosition(document.getLength());*/
		}
	    }
	    catch( Exception exc ){
		//exc.printStackTrace();
		System.err.println("Shell input strem has been stopped. Shell is not listening anything now. You should restart BIANA!");
	    }
	    
	    // try to interrupt the other thread
	    try {
		in.close();
	    } catch (IOException exc) {}
	    mainthread.interrupt();
	}
    }
    
    class keyListener implements KeyListener{
	
	private String command = null;
	
	public void keyReleased(KeyEvent e) {}
	
	public void keyPressed(KeyEvent e) {
	    if( "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\"\',. ;:-_+><*/()[]{}=!".indexOf(e.getKeyChar()) == -1 ){
		return;
	    }
	    current_command.append(e.getKeyChar());
	}
	
	public void keyTyped(KeyEvent e) {
	    setEditable(true);
	    if(getCaretPosition()<last_editable_position){
		setCaretPosition(document.getLength());
	    }
	    try{
		document.insertString(document.getLength(), " ", user_typed_style);
		document.remove(document.getLength()-1,1);
	    }
	    catch( Exception exc ){
		exc.printStackTrace();
	    }
	}
    }
    
    public void add_to_history(String command){
	this.history.add(command);
    }
    
    public String getHistoryText(){
	return Utilities.join(this.history,System.getProperty("line.separator"));
    }
	
    private class HistoryUp extends TextAction {
	public HistoryUp(){
	    super("gets-the-previous-history-command-action");
	}
	public void actionPerformed(ActionEvent e) {
	    try{
		if( current_history_position >= 0){
		    current_history_position-=1;
		    if( current_history_position != -1 ){
			document.remove(last_editable_position, document.getLength()-last_editable_position);
			document.insertString(last_editable_position, history.get(current_history_position), added_command_style);
		    }
		}
		setCaretPosition(document.getLength());
	    }
	    catch( Exception exc ){
		exc.printStackTrace();
	    }	
	}
    }

    public void add_command(String command){
	try{
	    document.insertString(document.getLength(), command+"\n", added_command_style);
	    setCaretPosition(document.getLength());
	}
	catch( Exception exc ){
	    exc.printStackTrace();
	}
    }

    private class SendCommand extends TextAction {

	private String command = null;

	public SendCommand(){
	    super("sends-command-to-controller");
	}

	public void actionPerformed(ActionEvent e){
	    try{
		command = document.getText(last_editable_position, document.getLength()-last_editable_position);
		history.add(command);
		out.write(command.getBytes());
		current_history_position = history.size();
		max_history_position = current_history_position;
		out.write(10);
		current_command.setLength(0);
		document.insertString(document.getLength(), "\n", getStyle("error"));
	    }
	    catch(Exception err){
		System.err.println(err.getMessage());
	    }
	}
    }

    private class HistoryDown extends TextAction {
	public HistoryDown(){
	    super("gets-the-next-history-command-action");
	}
	public void actionPerformed(ActionEvent e) {
	    try{
		if( current_history_position < max_history_position-1){
		    current_history_position+=1;
		    document.remove(last_editable_position, document.getLength()-last_editable_position);
		    document.insertString(last_editable_position, history.get(current_history_position), added_command_style);
		}
		else{
		    current_history_position = history.size();
		    document.remove(last_editable_position, document.getLength()-last_editable_position);
		    document.insertString(last_editable_position, current_command.toString(), user_typed_style);
		}
		setCaretPosition(document.getLength());
	    }
	    catch( Exception exc ){
		exc.printStackTrace();
	    }
	}
    }

    public static void main(String[] args) {

	JFrame t = new JFrame();
	t.setSize(200,200);
	try{
	    PipedOutputStream outPipe = new PipedOutputStream();
	    PipedInputStream inPipe = new PipedInputStream(outPipe);
	    PipedOutputStream outPipea = new PipedOutputStream();
	    PipedInputStream inPipea = new PipedInputStream(outPipea);

	    //PythonInterpreter p = new PythonInterpreter("/soft/bin/python2.5",inPipe,outPipea);
	    PythonInterpreter p = new PythonInterpreter("/soft/bin/python2.5",inPipe,outPipea, null);
	    t.add( new ShellPane(inPipea, outPipe) );
	}
	catch( Exception e ){
	    System.err.println("Error while creating pipes...");
	}

	
	t.setVisible(true);
	
    }

    public void actionPerformed(ActionEvent e) {

	if( e.getActionCommand().equals("reset_shell") ){
	    this.reset();
	}
	else if( e.getActionCommand().equals("copy_all_to_clipboard") ){
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents( new StringSelection(this.getHistoryText()), this);
	}
    }

    public void lostOwnership( Clipboard aClipboard, Transferable aContents ){}

}
