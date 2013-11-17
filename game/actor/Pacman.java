package game.actor;

import java.awt.Point;
import java.awt.geom.Point2D;
/**
 * the class to represent the user controlled hero of the game
 *
 */
public class Pacman extends Actor {
	
	private Item item;
	
	
	/**returns the item that this pacman holds*/
	public Item getItem(){
		return item;
	}
	/**sets the item that this pacman holds*/
	public void setItem(String name,int xLoc,int yLoc){
		item=new Item(name,new Point( xLoc, yLoc));
	}
	
	/**removes an item that this pacman holds*/
	public void removeItem(){
		item=null;
	}
	/**constructs a new pacman for playing*/
   public Pacman(double speed, Point2D initialLocation, String[][] imagePaths) {
      super("pacman", speed, initialLocation, imagePaths);
   }

   /**
    * use this method to register directional commands. Each time the arrow key
    * is pressed, this method should be called to store the new direction. This
    * method deals with the logic needed to determine what currentDirection and
    * nextDirection should be.
    * 
    * @param newDirection
    */
   public void addDirection(MovementDirection newDirection) {
      // if we are not moving now, set both directions to be newDirection
      if (currentDirection == MovementDirection.NONE ||
    		  MovementDirection.isOpposite(currentDirection, newDirection)) 
         currentDirection = newDirection;
      nextDirection = newDirection;
   }
}
