package org.trident.model.definitions;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.trident.model.Animation;

/**
 * Loads and handles npc definitions
 * @editor Gabbe
 */

public class NPCDefinition {

	private static NPCDefinition[] definitions;

	@SuppressWarnings("resource")
	public static boolean init() {
		String FileName = "./data/config/npc.cfg";
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[10];
		boolean EndOfFile = false;
		BufferedReader NpcConfigFile = null;
		try {
			NpcConfigFile = new BufferedReader(new FileReader("./"+FileName));
		} catch(FileNotFoundException fileex) {
			System.out.println(FileName+": file not found.");
			return false;
		}
		try {
			line = NpcConfigFile.readLine();
		} catch(IOException ioexception) {
			System.out.println(FileName+": error loading file.");
			return false;
		}
		definitions = new NPCDefinition[20000];
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
				if (token.equals("npc")) {
					NPCDefinition definition = new NPCDefinition();
					definition.id = Integer.parseInt(token3[0]);
					definition.name = token3[1];
					definition.level = Integer.parseInt(token3[2]);
					definition.attackAnimation = new Animation(Integer.parseInt(token3[3]));
					definition.defenceAnimation = new Animation(Integer.parseInt(token3[4]));
					definition.deathAnimation = new Animation(Integer.parseInt(token3[5]));
					definition.slayerLevelRequirement = Integer.parseInt(token3[6]);
					definition.respawnTime = Integer.parseInt(token3[7]);
					definition.weakness = token3[8];
					definitions[definition.id] = definition;
				}
			} else {
				if (line.equals("[ENDOFNPCLIST]")) {
					try { NpcConfigFile.close(); } catch(IOException ioexception) { }
					try {
						NPCDefinition.readSizes();
					} catch (FileNotFoundException e) {
						System.out.println("Error loading npc sizes");
					}
					return true;
				}
			}
			try {
				line = NpcConfigFile.readLine();
			} catch(IOException ioexception1) { EndOfFile = true; }
		}
		try { NpcConfigFile.close(); } catch(IOException ioexception) { }
		return false;
	}
	
	public static void readSizes() throws FileNotFoundException {
		Scanner s = new Scanner(new File("./data/config/npc_sizes.cfg"));
		while(s.hasNextLine()) {
			String line = s.nextLine();
			String[] data = line.split("	");
			int id = Integer.parseInt(data[0]);
			int size = Integer.parseInt(data[1]);
			if(definitions[id] != null)
			definitions[id].size = size;
		}
		s.close();
	}

	public static int count(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

	/**
	 * Returns NPCDefinition instance for a specified NPC Id.
	 * @param id	NPC Id to fetch definition for.
	 * @return		definitions[id].
	 */
	public static NPCDefinition forId(int id) {
		return (id < 0 || id > definitions.length || definitions[id] == null) ? new NPCDefinition() : definitions[id];
	}

	private int id;
	private int size;
	private String name = "Null";
	private String description = "Null";
	private int level;
	private boolean attackable;
	private boolean aggressive;
	private Animation attackAnimation;
	private Animation defenceAnimation;
	private Animation deathAnimation;
	private String weakness;
	private int slayerLevelRequirement;
	private int respawnTime;
	
	public int getId() {
		return id;
	}
	
	public int getSize() {
		return size;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getLevel() {
		return level;
	}
	
	public boolean isAttackable() {
		return attackable;
	}
	
	public boolean isAggressive() {
		return aggressive;
	}
	
	public Animation getAttackAnimation() {
		return attackAnimation;
	}
	
	public Animation getDefenceAnimation() {
		return defenceAnimation;
	}
	
	public Animation getDeathAnimation() {
		return deathAnimation;
	}
	
	public String getWeakness() {
		return weakness;
	}
	
	public int getSlayerLevelRequirement() {
		return slayerLevelRequirement;
	}
	
	public int getRespawnTime() {
		return respawnTime;
	}
}
