package org.trident.world.content.combat.combatdata.melee;

import org.trident.model.Skill;
import org.trident.world.content.combat.weapons.WeaponHandler.ExperienceStyle;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.entity.player.Player;

/*
 * Messy class with data
 */
public class MeleeData {

	public static int bestMeleeDef(Player p) {
		if(p.getAttributes().getBonusManager().getDefenceBonus()[0] > p.getAttributes().getBonusManager().getDefenceBonus()[1] && p.getAttributes().getBonusManager().getDefenceBonus()[0] > p.getAttributes().getBonusManager().getDefenceBonus()[2]) {
			return 0;
		}
		if(p.getAttributes().getBonusManager().getDefenceBonus()[1] > p.getAttributes().getBonusManager().getDefenceBonus()[0] && p.getAttributes().getBonusManager().getDefenceBonus()[1] > p.getAttributes().getBonusManager().getDefenceBonus()[2]) {
			return 1;
		}
		return p.getAttributes().getBonusManager().getDefenceBonus()[2] <= p.getAttributes().getBonusManager().getDefenceBonus()[0] || p.getAttributes().getBonusManager().getDefenceBonus()[2] <= p.getAttributes().getBonusManager().getDefenceBonus()[1] ? 0 : 2;
	}

	public static int bestMeleeAtk(Player p) {
		if(p.getAttributes().getBonusManager().getAttackBonus()[0] > p.getAttributes().getBonusManager().getAttackBonus()[1] && p.getAttributes().getBonusManager().getAttackBonus()[0] > p.getAttributes().getBonusManager().getAttackBonus()[2]) {
			return 0;
		}
		if(p.getAttributes().getBonusManager().getAttackBonus()[1] > p.getAttributes().getBonusManager().getAttackBonus()[0] && p.getAttributes().getBonusManager().getAttackBonus()[1] > p.getAttributes().getBonusManager().getAttackBonus()[2]) {
			return 1;
		}
		return p.getAttributes().getBonusManager().getAttackBonus()[2] <= p.getAttributes().getBonusManager().getAttackBonus()[1] || p.getAttributes().getBonusManager().getAttackBonus()[2] <= p.getAttributes().getBonusManager().getAttackBonus()[0] ? 0 : 2;
	}

	/**
	 * Obsidian items
	 */

	public static final int[] obsidianWeapons = {
		746, 747, 6523, 6525, 6526, 6527, 6528
	};

	public static boolean hasObsidianEffect(Player plr) {		
		if (plr.getEquipment().getItems()[2].getId() != 11128) 
			return false;

		for (int weapon : obsidianWeapons) {
			if (plr.getEquipment().getItems()[3].getId() == weapon) 
				return true;
		}
		return false;
	}

	public static int getStyleBonus(Player plr) {
		if(plr.getCombatAttributes().getAttackStyle().getExperienceReward() == ExperienceStyle.STRENGTH)
			return 1;
		return 0;
	}

	public static double getEffectiveStr(Player plr) {
		return ((plr.getSkillManager().getCurrentLevel(Skill.STRENGTH)) * getPrayerStr(plr)) + getStyleBonus(plr);		
	}

	public static double getPrayerStr(Player plr) {
		if (plr.getPrayerActive()[1])
			return 1.05;
		else if (plr.getPrayerActive()[6]) 
			return 1.1;
		else if (plr.getPrayerActive()[14]) 
			return 1.15;
		else if (plr.getPrayerActive()[24]) 
			return 1.18;
		else if (plr.getPrayerActive()[25]) 
			return 1.23;	
		else if (plr.getCurseActive()[CurseHandler.TURMOIL]) 
			return 1.26;
		return 1;
	}
}
