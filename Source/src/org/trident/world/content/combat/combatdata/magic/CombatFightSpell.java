package org.trident.world.content.combat.combatdata.magic;

import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * A {@link Spell} implemenation primarily used for spells that have no effects
 * at all when they hit the player.
 *
 * @author lare96
 */
public abstract class CombatFightSpell extends CombatSpell {

	@Override
	public void endCast(Player cast, GameCharacter castOn, int damageInflicted) {

		/** Normal combat spells have no effects. */
	}
}