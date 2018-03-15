package org.trident.world.content.skills.impl.woodcutting;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.GameObject;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.Achievements;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.Achievements.Tasks;
import org.trident.world.content.skills.impl.woodcutting.WoodcuttingData.Hatchet;
import org.trident.world.entity.player.Player;

public class Woodcutting {

	public static void cutWood(final Player player, final GameObject object) {
		player.getSkillManager().stopSkilling();
		if(player.getInventory().getFreeSlots() == 0) {
			player.getPacketSender().sendMessage("You don't have enough free inventory space.");
			return;
		}
		final int objId = object.getId();
		final Hatchet h = Hatchet.forId(WoodcuttingData.getHatchet(player));
		if (h != null) {
			if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) >= h.getRequiredLevel()) {
				final WoodcuttingData.Trees t = WoodcuttingData.Trees.forId(objId);
				if (t != null) {
					player.setEntityInteraction(object);
					if (player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) >= t.getReq()) {
						player.performAnimation(new Animation(h.getAnim()));
						final int delay = t.getTicks() - WoodcuttingData.getChopTimer(player, h);
						player.getSkillManager().getSkillAttributes().setCurrentTask(new Task(delay >= 2 ? delay : 1+Misc.getRandom(1), player, false) {
							int cycle = 0;
							@Override
							public void execute() {
								if(player.getInventory().getFreeSlots() == 0) {
									player.performAnimation(new Animation(65535));
									player.getPacketSender().sendMessage("You don't have enough free inventory space.");
									this.stop();
									return;
								}
								int reqCycle = Misc.getRandom(t.getTicks()) +1;
								if (cycle != reqCycle) {
									cycle++;
									player.performAnimation(new Animation(h.getAnim()));
								}
								if (cycle >= reqCycle) {
									player.getInventory().add(t.getReward(), 1);
									player.getSkillManager().addExperience(Skill.WOODCUTTING, t.getXp() * Skill.WOODCUTTING.getExperienceMultiplier(), false);
									player.performAnimation(new Animation(65535));
									cycle = 0;
									Achievements.handleAchievement(player, Tasks.TASK1);
									BirdNests.dropNest(player);
									this.stop();
									if (!t.isMulti() || Misc.getRandom(10) == 2) {
										treeRespawn(player, object);
										player.getPacketSender().sendMessage("You've chopped the tree down.");
									} else {
										cutWood(player, object);
										player.getPacketSender().sendMessage("You get some logs..");
									}
								}
							}
						});
						TaskManager.submit(player.getSkillManager().getSkillAttributes().getCurrentTask());
					} else {
						player.getPacketSender().sendMessage("You need a Woodcutting level of at least "+t.getReq()+" to cut this tree.");
					}
				}
			} else {
				player.getPacketSender().sendMessage("You do not have a hatchet which you have the required Woodcutting level to use.");
			}
		} else {
			player.getPacketSender().sendMessage("You do not have a hatchet that you can use.");
		}
	}
	
	public static void treeRespawn(final Player player, final GameObject oldTree) {
		if(oldTree == null || oldTree.getPickAmount() >= 1)
			return;
		oldTree.setPickAmount(1);
		for(Player players : player.getAttributes().getLocalPlayers()) {
			if(players == null)
				continue;
			if(players.getAttributes().getCurrentInteractingObject() != null && players.getAttributes().getCurrentInteractingObject().getPosition().equals(player.getAttributes().getCurrentInteractingObject().getPosition().copy())) {
				players.getSkillManager().stopSkilling();
				players.getPacketSender().sendClientRightClickRemoval();
			}
		}
		player.getPacketSender().sendClientRightClickRemoval();
		player.getSkillManager().stopSkilling();
		CustomObjects.globalObjectRespawnTask(new GameObject(1343, oldTree.getPosition().copy(), 10, 0), oldTree, 20 + Misc.getRandom(10));
	}
	
}
