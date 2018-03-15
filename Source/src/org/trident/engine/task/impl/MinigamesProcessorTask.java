package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.world.content.minigames.impl.FightPit;
import org.trident.world.content.minigames.impl.FishingTrawler;
import org.trident.world.content.minigames.impl.PestControl;

public class MinigamesProcessorTask extends Task {

	public MinigamesProcessorTask() {
		super(1);
	}

	@Override
	protected void execute() {
		FightPit.process();
		PestControl.process();
		FishingTrawler.process();
	}
}
