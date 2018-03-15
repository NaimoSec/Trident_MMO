package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.content.PriceChecker;
import org.trident.world.entity.player.Player;

public class EnterAmountToRemoveFromPriceChecker extends EnterAmount {

	public EnterAmountToRemoveFromPriceChecker(int item) {
		super(item);
	}
	
	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getAttributes().isPriceChecking() && getItem() > 0)
			PriceChecker.removeItem(player, getItem(), amount);
		else
			player.getPacketSender().sendInterfaceRemoval();
	}

}
