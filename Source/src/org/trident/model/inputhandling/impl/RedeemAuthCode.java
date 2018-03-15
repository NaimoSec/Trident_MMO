package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.Input;
import org.trident.world.entity.player.Player;

public class RedeemAuthCode extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		if(syntax == null || syntax.length() <= 2 || syntax.length() > 15) {
			player.getPacketSender().sendMessage("Invalid password syntax entered!");
			return;
		}
		//Voting.handleAuth(player, syntax);	
	}
}
