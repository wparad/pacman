package game;

import game.actor.Actor;
import game.actor.Ghost;
import game.actor.Item;
import game.actor.Mine;
import game.actor.Pacman;
import game.actor.Token;
import game.map.Cell;
import game.map.PacmanMap;
import game.player.AILocal;
import game.player.AIPlayer;
import game.player.LocalPlayer;
import game.player.Player;
import gui.GUI;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * This is the 'Central' class that holds all the data and state for the game.
 * It holds on to the local and remote player, the game phase, the map, and the
 * various resource tables.
 */
public class Game implements Constants {

	// the player that controls the ghosts
	public final AIPlayer aiPlayer;

	double[] position = { 12 * CELL, 11 * CELL, 13 * CELL, 11 * CELL,
			14 * CELL, 11 * CELL, 15 * CELL, 11 * CELL };

	double pacpos[] = { 14 * CELL, 23 * CELL };

	// the player who has control of the GUI
	public final LocalPlayer localPlayer[] = new LocalPlayer[2];

	/**
	 * This is the graph you have to write.
	 */
	public final PacmanMap map;

	// the gui
	public final GUI gui;

	// number of localPlayers
	public int numPlayers;
	
	private boolean setBomb=false;

	public int deadPlayer = -1;

	// which phase of the game was are in
	private Phase phase;

	private Vector<Item> itemList = new Vector<Item>();

	private ArrayList<Mine> mineList = new ArrayList<Mine>();

	private Set<Token> tokenList = new HashSet<Token>();

	private Set<Actor> actorList = new HashSet<Actor>();

	private Timer vulnerableGhostTimer;

	private TimerTask currentGhostTimerTask;

	private final int extraLife = 10000;

	private int level = 1;

	private int tokensremaining = 0;

	private int ghostScore;

	private Random rand = new Random();

	/**
	 * Constructor for the game.
	 * 
	 * @param gui
	 *            reference to the GUI
	 */
	
	public Game(GUI gui) {
		this.gui = gui;

		map = new PacmanMap();
		Actor.setGame(this);

		/*
		 * add the token objects
		 */
		Cell[][] cells = map.getCellGrid();
		for (int y = 0; y < map.getTileHeight(); y++) {
			for (int x = 0; x < map.getTileWidth(); x++) {
				if (cells[x][y].isToken()) {
					char type = cells[x][y].type;
					Token t = new Token(new Point2D.Double(x * CELL, y * CELL),
							type == '*');
					tokenList.add(t);
				}
			}
		}

		// create the players
		localPlayer[0] = new LocalPlayer(this, pacmanImageNames, pacpos, false);
		localPlayer[1] = new LocalPlayer(this, missPacmanImageNames, pacpos, false);

		aiPlayer = new AIPlayer(this, map, position);

		actorList.addAll(aiPlayer.getActorList());

		phase = Phase.SPLASH_SCREEN;
	}
	
	/**Sets initial Position for ghosts*/
	public void setInit(double[] pos) {
		position = pos;
	}
	/**Sets initial Position for pacmen*/
	public void pacInit(double[] pos) {
		pacpos = pos;
	}
	/**Starts the game playing for normal player*/
	public void playGame(int players) {
		numPlayers = players;
		deadPlayer = -1;
		aiPlayer.setGhosts();
		for (int i = 0; i < numPlayers; i++) {
			if(i == 0)localPlayer[0] = new LocalPlayer(this, pacmanImageNames, pacpos, false);
			if(i == 1)localPlayer[1] = new LocalPlayer(this, missPacmanImageNames, pacpos, false);
			actorList.add(localPlayer[i].getPacman());
			localPlayer[i].setLives(2);
			localPlayer[i].setScore(0);
		}
		tokensremaining = 0;
		for (Token a : tokenList) {
			a.reset();
			tokensremaining++;
		}
		phase = Phase.PLAY_GAME;
	}
	/**Starts the game playing for AI pacman*/
	public void playAIGame(int players) {
		numPlayers = players;
		deadPlayer = -1;
		aiPlayer.setAIGhosts();
		for (int i = 0; i < numPlayers; i++) {
			if(i == 0)	localPlayer[i] = new AILocal(this, pacmanImageNames, pacpos);
			if(i == 1)	localPlayer[i] = new AILocal(this, missPacmanImageNames, pacpos);
			actorList.add(localPlayer[i].getPacman());
			localPlayer[i].setLives(2);
			localPlayer[i].setScore(0);
		}
		new AILocal(this,tokenList);
		if(localPlayer[0] instanceof AILocal)
			((AILocal) localPlayer[0]).start();
		
		tokensremaining = 0;
		for (Token a : tokenList) {
			a.reset();
			tokensremaining++;
		}
		phase = Phase.PLAY_GAME;
	}

	/**
	 * Update the location of all of players' units based on dt amount of time
	 * passing.
	 * 
	 * @param player
	 *            the player where we getUnits() from
	 * @param dt
	 *            the amount of times (in seconds) that we need to move the unit
	 *            over. Actor speeds are given in pixels per second, so dt*speed
	 *            is the movement potential
	 */
	public void updateUnits(Player player, double dt) {
		player.move(dt);
	}

	/**returns the current level pacman is on*/
	public int getLevel() {
		return level;
	}

	/**
	 * draw all the tokens to the screen
	 * 
	 * @param g
	 */
	public void drawTokens(Graphics g) {
		for (Token t : tokenList) {
			t.draw(g);
		}
	}

	/**
	 * draw all the items to the screen
	 * 
	 * @param g
	 */
	public void drawItems(Graphics g) {
		for (Item t : itemList) {
			t.draw(g);
		}
	}
	/**Draws all the current mines to the screen that have spawned*/
	public void drawMines(Graphics g) {
		for (Mine m : new ArrayList<Mine>(mineList)) {
			m.draw(g);
		}
	}

	/**removes a mine when pacman picks it up*/
	public void removeMine(Mine m) {
		mineList.remove(m);
	}

	/**Adds an item to pacmans list when it is picked up*/
	public void addItem(String name, int x, int y) {
		itemList.add(new Item(name, new Point(x, y)));
	}

	/**
	 * check if pacman has hit any of the ghosts or tokens. Take the neccessary
	 * action depending on what was hit
	 * 
	 */
	public void checkCollisions() {
		for (int i = 0; i < numPlayers; i++) {
			if (i != deadPlayer) {
				Pacman p = localPlayer[i].getPacman();

				Iterable<Ghost> ghosts = aiPlayer.getActorList();

				for (Ghost g : ghosts) {
					if (p.collidesWith(g)) {
						// we hit a ghost - die if the ghost is active
						if (g.isActive()) {
							gui.die.playAndWait();

							if (localPlayer[i].loseLife()) {
								deadPlayer = i;
							}

							restart();
						} else if (g.isVulnerable() || g.isRecovering()) { // else
							// send
							// it to the
							// corral
							gui.eatingGhosts.play();
							localPlayer[i].addPoints(ghostScore);
							gui.showPoints(ghostScore, p.getCurrentPosition());
							ghostScore *= 2;
							g.sendToCorral();
						}
					}
					for (int k = 0; k < mineList.size(); k++) {
						Mine it = mineList.get(k);
						if (g.collidesWith(it) && it.isArmed()) {
							gui.explosion.play();
							gui.eatingGhosts.play();
							g.sendToCorral();
							it.explode(this);
						}
					}
				}
				// eat the toekn
				boolean win = false;
				for (Token t : tokenList) {
					if (p.collidesWith(t)) {
						// is a super token-> make the ghosts vulnerable
						if (t.isSuperToken()) {
							ghostScore = 200;
							for (Ghost g : ghosts) {
								g.makeVulnerable();
							}
							vulnerableGhostTimer = new Timer(true);
							if (currentGhostTimerTask != null) {
								currentGhostTimerTask.cancel();
							}
							currentGhostTimerTask = new RecoveringTimerTask();
							vulnerableGhostTimer.schedule(
									currentGhostTimerTask, 7000);
							localPlayer[i].addPoints(50);
							tokensremaining--;
						} else if (t.isVisible()) {
							localPlayer[i].addPoints(10);

							tokensremaining--;
						}
						t.eat();
						if (tokensremaining == 0)
							win = true;

					}
				}
				if (win) {
					win = false;
					isWinner();
				}
				for (int k = 0; k < itemList.size(); k++) {

					Item it = itemList.get(k);

					if (p.collidesWith(it)) {
						String name = it.getName();
						if (name.equals("cherry")) {
							localPlayer[i].addPoints(100);
							gui.showPoints(100, p.getCurrentPosition());
						} else if (name.equals("peach")) {
							localPlayer[i].addPoints(500);
							gui.showPoints(500, p.getCurrentPosition());
						} else if (name.equals("strawberry")) {
							localPlayer[i].addPoints(300);
							gui.showPoints(300, p.getCurrentPosition());
						} else if (name.equals("mellon")) {
							localPlayer[i].addPoints(1000);
							gui.showPoints(1000, p.getCurrentPosition());
						} else if (name.equals("apple")) {
							localPlayer[i].addPoints(700);
							gui.showPoints(700, p.getCurrentPosition());
						} else if (name.equals("ship")) {
							localPlayer[i].addPoints(2000);
							gui.showPoints(2000, p.getCurrentPosition());
						} else if (name.equals("bell")) {
							localPlayer[i].addPoints(3000);
							gui.showPoints(3000, p.getCurrentPosition());
						} else if (name.equals("key")) {
							localPlayer[i].addPoints(5000);
							gui.showPoints(5000, p.getCurrentPosition());
						} else if (name.equals("icecube")) {
							new SpeedThreadGhost(aiPlayer.getActorList(),0).start();
						} else if (name.equals("speedup")) {
							new SpeedThreadPac(p, 2).start();
						} else if (name.equals("speeddown")) {
							new SpeedThreadPac(p, .9).start();
						} else if (name.equals("gspeedup")) {
							new SpeedThreadGhost( aiPlayer.getActorList(),
									1.1).start();
						} else if (name.equals("gspeeddown")) {
							new SpeedThreadGhost( aiPlayer.getActorList(),
									.6).start();
						} else if (name.equals("bomb")) {
							if(!setBomb){
								gui.setBomb.play();
							}
							setBomb=true;
							p.setItem("bomb", 60 + 300 * i,
									gui.bg.getHeight() - 60);
						}
						gui.eatItem.play();
						itemList.remove(k--);
					}
				}
				for (int k = 0; k < mineList.size(); k++) {
					Mine it = mineList.get(k);
					if (p.collidesWith(it)) {
						if (it.isArmed()) {
							gui.explosion.play();
							gui.die.playAndWait();

							if (localPlayer[i].loseLife()) {
								deadPlayer = i;
							}

							restart();
						}
					} else if (!it.isIgnited()&&it.isPlayer(i)) {
						it.arm();
					}
				}
			}
			if (localPlayer[i].extralife()
					&& localPlayer[i].getScore() > extraLife) {
				localPlayer[i].gainLife();
				gui.extraLife.play();
			}
		}
	}

	/**
	 * TimerTask that is fired when all the ghosts should be put into Recovering
	 * mode. Also schedules the timer to make the ghosts active again
	 * 
	 * 
	 */
	class RecoveringTimerTask extends TimerTask {
		public void run() {
			for (Ghost g : aiPlayer.getActorList()) {
				g.makeRecovering();
			}
			currentGhostTimerTask = new ActiveTimerTask();
			vulnerableGhostTimer.schedule(currentGhostTimerTask, 3000);
		}
	}

	/**
	 * TimerTake that is fired when all the ghosts should be put into Active
	 * mode
	 * 
	 * 
	 */
	class ActiveTimerTask extends TimerTask {
		public void run() {
			for (Ghost g : aiPlayer.getActorList()) {
				g.makeActive();
			}
		}
	}

	/**
	 * Restarts the game back to the way it was at the start
	 * 
	 */
	public void restart() {
		gui.stopAnimation();
		for (Actor a : actorList)
			a.reset();
		itemList.clear();
		mineList.clear();
		gui.repaint();
		if (localPlayer[0].getLives() >= 0 || localPlayer[1].getLives() >= 0) {
			gui.callLevel();
			new Timer().schedule(new TimerTask() {
				public void run() {
					gui.intermission.playAndWait();
					gui.restartAnimation();
				}
			}, 1000);
		} else {
			gui.endGame();
		}
	}
	/**Spawns Items on the map for pacman to collect*/
	public void spawnItem(double rate) {
		if (rate > 0&&itemList.size()<100) {
			for (int i = 0; i < itemProbs.length; i++) {
				int gen = (rand.nextInt((int)(10000/rate)));
				if (gen <= itemProbs[i]) {
					int xLoc = rand.nextInt(map.getTileWidth());
					int yLoc = rand.nextInt(map.getTileHeight());
					if (map.isTraversable(xLoc, yLoc, false)) {
						addItem(itemNames[i], xLoc * CELL, yLoc * CELL);
					}
				}
			}
		}
	}
	/**lays a mine on the ground*/
	public void putMine(Point2D p,int in) {
		mineList.add(new Mine(p,in));
	}

	/**
	 * Check if we have won
	 */
	public void isWinner() {
		gui.stopAnimation();
		level++;
		for (Actor a : actorList) {
			a.reset();
			itemList.clear();
			mineList.clear();
			if (a instanceof Ghost) {
				double newspeed = a.getOSpeed() * 1.015;
				a.setSpeed(newspeed, newspeed);
			}
		}
		String nextMap;
		if (level < 5) {
			nextMap = "level1.txt";
		} else {
			nextMap = "level2.txt";
		}
		map.setColor(level);
		map.loadMap(nextMap);
		tokenList.clear();
		Cell[][] cells = map.getCellGrid();
		for (int y = 0; y < map.getTileHeight(); y++) {
			for (int x = 0; x < map.getTileWidth(); x++) {
				if (cells[x][y].isToken()) {
					char type = cells[x][y].type;
					Token t = new Token(new Point2D.Double(x * CELL, y * CELL),
							type == '*');
					tokenList.add(t);
				}
			}
		}
		gui.callLevel();
		new Timer().schedule(new TimerTask() {
			public void run() {
				gui.intermission.playAndWait();
				tokensremaining = 0;
				for (Token a : tokenList) {
					a.reset();
					tokensremaining++;
				}
				gui.restartAnimation();

			}
		}, 1000);

	}

	/**
	 * @return the Phase that this game is in
	 */
	public Phase getPhase() {
		return phase;
	}

	/**
	 * return the Map used by this game
	 * 
	 * @return
	 */
	public PacmanMap getMap() {
		return map;
	}

}

/**a class for changing speeds of ghosts*/
class SpeedThreadGhost extends Thread {
	static SpeedThreadGhost current;

	Iterable<Ghost> targets;

	double factor;

	SpeedThreadGhost(Iterable<Ghost> t, double f) {
		targets = t;
		factor = f;
	}
	/**changes Ghosts speeds for 5 seconds when item is used*/
	public void run() {
		if (current != null)
			current.interrupt();
		current = this;
		for (Actor g : targets) {
			g.setSpeed(g.getOSpeed() * factor, -1);
		}
		try {
			sleep(5000);
		} catch (InterruptedException e) {
			return;
		}
		for (Actor g : targets) {
			if (g.getOSpeed() * factor == g.getSpeed()) {
				g.setSpeed(g.getOSpeed(), -1);
			}
		}
	}
}
/**a class for changing speeds of pacman*/
class SpeedThreadPac extends Thread {
	static SpeedThreadPac current;

	Actor target;

	double factor;

	SpeedThreadPac(Actor t, double f) {
		target = t;
		factor = f;
	}
	/**start the speed changing*/
	public void run() {
		if (current != null)
			current.interrupt();
		current = this;

		target.setSpeed(target.getOSpeed() * factor, -1);

		try {
			sleep(5000);
		} catch (InterruptedException e) {
			return;
		}

		if (target.getOSpeed() * factor == target.getSpeed()) {
			target.setSpeed(target.getOSpeed(), -1);
		}
	}

}