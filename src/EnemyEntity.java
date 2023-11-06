import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/* AlienEntity.java
 * March 27, 2006
 * Represents one of the aliens
 */
public class EnemyEntity extends Entity {
	/*
	 * construct a new alien input: game - the game in which the alien is being
	 * created r - the image representing the alien x, y - initial location of alien
	 */
	public EnemyEntity(Game g, String r, int newX, int newY) {
		super(g, r, newX, newY, true); // calls the constructor in Entity
		direction = 2;
	} // constructor

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move alien
	 */
	public void move(long delta) {
		
		// proceed with normal move
		super.move(delta);
	} // move

	public void calculateMove() {
		
		switch (direction) {
			case 0: // up
				dx = 0;
				dy = -64;
				break;
			case 1: // right
				dx = 64;
				dy = 0;
				break;
			case 2: // down
				dx = 0;
				dy = 64;
				break;
			case 3: // left
				dx = -64;
				dy = 0;
				break;
		} // switch
		
		super.calculateMove();
	} // calculate
	
	/*
	 * doLogic Updates the game logic related to the aliens, ie. move it down the
	 * screen and change direction
	 */
	public void doLogic() {
		// swap horizontal direction and move down screen 10 pixels
		dx *= -1;
		y += 10;

		// if bottom of screen reached, player dies
		if (y > 570) {
			game.notifyDeath();
		} // if
	} // doLogic
	
    public static class Point {
        public int x;
        public int y;
        public Point previous;

        public Point(int x, int y, Point previous) {
            this.x = x;
            this.y = y;
            this.previous = previous;
        } // Point

        public boolean equals(Object o) {
            Point point = (Point) o;
            return x == point.x && y == point.y;
        } // equals

        public int hashCode() {
        	return Objects.hash(x, y);
        } // hashCode

        public Point offset(int ox, int oy) {
        	return new Point(x + ox, y + oy, this);
        } // offset
        
    } // Point

    private boolean IsWalkable(Point point) {
        if (point.y < 0 || point.y > Game.grid.length - 1) return false;
        if (point.x < 0 || point.x > Game.grid[0].length - 1) return false;
        return Game.grid[point.y][point.x] == 0;
    }

    private List<Point> FindNeighbors(Point point) {
        List<Point> neighbors = new ArrayList<>();
        Point up = point.offset(0,  1);
        Point down = point.offset(0,  -1);
        Point left = point.offset(-1, 0);
        Point right = point.offset(1, 0);
        if (IsWalkable(up)) neighbors.add(up);
        if (IsWalkable(down)) neighbors.add(down);
        if (IsWalkable(left)) neighbors.add(left);
        if (IsWalkable(right)) neighbors.add(right);
        return neighbors;
    }

    private List<Point> FindPath(Point start, Point end) {
        boolean finished = false;
        List<Point> used = new ArrayList<>();
        used.add(start);
        while (!finished) {
            List<Point> newOpen = new ArrayList<>();
            for(int i = 0; i < used.size(); ++i){
                Point point = used.get(i);
                for (Point neighbor : FindNeighbors(point)) {
                    if (!used.contains(neighbor) && !newOpen.contains(neighbor)) {
                        newOpen.add(neighbor);
                    }
                }
            }

            for(Point point : newOpen) {
                used.add(point);
                if (end.equals(point)) {
                    finished = true;
                    break;
                }
            }

            if (!finished && newOpen.isEmpty())
                return null;
        }

        List<Point> path = new ArrayList<>();
        Point point = used.get(used.size() - 1);
        while(point.previous != null) {
            path.add(0, point);
            point = point.previous;
        }
        return path;
    }
	
	/*
	 * collidedWith input: other - the entity with which the alien has collided
	 * purpose: notification that the alien has collided with something
	 */
	public void collidedWith(Entity other) {
		// collisions with aliens are handled in ShotEntity and RobotEntity
	} // collidedWith

} // AlienEntity class
