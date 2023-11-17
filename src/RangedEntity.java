/* RangedEntity.java
 * An EnemyEntity that pathfinds towards the player until it has an uninterrupted line to shoot at it
 * and then charges for one turn before shooting an instantaneous laser that hits all BOTs between the
 * RangedEntity and the next obstacle (tile with collision on)
 */

public class RangedEntity extends EnemyEntity{
	
	private int shooting; // the direction in which this will shoot
	private boolean shot; // true if RangedEntity shot last turn
	private LaserEntity laser; // laser fired by RangedEntity
	private HighlightEntity highlight; // telegraphs where RangedEntity will fire next turn
	
	public RangedEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, 1);
		shooting = -1; // -1 is sentinel value for when it is not shooting
	} // RangedEntity
	
	
	/* 
	 * RangedEntity will move to be in line with the player
	 * once it is, it will charge laser, shoot laser, or wait after shooting
	 * next step in the shooting process is stored using variables shot and shooting
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
			
		// if this is in line with the robot, sets movement to 0, finds which direction the robot is in
		// and charges and telegraphs a laser in that direction
		} else if ((x / TileEntity.TILE_SIZE == Game.robot.x / TileEntity.TILE_SIZE
				&& super.findPath(this, Game.robot).size() == Math.abs(y / TileEntity.TILE_SIZE - Game.robot.y / TileEntity.TILE_SIZE))
				|| (y / TileEntity.TILE_SIZE == Game.robot.y / TileEntity.TILE_SIZE
				&& super.findPath(this, Game.robot).size() == Math.abs(x / TileEntity.TILE_SIZE - Game.robot.x / TileEntity.TILE_SIZE))) {
				
			dx = 0;
				dy = 0;
				
				// the difference in coordinate between this Entity and the robot
				// when one is zero, the other must not be
				int shiftX = (int) ((Game.robot.x - x)/ TileEntity.TILE_SIZE);
				int shiftY = (int) ((Game.robot.y - y)/ TileEntity.TILE_SIZE);
				
				// turns RangedEntity towards robot
				if (shiftX > 0) {
					direction = 90;
				} else if (shiftX < 0) {
					direction = 270;
				} else if (shiftY > 0) {
					direction = 180;
				} else {
					direction = 0;
				} // else
								
				shooting = direction; // stores current direction to shoot next turn
				shootLaser(shooting, true); // highlights where laser will shoot
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
			this.die();
			game.notifyEnemyKilled();
		}
	} // collidedWith
	
	/* Either shoots a laser or highlights the space where the laser is to be shot
	 * direction: uses RangedEntity's direction at time of charging as the direction from the rangedEntity it is to be fired
	 * isHighlight : true if the rangedEntity is not shooting yet, merely telegraphing its next turn
	 */
	private void shootLaser(int direction, boolean isHighlight) {
		int scaleX;
		int scaleY;
		
		// Uses direction to determine where to apply laser's length
		switch(direction) {
			case 0:
				scaleX = 0;
				scaleY = -1;
				break;
			case 90:
				scaleX = 1;
				scaleY = 0;
				break;
			case 180:
				scaleX = 0;
				scaleY = 1;
				break;
			default:
				scaleX = -1;
				scaleY = 0;
		} // switch
		
		
		if (isHighlight) {
			highlight = new HighlightEntity(game, "sprites/laser/laser_", (int) x + scaleX * TileEntity.TILE_SIZE, (int) y + scaleY * TileEntity.TILE_SIZE, direction, scaleX, scaleY);
			game.addEntity(highlight);
		} else {
			laser = new LaserEntity(game, "sprites/laser/laser_", (int) x + scaleX * TileEntity.TILE_SIZE, (int) y + scaleY * TileEntity.TILE_SIZE, direction, scaleX, scaleY);
			game.addEntity(laser);
			game.removeEntity(highlight);
		}
	} // shootLaser
	
} // RangedEntity
