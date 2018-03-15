package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.Constants;
import org.trident.world.entity.player.Player;

public class CloseInterfacePacketListener implements PacketListener {
	
	@Override
	public void execute(Player player, Packet packet) {
		player.getPacketSender().sendClientRightClickRemoval();
		player.getPacketSender().sendInterfaceRemoval();
		player.getPacketSender().sendTabInterface(Constants.CLAN_CHAT_TAB, 29328); //Clan chat tab
		player.getAttributes().setSkillGuideInterfaceData(null);
	}
}
