package org.trident.util;

import java.util.logging.Logger;

import org.trident.world.World;
import org.trident.world.content.clan.ClanChatManager;
import org.trident.world.entity.player.Player;

/**
 * This file manages the actions that should be taken
 * upon exiting or terminating the server application.
 * 
 * @author relex lawl
 */

public class ShutdownHook extends Thread {

	/**
	 * The ShutdownHook logger to print out information.
	 */
	private static final Logger logger = Logger.getLogger(ShutdownHook.class.getName());

	@Override
	public void run() {
		logger.info("The shutdown hook is processing all required actions...");
		for (Player player : World.getPlayers()) {
			if(player == null)
				continue;
		//	player.getSession().disconnect();
		}
		ClanChatManager.save();
		logger.info("The shudown hook actions have been completed, shutting the server down...");
	}
}
