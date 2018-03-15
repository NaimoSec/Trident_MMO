package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.Input;
import org.trident.world.content.clan.ClanChatManager;
import org.trident.world.entity.player.Player;

public class EnterClanChatToJoin extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		if(syntax.length() <= 1) {
			player.getPacketSender().sendMessage("Invalid syntax entered.");
			return;
		}
		ClanChatManager.join(player, syntax);
	}
}
