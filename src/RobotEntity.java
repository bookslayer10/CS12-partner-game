/* RobotEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class RobotEntity extends Entity {

	/*
	 * construct the player's robot input: game - the game in which the ship is being
	 * created ref - a string with the name of the image associated to the sprite
	 * for the ship x, y - initial location of ship
	 */
	public RobotEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, true); // calls the constructor in Entity
		direction = 2;
	} // constructor

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move ship
	 */
	public void move(long delta) {
		/*
		int frameTime = (int) (System.currentTimeMillis() % 500) / 125;

		sprite = frames[2][frameTime];
		*/
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
	public boolean tryToMove(int gx, int gy) {
	
		calculateMove(x + gx, y + gy);		
		
		return getIsMoving();
	}

} // RobotEntity class
