package org.trident.world.content.skills.impl.agility;

import org.trident.model.GameObject;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.entity.player.Player;

public class Agility {

	public static boolean handleObject(Player p, GameObject object) {
		if(object.getId() == 2309) {
			if(p.getSkillManager().getMaxLevel(Skill.AGILITY) < 55) {
				p.getPacketSender().sendMessage("You need an Agility level of at least 55 to enter this course.");
				return true;
			}
		}
		ObstacleData agilityObject = ObstacleData.forId(object.getId());
		if(agilityObject != null) {
			if(p.getSkillManager().getSkillAttributes().getAgilityAttributes().isCrossingObstacle())
				return true;
			p.setPositionToFace(object.getPosition());
			p.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(true);
			boolean wasRunning = p.getAttributes().isRunning();
			if(agilityObject.mustWalk()) {
				p.getAttributes().setRunning(false);
				p.getPacketSender().sendRunStatus();
			}
			agilityObject.cross(p, wasRunning);
		}
		return false;
	}

	public static boolean passedAllObstacles(Player player) {
		for(boolean crossedObstacle : player.getSkillManager().getSkillAttributes().getAgilityAttributes().getCrossedObstacles()) {
			if(!crossedObstacle)
				return false;
		}
		return true;
	}

	public static void reset(Player player) {
		for(int i = 0; i < player.getSkillManager().getSkillAttributes().getAgilityAttributes().getCrossedObstacles().length; i++)
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(i, false);
	}
	
	public static boolean isSucessive(Player player) {
		return Misc.getRandom(player.getSkillManager().getCurrentLevel(Skill.AGILITY) / 2) > 1;
	}

	public static void ticketExchange(Player player) {
		if(player.getSkillManager().getSkillAttributes().getAgilityAttributes().isCrossingObstacle())
			return;
		player.getPacketSender().sendInterface(8292);
		player.getPacketSender().sendString(8383, "Ticket Exchange");
	}

	final static int[][] data = {{8387, 1, 200}, {8389, 10, 200}, {8390, 25, 200}, {8391, 100, 200}, {8392, 1000, 200}, {8382, 3, 2998}, {8393, 10, 3000},  {8381, 1000, 2997}};
	public static void buyItem(Player player, int button) {
		if(System.currentTimeMillis() - player.getAttributes().getClickDelay() <= 1000)
			return;
		int index = -1;
		for(int i = 0; i < data.length; i++) {
			if(data[i][0] == button) {
				index = i;
			}
		}
		if(index == -1)
			return;
		int amountNeeded = data[index][1];
		if(player.getInventory().getAmount(2996) < amountNeeded) {
			player.getPacketSender().sendMessage("You don't have enough Agility tickets to purchase this.");
			return;
		}
		int reward = data[index][2];
		if(reward == 200) { //Ticket reward
			player.getInventory().delete(2996, amountNeeded);
			player.getSkillManager().addExperience(Skill.AGILITY, reward * amountNeeded, false);
		} else {
			if(player.getInventory().getFreeSlots() < 1) {
				player.getPacketSender().sendMessage("Please get some free inventory space before buying this.");
				return;
			}
			player.getInventory().delete(2996, amountNeeded);
			player.getInventory().add(reward, 1);
		}
		player.getInventory().refreshItems();
		player.getAttributes().setClickDelay(System.currentTimeMillis());
	}

	public static int getPriceForItem(int item) {
		switch(item) {
		case 14936:
			return 100;
		case 14938:
			return 75;
		case 88:
			return 40;
		default:
			return 10000;
		}
	}
}
