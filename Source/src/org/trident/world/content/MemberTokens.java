package org.trident.world.content;

import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.world.entity.player.Player;

public class MemberTokens {

	/*
	 * The tokens
	 */
	private static final int[] tokens = {10942, 10934, 10935, 10943, 10944};
	
	/*
	 * Checks if a player is to be given a member rank for using a token
	 */
	public static boolean handleToken(Player player, Item item) {
		for(int i = 0; i < tokens.length; i++) {
			if(item.getId() == tokens[i] && player.getInventory().contains(tokens[i])) {
				player.getInventory().delete(tokens[i], 1);
				player.setRights(PlayerRights.forId(5+i));
				player.getPacketSender().sendRights();
				PlayerPanel.refreshPanel(player);
				player.getPacketSender().sendMessage("Your player rights have been changed!");
				return true;
			}
		}
		return false;
	}
}
