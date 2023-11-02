/* Entity.java
 * An entity is any object that appears in the game.
 * It is responsible for resolving collisions and movement.
 */

import java.awt.*;

public abstract class Entity {

	// Java Note: the visibility modifier "protected"
	// allows the variable to be seen by this class,
	// any classes in the same package, and any subclasses
	// "private" - this class only
	// "public" - any class can see it

	protected double x; // current x location
	protected double y; // current y location
	
	protected Game game;
	protected Sprite sprite; // this entity's sprite
	private Sprite[][] sprites = new Sprite[4][4]; // array of animated sprites with each direction (up, right, down,
												   // left) being a array of 4 frames
	protected boolean isAnimated;
	
	protected double dx = 0; // horizontal speed (px/s) + -> right
	protected double dy = 0; // vertical speed (px/s) + -> down
	protected int direction;
	protected double turnTargetX;
	protected double turnTargetY;
	
	static private final long TURN_LENGTH = 500;
	
	private Rectangle me = new Rectangle(); // bounding rectangle of
											// this entity
	private Rectangle him = new Rectangle(); // bounding rect. of other
												// entities

	/*
	 * Constructor input: reference to the image for this entity, initial x and y
	 * location to be drawn at
	 */
	public Entity(Game g, String r, int newX, int newY, boolean isAnimated) {
		game = g;
		x = newX;
		y = newY;
		this.isAnimated = isAnimated;
		
		if (!isAnimated) {
			sprite = (SpriteStore.get()).getSprite(r);
		} // if
		
		else {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					sprites[i][j] = (SpriteStore.get())
							.getSprite(r + i + "_" + j + ".png");
				} // for
			} // for
			sprite = sprites[0][0];
		} // else
		
	} // constructor

	/*
	 * move input: delta - the amount of time passed in ms output: none purpose:
	 * after a certain amount of time has passed, update the location
	 * changes sprite frame if it is animated
	 */
	
	public void move(long delta) {
		
		// moves the sprite to its new x position
		x += (delta * dx) / TURN_LENGTH;
		if ((dx > 0 && x > turnTargetX) || (dx < 0 && x < turnTargetX)) {
			x = turnTargetX;
			dx = 0;
		} // if
		
		y += (delta * dy) / TURN_LENGTH;
		if ((dy > 0 && y > turnTargetY) || (dy < 0 && y < turnTargetY)) {
			y = turnTargetY;
			dy = 0;
		} // if
		
		if (isAnimated) {
			
			calculateDirection();
			
			int frameTime = (int) (System.currentTimeMillis() % 500) / 125;
			sprite = sprites[direction][frameTime];
			
		} // if
	} // move
	
	// calculates the velocity of dx and dy based on target x and y
	// makes move do something
	public void calculateMove(double tx, double ty) {
		turnTargetX = tx;
		turnTargetY = ty;
		
		dx = tx - x;
		dy = ty - y;
		
		calculateDirection();
		
		int goalTile = (((int) (y + dy) / game.TILE_SIZE) * 29 + (int) (x + dx) / game.TILE_SIZE);
		if (goalTile < 0) {
			goalTile = 0;
		}
		
		if (this.collidesWith(game.tiles.get(goalTile), (int)dx, (int)dy) && game.tiles.get(goalTile).getCollision()) {
			dy = 0;
			dx = 0;
		} // if

	}
	
	// for overrides
	public void calculateMove() {
		
	}
	
	// get and set velocities
	public void setHorizontalMovement(double newDX) {
		dx = newDX;
	} // setHorizontalMovement

	public void setVerticalMovement(double newDY) {
		dy = newDY;
	} // setVerticalMovement

	public double getHorizontalMovement() {
		return dx;
	} // getHorizontalMovement

	public double getVerticalMovement() {
		return dy;
	} // getVerticalMovement

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	// get position
	public int getX() {
		return (int) x;
	} // getX

	public int getY() {
		return (int) y;
	} // getY
	
	public boolean getIsMoving() {
		return dx != 0 || dy != 0;
	}
	
	public void calculateDirection() {
		if (dx > 0) {
			direction = 1;
		} // if
		
		else if (dx < 0) {
			direction = 3;
		} // else if
		
		else if (dy > 0) {
			direction = 2;
		} // else if 
		
		else if (dy < 0){
			direction = 0;
		} // else if
	}
	
	/*
	 * Draw this entity to the graphics object provided at (x,y)
	 */
	public void draw(Graphics g) {
		sprite.draw(g, (int) x, (int) y);
	} // draw

	/*
	 * Do the logic associated with this entity. This method will be called
	 * periodically based on game events.
	 */
	public void doLogic() {
	}

	/*
	 * collidesWith input: the other entity to check collision against output: true
	 * if entities collide purpose: check if this entity collides with the other.
	 */
	public boolean collidesWith(Entity other, int shiftx, int shifty) {
		me.setBounds((int) x + shiftx, (int) y + shifty, sprite.getWidth(), sprite.getHeight());
		him.setBounds(other.getX(), other.getY(), other.sprite.getWidth(), other.sprite.getHeight());
		return me.intersects(him);
	} // collidesWith

	/*
	 * collidedWith input: the entity with which this has collided purpose:
	 * notification that this entity collided with another Note: abstract methods
	 * must be implemented by any class that extends this class
	 */
	public abstract void collidedWith(Entity other);

} // Entity class
