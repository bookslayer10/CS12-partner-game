import java.awt.Rectangle;

/* AlienEntity.java
 * March 27, 2006
 * Represents one of the aliens
 */
public class EnemyEntity extends Entity {
	
	private static int killed;
	private static int active;
	
	/*
	 * construct a new alien input: game - the game in which the alien is being
	 * created r - the image representing the alien x, y - initial location of alien
	 */
	public EnemyEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, true); // calls the constructor in Entity
		direction = 180;
	} // constructor

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move alien
	 */
	public void move(long delta) {
		
				
		
		// proceed with normal move
		super.move(delta);
	} // move

	public void calculateMove() {
		
		// where to put all the AI probably
		
		direction = (direction + 90) % 360;
		
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
		
		super.calculateMove();
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
	
	public Rectangle getHitbox(int shiftx, int shifty) {
		Rectangle rect = new Rectangle();
		
		rect.setBounds((int) x + sprite.getWidth() / 4 + shiftx, (int) y +  sprite.getHeight() / 4 + shifty, sprite.getWidth() / 2, sprite.getHeight() / 2);
		
		return rect;
	}
	
	public static void setKilled(int killed) {
		if(killed < 0) {
			System.out.println("Negative number of killed enemies!");
			EnemyEntity.killed = 0;
		} else {
			EnemyEntity.killed = killed;
		}
	}
	
	public static void setActive(int active) {
		if(active < 0) {
			System.out.println("Negative number of active enemies!");
			EnemyEntity.active = 0;
		} else {
			EnemyEntity.active = active;
		}
	}
	
	public static int getKilled() {
		return killed;
	}
	
	public static int getActive() {
		return active;
	}
	
} // AlienEntity class
