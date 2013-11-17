package game.actor;

import game.Game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
/**A class to keep track of mines*/
public class Mine extends Item {

	boolean armed = false;
	boolean ignited = false;
	int player;
	int frame=0;
	Image images[] = new Image[6];
	Game game;
	/**Constructs a new mine onto the field*/
	public Mine(Point2D initialPosition,int p) {
		super("bomb", initialPosition);
		images[0] = new ImageIcon(ClassLoader.getSystemResource(iconDirectory
				+ bombNames[0] + ".png")).getImage();
		images[1] = new ImageIcon(ClassLoader.getSystemResource(iconDirectory
				+ bombNames[1] + ".png")).getImage();
		player=p;
	}
	/**Arms a mine for detonation*/
	public void arm() {
		armed = true;
	}
	/**gets the player that the mine is from*/
	public boolean isPlayer(int in){
		return in==player;
	}
	/**returns the arming state of a mine*/
	public boolean isArmed() {
		return armed;
	}
	/**returns the state if a mine is ready to blow*/
	public boolean isIgnited() {
		return ignited;
	}
	/**explodes a mine*/
	public void explode(Game g){
		armed=false;
		ignited=true;
		images[0] = new ImageIcon(ClassLoader.getSystemResource(iconDirectory
				+ explosionNames[0] + ".png")).getImage();
		images[1] = new ImageIcon(ClassLoader.getSystemResource(iconDirectory
				+ explosionNames[1] + ".png")).getImage();
		images[2] = new ImageIcon(ClassLoader.getSystemResource(iconDirectory
				+ explosionNames[2] + ".png")).getImage();
		images[3]=images[2];
		images[4]=images[1];
		images[5]=images[0];
		frame=0;
		game=g;
	}
	
	/**
	 * draw the unit onto graphics context g
	 * 
	 * @param g
	 *            the Graphics object that we can use to draw
	 */
	public void draw(Graphics g) {
		Image image = images[frame++];
		if(armed){
			frame%=2;
		}
		else if(ignited&&frame==6){
				game.removeMine(this);
		}
		else if(!ignited){
			frame=0;
		}
		g.drawImage(image, (int) currentPosition.getX() - CELL / 2,
				(int) currentPosition.getY() - CELL / 2, null);
	}
}