
public class MeleeEntity extends EnemyEntity {

	public MeleeEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY);
		
	}
	
	// stopgap method that contains Entity's calculateMove() (copypasted) and just makes enemy walk towards center
	public void calculateMove() {
		
		switch (direction) {
		case 0:
			dx = 0;
			dy = -TileEntity.TILE_SIZE;
			break;	
		case 90:
			dx = TileEntity.TILE_SIZE;
			dy = 0;
			break;		
		case 180:
			dx = 0;
			dy = TileEntity.TILE_SIZE;
			break;		
		case 270:
			dx = -TileEntity.TILE_SIZE;
			dy = 0;
			break;
		}
		
		turnTargetX = x + dx;
		turnTargetY = y + dy;
		
		int goalTileIndex = (((int) (y + dy) / TileEntity.TILE_SIZE) * 29 + (int) (x + dx) / TileEntity.TILE_SIZE);
		if (goalTileIndex < 0) {
			goalTileIndex = 0;
		}
		
		goalTile = game.tiles.get(goalTileIndex);
	}

}
