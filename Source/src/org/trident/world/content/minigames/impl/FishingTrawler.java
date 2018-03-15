package org.trident.world.content.minigames.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Flag;
import org.trident.model.GameObject;
import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.minigames.impl.FishingTrawler.WallHandler.Wall;
import org.trident.world.entity.player.Player;

/** 
 * FishingTrawler minigame
 * @author Gabbe
 * Credits to -MrC for base/object ids etc
 */
public class FishingTrawler {

	/**
	 * @note Stores player and State
	 */
	public static Map<Player, String> playerMap = Collections.synchronizedMap(new HashMap<Player, String>());

	/**
	 * @return HashMap Value
	 */
	public static String getState(Player player) {
		return playerMap.get(player);
	}

	/**
	 * @note States of minigames
	 */
	public static final String PLAYING = "PLAYING";
	public static final String WAITING = "WAITING";

	/**
	 * The amount of players in the boat (waiting area)
	 */
	private static int playersInBoat, playersInGame;

	/**
	 * Is a game running?
	 */
	private static boolean gameRunning;

	/**
	 * Timers
	 */
	private static final int DEFAULT_GAME_TIMER = 240, DEFAULT_WAIT_TIMER = 60;
	private static int GAME_TIMER, WAIT_TIMER = DEFAULT_WAIT_TIMER;
	private static int PROPER_TIMER;

	/**
	 * Game modifiers
	 */
	private static boolean netRipped;
	private static boolean shipIsSunk;
	private static boolean[] wallBroken = new boolean[16];
	private static int fishCaught;
	private static int waterLevel;

	/**
	 * The random object instance
	 */
	private static final Random RANDOM_GEN = new Random();

	/**
	 * The minigame's objects
	 */
	private static final int PERFECT_WALL = 2177;
	private static final int PATCHED_WALL = 2168;
	private static final int LEAKING_WALL = 2167;
	private static ArrayList<GameObject> spawnedObjects = new ArrayList<GameObject>();

	/**
	 * The minigame's items
	 */
	private static final int ROPE = 954;
	private static final int SWAMP_PASTE = 1941;
	private static final int BAILING_BUCKET_FULL = 585;
	private static final int BAILING_BUCKET_EMPTY = 583;

	/**
	 * Moves a player in to the boat (waiting area)
	 * and adds the player to the map.
	 * @param p			The player entering
	 */
	public static void boardBoat(Player p) {
		if(p.getSkillManager().getMaxLevel(Skill.FISHING) < 50) {
			p.getPacketSender().sendMessage("You need a Fishing level of at least 50 to board this boat.");
			return;
		}
		if(p.getAdvancedSkills().getSummoning().getFamiliar() != null) {
			p.getPacketSender().sendMessage("Familiars are not allowed on this boat.");
			return;
		}
		if(getState(p) == null)
			playerMap.put(p, WAITING);
		p.moveTo(new Position(2672, 3170, 1));
		p.getPacketSender().sendString(21008, "(Need 2 to 20 players)");
		p.getPacketSender().sendString(21009, "");
	}

	/**
	 * Moves the player out of the boat (waiting area)
	 * and removes the player from the map.
	 * @param p			The player leaving
	 */
	public static void leave(Player p) {
		String state = getState(p);
		if(state != null)
			playerMap.remove(p);
		p.moveTo(new Position(2676, 3170));
	}

	/**
	 * Processes the minigame
	 */
	public static void process() {
		setPlayers();
		if(playersInBoat == 0 && !gameRunning)
			return;
		if(WAIT_TIMER > 0) {
			WAIT_TIMER--;
			updateBoatInterface();
		} else { 
			startGame();
			WAIT_TIMER = DEFAULT_WAIT_TIMER;
			return;
		}
		if(!gameRunning)
			return;
		int gameEndingType = getGameEnd();
		if(gameEndingType == 0) {
			GAME_TIMER--;
			updateGameInterface();
			if(PROPER_TIMER == 10) {
				int random = RANDOM_GEN.nextInt(2) + 1;
				for (int j = 0; j < random; j++)
					WallHandler.breakRandomWall();
				NetHandler.ripNet();
				WaterHandler.increaseWaterLevel();
				ShipHandler.switchBoats();
				if(!netRipped)
					RewardsHandler.increaseFish();
				PROPER_TIMER = 0;
			}
			PROPER_TIMER++;
		} else
			endGame(gameEndingType);
	}

	/**
	 * Starts a new game
	 */
	private static void startGame() {
		if(playersInBoat >= 2 && !gameRunning) {
			gameRunning = true;
			GAME_TIMER = DEFAULT_GAME_TIMER;
			netRipped = shipIsSunk = false;
			waterLevel = fishCaught = 0;
			for (int j = 0; j < wallBroken.length; j++)
				wallBroken[j] = false;
			WallHandler.resetWalls();
			for (Player player : playerMap.keySet()) {
				if (player != null) {
					String state = getState(player);
					if(state != null && state.equals(WAITING)) {
						player.getMovementQueue().stopMovement();
						player.getPacketSender().removeTabs();
						player.getPacketSender().sendMapState(2);
						player.getPacketSender().sendInterface(3281);
						player.getPacketSender().sendConfig(75, 11);
						player.moveTo(new Position(1885, 4825, 1));
						player.getPacketSender().sendString(11936, "");
						playerMap.put(player, PLAYING);
					}
				}
			}
			TaskManager.submit(new Task(2, false) {
				@Override
				public void execute() {
					for (Player player : playerMap.keySet()) {
						if (player != null) {
							String state = getState(player);
							if(state != null && state.equals(PLAYING)) {
								player.getMovementQueue().stopMovement();
								player.getPacketSender().sendTabs();
								player.getPacketSender().sendInterface(5596);
								player.getPacketSender().sendMapState(0);
								player.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().setAmountOfActions(0);
							}
						}
					}
					stop();
				}
			});
			return;
		} else {
			String msg = gameRunning ? "A current game is going on already. It will end within "+GAME_TIMER+" seconds." : "There need to be at least 2 players in the trawler to begin.";
			for(Player player : playerMap.keySet()) {
				if(player != null) {
					String state = getState(player);
					if(state != null && state.equals(WAITING))
						player.getPacketSender().sendMessage(msg);
				}
			}
			msg = null;
		}
	}

	private static void endGame(int gameEndingType) {
		WAIT_TIMER = DEFAULT_WAIT_TIMER;
		GAME_TIMER = DEFAULT_GAME_TIMER;
		gameRunning = false;
		switch(gameEndingType) {
		case 1: //Water was too much, game lost
			for (Player player : playerMap.keySet()) {
				if (player != null) {
					String state = getState(player);
					if(state != null && state.equals(PLAYING)) {
						player.moveTo(new Position(1952, 4826));
						player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(773).setCrossingObstacle(true);
						player.performAnimation(new Animation(773));
						player.getUpdateFlag().flag(Flag.APPEARANCE);
						player.getPacketSender().sendMessage("The boat sunk under water..");
					}
				}
			}
			TaskManager.submit(new Task(6, false) {
				@Override
				public void execute() {
					for (Player player : playerMap.keySet()) {
						if (player != null) {
							String state = getState(player);
							if(state != null && state.equals(PLAYING)) {
								player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(-1).setCrossingObstacle(false);
								player.performAnimation(new Animation(65535));
								player.getUpdateFlag().flag(Flag.APPEARANCE);
								player.moveTo(new Position(2676, 3169));
								player.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().setAmountOfActions(0);
							}
						}
					}
					resetMap();
					stop();
				}
			});
			break;
		case 2: //Timer ran out, game won
			for (Player player : playerMap.keySet()) {
				if (player != null) {
					String state = getState(player);
					if(state != null && state.equals(PLAYING)) {
						player.moveTo(new Position(2676, 3169));
						player.getPacketSender().sendMessage("Well done. Enjoy the catch!");
						player.getAttributes().setClickDelay(System.currentTimeMillis());
						player.getPacketSender().sendInterfaceRemoval();
						player.getMovementQueue().stopMovement();
						if(player.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getAmountOfActions() >= 3) {
							player.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().setRewards(RewardsHandler.getReward(player));
							RewardsHandler.showReward(player);
							player.getSkillManager().addExperience(Skill.FISHING, 5000 * player.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getAmountOfActions(), false);
							player.getPacketSender().sendMessage("You've received some Fishing experience and some raw fish!");
						} else
							player.getPacketSender().sendMessage("You didn't do much to earn a share of the catch.");
						player.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().setAmountOfActions(0);
					}
				}
			}
			resetMap();
			break;
		default:
			resetMap();
			break;
		}
		WallHandler.handleObject(null, "", true);
	}

	/**
	 * Resets the player map
	 */
	private static void resetMap() {
		playerMap.clear();
		for(Player p : World.getPlayers()) {
			if(p != null && p.getLocation() != null && p.getLocation() == Location.TRAWLER_BOAT)
				playerMap.put(p, WAITING);
		}
	}

	/**
	 * Gets the game ending
	 * @return 0 if no players ingame, 1 if boat is sunk, 2 if game is won
	 */
	private static int getGameEnd() {
		if (playersInGame == 0)
			return 1;
		if (waterLevel >= 100)
			return 1;
		if (GAME_TIMER == 0)
			return 2;
		return 0;
	}

	/**
	 * Updates the boat (waiting area) interface for every player in it.
	 */
	private static void updateBoatInterface() {
		for (Player p : playerMap.keySet()) {
			if(p == null)
				continue;
			String state = getState(p);
			if(state != null && state.equals(WAITING)) {
				p.getPacketSender().sendString(21006, "Next Departure: "+WAIT_TIMER+"");
				p.getPacketSender().sendString(21007, "Players Ready: "+playersInBoat+"");
			}
		}
	}

	/**
	 * Updates the game interface for every player in the boat.
	 */
	private static void updateGameInterface() {
		for (Player p : playerMap.keySet()) {
			if(p == null)
				continue;
			String state = getState(p);
			if(state != null && state.equals(PLAYING)) {
				p.getPacketSender().sendString(11935, netRipped ? "@red@Ripped" : "@gre@Healthy");
				p.getPacketSender().sendString(11937, ""+fishCaught);
				p.getPacketSender().sendString(11938, ""+GAME_TIMER);
				p.getPacketSender().sendConfig(391, waterLevel);
			}
		}
	}

	/**
	 * Gets amount of players which have the specified state
	 * @param st		The state which the player must have to pass the check
	 * @return			The amount of players in the minigame with that state
	 */
	private static void setPlayers() {
		playersInBoat = playersInGame = 0;
		for(Player player : playerMap.keySet()) {
			if(player != null) {
				String state = getState(player);
				if(state == null)
					continue;
				if(state.equals(WAITING))
					playersInBoat++;
				else if(state.equals(PLAYING))
					playersInGame++;
			}
		}
	}

	/*
	 * Returns true if the player can complete the interaction
	 */
	private static boolean doAction(Player p) {
		String state = getState(p);
		if (state == null || state != PLAYING)
			return false;
		if(p.getSkillManager().getSkillAttributes().getAgilityAttributes().isCrossingObstacle())
			return false;
		if (System.currentTimeMillis() - p.getAttributes().getClickDelay() >= 1600) {
			p.getAttributes().setClickDelay(System.currentTimeMillis());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * All wall-methods go in here
	 */
	public static class WallHandler {
		/**
		 * Generates the indexes of the walls that are not broken
		 */
		public static int[] getAvaliableWalls() {
			int[] toReturn = new int[getAvaliableWallSize()];
			int index = 0;
			for (int j = 0; j < wallBroken.length; j++) {
				if (wallBroken[j] == false) {
					toReturn[index] = j;
					index++;
				}
			}
			return toReturn;
		}

		/**
		 * Gets the amount of working(not broken) walls
		 * @return
		 */
		public static int getAvaliableWallSize() {
			int toReturn = 0;
			for (int j = 0; j < wallBroken.length; j++) {
				if (wallBroken[j] == false) {
					toReturn++;
				}
			}
			return toReturn;
		}

		/**
		 * Sets a random wall as broken & updates it
		 */
		public static void breakRandomWall() {
			try {
				final int[] walls = getAvaliableWalls();
				int random = walls[RANDOM_GEN.nextInt(walls.length)];
				wallBroken[random] = true;
				updateWall(random);
			} catch (Exception e) {// Exception should never occur

			}
		}

		/**
		 * Resets the boat's walls
		 */
		public static void resetWalls() {
			for(Wall w : Wall.values()) {
				if(w != null)
					CustomObjects.spawnGlobalObjectWithinDistance(new GameObject(PERFECT_WALL, new Position(w.x,w.y, w.y == 4826 ? 1 : 3)));
			}
		}

		/**
		 * Updates a wall based on index
		 * @param index 	The index of the wall which should be updated
		 */
		public static void updateWall(int index) {
			Wall w = Wall.getWallByIndex(index, shipIsSunk);
			if (w == null)
				return;
			GameObject object = CustomObjects.getGameObject(new Position(w.x, w.y));
			if(object == null)
				object = new GameObject(-1, new Position(w.x, w.y));
			handleObject(object, "add", false);
			object = new GameObject(-1, new Position(w.x + (shipIsSunk ? -128 : 128), w.y));
			handleObject(object, "add", false);
			if (wallBroken[index] == true) {
				GameObject leakingWall = new GameObject(LEAKING_WALL, new Position(w.x, w.y), 10, w.y == 4826 ? 1 : 3);
				handleObject(leakingWall, "add", false);
				if (shipIsSunk) {
					leakingWall = new GameObject(LEAKING_WALL, new Position(w.x - 128, w.y), 10, w.y == 4826 ? 1 : 3);
					handleObject(leakingWall, "add", false);
				} else {
					leakingWall = new GameObject(LEAKING_WALL, new Position(w.x + 128, w.y), 10, w.y == 4826 ? 1 : 3);
					handleObject(leakingWall, "add", false);
				}
				leakingWall = null;
			} else {
				GameObject patchedWall = new GameObject(PATCHED_WALL, new Position(w.x, w.y), 10, w.y == 4826 ? 1 : 3);
				handleObject(patchedWall, "add", false);
				if (shipIsSunk) {
					patchedWall = new GameObject(PATCHED_WALL, new Position(w.x- 128, w.y), 10, w.y == 4826 ? 1 : 3);
					handleObject(patchedWall, "add", false);
				} else {
					patchedWall = new GameObject(PATCHED_WALL, new Position(w.x + 128, w.y), 10, w.y == 4826 ? 1 : 3);
					handleObject(patchedWall, "add", false);
				}
			}
		}

		/**
		 * Spawns an object in the minigame
		 * @param obj
		 */
		public static void handleObject(GameObject obj, String type, boolean resetList) {
			if(type.equals("spawn")) {
				for(GameObject obj2 : spawnedObjects) {
					for(Player player : playerMap.keySet()) {
						if(player != null) {
							String state = getState(player);
							if(state == null || state.equals(WAITING))
								continue;
							if(player.getPosition().getZ() != obj2.getPosition().getZ())
								continue;
							if(obj2.getId() > 0)
								player.getPacketSender().sendObject(obj2);
							else
								player.getPacketSender().sendObjectRemoval(obj2);
						}
					}
				}
				return;
			}
			if(type.equals("add")) {
				spawnedObjects.add(obj);
				for(Player player : playerMap.keySet()) {
					if(player != null) {
						String state = getState(player);
						if(state == null || state.equals(WAITING))
							continue;
						if(player.getPosition().getZ() != obj.getPosition().getZ())
							continue;
						if(obj.getId() > 0)
							player.getPacketSender().sendObject(obj);
						else
							player.getPacketSender().sendObjectRemoval(obj);
					}
				}
			} 
			if(resetList)
				spawnedObjects.clear();
		}

		/**
		 * The wall enum contains the coordinates for each wall, since all of the objects of the
		 * same id's and the methods needed to get them
		 * @author -MrC
		 */
		public enum Wall {

			North_One_Normal(0, 1885, 4826), North_Two_Normal(1, 1886, 4826), North_Three_Normal(
					2, 1887, 4826), North_Four_Normal(3, 1888, 4826), North_Five_Normal(
							4, 1889, 4826), North_Six_Normal(5, 1890, 4826), North_Seven_Normal(
									6, 1891, 4826), North_Eight_Normal(7, 1892, 4826), South_One_Normal(
											8, 1885, 4823), South_Two_Normal(9, 1886, 4823), South_Three_Normal(
													10, 1887, 4823), South_Four_Normal(11, 1888, 4823), South_Five_Normal(
															12, 1889, 4823), South_Six_Normal(13, 1890, 4823), South_Seven_Normal(
																	14, 1891, 4823), South_Eight_Normal(15, 1892, 4823), North_One_Sinking(
																			0, 2013, 4826), North_Two_Sinking(1, 2014, 4826), North_Three_Sinking(
																					2, 2015, 4826), North_Four_Sinking(3, 2016, 4826), North_Five_Sinking(
																							4, 2017, 4826), North_Six_Sinking(5, 2018, 4826), North_Seven_Sinking(
																									6, 2019, 4826), North_Eight_Sinking(7, 2020, 4826), South_One_Sinking(
																											8, 2013, 4823), South_Two_Sinking(9, 2014, 4823), South_Three_Sinking(
																													10, 2015, 4823), South_Four_Sinking(11, 2016, 4823), South_Five_Sinking(
																															12, 2017, 4823), South_Six_Sinking(13, 2018, 4823), South_Seven_Sinking(
																																	14, 2019, 4823), South_Eight_Sinking(15, 2020, 4823);

			int index, x, y;

			Wall(int index, int x, int y) {
				this.index = index;
				this.y = y;
				this.x = x;
			}

			public static int getIndex(int x, int y) {
				for (Wall s : Wall.values()) {
					if (s != null) {
						if (s.x == x && s.y == y) {
							return s.index;
						}
					}
				}
				return -1;
			}

			public static Wall getWallByIndex(int index, boolean sinking) {
				for (Wall w : Wall.values()) {
					if (w.index == index) {
						if (sinking && w.x < 2000) {
							continue;
						} else {
							return w;
						}
					}
				}
				return null;
			}

		}
	}

	/**
	 * Handles events related to the net
	 * @author Gabbe
	 */
	public static class NetHandler {
		/**
		 * Rips the net
		 */
		public static void ripNet() {
			if (!netRipped && RANDOM_GEN.nextInt(10) > 7)
				netRipped = true;
		}

		/**
		 * Fixes the net on the boat
		 * @param p 	The player which is going to try to fix the net
		 */
		public static void fixNet(Player p) {
			if (doAction(p)) {
				if (!netRipped) {
					p.getPacketSender().sendMessage("The net is not ripped.");
					return;
				}
				if (!p.getInventory().contains(ROPE)) {
					p.getPacketSender().sendMessage("You need a rope before attempting to fix the net!");
					return;
				}
				p.performAnimation(new Animation(832));
				p.getInventory().delete(ROPE, 1);
				if (fixNetRandomizer(p.getSkillManager().getCurrentLevel(Skill.CRAFTING), 1, 0)) {
					netRipped = false;
					p.getPacketSender().sendMessage("You successfully fix the net!");
					p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().setAmountOfActions(p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getAmountOfActions() + 1);
				} else
					p.getPacketSender().sendMessage("You failed to repair the net!");
			}
		}

		/*
		 * Randomly returns true, players craft level increases chance of returning
		 * true
		 */
		public static boolean fixNetRandomizer(int level, int levelRequired, int itemBonus) {
			double chance = 0.0;
			double baseChance = Math.pow(10d - levelRequired / 10d, 2d) / 2d;
			chance = baseChance + ((level - levelRequired) / 2d) + (itemBonus / 10d);
			return chance >= (new Random().nextDouble() * 100.0);
		}
	}

	/**
	 * Handles events related to water
	 * @author Gabbe
	 *
	 */
	public static class WaterHandler {
		/**
		 * Adds water depending on the amount of leaks in the ship
		 */
		public static void increaseWaterLevel() {
			int leaks = 16 - WallHandler.getAvaliableWalls().length;
			waterLevel += (leaks / 2) + RANDOM_GEN.nextInt(leaks * RANDOM_GEN.nextInt(2) + 1);
		}
	}

	/**
	 * Handles actions related to the ship such as ladders etc
	 * @author Gabbe
	 *
	 */
	public static class ShipHandler {
		/**
		 * Bails water out of the ship
		 * @param p	The player which is going to bail water.
		 */
		public static void bailWater(Player p) {
			if (doAction(p)) {
				if (p.getInventory().contains(BAILING_BUCKET_EMPTY)) {
					if(p.getPosition().getZ() != 0) {
						p.getPacketSender().sendMessage("There is no water to bail here.");
						return;
					}
					p.performAnimation(new Animation(827));
					p.getInventory().setItem(p.getInventory().getSlot(BAILING_BUCKET_EMPTY), new Item(BAILING_BUCKET_FULL));
					p.getInventory().refreshItems();
					waterLevel -= RANDOM_GEN.nextInt(3) + 1;

				}
			}
		}
		/**
		 * Emptying a bucket of water
		 * @param p	The player which is going to empty water.
		 */
		public static void emptyFullBucket(Player p) {
			if (doAction(p)) {
				if (p.getInventory().contains(BAILING_BUCKET_FULL)) {
					p.performAnimation(new Animation(832));
					p.getInventory().setItem(p.getInventory().getSlot(BAILING_BUCKET_FULL), new Item(BAILING_BUCKET_EMPTY));
					p.getInventory().refreshItems();
					waterLevel -= RANDOM_GEN.nextInt(3) + 1;
				}
			}
		}
		/**
		 * Switches the boat to one filled with water/empty
		 */
		public static void switchBoats() {
			if (waterLevel > 25 && !shipIsSunk) {
				shipIsSunk = true;
				for(Player player : playerMap.keySet()) {
					if(player != null) {
						String state = getState(player);
						if(state != null && state.equals(PLAYING)) {
							player.getMovementQueue().stopMovement();
							player.moveTo(new Position(player.getPosition().getX() + 128, player.getPosition().getY(), player.getPosition().getZ()));
						}
					}
				}
			}
			if (waterLevel <= 25 && shipIsSunk) {
				shipIsSunk = false;
				for(Player player : playerMap.keySet()) {
					if(player != null) {
						String state = getState(player);
						if(state != null && state.equals(PLAYING)) {
							player.getMovementQueue().stopMovement();
							player.moveTo(new Position(player.getPosition().getX() - 128, player.getPosition().getY(), player.getPosition().getZ()));
						}
					}
				}
			}
		}
		/**
		 * Fixes a hole in the ship
		 * @param p the player to fix the hole
		 * @param x the x of the hole
		 * @param y the y of the hole
		 */
		public static void fixHole(Player p, int x, int y) {
			if (doAction(p)) {
				if (p.getInventory().contains(SWAMP_PASTE)) {
					int index = Wall.getIndex(x, y);
					if (index >= 0) {
						p.getInventory().delete(SWAMP_PASTE, 1);
						p.performAnimation(new Animation(832));
						wallBroken[index] = false;
						WallHandler.updateWall(index);
						p.setPositionToFace(new Position(x, y + (y == 4826 ? 1 : -1)));

					}
				} else {
					p.getPacketSender().sendMessage("You don't have any Swamp paste to patch this hole with.");
				}
			}
		}

		/**
		 * Moves a player up the boat ladder
		 * @param p		The player going up the ladder
		 * @param obX	The ladder's x
		 * @param obY	The ladder's y
		 */
		public static void goUpLadder(final Player p, final int obX, final int obY) {
			if (doAction(p)) {
				p.performAnimation(new Animation(828));
				TaskManager.submit(new Task(1, p, false) {
					@Override
					public void execute() {
						p.getMovementQueue().stopMovement();
						p.moveTo(!shipIsSunk ? new Position(obX == 1884 ? 1885 : 1892, obY, 1) : new Position(obX == 2021 ? 2020 : 2013, obY, 1));
						stop();
					}
				});
			}
		}

		/**
		 * Moves a player down the boat ladder
		 * @param p		The player going up the ladder
		 * @param obX	The ladder's x
		 * @param obY	The ladder's y
		 */
		public static void goDownLadder(final Player p, final int obX, final int obY) {
			if (doAction(p)) {
				p.performAnimation(new Animation(827));
				TaskManager.submit(new Task(1, p, false) {
					@Override
					public void execute() {
						p.getMovementQueue().stopMovement();
						p.moveTo(!shipIsSunk ? new Position(obX == 1884 ? 1885 : 1892, obY) : new Position(obX == 2021 ? 2020 : 2013, obY));
						WallHandler.handleObject(null, "spawn", false);
						stop();
					}
				});
			}
		}
	}

	/**
	 * Handles events related to rewards
	 * @author Gabbe
	 */
	public static class RewardsHandler {
		/**
		 * Increases the fish reward during a game
		 */
		public static void increaseFish() {
			fishCaught += RANDOM_GEN.nextInt(playersInGame + 2);
		}
		/**
		 * Shows a player's reward
		 * @param p	The player to show reward for
		 */
		public static void showReward(Player p) {
			resetRewardInterface(p);
			for(int j = 0; j < p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().size(); j++)
				p.getPacketSender().sendItemOnInterface(4640, p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().get(j).getId(), j, p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().get(j).getAmount());
			p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().setViewingInterface(true);
			p.getPacketSender().sendInterface(4564);
		}
		/**
		 * Resets the reward interface for a player by sending -1 items to it
		 * @param p		the player to reset the interface for
		 */
		public static void resetRewardInterface(Player p) {
			for(int j = 0; j < 45; j++)
				p.getPacketSender().sendItemOnInterface(4640, -1, j, -1);
		}
		public static void withdrawRewardItem(Player p, int item, int slot, boolean all) {
			if(!p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().isViewingInterface() || p.getAttributes().isBanking() || p.getAttributes().isShopping() || p.getAttributes().isPriceChecking())
				return;
			boolean containsItem = false;
			for(Item it : p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards()) {
				if(it != null && it.getId() == item) {
					containsItem = true;
					break;
				}
			}
			if(!containsItem)
				return;
			if (p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().get(slot).getAmount() >= 1) {
				p.getInventory().add(p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().get(slot).getId(), all ? p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().get(slot).getAmount() : 1);
				if(all) {
					p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().remove(slot);
					showReward(p);
					return;
				}
				p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().get(slot).setAmount(p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().get(slot).getAmount() - 1);;
				if (p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().get(slot).getAmount() <= 0) {
					p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().remove(slot);
					showReward(p);
				} else
					updateRewardSlot(p, slot);
			}
		}
		/**
		 * Updates a single slot in the reward interface
		 * @param c		
		 * @param slot
		 */
		public static void updateRewardSlot(Player p, int slot) {
			p.getPacketSender().sendItemOnInterface(4640, p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().get(slot).getId(), slot, p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().get(slot).getAmount());
			if(slot != 4 && p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().size() == 5)
				p.getPacketSender().sendItemOnInterface(4640, p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().get(4).getId(), 4, p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getRewards().get(4).getAmount());
		}
		/**
		 * Gets random fish and Fishing experience as reward for player.
		 * @param p
		 * @return
		 */
		public static ArrayList<Item> getReward(Player p) {
			ArrayList<Item> toReturn = new ArrayList<Item>();
			boolean turtles = true;
			boolean mantas = true;
			boolean lobsters = true;
			boolean swordfish = true;
			int turt = 0;
			int manta = 0;
			int lobs = 0;
			int swordFish = 0;
			int junk = 0;
			int done = 0;
			fishCaught = 6 + 3 + Misc.getRandom(p.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().getAmountOfActions());
			while (done != fishCaught) {
				done++;
				int random = RANDOM_GEN.nextInt(100);
				if (random >= 85 - chanceByLevel(p, 381)) {
					if (mantas) {
						manta++;
					}
				} else if (random >= 70 - chanceByLevel(p, 381)) {
					if (turtles) {
						turt++;
					}
				} else if (random >= 40) {
					if (swordfish) {
						swordFish++;
					}
				} else if (random >= 5) {
					if (lobsters) {
						lobs++;
					}
				} else {
					junk++;
				}
			}
			int xpToAdd = 0;
			if (manta > 0) {
				toReturn.add(new Item(389, manta));
				if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= 81) {
					xpToAdd += (manta * 46 * 6);
				}
			}
			if (turt > 0) {
				toReturn.add(new Item(395, turt));
				if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= 79) {
					xpToAdd += (manta * 38 * 6);
				}
			}
			if (lobs > 0) {
				toReturn.add(new Item(377, lobs));
				if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= 40) {
					xpToAdd += (manta * 90 * 6);
				}
			}
			if (swordFish > 0) {
				toReturn.add(new Item(371, swordFish));
				if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= 50) {
					xpToAdd += (manta * 100 * 6);
				}
			}
			if (junk > 0)
				toReturn.add(new Item(685, junk));
			p.getSkillManager().addExperience(Skill.FISHING, xpToAdd, false);
			return toReturn;
		}

		/*
		 * Slightly increases chance of higher level fish with levels
		 */
		public static int chanceByLevel(Player p, int fish) {
			switch (fish) {
			case 381:
				if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= 81
				&& p.getSkillManager().getCurrentLevel(Skill.FISHING) < 90) {
					return 5;
				} else if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= 90
						&& p.getSkillManager().getCurrentLevel(Skill.FISHING) < 99) {
					return 9;
				} else if (p.getSkillManager().getCurrentLevel(Skill.FISHING) == 99) {
					return 13;
				}
				return 0;
			case 395:
				if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= 79
				&& p.getSkillManager().getCurrentLevel(Skill.FISHING) < 85) {
					return 8;
				} else if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= 85
						&& p.getSkillManager().getCurrentLevel(Skill.FISHING) < 95) {
					return 13;
				} else if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= 95) {
					return 17;
				}
				return 0;
			}
			return 0;
		}
	}
}