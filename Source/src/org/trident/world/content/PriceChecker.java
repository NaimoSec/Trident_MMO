package org.trident.world.content;

import org.trident.model.Item;
import org.trident.util.Misc;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.entity.player.Player;

/**
 * @author: Gabbe
 * Handles the ingame pricechecker
 */
public class PriceChecker {

	/** 
	 * Array details:
	 * 0: Price checker slot
	 * 1: frameId for sending an item's wealth
	 * 2: frameId for sending an item
	 */
	private static final int frames[][] = {
		{0, 18353, 18246}, {1, 18356, 18500},{2, 18359, 18501}, {3, 18362, 18502 }, {4, 18365, 18503}, {5, 18368, 18504},
		{6, 18371, 18505}, {7, 18374, 18506}, {8, 18377, 18507}, {9, 18380, 18508}, {10, 18383, 18509}, {11, 18386, 18510},
		{12, 18389, 18511 }, {13, 18392, 18512}, {14, 18395, 18513}, {15, 18398, 18514}, {16, 18401, 18515}, {17, 18404, 18516},
		{18, 18407, 18517}, {19, 18410, 18518}
	};

	/**
	 * @param itemId
	 * @return item id's array slot
	 */
	public static int getSlot(Player player, int itemId) {
		int j = -1;
		for(int i = 0; i < player.getAttributes().getPriceCheckedItems().size(); i++) {
			if(player.getAttributes().getPriceCheckedItems().get(i).getId() == itemId) {
				j = i;
			}
		}		
		return j;
	}

	/**
	 * @param array's index
	 * @return frame to send item's wealth to
	 */
	public static int getFrameID(int itemSlot) {
		int k = -1;
		for(int i = 0; i < frames.length; i++) {
			if(itemSlot == frames[i][0]) {
				k = frames[i][1];
			}
		}
		return k;
	}

	/**
	 * @param array's index
	 * @return frame to send item to
	 */
	public static int getItemFrameID(int itemSlot) {
		int k = -1;
		for(int i = 0; i < frames.length; i++) {
			if(itemSlot == frames[i][0]) {
				k = frames[i][2];
			}
		}
		return k;
	}

	/**
	 * 
	 * @param itemId
	 * @return amount of item in the array
	 */
	public static int getItemAmount(Player player, int itemId) {
		int amount = 0;
		for(Item item : player.getAttributes().getPriceCheckedItems()) {
			if(item.getDefinition().getId() == itemId) {
				if(item.getDefinition().isStackable())
					amount = item.getAmount();
				else
					amount++;
			}
		}
		return amount;
	}

	/**
	 * @return total items wealth (calculates all items in the price checker)
	 */
	public static int calculateTotalWealth(Player player) {
		int k = 0;
		for(Item item: player.getAttributes().getPriceCheckedItems())
			k += item.getDefinition().getValue() * item.getAmount();
		if(k >= Integer.MAX_VALUE) {
			player.getPacketSender().sendString(18351, "Too High!");
			return 0;
		}
		return k;
	}

	/**
	 * Opens the Price checker
	 */
	public static void openPriceChecker(Player player) {
		if(player.getAttributes().isBanking() || player.getAttributes().isShopping() || player.getTrading().inTrade() || player.getAdvancedSkills().getSummoning().isStoring())
			return;
		player.getSkillManager().stopSkilling();
		if(Dungeoneering.doingDungeoneering(player)) {
			player.getPacketSender().sendMessage("You cannot do this at the moment.");
			return;
		}
		if(player.getAttributes().getInterfaceId() != -1) {
			player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
			return;
		}
		if(player.getAttributes().isPriceChecking()) {
			closePriceChecker(player);
			return;
		}
		if(player.getAttributes().getNewPlayerDelay() > 0) {
			player.getPacketSender().sendMessage("");
			player.getPacketSender().sendMessage("NOTE: Prices shown are the real item values.");
			player.getPacketSender().sendMessage("When you sell an item to a shop, you lose 20% of the item's real value.");
		}
		player.getAttributes().getPriceCheckedItems().clear();
		player.getPacketSender().sendInterfaceRemoval();
		player.getPacketSender().sendInterface(42000);
		for(int i = 0; i < frames.length; i++) {
			player.getPacketSender().sendString(getFrameID(i), "");
			player.getPacketSender().sendString(getFrameID(i)+1, "");
			player.getPacketSender().sendItemOnInterface(getItemFrameID(i), -1, 1);
		}
		player.getPacketSender().sendString(18413, "Click an item in your inventory to view it's wealth.");
		player.getPacketSender().sendString(18351, ""+Misc.insertCommasToNumber(Integer.toString(calculateTotalWealth(player))));
		player.getPacketSender().sendInterfaceSet(42000, 3321);
		player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
		player.getAttributes().setPriceChecking(true);
	}

	/**
	 * Closing the Price checker
	 */
	public static void closePriceChecker(Player player) {
		if(player.getAttributes().isBanking() || player.getAttributes().isShopping() || player.getTrading().inTrade())
			return;
		if(player.getAttributes().isPriceChecking()) {
			for (Item item : player.getAttributes().getPriceCheckedItems()) {
				if (item.getAmount() < 1)
					continue;
				player.getInventory().add(item);
				player.getAttributes().getPriceCheckedItems().remove(item);
			}
			player.getAttributes().getPriceCheckedItems().clear();
			player.getAttributes().setPriceChecking(false);
			player.getPacketSender().sendInterfaceRemoval();
			for(int i = 0; i < frames.length; i++) {
				player.getPacketSender().sendString(getFrameID(i), "");
				player.getPacketSender().sendString(getFrameID(i)+1, "");
				player.getPacketSender().sendItemOnInterface(getItemFrameID(i), -1, 1);
			}
			player.getPacketSender().sendString(18413, "Click an item in your inventory to view it's wealth.");
			player.getPacketSender().sendString(18351, ""+Misc.insertCommasToNumber(Integer.toString(calculateTotalWealth(player))));
		}
	}

	/**
	 * Price checks an item
	 * @param itemToCheck
	 * @param amount to price check
	 */
	public static void priceCheckItem(Player player, int itemId, int amount, int slot) {
		if(player.getAttributes().isBanking() || player.getAttributes().isShopping() || player.getTrading().inTrade() || player.getAdvancedSkills().getSummoning().isStoring())
			return;
		if(!player.getInventory().contains(itemId) || slot < 0)
			return;
		if(player.getAttributes().isPriceChecking()) {
			Item itemToCheck = player.getInventory().getItems()[slot];
			if(itemToCheck.getId() != itemId || itemToCheck.getAmount() <= 0)
				return;
			if(ItemLending.borrowedItem(player, itemToCheck.getId())) {
				player.getPacketSender().sendMessage("This item cannot be valued.");
				return;
			}
			if(!itemToCheck.sellable()) {
				player.getPacketSender().sendMessage("This item cannot be sold to a store, and does therefore not have a price.");
				return;
			}
			if(itemToCheck.getDefinition() == null)
				return;
			boolean itemExists = false;
			if(itemToCheck.getDefinition().isStackable())
				for (Item item : player.getAttributes().getPriceCheckedItems()) {
					if (item.getId() == itemToCheck.getId()) {
						itemExists = true;
						break;
					}
				}
			if(player.getAttributes().getPriceCheckedItems().size() == 20 && !itemExists)
				return;
			if(!itemToCheck.getDefinition().isStackable() && amount + player.getAttributes().getPriceCheckedItems().size() >= 20) {
				amount = 20-player.getAttributes().getPriceCheckedItems().size();
			}
			if (player.getInventory().getAmount(itemToCheck.getId()) < amount) {
				amount = player.getInventory().getAmount(itemToCheck.getId());
				if (amount == 0 || player.getInventory().getAmount(itemToCheck.getId()) < amount)
					return;
			}
			if (!itemToCheck.getDefinition().isStackable()) {
				for (int a = 0; a < amount && a < 28; a++) {
					if (player.getInventory().getAmount(itemToCheck.getId()) >= 1) {
						player.getAttributes().getPriceCheckedItems().add(new Item(itemToCheck.getId(), 1));
						player.getInventory().delete(itemToCheck.getId(), 1);
					}
				}
			} else {
				if (itemToCheck.getDefinition().isStackable() || itemToCheck.getDefinition().isNoted()) {
					boolean itemInPC = false;
					for (Item item : player.getAttributes().getPriceCheckedItems()) {
						if (item.getId() == itemToCheck.getId()) {
							itemInPC = true;
							item.setAmount(item.getAmount() + amount);
							player.getInventory().delete(itemToCheck.getId(), amount);
							break;
						}
					}
					if (!itemInPC) {
						player.getAttributes().getPriceCheckedItems().add(new Item(itemToCheck.getId(), amount));
						player.getInventory().delete(itemToCheck.getId(), amount);
					}
				}
			}
			updatePriceChecker(player);
			player.getPacketSender().sendString(18413, "");
			player.getPacketSender().sendInterfaceSet(42000, 3321);
			player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
		}
	}


	public static void removeItem(Player player, int itemId, int amount) {
		if(player.getAttributes().isBanking() || player.getAttributes().isShopping() || player.getTrading().inTrade() || player.getAdvancedSkills().getSummoning().isStoring())
			return;
		if(amount <= 0)
			return;
		Item itemToRem = null;
		for (Item item : player.getAttributes().getPriceCheckedItems()) {
			if (item != null && item.getAmount() > 0 && item.getId() == itemId) {
				itemToRem = item;
				break;
			}
		}
		if(itemToRem == null || itemToRem.getDefinition() == null)
			return;
		if(player.getAttributes().isPriceChecking()) {
			if (!itemToRem.getDefinition().isStackable()) {
				if (amount > 28)
					amount = 28;
				for (int a = 0; a < amount; a++) {
					for (Item item : player.getAttributes().getPriceCheckedItems()) {
						if (item.getId() == itemToRem.getId()) {
							if (!item.getDefinition().isStackable()) {
								player.getAttributes().getPriceCheckedItems().remove(item);
								player.getInventory().add(itemToRem.getId(), 1);
							} else {
								if (item.getAmount() > amount) {
									item.setAmount(item.getAmount() - amount);
									player.getInventory().add(itemToRem.getId(), amount);
								} else {
									amount = item.getAmount();
									player.getAttributes().getPriceCheckedItems().remove(item);
									player.getInventory().add(itemToRem.getId(), amount);
								}
							}
							break;
						}
					}
				}
			} else {
				for (Item item : player.getAttributes().getPriceCheckedItems()) {
					if (item.getId() == itemToRem.getId()) {

						if (item.getAmount() > amount) {
							item.setAmount(item.getAmount() - amount);
							player.getInventory().add(itemToRem.getId(), amount);
						} else {
							amount = item.getAmount();
							player.getAttributes().getPriceCheckedItems().remove(item);
							player.getInventory().add(itemToRem.getId(), amount);
						}
						break;
					}
				}
			}
			player.getPacketSender().sendInterfaceSet(42000, 3321);
			player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
			if(player.getAttributes().getPriceCheckedItems().size() == 0)
				player.getPacketSender().sendString(18413, "Click an item in your inventory to view it's wealth.");
			updatePriceChecker(player);
		}
	}

	/**
	 * Update Price checker interface, sends items and their wealth. Also sends total value.
	 */
	public static void updatePriceChecker(Player player) {
		if(player.getAttributes().isPriceChecking()) {
			for(int i = 0; i < frames.length; i++) {
				player.getPacketSender().sendString(getFrameID(i), "");
				player.getPacketSender().sendString(getFrameID(i)+1, "");
				player.getPacketSender().sendItemOnInterface(getItemFrameID(i), -1, 1);
			}
			if(player.getAttributes().getPriceCheckedItems().size() > 0) {
				Item itemToPC;
				for(int i = 0; i < player.getAttributes().getPriceCheckedItems().size(); i++) {
					itemToPC = new Item(player.getAttributes().getPriceCheckedItems().get(i).getId());
					if(itemToPC.getDefinition().isStackable()) {
						int itemAmount = player.getAttributes().getPriceCheckedItems().get(i).getAmount();
						int totalPrice = itemToPC.getDefinition().getValue() * itemAmount;
						int frame = getFrameID(i);
						player.getPacketSender().sendString(frame, ""+itemToPC.getDefinition().getValue()+" x"+itemAmount);
						player.getPacketSender().sendString(frame+1, "= "+totalPrice);
						player.getPacketSender().sendItemOnInterface(getItemFrameID(i), itemToPC.getId(), itemAmount);
					} else {
						player.getPacketSender().sendString(getFrameID(i), ""+Misc.insertCommasToNumber(Integer.toString(itemToPC.getDefinition().getValue()))+"");
						player.getPacketSender().sendItemOnInterface(getItemFrameID(i), itemToPC.getId(), 1);
					}
				}
				itemToPC = null;
			} else {
				//There's only one item in the array, and player is trying to remove it, so reset the interface
				for(int i = 0; i < frames.length; i++) {
					player.getPacketSender().sendString(getFrameID(i), "");
					player.getPacketSender().sendString(getFrameID(i)+1, "");
					player.getPacketSender().sendItemOnInterface(getItemFrameID(i), -1, 1);
				}
				player.getPacketSender().sendString(18413, "Click an item in your inventory to view it's wealth.");
			}
			player.getPacketSender().sendString(18351, ""+Misc.insertCommasToNumber(Integer.toString(calculateTotalWealth(player))));
		}
	}
}
