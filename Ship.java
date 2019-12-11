import java.awt.geom.Rectangle2D;

/**
* Ship interface
*/

public interface Ship extends Sprite {

	public enum Direction {
		NONE(0, 0), UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0), NORTHWEST(-1, -1), NORTHEAST(1, -1), SOUTHWEST(-1, 1), SOUTHEAST(1, 1);
		public final int dx;
		public final int dy;
		Direction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}
	};

	public void setDirection(Direction d);
	public void setMovementBounds(Rectangle2D bounds);
}
