package org.trident.world.content.skills;

import org.trident.model.inputhandling.impl.EnterAmountOfBonesToSacrifice;
import org.trident.model.inputhandling.impl.EnterAmountOfBowsToString;
import org.trident.model.inputhandling.impl.EnterAmountOfGemsToCut;
import org.trident.model.inputhandling.impl.EnterAmountOfLeatherToCraft;
import org.trident.model.inputhandling.impl.EnterAmountToCook;
import org.trident.model.inputhandling.impl.EnterAmountToFletch;
import org.trident.model.inputhandling.impl.EnterAmountToSpin;
import org.trident.world.content.skills.impl.cooking.Cooking;
import org.trident.world.content.skills.impl.crafting.Flax;
import org.trident.world.content.skills.impl.crafting.Gems;
import org.trident.world.content.skills.impl.fletching.Fletching;
import org.trident.world.content.skills.impl.prayer.BonesOnAltar;
import org.trident.world.entity.player.Player;

public class ChatboxInterfaceSkillAction {

	public static void handleChatboxInterfaceButtons(Player player, int buttonId) {
		if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 3000 || handleInterfaces(player, buttonId))
			return;
		int amount = buttonId == 2799 ? 1 : buttonId == 2798 ? 5 : buttonId == 1747 ? 28 : -1;
		if(player.getAttributes().getInputHandling() == null || amount <= 0) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		if(player.getAttributes().getInputHandling() instanceof EnterAmountOfGemsToCut)
			Gems.cutGem(player, amount, player.getSkillManager().getSkillAttributes().getSelectedItem());
		else if(player.getAttributes().getInputHandling() instanceof EnterAmountToCook)
			Cooking.cook(player, player.getSkillManager().getSkillAttributes().getSelectedItem(), amount);
		else if(player.getAttributes().getInputHandling() instanceof EnterAmountToSpin)
			Flax.spinFlax(player, amount);
		else if(player.getAttributes().getInputHandling() instanceof EnterAmountOfBonesToSacrifice)
			BonesOnAltar.offerBones(player, amount);
		else if(player.getAttributes().getInputHandling() instanceof EnterAmountOfBowsToString)
			Fletching.stringBow(player, amount);
		player.getAttributes().setClickDelay(System.currentTimeMillis());
	}
	
	public static boolean handleInterfaces(Player player, int buttonId) {
		if(buttonId == 8886 || buttonId == 8890 || buttonId == 8894 || buttonId == 8871 || buttonId == 8875) { // Fletching X amount
			player.getAttributes().setInputHandling(EnterAmountOfLeatherToCraft.isCrafting(player) ? new EnterAmountOfLeatherToCraft(buttonId) : new EnterAmountToFletch(buttonId));
			player.getPacketSender().sendEnterAmountPrompt("How many would you like to make?");
			return true;
		}
		return false;
	}
}