package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.Input;
import org.trident.util.Misc;
import org.trident.world.entity.player.Player;

public class EnterAuth extends Input {

	@Override
	public void handleSyntax(Player player, String syntax) {
		if(syntax.length() < 2) {
			player.getPacketSender().sendMessage("Invalid syntax entered.");
			return;
		}
		if(System.currentTimeMillis() - player.getAttributes().getLastVote() < Misc.HALF_A_DAY_IN_MILLIS) {
			player.getPacketSender().sendMessage("You have already voted in the last 12 hours. Try again later.");
			return;
		}
		if(player.getInventory().getFreeSlots() == 0) {
			player.getPacketSender().sendMessage("Please make sure you have some free inventory space before doing this.");
			return;
		}/*
		Voting.getInstance().handleAuth(player, syntax.toUpperCase());*/
	}
}
