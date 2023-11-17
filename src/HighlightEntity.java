import java.awt.Color;
import java.awt.Graphics;

/* HighlightEntity.Java
 * Is created by RangedEntity to show where it will shoot next turn
 * Uses laser properties (width, height, length) to show full area of effect
 * Stays on screen until laser is fired, or the RangedEntity dies
 */

public class HighlightEntity extends LaserEntity{

	/* Constructs a highlight box
	 * uses Entity constructor with game, sprite, and x, y coordinate
	 * also takes the direction relative to the RangedEntity from which it was fired
	 */
	public HighlightEntity(Game g, String r, int newX, int newY, int direction, int scaleX, int scaleY) {
		super(g, r, newX, newY, direction, scaleX, scaleY);	
	} // HighlightEntity

	/* Overrides default draw to include every tile the laser hits - uses same method as LaserEntity.getHitbox
	 * fields x and y are the top left corner of the first tile the laser will hit (right next to the RangedEntity that will fire it)
	 * x: if scaleX is negative (shooting left), shifts the corner left by the laser's extra length
	 * y: if scaleY is negative (shooting up), shifts the corner up by the laser's extra length 
	 * width: minimum length of 1 * sprite width, but if scaleX is non-zero (shooting left or right) increases it to limit * sprite width
	 * width: minimum width of 1 * sprite width, but if scaleX is non-zero (shooting left or right) increases it to limit * sprite width
	 * height: minimum height of 1 * sprite height, but if scaleY is non-zero (shooting up or down) increases it to limit * sprite height
	 */
	public void draw(Graphics g) {
		g.setColor(game.LASER);
		g.fillRect((int) x + Math.min(0, (limit - 1) * TileEntity.TILE_SIZE * scaleX),
				(int) y + Math.min(0, (limit - 1) * TileEntity.TILE_SIZE * scaleY),
				sprite.getWidth() * Math.max(1, Math.abs(scaleX) * limit),
				sprite.getHeight() * Math.max(1, Math.abs(scaleY) * limit));
	} // draw
	
	// does not interact with other Entities
	public void collidedWith(Entity other) {
		
	} // collidedWith
	
}
