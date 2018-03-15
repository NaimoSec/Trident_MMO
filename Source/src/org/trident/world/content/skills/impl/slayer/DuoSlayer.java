package org.trident.world.content.skills.impl.slayer;

import org.trident.world.content.Locations;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;

public class DuoSlayer {

	public static void killedNpc(Player killer, NPC npc) {
		if(killer.getAdvancedSkills().getSlayer().getSlayerTask() != SlayerTasks.NO_TASK) {
			if(killer.getAdvancedSkills().getSlayer().getSlayerTask().getNpcId() == npc.getId())
				killer.getAdvancedSkills().getSlayer().handleSlayerTaskDeath(true);
			Player duo = killer.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartner();
			if(duo != null) {
				duo.getAdvancedSkills().getSlayer().handleSlayerTaskDeath(Locations.goodDistance(killer.getPosition(), duo.getPosition(), 20));
				duo.getAdvancedSkills().getSlayer().getDuoSlayer().updateDuoSlayer();
			}
		}
	}
	
	Player player;
	public DuoSlayer(Player player) {
		this.player = player;
	}

	public void updateDuoSlayer() {
		Player duo = getDuoPartner();
		if(duo != null) {
			if(duo.getAdvancedSkills().getSlayer().getSlayerMaster() != player.getAdvancedSkills().getSlayer().getSlayerMaster()) {
				player.getPacketSender().sendMessage("Your duo partner has changed Slayer master.");
				resetDuoTeam(player);
				return;
			}
			if(duo.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString() == null || !duo.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString().equalsIgnoreCase(player.getUsername())) {
				resetDuoTeam(player);
				return;
			}
			if(duo.getAdvancedSkills().getSlayer().getSlayerTask() != player.getAdvancedSkills().getSlayer().getSlayerTask() || player.getAdvancedSkills().getSlayer().getAmountToSlay() != duo.getAdvancedSkills().getSlayer().getAmountToSlay()) {
				player.getAdvancedSkills().getSlayer().setTaskStreak(duo.getAdvancedSkills().getSlayer().getTaskStreak());
				player.getAdvancedSkills().getSlayer().setSlayerTask(duo.getAdvancedSkills().getSlayer().getSlayerTask());
				player.getAdvancedSkills().getSlayer().setAmountToSlay(duo.getAdvancedSkills().getSlayer().getAmountToSlay());
			}
		}
	}

	public static void resetDuoTeam(Player player) {
		Player duo = player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartner();
		if(duo != null) 
			duo.getAdvancedSkills().getSlayer().getDuoSlayer().setDuoPartner(null);
		else {
			player.getAdvancedSkills().getSlayer().getDuoSlayer().setDuoPartner(null);
			player.getAdvancedSkills().getSlayer().doingDuoSlayer = false;
		}
		checkPartnerReset(player);
	}

	public static void checkPartnerReset(Player player) {
		Player duo = player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartner();
		if(duo != null) {
			if(player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString() == null || !player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString().equalsIgnoreCase(duo.getUsername()) || duo.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString() == null || !duo.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString().equalsIgnoreCase(player.getUsername())) {
				duo.getPacketSender().sendMessage("Your Slayer duo team has been disbanded.");
				duo.getAdvancedSkills().getSlayer().getDuoSlayer().setDuoPartner(null);
				duo.getAdvancedSkills().getSlayer().doingDuoSlayer = false;
				player.getPacketSender().sendMessage("Your Slayer duo team has been disbanded.");
				player.getAdvancedSkills().getSlayer().getDuoSlayer().setDuoPartner(null);
				player.getAdvancedSkills().getSlayer().doingDuoSlayer = false;
			}
		}
	}

	public boolean assignDuoSlayerTask() {
		player.getPacketSender().sendInterfaceRemoval();
		if(player.getAdvancedSkills().getSlayer().getSlayerTask() != SlayerTasks.NO_TASK) {
			player.getPacketSender().sendMessage("You already have a Slayer task.");
			return false;
		}
		Player partner = getDuoPartner();
		if(partner == null) {
			player.getPacketSender().sendMessage("");
			player.getPacketSender().sendMessage("You can only get a new Slayer task when you're duo partner is online.");
			return false;
		}
		if(partner.getAdvancedSkills().getSlayer().getSlayerTask() != SlayerTasks.NO_TASK) {
			player.getPacketSender().sendMessage("Your partner already has a Slayer task.");
			return false;
		}
		if(partner.getAdvancedSkills().getSlayer().getSlayerMaster() != player.getAdvancedSkills().getSlayer().getSlayerMaster()) {
			player.getPacketSender().sendMessage("You and your partner need to have the same Slayer master.");
			return false;
		}
		if(partner.getAttributes().getInterfaceId() > 0) {
			player.getPacketSender().sendMessage("Your partner must close all their open interfaces.");
			return false;
		}
		partner.getAdvancedSkills().getSlayer().getDuoSlayer().setDuoPartner(player.getUsername());
		partner.getAdvancedSkills().getSlayer().doingDuoSlayer = true;
		player.getAdvancedSkills().getSlayer().doingDuoSlayer = true;
		return true;
	}

	/*
	 * The duo partner
	 */
	private String duoPartner;

	public String getDuoPartnerString() {
		return duoPartner;
	}

	public Player getDuoPartner() {
		return duoPartner == null ? null : PlayerHandler.getPlayerForName(duoPartner);
	}

	public void setDuoPartner(String partner) {
		if(partner != null && partner.equals("null"))
			partner = null;
		duoPartner = partner;
	}

	public String invitationOwner = null;
}
