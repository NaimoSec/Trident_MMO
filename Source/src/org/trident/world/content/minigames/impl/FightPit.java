package org.trident.world.content.minigames.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.trident.model.Position;
import org.trident.util.Misc;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.entity.player.Player;

public class FightPit {

	/**
	 * @note States of minigames
	 */
	private static final String PLAYING = "PLAYING";
	private static final String WAITING = "WAITING";
	/**
	 * @note Current fight pits champion
	 */
	private static String pitsChampion = "None";
	/**
	 *@note Countdown for game to start
	 */
	private static int gameStartTimer = 80;
	/**
	 * @note Elapsed Game start time
	 */
	private static int elapsedGameTime = 0;
	private static final int END_GAME_TIME = 400;
	/*
	 * @note Game started or not?
	 */
	private static boolean gameStarted = false;

	/**
	 * @note Stores player and State
	 */
	private static Map<Player, String> playerMap = Collections.synchronizedMap(new HashMap<Player, String>());

	/**
	 * @note Where to spawn when pits game starts
	 */
	private static final int MINIGAME_START_POINT_X = 2392;
	private static final int MINIGAME_START_POINT_Y = 5139;
	/**
	 * @note Exit game area
	 */
	private static final int EXIT_GAME_X = 2399;
	private static final int EXIT_GAME_Y = 5169;
	/**
	 * @note Exit waiting room
	 */
	public static final int EXIT_WAITING_X = 2399;
	public static final int EXIT_WAITING_Y = 5177;
	/**
	 * @note Waiting room coordinates
	 */
	private static final int WAITING_ROOM_X = 2399;
	private static final int WAITING_ROOM_Y = 5175;

	/**
	 * @return HashMap Value
	 */
	public static String getState(Player player) {
		return playerMap.get(player);
	}

	private static final int TOKKUL_ID = 6529;

	/**
	 *@note Adds player to waiting room.
	 */
	public static void addPlayer(Player player) {
		playerMap.put(player, WAITING);
		player.moveTo(new Position(WAITING_ROOM_X, WAITING_ROOM_Y, 0));
	}

	/**
	 * @note Starts the game and moves players to arena
	 */
	private static void enterGame(Player player) {
		boolean l = getState(player) == null || getState(player).equals(WAITING);
		if(l)
			playerMap.put(player, PLAYING);
		int teleportToX = MINIGAME_START_POINT_X + Misc.getRandom(12);
		int teleportToY = MINIGAME_START_POINT_Y + Misc.getRandom(12);
		if(!player.getMovementQueue().canWalk(player.getPosition().getX() - teleportToX,  player.getPosition().getY() -  teleportToY)) {
			teleportToX = MINIGAME_START_POINT_X + Misc.getRandom(3);
			teleportToY = MINIGAME_START_POINT_Y + Misc.getRandom(3);
		}
		if(l)
			player.moveTo(new Position(teleportToX, teleportToY, 0));
		player.followCharacter(null);
	}

	/**
	 * @note Removes player from pits if they're in waiting or in game
	 */
	public static void removePlayer(Player player, String removeReason) {
		switch(removeReason.toLowerCase()) {
		case "death":
			player.moveTo(new Position(WAITING_ROOM_X, WAITING_ROOM_Y, 0));
			playerMap.remove(player);
			playerMap.put(player, WAITING);
			endingGame();
			break;
		case "leave room":
			player.moveTo(new Position(EXIT_WAITING_X, EXIT_WAITING_Y, 0));
			if(playerMap.containsKey(player))
				playerMap.remove(player);
			//	player.getPacketSender().sendWalkableInterface(-1);
			break;
		case "leave game":
			player.moveTo(new Position(EXIT_GAME_X, EXIT_GAME_Y, 0));
			playerMap.remove(player);
			playerMap.put(player, WAITING);
			endingGame();
			break;
		case "logout":
			playerMap.remove(player);
			endingGame();
			break;
		}
	}

	public static boolean endingGame() {
		for (Player player : playerMap.keySet()) {
			if(player != null) {
				if(getListCount("PLAYING") == 1 && getState(player) != null && getState(player).equals("PLAYING")) {
					pitsChampion = player.getUsername();
					player.getPacketSender().sendMessage("You're the master of the pit!");
					//player.moveTo(new Position(WAITING_ROOM_X, WAITING_ROOM_Y, 0));
					//playerMap.remove(player);
					//playerMap.put(player, WAITING);
					player.getAppearance().setBountyHunterSkull(21);
					endGame();
					CombatHandler.resetAttack(player);
					boolean giveTokkul = playerMap.size() >= 5;
					if(giveTokkul) {
						int amount = 500 + (200 * playerMap.size()) + Misc.getRandom(500);
						player.getInventory().add(TOKKUL_ID, amount);
						DialogueManager.start(player, 359);
					} else
						DialogueManager.start(player, 360);
					return true;
				}
			}
		}
		return false;
	}

	public static void endGame() {
		if(gameStarted) {
			for (Player player : playerMap.keySet()) {
				if(player != null) {
					if(FightPit.getState(player) != null && FightPit.getState(player).equals("PLAYING")) {
						player.moveTo(new Position(WAITING_ROOM_X, WAITING_ROOM_Y, 0));
						playerMap.remove(player);
						playerMap.put(player, WAITING);
						CombatHandler.resetAttack(player);
					}
				}
			}
		}
		elapsedGameTime = 0;
		gameStarted = false;
		gameStartTimer = 80;
	}

	/**
	 * @return Players playing fight pits
	 */
	public static int getListCount(String state) {
		int count = 0;
		for (String s : playerMap.values()) {
			if(state == s) {
				count++; 
			}
		}
		return count;
	}

	/**
	 * @note Updates waiting room interfaces etplayer.
	 */
	public static boolean updateWaitingRoom(Player player) {
		player.getPacketSender().sendString(2805, "Next Game Begins In : " + gameStartTimer);
		player.getPacketSender().sendString(2806, "Champion: " + pitsChampion);
		if(player.getAttributes().getWalkableInterfaceId() != 2804) {
			player.getPacketSender().sendWalkableInterface(2804).sendConfig(560, 1);
		}
		return true;
	}

	/**
	 * @note Updates players in game interfaces etplayer.
	 */
	public static boolean updateGame(Player player) {
		player.getPacketSender().sendString(2805, "Foes Remaining: " + getListCount(PLAYING));
		player.getPacketSender().sendString(2806, "Champion: " + pitsChampion);
		if(player.getAttributes().getWalkableInterfaceId() != 2804) {
			player.getPacketSender().sendWalkableInterface(2804).sendConfig(560, 1);
		}
		return true;
	}

	/*
	 * @process 600ms Tick
	 */
	public static void process() {
		if (!gameStarted) {
			if (gameStartTimer > 0) {
				gameStartTimer--;
			} else if (gameStartTimer == 0) {
				if (getListCount(WAITING) > 1 || getListCount(WAITING) == 1 && getListCount(PLAYING) == 1)
					beginGame();
				gameStartTimer = 80;
			}
		}
		if (gameStarted) {
			elapsedGameTime++;
			if (elapsedGameTime == END_GAME_TIME) {
				endGame();
				elapsedGameTime = 0;
				gameStarted = false;
				gameStartTimer = 80;
			}
		}
	}

	/**
	 * @note Starts game for the players in waiting room
	 */
	private static void beginGame() {
		for (Player player : playerMap.keySet()) {
			enterGame(player);
		}
	}

	public static boolean inFightPits(final Player player) {
		if(FightPit.getState(player) != null && FightPit.getState(player).equals("PLAYING"))
			return true;
		return false;
	}

	/**
	 * Orb viewing
	 */

	public static void viewOrb(Player player) {
		/*if(!Locations.inPitsWaitRoom(player) || player.viewingOrb || Locations.inPits(player))
    		return;
    	for(int i = 0; i < org.Desolace.util.Constants.SIDEBAR_INTERFACES.length; i++)
    		player.getPacketSender().sendTabInterface(i, -1);
    	player.getPacketSender().sendTabInterface(4, 3209);
    	player.viewingOrb = true;
    	player.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);*/
	}

	public static void cancelOrbView(Player player) {
		/*if(!player.viewingOrb)
    		return;
    	player.viewingOrb = false;
    	for(int i = 0; i < org.Desolace.util.Constants.SIDEBAR_INTERFACES.length; i++)
    		player.getPacketSender().sendTabInterface(i, org.Desolace.util.Constants.SIDEBAR_INTERFACES[i]);
    	player.getPacketSender().sendTabInterface(Constants.PRAYER_TAB, player.getPrayerBook().getInterfaceId());
    	player.getPacketSender().sendTabInterface(Constants.MAGIC_TAB, player.getSpellbook().getInterfaceId());
    	player.getPacketSender().sendCameraNeutrality();
    	player.getMovementQueue().setMovementStatus(MovementStatus.NONE);*/
	}

	public static void viewOrbLocation(Player player, Position pos, int cameraAngle) {
		/*if(!Locations.inPitsWaitRoom(player) || !player.viewingOrb || Locations.inPits(player))
    		return;*/
		//player.getPacketSender().sendCameraAngle(pos, 5, cameraAngle);
	}
}
