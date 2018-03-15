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

public class Karamel extends CustomNPC {
	
	private static final Animation anim1 = new Animation(1979);
	private static final Graphic graphic1 = new Graphic(369);
	private static final Graphic graphic2 = new Graphic(1677, GraphicHeight.MIDDLE);
	private static final Graphic splash = new Graphic(85, GraphicHeight.MIDDLE);

	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		int random = Misc.getRandom(3);
		boolean melee = (random == 0 || random == 1);
		if(melee) {
			attacker.performAnimation(attacker.getAttackAnimation());
			DamageHandler.handleAttack(attacker, target, getBaseDamage(attacker, target), AttackType.MELEE, false, false);
			return;
		}
		attacker.performAnimation(anim1);
		TaskManager.submit(new Task(2, target, false) {
			@Override
			public void execute() {
				int magicDmg = Misc.getRandom(250) - DamageHandler.getMagicDefence(target);
				boolean froze = Misc.getRandom(10) < 3;
				if(!froze)
					target.performGraphic(graphic1);
				else
					target.performGraphic(graphic2);
				if(magicDmg <= 0) {
					magicDmg = 0;
					target.performGraphic(splash);
				} else if(target.getCombatAttributes().getFreezeDelay() < 1) {
					target.getCombatAttributes().setFreezeDelay(15);
					target.getPacketSender().sendMessage("You have been frozen!");
					target.getMovementQueue().stopMovement();
				}
				DamageHandler.handleAttack(attacker, target, new Damage(new Hit(magicDmg, CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
				attacker.getCombatAttributes().setAttackDelay(4);
				stop();
			}
		});
		attacker.getCombatAttributes().setAttackDelay(4);
	}

}
