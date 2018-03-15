package org.trident.net.login;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.SecureRandom;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.trident.GameServer;
import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.NewPlayerTask;
import org.trident.engine.task.impl.OverloadPotionTask;
import org.trident.engine.task.impl.PlayerSkillsTask;
import org.trident.engine.task.impl.PlayerSpecialAmountTask;
import org.trident.engine.task.impl.PrayerRenewalPotionTask;
import org.trident.model.PlayerRights;
import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.model.container.impl.Inventory;
import org.trident.net.packet.PacketBuilder;
import org.trident.net.packet.PacketDecoder;
import org.trident.net.packet.PacketEncoder;
import org.trident.util.Constants;
import org.trident.util.FileUtils;
import org.trident.util.IsaacRandom;
import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.util.NameUtils;
import org.trident.world.World;
import org.trident.world.content.Achievements;
import org.trident.world.content.BonusManager;
import org.trident.world.content.Locations;
import org.trident.world.content.Lottery;
import org.trident.world.content.PlayerPanel;
import org.trident.world.content.PlayerPunishment;
import org.trident.world.content.StartingHandler;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.clan.ClanChatManager;
import org.trident.world.content.combat.CombatExtras;
import org.trident.world.content.combat.combatdata.magic.Autocasting;
import org.trident.world.content.combat.pvp.BountyHunter;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.minigames.impl.Barrows;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;
import org.trident.world.entity.player.PlayerSaving;

/**
 * A {@link org.niobe.net.StatefulFrameDecoder} which decodes
 * the login requests.
 *
 * @author Gabbe
 */
public final class LoginDecoder extends FrameDecoder {

	private static final BigInteger RSA_MODULUS = new BigInteger("108871585040147527733156764633728259888635982356079582017538489077925711024498503786839065025850735108850638613862181844824991472323348340371543983004207568271791143033153064460790683686859744922339615691855859844025297316648264176584215363918318854573443204591084316718921015779024923217466603152414330167261");
	private static final BigInteger RSA_EXPONENT = new BigInteger("79227056986659686782271273004741873607409971016771402804224264478148725302964472627705398312600107707269247246171096884864938787220121901353428988776365508796712726160271805202806194667097848334507349315662178990700799440524511168198531805211295501127802702521057273011320283551526502427258995354590771993473");

	private static final int CONNECTED = 0;
	private static final int LOGGING_IN = 1;
	private int state = CONNECTED;
	private long seed;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		if(!channel.isConnected()) {
			return null;
		}
		switch (state) {
		case CONNECTED:
			if (buffer.readableBytes() < 2) {
				return null;
			}
			int request = buffer.readUnsignedByte();
			if (request != 14) {
				System.out.println("Invalid login request: " + request);
				channel.close();
				return null;
			}
			buffer.readUnsignedByte();
			seed = new SecureRandom().nextLong();
			channel.write(new PacketBuilder().writeLong(0).writeByte((byte) 0).writeLong(seed).toPacket());
			state = LOGGING_IN;
			return null;
		case LOGGING_IN:
			if (buffer.readableBytes() < 2) {
				System.out.println("no readable bytes");
				return null;
			}
			int loginType = buffer.readByte();
			if (loginType != 16 && loginType != 18) {
				System.out.println("Invalid login type: " + loginType);
				channel.close();
				return null;
			}			
			int blockLength = buffer.readByte() & 0xff;
			if (buffer.readableBytes() < blockLength) {
				channel.close();
				return null;
			}
			int magicId = buffer.readUnsignedByte();
			if(magicId != 0xFF) {
				System.out.println("Invalid magic id");
				channel.close();
				return null;
			}
			int clientVersion = buffer.readShort();
			int memory =  buffer.readByte();
			if (memory != 0 && memory != 1) {
				System.out.println("Unhandled memory byte value");
				channel.close();
				return null;
			}
			int[] archiveCrcs = new int[9];
			for (int i = 0; i < 9; i++) {
				archiveCrcs[i] = buffer.readInt();
			}
			int length = buffer.readUnsignedByte();
			/**
			 * Our RSA components. 
			 */
			ChannelBuffer rsaBuffer = buffer.readBytes(length);
			BigInteger bigInteger = new BigInteger(rsaBuffer.array());
			bigInteger = bigInteger.modPow(RSA_EXPONENT, RSA_MODULUS);
			rsaBuffer = ChannelBuffers.wrappedBuffer(bigInteger.toByteArray());
			int securityId = rsaBuffer.readByte();
			if(securityId != 10) {
				System.out.println("securityId id != 10.");
				channel.close();
				return null;
			}
			long clientSeed = rsaBuffer.readLong();
			long seedReceived = rsaBuffer.readLong();
			if (seedReceived != seed) {
				System.out.println("Unhandled seed read: [seed, seedReceived] : [" + seed + ", " + seedReceived + "]");
				channel.close();
				return null;
			}
			int[] seed = new int[4];
			seed[0] = (int) (clientSeed >> 32);
			seed[1] = (int) clientSeed;
			seed[2] = (int) (this.seed >> 32);
			seed[3] = (int) this.seed;
			IsaacRandom decodingRandom = new IsaacRandom(seed);
			for (int i = 0; i < seed.length; i++) {
				seed[i] += 50;
			}
			int uid = rsaBuffer.readInt();
			if(uid != 81597323) {
				channel.close();
				return null;
			}
			int hardwareA = rsaBuffer.readInt();
			String username = FileUtils.readString(rsaBuffer);
			String password = FileUtils.readString(rsaBuffer);
			if (username.length() > 12 || password.length() > 20) {
				System.out.println("Username or password length too long");
				return null;
			}
			username = Misc.formatText(username.toLowerCase());
			channel.getPipeline().replace("encoder", "encoder", new PacketEncoder(new IsaacRandom(seed)));
			channel.getPipeline().replace("decoder", "decoder", new PacketDecoder(decodingRandom));
			return login(channel, username, password, hardwareA, clientVersion);
		}
		return null;
	}

	public Player login(Channel channel, String username, String password, int hardwareA, int clientVersion) {
		Player player = new Player(channel).setUsername(username).setLongUsername(NameUtils.stringToLong(username)).setPassword(password).setHardwareNumber(hardwareA).setHostAdress(((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress());
		int response = getResponse(player);
		if(response == LoginResponses.LOGIN_SUCCESSFUL && clientVersion != Constants.GAME_VERSION)
			response = LoginResponses.OLD_CLIENT_VERSION;

		if(!GameServer.LAUNCHED)
			response = LoginResponses.SERVER_LAUNCHING;
		if (response == LoginResponses.LOGIN_SUCCESSFUL)
			response = PlayerSaving.load(player);
		if (PlayerHandler.getPlayerForName(player.getUsername()) != null)
			response = LoginResponses.LOGIN_ACCOUNT_ONLINE;
		else if(response == LoginResponses.LOGIN_SUCCESSFUL && !ConnectionDenyManager.enter(player.getHostAdress()))
			response = LoginResponses.LOGIN_CONNECTION_LIMIT;
		if (response == LoginResponses.LOGIN_SUCCESSFUL) {
			channel.write(new PacketBuilder().writeByte((byte)2).writeByte((byte)player.getRights().ordinal()).writeByte((byte)0).toPacket());
			
			World.register(player);
			
			/*
			 * Login, random crap
			 */
			Logger.log(player.getUsername(), "Player logged in.");
			player.write(new PacketBuilder(249).writeByteA(1).writeLEShortA(player.getIndex()).toPacket());
			player.getAttributes().setRegionChange(true);
			
			
			player.getInventory().refreshItems();
			player.getEquipment().refreshItems();
			player.getPacketSender().sendString(8135, ""+player.getAttributes().getMoneyInPouch()); //Update the money pouch
			player.getPacketSender().sendTabs();
			player.getPacketSender().sendTabInterface(Constants.MAGIC_TAB, player.getAttributes().getSpellbook().getInterfaceId());
			player.getPacketSender().sendTabInterface(Constants.PRAYER_TAB, player.getAttributes().getPrayerbook().getInterfaceId());
			for (Skill skill : Skill.values()) {
				player.getPacketSender().sendSkill(skill);
				player.getSkillManager().updateSkill(skill);
			}
			player.getPacketSender().sendInteractionOption("Follow", 3, false).sendInteractionOption("Trade With", 4, false);
			player.getPacketSender().sendString(4536, "Killcount: "+player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount());
			player.getPacketSender().sendItemContainer(player.getInventory(), Inventory.INTERFACE_ID).sendItemContainer(player.getEquipment(), Equipment.INVENTORY_INTERFACE_ID);
			player.getPacketSender().sendRunStatus().sendRunEnergy(player.getAttributes().getRunEnergy());
			BonusManager.update(player);
			player.getAttributes().setInterfaceId(-1);
			player.getPacketSender().sendConfig(427, !player.getAttributes().isAcceptingAid() ? 1 : 0);
			CurseHandler.deactivateAll(player);
			PrayerHandler.deactivateAll(player);
			Achievements.initTab(player);
			TaskManager.submit(new PlayerSkillsTask(player));
			if(player.getPlayerCombatAttributes().getSpecialAttackAmount() < 10.0)
				TaskManager.submit(new PlayerSpecialAmountTask(player));
			if(!player.getAttributes().hasStarted())
				StartingHandler.setupNewAccount(player);
			Autocasting.resetAutocast(player, true);
			BountyHunter.handleLogin(player);
			BonusManager.sendCurseBonuses(player);
			if(player.getAttributes().getPrayerRenewalPotionTimer() > 0)
				TaskManager.submit(new PrayerRenewalPotionTask(player));
			if(player.getAttributes().getOverloadPotionTimer() > 0)
				TaskManager.submit(new OverloadPotionTask(player));
			if(player.getAttributes().getNewPlayerDelay() > 0)
				TaskManager.submit(new NewPlayerTask(player));
			if(player.getCombatAttributes().getCurrentPoisonDamage() > 0)
				CombatExtras.poison(player, player.getCombatAttributes().getCurrentPoisonDamage(), true);
			player.getMovementQueue().stopMovement();
			Barrows.handleLogin(player);
			WeaponHandler.update(player);
			SoundEffects.handleLogin(player);
			ClanChatManager.handleLogin(player);
			player.getAdvancedSkills().getSummoning().login();
			player.getAdvancedSkills().getFarming().load(player);
			if(player.getAttributes().hasStarted() && System.currentTimeMillis() - player.getAttributes().getLastVote() >= 43200000)
				DialogueManager.start(player, 381);
			if(player.getAttributes().getResetPosition() != null) {
				player.moveTo(player.getAttributes().getResetPosition());
				player.getAttributes().setResetPosition(null);
			}
			player.getAdvancedSkills().getSlayer().getDuoSlayer().updateDuoSlayer();
			player.getRelations().setPrivateMessageId(1).updateLists(true);
			int playerAmount = (int) (World.getPlayers().size() * 1.7);

			player.getPacketSender().sendMessage("Welcome to Trident , there are ["+playerAmount+"] players online.");
			if(player.getAttributes().experienceLocked())
				player.getPacketSender().sendMessage("@red@WYour XP is currently locked. Unlock it to recieve XP.");
			if(Misc.isWeekend())
				//player.getPacketSender().sendMessage("@red@Bonus XP weekend is active!");
			Lottery.onLogin(player);
			PlayerPanel.refreshPanel(player);
			PlayerPanel.sendPlayersOnline();		
			player.getPointsHandler().refreshPanel();
			player.getPacketSender().sendRights();
			if(player.getRights() == PlayerRights.SUPPORT || player.getRights() == PlayerRights.MODERATOR || player.getRights() == PlayerRights.ADMINISTRATOR)
				PlayerHandler.sendGlobalPlayerMessage("<img=10><col=6600CC> "+Misc.formatText(player.getRights().toString().toLowerCase())+" "+player.getUsername()+" has just logged in, feel free to message me for support.");
			Locations.login(player);
			
			return player;
		} else {
			sendReturnCode(channel, response);
			return null;
		}
	}

	public static void sendReturnCode(final Channel channel, final int code) {
		channel.write(new PacketBuilder().writeByte((byte) code).toPacket()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(final ChannelFuture arg0) throws Exception {
				arg0.getChannel().close();
			}
		});
	}

	private static final int getResponse(Player player) {
		if (World.getPlayers().isFull()) {
			return LoginResponses.LOGIN_WORLD_FULL;
		} else if(GameServer.UPDATING) {
			return LoginResponses.LOGIN_GAME_UPDATE;
		} else if (!NameUtils.isValidName(player.getUsername())) {
			return LoginResponses.LOGIN_INVALID_CREDENTIALS;
		} else if (PlayerPunishment.banned(player.getUsername().toLowerCase())) {
			return LoginResponses.LOGIN_DISABLED_ACCOUNT;
		} else if (PlayerPunishment.IPBanned(player.getHostAdress())) {
			return LoginResponses.LOGIN_DISABLED_IP;
		} else if(player.getUsername().startsWith(" ")) {
			return LoginResponses.USERNAME_STARTS_WITH_SPACE;
		} else if(ConnectionDenyManager.isBlocked(player.getHostAdress()))
			return LoginResponses.LOGIN_REJECT_SESSION;
		else if(ConnectionDenyManager.isBlocked(player.getHardwareNumber()))
			return LoginResponses.LOGIN_DISABLED_COMPUTER;
		return LoginResponses.LOGIN_SUCCESSFUL;
	}

}
