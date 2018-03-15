package org.trident.net.packet.impl;

import org.trident.model.PlayerRights;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.entity.player.Player;

public class LendItemActionPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		if(!player.getTrading().inTrade())
			return;
		int id = packet.readInt();
		if(id > 0 && id < 30000 && player.getRights() != PlayerRights.ADMINISTRATOR)
			player.getTrading().getItemLending().loanItemAction(id);
	}

}
