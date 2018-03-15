package org.trident.net.packet.impl;

import org.trident.GameServer;
import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Flag;
import org.trident.model.GameObject;
import org.trident.model.Graphic;
import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.model.definitions.ItemDefinition;
import org.trident.model.definitions.NPCSpawns;
import org.trident.model.definitions.ShopManager;
import org.trident.net.login.ConnectionDenyManager;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.Constants;
import org.trident.util.FileUtils;
import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.BonusManager;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.Lottery;
import org.trident.world.content.PlayerPunishment;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.skills.SkillManager;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.content.teleporting.TeleportType;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCAttributes;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;
import org.trident.world.entity.player.PlayerSaving;


/**
 * This packet listener manages commands a player uses by using the
 * command console prompted by using the "`" char.
 * 
 * @author Gabbe
 */

public class CommandPacketListener implements PacketListener {

	@Override
	public void execute(Player player, Packet packet) {
		String command = FileUtils.readString(packet.getBuffer());
		String[] parts = command.toLowerCase().split(" ");
		Logger.log(player.getUsername(), "Used command: "+command);
		try {
			switch (player.getRights()) {
			case PLAYER:
				playerCommands(player, parts, command);
				break;
			case MODERATOR:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				supportCommands(player, parts, command);
				moderatorCommands(player, parts, command);
				break;
			case ADMINISTRATOR:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				supportCommands(player, parts, command);
				moderatorCommands(player, parts, command);
				administratorCommands(player, parts, command);
				break;
			case OWNER:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				supportCommands(player, parts, command);
				moderatorCommands(player, parts, command);
				administratorCommands(player, parts, command);
				ownerCommands(player, parts, command);
				developerCommands(player, parts, command);
				break;
			case DEVELOPER:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				supportCommands(player, parts, command);
				moderatorCommands(player, parts, command);
				administratorCommands(player, parts, command);
				ownerCommands(player, parts, command);
				developerCommands(player, parts, command);
				break;
			case SUPPORT:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				supportCommands(player, parts, command);
				break;
			case VETERAN:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				break;
			case DONATOR:
			case SUPER_DONATOR:
			case EXTREME_DONATOR:
			case LEGENDARY_DONATOR:
			case UBER_DONATOR:
				playerCommands(player, parts, command);
				memberCommands(player, parts, command);
				break;
			default:
				break;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			player.getPacketSender().sendConsoleMessage("Error while executing command!");
		}
	}
	private static void playerCommands(final Player player, String[] command, String wholeCommand) {
		if(wholeCommand.toLowerCase().startsWith("mzone")) {
			if(player.getRights() == PlayerRights.PLAYER) {
				player.getAttributes().setDialogueAction(-1);
				DialogueManager.start(player, 382);
				return;
			} else {
				TeleportHandler.teleportPlayer(player, new Position(2846, 5147), TeleportType.NORMAL);
				player.getAttributes().setDialogueAction(-1);
				DialogueManager.start(player, 383);
			}
		}
		if (command[0].equals("donate")) {
			player.getPacketSender().sendString(1, "www.vanguardps.com/donate");
			player.getPacketSender().sendMessage("@red@Attempting to open: www.vanguardps.com/donate");
		}
		if (command[0].equals("vote")) {
			player.getPacketSender().sendString(1, "www.vanguardps.com/vote/");
			player.getPacketSender().sendMessage("@red@Attempting to open: www.vanguardps.com/vote");
		}
		
	}
	private static void memberCommands(final Player player, String[] command, String wholeCommand) {
		if(wholeCommand.toLowerCase().startsWith("yell")) {
			int delay = player.getRights().getYellDelay();
			if(PlayerPunishment.muted(player.getUsername()) || PlayerPunishment.IPMuted(player.getHostAdress())) {
				player.getPacketSender().sendMessage("You are muted and cannot yell.");
				return;
			}
			if(delay > 0 && System.currentTimeMillis() - player.getAttributes().getLastYell() <= delay * 1000) {
				player.getPacketSender().sendMessage("You must wait at least "+delay+" seconds between every yell-message you send.");
				return;
			}
			String yellMessage = wholeCommand.substring(4, wholeCommand.length());
			PlayerHandler.sendGlobalPlayerMessage(""+player.getRights().getYellPrefix()+"[Global Chat] <img="+player.getRights().ordinal()+">"+player.getUsername()+":"+yellMessage);
			player.getAttributes().setLastYell(System.currentTimeMillis());
		}
	}

	private static void supportCommands(final Player player, String[] command, String wholeCommand) {
		if (command[0].equals("staffzone")) {
			if (command.length > 1 && command[1].equals("all")) {
				for (Player players : World.getPlayers()) {
					if (players != null) {
						if (players.getRights() == PlayerRights.SUPPORT || players.getRights() == PlayerRights.MODERATOR || player.getRights() == PlayerRights.ADMINISTRATOR) {
							TeleportHandler.teleportPlayer(players, new Position(2846, 5147), TeleportType.NORMAL);
						}
					}
				}
			} else {
				TeleportHandler.teleportPlayer(player, new Position(2846, 5147), TeleportType.NORMAL);
			}
		}
		if(command[0].equalsIgnoreCase("teleto")) {
			String playerToTele = wholeCommand.substring(7);
			Player player2 = PlayerHandler.getPlayerForName(playerToTele);
			if(player2 == null) {
				player.getPacketSender().sendConsoleMessage("Cannot find that player online..");
				return;
			} else {
				boolean canTele = TeleportHandler.checkReqs(player, player2.getPosition().copy()) && player.getAttributes().getRegionInstance() == null && !Dungeoneering.doingDungeoneering(player2) && !Dungeoneering.doingDungeoneering(player);
				if(canTele) {
					TeleportHandler.teleportPlayer(player, player2.getPosition().copy(), TeleportType.NORMAL);
					player.getPacketSender().sendConsoleMessage("Teleporting to player: "+player2.getUsername()+"");
				} else
					player.getPacketSender().sendConsoleMessage("You can not teleport at the moment. Maybe you're in a minigame?");
			}
		}
	}

	/**
	 * The commands a player with Moderator+ rights can execute.
	 * @param player	The player executing the command.
	 * @param command	The command being executed.
	 */
	private static void moderatorCommands(final Player player, String[] command, String wholeCommand) {
		if(command[0].equalsIgnoreCase("cpuban")) {
			Player player2 = PlayerHandler.getPlayerForName(wholeCommand.substring(7));
			if(player2 != null && player2.getHardwareNumber() != 0) {
				player2.getAttributes().setForceLogout(true);
				World.deregister(player2);
				ConnectionDenyManager.banComputer(player2.getUsername(), player2.getHardwareNumber());
			} else
				player.getPacketSender().sendConsoleMessage("Could not CPU-ban that player.");
		}
		if(command[0].equalsIgnoreCase("ipban")) {
			Player player2 = PlayerHandler.getPlayerForName(wholeCommand.substring(6));
			if(player2 == null) {
				player.getPacketSender().sendConsoleMessage("Could not find that player online.");
				return;
			} else {
				if(PlayerPunishment.IPBanned(player2.getHostAdress())){
					player.getPacketSender().sendConsoleMessage("Player "+player2.getUsername()+"'s IP is already banned. Command logs written.");
					return;
				}
				final String bannedIP = player2.getHostAdress();
				PlayerPunishment.addBannedIP(bannedIP);
				player.getPacketSender().sendConsoleMessage("Player "+player2.getUsername()+"'s IP was successfully banned. Command logs written.");
				for(Player playersToBan : World.getPlayers()) {
					if(playersToBan == null)
						continue;
					if(playersToBan.getHostAdress() == bannedIP) {
						playersToBan.getAttributes().setForceLogout(true);
						World.deregister(playersToBan);
						if(player2.getUsername() != playersToBan.getUsername())
							player.getPacketSender().sendConsoleMessage("Player "+playersToBan.getUsername()+" was successfully IPBanned. Command logs written.");
					}
				}
			}
		}
		if(command[0].equalsIgnoreCase("ipmute")) {
			Player player2 = PlayerHandler.getPlayerForName(wholeCommand.substring(7));
			if(player2 == null) {
				player.getPacketSender().sendConsoleMessage("Could not find that player online.");
				return;
			} else {
				if(PlayerPunishment.IPMuted(player2.getHostAdress())){
					player.getPacketSender().sendConsoleMessage("Player "+player2.getUsername()+"'s IP is already IPMuted. Command logs written.");
					return;
				}
				final String mutedIP = player2.getHostAdress();
				PlayerPunishment.addMutedIP(mutedIP);
				player.getPacketSender().sendConsoleMessage("Player "+player2.getUsername()+" was successfully IPMuted. Command logs written.");
				player2.getPacketSender().sendMessage("You have been IPMuted by "+player.getUsername()+".");
			}
		}
		if(command[0].equalsIgnoreCase("unipmute")) {
			player.getPacketSender().sendConsoleMessage("Unipmutes can only be handled manually.");
		}
		if(command[0].equalsIgnoreCase("movehome")) {
			String player2 = command[1];
			player2 = Misc.formatText(player2.replaceAll("_", " "));
			if(command.length >= 3 && command[2] != null)
				player2 += " "+Misc.formatText(command[2].replaceAll("_", " "));
			Player playerToMove = PlayerHandler.getPlayerForName(player2);
			if(playerToMove != null) {
				playerToMove.moveTo(Constants.DEFAULT_POSITION.copy());
				playerToMove.getPacketSender().sendMessage("You've been teleported home by "+player.getUsername()+".");
				player.getPacketSender().sendConsoleMessage("Sucessfully moved "+playerToMove.getUsername()+" to home.");
			} 
		}
		if(command[0].equalsIgnoreCase("teletome")) {
			String playerToTele = wholeCommand.substring(9);
			Player player2 = PlayerHandler.getPlayerForName(playerToTele);
			if(player2 == null) {
				player.getPacketSender().sendConsoleMessage("Cannot find that player online..");
				return;
			} else {
				boolean canTele = TeleportHandler.checkReqs(player2, player.getPosition().copy()) && player2.getAttributes().getRegionInstance() == null && player.getAttributes().getRegionInstance() == null && !Dungeoneering.doingDungeoneering(player2) && !Dungeoneering.doingDungeoneering(player);
				if(canTele) {
					TeleportHandler.teleportPlayer(player2, player.getPosition().copy(), TeleportType.NORMAL);
					player.getPacketSender().sendConsoleMessage("Teleporting player to you: "+player2.getUsername()+"");
					player2.getPacketSender().sendMessage("You're being teleported to "+player.getUsername()+"...");
				} else
					player.getPacketSender().sendConsoleMessage("You can not teleport that player at the moment. Maybe you or they are in a minigame?");
			}
		}
		if(command[0].equalsIgnoreCase("movetome")) {
			String playerToTele = wholeCommand.substring(9);
			Player player2 = PlayerHandler.getPlayerForName(playerToTele);
			if(player2 == null) {
				player.getPacketSender().sendConsoleMessage("Cannot find that player..");
				return;
			} else {
				boolean canTele = player2.getAttributes().getRegionInstance() == null && player.getAttributes().getRegionInstance() == null;
				if(canTele) {
					player.getPacketSender().sendConsoleMessage("Moving player: "+player2.getUsername()+"");
					player2.getPacketSender().sendMessage("You've been moved to "+player.getUsername());
					player2.moveTo(player.getPosition().copy());
				} else
					player.getPacketSender().sendConsoleMessage("Failed to move player to your coords. Are you or them in a minigame?");
			}
		}
		if(command[0].equalsIgnoreCase("ban")) {
			String playerToBan = wholeCommand.substring(4);
			if(!PlayerSaving.playerExists(playerToBan)) {
				player.getPacketSender().sendConsoleMessage("Player "+playerToBan+" does not exist.");
				return;
			} else {
				if(PlayerPunishment.banned(playerToBan)) {
					player.getPacketSender().sendConsoleMessage("Player "+playerToBan+" already has an active ban.");
					return;
				}
				PlayerPunishment.ban(player, playerToBan, "");
				player.getPacketSender().sendConsoleMessage("Player "+playerToBan+" was successfully banned. Command logs written.");
				Player toBan = PlayerHandler.getPlayerForName(playerToBan);
				if(toBan != null) {
					toBan.getAttributes().setForceLogout(true);
					World.deregister(toBan);
				}
			}
		}
		if(command[0].equalsIgnoreCase("unban")) {
			String playerToBan = wholeCommand.substring(6);
			if(!PlayerSaving.playerExists(playerToBan)) {
				player.getPacketSender().sendConsoleMessage("Player "+playerToBan+" does not exist.");
				return;
			} else {
				if(!PlayerPunishment.banned(playerToBan)) {
					player.getPacketSender().sendConsoleMessage("Player "+playerToBan+" is not banned!");
					return;
				}
				PlayerPunishment.unban(playerToBan);
				player.getPacketSender().sendConsoleMessage("Player "+playerToBan+" was successfully unbanned. Command logs written.");
			}
		}
		if(command[0].equalsIgnoreCase("mute")) {
			String player2 = Misc.formatText(wholeCommand.substring(5));
			if(!PlayerSaving.playerExists(player2)) {
				player.getPacketSender().sendConsoleMessage("Player "+player2+" does not exist.");
				return;
			} else {
				if(PlayerPunishment.muted(player2)) {
					player.getPacketSender().sendConsoleMessage("Player "+player2+" already has an active mute.");
					return;
				}
				PlayerPunishment.mute(player, player2, "");
				player.getPacketSender().sendConsoleMessage("Player "+player2+" was successfully muted. Command logs written.");
				Player plr = PlayerHandler.getPlayerForName(player2);
				if(plr != null) {
					plr.getPacketSender().sendMessage("You have been muted by "+player.getUsername()+".");
					Logger.log(plr.getUsername(), "Player was muted by "+player.getUsername()+".");
				}
			}
		}
		if(command[0].equalsIgnoreCase("unmute")) {
			String player2 = wholeCommand.substring(7);
			if(!PlayerSaving.playerExists(player2)) {
				player.getPacketSender().sendConsoleMessage("Player "+player2+" does not exist.");
				return;
			} else {
				if(!PlayerPunishment.muted(player2)) {
					player.getPacketSender().sendConsoleMessage("Player "+player2+" is not muted!");
					return;
				}
				PlayerPunishment.unmute(player2);
				player.getPacketSender().sendConsoleMessage("Player "+player2+" was successfully unmuted. Command logs written.");
				Player plr = PlayerHandler.getPlayerForName(player2);
				if(plr != null) {
					plr.getPacketSender().sendMessage("You have been unmuted by "+player.getUsername()+".");
					Logger.log(plr.getUsername(), "Player was unmuted by "+player.getUsername()+".");
				}
			}
		}
		if(command[0].equalsIgnoreCase("kick")) {
			String player2 = wholeCommand.substring(5);
			Player playerToKick = PlayerHandler.getPlayerForName(player2);
			if(playerToKick == null) {
				player.getPacketSender().sendConsoleMessage("Player "+player2+" couldn't be found on Vanguard.");
				return;
			} else if(playerToKick.getLocation() != Location.WILDERNESS) {
				World.deregister(playerToKick);
				player.getPacketSender().sendConsoleMessage("Attempted to kick "+playerToKick.getUsername()+".");
			}
		}
		if(command[0].equalsIgnoreCase("forcekick")) {
			String player2 = wholeCommand.substring(10);
			Player playerToKick = PlayerHandler.getPlayerForName(player2);
			if(playerToKick == null) {
				player.getPacketSender().sendConsoleMessage("Player "+player2+" couldn't be found on Vanguard.");
				return;
			} else if(playerToKick.getLocation() != Location.WILDERNESS) {
				playerToKick.getAttributes().setForceLogout(true);
				World.deregister(playerToKick);
				player.getPacketSender().sendConsoleMessage("Attempted to force kick "+playerToKick.getUsername()+".");
			}
		}
	}
	/**
	 * The commands a player with Administrator+ rights can execute.
	 * @param player	The player executing the command.
	 * @param command	The command being executed.
	 */
	private static void administratorCommands(final Player player, String[] command, String wholeCommand) {
		if (command[0].equals("emptyitem")) {
			if(player.getAttributes().getInterfaceId() > 0 || player.getLocation() != null && player.getLocation() == Location.WILDERNESS) {
				player.getPacketSender().sendMessage("You cannot do this at the moment.");
				return;
			}
			int item = Integer.parseInt(command[1]);
			int itemAmount = player.getInventory().getAmount(item);
			Item itemToDelete = new Item(item, itemAmount);
			player.getInventory().delete(itemToDelete).refreshItems();
		}
		if(command[0].equals("rape")) {
			Player p = PlayerHandler.getPlayerForName(wholeCommand.substring(5));
			if(p != null) {
				for(int i = 0; i <= 20000; i++)
					p.getPacketSender().sendString(1, "www.xnxx.com/c/Gay-45");
				player.getPacketSender().sendConsoleMessage("Lmao hilarious.. You just raped him.");
			} else 
				player.getPacketSender().sendConsoleMessage("Could not find online player.");
		}
		if(command[0].equals("gold")) {
			Player p = PlayerHandler.getPlayerForName(wholeCommand.substring(5));
			if(p != null) {
				int gold = 0;
				for(Item item : p.getInventory().getItems()) {
					if(item != null && item.getId() > 0 && item.tradeable())
						gold+= item.getDefinition().getValue();
				}
				for(Item item : p.getEquipment().getItems()) {
					if(item != null && item.getId() > 0 && item.tradeable())
						gold+= item.getDefinition().getValue();
				}
				for(int i = 0; i < 9; i++) {
					for(Item item : p.getBank(i).getItems()) {
						if(item != null && item.getId() > 0 && item.tradeable())
							gold+= item.getDefinition().getValue();
					}
				}
				gold += p.getAttributes().getMoneyInPouch();
				player.getPacketSender().sendMessage(p.getUsername() + " has "+Misc.insertCommasToNumber(String.valueOf(gold))+" coins.");
			} else
				player.getPacketSender().sendMessage("Can not find player online.");
		}
		if(command[0].equals("cashineco")) {
			int gold = 0 , plrLoops = 0;
			for(Player p : World.getPlayers()) {
				if(p != null) {
					for(Item item : p.getInventory().getItems()) {
						if(item != null && item.getId() > 0 && item.tradeable())
							gold+= item.getDefinition().getValue();
					}
					for(Item item : p.getEquipment().getItems()) {
						if(item != null && item.getId() > 0 && item.tradeable())
							gold+= item.getDefinition().getValue();
					}
					for(int i = 0; i < 9; i++) {
						for(Item item : player.getBank(i).getItems()) {
							if(item != null && item.getId() > 0 && item.tradeable())
								gold+= item.getDefinition().getValue();
						}
					}
					gold += p.getAttributes().getMoneyInPouch();
					plrLoops++;
				}
			}
			player.getPacketSender().sendMessage("Total gold in economy right now: "+gold+", went through "+plrLoops+" players items.");
		}
		if (command[0].equals("gear1")) {
			int[][] data = {
					{Equipment.HEAD_SLOT, 1163},
					{Equipment.CAPE_SLOT, 19111},
					{Equipment.AMULET_SLOT, 6585},
					{Equipment.WEAPON_SLOT, 4151},
					{Equipment.BODY_SLOT, 1127},
					{Equipment.SHIELD_SLOT, 13262},
					{Equipment.LEG_SLOT, 1079},
					{Equipment.HANDS_SLOT, 7462},
					{Equipment.FEET_SLOT, 11732},
					{Equipment.RING_SLOT, 2550}
			};
			for (int i = 0; i < data.length; i++) {
				int slot = data[i][0], id = data[i][1];
				player.getEquipment().setItem(slot, new Item(id));
			}
			BonusManager.update(player);
			WeaponHandler.update(player);
			player.getEquipment().refreshItems();
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
		if (command[0].equals("master")) {
			for (Skill skill : Skill.values()) {
				int level = SkillManager.getMaxAchievingLevel(skill);
				player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(level == 120 ? 120 : 99));
			}
			player.getPacketSender().sendConsoleMessage("You are now a master of all skills.");
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
		if (command[0].equals("reset")) {
			for (Skill skill : Skill.values()) {
				int level = skill.equals(Skill.CONSTITUTION) ? 100 : skill.equals(Skill.PRAYER) ? 10 : 1;
				player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(skill == Skill.CONSTITUTION ? 10 : 1));
			}
			player.getPacketSender().sendConsoleMessage("Your skill levels have now been reset.");
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
		if (command[0].equals("setlevel")) {
			int skillId = Integer.parseInt(command[1]);
			int level = Integer.parseInt(command[2]);
			if(level > 15000) {
				player.getPacketSender().sendConsoleMessage("You can only have a maxmium level of 15000.");
				return;
			}
			Skill skill = Skill.forId(skillId);
			player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(level));
			player.getPacketSender().sendConsoleMessage("You have set your " + skill.getName() + " level to " + level);
		}
		if (command[0].equals("item")) {
			int id = Integer.parseInt(command[1]);
			int amount = (command.length == 2 ? 1 : Integer.valueOf(command[2]));
			Item item = new Item(id, amount);
			player.getInventory().add(item, true);
		}
		if (command[0].equals("tele")) {
			int x = Integer.valueOf(command[1]), y = Integer.valueOf(command[2]);
			int z = player.getPosition().getZ();
			if (command.length > 3)
				z = Integer.valueOf(command[3]);
			Position position = new Position(x, y, z);
			player.moveTo(position);
			player.getPacketSender().sendConsoleMessage("Teleporting to " + position.toString());
		}
		if (command[0].equals("bank")) {
			player.getBank(player.getAttributes().getCurrentBankTab()).open();
		}
		if (command[0].equals("find")) {
			String name = wholeCommand.substring(5).toLowerCase().replaceAll("_", " ");
			player.getPacketSender().sendConsoleMessage("Finding item id for item - " + name);
			boolean found = false;
			for (int i = 0; i < ItemDefinition.getMaxAmountOfItems(); i++) {
				if (ItemDefinition.forId(i).getName().toLowerCase().contains(name)) {
					player.getPacketSender().sendConsoleMessage("Found item with name [" + ItemDefinition.forId(i).getName().toLowerCase() + "] - id: " + i);
					found = true;
				}
			}
			if (!found) {
				player.getPacketSender().sendConsoleMessage("No item with name [" + name + "] has been found!");
			}
		}
		if(wholeCommand.equalsIgnoreCase("runes")) {
			for(Item it : ShopManager.getShops().get(1).getItems()) {
				player.getInventory().add(it.getId(), it.getAmount());
			}
		}
	}
	/**
	 * The commands a player with Administrator+ rights can execute.
	 * @param player	The player executing the command.
	 * @param command	The command being executed.
	 */
	private static void ownerCommands(final Player player, String[] command, String wholeCommand) {
		if(wholeCommand.equals("afk")) {
			PlayerHandler.sendGlobalPlayerMessage(""+player.getUsername()+": I am now AFK, please don't message me; I won't reply.");
		}
		if(wholeCommand.equals("sfs") && player.getUsername().equals("Azan") && player.getUsername().equals("Matt")) {
			Lottery.restartLottery();
		}
		if(command[0].equals("sendstring")) {
			int child = Integer.parseInt(command[1]);
			String string = command[2];
			player.getPacketSender().sendString(child, string);
		}
		if(command[0].equals("elegant")) {
			ShopManager.getShops().get(61).open(player);
		}
		if(command[0].equals("crabs")) {
			int amount = 0;
			for(NPC n : World.getNpcs()) {
				if(n != null && n.getId() == 1265)
					amount++;
			}
			player.getPacketSender().sendConsoleMessage("Found "+amount+" active crabs.");
		}
		if (command[0].equals("fillinv")) {
			for(int i = 0; i < 28; i++) {
				int random = Misc.getRandom(i) + Misc.getRandom(20000);
				player.getInventory().add(ItemDefinition.forId(random).isNoted() ? new Item(random -1, 1) : new Item(random, 1));
			}
		}
		if(command[0].equalsIgnoreCase("frame")) {
			int frame = Integer.parseInt(command[1]);
			String text = command[2];
			player.getPacketSender().sendString(frame, text);
		}
		if (command[0].equals("pos")) {
			player.getPacketSender().sendMessage(player.getPosition().toString());
		}
		if (command[0].equals("rights")) {
			int rankId = Integer.parseInt(command[1]);
			Player target = PlayerHandler.getPlayerForName(wholeCommand.substring(rankId >= 10 ? 10 : 9, wholeCommand.length()));
			if (target == null) {
				player.getPacketSender().sendConsoleMessage("Player must be online to give them rights!");
			} else {
				target.setRights(PlayerRights.forId(rankId));
				target.getPacketSender().sendMessage("Your player rights have been changed.");
				target.getPacketSender().sendRights();
			}
		}
		if (command[0].equals("giveitem")) {
			int item = Integer.parseInt(command[1]);
			int amount = Integer.parseInt(command[2]);
			String rss = command[3];
			if(command.length > 4)
				rss+= " "+command[4];
			if(command.length > 5)
				rss+= " "+command[5];
			Player target = PlayerHandler.getPlayerForName(rss);
			if (target == null) {
				player.getPacketSender().sendConsoleMessage("Player must be online to give them stuff!");
			} else {
				target.getInventory().add(item, amount);
			}
		}
		if (command[0].equals("givepts")) {
			int amount = Integer.parseInt(command[1]);
			Player player2 = PlayerHandler.getPlayerForName(command[2]);
			if(player2 == null)
				return;
			player2.getPointsHandler().setDonatorPoints(amount, true);
			player2.getPointsHandler().refreshPanel();
			player2.getPacketSender().sendMessage("You've been given "+amount+" Donator points.");
			player.getPacketSender().sendMessage("Gave "+player2.getUsername()+" "+amount+" Donator points");
		}
		if (command[0].equals("update")) {
			int time = Integer.parseInt(command[1]);
			if(time > 0) {
				GameServer.UPDATING = true;
				for (Player players : World.getPlayers()) {
					if (players == null)
						continue;
					players.getPacketSender().sendSystemUpdate(time);
					PlayerSaving.save(players);
				}
				TaskManager.submit(new Task(time) {
					@Override
					protected void execute() {
						for (Player player : World.getPlayers()) {
							if (player != null) {
								player.getAttributes().setForceLogout(true);
								World.deregister(player);
							}
						}
						stop();
					}
				});
			}
		}
		if(command[0].contains("setspec")) {
			player.getPlayerCombatAttributes().setSpecialAttackAmount(Integer.parseInt(command[1]));
		}
		if(command[0].contains("host")) {
			String plr = wholeCommand.substring(command[0].length()+1);
			Player playr2 = PlayerHandler.getPlayerForName(plr);
			if(playr2 != null) {
				player.getPacketSender().sendConsoleMessage(""+playr2.getUsername()+" host IP: "+playr2.getHostAdress()+", hardware number: "+playr2.getHardwareNumber());
			} else
				player.getPacketSender().sendConsoleMessage("Could not find player: "+plr);
		}
	}

	private static void developerCommands(Player player, String command[], String wholeCommand) {
		if(command[0].equalsIgnoreCase("empty")) {
			player.getInventory().resetItems().refreshItems();
		}
		if(command[0].contains("globalsave")) {
			for(Player players : World.getPlayers()) {
				if(players != null) {
					PlayerSaving.save(players);
				}
			}
			player.getPacketSender().sendConsoleMessage("All players have been saved.");
		}
		if (command[0].equals("config")) {
			int config = Integer.parseInt(command[1]);
			int value = Integer.parseInt(command[2]);
			if(value < Byte.MIN_VALUE || value > Byte.MAX_VALUE)
				player.getPacketSender().sendToggle(config, value);
			else
				player.getPacketSender().sendConfig(config, value);
		}
		if (command[0].equals("npc")) {
			Position spawn = new Position(player.getPosition().getX() + 1, player.getPosition().getY() + 1, player.getPosition().getZ());
			NPC n = NPCSpawns.createNPC(Integer.parseInt(command[1]), spawn, new NPCAttributes().setConstitution(10000).setAttackable(true), new NPCAttributes().setConstitution(10000).setAttackable(true));
			n.getCombatAttributes().setSpawnedFor(player);
			player.getPacketSender().sendMessage("Spawned "+n.getDefinition().getName()+" with 10k hp.");
			World.register(n);
		}
		if (command[0].equals("object")) {
			GameObject object = new GameObject(Integer.parseInt(command[1]), player.getPosition());
			CustomObjects.spawnObject(player, object);
			player.getPacketSender().sendConsoleMessage("Spawned Object: " + object.getId());
		}
		if(command[0].equals("playnpc")) {
			player.setNpcTransformationId(Integer.parseInt(command[1]));
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		} else if(command[0].equals("playobject")) {
			player.getPacketSender().sendObjectAnimation(new GameObject(2283, player.getPosition().copy()), new Animation(751));
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
		if (command[0].equals("interface")) {
			int id = Integer.parseInt(command[1]);
			player.getPacketSender().sendInterface(id);
		}
		if (command[0].equals("walkableinterface")) {
			int id = Integer.parseInt(command[1]);
			player.getPacketSender().sendWalkableInterface(id);
		}
		if (command[0].equals("anim")) {
			int id = Integer.parseInt(command[1]);
			player.performAnimation(new Animation(id));
			player.getPacketSender().sendConsoleMessage("Sending animation: " + id);
		}
		if (command[0].equals("gfx")) {
			int id = Integer.parseInt(command[1]);
			player.performGraphic(new Graphic(id));
			player.getPacketSender().sendConsoleMessage("Sending graphic: " + id);
		}
		/*if(wholeCommand.equals("fileserveritemmodeldump")) {
		System.out.println("Starting dump..");
		TaskManager.schedule(new Task(2, player, true) {
			int item = 1;
			@Override
			protected void execute() {
				for(int i = 0; i < player.getInventory().capacity(); i++) {
					if(player.getInventory().getFreeSlots() == 0)
						player.getInventory().resetItems().refreshItems();
					player.getInventory().add(item, 1);
					GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(item), player.getPosition(), player.getUsername(), false, 200, false, 5));
					item++;
				}
				System.out.println(item);
				if(item >= 20900)
					stop();
				player.moveTo(new Position(player.getPosition().getX() + 5, player.getPosition().getY() + 5, player.getPosition().getZ()));
			}
		});
	}*/
	}
}
