
public class MeleeEntity extends EnemyEntity {
	
	boolean isDamaged = false;
	public MeleeEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, 2);
	} // constructor
	
	// if the melee entity gets damaged during its turn, switch the sprite list to the broken sprites
	public void move(long delta) {
		
		// only activates a single time
		if(getHealth() < 2 && !isDamaged) {
			isDamaged = true;
			
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					sprites[i][j] = (SpriteStore.get())
							.getSprite("sprites/broken_melee/broken_melee_" + i * 90 + "_" + j + ".png");
				} // for
			} // for
			sprite = sprites[0][0];
		} // if
		
		// proceed with normal move
		super.move(delta);
	} // move
	
	// if melee entity runs into another melee entity, 
	public void collidedWith(Entity other) {
		if(other instanceof MeleeEntity) {
			game.removeEntity(this);
			game.notifyEnemyKilled();
		}
	} // collidedWith
} // MeleeEntity