package org.trident.world.content;

import org.trident.model.Item;
import org.trident.model.container.impl.Equipment;
import org.trident.model.definitions.ItemDefinition;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.entity.player.Player;

public class BonusManager {

	public static void update(Player player) {
		double[] bonuses = new double[18];
		for (Item item : player.getEquipment().getItems()) {
			ItemDefinition definition = ItemDefinition.forId(item.getId());
			for (int i = 0; i < definition.getBonus().length; i++) {
				bonuses[i] += definition.getBonus()[i];
			}
		}
		for (int i = 0; i < STRING_ID.length; i++) {
			if (i <= 4) {
				player.getAttributes().getBonusManager().attackBonus[i] = bonuses[i];
			} else if (i <= 13) {
				int index = i - 5;
				player.getAttributes().getBonusManager().defenceBonus[index] = bonuses[i];
				if(player.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 11283 && !STRING_ID[i][1].contains("Magic")) {
					if(player.getAttributes().getDragonFireCharges() > 0) {
						player.getAttributes().getBonusManager().defenceBonus[index] += player.getAttributes().getDragonFireCharges();
						bonuses[i] += player.getAttributes().getDragonFireCharges();
					}
				}
			} else if (i <= 17) {
				int index = i - 14;
				player.getAttributes().getBonusManager().otherBonus[index] = bonuses[i];
			}
			player.getPacketSender().sendString(Integer.valueOf(STRING_ID[i][0]), STRING_ID[i][1] + ": " + bonuses[i]);
		}
	}

	public double[] getAttackBonus() {
		return attackBonus;
	}
	
	public double[] getDefenceBonus() {
		return defenceBonus;
	}

	public double[] getOtherBonus() {
		return otherBonus;
	}

	private double[] attackBonus = new double[5];

	private double[] defenceBonus = new double[9];

	private double[] otherBonus = new double[4];

	private static final String[][] STRING_ID = {
		{"1675", "Stab"},
		{"1676", "Slash"},
		{"1677", "Crush"},
		{"1678", "Magic"},
		{"1679", "Range"},

		{"1680", "Stab"},
		{"1681", "Slash"},
		{"1682", "Crush"},
		{"1683", "Magic"},
		{"1684", "Range"},
		{"16522", "Summoning"},
		{"16523", "Absorb Melee"},
		{"16524", "Absorb Magic"},
		{"16525", "Absorb Ranged"},

		{"1686", "Strength"},
		{"16526", "Ranged Strength"},
		{"1687", "Prayer"},
		{"16527", "Magic Damage"}
	};

	public static void sendCurseBonuses(final Player p) {
		sendAttackBonus(p);
		sendDefenceBonus(p);
		sendStrengthBonus(p);
		sendRangedBonus(p);
		sendMagicBonus(p);
	}

	public static void sendAttackBonus(Player p) {
		double boost = p.getCombatAttributes().getLeechedBonuses()[0];
		int bonus = 0;
		if(p.getCurseActive()[CurseHandler.LEECH_ATTACK]) {
			bonus = 5;
		} else if(p.getCurseActive()[CurseHandler.TURMOIL])
			bonus = 15;
		bonus += boost;
		if(bonus < -25)
			bonus = -25;
		p.getPacketSender().sendString(690, ""+getColor(bonus)+""+bonus+"%");
	}

	public static void sendRangedBonus(Player p) {
		double boost = p.getCombatAttributes().getLeechedBonuses()[4];
		int bonus = 0;
		if(p.getCurseActive()[CurseHandler.LEECH_RANGED])
			bonus = 5;
		bonus += boost;
		if(bonus < -25)
			bonus = -25;
		p.getPacketSender().sendString(693, ""+getColor(bonus)+""+bonus+"%");
	}

	public static void sendMagicBonus(Player p) {
		double boost = p.getCombatAttributes().getLeechedBonuses()[6];
		int bonus = 0;
		if(p.getCurseActive()[CurseHandler.LEECH_MAGIC])
			bonus = 5;
		bonus += boost;
		if(bonus < -25)
			bonus = -25;
		p.getPacketSender().sendString(694, ""+getColor(bonus)+""+bonus+"%");
	}

	public static void sendDefenceBonus(Player p) {
		double boost = p.getCombatAttributes().getLeechedBonuses()[1];
		int bonus = 0;		
		if(p.getCurseActive()[CurseHandler.LEECH_DEFENCE])
			bonus = 5;
		else if(p.getCurseActive()[CurseHandler.TURMOIL])
			bonus = 15;
		bonus += boost;
		if(bonus < -25)
			bonus = -25;
		p.getPacketSender().sendString(692, ""+getColor(bonus)+""+bonus+"%");
	}

	public static void sendStrengthBonus(Player p) {
		double boost = p.getCombatAttributes().getLeechedBonuses()[2];
		int bonus = 0;
		if(p.getCurseActive()[CurseHandler.LEECH_STRENGTH])
			bonus = 5;
		else if(p.getCurseActive()[CurseHandler.TURMOIL])
			bonus = 23;
		bonus += boost;
		if(bonus < -25) 
			bonus = -25;
		p.getPacketSender().sendString(691, ""+getColor(bonus)+""+bonus+"%");
	}

	public static String getColor(int i) {
		if(i > 0)
			return "@gre@+";
		if(i < 0)
			return "@red@";
		return "";
	}
}
