/**
 * COMP 410
 *
 * Make your class and its methods public!
 *
 * Your SPLT implementation will implement this BST interface.
 *
*/

package SPLT_A4;

/*
  Interface: The SPLT will provide this collection of operations:

  insert:
    in: a string (the element to be stored into the tree)
    return: void, if you need to update size when delegating, consider using a boolean as I mention in BST_Node
    effect: if the string is already in the tree, then there is no change to
              the tree state (save for splaying), and return
            if the string is not already in the tree, then a new tree cell/node
              is created, the string put into it as data, the new node is linked
              into the tree at the proper place; size is incremented by 1,
              and return
  remove:
    in: a string (the element to be taken out of the tree)
    return: void
    effect: see description on "when to splay"
    
  contains:
    in: a string (the element to be searched for)
    return: boolean, return true if the string being looked for is in the tree;
            return false otherwise
            this means return false if tree size is 0
    effect: no change to the state of the tree (save for splaying found node or what would be its parent)

  findMin:
    in: none
    return: string, the element value from the tree that is smallest
    effect: no tree state change (save for splaying)
    error: is tree size is 0, return null


  findMax:
    in: none
    return: string, the element value from the tree that is largest
    effect: no tree state change (save for splaying)
    error: is tree size is 0, return null

  size:
    in: nothing
    return: number of elements stored in the tree
    effect: no change to tree state

  empty:
    in: nothing
    return: boolean, true if the tree has no elements in it, true if size is 0
            return false otherwise
    effect: no change to tree state

  height:
    in: none
    return: integer, the length of the longest path in the tree from root to a leaf
    effect: no change in tree state
    error: return -1 is tree is empty (size is 0, root is null)

  getRoot:
    in: none
    return: a tree cell/node, the one that is the root of the entire tree
            means return a null if the tree is empty
    effect: no change to tree state

*/

// ADT operations

public interface SPLT_Interface {
  public void insert(String s);
  public void remove(String s);
  public String findMin();
  public String findMax();
  public boolean empty();
  public boolean contains(String s);
  public int size();
  public int height();
  public BST_Node getRoot();
}