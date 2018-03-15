package org.trident.world.content.combat.pvp;

import org.trident.model.GroundItem;
import org.trident.model.Item;
import org.trident.util.Misc;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.player.Player;

public class PvPDrops {
	
	/*
	 * Artifacts
	 */
	private final static int[] LOW_ARTIFACTS = { 14888, 14889, 14890, 14891, 14892 };
	private final static int[] MED_ARTIFACTS = { 14883, 14884, 14885, 14886 };
	private final static int[] HIGH_ARTIFACTS = { 14878, 14879, 14880, 14881, 14882 };
	private final static int[] EXR_ARTIFACTS = { 14876, 14877 };
	
	/**
	 * Handles a target drop
	 * @param Player player		Player who has killed Player o
	 * @param Player o			Player who has been killed by Player player
	 */
	public static void handleDrops(Player killer, Player loser) {
		if (loser != null) {
			if (killer.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget() != null && loser.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget() != null && loser.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getIndex() == killer.getIndex()) {
				if(killer.getAppearance().getBountyHunterSkull() < 6)
					killer.getAppearance().setBountyHunterSkull(killer.getAppearance().getBountyHunterSkull()+1);
				if(loser.getAppearance().getBountyHunterSkull() > 1)
					killer.getAppearance().setBountyHunterSkull(killer.getAppearance().getBountyHunterSkull()-1);
				BountyHunter.resetTargets(killer, loser, true, "You have defeated your target!");
				if(Misc.getRandom(100) >= 60)
					GroundItemManager.spawnGroundItem(killer, new GroundItem(new Item(getRandomItem(LOW_ARTIFACTS)), loser.getPosition().copy(), killer.getUsername(), false, 110, true, 100));
				else if (Misc.getRandom(100) >= 85)
					GroundItemManager.spawnGroundItem(killer, new GroundItem(new Item(getRandomItem(MED_ARTIFACTS)), loser.getPosition().copy(), killer.getUsername(), false, 110, true, 100));
				else if(Misc.getRandom(100) >= 90)
					GroundItemManager.spawnGroundItem(killer, new GroundItem(new Item(getRandomItem(HIGH_ARTIFACTS)), loser.getPosition().copy(), killer.getUsername(), false, 110, true, 100));
				else if(Misc.getRandom(100) >= 95)
					GroundItemManager.spawnGroundItem(killer, new GroundItem(new Item(getRandomItem(EXR_ARTIFACTS)), loser.getPosition().copy(), killer.getUsername(), false, 110, true, 100));
			}
			BountyHunter.updateInterface(killer);
			BountyHunter.updateInterface(loser);
		}
	}
	
	/**
	 * Get's a random int from the array specified
	 * @param array	The array specified
	 * @return		The random integer
	 */
	public static int getRandomItem(int[] array) {
		return array[Misc.getRandom(array.length - 1)];
	}
}
