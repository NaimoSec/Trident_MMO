package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

public class KorasiSwordSpecialAttack extends SpecialAttack {

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
		return 5.5;
	}
	
	@Override
	public double getAccuracy() {
		return 8;
	}
	
	@Override
	public double getMultiplier() {
		return 1.93;
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
	public void specialAction(final Player player, final GameCharacter victim, Damage damage) {
		player.getCombatAttributes().setAttackType(AttackType.MAGIC);
		final Damage victimDamage = new Damage(new Hit(Misc.getRandom((int) (DamageHandler.getBaseMeleeDamage(player))), CombatIcon.MAGIC, Hitmask.RED));
		TaskManager.submit(new Task(1) {
			@Override
			public void execute() {
				victim.performGraphic(VICTIM_GRAPHIC);
				DamageHandler.handleAttack(player, victim, victimDamage, AttackType.MAGIC, true, false);
				player.getCombatAttributes().setAttackType(AttackType.MELEE);
				stop();
			}
		});
	}
	
	private static final Animation ANIMATION = new Animation(14788);
	
	private static final Graphic GRAPHIC = new Graphic(1729);	
	
	private static final Graphic VICTIM_GRAPHIC = new Graphic(1730);
}
