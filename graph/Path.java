package graph;

import java.util.Iterator;
import java.util.Vector;


public class Path implements Iterable<Node> {
   Vector<Node> nodeVec;  // vector of nodes
   int currentProgress = 0;

   /* Constructor - an empty path
    */
   public Path() {
       nodeVec = new Vector<Node>();
   }

   /* Return the number of nodes in the path
    */
   public int nodeCount() {
       return nodeVec.size();
   }

   /* Return the node at a given position
    */
   public Node nodeAt(int i) {
       return nodeVec.get(i);
   }

   /* Add node to the end of the path
    */
   public void appendNode(Node v) {
       nodeVec.add(v);
   }
   
   /* Increment the position indicator 'currentProgress' of the path
    */
   public void advance(){
	   currentProgress++;
   }
   
   /* Return a iterator of the path, i.e. iterator through the nodes
    */
   public Iterator<Node> iterator() {
       return nodeVec.listIterator(currentProgress);
   }

   /* A utitliy function returning a (new) reversed version of the input path.
      Note that the currentProgress field of the returned path will always
      be zero.
    */
   public static Path reverse(Path p) {
       Path q = new Path();
       for(int i=p.nodeCount()-1; i>=0; i--)
           q.appendNode(p.nodeAt(i));
       return q;
   }
}
