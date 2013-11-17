

/******************************************************************************
 * Graph.java
 *
 * This file is part of CS211-fa06 Assignment 2
 *
 * Implement a graph in adjacency list format and some graph algorithms.
 * 
 * Requirement:
 * - Must extend AbstractGraph class. One may write additional methods or
 *   create addtional classes.
 * - Must override the default constructor (public Graph()).
 * - The implementation of each abstract method in AbstractGraph
 *   must satisfy the given asymptotic running time bound. Note that the
 *   specified bound are not necessarily tight (i.e. the optimal achievable
 *   asymptotical running time).
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

import game.A5RuntimeException;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Vector;
/**A class to keep track of a graph*/
public class MapGraph implements Graph {

	private Map<Object, Node> nodes = new HashMap<Object, Node>();

	private Map<Object, Edge> edges = new HashMap<Object, Edge>();

	/* Constants */
	public static final double INFINITY = Double.POSITIVE_INFINITY;

	public static final double MAX_EDGE_WEIGHT = Double.MAX_VALUE;
	
	/**method to test a file as a map from the command line*/
	public static void main(String[] arg){
		MapGraph test=readGraph("graph/test.txt");
		Scanner scan = new Scanner(System.in);
		String s =scan.nextLine();
		try{
			int a = Integer.parseInt(s.substring(0,s.indexOf(" ") - 1));
			int b = Integer.parseInt(s.substring(s.indexOf(" ")));
		
		Path shortest=test.shortestPath(
				test.getNodeById(a),test.getNodeById(b));
		System.out.println(shortest+": "+test.pathWeight(shortest));
		}
		catch(Exception e){}
	}
	

	/**
	 * Constructor- for instanciation
	 */
	public MapGraph() {

	}

	// --------- Access methods ---------

	/**
	 * Add a node to the graph. Two nodes can't have the same coordinate value.
	 * If the x, y coordinates already exists, do nothing and return false;
	 * otherwise, add the node and return true.
	 */
	protected boolean addNode(Node v) {
		if (!nodes.containsKey(v.getCoordinates())
				&& !nodes.containsKey(v.getId())) {
			nodes.put(v.getCoordinates(), v);
			nodes.put(v.getId(), v);
			return true;
		}
		return false;

	}

	/**
	 * Add a node to the graph, given its x,y location. Return the node's id if
	 * the node is added, -1 if some node already exists at that location. - A
	 * Node object is constructed in this method.
	 */
	public int addNode(int x, int y) {
		return (addNode(new Node(nodeCount(), x, y)) ? nodeCount()-1 : -1);
	}

	/**
	 * Return a node given its id. - Assume the id is valid. - This specific
	 * implementation will return null for invalid id.
	 */
	public Node getNodeById(int id) {
		return nodes.get(id);
	}

	/**
	 * Return a node given its x-y coordinates, null if no node at such place.
	 */
	public Node getNodeByCoord(int x, int y) {
		return nodes.get(new Point.Double(x, y));
	}

	/**
	 * Return the number of nodes in the graph
	 */
	public int nodeCount() {
		return nodes.size() / 2;
	}

	/**
	 * Add an edge to the graph, given the edge. Return true if edge is added,
	 * false if it already exists.
	 */
	protected boolean addEdge(Edge e) {
		if (e.v1.getId() != e.v2.getId()
				&& !edges.containsKey(new IntPair(e.v1.getId(), e.v2.getId()))
				&& !edges.containsKey(e.getId())) {
			e.v1.addNeighbor(e.v2);
			e.v2.addNeighbor(e.v1);
			edges.put(new IntPair(e.v1.getId(), e.v2.getId()), e);
			edges.put(e.getId(), e);
			return true;
		}
		return false;
	}

	/**
	 * Add an edge to the graph, given the two end points' ids and its weight.
	 * Return the edge's id if the edge is added, or -1 if adding it would form
	 * a multiple edge (i.e. an edge already exists betwee the two endpoints) or
	 * a loop (i.e. the two endpoints are one and the same). In the latter case
	 * (i.e. where -1 is returned), the edge shall not be added. - Assume the
	 * two endpoints have already been added to the graph. - An Edge object is
	 * constructed in this method.
	 */
	public int addEdge(int nid1, int nid2, double weight) {
		return (addEdge(new Edge(edgeCount(), getNodeById(nid1),
				getNodeById(nid2), weight)) ? edgeCount()-1 : -1);
	}

	/**
	 * Add an edge to the graph, give the ids of the two end points. The weight
	 * of the edge is set to the distance of the two Nodes on the x-y plane.
	 * Return the edge's id if the edge is added, -1 if an edge already exists
	 * between the two endpoints. - Assume the two endpoints have already been
	 * added to the graph, i.e. nid1 and nid2 are both valid.
	 */
	public int addEdgeWeighedByDistance(int nid1, int nid2) {
		return addEdge(nid1, nid2, getNodeById(nid1).getCoordinates().distance(
				getNodeById(nid2).getCoordinates()));
	}

	/**
	 * Return an edge given its id. - Assume the id is valid. - This specific
	 * implementation will return null for invalid id.
	 */
	public Edge getEdgeById(int id) {
		return edges.get(id);

	}

	/**
	 * Return an edge given its two endpoints, or null if the edge dosn't exist. -
	 * Assume neither of v1 and v2 is null.
	 */
	public Edge getEdgeByEndpoints(Node v1, Node v2) {
		return edges.get(new IntPair(v1.getId(), v2.getId()));
	}

	/**
	 * Return the weight of an edge given its two endpoints, or INFINITY if the
	 * edge doesn't exist. - Assume neither of v1 and v2 is null.
	 */
	public double getEdgeWeight(Node v1, Node v2) {
		return getEdgeByEndpoints(v1, v2).getWeight();

	}

	/**
	 * Return the number of edges in the graph
	 */
	public int edgeCount() {
		return edges.size() / 2;

	}

	// ---------- Methods related to graph algorithms ---------

	/**
	 * Computes the weight of a given path on the graph - Return INFINITY if p
	 * is empty, i.e. has nodeCount() == 0 (meaning path doesn't exist) - Assume
	 * p is not null.
	 */
	public double pathWeight(Path p) {
		if (p.nodeCount() == 0) {
			return INFINITY;
		}
		double weight = 0;
		for (int i = 0; i < p.nodeCount() - 1; i++) {
			weight += getEdgeByEndpoints(p.nodeAt(i), p.nodeAt(i + 1))
					.getWeight();
		}
		return weight;
	}

	/**
	 * Compute the shortest path from one node src to the dst on the graph
	 * (Dijkstra's algorithm) - Return an empty path (i.e. w/ no node) if src
	 * and dst are disconnected.
	 */

	public Path shortestPath(Node src, Node dst) 
		throws A5RuntimeException{
		PriorityQueue<KVPair> heap = new PriorityQueue<KVPair>();
		boolean visited[] = new boolean[nodeCount()];
		MyPath paths[] = new MyPath[nodeCount()];
		
		Iterator<Node> it;
		Node current = src;
		
		try{
			if(src!=getNodeById(src.id)||dst!=getNodeById(dst.id)){
				return new MyPath(this);
				//throw new A5RuntimeException("Source or Destination Node does not Exist in this Graph!");
			}
		}
		catch(NullPointerException e){
			//e.printStackTrace();
			return new MyPath(this);
			//throw new A5RuntimeException("Source or Destination Node is null");
		}
		
		paths[src.getId()]=new MyPath(this);
		paths[src.getId()].appendNode(src);
		
		if(!dst.getNeighbors().hasNext())
			return new MyPath(this);
		
		visited[current.getId()]=true;
		int numVisited=1;
		
		do {
			it = current.getNeighbors();
			while (it.hasNext()) {
				Node next = it.next();
				if (paths[next.getId()] == null || paths[next.getId()].getWeight() > paths[current.getId()].getWeight()
						+ getEdgeByEndpoints(current, next).getWeight()) {
					MyPath temp = paths[current.getId()].clone();
					temp.appendNode(next);
					paths[next.getId()]= temp;
					heap.add(new KVPair<MyPath,Node>(temp,next));
				}
			}
			while(visited[current.getId()]){
				KVPair temp=heap.poll();
				if(temp.val==dst){
					return (Path)temp.key;
				}
				current = (Node)temp.val;
			}
			visited[current.getId()]=true;
			numVisited++;
		}while (heap.size()>0&&numVisited<nodeCount());

		return new MyPath(this);

	}

	// ---OPTIONAL---OPTIONAL---OPTIONAL---

	/*
	 * The following methods are OPTIONAL, you do NOT have to implement them for
	 * the assignment. However, they may make testing easier.
	 */

	// ---------- I/O methods ----------
	/**
	 * Read in the graph from a file with 'filename'. Return the Graph object if
	 * successful, an A5RuntimeException will be thrown if some error has been
	 * detected.
	 */
	public static MapGraph readGraph(String filename) {
		MapGraph ret = new MapGraph();
		ret.loadGraph(filename);
		return ret;
	}

	/**
	 * load the graph by reading from a file with 'filename'. (non-static ver.)
	 * An A5RuntimeException will be thrown if some error has been detected
	 * (e.g. the input file doesn't represent a valid graph). - After the call,
	 * the graph object upon which the method is invoked will be updated to
	 * represent the graph specified by the input file.
	 */
	public void loadGraph(String filename) throws A5RuntimeException {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			int numNodes = Integer.parseInt(in.readLine());
			while (numNodes != nodeCount()) {
				String line = in.readLine();
				int x = Integer.parseInt(line.substring(0, line.indexOf(" ")));
				int y = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
				addNode(x, y);
			}
			int numEdges = Integer.parseInt(in.readLine());
			while (numEdges != edgeCount()) {
				String line = in.readLine();
				int x = Integer.parseInt(line.substring(0, line.indexOf(" ")));
				int y = Integer.parseInt(line.substring(line.indexOf(" ") + 1,
						line.lastIndexOf(" ")));
				double weight = Double.parseDouble(line.substring(line
						.lastIndexOf(" ")));
				if (weight == -1) {
					addEdgeWeighedByDistance(x, y);
				} else {
					addEdge(x, y, weight);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new A5RuntimeException("File not found");
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new A5RuntimeException("Invalid File");
		} catch (IOException e) {
			e.printStackTrace();
			throw new A5RuntimeException("Invalid File");
		}
	}

	/**
	 * Write the graph to file withe 'filename'. Throws A5RuntimeException if
	 * some error has occurred.
	 */
	public static void writeGraph(MapGraph g, String filename) {
		g.saveGraph(filename);
	}

	/**
	 * Non-static version of writeGraph Throws A5RuntimeException if some error
	 * has occurred.
	 */
	public void saveGraph(String filename) {
		try {
			PrintWriter out = new PrintWriter(filename);
			out.println(nodeCount());
			for (int i = 0; i < nodeCount(); i++) {
				out.println("" + getNodeById(i).x + " " + getNodeById(i).y);
			}
			out.println(edgeCount());
			for (int i = 0; i < edgeCount(); i++) {
				out.println("" + getEdgeById(i).v1.getId() + " "
						+ getEdgeById(i).v2.getId() + " "
						+ getEdgeById(i).getWeight());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new A5RuntimeException("File not found");
		}
	}

}

/**
 * HINT!! you might want to implement and use these two classes in your graph
 * class...
 */



/**
 * a comparable clonable extension of Path
 * 
 */
class MyPath extends Path implements Comparable<MyPath>, Cloneable {
	private double weight = 0;

	private MapGraph map;
	/**uses this mapgraph constructor*/
	MyPath(MapGraph m) {
		super();
		map = m;
	}
	/**retusn the weight of this path*/
	double getWeight() {
		if(nodeCount()>0)
			return weight;
		return MapGraph.INFINITY;
	}
	/**compares 2 paths*/
	public int compareTo(MyPath other) {
		if (getWeight() == other.getWeight())
			return 0;
		if (getWeight() < other.getWeight())
			return -1;
		return 1;
	}
	/**adds a node to this path*/
	public void appendNode(Node v) {
		if(nodeVec.size()>0){
		weight += map.getEdgeByEndpoints(nodeVec.get(nodeVec.size()-1), v)
				.getWeight();
		}
		super.appendNode(v);
	}
	/**compares 2 paths used in hashtable*/
	public boolean equals(Object other) {
		return other instanceof MyPath && nodeVec .equals(((MyPath)other).nodeVec)
				&& currentProgress == ((MyPath)other).currentProgress&&map==((MyPath)other).map;
	}
/**copies a path to a new path*/
	public MyPath clone() {

		MyPath clone = null;
		try {
			clone = (MyPath) super.clone();
			// make the shallow copy of the object of type Vector
			clone.nodeVec = new Vector<Node>(nodeVec);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;

	}
	/**returns a string of this path*/
	public String toString(){
		String ret="";
		for(Node a:this){
			ret+=a.id+" ";
		}
		return ret;
	}
	
}

/**
 * A utility class holds a pair of int
 */
class IntPair implements Comparable<IntPair> {
	protected int first, second;

	/**Constructor for 2 values to link them together*/
	public IntPair(int _first, int _second) {
		first =Math.min(_first,_second);
		second = Math.max(_first,_second);
	}
	/**compares 2 sets of values*/
	public int compareTo(IntPair other) {
		if (equals(other)) {
			return 0;
		}
		if (other.first > first
				|| (other.first == first && other.second > second)) {
			return -1;
		}
		return 1;
	}
	/**checkes equallity in a hashmap*/
	public boolean equals(Object o) {
		return o instanceof IntPair && first == ((IntPair)o).first && second ==((IntPair) o).second;
	}
	/**uses this hashcode of maping instead of normal*/
	public int hashCode(){
		return new Point(first,second).hashCode();
	}
	
}

/**
 * A utility class, holds a key-value object pair. The key has Comparable type
 * K, the object has generic type V
 */
class KVPair<K extends Comparable<K>, V> implements Comparable<KVPair<K, V>> {
	K key;

	V val;

	/**Constructor for k,V pair*/
	public KVPair(K _key, V _val) {
		key = _key;
		val = _val;
	}
	/**Compares 2 KVPairs*/
	public int compareTo(KVPair<K, V> other) {
		return key.compareTo(other.key);
	}
	/**changes how the hashCode + map work*/
	public boolean equals(Object other) {
		return other instanceof KVPair && key == ((KVPair)other).key && val == ((KVPair)other).val;
	}
	/**changes how the hashCode + map work*/
	public int hashCode(){
		return key.hashCode()+val.hashCode();
	}
}
