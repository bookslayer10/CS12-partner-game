
public class MortarEntity extends Entity {
	
	private boolean used = false; // true if shot hits something
	
	public MortarEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, false); // calls the constructor in Entity
	}

	@Override
	public void collidedWith(Entity other) {
		// prevents double kills
		if (used) {
			return;
		} // if

		// if it has hit an alien, kill it!
		if (other instanceof EnemyEntity) {
			// remove affect entities from the Entity list
			game.removeEntity(this);
			
			// remove affect entities from the Entity list
			game.removeEntity(this);
			
			((EnemyEntity) other).addHealth(-3);
			
			if(((EnemyEntity) other).getHealth() < 1) {
				game.removeEntity(this);
				game.notifyEnemyKilled();
			} // if
			
			used = true;
		} // if

	}
	
	
	
	
	
	
}
