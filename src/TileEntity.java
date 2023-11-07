
public class TileEntity extends Entity {
	private boolean collision;
	public static final int TILE_SIZE = 64; // hor. vel. of ship (pixels per turn)
	
	public TileEntity(Game g, String r, int x, int y) {
		super(g, r, x, y, false);
		switch (r.charAt(r.length() - 5)) {
			case '0':
				collision = false;
				break;
			default:
				collision = true;
		}
	}

	public boolean getCollision() {
		return collision;
	}
	
	public void collidedWith(Entity other) {
		// TODO Auto-generated method stub
	}
}
