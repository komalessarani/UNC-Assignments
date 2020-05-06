
public class BSTPlayground {
	
	public static void main(String[]args){//feel free to use the printLevelOrder method to verify your trees manually
		BST b = new BST();
		System.out.println("Contains goo: "+b.contains("goo"));
		System.out.println("IsEmpty: "+b.empty());
		System.out.println("Inserting.....");
		b.insert("hello");
		b.insert("world");
		b.insert("my");
		b.insert("name");
		b.insert("is");
		b.insert("blank");
		System.out.println("Contains goo: "+b.contains("goo"));
		System.out.println("Contains my: "+b.contains("my"));
		System.out.println("Contains is: "+b.contains("is"));
		System.out.println("Contains world: "+b.contains("world"));
		System.out.println("\ndone");
		System.out.println("Size: "+b.size);
		System.out.println("Height: "+b.height());
		System.out.println("Maximum: "+b.findMax());
		System.out.println("Minimum: "+b.findMin());
		System.out.println("\nTree Dump: ");
		printLevelOrder(b);
		String removeThis="hello";
		b.remove(removeThis);
		System.out.println("\nRemoved "+removeThis+"...now dumping tree: ");
		printLevelOrder(b);
		System.out.println("\nContains "+removeThis+": "+b.contains(removeThis));
		
		
	}
	static void printLevelOrder(BST tree){ //will print your current tree in Level-Order...https://en.wikipedia.org/wiki/Tree_traversal
		int h=tree.getRoot().getHeight(); //It was me
		for(int i=0;i<=h;i++){
			printGivenLevel(tree.getRoot(), i);
		}
		
	}
	static void printGivenLevel(BST_Node root,int level){ //DIO
		if(root==null)return;
		if(level==0)System.out.print(root.getData()+" ");
		else if(level>0){
			printGivenLevel(root.getLeft(),level-1);
			printGivenLevel(root.getRight(),level-1);
		}
	}
}
