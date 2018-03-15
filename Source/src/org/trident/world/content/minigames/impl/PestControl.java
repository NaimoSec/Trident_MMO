package org.trident.world.content.minigames.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Position;
import org.trident.model.Projectile;
import org.trident.model.Skill;
import org.trident.model.definitions.ItemDefinition;
import org.trident.model.definitions.NPCSpawns;
import org.trident.model.movement.MovementStatus;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.Following;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCAttributes;
import org.trident.world.entity.player.Player;

/** 
 * Pest control minigame
 * @author Gabbe
 */
public class PestControl {

	/**
	 * @note Stores player and State
	 */
	private static Map<Player, String> playerMap = Collections.synchronizedMap(new HashMap<Player, String>());

	/*
	 * Stores npcs
	 */
	private static CopyOnWriteArrayList<NPC> npcList = new CopyOnWriteArrayList<NPC>();

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
	private static int playersInBoat;

	/**
	 * Is a game running?
	 */
	private static boolean gameRunning;

	/**
	 * Moves a player in to the boat (waiting area)
	 * and adds the player to the map.
	 * @param p			The player entering
	 */
	public static void boardBoat(Player p) {
		if(p.getAdvancedSkills().getSummoning().getFamiliar() != null) {
			p.getPacketSender().sendMessage("Familiars are not allowed on the boat.");
			return;
		}
		if (p.getSkillManager().getCombatLevel() < 30) {
			p.getPacketSender().sendMessage("You must have a combat level of at least 30 to play this minigame.");
			return;
		}
		if(getState(p) == null)
			playerMap.put(p, WAITING);
		p.moveTo(new Position(2661, 2639, 0));
		p.getPacketSender().sendString(21117, "");
		p.getPacketSender().sendString(21118, "");
		p.getPacketSender().sendString(21008, "(Need 3 to 25 players)");
		p.getMovementQueue().setMovementStatus(MovementStatus.NONE).stopMovement();
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
		p.moveTo(new Position(2657, 2639, 0));
		p.getMovementQueue().setMovementStatus(MovementStatus.NONE).stopMovement();
	}

	/**
	 * Handles the static process required.
	 */
	public static void process() {
		playersInBoat = 0;
		for (Player player : playerMap.keySet()) {
			if(player == null)
				continue;
			String state = getState(player);
			if(state == null)
				continue;
			if(state.equals(WAITING))
				playersInBoat++;
		}
		if(playersInBoat == 0 && !gameRunning)
			return;
		updateBoatInterface();
		if(waitTimer > 0)
			waitTimer--;
		if(waitTimer <= 0) {
			if(!gameRunning)
				startGame();
			else {
				for (Player p : playerMap.keySet()) {
					if(p == null)
						continue;
					String state = getState(p);
					if(p != null && state != null && state.equals(WAITING))
						p.getPacketSender().sendMessage("A new Pest control game will be started once the current one has finished.");
				}
			}
			waitTimer = WAIT_TIMER;
		}
		if(gameRunning) {
			updateIngameInterface();
			if(Math.random() < 0.1)
				spawnRandomNPC();
			processNPCs();
			if(knight == null || (knight != null && knight.getConstitution() <= 0))
				endGame(false);
			else if (allPortalsDead())
				endGame(true);
		}
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
				p.getPacketSender().sendString(21006, "Next Departure: "+waitTimer+"");
				p.getPacketSender().sendString(21007, "Players Ready: "+playersInBoat+"");
				p.getPacketSender().sendString(21009, "Commendations: "+p.getPointsHandler().getCommendations()+"");
			}
		}
	}

	/**
	 * Updates the game interface for every player.
	 */
	private static void updateIngameInterface() {
		for (Player p : playerMap.keySet()) {
			if(p == null)
				continue;
			String state = getState(p);
			if(state != null && state.equals(PLAYING)) {
				p.getPacketSender().sendString(21111, getPortalText(0));
				p.getPacketSender().sendString(21112, getPortalText(1));
				p.getPacketSender().sendString(21113, getPortalText(2));
				p.getPacketSender().sendString(21114, getPortalText(3));
				String prefix = knight.getConstitution() < 500 ? "@red@" : knight.getConstitution() < 800 ? "@yel@" : "@gre@";
				p.getPacketSender().sendString(21115, knight != null && knight.getConstitution() > 0 ? prefix+"Knight's health: "+knight.getConstitution() : "Dead");
				prefix = p.getAttributes().getMinigameAttributes().getPestControlAttributes().getDamageDealt() == 0 ? "@red@" : p.getAttributes().getMinigameAttributes().getPestControlAttributes().getDamageDealt() < 100 ? "@yel@" : "@gre@";
				p.getPacketSender().sendString(21116, prefix+"Your damage : "+p.getAttributes().getMinigameAttributes().getPestControlAttributes().getDamageDealt()+"/100");
			}
		}
	}


	/**
	 * Starts a game and moves players in to the game.
	 */
	private static void startGame() {
		ArrayList<Player> playerList = null;
		boolean startGame = !gameRunning && playersInBoat >= 3;
		if(startGame) {
			gameRunning = true;
			spawnMainNPCs();
			playerList = new ArrayList<Player>();
		}
		for (Player player : playerMap.keySet()) {
			if (player != null) {
				String state = getState(player);
				if(state != null && state.equals(WAITING)) {
					if(startGame && gameRunning) {
						movePlayerToIsland(player);
						playerMap.put(player, PLAYING);
						playerList.add(player);
					} else
						player.getPacketSender().sendMessage("There must be at least 3 players in the boat before a game can start.");
				}
			}
		}
		if(startGame && gameRunning)
			unfreeze(playerList);
	}

	/**
	 * Teleports the player in to the game
	 */
	private static void movePlayerToIsland(Player p) {
		p.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE).stopMovement();
		p.getPacketSender().sendInterfaceRemoval();
		p.moveTo(new Position(2658, 2611, 0));
		if(p.getAdvancedSkills().getSummoning().getFamiliar() != null)
			p.getAdvancedSkills().getSummoning().getFamiliar().getSummonNpc().moveTo(p.getPosition().copy());
		DialogueManager.start(p, 358);
	}

	private static void unfreeze(final ArrayList<Player> list)  {
		if(list.size() <= 0)
			return;
		TaskManager.submit(new Task(3) {
			@Override
			public void execute() {
				for (Player p : list) {
					if(p == null)
						continue;
					if(p.getMovementQueue().getMovementStatus() == MovementStatus.CANNOT_MOVE)
						p.getMovementQueue().setMovementStatus(MovementStatus.NONE).stopMovement();
				}
				stop();
			}
		});
	}

	/**
	 * Ends a game and rewards players.
	 * @param won	Did the players manage to win the game?
	 */
	private static void endGame(boolean won) {
		ArrayList<Player> playerList = new ArrayList<Player>();
		for (Player p : playerMap.keySet()) {
			if(p == null)
				continue;
			String state = getState(p);
			if(state != null && state.equals(PLAYING)) {
				p.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE).stopMovement();
				playerList.add(p); //to unfreeze
				p.getPacketSender().sendInterfaceRemoval();
				p.moveTo(new Position(2657, 2639, 0));
				if(p.getAdvancedSkills().getSummoning().getFamiliar() != null)
					p.getAdvancedSkills().getSummoning().getFamiliar().getSummonNpc().moveTo(p.getPosition().copy());
				if (won && p.getAttributes().getMinigameAttributes().getPestControlAttributes().getDamageDealt() >= 50) {
					p.getPacketSender().sendMessage("The portals were successfully closed. You've been rewarded for your effort.");
					p.getPacketSender().sendMessage("You've received 13 Commendations and "+p.getSkillManager().getCombatLevel() * 50+" coins.");
					p.getPointsHandler().setCommendations(13, true);
					p.getPointsHandler().refreshPanel();
					p.getInventory().add(995, p.getSkillManager().getCombatLevel() * 50);
					p.restart();
				} else if (won)
					p.getPacketSender().sendMessage("You didn't participate enough to receive a reward.");
				else {
					p.getPacketSender().sendMessage("You failed to kill all the portals in time.");
					DialogueManager.start(p, 356);
				}
				p.getAttributes().getMinigameAttributes().getPestControlAttributes().setDamageDealt(0);
			}
		}
		/*
		 * Unfreeze players
		 */
		unfreeze(playerList);
		/*
		 * Reset the map
		 */
		playerMap.clear();
		for(Player p : World.getPlayers()) {
			if(p != null && p.getLocation() == Location.PEST_CONTROL_BOAT)
				playerMap.put(p, WAITING);
		}
		for(NPC n : npcList) {
			if(n == null)
				continue;
			if (n.getLocation() == Location.PEST_CONTROL_GAME && n.getCharacterToFollow() == null){
				n.setConstitution(0);
				World.deregister(n);
				n = null;
			}
		}
		npcList.clear();
		for(int i = 0; i < portals.length; i++)
			portals[i] = null;
		knight = null;
		gameRunning = false;
	}

	/*==========================================================================================================*/
	/*NPC STUFF*/
	/**
	 * Spawns the game's key/main NPC's on to the map
	 */
	private static void spawnMainNPCs() {
		int knightHealth = 1200 - (playersInBoat * 20);
		int portalHealth = 700 + playersInBoat * 140;
		knight = spawnPCNPC(3782, new Position(2656,2592), knightHealth, 0, 100, 0); //knight
		portals[0] = spawnPCNPC(6142, new Position(2628,2591), portalHealth,0,100,0); //purple
		portals[1] = spawnPCNPC(6143, new Position(2680,2588), portalHealth, 0, 100, 0); //red
		portals[2] = spawnPCNPC(6144, new Position(2669,2570), portalHealth, 0, 100, 0); //blue
		portals[3] = spawnPCNPC(6145, new Position(2645,2569), portalHealth, 0, 100, 0); //yellow
		npcList.add(knight);
		for(NPC n : portals)
			npcList.add(n);
	}

	/**
	 * Gets the text which shall be sent on to a player's interface
	 * @param i		The portal index to get information about
	 * @return		Information about the portal with the index specified
	 */
	private static String getPortalText(int i) {
		return (portals[i] != null && (portals[i].getConstitution() > 0 && portals[i].getConstitution() > 0)) ? Integer.toString(portals[i].getConstitution()) : "Dead";
	}

	/**
	 * Checks if all portals are dead (if true, the game will end and the players will win)
	 * @return		true if all portals are dead, otherwise false
	 */
	private static boolean allPortalsDead() {
		int count = 0;
		for(int i = 0; i < portals.length; i++) {
			if(portals[i] != null) {
				if(portals[i].getConstitution() <= 0 || portals[i].getConstitution() <= 0) {
					count++;
				}
			}
		}
		return count >= 4;	
	}

	/**
	 * Processes all NPC's within Pest control
	 */
	private static void processNPCs() {
		for(NPC npc : npcList) {
			if(npc == null)
				continue;
			if(npc.getLocation() == Location.PEST_CONTROL_GAME && npc.getConstitution() > 0) {
				for(PestControlNPC PCNPC: PestControlNPC.values()){
					if(npc.getId() >= PCNPC.lowestNPCID && npc.getId() <= PCNPC.highestNPCID){
						processPCNPC(npc, PCNPC);
						break;
					}
				}
			}
		}
	}

	/**
	 * Spawns a random NPC onto the map
	 */
	private static void spawnRandomNPC(){
		for(int i = 0; i < portals.length; i++){
			if(portals[i] != null && Math.random() > 0.5){
				PestControlNPC luckiest = PestControlNPC.values()[((int)(Math.random()*PestControlNPC.values().length))];
				if(luckiest != null){
					npcList.add(spawnPCNPC(luckiest.getLowestNPCID()+((int) (Math.random()*(luckiest.getHighestNPCID()-luckiest.getLowestNPCID()))), new Position(portals[i].getPosition().getX(), portals[i].getPosition().getY() - 1 , 0), 400, Misc.getRandom(70), Misc.getRandom(70), Misc.getRandom(70)));
				}
			}
		}
	}

	/**
	 * Processes a PC npc
	 * @param npc		The NPC to process
	 * @param PCNPC		The data of the npc to process
	 */
	private static void processPCNPC(NPC npc, PestControlNPC _npc) {
		if(knight == null || npc == null || _npc == null)
			return;
		switch(_npc){
		case SPINNER:
			processSpinner(npc);
			break;
		case SHIFTER:
			processShifter(npc, _npc);
			break;
		case TORCHER:
			processDefiler(npc, _npc);
			break;
		case DEFILER:
			processDefiler(npc, _npc);
			break;
		}
	}

	/**
	 * Processes the spinner NPC
	 * Finds the closest portal, walks to it and heals it if injured.
	 * @param npc	The Spinner NPC
	 */
	private static void processSpinner(NPC npc) {
		NPC closestPortal = null;
		int distance = Integer.MAX_VALUE;
		for(int i = 0; i < portals.length; i++){
			if(portals[i] != null && portals[i].getConstitution() > 0 && portals[i].getConstitution() > 0){
				int distanceCandidate = distance(npc.getPosition().getX(), npc.getPosition().getY(), portals[i].getPosition().getX(), portals[i].getPosition().getY());
				if(distanceCandidate < distance){
					closestPortal = portals[i];
					distance = distanceCandidate;
				}
			}
		}
		if(closestPortal == null)
			return;
		npc.setEntityInteraction(closestPortal);
		if(distance <= 3 && closestPortal.getConstitution() < closestPortal.getDefaultAttributes().getConstitution()){
			npc.performAnimation(new Animation(3911));
			closestPortal.setConstitution(closestPortal.getConstitution() + 2);
			if(closestPortal.getConstitution() > closestPortal.getDefaultAttributes().getConstitution())
				closestPortal.setConstitution(closestPortal.getDefaultAttributes().getConstitution());
		} else if(closestPortal != null){
			npc.getMovementQueue().walkStep(GetMove(npc.getPosition().getX(), closestPortal.getPosition().getX()), GetMove(npc.getPosition().getY(), closestPortal.getPosition().getY()));
			return;
		}
	}

	private static void processShifter(NPC npc, PestControlNPC npc_) {
		if(npc != null && knight != null) {
			if(isFree(npc, npc_)){
				if(distance(npc.getPosition().getX(), npc.getPosition().getY(), knight.getPosition().getX(), knight.getPosition().getY()) > 5){
					int npcId = npc.getId();
					Position pos = new Position(knight.getPosition().getX()+Misc.getRandom(3), knight.getPosition().getY()+Misc.getRandom(2), npc.getPosition().getZ());
					World.deregister(npc);
					npcList.remove(npc);
					npcList.add(spawnPCNPC(npcId, pos, 200, 10, 10, 10));
				} else {
					if(distance(npc.getPosition().getX(), npc.getPosition().getY(), knight.getPosition().getX(), knight.getPosition().getY()) > 1){
						npc.getMovementQueue().walkStep(GetMove(npc.getPosition().getX(), knight.getPosition().getX()), GetMove(npc.getPosition().getY(), knight.getPosition().getY()));
					} else {
						CombatHandler.resetAttack(npc);
						int max = 5 + (npc.getDefinition().getLevel() / 9);
						attack(npc, knight, 3901, max, CombatIcon.MELEE);
					}
				}
			}
			if(npc.getPosition().copy().equals(knight.getPosition().copy()))
				Following.stepAway(npc);
		}
	}

	private static void processDefiler(final NPC npc, final PestControlNPC npc_) {
		if(npc != null) {
			if(isFree(npc, npc_)){
				if(distance(npc.getPosition().getX(), npc.getPosition().getY(), knight.getPosition().getX(), knight.getPosition().getY()) > 20){
					npc.getMovementQueue().walkStep(GetMove(npc.getPosition().getX(), knight.getPosition().getX()), GetMove(npc.getPosition().getY(), knight.getPosition().getY()));
				} else {
					if(Math.random() <= 0.04)
						for(Player p : playerMap.keySet()) {
							if(p != null) {
								String state = getState(p);
								if(state.equals(PLAYING))
									p.getPacketSender().sendGlobalProjectile(new Projectile(npc.getPosition().copy(), knight.getPosition().copy(), new Graphic(1508, GraphicHeight.MIDDLE), 5, 50, 80), knight);
							}
						}
					TaskManager.submit(new Task(1) {
						@Override
						public void execute() {
							int max = 7 + (npc.getDefinition().getLevel() / 9);
							attack(npc, knight, npc_ == PestControlNPC.DEFILER ? 3920 : 3882, max, npc_ == PestControlNPC.DEFILER ? CombatIcon.RANGED : CombatIcon.MAGIC);
							stop();
						}
					});
				}
			}
		}
	}

	private static boolean attack(NPC npc, NPC knight, int anim, int maxhit, CombatIcon icon) {
		if(knight == null || npc == null)
			return false;
		npc.setEntityInteraction(knight);
		npc.setPositionToFace(knight.getPosition());
		if(npc.getCombatAttributes().getAttackDelay() == 0) {
			int damage = ((int) (Math.random()*maxhit));
			npc.performAnimation(new Animation(anim));
			knight.setDamage(new Damage(new Hit(damage, icon, Hitmask.RED)));
			knight.getCombatAttributes().setLastDamageReceived(System.currentTimeMillis());
			npc.getCombatAttributes().setAttackDelay(3 + Misc.getRandom(3)).setLastDamageReceived(System.currentTimeMillis());
			return true;
		}
		return false;
	}

	private static int distance(int x, int y, int dx, int dy){
		int xdiff = x - dx;
		int ydiff = y - dy;
		return (int) Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	public static int GetMove(int Place1,int Place2) { 
		if ((Place1 - Place2) == 0) {
			return 0;
		} else if ((Place1 - Place2) < 0) {
			return 1;
		} else if ((Place1 - Place2) > 0) {
			return -1;
		}
		return 0;
	}

	private static boolean isFree(NPC npc, PestControlNPC npc_) {
		if(npc.getCombatAttributes().getCurrentEnemy() == null){
			return true;
		} else {
			if(World.getNpcs().get(npc.getCombatAttributes().getCurrentEnemy().getIndex()).getCombatAttributes().getCurrentEnemy().getIndex() == npc.getIndex()){
				return false;
			} else {
				if(npc_.tries++ >= 12){
					npc_.tries = 0;
					CombatHandler.resetAttack(npc);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	public static PestControl[] runningGames = new PestControl[1];
	private int id;

	public int getId() {
		return id;
	}

	public PestControl(int id){
		this.id = id;
	}

	public enum PestControlNPC{
		SPINNER(3747, 3751),
		//SPLATTER(3727, 3731),
		SHIFTER(3732, 3741),
		TORCHER(3752, 3761),
		DEFILER(3762, 3771);
		//BRAWLER(3772, 3776);

		private final int lowestNPCID, highestNPCID;

		PestControlNPC(int lowestNPCID, int highestNPCID){
			this.lowestNPCID = lowestNPCID;
			this.highestNPCID = highestNPCID;
		}

		public int getLowestNPCID() {
			return lowestNPCID;
		}

		public int getHighestNPCID() {
			return highestNPCID;
		}

		public int tries;

	}

	public static final int WAIT_TIMER = 40;

	public static int waitTimer = WAIT_TIMER;
	private static NPC[] portals = new NPC[4];
	public static NPC knight;

	/**
	 * Handles the shop
	 * @param p			The player buying something from the shop
	 * @param item		The item which the player is buying
	 * @param id		The id of the item/skill which the player is buying
	 * @param amount	The amount of the item/skill xp which the player is buying
	 * @param cost		The amount it costs to buy this item
	 */
	public static void buyFromShop(Player p, boolean item, int id, int amount, int cost){
		if(p.getPointsHandler().getCommendations() < cost) {
			p.getPacketSender().sendMessage("You don't have enough Commendations to purchase this.");
			return;
		}
		final String name = ItemDefinition.forId(id).getName();
		final String comm = cost > 1 ? "Commendations" : "Commendation";
		if (!item) {
			p.getPointsHandler().setCommendations((p.getPointsHandler().getCommendations() - cost), false);
			Skill skill = Skill.forId(id);
			int xp = amount * cost;
			p.getSkillManager().addExperience(skill, xp, false);
			p.getPacketSender().sendMessage("You have purchased "+xp+" "+Misc.formatText(skill.toString().toLowerCase())+" XP.");
		} else {
			int id2 = 0;
			if(id > 19784 && id < 19787) {
				if(id == 19785)
					id2 = 8839;
				if(id == 19786)
					id2 = 8840;
				if(p.getInventory().getAmount(id2) >= 1)
					p.getInventory().delete(id2, 1);
				else {
					p.getPacketSender().sendMessage("You need to have "+Misc.anOrA(name)+" "+name+" to purhcase this uppgrade.");
					return;
				}
			}
			if(p.getInventory().getFreeSlots() > 0 || id2 != 0) {
				p.getPointsHandler().setCommendations((p.getPointsHandler().getCommendations() - cost), false);
				p.getInventory().add(id, amount);
				p.getPointsHandler().refreshPanel();
				p.getPacketSender().sendMessage("You have purchased "+Misc.anOrA(name)+" "+name+" for "+cost+" "+comm+".");
			} else
				p.getPacketSender().sendMessage("Please get some free inventory space before attempting to buy this.");	
		}
		p.getPacketSender().sendString(18729, Integer.toString(p.getPointsHandler().getCommendations()));
	}

	public static NPC spawnPCNPC(int id, Position pos, int constitution, int attackLevel, int defenceLevel, int strengthLevel) {
		NPC n = NPCSpawns.createNPC(id, pos, new NPCAttributes().setConstitution(constitution).setAttackLevel(attackLevel).setDefenceLevel(defenceLevel).setStrengthLevel(strengthLevel).setAttackable(true), new NPCAttributes().setConstitution(constitution));
		World.register(n);
		return n;
	}

}