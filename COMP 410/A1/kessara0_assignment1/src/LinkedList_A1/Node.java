/**
 * COMP 410
 * Don't modify this file!
*/

package LinkedList_A1;

public class Node { //this is your Node object, these are the Objects in your list
  public double data;
  public Node next; //links this node to the next Node in the List
  public Node prev; //links this node to the preceeding Node in the List (ie this Node is the prev Node's next node)
  public Node(double data){
    this.data=data;
    this.next=null;
    this.prev=null;
  }
  public String toString(){
    return "data: "+data+"\thasNext: "+(next!=null)+"\t\thasPrev: "+(prev!=null);
  }
  
  /*  Below are "getters" for our testing purposes. Please do not modify.  
      I would advise just referencing the fields of your Nodes since 
      they are public
  */
  public boolean isNode(){ //testing purposes please do not touch!
    return true;
  }
  public double getData(){ //testing purposes please do not touch!
    return data;
  }
  public Node getNext(){ //testing purposes please do not touch
    return next;
  }
  public Node getPrev(){ //testing purposes please do not touch
    return prev;
  }
}
