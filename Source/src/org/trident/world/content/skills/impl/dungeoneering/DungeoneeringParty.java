package org.trident.world.content.skills.impl.dungeoneering;

import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.model.Flag;
import org.trident.model.GroundItem;
import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.util.Constants;
import org.trident.world.World;
import org.trident.world.content.PlayerPanel;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.impl.DungeoneeringPartyInvitationDialogue;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerSaving;

/**
 * @author Gabbe
 */
public class DungeoneeringParty {

	public DungeoneeringParty(Player owner) {
		this.owner = owner;
		player_members = new CopyOnWriteArrayList<Player>();
		player_members.add(owner);
	}

	private Player owner;
	private CopyOnWriteArrayList<Player> player_members;
	private boolean hasEnteredDungeon;
	private int kills, deaths;

	public void invite(Player p) {
		if(getOwner() == null || p == getOwner())
			return;
		if(hasEnteredDungeon) {
			getOwner().getPacketSender().sendMessage("You cannot invite anyone right now.");
			return;
		}
		if(player_members.size() >= 5) {
			getOwner().getPacketSender().sendMessage("Your party is full.");
			return;
		}
		if(p.getLocation() != Location.DAEMONHEIM) {
			getOwner().getPacketSender().sendMessage("That player is not in Deamonheim.");
			return;
		}
		if(player_members.contains(p)) {
			getOwner().getPacketSender().sendMessage("That player is already in your party.");
			return;
		}
		if(p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor() != null && p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty() != null) {
			getOwner().getPacketSender().sendMessage("That player is currently in another party.");
			return;
		}
		if(System.currentTimeMillis() - getOwner().getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getLastInvitation() < 8000) {
			getOwner().getPacketSender().sendMessage("You must wait 8 seconds before every invitation.");
			return;
		}
		if(p.getAttributes().getInterfaceId() > 0) {
			getOwner().getPacketSender().sendMessage("That player is currently busy.");
			return;
		}
		getOwner().getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setLastInvitation(System.currentTimeMillis());
		DialogueManager.start(p, new DungeoneeringPartyInvitationDialogue(getOwner(), p));
		getOwner().getPacketSender().sendMessage("An invitation has been sent to "+p.getUsername()+".");
	}

	public void add(Player p) {
		sendMessage(""+p.getUsername()+" has joined the party.");
		p.getPacketSender().sendMessage("You've joined "+getOwner().getUsername()+"'s party.");
		player_members.add(p);
		p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setDungeoneeringFloor(getOwner().getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor());
		setupInterface(p, true);
		refreshInterface();		
	}

	public void remove(Player p) {
		player_members.remove(p);
		setupInterface(p, false);
		if(p == getOwner()) {
			for(Player member : player_members) {
				if(member != null && member.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor() != null && member.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty() == this) {
					member.getPacketSender().sendMessage("Your party has been deleted by the party's leader.");
					remove(member);
				}
			}
			if(hasEnteredDungeon()) {
				for(NPC npc : p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getNpcs()) {
					if(npc != null && npc.getPosition().getZ() == p.getPosition().getZ())
						World.deregister(npc);
				}
				for(GroundItem groundItem : p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getGroundItems()) {
					if(groundItem != null)
						GroundItemManager.remove(groundItem, true);
				}
			}
		} else {
			sendMessage(p.getUsername()+" has left the party.");
			if(hasEnteredDungeon()) {
				if(p.getInventory().contains(Dungeoneering.DUNGEONEERING_GATESTONE_ID)) {
					p.getInventory().delete(Dungeoneering.DUNGEONEERING_GATESTONE_ID, 1);
					getOwner().getInventory().add(Dungeoneering.DUNGEONEERING_GATESTONE_ID, 1);
				}
			}
		}
		if(hasEnteredDungeon()) {
			for(Item t : p.getEquipment().getItems()) {
				if(t != null && t.getId() > 0 && t.getId() != 15707) {
					p.getEquipment().delete(t);
				}
			}
			for(Item t : p.getInventory().getItems()) {
				if(t != null && t.getId() > 0 && t.getId() != 15707)
					p.getInventory().delete(t);
			}
			p.getEquipment().refreshItems();
			p.getInventory().refreshItems();
			WeaponHandler.update(p);
			p.getUpdateFlag().flag(Flag.APPEARANCE);
			p.moveTo(new Position(3450, 3715));
			if(p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getExperienceReceived() > 0 && p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getTokensReceived() > 0) {
				p.getPacketSender().sendMessage("You received "+p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getExperienceReceived()+" Dungeoneering experience and "+p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getTokensReceived()+" tokens.");
				PlayerPanel.refreshPanel(p);
				p.getSkillManager().addExperience(Skill.DUNGEONEERING, p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getExperienceReceived(), false);
				p.getPointsHandler().setDungeoneeringTokens(true, p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getTokensReceived());
				p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setExperienceReceived(0).setTokensReceived(0);
			}
			PrayerHandler.deactivateAll(p);
			CurseHandler.deactivateAll(p);
			for(Skill skill : Skill.values())
				p.getSkillManager().setCurrentLevel(skill, p.getSkillManager().getMaxLevel(skill));
		}
		p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setDungeoneeringFloor(null);
		refreshInterface();
		PlayerSaving.save(p);
	}

	public void refreshInterface() {
		for(Player member : getPlayers()) {
			if(member != null) {
				for(int s = 26236; s < 26240; s++)
					member.getPacketSender().sendString(s, "");
				member.getPacketSender().sendString(26235, getOwner().getUsername()+"'s Party");
				member.getPacketSender().sendString(26240, member.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getFloor() == null ? "-" : ""+(member.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getFloor().ordinal()+1));
				for(int i = 0; i < getPlayers().size(); i++) {
					Player p = getPlayers().get(i);
					if(p != null) {
						if(p == getOwner())
							continue;
						member.getPacketSender().sendString(26235+i, p.getUsername());
					}
				}
			}
		}
	}

	public void sendMessage(String message) {
		for(Player member : getPlayers()) {
			if(member != null) {
				member.getPacketSender().sendMessage(message);
			}
		}
	}

	public static void create(Player p) {
		if(p.getLocation() != Location.DAEMONHEIM) {
			p.getPacketSender().sendMessage("You must be in Daemonheim to create a party.");
			return;
		}
		if(p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor() != null) {
			p.getPacketSender().sendMessage("You are already in a Dungeoneering party.");
			return;
		}
		if(p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor() == null)
			p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setDungeoneeringFloor(new DungeoneeringFloor(new DungeoneeringParty(p), null));
		else if(p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty() == null)
			p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().setParty(new DungeoneeringParty(p));
		setupInterface(p, true);
		p.getPacketSender().sendMessage("@red@A Dungeoneering party has been created for you.").sendMessage("@red@You can invite other players.");
	}

	public static void setupInterface(Player p, boolean doingDung) {
		p.getPacketSender().sendTabInterface(Constants.QUESTS_TAB, doingDung ? Dungeoneering.DUNGEONEERING_PARTY_INTERFACE : 639);
		p.getPacketSender().sendString(1, doingDung ? "[DUNG]true" : "[DUNG]false");
		if(doingDung) {
			p.getPacketSender().sendTab(Constants.QUESTS_TAB);
			p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty().refreshInterface();
		}
	}

	public Player getOwner() {
		return owner;
	}

	public CopyOnWriteArrayList<Player> getPlayers() {
		return player_members;
	}

	public boolean hasEnteredDungeon() {
		return hasEnteredDungeon;
	}

	public void enteredDungeon(boolean hasEnteredDungeon) {
		this.hasEnteredDungeon = hasEnteredDungeon;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
}
