package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.FileUtils;
import org.trident.world.entity.player.Player;

/**
 * This packet manages the input taken from chat box interfaces that allow input,
 * such as withdraw x, bank x, enter name of friend, etc.
 * 
 * @author relex lawl
 */

public class EnterInputPacketListener implements PacketListener {


	@Override
	public void execute(Player player, Packet packet) {
		switch (packet.getOpcode()) {
		case ENTER_SYNTAX_OPCODE:
			String name = FileUtils.readString(packet.getBuffer());
			if(name == null)
				return;
			if(player.getAttributes().getInputHandling() != null)
				player.getAttributes().getInputHandling().handleSyntax(player, name);
			player.getAttributes().setInputHandling(null);
			break;
		case ENTER_AMOUNT_OPCODE:
			int amount = packet.readInt();
			if(amount <= 0)
				return;
			if(player.getAttributes().getInputHandling() != null)
				player.getAttributes().getInputHandling().handleAmount(player, amount);
			player.getAttributes().setInputHandling(null);
			break;
		}
	}

	public static final int ENTER_AMOUNT_OPCODE = 208, ENTER_SYNTAX_OPCODE = 60;
}
