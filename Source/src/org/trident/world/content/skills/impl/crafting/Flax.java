package org.trident.world.content.skills.impl.crafting;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.GameObject;
import org.trident.model.Skill;
import org.trident.model.inputhandling.impl.EnterAmountToSpin;
import org.trident.util.Misc;
import org.trident.world.content.CustomObjects;
import org.trident.world.entity.player.Player;

public class Flax {
	
	private static final int FLAX_ID = 1779;
	
	public static void pickFlax(Player player, GameObject flax) {
		if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 1200)
			return;
		if(player.getInventory().isFull()) {
			player.getPacketSender().sendMessage("You don't have enough free inventory space.");
			return;
		}
		player.performAnimation(new Animation(827));
		player.getInventory().add(FLAX_ID, 1);
		player.getPacketSender().sendMessage("You pick some Flax..");
		flax.setPickAmount(flax.getPickAmount() + 1);
		if(Misc.getRandom(3) == 1 && flax.getPickAmount() >= 1 || flax.getPickAmount() >= 6) {
			player.getPacketSender().sendClientRightClickRemoval();
			flax.setPickAmount(0);
			CustomObjects.globalObjectRespawnTask(new GameObject(-1, flax.getPosition()), flax, 10);
		}
		player.getAttributes().setClickDelay(System.currentTimeMillis());
	}
	
	public static void showSpinInterface(Player player) {
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().stopSkilling();
		if(!player.getInventory().contains(1779)) {
			player.getPacketSender().sendMessage("You do not have any Flax to spin.");
			return;
		}
		player.getAttributes().setInputHandling(new EnterAmountToSpin());
		player.getPacketSender().sendString(2799, "Flax").sendInterfaceModel(1746, FLAX_ID, 150).sendChatboxInterface(4429);
		player.getPacketSender().sendString(2800, "How many would you like to make?");
	}
	
	public static void spinFlax(final Player player, final int amount) {
		if(amount <= 0)
			return;
		player.getSkillManager().stopSkilling();
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().getSkillAttributes().setCurrentTask(new Task(2, player, true) {
			int amountSpan = 0;
			@Override
			public void execute() {
				if(!player.getInventory().contains(FLAX_ID)) {
					stop();
					return;
				}
				player.getSkillManager().addExperience(Skill.CRAFTING, 169, false);
				player.performAnimation(new Animation(896));
				player.getInventory().delete(FLAX_ID, 1);
				player.getInventory().add(1777, 1);
				amountSpan++;
				if(amountSpan >= amount)
					stop();
			}
		});
		TaskManager.submit(player.getSkillManager().getSkillAttributes().getCurrentTask());
	}
}
