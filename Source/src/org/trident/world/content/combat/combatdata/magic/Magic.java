package org.trident.world.content.combat.combatdata.magic;


public class Magic {
	
	private CombatSpell currentSpell, autocastSpell;
	
	public Magic setCurrentSpell(CombatSpell currentSpell) {
		this.currentSpell = currentSpell;
		return this;
	}
	
	public CombatSpell getCurrentSpell() {
		return currentSpell;
	}
	
	public Magic setAutocastSpell(CombatSpell autocastSpell) {
		this.autocastSpell = autocastSpell;
		return this;
	}
	
	public CombatSpell getAutocastSpell() {
		return autocastSpell;
	}
	
	public static final int ARMADYL_STORM_SPELL_ID = 21748;
}
