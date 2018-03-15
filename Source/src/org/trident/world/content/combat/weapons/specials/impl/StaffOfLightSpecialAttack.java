package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

public class StaffOfLightSpecialAttack extends SpecialAttack {
	
	@Override
	public Animation getAnimation() {
		return ANIMATION;
	}

	@Override
	public Graphic getGraphic() {
		return GRAPHIC;
	}

	@Override
	public double getSpecialAmount() {
		return 10;
	}

	@Override
	public boolean selfGraphic() {
		return true;
	}
	
	@Override
	public boolean isImmediate() {
		return true;
	}
	
	@Override
	public void specialAction(final Player player, GameCharacter victim, Damage damage) {
		player.getCombatAttributes().setAttackDelay(4);
		if (player.getCombatAttributes().hasStaffOfLightEffect()) {
			player.getPacketSender().sendMessage("You are already being protected by the Staff of light spirits!");
			return;
		}
		player.getCombatAttributes().setStaffOfLightEffect(true);
		player.getPacketSender().sendMessage("You are shielded by the spirits of the Staff of light!");
		TaskManager.submit(new Task(200, player, false) {
			@Override
			public void execute() {
				if (!player.getCombatAttributes().hasStaffOfLightEffect()) {
					stop();
					return;
				}
				player.getCombatAttributes().setStaffOfLightEffect(false);
				player.getPacketSender().sendMessage("Your staff of light shield has faded away!");
				stop();
			}		
		});
	}
	
	private static final Animation ANIMATION = new Animation(10516);
	
	private static final Graphic GRAPHIC = new Graphic(1958);
}
