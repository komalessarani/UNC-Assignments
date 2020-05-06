package DiGraph_A5;
import java.util.HashMap;

public class Node {
	long id;
	HashMap<String, Edge> inEdge;		//source edge map
	HashMap<String, Edge> outEdge;		//destination edge map
	String label;
	int count;
	int flag;
	long distance;	 //min distance from start node to another
	
	public Node(long id, String label) {
		this.id = id;
		this.label = label;
		this.inEdge = new HashMap();
		this.outEdge = new HashMap();
		this.count  = 0;
		this.flag = 0;	//to determine if node is known or not
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public long getID() {
		return this.id;
	}
}
