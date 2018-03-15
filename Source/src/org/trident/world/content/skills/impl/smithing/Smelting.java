package org.trident.world.content.skills.impl.smithing;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.Achievements;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.entity.player.Player;

public class Smelting {

	public static void openInterface(Player player) {
		player.getSkillManager().stopSkilling();
		for (int j = 0; j < SmithingData.SMELT_BARS.length; j++)
			player.getPacketSender().sendInterfaceModel(SmithingData.SMELT_FRAME[j], SmithingData.SMELT_BARS[j], 150);
		player.getPacketSender().sendChatboxInterface(2400);
	}

	public static void smeltBar(final Player player, final int barId, final int amount) {
		player.getSkillManager().stopSkilling();
		player.getPacketSender().sendInterfaceRemoval();
		if(!SmithingData.canSmelt(player, barId))
			return;
		player.performAnimation(new Animation(896));
		player.getSkillManager().getSkillAttributes().setCurrentTask(new Task(3, player, true) {
			int amountMade = 0;
			@Override
			public void execute() {
				if(!SmithingData.canSmelt(player, barId)) {
					stop();
					return;
				}
				SoundEffects.sendSoundEffect(player, SoundEffects.SoundData.SMELT_ITEM, 8, 0);
				player.setPositionToFace(new Position(3227, 3256, 0));
				player.performAnimation(new Animation(896));
				handleBarCreation(barId, player);
				amountMade++;
				if(amountMade >= amount)
					stop();
			}
		});
		TaskManager.submit(player.getSkillManager().getSkillAttributes().getCurrentTask());
	}

	public static void handleBarCreation(int barId, Player player) {
		if(SmithingData.ores[0] > 0) {
			player.getInventory().delete(SmithingData.ores[0], 1);
			if(SmithingData.ores[1] > 0 && SmithingData.ores[1] != 453) {
				player.getInventory().delete(SmithingData.ores[1], 1);
			} else if(SmithingData.ores[1] == 453) {
				player.getInventory().delete(SmithingData.ores[1], SmithingData.getCoalAmount(barId));
			}
			if(barId != 2351) { //Iron bar - 50% successrate
				player.getInventory().add(barId, 1);
				player.getSkillManager().addExperience(Skill.SMITHING, getExperience(barId), false);
				if(barId == 2349)
					Achievements.handleAchievement(player, Achievements.Tasks.TASK4);
			} else if(Misc.getRandom(2) == 1) {
				player.getInventory().add(barId, 1);
				player.getSkillManager().addExperience(Skill.SMITHING, getExperience(barId), false);
			} else
				player.getPacketSender().sendMessage("The Iron ore burns too quickly and you're unable to make an Iron bar.");
		}
	}

	public static int getExperience(int barId) {
		switch(barId) {
		case 2349: // Bronze bar
			return 50;
		case 2351: // Iron bar
			return 180;
		case 2353: // Steel bar
			return 250;
		case 2355: // Silver bar
		case 2357: // Gold bar
			return 350;
		case 2359: // Mithril bar
			return 500;
		case 2361: // Adamant bar
			return 700;
		case 2363: // Runite bar
			return 1000;
		}
		return 0;
	}
}
