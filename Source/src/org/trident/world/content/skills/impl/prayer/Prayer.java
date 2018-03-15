package org.trident.world.content.skills.impl.prayer;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Item;
import org.trident.model.Skill;
import org.trident.model.movement.MovementStatus;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.entity.player.Player;

/**
 * The prayer skill is based upon burying the corpses of enemies. Obtaining a higher level means
 * more prayer abilities being unlocked, which help out in combat.
 * 
 * @author Gabbe
 */

public class Prayer {
	
	public static boolean isBone(int bone) {
		return BonesData.forId(bone) != null;
	}
	
	public static void buryBone(final Player player, final int itemId) {
		if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 2000)
			return;
		final BonesData currentBone = BonesData.forId(itemId);
		if(currentBone == null)
			return;
		player.getPacketSender().sendInterfaceRemoval();
		player.performAnimation(new Animation(827));
		player.getMovementQueue().stopMovement();
		player.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
		player.getPacketSender().sendMessage("You dig a hole in the ground..");
		final Item bone = new Item(itemId);
		player.getInventory().delete(bone);
		TaskManager.submit(new Task(3, player, false) {
			@Override
			public void execute() {
				player.getPacketSender().sendMessage("..and bury the "+bone.getDefinition().getName()+".");
				player.getSkillManager().addExperience(Skill.PRAYER, currentBone.getBuryingXP(), false);
				SoundEffects.sendSoundEffect(player, SoundEffects.SoundData.BURY_BONE, 10, 0);
				player.getMovementQueue().setMovementStatus(MovementStatus.NONE);
				if(player.getLocation() == Location.SOULWARS)
					SoulWars.boneBury(player);
				stop();				
			}
		});
		player.getAttributes().setClickDelay(System.currentTimeMillis());
	}
}
