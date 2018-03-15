package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.Misc;
import org.trident.util.NameUtils;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;

/**
 * This packet listener is called when a player is doing something relative
 * to their friends or ignore list, such as adding or deleting a player from said list.
 * 
 * @author relex lawl
 */

public class PlayerRelationPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		try {
			long username = packet.readLong();
			switch (packet.getOpcode()) {
			case ADD_FRIEND_OPCODE:
				player.getRelations().addFriend(username);
				break;
			case ADD_IGNORE_OPCODE:
				player.getRelations().addIgnore(username);
				break;
			case REMOVE_FRIEND_OPCODE:
				player.getRelations().deleteFriend(username);
				break;
			case REMOVE_IGNORE_OPCODE:
				player.getRelations().deleteIgnore(username);
				break;
			case SEND_PM_OPCODE:
				Player friend = PlayerHandler.getPlayerForName(Misc.formatText(NameUtils.longToString(username)).replaceAll("_", " "));
				int size = packet.getSize();
				byte[] message = packet.readBytes(size);
				player.getRelations().message(friend, message, size);
				break;
			}
		} catch (IndexOutOfBoundsException e) {

		}
	}

	public static final int ADD_FRIEND_OPCODE = 188;

	public static final int REMOVE_FRIEND_OPCODE = 215;

	public static final int ADD_IGNORE_OPCODE = 133;

	public static final int REMOVE_IGNORE_OPCODE = 74;

	public static final int SEND_PM_OPCODE = 126;
}
