package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * Represents a powerful and accurate armadyl godsword special attack.
 * 
 * @author relex lawl
 */

public class BandosGodswordSpecialAttack extends SpecialAttack {

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
	public double getAccuracy() {
		return 1.5;
	}
	
	@Override
	public double getMultiplier() {
		return 1.12;
	}
	
	@Override
	public boolean selfGraphic() {
		return true;
	}

	@Override
	public boolean modifyDamage() {
		return false;
	}
	
	@Override
	public void specialAction(Player player, GameCharacter victim, Damage damage) {
		if(victim != null && victim.isPlayer()) {
			int skillDrain = Misc.getRandom(2);
			int damageDrain = (int) (damage.getHits()[0].getDamage() - DamageHandler.getMeleeDefence((Player)victim) * 0.1) / 10;
			if(damageDrain < 0)
				return;
			((Player)victim).getSkillManager().setCurrentLevel(Skill.forId(skillDrain), player.getSkillManager().getCurrentLevel(Skill.forId(skillDrain)) - damageDrain);
			if(((Player)victim).getSkillManager().getCurrentLevel(Skill.forId(skillDrain)) < 1)
					((Player)victim).getSkillManager().setCurrentLevel(Skill.forId(skillDrain), 1);
			player.getPacketSender().sendMessage("You've drained "+((Player)victim).getUsername()+"'s "+Misc.formatText(Skill.forId(skillDrain).toString().toLowerCase())+" level by "+damageDrain+".");
			((Player)victim).getPacketSender().sendMessage("Your "+Misc.formatText(Skill.forId(skillDrain).toString().toLowerCase())+" level has been drained.");
		}
	}
	
	
	private static final Animation ANIMATION = new Animation(11991);
	
	private static final Graphic GRAPHIC = new Graphic(2114);

}
