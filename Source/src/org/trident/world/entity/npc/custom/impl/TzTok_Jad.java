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

public class TzTok_Jad extends CustomNPC {

	private static final Animation anim1 = new Animation(9254);
	private static final Animation anim2 = new Animation(9277);
	private static final Animation anim3 = new Animation(9300);
	private static final Animation anim4 = new Animation(9276);
	private static final Graphic gfx1 = new Graphic(444);
	private static final Graphic gfx2 = new Graphic(1625);
	private static final Graphic gfx3 = new Graphic(1626);
	private static final Graphic gfx4 = new Graphic(451);
	private static final Graphic gfx5 = new Graphic(1627);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		if(attacker.getConstitution() <= 1200 && !attacker.getAttributes().hasHealed()) {
			attacker.performAnimation(anim1);
			attacker.performGraphic(gfx1);
			attacker.setConstitution(attacker.getConstitution() + Misc.getRandom(1600));
			attacker.getAttributes().setHealed(true);
		}
		if(Locations.goodDistance(attacker.getPosition().getX(), attacker.getPosition().getY(), attacker.getCombatAttributes().getCurrentEnemy().getPosition().getX(), attacker.getCombatAttributes().getCurrentEnemy().getPosition().getY(), 10))
			attacker.getMovementQueue().stopMovement();
		if(Misc.getRandom(4) == 2 && Locations.goodDistance(attacker.getPosition().getX(), attacker.getPosition().getY(), attacker.getCombatAttributes().getCurrentEnemy().getPosition().getX(), attacker.getCombatAttributes().getCurrentEnemy().getPosition().getY(), 1)) {
			attacker.performAnimation(anim2);
			int meleeDamage = Misc.getRandom(70);
			if(!target.getPrayerActive()[PrayerHandler.PROTECT_FROM_MELEE] && !target.getCurseActive()[CurseHandler.DEFLECT_MELEE])
				meleeDamage = 300 + Misc.getRandom(220);
			DamageHandler.handleAttack(attacker, target, new Damage(new Hit(meleeDamage, CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
			attacker.getCombatAttributes().setAttackDelay(5);
			return;
		} else
			if(Misc.getRandom(10) <= 5 || !Locations.goodDistance(attacker.getPosition().getX(), attacker.getPosition().getY(), attacker.getCombatAttributes().getCurrentEnemy().getPosition().getX(), attacker.getCombatAttributes().getCurrentEnemy().getPosition().getY(), 14)) {
				attacker.getCombatAttributes().setAttackDelay(7);
				attacker.performAnimation(anim3);
				attacker.performGraphic(gfx3);
				TaskManager.submit(new Task(1) {
					int tick = 0;
					@Override
					public void execute() {
						switch(tick) {
						case 2:
							fireGlobalProjectile(target, attacker, gfx5);
							break;
						case 4:
							int magicDmg = Misc.getRandom(40);
							boolean prayer = target.getPrayerActive()[PrayerHandler.PROTECT_FROM_MAGIC] || target.getCurseActive()[CurseHandler.DEFLECT_MAGIC];
							if(!prayer)
								magicDmg = 400 + Misc.getRandom(300);
							DamageHandler.handleAttack(attacker, target, new Damage(new Hit(magicDmg, CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
							stop();
							break;
						}
						tick++;
					}
				});
			} else {
				if(!Locations.goodDistance(attacker.getPosition().getX(), attacker.getPosition().getY(), attacker.getCombatAttributes().getCurrentEnemy().getPosition().getX(), attacker.getCombatAttributes().getCurrentEnemy().getPosition().getY(), 18))
					return;
				attacker.getCombatAttributes().setAttackDelay(7);
				attacker.performAnimation(anim4);
				attacker.performGraphic(gfx2);
				TaskManager.submit(new Task(4) {
					@Override
					public void execute() {
						target.performGraphic(gfx4);
						int rangedDmg = Misc.getRandom(55);
						boolean prayer = target.getPrayerActive()[PrayerHandler.PROTECT_FROM_MISSILES] || target.getCurseActive()[CurseHandler.DEFLECT_MISSILES];
						if(!prayer)
							rangedDmg = 400 + Misc.getRandom(300);
						DamageHandler.handleAttack(attacker, target, new Damage(new Hit(rangedDmg, CombatIcon.RANGED, Hitmask.RED)), AttackType.RANGED, false, false);
						stop();
					}
				});
			}
	}


}
