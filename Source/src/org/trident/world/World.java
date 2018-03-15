package org.trident.world;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

import org.trident.engine.task.TaskManager;
import org.trident.model.GameObject;
import org.trident.net.login.ConnectionDenyManager;
import org.trident.world.clip.region.RegionClipping;
import org.trident.world.clip.region.RegionManager;
import org.trident.world.content.DwarfMultiCannon;
import org.trident.world.content.Locations;
import org.trident.world.content.PlayerPanel;
import org.trident.world.content.clan.ClanChatManager;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.pvp.BountyHunter;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.content.skills.impl.hunter.Hunter;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.Entity;
import org.trident.world.entity.EntityContainer;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;
import org.trident.world.entity.player.PlayerSaving;
import org.trident.world.entity.updating.NpcUpdateSequence;
import org.trident.world.entity.updating.PlayerUpdateSequence;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
/**
 * Represents the world that contains every related to entities.
 * Processes entitys aswell, sending updates needed to the client.
 * 
 * @author Gabbe and lare96
 */

public class World {

	/** All of the registered players. */
	private static EntityContainer<Player> players = new EntityContainer<>(1000);

	/** All of the registered NPCs. */
	private static EntityContainer<NPC> npcs = new EntityContainer<>(2500);

	/** Used to block the game thread until updating has completed. */
	private static Phaser synchronizer = new Phaser(1);

	/** A thread pool that will update players in parallel. */
	private static ExecutorService updateExecutor = Executors.newFixedThreadPool(
			Runtime.getRuntime().availableProcessors(),
			new ThreadFactoryBuilder().setNameFormat("UpdateThread").setPriority(
					Thread.MAX_PRIORITY).build());

	public static void register(Entity entity) {
		if(entity.isPlayer()) {
			Player player = (Player) entity;
			if(players.add(player))
				System.out.println("[World] Registering player - [username, password] : ["+player.getUsername()+", "+player.getPassword()+"]");
		} else if(entity.isNpc()) {
			NPC npc = (NPC) entity;
			if(npcs.add(npc))
				RegionManager.register(npc);
		} else if(entity.isGameObject()) {
			GameObject gameObject = (GameObject) entity;
			RegionClipping.addObject(gameObject);
			for (Player p : players) {
				if(p == null)
					continue;
				if (p.getPosition().isWithinDistance(gameObject.getPosition()))
					p.getPacketSender().sendObject(gameObject);
			}
		}
	}

	public static void deregister(Entity entity) {
		if(entity.isPlayer()) {
			Player player = (Player) entity;
			if(player.logout() || player.getAttributes().forceLogout()) {
				player.getAttributes().setLoggedOut(true);
				player.getPacketSender().sendLogout();
				if(player.getAttributes().getRegionInstance() != null)
					player.getAttributes().getRegionInstance().destruct();
				if(player.getTrading().inTrade())
					player.getTrading().declineTrade(true);
				CombatHandler.resetAttack(player);
				Hunter.handleLogout(player);
				TaskManager.cancelTasks(player);
				Locations.logout(player);
				if(player.getAdvancedSkills().getSummoning().getFamiliar() != null)
					World.deregister(player.getAdvancedSkills().getSummoning().getFamiliar().getSummonNpc());
				player.getAdvancedSkills().getFarming().save(player);
				player.getRelations().updateLists(false);
				CombatHandler.resetAttack(player);
				BountyHunter.handleLogout(player);
				ClanChatManager.leave(player, false);
				Dungeoneering.leave(player);
				PrayerHandler.deactivatePrayers(player);
				CurseHandler.deactivateCurses(player);
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getAttributes().getCannon() != null)
					DwarfMultiCannon.pickupCannon(player, player.getAttributes().getCannon(), true);
				PlayerSaving.save(player);
				ConnectionDenyManager.exit(player.getHostAdress());
				if (World.getPlayers().contains(player))
					World.getPlayers().remove(player);
				PlayerPanel.sendPlayersOnline();
				org.trident.util.Logger.log(player.getUsername(), "Player logged out.");
				System.out.println("[World] Deregistering player - [username, password] : [" + player.getUsername() + ", " + player.getPassword() + "]");
				player.getChannel().close();
			}
		} else if(entity.isNpc()) {
			NPC npc = (NPC) entity;
			npc.getAttributes().setVisible(false);
			RegionManager.unregister(npc);
			if (npcs.get(npc.getIndex()) != null)
				npcs.remove(npc);
			npc = null;
		} else if(entity.isGameObject()) {
			GameObject gameObject = (GameObject) entity;
			for (Player player : players) {
				if(player == null)
					continue;
				if (player.getPosition().isWithinDistance(gameObject.getPosition())) {
					player.getPacketSender().sendObjectRemoval(gameObject);
				}
			}
			RegionClipping.removeObject(gameObject);
		}
	}

	/**
	 * Gets the container of players.
	 *
	 * @return the container of players.
	 */
	public static EntityContainer<Player> getPlayers() {
		return players;
	}
	/**
	 * Gets the container of npcs.
	 *
	 * @return the container of npcs.
	 */
	public static EntityContainer<NPC> getNpcs() {
		return npcs;
	}

	public static void tick() {
		try {
			// First we construct the update sequences.
			WorldUpdateSequence<Player> playerUpdate = new PlayerUpdateSequence(synchronizer, updateExecutor);
			WorldUpdateSequence<NPC> npcUpdate = new NpcUpdateSequence();
			// Then we execute pre-updating code.
			players.forEach(playerUpdate::executePreUpdate);
			npcs.forEach(npcUpdate::executePreUpdate);
			// Then we execute parallelized updating code.
			synchronizer.bulkRegister(players.size());
			players.forEach(playerUpdate::executeUpdate);
			synchronizer.arriveAndAwaitAdvance();
			// Then we execute post-updating code.
			players.forEach(playerUpdate::executePostUpdate);
			npcs.forEach(npcUpdate::executePostUpdate);
		} catch (Exception e) {
			e.printStackTrace();
			PlayerHandler.saveAll();
		}
	}
}

