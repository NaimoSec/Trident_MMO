package org.trident.net.packet.impl;

import org.trident.model.Animation;
import org.trident.model.Position;
import org.trident.model.movement.MovementStatus;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.content.PriceChecker;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.minigames.impl.Dueling;
import org.trident.world.entity.player.Player;

/**
 * This packet listener is called when a player has clicked on 
 * either the mini-map or the actual game map to move around.
 * 
 * @author relex lawl
 */

public class MovementPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		int size = packet.getSize();
		if (packet.getOpcode() == 248)
			size -= 14;
		if(!checkReqs(player))
			return;
		player.getMovementQueue().setMovementStatus(MovementStatus.MOVING);
		player.getPacketSender().sendNonWalkableAttributeRemoval();
		final int steps = (size - 5) / 2;
		if (steps < 0)
			return;
		final int firstStepX = packet.readLEShortA();
		final int[][] path = new int[steps][2];
		for (int i = 0; i < steps; i++) {
			path[i][0] = packet.readByte();
			path[i][1] = packet.readByte();
		}
		final int firstStepY = packet.readLEShort();
		//packet.readByteC();
		//final boolean runSteps = packet.readByteC() == 1;
		final Position[] positions = new Position[steps + 1];
		positions[0] = new Position(firstStepX, firstStepY, player.getPosition().getZ());
		for (int i = 0; i < steps; i++)
			positions[i + 1] = new Position(path[i][0] + firstStepX, path[i][1]
					+ firstStepY, player.getPosition().getZ());
		//if(MovementQueue.canWalk(player, player.getPosition(), positions[0])) {
			if (player.getMovementQueue().addFirstStep(positions[0])) {
				for (int i = 1; i < positions.length; i++) {
					//if(MovementQueue.canWalk(player, player.getPosition(), positions[i]))
						player.getMovementQueue().addStep(positions[i]);
				}
			}
		//}
	}

	public boolean checkReqs(Player player) {
		player.setEntityInteraction(null);
		if(player.getAttributes().clientIsLoading())
			return false;
		if(player.getSkillManager().getSkillAttributes().getAgilityAttributes().isCrossingObstacle() || player.getCombatAttributes().isStunned() || player.isTeleporting())
			return false;
		if(player.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().isViewingInterface())
			return false;
		if(player.getCombatAttributes().getFreezeDelay() > 0 || player.getMovementQueue().getMovementStatus() == MovementStatus.FROZEN) {
			player.getPacketSender().sendMessage("A magical force stops you from moving.");
			return false;
		}
		if(player.getDueling().inDuelScreen && player.getDueling().duelingStatus != 5) {
			player.getPacketSender().sendMessage("Please use the decline option to close your current duel.");
			return false;
		}
		if(player.getTrading().inTrade()) {
			player.getPacketSender().sendMessage("Please use the decline option to close your current trade.");
			return false;
		}
		if(player.getAttributes().isPriceChecking()) {
			PriceChecker.closePriceChecker(player);
			return false;
		}
		if (player.getConstitution() <= 0 || !player.getMovementQueue().getMovementStatus().equals(MovementStatus.NONE) && !player.getMovementQueue().getMovementStatus().equals(MovementStatus.MOVING))
			return false;
		if(player.getAttributes().isResting()) {
			player.performAnimation(new Animation(11788));
			player.getAttributes().setResting(false);
			player.getMovementQueue().reset(true);
			return false;
		}
		if(player.getCharacterToFollow() != null) {
			player.followCharacter(null);
			return true;
		}
		if(player.getLocation() == Location.DUEL_ARENA && Dueling.checkDuel(player, 5)) {
			if(player.getDueling().selectedDuelRules[Dueling.DuelRule.NO_MOVEMENT.ordinal()]) {
				player.getPacketSender().sendMessage("Movement has been disabled in this duel.");
				return false;
			}
		}
		if(player.getDifficulty() == null && player.getAttributes().hasStarted()) {
			DialogueManager.start(player, 432);
			player.getAttributes().setDialogueAction(403);
			return false;
		}
		return true;
	}

	public static final int COMMAND_MOVEMENT_OPCODE = 98;
	public static final int GAME_MOVEMENT_OPCODE = 164;
	public static final int MINIMAP_MOVEMENT_OPCODE = 248;

}