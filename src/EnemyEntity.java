/* AlienEntity.java
 * March 27, 2006
 * Represents one of the aliens
 */
public class EnemyEntity extends Entity {

	private int prototypeMove = 0;

	/*
	 * construct a new alien input: game - the game in which the alien is being
	 * created r - the image representing the alien x, y - initial location of alien
	 */
	public EnemyEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, true); // calls the constructor in Entity
		direction = 2;
	} // constructor

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move alien
	 */
	public void move(long delta) {
		
				
		
		// proceed with normal move
		super.move(delta);
	} // move

	public void calculateMove() {
		
		switch (prototypeMove) {
		case 0:
			calculateMove(x, y - 64);
			break;
		case 1:
			calculateMove(x + 64, y);
			break;
		case 2:
			calculateMove(x, y + 64);
			break;
		case 3:
			calculateMove(x - 64, y);
			break;
		}
		
		prototypeMove = (prototypeMove + 1) % 4;
	}
	
	/*
	 * doLogic Updates the game logic related to the aliens, ie. move it down the
	 * screen and change direction
	 */
	public void doLogic() {
		// swap horizontal direction and move down screen 10 pixels
		dx *= -1;
		y += 10;

		// if bottom of screen reached, player dies
		if (y > 570) {
			game.notifyDeath();
		} // if
	} // doLogic

	/*
	 * collidedWith input: other - the entity with which the alien has collided
	 * purpose: notification that the alien has collided with something
	 */
	public void collidedWith(Entity other) {
		// collisions with aliens are handled in ShotEntity and RobotEntity
	} // collidedWith

} // AlienEntity class
