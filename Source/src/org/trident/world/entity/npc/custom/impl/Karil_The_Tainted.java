package org.trident.world.entity.npc.custom.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Karil_The_Tainted extends CustomNPC {

	private static final Graphic graphic1 = new Graphic(27, GraphicHeight.MIDDLE);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		attacker.performAnimation(attacker.getAttackAnimation());
		TaskManager.submit(new Task(1, target, false) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 1:
					fireGlobalProjectile(target, attacker, graphic1);
					break;
				case 2:
					DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(155), CombatIcon.RANGED, Hitmask.RED)), AttackType.RANGED, false, false);
					attacker.getCombatAttributes().setAttackDelay(0);
					stop();
					break;
				}
				tick++;
			}
		});

	}

}
