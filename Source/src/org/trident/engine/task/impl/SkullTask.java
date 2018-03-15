package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.entity.player.Player;

public class SkullTask extends Task {

	public SkullTask(Player p) {
		super(200, p, false);
		this.p = p;
	}
	
	private Player p;

	@Override
	public void execute() {
		if(p.getPlayerCombatAttributes().isSkulled())
			CombatHandler.skull(p, false);
		stop();
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
	}
}
