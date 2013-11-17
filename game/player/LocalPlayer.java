package game.player;

import game.Constants;
import game.Game;
import game.actor.MovementDirection;
import game.actor.Pacman;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

/**
 * Class that extends Player to provide added functionality for the player
 * controlling the Pacman from the keyboard
 * 
 */
public class LocalPlayer extends Player implements KeyListener, Constants {

	private Pacman pacman;

	private int score = -1;

	private int lives = -1;

	private boolean oneup = false;

	private boolean ai = false;

	/**
	 * Constructs a new LocalPlayer.
	 * 
	 * @param game
	 *            a reference to the Game object
	 */
	public LocalPlayer(Game game, String[][] imagePaths, double[] pos,
			boolean ai) {
		super(game);
		this.ai = ai;
		pacman = new Pacman(140, new Point2D.Double(pos[0], pos[1]), imagePaths);
	}

	/**Contructor to allow for extra field manipulation*/
	LocalPlayer(Game game) {
		super(game);
	}
	/**returns the pacman held by this player*/
	public Pacman getPacman() {
		return pacman;
	}
	/**returns if player has received extra life*/
	public boolean extralife() {
		return !oneup;
	}
	/**returns if this player is an AI*/
	public boolean isAI() {
		return ai;
	}
	/**gets the score of this player*/
	public int getScore() {
		return score;
	}
	/**returns lives remaining*/
	public int getLives() {
		return lives;
	}
	/**sets the lives of this player*/
	public void setLives(int in) {
		lives = in;
	}
	/**player loses 1 life */
	public boolean loseLife() {
		return --lives < 0;
	}
	/**player gains a life*/
	public void gainLife() {
		lives++;
		oneup = true;
	}
	/**add points to this player's score*/
	public void addPoints(int in) {
		score += in;
	}
	/**set the score ofthis player*/
	public void setScore(int in) {
		score = in;
	}
	/**move this player's pacman*/
	public void move(double dt) {
		pacman.move(dt);
	}

	/**
	 * This code is called when a key event is generated because the user
	 * pressed a key
	 */
	public void keyPressed(KeyEvent k) {
		if (!ai) {
			switch (k.getKeyCode()) {
			case (KeyEvent.VK_KP_DOWN):
			case (KeyEvent.VK_DOWN):
				game.localPlayer[0].getPacman().addDirection(
						MovementDirection.SOUTH);
				break;
			case (KeyEvent.VK_KP_UP):
			case (KeyEvent.VK_UP):
				game.localPlayer[0].getPacman().addDirection(
						MovementDirection.NORTH);
				break;
			case (KeyEvent.VK_KP_RIGHT):
			case (KeyEvent.VK_RIGHT):
				game.localPlayer[0].getPacman().addDirection(
						MovementDirection.EAST);
				break;
			case (KeyEvent.VK_KP_LEFT):
			case (KeyEvent.VK_LEFT):
				game.localPlayer[0].getPacman().addDirection(
						MovementDirection.WEST);
				break;
			case (KeyEvent.VK_SPACE):
				if (game.localPlayer[0].getPacman().getItem() != null) {
					if (game.localPlayer[0].getPacman().getItem().getName()
							.equals("bomb")) {
						game.putMine(game.localPlayer[0].getPacman()
								.getCurrentPosition(), 0);
						game.localPlayer[0].getPacman().removeItem();
					}
				}
				break;
			case (KeyEvent.VK_S):
				game.localPlayer[1].pacman
						.addDirection(MovementDirection.SOUTH);
				break;
			case (KeyEvent.VK_W):
				game.localPlayer[1].pacman
						.addDirection(MovementDirection.NORTH);
				break;
			case (KeyEvent.VK_D):
				game.localPlayer[1].pacman.addDirection(MovementDirection.EAST);
				break;
			case (KeyEvent.VK_A):
				game.localPlayer[1].pacman.addDirection(MovementDirection.WEST);
				break;
			case (KeyEvent.VK_SHIFT):
				if (game.localPlayer[1].getPacman().getItem() != null) {
					if (game.localPlayer[1].getPacman().getItem().getName()
							.equals("bomb")) {
						game.putMine(game.localPlayer[1].getPacman()
								.getCurrentPosition(), 1);
						game.localPlayer[1].getPacman().removeItem();
					}
				}
				break;
			}
		}
		switch (k.getKeyCode()) {
		case (KeyEvent.VK_P):
			game.gui.pause();
			break;
		case (KeyEvent.VK_M):
			game.gui.setSound();
			break;

		case (KeyEvent.VK_ESCAPE):
			game.localPlayer[0].setLives(-1);
			game.localPlayer[1].setLives(-1);
			game.restart();
			break;
		}
	}
	/**does nothing*/
	public void keyTyped(KeyEvent k) {
	}
	/**does nothing*/
	public void keyReleased(KeyEvent k) {
	}
}
