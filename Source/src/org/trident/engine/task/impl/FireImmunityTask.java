package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.world.entity.player.Player;

public class FireImmunityTask extends Task {

	public FireImmunityTask(final Player p) {
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
		if(p.getAttributes().getFireImmunity() > 0) {
			p.getAttributes().setFireImmunity(p.getAttributes().getFireImmunity()- 1);
			if(p.getAttributes().getFireImmunity() == 20) 
				p.getPacketSender().sendMessage("Your resistance to dragonfire is about to run out.");
		} else {
			p.getAttributes().setFireImmunity(0).setFireDamageModifier(0).setFireImmunityEventRunning(false);
			p.getPacketSender().sendMessage("Your resistance to dragonfire has run out.");
			stop();
		}
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
	}
	
	public static void makeImmune(final Player p, int seconds, int fireDamageModifier) {
		p.getAttributes().setFireImmunity(seconds).setFireDamageModifier(fireDamageModifier);
		if(p.getAttributes().isFireImmuneEventRunning())
			return;
		p.getAttributes().setFireImmunityEventRunning(true);
		TaskManager.submit(new FireImmunityTask(p));
	}
}
