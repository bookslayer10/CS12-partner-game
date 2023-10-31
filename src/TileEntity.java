
public class TileEntity extends Entity {
	private boolean collision;
	private Game game;
	
	
	public TileEntity(Game game, String filename, int x, int y) {
		super(filename, x, y);
		this.game = game;
	}

	public void collidedWith(Entity other) {
		// TODO Auto-generated method stub
		
	}
}
