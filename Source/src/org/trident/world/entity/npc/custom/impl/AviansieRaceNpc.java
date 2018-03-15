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

public class AviansieRaceNpc extends CustomNPC {

	private static final Animation melee_anim = new Animation(6954);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		boolean useMelee = Locations.goodDistance(target.getPosition().copy(), attacker.getPosition().copy(), 1) && Misc.getRandom(2) <= 1;
		if(useMelee) {
			attacker.performAnimation(melee_anim);
			DamageHandler.handleAttack(attacker, target, getBaseDamage(attacker, target), AttackType.MELEE, false, false);
			return;
		}
		attacker.performAnimation(attacker.getAttackAnimation());
		TaskManager.submit(new Task(1) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 0:
					fireGlobalProjectile(target, attacker, new Graphic(getGfx(attacker.getId())));
					break;
				case 1:
					int dmg = getMaxDamage(attacker.getId()) - Misc.getRandom(DamageHandler.getRangedDefence(target));
					boolean mage = attacker.getId() == 6231;
					DamageHandler.handleAttack(attacker, target, new Damage(new Hit(dmg, mage ? CombatIcon.MAGIC : CombatIcon.RANGED, Hitmask.RED)), mage ? AttackType.MAGIC : AttackType.RANGED, false, false);
					stop();
					break;
				}
				tick++;
			}
			
		});
	}
	
	public static int getGfx(int npc) {
		switch(npc) {
		case 6230:
			return 1837;
		case 6231:
			return 2729;
		}
		return 37;
	}

	public static int getMaxDamage(int npc) {
		switch(npc) {
		case 6225:
		case 6231:
			return 140;
		}
		return 120;
	}
}
