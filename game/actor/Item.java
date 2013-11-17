package game.actor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
/**A class to keep track of items on the map*/
public class Item extends Actor {
	Rectangle size = new Rectangle(0, 0, CELL, CELL);
	Image image;
	/**Constructor tat makes a new item in a particular position*/
	public Item(String unitType, Point2D initialPosition) {
		super(unitType, 0, initialPosition, tokenImageNames);
		size.setLocation((int) getCurrentPosition().getX(),
				(int) getCurrentPosition().getY());
		image= new ImageIcon(ClassLoader.getSystemResource(iconDirectory + name + ".png")).getImage();
	}

	/**sets the size that an item can exist as*/
	public void setSize(int w, int h) {
		size.setSize(w, h);
	}
	/**returns the cell of an item*/
	public Rectangle getBoundingBox() {
		return size;
	}
	

	
	   /**
	    * draw the unit onto graphics context g
	    * 
	    * @param g
	    *           the Graphics object that we can use to draw
	    */
	   public void draw(Graphics g) {
	      g.drawImage(image, (int)(currentPosition.getX() - size.getWidth()/2), (int)(currentPosition.getY() - size.getHeight()/2), null);
	   }
	   
}

