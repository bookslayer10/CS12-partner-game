
public class MeleeEntity extends EnemyEntity {
	
	boolean isDamaged = false;
	public MeleeEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, 2);
	}

	public void move(long delta) {
		
		if(getHealth() < 2 && !isDamaged) {
			isDamaged = true;
			
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					sprites[i][j] = (SpriteStore.get())
							.getSprite("sprites/broken_melee/broken_melee_" + i * 90 + "_" + j + ".png");
				} // for
			} // for
			sprite = sprites[0][0];
		}
		
		// proceed with normal move
		super.move(delta);
	} // move
}
