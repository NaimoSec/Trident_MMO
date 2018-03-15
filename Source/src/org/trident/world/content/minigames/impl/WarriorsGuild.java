package org.trident.world.content.minigames.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.GameObject;
import org.trident.model.Graphic;
import org.trident.model.GroundItem;
import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.RegionInstance;
import org.trident.model.RegionInstance.RegionInstanceType;
import org.trident.model.definitions.NPCSpawns;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCData.CustomNPCData;
import org.trident.world.entity.player.Player;

public class WarriorsGuild {

	/*
	 * The armors required to animate an NPC
	 */
	private static final int[][] ARMOR_DATA = {{1075, 1117, 1155, 4278}, {1067, 1115, 1153, 4279}, {1069, 1119, 1157, 4280}, {1077, 1125, 1165, 4281}, {1071, 1121, 1159, 4282}, {1073, 1123, 1161, 4283}, {1079, 1127, 1163, 4284}};

	/*
	 * Has the player spawned armour?
	 */
	public static final int ARMOUR_SPAWNED_INDEX = 0;

	/*
	 * Has the player talked to Kamfreema?
	 */
	private static final int TALKED_TO_KAMFREENA_INDEX = 1;

	/**
	 * Handles what happens when a player uses an item on the animator.
	 * @param player	The player
	 * @param item		The item the player is using
	 * @param object	That animator object which the player is using an item on
	 */
	public static boolean itemOnAnimator(final Player player, final Item item, final GameObject object) {
		if(player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[ARMOUR_SPAWNED_INDEX] && player.getRights() != PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage("You have already spawned some animated armour.");
			return true;
		} else {
			for(int i = 0; i < ARMOR_DATA.length; i++) {
				for(int f = 0; f < ARMOR_DATA[0].length; f++) {
					if(item.getId() == ARMOR_DATA[i][f]) {
						if(player.getInventory().contains(ARMOR_DATA[i][0]) && player.getInventory().contains(ARMOR_DATA[i][1]) && player.getInventory().contains(ARMOR_DATA[i][2])) {
							final int items[] = new int[] {ARMOR_DATA[i][0], ARMOR_DATA[i][1], ARMOR_DATA[i][2]};
							final CustomNPCData npcData = CustomNPCData.forNpcId(ARMOR_DATA[i][3]);
							if(items != null && npcData != null) {
								for(int a = 0; a < items.length; a++)
									player.getInventory().delete(items[a], 1);
								player.getAttributes().setRegionInstance(new RegionInstance(player, RegionInstanceType.WARRIORS_GUILD));
								player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[ARMOUR_SPAWNED_INDEX] = true;
								object.performGraphic(new Graphic(1930));
								TaskManager.submit(new Task(2) {
									@Override
									public void execute() {
										NPC npc_ = NPCSpawns.createCustomNPC(npcData, new Position(player.getPosition().getX(), player.getPosition().getY() + 1));
										npc_.forceChat("I'M ALIVE!!!!").setEntityInteraction(player).getCombatAttributes().setAttackDelay(2);
										npc_.getCombatAttributes().setSpawnedFor(player);
										player.setPositionToFace(npc_.getPosition());
										CombatHandler.setAttack(npc_, player);
										World.register(npc_);
										player.getAttributes().getRegionInstance().getNpcsList().add(npc_);
										stop();
									}
								});
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Handles a drop after an NPC is slain in the Warriors guild
	 * @param player	The player to handle the drop for
	 * @param npc		The npc which will drop something
	 */
	public static void handleDrop(Player player, NPC npc) {
		if(player == null || npc == null)
			return;
		/*
		 * Tokens
		 */
		if(npc.getId() >= 4278 && npc.getId() <= 4284) {
			if(player.getAttributes().getRegionInstance() != null)
				player.getAttributes().getRegionInstance().getNpcsList().remove(npc);
			int[] armour = null;
			for(int i = 0; i < ARMOR_DATA.length; i++) {
				if(ARMOR_DATA[i][3] == npc.getId()) {
					armour = new int[] {ARMOR_DATA[i][0], ARMOR_DATA[i][1], ARMOR_DATA[i][2]};
					break;
				}
			}
			if(armour != null) {
				for(int i : armour)
					GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(i), npc.getPosition().copy(), player.getUsername(), false, 80, true, 80));
				player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[0] = false;
				GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(8851, getTokenAmount(npc.getId())), npc.getPosition().copy(), player.getUsername(), false, 80, true, 80));
				armour = null;
			}
		} else if(npc.getId() == 4291 && player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[1] && player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[2]) {
			if(Misc.getRandom(5) == 1)
				GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(getDefender(player)), npc.getPosition().copy(), player.getUsername(), false, 100, true, 100));
		}
	}

	private static final String[][] DEFENDERS = {{"13262", "Dragon"}, {"8850", "Rune"}, {"8849", "Adamant"}, {"8848", "Mithril"}, {"8847", "Black"}, {"8846", "Steel"}, {"8845", "Iron"}, {"8844", "Bronze"}};

	/**
	 * @return Returns the defender dropped in text.
	 */
	public static String getDefenderString(Player p) {
		int defender = getDefender(p);
		for(int i = 0; i < DEFENDERS.length; i++) {
			if(Integer.parseInt(DEFENDERS[i][0]) == defender) {
				return DEFENDERS[i][1];
			}
		}
		return DEFENDERS[0][1];
	}

	/**
	 * Gets the player's best defender
	 * @param player	The player to search items for
	 * @return			The best defender's item id
	 */
	public static int getDefender(Player player) {
		if(!player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[TALKED_TO_KAMFREENA_INDEX])
			return Integer.parseInt(DEFENDERS[6][0]);
		for(int i = 0; i < DEFENDERS.length; i++) {
			try {
				if(player.getInventory().contains(Integer.parseInt(DEFENDERS[i][0])))
					return Integer.parseInt(DEFENDERS[i - 1][0]);
			} catch(Exception e) {
				return Integer.parseInt(DEFENDERS[0][0]);
			}
		}
		return Integer.parseInt(DEFENDERS[6][0]);
	}

	/**
	 * Warriors guild dialogue, handles what Kamfreena says.
	 * @param Player		The player to show the dialogue for acording to their stats.
	 */
	public static Dialogue warriorsGuildDialogue(final Player player) {
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
			public String[] dialogue() {
				if(!player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[TALKED_TO_KAMFREENA_INDEX])
					return new String[] {"Since you haven't shown me any Defenders,", "I'll release some Cyclopes which might drop", "Bronze Defenders for you to start off with."};
				else
					return new String[] {"I'll release some Cyclops which might drop", ""+getDefenderString(player)+" Defenders for you. If you manage to get one,", "Make sure you come and show me.", "Good luck, warrior."};
			}

			@Override
			public int npcId() {
				return 4289;
			}

		};
	}

	/**
	 * The warriors guild task
	 */
	public static void handleTokenRemoval(final Player player) {
		if(player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[2])
			return;
		player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[2] = true;
		TaskManager.submit(new Task(40, player, false) {
			@Override
			public void execute() {
				if(!player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[2]) {
					this.stop();
					return;
				}
				if(player.getInventory().contains(8851)) {
					player.getInventory().delete(8851, 10);
					player.performGraphic(new Graphic(1368));
					player.getPacketSender().sendMessage("Some of your tokens crumble to dust..");
				} else {
					player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[2] = false;
					CombatHandler.resetAttack(player);
					player.getMovementQueue().stopMovement();
					player.moveTo(new Position(2844, 3539, 2));
					player.getPacketSender().sendMessage("You have run out of tokens.");
					resetCyclopsCombat(player);
					this.stop();
				}
			}
		});
	}

	/**
	 * Gets the amount of tokens to receive from an npc
	 * @param npc	The npc to check how many tokens to receive from
	 * @return		The amount of tokens to receive as a drop
	 */
	private static int getTokenAmount(int npc) {
		int[] animatedArmor = {4278, 4279, 4280, 4281, 4282, 4283, 4284};
		int[] tokens = {5, 8, 14, 20, 25, 30, 40};
		for(int f = 0; f < animatedArmor.length; f++) {
			if (npc == animatedArmor[f]) {
				return tokens[f];
			}
		}
		return 5;
	}

	/**
	 * Resets any cyclops's combat who are in combat with player
	 * @param player	The player to check if cyclop is in combat with
	 */
	public static void resetCyclopsCombat(Player player) {
		for(NPC n : player.getAttributes().getLocalNpcs()) {
			if(n == null)
				continue;
			if(n.getId() == 4291 && n.getLocation() == Location.WARRIORS_GUILD && n.getCombatAttributes().getCurrentEnemy() != null && n.getCombatAttributes().getCurrentEnemy() == player) {
				CombatHandler.resetAttack(n);
				n.getMovementQueue().stopMovement();
			}
		}
	}
}
