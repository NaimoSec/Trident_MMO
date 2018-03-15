package org.trident.net.packet.impl;

import org.trident.model.Flag;
import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.model.container.impl.Bank;
import org.trident.model.container.impl.Equipment;
import org.trident.model.container.impl.Shop;
import org.trident.model.inputhandling.impl.EnterAmountToBank;
import org.trident.model.inputhandling.impl.EnterAmountToBuyFromShop;
import org.trident.model.inputhandling.impl.EnterAmountToPriceCheck;
import org.trident.model.inputhandling.impl.EnterAmountToRemoveFromBank;
import org.trident.model.inputhandling.impl.EnterAmountToRemoveFromPriceChecker;
import org.trident.model.inputhandling.impl.EnterAmountToRemoveFromStake;
import org.trident.model.inputhandling.impl.EnterAmountToRemoveFromTrade;
import org.trident.model.inputhandling.impl.EnterAmountToSellToShop;
import org.trident.model.inputhandling.impl.EnterAmountToStake;
import org.trident.model.inputhandling.impl.EnterAmountToStore;
import org.trident.model.inputhandling.impl.EnterAmountToTrade;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.content.BonusManager;
import org.trident.world.content.PriceChecker;
import org.trident.world.content.Trading;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.combat.CombatExtras;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.minigames.impl.Dueling;
import org.trident.world.content.minigames.impl.FishingTrawler;
import org.trident.world.content.skills.impl.crafting.Jewelry;
import org.trident.world.content.skills.impl.smithing.EquipmentMaking;
import org.trident.world.content.skills.impl.smithing.SmithingData;
import org.trident.world.content.skills.impl.summoning.SummoningData;
import org.trident.world.entity.player.Player;

public class ItemContainerActionPacketListener implements PacketListener {

	/**
	 * The PacketListener logger to debug information and print out errors.
	 */
	//private final static Logger logger = Logger.getLogger(ItemContainerActionPacketListener.class);

	/**
	 * Manages an item's first action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void firstAction(Player player, Packet packet) {
		int interfaceId = packet.readShortA();
		int slot = packet.readShortA();
		int id = packet.readShortA();
		Item item = new Item(id);
		switch (interfaceId) {
		case 4640:
			FishingTrawler.RewardsHandler.withdrawRewardItem(player, id, slot, false);
			break;
		case 3323:
			/*	if(player.getTrading().getItemLending().loanState > 1) {
				if(player.getTrading().getItemLending().temporarLendItem != null) {
					if(player.getTrading().getItemLending().temporarLendItem.getId() == item.getId()) {
						Player player2 = player.getTrading().getTradeWith() > 0 ? GameServer.getWorld().getPlayers()[player.getTrading().getTradeWith()] : null;
						if(player2 == null)
							return;
						player.getTrading().getItemLending().loanState = 0;
						player.getTrading().getItemLending().temporarLendItem = null;
						player.getInventory().add(item.getId(), 1);
						player.getInventory().refreshItems();
						player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
						player.getTrading().getItemLending().refreshLoanBoxes();
						player.getTrading().getItemLending().refreshTrade(player2);
					}
				}
			}*/
			break;
		case 3324:
			break;
		case 4233:
			Jewelry.jewelryMaking(player, "RING", id, 1);
			break;
		case 4239:
			Jewelry.jewelryMaking(player, "NECKLACE", id, 1);
			break;
		case 4245:
			Jewelry.jewelryMaking(player, "AMULET", id, 1);
			break;
		case Equipment.INVENTORY_INTERFACE_ID:
			item = slot < 0 ? null : player.getEquipment().getItems()[slot];
			if(item == null || item.getId() != id)
				return;
			if(slot == Equipment.CAPE_SLOT && (player.getLocation() == Location.SOULWARS || player.getLocation() == Location.SOULWARS_WAIT)) {
				player.getPacketSender().sendMessage("You can not unequip this item right now.");
				return;
			}
			boolean stackItem = item.getDefinition().isStackable() && player.getInventory().getAmount(item.getId()) > 0;
			int inventorySlot = player.getInventory().getEmptySlot();
			if (inventorySlot != -1) {
				Item itemReplacement = new Item(-1, 0);
				player.getEquipment().setItem(slot, itemReplacement);
				if(!stackItem)
					player.getInventory().setItem(inventorySlot, item);
				else
					player.getInventory().add(item.getId(), item.getAmount());
				if (item.getDefinition().getEquipmentSlot() == Equipment.WEAPON_SLOT) {
					WeaponHandler.update(player);
					if (player.getCombatAttributes().hasStaffOfLightEffect() 
							&& item.getDefinition().getName().toLowerCase().contains("staff of light")) {
						player.getCombatAttributes().setStaffOfLightEffect(false);
						player.getPacketSender().sendMessage("You feel the spirit of the staff of light begin to fade away...");
					}
				}
				BonusManager.update(player);
				player.getEquipment().refreshItems();
				player.getInventory().refreshItems();
				player.getUpdateFlag().flag(Flag.APPEARANCE);
				SoundEffects.sendSoundEffect(player, SoundEffects.SoundData.EQUIP_ITEM, 10, 0);
				if(item.getDefinition().getEquipmentSlot() == Equipment.WEAPON_SLOT) {
					player.getPlayerCombatAttributes().setUsingSpecialAttack(false);
					WeaponHandler.update(player);
				}
			} else {
				player.getInventory().full();
			}
			break;
		case 4393: //Price checker
			if (player.getAttributes().isBanking() || player.getTrading().inTrade() || player.getAdvancedSkills().getSummoning().isStoring() || player.getAttributes().isShopping())
				return;
			if(player.getAttributes().isPriceChecking())
				PriceChecker.removeItem(player, id, 1);
			break;
		case 2700: //Summoning BoB
			if (player.getAttributes().isBanking() || player.getTrading().inTrade() || player.getAttributes().isPriceChecking() || player.getAttributes().isShopping())
				return;
			if(player.getAdvancedSkills().getSummoning().isStoring()) {
				player.getAdvancedSkills().getSummoning().removeStoredItem(id, 1);
			}
			break;
		case Bank.INTERFACE_ID:
			if (!player.getAttributes().isBanking() || player.getAttributes().getInterfaceId() != 5292)
				break;
			player.getBank(player.getAttributes().getCurrentBankTab()).switchItem(player.getInventory(), item, slot, true, true);
			player.getBank(player.getAttributes().getCurrentBankTab()).open();
			break;
		case Bank.INVENTORY_INTERFACE_ID:
			if (!player.getAttributes().isBanking() || player.getTrading().inTrade() || !player.getInventory().contains(item.getId()) || player.getAttributes().getInterfaceId() != 5292)
				return;
			player.getAttributes().setCurrentBankTab(Bank.getTabForItem(player, item.getId()));
			player.getInventory().switchItem(player.getBank(player.getAttributes().getCurrentBankTab()), item, slot, false, true);
			break;
		case Shop.ITEM_CHILD_ID:
			if(player.getAttributes().getShop() != null)
				player.getAttributes().getShop().checkValue(player, slot, false);
			break;
		case Shop.INVENTORY_INTERFACE_ID:
			if(player.getAttributes().getShop() != null)
				player.getAttributes().getShop().checkValue(player, slot, true);
			break;
		case Trading.INTERFACE_ID:
			if (player.getAttributes().isBanking() || player.getAttributes().isShopping())
				return;
			if(Dueling.checkDuel(player, 1)) {
				player.getDueling().stakeItem(id, 1, slot);
				return;
			}
			if(player.getAdvancedSkills().getSummoning().isStoring()) {
				player.getAdvancedSkills().getSummoning().storeItem(id, 1, slot);
				return;
			}
			if(player.getAttributes().isPriceChecking()) {
				PriceChecker.priceCheckItem(player, id, 1, slot);
				return;
			}
			if(player.getTrading().inTrade()) {
				player.getTrading().tradeItem(item.getId(), 1, slot);
			} else {
				player.getTrading().declineTrade(false);
			}
			break;
		case 6669:
			if(Dueling.checkDuel(player, 1)) {
				player.getDueling().removeStakedItem(id, 1);
				return;
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if (player.getAttributes().isBanking())
				return;
			if(player.getTrading().inTrade()) {
				player.getTrading().removeTradedItem(id, 1);
			} else {
				player.getTrading().declineTrade(false);
			}
			break;
		case 1119: //smithing interface row 1
		case 1120: // row 2
		case 1121: // row 3
		case 1122: // row 4
		case 1123: // row 5
			int barsRequired = SmithingData.getBarAmount(item);
			Item bar = new Item(player.getSkillManager().getSkillAttributes().getSelectedItem(), barsRequired);
			int x = 1;
			if(x > (player.getInventory().getAmount(bar.getId()) / barsRequired))
				x = (player.getInventory().getAmount(bar.getId()) / barsRequired);
			EquipmentMaking.smithItem(player, new Item(player.getSkillManager().getSkillAttributes().getSelectedItem(), barsRequired), new Item(item.getId(), SmithingData.getItemAmount(item)), x);
			break;
		default:
			//logger.info("Unhandled first item action - interfaceId: " + interfaceId);
			if(player.getRights() == PlayerRights.DEVELOPER)
				player.getPacketSender().sendMessage("Unhandled Itemcontainer item action1 - [interfaceId, itemId, slot] : [" + interfaceId + ", " + id + ", " + slot + "]");
			break;
		}
	}

	/**
	 * Manages an item's second action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void secondAction(Player player, Packet packet) {
		int interfaceId = packet.readLEShortA();
		int id = packet.readLEShortA();
		int slot = packet.readLEShort();
		Item item = new Item(id);
		switch (interfaceId) {
		case 4640:
			FishingTrawler.RewardsHandler.withdrawRewardItem(player, id, slot, true);
			break;
		case 1688:

			break;
		case 4233:
			Jewelry.jewelryMaking(player, "RING", id, 5);
			break;
		case 4239:
			Jewelry.jewelryMaking(player, "NECKLACE", id, 5);
			break;
		case 4245:
			Jewelry.jewelryMaking(player, "AMULET", id, 5);
			break;
		case 4393: //Price checker
			if (player.getAttributes().isBanking() || player.getTrading().inTrade() || player.getAdvancedSkills().getSummoning().isStoring() || player.getAttributes().isShopping())
				return;
			if(player.getAttributes().isPriceChecking())
				PriceChecker.removeItem(player, id, 5);
			break;
		case 2700: //Summoning BoB
			if (player.getAttributes().isBanking() || player.getTrading().inTrade() || player.getAttributes().isPriceChecking() || player.getAttributes().isShopping())
				return;
			if(player.getAdvancedSkills().getSummoning().isStoring())
				player.getAdvancedSkills().getSummoning().removeStoredItem(id, 5);
			break;
		case Trading.INTERFACE_ID:
			if (player.getAttributes().isBanking() || player.getAttributes().isShopping())
				return;
			if(Dueling.checkDuel(player, 1)) {
				player.getDueling().stakeItem(id, 5, slot);
				return;
			}
			if(player.getAdvancedSkills().getSummoning().isStoring()) {
				player.getAdvancedSkills().getSummoning().storeItem(id, 5, slot);
				return;
			}
			if(player.getAttributes().isPriceChecking()) {
				PriceChecker.priceCheckItem(player, id, 5, slot);
				return;
			}
			if(player.getTrading().inTrade()) {
				player.getTrading().tradeItem(id, 5, slot);
			} else {
				player.getTrading().declineTrade(false);
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if (player.getAttributes().isBanking())
				return;
			if(player.getTrading().inTrade()) {
				player.getTrading().removeTradedItem(id, 5);
			} else {
				player.getTrading().declineTrade(false);
			}
			break;
		case 6669:
			if(Dueling.checkDuel(player, 1)) {
				player.getDueling().removeStakedItem(id, 5);
				return;
			}
			break;
		case Bank.INTERFACE_ID:
			if (!player.getAttributes().isBanking() || item.getId() != id || player.getTrading().inTrade() || player.getAttributes().getInterfaceId() != 5292)
				return;
			player.getBank(player.getAttributes().getCurrentBankTab()).switchItem(player.getInventory(), new Item(id, 5), slot, true, true);
			player.getBank(player.getAttributes().getCurrentBankTab()).open();
			break;
		case Bank.INVENTORY_INTERFACE_ID:
			item = player.getInventory().forSlot(slot).copy().setAmount(5).copy();
			if (!player.getAttributes().isBanking() || item.getId() != id || player.getTrading().inTrade() || !player.getInventory().contains(item.getId()) || player.getAttributes().getInterfaceId() != 5292)
				return;
			player.getAttributes().setCurrentBankTab(Bank.getTabForItem(player, item.getId()));
			player.getInventory().switchItem(player.getBank(player.getAttributes().getCurrentBankTab()), item, slot, false, true);
			break;
		case Shop.ITEM_CHILD_ID:
			if(player.getAttributes().getShop() == null)
				return;
			item = player.getAttributes().getShop().forSlot(slot).copy().setAmount(1).copy();
			player.getAttributes().getShop().setPlayer(player).switchItem(player.getInventory(), item, slot, false, true);
			break;
		case Shop.INVENTORY_INTERFACE_ID:
			if(player.getAttributes().isShopping()) {
				player.getAttributes().getShop().sellItem(player, slot, 1);
				return;
			}
			break;
		case 1119: //smithing interface row 1
		case 1120: // row 2
		case 1121: // row 3
		case 1122: // row 4
		case 1123: // row 5
			int barsRequired = SmithingData.getBarAmount(item);
			Item bar = new Item(player.getSkillManager().getSkillAttributes().getSelectedItem(), barsRequired);
			int x = 5;
			if(x > (player.getInventory().getAmount(bar.getId()) / barsRequired))
				x = (player.getInventory().getAmount(bar.getId()) / barsRequired);
			EquipmentMaking.smithItem(player, new Item(player.getSkillManager().getSkillAttributes().getSelectedItem(), barsRequired), new Item(item.getId(), SmithingData.getItemAmount(item)), x);
			break;
		default:
			//logger.info("Unhandled second item action - interfaceId: " + interfaceId);
			if(player.getRights() == PlayerRights.DEVELOPER)
				player.getPacketSender().sendMessage("Unhandled Itemcontainer item action2 - [interfaceId, itemId, slot] : [" + interfaceId + ", " + id + ", " + slot + "]");
			break;
		}
	}

	/**
	 * Manages an item's third action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void thirdAction(Player player, Packet packet) {
		int interfaceId = packet.readLEShort();
		int id = packet.readShortA();
		int slot = packet.readShortA();
		Item item1 = new Item(id);
		switch (interfaceId) {
		case 4233:
			Jewelry.jewelryMaking(player, "RING", id, 10);
			break;
		case 4239:
			Jewelry.jewelryMaking(player, "NECKLACE", id, 10);
			break;
		case 4245:
			Jewelry.jewelryMaking(player, "AMULET", id, 10);
			break;
		case 1688:
			if(item1.getId() == 11283) { //Operate DFS
				int charges = player.getAttributes().getDragonFireCharges();
				if(charges >= 30 || player.getRights() == PlayerRights.DEVELOPER) {
					if(player.getCombatAttributes().getLastAttacker() != null)
						CombatExtras.handleDragonFireShield(player, player.getCombatAttributes().getLastAttacker());
					else
						player.getPacketSender().sendMessage("You can only use this in combat.");
				} else
					player.getPacketSender().sendMessage("Your shield doesn't have enough power yet. It has "+player.getAttributes().getDragonFireCharges()+"/30 dragon-fire charges.");
			}
			break;
		case 2700: //Summoning BoB
			if (player.getAttributes().isBanking() || player.getTrading().inTrade() || player.getAttributes().isPriceChecking() || player.getAttributes().isShopping())
				return;
			if(player.getAdvancedSkills().getSummoning().isStoring()) {
				player.getAdvancedSkills().getSummoning().removeStoredItem(id, 10);
			}
			break;
		case 4393: //Price checker
			if (player.getAttributes().isBanking() || player.getTrading().inTrade() || player.getAdvancedSkills().getSummoning().isStoring() || player.getAttributes().isShopping())
				return;
			if(player.getAttributes().isPriceChecking()) {
				PriceChecker.removeItem(player, id, 10);
			}
			break;
		case Trading.INTERFACE_ID:
			if (player.getAttributes().isBanking() || player.getAttributes().isShopping())
				return;
			if(Dueling.checkDuel(player, 1)) {
				player.getDueling().stakeItem(id, 10, slot);
				return;
			}
			if(player.getAdvancedSkills().getSummoning().isStoring()) {
				player.getAdvancedSkills().getSummoning().storeItem(id, 10, slot);
				return;
			}
			if(player.getAttributes().isPriceChecking()) {
				PriceChecker.priceCheckItem(player, id, 10, slot);
				return;
			}
			if(player.getTrading().inTrade()) {
				player.getTrading().tradeItem(id, 10, slot);
			} else {
				player.getTrading().declineTrade(false);
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade()) {
				player.getTrading().removeTradedItem(id, 10);
			} else {
				player.getTrading().declineTrade(false);
			}
			break;
		case 6669:
			if(Dueling.checkDuel(player, 1)) {
				player.getDueling().removeStakedItem(id, 10);
				return;
			}
			break;
		case Bank.INTERFACE_ID:
			if (!player.getAttributes().isBanking() || player.getTrading().inTrade() || player.getAttributes().getInterfaceId() != 5292)
				return;
			player.getBank(player.getAttributes().getCurrentBankTab()).switchItem(player.getInventory(), new Item(id, 10), slot, true, true);
			player.getBank(player.getAttributes().getCurrentBankTab()).open();
			break;
		case Bank.INVENTORY_INTERFACE_ID:
			Item item = player.getInventory().forSlot(slot).copy().setAmount(10).copy();
			if (!player.getAttributes().isBanking() || item.getId() != id || player.getTrading().inTrade() || !player.getInventory().contains(item.getId()) || player.getAttributes().getInterfaceId() != 5292)
				return;
			player.getAttributes().setCurrentBankTab(Bank.getTabForItem(player, item.getId()));
			player.getInventory().switchItem(player.getBank(player.getAttributes().getCurrentBankTab()), item, slot, false, true);
			break;
		case Shop.ITEM_CHILD_ID:
			if(player.getAttributes().getShop() == null)
				return;
			item = player.getAttributes().getShop().forSlot(slot).copy().setAmount(5).copy();
			player.getAttributes().getShop().setPlayer(player).switchItem(player.getInventory(), item, slot, false, true);
			break;
		case Shop.INVENTORY_INTERFACE_ID:
			if(player.getAttributes().isShopping()) {
				player.getAttributes().getShop().sellItem(player, slot, 5);
				return;
			}
			break;
		case 1119: //smithing interface row 1
		case 1120: // row 2
		case 1121: // row 3
		case 1122: // row 4
		case 1123: // row 5
			int barsRequired = SmithingData.getBarAmount(item1);
			Item bar = new Item(player.getSkillManager().getSkillAttributes().getSelectedItem(), barsRequired);
			int x = 10;
			if(x > (player.getInventory().getAmount(bar.getId()) / barsRequired))
				x = (player.getInventory().getAmount(bar.getId()) / barsRequired);
			EquipmentMaking.smithItem(player, new Item(player.getSkillManager().getSkillAttributes().getSelectedItem(), barsRequired), new Item(item1.getId(), SmithingData.getItemAmount(item1)), x);
			break;
		default:
			//logger.info("Unhandled third item action - interfaceId: " + interfaceId);
			if(player.getRights() == PlayerRights.DEVELOPER)
				player.getPacketSender().sendMessage("Unhandled Itemcontainer item action3 - [interfaceId, itemId, slot] : [" + interfaceId + ", " + id + ", " + slot + "]");
			break;
		}
	}

	/**
	 * Manages an item's fourth action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void fourthAction(Player player, Packet packet) {
		int slot = packet.readShortA();
		int interfaceId = packet.readShort();
		int id = packet.readShortA();
		switch (interfaceId) {
		case 2700: //Summoning BoB
			if (player.getAttributes().isBanking() || player.getTrading().inTrade() || player.getAttributes().isPriceChecking() || player.getAttributes().isShopping())
				return;
			if(player.getAdvancedSkills().getSummoning().isStoring()) {
				player.getAdvancedSkills().getSummoning().removeStoredItem(id, SummoningData.getItemAmount(player, id));
			}
			break;
		case 4393: //Price checker
			if (player.getAttributes().isBanking() || player.getTrading().inTrade() || player.getAdvancedSkills().getSummoning().isStoring() || player.getAttributes().isShopping())
				return;
			if(player.getAttributes().isPriceChecking()) {
				PriceChecker.removeItem(player, id, PriceChecker.getItemAmount(player, id));
			}
			break;
		case Trading.INTERFACE_ID:
			if (player.getAttributes().isBanking() || player.getAttributes().isShopping())
				return;
			if(Dueling.checkDuel(player, 1)) {
				player.getDueling().stakeItem(id, player.getInventory().getAmount(id), slot);
				return;
			}
			if(player.getAdvancedSkills().getSummoning().isStoring()) {
				player.getAdvancedSkills().getSummoning().storeItem(id, player.getInventory().getAmount(id), slot);
				return;
			}
			if(player.getAttributes().isPriceChecking()) {
				PriceChecker.priceCheckItem(player, id, player.getInventory().getAmount(id), slot);
				return;
			}
			if(player.getTrading().inTrade()) {
				player.getTrading().tradeItem(id, player.getInventory().getAmount(id), slot);
			} else {
				player.getTrading().declineTrade(false);
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade()) {
				Item itemToRemove = new Item(id);
				if(itemToRemove.getDefinition().isStackable()) {
					for (Item item : player.getTrading().offeredItems) {
						if(item.getId() == id) {
							player.getTrading().removeTradedItem(id, player.getTrading().offeredItems.get(slot).getAmount());
							break;
						}
					}
				} else {
					for (Item item : player.getTrading().offeredItems) {
						if(item.getId() == id) {
							player.getTrading().removeTradedItem(id, 28);
							break;
						}
					}
				}
			} else {
				player.getTrading().declineTrade(false);
			}
			break;
		case Bank.INTERFACE_ID:
			if (!player.getAttributes().isBanking() || player.getBank(Bank.getTabForItem(player, id)).getAmount(id) <= 0 || player.getTrading().inTrade() || player.getAttributes().getInterfaceId() != 5292)
				return;
			player.getBank(player.getAttributes().getCurrentBankTab()).switchItem(player.getInventory(), new Item(id, player.getBank(Bank.getTabForItem(player, id)).getAmount(id)), slot, true, true);
			player.getBank(player.getAttributes().getCurrentBankTab()).open();
			break;
		case 6669:
			if(Dueling.checkDuel(player, 1)) {
				Item itemToRemove = new Item(id);
				for (Item item2 : player.getDueling().stakedItems) {
					if(item2.getId() == id) {
						player.getDueling().removeStakedItem(id, itemToRemove.getDefinition().isStackable() ? item2.getAmount() : 28);
						break;
					}
				}
				return;
			}
			break;
		case Bank.INVENTORY_INTERFACE_ID:
			Item item = player.getInventory().forSlot(slot).copy().setAmount(player.getInventory().getAmount(id));
			if (!player.getAttributes().isBanking() || item.getId() != id || player.getTrading().inTrade() || !player.getInventory().contains(item.getId()) || player.getAttributes().getInterfaceId() != 5292)
				return;
			player.getAttributes().setCurrentBankTab(Bank.getTabForItem(player, item.getId()));
			player.getInventory().switchItem(player.getBank(player.getAttributes().getCurrentBankTab()), item, slot, false, true);
			break;
		case Shop.ITEM_CHILD_ID:
			if(player.getAttributes().getShop() == null)
				return;
			item = player.getAttributes().getShop().forSlot(slot).copy().setAmount(10).copy();
			player.getAttributes().getShop().setPlayer(player).switchItem(player.getInventory(), item, slot, true, true);
			break;
		case Shop.INVENTORY_INTERFACE_ID:
			if(player.getAttributes().isShopping()) {
				player.getAttributes().getShop().sellItem(player,slot, 10);
				return;
			}
			break;
		default:
			//	logger.info("Unhandled fourth item action - interfaceId: " + interfaceId);
			if(player.getRights() == PlayerRights.DEVELOPER)
				player.getPacketSender().sendMessage("Unhandled Itemcontainer item action4 - [interfaceId, itemId, slot] : [" + interfaceId + ", " + id + ", " + slot + "]");
			break;
		}
	}


	/**
	 * Manages an item's fifth action.
	 * @param player	The player clicking the item.
	 * @param packet	The packet to read values from.
	 */
	private static void fifthAction(Player player, Packet packet) {
		int slot = packet.readLEShort();
		int interfaceId = packet.readShortA();
		int id = packet.readLEShort();
		switch (interfaceId) {
		case Shop.ITEM_CHILD_ID:
			if(player.getAttributes().isBanking() || player.getAdvancedSkills().getSummoning().isStoring() || player.getAttributes().isPriceChecking())
				return;
			if(player.getAttributes().isShopping()) {
				player.getAttributes().setInputHandling(new EnterAmountToBuyFromShop(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to buy?");
				player.getAttributes().getShop().setPlayer(player);
			}
			break;
		case Trading.INTERFACE_ID:
			if(player.getAttributes().isBanking() || player.getAttributes().isShopping())
				return;
			if(Dueling.checkDuel(player, 1)) {
				player.getAttributes().setInputHandling(new EnterAmountToStake(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to stake?");
				return;
			}
			if(player.getAdvancedSkills().getSummoning().isStoring()) {
				player.getAttributes().setInputHandling(new EnterAmountToStore(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to store?");
				return;
			}
			if(player.getAttributes().isPriceChecking()) {
				player.getAttributes().setInputHandling(new EnterAmountToPriceCheck(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to price check?");
				return;
			}
			if(player.getTrading().inTrade()) {
				player.getAttributes().setInputHandling(new EnterAmountToTrade(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to trade?");
			} else {
				player.getTrading().declineTrade(false);
			}
			break;
		case Trading.INTERFACE_REMOVAL_ID:
			if(player.getTrading().inTrade()) {
				player.getAttributes().setInputHandling(new EnterAmountToRemoveFromTrade(id));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
			} else {
				player.getTrading().declineTrade(false);
			}
			break;
		case 4393:
			if(player.getAttributes().isBanking() || player.getAttributes().isShopping() || player.getAdvancedSkills().getSummoning().isStoring())
				return;
			if(player.getAttributes().isPriceChecking()) {
				player.getAttributes().setInputHandling(new EnterAmountToRemoveFromPriceChecker(id));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
				return;
			}
			break;
		case Bank.INVENTORY_INTERFACE_ID: //BANK X
			if(player.getAttributes().isBanking()) {
				player.getAttributes().setInputHandling(new EnterAmountToBank(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to bank?");
			}
			break;
		case Bank.INTERFACE_ID:
		case 11:
			if(player.getAttributes().isBanking()) {
				if(interfaceId == 11) {
					player.getAttributes().setInputHandling(new EnterAmountToRemoveFromBank(id, slot));
					player.getPacketSender().sendEnterAmountPrompt("How many would you like to withdraw?");
				} else {
					player.getBank(player.getAttributes().getCurrentBankTab()).switchItem(player.getInventory(), new Item(id, player.getBank(Bank.getTabForItem(player, id)).getAmount(id) - 1), slot, true, true);
					player.getBank(player.getAttributes().getCurrentBankTab()).open();
				}
			}
			break;
		case 3823: // shop selling X
			if(player.getAttributes().isBanking() || player.getAdvancedSkills().getSummoning().isStoring() || player.getAttributes().isPriceChecking())
				return;
			if(player.getAttributes().isShopping()) {
				player.getAttributes().setInputHandling(new EnterAmountToSellToShop(id, slot));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to sell?");
				player.getAttributes().getShop().setPlayer(player);
			}
			break;
		case 6669:
			if(Dueling.checkDuel(player, 1)) {
				player.getAttributes().setInputHandling(new EnterAmountToRemoveFromStake(id));
				player.getPacketSender().sendEnterAmountPrompt("How many would you like to remove?");
				return;
			}
			break;

		default:
			//logger.info("Unhandled fifth item action - interfaceId: " + interfaceId);
			if(player.getRights() == PlayerRights.DEVELOPER)
				player.getPacketSender().sendMessage("Unhandled Itemcontainer item action5 - [interfaceId, itemId, slot] : [" + interfaceId + ", " + id + ", " + slot + "]");
			break;
		}
	}

	@Override
	public void execute(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		switch (packet.getOpcode()) {
		case FIRST_ITEM_ACTION_OPCODE:
			firstAction(player, packet);
			break;
		case SECOND_ITEM_ACTION_OPCODE:
			secondAction(player, packet);
			break;
		case THIRD_ITEM_ACTION_OPCODE:
			thirdAction(player, packet);
			break;
		case FOURTH_ITEM_ACTION_OPCODE:
			fourthAction(player, packet);
			break;
		case FIFTH_ITEM_ACTION_OPCODE:
			fifthAction(player, packet);
			break;
		}
	}

	public static final int FIRST_ITEM_ACTION_OPCODE = 145;
	public static final int SECOND_ITEM_ACTION_OPCODE = 117;
	public static final int THIRD_ITEM_ACTION_OPCODE = 43;
	public static final int FOURTH_ITEM_ACTION_OPCODE = 129;
	public static final int FIFTH_ITEM_ACTION_OPCODE = 135;
}
