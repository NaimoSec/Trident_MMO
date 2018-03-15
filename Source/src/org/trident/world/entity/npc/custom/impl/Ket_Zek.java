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
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Ket_Zek extends CustomNPC {

	private static final Animation attack_anim = new Animation(9266);
	private static final Animation attack_anim2 = new Animation(9265);
	private static final Graphic graphic1 = new Graphic(1622);
	private static final Graphic graphic2 = new Graphic(1624);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		if(Misc.getRandom(5) <= 3 && Locations.goodDistance(attacker.getPosition().getX(), attacker.getPosition().getY(), target.getPosition().getX(), target.getPosition().getY(), 1)) {
			attacker.performAnimation(attack_anim2);
			DamageHandler.handleAttack(attacker, target, getBaseDamage(attacker, target), AttackType.MELEE, false, false);
			attacker.getCombatAttributes().setAttackDelay(4);
		} else {
			attacker.performAnimation(attack_anim);
			attacker.performGraphic(graphic1);
			attacker.getCombatAttributes().setAttackDelay(5);
			TaskManager.submit(new Task(1) {
				int tick = 0;
				@Override
				public void execute() {
					switch(tick) {
					case 1:
						fireGlobalProjectile(target, attacker, new Graphic(1623));
						break;
					case 2:
						int magicDmg = Misc.getRandom(120);
						final boolean prayer = target.getPrayerActive()[PrayerHandler.PROTECT_FROM_MAGIC] || target.getCurseActive()[CurseHandler.DEFLECT_MAGIC];
						if(!prayer)
							magicDmg = 300 + Misc.getRandom(150);
						magicDmg -= DamageHandler.getMagicDefence(target);
						DamageHandler.handleAttack(attacker, target, new Damage(new Hit(magicDmg, CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
						if(!prayer) 
							attacker.performGraphic(graphic2);
						attacker.getCombatAttributes().setAttackDelay(4);
						stop();
						break;
					}
					tick++;
				}
			});
			attacker.getCombatAttributes().setAttackDelay(5);
		}
	}
}
