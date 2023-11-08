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

	private static char NONE = '0';
	private static char MOUSE = '9';

	private BufferStrategy strategy; // take advantage of accelerated graphics
	private boolean waitingForKeyPress = true; // true if game held up until
	// a key is pressed
	private boolean makingMove = false;
	private char keyPressed;
	private int mouseX;
	private int mouseY;

	private boolean gameRunning = true;
	protected ArrayList<TileEntity> tiles = new ArrayList<TileEntity>(); // all tiles
	private ArrayList<Entity> entities = new ArrayList<Entity>(); // list of entities
	// in game
	private ArrayList<Entity> removeEntities = new ArrayList<Entity>(); // list of entities
	// to remove this loop
	private RobotEntity robot; // the robot

	public final int SCREEN_WIDTH = 1856;
	public final int SCREEN_HEIGHT = 960;
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
		String[] grid = new String[16];
		grid = FileInput.getFileContents("src/grid.txt");
		// create a grid of map tiles
		for (int row = 0; row < 15; row++) {
			for (int col = 0; col < 29; col++) {
				TileEntity tile = new TileEntity(this, "sprites/background/map_" + grid[row].charAt(col) + ".png", col * TileEntity.TILE_SIZE,
						row * TileEntity.TILE_SIZE);
				tiles.add(tile);
			} // for
		} // outer for
		
		EnemyEntity[] enemies = new EnemyEntity[5];
		for (int i = 0; i < 5; i++) {
			enemies[i] = new MeleeEntity(this, "sprites/melee/melee_", TileEntity.TILE_SIZE * (i + 3), TileEntity.TILE_SIZE * 2);
			entities.add(enemies[i]);
			EnemyEntity.setActive(EnemyEntity.getActive() + 1);
		}
		
		@SuppressWarnings("unused")
		ShotEntity testShot = new ShotEntity(this, "sprites/shot/shot_", 0, 0, 0);
		
		// create the ship and put in the top right of screen
		robot = new RobotEntity(this, "sprites/robot/robot_", TileEntity.TILE_SIZE * 10, TileEntity.TILE_SIZE * 10);
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
		message = "You have shut down. You survived " + turnNumber + " turns. You killed " + EnemyEntity.getKilled();
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
		robot.setEnergy(robot.getEnergy() + 20);
		
	} // notifyAlienKilled

//	/* Attempt to fire. */
//	public void tryToFire() {
//
//		//  add a shot (MOVING STRAIGHT UP AT 64 SPD)
//		ShotEntity shot = new ShotEntity(this, "sprites/shot/shot_", robot.getX(), robot.getY(), 0, -TILE_SIZE);
//		entities.add(shot);
//	} // tryToFire
	
	public void spawnEnemies() {
		
		double spawnChance = 0.1  + turnNumber * 0.005;
		double rangedChance = -0.1 + turnNumber * 0.02;
		
		TileEntity[] spawnTiles = new TileEntity[10];
		for (TileEntity tile: spawnTiles) {
			if (Math.random() > 1 - spawnChance) {
				entities.add(new MeleeEntity(this, "sprites/melee/melee_", tile.getX(), tile.getY()));
				EnemyEntity.setActive(EnemyEntity.getActive() + 1);
//				once ranged enemies are added
//				
//				if (Math.random() > 1 - rangedChance) {
//					entities.add(new RangedEntity(this, "sprites/melee/melee_", tile.getX(), tile.getY()));
//				}
//				else {					
//					entities.add(new MeleeEntity(this, "sprites/melee/melee_", tile.getX(), tile.getY()));
//				}
			} // if
		} // for
	} // spawnEnemies

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
			g.setColor(Color.black);
			g.fillRect(0, 0, 800, 600);

			
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
			
			// remove dead entities
			entities.removeAll(removeEntities);
			removeEntities.clear();
			
			// if waiting for "any key press", draw message
			if (waitingForKeyPress) {
				g.setColor(Color.white);
				g.drawString(message, (800 - g.getFontMetrics().stringWidth(message)) / 2, 250);
				g.drawString("Press any key", (800 - g.getFontMetrics().stringWidth("Press any key")) / 2, 300);
			} // if
			
			// if you run out of power, you die
			if(robot.getEnergy() < 1) {
				notifyDeath();
			}
			
			// clear graphics and flip buffer
			g.dispose();
			strategy.show();

			if (!makingMove) {

				// respond to user moving ship
				if (keyPressed == 'W') {
					if (robot.tryToMove(0)) {
						takeTurn();
						robot.setEnergy(robot.getEnergy() - 1);
					} // if

				} else if (keyPressed == 'D') {
					if (robot.tryToMove(90)) {
						takeTurn();
						robot.setEnergy(robot.getEnergy() - 1);
					} // if

				} else if (keyPressed == 'S') {
					if (robot.tryToMove(180)) {
						takeTurn();
						robot.setEnergy(robot.getEnergy() - 1);
					} // if

				} else if (keyPressed == 'A') {
					if (robot.tryToMove(270)) {
						takeTurn();
						robot.setEnergy(robot.getEnergy() - 1);
					} // if

					
				} else if (keyPressed == MOUSE) {
					
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
					robot.setEnergy(robot.getEnergy() - 3);
				}
			} else {
				keyPressed = NONE;
			}

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
	} // startGame

	private void takeTurn() {
		keyPressed = NONE;
		turnNumber++;
		
		System.out.println(robot.getEnergy());
		
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
			//System.out.println("Mouse pressed; position: " + e.getX() + " " + e.getY());
			
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
