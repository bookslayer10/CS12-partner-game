import java.awt.Graphics;
import java.awt.Rectangle;

public class MortarEntity extends Entity {
	
	private static final int BLAST_DIAMETER = 130;
	private boolean detonating = false;
	private int countdown;
	
	// MortarEntity constructor
	public MortarEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, false, 1); // calls the constructor in Entity
		countdown = 4;
	} // MortarEntity
	
	/* If MortarEntity has finished counting down, it will detonate
	 * if it has already begun to detonate, it will finish and get removed
	 * else, it will switch to the next largest crosshair indicator
	 */
	public void calculateMove() {
		countdown--;
		
		if(detonating) {
			this.die();		
		} else if(countdown <= 0) {
			detonating = true;		
			sprites = new Sprite[1][4];
			sprites[0] = loadSpriteArray("sprites/mortar/boom");
			isAnimated = true;
		} else {
			sprite = (SpriteStore.get()).getSprite("sprites/mortar/crosshair_" + (countdown - 1) + ".png");
		} // else if
	} // calculateMove
	
	// Hitbox includes tiles within diameter (3x3 tile area)
	public Rectangle getHitbox(int shiftx, int shifty) {
		Rectangle rect = new Rectangle();
		
		rect.setBounds(	(int) x - BLAST_DIAMETER / 2 + TileEntity.TILE_SIZE / 2,
						(int) y - BLAST_DIAMETER / 2 + TileEntity.TILE_SIZE / 2,
						BLAST_DIAMETER, BLAST_DIAMETER);
		
		return rect;
	} // getHitbox
	
	@Override
	public void draw(Graphics g) {
		sprite.draw(g, (int) x - TileEntity.TILE_SIZE, (int) y - TileEntity.TILE_SIZE);
	} // draw
	
	@Override
	public void collidedWith(Entity other) {
		if (detonating) {
			
			// instantly kills enemies and rewards energy
			if (other instanceof EnemyEntity) {				
				other.die();		
				game.notifyEnemyKilled();
			} // if
			
			// kills player if they walk into the blast
			if (other instanceof RobotEntity) {
				other.die();
			} // if
			
		} // if
	} // collidedWith
} // MortarEntity