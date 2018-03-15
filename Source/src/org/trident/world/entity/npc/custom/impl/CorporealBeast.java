package org.trident.world.entity.npc.custom.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Position;
import org.trident.model.Projectile;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.Locations;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class CorporealBeast extends CustomNPC {

	private static final Animation attack_anim = new Animation(10496);
	private static final Animation attack_anim2 = new Animation(10410);
	private static final Graphic attack_graphic = new Graphic(1834);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		/*
		 * Stomp attack
		 */
		attacker.getCombatAttributes().setAttackType(AttackType.MAGIC);
		boolean stomp = false;
		for (Player t : Misc.getCombinedPlayerList(target)) {
			if(t == null || t.getLocation() != Location.CORPOREAL_BEAST)
				continue;
			if (Locations.goodDistance(t.getPosition(), attacker.getPosition(), 2)) {
				stomp = true;
				DamageHandler.handleAttack(attacker, t, new Damage(new Hit(Misc.getRandom(400), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
			}
		}
		if (stomp) {
			attacker.performAnimation(attack_anim);
			attacker.performGraphic(attack_graphic);
		}
		int attackStyle = Misc.getRandom(4);
		if (attackStyle == 0 || attackStyle == 1) { // melee
			int distanceX = target.getPosition().getX() - attacker.getPosition().getX();
			int distanceY = target.getPosition().getY() - attacker.getPosition().getY();
			if (distanceX > 4 || distanceX < -1 || distanceY > 4 || distanceY < -1)
				attackStyle = 2 + Misc.getRandom(2); // set mage
			else {
				attacker.getCombatAttributes().setAttackType(AttackType.MELEE);
				attacker.performAnimation(new Animation(attackStyle == 0 ? 10057 : 10058));
				if(target.getLocation() == Location.CORPOREAL_BEAST)
					DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(400), CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
				return;
			}
		}
		if (attackStyle == 2) { // powerfull mage spiky ball
			attacker.performAnimation(attack_anim2);
			DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(650), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
			target.getPacketSender().sendGlobalProjectile(new Projectile(new Position(attacker.getPosition().getX(), attacker.getPosition().getY(), 43), new Position(target.getPosition().getX(), target.getPosition().getY(), 31), new Graphic(1825), 0, 50, 78), target);
		} else if (attackStyle == 3) { // translucent ball of energy
			attacker.performAnimation(attack_anim2);
			if(target.getLocation() == Location.CORPOREAL_BEAST)
				DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(550), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
			target.getPacketSender().sendGlobalProjectile(new Projectile(new Position(attacker.getPosition().getX(), attacker.getPosition().getY(), 43), new Position(target.getPosition().getX(), target.getPosition().getY(), 31), new Graphic(1823), 0, 50, 78), target);
			TaskManager.submit(new Task(1, target, false) {
				@Override
				public void execute() {
					int skill = Misc.getRandom(4);
					skill = skill == 0 ? 6 : (skill == 1 ? 23 : 1);
					Skill skillT = Skill.forId(skill);
					Player player = (Player) target;
					int lvl = player.getSkillManager().getCurrentLevel(skillT);
					lvl -= 1 + Misc.getRandom(4);
					player.getSkillManager().setCurrentLevel(skillT, lvl < 0 ? 0 : lvl);
					target.getPacketSender().sendMessage("Your " + skillT.getPName() +" has been slighly drained!");
					stop();
				}
			});
		} else if(attackStyle == 4) {
			attacker.performAnimation(attack_anim2);
			for (Player t : Misc.getCombinedPlayerList(target)) {
				if(t == null || t.getLocation() != Location.CORPOREAL_BEAST)
					continue;
				target.getPacketSender().sendProjectile(new Projectile(new Position(attacker.getPosition().getX(), attacker.getPosition().getY(), 43), new Position(t.getPosition().getX(), t.getPosition().getY(), 31), new Graphic(1824), 0, 50, 78), t);
			}
			TaskManager.submit(new Task(1, target, false) {
				@Override
				public void execute() {
					for (Player t : Misc.getCombinedPlayerList(target)) {
						if(t == null || t.getLocation() != Location.CORPOREAL_BEAST)
							continue;
						DamageHandler.handleAttack(attacker, t, new Damage(new Hit(Misc.getRandom(400), CombatIcon.RANGED, Hitmask.RED)), AttackType.RANGED, false, false);
					}
					stop();
				}
			});
		}
	}

}
