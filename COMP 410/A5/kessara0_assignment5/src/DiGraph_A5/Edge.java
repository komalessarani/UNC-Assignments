package DiGraph_A5;

public class Edge {
	String sourceLabel;		//the edge source
	String destinationLabel;	//the edge you want to go to 
	String edgeLabel;		
	long id, weight;
	
	public Edge(String sourceLabel, String destinationLabel, String edgeLabel, long id, long weight) {
		this.sourceLabel = sourceLabel;
		this.destinationLabel = destinationLabel;
		this.id = id;
		this.weight = weight;
		this.edgeLabel = edgeLabel;
	}
	
	//create setters and getters for all labels, id, and weight of edge
	public String getSource() {
		return this.sourceLabel;
	}
	
	public void setSource(String sourceLabel) {
		this.sourceLabel = sourceLabel;
	}
	
	public String getDestination() {
		return this.destinationLabel;
	}
	
	public void setDestination(String destinationLabel) {
		this.destinationLabel = destinationLabel;
	}
	
	public String getEdge() {
		return this.edgeLabel;
	}
	
	public void setEdge(String edgeLabel) {
		this.edgeLabel = edgeLabel;
	}
	
	public long getID() {
		return this.id;
	}
	
	public void setID(long id) {
		this.id = id;
	}
	
	public long getWeight() {
		return weight;
	}
	
	public void setWeight(long weight) {
		this.weight = weight;
	}
	
	
}
