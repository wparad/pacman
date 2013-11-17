/**************************
 * The edge class
 */

package graph;

/**A class to keep track of edges of a Mapgraph*/
public class Edge {
    int id;  // a unique non-negative identifier, assigned by the graph
    public Node v1, v2;  // the two end-points of the edge
    double weight;  // the weight of the edge

    /** Constructors
     */
    public Edge(int _id, Node _v1, Node _v2, double _weight) {
        id = _id;
        v1 = _v1;
        v2 = _v2;
        weight = _weight;
    }

    /* Access methods
     */
    /**returns the id*/
    public int getId() {
        return id;
    }
    /**gets the weight of this edge*/
    public double getWeight() {
        return weight;
    }
}
