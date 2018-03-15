package org.trident.world.content;

import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.model.Item;
import org.trident.model.LendedItem;
import org.trident.model.PlayerRights;
import org.trident.model.container.impl.Inventory;
import org.trident.model.definitions.ItemDefinition;
import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.minigames.impl.FightPit;
import org.trident.world.entity.player.Player;

/**
 * @author: @Gabbe
 * Warning:
 * This crap is so messy and ugly. Will redo it once I get some time over.
 * Should be dupe-free.
 */

public class Trading {

	private Player player;
	private ItemLending itemLending;
	public Trading(Player p) {
		this.player = p;
		itemLending = new ItemLending(p);
	}

	public void requestTrade(Player player2) {
		if(player == null || player2 == null || player.getConstitution() <= 0 || player2.getConstitution() <= 0 || player.isTeleporting() || player2.isTeleporting())
			return;
		if(player.getHostAdress().equals(player2.getHostAdress()) && player.getRights() != PlayerRights.OWNER && player.getRights() != PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage("Same IP-adress found. You cannot trade yourself from the same IP.");
			return;
		}
		if(System.currentTimeMillis() - lastTradeSent < 5000 && !inTrade()) {
			player.getPacketSender().sendMessage("You're sending trade requests too frequently. Please slow down.");
			return;
		}
		if(inTrade()) {
			declineTrade(true);
			return;
		}
		if(player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom() && !player2.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()) {
			player.getPacketSender().sendMessage("You cannot reach that.");
			return;
		}
		if(player.getAttributes().isShopping() || player.getAttributes().isBanking()) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		if(player2.getAttributes().getInterfaceId() > 0 || player2.getTrading().inTrade() || player2.getAttributes().isBanking() || player2.getAttributes().isShopping() || player2.getDueling().inDuelScreen || FightPit.inFightPits(player2)) {
			player.getPacketSender().sendMessage("The other player is currently busy.");
			return;
		}
		if(player.getAttributes().getInterfaceId() > 0 || inTrade() || player.getAttributes().isBanking() || player.getAttributes().isShopping() || player.getDueling().inDuelScreen || FightPit.inFightPits(player)) {
			player.getPacketSender().sendMessage("You are currently unable to trade another player.");
			if(player.getAttributes().getInterfaceId() > 0)
				player.getPacketSender().sendMessage("Please close all open interfaces before requesting to open another one.");
			return;
		}
		tradeWith = player2.getIndex();
		if(getTradeWith() == player.getIndex())
			return;
		if(!Locations.goodDistance(player.getPosition().getX(), player.getPosition().getY(), player2.getPosition().getX(), player2.getPosition().getY(), 2)) {
			player.getPacketSender().sendMessage("Please get closer to request a trade.");
			return;
		}
		if(!inTrade() && player2.getTrading().tradeRequested() && player2.getTrading().getTradeWith() == player.getIndex()) {
			openTrade();
			player2.getTrading().openTrade();
		} else if(!inTrade()) {
			setTradeRequested(true);
			player.getPacketSender().sendMessage("You've sent a trade request to "+player2.getUsername()+".");
			player2.getPacketSender().sendMessage(player.getUsername() +":tradereq:");
		}
		lastTradeSent = System.currentTimeMillis();
	}

	public void openTrade() {
		player.getPacketSender().sendClientRightClickRemoval();
		Player player2 = World.getPlayers().get(getTradeWith());
		if(player == null || player2 == null || getTradeWith() == player.getIndex() || player.getAttributes().isBanking())
			return;
		setTrading(true);
		setTradeRequested(false);
		setCanOffer(true);
		setTradeStatus(1);
		player.getPacketSender().sendInterfaceItems(3415, offeredItems);
		player2.getPacketSender().sendInterfaceItems(3415, player2.getTrading().offeredItems);
		sendText(player2, true);
		player.getInventory().refreshItems();
		player.getPacketSender().sendInterfaceItems(3415, offeredItems);
		player.getPacketSender().sendInterfaceItems(3416, player2.getTrading().offeredItems);
		getItemLending().refreshLoanBoxes();
		getItemLending().temporarLendItem = null;
		getItemLending().loanState = 0;
		inTradeWith = player2.getIndex();
	}

	public void declineTrade(boolean tellOther) {
		Player player2 = getTradeWith() >= 0 ? World.getPlayers().get(getTradeWith()) : null;
		for (Item item : offeredItems) {
			if (item.getAmount() < 1)
				continue;
			player.getInventory().add(item);
			Logger.log(player.getUsername(), "Received item from unfinished trade: "+item.getDefinition().getName()+" x"+Misc.insertCommasToNumber(String.valueOf(item.getAmount()))+" from trade partner: "+player2 != null ? player2.getUsername() : "null");
		}
		if(getItemLending().temporarLendItem != null && getItemLending().loanState > 1)
			player.getInventory().add(new Item(getItemLending().temporarLendItem.getId(), 1));
		offeredItems.clear();
		if(tellOther && getTradeWith() > -1) {
			if(player2 == null)
				return;
			player2.getTrading().declineTrade(false);
			player2.getPacketSender().sendMessage("Other player declined the trade.");
		}
		resetTrade();
	}

	public void sendText(Player player2, boolean tradeWealthUpdate) {
		if(player2 == null)
			return;
		player2.getPacketSender().sendString(3451, "" + Misc.formatPlayerName2(player.getUsername()) + "");
		player2.getPacketSender().sendString(3417, "Trading with: " + Misc.formatPlayerName2(player.getUsername()) + "");
		player.getPacketSender().sendString(3451, "" + Misc.formatPlayerName2(player2.getUsername()) + "");
		player.getPacketSender().sendString(3417, "Trading with: " + Misc.formatPlayerName2(player2.getUsername()) + "");
		player.getPacketSender().sendString(3431, "");
		player.getPacketSender().sendString(3535, "Are you sure you want to make this trade?");
		/**
		 * Trade wealth
		 */
		if(tradeWealthUpdate) {
			tradeWealth = 0;
			player2.getTrading().tradeWealth = 0;
			for (Item tradedItem : offeredItems) {
				tradeWealth += ((tradedItem.sellable() ? tradedItem.getDefinition().getValue() : 0) * tradedItem.getAmount());
			}
			for (Item tradedItem : player2.getTrading().offeredItems)
				player2.getTrading().tradeWealth += ((tradedItem.sellable() ? tradedItem.getDefinition().getValue() : 0) * tradedItem.getAmount());
			int playerDifference1 = (tradeWealth - player2.getTrading().tradeWealth);
			int playerDifference2 = (player2.getTrading().tradeWealth - tradeWealth);
			boolean player1HasMore = (playerDifference1 > playerDifference2);
			boolean equalsSame = (tradeWealth == player2.getTrading().tradeWealth);
			if (tradeWealth < -1)
				tradeWealth = 2147483647;
			if (player2.getTrading().tradeWealth < -1)
				player2.getTrading().tradeWealth = 2147483647;
			String playerValue1 = ""+ Misc.getTotalAmount(tradeWealth) + " ("+ Misc.format(tradeWealth) + ")";
			String playerValue2 = ""+ Misc.getTotalAmount(player2.getTrading().tradeWealth) + " ("+ Misc.format(player2.getTrading().tradeWealth) + ")";
			if (tradeWealth < -1)
				playerValue1 = "+" + playerValue1;
			if (player2.getTrading().tradeWealth < -1)
				playerValue2 = "+" + playerValue2;
			if(inTrade() && player2.getTrading().inTrade()) {
				if(equalsSame) {
					player.getPacketSender().sendString(12504, "@yel@Equal trade");
					player2.getPacketSender().sendString(12504, "@yel@Equal trade");
				} else if(player1HasMore) {
					player.getPacketSender().sendString(12504, "-@red@" + Misc.getTotalAmount(playerDifference1) + " (" + Misc.format(playerDifference1) + ")");
					player2.getPacketSender().sendString(12504, "+@yel@" + Misc.getTotalAmount(playerDifference1)+ " (" + Misc.format(playerDifference1) + ")");
				} else if(!player1HasMore) {
					player.getPacketSender().sendString(12504,"+@yel@" + Misc.getTotalAmount(playerDifference2)+ " (" + Misc.format(playerDifference2) + ")");
					player2.getPacketSender().sendString(12504, "-@red@" + Misc.getTotalAmount(playerDifference2)+ " (" + Misc.format(playerDifference2) + ")");
				}
			}
			player.getPacketSender().sendString(12506, playerValue1);
			player.getPacketSender().sendString(12507, playerValue2);
			player.getPacketSender().sendString(12505, Misc.formatText(player2.getUsername()) + " has\\n "+ player2.getInventory().getFreeSlots() + " free\\n inventory slots.");
			player.getPacketSender().sendInterfaceSet(3323, 3321);
			player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
			player2.getPacketSender().sendString(12506, playerValue2);
			player2.getPacketSender().sendString(12507, playerValue1);
			player2.getPacketSender().sendString(12505, Misc.formatText(player.getUsername()) + " has\\n "+ player.getInventory().getFreeSlots()+ " free\\n inventory slots.");
		}
	}

	public void tradeItem(int itemId, int amount, int slot) {
		if(slot < 0)
			return;
		if(!getCanOffer())
			return;
		Player player2 = World.getPlayers().get(getTradeWith());
		if(player2 == null || player == null)
			return;
		if(player.getRights() == PlayerRights.ADMINISTRATOR) {
			player.getPacketSender().sendMessage("Administrators cannot trade items.");
			return;
		}
		if(player.getAttributes().getNewPlayerDelay() > 0 && player.getRights().ordinal() == 0) {
			player.getPacketSender().sendMessage("You must wait another "+player.getAttributes().getNewPlayerDelay() / 60+" minutes before being able to trade items.");
			return;
		}
		if(player.getRights() != PlayerRights.DEVELOPER) {
			if (!new Item(itemId).tradeable() || ItemLending.borrowedItem(player, itemId)) {
				player.getPacketSender().sendMessage("This item is currently untradeable and cannot be traded.");
				return;
			}
			if (ItemLending.borrowedItem(player, itemId)) {
				player.getPacketSender().sendMessage("This item is untradeable and cannot be traded.");
				return;
			}
		}
		falseTradeConfirm();
		player.getPacketSender().sendClientRightClickRemoval();
		if(!inTrade() || !canOffer) {
			declineTrade(true);
			return;
		}
		if(!player.getInventory().contains(itemId))
			return;
		if(slot >= player.getInventory().capacity() || player.getInventory().getItems()[slot].getId() != itemId || player.getInventory().getItems()[slot].getAmount() <= 0)
			return;
		Item itemToTrade = player.getInventory().getItems()[slot];
		if (player.getInventory().getAmount(itemId) < amount) {
			amount = player.getInventory().getAmount(itemId);
			if (amount == 0 || player.getInventory().getAmount(itemId) < amount) {
				return;
			}
		}
		if (!itemToTrade.getDefinition().isStackable()) {
			for (int a = 0; a < amount && a < 28; a++) {
				if (player.getInventory().getAmount(itemId) >= 1) {
					offeredItems.add(new Item(itemId, 1));
					player.getInventory().delete(itemId, 1);
				}
			}
		} else
			if (itemToTrade.getDefinition().isStackable()) {
				boolean itemInTrade = false;
				for (Item item : offeredItems) {
					if (item.getId() == itemId) {
						itemInTrade = true;
						item.setAmount(item.getAmount() + amount);
						player.getInventory().delete(itemId, amount);
						break;
					}
				}
				if (!itemInTrade) {
					offeredItems.add(new Item(itemId, amount));
					player.getInventory().delete(itemId, amount);
				}
			}
		Logger.log(player.getUsername(), "Trade item: "+itemToTrade.getDefinition().getName()+", noted: "+itemToTrade.getDefinition().isNoted()+", amount: "+Misc.insertCommasToNumber(String.valueOf(amount))+". Trade partner: "+player2.getUsername());
		player.getInventory().refreshItems();
		player.getPacketSender().sendInterfaceItems(3416, player2.getTrading().offeredItems);
		player.getPacketSender().sendInterfaceItems(3415, offeredItems);
		player.getPacketSender().sendString(3431, "");
		acceptedTrade = false;
		tradeConfirmed = false;
		tradeConfirmed2 = false;
		player2.getPacketSender().sendInterfaceItems(3416, offeredItems);
		player2.getPacketSender().sendString(3431, "");
		player2.getTrading().acceptedTrade = false;
		player2.getTrading().tradeConfirmed = false;
		player2.getTrading().tradeConfirmed2 = false;
		sendText(player2, true);
	}

	public void removeTradedItem(int itemId, int amount) {
		if(!getCanOffer())
			return;
		Player player2 = World.getPlayers().get(getTradeWith());
		if(player2 == null)
			return;
		if(!inTrade() || !canOffer) {
			declineTrade(false);
			return;
		}
		falseTradeConfirm();
		ItemDefinition def = ItemDefinition.forId(itemId);
		if (!def.isStackable()) {
			if (amount > 28)
				amount = 28;
			for (int a = 0; a < amount; a++) {
				for (Item item : offeredItems) {
					if (item.getId() == itemId) {
						if (!item.getDefinition().isStackable()) {
							offeredItems.remove(item);
							player.getInventory().add(itemId, 1);
						}
						break;
					}
				}
			}
		} else
			for (Item item : offeredItems) {
				if (item.getId() == itemId) {
					if (item.getDefinition().isStackable()) {
						if (item.getAmount() > amount) {
							item.setAmount(item.getAmount() - amount);
							player.getInventory().add(itemId, amount);
						} else {
							amount = item.getAmount();
							offeredItems.remove(item);
							player.getInventory().add(itemId, amount);
						}
					}
					break;
				}
			}
		Logger.log(player.getUsername(), "Removed item in trade: "+def.getName()+", noted: "+def.isNoted()+", amount: "+Misc.insertCommasToNumber(String.valueOf(amount))+". Trade partner: "+player2.getUsername());
		falseTradeConfirm();
		player.getInventory().refreshItems();
		player.getPacketSender().sendInterfaceItems(3416, player2.getTrading().offeredItems);
		player.getPacketSender().sendInterfaceItems(3415, offeredItems);
		player2.getPacketSender().sendInterfaceItems(3416, offeredItems);
		sendText(player2, true);
		player.getPacketSender().sendString(3431, "");
		player2.getPacketSender().sendString(3431, "");
		player.getPacketSender().sendClientRightClickRemoval();
	}

	public void acceptTrade(int stage) {
		if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 1000)
			return;
		if(getTradeWith() < 0) {
			declineTrade(false);
			return;
		}
		Player player2 = World.getPlayers().get(getTradeWith());
		if(player == null || player2 == null) {
			declineTrade(false);
			return;
		}
		if(!twoTraders(player, player2)) {
			player.getPacketSender().sendMessage("An error has occured. Please try re-trading the player.");
			return;
		}
		if(stage == 2) {
			if(!inTrade() || !player2.getTrading().inTrade() || !player2.getTrading().tradeConfirmed) {
				declineTrade(true);
				return;
			}
			if(!tradeConfirmed)
				return;
			acceptedTrade = true;
			tradeConfirmed2 = true;
			player2.getPacketSender().sendString(3535, "Other player has accepted.");
			player.getPacketSender().sendString(3535, "Waiting for other player...");
			if (inTrade() && player2.getTrading().tradeConfirmed2) {
				acceptedTrade = true;
				giveItems();
				player.getPacketSender().sendMessage("Trade accepted.");
				player2.getTrading().acceptedTrade = true;
				player2.getTrading().giveItems();
				player2.getPacketSender().sendMessage("Trade accepted.");
				resetTrade();
				player2.getTrading().resetTrade();
			}
		} else if(stage == 1) {
			player2.getTrading().goodTrade = true;
			player2.getPacketSender().sendString(3431, "Other player has accepted.");
			goodTrade = true;
			player.getPacketSender().sendString(3431, "Waiting for other player...");
			tradeConfirmed = true;
			if (inTrade() && player2.getTrading().tradeConfirmed && player2.getTrading().goodTrade && goodTrade) {
				confirmScreen();
				player2.getTrading().confirmScreen();
			}
		}
		player.getAttributes().setClickDelay(System.currentTimeMillis()); 
	}

	public void confirmScreen() {
		Player player2 = World.getPlayers().get(getTradeWith());
		if (player2 == null)
			return;
		setCanOffer(false);
		player.getInventory().refreshItems();
		String SendTrade = "Absolutely nothing!";
		String SendAmount;
		int Count = 0;
		if(getItemLending().loanState > 1) {
			if(getItemLending().temporarLendItem != null) {
				SendTrade = "[LEND: "+getItemLending().hours+"H] " + getItemLending().temporarLendItem.getDefinition().getName().replaceAll("_", " ");
				Count = 1;
			}
		}
		for (Item item : offeredItems) {
			if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
				SendAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Misc.format(item.getAmount()) + ")";
			} else if (item.getAmount() >= 1000000) {
				SendAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + Misc.format(item.getAmount()) + ")";
			} else {
				SendAmount = "" + Misc.format(item.getAmount());
			}
			if (Count == 0) {
				SendTrade = item.getDefinition().getName().replaceAll("_", " ");
			} else
				SendTrade = SendTrade + "\\n" + item.getDefinition().getName().replaceAll("_", " ");
			if (item.getDefinition().isStackable())
				SendTrade = SendTrade + " x " + SendAmount;
			Count++;
		}

		player.getPacketSender().sendString(3557, SendTrade);
		SendTrade = "Absolutely nothing!";
		SendAmount = "";
		Count = 0;
		if(player2.getTrading().getItemLending().loanState > 1) {
			if(player2.getTrading().getItemLending().temporarLendItem != null) {
				SendTrade = "[LEND: "+player2.getTrading().getItemLending().hours+"H] " + player2.getTrading().getItemLending().temporarLendItem.getDefinition().getName().replaceAll("_", " ");
				Count = 1;
			}
		}
		for (Item item : player2.getTrading().offeredItems) {
			if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
				SendAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Misc.format(item.getAmount()) + ")";
			} else if (item.getAmount() >= 1000000) {
				SendAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + Misc.format(item.getAmount()) + ")";
			} else {
				SendAmount = "" + Misc.format(item.getAmount());
			}
			if (Count == 0) {
				SendTrade = item.getDefinition().getName().replaceAll("_", " ");
			} else
				SendTrade = SendTrade + "\\n" + item.getDefinition().getName().replaceAll("_", " ");
			if (item.getDefinition().isStackable())
				SendTrade = SendTrade + " x " + SendAmount;
			Count++;
		}
		player.getPacketSender().sendString(3558, SendTrade);
		/*
		 * Remove all tabs!
		 */
		player.getPacketSender().sendInterfaceSet(3443, Inventory.INTERFACE_ID);
		player.getPacketSender().sendItemContainer(player.getInventory(), Inventory.INTERFACE_ID);
	}

	public void giveItems() {
		Player player2 = World.getPlayers().get(getTradeWith());
		if (player2 == null)
			return;
		if(!inTrade() || !player2.getTrading().inTrade())
			return;
		try {
			for (Item item : player2.getTrading().offeredItems) {
				player.getInventory().add(item);
				Logger.log(player.getUsername(), "Received item from finished trade: "+item.getDefinition().getName()+" x"+Misc.insertCommasToNumber(String.valueOf(item.getAmount()))+" from trade partner: "+player2.getUsername());

			}
			if(player2.getTrading().getItemLending().loanState > 1) {
				if(player2.getTrading().getItemLending().temporarLendItem != null) {
					LendedItem item = new LendedItem(player2.getTrading().getItemLending().temporarLendItem.getId());
					item.setItemOwner(player2.getUsername()); 
					item.setItemLoaner(player.getUsername()); 
					item.setStartMilliS(System.currentTimeMillis()); 
					item.setReturnMilliS(3600000 * player2.getTrading().getItemLending().hours);
					ItemLending.lendedItems.add(item);
					player2.getTrading().getItemLending().setLentItem(item);
					getItemLending().setBorrowedItem(item);
					player.getInventory().add(ItemLending.getLendItem(getItemLending().getBorrowedItem().getId()), 1);
					player2.getTrading().getItemLending().temporarLendItem = null;
					player2.getTrading().getItemLending().loanState = 0;
				}
			}


		} catch (Exception ignored) {
		}
	}

	public void resetTrade() {
		inTradeWith = -1;
		offeredItems.clear();
		setCanOffer(true);
		setTrading(false);
		setTradeWith(-1);
		setTradeStatus(0);
		lastTradeSent = 0;
		acceptedTrade = false;
		tradeConfirmed = false;
		tradeConfirmed2 = false;
		tradeRequested = false;
		canOffer = true;
		goodTrade = false;
		player.getPacketSender().sendString(3535, "Are you sure you want to make this trade?");
		player.getPacketSender().sendInterfaceRemoval();
		getItemLending().refreshLoanBoxes();
		getItemLending().temporarLendItem = null;
		getItemLending().loanState = 0;
		player.getPacketSender().sendInterfaceRemoval();
	}


	private boolean falseTradeConfirm() { //Thanks to Arrowzflame for this! :) /Gabbe <3
		Player player2 = World.getPlayers().get(getTradeWith());
		return tradeConfirmed = player2.getTrading().tradeConfirmed = false;
	}

	public CopyOnWriteArrayList<Item> offeredItems = new CopyOnWriteArrayList<Item>();
	private boolean inTrade = false;
	private boolean tradeRequested = false;
	private int tradeWith = -1;
	private int tradeStatus = 0;
	public int tradeWealth;
	public long lastTradeSent = 0;
	private boolean canOffer = true;
	public boolean tradeConfirmed = false;
	public boolean tradeConfirmed2 = false;
	public boolean acceptedTrade = false;
	public boolean goodTrade = false;

	public void setTrading(boolean trading) {
		this.inTrade = trading;
	}

	public boolean inTrade() {
		return this.inTrade;
	}

	public void setTradeRequested(boolean tradeRequested) {
		this.tradeRequested = tradeRequested;
	}

	public boolean tradeRequested() {
		return this.tradeRequested;
	}

	public void setTradeWith(int tradeWith) {
		this.tradeWith = tradeWith;
	}

	public int getTradeWith() {
		return this.tradeWith;
	}

	public void setTradeStatus(int status) {
		this.tradeStatus = status;
	}

	public int getTradeStatus() {
		return this.tradeStatus;
	}

	public void setCanOffer(boolean canOffer) {
		this.canOffer = canOffer;
	}

	public boolean getCanOffer() {
		return this.canOffer;
	}

	public ItemLending getItemLending() {
		return itemLending;
	}

	public int inTradeWith = -1;

	/**
	 * Checks if two players are the only ones in a trade.
	 * @param p1	Player1 to check if he's 1/2 player in trade.
	 * @param p2	Player2 to check if he's 2/2 player in trade.
	 * @return		true if only two people are in the trade.
	 */
	public static boolean twoTraders(Player p1, Player p2) {
		int count = 0;
		for(Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if(player.getTrading().inTradeWith == p1.getIndex() || player.getTrading().inTradeWith == p2.getIndex()) {
				count++;
			}
		}
		return count == 2;
	}

	/**
	 * The trade interface id.
	 */
	public static final int INTERFACE_ID = 3322;

	/**
	 * The trade interface id for removing items.
	 */
	public static final int INTERFACE_REMOVAL_ID = 3415;

}
