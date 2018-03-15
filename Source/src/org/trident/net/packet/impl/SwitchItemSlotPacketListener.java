package org.trident.net.packet.impl;

import org.trident.model.Item;
import org.trident.model.container.impl.Bank;
import org.trident.model.container.impl.Inventory;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.entity.player.Player;

/**
 * This packet listener is called when an item is dragged onto another slot.
 * 
 * @author relex lawl
 */

public class SwitchItemSlotPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		int interfaceId = packet.readLEShortA();
		packet.readByteC();
		int fromSlot = packet.readLEShortA();
		int toSlot = packet.readLEShort();
		if(player.getAttributes().isBanking() && interfaceId != 5064 /*INVENTORY */) {
			if(player.getAttributes().getBankSearchingAttribtues().isSearchingBank()) {
				player.getPacketSender().sendMessage("You cannot edit or create tabs right now.");
				return;
			}
			boolean toBankTab = false;
			for(int i = 0; i < Bank.BANK_TAB_INTERFACES.length; i++) {
				toBankTab = Bank.BANK_TAB_INTERFACES[i][0] == interfaceId;
				Item item = new Item(player.getBank(player.getAttributes().getCurrentBankTab()).getItems()[fromSlot].getId(), player.getBank(player.getAttributes().getCurrentBankTab()).getItems()[fromSlot].getAmount());
				if(!player.getBank(player.getAttributes().getCurrentBankTab()).contains(item.getId()) || player.getBank(player.getAttributes().getCurrentBankTab()).getAmount(item.getId()) < item.getAmount())
					return;
				if(toBankTab) {
					int bankTab = Bank.BANK_TAB_INTERFACES[i][1];
					int slot = player.getBank(player.getAttributes().getCurrentBankTab()).getSlot(item.getId());
					if(slot < 0)
						return;
					player.getAttributes().setNoteWithdrawal(false);
					player.getBank(Bank.getTabForItem(player, item.getId())).switchItem(player.getBank(bankTab), item, slot, true, false);
					Bank.sendTabs(player);
					player.getBank(player.getAttributes().getCurrentBankTab()).open();
					return;
				}
			}
			if(!toBankTab && fromSlot != toSlot) {
				player.getBank(player.getAttributes().getCurrentBankTab()).swap(fromSlot, toSlot);
				player.getBank(player.getAttributes().getCurrentBankTab()).open();
				return;
			}
		}
		if(fromSlot == toSlot)
			return;
		switch (interfaceId) {
		case Inventory.INTERFACE_ID:
			if(fromSlot >= 0 && fromSlot < player.getInventory().capacity() && toSlot >= 0 && toSlot < player.getInventory().capacity() && toSlot != fromSlot) {
				player.getInventory().swap(fromSlot, toSlot);
			}
			break;

		}
	}
}
