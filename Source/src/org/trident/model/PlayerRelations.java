package org.trident.model;

import java.util.ArrayList;
import java.util.List;

import org.trident.util.NameUtils;
import org.trident.world.World;
import org.trident.world.content.clan.ClanChatManager;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;

/**
 * This file represents a player's relation with other world entities,
 * this manages adding and removing friends who we can chat with and also
 * adding and removing ignored players who will not be able to message us or see us online.
 *
 * @author relex lawl
 * Redone a bit by Gabbe
 */

public class PlayerRelations {

	/**
	 * The player's current friend status, checks if others will be able to see them online.
	 */
	private PrivateChatStatus status = PrivateChatStatus.ON;

	/**
	 * This map contains the player's friends list.
	 */
	private List<Long> friendList = new ArrayList<Long>(200);

	/**
	 * This map contains the player's ignore list.
	 */
	private List<Long> ignoreList = new ArrayList<Long>(100);

	/**
	 * The player's current private message index.
	 */
	private int privateMessageId = 1;

	/**
	 * Gets the current private message index.
	 * @return	The current private message index + 1.
	 */
	public int getPrivateMessageId() {
		return privateMessageId++;
	}

	/**
	 * Sets the current private message index.
	 * @param privateMessageId	The new private message index value.	
	 * @return					The PlayerRelations instance.
	 */
	public PlayerRelations setPrivateMessageId(int privateMessageId) {
		this.privateMessageId = privateMessageId;
		return this;
	}

	public PlayerRelations setStatus(PrivateChatStatus status, boolean update) {
		this.status = status;
		if(update)
			updateLists(true);
		return this;
	}

	public PrivateChatStatus getStatus() {
		return this.status;
	}

	/**
	 * Gets the player's friend list.
	 * @return	The player's friends.
	 */
	public List<Long> getFriendList() {
		return friendList;
	}

	/**
	 * Gets the player's ignore list.
	 * @return	The player's ignore list.
	 */
	public List<Long> getIgnoreList() {
		return ignoreList;
	}

	/**
	 * Updates the player's friend list.
	 * @param online	If <code>true</code>, the players who have this player added, will be sent the notification this player has logged in.
	 * @return			The PlayerRelations instance.
	 */
	public PlayerRelations updateLists(boolean online) {
		sendFriends();
		if (status == PrivateChatStatus.OFF)
			online = false;
		player.getPacketSender().sendFriendStatus(2);
		for (Player players : World.getPlayers()) {
			if(players == null)
				continue;
			boolean temporaryOnlineStatus = online;
			if (players.getRelations().friendList.contains(player.getLongUsername())) {
				if (status.equals(PrivateChatStatus.FRIENDS_ONLY) && !friendList.contains(players.getLongUsername()) ||
						status.equals(PrivateChatStatus.OFF) || ignoreList.contains(players.getLongUsername())) {
					temporaryOnlineStatus = false;
				}
				players.getPacketSender().sendFriend(player.getLongUsername(), temporaryOnlineStatus ? 1 : 0);
			}
			boolean tempOn = true;
			if (player.getRelations().friendList.contains(players.getLongUsername())) {
				if (players.getRelations().status.equals(PrivateChatStatus.FRIENDS_ONLY) && !players.getRelations().getFriendList().contains(player.getLongUsername()) ||
						players.getRelations().status.equals(PrivateChatStatus.OFF) || player.getRelations().getIgnoreList().contains(players.getLongUsername())) {
					tempOn = false;
				}
				player.getPacketSender().sendFriend(players.getLongUsername(), tempOn ? 1 : 0);
			}	
		}
		player.getPacketSender().sendIgnoreList();
		return this;
	}

	public void sendFriends() {
		for(int i = 0; i < player.getRelations().getFriendList().size(); i++) {
			player.getPacketSender().sendFriend(player.getRelations().getFriendList().get(i), 0);
		}
	}

	/**
	 * Adds a player to the associated-player's friend list.
	 * @param username	The user name of the player to add to friend list.
	 */
	public void addFriend(Long username) {
		String name = NameUtils.longToString(username);
		if (friendList.size() >= 200) {
			player.getPacketSender().sendMessage("Your friend list is full!");
			return;
		}
		if (ignoreList.contains(username)) {
			player.getPacketSender().sendMessage("Please remove " + name + " from your ignore list first.");
			return;
		}
		if (friendList.contains(username)) {
			player.getPacketSender().sendMessage(name + " is already on your friends list!");
		} else {
			friendList.add(username);
			updateLists(true);
			Player friend = PlayerHandler.getPlayerForName(name);
			if (friend != null)
				friend.getRelations().updateLists(true);
			if(player.getAttributes().getClanChat() != null)
				ClanChatManager.updateList(player.getAttributes().getClanChat());
		}
	}

	/*
	 * Checks if a player is friend with someone.
	 */
	public boolean isFriendWith(String player) {
		return friendList.contains(NameUtils.stringToLong(player));
	}

	/**
	 * Deletes a friend from the associated-player's friends list.
	 * @param username	The user name of the friend to delete.
	 */
	public void deleteFriend(Long username) {
		if (friendList.contains(username)) {
			friendList.remove(username);
			if (!status.equals(PrivateChatStatus.ON)) {	
				Player ignored = PlayerHandler.getPlayerForName(NameUtils.longToString(username));
				if (ignored != null)
					ignored.getRelations().updateLists(false);
				updateLists(false);
				if(player.getAttributes().getClanChat() != null)
					ClanChatManager.updateList(player.getAttributes().getClanChat());
			}
		} else {
			player.getPacketSender().sendMessage("This player is not on your friends list!");
		}
	}

	/**
	 * Adds a player to the associated-player's ignore list.
	 * @param username	The user name of the player to add to ignore list.
	 */
	public void addIgnore(Long username) {
		String name = NameUtils.longToString(username);
		if (ignoreList.size() >= 100) {
			player.getPacketSender().sendMessage("Your ignore list is full!");
			return;
		}
		if (friendList.contains(username)) {
			player.getPacketSender().sendMessage("Please remove " + name + " from your friend list first.");
			return;
		}
		if (ignoreList.contains(username)) {
			player.getPacketSender().sendMessage(name + " is already on your ignore list!");
		} else {
			ignoreList.add(username);
			updateLists(false);
			Player ignored = PlayerHandler.getPlayerForName(name);
			if (ignored != null)
				ignored.getRelations().updateLists(false);
		}
	}

	/**
	 * Deletes an ignored player from the associated-player's ignore list.
	 * @param username	The user name of the ignored player to delete from ignore list.
	 */
	public void deleteIgnore(Long username) {
		if (ignoreList.contains(username)) {
			ignoreList.remove(username);
			updateLists(true);
			if (status.equals(PrivateChatStatus.ON)) {
				Player ignored = PlayerHandler.getPlayerForName(NameUtils.longToString(username));
				if (ignored != null)
					ignored.getRelations().updateLists(true);
			}
		} else {
			player.getPacketSender().sendMessage("This player is not on your ignore list!");
		}
	}

	/**
	 * Sends a private message to {@code friend}.
	 * @param friend	The player to private message.
	 * @param message	The message being sent in bytes.
	 * @param size		The size of the message.
	 */
	public void message(Player friend, byte[] message, int size) {
		if(friend == null || message == null)
			return;
		try {
			if (PlayerHandler.getPlayerForName(friend.getUsername()) == null || friend.getRelations().status.equals(PrivateChatStatus.FRIENDS_ONLY) && !friend.getRelations().friendList.contains(player) || friend.getRelations().status.equals(PrivateChatStatus.OFF)) {
				this.player.getPacketSender().sendMessage("This player is currently offline.");
				return;
			}
			if(status == PrivateChatStatus.OFF)
				setStatus(PrivateChatStatus.FRIENDS_ONLY, true);
			friend.getPacketSender().sendPrivateMessage(player.getLongUsername(), player.getRights(), message, size);
		} catch (Exception e) {
			e.printStackTrace();
			friend.getPacketSender().sendMessage("Failed to send message.");
		}
	}

	/**
	 * Represents a player's friends list status, whether
	 * others will be able to see them online or not.
	 */
	public static enum PrivateChatStatus {
		ON(990),
		FRIENDS_ONLY(991),
		OFF(992);

		PrivateChatStatus(int actionId) {
			this.actionId = actionId;
		}

		private int actionId;

		public int getActionId() {
			return this.actionId;
		}

		public static PrivateChatStatus forIndex(int i) {
			for(PrivateChatStatus status : PrivateChatStatus.values()) {
				if(status != null && status.ordinal() == i)
					return status;
			}
			return ON;
		}

		public static PrivateChatStatus forActionId(int id) {
			for(PrivateChatStatus status : PrivateChatStatus.values()) {
				if(status != null && status.getActionId() == id)
					return status;
			}
			return null;
		}
	}

	/**
	 * The PlayerRelations constructor.
	 * @param player	The associated-player.
	 */
	public PlayerRelations(Player player) {
		this.player = player;
	}

	/**
	 * The associated player.
	 */
	private Player player;
}
