package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.content.minigames.impl.Dueling;
import org.trident.world.entity.player.Player;

public class EnterAmountToStake extends EnterAmount {

	public EnterAmountToStake(int item, int slot) {
		super(item, slot);
	}
	
	@Override
	public void handleAmount(Player player, int amount) {
		if(Dueling.checkDuel(player, 1) && getItem() > 0 && getSlot() >= 0 && getSlot() < 28)
			player.getDueling().stakeItem(getItem(), amount, getSlot());
		else
			player.getPacketSender().sendInterfaceRemoval();
	}

}
