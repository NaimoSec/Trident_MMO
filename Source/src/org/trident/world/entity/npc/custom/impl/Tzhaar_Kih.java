package org.trident.world.entity.npc.custom.impl;

import org.trident.model.Skill;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Tzhaar_Kih extends CustomNPC {

	@Override
	public void executeAttack(NPC attacker, Player target) {
		attacker.performAnimation(attacker.getAttackAnimation());
		DamageHandler.handleAttack(attacker, target, getBaseDamage(attacker, target), AttackType.MELEE, false, false);
		if(attacker.getConstitution() > 0 && target.getConstitution() > 0) {
			if(target.getSkillManager().getCurrentLevel(Skill.PRAYER) > 0) {
				target.getSkillManager().setCurrentLevel(Skill.PRAYER, target.getSkillManager().getCurrentLevel(Skill.PRAYER) - 10);
				if(target.getSkillManager().getCurrentLevel(Skill.PRAYER) <= 0) {
					target.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
					target.getPacketSender().sendMessage("You have run out of Prayer points.");
				}
			}
		}
	}

}
