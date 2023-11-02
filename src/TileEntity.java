
public class TileEntity extends Entity {
	private boolean collision;

	public TileEntity(Game g, String r, int x, int y) {
		super(g, r, x, y, false);
		switch (r.charAt(r.length() - 5)) {
			case '0':
				collision = false;
				break;
			case '1':
			case '2':
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
