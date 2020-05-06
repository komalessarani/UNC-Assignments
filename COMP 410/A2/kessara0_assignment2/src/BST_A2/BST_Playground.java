package BST_A2;

public class BST_Playground {
/*
 * you will test your own BST implementation in here
 *
 * we will replace this with our own when grading, and will
 * do what you should do in here... create BST objects,
 * put data into them, take data out, look for values stored
 * in it, checking size and height, and looking at the BST_Nodes
 * to see if they are all linked up correctly for a BST
 * 
*/
	
  
  public static void main(String[]args){
	  //test some methods
	  
	  	//test size of empty string: should be 1
	  BST insertTest = new BST();
	  insertTest.insert("");
	  System.out.println("Here is the tree: ");
	  printLevelOrder(insertTest);
	  System.out.println("\nHere's the size of insertTree: " + insertTest.size);
	  
	  	//insert duplicate of empty string to test size: should be 1
	  insertTest.insert("");
	  System.out.println("\nHere is the tree: ");
	  printLevelOrder(insertTest);
	  System.out.println("\nHere's the size of insertTree: " + insertTest.size);
	  insertTest.remove("");		//remove the empty string
	  System.out.println("Here's the size of insertTree: " + insertTest.size);
	  
	  	//insert a string greater than length 0
	  insertTest.insert("string");
	  System.out.println("\nHere is the tree: ");
	  printLevelOrder(insertTest);
	  
	  	//insert duplicate of string
	  insertTest.insert("string");
	  System.out.println("\nHere is the tree: ");
	  printLevelOrder(insertTest);
	  
	  
	  	//insert string on left and right side of tree
	  insertTest.insert("str");
	  insertTest.insert("ing");
	  System.out.println("\nHere is the tree: ");
	  printLevelOrder(insertTest);
	  
	  //find the min, max, size, and see if the empty method and contains method correctly works
	  System.out.println("\nHere's the minimum: " + insertTest.findMin());
	  System.out.println("Here's the maximum: " + insertTest.findMax());
	  System.out.println("Here's the size: " + insertTest.size());
	  System.out.println("True or false, the tree is empty: " + insertTest.empty());
	  System.out.println("True or false, the word string is in the tree: " + insertTest.contains("string"));  
	  	

   // you should test your BST implementation in here
   // it is up to you to test it thoroughly and make sure
   // the methods behave as requested above in the interface
  
   // do not simple depend on the oracle test we will give
   // use the oracle tests as a way of checking AFTER you have done
   // your own testing

   // one thing you might find useful for debugging is a print tree method
   // feel free to use the printLevelOrder method to verify your trees manually
   // or write one you like better
   // you may wish to print not only the node value, and indicators of what
   // nodes are the left and right subtree roots,
   // but also which node is the parent of the current node
 
  }

  static void printLevelOrder(BST tree){ 
  //will print your current tree in Level-Order...
  //https://en.wikipedia.org/wiki/Tree_traversal
    int h=tree.getRoot().getHeight();
    for(int i=0;i<=h;i++){
      printGivenLevel(tree.getRoot(), i);
    }
    
  }
  static void printGivenLevel(BST_Node root,int level){
    if(root==null)return;
    if(level==0)System.out.print(root.data+" ");
    else if(level>0){
      printGivenLevel(root.left,level-1);
      printGivenLevel(root.right,level-1);
    }
  }
  static void printInOrder(BST_Node root){
  //will print your current tree In-Order
  if(root!=null){
    printInOrder(root.getLeft());
    System.out.print(root.getData() + " ");
    printInOrder(root.getRight());
  }
  }
}