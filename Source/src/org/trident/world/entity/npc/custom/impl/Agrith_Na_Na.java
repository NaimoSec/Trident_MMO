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
import org.trident.world.content.Locations;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Agrith_Na_Na extends CustomNPC {

	private static final Animation ATTACK_ANIM = new Animation(3502, 8);
	private static final Graphic GRAPHIC = new Graphic(1901);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		boolean castMagic = Misc.getRandom(20) < 10 || !Locations.goodDistance(attacker.getPosition().copy(), target.getPosition().copy(), 1);
		if(!castMagic) {
			attacker.performAnimation(attacker.getAttackAnimation());
			DamageHandler.handleAttack(attacker, target, getBaseDamage(attacker, target), AttackType.MELEE, false, false);
			return;
		} 
		attacker.performGraphic(GRAPHIC);
		attacker.performAnimation(ATTACK_ANIM);
		target.getPacketSender().sendProjectile(new Projectile(attacker.getPosition().copy(), target.getPosition().copy(), new Graphic(701, GraphicHeight.MIDDLE), 80, 50, 10), target);
		TaskManager.submit(new Task(3, target, false) {
			@Override
			public void execute() {
				int magicDmg = 80 + Misc.getRandom(100) - Misc.getRandom(DamageHandler.getMagicDefence(target));
				Damage damage = new Damage(new Hit(magicDmg, CombatIcon.MAGIC, Hitmask.RED));
				DamageHandler.handleAttack(attacker, target, damage, AttackType.MAGIC, false, false);
				stop();
			}
		});
	}

}
