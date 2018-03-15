package org.trident.world.content.teleporting;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Flag;
import org.trident.model.Graphic;
import org.trident.model.Position;
import org.trident.model.movement.MovementStatus;
import org.trident.util.Misc;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.entity.player.Player;


public class TeleportHandler {

	public static void teleportPlayer(final Player player, final Position targetLocation, final TeleportType teleportType) {
		if(teleportType != TeleportType.LEVER) {
			if(!checkReqs(player, targetLocation)) {
				if(player.getAttributes().getInterfaceId() == 50 && player.getAttributes().hasStarted())
					player.getPacketSender().sendInterfaceRemoval();
				return;
			}
		}
		if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 4500 || player.getMovementQueue().getMovementStatus() == MovementStatus.CANNOT_MOVE)
			return;
		CombatHandler.resetAttack(player);
		player.setTeleporting(true).getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE).stopMovement();
		cancelCurrentActions(player);
		player.getPacketSender().sendSound(SoundEffects.SoundData.TELEPORT.getSounds()[0], 10, 0);		
		player.performAnimation(teleportType.getStartAnimation());
		player.performGraphic(teleportType.getStartGraphic());
		TaskManager.submit(new Task(1, player, true) {
			final Location previous = player.getLocation();
			int tick = 0;
			@Override
			public void execute() {
				switch(teleportType) {
				case LEVER:
					if(tick == 0)
						player.performAnimation(new Animation(2140));
					else if(tick == 2) {
						player.performAnimation(new Animation(8939, 20));
						player.performGraphic(new Graphic(1576));
					} else if(tick == 4) {
						player.performAnimation(new Animation(8941));
						player.performGraphic(new Graphic(1577));
						player.setTeleportPosition(targetLocation).setPosition(targetLocation);
						player.setTeleporting(false);
						player.getPacketSender().sendSound(SoundEffects.SoundData.TELEPORT.getSounds()[1], 10, 3);
						stop();
					}
					break;
				default:
					if(tick == teleportType.getStartTick()) {
						player.performAnimation(teleportType.getEndAnimation());
						player.performGraphic(teleportType.getEndGraphic());
						Position newTargetLocation = Dungeoneering.doingDungeoneering(player) ? player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getFloor().getEntrance().copy().setZ(player.getPosition().getZ()) : targetLocation;
						player.setTeleportPosition(newTargetLocation).setPosition(newTargetLocation);
						player.setTeleporting(false);
						player.getPacketSender().sendSound(SoundEffects.SoundData.TELEPORT.getSounds()[1], 10, 3);
					} else if(tick == teleportType.getStartTick() + 3) {
						player.getMovementQueue().stopMovement().setMovementStatus(MovementStatus.NONE);
					} else if(tick == teleportType.getStartTick() + 4)
						stop();
					break;
				}
				tick++;
			}
			@Override
			public void stop() {
				setEventRunning(false);
				player.getPacketSender().sendInterfaceRemoval().sendNonWalkableAttributeRemoval();
				player.getAttributes().setClickDelay(0);
				player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[2] = false;
				if(player.getLocation() == Location.DUEL_ARENA) {
					DialogueManager.start(player, 259);
					player.getAttributes().setAcceptingAid(false);
					player.getPacketSender().sendConfig(427, 1);
				} else if(player.getPosition().getX() == 3420 && player.getPosition().getY() == 2916) //Member's zone
					DialogueManager.start(player, 383);
				else if(player.getAttributes().getPlayerAnimations()[0].getId() == 1501 && player.getLocation() == Location.WILDERNESS) {
					WeaponHandler.setWeaponAnimationIndex(player);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
				}
				if(previous == Location.BARROWS)
					previous.leave(player);
				if(player.getPosition().getX() == 2345 && player.getPosition().getY() == 3694) //Fishing colony dialogue
					DialogueManager.start(player, 445);
			}
		});
		player.getAttributes().setClickDelay(System.currentTimeMillis());
	}

	public static boolean interfaceOpen(Player player) {
		if(player.getAttributes().getInterfaceId() > 0 && player.getAttributes().getInterfaceId() != 50100) {
			player.getPacketSender().sendMessage("Please close the interface you have open before opening another.");
			return true;
		}
		return false;
	}

	public static boolean clickSpell(Player player, int ID) {
		if(!player.getAttributes().hasStarted()) // Didnt finish tut
			return false;
		switch(ID) {
		case 11001: //Home teleport
			teleportPlayer(player, new Position(3088 + Misc.getRandom(2), 3503 + Misc.getRandom(2), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;
		case 11004: // Loadstone teleport
			player.getPacketSender().sendInterface(50100);
			return true;
		case 11008: //Training teleport
			DialogueManager.start(player, 200);
			player.getAttributes().setDialogueAction(205);
			return true;
		case 11011:
			DialogueManager.start(player, 201);
			player.getAttributes().setDialogueAction(206);
			return true;

		case 11017: //Minigame
			DialogueManager.start(player, 326);
			player.getAttributes().setDialogueAction(208);
			return true;
		case 11014: //Boss teles
			DialogueManager.start(player, 277);
			player.getAttributes().setDialogueAction(277);
			return true;
		case 11020: // wild teles
			DialogueManager.start(player, 276);
			player.getAttributes().setDialogueAction(287);
			return true;
			/**
			 * Lodestone Teleports
			 */
		case -15425: //Bandit Camp Teleport
			teleportPlayer(player, new Position(3175 +Misc.getRandom(2), 2980 +Misc.getRandom(2), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15419: //Lumbridge City Teleport
			teleportPlayer(player, new Position(Misc.getRandom(1) == 0 ? 3222 : 3221, Misc.getRandom(1) == 0 ? 3218 : 3219), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15431: //Port Sarim Teleport
			teleportPlayer(player, new Position(3020 +Misc.getRandom(2), 3243 +Misc.getRandom(2), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15416: //Al Kharid Teleport
			teleportPlayer(player, new Position(3293 +Misc.getRandom(2), 3183 +Misc.getRandom(2), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15410: //Draynor Village Teleport
			teleportPlayer(player, new Position(3080 +Misc.getRandom(2), 3250 +Misc.getRandom(2), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15413: //Varrock City
			teleportPlayer(player, new Position(3207 +Misc.getRandom(1), 3429 +Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15406: //Falador City
			teleportPlayer(player, new Position(2962 +Misc.getRandom(2), 3381 +Misc.getRandom(2), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15403: //Edgeville
			teleportPlayer(player, new Position(3087 +Misc.getRandom(1), 3503 +Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15400: //Taverly City
			teleportPlayer(player, new Position(2896 +Misc.getRandom(1), 3457 +Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15397: //Burthorpe
			teleportPlayer(player, new Position(2927 +Misc.getRandom(2), 3558 +Misc.getRandom(2), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15394: //Catherby
			teleportPlayer(player, new Position(2809 +Misc.getRandom(1), 3435 +Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15391: //Seer's Village
			teleportPlayer(player, new Position(2726 +Misc.getRandom(1), 3485 +Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15428: //Ardounge Market
			teleportPlayer(player, new Position(2664 +Misc.getRandom(2), 3309 +Misc.getRandom(2), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15422: //Yanille
			teleportPlayer(player, new Position(2605 +Misc.getRandom(1), 3097 +Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;

		case -15434: //Lunar Isle
			teleportPlayer(player, new Position(2112 +Misc.getRandom(1), 3915 +Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			return true;
		}
		return false;
	}

	public static boolean checkReqs(Player player, Position targetLocation) {
		if(player.getConstitution() <= 0)
			return false;
		if(player.getSkillManager().getSkillAttributes().getAgilityAttributes().isCrossingObstacle())
			return false;
		if(!player.getAttributes().hasStarted())
			return false;
		if(player.getCombatAttributes().getTeleportBlockDelay() > 0) {
			player.getPacketSender().sendMessage("A magical spell is blocking you from teleporting.");
			return false;
		}
		if(player.getLocation() != null && !player.getLocation().canTeleport(player))
			return false;
		return true;
	}

	public static void cancelCurrentActions(Player player) {
		player.getAttributes().setCurrentInteractingItem(null).setCurrentInteractingNPC(null).setResting(false);
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().stopSkilling();
	}
}
