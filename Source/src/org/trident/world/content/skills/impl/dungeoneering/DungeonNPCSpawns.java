package org.trident.world.content.skills.impl.dungeoneering;

import java.util.ArrayList;

import org.trident.model.Position;
import org.trident.world.World;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCAttributes;

public class DungeonNPCSpawns {

	/*
	 * Ugliest method in the whole universe
	 */
	public static void spawnNpcs(DungeoneeringFloor floor, final int height) { //I just couldn't bother to put this in the enum.. 
		/*
		 * Smuggler
		 */
		NPC smuggler = new NPC(11226, new Position(floor.getFloor().getSmugglerPosition().getX(), floor.getFloor().getSmugglerPosition().getY(), height));
		World.register(smuggler);
		floor.getNpcs().add(smuggler);
		/*
		 * Others
		 */
		ArrayList<NPC> spawns = new ArrayList<NPC>();
		int difficulty = floor.setDifficulty().getDifficulty();
		if(floor.getFloor() == DungeoneeringFloors.FIRST_FLOOR) {
			spawns.add(new NPC(912, new Position(2443, 4955, height)));
			spawns.add(new NPC(124, new Position(2441, 4953, height)));
			spawns.add(new NPC(difficulty <= 1 ? 6716 : difficulty <= 3 ? 6689 : 6727, new Position(2460, 4964, height)));
			if(floor.getParty().getPlayers().size() >= 2) {
				spawns.add(new NPC(27, new Position(2462, 4961, height)));
				spawns.add(new NPC((difficulty == 0 || difficulty == 1) ? 5393 : 5394, new Position(2440, 4958, height)));
			} else if(floor.getDifficulty() >= 3) {
				spawns.add(new NPC(1615, new Position(2445, 4958, height)));
				spawns.add(new NPC(912, new Position(2460, 4967, height)));
				if(floor.getDifficulty() >= 4) {
					spawns.add(new NPC(1615, new Position(2444, 4956, height)));
					spawns.add(new NPC(1220, new Position(2460, 4966, height)));
					spawns.add(new NPC(27, new Position(2475, 4953, height)));
				}
			}
			spawns.add(new NPC((difficulty == 0 || difficulty == 1) ? 83 : 84, new Position(2476, 4956, height)));
			spawns.add(new NPC(8598, new Position(2472, 4958, height)));
			spawns.add(new NPC(9939, new Position(2472, 4940, height)));
		}
		for(NPC npc : spawns) {
			npc.setAttributes(getProperties(npc.getId(), difficulty)).setDefaultAttributes(getProperties(npc.getId(), difficulty));
			World.register(npc);
			floor.getNpcs().add(npc);
		}
		spawns = null;
	}
	
	public static NPCAttributes getProperties(int id, int difficulty) {
		NPCAttributes properties = new NPCAttributes();
		switch(id) {
		case 912:
			properties.setConstitution(300 + (100*difficulty));
			break;
		case 8598:
		case 5393:
			properties.setAttackLevel(20 + (10 * difficulty)).setStrengthLevel(20 + (10 * difficulty)).setDefenceLevel(21 + (5 * difficulty)).setConstitution(300 + (100*difficulty));
			break;
		case 5394:
			properties.setConstitution(340 + (100*difficulty)).setAttackLevel(25 + (10 * difficulty)).setStrengthLevel(25 + (10 * difficulty)).setDefenceLevel(23 + (5 * difficulty));
			break;
		case 124:
			properties.setConstitution(325 + (100*difficulty)).setAttackLevel(22 + (10 * difficulty)).setStrengthLevel(22 + (10 * difficulty)).setDefenceLevel(25 + (5 * difficulty));
			break;
		case 1220:
			properties.setConstitution(405 + (100*difficulty)).setAttackLevel(32 + (10 * difficulty)).setStrengthLevel(34 + (10 * difficulty)).setDefenceLevel(35 + (5 * difficulty));
			break;
		case 6727:
			properties.setConstitution(1000 + (500 * difficulty));
			break;
		case 6689:
			properties.setConstitution(750 + (500 * difficulty));
			break;
		case 6716:
			properties.setConstitution(500 + (300 * difficulty));
			break;
		case 27:
			properties.setConstitution(222);
			break;
		case 941:
			properties.setConstitution(300 + (300*difficulty));
			break;
		case 83:
			properties.setConstitution(500 + (200 * difficulty)).setAttackLevel(30 + (10 * difficulty)).setStrengthLevel(30 + (10 * difficulty)).setMaxHit(180 + (50 * difficulty)).setDefenceLevel(30 + (10* difficulty));
			break;
		case 84:
			properties.setConstitution(700 + (200 * difficulty)).setAttackLevel(35 + (10 * difficulty)).setStrengthLevel(35 + (10 * difficulty)).setMaxHit(180 + (55 * difficulty)).setDefenceLevel(35 + (10* difficulty));
			break;
		case 9939:
			properties.setConstitution(2200 + (950 * difficulty)).setDefenceLevel(90 + (30 * difficulty));
			break;
		case 1615:
			properties.setConstitution(600 + (200 * difficulty)).setDefenceLevel(30 + (10 * difficulty)).setAttackLevel(40 + (10 * difficulty)).setStrengthLevel(40 + (10 * difficulty));
			break;
		}
		properties.setAggressive(true).setAttackable(true);
		return properties;
	}
}
