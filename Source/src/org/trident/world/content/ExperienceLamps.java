package org.trident.world.content;

import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.skills.SkillManager;
import org.trident.world.entity.player.Player;

public class ExperienceLamps {

	public static boolean handleLamp(Player player, int item) {
		LampData lamp = LampData.forId(item);
		if(lamp == null)
			return false;
		else {
			if(player.getAttributes().getInterfaceId() > 0)
				player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			else {
				player.getPacketSender().sendString(38006, "Choose XP type...");
				player.getPacketSender().sendInterface(38000);
				player.getAttributes().setLoyaltyProductSelected(new Object[2]);
				player.getAttributes().getLoyaltyProductSelected()[0] = lamp;
			}
		}
		return true;
	}

	public static void handleButton(Player player, int button) {
		if(button == -27451) {
			try {
				player.getPacketSender().sendInterfaceRemoval();
				player.getPacketSender().sendString(38006, "Choose XP type...");
				boolean resetingStat = player.getAttributes().getLoyaltyProductSelected()[0] == null;
				Skill skill = (Skill) player.getAttributes().getLoyaltyProductSelected()[1];
				if(resetingStat) {
					player.getSkillManager().resetSkill(skill);
				} else {
					LampData lamp = (LampData) player.getAttributes().getLoyaltyProductSelected()[0];
					if(!player.getInventory().contains(lamp.getItemId()))
						return;
					int exp = getExperienceReward(player, lamp, skill);
					player.getInventory().delete(lamp.getItemId(), 1);
					player.getSkillManager().addExperience(skill, exp, true);
					player.getPacketSender().sendMessage("You've received some experience in "+Misc.formatText(skill.toString().toLowerCase())+".");
				}
			} catch(Exception e) {}
			return;
		} else {
			Interface_Buttons interfaceButton = Interface_Buttons.forButton(button);
			if(interfaceButton == null)
				return;
			player.getAttributes().getLoyaltyProductSelected()[1] = Skill.forName(interfaceButton.toString());
			player.getPacketSender().sendString(38006, Misc.formatText(player.getAttributes().getLoyaltyProductSelected()[1].toString().toLowerCase()));
		}
	}

	enum LampData {
		NORMAL_XP_LAMP(11137),
		DRAGONKIN_LAMP(18782);

		LampData(int itemId) {
			this.itemId = itemId;
		}

		private int itemId;

		public int getItemId() {
			return this.itemId;
		}

		public static LampData forId(int id) {
			for(LampData lampData : LampData.values()) {
				if(lampData != null && lampData.getItemId() == id)
					return lampData;
			}
			return null;
		}
	}

	enum Interface_Buttons {

		ATTACK(-27529),
		MAGIC(-27526),
		MINING(-27523),
		WOODCUTTING(-27520),
		AGILITY(-27517),
		FLETCHING(-27514),
		THIEVING(-27511),
		STRENGTH(-27508),
		RANGED(-27505),
		SMITHING(-27502),
		FIREMAKING(-27499),
		HERBLORE(-27496),
		SLAYER(-27493),
		CONSTRUCTION(-27490),
		DEFENCE(-27487),
		PRAYER(-27484),
		FISHING(-27481),
		CRAFTING(-27478),
		FARMING(-27475),
		HUNTER(-27472),
		SUMMONING(-27469),
		CONSTITUTION(-27466),
		DUNGEONEERING(-27463),
		COOKING(-27460),
		RUNECRAFTING(-27457);

		Interface_Buttons(int button) {
			this.button = button;
		}

		private int button;

		public static Interface_Buttons forButton(int button) {
			for(Interface_Buttons skill : Interface_Buttons.values()) {
				if(skill != null && skill.button == button) {
					return skill;
				}
			}
			return null;
		}
	}

	public static int getExperienceReward(Player player, LampData lamp, Skill skill) {
		int base = lamp == LampData.DRAGONKIN_LAMP ? 150000 : 2000;
		int maxLvl = player.getSkillManager().getMaxLevel(skill);
		player.getSkillManager();
		if(SkillManager.isNewSkill(skill))
			maxLvl = maxLvl / 10;
		return (int) (base+10*(Math.pow(maxLvl, 2.5)));
	}

	public static boolean selectingExperienceReward(Player player) {
		return player.getAttributes().getInterfaceId() == 38000;
	}
}
