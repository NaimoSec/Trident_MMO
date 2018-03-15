package org.trident.net.packet.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.GameObject;
import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.container.impl.Inventory;
import org.trident.model.container.impl.Shop;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.Misc;
import org.trident.world.content.Consumables;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.Digging;
import org.trident.world.content.DwarfMultiCannon;
import org.trident.world.content.Effigies;
import org.trident.world.content.ExperienceLamps;
import org.trident.world.content.MemberTokens;
import org.trident.world.content.MoneyPouch;
import org.trident.world.content.StartingHandler;
import org.trident.world.content.StatReset;
import org.trident.world.content.ToyHorses;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.audio.SoundEffects.SoundData;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.impl.HelpbookDialogues;
import org.trident.world.content.dialogue.impl.SlayerDialogues;
import org.trident.world.content.minigames.impl.FishingTrawler;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.content.skills.impl.dungeoneering.DungeoneeringParty;
import org.trident.world.content.skills.impl.dungeoneering.ItemBinding;
import org.trident.world.content.skills.impl.herblore.Herblore;
import org.trident.world.content.skills.impl.herblore.IngridientsBook;
import org.trident.world.content.skills.impl.hunter.Hunter;
import org.trident.world.content.skills.impl.hunter.Trap.TrapState;
import org.trident.world.content.skills.impl.hunter.traps.BoxTrap;
import org.trident.world.content.skills.impl.hunter.traps.SnareTrap;
import org.trident.world.content.skills.impl.prayer.Prayer;
import org.trident.world.content.skills.impl.runecrafting.Runecrafting;
import org.trident.world.content.skills.impl.runecrafting.RunecraftingPouches;
import org.trident.world.content.skills.impl.slayer.SlayerTasks;
import org.trident.world.content.skills.impl.summoning.SummoningData;
import org.trident.world.content.skills.impl.woodcutting.BirdNests;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.content.teleporting.teletab.TabletBreaking;
import org.trident.world.content.treasuretrails.ClueScroll;
import org.trident.world.content.treasuretrails.CoordinateScrolls;
import org.trident.world.content.treasuretrails.DiggingScrolls;
import org.trident.world.content.treasuretrails.MapScrolls;
import org.trident.world.content.treasuretrails.Puzzle;
import org.trident.world.content.treasuretrails.SearchScrolls;
import org.trident.world.entity.player.Player;

/**
 * This packet listener is called when an item action is performed/clicked upon.
 * 
 * @author relex lawl
 */

public class ItemActionPacketListener implements PacketListener {

	/**
	 * The PacketListener logger to debug information and print out errors.
	 */
	//private final static Logger logger = Logger.getLogger(ItemActionPacketListener.class);

	private static void firstAction(final Player player, Packet packet) {
		int interfaceId = packet.readUnsignedShort();
		int slot = packet.readShort();
		int itemId = packet.readShort();/*
		if (interfaceId == 38274){
			Construction.handleItemClick(itemId, player);
			return;
		}
		Construction.handleFirstItemClick(player, itemId);*/
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
		if(ExperienceLamps.handleLamp(player, itemId))
			return;
		ToyHorses.handleToyHorsesActions(player, itemId);
		if (Herblore.cleanHerb(player, itemId))
			return;
		if(BirdNests.isNest(itemId)) {
			BirdNests.searchNest(player, itemId);
			return;
		}
		Item item = player.getInventory().getItems()[slot];
		player.getAttributes().setCurrentInteractingItem(item);
		/**
		 * Clue scrolls
		 */
		if(ClueScroll.handleCasket(player, itemId) || SearchScrolls.loadClueInterface(player, itemId) || MapScrolls.loadClueInterface(player, itemId) || Puzzle.loadClueInterface(player, itemId) || CoordinateScrolls.loadClueInterface(player, itemId) || DiggingScrolls.loadClueInterface(player, itemId))
			return;
		/*for (int i: ClueScrolls.CLUE_SCROLL_IDS)
			if (itemId == i){
				if (ClueScrolls.hasClue(player) != null){
					ClueScrolls.showMap(player, Scrolls.getMap(item.getId()));
					return;
				}
			} else if (itemId == 2717){
				ClueScrolls.giveReward(player);
				return;
			}*/
		/**
		 * Effigies
		 */
		if(Effigies.isEffigy(itemId)) {
			Effigies.handleEffigy(player, itemId);
			return;
		}
		SoulWars.handleItemClick(itemId, player);
		switch (interfaceId) {
		case Inventory.INTERFACE_ID:
			if (item == null)
				return;
			if (Consumables.isFood(player, item, slot))
				return;
			if(Consumables.isPotion(itemId)) {
				Consumables.handlePotion(player, itemId, slot);
				return;
			}
			if (Prayer.isBone(itemId)) {
				Prayer.buryBone(player, itemId);
				return;
			}
			if(MemberTokens.handleToken(player, item)) {
				return;
			}
			break;
		}
		switch (itemId) {
		case 13663:
			StatReset.openInterface(player);
			break;
		case 5509:
			RunecraftingPouches.fill(player, RunecraftingPouches.Pouch.SMALL);
			break;
		case 15707:
			DungeoneeringParty.create(player);
			break;
		case 10006:
			// Hunter.getInstance().laySnare(client);
			Hunter.layTrap(
					player,
					new SnareTrap(new GameObject(19175, new Position(player.
							getPosition().getX(), player.getPosition().getY(), player
							.getPosition().getZ())), TrapState.SET, 500, player));
			break;
		case 10008:			
			Hunter.layTrap(
					player,
					new BoxTrap(new GameObject(19187, new Position(player.
							getPosition().getX(), player.getPosition().getY(), player
							.getPosition().getZ())), TrapState.SET, 500, player));
			break;
		case 15246:
			if(!player.getInventory().contains(15246))
				return;
			if(player.getAttributes().getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
				return;
			}
			player.getInventory().delete(15246, 1).add(556, 150).add(558, 150);
			player.getPacketSender().sendMessage("The package contained some magical runes!");
			break;
		case 583:
			FishingTrawler.ShipHandler.bailWater(player);
			break;
		case 585:
			FishingTrawler.ShipHandler.emptyFullBucket(player);
			break;
		case 9475:
			if(System.currentTimeMillis() - player.getAttributes().getFoodDelay() >= 1800) {
				player.performAnimation(new Animation(829));
				SoundEffects.sendSoundEffect(player, SoundData.EAT_FOOD, 10, 0);
				player.getInventory().delete(item);
				player.getAttributes().setRunEnergy(100);
				player.getPacketSender().sendMessage("The Mint cake has restored your energy!");
				player.getAttributes().setFoodDelay(System.currentTimeMillis());
			}
			break;
		case 5972:
			if(System.currentTimeMillis() - player.getAttributes().getFoodDelay() >= 1800) {
				player.performAnimation(new Animation(829));
				SoundEffects.sendSoundEffect(player, SoundData.EAT_FOOD, 10, 0);
				player.getInventory().delete(item);
				if(player.getAttributes().getRunEnergy() < 100) {
					player.getAttributes().setRunEnergy(player.getAttributes().getRunEnergy() + 5);
					if(player.getAttributes().getRunEnergy() > 100)
						player.getAttributes().setRunEnergy(100);
					player.getPacketSender().sendMessage("The Papaya fruit retores some of your energy!");
					player.getAttributes().setFoodDelay(System.currentTimeMillis());
				}
			}
			break;
		case 757:
			if(player.getLocation() != Location.EDGEVILLE) {
				player.getPacketSender().sendMessage("Please go to Edgeville before reading this.");
				return;
			}
			DialogueManager.start(player, HelpbookDialogues.firstDialogue(player));
			break;
		case 6950:
			if(player.getLocation() != Location.EDGEVILLE) {
				player.getPacketSender().sendMessage("Please go to Edgeville before activating the orb.");
				return;
			}
			if(player.getAttributes().getInterfaceId() > 0 || !TeleportHandler.checkReqs(player, null)) {
				player.getPacketSender().sendMessage("You cannot do this at the moment.");
				return;
			}
			player.getAttributes().setResetPosition(player.getPosition().copy());
			StartingHandler.startTutorial(player);
			break;
		case 299:
			if(System.currentTimeMillis() - player.getAttributes().getClickDelay() <= 3000)
				return;
			if(CustomObjects.objectExists(player.getPosition().copy())) {
				player.getPacketSender().sendMessage("You cannot plant a seed here.");
				return;
			}
			final GameObject flower = new GameObject(CustomObjects.FLOWER_IDS[(int)(Math.random()*CustomObjects.FLOWER_IDS.length)], player.getPosition().copy());
			player.getMovementQueue().stopMovement();
			player.getInventory().delete(299, 1);
			player.getSkillManager().addExperience(Skill.FARMING, 200, false);
			player.performAnimation(new Animation(827));
			player.getPacketSender().sendMessage("You plant the seed..");
			TaskManager.submit(new Task(1, player, false) {
				@Override
				public void execute() {
					CustomObjects.globalObjectRemovalTask(flower, 60);
					if(player.getMovementQueue().canWalk(-1, 0))
						player.getMovementQueue().walkStep(-1, 0);
					else
						player.getMovementQueue().walkStep(0, -1);
					player.setEntityInteraction(flower);
					stop();
				}
			});
			player.getAttributes().setClickDelay(System.currentTimeMillis());
			break;
		case 15262:
			if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 1000)
				return;
			player.getInventory().delete(15262, 1);
			player.getInventory().add(18016, 10000).refreshItems();
			player.getAttributes().setClickDelay(System.currentTimeMillis());
			break;
		case 292:
			IngridientsBook.readBook(player, 0, false);
			break;
		case 6:
			DwarfMultiCannon.setupCannon(player);
			break;
		case 407:
			player.getInventory().delete(407, 1);
			if (Misc.getRandom(3) < 3) {
				player.getInventory().add(409, 1);
			} else if(Misc.getRandom(4) < 4) {
				player.getInventory().add(411, 1);
			} else 
				player.getInventory().add(413, 1);
			break;
		case 405:
			player.getInventory().delete(405, 1);
			if (Misc.getRandom(1) < 1) {
				int coins = Misc.getRandom(2500);
				player.getInventory().add(995, coins);
				player.getPacketSender().sendMessage("The casket contained "+coins+" coins!");
			} else
				player.getPacketSender().sendMessage("The casket was empty.");
			break;
		case 15387:
			player.getInventory().delete(15387, 1);
			int[] rewards = {3000, 219, 5016, 6293, 6889, 2205, 3051, 269, 329, 3779, 6371, 2442, 347, 247};
			player.getInventory().add(rewards[Misc.getRandom(rewards.length-1)], 1);
			break;
		case 952:
			Digging.dig(player);
			break;
		case 4155:
			if(player.getAdvancedSkills().getSlayer().getSlayerTask() == SlayerTasks.NO_TASK) {
				player.getPacketSender().sendInterfaceRemoval();
				player.getPacketSender().sendMessage("Your Enchanted gem will only work if you have a Slayer task.");
				return;
			}
			Dialogue dialogue = SlayerDialogues.dialogue(player);
			DialogueManager.start(player, dialogue);
			break;
		case 8007:
		case 8008:
		case 8009:
		case 8012:
		case 8011:
		case 8006:
		case 8010:
		case 13611:
			if(!TabletBreaking.isTab(player, item, slot))
				player.getPacketSender().sendMessage("An error occured. Please try again.");
			break;
		default:
			//logger.info("Unhandled 1st item action - [interfaceId, itemId, slot] : [" + interfaceId + ", " + itemId + ", " + slot + "]");
			if(player.getRights() == PlayerRights.DEVELOPER)
				player.getPacketSender().sendMessage("Unhandled 1st item action - [interfaceId, itemId, slot] : [" + interfaceId + ", " + itemId + ", " + slot + "]");
			break;
		}
	}

	public static void secondAction(Player player, Packet packet) {
		int interfaceId = packet.readLEShortA();
		int slot = packet.readLEShort();
		int itemId = packet.readShortA();
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(itemId == 995) {
			MoneyPouch.depositMoney(player, player.getInventory().getAmount(995));
			return;
		}
		if (interfaceId == Inventory.INTERFACE_ID) {
			Item item = player.getInventory().forSlot(slot);
			if (item == null || item.getId() != itemId)
				return;
			if (SummoningData.isPouch(player, item))
				return;
		}

		//Slayer rings
		for(int i = 13281; i < 13289; i++) {
			if(itemId == i) {
				player.getAdvancedSkills().getSlayer().handleSlayerRingTP(itemId);
				return;
			}
		}

		if(interfaceId == Shop.INTERFACE_ID) {

		}
		switch (itemId) {
		case 1438:
		case 1448:
		case 1440:
		case 1442:
		case 1446:
		case 1454:
		case 1452:
		case 1462:
		case 1458:
		case 1456:
		case 1450:
		case 1460:
			Runecrafting.handleTalisman(player, itemId);
			break;
		case 1444:
			player.getPacketSender().sendMessage("This Runecrafting altar is currently disabled.");
			break;
		default:
			//logger.info("Unhandled second item action - [interfaceId, itemId, slot] : [" + interfaceId + ", " + itemId + ", " + slot + "]");
			if(player.getRights() == PlayerRights.DEVELOPER)
				player.getPacketSender().sendMessage("Unhandled second item action - [interfaceId, itemId, slot] : [" + interfaceId + ", " + itemId + ", " + slot + "]");
			break;
		}
	}

	@Override
	public void execute(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		switch (packet.getOpcode()) {
		case SECOND_ITEM_ACTION_OPCODE:
			secondAction(player, packet);
			break;
		case FIRST_ITEM_ACTION_OPCODE:
			firstAction(player, packet);
			break;
		case THIRD_ITEM_ACTION_OPCODE:
			thirdClickAction(player, packet);
			break;
		}
	}

	public void thirdClickAction(Player player, Packet packet) {
		int itemId = packet.readShortA();
		int slot = packet.readLEShortA();
		int interfaceId = packet.readLEShortA();
		if(slot < 0 || slot > player.getInventory().capacity())
			return;
		if(player.getInventory().getItems()[slot].getId() != itemId)
			return;
		//Slayer rings
		for(int i = 13281; i < 13289; i++) {
			if(itemId == i) {
				player.getPacketSender().sendInterfaceRemoval();
				SlayerTasks task = player.getAdvancedSkills().getSlayer().getSlayerTask();
				if(task == SlayerTasks.NO_TASK)
					player.getPacketSender().sendMessage("You do not have a Slayer task.");
				else {
					String s = Misc.formatText(player.getAdvancedSkills().getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " "));
					if(player.getAdvancedSkills().getSlayer().getAmountToSlay() > 1)
						s += "s";
					player.getPacketSender().sendMessage("Your current task is to kill another "+player.getAdvancedSkills().getSlayer().getAmountToSlay()+" "+s+".");

				}
				return;
			}
		}
		if(org.trident.world.content.skills.impl.hunter.JarData.forJar(itemId) != null) {
			Hunter.lootJar(player, new Item(itemId, 1), org.trident.world.content.skills.impl.hunter.JarData.forJar(itemId));
			return;
		}
		if(ItemBinding.isBindable(itemId)) {
			ItemBinding.bindItem(player, itemId);
			return;
		}
		switch(itemId) {
		case 6570:
			if(player.getInventory().contains(6570) && player.getInventory().getAmount(6529) >= 70000) {
				player.getInventory().delete(6570, 1).delete(6529, 70000).add(19111, 1);
				player.getPacketSender().sendMessage("You have upgraded your Fire cape into a TokHaar-Kal cape!");
			} else {
				player.getPacketSender().sendMessage("You need at least 70.000 Tokkul to upgrade this item.");
			}
			break;
		case 5509:
			RunecraftingPouches.check(player, RunecraftingPouches.Pouch.SMALL);
			break;
		case 11283: //DFS
			player.getPacketSender().sendMessage("Your Dragonfire shield has "+player.getAttributes().getDragonFireCharges()+"/30 dragon-fire charges.");
			break;
		default:
			if(player.getRights() == PlayerRights.DEVELOPER)
				player.getPacketSender().sendMessage("Unhandled third item action - [interfaceId, itemId, slot] : [" + interfaceId + ", " + itemId + ", " + slot + "]");
			break;
		}
	}

	public static final int SECOND_ITEM_ACTION_OPCODE = 75;

	public static final int FIRST_ITEM_ACTION_OPCODE = 122;

	public static final int THIRD_ITEM_ACTION_OPCODE = 16;

}