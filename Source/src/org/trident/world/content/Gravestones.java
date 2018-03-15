package org.trident.world.content;

import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.GravestoneTask;
import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.Skill;
import org.trident.world.World;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

/**
 * Gravestones
 * @author Gabbe
 */
public class Gravestones {

	public static class GravestoneAttributes {
		public GravestoneAttributes(){}
		private int minutes, seconds;
		private NPC gravestone;

		public int getMinutes() {
			return minutes;
		}

		public void setMinutes(int minutes) {
			this.minutes = minutes;
		}

		public int getSeconds() {
			return seconds;
		}

		public void setSeconds(int seconds) {
			this.seconds = seconds;
		}

		public NPC getGravestone() {
			return gravestone;
		}

		public void setGravestone(NPC gravestone) {
			this.gravestone = gravestone;
		}
	}

	/**
	 * Spawns a global NPC which is a gravestone on the server
	 * @param player 	Gravestone owner
	 */
	public static void spawnGravestone(Player player) {
		if(player.getAttributes().getGravestoneAttributes().getGravestone() != null)
			return;
		player.getAttributes().getGravestoneAttributes().setGravestone(new NPC(6565, player.getPosition().copy()));
		player.getAttributes().getGravestoneAttributes().getGravestone().getAttributes().setWalkingDistance(0);
		World.register(player.getAttributes().getGravestoneAttributes().getGravestone());
		player.getAttributes().getGravestoneAttributes().setMinutes(3);
		player.getAttributes().getGravestoneAttributes().setSeconds(40);
		TaskManager.submit(new GravestoneTask(player));
		player.getPacketSender().sendMessage("@red@A gravestone has been created on the spot where you died.");
		player.getPacketSender().sendMessage("@red@Other players can bless it to make it last longer.");
		player.getPacketSender().sendMessage("@red@If you demolish it, it will dissapear along with all items under it.");
		
	}

	/**
	 * Sends a gravestone's information to a player
	 * @param player	Player to send information to
	 * @param n			Gravestone to send information about to Player player
	 */
	public static void sendGraveInformation(Player player, NPC n) {
		Player graveOwner = getGravestoneOwner(player, n);
		if(graveOwner != null) {
			for(int i = 8145; i < 8196; i++)
				player.getPacketSender().sendString(i, "");
			player.getPacketSender().sendString(8136, "Close window");
			player.getPacketSender().sendString(8144, "In memory of "+graveOwner.getUsername()+"..");
			player.getPacketSender().sendString(8146, "Here lies "+graveOwner.getUsername()+"..");
			player.getPacketSender().sendString(8147, "This grave will collapse in:");
			player.getPacketSender().sendString(8148, "" + graveOwner.getAttributes().getGravestoneAttributes().getMinutes() + " minutes and "+graveOwner.getAttributes().getGravestoneAttributes().getSeconds()+" seconds.");
			player.getPacketSender().sendString(8149, "Note: You can bless it to make it last longer.");
			player.getPacketSender().sendInterface(8134);
		} else
			World.deregister(n);
	}

	/**
	 * Blesses a gravestone, causing it to last longer.
	 * @param player	Player blessing the gravestone
	 * @param n			Gravestone to bless
	 */
	public static void blessGravestone(Player player, NPC n) {
		if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < 70) {
			player.getPacketSender().sendMessage("You need a current Prayer level of at least 70 to bless this gravestone.");
			return;
		}
		Player graveOwner = getGravestoneOwner(player, n);
		if(graveOwner == player) {
			player.getPacketSender().sendMessage("You cannot bless your own gravestone.");
			return;
		}
		if(graveOwner != null) {
			player.setEntityInteraction(n);
			player.performAnimation(new Animation(645));
			n.performGraphic(new Graphic(1996));
			player.getPacketSender().sendMessage("You have blessed "+graveOwner.getUsername()+"'s gravestone, causing it to last longer.");
			player.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
			graveOwner.getAttributes().getGravestoneAttributes().setMinutes(graveOwner.getAttributes().getGravestoneAttributes().getMinutes() + 1);
			if(graveOwner.getAttributes().getGravestoneAttributes().getMinutes() > 2)
				graveOwner.getAttributes().getGravestoneAttributes().setMinutes(2);
			graveOwner.getPacketSender().sendMessage(""+player.getUsername()+" has blessed your gravestone.");
		}
	}

	/**
	 * Demolishes a Gravestone and removes it from the game
	 * @param player	Player which is demolishing the gravestone
	 * @param n			The gravestone to demolish
	 */
	public static void demolishGravestone(Player player, NPC n) {
		if(player.getAttributes().getGravestoneAttributes().getGravestone() != null && player.getAttributes().getGravestoneAttributes().getGravestone() == n)
			player.getAttributes().getGravestoneAttributes().setMinutes(-1);
		else
			player.getPacketSender().sendMessage("This is not your gravestone to demolish.");
	}

	/**
	 * Formats text to receive a proper timer on the interface
	 * @param minutes	Minutes to use in the format
	 * @param seconds	Seconds to use in the format
	 */
	public static String formatText(int minutes, int seconds) {
		return "" + minutes + ":" + (seconds >= 10 ? ""+seconds : "0"+seconds) + "";
	}

	/**
	 * Gets a gravestones owner
	 * @param player	Player trying to find the owner
	 * @param n			Gravestone to find the owner for
	 * @return			The gravestone's owner
	 */
	public static Player getGravestoneOwner(Player player, NPC n) {
		if(player.getAttributes().getGravestoneAttributes().getGravestone() != null && player.getAttributes().getGravestoneAttributes().getGravestone() == n)
			return player;
		for(Player plr : World.getPlayers()) {
			if(plr == null)
				continue;
			if(plr != null && plr.getAttributes().getGravestoneAttributes().getGravestone() != null && plr.getAttributes().getGravestoneAttributes().getGravestone() == n) {
				return plr;
			}
		}
		return null;
	}

}
