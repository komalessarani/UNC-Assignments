package DiGraph_A5;

public class MinBinHeap implements Heap_Interface {
	private EntryPair[] array; // load this array
	private int size;
	private static final int arraySize = 10000; // Everything in the array will initially
												// be null. This is ok! Just build out
												// from array[1]
	public MinBinHeap() {
		this.array = new EntryPair[arraySize];
		array[0] = new EntryPair(null, -100000); // 0th will be unused for simplicity
													// of child/parent computations...
													// the book/animation page both do this.
	}

	// Please do not remove or modify this method! Used to test your entire Heap.
	@Override
	public EntryPair[] getHeap() {
		return this.array;
	}

	@Override
	public void insert(EntryPair entry) {
		// create a variable to keep track of the hole you are comparing with entry
		int hole = size + 1;

		while (hole > 1) {
			// if the parent hole has a lower priority than the entry pair, the current hole
			// becomes the entry
			if (array[hole / 2].priority < entry.priority) {
				array[hole] = entry;
				break; // stop the loop
			} else {
				array[hole] = array[hole / 2]; // current hole becomes parent hole
				hole /= 2; // update value of hole
			}
		}
		array[hole] = entry; // current hole becomes entry of the user
		size++; // increment the size

	}

	@Override
	public void delMin() {
		int hole = 1, index = 0;
		int si = size;

		//if there are holes in the heap
		if (si > 0) {
			EntryPair temp = new EntryPair(null, 0);
			temp = array[si]; // save the last spot
			array[si] = null;
			size--;

			while (hole < si) {
				// if no children
				if (array[hole * 2] == null && array[hole * 2 + 1] == null) {
					array[hole] = temp; // change current hole to temp which is null
					break; // done deleting so exit loop
				}

				// if left child
				else if (array[hole * 2] != null && array[hole * 2 + 1] == null) {
					if (array[2 * hole].priority < temp.priority) {
						array[hole] = array[2 * hole];
						array[2 * hole] = temp;
						break;
					} else {
						array[hole] = temp;
						break;
					}
				} else { // has two child
					if (array[hole * 2].priority < array[hole * 2 + 1].priority) {
						index = 2 * hole;
					} else {
						index = 2 * hole + 1;
					}

					if (array[index].priority < temp.priority) {
						array[hole] = array[index];
						hole = index;
					} else {
						array[hole] = temp;
						break;
					}
				}
			} // end while loop
		} // end if statement
	}

	@Override
	public EntryPair getMin() {
		if (size == 0) {
			return null; // return null if the heap is empty
		} else {
			return array[1];
		}
	}

	@Override
	public int size() {
		return size;
	}

	// create a helper method that sorts the array
	private void sort(int hole) {
		// if only one child
		if (array[hole * 2] != null && array[hole * 2 + 1] == null) {
			if (array[hole * 2].priority < array[hole].priority) {
				swap(hole * 2, hole);
			}
		}
		// if two children
		else {
			if (array[hole * 2].priority < array[hole * 2 + 1].priority) { // check which priority is higher
				if (array[hole * 2].priority < array[hole].priority) {
					swap(hole * 2, hole);
				}
			} else {
				if (array[hole * 2 + 1].priority < array[hole].priority) {
					swap(hole * 2 + 1, hole);
				}
			}
		}
	}
	
//create a helper method to swap places
	private void swap(int first, int second) {
		EntryPair temp = new EntryPair(null, 0); // create a temp pair as a way to swap the two holes
		temp = array[first];
		array[first] = array[second];
		array[second] = temp;
	}

	@Override
	public void build(EntryPair[] entries) {
		int s = entries.length; // store the length of the entries

		for (int i = 0; i < s; i++) {
			array[i + 1] = entries[i]; // assign the array the values of the entries of the user
		}
		size = s; // size becomes whatever the size of the entries array is
		// parent of the last hole
		int hole = s / 2;

		// check as you go up
		while (hole > 0) {
			sort(hole);
			hole--;
		}
		// eventually hole reaches 0
		hole = 2;
		// check as you go down
		while (hole <= s / 2) {
			sort(hole);
			hole++;
		}
	}
}
