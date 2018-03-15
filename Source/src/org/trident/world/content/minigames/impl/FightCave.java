package org.trident.world.content.minigames.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Position;
import org.trident.model.RegionInstance;
import org.trident.model.RegionInstance.RegionInstanceType;
import org.trident.model.definitions.NPCSpawns;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCData.CustomNPCData;
import org.trident.world.entity.player.Player;

/**
 * Handles the Fight cave minigame
 * @author Gabbe
 */
public class FightCave {

	public static void enterCave(Player player, boolean login) {
		if(!login)
			player.moveTo(new Position(2413, 5117, player.getIndex() * 4));
		DialogueManager.start(player, DialogueManager.getDialogues().get(login ? 249 : 248));
		player.getAttributes().setRegionInstance(new RegionInstance(player, RegionInstanceType.FIGHT_CAVE));
		spawnWave(player, player.getAttributes().getMinigameAttributes().getFightCaveAttributes().getWave(), login);
	}

	public static void leaveCave(Player player, boolean died) {
		player.getAttributes().getMinigameAttributes().getFightCaveAttributes().setWave(0);
		CombatHandler.resetAttack(player);
		player.moveTo(new Position(2439, 5169));
		if(died)
			DialogueManager.start(player, 250);
		if(player.getAttributes().getRegionInstance() != null)
			player.getAttributes().getRegionInstance().destruct();
		player.restart();
	}

	public static void spawnWave(final Player player, final int wave, final boolean login) {
		TaskManager.submit(new Task(login ? 15 : 4, player, false) {
			@Override
			public void execute() {
				if(player.getAttributes().getRegionInstance() == null) {
					stop();
					return;
				}
				player.getPacketSender().sendString(37308, "@yel@Wave: "+wave);
				int amountOfNpcs = WAVES[wave].length;
				player.getAttributes().getMinigameAttributes().getFightCaveAttributes().setWave(wave).setAmountToKill(amountOfNpcs);
				for(int i = 0; i < amountOfNpcs; i++) {
					CustomNPCData data = CustomNPCData.forNpcId(WAVES[wave][i]);
					if(data.npcId == 2630)
						player.getAttributes().getMinigameAttributes().getFightCaveAttributes().setAmountToKill(player.getAttributes().getMinigameAttributes().getFightCaveAttributes().getAmountToKill() + 1);
					NPC n = NPCSpawns.createCustomNPC(data, new Position(coordinates[i][0],coordinates[i][1], player.getPosition().getZ()));
					n.getCombatAttributes().setSpawnedFor(player);
					CombatHandler.setAttack(n, player);
					World.register(n);
					player.getAttributes().getRegionInstance().getNpcsList().add(n);
				}
				if(wave == 32) //Jad
					DialogueManager.start(player, 251);
				if(login)
					player.getPacketSender().sendInterfaceRemoval();
				stop();
			}
		});
	}

	public static void handleCaveDeath(final Player player, NPC n) {
		if(player.getAttributes().getRegionInstance() != null)
			player.getAttributes().getRegionInstance().getNpcsList().remove(n);
		if(n.getId() == 2630) {
			CustomNPCData data = CustomNPCData.forNpcId(2738);
			for(int i = 0; i < 2; i++) {
				NPC n2 = NPCSpawns.createCustomNPC(data, new Position(n.getPosition().getX(), n.getPosition().getY() - (i + 1), n.getPosition().getZ()));
				n2.getCombatAttributes().setSpawnedFor(player);
				CombatHandler.setAttack(n2, player);
				World.register(n2);
				player.getAttributes().getRegionInstance().getNpcsList().add(n2);
			}
			return;
		}
		player.getAttributes().getMinigameAttributes().getFightCaveAttributes().setAmountToKill(player.getAttributes().getMinigameAttributes().getFightCaveAttributes().getAmountToKill() - 1);
		if(n.getId() == 2745) {
			leaveCave(player, false);
			DialogueManager.start(player, 252);
			player.getInventory().add(6570, 1).add(6529, 7000 + Misc.getRandom(3000));
			return;
		}
		if(player.getAttributes().getMinigameAttributes().getFightCaveAttributes().getAmountToKill() <= 0) {
			TaskManager.submit(new Task(4, player, false) {
				@Override
				public void execute() {
					player.getPacketSender().sendInterfaceRemoval();
					player.getAttributes().getMinigameAttributes().getFightCaveAttributes().setWave(player.getAttributes().getMinigameAttributes().getFightCaveAttributes().getWave()+1);
					spawnWave(player, player.getAttributes().getMinigameAttributes().getFightCaveAttributes().getWave(), false);
					stop();
				}
			});
		}
	}

	//Ripped these from r-s, thx
	private final static int[][] coordinates = { { 2398, 5086 }, { 2387, 5095 },{ 2407, 5098 }, { 2417, 5082 }, { 2390, 5076 }, { 2410, 5090 } };
	private final static int[][] WAVES = {/*{2627},{2627,2627},{2630},{2630,2627},{2630,2627,2627},{2630,2630},{2631},{2631,2627},{2631,2627,2627},{2631,2630},{2631,2630,2627},{2631,2630,2627,2627},{2631,2630,2630},{2631,2631},{2741},{2741,2627},{2741,2627,2627},{2741,2630},{2741,2630,2627},{2741,2630,2627,2627},{2741,2630,2630},{2741,2631},{2741,2631,2627},{2741,2631,2627,2627},{2741,2631,2630},{2741,2631,2630,2627},{2741,2631,2630,2627,2627},{2741,2631,2630,2630},{2741,2631,2631},{2741,2741},*/{2743},{2743,2627},{2743,2627,2627},{2743,2630},{2743,2630,2627},{2743,2630,2627,2627},{2743,2630,2630},{2743,2631},{2743,2631,2627},{2743,2631,2627,2627},{2743,2631,2630},{2743,2631,2630,2627},{2743,2631,2630,2627,2627},{2743,2631,2630,2630},{2743,2631,2631},{2743,2741},{2743,2741,2627},{2743,2741,2627,2627},{2743,2741,2630},{2743,2741,2630,2627},{2743,2741,2630,2627,2627},{2743,2741,2630,2630},{2743,2741,2631},{2743,2741,2631,2627},{2743,2741,2631,2627,2627},{2743,2741,2631,2630},{2743,2741,2631,2630,2627},{2743,2741,2631,2630,2627,2627},{2743,2741,2631,2630,2630},{2743,2741,2631,2631},{2743,2741,2741},{2743,2743},{2745}};
}
