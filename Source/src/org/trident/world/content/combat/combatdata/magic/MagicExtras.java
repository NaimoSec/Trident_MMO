package org.trident.world.content.combat.combatdata.magic;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Graphic;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

/**
 * Handles Misc stuff such as other spells and tools.
 * @author Gabbe
 *
 */

public class MagicExtras {

	/** @author Gabbe
	 * Handles 'snare-spells' such as Bind, Snare and Entangle.
	 * @param c	The player that cast the spell
	 * @param target	The target to cast the spell on
	 * @param delay		The amount of time the spell will last
	 * @param graphic	The graphic to send after the spell has taken action
	 */

	public static void freezeTarget(GameCharacter target, int delay, Graphic graphic) {
		if(target == null || target.getConstitution() <= 0 || target.getCombatAttributes().getFreezeDelay() > 0)
			return;
		target.getCombatAttributes().setFreezeDelay(delay);
		target.getMovementQueue().stopMovement();
		if(target instanceof Player) {
			Player targ = (Player) target;
			if(targ != null)
				targ.getPacketSender().sendMessage("You have been frozen!");
		}
		if(graphic != null)
			target.performGraphic(graphic);
	}

	/** @author Gabbe
	 * Handles weakening spells such as Confusion, Weaken, Stun
	 * @param plr	The player that cast the spell
	 * @param target	The target to cast the spell on
	 * @param skill	The skill to weaken/disable
	 * @param amount	The percentage amount to weaken/disable
	 */
	public static void weakenSkill(Player plr, GameCharacter target, Skill skill, double amount) {
		boolean immune = false;
		if(target instanceof Player) { //Target is a player
			Player c = (Player) target;
			immune = c.getSkillManager().getCurrentLevel(skill) < c.getSkillManager().getMaxLevel(skill) - 3;
			if(immune) {
				plr.getPacketSender().sendMessage("That target is currently immune to this kind of disable.");
				return;
			}
			if(skill == Skill.ATTACK) {
				c.getSkillManager().setCurrentLevel(Skill.ATTACK, (int) (c.getSkillManager().getCurrentLevel(Skill.ATTACK) * amount));
				if(c.getSkillManager().getCurrentLevel(Skill.ATTACK) <= 0)
					c.getSkillManager().setCurrentLevel(Skill.ATTACK, 1);
			} else if(skill == Skill.STRENGTH) {
				c.getSkillManager().setCurrentLevel(Skill.STRENGTH, (int) (c.getSkillManager().getCurrentLevel(Skill.STRENGTH) * amount));
				if(c.getSkillManager().getCurrentLevel(Skill.STRENGTH) <= 0)
					c.getSkillManager().setCurrentLevel(Skill.STRENGTH, 1);
			} else if(skill == Skill.DEFENCE) {
				c.getSkillManager().setCurrentLevel(Skill.DEFENCE, (int) (c.getSkillManager().getCurrentLevel(Skill.DEFENCE) * amount));
				if(c.getSkillManager().getCurrentLevel(Skill.DEFENCE) <= 0)
					c.getSkillManager().setCurrentLevel(Skill.DEFENCE, 1);
			}
			c.getPacketSender().sendMessage("Your "+Misc.formatText(skill.toString().toLowerCase())+" level has been lowered!");
			plr.getPacketSender().sendMessage("You manage to reduce your target's "+Misc.formatText(skill.toString().toLowerCase())+" level.");
		} else if(target instanceof NPC) {
			NPC n = (NPC) target;
			if(skill == Skill.ATTACK)
				immune = n.getAttributes().getAttackLevel() < n.getDefaultAttributes().getAttackLevel() - 3;
			else if(skill == Skill.STRENGTH)
				immune = n.getAttributes().getStrengthLevel() < n.getDefaultAttributes().getStrengthLevel() - 3;
			else if(skill == Skill.DEFENCE)
				immune = n.getAttributes().getDefenceLevel() < n.getDefaultAttributes().getDefenceLevel() - 3;
			if(immune) {
				plr.getPacketSender().sendMessage("That target is currently immune to this kind of disable.");
				return;
			}
			if(skill == Skill.ATTACK) {
				n.getAttributes().setAttackLevel((int) (n.getAttributes().getAttackLevel() * amount));
				restoreSkillTask(n, Skill.ATTACK);
			} else if(skill == Skill.STRENGTH) {
				n.getAttributes().setStrengthLevel((int) (n.getAttributes().getStrengthLevel() * amount));
				restoreSkillTask(n, Skill.STRENGTH);
			} else if(skill == Skill.DEFENCE) {
				n.getAttributes().setDefenceLevel((int) (n.getAttributes().getDefenceLevel() * amount));
				restoreSkillTask(n, Skill.DEFENCE);
			}
		}
	}

	/**
	 * A Task that restores an NPC's specified skill after being disabled.
	 * @param n The NPC to restore the skill for
	 * @param skill The skill to restore
	 */
	private static void restoreSkillTask(final NPC n, final Skill skill) {
		TaskManager.submit(new Task(30, false) {
			@Override
			public void execute() {
				if(n == null || n.getConstitution() <= 0) {
					this.stop();
					return;
				}
				if(skill == Skill.ATTACK)
					n.getAttributes().setAttackLevel(n.getDefaultAttributes().getAttackLevel());
				else if(skill == Skill.STRENGTH)
					n.getAttributes().setStrengthLevel(n.getDefaultAttributes().getStrengthLevel());
				else if(skill == Skill.DEFENCE)
					n.getAttributes().setDefenceLevel(n.getDefaultAttributes().getDefenceLevel());
				this.stop();
			}
		});
	}

	public static void teleportBlockDecrease(final GameCharacter p) {
		TaskManager.submit(new Task(1) {
			@Override
			public void execute() {
				if(p == null || p.getConstitution() <= 0) { //Target died, reset tele block delay
					p.getCombatAttributes().setTeleportBlockDelay(0);
					stop();
					return;
				}
				if(p.getCombatAttributes().getTeleportBlockDelay() > 0)
					p.getCombatAttributes().setTeleportBlockDelay(p.getCombatAttributes().getTeleportBlockDelay() - 1);
				else
					stop();
			}
		});
	}
}
