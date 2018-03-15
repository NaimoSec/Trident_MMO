package org.trident.model.inputhandling.impl;

import org.trident.model.Item;
import org.trident.model.container.impl.Shop;
import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.entity.player.Player;

public class EnterAmountToBuyFromShop extends EnterAmount {

	public EnterAmountToBuyFromShop(int item, int slot) {
		super(item, slot);
	}
	
	@Override
	public void handleAmount(Player player, int amount) {
		if(player.getAttributes().isShopping() && getItem() > 0 && getSlot() >= 0) {
			Shop shop = player.getAttributes().getShop();
			if(shop != null) {
				if(getSlot() >= shop.getItems().length || shop.getItems()[getSlot()].getId() != getItem())
					return;
				player.getAttributes().getShop().setPlayer(player).forSlot(getSlot()).copy().setAmount(amount).copy();
				shop.switchItem(player.getInventory(), new Item(getItem(), amount), getSlot(), false, true);
			} else
				player.getPacketSender().sendInterfaceRemoval();
		} else
			player.getPacketSender().sendInterfaceRemoval();
		
	}

}
