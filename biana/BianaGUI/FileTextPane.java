
import javax.swing.JTextPane;
import java.io.*;

public class FileTextPane extends JTextPane {

    private static final long serialVersionUID = 1L;
	
    public FileTextPane( String pFileName ) throws IOException{

	BufferedReader reader = new BufferedReader(new FileReader(pFileName));
	StringBuffer temp = new StringBuffer();

	if( reader == null ){
	    System.err.println("File with database info not found");
	}
	while( reader.ready() ){
	    temp.append(reader.readLine());
	    temp.append("\n");
	}
	
	setText(temp.toString());
	setEditable(false);
	
	reader.close();
    }
}
