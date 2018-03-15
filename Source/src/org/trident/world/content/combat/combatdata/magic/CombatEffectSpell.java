package org.trident.world.content.combat.combatdata.magic;

import org.trident.model.Item;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * A {@link Spell} implemenation primarily used for spells that have effects
 * when they hit the player.
 *
 * @author lare96
 */
public abstract class CombatEffectSpell extends CombatSpell {

	@Override
	public int maximumStrength() {

		/** Effect spells don't show a hitsplat. */
		return -1;
	}

	@Override
	public Item[] equipmentRequired(Player player) {

		/** Effect spells never require equipment. */
		return null;
	}

	@Override
	public void endCast(Player cast, GameCharacter castOn,
			int damageInflicted) {
		if (damageInflicted > 0) {
			spellEffect(cast, castOn);
		}
	}

	/**
	 * The effect that will take place once the spell hits the target.
	 *
	 * @param cast
	 * the caster of the spell.
	 * @param castOn
	 * the target being hit by the spell.
	 */
	public abstract void spellEffect(Player cast, GameCharacter castOn);
}