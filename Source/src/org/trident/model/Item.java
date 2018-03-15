package org.trident.model;

import org.trident.model.definitions.ItemDefinition;

/**
 * Represents an item which is owned by a player.
 * 
 * @author relex lawl
 */

public class Item {

	/**
	 * An Item object constructor.
	 * @param id		Item id.
	 * @param amount	Item amount.
	 */
	public Item(int id, int amount) {
		this.id = id;
		this.amount = amount;
	}

	/**
	 * An Item object constructor.
	 * @param id		Item id.
	 */
	public Item(int id) {
		this(id, 1);
	}

	/**
	 * The item id.
	 */
	private int id;

	/**
	 * Gets the item's id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the item's id.
	 * @param id	New item id.
	 */
	public Item setId(int id) {
		this.id = id;
		return this;
	}

	/**
	 * Amount of the item.
	 */
	private int amount;

	/**
	 * Price of the item.
	 */
	public int price;

	/**
	 * Gets the amount of the item.
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Sets the amount of the item.
	 */
	public Item setAmount(int amount) {
		this.amount = amount;
		return this;
	}

	/**
	 * Gets item's definition.
	 */
	public ItemDefinition getDefinition() {
		return ItemDefinition.forId(id);
	}

	public boolean tradeable() {
		for(int i = 0; i < org.trident.util.Constants.untradeableItems.length; i++) {
			if(org.trident.util.Constants.untradeableItems[i] == this.getId() || getDefinition().isNoted() && org.trident.util.Constants.untradeableItems[i] == this.getId()-1) {
				return false;
			}
		}
		String name = getDefinition().getName().toLowerCase();
		if(name.contains("clue scroll"))
			return false;
		if(name.contains("overload") || name.contains("extreme"))
			return false;
		if(name.toLowerCase().contains("(deg)") || name.toLowerCase().contains("brawling"))
			return false;
		return true;
	}

	public boolean sellable() {
		for(int i = 0; i < org.trident.util.Constants.unsellable.length; i++) {
			if(org.trident.util.Constants.unsellable[i] == this.getId() || getDefinition().isNoted() && org.trident.util.Constants.unsellable[i] == this.getId()-1) {
				return false;
			}
		}
		return tradeable();
	}

	/**
	 * Copying the item by making a new item with same values.
	 */
	public Item copy() {
		return new Item(id, amount);
	}

	/**
	 * Rarity of an item, used for hunter imps
	 */
	public enum itemRarity {
		COMMON(0), UNCOMMON(1), RARE(2), VERY_RARE(3);

		itemRarity(int rarity) {
			this.rarity = rarity;
		}
		public int rarity;
	}

	public int rarity;

	public Item setRarity(itemRarity rarity) {
		this.rarity = rarity.rarity;
		return this;
	}

	public int getRarity() {
		return this.rarity;
	}

	/**
	 * Increment the amount by 1.
	 */
	public void incrementAmount() {
		if ((amount + 1) > Integer.MAX_VALUE) {
			return;
		}
		amount++;
	}

	/**
	 * Decrement the amount by 1.
	 */
	public void decrementAmount() {
		if ((amount - 1) < 0) {
			return;
		}
		amount--;
	}

	/**
	 * Increment the amount by the specified amount.
	 */
	public void incrementAmountBy(int amount) {
		if ((this.amount + amount) > Integer.MAX_VALUE) {
			this.amount = Integer.MAX_VALUE;
		} else {
			this.amount += amount;
		}
	}

	/**
	 * Decrement the amount by the specified amount.
	 */
	public void decrementAmountBy(int amount) {
		if ((this.amount - amount) < 1) {
			this.amount = 0;
		} else {
			this.amount -= amount;
		}
	}



}