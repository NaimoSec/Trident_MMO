package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.world.entity.player.Player;

public class PoisonImmunityTask extends Task {

	public PoisonImmunityTask(final Player p) {
		super(1, p, false);
		this.p = p;
	}
	
	final Player p;

	@Override
	public void execute() {
		if(p == null) {
			stop();
			return;
		}
		int currentImmunity = p.getCombatAttributes().getPoisonImmunity();
		if(currentImmunity > 0) {
			p.getCombatAttributes().setPoisonImmunity(currentImmunity-1);
		} else {
			p.getCombatAttributes().setPoisonImmunity(0);
			p.getPacketSender().sendMessage("You are no longer immune to poison.");
			stop();
		}
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
	}
	
	public static void makeImmune(final Player p, int seconds) {
		int currentImmunity = p.getCombatAttributes().getPoisonImmunity();
		boolean startEvent = currentImmunity == 0;
		p.getCombatAttributes().setPoisonImmunity(currentImmunity+seconds);
		if(!startEvent)
			return;
		TaskManager.submit(new PoisonImmunityTask(p));
	}
}
