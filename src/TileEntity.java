
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
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case 'a':
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
