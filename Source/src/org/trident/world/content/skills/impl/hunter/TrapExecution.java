package org.trident.world.content.skills.impl.hunter;

import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.skills.impl.hunter.traps.BoxTrap;
import org.trident.world.entity.npc.NPC;

/**
 * 
 * @author Rene
 */
public class TrapExecution {

	/**
	 * Contains all interested Hunter NPCS
	 */

	/**
	 * Handles Trap's with the state of 'baiting'
	 * 
	 * @param trap
	 */
	public static void setBaitProcess(Trap trap) {
		/*
		 * if (trap.getTrapState() != TrapState.BAITING) { return; } for (NPC
		 * npc : interestedNpcs.values()) { if (npc == null || npc.isDead()) {
		 * continue; } if (trap.getOwner() != null) { if
		 * (trap.getOwner().playerLevel[PlayerConstants.HUNTER]
		 * < Hunter .requiredLevel(
		 * npc.getDefinition().getType())) { return; } if (Misc.random(50) <
		 * successFormula(trap, npc)) { Hunter.catchNPC(trap,
		 * npc); } } } interestedNpcs.clear();
		 */
	}

	/**
	 * Handles Trap's with a state of 'set'
	 * 
	 * @param trap
	 */
	public static void setTrapProcess(Trap trap) {
		for (final NPC npc : Hunter.hunterNpcs) {
			if (npc == null || npc.getAttributes().isDying() || !npc.getAttributes().isVisible())
				continue;
			if(trap instanceof BoxTrap && npc.getId() != 5079 && npc.getId() != 5080)
				continue;
			if (trap.getGameObject().getPosition().equals(npc.getPosition()))
				if (Misc.getRandom(100) < successFormula(trap, npc)) {
					Hunter.catchNPC(trap, npc);
					return;
				}
		}

	}

	public static int successFormula(Trap trap, NPC npc) {
		if (trap.getOwner() == null)
			return 0;
		int chance = 75;
		if (Hunter.hasLarupia(trap.getOwner()))
			chance = chance + 15;
		if (trap.isBaited())
			chance = chance + 20;
		chance = chance
				+ (int) (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) / 1.5)
				+ 10;

		if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 25)
			chance = (int) (chance * 1.5) + 8;
		if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 40)
			chance = (int) (chance * 1.4) + 3;
		if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 50)
			chance = (int) (chance * 1.3) + 1;
		if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 55)
			chance = (int) (chance * 1.2);
		if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 60)
			chance = (int) (chance * 1.1);
		if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < 65)
			chance = (int) (chance * 1.05) + 3;

		return chance;
	}

	/**
	 * Handles the cycle management of each traps timer
	 * 
	 * @param trap
	 *            is the given trap we are managing
	 * @return false if the trap is too new to have caught
	 */
	public static boolean trapTimerManagement(Trap trap) {
		if (trap.getTicks() > 0)
			trap.setTicks(trap.getTicks() - 1);
		if (trap.getTicks() <= 0) {
			Hunter.deregister(trap);
			if (trap.getOwner() != null)
				trap
				.getOwner()
				.getPacketSender()
				.sendMessage(
						"You've left your trap for too long, and have lost sight of it.");
		}

		return true;
	}
}
