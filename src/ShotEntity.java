import java.awt.Rectangle;

/* ShotEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class ShotEntity extends Entity {
	
	private boolean used = false; // true if shot hits something
	private static int SHOTSPEED = 4;
	private static int DIAGONAL_SHOTSPEED = 3;

	/*
	 * construct the shot input: game - the game in which the shot is being created
	 * ref - a string with the name of the image associated to the sprite for the
	 * shot x, y - initial location of shot
	 */
	public ShotEntity(Game g, String r, int newX, int newY, int direction) {
		super(g, r, newX, newY, true); // calls the constructor in Entity
		
		sprites = new Sprite[8][4];
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 4; j++) {
				sprites[i][j] = (SpriteStore.get())
						.getSprite(r + i * 45 + "_" + j + ".png");
			} // for
		} // for
		sprite = sprites[0][0];
		
		this.direction = direction;
	} // constructor

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move shot
	 */
	public void move(long delta) {
		super.move(delta); // calls the move method in Entity
	
		int frameTime = (int) (System.currentTimeMillis() % 500) / 125;
		sprite = sprites[direction / 45][frameTime];

		if (x < -100 || x > game.SCREEN_WIDTH + 100 || y < -100 || y > game.SCREEN_HEIGHT + 100) {
			game.removeEntity(this);
		} // if
		
		for (int i = 0; i < game.tiles.size(); i++) {
			if (game.tiles.get(i).getCollision() && this.collidesWith(game.tiles.get(i), 0, 0)) {
				game.removeEntity(this);
			} // if
		}
		
	} // move
	
	
	public void calculateMove() {
		
		switch (direction) {
		case 0:
			dx = 0;
			dy = -SHOTSPEED * TileEntity.TILE_SIZE;
			break;
		case 45:
			dx = DIAGONAL_SHOTSPEED * TileEntity.TILE_SIZE;
			dy = -DIAGONAL_SHOTSPEED * TileEntity.TILE_SIZE;
			break;
		case 90:
			dx = SHOTSPEED * TileEntity.TILE_SIZE;
			dy = 0;
			break;
		case 135:
			dx = DIAGONAL_SHOTSPEED * TileEntity.TILE_SIZE;
			dy = DIAGONAL_SHOTSPEED * TileEntity.TILE_SIZE;
			break;
		case 180:
			dx = 0;
			dy = SHOTSPEED * TileEntity.TILE_SIZE;
			break;
		case 225:
			dx = -DIAGONAL_SHOTSPEED * TileEntity.TILE_SIZE;
			dy = DIAGONAL_SHOTSPEED * TileEntity.TILE_SIZE;
			break;
		case 270:
			dx = -SHOTSPEED * TileEntity.TILE_SIZE;
			dy = 0;
			break;
		case 315:
			dx = -DIAGONAL_SHOTSPEED * TileEntity.TILE_SIZE;
			dy = -DIAGONAL_SHOTSPEED * TileEntity.TILE_SIZE;
			break;
		}
		
		super.calculateMove();
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
			
			game.notifyEnemyKilled();
			used = true;
		} // if

	} // collidedWith
	
	public Rectangle getHitbox(int shiftx, int shifty) {
		Rectangle rect = new Rectangle();
		
		rect.setBounds((int) x + sprite.getWidth() / 4 + shiftx, (int) y +  sprite.getHeight() / 4 + shifty, sprite.getWidth() / 2, sprite.getHeight() / 2);
		
		return rect;
	}

} // RobotEntity class
