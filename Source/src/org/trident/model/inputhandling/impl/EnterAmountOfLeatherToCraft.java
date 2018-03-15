package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.content.skills.impl.crafting.LeatherMaking;
import org.trident.world.content.skills.impl.crafting.leatherData;
import org.trident.world.entity.player.Player;

public class EnterAmountOfLeatherToCraft extends EnterAmount {

	private int button;
	public EnterAmountOfLeatherToCraft(int button) {
		this.button = button;
	}
	
	@Override
	public void handleAmount(Player player, int amount) {
		for (final leatherData l : leatherData.values()) {
			if (l.getAmount(button + 1) == 10 && player.getSkillManager().getSkillAttributes().getSelectedItem() == l.getLeather()) {
				LeatherMaking.craftLeather(player, l, amount);
				break;
			}
		}
	}

	public static boolean isCrafting(Player player) {
		for (final leatherData l : leatherData.values()) {
			if (player.getSkillManager().getSkillAttributes().getSelectedItem() == l.getLeather()) {
				return true;
			}
		}
		return false;
	}
}
