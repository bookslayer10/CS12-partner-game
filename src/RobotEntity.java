import java.awt.Rectangle;

/* RobotEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class RobotEntity extends Entity {

	protected final int MAX_ENERGY = 100;
	private int energy;
	private static final int MOVE_SPEED = 1 * TileEntity.TILE_SIZE;
	
	/*
	 * construct the player's robot input: game - the game in which the ship is being
	 * created ref - a string with the name of the image associated to the sprite
	 * for the ship x, y - initial location of ship
	 */
	public RobotEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, true); // calls the constructor in Entity
		direction = 180;
		energy = MAX_ENERGY;
	} // constructor

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move ship
	 */
	public void move(long delta) {
		super.move(delta); // calls the move method in Entity
	} // move

	/*
	 * collidedWith input: other - the entity with which the ship has collided
	 * purpose: notification that the player's ship has collided with something
	 */
	public void collidedWith(Entity other) {
		if (other instanceof EnemyEntity) {
			game.notifyDeath();
		} // if
	} // collidedWith
	
	// takes the delta value of the movement, checks to see if it's possible, and if so it starts 
	public boolean tryToMove(int direction) {
		
		this.direction = direction;
		
		switch (direction) {
		case 0: // up
			dx = 0;
			dy = -MOVE_SPEED;
			break;
		case 90: // right
			dx = MOVE_SPEED;
			dy = 0;
			break;
		case 180: // down
			dx = 0;
			dy = MOVE_SPEED;
			break;
		case 270: // left
			dx = -MOVE_SPEED;
			dy = 0;
			break;
		} // switch
		
		calculateMove();
		
		if (this.collidesWith(goalTile, (int)dx, (int)dy) && goalTile.getCollision()) {
			dy = 0;
			dx = 0;
		} // if
		
		return getIsMoving();
	} // tryToMove
	
	// sets the direction of the robot from the input degree value.
	public void setDirection(int degree) {
		if (degree > 180 ) {
			direction = 270;
		} else if (degree > 0 && degree < 180) {
			direction = 90;
		} else {
			direction = degree;
		} // else
	} // setDirection
	
	@Override
	public Rectangle getHitbox(int shiftx, int shifty) {
		Rectangle rect = new Rectangle();
		
		rect.setBounds((int) x + sprite.getWidth() / 4 + shiftx, (int) y +  sprite.getHeight() / 4 + shifty, sprite.getWidth() / 2, sprite.getHeight() / 2);
		
		return rect;
	} // getHitbox
	
	public int getEnergy() {
		return energy;
	} // getEnergy
	
	// simplification for easier use of energy
	public void useEnergy(int usedEnergy) {
		setEnergy(getEnergy() - usedEnergy);
	} // useEnergy
	
	public void setEnergy(int energy) {
		energy = Math.max(energy, 0);
		this.energy = Math.min(energy, MAX_ENERGY);
	} // setEnergy

} // RobotEntity