package org.trident.world.content.skills.impl.prayer;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.Skill;
import org.trident.model.definitions.ItemDefinition;
import org.trident.model.inputhandling.impl.EnterAmountOfBonesToSacrifice;
import org.trident.world.entity.player.Player;


public class BonesOnAltar {

	public static void openInterface(Player player, int itemId) {
		player.getSkillManager().stopSkilling();
		player.getSkillManager().getSkillAttributes().setSelectedItem(itemId);
		player.getAttributes().setInputHandling(new EnterAmountOfBonesToSacrifice());
		player.getPacketSender().sendString(2799, ItemDefinition.forId(itemId).getName()).sendInterfaceModel(1746, itemId, 150) .sendChatboxInterface(4429);
		player.getPacketSender().sendString(2800, "How many would you like to offer?");
	}

	public static void offerBones(final Player player, final int amount) {
		final int boneId = player.getSkillManager().getSkillAttributes().getSelectedItem();
		player.getSkillManager().stopSkilling();
		final BonesData currentBone = BonesData.forId(boneId);
		if(currentBone == null)
			return;
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().getSkillAttributes().setCurrentTask(new Task(2, player, true) {
			int amountSacrificed = 0;
			@Override
			public void execute() {
				if(amountSacrificed >= amount) {
					stop();
					return;
				}
				if(!player.getInventory().contains(boneId)) {
					player.getPacketSender().sendMessage("You have run out of "+ItemDefinition.forId(boneId).getName()+".");
					stop();
					return;
				}
				if(player.getAttributes().getCurrentInteractingObject() != null) {
					player.setPositionToFace(player.getAttributes().getCurrentInteractingObject().getPosition().copy());
					player.getAttributes().getCurrentInteractingObject().performGraphic(new Graphic(624));
				}
				amountSacrificed++;
				player.getInventory().delete(boneId, 1);
				player.performAnimation(new Animation(713));
				player.getSkillManager().addExperience(Skill.PRAYER, currentBone.getAltarXP(), false);
			}
			@Override
			public void stop() {
				setEventRunning(false);
				player.getPacketSender().sendMessage("You have pleased the gods with your "+(amountSacrificed == 1 ? "sacrifice" : "sacrifices")+".");
			}
		});
		TaskManager.submit(player.getSkillManager().getSkillAttributes().getCurrentTask());
	}
}
