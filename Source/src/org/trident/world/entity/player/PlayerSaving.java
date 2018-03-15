package org.trident.world.entity.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.trident.engine.task.impl.FamiliarSpawnTask;
import org.trident.model.Difficulty;
import org.trident.model.Item;
import org.trident.model.LendedItem;
import org.trident.model.MagicSpellbook;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.Prayerbook;
import org.trident.model.Skill;
import org.trident.model.PlayerRelations.PrivateChatStatus;
import org.trident.model.container.impl.Bank;
import org.trident.net.login.LoginResponses;
import org.trident.util.Misc;
import org.trident.world.content.clan.ClanChatMessageColor;
import org.trident.world.content.combat.weapons.WeaponHandler.AttackStyle;
import org.trident.world.content.skills.impl.slayer.SlayerMaster;
import org.trident.world.content.skills.impl.slayer.SlayerTasks;

/*
 * Manages the player saving and loading process.
 * Using PI's saving because it's easy to manage & edit.
 * @author Gabbe
 */

public class PlayerSaving {

	/*
	 * The directory in which saved player files are stored.
	 */
	private static final String DIRECTORY = "./data/saves/accounts/";

	/*
	 * Saves a player's attributes to a .txt file located in {@code DIRECTORY}.
	 * @param player	The player's attributes to save
	 */
	public static void save(Player player) {
		if(player == null) 
			return;
		BufferedWriter characterfile = null;
		try {
			characterfile = new BufferedWriter(new FileWriter(DIRECTORY + player.getUsername().toLowerCase()+".txt"));
			/* ACCOUNT */
			characterfile.write("[ACCOUNT]", 0, 9);
			characterfile.newLine();
			characterfile.write("character-username = ", 0, 21);
			characterfile.write(player.getUsername(), 0, player.getUsername().length());
			characterfile.newLine();
			characterfile.write("character-password = ", 0, 21);
			characterfile.write(player.getPassword(), 0, player.getPassword().length());
			characterfile.newLine();
			characterfile.write("email-adress = ", 0, 15);
			String mail = player.getEmailAdress() == null ? "n" : player.getEmailAdress();
			characterfile.write(mail, 0, mail.length());
			characterfile.newLine();
			characterfile.write("last-IP = ", 0, 10);
			characterfile.write(player.getHostAdress(), 0, player.getHostAdress().length());
			characterfile.newLine();
			characterfile.write("last-CPU = ", 0, 11);
			characterfile.write(""+player.getHardwareNumber(), 0, String.valueOf(player.getHardwareNumber()).length());
			characterfile.newLine();
			characterfile.newLine();

			/* CHARACTER */
			characterfile.write("[CHARACTER]", 0, 11);
			characterfile.newLine();
			characterfile.write("difficulty = ", 0, 13);
			characterfile.write(Integer.valueOf(player.getDifficulty() != null ? player.getDifficulty().ordinal() : -1).toString(), 0, Integer.valueOf(player.getDifficulty() != null ? player.getDifficulty().ordinal() : -1).toString().length());
			characterfile.newLine();
			characterfile.write("positionX = ", 0, 12);
			characterfile.write(Integer.valueOf(player.getPosition().getX()).toString(), 0, Integer.valueOf(player.getPosition().getX()).toString().length());
			characterfile.newLine();
			characterfile.write("positionY = ", 0, 12);
			characterfile.write(Integer.valueOf(player.getPosition().getY()).toString(), 0, Integer.valueOf(player.getPosition().getY()).toString().length());
			characterfile.newLine();
			characterfile.write("positionZ = ", 0, 12);
			characterfile.write(Integer.valueOf(player.getPosition().getZ()).toString(), 0, Integer.valueOf(player.getPosition().getZ()).toString().length());
			characterfile.newLine();
			characterfile.write("rights = ", 0, 9);
			characterfile.write(Integer.valueOf(player.getRights().ordinal()).toString(), 0, Integer.valueOf(player.getRights().ordinal()).toString().length());
			characterfile.newLine();
			characterfile.write("title = ", 0, 8);
			characterfile.write(Integer.valueOf(player.getAttributes().getLoyaltyTitle()).toString(), 0, Integer.valueOf(player.getAttributes().getLoyaltyTitle()).toString().length());
			characterfile.newLine();
			characterfile.write("running = ", 0, 10);
			characterfile.write(Boolean.valueOf(player.getAttributes().isRunning()).toString(), 0, Boolean.valueOf(player.getAttributes().isRunning()).toString().length());
			characterfile.newLine();
			characterfile.write("run-energy = ", 0, 13);
			characterfile.write(Integer.valueOf(player.getAttributes().getRunEnergy()).toString(), 0, Integer.valueOf(player.getAttributes().getRunEnergy()).toString().length());
			characterfile.newLine();
			characterfile.write("got-starter = ", 0, 13);
			characterfile.write(Boolean.valueOf(player.getAttributes().hasStarted()).toString(), 0, Boolean.valueOf(player.getAttributes().hasStarted()).toString().length());
			characterfile.newLine();
			for(int i = 0; i < player.getAttributes().getTutorialFinished().length; i++) {
				characterfile.write("finished-tut-"+i+" = ", 0, 17);
				characterfile.write(Boolean.valueOf(player.getAttributes().getTutorialFinished()[i]).toString(), 0, Boolean.valueOf(player.getAttributes().getTutorialFinished()[i]).toString().length());
				characterfile.newLine();
			}
			characterfile.write("experience-locked = ", 0, 20);
			characterfile.write(Boolean.valueOf(player.getAttributes().experienceLocked()).toString(), 0, Boolean.valueOf(player.getAttributes().experienceLocked()).toString().length());
			characterfile.newLine();
			characterfile.write("spell-book = ", 0, 13);
			characterfile.write(Integer.valueOf(player.getAttributes().getSpellbook().ordinal()).toString(), 0, Integer.valueOf(player.getAttributes().getSpellbook().ordinal()).toString().length());
			characterfile.newLine();
			characterfile.write("prayer-book = ", 0, 14);
			characterfile.write(Integer.valueOf(player.getAttributes().getPrayerbook().ordinal()).toString(), 0, Integer.valueOf(player.getAttributes().getPrayerbook().ordinal()).toString().length());
			characterfile.newLine();
			characterfile.write("dung-tokens = ", 0, 14);
			characterfile.write(Integer.valueOf(player.getPointsHandler().getDungeoneeringTokens()).toString(), 0, Integer.valueOf(player.getPointsHandler().getDungeoneeringTokens()).toString().length());
			characterfile.newLine();
			characterfile.write("achievement-points = ", 0, 21);
			characterfile.write(Integer.valueOf(player.getPointsHandler().getAchievementPoints()).toString(), 0, Integer.valueOf(player.getPointsHandler().getAchievementPoints()).toString().length());
			characterfile.newLine();
			characterfile.write("conquest-points = ", 0, 18);
			characterfile.write(Integer.valueOf(player.getPointsHandler().getConquestPoints()).toString(), 0, Integer.valueOf(player.getPointsHandler().getConquestPoints()).toString().length());
			characterfile.newLine();
			characterfile.write("loyalty-points = ", 0, 17);
			characterfile.write(Integer.valueOf(player.getPointsHandler().getLoyaltyProgrammePoints()).toString(), 0, Integer.valueOf(player.getPointsHandler().getLoyaltyProgrammePoints()).toString().length());
			characterfile.newLine();
			characterfile.write("commendations = ", 0, 16);
			characterfile.write(Integer.valueOf(player.getPointsHandler().getCommendations()).toString(), 0, Integer.valueOf(player.getPointsHandler().getCommendations()).toString().length());
			characterfile.newLine();
			characterfile.write("donator-points = ", 0, 16);
			characterfile.write(Integer.valueOf(player.getPointsHandler().getDonatorPoints()).toString(), 0, Integer.valueOf(player.getPointsHandler().getDonatorPoints()).toString().length());
			characterfile.newLine();
			characterfile.write("slayer-points = ", 0, 16);
			characterfile.write(Integer.valueOf(player.getPointsHandler().getSlayerPoints()).toString(), 0, Integer.valueOf(player.getPointsHandler().getSlayerPoints()).toString().length());
			characterfile.newLine();
			characterfile.write("player-killing-points = ", 0, 24);
			characterfile.write(Integer.valueOf(player.getPointsHandler().getPkPoints()).toString(), 0, Integer.valueOf(player.getPointsHandler().getPkPoints()).toString().length());
			characterfile.newLine();
			characterfile.write("zeals = ", 0, 8);
			characterfile.write(Integer.valueOf(player.getPointsHandler().getZeals()).toString(), 0, Integer.valueOf(player.getPointsHandler().getZeals()).toString().length());
			characterfile.newLine();
			characterfile.write("wildy-kills = ", 0, 14);
			characterfile.write(Integer.valueOf(player.getPlayerCombatAttributes().getKills()).toString(), 0, Integer.valueOf(player.getPlayerCombatAttributes().getKills()).toString().length());
			characterfile.newLine();
			characterfile.write("wildy-deaths = ", 0, 15);
			characterfile.write(Integer.valueOf(player.getPlayerCombatAttributes().getDeaths()).toString(), 0, Integer.valueOf(player.getPlayerCombatAttributes().getDeaths()).toString().length());
			characterfile.newLine();
			characterfile.write("arena-victories = ", 0, 18);
			characterfile.write(Integer.valueOf(player.getDueling().arenaStats[1]).toString(), 0, Integer.valueOf(player.getDueling().arenaStats[1]).toString().length());
			characterfile.newLine();
			characterfile.write("arena-losses = ", 0, 15);
			characterfile.write(Integer.valueOf(player.getDueling().arenaStats[0]).toString(), 0, Integer.valueOf(player.getDueling().arenaStats[0]).toString().length());
			characterfile.newLine();
			characterfile.newLine();

			/* COMBAT */
			characterfile.write("[COMBAT]", 0, 8);
			characterfile.newLine();
			characterfile.write("weapon-attack-style = ", 0, 22);
			characterfile.write(Integer.valueOf(player.getPlayerCombatAttributes().getAttackStyle().ordinal()).toString(), 0, Integer.valueOf(player.getPlayerCombatAttributes().getAttackStyle().ordinal()).toString().length());
			characterfile.newLine();
			characterfile.write("special-attack-energy = ", 0, 24);
			characterfile.write(Double.valueOf(player.getPlayerCombatAttributes().getSpecialAttackAmount()).toString(), 0, Double.valueOf(player.getPlayerCombatAttributes().getSpecialAttackAmount()).toString().length());
			characterfile.newLine();
			characterfile.write("auto-retaliating = ", 0, 19);
			characterfile.write(Boolean.valueOf(player.getCombatAttributes().isAutoRetaliation()).toString(), 0, Boolean.valueOf(player.getCombatAttributes().isAutoRetaliation()).toString().length());
			characterfile.newLine();
			characterfile.write("dfs-charges = ", 0, 14);
			characterfile.write(Integer.valueOf(player.getAttributes().getDragonFireCharges()).toString(), 0, Integer.valueOf(player.getAttributes().getDragonFireCharges()).toString().length());
			characterfile.newLine();
			characterfile.write("last-veng = ", 0, 12);
			characterfile.write(Long.valueOf(player.getPlayerCombatAttributes().getLastVengeanceCast()).toString(), 0, Long.valueOf(player.getPlayerCombatAttributes().getLastVengeanceCast()).toString().length());
			characterfile.newLine();
			characterfile.write("last-aid = ", 0, 11);
			characterfile.write(Long.valueOf(player.getCombatAttributes().lastAid).toString(), 0, Long.valueOf(player.getCombatAttributes().lastAid).toString().length());
			characterfile.newLine();
			characterfile.write("target-percentage = ", 0, 20);
			characterfile.write(Integer.valueOf(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTargetPercentage()).toString(), 0, Integer.valueOf(player.getPlayerCombatAttributes().getBountyHunterAttributes().getTargetPercentage()).toString().length());
			characterfile.newLine();
			characterfile.write("target-percentage-timer = ", 0, 26);
			characterfile.write(Long.valueOf(player.getPlayerCombatAttributes().getBountyHunterAttributes().getLastTargetPercentageIncrease()).toString(), 0, Long.valueOf(player.getPlayerCombatAttributes().getBountyHunterAttributes().getLastTargetPercentageIncrease()).toString().length());
			characterfile.newLine();
			characterfile.write("entered-gwd-boss = ", 0, 19);
			characterfile.write(Boolean.valueOf(player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()).toString(), 0, Boolean.valueOf(player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()).toString().length());
			characterfile.newLine();
			characterfile.write("gwd-altar-timer = ", 0, 18);
			characterfile.write(Long.valueOf(player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getAltarDelay()).toString(), 0, Long.valueOf(player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getAltarDelay()).toString().length());
			characterfile.newLine();
			for(int i = 0; i < player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount().length; i++) {
				characterfile.write("gwd-killcount-"+i+" = ", 0, 18);
				characterfile.write(Integer.valueOf(player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[i]).toString(), 0, Integer.valueOf(player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[i]).toString().length());
				characterfile.newLine();
			}
			characterfile.write("poison = ", 0, 9);
			characterfile.write(Integer.valueOf(player.getCombatAttributes().getCurrentPoisonDamage()).toString(), 0, Integer.valueOf(player.getCombatAttributes().getCurrentPoisonDamage()).toString().length());
			characterfile.newLine();
			characterfile.write("killed-players = ", 0, 17);
			if (player.getPlayerCombatAttributes().getPkRewardSystem().getKilledPlayers().size() > 0) {
				for (String s : player.getPlayerCombatAttributes().getPkRewardSystem().getKilledPlayers()) {
					characterfile.write(s + "\t");
				}
			}
			characterfile.newLine();
			characterfile.newLine();

			/* APPEARANCE */
			characterfile.write("[APPEARANCE]", 0, 12);
			characterfile.newLine();
			for (int i = 0; i < player.getAppearance().getLook().length; i++) {
				characterfile.write("character-look = ", 0, 17);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.getAppearance().getLook()[i]), 0,Integer.toString(player.getAppearance().getLook()[i]).length());
				characterfile.newLine();
			}
			characterfile.newLine();

			/* MONEY POUCH */
			characterfile.write("[MONEY POUCH]", 0, 13);
			characterfile.newLine();
			characterfile.write("money-in-pouch = ", 0, 17);
			characterfile.write(Integer.valueOf(player.getAttributes().getMoneyInPouch()).toString(), 0, Integer.valueOf(player.getAttributes().getMoneyInPouch()).toString().length());
			characterfile.newLine();
			characterfile.newLine();

			/* ITEMS IN INVENTORY */
			characterfile.write("[INVENTORY]", 0, 11);
			characterfile.newLine();
			for (int i = 0; i < player.getInventory().capacity(); i++) {
				if(player.getInventory().getItems()[i].getId() > 0) {
					characterfile.write("character-item = ", 0, 17);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.getInventory().getItems()[i].getId()), 0, Integer.toString(player.getInventory().getItems()[i].getId()).length());
					characterfile.write("	", 0, 1);
					characterfile.write(Integer.toString(player.getInventory().getItems()[i].getAmount()), 0,Integer.toString(player.getInventory().getItems()[i].getAmount()).length());
					characterfile.newLine();
				}
			}
			characterfile.newLine();

			/* ITEMS EQUIPPED */
			characterfile.write("[EQUIPMENT]", 0, 11);
			characterfile.newLine();
			for (int i = 0; i < player.getEquipment().capacity(); i++) {
				characterfile.write("character-equip = ", 0, 18);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.getEquipment().getItems()[i].getId()), 0, Integer.toString(player.getEquipment().getItems()[i].getId()).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.getEquipment().getItems()[i].getAmount()), 0, Integer.toString(player.getEquipment().getItems()[i].getAmount()).length());
				characterfile.write("	", 0, 1);
				characterfile.newLine();
			}
			characterfile.newLine();

			/* BANK */
			characterfile.write("[BANK]", 0, 6);
			characterfile.newLine();
			for(int tab = 0; tab < 9; tab++) {
				for (int i = 0; i < player.getBank(tab).capacity(); i++) {
					if (player.getBank(tab).getItems()[i].getId() > 0) {
						characterfile.write("character-bank-"+tab+" = ", 0, new String("character-bank-"+tab+" = ").length());
						characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
						characterfile.write("	", 0, 1);
						characterfile.write(Integer.toString(player.getBank(tab).getItems()[i].getId()), 0, Integer.toString(player.getBank(tab).getItems()[i].getId()).length());
						characterfile.write("	", 0, 1);
						characterfile.write(Integer.toString(player.getBank(tab).getItems()[i].getAmount()), 0, Integer.toString(player.getBank(tab).getItems()[i].getAmount()).length());
						characterfile.newLine();
					}
				}
			}
			characterfile.write("invalid-pin-attempts = ", 0, 23);
			characterfile.write(Integer.valueOf(player.getAttributes().getBankPinAttributes().getInvalidAttempts()).toString(), 0, Integer.valueOf(player.getAttributes().getBankPinAttributes().getInvalidAttempts()).toString().length());
			characterfile.newLine();
			characterfile.write("last-pin-attempt = ", 0, 19);
			characterfile.write(Long.valueOf(player.getAttributes().getBankPinAttributes().getLastAttempt()).toString(), 0, Long.valueOf(player.getAttributes().getBankPinAttributes().getLastAttempt()).toString().length());
			characterfile.newLine();
			characterfile.write("has-pin = ", 0, 10);
			characterfile.write(Boolean.valueOf(player.getAttributes().getBankPinAttributes().hasBankPin()).toString(), 0, Boolean.valueOf(player.getAttributes().getBankPinAttributes().hasBankPin()).toString().length());
			characterfile.newLine();
			if(player.getAttributes().getBankPinAttributes().hasBankPin()) {
				for (int i = 0; i < player.getAttributes().getBankPinAttributes().getBankPin().length; i++) {
					characterfile.write("character-pin-"+i+" = ", 0, new String("character-pin-"+i+" = ").length());
					characterfile.write(Integer.toString(player.getAttributes().getBankPinAttributes().getBankPin()[i]), 0, Integer.toString(player.getAttributes().getBankPinAttributes().getBankPin()[i]).length());
					characterfile.newLine();
				}
			}
			characterfile.newLine();

			/* FAMILIAR'S INVENTORY */
			characterfile.write("[STORED]", 0, 8);
			characterfile.newLine();
			for (int i = 0; i < 30; i++) {
				if(i >= player.getAdvancedSkills().getSummoning().storedItems.size())
					continue;
				characterfile.write("stored = ", 0, 9);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.getAdvancedSkills().getSummoning().storedItems.get(i).getId()), 0, Integer.toString(player.getAdvancedSkills().getSummoning().storedItems.get(i).getId()).length());
				characterfile.newLine();
			}
			characterfile.newLine();

			/* SKILLING */
			characterfile.write("[STATS]", 0, 7);
			characterfile.newLine();
			for (int i = 0; i < Skill.values().length; i++) {
				Skill skill = Skill.forId(i);
				characterfile.write("character-skill = ", 0, 18);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.getSkillManager().getCurrentLevel(skill)), 0, Integer.toString(player.getSkillManager().getCurrentLevel(skill)).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.getSkillManager().getMaxLevel(skill)), 0, Integer.toString(player.getSkillManager().getMaxLevel(skill)).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.getSkillManager().getExperience(skill)), 0, Integer.toString(player.getSkillManager().getExperience(skill)).length());
				characterfile.newLine();
			}
			characterfile.newLine();
			characterfile.write("[SKILLING]", 0, 10);
			characterfile.newLine();
			characterfile.write("ardougne-chest-timer = ", 0, 23);
			characterfile.write(Long.valueOf(player.getAttributes().getArdougneChestLootingDelay()).toString(), 0, Long.valueOf(player.getAttributes().getArdougneChestLootingDelay()).toString().length());
			characterfile.newLine();
			characterfile.write("slayer-task = ", 0, 14);
			characterfile.write(Integer.valueOf(player.getAdvancedSkills().getSlayer().getSlayerTask().ordinal()).toString(), 0, Integer.valueOf(player.getAdvancedSkills().getSlayer().getSlayerTask().ordinal()).toString().length());
			characterfile.newLine();
			characterfile.write("previous-slayer-task = ", 0, 23);
			characterfile.write(Integer.valueOf(player.getAdvancedSkills().getSlayer().getLastTask().ordinal()).toString(), 0, Integer.valueOf(player.getAdvancedSkills().getSlayer().getLastTask().ordinal()).toString().length());
			characterfile.newLine();
			characterfile.write("slayer-task-amount = ", 0, 21);
			characterfile.write(Integer.valueOf(player.getAdvancedSkills().getSlayer().getAmountToSlay()).toString(), 0, Integer.valueOf(player.getAdvancedSkills().getSlayer().getAmountToSlay()).toString().length());
			characterfile.newLine();
			characterfile.write("slayer-task-streak = ", 0, 21);
			characterfile.write(Integer.valueOf(player.getAdvancedSkills().getSlayer().getTaskStreak()).toString(), 0, Integer.valueOf(player.getAdvancedSkills().getSlayer().getTaskStreak()).toString().length());
			characterfile.newLine();
			characterfile.write("slayer-master = ", 0, 16);
			characterfile.write(Integer.valueOf(player.getAdvancedSkills().getSlayer().getSlayerMaster().ordinal()).toString(), 0, Integer.valueOf(player.getAdvancedSkills().getSlayer().getSlayerMaster().ordinal()).toString().length());
			characterfile.newLine();
			characterfile.write("duo-slayer = ", 0, 13);
			characterfile.write(Boolean.valueOf(player.getAdvancedSkills().getSlayer().doingDuoSlayer).toString(), 0, Boolean.valueOf(player.getAdvancedSkills().getSlayer().doingDuoSlayer).toString().length());
			characterfile.newLine();
			characterfile.write("duo-slayer-partner = ", 0, 21);
			characterfile.write(player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString() == null ? "null" : player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString(), 0, (player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString() == null ? "null" : player.getAdvancedSkills().getSlayer().getDuoSlayer().getDuoPartnerString()).length());
			characterfile.newLine();
			characterfile.write("double-slayer-xp = ", 0, 19);
			characterfile.write(Boolean.valueOf(player.getAdvancedSkills().getSlayer().doubleSlayerXP).toString(), 0, Boolean.valueOf(player.getAdvancedSkills().getSlayer().doubleSlayerXP).toString().length());
			characterfile.newLine();
			characterfile.write("summoning-npcid = ", 0, 18);
			characterfile.write(Integer.valueOf(player.getAdvancedSkills().getSummoning().getFamiliar() != null ? player.getAdvancedSkills().getSummoning().getFamiliar().getSummonNpc().getId() : -1).toString(), 0, Integer.valueOf(player.getAdvancedSkills().getSummoning().getFamiliar() != null ? player.getAdvancedSkills().getSummoning().getFamiliar().getSummonNpc().getId() : -1).toString().length());
			characterfile.newLine();
			characterfile.write("summoning-death-timer = ", 0, 24);
			characterfile.write(Integer.valueOf(player.getAdvancedSkills().getSummoning().getFamiliar() != null ? player.getAdvancedSkills().getSummoning().getFamiliar().getDeathTimer() : -1).toString(), 0, Integer.valueOf(player.getAdvancedSkills().getSummoning().getFamiliar() != null ? player.getAdvancedSkills().getSummoning().getFamiliar().getDeathTimer() : -1).toString().length());
			characterfile.newLine();
			for(int i = 0; i < player.getSkillManager().getSkillAttributes().getAgilityAttributes().getCrossedObstacles().length; i++) {
				characterfile.write("walked-agility-object = ", 0, 24);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i) .length());
				characterfile.write("	", 0, 1);
				characterfile.write(Boolean.toString(player.getSkillManager().getSkillAttributes().getAgilityAttributes().getCrossedObstacle(i)), 0, Boolean.toString(player.getSkillManager().getSkillAttributes().getAgilityAttributes().getCrossedObstacle(i)).length());
				characterfile.newLine();
			}
			characterfile.write("storedRuneEss = ", 0, 16);
			characterfile.write(Integer.valueOf(player.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredRuneEssence()).toString(), 0, Integer.valueOf(player.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredRuneEssence()).toString().length());
			characterfile.newLine();
			characterfile.write("storedPureEss = ", 0, 16);
			characterfile.write(Integer.valueOf(player.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredPureEssence()).toString(), 0, Integer.valueOf(player.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredPureEssence()).toString().length());
			characterfile.newLine();
			characterfile.write("effigy-status = ", 0, 16);
			characterfile.write(Integer.valueOf(player.getAttributes().getEffigy()).toString(), 0, Integer.valueOf(player.getAttributes().getEffigy()).toString().length());
			characterfile.newLine();
			characterfile.write("process-farming = ", 0, 18);
			characterfile.write(Boolean.valueOf(player.getAttributes().shouldProcessFarming()).toString(), 0, Boolean.valueOf(player.getAttributes().shouldProcessFarming()).toString().length());
			characterfile.newLine();
			characterfile.newLine();
			characterfile.write("[MINIGAMES]", 0, 11);
			characterfile.newLine();
			for (int i = 0; i < player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData().length; i++) {
				characterfile.write("warriors-guild-index = ", 0, 23);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Boolean.toString(player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[i]), 0, Boolean.toString(player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[i]).length());
				characterfile.newLine();
			}
			characterfile.write("fight-cave-wave = ", 0, 18);
			characterfile.write(Integer.valueOf(player.getAttributes().getMinigameAttributes().getFightCaveAttributes().getWave()).toString(), 0, Integer.valueOf(player.getAttributes().getMinigameAttributes().getFightCaveAttributes().getWave()).toString().length());
			characterfile.newLine();
			for (int b = 0; b < player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData().length; b++) {
				characterfile.write("brother-info = ", 0, 15);
				characterfile.write(Integer.toString(b), 0, Integer.toString(b)	.length());
				characterfile.write("	", 0, 1);
				characterfile.write(player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[b][1] <= 1 ? Integer.toString(0)	: Integer.toString(player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[b][1]), 0,Integer.toString(player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[b][1]).length());
				characterfile.newLine();
			}
			characterfile.write("random-barrows-coffin = ", 0, 24);
			characterfile.write(Integer.valueOf(player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getRandomCoffin()).toString(), 0, Integer.valueOf(player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getRandomCoffin()).toString().length());
			characterfile.newLine();
			characterfile.write("barrows-killcount = ", 0, 20);
			characterfile.write(Integer.valueOf(player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount()).toString(), 0, Integer.valueOf(player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getKillcount()).toString().length());
			characterfile.newLine();
			characterfile.newLine();
			characterfile.write("[QUESTS]", 0, 8);
			characterfile.newLine();
			characterfile.write("completedrfdwaves = ", 0, 20);
			characterfile.write(Integer.valueOf(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted()).toString(), 0, Integer.valueOf(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted()).toString().length());
			characterfile.newLine();
			for (int i = 0; i < 9; i++) {
				characterfile.write("rfdQuest = ", 0, 11);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Boolean.toString(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(i)), 0, Boolean.toString(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(i)).length());
				characterfile.newLine();
			}
			for (int i = 0; i < 3; i++) {
				characterfile.write("nomadquest = ", 0, 13);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Boolean.toString(player.getAttributes().getMinigameAttributes().getNomadAttributes().hasFinishedPart(i)), 0, Boolean.toString(player.getAttributes().getMinigameAttributes().getNomadAttributes().hasFinishedPart(i)).length());
				characterfile.newLine();
			}
			characterfile.newLine();

			/* TIMERS */
			characterfile.write("[DELAYS]", 0, 8);
			characterfile.newLine();
			characterfile.write("new-account-timer = ", 0, 20);
			characterfile.write(Integer.valueOf(player.getAttributes().getNewPlayerDelay()).toString(), 0, Integer.valueOf(player.getAttributes().getNewPlayerDelay()).toString().length());
			characterfile.newLine();
			characterfile.write("overload-timer = ", 0, 17);
			characterfile.write(Integer.valueOf(player.getAttributes().getOverloadPotionTimer()).toString(), 0, Integer.valueOf(player.getAttributes().getOverloadPotionTimer()).toString().length());
			characterfile.newLine();
			characterfile.write("prayer-renewal-timer = ", 0, 22);
			characterfile.write(Integer.valueOf(player.getAttributes().getPrayerRenewalPotionTimer()).toString(), 0, Integer.valueOf(player.getAttributes().getPrayerRenewalPotionTimer()).toString().length());
			characterfile.newLine();
			characterfile.write("last-call-for-help = ", 0, 21);
			characterfile.write(Long.valueOf(player.getAttributes().getCallForHelpDelay()).toString(), 0, Long.valueOf(player.getAttributes().getCallForHelpDelay()).toString().length());
			characterfile.newLine();
			characterfile.write("lastyell = ", 0, 11);
			characterfile.write(Long.valueOf(player.getAttributes().getLastYell()).toString(), 0, Long.valueOf(player.getAttributes().getLastYell()).toString().length());
			characterfile.newLine();
			characterfile.write("lastvote = ", 0, 11);
			characterfile.write(Long.valueOf(player.getAttributes().getLastVote()).toString(), 0, Long.valueOf(player.getAttributes().getLastVote()).toString().length());
			characterfile.newLine();
			for (int i = 0; i < player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getBoundItems().length; i++) {
				if(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getBoundItems()[i] <= 0)
					continue;
				characterfile.write("item-bind = ", 0, 12);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getBoundItems()[i]), 0, Integer.toString(player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getBoundItems()[i]).length());
				characterfile.newLine();
			}
			for (int i = 0; i < player.getEquipment().getItemDegradationCharges().length; i++) {
				if(player.getEquipment().getItemDegradationCharges()[i] <= 0)
					continue;
				characterfile.write("item-degrade = ", 0, 15);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Integer.toString(player.getEquipment().getItemDegradationCharges()[i]), 0, Integer.toString(player.getEquipment().getItemDegradationCharges()[i]).length());
				characterfile.newLine();
			}
			characterfile.newLine();

			/* LENDING */
			characterfile.write("[LENDING]", 0, 9);
			characterfile.newLine();
			int itemId = player.getTrading().getItemLending().getBorrowedItem() != null ? player.getTrading().getItemLending().getBorrowedItem().getId() : -1;
			String itemOwner = player.getTrading().getItemLending().getBorrowedItem() != null ? player.getTrading().getItemLending().getBorrowedItem().getItemOwner() : "";
			String itemLoaner = player.getTrading().getItemLending().getBorrowedItem() != null ? player.getTrading().getItemLending().getBorrowedItem().getItemLoaner() : "";
			long start = player.getTrading().getItemLending().getBorrowedItem() != null ? player.getTrading().getItemLending().getBorrowedItem().getStartMilliS() : -1;
			long end = player.getTrading().getItemLending().getBorrowedItem() != null ? player.getTrading().getItemLending().getBorrowedItem().getReturnMilliS() : -1;
			characterfile.write("lendItemId = ", 0, 13);
			characterfile.write(Integer.valueOf(itemId).toString(), 0, Integer.valueOf(itemId).toString().length());
			characterfile.newLine();
			characterfile.write("itemowner = ", 0, 12);
			characterfile.write(itemOwner, 0, itemOwner.length());
			characterfile.newLine();
			characterfile.write("itemloaner = ", 0, 12);
			characterfile.write(itemLoaner, 0, itemLoaner.length());
			characterfile.newLine();
			characterfile.write("starttimer = ", 0, 13);
			characterfile.write(Long.valueOf(start).toString(), 0, Long.valueOf(start).toString().length());
			characterfile.newLine();
			characterfile.write("endtimer = ", 0, 11);
			characterfile.write(Long.valueOf(end).toString(), 0, Long.valueOf(end).toString().length());
			characterfile.newLine();
			characterfile.newLine();

			/* BORROWING */
			characterfile.write("[BORROWING]", 0, 11);
			characterfile.newLine();
			itemId = player.getTrading().getItemLending().getLentItem() != null ? player.getTrading().getItemLending().getLentItem().getId() : -1;
			itemOwner = player.getTrading().getItemLending().getLentItem() != null ? player.getTrading().getItemLending().getLentItem().getItemOwner() : "";
			itemLoaner = player.getTrading().getItemLending().getLentItem() != null ? player.getTrading().getItemLending().getLentItem().getItemLoaner() : "";
			start = player.getTrading().getItemLending().getLentItem() != null ? player.getTrading().getItemLending().getLentItem().getStartMilliS() : -1;
			end = player.getTrading().getItemLending().getLentItem() != null ? player.getTrading().getItemLending().getLentItem().getReturnMilliS() : -1;
			characterfile.write("borrowItemId = ", 0, 15);
			characterfile.write(Integer.valueOf(itemId).toString(), 0, Integer.valueOf(itemId).toString().length());
			characterfile.newLine();
			characterfile.write("itemowner = ", 0, 12);
			characterfile.write(itemOwner, 0, itemOwner.length());
			characterfile.newLine();
			characterfile.write("itemloaner = ", 0, 12);
			characterfile.write(itemLoaner, 0, itemLoaner.length());
			characterfile.newLine();
			characterfile.write("starttimer = ", 0, 13);
			characterfile.write(Long.valueOf(start).toString(), 0, Long.valueOf(start).toString().length());
			characterfile.newLine();
			characterfile.write("endtimer = ", 0, 11);
			characterfile.write(Long.valueOf(end).toString(), 0, Long.valueOf(end).toString().length());
			characterfile.newLine();
			characterfile.newLine();

			/* FRIENDS */
			characterfile.write("[FRIENDS]", 0, 9);
			characterfile.newLine();
			for (int i = 0; i < 200; i++) {
				if(i < player.getRelations().getFriendList().size()) {
					characterfile.write("character-friend = ", 0, 19);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write("" + player.getRelations().getFriendList().get(i));
					characterfile.newLine();
				}
			}
			characterfile.newLine();

			/* IGNORES */
			characterfile.write("[IGNORES]", 0, 9);
			characterfile.newLine();
			for (int i = 0; i < 200; i++) {
				if(i < player.getRelations().getIgnoreList().size()) {
					characterfile.write("character-ignore = ", 0, 19);
					characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
					characterfile.write("	", 0, 1);
					characterfile.write("" + player.getRelations().getIgnoreList().get(i));
					characterfile.newLine();
				}
			}
			characterfile.newLine();

			characterfile.write("[CLAN]", 0, 6);
			characterfile.newLine();
			characterfile.write("has-clan = ", 0, 11);
			characterfile.write(Boolean.valueOf(player.getAttributes().hasClan()).toString(), 0, Boolean.valueOf(player.getAttributes().hasClan()).toString().length());
			characterfile.newLine();
			characterfile.write("clan-chat = ", 0, 12);
			characterfile.write(player.getAttributes().getClanChatName() != null ? player.getAttributes().getClanChatName() : "null", 0, (player.getAttributes().getClanChatName() != null ? player.getAttributes().getClanChatName() : "null").length());
			characterfile.newLine();
			characterfile.write("clan-chat-color = ", 0, 18);
			characterfile.write(Integer.valueOf(player.getAttributes().getClanChatMessageColor().ordinal()).toString(), 0, Integer.valueOf(player.getAttributes().getClanChatMessageColor().ordinal()).toString().length());
			characterfile.newLine();
			characterfile.write("clan-chat-rgbindex = ", 0, 21);
			characterfile.write(Integer.valueOf(player.getAttributes().getClanChatMessageColor().getRgbIndex()).toString(), 0, Integer.valueOf(player.getAttributes().getClanChatMessageColor().getRgbIndex()).toString().length());
			characterfile.newLine();
			characterfile.newLine();

			/* ACHIEVEMENTS */
			characterfile.write("[ACHIEVEMENTS]", 0, 14);
			characterfile.newLine();
			for (int i = 0; i < player.getAttributes().getAchievements().length; i++) {
				characterfile.write("achievement = ", 0, 14);
				characterfile.write(Integer.toString(i), 0, Integer.toString(i).length());
				characterfile.write("	", 0, 1);
				characterfile.write(Boolean.toString(player.getAttributes().getAchievements()[i]), 0, Boolean.toString(player.getAttributes().getAchievements()[i]).length());
				characterfile.newLine();
			}
			characterfile.newLine();

			/* CLIENT */
			characterfile.write("[CLIENT]", 0, 8);
			characterfile.newLine();
			characterfile.write("accepting-aid = ", 0, 16);
			characterfile.write(Boolean.valueOf(player.getAttributes().isAcceptingAid()).toString(), 0, Boolean.valueOf(player.getAttributes().isAcceptingAid()).toString().length());
			characterfile.newLine();
			characterfile.write("sound-volume = ", 0, 15);
			characterfile.write(Integer.valueOf(player.getAttributes().getVolume()).toString(), 0, Integer.valueOf(player.getAttributes().getVolume()).toString().length());
			characterfile.newLine();
			characterfile.write("online-status = ", 0, 16);
			characterfile.write(Integer.valueOf(player.getRelations().getStatus().ordinal()).toString(), 0, Integer.valueOf(player.getRelations().getStatus().ordinal()).toString().length());
			characterfile.newLine();
			characterfile.newLine();

			/* MISC */
			characterfile.write("[MISC]", 0, 6);
			characterfile.newLine();
			//Reset positions
			int resetX = player.getAttributes().getResetPosition() != null ? player.getAttributes().getResetPosition().getX() : -1;
			int resetY = player.getAttributes().getResetPosition() != null ? player.getAttributes().getResetPosition().getY() : -1;
			int resetZ = player.getAttributes().getResetPosition() != null ? player.getAttributes().getResetPosition().getZ() : -1;
			characterfile.write("resetposX = ", 0, 12);
			characterfile.write(Integer.valueOf(resetX).toString(), 0, Integer.valueOf(resetX).toString().length());
			characterfile.newLine();
			characterfile.write("resetposY = ", 0, 12);
			characterfile.write(Integer.valueOf(resetY).toString(), 0, Integer.valueOf(resetY).toString().length());
			characterfile.newLine();
			characterfile.write("resetposZ = ", 0, 12);
			characterfile.write(Integer.valueOf(resetZ).toString(), 0, Integer.valueOf(resetZ).toString().length());
			characterfile.newLine();
			characterfile.newLine();


			characterfile.write("[EOF]", 0, 5);
			characterfile.newLine();
			characterfile.newLine();
			characterfile.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Loads and checks if a player has a saved file located in {@code DIRECTORY}.
	 * @param player	The player to load file for.
	 * @return			File successfully loaded.
	 */
	public static int load(Player player) {
		String line = "";
		String token = "";
		String token2 = "";
		String[] token3 = new String[3];
		boolean EndOfFile = false;
		int ReadMode = 0;
		BufferedReader characterfile = null;
		try {
			if (playerExists(Misc.removeSpaces(player.getUsername())) && !playerExists(player.getUsername())) {
				return LoginResponses.LOGIN_INVALID_CREDENTIALS;
			}
			characterfile = new BufferedReader(new FileReader(DIRECTORY + player.getUsername().toLowerCase()+".txt"));
		} catch (FileNotFoundException fileex1) {

		}

		if (characterfile == null) {
			return LoginResponses.LOGIN_SUCCESSFUL;
		}
		try {
			line = characterfile.readLine();
		} catch (IOException ioexception) {
			System.out.println(player.getUsername() + ": Error loading file!");
			return LoginResponses.LOGIN_SUCCESSFUL;
		}
		while (EndOfFile == false && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token3 = token2.split("\t");
				switch (ReadMode) {
				case ACCOUNT_INFO:
					if (token.equals("character-password")) {
						String password = token2;
						if (!player.getPassword().equals(password))
							return LoginResponses.LOGIN_INVALID_CREDENTIALS;
					} /*else if (token.equals("connected-from"))
						player.hostAdress = token2;*/
					else if(token.equals("email-adress")) {
						player.setEmailAdress(token2.equals("n") ? null : token2);
					}
					break;
				case CHARACTER_FIELDS:
					if (token.equals("difficulty"))
						player.setDifficulty(Difficulty.forId(Integer.parseInt(token2)));
					else if (token.equals("positionX"))
						player.getPosition().setX(Integer.parseInt(token2));
					else if (token.equals("positionY"))
						player.getPosition().setY(Integer.parseInt(token2));
					else if (token.equals("positionZ"))
						player.getPosition().setZ(Integer.parseInt(token2));
					else if (token.equals("rights"))
						player.setRights(PlayerRights.forId(Integer.parseInt(token2)));
					else if (token.equals("title"))
						player.getAttributes().setLoyaltyTitle(Integer.parseInt(token2));
					else if (token.equals("running"))
						player.getAttributes().setRunning(Boolean.parseBoolean(token2));
					else if (token.equals("run-energy"))
						player.getAttributes().setRunEnergy(Integer.parseInt(token2));
					else if (token.equals("got-starter"))
						player.getAttributes().setStarted(Boolean.parseBoolean(token2));
					else if (token.contains("finished-tut-")) {
						int tutIndex = Integer.parseInt(token.substring(token.length() - 1, token.length()));
						player.getAttributes().getTutorialFinished()[tutIndex] = Boolean.parseBoolean(token2);
					}
					else if (token.equals("experience-locked"))
						player.getAttributes().setExperienceLocked(Boolean.parseBoolean(token2));
					else if (token.equals("spell-book"))
						player.getAttributes().setSpellbook(MagicSpellbook.forId(Integer.parseInt(token2)));
					else if (token.equals("prayer-book"))
						player.getAttributes().setPrayerbook(Prayerbook.forId(Integer.parseInt(token2)));
					else if (token.equals("achievement-points"))
						player.getPointsHandler().setAchievementPoints(Integer.parseInt(token2), false);
					else if (token.equals("dung-tokens"))
						player.getPointsHandler().setDungeoneeringTokens(false, Integer.parseInt(token2));
					else if (token.equals("conquest-points"))
						player.getPointsHandler().setConquestPoints(Integer.parseInt(token2), false);
					else if (token.equals("loyalty-points"))
						player.getPointsHandler().setLoyaltyProgrammePoints(Integer.parseInt(token2), false);
					else if (token.equals("commendations"))
						player.getPointsHandler().setCommendations(Integer.parseInt(token2), false);
					else if (token.equals("donator-points"))
						player.getPointsHandler().setDonatorPoints(Integer.parseInt(token2), false);
					else if (token.equals("slayer-points"))
						player.getPointsHandler().setSlayerPoints(Integer.parseInt(token2), false);
					else if (token.equals("player-killing-points"))
						player.getPointsHandler().setPkPoints(Integer.parseInt(token2), false);
					else if (token.equals("zeals"))
						player.getPointsHandler().setZeals(Integer.parseInt(token2), false);
					else if (token.equals("wildy-kills"))
						player.getPlayerCombatAttributes().setKills(Integer.parseInt(token2));
					else if (token.equals("wildy-deaths"))
						player.getPlayerCombatAttributes().setDeaths(Integer.parseInt(token2));
					else if (token.equals("arena-victories"))
						player.getDueling().arenaStats[1] = Integer.parseInt(token2);
					else if (token.equals("arena-losses"))
						player.getDueling().arenaStats[0] = Integer.parseInt(token2);
					break;

				case COMBAT_FIELDS:
					if(token.equals("weapon-attack-style"))
						player.getPlayerCombatAttributes().setAttackStyle(AttackStyle.forId(Integer.parseInt(token2)));
					else if(token.equals("special-attack-energy"))
						player.getPlayerCombatAttributes().setSpecialAttackAmount(Double.parseDouble(token2));
					else if(token.equals("auto-retaliating")) 
						player.getCombatAttributes().setAutoRetaliation(Boolean.parseBoolean(token2));
					else if(token.equals("dfs-charges"))
						player.getAttributes().setDragonFireCharges(Integer.parseInt(token2), true);
					else if(token.equals("last-veng")) 
						player.getPlayerCombatAttributes().setLastVengeanceCast(Long.parseLong(token2));
					else if(token.equals("last-aid")) 
						player.getCombatAttributes().lastAid = Long.parseLong(token2);
					else if(token.equals("target-percentage")) 
						player.getPlayerCombatAttributes().getBountyHunterAttributes().setTargetPercentage(Integer.parseInt(token2));
					else if(token.equals("target-percentage-timer")) 
						player.getPlayerCombatAttributes().getBountyHunterAttributes().setLastTargetPercentageIncrease(Long.parseLong(token2));
					else if(token.equals("entered-gwd-boss"))
						player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().setHasEnteredRoom(Boolean.parseBoolean(token2));
					else if(token.equals("gwd-altar-timer"))
						player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().setAltarDelay(Long.parseLong(token2));
					else if(token.equals("poison"))
						player.getCombatAttributes().setCurrentPoisonDamage(Integer.parseInt(token2));
					else if (token.contains("gwd-killcount-")) {
						int godIndex = Integer.parseInt(token.substring(token.length() - 1, token.length()));
						if(godIndex < 4)
							player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[godIndex] = Integer.parseInt(token2);
					}
					else if (token.equals("killed-players")) {
						List<String> players = new ArrayList<String>();
						for (String s : token3) {
							players.add(s);
						}
						player.getPlayerCombatAttributes().getPkRewardSystem().setKilledPlayers(players);
					}
					break;

				case APPEARANCE_VALUES:
					if (token.equals("character-look"))
						player.getAppearance().set(Integer.parseInt(token3[0]), Integer.parseInt(token3[1]));
					break;
				case MONEY_POUCH_VALUE:
					if(token.equals("money-in-pouch"))
						player.getAttributes().setMoneyInPouch(Integer.parseInt(token2));
					break;

				case INVENTORY_VALUES:
					if (token.equals("character-item")) {
						int slot = Integer.parseInt(token3[0]);
						int itemId = Integer.parseInt(token3[1]);
						int itemAmount = Integer.parseInt(token3[2]);
						if (itemId != 65535) {
							Item item = new Item(itemId, itemAmount);
							player.getInventory().setItem(slot, item);
						}
					}
					break;

				case EQUIPMENT_VALUES:
					if (token.equals("character-equip")) {
						int slot = Integer.parseInt(token3[0]);
						int itemId = Integer.parseInt(token3[1]);
						int itemAmount = Integer.parseInt(token3[2]);
						player.getEquipment().setItem(slot, new Item(itemId, itemAmount));
					}
					break;

				case BANK_VALUES:
					if (token.contains("character-bank-")) {
						int slot = Integer.parseInt(token3[0]);
						int itemId = Integer.parseInt(token3[1]);
						int amount = Integer.parseInt(token3[2]);
						int tab = Integer.parseInt(token.substring(token.length() - 1, token.length()));
						player.getBank(tab).setItem(slot, new Item(itemId, amount));
					} else if(token.equals("invalid-pin-attempts"))
						player.getAttributes().getBankPinAttributes().setInvalidAttempts(Integer.parseInt(token2));
					else if(token.equals("last-pin-attempt"))
						player.getAttributes().getBankPinAttributes().setLastAttempt(Long.parseLong(token2));
					else if(token.equals("has-pin"))
						player.getAttributes().getBankPinAttributes().setHasBankPin(Boolean.parseBoolean(token2));
					else if(token.contains("character-pin-") && player.getAttributes().getBankPinAttributes().hasBankPin()) {
						int i = Integer.parseInt(token.substring(token.length() - 1, token.length()));
						player.getAttributes().getBankPinAttributes().getBankPin()[i] = Integer.parseInt(token2);
					}
					break;

				case BEAST_OF_BURDEN_VALUES:
					if (token.equals("stored"))
						player.getAdvancedSkills().getSummoning().storedItems.add(Integer.parseInt(token3[0]), new Item(Integer.parseInt(token3[1]), 1));
					break;

				case SKILL_VALUES:
					if (token.equals("character-skill")) {
						int skillId = Integer.parseInt(token3[0]);
						int currentLevel = Integer.parseInt(token3[1]);
						int maxLevel = Integer.parseInt(token3[2]);
						int xp = Integer.parseInt(token3[3]);
						Skill skill = Skill.forId(skillId);
						player.getSkillManager().setCurrentLevel(skill, currentLevel, false).setMaxLevel(skill, maxLevel, false).setExperience(skill, xp, false);
					}
					break;

				case SKILLING_FIELDS:
					if (token.equals("ardougne-chest-timer"))
						player.getAttributes().setArdougneChestLootingDelay(Long.parseLong(token2));
					else if(token.equals("slayer-task"))
						player.getAdvancedSkills().getSlayer().setSlayerTask(SlayerTasks.forId(Integer.parseInt(token2)));
					else if(token.equals("previous-slayer-task"))
						player.getAdvancedSkills().getSlayer().setLastTask(SlayerTasks.forId(Integer.parseInt(token2)));
					else if(token.equals("slayer-task-amount"))
						player.getAdvancedSkills().getSlayer().setAmountToSlay(Integer.parseInt(token2));
					else if(token.equals("slayer-task-streak"))
						player.getAdvancedSkills().getSlayer().setTaskStreak(Integer.parseInt(token2));
					else if(token.equals("slayer-master"))
						player.getAdvancedSkills().getSlayer().setSlayerMaster(SlayerMaster.forId(Integer.parseInt(token2)));
					else if(token.equals("duo-slayer"))
						player.getAdvancedSkills().getSlayer().doingDuoSlayer = Boolean.parseBoolean(token2);
					else if(token.equals("duo-slayer-partner"))
						player.getAdvancedSkills().getSlayer().getDuoSlayer().setDuoPartner(token2);
					else if(token.equals("double-slayer-xp"))
						player.getAdvancedSkills().getSlayer().doubleSlayerXP = Boolean.parseBoolean(token2);
					else if(token.equals("summoning-npcid")) {
						int npc = Integer.parseInt(token2);
						if(npc > 0)
							player.getAdvancedSkills().getSummoning().setFamiliarSpawnTask(new FamiliarSpawnTask(player)).setFamiliarId(npc);
					} else if(token.equals("summoning-death-timer")) {
						int deathTimer = Integer.parseInt(token2);
						if(deathTimer > 0 && player.getAdvancedSkills().getSummoning().getSpawnTask() != null)
							player.getAdvancedSkills().getSummoning().getSpawnTask().setDeathTimer(deathTimer);
					} else if(token.equals("walked-agility-object"))
						player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(Integer.parseInt(token3[0]), Boolean.parseBoolean(token3[1]));
					else if(token.equals("storedRuneEss"))
						player.getSkillManager().getSkillAttributes().getRunecraftingAttributes().setStoredRuneEssence(Integer.parseInt(token2));
					else if(token.equals("storedPureEss"))
						player.getSkillManager().getSkillAttributes().getRunecraftingAttributes().setStoredPureEssence(Integer.parseInt(token2));
					else if(token.equals("effigy-status"))
						player.getAttributes().setEffigy(Integer.parseInt(token2));
					else if(token.equals("process-farming"))
						player.getAttributes().setShouldProcessFarming(Boolean.parseBoolean(token2));
					break;

				case MINIGAME_FIELDS:
					if(token.equals("warriors-guild-index"))
						player.getAttributes().getMinigameAttributes().getWarriorsGuildAttributes().getData()[Integer.parseInt(token3[0])] = Boolean.parseBoolean(token3[1]);
					else if(token.equals("fight-cave-wave"))
						player.getAttributes().getMinigameAttributes().getFightCaveAttributes().setWave(Integer.parseInt(token2));
					else if(token.equals("brother-info"))
						player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().getBarrowsData()[Integer.parseInt(token3[0])][1] = Integer.parseInt(token3[1]);
					else if(token.equals("random-barrows-coffin"))
						player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().setRandomCoffin(Integer.parseInt(token2));
					else if(token.equals("barrows-killcount"))
						player.getAttributes().getMinigameAttributes().getBarrowsMinigameAttributes().setKillcount(Integer.parseInt(token2));
					break;

				case QUEST_FIELDS:
					if(token.equals("completedrfdwaves"))
						player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().setWavesCompleted(Integer.parseInt(token2));
					else if(token.equals("rfdQuest"))
						player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().setPartFinished(Integer.parseInt(token3[0]), Boolean.parseBoolean(token3[1]));
					else if(token.equals("nomadquest"))
						player.getAttributes().getMinigameAttributes().getNomadAttributes().setPartFinished(Integer.parseInt(token3[0]), Boolean.parseBoolean(token3[1]));
					break;

				case DELAY_FIELDS:
					if (token.equals("new-account-timer"))
						player.getAttributes().setNewPlayerDelay(Integer.parseInt(token2));
					else if(token.equals("overload-timer"))
						player.getAttributes().setOverloadPotionTimer(Integer.parseInt(token2));
					else if(token.equals("prayer-renewal-timer"))
						player.getAttributes().setPrayerRenewalPotionTimer(Integer.parseInt(token2));
					else if(token.equals("last-call-for-help"))
						player.getAttributes().setCallForHelpDelay(Long.parseLong(token2));
					else if(token.equals("lastyell"))
						player.getAttributes().setLastYell(Long.parseLong(token2));
					else if(token.equals("lastvote"))
						player.getAttributes().setLastVote(Long.parseLong(token2));
					else if(token.equals("item-degrade"))
						player.getEquipment().getItemDegradationCharges()[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
					else if(token.equals("item-bind"))
						player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getBoundItems()[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
					break;

				case ITEM_LENDING_READMODE:
					if(token.equals("lendItemId")) {
						int item = Integer.parseInt(token2);
						if(item > 0)
							player.getTrading().getItemLending().setLentItem(new LendedItem(item));
					} else if(token.equals("itemowner")) {
						String itemOwner = token2;
						if(player.getTrading().getItemLending().getLentItem() != null)
							player.getTrading().getItemLending().getLentItem().setItemOwner(itemOwner);
					} else if(token.equals("itemloaner")) {
						String itemLoaner = token2;
						if(player.getTrading().getItemLending().getLentItem() != null)
							player.getTrading().getItemLending().getLentItem().setItemLoaner(itemLoaner);
					} else if(token.equals("starttimer")) {
						long startTimer = Long.parseLong(token2);
						if(player.getTrading().getItemLending().getLentItem() != null)
							player.getTrading().getItemLending().getLentItem().setStartMilliS(startTimer);
					} else if(token.equals("endtimer")) {
						long endTimer = Long.parseLong(token2);
						if(player.getTrading().getItemLending().getLentItem() != null)
							player.getTrading().getItemLending().getLentItem().setReturnMilliS(endTimer);
					}
					break;

				case BORROW_ITEM_READMODE:
					if(token.equals("borrowItemId")) {
						int item = Integer.parseInt(token2);
						if(item > 0)
							player.getTrading().getItemLending().setBorrowedItem(new LendedItem(item));
					} else if(token.equals("itemowner")) {
						String itemOwner = token2;
						if(player.getTrading().getItemLending().getBorrowedItem() != null)
							player.getTrading().getItemLending().getBorrowedItem().setItemOwner(itemOwner);
					} else if(token.equals("itemloaner")) {
						String itemLoaner = token2;
						if(player.getTrading().getItemLending().getBorrowedItem() != null)
							player.getTrading().getItemLending().getBorrowedItem().setItemLoaner(itemLoaner);
					} else if(token.equals("starttimer")) {
						long startTimer = Long.parseLong(token2);
						if(player.getTrading().getItemLending().getBorrowedItem() != null)
							player.getTrading().getItemLending().getBorrowedItem().setStartMilliS(startTimer);
					} else if(token.equals("endtimer")) {
						long endTimer = Long.parseLong(token2);
						if(player.getTrading().getItemLending().getBorrowedItem() != null)
							player.getTrading().getItemLending().getBorrowedItem().setReturnMilliS(endTimer);
					}
					break;

				case FRIENDS_READMODE:
					if(token.equals("character-friend")) {
						player.getRelations().getFriendList().add(Long.parseLong(token3[1]));
					}
					break;

				case IGNORED_PLAYERS_READMODE:
					if(token.equals("character-ignore")) {
						player.getRelations().getIgnoreList().add(Long.parseLong(token3[1]));
					}
					break;

				case ACHIEVEMENT_FIELDS:
					if(token.equals("achievement"))
						player.getAttributes().getAchievements()[Integer.parseInt(token3[0])] = Boolean.parseBoolean(token3[1]);
					break;

				case CLIENT_FIELDS:
					if(token.equals("accepting-aid"))
						player.getAttributes().setAcceptingAid(Boolean.parseBoolean(token2));
					else if(token.equals("sound-volume"))
						player.getAttributes().setVolume(Integer.parseInt(token2));
					else if(token.equals("online-status"))
						player.getRelations().setStatus(PrivateChatStatus.forIndex(Integer.parseInt(token2)), false);
					break;

				case CLAN_CHAT_FIELDS:
					if(token.equals("has-clan"))
						player.getAttributes().setClan(Boolean.parseBoolean(token2));
					else if(token.equals("clan-chat")) {
						player.getAttributes().setClanChatName(token2);
					} else if(token.equals("clan-chat-color"))
						player.getAttributes().setClanChatMessageColor(ClanChatMessageColor.forId(Integer.parseInt(token2)));
					else if(token.equals("clan-chat-rgbindex"))
						player.getAttributes().getClanChatMessageColor().setRgbIndex(Integer.parseInt(token2));;
						break;

				case MISC_FIELDS:
					if(token.equals("resetposX")) {
						int resetX = Integer.parseInt(token2);
						if(resetX > 0)
							player.getAttributes().setResetPosition(new Position(resetX, 1));
					} else if(token.equals("resetposY")) {
						int resetY = Integer.parseInt(token2);
						if(resetY > 0 && player.getAttributes().getResetPosition() != null)
							player.getAttributes().getResetPosition().setY(resetY);
					} else if(token.equals("resetposZ")) {
						int resetZ = Integer.parseInt(token2);
						if(resetZ > 0 && player.getAttributes().getResetPosition() != null)
							player.getAttributes().getResetPosition().setZ(resetZ);
					}
					break;
				}
			} else {
				if (line.equals("[ACCOUNT]"))
					ReadMode = ACCOUNT_INFO;
				else if (line.equals("[CHARACTER]"))
					ReadMode = CHARACTER_FIELDS;
				else if (line.equals("[COMBAT]"))
					ReadMode = COMBAT_FIELDS;
				else if (line.equals("[APPEARANCE]"))
					ReadMode = APPEARANCE_VALUES;
				else if (line.equals("[MONEY POUCH]"))
					ReadMode = MONEY_POUCH_VALUE;
				else if (line.equals("[INVENTORY]"))
					ReadMode = INVENTORY_VALUES;
				else if (line.equals("[EQUIPMENT]"))
					ReadMode = EQUIPMENT_VALUES;
				else if (line.equals("[BANK]")) {
					if(player.getBank(0) == null)
						for(int i = 0; i < 9; i++)
							player.setBank(i, new Bank(player));
					ReadMode = BANK_VALUES;
				} else if (line.equals("[STORED]"))
					ReadMode = BEAST_OF_BURDEN_VALUES;
				else if (line.equals("[STATS]"))
					ReadMode = SKILL_VALUES;
				else if (line.equals("[SKILLING]"))
					ReadMode = SKILLING_FIELDS;
				else if(line.equals("[MINIGAMES]"))
					ReadMode = MINIGAME_FIELDS;
				else if(line.equals("[QUESTS]"))
					ReadMode = QUEST_FIELDS;
				else if(line.equals("[DELAYS]"))
					ReadMode = DELAY_FIELDS;
				else if(line.equals("[LENDING]")) 
					ReadMode = ITEM_LENDING_READMODE;
				else if(line.equals("[BORROWING]"))
					ReadMode = BORROW_ITEM_READMODE;
				else if(line.equals("[FRIENDS]"))
					ReadMode = FRIENDS_READMODE;
				else if(line.equals("[IGNORES]"))
					ReadMode = IGNORED_PLAYERS_READMODE;
				else if(line.equals("[ACHIEVEMENTS]"))
					ReadMode = ACHIEVEMENT_FIELDS;
				else if(line.equals("[CLIENT]"))
					ReadMode = CLIENT_FIELDS;
				else if(line.equals("[CLAN]"))
					ReadMode = CLAN_CHAT_FIELDS;
				else if(line.equals("[MISC]"))
					ReadMode = MISC_FIELDS;
				else if (line.equals("[EOF]")) {
					try {
						characterfile.close();
					} catch (IOException ioexception) {
						ioexception.printStackTrace();
					}
					return LoginResponses.LOGIN_SUCCESSFUL;
				}
			}
			try {
				line = characterfile.readLine();
			} catch (IOException ioexception1) {
				EndOfFile = true;
				ioexception1.printStackTrace();
			}
		}
		try {
			characterfile.close();
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
		return LoginResponses.LOGIN_COULD_NOT_COMPLETE;

	}

	public static boolean playerExists(String name) {
		return new File(DIRECTORY + name +".txt").exists() || new File(DIRECTORY + name.toLowerCase() +".txt").exists();
	}

	private static final int ACCOUNT_INFO = 1, CHARACTER_FIELDS = 2, COMBAT_FIELDS = 3, APPEARANCE_VALUES = 4, MONEY_POUCH_VALUE = 5, INVENTORY_VALUES = 6, EQUIPMENT_VALUES = 7, BANK_VALUES = 8, BEAST_OF_BURDEN_VALUES = 9, SKILL_VALUES = 10, SKILLING_FIELDS = 11, MINIGAME_FIELDS = 12, QUEST_FIELDS = 13, DELAY_FIELDS = 14, ITEM_LENDING_READMODE = 15, BORROW_ITEM_READMODE = 16, ACHIEVEMENT_FIELDS = 17, CLIENT_FIELDS = 18, FRIENDS_READMODE = 19, IGNORED_PLAYERS_READMODE = 20, CLAN_CHAT_FIELDS = 21, MISC_FIELDS = 22;
}
