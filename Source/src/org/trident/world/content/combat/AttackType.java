package org.trident.world.content.combat;

/**
 * Represents a GameCharacter's attack style.
 * 
 * @author relex lawl
 */

public enum AttackType {

	/*
	 * Close, hand-to-hand combat.
	 */
	MELEE,
	
	/*
	 * Far-ranged attacks with ammunition.
	 */
	RANGED,
	
	/*
	 * Far-ranged magical attacks.
	 */
	MAGIC;
	
	public static AttackType forId(int id) {
		for(AttackType types : AttackType.values()) {
			if(types.ordinal() == id) {
				return types;
			}
		}
		return null;
	}
}
