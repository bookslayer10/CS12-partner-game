import java.awt.Rectangle;

/* TileEntity.java
 * All map tiles. Stores whether entities can move and/or spawn on them.
 */

public class TileEntity extends Entity {
	private boolean collision;
	private boolean spawnable;
	public static final int TILE_SIZE = 64; // hor. vel. of ship (pixels per turn)
	
	public TileEntity(Game g, String r, int x, int y) {
		super(g, r, x, y, false, 0);
		switch (r.substring(23, r.length() - 4)) {		
			case "0":
				spawnable = false;
				collision = false;
				break;
			case "20":
				spawnable = true;
				collision = true;
				break;
			default:
				spawnable = false;
				collision = true;
		} // switch
	}
	
	public boolean isSpawnable() {
		return spawnable;
	}

	public Rectangle getHitbox(int shiftx, int shifty) {
		Rectangle rect = new Rectangle();
		
		rect.setBounds((int) x + sprite.getWidth() / 4 + shiftx, (int) y +  sprite.getHeight() / 4 + shifty, sprite.getWidth() / 2, sprite.getHeight() / 2);
		
		return rect;
	}
	
	public boolean getCollision() {
		return collision;
	}
	
	public void collidedWith(Entity other) {
		// TODO Auto-generated method stub
	}
}
