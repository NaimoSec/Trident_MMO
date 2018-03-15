package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.FileUtils;
import org.trident.world.content.Locations.Location;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;

public class DungeoneeringPartyInvitatationPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		String plrToInvite = FileUtils.readString(packet.getBuffer());
		if(plrToInvite == null || plrToInvite.length() <= 0)
			return;
		if(player.getLocation() == Location.DAEMONHEIM) {
			if(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor() == null || player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty() == null || player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty().getOwner() == null)
				return;
			player.getPacketSender().sendInterfaceRemoval();
			if(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty().getOwner() != player) {
				player.getPacketSender().sendMessage("Only the party leader can invite other players.");
				return;
			}
			Player invite = PlayerHandler.getPlayerForName(plrToInvite);
			if(invite == null) {
				player.getPacketSender().sendMessage("That player is currently not online.");
				return;
			}
			player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty().invite(invite);
		}
	}
}
