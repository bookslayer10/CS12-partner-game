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
	protected Sprite[][] sprites; // array of animated sprites with each direction (up, right, down,
												   // left) being a array of 4 frames
	protected boolean isAnimated;
	
	protected double dx = 0; // horizontal speed (px/s) + -> right
	protected double dy = 0; // vertical speed (px/s) + -> down
	protected int direction;
	protected double turnTargetX;
	protected double turnTargetY;
	
	protected static final long TURN_LENGTH = 350;
	
	protected TileEntity goalTile;

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
			sprites = new Sprite[4][4];
			for (int i = 0; i < 4; i++) {
				sprites[i] = loadSpriteArray(r + i * 90);
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
			int frameTime = (int) (System.currentTimeMillis() % 500) / 125;
			sprite = sprites[direction / 90][frameTime];
			
		} // if
	} // move
	
	// calculates the velocity of dx and dy based on target x and y
	// makes move do something
	public void calculateMove() {
		turnTargetX = x + dx;
		turnTargetY = y + dy;
		
		int goalTileIndex = (((int) (y + dy) / TileEntity.TILE_SIZE) * 29 + (int) (x + dx) / TileEntity.TILE_SIZE);
		if (goalTileIndex < 0) {
			goalTileIndex = 0;
		}
		if (goalTileIndex > game.tiles.size() - 1) {
			goalTileIndex = game.tiles.size() - 1;
		}
		
		goalTile = game.tiles.get(goalTileIndex);
		
	} // calculateMove
	
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

	public Sprite[] loadSpriteArray(String r) {
		Sprite[] sprites = new Sprite[4];
		
		for (int j = 0; j < 4; j++) {
			sprites[j] = (SpriteStore.get())
					.getSprite(r + "_" + j + ".png");
		} // for
		
		return sprites;		
	}
	
	// get position
	public int getX() {
		return (int) x;
	} // getX

	public int getY() {
		return (int) y;
	} // getY
	
	// return if the entity is in motion
	public boolean getIsMoving() {
		return dx != 0 || dy != 0;
	} // getIsMoving
	
	/*
	 * Draw this entity to the graphics object provided at (x,y)
	 */
	public void draw(Graphics g) {
		sprite.draw(g, (int) x, (int) y);
	} // draw

	/*
	 * collidesWith input: the other entity to check collision against output: true
	 * if entities collide purpose: check if this entity collides with the other.
	 */
	public boolean collidesWith(Entity other, int shiftx, int shifty) {
		return getHitbox(shiftx, shifty).intersects(other.getHitbox(0, 0));
	} // collidesWith
	
	// returns the hitbox of the entity, with the option to shift it around
	public Rectangle getHitbox(int shiftx, int shifty) {
		Rectangle rect = new Rectangle();
		
		rect.setBounds((int) x + shiftx, (int) y + shifty, sprite.getWidth(), sprite.getHeight());
		
		return rect;
	} // getHitbox
	
	/*
	 * collidedWith input: the entity with which this has collided purpose:
	 * notification that this entity collided with another Note: abstract methods
	 * must be implemented by any class that extends this class
	 */
	public abstract void collidedWith(Entity other);

} // Entity class
