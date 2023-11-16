
public class RangedEntity extends EnemyEntity{
	
	private int shooting;
	private boolean shot;
	
	public RangedEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, 1);
		shooting = -1;
	} // RangedEntity
	
	public void calculateMove() {
		super.calculateMove();
		
		if (shooting != -1) {
			shootLaser(shooting, false);
			shooting = -1;
			dx = 0;
			dy = 0;
			shot = true;
		} else if (shot) {
			dx = 0;
			dy = 0;
			shot = false;
		} else if ((x / TileEntity.TILE_SIZE == Game.robot.x / TileEntity.TILE_SIZE
				&& super.findPath(this, Game.robot).size() == Math.abs(y / TileEntity.TILE_SIZE - Game.robot.y / TileEntity.TILE_SIZE))
				|| (y / TileEntity.TILE_SIZE == Game.robot.y / TileEntity.TILE_SIZE
				&& super.findPath(this, Game.robot).size() == Math.abs(x / TileEntity.TILE_SIZE - Game.robot.x / TileEntity.TILE_SIZE))) {
				dx = 0;
				dy = 0;
				
				int shiftX = (int) ((Game.robot.x - x)/ TileEntity.TILE_SIZE);
				int shiftY = (int) ((Game.robot.y - y)/ TileEntity.TILE_SIZE);
				
				if (shiftX > 0) {
					direction = 90;
				} else if (shiftX < 0) {
					direction = 270;
				} else if (shiftY > 0) {
					direction = 180;
				} else {
					direction = 0;
				} // else
				shooting = direction;
				shootLaser(shooting, true);
		} // if
		
	} // calculateMove
	
	private void shootLaser(int direction, boolean highlight) {
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
		
		if (highlight) {
			game.addEntity(new HighlightEntity(game, "sprites/laser/laser_", (int) x + magX * TileEntity.TILE_SIZE, (int) y + magY * TileEntity.TILE_SIZE, direction, magX, magY));
		} else {
		game.addEntity(new LaserEntity(game, "sprites/laser/laser_", (int) x + magX * TileEntity.TILE_SIZE, (int) y + magY * TileEntity.TILE_SIZE, direction, magX, magY));
		}
	} // shootLaser
	
} // RangedEntity
