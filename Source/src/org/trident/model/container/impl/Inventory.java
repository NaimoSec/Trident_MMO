package org.trident.model.container.impl;

import org.trident.model.Item;
import org.trident.model.container.ItemContainer;
import org.trident.model.container.StackType;
import org.trident.model.container.impl.Bank.BankSearchAttributes;
import org.trident.model.definitions.ItemDefinition;
import org.trident.world.entity.player.Player;

/**
 * Represents a player's inventory item container.
 * 
 * @author relex lawl
 */

public class Inventory extends ItemContainer {

	/**
	 * The Inventory constructor.
	 * @param player	The player who's inventory is being represented.
	 */
	public Inventory(Player player) {
		super(player);
	}

	@Override
	public Inventory switchItem(ItemContainer to, Item item, int slot, boolean sort, boolean refresh) {
		if (getItems()[slot].getId() != item.getId())
			return this;
		if (to.getFreeSlots() <= 0 && !(to.contains(item.getId()) && item.getDefinition().isStackable())) {
			to.full();
			return this;
		}
		delete(item, slot, refresh, to);
		if (to instanceof Bank && ItemDefinition.forId(item.getId()).isNoted() && !ItemDefinition.forId(item.getId() - 1).isNoted())
			item.setId(item.getId() - 1);
		to.add(item);
		if (sort && getAmount(item.getId()) <= 0)
			sortItems();
		if (refresh) {
			refreshItems();
			to.refreshItems();
		}
		if(to instanceof Bank && getPlayer().getAttributes().getBankSearchingAttribtues().isSearchingBank() && getPlayer().getAttributes().getBankSearchingAttribtues().getSearchedBank() != null) {
			BankSearchAttributes.addItemToBankSearch(getPlayer(), item);
		}
		return this;
	}

	@Override
	public int capacity() {
		return 28;
	}

	@Override
	public StackType stackType() {
		return StackType.DEFAULT;
	}

	@Override
	public Inventory refreshItems() {
		getPlayer().getPacketSender().sendItemContainer(this, INTERFACE_ID);
		return this;
	}

	@Override
	public Inventory full() {
		getPlayer().getPacketSender().sendMessage("Not enough space in your inventory.");
		return this;
	}

	/**
	 * Adds a set of items into the inventory.
	 *
	 * @param item
	 * the set of items to add.
	 */
	public void addItemSet(Item[] item) {
		for (Item addItem : item) {
			if (addItem == null) {
				continue;
			}
			add(addItem);
		}
	}

	/**
	 * Deletes a set of items from the inventory.
	 *
	 * @param item
	 * the set of items to delete.
	 */
	public void deleteItemSet(Item[] item) {
		for (Item deleteItem : item) {
			if (deleteItem == null) {
				continue;
			}

			delete(deleteItem);
		}
	}

	public static final int INTERFACE_ID = 3214;
}
