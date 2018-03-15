package org.trident.world.content.skills.impl.thieving;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Item;
import org.trident.model.Skill;
import org.trident.model.definitions.ItemDefinition;
import org.trident.model.definitions.NPCDefinition;
import org.trident.util.Misc;
import org.trident.world.content.Achievements;
import org.trident.world.content.Achievements.Tasks;
import org.trident.world.content.Locations.Location;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

public class Pickpocketing {

	//NPC ID, LEVEL REQ, XP,  REWARD ID , AMOUNT, MAXHIT, FAILING VALUE
	public static int data[][] = {
		{1, 1, 140, 995, 50, 12, 1}, 		//Man
		{2, 1, 140, 995, 50, 12, 1}, 		//Man
		{3, 1, 140, 995, 50, 12, 1}, 		//Man
		{24, 1, 140, 995, 50, 12, 1},		//Man
		{5920, 1, 140, 995, 50, 20, 1}, 	//Varrock guard
		{4, 1, 140, 995, 50, 12, 1}, 		//Woman
		{5, 1, 140, 995, 50, 12, 1}, 		//Woman
		{6, 1, 140, 995, 50, 12, 1}, 		//Woman
		{25, 1, 140, 995, 50, 12, 1}, 		//Woman
		{5923, 1, 140, 995, 50, 14, 1}, 	//Woman
		{7, 10, 300, 995, 150, 19, 1}, 		//Farmer
		{1714, 15, 360, -1, 0, 19, 1},		//HAM Man
		{1715, 15, 360, -1, 0, 19, 1}, 		//HAM Woman
		{4311, 15, 360, -1, 0, 19, 1}, 		//HAM Guard
		{15, 25, 680, 995, 200, 19, 1}, 		//Warrior Woman
		{2234, 38, 1080, -1, 0, 25, 2}, 	//Master Farmer
		{9, 40, 1300, 995, 320, 25, 2}, 	//Guard
		{296, 40, 1300, 995, 300, 25, 2}, 	//Guard fremmenik
		{5919, 40, 1300, 995, 350, 25, 2},	//Varrock Guard
		{1305, 45, 1600, 995, 400, 25, 2},	//Fremennik
		{3299, 45, 1350, -1, 0, 27, 3}, 	//Master Farmer ,martin
		
		{1306, 45, 1600, 995, 400, 25, 2},	//Fremennik
		{1307, 45, 1600, 995, 400, 25, 2},	//Fremennik
		{1308, 45, 1600, 995, 400, 25, 2},	//Fremennik
		{1309, 45, 1600, 995, 400, 25, 2},	//Fremennik
		{1310, 45, 1600, 995, 400, 25, 2},	//Fremennik
		{1311, 45, 1600, 995, 400, 25, 2},	//Fremennik
		{1312, 45, 1600, 995, 400, 25, 2},	//Fremennik
		{1313, 45, 1600, 995, 400, 25, 2},	//Fremennik
		{1314, 45, 1600, 995, 400, 25, 2},	//Fremennik
		{23, 55, 2430, 995, 500, 30, 2},	//Knight Of Ardounge
		{34, 65, 3620, 995, 580, 30, 2},	//Watchman
		{20, 70, 4300, 995, 750, 30, 2},	//Paladin
		{168, 75, 6800, 995, 820, 30, 2},	//Gnome Woman
		{169, 75, 6800, 995, 820, 30, 2},	//Gnome Woman
		{66, 75, 6800, 995, 860, 30, 2},	//Gnome
		{67, 75, 6800, 995, 860, 30, 2},	//Gnome
		{160, 75, 6800, 995, 460, 40, 2},	//Gnome Child
		{159, 75, 6800, 995, 460, 40, 2},	//Gnome Child
		{21, 80, 8700, 995, 980, 40, 2},	//Hero
	};

	public static void pickPocket(final Player player, final NPC npc) {
		if(npc == null)
			return;
		if(player.getInventory().getFreeSlots() <= 0) {
			player.getPacketSender().sendMessage("You don't have any free inventory slots.");
			return;
		}
		if (System.currentTimeMillis() - player.getCombatAttributes().getLastDamageReceived() < 4000) {
			player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat before doing this.");
			return;
		}
		final int npcId = npc.getId();
		if(player.getSkillManager().getMaxLevel(Skill.THIEVING) < getLevelReq(npcId)) {
			player.getPacketSender().sendMessage("You need a Thieving level of at least " + getLevelReq(npcId) + " to do that.");
			return;
		}
		if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 3000)
			return;
		player.setPositionToFace(npc.getPosition().copy());
		player.performAnimation(new Animation(881));
		TaskManager.submit(new Task(1, player, false) {
			@Override
			public void execute() {
				final Item loot = new Item(getLoot(npcId), 1);
				if(failed(player, npcId)) {
					npc.setPositionToFace(player.getPosition());
					player.setDamage(new Damage(new Hit(Misc.getRandom(getMaxhit(npc.getId())), CombatIcon.MELEE, Hitmask.RED)));
					npc.performAnimation(npc.getAttackAnimation());
					npc.forceChat("What are you doing in my pocket?!");
					player.performAnimation(new Animation(65535));
					stop();
					return;
				}
				if(!specialCase(player, npc.getId())) {
					player.getInventory().add(loot.getId(), Misc.getRandom(getAmount(npcId)));
					player.getPacketSender().sendMessage("You pick the "+npc.getDefinition().getName().replaceAll("_", " ")+"'s pocket for some "+loot.getDefinition().getName().toLowerCase()+".");
					if(player.getLocation() == Location.LUMBRIDGE && (npc.getId() == 1 || npc.getId() == 2))
						Achievements.handleAchievement(player, Tasks.TASK12);
				}
				player.getSkillManager().addExperience(Skill.THIEVING, (int) (getExp(npcId) * 1.7), false);
				stop();
			}
		});
		player.getAttributes().setClickDelay(System.currentTimeMillis());
	}

	public static boolean failed(Player player, int npcId) {
		int reqLvl = getLevelReq(npcId);
		int plrLvl = (int) (player.getSkillManager().getCurrentLevel(Skill.THIEVING)*1.5);
		return Misc.getRandom(plrLvl) < Misc.getRandom(reqLvl);
	}

	public static int getLevelReq(int npcId) {
		int req = 1;
		for(int i= 0; i < data.length; i++) {
			if(data[i][0] == npcId) {
				req = data[i][1];
			}
		}
		return req;
	}

	public static int getExp(int npcId) {
		int exp = 1;
		for(int i= 0; i < data.length; i++) {
			if(data[i][0] == npcId) {
				exp = data[i][2];
			}
		}
		return exp;
	}

	public static boolean isPickPocketNPC(int npcId) {
		for(int i= 0; i < data.length; i++) {
			if(data[i][0] == npcId) {
				return true;
			}
		}
		return false;
	}

	public static int getLoot(int npcId) {
		int loot = 995; //Money if nothing was found
		for(int i= 0; i < data.length; i++) {
			if(data[i][0] == npcId) {
				loot = data[i][3];
			}
		}
		return loot;	
	}

	public static int getAmount(int npcId) {
		int amount = 1; 
		for(int i= 0; i < data.length; i++) {
			if(data[i][0] == npcId) {
				amount = data[i][4];
			}
		}
		return amount;	
	}

	public static int getMaxhit(int npcId) {
		int maxHit = 1; 
		for(int i= 0; i < data.length; i++) {
			if(data[i][0] == npcId) {
				maxHit = data[i][5];
			}
		}
		return maxHit;	
	}

	public static int getFail(int npcId) {
		for(int i= 0; i < data.length; i++) {
			if(data[i][6] == npcId) {
				return data[i][5];
			}
		}
		return -1;	
	}

	public static boolean specialCase(Player player, int npcId) {
		switch(npcId) {
		case 2234://Master farmer
		case 3299:
			int loot = Stalls.getRandomSeed();
			player.getInventory().add(loot, 1);
			player.getPacketSender().sendMessage("You pick the "+NPCDefinition.forId(npcId).getName().replaceAll("_", " ")+"'s pocket for some "+ItemDefinition.forId(loot).getName().toLowerCase()+".");
			return true;
		case 4311:
		case 1714:
		case 1715:
			loot = HAM_REWARDS[Misc.getRandom(HAM_REWARDS.length - 1)];
			player.getInventory().add(loot, 1);
			player.getPacketSender().sendMessage("You pick the "+NPCDefinition.forId(npcId).getName().replaceAll("_", " ")+"'s pocket for some "+ItemDefinition.forId(loot).getName().toLowerCase()+".");
			return true;
		}
		return false;
	}

	public static final int[] HAM_REWARDS = new int[] {1629, 952, 1203, 1205, 1207, 1265, 1267, 1269, 1627, 1625, 697, 686, 687, 882, 884, 886, 199, 201, 203, 205, 209, 211, 213, 215, 1349, 1351, 1353, 1733, 1734, 4298, 4300, 4302, 4304, 4306, 4308, 4310, 14678, 319, 314, 1131, 1739, 1511, 2138, 688, 39, 800, 806, 825, 1139, 40, 440, 801, 1137, 1239, 1335, 1363, 1843, 2945, 1735, 1733, 4, 1592, 1594};

}
