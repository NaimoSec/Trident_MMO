package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.World;
import org.trident.world.entity.player.Player;

public class DuelAcceptancePacketListener implements PacketListener {
	
	@Override
	public void execute(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		int index = packet.getOpcode() == 128 ? (packet.readShort() & 0xFF) : packet.readLEShort();
		if(index > World.getPlayers().size())
			return;
		Player target = World.getPlayers().get(index);
		if (target == null) 
			return;
		if(target.getIndex() != player.getIndex())
			player.getDueling().challengePlayer(target);
	}
	
	public static final int OPCODE = 128;
}
