import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Hashtable;
import java.util.Stack;
import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

class socketThread implements Runnable{
	
    static ServerSocket serverSocket = null;
    static Vector<BianaListener> listeners = null;
    static Hashtable<String,Object> temporal_values = null;
    
    private int port = -1;
    
    public socketThread(){
	if( serverSocket == null ){
	    this.createSocket();
	}
    }

    public socketThread(boolean force_to_wait){

	while( serverSocket != null ){
	    try{
		//do what you want to do before sleeping
		System.err.println("Waiting for closed socket...");
		Thread.currentThread().sleep(1000);//sleep for 1000 ms
		//do what you want to do after sleeptig
	    }
	    catch(InterruptedException ie){
		//If this thread was intrrupted by nother thread 
	    }
	}
	this.createSocket();
    }


    private void createSocket(){
	
	int start_port = 2000;
	boolean connected =false;
	
	while(connected == false && start_port<2010){
	    try {
		serverSocket = new ServerSocket(start_port);
		connected = true;
	    } catch (IOException e) {
		//e.printStackTrace();
		System.err.println("Error connecting to port "+start_port);
		start_port += 1;
	    }
	}
	if( connected==true ){
	    port = start_port;
	    listeners = new Vector<BianaListener>();
	    temporal_values = new Hashtable<String, Object>();
	    new Thread(this).start();
	    System.err.println("BIANA SERVER STARTED AT PORT "+port);
	}
	else{
	    System.err.println("IMPOSSIBLE TO START BIANA SERVER SOCKET BETWEEN PORTS "+start_port+"-"+port);
	}
    }

    public void waitForClosedSocket(){
	while( serverSocket != null ){
	    try{
		//do what you want to do before sleeping
		System.err.println("Waiting for closed socket...");
		Thread.currentThread().sleep(1000);//sleep for 1000 ms
		//do what you want to do after sleeptig
	    }
	    catch(InterruptedException ie){
		//If this thread was intrrupted by nother thread 
	    }
	}
    }

    public int getPort(){
	return this.port;
    }

    public void close(){
	System.err.println("Socket is being closed!");
	serverSocket = null;
    }
    
    public void add_listener(BianaListener pListener){
	listeners.add(pListener);
    }
    
    public void remove_listener(BianaListener pListener){
	listeners.remove(pListener);
    }

    public void run() {
	
	try{
	    //Read the response XML document
	    XMLReader parser = XMLReaderFactory.createXMLReader();
	    // There's a name conflict with java.net.ContentHandler
	    // so we have to use the fully package qualified name.
	    //org.xml.sax.ContentHandler handler = new BianaXMLHandler();
	    ContentHandler handler = new BianaXMLHandler();
	    parser.setContentHandler(handler);
	    
	    Socket connected_socket;
	    
	    while(true){
		connected_socket = serverSocket.accept();
		System.err.println("Server socket accepted connection");
		//InputSource input = null;
		try{
		    InputSource input = new InputSource(connected_socket.getInputStream());
		    parser.parse(input);
		    //input = new InputSource(connected_socket.getInputStream());
		    //if(input.getCharacterStream()!=null)
		    //	System.err.println(input.getCharacterStream().toString());
		}
		catch( Exception e ){
		    e.printStackTrace();
		    for( int i=0; i<listeners.size(); i++ ){
			listeners.get(i).error_message("SOCKET ERROR.",e.toString());
			//listeners.get(i).error_message("SOCKET ERROR.",e.toString()+"<br><br>TEXT:"+input.getCharacterStream().toString());
			listeners.get(i).reconnect();
			listeners.get(i).show_info_message("Due to an error, connection with BIANA has been restarted. Last command has not been correctly processed. If BIANA is not responding please restart BIANA!");
		    }
		}
	    }
	}
	catch( Exception e ){
	    e.printStackTrace();
	    for( int i=0; i<listeners.size(); i++ ){
	    	listeners.get(i).error_message("SOCKET ERROR",e.toString());
	    }
	}
    }

  
    private class BianaXMLHandler extends DefaultHandler {

	private Stack<XMLNode> XMLNodes_stack = new Stack<XMLNode>();

	public void startElement(String namespaceURI, String localName,
				 String qualifiedName, Attributes atts) throws SAXException {
	    

	    /* Makes a copy of the attributes as a Hashtable as atts is not copied... */
	    Hashtable<String, String> attributes = new Hashtable<String,String>();
	    for( int i=0; i<atts.getLength(); i++ ){
		attributes.put(atts.getQName(i),atts.getValue(i));
	    }

	    //System.err.println("Detected start element "+localName);

	    if(localName.equals("start_biana_process")){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).processing_message(atts.getValue("process"));
		}
	    }
	    else if( localName.equals("biana_to_gui") ){
		for( int i=0; i<listeners.size(); i++ ){
	               listeners.get(i).set_command_completed();
	        }
	    }

	    XMLNode new_node = new XMLNode(localName,attributes);

	    if( XMLNodes_stack.empty() == false ){
		XMLNode previous_node = XMLNodes_stack.peek();
		previous_node.addChild(new_node);
	    }
	    XMLNodes_stack.push(new_node);
	}

	public void endElement(String namespaceURI, String localName,
			       String qualifiedName) throws SAXException {

	    XMLNode node = XMLNodes_stack.pop();
	    
	    //System.err.println("Detected end element: "+localName);

	    /* EXTERNAL DATABASE INFO */
	    if( localName.equals("biana_to_gui") ){
		close();
	    }
	    else if(localName.equals("db_info")){
		//System.err.println("Starting reading db_info");
		XMLNode temp;
		Vector<String> available_unification_protocols = new Vector<String>();
		Vector<ExternalDatabase> available_external_databases = new Vector<ExternalDatabase>();
		Hashtable<String, String> attribute_ontologies = new Hashtable<String,String>();
		ArrayList<XMLNode> ont_childs = node.getChilds("ontology_attribute");
		ArrayList<XMLNode> eD_childs = node.getChilds("external_database");
		ArrayList<XMLNode> uP_childs = node.getChilds("unification_protocol");
		if( ont_childs != null ){
		    for( Iterator it = ont_childs.iterator(); it.hasNext(); ){
			temp = (XMLNode)it.next();
			attribute_ontologies.put(temp.getAttribute("attribute"),temp.getAttribute("ontology_name"));
			//System.err.println("Adding ontology "+temp.getAttribute("ontology_name")+" for attribute "+temp.getAttribute("attribute"));
		    }
		}
		if( eD_childs != null ){
		    for( Iterator it = eD_childs.iterator(); it.hasNext(); ){
			temp = (XMLNode)it.next();
			ExternalDatabase eDt = new ExternalDatabase(temp.getAttribute("name"),temp.getAttribute("version"),temp.getAttribute("description"),temp.getAttribute("id"));
			available_external_databases.add(eDt);
			if( temp.getChilds("eDB_external_entity_attribute") != null ){
			    //System.err.println("External entity attributes detected\n");
			    for( Iterator it_attrs = temp.getChilds("eDB_external_entity_attribute").iterator(); it_attrs.hasNext(); ){
				eDt.add_available_eE_attribute("eE",((XMLNode)it_attrs.next()).getAttribute("name"));
			    }
			}
			if( temp.getChilds("eDB_external_entity_type") != null ){
			    //System.err.println("External entity type detected\n");
			    for( Iterator it_attrs = temp.getChilds("eDB_external_entity_type").iterator(); it_attrs.hasNext(); ){
				eDt.add_available_eE_type(((XMLNode)it_attrs.next()).getAttribute("name"));
			    }
			}
			if( temp.getChilds("eDB_external_entity_relation_attribute") != null ){
			    //System.err.println("External entity relation attribute detected\n");
			    for( Iterator it_attrs = temp.getChilds("eDB_external_entity_relation_attribute").iterator(); it_attrs.hasNext(); ){
				eDt.add_available_eE_attribute("relation",((XMLNode)it_attrs.next()).getAttribute("name"));
			    }
			}
			if( temp.getChilds("eDB_external_entity_relation_type") != null ){
			    //System.err.println("External entity relation type detected\n");
			    for( Iterator it_attrs = temp.getChilds("eDB_external_entity_relation_type").iterator(); it_attrs.hasNext(); ){
				eDt.add_available_eEr_type(((XMLNode)it_attrs.next()).getAttribute("name"));
			    }
			}
		    }
		}
		if( uP_childs != null ){
		    for( Iterator it = uP_childs.iterator(); it.hasNext(); ){
			temp = (XMLNode)it.next();
			available_unification_protocols.add(temp.getAttribute("description"));
		    }
		}
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).new_biana_database(node.getAttribute("dbname"),
							node.getAttribute("dbhost"),
							node.getAttribute("dbuser"),
							node.getAttribute("dbpass"),
							available_unification_protocols,
							available_external_databases,
							attribute_ontologies);
		}
	    }
	    else if( localName.equals("not_available_database") ){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).not_available_biana_database(node.getAttribute("dbname"),
								  node.getAttribute("dbhost"));
		}
	    }
	    else if( localName.equals("biana_database_deleted") ){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).delete_biana_database(node.getAttribute("dbname"),
							   node.getAttribute("dbhost"));
		}
	    }
	    
	    /* SESSION MANAGEMENT AND GLOBAL INFORMATION */
	    else if (localName.equals("new_session")){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).new_session(node.getAttribute("dbname"),node.getAttribute("dbhost"),node.getAttribute("unification_protocol"),node.getAttribute("id"),node.getAttribute("description"));
		}
	    }
	    else if( localName.equals("available_parsers") ){
		ArrayList<XMLNode> parserChilds = node.getChilds("parser");
		for( Iterator it = parserChilds.iterator(); it.hasNext(); ){
		    XMLNode parserNode = (XMLNode)it.next();
		    for( int i=0; i<listeners.size(); i++ ){
			String[] a = new String[3];
			a[0] = parserNode.getAttribute("name");
			a[1] = parserNode.getAttribute("description");
			a[2] = parserNode.getAttribute("external_entity_definition");
			//System.err.println(a);
			listeners.get(i).add_available_parser(a);
		    }
		}
	    }
	    /*else if( localName.equals("unification_protocol_atom") ){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).add_unification_protocol_atom(node.getAttribute("dbA"), node.getAttribute("dbB"), node.getAttribute("attributes"));
		}
	    }*/
	    else if( localName.equals("external_entity_type") ){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).add_available_external_entity_type(node.getAttribute("name"));
		}
	    }
	    else if( localName.equals("external_entity_attribute") ){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).add_available_external_entity_attribute(node.getAttribute("name"));
		}
	    }
	    else if( localName.equals("external_entity_relation_type") ){
		//System.err.println("Receiving external entity relation type");
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).add_available_external_entity_relation_type(node.getAttribute("name"));
		}
	    }
	    else if( localName.equals("close_session")){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).close_session(node.getAttribute("sessionID"));
		}
	    }
	    else if( localName.equals("select_user_entity_set")){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).select_user_entity_set(node.getAttribute("id"));
		}
	    }
	    else if( localName.equals("clear_user_entity_set_selection")){
		//System.err.println("Socket has received the order of unselect all user entity set selection");
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).unselect_all_user_entity_sets();
		}
	    }
	    else if( localName.equals("session") ){
		XMLNode sessionNode = node;
		ArrayList<XMLNode> cnodes = node.getChilds("user_entity_set");
		if( cnodes != null ){
		    for( Iterator it_ues = cnodes.iterator(); it_ues.hasNext(); ){
			XMLNode ues = (XMLNode)it_ues.next();
			ArrayList<XMLNode> t = ues.getChilds("user_entity");
			ArrayList<String[]> nodeIds = new ArrayList<String[]>();
			if( t != null ){
			    for( Iterator it_ue = t.iterator(); it_ue.hasNext(); ){
				XMLNode ue = (XMLNode)it_ue.next();
				String a[] = {ue.getAttribute("id"),ue.getAttribute("type")};
				nodeIds.add(a);
				/*for( int i=0; i<listeners.size(); i++ ){
				    listeners.get(i).add_node(ues.getAttribute("id"),ue.getAttribute("id"),ue.getAttribute("type"));
				    }*/
			    }
			    for( int i=0; i<listeners.size(); i++ ){
				listeners.get(i).add_nodes(ues.getAttribute("id"),nodeIds);
			    }
			}
			
			t = ues.getChilds("user_entity_relation");
			ArrayList<String[]> edges = null;
			if( t != null ){
			    edges = new ArrayList<String[]>();
			    for( Iterator it_uer = t.iterator(); it_uer.hasNext(); ){
				XMLNode uer = (XMLNode)it_uer.next();
				String a[] = {uer.getAttribute("node1"),uer.getAttribute("node2"),uer.getAttribute("type"),uer.getAttribute("relation_id")};
				edges.add(a);
			    }
			    for( int i=0; i<listeners.size(); i++ ){
				//listeners.get(i).add_edge(ues.getAttribute("id"),uer.getAttribute("node1"),uer.getAttribute("node2"),uer.getAttribute("type"),uer.getAttribute("relation_id"));
				listeners.get(i).add_edges(ues.getAttribute("id"),edges);
			    }
			}

			t = ues.getChilds("new_tag");
			if( t != null ){
			    for( Iterator it_tag = t.iterator(); it_tag.hasNext(); ){
				XMLNode tag_node = (XMLNode)it_tag.next();
				for( int i=0; i<listeners.size(); i++ ){
				    listeners.get(i).addTag(ues.getAttribute("id"),tag_node.getAttribute("tag"),"nodeTag");
				}
			    }
			}

			t = ues.getChilds("new_relation_tag");
			if( t != null ){
			    for( Iterator it_tag = t.iterator(); it_tag.hasNext(); ){
				XMLNode tag_node = (XMLNode)it_tag.next();
				for( int i=0; i<listeners.size(); i++ ){
				    listeners.get(i).addTag(ues.getAttribute("id"),tag_node.getAttribute("tag"),"edgeTag");
				}
			    }
			}

			t = ues.getChilds("add_new_group");
			if( t != null ){
			    for( Iterator it_group = t.iterator(); it_group.hasNext(); ){
				XMLNode group_node = (XMLNode)it_group.next();
				for( int i=0; i<listeners.size(); i++ ){
				    listeners.get(i).addNodesToGroup(ues.getAttribute("id"),
								     group_node.getAttribute("groupName"),
								     group_node.getAttribute("groupType"),
								     group_node.getAttribute("groupID"),
								     group_node.getAttribute("parentGroupID"),
								     group_node.getAttribute("parentGroupName"),
								     group_node.getAttribute("node_ids").split(","));
				}
			    }
			}

			t = ues.getChilds("update_network_depth");
			if( t != null ){
			    for( Iterator it_und= t.iterator(); it_und.hasNext(); ){
				XMLNode und_node = (XMLNode)it_und.next();
				for( int i=0; i<listeners.size(); i++ ){
				    listeners.get(i).update_network(ues.getAttribute("id"),new Integer(und_node.getAttribute("levels")));
				}
			    }
			}

			t = ues.getChilds("clear_user_entity_selection");
			if( t != null ){
			    //System.err.println("Socket has received the instruction to unselect all user entities from "+ues.getAttribute("id"));
			    for( int i=0; i<listeners.size(); i++ ){
				listeners.get(i).unselect_user_entities(ues.getAttribute("id"));
			    }
			}

			t = ues.getChilds("clear_user_entity_relation_selection");
			if( t != null ){
			    //System.err.println("Socket has received the instruction to unselect all user entities relations from "+ues.getAttribute("id"));
			    for( int i=0; i<listeners.size(); i++ ){
				listeners.get(i).unselect_user_entity_relations(ues.getAttribute("id"));
			    }
			}

			t = ues.getChilds("select_user_entity");
			if( t != null ){
			    for( Iterator it_sue= t.iterator(); it_sue.hasNext(); ){
				XMLNode sue_node = (XMLNode)it_sue.next();
				String[] ids = sue_node.getAttribute("id").split(",");
				for( int i=0; i<listeners.size(); i++ ){
				    listeners.get(i).select_user_entity(ues.getAttribute("id"),ids);
				}
			    }
			}

			t = ues.getChilds("remove_user_entities");
			if( t != null ){
			    for( Iterator it_sue= t.iterator(); it_sue.hasNext(); ){
				XMLNode sue_node = (XMLNode)it_sue.next();
				String[] ids = sue_node.getAttribute("ids").split(",");
				for( int i=0; i<listeners.size(); i++ ){
				    listeners.get(i).remove_user_entities(ues.getAttribute("id"),ids);
				}
			    }
			}

			t = ues.getChilds("remove_user_entity_relations");
			if( t != null ){
			    for( Iterator it_sue= t.iterator(); it_sue.hasNext(); ){
				XMLNode sue_node = (XMLNode)it_sue.next();
				String[] triples = sue_node.getAttribute("ids").split(",");
				if( triples.length%3 != 0 ){
				    System.err.println("ERROR. EDGE ATTRIBUTE LIST MUST BE MULTIPLE OF 3");
				}
				else{
				    String[][] values = new String[triples.length/3][3];
				    for( int i=0; i<triples.length; i+=3 ){
					values[i/3][0] = triples[i];
					values[i/3][1] = triples[i+1];
					values[i/3][2] = triples[i+2];
				    }
				    for( int i=0; i<listeners.size(); i++ ){
					listeners.get(i).remove_user_entity_relations(ues.getAttribute("id"),values);
				    }
				}
			    }
			}

			t = ues.getChilds("select_user_entity_relation");
			if( t != null ){
			    for( Iterator it_sue= t.iterator(); it_sue.hasNext(); ){
				XMLNode sue_node = (XMLNode)it_sue.next();
				String[] triples = sue_node.getAttribute("id").split(",");
				if( triples.length%3 != 0 ){
				    System.err.println("ERROR. EDGE ATTRIBUTE LIST MUST BE MULTIPLE OF 3");
				}
				else{
				    String[][] values = new String[triples.length/3][3];
				    for( int i=0; i<triples.length; i+=3 ){
					values[i/3][0] = triples[i];
					values[i/3][1] = triples[i+1];
					values[i/3][2] = triples[i+2];
				    }
				    for( int i=0; i<listeners.size(); i++ ){
					//TODO
					listeners.get(i).select_user_entity_relation(ues.getAttribute("id"),values);
				    }
				}
			    }
			}
			
		    }
		    

		    // End user entity data (to update graphical interface)
		    for( int i=0; i<listeners.size(); i++ ){
			listeners.get(i).end_user_entity_set_data();
		    }
		}

		cnodes = node.getChilds("new_user_entity_set");
		if( cnodes != null ){
		    for( Iterator it = cnodes.iterator(); it.hasNext(); ){
			XMLNode nues = (XMLNode)it.next();
			for( int i=0; i<listeners.size(); i++ ){
			    listeners.get(i).new_user_entity_set(nues.getAttribute("id"));
			}
		    }
		}

		cnodes = node.getChilds("remove_user_entity_set");
		if( cnodes != null ){
		    for( Iterator it = cnodes.iterator(); it.hasNext(); ){
			XMLNode nues = (XMLNode)it.next();
			//System.err.println("Socket received order to destroy uEs "+nues.getAttribute("id"));
			for( int i=0; i<listeners.size(); i++ ){
			    listeners.get(i).destroy_user_entity_set(nues.getAttribute("id"));
			}
		    }
		}
	    }
	    else if(localName.equals("error_notification")){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).error_message(node.getAttribute("value"),node.getContent());
		}
	    }
	    else if(localName.equals("info_message")){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).show_info_message(node.getContent());
		}
	    }
	    else if(localName.equals("end_biana_process")){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).end_processing();
		}
		return;
	    }
	    else if(localName.equals("biana_ping_response")){
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).biana_ping_response();
		}
	    }
	    else if(localName.equals("table")){
		Vector<String> columns = new Vector<String>();
		Vector<Vector<String>> values = new Vector<Vector<String>>();
		Vector<String> rowIdentifiers = new Vector<String>();
		ArrayList<XMLNode> tr_childs = node.getChilds("tr");
		for( Iterator it = tr_childs.iterator(); it.hasNext(); ){
		    XMLNode p = (XMLNode)it.next();
		    ArrayList<XMLNode> th_childs = p.getChilds("th");
		    if( th_childs != null ){
			for( Iterator it_th = th_childs.iterator(); it_th.hasNext(); ){
			    columns.add(((XMLNode)it_th.next()).getContent());
			}
		    }
		    else{
			rowIdentifiers.add(p.getAttribute("rowID"));
			Vector<String> v = new Vector<String>();
			values.add(v);
			ArrayList<XMLNode> td_childs = p.getChilds("td");
			for( Iterator it_td = td_childs.iterator(); it_td.hasNext(); ){
			    v.add(((XMLNode)it_td.next()).getContent());
			}
		    }
		}
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).show_table_details(columns,values,rowIdentifiers, node.getAttribute("title"), node.getAttribute("command"));
		}
	    }
	    else if(localName.equals("ontology")){
		DefaultMutableTreeNode ontologyNode = new DefaultMutableTreeNode(node.getAttribute("type"));
		JTree tree = new JTree(ontologyNode);
		XMLNode currentNode = node;
		recursive_node_run(ontologyNode,node);
		System.err.println("Reading ontology");
		for( int i=0; i<listeners.size(); i++ ){
		    listeners.get(i).newOntologyTree(node.getAttribute("name"),tree);
		}
	    }
	    else{
	    	//System.err.println(localName+" NOT recognized");
	    }
	    for( int i=0; i<listeners.size(); i++ ){
		listeners.get(i).set_command_completed();
	    }	   
	    return;
	}
	
	public void characters(char[] ch, int start, int length)
	    throws SAXException {
	    
	    StringBuffer str = new StringBuffer();

	    for (int i = start; i < start+length; i++) {
		str.append(ch[i]);
	    }

	    (XMLNodes_stack.peek()).appendContent(str.toString());

	}

	private void recursive_node_run(DefaultMutableTreeNode parentTreeNode, XMLNode parentNode){
	    ArrayList<XMLNode> nodeChilds = parentNode.getChilds("node");
	    if( nodeChilds != null ){
		for( Iterator it = nodeChilds.iterator(); it.hasNext(); ){
		    XMLNode newNode = (XMLNode)it.next();
		    //DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(newNode.getAttribute("id"));
		    OntologyTreeNode newTreeNode = new OntologyTreeNode(newNode.getAttribute("id"),newNode.getAttribute("ontologyNodeID"));
		    parentTreeNode.add(newTreeNode);
		    recursive_node_run(newTreeNode,newNode);
		}
	    }
	}


	private class XMLNode {
	    
	    private String nodeName;
	    private Hashtable<String,String> attributes;
	    private Hashtable<String, ArrayList<XMLNode>> childs;
	    private StringBuffer content;
	    
	    public XMLNode(String pNodeName, Hashtable<String,String> pAttributes){
		this.nodeName = pNodeName;
		this.attributes = pAttributes;
		this.childs = new Hashtable<String, ArrayList<XMLNode>>();
		this.content = new StringBuffer();
	    }

	    public void appendContent(String pContent){
		this.content.append(pContent);
	    }

	    public String getContent(){
		return this.content.toString();
	    }
	    
	    public String getNodeName(){
		return this.nodeName;
	    }

	    public String getAttribute(String attributeName){
		return this.attributes.get(attributeName);
	    }
	    
	    public void addChild(XMLNode pChildNode){
		if( this.childs.containsKey(pChildNode.getNodeName()) ){
		    this.childs.get(pChildNode.getNodeName()).add(pChildNode);
		}
		else{
		    ArrayList<XMLNode> new_list = new ArrayList<XMLNode>();
		    new_list.add(pChildNode);
		    this.childs.put(pChildNode.getNodeName(),new_list);
		}
	    }

	    public ArrayList<XMLNode> getChilds(String pChildNodeName){
		return this.childs.get(pChildNodeName);
	    }

	    public void print_childs(){
		for( Iterator it = this.childs.keySet().iterator(); it.hasNext(); ){
		    System.err.println(it.next());
		}
	    }
	}
    }
    
    public static void main(String[] args) {
    	new Thread( new socketThread() ).start();
    }
}


