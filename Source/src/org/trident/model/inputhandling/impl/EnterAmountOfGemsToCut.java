package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.content.skills.impl.crafting.Gems;
import org.trident.world.entity.player.Player;

public class EnterAmountOfGemsToCut extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getSkillManager().getSkillAttributes().getSelectedItem() > 0)
			Gems.cutGem(player, amount, player.getSkillManager().getSkillAttributes().getSelectedItem());
	}

}
