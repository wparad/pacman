package game.player;

import game.A5FatalException;
import game.Game;
import game.actor.Ghost;
import game.actor.MovementDirection;
import game.actor.Token;
import game.map.PacmanMap;
import graph.MapGraph;
import graph.Path;

import java.awt.geom.Point2D;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class AILocal extends LocalPlayer {
	protected static Game game;

	private final int calculationInterval = 150;

	private MovementDirection lastDirection = MovementDirection.NONE;

	private Timer timer = new Timer(false);

	private static Set<Token> tokenList = null;

	/** Contructor for createding an Autoplayed game */
	public AILocal(Game game, String[][] imagePaths, double[] pos) {
		super(game, imagePaths, pos, true);
		AILocal.game = game;
	}

	/** sends the private field tokens to this class for initilization */
	public AILocal(Game game, Set<Token> t) throws A5FatalException {
		super(game);
		if (AILocal.game == game)
			tokenList = t;
		else
			throw new A5FatalException("Inconsistency");
	}

	/** starts pacman moving */
	public void start() {
		timer.scheduleAtFixedRate(new MovePacTask(this), calculationInterval,
				calculationInterval);
	}

	/** A class to keep track of the moving pacman */
	class MovePacTask extends TimerTask {
		LocalPlayer current;
		MovePacTask(LocalPlayer c){
			current=c;
		}
		public void run() {
			if(current!=game.localPlayer[0]){
				timer.cancel();
			}
			else{
			movePacmen();

			}

		}

		/** Actually updates pacman's position */
		protected void movePacmen() {
			MapGraph graph = game.map.getGraph();
			Point2D pac = game.localPlayer[0].getPacman().getCurrentCellPoint();
			int xpos = (int) pac.getX(), ypos = (int) pac.getY();
			PacmanMap map = game.map;
			Set<MovementDirection> possible = map.getPossibleDirections(xpos,
					ypos, false);
			possible.remove(MovementDirection.getOpposite(lastDirection));
			MovementDirection md = MovementDirection.NONE, md2 = MovementDirection.NONE, mdfinal = MovementDirection.NONE, mdGhost = MovementDirection.NONE;
			Path lastpath = null, path = null, secondpath = null;
			for (Ghost g : game.aiPlayer.ghosts) {
				if (!g.isActive())
					continue;
				int x = (int) g.getCurrentCellPoint().getX();
				int y = (int) g.getCurrentCellPoint().getY();
				path = graph.shortestPath(graph.getNodeByCoord(xpos, ypos),
						graph.getNodeByCoord(x, y));
				if (lastpath == null
						|| (path != null && path.nodeCount() < lastpath
								.nodeCount())) {
					lastpath = path;
				}
			}
			if (lastpath != null && lastpath.nodeCount() > 1
					&& lastpath.nodeCount() < 8) {
				int stepX = (int) lastpath.nodeAt(1).getCoordinates().getX();
				int stepY = (int) lastpath.nodeAt(1).getCoordinates().getY();
				mdGhost = game.map.getDirection((stepX - xpos), (stepY - ypos));
			}
path = null;
lastpath = null;
			for (Token token : tokenList) {
				if (lastpath != null && lastpath.nodeCount() <= 2)
					break;
				if (!token.isVisible())
					continue;
				int x = (int) token.getCurrentCellPoint().getX();
				int y = (int) token.getCurrentCellPoint().getY();
				path = graph.shortestPath(graph.getNodeByCoord(xpos, ypos),
						graph.getNodeByCoord(x, y));
				if (lastpath == null
						|| (path != null && path.nodeCount() < lastpath
								.nodeCount())) {
					if (lastpath != null && lastpath.nodeCount() > 1) {
						int stepX = (int) lastpath.nodeAt(1).getCoordinates()
								.getX();
						int stepY = (int) lastpath.nodeAt(1).getCoordinates()
								.getY();
						md = game.map.getDirection((stepX - xpos),
								(stepY - ypos));
					}

					if (secondpath != null && secondpath.nodeCount() > 1) {
						int stepX = (int) secondpath.nodeAt(1).getCoordinates()
								.getX();
						int stepY = (int) secondpath.nodeAt(1).getCoordinates()
								.getY();
						md2 = game.map.getDirection((stepX - xpos),
								(stepY - ypos));
						if (md2 != md)
							mdfinal = md2;

					}
					secondpath = lastpath;
					lastpath = path;
				}
			}
			if (lastpath != null && lastpath.nodeCount() > 1) {
				int stepX = (int) lastpath.nodeAt(1).getCoordinates().getX();
				int stepY = (int) lastpath.nodeAt(1).getCoordinates().getY();
				md = game.map.getDirection((stepX - xpos), (stepY - ypos));
			} else
				md = lastDirection;
			possible.remove(mdGhost);

			if (!possible.contains(md))
				md = mdfinal;
			else
				mdfinal = md;
			if (!possible.contains(mdfinal)) {
				for (MovementDirection m : possible)
					md = m;
			}
			lastDirection = md;
			game.localPlayer[0].getPacman().addDirection(md);
		}
	}
}
