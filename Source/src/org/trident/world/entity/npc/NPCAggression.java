package org.trident.world.entity.npc;

import org.trident.world.content.Locations;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * Handles NPC's aggression and targets players.
 * @author Gabbe
 */
public class NPCAggression {

	public static void processFor(Player player) {
		if(player == null)
			return;
		boolean multi = Location.inMulti(player);
		for(NPC npc : player.getAttributes().getLocalNpcs()) {
			if(!multi) {
				if(player.getCombatAttributes().isTargeted() && !processTargetation(player))
					return;
			}
			if(npc == null || !npc.getAttributes().isAggressive() || npc.getConstitution() <= 0 || npc.getAttributes().isWalkingHome() && player.getPosition().getZ() < 4 || !Locations.goodDistance(player.getPosition(), npc.getPosition(), npc.getLocation() != Location.GODWARS_DUNGEON ? 7 : 25))
				continue;
			processTargetation(npc);
			if(CombatHandler.inCombat(npc) && !Location.inMulti(npc) && npc.getCombatAttributes().getCurrentEnemy() == player)
				continue;
			if(NPCData.godwarsDungeonBoss(npc.getId()) && !player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom() || player.getAttributes().getInactiveTimer() >= 15 || player.getSkillManager().getCombatLevel() > (npc.getDefinition().getLevel() * 2) && player.getPosition().getZ() < 4)
				continue;
			player.getCombatAttributes().setTargeted(true).setLastDamageReceived(System.currentTimeMillis());
			CombatHandler.setAttack(npc, player);
			if(!multi)
				break;
		}
	}

	public static boolean processTargetation(GameCharacter gc) {
		if((gc.getCombatAttributes().isTargeted() || gc.getCombatAttributes().getLastAttacker() != null) && System.currentTimeMillis() - gc.getCombatAttributes().getLastDamageReceived() > 8000) {
			gc.getCombatAttributes().setLastAttacker(null).setTargeted(false).getHitMap().clear();
			return true;
		}
		return false;
	}
}
