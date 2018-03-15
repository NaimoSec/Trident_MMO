package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.World;
import org.trident.world.content.minigames.impl.FightPit;
import org.trident.world.entity.player.Player;

/**
 * This packet listener is called when a player accepts a trade offer,
 * whether it's from the chat box or through the trading player menu.
 * 
 * @author relex lawl
 */

public class TradeAcceptancePacketListener implements PacketListener {
	
	@Override
	public void execute(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		if(player.isTeleporting())
			return;
		if(FightPit.inFightPits(player)) {
			player.getPacketSender().sendMessage("You cannot trade other players here.");
			return;
		}
		int index = packet.getOpcode() == TRADE_OPCODE ? (packet.readShort() & 0xFF) : packet.readLEShort();
		if(index < 0 || index > World.getPlayers().size())
			return;
		Player target = World.getPlayers().get(index);
		if (target == null) 
			return;
		if(target.getIndex() != player.getIndex())
			player.getTrading().requestTrade(target);
	}

	public static final int TRADE_OPCODE = 39;
	public static final int CHATBOX_TRADE_OPCODE = 139;
}
