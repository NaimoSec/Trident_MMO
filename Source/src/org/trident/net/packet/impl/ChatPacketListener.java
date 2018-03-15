package org.trident.net.packet.impl;

import org.trident.model.Flag;
import org.trident.model.ChatMessage.Message;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.content.PlayerPunishment;
import org.trident.world.entity.player.Player;

/**
 * This packet listener manages the spoken text by a player.
 * 
 * @author relex lawl
 */

public class ChatPacketListener implements PacketListener {
	
	@Override
	public void execute(Player player, Packet packet) {
		int effects = packet.readUnsignedByteS();
		int color = packet.readUnsignedByteS();
		int size = packet.getSize();
		byte[] text = packet.readBytesA(size);
		//String term = Misc.textUnpack(text, size).toLowerCase();
		if(PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAdress())) {
			player.getPacketSender().sendMessage("You are muted and cannot chat.");
			return;
		}
		player.getChatMessages().set(new Message(color, effects, text));
		player.getUpdateFlag().flag(Flag.CHAT);
	}
}
