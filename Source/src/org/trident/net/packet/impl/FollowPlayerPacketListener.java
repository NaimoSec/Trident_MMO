package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.World;
import org.trident.world.entity.player.Player;
/**
 * Handles the follow player packet listener
 * Sets the player to follow when the packet is executed
 * @author Gabbe 
 */
public class FollowPlayerPacketListener implements PacketListener {


	@Override
	public void execute(Player player, Packet packet) {
		if(player.getConstitution() <= 0)
			return;
		int otherPlayersIndex = packet.readLEShort();
		if(otherPlayersIndex > World.getPlayers().size())
			return;
		Player leader =	World.getPlayers().get(otherPlayersIndex);
		if(leader == null)
			return;
		if(leader.getConstitution() <= 0 || player.getConstitution() <= 0 || !player.getLocation().isFollowingAllowed()) {
			player.getPacketSender().sendMessage("You cannot follow other players right now.");
			return;
		}
		player.setEntityInteraction(leader);
		player.followCharacter(leader);
	}

}
