package org.trident.world.content.skills.impl.thieving;

import org.trident.model.Animation;
import org.trident.model.GameObject;
import org.trident.model.Item;
import org.trident.model.Skill;
import org.trident.model.definitions.ItemDefinition;
import org.trident.util.Misc;
import org.trident.world.content.Achievements;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.Locations;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

public class Stalls {

	public static void thievFromStall(Player player, final GameObject object, final int oldId, final int newid, final int face, int lvlreq, int xp, int reward, String message, int respawn) {
		if(player.getInventory().getFreeSlots() < 1) {
			player.getPacketSender().sendMessage("You need some more inventory space to do this.");
			return;
		}
		if (System.currentTimeMillis() - player.getCombatAttributes().getLastDamageReceived() < 4000) {
			player.getPacketSender().sendMessage("You must wait a few seconds after being out of combat before doing this.");
			return;
		}
		if(System.currentTimeMillis() - player.getAttributes().getClickDelay() <= 2000)
			return;
		if(player.getSkillManager().getMaxLevel(Skill.THIEVING) < lvlreq) {
			player.getPacketSender().sendMessage("You need a Thieving level of at least " + lvlreq + " to steal from this stall.");
			return;
		}
		player.setPositionToFace(object.getPosition());
		player.setAnimation(new Animation(881));
		player.getPacketSender().sendMessage(message);
		handleGuard(player);
		player.getPacketSender().sendClientRightClickRemoval();
		player.getSkillManager().addExperience(Skill.THIEVING, xp, false);
		GameObject object1 = new GameObject(newid, object.getPosition());
		GameObject object2 = new GameObject(oldId, object.getPosition());
		player.getPacketSender().sendClientRightClickRemoval();
		object1.setFace(face);object2.setFace(face);
		CustomObjects.globalObjectRespawnTask(object1, object2, respawn);
		player.getAttributes().setClickDelay(System.currentTimeMillis());
		player.getInventory().add(new Item(reward, 1));
		if(reward == 1891)
			Achievements.handleAchievement(player, Achievements.Tasks.TASK28);
	}

	public static boolean isThievingStall(Player player, GameObject obj) {
		int objX = obj.getPosition().getX();
		int objY = obj.getPosition().getY();
		/*
		 * Ardougne stalls
		 */
		boolean cakeStall = objX == 2667 && objY == 3310 || objX == 2655 && objY == 3311;
		boolean silkStall = objX == 2662 && objY == 3314 || objX == 2656 && objY == 3302;
		boolean furStall = objX == 2663 && objY == 3296;
		boolean silverStall = objX == 2657 && objY == 3314;
		boolean spiceStall = objX == 2658 && objY == 3297;
		boolean gemStall = objX == 2667 && objY == 3303;
		/*
		 * Draynor village stalls
		 */
		boolean marketStall = objX == 3083 && objY == 3251;
		boolean seedStall = objX == 3079 && objY == 3253 || obj.getId() == 7053;
		/*
		 * Fremennik Stalls
		 */
		boolean fremennikFurStall = objX == 2638 && objY == 3676 || objX == 2647 && objY == 3680;
		boolean fremennikFishStall = objX == 2638 && objY == 3680 || objX == 2638 && objY == 3672 || objX == 2647 && objY == 3676;
		
		if(cakeStall) {
			if(Locations.goodDistance(player.getPosition(), obj.getPosition(), 2)) {
				thievFromStall(player, obj, 34384, 634, getFace(obj), 5, 250, 1891, "You steal some Cake.", 7);
				return true;
			}
		}

		if(silkStall) {
			if(Locations.goodDistance(player.getPosition(), obj.getPosition(), 2)) {
				thievFromStall(player, obj, 34383, 634, getFace(obj), 20, 400, 950, "You steal some Silk.", 13);
				return true;
			}
		}

		if(furStall) {
			if(Locations.goodDistance(player.getPosition(), obj.getPosition(), 2)) {
				thievFromStall(player, obj, 34387, 634, getFace(obj), 35, 825, 6814, "You steal some Fur.", 17);
				return true;
			}
		}

		if(silverStall) {
			if(Locations.goodDistance(player.getPosition(), obj.getPosition(), 2)) {
				thievFromStall(player, obj, 34382, 634, getFace(obj), 50, 1000, 442, "You steal some Silver.", 23);
				return true;
			}
		}

		if(spiceStall) {
			if(Locations.goodDistance(player.getPosition(), obj.getPosition(), 2)) {
				thievFromStall(player, obj, 34386, 634, getFace(obj), 65, 1800, 2007, "You steal some Spice.", 30);
				return true;
			}
		}

		if(gemStall) {
			if(Locations.goodDistance(player.getPosition(), obj.getPosition(), 2)) {
				thievFromStall(player, obj, 34385, 634, getFace(obj), 75, 4500, 1607, "You steal a Sapphire.", 35);
				return true;
			}
		}

		if(marketStall) {
			if(Locations.goodDistance(player.getPosition(), obj.getPosition(), 2)) {
				thievFromStall(player, obj, 14011, 634, getFace(obj), 36, 900, 245, "You steal some Wine of Zamorak.", 16);
				return true;
			}
		}

		if(seedStall) {
			if(Locations.goodDistance(player.getPosition(), obj.getPosition(), 2)) {
				Item loot = new Item(getRandomSeed());
				String lootName = Misc.formatText(ItemDefinition.forId(loot.getId()).getName().toLowerCase());
				String message = "You steal "+Misc.anOrA(lootName)+" "+lootName+".";
				thievFromStall(player, obj, 7053, 634, getFace(obj), 27, 100, loot.getId(), message, 20);
				return true;
			}
		}

		if(fremennikFurStall) {
			if(Locations.goodDistance(player.getPosition(), obj.getPosition(), 2)) {
				thievFromStall(player, obj, 4278, 634, obj.getPosition().getY() == 3680 ? 3 : 1, 35, 130, 958, "You steal a Grey wolf fur.", 20);
			}
		}

		if(fremennikFishStall) {
			if(Locations.goodDistance(player.getPosition(), obj.getPosition(), 2)) {
				int[] randoms = {359, 377, 331};
				int reward = randoms[Misc.getRandom(randoms.length - 1)];
				Item loot = new Item(reward);
				thievFromStall(player, obj, 4277, 634, obj.getPosition().getY() == 3676 ? 3 : 1, 42, 180, loot.getId(), "You steal some fish.", 30);
			}
		}

		return false;
	}

	/**
	 * Searches the NPC array for the guard and assign it to attack the player.
	 * @param player
	 */
	public static void handleGuard(Player player) {
		if(Misc.getRandom(10) <= 8)
			return;
		for(NPC n : player.getAttributes().getLocalNpcs()) {
			if(n == null)
				continue;
			if(n.getId() == 9 && !CombatHandler.inCombat(n) && !CombatHandler.inCombat(player) && n.getConstitution() > 0 && Locations.goodDistance(n.getPosition().getX(), n.getPosition().getY(), player.getPosition().getX(), player.getPosition().getY(), 7)) {
				n.forceChat("Hey, you there! You thief!");
				CombatHandler.setAttack(n, player);
				break;
			}
		}
	}

	public static int getFace(GameObject obj) {
		// east cake stall
		if(obj.getPosition().getX() == 2667 && obj.getPosition().getY() == 3310)
			return 3;
		// west cake stall
		if(obj.getPosition().getX() == 2655 && obj.getPosition().getY() == 3311)
			return 1;
		// north silk stall
		if(obj.getPosition().getX() == 2662 && obj.getPosition().getY() == 3314)
			return 2;
		// west silk stall
		if(obj.getPosition().getX() == 2656 && obj.getPosition().getY() == 3302)
			return 1;
		// south fur stall
		if(obj.getPosition().getX() == 2663 && obj.getPosition().getY() == 3296)
			return 4;
		// south spice stall
		if(obj.getPosition().getX() == 2658 && obj.getPosition().getY() == 3297)
			return 4;
		// north silver stall
		if(obj.getPosition().getX() == 2657 && obj.getPosition().getY() == 3314)
			return 2;
		// east game stall
		if(obj.getPosition().getX() == 2667 && obj.getPosition().getY() == 3303)
			return 3;
		// draynor village wine stall
		if(obj.getPosition().getX() == 3083 && obj.getPosition().getY() == 3251)
			return 3;
		// draynor village seed stall
		if(obj.getPosition().getX() == 3075 && obj.getPosition().getY() == 3249)
			return  obj.getId() == 7053 ? 1 : 3;
		return 2;
	}

	public int getRandomGem() {
		if(Misc.getRandom(5) < 3)
			return 1623;
		if(Misc.getRandom(10) > 5)
			return 1619;
		if(Misc.getRandom(5) > 3) 
			return 1621;
		if(Misc.getRandom(20) > 15)
			return 1617;
		if(Misc.getRandom(30) > 25)
			return 1631;
		return 1625;
	}

	public static int getRandomSeed() {
		if(Misc.getRandom(3) == 1)
			return 1942;
		if(Misc.getRandom(4) == 2)
			return 5291;
		if(Misc.getRandom(5) == 3) 
			return 5292;
		if(Misc.getRandom(7) == 6)
			return 5293;
		if(Misc.getRandom(8) == 3)
			return 5294;
		if(Misc.getRandom(10) == 9)
			return 5295;
		return 5291;
	}
}
