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
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

public class MorrigansThrowingAxeSpecialAttack extends SpecialAttack {

	@Override
	public Animation getAnimation() {
		return ANIMATION;
	}

	@Override
	public double getSpecialAmount() {
		return 5;
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
	public boolean modifyDamage() {
		return true;
	}

	@Override
	public void specialAction(final Player player, final GameCharacter victim, Damage damage) {
		if(victim.getConstitution() < 0)
			return;
		player.getCombatAttributes().setAttackDelay(4);
		TaskManager.submit(new Task(1, player, false) {
			@Override
			public void execute() {
				CombatHandler.setAttack(player, victim);
				int dmg = (int) (DamageHandler.getRangedMaxHit(player) * 1.3);
				if(victim.isPlayer())
					dmg -= Misc.getRandom(DamageHandler.getRangedDefence((Player)victim));
				dmg += Misc.getRandom(200);
				if(dmg < 0)
					dmg = 0;
				Damage damageToSet = new Damage(new Hit(Misc.getRandom(dmg), CombatIcon.RANGED, Hitmask.RED));
				DamageHandler.handleAttack(player, victim, damageToSet, AttackType.RANGED, true, false);
				player.getCombatAttributes().setAttackDelay(4);
				stop();
			}
		});
	}

	private static final Animation ANIMATION = new Animation(10504);
	private static final Graphic GRAPHIC = new Graphic(1838);

}
