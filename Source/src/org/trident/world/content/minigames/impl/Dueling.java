package org.trident.world.content.minigames.impl;

import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Flag;
import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.container.impl.Equipment;
import org.trident.model.container.impl.Inventory;
import org.trident.model.definitions.ItemDefinition;
import org.trident.model.movement.MovementStatus;
import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.Achievements;
import org.trident.world.content.BonusManager;
import org.trident.world.content.ItemLending;
import org.trident.world.content.Locations;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerSaving;

public class Dueling {

	Player player;
	public Dueling(Player player) {
		this.player = player;
	}

	public void challengePlayer(Player playerToDuel) {
		if(player.getLocation() != Location.DUEL_ARENA)
			return;
		if(player.getHostAdress().equals(playerToDuel.getHostAdress()) && player.getRights() != PlayerRights.OWNER && player.getRights() != PlayerRights.DEVELOPER) {
			player.getPacketSender().sendMessage("Same IP-adress found. You cannot duel yourself from the same IP.");
			return;
		}
		if(player.getAttributes().getInterfaceId() > 0) {
			player.getPacketSender().sendMessage("Please close the interface you have open before trying to open a new one.");
			return;
		}
		if(!Locations.goodDistance(player.getPosition().getX(), player.getPosition().getY(), playerToDuel.getPosition().getX(), playerToDuel.getPosition().getY(), 2)) {
			player.getPacketSender().sendMessage("Please get closer to request a duel.");
			return;
		}
		if(!checkDuel(player, 0)) {
			player.getPacketSender().sendMessage("Unable to request duel. Please try logging out and then logging back in.");
			return;
		}
		if(!checkDuel(playerToDuel, 0) || playerToDuel.getAttributes().getInterfaceId() > 0) {
			player.getPacketSender().sendMessage("The other player is currently busy.");
			return;
		}
		if(player.getDueling().duelingStatus == 5) {
			player.getPacketSender().sendMessage("You can only challenge people outside the arena.");
			return;
		}
		if(inDuelScreen)
			return;
		if(player.getTrading().inTrade())
			player.getTrading().declineTrade(true);
		duelingWith = playerToDuel.getIndex();
		if(duelingWith == player.getIndex())
			return;
		duelRequested = true;
		boolean challenged = playerToDuel.getDueling().duelingStatus == 0 && duelRequested || playerToDuel.getDueling().duelRequested;
		if (duelingStatus == 0 && challenged && duelingWith == playerToDuel.getIndex() && playerToDuel.getDueling().duelingWith == player.getIndex()) {
			if (duelingStatus == 0) {
				openDuel();
				playerToDuel.getDueling().openDuel();
			} else {
				player.getPacketSender().sendMessage("You must decline this duel before accepting another one!");
			}
		} else if(duelingStatus == 0) {
			playerToDuel.getPacketSender().sendMessage(player.getUsername() +":duelreq:");
			player.getPacketSender().sendMessage("You have sent a duel request to "+playerToDuel.getUsername()+".");
		}
	}

	public void openDuel() {
		Player playerToDuel = World.getPlayers().get(duelingWith);
		if (playerToDuel == null)
			return;
		player.getPacketSender().sendClientRightClickRemoval();
		inDuelWith = playerToDuel.getIndex();
		stakedItems.clear();
		inDuelScreen = true;
		duelingStatus = 1;
		if(!checkDuel(player, 1))
			return;
		for (int i = 0; i < selectedDuelRules.length; i++)
			selectedDuelRules[i] = false;
		player.getPacketSender().sendConfig(286, 0);
		player.getTrading().setCanOffer(true);
		player.getPacketSender().sendDuelEquipment();
		player.getPacketSender().sendString(6671, "Dueling with: " + playerToDuel.getUsername() +", Level: "+playerToDuel.getSkillManager().getCombatLevel()+", Duel victories: "+playerToDuel.getDueling().arenaStats[0]+", Duel losses: "+playerToDuel.getDueling().arenaStats[1]);
		player.getPacketSender().sendString(6684, "");
		player.getPacketSender().sendInterfaceSet(6575, 3321);
		player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
		player.getPacketSender().sendInterfaceItems(6670, playerToDuel.getDueling().stakedItems);
		player.getPacketSender().sendInterfaceItems(6669, player.getDueling().stakedItems);
	}

	public void declineDuel(boolean tellOther) {
		Player playerToDuel = duelingWith >= 0? World.getPlayers().get(duelingWith) : null;
		if(tellOther) {
			if (playerToDuel == null)
				return; 
			if (playerToDuel == null || playerToDuel.getDueling().duelingStatus == 6) {
				return;
			}
			playerToDuel.getDueling().declineDuel(false);
		}
		for (Item item : stakedItems) {
			if (item.getAmount() < 1)
				continue;
			player.getInventory().add(item);
			Logger.log(player.getUsername(), "Received item from unfinished stake: "+item.getDefinition().getName()+" x"+Misc.insertCommasToNumber(String.valueOf(item.getAmount()))+" from duel partner: "+playerToDuel != null ? playerToDuel.getUsername() : "null");
		}
		reset();
		player.getPacketSender().sendInterfaceRemoval();
	}

	public void stakeItem(int itemId, int amount, int slot) {
		if(slot < 0)
			return;
		if(!player.getTrading().getCanOffer())
			return;
		if(player.getAttributes().getNewPlayerDelay() > 0 && player.getRights().ordinal() == 0) {
			player.getPacketSender().sendMessage("You must wait another "+player.getAttributes().getNewPlayerDelay() / 60+" minutes before being able to duel.");
			return;
		}
		if(player.getRights() == PlayerRights.ADMINISTRATOR) {
			player.getPacketSender().sendMessage("You cannot stake items since you are an Administrator.");
			return;
		}
		if(!new Item(itemId).tradeable() || ItemLending.borrowedItem(player, itemId)) {
			player.getPacketSender().sendMessage("This is an untradeable item and cannot be sold.");
			return;
		}
		if (ItemLending.borrowedItem(player, itemId)) {
			player.getPacketSender().sendMessage("This item is untradeable and cannot be staked.");
			return;
		}
		if(!player.getInventory().contains(itemId) || !inDuelScreen)
			return;
		Player playerToDuel = World.getPlayers().get(duelingWith);
		if (playerToDuel == null)
			return; 
		if(!checkDuel(player, 1) || !checkDuel(playerToDuel, 1) || slot >= player.getInventory().capacity() || player.getInventory().getItems()[slot].getId() != itemId || player.getInventory().getItems()[slot].getAmount() <= 0) {
			declineDuel(false);
			playerToDuel.getDueling().declineDuel(false);
			return;
		}
		if (player.getInventory().getAmount(itemId) < amount) {
			amount = player.getInventory().getAmount(itemId);
			if (amount == 0 || player.getInventory().getAmount(itemId) < amount) {
				return;
			}
		}
		if (!ItemDefinition.forId(itemId).isStackable()) {
			for (int a = 0; a < amount; a++) {
				if (player.getInventory().contains(itemId)) {
					stakedItems.add(new Item(itemId, 1));
					player.getInventory().delete(new Item(itemId));
				}
			}
		} else {
			if(amount <= 0 || player.getInventory().getItems()[slot].getAmount() <= 0)
				return;
			boolean itemInScreen = false;
			for (Item item : stakedItems) {
				if (item.getId() == itemId) {
					itemInScreen = true;
					item.setAmount(item.getAmount() + amount);
					player.getInventory().delete(new Item(itemId).setAmount(amount), slot);
					break;
				}
			}
			if (!itemInScreen) {
				player.getInventory().delete(new Item(itemId, amount), slot);
				stakedItems.add(new Item(itemId, amount));
			}
		}
		player.getPacketSender().sendClientRightClickRemoval();
		Item it = new Item(itemId, amount);
		Logger.log(player.getUsername(), "Stake item: "+it.getDefinition().getName()+", noted: "+it.getDefinition().isNoted()+", amount: "+Misc.insertCommasToNumber(String.valueOf(amount))+". Stake partner: "+playerToDuel.getUsername());
		player.getPacketSender().sendInterfaceItems(6670, playerToDuel.getDueling().stakedItems);
		player.getPacketSender().sendInterfaceItems(6669, player.getDueling().stakedItems);
		playerToDuel.getPacketSender().sendInterfaceItems(6670, player.getDueling().stakedItems);
		player.getPacketSender().sendString(6684, "");
		playerToDuel.getPacketSender().sendString(6684, "");
		duelingStatus = 1; playerToDuel.getDueling().duelingStatus = 1;
		player.getInventory().refreshItems();
		player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
	}

	public void removeStakedItem(int itemId, int amount) {
		if(!inDuelScreen || !player.getTrading().getCanOffer())
			return;
		Player playerToDuel = World.getPlayers().get(duelingWith);
		if (playerToDuel == null)
			return;
		if(!checkDuel(player, 1) || !checkDuel(playerToDuel, 1)) {
			declineDuel(false);
			playerToDuel.getDueling().declineDuel(false);
			return;
		}
		/*
	        if (Item.itemStackable[itemID]) {
	            if (playerToDuel.getInventory().getFreeSlots() - 1 < (c.duelSpaceReq)) {
	                c.sendMessage("You have too many rules set to remove that item.");
	                return false;
	            }
	        }*/
		player.getPacketSender().sendClientRightClickRemoval();
		if (!ItemDefinition.forId(itemId).isStackable()) {
			if (amount > 28)
				amount = 28;
			for (int a = 0; a < amount; a++) {
				for (Item item : stakedItems) {
					if (item.getId() == itemId) {
						if (!item.getDefinition().isStackable()) {
							if(!checkDuel(player, 1) || !checkDuel(playerToDuel, 1)) {
								declineDuel(false);
								playerToDuel.getDueling().declineDuel(false);
								return;
							}
							stakedItems.remove(item);
							player.getInventory().add(item);
							Logger.log(player.getUsername(), "Removed item from stake: "+item.getDefinition().getName()+", noted: "+item.getDefinition().isNoted()+", amount: "+Misc.insertCommasToNumber(String.valueOf(amount))+". Stake partner: "+playerToDuel.getUsername());
						}
						break;
					}
				}
			}
		} else
			for (Item item : stakedItems) {
				if (item.getId() == itemId) {
					if (item.getDefinition().isStackable()) {
						if (item.getAmount() > amount) {
							item.setAmount(item.getAmount() - amount);
							player.getInventory().add(itemId, amount);
						} else {
							amount = item.getAmount();
							stakedItems.remove(item);
							player.getInventory().add(item.getId(), amount);
						}
						Logger.log(player.getUsername(), "Removed item from stake: "+item.getDefinition().getName()+", noted: "+item.getDefinition().isNoted()+", amount: "+Misc.insertCommasToNumber(String.valueOf(amount))+". Stake partner: "+playerToDuel.getUsername());
					}
					break;
				}
			}
		player.getPacketSender().sendInterfaceItems(6670, playerToDuel.getDueling().stakedItems);
		player.getPacketSender().sendInterfaceItems(6669, player.getDueling().stakedItems);
		playerToDuel.getPacketSender().sendInterfaceItems(6670, player.getDueling().stakedItems);
		playerToDuel.getPacketSender().sendString(6684, "");
		player.getPacketSender().sendString(6684, "");
		duelingStatus = 1; playerToDuel.getDueling().duelingStatus = 1;
		player.getInventory().refreshItems();
		player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
	}

	public void selectRule(DuelRule duelRule) {
		if(duelingWith < 0)
			return;
		final Player playerToDuel = World.getPlayers().get(duelingWith);
		if (playerToDuel == null)
			return;
		if(player.getAttributes().getInterfaceId() != 6575)
			return;
		int index = duelRule.ordinal();
		boolean alreadySet = selectedDuelRules[duelRule.ordinal()];
		boolean slotOccupied = duelRule.getEquipmentSlot() > 0 ? player.getEquipment().getItems()[duelRule.getEquipmentSlot()].getId() > 0 || playerToDuel.getEquipment().getItems()[duelRule.getEquipmentSlot()].getId() > 0 : false;
		if(duelRule == DuelRule.NO_SHIELD) {
			if(player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() > 0 && WeaponHandler.twoHandedWeapon(ItemDefinition.forId(player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId()).getName(), player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId()) || WeaponHandler.twoHandedWeapon(ItemDefinition.forId(playerToDuel.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId()).getName(), playerToDuel.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId()))
				slotOccupied = true;
		}
		int spaceRequired = slotOccupied ? duelRule.getInventorySpaceReq() : 0;
		for(int i = 10; i < this.selectedDuelRules.length; i++) {
			if(selectedDuelRules[i]) {
				DuelRule rule = DuelRule.forId(i);
				if(rule.getEquipmentSlot() > 0)
					if(player.getEquipment().getItems()[rule.getEquipmentSlot()].getId() > 0 || playerToDuel.getEquipment().getItems()[rule.getEquipmentSlot()].getId() > 0)
						spaceRequired += rule.getInventorySpaceReq();
			}
		}
		if (!alreadySet && player.getInventory().getFreeSlots() < spaceRequired) {
			player.getPacketSender().sendMessage("You do not have enough free inventory space to set this rule.");
			return;
		}
		if(!alreadySet && playerToDuel.getInventory().getFreeSlots() < spaceRequired) {
			player.getPacketSender().sendMessage(""+playerToDuel.getUsername()+" does not have enough inventory space for this rule to be set.");
			return;
		}
		if (!player.getDueling().selectedDuelRules[index]) {
			player.getDueling().selectedDuelRules[index] = true;
			player.getDueling().duelConfig += duelRule.getConfigId();
		} else {
			player.getDueling().selectedDuelRules[index] = false;
			player.getDueling().duelConfig -= duelRule.getConfigId();
		}
		player.getPacketSender().sendToggle(286, player.getDueling().duelConfig);
		playerToDuel.getDueling().duelConfig = player.getDueling().duelConfig;
		playerToDuel.getDueling().selectedDuelRules[index] = player.getDueling().selectedDuelRules[index];
		playerToDuel.getPacketSender().sendToggle(286, playerToDuel.getDueling().duelConfig);
		player.getPacketSender().sendString(6684, "");
		playerToDuel.getPacketSender().sendString(6684, "@red@"+player.getUsername()+" modified the rules! Duel must be accepted again.");
		player.getPacketSender().sendString(6684, "@red@The rules were modified. Duel must be accepted again.");
		if(playerToDuel.getDueling().duelingStatus == 2)
			playerToDuel.getDueling().duelingStatus = 1;
		if(player.getDueling().duelingStatus == 2)
			player.getDueling().duelingStatus = 1;

		if (selectedDuelRules[DuelRule.OBSTACLES.ordinal()]) {
			if (selectedDuelRules[DuelRule.NO_MOVEMENT.ordinal()]) {
				Position duelTele = new Position(3366 + Misc.getRandom(12), 3246 + Misc.getRandom(6), 0);
				player.getDueling().duelTelePos = duelTele;
				playerToDuel.getDueling().duelTelePos = player.getDueling().duelTelePos.copy();
				playerToDuel.getDueling().duelTelePos.setX(player.getDueling().duelTelePos.getX() - 1);
			}
		} else {
			if (selectedDuelRules[DuelRule.NO_MOVEMENT.ordinal()]) {
				Position duelTele = new Position(3335 + Misc.getRandom(12), 3246 + Misc.getRandom(6), 0);
				player.getDueling().duelTelePos = duelTele;
				playerToDuel.getDueling().duelTelePos = player.getDueling().duelTelePos.copy();
				playerToDuel.getDueling().duelTelePos.setX(player.getDueling().duelTelePos.getX() - 1);
			}
		}
	}

	/**
	 * Checks if two players are the only ones in a duel.
	 * @param p1	Player1 to check if he's 1/2 player in trade.
	 * @param p2	Player2 to check if he's 2/2 player in trade.
	 * @return		true if only two people are in the duel.
	 */
	public static boolean twoDuelers(Player p1, Player p2) {
		int count = 0;
		for(Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if(player.getDueling().inDuelWith == p1.getIndex() || player.getDueling().inDuelWith == p2.getIndex()) {
				count++;
			}
		}
		return count == 2;
	}

	public void confirmDuel() {
		final Player playerToDuel = World.getPlayers().get(duelingWith);
		if (playerToDuel == null)
			return; 
		else {
			if(!twoDuelers(player, playerToDuel)) {
				player.getPacketSender().sendMessage("An error has occured. Please try requesting a new duel.");
				return;
			}
		}
		String itemId = "";
		for (Item item : player.getDueling().stakedItems) {
			ItemDefinition def = item.getDefinition();
			if (def.isStackable()) {
				itemId += def.getName() + " x " + Misc.format(item.getAmount()) + "\\n";
			} else {
				itemId += def.getName() + "\\n";
			}
		}
		player.getPacketSender().sendString(6516, itemId);
		itemId = "";
		for (Item item : playerToDuel.getDueling().stakedItems) {
			ItemDefinition def = item.getDefinition();
			if (def.isStackable()) {
				itemId += def.getName() + " x " + Misc.format(item.getAmount()) + "\\n";
			} else {
				itemId += def.getName() + "\\n";
			}
		}
		player.getPacketSender().sendString(6517, itemId);
		player.getPacketSender().sendString(8242, "");
		for (int i = 8238; i <= 8253; i++)
			player.getPacketSender().sendString(i, "");
		player.getPacketSender().sendString(8250, "Hitpoints will be restored.");
		player.getPacketSender().sendString(8238, "Boosted stats will be restored.");
		if (selectedDuelRules[DuelRule.OBSTACLES.ordinal()])
			player.getPacketSender().sendString(8239, "@red@There will be obstacles in the arena.");
		player.getPacketSender().sendString(8240, "");
		player.getPacketSender().sendString(8241, "");
		int lineNumber = 8242;
		for (int i = 0; i < DuelRule.values().length; i++) {
			if(i == DuelRule.OBSTACLES.ordinal())
				continue;
			if (selectedDuelRules[i]) {
				player.getPacketSender().sendString(lineNumber, "" + DuelRule.forId(i).toString());
				lineNumber++;
			}
		}
		player.getPacketSender().sendString(6571, "");
		player.getPacketSender().sendInterfaceSet(6412, Inventory.INTERFACE_ID);
		player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
	}

	public static void handleDuelingButtons(final Player player, int button) {
		if(DuelRule.forButtonId(button) != null) {
			player.getDueling().selectRule(DuelRule.forButtonId(button));
			return;
		} else {
			if(player.getDueling().duelingWith < 0)
				return;
			final Player playerToDuel = World.getPlayers().get(player.getDueling().duelingWith);
			switch(button) {
			case 6674:
				if(!player.getDueling().inDuelScreen)
					return;
				if (playerToDuel == null)
					return; 
				if(!checkDuel(player, 1) && !checkDuel(player, 2))
					return;
				if (player.getDueling().selectedDuelRules[Dueling.DuelRule.NO_MELEE.ordinal()] && player.getDueling().selectedDuelRules[Dueling.DuelRule.NO_RANGED.ordinal()] && player.getDueling().selectedDuelRules[Dueling.DuelRule.NO_MAGIC.ordinal()]) {
					player.getPacketSender().sendMessage("You won't be able to attack the other player with the current rules.");
					break;
				}
				player.getDueling().duelingStatus = 2;
				if (player.getDueling().duelingStatus == 2) {
					player.getPacketSender().sendString(6684, "Waiting for other player...");
					playerToDuel.getPacketSender().sendString(6684, "Other player has accepted.");
				}
				if (playerToDuel.getDueling().duelingStatus == 2) {
					playerToDuel.getPacketSender().sendString(6684, "Waiting for other player...");
					player.getPacketSender().sendString(6684, "Other player has accepted.");
				}
				if (player.getDueling().duelingStatus == 2 && playerToDuel.getDueling().duelingStatus == 2) {
					player.getTrading().setCanOffer(false); playerToDuel.getTrading().setCanOffer(false);
					player.getDueling().duelingStatus = 3;
					playerToDuel.getDueling().duelingStatus = 3;
					playerToDuel.getDueling().confirmDuel();
					player.getDueling().confirmDuel();
				}
				break;
			case 6520:
				if(!player.getDueling().inDuelScreen || (!checkDuel(player, 3) && !checkDuel(player, 4)) || playerToDuel == null)
					return;
				player.getDueling().duelingStatus = 4;
				if (playerToDuel.getDueling().duelingStatus == 4 && player.getDueling().duelingStatus == 4) {
					player.getDueling().startDuel();
					playerToDuel.getDueling().startDuel();
				} else {
					player.getPacketSender().sendString(6571, "Waiting for other player...");
					playerToDuel.getPacketSender().sendString(6571, "Other player has accepted");
				}
				break;
			}
		}
	}

	public void startDuel() {
		inDuelScreen = false;
		final Player playerToDuel = World.getPlayers().get(duelingWith);
		if (playerToDuel == null) {
			duelVictory();
			return;
		}
		player.getTrading().offeredItems.clear();
		duelingData[0] = playerToDuel != null ? playerToDuel.getUsername() : "Disconnected";
		duelingData[1] = playerToDuel != null ? playerToDuel.getSkillManager().getCombatLevel() : 3;
		Item equipItem;
		for(int i = 10; i < selectedDuelRules.length; i++) {
			DuelRule rule = DuelRule.forId(i);
			if(selectedDuelRules[i]) {
				if(rule.getEquipmentSlot() < 0)
					continue;
				if(player.getEquipment().getItems()[rule.getEquipmentSlot()].getId() > 0) {
					equipItem = new Item(player.getEquipment().getItems()[rule.getEquipmentSlot()].getId(), player.getEquipment().getItems()[rule.getEquipmentSlot()].getAmount());
					player.getEquipment().delete(equipItem);
					player.getInventory().add(equipItem);
				}
			}
		}
		if(selectedDuelRules[DuelRule.NO_WEAPON.ordinal()] || selectedDuelRules[DuelRule.NO_SHIELD.ordinal()]) {
			if(player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() > 0) {
				if(WeaponHandler.twoHandedWeapon(ItemDefinition.forId(player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId()).getName(), player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId())) {
					equipItem = new Item(player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId(), player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getAmount());
					player.getEquipment().delete(equipItem);
					player.getInventory().add(equipItem);
				}
			}
		}
		equipItem = null;
		player.getInventory().refreshItems();
		player.getEquipment().refreshItems();
		duelingStatus = 5;
		player.getPacketSender().sendInterfaceRemoval();
		if (selectedDuelRules[DuelRule.OBSTACLES.ordinal()]) {
			if (selectedDuelRules[DuelRule.NO_MOVEMENT.ordinal()]) {
				player.moveTo(duelTelePos);
			} else {
				player.moveTo(new Position(3366 + Misc.getRandom(12), 3246 + Misc.getRandom(6), 0));
			}
		} else {
			if (selectedDuelRules[DuelRule.NO_MOVEMENT.ordinal()]) {
				player.moveTo(duelTelePos);
			} else {
				player.moveTo(new Position(3335 + Misc.getRandom(12), 3246 + Misc.getRandom(6), 0));
			}
		}
		player.restart();
		player.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
		player.getPacketSender().sendPositionalHint(playerToDuel.getPosition().copy(), 10);
		PlayerSaving.save(player);
		player.getCombatAttributes().setStunned(true);
		TaskManager.submit(new Task(2, player, false) {
			@Override
			public void execute() {
				if(player.getLocation() != Location.DUEL_ARENA) {
					player.getMovementQueue().setMovementStatus(MovementStatus.NONE);
					player.getCombatAttributes().setStunned(false);
					stop();
					return;
				}
				if(timer == 3 || timer == 2 || timer == 1)
					player.forceChat(""+timer+"..");
				else {
					player.forceChat("FIGHT!!");
					player.getCombatAttributes().setStunned(false);
					player.getMovementQueue().setMovementStatus(MovementStatus.NONE);
					timer = 0;
					stop();
					return;
				}
				timer--;
			}
		});
		player.getUpdateFlag().flag(Flag.APPEARANCE);
		BonusManager.update(player);
	}

	public void duelVictory() {
		duelingStatus = 6;
		player.restart();
		player.getMovementQueue().setMovementStatus(MovementStatus.NONE);
		if(duelingWith > 0) {
			Player playerDuel = World.getPlayers().get(duelingWith);
			if(playerDuel != null && playerDuel.getDueling().stakedItems.size() > 0)
				stakedItems.addAll(playerDuel.getDueling().stakedItems);
		}
		player.getPacketSender().sendInterfaceItems(6822, stakedItems);
		player.getPacketSender().sendString(6840, ""+duelingData[0]);
		player.getPacketSender().sendString(6839, "" + duelingData[1]);
		player.moveTo(new Position(3368 + Misc.getRandom(5), 3267+ Misc.getRandom(3), 0));
		for (Item item : stakedItems)
			addItem(item);
		reset();
		arenaStats[0]++;
		PlayerSaving.save(player);
		player.setEntityInteraction(null);
		Achievements.handleAchievement(player, Achievements.Tasks.TASK22);
		player.getMovementQueue().stopMovement();
		player.getPacketSender().sendInterface(6733);
		player.getPointsHandler().refreshPanel();
	}

	public void addItem(Item item) {
		if (item.getId() > 0 && item.getAmount() > 0) {
			player.getInventory().add(item);
			Player playerToDuel = duelingWith >= 0 ? World.getPlayers().get(duelingWith) : null;
			String partner = playerToDuel != null ? playerToDuel.getUsername() : "null";
			Logger.log(player.getUsername(), "Received item from finished stake: "+item.getDefinition().getName()+" x"+Misc.insertCommasToNumber(String.valueOf(item.getAmount()))+" from stake partner: "+partner);
		}
	}

	public static boolean checkDuel(Player playerToDuel, int statusReq) {
		if(playerToDuel.getDueling().duelingStatus != statusReq || playerToDuel.getAttributes().isBanking() || playerToDuel.getAttributes().isShopping() || playerToDuel.getConstitution() <= 0 || playerToDuel.getAttributes().isResting() || FightPit.inFightPits(playerToDuel) || playerToDuel.getAdvancedSkills().getSummoning().isStoring())
			return false;
		return true;
	}

	public void reset() {
		inDuelWith = -1;
		duelingStatus = 0;
		inDuelScreen = false;
		duelRequested = false;
		for (int i = 0; i < selectedDuelRules.length; i++)
			selectedDuelRules[i] = false;
		player.getTrading().setCanOffer(true);
		player.getPacketSender().sendConfig(286, 0);
		stakedItems.clear();
		if(duelingWith >= 0) {
			Player playerToDuel = World.getPlayers().get(duelingWith);
			if(playerToDuel != null) {
				player.getPacketSender().sendInterfaceItems(6670, playerToDuel.getDueling().stakedItems);
				playerToDuel.getPacketSender().sendInterfaceItems(6670, player.getDueling().stakedItems);
			}
			player.getPacketSender().sendInterfaceItems(6669, player.getDueling().stakedItems);
		}
		duelingWith = -1;
		duelConfig = 0;
		duelTelePos = null;
		timer = 3;
		CombatHandler.resetAttack(player);
	}

	public int duelingStatus = 0;
	public int duelingWith = -1;
	public boolean inDuelScreen = false;
	public boolean duelRequested = false;
	public boolean[] selectedDuelRules = new boolean[DuelRule.values().length];
	public CopyOnWriteArrayList<Item> stakedItems = new CopyOnWriteArrayList<Item>();
	public int arenaStats[] = {0, 0};
	public int spaceReq = 0;
	public int duelConfig;
	public int timer = 3;
	public int inDuelWith = -1;

	public Object[] duelingData = new Object[2];
	protected Position duelTelePos = null;

	public static enum DuelRule {
		NO_RANGED(16, 6725, -1, -1),
		NO_MELEE(32, 6726, -1, -1),
		NO_MAGIC(64, 6727, -1, -1),
		NO_SPECIAL_ATTACKS(8192, 7816, -1, -1),
		FUN_WEPAONS_ONLY(4096, 670, -1, -1),
		NO_FORFEIT(1, 6721, -1, -1),
		NO_POTIONS(128, 6728, -1, -1),
		NO_FOOD(256, 6729, -1, -1),
		NO_PRAYER(512, 6730, -1, -1),
		NO_MOVEMENT(2, 6722, -1, -1),
		OBSTACLES(1024, 6732, -1, -1),

		NO_HELM(16384, 13813, 1, Equipment.HEAD_SLOT),
		NO_CAPE(32768, 13814, 1, Equipment.CAPE_SLOT),
		NO_AMULET(65536, 13815, 1, Equipment.AMULET_SLOT),
		NO_AMMUNITION(134217728, 13816, 1, Equipment.AMMUNITION_SLOT),
		NO_WEAPON(131072, 13817, 1, Equipment.WEAPON_SLOT),
		NO_BODY(262144, 13818, 1, Equipment.BODY_SLOT),
		NO_SHIELD(524288, 13819, 1, Equipment.SHIELD_SLOT),
		NO_LEGS(2097152, 13820, 1, Equipment.LEG_SLOT),
		NO_RING(67108864, 13821, 1, Equipment.RING_SLOT),
		NO_BOOTS(16777216, 13822, 1, Equipment.FEET_SLOT),
		NO_GLOVES(8388608, 13823, 1, Equipment.HANDS_SLOT);

		DuelRule(int configId, int buttonId, int inventorySpaceReq, int equipmentSlot) {
			this.configId = configId;
			this.buttonId = buttonId;
			this.inventorySpaceReq = inventorySpaceReq;
			this.equipmentSlot = equipmentSlot;
		}

		private int configId;
		private int buttonId;
		private int inventorySpaceReq;
		private int equipmentSlot;

		public int getConfigId() {
			return configId;
		}

		public int getButtonId() {
			return this.buttonId;
		}

		public int getInventorySpaceReq() {
			return this.inventorySpaceReq;
		}

		public int getEquipmentSlot() {
			return this.equipmentSlot;
		}

		public static DuelRule forId(int i) {
			for(DuelRule r : DuelRule.values()) {
				if(r.ordinal() == i)
					return r;
			}
			return null;
		}

		static DuelRule forButtonId(int buttonId) {
			for(DuelRule r : DuelRule.values()) {
				if(r.getButtonId() == buttonId)
					return r;
			}
			return null;
		}

		@Override
		public String toString() {
			return Misc.formatText(this.name().toLowerCase());
		}
	}
}
