import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Area;
import java.util.Random;

public class AsteroidFactory {

	// creates the new AsteroidFactory
	private final static AsteroidFactory instance = new AsteroidFactory();

	// creates the starting bounds
	private static Rectangle startBounds;

	private AsteroidFactory() {
	}

	/**
	* Gets the instance of AsteroidFactory
	* @return AsteroidFactory instance
	*/
	public static AsteroidFactory getInstance() {
		return instance;
	}

	/**
	* Determines the starting bounds of the asteroids
	* @param x - the x-position of the asteroid
	* @param minY - lowest position that the asteroid will start at
	* @param maxY - highest position that the asteroid will start at
	*/
	public void setStartBounds(int x, int minY, int maxY) {
		startBounds = new Rectangle(x, minY, x, maxY);
	}

	/**
	* Creates an asteroid
	* @param level - the game's level that will influence the asteroid size and speed
	* @return a new asteroid implementation with a random y position, height, width, and size
	*/

	public Asteroid makeAsteroid(int level) {
		int bonus = level - 1;
		return new AsteroidImpl(startBounds.x, random(startBounds.y, startBounds.height),
			random(10 + (bonus * 5), 40 + (bonus * 5)), random(10 + (bonus * 5), 40 + (bonus * 5)), random((1 + bonus), 4 + (int)(bonus * 1.5)));
	}

	/**
	* Generates a random number
	* @param min - minimum value that's returned
	* @param max - maximum value that's returned
	* @return a random number between min and max
	*/

	private static int random(int min, int max) {
		Random rand = java.util.concurrent.ThreadLocalRandom.current();
		return min + (int) (rand.nextDouble()*(max-min));
	}

	/**
	* Implements an Asteroid
	*/

	private static class AsteroidImpl implements Asteroid {
		// asteroid colour
		private final static Color COLOR = Color.DARK_GRAY;

		// asteroid elipse and speed
		private final Ellipse2D.Double shape;
		private final int speed;

		/**
		* Constructs an asteroid given the following parameters:
		* @param x - desired x position
		* @param y - desired y position
		* @param width - desired width
		* @param height - desired height
		* @param aPeed - desired speed
		*/

		private AsteroidImpl(int x, int y, int width, int height, int aSpeed) {
			shape = new Ellipse2D.Double(x, y, width, height);
			speed = aSpeed;
		}

		/**
		* Moves the asteroid
		*/

		public void move() {
			shape.x -= speed;
		}

		/**
		* Checks if an asteroid is visible
		* @return true if it is; false if not
		*/

		public boolean isVisible() {
			return shape.x >= (0 - shape.width);
		}

		/**
		* Draws the asteroids
		*/

		public void draw(Graphics2D g) {
			g.setColor(COLOR);
			g.fill(shape);
			g.draw(shape);
		}

		/**
		* Returns the shape (in this case, the asteroid)
		*/

		public Shape getShape() {
			return shape;
		}

		/**
		* Checks if two shapes intersect each other
		* @param the other objects
		* @return true if so; false if not
		*/

		public boolean intersects(Sprite other) {
			return shape.getBounds2D().intersects((other.getShape()).getBounds2D());
		}
	}
}
