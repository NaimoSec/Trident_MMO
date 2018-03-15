package org.trident.net.packet.impl;

import org.trident.model.GroundItem;
import org.trident.model.Item;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.world.content.ItemLending;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.content.skills.impl.dungeoneering.ItemBinding;
import org.trident.world.content.treasuretrails.Puzzle;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerSaving;

/**
 * This packet listener is called when a player drops an item they
 * have placed in their inventory.
 * 
 * @author relex lawl
 */

public class DropItemPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		@SuppressWarnings("unused")
		int itemIndex = packet.readUnsignedShortA();
		@SuppressWarnings("unused")
		int interfaceIndex = packet.readUnsignedShort();
		int itemSlot = packet.readUnsignedShortA();
		if(player.getAttributes().getInterfaceId() == 6976) {
			if(player.puzzleStoredItems != null) {
				if(player.puzzleStoredItems[itemSlot] != null && player.puzzleStoredItems[itemSlot].getId() > 0)
					Puzzle.moveSlidingPiece(player, player.puzzleStoredItems[itemSlot].getId());
			}
			return;
		}
		if (player.getConstitution() <= 0 || player.getAttributes().getInterfaceId() > 0)
			return;
		if(itemSlot < 0 || itemSlot > player.getInventory().capacity())
			return;
		if(player.getConstitution() <= 0 || player.isTeleporting())
			return;
		Item item = player.getInventory().getItems()[itemSlot];
		player.getPacketSender().sendInterfaceRemoval();
		CombatHandler.resetAttack(player);
		if (item != null && item.getId() != -1 && item.getAmount() >= 1) {
			if(item.tradeable() && !ItemLending.borrowedItem(player, item.getId()) && !ItemBinding.isBoundItem(item.getId())) {
				player.getInventory().setItem(itemSlot, new Item(-1, 0)).refreshItems();
				if(!Dungeoneering.doingDungeoneering(player)) 
					GroundItemManager.spawnGroundItem(player, new GroundItem(item, player.getPosition().copy(), player.getUsername(), player.getHostAdress(), false, 80, player.getPosition().getZ() >= 0 && player.getPosition().getZ() < 4 ? true : false, 80));
				else 
					player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().createGroundItem(item, player.getPosition());
				Logger.log(player.getUsername(), "Dropped item: "+item.getDefinition().getName()+" x"+Misc.insertCommasToNumber(String.valueOf(item.getAmount())));
			} else
				destroyItemInterface(player, item);
		}
		PlayerSaving.save(player);
		SoundEffects.sendSoundEffect(player, SoundEffects.SoundData.DROP_ITEM, 10, 0);
	}
	
	public static void destroyItemInterface(Player player, Item item) {//Destroy item created by Remco
		if(player.getAttributes().getItemToDrop() == null || player.getAttributes().getItemToDrop() != item)
			player.getAttributes().setItemToDrop(item);
		boolean borrowedItem = ItemLending.borrowedItem(player, item.getId());
		String[][] info = {//The info the dialogue gives
				{ "Are you sure you want to discard this item?", "14174" },
				{ "Yes.", "14175" }, { "No.", "14176" }, { "", "14177" },
				{ borrowedItem ? "Do you want to discard this item? It will return to its owner." : "This item will vanish once it hits the floor.", "14182" }, {borrowedItem ? "You will not be able to get it back unless you re-borrow it." : "You cannot get it back if discarded.", "14183" },
				{ item.getDefinition().getName(), "14184" } };
		player.getPacketSender().sendItemOnInterface(14171, borrowedItem ? player.getTrading().getItemLending().getBorrowedItem().getId() : item.getId(), 0, item.getAmount());
		for (int i = 0; i < info.length; i++)
			player.getPacketSender().sendString(Integer.parseInt(info[i][1]), info[i][0]);
		player.getPacketSender().createFrame(14170);
	}
}
