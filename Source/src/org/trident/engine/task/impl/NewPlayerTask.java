package org.trident.engine.task.impl;


import org.trident.engine.task.Task;
import org.trident.world.entity.player.Player;

/**
 * Familiar spawn on login
 * @author Gabbe
 */
public class NewPlayerTask extends Task {

	public NewPlayerTask(Player player) {
		super(1, player, true);
		this.player = player;
	}

	private Player player;

	@Override
	public void execute() {
		player.getAttributes().setNewPlayerDelay(player.getAttributes().getNewPlayerDelay() - 1);
		if(player.getAttributes().getNewPlayerDelay() <= 0) {
			player.getPacketSender().sendMessage("You are now able to trade, stake and Pk.");
			player.getAttributes().setNewPlayerDelay(-1);
			stop();
		}
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
	}

}
