package org.trident.model.inputhandling.impl;

import org.trident.model.inputhandling.EnterAmount;
import org.trident.world.content.skills.impl.summoning.PouchMaking;
import org.trident.world.entity.player.Player;

public class EnterAmountToInfuse extends EnterAmount {

	@Override
	public void handleAmount(Player player, int amount) {
		PouchMaking.infusePouches(player, amount);
	}

}
