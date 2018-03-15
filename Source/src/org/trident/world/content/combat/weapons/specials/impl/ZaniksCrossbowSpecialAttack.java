package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.combat.combatdata.magic.MagicExtras;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

public class ZaniksCrossbowSpecialAttack extends SpecialAttack {

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
	public boolean modifyDamage() {
		return true;
	}

	@Override
	public void specialAction(final Player player, final GameCharacter victim, Damage damage) {
		if(victim.getConstitution() < 0)
			return;
		player.getPacketSender().sendMessage("@red@Your crossbow attempts to weaken your target..");
		MagicExtras.weakenSkill(player, victim, Skill.DEFENCE, 0.85);
		player.performGraphic(GRAPHIC2);
		int heal = 150 + Misc.getRandom(100);
		if(player.getConstitution() + heal < 1200) {
			player.heal(new Damage(new Hit(heal, CombatIcon.NONE, Hitmask.LIGHT_YELLOW)));
			player.getPacketSender().sendMessage("@red@Your crossbow heals you.");
		}
	}

	private static final Animation ANIMATION = new Animation(4230);
	private static final Graphic GRAPHIC = new Graphic(1935), GRAPHIC2 = new Graphic(1944);

}
