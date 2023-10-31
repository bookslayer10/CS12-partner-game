
public class TileEntity extends Entity {
	private boolean collision;
	private Game game;
	
	// 64 by 64
	public TileEntity(Game game, String filename, int x, int y) {
		super(filename, x, y);
		this.game = game;
	}

	public void collidedWith(Entity other) {
		// TODO Auto-generated method stub
		
	}
}
