package org.trident.net.packet.impl;

import org.trident.model.PlayerRelations.PrivateChatStatus;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.entity.player.Player;

public class ChangeRelationStatusPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		int actionId = packet.readInt();
		player.getRelations().setStatus(PrivateChatStatus.forActionId(actionId), true);
	}

}
