package org.trident.world.content;

import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerSaving;

/**
 * bank-pin
 * @author Gabbe
 * NOTE: This was taken & redone from my PI base
 */
public class BankPin {

	public static void deletePin(Player player) {
		player.getAttributes().getBankPinAttributes().setHasBankPin(false).setHasEnteredBankPin(false).setInvalidAttempts(0).setLastAttempt(System.currentTimeMillis());
		for(int i = 0; i < player.getAttributes().getBankPinAttributes().getBankPin().length; i++) {
			player.getAttributes().getBankPinAttributes().getBankPin()[i] = -1;
			player.getAttributes().getBankPinAttributes().getEnteredBankPin()[i] = -1;
		}
		player.getPacketSender().sendMessage("Your bank-pin was deleted.");
		Logger.log(player.getUsername(), "Player deleted their bank-pin from the host: "+player.getHostAdress());
	}

	public static void init(Player player) {
		if(player.getAttributes().getBankPinAttributes().getInvalidAttempts() == 3) {
			if(System.currentTimeMillis() - player.getAttributes().getBankPinAttributes().getLastAttempt() < 400000) {
				player.getPacketSender().sendMessage("You must wait "+(int)((400 - (System.currentTimeMillis() - player.getAttributes().getBankPinAttributes().getLastAttempt()) * 0.001))+" seconds before attempting to enter your bank-pin again.");
				return;
			} else
				player.getAttributes().getBankPinAttributes().setInvalidAttempts(0);
			player.getPacketSender().sendInterfaceRemoval();
		}
		randomizeNumbers(player);
		player.getPacketSender().sendString(15313, "First click the FIRST digit");
		player.getPacketSender().sendString(14923, "");
		player.getPacketSender().sendString(14913, "?");
		player.getPacketSender().sendString(14914, "?");
		player.getPacketSender().sendString(14915, "?");
		player.getPacketSender().sendString(14916, "?");
		sendPins(player);
		player.getPacketSender().sendInterface(7424);
		for(int i = 0; i < player.getAttributes().getBankPinAttributes().getEnteredBankPin().length; i++)
			player.getAttributes().getBankPinAttributes().getEnteredBankPin()[i] = -1;
	}

	public static void clickedButton(Player player, int button) {
		sendPins(player);
		if(player.getAttributes().getBankPinAttributes().getEnteredBankPin()[0] == -1) {
			player.getPacketSender().sendString(15313, "Now click the SECOND digit");
			player.getPacketSender().sendString(14913, "*");
			for(int i = 0; i < actionButtons.length; i++)
				if(actionButtons[i] == button)
					player.getAttributes().getBankPinAttributes().getEnteredBankPin()[0] = player.getAttributes().getBankPinAttributes().getBankPins()[i];
		} else if(player.getAttributes().getBankPinAttributes().getEnteredBankPin()[1] == -1) {
			player.getPacketSender().sendString(15313, "Now click the THIRD digit");
			player.getPacketSender().sendString(14914, "*");
			for(int i = 0; i < actionButtons.length; i++)
				if(actionButtons[i] == button)
					player.getAttributes().getBankPinAttributes().getEnteredBankPin()[1] = player.getAttributes().getBankPinAttributes().getBankPins()[i];
		} else if(player.getAttributes().getBankPinAttributes().getEnteredBankPin()[2] == -1) {
			player.getPacketSender().sendString(15313, "Now click the FINAL digit");
			player.getPacketSender().sendString(14915, "*");
			for(int i = 0; i < actionButtons.length; i++)
				if(actionButtons[i] == button)
					player.getAttributes().getBankPinAttributes().getEnteredBankPin()[2] = player.getAttributes().getBankPinAttributes().getBankPins()[i];
		} else if(player.getAttributes().getBankPinAttributes().getEnteredBankPin()[3] == -1) {
			player.getPacketSender().sendString(14916, "*");
			for(int i = 0; i < actionButtons.length; i++)
				if(actionButtons[i] == button)
					player.getAttributes().getBankPinAttributes().getEnteredBankPin()[3] = player.getAttributes().getBankPinAttributes().getBankPins()[i];
			if(!player.getAttributes().getBankPinAttributes().hasBankPin()) {
				player.getAttributes().getBankPinAttributes().setHasBankPin(true).setHasEnteredBankPin(true).setBankPin(player.getAttributes().getBankPinAttributes().getEnteredBankPin());
				player.getPacketSender().sendMessage("You've created a bank-pin. Your digit is "+player.getAttributes().getBankPinAttributes().getEnteredBankPin()[0]+"-"+player.getAttributes().getBankPinAttributes().getEnteredBankPin()[1]+"-"+player.getAttributes().getBankPinAttributes().getEnteredBankPin()[2]+"-"+player.getAttributes().getBankPinAttributes().getEnteredBankPin()[3]+". Please write it down.");
				player.getPacketSender().sendInterfaceRemoval();
				PlayerSaving.save(player);
				Logger.log(player.getUsername(), "Player set a bank-pin from the host: "+player.getHostAdress());
				return;
			}
			for(int i = 0; i < player.getAttributes().getBankPinAttributes().getEnteredBankPin().length; i++) {
				if(player.getAttributes().getBankPinAttributes().getEnteredBankPin()[i] != player.getAttributes().getBankPinAttributes().getBankPin()[i]) {
					player.getPacketSender().sendInterfaceRemoval();
					int invalidAttempts = player.getAttributes().getBankPinAttributes().getInvalidAttempts() + 1;
					if(invalidAttempts >= 3)
						player.getAttributes().getBankPinAttributes().setLastAttempt(System.currentTimeMillis());
					player.getAttributes().getBankPinAttributes().setInvalidAttempts(invalidAttempts);
					player.getPacketSender().sendMessage("Invalid bank-pin entered entered.");
					return;
				}
			}
			player.getAttributes().getBankPinAttributes().setInvalidAttempts(0).setHasEnteredBankPin(true);
			player.getBank(player.getAttributes().getCurrentBankTab()).open();
		}
		randomizeNumbers(player);
	}

	private static void sendPins(Player player) {
		for(int i = 0; i < player.getAttributes().getBankPinAttributes().getBankPins().length; i++)
			player.getPacketSender().sendString(stringIds[i], ""+player.getAttributes().getBankPinAttributes().getBankPins()[i]);
	}

	public static void randomizeNumbers(Player player) {
		int i = Misc.getRandom(5);
		switch(i) {
		case 0:
			player.getAttributes().getBankPinAttributes().getBankPins()[0] = 1;
			player.getAttributes().getBankPinAttributes().getBankPins()[1] = 7;
			player.getAttributes().getBankPinAttributes().getBankPins()[2] = 0;
			player.getAttributes().getBankPinAttributes().getBankPins()[3] = 8;
			player.getAttributes().getBankPinAttributes().getBankPins()[4] = 4;
			player.getAttributes().getBankPinAttributes().getBankPins()[5] = 6;
			player.getAttributes().getBankPinAttributes().getBankPins()[6] = 5;
			player.getAttributes().getBankPinAttributes().getBankPins()[7] = 9;
			player.getAttributes().getBankPinAttributes().getBankPins()[8] = 3;
			player.getAttributes().getBankPinAttributes().getBankPins()[9] = 2;
			break;

		case 1:
			player.getAttributes().getBankPinAttributes().getBankPins()[0] = 5;
			player.getAttributes().getBankPinAttributes().getBankPins()[1] = 4;
			player.getAttributes().getBankPinAttributes().getBankPins()[2] = 3;
			player.getAttributes().getBankPinAttributes().getBankPins()[3] = 7;
			player.getAttributes().getBankPinAttributes().getBankPins()[4] = 8;
			player.getAttributes().getBankPinAttributes().getBankPins()[5] = 6;
			player.getAttributes().getBankPinAttributes().getBankPins()[6] = 9;
			player.getAttributes().getBankPinAttributes().getBankPins()[7] = 2;
			player.getAttributes().getBankPinAttributes().getBankPins()[8] = 1;
			player.getAttributes().getBankPinAttributes().getBankPins()[9] = 0;
			break;

		case 2:
			player.getAttributes().getBankPinAttributes().getBankPins()[0] = 4;
			player.getAttributes().getBankPinAttributes().getBankPins()[1] = 7;
			player.getAttributes().getBankPinAttributes().getBankPins()[2] = 6;
			player.getAttributes().getBankPinAttributes().getBankPins()[3] = 5;
			player.getAttributes().getBankPinAttributes().getBankPins()[4] = 2;
			player.getAttributes().getBankPinAttributes().getBankPins()[5] = 3;
			player.getAttributes().getBankPinAttributes().getBankPins()[6] = 1;
			player.getAttributes().getBankPinAttributes().getBankPins()[7] = 8;
			player.getAttributes().getBankPinAttributes().getBankPins()[8] = 9;
			player.getAttributes().getBankPinAttributes().getBankPins()[9] = 0;
			break;

		case 3:
			player.getAttributes().getBankPinAttributes().getBankPins()[0] = 9;
			player.getAttributes().getBankPinAttributes().getBankPins()[1] = 4;
			player.getAttributes().getBankPinAttributes().getBankPins()[2] = 2;
			player.getAttributes().getBankPinAttributes().getBankPins()[3] = 7;
			player.getAttributes().getBankPinAttributes().getBankPins()[4] = 8;
			player.getAttributes().getBankPinAttributes().getBankPins()[5] = 6;
			player.getAttributes().getBankPinAttributes().getBankPins()[6] = 0;
			player.getAttributes().getBankPinAttributes().getBankPins()[7] = 3;
			player.getAttributes().getBankPinAttributes().getBankPins()[8] = 1;
			player.getAttributes().getBankPinAttributes().getBankPins()[9] = 5;
			break;

		case 4:
			player.getAttributes().getBankPinAttributes().getBankPins()[0] = 8;
			player.getAttributes().getBankPinAttributes().getBankPins()[1] = 7;
			player.getAttributes().getBankPinAttributes().getBankPins()[2] = 6;
			player.getAttributes().getBankPinAttributes().getBankPins()[3] = 2;
			player.getAttributes().getBankPinAttributes().getBankPins()[4] = 5;
			player.getAttributes().getBankPinAttributes().getBankPins()[5] = 4;
			player.getAttributes().getBankPinAttributes().getBankPins()[6] = 1;
			player.getAttributes().getBankPinAttributes().getBankPins()[7] = 0;
			player.getAttributes().getBankPinAttributes().getBankPins()[8] = 3;
			player.getAttributes().getBankPinAttributes().getBankPins()[9] = 9;
			break;
		}
		sendPins(player);
	}

	private static final int stringIds[] = { 
		14883, 14884, 14885, 14886, 
		14887, 14888, 14889, 14890, 
		14891, 14892
	};

	private static final int actionButtons[] = { 
		14873, 14874, 14875, 14876, 
		14877, 14878, 14879, 14880, 
		14881, 14882
	};

	public static class BankPinAttributes {
		public BankPinAttributes() {}

		private boolean hasBankPin;
		private boolean hasEnteredBankPin;
		private int[] bankPin = new int[4];
		private int[] enteredBankPin = new int[4];
		private int bankPins[] = {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9
		};
		private int invalidAttempts;
		private long lastAttempt;

		public boolean hasBankPin() {
			return hasBankPin;
		}

		public BankPinAttributes setHasBankPin(boolean hasBankPin) {
			this.hasBankPin = hasBankPin;
			return this;
		}

		public boolean hasEnteredBankPin() {
			return hasEnteredBankPin;
		}

		public BankPinAttributes setHasEnteredBankPin(boolean hasEnteredBankPin) {
			this.hasEnteredBankPin = hasEnteredBankPin;
			return this;
		}

		public int[] getBankPin() {
			return bankPin;
		}
		
		public BankPinAttributes setBankPin(int[] bankPin) {
			this.bankPin = bankPin;
			return this;
		}

		public int[] getEnteredBankPin() {
			return enteredBankPin;
		}

		public int[] getBankPins() {
			return bankPins;
		}

		public int getInvalidAttempts() {
			return invalidAttempts;
		}

		public BankPinAttributes setInvalidAttempts(int invalidAttempts) {
			this.invalidAttempts = invalidAttempts;
			return this;
		}

		public long getLastAttempt() {
			return lastAttempt;
		}
		
		public BankPinAttributes setLastAttempt(long lastAttempt) {
			this.lastAttempt = lastAttempt;
			return this;
		}
	}
}
