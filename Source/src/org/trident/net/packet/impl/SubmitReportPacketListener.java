package org.trident.net.packet.impl;

import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.FileUtils;
import org.trident.util.Misc;
import org.trident.world.content.ReportPlayer;
import org.trident.world.entity.player.Player;

public class SubmitReportPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		String reportString = FileUtils.readString(packet.getBuffer());
		if(reportString == null || reportString.length() <= 1)
			return;
		try {
			String reporting = Misc.formatText(reportString.substring(0, reportString.indexOf("''")).toLowerCase());
			String reason = reportString.substring(reportString.indexOf("''") + 2, reportString.length());
			ReportPlayer.sendReport(player, reporting, reason);
		} catch(Exception e) {
			player.getPacketSender().sendMessage("An error occured. Please try again.");
		}
	}

}
