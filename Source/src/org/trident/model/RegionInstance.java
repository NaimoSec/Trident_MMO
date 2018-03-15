package org.trident.model;

import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.world.World;
import org.trident.world.content.minigames.impl.Barrows;
import org.trident.world.content.minigames.impl.WarriorsGuild;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;


public class RegionInstance {

	public enum RegionInstanceType {
		BARROWS,
		CONQUEST,
		FIGHT_CAVE,
		WARRIORS_GUILD,
		NOMAD,
		MAGE_ARENA,
		RECIPE_FOR_DISASTER;
	}
	
	public RegionInstance(Player p, RegionInstanceType type) {
		this.owner = p;
		this.type = type;
		this.npcsList = new CopyOnWriteArrayList<NPC>();
	}
	
	public void destruct() {
		for(NPC n : npcsList) {
			if(n != null && n.getConstitution() > 0 && World.getNpcs().get(n.getIndex()) != null && n.getCombatAttributes().getSpawnedFor() != null && n.getCombatAttributes().getSpawnedFor().getIndex() == owner.getIndex()) {
				if(n.getId() >= 4278 && n.getId() <= 4284)
					owner.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[WarriorsGuild.ARMOUR_SPAWNED_INDEX] = false;
				if(n.getId() >= 2024 && n.getId() <= 2034) //Dont know ids lol
					Barrows.killBarrowsNpc(owner, n, false);
				World.deregister(n);
			}
		}
		npcsList.clear();
		owner.getAttributes().setRegionInstance(null);
	}
	
	@Override
	public boolean equals(Object other) {
		return (RegionInstanceType)other == type;
	}
	
	public CopyOnWriteArrayList<NPC> getNpcsList() {
		return npcsList;
	}

	private Player owner;
	private RegionInstanceType type;
	private CopyOnWriteArrayList<NPC> npcsList;
}
