package org.trident.model.inputhandling.impl;

import org.trident.model.definitions.ItemDefinition;
import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.entity.player.Player;

public class EnterAmountOfLoyaltyItemsToBuy extends EnterAmount {

	private int itemId, cost;
	public EnterAmountOfLoyaltyItemsToBuy(int itemId, int cost) {
		this.itemId = itemId;
		this.cost = cost;
	}

	@Override
	public void handleAmount(Player player, int amount) {
		player.getPacketSender().sendInterfaceRemoval();
		int points = player.getPointsHandler().getLoyaltyProgrammePoints();
		if((cost * amount) > points) {
			int canBuy = points / cost;
			player.getPacketSender().sendMessage(canBuy > 0 ? "@red@You have enough Loyalty points to buy only "+canBuy+" of this item." : "You do not have enough Loyalty points to buy this item.");
			return;
		}
		int slotsNeeded = ItemDefinition.forId(itemId).isStackable() ? 1 : amount;
		if(player.getInventory().getFreeSlots() < slotsNeeded) {
			player.getPacketSender().sendMessage("You need at least "+slotsNeeded+" free inventory slots before doing this.");
			return;
		}
		player.getPointsHandler().setLoyaltyProgrammePoints(points - (cost * amount), false);
		player.getPointsHandler().refreshPanel();
		player.getInventory().add(itemId, amount);
	}
}
