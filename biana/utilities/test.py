import biana.utilities.graph_utilities as gu
import networkx as networkx
graph = networkx.Graph()
#graph.add_edges_from([(1,2),(1,3),(2,3),(2,4),(4,5)])
graph.add_edges_from([(1,2), (2,3), (3,4), (2,4), (3,5)])

def main():
    #new_graph = test_random_model()
    #new_graph = gu.randomize_graph(graph, "erdos_renyi")
    #new_graph = gu.randomize_graph(graph, "barabasi_albert")
    #new_graph = gu.prune_graph_at_given_percentage(graph, 40, [2])
    new_graph = gu.permute_graph_at_given_percentage(graph, 50)
    print new_graph.edges()

def test_random_model():
    n_node = graph.number_of_nodes()
    n_edge = graph.number_of_edges()

    p = float(2 * n_edge) / (n_node*n_node - 2*n_node)
    #new_graph = networkx.erdos_renyi_graph(n_node, p)
    new_graph = networkx.barabasi_albert_graph(n_node, n_edge/n_node)
    mapping = dict(zip(new_graph.nodes(), graph.nodes()))
    new_graph = networkx.relabel_nodes(new_graph, mapping)

    return new_graph

    available_edges = graph.edges()

    for edge in new_graph.edges():
	if len(available_edges) > 0:
	    edge_org = available_edges.pop()
	    new_graph.add_edge(edge[0], edge[1], graph.get_edge(edge_org[0], edge_org[1]))
	else:
	    print "Removing:", edge
	    new_graph.remove_edge(edge[0], edge[1])

    for edge_org in available_edges:
	print "Adding:", edge_org
	new_graph.add_edge(edge_org[0], edge_org[1], graph.get_edge(edge_org[0], edge_org[1]))
    return new_graph

if __name__ == "__main__":
    main()

