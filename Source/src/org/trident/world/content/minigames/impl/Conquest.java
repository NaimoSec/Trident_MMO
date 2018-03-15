package org.trident.world.content.minigames.impl;

import org.trident.model.GameObject;
import org.trident.model.Position;
import org.trident.model.RegionInstance;
import org.trident.model.RegionInstance.RegionInstanceType;
import org.trident.model.definitions.NPCSpawns;
import org.trident.world.World;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCData.CustomNPCData;
import org.trident.world.entity.player.Player;

/**
 * Handles the Fight Arena minigame
 * @author Gabbe
 */
public class Conquest {

	/**
	 * Enters the arena and spawns waves.
	 * @param p		The player entering the arena
	 */
	public static void enterArena(Player player) {
		player.moveTo(new Position(2607, 9667, player.getIndex() * 4));
		player.getAttributes().setRegionInstance(new RegionInstance(player, RegionInstanceType.CONQUEST));
		player.getAttributes().getMinigameAttributes().getConquestArenaAttributes().setWave(0);
		DialogueManager.start(player, 396);
		for(int x = 2606; x <= 2608; x++)
			player.getPacketSender().sendObject(new GameObject(1, new Position(x, 9679)));
		spawnWave(player, 0);
		player.getAttributes().getMinigameAttributes().getConquestArenaAttributes().setInArena(true);
	}

	/**
	 * Leaves the arena
	 * @param player	The player leaving the arena
	 */
	public static void leaveArena(Player player) {
		CombatHandler.resetAttack(player);
		player.getMovementQueue().stopMovement();
		player.moveTo(new Position(2607, 9681));
		player.getAttributes().getMinigameAttributes().getConquestArenaAttributes().setInArena(false);
		if(player.getAttributes().getRegionInstance() != null)
			player.getAttributes().getRegionInstance().destruct();
		player.restart();
	}

	/**
	 * Spawns a wave (set of npcs) for a player
	 * @param player		The player to spawn the wave for
	 * @param wave			The wave to spawn
	 */
	public static void spawnWave(Player player, int wave) {
		if(wave >= 13 || player.getAttributes().getRegionInstance() == null) {
			leaveArena(player);
			player.getPacketSender().sendMessage("Well done! You managed to complete all waves.");
			return;
		}
		player.getAttributes().getMinigameAttributes().getConquestArenaAttributes().setWave(wave);
		int amountOfNpcs = waves[wave].length;
		player.getAttributes().getMinigameAttributes().getConquestArenaAttributes().setAmountToKill(amountOfNpcs);
		for(int i = 0; i < amountOfNpcs; i++) {
			NPC n = NPCSpawns.createCustomNPC(CustomNPCData.forNpcId(waves[wave][i]), new Position(coordinates[i][0],coordinates[i][1], player.getPosition().getZ()));
			if(n.getId() == ARCHER || n.getId() == ZAMORAK_RANGER || n.getId() == BATTLE_MAGE)
				n.forceChat("Prepare to die!");
			n.getCombatAttributes().setSpawnedFor(player);
			CombatHandler.setAttack(n, player);
			World.register(n);
			player.getAttributes().getRegionInstance().getNpcsList().add(n);
		}
	}

	/**
	 * Handles the death of an NPC
	 * @param p		The player who has killed the npc
	 * @param n		The NPC which has died
	 */
	public static void handleNPCDeath(Player p, NPC n) {
		if(n == null || p == null || p.getAttributes().getRegionInstance() == null)
			return;
		int points = 1;
		switch(n.getId()) {
		case ZAMORAK_RANGER:
		case GREEN_DRAGON:
			points = 3;
			break;
		case ARCHER:
		case BANDIT:
		case SHADOW_SPIDER:
			points = 2;
			break;
		case BATTLE_MAGE:
		case SKELETON:
		case SCORPION:
			points = 1;
			break;
		}
		p.getPointsHandler().setConquestPoints(points, true);
		p.getAttributes().getRegionInstance().getNpcsList().remove(n);
		p.getAttributes().getMinigameAttributes().getConquestArenaAttributes().setAmountToKill(p.getAttributes().getMinigameAttributes().getConquestArenaAttributes().getAmountToKill() - 1);
		if(p.getAttributes().getMinigameAttributes().getConquestArenaAttributes().getAmountToKill() <= 0)
			spawnWave(p, p.getAttributes().getMinigameAttributes().getConquestArenaAttributes().getWave()+1);
	}

	private static final int ARCHER = 27, BATTLE_MAGE = 912, SKELETON = 90, SCORPION = 108, SHADOW_SPIDER = 58, BANDIT = 1880, ZAMORAK_RANGER = 6365, GREEN_DRAGON = 941;

	/*
	 * Waves
	 */
	private static final int[][] waves = {
		{ARCHER},
		{ARCHER, ARCHER},
		{BATTLE_MAGE},
		{BATTLE_MAGE, ARCHER},
		{BATTLE_MAGE, BATTLE_MAGE},
		{SKELETON},
		{SKELETON, BATTLE_MAGE},
		{SCORPION, ARCHER},
		{SHADOW_SPIDER, SCORPION},
		{BANDIT, ARCHER},
		{ZAMORAK_RANGER},
		{ARCHER, ZAMORAK_RANGER},
		{GREEN_DRAGON},
	};

	/*
	 * Coordinates
	 */
	private static final int[][] coordinates = {
		{2607, 9676},
		{2608, 9676},
		{2604, 9670},
		{2604, 9670},
		{2607, 9670},
		{2609, 9670},
		{2607, 9673},
		{2610, 9670},
		{2607, 9668},
		{2608, 9668},
		{2604, 9670},
		{2609, 9670},
		{2606, 9672},
	};
}
