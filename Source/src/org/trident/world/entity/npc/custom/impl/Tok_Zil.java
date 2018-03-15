package org.trident.world.entity.npc.custom.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.Misc;
import org.trident.world.content.Locations;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Tok_Zil extends CustomNPC {

	private static final Animation anim1 = new Animation(9245);
	private static final Animation anim2 = new Animation(9243);
	private static final Graphic gfx = new Graphic(443);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		if(Misc.getRandom(5) <= 3 && Locations.goodDistance(attacker.getPosition().getX(), attacker.getPosition().getY(), attacker.getCombatAttributes().getCurrentEnemy().getPosition().getX(), attacker.getCombatAttributes().getCurrentEnemy().getPosition().getY(), 1)) {
			attacker.performAnimation(anim1);
			DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(180), CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
			attacker.getCombatAttributes().setAttackDelay(5);
		} else {
			attacker.performAnimation(anim2);
			attacker.getCombatAttributes().setAttackDelay(5);
			TaskManager.submit(new Task(1) {
				int tick = 0;
				@Override
				public void execute() {
					switch(tick) {
					case 1:
						fireGlobalProjectile(target, attacker, gfx);
						break;
					case 2:
						DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(150), CombatIcon.RANGED, Hitmask.RED)), AttackType.RANGED, false, false);
						attacker.getCombatAttributes().setAttackDelay(4);
						stop();
						break;
					}
					tick++;
				}
			});
		}
	}
}
