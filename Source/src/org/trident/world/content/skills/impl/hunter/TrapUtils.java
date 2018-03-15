package org.trident.world.content.skills.impl.hunter;

import org.trident.model.Animation;
import org.trident.world.content.skills.impl.hunter.traps.BoxTrap;
import org.trident.world.content.skills.impl.hunter.traps.SnareTrap;
import org.trident.world.entity.player.Player;

/**
 * 
 * @author Faris
 */
public class TrapUtils {

	/**
	 * Instances the Hunter
	 */
	private static TrapUtils INSTANCE = new TrapUtils();

	/**
	 * Gets the Instance
	 */
	public static TrapUtils getInstance() {
		if (INSTANCE == null)
			return new TrapUtils();
		return INSTANCE;
	}

	/**
	 * Adds the baited state to the given trap and handles all the associated
	 * player actions, animations and messages
	 * 
	 * @param client
	 *            is the player using the bait
	 * @param trap
	 *            is the given trap
	 * @param bait
	 *            is the type of bait they are adding
	 */
	public void baitTrap(Player client, Trap trap, Bait bait) {
		if (!isCorrectBait(trap, bait)) {
			client.getPacketSender().sendMessage(
					"This is the wrong type of bait for your trap.");
			return;
		}
		client.getPacketSender().sendMessage(
				"You add some bait to your trap...");
		client.setPositionToFace(trap.getGameObject().getPosition());
		client.performAnimation(new Animation(827));
		trap.setBaited(true);
		client.getInventory().delete(
				getBaitForTrap(trap).getBaitId(), 1);
	}

	public Bait getBait(int itemId) {
		switch (itemId) {
		case 9996:
			return Bait.SPICY_MINCED_MEAT;
		case 1982:
			return Bait.TOMATO;
		}
		return null;
	}

	/**
	 * the correct bait type to be used with the trap
	 * 
	 * @param trap
	 *            is the given trap
	 * @return the bait required
	 */
	private Bait getBaitForTrap(Trap trap) {
		if (trap instanceof BoxTrap)
			return Bait.SPICY_MINCED_MEAT;
		if (trap instanceof SnareTrap)
			return Bait.TOMATO;
		return null;
	}

	/**
	 * Checks if the used bait is the correct bait to be used with the given
	 * trap type
	 * 
	 * @param trap
	 *            is the given trap reference
	 * @param bait
	 *            is the type of bait used
	 * @return
	 */
	public boolean isCorrectBait(Trap trap, Bait bait) {
		return bait == getBaitForTrap(trap);
	}

}
