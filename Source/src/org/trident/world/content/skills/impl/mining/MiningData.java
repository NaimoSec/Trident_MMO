package org.trident.world.content.skills.impl.mining;

import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.world.entity.player.Player;

public class MiningData {

	public static final int[] RANDOM_GEMS = {1623,1621,1619,1617,1631};

	public static enum Pickaxe {

		Bronze(1265, 1, 625, 1.0),
		Iron(1267, 1, 626, 1.05),
		Steel(1269, 6, 627, 1.1),
		Mithril(1273, 21, 628, 1.2),
		Adamant(1271, 31, 629, 1.25),
		Rune(1275, 41, 624, 1.3),
		Dragon(15259, 61, 12188, 1.50),
		Adze(13661, 80, 10226, 1.60);

		private int id, req, anim;
		private double speed;
		
		private Pickaxe(int id, int req, int anim, double speed) {
			this.id = id;
			this.req = req;
			this.anim = anim;
			this.speed = speed;
		}

		public int getId() {
			return id;
		}
		public int getReq() {
			return req;
		}

		public int getAnim() {
			return anim;
		}
		
		public double getSpeed() {
			return this.speed;
		}
	}

	public static Pickaxe forPick(int id) {
		for (Pickaxe p : Pickaxe.values()) {
			if (p.getId() == id) {
				return p;
			}
		}
		return null;
	}

	public static enum Ores {
		Rune_essence(new int[]{24444}, 1, 10, 1436, 2, -1),
		Pure_essence(new int[]{24445}, 17, 25, 7936, 3, -1),
		
		Clay(new int[]{9711, 9712, 9713, 15503, 15504, 15505}, 1, 100, 434, 3, 2),
		Copper(new int[]{9708, 9709, 9710, 11936, 11960, 11961, 11962, 11189, 11190, 11191, 29231, 29230, 2090}, 1, 175, 436, 4, 4),
		Tin(new int[]{9714, 9715, 9716, 11933, 11957, 11958, 11959, 11186, 11187, 11188, 2094, 29227, 29229}, 1, 175, 438, 4, 4),
		Iron(new int[]{9717, 9718, 9719, 2093, 2092, 11954, 11955, 11956, 29221, 29222, 29223}, 15, 350, 440, 5, 5),
		Silver(new int[]{2100, 2101, 29226, 29225, 11948, 11949}, 20, 450, 442, 5, 7),
		Coal(new int[]{2097, 5770, 29216, 29215, 29217, 11965, 11964, 11963, 11930, 11931, 11932}, 30, 650, 453, 5, 7),
		Gold(new int[]{9720, 9721, 9722, 11951, 11183, 11184, 11185, 2099}, 40, 800, 444, 5, 10),
		Mithril(new int[]{25370, 25368, 5786, 5784, 11942, 11943, 11944, 11945, 11946, 29236, 11947, 11942, 11943}, 50, 1411, 447, 6, 11),
		Adamantite(new int[]{11941, 11939, 29233, 29235}, 70, 1859, 449, 7, 13),
		Runite(new int[]{14859, 4860, 2106, 2107}, 85, 2857, 451, 8, 9);


		private int objid[];
		private int itemid, req, xp, ticks, respawnTimer;

		private Ores(int[] objid, int req, int xp, int itemid, int ticks, int respawnTimer) {
			this.objid = objid;
			this.req = req;
			this.xp = xp;
			this.itemid = itemid;
			this.ticks = ticks;
			this.respawnTimer = respawnTimer;
		}

		public int getRespawn() {
			return respawnTimer;
		}

		public int getLevelReq(){
			return req;
		}

		public int getXpAmount(){
			return xp;
		}

		public int getItemId(){
			return itemid;
		}

		public int getTicks() {
			return ticks;
		}
	}

	public static Ores forRock(int id) {
		for (Ores ore : Ores.values()) {
			for (int obj : ore.objid) {
				if (obj == id) {
					return ore;
				}
			}
		}
		return null;
	}

	public static int getPickaxe(final Player plr) {
		for (Pickaxe p : Pickaxe.values()) {
			if (plr.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == p.getId())
				return p.getId();
			else if (plr.getInventory().contains(p.getId()))
				return p.getId();
		}
		return -1;
	}
	
	public static int getReducedTimer(final Player plr, Pickaxe pickaxe) {
		int skillReducement = (int) (plr.getSkillManager().getMaxLevel(Skill.MINING) * 0.03);
		int pickaxeReducement = (int) pickaxe.getSpeed();
		return skillReducement + pickaxeReducement;
	}
}