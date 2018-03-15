package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Skill;
import org.trident.world.content.Consumables;
import org.trident.world.content.Locations.Location;
import org.trident.world.entity.player.Player;

public class OverloadPotionTask extends Task {

	public OverloadPotionTask(Player player) {
		super(1, player, true);
		this.player = player;
	}
	
	final Player player;

	@Override
	public void execute() {
		int timer = player.getAttributes().getOverloadPotionTimer();
		if(timer == 600 || timer == 598 || timer == 596 || timer == 594 || timer == 592) {
			player.performAnimation(new Animation(3170));
			player.setDamage(new Damage(new Hit(100, CombatIcon.NONE, Hitmask.RED)));
		}
		if (timer == 600 || timer == 570 || timer == 540 || timer == 510 || timer == 480 || timer == 450 || timer == 420 || timer == 390 || timer == 360 || timer == 330 || timer == 300 || timer == 270 || timer == 240 || timer == 210 || timer == 180 || timer == 150 || timer == 120 || timer == 90 || timer == 60 || timer == 30) {
			Consumables.overloadIncrease(player, Skill.ATTACK, 0.27);
			Consumables.overloadIncrease(player, Skill.STRENGTH, 0.27);
			Consumables.overloadIncrease(player, Skill.DEFENCE, 0.27);
			Consumables.overloadIncrease(player, Skill.RANGED, 0.235);
			player.getSkillManager().setCurrentLevel(Skill.MAGIC, player.getSkillManager().getMaxLevel(Skill.MAGIC) + 7);
		}
		player.getAttributes().setOverloadPotionTimer(timer - 1);
		if(player.getAttributes().getOverloadPotionTimer() == 20) 
			player.getPacketSender().sendMessage("Your Overload's effect is about to run out.");
		if(player.getAttributes().getOverloadPotionTimer() <= 0 || player.getLocation() != null && player.getLocation() == Location.WILDERNESS || player.getLocation() == Location.DUEL_ARENA || player.getLocation() == Location.DUNGEONEERING) {
			player.getPacketSender().sendMessage("Your Overload's effect has run out.");
			for(int i = 0; i < 7; i++) {
				if(i == 3 || i == 5)
					continue;
				player.getSkillManager().setCurrentLevel(Skill.forId(i), player.getSkillManager().getMaxLevel(i));
			}
			player.getAttributes().setOverloadPotionTimer(0);
			stop();
		}
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
	}
}
