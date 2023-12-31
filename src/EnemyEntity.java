import java.awt.Rectangle;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/* AlienEntity.java
 * March 27, 2006
 * Represents one of the aliens
 */
public class EnemyEntity extends Entity {

	private static int killed;
	private static int active;
	private int health;

	/*
	 * construct a new alien input: game - the game in which the alien is being
	 * created r - the image representing the alien x, y - initial location of alien
	 */
	public EnemyEntity(Game g, String r, int newX, int newY, int health) {
		super(g, r, newX, newY, true); // calls the constructor in Entity
		this.health = health;
		
		// Sets direction based on which edge of the screen the enemy is at
		if (x == g.SCREEN_WIDTH - TileEntity.TILE_SIZE) {
			direction = 270;
		} else if (x == 0) {
			direction = 90;
		} else if (y == g.SCREEN_HEIGHT - TileEntity.TILE_SIZE) {
			direction = 0;
		} else {
			direction = 180;
		} // else

	} // constructor

	public void calculateMove() {
		Point point = findPath(this, Game.robot).get(0);

		if (point != null) {
			dx = point.x * 64 - this.x;
			dy = point.y * 64 - this.y;
		} // if
		
		if (dx > 0) {
			direction = 90;
		} else if (dx < 0) {
			direction = 270;
		} else if (dy > 0) {
			direction = 180;
		} else if (dy < 0) {
			direction = 0;
		} // if

		super.calculateMove();
	} // calculate

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
		if (point.y < 0 || point.y > Game.grid.length - 1)
			return false;
		if (point.x < 0 || point.x > Game.grid[0].length - 1)
			return false;
		return Game.grid[point.y][point.x] == 0;
	}

	private List<Point> findNeighbours(Point point) {
		List<Point> neighbors = new ArrayList<>();
		Point up = point.offset(0, 1);
		Point down = point.offset(0, -1);
		Point left = point.offset(-1, 0);
		Point right = point.offset(1, 0);
		if (isWalkable(up))
			neighbors.add(up);
		if (isWalkable(down))
			neighbors.add(down);
		if (isWalkable(left))
			neighbors.add(left);
		if (isWalkable(right))
			neighbors.add(right);
		return neighbors;
	}

	protected List<Point> findPath(Entity enemy, Entity robot) {
		Point start = new Point(enemy.getX() / TileEntity.TILE_SIZE, enemy.getY() / TileEntity.TILE_SIZE, null);
		Point end = new Point(robot.getX() / TileEntity.TILE_SIZE, robot.getY() / TileEntity.TILE_SIZE, null);
		// System.out.println("[" + start.x + "][" + start.y + "]");

		boolean finished = false;
		List<Point> used = new ArrayList<>();
		used.add(start);
		while (!finished) {
			// System.out.println("Used: " + used.size());
			List<Point> newOpen = new ArrayList<>();
			for (int i = 0; i < used.size(); ++i) {
				Point point = used.get(i);
				for (Point neighbor : findNeighbours(point)) {
					if (!used.contains(neighbor) && !newOpen.contains(neighbor)) {
						newOpen.add(neighbor);
					} // if
				} // for
			} // for

			for (int i = 0; i < newOpen.size(); i++) {
				used.add(newOpen.get(i));
				if (end.equals(newOpen.get(i))) {
					finished = true;
					break;
				} // for
			} // if

			if (!finished && newOpen.isEmpty()) {
				return null;
			}
		} // while

		List<Point> path = new ArrayList<>();
		Point point = used.get(used.size() - 1);

		while (!point.equals(start)) {
			path.add(0, point);
			point = point.previous;
		} // while

		return path;

	} // findPath

	/*
	 * collidedWith input: other - the entity with which the alien has collided
	 * purpose: notification that the alien has collided with something
	 */
	public void collidedWith(Entity other) {
		if(other instanceof EnemyEntity) {
			game.removeEntity(this);
			game.notifyEnemyKilled();
		}
	} // collidedWith

	@Override
	public Rectangle getHitbox(int shiftx, int shifty) {
		Rectangle rect = new Rectangle();

		rect.setBounds((int) x + sprite.getWidth() / 4 + shiftx, (int) y + sprite.getHeight() / 4 + shifty,
				sprite.getWidth() / 2, sprite.getHeight() / 2);

		return rect;
	} // getHitbox

	public static void setKilled(int killed) {
		if (killed < 0) {
			System.out.println("Negative number of killed enemies!");
			EnemyEntity.killed = 0;
		} else {
			EnemyEntity.killed = killed;
		} // else
	} // setKilled

	public static void setActive(int active) {
		if (active < 0) {
			System.out.println("Negative number of active enemies!");
			EnemyEntity.active = 0;
		} else {
			EnemyEntity.active = active;
		} // else 
	} // setActive

	public static int getKilled() {
		return killed;
	} // getKilled

	public static int getActive() {
		return active;
	} // getActive

	public void setHealth(int health) {
		this.health = health;
	} // setHealth

	public int getHealth() {
		return health;
	} // getHealth

	public void addHealth(int healthChange) {
		setHealth(getHealth() + healthChange);
	} // addHealth

} // EnemyEntity class
