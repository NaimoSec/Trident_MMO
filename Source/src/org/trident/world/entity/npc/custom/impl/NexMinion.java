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
import org.trident.model.movement.MovementStatus;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCAggression;
import org.trident.world.entity.npc.NPCAttributes;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

/**
 * @author Gabbe
 */
public class NexMinion extends NPC {
	
	public NexMinion(int id, Position position) {
		super(id, position);
		setAttributes(new NPCAttributes().setAggressive(true).setAttackable(true).setWalkingDistance(0).setAttackLevel(1).setDefenceLevel(350).setStrengthLevel(1).setConstitution(4500).setAbsorbMelee(40).setAbsorbRanged(40).setAbsorbMagic(40).setAttackSpeed(8).setMaxHit(350)).setDefaultAttributes(new NPCAttributes().setAggressive(true).setAttackable(true).setWalkingDistance(0).setAttackLevel(1).setDefenceLevel(350).setStrengthLevel(1).setConstitution(4500).setAbsorbMelee(40).setAbsorbRanged(40).setAbsorbMagic(40).setAttackSpeed(8).setMaxHit(350));
		World.register(this);
		getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
	}

	private static final Animation anim = new Animation(1979);

	public static void attack(final NPC attacker, final Player target) {
		if(!target.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom() || Misc.getRandom(20) <= 2) {
			Player plr2 = Misc.getCloseRandomPlayer(target.getAttributes().getLocalPlayers());
			if(plr2 != null) {
				CombatHandler.resetAttack(attacker);
				NPCAggression.processFor(plr2);
				return;
			}
		}
		attacker.performAnimation(anim);
		int gfx = 383;
		switch(attacker.getId()) {
		case 13451:
			gfx = 383;
			break;
		case 13452:
			gfx = 381;
			break;
		case 13453:
			gfx = 373;
			break;
		case 13454:
			gfx = 367;
			break;
		}
		target.performGraphic(new Graphic(gfx));
		TaskManager.submit(new Task(1, target, false) {
			@Override
			public void execute() {
				int magicDmg = 100 + Misc.getRandom(150) - DamageHandler.getMagicDefence(target);
				if(magicDmg > 0)
					DamageHandler.handleAttack(attacker, target, new Damage(new Hit(magicDmg, CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
				stop();
			}
		});
	}

	public static void handleDeath(int npc) {
		int index = npc - 13451;
		if(index >= 0)
			CustomNPC.getNex().getMagesKilled()[index] = true;
	}

	public static boolean nexMinion(int npc) {
		return npc >= 13451 && npc <= 13454;
	}

	public static boolean attackable(int npc) {
		int index = npc - 13451;
		if(index >= 0)
			return CustomNPC.getNex().getMagesAttackable()[index];
		return true;
	}

}
