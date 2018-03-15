package org.trident.world.content.combat.combatdata.magic;

import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.entity.player.Player;

/**
 * Handles Autocasting Magic Spells
 * @author Gabbe
 *
 */
public class Autocasting {

	/**
	 * Array containing all the spell ID's
	 */
	private static int[] spellIds = { 1152, 1154, 1156, 1158, 1160, 1163, 1166, 1169,
		1171, 1172, 1175, 1177, 1181, 1183, 1185, 1188, 1189, 1539, 12037, 1190, 1191, 1192, 12939,
		12987, 12901, 12861, 12963, 13011, 12919, 12881, 12951, 12999, 12911,
		12871, 12975, 12929, 12891, 21744, 22168, 21745, 21746, 50163,
		50211, 50119, 50081, 50151, 50199, 50111, 50071, 50175, 50223,
		50129, 50091 };

	public static boolean handleAutocast(final Player p, int actionButtonId) {
		switch(actionButtonId) {
		case 6666:
			resetAutocast(p, true);
			return true;
		case 6667:
			resetAutocast(p, false);
			return true;
		}
		for (int i = 0; i < spellIds.length; i++) {
			if (actionButtonId == spellIds[i]) {
				CombatMagicSpells cbSpell = CombatMagicSpells.getSpell(actionButtonId);
				if(cbSpell == null) {
					p.getMovementQueue().stopMovement();
					return true;
				}
				CombatSpell spell = cbSpell.getSpell();
				if(!spell.prepareCast(p, null, false)) {
					resetAutocast(p, true);
					return true;
				}
				p.getPlayerCombatAttributes().getMagic().setAutocastSpell(spell).setCurrentSpell(spell);
				p.getCombatAttributes().setAttackType(AttackType.MAGIC);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Resets autocast
	 * @param p				The player to reset the autocast for
	 * @param fullreset		Should the client be updated?
	 */
	public static void resetAutocast(Player p, boolean fullreset) {
		if(fullreset) //Should the client update?
			p.getPacketSender().sendMessage("@autocastoff");
		CombatHandler.resetAttack(p);
		p.getPlayerCombatAttributes().getMagic().setAutocastSpell(null).setCurrentSpell(null);
		CombatHandler.setProperAttackType(p);
	}
}
