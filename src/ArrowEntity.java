
public class ArrowEntity extends Entity {

	public ArrowEntity (Game g, String r, int newX, int newY, boolean isAnimated) {
		super(g, r, newX, newY, true); // calls the constructor in Entity
	}

	@Override
	public void collidedWith(Entity other) {
		// No collision, ever
	}
	
	
}
