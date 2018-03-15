package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.MathUtils;
import org.trident.util.Misc;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

public class DragonClawSpecialAttack extends SpecialAttack {

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
	public boolean isDoubleHit() {
		return true;
	}
	
	@Override
	public double getAccuracy() {
		return 6;
	}
	
	@Override
	public double getMultiplier() {
		return 1.05;
	}
	
	@Override
	public boolean modifyDamage() {
		return true;
	}
	
	@Override
	public void specialAction(final Player player, final GameCharacter victim, Damage damage) {
		final int first = (int) (Misc.getRandom((int)DamageHandler.getBaseMeleeDamage(player)) * getMultiplier());
		final int second = first <= 0 ? Misc.getRandom((int)DamageHandler.getBaseMeleeDamage(player)) : (int) (first/2);
		final int third = first <= 0 && second > 0 ? (int) (second/2) :  first <= 0 && second <= 0 ? Misc.getRandom((int)DamageHandler.getBaseMeleeDamage(player)) : MathUtils.random(second);
		final int fourth = first <= 0 && second <= 0 && third <= 0 ? (int) (Misc.getRandom((int)DamageHandler.getBaseMeleeDamage(player)) * 1.47) + MathUtils.random(7) : first <= 0 && second <= 0 ?
				Misc.getRandom((int)DamageHandler.getBaseMeleeDamage(player)) : third;
		damage = new Damage(new Hit(first, CombatIcon.MELEE, Hitmask.RED), new Hit(second, CombatIcon.MELEE, Hitmask.RED));
		DamageHandler.handleAttack(player, victim, damage,  player.getCombatAttributes().getAttackType(), true, false);
		final Damage secondDamage = new Damage(new Hit(third, CombatIcon.MELEE, Hitmask.RED), new Hit(fourth, CombatIcon.MELEE, Hitmask.RED));
		TaskManager.submit(new Task(1) {
			@Override
			public void execute() {
				DamageHandler.handleAttack(player, victim, secondDamage,  player.getCombatAttributes().getAttackType(), true, false);
				stop();
			}
		});
	}
	
	private static final Animation ANIMATION = new Animation(10961);
	
	private static final Graphic GRAPHIC = new Graphic(1950);
}
