import java.awt.Rectangle;

/* RobotEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class RobotEntity extends Entity {

	private int energy = 40;
	
	/*
	 * construct the player's robot input: game - the game in which the ship is being
	 * created ref - a string with the name of the image associated to the sprite
	 * for the ship x, y - initial location of ship
	 */
	public RobotEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, true); // calls the constructor in Entity
		direction = 180;
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
			dy = -64;
			break;
		case 90: // right
			dx = 64;
			dy = 0;
			break;
		case 180: // down
			dx = 0;
			dy = 64;
			break;
		case 270: // left
			dx = -64;
			dy = 0;
			break;
		} // switch
		
		calculateMove();
		
		if (this.collidesWith(goalTile, (int)dx, (int)dy) && goalTile.getCollision()) {
			dy = 0;
			dx = 0;
		} // if
		
		return getIsMoving();
	}
	
	public Rectangle getHitbox(int shiftx, int shifty) {
		Rectangle rect = new Rectangle();
		
		rect.setBounds((int) x + sprite.getWidth() / 4 + shiftx, (int) y +  sprite.getHeight() / 4 + shifty, sprite.getWidth() / 2, sprite.getHeight() / 2);
		
		return rect;
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public void setEnergy(int energy) {
		this.energy = Math.min(energy, 40);
	}

} // RobotEntity class
