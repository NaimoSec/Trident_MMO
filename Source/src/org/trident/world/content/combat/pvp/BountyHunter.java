package org.trident.world.content.combat.pvp;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.world.World;
import org.trident.world.content.Achievements;
import org.trident.world.content.Locations.Location;
import org.trident.world.entity.player.Player;

/**
 * Handles Bounty Hunter System (PvP)
 * @author Gabbe
 */
public class BountyHunter {

	/*
	 * The minimum amount of Target Percentage required to receive a target.
	 */
	private static final int TARGET_PERCENTAGE_REQUIRED = 90; 

	/*
	 * The minimum amount of milliseconds to increase the player's target percentage
	 */
	private static final long TARGET_PERCENTAGE_INCREASEMENT_TIMER = 15000;

	/**
	 * Handles a player's login.
	 * @param player	The player to handle the login for.
	 */
	public static void handleLogin(Player player) {
		if(player.getLocation() != null && player.getLocation() == Location.WILDERNESS)
			processBountyHunter(player);
	}

	/**
	 * Handles a player's logout.
	 * @param player	The player to handle the logout for.
	 */
	public static void handleLogout(Player player) {
		if(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget() != null)
			resetTargets(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget(), player, false, "Your target logged out and has been reset.");
	}

	/**
	 * Handles the main event for the player while in Bounty hunter.
	 * Adds Earning Potential and Target Percentage.
	 * @param c	The player to add the Target percentage to.
	 */
	public static void processBountyHunter(final Player player) {
		if(player.getPlayerCombatAttributes().getBountyHunterAttributes().isEventRunning(1) || (player.getLocation() == null  || player.getLocation() != Location.WILDERNESS))
			return;
		player.getPlayerCombatAttributes().getBountyHunterAttributes().setEventRunning(1, true);
		TaskManager.submit(new Task(15, player, true) {
			@Override
			public void execute() {
				if(player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
					if(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget() == null) {
						if(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTargetPercentage() >= TARGET_PERCENTAGE_REQUIRED) {
							Player target = findTarget(player);
							if(target != null)
								assignTargets(player, target);
						}
					}
					if(System.currentTimeMillis() - player.getPlayerCombatAttributes().getBountyHunterAttributes().getLastTargetPercentageIncrease() >= TARGET_PERCENTAGE_INCREASEMENT_TIMER) {
						addTargetPercentage(player, 3);
						player.getPlayerCombatAttributes().getBountyHunterAttributes().setLastTargetPercentageIncrease(System.currentTimeMillis());
					}
					updateInterface(player);
				} else {
					player.getPlayerCombatAttributes().getBountyHunterAttributes().setEventRunning(1, false);
					this.stop();
				}
			}
		});
	}


	public static void processLeftWilderness(final Player player) {
		if(player.getPlayerCombatAttributes().getBountyHunterAttributes().isEventRunning(2)|| player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget() == null)
			return;
		player.getPlayerCombatAttributes().getBountyHunterAttributes().setEventRunning(2, true);
		TaskManager.submit(new Task(1, player, true) {
			@Override
			public void execute() {
				if(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget() != null) {
					if(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getLocation() == null || player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getLocation() != Location.WILDERNESS) {
						if(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPlayerCombatAttributes().getBountyHunterAttributes().getSafeTimer() == 300) {
							player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPacketSender().sendMessage("You have 5 minutes to return to the Wilderness, or you will lose your target.");
							player.getPacketSender().sendMessage("Your target has 5 minutes to return to the Wilderness, or they will lose you as target.");
						} else if(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPlayerCombatAttributes().getBountyHunterAttributes().getSafeTimer() == 240) {
							player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPacketSender().sendMessage("You have 4 minutes to return to the Wilderness, or you will lose your target.");
							player.getPacketSender().sendMessage("Your target has 4 minutes to return to the Wilderness, or they will lose you as target.");
						} else if(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPlayerCombatAttributes().getBountyHunterAttributes().getSafeTimer() == 180) {
							player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPacketSender().sendMessage("You have 3 minutes to return to the Wilderness, or you will lose your target.");
							player.getPacketSender().sendMessage("Your target has 3 minutes to return to the Wilderness, or they will lose you as target.");
						} else if(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPlayerCombatAttributes().getBountyHunterAttributes().getSafeTimer() == 120) {
							player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPacketSender().sendMessage("You have 2 minutes to return to the Wilderness, or you will lose your target.");
							player.getPacketSender().sendMessage("Your target has 2 minutes to return to the Wilderness, or they will lose you as target.");
						} else if(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPlayerCombatAttributes().getBountyHunterAttributes().getSafeTimer() == 60) {
							player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPacketSender().sendMessage("You have 1 minute to return to the Wilderness, or you will lose your target.");
							player.getPacketSender().sendMessage("Your target has 1 minute to return to the Wilderness, or they will lose you as target.");
						}
						if(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPlayerCombatAttributes().getBountyHunterAttributes().getSafeTimer() > 0)
							player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPlayerCombatAttributes().getBountyHunterAttributes().setSafeTimer(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPlayerCombatAttributes().getBountyHunterAttributes().getSafeTimer() - 1);
						if(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPlayerCombatAttributes().getBountyHunterAttributes().getSafeTimer() <= 0) {
							player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getPlayerCombatAttributes().getBountyHunterAttributes().setSafeTimer(0);
							BountyHunter.resetTargets(player, player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget(), false, "Your target was in safe-zone for too long and has been reset.");
							this.stop();
							return;
						}
					}
				} else {
					player.getPlayerCombatAttributes().getBountyHunterAttributes().setEventRunning(2, false);
					this.stop();
				}
			}
		});
	}	

	/**
	 * Finds a target for a player.
	 * @param c	The player to find a target for.
	 */
	public static Player findTarget(Player player) {
		for(Player players : World.getPlayers()) {
			if(players == null)
				continue;
			if(checkTarget(player, players))
				return players;
		}
		return null;
	}

	/**
	 * Can a player receive a target?
	 * @param player	The player to check status for.
	 * @return	Player's status.
	 */
	public static boolean checkTarget(Player player, Player target) {
		return target.getLocation() != null && target.getLocation() == Location.WILDERNESS && player.getLocation() != null && player.getLocation() == Location.WILDERNESS && (target.getSkillManager().getCombatLevel() == player.getSkillManager().getCombatLevel() || target.getSkillManager().getCombatLevel() == player.getSkillManager().getCombatLevel() - 1 || target.getSkillManager().getCombatLevel() == player.getSkillManager().getCombatLevel() +1) && target.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget() == null && player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget() == null && target.getIndex() != player.getIndex() && target.getPlayerCombatAttributes().getBountyHunterAttributes().getTargetPercentage() >= TARGET_PERCENTAGE_REQUIRED && player.getPlayerCombatAttributes().getBountyHunterAttributes().getTargetPercentage() >= TARGET_PERCENTAGE_REQUIRED;
	}

	/**
	 * Assigns targets for two players
	 * @param c	Player1 to assign target for.
	 * @param target	Player2 to assign target for.
	 */

	public static void assignTargets(Player player, Player target) {
		if(target == null || player == null)
			return;
		player.getPlayerCombatAttributes().getBountyHunterAttributes().setTarget(target);
		player.getPacketSender().sendMessage("You've been assigned a target!");
		player.getPacketSender().sendEntityHint(target);
		updateInterface(player);
		target.getPacketSender().sendMessage("You've been assigned a target!");
		target.getPacketSender().sendEntityHint(player);
		updateInterface(target);
		target.getPlayerCombatAttributes().getBountyHunterAttributes().setTarget(player);
		processLeftWilderness(player);
		processLeftWilderness(target);
	}

	/**
	 * Resets targets, because a player logged out or was defeated.
	 * @param c	The player to reset target for.
	 * @param leaver	The leaver to reset target for.
	 * @param string 
	 */
	public static void resetTargets(Player c, Player leaver, boolean killed, String string) {
		if(!killed) { //leaver logged out
			leaver.getPlayerCombatAttributes().getBountyHunterAttributes().setTarget(null);
			leaver.getPlayerCombatAttributes().getBountyHunterAttributes().setTargetPercentage(0);
			c.getPlayerCombatAttributes().getBountyHunterAttributes().setTarget(null);
			if(string != null)
				c.getPacketSender().sendMessage(string);
			updateInterface(c);
		} else {
			leaver.getPlayerCombatAttributes().getBountyHunterAttributes().setTarget(null);
			leaver.getPlayerCombatAttributes().getBountyHunterAttributes().setTargetPercentage(0);
			c.getPlayerCombatAttributes().getBountyHunterAttributes().setTarget(null);
			c.getPlayerCombatAttributes().getBountyHunterAttributes().setTargetPercentage(0);
			updateInterface(c);
			updateInterface(leaver);
			Achievements.handleAchievement(c, Achievements.Tasks.TASK26);
			c.getPlayerCombatAttributes();
		}
		c.getPacketSender().sendEntityHintRemoval(true);
		leaver.getPacketSender().sendEntityHintRemoval(true);
		c.getPlayerCombatAttributes().getBountyHunterAttributes().setSafeTimer(0);
		leaver.getPlayerCombatAttributes().getBountyHunterAttributes().setSafeTimer(0);
	}

	/**
	 * Updates the Interface for a player.
	 * @param player	The player to update the interface for.
	 */
	public static void updateInterface(Player player) {
		player.getPacketSender().sendString(25350, player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget() != null ? player.getPlayerCombatAttributes().getBountyHunterAttributes().getTarget().getUsername() : "");
		player.getPacketSender().sendString(25354, ""+player.getPlayerCombatAttributes().getBountyHunterAttributes().getTargetPercentage()+"%");
		if(player.getAppearance().getBountyHunterSkull() == -1)
			player.getAppearance().setBountyHunterSkull(2);
	}

	/**
	 * Adds Target Percentage to the assigned Player.
	 * @param c	The player to add Target Percentage for.
	 * @param amountToAdd	How much Target Percentage the player will receive.
	 */
	public static void addTargetPercentage(Player c, int amountToAdd) {
		if(c.getPlayerCombatAttributes().getBountyHunterAttributes().getTargetPercentage() + amountToAdd > 100 || c.getPlayerCombatAttributes().getBountyHunterAttributes().getTargetPercentage() == 100) {
			c.getPlayerCombatAttributes().getBountyHunterAttributes().setTargetPercentage(100);
		} else {
			c.getPlayerCombatAttributes().getBountyHunterAttributes().setTargetPercentage(c.getPlayerCombatAttributes().getBountyHunterAttributes().getTargetPercentage() + amountToAdd);
		}
	}
	
	public static class BountyHunterAttributes {
		public BountyHunterAttributes() {
			
		}
		private Player target;
		private int targetPercentage;
		private long lastPercentageIncrease;
		private boolean[] eventsRunning = new boolean[3];
		private int safeTimer = 300;
		
		public Player getTarget() {
			return target;
		}
		
		public void setTarget(Player target) {
			this.target = target;
		}
		
		public int getTargetPercentage() {
			return targetPercentage;
		}
		
		public void setTargetPercentage(int targetPercentage) {
			this.targetPercentage = targetPercentage;
		}
		
		public long getLastTargetPercentageIncrease() {
			return lastPercentageIncrease;
		}
		
		public void setLastTargetPercentageIncrease(long lastPercentageIncrease) {
			this.lastPercentageIncrease = lastPercentageIncrease;
		}
		
		public boolean isEventRunning(int index) {
			return eventsRunning[index];
		}
		
		public void setEventRunning(int index, boolean running) {
			this.eventsRunning[index] = running;
		}
		
		public int getSafeTimer() {
			return safeTimer;
		}
		
		public void setSafeTimer(int safeTimer) {
			this.safeTimer = safeTimer;
		}
	}
}
