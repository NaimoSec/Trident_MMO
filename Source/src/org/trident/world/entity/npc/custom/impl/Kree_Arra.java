package org.trident.world.entity.npc.custom.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Position;
import org.trident.model.movement.MovementQueue;
import org.trident.util.Misc;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Kree_Arra extends CustomNPC {

	private static final Graphic graphic1 = new Graphic(128);

	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		if(!target.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()) {
			CombatHandler.resetAttack(attacker);
			return;
		}
		final AttackType style = Misc.getRandom(1) == 0 ? AttackType.MAGIC : AttackType.RANGED;
		attacker.performAnimation(attacker.getAttackAnimation());
		TaskManager.submit(new Task(1, false) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 1:
					for (final Player near : Misc.getCombinedPlayerList(target)) {
						if(near == null || near.getLocation() != Location.GODWARS_DUNGEON || near.isTeleporting())
							continue;
						if(near.getPosition().distanceToPoint(attacker.getPosition().getX(), attacker.getPosition().getY()) > 20)
							continue;
						CustomNPC.fireGlobalProjectile(near, attacker, new Graphic(style == AttackType.MAGIC ? 1198 : 1197));
						if(style == AttackType.RANGED) { //Moving players randomly
							int xToMove = Misc.getRandom(1);
							int yToMove = Misc.getRandom(1);
							int xCoord = target.getPosition().getX();
							int yCoord = target.getPosition().getY();
							if (xCoord >= 2841 || xCoord <= 2823 || yCoord <= 5295 || yCoord >= 5307) {
								return;
							} else if (Misc.getRandom(3) <= 1 && MovementQueue.canWalk(target, target.getPosition(), new Position(xCoord +- xToMove, yCoord +- yToMove, 2))) {
								target.getMovementQueue().stopMovement();
								if(!target.isTeleporting())
									target.moveTo(new Position(xCoord +- xToMove, yCoord +- yToMove, 2));
								target.performGraphic(graphic1);
							}
						}
					}
					break;
				case 2:
					for (final Player near : Misc.getCombinedPlayerList(target)) {
						if(near == null || near.getLocation() != Location.GODWARS_DUNGEON || near.isTeleporting())
							continue;
						if(near.getPosition().distanceToPoint(attacker.getPosition().getX(), attacker.getPosition().getY()) > 20)
							continue;
						int randomDmg = Misc.getRandom(attacker.getAttributes().getMaxHit());
						if(style == AttackType.MAGIC) {
							randomDmg -= Misc.getRandom(DamageHandler.getMagicDefence(target));
							if(near.getPrayerActive()[PrayerHandler.PROTECT_FROM_MAGIC] || near.getCurseActive()[CurseHandler.DEFLECT_MAGIC])
								randomDmg = Misc.getRandom(30);
							if(randomDmg > 210)
								randomDmg = 210;
						} else {//Style is ranged {
							randomDmg -= Misc.getRandom(DamageHandler.getRangedDefence(target));
							if(near.getPrayerActive()[PrayerHandler.PROTECT_FROM_MISSILES] || near.getCurseActive()[CurseHandler.DEFLECT_MISSILES])
								randomDmg = Misc.getRandom(30);
						}
						if(randomDmg < 0)
							randomDmg = 0;
						DamageHandler.handleAttack(attacker, near, new Damage(new Hit(randomDmg, CombatIcon.forId(style.ordinal()), Hitmask.RED)), style, false, false);
					}
					stop();
					break;
				}
				tick++;
			}
		});
	}

}
