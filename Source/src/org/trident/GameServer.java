package org.trident;

import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.trident.engine.GameEngine;
import org.trident.model.definitions.GameObjectDefinition;
import org.trident.model.definitions.ItemDefinition;
import org.trident.model.definitions.NPCDefinition;
import org.trident.model.definitions.NPCDrops;
import org.trident.model.definitions.NPCSpawns;
import org.trident.model.definitions.ShopManager;
import org.trident.net.PipelineFactory;
import org.trident.net.login.ConnectionDenyManager;
import org.trident.util.ShutdownHook;
import org.trident.util.StarterSaver;
import org.trident.world.clip.region.RegionClipping;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.Lottery;
import org.trident.world.content.PlayerPunishment;
import org.trident.world.content.clan.ClanChatManager;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.skills.impl.hunter.Hunter;
import org.trident.world.content.skills.impl.runecrafting.DesoSpan;
import org.trident.world.entity.npc.custom.CustomNPC;

/**
 * The starting point of Desolace, this is where netty is launched
 * and then game necessities are initialized, such as definitions and world tasks.
 * The world processes the game.
 * @author Gabbe and Samy
 * @credits Relex lawl: For the framework and clipping BASE
 */

public class GameServer {

	/**
	 * The GameServer's logger to print out information and errors.
	 */
	private static final Logger logger = Logger.getLogger(GameServer.class.getName());

	/**
	 * The starting point where everything is initialized.
	 * @param args
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */

	public static void main(String[] args) throws Exception {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		logger.info("Initializing Trident.");

		//Initialize utilities
		GameObjectDefinition.init();
		RegionClipping.init();
		NPCDefinition.init();
		ItemDefinition.init();
		ShopManager.init();
		NPCSpawns.init();
		CustomNPC.init();
		NPCDrops.init();
		ClanChatManager.init();
		PlayerPunishment.init();
		DialogueManager.init();
		StarterSaver.init();
		Lottery.init();
		CustomObjects.initalizeObjects();
		Hunter.spawnImplings();
		DesoSpan.spawnEnergySources();
		ConnectionDenyManager.init();
		logger.info("Sucessfully loaded all utilities!");
		
		//Binding
		ServerBootstrap serverBootstrap = new ServerBootstrap (new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        serverBootstrap.setPipelineFactory (new PipelineFactory(new HashedWheelTimer()));
        serverBootstrap.bind (new InetSocketAddress(43595));
        
		// Initialize and start the engine.
		GameEngine.init();
		logger.info("The engine is now running!");
		LAUNCHED = true;

	}

	public static boolean LAUNCHED, UPDATING;
}
