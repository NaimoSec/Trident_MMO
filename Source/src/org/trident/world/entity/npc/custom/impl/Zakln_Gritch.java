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

public class Zakln_Gritch extends CustomNPC {

	private static final Animation anim = new Animation(69);
	private static final Graphic gfx = new Graphic(386);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		boolean melee = Misc.getRandom(3) == 1 && Locations.goodDistance(target.getPosition(), attacker.getPosition(), 1);
		if(melee) {
			attacker.performAnimation(attacker.getAttackAnimation());
			DamageHandler.handleAttack(attacker, target, DamageHandler.getDamage(attacker, target), AttackType.MELEE, false, false);
			return;
		}
		attacker.performAnimation(anim);
		TaskManager.submit(new Task(1, target, false) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 1:
					fireGlobalProjectile(target, attacker, gfx);
					break;
				case 2:
					int dmg = 120 + Misc.getRandom(120);
					dmg -= Misc.getRandom(DamageHandler.getRangedDefence(target));
					DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(dmg), CombatIcon.RANGED, Hitmask.RED)), AttackType.RANGED, false, false);
					stop();
					break;
				}
				tick++;
			}

		});
	}

}
