package org.trident.world.content;

import org.trident.model.Item;
import org.trident.util.Logger;
import org.trident.world.entity.player.Player;

/**
 * Sells Artifacts, dropped by Players in the Wilderness
 * @author Gabbe
 *
 */
public class Artifacts {

	public static int artifacts[] = {14876, 14877, 14878, 14879, 14880, 14881, 14882, 14883, 14884, 14885, 14886, 14887, 14888, 14889, 14890, 14891, 14892};
	
	public static void sellArtifacts(Player c) {
		c.getPacketSender().sendInterfaceRemoval();
		boolean artifact = false;
		for(int k = 0; k < artifacts.length; k++) {
			if(c.getInventory().contains(artifacts[k])) {
				artifact = true;
			}
		}
		if(!artifact) {
			c.getPacketSender().sendMessage("You do not have any Artifacts in your inventory to sell to Mandrith.");
			return;
		}
		for(int i = 0; i < artifacts.length; i++) {
			if(c.getInventory().contains(artifacts[i])) {
				if(c.getInventory().getFreeSlots() >= 1) {
					Item item = new Item(artifacts[i]);
					c.getInventory().delete(artifacts[i], 1);
					c.getInventory().add(995, item.getDefinition().getValue());
					c.getInventory().refreshItems();
					Logger.log(c.getUsername(), "Player sold artifact "+item.getDefinition().getName()+" to Mandrith for: "+item.getDefinition().getValue());
				} else
					break;
			}
		}
		
	}
	
}
