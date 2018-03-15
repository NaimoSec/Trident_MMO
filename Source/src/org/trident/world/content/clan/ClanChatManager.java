package org.trident.world.content.clan;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.util.NameUtils;
import org.trident.world.content.PlayerPunishment;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

public class ClanChatManager {

	private static final String FILE_DIRECTORY = "./data/saves/clans/";

	private static ClanChat[] clans = new ClanChat[5000];

	public static ClanChat[] getClans() {
		return clans;
	}

	public static int getClan(String playername) {
		try {
			int index = 0;
			for (File file : (new File(FILE_DIRECTORY)).listFiles()) {
				if(!file.exists())
					continue;
				if(file.getName().equalsIgnoreCase(playername))
					return index;
				index++;
			}
		} catch(Exception e) {

		}
		return -1;
	}

	public static void init() {
		try {
			for (File file : (new File(FILE_DIRECTORY)).listFiles()) {
				if(!file.exists())
					continue;
				DataInputStream input = new DataInputStream(new FileInputStream(file));
				String name = input.readUTF();
				String owner = input.readUTF();
				int index = input.readShort();
				ClanChat clan = new ClanChat(name, owner, index);
				clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_ENTER, ClanChatRank.forId(input.read()));
				clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_KICK, ClanChatRank.forId(input.read()));
				int totalRanks = input.readShort();
				for (int i = 0; i < totalRanks; i++) {
					clan.getRankedNames().put(input.readUTF(), ClanChatRank.forId(input.read()));
				}
				clans[index] = clan;
				input.close();
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public static void writeFile(ClanChat clan) {
		try {
			File file = new File(FILE_DIRECTORY + clan.getOwner().getUsername());
			if (file.exists())
				file.createNewFile();
			DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
			output.writeUTF(clan.getName());
			output.writeUTF(clan.getOwner().getUsername());
			output.writeShort(clan.getIndex());
			output.write(clan.getRankRequirement()[0] != null ? clan.getRankRequirement()[0].ordinal() : -1);
			output.write(clan.getRankRequirement()[1] != null ? clan.getRankRequirement()[0].ordinal() : -1);
			output.writeShort(clan.getRankedNames().size());
			for (Entry<String, ClanChatRank> iterator : clan.getRankedNames().entrySet()) {
				String name = iterator.getKey();
				int rank = iterator.getValue().ordinal();
				output.writeUTF(name);
				output.write(rank);
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		for (ClanChat clan : clans) {
			if (clan != null) {
				writeFile(clan);
			}
		}
	}

	public static void createClan(Player player) {
		player.getPacketSender().sendInterfaceRemoval();
		if(player.getAttributes().hasClan()) {
			player.getPacketSender().sendMessage("You already have a clan chat.");
			return;
		}
		if (player.getAttributes().getClanChat() != null) {
			player.getPacketSender().sendMessage("You cannot create a clan chat because you're already in an active chat.");
			return;
		}
		File file = new File(FILE_DIRECTORY + player.getUsername());
		if (file.exists())
			file.delete();
		if(create(player)) {
			player.getPacketSender().sendMessage("You now have a clan chat. To enter the chat, simply use your name as keyword.");
			player.getAttributes().setClan(true);
		}
	}

	public static void deleteClan(Player player) {
		player.getPacketSender().sendInterfaceRemoval();
		if (player.getAttributes().getClanChat() != null) {
			player.getPacketSender().sendMessage("Please leave the chat you are in before doing this.");
			return;
		}
		if(!player.getAttributes().hasClan()) {
			player.getPacketSender().sendMessage("You do not have a clan chat.");
			return;
		}
		shutdown(player);
	}

	public static boolean create(Player player) {
		File file = new File(FILE_DIRECTORY + player.getUsername());
		if (file.exists()) {
			player.getPacketSender().sendMessage("Your clan channel is already public!");
			return false;
		}
		int index = getIndex();
		if (index == -1) {
			player.getPacketSender().sendMessage("Too many clan chats! Please contact an administrator and report this error.");
			return false;
		}
		clans[index] = new ClanChat(player, player.getUsername(), index);
		clans[index].getRankedNames().put(player.getUsername(), ClanChatRank.OWNER);
		writeFile(clans[index]);
		return true;
	}

	public static void join(Player player, String channel) {
		if(channel == null || channel.equals("null"))
			return;
	
		if (player.getAttributes().getClanChat() != null) {
			player.getPacketSender().sendMessage("You are already in a clan channel.");
			return;
		}
		File file = new File(FILE_DIRECTORY + channel);
		if (!file.exists()) {
			player.getPacketSender().sendMessage("The channel you tried to join does not exist.");
			return;
		}
		channel = channel.toLowerCase();
		for (ClanChat clan : clans) {
			if (clan != null && clan.getName().toLowerCase().equals(channel)) {
				join(player, clan);
				break;
			}
		}
	}

	public static void updateList(ClanChat clan) {
		for (ClanChatMember members : clan.getMembers()) {
			if (members != null && members.getPlayer() != null) {
				int childId = 29344;
				Player member = members.getPlayer();
				for (ClanChatMember others : clan.getMembers()) {
					if (others != null && others.getPlayer() != null) {

						/*
						 * Adding friend rank if friend with owner and player is reg player or donor
						 */
						if(others.getPlayer().getAttributes().getClanChat() != null && (others.getPlayer().getRights() == PlayerRights.PLAYER || others.getPlayer().getRights() == PlayerRights.DONATOR))
							if(others.getPlayer().getAttributes().getClanChat().getRank(others.getPlayer()) == null && others.getPlayer().getRelations().isFriendWith(others.getPlayer().getAttributes().getClanChat().getOwnerName()))
								others.getPlayer().getAttributes().getClanChat().getRankedNames().put(others.getPlayer().getUsername(), ClanChatRank.FRIEND);
							else if(others.getPlayer().getAttributes().getClanChat().getRank(others.getPlayer()) != null && others.getPlayer().getAttributes().getClanChat().getRank(others.getPlayer()) == ClanChatRank.FRIEND && ! others.getPlayer().getRelations().isFriendWith(others.getPlayer().getAttributes().getClanChat().getOwnerName()))
								others.getPlayer().getAttributes().getClanChat().getRankedNames().remove(others.getPlayer().getUsername());


						ClanChatRank rank = clan.getRank(others.getPlayer());
						if (others.getPlayer().getRights() == PlayerRights.OWNER || others.getPlayer().getRights() == PlayerRights.ADMINISTRATOR) {
							if(rank != ClanChatRank.OWNER)
								rank = ClanChatRank.SERVER_ADMINISTRATOR;
						}
						//String prefix = rank != null ? ("<clan=" + (rank.ordinal() + 230) +  "> ") : "";
						member.getPacketSender().sendString(childId, /*prefix + */others.getPlayer().getUsername());
						childId++;
					}
				}
				for (int i = childId; i < 29444; i++) {
					member.getPacketSender().sendString(i, "");
				}
			}
		}
	}

	public static void sendMessage(Player player, String message) {
		if(player.getHostAdress() != null && PlayerPunishment.IPMuted(player.getHostAdress()) || PlayerPunishment.muted(player.getUsername())) {
			player.getPacketSender().sendMessage("Muted players cannot talk in clan chat channels.");
			return;
		}
		ClanChat clan = player.getAttributes().getClanChat();
		if (clan == null) {
			player.getPacketSender().sendMessage("You're not in a clan channel.");
			return;
		}
		ClanChatRank rank = clan.getRank(player);
		if (clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK] != null && 
				rank.ordinal() < clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_TALK].ordinal()) {
			player.getPacketSender().sendMessage("You do not have the required rank to speak.");
			return;
		}
		for (ClanChatMember member : clan.getMembers()) {
			if (member != null && member.getPlayer() != null) {
				Player memberPlayer = member.getPlayer();
				if(memberPlayer.getRelations().getIgnoreList().contains(player.getLongUsername()))
					continue;
				String bracketColor = "<col=16777215>";
				String clanNameColor = "<col=255>";
				String nameColor = "<col=000000>";
				if (memberPlayer.getAttributes().getClanChatMessageColor().getRgbIndex() != 0) {
					bracketColor = "<col=FFFFFF>";
					clanNameColor = "<col=11263>";
					nameColor = "<col=FFFFFF>";
				}
				if(player.getAttributes().getClanChatMessageColor().getRgbIndex() >= memberPlayer.getAttributes().getClanChatMessageColor().getRGB().length)
					player.getAttributes().getClanChatMessageColor().setRgbIndex(0);
				memberPlayer.getPacketSender().sendMessage("@clan:A@" + bracketColor + "[" + clanNameColor + clan.getName() + bracketColor + "]" + nameColor + " <img="+player.getRights().ordinal()+"> " +
						NameUtils.capitalizeWords(player.getUsername()) +
						": <col=" + memberPlayer.getAttributes().getClanChatMessageColor().getRGB()[player.getAttributes().getClanChatMessageColor().getRgbIndex()] + ">" +
						NameUtils.capitalize(message));
			}
		}
	}

	public static void sendMessage(ClanChat clan, String message) {
		for (ClanChatMember member : clan.getMembers()) {
			if (member != null && member.getPlayer() != null) {
				member.getPlayer().getPacketSender().sendMessage(message);
			}
		}
	}

	public static void leave(Player player, boolean kicked) {
		ClanChat clan = player.getAttributes().getClanChat();
		if (clan == null) {
			player.getPacketSender().sendMessage("You are not in a clan channel.");
			return;
		}
		player.getPacketSender().sendString(29340, "Talking in: N/A");
		player.getPacketSender().sendString(29450, "Owner: N/A");
		player.getPacketSender().sendString(29454, "Lootshare: N/A");
		player.getAttributes().setClanChat(null);
		clan.removeMember(player.getUsername());
		for (int i = 29344; i < 29444; i++) {
			player.getPacketSender().sendString(i, "");
		}
		updateList(clan);
		player.getPacketSender().sendMessage(kicked ? "You have been kicked from the channel." : "You have left the channel.");

	}

	public static void resetInterface(Player player) {
		player.getPacketSender().sendString(29340, "Talking in: N/A");
		player.getPacketSender().sendString(29450, "Owner: N/A");
		player.getPacketSender().sendString(29454, "Lootshare: N/A");
		for (int i = 29344; i < 29444; i++) {
			player.getPacketSender().sendString(i, "");
		}
	}

	private static void join(Player player, ClanChat clan) {
		if (clan.getOwnerName().equals(player.getUsername())) {
			if (clan.getOwner() == null) {
				clan.setOwner(player);
			}
			clan.getRankedNames().put(player.getUsername(), ClanChatRank.OWNER);
		}
		player.getPacketSender().sendMessage("Attempting to join channel...");
		if (clan.getTotalMembers() >= 100) {
			player.getPacketSender().sendMessage("This clan channel is currently full.");
			return;
		}
		ClanChatRank rank = clan.getRank(player);
		if (player.getRights() == PlayerRights.OWNER || player.getRights() == PlayerRights.ADMINISTRATOR) {
			if(rank != ClanChatRank.OWNER)
				rank = ClanChatRank.SERVER_ADMINISTRATOR;
		}
		if (clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER] != null) {
			if (rank == null || clan.getRankRequirement()[ClanChat.RANK_REQUIRED_TO_ENTER].ordinal() > rank.ordinal()) {
				player.getPacketSender().sendMessage("Your rank is not high enough to enter this channel.");
				return;
			}
		}
		player.getAttributes().setClanChat(clan);
		player.getAttributes().setClanChatName(clan.getName());
		String clanName = NameUtils.capitalizeWords(clan.getName());
		ClanChatMember member = new ClanChatMember(player, clan, clan.getRank(player));
		clan.addMember(member);
		player.getPacketSender().sendString(29340, "Talking in: @whi@" + clanName);
		player.getPacketSender().sendString(29450, "Owner: " + NameUtils.capitalizeWords(clan.getOwnerName()));
		member.getPlayer().getPacketSender().sendString(29454, "Lootshare: "+getLootshareStatus(clan));
		player.getPacketSender().sendMessage("Now talking in channel " + clanName+".");
		player.getPacketSender().sendMessage("To talk start each line of chat with the / symbol.");
		/*if(rank == ClanChatRank.SERVER_ADMINISTRATOR || rank == ClanChatRank.OWNER)
			player.getPacketSender().sendMessage("Please use /commands for a list of clan chat commands.");*/
		updateList(clan);
	}

	public static void shutdown(Player player) {
		ClanChat clan = getClanChat(player);
		File file = new File(FILE_DIRECTORY + player.getUsername());
		if (clan == null || !file.exists()) {
			player.getPacketSender().sendMessage("You need to have a public clan channel to do this.");
			return;
		}
		for (ClanChatMember member : clan.getMembers()) {
			if (member != null) {
				leave(member.getPlayer(), true);
				member.getPlayer().getAttributes().setClanChatName("");
			}
		}
		player.getAttributes().setClanChatName("");
		player.getAttributes().setClan(false);
		player.getPacketSender().sendMessage("Your clan chat was successfully deleted.");
		clans[clan.getIndex()] = null;
		file.delete();
	}

	public static void setName(Player player, String newName) {
		final ClanChat clan = getClanChat(player);
		if (clan == null) {
			player.getPacketSender().sendMessage("You need to have a public clan channel to do this.");
			return;
		}
		if (newName.length() > 12)
			newName = newName.substring(0, 11);
		final String name = NameUtils.capitalizeWords(newName);
		String green = ClanChatMessageColor.GREEN.getRGB()[player.getAttributes().getClanChatMessageColor().getRgbIndex()];
		player.getPacketSender().sendMessage("<col=" + green + ">Changes will take effect in 60 seconds.");
		clan.applyChange(new ClanChatAction() {
			@Override
			public void execute() {
				clan.setName(name);
			}
		});
	}

	public static void kick(Player player, Player target) {
		ClanChat clan = player.getAttributes().getClanChat();
		if (clan == null) {
			player.getPacketSender().sendMessage("You're not in a clan channel.");
			return;
		}
		final ClanChatRank rank = clan.getRank(player);
		if (rank == null) {
			player.getPacketSender().sendMessage("You do not have the required rank to kick this player.");
			return;
		}
		for (ClanChatMember member : clan.getMembers()) {
			if (member != null && member.getPlayer().equals(target)) {
				if (rank.ordinal() < member.getRank().ordinal()) {
					player.getPacketSender().sendMessage("You cannot kick a player who has a higher rank than you!");
					break;
				}
				leave(member.getPlayer(), true);
				break;
			}
		}
	}

	public static void setRank(final Player owner, final Player target, final ClanChatRank rank) {
		final ClanChat clan = getClanChat(owner);
		if (clan != null) {
			String green = ClanChatMessageColor.GREEN.getRGB()[owner.getAttributes().getClanChatMessageColor().getRgbIndex()];
			owner.getPacketSender().sendMessage("<col=" + green + ">Changes will take effect in 60 seconds.");
			clan.applyChange(new ClanChatAction() {
				@Override
				public void execute() {
					clan.giveRank(target, rank);
				}
			});
		} else {
			owner.getPacketSender().sendMessage("You need to have a public clan channel to do this.");
		}
	}

	public static void setRankToEnter(final Player player, final ClanChatRank rank) {
		final ClanChat clan = getClanChat(player);
		if (clan != null) {
			String green = ClanChatMessageColor.GREEN.getRGB()[player.getAttributes().getClanChatMessageColor().getRgbIndex()];
			player.getPacketSender().sendMessage("<col=" + green + ">Changes will take effect in 60 seconds.");
			clan.applyChange(new ClanChatAction() {
				@Override
				public void execute() {
					clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_ENTER, rank);
				}
			});
		} else {
			player.getPacketSender().sendMessage("You need to have a public clan channel to do this.");
		}
	}

	public static void setRankToKick(final Player player, final ClanChatRank rank) {
		final ClanChat clan = getClanChat(player);
		if (clan != null) {
			String green = ClanChatMessageColor.GREEN.getRGB()[player.getAttributes().getClanChatMessageColor().getRgbIndex()];
			player.getPacketSender().sendMessage("<col=" + green + ">Changes will take effect in 60 seconds.");
			clan.applyChange(new ClanChatAction() {
				@Override
				public void execute() {
					clan.setRankRequirements(ClanChat.RANK_REQUIRED_TO_KICK, rank);
				}
			});
		}
	}

	public static boolean dropShareLoot(Player player, NPC npc, Item itemDropped) {
	/*	ClanChat clan = player.getFields().getClanChat();
		if (clan != null) {
			boolean received = false;
			List<Player> players = getPlayersWithinPosition(clan, npc.getPosition());
			String green = "<col=" + ClanChatMessageColor.GREEN.getRGB()[player.getFields().rgbIndex] + ">";
			if (clan.isItemSharing() && itemDropped.getId() != 995) {
				Player rewarded = players.size() > 0 ? players.get(MathUtils.random(players.size() - 1)) : null;
				if (rewarded != null) {
					rewarded.getPacketSender().sendMessage(green + "You have received " + itemDropped.getAmount() + "x " + itemDropped.getDefinition().getName() + ".");
					received = true;
				}
			}
			if (clan.isCoinSharing() && itemDropped.getId() == 995) {
				for (Item drop : npc.getDrops()) {
					if ((drop.getDefinition().getValue() * drop.getAmount()) < 50000) {
						GroundItem groundItem = new GroundItem(drop, npc.getPosition().copy());
						GameServer.getWorld().register(groundItem, player);
						continue;
					}
					int amount = (int) (ItemDefinition.forId(drop.getId()).getValue() / players.size());
					Item split = new Item(995, amount);
					for (Player member : players) {
						GroundItem groundItem = new GroundItem(split.copy(), npc.getPosition().copy());
						GameServer.getWorld().register(groundItem, member);
						member.getPacketSender().sendMessage(green + "You have received " + amount + "x " + split.getDefinition().getName() + " as part of a split drop.");
					}
				}
			} else if(!clan.isItemSharing() && !clan.isCoinSharing() || !received)
				return false;
		} else
			return false;*/
		return false;
	}

	public static void toggleLootShare(Player player, final ClanChatDropShare type, final boolean on) {
		final ClanChat clan = player.getAttributes().getClanChat();
		if (clan == null) {
			player.getPacketSender().sendMessage("You're not in a clan channel.");
			return;
		}
		if(player.getRights() != PlayerRights.OWNER && player.getRights() != PlayerRights.DEVELOPER) {
			if(clan.getOwner() == null)
				return;
			if (!clan.getOwner().equals(player)) {
				player.getPacketSender().sendMessage("Only the owner of the channel has the power to do this.");
				return;
			}
		}
		if(clan.taskSubmitted) {
			player.getPacketSender().sendMessage("This clan has already requested a lootshare change. Try again in 60 seconds.");
			return;
		}
		clan.taskSubmitted = true;
		String green = ClanChatMessageColor.GREEN.getRGB()[player.getAttributes().getClanChatMessageColor().getRgbIndex()];
		player.getPacketSender().sendMessage("<col=" + green + ">Changes will take effect in 60 seconds.");
		clan.applyChange(new ClanChatAction() {
			@Override
			public void execute() {
				String message = "Lootshare status changed!";
				switch(type) {
				case COIN_SHARE:
					message = on ? "@gre@Coin share has been enabled by the clan leader." : "@red@Coin share has been disabled by the clan leader.";
					clan.setCoinSharing(on);
					break;
				case ITEM_SHARE:
					message = on ? "@gre@Item share has been enabled by the clan leader." : "@red@Item share has been disabled by the clan leader.";
					clan.setItemSharing(on);
					break;
				case NONE:
					message = "@red@Lootsharing has been completely disabled in this clan chat.";
					clan.setItemSharing(false);
					clan.setCoinSharing(false);
					break;
				}
				for (ClanChatMember member : clan.getMembers()) {
					if (member != null && member.getPlayer() != null) {
						member.getPlayer().getPacketSender().sendString(29454, "Lootshare: "+getLootshareStatus(clan));
					}
				}
				clan.taskSubmitted = false;
				sendMessage(clan, message);
				message = null;
			}
		});
	}

	private static String getLootshareStatus(ClanChat clan) {
		String lootshare = "Off";
		if(clan.isItemSharing() && !clan.isCoinSharing())
			lootshare = "Item Share";
		else if(!clan.isItemSharing() && clan.isCoinSharing())
			lootshare = "Coin Share";
		else if(clan.isCoinSharing() && clan.isItemSharing())
			lootshare = "Item & Coin Share";
		return lootshare;
	}

	public static Dialogue getToggleLootshareDialogue(final ClanChat clan) {
		return new Dialogue() {
			@Override
			public DialogueType type() {
				return DialogueType.OPTION;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NO_EXPRESSION;
			}

			@Override
			public String[] dialogue() {
				return new String[]{clan.isItemSharing() ? "Turn off ItemShare" : "Turn on ItemShare", clan.isCoinSharing() ? "Turn off CoinShare" : "Turn on CoinShare", "Cancel"};
			}
		};
	}

	/**
	 * Setting player's clan chat message color
	 * @param player	player setting clan chat message color
	 * @param color		color to set clan chat message color to
	 */
	public static void setMessageColor(Player player, ClanChatMessageColor color) {
		player.getAttributes().setClanChatMessageColor(color);
	}

	public static ClanChat getClanChat(Player player) {
		for (ClanChat clan : clans) {
			if(clan == null || clan.getOwner() == null)
				return null;
			if (clan.getOwner().equals(player)) {
				return clan;
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private static List<Player> getPlayersWithinPosition(ClanChat clan, Position position) {
		List<Player> players = new LinkedList<Player>();
		for (ClanChatMember member : clan.getMembers()) {
			if (member != null && member.getPlayer() != null && member.getPlayer().getPosition().isWithinDistance(position)) {
				players.add(member.getPlayer());
			}
		}
		return players;
	}

	private static int getIndex() {
		for (int i = 0; i < clans.length; i++) {
			if (clans[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public static void handleLogin(Player player) {
		resetInterface(player);
		if(player.getAttributes().getClanChatName() == null || player.getAttributes().getClanChatName().equals("null") || player.getAttributes().getClanChatName().equals("") || player.getAttributes().getClanChatName().toLowerCase().equals("general"))
			player.getAttributes().setClanChatName("Trident");
		ClanChatManager.join(player, player.getAttributes().getClanChatName());
		/*
		 * Send clan settings
		 *
		boolean hasClan = true ; //player.getFields().hasClan();
		if(!hasClan) {
			player.getPacketSender().sendString(18306, "Chat Disabled");
			String title = "";
			for (int id = 18307; id < 18317; id += 3) {
				if (id == 18307) {
					title = "Anyone";
				} else if (id == 18310) {
					title = "Anyone";
				} else if (id == 18313) {
					title = "General+";
				} else if (id == 18316) {
					title = "Only Me";
				}
				player.getPacketSender().sendString(id + 2, title);
			}
			for (int index = 0; index < 100; index++) {
				player.getPacketSender().sendString(18323 + index, "ranked members");
			}
			for (int index = 0; index < 100; index++) {
				player.getPacketSender().sendString(18424 + index, "");
			}
			return;
		}
		 */
	}
}
