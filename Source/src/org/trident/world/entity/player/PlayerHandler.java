package org.trident.world.entity.player;

import org.trident.world.World;
import org.trident.world.content.clan.ClanChatManager;

public class PlayerHandler {

	/**
	 * Gets the player according to said name.
	 * @param name	The name of the player to search for.
	 * @return		The player who has the same name as said param.
	 */
	public static Player getPlayerForName(String name) {
		for (Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if (player.getUsername().equalsIgnoreCase(name))
				return player;
		}
		return null;
	}

	/**
	 * Gets the player according to said encoded name.
	 * @param name	The name in long of the player to search for.
	 * @return		The player who has the same name as said param.
	 */
	public static Player getPlayerForNameLong(long encodedName) {
		for (Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if (player.getLongUsername().equals(encodedName))
				return player;
		}
		return null;
	}

	/**
	 * Sends a global message to every player online
	 * @param message		The message to be sent
	 */
	public static void sendGlobalPlayerMessage(String message) {
		for(Player players : World.getPlayers()) {
			if(players == null)
				continue;
			players.getPacketSender().sendMessage(message);
		}
	}

	public static void saveAll() {
		for (Player player : World.getPlayers()) {
			if(player == null)
				continue;
			PlayerSaving.save(player);
		}
		ClanChatManager.save();
	}
}
