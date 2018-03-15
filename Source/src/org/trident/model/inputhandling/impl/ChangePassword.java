package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.Input;
import org.trident.util.NameUtils;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerSaving;

public class ChangePassword extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		if(syntax == null || syntax.length() <= 2 || syntax.length() > 15 || !NameUtils.isValidName(syntax)) {
			player.getPacketSender().sendMessage("That password is invalid. Please try another password.");
			return;
		}
		if(syntax.contains("_")) {
			player.getPacketSender().sendMessage("Your password can not contain underscores.");
			return;
		}
		if(System.currentTimeMillis() - player.getAttributes().getSqlDelay() < 30000) {
			player.getPacketSender().sendMessage("Please wait another "+(int)((30 - (System.currentTimeMillis() - player.getAttributes().getSqlDelay()) * 0.001))+" seconds before doing this.");
			return;
		}
		player.getAttributes().setSqlDelay(System.currentTimeMillis());
		//if(AccountRecovery.saveEmail(player, player.getEmailAdress(), syntax, true)) {
			player.setPassword(syntax);
			PlayerSaving.save(player);
			player.getPacketSender().sendMessage("You have changed your password to: "+syntax);
		//}
	}
}
