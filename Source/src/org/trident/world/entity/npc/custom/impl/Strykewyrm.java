package org.trident.world.entity.npc.custom.impl;

import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatExtras;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Strykewyrm extends CustomNPC {
	
	@Override
	public void executeAttack(NPC attacker, Player target) {
		attacker.performAnimation(attacker.getAttackAnimation());
		attacker.getCombatAttributes().setAttackType(AttackType.MELEE);
		DamageHandler.handleAttack(attacker, target, CustomNPC.getBaseDamage(attacker, target), AttackType.MELEE, false, false);
		if(Misc.getRandom(100) >= 90)
			CombatExtras.poison(target, 50 + Misc.getRandom(30), false);
	}

}
