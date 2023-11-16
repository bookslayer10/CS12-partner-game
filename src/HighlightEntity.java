import java.awt.Color;
import java.awt.Graphics;

public class HighlightEntity extends LaserEntity{

	public HighlightEntity(Game g, String r, int newX, int newY, int direction, int magX, int magY) {
		super(g, r, newX, newY, direction, magX, magY);
		
	}

	
	public void draw(Graphics g) {
		g.setColor(game.LASER);
		g.fillRect((int) x + Math.min(0, (limit - 1) * TileEntity.TILE_SIZE * magX),
				(int) y + Math.min(0, (limit - 1) * TileEntity.TILE_SIZE * magY),
				sprite.getWidth() * Math.max(1, Math.abs(magX) * limit),
				sprite.getHeight() * Math.max(1, Math.abs(magY) * limit));
	} // draw
	
	public void collidedWith(Entity other) {
		
	} // collidedWith
	
}
