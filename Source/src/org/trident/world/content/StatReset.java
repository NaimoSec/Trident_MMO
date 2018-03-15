package org.trident.world.content;

import org.trident.world.entity.player.Player;

public class StatReset {

	public static void openInterface(Player p) {
		if(p.getAttributes().getInterfaceId() > 0) {
			p.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			return;
		}
		p.getAttributes().setLoyaltyProductSelected(new Object[2]);
		p.getPacketSender().sendString(38006, "Choose stat to reset!").sendMessage("@red@Please select a skill you wish to reset and then click on the 'Confim' button.");
		p.getPacketSender().sendInterface(38000);
	}
}
