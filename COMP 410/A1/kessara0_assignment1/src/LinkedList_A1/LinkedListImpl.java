/**
 * COMP 410
 *See inline comment descriptions for methods not described in interface.
 *
*/
package LinkedList_A1;

public class LinkedListImpl implements LIST_Interface {
	Node sentinel; // this will be the entry point to your linked list (the head)
	int numElems;

	public LinkedListImpl() {// this constructor is needed for testing purposes. Please don't modify!
		sentinel = new Node(0); // Note that the root's data is not a true part of your data set!
	}

	// implement all methods in interface, and include the getRoot method we made
	// for testing purposes. Feel free to implement private helper methods!

	public Node getRoot() { // leave this method as is, used by the grader to grab your linkedList easily.
		return sentinel;
	}

	@Override
	public boolean insert(double elt, int index) {
		// return false if the index is beyond the list size
		if (index > numElems || index < 0) {
			return false;
		}
		
		//point the sentinel to itself
		if(sentinel.next == null) {
			sentinel.prev = sentinel;
			sentinel.next = sentinel;
		}
		Node newNode = new Node(elt);        // node that will be inserted into list
		Node afterN = findIndex(index);      // node that is after the inserted node
		Node beforeN = findIndex(index-1);   // node that is before the inserted node
		afterN.prev = newNode;     //point the afterNode's previous to the node being inserted
		beforeN.next = newNode;    //point the beforeNode's next to the node being inserted
		newNode.next = afterN;     //set the pointers to after and before Node using the current location
		newNode.prev = beforeN; 
		numElems++;  //add number of nodes in the list
		return true;
	}
	
	//traverses to the index wanted and returns the node at that index
	private Node findIndex(int index) {
		Node current = sentinel;   
		
		//loop through to assign the current node to the node at the index desired
		for(int i =-1; i < index; i++) {
			current = current.next;
		}
		return current;
	}
	@Override
	public boolean remove(int index) {
		 // return false if the index is beyond the list size
		if(isEmpty() == true || index >= numElems || index < 0) {
			return false;
		}
		Node afterN = findIndex(index+1);   
		Node beforeN = findIndex(index-1);
		afterN.prev = beforeN;     //reset the pointer of afterNode's previous to the node before the one inserted 
		beforeN.next = afterN;    
		numElems--;               // decrease the number of elements in list
		return true;
	}

	@Override
	public double get(int index) {
		if(isEmpty()|| index >= numElems || index < 0) {
			return Double.NaN;      //if index is invalid, return a double.nan value
		}
		//get the double value from the Node
		return findIndex(index).data;
	}

	@Override
	public int size() {
		return numElems;
	}

	@Override
	public boolean isEmpty() {
		return numElems == 0;
	}

	@Override
	public void clear() {
		//remove all elements from list
		sentinel.next = sentinel;         
		sentinel.prev = sentinel;
		numElems = 0;
	}
}