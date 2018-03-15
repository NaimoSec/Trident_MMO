package org.trident.net.packet.impl;

import org.trident.model.Skill;
import org.trident.model.definitions.ItemDefinition;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.Misc;
import org.trident.world.entity.player.Player;

public class ExamineItemPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		int item = packet.readShort();
		if(item == 995 || item == 18201) {
			player.getPacketSender().sendMessage(""+player.getInventory().getAmount(995)+"x shining coins.");
			return;
		}
		ItemDefinition itemDef = ItemDefinition.forId(item);
		if(itemDef != null) {
			player.getPacketSender().sendMessage(itemDef.getDescription());
			for (Skill skill : Skill.values()) {
				if (itemDef.getRequirement()[skill.ordinal()] > player.getSkillManager().getMaxLevel(skill)) {
					player.getPacketSender().sendMessage("@red@WARNING: You need " + new StringBuilder().append(skill.getName().startsWith("a") || skill.getName().startsWith("e") || skill.getName().startsWith("i") || skill.getName().startsWith("o") || skill.getName().startsWith("u") ? "an " : "a ").toString() + Misc.formatText(skill.getName()) + " level of at least " + itemDef.getRequirement()[skill.ordinal()] + " to wear this.");
				}
			}
		}
	}

}
