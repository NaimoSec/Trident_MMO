package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.content.skills.impl.cooking.Cooking;
import org.trident.world.entity.player.Player;

public class EnterAmountToCook extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getSkillManager().getSkillAttributes().getSelectedItem() > 0)
			Cooking.cook(player, player.getSkillManager().getSkillAttributes().getSelectedItem(), amount);
	}

}
