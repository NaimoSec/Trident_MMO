package org.trident.net.packet;

import org.trident.world.entity.player.Player;

/**
 * Represents a Packet received from client.
 * 
 * @author relex lawl
 */

public interface PacketListener {
	
	/**
	 * Executes the packet.
	 * @param player	The player to which execute the packet for.
	 * @param packet	The packet being executed.
	 */
	public void execute(Player player, Packet packet);
}
