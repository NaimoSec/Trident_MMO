package org.trident.world.content.skills.impl.mining;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.world.entity.player.Player;

public class Prospecting {

	public static boolean prospectOre(final Player plr, int objectId) {
		final MiningData.Ores oreData = MiningData.forRock(objectId);
		if(oreData != null) {
			if(System.currentTimeMillis()- plr.getAttributes().getClickDelay() <= 3000)
				return true;
			plr.getSkillManager().stopSkilling();
			plr.getPacketSender().sendMessage("You examine the ore...");
			TaskManager.submit(new Task(2, plr, false) {
				@Override
				public void execute() {
					plr.getPacketSender().sendMessage("..the rock contains "+oreData.toString().toLowerCase()+" ore.");
					this.stop();
				}
			});
			plr.getAttributes().setClickDelay(System.currentTimeMillis());
			return true;
		}
		return false;
	}
}
