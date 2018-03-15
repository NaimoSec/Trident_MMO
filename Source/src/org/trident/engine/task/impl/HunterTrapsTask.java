package org.trident.engine.task.impl;

import java.util.Iterator;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.world.content.skills.impl.hunter.Hunter;
import org.trident.world.content.skills.impl.hunter.Trap;
import org.trident.world.content.skills.impl.hunter.TrapExecution;
import org.trident.world.content.skills.impl.hunter.Trap.TrapState;

public class HunterTrapsTask extends Task {
	
	public HunterTrapsTask() {
		super(1);
	}

	@Override
	protected void execute() {
		final Iterator<Trap> iterator = Hunter.traps.iterator();
		while (iterator.hasNext()) {
			final Trap trap = iterator.next();
			if (trap == null)
				continue;
			if (trap.getOwner() == null)
				Hunter.deregister(trap);
			TrapExecution.setTrapProcess(trap);
			TrapExecution.trapTimerManagement(trap);
			if (trap.getTrapState().equals(TrapState.BAITING))
				TrapExecution.setBaitProcess(trap);
			if (trap.getTrapState().equals(TrapState.FALLEN))
				TrapExecution.setTrapProcess(trap);
		}
		if(Hunter.traps.isEmpty())
			stop();
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
		running = false;
	}
	
	public static void fireTask() {
		if(running)
			return;
		running = true;
		TaskManager.submit(new HunterTrapsTask());
	}
	
	private static boolean running;
}
