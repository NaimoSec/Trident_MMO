package org.trident.model.movement;

import java.util.ArrayDeque;
import java.util.Deque;

import org.trident.model.Direction;
import org.trident.model.Position;
import org.trident.util.Misc;
import org.trident.world.clip.region.RegionClipping;
import org.trident.world.content.EnergyHandler;
import org.trident.world.content.Locations;
import org.trident.world.content.Locations.Location;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCAggression;
import org.trident.world.entity.player.Player;

/**
 * A queue of {@link Direction}s which a {@link Character} will follow.
 * 
 * @author Graham Edgecombe 
 * Edited by Gabbe
 */
public final class MovementQueue {

	/**
	 * Represents a single point in the queue.
	 * 
	 * @author Graham Edgecombe
	 */
	private static final class Point {

		/**
		 * The point's position.
		 */
		private final Position position;

		/**
		 * The direction to walk to this point.
		 */
		private final Direction direction;

		/**
		 * Creates a point.
		 * 
		 * @param position
		 *            The position.
		 * @param direction
		 *            The direction.
		 */
		public Point(Position position, Direction direction) {
			this.position = position;
			this.direction = direction;
		}

		@Override
		public String toString() {
			return Point.class.getName() + " [direction=" + direction
					+ ", position=" + position + "]";
		}

	}

	/**
	 * The maximum size of the queue. If any additional steps are added, they
	 * are discarded.
	 */
	private static final int MAXIMUM_SIZE = 50;

	/**
	 * The character whose walking queue this is.
	 */
	private final GameCharacter character;

	/**
	 * The queue of directions.
	 */
	private final Deque<Point> points = new ArrayDeque<Point>();

	/**
	 * Creates a walking queue for the specified character.
	 * 
	 * @param character
	 *            The character.
	 */
	public MovementQueue(GameCharacter character) {
		this.character = character;
	}

	/**
	 * Adds the first step to the queue, attempting to connect the server and
	 * client position by looking at the previous queue.
	 * 
	 * @param clientConnectionPosition
	 *            The first step.
	 * @return {@code true} if the queues could be connected correctly,
	 *         {@code false} if not.
	 */
	public boolean addFirstStep(Position clientConnectionPosition) {
		points.clear();
		addStep(clientConnectionPosition);
		return true;
	}

	/**
	 * Adds a step to walk to the queue.
	 * 
	 * @param x
	 *            X to walk to
	 * @param y
	 *            Y to walk to
	 * @param clipped
	 *            Can the step walk through objects?
	 */
	public void walkStep(int x, int y) {
		Position position = character.getPosition().copy();
		position.setX(position.getX() + x);
		position.setY(position.getY() + y);
		addStep(position);
	}

	/**
	 * Adds a step.
	 * 
	 * @param x
	 *            The x coordinate of this step.
	 * @param y
	 *            The y coordinate of this step.
	 * @param heightLevel
	 * @param flag
	 */
	private void addStep(int x, int y, int heightLevel) {
		if (getMovementStatus() != MovementStatus.NONE && getMovementStatus() != MovementStatus.MOVING)
			return;
		if (points.size() >= MAXIMUM_SIZE)
			return;

		final Point last = getLast();
		final int deltaX = x - last.position.getX();
		final int deltaY = y - last.position.getY();
		final Direction direction = Direction.fromDeltas(deltaX, deltaY);
		if (direction != Direction.NONE)
			points.add(new Point(new Position(x, y, heightLevel), direction));
		else
			character.setDirections(Direction.NONE, Direction.NONE);
	}

	/**
	 * Adds a step to the queue.
	 * 
	 * @param step
	 *            The step to add.
	 * @oaram flag
	 */
	public void addStep(Position step) {
		if (character.getCombatAttributes().getFreezeDelay() > 0 || getMovementStatus() == MovementStatus.CANNOT_MOVE)
			return;
		final Point last = getLast();
		final int x = step.getX();
		final int y = step.getY();
		int deltaX = x - last.position.getX();
		int deltaY = y - last.position.getY();
		final int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));
		for (int i = 0; i < max; i++) {
			if (deltaX < 0)
				deltaX++;
			else if (deltaX > 0)
				deltaX--;
			if (deltaY < 0)
				deltaY++;
			else if (deltaY > 0)
				deltaY--;
			addStep(x - deltaX, y - deltaY, step.getZ());
		}
	}

	public boolean canWalk(int deltaX, int deltaY) {
		return canWalk(character, character.getPosition(), new Position(character.getPosition().getX()+deltaX, character.getPosition().getY()+deltaY, character.getPosition().getZ()));
	}

	public static boolean canWalk(GameCharacter character, Position from, Position to) {
		if(from.getZ() == -1 && to.getZ() == -1 && character.isNpc() && !((NPC)character).getAttributes().isSummoningNpc() || character.getLocation() == Location.RECIPE_FOR_DISASTER)
			return true;
		if(character.isNpc()) {
			NPC npc = ((NPC)character);
			if(npc.getRegion() != null) {
				for(NPC n : npc.getRegion().getNpcs()) {
					if(n != null && !n.getAttributes().isSummoningNpc() && n.getConstitution() > 0 && !n.getAttributes().isSummoningNpc() && n.getPosition().equals(to)) {
						return false;
					}
				}
			}
		}
		return RegionClipping.canMove(from, to, 1, 1);
	}


	/*
	 * public boolean checkBarricade(int x, int y) { Position position =
	 * character.getPosition(); if(character.isPlayer()) {
	 * if(Locations.inSoulWars((Player)character)) {
	 * if(SoulWars.checkBarricade(position.getX() + x, position.getY()+ y,
	 * position.getZ())) { ((Player)character).getPacketSender().sendMessage(
	 * "The path is blocked by a Barricade."); reset(true); return true; } } }
	 * return false; }
	 */

	/**
	 * Gets the last point.
	 * 
	 * @return The last point.
	 */
	private Point getLast() {
		final Point last = points.peekLast();
		if (last == null)
			return new Point(character.getPosition(), Direction.NONE);
		return last;
	}

	/**
	 * @return true if the character is moving.
	 */
	public boolean isMoving() {
		return !points.isEmpty();
	}

	/**
	 * Called every pulse, updates the queue.
	 */
	public void pulse() {
		boolean locationCheck = false, teleporting = character.getTeleportPosition() != null;
		if (teleporting) {
			stopMovement();
			character.setTeleporting(true);
			character.setPosition(character.getTeleportPosition().copy());
			character.setTeleportPosition(null);
			if(character.isPlayer())
				handleRegionChange();
			locationCheck = true;
		} else {
			Direction first = Direction.NONE;
			Direction second = Direction.NONE;
			Point next = points.poll();
			if (next != null) {
				first = next.direction;
				Position position = next.position;
				/*
				 * NPCs walking into each other
				 */
				if(character.isNpc()) {
					for(NPC n : ((NPC)character).getRegion().getNpcs()) {
						if(n != null && !n.getAttributes().isSummoningNpc() && n.getConstitution() > 0 && !n.getAttributes().isSummoningNpc() && n.getPosition().equals(position)) {
							return;
						}
					}
				}
				boolean running = character.isPlayer() && ((Player) character).getAttributes().isRunning();
				if (running) {
					next = points.poll();
					if (next != null) {
						second = next.direction;
						position = next.position;
					}
				}
				character.setPosition(position);
				if(character.isPlayer())
					handleRegionChange();
				locationCheck = true;
			}
			character.setDirections(first, second);
		}
		if(locationCheck || character.isPlayer()) {
			if(character.isPlayer()) {
				Locations.process(character);
				EnergyHandler.processPlayerEnergy((Player)character);
				NPCAggression.processFor((Player)character);
			}
		}
	}

	public void handleRegionChange() {
		final int diffX = character.getPosition().getX()
				- character.getLastKnownRegion().getRegionX() * 8;
		final int diffY = character.getPosition().getY()
				- character.getLastKnownRegion().getRegionY() * 8;
		boolean regionChanged = false;
		if (diffX < 16)
			regionChanged = true;
		else if (diffX >= 88)
			regionChanged = true;
		if (diffY < 16)
			regionChanged = true;
		else if (diffY >= 88)
			regionChanged = true;
		if (regionChanged)
			((Player)character).getAttributes().setRegionChange(true);
	}

	/**
	 * Stops the movement.
	 */
	public MovementQueue stopMovement() {
		reset(true);
		if (character.isPlayer())
			((Player) character).getAttributes().setWalkToTask(null);
		if(status == MovementStatus.MOVING)
			status = MovementStatus.NONE;
		return this;
	}

	/**
	 * Resets the movement.
	 * 
	 * @param removeMiniflag
	 */
	public void reset(boolean removeMiniflag) {
		points.clear();
	}

	/**
	 * Gets the size of the queue.
	 * 
	 * @return The size of the queue.
	 */
	public int size() {
		return points.size();
	}

	private MovementStatus status = MovementStatus.NONE;

	public MovementStatus getMovementStatus() {
		return this.status;
	}

	public MovementQueue setMovementStatus(MovementStatus status) {
		this.status = status;
		return this;
	}

	/**
	 * The force movement array index values.
	 */
	public static final int FIRST_MOVEMENT_X = 0, FIRST_MOVEMENT_Y = 1,
			SECOND_MOVEMENT_X = 2, SECOND_MOVEMENT_Y = 3,
			MOVEMENT_SPEED = 4, MOVEMENT_REVERSE_SPEED = 5,
			MOVEMENT_DIRECTION = 6;

	/*
	 * Npc walking and stuff
	 */
	/**
	 * Handles NPC's random walk
	 * Credits to Relex lawl for this method.
	 * @param n	The NPC to handle the random walk for.
	 */

	public static void walkAround(NPC n)
	{
		if(n.getAttributes().isWalkingHome()) {
			if(!Locations.goodDistance(n.getPosition().copy(), n.getDefaultPosition(), 1))
				return;
			else
				n.getAttributes().setWalkingHome(false);
		}
		if(Misc.getRandom(8) > n.getAttributes().getWalkingDistance())
			return;
		n.setEntityInteraction(null);
		int dir = -1;
		int x = 0, y = 0;
		if (!RegionClipping.blockedNorth(n.getPosition()))
		{
			dir = 0;
		}
		else if (!RegionClipping.blockedEast(n.getPosition()))
		{
			dir = 4;
		}
		else if (!RegionClipping.blockedSouth(n.getPosition()))
		{
			dir = 8;
		}
		else if (!RegionClipping.blockedWest(n.getPosition()))
		{
			dir = 12;
		}
		int random = Misc.getRandom(3);

		boolean found = false;

		if (random == 0)
		{
			if (!RegionClipping.blockedNorth(n.getPosition()))
			{
				y = 1;
				found = true;
			}
		}
		else if (random == 1)
		{
			if (!RegionClipping.blockedEast(n.getPosition()))
			{
				x = 1;
				found = true;
			}
		}
		else if (random == 2)
		{
			if (!RegionClipping.blockedSouth(n.getPosition()))
			{
				y = -1;
				found = true;
			}
		}
		else if (random == 3)
		{
			if (!RegionClipping.blockedWest(n.getPosition()))
			{
				x = -1;
				found = true;
			}
		}
		if (!found)
		{
			if (dir == 0)
			{
				y = 1;
			}
			else if (dir == 4)
			{
				x = 1;
			}
			else if (dir == 8)
			{
				y = -1;
			}
			else if (dir == 12)
			{
				x = -1;
			}
		}
		if(x == 0 && y == 0)
			return;
		int spawnX = n.getDefaultPosition().getX();
		int spawnY = n.getDefaultPosition().getY();
		if (x == 1) {
			if (n.getPosition().getX() + x > spawnX + 1)
				return;
		}
		if (x == -1) {
			if (n.getPosition().getX() - x < spawnX - 1)
				return;
		}
		if (y == 1) {
			if (n.getPosition().getY() + y > spawnY + 1)
				return;
		}
		if (y == -1) {
			if (n.getPosition().getY() - y < spawnY - 1)
				return;
		}
		n.getMovementQueue().walkStep(x, y);
	}

	/**
	 * Makes an NPC walk home
	 * @param n		The npc who should walk home
	 * @author Gabbe
	 */
	public static void walkHome(NPC n) {
		int spawnX = n.getDefaultPosition().getX();
		int spawnY = n.getDefaultPosition().getY();
		PathFinder.findPath(n, spawnX, spawnY, true, 1, 1);
	}

}