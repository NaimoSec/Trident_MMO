package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.entity.player.Player;

public class ChangeVolumePacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		int volume = packet.readInt();
		player.getAttributes().setVolume(volume);
	}

}
