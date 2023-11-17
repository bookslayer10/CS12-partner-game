/* MeleeEntity.java
 * An EnemyEntity that pathfinds towards the player and deals contact damage
 * Spawns with 2 health, and shows when it is damaged by switching to a separate sprite
 */

public class MeleeEntity extends EnemyEntity {
	
	boolean isDamaged = false;
	public MeleeEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, 2);
	} // constructor
	
	// if the melee entity gets damaged during its turn, reloads sprite arrays with "broken" animations
	public void move(long delta) {
		
		// only loads arrays once
		if(getHealth() < 2 && !isDamaged) {
			isDamaged = true;
						
			for (int i = 0; i < 4; i++) {
				sprites[i] = loadSpriteArray("sprites/broken_melee/broken_melee_" + i * 90);
			} // for
			sprite = sprites[0][0];
		
		} // if
		
		// proceed with normal move
		super.move(delta);
	} // move
	
} // MeleeEntity