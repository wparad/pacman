package gui;

import game.Constants;
import game.Game;
import game.player.AILocal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;

/**
 * Class to create the GUI for the Game
 */
public class GUI extends JFrame implements Constants, ActionListener {

	private static final long serialVersionUID = 4322838670943725248L;

	private Animator animator = new Animator();

	private final Game game;

	public JFrame frame = this;

	private final JButton singleStart = new JButton("Single Player");

	private final JButton multiStart = new JButton("Multi Start");

	private final JButton autoplay = new JButton("Auto Play");
	
	private final JButton options = new JButton("Controls");

	private final JTextField enterName = new JTextField("Enter Name");

	private final JLabel scoreLabel = new JLabel();

	private final JLabel gameOver = new JLabel("Game Over");

	private final JRadioButton sound = new JRadioButton("Sound");

	private final MyCanvas info = new MyCanvas();

	public final Background bg;

	private Font bigPacFont, smallPacFont;

	private final JPanel splashScreen = new JPanel(null);

	private final JPanel scoreScreen = new JPanel(null);

	private ArrayList<Score> scores = new ArrayList<Score>();

	public final Sound openingSong = new Sound("openingSong");

	public final Sound eatDot = new Sound("eatdot");

	public final Sound eatBigDot = new Sound("eatBigDot2");

	public final Sound die = new Sound("die");

	public final Sound eatingGhosts = new Sound("eatingGhosts");

	public final Sound intermission = new Sound("intermission");

	public final Sound loseSound = new Sound("gameover");

	public final Sound extraLife = new Sound("extralife");

	public final Sound explosion = new Sound("explosion");
	
	private ArrayList<Clip> nowPlaying=new ArrayList<Clip>();

	public final Sound setBomb = new Sound("setbomb");

	public final Sound eatItem = new Sound("eatitem");

	public final JMenuBar colorChooser = new JMenuBar();

	private final JLabel dropLabel = new JLabel("Item Rate");

	public final JSpinner dropRate = new JSpinner(new SpinnerNumberModel(1, 0,
			2, .1));

	public final JButton basePic = new JButton(new ImageIcon(iconDirectory
			+ "allyourbase.png"));

	private static boolean silent = false; // silent mode

	private int checkPlace;

	private int checkPlayer;

	private JLabel label = new JLabel("GET READY");

	/**Starts the game playing*/
	public static void main(String[] args) {
		silent = args.length > 0 && args[0].equals("-s");
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
		}
		new GUI();
	}
	/**Constructs the holder for the game and runs it*/
	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("CS211 Pacman");

		game = new Game(this);
		bg = new Background(game);

		FileInputStream con;
		try {

			con = new FileInputStream(fontDirectory + "Pacmania.ttf");

			bigPacFont = Font.createFont(Font.TRUETYPE_FONT, con);
			con.close();
			bigPacFont = bigPacFont.deriveFont(Font.PLAIN, 60);
			smallPacFont = bigPacFont.deriveFont(Font.PLAIN, 30);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		loadScores();

		setSize(400, 600);
		setResizable(false);

		// layout components

		JLabel title = new JLabel("PacMan");
		title.setFont(bigPacFont);
		title.setBounds(60, 10, 300, 60);
		splashScreen.add(title);

		JMenu colorMenu = new JMenu("Color Scheme");
		ButtonGroup colorGroup = new ButtonGroup();
		ColorRadioButton c = new ColorRadioButton("Default", new Color(
				Color.yellow.getRGB()), new Color(0, 0, 0));
		c.setSelected(true);
		colorGroup.add(c);
		colorGroup.add(new ColorRadioButton("White/Black", Color.white,
				Color.black));
		colorGroup.add(new ColorRadioButton("Spruce Forest", new Color(0, 45,
				45), new Color(32, 196, 112)));
		colorGroup.add(new ColorRadioButton("Ocean Breeze", new Color(72, 200,
				200), new Color(0, 20, 86)));
		colorGroup.add(new ColorRadioButton("681 Web Site", new Color(0x33,
				0x33, 0x55), new Color(0xFF, 0xFF, 0x99)));
		colorGroup.add(new ColorRadioButton("682 Web Site", new Color(0xCC,
				0xCC, 0x99), new Color(0x99, 0x00, 0x00)));
		colorGroup.add(new ColorRadioButton("Cornell",
				new Color(255, 255, 255), new Color(179, 27, 27)));
		colorGroup.add(new ColorRadioButton("Custom...", Color.lightGray,
				Color.lightGray));
		Enumeration<AbstractButton> e = colorGroup.getElements();
		while (e.hasMoreElements()) {
			colorMenu.add(e.nextElement());
		}

		colorChooser.add(colorMenu);

		colorChooser.setBounds(165, 500, (int) colorChooser.getPreferredSize()
				.getWidth(), (int) colorChooser.getPreferredSize().getHeight());
		splashScreen.add(colorChooser);

		sound.setBounds(20, 400, (int) sound.getPreferredSize().getWidth(),
				(int) sound.getPreferredSize().getHeight());
		splashScreen.add(sound);

		dropLabel.setBounds(330, 380, (int) dropLabel.getPreferredSize()
				.getWidth(), (int) dropLabel.getPreferredSize().getHeight());
		splashScreen.add(dropLabel);

		dropRate
				.setBounds(330, 400, 50, (int) dropRate.getPreferredSize()
						.getHeight());
		splashScreen.add(dropRate);

		singleStart.addActionListener(this);
		singleStart.setBounds(50, 500, (int) singleStart.getPreferredSize()
				.getWidth(), (int) singleStart.getPreferredSize().getHeight());
		splashScreen.add(singleStart);

		autoplay.setBounds(58, 530, (int) autoplay.getPreferredSize()
				.getWidth(), (int) autoplay.getPreferredSize().getHeight());
		autoplay.addActionListener(this);
		splashScreen.add(autoplay);
		
		
		
		multiStart.setBounds(260, 500, (int) multiStart.getPreferredSize()
				.getWidth(), (int) multiStart.getPreferredSize().getHeight());
		multiStart.addActionListener(this);
		splashScreen.add(multiStart);

		options.setBounds(150, 450,
				100, (int) options
						.getPreferredSize().getHeight());
		options.addActionListener(this);
		splashScreen.add(options);

		info.setBounds(100, 100, 200, 300);
		splashScreen.add(info);

		add(splashScreen);

		singleStart.setEnabled(true);

		gameOver.setFont(bigPacFont);
		gameOver.setBounds(0, 100, 400, 60);
		scoreScreen.add(gameOver);

		// scoreScreen.setBackground(colorSet.getBack());
		scoreLabel.setBounds(150, 200, 200, 30);
		scoreScreen.add(scoreLabel);

		enterName.setBounds(150, 300, 100, 30);
		enterName.addActionListener(this);
		scoreScreen.add(enterName);

		label.setBounds(215, 325, 300, 50);
		label.setFont(smallPacFont);

		basePic.setBounds(50, 200, (int) basePic.getPreferredSize().getWidth(),
				(int) basePic.getPreferredSize().getHeight());
		basePic.setDisabledIcon(basePic.getIcon());
		basePic.setEnabled(false);

		scoreScreen.add(basePic);

		setColors(Color.yellow, Color.black);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2,
				(screenSize.height - getHeight()) / 2);
		setVisible(true);
	}
	/**Updates the locations of all the players in the game*/
	public void updateLocations(double dt) {
		for (int i = 0; i < game.numPlayers; i++) {
			if (i != game.deadPlayer) {
				game.updateUnits(game.localPlayer[i], dt);
			}
		}
		game.updateUnits(game.aiPlayer, dt);
	}

	/**Stops the animator from updating the screen and playing*/
	public void stopAnimation() {
		animator.stopAnimation();
	}
	/**Starts the animator playing from stopped*/
	public void restartAnimation() {
		synchronized (animator) {
			animator.notify();
		}
	}
	/**a class to keep track of scores*/
	class Score {
		int score;

		String name;

		Score(String n, int s) {
			score = s;
			name = n;
		}

	}
	/**Load the scores from a file into the game*/
	public void loadScores() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					scoreDirectory + "score.txt"));
			String check = in.readLine();
			while (check != null) {
				if (check.indexOf(scoreSeparator) == -1) {
					throw new IOException();
				}
				int score = Integer.parseInt(check.substring(check
						.indexOf(scoreSeparator) + 1));
				scores.add(new Score(check.substring(0, check
						.indexOf(scoreSeparator)), score));
				check = in.readLine();
			}
			in.close();
			return;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		System.out.println("no score file found or invalid will start new one");
	}
	
	/**sets if sound is on*/
	public void setSound() {
		stopAll();
		sound.setSelected(!sound.isSelected());
	}
	/**displays the points on the screen*/
	public void showPoints(int points, Point2D loc) {
		bg.addMessage("" + points, loc);
	}
	/**checks what do when one of the splashscreen buttons is pressed:
	 * starts single/multiplayer or shows options*/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(singleStart)) {
			remove(splashScreen);
			add(bg);
			pack();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((screenSize.width - getWidth()) / 2,
					(screenSize.height - getHeight()) / 2);
			callLevel();
			new Thread() {
				public void run() {
					game.playGame(1);
					addKeyListener(game.localPlayer[0]);
					openingSong.playAndWait();
					animator.start();
				}
			}.start();
			GUI.this.requestFocusInWindow();
		}else if(e.getSource().equals(autoplay)){
			this.dropRate.setValue(new Integer(0));
			remove(splashScreen);
			add(bg);
			pack();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((screenSize.width - getWidth()) / 2,
					(screenSize.height - getHeight()) / 2);
			callLevel();
			new Thread() {
				public void run() {
					game.playAIGame(1);
					addKeyListener(game.localPlayer[0]);
					openingSong.playAndWait();
					animator.start();
				}
			}.start();
			GUI.this.requestFocusInWindow();
		}	
		
		else if (e.getSource().equals(multiStart)) {
			remove(splashScreen);
			add(bg);
			pack();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((screenSize.width - getWidth()) / 2,
					(screenSize.height - getHeight()) / 2);
			callLevel();
			new Thread() {
				public void run() {
					game.playGame(2);
					addKeyListener(game.localPlayer[0]);
					openingSong.playAndWait();
					animator.start();
				}
			}.start();
			GUI.this.requestFocusInWindow();
		} else if (e.getSource().equals(enterName)) {

			if (checkPlace == scores.size() - 1) {
				if (scores.size() < 10) {
					scores.add(scores.set(checkPlace, new Score(enterName
							.getText(), game.localPlayer[checkPlayer]
							.getScore())));
				} else {
					scores.set(checkPlace, new Score(enterName.getText(),
							game.localPlayer[checkPlayer].getScore()));
				}
			} else {
				Score temp = scores.set(checkPlace, new Score(enterName
						.getText(), game.localPlayer[checkPlayer].getScore()));
				for (int i = checkPlace + 1; i < scores.size(); i++) {
					temp = scores.set(i, temp);
				}
				if (scores.size() < 10) {
					scores.add(temp);
				}
			}

			try {
				PrintWriter out = new PrintWriter(new FileWriter(scoreDirectory
						+ "score.txt"));
				for (Score score : scores) {
					out.println(score.name + scoreSeparator + score.score);
				}
				out.flush();
				out.close();
			} catch (IOException er) {
				er.printStackTrace();
			}
			if (checkPlayer < game.numPlayers - 1) {
				checkScore(checkPlayer + 1);
				return;
			}
			remove(scoreScreen);
			add(splashScreen);
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation((screenSize.width - getWidth()) / 2,
					(screenSize.height - getHeight()) / 2);
			repaint();
		} else if (e.getSource().equals(options)) {
			if(options.getText().equals("High Scores")){
				options.setText("Controls");
				info.setMode(0);
			}
			else if(options.getText().equals("Controls")){
				options.setText("High Scores");
				info.setMode(1);
			}
		}
		repaint();
	}
	
	/**Sets the colors of the game*/
	public void setColors(Color f, Color b) {
		splashScreen.setBackground(b);
		for (Component c : splashScreen.getComponents()) {
			c.setBackground(b);
			c.setForeground(f);
		}
		
		for (Component c : colorChooser.getComponents()) {
			c.setBackground(b);
			c.setForeground(f);
		}
		scoreScreen.setBackground(b);
		for (Component c : scoreScreen.getComponents()) {
			c.setBackground(b);
			c.setForeground(f);
		}
		label.setBackground(b);
		label.setForeground(f);

	}

	/**Pauses the game*/
	public void pause() {
		if (animator.isRunning()) {
			animator.stopAnimation();
		} else {
			GUI.this.requestFocusInWindow();
			restartAnimation();
		}
	}

	/**Ends the game*/
	public void endGame() {
		stopAnimation();
		restartAnimation();
		animator = new Animator();
		if(game.localPlayer[0] instanceof AILocal)
			dropRate.setValue(1);
		remove(bg);
		removeKeyListener(game.localPlayer[0]);
		setSize(400, 600);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2,
				(screenSize.height - getHeight()) / 2);
		enterName.setVisible(false);
		add(scoreScreen);
		new Thread() {
			public void run(){
				loseSound.playAndWait();
				checkScore(0);
			}
		}.start();
		repaint();
	}

	/**Calculates posible positions of 1st and second player*/
	public void checkScore(int player) {
		int place;
		for (int i = 0; i < game.numPlayers; i++) {
			for (place = 0; place < scores.size()
					&& game.localPlayer[player].getScore() < scores.get(place).score; place++) {
			}
			if (place < 10) {
				enterName.setVisible(true);
				scoreLabel.setText("Player " + (player + 1) + " got "
						+ (place + 1) + " place!");
				checkPlace = place;
				checkPlayer = player;
				return;
			}
		}
		remove(scoreScreen);
		animator = new Animator();
		add(splashScreen);
		repaint();
	}

	/**
	 * Class for running Animations.
	 */
	class Animator extends Thread {
		final long FRAME_TIME = 60; // min # of milliseconds/frame

		final double DT_MAX = .03; // max frame latency

		boolean isRunning = false; // Is the simulator running?

		// start the simulation
		public synchronized void run() {
			double dt = 0; // delta time (change in time from frame to frame)
			long nextFrame = 0; // when the next frame should occur
			long lastFrame = System.currentTimeMillis(); // the time of the
			// last frame
			long firstFrame = lastFrame;

			nextFrame = System.currentTimeMillis() + FRAME_TIME;
			isRunning = true; // Is the simulator running?
			long now = 0;

			// main game loop
			while (game.localPlayer[0].getLives() >= 0
					|| game.localPlayer[1].getLives() >= 0) {
				while (isRunning) {
					now = System.currentTimeMillis();

					// incremental update
					if (now > nextFrame) {
						// update the locations of everything
						updateLocations(FRAME_TIME / 1000.0);

						game.checkCollisions();

						game.spawnItem(Double.parseDouble(dropRate.getValue()
								.toString()));

						// redraw to the screen
						bg.repaint();

						// update the time
						lastFrame = now;
						nextFrame = now + FRAME_TIME;
					}

					// calculate the delta time and scale if running slowly
					dt = (double) (now - lastFrame) / 1000;
					if (dt > DT_MAX)
						dt = DT_MAX;
					bg.setAnimationTime(now - firstFrame);
				}

				// someone stopped us
				try {
					wait();
				} catch (InterruptedException ie) {
				}
				isRunning = true;
			}
		}

		public void stopAnimation() {
			isRunning = false;
		}

		public boolean isRunning() {
			return isRunning;
		}
	}
	
	public void stopAll(){
		for(int i=0;i<nowPlaying.size();i++){
			if(nowPlaying.get(i)==null||!nowPlaying.get(i).isRunning()){
				nowPlaying.remove(i);
				i--;
				continue;
			}
				nowPlaying.get(i).stop();
		}
	}

	/**
	 * Sounds
	 */
	public class Sound implements LineListener {
		private Clip clip;
		 
		Sound(String soundFileName) {
			InputStream sound = ClassLoader
					.getSystemResourceAsStream(soundDirectory + soundFileName
							+ ".wav");
			try {
				clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(sound));
				clip.addLineListener(this);
			} catch (Exception e) {
				clip = null;
			}
		}
		
		
		


		public void play() {
			if (silent || clip == null)
				return;
			if (sound.isSelected()) {
					for(int i=0;i<nowPlaying.size();i++){
						if(nowPlaying.get(i)==null||!nowPlaying.get(i).isRunning()){
							nowPlaying.remove(i);
							i--;
							continue;
						}
					}
				nowPlaying.add(clip);
				clip.setFramePosition(0);
				clip.loop(0);
			}
		}

		// wait the specified number of seconds, or until
		// the song is over, whichever comes first
		public synchronized void playAndWait() {
			play();
			try {
				wait(clip.getMicrosecondLength() / 1000);
			} catch (InterruptedException ie) {
			}
			nowPlaying.remove(clip);
		}

		// to implement the LineListener interface
		public synchronized void update(LineEvent le) {
			
		}
	}

	/**A class for add panels to the gui*/
	class MyCanvas extends JPanel {

		private static final long serialVersionUID = 1L;

		int mode;
		
		/**sets the type of panel*/
		public void setMode(int i) {
			mode = i;
		}
		/**paints this panel to the screen*/
		public void paint(Graphics g) {
			g.setColor(getForeground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(getBackground());
			switch (mode) {
			case 0:
				g.drawString("High Scores!", 60, 10);
				for (int i = 0; i < scores.size(); i++) {
					g.drawString(scores.get(i).name + ": "
							+ scores.get(i).score, 10, 40 + 25 * i);
				}
				break;
			case 1:
				g.drawString("Controls", 50, 10);
				g.drawString("Player 1:", 10, 40);
				g.drawString("up: up arrow", 10, 55);
				g.drawString("down: down arrow", 10, 70);
				g.drawString("left: left arrow", 10, 85);
				g.drawString("right: right arrow", 10, 100);
				g.drawString("item: space", 10, 115);
				
				g.drawString("Player 2:", 10, 145);
				g.drawString("up: w", 10, 160);
				g.drawString("down: s", 10, 175);
				g.drawString("left: a", 10, 190);
				g.drawString("right: d", 10, 205);
				g.drawString("item: shift", 10, 220);
				
				g.drawString("pause: p", 10, 250);
				g.drawString("mute on/off: m", 10, 265);
				g.drawString("quit current game: esc", 10, 280);
				break;
			}
		}

	}

	/**starts the level*/
	public void callLevel() {
		new levelStart(this).start();
	}
	/**A class to keep track of the level the players are running on*/
	class levelStart extends Thread {
		private GUI gui = null;

		private levelStart(GUI gui) {
			this.gui = gui;
		}
		/**runs this new level*/
		public void run() {
			label.setText("  LEVEL " + gui.game.getLevel());
			gui.bg.add(label);
			repaint();
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			label.setText("GET READY");
			repaint();
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			label.setText("   START");
			repaint();
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
			gui.bg.remove(label);
		}

	}

	private class ColorRadioButton extends JRadioButtonMenuItem implements
			ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		Color background;

		Color foreground;

		ColorRadioButton(String s, Color b, Color f) {
			super(s);
			background = b;
			foreground = f;
			setActionCommand(s);
			addActionListener(this);
			if (!s.equals("Custom...")) {
				setIcon(createIcon(b, f));
			}
		}

		Icon createIcon(Color background, Color foreground) {
			Image image = new BufferedImage(12, 12, BufferedImage.TYPE_INT_RGB);
			Graphics graphics = image.getGraphics();
			graphics.setColor(background);
			graphics.fillRect(0, 0, 12, 12);
			graphics.setColor(foreground);
			graphics.fillRect(5, 0, 2, 12);
			graphics.fillRect(0, 5, 12, 2);
			return new ImageIcon(image);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Custom...")) {

				Color bc = JColorChooser.showDialog(frame,
						"Choose Background Color", background);
				if (bc == null) {
					return;
				}
				Color fc = JColorChooser.showDialog(frame,
						"Choose Foreground Color", foreground);
				if (fc == null) {
					return;
				}
				background = bc;
				foreground = fc;
				setIcon(createIcon(bc, fc));
			}
			setColors(background, foreground);
		}

		public String toString() {
			return getActionCommand();
		}
	}
}
