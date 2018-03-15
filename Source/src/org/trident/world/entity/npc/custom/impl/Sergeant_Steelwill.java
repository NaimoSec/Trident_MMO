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

public class Sergeant_Steelwill extends CustomNPC {

	private static final int MAX_MAGIC_DAMAGE = 210;

	private static final Graphic gfx1 = new Graphic(1203);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		int dmg = Misc.getRandom(MAX_MAGIC_DAMAGE) - Misc.getRandom(DamageHandler.getMagicDefence(target));
		final Damage damage = new Damage(new Hit(Misc.getRandom(dmg), CombatIcon.MAGIC, Hitmask.RED));
		attacker.performAnimation(attacker.getAttackAnimation());
		attacker.performGraphic(new Graphic(1202));
		TaskManager.submit(new Task(1, target, false) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 1:
					CustomNPC.fireGlobalProjectile(target, attacker, gfx1);
					break;
				case 3:
					DamageHandler.handleAttack(attacker, target, damage, AttackType.MAGIC, false, false);
					stop();
					break;
				}
				tick++;
			}

		});
	}

}
