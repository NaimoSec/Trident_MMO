package org.trident.world.entity.updating;

import java.util.Iterator;

import org.trident.model.Appearance;
import org.trident.model.Direction;
import org.trident.model.Flag;
import org.trident.model.Gender;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.UpdateFlag;
import org.trident.model.ChatMessage.Message;
import org.trident.model.container.impl.Equipment;
import org.trident.model.definitions.ItemDefinition;
import org.trident.model.movement.MovementQueue;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketBuilder;
import org.trident.net.packet.Packet.PacketType;
import org.trident.net.packet.PacketBuilder.AccessType;
import org.trident.net.packet.impl.RegionChangePacketListener;
import org.trident.util.Misc;
import org.trident.util.NameUtils;
import org.trident.world.World;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.skills.SkillManager;
import org.trident.world.entity.Entity;
import org.trident.world.entity.player.Player;

/**
 * Represents the associated player's player updating.
 * 
 * @author relex lawl
 */

public class PlayerUpdating {

	/**
	 * The max amount of players that can be added per cycle.
	 */
	private static final int MAX_NEW_PLAYERS_PER_CYCLE = 25;

	/**
	 * Loops through the associated player's {@code localPlayer} list and updates them.
	 * @return	The PlayerUpdating instance.
	 */

	public static void update(final Player player) {
		if (player.getAttributes().isChangingRegion())
			RegionChangePacketListener.changeRegion(player);
		PacketBuilder update = new PacketBuilder();
		PacketBuilder packet = new PacketBuilder(81, PacketType.SHORT);
		packet.initializeAccess(AccessType.BIT);
		updateMovement(player, packet);
		appendUpdates(player, update, player, false, true);
		packet.writeBits(8, player.getAttributes().getLocalPlayers().size());
		for (Iterator<Player> playerIterator = player.getAttributes().getLocalPlayers().iterator(); playerIterator.hasNext();) {
			Player otherPlayer = playerIterator.next();
			if (World.getPlayers().get(otherPlayer.getIndex()) != null && otherPlayer.getPosition().isWithinDistance(player.getPosition()) && !otherPlayer.isTeleporting()) {
				updateOtherPlayerMovement(player, packet, otherPlayer);
				if (otherPlayer.getUpdateFlag().isUpdateRequired()) {
					appendUpdates(player, update, otherPlayer, false, false);
				}
			} else {
				playerIterator.remove();
				packet.writeBits(1, 1);
				packet.writeBits(2, 3);
			}
		}
		int playersAdded = 0;
		for(Player otherPlayer : World.getPlayers()) {
			if (player.getAttributes().getLocalPlayers().size() >= 79 || playersAdded > MAX_NEW_PLAYERS_PER_CYCLE)
				break;
			if (otherPlayer == null || otherPlayer == player || player.getAttributes().getLocalPlayers().contains(otherPlayer) || !otherPlayer.getPosition().isWithinDistance(player.getPosition()))
				continue;
			player.getAttributes().getLocalPlayers().add(otherPlayer);
			addPlayer(player, otherPlayer, packet);
			appendUpdates(player, update, otherPlayer, true, false);
			playersAdded++;
		}
		if (update.toPacket().getBuffer().writerIndex() > 0) {
			packet.writeBits(11, 2047);
			packet.initializeAccess(AccessType.BYTE);
			packet.writeBuffer(update.toPacket().getBuffer());
		} else {
			packet.initializeAccess(AccessType.BYTE);
		}
		player.write(packet.toPacket());
		player.getAttributes().setRegionChange(false);
	}

	/**
	 * Adds a new player to the associated player's client.
	 * @param target	The player to add to the other player's client.
	 * @param builder	The packet builder to write information on.
	 * @return			The PlayerUpdating instance.
	 */
	private static void addPlayer(Player player, Player target, PacketBuilder builder) {
		builder.writeBits(11, target.getIndex());
		builder.writeBits(1, 1);
		builder.writeBits(1, 1);
		int yDiff = target.getPosition().getY() - player.getPosition().getY();
		int xDiff = target.getPosition().getX() - player.getPosition().getX();
		builder.writeBits(5, yDiff);
		builder.writeBits(5, xDiff);
	}

	/**
	 * Updates the associated player's movement queue.
	 * @param builder	The packet builder to write information on.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateMovement(Player player, PacketBuilder builder) {
		/*
		 * Check if the player is teleporting.
		 */
		if (player.isTeleporting() || player.getAttributes().isChangingRegion()) {
			/*
			 * They are, so an update is required.
			 */
			builder.writeBits(1, 1);

			/*
			 * This value indicates the player teleported.
			 */
			builder.writeBits(2, 3);

			/*
			 * This is the new player height.
			 */
			builder.writeBits(2, player.getPosition().getZ());

			/*
			 * This indicates that the client should discard the walking queue.
			 */
			builder.writeBits(1, player.isTeleporting() ? 1 : 0);

			/*
			 * This flag indicates if an update block is appended.
			 */
			builder.writeBits(1, player.getUpdateFlag().isUpdateRequired() ? 1 : 0);

			/*
			 * These are the positions.
			 */
			builder.writeBits(7,
					player.getPosition().getLocalY(player.getLastKnownRegion()));
			builder.writeBits(7,
					player.getPosition().getLocalX(player.getLastKnownRegion()));
		} else /*
		 * Otherwise, check if the player moved.
		 */
			if (player.getPrimaryDirection().toInteger() == -1) {
				/*
				 * The player didn't move. Check if an update is required.
				 */
				if (player.getUpdateFlag().isUpdateRequired()) {
					/*
					 * Signifies an update is required.
					 */
					builder.writeBits(1, 1);

					/*
					 * But signifies that we didn't move.
					 */
					builder.writeBits(2, 0);
				} else
					/*
					 * Signifies that nothing changed.
					 */
					builder.writeBits(1, 0);
			} else /*
			 * Check if the player was running.
			 */
				if (player.getSecondaryDirection().toInteger() == -1) {

					/*
					 * The player walked, an update is required.
					 */
					builder.writeBits(1, 1);

					/*
					 * This indicates the player only walked.
					 */
					builder.writeBits(2, 1);

					/*
					 * This is the player's walking direction.
					 */

					builder.writeBits(3, player.getPrimaryDirection().toInteger());

					/*
					 * This flag indicates an update block is appended.
					 */
					builder.writeBits(1, player.getUpdateFlag().isUpdateRequired() ? 1 : 0);
				} else {

					/*
					 * The player ran, so an update is required.
					 */
					builder.writeBits(1, 1);

					/*
					 * This indicates the player ran.
					 */
					builder.writeBits(2, 2);

					/*
					 * This is the walking direction.
					 */
					builder.writeBits(3, player.getPrimaryDirection().toInteger());

					/*
					 * And this is the running direction.
					 */
					builder.writeBits(3, player.getSecondaryDirection().toInteger());

					/*
					 * And this flag indicates an update block is appended.
					 */
					builder.writeBits(1, player.getUpdateFlag().isUpdateRequired() ? 1 : 0);
				}
	}

	/**
	 * Updates another player's movement queue.
	 * @param builder			The packet builder to write information on.
	 * @param target			The player to update movement for.
	 * @return					The PlayerUpdating instance.
	 */
	private static void updateOtherPlayerMovement(Player player, PacketBuilder builder, Player target) {
		/*
		 * Check which type of movement took place.
		 */
		if (target.getPrimaryDirection().toInteger() == -1) {
			/*
			 * If no movement did, check if an update is required.
			 */
			if (target.getUpdateFlag().isUpdateRequired()) {
				/*
				 * Signify that an update happened.
				 */
				builder.writeBits(1, 1);

				/*
				 * Signify that there was no movement.
				 */
				builder.writeBits(2, 0);
			} else
				/*
				 * Signify that nothing changed.
				 */
				builder.writeBits(1, 0);
		} else if (target.getSecondaryDirection().toInteger() == -1) {
			/*
			 * The player moved but didn't run. Signify that an update is
			 * required.
			 */
			builder.writeBits(1, 1);

			/*
			 * Signify we moved one tile.
			 */
			builder.writeBits(2, 1);

			/*
			 * Write the primary sprite (i.e. walk direction).
			 */
			builder.writeBits(3, target.getPrimaryDirection().toInteger());

			/*
			 * Write a flag indicating if a block update happened.
			 */
			builder.writeBits(1, target.getUpdateFlag().isUpdateRequired() ? 1 : 0);
		} else {
			/*
			 * The player ran. Signify that an update happened.
			 */
			builder.writeBits(1, 1);

			/*
			 * Signify that we moved two tiles.
			 */
			builder.writeBits(2, 2);

			/*
			 * Write the primary sprite (i.e. walk direction).
			 */
			builder.writeBits(3, target.getPrimaryDirection().toInteger());

			/*
			 * Write the secondary sprite (i.e. run direction).
			 */
			builder.writeBits(3, target.getSecondaryDirection().toInteger());

			/*
			 * Write a flag indicating if a block update happened.
			 */
			builder.writeBits(1, target.getUpdateFlag().isUpdateRequired() ? 1 : 0);
		}
	}

	/**
	 * Appends a player's update mask blocks.
	 * @param builder				The packet builder to write information on.
	 * @param target				The player to update masks for.
	 * @param updateAppearance		Update the player's appearance without the flag being set?
	 * @param noChat				Do not allow player to chat?
	 * @return						The PlayerUpdating instance.
	 */
	private static void appendUpdates(Player player, PacketBuilder builder, Player target, boolean updateAppearance, boolean noChat) {
		if (!target.getUpdateFlag().isUpdateRequired() && !updateAppearance)
			return;
		synchronized (target) {
			final UpdateFlag flag = target.getUpdateFlag();
			int mask = 0;
			if (flag.flagged(Flag.GRAPHIC) && target.getGraphic() != null) {
				mask |= 0x100;
			}
			if (flag.flagged(Flag.ANIMATION) && target.getAnimation() != null) {
				mask |= 0x8;
			}
			if (flag.flagged(Flag.FORCED_CHAT) && target.getForcedChat().length() > 0) {
				mask |= 0x4;
			}
			if (flag.flagged(Flag.CHAT) && !noChat && !player.getRelations().getIgnoreList().contains(target.getLongUsername())) {
				mask |= 0x80;
			}
			if (flag.flagged(Flag.ENTITY_INTERACTION)) {
				mask |= 0x1;
			}
			if (flag.flagged(Flag.APPEARANCE) || updateAppearance) {
				mask |= 0x10;
			}
			if (flag.flagged(Flag.FACE_POSITION)) {
				mask |= 0x2;
			}
			if (flag.flagged(Flag.SINGLE_HIT) && target.getDamage().getHits() != null) {
				mask |= 0x20;
			}
			if (flag.flagged(Flag.DOUBLE_HIT) && target.getDamage().getHits() != null && target.getDamage().getHits().length >= 2) {
				mask |= 0x200;
			}
			if (flag.flagged(Flag.FORCED_MOVEMENT) && target.getAttributes().getForceMovement() != null) {
				mask |= 0x400;
			}
			if (mask >= 0x100) {
				mask |= 0x40;			
				builder.writeByte((mask & 0xFF));
				builder.writeByte((mask >> 8));
			} else {
				builder.writeByte(mask);
			}
			if (flag.flagged(Flag.GRAPHIC) && target.getGraphic() != null) {
				updateGraphics(builder, target);
			}
			if (flag.flagged(Flag.ANIMATION) && target.getAnimation() != null) {
				updateAnimation(builder, target);
			}
			if (flag.flagged(Flag.FORCED_CHAT) && target.getForcedChat().length() > 0) {
				updateForcedChat(builder, target);
			}
			if (flag.flagged(Flag.CHAT) && !noChat && !player.getRelations().getIgnoreList().contains(target.getLongUsername())) {
				updateChat(builder, target);
			}
			if (flag.flagged(Flag.ENTITY_INTERACTION)) {
				updateEntityInteraction(builder, target);
			}
			if (flag.flagged(Flag.APPEARANCE) || updateAppearance) {
				updateAppearance(player, builder, target);
			}
			if (flag.flagged(Flag.FACE_POSITION)) {
				updateFacingPosition(builder, target);
			}
			if (flag.flagged(Flag.SINGLE_HIT) && target.getDamage().getHits() != null) {
				updateSingleHit(builder, target);
			}
			if (flag.flagged(Flag.DOUBLE_HIT) && target.getDamage().getHits() != null && target.getDamage().getHits().length >= 2) {
				updateDoubleHit(builder, target);
			}
			if (flag.flagged(Flag.FORCED_MOVEMENT) && target.getAttributes().getForceMovement() != null) {
				updateForcedMovement(player, builder, target);
			}
		}
	}

	/**
	 * This update block is used to update player chat.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update chat for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateChat(PacketBuilder builder, Player target) {
		Message message = target.getChatMessages().get();
		byte[] bytes = message.getText();
		builder.writeLEShort(((message.getColour() & 0xff) << 8) | (message.getEffects() & 0xff));
		builder.writeByte(target.getRights().ordinal());
		builder.writeByte(0); 
		builder.writeByteC(bytes.length);
		builder.writeByteArray(bytes);
	}

	/**
	 * This update block is used to update forced player chat. 
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update forced chat for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateForcedChat(PacketBuilder builder, Player target) {
		builder.writeString(target.getForcedChat());
	}

	/**
	 * This update block is used to update forced player movement.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update forced movement for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateForcedMovement(Player player, PacketBuilder builder, Player target) {
		Position position = target.getPosition();
		Position myPosition = player.getLastKnownRegion();
		builder.writeByteC((position.getLocalX(myPosition) + target.getAttributes().getForceMovement()[MovementQueue.FIRST_MOVEMENT_X]));
		builder.writeByteS((position.getLocalY(myPosition) + target.getAttributes().getForceMovement()[MovementQueue.FIRST_MOVEMENT_Y]));
		builder.writeByteS((position.getLocalX(myPosition) + target.getAttributes().getForceMovement()[MovementQueue.SECOND_MOVEMENT_X]));
		builder.writeByteC((position.getLocalX(myPosition) + target.getAttributes().getForceMovement()[MovementQueue.SECOND_MOVEMENT_Y]));
		builder.writeShort(target.getAttributes().getForceMovement()[MovementQueue.MOVEMENT_SPEED]);
		builder.writeShortA(target.getAttributes().getForceMovement()[MovementQueue.MOVEMENT_REVERSE_SPEED]);
		builder.writeByte(target.getAttributes().getForceMovement()[MovementQueue.MOVEMENT_DIRECTION]);
	}

	/**
	 * This update block is used to update a player's animation.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update animations for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateAnimation(PacketBuilder builder, Player target) {
		builder.writeLEShort(target.getAnimation().getId());
		builder.writeByteC(target.getAnimation().getDelay());
	}

	/**
	 * This update block is used to update a player's graphics.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update graphics for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateGraphics(PacketBuilder builder, Player target) {
		builder.writeLEShort(target.getGraphic().getId());
		builder.writeInt(((target.getGraphic().getHeight().ordinal() * 50) << 16) + (target.getGraphic().getDelay() & 0xffff));
	}

	/**
	 * This update block is used to update a player's single hit.
	 * @param builder	The packet builder used to write information on.
	 * @param target	The player to update the single hit for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateSingleHit(PacketBuilder builder, Player target) {
		builder.writeShortA(target.getDamage().getHits()[0].getDamage());
		builder.writeByte(target.getDamage().getHits()[0].getHitmask().ordinal()); // hitmask
		builder.writeByte(target.getDamage().getHits()[0].getCombatIcon().ordinal() - 1 );
		builder.writeShortA(target.getDamage().getHits()[0].getAbsorption());
		builder.writeShortA(Misc.getCurrentHP(target.getConstitution(), SkillManager.getLevelForExperience(target.getSkillManager().getExperience(Skill.CONSTITUTION)) * 10, 100));
		builder.writeShortA(100);
	}

	/**
	 * This update block is used to update a player's double hit.
	 * @param builder	The packet builder used to write information on.
	 * @param target	The player to update the double hit for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateDoubleHit(PacketBuilder builder, Player target) {
		if(target.getDamage().getHits().length != 2)
			return;
		builder.writeShortA(target.getDamage().getHits()[1].getDamage());
		builder.writeByte(target.getDamage().getHits()[1].getHitmask().ordinal()); // hitmask
		builder.writeByte(target.getDamage().getHits()[1].getCombatIcon().ordinal() - 1 );
		builder.writeShortA(target.getDamage().getHits()[1].getAbsorption());
		builder.writeShortA(Misc.getCurrentHP(target.getConstitution(), SkillManager.getLevelForExperience(target.getSkillManager().getExperience(Skill.CONSTITUTION)) * 10, 100));
		builder.writeShortA(100);
	}

	/**
	 * This update block is used to update a player's face position.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update face position for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateFacingPosition(PacketBuilder builder, Player target) {
		final Position position = target.getPositionToFace();
		int x = position == null ? 0 : position.getX(); 
		int y = position == null ? 0 : position.getY();
		builder.writeLEShortA(x * 2 + 1);
		builder.writeLEShort(y * 2 + 1);
	}

	/**
	 * This update block is used to update a player's entity interaction.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update entity interaction for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateEntityInteraction(PacketBuilder builder, Player target) {
		Entity entity = target.getInteractingEntity();
		if (entity != null) {
			int index = entity.getIndex();
			if (entity instanceof Player)
				index += + 32768;
			builder.writeLEShort(index);
		} else {
			builder.writeLEShort(-1);
		}
	}

	/**
	 * This update block is used to update a player's appearance, this includes 
	 * their equipment, clothing, combat level, gender, head icons, user name and animations.
	 * @param builder	The packet builder to write information on.
	 * @param target	The player to update appearance for.
	 * @return			The PlayerUpdating instance.
	 */
	private static void updateAppearance(Player player, PacketBuilder builder, Player target) {	
		Appearance appearance = target.getAppearance();
		Equipment equipment = target.getEquipment();
		PacketBuilder properties = new PacketBuilder();
		properties.writeByte(appearance.getGender().ordinal());
		properties.writeByte(appearance.getHeadHint());
		properties.writeByte(target.getLocation() == Location.WILDERNESS ? appearance.getBountyHunterSkull() : -1);
		properties.writeShort(target.getPlayerCombatAttributes().isSkulled() ? 1 : 0);
		if (player.getNpcTransformationId() <= 0) {
			int[] equip = new int[equipment.capacity()];
			for (int i = 0; i < equipment.capacity(); i++) {
				equip[i] = equipment.getItems()[i].getId();
			}
			if (equip[Equipment.HEAD_SLOT] > -1) {
				properties.writeShort(0x200 + equip[Equipment.HEAD_SLOT]);
			} else {
				properties.writeByte(0);
			}
			if (equip[Equipment.CAPE_SLOT] > -1) {
				properties.writeShort(0x200 + equip[Equipment.CAPE_SLOT]);
			} else {
				properties.writeByte(0);
			}
			if (equip[Equipment.AMULET_SLOT] > -1) {
				properties.writeShort(0x200 + equip[Equipment.AMULET_SLOT]);
			} else {
				properties.writeByte(0);
			}
			if (equip[Equipment.WEAPON_SLOT] > -1) {
				properties.writeShort(0x200 + equip[Equipment.WEAPON_SLOT]);
			} else {
				properties.writeByte(0);
			}
			if (equip[Equipment.BODY_SLOT] > -1) {
				properties.writeShort(0x200 + equip[Equipment.BODY_SLOT]);
			} else {
				properties.writeShort(0x100 + appearance.getLook()[Appearance.CHEST]);
			}
			if (equip[Equipment.SHIELD_SLOT] > -1) {
				properties.writeShort(0x200 + equip[Equipment.SHIELD_SLOT]);
			} else {
				properties.writeByte(0);
			}
			
			if (ItemDefinition.forId(equip[Equipment.BODY_SLOT]).isFullBody()) {
				properties.writeByte(0);
			} else {
				properties.writeShort(0x100 + appearance.getLook()[Appearance.ARMS]);
			}
			
			if (equip[Equipment.LEG_SLOT] > -1) {
				properties.writeShort(0x200 + equip[Equipment.LEG_SLOT]);
			} else {
				properties.writeShort(0x100 + appearance.getLook()[Appearance.LEGS]);
			}
			
			if (ItemDefinition.forId(equip[Equipment.HEAD_SLOT]).isFullHelm()) {
				properties.writeByte(0);
			} else {
				properties.writeShort(0x100 + appearance.getLook()[Appearance.HEAD]);
			}
			if (equip[Equipment.HANDS_SLOT] > -1) {
				properties.writeShort(0x200 + equip[Equipment.HANDS_SLOT]);
			} else {
				properties.writeShort(0x100 + appearance.getLook()[Appearance.HANDS]);
			}
			if (equip[Equipment.FEET_SLOT] > -1) {
				properties.writeShort(0x200 + equip[Equipment.FEET_SLOT]);
			} else {
				properties.writeShort(0x100 + appearance.getLook()[Appearance.FEET]);
			}
			if (appearance.getLook()[Appearance.BEARD] <= 0 || appearance.getGender().equals(Gender.FEMALE)) {
				properties.writeByte(0);
			} else {
				properties.writeShort(0x100 + appearance.getLook()[Appearance.BEARD]);
			}
		} else {
			properties.writeShort(-1);
			properties.writeShort(player.getNpcTransformationId());
		}
		properties.writeByte(appearance.getLook()[Appearance.HAIR_COLOUR]);
		properties.writeByte(appearance.getLook()[Appearance.TORSO_COLOUR]);
		properties.writeByte(appearance.getLook()[Appearance.LEG_COLOUR]);
		properties.writeByte(appearance.getLook()[Appearance.FEET_COLOUR]);
		properties.writeByte(appearance.getLook()[Appearance.SKIN_COLOUR]);
		for(int i = 0; i < 7; i++) {
			if(target.getAttributes().getPlayerAnimations()[i] == null)
				WeaponHandler.setWeaponAnimationIndex(target);
			properties.writeShort(target.getSkillManager().getSkillAttributes().getAgilityAttributes().getAnimation() > 0 ? target.getSkillManager().getSkillAttributes().getAgilityAttributes().getAnimation() : target.getAttributes().getPlayerAnimations()[i].getId());
		}
		properties.writeLong(NameUtils.stringToLong(target.getUsername()));	
		properties.writeByte(target.getSkillManager().getCombatLevel());
		properties.writeShort(target.getRights().ordinal());
		properties.writeShort(target.getAttributes().getLoyaltyTitle());
		Packet packet = properties.toPacket();
		builder.writeByteC(packet.getBuffer().writerIndex());
		builder.writeBuffer(packet.getBuffer());
	}

	/**
	 * Resets the associated player's flags that will need to be removed
	 * upon completion of a single update.
	 * @return	The PlayerUpdating instance.
	 */
	public static void resetFlags(Player player) {
		player.getUpdateFlag().reset();
		player.getAttributes().setRegionChange(false);
		player.setTeleportPosition(null).setTeleporting(false).setForcedChat("");
		player.setDirections(Direction.NONE, Direction.NONE);
	}

}