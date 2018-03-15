package org.trident.world.content.skills.impl.dungeoneering;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.GameObject;
import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.player.Player;

/**
 * Handles the Dungeoneering skill
 * @author Gabbe
 */
public class Dungeoneering {

	/*
	 * 
	 */
	public static final int DUNGEONEERING_PARTY_INTERFACE = 26224;
	public static final int DUNGEONEERING_GATESTONE_ID = 17489;

	/**
	 * Starts a Dungeoneering floor.
	 * Creates a mapinstance for the player and moves them to the dungeon coords.
	 * @param p			The player who's starting Dungeoneering.
	 * @param floor		The floor which the player is going to be exploring.
	 */
	public static void start(final Player p) {
		if(p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor() == null || p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty() == null) {
			DialogueManager.start(p, 427);
			return;
		}
		final DungeoneeringFloor floor = p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor();
		if(floor.getParty().hasEnteredDungeon())
			return;
		if(floor.getFloor() == null) {
			DialogueManager.start(p, 428);
			return;
		}
		if(floor.getParty().getOwner() != p) {
			p.getPacketSender().sendMessage("Only the party leader can start the dungeon.");
			return;
		}
		String plrCannotEnter = null;
		for(Player member : floor.getParty().getPlayers()) {
			if(member != null) {
				if(member.getAdvancedSkills().getSummoning().getFamiliar() != null) {
					member.getPacketSender().sendMessage("You must dismiss your familiar before being allowed to enter a dungeon.");
					p.getPacketSender().sendMessage(""+p.getUsername()+" has to dismiss their familiar before you can enter the dungeon.");
					return;
				}
				for(Item t : member.getEquipment().getItems()) {
					if(t != null && t.getId() > 0 && t.getId() != 15707) {
						plrCannotEnter = member.getUsername();
					}
				}
				for(Item t : member.getInventory().getItems()) {
					if(t != null && t.getId() > 0 && t.getId() != 15707) {
						plrCannotEnter = member.getUsername();
					}
				}
				if(plrCannotEnter != null) {
					p.getPacketSender().sendMessage("Your team cannot enter the dungeon because "+plrCannotEnter+" hasn't banked").sendMessage("all of their items.");
					return;
				}
			}
		}
		floor.getParty().enteredDungeon(true);
		final int floorId = floor.getFloor().ordinal()+1;
		final int height = p.getIndex() * 4;
		final int amt = floor.getParty().getPlayers().size() >= 2 ? 35000: 45000;
		for(Player member : floor.getParty().getPlayers()) {
			if(member != null) {
				member.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setExperienceReceived(0).setTokensReceived(0);
				member.getAttributes().setRegionInstance(null);
				member.getMovementQueue().stopMovement();
				member.getAttributes().setClickDelay(2000);
				member.moveTo(new Position(floor.getFloor().getEntrance().getX(), floor.getFloor().getEntrance().getY(), height));
				member.getInventory().add(18201, amt);
				ItemBinding.onDungeonEntrance(member);
				member.getPacketSender().sendInterfaceRemoval().sendMessage("").sendMessage("@red@Welcome to Dungeoneering floor "+floorId+"!").sendMessage("@red@Remember that you can buy items from the smuggler!").sendMessage("").sendMessage("The party leader has received a Gatestone.");
				PrayerHandler.deactivateAll(member);
				CurseHandler.deactivateAll(member);
				for(Skill skill : Skill.values())
					member.getSkillManager().setCurrentLevel(skill, member.getSkillManager().getMaxLevel(skill));
				member.getAttributes().setWalkToTask(null);
				member.getPacketSender().sendClientRightClickRemoval();
			}
		}
		floor.getParty().setDeaths(0);
		floor.getParty().setKills(0);
		DungeoneeringHandler.updateFrame(floor, 37508, "Party deaths: 0");
		DungeoneeringHandler.updateFrame(floor, 37509, "Party kills: 0");
		TaskManager.submit(new Task(6) {
			@Override
			public void execute() {
				setupFloor(floor, height);
				stop();
			}
		});
		p.getInventory().add(new Item(17489));
	}

	/**
	 * Sets up a floor with entities etc for a team.
	 * @param floor		The floor to setup entities for.
	 */
	public static void setupFloor(DungeoneeringFloor floor, int height) {
		/*
		 * Spawning npcs
		 */
		DungeonNPCSpawns.spawnNpcs(floor, height);
		/*if(floor.getFloor().getNpcs() != null) {
			for(NPC n : floor.getFloor().getNpcs()[0]) {
				if(n != null) {
					n.getPosition().setZ(height);
					GameServer.getWorld().register(n);
					floor.getRegion().addNpc(n);
				}
			}
		}*/
		/*
		 * Spawning objects
		 */
		if(floor.getFloor().getObjects() != null) {
			for(GameObject obj : floor.getFloor().getObjects()) {
				if(obj != null) {
					obj.getPosition().setZ(height);
					CustomObjects.spawnGlobalObjectWithinDistance(obj);
				}
			}
		}
	}

	/**
	 * Leaves a dungeoneering floor
	 * @param p	The player leaving the floor
	 */
	public static void leave(Player p) {
		if(p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor() == null || p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty() == null)
			return;
		p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty().remove(p);
	}
	
	/**
	 * Checks if a Player is doing Dungeoneering
	 * @param p		The player to check if they're doing Dungeoneering or not for.
	 * @return		Returns true if the player is doing Dungeoneering or false if they aren't.
	 */
	public static boolean doingDungeoneering(Player p) {
		return p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor() != null && p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty().hasEnteredDungeon();
	}
}
