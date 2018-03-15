package org.trident.world.content.combat.combatdata.magic;

/**
 * Holds data for all combination runes that can be used in place of two
 * different runes.
 *
 * @author lare96
 */
public enum CombatMagicRuneCombination {

	STEAM(4694, 555, 554), MIST(4695, 555, 556), DUST(4696, 556, 557), SMOKE(
			4697, 556, 554), MUD(4698, 555, 557), LAVA(4699, 557, 554);

	/** The combination rune. */
	private int combinationRune;

	/** The first substitute rune. */
	private int firstRune;

	/** The second substitute rune. */
	private int secondRune;

	/**
	 * Create a new {@link CombatMagicRuneCombination}.
	 *
	 * @param combinationRune
	 * the combination rune.
	 * @param firstRune
	 * the first substitute rune.
	 * @param secondRune
	 * the second substitute rune
	 */
	private CombatMagicRuneCombination(int combinationRune, int firstRune,
			int secondRune) {
		this.combinationRune = combinationRune;
		this.firstRune = firstRune;
		this.secondRune = secondRune;
	}

	/**
	 * Gets the combination rune.
	 *
	 * @return the combination rune.
	 */
	public int getCombinationRune() {
		return combinationRune;
	}

	/**
	 * Gets the first substitute rune.
	 *
	 * @return the first substitute rune.
	 */
	public int getFirstRune() {
		return firstRune;
	}

	/**
	 * Gets the second substitute rune.
	 *
	 * @return the second substitute rune
	 */
	public int getSecondRune() {
		return secondRune;
	}
}