import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
* Implements the ship
*/

public class ShipImpl implements Ship {

	// ship colours
	private final static Color FILL = Color.GREEN;
	private final static Color BORDER = Color.BLACK;

	// ship specs and dimensions
	private final static int HIGHEST_I = 0; // the array position of the top
	private final static int LOWEST_I = 1;  // the array position of the bottom
	private final static int FRONT_I = 2;
	private final static int HEIGHT = 20;
	private final static int WIDTH = HEIGHT;
	private final static int SPEED = 2;
	private final Polygon shape;

	// ship movement stuff
	private Direction d;
	private Rectangle2D movementBounds;

	/**
	* Constructs the ship
	* @param x - ship's x position
	* @param y - ship's y position
	*/
	public ShipImpl(int x, int y) {
		shape = new Polygon(
			new int[] {0,0,WIDTH}, //top left, bottom left, front middle
			new int[] {0,HEIGHT,HEIGHT/2}, 3);

		shape.translate(x, y);
		d = Direction.NONE;
	}

	/**
	* Sets the ship's Direction
	* @param d - direction of the ship
	*/

	public void setDirection(Direction d) {
		this.d = d;
	}

	/**
	* Sets the movement bounds and adjusts it accordingly depending on the height/width ratio.
	* @param movementBounds - the movement bounds for the ship
	*/

	public void setMovementBounds(Rectangle2D movementBounds) {
		// constructs the movement bounds
		this.movementBounds = movementBounds;

		// modifies the parameters to accomodate the moving ship
		this.movementBounds.setRect(
			movementBounds.getX() + WIDTH,
			movementBounds.getY() + HEIGHT,
			movementBounds.getMaxX() - (WIDTH * 2),
			movementBounds.getMaxY() - (HEIGHT * 2));
	}

	/**
	* Moves the ship while keeping it in the bounds.
	*/

	public void move() {
		// moves the ship
		shape.translate(SPEED * d.dx, SPEED * d.dy);

		// resets it if it isn't in the bounds
		if (!isInBounds()) {
			shape.translate(-SPEED * d.dx, -SPEED * d.dy);
		}
	}

	/**
	* Checks if the shape is in the bounds.
	* @return true if it is; false if not.
	*/

	private boolean isInBounds() {
		return shape.intersects(movementBounds);
	}

	/*
	* Draws the ship.
	*/

	public void draw(Graphics2D g) {
		// fills ship
		g.setColor(FILL);
		g.fill(shape);

		// sets border
		g.setColor(BORDER);

		// draws the ship
		g.setStroke(new BasicStroke(1f));
		g.draw(shape);
	}

	/**
	* Gets the ship (or shape)
	* @return the ship
	*/

	public Shape getShape() {
		return shape;
	}

	/**
	* Checks if the ship intersects with something else, such as (say) an asteroid.
	* @param other - the other object that will be checked for intersection.
	* @return true if both objects intersect; false otherwise
	*/

	public boolean intersects(Sprite other) {
		return shape.getBounds2D().intersects((other.getShape()).getBounds2D());
	}
}
