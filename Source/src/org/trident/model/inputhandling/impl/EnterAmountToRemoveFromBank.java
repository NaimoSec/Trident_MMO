package org.trident.model.inputhandling.impl;

import org.trident.model.Item;
import org.trident.model.container.impl.Bank;
import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.entity.player.Player;

public class EnterAmountToRemoveFromBank extends EnterAmount {


	public EnterAmountToRemoveFromBank(int item, int slot) {
		super(item, slot);
	}

	@Override
	public void handleAmount(Player player, int amount) {
		if(!player.getAttributes().isBanking())
			return;
		int tab = Bank.getTabForItem(player, getItem());
		int item = player.getAttributes().getBankSearchingAttribtues().isSearchingBank() && player.getAttributes().getBankSearchingAttribtues().getSearchedBank() != null ? player.getAttributes().getBankSearchingAttribtues().getSearchedBank().getItems()[getSlot()].getId() : player.getBank(tab).getItems()[getSlot()].getId();
		if(item != getItem())
			return;
		if(!player.getBank(tab).contains(item))
			return;
		int invAmount = player.getBank(tab).getAmount(item);
		if(amount > invAmount) 
			amount = invAmount;
		if(amount <= 0)
			return;
		player.getBank(tab).setPlayer(player).switchItem(player.getInventory(), new Item(item, amount), player.getBank(tab).getSlot(item), false, true);
	}
}
