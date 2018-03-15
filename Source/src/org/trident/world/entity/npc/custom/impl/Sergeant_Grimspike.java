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
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Sergeant_Grimspike extends CustomNPC {

	private static final int MAX_DAMAGE = 300;

	private static final Graphic gfx = new Graphic(37);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		int dmg = target.getPrayerActive()[PrayerHandler.PROTECT_FROM_MISSILES] || target.getPrayerActive()[CurseHandler.DEFLECT_MISSILES] ? Misc.getRandom(10) : Misc.getRandom(MAX_DAMAGE);
		dmg -= Misc.getRandom( DamageHandler.getRangedDefence(target));
		final Damage damage = new Damage(new Hit(dmg >= 0 ? dmg : 0, CombatIcon.RANGED, Hitmask.RED));
		attacker.performAnimation(attacker.getAttackAnimation());
		TaskManager.submit(new Task(1, target, false) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 1:
					CustomNPC.fireGlobalProjectile(target, attacker, gfx);
					break;
				case 3:
					DamageHandler.handleAttack(attacker, target, damage, AttackType.RANGED, false, false);
					stop();
					break;
				}
				tick++;
			}

		});
	}

}
