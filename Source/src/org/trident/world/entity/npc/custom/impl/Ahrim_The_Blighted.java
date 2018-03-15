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

public class Ahrim_The_Blighted extends CustomNPC {

	private static final Graphic graphic1 = new Graphic(145, GraphicHeight.MIDDLE);
	private static final Graphic graphic2 = new Graphic(147, GraphicHeight.HIGH);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		attacker.performAnimation(attacker.getAttackAnimation());
		attacker.performGraphic(graphic1);
		fireGlobalProjectile(target, attacker, graphic1);
		TaskManager.submit(new Task(3, target, false) {
			@Override
			public void execute() {
				DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(200), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
				target.performGraphic(graphic2);
				attacker.getCombatAttributes().setAttackDelay(4);
				stop();
			}
		});
	}

}
