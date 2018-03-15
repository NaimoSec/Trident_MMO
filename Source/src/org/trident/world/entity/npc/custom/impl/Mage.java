package org.trident.world.entity.npc.custom.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
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

public class Mage extends CustomNPC {

	private static final Animation anim1 = new Animation(14221);
	private static final Graphic graphic1 = new Graphic(2729);
	private static final Graphic graphic2 = new Graphic(2728);
	private static final Graphic graphic3 = new Graphic(2737, GraphicHeight.MIDDLE);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		attacker.getCombatAttributes().setAttackType(AttackType.MAGIC);
		if(attacker.getId() != 6278) {
			attacker.performAnimation(anim1);
			attacker.performGraphic(graphic2);
		} else
			attacker.performAnimation(attacker.getAttackAnimation());
		TaskManager.submit(new Task(1, target, false) {
			int delay = 0, damage = (attacker.getId() == 912 ? 130 : 200) - Misc.getRandom(DamageHandler.getMagicDefence(target));
			@Override
			public void execute() {
				switch(delay) {
				case 1:
					CustomNPC.fireGlobalProjectile(target, attacker, graphic1);
					break;
				case 2:
					target.performGraphic(graphic3);
					DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(damage), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
					attacker.getCombatAttributes().setAttackDelay(3);
					stop();
					break;
				}
				delay++;
			}
		});
		attacker.getCombatAttributes().setAttackDelay(4);
	}

}
