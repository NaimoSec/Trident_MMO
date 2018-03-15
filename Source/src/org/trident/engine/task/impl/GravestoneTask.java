package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.world.World;
import org.trident.world.content.Gravestones;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.player.Player;

public class GravestoneTask extends Task {

	public GravestoneTask(Player player) {
		super(2, player, false);
		this.player = player;
	}

	private Player player;

	@Override
	public void execute() {
		if(player.getAttributes().getGravestoneAttributes().getMinutes() < 0) {
			stop();
			return;
		}
		player.getAttributes().getGravestoneAttributes().setSeconds(player.getAttributes().getGravestoneAttributes().getSeconds() - 1);
		if(player.getAttributes().getWalkableInterfaceId() != 37400)
			player.getPacketSender().sendWalkableInterface(37400);
		player.getPacketSender().sendString(37402, Gravestones.formatText(player.getAttributes().getGravestoneAttributes().getMinutes(), player.getAttributes().getGravestoneAttributes().getSeconds()));
		if (player.getAttributes().getGravestoneAttributes().getSeconds() == 0 && player.getAttributes().getGravestoneAttributes().getMinutes() >= 0) {
			player.getAttributes().getGravestoneAttributes().setMinutes(player.getAttributes().getGravestoneAttributes().getMinutes() - 1);
			player.getAttributes().getGravestoneAttributes().setSeconds(60);
		}
	}

	@Override
	public void stop() {
		setEventRunning(false);
		if(player.getAttributes().getGravestoneAttributes().getGravestone() != null) {
			GroundItemManager.clearArea(player.getAttributes().getGravestoneAttributes().getGravestone().getPosition().copy(), player.getUsername());
			World.deregister(player.getAttributes().getGravestoneAttributes().getGravestone());
			player.getAttributes().getGravestoneAttributes().setGravestone(null);
			player.getPacketSender().sendMessage("@red@Your gravestone has crumbled and all items beneath it have vanished..");
		}
		player.getAttributes().getGravestoneAttributes().setMinutes(0);
		player.getAttributes().getGravestoneAttributes().setSeconds(0);
		player.getPacketSender().sendWalkableInterface(-1);
	}

}
