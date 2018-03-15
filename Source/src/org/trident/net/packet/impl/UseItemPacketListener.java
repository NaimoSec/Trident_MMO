package org.trident.net.packet.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.FinalizedMovementTask;
import org.trident.engine.task.impl.WalkToAction;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.GameObject;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.container.impl.Inventory;
import org.trident.model.inputhandling.impl.EnterAmountOfLogsToAdd;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.ItemForging;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.impl.SlayerDialogues;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.content.minigames.impl.WarriorsGuild;
import org.trident.world.content.skills.impl.cooking.Cooking;
import org.trident.world.content.skills.impl.cooking.CookingData;
import org.trident.world.content.skills.impl.crafting.Gems;
import org.trident.world.content.skills.impl.crafting.Jewelry;
import org.trident.world.content.skills.impl.crafting.LeatherMaking;
import org.trident.world.content.skills.impl.firemaking.Firemaking;
import org.trident.world.content.skills.impl.fletching.Fletching;
import org.trident.world.content.skills.impl.herblore.GrindingAction;
import org.trident.world.content.skills.impl.herblore.Herblore;
import org.trident.world.content.skills.impl.herblore.PotionCombinating;
import org.trident.world.content.skills.impl.herblore.WeaponPoison;
import org.trident.world.content.skills.impl.prayer.BonesOnAltar;
import org.trident.world.content.skills.impl.prayer.Prayer;
import org.trident.world.content.skills.impl.smithing.EquipmentMaking;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

/**
 * This packet listener is called when a player 'uses' an item on another
 * entity.
 * 
 * @author relex lawl
 */

public class UseItemPacketListener implements PacketListener {

	/**
	 * The PacketListener logger to debug information and print out errors.
	 */
	// private final static Logger logger =
	// Logger.getLogger(UseItemPacketListener.class);

	private static void useItem(Player player, Packet packet) {
		if (player.isTeleporting() || player.getConstitution() <= 0)
			return;
		int interfaceId = packet.readLEShortA();
		int slot = packet.readShortA();
		int id = packet.readLEShort();
		Item item = player.getInventory().forSlot(slot);
		if (item == null)
			return;
		switch (interfaceId) {
		case Inventory.INTERFACE_ID:
			switch (id) {
			case 6:
				// new DwarfMultiCannon(player.getPosition());
				break;
			default:
				// logger.info("Unhandled first item action - id: " + id);
				if (player.getRights() == PlayerRights.DEVELOPER)
					player.getPacketSender().sendMessage(
							"Unhandled useItem - [interfaceId, itemId, slot] : ["
									+ interfaceId + ", " + id + ", " + slot
									+ "]");
				break;
			}
			break;
		}
	}

	private static void itemOnItem(Player player, Packet packet) {
		int usedWithSlot = packet.readUnsignedShort();
		int itemUsedSlot = packet.readUnsignedShortA();
		if (usedWithSlot < 0 || itemUsedSlot < 0
				|| itemUsedSlot > player.getInventory().capacity()
				|| usedWithSlot > player.getInventory().capacity())
			return;
		Item usedWith = player.getInventory().getItems()[usedWithSlot];
		Item itemUsedWith = player.getInventory().getItems()[itemUsedSlot];
		GrindingAction.init(player, itemUsedWith.getId(), usedWith.getId());
		WeaponPoison.execute(player, itemUsedWith.getId(), usedWith.getId());
		if (itemUsedWith.getId() == 590 || usedWith.getId() == 590)
			Firemaking.lightFire(player, itemUsedWith.getId() == 590 ? usedWith.getId() : itemUsedWith.getId(), false, 1);
		if (itemUsedWith.getDefinition().getName().contains("(")
				&& usedWith.getDefinition().getName().contains("("))
			PotionCombinating.combinePotion(player, usedWith.getId(),
					itemUsedWith.getId());
		if (usedWith.getId() == Herblore.VIAL || itemUsedWith.getId() == Herblore.VIAL){
			if (Herblore.makeUnfinishedPotion(player, usedWith.getId()) || Herblore.makeUnfinishedPotion(player, itemUsedWith.getId()))
				return;
		}
		if (Herblore.finishPotion(player, usedWith.getId(), itemUsedWith.getId()) || Herblore.finishPotion(player, itemUsedWith.getId(), usedWith.getId()))
			return;
		if (itemUsedWith.getId() == 1755 || usedWith.getId() == 1755)
			Gems.selectionInterface(player, usedWith.getId() == 1755 ? itemUsedWith.getId() : usedWith.getId());
		if (itemUsedWith.getId() == 1759 || usedWith.getId() == 1759)
			Jewelry.stringAmulet(player, usedWith.getId() == 1759 ? itemUsedWith.getId() : usedWith.getId());
		if (usedWith.getId() == 1733 || itemUsedWith.getId() == 1733)
			LeatherMaking.craftLeatherDialogue(player, usedWith.getId(),
					itemUsedWith.getId());
		if (usedWith.getId() == 946 || itemUsedWith.getId() == 946)
			Fletching.openSelection(player, usedWith.getId() == 946 ? itemUsedWith.getId() : usedWith.getId());
		if (usedWith.getId() == 1777 || itemUsedWith.getId() == 1777)
			Fletching.openBowStringSelection(player, usedWith.getId() == 1777 ? itemUsedWith.getId() : usedWith.getId());
		if (usedWith.getId() == 53 || itemUsedWith.getId() == 53
				|| usedWith.getId() == 52 || itemUsedWith.getId() == 52)
			Fletching
			.makeArrows(player, usedWith.getId(), itemUsedWith.getId());
		Herblore.handleSpecialPotion(player, itemUsedWith.getId(),
				usedWith.getId());
		ItemForging.forgeItem(player, itemUsedWith.getId(), usedWith.getId());
		if (player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage(
					"ItemOnItem - [usedItem, usedWith] : [" + usedWith.getId()
					+ ", " + itemUsedWith + "]");
	}

	private static void itemOnObject(final Player player, Packet packet) {
		@SuppressWarnings("unused")
		int interfaceType = packet.readShort();
		final int objectId = packet.readShort();
		final int objectY = packet.readLEShortA();
		final int itemSlot = packet.readLEShort();
		final int objectX = packet.readLEShortA();
		final int itemId = packet.readShort();
		if (itemSlot < 0 || itemSlot > player.getInventory().capacity())
			return;
		final Item item = player.getInventory().getItems()[itemSlot];
		if (item == null)
			return;
		final GameObject gameObject = new GameObject(objectId, new Position(
				objectX, objectY, player.getPosition().getZ()));
		player.getAttributes().setCurrentInteractingObject(gameObject);
		player.getAttributes().setWalkToTask(new WalkToAction(player, gameObject.getPosition().copy(),
				gameObject, new FinalizedMovementTask() {
			@Override
			public void execute() {
				SoulWars.handleItemOnObject(objectId,
						itemId, player);
				if (objectId == 15621) { // Warriors guild
					// animator
					if (!WarriorsGuild.itemOnAnimator(player,
							item, gameObject))
						player.getPacketSender()
						.sendMessage(
								"Nothing interesting happens..");
					return;
				}
				if (CookingData.forFish(item.getId()) != null
						&& CookingData.isRange(gameObject)) {
					player.setPositionToFace(gameObject.getPosition());
					Cooking.selectionInterface(player,
							CookingData.forFish(item.getId()));
					return;
				}
				if (Prayer.isBone(itemId)) {
					BonesOnAltar.openInterface(
							player, itemId);
					return;
				}
				if (player.getAdvancedSkills().getFarming().plant(itemId, objectId,
						objectX, objectY))
					return;
				if (player.getAdvancedSkills().getFarming().useItemOnPlant(itemId,
						objectX, objectY))
					return;
				switch (gameObject.getId()) {
				case 2732:
					EnterAmountOfLogsToAdd.openInterface(player);
					break;
				case 11666:
					if(itemId == 1592 || itemId == 1595 || itemId == 1597 || itemId == 2357 || itemId == 1607 || itemId == 1603 || itemId == 1601 || itemId == 1615 || itemId == 6573)
						Jewelry.showInterface(player);
					break;
				case 2783:
					EquipmentMaking.handleAnvil(player);
					break;
				case 12692:
					if (itemId == 14472 || itemId == 14474
					|| itemId == 14476
					|| itemId == 14478) {
						DialogueManager.start(player, 279);
						player.getAttributes().setDialogueAction(
								279);
					}
					break;

				default:
					// logger.info("Unhandled item on object - [item used, object used on] : ["
					// + itemId + ", " +
					// gameObject.getDefinition().getId() +
					// "]");
					if (player.getRights() == PlayerRights.DEVELOPER)
						player.getPacketSender()
						.sendMessage(
								"Unhandled item on object - [item used, object used on] : ["
										+ itemId
										+ ", "
										+ gameObject
										.getDefinition()
										.getId()
										+ "]");
					break;
				}
			}
		}));
	}

	private static void itemOnNpc(final Player player, Packet packet) {
		int id = packet.readShortA();
		int index = packet.readShortA();
		final int slot = packet.readLEShort();
		if (slot < 0 || slot > player.getInventory().capacity() || index < 0
				|| index > World.getNpcs().size())
			return;
		final Item item = player.getInventory().getItems()[slot];
		final NPC npc = World.getNpcs().get(index);
		if (item.getId() != id)
			return;
		switch (id) {
		case 14644:
			if (npc.getId() == 8600) {
				if (SoulWars.redBarricades.contains(npc)
						|| SoulWars.blueBarricades.contains(npc)) {
					if (npc.getAttributes().isDying())
						return;
					player.getInventory().delete(item);
					npc.performGraphic(new Graphic(346, GraphicHeight.MIDDLE));
					npc.setDamage(new Damage(new Hit(9001, CombatIcon.MAGIC,
							Hitmask.CRITICAL)));
					npc.getAttributes().setDying(true);
					TaskManager.submit(
							new Task(3, player, false) {
								@Override
								public void execute() {
									SoulWars.redBarricades
									.remove(npc);
									SoulWars.blueBarricades
									.remove(npc);
									World.deregister(npc);
									stop();
								}
							});
				}
			}
			break;
		}
	}

	private static void itemOnPlayer(Player player, Packet packet) {
		int interfaceId = packet.readUnsignedShortA();
		int targetIndex = packet.readUnsignedShort();
		int itemId = packet.readUnsignedShort();
		int slot = packet.readLEShort();
		if (slot < 0 || slot > player.getInventory().capacity()
				|| targetIndex > World.getPlayers().size())
			return;
		Player target = World.getPlayers().get(targetIndex);
		switch (itemId) {
		case 14640:
			if (SoulWars.redTeam.contains(player)
					&& SoulWars.redTeam.contains(target)
					|| SoulWars.blueTeam.contains(player)
					&& SoulWars.blueTeam.contains(target)) {
				player.getInventory().delete(14640, 1);
			}
			break;
		case 4155:
			if (player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString() != null
			&& player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString()
			.equals("null"))
				player.getAdvancedSkills().getSlayer().getDuoSlayer().setDuoPartner(null);
			if (player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString() != null) {
				player.getPacketSender().sendMessage(
						"You already have a duo partner.");
				return;
			}
			Player duoPartner = World.getPlayers().get(targetIndex);
			if (duoPartner != null) {
				if (duoPartner.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString() != null) {
					player.getPacketSender().sendMessage(
							"This player already has a duo partner.");
					return;
				}
				if (duoPartner.getAttributes().getInterfaceId() > 0
						|| duoPartner.getLocation() != null && duoPartner.getLocation() == Location.WILDERNESS) {
					player.getPacketSender().sendMessage(
							"This player is currently busy.");
					return;
				}
				DialogueManager.start(duoPartner,
						SlayerDialogues.inviteDuo(duoPartner, player));
				player.getPacketSender().sendMessage(
						"You have invited " + duoPartner.getUsername()
						+ " to join your Slayer duo team.");
			}
			break;
		case 962:
			if(!player.getInventory().contains(962) || player.getRights() == PlayerRights.ADMINISTRATOR)
				return;
			player.setPositionToFace(target.getPosition());
			player.performGraphic(new Graphic(1006));
			player.performAnimation(new Animation(451));
			player.getPacketSender().sendMessage("You pull the Christmas cracker...");
			target.getPacketSender().sendMessage(""+player.getUsername()+" pulls a Christmas cracker on you..");
			player.getInventory().delete(962, 1);
			if(Misc.getRandom(1) == 1) {
				target.getPacketSender().sendMessage("The cracker explodes and you receive a Party hat!");
				target.getInventory().add((1038 + Misc.getRandom(5)*2), 1);
				player.getPacketSender().sendMessage(""+target.getUsername()+" has received a Party hat!");
			} else {
				player.getPacketSender().sendMessage("The cracker explodes and you receive a Party hat!");
				player.getInventory().add((1038 + Misc.getRandom(5)*2), 1);			
				target.getPacketSender().sendMessage(""+player.getUsername()+" has received a Party hat!");
			}
			break;
		default:
			// logger.info("Unhandled item on player item action - [itemId, interfaceId, player index, inventory slot] : ["
			// + itemId + ", " + interfaceId + ", " + targetIndex + ", " + slot
			// + "].");
			if (player.getRights() == PlayerRights.DEVELOPER)
				player.getPacketSender()
				.sendMessage(
						"Unhandled item on player item action - [itemId, interfaceId, player index, inventory slot] : ["
								+ itemId
								+ ", "
								+ interfaceId
								+ ", "
								+ targetIndex + ", " + slot + "].");
			break;
		}
	}

	@Override
	public void execute(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		switch (packet.getOpcode()) {
		case ITEM_ON_ITEM:
			itemOnItem(player, packet);
			break;
		case USE_ITEM:
			useItem(player, packet);
			break;
		case ITEM_ON_OBJECT:
			itemOnObject(player, packet);
			break;
		case ITEM_ON_GROUND_ITEM:
			// TODO
			break;
		case ITEM_ON_NPC:
			itemOnNpc(player, packet);
			break;
		case ITEM_ON_PLAYER:
			itemOnPlayer(player, packet);
			break;
		}
	}

	public final static int USE_ITEM = 122;

	public final static int ITEM_ON_NPC = 57;

	public final static int ITEM_ON_ITEM = 53;

	public final static int ITEM_ON_OBJECT = 192;

	public final static int ITEM_ON_GROUND_ITEM = 25;

	public static final int ITEM_ON_PLAYER = 14;
}
