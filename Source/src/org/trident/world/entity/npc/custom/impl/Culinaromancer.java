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
import org.trident.model.Position;
import org.trident.util.Misc;
import org.trident.world.content.Locations;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Culinaromancer extends CustomNPC {

	private static final Animation tele_anim = new Animation(803);
	private static final Animation attack_anim = new Animation(1979);
	private static final Graphic splash_graphic = new Graphic(85, GraphicHeight.MIDDLE);
	private static final Graphic graphic1 = new Graphic(369);
	private static final Graphic graphic2 = new Graphic(1677, GraphicHeight.MIDDLE);
	
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		boolean teleport = Misc.getRandom(10) == 1;
		if(teleport) {
			attacker.performAnimation(tele_anim);
			attacker.getMovementQueue().stopMovement();
			attacker.moveTo(new Position(1893, 5347 + Misc.getRandom(10), attacker.getPosition().getZ()));
			return;
		}
		boolean melee = Misc.getRandom(6) == 1;
		if(melee && Locations.goodDistance(attacker.getPosition().copy(), target.getPosition().copy(), 1)) {
			DamageHandler.handleAttack(attacker, target, getBaseDamage(attacker, target), AttackType.MELEE, false, false);
			return;
		}
		attacker.performAnimation(attack_anim);
		TaskManager.submit(new Task(2) {
			@Override
			public void execute() {
				int magicDmg = 100 + Misc.getRandom(220) - DamageHandler.getMagicDefence(target);
				boolean froze = Misc.getRandom(10) < 3 || target.getCombatAttributes().getFreezeDelay() > 0;
				if(!froze)
					target.performGraphic(graphic1);
				else
					target.performGraphic(graphic2);
				if(magicDmg <= 0) {
					magicDmg = 0;
					target.performGraphic(splash_graphic);
				} else if(target.getCombatAttributes().getFreezeDelay() < 1) {
					target.getCombatAttributes().setFreezeDelay(15);
					target.getPacketSender().sendMessage("You have been frozen!");
					target.getMovementQueue().stopMovement();
				}
				Damage damage = new Damage(new Hit(magicDmg, CombatIcon.MAGIC, Hitmask.RED));
				DamageHandler.handleAttack(attacker, target, damage, AttackType.MAGIC, false, false);
				stop();
			}
		});
	}

}
