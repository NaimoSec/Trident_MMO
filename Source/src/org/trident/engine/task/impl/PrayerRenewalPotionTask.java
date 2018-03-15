package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.model.Graphic;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.entity.player.Player;

public class PrayerRenewalPotionTask extends Task {

	public PrayerRenewalPotionTask(Player player) {
		super(1, player, true);
		this.player = player;
	}

	final Player player;

	@Override
	public void execute() {
		player.getAttributes().setPrayerRenewalPotionTimer(player.getAttributes().getPrayerRenewalPotionTimer() - 1);
		if(player.getAttributes().getPrayerRenewalPotionTimer() > 0) {
			if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager().getMaxLevel(Skill.PRAYER)) {
				player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getCurrentLevel(Skill.PRAYER) + 1);
				if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) > player.getSkillManager().getMaxLevel(Skill.PRAYER))
					player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getMaxLevel(Skill.PRAYER));
			}
			if(player.getAttributes().getPrayerRenewalPotionTimer() == 20)
				player.getPacketSender().sendMessage("Your Prayer Renewal's effect is about to run out.");
			if(Misc.getRandom(10) <= 2)
				player.performGraphic(new Graphic(1300));
		} else {
			player.getPacketSender().sendMessage("Your Prayer Renewal's effect has run out");
			stop();
		}
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
		player.getAttributes().setPrayerRenewalPotionTimer(0);
	}

}
