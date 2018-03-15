package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.content.skills.impl.firemaking.Firemaking;
import org.trident.world.content.skills.impl.firemaking.Logdata;
import org.trident.world.content.skills.impl.firemaking.Logdata.logData;
import org.trident.world.entity.player.Player;

public class EnterAmountOfLogsToAdd extends EnterAmount {

	
	@Override
	public void handleAmount(Player player, int amount) {
		Firemaking.lightFire(player, -1, true, amount);
		if(player.getAttributes().getCurrentInteractingObject() != null)
			player.setPositionToFace(player.getAttributes().getCurrentInteractingObject().getPosition());
	}
	
	public static void openInterface(Player player) {
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().stopSkilling();
		final logData lData = Logdata.getLogData(player, -1);
		if(lData == null) {
			player.getPacketSender().sendMessage("You do not have any logs to add to this fire.");
			return;
		}
		player.getAttributes().setInputHandling(new EnterAmountOfLogsToAdd());
		player.getPacketSender().sendEnterAmountPrompt("How many logs would you like to add to the fire?");
	}

}
