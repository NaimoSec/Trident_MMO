package org.trident.world.entity.npc.custom.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Wingman_Skree extends CustomNPC {

	private static final Graphic gfx1 = new Graphic(1505);
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		attacker.performAnimation(attacker.getAttackAnimation());
		final int dmgToSet = 1 + Misc.getRandom(attacker.getAttributes().getMaxHit()) - Misc.getRandom(DamageHandler.getMagicDefence(target));
		TaskManager.submit(new Task(1, false) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 0:
					CustomNPC.fireGlobalProjectile(target, attacker, gfx1);
					break;
				case 1:
					Damage damage = new Damage(new Hit(dmgToSet, CombatIcon.MAGIC, Hitmask.RED));
					DamageHandler.handleAttack(attacker, target, damage, AttackType.MAGIC, false, false);
					stop();
					break;
				}
				tick++;
			}
		});
	}

}
