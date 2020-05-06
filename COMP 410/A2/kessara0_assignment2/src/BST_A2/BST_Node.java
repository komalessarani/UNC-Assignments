package BST_A2;

public class BST_Node {
  String data;
  BST_Node left;
  BST_Node right;
  
  BST_Node(String data){ this.data=data; this.left = null; this.right = null;}

  // --- used for testing  ----------------------------------------------
  //
  // leave these 3 methods in, as is

  public String getData(){ return data; }
  public BST_Node getLeft(){ return left; }
  public BST_Node getRight(){ return right; }

  // --- end used for testing -------------------------------------------

  
  // --- fill in these methods ------------------------------------------
  //
  // at the moment, they are stubs returning false 
  // or some appropriate "fake" value
  //
  // you make them work properly
  // add the meat of correct implementation logic to them

  // you MAY change the signatures if you wish...
  // make the take more or different parameters
  // have them return different types
  //
  // you may use recursive or iterative implementations

  /*
  public boolean containsNode(String s){ return false; }
  public boolean insertNode(String s){ return false; }
  public boolean removeNode(String s){ return false; }
  public BST_Node findMin(){ return left; }
  public BST_Node findMax(){ return right; }
  public int getHeight(){ return 0; }
  */

  // --- end fill in these methods --------------------------------------


  // --------------------------------------------------------------------
  // you may add any other methods you want to get the job done
  // --------------------------------------------------------------------
  
  //fix these methods 
  public boolean containsNode(String s) {
	  if(this.getData().equals(s)) {		// check if this node is equal to the current string
		  return true;
	  }
	  else if(this.getLeft() != null && s.compareTo(this.getData()) < 0) { 
		  return this.getLeft().containsNode(s);	// check if the left node is equal to the current string
	  }
	  else if(this.getRight() != null && s.compareTo(this.getData()) > 0) {
		  return this.getRight().containsNode(s);  // check if the right node is equal to the current string
	  }
	  else {
		  return false;			// string is not in the node
	  }
  }

  public boolean insertNode(String s) {
	  if(s.compareTo(this.getData()) < 0) {		//check if s fits at the left
		  if(this.getLeft() !=  null) {			//if a node already exists, insert s into its tree	
			  return this.getLeft().insertNode(s);
		  } 
		  else {			//if node doesn't exist already, create new one
			  this.left = new BST_Node(s);
			  return true;
		  }
	  }
	  
	  //check if s fits at the right node
	  else if(s.compareTo(this.getData()) > 0) {		
		  if(this.getRight() != null) {
			  return this.getRight().insertNode(s);		//if a node already exists, insert s into its tree
		  }
		  else {
			  this.right = new BST_Node(s);
			  return true;
		  }
	  }
	  else {
		  return false;		//this indicates that the string already exists and therefore should not be 
		  					//entered in the tree again
	  }
  }
  
  public boolean removeNode (String s, BST_Node higher) {
	  //check if the current node is equivalent to s and if so, then remove that
	  if(this.getData().equals(s) && this.getLeft() == null && this.getRight() == null) {
		  if(higher.getLeft() != null && higher.getLeft().getData().equals(this.getData())) {
			  higher.left = null;
		  }
		  else {
			  higher.right = null;
		  }
		  
		  return true;		//indicate success
	  }

	  //check if the string is in a tree with a left node/branch
	  else if(this.getData().equals(s) && this.getLeft() != null && this.getRight() == null) {
		  if(higher.getLeft() != null && higher.getLeft().getData().equals(this.getData())) {
			  higher.left = this.left;
		  }
		  else {
			  higher.right = this.left;
		  }
		  
		  return true;		//indicate success
	  }
	  
	  //check if the string is in a tree with a right node/branch
	  else if(this.getData().equals(s) && this.getLeft() == null && this.getRight() != null) {
		  if(higher.getLeft() != null && higher.getLeft().getData().equals(this.getData())) {
			  higher.left = this.right;
		  }
		  else {
			  higher.right = this.right;
		  }
		  
		  return true;		//indicate success
	  }
	  
	  //check if the string is in a tree with both right and left nodes/branches
	  else if(this.getData().equals(s) && this.getLeft() != null && this.getRight() != null) {
		  //find the min and remove it
		  String minimum = this.right.findMin().getData();
		  this.data = minimum;
		  
		  //remove the repeated value
		  return this.right.removeNode(minimum, this);
	  }
	  
	  //if not equivalent and before
	  else if(s.compareTo(this.getData()) < 0) 
		  return this.left.removeNode(s, this);
	  
	  //if not equivalent and after
	  else if(s.compareTo(this.getData()) > 0) 
		  return this.right.removeNode(s, this);
	
		  return false;
  }
  
  public BST_Node findMin() {
	  //check if there is a node that is more left than current one
	  if(this.getLeft() != null) {
		  return this.getLeft().findMin();
	  }
	  else {
		  return this;     //if there is not, then we already have the left most node
	  }
  }
  
  public BST_Node findMax() {
	  //check if there is a node that is more right than current one
	  if(this.getRight() != null) {
		  return this.getRight().findMax();
	  }
	  else {
		  return this;     //if there is not, then we already have the right most node
	  }
  }
  
  public int getHeight() {
	  if(this.getRight() == null && this.getLeft() == null) {
		  return 1;			// if there is only the current node then height is 1
	  }
	  else if(this.getLeft() == null && this.getRight() != null) {
		  return this.getRight().getHeight() + 1; // if there is a right node, get height of that and 
		  										  // add the current node to its height
	  }
	  else if(this.getLeft() != null && this.getRight() == null) {
		  return this.getLeft().getHeight() + 1; // if there is a left node, get height of that and 
			  // add the current node to its height
	  }
	  else {
		  // if both right and left nodes exist, then find the larger height and add current node to it
		  if(this.getLeft().getHeight() >= this.getRight().getHeight()) {
			  return this.getLeft().getHeight() + 1;
		  }
		  else {
			  return this.getRight().getHeight() + 1;
		  }
	  }
  }
    
  public String toString(){
    return "Data: "+this.data+", Left: "+((this.left!=null)?left.data:"null")
            +",Right: "+((this.right!=null)?right.data:"null");
  }
}