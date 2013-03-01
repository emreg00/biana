
import java.util.Vector;
import javax.swing.JTree;
import java.util.Hashtable;
import java.util.ArrayList;

/**
 * 
 * @author Javier Garcia
 *
 */
public interface BianaListener {

    /* Process control */
    public void processing_message(String message);
    public void end_processing();
    public void error_message(String pMessageSummary, String errorMessage);
    public void show_info_message(String message);
    public void closedConnection(String pMessage);
    public void reconnect();
    
    public void set_command_completed();

    public void new_user_entity_set(String pName);
    public void update_network(String pSetName, int levels);
    public void addTag(String pSetName, String tagName, String tagType);
    public void add_node(String pSetName, String node, String type);
    public void add_nodes(String pSetName, ArrayList<String[]> nodes_types);
    public void add_edges(String pSetName, ArrayList<String[]> edges_types_and_id);
    public void add_edge(String pSetName, String node1, String node2, String type, String relation_id);
    public void select_user_entity(String pSetName, String node);
    public void select_user_entity(String pSetName, String[] node);
    public void select_user_entity_relation(String pSetName, String node1, String node2, String type);
    public void select_user_entity_relation(String pSetName, String[][] edges);
    public void unselect_user_entities(String pSetName);
    public void unselect_user_entity_relations(String pSetName);
    public void remove_user_entities(String pSetName, String[] node);
    public void remove_user_entity_relations(String pSetName, String[][] edges);
    public void select_user_entity_set(String pSetName);
    public void destroy_user_entity_set(String pSetName);
    public void unselect_all_user_entity_sets();

    public void addNodesToGroup(String pSetName, String pGroupName, String pGroupType, String pGroupID, String pParentGroupID, String pParentGroupName, String[] nodes);

    /* Biana databases administration */
    public void new_biana_database(String dbname, String dbhost, String dbuser, String dbpass, Vector<String> pAvailableAttributes, Vector<ExternalDatabase> pExternalDatabases, Hashtable<String,String> pAttributeOntologies);
    public void delete_biana_database(String dbname, String dbhost);
    public void not_available_biana_database(String dbname, String dbhost);

    public void show_table_details(Vector<String> columns, Vector<Vector<String>> values, Vector<String> rowIdentifiers, String title, String command);
    public void newOntologyTree(String name, JTree tree);
    public void add_available_parser(String[] attributes);
    public void add_available_external_entity_type(String name);
    public void add_available_external_entity_relation_type(String name);
    public void add_available_external_entity_attribute(String name);
    
    //public void add_unification_protocol_atom(String eDbA, String eDbB, String attributes);
    
    /* Session Control */
    public void new_session(String dbname, String dbhost, String pUnificationProtocol, String pSessionID, String pSessionDescription);
    public void close_session(String pSessionID);
    public void end_user_entity_set_data();


    public void biana_ping_response();
}
