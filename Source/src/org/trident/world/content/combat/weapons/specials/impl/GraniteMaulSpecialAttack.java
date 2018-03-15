package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

public class GraniteMaulSpecialAttack extends SpecialAttack {

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
		return 5.0;
	}
	
	@Override
	public boolean isDoubleHit() {
		return false;
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
	public void specialAction(final Player player, final GameCharacter victim, Damage damage) {
		if(victim == null)
			return;
		player.getPlayerCombatAttributes().setUsingSpecialAttack(true);
		if(victim.getConstitution() < 0 || !CombatHandler.checkRequirements(player, victim) || !CombatHandler.closeDistance(player, victim))
			return;
		CombatHandler.setAttack(player, victim);
		final int dmg = (int) (DamageHandler.getBaseMeleeDamage(player));
		DamageHandler.handleAttack(player, victim, new Damage(new Hit(Misc.getRandom(dmg), CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, true, false);
		player.getCombatAttributes().setAttackDelay(3);
		player.getPlayerCombatAttributes().setUsingSpecialAttack(false);
	}


	private static final Animation ANIMATION = new Animation(1667);
	
	private static final Graphic GRAPHIC = new Graphic(340, GraphicHeight.HIGH);
}
