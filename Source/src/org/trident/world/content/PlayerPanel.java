package org.trident.world.content;

import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.skills.impl.slayer.SlayerTasks;
import org.trident.world.entity.player.Player;

public class PlayerPanel {

	public static void sendPlayersOnline() {
		int playerAmount = (int) (World.getPlayers().size() * 1.7);
		for(Player plr : World.getPlayers()) {
			if(plr == null)
				continue;
			plr.getPacketSender().sendString(39159, "Players online:   [ @yel@"+playerAmount+"@yel@]");
		}
	}

	public static void refreshPanel(Player player) {
		/**
		 * Players online
		 * No need to send because players online are globally sent on a player's login or logout.
		 */
		/**
		 * Difficulty
		 */
		player.getPacketSender().sendString(39160, "@or2@Game Mode: @yel@"+(player.getDifficulty() != null ? Misc.formatText(player.getDifficulty().toString().toLowerCase()) : "None")+"");
		/**
		 * Player's name
		 */
		player.getPacketSender().sendString(39161, "@or2@Player Name:  @yel@"+player.getUsername());
		/**
		 * Player's rank
		 */
		player.getPacketSender().sendString(39162, "@or2@Player Rank:  @yel@"+Misc.formatText(player.getRights().toString().toLowerCase()));
		/**
		 * Slayer
		 */
		player.getPacketSender().sendString(39163, "@or2@Slayer Master:  @yel@"+Misc.formatText(player.getAdvancedSkills().getSlayer().getSlayerMaster().toString().toLowerCase().replaceAll("_", " ")));
		if(player.getAdvancedSkills().getSlayer().getSlayerTask() == SlayerTasks.NO_TASK) 
			player.getPacketSender().sendString(39164, "@or2@Slayer Task:  @yel@"+Misc.formatText(player.getAdvancedSkills().getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " "))+"");
		else
			player.getPacketSender().sendString(39164, "@or2@Slayer Task:  @yel@"+Misc.formatText(player.getAdvancedSkills().getSlayer().getSlayerTask().toString().toLowerCase().replaceAll("_", " "))+"s");
		player.getPacketSender().sendString(39165, "@or2@Slayer Task Streak:  @yel@"+player.getAdvancedSkills().getSlayer().getTaskStreak()+"");
		if(player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString() != null && !player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString().equals("null"))
			player.getPacketSender().sendString(39166, "@or2@Slayer Duo Partner:  @yel@"+player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString()+"");
		else
			player.getPacketSender().sendString(39166, "@or2@Slayer Duo Partner:");
		/**
		 * XP Lock
		 */
		player.getPacketSender().sendString(39167, "@or2@Experience Lock:  @yel@"+(player.getAttributes().experienceLocked() ? "Locked" : "Unlocked")+"");
		/**
		 * Points
		 */
		player.getPointsHandler().refreshPanel();
	}

}
