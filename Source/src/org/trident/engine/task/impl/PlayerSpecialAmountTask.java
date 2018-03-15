package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.entity.player.Player;

public class PlayerSpecialAmountTask extends Task {

	public PlayerSpecialAmountTask(Player player) {
		super(20, player, false);
		this.player = player;
		player.getPlayerCombatAttributes().setRecoveringSpecialAttack(true);
	}
	
	private final Player player;
	
	@Override
	public void execute() {
		if (player == null || player.getPlayerCombatAttributes().getSpecialAttackAmount() >= 10 ||
				!player.getPlayerCombatAttributes().isRecoveringSpecialAttack()) {
			player.getPlayerCombatAttributes().setRecoveringSpecialAttack(false);
			stop();
			return;
		}
		double amount = player.getPlayerCombatAttributes().getSpecialAttackAmount() + .5;
		if (amount >= 10) {
			amount = 10;
			player.getPlayerCombatAttributes().setRecoveringSpecialAttack(false);
			stop();
		}
		player.getPlayerCombatAttributes().setSpecialAttackAmount(amount);
		WeaponHandler.update(player);
		if(player.getPlayerCombatAttributes().getSpecialAttackAmount() % 50 == 0)
			player.getPacketSender().sendMessage("<col=00FF00>Your special attack energy is now " + player.getPlayerCombatAttributes().getSpecialAttackAmount() + "%.");
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
	}
}