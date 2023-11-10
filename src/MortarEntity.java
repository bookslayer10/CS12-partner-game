import java.awt.Rectangle;

public class MortarEntity extends Entity {
	
	private boolean detonating = false;
	private int countdown = 3;
	
	
	public MortarEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, false); // calls the constructor in Entity
	}
	
	@Override
	public void calculateMove() {
		countdown--;
		
		if(countdown < 1) {
			detonating = true;
		}
	}
	
	@Override
	public Rectangle getHitbox(int shiftx, int shifty) {
		Rectangle rect = new Rectangle();
		
		rect.setBounds((int) x + shiftx, (int) y + shifty, sprite.getWidth(), sprite.getHeight());
		
		return rect;
	}
	
	@Override
	public void collidedWith(Entity other) {
		if (!detonating) {
			// when the shot detonates, remove it next frame
			game.removeEntity(this);
			
			// if it has hit an alien, kill it!
			if (other instanceof EnemyEntity) {
				
				((EnemyEntity) other).addHealth(-3);
				
				if(((EnemyEntity) other).getHealth() < 1) {
					game.removeEntity(this);
					game.notifyEnemyKilled();
				} // if
			} // if
		} // if
	} // collidedWith
} // MortarEntity