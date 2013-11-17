package game.actor;

/**
 * Enum to represent the direction an Actor is moving in
 *
 */
public enum MovementDirection {
   NORTH, SOUTH, EAST, WEST, NONE;
/**returns if these Directions are opposite to each other*/
   public static boolean isOpposite(MovementDirection d1, MovementDirection d2) {
      return (d1 == NORTH && d2 == SOUTH)
      || (d1 == SOUTH && d2 == NORTH)
      || (d1 == EAST && d2 == WEST)
      || (d1 == WEST && d2 == EAST);
   }
   /**returns the opposite of this direction*/
   public static MovementDirection getOpposite(MovementDirection d) {
      if (d == NORTH) return SOUTH;
      if (d == SOUTH) return NORTH;
      if (d == EAST) return WEST;
      if (d == WEST) return EAST;
      return NONE;
   }
}
