package org.trident.world.content;

import org.trident.model.Flag;
import org.trident.model.Item;
import org.trident.model.container.impl.Equipment;
import org.trident.model.definitions.ItemDefinition;
import org.trident.world.entity.player.Player;

/**
 * Handles item degrading
 * Note: The proper way to do this would be to put the timer inside the item model and saving it there.
 * @author Gabbe
 */
public class ItemDegrading {
	
	/**
	 * Handles the degradiation of items.
	 * Makes items turn to dust after using them in combat for a long time.
	 * @param p 	The player who's armour is going to be degraded
	 */
	public static void handleItemDegrading(Player p, boolean combat) {
		for(DegradingItems degradingItem : DegradingItems.values()) {
			if(!combat && !degradingItem.toString().toLowerCase().contains("brawling") || combat && degradingItem.toString().toLowerCase().contains("brawling")) //Stop loop, brawling gloves shouldnt degrade by combat anyway
				continue;
			int equipId = p.getEquipment().getItems()[degradingItem.equipSlot].getId();
			if(equipId == degradingItem.nonDeg || equipId == degradingItem.deg) {
				boolean degradeCompletely = p.getEquipment().getItemDegradationCharges()[degradingItem.equipSlot] >= degradingItem.degradingCharges;
				if(equipId == degradingItem.deg && !degradeCompletely) {
					p.getEquipment().getItemDegradationCharges()[degradingItem.equipSlot]++;
					continue;
				}
				degradeCompletely = degradeCompletely && equipId == degradingItem.deg;
				p.getEquipment().setItem(degradingItem.equipSlot, new Item(degradeCompletely ? - 1 : degradingItem.deg)).refreshItems();
				p.getEquipment().getItemDegradationCharges()[degradingItem.equipSlot] = 0;
				p.getUpdateFlag().flag(Flag.APPEARANCE);
				String ext = !degradeCompletely ? "degraded slightly" : "turned into dust";
				p.getPacketSender().sendMessage("Your "+ItemDefinition.forId(equipId).getName().replace(" (deg)", "")+" has "+ext+"!");
			}
		}
	}

	/*
	 * The enum holding all degradeable equipment
	 */
	private enum DegradingItems {
		
		/*
		 * Recoil
		 */
		RING_OF_RECOIL(2550, 2550, Equipment.RING_SLOT, 100),
		/*
		 * Statius's equipment
		 */
		CORRUPT_STATIUS_FULL_HELM(13920, 13922, Equipment.HEAD_SLOT, 200),
		CORRUPT_STATIUS_PLATEBODY(13908, 13910, Equipment.BODY_SLOT, 200),
		CORRUPT_STATIUS_PLATELEGS(13914, 13916, Equipment.LEG_SLOT, 200),
		CORRUPT_STATIUS_WARHAMMER(13926, 13928, Equipment.WEAPON_SLOT, 200),
		
		/*
		 * Vesta's equipment
		 */
		CORRUPT_VESTAS_CHAINBODY(13911, 13913, Equipment.BODY_SLOT, 200),
		CORRUPT_VESTAS_PLATESKIRT(13917, 13919, Equipment.LEG_SLOT, 200),
		CORRUPT_VESTAS_LONGSWORD(13923, 13925, Equipment.WEAPON_SLOT, 160),
		CORRUPT_VESTAS_SPEAR(13929, 13931, Equipment.WEAPON_SLOT, 160),
		
		/*
		 * Zuriel's equipment
		 */
		CORRUPT_ZURIELS_HOOD(13938, 13940, Equipment.HEAD_SLOT, 200),
		CORRUPT_ZURIELS_ROBE_TOP(13932, 13934, Equipment.BODY_SLOT, 200),
		CORRUPT_ZURIELS_ROBE_BOTTOM(13935, 13937, Equipment.LEG_SLOT, 200),
		CORRUPT_ZURIELS_STAFF(13941, 13943, Equipment.WEAPON_SLOT, 200),
		
		/*
		 * Morrigan's equipment
		 */
		CORRUPT_MORRIGANS_COIF(13950, 13952, Equipment.HEAD_SLOT, 200),
		CORRUPT_MORRIGANS_LEATHER_BODY(13944, 13946, Equipment.BODY_SLOT, 200),
		CORRUPT_MORRIGANS_LEATHER_CHAPS(13944, 13946, Equipment.LEG_SLOT, 200),
		
		/*
		 * Brawling gloves
		 */
		BRAWLING_GLOVES_SMITHING(13855, 13855, Equipment.HANDS_SLOT, 80),
		BRAWLING_GLOVES_PRAYER(13848, 13848, Equipment.HANDS_SLOT, 80),
		BRAWLING_GLOVES_COOKING(13857, 13857, Equipment.HANDS_SLOT, 80),
		BRAWLING_GLOVES_FISHING(13856, 13856, Equipment.HANDS_SLOT, 80),
		BRAWLING_GLOVES_THIEVING(13854, 13854, Equipment.HANDS_SLOT, 80),
		BRAWLING_GLOVES_HUNTER(13853, 13853, Equipment.HANDS_SLOT, 80),
		BRAWLING_GLOVES_MINING(13852, 13852, Equipment.HANDS_SLOT, 80),
		BRAWLING_GLOVES_FIREMAKING(13851, 13851, Equipment.HANDS_SLOT, 80),
		BRAWLING_GLOVES_WOODCUTTING(13850, 13850, Equipment.HANDS_SLOT, 80);
		
		DegradingItems(int nonDeg, int deg, int equipSlot, int degradingCharges) {
			this.nonDeg = nonDeg;
			this.deg = deg;
			this.equipSlot = equipSlot;
			this.degradingCharges = degradingCharges;
		}
		
		private int nonDeg, deg;
		private int equipSlot;
		private int degradingCharges;
	}
}
