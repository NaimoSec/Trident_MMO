package org.trident.model.inputhandling.impl;

import org.trident.model.container.impl.Bank.BankSearchAttributes;
import org.trident.model.inputhandling.Input;
import org.trident.world.entity.player.Player;

public class EnterSyntaxToBankSearchFor extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		boolean searchingBank = player.getAttributes().isBanking() && player.getAttributes().getBankSearchingAttribtues().isSearchingBank();
		if(searchingBank)
			BankSearchAttributes.beginSearch(player, syntax);
	}
}
