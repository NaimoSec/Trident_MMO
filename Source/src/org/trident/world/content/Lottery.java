package org.trident.world.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.trident.model.PlayerRights;
import org.trident.util.Misc;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;
import org.trident.world.entity.player.PlayerSaving;

/**
 * Handles the Lottery.
 * @author Gabbe
 */
public class Lottery {

	/**
	 * The list holding all users who have entered the lottery.
	 */
	private static final List<String> CONTESTERS = new ArrayList<String>();

	/*
	 * The location to the Lottery file where users are saved.
	 */
	private static final File CONTESTERS_FILE_LOCATION = new File("./data/saves/lottery/lottery.txt");

	/*
	 * The location to the Lottery file where the winners are saved.
	 */
	private static final File LAST_WINNER_FILE_LOCATION = new File("./data/saves/lottery/lotterywin.txt");

	/*
	 * Can players enter the lottery right now?
	 */
	private static boolean LOTTERY_ENABLED = true;

	/*
	 * The amount of coins required to enter the lottery.
	 */
	private static final int PRICE_TO_ENTER = 300000;

	/*
	 * Get's the amount of gold people have put in the pot.
	 */
	public static final int getPot() {
		return (CONTESTERS.size() * PRICE_TO_ENTER);
	}

	/*
	 * The user who won the Lottery last
	 */
	private static String LAST_WINNER = "None";
	
	public static String getLastWinner() {
		return LAST_WINNER;
	}
	
	/*
	 * Has the last week's winner been rewarded?
	 */
	private static boolean LAST_WINNER_REWARDED = true;

	/**
	 * Gets a random winner for the lottery.
	 * @return	A random user who has won the lottery.
	 */
	public static String getRandomWinner() {
		String winner = null;
		int listSize = CONTESTERS.size();
		if(listSize >= 4)
			winner = CONTESTERS.get(Misc.getRandom(listSize - 1));
		return winner;
	}

	/**
	 * Handles a player who wishes to enter the lottery.
	 * @param p			The player who wants to enter the lottery.
	 */
	public static void enterLottery(Player p) {
		if(!LOTTERY_ENABLED) {
			p.getPacketSender().sendInterfaceRemoval().sendMessage("The lottery is currently not active. Try again soon!");
			return;
		}
		if(p.getAttributes().getNewPlayerDelay() > 0) {
			p.getPacketSender().sendInterfaceRemoval().sendMessage("You must play more before being able to join the lottery.");
			return;
		}
		if(CONTESTERS.contains(p.getUsername())) {
			DialogueManager.start(p, 425);
			return;
		}
		boolean usePouch = p.getAttributes().getMoneyInPouch() >= PRICE_TO_ENTER;
		if(p.getInventory().getAmount(995) < PRICE_TO_ENTER && !usePouch || p.getRights() == PlayerRights.ADMINISTRATOR || p.getRights() == PlayerRights.OWNER) {
			p.getPacketSender().sendInterfaceRemoval().sendMessage("").sendMessage("You do not have enough money in your inventory to enter this week's lottery.").sendMessage("The lottery for this week costs "+Misc.insertCommasToNumber(""+PRICE_TO_ENTER+"")+" coins to enter.");
			return;
		}
		if(usePouch) {
			p.getAttributes().setMoneyInPouch(p.getAttributes().getMoneyInPouch() - PRICE_TO_ENTER);
			p.getPacketSender().sendString(8135, ""+p.getAttributes().getMoneyInPouch());
		} else
			p.getInventory().delete(995, PRICE_TO_ENTER);
		addToLottery(p.getUsername());
		p.getPacketSender().sendMessage("You have entered the lottery!").sendMessage("A winner is announced every Friday.");
		DialogueManager.start(p, 426);
		PlayerSaving.save(p);
	}

	/**
	 * Adds a user to the lottery by writing their username to the file aswell as adding them to the list of users 
	 * who have entered already.
	 * @param user		The username to add to the lists.
	 */
	public static void addToLottery(String user) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(CONTESTERS_FILE_LOCATION, true));
			writer.write(""+user+"");
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CONTESTERS.add(user);
	}

	/**
	 * Reads the lottery list and adds every user from the .txt files to the lists.
	 */
	public static void init() {
		try {
			BufferedReader r = new BufferedReader(new FileReader(CONTESTERS_FILE_LOCATION));
			while(true) {
				String line = r.readLine();
				if(line == null) {
					break;
				} else {
					line = line.trim();
				}
				if(line.length() > 0) {
					if(!CONTESTERS.contains(line)) //user might have gotten on list twice somehow.. don't give them extra chance of winning
						CONTESTERS.add(line);
				}
			}
			r.close();

			BufferedReader r2 = new BufferedReader(new FileReader(LAST_WINNER_FILE_LOCATION));
			while(true) {
				String line = r2.readLine();
				if(line == null) {
					break;
				} else {
					line = line.trim();
				}
				if(line.length() > 0) {
					if(!line.contains("NOT REWARDED. NEEDS REWARD!"))
						LAST_WINNER = line;
					else
						LAST_WINNER_REWARDED = false;
				}
			}
			r2.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Restarts the lottery and rewards this week's winner.
	 */
	public static void restartLottery() {
		if(!LOTTERY_ENABLED)
			return;
		try {
			String winner = getRandomWinner();
			if(winner != null) {
				LAST_WINNER = winner;
				Player player = PlayerHandler.getPlayerForName(winner);
				BufferedWriter writer = new BufferedWriter(new FileWriter(LAST_WINNER_FILE_LOCATION));
				writer.write(winner);
				writer.newLine();
				if(player != null) {
					rewardPlayer(player, true);
				} else {
					LAST_WINNER_REWARDED = false;
					writer.write("NOT REWARDED. NEEDS REWARD!");
					System.out.println("Player "+winner+" won the lottery but wasn't online.");
				}
				CONTESTERS.clear();
				writer.close();
				writer = new BufferedWriter(new FileWriter(CONTESTERS_FILE_LOCATION));
				writer.write("");
				writer.close();
				PlayerHandler.sendGlobalPlayerMessage("<col=D9D919><shad=0>This week's lottery winner is "+winner+"! Congratulations!");
			} else
				PlayerHandler.sendGlobalPlayerMessage("<col=D9D919><shad=0>The lottery needs some more contesters before a winner can be selected.");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Rewards a player with items for winning the lottery.
	 * @param player			The player to reward
	 * @param ignore			Should a check be ignored?
	 * @throws IOException		Throws exceptions
	 */
	public static void rewardPlayer(Player player, boolean ignore) throws IOException {
		if((!LAST_WINNER_REWARDED || ignore) && LAST_WINNER.equalsIgnoreCase(player.getUsername())) {
			LAST_WINNER_REWARDED = true;
			player.getAttributes().setMoneyInPouch(player.getAttributes().getMoneyInPouch() + getPot());
			player.getPacketSender().sendString(8135, ""+player.getAttributes().getMoneyInPouch());
			player.getPacketSender().sendMessage("You've won the lottery for this week! Congratulations!");
			player.getPacketSender().sendMessage("The reward has been added to your money pouch.");
			BufferedWriter writer = new BufferedWriter(new FileWriter(LAST_WINNER_FILE_LOCATION));
			writer.write(player.getUsername());
			writer.close();
		}
	}

	/**
	 * Handles the lottery for a player on login
	 * Checks if a user won the lottery without being rewarded.
	 * @param p		The player to handle login for.
	 */
	public static void onLogin(Player p) {
		try {
			rewardPlayer(p, false);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
