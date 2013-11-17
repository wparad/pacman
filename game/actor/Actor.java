package game.actor;

import game.Constants;
import game.Game;
import game.map.PacmanMap;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.EnumSet;
import java.util.Set;

/**
 * Class to represent an Actor in the game
 * Since it is Abstract, it must be extended
 * 
 * 
 */
public abstract class Actor implements Constants {
   
	// these are used to keep track of movement
   static final EnumSet<MovementDirection> SOUTH_OR_EAST = EnumSet.of(MovementDirection.SOUTH, MovementDirection.EAST);
   MovementDirection currentDirection = MovementDirection.NONE;
   MovementDirection nextDirection = MovementDirection.NONE;
   public MovementDirection forbiddenDirection = MovementDirection.NONE;
   private Point2D initialPosition; // where the actor was created
   protected Point2D currentPosition; // the current position of the Actor
   public Point2D lastPosition[]=new Point2D[2];
   double speed,ospeed; // speed in pixels per second
   // static reference to the Game object
   static Game game;
   String name = null;
   /*
    * The standard and current image set used for this Actor
    */
   Images standardImages;
   Images currentImages;

   /**
    * Constructs a new Actor instance
    * 
    * @param unitType
    *           what type of unit
    * @param speed
    *           how fast the unit moves (pixels/sec)
    * @param initialPosition
    *           initial location
    * @param imagePaths
    *           the file paths where this unit's images are stored
    */
   public Actor(String unitType, double speed, Point2D initialPosition, String[][] imagePaths) {
      this.speed = speed;
      ospeed = speed;
      name = unitType;
      currentImages = standardImages = new Images(imagePaths);
      currentPosition = this.initialPosition = initialPosition;
   }
   /**
    *puts the actor back to where it came from 
    */
   public void reset() {
      currentPosition = initialPosition;
   }

   /**
    * Given that delta_time has passed, move this actor. This method relies on
    * the Map for allowable coordinates and on the current and next Movement
    * Direction fields being set. It moves the Actor along currentDirection
    * until it 1.) hits a wall and stops or 2.) reaches an intersection and
    * changes direction.
    * 
    * @param dt
    *           the delta_time - amount of time passed
    */
   public void move(double dt) {
      Point2D newLocation = calculatePotentialNewLocation(dt);
      Point2D newLeadingEdge = convertToLeadingEdge(newLocation);

      // Set up our new, old tiles and calculate the 'leading edge' if we are
      // going South or East
      int newTileX = (int) newLeadingEdge.getX() / CELL;
      int newTileY = (int) newLeadingEdge.getY() / CELL;
      int nlnewTileX = (int) newLocation.getX() / CELL;
      int nlnewTileY = (int) newLocation.getY() / CELL;
      int oldTileX = (int) currentPosition.getX() / CELL;
      int oldTileY = (int) currentPosition.getY() / CELL;
      int tileX = 0;
      int tileY = 0;
      
      if (SOUTH_OR_EAST.contains(currentDirection)) {
         tileX = nlnewTileX;
         tileY = nlnewTileY;
      } else {
         tileX = oldTileX;
         tileY = oldTileY;
      }
      
      // the leading edge of our square has moved to a new tile
      if (oldTileX != newTileX || oldTileY != newTileY) {
         PacmanMap map = game.map;
         // we need to check if the old space is an intersection (only when we
         // 'finish' a space can we turn)
         Set<MovementDirection> possibleDirections = map.getPossibleDirections(tileX, tileY, this instanceof Ghost);
         boolean isIntersection = map.isIntersection(tileX, tileY, this instanceof Ghost,nextDirection);
         if(this instanceof Ghost)
        	 possibleDirections.remove(MovementDirection.getOpposite(currentDirection));
         // if this is an intersection, we can go our 'nextDirection' way
         if (isIntersection &&  nextDirection != currentDirection && directionIsPossible(nextDirection, possibleDirections)) {

            // relocate, redirect and recall this method so we move in the new
            // direction
            currentPosition = new Point2D.Double(tileX * CELL, tileY * CELL);
            currentDirection = nextDirection;
            move(dt);
            return;
         }
         // otherwise, the new space is just a straight line, check if we can
         // move to this space
         if (map.isTraversable(newTileX, newTileY, this instanceof Ghost)) {
            if(map.isWarpPoint(newTileX, newTileY)){
               Point2D warpPoint = map.getMatchingWarpPoint(newTileX, newTileY);
               newLocation = new Point2D.Double(warpPoint.getX() * CELL, warpPoint.getY() * CELL); 
            }
            // no problems then
         } else { // Or we have reached a dead end, so stop moving
        	if(nextDirection == forbiddenDirection){
        		int x = (int) getCurrentPosition().getX();
        		int y = (int) getCurrentPosition().getX();
        		for(MovementDirection MD: possibleDirections){
        			currentDirection = MD;
        			nextDirection = MD;
        		}
        		if(currentDirection == MovementDirection.NORTH){
        			newLocation = new Point2D.Double(x,y - CELL);
        		}
        		if(currentDirection == MovementDirection.SOUTH){
        			newLocation = new Point2D.Double(x,y + CELL);
        		}
        		if(currentDirection == MovementDirection.EAST){
        			newLocation = new Point2D.Double(x + CELL,y );
        		}
        		if(currentDirection == MovementDirection.WEST){
        			newLocation = new Point2D.Double(x - CELL,y);
        		}
        	}
        	currentPosition = new Point2D.Double(tileX * CELL, tileY * CELL);
            newLocation = currentPosition;
            forbiddenDirection = MovementDirection.getOpposite(currentDirection);
            nextDirection = MovementDirection.NONE;
            currentDirection = nextDirection;
         }
      }

      

      
      if(lastPosition[0]==null||(!lastPosition[0].equals(getCurrentCellPoint()))){
    	  lastPosition[1]=lastPosition[0];
    	  lastPosition[0]=getCurrentCellPoint();
    	  
    	  
      }
//    move to whatever new position we computed above
      currentPosition = newLocation;
   }

   /**
    * Check to see if direction is a member of possibleDirections
    * 
    * @param direction
    * @param possibleDirections
    * @return true if direction is contained in possibleDirections
    */
   private boolean directionIsPossible(MovementDirection direction, Iterable<MovementDirection> possibleDirections) {
      for (MovementDirection dir : possibleDirections) {
         if (dir == direction) return true;
      }
      return false;
   }

   /**
    * Calculate where we would be if we moved for dt time along our current
    * direction
    * 
    * @param dt
    *           delta_time - how much time has passed
    * @return the new potential location
    */
   private Point2D calculatePotentialNewLocation(double dt) {
      double movePotential = getSpeed() * dt;
      switch (currentDirection) {
      case NONE: return currentPosition;
      case NORTH: return new Point2D.Double(currentPosition.getX(), currentPosition.getY() - movePotential);
      case SOUTH: return new Point2D.Double(currentPosition.getX(), currentPosition.getY() + movePotential);
      case EAST: return new Point2D.Double(currentPosition.getX() + movePotential, currentPosition.getY());
      case WEST: return new Point2D.Double(currentPosition.getX() - movePotential, currentPosition.getY());
      default: return null;
      }
   }

   /**
    * Takes the upperLeft corner of the actor and returns the 'leading edge'.
    * The Leading edge is the part of the image that is at the front, based on
    * the direction of the Actor's movment
    * 
    * @param upperLeft
    *           the coordinates of the image
    * @return the leading edge's coordinates
    */
   private Point2D convertToLeadingEdge(Point2D upperLeft) {
      if (currentDirection == MovementDirection.SOUTH) {
         return new Point2D.Double(upperLeft.getX(), upperLeft.getY() + CELL);
      }
      if (currentDirection == MovementDirection.EAST) {
         return new Point2D.Double(upperLeft.getX() + CELL, upperLeft.getY());
      }
      return upperLeft;
   }

   /**
    * draw the unit onto graphics context g
    * 
    * @param g
    *           the Graphics object that we can use to draw
    */
   public void draw(Graphics g) {
      Image image = currentImages.getNextImage(currentDirection);
      g.drawImage(image, (int)currentPosition.getX() - CELL/2, (int)currentPosition.getY() - CELL/2, null);
   }

   /**checks collisions with other actors*/
   public boolean collidesWith(Actor a) {
      Rectangle rect = a.getBoundingBox();
      return rect.intersects(this.getBoundingBox());
   }

   /**gets the box of a actor on the board*/
   public Rectangle getBoundingBox() {
      return new Rectangle((int) getCurrentPosition().getX(), (int) getCurrentPosition().getY(), CELL, CELL);
   }

   /**
    * 
    * @return the current position of this unit
    */
   public Point2D getCurrentPosition() {
      return currentPosition;
   }
   /**Returns the corner of a cell of a actor*/
   public Point getCurrentCellPoint() {
      Point2D p = getCurrentPosition();
      return new Point((int)p.getX()/CELL, (int)p.getY()/CELL);
   }
   /**Returns the current speed of this actor*/
   public double getSpeed() {
      return speed;
   }
   /**returns the original speed of the actor*/
   public double getOSpeed(){
	   return  ospeed;
   }
   /**returns the name of this actor*/
   public String getName(){
	   return  name;
   }
   /**Sets which play this actor is in*/
   public static void setGame(Game game){
	   Actor.game = game;
   }
   /**Sets the original and current speed*/
   public void setSpeed(double speed,double ospeed){
	   this.speed = speed;
	   if(ospeed != -1) this.ospeed = ospeed;
   }

}
