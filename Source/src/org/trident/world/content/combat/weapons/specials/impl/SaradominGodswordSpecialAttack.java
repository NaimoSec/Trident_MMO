package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Skill;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * Represents a powerful and accurate armadyl godsword special attack.
 * 
 * @author relex lawl
 */

public class SaradominGodswordSpecialAttack extends SpecialAttack {

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
		return 5;
	}
	
	@Override
	public double getAccuracy() {
		return 1.5;
	}
	
	@Override
	public double getMultiplier() {
		return 1.10;
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
		int dmg = damage.getHits()[0].getDamage();
		int damageHeal = (int) (dmg * 0.5);
		int damagePrayerHeal = (int) (dmg * 0.25);
		if(player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) < player.getSkillManager().getMaxLevel(Skill.CONSTITUTION)) {
			int level = player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) + damageHeal > player.getSkillManager().getMaxLevel(Skill.CONSTITUTION) ? player.getSkillManager().getMaxLevel(Skill.CONSTITUTION) : player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) + damageHeal;
			player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, level);
		}
		if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager().getMaxLevel(Skill.PRAYER)) {
			int level = player.getSkillManager().getCurrentLevel(Skill.PRAYER) + damagePrayerHeal > player.getSkillManager().getMaxLevel(Skill.PRAYER) ? player.getSkillManager().getMaxLevel(Skill.PRAYER) : player.getSkillManager().getCurrentLevel(Skill.PRAYER) + damagePrayerHeal;
			player.getSkillManager().setCurrentLevel(Skill.PRAYER, level);
		}
	}
	
	private static final Animation ANIMATION = new Animation(7071);
	
	private static final Graphic GRAPHIC = new Graphic(1220);
}
