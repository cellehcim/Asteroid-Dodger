/**
  Implements a game component for a Gradius-inspired game.
  @author Michelle Chan
  @version 07-05-2017 @ 1 PM
  @see ShipImpl.java; AsteroidFactory.java
*/

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.awt.image.BufferedImage;
import java.awt.geom.*;

public class GameComponent extends JComponent {

	static long score;
	static int asteroidsSurvived;

	// Comic Sans font because why not?
	private final static Font newFont = new Font("Comic Sans MS", Font.BOLD, 24);
	static {
		new Thread(() -> {
			BufferedImage img = new BufferedImage(10,10, BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g = img.createGraphics();
			g.setFont(newFont);
			g.drawString("GAME OVER!", 5,5);
		}).start();
	}

	// small UI fonts
	private final static Font smallFont = new Font("Monospace", Font.BOLD, 12);
	static {
		new Thread(() -> {
			BufferedImage img = new BufferedImage(10,10, BufferedImage.TYPE_BYTE_GRAY);
			Graphics2D g = img.createGraphics();
			g.setFont(smallFont);
			g.drawString(Long.toString(score), 5,5);
		}).start();
	}

	// instance variables for the timer and ship
	Timer[] t;
	Ship s;

	// holds the asteroids
	HashSet<Asteroid> asteroids;

	// key listeners
	ShipKeyListener skl;

	// ship health
	int health = 150;
	private final int maxHealth = health;

	// scoring + levels
	long start;
	int level = 1;

	// checks if the game is over
	boolean isGameOver;

	public GameComponent() {
		s = new ShipImpl(10, GameFrame.HEIGHT/3);;
		t = new Timer[2];
		t[0] = new Timer(1000/60, (a) -> {this.update();});
		t[1] = new Timer(1000/4, (a) -> {this.makeAsteroid();});
		start = System.currentTimeMillis();
		skl = new ShipKeyListener();
		addKeyListener(skl);
		asteroids = new HashSet<Asteroid>();
	}

	/**
	* Renders the drawing component
	*/

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintComponent(g2);
	}

	/**
	* Draws the graphics
	*/
	private void paintComponent(Graphics2D g) {
		if (isGameOver) {
			drawGameOver(g);
		} else {
			s.draw(g);

			for (Asteroid a : asteroids) {
				a.draw(g);
			}

			drawHealthBar(g);

			g.setFont(smallFont);
			g.drawString("Score: " + Long.toString(score) + "\tLevel: " + Integer.toString(level), 10, getHeight() - 20);
		}
	}

	/**
	* Updates the game, removes health upon collision, and ends the game if there's no ship health.
	* Also redraws everything in the component.
	*/

	private void update() {
		requestFocusInWindow();
		s.move();
		moveAsteroids();
		if (checkCollisions()) {
			health--;
			if (health == 0) {
				gameOver();
			} else {

			}
		}
		long now = System.currentTimeMillis();
		score = (now - start) / 1000;
		levelUp();
		this.repaint();
	}

	/**
	* Starts the game by starting the timers, determining movement/start bounds, and making the first asteroids
	*/

	public void start() {
		s.setMovementBounds(new Rectangle(0,0, getWidth(), getHeight()));
		AsteroidFactory a = AsteroidFactory.getInstance();
		a.setStartBounds(getWidth(), 0, getHeight());
		t[0].start();
		t[1].start();
		makeAsteroid();
	}

	/**
	* Stops the game.
	*/

	public void gameOver() {
		t[0].stop();
		t[1].stop();
		isGameOver = true;
		removeKeyListener(skl);
	}

	/**
	* Determines level-up critera and increases the level upon reaching a certain score.
	*/

	public void levelUp() {
		if (Math.pow(3, level + 2) == score) {
			level++;
		}
	}

	/**
	* Draws the Game Over screen
	*/

	public void drawGameOver(Graphics2D g) {
		// creates background
		Rectangle gameOverScreen = new Rectangle(0, 0, 900, 700);
		g.setColor(Color.BLACK);
		g.fill(gameOverScreen);
		g.draw(gameOverScreen);


		// configures font and colour
		g.setColor(Color.WHITE);
		g.setFont(newFont);

		// dumb flavour text
		g.drawString("Your ship exploded.", (getWidth() * 37) / 100, (getHeight() * 2 / 10));
		g.drawString("And no, there is no animated display of ship parts and fragments", getWidth() * 5 / 100, getHeight() * 35 / 100);
		g.drawString("of your body flying throughout the galaxy.", getWidth() * 2 / 10, getHeight() * 40 / 100);
		g.drawString("We do not reward failure.", getWidth() * 30 / 100, getHeight() * 45 / 100);
		g.drawString("GAME OVER!", (getWidth() * 40) / 100, (getHeight() * 60 / 100));

		// stats
		g.drawString("Score: " + score, (getWidth() * 37) / 100, getHeight() * 75 / 100);
		g.drawString("Highest Level Reached: " + level, (getWidth() * 28) / 100, getHeight() * 80 / 100);
		g.drawString("Asteroids survived: " + asteroidsSurvived, (getWidth() * 30) / 100, getHeight() * 85 / 100);

	}

	/**
	* Draws the ship health bar
	*/

	public void drawHealthBar(Graphics2D g) {
		int percent = (int)(((float) health / maxHealth) * 100);
		Rectangle bar = new Rectangle(10, 10, 100, 10);
		g.setColor(Color.GREEN);
		g.fillRect(10, 10, percent, 10);
		//g.setStroke(new BasicStroke(1f));
		g.draw(bar);
	}

	/**
	* Creates an asteroid and adds it to the array + game
	*/

	public void makeAsteroid() {
		asteroids.add(AsteroidFactory.getInstance().makeAsteroid(level));
	}

	/**
	* Moves asteroids and removes off-screen ones.
	* Also creates a hidden stat that tells the player post-game how many asteriods they've survived.
	*/

	public void moveAsteroids() {
		Iterator<Asteroid> it = asteroids.iterator();

		while (it.hasNext()) {
			Asteroid a = it.next();
			if (!a.isVisible()) {
				it.remove();
				asteroidsSurvived++;
			}
			a.move();
		}
	}

	/**
	* Checks
	*/

	public boolean checkCollisions() {
		for (Asteroid a : asteroids) {
			if (a.intersects(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	* Key listeners that will record directions and thus move the ship.
	* Recognizes arrow keys, number pads, and WASD.
	*/

	public class ShipKeyListener extends KeyAdapter {
		/**
		* Sets the direction given a certain key
		* @param e - the key event that will determine direction
		*/
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
				case KeyEvent.VK_NUMPAD8:
					s.setDirection(Ship.Direction.UP);
					break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
				case KeyEvent.VK_NUMPAD2:
					s.setDirection(Ship.Direction.DOWN);
					break;

				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
				case KeyEvent.VK_NUMPAD4:
					s.setDirection(Ship.Direction.LEFT);
					break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
				case KeyEvent.VK_NUMPAD6:
					s.setDirection(Ship.Direction.RIGHT);
					break;
			}
		}

		/**
		* Sets the direction to none if there's nothing pressed.
		*/

		public void keyReleased(KeyEvent e) {
			s.setDirection(Ship.Direction.NONE);
		}
	}
}
