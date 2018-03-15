package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.content.MoneyPouch;
import org.trident.world.entity.player.Player;

public class WithdrawMoneyFromPouchPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		int amount = packet.readInt();
		MoneyPouch.withdrawMoney(player, amount);
	}

}
