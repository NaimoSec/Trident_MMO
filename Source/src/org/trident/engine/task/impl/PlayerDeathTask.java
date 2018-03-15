package org.trident.engine.task.impl;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.engine.task.Task;
import org.trident.model.Animation;
import org.trident.model.GroundItem;
import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.movement.MovementStatus;
import org.trident.util.Constants;
import org.trident.util.Logger;
import org.trident.world.content.Gravestones;
import org.trident.world.content.ItemLending;
import org.trident.world.content.ItemsKeptOnDeath;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.CombatExtras;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.pvp.PvPDrops;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerSaving;

/**
 * Represents a player's death task, through which the process of dying is handled,
 * the animation, dropping items, etc.
 * 
 * @author relex lawl, redone by Gabbe.
 */

public class PlayerDeathTask extends Task {

	/**
	 * The PlayerDeathTask constructor.
	 * @param player	The player setting off the task.
	 */
	public PlayerDeathTask(Player player) {
		super(1, player, false);
		this.player = player;
		this.loc = player.getLocation();
	}

	private Player player;
	private int ticks = 5;
	private boolean dropItems = true;
	Location loc;
	ArrayList<Item> itemsToKeep = null; 
	
	@Override
	public void execute() {
		try {
			Position oldPosition = player.getPosition().copy();
			switch (ticks) {
			case 5:
				player.getMovementQueue().stopMovement();
				player.getPacketSender().sendInterfaceRemoval();
				player.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
				GameCharacter enemy = player.getCombatAttributes().getCurrentEnemy();
				if(enemy == null)
					enemy = player.getCombatAttributes().getLastAttacker();
				if(enemy != null) {
					if(player.getCurseActive()[CurseHandler.WRATH] && !player.getAttributes().prayerIsDealingDamage())
						CombatExtras.handleWrath(player, enemy);
					else if(player.getPrayerActive()[PrayerHandler.RETRIBUTION] && !player.getAttributes().prayerIsDealingDamage())
						CombatExtras.handleRetribution(player, enemy);
				}
				break;
			case 3:
				player.getAttributes().setPrayerIsDealingDamage(false);
				player.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
				player.setAnimation(new Animation(9055));
				player.getPacketSender().sendMessage("Oh dear, you are dead!");
				break;
			case 1:
				if(loc != Location.DUEL_ARENA && loc != Location.FREE_FOR_ALL_ARENA && loc != Location.FREE_FOR_ALL_WAIT && loc != Location.SOULWARS && loc != Location.FIGHT_PITS && loc != Location.FIGHT_PITS_WAIT_ROOM && loc != Location.FIGHT_CAVES && loc != Location.CONQUEST) {
					if(player.getUsername().equalsIgnoreCase("gabbe") || player.getRights().equals(PlayerRights.ADMINISTRATOR) || player.getRights().equals(PlayerRights.OWNER) || player.getRights().equals(PlayerRights.DEVELOPER))
						dropItems = false;
					Player killer = CombatHandler.getKiller(player);
					if(loc == Location.WILDERNESS) {
						if(killer != null && (killer.getRights().equals(PlayerRights.ADMINISTRATOR) || killer.getRights().equals(PlayerRights.OWNER) || killer.getRights().equals(PlayerRights.DEVELOPER)))
							dropItems = false;
					}
					if(dropItems) {
						itemsToKeep = ItemsKeptOnDeath.getItemsToKeep(player);
						if(player.getAdvancedSkills().getSummoning().getFamiliar() != null)
							player.getAdvancedSkills().getSummoning().resetFollower(true);
						final CopyOnWriteArrayList<Item> playerItems = new CopyOnWriteArrayList<Item>();
						playerItems.addAll(player.getInventory().getValidItems());
						playerItems.addAll(player.getEquipment().getValidItems());
						final Position position = player.getPosition();
						final boolean createGrave = !(loc == Location.DUNGEONEERING || loc == Location.WILDERNESS || loc == Location.NOMAD);
						for (Item item : playerItems) {
							if(player.getTrading().getItemLending().getBorrowedItem() != null && ItemLending.borrowedItem(player, item.getId())) {
								ItemLending.returnBorrowedItem(player.getTrading().getItemLending().getBorrowedItem());
								Logger.log(player.getUsername(), "Player died with a borrowed item. The item was returned.");
								continue;
							}
							if(!item.tradeable() || itemsToKeep.contains(item)) {
								if(!itemsToKeep.contains(item))
									itemsToKeep.add(item);
								Logger.log(player.getUsername(), "Player died, keeping item on death: "+item.getDefinition().getName()+", noted: "+item.getDefinition().isNoted()+", amount: "+item.getAmount());
								continue;
							}
							if((createGrave || player.getLocation() == Location.WILDERNESS) && item != null && item.getId() > 0 && item.getAmount() > 0) {
								GroundItemManager.spawnGroundItem(killer != null ? killer : player, new GroundItem(item, position, killer != null ? killer.getUsername() : player.getUsername(), player.getHostAdress(), false, createGrave ? 400 : 150, true, 150));
								Logger.log(player.getUsername(), "Player died, dropping item on death: "+item.getDefinition().getName()+", noted: "+item.getDefinition().isNoted()+", amount: "+item.getAmount());
							}
							if(item.getId() == 11283)
								player.getAttributes().setDragonFireCharges(0, false);
						}
						if(killer != null) {
							GroundItemManager.spawnGroundItem(killer, new GroundItem(new Item(526), position, killer.getUsername(), false, 150, true, 150));
							player.getPlayerCombatAttributes().setDeaths(player.getPlayerCombatAttributes().getDeaths() + 1);
							player.getPointsHandler().refreshPanel();
							killer.getPlayerCombatAttributes().setKills(killer.getPlayerCombatAttributes().getKills() + 1);
							killer.getPointsHandler().refreshPanel();
							PvPDrops.handleDrops(killer, player);
							killer.getPlayerCombatAttributes().getPkRewardSystem().add(player);
						} else if(createGrave) {
							GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(526), position, player.getUsername(), false, 20, true, 150));
							Gravestones.spawnGravestone(player);
						}
						player.getInventory().resetItems().refreshItems();
						player.getEquipment().resetItems().refreshItems();
					}
				} else
					dropItems = false;
				player.getCombatAttributes().setCurrentEnemy(null).setLastAttacker(null);
				break;
			case 0:
				player.restart();
				player.getMovementQueue().setMovementStatus(MovementStatus.NONE);
				if(dropItems && itemsToKeep != null) {
					for(Item it : itemsToKeep)
						player.getInventory().add(it);
				}
				loc.onDeath(player);
				if(loc != Location.DUNGEONEERING) {
					if(player.getPosition().equals(oldPosition))
						player.moveTo(Constants.DEFAULT_POSITION.copy());
				}
				PlayerSaving.save(player);
				player = null;
				oldPosition = null;
				stop();
				break;
			}
			ticks--;
		} catch(Exception e) {
			setEventRunning(false);
			e.printStackTrace();
			if(player != null) {
				player.moveTo(Constants.DEFAULT_POSITION.copy());
				player.setConstitution(player.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
			}	
		}
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
	}
}
