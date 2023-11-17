import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/* 
 * LaserEntity.Java
 * Is created by RangedEntity to kill player
 * Stays on screen until next turn, or the RangedEntity dies
 */
public class LaserEntity extends Entity {
	
	/* scaleX, scaleX are int values from 1- to 1  that indicate the scaling of dimensions due to direction
	 * (Ex. a laser shot down would have scaleX = 0 and scaleY = 1)
	 */
	protected int scaleX;
	protected int scaleY;
	
	
	protected int limit; // length of the laser (ends upon reaching a tile with collision)
	protected int countdown = 1; // allows the laser to linger in-game for one turn
	
	/* Constructs a Laser
	 * uses Entity constructor with game, sprite, and x, y coordinate
	 * also takes the direction relative to the RangedEntity from which it was fired
	 */
	public LaserEntity(Game g, String r, int newX, int newY, int direction, int scaleX, int scaleY) {
		super(g, r, newX, newY, true);
		
		this.direction = direction;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		
		// Sets limit to length of laser where it will not overlap an obstacle
		for (limit = 0; ; limit++) {
			
			// one tile past the farthest tile reached at current length
			int goalTileIndex = (((int) (y / TileEntity.TILE_SIZE) + scaleY * limit)* 29 + (int) (x / TileEntity.TILE_SIZE + scaleX * limit));
			if (goalTileIndex < 0) {
				goalTileIndex = 0;
			}
			if (goalTileIndex > game.tiles.size() - 1) {
				goalTileIndex = game.tiles.size() - 1;
			}
			
			// if the next tile would collide, ends loop. otherwise, will test next tile
			if (game.tiles.get(goalTileIndex).getCollision()) {
				return;
			} // if goalTile collides
		}
		
	} // LaserEntity
	
	// Counts down on first turn it exists, then deletes itself on second
	public void calculateMove() {
		if (countdown-- == 0) {
			game.removeEntity(this);
		}
	} // calculateMove
	
	// draws a sprite for each tile within limit
	public void draw(Graphics g) {
		for (int i = 0; i < limit; i++) {
			sprite.draw(g, (int) x + i * scaleX * TileEntity.TILE_SIZE, (int) y + i * scaleY * TileEntity.TILE_SIZE);
		} // for
	} // draw
	
	/* 
	 * Overrides default rectangle hitbox to include every tile the laser hits
	 * fields x and y are the top left corner of the first tile the laser hits (right next to the RangedEntity that fired it)
	 * x: if scaleX is negative (shooting left), shifts the corner left by the laser's extra length
	 * y: if scaleY is negative (shooting up), shifts the corner up by the laser's extra length 
	 * width: minimum length of 1 * sprite width, but if scaleX is non-zero (shooting left or right) increases it to limit * sprite width
	 * width: minimum width of 1 * sprite width, but if scaleX is non-zero (shooting left or right) increases it to limit * sprite width
	 * height: minimum height of 1 * sprite height, but if scaleY is non-zero (shooting up or down) increases it to limit * sprite height
	 */
	public Rectangle getHitbox(int shiftx, int shifty) {
		Rectangle rect = new Rectangle();	
		rect.setBounds((int) x + Math.min(0, (limit - 1) * TileEntity.TILE_SIZE * scaleX),
				(int) y + Math.min(0, (limit - 1) * TileEntity.TILE_SIZE * scaleY),
				sprite.getWidth() * Math.max(1, Math.abs(scaleX) * limit),
				sprite.getHeight() * Math.max(1, Math.abs(scaleY) * limit));		
		return rect;
	} // getHitbox
	
	public void collidedWith(Entity other) {
		if (other instanceof EnemyEntity) {

			// Damages other enemies, kills them if their health reaches 0
			((EnemyEntity) other).addHealth(-1);			
			if(((EnemyEntity) other).getHealth() < 1) {
				game.removeEntity(other);
			} // if
		} // if
		
		if (other instanceof RobotEntity) {
			if (game.makingMove == false ) {
				game.notifyDeath();
			} // if
		} // if
		
	}

} // LaserEntity