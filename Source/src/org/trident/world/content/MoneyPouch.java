package org.trident.world.content;

import org.trident.model.Item;
import org.trident.model.container.impl.Bank;
import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.entity.player.Player;

/**
 * Handles the money pouch
 * @author Goml
 * Perfected by Gabbe
 */
public class MoneyPouch {

	/**
	 * Deposits money into the money pouch
	 * @param amount How many coins to deposit
	 * @return true Returns true if transaction was successful
	 * @return false Returns false if transaction was unsuccessful
	 */
	public static boolean depositMoney(Player plr, int amount) {
		if(amount <= 0 || Dungeoneering.doingDungeoneering(plr))
			return false;
		if(plr.getAttributes().getInterfaceId() > 0) {
			plr.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
			return false;
		}
		if(plr.getConstitution() <= 0) {
			plr.getPacketSender().sendMessage("You cannot do this while dying.");
			return false;
		}
		if (validateAmount(plr, amount)) {
			float addedMoney = (float)plr.getAttributes().getMoneyInPouch() + (float)amount;
			if (addedMoney > Integer.MAX_VALUE) {
				int canStore = Integer.MAX_VALUE - plr.getAttributes().getMoneyInPouch();
				plr.getInventory().delete(995, canStore);
				plr.getAttributes().setMoneyInPouch(plr.getAttributes().getMoneyInPouch() + canStore);
				plr.getPacketSender().sendString(8135, ""+plr.getAttributes().getMoneyInPouch());
				plr.getPacketSender().sendMessage("You've added "+canStore + " coins to your money pouch.");
				Logger.log(plr.getUsername(), "Deposited "+Misc.insertCommasToNumber(String.valueOf(canStore))+" coins in money pouch.");
				return true;
			} else {
				plr.getInventory().delete(995, amount);
				plr.getAttributes().setMoneyInPouch(plr.getAttributes().getMoneyInPouch() + amount);
				plr.getPacketSender().sendString(8135, ""+plr.getAttributes().getMoneyInPouch());
				plr.getPacketSender().sendMessage("You've added "+amount+" coins to your money pouch.");
				Logger.log(plr.getUsername(), "Deposited "+Misc.insertCommasToNumber(String.valueOf(amount))+" coins in money pouch.");
				return true;
			}
		} else {
			plr.getPacketSender().sendMessage("You do not seem to have " +amount+" coins in your inventory.");
			return false;
		}
	}

	/**
	 * @param amount How many coins to withdraw
	 * @return true Returns true if transaction was successful
	 * @return false Returns false if the transaction was unsuccessful
	 */
	public static boolean withdrawMoney(Player plr, int amount) {
		if(amount <= 0 || Dungeoneering.doingDungeoneering(plr))
			return false;
		if(plr.getAttributes().getMoneyInPouch() <= 0) {
			plr.getPacketSender().sendMessage("Your money pouch is empty.");
			return false;
		}
		boolean allowWithdraw = plr.getTrading().inTrade() || plr.getDueling().inDuelScreen;
		if(!allowWithdraw) {
			if(plr.getAttributes().getInterfaceId() > 0) {
				plr.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return false;
			}
			plr.getPacketSender().sendInterfaceRemoval();
		}
		if(amount > plr.getAttributes().getMoneyInPouch())
			amount = plr.getAttributes().getMoneyInPouch();
		if((plr.getInventory().getAmount(995) + amount) > Integer.MAX_VALUE) {
			int canWithdraw = Integer.MAX_VALUE - plr.getInventory().getAmount(995);
			plr.getAttributes().setMoneyInPouch(plr.getAttributes().getMoneyInPouch() - canWithdraw);
			plr.getInventory().add(995, canWithdraw);
			plr.getPacketSender().sendString(8135, ""+plr.getAttributes().getMoneyInPouch());
			plr.getPacketSender().sendMessage("You could only withdraw "+canWithdraw+" coins.");
			Logger.log(plr.getUsername(), "Withdrew "+Misc.insertCommasToNumber(String.valueOf(canWithdraw))+" coins from money pouch.");
			if(allowWithdraw)
				plr.getPacketSender().sendItemContainer(plr.getInventory(), 3322);
			return true;
		} else if ((plr.getInventory().getAmount(995) + amount) < Integer.MAX_VALUE) {
			plr.getAttributes().setMoneyInPouch(plr.getAttributes().getMoneyInPouch() - amount);
			plr.getInventory().add(995, amount);
			plr.getPacketSender().sendString(8135, ""+plr.getAttributes().getMoneyInPouch());
			plr.getPacketSender().sendMessage("You withdraw "+amount+" coins from your pouch.");
			Logger.log(plr.getUsername(), "Withdrew "+Misc.insertCommasToNumber(String.valueOf(amount))+" coins from money pouch.");
			if(allowWithdraw)
				plr.getPacketSender().sendItemContainer(plr.getInventory(), 3322);
			return true;
		}
		return false;
	}


	public static void toBank(Player player) {
		if(!player.getAttributes().isBanking() || player.getAttributes().getInterfaceId() != 5292)
			return;
		Item item = new Item(995, player.getAttributes().getMoneyInPouch());
		if(item.getAmount() <= 0)
			return;
		player.getAttributes().setCurrentBankTab(Bank.getTabForItem(player, item.getId()));
		if(player.getBank(player.getAttributes().getCurrentBankTab()).getFreeSlots() <= 0 && !player.getBank(player.getAttributes().getCurrentBankTab()).contains(995)) {
			player.getPacketSender().sendMessage("Your bank is currently full.");
			return;
		}
		if((player.getBank(player.getAttributes().getCurrentBankTab()).getAmount(995) + item.getAmount()) >= Integer.MAX_VALUE) {
			player.getPacketSender().sendMessage("Your bank cannot hold any more coins!");
			return;
		}
		player.getBank(player.getAttributes().getCurrentBankTab()).add(item);
		player.getAttributes().setMoneyInPouch(0);
		player.getPacketSender().sendString(8135, "0"); //refresh money pouch
	}

	/**
	 * Validates that the player has the coins in their inventory
	 * @param amount The amount the player wishes to insert
	 * @return true Returns true if the player has the coins in their inventory
	 * @return false Returns false if the player does not have the coins in their inventory
	 */
	private static boolean validateAmount(Player plr, int amount) {
		return plr.getInventory().getAmount(995) >= amount;
	}

}