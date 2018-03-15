package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

public class DarkBowSpecialAttack extends SpecialAttack {

	@Override
	public Animation getAnimation() {
		return ANIMATION;
	}

	@Override
	public double getSpecialAmount() {
		return 5.5;
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public boolean modifyDamage() {
		return true;
	}

	@Override
	public void specialAction(final Player player, final GameCharacter victim, Damage damage) {
		if(victim.getConstitution() < 0)
			return;
		CombatHandler.setAttack(player, victim);
		int dmg = (int) (DamageHandler.getRangedMaxHit(player) * 1.1);
		Damage damageToSet = new Damage(new Hit(Misc.getRandom(dmg), CombatIcon.RANGED, Hitmask.RED), new Hit(Misc.getRandom(dmg), CombatIcon.RANGED, Hitmask.RED));
		DamageHandler.handleAttack(player, victim, damageToSet, AttackType.RANGED, true, false);
	}

	private static final Animation ANIMATION = new Animation(426);

}
