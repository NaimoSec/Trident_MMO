package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.content.minigames.impl.ArcheryCompetition;
import org.trident.world.entity.player.Player;

public class EnterAmountOfTicketsToExchange extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		ArcheryCompetition.exhchangeTickets(player, amount);
	}

}
