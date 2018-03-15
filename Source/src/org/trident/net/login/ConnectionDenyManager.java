package org.trident.net.login;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.trident.util.Stopwatch;

/**
 * Checks if connections should be allowed to connect or not.
 * @author Gabbe
 */

public class ConnectionDenyManager {
	
	public static void init() {
		loadHostBlacklist();
		loadBannedComputers();
	}

	/** BLACKLISTED CONNECTIONS SUCH AS PROXIES **/
	private static final String BLACKLIST_DIR = "./data/blockedhosts.txt";
	private static List<String> BLACKLISTED_HOSTNAMES = new ArrayList<String>();

	private static void loadHostBlacklist() {
		String word = null;
		try {
			BufferedReader in = new BufferedReader(
					new FileReader(BLACKLIST_DIR));
			while ((word = in.readLine()) != null)
				BLACKLISTED_HOSTNAMES.add(word.toLowerCase());
			in.close();
			in = null;
		} catch (final Exception e) {
			System.out.println("Could not load blacklisted hosts.");
		}
	}
	
	public static boolean isBlocked(String host) {
		return BLACKLISTED_HOSTNAMES.contains(host.toLowerCase());
	}
	
	/** BLACKLISTED HARDWARE NUMBERS **/
	private static final String BLACKLISTED_HW_DIR = "./data/blockedhardwares.txt";
	private static List<Integer> BLACKLISTED_HW = new ArrayList<Integer>();
	
	private static void loadBannedComputers() {
		String line = null;
		try {
			BufferedReader in = new BufferedReader(
					new FileReader(BLACKLISTED_HW_DIR));
			while ((line = in.readLine()) != null) {
				if(line.contains("="))
					BLACKLISTED_HW.add(Integer.parseInt(line.substring(line.indexOf("=")+1)));
			}
			in.close();
			in = null;
		} catch (final Exception e) {
			System.out.println("Could not load blacklisted hadware numbers.");
		}
	}
	
	public static void banComputer(String playername, int mac) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(BLACKLISTED_HW_DIR, true));
			writer.write(""+playername+"="+mac);
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!BLACKLISTED_HW.contains(mac))
			BLACKLISTED_HW.add(mac);
	}
	
	public static boolean isBlocked(int host) {
		return BLACKLISTED_HW.contains(host);
	}

	/** HOST THROTTLER **/
	private static final int MAX_CONNECTIONS_PER_HOST = 2;
	private static Map<String, Integer> hostMap = new ConcurrentHashMap<>();
	
	 /* Checks the host into the gateway.
	 * 
	 * @param host
	 *            the host that needs to be checked.
	 * @return true if the host can connect, false if they have reached or
	 *         surpassed the maximum amount of connections.
	 */
	public static boolean enter(String host) {

		// If the host is coming from the hosting computer we don't need to
		// check it.
		if (host.equals("127.0.0.1") || host.equals("localhost")) {
			return true;
		}

		// Makes sure this host is not connecting too fast.
		if (!throttleHost(host)) {
			return false;
		}

		// Retrieve the amount of connections this host has.
		Integer amount = hostMap.get(host);

		// If the host was not in the map, they're clear to go.
		if (amount == null) {
			hostMap.put(host, 1);
			return true;
		}

		// If they've reached or surpassed the connection limit, reject the
		// host.
		if (amount >= MAX_CONNECTIONS_PER_HOST) {
			return false;
		}

		// Otherwise, replace the key with the next value if it was present.
		hostMap.put(host, amount + 1);

		return true;
	}

	/**
	 * Unchecks the host from the gateway.
	 * 
	 * @param host
	 *            the host that needs to be unchecked.
	 */
	public static void exit(String host) {

		// If we're connecting locally, no need to uncheck.
		if (host.equals("127.0.0.1") || host.equals("localhost")) {
            return;
        }

		// Get the amount of connections stored for the host.
		Integer amount = hostMap.get(host);

		if (amount == 1) {

			// Remove the host from the map if it's at one connection.
			hostMap.remove(host);
			timeMap.remove(host);
			return;
		} else if (amount > 1) {

			// Otherwise decrement the amount of connections stored.
			hostMap.put(host, amount - 1);
		}
	}
	
	 private static Map<String, Stopwatch> timeMap = new ConcurrentHashMap<>();
	
	  /**
     * Makes sure the host can only connect a certain amount of times in a
     * certain time interval
     * 
     * @param host
     *            the host being throttled.
     * @return true if the host is allowed to pass.
     */
    public static boolean throttleHost(String host) {

        // If the host has connected once already we need to check if they are
        // allowed to connect again.
        if (timeMap.containsKey(host)) {

            // Get the time since the last connection.
            long time = timeMap.get(host).elapsed();

            // Get how many existing connections this host has.
            Integer connection = hostMap.get(host) == null ? 0
                : hostMap.get(host);

            // If the time since the last connection is less than
            // <code>THROTTLE_TIME_INTERVAL</code> and the amount of connections
            // is equal to or above the
            // <code>AMOUNT_OF_CONNECTIONS_PER_SECOND</code> then the host is
            // connecting too fast.
            if (time < 1000 && connection >= 1) {
            	System.out.println("Session request from " + host + " denied: connecting too fast!");
                return false;
            }

            // If the host has waited one second before connecting again the
            // timer is reset and the host is allowed to pass.
            timeMap.get(host).reset();
            return true;
        }

        // If the host is connecting for the first time (has no other clients
        // logged in) then the host is added to the the map with its own timer.
        timeMap.put(host, new Stopwatch().reset());
        return true;
    }
}
