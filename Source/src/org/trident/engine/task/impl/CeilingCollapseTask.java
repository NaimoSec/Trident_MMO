package org.trident.engine.task.impl;


import org.trident.engine.task.Task;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.Misc;
import org.trident.world.content.Locations.Location;
import org.trident.world.entity.player.Player;

/**
 * Barrows
 * @author Gabbe
 */
public class CeilingCollapseTask extends Task {

	public CeilingCollapseTask(Player player) {
		super(9, player, false);
		this.player = player;
	}

	private Player player;

	@Override
	public void execute() {
		if(player.getLocation() != Location.BARROWS) {
			stop();
			return;
		}
		player.performGraphic(new Graphic(60));
		player.getPacketSender().sendMessage("Some rocks fall from the ceiling and hit you.");
		player.forceChat("Ouch!");
		player.setDamage(new Damage(new Hit(30 + Misc.getRandom(20), CombatIcon.BLOCK, Hitmask.RED)));
	}

	@Override
	public void stop() {
		setEventRunning(false);
		player.getPacketSender().sendCameraNeutrality();
	}

}
