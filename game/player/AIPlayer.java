package game.player;

import game.Constants;
import game.Game;
import game.actor.Ghost;
import game.actor.MovementDirection;
import graph.MapGraph;
import graph.Path;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class represents the ghosts, which are controlled by the program.
 */
public class AIPlayer extends Player implements Constants {

	List<Ghost> ghosts = new ArrayList<Ghost>();

	public Timer timer = new Timer(false);

	private Ghost blinky, inky, pinky, clyde;

	Random rand = new Random();

	final int calculationInterval = 50; // new path calculation interval

	/** Initializes Ghosts to attack pacman in the game */
	public AIPlayer(Game game, game.map.PacmanMap map, double[] pos) {
		super(game);
		blinky = new Ghost("blinky", 100, new Point2D.Double(pos[0], pos[1]),
				blinkyImageNames);
		pinky = new Ghost("pinky", 85, new Point2D.Double(pos[2], pos[3]),
				pinkyImageNames);
		inky = new Ghost("inky", 70, new Point2D.Double(pos[4], pos[5]),
				inkyImageNames);
		clyde = new Ghost("clyde", 55, new Point2D.Double(pos[6], pos[7]),
				clydeImageNames);
		// initialize ghosts
		ghosts.add(blinky);
		ghosts.add(pinky);
		ghosts.add(inky);
		ghosts.add(clyde);
		timer.scheduleAtFixedRate(new MoveGhostTask(), calculationInterval,
				calculationInterval);
	}

	public void setAIGhosts() {
		if (ghosts.size() != 2) {
			ghosts.remove(blinky);
			ghosts.remove(pinky);
		}
	}

	public void setGhosts() {
		if (ghosts.size() == 2) {
			ghosts.add(blinky);
			ghosts.add(pinky);
		}
	}

	/** Moves all the ghosts location based on time difference dt */
	public void move(double dt) {
		for (Ghost g : ghosts) {
			g.move(dt);
		}
	}

	/** returns a list of the ghosts */
	public List<Ghost> getActorList() {
		return ghosts;
	}

	/** A class to update positions of all the ghosts */
	class MoveGhostTask extends TimerTask {
		/** move the ghosts continuously */
		public void run() {
			moveGhosts();
		}

		/**
		 * YOUR CODE GOES HERE!!!!!! Write the code to move the ghosts so that
		 * they chase pacman. You can use your graph shortestPath algorithm to
		 * find the best route to pacman. (game.getMap().getGraph()) Use
		 * ghost.addDirection(direction_to_move) to tell the ghosts which way to
		 * go
		 * 
		 */
		protected void moveGhosts() {
			MapGraph graph = game.map.getGraph();

			for (Ghost ghost : ghosts) {
				int pathsize = -2;
				for (int target = 0; target < game.localPlayer.length; target++) {
					if (game.localPlayer[target].getLives() < 0)
						continue;
					Point2D pac = game.localPlayer[target].getPacman()
							.getCurrentCellPoint();

					int xLoc = (int) ghost.getCurrentCellPoint().getX();
					int yLoc = (int) ghost.getCurrentCellPoint().getY();
					int dir = 1;
					if (!ghost.isActive()) {
						dir = -1;
					}
					Path path = graph.shortestPath(graph.getNodeByCoord(xLoc,
							yLoc), graph.getNodeByCoord((int) pac.getX(),
							(int) pac.getY()));
					MovementDirection md = null;
					if (path == null || path.nodeCount() <= 1) {
						md = MovementDirection.values()[rand.nextInt(4)];
						pathsize = -1;
					} else {
						if (pathsize == -2)
							pathsize = path.nodeCount();
						else if (path.nodeCount() > pathsize)
							break;
						int stepX = (int) path.nodeAt(1).getCoordinates()
								.getX();
						int stepY = (int) path.nodeAt(1).getCoordinates()
								.getY();
						md = game.map.getDirection(dir * (stepX - xLoc), dir
								* (stepY - yLoc));
						MovementDirection orig = md;
						if (!game.map.canMoveDirection(xLoc, yLoc, md, true)) {
							if (stepX - xLoc == 0) {
								md = MovementDirection.EAST;
								if (!game.map.canMoveDirection(xLoc, yLoc, md,
										false)) {
									md = MovementDirection.WEST;
								}
								if (!game.map.canMoveDirection(xLoc, yLoc, md,
										false)) {
									md = MovementDirection.getOpposite(orig);
								}
							} else {
								md = MovementDirection.NORTH;
								if (!game.map.canMoveDirection(xLoc, yLoc, md,
										false)) {
									md = MovementDirection.SOUTH;
								}
								if (!game.map.canMoveDirection(xLoc, yLoc, md,
										false)) {
									md = MovementDirection.getOpposite(orig);
								}
							}
						}
					}
					ghost.addDirection(md);
				}
			}
		}
	}
}
