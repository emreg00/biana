
import java.io.*;
import java.util.*;
import java.io.OutputStream;

public class PythonInterpreter implements Runnable{

    // these are out here so the inner class can access them...
    private BufferedReader in = null;
    private boolean ok = true;
    
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    
    private ProcessBuilder pb = null;
    private Process p = null;
    
    private PrintStream out = null;

    private BianaProcessController controller = null;
    
    public PythonInterpreter(String pExecutable, InputStream pInputStream, OutputStream pOutputStream, BianaProcessController controller) throws Exception{
    	
    	this.inputStream = pInputStream;
    	this.outputStream = pOutputStream;

	this.controller = controller;
    		
    	//Create the process builder
    	this.pb = new ProcessBuilder(pExecutable, "-i"); // force interactive
    	pb.redirectErrorStream(true); // combine stdout and stderr
    	
    	//Starts the process
    	boolean started = false;
    	try{
    		p = pb.start();
    		started = true;
    		this.in = new BufferedReader(new InputStreamReader(p.getInputStream()));
    		out = new PrintStream(p.getOutputStream(), true);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		if (started) p.destroy();
    		throw new Exception();
    	}
    	
    	//Start the redirection of data stream
    	new Thread(this).start();
    }
    
    public void run(){
	
	System.err.println("Starting python interpreter...");
	
	try{			
	    
	    final Scanner userIn = new Scanner(this.inputStream);
	    final Thread mainthread = Thread.currentThread();
	    
	    class ReaderThread implements Runnable {
		
		public void run() {
			System.err.println("Python interpreter has started to receive the input");
		    try {
			while (true) {
			    int c = in.read();
			    if (c == -1) {
				ok = false;
				break;
			    }
			    outputStream.write(c);
			}
		    }
		    catch (IOException exc) {
			ok = false;
		    }
		    
		    mainthread.interrupt();
		}
	    }
	    
	    Thread rt = new Thread(new ReaderThread());
	    rt.start();
	    
	    while (ok) {
		try {
		    String input = userIn.nextLine();
		    out.println(input);
		}
		catch (NoSuchElementException exc) {
		    ok = false;
		}
	    }
	    p.destroy();
	    p.waitFor();

	    if( p.exitValue() == 10 ) controller.error_message("BIANA Python Package not Found","Check whether BIANA python package is correctly installed & configured:\n- Check if BIANA installation directory is correctly added to PYTHONPATH environment variable.\n- Restart your computer if you have not done so after installing BIANA.");
	    
	    System.err.println("Interpreter finished because input finished");
	}
	catch( Exception e ){
	    System.err.println("Exception in run method of PythonIntepreter... why???");
	    e.printStackTrace();
	}
    }
	
	
    public static void main(String args[]){
	
	try{
		//new PythonInterpreter("C:/Archivos de programa/python2.5/python.exe",System.in,System.out);
		new PythonInterpreter("C:/Archivos de programa/python2.5/python.exe",System.in,System.out, null);
	}
	catch( Exception e ){
	    System.err.println("Exception...");
	    e.printStackTrace();
	}
    }
}





