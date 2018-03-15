package org.trident.world.content.skills.impl.dungeoneering;

import org.trident.model.GameObject;
import org.trident.model.Graphic;
import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.Locations;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

/**
 * @author Gabbe
 */
public class DungeoneeringHandler {

	/**
	 * Handles an object click.
	 * @param p				The player who has clicked an object.
	 * @param id			The object which the player has clicked.
	 * @return				Returns true if the object which the player clicked is related to Dungeoneering, false otherwise.
	 */
	public static boolean handleObject(Player p, GameObject object) {
		if(Dungeoneering.doingDungeoneering(p)) {
			switch(object.getId()) {
			case 45803:
			case 1767:
				DialogueManager.start(p, 346);
				p.getAttributes().setDialogueAction(317);
				return true;
			case 7352:
				if(p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getGatestonePosition() != null) {
					p.moveTo(p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getGatestonePosition());
					p.setEntityInteraction(null);
					p.getPacketSender().sendMessage("You are teleported to your party's gatestone.");
					p.performGraphic(new Graphic(1310));
				} else
					p.getPacketSender().sendMessage("Your party must drop a Gatestone somewhere in the dungeon to use this portal.");
				return true;
			case 7353:
				p.moveTo(new Position(2439, 4956, p.getPosition().getZ()));
				return true;
			case 7321:
				p.moveTo(new Position(2452, 4944, p.getPosition().getZ()));
				return true;
			case 7322:
				if(p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty().getKills() < 2) {
					p.getPacketSender().sendMessage("The portal is not functioning properly.");
					return true;
				}
				p.moveTo(new Position(2455, 4964, p.getPosition().getZ()));
				return true;
			case 7315:
				p.moveTo(new Position(2447, 4956, p.getPosition().getZ()));
				return true;
			case 7316:
				p.moveTo(new Position(2471, 4956, p.getPosition().getZ()));
				return true;
			case 7318:
				p.moveTo(new Position(2464, 4963, p.getPosition().getZ()));
				return true;
			case 7319:
				p.moveTo(new Position(2467, 4940, p.getPosition().getZ()));
				return true;
			case 7324:
				p.moveTo(new Position(2481, 4956, p.getPosition().getZ()));
				return true;
			}
		}
		return false;
	}

	/**
	 * Handles a button clicked.
	 * @param p				The player who has clicked a button.
	 * @param buttonId		The button which the player has clicked.
	 * @return				Returns true if the button which the player clicked is related to Dungeoneering, false otherwise.
	 */
	public static boolean handleButton(Player p, int buttonId) {
		switch(buttonId) {
		/*case -29793: //third interface
			case -30293:
			case -30093:
			case -29993:
			case -29893:
				p.getPacketSender().sendInterface(35333);
				return true;
			case -29790: //Sixth interface
			case -30290:
			case -30190:
			case -30090:
			case -29990:
				p.getPacketSender().sendInterface(35633);
				return true;
			case -29791: //Fifth interface
			case -30291:
			case -30191:
			case -30091:
			case -29891:
				p.getPacketSender().sendInterface(35533);
				return true;
			case -29792: //Fourth interface
			case -30292:
			case -30192:
			case -29992:
			case -29892:
				p.getPacketSender().sendInterface(35433);
				return true;
			case -29794: //Second interface
			case -30194:
			case -30094:
			case -29994:
			case -29894:
				p.getPacketSender().sendInterface(35233);
				return true;
			case -30295: //First interface
			case -30195:
			case -30095:
			case -29995:
			case -29895:
				p.getPacketSender().sendInterface(35733);
				return true;*/
		case 26229:
		case 26226:
			if(Dungeoneering.doingDungeoneering(p)) {
				DialogueManager.start(p, 346);
				p.getAttributes().setDialogueAction(317);
				return true;
			}
			Dungeoneering.leave(p);
			p.getPacketSender().sendInterfaceRemoval();
			p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setDungeoneeringFloor(null);
			return true;
		case 26244:
			if(p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor() == null || p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty() == null || p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty().getOwner() == null)
				return true;
			if(p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty().getOwner() != p) {
				p.getPacketSender().sendMessage("Only the party owner can change the floor.");
				return true;
			}
			if(Dungeoneering.doingDungeoneering(p)) {
				p.getPacketSender().sendMessage("You can't change the floor now!");
				return true;
			}
			DialogueManager.start(p, 343);
			p.getAttributes().setDialogueAction(315);
			return true;
		}
		return false;
	}

	private static final Item[] misc = {new Item(555, 121), new Item(557, 87), new Item(554, 81), new Item(565, 63), new Item(5678), new Item(560, 97), new Item(861, 1), new Item(892, 127), new Item(18161, 2), new Item(18159, 2), new Item(139, 1)};
	public static void handleNpcDeath(Player p, NPC n) {
		if(n.getPosition().getZ() == p.getPosition().getZ()) {
			DungeoneeringFloor floor = p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor();
			if(!floor.getNpcs().contains(n))
				return;
			floor.getNpcs().remove(n);
			floor.getParty().setKills(floor.getParty().getKills()+1);
			updateFrame(floor, 37509, "Party kills: "+floor.getParty().getKills());
			if(floor.getParty().getKills() == 2)
				floor.getParty().sendMessage("@red@You hear a click, the eastern portal is now functioning.");
			int random = Misc.getRandom(300);
			boolean boss = n.getId() == 9939;
			if(random >= 20 && random <= 45 || boss) {
				floor.createGroundItem(new Item(ItemBinding.getRandomBindableItem()), n.getPosition().copy());
				//p.getInventory().add(randomReward);
				//floor.getParty().sendMessage("@red@"+p.getUsername()+" has received the bindable item: "+randomReward.getDefinition().getName());
				if(boss)
					floor.getParty().sendMessage("@red@The boss has been slain! Feel free to exit the dungeon via tha northern ladder.");
			} else if(random >= 100 && random <= 150) {
				int amt = 3000 + Misc.getRandom(10000);
				floor.createGroundItem(new Item(18201, amt), n.getPosition().copy());
				//p.getInventory().add(new Item(18201, amt));
				//floor.getParty().sendMessage("@red@"+p.getUsername()+" has received "+amt+" coins.");
			} else if(random > 150 && random < 250)
				floor.createGroundItem(misc[Misc.getRandom(misc.length-1)], n.getPosition().copy());
			for(Player plr : floor.getParty().getPlayers()) {
				if(plr != null && Locations.goodDistance(plr.getPosition(), p.getPosition(), 10)) {
					int xp =  900 + Misc.getRandom(247);
					int tokens = Misc.getRandom(15);
					if(boss) {
						xp += 5137 + Misc.getRandom(3000);
						tokens += floor.getParty().getPlayers().size() <= 2 ? 150 + Misc.getRandom(55) : 143 + Misc.getRandom(23);
						xp += (plr.getSkillManager().getMaxLevel(Skill.DUNGEONEERING) * 10) * 40;
					}
					plr.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setExperienceReceived(plr.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getExperienceReceived() + xp);
					plr.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setTokensReceived(plr.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getTokensReceived() + tokens);
				}
			}
		}
	}

	public static void updateFrame(DungeoneeringFloor floor, int frame, String s) {
		for(Player p : floor.getParty().getPlayers()) {
			if(p != null)
				p.getPacketSender().sendString(frame, s);
		}
	}

	public static void handlePlayerDeath(Player player) {
		DungeoneeringFloor floor = player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor();
		Position pos = player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getFloor().getEntrance();
		player.moveTo(new Position(pos.getX(), pos.getY(), player.getPosition().getZ()));
		floor.getParty().sendMessage("@red@"+player.getUsername()+" has died and been moved to the lobby.");
		floor.getParty().setDeaths(floor.getParty().getDeaths()+1);
		updateFrame(floor, 37508, "Party deaths: "+floor.getParty().getDeaths());
		player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setExperienceReceived(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getExperienceReceived() - (3000 + Misc.getRandom(6000)));
		if(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getExperienceReceived() < 0)
			player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setExperienceReceived(0);
		player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setTokensReceived(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getTokensReceived() - (20 + Misc.getRandom(10)));
		if(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getTokensReceived() < 0)
			player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setTokensReceived(0);
	}
}
