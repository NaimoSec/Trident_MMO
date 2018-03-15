package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.world.content.skills.impl.summoning.Summoning;
import org.trident.world.content.skills.impl.summoning.SummoningData;
import org.trident.world.entity.player.Player;

public class FamiliarDeathTimerTask extends Task {

	public FamiliarDeathTimerTask(Player player) {
		super(50, player, true);
		this.player = player;
	}
	
	final Player player;
	
	@Override
	public void execute() {
		if(player.getAdvancedSkills().getSummoning().getFamiliar() == null) {
			stop();
			return;
		}
		if(player.getAdvancedSkills().getSummoning().getFamiliar().getDeathTimer() >= 30) {
			player.getAdvancedSkills().getSummoning().getFamiliar().setDeathTimer(player.getAdvancedSkills().getSummoning().getFamiliar().getDeathTimer() - 30);
			if(player.getAdvancedSkills().getSummoning().getFamiliar().getDeathTimer() == 60)
				player.getPacketSender().sendMessage("@red@Warning: Your familiar will fade away in one minute.");
			player.getPacketSender().sendString(54043, ""+Summoning.getTimer(player.getAdvancedSkills().getSummoning().getFamiliar().getDeathTimer() >= 30 ? player.getAdvancedSkills().getSummoning().getFamiliar().getDeathTimer() : 30));
			player.getPacketSender().sendString(54024, ""+SummoningData.calculateScrolls(player));
		} else {
			player.getAdvancedSkills().getSummoning().resetFollower(true);
			stop();
		}
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
		player.getAdvancedSkills().getSummoning().summoningEvent = false;
	}
	
	public static void start(Player player) {
		if(player.getAdvancedSkills().getSummoning().getFamiliar() == null || player.getAdvancedSkills().getSummoning().summoningEvent)
			return;
		player.getAdvancedSkills().getSummoning().summoningEvent = true;
		TaskManager.submit(new FamiliarDeathTimerTask(player));
	}

}
