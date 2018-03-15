package org.trident.world.content;

import java.util.ArrayList;

import org.trident.model.Item;
import org.trident.util.Misc;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.player.Player;

/**
 * Handles items kept on death.
 * @author Gabbe
 */
public class ItemsKeptOnDeath {

	/**
	 * Sends the items kept on death interface for a player.
	 * @param player	Player to send the items kept on death interface for.
	 */
	public static void sendInterface(Player player) {
		clearInterfaceData(player); //To prevent sending multiple layers of items.
		sendInterfaceData(player); //Send info on the interface.
		player.getPacketSender().sendInterface(17100); //Open the interface.
	}

	/**
	 * Sends the items kept on death data for a player.
	 * @param player	Player to send the items kept on death data for.
	 */
	public static void sendInterfaceData(Player player) {
		ArrayList<Item> toKeep = getItemsToKeep(player);
		/**
		 * Sends text info
		 */
		String[] infoToFill = new String[6];
		switch(toKeep.size()) {
		case 0: //Player must be skulled WITHOUT the protect item prayer/curse active, player will keep 0 items.
			infoToFill[0] = "You are skulled and will"; infoToFill[1] = "not keep any items on"; infoToFill[2] = "death.";
			break;
		case 1: //Player must be skulled but have the protect item prayer/curse active, player will keep 1 item.
		case 4: //Player is not skulled & has protect item prayer/curse active, player will keep 4 items. Send this info.
			infoToFill[0] = "You have the Protect Item"; infoToFill[1] = "prayer/curse active."; infoToFill[2] = "Therefore`, you will keep"; infoToFill[3] = "an extra item on death.";
			break;
		case 2: //No such case where a player can keep 2 items.
			break;
		case 3: // Player has no factors active, size of array is 3 and player will keep 3 items.
			infoToFill[0] = "You will keep 3 items"; infoToFill[1] = "on death since you"; infoToFill[2] = "have no factors active.";
			break;
		}
		infoToFill[4] = "@red@All untradeable items"; infoToFill[5] = "@red@are automatically kept.";
		for(int i = 0; i < infoToFill.length; i++)
			if(infoToFill[i] == null)
				player.getPacketSender().sendString(17143 + i, "");
			else
				player.getPacketSender().sendString(17143 + i, infoToFill[i]);
		player.getPacketSender().sendMessage("@red@Note: All untradeable items are automatically kept on death!!!");
		for(int i = 0; i < toKeep.size(); i++) {
			if(toKeep.get(i) != null && toKeep.get(i).tradeable())
				player.getPacketSender().sendItemOnInterface(17108+i, toKeep.get(i).getId(), toKeep.get(i).getAmount());
		}
		int toSend = 17112;
		for(Item item : Misc.combine(player.getInventory().getItems(), player.getEquipment().getItems())) {
			if(item != null && item.getId() > 0 && item.tradeable() && !toKeep.contains(item)) {
				if(toSend >= 17142)
					toSend = 17150;
				if(toSend >= 17160)
					break;
				player.getPacketSender().sendItemOnInterface(toSend, item.getId(), item.getAmount());
				toSend++;
			}
		}
		infoToFill = null;
	}

	/**
	 * Clears the items kept on death interface for a player.
	 * @param player	Player to clear the items kept on death interface for.
	 */
	public static void clearInterfaceData(Player player) {
		player.getPacketSender().sendString(17107, "");
		Item noItem = new Item(-1);
		for(int i = 17108; i <= 17142; i++)
			player.getPacketSender().sendItemOnInterface(i, noItem.getId(), noItem.getAmount());
		for(int i = 17150; i <= 17160; i++)
			player.getPacketSender().sendItemOnInterface(i, noItem.getId(), noItem.getAmount());
		noItem = null;
	}

	/**
	 * Sets the items to keep on death for a player.
	 * @param player	Player to set items for.
	 */
	public static ArrayList<Item> getItemsToKeep(Player player) {
		Item[] combined = Misc.combine(player.getInventory().getItems(), player.getEquipment().getItems());
		combined = sortItemsByValue(combined);
		boolean keepExtraItem = player.getPrayerActive()[PrayerHandler.PROTECT_ITEM] || player.getCurseActive()[CurseHandler.PROTECT_ITEM];
		int size = (player.getPlayerCombatAttributes().isSkulled() ? 0 : 3) + (keepExtraItem ? 1 : 0);
		ArrayList<Item> toKeep = new ArrayList<Item>();
		for (int i = 0; i < size && i < combined.length; i++) {
			if(combined[i] != null)
				toKeep.add(combined[i]);
		}
		return toKeep;
	}

	/**
	 * Organizes an Item array from greatest to least in value
	 * @param netItems	The Item array to organize
	 * @return	returns the given array in organized format
	 */
	private static Item[] sortItemsByValue(Item[] netItems) {
		int tripCounter = 0;
		do {
			Item previous = null;
			tripCounter = 0;
			for(Item currentItem: netItems) {
				if(previous == null) {
					previous = currentItem;
				} else {
					if(currentItem.tradeable() && (currentItem.getDefinition().getValue() * currentItem.getAmount()) > (previous.getDefinition().getValue() * previous.getAmount())) {
						int[] indexes = {findIndex(netItems, currentItem), findIndex(netItems, previous)};
						netItems[indexes[1]] = currentItem;
						netItems[indexes[0]] = previous;
						tripCounter++;
					}
					previous = currentItem;
				}
			}
		} while (tripCounter > 0);
		return netItems;
	}

	/**
	 * Searches the given Item array for a specific Item object in it and returns its index
	 * @param netItems	The Item array that is to be searched
	 * @param toFind	The Item object to look for in the array
	 * @return	Returns the index of the Item object in the Item array or -1 if it is not found
	 */
	private static int findIndex(Item[] netItems, Item toFind) {
		for(int i = 0; i < netItems.length; i ++) {
			if(netItems[i] == toFind)
				return i;
		}
		return -1;
	}

}
