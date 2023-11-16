
public class RangedEntity extends EnemyEntity{
	public RangedEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, 1);
		
	} // RangedEntity
	
	public void calculateMove() {
		super.calculateMove();
		
		if ((x / TileEntity.TILE_SIZE == Game.robot.x / TileEntity.TILE_SIZE
				&& super.findPath(this, Game.robot).size() == Math.abs(y / TileEntity.TILE_SIZE - Game.robot.y / TileEntity.TILE_SIZE))
				|| (y / TileEntity.TILE_SIZE == Game.robot.y / TileEntity.TILE_SIZE
				&& super.findPath(this, Game.robot).size() == Math.abs(x / TileEntity.TILE_SIZE - Game.robot.x / TileEntity.TILE_SIZE))) {
				dx = 0;
				dy = 0;
		} // if
		
	} // calculateMove
	
	private void shootLaser(int distance, int direction) {
		
	} // shootLaser
	
} // RangedEntity
