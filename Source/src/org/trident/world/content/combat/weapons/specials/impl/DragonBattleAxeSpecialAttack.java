package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Skill;
import org.trident.world.content.Consumables;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * Represents an abyssal whip special attack.
 * 
 * @author relex lawl
 */

public class DragonBattleAxeSpecialAttack extends SpecialAttack {

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
	public boolean selfGraphic() {
		return true;
	}

	@Override
	public boolean modifyDamage() {
		return true;
	}
	
	@Override
	public boolean isImmediate() {
		return true;
	}

	@Override
	public void specialAction(Player player, GameCharacter victim, Damage damage) {
		player.forceChat("Aaaaaaaargh!!");
		Consumables.drinkStatPotion(player, -1, -1, -1, Skill.STRENGTH.ordinal(), true);
		player.getSkillManager().setCurrentLevel(Skill.ATTACK, player.getSkillManager().getCurrentLevel(Skill.ATTACK) - 7);
	}

	private static final Animation ANIMATION = new Animation(1084);

	private static final Graphic GRAPHIC = new Graphic(246);
}
