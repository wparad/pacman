package game.actor;

import game.Constants;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.EnumSet;
import java.util.Timer;
import java.util.TimerTask;

enum GhostState { ACTIVE, SEND_TO_CORRAL, CORRAL, VULNERABLE, RECOVERING };
/**
 * Class to represent a Ghost in the Pacman Game
 * Ghosts add the ability to become vulnerable and to 'fly' back to the corral
 *
 */
public class Ghost extends Actor {

	// the possible states and current state of the ghost
	static final EnumSet<GhostState> MOVING = EnumSet.of(GhostState.ACTIVE, GhostState.VULNERABLE, GhostState.RECOVERING);
	public GhostState ghostState = GhostState.ACTIVE;
	
	// how near the ghost corral does it need to be to stop flying
	public static double NEAR_THRESHOLD = 10;
	// how long should a ghost be stuck in the corral
	public static long CORRAL_TIMEOUT = 5000;
	
	// timer to free the ghost
	private Timer corralTimer = new Timer(false);
	private FreeGhostTask currentFreeGhostTask;

	// images used
	private Images vulnerableGhostImages = new Images(Constants.vulnerableGhostImageNames);
	private Images recoveringGhostImages = new Images(Constants.recoveringGhostImageNames);

	/**
	 * create a new ghost
	 * @param unitID
	 * @param speed in pixels per secound
	 * @param initialLocation
	 * @param imagePaths
	 */
	public Ghost(String unitID, double speed, Point2D initialLocation, String[][] imagePaths) {
		super(unitID, speed, initialLocation, imagePaths);
	}
	/**
	 * makes the Ghost vulnerable to being eaten by pacman
	 * swaps images with the blue ghost
	 *
	 */
	public void makeVulnerable() {
		if(ghostState == GhostState.ACTIVE||ghostState == GhostState.RECOVERING ){
			ghostState = GhostState.VULNERABLE;
			currentImages = vulnerableGhostImages;
			setSpeed(.7*ospeed, -1);
			
			//allows ghosts to change direction but and not
			//clip walls
			int xLoc = (int)getCurrentCellPoint().getX();
			int yLoc = (int)getCurrentCellPoint().getY();
			currentPosition=new Point(xLoc*CELL,yLoc*CELL);
			
			currentDirection = MovementDirection.NONE;
			if(currentFreeGhostTask!=null){
				currentFreeGhostTask.cancel();
			}
		}
	}
	
	
	
	/**
	 * put the ghost into flashing, recovery mode
	 *
	 */
	public void makeRecovering() {
		if(ghostState == GhostState.VULNERABLE ){
			ghostState = GhostState.RECOVERING;
			currentImages = recoveringGhostImages;
		}
	}
	/**
	 * makes the ghost appear normally
	 *
	 */
	public void makeActive() {
		if(ghostState == GhostState.RECOVERING || ghostState == GhostState.CORRAL ){
			ghostState = GhostState.ACTIVE;
			currentImages = standardImages;
			setSpeed(ospeed, -1);
		}
	}
	/**resets this Ghosts to original state of game*/
	   public void reset() {
		      super.reset();
		      ghostState = GhostState.ACTIVE;
				currentImages = standardImages;
				setSpeed(ospeed, -1);
	   }
	   /**returns if this ghost is vulnerable to super pacman*/
	public boolean isVulnerable() {
		return ghostState == GhostState.VULNERABLE;
	}
	/**returns if this ghost is recovering*/
	public boolean isRecovering() {
		return ghostState == GhostState.RECOVERING;
	}
	/**returns if this ghost can eat kill pacman*/
	public boolean isActive() {
		return ghostState == GhostState.ACTIVE;
	}
	/**A class to update Ghosts*/
	class FreeGhostTask extends TimerTask {
		/**Updates the Ghost*/
		public void run() {
			makeActive();
		}
	}
	/**
	 * forces the ghost to fly to the Corral, purges any timers
	 * and sets the timer to free the ghost from the Corral
	 *
	 */
	public void sendToCorral() {
		ghostState = GhostState.SEND_TO_CORRAL;
		corralTimer.purge();
		currentFreeGhostTask=new FreeGhostTask();
		corralTimer.schedule(currentFreeGhostTask, CORRAL_TIMEOUT);
		currentImages = standardImages;
	}

	/**
	 * Adds a direction for the ghost to travel in in accordance to the
	 * way 'pacman works'. 
	 * @param newDirection
	 */
	public void addDirection(MovementDirection newDirection) {

		// if we are not moving now, set both directions to be newDirection
		
		// if we are turning around, set both
		if (MovementDirection.isOpposite(currentDirection, newDirection))
			 newDirection = currentDirection;
		else if (currentDirection == MovementDirection.NONE)
			currentDirection = newDirection;			
		nextDirection = newDirection;
	}
	
	/**
	 * move the ghost along is chosen path, OR fly to the Corral depending on the state
	 * @param dt the delta time that has passed since the last drawing
	 */
	public void move(double dt) {
		if (MOVING.contains(ghostState)) {
			super.move(dt);
		} else {
			Point2D corral = game.map.getCorralLocation();
			if (!isNear(currentPosition, corral)) {
				Point2D dest = corral;
				double dx = dest.getX() - this.currentPosition.getX();
				double dy = dest.getY() - this.currentPosition.getY();
				Point2D vec = normalize(new Point2D.Double(dx, dy));
				currentPosition.setLocation(currentPosition.getX() + (vec.getX() * speed / 5), currentPosition.getY() + (vec.getY() * speed / 5));
			} else {
				currentPosition.setLocation(corral);
				ghostState = GhostState.CORRAL;
				currentImages = standardImages;
			}
		}
	}
	/**
	 * Normaizes Point2D to a length of 1
	 * @param vec
	 * @return
	 */
	public Point2D normalize(Point2D vec) {
		double x = vec.getX();
		double y = vec.getY();
		double d_2 = x * x + y * y;
		double d_root = Math.sqrt(d_2);
		return new Point2D.Double(x / d_root, y / d_root);
	}
	/**
	 * returns true if pt1 is within NEAR_THRESHOLD of pt2 
	 * @param pt1
	 * @param pt2
	 * @return
	 */
	public boolean isNear(Point2D pt1, Point2D pt2) {
		return pt1.distance(pt2) < NEAR_THRESHOLD;
	}
}
