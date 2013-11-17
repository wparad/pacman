package game.actor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * Class to represent the little tokens that get eaten by pacman
 * 
 */
public class Token extends Actor {
	// should we draw the token?
	private boolean isVisible = true;

	// is this a super, invincibility giving token?
	private boolean isSuperToken;

	/**
	 * create a new token
	 * 
	 * @param initialLocation
	 * @param isSuperToken
	 */
	public Token(Point2D initialLocation, boolean isSuperToken) {
		super("token", 0, initialLocation, tokenImageNames);
		this.isSuperToken = isSuperToken;
	}

	/**
	 * eat the token is we haven't already (make it Not visible) plays the
	 * annoying sound if the token is eaten
	 */
	public void eat() {
		if (isVisible) {
			if (isSuperToken())
				game.gui.eatBigDot.play();
			else
				game.gui.eatDot.play();
		}
		isVisible = false;
	}

	/**
	 * resets the token to being visible again
	 */
	public void reset() {
		isVisible = true;
	}

	/**
	 * returns true if the token is visible and a super token
	 * 
	 * @return
	 */
	public boolean isSuperToken() {
		return isVisible && isSuperToken;
	}

	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * using graphics g, draw the token to the screen
	 */
	public void draw(Graphics g) {
		if (isVisible) {
			int x = (int) getCurrentPosition().getX() / CELL;
			int y = (int) getCurrentPosition().getY() / CELL;
			g.setColor(Color.WHITE);
			if (isSuperToken) {
				g.fillOval(x * CELL + CELL / 2 - 7, y * CELL + CELL / 2 - 7,
						13, 13);
			} else {
				g.fillRect(x * CELL + CELL / 2 - 1, y * CELL + CELL / 2 - 1, 3,
						3);
			}
		}
	}

	/**
	 * returns the bouding box around the token to use for hit detection
	 */
	public Rectangle getBoundingBox() {
		return new Rectangle((int) getCurrentPosition().getX() + CELL / 4,
				(int) getCurrentPosition().getY() + CELL / 4, CELL / 4,
				CELL / 4);
	}
}
