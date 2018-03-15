package org.trident.world.content.combat.combatdata.magic;

/**
* Holds data for all of the staves that can be used in place of runes.
*
* @author lare96
*/
public enum CombatMagicStaff {

	AIR(new int[] { 1381, 1397, 1405 }, new int[] { 556 }), WATER(new int[] {
			1383, 1395, 1403 }, new int[] { 555 }), EARTH(new int[] { 1385,
					1399, 1407 }, new int[] { 557 }), FIRE(
							new int[] { 1387, 1393, 1401 }, new int[] { 554 }), MUD(new int[] {
									6562, 6563 }, new int[] { 555, 557 }), LAVA(
											new int[] { 3053, 3054 }, new int[] { 554, 557 });

	/** The item ids of the staves that can be used in place of runes. */
	private int[] itemIds;

	/** The item ids of the runes that the staves can be used for. */
	private int[] runeIds;

	/**
	 * Create a new {@link CombatMagicStaff}.
	 *
	 * @param itemIds
	 * the item ids of the staves that can be used in place of runes.
	 * @param runeIds
	 * the item ids of the runes that the staves can be used for.
	 */
	private CombatMagicStaff(int[] itemIds, int[] runeIds) {
		this.itemIds = itemIds;
		this.runeIds = runeIds;
	}

	/**
	 * Gets the item ids of the staves that can be used in place of runes.
	 *
	 * @return the item ids of the staves that can be used in place of runes.
	 */
	public int[] getStaffIds() {
		return itemIds;
	}

	/**
	 * Gets the item ids of the runes that the staves can be used for.
	 *
	 * @return the item ids of the runes that the staves can be used for.
	 */
	public int[] getRuneIds() {
		return runeIds;
	}
}