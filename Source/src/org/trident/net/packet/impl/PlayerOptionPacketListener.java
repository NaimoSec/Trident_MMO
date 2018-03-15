package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.World;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.entity.player.Player;

/**
 * This packet listener is called when a player has clicked on another player's
 * menu actions.
 * 
 * @author relex lawl
 */

public class PlayerOptionPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		if(player.isTeleporting())
			return;
		switch(packet.getOpcode()) {
		case 153:
			attack(player, packet);
			break;
		case 128:
			option1(player, packet);
			break;
		case 37:
			option2(player,  packet);
			break;
		case 227:
			option3(player, packet);
			break;
		}
	}

	private static void attack(final Player player, Packet packet) {
		int index = packet.readLEShort();
		if(index > World.getPlayers().size() || index < 0)
			return;
		final Player player2 = World.getPlayers().get(index);
		if(player2 == null || player2.getConstitution() <= 0) {
			player.getMovementQueue().stopMovement();
			return;
		}
		if(player.getLocation() == Location.DUEL_ARENA && player.getDueling().duelingStatus == 0) {
			player.getDueling().challengePlayer(player2);
			return;
		}
		CombatHandler.setProperAttackType(player);
		CombatHandler.attack(player, player2);
		player.setEntityInteraction(player2);
	}

	/**
	 * Manages the first option click on a player option menu.
	 * @param player	The player clicking the other entity.
	 * @param packet	The packet to read values from.
	 */
	private static void option1(final Player player, Packet packet) {
		int id = packet.readShort() & 0xFFFF;
		if(id < 0 || id > World.getPlayers().size())
			return;
		Player victim = World.getPlayers().get(id);
		if (victim == null)
			return;
		/*GameServer.getTaskScheduler().schedule(new WalkToTask(player, victim.getPosition(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				//do first option here
			}
		}));*/
	}

	/**
	 * Manages the second option click on a player option menu.
	 * @param player	The player clicking the other entity.
	 * @param packet	The packet to read values from.
	 */
	private static void option2(Player player, Packet packet) {
		int id = packet.readShort() & 0xFFFF;
		if(id < 0 || id > World.getPlayers().size())
			return;
		Player victim = World.getPlayers().get(id);
		if (victim == null)
			return;
		/*GameServer.getTaskScheduler().schedule(new WalkToTask(player, victim.getPosition(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				//do second option here
			}
		}));*/
	}

	/**
	 * Manages the third option click on a player option menu.
	 * @param player	The player clicking the other entity.
	 * @param packet	The packet to read values from.
	 */
	private static void option3(Player player, Packet packet) {
		int id = packet.readLEShortA() & 0xFFFF;
		if(id < 0 || id > World.getPlayers().size())
			return;
		Player victim = World.getPlayers().get(id);
		if (victim == null)
			return;
		/*GameServer.getTaskScheduler().schedule(new WalkToTask(player, victim.getPosition(), new FinalizedMovementTask() {
			@Override
			public void execute() {
				//do third option here
			}
		}));*/
	}
}
