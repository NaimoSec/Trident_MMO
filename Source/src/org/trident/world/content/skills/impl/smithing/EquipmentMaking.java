package org.trident.world.content.skills.impl.smithing;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.Item;
import org.trident.model.Skill;
import org.trident.model.definitions.ItemDefinition;
import org.trident.world.content.Achievements;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.entity.player.Player;

public class EquipmentMaking {
	
	public static void handleAnvil(Player player) {
		String bar = searchForBars(player);
		if(bar == null) {
			player.getPacketSender().sendMessage("You do not have any bars in your inventory to smith.");
			return;
		} else {
			switch(bar.toLowerCase()) {
			case "bronze bar":
				player.getSkillManager().getSkillAttributes().setSelectedItem(2349);
				SmithingData.showBronzeInterface(player);
				break;
			case "iron bar":
				player.getSkillManager().getSkillAttributes().setSelectedItem(2351);
				SmithingData.makeIronInterface(player);
				break;
			case "steel bar":
				player.getSkillManager().getSkillAttributes().setSelectedItem(2353);
				SmithingData.makeSteelInterface(player);
				break;
			case "mithril bar":
				player.getSkillManager().getSkillAttributes().setSelectedItem(2359);
				SmithingData.makeMithInterface(player);
				break;
			case "adamant bar":
				player.getSkillManager().getSkillAttributes().setSelectedItem(2361);
				SmithingData.makeAddyInterface(player);
				break;
			case "rune bar":
				player.getSkillManager().getSkillAttributes().setSelectedItem(2363);
				SmithingData.makeRuneInterface(player);
				break;
			}
		}
	}
	
	public static String searchForBars(Player player) {
		for(int bar : SmithingData.SMELT_BARS) {
			if(player.getInventory().contains(bar)) {
				return ItemDefinition.forId(bar).getName();
			}
		}
		return null;
	}
	
	public static void smithItem(final Player player, final Item bar, final Item itemToSmith, final int x) {
		if(bar.getId() < 0)
			return;
		player.getSkillManager().stopSkilling();
		if(!player.getInventory().contains(2347)) {
			player.getPacketSender().sendMessage("You need a Hammer to smith items.");
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		if(player.getInventory().getAmount(bar.getId()) < bar.getAmount() || x <= 0) {
			player.getPacketSender().sendMessage("You do not have enough bars to smith this item.");
			return;
		}
		if(SmithingData.getData(itemToSmith, "reqLvl") > player.getSkillManager().getCurrentLevel(Skill.SMITHING)) {
			player.getPacketSender().sendMessage("You need a Smithing level of at least "+SmithingData.getData(itemToSmith, "reqLvl")+" to make this item.");
			return;
		}
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().getSkillAttributes().setCurrentTask(new Task(3, player, true) {
			int amountMade = 0;
			@Override
			public void execute() {
				if(player.getInventory().getAmount(bar.getId()) < bar.getAmount() || !player.getInventory().contains(2347) || amountMade >= x) {
					this.stop();
					return;
				}
				if(player.getAttributes().getCurrentInteractingObject() != null)
					player.getAttributes().getCurrentInteractingObject().performGraphic(new Graphic(2123));
				player.performAnimation(new Animation(898));
				amountMade++;
				player.getInventory().delete(bar);
				player.getInventory().add(itemToSmith);
				if(itemToSmith.getId() == 1205)
					Achievements.handleAchievement(player, Achievements.Tasks.TASK5);
				player.getInventory().refreshItems();
				player.getSkillManager().addExperience(Skill.SMITHING, (int) (SmithingData.getData(itemToSmith, "xp") * 1.5), false);
				SoundEffects.sendSoundEffect(player, SoundEffects.SoundData.SMITH_ITEM, 10, 3);
			}
		});
		TaskManager.submit(player.getSkillManager().getSkillAttributes().getCurrentTask());
	}
}
