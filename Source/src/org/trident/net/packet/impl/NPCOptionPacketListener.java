package org.trident.net.packet.impl;

import org.trident.engine.task.impl.FinalizedMovementTask;
import org.trident.engine.task.impl.WalkToAction;
import org.trident.model.Graphic;
import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.definitions.ShopManager;
import org.trident.model.movement.MovementStatus;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.World;
import org.trident.world.content.Achievements;
import org.trident.world.content.Gravestones;
import org.trident.world.content.Locations;
import org.trident.world.content.LoyaltyProgrammeHandler;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.combatdata.magic.CombatMagicSpells;
import org.trident.world.content.combat.combatdata.magic.CombatSpell;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.impl.DefaultNPCDialogue;
import org.trident.world.content.dialogue.impl.SlayerDialogues;
import org.trident.world.content.minigames.impl.Barrows;
import org.trident.world.content.minigames.impl.WarriorsGuild;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.content.skills.impl.hunter.Hunter;
import org.trident.world.content.skills.impl.runecrafting.DesoSpan;
import org.trident.world.content.skills.impl.slayer.SlayerTasks;
import org.trident.world.content.skills.impl.summoning.SummoningData;
import org.trident.world.content.skills.impl.thieving.Pickpocketing;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.content.teleporting.TeleportType;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

public class NPCOptionPacketListener implements PacketListener {


	private static void firstClick(final Player player, final Packet packet) {
		int index = packet.readLEShort();
		if(index < 0 || index > World.getNpcs().size())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		player.setEntityInteraction(npc);
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("First click npc id: "+npc.getId());
		player.getAttributes().setWalkToTask(new WalkToAction(player, npc.getPosition(), npc, new FinalizedMovementTask() {
			@Override
			public void execute() {
				Dialogue dialogue = null;
				switch (npc.getId()) {
				case 6055:
				case 6056:
				case 6057:
				case 6058:
				case 6059:
				case 6060:
				case 6061:
				case 6062:
				case 6063:
				case 6064:
				case 7903:
					Hunter.catchImpling(player, npc);
					break;
				case 8032:
					dialogue = DialogueManager.getDialogues().get(442);
					break;
				case 8022:
				case 8028:
					DesoSpan.siphon(player, npc);
					break;
				case 534:
					player.getAttributes().setDialogueAction(402);
					dialogue = DialogueManager.getDialogues().get(431);
					break;
				case 3001:
					dialogue = DialogueManager.getDialogues().get(420);
					player.getAttributes().setDialogueAction(359);
					break;
				case 3299:
					dialogue = DialogueManager.getDialogues().get(415);
					player.getAttributes().setDialogueAction(357);
					break;
				case 2579:
					dialogue = DialogueManager.getDialogues().get(406);
					player.getAttributes().setDialogueAction(352);
					break;
				case 251:
					dialogue = DialogueManager.getDialogues().get(404);
					player.getAttributes().setDialogueAction(348);
					break;
				case 463:
					player.getAttributes().setDialogueAction(346);
					dialogue = DialogueManager.getDialogues().get(400);
					break;
				case 2192:
					dialogue = DialogueManager.getDialogues().get(392);
					player.getAttributes().setDialogueAction(341);
					break;
				case 2270:
					dialogue = DialogueManager.getDialogues().get(386);
					player.getAttributes().setDialogueAction(336);
					break;
				case 2622:
					dialogue = DialogueManager.getDialogues().get(369);
					player.getAttributes().setDialogueAction(332);
					break;
				case 571:
					dialogue = DialogueManager.getDialogues().get(371);
					player.getAttributes().setDialogueAction(-1);
					break;
				case 2233:
					dialogue = DialogueManager.getDialogues().get(379);
					player.getAttributes().setDialogueAction(-1);
					break;
				case 570:
					dialogue = DialogueManager.getDialogues().get(372);
					player.getAttributes().setDialogueAction(-1);
					break;
				case 572:
					dialogue = DialogueManager.getDialogues().get(375);
					player.getAttributes().setDialogueAction(-1);
					break;
				case 3671:
					dialogue = DialogueManager.getDialogues().get(377);
					player.getAttributes().setDialogueAction(-1);
					break;
				case 970:
					dialogue = DialogueManager.getDialogues().get(378);
					player.getAttributes().setDialogueAction(-1);
					break;
				case 569:
					dialogue = DialogueManager.getDialogues().get(374);
					player.getAttributes().setDialogueAction(-1);
					break;
				case 547:
					dialogue = DialogueManager.getDialogues().get(376);
					player.getAttributes().setDialogueAction(-1);
					break;
				case 530:
					dialogue = DialogueManager.getDialogues().get(373);
					player.getAttributes().setDialogueAction(-1);
					break;
				case 1315:
					dialogue = DialogueManager.getDialogues().get(380);
					player.getAttributes().setDialogueAction(-1);
					break;
				case 945:
					dialogue = DialogueManager.getDialogues().get(367);
					player.getAttributes().setDialogueAction(329);
					break;
				case 4247:
					dialogue = DialogueManager.getDialogues().get(348);
					player.getAttributes().setDialogueAction(320);
					break;
				case 9713:
					dialogue = DialogueManager.getDialogues().get(340);
					player.getAttributes().setDialogueAction(315);
					break;
				case 9712:
					dialogue = DialogueManager.getDialogues().get(331);
					break;
				case 8591:
					//player.nomadQuest[0] = player.nomadQuest[1] = player.nomadQuest[2] = false;
					if(!player.getAttributes().getMinigameAttributes().getNomadAttributes().hasFinishedPart(0) || !player.getAttributes().getMinigameAttributes().getNomadAttributes().hasFinishedPart(1)) {
						DialogueManager.start(player, 319);
						player.getAttributes().setDialogueAction(308);
					} else if(player.getAttributes().getMinigameAttributes().getNomadAttributes().hasFinishedPart(1) && !player.getAttributes().getMinigameAttributes().getNomadAttributes().hasFinishedPart(2)) {
						DialogueManager.start(player, 323);
						player.getAttributes().setDialogueAction(309);
					} else if(player.getAttributes().getMinigameAttributes().getNomadAttributes().hasFinishedPart(2))
						DialogueManager.start(player, 324);
					break;
				case 650:
					DialogueManager.start(player, 318);
					break;
				case 4289:
					DialogueManager.start(player, WarriorsGuild.warriorsGuildDialogue(player));
					player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[1] = true;
					break;
				case 4657:
					dialogue = DialogueManager.getDialogues().get(315);
					player.getAttributes().setDialogueAction(306);
					break;
				case 6565:
					Gravestones.sendGraveInformation(player, npc);
					break;
				case 308:
					DialogueManager.start(player, 312);
					player.getAttributes().setDialogueAction(305);
					break;
				case 6970:
					DialogueManager.start(player, 307);
					player.getAttributes().setDialogueAction(303);
					break;
				case 2024:
					boolean hasBarrows = false;
					for(Item item : player.getInventory().getItems()) {
						for(int i = 0; i < Barrows.brokenBarrows.length; i++) {
							if (item.getId() == Barrows.brokenBarrows[i][1]) {
								hasBarrows = true;
							}
						}
					}
					if(hasBarrows) {
						DialogueManager.start(player, 293);
						player.getAttributes().setDialogueAction(286);
					} else
						DialogueManager.start(player, 292);
					break;
				case 7969:
					DialogueManager.start(player, 283);
					player.getAttributes().setDialogueAction(283);
					break;
				case 278:
					if(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(0) && player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() < 6) {
						DialogueManager.start(player, 271);
						return;
					}
					if(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() == 6) {
						DialogueManager.start(player, 273);
						return;
					}
					DialogueManager.start(player, 261);
					player.getAttributes().setDialogueAction(224);
					break;
				case 847:
					dialogue = DialogueManager.getDialogues().get(242);
					player.getAttributes().setDialogueAction(220);
					break;
				case 2820:
					dialogue = DialogueManager.getDialogues().get(239);
					player.getAttributes().setDialogueAction(219);
					break;
				case 5113:
					dialogue = DialogueManager.getDialogues().get(236);
					player.getAttributes().setDialogueAction(218);
					break;
				case 575:
					dialogue = DialogueManager.getDialogues().get(231);
					player.getAttributes().setDialogueAction(216);
					break;
				case 805:
					dialogue = DialogueManager.getDialogues().get(205);
					player.getAttributes().setDialogueAction(213);
					break;
				case 948:
					dialogue = DialogueManager.getDialogues().get(107);
					player.getAttributes().setDialogueAction(203);
					break;
				case 8459:
					dialogue = DialogueManager.getDialogues().get(101);
					player.getAttributes().setDialogueAction(202);
					break;
				case 437:
					dialogue = DialogueManager.getDialogues().get(98);
					player.getAttributes().setDialogueAction(201);
					break;
				case 4946:
					dialogue = DialogueManager.getDialogues().get(91);
					player.getAttributes().setDialogueAction(200);
					break;
				case 8725:
					dialogue = DialogueManager.getDialogues().get(90);
					player.getAttributes().setDialogueAction(100);
					break;
				case 961:
					dialogue = DialogueManager.getDialogues().get(player.getLocation() == Location.DUEL_ARENA ? 88 : 89);
					player.getAttributes().setDialogueAction(58);
					break;
				case 553:
					dialogue = DialogueManager.getDialogues().get(78);
					player.getAttributes().setDialogueAction(55);
					break;
				case 1658:
					dialogue = DialogueManager.getDialogues().get(73);
					player.getAttributes().setDialogueAction(53);
					break;
				case 802:
					dialogue = DialogueManager.getDialogues().get(67);
					player.getAttributes().setDialogueAction(50);
					break;
				case 4288:
					dialogue = DialogueManager.getDialogues().get(64);
					player.getAttributes().setDialogueAction(44);
					break;
				case 1597:
				case 8275:
				case 9085:
					if(npc.getId() != player.getAdvancedSkills().getSlayer().getSlayerMaster().getNpcId()) {
						player.getPacketSender().sendMessage("This is not your current Slayer master.");
						return;
					}
					if(npc.getId() == 9085 && player.getSkillManager().getMaxLevel(Skill.SLAYER) >= 99)
						dialogue = SlayerDialogues.greetMaster(player);
					else
						dialogue = SlayerDialogues.dialogue(player);
					break;
				case 649:
					dialogue = DialogueManager.getDialogues().get(60);
					player.getAttributes().setDialogueAction(31);
					break;
				case 682:
					dialogue = DialogueManager.getDialogues().get(48);
					player.getAttributes().setDialogueAction(29);
					break;
				case 1:
					dialogue = DialogueManager.getDialogues().get(0);
					break;
				case 946:
					dialogue = DialogueManager.getDialogues().get(25);
					player.getAttributes().setDialogueAction(11);
					break;
				case 550:
				case 1861:
					dialogue = DialogueManager.getDialogues().get(6);
					player.getAttributes().setDialogueAction(3);
					break;
				case 520:
				case 521:
					dialogue = DialogueManager.getDialogues().get(18);
					player.getAttributes().setDialogueAction(8);
					break;
				case 216:
					dialogue = DialogueManager.getDialogues().get(16);
					player.getAttributes().setDialogueAction(7);
					break;
				case 561:
				case 705:
					dialogue = DialogueManager.getDialogues().get(20);
					player.getAttributes().setDialogueAction(9);
					break;
				case 4906:
					dialogue = DialogueManager.getDialogues().get(22);
					player.getAttributes().setDialogueAction(10);
					break;
				case 494:
				case 2271:
					DialogueManager.start(player, 10);
					player.getAttributes().setDialogueAction(0);
					if(npc.getPosition().getX() == 3208 && npc.getPosition().getY() == 3222 && npc.getPosition().getZ() == 2)
						Achievements.handleAchievement(player, Achievements.Tasks.TASK8);
					break;
				default:
					dialogue = new DefaultNPCDialogue(npc);
					break;
				}
				final Dialogue finalDialogue = dialogue;
				if(npc.getCharacterToFollow() != null && npc.getCharacterToFollow().getIndex() != player.getIndex()) {
					player.getPacketSender().sendMessage("This is not your familiar.");
					return;
				}
				player.setEntityInteraction(npc); player.setPositionToFace(npc.getPosition().copy());
				if(npc.getId() != 6565)
					npc.setPositionToFace(player.getPosition());
				if (finalDialogue != null) {
					DialogueManager.start(player, finalDialogue);
				}
			};
		}));
	}

	private static void attackNPC(final Player player, final Packet packet) {
		int index = packet.readShortA();
		if(index < 0 || index > World.getNpcs().size())
			return;
		NPC gc = World.getNpcs().get(index);
		if(gc == null || gc.getConstitution() <= 0) {
			player.getMovementQueue().stopMovement();
			return;
		}
		if(gc.getId() == 650) {
			ShopManager.getShops().get(5).open(player);
			return;
		}
		CombatHandler.setProperAttackType(player);
		CombatHandler.attack(player, gc);
		player.setEntityInteraction(gc);
	}

	@Override
	public void execute(Player player, Packet packet) {
		if(player.isTeleporting() || player.getMovementQueue().getMovementStatus() == MovementStatus.CANNOT_MOVE)
			return;
		switch (packet.getOpcode()) {
		case ATTACK_NPC:
			attackNPC(player, packet);
			break;
		case FIRST_CLICK_OPCODE:
			firstClick(player, packet);
			break;
		case SECOND_CLICK_OPCODE:
			handleSecondClick(player, packet);
			break;
		case THIRD_CLICK_OPCODE:
			handleThirdClick(player, packet);
			break;
		case FOURTH_CLICK_OPCODE:
			handleFourthClick(player, packet);
			break;
		case MAGE_NPC:
			int npcIndex = packet.readLEShortA();
			int spellId = packet.readShortA();
			if(npcIndex < 0 || npcIndex > World.getNpcs().size())
				return;
			NPC n = World.getNpcs().get(npcIndex);
			if(n == null || !n.getAttributes().isAttackable() || n.getConstitution() <= 0) {
				player.getMovementQueue().stopMovement();
				return;
			}
			CombatMagicSpells cbSpell = CombatMagicSpells.getSpell(spellId);
			if(cbSpell == null) {
				player.getMovementQueue().stopMovement();
				return;
			}
			CombatSpell spell = cbSpell.getSpell();
			player.getPlayerCombatAttributes().getMagic().setCurrentSpell(spell);
			player.getCombatAttributes().setAttackType(AttackType.MAGIC);
			CombatHandler.attack(player, n);
		}
	}

	public void handleSecondClick(final Player player, Packet packet) {
		int index = packet.readLEShortA();
		if(index < 0 || index > World.getNpcs().size())
			return;
		final NPC npc = World.getNpcs().get(index);
		if(npc == null)
			return;
		final int npcId = npc.getId();
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Second click npc id: "+npcId);
		player.setEntityInteraction(npc);
		if(SummoningData.beastOfBurden(npcId) && Locations.goodDistance(npc.getPosition().copy(), player.getPosition().copy(), 3)) {
			player.getAdvancedSkills().getSummoning().store();
			return;
		}
		player.getAttributes().setWalkToTask(new WalkToAction(player, npc.getPosition(), npc, new FinalizedMovementTask() {
			@Override
			public void execute() {
				player.getAttributes().setCurrentInteractingNPC(npc);
				if(Pickpocketing.isPickPocketNPC(npcId)) {
					Pickpocketing.pickPocket(player, npc);
					return;
				}
				if(SummoningData.beastOfBurden(npcId) && Locations.goodDistance(npc.getPosition().copy(), player.getPosition().copy(), 3)) {
					player.getAdvancedSkills().getSummoning().store();
					return;
				}
				if(npc.getCharacterToFollow() != null && npc.getCharacterToFollow().getIndex() != player.getIndex()) {
					player.getPacketSender().sendMessage("This is not your familiar.");
					return;
				}
				player.setEntityInteraction(npc);
				npc.setPositionToFace(player.getPosition());
				switch(npcId) {
				case 8032:
					ShopManager.getShops().get(60).open(player);
					break;
				case 534:
					ShopManager.getShops().get(59).open(player);
					break;
				case 2192:
					ShopManager.getShops().get(53).open(player);
					player.getPacketSender().sendMessage("You currently have "+player.getPointsHandler().getConquestPoints()+" Conquest points.");
					break;
				case 7969:
					ShopManager.getShops().get(50).open(player);
					break;
				case 705:
					DialogueManager.start(player, 391);
					player.getAttributes().setDialogueAction(340);
					break;
				case 946:
					ShopManager.getShops().get(2).open(player);
					break;
				case 1861:
					ShopManager.getShops().get(3).open(player);
					break;
				case 571:
					ShopManager.getShops().get(38).open(player);
					break;
				case 569:
					ShopManager.getShops().get(45).open(player);
					break;
				case 547:
					ShopManager.getShops().get(44).open(player);
					break;
				case 570:
					ShopManager.getShops().get(39).open(player);
					break;
				case 572:
					ShopManager.getShops().get(46).open(player);
					break;
				case 3671:
					ShopManager.getShops().get(41).open(player);
					break;
				case 970:
					ShopManager.getShops().get(47).open(player);
					break;
				case 530:
					ShopManager.getShops().get(40).open(player);
					break;
				case 2622:
					player.getAttributes().setDialogueAction(-1);
					ShopManager.getShops().get(36).open(player);
					break;
				case 2233:
					player.getAttributes().setDialogueAction(-1);
					ShopManager.getShops().get(42).open(player);
					break;
				case 1315:
					player.getAttributes().setDialogueAction(-1);
					ShopManager.getShops().get(48).open(player);
					break;
				case 650:
					ShopManager.getShops().get(37).open(player);
					break;
				case 11226:
					if(Dungeoneering.doingDungeoneering(player))
						ShopManager.getShops().get(33).open(player);
					break;
				case 8591:
					DialogueManager.start(player, 329);
					player.getAttributes().setDialogueAction(312);
					break;
				case 546:
					ShopManager.getShops().get(2).open(player);
					break;
				case 5113:
					ShopManager.getShops().get(17).open(player);
					break;
				case 6970:
					ShopManager.getShops().get(25).open(player);
					break;
				case 1439:
					LoyaltyProgrammeHandler.reset(player);
					player.getPacketSender().sendInterface(LoyaltyProgrammeHandler.TITLE_SHOP_INTERFACE);
					break;
				case 548:
					ShopManager.getShops().get(23).open(player);
					break;
				case 561:
					DialogueManager.start(player, DialogueManager.getDialogues().get(21));
					player.getAttributes().setDialogueAction(9);
					break;
				case 682:
					DialogueManager.start(player, DialogueManager.getDialogues().get(9));
					player.getAttributes().setDialogueAction(4);
					break;
				case 550:
					DialogueManager.start(player, DialogueManager.getDialogues().get(8));
					player.getAttributes().setDialogueAction(4);
					break;
				case 2676:
					player.getPacketSender().sendInterface(3559);
					player.getAppearance().setCanChangeAppearance(true);
					break;
				case 575:
					ShopManager.getShops().get(16).open(player);
					player.getAttributes().setDialogueAction(-1);
					break;
				case 3789:
					player.getPacketSender().sendInterface(18730);
					player.getPacketSender().sendString(18729, Integer.toString(player.getPointsHandler().getCommendations()));
					break;
				case 437:
					ShopManager.getShops().get(12).open(player);
					player.getAttributes().setDialogueAction(-1);
					break;
				case 4946:
					ShopManager.getShops().get(11).open(player);
					break;
				case 1658:
					player.getPacketSender().sendInterfaceRemoval();
					DialogueManager.start(player, 28);
					player.getAttributes().setDialogueAction(11);
					break;
				case 553:
					DialogueManager.start(player, 306);
					player.getAttributes().setDialogueAction(11);
					break;
				case 961:
					ShopManager.getShops().get(9).open(player);
					break;
				case 1597:
				case 9085:
					if(npc.getId() != player.getAdvancedSkills().getSlayer().getSlayerMaster().getNpcId()) {
						player.getPacketSender().sendMessage("This is not your current Slayer master.");
						return;
					}
					if(player.getAdvancedSkills().getSlayer().getSlayerTask() == SlayerTasks.NO_TASK)
						player.getAdvancedSkills().getSlayer().assignTask();
					else
						DialogueManager.start(player, SlayerDialogues.findAssignment(player));
					break;
				case 521:
				case 520:
					ShopManager.getShops().get(0).open(player);
					break;
				case 2: //Ranged shop
					DialogueManager.start(player, 8);
					player.getAttributes().setDialogueAction(4);
					break;
				case 494:
				case 2619:
				case 2271:
					player.getBank(player.getAttributes().getCurrentBankTab()).open();
					break;
				}
			}
		}));
	}

	public void handleThirdClick(final Player player, final Packet packet) {
		int index = packet.readShort();
		if(index < 0 || index > World.getNpcs().size())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Third click npc id: "+npc.getId());
		player.getAttributes().setWalkToTask(new WalkToAction(player, npc.getPosition(), npc, new FinalizedMovementTask() {
			@Override
			public void execute() {
				player.getAttributes().setCurrentInteractingNPC(npc);
				switch(npc.getId()) {
				case 534:
					DialogueManager.start(player, 437);
					player.getAttributes().setDialogueAction(402);
					break;
				case 961:
					boolean restored = false;
					for(Skill skill : Skill.values()) {
						if(player.getSkillManager().getCurrentLevel(skill) < player.getSkillManager().getMaxLevel(skill)) {
							player.getSkillManager().setCurrentLevel(skill, player.getSkillManager().getMaxLevel(skill));
							restored = true;
						}
					}
					if(restored) {
						player.performGraphic(new Graphic(1302));
						player.getPacketSender().sendMessage("Your stats have been restored.");
					} else
						player.getPacketSender().sendMessage("Your stats do not need to be restored at the moment.");
					break;
				case 7969:
					player.getPacketSender().sendMessage("This is coming shortly! Make sure to complete the achievements meanwhile.");
					break;
				case 705:
					ShopManager.getShops().get(6).open(player);
					break;
				case 946:
					ShopManager.getShops().get(1).open(player);
					break;
				case 1861:
					ShopManager.getShops().get(4).open(player);
					break;
				case 6565:
					Gravestones.blessGravestone(player, npc);
					break;
				case 553:
					TeleportHandler.teleportPlayer(player, new Position(2910, 4831), TeleportType.NORMAL);
					break;
				case 1597:
				case 8275:
				case 9085:
					ShopManager.getShops().get(30).open(player);
					break;
				}
			}
		}));
	}

	public void handleFourthClick(final Player player, final Packet packet) {
		int index = packet.readLEShort();
		if(index < 0 || index > World.getNpcs().size())
			return;
		final NPC npc = World.getNpcs().get(index);
		if (npc == null)
			return;
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Fourth click npc id: "+npc.getId());
		player.getAttributes().setWalkToTask(new WalkToAction(player, npc.getPosition(), npc, new FinalizedMovementTask() {
			@Override
			public void execute() {
				player.getAttributes().setCurrentInteractingNPC(npc);
				switch(npc.getId()){
				case 705:
					ShopManager.getShops().get(22).open(player);
					break;
				case 6565:
					Gravestones.demolishGravestone(player, npc);
					break;
				case 1597:
				case 9085:
				case 8275:
					player.getPacketSender().sendString(36030, "Current Points:   "+player.getPointsHandler().getSlayerPoints());
					player.getPacketSender().sendInterface(36000);
					break;
				}
			}
		}));
	}

	public static final int ATTACK_NPC = 72, FIRST_CLICK_OPCODE = 155, MAGE_NPC = 131, SECOND_CLICK_OPCODE = 17, THIRD_CLICK_OPCODE = 21, FOURTH_CLICK_OPCODE = 18;
}
