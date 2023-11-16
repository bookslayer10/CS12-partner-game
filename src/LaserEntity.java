import java.awt.Graphics;
import java.awt.Rectangle;

public class LaserEntity extends Entity {
	
	private int magX;
	private int magY;
	private int limit;
	
	public LaserEntity(Game g, String r, int newX, int newY, int direction, int magX, int magY) {
		super(g, r, newX, newY, true);
		this.magX = magX;
		this.magY = magY;
		this.direction = direction;

		for (limit = 0; ; limit++) {
			int goalTileIndex = (((int) (y + magY * limit) / TileEntity.TILE_SIZE) * 29 + (int) (x + magX * limit) / TileEntity.TILE_SIZE);
			if (goalTileIndex < 0) {
				goalTileIndex = 0;
			}
			if (goalTileIndex > game.tiles.size() - 1) {
				goalTileIndex = game.tiles.size() - 1;
			}
			
			if (game.tiles.get(goalTileIndex).getCollision()) {
				return;
			}
		}
	} // LaserEntity
	
	
	public void draw(Graphics g) {
		for (int i = 0; i < limit; i++) {
			sprite.draw(g, (int) x + i * magX * TileEntity.TILE_SIZE, (int) y + i * magY * TileEntity.TILE_SIZE);
		} // for
	} // draw
	
	public Rectangle getHitbox(int shiftx, int shifty) {
		Rectangle rect = new Rectangle();
		
		rect.setBounds((int) x + Math.min(0, limit * magX),
				(int) y + Math.min(0, limit * magY),
				sprite.getWidth() * Math.max(1, Math.abs(magX) * limit),
				sprite.getHeight() * Math.max(1, Math.abs(magY) * limit));
		
		return rect;
	} // getHitbox
	
	public void collidedWith(Entity other) {
		if (other instanceof EnemyEntity) {
			
			((EnemyEntity) other).addHealth(-1);
			
			if(((EnemyEntity) other).getHealth() < 1) {
				game.removeEntity(other);
			} // if
		} // if
		
		if (other instanceof RobotEntity) {
			game.notifyDeath();
		} // if
		
	}

} // LaserEntity