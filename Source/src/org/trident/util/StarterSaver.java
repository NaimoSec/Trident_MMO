package org.trident.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Saves starters
 */
public class StarterSaver {


	private static final String STARTER_FILE = "./data/saves/starters/starters.txt";
	private static CopyOnWriteArrayList<String> LIST = new CopyOnWriteArrayList<String>();

	public static void init() {
		try {
			BufferedReader r = new BufferedReader(new FileReader(STARTER_FILE));
			while(true) {
				String line = r.readLine();
				if(line == null) {
					break;
				} else {
					line = line.trim();
				}
				LIST.add(line);
			}
			r.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void receivedStarter(String s) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(STARTER_FILE, true));
			writer.write(""+s);
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		LIST.add(s);
	}

	/**
	 * Gets the amount of times an IP has received a starting package
	 * @param IP	The IP to check the file for
	 * @return		The amount of times an IP has received starter
	 */
	public static int getStartersReceived(String s) {
		int starters = 0;
		for(String s2 : LIST) {
			if(s2.equals(s))
				starters++;
		}
		return starters;
	}
}
