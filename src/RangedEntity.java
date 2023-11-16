/* RangedEntity.java
 * An EnemyEntity that pathfinds towards the player until it has an uninterrupted line to shoot at it
 * and then charges for one turn before shooting an instantaneous laser that hits all BOTs between the
 * RangedEntity and the next obstacle (tile with collision on)
 */

public class RangedEntity extends EnemyEntity{
	
	private int shooting; // the direction in which this will shoot
	private boolean shot;
	private LaserEntity laser;
	private HighlightEntity highlight;
	
	public RangedEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, 1);
		shooting = -1; // -1 is sentinel value for when it is not shooting
	} // RangedEntity
	
	
	/* 
	 * 
	 */
	public void calculateMove() {
		super.calculateMove();
		
		// if this is shooting, creates a laserEntity
		if (shooting != -1) {
			shootLaser(shooting, false);
			shooting = -1;
			dx = 0;
			dy = 0;
			shot = true;
			
		// if this shot last turn, keeps it in place for one more turn
		} else if (shot) {
			dx = 0;
			dy = 0;
			shot = false;
			
		// if this is in line with the robot, 
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
	
	/* Upon death, removes the RangedEntity and Entities that originate from it (laser and highlight)
	 */
	
	public void die() {
		game.removeEntity(this);
		game.removeEntity(laser);
		game.removeEntity(highlight);
	} // die
	
	public void collidedWith(Entity other) {
		if(other instanceof EnemyEntity) {
			game.removeEntity(this);
			game.notifyEnemyKilled();
		}
	} // collidedWith
	
	/* Either shoots a laser or highlights the space where the laser is to be shot
	 * direction: the direction from the rangedEntity it is to be fired
	 * isHighlight : true if the rangedEntity is not shooting yet, merely telegraphing its next turn
	 */
	private void shootLaser(int direction, boolean isHighlight) {
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
		
		
		if (isHighlight) {
			highlight = new HighlightEntity(game, "sprites/laser/laser_", (int) x + magX * TileEntity.TILE_SIZE, (int) y + magY * TileEntity.TILE_SIZE, direction, magX, magY);
			game.addEntity(highlight);
		} else {
			laser = new LaserEntity(game, "sprites/laser/laser_", (int) x + magX * TileEntity.TILE_SIZE, (int) y + magY * TileEntity.TILE_SIZE, direction, magX, magY);
			game.addEntity(laser);
		}
	} // shootLaser
	
} // RangedEntity
