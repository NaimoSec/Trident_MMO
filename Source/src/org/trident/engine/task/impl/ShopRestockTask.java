package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.model.Item;
import org.trident.model.container.impl.Shop;

public class ShopRestockTask extends Task {

	public ShopRestockTask(Shop shop) {
		super(30);
		this.shop = shop;
	}

	private final Shop shop;

	@Override
	protected void execute() {
		if(shop.fullyRestocked()) {
			stop();
			return;
		}
		if(shop.getId() != Shop.GENERAL_STORE) {
			for(int shopItemIndex = 0; shopItemIndex < shop.getOriginalStock().length; shopItemIndex++) {
				Item currentShopItem = shop.getItems()[shopItemIndex];
				int originalStockAmount = shop.getOriginalStock()[shopItemIndex].getAmount();
				int currentStockAmount = currentShopItem.getAmount();
				if(originalStockAmount > currentStockAmount) {
					int amountMissing = originalStockAmount - currentStockAmount;
					int amountToSet = (getRestockAmount(amountMissing) + currentStockAmount);
					shop.setItem(shopItemIndex, currentShopItem.setAmount((amountToSet > originalStockAmount) ? originalStockAmount : amountToSet));
				}
			}
		} else {
			for(int i = 0; i < shop.getItems().length; i++) {
				Item it = shop.getItems()[i];
				if(it != null && it.getId() > 0) {
					if(it.getAmount() <= 1)
						shop.delete(new Item(it.getId()));
					else 
						it.setAmount(it.getAmount() - getDeleteRatio(it.getAmount()));
				}
			}
		}
		shop.publicRefresh();
		shop.refreshItems();
		if(shop.fullyRestocked())
			stop();
	}

	@Override
	public void stop() {
		setEventRunning(false);
		shop.setRestockingItems(false);
	}

	public int getRestockAmount(int amountMissing) {
		return (int)(Math.pow(amountMissing, 1.2)/30+1);
	}

	public int getDeleteRatio(int currentAmount) {
		return (int)(Math.pow(currentAmount, 1.05)/50+1);
	}
}
