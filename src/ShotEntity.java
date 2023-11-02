/* ShotEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class ShotEntity extends Entity {

	private double moveSpeed = -300; // vert speed shot moves
	private boolean used = false; // true if shot hits something
	
	private double ix;
	private double iy;

	/*
	 * construct the shot input: game - the game in which the shot is being created
	 * ref - a string with the name of the image associated to the sprite for the
	 * shot x, y - initial location of shot
	 */
	public ShotEntity(Game g, String r, int newX, int newY, int ix, int iy) {
		super(g, r, newX, newY, true); // calls the constructor in Entity

		this.ix = ix;
		this.iy = iy;
	} // constructor

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move shot
	 */
	public void move(long delta) {
		super.move(delta); // calls the move method in Entity

		// if shot moves off top of screen, remove it from entity list
		if (y < -100) {
			game.removeEntity(this);
		} // if

	} // move
	
	
	public void calculateMove() {
		
		if (x < -100 || x > game.SCREEN_WIDTH + 100 || y < -100 || y > game.SCREEN_HEIGHT + 100) {
			game.removeEntity(this);
		} // if		
		
		turnTargetX = x + ix;
		turnTargetY = y + iy;
		
		super.calculateMove(turnTargetX, turnTargetY);
		if (!getIsMoving()) {
			game.removeEntity(this);
		}
	}
	
	/*
	 * collidedWith input: other - the entity with which the shot has collided
	 * purpose: notification that the shot has collided with something
	 */
	public void collidedWith(Entity other) {
		// prevents double kills
		if (used) {
			return;
		} // if

		// if it has hit an alien, kill it!
		if (other instanceof EnemyEntity) {
			// remove affect entities from the Entity list
			game.removeEntity(this);
			game.removeEntity(other);

			// notify the game that the alien is dead
			game.notifyAlienKilled();
			used = true;
		} // if

	} // collidedWith

} // RobotEntity class
