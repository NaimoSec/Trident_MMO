
package org.trident.world.content.skills.impl.slayer;

import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.definitions.NPCDefinition;
import org.trident.model.movement.MovementStatus;
import org.trident.util.Misc;
import org.trident.world.content.Achievements;
import org.trident.world.content.PlayerPanel;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.impl.SlayerDialogues;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.entity.player.Player;

/**
 * @author Gabbe
 */

public class Slayer {

	private Player player;
	public Slayer(Player p) {
		this.player = p;
		this.duoSlayer = new DuoSlayer(p);
	}

	public void assignTask() {
		boolean hasTask = getSlayerTask() != SlayerTasks.NO_TASK && player.getAdvancedSkills().getSlayer().getLastTask() != getSlayerTask();
		boolean duoSlayer = doingDuoSlayer && player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString() != null;
		if(duoSlayer && !player.getAdvancedSkills().getSlayer().getDuoSlayer().assignDuoSlayerTask())
			return;
		if(hasTask) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		int[] taskData = getNewTaskData();
		int slayerTaskId = taskData[0], slayerTaskAmount = taskData[1];
		SlayerTasks taskToSet = SlayerTasks.forId(slayerTaskId);
		while(taskToSet == player.getAdvancedSkills().getSlayer().getLastTask() || NPCDefinition.forId(taskToSet.getNpcId()).getSlayerLevelRequirement() > player.getSkillManager().getCurrentLevel(Skill.SLAYER)) {
			assignTask();
			return;
		}
		player.getAdvancedSkills().getSlayer().setAmountToSlay(slayerTaskAmount); 
		setSlayerTask(taskToSet);
		player.getPacketSender().sendInterfaceRemoval();
		DialogueManager.start(player, SlayerDialogues.receivedTask(player, getSlayerMaster(), getSlayerTask()));
		PlayerPanel.refreshPanel(player);
		if(duoSlayer) {
			Player duo = getDuoSlayer().getDuoPartner();
			duo.getAdvancedSkills().getSlayer().setSlayerTask(taskToSet);
			duo.getAdvancedSkills().getSlayer().setAmountToSlay(slayerTaskAmount);
			duo.getPacketSender().sendInterfaceRemoval();
			duo.getAdvancedSkills().getSlayer().doingDuoSlayer = true;
			DialogueManager.start(duo, SlayerDialogues.receivedTask(duo, duo.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartner().getAdvancedSkills().getSlayer().getSlayerMaster(), duo.getAdvancedSkills().getSlayer().getSlayerTask()));
			PlayerPanel.refreshPanel(duo);
			player.getAdvancedSkills().getSlayer().doingDuoSlayer = true;
		}
	}

	private int[] getNewTaskData() {
		int slayerTaskId = 1, slayerTaskAmount = 20;
		int easyTasks = 0, mediumTasks = 0, hardTasks = 0;
		/*
		 * Calculating amount of tasks
		 */
		for(SlayerTasks task: SlayerTasks.values()) {
			if(task.getTaskMaster() == SlayerMaster.VANNAKA)
				easyTasks++;
		}
		for(SlayerTasks task: SlayerTasks.values()) {
			if(task.getTaskMaster() == SlayerMaster.DURADEL)
				mediumTasks++;
		}
		for(SlayerTasks task: SlayerTasks.values()) {
			if(task.getTaskMaster() == SlayerMaster.KURADEL)
				hardTasks++;
		}
		/*
		 * Getting a task
		 */
		if(getSlayerMaster() == SlayerMaster.VANNAKA) {
			slayerTaskId = 1 + Misc.getRandom(easyTasks);
			if(slayerTaskId > easyTasks)
				slayerTaskId = easyTasks;
			slayerTaskAmount = 15 + Misc.getRandom(20);
		} else if(getSlayerMaster() == SlayerMaster.DURADEL) {
			slayerTaskId = easyTasks - 1 + Misc.getRandom(mediumTasks);
			slayerTaskAmount = 22 + Misc.getRandom(60);
		} else if(getSlayerMaster() == SlayerMaster.KURADEL) {
			slayerTaskId = 1 + easyTasks + mediumTasks + Misc.getRandom(hardTasks - 1);
			slayerTaskAmount = 20 + Misc.getRandom(30);
		}
		return new int[] {slayerTaskId, slayerTaskAmount};
	}

	public void resetSlayerTask(boolean changingGameDifficulty) {
		SlayerTasks task = getSlayerTask();
		if(task == SlayerTasks.NO_TASK)
			return;
		setSlayerTask(SlayerTasks.NO_TASK);
		setAmountToSlay(0);
		setTaskStreak(0);
		if(!changingGameDifficulty)
			player.getPointsHandler().setSlayerPoints(player.getPointsHandler().getSlayerPoints() - 5, false);
		else
			setSlayerMaster(SlayerMaster.VANNAKA);
		PlayerPanel.refreshPanel(player);
		Player duo = getDuoSlayer().getDuoPartner();
		if(duo != null) {
			duo.getAdvancedSkills().getSlayer().setSlayerTask(SlayerTasks.NO_TASK);
			duo.getAdvancedSkills().getSlayer().setAmountToSlay(0);
			duo.getAdvancedSkills().getSlayer().setTaskStreak(0);
			duo.getPacketSender().sendMessage("Your partner exchanged 5 Slayer points to reset your team's Slayer task.");
			PlayerPanel.refreshPanel(duo);
			player.getPacketSender().sendMessage("You've successfully reset your team's Slayer task.");
		} else if(!changingGameDifficulty)
			player.getPacketSender().sendMessage("Your Slayer task has been reset.");
	}

	public void handleSlayerTaskDeath(boolean givexp) {
		if(getSlayerTask() != null && getSlayerTask() != SlayerTasks.NO_TASK) {
			int xp = getSlayerTask().getXP();
			if(player.getAdvancedSkills().getSlayer().getAmountToSlay() >= 1) {
				player.getAdvancedSkills().getSlayer().setAmountToSlay(player.getAdvancedSkills().getSlayer().getAmountToSlay() - 1);
			} else if(player.getAdvancedSkills().getSlayer().getAmountToSlay() < 1) {
				xp += Misc.getRandom(200);
				player.getPacketSender().sendMessage("");
				player.getPacketSender().sendMessage("You've completed your Slayer task! Return to a Slayer master for another one.");
				player.getAdvancedSkills().getSlayer().setTaskStreak(player.getAdvancedSkills().getSlayer().getTaskStreak() + 1);
				player.getAdvancedSkills().getSlayer().givePoints(getSlayerMaster());
				player.getAdvancedSkills().getSlayer().setLastTask(getSlayerTask());
				setSlayerTask(SlayerTasks.NO_TASK);
				player.getAdvancedSkills().getSlayer().setAmountToSlay(0);
				player.getAdvancedSkills().getSlayer().updatePanel();
				Achievements.handleAchievement(player, Achievements.Tasks.TASK24);
			}
			if(givexp)
				player.getSkillManager().addExperience(Skill.SLAYER, doubleSlayerXP ? xp * 2 : xp, false);
		}
	}

	public void updatePanel() { //Update the player panel with only  slayer stuff to prevent sending 10+ strings when it's not needed..
		player.getPacketSender().sendString(39162, "@or2@Slayer Master:  @yel@"+Misc.formatText(getSlayerMaster().toString().toLowerCase().replaceAll("_", " ")));
		if(getSlayerTask() == SlayerTasks.NO_TASK) 
			player.getPacketSender().sendString(39163, "@or2@Slayer Task:  @yel@"+Misc.formatText(getSlayerTask().toString().toLowerCase().replaceAll("_", " "))+"");
		else
			player.getPacketSender().sendString(39163, "@or2@Slayer Task:  @yel@"+Misc.formatText(getSlayerTask().toString().toLowerCase().replaceAll("_", " "))+"s");
		player.getPacketSender().sendString(39164, "@or2@Slayer Task Streak:  @yel@"+player.getAdvancedSkills().getSlayer().getTaskStreak()+"");
		if(player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString() != null && !player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString().equals("null"))
			player.getPacketSender().sendString(39165, "@or2@Slayer Duo Partner:  @yel@"+player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString()+"");
		else
			player.getPacketSender().sendString(39165, "@or2@Slayer Duo Partner:");
	}

	@SuppressWarnings("incomplete-switch")
	public void givePoints(SlayerMaster master) {
		int pointsReceived = 4;
		switch(master) {
		case DURADEL:
			pointsReceived = 7;
			break;
		case KURADEL:
			pointsReceived = 10;
			break;
		}
		int per5 = pointsReceived * 3;
		int per10 = pointsReceived * 5;
		if(player.getAdvancedSkills().getSlayer().getTaskStreak() == 5) {
			player.getPointsHandler().setSlayerPoints(per5, true);
			player.getPacketSender().sendMessage("You received "+per5+" Slayer points.");
		} else if(player.getAdvancedSkills().getSlayer().getTaskStreak() == 10) {
			player.getPointsHandler().setSlayerPoints(per10, true);
			player.getPacketSender().sendMessage("You received "+per10+" Slayer points and your Task Streak has been reset.");
			player.getAdvancedSkills().getSlayer().setTaskStreak(0);
		} else if(player.getAdvancedSkills().getSlayer().getTaskStreak() >= 0 && player.getAdvancedSkills().getSlayer().getTaskStreak() < 5 || player.getAdvancedSkills().getSlayer().getTaskStreak() >= 6 && player.getAdvancedSkills().getSlayer().getTaskStreak() < 10) {
			player.getPointsHandler().setSlayerPoints(pointsReceived, true);
			player.getPacketSender().sendMessage("You received "+pointsReceived+" Slayer points.");
		}
		player.getPointsHandler().refreshPanel();
	}

	public void handleSlayerRingTP(int itemId) {
		if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 4500)
			return;
		if(player.getMovementQueue().getMovementStatus() == MovementStatus.CANNOT_MOVE)
			return;
		SlayerTasks task = getSlayerTask();
		if(task == SlayerTasks.NO_TASK)
			return;
		Position slayerTaskPos = new Position(task.getTaskPosition().getX(), task.getTaskPosition().getY(), task.getTaskPosition().getZ());
		if(!TeleportHandler.checkReqs(player, slayerTaskPos))
			return;
		TeleportHandler.teleportPlayer(player, slayerTaskPos, player.getAttributes().getSpellbook().getTeleportType());
		Item slayerRing = new Item(itemId);
		player.getInventory().delete(slayerRing);
		if(slayerRing.getId() < 13288)
			player.getInventory().add(slayerRing.getId() + 1, 1);
		else
			player.getPacketSender().sendMessage("Your Ring of Slaying crumbles to dust.");
	}

	public boolean doingDuoSlayer = false;
	private int amountToSlay = 0;
	private int taskStreak = 0;
	private SlayerTasks lastTask = SlayerTasks.NO_TASK;

	public int getAmountToSlay() {
		return this.amountToSlay;
	}

	public void setAmountToSlay(int amountToSlay) {
		this.amountToSlay = amountToSlay;
	}

	public int getTaskStreak() {
		return this.taskStreak;
	}

	public void setTaskStreak(int taskStreak) {
		this.taskStreak = taskStreak;
	}

	public SlayerTasks getLastTask() {
		return this.lastTask;
	}

	public void setLastTask(SlayerTasks lastTask) {
		this.lastTask = lastTask;
	}

	private DuoSlayer duoSlayer; 

	public DuoSlayer getDuoSlayer() {
		return this.duoSlayer;
	}

	public boolean doubleSlayerXP = false;

	public static boolean handleRewardsInterface(Player player, int button) {
		if(player.getAttributes().getInterfaceId() == 36000) {
			switch(button) {
			case -29534:
				player.getPacketSender().sendInterfaceRemoval();
				break;
			case -29522:
				if(player.getPointsHandler().getSlayerPoints() < 10) {
					player.getPacketSender().sendMessage("You do not have 10 Slayer points.");
					return true;
				}
				player.getPointsHandler().refreshPanel();
				player.getPointsHandler().setSlayerPoints(-10, true);
				player.getSkillManager().addExperience(Skill.SLAYER, 2745, false);
				player.getPacketSender().sendMessage("You've bought 2745 Slayer XP for 10 Slayer points.");
				break;
			case -29519:
				if(player.getPointsHandler().getSlayerPoints() < 30) {
					player.getPacketSender().sendMessage("You do not have 30 Slayer points.");
					return true;
				}
				if(player.getInventory().getFreeSlots() == 0) {
					player.getPacketSender().sendMessage("Please get some free inventory space first.");
					return true;
				}
				player.getPointsHandler().setSlayerPoints(-30, true);
				player.getInventory().add(13281, 1);
				player.getPointsHandler().refreshPanel();
				player.getPacketSender().sendMessage("You've bought a Ring of Slaying for 30 Slayer points.");
				break;
			case -29510:
			case -29513:
				boolean arrows = button == -29510; int cost = arrows ? 35 : 65;
				if(player.getPointsHandler().getSlayerPoints() < cost) {
					player.getPacketSender().sendMessage("You do not have "+cost+" Slayer points.");
					return true;
				}
				if(player.getInventory().getFreeSlots() == 0) {
					player.getPacketSender().sendMessage("Please get some free inventory space first.");
					return true;
				}
				player.getPointsHandler().setSlayerPoints(-cost, true);
				Item item = arrows ? new Item(4160, 250) : new Item(13280, 250);
				player.getInventory().add(item, true);
				String itemName = item.getDefinition().getName();
				if(arrows)
					itemName += "s";
				player.getPointsHandler().refreshPanel();
				player.getPacketSender().sendMessage("You've bought 250 "+itemName+" for "+cost+" Slayer points.");
				break;
			case -29516:
				if(player.getPointsHandler().getSlayerPoints() < 300) {
					player.getPacketSender().sendMessage("You do not have 300 Slayer points.");
					return true;
				}
				if(player.getAdvancedSkills().getSlayer().doubleSlayerXP) {
					player.getPacketSender().sendMessage("You already have this buff.");
					return true;
				}
				player.getPointsHandler().setSlayerPoints(-300, true);
				player.getAdvancedSkills().getSlayer().doubleSlayerXP = true;
				player.getPointsHandler().refreshPanel();
				player.getPacketSender().sendMessage("You will now permanently receive double Slayer experience.");
				break;
			}
			player.getPacketSender().sendString(36030, "Current Points:   "+player.getPointsHandler().getSlayerPoints());
			return true;
		}
		return false;
	}

	private SlayerTasks slayerTask = SlayerTasks.NO_TASK;
	private SlayerMaster slayerMaster = SlayerMaster.VANNAKA;

	public SlayerTasks getSlayerTask() {
		return slayerTask;
	}

	public void setSlayerTask(SlayerTasks slayerTask) {
		this.slayerTask = slayerTask;
	}

	public SlayerMaster getSlayerMaster() {
		return slayerMaster;
	}

	public void setSlayerMaster(SlayerMaster master) {
		this.slayerMaster = master;
	}
}
