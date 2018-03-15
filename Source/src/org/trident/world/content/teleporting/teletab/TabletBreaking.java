package org.trident.world.content.teleporting.teletab;

import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.content.teleporting.TeleportType;
import org.trident.world.entity.player.Player;


/**
 * The use of teleport tabs for quick access to other map areas.
 * @author Jacob Ford
 *
 */
public class TabletBreaking {
	
	/**
	 * Checks if <code>item</code> is a valid tele tablet.
	 * @param player The player.
	 * @param item The item.
	 * @param slot The slot.
	 * @return If <code>true</code> player will proceed to to teleport.
	 */
	public static boolean isTab(Player player, Item item, int slot) {
		TabletData tab = TabletData.forId(item.getId());
		if (tab != null) {
			execute(player, tab, slot);
			return true;
		}
		return false;
	}

	/**
	 * Activating the teleport tab.
	 * @param player The player.
	 * @param item The tab.
	 * @param slot The item slot.
	 * @return 
	 */
	private static void execute(final Player player, final TabletData tab, int slot) {
		if (tab != null) {
			if(System.currentTimeMillis() - player.getAttributes().getClickDelay() > 5000) { // Don't need to set it, it's set in the tele method.
				player.getInventory().delete(new Item(tab.getTabletId(), 1));
				player.getPacketSender().sendMessage("You break the teleport tab.");
				Position tabTele = new Position(tab.getPostionX(), tab.getPostionY(), 0);
				TeleportHandler.teleportPlayer(player, tabTele, TeleportType.TELE_TAB);
			}
		}
	}
}