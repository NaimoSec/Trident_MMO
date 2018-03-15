package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Skill;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

public class StatiusWarhammerSpecialAttack extends SpecialAttack {

	@Override
	public Animation getAnimation() {
		return ANIMATION;
	}

	@Override
	public Graphic getGraphic() {
		return GRAPHIC;
	}
	
	@Override
	public boolean selfGraphic() {
		return true;
	}

	@Override
	public double getSpecialAmount() {
		return 4.5;
	}
	
	@Override
	public double getAccuracy() {
		return 1.2;
	}
	
	@Override
	public double getMultiplier() {
		return 1.23;
	}
	
	@Override
	public void specialAction(Player player, GameCharacter victim, Damage damage) {
		if(victim.isPlayer()) {
			Player target = (Player)victim;
			int currentDef = target.getSkillManager().getCurrentLevel(Skill.DEFENCE);
			int defDecrease = (int) (currentDef * 0.11);
			if((currentDef - defDecrease) <= 0 || currentDef <= 0)
				return;
			target.getSkillManager().setCurrentLevel(Skill.DEFENCE, defDecrease);
			target.getPacketSender().sendMessage("Your opponent has reduced your Defence level.");
			player.getPacketSender().sendMessage("Your hammer forces some of your opponent's Defence to break.");
		}
	}
	
	private static final Animation ANIMATION = new Animation(10505);
	
	private static final Graphic GRAPHIC = new Graphic(1840);
}
