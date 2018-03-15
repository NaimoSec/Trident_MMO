package org.trident.world.content.combat.pvp;

import java.util.ArrayList;
import java.util.List;

import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.world.content.Locations.Location;
import org.trident.world.entity.player.Player;

/**
 * Handles pk point rewards
 * 
 * @author Arithium
 * 
 */
public class PkRewardSystem {

	/**
	 * Constructs a new Player instance
	 */
	private final Player player;

	/**
	 * The maximium amount of players you can kill before resetting your list
	 */
	private final int WAIT_LIMIT = 3;

	/**
	 * A list of all the players you have killed
	 */
	private List<String> killedPlayers = new ArrayList<String>();

	/**
	 * Constructs a new <code>PkRewardSystem</code> with a client instance
	 * 
	 * @param client
	 */
	public PkRewardSystem(Player player) {
		this.player = player;
	}

	/**
	 * Adds a client to the list
	 * 
	 * @param other
	 */
	public void add(Player other) {
		if(other == null)
			return;
		Logger.log(player.getUsername(), "Player killed the player: "+other.getUsername()+".");
		if (killedPlayers.size() >= WAIT_LIMIT) {
			/*
			 * Clears the list
			 */
			killedPlayers.clear();
			handleReward(other);
		} else {
			if (!killedPlayers.contains(other.getUsername()))
				handleReward(other);
			else
				player.getPacketSender().sendMessage("You have recently defeated " + other.getUsername() + ".");
		}
	}

	/**
	 * Gives the player a reward for defeating his opponent
	 * @param other
	 */
	private void handleReward(Player o) {
		if (!player.getHostAdress().equalsIgnoreCase(o.getHostAdress()) && player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
			killedPlayers.add(o.getUsername());
			player.getPacketSender().sendMessage(getRandomKillMessage(o.getUsername()));
			player.getPointsHandler().setPkPoints(1, true);
			player.getPacketSender().sendMessage("You've received a Pk point.");
			player.getPointsHandler().refreshPanel();
			Logger.log(player.getUsername(), "Player received reward for killing the player: "+o.getUsername()+".");
		}
	}

	public List<String> getKilledPlayers() {
		return killedPlayers;
	}

	public void setKilledPlayers(List<String> list) {
		killedPlayers = list;
	}

	/**
	 * Gets a random message after killing a player
	 * @param killedPlayer 		The player that was killed
	 */
	public static String getRandomKillMessage(String killedPlayer){
		int deathMsgs = Misc.getRandom(8);
		switch(deathMsgs) {
		case 0: return "With a crushing blow, you defeat "+killedPlayer+".";
		case 1: return "It's humiliating defeat for "+killedPlayer+".";
		case 2: return ""+killedPlayer+" didn't stand a chance against you.";
		case 3: return "You've defeated "+killedPlayer+".";
		case 4: return ""+killedPlayer+" regrets the day they met you in combat.";
		case 5: return "It's all over for "+killedPlayer+".";
		case 6: return ""+killedPlayer+" falls before you might.";
		case 7: return "Can anyone defeat you? Certainly not "+killedPlayer+".";
		case 8: return "You were clearly a better fighter than "+killedPlayer+".";
		}
		return null;
	}

}
