/******************************************************************************
 * AbastractGraph.java
 *
 * This file is part of CS211-fa06 Assignment 2
 *
 * Specification of the methods that are required of a graph. The graph
 * shall be an undirected simple graph.
 *
 * NOTE:
 * - In the running time and memory usage specification, let V denote the 
 *   number of nodes, E denote the number of edges, and N denote the size 
 *   of the graph, i.e. N = V + E.
 *
 * Graph I/O Specification:
 *     The graph file is given as a pure text file. The first line has a
 * single integer n, which is the number of nodes in the graph. The next n lines
 * (line 2 -- n+1) each has two integers, delimited by a white space,
 * representing in order the x and y coordinates of the node. Since nodes
 * are added to graph sequentially, each node's sequence number (starting at 
 * zero) shall be its line number (in the input file) minus 2. After this,
 * the next line has a single integer m standing for the number of edges.
 * The following m lines each have three values: the first two integers are
 * the sequence numbers of the two endpoints of the edge and the third 
 * floating point number is the weight of the edge. If the third number 
 * is -1, the edge weight shall be the Euclidean distance between endpoints.
 */

package graph;


/***
 * Graph Interface - specifies the minimum set of methods of a graph
 * 
 * Memory Usage Requirement:
 *   - The total amount of memory used to represent the graph must be O(N).
 */
public interface Graph {
    // --------- Access methods ---------

    /** Add a node to the graph, given its x,y location.
       Return the node's id if the node is added, -1 if some node
       already exists at that location.
       - Running time: O(log(N))
     */
    public int addNode(int x, int y);

    /** Return a node given its id. 
       - Assume the id is valid.
       - Running time: O(1)
     */
    public  Node getNodeById(int id);

    /** Return a node given its x-y coordinates, null if no node at such place.
       - Running time: O(log(N))
     */
    public  Node getNodeByCoord(int x, int y);

    /** Return the number of nodes in the graph.
       - Running ime: O(1)
     */
    public  int nodeCount();

    /** Add an edge to the graph, given the two end points' ids and its weight.
       Return the edge's id if the edge is added, or -1 if adding it would form
       a multiple edge (i.e. an edge already exists betwee the two endpoints)
       or a loop (i.e. the two endpoints are one and the same). In the latter
       case (i.e. where -1 is returned), the edge shall not be added.
       - Assume the two endpoints have already been added to the graph, i.e.
         nid1 and nid2 are both valid.
       - Running time: O(log(N))
     */
    public  int addEdge(int nid1, int nid2, double weight);

    /** Return an edge given its id. 
       - Assume the id is valid.
       - Running time: O(1)
     */
    public  Edge getEdgeById(int id);

    /** Return an edge given its two endpoints, or null if the edge dosn't exist.
       - Assume neither of v1 and v2 is null.
       - Running ime: O(log(N))
     */
    public  Edge getEdgeByEndpoints(Node v1, Node v2);

    /** Return the number of edges in the graph.
       - Running ime: O(1)
     */
    public  int edgeCount();


    // ---------- Methods related to graph algorithms ---------

    /** Computes the weight of a given path on the graph
       - Return INFINITY if p is empty, i.e. has nodeCount() == 0 (meaning 
         path doesn't exist)
       - Assume p is not null.
       - Running ime: O(p.nodeCount()*log(N))
     */
    public  double pathWeight(Path p);

    /** Compute the shortest path from src to the dst on the graph
       (Dijkstra's algorithm)
       - The first node in the path should be src, the last one should be dst.
       - Return an empty path (i.e. w/ no node) if src and dst are disconnected.
       - Running ime: O(N*log(N))
     */
    public  Path shortestPath(Node src, Node dst);


    // ---------- I/O methods ----------
    

    /* load the graph by reading from a file with 'filename'.
       An A5RuntimeException will be thrown if some error has been 
       detected (e.g. the input file doesn't represent a valid graph).
       - After the call, the graph object upon which the method is
         invoked will be updated to represent the graph specified
         by the input file.
       - Running ime: O(N*log(N))
     */
    
    //OPTIONAL
    //throw an UnsupportedOperationException if you
    //do not implement this
    public void loadGraph(String filename);
}

