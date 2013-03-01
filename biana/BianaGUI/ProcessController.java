

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.io.FileOutputStream;


public class ProcessController extends Thread {

	private BlockingQueue<String[]> queue;
	private DatabaseAdministratorPane window;
	private int current_process;
	private boolean running = false;
	
	public ProcessController(DatabaseAdministratorPane parentwindow, BlockingQueue<String[]> pQueue){
		this.queue = pQueue;
		this.window = parentwindow;
		this.current_process = -1;
		this.start();
	}
	
	public void add_new_process(String[] command){
		try{
			//System.err.println("Adding command to queue");
			this.queue.put(command);
		}
		catch( Exception e ){
			e.getMessage();
		}
	}
	
	private void execute(String[] process_command){

		this.running = true;
		//ProcessBuilder pb = new ProcessBuilder("C:\\Archivos de programa\\python2.5\\python", "C:\\Archivos de programa\\python2.5\\test.py");
		//ProcessBuilder pb = new ProcessBuilder(Executables.PYTHON_EXEC, "C:\\Archivos de programa\\python2.5\\test.py", "--parameter2", "--parameter3", "--parameter4=\\\"C:\\Archivos de programa\\python2.5\\test.py\\\"");
		//System.err.println("Executing "+process_command);
		StringBuffer str = new StringBuffer();
		for( int i=0; i<process_command.length; i++ ){
			str.append(process_command[i]);
			//str.append("\n");
		}
		System.err.println("Executing "+str.toString());
		ProcessBuilder pb = new ProcessBuilder(process_command);
		pb.redirectErrorStream(true);
		
		Process p = null;
		boolean started = false;
		
		try {
		    p = pb.start();
		    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    started = true;
		    this.current_process++;
		    FileOutputStream fout = null;
		    try {
		        File temp = File.createTempFile("bianaParser", ".txt");
		        temp.deleteOnExit();
		        fout = new FileOutputStream(temp.getAbsoluteFile());
		        this.window.setCurrentOutput(this.current_process, temp.getAbsolutePath());
		        
			} catch (IOException err) {
				System.err.println("Error in creating parser out temp file");
		    }
		    //this.window.setCurrentOutput(this.current_process,"process_"+this.current_process);
		    this.window.change_process_status(current_process, "Running");
		    int c;
		    while(true){
			c = in.read();
			if( c==-1 ){
			    break;
			}
			fout.write(c);
		    }
		    fout.close();
		}
		catch (IOException exc) {
			exc.printStackTrace();
			if (started) p.destroy();
			return;
		}
		
		try{
			//System.err.println("Going to wait to finish process...");
			System.err.println(p.waitFor());
			//System.err.println("Process "+this.current_process+" really finished...");
			System.err.println("Status: "+p.exitValue());
			if( p.exitValue() == 0 ){
			    this.window.change_process_status(current_process, "Finished");
			}
			else{
			    this.window.change_process_status(current_process, "Error in parsing");
			}
		}
		catch( InterruptedException e ){
			p.destroy();
			e.printStackTrace();
		}
		catch( Exception e ){
			e.printStackTrace();
		}
		//System.err.println("Going to destroy...");
		
		//p.destroy();
		this.running = false;
	}
	
	public void run(){
		
		//System.err.println("Executing process management...");

		try{
			while(true){
				try{
					String[] process_command = queue.take();
					//System.err.println("PROCESS SUBMITTED: "+process_command);
					//System.err.println("Going to execute...");
					//System.err.println("Running status: "+this.running);
					while( this.running ){
						System.err.println("Waiting in bucle...");
					}
					this.running = true;
					this.execute(process_command);
					this.running = false;
					//Thread.sleep(100000);
					System.err.println(Thread.currentThread().isInterrupted());
					//Thread.sleep(1000000000);
					System.err.println("Execution finished...");
				}
		        catch (InterruptedException e) {
		        	//e.printStackTrace();
		        	//System.err.println("Exception on retrieving...");
		        	//throw e;
		             // Restore the interrupted status
		        	 //Thread.currentThread().interrupt();
		        }
			}
			
		}
		catch( Exception e ){
			e.printStackTrace();
		}
		
		//this.running_process = false;
	}
}
