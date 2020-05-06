package BST_A2;

public class BST implements BST_Interface {
  public BST_Node root;
  int size;
  
  public BST(){ size=0; root=null; }
  
  @Override
  //used for testing, please leave as is
  public BST_Node getRoot(){ return root; }

@Override
public boolean insert(String s) {
	boolean checkInsert = false;
	if(root != null) {		// check if root already exists
		checkInsert = root.insertNode(s);
	}
	else {					// if root does not exist, add new node and increment size
		this.root = new BST_Node(s);	
		checkInsert = true;
	}
	if(checkInsert == true) {
		size++; 
	}
	return checkInsert;
}

@Override
public boolean remove(String s) {
	if(this.root == null || size == 0) {		//if tree is empty or root is null then nothing to remove
		return false;
	}
	if(root.containsNode(s) == false) {		//if root does not have the string, return false
		return false;
	}
	else if(this.root.getData().equals(s)){		//if root does have data and exists then remove successfully
		BST_Node remove = new BST_Node(null);		//create a temp node 
		remove.left = this.root;
		boolean checkSuccess = this.root.removeNode(s, remove);
		this.root = remove.getLeft();
		if(checkSuccess == true) {
			size--;
		}
		return checkSuccess;
	}
	else {
		boolean checkSuccess = this.root.removeNode(s, null);
		if(checkSuccess == true) {
			size--;
		}
		return checkSuccess;
	}
}

@Override
public String findMin() {
	if(this.root == null) {
		return null;
	}
	else {
		return this.root.findMin().getData();
	}
}

@Override
public String findMax() {
	if(this.root == null) {
		return null;
	}
	else {
		return this.root.findMax().getData();
	}
}

@Override
public boolean empty() {
	// if size is zero and root is null, this should be empty and return true
	return this.size == 0 || this.root == null;
}

@Override
public boolean contains(String s) {
	if(size == 0 || root == null) {
		return false;
	}
	else {
		return this.root.containsNode(s);		//delegate this to BST_node contains method
	}
}

@Override
public int size() {
	return this.size;
}

@Override
public int height() {
	//check if size is zero or if root is null
	if(size == 0 || root == null) {
		return -1;
	}
	else {
		return root.getHeight()-1;
	}
}

}