package gui;

import game.Constants;
import game.Game;
import game.Phase;
import game.actor.Ghost;
import game.actor.Item;
import game.actor.Pacman;
import game.map.Cell;
import game.map.PacmanMap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Class to extend the functionality of ScrollablePicture so that we can create
 * a background image from a map definition file. This class also serves as the
 * primary redering platform - all on-screen drawing occurs in the
 * paintComponent method
 * 
 * 
 */
public class Background extends JPanel implements Constants {

	private static final long serialVersionUID = -1567109021282882103L;

	private static final ArrayList<Message> messages = new ArrayList<Message>();

	// Reference to the game that this is being played on
	protected Game game;

	// icon for lives
	Image pacLife;
	
	//back buffer for double buffering
	BufferedImage buffer;
	
	Graphics2D gBuf;

	// keep track of time left in the animation phase
	protected long animationTime = 0;

	Cell[][] cellGrid;

	/**
	 * Create a new background for game. The scrollable image and listeners are
	 * created.
	 * 
	 * @param game
	 *            reference to the game object. this is used for getting units
	 *            to draw and statistics
	 */
	public Background(Game game) {
		this.setLayout(null);
		this.game = game;
		PacmanMap map = game.map;
		Toolkit kit = Toolkit.getDefaultToolkit();
		pacLife = kit.createImage(iconDirectory + "pacmanleft.png");
		buffer=new BufferedImage(CELL * map.getTileWidth(), CELL
				* map.getTileHeight() + 50, BufferedImage.TYPE_INT_RGB);
		gBuf=buffer.createGraphics();
		cellGrid = map.getCellGrid();
		setPreferredSize(new Dimension(CELL * map.getTileWidth(), CELL
				* map.getTileHeight() + 50));
	}

	/**
	 * This method is called automatically by java whenever repaint() is called.
	 * All drawing to the screen occurs here
	 * 
	 * @param g
	 *            the graphics object for all drawing commands that java
	 *            provides when this method is called
	 *            this method uses double buffering to improve paint speed
	 *            this takes advantage of being able to paint all the pixels
	 *            one time is faster then many
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(gBuf);

		// paint the board
		gBuf.setColor(Color.BLACK);
		gBuf.drawImage( game.map.getBackground(), 0, 0, null);
		gBuf.fillRect(0,  game.map.getTileHeight()*CELL, getWidth(), 50);
		
		// draw the navigable cells
		game.drawTokens(gBuf);
		
		// draw the navigable cells
		game.drawItems(gBuf);
		
		game.drawMines(gBuf);

		if (game.getPhase() == Phase.PLAY_GAME) {
			for (int i = 0; i < game.numPlayers; i++) {
				if (i != game.deadPlayer) {
					Pacman pacman = game.localPlayer[i].getPacman();
					pacman.draw(gBuf);
				}
			}
			List<Ghost> ghostList = game.aiPlayer.getActorList();

			for (Ghost gh : ghostList) {
				gh.draw(gBuf);
			}
			gBuf.setColor(Color.yellow);
			// draw lives
			for (int p = 0; p < game.numPlayers; p++) {
				if (p != game.deadPlayer) {
					gBuf.drawString(" Item: " ,10 + 300 * p, getHeight() - 60);
					Item item= game.localPlayer[p].getPacman().getItem();
					
					gBuf.fillRect(50 + 300 * p, getHeight() - 70,2*CELL,2*CELL);
					
					if(item!=null){
						item.draw(gBuf);
					}
					
					gBuf.drawString(" Lives:", 100 + 300 * p, getHeight() - 10);
					for (int i = 0; i < game.localPlayer[p].getLives(); i++) {
						// System.out.println(Actor.iconDirectory);
						gBuf.drawImage(pacLife, 150 + 300 * p + 50 * i,
								getHeight() - 40, this);

					}
					// draw score
					gBuf.drawString(" Score: " + game.localPlayer[p].getScore(),
							10 + 300 * p, getHeight() - 10);
				}
			}

			// draw messages
			for (int i = 0; i < messages.size(); i++) {
				if (messages.get(i).isOver()) {
					messages.remove(messages.get(i));
				} else {
					messages.get(i).paint(gBuf);
				}
			}
/*
			gBuf.drawString(" t: " + (int) (animationTime / 1000) + ":"
					+ (int) ((animationTime / 10) % 100), (int) gBuf
					.getClipBounds().getMinX() + 200, (int) gBuf.getClipBounds()
					.getMinY() + 10);*/
		}
		g.drawImage(buffer,0,0,this);
	}

	/**
	 * returns the time remaining for the animation phase
	 * 
	 * @return long the animation time
	 */
	public long getAnimationTime() {
		return animationTime;
	}

	/**
	 * sets the time remaining for the animation phase
	 * 
	 * @param animationTime
	 *            the new time remaining
	 */
	public void setAnimationTime(long animationTime) {
		this.animationTime = animationTime;
	}

	/**Adds a message to the screen at specific location*/
	public void addMessage(String in, Point2D loc) {
		messages.add(new Message(in, loc));
	}
	/**same as addMessage but for only a specified time*/
	public void addMessage(String in, Point2D loc, long time) {
		messages.add(new Message(in, loc, System.currentTimeMillis() + time));
	}
	/**Class to keep track of messages*/
	class Message {
		private long endTime;

		private String message;

		private Point2D loc;
		/**makes a new message to display*/
		Message(String msg, Point2D p) {
			this(msg, p, System.currentTimeMillis() + 1000);
		}
		/**makes a new message to display for a specified time*/
		Message(String msg, Point2D p, long end) {
			endTime = end;
			message = msg;
			loc = p;
		}
		/**calculates if the message should be removed*/
		public boolean isOver() {
			return System.currentTimeMillis() > endTime;
		}
		/**paints this message to the screen*/
		public void paint(Graphics g) {
			g.drawString(message, (int) loc.getX(), (int) loc.getY());
		}

	}
}
