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
		Point point = findPath(this, Game.robot);
		dx = point.x - this.x;
		dy = point.y - this.y;
		
		
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
        private int x;
        private int y;
        private Point previous;

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

    private boolean isWalkable(Point point) {
        if (point.y < 0 || point.y > Game.grid.length - 1) return false;
        if (point.x < 0 || point.x > Game.grid[0].length - 1) return false;
        return Game.grid[point.y][point.x] == 0;
    }

    private List<Point> findNeighbours(Point point) {
        List<Point> neighbors = new ArrayList<>();
        Point up = point.offset(0,  1);
        Point down = point.offset(0,  -1);
        Point left = point.offset(-1, 0);
        Point right = point.offset(1, 0);
        if (isWalkable(up)) neighbors.add(up);
        if (isWalkable(down)) neighbors.add(down);
        if (isWalkable(left)) neighbors.add(left);
        if (isWalkable(right)) neighbors.add(right);
        return neighbors;
    }

    private Point findPath(Entity enemy, Entity robot) {
    	Point start = new Point(enemy.getX() / 29, enemy.getY() / 15, null);
    	Point end = new Point(robot.getX() / 29, robot.getY() / 15, null);

        boolean finished = false;
        List<Point> used = new ArrayList<>();
        used.add(start);
        while (!finished) {
            List<Point> newOpen = new ArrayList<>();
            for (int i = 0; i < used.size(); ++i){
                Point point = used.get(i);
                for (Point neighbor : findNeighbours(point)) {
                    if (!used.contains(neighbor) && !newOpen.contains(neighbor)) {
                        newOpen.add(neighbor);
                    } // if
                } // for
            } // for

            for (Point point : newOpen) {
                used.add(point);
                if (end.equals(point)) {
                    finished = true;
                    break;
                } // for
            } // if

            if (finished && newOpen.isEmpty())
                return null;
        } // while

        List<Point> path = new ArrayList<>();
        Point point = used.get(used.size() - 1);
        while (point.previous != null) {
            path.add(0, point);
            point = point.previous;
        } // while
        return path.get(0);
    }
	
	/*
	 * collidedWith input: other - the entity with which the alien has collided
	 * purpose: notification that the alien has collided with something
	 */
	public void collidedWith(Entity other) {
		// collisions with aliens are handled in ShotEntity and RobotEntity
	} // collidedWith

} // AlienEntity class
