package org.trident.world.content.skills.impl.slayer;

import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.PlayerPanel;
import org.trident.world.entity.player.Player;

public enum SlayerMaster {
	
	VANNAKA(1, 1597, new Position(3145, 9912)),
	DURADEL(50, 8275, new Position(2824, 2959, 1)),
	KURADEL(80, 9085, new Position(1748, 5326));
	
	private SlayerMaster(int slayerReq, int npcId, Position position) {
		this.slayerReq = slayerReq;
		this.npcId = npcId;
		this.position = position;
	}
	
	private int slayerReq;
	private int npcId;
	private Position position;
	
	public int getSlayerReq() {
		return this.slayerReq;
	}
	
	public int getNpcId() {
		return this.npcId;
	}
	
	public Position getPosition() {
		return this.position;
	}
	
	public static SlayerMaster forId(int id) {
		for (SlayerMaster master : SlayerMaster.values()) {
			if (master.ordinal() == id) {
				return master;
			}
		}
		return null;
	}
	
	public static void changeSlayerMaster(Player player, SlayerMaster master) {
		player.getPacketSender().sendInterfaceRemoval();
		if(player.getSkillManager().getCurrentLevel(Skill.SLAYER) < master.getSlayerReq()) {
			player.getPacketSender().sendMessage("This Slayer master does not teach noobies. You need a Slayer level of at least "+master.getSlayerReq()+".");
			return;
		}
		if(player.getAdvancedSkills().getSlayer().getSlayerTask() != SlayerTasks.NO_TASK) {
			player.getPacketSender().sendMessage("Please finish your current task before changing Slayer master.");
			return;
		}
		if(player.getAdvancedSkills().getSlayer().getSlayerMaster() == master) {
			player.getPacketSender().sendMessage(""+Misc.formatText(master.toString().toLowerCase())+" is already your Slayer master.");
			return;
		}
		player.getAdvancedSkills().getSlayer().setSlayerMaster(master);
		PlayerPanel.refreshPanel(player);
		player.getPacketSender().sendMessage("You've sucessfully changed Slayer master.");
	}
}
