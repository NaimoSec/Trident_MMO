package org.trident.world.content;

import org.trident.model.Skill;
import org.trident.world.entity.player.Player;

/**
 * Handles a player's run energy
 * @author Gabbe
 * Thanks to Russian for formula!
 */
public class EnergyHandler {

	public static void processPlayerEnergy(Player p) {
		if(p.getAttributes().isRunning() && p.getMovementQueue().isMoving()) {
			int energy = p.getAttributes().getRunEnergy();
			if (energy > 0) {
				energy = (energy-1);
				p.getAttributes().setRunEnergy(energy);
				p.getPacketSender().sendRunEnergy(energy);
			} else {
				p.getAttributes().setRunning(false);
				p.getPacketSender().sendRunStatus();
				p.getPacketSender().sendRunEnergy(0);
			}
		}
		if (p.getAttributes().getRunEnergy() < 100) {
			if (System.currentTimeMillis() > restoreEnergyFormula(p) + p.getAttributes().getLastRunRecovery()) {
				int energy = p.getAttributes().getRunEnergy() + 1;
				p.getAttributes().setRunEnergy(energy).setLastRunRecovery(System.currentTimeMillis());
				p.getPacketSender().sendRunEnergy(energy);
			}
		}
	}

	public static double restoreEnergyFormula(Player p) {
		return 2260 - (p.getSkillManager().getCurrentLevel(Skill.AGILITY) * (p.getAttributes().isResting() ? 13000 : 10));
	}
}
