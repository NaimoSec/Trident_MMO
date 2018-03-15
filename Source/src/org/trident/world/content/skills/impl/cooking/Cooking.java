package org.trident.world.content.skills.impl.cooking;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Skill;
import org.trident.model.definitions.ItemDefinition;
import org.trident.model.inputhandling.impl.EnterAmountToCook;
import org.trident.world.content.Achievements;
import org.trident.world.content.Locations.Location;
import org.trident.world.entity.player.Player;

public class Cooking {
	
	public static void selectionInterface(Player player, CookingData cookingData) {
		if(cookingData == null)
			return;
		player.getSkillManager().getSkillAttributes().setSelectedItem(cookingData.getRawItem());
		player.getAttributes().setInputHandling(new EnterAmountToCook());
		player.getPacketSender().sendString(2799, ItemDefinition.forId(cookingData.getCookedItem()).getName()).sendInterfaceModel(1746, cookingData.getCookedItem(), 150).sendChatboxInterface(4429);
		player.getPacketSender().sendString(2800, "How many would you like to cook?");
	}
	
	public static void cook(final Player player, final int rawFish, final int amount) {
		final CookingData fish = CookingData.forFish(rawFish);
		if(fish == null)
			return;
		player.getSkillManager().stopSkilling();
		player.getPacketSender().sendInterfaceRemoval();
		if(!CookingData.canCook(player, rawFish))
			return;
		player.performAnimation(new Animation(896));
		player.getSkillManager().getSkillAttributes().setCurrentTask(new Task(2, player, false) {
			int amountCooked = 0;
			@Override
			public void execute() {
				if(!CookingData.canCook(player, rawFish)) {
					stop();
					return;
				}
				player.performAnimation(new Animation(896));
				player.getInventory().delete(rawFish, 1);
				if(!CookingData.success(player, 3, fish.getLevelReq(), fish.getStopBurn())) {
					player.getInventory().add(fish.getBurntItem(), 1);
					player.getPacketSender().sendMessage("You accidently burn the "+fish.getName()+".");
				} else {
					player.getInventory().add(fish.getCookedItem(), 1);
					player.getSkillManager().addExperience(Skill.COOKING, fish.getXp() * Skill.COOKING.getExperienceMultiplier(), false);
				}
				if(player.getLocation() == Location.LUMBRIDGE && fish.getCookedItem() == 379 && player.getAttributes().getCurrentInteractingObject() != null && player.getAttributes().getCurrentInteractingObject().getId() == 114)
					Achievements.handleAchievement(player, Achievements.Tasks.TASK13);
				amountCooked++;
				if(amountCooked >= amount)
					stop();
			}
			@Override
			public void stop() {
				setEventRunning(false);
				player.getSkillManager().getSkillAttributes().setSelectedItem(-1);
				player.performAnimation(new Animation(65535));
			}		
		});
		TaskManager.submit(player.getSkillManager().getSkillAttributes().getCurrentTask());
	}
}
