package org.trident.world.content.skills.impl.mining;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.GameObject;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.Achievements;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.Locations;
import org.trident.world.content.skills.impl.mining.MiningData.Ores;
import org.trident.world.entity.player.Player;

public class Mining {
	
	public static void startMining(final Player player, final GameObject oreObject) {
		player.getSkillManager().stopSkilling();
		if(!Locations.goodDistance(player.getPosition().copy(), oreObject.getPosition(), 1) && oreObject.getId() != 24444)
			return;
		if(player.getInventory().getFreeSlots() == 0) {
			player.getPacketSender().sendMessage("You do not have any free inventory space left.");
			return;
		}
		final Ores o = MiningData.forRock(oreObject.getId());
		final boolean giveGem = o != Ores.Rune_essence && o != Ores.Pure_essence;
		final int reqCycle = Misc.getRandom(o.getTicks() - 1);
		if (o != null) {
			final int pickaxe = MiningData.getPickaxe(player);
			final int miningLevel = player.getSkillManager().getCurrentLevel(Skill.MINING);
			if (pickaxe > 0) {
				if (miningLevel >= o.getLevelReq()) {
					final MiningData.Pickaxe p = MiningData.forPick(pickaxe);
					if (miningLevel >= p.getReq()) {
						player.performAnimation(new Animation(p.getAnim()));
						final int delay = o.getTicks() - MiningData.getReducedTimer(player, p);
						player.getSkillManager().getSkillAttributes().setCurrentTask(new Task(delay >= 2 ? delay : 1, player, false) {
							int cycle = 0;
							@Override
							public void execute() {
								if(player.getInventory().getFreeSlots() == 0) {
									player.performAnimation(new Animation(65535));
									stop();
									player.getPacketSender().sendMessage("You do not have any free inventory space left.");
									return;
								}
								if (cycle != reqCycle) {
									cycle++;
									player.performAnimation(new Animation(p.getAnim()));
								}
								if (giveGem && Misc.getRandom(50) == 15) {
									player.getInventory().add(MiningData.RANDOM_GEMS[(int)(MiningData.RANDOM_GEMS.length * Math.random())], 1);
									player.getPacketSender().sendMessage("You've found a gem!");
								}
								if (cycle == reqCycle) {
									if(o.getItemId() == 438)
										Achievements.handleAchievement(player, Achievements.Tasks.TASK3);
									player.getInventory().add(o.getItemId(), 1);
									player.getSkillManager().addExperience(Skill.MINING, (int) (o.getXpAmount() * 1.4), false);
									player.getPacketSender().sendMessage("You mine some ore.");
									player.performAnimation(new Animation(65535));
									cycle = 0;
									this.stop();
									if(o.getRespawn() > 0)
										oreRespawn(player, oreObject, o);
									else
										startMining(player, oreObject);
								}
							}
						});
						TaskManager.submit(player.getSkillManager().getSkillAttributes().getCurrentTask());
					} else {
						player.getPacketSender().sendMessage("You need a Mining level of at least "+p.getReq()+" to use this pickaxe.");
					}
				} else {
					player.getPacketSender().sendMessage("You need a Mining level of at least "+o.getLevelReq()+" to mine this rock.");
				}
			} else {
				player.getPacketSender().sendMessage("You don't have a pickaxe to mine this rock with.");
			}
		}
	}
	
	public static void oreRespawn(final Player player, final GameObject oldOre, Ores o) {
		if(oldOre == null || oldOre.getPickAmount() >= 1)
			return;
		oldOre.setPickAmount(1);
		for(Player players : player.getAttributes().getLocalPlayers()) {
			if(players == null)
				continue;
			if(players.getAttributes().getCurrentInteractingObject() != null && players.getAttributes().getCurrentInteractingObject().getPosition().equals(player.getAttributes().getCurrentInteractingObject().getPosition().copy())) {
				players.getPacketSender().sendClientRightClickRemoval();
				players.getSkillManager().stopSkilling();
			} 
		}
		player.getPacketSender().sendClientRightClickRemoval();
		player.getSkillManager().stopSkilling();
		CustomObjects.globalObjectRespawnTask(new GameObject(452, oldOre.getPosition().copy(), 10, 0), oldOre, o.getRespawn());
	}
}
