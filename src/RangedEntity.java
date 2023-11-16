
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
				
				int shiftX = (int) (x / TileEntity.TILE_SIZE - Game.robot.x / TileEntity.TILE_SIZE);
				int shiftY = (int) (y / TileEntity.TILE_SIZE - Game.robot.y / TileEntity.TILE_SIZE);
				
				if (shiftX > 0) {
					direction = 90;
				} else if (shiftX < 0) {
					direction = 270;
				} else if (shiftY > 0) {
					direction = 180;
				} else {
					direction = 0;
				} // else
				shootLaser(direction);
				
		} // if
		
	} // calculateMove
	
	private void shootLaser(int direction) {
		int magX;
		int magY;
		switch(direction) {
		case 0:
			magX = 0;
			magY = -1;
			break;
		case 90:
			magX = 1;
			magY = 0;
			break;
		case 180:
			magX = 0;
			magY = 1;
			break;
		default:
			magX = -1;
			magY = 0;
	} // switch
		
		game.addEntity(new LaserEntity(game, "sprites/laser/laser_", (int) x + magX * TileEntity.TILE_SIZE, (int) y + magY * TileEntity.TILE_SIZE, direction, magX, magY));
	} // shootLaser
	
} // RangedEntity
