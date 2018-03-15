package org.trident.world.entity.npc.custom.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
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

public class Commander_Zilyana extends CustomNPC {

	private static final int MAX_HIT = 310;
	private static final Animation attack_anim = new Animation(6967);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		AttackType type = Misc.getRandom(2) == 1 ? AttackType.MAGIC : AttackType.MELEE;
		switch(type) {
		case MELEE:
			attacker.performAnimation(attacker.getAttackAnimation());
			int meleeDamage = Misc.getRandom(MAX_HIT - DamageHandler.getMeleeDefence(target));
			if(meleeDamage < 0)
				meleeDamage = 0;
			DamageHandler.handleAttack(attacker, target, new Damage(new Hit(meleeDamage, CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
			break;
		default:
			attacker.performAnimation(attack_anim);
			TaskManager.submit(new Task(2, false) {
				@Override
				public void execute() {
					DamageHandler.handleAttack(attacker, target, getMagicDamage(target), AttackType.MAGIC, false, false);
					stop();
				}

			});
			break;
		}
	}
	
	public static Damage getMagicDamage(Player p) {
		return new Damage(new Hit(MAX_HIT - Misc.getRandom(DamageHandler.getMagicDefence(p)), CombatIcon.MAGIC, Hitmask.RED), new Hit(Misc.getRandom(MAX_HIT - DamageHandler.getMagicDefence(p)), CombatIcon.MAGIC, Hitmask.RED));
	}

}
