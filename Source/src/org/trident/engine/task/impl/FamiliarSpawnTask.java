package org.trident.engine.task.impl;


import org.trident.engine.task.Task;
import org.trident.world.content.skills.impl.summoning.Summoning.FamiliarData;
import org.trident.world.entity.player.Player;

/**
 * Familiar spawn on login
 * @author Gabbe
 */
public class FamiliarSpawnTask extends Task {

	public FamiliarSpawnTask(Player player) {
		super(1, player, true);
		this.player = player;
	}

	private Player player;
	public int familiarId;
	public int deathTimer;

	public FamiliarSpawnTask setFamiliarId(int familiarId) {
		this.familiarId = familiarId;
		return this;
	}

	public FamiliarSpawnTask setDeathTimer(int deathTimer) {
		this.deathTimer = deathTimer;
		return this;
	}

	@Override
	public void execute() {
		stop();
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
		player.getAdvancedSkills().getSummoning().summon(FamiliarData.forNPCId(familiarId), false, true);
		if(player.getAdvancedSkills().getSummoning().getFamiliar() != null && deathTimer > 0)
			player.getAdvancedSkills().getSummoning().getFamiliar().setDeathTimer(deathTimer);
	}

}
