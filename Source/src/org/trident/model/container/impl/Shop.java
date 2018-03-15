package org.trident.model.container.impl;

import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.ShopRestockTask;
import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.model.container.ItemContainer;
import org.trident.model.container.StackType;
import org.trident.model.definitions.ItemDefinition;
import org.trident.model.definitions.ShopManager;
import org.trident.model.inputhandling.impl.EnterAmountToBuyFromShop;
import org.trident.model.inputhandling.impl.EnterAmountToSellToShop;
import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.Achievements;
import org.trident.world.content.CustomShops;
import org.trident.world.content.ItemLending;
import org.trident.world.content.Achievements.Tasks;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.quests.RecipeForDisaster;
import org.trident.world.entity.player.Player;

/**
 * Messy but perfect Shop System
 * @author Gabbe
 */

public class Shop extends ItemContainer {

	/*
	 * The shop constructor
	 */
	public Shop(Player player, int id, String name, Item currency, Item[] stockItems) {
		super(player);
		if (stockItems.length > 50)
			throw new ArrayIndexOutOfBoundsException("Stock cannot have more than 101 items; check shop[" + id + "]: stockLength: " + stockItems.length);
		this.id = id;
		this.name = name.length() > 0 ? name : "General Store";
		this.currency = currency;
		this.originalStock = new Item[stockItems.length];
		for(int i = 0; i < stockItems.length; i++) {
			Item item = new Item(stockItems[i].getId(), stockItems[i].getAmount());
			add(item, false);
			this.originalStock[i] = item;
		}
	}

	private final int id;

	private String name;

	private Item currency;

	private Item[] originalStock;

	public Item[] getOriginalStock() {
		return this.originalStock;
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	public Shop setName(String name) {
		this.name = name;
		return this;
	}

	public Item getCurrency() {
		return currency;
	}

	public Shop setCurrency(Item currency) {
		this.currency = currency;
		return this;
	}

	private boolean restockingItems;

	public boolean isRestockingItems() {
		return restockingItems;
	}

	public void setRestockingItems(boolean restockingItems) {
		this.restockingItems = restockingItems;
	}

	/**
	 * Opens a shop for a player
	 * @param player	The player to open the shop for
	 * @return			The shop instance
	 */
	public Shop open(Player player) {
		setPlayer(player);
		getPlayer().getPacketSender().sendInterfaceRemoval().sendClientRightClickRemoval();
		getPlayer().getAttributes().setShop(ShopManager.getShops().get(id)).setInterfaceId(INTERFACE_ID).setShopping(true);
		refreshItems();
		if(player.getAttributes().getNewPlayerDelay() > 0)
			player.getPacketSender().sendMessage("Note: when selling an item to a shop, the item loses 20% of it's real value.");
		return this;
	}

	/**
	 * Refreshes a shop for every player who's viewing it
	 */
	public void publicRefresh() {
		if(id == 21) //Recipe for Disaster chest isn't public
			return;
		Shop publicShop = ShopManager.getShops().get(id);
		if (publicShop == null)
			return;
		publicShop.setItems(getItems());
		for (Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if (player.getAttributes().getShop() != null && player.getAttributes().getShop().id == id && player.getAttributes().isShopping())
				player.getAttributes().getShop().setItems(publicShop.getItems());
		}
	}

	/**
	 * Checks a value of an item in a shop
	 * @param player		The player who's checking the item's value
	 * @param slot			The shop item's slot (in the shop!)
	 * @param sellingItem	Is the player selling the item?
	 */
	public void checkValue(Player player, int slot, boolean sellingItem) {
		this.setPlayer(player);
		Item shopItem = new Item(getItems()[slot].getId());
		if(!player.getAttributes().isShopping()) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		if(!sellingItem)
			if(CustomShops.isCustomShop(this.id)) {
				CustomShops.checkValue(player, this.id, shopItem);
				return;
			}
		if(sellingItem) {
			if(id != 0) {
				player.getPacketSender().sendMessage("Please visit the General store or use the Price checker to value this item.");
				return;
			}
			if(shopItem.getId() == 995)
				return;
			Item itemToSell = player.getInventory().getItems()[slot];
			if(!itemToSell.sellable()) {
				player.getPacketSender().sendMessage("This item cannot be sold to a store.");
				return;
			}
			if(ItemLending.borrowedItem(player, itemToSell.getId())) {
				player.getPacketSender().sendMessage("This item cannot be sold.");
				return;
			}
			int itemValue = ItemDefinition.forId(itemToSell.getId()).getValue();
			double shopPrice = (int) (itemValue * .80);
			if(player!= null && itemValue > 0) {
				player.getPacketSender().sendMessage(""+ItemDefinition.forId(itemToSell.getId()).getName()+": shop will buy for "+(int) shopPrice+" "+currency.getDefinition().getName().toLowerCase()+"."+shopPriceEx((int) shopPrice));
				return;
			}
		}
		int itemValue = ItemDefinition.forId(shopItem.getId()).getValue();
		getPlayer().getPacketSender().sendMessage(""+ItemDefinition.forId(shopItem.getId()).getName()+": currently costs "+itemValue+" "+currency.getDefinition().getName().toLowerCase()+"."+shopPriceEx(itemValue));
	}

	public void sellItem(Player player, int slot, int amountToSell) {
		this.setPlayer(player);
		if(!player.getAttributes().isShopping() || player.getTrading().inTrade() || player.getAttributes().isBanking()) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		if(getPlayer().getRights() == PlayerRights.ADMINISTRATOR) {
			getPlayer().getPacketSender().sendMessage("You cannot sell items.");
			return;
		}
		if(!getPlayer().getAttributes().isShopping() || player.getTrading().inTrade() || player.getAttributes().isBanking() || player.getAdvancedSkills().getSummoning().isStoring() || player.getAttributes().isPriceChecking()) {
			getPlayer().getPacketSender().sendInterfaceRemoval();
			return;
		}
		if(id != 0) {
			player.getPacketSender().sendMessage("You can only sell items to a General store.");
			return;
		}
		Item itemToSell = player.getInventory().getItems()[slot];
		if(!player.getInventory().contains(itemToSell.getId()) || itemToSell.getId() == 995)
			return;
		if(!itemToSell.sellable()) {
			player.getPacketSender().sendMessage("This item cannot be sold to a store.");
			return;
		}
		if(ItemLending.borrowedItem(player, itemToSell.getId())) {
			player.getPacketSender().sendMessage("This item cannot be sold.");
			return;
		}
		if(this.full(itemToSell.getId()))
			return;
		if(player.getInventory().getAmount(itemToSell.getId()) < amountToSell)
			amountToSell = player.getInventory().getAmount(itemToSell.getId());
		if(amountToSell == 0)
			return;
		if(amountToSell > 300) {
			player.getPacketSender().sendMessage("You can only sell 300 "+ItemDefinition.forId(itemToSell.getId()).getName()+"s at a time."); 
			return;
		}
		int itemId = itemToSell.getId();
		int currency = this.getCurrency().getId();
		int itemValue = ItemDefinition.forId(itemToSell.getId()).getValue();
		double shopPrice = (int) (itemValue * .80);
		boolean inventorySpace = false;
		if(!itemToSell.getDefinition().isStackable()) {
			if(!player.getInventory().contains(this.getCurrency().getId()))
				inventorySpace = true;
		}
		if(player.getInventory().getFreeSlots() <= 0 && getPlayer().getInventory().getAmount(this.getCurrency().getId()) > 0)
			inventorySpace = true;
		if(getPlayer().getInventory().getFreeSlots() > 0 || getPlayer().getInventory().getAmount(this.getCurrency().getId()) > 0)
			inventorySpace = true;
		int amountSold = 0;
		for (int i = amountToSell; i > 0; i--) {
			itemToSell = new Item(itemId);
			if(this.full(itemToSell.getId()) || !player.getInventory().contains(itemToSell.getId()) || !player.getAttributes().isShopping())
				break;
			if(!itemToSell.getDefinition().isStackable()) {
				if(inventorySpace) {
					super.switchItem(player.getInventory(), this, itemToSell.getId(), -1);
					if(shopPrice > 0)
						player.getInventory().add(currency, (int) shopPrice);
					if(itemToSell.getId() == 317 && player.getLocation() == Location.LUMBRIDGE)
						Achievements.handleAchievement(player, Achievements.Tasks.TASK14);
				} else {
					player.getPacketSender().sendMessage("Please free some inventory space before doing that.");
					break;
				}
			} else {
				if(inventorySpace) {
					super.switchItem(player.getInventory(), this, itemToSell.getId(), amountToSell);
					if(shopPrice > 0)
						player.getInventory().add(currency, (int) shopPrice * amountToSell);
					break;
				} else {
					player.getPacketSender().sendMessage("Please free some inventory space before doing that.");
					break;
				}
			}
			amountToSell--;
			amountSold++;
		}
		if(amountSold > 0) {
			Logger.log(player.getUsername(), "Sold item: "+itemToSell.getDefinition().getName()+", noted: "+itemToSell.getDefinition().isNoted()+", amount: "+Misc.insertCommasToNumber(String.valueOf(amountSold))+", for: "+Misc.getTotalAmount((int) (shopPrice * amountSold)));
		}
		if(getId() == GENERAL_STORE)
			fireRestockTask();
		refreshItems();
		publicRefresh();
	}

	/**
	 * Buying an item from a shop
	 */
	@Override
	public Shop switchItem(ItemContainer to, Item item, int slot, boolean sort, boolean refresh) {
		if(!getPlayer().getAttributes().isShopping() || getPlayer().getTrading().inTrade() || getPlayer().getAttributes().isBanking() || getPlayer().getAdvancedSkills().getSummoning().isStoring() || getPlayer().getAttributes().isPriceChecking()) {
			getPlayer().getPacketSender().sendInterfaceRemoval();
			return this;
		}
		if (!shopSellsItem(item))
			return this;
		if(!hasInventorySpace(item)) {
			getPlayer().getPacketSender().sendMessage("You do not have any free inventory slots.");
			return this;
		}
		if (item.getAmount() > getItems()[slot].getAmount())
			item.setAmount(getItems()[slot].getAmount());
		int amountBuying = item.getAmount();
		if(amountBuying == 0)
			return this;
		if(amountBuying > 300) {
			getPlayer().getPacketSender().sendMessage("You can only buy 300 "+ItemDefinition.forId(item.getId()).getName()+"s at a time.");
			return this;
		}
		if(CustomShops.isCustomShop(this.id)) {
			CustomShops.buyItem(getPlayer(), this.id, item, amountBuying);
			return this;
		}
		boolean usePouch = false;
		int playerCurrencyAmount = getPlayer().getInventory().getAmount(currency.getId());
		int value = ItemDefinition.forId(item.getId()).getValue();
		if(currency.getId() == 995) {
			if(getPlayer().getAttributes().getMoneyInPouch() >= value) {
				playerCurrencyAmount = getPlayer().getAttributes().getMoneyInPouch();
				usePouch = true;
			}
		}
		if (playerCurrencyAmount <= 0 || playerCurrencyAmount < value) {
			getPlayer().getPacketSender().sendMessage("You do not have enough " + ItemDefinition.forId(currency.getId()).getName().toLowerCase() + " to purchase this item.");
			return this;
		}
		int amountBought = 0;
		for (int i = amountBuying; i > 0; i--) {
			if (!shopSellsItem(item))
				return this;
			if(!item.getDefinition().isStackable()) {
				if(playerCurrencyAmount >= value && hasInventorySpace(item)) {
					super.switchItem(to, new Item(item.getId(), 1), slot, false, refresh);
					if(usePouch)
						getPlayer().getAttributes().setMoneyInPouch(getPlayer().getAttributes().getMoneyInPouch() - value);
					else
						getPlayer().getInventory().delete(currency.getId(), value, true);
					playerCurrencyAmount -= value;
				} else {
					break;
				}
			} else {
				if(hasInventorySpace(item)) {
					int moneyAmount = getPlayer().getInventory().getAmount(this.getCurrency().getId());
					if(usePouch)
						moneyAmount = getPlayer().getAttributes().getMoneyInPouch();
					if(value == 0)
						break;
					int canBeBought = moneyAmount / (value); //How many noted items can be bought for the player's money
					if(canBeBought >= amountBuying)
						canBeBought = amountBuying;
					if(canBeBought == 0)
						break;
					super.switchItem(to, new Item(item.getId(), canBeBought), slot, false, refresh);
					if(usePouch)
						getPlayer().getAttributes().setMoneyInPouch(getPlayer().getAttributes().getMoneyInPouch() - (value * canBeBought));
					else
						getPlayer().getInventory().delete(currency.getId(), value * canBeBought, true);
					amountBought = canBeBought;
					break;
				} else {
					break;
				}
			}
			amountBuying--;
			amountBought++;
		}
		if(usePouch)
			getPlayer().getPacketSender().sendString(8135, ""+getPlayer().getAttributes().getMoneyInPouch()); //Update the money pouch
		if(amountBought > 0) {
			Logger.log(getPlayer().getUsername(), "Bought item from shopId: "+getId()+", item: "+item.getDefinition().getName()+", noted: "+item.getDefinition().isNoted()+", amount: "+Misc.insertCommasToNumber(String.valueOf(amountBought).replace("Coins",  ""))+", for: "+Misc.getTotalAmount((int) (value * amountBought))+", currency: "+currency.getDefinition().getName().toLowerCase()+", MPouch: "+usePouch);
			if(item.getId() == 2520 || item.getId() == 2522 || item.getId() == 2524 || item.getId() == 2526)
				Achievements.handleAchievement(getPlayer(), Tasks.TASK15);
		}
		fireRestockTask();
		if(id != 21) {
			refreshItems();
			publicRefresh();
		}
		return this;
	}

	/**
	 * Checks if a player has enough inventory space to buy an item
	 * @param item	The item which the player is buying
	 * @return		true or false if the player has enough space to buy the item
	 */
	public boolean hasInventorySpace(Item item) {
		boolean inventorySpace = false;
		if(getPlayer().getInventory().getFreeSlots() >= 1)
			inventorySpace = true;
		if(item.getDefinition().isStackable()) {
			if(getPlayer().getInventory().contains(item.getId()))
				inventorySpace = true;
		}
		if(getPlayer().getInventory().getFreeSlots() == 0 && getPlayer().getInventory().getAmount(995) == ItemDefinition.forId(item.getId()).getValue())
			inventorySpace = true;
		return inventorySpace;
	}

	@Override
	public Shop add(Item item, boolean refresh) {
		super.add(item, refresh);
		if(id != 21)
			publicRefresh();
		return this;
	}

	@Override
	public int capacity() {
		return 50;
	}

	@Override
	public StackType stackType() {
		return StackType.STACKS;
	}

	@Override
	public Shop refreshItems() {
		if(id == 21) {
			RecipeForDisaster.openRFDShop(getPlayer());
			return this;
		}
		for (Player player : World.getPlayers()) {
			if (player == null || !player.getAttributes().isShopping() || player.getAttributes().getShop() == null || player.getAttributes().getShop().id != id)
				continue;
			player.getPacketSender().sendItemContainer(player.getInventory(), INVENTORY_INTERFACE_ID);
			player.getPacketSender().sendItemContainer(ShopManager.getShops().get(id), ITEM_CHILD_ID);
			player.getPacketSender().sendString(NAME_INTERFACE_CHILD_ID, name);
			if(player.getAttributes().getInputHandling() == null || !(player.getAttributes().getInputHandling() instanceof EnterAmountToSellToShop || player.getAttributes().getInputHandling() instanceof EnterAmountToBuyFromShop))
				player.getPacketSender().sendInterfaceSet(INTERFACE_ID, INVENTORY_INTERFACE_ID - 1);
		}
		return this;
	}

	@Override
	public Shop full() {
		getPlayer().getPacketSender().sendMessage("The shop is currently full. Please come back later.");
		return this;
	}

	public String shopPriceEx(int shopPrice) {
		String ShopAdd = "";
		if (shopPrice >= 1000 && shopPrice < 1000000) {
			ShopAdd = " (" + (shopPrice / 1000) + "K)";
		} else if (shopPrice >= 1000000) {
			ShopAdd = " (" + (shopPrice / 1000000) + " million)";
		}
		return ShopAdd;
	}

	private boolean shopSellsItem(Item item) {
		return contains(item.getId());
	}

	public void fireRestockTask() {
		if(isRestockingItems() || fullyRestocked())
			return;
		setRestockingItems(true);
		TaskManager.submit(new ShopRestockTask(this));
	}

	public boolean fullyRestocked() {
		if(id == GENERAL_STORE) {
			return getValidItems().size() == 0;
		} else if(getOriginalStock() != null) {
			for(int shopItemIndex = 0; shopItemIndex < getOriginalStock().length; shopItemIndex++) {
				Item currentShopItem = getItems()[shopItemIndex];
				if(getOriginalStock()[shopItemIndex].getAmount() > currentShopItem.getAmount())
					return false;
			}
		}
		return true;
	}

	/**
	 * The shop interface id.
	 */
	public static final int INTERFACE_ID = 3824;

	/**
	 * The starting interface child id of items.
	 */
	public static final int ITEM_CHILD_ID = 3900;

	/**
	 * The interface child id of the shop's name.
	 */
	public static final int NAME_INTERFACE_CHILD_ID = 3901;

	/**
	 * The inventory interface id, used to set the items right click values
	 * to 'sell'.
	 */
	public static final int INVENTORY_INTERFACE_ID = 3823;

	/**
	 * The general shop id
	 */
	public static final int GENERAL_STORE = 0;
}
