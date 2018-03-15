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
import org.trident.util.Misc;
import org.trident.world.content.Locations;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class General_Graardor extends CustomNPC {
	
	private static final Animation attack_anim = new Animation(7063);
	private static final Graphic graphic1 = new Graphic(1200, GraphicHeight.MIDDLE);

	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		AttackType style = Misc.getRandom(4) <= 1 && Locations.goodDistance(attacker.getPosition(), target.getPosition(), 1) ? AttackType.MELEE : AttackType.RANGED;
		switch(style) {
		case MELEE:
			attacker.performAnimation(attacker.getAttackAnimation());
			int damage = 400 + Misc.getRandom(250);
			damage -= DamageHandler.getMeleeDefence(target);
			DamageHandler.handleAttack(attacker, target, new Damage(new Hit(damage, CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
			break;
		default:
			attacker.performAnimation(attack_anim);
			for (final Player near : Misc.getCombinedPlayerList(target)) {
				if(near == null || near.getLocation() != Location.GODWARS_DUNGEON || near.isTeleporting())
					continue;
				if(near.getPosition().distanceToPoint(attacker.getPosition().getX(), attacker.getPosition().getY()) > 20)
					continue;
				CustomNPC.fireGlobalProjectile(target, attacker, graphic1);
			}
			TaskManager.submit(new Task(2, false) {
				@Override
				public void execute() {
					for (final Player near : Misc.getCombinedPlayerList(target)) {
						if(near == null || near.getLocation() != Location.GODWARS_DUNGEON || near.isTeleporting() || !near.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom())
							continue;
						if(near.getPosition().distanceToPoint(attacker.getPosition().getX(), attacker.getPosition().getY()) > 20)
							continue;
						int randomDmg = 200 + Misc.getRandom(200);
						randomDmg -= Misc.getRandom(DamageHandler.getRangedDefence(near));
						DamageHandler.handleAttack(attacker, target, new Damage(new Hit(randomDmg, CombatIcon.RANGED, Hitmask.RED)), AttackType.RANGED, false, false);
					}
					stop();
				}
			});
			break;
		}
	}

}
