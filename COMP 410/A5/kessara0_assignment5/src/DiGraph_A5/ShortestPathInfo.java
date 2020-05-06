package DiGraph_A5;

public class ShortestPathInfo {
/*
 * 
 * This class is to represent a single shortest path 
 * from a source Node to a destination Node
 * 
 * Description of each field you are to populate:
 * 
 * String dest: the label of the destination node
 * long totalWeight: the sum of the edge weights on the shortest path 
 *                   from source node to destination node
 * 
 * Note on totalWeight: If a path from source node to the destination node 
 *                      does not exist, set totalWeight to -1.
 * 
 * Please leave this class untouched! You must populate your ShortestPathInfo object 
 * using the given constructor
 * An array of populated ShortestPathInfo objects is to be returned by the 
 * shortestPath method in your DiGraph
*/
  private String dest;
  private long totalWeight;
  
  public ShortestPathInfo(String dest, long totalWeight){
    this.dest=dest;
    this.totalWeight=totalWeight;
  }

  public String getDest() {
    return dest;
  }

  public long getTotalWeight() {
    return totalWeight;
  }
  
  public String toString(){
    return "dest: "+dest+"\ttotalWeight: "+totalWeight;
  }
}

