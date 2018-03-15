package org.trident.net.packet.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.FinalizedMovementTask;
import org.trident.engine.task.impl.WalkToAction;
import org.trident.model.Animation;
import org.trident.model.Direction;
import org.trident.model.DwarfCannon;
import org.trident.model.Flag;
import org.trident.model.GameObject;
import org.trident.model.Graphic;
import org.trident.model.MagicSpellbook;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.definitions.GameObjectDefinition;
import org.trident.model.inputhandling.impl.EnterAmountOfLogsToAdd;
import org.trident.model.movement.MovementStatus;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.Constants;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.clip.region.RegionClipping;
import org.trident.world.content.Achievements;
import org.trident.world.content.CrystalChest;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.DwarfMultiCannon;
import org.trident.world.content.Locations;
import org.trident.world.content.WildernessObelisks;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.minigames.impl.ArcheryCompetition;
import org.trident.world.content.minigames.impl.Barrows;
import org.trident.world.content.minigames.impl.Conquest;
import org.trident.world.content.minigames.impl.Dueling;
import org.trident.world.content.minigames.impl.FightCave;
import org.trident.world.content.minigames.impl.FightPit;
import org.trident.world.content.minigames.impl.FishingTrawler;
import org.trident.world.content.minigames.impl.PestControl;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.content.minigames.impl.WarriorsGuild;
import org.trident.world.content.quests.Nomad;
import org.trident.world.content.quests.RecipeForDisaster;
import org.trident.world.content.skills.impl.agility.Agility;
import org.trident.world.content.skills.impl.crafting.Flax;
import org.trident.world.content.skills.impl.dungeoneering.DungeoneeringHandler;
import org.trident.world.content.skills.impl.fishing.Fishing;
import org.trident.world.content.skills.impl.hunter.Hunter;
import org.trident.world.content.skills.impl.mining.Mining;
import org.trident.world.content.skills.impl.mining.MiningData;
import org.trident.world.content.skills.impl.mining.Prospecting;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.content.skills.impl.runecrafting.Runecrafting;
import org.trident.world.content.skills.impl.runecrafting.RunecraftingData;
import org.trident.world.content.skills.impl.smithing.EquipmentMaking;
import org.trident.world.content.skills.impl.smithing.Smelting;
import org.trident.world.content.skills.impl.summoning.PouchMaking;
import org.trident.world.content.skills.impl.thieving.MastersChest;
import org.trident.world.content.skills.impl.thieving.Stalls;
import org.trident.world.content.skills.impl.woodcutting.Woodcutting;
import org.trident.world.content.skills.impl.woodcutting.WoodcuttingData;
import org.trident.world.content.skills.impl.woodcutting.WoodcuttingData.Hatchet;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.content.teleporting.TeleportType;
import org.trident.world.content.treasuretrails.MapScrolls;
import org.trident.world.content.treasuretrails.SearchScrolls;
import org.trident.world.entity.player.Player;

/**
 * This packet listener is called when a player clicked
 * on a game object.
 * 
 * @author relex lawl
 */

public class ObjectActionPacketListener implements PacketListener {

	/**
	 * The PacketListener logger to debug information and print out errors.
	 */
	//private final static Logger logger = Logger.getLogger(ObjectActionPacketListener.class);

	private static void firstClick(final Player player, Packet packet) {
		final int x = packet.readLEShortA();
		final int id = packet.readUnsignedShort();
		final int y = packet.readUnsignedShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final GameObject gameObject = new GameObject(id, position);
		if(id > 0 && id != 6 && !RegionClipping.objectExists(gameObject) && FishingTrawler.getState(player) == null) {
			//player.getPacketSender().sendMessage("An error occured. Error code: "+id).sendMessage("Please report the error to a staff member.");
			return;
		}
		int distanceX = (player.getPosition().getX() - position.getX());
		int distanceY = (player.getPosition().getY() - position.getY());
		if (distanceX < 0)
			distanceX = -(distanceX);
		if (distanceY < 0)
			distanceY = -(distanceY);
		int size = distanceX > distanceY ? GameObjectDefinition.forId(id).getSizeX() : GameObjectDefinition.forId(id).getSizeY();
		if (size <= 0)
			size = 1;
		gameObject.setSize(size);
		player.getAttributes().setCurrentInteractingObject(gameObject);
		if(player.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().isViewingInterface() || player.getMovementQueue().getMovementStatus() == MovementStatus.CANNOT_MOVE)
			return;
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("First click object id; [id, position] : [" + id + ", " + position.toString() + "]");
		if(specialObjects(player, gameObject, id, 1)) //Objects that must receive an action before being ran up to (IE RC Altars)
			return;
		player.getAttributes().setWalkToTask(new WalkToAction(player, position, gameObject, new FinalizedMovementTask() {
			@Override
			public void execute() {
				player.setPositionToFace(gameObject.getPosition());
				//Construction.handleFirstObjectClick(x, y, id, player);
				if(gameObject != null)
					player.setPositionToFace(gameObject.getPosition());
				if(MapScrolls.handleCrate(player, x, y) || SearchScrolls.handleObject(player, gameObject.getPosition()))
					return;
				if(DungeoneeringHandler.handleObject(player, gameObject))
					return;
				if(Agility.handleObject(player, gameObject))
					return;
				if(WoodcuttingData.Trees.forId(id) != null) {
					Woodcutting.cutWood(player, gameObject);
					return;
				}
				if(MiningData.forRock(gameObject.getId()) != null) {
					Mining.startMining(player, gameObject);
					return;
				}
				if (player.getAdvancedSkills().getFarming().click(player, x, y, 1))
					return;
				if(Runecrafting.runecraftingAltar(player, gameObject.getId())) {
					RunecraftingData.RuneData rune = RunecraftingData.RuneData.forId(gameObject.getId());
					if(rune == null)
						return;
					Runecrafting.craftRunes(player, rune);
				}
				if(Barrows.handleObject(player, gameObject))
					return;
				if(player.getLocation() != null && player.getLocation() == Location.WILDERNESS && WildernessObelisks.handleObelisk(gameObject.getId()))
					return;
				SoulWars.handleObjectClick(gameObject.getId(), player);
				if(player.getLocation() == Location.GODWARS_DUNGEON && Locations.GodWarsDungeon.handleObject(player, gameObject, 1))
					return;
				switch (id) {
				case 9294:
					if(player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 80) {
						player.getPacketSender().sendMessage("You need an Agility level of at least 80 to use this shortcut.");
						return;
					}
					player.performAnimation(new Animation(769));
					TaskManager.submit(new Task(1, player, false) {
						@Override
						protected void execute() {
							player.moveTo(new Position(player.getPosition().getX() >= 2880 ? 2878 : 2880, 9813));	
							stop();
						}
					});
					break;
				case 2026:
				case 2028:
				case 2029:
				case 2030:
				case 2031:
					player.setEntityInteraction(gameObject);
					Fishing.setupFishing(player, Fishing.forSpot(gameObject.getId(), false));
					return;
				case 2732:
					EnterAmountOfLogsToAdd.openInterface(player);
					break;
				case 2467:
					player.setDirection(Direction.WEST);
					player.moveTo(new Position(3087, 3510));
					player.getPacketSender().sendMessage("You are teleported to Edgeville..");
					break;
				case 20987:
					player.moveTo(new Position(2209, 5348));
					break;
				case 57258:
					player.moveTo(new Position(2871, 5318, 2));
					player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().setHasEnteredRoom(false);
					break;
				case 1536:
					if(gameObject.getPosition().getY() == 3211) {
						if(player.getPosition().getY() > 3211)
							player.moveTo(new Position(player.getPosition().getX(), 3211));
						else
							player.moveTo(new Position(player.getPosition().getX(), 3212));
					} else
						if(player.getPosition().getY() <= 3225)
							player.moveTo(new Position(player.getPosition().getX(), 3226));
						else
							player.moveTo(new Position(player.getPosition().getX(), 3225));
					break;
				case 19187:
				case 19175:
					Hunter.dismantle(player, gameObject);
					break;
				case 411:
					player.getAttributes().setDialogueAction(285);
					DialogueManager.start(player, 288);
					break;
				case 6552:
					player.getAttributes().setDialogueAction(289);
					DialogueManager.start(player, 300);
					break;
				case 19191:
				case 19189:
				case 19180:
				case 19184:
				case 19182:
				case 19178:
					Hunter.lootTrap(player, gameObject);
					break;
				case 8702:
					Fishing.setupFishing(player, Fishing.Spot.HARPOON2);
					break;
				case 20331:
					if(player.getInventory().getFreeSlots() < 1){
						player.getPacketSender().sendMessage("You need some more inventory space to do this.");
						return;
					}
					if((System.currentTimeMillis() - player.getAttributes().getClickDelay() < 2000) || (System.currentTimeMillis() - player.getCombatAttributes().getLastDamageReceived() < 4000))
						return;
					if (player.getSkillManager().getCurrentLevel(Skill.THIEVING) >= 90) {
						player.setAnimation(new Animation(881));
						player.getSkillManager().addExperience(Skill.THIEVING, 16000, false);
						double i = Math.random();
						if (i >= 0.70) 
							player.getInventory().add(1205, 1); //Bronze Dagger 30%
						else if (i >= 0.45 && i < 0.70)
							player.getInventory().add(1203, 1); //Iron Dagger 25%
						else if (i >= 0.25 && i < 0.45)
							player.getInventory().add(1207, 1); //Steel Dagger 20%
						else if (i >= 0.13 && i < 0.25)
							player.getInventory().add(1209, 1); //Mithril Dagger 12%
						else if (i >= 0.05 && i < 0.13)
							player.getInventory().add(1211, 1); //Adamant Dagger 8%
						else if (i >= 0.005 && i < 0.05)
							player.getInventory().add(1213, 1); //Rune Dagger 4.5%
						else if (i >= 0 && i < 0.005)
							player.getInventory().add(1215, 1); //Dragon Dagger 0.5%
						player.getAttributes().setClickDelay(System.currentTimeMillis());
					} else
						player.getPacketSender().sendMessage("You need a Thieving level of at least 90 to steal from this stall.");
					break;
				case 25339:
				case 25340:
					player.moveTo(new Position(1778, 5346, player.getPosition().getZ() == 0 ? 1 : 0));
					break;
				case 61:
					if(player.getRights() == PlayerRights.DONATOR || player.getRights() == PlayerRights.SUPER_DONATOR) {
						player.getPacketSender().sendMessage("Only Gold members can use this portal.");
						return;
					}
					if(System.currentTimeMillis() - player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getAltarDelay() < 60000) {
						player.getPacketSender().sendMessage("You can only do this once per minute.");
						return;
					}
					player.performAnimation(new Animation(645));
					for(Skill skill : Skill.values()) {
						if(player.getSkillManager().getCurrentLevel(skill) < player.getSkillManager().getMaxLevel(skill))
							player.getSkillManager().setCurrentLevel(skill, player.getSkillManager().getMaxLevel(skill));
					}
					if(player.getPlayerCombatAttributes().getSpecialAttackAmount() < 10) {
						player.getPlayerCombatAttributes().setSpecialAttackAmount(10.0);
						WeaponHandler.update(player);
					}
					player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().setAltarDelay(System.currentTimeMillis());
					break;
				case 5960: //Levers
				case 5959:
					player.setDirection(id == 5960 ? Direction.SOUTH : Direction.WEST);
					TeleportHandler.teleportPlayer(player, id == 5959 ? new Position(2539, 4712) : new Position(3090, 3956), TeleportType.LEVER);
					break;
				case 10091:
					player.getSkillManager().stopSkilling();
					DialogueManager.start(player, DialogueManager.getDialogues().get(405));
					player.getAttributes().setDialogueAction(350);
					break;
				case 2178:
					FishingTrawler.boardBoat(player);
					break;
				case 2179:
					FishingTrawler.leave(player);
					break;
				case 2174:
					FishingTrawler.ShipHandler.goUpLadder(player, x, y);
					break;
				case 2175:
					FishingTrawler.ShipHandler.goDownLadder(player, x, y);
					break;
				case 2167:
					FishingTrawler.ShipHandler.fixHole(player, x, y);
					break;
				case 2164:
				case 2165:
					FishingTrawler.NetHandler.fixNet(player);
					break;
				case 23271:
					player.getMovementQueue().stopMovement();
					boolean cross = player.getPosition().getY() <= 3520;
					player.moveTo(new Position(player.getPosition().getX(), cross? 3523 : 3520));
					break;
				case 38700:
					player.getAttributes().setCurrentInteractingNPC(null);
					if(player.getLocation() == Location.FREE_FOR_ALL_WAIT || player.getLocation() == Location.FREE_FOR_ALL_ARENA) {
						player.moveTo(Constants.DEFAULT_POSITION);
						return;
					}
					DialogueManager.start(player, 389);
					player.getAttributes().setDialogueAction(337);
					break;
				case 172:
					CrystalChest.handleChest(player, gameObject);
					break;
				case 10527:
					if(player.getPosition().getX() == 3426 && player.getPosition().getY() == 3555 && player.getPosition().getZ() == 1) {
						player.moveTo(new Position(3426, 3556, 1));
					}else if(player.getPosition().getX() == 3426 && player.getPosition().getY() == 3556 && player.getPosition().getZ() == 1) {
						player.moveTo(new Position(3426, 3555, 1));
					}
					break;
				case 10529:
					if(player.getPosition().getX() == 3427 && player.getPosition().getY() == 3555 && player.getPosition().getZ() == 1) {
						player.moveTo(new Position(3427, 3556, 1));
					}else if(player.getPosition().getX() == 3427 && player.getPosition().getY() == 3556 && player.getPosition().getZ() == 1) {
						player.moveTo(new Position(3427, 3555, 1));
					}else if(player.getPosition().getX() == 3445 && player.getPosition().getY() == 3555 && player.getPosition().getZ() == 2) {
						player.moveTo(new Position(3445, 3554, 2));
					}else if(player.getPosition().getX() == 3445 && player.getPosition().getY() == 3554 && player.getPosition().getZ() == 2) {
						player.moveTo(new Position(3445, 3555, 2));
					}
					break;
				case 9319:
					if(player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 61){
						player.getPacketSender().sendMessage("You need an Agility level of at least 61 or higher to climb this");
						return;
					}
					if(player.getPosition().getZ() == 0)
					player.moveTo(new Position(3422, 3549, 1));
					else if(player.getPosition().getZ() == 1) {
						if(gameObject.getPosition().getX() == 3447) 
							player.moveTo(new Position(3447, 3575, 2));
						else
							player.moveTo(new Position(3447, 3575, 0));
					}
					break;

				case 9320:
					if(player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 61) {
						player.getPacketSender().sendMessage("You need an Agility level of at least 61 or higher to climb this");
						return;
					}
					if(player.getPosition().getZ() == 1)
						player.moveTo(new Position(3422, 3549, 0));
					else if(player.getPosition().getZ() == 0)
						player.moveTo(new Position(3447, 3575, 1));
					else if(player.getPosition().getZ() == 2)
						player.moveTo(new Position(3447, 3575, 1));
					player.performAnimation(new Animation(828));
					break;

				case 4487:
					if(player.getPosition().getX() == 3428 && player.getPosition().getY() == 3536)
						player.moveTo(new Position(3428, 3535, 0));
					else if(player.getPosition().getX() == 3428 && player.getPosition().getY() == 3535)
						player.moveTo(new Position(3428, 3536, 0));
					break;
				case 4490:
					if(player.getPosition().getX() == 3429 && player.getPosition().getY() == 3535)
						player.moveTo(new Position(3429, 3536, 0));
					else if(player.getPosition().getX() == 3429 && player.getPosition().getY() == 3536)
						player.moveTo(new Position(3429, 3535, 0));
					break;
				case 6910:
				case 4483:
				case 3193:
					player.getBank(player.getAttributes().getCurrentBankTab()).open();
					break;
				case 39515:
					if(player.getRights() == PlayerRights.DONATOR) {
						player.getPacketSender().sendMessage("Only Super and Extreme Donators can use this portal.");
						return;
					}
					player.moveTo(new Position(2830, 9522, 0));
					player.getPacketSender().sendMessage("The Green Dragons here appear to be weakened.");
					break;

				case 2465:
					if(player.getRights() == PlayerRights.PLAYER) {
						player.getPacketSender().sendMessage("Only Donators can use this portal.");
						return;
					}
					player.moveTo(new Position(3422+-Misc.getRandom(2), 2918+-Misc.getRandom(2), 0));
					break;
				case 11356:
					if(player.getRights() == PlayerRights.DONATOR) {
						player.getPacketSender().sendMessage("Only Super and Extreme Donators can use this portal.");
						return;
					}
					player.moveTo(new Position(2861, 9731, 0));
					break;

				case 47180:
					if(player.getRights() == PlayerRights.DONATOR || player.getRights() == PlayerRights.SUPER_DONATOR) {
						player.getPacketSender().sendMessage("Only Extreme Donators can use this portal.");
						return;
					}
					player.moveTo(new Position(2586, 3912, 0));
					break;
				case 1805:
					if(player.getPosition().getX() == 3191 && player.getPosition().getY() == 3362)
						player.moveTo(new Position(3191, 3363));
					else if(player.getPosition().getX() == 3191 && player.getPosition().getY() == 3363)
						player.moveTo(new Position(3191, 3362));
					break;
				case 47976:
					Nomad.endFight(player, false);
					break;
				case 16050:
					Nomad.endFight(player, false);
					break;
				case 9293:
					TaskManager.submit(new Task(1, player, false) {
						@Override
						public void execute() {
							if(player.getPosition().getX() == 2886)
								player.moveTo(new Position(2892, 9799));
							else if(player.getPosition().getX() == 2892)
								player.moveTo(new Position(2886, 9799));
							stop();
						}
					});
					break;
				case 25938:
					player.performAnimation(new Animation(828));
					TaskManager.submit(new Task(1, player, false) {
						@Override
						public void execute() {
							player.moveTo(new Position(2715, 3471, 1));
							stop();
						}
					});
					break;
				case 25939:
					player.performAnimation(new Animation(828));
					TaskManager.submit(new Task(1, player, false) {
						@Override
						public void execute() {
							player.moveTo(new Position(2715, 3471, 0));
							stop();
						}
					});
					break;
				case 11993: //Wizard's tower
					if(player.getPosition().getX() == 3109 && player.getPosition().getY() == 3162 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3110, 3162, 1));
					else if(player.getPosition().getX() == 3110 && player.getPosition().getY() == 3162 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3109, 3162, 1));
					else if(player.getPosition().getX() == 3108 && player.getPosition().getY() == 3159 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3108, 3158, 1));
					else if(player.getPosition().getX() == 3108 && player.getPosition().getY() == 3158 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3108, 3159, 1));
					else if(player.getPosition().getX() == 3109 && player.getPosition().getY() == 3159 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3110, 3159, 1));
					else if(player.getPosition().getX() == 3110 && player.getPosition().getY() == 3159 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3109, 3159, 1));
					else if(player.getPosition().getX() == 3107 && player.getPosition().getY() == 3162 && player.getPosition().getZ() == 2)
						player.moveTo(new Position(3108, 3162, 2));
					else if(player.getPosition().getX() == 3108 && player.getPosition().getY() == 3162 && player.getPosition().getZ() == 2)
						player.moveTo(new Position(3107, 3162, 2));
					else if(player.getPosition().getY() == 3167)
						player.moveTo(new Position(3109, 3166));
					else if(player.getPosition().getY() == 3166)
						player.moveTo(new Position(3109, 3167));
					else if(player.getPosition().getX() == 3108 && player.getPosition().getY() == 3162)
						player.moveTo(new Position(3106, 3162));
					else if(player.getPosition().getX() == 3107 && player.getPosition().getY() == 3163)
						player.moveTo(new Position(3106, 3162));
					else if(player.getPosition().getX() == 3106 && player.getPosition().getY() == 3162)
						player.moveTo(new Position(3108, 3162));
					else if(player.getPosition().getX() == 3107 && player.getPosition().getY() == 3163)
						player.moveTo(new Position(3108, 3162));
					else if(player.getPosition().getY() == 3163)
						player.moveTo(new Position(3111, 3162));
					else if(player.getPosition().getY() == 3162)
						player.moveTo(new Position(3111, 3163));
					break;
				case 12537:
					player.getAttributes().setDialogueAction(292);
					DialogueManager.start(player, 275);
					break;
				case 12538:
				case 12536:
					player.getPacketSender().sendInterfaceRemoval();
					player.moveTo(new Position(3104, 3161, 1));
					break;
				case 5103: //Brimhaven vines
				case 5104:
				case 5105:
				case 5106:
				case 5107:
					if(System.currentTimeMillis() - player.getAttributes().getClickDelay() <= 4000)
						return;
					if(player.getSkillManager().getCurrentLevel(Skill.WOODCUTTING) < 30) {
						player.getPacketSender().sendMessage("You need a Woodcutting level of at least 30 to do this.");
						return;
					}
					if(WoodcuttingData.getHatchet(player) < 0) {
						player.getPacketSender().sendMessage("You do not have a hatchet which you have the required Woodcutting level to use.");
						return;
					}
					final Hatchet axe = Hatchet.forId(WoodcuttingData.getHatchet(player));
					player.performAnimation(new Animation(axe.getAnim()));
					gameObject.setFace(-1);
					TaskManager.submit(new Task(3 + Misc.getRandom(4), player, false) {
						@Override
						protected void execute() {
							if(player.getMovementQueue().isMoving()) {
								stop();
								return;
							}
							int x = 0;
							int y = 0;
							if(player.getPosition().getX() == 2689 && player.getPosition().getY() == 9564) {
								x = 2;
								y = 0;
							} else if(player.getPosition().getX() == 2691 && player.getPosition().getY() == 9564) {
								x = -2;
								y = 0;
							} else if(player.getPosition().getX() == 2683 && player.getPosition().getY() == 9568) {
								x = 0;
								y = 2;
							} else if(player.getPosition().getX() == 2683 && player.getPosition().getY() == 9570) {
								x = 0;
								y = -2;
							} else if(player.getPosition().getX() == 2674 && player.getPosition().getY() == 9479) {
								x = 2;
								y = 0;
							} else if(player.getPosition().getX() == 2676 && player.getPosition().getY() == 9479) {
								x = -2;
								y = 0;
							} else if(player.getPosition().getX() == 2693 && player.getPosition().getY() == 9482) {
								x = 2;
								y = 0;
							} else if(player.getPosition().getX() == 2672 && player.getPosition().getY() == 9499) {
								x = 2;
								y = 0;
							} else if(player.getPosition().getX() == 2674 && player.getPosition().getY() == 9499) {
								x = -2;
								y = 0;
							}
							CustomObjects.objectRespawnTask(player, new GameObject(-1, gameObject.getPosition().copy()), gameObject, 3);
							player.getPacketSender().sendMessage("You chop down the vines..");
							player.getSkillManager().addExperience(Skill.WOODCUTTING, 45, false);
							player.performAnimation(new Animation(65535));
							player.getMovementQueue().walkStep(x, y);
							stop();
						}
					});
					player.getAttributes().setClickDelay(System.currentTimeMillis());
					break;
				case 5099:
				case 5100:
					if(System.currentTimeMillis() - player.getAttributes().getClickDelay() <= 2000)
						return;
					final int req = gameObject.getId() == 5099 ? 30 : 22;
					if(player.getSkillManager().getCurrentLevel(Skill.AGILITY) < req) {
						player.getPacketSender().sendMessage("You need an Agility level of at least "+req+" to do this.");
						return;
					}
					player.getAttributes().setResetPosition(player.getPosition().copy());
					if(req == 30)
						player.getMovementQueue().walkStep(0, player.getPosition().getY() > 9492 ? -1 : 0);
					else
						player.getMovementQueue().walkStep(0, player.getPosition().getY() == 9566 ? 1 : -1);
					TaskManager.submit(new Task(1, player, false) {
						@Override
						protected void execute() {
							if(player.getPosition().getX() == 2698 && player.getPosition().getY() == 9498)
								player.moveTo(new Position(2698, 9492));
							else if(player.getPosition().getX() == 2698 && player.getPosition().getY() == 9492)
								player.moveTo(new Position(2698, 9499));
							else if(player.getPosition().getX() == 2655 && player.getPosition().getY() == 9567)
								player.moveTo(new Position(2655, 9573));
							else if(player.getPosition().getX() == 2655 && player.getPosition().getY() == 9572)
								player.moveTo(new Position(2655, 9566));
							player.getSkillManager().addExperience(Skill.AGILITY, req == 30 ? 45 : 35, false);
							player.getAttributes().setResetPosition(null);
							stop();
						}
					});
					player.getAttributes().setClickDelay(System.currentTimeMillis());
					break;
				case 5110:
				case 5111:
					if(System.currentTimeMillis() - player.getAttributes().getClickDelay() <= 2000)
						return;
					player.performAnimation(new Animation(769));
					TaskManager.submit(new Task(1, player, false) {
						@Override
						protected void execute() {
							player.moveTo(id == 5111 ? new Position(2649, 9562) : new Position(2647, 9557));
							stop();
						}
					});
					player.getAttributes().setClickDelay(System.currentTimeMillis());
					break;
				case 2012:
				case 2015:
				case 26972:
				case 2019:
				case 36786:
				case -30784: // banks
				case -23158:
				case 2213:
				case 11402:
				case 14367:
					player.getAttributes().setCurrentInteractingNPC(null).setDialogueAction(0);
					DialogueManager.start(player, 10);
					if(id == 36786)
						Achievements.handleAchievement(player, Achievements.Tasks.TASK8);
					break;
				case 4493:
					player.moveTo(new Position(3433, 3538, 1));
					break;
				case 4494:
					player.moveTo(new Position(3438, 3538, 0));
					break;
				case 4495:
					player.moveTo(new Position(3417, 3541, 2));
					break;
				case 4496:
					player.moveTo(new Position(3412, 3541, 1));
					break;
				case 36915:
					if(player.getPosition().getX() == 3237 && player.getPosition().getY() == 3285)
						player.moveTo(new Position(3236, 3285, 0));
					else if(player.getPosition().getX() == 3236 && player.getPosition().getY() == 3285)
						player.moveTo(new Position(3237, 3285, 0));

					else if(player.getPosition().getX() == 3252 && player.getPosition().getY() == 3266)
						player.moveTo(new Position(3253, 3266, 0));
					else if(player.getPosition().getX() == 3253 && player.getPosition().getY() == 3266)
						player.moveTo(new Position(3252, 3266, 0));

					else if(player.getPosition().getX() == 3186 && player.getPosition().getY() == 3267)
						player.moveTo(new Position(3185, 3267, 0));
					else if(player.getPosition().getX() == 3185 && player.getPosition().getY() == 3267)
						player.moveTo(new Position(3186, 3267, 0));
					break;

				case 36846:
					if(player.getPosition().getX() >= 3204 && player.getPosition().getX() <= 3207 && player.getPosition().getY() == 3245)
						player.moveTo(new Position(3204, 3244));
					else if(player.getPosition().getX() >= 3204 && player.getPosition().getX() <= 3207 && player.getPosition().getY() == 3244)
						player.moveTo(new Position(3204, 3245));
					else if(player.getPosition().getX() == 3228 && player.getPosition().getY() == 3240 && player.getPosition().getZ() == 0)
						player.moveTo(new Position(3229, 3240, 0));
					else if(player.getPosition().getX() == 3229 && player.getPosition().getY() == 3240 && player.getPosition().getZ() == 0)
						player.moveTo(new Position(3228, 3240, 0));
					else if(player.getPosition().getX() == 3226 && player.getPosition().getY() == 3223)
						player.moveTo(new Position(3227, 3223, 0));
					else if(player.getPosition().getX() == 3227 && player.getPosition().getY() == 3223)
						player.moveTo(new Position(3226, 3223, 0));

					else if(player.getPosition().getX() == 3226 && player.getPosition().getY() == 3214)
						player.moveTo(new Position(3227, 3214, 0));
					else if(player.getPosition().getX() == 3227 && player.getPosition().getY() == 3214)
						player.moveTo(new Position(3226, 3214, 0));

					else if(player.getPosition().getX() == 3208 && player.getPosition().getY() == 3210 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3207, 3210, 1));
					else if(player.getPosition().getX() == 3207 && player.getPosition().getY() == 3210 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3208, 3210, 1));

					else if(player.getPosition().getX() == 3208 && player.getPosition().getY() == 3227 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3207, 3227, 1));
					else if(player.getPosition().getX() == 3207 && player.getPosition().getY() == 3227 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3208, 3227, 1));
					else if(player.getPosition().getX() == 3230 && player.getPosition().getY() == 3236)
						player.moveTo(new Position(3230, 3235, 0));
					else if(player.getPosition().getX() == 3230 && player.getPosition().getY() == 3235)
						player.moveTo(new Position(3230, 3236, 0));

					else if(player.getPosition().getX() == 3214 && player.getPosition().getY() == 3242)
						player.moveTo(new Position(3215, 3242, 1));
					else if(player.getPosition().getX() == 3215 && player.getPosition().getY() == 3242)
						player.moveTo(new Position(3214, 3242, 1));

					else if(player.getPosition().getX() == 3220 && player.getPosition().getY() == 3265)
						player.moveTo(new Position(3219, 3265, 0));
					else if(player.getPosition().getX() == 3219 && player.getPosition().getY() == 3265)
						player.moveTo(new Position(3220, 3265, 0));

					else if(player.getPosition().getX() == 3230 && player.getPosition().getY() == 3291)
						player.moveTo(new Position(3231, 3291, 0));
					else if(player.getPosition().getX() == 3231 && player.getPosition().getY() == 3291)
						player.moveTo(new Position(3230, 3291, 0));

					else if(player.getPosition().getX() == 3224 && player.getPosition().getY() == 3293)
						player.moveTo(new Position(3225, 3293, 0));
					else if(player.getPosition().getX() == 3225 && player.getPosition().getY() == 3293)
						player.moveTo(new Position(3224, 3293, 0));

					else if(player.getPosition().getX() == 3219 && player.getPosition().getY() == 3241)
						player.moveTo(new Position(3218, 3241, 0));
					else if(player.getPosition().getX() == 3218 && player.getPosition().getY() == 3241)
						player.moveTo(new Position(3219, 3241, 0));

					else if(player.getPosition().getX() == 3230 && player.getPosition().getY() == 3239)
						player.moveTo(new Position(3230, 3238, 1));
					else if(player.getPosition().getX() == 3230 && player.getPosition().getY() == 3238)
						player.moveTo(new Position(3230, 3239, 1));

					else if(player.getPosition().getX() == 3229 && player.getPosition().getY() == 3240 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3228, 3240, 1));
					else if(player.getPosition().getX() == 3228 && player.getPosition().getY() == 3240 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3229, 3240, 1));

					else if(player.getPosition().getX() == 3234 && player.getPosition().getY() == 3207)
						player.moveTo(new Position(3233, 3207, 0));
					else if(player.getPosition().getX() == 3233 && player.getPosition().getY() == 3207)
						player.moveTo(new Position(3234, 3207, 0));

					else if(player.getPosition().getX() == 3235 && player.getPosition().getY() == 3199)
						player.moveTo(new Position(3235, 3198, 0));
					else if(player.getPosition().getX() == 3235 && player.getPosition().getY() == 3198)
						player.moveTo(new Position(3235, 3199, 0));

					else if(player.getPosition().getX() == 3194 && player.getPosition().getY() == 3252)
						player.moveTo(new Position(3195, 3252, 1));
					else if(player.getPosition().getX() == 3195 && player.getPosition().getY() == 3252)
						player.moveTo(new Position(3194, 3252, 1));

					else if(player.getPosition().getX() == 3201 && player.getPosition().getY() == 3241)
						player.moveTo(new Position(3202, 3241, 1));
					else if(player.getPosition().getX() == 3202 && player.getPosition().getY() == 3241)
						player.moveTo(new Position(3201, 3241, 1));

					else if(player.getPosition().getX() == 3189 && player.getPosition().getY() == 3275)
						player.moveTo(new Position(3189, 3276, 0));
					else if(player.getPosition().getX() == 3189 && player.getPosition().getY() == 3276)
						player.moveTo(new Position(3189, 3275, 0));

					else if(player.getPosition().getX() == 3188 && player.getPosition().getY() == 3272)
						player.moveTo(new Position(3187, 3272, 0));
					else if(player.getPosition().getX() == 3187 && player.getPosition().getY() == 3272)
						player.moveTo(new Position(3188, 3272, 0));
					break;
				case 36844:
					if(player.getPosition().getX() == 3208 && player.getPosition().getY() == 3222 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3207, 3222, 1));
					else if(player.getPosition().getX() == 3207 && player.getPosition().getY() == 3222 && player.getPosition().getZ() == 1)
						player.moveTo(new Position(3208, 3222, 1));
					break;
				case 36771:
					if(player.getPosition().getX() == 3207 && player.getPosition().getY() == 3224 && player.getPosition().getZ() == 2) {
						player.moveTo(new Position(3207, 3222, 3));
						Achievements.handleAchievement(player, Achievements.Tasks.TASK16);
					}	
					break;
				case 36772:
					if(player.getPosition().getX() == 3207 && player.getPosition().getY() == 3222 && player.getPosition().getZ() == 3)
						player.moveTo(new Position(3207, 3224, 2));
					break;
				case 37335:
					if(player.getPosition().getX() == 3210 && player.getPosition().getY() == 3217 && player.getPosition().getZ() == 3)
						player.getPacketSender().sendMessage("The wrench appears to be stuck.");
					break;

				case 35551:
					if(player.getPosition().getX() == 3267 && player.getPosition().getY() == 3228){
						player.getPacketSender().sendMessage("You step through the gate to Al Kharid.");
						player.moveTo(new Position(3268, 3228, 0));
					}
					else if(player.getPosition().getX() == 3268 && player.getPosition().getY() == 3228)
						player.moveTo(new Position(3267, 3228, 0));
					break;

				case 35549:
					if(player.getPosition().getX() == 3267 && player.getPosition().getY() == 3227){
						player.getPacketSender().sendMessage("You step through the gate to Al Kharid.");
						player.moveTo(new Position(3268, 3227, 0));
					}
					else if(player.getPosition().getX() == 3268 && player.getPosition().getY() == 3227)
						player.moveTo(new Position(3267, 3227, 0));
					break;

				case 1530:
					if(player.getPosition().getX() == 3157 && player.getPosition().getY() == 9640)
						player.moveTo(new Position(3158, 9639, 0));
					else if(player.getPosition().getX() == 3158 && player.getPosition().getY() == 9639)
						player.moveTo(new Position(3157, 9640, 0));

					if(player.getPosition().getX() == 3158 && player.getPosition().getY() == 9641)
						player.moveTo(new Position(3159, 9640, 0));
					else if(player.getPosition().getX() == 3241 && player.getPosition().getY() == 3214)
						player.moveTo(new Position(3158, 9641, 0));
					break;
				case 36984:
					if(player.getPosition().getX() == 3240 && player.getPosition().getY() == 3213)
						player.moveTo(new Position(3240, 3213, 1));
					else if(player.getPosition().getX() == 3241 && player.getPosition().getY() == 3214)
						player.moveTo(new Position(3240, 3213, 1));
					else if(player.getPosition().getX() == 3242 && player.getPosition().getY() == 3213)
						player.moveTo(new Position(3240, 3213, 1));
					break;
				case 36986:
					if(player.getPosition().getX() == 3245 && player.getPosition().getY() == 3213)
						player.moveTo(new Position(3247, 3213, 1));
					else if(player.getPosition().getX() == 3246 && player.getPosition().getY() == 3214)
						player.moveTo(new Position(3247, 3213, 1));
					else if(player.getPosition().getX() == 3247 && player.getPosition().getY() == 3213)
						player.moveTo(new Position(3247, 3213, 1));
					break;
				case 36985:
					if(player.getPosition().getX() == 3240 && player.getPosition().getY() == 3213)
						player.moveTo(new Position(3240, 3213, 0));
					break;
				case 36987:
					if(player.getPosition().getX() == 3247 && player.getPosition().getY() == 3213)
						player.moveTo(new Position(3245, 3213, 0));
					break;
				case 36988:
					if(player.getPosition().getX() == 3246 && player.getPosition().getY() == 3207)
						player.moveTo(new Position(3245, 3206, 2));
					break;
				case 36990:
					if(player.getPosition().getX() == 3245 && player.getPosition().getY() == 3206)
						player.moveTo(new Position(3246, 3207, 1));
					break;
				case 36991:
					if(player.getPosition().getX() == 3242 && player.getPosition().getY() == 3206)
						player.moveTo(new Position(3241, 3207, 1));
					break;
				case 36989:
					if(player.getPosition().getX() == 3241 && player.getPosition().getY() == 3207)
						player.moveTo(new Position(3242, 3206, 2));
					break;
				case -28562:
					if(player.getInventory().contains(1351)){
						player.getPacketSender().sendMessage("You already have a Bronze Pickaxe.");
					}else 
						player.getInventory().add(1351, 1);
					break;
				case 5493:
					player.moveTo(new Position(3165, 3251, 0));
					break;
				case 5492:
					if(player.getSkillManager().getCurrentLevel(Skill.THIEVING) < 20) {
						player.getPacketSender().sendMessage("You need a Thieving level of at least 20 to go down there.");
						return;
					}
					player.performAnimation(new Animation(827));
					player.getPacketSender().sendMessage("You enter the trapdoor..");
					TaskManager.submit(new Task(1, player, false) {
						@Override
						public void execute() {
							player.moveTo(new Position(3149, 9652, 0));
							stop();
						}
					});
					break;
				case 4039:
					player.performAnimation(new Animation(827));
					player.getPacketSender().sendMessage("You enter the trapdoor..");
					TaskManager.submit(new Task(1, player, false) {
						@Override
						public void execute() {
							player.moveTo(new Position(2209, 5348, 0));
							stop();
						}
					});
					break;
				case 28714:
					player.performAnimation(new Animation(828));
					TaskManager.submit(new Task(1, player, false) {
						@Override
						public void execute() {
							player.moveTo(new Position(2926, 3444, 0));
							stop();
						}
					});
					break;
				case 36776:
					player.moveTo(new Position(3205, 3228, 1));
					break;
				case 36777:
					DialogueManager.start(player, 275);
					player.getAttributes().setDialogueAction(225);
					break;
				case 2182:
					player.getBank(player.getAttributes().getCurrentBankTab()).open();
					break;
				case 12354:
				case 12356:
					if(!player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0)) {
						player.getPacketSender().sendMessage("You have no business in there.");
						return;
					}
					if(player.getPosition().getZ() > 0) {
						if(player.getAttributes().getRegionInstance() != null)
							player.getAttributes().getRegionInstance().destruct();
						player.moveTo(new Position(3207, 3215, 0));
					} else
						RecipeForDisaster.enterRFD(player);
					break;
				case 12353:
				case 12352:
					if(player.getPosition().getX() == 1866)
						player.getMovementQueue().walkStep(2, 0);
					else if(player.getPosition().getX() == 1868)
						player.getMovementQueue().walkStep(-2, 0);
					break;
				case 12348:
				case 12351:
					if(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() == 6 || !player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0)) {
						if(player.getPosition().getY() == 3218) 
							player.moveTo(new Position(3207, 3217));
						else if(player.getPosition().getY() == 3217) 
							player.moveTo(new Position(3207, 3218));
						return;
					}
					if(player.getPosition().getY() < 5000) {
						player.moveTo(new Position(1866, 5317));
						if(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0) && !player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(1)) {
							DialogueManager.start(player, 272);
							player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().setPartFinished(1, true);
						}
					} else
						player.moveTo(new Position(3207, 3217));
					break;
				case 12349:
					if(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() == 6 || !player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0)) {
						if(player.getPosition().getX() == 3213) 
							player.moveTo(new Position(3212, 3221));
						else if(player.getPosition().getX() == 3212) 
							player.moveTo(new Position(3213, 3221));
						return;
					}
					if(player.getPosition().getY() < 5000) {
						player.moveTo(new Position(1866, 5323));
						if(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0) && !player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(1)) {
							DialogueManager.start(player, 272);
							player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().setPartFinished(1, true);
						}
					} else
						player.moveTo(new Position(3213, 3222));
					break;
				case 12350:
					if(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() == 6 || !player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0)) {
						if(player.getPosition().getX() == 3213) 
							player.moveTo(new Position(3212, 3222));
						else if(player.getPosition().getX() == 3212) 
							player.moveTo(new Position(3213, 3222));
						return;
					}
					if(player.getPosition().getY() < 5000) {
						player.moveTo(new Position(1866, 5324));
						if(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0) && !player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(1)) {
							DialogueManager.start(player, 272);
							player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().setPartFinished(1, true);
						}
					} else
						player.moveTo(new Position(3213, 3221));
					break;
				case 12389:
					if((player.getPosition().getX() == 3115) && (player.getPosition().getY() == 3452)) {
						player.moveTo(new Position(3117, 9852));
					}
					break;
				case 1804:
					if ((player.getPosition().getX() == 3115) && (player.getPosition().getY() == 3449)) {
						player.moveTo(new Position(3115, 3450));
					}
					else if 
					((player.getPosition().getX() == 3115) && (player.getPosition().getY() == 3450)) {
						player.moveTo(new Position(3115, 3449));
					}
					break;
				case 25336:
					player.getPacketSender().sendMessage("These stairs do not lead anywhere.");
					break;
				case 1754:
					player.moveTo(new Position(3210, 9616));
					break;
				case 29355:
					if ((player.getPosition().getX() == 3115) && (player.getPosition().getY() == 9852) || (player.getPosition().getX() == 3117) && (player.getPosition().getY() == 9852)) {
						player.moveTo(new Position(3115, 3452));

					} else if ((player.getPosition().getX() == 3209) && (player.getPosition().getY() == 9617) || (player.getPosition().getX() == 3208) && (player.getPosition().getY() == 9616)) {
						player.performAnimation(new Animation(828));
						player.moveTo(new Position(3210, 3216));
					}
					break;
				case 3203:
					if(player.getLocation() == Location.DUEL_ARENA && player.getDueling().duelingStatus == 5) {
						if(player.getDueling().selectedDuelRules[Dueling.DuelRule.NO_FORFEIT.ordinal()]) {
							player.getPacketSender().sendMessage("Forfeiting has been disabled in this duel.");			
							return;
						}
						CombatHandler.resetAttack(player);
						if(player.getDueling().duelingWith > -1) {
							Player duelEnemy = World.getPlayers().get(player.getDueling().duelingWith);
							if(duelEnemy == null)
								return;
							CombatHandler.resetAttack(duelEnemy);
							duelEnemy.getMovementQueue().stopMovement();
							duelEnemy.getDueling().duelVictory();
						}
						player.moveTo(new Position(3368 + Misc.getRandom(5), 3267+ Misc.getRandom(3), 0));
						player.getDueling().reset();
						player.getMovementQueue().setMovementStatus(MovementStatus.NONE);
						TeleportHandler.cancelCurrentActions(player);
						player.getAttributes().setRunEnergy(100);
						player.getPlayerCombatAttributes().setSpecialAttackAmount(10.0);
						WeaponHandler.update(player);
						CurseHandler.deactivateAll(player);
						PrayerHandler.deactivateAll(player);
						for (Skill skill : Skill.values())
							player.getSkillManager().setCurrentLevel(skill, player.getSkillManager().getMaxLevel(skill));
					}
					break;
				case 9391: 
					FightPit.viewOrb(player);
					break;
				case 1738:
					if(player.getLocation() == Location.LUMBRIDGE && player.getPosition().getZ() == 0) {
						player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), 1));
					} else {
						player.moveTo(new Position(2840, 3539, 2));
					}
					break;
				case 1739:
					DialogueManager.start(player, 275);
					player.getAttributes().setDialogueAction(226);
					break;
				case 1740:
					if(player.getLocation() == Location.LUMBRIDGE && player.getPosition().getZ() == 2)
						player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), 1));
					break;
				case 15638:
					player.moveTo(new Position(2840, 3539, 0));
					break;
				case 15653: //Warriors guild door entrance
					if(player.getPosition().getX() == 2877) {
						int total = player.getSkillManager().getMaxLevel(Skill.ATTACK) + player.getSkillManager().getMaxLevel(Skill.STRENGTH);
						boolean has99 = player.getSkillManager().getMaxLevel(Skill.ATTACK) >= 99 || player.getSkillManager().getMaxLevel(Skill.STRENGTH) >= 99;
						if(total < 130 && !has99) {
							player.getPacketSender().sendMessage("");
							player.getPacketSender().sendMessage("You do not meet the requirements of a Warrior.");
							player.getPacketSender().sendMessage("You need to have a total Attack and Strength level of at least 140.");
							player.getPacketSender().sendMessage("Having level 99 in either is fine aswell.");
							return;
						}
						player.moveTo(new Position(2876, 3546, 0));
						Achievements.handleAchievement(player, Achievements.Tasks.TASK23);
					} else if(player.getPosition().getX() == 2876)
						player.moveTo(new Position(2877, 3546, 0));
					break;
				case 15644:
				case 15641:
					switch(player.getPosition().getZ()) {
					case 0:
						if(player.getPosition().getX() == 2855 && player.getPosition().getY() == 3546)
							player.moveTo(new Position(2855, 3545));
						else if(player.getPosition().getX() == 2855 && player.getPosition().getY() == 3545)
							player.moveTo(new Position(2855, 3546));
						else if(player.getPosition().getX() == 2854 && player.getPosition().getY() == 3546)
							player.moveTo(new Position(2854, 3545));
						else if(player.getPosition().getX() == 2854 && player.getPosition().getY() == 3545)
							player.moveTo(new Position(2854, 3546));
						break;
					case 2:
						if(player.getPosition().getX() == 2846) {
							if(player.getInventory().getAmount(8851) < 70) {
								player.getPacketSender().sendMessage("You need at least 70 tokens to enter this area.");
								return;
							}
							if(!player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[1])
								DialogueManager.start(player, WarriorsGuild.warriorsGuildDialogue(player));
							player.moveTo(new Position(2847, player.getPosition().getY(), 2));
							WarriorsGuild.handleTokenRemoval(player);
						} else if(player.getPosition().getX() == 2847) {
							WarriorsGuild.resetCyclopsCombat(player);
							player.moveTo(new Position(2846, player.getPosition().getY(), 2));
							player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[2] = false;
						}
						break;
					}
					break;
				case 9369:
					if (player.getPosition().getY() > 5175) {
						FightPit.addPlayer(player);
					} else {
						FightPit.removePlayer(player, "leave room");
					}
					break;
				case 9368:
					if (player.getPosition().getY() < 5169) {
						FightPit.removePlayer(player, "leave game");
					}
					break;
				case 11409:
					if(player.getPosition().getX() > 2410) {
						player.moveTo(new Position(2410, player.getPosition().getY(), 0));
					} else {
						player.moveTo(new Position(2418, player.getPosition().getY(), 0));
					}
					break;
				case 9357:
					FightCave.leaveCave(player, false);
					break;
				case 9356:
					DialogueManager.start(player, DialogueManager.getDialogues().get(246));
					player.getAttributes().setDialogueAction(221);
					break;
				case 14296:
					player.moveTo(new Position( player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ() + 1));
					break;
				case 14235:
				case 5096:
					if (gameObject.getPosition().getX() == 2644 && gameObject.getPosition().getY() == 9593)
						player.moveTo(new Position(2649, 9591));
					break;

				case 5094:
					if (gameObject.getPosition().getX() == 2648 && gameObject.getPosition().getY() == 9592)
						player.moveTo(new Position(2643, 9594, 2));
					break;

				case 5098:
					if (gameObject.getPosition().getX() == 2635 && gameObject.getPosition().getY() == 9511)
						player.moveTo(new Position(2637, 9517));
					break;

				case 5097:
					if (gameObject.getPosition().getX() == 2635 && gameObject.getPosition().getY() == 9514)
						player.moveTo(new Position(2636, 9510, 2));
					break;

				case 14233:
					if(player.getPosition().getX() == 2642) {
						player.moveTo(new Position(2643, player.getPosition().getY(), player.getPosition().getZ()));
					} else if(player.getPosition().getX() == 2643) {
						player.moveTo(new Position(2642, player.getPosition().getY(), player.getPosition().getZ()));
					}
					if(player.getPosition().getX() == 2670) {
						player.moveTo(new Position(2671, player.getPosition().getY(), player.getPosition().getZ()));
					} else if(player.getPosition().getX() == 2671) {
						player.moveTo(new Position(2670, player.getPosition().getY(), player.getPosition().getZ()));
					}
					if(player.getPosition().getY() == 2585) {
						player.moveTo(new Position(player.getPosition().getX(), 2584, player.getPosition().getZ()));
					} else if(player.getPosition().getY() == 2584) {
						player.moveTo(new Position(player.getPosition().getX(), 2585, player.getPosition().getZ()));
					}
					break;
					/**
					 *** Chaos tunnel portals
					 **/
				case 28779:
					if(gameObject.getPosition().getX() == 3254 && gameObject.getPosition().getY() == 5451) {
						player.moveTo(new Position(3250, 5448, 0));
					}
					if(gameObject.getPosition().getX() == 3250 && gameObject.getPosition().getY() == 5448) {
						player.moveTo(new Position(3254, 5451, 0));
					}
					if(gameObject.getPosition().getX() == 3241 && gameObject.getPosition().getY() == 5445) {
						player.moveTo(new Position(3233, 5445, 0));
					}
					if(gameObject.getPosition().getX() == 3233 && gameObject.getPosition().getY() == 5445) {
						player.moveTo(new Position(3241, 5445, 0));
					}
					if(gameObject.getPosition().getX() == 3259 && gameObject.getPosition().getY() == 5446) {
						player.moveTo(new Position(3265, 5491, 0));
					}
					if(gameObject.getPosition().getX() == 3265 && gameObject.getPosition().getY() == 5491) {
						player.moveTo(new Position(3259, 5446, 0));
					}
					if(gameObject.getPosition().getX() == 3260 && gameObject.getPosition().getY() == 5491) {
						player.moveTo(new Position(3266, 5446, 0));
					}
					if(gameObject.getPosition().getX() == 3266 && gameObject.getPosition().getY() == 5446) {
						player.moveTo(new Position(3260, 5491, 0));
					}
					if(gameObject.getPosition().getX() == 3241 && gameObject.getPosition().getY() == 5469) {
						player.moveTo(new Position(3233, 5470, 0));
					}
					if(gameObject.getPosition().getX() == 3233 && gameObject.getPosition().getY() == 5470) {
						player.moveTo(new Position(3241, 5469, 0));
					}
					if(gameObject.getPosition().getX() == 3235 && gameObject.getPosition().getY() == 5457) {
						player.moveTo(new Position(3229, 5454, 0));
					}
					if(gameObject.getPosition().getX() == 3229 && gameObject.getPosition().getY() == 5454) {
						player.moveTo(new Position(3235, 5457, 0));
					}
					if(gameObject.getPosition().getX() == 3280 && gameObject.getPosition().getY() == 5460) {
						player.moveTo(new Position(3273, 5460, 0));
					}
					if(gameObject.getPosition().getX() == 3273 && gameObject.getPosition().getY() == 5460) {
						player.moveTo(new Position(3280, 5460, 0));
					}
					if(gameObject.getPosition().getX() == 3283 && gameObject.getPosition().getY() == 5448) {
						player.moveTo(new Position(3287, 5448, 0));
					}
					if(gameObject.getPosition().getX() == 3287 && gameObject.getPosition().getY() == 5448) {
						player.moveTo(new Position(3283, 5448, 0));
					}
					if(gameObject.getPosition().getX() == 3244 && gameObject.getPosition().getY() == 5495) {
						player.moveTo(new Position(3239, 5498, 0));
					}
					if(gameObject.getPosition().getX() == 3239 && gameObject.getPosition().getY() == 5498) {
						player.moveTo(new Position(3244, 5495, 0));
					}
					if(gameObject.getPosition().getX() == 3232 && gameObject.getPosition().getY() == 5501) {
						player.moveTo(new Position(3238, 5507, 0));
					}
					if(gameObject.getPosition().getX() == 3238 && gameObject.getPosition().getY() == 5507) {
						player.moveTo(new Position(3232, 5501, 0));
					}
					if(gameObject.getPosition().getX() == 3218 && gameObject.getPosition().getY() == 5497) {
						player.moveTo(new Position(3222, 5488, 0));
					}
					if(gameObject.getPosition().getX() == 3222 && gameObject.getPosition().getY() == 5488) {
						player.moveTo(new Position(3218, 5497, 0));
					}
					if(gameObject.getPosition().getX() == 3218 && gameObject.getPosition().getY() == 5478) {
						player.moveTo(new Position(3215, 5475, 0));
					}
					if(gameObject.getPosition().getX() == 3215 && gameObject.getPosition().getY() == 5475) {
						player.moveTo(new Position(3218, 5478, 0));
					}
					if(gameObject.getPosition().getX() == 3224 && gameObject.getPosition().getY() == 5479) {
						player.moveTo(new Position(3222, 5474, 0));
					}
					if(gameObject.getPosition().getX() == 3222 && gameObject.getPosition().getY() == 5474) {
						player.moveTo(new Position(3224, 5479, 0));
					}
					if(gameObject.getPosition().getX() == 3208 && gameObject.getPosition().getY() == 5471) {
						player.moveTo(new Position(3210, 5477, 0));
					}
					if(gameObject.getPosition().getX() == 3210 && gameObject.getPosition().getY() == 5477) {
						player.moveTo(new Position(3208, 5471, 0));
					}
					if(gameObject.getPosition().getX() == 3214 && gameObject.getPosition().getY() == 5456) {
						player.moveTo(new Position(3212, 5452, 0));
					}
					if(gameObject.getPosition().getX() == 3212 && gameObject.getPosition().getY() == 5452) {
						player.moveTo(new Position(3214, 5456, 0));
					}
					if(gameObject.getPosition().getX() == 3204 && gameObject.getPosition().getY() == 5445) {
						player.moveTo(new Position(3197, 5448, 0));
					}
					if(gameObject.getPosition().getX() == 3197 && gameObject.getPosition().getY() == 5448) {
						player.moveTo(new Position(3204, 5445, 0));
					}
					if(gameObject.getPosition().getX() == 3189 && gameObject.getPosition().getY() == 5444) {
						player.moveTo(new Position(3187, 5460, 0));
					}
					if(gameObject.getPosition().getX() == 3187 && gameObject.getPosition().getY() == 5460) {
						player.moveTo(new Position(3189, 5444, 0));
					}
					if(gameObject.getPosition().getX() == 3192 && gameObject.getPosition().getY() == 5472) {
						player.moveTo(new Position(3186, 5472, 0));
					}
					if(gameObject.getPosition().getX() == 3186 && gameObject.getPosition().getY() == 5472) {
						player.moveTo(new Position(3192, 5472, 0));
					}
					if(gameObject.getPosition().getX() == 3185 && gameObject.getPosition().getY() == 5478) {
						player.moveTo(new Position(3191, 5482, 0));
					}
					if(gameObject.getPosition().getX() == 3191 && gameObject.getPosition().getY() == 5482) {
						player.moveTo(new Position(3185, 5478, 0));
					}
					if(gameObject.getPosition().getX() == 3171 && gameObject.getPosition().getY() == 5473) {
						player.moveTo(new Position(3167, 5471, 0));
					}
					if(gameObject.getPosition().getX() == 3167 && gameObject.getPosition().getY() == 5471) {
						player.moveTo(new Position(3171, 5473, 0));
					}
					if(gameObject.getPosition().getX() == 3171 && gameObject.getPosition().getY() == 5478) {
						player.moveTo(new Position(3167, 5478, 0));
					}
					if(gameObject.getPosition().getX() == 3167 && gameObject.getPosition().getY() == 5478) {
						player.moveTo(new Position(3171, 5478, 0));
					}
					if(gameObject.getPosition().getX() == 3168 && gameObject.getPosition().getY() == 5456) {
						player.moveTo(new Position(3178, 5460, 0));
					}
					if(gameObject.getPosition().getX() == 3178 && gameObject.getPosition().getY() == 5460) {
						player.moveTo(new Position(3168, 5456, 0));
					}
					if(gameObject.getPosition().getX() == 3191 && gameObject.getPosition().getY() == 5495) {
						player.moveTo(new Position(3194, 5490, 0));
					}
					if(gameObject.getPosition().getX() == 3194 && gameObject.getPosition().getY() == 5490) {
						player.moveTo(new Position(3191, 5495, 0));
					}
					if(gameObject.getPosition().getX() == 3141 && gameObject.getPosition().getY() == 5480) {
						player.moveTo(new Position(3142, 5489, 0));
					}
					if(gameObject.getPosition().getX() == 3142 && gameObject.getPosition().getY() == 5489) {
						player.moveTo(new Position(3141, 5480, 0));
					}
					if(gameObject.getPosition().getX() == 3142 && gameObject.getPosition().getY() == 5462) {
						player.moveTo(new Position(3154, 5462, 0));
					}
					if(gameObject.getPosition().getX() == 3154 && gameObject.getPosition().getY() == 5462) {
						player.moveTo(new Position(3142, 5462, 0));
					}
					if(gameObject.getPosition().getX() == 3143 && gameObject.getPosition().getY() == 5443) {
						player.moveTo(new Position(3155, 5449, 0));
					}
					if(gameObject.getPosition().getX() == 3155 && gameObject.getPosition().getY() == 5449) {
						player.moveTo(new Position(3143, 5443, 0));
					}
					if(gameObject.getPosition().getX() == 3307 && gameObject.getPosition().getY() == 5496) {
						player.moveTo(new Position(3317, 5496, 0));
					}
					if(gameObject.getPosition().getX() == 3317 && gameObject.getPosition().getY() == 5496) {
						player.moveTo(new Position(3307, 5496, 0));
					}
					if(gameObject.getPosition().getX() == 3318 && gameObject.getPosition().getY() == 5481) {
						player.moveTo(new Position(3322, 5480, 0));
					}
					if(gameObject.getPosition().getX() == 3322 && gameObject.getPosition().getY() == 5480) {
						player.moveTo(new Position(3318, 5481, 0));
					}
					if(gameObject.getPosition().getX() == 3299 && gameObject.getPosition().getY() == 5484) {
						player.moveTo(new Position(3303, 5477, 0));
					}
					if(gameObject.getPosition().getX() == 3303 && gameObject.getPosition().getY() == 5477) {
						player.moveTo(new Position(3299, 5484, 0));
					}
					if(gameObject.getPosition().getX() == 3286 && gameObject.getPosition().getY() == 5470) {
						player.moveTo(new Position(3285, 5474, 0));
					}
					if(gameObject.getPosition().getX() == 3285 && gameObject.getPosition().getY() == 5474) {
						player.moveTo(new Position(3286, 5470, 0));
					}
					if(gameObject.getPosition().getX() == 3290 && gameObject.getPosition().getY() == 5463) {
						player.moveTo(new Position(3302, 5469, 0));
					}
					if(gameObject.getPosition().getX() == 3302 && gameObject.getPosition().getY() == 5469) {
						player.moveTo(new Position(3290, 5463, 0));
					}
					if(gameObject.getPosition().getX() == 3296 && gameObject.getPosition().getY() == 5455) {
						player.moveTo(new Position(3299, 5450, 0));
					}
					if(gameObject.getPosition().getX() == 3299 && gameObject.getPosition().getY() == 5450) {
						player.moveTo(new Position(3296, 5455, 0));
					}
					if(gameObject.getPosition().getX() == 3280 && gameObject.getPosition().getY() == 5501) {
						player.moveTo(new Position(3285, 5508, 0));
					}
					if(gameObject.getPosition().getX() == 3285 && gameObject.getPosition().getY() == 5508) {
						player.moveTo(new Position(3280, 5501, 0));
					}
					if(gameObject.getPosition().getX() == 3300 && gameObject.getPosition().getY() == 5514) {
						player.moveTo(new Position(3297, 5510, 0));
					}
					if(gameObject.getPosition().getX() == 3297 && gameObject.getPosition().getY() == 5510) {
						player.moveTo(new Position(3300, 5514, 0));
					}
					if(gameObject.getPosition().getX() == 3289 && gameObject.getPosition().getY() == 5533) {
						player.moveTo(new Position(3288, 5536, 0));
					}
					if(gameObject.getPosition().getX() == 3288 && gameObject.getPosition().getY() == 5536) {
						player.moveTo(new Position(3289, 5533, 0));
					}
					if(gameObject.getPosition().getX() == 3285 && gameObject.getPosition().getY() == 5527) {
						player.moveTo(new Position(3282, 5531, 0));
					}
					if(gameObject.getPosition().getX() == 3282 && gameObject.getPosition().getY() == 5531) {
						player.moveTo(new Position(3285, 5527, 0));
					}
					if(gameObject.getPosition().getX() == 3325 && gameObject.getPosition().getY() == 5518) {
						player.moveTo(new Position(3323, 5531, 0));
					}
					if(gameObject.getPosition().getX() == 3323 && gameObject.getPosition().getY() == 5531) {
						player.moveTo(new Position(3325, 5518, 0));
					}
					if(gameObject.getPosition().getX() == 3299 && gameObject.getPosition().getY() == 5533) {
						player.moveTo(new Position(3297, 5536, 0));
					}
					if(gameObject.getPosition().getX() == 3297 && gameObject.getPosition().getY() == 5536) {
						player.moveTo(new Position(3299, 5533, 0));
					}
					if(gameObject.getPosition().getX() == 3321 && gameObject.getPosition().getY() == 5554) {
						player.moveTo(new Position(3315, 5552, 0));
					}
					if(gameObject.getPosition().getX() == 3315 && gameObject.getPosition().getY() == 5552) {
						player.moveTo(new Position(3321, 5554, 0));
					}
					if(gameObject.getPosition().getX() == 3291 && gameObject.getPosition().getY() == 5555) {
						player.moveTo(new Position(3285, 5556, 0));
					}
					if(gameObject.getPosition().getX() == 3285 && gameObject.getPosition().getY() == 5556) {
						player.moveTo(new Position(3291, 5555, 0));
					}
					if(gameObject.getPosition().getX() == 3266 && gameObject.getPosition().getY() == 5552) {
						player.moveTo(new Position(3262, 5552, 0));
					}
					if(gameObject.getPosition().getX() == 3262 && gameObject.getPosition().getY() == 5552) {
						player.moveTo(new Position(3266, 5552, 0));
					}
					if(gameObject.getPosition().getX() == 3256 && gameObject.getPosition().getY() == 5561) {
						player.moveTo(new Position(3253, 5561, 0));
					}
					if(gameObject.getPosition().getX() == 3253 && gameObject.getPosition().getY() == 5561) {
						player.moveTo(new Position(3256, 5561, 0));
					}
					if(gameObject.getPosition().getX() == 3249 && gameObject.getPosition().getY() == 5546) {
						player.moveTo(new Position(3252, 5543, 0));
					}
					if(gameObject.getPosition().getX() == 3252 && gameObject.getPosition().getY() == 5543) {
						player.moveTo(new Position(3249, 5546, 0));
					}
					if(gameObject.getPosition().getX() == 3261 && gameObject.getPosition().getY() == 5536) {
						player.moveTo(new Position(3268, 5534, 0));
					}
					if(gameObject.getPosition().getX() == 3268 && gameObject.getPosition().getY() == 5534) {
						player.moveTo(new Position(3261, 5536, 0));
					}
					if(gameObject.getPosition().getX() == 3243 && gameObject.getPosition().getY() == 5526) {
						player.moveTo(new Position(3241, 5529, 0));
					}
					if(gameObject.getPosition().getX() == 3241 && gameObject.getPosition().getY() == 5529) {
						player.moveTo(new Position(3243, 5526, 0));
					}
					if(gameObject.getPosition().getX() == 3230 && gameObject.getPosition().getY() == 5547) {
						player.moveTo(new Position(3226, 5553, 0));
					}
					if(gameObject.getPosition().getX() == 3226 && gameObject.getPosition().getY() == 5553) {
						player.moveTo(new Position(3230, 5547, 0));
					}
					if(gameObject.getPosition().getX() == 3206 && gameObject.getPosition().getY() == 5553) {
						player.moveTo(new Position(3204, 5546, 0));
					}
					if(gameObject.getPosition().getX() == 3204 && gameObject.getPosition().getY() == 5546) {
						player.moveTo(new Position(3206, 5553, 0));
					}
					if(gameObject.getPosition().getX() == 3211 && gameObject.getPosition().getY() == 5533) {
						player.moveTo(new Position(3214, 5533, 0));
					}
					if(gameObject.getPosition().getX() == 3214 && gameObject.getPosition().getY() == 5533) {
						player.moveTo(new Position(3211, 5533, 0));
					}
					if(gameObject.getPosition().getX() == 3208 && gameObject.getPosition().getY() == 5527) {
						player.moveTo(new Position(3211, 5523, 0));
					}
					if(gameObject.getPosition().getX() == 3211 && gameObject.getPosition().getY() == 5523) {
						player.moveTo(new Position(3208, 5527, 0));
					}
					if(gameObject.getPosition().getX() == 3201 && gameObject.getPosition().getY() == 5531) {
						player.moveTo(new Position(3197, 5529, 0));
					}
					if(gameObject.getPosition().getX() == 3197 && gameObject.getPosition().getY() == 5529) {
						player.moveTo(new Position(3201, 5531, 0));
					}
					if(gameObject.getPosition().getX() == 3202 && gameObject.getPosition().getY() == 5515) {
						player.moveTo(new Position(3196, 5512, 0));
					}
					if(gameObject.getPosition().getX() == 3196 && gameObject.getPosition().getY() == 5512) {
						player.moveTo(new Position(3202, 5515, 0));
					}
					if(gameObject.getPosition().getX() == 3190 && gameObject.getPosition().getY() == 5515) {
						player.moveTo(new Position(3190, 5519, 0));
					}
					if(gameObject.getPosition().getX() == 3190 && gameObject.getPosition().getY() == 5519) {
						player.moveTo(new Position(3190, 5515, 0));
					}
					if(gameObject.getPosition().getX() == 3185 && gameObject.getPosition().getY() == 5518) {
						player.moveTo(new Position(3181, 5517, 0));
					}
					if(gameObject.getPosition().getX() == 3181 && gameObject.getPosition().getY() == 5517) {
						player.moveTo(new Position(3185, 5518, 0));
					}
					if(gameObject.getPosition().getX() == 3187 && gameObject.getPosition().getY() == 5531) {
						player.moveTo(new Position(3182, 5530, 0));
					}
					if(gameObject.getPosition().getX() == 3182 && gameObject.getPosition().getY() == 5530) {
						player.moveTo(new Position(3187, 5531, 0));
					}
					if(gameObject.getPosition().getX() == 3169 && gameObject.getPosition().getY() == 5510) {
						player.moveTo(new Position(3159, 5501, 0));
					}
					if(gameObject.getPosition().getX() == 3159 && gameObject.getPosition().getY() == 5501) {
						player.moveTo(new Position(3169, 5510, 0));
					}
					if(gameObject.getPosition().getX() == 3165 && gameObject.getPosition().getY() == 5515) {
						player.moveTo(new Position(3173, 5530, 0));
					}
					if(gameObject.getPosition().getX() == 3173 && gameObject.getPosition().getY() == 5530) {
						player.moveTo(new Position(3165, 5515, 0));
					}
					if(gameObject.getPosition().getX() == 3156 && gameObject.getPosition().getY() == 5523) {
						player.moveTo(new Position(3152, 5520, 0));
					}
					if(gameObject.getPosition().getX() == 3152 && gameObject.getPosition().getY() == 5520) {
						player.moveTo(new Position(3156, 5523, 0));
					}
					if(gameObject.getPosition().getX() == 3148 && gameObject.getPosition().getY() == 5533) {
						player.moveTo(new Position(3153, 5537, 0));
					}
					if(gameObject.getPosition().getX() == 3153 && gameObject.getPosition().getY() == 5537) {
						player.moveTo(new Position(3148, 5533, 0));
					}
					if(gameObject.getPosition().getX() == 3143 && gameObject.getPosition().getY() == 5535) {
						player.moveTo(new Position(3147, 5541, 0));
					}
					if(gameObject.getPosition().getX() == 3147 && gameObject.getPosition().getY() == 5541) {
						player.moveTo(new Position(3143, 5535, 0));
					}
					if(gameObject.getPosition().getX() == 3168 && gameObject.getPosition().getY() == 5541) {
						player.moveTo(new Position(3171, 5542, 0));
					}
					if(gameObject.getPosition().getX() == 3171 && gameObject.getPosition().getY() == 5542) {
						player.moveTo(new Position(3168, 5541, 0));
					}
					if(gameObject.getPosition().getX() == 3190 && gameObject.getPosition().getY() == 5549) {
						player.moveTo(new Position(3190, 5554, 0));
					}
					if(gameObject.getPosition().getX() == 3190 && gameObject.getPosition().getY() == 5554) {
						player.moveTo(new Position(3190, 5549, 0));
					}
					if(gameObject.getPosition().getX() == 3180 && gameObject.getPosition().getY() == 5557) {
						player.moveTo(new Position(3174, 5558, 0));
					}
					if(gameObject.getPosition().getX() == 3174 && gameObject.getPosition().getY() == 5558) {
						player.moveTo(new Position(3180, 5557, 0));
					}
					if(gameObject.getPosition().getX() == 3162 && gameObject.getPosition().getY() == 5557) {
						player.moveTo(new Position(3158, 5561, 0));
					}
					if(gameObject.getPosition().getX() == 3158 && gameObject.getPosition().getY() == 5561) {
						player.moveTo(new Position(3162, 5557, 0));
					}
					if(gameObject.getPosition().getX() == 3166 && gameObject.getPosition().getY() == 5553) {
						player.moveTo(new Position(3162, 5545, 0));
					}
					if(gameObject.getPosition().getX() == 3162 && gameObject.getPosition().getY() == 5545) {
						player.moveTo(new Position(3166, 5553, 0));
					}
					if(gameObject.getPosition().getX() == 3142 && gameObject.getPosition().getY() == 5545) {
						player.moveTo(new Position(3115, 5528, 0));
					}
					break;
				case 29360:
				case 29362:
					if(player.getPosition().getX() == 3094) {
						player.moveTo(new Position(3093, player.getPosition().getY()));
					} else {
						player.moveTo(new Position(3094, player.getPosition().getY()));
					}
					break;
				case 1528:
					if(player.getPosition().getY() == 2977) {
						player.moveTo(new Position(3172, 2976, 0));
					} else if(player.getPosition().getY() == 2976) {
						player.moveTo(new Position(3172, 2977, 0));
					} else if(player.getPosition().getX() == 3182) {
						player.moveTo(new Position(3183, 2984, 0));
					} else if(player.getPosition().getX() == 3183) {
						player.moveTo(new Position(3182, 2984, 0));
					}
					break;
				case 21600:
					if(player.getPosition().getY() == 3802) {
						player.moveTo(new Position(2326, 3801, 0));
					} else
						player.moveTo(new Position(2326, 3802, 0));
					break;
				case 28716:
					PouchMaking.open(player);
					break;
				case 1441:
				case 1442:
				case 1444:
					player.setPositionToFace(gameObject.getPosition());
					player.performAnimation(new Animation(6132));
					final int moveY = player.getPosition().getY() >= 3522 ? player.getPosition().getY() -3 : player.getPosition().getY() + 3;
					TaskManager.submit(new Task(2, player, false) {
						@Override
						protected void execute() {
							player.moveTo(new Position(player.getPosition().getX(), moveY));
							stop();
						}
					});
					break;
				case 15514:
					if(player.getPosition().getX() == 3252 && player.getPosition().getY() == 3266)
						player.moveTo(new Position(3253, 3266));
					else if(player.getPosition().getX() == 3253 && player.getPosition().getY() == 3266)
						player.moveTo(new Position(3252, 3266));
					break;
				case 15516:
					if(player.getPosition().getX() == 3252 && player.getPosition().getY() == 3267)
						player.moveTo(new Position(3253, 3267));
					else if(player.getPosition().getX() == 3253 && player.getPosition().getY() == 3267)
						player.moveTo(new Position(3252, 3267));
					break;
				case 14315:
					PestControl.boardBoat(player);
					break;
				case 14314:
					PestControl.leave(player);
					break;
				case 36913:
					if(player.getPosition().getX() == 3212)
						player.moveTo(new Position(3213, player.getPosition().getY(), player.getPosition().getZ()));
					else if(player.getPosition().getX() == 3113)
						player.moveTo(new Position(3212, player.getPosition().getY(), player.getPosition().getZ()));
					else if(player.getPosition().getX() == 3236 && player.getPosition().getY() == 3284)
						player.moveTo(new Position(3237, 3284, 0));
					else if(player.getPosition().getX() == 3237 && player.getPosition().getY() == 3284)
						player.moveTo(new Position(3236, 3284, 0));

					else if(player.getPosition().getX() == 3252 && player.getPosition().getY() == 3267)
						player.moveTo(new Position(3253, 3267, 0));
					else if(player.getPosition().getX() == 3253 && player.getPosition().getY() == 3267)
						player.moveTo(new Position(3252, 3267, 0));

					else if(player.getPosition().getX() == 3186 && player.getPosition().getY() == 3268)
						player.moveTo(new Position(3185, 3268, 0));
					else if(player.getPosition().getX() == 3185 && player.getPosition().getY() == 3268)
						player.moveTo(new Position(3186, 3268, 0));
					else if(player.getPosition().getX() == 3236 && player.getPosition().getY() == 3295)
						player.moveTo(new Position(3237, 3295, 0));
					else if(player.getPosition().getX() == 3237 && player.getPosition().getY() == 3295)
						player.moveTo(new Position(3236, 3295, 0));

					else if(player.getPosition().getX() == 3212 && player.getPosition().getY() == 3261)
						player.moveTo(new Position(3213, 3261, 0));
					else if(player.getPosition().getX() == 3213 && player.getPosition().getY() == 3261)
						player.moveTo(new Position(3212, 3261, 0));

					else if(player.getPosition().getX() == 3262 && player.getPosition().getY() == 3321)
						player.moveTo(new Position(3262, 3322, 0));
					else if(player.getPosition().getX() == 3262 && player.getPosition().getY() == 3322)
						player.moveTo(new Position(3262, 3321, 0));

					else if(player.getPosition().getX() == 3188 && player.getPosition().getY() == 3279)
						player.moveTo(new Position(3188, 3280, 0));
					else if(player.getPosition().getX() == 3188 && player.getPosition().getY() == 3280)
						player.moveTo(new Position(3188, 3279, 0));
					break;
				case 29315:
				case 29316:
					if(player.getPosition().getX() == 3103) {
						player.moveTo(new Position(3104, player.getPosition().getY(), 0));
					} else if (player.getPosition().getX() == 3104) {
						player.moveTo(new Position(3103, player.getPosition().getY(), 0));
					} else if(player.getPosition().getX() == 3146) {
						player.moveTo(new Position(3145, player.getPosition().getY(), 0));
					} else if(player.getPosition().getX() == 3145) {
						player.moveTo(new Position(3146, player.getPosition().getY(), 0));
					} else if(player.getPosition().getY() == 9944) {
						player.moveTo(new Position(player.getPosition().getX(), 9945, 0));
					} else if(player.getPosition().getY() == 9945) {
						player.moveTo(new Position(player.getPosition().getX(), 9944, 0));
					}
					break;
				case 2320: // Monkey bars, edge dung
					if(player.getSkillManager().getSkillAttributes().getAgilityAttributes().isCrossingObstacle())
						return;
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(true).setCrossedObstacle(1, true);
					player.setPositionToFace(gameObject.getPosition());
					final boolean back = player.getPosition().getY() == 9969 || player.getPosition().getY() == 9970;
					TaskManager.submit(new Task(1, player, true) {
						int tick = 0;
						@Override
						protected void execute() {
							tick--;
							player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(744);
							player.getUpdateFlag().flag(Flag.APPEARANCE);
							player.getMovementQueue().walkStep(0, back ? -1:1);
							if(tick <= -6) {
								player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(-1).setCrossingObstacle(false).setCrossedObstacle(1, false);
								player.getUpdateFlag().flag(Flag.APPEARANCE);
								player.getSkillManager().addExperience(Skill.AGILITY, 25, false);
								stop();
							}
						}
					});
					break;
				case 28690:
				case 28691:
					if(player.getPosition().getX() == 2936) {
						player.moveTo(new Position(player.getPosition().getX() - 1, player.getPosition().getY(), 0));
						return;
					}
					if(player.getPosition().getX() == 2935) {
						player.moveTo(new Position(player.getPosition().getX() + 1, player.getPosition().getY(), 0));
						return;
					}
					break;
				case 29946:
				case 29944:
					if(player.getSkillManager().getCurrentLevel(Skill.SUMMONING) == player.getSkillManager().getMaxLevel(Skill.SUMMONING)) {
						player.getPacketSender().sendMessage("You do not need to recharge your Summoning points right now.");
						return;
					}
					player.performGraphic(new Graphic(1517));
					player.getSkillManager().setCurrentLevel(Skill.SUMMONING, player.getSkillManager().getMaxLevel(Skill.SUMMONING), true);
					player.getPacketSender().sendString(18045, " "+player.getSkillManager().getCurrentLevel(Skill.SUMMONING)+"/"+player.getSkillManager().getMaxLevel(Skill.SUMMONING));
					player.getPacketSender().sendMessage("You recharge your Summoning points.");
					break;
				case -20328:

					if(player.getPosition().getX() == 3213 && player.getPosition().getY() == 3262)
						player.moveTo(new Position(3212, 3262, 0));
					else if(player.getPosition().getX() == 3212 && player.getPosition().getY() == 3262)
						player.moveTo(new Position(3213, 3262, 0));


					else if(player.getPosition().getX() == 3237 && player.getPosition().getY() == 3296)
						player.moveTo(new Position(3236, 3296, 0));
					else if(player.getPosition().getX() == 3236 && player.getPosition().getY() == 3296)
						player.moveTo(new Position(3237, 3296, 0));

					else if(player.getPosition().getX() == 3261 && player.getPosition().getY() == 3321)
						player.moveTo(new Position(3261, 3322, 0));
					else if(player.getPosition().getX() == 3261 && player.getPosition().getY() == 3322)
						player.moveTo(new Position(3261, 3321, 0));

					else if(player.getPosition().getX() == 3189 && player.getPosition().getY() == 3279)
						player.moveTo(new Position(3189, 3280, 0));
					else if(player.getPosition().getX() == 3189 && player.getPosition().getY() == 3280)
						player.moveTo(new Position(3189, 3279, 0));
					break;
				case 12692:
				case 2783:
					player.getAttributes().setCurrentInteractingObject(gameObject);
					EquipmentMaking.handleAnvil(player);
					break;
				case 782:
				case 42217:
				case 42378:
				case 14369:
				case 11758:
				case 4018:
					DialogueManager.start(player, 10);
					player.getAttributes().setDialogueAction(0);
					break;
				case 409: // Altar praying - normal
				case 27661:
				case 2640:
				case -28564:
				case -27546:
				case 36972:
					player.performAnimation(new Animation(645));
					if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < player.getSkillManager().getMaxLevel(Skill.PRAYER)) {
						player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getMaxLevel(Skill.PRAYER), true);
						player.getPacketSender().sendMessage("You recharge your Prayer points.");
					}
					if(player.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) < player.getSkillManager().getMaxLevel(Skill.CONSTITUTION)) {
						player.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, player.getSkillManager().getMaxLevel(Skill.CONSTITUTION), true);
						player.getPacketSender().sendMessage("You recharge your Constitution points.");
					}
					break;
				case 2548:
				case 2546:
					if(player.getPosition().getX() == 2580) {
						player.moveTo(new Position(player.getPosition().getX() - 1, player.getPosition().getY(), player.getPosition().getZ()));
					} else if(player.getPosition().getX() == 2579) {
						player.moveTo(new Position(player.getPosition().getX() + 1, player.getPosition().getY(), player.getPosition().getZ()));
					}
					break;
				case 13290:
					MastersChest.handleChest(player);
					break;
				default:
					//	System.out.println("Unhandled first click object id; [id, position] : [" + id + ", " + position.toString() + "]");
					break;
				}
			}
		}));
	}

	private static void secondClick(final Player player, Packet packet) {
		final int id = packet.readLEShortA();
		final int y = packet.readLEShort();
		final int x = packet.readUnsignedShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final GameObject gameObject = new GameObject(id, position);
		if(id > 0 && id != 6 && !RegionClipping.objectExists(gameObject)) {
			//player.getPacketSender().sendMessage("An error occured. Error code: "+id).sendMessage("Please report the error to a staff member.");
			return;
		}
		player.setPositionToFace(gameObject.getPosition());
		int distanceX = (player.getPosition().getX() - position.getX());
		int distanceY = (player.getPosition().getY() - position.getY());
		if (distanceX < 0)
			distanceX = -(distanceX);
		if (distanceY < 0)
			distanceY = -(distanceY);
		int size = distanceX > distanceY ? distanceX : distanceY;
		gameObject.setSize(size);
		player.getAttributes().setCurrentInteractingObject(gameObject);
		if(specialObjects(player, gameObject, gameObject.getId(), 2))
			return;
		if (player.getAdvancedSkills().getFarming().click(player, x, y, 1))
			return;
		player.getAttributes().setWalkToTask(new WalkToAction(player, position, gameObject, new FinalizedMovementTask() {
			public void execute() {
				if(MiningData.forRock(gameObject.getId()) != null) {
					Prospecting.prospectOre(player, id);
					return;
				}
				//Construction.handleSecondObjectClick(x, y, id, player);
				switch (id) {
				case 2026:
				case 2028:
				case 2029:
				case 2030:
				case 2031:
					player.setEntityInteraction(gameObject);
					Fishing.setupFishing(player, Fishing.forSpot(gameObject.getId(), true));
					return;
				case 1740:
					if(player.getLocation() == Location.LUMBRIDGE && player.getPosition().getZ() == 2)
						player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), 1));
					break;
				case 1739:
					if(player.getLocation() == Location.LUMBRIDGE && player.getPosition().getZ() == 1)
						player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), 2));
					break;
				case 19191:
				case 19189:
				case 19180:
				case 19184:
				case 19182:
				case 19178:
				case 19175:
				case 19187:
					player.getPacketSender().sendMessage("This trap is currently waiting for pray to step on it.");
					break;
				case 26288:
					TeleportHandler.teleportPlayer(player, new Position(2839, 5295, 2), TeleportType.NORMAL);
					player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().setHasEnteredRoom(false);
					break;
				case -28580:
				case 26814:
				case 11666:
					Smelting.openInterface(player);
					if(id != 26814)
						player.setPositionToFace(new Position(3227, 3256));
					break;
				case -28759:
					player.moveTo(new Position(3205, 3228, 2));
					break;
				case 2182:
					RecipeForDisaster.openRFDShop(player);
					break;
				case 28716:
					player.performGraphic(new Graphic(1517));
					player.getSkillManager().setCurrentLevel(Skill.SUMMONING, player.getSkillManager().getMaxLevel(Skill.SUMMONING), true);
					player.getPacketSender().sendString(18045, " "+player.getSkillManager().getCurrentLevel(Skill.SUMMONING)+"/"+player.getSkillManager().getMaxLevel(Skill.SUMMONING));
					player.getPacketSender().sendMessage("You recharge your Summoning points.");
					break;
				case 11758:
				case 14369:
				case 782:
				case -28750:
				case 2012:
				case 2015:
				case 2019:
				case -30784:
				case 27633:
				case 2213:
				case 11402:
				case 4018:
				case 26972:
				case 14367:
					player.getBank(player.getAttributes().getCurrentBankTab()).open();
					break;
				case 2646:
					Flax.pickFlax(player, new GameObject(2646, position));
					break;
				default:
					//logger.info("Unhandled second click object id; [id, position] : [" + id + ", " + position.toString() + "]");
					break;
				}
			}
		}));
	}

	private static void thirdClick(final Player player, Packet packet) {
		final int id = packet.readLEShortA();
		final int y = packet.readLEShort();
		final int x = packet.readUnsignedShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final GameObject gameObject = new GameObject(id, position);
		if(id > 0 && id != 6 && !RegionClipping.objectExists(gameObject)) {
			//player.getPacketSender().sendMessage("An error occured. Error code: "+id).sendMessage("Please report the error to a staff member.");
			return;
		}
		player.setPositionToFace(gameObject.getPosition());
		int distanceX = (player.getPosition().getX() - position.getX());
		int distanceY = (player.getPosition().getY() - position.getY());
		if (distanceX < 0)
			distanceX = -(distanceX);
		if (distanceY < 0)
			distanceY = -(distanceY);
		int size = distanceX > distanceY ? distanceX : distanceY;
		gameObject.setSize(size);
		player.getAttributes().setCurrentInteractingObject(gameObject);
		if(specialObjects(player, gameObject, gameObject.getId(), 3))
			return;
		//if(gameObject.getId() > 0 && !Region3.objectExists(gameObject)) {
		//System.out.println("Thirdclick Object: "+gameObject.getId()+" does not exist.");
		//return;
		//}
		player.getAttributes().setWalkToTask(new WalkToAction(player, position, gameObject, new FinalizedMovementTask() {
			@Override
			public void execute() {
				//Construction.handleThirdObjectClick(x, y, id, player);
				switch (id) {
				default:
					//logger.info("Unhandled second click object id; [id, position] : [" + id + ", " + position.toString() + "]");
					if(player.getRights() == PlayerRights.DEVELOPER)
						player.getPacketSender().sendMessage("Unhandled third click object id; [id, position] : [" + id + ", " + position.toString() + "]");

					break;
				}
			}
		}));
	}

	private static void fourthClick(final Player player, Packet packet) {
		final int id = packet.readLEShortA();
		final int y = packet.readLEShort();
		final int x = packet.readUnsignedShortA();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final GameObject gameObject = new GameObject(id, position);
		if(id > 0 && !RegionClipping.objectExists(gameObject)) {
			//player.getPacketSender().sendMessage("An error occured. Error code: "+id).sendMessage("Please report the error to a staff member.");
			return;
		}
		player.setPositionToFace(gameObject.getPosition());
		int distanceX = (player.getPosition().getX() - position.getX());
		int distanceY = (player.getPosition().getY() - position.getY());
		if (distanceX < 0)
			distanceX = -(distanceX);
		if (distanceY < 0)
			distanceY = -(distanceY);
		int size = distanceX > distanceY ? distanceX : distanceY;
		gameObject.setSize(size);
		player.getAttributes().setCurrentInteractingObject(gameObject);
		//System.out.println("Fourth click obj. ID: "+id+", Y: "+y+", X: "+x);
		player.getAttributes().setWalkToTask(new WalkToAction(player, position, gameObject, new FinalizedMovementTask() {
			@Override
			public void execute() {
				//Construction.handleThirdObjectClick(x, y, id, player);
				switch (id) {
				default:
					//logger.info("Unhandled second click object id; [id, position] : [" + id + ", " + position.toString() + "]");
					if(player.getRights() == PlayerRights.DEVELOPER)
						player.getPacketSender().sendMessage("Unhandled fourth click object id; [id, position] : [" + id + ", " + position.toString() + "]");

					break;
				}
			}
		}));
	}

	private static void fifthClick(final Player player, Packet packet) {
		final int id = packet.readUnsignedShortA();
		final int y = packet.readUnsignedShortA();
		final int x = packet.readShort();
		final Position position = new Position(x, y, player.getPosition().getZ());
		final GameObject gameObject = new GameObject(id, position);
		if(id > 0 && !RegionClipping.objectExists(gameObject)) {
			//player.getPacketSender().sendMessage("An error occured. Error code: "+id).sendMessage("Please report the error to a staff member.");
			return;
		}
		player.setPositionToFace(gameObject.getPosition());
		int distanceX = (player.getPosition().getX() - position.getX());
		int distanceY = (player.getPosition().getY() - position.getY());
		if (distanceX < 0)
			distanceX = -(distanceX);
		if (distanceY < 0)
			distanceY = -(distanceY);
		int size = distanceX > distanceY ? distanceX : distanceY;
		gameObject.setSize(size);
		player.getAttributes().setCurrentInteractingObject(gameObject);
		player.getAttributes().setWalkToTask(new WalkToAction(player, position, gameObject, new FinalizedMovementTask() {
			@Override
			public void execute() {
				Hunter.pushThroughWall(player);
				//Construction.handleFifthObjectClick(x, y, id, player);
			}
		}));
	}

	@Override
	public void execute(Player player, Packet packet) {
		if(player.isTeleporting())
			return;
		switch (packet.getOpcode()) {
		case FIRST_CLICK:
			firstClick(player, packet);
			break;
		case SECOND_CLICK:
			secondClick(player, packet);
			break;
		case THIRD_CLICK:
			thirdClick(player, packet);
			break;
		case FOURTH_CLICK:
			fourthClick(player, packet);
			break;
		case FIFTH_CLICK:
			fifthClick(player, packet);
			break;
		}
	}

	private static boolean specialObjects(final Player player, final GameObject gameObject, int id, int clickAction) {
		if(clickAction == 1) {
			switch(id) {
			case 1749:
				if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 2000)
					return true;
				player.performAnimation(new Animation(827));
				TaskManager.submit(new Task(1, player, false) {
					@Override
					public void execute() {
						if(gameObject.getPosition().getY() == 9680 && !player.getAttributes().getMinigameAttributes().getConquestArenaAttributes().isInArena())
							Conquest.enterArena(player);
						else
							Conquest.leaveArena(player);
						stop();
					}
				});
				player.getAttributes().setClickDelay(System.currentTimeMillis());
				break;
			case 2282:
				Agility.handleObject(player, gameObject);
				break;
			case 57225:
				if(Locations.goodDistance(gameObject.getPosition(), player.getPosition(), 4)) {
					if(!player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()) {
						player.moveTo(new Position(2911, 5203));
						player.getPacketSender().sendMessage("You enter Nex's lair..");
					} else
						player.moveTo(new Position(2906, 5204));
					player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().setHasEnteredRoom(!player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom());
				}
				break;
			case 36970:
				if(Locations.goodDistance(player.getPosition().copy(), gameObject.getPosition().copy(), 3))
					Flax.showSpinInterface(player);
				return true;
			case 2491:
				if(!Locations.goodDistance(player.getPosition().copy(), gameObject.getPosition().copy(), 7))
					return true;
				DialogueManager.start(player, 115);
				player.getAttributes().setDialogueAction(211);
				return true;
			case 3220:
			case 3222:
			case 2704:
			case 3218:
			case 2702:
			case 2698:
			case 3221:
			case 2705:
			case 6:
				DwarfCannon cannon = player.getAttributes().getCannon();
				if (cannon == null || cannon.getOwnerIndex() != player.getIndex()) {
					player.getPacketSender().sendMessage("This is not your cannon!");
				} else {
					DwarfMultiCannon.startFiringCannon(player, cannon);
				}
				break;
			case 6704:
				player.moveTo(new Position(3577, 3282, 0));
				break;
			case 6706:
				player.moveTo(new Position(3554, 3283, 0));
				break;
			case 6705:
				player.moveTo(new Position(3566, 3275, 0));
				break;
			case 6702:
				player.moveTo(new Position(3564, 3289, 0));
				break;
			case 6703:
				player.moveTo(new Position(3574, 3298, 0));
				break;
			case 6707:
				player.moveTo(new Position(3556, 3298, 0));
				break;
			case 2513:
				ArcheryCompetition.fireTarget(player, gameObject);
				break;
			}
			return false;
		} else if(clickAction == 2) {
			switch(id) {
			case 27663:
				if(Locations.goodDistance(player.getPosition().copy(), gameObject.getPosition().copy(), 2))
					player.getBank(player.getAttributes().getCurrentBankTab()).open();
				break;
			case -28566:
			case 25824:
			case 4202:
			case 2644:
				if(Locations.goodDistance(player.getPosition().copy(), gameObject.getPosition().copy(), 3))
					Flax.showSpinInterface(player);
				return true;
			case 6:
				DwarfCannon cannon = player.getAttributes().getCannon();
				if (cannon == null || cannon.getOwnerIndex() != player.getIndex()) {
					player.getPacketSender().sendMessage("This is not your cannon!");
				} else {
					DwarfMultiCannon.pickupCannon(player, cannon, false);
				}
				break;
			case 17010:
				player.performAnimation(new Animation(645));
				if(player.getAttributes().getSpellbook() == MagicSpellbook.LUNAR) {
					MagicSpellbook spellbook = MagicSpellbook.NORMAL;
					player.getAttributes().setSpellbook(spellbook);
					player.getPacketSender().sendTabInterface(Constants.MAGIC_TAB, player.getAttributes().getSpellbook().getInterfaceId());
					return true;
				} else {
					MagicSpellbook spellbook = MagicSpellbook.LUNAR;
					player.getAttributes().setSpellbook(spellbook);
					player.getPacketSender().sendTabInterface(Constants.MAGIC_TAB, player.getAttributes().getSpellbook().getInterfaceId());
				}
				return true;
			}
			if(Stalls.isThievingStall(player, gameObject))
				return true;
			return false;
		} else 	if(clickAction == 3) {
			switch(gameObject.getId()) {
			case 3076:
				if(player.getPosition().getY() == 3228)
					player.moveTo(new Position(3205, 3228, 0));
				else if(player.getPosition().getY() == 3208 || player.getPosition().getY() == 3209)
					player.moveTo(new Position(3205, 3209, 0));
				return true;
			}
		}
		return false;
	}

	public static final int FIRST_CLICK = 132, SECOND_CLICK = 252, THIRD_CLICK = 70, FOURTH_CLICK = 234, FIFTH_CLICK = 228;
}
