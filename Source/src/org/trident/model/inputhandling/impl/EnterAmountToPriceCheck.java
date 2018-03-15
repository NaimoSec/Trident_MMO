package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.content.PriceChecker;
import org.trident.world.entity.player.Player;

public class EnterAmountToPriceCheck extends EnterAmount {

	public EnterAmountToPriceCheck(int item, int slot) {
		super(item, slot);
	}
	
	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getAttributes().isPriceChecking() && getItem() > 0 && getSlot() >= 0 && getSlot() < 28)
			PriceChecker.priceCheckItem(player, getItem(), amount, getSlot());
		else
			player.getPacketSender().sendInterfaceRemoval();
	}
	
}
