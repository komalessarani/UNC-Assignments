package DiGraph_A5;

import java.util.*;

public class DiGraph implements DiGraphInterface {

	// in here go all your data and methods for the graph
	HashMap<String, Node> NodeMap = new HashMap<String, Node>(); // the key is the String label and the value is the
																	// Node
	HashMap<Long, Edge> EdgeMap = new HashMap<Long, Edge>();
	HashSet<Long> NodeID = new HashSet<Long>(); // store Nodes' id

	public DiGraph() { // default constructor
		// explicitly include this
		// we need to have the default constructor
		// if you then write others, this one will still be there
	}

	@Override
	public boolean addNode(long idNum, String label) {
		// if the idNum is out of bounds or the label has an invalid value
		if (idNum < 0 || label == null) {
			return false;
		}
		// if there is no node with this label and there is no node with this id, then
		// do the following
		else if (NodeMap.containsKey(label) == false && NodeID.contains(idNum) == false) {
			Node newNode = new Node(idNum, label); // create a new node
			NodeMap.put(label, newNode); // add it to the hash map
			NodeID.add(idNum); // add the node's id to the hashset
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean addEdge(long idNum, String sLabel, String dLabel, long weight, String eLabel) {
		// if the id is less than 0 or if the id isn't unique, return false
		if (idNum < 0 || EdgeMap.containsKey(idNum) == true) {
			return false;
		} else if (NodeMap.containsKey(sLabel) == false || NodeMap.containsKey(dLabel) == false) {
			return false;
		} else {
			Node sNode = NodeMap.get(sLabel); // create a temp source node
			Node dNode = NodeMap.get(dLabel); // create a temp destination node
			// if there is already an edge between the source and destination node return
			// false
			if (sNode.outEdge.containsKey(dLabel) && dNode.inEdge.containsKey(sLabel)) {
				return false;
			} else {
				Edge newEdge = new Edge(sLabel, dLabel, eLabel, idNum, weight); // create a temp Edge object
				EdgeMap.put(idNum, newEdge); // add to the graph
				sNode.outEdge.put(dLabel, newEdge);
				dNode.inEdge.put(sLabel, newEdge);
				dNode.count++;
				return true;
			}
		}
	}

	@Override
	public boolean delNode(String label) {
		// if node doesn't exist, return false
		if (NodeMap.containsKey(label) == false) {
			return false;
		} else {
			// create a node that contains the data of the node that needs to be removed
			Node remNode = NodeMap.get(label);

			// use iterators to go through the nodes edges
			Iterator<String> inSourceL = remNode.inEdge.keySet().iterator();
			Iterator<String> outSourceD = remNode.outEdge.keySet().iterator();

			// delete the edges until there are none left
			while (inSourceL.hasNext()) {
				delEdge(inSourceL.next(), remNode.label);
			}
			while (outSourceD.hasNext()) {
				delEdge(remNode.label, outSourceD.next());
			}

			// remove from the node from the node map and the id from the node id hash set
			NodeMap.remove(label, remNode);
			NodeID.remove(remNode.id);
			return true;
		}
	}

	@Override
	public boolean delEdge(String sLabel, String dLabel) {
		// if the node map doesn't have either labels then return false
		if (NodeMap.containsKey(sLabel) == false || NodeMap.containsKey(dLabel) == false) {
			return false;
		} else {
			// create nodes for source and destination
			Node sNode = NodeMap.get(sLabel);
			Node dNode = NodeMap.get(dLabel);

			// if the source's edge points to the destination's label
			if (sNode.outEdge.containsKey(dLabel)) {
				// create an edge which will be removed
				Edge rEdge = NodeMap.get(sLabel).outEdge.get(dLabel);
				sNode.outEdge.remove(dLabel, rEdge); // use the source node to remove the destination label
				dNode.inEdge.remove(sLabel, rEdge); // use the destination node to remove the source label
				dNode.count--; // decrease the node count
				EdgeMap.remove(rEdge.id); // remove the edge from the map
				return true;
			}
			return false;
		}
	}

	@Override
	public long numNodes() {
		return NodeID.size();
	}

	@Override
	public long numEdges() {
		return EdgeMap.size();
	}

	@Override
	public ShortestPathInfo[] shortestPath(String label) {
		// create shortest path array that will be returned at end
		ShortestPathInfo[] shortP = new ShortestPathInfo[NodeID.size()];

		// create a MinBinHeap object
		MinBinHeap pq = new MinBinHeap();

		// create an starting entry pair to add to pq
		EntryPair source = new EntryPair(label, 0);
		pq.insert(source);

		shortP[0] = new ShortestPathInfo(label, 0);
		int i = 0;

		// use while loop to go through priority queue until it is empty
		while (pq.size() != 0) {
			// assign the pq's min to the current node
			Node curr = NodeMap.get(pq.getMin().value);
			long cDis = pq.getMin().priority; // get the distance of the current node (aka the min)
			pq.delMin();

			// if the node is known, continue, else make it known
			if (curr.flag == 1) {
				continue;
			} else {
				shortP[i] = new ShortestPathInfo(curr.label, cDis); // add the node's distance to the array
				curr.flag = 1; // make the node known
			}

			// get adjacent nodes
			Iterator<Edge> adj = curr.outEdge.values().iterator(); // use iterator to go through the adjacent nodes
			while (adj.hasNext()) {
				Node temp = NodeMap.get(adj.next().destinationLabel); // give the temp node the value of the
																		// destinatione edge
				if (temp.flag == 0) { // if not known
					long nDist = curr.outEdge.get(temp.label).weight + cDis; // calculate new distance by adding the
																				// previous and new distance

					if (temp.distance == 0 || temp.distance > nDist) { // if the distance is 0 or bigger than the new distance then change 
						temp.distance = nDist;						   // the distance to the new distance 
																	   // and add the node with this data to the pq
						// create a pair with the adj node and its distance
						EntryPair adjPair = new EntryPair(temp.label, temp.distance);
						// add this pair to the pq
						pq.insert(adjPair);
					}
				}
			}
			i++;
		}
		// if the node is unreachable, set their distances to -1
		if (i < NodeID.size()) {
			for (Node n : NodeMap.values()) {
				if (n.flag == 0) { // if node not known
					n.distance--;
					shortP[i] = new ShortestPathInfo(n.label, n.distance);
					i++;
				}
			}
		}

		return shortP;
	}

}
