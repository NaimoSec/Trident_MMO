package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.entity.player.Player;

public class EnterAmountToStore extends EnterAmount {

	public EnterAmountToStore(int item, int slot) {
		super(item, slot);
	}
	
	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getAdvancedSkills().getSummoning().isStoring() && getItem() > 0 && getSlot() >= 0 && getSlot() < 28)
			player.getAdvancedSkills().getSummoning().storeItem(getItem(), amount, getSlot());
		else
			player.getPacketSender().sendInterfaceRemoval();

	}

}
