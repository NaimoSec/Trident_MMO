package org.trident.net.packet.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.GroundItem;
import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.model.Projectile;
import org.trident.model.Skill;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.Constants;
import org.trident.util.Logger;
import org.trident.world.content.ItemLending;
import org.trident.world.content.Locations;
import org.trident.world.content.combat.combatdata.magic.Enchanting;
import org.trident.world.content.combat.combatdata.magic.MagicSpells;
import org.trident.world.content.combat.combatdata.magic.Spell;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.player.Player;

/**
 * Handles magic on items. 
 * @author Gabbe
 */
public class MagicOnItemsPacketListener implements PacketListener {

	@Override
	public void execute(final Player player, Packet packet) {
		if(packet.getOpcode() == MAGIC_ON_GROUNDITEMS) {
			final int itemY = packet.readLEShort();
			final int itemId = packet.readShort();
			final int itemX = packet.readLEShort();
			final int spellId = packet.readUnsignedShortA();
			final MagicSpells spell = MagicSpells.forSpellId(spellId);
			if(spell == null)
				return;
			player.getMovementQueue().stopMovement();
			if(!spell.getSpell().prepareCast(player, null, true))
				return;
			switch(spell) {
			case TELEKINETIC_GRAB:
				if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 6000)
					return;
				player.getAttributes().setClickDelay(System.currentTimeMillis());
				final GroundItem item = GroundItemManager.getGroundItem(player, new Item(itemId), new Position(itemX, itemY, player.getPosition().getZ()));
				if(item == null || item.getItem().getId() != itemId)
					return;
				if(!Locations.goodDistance(player.getPosition(), item.getPosition(), 9)) {
					player.getPacketSender().sendMessage("You can't reach that!");
					return;
				}
				if(player.getInventory().getFreeSlots() <= 0) {
					player.getInventory().full();
					return;
				}
				player.setPositionToFace(item.getPosition());
				player.performAnimation(new Animation(711));
				player.performGraphic(new Graphic(142, GraphicHeight.MIDDLE));
				TaskManager.submit(new Task(1, player, false) {
					int tick = 0;
					@Override
					public void execute() {
						switch(tick) {
						case 2:
							player.getPacketSender().sendGlobalProjectile(new Projectile(new Position(player.getPosition().getX(), player.getPosition().getY(), 43), new Position(item.getPosition().getX(), item.getPosition().getY(), 31), new Graphic(143), 0, 50, 50), null);
							break;
						case 4:
							this.stop();
							GroundItem item2 = GroundItemManager.getGroundItem(player, new Item(itemId), new Position(itemX, itemY, player.getPosition().getZ()));
							if(item2 == null || item2 != item)
								return;
							player.getSkillManager().addExperience(Skill.MAGIC, spell.getSpell().baseExperience(), false);
							item.setPickedUp(true);
							GroundItemManager.remove(item, true);
							player.getInventory().add(item.getItem());
							player.getPacketSender().sendClientRightClickRemoval();
							player.getAttributes().setClickDelay(0);
							Logger.log(player.getUsername(), "Player used TELEKINETIC_GRAB to pick up: "+item.getItem().getId()+" x"+item.getItem().getAmount()+"");
							break;
						}
						tick++;
					}
				});
				break;
			default:
				break;
			}
		} else if(packet.getOpcode() == MAGIC_ON_ITEMS) {
			int slot = packet.readShort();
			int itemId = packet.readShortA();
			@SuppressWarnings("unused")
			int childId = packet.readShort();
			int spellId = packet.readShortA();
			if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 1300)
				return;
			if(slot < 0 || slot > player.getInventory().capacity())
				return;
			if(player.getInventory().getItems()[slot].getId() != itemId)
				return;
			if(Enchanting.enchantItem(player, itemId, spellId))
				return;
			Item item = new Item(itemId);
			MagicSpells magicSpell = MagicSpells.forSpellId(spellId);
			if(magicSpell == null)
				return;
			Spell spell = magicSpell.getSpell();
			if(spell == null || !spell.prepareCast(player, null, false))
				return;
			switch(magicSpell) {
			case LOW_ALCHEMY:
			case HIGH_ALCHEMY:
				if(!item.tradeable() || ItemLending.borrowedItem(player, item.getId()) || item.getId() == 995) {
					player.getPacketSender().sendMessage("This spell can not be cast on this item.");
					return;
				}
				for(int i = 0; i < Constants.unsellable.length; i++) {
					if(Constants.unsellable[i] == item.getId()) {
						player.getPacketSender().sendMessage("This spell can not be cast on this item.");
						return;
					}
				}
				if(ItemLending.borrowedItem(player, item.getId()) || item.getDefinition().getValue() <= 0) {
					player.getPacketSender().sendMessage("This spell can not be cast on this item.");
					return;
				}
				player.getInventory().delete(item.getId(), 1).delete(561, 1).delete(554, magicSpell == MagicSpells.HIGH_ALCHEMY ? 5 : 3).add(995,  200 + (int) (item.getDefinition().getValue() * (magicSpell == MagicSpells.HIGH_ALCHEMY ? 1 : 0.8)));
				player.performAnimation(new Animation(712));
				player.performGraphic(new Graphic(magicSpell == MagicSpells.HIGH_ALCHEMY ? 113 : 112, GraphicHeight.LOW));
				player.getSkillManager().addExperience(Skill.MAGIC, spell.baseExperience(), false);
				player.getPacketSender().sendTab(Constants.MAGIC_TAB);
				break;
			case SUPERHEAT_ITEM:
				for(int i = 0; i < ORE_DATA.length; i++) {
					if(item.getId() == ORE_DATA[i][0]) {
						if(player.getInventory().getAmount(ORE_DATA[i][2]) < ORE_DATA[i][3]) {
							player.getPacketSender().sendMessage("You do not have enough "+new Item(ORE_DATA[i][2]).getDefinition().getName()+"s for this spell.");
							return;
						}
						player.getInventory().delete(item.getId(), 1);
						for(int k = 0; k < ORE_DATA[i][3]; k++)
							player.getInventory().delete(ORE_DATA[i][2], 1);
						player.performAnimation(new Animation(725));
						player.performGraphic(new Graphic(148, GraphicHeight.HIGH));
						player.getInventory().add(ORE_DATA[i][4], 1).delete(554, 4).delete(561, 1);
						player.getPacketSender().sendTab(Constants.MAGIC_TAB);
						player.getSkillManager().addExperience(Skill.MAGIC, spell.baseExperience(), false);
						return;
					}		
				}
				player.getPacketSender().sendMessage("This spell can only be cast on Mining ores.");
				break;
			case BAKE_PIE:
				if (itemId == 2317 || itemId == 2319 || itemId == 2321) {
					player.getSkillManager().addExperience(Skill.MAGIC, spell.baseExperience(), false);
					player.performAnimation(new Animation(4413));
					player.performGraphic(new Graphic(746, GraphicHeight.HIGH));
					player.getInventory().delete(item.getId(), 1);
					player.getPacketSender().sendMessage("You bake the pie");
					player.getInventory().add(itemId == 2317 ? 2323 : itemId == 2319 ? 2327 : itemId == 2321 ? 2325 : -1, 1);
				} else
					player.getPacketSender().sendMessage("This spell can only be cast on an uncooked pie.");
				break;
			default:
				break;
			}
			player.getAttributes().setClickDelay(System.currentTimeMillis());
			player.getInventory().refreshItems();
		}
	}

	final static int[][] ORE_DATA = {
		{436, 1, 438, 1, 2349, 53}, // TIN
		{438, 1, 436, 1, 2349, 53}, // COPPER
		{440, 1, -1, -1, 2351, 53}, // IRON ORE
		{442, 1, -1, -1, 2355, 53}, // SILVER ORE
		{444, 1, -1, -1, 2357, 23}, // GOLD BAR
		{447, 1, 453, 4, 2359, 30}, // MITHRIL ORE
		{449, 1, 453, 6, 2361, 38}, // ADDY ORE
		{451, 1, 453, 8, 2363, 50}, // RUNE ORE
	};

	public static final int MAGIC_ON_GROUNDITEMS = 181;
	public static final int MAGIC_ON_ITEMS = 237;
}
