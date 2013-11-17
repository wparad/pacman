/***************************
 * The node (vertex) class
 */

package graph;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Vector;

public class Node {
    int id;  // a unique non-negative identifier, assigned by the graph
    int x, y;  // coordinate of the node on the x-y plane
    Vector<Node> neighbors;  // neighbors of the node

    /* Constructors
     */
    public Node(int _id, int _x, int _y) {
        id = _id;
        x = _x;
        y = _y;
        neighbors = new Vector<Node>();
    }

    /* Add a neighbor to the node
     */
    public void addNeighbor(Node v) {
        neighbors.add(v);
    }

    /* Return an iterator of neighboring nodes
     */
    public Iterator<Node> getNeighbors() {
        return neighbors.iterator();
    }

    /* Access methods
     */
	public int getId() {
		return id;
	}

    public Point2D getCoordinates(){
		return new Point.Double(x, y);
	}
}