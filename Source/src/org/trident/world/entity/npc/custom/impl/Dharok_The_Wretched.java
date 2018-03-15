package org.trident.world.entity.npc.custom.impl;

import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Dharok_The_Wretched extends CustomNPC {

	@Override
	public void executeAttack(NPC attacker, Player target) {
		attacker.performAnimation(attacker.getAttackAnimation());
		int damage = Misc.getRandom(100); damage += (int) ((int)(attacker.getDefaultAttributes().getConstitution() - attacker.getConstitution()) * 0.6); damage -= Misc.getRandom(DamageHandler.getMeleeDefence(target));
		if(damage < 0)
			damage = 0;
		DamageHandler.handleAttack(attacker, target, new Damage(new Hit(damage, CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
	}

}
