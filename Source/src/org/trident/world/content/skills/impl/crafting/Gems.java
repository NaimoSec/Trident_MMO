package org.trident.world.content.skills.impl.crafting;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Skill;
import org.trident.model.definitions.ItemDefinition;
import org.trident.model.inputhandling.impl.EnterAmountOfGemsToCut;
import org.trident.world.entity.player.Player;

public class Gems {

	enum GEM_DATA {
		
		OPAL(1625, 1609, 8, 347, new Animation(886)),
		JADE(1627, 1611, 13, 680, new Animation(886)),
		RED_TOPAZ(1629, 1613, 16, 790, new Animation(887)),
		SAPPHIRE(1623, 1607, 20, 1200, new Animation(888)),
		EMERALD(1621, 1605, 27, 1800, new Animation(889)),
		RUBY(1619, 1603, 34, 2600, new Animation(892)),
		DIAMOND(1617, 1601, 43, 3500, new Animation(886)),
		DRAGONSTONE(1631, 1615, 55, 4400, new Animation(885)),
		ONYX(6571, 6573, 67, 8513, new Animation(885));
		
		GEM_DATA(int uncutGem, int cutGem, int levelReq, int xpReward, Animation animation) {
			this.uncutGem = uncutGem;
			this.cutGem = cutGem;
			this.levelReq = levelReq;
			this.xpReward = xpReward;
			this.animation = animation;
		}
		
		private int uncutGem, cutGem, levelReq, xpReward;
		private Animation animation;

		public int getUncutGem() {
			return uncutGem;
		}

		public int getCutGem() {
			return cutGem;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getXpReward() {
			return xpReward;
		}

		public Animation getAnimation() {
			return animation;
		}
		
		public static GEM_DATA forUncutGem(int uncutGem) {
			for(GEM_DATA data : GEM_DATA.values()) {
				if(data.getUncutGem() == uncutGem)
					return data;
			}
			return null;
		}
	}
	
	public static void selectionInterface(Player player, int gem) {
		player.getPacketSender().sendInterfaceRemoval();
		GEM_DATA data = GEM_DATA.forUncutGem(gem);
		if(data == null)
			return;
		if (player.getSkillManager().getMaxLevel(Skill.CRAFTING) < data.getLevelReq()) {
			player.getPacketSender().sendMessage("You need a Crafting level of atleast "+ data.getLevelReq() +" to craft this gem.");
			return;
		}
		player.getSkillManager().getSkillAttributes().setSelectedItem(gem);
		player.getAttributes().setInputHandling(new EnterAmountOfGemsToCut());
		player.getPacketSender().sendString(2799, ItemDefinition.forId(gem).getName()).sendInterfaceModel(1746, gem, 150).sendChatboxInterface(4429);
		player.getPacketSender().sendString(2800, "How many would you like to craft?");
	}

	public static void cutGem(final Player player, final int amount, final int uncutGem) {
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().stopSkilling();
		final GEM_DATA data = GEM_DATA.forUncutGem(uncutGem);
		if(data == null)
			return;
		player.getSkillManager().getSkillAttributes().setCurrentTask(new Task(2, player, true) {
			int amountCut = 0;
			@Override
			public void execute() {
				if(!player.getInventory().contains(uncutGem)) {
					stop();
					return;
				}
				player.performAnimation(data.getAnimation());
				player.getInventory().delete(uncutGem, 1);
				player.getInventory().add(data.getCutGem(), 1);
				player.getSkillManager().addExperience(Skill.CRAFTING, data.getXpReward(), false);
				amountCut++;
				if(amountCut >= amount)
					stop();
			}
		});
		TaskManager.submit(player.getSkillManager().getSkillAttributes().getCurrentTask());
	}

}
