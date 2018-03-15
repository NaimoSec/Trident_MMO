package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.definitions.NPCDrops;
import org.trident.model.movement.MovementStatus;
import org.trident.world.World;
import org.trident.world.content.Achievements;
import org.trident.world.content.Locations;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.skills.impl.slayer.DuoSlayer;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.npc.custom.impl.NexMinion;
import org.trident.world.entity.player.Player;

/**
 * Represents an npc's death task, which handles everything
 * an npc does before and after their death animation (including it), 
 * such as dropping their drop table items.
 * 
 * @author relex lawl
 */

public class NPCDeathTask extends Task {

	/**
	 * The NPCDeathTask constructor.
	 * @param npc	The npc being killed.
	 */
	public NPCDeathTask(NPC npc) {
		super(2);
		this.npc = npc;
		this.killer = CombatHandler.getKiller(npc);
		this.ticks = 2;
	}

	/**
	 * The npc setting off the death task.
	 */
	private final NPC npc;

	/**
	 * The amount of ticks on the task.
	 */
	private int ticks = 2;

	/**
	 * The player who killed the NPC
	 */
	private Player killer = null;

	@Override
	public void execute() {
		try {
			npc.setEntityInteraction(null);
			switch (ticks) {
			case 2:
				npc.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE).stopMovement();
				if(npc.getId() != 8028 && npc.getId() != 8022)
					npc.performAnimation(npc.getDefinition() != null && npc.getDefinition().getDeathAnimation() != null && npc.getDefinition().getDeathAnimation().getId() > 0 ? npc.getDefinition().getDeathAnimation() : new Animation(836));
				CombatHandler.resetAttack(npc);
				break;
			case 1:
				if(killer != null && Locations.goodDistance(killer.getPosition(), npc.getPosition(), 6))
					SoundEffects.sendSoundEffect(killer, SoundEffects.getNpcDeathSounds(npc.getId()), 10, 0);
				break;
			case 0:
				if(npc.getId() == 3200) //Chaos elemental death gfx
					npc.performGraphic(new Graphic(661));
				else if(npc.getId() == 8028)
					npc.performGraphic(new Graphic(267));
				else if(npc.getId() == 8022)
					npc.performGraphic(new Graphic(84));
				else if(npc.getId() == 13447)// Nex
					CustomNPC.getNex().handleDeath();
				else if(NexMinion.nexMinion(npc.getId()))// Nex Minion
					NexMinion.handleDeath(npc.getId());
				if(killer != null) {
					if(npc.getLocation().handleKilledNPC(killer, npc)) {
						stop();
						return;
					}
					Achievements.killedNpc(killer, npc, npc.getDefinition());
					DuoSlayer.killedNpc(killer, npc);
					NPCDrops.dropItems(killer, npc);
				}
				stop();
				break;
			}
			ticks--;
		} catch(Exception e) {
			e.printStackTrace();
			stop();
		}
	}

	@Override
	public void stop() {
		setEventRunning(false);
		
		if(killer != null && killer.getCombatAttributes().isTargeted() && !Location.inMulti(killer))
			killer.getCombatAttributes().setTargeted(false);
		
		npc.getAttributes().setDying(false).setDead(false);

		//respawn
		if(npc.getAttributes().shouldRespawn() && npc.getDefinition() != null && npc.getDefinition().getRespawnTime() > 0)
			TaskManager.submit(new NPCRespawnTask(npc, npc.getDefinition().getRespawnTime()));
		
		World.deregister(npc);
	}
}
