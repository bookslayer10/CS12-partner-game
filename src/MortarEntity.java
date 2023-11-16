import java.awt.Graphics;
import java.awt.Rectangle;

public class MortarEntity extends Entity {
	
	private static final int BLAST_DIAMETER = 130;
	private boolean detonating = false;
	private int countdown;
	
	// MortarEntity constructor
	public MortarEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, false); // calls the constructor in Entity
		countdown = 4;
	} // default constructor
	
	@Override
	public void calculateMove() {
		countdown--;
		
		if(detonating) {
			this.die();		
		} else if(countdown <= 0) {
			detonating = true;
			sprites = new Sprite[1][4];
			sprites[0] = loadSpriteArray("sprites/mortar/boom");
			direction = 0;
			isAnimated = true;
		} else {
			sprite = (SpriteStore.get()).getSprite("sprites/mortar/crosshair_" + (countdown - 1) + ".png");
		} // else if
	} // calculateMove
	
	@Override
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
			// if it has hit an alien, kill it!
			if (other instanceof EnemyEntity) {
				
				((EnemyEntity) other).addHealth(-3);
				
				if(((EnemyEntity) other).getHealth() < 1) {
					other.die();
					game.notifyEnemyKilled();
					game.awardEnergy(12);
				} // if
			} // if
			
			if (other instanceof RobotEntity) {
				game.notifyDeath();
			} // if
			
		} // if
	} // collidedWith
} // MortarEntity