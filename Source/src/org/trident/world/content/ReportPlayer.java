package org.trident.world.content;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import org.trident.util.Misc;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;
import org.trident.world.entity.player.PlayerSaving;

public class ReportPlayer {
	
	public static void init(Player player) {
		if(System.currentTimeMillis() - player.getAttributes().getLastReport() <= 300000) {
			player.getPacketSender().sendMessage("You recently sent a report and must wait before sending another one.");
			return;
		}
		player.getPacketSender().sendInterface(10000);
		player.getPacketSender().sendString(10004, "");
		player.getPacketSender().sendString(10006, "");
		player.getPacketSender().sendString(10007, "");
		player.getPacketSender().sendString(10008, "");
	}
	
	public static void sendReport(Player player, String reporting, String reason) {
		if(reporting.length() == 0 || reporting.equals(player.getUsername())) {
			player.getPacketSender().sendMessage("Please enter a valid player to report.");
			return;
		} else if(reporting.length() > 0 && !PlayerSaving.playerExists(reporting)) {
			player.getPacketSender().sendMessage("The player "+Misc.formatText(reporting)+" was not found.");
			return;
		}
		if(reason.length() == 0) {
			player.getPacketSender().sendMessage("Please enter a valid reason for this report.");
			return; 
		}
		Calendar C = Calendar.getInstance();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("./data/saves/reports/Reports.txt", true));
			try {
				writer.newLine();
				writer.write("----------------------------------------------");
				writer.write("Year : " + C.get(Calendar.YEAR) + "\tMonth : "+ C.get(Calendar.MONTH) + "\tDay : "	+ C.get(Calendar.DAY_OF_MONTH));
				writer.newLine();
				writer.write("Player "+player.getUsername()+" has reported "+reporting+".");
				writer.newLine();
				writer.write("Player "+player.getUsername()+" submitted the following reason for this report:");
				writer.newLine();
				writer.newLine();
				writer.write(reason);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		player.getPacketSender().sendInterfaceRemoval();
		player.getPacketSender().sendMessage("");
		final String[] s = reporting.equals("Bug") ? new String[]{"a bug", "game"} : new String[]{"the player "+reporting+"", "community"};
		player.getPacketSender().sendMessage("You've successfully reported "+s[0]+".");
		player.getPacketSender().sendMessage("Our Staff members will view the report very shortly.");
		player.getPacketSender().sendMessage("Thank you for helping us keep the "+s[1]+" great!");
		player.getPacketSender().sendMessage("");
		player.getAttributes().setLastReport(System.currentTimeMillis());
		Player c2 = PlayerHandler.getPlayerForName(reporting);
		if(c2 != null) {
			c2.getPacketSender().sendMessage("");
			c2.getPacketSender().sendMessage("Your account has been reported.");
			c2.getPacketSender().sendMessage("We know that everyone has bad days, but try to make the best of them.");
			c2.getPacketSender().sendMessage("If you're sure that this was a false report, ignore this message.");
			c2.getPacketSender().sendMessage("");
		}
	}

}
