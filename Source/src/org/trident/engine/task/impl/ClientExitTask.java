package org.trident.engine.task.impl;


import org.trident.engine.task.Task;
import org.trident.world.World;
import org.trident.world.entity.player.Player;

/**
 * Prevents xlogging 
 * @author Gabbe
 */
public class ClientExitTask extends Task {

	public ClientExitTask(Player player) {
		super(10, true);
		this.player = player;
	}

	int attempts = 0;
	private Player player;

	@Override
	public void execute() {
		if(player == null || attempts == 8|| player.logout()) {
			stop();
			return;
		}
		attempts++;
	}

	@Override
	public void stop() {
		setEventRunning(false);
		player.getAttributes().setForceLogout(true);
		World.deregister(player);
	}

}
