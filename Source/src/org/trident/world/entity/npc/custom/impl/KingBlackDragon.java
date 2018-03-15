package org.trident.world.entity.npc.custom.impl;

import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.Misc;
import org.trident.world.content.Locations;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class KingBlackDragon extends CustomNPC {

	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		attacker.setEntityInteraction(target);
		if(Locations.goodDistance(target.getPosition().copy(), attacker.getPosition().copy(), 2) && Misc.getRandom(5) <= 4) {
			attacker.performAnimation(attacker.getAttackAnimation());
			attacker.getCombatAttributes().setAttackType(AttackType.MELEE);
			int dmg = Misc.getRandom(230) - Misc.getRandom(DamageHandler.getMeleeDefence(target));
			DamageHandler.handleAttack(attacker, target, new Damage(new Hit(dmg, CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
		} else
			CustomNPC.handleDragonFireBreath(attacker, target);
	}
}



