package org.trident.world.content.skills.impl.herblore;

import org.trident.model.Animation;
import org.trident.model.Skill;
import org.trident.model.definitions.ItemDefinition;
import org.trident.world.entity.player.Player;

public class GrindingAction {

	/**
	 * Id of Pestle and Mortar
	 */
	public static final int PESTLE_AND_MORTAR = 233;

	/**
	 * Enum holding data for id's and end products.
	 * 
	 * @author Bane(http://www.rune-server.org/members/bane_/)
	 * @see org.trident.world.content.skills.impl.herblore
	 */
	public enum Data {
		UNICORN(237, 235, 20), CHOCO(1973, 1975, 50), NEST(5075, 6693, 65), KEBBIT(
				10109, 10111, 100), SCALE(243, 241, 175), DIAMOND(14703, 14704,
				225), GOAT(9735, 9736, 320), SHARDS(6466, 6467, 300), MUD(4698,
				9594, 350), ASHES(592, 8865, 380), WEED1(401, 6683, 400), WEED2(
				403, 6683, 420), BAT(530, 2391, 445), CHARCOAL(973, 704, 460), COD(
				341, 7528, 490), KELP(7516, 7517, 520), CRABMEAT(7518, 7527,
				540), ASTRAL(11156, 11155, 600), SQUAH(9079, 9082, 650), THISLE(
				3263, 3264, 710), GARLIC(1550, 4698, 765), MUSHROOM(4620, 4622,
				840);

		/**
		 * Start id.
		 */
		public int id;

		/**
		 * End id.
		 */
		public int end;

		/**
		 * Amount of experience gained.
		 */
		public int xp;

		/**
		 * 
		 * @param id
		 *            start id for the uncrushed items.
		 * @param end
		 *            end id for the crushed items.
		 * @param xp
		 *            xp gained from grinding items.
		 */
		Data(int id, int end, int xp) {
			this.id = id;
			this.end = end;
			this.xp = xp;
		}

		/**
		 * Getter for Id.
		 * 
		 * @return
		 */
		public int getId() {
			return id;
		}

		/**
		 * Getter for End id.
		 * 
		 * @return
		 */
		public int getEnd() {
			return end;
		}

		/**
		 * Getter for xp gained.
		 * 
		 * @return
		 */
		private int getXp() {
			return xp;
		}
	}

	/**
	 * Initialize method for grinding the items.
	 * 
	 * @param c
	 * @param itemUsed
	 * @param useWith
	 */
	public static void init(Player c, int itemUsed, int useWith) {
		for (Data d : Data.values()) {
			if (itemUsed == PESTLE_AND_MORTAR && useWith == d.getId()
					|| itemUsed == d.getId() && useWith == PESTLE_AND_MORTAR) {
				c.performAnimation(new Animation(364));
				c.getInventory().delete(d.getId(), 1);
				c.getInventory().add(d.getEnd(), 1);
				c.getPacketSender().sendMessage(
						"You carefully grind the "
								+ ItemDefinition.forId(d.getId()).getName()
								+ ".");
				c.getSkillManager().addExperience(Skill.HERBLORE, d.getXp(), false);
			}
		}
	}
}
