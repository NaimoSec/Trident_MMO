package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.FileUtils;
import org.trident.world.content.PlayerPunishment;
import org.trident.world.content.clan.ClanChatManager;
import org.trident.world.entity.player.Player;

public class SendClanChatMessagePacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		String clanMessage = FileUtils.readString(packet.getBuffer());
		if(clanMessage == null || clanMessage.length() < 1)
			return;
		if(PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAdress())) {
			player.getPacketSender().sendMessage("You are muted and cannot chat.");
			return;
		}
		ClanChatManager.sendMessage(player, clanMessage);
	}

}
