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


	protected BufferStrategy strategy; // take advantage of accelerated graphics
	private boolean waitingForKeyPress = true; // true if game held up until
	// a key is pressed
	protected boolean makingMove = false;
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
	private double directionOfShot;
	private Sprite controls; // shows remaining energy
	private Sprite battery; // shows remaining energy
	private final Color BATTERY = new Color(51, 55, 56);
	private final Color BACKGROUND = new Color(51, 55, 56, 127);
	public final Color LASER = new Color(188, 241, 247, 50);
	private int introSlidesLeft = 5;
	Font instructionsFont = new Font( "Monospaced",  Font.PLAIN, 18);
	Font headerFont = new Font( "Monospaced", Font.BOLD, 30);
	private Sprite[] arrows = new Sprite[8];
	public final int SCREEN_WIDTH = 1856;
	public final int SCREEN_HEIGHT = 960;

	private final char NONE = '0';
	
	protected static int[][] grid;
	protected static String[][] instructions;
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
		
		// add mouse listener to this canvas
		addMouseMotionListener(new MouseMotionHandler());

		// request focus so key events are handled by this canvas
		requestFocus();

		// create buffer strategy to take advantage of accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		grid = FileInput.getMapContents("src/grid.txt");
		int st = 0; // index of next spawnable tile
		
		instructions = FileInput.getInstructions("src/instructions.txt");
		
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
		
		// create the robot and put in the middle of screen
		
		battery = (SpriteStore.get()).getSprite("sprites/battery.png");
		controls = (SpriteStore.get()).getSprite("sprites/controls.png");
		
		@SuppressWarnings("unused")
		ShotEntity testShot = new ShotEntity(this, "sprites/shot/shot_", 0, 0, 0);
		
		for(int i = 0; i < 8; i++) {
			arrows[i] = (SpriteStore.get()).getSprite("sprites/arrow/arrow_" + 45 * i + ".png");
		}
		
		startGame();

		// start the game
		gameLoop();
	} // constructor
	
	/*
	 * Add an entity to the game. It will be moved and drawn.
	 */
	public void addEntity(Entity entity) {
		entities.add(entity);
	} // addEntity
	
	/*
	 * Remove an entity from the game. It will no longer be moved or drawn.
	 */
	public void removeEntity(Entity entity) {
		removeEntities.add(entity);
	} // removeEntity
	
	
	public void drawInstructions(Graphics2D g, int slide) {	
		
		g.setColor(BATTERY);
		g.fillRect(300, 200, SCREEN_WIDTH - 600, SCREEN_HEIGHT - 400);
		
		g.setColor(Color.white);
		
		int maxLineWidth = 0;
		
		for (String line : instructions[5 - slide]) {
			maxLineWidth = Math.max(maxLineWidth, g.getFontMetrics().stringWidth(line));
		} // for
		
		for (int i = 0; i < instructions[5 - slide].length; i++) {
			String line = instructions[5 - slide][i];
			
				
			if (i == 0) {
				g.setFont(headerFont);
			} else {
				g.setFont(instructionsFont);
			} // else
			
			if (line == null) {
				line = "";
			}
			
			g.drawString(line, (SCREEN_WIDTH - (i < 13 && i > 0 ? maxLineWidth : g.getFontMetrics().stringWidth(line))) / 2, 320 + i * 25);
			g.setFont(instructionsFont);
		}
	}
	

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
			message = message.concat(" enemy. ");
		} else {
			message = message.concat(" enemies. ");
		} // else
		
		int score = turnNumber * 3 + EnemyEntity.getKilled() * 2;
		
		message = message.concat("Your score was: " + score);
		
		waitingForKeyPress = true;
	} // notifyDeath

	/*
	 * Notification than an alien has been killed
	 */
	public void notifyEnemyKilled() {
		EnemyEntity.setKilled(EnemyEntity.getKilled() + 1);
		EnemyEntity.setActive(EnemyEntity.getActive() - 1);
	} // notifyAlienKilled
	
	// award energy on a kill with a shot
	public void awardEnergy(int energy) {
		robot.useEnergy(-energy);
	} // awardEnergy
	
	/* If there are no enemies in play, will spawn an enemy on a random valid 
	 * tile. There is a chance to spawn an enemy of variable type on each tile 
	 * in spawnTiles[]. The chance for additional enemies to spawn and the 
	 * chance for a ranged enemy to spawn both increase as the game goes. 
	 */
	public void spawnEnemies() {
		
		// chance for an individual tile to spawn an enemy, increases over time
		double spawnChance = 0.002  + turnNumber * 0.0004;
		
		// chance for ranged enemies to spawn, increases over time, capped at 50%
		double rangedChance = 1;// Math.min(turnNumber * 0.005, 0.5);
		
		for (TileEntity tile: spawnTiles) {
			if (Math.random() > 1 - spawnChance) {
				entities.add(randomEnemy(tile.getX(), tile.getY(), rangedChance));
			} // if
		} // for
		
		//adds an enemy on a random tile if none are on screen
		if(EnemyEntity.getActive() <= 1) {
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
			} // if(!waitingForKeyPress)

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
				
				g.setColor(BACKGROUND);
				g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
				g.setFont(instructionsFont);
				
				
				if (introSlidesLeft > 0) {
					drawInstructions(g, introSlidesLeft);
				} else {
					
					
					g.setColor(BATTERY);
					g.fillRect(100, 400, SCREEN_WIDTH - 200, SCREEN_HEIGHT - 800);
					
					g.setColor(Color.white);
					g.setFont(headerFont);
					g.drawString(message, (SCREEN_WIDTH - g.getFontMetrics().stringWidth(message)) / 2, SCREEN_HEIGHT / 2 - 20);
					g.drawString("Press any key to continue.",
						(SCREEN_WIDTH - g.getFontMetrics().stringWidth("Press any key to continue.")) / 2, SCREEN_HEIGHT / 2 + 20);
				}
														
			} else {
				// 0 to 180 to -0
				directionOfShot = Math.toDegrees(Math.atan2(	(double) mouseY - (robot.getY() + TileEntity.TILE_SIZE / 2),
																	(double) mouseX - (robot.getX() + TileEntity.TILE_SIZE / 2)));
				
				directionOfShot += 90;
				
				// turn it into full 360
				if(directionOfShot < 0) {
					directionOfShot = 360 + directionOfShot;
				}
				
				directionOfShot = directionOfShot / 360 * 8;			
				directionOfShot = Math.round(directionOfShot) % 8;				
				arrows[(int) directionOfShot].draw(g, robot.getX() - TileEntity.TILE_SIZE, robot.getY() - TileEntity.TILE_SIZE);				
				directionOfShot = directionOfShot * 360 / 8;
				
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
							robot.useEnergy(0);
							break;
							
						// shoots shot on left click
						case (char) MouseEvent.BUTTON1:
							
							entities.add(new ShotEntity(this, "sprites/shot/shot_", robot.getX(), robot.getY(), (int) directionOfShot));
							takeTurn();
							robot.useEnergy(4);
							robot.setDirection((int) directionOfShot);
							break;
							
						// mortar fire on right click
						case (char) MouseEvent.BUTTON3:
							
							int mortarX = mouseX / TileEntity.TILE_SIZE;
							int mortarY = mouseY / TileEntity.TILE_SIZE;
							
							mortarX *= TileEntity.TILE_SIZE;
							mortarY *= TileEntity.TILE_SIZE;
							
							entities.add(new MortarEntity(this, "sprites/mortar/crosshair_1.png", mortarX, mortarY));
							takeTurn();
							robot.useEnergy(16);
							robot.setDirection((int) directionOfShot);
							
							//g.fillRect(mortarX - 58, mortarY - 58, 180, 180);
							
							break;
							
						default:
							keyPressed = NONE;
					} // switch
					
				} // if (!makingMove
				
			} // else
			
			controls.draw(g, 23 * TileEntity.TILE_SIZE , TileEntity.TILE_SIZE * 13);
			
			// Draw full battery then cover up missing energy
			battery.draw(g, 64, TileEntity.TILE_SIZE * 13);
			g.setColor(BATTERY);
			double modifier = 320.0 / robot.MAX_ENERGY;
	        	g.fillRect((int) (robot.getEnergy() * modifier) + 96, TileEntity.TILE_SIZE * 13 + 20, (int) (320 - robot.getEnergy() * modifier), 88);
			
			// clear graphics and flip buffer
			g.dispose();
			strategy.show();
			
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
		robot = new RobotEntity(this, "sprites/robot/robot_", TileEntity.TILE_SIZE * 14, TileEntity.TILE_SIZE * 7);
		entities.add(robot);
		
		EnemyEntity.setKilled(0);
		EnemyEntity.setActive(0);
		turnNumber = 0;

		// blank out any keyboard settings that might exist
		keyPressed = NONE;
		
		robot.setEnergy(robot.MAX_ENERGY);
		message = "Press any key to continue.";
	} // startGame

	private void takeTurn() {
		keyPressed = NONE;
		turnNumber++;
		
		// spawn enemies on the entrance tiles
		spawnEnemies();
		
		// set every entity goal position, make them start moving
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = (Entity) entities.get(i);
			
			if (!(entity instanceof RobotEntity)) {
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
			
		} // keyPressed

		public void keyTyped(KeyEvent e) {

			// if waiting for key press to start game
			if (waitingForKeyPress) {
				
				introSlidesLeft -= 1;
				if (introSlidesLeft > 0) {
					return;
				}
				
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
			// changes instruction slides at start
			if (waitingForKeyPress) {
				
				// goes backwards in instructions on right-click
				if ((char) e.getButton() == (char) MouseEvent.BUTTON3 && introSlidesLeft > 0) {
					introSlidesLeft += 1;
					if (introSlidesLeft > 5) {
						introSlidesLeft = 5;
					} // if
					return;
					
				// otherwise advances slides
				} else {
					introSlidesLeft -= 1;
					if (introSlidesLeft > 0) {
						return;
					}
				}
				
				
							
				waitingForKeyPress = false;
				startGame();
			
			} else {		
			keyPressed = (char) e.getButton();
			}
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			

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

	private class MouseMotionHandler implements MouseMotionListener {
		
		@Override
		public void mouseDragged(MouseEvent e) {
			//mouseX = e.getX();
			//mouseY = e.getY();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
		}
		
	}
	
	/**
	 * Main Program
	 */
	public static void main(String[] args) {
		// instantiate this object
		new Game();
	} // main
} // Game
