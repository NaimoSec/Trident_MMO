package org.trident.world.entity.grounditem;

import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.engine.task.impl.GroundItemsTask;
import org.trident.model.GroundItem;
import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.definitions.ItemDefinition;
import org.trident.util.Logger;
import org.trident.world.World;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.minigames.impl.Barrows;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;

/**
 * The Grounditems handler, redone for more optimization.
 * Did this quickly, should use some sort of ConcurrentLinkedQueue to handle it instead of a list.
 * @author Gabbe
 */
public class GroundItemManager {

	/*
	 * Our list which holds all grounditems, used an CopyOnWriteArrayList to prevent modification issues
	 * TODO: Change into a queue of some sort
	 */
	private static CopyOnWriteArrayList<GroundItem> groundItems = new CopyOnWriteArrayList<GroundItem>();

	/**
	 * Removes a grounditem from the world
	 * @param groundItem	The grounditem to remove from the world.
	 * @param delistGItem	Should the grounditem be deleted from the arraylist aswell?
	 */
	public static void remove(GroundItem groundItem, boolean delistGItem) {
		if(groundItem != null) {
			if(groundItem.isGlobal()) {
				for (Player p : World.getPlayers()) {
					if(p == null)
						continue;
					if(p.getPosition().distanceToPoint(groundItem.getPosition().getX(), groundItem.getPosition().getY()) <= 120)
						p.getPacketSender().removeGroundItem(groundItem.getItem().getId(), groundItem.getPosition().getX(), groundItem.getPosition().getY(), groundItem.getItem().getAmount());
				}
			} else {
				Player person = PlayerHandler.getPlayerForName(groundItem.getOwner());
				if(person != null  && person.getPosition().distanceToPoint(groundItem.getPosition().getX(), groundItem.getPosition().getY()) <= 120)
					person.getPacketSender().removeGroundItem(groundItem.getItem().getId(), groundItem.getPosition().getX(), groundItem.getPosition().getY(), groundItem.getItem().getAmount());
			}
			if(delistGItem)
				groundItems.remove(groundItem);
		}
	}

	/**
	 * This method spawns a grounditem for a player.
	 * @param p		The owner of the grounditem
	 * @param g		The grounditem to spawn
	 */
	public static void spawnGroundItem(Player p, GroundItem g) {
		if(p == null || p.getRights() == PlayerRights.ADMINISTRATOR || p.getRights() == PlayerRights.OWNER || p.getRights() == PlayerRights.DEVELOPER && !p.getUsername().equalsIgnoreCase("Gabbe"))
			return;
		final Item item = g.getItem();
		if(item.getDefinition().getName().toLowerCase().contains("clue scroll"))
			return;
		if(p.getLocation() == Location.NOMAD || Dungeoneering.doingDungeoneering(p) && item.getId() != 17489)
			return;
		if (item.getId() >= 2412 && item.getId() <= 2414) {
			p.getPacketSender().sendMessage("The cape vanishes as it touches the ground.");
			return;
		}
		if (item.getId() > 4705 && item.getId() < 4760) {
			for (int j = 0; j < Barrows.brokenBarrows.length; j++) {
				if (Barrows.brokenBarrows[j][0] == item.getId()) {
					item.setId(Barrows.brokenBarrows[j][1]);
					break;
				}
			}
		}
		if(ItemDefinition.forId(item.getId()).isStackable()) {
			GroundItem it = getGroundItem(p, item, g.getPosition());
			if(it != null) {
				it.getItem().setAmount(it.getItem().getAmount() + g.getItem().getAmount());
				if(it.getItem().getAmount() <= 0)
					remove(it, true);
				else
					it.setRefreshNeeded(true);
				return;
			}
		}
		if(p.getAttributes().getNewPlayerDelay() > 0) {
			g.setGlobalStatus(false);
			g.setGoGlobal(false);
		}
		add(g, true);
	}

	/**
	 * Adds a grounditem to the world
	 * @param groundItem	The grounditem to add to the world
	 * @param listGItem		Should the grounditem be added to the arraylist?
	 */
	public static void add(GroundItem groundItem, boolean listGItem) {
		if(groundItem.isGlobal()) {
			for (Player p : World.getPlayers()) {
				if(p == null)
					continue;
				if (groundItem.getPosition().getZ() == p.getPosition().getZ() && p.getPosition().distanceToPoint(groundItem.getPosition().getX(), groundItem.getPosition().getY()) <= 120)
					p.getPacketSender().createGroundItem(groundItem.getItem().getId(), groundItem.getPosition().getX(), groundItem.getPosition().getY(), groundItem.getItem().getAmount());
			}
		} else {
			Player person = PlayerHandler.getPlayerForName(groundItem.getOwner());
			if(person != null && groundItem.getPosition().getZ() == person.getPosition().getZ() && person.getPosition().distanceToPoint(groundItem.getPosition().getX(), groundItem.getPosition().getY()) <= 120)
				person.getPacketSender().createGroundItem(groundItem.getItem().getId(), groundItem.getPosition().getX(), groundItem.getPosition().getY(), groundItem.getItem().getAmount());
		}
		if(listGItem) {
			if(Location.getLocation(groundItem) == Location.DUNGEONEERING)
				groundItem.setShouldProcess(false);
			groundItems.add(groundItem);
			GroundItemsTask.fireTask();
		}
	}

	/**
	 * Handles the pick up of a grounditem
	 * @param p			The player picking up the item
	 * @param item
	 * @param position
	 */
	public static void pickupGroundItem(Player p, Item item, Position position) {
		if(System.currentTimeMillis() - p.getAttributes().getLastItemPickup() < 500)
			return;
		boolean canAddItem = p.getInventory().getFreeSlots() > 0 || item.getDefinition().isStackable() && p.getInventory().contains(item.getId());
		if(!canAddItem) {
			p.getInventory().full();
			return;
		}
		GroundItem gt = getGroundItem(p, item, position);
		if(gt == null || gt.hasBeenPickedUp() || !groundItems.contains(gt)) //last one isn't needed, but hey, just trying to be safe
			return;
		else {/*
			if(p.getHostAdress().equals(gt.getFromIP()) && !p.getUsername().equals(gt.getOwner())) { //Transferring items by IP..
				p.getPacketSender().sendMessage("An error occured.");
				return;
			}*/
			item = gt.getItem();
			gt.setPickedUp(true);
			remove(gt, true);
			p.getInventory().add(item);
			Logger.log(p.getUsername(), "Player picked up item: "+item.getDefinition().getName()+", noted: "+item.getDefinition().isNoted()+", amount: "+item.getAmount());
			SoundEffects.sendSoundEffect(p, SoundEffects.SoundData.PICKUP_ITEM, 10, 0);
			if(item.getId() == 17489) {
				if(!Dungeoneering.doingDungeoneering(p))
					return;
				p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().setGatestonePosition(null);
			}
			p.getAttributes().setLastItemPickup(System.currentTimeMillis());
		}
	}

	/**
	 * Handles a region change for a player.
	 * This method respawns all grounditems for a player who has changed region.
	 * @param p		The player who has changed region
	 */
	public static void handleRegionChange(Player p) {
		for(GroundItem gi : groundItems) {
			if(gi == null)
				continue;
			p.getPacketSender().removeGroundItem(gi.getItem().getId(), gi.getPosition().getX(), gi.getPosition().getY(), gi.getItem().getAmount());
		}
		for(GroundItem gi : groundItems) {
			if(gi == null || p.getPosition().getZ() != gi.getPosition().getZ() || p.getPosition().distanceToPoint(gi.getPosition().getX(), gi.getPosition().getY()) > 120)
				continue;
			if(gi.isGlobal() || !gi.isGlobal() && gi.getOwner().equals(p.getUsername()))
				p.getPacketSender().createGroundItem(gi.getItem().getId(), gi.getPosition().getX(), gi.getPosition().getY(), gi.getItem().getAmount());
		}
	}

	/**
	 * Checks if a grounditem exists in the stated position.
	 * @param p			The player trying to check if the grounditem exists
	 * @param item		The grounditem's item
	 * @param position	The position to check if a grounditem exists on
	 * @return			true if a grounditem exists in the specified position, otherwise false
	 */
	public static GroundItem getGroundItem(Player p, Item item, Position position) {
		for(GroundItem l : groundItems) {
			if(l == null || l.getPosition().getZ() != position.getZ())
				continue;
			if(l.getPosition().equals(position) && l.getItem().getId() == item.getId()) {
				if(l.isGlobal())
					return l;
				else if(p != null) {
					Player owner = PlayerHandler.getPlayerForName(l.getOwner());
					if(owner == null || owner.getIndex() != p.getIndex())
						continue;
					return l;
				}
			}
		}
		return null;
	}

	/**
	 * Clears a position of ground items
	 * @param pos		The position to remove all ground items on
	 * @param owner		The owner of the grounditems to remove
	 */
	public static void clearArea(Position pos, String owner) {
		for(GroundItem l : groundItems) {
			if(l == null || l.getPosition().getZ() != pos.getZ())
				continue;
			if(l.getPosition().equals(pos) && l.getOwner().equals(owner)) 
				remove(l, true);
		}
	}
	
	public static CopyOnWriteArrayList<GroundItem> getGroundItems() {
		return groundItems;
	}
}