package org.trident.world.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.trident.world.entity.player.Player;

/**
 * This file manages player punishments, such as mutes, bans, etc.
 * @author Gabbe
 */

public class PlayerPunishment {
	
	/**
	 * Leads to directory where banned account files are stored.
	 */
	private static final String BAN_DIRECTORY = "./data/saves/punishment/bans/";
	
	/**
	 * Leads to directory where muted account files are stored.
	 */
	private static final String MUTE_DIRECTORY = "./data/saves/punishment/mutes/";
	/**
	 * The lists containing punishments
	 */
	public static ArrayList <String>IPSBanned = new ArrayList<String> ();
	
	public static ArrayList <String>IPSMuted= new ArrayList<String> ();
	
	/**
	 * Loads up all the player mutes located in {@code MUTE_DIRECTORY}.
	 * Does not load bans as it's not needed, they will be logged out and will not be able
	 * to log in.
	 */
	public static void init() {
		initializeList(BAN_DIRECTORY, "IPBans", IPSBanned);
		initializeList(MUTE_DIRECTORY, "IPMutes", IPSMuted);
	}
	
	
	/**
	 * Bans {@param target} if {@param player}'s rank has a higher privilege-level.
	 * @param player	The player banning the target.
	 * @param target	The player being muted.
	 * @param length	The length the ban will last.
	 * @param value		The amount of said length. (if length = Length.DAY and value = 1, they will be banned for 1 day).
	 */
	public static void ban(Player player, String target, String reason) {
		punish(player.getUsername(), target, reason, BAN_DIRECTORY+"playerbans/");
	}
	
	public static void mute(Player player, String target,String reason) {
		punish(player.getUsername(), target, reason, MUTE_DIRECTORY);
	}
	
	/**
	* Reads IP's off the text file and adds them into the list for further usage.
	**/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void initializeList(String directory, String file, ArrayList list) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(""+directory+""+file+".txt"));
			String data = null;
			try {
				while ((data = in.readLine()) != null) {
					list.add(data);
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void addBannedIP(String IP) {
		addToFile(""+BAN_DIRECTORY+"IPBans.txt", IP);
		IPSBanned.add(IP);
	}
	
	public static void addMutedIP(String IP) {
		addToFile(""+MUTE_DIRECTORY+"IPMutes.txt", IP);
		IPSMuted.add(IP);
	}
	
	/**
	 * Checks if a player is banned.
	 * @param directory	The player's name.
	 * @return			If <code>true</code> that means player is banned.
	 */
	public static boolean banned(String player) {
		File file = new File(BAN_DIRECTORY+"playerbans/" + player + ".txt");
		return file.exists();
	}
	
	public static boolean muted(String player) {
		File file = new File(MUTE_DIRECTORY + player + ".txt");
		return file.exists();
	}
	
	public static boolean IPBanned(String IP) {
		return IPSBanned.contains(IP);
	}
	
	public static boolean IPMuted(String IP) {
		return IPSMuted.contains(IP);
	}
	
	
	/**
	 * Bans <code>Player</code> and adds a file to directory.
	 * @param target	Player being banned.
	 * @param expires	Date which ban will expire.
	 * @param reason	Reason for being banned.
	 */
	
	private static void punish(String owner, String target, String reason, String directory) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(directory + target.toLowerCase() + ".txt"));
			writer.write("Year : " + Calendar.getInstance().get(Calendar.YEAR) + "\tMonth : "+ Calendar.getInstance().get(Calendar.MONTH) + "\tDay : "+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
			writer.newLine();
			writer.write("Punished by: "+owner+"");
			writer.newLine();
			writer.write("Reason:");
			writer.write(" ");
			writer.write(reason);
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void unban(String player) {
		File file = new File(BAN_DIRECTORY+"playerbans/" + player + ".txt");
		file.delete();
	}
	
	public static void unmute(String player) {
		File file = new File(MUTE_DIRECTORY + player + ".txt");
		file.delete();
	}
	
	/**
	 * Represents a length in which a player can be punished.
	 * 
	 * @author relex lawl
	 */
	public enum Length {
		SECONDS(Calendar.SECOND),
		MINUTES(Calendar.MINUTE),
		HOURS(Calendar.HOUR_OF_DAY),
		DAYS(Calendar.DATE),
		MONTHS(Calendar.MONTH),
		YEARS(Calendar.YEAR);
		
		/**
		 * The Length constructor.
		 * @param value		The value the length holds.
		 */
		private Length(int value) {
			this.value = value;
		}
		
		/**
		 * The value the length holds as declared in
		 * {@link java.util.Calendar}.
		 */
		@SuppressWarnings("unused")
		private int value;
		
		public Length forId(int i) {
			for(Length values : Length.values()) {
				if(values.ordinal() == i)
					return values;
			}
			return null;
		}
	}
	
	public static void deleteFromFile(String file, String name) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(file));
			ArrayList<String> contents = new ArrayList<String>();
			while(true) {
				String line = r.readLine();
				if(line == null) {
					break;
				} else {
					line = line.trim();
				}
				if(!line.equalsIgnoreCase(name)) {
					contents.add(line);
				}
			}
			r.close();
			BufferedWriter w = new BufferedWriter(new FileWriter(file));
			for(String line : contents) {
				w.write(line, 0, line.length());
				w.newLine();
			}
			w.flush();
			w.close();
		} catch (Exception e) {}
	}
	
	public static void addToFile(String file, String data) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
		    try {
				out.newLine();
				out.write(data);
		    } finally {
				out.close();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
