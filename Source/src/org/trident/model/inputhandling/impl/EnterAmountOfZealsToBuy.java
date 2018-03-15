package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.entity.player.Player;

public class EnterAmountOfZealsToBuy extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		SoulWars.RewardShop.exchange(player, amount);
	}

}
