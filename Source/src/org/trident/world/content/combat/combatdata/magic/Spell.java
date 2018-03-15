package org.trident.world.content.combat.combatdata.magic;

import org.trident.model.Damage;
import org.trident.model.Item;
import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.minigames.impl.Dueling;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCData.CustomNPCData;
import org.trident.world.entity.player.Player;

/**
 * @author lare96
 */
public abstract class Spell {

	/**
	 * Determines if the spell is able to be cast.
	 *
	 * @param cast
	 * the person casting the spell.
	 * @param castOn
	 * the target of the spell.
	 * @return true if the spell is successful.
	 */
	public boolean prepareCast(Player p, GameCharacter castOn, boolean willCast) {
		if (p.getSkillManager().getCurrentLevel(Skill.MAGIC) < levelRequired()) {
			p.getPacketSender().sendMessage("You need a Magic level of at least " + levelRequired() + " to cast this spell.");
			return false;
		}
		if(!checkItemRequirements(p, willCast))
			return false;
		if(p.getLocation() == Location.DUEL_ARENA && Dueling.checkDuel(p, 5) && castOn != null) {
			if(p.getDueling().selectedDuelRules[Dueling.DuelRule.NO_MAGIC.ordinal()]) {
				p.getPacketSender().sendMessage("Magic-based attacks have been disabled in this duel.");
				return false;
			}
		}
		if(spellId() == 1171) { //Crumble undead spell
			if(castOn == null) //Autocast
				return true;
			if(castOn.isNpc()) {
				NPC n = (NPC) castOn; 
				if(CustomNPCData.isUndeadNpc(n))
					return true;
			}
			p.getPacketSender().sendMessage("This spell can only be cast on undead creatures.");
			return false;
		}
		return true;
	}
	
	public boolean checkItemRequirements(Player p, boolean willCast) {
		if (this.itemsRequired(p) != null) {
			Item[] compareItem = itemsRequired(p).clone();
			CombatMagicStaff runeStaff = getStaff(p);
			CombatMagicRuneCombination[] combinationRune = getCombinationRunes(p);
			Item[] removeRune = new Item[compareItem.length
			                             + combinationRune.length];
			int slot = 0;

			if (runeStaff != null) {
				for (int i = 0; i < compareItem.length; i++) {
					if (compareItem[i] == null) {
						continue;
					}

					for (int runeId : runeStaff.getRuneIds()) {
						if (compareItem[i] == null) {
							continue;
						}

						if (compareItem[i].getId() == runeId) {
							compareItem[i] = null;
							continue;
						}
					}
				}
			}

			for (int i = 0; i < compareItem.length; i++) {
				if (compareItem[i] == null) {
					continue;
				}

				for (CombatMagicRuneCombination rune : combinationRune) {
					if (compareItem[i] == null || rune == null) {
						continue;
					}

					int runesNeeded = compareItem[i].getAmount();

					if (compareItem[i].getId() == rune.getFirstRune()) {
						if (runesNeeded > p.getInventory()
								.getAmount(rune.getCombinationRune())) {
							continue;
						}

						compareItem[i].decrementAmountBy(runesNeeded);
						removeRune[slot++] = new Item(
								rune.getCombinationRune(), runesNeeded);
						if(willCast)
							p.getInventory()
							.getById(rune.getCombinationRune())
							.decrementAmountBy(runesNeeded);
					} else if (compareItem[i].getId() == rune
							.getSecondRune()) {
						if (runesNeeded > p.getInventory().getAmount(rune.getCombinationRune())) {
							continue;
						}

						compareItem[i].decrementAmountBy(runesNeeded);
						if(willCast)
							p.getInventory()
							.getById(rune.getCombinationRune())
							.decrementAmountBy(runesNeeded);
						removeRune[slot++] = new Item(
								rune.getCombinationRune(), runesNeeded);
					}

					if (compareItem[i].getAmount() == 0) {
						compareItem[i] = null;
					}
				}
			}

			if (!p.getInventory().contains(compareItem)) {
				p.getPacketSender()
				.sendMessage(
						"You do not have the required items to cast this spell.");
				if(willCast)
					p.getInventory().addItemSet(removeRune);
				return false;
			}
			if(willCast)
				p.getInventory().deleteItemSet(compareItem);
		}

		/** Check the equipment required. */
		if (this.equipmentRequired(p) != null) {
			if (!p.getEquipment()
					.contains(this.equipmentRequired(p))) {
				p.getPacketSender()
				.sendMessage(
						"You do not have the required equipment to cast this spell.");
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the staff that the player is currently wielding if any.
	 *
	 * @param player
	 * the player that will be checked for a staff.
	 * @return the staff that the player is currently wielding.
	 */
	public CombatMagicStaff getStaff(Player p) {
		for (CombatMagicStaff runeStaff : CombatMagicStaff.values()) {
			for (int itemId : runeStaff.getStaffIds()) {
				if (itemId == p.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId()) {
					return runeStaff;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the combination runes in the players inventory if any.
	 *
	 * @param player
	 * the player that will be checked for rune.
	 * @return the runes in the players inventory.
	 */
	public CombatMagicRuneCombination[] getCombinationRunes(Player p) {
		CombatMagicRuneCombination[] array = new CombatMagicRuneCombination[CombatMagicRuneCombination
		                                                                    .values().length];
		int slot = 0;

		for (CombatMagicRuneCombination rune : CombatMagicRuneCombination
				.values()) {
			if (p.getInventory().contains(rune.getCombinationRune())) {
				array[slot++] = rune;
			}
		}
		return array;
	}

	/**
	 * The id of the spell being cast (if any).
	 *
	 * @return the id of the spell.
	 */
	public abstract int spellId();

	/**
	 * The level required to cast this spell.
	 *
	 * @return the level required to cast this spell.
	 */
	public abstract int levelRequired();

	/**
	 * The base experience given when this spell is cast.
	 *
	 * @return the base experience given when this spell is cast.
	 */
	public abstract int baseExperience();

	/**
	 * The items required to cast this spell.
	 *
	 * @param player
	 * the player's inventory to check.
	 *
	 * @return the items required to cast this spell.
	 */
	public abstract Item[] itemsRequired(Player player);

	/**
	 * The equipment required to cast this spell.
	 *
	 * @param player
	 * the player's equipment to check.
	 *
	 * @return the equipment required to cast this spell.
	 */
	public abstract Item[] equipmentRequired(Player player);

	/**
	 * Invoked when the spell is cast.
	 *
	 * @param p
	 * the person casting the spell.
	 * @param castOn
	 * the target of the spell.
	 */
	public abstract void castSpell(Player p, GameCharacter castOn, Damage damage);
}