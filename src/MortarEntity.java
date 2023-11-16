import java.awt.Rectangle;

public class MortarEntity extends Entity {
	
	private static final int BLAST_DIAMETER = 170;
	private boolean detonating = false;
	private int countdown = 4;
	
	
	public MortarEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, false); // calls the constructor in Entity
	}
	
	@Override
	public void calculateMove() {
		countdown--;
		
		if(detonating) {
			game.removeEntity(this);
		} else if(countdown < 1) {
			detonating = true;
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
	public void collidedWith(Entity other) {
		if (detonating) {
			// if it has hit an alien, kill it!
			if (other instanceof EnemyEntity) {
				
				((EnemyEntity) other).addHealth(-3);
				
				if(((EnemyEntity) other).getHealth() < 1) {
					game.removeEntity(other);
					game.notifyEnemyKilled();
					game.awardEnergy(5);
				} // if
			} // if
			
			if (other instanceof RobotEntity) {
				game.notifyDeath();
			} // if
			
		} // if
	} // collidedWith
} // MortarEntity