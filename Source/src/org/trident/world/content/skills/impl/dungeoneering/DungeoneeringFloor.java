package org.trident.world.content.skills.impl.dungeoneering;

import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.model.GroundItem;
import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

/**
 * @author Gabbe
 */
public class DungeoneeringFloor {

	public DungeoneeringFloor(DungeoneeringParty party, DungeoneeringFloors floor) {
		this.party = party;
		this.floor = floor;
	}

	private DungeoneeringParty party;
	private DungeoneeringFloors floor;
	private CopyOnWriteArrayList<NPC> npc_members = new CopyOnWriteArrayList<NPC>();
	private CopyOnWriteArrayList<GroundItem> ground_items = new CopyOnWriteArrayList<GroundItem>();
	
	public DungeoneeringFloors getFloor() {
		return floor;
	}

	public void setFloor(DungeoneeringFloors floor) {
		this.floor = floor;
	}

	public DungeoneeringParty getParty() {
		return party;
	}

	public void setParty(DungeoneeringParty party) {
		this.party = party;
	}

	public CopyOnWriteArrayList<NPC> getNpcs() {
		return npc_members;
	}
	
	public CopyOnWriteArrayList<GroundItem> getGroundItems() {
		return ground_items;
	}
	
	private Position gatestonePosition;

	public Position getGatestonePosition() {
		return gatestonePosition;
	}

	public void setGatestonePosition(Position gatestonePosition) {
		this.gatestonePosition = gatestonePosition;
	}

	private int difficulty;

	public int getDifficulty() {
		return difficulty;
	}

	public DungeoneeringFloor setDifficulty() {
		for(Player member : getParty().getPlayers()) {
			if(member != null)
				difficulty += member.getSkillManager().getCombatLevel() / 120;
		}
		if(difficulty > 4)
			difficulty = 4;
		return this;
	}
	
	/*
	 * Creates a grounditem
	 */
	public void createGroundItem(Item item, Position pos) {
		GroundItem groundItem = new GroundItem(item, pos, "Dungeoneering", true, -1, false, -1);
		GroundItemManager.add(groundItem, true);
		getGroundItems().add(groundItem);
		if(item.getId() == 17489)
			setGatestonePosition(groundItem.getPosition().copy());
	}

}