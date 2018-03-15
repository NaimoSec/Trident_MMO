package org.trident.engine.task.impl;

/**
 * Represents a task that must be executed once
 * a movement task has been completed.
 * 
 * @author relex lawl
 */

public interface FinalizedMovementTask {
	
	public void execute();
}
