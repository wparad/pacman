package game.map;

import game.A5FatalException;
import game.Constants;
import game.actor.MovementDirection;
import graph.MapGraph;
import graph.Node;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to represent a Map. The map is stored both as a matrix of Terrain tiles
 * and as a Graph were each node is a tile and each edge is the cost of
 * traveling onto that tile.
 * 
 */
public class PacmanMap implements Constants {
	// The grid that the game takes place on.
	protected Cell[][] cellGrid;

	// the graph representation of this Map
	protected MapGraph graph;

	// the width of the map in tiles (NOT pixels)
	protected int tileWidth;

	// the height of the map in tiles (NOT pixels)
	protected int tileHeight;

	private Color wallColor;

	protected BufferedImage background;

	protected Point2D corralLocation;

	protected Point2D warpPoint1;

	protected Point2D warpPoint2;

	/**
	 * Load the map definition for this game. NOTE: the decision to address the
	 * graph by (row, col) translates into (y, x) into other portions of the
	 * code.
	 */
	public PacmanMap() {
		wallColor = Color.blue;
		loadMap("level1.txt");
	}
	/**Loads a new grid to this map*/
	public PacmanMap(String s) {
		loadMap(s);
	}
	/**sets the wall color of this map*/
	public void setColor(Color c) {
		wallColor = c;
	}
	/**sets the color with an int instead of Color*/
	public void setColor(int i) {
		switch (i % 12) {

		case 0:
			wallColor = Color.DARK_GRAY;
			break;

		case 1:
			wallColor = Color.blue;
			break;

		case 2:
			wallColor = Color.cyan;
			break;

		case 3:
			wallColor = Color.gray;
			break;

		case 4:
			wallColor = Color.green;
			break;

		case 5:
			wallColor = Color.LIGHT_GRAY;
			break;

		case 6:
			wallColor = Color.magenta;
			break;

		case 7:
			wallColor = Color.orange;
			break;

		case 8:
			wallColor = Color.pink;
			break;

		case 9:
			wallColor = Color.red;
			break;

		case 10:
			wallColor = Color.white;
			break;

		case 11:
			wallColor = Color.yellow;
			break;

		}
	}
	
	/**returns the color of the wall*/
	public Color getColor() {
		return wallColor;
	}
	/**Loads a new grid to this map by filename*/
	public void loadMap(String mapName) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(ClassLoader
					.getSystemResourceAsStream(mapDirectory + mapName)));
		} catch (Exception e) {
			throw new A5FatalException("Could not read resource");
		}
		List<String> ls = new ArrayList<String>();
		while (true) {
			String s = null;
			try {
				s = in.readLine();
			} catch (Exception eof) {
				throw new A5FatalException("Could not read resource");
			}
			if (s == null)
				break;
			ls.add(s);
		}
		tileHeight = ls.size();
		tileWidth = ls.get(0).length();

		// create background image
		graph = new MapGraph();
		background = new BufferedImage(tileWidth * CELL, tileHeight * CELL,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = background.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, tileWidth * CELL, tileHeight * CELL);

		// create the cells
		cellGrid = new Cell[tileWidth][tileHeight];
		for (int y = 0; y < tileHeight; y++) {
			String s = ls.get(y);
			for (int x = 0; x < tileWidth; x++) {
				char c = s.charAt(x);
				cellGrid[x][y] = new Cell(x, y, c);
				cellGrid[x][y].drawBackground(g, wallColor);
				// C is a corralLocation
				if (c == 'C') {
					corralLocation = new Point2D.Double(x * CELL, y * CELL);
				}
				// W is a warp point
				if (c == 'W') {
					if (warpPoint1 == null) {
						warpPoint1 = new Point2D.Double(x, y);
					} else {
						warpPoint2 = new Point2D.Double(x, y);
					}
				}
				if (this.isTraversable(x, y, true)) {
					graph.addNode(x, y);
				}
			}

		}

		/**
		 * Creates the graph representation of this map.
		 */
		for (int y = 0; y < tileHeight; y++) {
			for (int x = 0; x < tileWidth; x++) {
				if (this.isTraversable(x, y, true))
					for (Point2D point : this.getNeighbors(x, y, true)) {
						Node n1 = graph.getNodeByCoord(x, y);
						Node n2 = graph.getNodeByCoord((int) point.getX(),
								(int) point.getY());
						graph.addEdge(n1.getId(), n2.getId(), 1d);
					}
			}
		}

	}

	/**
	 * returns the location of the Ghost Corral
	 * 
	 * @return
	 */
	public Point2D getCorralLocation() {
		return this.corralLocation;
	}

	/**
	 * return the CellGrid where the actors and walls are located
	 * 
	 * @return
	 */
	public Cell[][] getCellGrid() {
		return cellGrid;
	}

	/**
	 * 
	 * @return the graph
	 */

	public MapGraph getGraph() {
		return graph;
	}

	/**
	 * returns true if tile coordinates (x, y) are traversable, ie, if pacman or
	 * a ghost is allowed to go here
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isTraversable(int x, int y, boolean isGhost) {
		return x >= 0 && y >= 0 && x < tileWidth && y < tileHeight
				&& cellGrid[x][y].isNavigable(isGhost);
	}

	/**
	 * returns true if given current coordinates (tileX, tileY), and
	 * movmentDirection direction, we are allowed to move to the adjecent space
	 * 
	 * @param x
	 * @param y
	 * @param direction
	 * @return
	 */
	public boolean canMoveDirection(int x, int y, MovementDirection direction,
			boolean isGhost) {
		if (isWarpPoint(x, y)
				&& (direction == MovementDirection.EAST || direction == MovementDirection.WEST))
			return true;
		switch (direction) {
		case NORTH:
			return y > 0 && cellGrid[x][y - 1].isNavigable(isGhost);
		case SOUTH:
			return y < tileHeight - 1
					&& cellGrid[x][y + 1].isNavigable(isGhost);
		case EAST:
			return x < tileWidth - 1 && cellGrid[x + 1][y].isNavigable(isGhost);
		case WEST:
			return x > 0 && cellGrid[x - 1][y].isNavigable(isGhost);
		default:
			return false;
		}
	}

	/**
	 * create a list of allowed movement directions for coordinates (tileX,
	 * tileY)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Iterable<Point2D> getNeighbors(int x, int y, boolean isGhost) {
		Set<Point2D> neighbors = new HashSet<Point2D>();
		if (isTraversable(x + 1, y, isGhost))
			neighbors.add(new Point2D.Double(x + 1, y));
		if (isTraversable(x - 1, y, isGhost))
			neighbors.add(new Point2D.Double(x - 1, y));
		if (isTraversable(x, y - 1, isGhost))
			neighbors.add(new Point2D.Double(x, y - 1));
		if (isTraversable(x, y + 1, isGhost))
			neighbors.add(new Point2D.Double(x, y + 1));
		return neighbors;
	}

	/**
	 * returns a movementDirection based on the direction that x and y point
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public MovementDirection getDirection(int x, int y) {
		if (x > 0 && y == 0)
			return MovementDirection.EAST;
		if (x < 0 && y == 0)
			return MovementDirection.WEST;
		if (x == 0 && y > 0)
			return MovementDirection.SOUTH;
		return MovementDirection.NORTH;
	}

	/**
	 * returns an Enumset of Possible directions for an actor at (x, y)
	 * 
	 * @param x
	 * @param y
	 * @param isGhost -
	 *            ghosts are allowed in the corral, so we need to differentiate
	 * @return
	 */
	public EnumSet<MovementDirection> getPossibleDirections(int x, int y,
			boolean isGhost) {
		Set<MovementDirection> possibleDirections = new HashSet<MovementDirection>();
		for (Point2D p : getNeighbors(x, y, isGhost)) {
			
			possibleDirections.add(getDirection((int) p.getX() - x, (int) p
					.getY()
					- y));
		}
		if (possibleDirections.size() == 0)
			return EnumSet.noneOf(MovementDirection.class);
		return EnumSet.copyOf(possibleDirections);
	}
	

	/**
	 * return true if coordinates (tileX, tileY) are an intersection. An
	 * intersection is any point that allows an Actor to move 90 deg from its
	 * current direction
	 * 
	 * @param tileX
	 * @param tileY
	 * @return
	 */
	public boolean isIntersection(int tileX, int tileY, boolean isGhost,
			MovementDirection md) {
		int sum = 0;
		if (isGhost) {
			md = MovementDirection.getOpposite(md);
			if (md != MovementDirection.NORTH
					&& canMoveDirection(tileX, tileY, MovementDirection.NORTH,
							isGhost)) {
				sum += 1;
			}
			if (md != MovementDirection.SOUTH
					&& canMoveDirection(tileX, tileY, MovementDirection.SOUTH,
							isGhost)) {
				sum += 1;
			}
			if (md != MovementDirection.EAST
					&& canMoveDirection(tileX, tileY, MovementDirection.EAST,
							isGhost)) {
				sum += 1;
			}
			if (md != MovementDirection.WEST
					&& canMoveDirection(tileX, tileY, MovementDirection.WEST,
							isGhost)) {
				sum += 1;
			}
		} else {
			if (canMoveDirection(tileX, tileY, MovementDirection.NORTH, isGhost)) {
				sum += 1;
			}
			if (canMoveDirection(tileX, tileY, MovementDirection.SOUTH, isGhost)) {
				sum += 1;
			}
			if (canMoveDirection(tileX, tileY, MovementDirection.EAST, isGhost)) {
				sum += 1;
			}
			if (canMoveDirection(tileX, tileY, MovementDirection.WEST, isGhost)) {
				sum += 1;
			}
		}
		return sum > 1;
	}

	/**
	 * returns true if (x, y) is a warp point
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isWarpPoint(int x, int y) {
		Point2D pt = new Point2D.Double(x, y);
		return warpPoint1.equals(pt) || warpPoint2.equals(pt);
	}

	/**
	 * Finds and returns the matching warp point to (x,y), null if none exists
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Point2D getMatchingWarpPoint(int x, int y) {
		Point2D pt = new Point2D.Double(x, y);
		if (warpPoint1.equals(pt)) {
			return warpPoint2;
		} else if (warpPoint2.equals(pt)) {
			return warpPoint1;
		} else
			return null;
	}

	/**
	 * return the Map's height in tiles
	 * 
	 * @return
	 */
	public int getTileHeight() {
		return tileHeight;
	}

	/**
	 * return the Map's width in tiles
	 * 
	 * @return
	 */
	public int getTileWidth() {
		return tileWidth;
	}

	/**
	 * returns the Image that represents the Map's Background
	 * 
	 * @return
	 */
	public BufferedImage getBackground() {
		return background;
	}
}
