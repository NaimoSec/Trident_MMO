package org.trident.world.entity.npc.custom.impl;

import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Guthan_The_Infested extends CustomNPC {

	private static final Graphic graphic1 = new Graphic(398);
	
	@Override
	public void executeAttack(NPC attacker, Player target) {
		attacker.performAnimation(attacker.getAttackAnimation());
		Damage damage = getBaseDamage(attacker, target);
		if(damage.getHits()[0].getDamage() > 0 && Misc.getRandom(9) <= 5 && (attacker.getConstitution() + (int) damage.getHits()[0].getDamage()) <= attacker.getDefaultAttributes().getConstitution()) {
			target.performGraphic(graphic1);
			attacker.setConstitution(attacker.getConstitution() + (int) damage.getHits()[0].getDamage());
		}
		DamageHandler.handleAttack(attacker, target, damage, AttackType.MELEE, false, false);
	}

}
