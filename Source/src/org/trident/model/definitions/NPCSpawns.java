package org.trident.model.definitions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.trident.model.Position;
import org.trident.world.World;
import org.trident.world.content.skills.impl.hunter.Hunter;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCAttributes;
import org.trident.world.entity.npc.NPCData.CustomNPCData;

/**
 * Took the spawn-config crap from PI /Gabbe
 * @author Sanity
 *
 */
public class NPCSpawns {

	/**
	 * Loads the NPC-list and spawns the npcs found on the list on to the world.
	 */
	@SuppressWarnings("resource")
	public static boolean init() {
		String FileName = "./data/config/spawn-config.cfg";
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[10];
		boolean EndOfFile = false;
		BufferedReader spawnConfig = null;
		try {
			spawnConfig = new BufferedReader(new FileReader("./"+FileName));
		} catch(FileNotFoundException fileex) {
			System.out.println(FileName+": file not found.");
			return false;
		}
		try {
			line = spawnConfig.readLine();
		} catch(IOException ioexception) {
			System.out.println(FileName+": error loading file.");
			return false;
		}
		while(EndOfFile == false && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token3 = token2_2.split("\t");
				if (token.equals("spawn")) {
					int npcId = Integer.parseInt(token3[0]);
					Position spawnLocation = new Position(Integer.parseInt(token3[1]), Integer.parseInt(token3[2]), Integer.parseInt(token3[3]));
					int walkingDistance = Integer.parseInt(token3[4]);
					boolean attackable = Boolean.parseBoolean(token3[5]);
					boolean aggressive = Boolean.parseBoolean(token3[6]);
					int constitution = Integer.parseInt(token3[7]);
					int attack = Integer.parseInt(token3[8]);
					int strength = Integer.parseInt(token3[9]);
					int defence = Integer.parseInt(token3[10]);
					int absorbMelee = Integer.parseInt(token3[11]);
					int absorbRanged = Integer.parseInt(token3[12]);
					int absorbMagic = Integer.parseInt(token3[13]);
					int attackSpeed = Integer.parseInt(token3[14]);
					int maxHit = Integer.parseInt(token3[15]);
					if(constitution < 1)
						constitution = 1;
					if(attackSpeed < 3)
						attackSpeed = 3;
					NPCAttributes properties = new NPCAttributes();
					properties.setConstitution(constitution).setAttackLevel(attack).setDefenceLevel(defence).setStrengthLevel(strength).setMaxHit(maxHit).setAbsorbMelee(absorbMelee).setAbsorbRanged(absorbRanged).setAbsorbMagic(absorbMagic).setAggressive(aggressive).setAttackable(attackable).setWalkingDistance(walkingDistance).setRespawn(true);
					//Making a copy for default properties
					NPCAttributes defaultProperties = new NPCAttributes();
					defaultProperties.setConstitution(constitution).setAttackLevel(attack).setDefenceLevel(defence).setStrengthLevel(strength).setMaxHit(maxHit).setAbsorbMelee(absorbMelee).setAbsorbRanged(absorbRanged).setAbsorbMagic(absorbMagic).setAggressive(aggressive).setAttackable(attackable).setWalkingDistance(walkingDistance).setRespawn(true);
					NPC n = createNPC(npcId, spawnLocation, properties, defaultProperties);
					World.register(n);
					if(npcId > 5070 && npcId < 5081)
						Hunter.hunterNpcs.add(n);
				}
			} else {
				if (line.equals("[ENDOFSPAWNLIST]")) {
					try { spawnConfig.close(); } catch(IOException ioexception) { }
					return true;
				}
			}
			try {
				line = spawnConfig.readLine();
			} catch(IOException ioexception1) { EndOfFile = true; }
		}
		try { spawnConfig.close(); } catch(IOException ioexception) { }
		return false;
	}

	public static NPC createNPC(int id, Position pos, NPCAttributes properties, NPCAttributes defaultProperties) {
		return new NPC(id, pos).setAttributes(properties).setDefaultAttributes(defaultProperties);
	}
	
	public static NPC createCustomNPC(CustomNPCData data, Position pos) {
		return new NPC(data.npcId, pos).setAttributes(new NPCAttributes().setAttackable(true).setAggressive(data == CustomNPCData.SOULWARS_BARRICADE ? false : true).setConstitution(data.constitution).setAttackLevel(data.attack).setStrengthLevel(data.strength).setDefenceLevel(data.defence).setMaxHit(data.maxHit).setAttackSpeed(data.attackSpeed)).setDefaultAttributes(new NPCAttributes().setConstitution(data.constitution));
	}
}
