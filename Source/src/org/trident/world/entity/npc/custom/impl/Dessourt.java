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
import org.trident.model.Projectile;
import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Dessourt extends CustomNPC {

	private static final Animation anim1 = new Animation(3505);
	private static final Graphic graphic1 = new Graphic(550);
	private static final Graphic graphic2 = new Graphic(554);
	private static final Graphic graphic3 = new Graphic(552, GraphicHeight.MIDDLE);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		boolean castMage = Misc.getRandom(3) <= 2;
		if(!castMage) {
			attacker.performAnimation(attacker.getAttackAnimation());
			DamageHandler.handleAttack(attacker, target, getBaseDamage(attacker, target), AttackType.MELEE, false, false);
			return;
		}
		attacker.performGraphic(graphic1);
		attacker.performAnimation(new Animation(attacker.getAttackAnimation().getId(), 10));
		target.getPacketSender().sendProjectile(new Projectile(attacker.getPosition().copy(), target.getPosition().copy(), new Graphic(558, GraphicHeight.MIDDLE), 70, 50, 10), target);
		TaskManager.submit(new Task(3, target, false) {
			@Override
			public void execute() {
				int magicDmg = Misc.getRandom(250) - DamageHandler.getMagicDefence(target);
				DamageHandler.handleAttack(attacker, target, new Damage(new Hit(magicDmg, CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
				if(magicDmg > 0) {
					attacker.performAnimation(anim1);
					attacker.performGraphic(graphic2);
					attacker.setConstitution(attacker.getConstitution()+magicDmg);
					attacker.forceChat("Hsss..");
					target.performGraphic(graphic3);
					if(attacker.getConstitution() > attacker.getDefaultAttributes().getConstitution())
						attacker.setConstitution(attacker.getDefaultAttributes().getConstitution());
				}
				stop();
			}
		});
	}

}
