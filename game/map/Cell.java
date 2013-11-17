package game.map;

import game.Constants;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
/**
 * 
 * Class to represent a Cell in the Map. A cell can have many types depending on
 * what actors can traverse it and how it should be drawn
 *
 */
public class Cell implements Constants {
   public int x, y;
   public char type;
   /**Constructor makes a new cell with a specific type*/
   public Cell(int x, int y, char type) {
      this.type = type;
      this.x = x;
      this.y = y;
   }
   
   //called once to create background image  
   /**draws the cells to the background for each type of cell*/
   public void drawBackground(Graphics g,Color c) {
      switch (type) {
      case 'e': //corral exit
         g.setColor(Color.WHITE);
         g.fillRect(x*CELL, y*CELL + CELL/2 - 1, CELL, 3);
         break;
      case 'h': //horizontal line
         g.setColor(c);
         g.fillRect(x*CELL, y*CELL + CELL/2 - 1, CELL, 3);
         break;
      case 'v': //vertical line
         g.setColor(c);
         g.fillRect(x*CELL + CELL/2 - 1, y*CELL, 3, CELL);
         break;
      case 'm': corner(g, -1, 1,c); break; //northeast corner ('m' = maine)
      case 'w': corner(g, 1, 1,c); break; //northwest corner ('w' = washington)
      case 'f': corner(g, -1, -1,c); break; //southeast corner ('f' = florida)
      case 'c': corner(g, 1, -1,c); break; //southwest corner ('c' = california)
      case 'o': //empty navigable cell
      case 'W': //warp space
      case '.': //navigable cell with edible dot
      case '*': //navigable cell with big edible dot
      case 'x': //empty non-navigable cell
      case 'C': //empty the Corral
      default:
         break;
      }
   }
   
   //draw a rounded corner 3 pixels thick
   /**Draws corners a specific way*/
   public void corner(Graphics g, int xSign, int ySign,Color c) {
      Rectangle oldClip = g.getClipBounds();
      g.setClip(x*CELL, y*CELL, CELL, CELL);
      int xBase = x*CELL + xSign*CELL/2;
      int yBase = y*CELL + ySign*CELL/2;
      g.setColor(c);
      g.drawOval(xBase, yBase, CELL, CELL);
      g.drawOval(xBase + 1, yBase + 1, CELL - 2, CELL - 2);
      g.drawOval(xBase - 1, yBase - 1, CELL + 2, CELL + 2);
      g.setClip(oldClip);
   }
   /**
    * returns true if the specified actor is allowed to run over this cell.
    * Ghosts are allowed to go everywhere pacman can, and also into the corral
    * @param isGhost
    * @return
    */
   public boolean isNavigable(boolean isGhost) {
      if (isGhost) return "o.*eCW".indexOf(type) >= 0;
      return "o.*W".indexOf(type) >= 0;
   }
   /**
    * returns true if this cell is a Token
    * @return
    */
   public boolean isToken() {
      return ".*".indexOf(type) >= 0;
   }
}
