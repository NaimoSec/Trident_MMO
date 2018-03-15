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
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class TormentedDemon extends CustomNPC {

	private static final Animation anim = new Animation(10922);
	private static final Animation anim2 = new Animation(10918);
	private static final Animation anim3 = new Animation(10917);
	private static final Graphic gfx1 = new Graphic(1886, 3, GraphicHeight.MIDDLE);
	private static final Graphic gfx2 = new Graphic(1885);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		attacker.getMovementQueue().stopMovement();
		attacker.setEntityInteraction(target);
		if(Locations.goodDistance(target.getPosition().copy(), attacker.getPosition().copy(), 1) && Misc.getRandom(6) <= 4) {
			attacker.performAnimation(anim);
			attacker.performGraphic(gfx1);
			attacker.getCombatAttributes().setAttackType(AttackType.MELEE);
			TaskManager.submit(new Task(1, false) {
				@Override
				public void execute() {
					int damage = 150 + Misc.getRandom(100) - Misc.getRandom(DamageHandler.getMeleeDefence((Player)target));
					if(damage < 0)
						damage = 0;
					Damage dmg = new Damage(new Hit(damage, CombatIcon.MELEE, Hitmask.RED));
					DamageHandler.handleAttack(attacker, target, dmg, AttackType.MELEE, false, false);
					stop();
				}
			});
		} else if(Locations.goodDistance(target.getPosition().copy(), attacker.getPosition().copy(), 6) && Misc.getRandom(10) <= 7) {
			attacker.getCombatAttributes().setAttackType(AttackType.RANGED);
			attacker.performAnimation(anim2);
			target.getPacketSender().sendGlobalProjectile(new Projectile(attacker.getPosition().copy(), target.getPosition().copy(), new Graphic(1884, GraphicHeight.HIGH), 50, 50, 30), target);
			TaskManager.submit(new Task(2, false) {
				@Override
				public void execute() {
					int damage = 100 + Misc.getRandom(120) - Misc.getRandom(DamageHandler.getRangedDefence((Player)target));
					if(damage < 0)
						damage = 0;
					Damage dmg = new Damage(new Hit(damage, CombatIcon.RANGED, Hitmask.RED));
					DamageHandler.handleAttack(attacker, target, dmg, AttackType.RANGED, false, false);
					stop();
				}
			});
		} else if(Locations.goodDistance(target.getPosition().copy(), attacker.getPosition().copy(), 10)) {
			attacker.getCombatAttributes().setAttackType(AttackType.MAGIC);
			attacker.performAnimation(anim3);
			target.performGraphic(gfx2);
			TaskManager.submit(new Task(1, false) {
				@Override
				public void execute() {
					int damage = 50 + Misc.getRandom(200) - Misc.getRandom(DamageHandler.getMagicDefence((Player)target));
					if(damage < 0)
						damage = 0;
					Damage dmg = new Damage(new Hit(damage, CombatIcon.MAGIC, Hitmask.RED));
					DamageHandler.handleAttack(attacker, target, dmg, AttackType.MAGIC, false, false);
					stop();
				}
			});
		} else
			CombatHandler.resetAttack(attacker);
		attacker.getCombatAttributes().setAttackDelay(6);
	}
}



