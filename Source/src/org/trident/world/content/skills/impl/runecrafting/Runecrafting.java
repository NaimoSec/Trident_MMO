package org.trident.world.content.skills.impl.runecrafting;

import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.world.content.Achievements;
import org.trident.world.content.skills.impl.runecrafting.RunecraftingData.TalismanData;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.entity.player.Player;

/**
 * Handles the Runecrafting skill
 * @author Gabbe
 */
public class Runecrafting {
	
	public static void craftRunes(final Player player, RunecraftingData.RuneData rune) {
		if(!canRuneCraft(player, rune))
			return;
		int essence = -1;
		if(player.getInventory().contains(1436) && !rune.pureRequired())
			essence = 1436;
		if(player.getInventory().contains(7936) && essence < 0)
			essence = 7936;
		if(essence == -1)
			return;
		player.performGraphic(new Graphic(186));
		player.performAnimation(new Animation(791));
		int amountToMake = RunecraftingData.getMakeAmount(rune, player);
		for(int i = 28; i > 0; i--) {
			if(!player.getInventory().contains(essence))
				break;
			player.getInventory().delete(essence, 1);
			player.getInventory().add(rune.getRuneID(), amountToMake);
			player.getSkillManager().addExperience(Skill.RUNECRAFTING, rune.getXP(), false);
		}
		player.performGraphic(new Graphic(129));
		player.getSkillManager().addExperience(Skill.RUNECRAFTING, rune.getXP(), false);
		player.getPacketSender().sendMessage("You bind the altar's power into "+rune.getName()+ "s..");
		if(rune.getRuneID() == 556)
			Achievements.handleAchievement(player, Achievements.Tasks.TASK11);
		player.getAttributes().setClickDelay(System.currentTimeMillis());
	}
	
	public static void handleTalisman(Player player, int ID) {
		TalismanData talisman = RunecraftingData.TalismanData.forId(ID);
		if(talisman == null)
			return;
		if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) < talisman.getLevelRequirement()) {
			player.getPacketSender().sendMessage("You need a Runecrafting level of at least " +talisman.getLevelRequirement()+ " to use this Talisman's teleport function.");
			return;
		}
		Position targetLocation = talisman.getLocation();
		TeleportHandler.teleportPlayer(player, targetLocation, player.getAttributes().getSpellbook().getTeleportType());
	}
	
	public static boolean canRuneCraft(Player player, RunecraftingData.RuneData rune) {
		if(rune == null)
			return false;
		if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) < rune.getLevelRequirement()) {
			player.getPacketSender().sendMessage("You need a Runecrafting level of at least " +rune.getLevelRequirement() + " to craft this.");
			return false;
		}
		if(rune.pureRequired() && !player.getInventory().contains(7936) && !player.getInventory().contains(1436)) {
			player.getPacketSender().sendMessage("You do not have any Pure essence in your inventory.");
			return false;
		} else if(rune.pureRequired() && !player.getInventory().contains(7936) && player.getInventory().contains(1436)) {
			player.getPacketSender().sendMessage("Only Pure essence has the power to bind this altar's energy.");
			return false;
		}
		if(!player.getInventory().contains(7936) && !player.getInventory().contains(1436)) {
			player.getPacketSender().sendMessage("You do not have any Rune or Pure essence in your inventory.");
			return false;
		}
		if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 4500)
			return false;
		return true;
	}
	
	public static boolean runecraftingAltar(Player player, int ID) {
		return ID >= 2478 && ID < 2489 || ID == 17010 || ID == 30624 || ID == 47120;
	}

}
