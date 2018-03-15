package org.trident.world.content.skills.impl.slayer;

import org.trident.model.Position;

/**
 * @author Gabbe 
 */

public enum SlayerTasks {
		
		NO_TASK(null, -1, null, -1, null),
		
		/**
		 * Easy tasks
		 */
		ROCK_CRAB(SlayerMaster.VANNAKA, 1265, "Rock Crabs can be found East of Fremennik.", 210, new Position(2709, 3715, 0)),
		EXPERIMENT(SlayerMaster.VANNAKA, 1677, "Experiments can be found south-east of Fenkenstrain's Castle.", 215, new Position(3564, 9954, 0)),
		FLESH_CRAWLER(SlayerMaster.VANNAKA, 4390, "Flesh Crawlers can be found in the Stronghold of Security.", 400, new Position(2041, 5193, 0)),
		GIANT_BAT(SlayerMaster.VANNAKA, 78, "Giant Bats can be found in Taverly Dungeon.", 200, new Position(2907, 9833)),
		CHAOS_DRUID(SlayerMaster.VANNAKA, 181, "Chaos Druids can be found in Edgeville Dungeon.", 212, new Position(3109, 9931, 0)),
		ZOMBIE(SlayerMaster.VANNAKA, 76, "Zombies can be found in Edgeville Dungeon.", 200, new Position(3144, 9903, 0)),
		HOBGOBLIN(SlayerMaster.VANNAKA, 2686, "Hobgoblins can be found in Edgeville Dungeon.", 450, new Position(3123, 9876, 0)),
		HILL_GIANT(SlayerMaster.VANNAKA, 117, "Hill Giants can be found in Edgeville Dungeon.", 470, new Position(3120, 9844, 0)),
		DEADLY_RED_SPIDER(SlayerMaster.VANNAKA, 63, "Deadly Red Spiders can be found in Edgeville Dungeon.", 450, new Position(3083, 9940, 0)),
		BABY_BLUE_DRAGON(SlayerMaster.VANNAKA, 52, "Baby Blue Dragons can be found in Taverly Dungeon.", 500, new Position(2891, 9772, 0)),
		SKELETON(SlayerMaster.VANNAKA, 90, "Skeletons can be found in Edgeville Dungeon.", 220, new Position(3094, 9896)),
		EARTH_WARRIOR(SlayerMaster.VANNAKA, 124, "Earth Warriors can be found in Edgeville Dungeon.", 540, new Position(3124, 9986)),
		
		/**
		 * Medium tasks
		 */
		BANDIT(SlayerMaster.DURADEL, 1880, "Bandits can be found in Al-Kharid.", 650, new Position(3172, 2976)),
		WILD_DOG(SlayerMaster.DURADEL, 1593, "Wild Dogs can be found in Brimhaven Dungeon.", 670, new Position(2680, 9557)),
		MOSS_GIANT(SlayerMaster.DURADEL, 112, "Moss Giants can be found in Brimhaven Dungeon.", 660, new Position(2676, 9549)),
		FIRE_GIANT(SlayerMaster.DURADEL, 110, "Fire Giants can be found in Brimhaven Dungeon.", 690, new Position(2664, 9480)),
		GREEN_DRAGON(SlayerMaster.DURADEL, 941, "Green Dragons can be found in western Wilderness.", 750, new Position(2977, 3615)),
		BLUE_DRAGON(SlayerMaster.DURADEL, 55, "Blue Dragons can be found in Taverly Dungeon.", 800, new Position(2892, 9799)),
		HELLHOUND(SlayerMaster.DURADEL, 49, "Hellhounds can be found in Taverly Dungeon.", 800, new Position(2870, 9848)),
		BLACK_DEMON(SlayerMaster.DURADEL, 84, "Black Demons can be found in Edgeville Dungeon.", 827, new Position(3089, 9967)),
		BLOODVELD(SlayerMaster.DURADEL, 1618, "Bloodvelds can be found in Slayer Tower.", 720, new Position(3418, 3570, 1)),
		INFERNAL_MAGE(SlayerMaster.DURADEL, 1643, "Infernal Mages can be found in Slayer Tower.", 700, new Position(3445, 3579, 1)),
		ABERRANT_SPECTRE(SlayerMaster.DURADEL, 1604, "Aberrant Spectres can be found in Slayer Tower.", 730, new Position(3432, 3553, 1)),
		NECHRYAEL(SlayerMaster.DURADEL, 1613, "Nechryaels can be found in Slayer Tower.", 780, new Position(3448, 3564, 2)),
		GARGOYLE(SlayerMaster.DURADEL, 1610, "Gargoyles can be found in Slayer Tower.", 810, new Position(3438, 3534, 2)),
		TZHAAR_XIL(SlayerMaster.DURADEL, 2605, "TzHaar-Xils can be found in Tzhaar City.", 900, new Position(2445, 5147)),
		TZHAAR_HUR(SlayerMaster.DURADEL, 2600, "TzHaar-Hurs can be found in Tzhaar City.", 790, new Position(2456, 5135)),
		ORK(SlayerMaster.DURADEL, 6273, "Orks can be found in the Godwars Dungeon.", 760, new Position(2867, 5322)),
		JUNGLE_STRYKEWYRM(SlayerMaster.DURADEL, 9467, "Strykewyrms can be found in their muddy dungeon.", 780, new Position(2731, 5095)),
		DESERT_STRYKEWYRM(SlayerMaster.DURADEL, 9465, "Strykewyrms can be found in their muddy dungeon.", 835, new Position(2731, 5095)),
		
		/**
		 * Hard tasks
		 */
		WATERFIEND(SlayerMaster.KURADEL, 5361, "Waterfiends can be found in the Ancient Cavern.", 1340, new Position(1737, 5353)),
		KING_BLACK_DRAGON(SlayerMaster.KURADEL, 50, "The King Black Dragon can be found using the Boss tele.", 1650, new Position(2273, 4680, 1)),
		STEEL_DRAGON(SlayerMaster.KURADEL, 1592, "Steel dragons can be found in Brimhaven Dungeon.", 1560, new Position(2710, 9441)),
		MITHRIL_DRAGON(SlayerMaster.KURADEL, 5363, "Mithril Dragons can be found in the Ancient Cavern.", 1600, new Position(1761, 5329, 1)),
		GREEN_BRUTAL_DRAGON(SlayerMaster.KURADEL, 5362, "Green Brutal Dragons can be found in the Ancient Cavern.", 1559, new Position(1767, 5340)),
		SKELETON_WARLORD(SlayerMaster.KURADEL, 6105, "Skeleton Warlords can be found in the Ancient Cavern.", 1440, new Position(1763, 5358)),
		SKELETON_THUG(SlayerMaster.KURADEL, 6107, "Skeleton Thugs can be found in the Ancient Cavern.", 1440, new Position(1788, 5335)),
		TORMENTED_DEMON(SlayerMaster.KURADEL, 8349, "Tormented Demons can be found using the Boss tele.", 1647, new Position(2602, 5713)),
		AVIANSIE(SlayerMaster.KURADEL, 6246, "Aviansies can be found in the Godwars Dungeon.", 1460, new Position(2868, 5268, 2)),
		GENERAL_GRAARDOR(SlayerMaster.KURADEL, 6260, "General Graardor can be found in the Godwars Dungeon.", 2400, new Position(2863, 5354, 2)),
		FROST_DRAGON(SlayerMaster.KURADEL, 51, "Frost Dragons can be found in the deepest Wilderness.", 2257, new Position(2968, 3902)),
		ICE_STRYKEWYRM(SlayerMaster.KURADEL, 9463, "Strykewyrms can be found in their muddy dungeon.", 1870, new Position(2731, 5095));
		
		private SlayerTasks(SlayerMaster taskMaster, int npcId, String npcLocation, int XP, Position taskPosition) {
			this.taskMaster = taskMaster;
			this.npcId = npcId;
			this.npcLocation = npcLocation;
			this.XP = XP;
			this.taskPosition = taskPosition;
		}
		
		private SlayerMaster taskMaster;
		private int npcId;
		private String npcLocation;
		private int XP;
		private Position taskPosition;
		
		public SlayerMaster getTaskMaster() {
			return this.taskMaster;
		}
		
		public int getNpcId() {
			return this.npcId;
		}
		
		public String getNpcLocation() {
			return this.npcLocation;
		}
		
		public int getXP() {
			return this.XP;
		}
		
		public Position getTaskPosition() {
			return this.taskPosition;
		}
		
		public static SlayerTasks forId(int id) {
			for (SlayerTasks tasks : SlayerTasks.values()) {
				if (tasks.ordinal() == id) {
					return tasks;
				}
			}
			return null;
		}
}
