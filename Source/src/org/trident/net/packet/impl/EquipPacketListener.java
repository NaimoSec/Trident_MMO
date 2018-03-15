package org.trident.net.packet.impl;

import org.trident.model.Flag;
import org.trident.model.Item;
import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.model.container.impl.Inventory;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.Misc;
import org.trident.world.content.Achievements;
import org.trident.world.content.BonusManager;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.minigames.impl.Dueling;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.content.skills.impl.runecrafting.RunecraftingPouches;
import org.trident.world.content.skills.impl.slayer.SlayerTasks;
import org.trident.world.entity.player.Player;

/**
 * This packet listener manages the equip action a player
 * executes when wielding or equipping an item.
 * 
 * @author relex lawl
 */

public class EquipPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		if (player.getConstitution() <= 0)
			return;
		int id = packet.readShort();
		int slot = packet.readShortA();
		int interfaceId = packet.readShortA();
		if(id == 4155) {
			player.getPacketSender().sendInterfaceRemoval();
			SlayerTasks task = player.getAdvancedSkills().getSlayer().getSlayerTask();
			if(task == SlayerTasks.NO_TASK)
				player.getPacketSender().sendMessage("You do not have a Slayer task.");
			else
				player.getPacketSender().sendMessage("Your current task is to kill another "+(player.getAdvancedSkills().getSlayer().getAmountToSlay())+" "+Misc.formatText(player.getAdvancedSkills().getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " "))+"s.");
			return;
		} else if(id == 15262) {
			if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 1000)
				return;
			int amt = player.getInventory().getAmount(15262);
			if(amt > 0)
				player.getInventory().delete(15262, amt).add(18016, 10000 * amt);
			player.getAttributes().setClickDelay(System.currentTimeMillis());
			return;
		} else if(id == 5509) {
			RunecraftingPouches.empty(player, RunecraftingPouches.Pouch.SMALL);
			return;
		}
		switch (interfaceId) {
		case Inventory.INTERFACE_ID:
			/*
			 * Making sure slot is valid.
			 */
			if (slot >= 0 && slot <= 28) {
				Item item = player.getInventory().getItems()[slot].copy();
				if(!player.getInventory().contains(item.getId()))
					return;
				/*
				 * Making sure item exists and that id is consistent.
				 */
				if (item != null && id == item.getId()) {
					for (Skill skill : Skill.values()) {
						if (item.getDefinition().getRequirement()[skill.ordinal()] > player.getSkillManager().getMaxLevel(skill)) {
							StringBuilder vowel = new StringBuilder();
							if (skill.getName().startsWith("a") || skill.getName().startsWith("e") || skill.getName().startsWith("i") || skill.getName().startsWith("o") || skill.getName().startsWith("u")) {
								vowel.append("an ");
							} else {
								vowel.append("a ");
							}
							player.getPacketSender().sendMessage("You need " + vowel.toString() + Misc.formatText(skill.getName()) + " level of at least " + item.getDefinition().getRequirement()[skill.ordinal()] + " to wear this.");
							return;
						}
					}
					int equipmentSlot = item.getDefinition().getEquipmentSlot();
					if(equipmentSlot == Equipment.CAPE_SLOT) {
						if(SoulWars.isWithin(SoulWars.RED_LOBBY, player) || SoulWars.isWithin(SoulWars.BLUE_LOBBY, player)
								|| SoulWars.gameRunning && (SoulWars.redTeam.contains(player) || SoulWars.blueTeam.contains(player)))
						{
							player.getPacketSender().sendMessage("You can't wear capes in Soul wars.");
							return;
						}
					}
					Item equipItem = player.getEquipment().forSlot(equipmentSlot).copy();
					for(int i = 10; i < player.getDueling().selectedDuelRules.length; i++) {
						if(player.getDueling().selectedDuelRules[i]) {
							Dueling.DuelRule duelRule = Dueling.DuelRule.forId(i);
							if(equipmentSlot == duelRule.getEquipmentSlot() || duelRule == Dueling.DuelRule.NO_SHIELD && WeaponHandler.twoHandedWeapon(item.getDefinition().getName(), item.getId())) {
								player.getPacketSender().sendMessage("The rules that were set do not allow this item to be equipped.");
								return;
							}
						}
					}
					if (player.getCombatAttributes().hasStaffOfLightEffect() 
							&& equipItem.getDefinition().getName().toLowerCase().contains("staff of light")) {
						player.getCombatAttributes().setStaffOfLightEffect(false);
						player.getPacketSender().sendMessage("You feel the spirit of the Staff of Light begin to fade away...");
					}
					if (equipItem.getDefinition().isStackable() && equipItem.getId() == item.getId()) {
						int amount = equipItem.getAmount() + item.getAmount() <= Integer.MAX_VALUE ? equipItem.getAmount() + item.getAmount() : Integer.MAX_VALUE;
						player.getInventory().delete(item);
						player.getEquipment().getItems()[equipmentSlot].setAmount(amount);
						equipItem.setAmount(amount);
						player.getEquipment().refreshItems();
					} else {
						if (item.getDefinition().isTwoHanded() && item.getDefinition().getEquipmentSlot() == Equipment.WEAPON_SLOT) {
							int slotsNeeded = 0;
							if (player.getEquipment().isSlotOccupied(Equipment.SHIELD_SLOT) && player.getEquipment().isSlotOccupied(Equipment.WEAPON_SLOT)) {
								slotsNeeded++;
							}
							if (player.getInventory().getFreeSlots() >= slotsNeeded) {
								Item shield = player.getEquipment().getItems()[Equipment.SHIELD_SLOT];
								player.getInventory().setItem(slot, equipItem);
								player.getInventory().add(shield);
								player.getEquipment().delete(shield);
								player.getEquipment().setItem(equipmentSlot, item);
							} else {
								player.getInventory().full();
								return;
							}
						} else if (equipmentSlot == Equipment.SHIELD_SLOT && player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getDefinition().isTwoHanded()) {
							player.getInventory().setItem(slot, player.getEquipment().getItems()[Equipment.WEAPON_SLOT]);
							player.getEquipment().setItem(Equipment.WEAPON_SLOT, new Item(-1));
							player.getEquipment().setItem(Equipment.SHIELD_SLOT, item);
						} else {
							if (item.getDefinition().getEquipmentSlot() == equipItem.getDefinition().getEquipmentSlot() && equipItem.getId() != -1) {
								if(player.getInventory().contains(equipItem.getId())) {
									player.getInventory().delete(item);
									player.getInventory().add(equipItem);
								} else
									player.getInventory().setItem(slot, equipItem);
								player.getEquipment().setItem(equipmentSlot, item);
							} else {
								player.getInventory().setItem(slot, new Item(-1, 0));
								player.getEquipment().setItem(item.getDefinition().getEquipmentSlot(), item);
							}
						}
					}
					if(equipmentSlot == Equipment.WEAPON_SLOT || equipmentSlot == Equipment.SHIELD_SLOT) {
						player.getPlayerCombatAttributes().setUsingSpecialAttack(false);
						WeaponHandler.update(player);
					}
					BonusManager.update(player);
					player.getInventory().refreshItems();
					player.getEquipment().refreshItems();
					if(player.getPlayerCombatAttributes().getMagic().getAutocastSpell() == null) {
						CombatHandler.setProperAttackType(player);
						if(player.getCombatAttributes().getAttackType() == AttackType.MELEE)
							Achievements.handleAchievement(player, Achievements.Tasks.TASK6);
						else if(player.getCombatAttributes().getAttackType() == AttackType.RANGED)
							Achievements.handleAchievement(player, Achievements.Tasks.TASK7);
					}
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					if(item.getId() != 4153)
						CombatHandler.resetAttack(player);
					SoundEffects.sendSoundEffect(player, SoundEffects.SoundData.EQUIP_ITEM, 10, 0);
				}
			}
			break;
		}
	}
}