package org.trident.world.entity.npc.custom.impl;

import org.trident.util.Misc;
import org.trident.world.content.Locations;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Dragon extends CustomNPC {

	@Override
	public void executeAttack(NPC attacker, Player target) {
		if(Locations.goodDistance(attacker.getPosition().copy(), target.getPosition().copy(), 1) && Misc.getRandom(5) <= 3) {
			attacker.performAnimation(attacker.getAttackAnimation());
			attacker.getCombatAttributes().setAttackType(AttackType.MELEE);
			DamageHandler.handleAttack(attacker, target, CustomNPC.getBaseDamage(attacker, target), AttackType.MELEE, false, false);
		} else
			CustomNPC.handleDragonFireBreath(attacker, target);
	}
	
}
