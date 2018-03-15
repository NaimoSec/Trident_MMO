package org.trident.world.entity.updating;

import java.util.Iterator;

import org.trident.model.Direction;
import org.trident.model.Flag;
import org.trident.model.Position;
import org.trident.model.UpdateFlag;
import org.trident.net.packet.PacketBuilder;
import org.trident.net.packet.Packet.PacketType;
import org.trident.net.packet.PacketBuilder.AccessType;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.entity.Entity;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

/**
 * Represents a player's npc updating task, which loops through all local
 * npcs and updates their masks according to their current attributes.
 * 
 * @author Relex lawl
 */

public class NPCUpdating {
	
	/**
	 * Handles the actual npc updating for the associated player.
	 * @return	The NPCUpdating instance.
	 */
	public static void update(Player player) {
		PacketBuilder update = new PacketBuilder();
		PacketBuilder packet = new PacketBuilder(65, PacketType.SHORT);
		packet.initializeAccess(AccessType.BIT);
		packet.writeBits(8, player.getAttributes().getLocalNpcs().size());
		for (Iterator<NPC> npcIterator = player.getAttributes().getLocalNpcs().iterator(); npcIterator.hasNext();) {
			NPC npc = npcIterator.next();
			if (World.getNpcs().get(npc.getIndex()) != null && npc.getAttributes().isVisible() && player.getPosition().isWithinDistance(npc.getPosition())) {
				updateMovement(npc, packet);
				if (npc.getUpdateFlag().isUpdateRequired()) {
					appendUpdates(npc, update);
				}
			} else {
				npcIterator.remove();
				packet.writeBits(1, 1);
				packet.writeBits(2, 3);
			}
		}
		for(NPC npc : World.getNpcs()) {
			if (player.getAttributes().getLocalNpcs().size() >= 79) //Originally 255
				break;
			if (npc == null || player.getAttributes().getLocalNpcs().contains(npc) || !npc.getAttributes().isVisible())
				continue;
			if (npc.getPosition().isWithinDistance(player.getPosition())) {
				player.getAttributes().getLocalNpcs().add(npc);
				addNPC(player, npc, packet);
				if (npc.getUpdateFlag().isUpdateRequired()) {
					appendUpdates(npc, update);
				}
			}
		}
		if (update.toPacket().getBuffer().writerIndex() > 0) {
			packet.writeBits(14, 16383);
			packet.initializeAccess(AccessType.BYTE);
			packet.writeBuffer(update.toPacket().getBuffer());
		} else {
			packet.initializeAccess(AccessType.BYTE);
		}
		player.write(packet.toPacket());
	}

	/**
	 * Adds an npc to the associated player's client.
	 * @param npc		The npc to add.
	 * @param builder	The packet builder to write information on.
	 * @return			The NPCUpdating instance.
	 */
	private static void addNPC(Player player, NPC npc, PacketBuilder builder) {
		/*	builder.writeBits(14, npc.getIndex());
		builder.writeBits(5, npc.getPosition().getY() - player.getPosition().getY());
		builder.writeBits(5, npc.getPosition().getX() - player.getPosition().getX());
		builder.writeBits(1, 0);
		builder.writeBits(16, npc.getId());
		builder.writeBits(1, npc.getUpdateFlag().isUpdateRequired() ? 1 : 0);*/
		builder.writeBits(14, npc.getIndex());
		builder.writeBits(5, npc.getPosition().getY()-player.getPosition().getY());
		builder.writeBits(5, npc.getPosition().getX()-player.getPosition().getX());
		builder.writeBits(1, 0);
		builder.writeBits(18, npc.getId());
		builder.writeBits(1, npc.getUpdateFlag().isUpdateRequired() ? 1 : 0);
	}

	/**
	 * Updates the npc's movement queue.
	 * @param npc		The npc who's movement is updated.
	 * @param builder	The packet builder to write information on.
	 * @return			The NPCUpdating instance.
	 */
	private static void updateMovement(NPC npc, PacketBuilder builder) {
		if (npc.getSecondaryDirection().toInteger() == -1) {
			if (npc.getPrimaryDirection().toInteger() == -1) {
				if (npc.getUpdateFlag().isUpdateRequired()) {
					builder.writeBits(1, 1);
					builder.writeBits(2, 0);
				} else {
					builder.writeBits(1, 0);
				}
			} else {
				builder.writeBits(1, 1);
				builder.writeBits(2, 1);
				builder.writeBits(3, npc.getPrimaryDirection().toInteger());
				builder.writeBits(1, npc.getUpdateFlag().isUpdateRequired() ? 1 : 0);
			}
		} else {
			builder.writeBits(1, 1);
			builder.writeBits(2, 2);
			builder.writeBits(3, npc.getPrimaryDirection().toInteger());
			builder.writeBits(3, npc.getSecondaryDirection().toInteger());
			builder.writeBits(1, npc.getUpdateFlag().isUpdateRequired() ? 1 : 0);
		}
	}

	/**
	 * Appends a mask update for {@code npc}.
	 * @param npc		The npc to update masks for.
	 * @param builder	The packet builder to write information on.
	 * @return			The NPCUpdating instance.
	 */
	private static void appendUpdates(NPC npc, PacketBuilder builder) {
		int mask = 0;
		UpdateFlag flag = npc.getUpdateFlag();
		if (flag.flagged(Flag.ANIMATION) && npc.getAnimation() != null) {
			mask |= 0x10;
		}
		if (flag.flagged(Flag.GRAPHIC) && npc.getGraphic() != null) {
			mask |= 0x80;
		}
		if (flag.flagged(Flag.SINGLE_HIT) && npc.getDamage().getHits().length >= 1) {
			mask |= 0x8;
		}
		if (flag.flagged(Flag.ENTITY_INTERACTION)) {
			mask |= 0x20;
		}
		if (flag.flagged(Flag.FORCED_CHAT) && npc.getForcedChat().length() > 0) {
			mask |= 0x1;
		}
		if (flag.flagged(Flag.DOUBLE_HIT) && npc.getDamage().getHits().length >= 2) {
			mask |= 0x40;
		}
		if (flag.flagged(Flag.TRANSFORM) && npc.getAttributes().getTransformationId() != -1) {
			mask |= 0x2;
		}
		if (flag.flagged(Flag.FACE_POSITION) && npc.getPositionToFace() != null) {
			mask |= 0x4;
		}
		builder.writeByte(mask);
		if (flag.flagged(Flag.ANIMATION) && npc.getAnimation() != null) {
			updateAnimation(builder, npc);
		}
		if (flag.flagged(Flag.SINGLE_HIT) && npc.getDamage().getHits().length >= 1) {
			updateSingleHit(builder, npc);
		}
		if (flag.flagged(Flag.GRAPHIC) && npc.getGraphic() != null) {
			updateGraphics(builder, npc);
		}
		if (flag.flagged(Flag.ENTITY_INTERACTION)) {
			Entity entity = npc.getInteractingEntity();
			builder.writeShort(entity == null ? -1 : entity.getIndex() + (entity instanceof Player ? 32768 : 0));
		}
		if (flag.flagged(Flag.FORCED_CHAT) && npc.getForcedChat().length() > 0) {
			builder.writeString(npc.getForcedChat());
		}
		if (flag.flagged(Flag.DOUBLE_HIT) && npc.getDamage().getHits().length >= 2) {
			updateDoubleHit(builder, npc);
		}
		if (flag.flagged(Flag.TRANSFORM) && npc.getAttributes().getTransformationId() != -1) {
			builder.writeLEShortA(npc.getAttributes().getTransformationId());
		}
		if (flag.flagged(Flag.FACE_POSITION) && npc.getPositionToFace() != null) {
			final Position position = npc.getPositionToFace();
			int x = position == null ? 0 : position.getX(); 
			int y = position == null ? 0 : position.getY();
			builder.writeLEShort(x * 2 + 1);
			builder.writeLEShort(y * 2 + 1);
		}
	}

	/**
	 * Updates {@code npc}'s current animation and displays it for all local players.
	 * @param builder	The packet builder to write information on.
	 * @param npc		The npc to update animation for.
	 * @return			The NPCUpdating instance.
	 */
	private static void updateAnimation(PacketBuilder builder, NPC npc) {
		builder.writeLEShort(npc.getAnimation().getId());
		builder.writeByte(npc.getAnimation().getDelay());
	}

	/**
	 * Updates {@code npc}'s current graphics and displays it for all local players.
	 * @param builder	The packet builder to write information on.
	 * @param npc		The npc to update graphics for.
	 * @return			The NPCUpdating instance.
	 */
	private static void updateGraphics(PacketBuilder builder, NPC npc) {
		builder.writeShort(npc.getGraphic().getId());
		builder.writeInt(((npc.getGraphic().getHeight().ordinal() * 50) << 16) + (npc.getGraphic().getDelay() & 0xffff));
	}

	/**
	 * Updates the npc's single hit.
	 * @param builder	The packet builder to write information on.
	 * @param npc		The npc to update the single hit for.
	 * @return			The NPCUpdating instance.
	 */
	private static void updateSingleHit(PacketBuilder builder, NPC npc) {
		builder.writeShortA(npc.getDamage().getHits()[0].getDamage());
		builder.writeByteC(npc.getDamage().getHits()[0].getHitmask().ordinal());
		builder.writeByte(npc.getDamage().getHits()[0].getCombatIcon().getId());
		builder.writeShortA(Misc.getCurrentHP(npc.getAttributes().getConstitution(), npc.getDefaultAttributes().getConstitution(), 100));
		builder.writeShortA(100);
	}

	/**
	 * Updates the npc's double hit.
	 * @param builder	The packet builder to write information on.
	 * @param npc		The npc to update the double hit for.
	 * @return			The NPCUpdating instance.
	 */
	private static void updateDoubleHit(PacketBuilder builder, NPC npc) {
		if(npc.getDamage().getHits().length != 2)
			return;
		builder.writeShortA(npc.getDamage().getHits()[1].getDamage());
		builder.writeByteS(npc.getDamage().getHits()[1].getHitmask().ordinal());
		builder.writeByte(npc.getDamage().getHits()[1].getCombatIcon().getId());
		builder.writeShortA(Misc.getCurrentHP(npc.getAttributes().getConstitution(), npc.getDefaultAttributes().getConstitution(), 100));
		builder.writeShortA(100);
	}

	/**
	 * Resets all the npc's flags that should be reset after a tick's update
	 * @param npc	The npc to reset flags for.
	 */
	public static void resetFlags(NPC npc) {
		npc.getUpdateFlag().reset();
		npc.setTeleporting(false).setTeleportPosition(null).setForcedChat("");
		npc.setDirections(Direction.NONE, Direction.NONE);
	}
}
