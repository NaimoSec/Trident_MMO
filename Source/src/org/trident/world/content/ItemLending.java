package org.trident.world.content;

import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.model.Flag;
import org.trident.model.Item;
import org.trident.model.LendedItem;
import org.trident.model.container.impl.Bank;
import org.trident.model.definitions.ItemDefinition;
import org.trident.model.movement.MovementStatus;
import org.trident.world.World;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;
import org.trident.world.entity.player.PlayerSaving;

/**
 * Handles Item lending
 * @author Gabbe
 * SAVING/PROCESSING IS DONE HORRIBLY HORRIBLY WRONG. xD
 *
 */
public class ItemLending {

	Player player;
	public ItemLending(Player p) {
		this.player = p;
	}

	/**
	 * Handles the action when loaning items in a trade.
	 * @param id	The item to handle the action for.
	 */
	public void loanItemAction(int id) {
		if(!player.getTrading().inTrade())
			return;
		if(!player.getInventory().contains(id)) {
			return;
		}
		if(temporarLendItem != null && loanState > 1) {
			player.getPacketSender().sendMessage("You can only loan out one item at a time.");
			return;
		}
		if(getLentItem() != null) {
			player.getPacketSender().sendMessage("You have already lent out an item in the past 24 hours.");
			return;
		}
		if(borrowedItem(player, id)) {
			player.getPacketSender().sendMessage("You cannot lend an item which isn't yours.");
			return;
		}
		if(!lendable(id)) {
			player.getPacketSender().sendMessage("This item is currently not available for lending. Please suggest it on the forums.");
			return;
		}
		if(!player.getUsername().equalsIgnoreCase("gabbe")) {
			player.getPacketSender().sendMessage("Currently disabled.");
			return;
		}
		player.getPacketSender().sendEnterAmountPrompt("Enter amount of hours:");
		temporarLendItem = new Item(id);
		loanState = 1;
	}

	/**
	 * Sends the previously selected (to be) loan item into the trade interface.
	 * @param hours	The hours of the loan to display in the trade.
	 */
	public void loanItemToTrade(int hours) {
		if(!player.getTrading().inTrade() || temporarLendItem == null)
			return;
		Player player2 = player.getTrading().getTradeWith() > 0 ? World.getPlayers().get(player.getTrading().getTradeWith()) : null;
		if(player2 == null)
			return;
		player.getInventory().delete(temporarLendItem);
		player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
		loanState = 2;
		this.hours = hours;
		refreshLoanBoxes();
		refreshTrade(player2);
	}

	/**
	 * Refreshes the trade interface for 2 players and removes the 'waiting for other player...'
	 * @param player2	The second player to reset the interface for
	 */
	public void refreshTrade(Player player2) {
		player.getInventory().refreshItems();
		player.getPacketSender().sendInterfaceItems(3416, player2.getTrading().offeredItems);
		player.getPacketSender().sendInterfaceItems(3415, player.getTrading().offeredItems);
		player.getPacketSender().sendString(3431, "");
		player.getTrading().acceptedTrade = false;
		player.getTrading().tradeConfirmed = false;
		player.getTrading().tradeConfirmed2 = false;
		player2.getPacketSender().sendInterfaceItems(3416, player.getTrading().offeredItems);
		player2.getPacketSender().sendString(3431, "");
		player2.getTrading().acceptedTrade = false;
		player2.getTrading().tradeConfirmed = false;
		player2.getTrading().tradeConfirmed2 = false;
		player.getTrading().sendText(player2, true);
	}

	/**
	 * Refreshes the loan boxes in a trade
	 */
	public void refreshLoanBoxes() {
		Player player2 = player.getTrading().getTradeWith() > 0 ? World.getPlayers().get(player.getTrading().getTradeWith()) : null;
		if(!player.getTrading().inTrade())
			temporarLendItem = null;
		player.getPacketSender().sendString(3456, "");
		player.getPacketSender().sendString(3457, "");
		if(player2 == null) {
			if(temporarLendItem != null) {
				player.getPacketSender().sendItemOnInterface(3454, temporarLendItem.getId(), 1);
				player.getPacketSender().sendString(3456, "Hours: "+hours);
			} else
				player.getPacketSender().sendItemOnInterface(3454, -1, 1);
			player.getPacketSender().sendItemOnInterface(3455, -1, 1);
			return;
		} else {
			player2.getPacketSender().sendString(3456, "");
			player2.getPacketSender().sendString(3457, "");
		}
		/**
		 * Refresh loan boxes for player1
		 */
		if(player.getTrading().getItemLending().temporarLendItem != null) {
			player.getPacketSender().sendItemOnInterface(3454, player.getTrading().getItemLending().temporarLendItem.getId(), 1);
			player.getPacketSender().sendString(3456, "Hours: "+hours);
		} else
			player.getPacketSender().sendItemOnInterface(3454, -1, 1);
		if(player2.getTrading().getItemLending().temporarLendItem != null) {
			player.getPacketSender().sendItemOnInterface(3455, player2.getTrading().getItemLending().temporarLendItem.getId(), 1);
			player.getPacketSender().sendString(3457, "Hours: "+player2.getTrading().getItemLending().hours);
		} else
			player.getPacketSender().sendItemOnInterface(3455, -1, 1);

		/**
		 * Refresh loan boxes for player2
		 */
		if(player2.getTrading().getItemLending().temporarLendItem != null) {
			player2.getPacketSender().sendItemOnInterface(3454, player2.getTrading().getItemLending().temporarLendItem.getId(), 1);
			player2.getPacketSender().sendString(3456, "Hours: "+player2.getTrading().getItemLending().hours);
		} else
			player2.getPacketSender().sendItemOnInterface(3454, -1, 1);
		if(player.getTrading().getItemLending().temporarLendItem != null) {
			player2.getPacketSender().sendItemOnInterface(3455, player.getTrading().getItemLending().temporarLendItem.getId(), 1);
			player2.getPacketSender().sendString(3457, "Hours: "+player.getTrading().getItemLending().hours);
		} else
			player2.getPacketSender().sendItemOnInterface(3455, -1, 1);
	}

	/**
	 * Returns the borrowed item to the owner and deletes it from the player.
	 * @param lendedItem		The item which is going to be removed/added.
	 */
	public static void returnBorrowedItem(LendedItem lendedItem) {
		Player itemLoaner = PlayerHandler.getPlayerForName(lendedItem.getItemLoaner());
		if(itemLoaner != null)
			removeItem(itemLoaner);
		Player itemOwner = PlayerHandler.getPlayerForName(lendedItem.getItemOwner());
		if(itemOwner != null) {
			if(!lendedItem.itemHasReturned()) {
				int bankTo = Bank.getTabForItem(itemOwner, lendedItem.getId());
				itemOwner.getBank(bankTo).add(new Item(lendedItem.getId(), 1)).refreshItems();
				itemOwner.getTrading().getItemLending().resetLend();
				lendedItem.setItemReturned(true);
			}
		}
		lendedItems.remove(lendedItem);
	}

	/**
	 * Removes the borrowed item from a player.
	 * @param itemLoaner	The player to remove the item for
	 */
	public static void removeItem(Player itemLoaner) {
		LendedItem lendedItem = itemLoaner.getTrading().getItemLending().getBorrowedItem();
		if(lendedItem == null)
			return;
		Item item2 = new Item(getLendItem(lendedItem.getId()), 1);
		int bankFrom = Bank.getTabForItem(itemLoaner, ItemLending.getLendItem(lendedItem.getId()));
		for(Item item : itemLoaner.getInventory().getItems()) {
			if(item != null) {
				if(borrowedItem(itemLoaner, item.getId())) {
					itemLoaner.getInventory().delete(item2);
					itemLoaner.getInventory().refreshItems();
					itemLoaner.getTrading().getItemLending().resetLoan();
					return;
				}
			}
		}
		for(Item item : itemLoaner.getEquipment().getItems()) {
			if(item != null) {
				if(borrowedItem(itemLoaner, item.getId())) {
					itemLoaner.getEquipment().delete(item2);
					itemLoaner.getEquipment().refreshItems();
					itemLoaner.getTrading().getItemLending().resetLoan();
					itemLoaner.getUpdateFlag().flag(Flag.APPEARANCE);
					return;
				}
			}
		}
		for(int i = 0; i < 9; i++) {
			for(Item item : itemLoaner.getBank(i).getItems()) {
				if(item != null) {
					if(borrowedItem(itemLoaner, item.getId())) {
						itemLoaner.getBank(bankFrom).delete(item2);
						itemLoaner.getBank(bankFrom).refreshItems();
						itemLoaner.getTrading().getItemLending().resetLoan();
						return;
					}
				}
			}
		}
	}

	/**
	 * Resets a player's lending
	 */
	public void resetLend() {
		setLentItem(null);
		loanState = 0;
		hours = 0;
		PlayerSaving.save(player);
		player.getPacketSender().sendMessage("Your lent item has returned to your bank.");
	}

	/**
	 * Resets a player's loan
	 */
	public void resetLoan() {
		setBorrowedItem(null);
		loanState = 0;
		hours = 0;
		PlayerSaving.save(player);
		player.getPacketSender().sendMessage("Your borrowed item has returned to its owner.");
	}

	public int hours = 0;
	public int loanState = 0;

	private LendedItem borrowedItem = null;

	public LendedItem getBorrowedItem() {
		return borrowedItem;
	}

	public Item temporarLendItem = null;

	public void setBorrowedItem(LendedItem borrowedItem) {
		this.borrowedItem = borrowedItem;
	}

	private LendedItem lentItem = null;

	public LendedItem getLentItem() {
		return lentItem;
	}

	public void setLentItem(LendedItem lentItem) {
		this.lentItem = lentItem;
	}

	/**
	 * SAVING
	 */

	public Dialogue discardBorrowedItem(final Player player) {
		return new Dialogue() {
			@Override
			public DialogueType type() {
				return DialogueType.ITEM_STATEMENT;
			}

			@Override
			public String[] item() {
				return new String[]{"11694", "200", "Discard"};
			}

			@Override
			public Dialogue nextDialogue() {
				return DialogueManager.getDialogues().get(281);
			}

			@Override
			public String[] dialogue() {
				return new String[] {
						"Loan expires in: minutes.",
						"If you discard this item, it will return to its owner.",
						"You won't be able to use it again unless you borrow it again."
				};
			}

			@Override
			public void specialAction() {

			}

			@Override
			public DialogueExpression animation() {
				return null;
			}
		};
	}

	public static int getLendItem(int rawItem) {
		for(int i = 0; i < lendItems.length; i++) {
			if(rawItem == lendItems[i][0])
				return lendItems[i][1];
		}
		return -1;
	}

	public static int getRawItem(int lendItem) {
		for(int i = 0; i < lendItems.length; i++) {
			if(lendItem == lendItems[i][1])
				return lendItems[i][0];
		}
		return -1;
	}

	public static boolean borrowedItem(Player player, int item) {
		if(player.getTrading().getItemLending().getBorrowedItem() != null && player.getTrading().getItemLending().getBorrowedItem().getId() == getRawItem(item))
			return true;
		return false;
	}

	public static boolean lendable(int item) {
		if(ItemDefinition.forId(item).isStackable()) 
			return false;
		for(int i = 0; i < lendItems.length; i++) {
			if(item == lendItems[i][0])
				return true;
		}
		return false;
	}

	/**
	 * Makes the player borrow an item from the server.
	 * @param p			The player to borrow an item from the server.
	 * @param item		The item to borrow.
	 * @param itemToAdd	The item to be added in inventory.
	 */
	public static void lendItemToPlayer(Player p, int item, int itemToAdd) {
		if(item != -1) {
			LendedItem borrowedItem = new LendedItem(item); borrowedItem.setAmount(1); 
			borrowedItem.setItemOwner("Trident");
			borrowedItem.setItemLoaner(p.getUsername()); 
			borrowedItem.setStartMilliS(System.currentTimeMillis());
			borrowedItem.setReturnMilliS(3600000 * 24);
			ItemLending.lendedItems.add(borrowedItem);
			p.getTrading().getItemLending().setBorrowedItem(borrowedItem);
			p.getInventory().add(itemToAdd, 1);
		}
		p.getPacketSender().sendInterfaceRemoval();
		p.getMovementQueue().setMovementStatus(MovementStatus.NONE);
	}
	
	public static final int[][] lendItems = 
		{  {11694, 13450},
		{4151, 13444},
		{11235, 13405},
		{15486, 15502},
		{13661, 13661}
		};

	/**
	 * Lended Items Processing, Saving and Handling.
	 */
	public static CopyOnWriteArrayList<LendedItem> lendedItems = new CopyOnWriteArrayList<LendedItem>();

	public static void processLendedItems() {
		for (LendedItem item : lendedItems) {
			if(item == null || item.itemHasReturned()) {
				lendedItems.remove(item);
				return;
			} else
				if (System.currentTimeMillis() - item.getStartMilliS() 
						>= item.getReturnMilliS()) {
					ItemLending.returnBorrowedItem(item);
					lendedItems.remove(item);
				}
		}
	}
}
