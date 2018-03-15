package org.trident.world.content.minigames.impl;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Flag;
import org.trident.model.GameObject;
import org.trident.model.GroundItem;
import org.trident.model.Item;
import org.trident.model.PlayerInteractingOption;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.model.definitions.NPCSpawns;
import org.trident.model.inputhandling.impl.EnterAmountOfZealsToBuy;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.Following;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCData.CustomNPCData;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;
import org.trident.world.entity.player.PlayerSaving;

/**
 * @author owner blade
 * Redone a bit by Gabbe
 */
public class SoulWars {

	/**
	 * Coord data
	 */
	public static final int[] SOULWARS_GAME = new int[]{1790, 3200, 1985, 3265};
	public static final int[] RED_AVATAR_ROOM = new int[] {1960, 3245, 1974, 3257};
	public static final int[] BLUE_AVATAR_ROOM = new int[] {1799, 3203, 1816, 3218};
	public static final int[] RED_AVATAR_SPAWN = new int[] { 1967, 3251 };
	public static final int[] BLUE_AVATAR_SPAWN = new int[] { 1807, 3210 };

	public static final int[] BLUE_BASE = new int[] { 1816, 3220, 1823, 3230 };
	public static final int[] BLUE_GRAVEYARD = new int[] { 1837, 3213, 1847, 3223 };
	public static final int[] BLUE_GRAVEYARD_SPAWN = new int[] { 1841, 3217, 1843,
		3219 };
	public static final int[] OBELISK = new int[] { 1871, 3221, 1902, 3242 };
	public static final int[] RED_BASE = new int[] { 1951, 3234, 1958, 3244 };
	public static final int[] RED_GRAVEYARD = new int[] { 1928, 3240, 1938, 3250 };
	public static final int[] RED_GRAVEYARD_SPAWN = new int[] { 1932, 3244, 1958, 3244 };
	public static final int[] BLUE_LOBBY_GRAVEYARD = new int[] { 1885, 3166, 1887, 3173 };
	public static final int[] BLUE_LOBBY = new int[] { 1870, 3158, 1879, 3166 };
	public static final int[] RED_LOBBY_GRAVEYARD = new int[] { 1893, 3166, 1895, 3173 };
	public static final int[] RED_LOBBY = new int[] { 1900, 3157, 1909, 3166 };

	/**
	 * Avatar npc ids
	 */
	public final static int GHOST_ID = 8623;
	public final static int RED_AVATAR = 8596, BLUE_AVATAR = 8597, PYREFIEND = 8598, JELLY = 1640;
	private final static int RED = 0, BLUE = 1, NEITHER = 2;
	public final static int SOUL_FRAGMENT = 14639;
	public final static int SOUL_OBELISK = 42010;

	public final static int RED_CAPE = 14641, BLUE_CAPE = 14642, BANDAGE = 14640, BARRICADE = 14643, EXPLOSIVE_POTION = 14644;
	public final static int BARRICADE_NPC = 8600;

	public static CopyOnWriteArrayList<Player> PLAYERS;
	public static ArrayList<Player> redTeam;
	public static ArrayList<Player> blueTeam;
	private static int obelisk, redGraveYard, blueGraveYard;
	private static int obeliskTimer, redGraveYardTimer, blueGraveYardTimer;
	public static int redSlayerLevel, blueSlayerLevel;
	private static int blueKills, redKills;
	public static boolean gameRunning;
	private static long currentStartTime;
	public static NPC redAvatar;
	public static NPC blueAvatar;
	public static ArrayList<NPC> redBarricades;
	public static ArrayList<NPC> blueBarricades;

	/*
	 * Forcing a game
	 */
	public static boolean forceStart;

	static {
		redTeam = new ArrayList<>();
		blueTeam = new ArrayList<>();
		redBarricades = new ArrayList<>();
		blueBarricades = new ArrayList<>();
		PLAYERS = new CopyOnWriteArrayList<>();
		currentStartTime = System.currentTimeMillis();
		reset();
	}

	public static int getRedTeamCount() {
		return redTeam.size();
	}

	public static int getBlueTeamCount() {
		return blueTeam.size();
	}

	public static int getMinutesPassed() {
		long currentTime = System.currentTimeMillis();
		long diffInTime = currentTime - currentStartTime;
		return (int) ((diffInTime / (1000 * 60)) % 60);
	}


	public static void reset() {
		blueKills = 0;
		redKills = 0;
		gameRunning = false;
		redTeam.clear();
		blueTeam.clear();
		redBarricades.clear();
		blueBarricades.clear();
		redSlayerLevel = 100;
		blueSlayerLevel = 100;
		obelisk = NEITHER;
		obeliskTimer = 0;
		redGraveYard = NEITHER;
		redGraveYardTimer = 0;
		blueGraveYard = NEITHER;
		blueGraveYardTimer = 0;
	}

	static int redLobbyPlayers, blueLobbyPlayers;

	private static boolean globalMsg = false;

	public static void process() {
		handleWaitingPlayers();
		int minutesPassed = getMinutesPassed();
		if (minutesPassed == 20 && gameRunning)
			endGame();
		if(minutesPassed == 22 && !globalMsg && redLobbyPlayers >= 1 && blueLobbyPlayers >= 1) {
			PlayerHandler.sendGlobalPlayerMessage("A game of Soul Wars is about to begin. Bored? Go try it out!");
			globalMsg = true;
		}
		if (!gameRunning && minutesPassed == 25 || forceStart) {
			if(forceStart) {
				forceStart = false;
				if(gameRunning) {
					endGame();
					return;
				}
			} else {
				if(redLobbyPlayers >= 1 &&  redLobbyPlayers < 3 || blueLobbyPlayers >= 1 && blueLobbyPlayers < 3) {
					for(Player p : PLAYERS) {
						if(p == null || p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getTeam() < 1)
							continue;
						p.getPacketSender().sendMessage("There must be at least 3 players in each team to start a Soul Wars game.");
					}
					currentStartTime = System.currentTimeMillis() - 1200000; //removes 20 mins
					return;
				}
			}
			startNewGame();
		}
		if (!gameRunning)
			return;
		int redInObelisk = 0;
		int redInRedGraveYard = 0;
		int redInBlueGraveYard = 0;
		for (Player p : redTeam) {
			if(p == null)
				continue;
			drawTimeLeft(p);
			if (inObelisk(p)) {
				redInObelisk++;
			}
			if (inRedGraveYard(p)) {
				redInRedGraveYard++;
			}
			if (inBlueGraveYard(p)) {
				redInBlueGraveYard++;
			}
		}
		int blueInObelisk = 0;
		int blueInRedGraveYard = 0;
		int blueInBlueGraveYard = 0;
		for (Player p : blueTeam) {
			drawTimeLeft(p);
			if (inObelisk(p)) {
				blueInObelisk++;
			}
			if (inRedGraveYard(p)) {
				blueInRedGraveYard++;
			}
			if (inBlueGraveYard(p)) {
				blueInBlueGraveYard++;
			}
		}
		if (redInObelisk > blueInObelisk) {
			if (obeliskTimer == 7) {
				if(obelisk != RED)
					setObelisk(RED);
				drawMainInterface();
				obeliskTimer = 7;
			} else {
				obeliskTimer++;
				drawMainInterface();
			}
		} else if (blueInObelisk > redInObelisk) {
			if (obeliskTimer == -7) {
				if(obelisk != BLUE)
					setObelisk(BLUE);
				drawMainInterface();
				obeliskTimer = -7;
			} else {
				obeliskTimer--;
				drawMainInterface();
			}
		}

		if (redInRedGraveYard > blueInRedGraveYard) {
			if (redGraveYardTimer == 7) {
				redGraveYard = RED;
				redGraveYardTimer = 7;
				drawMainInterface();
			} else {
				redGraveYardTimer++;
				drawMainInterface();
			}
		} else if (blueInRedGraveYard > redInRedGraveYard) {
			if (redGraveYardTimer == -7) {
				redGraveYardTimer = -7;
				redGraveYard = BLUE;
				drawMainInterface();
			} else {
				redGraveYardTimer--;
				drawMainInterface();
			}
		}

		if (redInBlueGraveYard > blueInBlueGraveYard) {
			if (blueGraveYardTimer == 7) {
				blueGraveYardTimer = 7;
				blueGraveYard = RED;
				drawMainInterface();
			} else {
				blueGraveYardTimer++;
				drawMainInterface();
			}
		} else if (blueInBlueGraveYard > redInBlueGraveYard) {
			if (blueGraveYardTimer == -7) {
				blueGraveYardTimer = -7;
				blueGraveYard = BLUE;
				drawMainInterface();
			} else {
				blueGraveYardTimer--;
				drawMainInterface();
			}
		}
	}

	public static void drawAvatarDeaths() {
		for (Player p : redTeam) {
			if(p == null)
				continue;
			p.getPacketSender().sendString(28520, "" + redKills);
			p.getPacketSender().sendString(28521, "" + blueKills);
		}
		for (Player p : blueTeam) {
			p.getPacketSender().sendString(28520, "" + redKills);
			p.getPacketSender().sendString(28521, "" + blueKills);
		}
	}

	public static void startNewGame() {
		globalMsg = false;
		gameRunning = true;
		currentStartTime = System.currentTimeMillis();
		for(Player p : World.getPlayers()) {
			if (p == null)
				continue;
			if (isWithin(RED_LOBBY, p)) {
				if(p.getEquipment().getItems()[Equipment.CAPE_SLOT].getId() != -1)
				{
					p.getPacketSender().sendMessage("You can't wear a cape in Soul wars.");
					continue;
				}
				p.moveTo(new Position(1954, 3239, 0));
				redTeam.add(p);
				p.getEquipment().setItem(Equipment.CAPE_SLOT, new Item(RED_CAPE).setAmount(1)); p.getEquipment().refreshItems();
				p.getUpdateFlag().flag(Flag.APPEARANCE);
				p.getPacketSender().sendWalkableInterface(28510);
				handleActivity(p, 30, false, false);
				p.getPacketSender().sendInteractionOption("Attack", 2, true);
			}
			if (isWithin(BLUE_LOBBY, p)) {
				if(p.getEquipment().getItems()[Equipment.CAPE_SLOT].getId() != -1)
				{
					p.getPacketSender().sendMessage("You can't wear a cape in Soul wars.");
					continue; 
				}
				p.moveTo(new Position(1821, 3225, 0));
				blueTeam.add(p);
				p.getEquipment().setItem(Equipment.CAPE_SLOT, new Item(BLUE_CAPE).setAmount(1)); p.getEquipment().refreshItems();
				p.getUpdateFlag().flag(Flag.APPEARANCE);
				p.getPacketSender().sendWalkableInterface(28510);
				handleActivity(p, 30, false, false);
				p.getPacketSender().sendInteractionOption("Attack", 2, true);
			}
		}
		redAvatar = PestControl.spawnPCNPC(RED_AVATAR, new Position(RED_AVATAR_SPAWN[0], RED_AVATAR_SPAWN[1]), 1500, 200, 400, 200);
		redAvatar.getAttributes().setWalkingDistance(1);
		blueAvatar = PestControl.spawnPCNPC(BLUE_AVATAR, new Position(BLUE_AVATAR_SPAWN[0], BLUE_AVATAR_SPAWN[1]), 1500, 200, 400, 200);
		blueAvatar.getAttributes().setWalkingDistance(1);
		gameRunning = true;
		drawMainInterface();
		drawAvatarDeaths();
		drawAvatarHealth();
		drawAvatarLevel();
		drawTimeLeft();
		setPlayersActivityTask();
	}

	public static void endGame() {

		gameRunning = false;
		int victory;
		if (redKills > blueKills)
			victory = RED;
		else if (blueKills > redKills)
			victory = BLUE;
		else
			victory = NEITHER;
		for (Player p : redTeam) {
			if(p == null)
				continue;
			if (victory == RED) {
				p.getPacketSender().sendMessage("Congratulations, you won 3 zeal.");
				p.getPointsHandler().setZeals(3, true);
			}
			if (victory == BLUE) {
				p.getPacketSender().sendMessage("You lost, but still won 1 zeal.");
				p.getPointsHandler().setZeals(1, true);
			}
			if (victory == NEITHER) {
				p.getPacketSender().sendMessage("You tied, but still won 2 zeal.");
				p.getPointsHandler().setZeals(2, true);
			}
			int[] to = getRandomCoordsInArea(RED_LOBBY_GRAVEYARD);
			p.moveTo(new Position(to[0], to[1]));
			p.getPacketSender().sendWalkableInterface(-1);
			removeItemsEnd(p);
			p.getPointsHandler().refreshPanel();
			PLAYERS.remove(p);
			p.getPacketSender().sendInteractionOption("null", 2, true);
		}
		for (Player p : blueTeam) {
			if (victory == BLUE) {
				p.getPacketSender().sendMessage("Congratulations, you won 3 zeal.");
				p.getPointsHandler().setZeals(3, true);
			}
			if (victory == RED) {
				p.getPacketSender().sendMessage("You lost, but still won 1 zeal.");
				p.getPointsHandler().setZeals(1, true);
			}
			if (victory == NEITHER) {
				p.getPacketSender().sendMessage("You tied, but still won 2 zeal.");
				p.getPointsHandler().setZeals(2, true);
			}
			int[] to = getRandomCoordsInArea(BLUE_LOBBY_GRAVEYARD);
			p.moveTo(new Position(to[0], to[1]));
			p.getPacketSender().sendWalkableInterface(-1);
			removeItemsEnd(p);
			p.getPointsHandler().refreshPanel();
			PLAYERS.remove(p);
			p.getPacketSender().sendInteractionOption("null", 2, true);
		}
		World.deregister(redAvatar);
		World.deregister(blueAvatar);
		for(NPC npc : redBarricades)
		{
			World.deregister(npc);
		}
		for(NPC npc : blueBarricades)
		{
			World.deregister(npc);
		}
		blueTeam.clear();
		redTeam.clear();
		reset();
	}

	public static void leaveSoulWars(Player p) {
		int[] to = getRandomCoordsInArea(RED_LOBBY_GRAVEYARD);
		p.moveTo(new Position(to[0], to[1]));
		p.getPacketSender().sendWalkableInterface(-1);
		removeItemsEnd(p);
		if(!redTeam.remove(p))
			blueTeam.remove(p);
		PLAYERS.remove(p);
		p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setTeam(-1);
		p.getPacketSender().sendInteractionOption("null", 2, true);
	}

	public static void removeItemsEnd(Player p)
	{
		if(p == null)
			return;
		PrayerHandler.deactivateAll(p); CurseHandler.deactivateAll(p);
		p.setConstitution(p.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
		CombatHandler.resetAttack(p);
		p.getEquipment().setItem(Equipment.CAPE_SLOT, new Item(-1).setAmount(1)); p.getEquipment().refreshItems();
		p.getUpdateFlag().flag(Flag.APPEARANCE);
		p.getAttributes().setPlayerInteractingOption(PlayerInteractingOption.NONE);
		for(Item item2 : p.getInventory().getItems()) {
			if(item2 == null || item2.getId() == -1)
				continue;
			if(item2.getId() == SOUL_FRAGMENT || item2.getId() == BARRICADE || item2.getId() == EXPLOSIVE_POTION || item2.getId() == BANDAGE)
				p.getInventory().delete(item2);
		}
		PlayerSaving.save(p);
	}

	public static void boneBury(Player p) {
		boolean succes = Misc.getRandom(3) == 1;
		if (succes) {
			if (redTeam.contains(p)) {
				if (isWithin(RED_BASE, p)) {
					redSlayerLevel++;
					drawAvatarLevel();
					p.getPacketSender().sendMessage("Somehow, your buryal strengthens your avatar.");
					handleActivity(p, 3, true, true);
				}
				if (isWithin(RED_GRAVEYARD, p) && redGraveYard == RED) {
					redSlayerLevel++;
					drawAvatarLevel();
					p.getPacketSender().sendMessage("Somehow, your buryal strengthens your avatar.");
					handleActivity(p, 3, true, true);
				}
				if (isWithin(BLUE_GRAVEYARD, p) && blueGraveYard == RED) {
					redSlayerLevel++;
					drawAvatarLevel();
					p.getPacketSender().sendMessage("Somehow, your buryal strengthens your avatar.");
					handleActivity(p, 3, true, true);
				}
			}
			if (blueTeam.contains(p)) {
				if (isWithin(BLUE_BASE, p)) {
					blueSlayerLevel++;
					drawAvatarLevel();
					p.getPacketSender().sendMessage("Somehow, your buryal strengthens your avatar.");
					handleActivity(p, 3, true, true);
				}
				if (isWithin(RED_GRAVEYARD, p) && redGraveYard == BLUE) {
					blueSlayerLevel++;
					drawAvatarLevel();
					p.getPacketSender().sendMessage("Somehow, your buryal strengthens your avatar.");
					handleActivity(p, 3, true, true);
				}
				if (isWithin(BLUE_GRAVEYARD, p) && blueGraveYard == BLUE) {
					blueSlayerLevel++;
					drawAvatarLevel();
					p.getPacketSender().sendMessage("Somehow, your buryal strengthens your avatar.");
					handleActivity(p, 3, true, true);
				}
			}
		}
		if (redSlayerLevel > 100)
			redSlayerLevel = 100;
		if (blueSlayerLevel > 100)
			blueSlayerLevel = 100;
	}

	public static void drawMainInterface() {
		int redGraveYardColor = NEITHER;
		if (redGraveYardTimer > 0)
			redGraveYardColor = RED;
		if (redGraveYardTimer < 0)
			redGraveYardColor = BLUE;
		int blueGraveYardColor = NEITHER;
		if (blueGraveYardTimer > 0)
			blueGraveYardColor = RED;
		if (blueGraveYardTimer < 0)
			blueGraveYardColor = BLUE;
		int obeliskColor = NEITHER;
		if (obeliskTimer > 0)
			obeliskColor = RED;
		if (obeliskTimer < 0)
			obeliskColor = BLUE;
		for (Player p : redTeam) {
			if(p == null)
				continue;
			p.getPacketSender().sendPacket161(28512, redGraveYardColor, Math.abs(redGraveYardTimer) * 5);
			p.getPacketSender().sendPacket161(28513, obeliskColor, Math.abs(obeliskTimer) * 5);
			p.getPacketSender().sendPacket161(28514, blueGraveYardColor, Math.abs(blueGraveYardTimer) * 5);
		}
		for (Player p : blueTeam) {
			p.getPacketSender().sendPacket161(28512, redGraveYardColor, Math.abs(redGraveYardTimer) * 5);
			p.getPacketSender().sendPacket161(28513, obeliskColor, Math.abs(obeliskTimer) * 5);
			p.getPacketSender().sendPacket161(28514, blueGraveYardColor, Math.abs(blueGraveYardTimer) * 5);
		}
	}

	public static void drawAvatarHealth() {
		double redPercentage = (((double) redAvatar.getConstitution() / (double) 1000D) * 100D);
		double bluePercentage = (((double) blueAvatar.getConstitution() / (double) 1000D) * 100D);
		for (Player p : redTeam) {
			if(p == null)
				continue;
			p.getPacketSender().sendString(28526, (int)redPercentage + "%");
			p.getPacketSender().sendString(28527, (int)bluePercentage + "%");
		}
		for (Player p : blueTeam) {
			p.getPacketSender().sendString(28526, (int)redPercentage + "%");
			p.getPacketSender().sendString(28527, (int)bluePercentage + "%");
		}
	}

	public static void handleItemClick(int itemId, Player p)
	{
		if(itemId == BARRICADE)
		{

			if(isWithin(RED_GRAVEYARD_SPAWN, p)
					|| isWithin(BLUE_GRAVEYARD_SPAWN, p)
					|| isWithin(BLUE_BASE, p)
					|| isWithin(RED_BASE, p))
			{
				p.getPacketSender().sendMessage("You can't place barricades here");
				return;
			}
			if(redTeam.contains(p))
			{
				if(redBarricades.size() != 10) {
					NPC barri = NPCSpawns.createCustomNPC(CustomNPCData.SOULWARS_BARRICADE, p.getPosition().copy());
					World.register(barri);
					redBarricades.add(barri);
				} else {
					p.getPacketSender().sendMessage("You team already has 10 Barricades set up");
					return;
				}
			}
			if(blueTeam.contains(p))
			{
				if(blueBarricades.size() != 10) {
					NPC barri = NPCSpawns.createCustomNPC(CustomNPCData.SOULWARS_BARRICADE, p.getPosition().copy());
					World.register(barri);
					blueBarricades.add(barri);
				} else {
					p.getPacketSender().sendMessage("You team already has 10 Barricades set up");
					return;
				}
			}
			handleActivity(p, 1, true, false);
			Following.stepAway(p);
			p.getInventory().delete(BARRICADE, 1);
		}
	}

	public static void handleItemOnObject(int objectID, int itemID, Player p) {
		if (objectID >= -23526 && objectID <= -23526 + 3) {
			if (itemID == SOUL_FRAGMENT) {
				if (!isWithin(OBELISK, p))
					return;
				int team;
				if (redTeam.contains(p))
					team = RED;
				else if (blueTeam.contains(p))
					team = BLUE;
				else
					return;
				if (team == RED && obelisk != RED || team == BLUE
						&& obelisk != BLUE) {
					p.getPacketSender().sendMessage("Your team does not own the obelisk.");
				} else {
					if (team == RED) {
						blueSlayerLevel -= p.getInventory().getAmount(SOUL_FRAGMENT);
						if (blueSlayerLevel < 1)
							blueSlayerLevel = 1;
					}
					if (team == BLUE) {
						redSlayerLevel -= p.getInventory().getAmount(SOUL_FRAGMENT);
						if (redSlayerLevel < 1)
							redSlayerLevel = 1;
						for(Player p2: redTeam) {
							if(p2 == null)
								continue;
							p2.getPacketSender().sendMessage("Your Avatar has been weakened..");
						}
					}
					handleActivity(p, 5, true, false);
					p.getInventory().delete(SOUL_FRAGMENT, p.getInventory().getAmount(SOUL_FRAGMENT));
					drawAvatarLevel();
				}
			}
		}

	}

	public static void handleObjectClick(int objectID, Player p) {
		switch (objectID) {
		case 42021:
			int[] to = getRandomCoordsInArea(BLUE_LOBBY_GRAVEYARD);
			p.getMovementQueue().reset(true);
			p.moveTo(new Position(to[0], to[1]));
			p.getPacketSender().sendWalkableInterface(-1);
			removeItemsEnd(p);
			blueTeam.remove(p);
			PLAYERS.remove(p);
			p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setTeam(-1);
			break;
		case 42022:
			if(p.getNpcTransformationId() > 0) {
				WeaponHandler.setWeaponAnimationIndex(p);
				p.setNpcTransformationId(-1);
				p.getUpdateFlag().flag(Flag.APPEARANCE);
			}
			to = getRandomCoordsInArea(RED_LOBBY_GRAVEYARD);
			p.getMovementQueue().reset(true);
			p.moveTo(new Position(to[0], to[1]));
			p.getPacketSender().sendWalkableInterface(-1);
			removeItemsEnd(p);
			redTeam.remove(p);
			PLAYERS.remove(p);
			p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setTeam(-1);
			break;

		case 42023:
		case 42024://blue band
			if(redTeam.contains(p) || blueTeam.contains(p)){
				if(p.getInventory().getFreeSlots() == 0) {
					p.getPacketSender().sendMessage("You cannot carry anymore Bandages.");
					return;
				}
				handleActivity(p, 1, true, false);
				p.getInventory().add(BANDAGE, 1);
			}
			break;
		case 42025:
		case 42026://barricade blue
			if(redTeam.contains(p) || blueTeam.contains(p)){
				if(p.getInventory().getFreeSlots() == 0) {
					p.getPacketSender().sendMessage("You cannot carry anymore Barricades.");
					return;
				}
				handleActivity(p, 1, true, false);
				p.getInventory().add(BARRICADE, 1);
			}
			break;
		case 42027:
		case 42028://pot blue
			if(redTeam.contains(p) || blueTeam.contains(p)){
				if(p.getInventory().getFreeSlots() == 0) {
					p.getPacketSender().sendMessage("You cannot carry anymore Explosive potions.");
					return;
				}
				handleActivity(p, 1, true, false);
				p.getInventory().add(EXPLOSIVE_POTION, 1);
			}
			break;
		case 42020:
			if (isWithin(BLUE_GRAVEYARD_SPAWN, p)) {
				if (p.getNpcTransformationId() == GHOST_ID) {
					p.getPacketSender().sendMessage("You must wait a few seconds before you can leave.");
				} else
					p.moveTo(new Position(1842, 3220));
			} else if (isWithin(BLUE_GRAVEYARD, p))
				p.getPacketSender().sendMessage("You can't go in that way.");
			break;
		case 42019:
			if (isWithin(RED_GRAVEYARD_SPAWN, p)) {
				if(p.getNpcTransformationId() == GHOST_ID) {
					p.getPacketSender().sendMessage("You must wait a few seconds before you can leave.");
				} else
					p.moveTo(new Position(1933, 3243));
			} else if (isWithin(RED_GRAVEYARD, p))
				p.getPacketSender().sendMessage("You can't go in that way.");
			break;
		case 42015:
			if (blueTeam.contains(p)) {
				if (isWithin(BLUE_BASE, p)) {
					if(p.getNpcTransformationId() == GHOST_ID) {
						p.getPacketSender().sendMessage("You must wait a few seconds before you can leave.");
					} else
						p.moveTo(new Position(1814, 3225));
				} else 
					p.moveTo(new Position(1816, 3225));
				handleActivity(p, 1, true, false);
			}
			break;

		case 42018:
			if (redTeam.contains(p)) {
				if (isWithin(RED_BASE, p)) {
					if(p.getNpcTransformationId() == GHOST_ID) {
						p.getPacketSender().sendMessage("You must wait a few seconds before you can leave.");
					} else
						p.moveTo(new Position(1959, 3239));
				} else
					p.moveTo(new Position(1958, 3239));
				handleActivity(p, 1, true, false);
			}
			break;
		case 42030:
			if (isWithin(RED_LOBBY, p)) {
				p.moveTo(new Position(1899, 3162));
				p.getPacketSender().sendWalkableInterface(-1);
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setTeam(-1);
				PLAYERS.remove(p);
			} else {
				if(p.getEquipment().getItems()[Equipment.CAPE_SLOT].getId() != -1)
				{
					p.getPacketSender().sendMessage("You can't wear a cape in Soul wars.");
					break;
				} 
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setTeam(1);
				PLAYERS.add(p);
				p.moveTo(new Position(1900, 3162));
				p.getPacketSender().sendWalkableInterface(28500);

			}
			break;
		case 42029:
			if (isWithin(BLUE_LOBBY, p)) {
				p.moveTo(new Position(1880, 3162));
				p.getPacketSender().sendWalkableInterface(-1);
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setTeam(-1);
				PLAYERS.remove(p);
			} else {
				if(p.getEquipment().getItems()[Equipment.CAPE_SLOT].getId() != -1)
				{
					p.getPacketSender().sendMessage("You can't wear a cape in Soul wars.");
					break;
				}
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setTeam(2);
				PLAYERS.add(p);
				p.moveTo(new Position(1879, 3162));
				p.getPacketSender().sendWalkableInterface(28500);
			}
			break;
		case 42031:
			if(p.getEquipment().getItems()[Equipment.CAPE_SLOT].getId() != -1)
			{
				p.getPacketSender().sendMessage("You can't wear a cape in Soul wars.");
				return;
			}
			if (redLobbyPlayers > blueLobbyPlayers) {
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setTeam(2);
				p.moveTo(new Position(1879, 3162));
			} else if (blueLobbyPlayers > redLobbyPlayers) {
				p.moveTo(new Position(1900, 3162));
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setTeam(1);
			} else {
				boolean toRed = Misc.getRandom(1) == 0;
				if (toRed) {
					p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setTeam(1);
					p.moveTo(new Position(1900, 3162));
				} else {
					p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setTeam(2);
					p.moveTo(new Position(1879, 3162));
				}
			}
			PLAYERS.add(p);
			p.getPacketSender().sendWalkableInterface(28500);
			break;
		case 42192:
			p.getBank(0).open();
			break;
		}
	}

	public static void drawAvatarLevel() {
		for (Player p : redTeam) {
			if(p == null)
				continue;
			p.getPacketSender().sendString(28523, "" + blueSlayerLevel);
			p.getPacketSender().sendString(28524, "" + redSlayerLevel);
		}
		for (Player p : blueTeam) {
			if(p == null)
				continue;
			p.getPacketSender().sendString(28523, "" + blueSlayerLevel);
			p.getPacketSender().sendString(28524, "" + redSlayerLevel);
		}
	}

	public static void drawTimeLeft() {
		int minutesPassed = getMinutesPassed();
		int minutesLeft = 25 - minutesPassed;
		for (Player p : redTeam) {
			if(p == null)
				continue;
			p.getPacketSender().sendString(28529, minutesLeft + " min");
		}
		for (Player p : blueTeam) {
			if(p == null)
				continue;
			p.getPacketSender().sendString(28529, minutesLeft + " min");
		}
	}

	public static void setObelisk(int i) {
		obelisk = i;
		for (Player p : redTeam) {
			if(p == null)
				continue;
			if(obelisk == RED)
				p.getPacketSender().sendMessage("Your team has taken control of the Obelisk!");
			else
				p.getPacketSender().sendMessage("The blue team has taken control of the Obelisk!");
			setCorrectObelisk(p);
		}
		for (Player p : blueTeam) {
			if(p == null)
				continue;
			if(obelisk == BLUE)
				p.getPacketSender().sendMessage("Your team has taken control of the Obelisk!");
			else
				p.getPacketSender().sendMessage("The red team has taken control of the Obelisk!");
			setCorrectObelisk(p);
		}
	}

	public static void setPlayersActivityTask() {
		TaskManager.submit(new Task(20, true) {
			@Override
			public void execute() {
				if(!gameRunning || redTeam.size() == 0 || blueTeam.size() == 0) {
					stop();
					return;
				}
				try {
					for(Player p : redTeam) {
						if(p == null)
							continue;
						if(!CombatHandler.inCombat(p))
							handleActivity(p, -1, true, true);
					}
					for(Player p : blueTeam) {
						if(p == null)
							continue;
						if(!CombatHandler.inCombat(p))
							handleActivity(p, -1, true, true);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void setCorrectObelisk(Player p) {
		int obeliskToShow = SOUL_OBELISK;
		if (obelisk == RED) {
			obeliskToShow = SOUL_OBELISK + 2;
		} else if (obelisk == BLUE) {
			obeliskToShow = SOUL_OBELISK + 1;
		}
		CustomObjects.spawnGlobalObjectWithinDistance(new GameObject(obeliskToShow, new Position(1886, 3231, 0)));
	}

	public static void drawTimeLeft(Player p) {
		int minutesPassed = getMinutesPassed();
		int minutesLeft = 20 - minutesPassed;
		p.getPacketSender().sendString(28529, minutesLeft + " min");
	}

	public static void handleWaitingPlayers() {
		redLobbyPlayers = blueLobbyPlayers = 0;
		for (Player player : PLAYERS) {
			if(player == null || player.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getTeam() < 1)
				continue;
			if(player.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getTeam() == 1)
				redLobbyPlayers++;
			else if(player.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getTeam() == 2)
				blueLobbyPlayers++;
		}
		for(Player p : PLAYERS) {
			if(p != null) {
				p.getPacketSender().sendString(28502, "Blue: "+blueLobbyPlayers+"/5");
				p.getPacketSender().sendString(28503, "Red: "+redLobbyPlayers+"/5");
			}
		}
	}

	public static boolean inObelisk(Player p) {
		return isWithin(OBELISK, p);
	}

	public static boolean inRedGraveYard(Player p) {
		return isWithin(RED_GRAVEYARD, p) && !isWithin(RED_GRAVEYARD_SPAWN, p);
	}

	public static boolean inBlueGraveYard(Player p) {
		return isWithin(BLUE_GRAVEYARD, p) && !isWithin(BLUE_GRAVEYARD_SPAWN, p);
	}

	public static boolean checkBarricade(int x, int y, int z) {
		for (NPC npc : SoulWars.redBarricades) {
			if (npc.getPosition().getX() == x && npc.getPosition().getY() == y && npc.getPosition().getZ() == z) {
				return true;
			}
		}
		for (NPC npc : SoulWars.blueBarricades) {
			if (npc.getPosition().getX() == x && npc.getPosition().getY() == y && npc.getPosition().getZ() == z) {
				return true;
			}
		}
		return false;
	}

	public static void handleDeath(final Player p) {
		int[] coords = getOnDeathCoords(p);
		if(coords == null)
			return;
		GroundItemManager.spawnGroundItem(p, new GroundItem(new Item(526), p.getPosition(), p.getUsername(), false, 2, true, 80));
		if(p.getInventory().contains(SOUL_FRAGMENT)) {
			int amt = p.getInventory().getAmount(SOUL_FRAGMENT);
			p.getInventory().delete(SOUL_FRAGMENT, amt);
			GroundItemManager.spawnGroundItem(p, new GroundItem(new Item(SOUL_FRAGMENT, amt), p.getPosition(), p.getUsername(), false, 2, true, 80));
		}
		p.moveTo(new Position(coords[0], coords[1]));
	}

	public static int[] getOnDeathCoords(Player p) {

		int team;
		if (redTeam.contains(p))
			team = RED;
		else if (blueTeam.contains(p))
			team = BLUE;
		else
			return null;
		if (team == RED) {
			if (blueGraveYard == RED) {
				return new int[] {1841, 3217};
			} else if (redGraveYard == RED) {
				return new int[] {1932, 3244};
			} else {
				return new int[] {1954, 3239};
			}
		}

		if (team == BLUE) {
			if (redGraveYard == BLUE) {
				return new int[] {1932, 3244};
			} else if (blueGraveYard == BLUE) {
				return new int[] {1841, 3217};
			} else {
				return new int[] {1821, 3225};
			}
		}
		return null;
	}

	public static void addRedKill() {
		redKills++;
		drawAvatarDeaths();
		for(Player p : redTeam) {
			if(p == null)
				continue;
			p.getPacketSender().sendMessage("Your Avatar has been slain!");
		}
		for(Player p : blueTeam) {
			if(p == null)
				continue;
			p.getPacketSender().sendMessage("Your team has slain the enemy team's Avatar!");
		}
	}

	public static void addBlueKills() {
		blueKills++;
		drawAvatarDeaths();
		for(Player p : blueTeam) {
			if(p == null)
				continue;
			p.getPacketSender().sendMessage("Your Avatar has been slain!");
		}
		for(Player p : redTeam) {
			if(p == null)
				continue;
			p.getPacketSender().sendMessage("Your team has slain the enemy team's Avatar!");
		}
	}

	public static void handleAvatarDeath(int avatar) {
		if(avatar == RED_AVATAR) {
			addBlueKills();
			redSlayerLevel = 100;
			TaskManager.submit(new Task(10) {
				@Override
				public void execute() {
					this.stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					if(gameRunning) {
						redAvatar = PestControl.spawnPCNPC(RED_AVATAR, new Position(RED_AVATAR_SPAWN[0], RED_AVATAR_SPAWN[1]), 1500, 200, 400, 200);
						redAvatar.getAttributes().setWalkingDistance(1);
					}
				}
			});
		} else if(avatar == BLUE_AVATAR) {
			blueSlayerLevel = 100;
			addRedKill();
			TaskManager.submit(new Task(10) {
				@Override
				public void execute() {
					if(gameRunning) {
						blueAvatar = PestControl.spawnPCNPC(BLUE_AVATAR, new Position(BLUE_AVATAR_SPAWN[0], BLUE_AVATAR_SPAWN[1]), 1500, 200, 400, 200);
						blueAvatar.getAttributes().setWalkingDistance(1);
					}
					stop();
				}
			});
		}
	}

	/**
	 * Rewards
	 * @author Gabbe
	 */
	public static class RewardShop {

		public static void openShop(Player p) {
			p.getPacketSender().sendString(31091, "   Zeals : "+p.getPointsHandler().getZeals());
			p.getPacketSender().sendString(31092, "   Zeals : "+p.getPointsHandler().getZeals());
			p.getPacketSender().sendInterface(31000);
		}

		public static boolean handleButton(Player p, int button) {
			switch(button) {
			case 31029:
				p.getPacketSender().sendInterface(31035);
				break;
			case 31052:
				p.getPacketSender().sendInterface(31000);
				break;
			case 31005:
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(0);
				DialogueManager.start(p, buyDialogue(p));
				break;
			case 31008:
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(2);
				DialogueManager.start(p, buyDialogue(p));
				break;
			case 31011:
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(1);
				DialogueManager.start(p, buyDialogue(p));
				break;
			case 31014:
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(3);
				DialogueManager.start(p, buyDialogue(p));
				break;
			case 31017:
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(4);
				DialogueManager.start(p, buyDialogue(p));
				break;
			case 31020:
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(6);
				DialogueManager.start(p, buyDialogue(p));
				break;
			case 31023:
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(5);
				DialogueManager.start(p, buyDialogue(p));
				break;
			case 31026:
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(18);
				DialogueManager.start(p, buyDialogue(p));
				break;
			case 31040: //Gold charm
				if(p.getInventory().getFreeSlots() == 0 && !p.getInventory().contains(12158)) {
					p.getPacketSender().sendMessage("Your inventory is full. Free some space and try again.");
					return true;
				}
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(20);
				DialogueManager.start(p, buyDialogue(p));
				break;
			case 31043: //Green charm
				if(p.getInventory().getFreeSlots() == 0 && !p.getInventory().contains(12159)) {
					p.getPacketSender().sendMessage("Your inventory is full. Free some space and try again.");
					return true;
				}
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(21);
				DialogueManager.start(p, buyDialogue(p));
				break;
			case 31046: //Crimson charm
				if(p.getInventory().getFreeSlots() == 0 && !p.getInventory().contains(12160)) {
					p.getPacketSender().sendMessage("Your inventory is full. Free some space and try again.");
					return true;
				}
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(22);
				DialogueManager.start(p, buyDialogue(p));
				break;
			case 31049: //Blue charm
				if(p.getInventory().getFreeSlots() == 0 && !p.getInventory().contains(12163)) {
					p.getPacketSender().sendMessage("Your inventory is full. Free some space and try again.");
					return true;
				}
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(23);
				DialogueManager.start(p, buyDialogue(p));
				break;
			}
			return false;
		}

		public static void exchange(Player p, int amount) {
			p.getPacketSender().sendInterfaceRemoval();
			if(p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getProductChosen() < 0)
				return;
			if(amount > p.getPointsHandler().getZeals())
				amount = p.getPointsHandler().getZeals();
			if(amount < 1) {
				p.getPacketSender().sendMessage("You do not have any Zeals.");
				return;
			}
			String s = amount == 1 ? "" : "s";
			switch(p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getProductChosen()) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 18:
				Skill skillSelected = Skill.forId(p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getProductChosen());
				p.getPointsHandler().setZeals(-amount, true);
				int exp = getExperienceReward(p, skillSelected) * amount;
				p.getSkillManager().addExperience(skillSelected, exp, false);
				p.getPacketSender().sendMessage("You've received "+exp+" experience in "+skillSelected.getPName()+" for your "+amount+" Zeal"+s+".");
				break;
			case 20:
				p.getPointsHandler().setZeals(-amount, true);
				p.getInventory().add(new Item(12158, 5*amount));
				p.getPacketSender().sendMessage("You've received "+5*amount+" Gold charms for your "+amount+" Zeal"+s+".");
				break;
			case 21:
				p.getPointsHandler().setZeals(-amount, true);
				p.getInventory().add(new Item(12159, 4*amount));
				p.getPacketSender().sendMessage("You've received "+4*amount+" Green charms for your "+amount+" Zeal"+s+".");
				break;
			case 22:
				p.getPointsHandler().setZeals(-amount, true);
				p.getInventory().add(new Item(12160, 3*amount));
				p.getPacketSender().sendMessage("You've received "+3*amount+" Crimson charms for your "+amount+" Zeal"+s+".");
				break;
			case 23:
				p.getPointsHandler().setZeals(-amount, true);
				p.getInventory().add(new Item(12163, 2*amount));
				p.getPacketSender().sendMessage("You've received "+2*amount+" Blue charms for your "+amount+" Zeal"+s+".");
				break;
			}
			p.getPacketSender().sendString(31091, "   Zeals : "+p.getPointsHandler().getZeals());
			p.getPacketSender().sendString(31092, "   Zeals : "+p.getPointsHandler().getZeals());
			p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setProductChosen(-1);
		}

		public static int getExperienceReward(Player p, Skill skill) {
			return (skill == Skill.SLAYER || skill == Skill.PRAYER ? 20000 : 60000);
		}

		public static Dialogue buyDialogue(final Player p) {
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}

				@Override
				public int npcId() {
					return 8591;
				}

				@Override
				public String[] dialogue() { 
					if(p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getProductChosen() >= 20) {
						int amount = 5; String type = "Gold";
						switch(p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getProductChosen()) {
						case 21:
							amount = 4;
							type = "Green";
							break;
						case 22:
							amount = 3;
							type = "Crimson";
							break;
						case 23:
							amount = 2;
							type = "Blue";
							break;
						}
						return new String[] {"You will receive "+amount+" "+type+" charms per Zeal.", "How many Zeals would you like to spend?"};
					} else {
						Skill skill = Skill.forId(p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getProductChosen());
						return new String[] {"You will receive "+getExperienceReward(p, skill)+" "+skill.getPName()+" experience per Zeal.", "How many would you like to exchange?"};
					}
				}

				@Override
				public Dialogue nextDialogue() {
					return new Dialogue() {

						@Override
						public DialogueType type() {
							return DialogueType.STATEMENT;
						}

						@Override
						public DialogueExpression animation() {
							return DialogueExpression.LISTEN_LAUGH;
						}

						@Override
						public String[] dialogue() {
							return new String[] {""};
						}

						@Override
						public void specialAction() {
							p.getPacketSender().sendEnterAmountPrompt("Enter Amount of Zeals to spend:");
							p.getAttributes().setInputHandling(new EnterAmountOfZealsToBuy());
						}
					};
				}
			};
		}
	}

	public static void handleActivity(Player p, int activity, boolean add, boolean update) {
		if(p.getLocation() == Location.SOULWARS) {
			p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setActivity(add ? p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getActivity()+activity : activity);
			if(p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getActivity() > 30)
				p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().setActivity(30);
			else if(p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getActivity() < 10)
				p.getPacketSender().sendMessage("Warning! You must become more active or you might get kicked out of the game!");
			if(update)
				p.getPacketSender().sendPacket161(28516, SoulWars.blueTeam.contains(p) ? 0x3366FF : 0xff9900, Math.abs(p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getActivity()) * 5);
			if(p.getAttributes().getMinigameAttributes().getSoulWarsAttributes().getActivity() == 0) {
				SoulWars.leaveSoulWars(p);
				p.getPacketSender().sendMessage("You were kicked out of the game because you were inactive.");
			}
		}
	}

	public static int[] getRandomCoordsInArea(int[] area) {
		int minX = area[0];
		int minY = area[1];
		int maxX = area[2];
		int maxY = area[3];
		int diffX = maxX - minX;
		int diffY = maxY - minY;
		int randX = minX + Misc.getRandom(diffX);
		int randY = minY + Misc.getRandom(diffY);
		return new int[] { randX, randY };
	}

	public static boolean isWithin(int[] area, Player p) {
		if(p == null)
			return false;
		int x = p.getPosition().getX();
		int y = p.getPosition().getY();
		return (x >= area[0] && y >= area[1] && x <= area[2] && y <= area[3]);
	}
}
