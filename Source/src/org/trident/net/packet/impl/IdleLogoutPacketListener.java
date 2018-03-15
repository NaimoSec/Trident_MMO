package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.entity.player.Player;

/**
 * This packet listener is called when a player is not active in-game
 * for a certain period of time.
 * 
 * @author relex lawl
 */

public class IdleLogoutPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		/*if (player.getConstitution() <= 0 || player.getMovementQueue().isMoving() || CombatHandler.inCombat(player)) {
			return;
		}
		player.getAttributes().setInactiveTimer(player.getAttributes().getInactiveTimer() + 1);
		if(player.getAttributes().getInactiveTimer() >= 20)
			World.deregister(player);*/
	}

}
