package org.trident.world.content;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Graphic;
import org.trident.model.definitions.NPCDefinition;
import org.trident.util.Misc;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

/**
 * Achievements
 * Note: I've only done Lumbridge/Draynor tasks.
 * @author Gabbe
 */
public class Achievements {

	public enum Difficulty {
		BEGINNER, EASY, MEDIUM, HARD;
	}

	public enum TaskCity {
		Lumbridge_and_Draynor;
	}

	public enum Tasks {
		TASK1(Difficulty.BEGINNER, TaskCity.Lumbridge_and_Draynor, "1. Adventurer's log", "Cut a log from a regular tree.", 1, new int[]{8147, 8148}),
		TASK2(Difficulty.BEGINNER, TaskCity.Lumbridge_and_Draynor, "2. Log-a-rhythm", "Set fire to a regular log.", 1, new int[]{8150, 8151}),
		TASK3(Difficulty.BEGINNER, TaskCity.Lumbridge_and_Draynor, "3. Heavy Metal", "Mine some tin.", 1, new int[]{8153, 8154}),
		TASK4(Difficulty.BEGINNER, TaskCity.Lumbridge_and_Draynor, "4. Bar One", "Smelt a Bronze bar.", 1, new int[]{8156, 8157}),
		TASK5(Difficulty.BEGINNER, TaskCity.Lumbridge_and_Draynor, "5. Cutting Edge Technology", "Smith a Bronze dagger.", 1, new int[]{8159, 8160}),
		TASK6(Difficulty.BEGINNER, TaskCity.Lumbridge_and_Draynor, "6. Armed and Dangerous", "Wield a Melee weapon.", 1, new int[]{8162, 8163}),
		TASK7(Difficulty.BEGINNER, TaskCity.Lumbridge_and_Draynor, "7. Armed and Reaching", "Wield a Ranged weapon.", 1, new int[]{8165, 8166}),
		TASK8(Difficulty.BEGINNER, TaskCity.Lumbridge_and_Draynor, "8. You Can Bank On Me", "Talk to the banker in Lumbridge Castle.", 1, new int[]{8168, 8169}),
		TASK9(Difficulty.BEGINNER, TaskCity.Lumbridge_and_Draynor, "9. Hang on to something", "Deposit an item into your bank.", 1, new int[]{8171, 8172}),
		TASK10(Difficulty.BEGINNER, TaskCity.Lumbridge_and_Draynor, "10. On Your Way", "Finish all Beginner tasks.", 2, new int[]{8174, 8175}),

		TASK11(Difficulty.EASY, TaskCity.Lumbridge_and_Draynor, "1. Slippery When In Air", "Craft an Air rune at the Air Altar.", 1, new int[]{8147, 8148}),
		TASK12(Difficulty.EASY, TaskCity.Lumbridge_and_Draynor, "2. Just Borrowing It", "Pickpocket a Man in Lumbridge.", 1, new int[]{8150, 8151}),
		TASK13(Difficulty.EASY, TaskCity.Lumbridge_and_Draynor, "3. A Meal Fit For A Duke", "Cook a Lobster on the range in Lumbridge Castle kitchen.", 1, new int[]{8153, 8154}),
		TASK14(Difficulty.EASY, TaskCity.Lumbridge_and_Draynor, "4. The Fruit of the Sea", "Sell a Raw Shrimp to the Lumbridge General Store.", 1, new int[]{8156, 8157}),
		TASK15(Difficulty.EASY, TaskCity.Lumbridge_and_Draynor, "5. Self Time", "Buy a Toy from Diango in Draynor Village.", 1, new int[]{8159, 8160}),
		TASK16(Difficulty.EASY, TaskCity.Lumbridge_and_Draynor, "6. Castle Power", "Climb to the highest point of Lumbridge.", 1, new int[]{8162, 8163}),
		TASK17(Difficulty.EASY, TaskCity.Lumbridge_and_Draynor, "7. Bovine Intervention", "Kill a cow for its hide.", 1, new int[]{8165, 8166}),
		TASK18(Difficulty.EASY, TaskCity.Lumbridge_and_Draynor, "8. Rest Up", "Restore some energy by resting.", 1, new int[]{8168, 8169}),
		TASK19(Difficulty.EASY, TaskCity.Lumbridge_and_Draynor, "9. Why Is It Walking?", "Kill an undead creature.", 1, new int[]{8171, 8172}),
		TASK20(Difficulty.EASY, TaskCity.Lumbridge_and_Draynor, "10. On Your Way", "Finish all easy tasks.", 2, new int[]{8174, 8175}),

		TASK21(Difficulty.MEDIUM, TaskCity.Lumbridge_and_Draynor, "1. Bandits Are No Good", "Kill a bandit in the Al-kharid pub.", 1, new int[]{8147, 8148}),
		TASK22(Difficulty.MEDIUM, TaskCity.Lumbridge_and_Draynor, "2. The Duellist", "Win a duel in the Duel Arena.", 1, new int[]{8150, 8151}),
		TASK23(Difficulty.MEDIUM, TaskCity.Lumbridge_and_Draynor, "3. The Warrior", "Enter the Warrior's Guild.", 1, new int[]{8153, 8154}),
		TASK24(Difficulty.MEDIUM, TaskCity.Lumbridge_and_Draynor, "4. The Slayer", "Finish a Slayer task.", 1, new int[]{8156, 8157}),
		TASK25(Difficulty.MEDIUM, TaskCity.Lumbridge_and_Draynor, "5. The Master", "Perform a Skillcape emote.", 1, new int[]{8159, 8160}),
		TASK26(Difficulty.MEDIUM, TaskCity.Lumbridge_and_Draynor, "6. The Hunter", "Kill your target in the Wilderness.", 1, new int[]{8162, 8163}),
		TASK27(Difficulty.MEDIUM, TaskCity.Lumbridge_and_Draynor, "7. Set The Score", "Obtain a Total level of 500.", 1, new int[]{8165, 8166}),
		TASK28(Difficulty.MEDIUM, TaskCity.Lumbridge_and_Draynor, "8. Free Food Is The Best", "Steal a Cake from an Ardougne stall.", 1, new int[]{8168, 8169}),
		TASK29(Difficulty.MEDIUM, TaskCity.Lumbridge_and_Draynor, "9. The Cursed Spirit", "Summon a Spirit Dreadfowl.", 1, new int[]{8171, 8172}),
		TASK30(Difficulty.MEDIUM, TaskCity.Lumbridge_and_Draynor, "10. The End Is Near", "Finish all Medium tasks.", 2, new int[]{8174, 8175});


		Tasks(Difficulty difficulty, TaskCity taskCity, String taskName, String taskDecription, int reward, int[] frameId) {
			this.difficulty = difficulty;
			this.taskCity = taskCity;
			this.taskName = taskName;
			this.taskDecription = taskDecription;
			this.reward = reward;
			this.frameId = frameId;
		}

		private Difficulty difficulty;
		private TaskCity taskCity;
		private String taskName;
		private String taskDecription;
		private int reward;
		private int[] frameId;

		public Difficulty getDifficulty() {
			return difficulty;
		}

		public String getName() {
			return taskName;
		}

		public String getDescription() {
			return taskDecription;
		}

		public int getReward() {
			return reward;
		}

		public int[] getFrameId() {
			return frameId;
		}

		public TaskCity getTaskCity() {
			return taskCity;
		}

		public static Tasks[] getTasks(TaskCity city, Difficulty difficulty) {
			int size = 0;
			for (Tasks achievements : Tasks.values())
				if(achievements != null && achievements.getTaskCity() == city && achievements.getDifficulty() == difficulty)
					size++;
			Tasks[] fixedTaskArray = new Tasks[size];
			int loop = 0;
			for(Tasks achievements : Tasks.values()) {
				if(achievements != null && achievements.getTaskCity() == city && achievements.getDifficulty() == difficulty) {
					fixedTaskArray[loop] = achievements;
					loop++;
				}
			}
			return fixedTaskArray;
		}

		public static Tasks forId(int i) {
			for(Tasks achievements : Tasks.values()) {
				if(achievements != null && achievements.ordinal() == i)
					return achievements;
			}
			return null;
		}
	}

	private static String getColor(Player player, Difficulty difficulty, TaskCity taskCity) {
		String color = "@red@";
		if(difficulty == null) {
			switch(taskCity) {
			case Lumbridge_and_Draynor:
				if(getColor(player, Difficulty.BEGINNER, taskCity) == "@red@" && getColor(player, Difficulty.EASY, taskCity) == "@red@" && getColor(player, Difficulty.MEDIUM, taskCity) == "@red@")
					return "@red@";
				if(getColor(player, Difficulty.BEGINNER, taskCity) == "@gre@" && getColor(player, Difficulty.EASY, taskCity) == "@gre@" && getColor(player, Difficulty.MEDIUM, taskCity) == "@gre@")
					return "@gre@";
			}
			return "@yel@";
		}
		int index;
		switch(difficulty) {
		case EASY:
			index = 19;
			break;
		case MEDIUM:
			index = 29;
			break;
		case HARD:
			index = 39;
			break;
		default:
			index = 9;
			break;
		}
		if(player.getAttributes().getAchievements()[index])
			return "@gre@";
		int completedTasks = 0;
		for(int i = index-9; i < index; i++) {
			if(player.getAttributes().getAchievements()[i])
				completedTasks++;
		}
		if(completedTasks > 0)
			return "@yel@";
		return color;
	}

	public static void initTab(Player player) {
		player.getPacketSender().sendString(15008, getColor(player, null, TaskCity.Lumbridge_and_Draynor)+"Lumbridge/Draynor");
		player.getPacketSender().sendString(15009, getColor(player, Difficulty.BEGINNER, TaskCity.Lumbridge_and_Draynor)+"Beginner");
		player.getPacketSender().sendString(15010, getColor(player, Difficulty.EASY, TaskCity.Lumbridge_and_Draynor)+"Easy");
		player.getPacketSender().sendString(15011, getColor(player, Difficulty.MEDIUM, TaskCity.Lumbridge_and_Draynor)+"Medium");
	}

	public static void handleAchievement(final Player player, Tasks task) {
		if(player.getAttributes().getAchievements()[task.ordinal()])
			return;
		player.getAttributes().getAchievements()[task.ordinal()] = true;
		player.performGraphic(new Graphic(1634));
		if(player.getAttributes().getInterfaceId() <= 0 && player.getAttributes().getWalkableInterfaceId() < 0 && !player.getTrading().inTrade() && !player.getAttributes().isPriceChecking() && !player.getDueling().inDuelScreen) {
			player.getPacketSender().sendString(50004, task.getName().substring(2));
			player.getPacketSender().sendWalkableInterface(50000);
			TaskManager.submit(new Task(8, player, false) {
				@Override
				public void execute() {
					player.getPacketSender().sendWalkableInterface(-1);
					stop();
				}
			});
		}
		player.getPacketSender().sendMessage("");
		player.getPacketSender().sendMessage("Congratulations! You've completed the task: "+task.getName().substring(2)+"!");
		String s = task.getReward () == 1 ? "" : "s";
		player.getPacketSender().sendMessage("You've received "+task.getReward()+" Achievement point"+s+".");
		player.getPointsHandler().setAchievementPoints(task.getReward(), true);
		player.getPointsHandler().refreshPanel();
		initTab(player);
		int index;
		switch(task.getDifficulty()) {
		case EASY:
			index = 19;
			break;
		case MEDIUM:
			index = 29;
			break;
		case HARD:
			index = 39;
			break;
		default:
			index = 9;
			break;
		}
		boolean finishedTask = false;
		for(int i = index-9; i < index; i++) {
			finishedTask = player.getAttributes().getAchievements()[i];
			if(!finishedTask)
				return;
		}
		if(finishedTask)
			handleAchievement(player, Tasks.forId(index));
	}

	public static void initInterface(Player player, TaskCity city, Difficulty difficulty) {
		if(player == null)
			return;
		if(player.getAttributes().getInterfaceId() > 0) {
			player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
			return;
		}
		Tasks[] tasks = Tasks.getTasks(city, difficulty);
		if(tasks == null)
			return;
		for(int i = 8145; i < 8196; i++)
			player.getPacketSender().sendString(i, "");
		player.getPacketSender().sendInterface(8134);
		player.getPacketSender().sendString(8136, "Close window");
		player.getPacketSender().sendString(8144, ""+getColor(player, difficulty, city)+"Achievements, "+city.toString().replaceAll("_", " ")+": "+Misc.formatText(difficulty.toString().toLowerCase())+".");
		player.getPacketSender().sendString(8145, "");
		/**
		 * Write tasks to interface
		 */
		for(Tasks achievements : tasks) {
			String[] text = {player.getAttributes().getAchievements()[achievements.ordinal()] ? "@str@" : "", achievements.getName()+":"};
			player.getPacketSender().sendString(achievements.getFrameId()[0], text[0]+text[1]);
			text[1] = achievements.getDescription();
			player.getPacketSender().sendString(achievements.getFrameId()[1], text[0]+text[1]);
		}
		player.getPacketSender().sendInterface(8134);
		tasks = null;
	}
	
	public static void killedNpc(Player killer, NPC npc, NPCDefinition definition) {
		if(definition != null) {
			String name = definition.getName().toLowerCase();
			if(name.contains("cow"))
				Achievements.handleAchievement(killer, Achievements.Tasks.TASK17);
			else if(name.contains("skeleton") || name.contains("zombie"))
				Achievements.handleAchievement(killer, Achievements.Tasks.TASK19);
		}
		if(npc.getId() == 1880) //Bandit
			Achievements.handleAchievement(killer, Achievements.Tasks.TASK21);
	}
}
