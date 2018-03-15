package org.trident.world.entity.npc.custom.impl;

import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Torag_The_Corrupted extends CustomNPC {

	@Override
	public void executeAttack(NPC attacker, Player target) {
		attacker.performAnimation(attacker.getAttackAnimation());
		DamageHandler.handleAttack(attacker, target, getBaseDamage(attacker, target), AttackType.MELEE, false, false);
		if(Misc.getRandom(20) <= 5) {
			target.getPacketSender().sendMessage("Torag's hammers have drained your energy..");
			int energy = target.getAttributes().getRunEnergy() - 20;
			if(energy < 0)
				energy = 0;
			target.getAttributes().setRunEnergy(energy);
			target.getPacketSender().sendRunEnergy(energy);
		}
	}

}
