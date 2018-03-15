package org.trident.engine.task.impl;

import org.trident.model.Position;
import org.trident.world.content.Locations;
import org.trident.world.entity.Entity;
import org.trident.world.entity.player.Player;

/**
 * Represents a movement action for a game character.
 * @author Gabbe
 */

public class WalkToAction {

	/**
	 * The WalkToTask constructor.
	 * @param entity			The associated game character.
	 * @param destination		The destination the game character will move to.
	 * @param finalizedTask		The task a player must execute upon reaching said destination.
	 */
	public WalkToAction(Player entity, Position destination, FinalizedMovementTask finalizedTask) {
		this.player = entity;
		this.destination = destination;
		this.finalizedTask = finalizedTask;
		this.interactingEntity = null;
	}

	/**
	 * The WalkToTask constructor.
	 * @param entity			The associated game character.
	 * @param destination		The destination the game character will move to.
	 * @param finalizedTask		The task a player must execute upon reaching said destination.
	 */
	public WalkToAction(Player entity, Position destination, Entity interactingEntity, FinalizedMovementTask finalizedTask) {
		this.player = entity;
		this.destination = destination;
		this.finalizedTask = finalizedTask;
		this.interactingEntity = interactingEntity;
	}

	/**
	 * The associated game character.
	 */
	private final Player player;

	/**
	 * The destination the game character will move to.
	 */
	private Position destination;

	/**
	 * The task a player must execute upon reaching said destination.
	 */
	private final FinalizedMovementTask finalizedTask;

	/**
	 * If <code>true</code> the associated game character will need to have a distance of 1 from destination, otherwise they would need to be at a distance 0.
	 */
	private final Entity interactingEntity;

	/**
	 * Executes the action if distance is correct
	 */
	public void tick() {
		if(player == null)
			return;
		if(player.getAttributes().getWalkToAction() != null && player.getAttributes().getWalkToAction() == this) {
			if(player.isTeleporting() || player.getConstitution() <= 0 || destination == null) {
				player.getAttributes().setWalkToTask(null);
				return;
			}
			if (Locations.goodDistance(player.getPosition().getX(), player.getPosition().getY(), destination.getX(), destination.getY(), 1)|| destination.equals(player.getPosition()) || player.getPosition().getDistance(destination) == (interactingEntity != null ? interactingEntity.getSize() : 0) || player.getPosition().getDistance(destination) == (interactingEntity != null ? interactingEntity.getSize()+1 : 1)) {
				finalizedTask.execute();
				player.setEntityInteraction(null);
				player.getAttributes().setWalkToTask(null);
			}
		}
	}
}
