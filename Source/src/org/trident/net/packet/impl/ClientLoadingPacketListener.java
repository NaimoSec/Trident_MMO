package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.entity.player.Player;

public class ClientLoadingPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		boolean isLoading = packet.readInt() == 1;
		player.getAttributes().setClientIsLoading(isLoading);
	}

}
