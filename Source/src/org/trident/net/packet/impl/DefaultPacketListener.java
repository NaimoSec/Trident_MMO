package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.entity.player.Player;

/**
 * This packet listener manages a non-set/nulled packets and logs out
 * their basic information. 
 * 
 * @author relex lawl
 */

public class DefaultPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		System.out.println("Unhandled packet: " + packet.getOpcode());
		System.out.println("Packet size: " + packet.getSize());
	}

}
