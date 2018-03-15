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

public class DragonHalberdSpecialAttack extends SpecialAttack {

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
		return 3.0;
	}
	
	@Override
	public void specialAction(final Player player, final GameCharacter victim, Damage damage) {
		if(victim.getConstitution() < 0)
			return;
		CombatHandler.setAttack(player, victim);
		final int dmg = (int) (DamageHandler.getBaseMeleeDamage(player) * 0.6);
		DamageHandler.handleAttack(player, victim, new Damage(new Hit(Misc.getRandom(dmg), CombatIcon.MELEE, Hitmask.RED), new Hit(Misc.getRandom(dmg), CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, true, false);
		/*if(!Locations.goodDistance(player.getPosition().copy(), victim.getPosition().copy(), 1)) {
			TaskManager.submit(new Task(1, false)  {
				@Override
				protected void execute() {
					DamageHandler.handleAttack(player, victim, new Damage(new Hit(Misc.getRandom(dmg), CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, true, false);
					stop();
				}
			});
		}*/
	}
	
	private static final Animation ANIMATION = new Animation(1203);
	
	private static final Graphic GRAPHIC = new Graphic(282, GraphicHeight.HIGH);
}
