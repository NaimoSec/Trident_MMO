package org.trident.model.container.impl;

import org.trident.model.Flag;
import org.trident.model.Item;
import org.trident.model.container.ItemContainer;
import org.trident.model.container.StackType;
import org.trident.model.definitions.ItemDefinition;
import org.trident.world.content.BankPin;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.entity.player.Player;

/**
 * 100% safe Bank System
 * @author Gabbe
 */

public class Bank extends ItemContainer {

	public Bank(Player player) {
		super(player);
	}

	public Bank open() {
		getPlayer().getPacketSender().sendClientRightClickRemoval();
		if(getPlayer().getAttributes().getBankPinAttributes().hasBankPin() && !getPlayer().getAttributes().getBankPinAttributes().hasEnteredBankPin()) {
			BankPin.init(getPlayer());
			return this;
		}
		getPlayer().getAttributes().setBanking(true).setInputHandling(null);
		sortItems().refreshItems();
		if(getPlayer().getAttributes().withdrawAsNote())
			getPlayer().getPacketSender().sendConfig(115, 1);
		else
			getPlayer().getPacketSender().sendConfig(115, 0);
		if(getPlayer().getAttributes().getBankSearchingAttribtues().isSearchingBank() && getPlayer().getAttributes().getBankSearchingAttribtues().getSearchedBank() != null)
			getPlayer().getPacketSender().sendConfig(117, 1);
		else
			getPlayer().getPacketSender().sendConfig(117, 0).sendString(5383, "Bank account for: "+getPlayer().getUsername()+"");
			getPlayer().getPacketSender().sendInterfaceSet(5292, 5063);
			return this;
	}

	@Override
	public Bank switchItem(ItemContainer to, Item item, int slot, boolean sort, boolean refresh) {
		if(!getPlayer().getAttributes().isBanking() || getPlayer().getAttributes().getInterfaceId() != 5292 || to instanceof Inventory && !(getPlayer().getBank(getPlayer().getAttributes().getCurrentBankTab()).contains(item.getId()) || getPlayer().getAttributes().getBankSearchingAttribtues().getSearchedBank() != null && getPlayer().getAttributes().getBankSearchingAttribtues().getSearchedBank().contains(item.getId()))) {
			getPlayer().getPacketSender().sendClientRightClickRemoval();
			return this;
		}
		ItemDefinition def = ItemDefinition.forId(item.getId() + 1);
		if (to.getFreeSlots() <= 0 && (!(to.contains(item.getId()) && item.getDefinition().isStackable())) && !(getPlayer().getAttributes().withdrawAsNote() && def != null && def.isNoted() && to.contains(def.getId()))) {
			to.full();
			return this;
		}
		if(item.getAmount() > to.getFreeSlots() && !item.getDefinition().isStackable()) {
			if(to instanceof Inventory) {
				if (getPlayer().getAttributes().withdrawAsNote()) {
					if (def == null || !def.isNoted())
						item.setAmount(to.getFreeSlots());
				} else
					item.setAmount(to.getFreeSlots());
			}
		}
		if(getPlayer().getAttributes().getBankSearchingAttribtues().isSearchingBank() && getPlayer().getAttributes().getBankSearchingAttribtues().getSearchedBank() != null) {
			int tab = Bank.getTabForItem(getPlayer(), item.getId());
			if(!getPlayer().getBank(tab).contains(item.getId()) || !getPlayer().getAttributes().getBankSearchingAttribtues().getSearchedBank().contains(item.getId()))
				return this;
			if(item.getAmount() > getPlayer().getBank(tab).getAmount(item.getId()))
				item.setAmount(getPlayer().getBank(tab).getAmount(item.getId()));
			if(item.getAmount() <= 0)
				return this;
			getPlayer().getBank(tab).delete(item);
			getPlayer().getAttributes().getBankSearchingAttribtues().getSearchedBank().delete(item);
			getPlayer().getAttributes().getBankSearchingAttribtues().getSearchedBank().open();
		} else {
			if (getItems()[slot].getId() != item.getId() || !contains(item.getId()))
				return this;
			if(item.getAmount() > getAmount(item.getId()))
				item.setAmount(getAmount(item.getId()));
			if(item.getAmount() <= 0)
				return this;
			delete(item, slot, refresh, to);
		}
		if (getPlayer().getAttributes().withdrawAsNote()) {
			if (def != null && def.isNoted() && item.getDefinition() != null && def.getName().equalsIgnoreCase(item.getDefinition().getName()) && !def.getName().contains("Torva") && !def.getName().contains("Virtus") && !def.getName().contains("Pernix") && !def.getName().contains("Torva"))
				item.setId(item.getId() + 1);
			else
				getPlayer().getPacketSender().sendMessage("This item cannot be withdrawn as a note.");
		}
		to.add(item, refresh);
		if (sort && getAmount(item.getId()) <= 0)
			sortItems();
		if (refresh) {
			refreshItems();
			to.refreshItems();
		}
		return this;
	}

	@Override
	public int capacity() {
		return 352;
	}

	@Override
	public StackType stackType() {
		return StackType.STACKS;
	}

	@Override
	public Bank refreshItems() {
		Bank bank = getPlayer().getAttributes().getBankSearchingAttribtues().isSearchingBank() && getPlayer().getAttributes().getBankSearchingAttribtues().getSearchedBank() != null ? getPlayer().getAttributes().getBankSearchingAttribtues().getSearchedBank() : this;
		getPlayer().getPacketSender().sendString(22033, "" + bank.getValidItems().size());
		getPlayer().getPacketSender().sendString(22034, "" + bank.capacity());
		getPlayer().getPacketSender().sendItemContainer(bank, INTERFACE_ID);
		getPlayer().getPacketSender().sendItemContainer(getPlayer().getInventory(), INVENTORY_INTERFACE_ID);
		sendTabs(getPlayer());
		if(!getPlayer().getAttributes().isBanking() || getPlayer().getAttributes().getInterfaceId() != 5292)
			getPlayer().getPacketSender().sendClientRightClickRemoval();
		return this;
	}

	@Override
	public Bank full() {
		getPlayer().getPacketSender().sendMessage("Not enough space in bank.");
		return this;
	}

	public static void sendTabs(Player player) {
		boolean moveRest = false;
		if(isEmpty(player.getBank(1))) { //tab 1 empty
			player.setBank(1, player.getBank(2));
			player.setBank(2, new Bank(player));
			moveRest = true;
		}
		if(isEmpty(player.getBank(2)) || moveRest) { 
			player.setBank(2, player.getBank(3));
			player.setBank(3, new Bank(player));
			moveRest = true;
		}
		if(isEmpty(player.getBank(3)) || moveRest) { 
			player.setBank(3, player.getBank(4));
			player.setBank(4, new Bank(player));
			moveRest = true;
		}
		if(isEmpty(player.getBank(4)) || moveRest) { 
			player.setBank(4, player.getBank(5));
			player.setBank(5, new Bank(player));
			moveRest = true;
		}
		if(isEmpty(player.getBank(5)) || moveRest) { 
			player.setBank(5, player.getBank(6));
			player.setBank(6, new Bank(player));
			moveRest = true;
		}
		if(isEmpty(player.getBank(6)) || moveRest) { 
			player.setBank(6, player.getBank(7));
			player.setBank(7, new Bank(player));
			moveRest = true;
		}
		if(isEmpty(player.getBank(7)) || moveRest) { 
			player.setBank(7, player.getBank(8));
			player.setBank(8, new Bank(player));
		}
		/*boolean moveRest = false;
		for(int i = 1; i <= 7; i++) {
			if(isEmpty(player.getBank(i)) || moveRest) {
				int j = i+2 > 8 ? 8 : i+2;
				player.setBank(i, player.getBank(j));
				player.setBank(j, new Bank(player));
				moveRest = true;
			}
		}*/
		int tabs = getTabCount(player);
		if(player.getAttributes().getCurrentBankTab() > tabs)
			player.getAttributes().setCurrentBankTab(tabs);
		player.getPacketSender().sendString(27001, Integer.toString(tabs)).sendString(27002, Integer.toString(player.getAttributes().getCurrentBankTab()));
		int l = 1;
		for(int i = 22035; i < 22043; i++) {
			player.getPacketSender().sendItemOnInterface(i, getInterfaceModel(player.getBank(l)), 0, 1);
			l++;
		}
		player.getPacketSender().sendString(27000, "1");
	}

	public static void depositItems(Player p, ItemContainer from, boolean ignoreReqs) {
		if(!ignoreReqs)
			if(!p.getAttributes().isBanking() || p.getAttributes().getInterfaceId() != 5292)
				return;
		for (Item it : from.getValidItems()) {
			if(p.getBank(p.getAttributes().getCurrentBankTab()).getFreeSlots() <= 0 && !(p.getBank(p.getAttributes().getCurrentBankTab()).contains(it.getId()) && it.getDefinition().isStackable())) {
				p.getPacketSender().sendMessage("Bank full.");
				break;
			}
			Item toBank = new Item(ItemDefinition.forId(it.getId()).isNoted() ? (it.getId() - 1) : it.getId(), it.getAmount());
			int tab = getTabForItem(p, toBank.getId());
			p.getAttributes().setCurrentBankTab(tab);
			p.getBank(tab).add(toBank.copy());
			if(p.getAttributes().getBankSearchingAttribtues().isSearchingBank() && p.getAttributes().getBankSearchingAttribtues().getSearchedBank() != null)
				BankSearchAttributes.addItemToBankSearch(p, toBank);
			from.delete(it);
		}
		from.refreshItems();
		p.getBank(p.getAttributes().getCurrentBankTab()).sortItems().refreshItems();
		if(from instanceof Equipment) {
			WeaponHandler.update(p);
			p.getUpdateFlag().flag(Flag.APPEARANCE);
		}
	}

	public static boolean isEmpty(Bank bank)
	{
		return bank.sortItems().getValidItems().size() <= 0;
	}

	public static int getTabCount(Player player) {
		int tabs = 0;
		for(int i = 1; i < 9; i++) {
			if(!isEmpty(player.getBank(i))) {
				tabs++;
			} else
				break;
		}
		return tabs;
	}

	public static int getTabForItem(Player player, int itemID) {
		for(int k = 0; k < 9; k++) {
			Bank bank = player.getBank(k);
			for(int i = 0; i < bank.getValidItems().size(); i++) {
				if(bank.getItems()[i].getId() == itemID) {
					return k;
				}
			}
		}
		return player.getAttributes().getCurrentBankTab();
	}

	public static int getInterfaceModel(Bank bank) {
		if(bank.getItems()[0] == null || bank.getValidItems().size() == 0)
			return -1;
		int model = bank.getItems()[0].getId();
		int amount = bank.getItems()[0].getAmount();
		if (model == 995) {
			if (amount > 9999) {
				model = 1004;
			} else if (amount > 999) {
				model = 1003;
			} else if (amount > 249) {
				model = 1002;
			} else if (amount > 99) {
				model = 1001;
			}  else if (amount > 24) {
				model = 1000;
			} else if (amount > 4) {
				model = 999;
			} else if (amount > 3) {
				model = 998;
			} else if (amount > 2) {
				model = 997;
			} else if (amount > 1) {
				model = 996;
			}
		}
		return model;
	}

	/**
	 * The bank interface id.
	 */
	public static final int INTERFACE_ID = 5382;

	/**
	 * The bank inventory interface id.
	 */
	public static final int INVENTORY_INTERFACE_ID = 5064;

	/**
	 * The bank tab interfaces
	 */
	public static final int[][] BANK_TAB_INTERFACES = {{5, 0},{13, 1}, {26, 2}, {39, 3}, {52, 4}, {65, 5}, {78, 6}, {91, 7}, {104, 8}};
	/**
	 * The item id of the selected item in the 'bank X' option
	 */

	public static class BankSearchAttributes {

		private boolean searchingBank;
		private String searchSyntax;
		private Bank searchedBank;

		public boolean isSearchingBank() {
			return searchingBank;
		}

		public BankSearchAttributes setSearchingBank(boolean searchingBank) {
			this.searchingBank = searchingBank;
			return this;
		}

		public String getSearchSyntax() {
			return searchSyntax;
		}

		public BankSearchAttributes setSearchSyntax(String searchSyntax) {
			this.searchSyntax = searchSyntax;
			return this;
		}

		public Bank getSearchedBank() {
			return searchedBank;
		}

		public BankSearchAttributes setSearchedBank(Bank searchedBank) {
			this.searchedBank = searchedBank;
			return this;
		}

		public static void beginSearch(Player player, String searchSyntax) {
			player.getPacketSender().sendClientRightClickRemoval();
			searchSyntax = getFixedSyntax(searchSyntax);
			player.getPacketSender().sendString(5383, "Searching for: "+searchSyntax+"..");
			player.getAttributes().getBankSearchingAttribtues().setSearchingBank(true).setSearchSyntax(searchSyntax);
			player.getAttributes().setCurrentBankTab(0).setNoteWithdrawal(false);
			player.getPacketSender().sendString(27002, Integer.toString(player.getAttributes().getCurrentBankTab())).sendString(27000, "1");
			player.getAttributes().getBankSearchingAttribtues().setSearchedBank(new Bank(player));
			for(Bank bank : player.getBanks()) {
				bank.sortItems();
				for(Item bankedItem : bank.getValidItems())
					addItemToBankSearch(player, bankedItem);
			}
			player.getAttributes().getBankSearchingAttribtues().getSearchedBank().open();
			player.getPacketSender().sendString(5383, "Showing results found for: "+searchSyntax+"");
		}

		public static void addItemToBankSearch(Player player, Item item) {
			if(player.getAttributes().getBankSearchingAttribtues().getSearchSyntax() != null) {
				if(item.getDefinition().getName().toLowerCase().contains(player.getAttributes().getBankSearchingAttribtues().getSearchSyntax())) {
					if(player.getAttributes().getBankSearchingAttribtues().getSearchedBank().getFreeSlots() == 0)
						return;
					player.getAttributes().getBankSearchingAttribtues().getSearchedBank().add(item, true);
				}
			}
		}

		public static void stopSearch(Player player, boolean openBank) {
			player.getPacketSender().sendClientRightClickRemoval();
			player.getAttributes().getBankSearchingAttribtues().setSearchedBank(null).setSearchingBank(false).setSearchSyntax(null);
			player.getAttributes().setCurrentBankTab(0).setNoteWithdrawal(false);
			player.getPacketSender().sendString(27002, Integer.toString(0)).sendString(27000, "1").sendConfig(117, 0).sendString(5383, "Bank account for: "+player.getUsername()+"");;
			if(openBank)
				player.getBank(0).open();
			player.getAttributes().setInputHandling(null);
		}

		public static void withdrawFromSearch(Player player, Item item) {
			if(player.getAttributes().isBanking() && player.getAttributes().getBankSearchingAttribtues().isSearchingBank()) {
				int tab = Bank.getTabForItem(player, item.getId());
				if(tab == player.getAttributes().getCurrentBankTab() && !player.getBank(tab).contains(item.getId()))
					return;
			}
		}

		public static String getFixedSyntax(String searchSyntax) {
			searchSyntax = searchSyntax.toLowerCase();
			switch(searchSyntax) {
			case "ags":
				return "armadyl godsword";
			case "sgs":
				return "saradomin godsword";
			case "bgs":
				return "bandos godsword";
			case "zgs":
				return "zamorak godsword";
			case "dclaws":
				return "dragon claws";
			case "bcp":
				return "bandos chestplate";
			case "dds":
				return "dragon dagger";
			case "sol":
				return "staff of light";
			case "dh":
				return "dharok";
			case "vls":
				return "vesta's longsword";
			case "tassy":
				return "bandos tassets";
			case "swh":
				return "statius's warhammer";
			case "steads":
				return "steadfast boots";
			case "sq":
				return "square shield";
			case "rol":
				return "ring of life";
			case "pl8":
				return "platebody";
			case "obby":
				return "obsidian";
			case "nat":
				return "nature rune";
			case "lobby":
				return "lobster";
			case "hally":
				return "halberd";
			case "ely":
				return "elysian spirit shield";
			case "dfs":
				return "dragonfire shield";
			case "dbones":
				return "dragon bones";
			case "cls":
				return "chaotic longsword";
			case "cmaul":
				return "chaotic maul";
			case "ccbow":
				return "chaotic crossbow";
			case "ammy":
				return "amulet";
			}
			return searchSyntax;
		}
	}
}