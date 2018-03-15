package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.Input;
import org.trident.world.entity.player.Player;

public class SetEmail extends Input {
	
	@Override
	public void handleSyntax(Player player, String syntax) {
		if(syntax.length() <= 3 || !syntax.contains("@") || syntax.endsWith("@")) {
			player.getPacketSender().sendMessage("Invalid syntax, please enter a valid one.");
			return;
		}
		if(System.currentTimeMillis() - player.getAttributes().getSqlDelay() < 30000) {
			player.getPacketSender().sendMessage("Please wait another "+(int)((30 - (System.currentTimeMillis() - player.getAttributes().getSqlDelay()) * 0.001))+" seconds before doing this.");
			return;
		}
	//	if(AccountRecovery.saveEmail(player, syntax, player.getPassword(), false)) {
			player.setEmailAdress(syntax);
			player.getPacketSender().sendMessage("Your new email-adress is now: "+syntax);
		//}
	}
}
