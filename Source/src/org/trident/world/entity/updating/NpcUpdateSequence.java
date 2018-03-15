package org.trident.world.entity.updating;

import org.trident.model.movement.MovementQueue;
import org.trident.world.World;
import org.trident.world.WorldUpdateSequence;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.entity.npc.NPC;

/**
 * A {@link WorldUpdateSequence} implementation for {@link Npc}s that provides
 * code for each of the updating stages. The actual updating stage is not
 * supported by this implementation because npc's are updated for players.
 *
 * @author lare96
 */
public class NpcUpdateSequence implements WorldUpdateSequence<NPC> {
	
	@Override
	public void executePreUpdate(NPC t) {
		try {
			if(t.getAttributes().getWalkingDistance() > 0 && t.getConstitution() > 0 && !CombatHandler.inCombat(t))
				MovementQueue.walkAround(t);
			t.getMovementQueue().pulse();
			t.process();
		} catch (Exception e) {
			e.printStackTrace();
			World.deregister(t);
		}
	}
	@Override
	public void executeUpdate(NPC t) {
		throw new UnsupportedOperationException(
				"NPCs cannot be updated for NPCs!");
	}
	
	@Override
	public void executePostUpdate(NPC t) {
		try {
			NPCUpdating.resetFlags(t);
		} catch (Exception e) {
			e.printStackTrace();
			World.deregister(t);
		}
	}
}