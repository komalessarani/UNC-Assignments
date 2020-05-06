package SPLT_A4;

public class BST_Node {
  String data;
  BST_Node left;
  BST_Node right;
  BST_Node par; //parent...not necessarily required, but can be useful in splay tree
  boolean justMade; //could be helpful if you change some of the return types on your BST_Node insert.
            //I personally use it to indicate to my SPLT insert whether or not we increment size.
  
  BST_Node(String data){ 
    this.data=data;
    this.justMade=true;
  }
  
  BST_Node(String data, BST_Node left,BST_Node right,BST_Node par){ //feel free to modify this constructor to suit your needs
    this.data=data;
    this.left=left;
    this.right=right;
    this.par=par;
    this.justMade=true;
  }

  // leave these 3 methods in, as is (meaning also make sure they do in fact return data,left,right respectively)

  public String getData(){ return data; }
  public BST_Node getLeft(){ return left; }
  public BST_Node getRight(){ return right; }


  /*
  public BST_Node containsNode(String s){ return false; } //note: I personally find it easiest to make this return a Node,(that being the node splayed to root), you are however free to do what you wish.
  public BST_Node insertNode(String s){ return false; } //Really same logic as above note
  public boolean removeNode(String s){ return false; } //I personal do not use the removeNode internal method in my impl since it is rather easily done in SPLT, feel free to try to delegate this out, however we do not "remove" like we do in BST
  public BST_Node findMin(){ return left; } 
  public BST_Node findMax(){ return right; }
  public int getHeight(){ return 0; }

  private void splay(BST_Node toSplay) { return false; } //you could have this return or take in whatever you want..so long as it will do the job internally. As a caller of SPLT functions, I should really have no idea if you are "splaying or not"
                        //I of course, will be checking with tests and by eye to make sure you are indeed splaying
                        //Pro tip: Making individual methods for rotateLeft and rotateRight might be a good idea!
  */
  
  public BST_Node containsNode(String s) {
	  if(data.equals(s)) {
		  splay(this);
		  return this;
	  }
	  if(data.compareTo(s) > 0) {		//s lexiconically less than data
		  if(left != null) {
			  return left.containsNode(s);
		  }
		  else {
			  splay(this);
			  return this;
		  }
	  }
	  if(data.compareTo(s) < 0) {
		  if(right != null) {
			  return right.containsNode(s);
		  }
		  else {
			  splay(this);
			  return this;
		  }
	  }
	  return null;	//hopefully never hit this
  }
  
  public BST_Node insertNode(String s) {
	  if(data.compareTo(s)>0){
			if(this.left != null){
				return this.left.insertNode(s);
			}
			this.left=new BST_Node(s, null, null, this);
			BST_Node root = this.left;
			splay(this.left);
			return root;
		}
		if(data.compareTo(s)<0){
			if(this.right!=null){
				return this.right.insertNode(s);
			}
			this.right=new BST_Node(s, null, null, this);
			BST_Node root = this.right;
			splay(this.right);
			return root;
		}
		splay(this);
		return this;
  }
  
  public BST_Node findMin() {
		if(left != null) {
			return left.findMin();
		}
		splay(this);
		return this;
  }
  
  public BST_Node findMax() {
		if(right != null) {
			return right.findMax();
		}
		splay(this);
		return this;
  }
  
  public int getHeight() {
		int l=0;
		int r=0;
		if(left!=null)l+=left.getHeight()+1;
		if(right!=null)r+=right.getHeight()+1;
		return Integer.max(l, r);
  }
  
  private void rotateRight(BST_Node r) {
	  BST_Node b = r.par;
	  if(b.par != null) { 		//if there is a grandparent
		  if(b != b.par.left) {		//if the grandparent doesn't equal the great grandparent's left child
			  b.par.right = r;		//the grandparent's parent's right child is r
		  }
		  else {
			  b.par.left = r;		//the grandparent's parent's left child is r
		  }
	  } //so this if statement assigns r to either the right child or the left child depending on if b is
	  	//the parent of a left child or not
	  
	  if(r.right != null) {			//if the r Node's right child is not null
		  r.right.par = b;			//the r Node's right child's parent is b
	  }
	  
	  //fixing some pointers
	  r.par = b.par;				
	  b.par = r;					
	  b.left = r.right;				 
	  r.right = b;
  }
  
  private void rotateLeft(BST_Node l) {
	  BST_Node b = l.par;
	  if(b.par != null) { 		//if there is a grandparent
		  if(b != b.par.left) {		//if the grandparent doesn't equal the great grandparent's left child
			  b.par.right = l;		//the grandparent's parent's right child is l
		  }
		  else {
			  b.par.left = l;		//the grandparent's parent's left child is l
		  }
	  } //so this if statement assigns l to either the right child or the left child depending on if b is
	  	//the parent of a left child or not
	  
	  if(l.left != null) {			//if the l Node's left child is not null
		  l.left.par = b;			//the l Node's left child's parent is b
	  }
	  
	  //fixing some pointers
	  l.par = b.par;				
	  b.par = l;					
	  b.right = l.left;				 
	  l.left = b;
  }
	private void splay(BST_Node toSplay) {
		while (toSplay.par != null) {
			BST_Node dad = toSplay.par;
			BST_Node dadPar = dad.par;

			// only one away from root
			if (dadPar == null) {
				if (toSplay == dad.left) { // if the node is the parent's left child
					rotateRight(toSplay); // then rotate right
				} else {
					rotateLeft(toSplay); // else rotate to left
				}
			} else if (toSplay == dad.left) { // if the node is to the left of the parent
				if (dad == dadPar.left) {		//zig zig case
					rotateRight(toSplay.par);
					rotateRight(toSplay);
				} else { // zig then zag (LR) of grandparent
					rotateRight(toSplay);
					rotateLeft(toSplay);
				}
			} else if (toSplay == dad.right) { // if the node is to the right of the parent
				if (dad == dadPar.right) {		//zag zag case
					rotateLeft(toSplay.par);
					rotateLeft(toSplay);
				} else { // zag then zig (RL) of grandparent
					rotateLeft(toSplay);
					rotateRight(toSplay);
				}
			}
		}
	}

  
}