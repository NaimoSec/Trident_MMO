package org.trident.world.content;

import org.trident.model.movement.PathFinder;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

/**
 * Handles following
 * @author Gabbe
 */
public class Following {

	/**
	 * Finds a route to the other character.
	 */
	public static void followGameCharacter(GameCharacter follower, GameCharacter leader) {
		if(follower.getInteractingEntity() == null || follower.getInteractingEntity() != leader || follower.getPositionToFace() != leader.getPosition()) {
			follower.setEntityInteraction(leader);
			follower.setPositionToFace(leader.getPosition());
		}
		boolean summoningFollower = follower.isNpc() && ((NPC)follower).getAttributes().isSummoningNpc();
		int otherX = leader.getPosition().getX();
		int otherY = leader.getPosition().getY();
		boolean sameSpot = (follower.getPosition().equals(leader.getPosition()) || summoningFollower && otherY > (2 + follower.getSize())) && !follower.getMovementQueue().isMoving() && !leader.getMovementQueue().isMoving();
		if(!summoningFollower && sameSpot) {
			stepAway(follower);
			return;
		}
		if(!Locations.goodDistance(follower.getPosition(), leader.getPosition(), summoningFollower ? 2 : 1)) {
			if(summoningFollower)
				otherY -= (2 + follower.getSize());
			if(leader.isPlayer()) {
				if(!leader.isTeleporting() && !Locations.goodDistance(leader.getPosition().getX(), leader.getPosition().getY(), follower.getPosition().getX(), follower.getPosition().getY(), 15) || leader.getPosition().getZ() != follower.getPosition().getZ()) {
					if(follower.isPlayer()) {
						((Player)follower).getPacketSender().sendMessage("Unable to find "+((Player)leader).getUsername()+".");
						follower.followCharacter(null);
						follower.setEntityInteraction(null);
						follower.getMovementQueue().stopMovement();
					} else if(summoningFollower)
						((Player)leader).getAdvancedSkills().getSummoning().moveFollower();
					return;
				}
			}
			PathFinder.findPath(follower, otherX - follower.getSize(), otherY, true, follower.getSize(), follower.getSize());
		}
	}


	/**
	 * Steps away from a Gamecharacter
	 * @param character		The gamecharacter to step away from
	 */
	public static void stepAway(GameCharacter character) {
		if(character.getMovementQueue().canWalk(-1, 0))
			character.getMovementQueue().walkStep(-1, 0);
		else if(character.getMovementQueue().canWalk(1, 0))
			character.getMovementQueue().walkStep(1, 0);
		else if(character.getMovementQueue().canWalk(0, -1))
			character.getMovementQueue().walkStep(0, -1);
		else if(character.getMovementQueue().canWalk(0, 1))
			character.getMovementQueue().walkStep(0, 1);
	}
}
