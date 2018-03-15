package org.trident.world.content.skills.impl.firemaking;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.GameObject;
import org.trident.model.Skill;
import org.trident.model.movement.MovementStatus;
import org.trident.util.Misc;
import org.trident.world.content.Achievements;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.Following;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * The Firemaking skill
 * @author Gabbe
 */

public class Firemaking {

	public static void lightFire(final Player player, int log, final boolean addingToFire, final int amount) {
		if (System.currentTimeMillis() - player.getAttributes().getClickDelay() < 2000)
			return;
		boolean objectExists = CustomObjects.objectExists(player.getPosition().copy());
		if(!player.getLocation().isFiremakingAllowed()|| objectExists && !addingToFire || player.getPosition().getZ() > 0 || !player.getMovementQueue().canWalk(1, 0) && !player.getMovementQueue().canWalk(-1, 0) && !player.getMovementQueue().canWalk(0, 1) && !player.getMovementQueue().canWalk(0, -1)) {
			player.getPacketSender().sendMessage("You can not light a fire here.");
			return;
		}
		final Logdata.logData logData = Logdata.getLogData(player, log);
		if(logData == null)
			return;
		player.getMovementQueue().stopMovement();
		if(objectExists && addingToFire)
			Following.stepAway(player);
		player.getPacketSender().sendInterfaceRemoval();
		player.setEntityInteraction(null);
		player.getSkillManager().stopSkilling();
		int cycle = 2 + Misc.getRandom(3);
		if (player.getSkillManager().getMaxLevel(Skill.FIREMAKING) < logData.getLevel()) {
			player.getPacketSender().sendMessage("You need a Firemaking level of atleast "+logData.getLevel()+" to light this.");
			player.getMovementQueue().setMovementStatus(MovementStatus.NONE);
			return;
		}
		if(!addingToFire) {
			player.getPacketSender().sendMessage("You attempt to light a fire..");
			player.performAnimation(new Animation(733));
			player.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
		}
		player.getSkillManager().getSkillAttributes().setCurrentTask(new Task(addingToFire ? 2 : cycle, player, addingToFire ? true : false) {
			int added = 0;
			@Override
			public void execute() {
				if(addingToFire && player.getAttributes().getCurrentInteractingObject() == null) { //fire has died
					player.getSkillManager().stopSkilling();
					player.getPacketSender().sendMessage("The fire has died out.");
					return;
				}
				player.getInventory().delete(logData.getLogId(), 1);
				SoundEffects.sendSoundEffect(player, SoundEffects.SoundData.LIGHT_FIRE, 10, 0);
				if(addingToFire) {
					player.performAnimation(new Animation(827));
					player.getPacketSender().sendMessage("You add some logs to the fire..");
				} else {
					if(!player.getMovementQueue().isMoving())
						forceWalk(player);
					CustomObjects.globalFiremakingTask(new GameObject(2732, player.getPosition().copy()), player, logData.getBurnTime());
					player.getPacketSender().sendMessage("The fire catches and the logs begin to burn.");
					stop();
				}
				player.getSkillManager().addExperience(Skill.FIREMAKING, logData.getXp() * Skill.FIREMAKING.getExperienceMultiplier(), false);
				if(logData.getLogId() == 1511)
					Achievements.handleAchievement(player, Achievements.Tasks.TASK2);
				added++;
				if(added >= amount || !player.getInventory().contains(logData.getLogId())) {
					stop();
					return;
				}
			}
		});
		TaskManager.submit(player.getSkillManager().getSkillAttributes().getCurrentTask());
		player.getAttributes().setClickDelay(System.currentTimeMillis() + 500);
	}
	
	/**
	 * Handles the walking after lighting a log
	 * @param player	The player to handle the walking for
	 */
	public static void forceWalk(GameCharacter gc) {
		gc.getMovementQueue().setMovementStatus(MovementStatus.NONE);
		gc.performAnimation(new Animation(65535));
		Following.stepAway(gc);
	}

}