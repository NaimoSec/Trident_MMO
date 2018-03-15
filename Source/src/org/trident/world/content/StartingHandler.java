package org.trident.world.content;

import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.NewPlayerTask;
import org.trident.model.Flag;
import org.trident.model.Item;
import org.trident.model.container.impl.Bank;
import org.trident.model.container.impl.Equipment;
import org.trident.model.movement.MovementStatus;
import org.trident.util.StarterSaver;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.impl.TutorialDialogues;
import org.trident.world.entity.player.Player;

public class StartingHandler {

	/**
	 * Sets up a new account.
	 * @param player	The player to setup the account for.
	 */
	public static void setupNewAccount(Player player) {
		player.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
		for(int i = 0; i < 9; i++)
			player.setBank(i, new Bank(player));
		if(!player.getAttributes().hasStarted())
			DialogueManager.start(player, TutorialDialogues.startingDialogue(player));
	}

	/**
	 * Starts the tutorial dialogues for a player 
	 * and makes them unable to walk during it.
	 * @param player	The player to start the tutorial for.
	 */
	public static void startTutorial(Player player) {
		player.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
		DialogueManager.start(player, TutorialDialogues.tutorialDialogues(player));
	}

	/**
	 * Finishes the tutorial and gives them a reward if
	 * they completed it all.
	 * @param player	The player who is going to finish the tutorial.
	 * @param warned	Has the player been warned about exiting the tutorial?
	 */
	public static void finishTutorial(Player player, boolean warned) {
		if(!warned && !player.getAttributes().getTutorialFinished()[5]) {
			boolean finishedFullTut = true;
			for(int i = 0; i < 4; i++) {
				if(!player.getAttributes().getTutorialFinished()[i]) {
					finishedFullTut = false;
					break;
				}
			}
			if(!finishedFullTut) {
				DialogueManager.start(player, TutorialDialogues.confirmFinishTut(player));
				return;
			} else
				player.getAttributes().getTutorialFinished()[4] = true;
		}
		player.getMovementQueue().setMovementStatus(MovementStatus.NONE);
		player.getPacketSender().sendInterfaceRemoval();
		giveStarterPackage(player);
		if(player.getAttributes().getResetPosition() != null)
			player.moveTo(player.getAttributes().getResetPosition());
		player.getAttributes().setResetPosition(null);
	}

	/**
	 * Adds the starter items for a new player if
	 * they haven't already received 2 on other accounts from same host.
	 * @param player	The player to add starting items to.
	 */
	public static void giveStarterPackage(Player player) {
		if(player.getAttributes().hasStarted())
			return;
		if(StarterSaver.getStartersReceived(player.getHardwareNumber() != 0 ? String.valueOf(player.getHardwareNumber()) : player.getHostAdress()) < 2) {
			//First row, money food and magic orb
			player.getInventory().setItem(0, new Item(995, 400000)).setItem(1, new Item(330, 500)).setItem(2, new Item(362, 200)).setItem(3, new Item(386, 50));
			//Second row, skilling items
			player.getInventory().setItem(4, new Item(1351)).setItem(5, new Item(590)).setItem(6, new Item(1265)).setItem(7, new Item(946));
			//Third row, weapons
			player.getInventory().setItem(8, new Item(1323)).setItem(9, new Item(841)).setItem(10, new Item(1379));
			//Fourth row, melee equipment
			player.getInventory().setItem(12, new Item(1155)).setItem(13, new Item(1117)).setItem(14, new Item(1075)).setItem(15, new Item(1189));
			//Fourth row ranged equipment
			player.getInventory().setItem(16, new Item(1167)).setItem(17, new Item(1129)).setItem(18, new Item(1095)).setItem(19, new Item(882, 150));
			//Fifth row, magic equip
			player.getInventory().setItem(20, new Item(12964)).setItem(21, new Item(12971)).setItem(22, new Item(12978)).setItem(23, new Item(15246));
			player.getInventory().add(757, 1);
			//player.getEquipment().setItem(Equipment.CAPE_SLOT, new Item(1007));
			player.getEquipment().refreshItems();
			player.getInventory().refreshItems();
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			player.getAttributes().setNewPlayerDelay(3100);
			TaskManager.submit(new NewPlayerTask(player));
			DialogueManager.start(player, TutorialDialogues.finishedTutorial(player));
			player.getPacketSender().sendMessage("Read the book in your inventory for some quick tips.");
			StarterSaver.receivedStarter(player.getHardwareNumber() != 0 ? String.valueOf(player.getHardwareNumber()) : player.getHostAdress());
			player.getAttributes().getTutorialFinished()[5] = true;
			//player.getPacketSender().sendBlinkingHint("Shortcuts", "Make sure to check out---the shortcuts!---You can find most things --- there.", 438, 85, 15, 5, 2, 45);
		} else
			player.getPacketSender().sendMessage("Your connection has received enough starters.");
		player.getAttributes().setStarted(true);
	}
}
