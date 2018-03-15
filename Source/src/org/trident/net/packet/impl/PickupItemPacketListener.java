package org.trident.net.packet.impl;

import org.trident.engine.task.impl.FinalizedMovementTask;
import org.trident.engine.task.impl.WalkToAction;
import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.model.definitions.ItemDefinition;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.player.Player;

/**
 * This packet listener is used to pick up ground items
 * that exist in the world.
 * 
 * @author relex lawl
 */

public class PickupItemPacketListener implements PacketListener {

	@Override
	public void execute(final Player player, Packet packet) {
		final int y = packet.readLEShort();
		final int itemId = packet.readShort();
		final int x = packet.readLEShort();
		if(player.isTeleporting())
			return;
		final Position position = new Position(x, y, player.getPosition().getZ());
		if(System.currentTimeMillis()- player.getAttributes().getLastItemPickup() < 500)
			return;
		if(player.getConstitution() <= 0 || player.isTeleporting())
			return;
		player.getAttributes().setWalkToTask(new WalkToAction(player, position, new FinalizedMovementTask() {
			@Override
			public void execute() {
				if (Math.abs(player.getPosition().getX() - x) > 25 || Math.abs(player.getPosition().getY() - y) > 25) {
					player.getMovementQueue().stopMovement();
					return;
				}
				boolean canPickup = player.getInventory().getFreeSlots() > 0 || (player.getInventory().getFreeSlots() == 0 && ItemDefinition.forId(itemId).isStackable() && player.getInventory().contains(itemId));
				if(!canPickup) {
					player.getInventory().full();
					return;
				}
				//if(player.getPosition().copy().equals(position))
				if(GroundItemManager.getGroundItem(player, new Item(itemId), position) != null)
					GroundItemManager.pickupGroundItem(player, new Item(itemId), new Position(x, y, player.getPosition().getZ()));
			}
		}));
	}
}
