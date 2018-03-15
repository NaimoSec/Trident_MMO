package org.trident.net.packet.impl;

import java.util.List;

import org.trident.model.Animation;
import org.trident.model.Difficulty;
import org.trident.model.Flag;
import org.trident.model.GameObject;
import org.trident.model.Graphic;
import org.trident.model.Item;
import org.trident.model.MagicSpellbook;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.container.impl.Bank;
import org.trident.model.container.impl.Bank.BankSearchAttributes;
import org.trident.model.definitions.ShopManager;
import org.trident.model.inputhandling.impl.EnterAmountOfTicketsToExchange;
import org.trident.model.inputhandling.impl.EnterClanChatToJoin;
import org.trident.model.inputhandling.impl.EnterSyntaxToBankSearchFor;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.util.Constants;
import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.Achievements;
import org.trident.world.content.Artifacts;
import org.trident.world.content.BankPin;
import org.trident.world.content.BonusManager;
import org.trident.world.content.Consumables;
import org.trident.world.content.Effigies;
import org.trident.world.content.Emotes;
import org.trident.world.content.ExperienceLamps;
import org.trident.world.content.ItemLending;
import org.trident.world.content.ItemsKeptOnDeath;
import org.trident.world.content.Lottery;
import org.trident.world.content.LoyaltyProgrammeHandler;
import org.trident.world.content.MoneyPouch;
import org.trident.world.content.PlayerPanel;
import org.trident.world.content.PriceChecker;
import org.trident.world.content.ReportPlayer;
import org.trident.world.content.SkillGuides;
import org.trident.world.content.Skillcapes;
import org.trident.world.content.StartingHandler;
import org.trident.world.content.Achievements.Tasks;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.clan.ClanChat;
import org.trident.world.content.clan.ClanChatDropShare;
import org.trident.world.content.clan.ClanChatManager;
import org.trident.world.content.clan.ClanChatMessageColor;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.combatdata.magic.Autocasting;
import org.trident.world.content.combat.combatdata.magic.MagicSpells;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.impl.ChampionsGuildDialogues;
import org.trident.world.content.dialogue.impl.HelpbookDialogues;
import org.trident.world.content.dialogue.impl.LotteryDialogues;
import org.trident.world.content.dialogue.impl.MoreLoyaltyRewardDialogues;
import org.trident.world.content.dialogue.impl.SlayerDialogues;
import org.trident.world.content.dialogue.impl.TutorialDialogues;
import org.trident.world.content.minigames.impl.ArcheryCompetition;
import org.trident.world.content.minigames.impl.Barrows;
import org.trident.world.content.minigames.impl.Dueling;
import org.trident.world.content.minigames.impl.FightCave;
import org.trident.world.content.minigames.impl.FightPit;
import org.trident.world.content.minigames.impl.PestControl;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.content.quests.Nomad;
import org.trident.world.content.quests.RecipeForDisaster;
import org.trident.world.content.skills.ChatboxInterfaceSkillAction;
import org.trident.world.content.skills.SkillManager;
import org.trident.world.content.skills.impl.agility.Agility;
import org.trident.world.content.skills.impl.crafting.LeatherMaking;
import org.trident.world.content.skills.impl.crafting.Tanning;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.content.skills.impl.dungeoneering.DungeoneeringFloors;
import org.trident.world.content.skills.impl.dungeoneering.DungeoneeringHandler;
import org.trident.world.content.skills.impl.dungeoneering.ItemBinding;
import org.trident.world.content.skills.impl.fishing.Fishing;
import org.trident.world.content.skills.impl.fletching.Fletching;
import org.trident.world.content.skills.impl.herblore.IngridientsBook;
import org.trident.world.content.skills.impl.mining.Mining;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.content.skills.impl.slayer.DuoSlayer;
import org.trident.world.content.skills.impl.slayer.Slayer;
import org.trident.world.content.skills.impl.slayer.SlayerMaster;
import org.trident.world.content.skills.impl.smithing.SmithingData;
import org.trident.world.content.skills.impl.summoning.PouchMaking;
import org.trident.world.content.skills.impl.summoning.SummoningTab;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.content.teleporting.TeleportType;
import org.trident.world.content.treasuretrails.Sextant;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;
/**
 * This packet listener manages a button that the player has clicked upon.
 * 
 * @author Gabbe
 */

public class ButtonClickPacketListener implements PacketListener {

	//CURRENT DIALOGUE ACTION : 408

	@Override
	public void execute(Player player, Packet packet) {
		int id = packet.readShort();
		//System.out.println(id +"L" +player.getAttributes().getDialogueAction()) ;
		if(player.getRights() == PlayerRights.DEVELOPER)
			player.getPacketSender().sendMessage("Clicked button, actionId: "+id+", dialogue action id: "+player.getAttributes().getDialogueAction());
		WeaponHandler.handleButton(player, id);
		if(Sextant.handleSextantButtons(player, id))
			return;
		/*if(Construction.handleButtonClick(id, player))
			return;*/
		if(Emotes.doEmote(player, id) || SkillGuides.handleInterface(player, id))
			return;
		if(ClanChatMessageColor.setColor(player, id))
			return;
		if(PouchMaking.pouchInterface(player, id))
			return;
		if (!defaultButton(player, id))
			return;
		if(Autocasting.handleAutocast(player, id))
			return;
		if(Fletching.fletchingButton(player, id))
			return;
		if(LeatherMaking.handleButton(player, id) || Tanning.handleButton(player, id))
			return;
		if(TeleportHandler.clickSpell(player, id))
			return;
		if(SoulWars.RewardShop.handleButton(player, id))
			return;
		if(player.getAttributes().getInterfaceId() == 8292) {
			Agility.buyItem(player, id);
			return;
		}
		if(DungeoneeringHandler.handleButton(player, id))
			return;
		if(ExperienceLamps.selectingExperienceReward(player)) {
			ExperienceLamps.handleButton(player, id);
			return;
		}
		SmithingData.handleButtons(player, id);
		if(player.getLocation() == Location.DUEL_ARENA)
			Dueling.handleDuelingButtons(player, id);
		if(LoyaltyProgrammeHandler.handleShop(player, id))
			return;
		if(Slayer.handleRewardsInterface(player, id))
			return;
		switch (id) {
		case 12:
			if(player.getAttributes().getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
				return;
			}
			if(CombatHandler.inCombat(player)) {
				player.getPacketSender().sendMessage("You can't open that right now.");
				return;
			}
			player.getPacketSender().sendBlinkingHint("", "", 0, 0, 0, 0, -1, 0);
			player.getPacketSender().sendInterface(17930);
			player.getMovementQueue().stopMovement();
			break;
		case 350:
			player.getPacketSender().sendMessage("You can autocast spells by left clicking on them and selecting 'autocast'.");
			player.getPacketSender().sendTab(Constants.MAGIC_TAB);
			break;
		case -26362:
			PlayerPanel.refreshPanel(player);
			player.getPacketSender().sendTabInterface(Constants.QUESTS_TAB, 700);
			break;
		case -26162:
			PlayerPanel.refreshPanel(player);
			player.getPacketSender().sendTabInterface(Constants.QUESTS_TAB, 639);
			break;
		case 10001:
			if(player.getAttributes().getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			Consumables.handleHealAction(player);
			break;
		case 12464:
			if(player.getLocation() == Location.DUEL_ARENA) {
				if(!player.getAttributes().isAcceptingAid()) {
					player.getPacketSender().sendMessage("Your aid cannot be toggled here.");
					return;
				}
			}
			player.getAttributes().setAcceptingAid(!player.getAttributes().isAcceptingAid());
			player.getPacketSender().sendMessage(player.getAttributes().isAcceptingAid() ? "You are now accepting aid from other players." : "You are no longer accepting aid from other players.");
			player.getPacketSender().sendConfig(427, !player.getAttributes().isAcceptingAid() ? 1 : 0);
			break;
		case 10000:
		case 950:
			if(player.getAttributes().getInterfaceId() < 0) {
				player.getPacketSender().sendInterface(40030);
				player.getPacketSender().sendMessage("This is a work in progress notify staff if there are any issues.");
			} else 
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			break;
		case 14175:
			if(player.getAttributes().getItemToDrop() != null && player.getInventory().contains(player.getAttributes().getItemToDrop().getId())) {
				if(player.getTrading().getItemLending().getBorrowedItem() != null && ItemLending.borrowedItem(player, player.getAttributes().getItemToDrop().getId()))
					ItemLending.returnBorrowedItem(player.getTrading().getItemLending().getBorrowedItem());
				else
					player.getPacketSender().sendMessage("Your "+player.getAttributes().getItemToDrop().getDefinition().getName()+" vanishes as it touches the ground..");
				ItemBinding.unbindItem(player, player.getAttributes().getItemToDrop().getId());
				player.getInventory().delete(player.getAttributes().getItemToDrop()).refreshItems();
				Logger.log(player.getUsername(), "Player destroyed item: "+player.getAttributes().getItemToDrop().getDefinition().getName()+" x"+Misc.insertCommasToNumber(String.valueOf(player.getAttributes().getItemToDrop().getAmount())));
			}
			player.getAttributes().setItemToDrop(null);
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 14176:
			player.getAttributes().setItemToDrop(null);
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 5294:
			player.getPacketSender().sendClientRightClickRemoval().sendInterfaceRemoval();
			if(!player.getAttributes().getBankPinAttributes().hasBankPin()) {
				player.getAttributes().setDialogueAction(5);
				DialogueManager.start(player, DialogueManager.getDialogues().get(13));
			} else {
				player.getAttributes().setDialogueAction(6);
				DialogueManager.start(player, DialogueManager.getDialogues().get(14));
			}
			break;
		case -15535:
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 15008:
			player.getPacketSender().sendMessage("Please select a difficulty.");
			break;
		case 15009:
			Achievements.initInterface(player, Achievements.TaskCity.Lumbridge_and_Draynor, Achievements.Difficulty.BEGINNER);
			break;
		case 15010:
			Achievements.initInterface(player, Achievements.TaskCity.Lumbridge_and_Draynor, Achievements.Difficulty.EASY);
			break;
		case 15011:
			Achievements.initInterface(player, Achievements.TaskCity.Lumbridge_and_Draynor, Achievements.Difficulty.MEDIUM);
			break;
		case 4550:
			if(player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getRiddleAnswer() >= 0)
				Barrows.handlePuzzle(player, 0);
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 4551:
			if(player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getRiddleAnswer() >= 0)
				Barrows.handlePuzzle(player, 1);
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 4552:
			if(player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getRiddleAnswer() >= 0)
				Barrows.handlePuzzle(player, 2);
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 4463: //Stop viewing orb
			FightPit.cancelOrbView(player);
			break;
		case 15240: //North west orb
			FightPit.viewOrbLocation(player, new Position(2384, 5157), 4);
			break;
		case 15241: //North east orb
			FightPit.viewOrbLocation(player, new Position(103, 5), 4);
			break;
		case 15239: //Centre orb
			FightPit.viewOrbLocation(player, new Position(55, 9), 4);
			break;
		case 15243: //South west orb
			FightPit.viewOrbLocation(player, new Position(300, 9), 70);
			break;
		case 22762:
			PouchMaking.open(player);
			break;
		case 23475:
			player.getPacketSender().sendInterface(22760);
			break;
		case 841:
			IngridientsBook.readBook(player, player.getSkillManager().getSkillAttributes().getHerbloreAttributes().getCurrentBookPage() + 2, true);
			break;
		case 839:
			IngridientsBook.readBook(player, player.getSkillManager().getSkillAttributes().getHerbloreAttributes().getCurrentBookPage() - 2, true);
			break;
		case 27654:
			if(player.getAttributes().getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			if(Dungeoneering.doingDungeoneering(player)) {
				player.getPacketSender().sendMessage("All items are kept here.");
				return;
			}
			ItemsKeptOnDeath.sendInterface(player);
			break;
		case 15210:
		case 10162:
		case 10007:
		case 17102:
		case -15387:
		case 2734:
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 1159: //Bones to Bananas
		case 15877://Bones to peaches
		case 30306:
			MagicSpells.handleMagicSpells(player, id);
			break;
			/**
			 * Pest control reward interface
			 */
			//EXP TAB
		case 18775:/*player.getPacketSender().sendInterface(18753);*/player.getPacketSender().sendMessage("This section is currently unavailable.");break;
		case 18743:player.getPacketSender().sendInterface(18746);break; // Equipment Void
		case 18748:player.getPacketSender().sendInterface(18730);break; // Experience
		case 18728:player.getPacketSender().sendInterfaceRemoval();break;
		case 18774:player.getPacketSender().sendInterface(18730);break; // Equipment
		case 18773:player.getPacketSender().sendMessage("This section is currently unavailable.");/*player.getPacketSender().sendInterface(18700);*/break; // EXP rewards tab
		case 18716:PestControl.buyFromShop(player, false, 0, 5250, 1);break;//Attack
		case 18720:PestControl.buyFromShop(player, false, 0, 5250, 10);break;
		case 18726:PestControl.buyFromShop(player, false, 0, 5250, 100);break;
		case 18706:PestControl.buyFromShop(player, false, 1, 5600, 1);break;//Defence
		case 18708:PestControl.buyFromShop(player, false, 1, 5600, 10);break;
		case 18710:PestControl.buyFromShop(player, false, 1, 5600, 100);break;
		case 18717:PestControl.buyFromShop(player, false, 2, 5600, 1);break;//Strength
		case 18723:PestControl.buyFromShop(player, false, 2, 5600, 10);break;
		case 18703:PestControl.buyFromShop(player, false, 2, 5600, 100);break;
		case 18712:PestControl.buyFromShop(player, false, 3, 5600, 1);break;//Constitution
		case 18722:PestControl.buyFromShop(player, false, 3, 5600, 10);break;
		case 18727:PestControl.buyFromShop(player, false, 3, 5600, 100);break;
		case 18718:PestControl.buyFromShop(player, false, 4, 5120, 1);break;//Ranged
		case 18724:PestControl.buyFromShop(player, false, 4, 5120, 10);break;
		case 18704:PestControl.buyFromShop(player, false, 4, 5120, 100);break;
		case 18719:PestControl.buyFromShop(player, false, 5, 1980, 1);break;//Prayer
		case 18725:PestControl.buyFromShop(player, false, 5, 1980, 10);break;
		case 18705:PestControl.buyFromShop(player, false, 5, 1980, 100);break;
		case 18714:PestControl.buyFromShop(player, false, 6, 4480, 1);break;//Magic
		case 18709:PestControl.buyFromShop(player, false, 6, 4480, 10);break;
		case 18711:PestControl.buyFromShop(player, false, 6, 4480, 100);break;
		//PC Equipment Tab
		case 18733:PestControl.buyFromShop(player, true, 11665, 1, 200); break;//melee helm
		case 18735:PestControl.buyFromShop(player, true, 11664, 1, 200); break;//ranger helm
		case 18741:PestControl.buyFromShop(player, true, 11663, 1, 200); break;//mage helm
		case 18734:PestControl.buyFromShop(player, true, 8839, 1, 250); break;//top
		case 18737:PestControl.buyFromShop(player, true, 8840, 1, 250); break;//robes
		case 18742:PestControl.buyFromShop(player, true, 8842, 1, 150); break;//gloves
		case 18740:PestControl.buyFromShop(player, true, 19711, 1, 350); break;//deflector
		//ENCHANCE
		case 18749:PestControl.buyFromShop(player, true, 19785, 1, 125); break;//elite top
		case 18750:PestControl.buyFromShop(player, true, 19786, 1, 125); break;//elite legs
		//COMSUMABLES
		case 18755:PestControl.buyFromShop(player, true, 6687, 1, 5); break;//Sara brew
		case 18756:PestControl.buyFromShop(player, true, 9475, 1, 6); break;//Mint cake
		case 18757:PestControl.buyFromShop(player, true, 12163, 1, 1); break;//Blue charm
		case 18758:PestControl.buyFromShop(player, true, 3016, 1, 2); break;//Super energy pot
		case 18759:PestControl.buyFromShop(player, true, 3040, 1, 2); break;//Magic pot
		case 18760:PestControl.buyFromShop(player, true, 397, 1, 1); break;//Sea turtle
		case 1019:
			SummoningTab.callFollower(player);
			break;
		case 1020:
			if(player.getAttributes().getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
				return;
			}
			SummoningTab.renewFamiliar(player);
			break;
		case 1021:
			if(player.getAttributes().getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			SummoningTab.handleDismiss(player, false);
			break;
		case 1018:
		case 2735:
			player.getAdvancedSkills().getSummoning().toInventory();
			break;
		case 27653: //open price checker
			PriceChecker.openPriceChecker(player);
			break;
		case 18247:
			PriceChecker.closePriceChecker(player);
			break;
		case 9999: // open report player interface
			ReportPlayer.init(player);
			break;
		case 10002:
			player.getPacketSender().sendString(1, "[UPDATEEREPORT 1]");
			break;
		case 10003:
			player.getPacketSender().sendString(1, "[UPDATEEREPORT 2]");
			break;
		case 3548:
			if(player.getTrading().inTrade()) {
				player.getTrading().declineTrade(true);
			} else {
				player.getTrading().declineTrade(false);
			}
			break;
		case 3546:
			if(player.getTrading().inTrade()) {
				player.getTrading().acceptTrade(2);
			} else {
				player.getTrading().declineTrade(false);
			}
			break;
		case 3420:
			if(player.getTrading().inTrade())
				player.getTrading().acceptTrade(1);
			break;
		case 3422:
		case 3442:
			if(player.getTrading().inTrade()) {
				player.getTrading().declineTrade(true);
			}
			break;
		case 31186:
			DialogueManager.start(player, 62);
			player.getAttributes().setDialogueAction(39);
			break;
		case 31236: //Summoning
			DialogueManager.start(player, 282);
			player.getAttributes().setDialogueAction(282);
			break;
		case 31161: //Prayer skill button
			DialogueManager.start(player, 38);
			player.getAttributes().setDialogueAction(212);
			break;
		case 31151: //Dungeoneering skill button
			DialogueManager.start(player, 330);
			player.getAttributes().setDialogueAction(314);
			//player.getPacketSender().sendMessage("I'll release it soon, just testing it.");
			break;
		case 31231:
			DialogueManager.start(player, 419);
			player.getAttributes().setDialogueAction(358);
			break;
		case 31146: //Construction skill button
			DialogueManager.start(player, 347);
			player.getAttributes().setDialogueAction(318);
			break;
		case 31166: //Herblore skill button
			DialogueManager.start(player, 39);
			player.getAttributes().setDialogueAction(24);
			break;
		case 31171: //Thieving skill button
			if(player.getSkillManager().getCurrentLevel(Skill.THIEVING) <= 1) {
				player.getPacketSender().sendInterfaceRemoval().sendMessage("You have a Thieving level of 1. You should pickpocket some men in the Edgeville").sendMessage("house for some experience.");
				return;
			}
			DialogueManager.start(player, 40);
			player.getAttributes().setDialogueAction(25);
			break;
		case 31111: //Attack skill button
		case 31116: //Strength skill button
		case 31121: //Defence skill button
			DialogueManager.start(player, 36);
			player.getAttributes().setDialogueAction(22);
			break;
		case 31126: //Ranged skill button
			DialogueManager.start(player, 37);
			player.getAttributes().setDialogueAction(23);
			break;
		case 31136: //Magic skill button
			DialogueManager.start(player, 35);
			player.getAttributes().setDialogueAction(21);
			break;
		case 31191: //Hunter skill button
			DialogueManager.start(player, 235);
			player.getAttributes().setDialogueAction(217);
			break;
		case 31156: // HP / Constitutition Skill
			DialogueManager.start(player, 84);
			player.getAttributes().setDialogueAction(57);
			break;
		case 31141: //Runecrafting skill button
			DialogueManager.start(player, 34);
			player.getAttributes().setDialogueAction(20);
			break;
		case 31131: //Prayer skill button
			DialogueManager.start(player, 33);
			player.getAttributes().setDialogueAction(19);
			break;
		case 31211:
			DialogueManager.start(player, 32);
			player.getAttributes().setDialogueAction(18);
			break;
		case -11513:
			SummoningTab.handleSpecialAttack(player);
			break;
		case -11510:
			SummoningTab.handleAttack(player);
			break;
		case 2703:
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case -11507:
			player.getAdvancedSkills().getSummoning().toInventory();
			break;
		case -11504:
			SummoningTab.renewFamiliar(player);
			break;
		case -11501:
			SummoningTab.callFollower(player);
			break;
		case -11498:
			SummoningTab.handleDismiss(player, false);
			break;
		case 29329:
			if(player.getAttributes().getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			player.getAttributes().setInputHandling(new EnterClanChatToJoin());
			player.getPacketSender().sendEnterInputPrompt("Enter the name of the player whose chat you wish to join:");
			break;
		case 29335:
			DialogueManager.start(player, 29);
			player.getAttributes().setDialogueAction(16);
			break;
		case 2422:
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 1036:
			if(player.getAttributes().isResting()) {
				player.getPacketSender().sendMessage("You are already resting!");
				return;
			}
			if(player.getAttributes().getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
				return;
			}
			if(CombatHandler.inCombat(player)) {
				player.getPacketSender().sendMessage("You cannot do this while in combat.");
				return;
			}
			player.getMovementQueue().stopMovement();
			player.performAnimation(new Animation(11786));
			player.getAttributes().setResting(true);
			Achievements.handleAchievement(player, Achievements.Tasks.TASK18);
			break;
		case 31181: //Fletching skill button
			DialogueManager.start(player, 230);
			player.getAttributes().setDialogueAction(215);
			break;
		case 31226: //Woodcutting
			DialogueManager.start(player, 47);
			player.getAttributes().setDialogueAction(29);
			break;
		case 31216: //Cooking skill button
			DialogueManager.start(player, 45);
			player.getAttributes().setDialogueAction(27);
			break;
		case 31221: //Firemaking skill button
			DialogueManager.start(player, 46);
			player.getAttributes().setDialogueAction(28);
			break;
		case 31206: //Smithing skill button
			DialogueManager.start(player, 44);
			player.getAttributes().setDialogueAction(15);
			break;
		case 31201: //Mining skill button
			DialogueManager.start(player, 43);
			player.getAttributes().setDialogueAction(14);
			break;
		case 31176: //Crafting skill button
			DialogueManager.start(player, 42);
			player.getAttributes().setDialogueAction(13);
			break;
		case 2471:
			player.getAttributes().setInterfaceId(50);
			if(player.getAttributes().getDialogueAction() == 4) {
				player.getPacketSender().sendInterfaceRemoval();
				ShopManager.getShops().get(3).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 10) {
				DialogueManager.start(player, 24);
				player.getAttributes().setDialogueAction(10);
			} else if(player.getAttributes().getDialogueAction() == 11) {
				ShopManager.getShops().get(8).open(player);
			} else if(player.getAttributes().getDialogueAction() == 12) {
				Tanning.selectionInterface(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 15) {
				player.getPacketSender().sendInterfaceRemoval();
				TeleportHandler.teleportPlayer(player, new Position(3225, 3254, 0), player.getAttributes().getSpellbook().getTeleportType());
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 20) {
				//OPEN WIKI HERE RC
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 25) { //Thieving Guide
				//Open guide for Thieving here
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 26) { //Ardougne market
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				TeleportHandler.teleportPlayer(player, new Position(2662, 3305, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 29) {
				DialogueManager.start(player, 52);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 40) { //Slayer: Select Master: Vannaka
				SlayerMaster.changeSlayerMaster(player, SlayerMaster.VANNAKA);
			} else if(player.getAttributes().getDialogueAction() == 44) { //Slayer: Reset a task
				player.getAttributes().setDialogueAction(44);
				DialogueManager.start(player, ChampionsGuildDialogues.skillcapeOfAttack(player));
			} else if(player.getAttributes().getDialogueAction() == 48) {
				ArcheryCompetition.exhchangeTickets(player, 50);
			} else if(player.getAttributes().getDialogueAction() == 50) { //Brother jered
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
				ShopManager.getShops().get(7).open(player);
			} else if(player.getAttributes().getDialogueAction() == 53) { //Robe Store Owner Magic Guild Magic shop
				DialogueManager.start(player, 28);
				player.getAttributes().setDialogueAction(11);
			} else if(player.getAttributes().getDialogueAction() == 58) { //Consumable shop
				player.getAttributes().setDialogueAction(-1);
				ShopManager.getShops().get(9).open(player);
			} else if(player.getAttributes().getDialogueAction() == 100) {
				Artifacts.sellArtifacts(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 202) { //Herblore shop
				player.getAttributes().setDialogueAction(-1);
				ShopManager.getShops().get(13).open(player);
			} else if(player.getAttributes().getDialogueAction() == 201) {
				ShopManager.getShops().get(12).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 208) {
				DialogueManager.start(player, 327);
				player.getAttributes().setDialogueAction(311);
			} else if(player.getAttributes().getDialogueAction() == 210) { // Recipe for disaster quest location
				TeleportHandler.teleportPlayer(player, new Position(3207, 3214, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 211) { // Nomad quest location
				TeleportHandler.teleportPlayer(player, new Position(1891, 3177, 0), player.getAttributes().getSpellbook().getTeleportType());
			}  else if(player.getAttributes().getDialogueAction() == 213) {
				ShopManager.getShops().get(15).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 225) {
				player.moveTo(new Position(3205, 3228, 2));
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 226) {
				player.moveTo(new Position(3205, 3209, 2));
				player.getPacketSender().sendInterfaceRemoval();
				Achievements.handleAchievement(player, Tasks.TASK16);
			} else if(player.getAttributes().getDialogueAction() == 285) {
				PrayerHandler.switchPrayerbook(player, false);
				DialogueManager.start(player, 289);
			} else if(player.getAttributes().getDialogueAction() == 305) { //Fishing shop
				ShopManager.getShops().get(28).open(player);
			} else if(player.getAttributes().getDialogueAction() == 311) { //Recipe for disaster 
				DialogueManager.start(player, 118);
				player.getAttributes().setDialogueAction(210);
			} else if(player.getAttributes().getDialogueAction() == 312) {
				SoulWars.RewardShop.openShop(player);
			} else if(player.getAttributes().getDialogueAction() == 315) {
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(315);
				Dungeoneering.start(player);
			} else if(player.getAttributes().getDialogueAction() == 329) {
				player.getPacketSender().sendInterfaceRemoval();
				ShopManager.getShops().get(49).open(player);
				player.getPacketSender().sendMessage("");
				player.getPacketSender().sendMessage("You currently have "+player.getPointsHandler().getDonatorPoints()+" Donator points.");
				player.getPacketSender().sendMessage("@red@You can purchase Donator points and ranks from the Trident store.").sendMessage("You can access the store by doing ::donate.");
			} else if(player.getAttributes().getDialogueAction() == 330) { //clan chat item sharing toggle
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getAttributes().getClanChat() == null)
					return;
				ClanChatManager.toggleLootShare(player, ClanChatDropShare.ITEM_SHARE, !player.getAttributes().getClanChat().isItemSharing());
			} else if(player.getAttributes().getDialogueAction() == 336) {
				DialogueManager.start(player, 388);
			} else if(player.getAttributes().getDialogueAction() == 337) {
				player.getPacketSender().sendInterface(50100);
			} else if(player.getAttributes().getDialogueAction() == 346) {
				DialogueManager.start(player, 403);
			} else if(player.getAttributes().getDialogueAction() == 350) {
				player.getPacketSender().sendInterfaceRemoval();
				Fishing.setupFishing(player, Fishing.Spot.MONK_FISH);
			} else if(player.getAttributes().getDialogueAction() == 352) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getDifficulty() == Difficulty.EASY) {
					player.getPacketSender().sendMessage("Only players which are playing the 'Normal' or 'Hard' difficulty mode can buy this cape.");
					return;
				}
				boolean maxed = true;
				for(Skill skill : Skill.values()) {
					if(skill == Skill.CONSTRUCTION)
						continue;
					if(player.getSkillManager().getMaxLevel(skill) < SkillManager.getMaxAchievingLevel(skill)) {
						maxed = false;
						break;
					}
				}
				if(maxed) {
					if(player.getInventory().getFreeSlots() == 0) {
						player.getPacketSender().sendMessage("You do not have enough free inventory space");
						return;
					}
					if(player.getInventory().getAmount(995) >= 200000) {
						player.getInventory().delete(995, 200000).add(20747, 1);
						player.getPacketSender().sendMessage("You purchase a Max cape for 200,000 coins.");
					} else
						player.getPacketSender().sendMessage("You need at least 200,000 coins in your inventory to buy that item.");
				} else
					DialogueManager.start(player, 410);
			} else if(player.getAttributes().getDialogueAction() == 353) {
				TeleportHandler.teleportPlayer(player, new Position(2871, 5318, 2), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 357) {
				ShopManager.getShops().get(56).open(player);
			} else if(player.getAttributes().getDialogueAction() == 402) {
				ShopManager.getShops().get(59).open(player);
			} else if(player.getAttributes().getDialogueAction() == 405) {
				DialogueManager.start(player, MoreLoyaltyRewardDialogues.buyItem(player, 11137, 4000));
			}
			break;
		case 2482:
			player.getAttributes().setInterfaceId(50);
			if(player.getAttributes().getDialogueAction() == 9) {
				DialogueManager.start(player, 391);
				player.getAttributes().setDialogueAction(340);
			} else if(player.getAttributes().getDialogueAction() == 13) {
				//OPEN CRAFT GUIDE HERE
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 16) {
				ClanChatManager.createClan(player);
			} else if(player.getAttributes().getDialogueAction() == 41) { //Slayer: Assign a task
				player.getAdvancedSkills().getSlayer().assignTask();
			} else if(player.getAttributes().getDialogueAction() == 42) { //Slayer: Reset a task
				DialogueManager.start(player, SlayerDialogues.findAssignment(player));
			} else if(player.getAttributes().getDialogueAction() == 55) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				ShopManager.getShops().get(8).open(player);
			} else if(player.getAttributes().getDialogueAction() == 278) { //Boss teleports 1: King Black Dragon
				TeleportHandler.teleportPlayer(player, new Position(2273, 4680), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 205) {
				TeleportHandler.teleportPlayer(player, new Position(2707, 3711), player.getAttributes().getSpellbook().getTeleportType());
				if(player.getAttributes().getNewPlayerDelay() > 0)
					player.getPacketSender().sendMessage("Rock crabs have a chance of dropping Oysters with valuables inside!");
			} else if(player.getAttributes().getDialogueAction() == 289) { //Magicbook switching, modern magic
				//if(!true)
				//	DialogueManager.start(player, 302);
				//else
				//TODO: lunar spell book change spell
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setSpellbook(MagicSpellbook.NORMAL);
				player.getPacketSender().sendTabInterface(Constants.MAGIC_TAB, player.getAttributes().getSpellbook().getInterfaceId());
				Autocasting.resetAutocast(player, true);
			} else if(player.getAttributes().getDialogueAction() == 303) { //Summoning, open shop options
				DialogueManager.start(player, 305);
				player.getAttributes().setDialogueAction(304);
			} else if(player.getAttributes().getDialogueAction() == 304) { //Summoning, open shop option 1
				ShopManager.getShops().get(25).open(player);
			} else if(player.getAttributes().getDialogueAction() == 320) { //Construction skillcape
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
				if(player.getSkillManager().getMaxLevel(Skill.CONSTRUCTION) >= 99) { 
					Skillcapes.buySkillcape(player, Skill.CONSTRUCTION, 9789, 9790, 9791, true, 4247);
				} else
					DialogueManager.start(player, DialogueManager.getDialogues().get(350));
			} else if(player.getAttributes().getDialogueAction() == 340) {
				player.getAttributes().setDialogueAction(-1);
				ShopManager.getShops().get(5).open(player);
			} else if(player.getAttributes().getDialogueAction() == 341) {
				DialogueManager.start(player, 394);
			} else if(player.getAttributes().getDialogueAction() == 348) {
				if(player.getRights() == PlayerRights.PLAYER) {
					player.getAttributes().setDialogueAction(-1);
					DialogueManager.start(player, 382);
					return;
				}
				TeleportHandler.teleportPlayer(player, new Position(3420, 2916), TeleportType.NORMAL);
			} else if(player.getAttributes().getDialogueAction() == 356) {
				TeleportHandler.teleportPlayer(player, new Position(1891, 3177), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 405) {
				DialogueManager.start(player, MoreLoyaltyRewardDialogues.buyItem(player, 12158, 100));
			} 
			break;
		case 2472:
			player.getAttributes().setInterfaceId(50);
			if(player.getAttributes().getDialogueAction() == 4) {
				player.getPacketSender().sendInterfaceRemoval();
				ShopManager.getShops().get(4).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 10) {
				player.getPacketSender().sendInterfaceRemoval();
				ShopManager.getShops().get(20).open(player);
			} else if(player.getAttributes().getDialogueAction() == 11) {
				ShopManager.getShops().get(1).open(player);
			} else if(player.getAttributes().getDialogueAction() == 15) {
				player.getPacketSender().sendInterfaceRemoval();
				TeleportHandler.teleportPlayer(player, new Position(3228, 3254, 0), player.getAttributes().getSpellbook().getTeleportType());
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 20) {
				TeleportHandler.teleportPlayer(player, new Position(3253, 3401), TeleportType.NORMAL);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 26) { //Thieving markets: Draynor
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				TeleportHandler.teleportPlayer(player, new Position(3081, 3251, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 29) { // Ranged shop Ranging Guild
				player.getAttributes().setDialogueAction(4);
				Dialogue dialogue = DialogueManager.getDialogues().get(9);
				DialogueManager.start(player, dialogue);
			} else if(player.getAttributes().getDialogueAction() == 40) { //Slayer: Select Master: Duradel
				SlayerMaster.changeSlayerMaster(player, SlayerMaster.DURADEL);
			} else if(player.getAttributes().getDialogueAction() == 44) {
				player.getPacketSender().sendInterfaceRemoval();
				DialogueManager.start(player, ChampionsGuildDialogues.skillcapeOfStrength(player));
			} else if(player.getAttributes().getDialogueAction() == 48) {
				ArcheryCompetition.exhchangeTickets(player, 100);
			} else if(player.getAttributes().getDialogueAction() == 50) { //Prayer skillcape
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
				if(player.getSkillManager().getMaxLevel(Skill.PRAYER) >= 990) { 
					Dialogue dialogue = DialogueManager.getDialogues().get(71);
					DialogueManager.start(player, dialogue);
					player.getAttributes().setDialogueAction(51);
				} else
					DialogueManager.start(player, DialogueManager.getDialogues().get(69));
			} else if(player.getAttributes().getDialogueAction() == 53) { //Magic skillcape
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
				if(player.getSkillManager().getMaxLevel(Skill.MAGIC) >= 99) { 
					DialogueManager.start(player, DialogueManager.getDialogues().get(77));
					player.getAttributes().setDialogueAction(54);
				} else
					DialogueManager.start(player, DialogueManager.getDialogues().get(75));

			} else if(player.getAttributes().getDialogueAction() == 58) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getMaxLevel(Skill.CONSTITUTION) >= 990) {
					DialogueManager.start(player, 87);
					player.getAttributes().setDialogueAction(58);
				} else {
					DialogueManager.start(player, 85);
					player.getAttributes().setDialogueAction(-1);
				}
			} else if(player.getAttributes().getDialogueAction() == 100) {
				ShopManager.getShops().get(29).open(player);
			} else if(player.getAttributes().getDialogueAction() == 202) {
				DialogueManager.start(player, 103);
			} else if(player.getAttributes().getDialogueAction() == 201) {
				Agility.ticketExchange(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 208) {
				DialogueManager.start(player, 203);
				player.getAttributes().setDialogueAction(208);
			} else if(player.getAttributes().getDialogueAction() == 210) {
				RecipeForDisaster.openQuestLog(player);
			} else if(player.getAttributes().getDialogueAction() == 211) {
				Nomad.openQuestLog(player);
			} else if(player.getAttributes().getDialogueAction() == 213) {
				Tanning.selectionInterface(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 217) {
				TeleportHandler.teleportPlayer(player, new Position(2589, 4319, 0), TeleportType.PURO_PURO);
			} else if(player.getAttributes().getDialogueAction() == 225) {
				player.moveTo(new Position(3205, 3228, 0));
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 226) {
				player.moveTo(new Position(3205, 3209, 0));
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 285) {
				if(player.getSkillManager().getMaxLevel(Skill.PRAYER) >= 50 && player.getSkillManager().getMaxLevel(Skill.DEFENCE) >= 30) {
					PrayerHandler.switchPrayerbook(player, false);
					DialogueManager.start(player, 289);
				} else
					DialogueManager.start(player, 290);
			} else if(player.getAttributes().getDialogueAction() == 292) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				player.moveTo(new Position(3104, 3161, 0));
			} else if(player.getAttributes().getDialogueAction() == 305) { //Fishing skillcape
				if(player.getSkillManager().getMaxLevel(Skill.FISHING) >= 99)
					Skillcapes.buySkillcape(player, Skill.FISHING, 9798, 9799, 9800, true, 308);
				else
					DialogueManager.start(player, 314);
				player.getAttributes().setDialogueAction(305);
			} else if(player.getAttributes().getDialogueAction() == 306) { //Open Webshop
				player.getPacketSender().sendInterfaceRemoval();
				player.getPacketSender().sendString(1, "www.vanguardps.com/donate/");
				player.getPacketSender().sendMessage("Attempted to open vanguardps.com/donate");
			} else if(player.getAttributes().getDialogueAction() == 311) { //Minigame teleport 1: Nomads Requiem
				player.getAttributes().setDialogueAction(211);
				DialogueManager.start(player, 118);
			} else if(player.getAttributes().getDialogueAction() == 312) {
				/*if(!player.getAttributes().getMinigameAttributes().getNomadAttributes().hasFinishedPart(2))
					DialogueManager.start(player, 325);
				else
					ShopManager.getShops().get(32).open(player);*/
				player.getPacketSender().sendMessage("Currently Disabled, issues being resolved. Sorry for the inconvenience");
			} else if(player.getAttributes().getDialogueAction() == 330) { //clan chat coin sharing toggle
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getAttributes().getClanChat() == null)
					return;
				ClanChatManager.toggleLootShare(player, ClanChatDropShare.COIN_SHARE, !player.getAttributes().getClanChat().isCoinSharing());
			} else if(player.getAttributes().getDialogueAction() == 329) {
				player.getPacketSender().sendInterfaceRemoval();
				ShopManager.getShops().get(55).open(player);
				player.getPacketSender().sendMessage("");
				player.getPacketSender().sendMessage("You currently have "+player.getPointsHandler().getDonatorPoints()+" Donator points.");
				player.getPacketSender().sendMessage("@red@You can purchase Donator points and ranks from the Trident store.").sendMessage("You can access the store by doing ::donate.");
			} else if(player.getAttributes().getDialogueAction() == 336) { //Thieving skillcape
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getMaxLevel(Skill.THIEVING) >= 99)
					Skillcapes.buySkillcape(player, Skill.THIEVING, 9777, 9778, 9779, true, 2270);
			} else if(player.getAttributes().getDialogueAction() == 337) {
				player.getPacketSender().sendInterfaceRemoval();
				player.moveTo(new Position(2815, 5511));
				player.getPacketSender().sendMessage("You can fight other players here. You will not lose items on death.");
			} else if(player.getAttributes().getDialogueAction() == 346) {
				ShopManager.getShops().get(54).open(player);
			} else if(player.getAttributes().getDialogueAction() == 350) {
				player.getPacketSender().sendInterfaceRemoval();
				Fishing.setupFishing(player, Fishing.Spot.ROCKTAIL);
			} else if(player.getAttributes().getDialogueAction() == 352) {
				DialogueManager.start(player, 409);
			} else if(player.getAttributes().getDialogueAction() == 353) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getCurrentLevel(Skill.SLAYER) < 80) {
					player.getPacketSender().sendMessage("You need a Slayer level of at least 80 to visit this dungeon.");
					return;
				}
				TeleportHandler.teleportPlayer(player, new Position(1746, 5324), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 357) {
				if(player.getSkillManager().getMaxLevel(Skill.FARMING) >= 99) { 
					Skillcapes.buySkillcape(player, Skill.FARMING, 9810, 9811, 9812, true, 3299);
				} else
					DialogueManager.start(player, DialogueManager.getDialogues().get(418));
				player.getAttributes().setDialogueAction(357);
			} else if(player.getAttributes().getDialogueAction() == 315) {
				ShopManager.getShops().get(57).open(player);
				player.getPacketSender().sendMessage("@red@You currently have "+player.getPointsHandler().getDungeoneeringTokens()+" Dungeoneering tokens.");
			} else if(player.getAttributes().getDialogueAction() == 402) {
				DialogueManager.start(player, 437);
			} else if(player.getAttributes().getDialogueAction() == 405) {
				DialogueManager.start(player, 441);
			}
			break;
		case 2473:
			player.getAttributes().setInterfaceId(50);
			if(player.getAttributes().getDialogueAction() == 9 || player.getAttributes().getDialogueAction() == 4 || player.getAttributes().getDialogueAction() == 329 || player.getAttributes().getDialogueAction() == 337 || player.getAttributes().getDialogueAction() == 346 || player.getAttributes().getDialogueAction() == 350) {
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 10) {
				if(player.getSkillManager().getMaxLevel(Skill.WOODCUTTING) >= 99)
					Skillcapes.buySkillcape(player, Skill.WOODCUTTING, 9807, 9808, 9809, true, 4906);
				else
					DialogueManager.start(player, 245);
				player.getAttributes().setDialogueAction(-1);
			}  else if(player.getAttributes().getDialogueAction() == 11) {
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
			}  else if(player.getAttributes().getDialogueAction() == 12) {
				if(player.getSkillManager().getMaxLevel(Skill.CRAFTING) >= 99) {
					DialogueManager.start(player, 33);
					player.getAttributes().setDialogueAction(12);
				} else {
					DialogueManager.start(player, 32);
					player.getAttributes().setDialogueAction(-1);
				}
			} else if(player.getAttributes().getDialogueAction() == 20) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) < 40) {
					player.getPacketSender().sendMessage("You need a Runecrafting level of at least 40 to go to DesoSpan.");
					return;
				}
				TeleportHandler.teleportPlayer(player, new Position(2597, 4773), TeleportType.NORMAL);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 26) { //Thieving markets: Fremennik
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				TeleportHandler.teleportPlayer(player, new Position(2643, 3677, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 29) {
				if(player.getSkillManager().getMaxLevel(Skill.RANGED) >= 99) {
					DialogueManager.start(player, 50);
					player.getAttributes().setDialogueAction(30);
				} else {
					DialogueManager.start(player, 66);
					player.getAttributes().setDialogueAction(-1);
				}
			} else if(player.getAttributes().getDialogueAction() == 40) { //Slayer: Select Master: Kuradel
				SlayerMaster.changeSlayerMaster(player, SlayerMaster.KURADEL);
			} else if(player.getAttributes().getDialogueAction() == 44) {
				DialogueManager.start(player, ChampionsGuildDialogues.skillcapeOfDefence(player));
			} else if(player.getAttributes().getDialogueAction() == 48) {
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendEnterAmountPrompt("Enter amount:");
				player.getAttributes().setInputHandling(new EnterAmountOfTicketsToExchange());
			} else if(player.getAttributes().getDialogueAction() == 50) { //Prayerbook switching
				DialogueManager.start(player, 288);
				player.getAttributes().setDialogueAction(285);
			} else if(player.getAttributes().getDialogueAction() == 53) { //Magicbook switching
				player.getAttributes().setDialogueAction(289);
				DialogueManager.start(player, 300);
			} else if(player.getAttributes().getDialogueAction() == 58) {
				player.getPacketSender().sendInterfaceRemoval();
				boolean restored = false;
				for(Skill skill : Skill.values()) {
					if(player.getSkillManager().getCurrentLevel(skill) < player.getSkillManager().getMaxLevel(skill)) {
						player.getSkillManager().setCurrentLevel(skill, player.getSkillManager().getMaxLevel(skill));
						restored = true;
					}
				}
				if(restored) {
					DialogueManager.start(player, 384);
					player.performGraphic(new Graphic(1302));
					player.getPacketSender().sendMessage("Your stats have been restored.");
				} else
					player.getPacketSender().sendMessage("Your stats do not need to be restored at the moment.");
			} else if(player.getAttributes().getDialogueAction() == 202) {
				if(player.getSkillManager().getMaxLevel(Skill.HERBLORE) >= 99) {
					Skillcapes.buySkillcape(player, Skill.HERBLORE, 9774, 9775, 9776, true, 8459);
				} else {
					DialogueManager.start(player, 106);
					player.getAttributes().setDialogueAction(-1);
				}
			} else if(player.getAttributes().getDialogueAction() == 100) {
				ShopManager.getShops().get(31).open(player);
			} else if(player.getAttributes().getDialogueAction() == 201) {
				if(player.getSkillManager().getMaxLevel(Skill.AGILITY) < 99) { 
					Dialogue dialogue = DialogueManager.getDialogues().get(100);
					DialogueManager.start(player, dialogue);
				} else
					Skillcapes.buySkillcape(player, Skill.AGILITY, 9771, 9772, 9773, true, 437);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 208) { //Minigame cancel option
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 210 || player.getAttributes().getDialogueAction() == 211) {
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 213) {
				if(player.getSkillManager().getMaxLevel(Skill.CRAFTING) < 99) { 
					Dialogue dialogue = DialogueManager.getDialogues().get(234);
					DialogueManager.start(player, dialogue);
				} else
					Skillcapes.buySkillcape(player, Skill.CRAFTING, 9780, 9781, 9782, true, 805);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 217) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getCurrentLevel(Skill.HUNTER) < 23) {
					player.getPacketSender().sendMessage("You need a Hunter level of at least 23 to visit the hunting area.");
					return;
				}
				TeleportHandler.teleportPlayer(player, new Position(2916, 2903, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 225 || player.getAttributes().getDialogueAction() == 226 || player.getAttributes().getDialogueAction() == 285) {
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 292) {
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 305) { //Fishing last option, close
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 306 || player.getAttributes().getDialogueAction() == 311 || player.getAttributes().getDialogueAction() == 330 || player.getAttributes().getDialogueAction() == 336) { //Sir Prysin
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 352) {
				DialogueManager.start(player, 411);
			} else if(player.getAttributes().getDialogueAction() == 353) {
				player.getAttributes().setDialogueAction(206);
				DialogueManager.start(player, 201);
			} else if(player.getAttributes().getDialogueAction() == 357 || player.getAttributes().getDialogueAction() == 402 || player.getAttributes().getDialogueAction() == 405) {
				player.getPacketSender().sendInterfaceRemoval();
			}
			break;
		case 2461:
			player.getAttributes().setInterfaceId(50);
			if(Effigies.handleEffigyAction(player, id))
				return;
			if(player.getAttributes().getDialogueAction() == 0) {
				player.getBank(player.getAttributes().getCurrentBankTab()).open();
			} else if(player.getAttributes().getDialogueAction() == 1) {
				StartingHandler.startTutorial(player);
			} else if(player.getAttributes().getDialogueAction() == 3) {
				DialogueManager.start(player, 8);
				player.getAttributes().setDialogueAction(4);
			} else if(player.getAttributes().getDialogueAction() == 4) {
				ShopManager.getShops().get(1).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 5) {
				player.getPacketSender().sendInterfaceRemoval();
				if (!player.getAttributes().getBankPinAttributes().hasBankPin())
					BankPin.init(player);
				else 
					player.getPacketSender().sendMessage("You already have a bank PIN.");
			} else if(player.getAttributes().getDialogueAction() == 6) {
				player.getPacketSender().sendInterfaceRemoval();
				if(!player.getAttributes().getBankPinAttributes().hasBankPin())
					return;
				if (player.getAttributes().getBankPinAttributes().hasEnteredBankPin())
					BankPin.deletePin(player);
				else {
					player.getPacketSender().sendMessage("You must have entered your bank PIN to do this.");
					player.getPacketSender().sendMessage("Please visit the nearest bank to enter it.");
					player.getPacketSender().sendInterfaceRemoval();
				}
			} else if(player.getAttributes().getDialogueAction() == 11) {
				DialogueManager.start(player, 27);
			} else if(player.getAttributes().getDialogueAction() == 12) {
				if(player.getInventory().getFreeSlots() < 2) {
					DialogueManager.start(player, 35);
					player.getAttributes().setDialogueAction(-1);
					return;
				} else if(player.getInventory().getAmount(995) < 99000) {
					DialogueManager.start(player, 36);
					player.getAttributes().setDialogueAction(-1);
					return;
				}
				player.getInventory().delete(995, 99000, true);
				player.getInventory().add(9780, 1);
				player.getInventory().add(9782, 1);
				DialogueManager.start(player, 37);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 14) {
				//OPEN MINING GUIDE HERE
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 15) {
				//OPEN SMITHING GUIDE HERE
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 17) {
				SummoningTab.handleDismiss(player, true);
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 19) {
				//OPEN WIKI HERE PRAYER
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 21) {
				//OPEN WIKI HERE MAGIC
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 22) {
				//OPEN WIKI HERE COMBAT
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 23) {
				//OPEN WIKI HERE RANGED
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 27) {
				//OPEN WIKI HERE COOKING
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 28) {
				//OPEN WIKI HERE FIREMAKING
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 29) {
				//OPEN WIKI HERE WOODCUTTING
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 30) {
				if(player.getSkillManager().getMaxLevel(Skill.RANGED) >= 99) {
					player.getAttributes().setDialogueAction(-1);
					player.getPacketSender().sendInterfaceRemoval();
					if(player.getInventory().getFreeSlots() < 2) {
						player.getPacketSender().sendMessage("You do not have enough free inventory space.");
						return;
					}
					if(player.getInventory().getAmount(995) >= 99000) {
						player.getInventory().delete(995, 99000);
						player.getInventory().add(9758, 1);
						if(player.getSkillManager().hasAnother99(4)) {
							player.getInventory().add(9757, 1);
						} else {
							player.getInventory().add(9756, 1);
						}
					} else {
						player.getPacketSender().sendMessage("You do not have enough coins.");
					}
				} else {
					player.getAttributes().setDialogueAction(-1);
					player.getPacketSender().sendInterfaceRemoval();
				}
			} else if(player.getAttributes().getDialogueAction() == 31) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getMaxLevel(Skill.RANGED) < 50) {
					player.getPacketSender().sendMessage("You need a Ranged level of at least 50 to participate in this minigame.");
					return;
				}
				if(player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().isParticipating()) {
					player.getPacketSender().sendMessage("You're already participating! Go shoot at a target!");
					return;
				}
				boolean usePouch = player.getAttributes().getMoneyInPouch() >= 200;
				if(usePouch || player.getInventory().getAmount(995) >= 200) {
					if(usePouch) {
						player.getAttributes().setMoneyInPouch(player.getAttributes().getMoneyInPouch() - 200);
						player.getPacketSender().sendString(8135, ""+player.getAttributes().getMoneyInPouch());
					} else
						player.getInventory().delete(995, 200);
					player.getAttributes().getMinigameAttributes().getArcheryCompetitionAttributes().setParticipating(true);
					player.getPacketSender().sendMessage("You're now participating. Go shoot at a target!");
				} else {
					player.getPacketSender().sendMessage("You do not have enough coins.");
				}
			} else if(player.getAttributes().getDialogueAction() == 43) { //Slayer: Reseting a task
				player.getAdvancedSkills().getSlayer().resetSlayerTask(false);
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 45) { //Attack skillcape
				Skillcapes.buySkillcape(player, Skill.ATTACK, 9747, 9748, 9749, true, 4288);
			} else if(player.getAttributes().getDialogueAction() == 46) { //Strength skillcape
				Skillcapes.buySkillcape(player, Skill.STRENGTH, 9750, 9751, 9752, true, 4288);
			} else if(player.getAttributes().getDialogueAction() == 47) { //Defence skillcape
				Skillcapes.buySkillcape(player, Skill.DEFENCE, 9753, 9754, 9755, true, 4288);
			} else if(player.getAttributes().getDialogueAction() == 51) { //Defence skillcape
				Skillcapes.buySkillcape(player, Skill.PRAYER, 9759, 9760, 9761, true, 802);
			} else if(player.getAttributes().getDialogueAction() == 54) { //Magic skillcape
				Skillcapes.buySkillcape(player, Skill.MAGIC, 9762, 9763, 9764, true, 1658);
			} else if(player.getAttributes().getDialogueAction() == 56) { //Runecrafting skillcape
				Skillcapes.buySkillcape(player, Skill.RUNECRAFTING, 9765, 9766, 9767, true, 13632);
			} else if(player.getAttributes().getDialogueAction() == 57) {
				//OPEN WIKI HERE CONSTITUTION
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 58) {
				Skillcapes.buySkillcape(player, Skill.CONSTITUTION, 9768, 9769, 9770, true, 961);
			} else if(player.getAttributes().getDialogueAction() == 200) {
				ShopManager.getShops().get(11).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 203) {
				ShopManager.getShops().get(14).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 55) {
				TeleportHandler.teleportPlayer(player, new Position(2910, 4831), TeleportType.NORMAL);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 211) {
				if(player.getAttributes().getCurrentInteractingObject() != null)
					Mining.startMining(player, new GameObject(24444, player.getAttributes().getCurrentInteractingObject().getPosition()));
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 216) {
				if(player.getSkillManager().getMaxLevel(Skill.FLETCHING) >= 99) {
					Skillcapes.buySkillcape(player, Skill.FLETCHING, 9783, 9784, 9785, true, 575);
				} else 
					DialogueManager.start(player, 233);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 218) { //Hunting shop
				ShopManager.getShops().get(17).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 219) { //Hunting shop
				ShopManager.getShops().get(18).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 220) { //Hunting shop
				ShopManager.getShops().get(19).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 221) { //Hunting shop
				FightCave.enterCave(player, false);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 222) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount() < 5) {
					player.getPacketSender().sendMessage("You must have a killcount of at least 5 to enter the tunnel.");
					return;
				}
				player.moveTo(Barrows.UNDERGROUND_SPAWNS[Misc.getRandom(Barrows.UNDERGROUND_SPAWNS.length - 1)]);
			} else if(player.getAttributes().getDialogueAction() == 224) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				DialogueManager.start(player, 271);
				player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().setPartFinished(0, true);
			} else if(player.getAttributes().getDialogueAction() == 279) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				if (player.getSkillManager().getMaxLevel(Skill.SMITHING) >= 92) {
					if (player.getInventory().contains(14472) && player.getInventory().contains(14474) && player.getInventory().contains(14476) && player.getInventory().contains(14478)) { 
						player.getInventory().delete(14472, 1);
						player.getInventory().delete(14474, 1);
						player.getInventory().delete(14476, 1);

						player.getInventory().add(14479, 1);
					} else
						player.getPacketSender().sendMessage("You don't have the required materials to smith a Dragon Platebody.");
				} else
					player.getPacketSender().sendMessage("You need a Smithing level of at least 92 to create a Dragon Platebody.");
			} else if(player.getAttributes().getDialogueAction() == 281) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getTrading().getItemLending().getBorrowedItem() != null) {
					ItemLending.lendedItems.remove(player.getTrading().getItemLending().getBorrowedItem());
					ItemLending.returnBorrowedItem(player.getTrading().getItemLending().getBorrowedItem());
				}
			} else if(player.getAttributes().getDialogueAction() == 282) {
				//OPEN WIKI HERE SUMMONING
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 283) {
				ShopManager.getShops().get(50).open(player);
			} else if(player.getAttributes().getDialogueAction() == 286) {
				Barrows.fixBarrows(player);
			} else if(player.getAttributes().getDialogueAction() == 301) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getAdvancedSkills().getSlayer().getDuoSlayer().invitationOwner != null) {
					Player inviteOwner = PlayerHandler.getPlayerForName(player.getAdvancedSkills().getSlayer().getDuoSlayer().invitationOwner);
					if(inviteOwner != null) {
						if(player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString() != null) {
							player.getPacketSender().sendMessage("You already have a Slayer duo partner.");
							inviteOwner.getPacketSender().sendMessage(""+player.getUsername()+" already has a Slayer duo partner.");
							return;
						}
						inviteOwner.getPacketSender().sendMessage(""+player.getUsername()+" has joined your duo Slayer team.");
						inviteOwner.getPacketSender().sendMessage("Seek respective Slayer master for a task.");
						inviteOwner.getAdvancedSkills().getSlayer().getDuoSlayer().setDuoPartner(player.getUsername());
						inviteOwner.getAdvancedSkills().getSlayer().doingDuoSlayer = true;
						player.getPacketSender().sendMessage("You have joined "+inviteOwner.getUsername()+"'s duo Slayer team.");
						player.getAdvancedSkills().getSlayer().getDuoSlayer().setDuoPartner(inviteOwner.getUsername());
						player.getAdvancedSkills().getSlayer().doingDuoSlayer = true;
					} else
						player.getPacketSender().sendMessage("Failed to accept invitation.");
					player.getAdvancedSkills().getSlayer().getDuoSlayer().invitationOwner = null;
				}
			} else if(player.getAttributes().getDialogueAction() == 308) {
				/*DialogueManager.start(player, 321);
				player.getAttributes().getMinigameAttributes().getNomadAttributes().setPartFinished(0, true);
				player.getAttributes().setDialogueAction(309);*/
				player.getPacketSender().sendMessage("Currently Disabled, issues being resolved. Sorry for the inconvenience");
			} else if(player.getAttributes().getDialogueAction() == 309) {
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 317) {
				Dungeoneering.leave(player);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 320) { //Buying a house
				if(player.getInventory().getAmount(995) < 50000) {
					DialogueManager.start(player, 355);
					return;
				}
				player.getInventory().delete(995, 55000);
				player.getAttributes().setDialogueAction(654);
				//Construction.handleButtonClick(2461, player);
			} else if(player.getAttributes().getDialogueAction() == 332) { //Tzhaar tokkul shop
				player.getAttributes().setDialogueAction(-1);
				ShopManager.getShops().get(36).open(player);
			} else if(player.getAttributes().getDialogueAction() == 390) { //Skip tutorial
				player.getAttributes().getTutorialFinished()[5] = true;
				StartingHandler.finishTutorial(player, true);
			} else if(player.getAttributes().getDialogueAction() == 360) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getPartyInvitation() != null) {
					if(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor() == null || player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty() == null) {
						player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getPartyInvitation().add(player);
					}
				}
				player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setPartyInvitation(null);
			} else if(player.getAttributes().getDialogueAction() == 361) {
				Skillcapes.buySkillcape(player, Skill.SLAYER, 9786, 9787, 9788, true, 9085);
			} else if(player.getAttributes().getDialogueAction() == 402) {
				DialogueManager.start(player, 432);
				player.getAttributes().setDialogueAction(403);
			} else if(player.getAttributes().getDialogueAction() == 406) {
				if(player.getAttributes().getInterfaceId() == 50) {
					player.getInventory().resetItems().refreshItems();
					player.getPacketSender().sendMessage("Your inventory has been emptied.");
				}
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 329) {
				player.getAttributes().setDialogueAction(-1);
				LoyaltyProgrammeHandler.reset(player);
				player.getPacketSender().sendInterface(LoyaltyProgrammeHandler.TITLE_SHOP_INTERFACE);
			} else if(player.getAttributes().getDialogueAction() == 409 || player.getAttributes().getDialogueAction() == 410) {
				for (Skill skill : Skill.values()) {
					int level = skill.equals(Skill.CONSTITUTION) ? 100 : skill.equals(Skill.PRAYER) ? 10 : 1;
					player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(skill == Skill.CONSTITUTION ? 10 : 1));
				}
				player.getEquipment().resetItems().refreshItems();
				player.getInventory().resetItems().refreshItems();
				for(Bank b : player.getBanks()) {
					if(b != null)
						b.resetItems();
				}
				player.getUpdateFlag().flag(Flag.APPEARANCE);
				player.setDifficulty(player.getAttributes().getDialogueAction() == 410 ? Difficulty.HARDCORE_IRONMAN : Difficulty.IRONMAN);
				player.getAdvancedSkills().getSlayer().resetSlayerTask(true);
				PlayerPanel.refreshPanel(player);
				player.getPacketSender().sendInterfaceRemoval();
			}
			break; 
		case 2483: //2nd tele option on 4 option dialogues
			player.getAttributes().setInterfaceId(50);
			if(player.getAttributes().getDialogueAction() == 9) {
				player.getPacketSender().sendInterfaceRemoval();
				ShopManager.getShops().get(6).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 13) {
				player.getPacketSender().sendInterfaceRemoval();
				TeleportHandler.teleportPlayer(player, new Position(2734, 3442, 0), player.getAttributes().getSpellbook().getTeleportType());
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 16) {
				ClanChatManager.deleteClan(player);
			} else if(player.getAttributes().getDialogueAction() == 18) { //Fishing colony
				TeleportHandler.teleportPlayer(player, new Position(2345, 3694, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 25) { //Thieving markets
				DialogueManager.start(player, 41);
				player.getAttributes().setDialogueAction(26);
			} else if(player.getAttributes().getDialogueAction() == 39) { //Slayer: Select masters
				DialogueManager.start(player, 63);
				player.getAttributes().setDialogueAction(40);
			} else if(player.getAttributes().getDialogueAction() == 205) { //Training teleport 2: Experiments
				TeleportHandler.teleportPlayer(player, new Position(3557 + (Misc.getRandom(2)), 9946 + Misc.getRandom(2), 0), player.getAttributes().getSpellbook().getTeleportType());
				if(player.getAttributes().getNewPlayerDelay() > 0)
					player.getPacketSender().sendMessage("Experiments don't drop anything but they are great for quick experience!");
				//Magic.teleportPlayer(player, new Position(2329, 3796, 0), Magic.getType(player.getProperties().getSpellbook()));
			} else if(player.getAttributes().getDialogueAction() == 41) {
				player.getPacketSender().sendInterface(36000);
				player.getPacketSender().sendString(36030, "Current Points:   "+player.getPointsHandler().getSlayerPoints());
			} else if(player.getAttributes().getDialogueAction() == 42) { //Slayer: Reset a task
				DialogueManager.start(player, SlayerDialogues.resetTaskDialogue(player));
			} else if(player.getAttributes().getDialogueAction() == 55) {
				DialogueManager.start(player, 116);
			} else if(player.getAttributes().getDialogueAction() == 212) { //Agility Gnome course
				TeleportHandler.teleportPlayer(player, new Position(2480, 3435, 0), player.getAttributes().getSpellbook().getTeleportType());
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 278) { //Boss teleport page 2 : Tormented Demons
				TeleportHandler.teleportPlayer(player, new Position(2602 + Misc.getRandom(1), 5724 + Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 281) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 289) { //Magicbook switching, modern magic
				if(player.getSkillManager().getMaxLevel(Skill.MAGIC) < 50) {
					DialogueManager.start(player, 303);
					return;
				}
				if(!true)
					DialogueManager.start(player, 302);
				else
					player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setSpellbook(MagicSpellbook.ANCIENT);
				player.getPacketSender().sendTabInterface(Constants.MAGIC_TAB, player.getAttributes().getSpellbook().getInterfaceId());
				Autocasting.resetAutocast(player, true);
			} else if(player.getAttributes().getDialogueAction() == 303) { //Summoning, how to get charms
				DialogueManager.start(player, 310);
				player.getAttributes().setDialogueAction(303);
			} else if(player.getAttributes().getDialogueAction() == 304) { //Summoning, open shop option 2
				ShopManager.getShops().get(26).open(player);
			} else if(player.getAttributes().getDialogueAction() == 320) { //Construction, option 2 buy house
				/*if(player.houseRooms[0][0][0] != null)
					DialogueManager.start(player, 351);
				else
					DialogueManager.start(player, 352);*/
			} else if(player.getAttributes().getDialogueAction() == 340) {
				ShopManager.getShops().get(51).open(player);
			} else if(player.getAttributes().getDialogueAction() == 341) {
				DialogueManager.start(player, 395);
			} else if(player.getAttributes().getDialogueAction() == 348) {
				DialogueManager.start(player, 413);
				player.getAttributes().setDialogueAction(329);
			} else if(player.getAttributes().getDialogueAction() == 356) {
				DialogueManager.start(player, 203);
				player.getAttributes().setDialogueAction(208);
			} else if(player.getAttributes().getDialogueAction() == 405) {
				DialogueManager.start(player, MoreLoyaltyRewardDialogues.buyItem(player, 12159, 150));
			}
			break;
		case 2484: //3rd tele option on 4 option dialogues
			player.getAttributes().setInterfaceId(50);
			if(player.getAttributes().getDialogueAction() == 9) {
				ShopManager.getShops().get(22).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 13) {
				player.getPacketSender().sendInterfaceRemoval();
				TeleportHandler.teleportPlayer(player, new Position(3209, 3215, 1), player.getAttributes().getSpellbook().getTeleportType());
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 16) {
				player.getPacketSender().sendInterfaceRemoval();
				player.getPacketSender().sendTabInterface(Constants.CLAN_CHAT_TAB, 39000);
				player.getPacketSender().sendTab(Constants.CLAN_CHAT_TAB);
			} else if(player.getAttributes().getDialogueAction() == 18) { //Catherby aquarium fishing
				TeleportHandler.teleportPlayer(player, new Position(2836, 3446, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 25) { //Thieving master's chest
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getMaxLevel(Skill.THIEVING) < 20) {
					player.getPacketSender().sendMessage("You need a Thieving level of at least 20 to teleport to the H.A.M camp.");
					return;
				}
				TeleportHandler.teleportPlayer(player, new Position(3165, 9628, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 39) { //Slayer: Teleport to master
				if(player.getAdvancedSkills().getSlayer().getSlayerMaster().getPosition() != null)
					TeleportHandler.teleportPlayer(player, new Position(player.getAdvancedSkills().getSlayer().getSlayerMaster().getPosition().getX(), player.getAdvancedSkills().getSlayer().getSlayerMaster().getPosition().getY(), player.getAdvancedSkills().getSlayer().getSlayerMaster().getPosition().getZ()), player.getAttributes().getSpellbook().getTeleportType());
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 41) { //Slayer open shop
				ShopManager.getShops().get(30).open(player);
			} else if(player.getAttributes().getDialogueAction() == 42) { //Slayer: Ask how many points u get per task
				DialogueManager.start(player, SlayerDialogues.totalPointsReceived(player));
			} else if(player.getAttributes().getDialogueAction() == 55) {
				DialogueManager.start(player, 80);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 205) { //Training teleport 3: Flesh crawlers
				if(player.getAttributes().getNewPlayerDelay() > 0)
					player.getPacketSender().sendMessage("Flesh crawlers are a great source of grimy herbs!");
				TeleportHandler.teleportPlayer(player, new Position(2045, 5194, 0), player.getAttributes().getSpellbook().getTeleportType());
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 212) { //Agility Wilderness course
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getMaxLevel(Skill.AGILITY) < 35) {
					player.getPacketSender().sendMessage("You need an Agility level of at least level 35 to use this course.");
					return;
				}
				TeleportHandler.teleportPlayer(player, new Position(2552, 3556, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 278) { //Boss teleport page 2 : Tormented demon
				player.getPacketSender().sendInterfaceRemoval();
				player.getPacketSender().sendMessage("Revenants can be found roaming the Wilderness.");
			} else if(player.getAttributes().getDialogueAction() == 289) { //Magicbook switching, modern magic
				if(player.getSkillManager().getMaxLevel(Skill.MAGIC) < 65 || player.getSkillManager().getMaxLevel(Skill.DEFENCE) < 40) {
					DialogueManager.start(player, 304);
					return;
				}
				if(player.getAttributes().getSpellbook() == MagicSpellbook.LUNAR) {
					player.getPacketSender().sendMessage("You are already using this spellbook!");
					player.getPacketSender().sendInterfaceRemoval();
					player.getAttributes();
					return;
				}
				DialogueManager.start(player, 302);
				player.getAttributes().setSpellbook(MagicSpellbook.LUNAR);
				player.getPacketSender().sendTabInterface(Constants.MAGIC_TAB, player.getAttributes().getSpellbook().getInterfaceId());
				Autocasting.resetAutocast(player, true);
			} else if(player.getAttributes().getDialogueAction() == 303) { //Summoning Skillcape
				if(player.getSkillManager().getMaxLevel(Skill.SUMMONING) < 99)
					DialogueManager.start(player, 311);
				else
					Skillcapes.buySkillcape(player, Skill.SUMMONING, 12169, 12170, 12171, true, 6970);
				player.getAttributes().setDialogueAction(303);
			} else if(player.getAttributes().getDialogueAction() == 304) { //Summoning, open shop option 3
				ShopManager.getShops().get(27).open(player);
			} else if(player.getAttributes().getDialogueAction() == 340) {
				player.getAttributes().setDialogueAction(-1);
				ShopManager.getShops().get(52).open(player);
			} else if(player.getAttributes().getDialogueAction() == 341) {
				ShopManager.getShops().get(53).open(player);
			} else if(player.getAttributes().getDialogueAction() == 348) {
				player.getPacketSender().sendInterfaceRemoval();
				player.getPacketSender().sendString(1, "www.vanguardps.com/donate/");
				player.getPacketSender().sendMessage("Attempted to open vanguardps.com/donate");	
			} else if(player.getAttributes().getDialogueAction() == 356) {
				DialogueManager.start(player, 117);
				player.getAttributes().setDialogueAction(209);
			} else if(player.getAttributes().getDialogueAction() == 405) {
				DialogueManager.start(player, MoreLoyaltyRewardDialogues.buyItem(player, 12160, 250));
			}
			break;
		case 2485: //4th tele option on 4 option dialogues
			player.getAttributes().setInterfaceId(50);
			if(player.getAttributes().getDialogueAction() == 205) { //Training teleport 4: Bandit Camp
				TeleportHandler.teleportPlayer(player, new Position(3172 + Misc.getRandom(5), 2981 + Misc.getRandom(3), 0), player.getAttributes().getSpellbook().getTeleportType());
				if(player.getAttributes().getNewPlayerDelay() > 0)
					player.getPacketSender().sendMessage("Bandits drop good items and give good experience.");
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 13) {
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
				if (player.getSkillManager().getMaxLevel(Skill.CRAFTING) < 20) {
					player.getPacketSender().sendMessage("You need a Crafting level of at least 20 to visit the Crafting guild.");
					return;
				}
				TeleportHandler.teleportPlayer(player, new Position(2930, 3289, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 16) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 18) { //Fishing trawler tele from fish skill options
				TeleportHandler.teleportPlayer(player, new Position(2676, 3171, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 25) { //Thieving master's chest
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getMaxLevel(Skill.THIEVING) < 99) {
					player.getPacketSender().sendMessage("You need a Thieving level of at least 99 to teleport to the Master's chest.");
					return;
				}
				TeleportHandler.teleportPlayer(player, new Position(2580, 3308), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 39) { //Slayer: Teleport to master
				TeleportHandler.teleportPlayer(player, new Position(3427, 3537, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 41) {
				player.getPacketSender().sendInterfaceRemoval();
				DuoSlayer.resetDuoTeam(player);
			} else if(player.getAttributes().getDialogueAction() == 42) {
				player.getPacketSender().sendInterfaceRemoval();
				DuoSlayer.resetDuoTeam(player);
			} else if(player.getAttributes().getDialogueAction() == 55) {
				player.getAttributes().setDialogueAction(-1);
				if(player.getSkillManager().getMaxLevel(Skill.RUNECRAFTING) >= 99) {
					player.getAttributes().setDialogueAction(56);
					DialogueManager.start(player, 83);
				} else {
					player.getAttributes().setDialogueAction(-1);
					DialogueManager.start(player, 81);
				}
			} else if(player.getAttributes().getDialogueAction() == 208) {
				DialogueManager.start(player, 203);
			} else if(player.getAttributes().getDialogueAction() == 209) {
				DialogueManager.start(player, 203);
				player.getAttributes().setDialogueAction(208);
			} else if(player.getAttributes().getDialogueAction() == 212) { //Agility Wilderness course
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getMaxLevel(Skill.AGILITY) < 55) {
					player.getPacketSender().sendMessage("You need an Agility level of at least level 55 to use this course.");
					return;
				}
				TeleportHandler.teleportPlayer(player, new Position(2998, 3914, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 278) { //Boss teleport page 2 : Previous Page
				DialogueManager.start(player, 277);
				player.getAttributes().setDialogueAction(277);
			} else if(player.getAttributes().getDialogueAction() == 289) { //Magic spellbook switching, cancel option
				player.getPacketSender().sendInterfaceRemoval();
				if(true && player.getAttributes().getSpellbook() == MagicSpellbook.LUNAR)
					//true = false;
					player.getAttributes();
			} else if(player.getAttributes().getDialogueAction() == 303 || player.getAttributes().getDialogueAction() == 9) { //Summoning Pikkupstix close window
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 304) { //Summoning, open shop option cancel
				DialogueManager.start(player, 309);
				player.getAttributes().setDialogueAction(303);
			} else if(player.getAttributes().getDialogueAction() == 341 || player.getAttributes().getDialogueAction() == 348 || player.getAttributes().getDialogueAction() == 356) {
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 403) {
				player.getPacketSender().sendString(1, "www.vanguardps.com/difficulties");
				DialogueManager.start(player, 434);
			} else if(player.getAttributes().getDialogueAction() == 405) {
				DialogueManager.start(player, MoreLoyaltyRewardDialogues.buyItem(player, 12163, 350));
			}
			break;
		case 2494: // 1st tele option on 5 option dialogues
			player.getAttributes().setInterfaceId(50);
			if(player.getAttributes().getDialogueAction() == 206) { //Dungeon teleport 1: Edgeville Dungeon
				TeleportHandler.teleportPlayer(player, new Position(3097 + Misc.getRandom(1), 9869 + Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 208) { //warriors guild
				TeleportHandler.teleportPlayer(player, new Position(2878, 3546), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 209) { //Minigame teleport 2 : Fight Cave
				TeleportHandler.teleportPlayer(player, new Position(2439 + Misc.getRandom(2), 5171 + Misc.getRandom(2), 0), player.getAttributes().getSpellbook().getTeleportType());
			} else 	if(player.getAttributes().getDialogueAction() == 277) { //Boss teleport 1
				TeleportHandler.teleportPlayer(player, new Position(2903, 5204), player.getAttributes().getSpellbook().getTeleportType());
			} else 	if(player.getAttributes().getDialogueAction() == 287) { //Wilderness teleport 1 : Edgeville ditch
				TeleportHandler.teleportPlayer(player, new Position(3088 + Misc.getRandom(3), 3519, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else 	if(player.getAttributes().getDialogueAction() == 294) { //Starter: How do I make money
				DialogueManager.start(player, TutorialDialogues.makingMoney(player));
			} else if(player.getAttributes().getDialogueAction() == 300) { //Recolour first option
				LoyaltyProgrammeHandler.recolourItem(player, 0);
			} else if(player.getAttributes().getDialogueAction() == 315) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor() == null || player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty() == null)
					return;
				player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().setFloor(DungeoneeringFloors.FIRST_FLOOR);
				player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty().refreshInterface();
				player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty().sendMessage("The party leader has changed the party's Dungeoneering floor.");
			}  else if(player.getAttributes().getDialogueAction() == 329) { //Town Crier, loyalty shop
				player.getAttributes().setDialogueAction(-1);
				LoyaltyProgrammeHandler.reset(player);
				player.getPacketSender().sendInterface(LoyaltyProgrammeHandler.TITLE_SHOP_INTERFACE);
			} else if(player.getAttributes().getDialogueAction() == 403) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getDifficulty() != null && player.getDifficulty() == Difficulty.EASY) {
					player.getPacketSender().sendMessage("You are already playing on this difficulty.");
					return;
				}
				List<Item> validItems = player.getEquipment().getValidItems();
				boolean starterItem = validItems.size() == 1 && validItems.get(0).getId() == 1007;
				if(!starterItem) {
					if(player.getEquipment().getValidItems().size() != 0) {
						Bank.depositItems(player, player.getEquipment(), true);
						player.getPacketSender().sendMessage("Your equipment has been sent to your bank.");
					}
				}
				for (Skill skill : Skill.values()) {
					int level = skill.equals(Skill.CONSTITUTION) ? 100 : skill.equals(Skill.PRAYER) ? 10 : 1;
					player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(skill == Skill.CONSTITUTION ? 10 : 1));
				}
				player.getUpdateFlag().flag(Flag.APPEARANCE);
				player.setDifficulty(Difficulty.EASY);
				player.getAdvancedSkills().getSlayer().resetSlayerTask(true);
				PlayerPanel.refreshPanel(player);
				/*if(NormalHighscores.getInstance().isValid())
					try {
						NormalHighscores.getInstance().getConnection().createStatement().executeUpdate("DELETE FROM `hs_users` WHERE username = '"+player.getUsername()+"';");
					} catch (Exception e) {
						NormalHighscores.getInstance().setIsValid(false);
					}*/
			} else if(player.getAttributes().getDialogueAction() == 355) {
				DialogueManager.start(player, HelpbookDialogues.secondDialogue(player));
			} else if(player.getAttributes().getDialogueAction() == 378) {
				ItemLending.lendItemToPlayer(player, 4151, 13444);
			} else if(player.getAttributes().getDialogueAction() == 359) {
				DialogueManager.start(player, 424);
			} else
				player.getAttributes().setDialogueAction(-1);
			break;
		case 2462:
			player.getAttributes().setInterfaceId(50);
			if(Effigies.handleEffigyAction(player, id))
				return;
			if(player.getAttributes().getDialogueAction() == 0) {
				if (!player.getAttributes().getBankPinAttributes().hasBankPin()) {
					DialogueManager.start(player, 12);
					player.getAttributes().setDialogueAction(5);
				} else {
					DialogueManager.start(player, 14);
					player.getAttributes().setDialogueAction(6);
				}
			} else if(player.getAttributes().getDialogueAction() == 1) {
				player.getAttributes().getTutorialFinished()[5] = true;
				StartingHandler.finishTutorial(player, true);
			} else if(player.getAttributes().getDialogueAction() == 3) {
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 4) {
				ShopManager.getShops().get(1).open(player);
			} else if(player.getAttributes().getDialogueAction() == 5) {
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 11 || player.getAttributes().getDialogueAction() == 317) {
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 12 || player.getAttributes().getDialogueAction() == 17) {
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 13) {
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
				if (player.getSkillManager().getMaxLevel(Skill.CRAFTING) < 20) {
					player.getPacketSender().sendMessage("You need a Crafting level of at least 20 to visit the Crafting guild.");
					return;
				}
				TeleportHandler.teleportPlayer(player, new Position(2930, 3289, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 14) {
				TeleportHandler.teleportPlayer(player, new Position(3023, 9740, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 15) {
				TeleportHandler.teleportPlayer(player, new Position(3226, 3253, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 17) {
			} else if(player.getAttributes().getDialogueAction() == 19) { //Prayer monastery
				TeleportHandler.teleportPlayer(player, new Position(3051, 3496, 1), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 20) { //Aubury
				TeleportHandler.teleportPlayer(player, new Position(3253, 3402), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 21) { //Magic guild
				TeleportHandler.teleportPlayer(player, new Position(2591, 3090, 2), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 22) { //Champion's guild
				TeleportHandler.teleportPlayer(player, new Position(3191, 3361, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 23) { //Ranged guild
				TeleportHandler.teleportPlayer(player, new Position(2664, 3432, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 24) { //Herblore training
				TeleportHandler.teleportPlayer(player, new Position(2903, 3450, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 27) { //Cooking guild
				TeleportHandler.teleportPlayer(player, new Position(3047, 4968, 1), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 28) { //Firemaking master
				TeleportHandler.teleportPlayer(player, new Position(2706, 3430, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 29) { //Woodcutting master
				TeleportHandler.teleportPlayer(player, new Position(2721, 3500, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 30) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 31) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				/*Dialogue lol = DialogueManager.dialogues.get(54);
				DialogueManager.start(player, lol);
				player.getProperties().setDialogueAction(48);*/
				ShopManager.getShops().get(10).open(player);
			} else if(player.getAttributes().getDialogueAction() == 43) { //Slayer: Cancel reseting a task
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 45 || player.getAttributes().getDialogueAction() == 46 || player.getAttributes().getDialogueAction() == 54 || player.getAttributes().getDialogueAction() == 47 || player.getAttributes().getDialogueAction() == 51 || player.getAttributes().getDialogueAction() == 56 || player.getAttributes().getDialogueAction() == 55 || player.getAttributes().getDialogueAction() == 58) { //skillcapes No to buying option
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 57) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
				TeleportHandler.teleportPlayer(player, new Position(3370, 3276, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 100) {
				ShopManager.getShops().get(22).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 200) {
				if(player.getSkillManager().getMaxLevel(Skill.FIREMAKING) < 99) { 
					Dialogue dialogue = DialogueManager.getDialogues().get(93);
					DialogueManager.start(player, dialogue);
				} else
					Skillcapes.buySkillcape(player, Skill.FIREMAKING, 9804, 9805, 9806, true, 4288);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 203) {
				if(player.getSkillManager().getMaxLevel(Skill.MINING) < 99) {
					Dialogue dialogue = DialogueManager.getDialogues().get(110);
					DialogueManager.start(player, dialogue);
				} else
					Skillcapes.buySkillcape(player, Skill.MINING, 9792, 9793, 9794, true, 948);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 211) {
				if(player.getSkillManager().getMaxLevel(Skill.MINING) < 17) {
					player.getPacketSender().sendMessage("You need a Mining level of at least 17 to mine this ore.");
				} else if(player.getAttributes().getCurrentInteractingObject() != null)
					Mining.startMining(player, new GameObject(24445, player.getAttributes().getCurrentInteractingObject().getPosition()));
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 215) {
				TeleportHandler.teleportPlayer(player, new Position(2821, 3442), player.getAttributes().getSpellbook().getTeleportType());
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 216) {
				ShopManager.getShops().get(16).open(player);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 218) {
				if(player.getSkillManager().getMaxLevel(Skill.HUNTER) >= 99) {
					Skillcapes.buySkillcape(player, Skill.HUNTER, 9948, 9949, 9950, true, 5113);
				} else 
					DialogueManager.start(player, 238);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 219) {
				if(player.getSkillManager().getMaxLevel(Skill.SMITHING) >= 99) {
					Skillcapes.buySkillcape(player, Skill.SMITHING, 9795, 9796, 9797, true, 2820);
				} else 
					DialogueManager.start(player, 241);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 220) {
				if(player.getSkillManager().getMaxLevel(Skill.COOKING) >= 99) {
					Skillcapes.buySkillcape(player, Skill.COOKING, 9801, 9802, 9803, true, 847);
				} else 
					DialogueManager.start(player, 244);
				player.getAttributes().setDialogueAction(-1);
			} else if(player.getAttributes().getDialogueAction() == 222) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 224) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 279 || player.getAttributes().getDialogueAction() == 283) {
				player.getAttributes().setDialogueAction(-1);
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 282) { //Summoning master teleport
				TeleportHandler.teleportPlayer(player, new Position(2209, 5348, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 286) {
				DialogueManager.start(player, 292);
			} else if(player.getAttributes().getDialogueAction() == 301) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getAdvancedSkills().getSlayer().getDuoSlayer().invitationOwner != null) {
					Player inviteOwner = PlayerHandler.getPlayerForName(player.getAdvancedSkills().getSlayer().getDuoSlayer().invitationOwner);
					if(inviteOwner != null)
						inviteOwner.getPacketSender().sendMessage(""+player.getUsername()+" has declined your invitation.");
					player.getAdvancedSkills().getSlayer().getDuoSlayer().invitationOwner = null;
				}
				player.getPacketSender().sendMessage("You've declined the invitation.");
			} else if(player.getAttributes().getDialogueAction() == 308) {
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 309) {
				if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 2000)
					return;
				Nomad.startFight(player);
				player.getAttributes().getMinigameAttributes().getNomadAttributes().setPartFinished(1, true);
				player.getAttributes().setDialogueAction(-1);
				player.getAttributes().setClickDelay(System.currentTimeMillis());
			} else if(player.getAttributes().getDialogueAction() == 314) {
				TeleportHandler.teleportPlayer(player, new Position(3450, 3715), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 318) { //Construction house portal tele
				TeleportHandler.teleportPlayer(player, new Position(2544, 3094), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 332) {
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 390) {
				DialogueManager.start(player, TutorialDialogues.tutorialDialogues(player));
			} else if(player.getAttributes().getDialogueAction() == 358) { //Farming master
				TeleportHandler.teleportPlayer(player, new Position(3052, 3304), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 360) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getPartyInvitation() != null && player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getPartyInvitation().getOwner() != null)
					player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getPartyInvitation().getOwner().getPacketSender().sendMessage(""+player.getUsername()+" has declined your invitation.");
				player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setPartyInvitation(null);
			} else if(player.getAttributes().getDialogueAction() == 361) {
				DialogueManager.start(player, SlayerDialogues.dialogue(player).nextDialogue());
			} else if(player.getAttributes().getDialogueAction() == 402) {
				DialogueManager.start(player, 436);
			} else if(player.getAttributes().getDialogueAction() == 406) {
				if(player.getAttributes().getInterfaceId() == 50)
					player.getPacketSender().sendInterface(17930);
			} else if(player.getAttributes().getDialogueAction() == 329) {
				player.getPacketSender().sendInterfaceRemoval();
			}
			break; 
		case 2495:
			player.getAttributes().setInterfaceId(50);
			if(player.getAttributes().getDialogueAction() == 208) { // Pest Control
				TeleportHandler.teleportPlayer(player, new Position(2663 + Misc.getRandom(1), 2651 + Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 209) { //Minigame teleport 3: Fight Pit
				TeleportHandler.teleportPlayer(player, new Position(2399, 5177), player.getAttributes().getSpellbook().getTeleportType());
			} else  if(player.getAttributes().getDialogueAction() == 206) { //Dungeon teleport 2: Chaos Tunnels
				TeleportHandler.teleportPlayer(player, new Position(3184 + Misc.getRandom(1), 5471 + Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			} else  if(player.getAttributes().getDialogueAction() == 277) { //Boss tele 2: Corporeal beast
				TeleportHandler.teleportPlayer(player, new Position(2884 + Misc.getRandom(1), 4374 + Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			} else 	if(player.getAttributes().getDialogueAction() == 287) { //Wilderness teleport 2 : Edgeville west drags
				TeleportHandler.teleportPlayer(player, new Position(2980 + Misc.getRandom(3), 3596 + Misc.getRandom(3), 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 294) { //Starter: Why and how do I level up skills?
				DialogueManager.start(player, TutorialDialogues.whyLevelUp(player));
			} else if(player.getAttributes().getDialogueAction() == 300) { //Recolour second option
				LoyaltyProgrammeHandler.recolourItem(player, 1);
			} else if(player.getAttributes().getDialogueAction() == 329) { //Town Crier, visit webshop	
				DialogueManager.start(player, 413);
				
			} else if(player.getAttributes().getDialogueAction() == 355) {
				DialogueManager.start(player, HelpbookDialogues.thirdDialogue(player));
			} else if(player.getAttributes().getDialogueAction() == 378) {
				ItemLending.lendItemToPlayer(player, 11235, 13405);
			} else if(player.getAttributes().getDialogueAction() == 359) {
				Lottery.enterLottery(player);
			} else if(player.getAttributes().getDialogueAction() == 403) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getDifficulty() != null && player.getDifficulty() == Difficulty.NORMAL) {
					player.getPacketSender().sendMessage("You are already playing on this difficulty.");
					return;
				}
				List<Item> validItems = player.getEquipment().getValidItems();
				boolean starterItem = validItems.size() == 1 && validItems.get(0).getId() == 1007;
				if(!starterItem) {
					if(player.getEquipment().getValidItems().size() != 0) {
						Bank.depositItems(player, player.getEquipment(), true);
						player.getPacketSender().sendMessage("Your equipment has been sent to your bank.");
					}
				}
				for (Skill skill : Skill.values()) {
					int level = skill.equals(Skill.CONSTITUTION) ? 100 : skill.equals(Skill.PRAYER) ? 10 : 1;
					player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(skill == Skill.CONSTITUTION ? 10 : 1));
				}
				player.getUpdateFlag().flag(Flag.APPEARANCE);
				player.setDifficulty(Difficulty.NORMAL);
				player.getAdvancedSkills().getSlayer().resetSlayerTask(true);
				PlayerPanel.refreshPanel(player);
			}
			break;
		case 2496:
			player.getAttributes().setInterfaceId(50);
			if(player.getAttributes().getDialogueAction() == 208) { // Duel Arena
				TeleportHandler.teleportPlayer(player, new Position(3368 + Misc.getRandom(5), 3267+ Misc.getRandom(3), 0), player.getAttributes().getSpellbook().getTeleportType());
			} else 	if(player.getAttributes().getDialogueAction() == 209) { // Fight Arena
				TeleportHandler.teleportPlayer(player, new Position(2607, 9682), player.getAttributes().getSpellbook().getTeleportType());
			} else  if(player.getAttributes().getDialogueAction() == 277) { //Boss tele 3: Chaos elemental
				TeleportHandler.teleportPlayer(player, new Position(3291, 3910, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else  if(player.getAttributes().getDialogueAction() == 287) { //Wilderness teleport 3 : Chaos altar
				TeleportHandler.teleportPlayer(player, new Position(3239 + Misc.getRandom(2), 3619 + Misc.getRandom(2), 0), player.getAttributes().getSpellbook().getTeleportType());
			} else  if(player.getAttributes().getDialogueAction() == 206) { //Dungeon 3 : Brimhaven Dungeon
				TeleportHandler.teleportPlayer(player, new Position(2713, 9564, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 294) { //Starter: How do I navigate to other locations?
				DialogueManager.start(player, TutorialDialogues.navigation(player));
			} else if(player.getAttributes().getDialogueAction() == 300) { //Recolour third option
				LoyaltyProgrammeHandler.recolourItem(player, 2);
			} else if(player.getAttributes().getDialogueAction() == 329) { //Visit special members zone
				if(player.getRights() == PlayerRights.PLAYER) {
					player.getAttributes().setDialogueAction(-1);
					DialogueManager.start(player, 382);
					return;
				}
				TeleportHandler.teleportPlayer(player, new Position(3420, 2916), TeleportType.NORMAL);
			} else if(player.getAttributes().getDialogueAction() == 359) {
				DialogueManager.start(player, LotteryDialogues.getCurrentPot(player));
			} else if(player.getAttributes().getDialogueAction() == 378) {
				ItemLending.lendItemToPlayer(player, 15486, 15502);
			} else if(player.getAttributes().getDialogueAction() == 403) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getDifficulty() != null && player.getDifficulty() == Difficulty.HARD) {
					player.getPacketSender().sendMessage("You are already playing on this difficulty.");
					return;
				}
				List<Item> validItems = player.getEquipment().getValidItems();
				boolean starterItem = validItems.size() == 1 && validItems.get(0).getId() == 1007;
				if(!starterItem) {
					if(player.getEquipment().getValidItems().size() != 0) {
						Bank.depositItems(player, player.getEquipment(), true);
						player.getPacketSender().sendMessage("Your equipment has been sent to your bank.");
					}
				}
				for (Skill skill : Skill.values()) {
					int level = skill.equals(Skill.CONSTITUTION) ? 100 : skill.equals(Skill.PRAYER) ? 10 : 1;
					player.getSkillManager().setCurrentLevel(skill, level).setMaxLevel(skill, level).setExperience(skill, SkillManager.getExperienceForLevel(skill == Skill.CONSTITUTION ? 10 : 1));
				}
				player.getUpdateFlag().flag(Flag.APPEARANCE);
				player.setDifficulty(Difficulty.HARD);
				player.getAdvancedSkills().getSlayer().resetSlayerTask(true);
				PlayerPanel.refreshPanel(player);
			}
			break;
		case 2497:
			player.getAttributes().setInterfaceId(50);
			if(player.getAttributes().getDialogueAction() == 277) { //Boss tele 4: Slash bash
				TeleportHandler.teleportPlayer(player, new Position(2525, 9458, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 208) { //Barrows
				TeleportHandler.teleportPlayer(player, new Position(3565, 3313, 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 209) { //Soul Wars , FISHING TRAWLER
				TeleportHandler.teleportPlayer(player, new Position(2676, 3171), player.getAttributes().getSpellbook().getTeleportType());
			} else  if(player.getAttributes().getDialogueAction() == 287) { //Wilderness teleport 4 : East dragons
				TeleportHandler.teleportPlayer(player, new Position(3329 + Misc.getRandom(2), 3660 + Misc.getRandom(2), 0), player.getAttributes().getSpellbook().getTeleportType());
			} else  if(player.getAttributes().getDialogueAction() == 206) { //Dungeon teleport 4 : Taverly dungeon
				TeleportHandler.teleportPlayer(player, new Position(2884 + Misc.getRandom(1), 9799 + Misc.getRandom(1), 0), player.getAttributes().getSpellbook().getTeleportType());
			} else if(player.getAttributes().getDialogueAction() == 294) { //Starter: How do I navigate to other locations?
				DialogueManager.start(player, TutorialDialogues.mainShops(player));
			} else if(player.getAttributes().getDialogueAction() == 300) { //Recolour fourth option
				LoyaltyProgrammeHandler.recolourItem(player, 3);
			} else if(player.getAttributes().getDialogueAction() == 329) { //Town Crier, report bug
				player.getPacketSender().sendInterfaceRemoval();
				player.getPacketSender().sendString(1, "[BUGREPORT]");
				player.getPacketSender().sendMessage("");
				player.getPacketSender().sendMessage("Please enter as much information as possible in the second box!");
			} else if(player.getAttributes().getDialogueAction() == 355) {
				DialogueManager.start(player, HelpbookDialogues.fourthDialogue(player));
			} else if(player.getAttributes().getDialogueAction() == 378) {
				ItemLending.lendItemToPlayer(player, 13661, 13661);
			} else if(player.getAttributes().getDialogueAction() == 359) {
				DialogueManager.start(player, LotteryDialogues.getLastWinner(player));
			} else if(player.getAttributes().getDialogueAction() == 403) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getDifficulty() != null && player.getDifficulty() == Difficulty.IRONMAN) {
					player.getPacketSender().sendMessage("You are already playing on this difficulty.");
					return;
				} else {
					DialogueManager.start(player, 446);
					player.getAttributes().setDialogueAction(409);
					return;
				}
			}
			break;
		case 2498:
			player.getAttributes().setInterfaceId(50);
			if(player.getAttributes().getDialogueAction() == 1) {
				StartingHandler.finishTutorial(player, false);
			} else if(player.getAttributes().getDialogueAction() == 208) {
				DialogueManager.start(player, 117);
				player.getAttributes().setDialogueAction(209);
			} else if(player.getAttributes().getDialogueAction() == 206) { //Dungeon tele 5 : More options
				DialogueManager.start(player, 412);
				player.getAttributes().setDialogueAction(353);
			} else if(player.getAttributes().getDialogueAction() == 209) {
				DialogueManager.start(player, 414);
				player.getAttributes().setDialogueAction(356);
			} else  if(player.getAttributes().getDialogueAction() == 277) { //Boss tele 5: Next Page
				DialogueManager.start(player, 278);
				player.getAttributes().setDialogueAction(278);
			} else 	if(player.getAttributes().getDialogueAction() == 287) { //Wilderness teleport 5 : Bandit camp (Multi)
				int random = Misc.getRandom(4);
				switch(random) {
				case 0:
					TeleportHandler.teleportPlayer(player, new Position(3035, 3701, 0), player.getAttributes().getSpellbook().getTeleportType());
					break;
				case 1:
					TeleportHandler.teleportPlayer(player, new Position(3036, 3694, 0), player.getAttributes().getSpellbook().getTeleportType());
					break;
				case 2:
					TeleportHandler.teleportPlayer(player, new Position(3045, 3697, 0), player.getAttributes().getSpellbook().getTeleportType());
					break;
				case 3:
					TeleportHandler.teleportPlayer(player, new Position(3043, 3691, 0), player.getAttributes().getSpellbook().getTeleportType());
					break;
				case 4:
					TeleportHandler.teleportPlayer(player, new Position(3037, 3687, 0), player.getAttributes().getSpellbook().getTeleportType());
					break;
				}
			} else if(player.getAttributes().getDialogueAction() == 294) { //Starter: Skip tutorial and play desolace!
				StartingHandler.finishTutorial(player, false);
			} else if(player.getAttributes().getDialogueAction() == 300) { //Recolour fifth option
				LoyaltyProgrammeHandler.recolourItem(player, 4);
			} else if(player.getAttributes().getDialogueAction() == 329 || player.getAttributes().getDialogueAction() == 359) { //Town crier, close, fifth option
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 355) {
				player.getPacketSender().sendInterfaceRemoval();
			} else if(player.getAttributes().getDialogueAction() == 378) {
				ItemLending.lendItemToPlayer(player, -1, -1);
			} else if(player.getAttributes().getDialogueAction() == 403) {
				player.getPacketSender().sendInterfaceRemoval();
				if(player.getDifficulty() != null && player.getDifficulty() == Difficulty.HARDCORE_IRONMAN) {
					player.getPacketSender().sendMessage("You are already playing on this difficulty.");
					return;
				} else {
					DialogueManager.start(player, 446);
					player.getAttributes().setDialogueAction(410);
					return;
				}
			}
			break;
			//Bank pins start
		case 14922:
			player.getPacketSender().sendInterfaceRemoval();
			break;
		case 14873:
		case 14874:
		case 14875:
		case 14876:
		case 14877:
		case 14878:
		case 14879:
		case 14880:
		case 14881:
		case 14882:
			BankPin.clickedButton(player, id);
			break;
		case 150:
			player.getCombatAttributes().setAutoRetaliation(!player.getCombatAttributes().isAutoRetaliation());
			//player.getPacketSender().sendConfig(172, player.getCombatAttributes().isAutoRetaliation() ? 1 : 0);
			break;
		case 7853:
		case 7854:
			player.getPacketSender().sendTabInterface(Constants.OPTIONS_TAB, 904);
			break;
		case 29332:
			ClanChat clan = player.getAttributes().getClanChat();
			if (clan == null) {
				player.getPacketSender().sendMessage("You are not in a clan channel.");
				return;
			}
			ClanChatManager.leave(player, false);
			player.getAttributes().setClanChatName(null);
			break;
		case 29455:
			if(player.getAttributes().getInterfaceId() > 0) {
				player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
				return;
			}
			if(player.getAttributes().getClanChat() == null) {
				player.getPacketSender().sendMessage("You are not in a clan chat channel.");
				return;
			}
			ClanChat chat = player.getAttributes().getClanChat();
			if(chat.taskSubmitted) {
				player.getPacketSender().sendMessage("This clan chat has already requested a lootshare change. Try again in 60 seconds.");
				return;
			}
			if(chat.getOwnerName().equalsIgnoreCase(player.getUsername()) || player.getRights() == PlayerRights.DEVELOPER || player.getRights() == PlayerRights.OWNER) {
				DialogueManager.start(player, ClanChatManager.getToggleLootshareDialogue(chat));
				player.getAttributes().setDialogueAction(330);
			} else
				player.getPacketSender().sendMessage("Only the clan chat owner can toggle this option.");
			chat = null;
			//ClanChatManager.toggleLootShare(player);
			break;
		case 1035:
		case 152:
			if(player.getAttributes().getRunEnergy() <= 1) {
				player.getPacketSender().sendMessage("You do not have enough energy to do this.");
				player.getAttributes().setRunning(false);
			} else
				player.getAttributes().setRunning(!player.getAttributes().isRunning());
			player.getPacketSender().sendRunStatus();
			break;
		case 27009:
			MoneyPouch.toBank(player);
			break;
		case 27005:
		case 22012:
			Bank.depositItems(player, id == 27005 ? player.getEquipment() : player.getInventory(), false);
			break;
		case 22008:
			if(!player.getAttributes().isBanking() || player.getAttributes().getInterfaceId() != 5292)
				return;
			player.getAttributes().setNoteWithdrawal(!player.getAttributes().withdrawAsNote());
			break;
		case 22004:
			if(!player.getAttributes().isBanking())
				return;
			if(!player.getAttributes().getBankSearchingAttribtues().isSearchingBank()) {
				player.getAttributes().getBankSearchingAttribtues().setSearchingBank(true);
				player.getAttributes().setInputHandling(new EnterSyntaxToBankSearchFor());
				player.getPacketSender().sendEnterInputPrompt("What would you like to search for?");
			} else {
				BankSearchAttributes.stopSearch(player, true);
			}
			break;
		case 27014:
		case 27015:
		case 27016:
		case 27017:
		case 27018:
		case 27019:
		case 27020:
		case 27021:
		case 27022:
			if(!player.getAttributes().isBanking())
				return;
			if(player.getAttributes().getBankSearchingAttribtues().isSearchingBank())
				BankSearchAttributes.stopSearch(player, true);
			int bankId = id - 27014;
			boolean empty = bankId > 0 ? Bank.isEmpty(player.getBank(bankId)) : false;
			if(!empty || bankId == 0) {
				player.getAttributes().setCurrentBankTab(bankId);
				player.getPacketSender().sendString(27002, Integer.toString(player.getAttributes().getCurrentBankTab()));
				player.getPacketSender().sendString(27000, "1");
				player.getBank(bankId).open();
			} else
				player.getPacketSender().sendMessage("To create a new tab, please drag an item here.");	
			break;
		case 27023:
			if(!player.getAttributes().isBanking())
				return;
			player.getAdvancedSkills().getSummoning().toBank();
			break;
		case 2458:
			World.deregister(player);
			break;
		case 27651:
		case 21341:
			if(player.getAttributes().getInterfaceId() < 0) {
				BonusManager.update(player);
				player.getPacketSender().sendInterface(21172);
			} else 
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			break;
		case 2799:
		case 2798:
		case 1747:
		case 8890:
		case 8886:
		case 8875:
		case 8871:
		case 8894:
			ChatboxInterfaceSkillAction.handleChatboxInterfaceButtons(player, id);
			break;
		case 1748:
			player.getPacketSender().sendEnterAmountPrompt("Enter amount:");
			break;
		default:
			break;
		}
	}

	private static boolean defaultButton(Player player, int id) {
		if(id == 27658) { //XP LOCK
			player.getAttributes().setExperienceLocked(!player.getAttributes().experienceLocked());
			String type = player.getAttributes().experienceLocked() ? "locked" : "unlocked";
			player.getPacketSender().sendMessage("Your experience is now "+type+".");
			PlayerPanel.refreshPanel(player);
			return false;
		}
		if (PrayerHandler.isButton(id)) {
			PrayerHandler.togglePrayerWithActionButton(player, id);
			return false;
		}
		if (CurseHandler.isButton(player, id)) {
			return false;
		}
		if(id > 31000 && id < 33000 && !player.getAttributes().hasStarted()) //Clicking skill buttons without finishing tut
			return false;
		return true;
	}
}
