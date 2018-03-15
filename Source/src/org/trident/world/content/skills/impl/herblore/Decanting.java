package org.trident.world.content.skills.impl.herblore;

import org.trident.world.entity.player.Player;

public class Decanting {

	public static void startDecanting(Player c) {
		for (Potion p : Potion.values()) {
			int full = p.getFullId();
			int half = p.getHalfId();
			int quarter = p.getQuarterId();
			int threeQuarters = p.getThreeQuartersId();
			int totalDoses = 0;
			int remainder = 0;
			int totalEmptyPots = 0;
			if (c.getInventory().contains(threeQuarters)) {
				totalDoses += (3 * c.getInventory().getAmount(threeQuarters));
				totalEmptyPots += c.getInventory().getAmount(threeQuarters);
				c.getInventory().delete(threeQuarters,
						c.getInventory().getAmount(threeQuarters));
			}
			if (c.getInventory().contains(half)) {
				totalDoses += (2 * c.getInventory().getAmount(half));
				totalEmptyPots += c.getInventory().getAmount(half);
				c.getInventory().delete(half, c.getInventory().getAmount(half));
			}
			if (c.getInventory().contains(quarter)) {
				totalDoses += (1 * c.getInventory().getAmount(quarter));
				totalEmptyPots += c.getInventory().getAmount(quarter);
				c.getInventory().delete(quarter,
						c.getInventory().getAmount(quarter));
			}
			if (totalDoses > 0) {
				if (totalDoses >= 4)
					c.getInventory().add(full, totalDoses / 4);
				else if (totalDoses == 3)
					c.getInventory().add(threeQuarters, 1);
				else if (totalDoses == 2)
					c.getInventory().add(half, 1);
				else if (totalDoses == 1)
					c.getInventory().add(quarter, 1);
				if ((totalDoses % 4) != 0) {
					totalEmptyPots -= 1;
					remainder = totalDoses % 4;
					if (remainder == 3)
						c.getInventory().add(threeQuarters, 1);
					else if (remainder == 2)
						c.getInventory().add(half, 1);
					else if (remainder == 1)
						c.getInventory().add(quarter, 1);
				}
				totalEmptyPots -= (totalDoses / 4);
				c.getInventory().add(229, totalEmptyPots);
			}
		}
	}
}
