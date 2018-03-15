package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.model.definitions.NPCSpawns;
import org.trident.world.World;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;

public class NPCRespawnTask extends Task {
	
	public NPCRespawnTask(NPC npc, int respawn) {
		super(respawn, npc, false);
		this.npc = npc;
	}

	final NPC npc;
	
	@Override
	public void execute() {
		stop();
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
		
		if(npc.getId() == 13447) // NEX
			CustomNPC.getNex().reset();
		else
			World.register(NPCSpawns.createNPC(npc.getId(), npc.getDefaultPosition(), npc.getDefaultAttributes(), npc.getDefaultAttributes().copy()));
	}

}
