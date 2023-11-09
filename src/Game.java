/* Game.java
 * CS12 Game - another change
 * 
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;

public class Game extends Canvas {


	private BufferStrategy strategy; // take advantage of accelerated graphics
	private boolean waitingForKeyPress = true; // true if game held up until
	// a key is pressed
	private boolean makingMove = false;
	private char keyPressed;
	private int mouseX;
	private int mouseY;

	private boolean gameRunning = true;
	protected ArrayList<TileEntity> tiles = new ArrayList<TileEntity>(); // all tiles
	private TileEntity[] spawnTiles = new TileEntity[10]; // tiles enemies can appear on
	private ArrayList<Entity> entities = new ArrayList<Entity>(); // list of entities
	// in game
	private ArrayList<Entity> removeEntities = new ArrayList<Entity>(); // list of entities
	// to remove this loop
	protected static RobotEntity robot; // the robot
	private Sprite battery; // shows remaining energy
	private Color BATTERY = new Color(51, 55, 56);

	public final int SCREEN_WIDTH = 1856;
	public final int SCREEN_HEIGHT = 960;

	private final char NONE = '0';
	private final char MOUSE = '9';
	
	protected static int[][] grid;
	private int turnNumber; // # of turns elapsed
	

	private String message = ""; // message to display while waiting
	// for a key press
	
	/*
	 * Construct our game and set it running.
	 */
	public Game() {
		// create a frame to contain game
		JFrame container = new JFrame("SUPERBOT");

		// get hold the content of the frame
		JPanel panel = (JPanel) container.getContentPane();

		// set up the resolution of the game
		panel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		panel.setLayout(null);

		// set up canvas size (this) and add to frame
		setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		panel.add(this);

		// Tell AWT not to bother repainting canvas since that will
		// be done using graphics acceleration
		setIgnoreRepaint(true);

		// make the window visible
		container.pack();
		container.setResizable(false);
		container.setVisible(true);

		// if user closes window, shutdown game and jre
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // windowClosing
		});

		// add key listener to this canvas
		addKeyListener(new KeyInputHandler());

		// add mouse listener to this canvas
		addMouseListener(new MouseInputHandler());

		// request focus so key events are handled by this canvas
		requestFocus();

		// create buffer strategy to take advantage of accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		startGame();

		// start the game
		gameLoop();
	} // constructor

	/*
	 * initEntities input: none output: none purpose: Initialise the starting state
	 * of the ship and alien entities. Each entity will be added to the array of
	 * entities in the game.
	 */
	private void initEntities() {
		grid = FileInput.getFileContents("src/grid.txt");
		int st = 0; // index of next spawnable tile
		
		// create a grid of map tiles
		for (int row = 0; row < 15; row++) {
			for (int col = 0; col < 29; col++) {
				TileEntity tile = new TileEntity(this, "sprites/background/map_"
						+ grid[row][col] + ".png", col * TileEntity.TILE_SIZE, row * TileEntity.TILE_SIZE);
				tiles.add(tile);
				
				if (tile.isSpawnable()) {
					spawnTiles[st++] = tile;
				} // if

			} // for
		} // outer for
		
		battery = (SpriteStore.get()).getSprite("sprites/battery.png");
		
		@SuppressWarnings("unused")
		ShotEntity testShot = new ShotEntity(this, "sprites/shot/shot_", 0, 0, 0);
		
		// create the robot and put in the middle of screen
		robot = new RobotEntity(this, "sprites/robot/robot_", TileEntity.TILE_SIZE * 14, TileEntity.TILE_SIZE * 7);
		entities.add(robot);
	} // initEntities
	
	/*
	 * Remove an entity from the game. It will no longer be moved or drawn.
	 */
	public void removeEntity(Entity entity) {
		removeEntities.add(entity);
	} // removeEntity

	/*
	 * Notification that the player has died.
	 */

	public void notifyDeath() {
		if (robot.getEnergy() < 1) {
			message = "You have shut down. ";
		} else {
			message = "You were defeated. ";
		}
		message = message.concat("You survived " + turnNumber + " turns. You killed " + EnemyEntity.getKilled());
		if(EnemyEntity.getKilled() == 1) {
			message = message.concat(" enemy.");
		} else {
			message = message.concat(" enemies.");
		} // else
		
		waitingForKeyPress = true;
	} // notifyDeath

	/*
	 * Notification than an alien has been killed
	 */
	public void notifyEnemyKilled() {
		EnemyEntity.setKilled(EnemyEntity.getKilled() + 1);
		EnemyEntity.setActive(EnemyEntity.getActive() - 1);
		
		// award energy on a kill
		robot.setEnergy(robot.getEnergy() + 10);
		
	} // notifyAlienKilled
	
	/* If there are no enemies in play, will spawn an enemy on a random valid 
	 * tile. There is a chance to spawn an enemy of variable type on each tile 
	 * in spawnTiles[]. The chance for additional enemies to spawn and the 
	 * chance for a ranged enemy to spawn both increase as the game goes. 
	 */
	public void spawnEnemies() {
		
		// chance for an individual tile to spawn an enemy, increases over time
		double spawnChance = 0.001  + turnNumber * 0.0002;
		
		// chance for ranged enemies to spawn, increases over time, capped at 50%
		double rangedChance = Math.min(turnNumber * 0.0004, 0.5);
		
		// !!!!!!REMOVE WHEN RANGED ENEMIES ARE SUPPORTED!!!!!!
		// !!!!!!REMOVE WHEN RANGED ENEMIES ARE SUPPORTED!!!!!!
		// !!!!!!REMOVE WHEN RANGED ENEMIES ARE SUPPORTED!!!!!!
		rangedChance = 0; 
		// !!!!!!REMOVE WHEN RANGED ENEMIES ARE SUPPORTED!!!!!!
		
		for (TileEntity tile: spawnTiles) {
			if (Math.random() > 1 - spawnChance) {
				entities.add(randomEnemy(tile.getX(), tile.getY(), rangedChance));
			} // if
		} // for
		
		//adds an enemy on a random tile if none are on screen
		if(EnemyEntity.getActive() == 0) {
			int tile = (int) (Math.random() * 10);
			entities.add(randomEnemy(spawnTiles[tile].getX(), spawnTiles[tile].getY(), rangedChance));
		} // if
		
	} // spawnEnemies
	
	/* takes x, y as a coordinate pair and a rangedChance as the double 
	 * representative of the chance for a ranged enemy to spawn and creates 
	 * an enemy of random type on the given location
	 */
	public EnemyEntity randomEnemy(int x, int y, double rangedChance) {
		EnemyEntity.setActive(EnemyEntity.getActive() + 1);
		if (Math.random() > 1 - rangedChance) {
			return new RangedEntity(this, "sprites/ranged/ranged_", x, y);
		} // is
		else {					
			return new MeleeEntity(this, "sprites/melee/melee_", x, y);
		} // else
	} // EnemyEntity

	public void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();
		// keep loop running until game ends
		while (gameRunning) {

			// calc. time since last update, will be used to calculate
			// entities movement
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();

			// get graphics context for the accelerated surface and make it black
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			
			// set making move to false, only continue if an entity in the for loop sets it
			// to true
			makingMove = false;
			if(!waitingForKeyPress) {
				for (int i = 0; i < entities.size(); i++) {
					Entity entity = (Entity) entities.get(i);
					entity.move(delta);
					if (entity.getIsMoving()) {
						makingMove = true;
					}
				} // for
				
				// brute force collisions, compare every entity
				// against every other entity. If any collisions
				// are detected notify both entities that it has
				// occurred
				for (int i = 0; i < entities.size(); i++) {
					for (int j = i + 1; j < entities.size(); j++) {
						Entity me = (Entity) entities.get(i);
						Entity him = (Entity) entities.get(j);

						if (me.collidesWith(him, 0, 0)) {
							me.collidedWith(him);
							him.collidedWith(me);
						} // if
					} // inner for
				} // outer for
			}

			// draw tiles
			for (int i = 0; i < tiles.size(); i++) {
				tiles.get(i).draw(g);
			} // for

			// draw all entities
			for (int i = 0; i < entities.size(); i++) {
				Entity entity = (Entity) entities.get(i);
				entity.draw(g);
			} // for
			
			// Draw full battery then cover up missing energy
			battery.draw(g, 64, TileEntity.TILE_SIZE * 13);
			g.setColor(BATTERY);
			double modifier = 320.0 / robot.MAX_ENERGY;
	        g.fillRect((int) (robot.getEnergy() * modifier) + 96, TileEntity.TILE_SIZE * 13 + 20, (int) (320 - robot.getEnergy() * modifier), 88);
			
			// remove dead entities
			entities.removeAll(removeEntities);
			removeEntities.clear();
			
			// if waiting for "any key press", draw message
			if (waitingForKeyPress) {
				g.setColor(Color.white);
				g.drawString(message, (800 - g.getFontMetrics().stringWidth(message)) / 2, 250);
				g.drawString("Press any key", (800 - g.getFontMetrics().stringWidth("Press any key")) / 2, 300);
			} // if
			
			// clear graphics and flip buffer
			g.dispose();
			strategy.show();

			if (!makingMove) {
				
				// if you run out of power, you die
				// only checks this when a turn is finished
				if(robot.getEnergy() < 1 && !waitingForKeyPress) {
					notifyDeath();
				}
				
				switch (keyPressed) {
				
					case 'W':
						if (robot.tryToMove(0)) {
							takeTurn();
							robot.useEnergy(2);
						} // if
						break;
					case 'D':
						if (robot.tryToMove(90)) {
							takeTurn();
							robot.useEnergy(2);
						} // if
						break;
					case 'S':
						if (robot.tryToMove(180)) {
							takeTurn();
							robot.useEnergy(2);
						} // if
						break;
					case 'A':
						if (robot.tryToMove(270)) {
							takeTurn();
							robot.useEnergy(2);
						} // if
						break;
						
					// robot waits one turn
					case 'Q':
						takeTurn();
						robot.useEnergy(1);
						break;
						
					// shoots on click
					case MOUSE:
						
						mouseX -= robot.getX() + TileEntity.TILE_SIZE / 2;
						mouseY -= robot.getY() + TileEntity.TILE_SIZE / 2;
						
						// 0 to 180 to -0
						double directionOfShot = Math.toDegrees(Math.atan2((double) mouseY, (double)mouseX));
						
						directionOfShot += 90;
						
						// turn it into full 360
						if(directionOfShot < 0) {
							directionOfShot = 360 + directionOfShot;
						}
						
						directionOfShot = directionOfShot / 360 * 8;
						
						directionOfShot = Math.round(directionOfShot) % 8;
						
						directionOfShot = directionOfShot * 360 / 8;
	
						entities.add(new ShotEntity(this, "sprites/shot/shot_", robot.getX(), robot.getY(), (int) directionOfShot));
						takeTurn();
						robot.useEnergy(4);
						robot.setDirection((int) directionOfShot);
						break;
					default:
						keyPressed = NONE;
				} // switch
				
			} // if (!makingMove

			// pause
			try {
				Thread.sleep(10);
			} catch (Exception e) {
			} // catch

		} // while

	} // gameLoop

	/*
	 * startGame input: none output: none purpose: start a fresh game, clear old
	 * data
	 */
	private void startGame() {
		// clear out any existing entities and initalize a new set
		entities.clear();
		
		EnemyEntity.setKilled(0);
		EnemyEntity.setActive(0);
		turnNumber = 0;

		// blank out any keyboard settings that might exist
		keyPressed = NONE;
		
		initEntities();
		robot.setEnergy(robot.MAX_ENERGY);
	} // startGame

	private void takeTurn() {
		keyPressed = NONE;
		turnNumber++;
		
		// spawn enemies on the entrance tiles
		spawnEnemies();
		
		// set every entity goal position, make them start moving
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity) entities.get(i);
			
			if (entity instanceof EnemyEntity) {
				entity.calculateMove();
			}
			
			if (entity instanceof ShotEntity) {
				entity.calculateMove();
			}

		} // for
	}

	/*
	 * inner class KeyInputHandler handles keyboard input from the user
	 */
	private class KeyInputHandler extends KeyAdapter {

		private int pressCount = 1; // the number of key presses since
									// waiting for 'any' key press

		/*
		 * The following methods are required for any class that extends the abstract
		 * class KeyAdapter. They handle keyPressed, keyReleased and keyTyped events.
		 */
		public void keyPressed(KeyEvent e) {

			// if waiting for keypress to start game, do nothing
			if (waitingForKeyPress) {
				return;
			} // if

			keyPressed = (char) e.getKeyCode();

			// make sure not to activate mouse clicks when pressing 9
			if (keyPressed == MOUSE) {
				keyPressed = NONE;
			}
		} // keyPressed

		public void keyTyped(KeyEvent e) {

			// if waiting for key press to start game
			if (waitingForKeyPress) {
				if (pressCount == 1) {
					waitingForKeyPress = false;
					startGame();
					pressCount = 0;
				} else {
					pressCount++;
				} // else
			} // if waitingForKeyPress

		} // keyTyped

	} // class KeyInputHandler

	/*
	 * inner class KeyInputHandler handles keyboard input from the user
	 */
	private class MouseInputHandler implements MouseListener {

		public void mousePressed(MouseEvent e) {
			// if waiting for keypress to start game, do nothing
			if (waitingForKeyPress) {
				return;
			} // if
			
			keyPressed = MOUSE;
			mouseX = e.getX();
			mouseY = e.getY();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	} // class KeyInputHandler

	/**
	 * Main Program
	 */
	public static void main(String[] args) {
		// instantiate this object
		new Game();
	} // main
} // Game
