package org.trident.world.content.minigames.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.GameObject;
import org.trident.model.Graphic;
import org.trident.model.Item;
import org.trident.model.Projectile;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.entity.player.Player;

/**
 * @uthor: Gabbe
 */
public class ArcheryCompetition {

	public static void fireTarget(final Player player, final GameObject target) {
		player.getMovementQueue().stopMovement();
		if(!hasRequirements(player))
			return;
		player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().setEventRunning(true);
		player.setPositionToFace(target.getPosition());
		player.performAnimation(new Animation(426));
		TaskManager.submit(new Task(2, player, false) {
			@Override
			public void execute() {
				player.getPacketSender().sendProjectile(new Projectile(player.getPosition(), target.getPosition(), new Graphic(19)), target);
				handlePoints(player);
				player.getEquipment().delete(new Item(882), 13);
				stop();
			}
			@Override
			public void stop() {
				setEventRunning(false);
				player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().setEventRunning(false);
			}
		});
	}

	public static boolean hasRequirements(Player player) {
		boolean wearingBronzeArrows = player.getEquipment().getAmount(882) >= 10 || player.getEquipment().contains(882);
		boolean wearingBow = player.getEquipment().getItems()[3].getId() == 841;
		if(player.getSkillManager().getMaxLevel(Skill.RANGED) < 50) {
			player.getPacketSender().sendMessage("You need a Ranged level of at least 50 to participate in this minigame.");
			return false;
		}
		if(!player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().isParticipating()) {
			player.getPacketSender().sendMessage("To participate, please pay the Archer 200 coins.");
			return false;
		}
		if(!wearingBow) {
			player.getPacketSender().sendMessage("You need to be wearing a Shortbow to shoot at a target.");
			return false;
		}
		if(!wearingBronzeArrows) {
			player.getPacketSender().sendMessage("You need to have some Bronze arrows equipped to shoot at a target.");
			return false;
		}
		if(!inRange(player)) {
			player.getPacketSender().sendMessage("You need to be in range to shoot at a target.");
			return false;
		}
		if(player.getInventory().getFreeSlots() < 1) {
			player.getPacketSender().sendMessage("You need 1 free inventory slot.");
			return false;
		}
		if(!inRange(player))
			return false;

		if(player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().eventIsRunning())
			return false;
		return true;
	}

	public static boolean inRange(Player player) {
		int x = player.getPosition().getX();
		int y = player.getPosition().getY();
		return (x >= 2669 && x < 2675 && y >= 3414 && y < 3422);
	}

	//Calculates score by randomizing ranged level / 6 and then calculates points of total results.
	public static void handlePoints(Player player) {
		player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().setTargetsHit(player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().getTargetsHit() + 1);
		int k = player.getSkillManager().getMaxLevel(Skill.RANGED) / 10;
		final int scoree = Misc.getRandom(k) + 1;
		for(int i = 0; i < player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().getScore().length; i++) {
			if(player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().getScore()[i] == 0)
				player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().getScore()[i] = scoree;
		}
		player.getPacketSender().sendMessage("Targets fired: "+player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().getTargetsHit()+"/5");
		if(player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().getTargetsHit() == 5) {
			int result = 0;
			for(int i = 0; i < player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().getScore().length; i++) 
				result += player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().getScore()[i];
			int tickets = Misc.getRandom(result) + 1;
			int rangedXP = Misc.getRandom(800);
			player.getPacketSender().sendMessage("You received a total score of "+result+" and "+tickets+" Archery tickets.");
			if(!player.getAttributes().experienceLocked()) {
				player.getPacketSender().sendMessage("You've received "+rangedXP+" Ranged XP.");
				player.getSkillManager().addExperience(Skill.RANGED, rangedXP, false);
			}
			resetScore(player);
			DialogueManager.start(player, 59);
			player.getInventory().add(1464, tickets);
			return;
		}
	}

	public static void resetScore(Player player) {
		for(int i = 0; i < player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().getScore().length; i++)
			player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().getScore()[i] = 0;
		player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().setTargetsHit(0).setEventRunning(false).setParticipating(false);
		player.getAttributes().setInputHandling(null);
	}

	public static void exhchangeTickets(Player player, int ticketAmount) {
		player.getPacketSender().sendInterfaceRemoval();
		player.getAttributes().setDialogueAction(-1);
		if(player.getInventory().getFreeSlots() < 1) {
			player.getPacketSender().sendMessage("You do not have enough free inventory slots.");
			return;
		}
		if(player.getInventory().getAmount(1464) >= ticketAmount) {
			player.getInventory().delete(1464, ticketAmount);
			player.getInventory().add(892, ticketAmount * 2);
			player.getPacketSender().sendMessage("You've exchanged "+ticketAmount+" Archery tickets for "+ticketAmount*2+" Rune arrows.");
		} else {
			player.getPacketSender().sendMessage("You do not have that many Archery tickets!");
			return;
		}
		resetScore(player); // Just to be safe :D
	}

	public static int getItemPrice(int itemId) {
		switch(itemId) {
		case 19143:
		case 19149:
		case 19146:
			return 2500;
		case 19152:
		case 19162:
		case 19157:
			return 3;
		}
		return 100000;
	}

}
