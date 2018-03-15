package org.trident.world.content.skills;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Difficulty;
import org.trident.model.Flag;
import org.trident.model.Graphic;
import org.trident.model.PlayerRights;
import org.trident.model.Skill;
import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.world.content.Achievements;
import org.trident.world.content.BonusManager;
import org.trident.world.content.BrawlingGloves;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;

/**
 * Represents a player's skills in the game, also manages
 * calculations such as combat level and total level.
 * 
 * @author relex lawl
 * @editor Gabbe
 */

public class SkillManager {

	private final SkillAttributes skillAttributes = new SkillAttributes();

	/**
	 * The Skills constructor, also sets a player's constitution
	 * and prayer level to 100 and 10 respectively.
	 * @param player	The player's who skill set is being represented.
	 */
	public SkillManager(Player player) {
		this.player = player;
		for (int i = 0; i < MAX_SKILLS; i++) {
			level[i] = maxLevel[i] = 1;
			experience[i] = 0;
		}
		level[Skill.CONSTITUTION.ordinal()] = maxLevel[Skill.CONSTITUTION.ordinal()] = 100;
		experience[Skill.CONSTITUTION.ordinal()] = 1184;
		level[Skill.PRAYER.ordinal()] = maxLevel[Skill.PRAYER.ordinal()] = 10;
	}

	/**
	 * Checks if a player has another 99 other then the skill defined.
	 * Used for rewarding T/G Skillcapes.
	 * @param skill
	 * @return
	 */
	public boolean hasAnother99(int skill) {
		for (int i = 0; i < MAX_SKILLS; i++)
			if(i != skill && getMaxLevel(i) >= ((i == 5 || i == 3) ? 990 : 99))
				return true;
		return false;
	}

	/*
	 * Easier XP
	 */
	private static final boolean EASY_MODE_ON = true;

	/**
	 * Adds experience to {@code skill} by the {@code experience} amount.
	 * @param skill			The skill to add experience to.
	 * @param experience	The amount of experience to add to the skill.
	 * @return				The Skills instance.
	 */
	public SkillManager addExperience(Skill skill, int experience, boolean ignoreModifiers) {
		if(player.getDifficulty() == null)
			return this;
		if(player.getAttributes().experienceLocked()) {
			player.getPacketSender().sendMessage("@CANTADDXP:LOCKED@");
			return this;
		}
		player.getAttributes().setUpdateHighscores(true);
		/*
		 * Modifiers (mainly bonuses)
		 */
		if(!ignoreModifiers) {
			if(EASY_MODE_ON)
				experience = (int)(experience * 2);
			if(player.getDifficulty() == Difficulty.EASY && (skill == Skill.ATTACK || skill == Skill.DEFENCE || skill == Skill.STRENGTH || skill == Skill.CONSTITUTION || skill == Skill.RANGED || skill == Skill.PRAYER || skill == Skill.MAGIC))
				experience = experience * 5;
			else if(player.getDifficulty() == Difficulty.HARD) 
				experience = experience / 3;
			if(Misc.isWeekend())
				experience *= 1.25;
			if(player.getRights() == PlayerRights.DONATOR)
				experience *= 1.2;
			else if(player.getRights() == PlayerRights.SUPER_DONATOR)
				experience *= 1.5;
			else if(player.getRights() == PlayerRights.EXTREME_DONATOR)
				experience *= 2;
			experience = BrawlingGloves.getExperienceIncrease(player, skill.ordinal(), experience);
		}
		/*
		 * If the experience in the skill is already greater or equal to
		 * {@code MAX_EXPERIENCE} then stop.
		 */
		if (this.experience[skill.ordinal()] >= MAX_EXPERIENCE)
			return this;
		/*
		 * The skill's level before adding experience.
		 */
		int startingLevel = isNewSkill(skill) ? (int) (maxLevel[skill.ordinal()]/10) : maxLevel[skill.ordinal()];
		/*
		 * Adds the experience to the skill's experience.
		 */
		this.experience[skill.ordinal()] = this.experience[skill.ordinal()] + experience > MAX_EXPERIENCE ? MAX_EXPERIENCE : this.experience[skill.ordinal()] + experience;
		/*
		 * The skill's level after adding the experience.
		 */
		int newLevel = getLevelForExperience(this.experience[skill.ordinal()]);
		/*
		 * If the starting level less than the new level, level up.
		 */
		if (newLevel > startingLevel) {
			int level = newLevel - startingLevel;
			String skillName = Misc.formatText(skill.toString().toLowerCase());
			maxLevel[skill.ordinal()] += isNewSkill(skill) ? level * 10 : level;
			/*
			 * If the skill is not constitution, prayer or summoning, then set the current level
			 * to the max level.
			 */
			if (!isNewSkill(skill) && !skill.equals(Skill.SUMMONING))
				setCurrentLevel(skill, maxLevel[skill.ordinal()]);
			//player.getPacketSender().sendFlashingSidebar(Constants.SKILLS_TAB);
			SoundEffects.sendSoundEffect(player, SoundEffects.SoundData.LEVELUP, 10, 0);
			player.getAttributes().setDialogue(null);
			if(skill != Skill.FARMING) {
				player.getPacketSender().sendString(4268, "Congratulations! You have achieved a " + skillName + " level!");
				player.getPacketSender().sendString(4269, "Well done. You are now level " + newLevel + ".");
				player.getPacketSender().sendString(358, "Click here to continue.");
				player.getPacketSender().sendChatboxInterface(skill.getUpdateStrings()[3]);
			}
			player.performGraphic(new Graphic(312));
			player.getPacketSender().sendMessage("You've just advanced " + skillName + " level! You have reached level " + newLevel);
			if(player.getSkillManager().getTotalLevel() >= 500)
				Achievements.handleAchievement(player, Achievements.Tasks.TASK27);
			if (maxLevel[skill.ordinal()] == getMaxAchievingLevel(skill)) {
				player.getPacketSender().sendMessage("Well done! You've achieved the highest possible level in this skill!");
				if(player.getDifficulty() == Difficulty.NORMAL || player.getDifficulty() == Difficulty.HARD)
					PlayerHandler.sendGlobalPlayerMessage("<shad=15536940>News: "+player.getUsername()+" has just achieved the highest possible level in "+skillName+"!");
				TaskManager.submit(new Task(2, player, true) {
					int localGFX = 1634;
					@Override
					public void execute() {
						player.performGraphic(new Graphic(localGFX));
						if (localGFX == 1637) {
							stop();
							return;
						}
						localGFX++;
						player.performGraphic(new Graphic(localGFX));
					}
				});
			} else
				TaskManager.submit(new Task(2, player, false) {
					@Override
					public void execute() {
						player.performGraphic(new Graphic(199));
						stop();
					}
				});
			player.getUpdateFlag().flag(Flag.APPEARANCE);
		}
		updateSkill(skill, experience);
		return this;
	}

	public SkillManager stopSkilling() {
		if(getSkillAttributes().getCurrentTask() != null)
			getSkillAttributes().getCurrentTask().stop();
		getSkillAttributes().setCurrentTask(null).setSelectedItem(-1);
		player.getAttributes().setInputHandling(null);
		return this;
	}

	/**
	 * Updates the skill strings, for skill tab and orb updating.
	 * @param skill	The skill who's strings to update.
	 * @return		The Skills instance.
	 */
	public SkillManager updateSkill(Skill skill) {
		int maxLevel = getMaxLevel(skill), currentLevel = getCurrentLevel(skill);
		if (isNewSkill(skill)) {
			maxLevel = (maxLevel / 10);
			currentLevel = (currentLevel / 10);
		}
		if (skill == Skill.PRAYER)
			player.getPacketSender().sendString(687, currentLevel + "/" + maxLevel);
		player.getPacketSender().sendString(skill.getUpdateStrings()[0], Integer.toString(currentLevel));
		player.getPacketSender().sendString(skill.getUpdateStrings()[1], Integer.toString(maxLevel));
		player.getPacketSender().sendString(skill.getUpdateStrings()[2], Integer.toString(getExperience(skill)));
		player.getPacketSender().sendString(31200, "" + getTotalLevel());
		player.getPacketSender().sendString(19000, "Combat level: " + getCombatLevel());
		player.getPacketSender().sendSkill(skill);
		return this;
	}

	/**
	 * Updates the skill strings, for skill tab and orb updating.
	 * @param skill	The skill who's strings to update.
	 * @param experience The amount of experience added to the skill.
	 * @return		The Skills instance.
	 */
	public SkillManager updateSkill(Skill skill, int experience) {
		int maxLevel = getMaxLevel(skill), currentLevel = getCurrentLevel(skill);
		if (isNewSkill(skill)) {
			maxLevel = (maxLevel / 10);
			currentLevel = (currentLevel / 10);
		}
		if (skill == Skill.PRAYER)
			player.getPacketSender().sendString(687, currentLevel + "/" + maxLevel);
		player.getPacketSender().sendString(skill.getUpdateStrings()[0], Integer.toString(currentLevel));
		player.getPacketSender().sendString(skill.getUpdateStrings()[1], Integer.toString(maxLevel));
		player.getPacketSender().sendString(skill.getUpdateStrings()[2], Integer.toString(getExperience(skill)));
		player.getPacketSender().sendString(31200, "" + getTotalLevel());
		player.getPacketSender().sendString(19000, "Combat level: " + player.getSkillManager().getCombatLevel());
		player.getPacketSender().sendSkill(skill, experience);
		return this;
	}

	public SkillManager resetSkill(Skill skill) {
		if(player.getEquipment().getFreeSlots() != player.getEquipment().capacity()) {
			player.getPacketSender().sendMessage("Please unequip all your items first.");
			return this;
		}
		if(player.getLocation() == Location.WILDERNESS || CombatHandler.inCombat(player)) {
			player.getPacketSender().sendMessage("You cannot do this at the moment");
			return this;
		}
		player.getInventory().delete(13663, 1);
		int lvl = skill == Skill.PRAYER ? 10 : skill == Skill.CONSTITUTION ? 100 : 1;
		setCurrentLevel(skill, lvl).setMaxLevel(skill, lvl).setExperience(skill, SkillManager.getExperienceForLevel(lvl));
		PrayerHandler.deactivateAll(player); CurseHandler.deactivateAll(player); WeaponHandler.update(player); BonusManager.update(player);
		player.getPacketSender().sendMessage("You have reset your "+skill.getPName()+" level.");
		Logger.log(player.getUsername(), "Player reseted their "+skill.getPName()+" level.");
		return this;
	}

	/**
	 * Gets the minimum experience in said level.
	 * @param level		The level to get minimum experience for.
	 * @return			The least amount of experience needed to achieve said level.
	 */
	public static int getExperienceForLevel(int level) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level) {
				return output;
			}
			output = (int)Math.floor(points / 4);
		}
		return 0;
	}

	/**
	 * Gets the level from said experience.
	 * @param experience	The experience to get level for.
	 * @return				The level you obtain when you have specified experience.
	 */
	public static int getLevelForExperience(int experience) {
		int points = 0, output = 0;
		for (int lvl = 1; lvl <= 99; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if (output >= experience) {
				return lvl;
			}
		}
		return 99;
	}

	/**
	 * Calculates the player's combat level.
	 * @return	The average of the player's combat skills.
	 */
	public int getCombatLevel() {
		final int attack = maxLevel[Skill.ATTACK.ordinal()];
		final int defence = maxLevel[Skill.DEFENCE.ordinal()];
		final int strength = maxLevel[Skill.STRENGTH.ordinal()];
		final int hp = (int) (maxLevel[Skill.CONSTITUTION.ordinal()] / 10);
		final int prayer = (int) (maxLevel[Skill.PRAYER.ordinal()] / 10);
		final int ranged = maxLevel[Skill.RANGED.ordinal()];
		final int magic = maxLevel[Skill.MAGIC.ordinal()];
		final int summoning = maxLevel[Skill.SUMMONING.ordinal()];
		int combatLevel = 3;	
		combatLevel = (int) ((defence + hp + Math.floor(prayer / 2)) * 0.2535) + 1;
		final double melee = (attack + strength) * 0.325;
		final double ranger = Math.floor(ranged * 1.5) * 0.325;
		final double mage = Math.floor(magic * 1.5) * 0.325;
		if (melee >= ranger && melee >= mage) {
			combatLevel += melee;
		} else if (ranger >= melee && ranger >= mage) {
			combatLevel += ranger;
		} else if (mage >= melee && mage >= ranger) {
			combatLevel += mage;
		}
		combatLevel += summoning * 0.125;
		if (combatLevel > 138)
			return 138;
		if (combatLevel < 3)
			return 3;
		return combatLevel;
	}

	/**
	 * Gets the player's total level.
	 * @return	The value of every skill summed up.
	 */
	public int getTotalLevel() {
		int total = 0;
		for (Skill skill : Skill.values()) {
			/*
			 * If the skill is not equal to constitution or prayer, total can 
			 * be summed up with the maxLevel.
			 */
			if (!isNewSkill(skill))
				total += maxLevel[skill.ordinal()];
			/*
			 * Other-wise add the maxLevel / 10, used for 'constitution' and prayer * 10.
			 */
			else
				total += maxLevel[skill.ordinal()] / 10;
		}
		return total;
	}

	/**
	 * Gets the player's total experience.
	 * @return	The experience value from the player's every skill summed up.
	 */
	public int getTotalExp() {
		int xp = 0;
		for (Skill skill : Skill.values())
			xp += player.getSkillManager().getExperience(skill);
		return xp;
	}

	/**
	 * Checks if the skill is a x10 skill.
	 * @param skill		The skill to check.
	 * @return			The skill is a x10 skill.
	 */
	public static boolean isNewSkill(Skill skill) {
		return skill == Skill.CONSTITUTION || skill == Skill.PRAYER;
	}

	/**
	 * Gets the max level for <code>skill</code>
	 * @param skill		The skill to get max level for.
	 * @return			The max level that can be achieved in said skill.
	 */
	public static int getMaxAchievingLevel(Skill skill) {
		int level = 99;
		if (isNewSkill(skill))
			level = 990;
		/*if (skill == Skill.DUNGEONEERING)
			level = 120;*/
		return level;
	}

	/**
	 * Gets the current level for said skill.
	 * @param skill		The skill to get current/temporary level for.
	 * @return			The skill's level.
	 */
	public int getCurrentLevel(Skill skill) {
		return level[skill.ordinal()];
	}

	/**
	 * Gets the max level for said skill.
	 * @param skill		The skill to get max level for.
	 * @return			The skill's maximum level.
	 */
	public int getMaxLevel(Skill skill) {
		return maxLevel[skill.ordinal()];
	}

	/**
	 * Gets the max level for said skill.
	 * @param skill		The skill to get max level for.
	 * @return			The skill's maximum level.
	 */
	public int getMaxLevel(int skill) {
		return maxLevel[skill];
	}

	/**
	 * Gets the experience for said skill.
	 * @param skill		The skill to get experience for.
	 * @return			The experience in said skill.
	 */
	public int getExperience(Skill skill) {
		return experience[skill.ordinal()];
	}

	/**
	 * Sets the current level of said skill.
	 * @param skill		The skill to set current/temporary level for.
	 * @param level		The level to set the skill to.
	 * @param refresh	If <code>true</code>, the skill's strings will be updated.
	 * @return			The Skills instance.
	 */
	public SkillManager setCurrentLevel(Skill skill, int level, boolean refresh) {
		this.level[skill.ordinal()] = level;
		if (refresh)
			updateSkill(skill);
		return this;
	}

	/**
	 * Sets the maximum level of said skill.
	 * @param skill		The skill to set maximum level for.
	 * @param level		The level to set skill to.
	 * @param refresh	If <code>true</code>, the skill's strings will be updated.
	 * @return			The Skills instance.
	 */
	public SkillManager setMaxLevel(Skill skill, int level, boolean refresh) {
		maxLevel[skill.ordinal()] = level;
		if (refresh)
			updateSkill(skill);
		return this;
	}

	/**
	 * Sets the experience of said skill.
	 * @param skill			The skill to set experience for.
	 * @param experience	The amount of experience to set said skill to.
	 * @param refresh		If <code>true</code>, the skill's strings will be updated.
	 * @return				The Skills instance.
	 */
	public SkillManager setExperience(Skill skill, int experience, boolean refresh) {
		this.experience[skill.ordinal()] = experience;
		if (refresh)
			updateSkill(skill);
		return this;
	}

	/**
	 * Sets the current level of said skill.
	 * @param skill		The skill to set current/temporary level for.
	 * @param level		The level to set the skill to.
	 * @return			The Skills instance.
	 */
	public SkillManager setCurrentLevel(Skill skill, int level) {
		setCurrentLevel(skill, level, true);
		return this;
	}

	/**
	 * Sets the maximum level of said skill.
	 * @param skill		The skill to set maximum level for.
	 * @param level		The level to set skill to.
	 * @return			The Skills instance.
	 */
	public SkillManager setMaxLevel(Skill skill, int level) {
		setMaxLevel(skill, level, true);
		return this;
	}

	/**
	 * Sets the experience of said skill.
	 * @param skill			The skill to set experience for.
	 * @param experience	The amount of experience to set said skill to.
	 * @return				The Skills instance.
	 */
	public SkillManager setExperience(Skill skill, int experience) {
		setExperience(skill, experience, true);
		return this;
	}

	public SkillAttributes getSkillAttributes() {
		return skillAttributes;
	}

	/**
	 * The player associated with this Skills instance.
	 */
	private Player player;

	/**
	 * The current/temporary levels of the player's skills.
	 */
	private int[] level = new int[MAX_SKILLS];

	/**
	 * The maximum levels of the player's skills.
	 */
	private int[] maxLevel = new int[MAX_SKILLS];

	/**
	 * The experience of the player's skills.
	 */
	private int[] experience = new int[MAX_SKILLS];

	/**
	 * The maximum amount of skills in the game.
	 */
	public static final int MAX_SKILLS = 25;

	/**
	 * The maximum amount of experience you can
	 * achieve in a skill.
	 */
	public static final int MAX_EXPERIENCE = 200000000;
}