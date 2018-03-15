package org.trident.world.content;

import org.trident.model.Skill;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.impl.GiveSkillcapeDialogue;
import org.trident.world.entity.player.Player;
/**
 * Handles Skillcapes
 * @author Gabbe
 *
 */
public class Skillcapes {

	public static void buySkillcape(Player player, Skill skill, int skillcape, int skillcapeG, int hood, boolean dialogue, int npcIdFDialogue) {
		boolean boughtCape = false;
		player.getAttributes().setDialogueAction(-1);
		player.getPacketSender().sendInterfaceRemoval();
		if((player.getSkillManager().getMaxLevel(skill) >= 99 && skill.ordinal() != 3 && skill.ordinal() != 5) || (player.getSkillManager().getMaxLevel(skill) >= 990 && skill.ordinal() == 3) || (player.getSkillManager().getMaxLevel(skill) >= 990 && skill.ordinal() == 5)) {
			if(player.getInventory().getFreeSlots() < 2) {
				player.getPacketSender().sendMessage("You do not have enough free inventory space.");
				return;
			}
			boolean usePouch = player.getAttributes().getMoneyInPouch() >= 99000;
			if(usePouch || player.getInventory().getAmount(995) >= 99000) {
				if(!usePouch)
					player.getInventory().delete(995, 99000);
				else {
					player.getAttributes().setMoneyInPouch(player.getAttributes().getMoneyInPouch() - 99000);
					player.getPacketSender().sendString(8135, ""+player.getAttributes().getMoneyInPouch());
				}
				player.getInventory().add(hood, 1);
				player.getInventory().add(player.getSkillManager().hasAnother99(skill.ordinal()) ? skillcapeG : skillcape, 1);
				boughtCape = true;
			} else
				player.getPacketSender().sendMessage("You do not have enough coins.");
		}
		if(boughtCape && dialogue)
			DialogueManager.start(player, GiveSkillcapeDialogue.gaveSkillCape(npcIdFDialogue));
	}
}
