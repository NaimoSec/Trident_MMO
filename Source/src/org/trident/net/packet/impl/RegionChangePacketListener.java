package org.trident.net.packet.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.RegionInstance.RegionInstanceType;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.clip.region.RegionClipping;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.content.skills.impl.hunter.Hunter;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCData;
import org.trident.world.entity.player.Player;

/**
 * This packet listener is called when a player is entering a new region.
 * 
 * @author relex lawl
 */

public class RegionChangePacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		changeRegion(player);
	}

	public static void changeRegion(final Player player) {
		RegionClipping.loadRegion(player.getPosition().getX(), player.getPosition().getY());
		CustomObjects.handleRegionChange(player);
		GroundItemManager.handleRegionChange(player); 
		Hunter.handleRegionChange(player);
		player.getPacketSender().sendMapRegion();
		if(player.getAttributes().isChangingRegion()) {
			if(!Dungeoneering.doingDungeoneering(player)) {
				if(player.getAttributes().getRegionInstance() != null && player.getPosition().getX() != 1 && player.getPosition().getY() != 1) {
					if(player.getAttributes().getRegionInstance().equals(RegionInstanceType.BARROWS) || player.getAttributes().getRegionInstance().equals(RegionInstanceType.WARRIORS_GUILD))
						player.getAttributes().getRegionInstance().destruct();
				}
				TaskManager.submit(new Task(1, player, false) {
					@Override
					public void execute() {
						for(NPC n : player.getAttributes().getLocalNpcs()) {
							if(n == null || n.getAttributes().isAttackable() || n.getAttributes().getWalkingDistance() > 0)
								continue;
							n.setDirection(NPCData.getDirectionToFace(n));
						}
						stop();
					}
				});
			}
		}

	}
}
