import java.awt.*;

public interface Sprite {
	public void draw(Graphics2D g);
	public void move();
	public Shape getShape(); // used by intersects
	public boolean intersects(Sprite other);
}
