package SPLT_A4;

public class SPLT implements SPLT_Interface{
  private BST_Node root;
  private int size;
  
  public SPLT() {
    this.size = 0;
  } 
  
  public BST_Node getRoot() { //please keep this in here! I need your root node to test your tree!
    return root;
  }

@Override
public void insert(String s) {
	if(empty()) {
		root = new BST_Node(s);		//create new node at root if tree is empty
	}
	else {
		root = root.insertNode(s);	//insert 
	}
	
	if(root.justMade) {
		size++;
		root.justMade = false;
	}
}

@Override
public void remove(String s) {
	
	if(root == null || s == null || empty() || !(contains(s))) {
		return;
	}
	
	if(root.left == null) {			//if the root's left child doesn't exist
		root = root.right;			//the root is now the value that was in the root's right child
	}
	else {
		BST_Node r = this.root.right;		//the temp variable is the right child of the root
		if(r == null) {						//if the right child is null	
			root = root.left;				//then the root should be assigned the value of the root's left child
		}
		else {
			root = root.left.findMax();		//else, find the max of the left child to replace the root
		}
		
		if(r != null) {						//if the right child of the root does exist
			root.right = r;					
		}
		
		if(root.right != null) {			
			root.right.par = root;			//the parent of the root's right child is the root
		}
	}
	if(root != null) {						//if the root isn't null
		root.par = null;					//then the parent of the root is 
	}
	
	size--;	
}

@Override
public String findMin() {
	if(empty()) {
		return null;
	}
	else {
		root = root.findMin();
		return root.data;
	}
}

@Override
public String findMax() {
	if(empty()) {
		return null;
	}
	else {
		root = root.findMax();
		return root.data;
	}
}

@Override
public boolean empty() {
	return size == 0;
}

@Override
public boolean contains(String s) {
	if(empty()) {
		return false;
	}
	root = root.containsNode(s);
	return root.data.equals(s);
}

@Override
public int size() {
	return size;
}

@Override
public int height() {
	if(empty()) {
		return -1;
	}
	else {
		return root.getHeight();
	}
}  

}
