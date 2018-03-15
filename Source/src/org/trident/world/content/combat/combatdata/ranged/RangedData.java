package org.trident.world.content.combat.combatdata.ranged;
import org.trident.model.Item;
import org.trident.model.container.impl.Equipment;
import org.trident.util.Misc;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.minigames.impl.Dueling;
import org.trident.world.entity.player.Player;

/**
 * Holds data used for the Ranged attack type.
 * @author Gabbe
 */
public class RangedData {

	//TODO: Add poisonous ammo
	public enum RangedWeaponData {
		
		LONGBOW(new int[] {839}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW}, Type.LONGBOW),
		SHORTBOW(new int[] {841}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW}, Type.SHORTBOW),
		OAK_LONGBOW(new int[] {845}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW}, Type.LONGBOW),
		OAK_SHORTBOW(new int[] {843}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW}, Type.SHORTBOW),
		WILLOW_LONGBOW(new int[] {847}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW}, Type.LONGBOW),
		WILLOW_SHORTBOW(new int[] {849}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW}, Type.SHORTBOW),
		MAPLE_LONGBOW(new int[] {851}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW}, Type.LONGBOW),
		MAPLE_SHORTBOW(new int[] {853}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW}, Type.SHORTBOW),
		YEW_LONGBOW(new int[] {855}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW, AmmunitionData.RUNE_ARROW}, Type.LONGBOW),
		YEW_SHORTBOW(new int[] {857}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW, AmmunitionData.RUNE_ARROW}, Type.SHORTBOW),
		MAGIC_LONGBOW(new int[] {859}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW, AmmunitionData.RUNE_ARROW}, Type.LONGBOW),
		MAGIC_SHORTBOW(new int[] {861}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW, AmmunitionData.RUNE_ARROW}, Type.SHORTBOW),
		DARK_BOW(new int[] {11235, 13405, 15701, 15702, 15703, 15704}, new AmmunitionData[] {AmmunitionData.BRONZE_ARROW, AmmunitionData.IRON_ARROW, AmmunitionData.STEEL_ARROW, AmmunitionData.MITHRIL_ARROW, AmmunitionData.ADAMANT_ARROW, AmmunitionData.RUNE_ARROW, AmmunitionData.DRAGON_ARROW}, Type.DARK_BOW),

		BRONZE_CROSSBOW(new int[] {9174}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT}, Type.CROSSBOW),
		IRON_CROSSBOW(new int[] {9177}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT, AmmunitionData.IRON_BOLT}, Type.CROSSBOW),
		STEEL_CROSSBOW(new int[] {9179}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT, AmmunitionData.IRON_BOLT, AmmunitionData.JADE_BOLT, AmmunitionData.STEEL_BOLT, AmmunitionData.PEARL_BOLT}, Type.CROSSBOW),
		MITHRIL_CROSSBOW(new int[] {9181}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT, AmmunitionData.IRON_BOLT, AmmunitionData.JADE_BOLT, AmmunitionData.STEEL_BOLT, AmmunitionData.PEARL_BOLT, AmmunitionData.MITHRIL_BOLT, AmmunitionData.TOPAZ_BOLT}, Type.CROSSBOW),
		ADAMANT_CROSSBOW(new int[] {9183}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT, AmmunitionData.IRON_BOLT, AmmunitionData.JADE_BOLT, AmmunitionData.STEEL_BOLT, AmmunitionData.PEARL_BOLT, AmmunitionData.MITHRIL_BOLT, AmmunitionData.TOPAZ_BOLT, AmmunitionData.ADAMANT_BOLT, AmmunitionData.SAPPHIRE_BOLT, AmmunitionData.EMERALD_BOLT, AmmunitionData.RUBY_BOLT}, Type.CROSSBOW),
		/* Crossbows who accept all ammo */HIGHEST_CROSSBOWS(new int[] {9185, 18357, 14684}, new AmmunitionData[] {AmmunitionData.BRONZE_BOLT, AmmunitionData.OPAL_BOLT, AmmunitionData.IRON_BOLT, AmmunitionData.JADE_BOLT, AmmunitionData.STEEL_BOLT, AmmunitionData.PEARL_BOLT, AmmunitionData.MITHRIL_BOLT, AmmunitionData.TOPAZ_BOLT, AmmunitionData.ADAMANT_BOLT, AmmunitionData.SAPPHIRE_BOLT, AmmunitionData.EMERALD_BOLT, AmmunitionData.RUBY_BOLT, AmmunitionData.RUNITE_BOLT, AmmunitionData.DIAMOND_BOLT, AmmunitionData.ONYX_BOLT, AmmunitionData.DRAGON_BOLT}, Type.CROSSBOW),

		BRONZE_DART(new int[] {806}, new AmmunitionData[] {AmmunitionData.BRONZE_DART}, Type.THROW),
		IRON_DART(new int[] {807}, new AmmunitionData[] {AmmunitionData.IRON_DART}, Type.THROW),
		STEEL_DART(new int[] {808}, new AmmunitionData[] {AmmunitionData.STEEL_DART}, Type.THROW),
		MITHRIL_DART(new int[] {809}, new AmmunitionData[] {AmmunitionData.MITHRIL_DART}, Type.THROW),
		ADAMANT_DART(new int[] {810}, new AmmunitionData[] {AmmunitionData.ADAMANT_DART}, Type.THROW),
		RUNE_DART(new int[] {811}, new AmmunitionData[] {AmmunitionData.RUNE_DART}, Type.THROW),
		DRAGON_DART(new int[] {11230}, new AmmunitionData[] {AmmunitionData.DRAGON_DART}, Type.THROW),

		BRONZE_KNIFE(new int[] {864}, new AmmunitionData[] {AmmunitionData.BRONZE_KNIFE}, Type.THROW),
		IRON_KNIFE(new int[] {863}, new AmmunitionData[] {AmmunitionData.IRON_KNIFE}, Type.THROW),
		STEEL_KNIFE(new int[] {865}, new AmmunitionData[] {AmmunitionData.STEEL_KNIFE}, Type.THROW),
		BLACK_KNIFE(new int[] {869}, new AmmunitionData[] {AmmunitionData.BLACK_KNIFE}, Type.THROW),
		MITHRIL_KNIFE(new int[] {866}, new AmmunitionData[] {AmmunitionData.MITHRIL_KNIFE}, Type.THROW),
		ADAMANT_KNIFE(new int[] {867}, new AmmunitionData[] {AmmunitionData.ADAMANT_KNIFE}, Type.THROW),
		RUNE_KNIFE(new int[] {868}, new AmmunitionData[] {AmmunitionData.RUNE_KNIFE}, Type.THROW),

		BRONZE_THROWNAXE(new int[] {800}, new AmmunitionData[] {AmmunitionData.BRONZE_THROWNAXE}, Type.THROW),
		IRON_THROWNAXE(new int[] {801}, new AmmunitionData[] {AmmunitionData.IRON_THROWNAXE}, Type.THROW),
		STEEL_THROWNAXE(new int[] {802}, new AmmunitionData[] {AmmunitionData.STEEL_THROWNAXE}, Type.THROW),
		MITHRIL_THROWNAXE(new int[] {803}, new AmmunitionData[] {AmmunitionData.MITHRIL_THROWNAXE}, Type.THROW),
		ADAMANT_THROWNAXE(new int[] {804}, new AmmunitionData[] {AmmunitionData.ADAMANT_THROWNAXE}, Type.THROW),
		RUNE_THROWNAXE(new int[] {805}, new AmmunitionData[] {AmmunitionData.RUNE_THROWNAXE}, Type.THROW),
		MORRIGANS_THROWNAXE(new int[] {13883, 13957}, new AmmunitionData[] {AmmunitionData.MORRIGANS_THROWNAXE}, Type.THROW),

		BRONZE_JAVELIN(new int[] {825}, new AmmunitionData[] {AmmunitionData.BRONZE_JAVELIN}, Type.THROW),
		IRON_JAVELIN(new int[] {826}, new AmmunitionData[] {AmmunitionData.IRON_JAVELIN}, Type.THROW),
		STEEL_JAVELIN(new int[] {827}, new AmmunitionData[] {AmmunitionData.STEEL_JAVELIN}, Type.THROW),
		MITHRIL_JAVELIN(new int[] {828}, new AmmunitionData[] {AmmunitionData.MITHRIL_JAVELIN}, Type.THROW),
		ADAMANT_JAVELIN(new int[] {829}, new AmmunitionData[] {AmmunitionData.ADAMANT_JAVELIN}, Type.THROW),
		RUNE_JAVELIN(new int[] {830}, new AmmunitionData[] {AmmunitionData.RUNE_JAVELIN}, Type.THROW),
		MORRIGANS_JAVELIN(new int[] {13879, 13953}, new AmmunitionData[] {AmmunitionData.MORRIGANS_JAVELIN}, Type.THROW),

		CHINCHOMPA(new int[] {10033}, new AmmunitionData[] {AmmunitionData.CHINCHOMPA}, Type.THROW),
		RED_CHINCHOMPA(new int[] {10034}, new AmmunitionData[] {AmmunitionData.RED_CHINCHOMPA}, Type.THROW),

		HAND_CANNON(new int[] {15241}, new AmmunitionData[] {AmmunitionData.HAND_CANNON_SHOT}, Type.HAND_CANNON);

		RangedWeaponData(int[] weaponIds, AmmunitionData[] ammunitionData, Type type) {
			this.weaponIds = weaponIds;
			this.ammunitionData = ammunitionData;
			this.type = type;
		}

		private int[] weaponIds;
		private AmmunitionData[] ammunitionData;
		private Type type;

		public int[] getWeaponIds() {
			return weaponIds;
		}

		public AmmunitionData[] getAmmunitionData() {
			return ammunitionData;
		}

		public Type getType() {
			return type;
		}

		public static RangedWeaponData getData(Player p) {
			int weapon = p.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
			for(RangedWeaponData data : RangedWeaponData.values()) {
				for(int i : data.getWeaponIds()) {
					if(i == weapon)
						return data;
				}
			}
			return null;
		}

		public static AmmunitionData getAmmunitionData(Player p) {
			RangedWeaponData data = p.getPlayerCombatAttributes().getRangedWeaponData();
			if(data != null) {
				int ammunition = p.getEquipment().getItems()[data.getType() == Type.THROW ? Equipment.WEAPON_SLOT : Equipment.AMMUNITION_SLOT].getId();
				for(AmmunitionData ammoData : AmmunitionData.values()) {
					for(int i : ammoData.getItemIds()) {
						if(i == ammunition)
							return ammoData;
					}
				}
			}
			return AmmunitionData.BRONZE_ARROW;
		}
	}

	public enum AmmunitionData {
		BRONZE_ARROW(new int[] {882}, 19, 10, 30, 1, 7),
		IRON_ARROW(new int[] {884}, 18, 9, 30, 1, 10),
		STEEL_ARROW(new int[] {886}, 20, 11, 30, 1, 16),
		MITHRIL_ARROW(new int[] {888}, 21, 12, 30, 1, 22),
		ADAMANT_ARROW(new int[] {890}, 22, 13, 30, 1, 31),
		RUNE_ARROW(new int[] {892}, 24, 15, 30, 1, 49),
		DRAGON_ARROW(new int[] {11212}, 1111, 1120, 30, 1, 65),

		BRONZE_BOLT(new int[] {877}, -1, 27, 30, 1, 13),
		OPAL_BOLT(new int [] {879, 9236}, -1, 27, 30, 1, 20),
		IRON_BOLT(new int[] {9140}, -1, 27, 30, 1, 28),
		JADE_BOLT(new int[] {9335, 9237}, -1, 27, 30, 1, 31),
		STEEL_BOLT(new int[] {9141}, -1, 27, 30, 1, 35),
		PEARL_BOLT(new int[] {880, 9238}, -1, 27, 30, 1, 38),
		MITHRIL_BOLT(new int[] {9142}, -1, 27, 30, 1, 40),
		TOPAZ_BOLT(new int[] {9336, 9239}, -1, 27, 30, 1, 50),
		ADAMANT_BOLT(new int[] {9143}, -1, 27, 30, 1, 60),
		SAPPHIRE_BOLT(new int[] {9337, 9240}, -1, 27, 30, 1, 65),
		EMERALD_BOLT(new int[] {9338, 9241}, -1, 27, 30, 1, 70),
		RUBY_BOLT(new int[] {9339, 9242}, -1, 27, 30, 1, 75),
		RUNITE_BOLT(new int[] {9144}, -1, 27, 30, 1, 84),
		DIAMOND_BOLT(new int[] {9340, 9243}, -1, 27, 30, 1, 88),
		ONYX_BOLT(new int[] {9342, 9245}, -1, 27, 30, 1, 90),
		DRAGON_BOLT(new int[] {9341, 9244}, -1, 27, 30, 1, 95),

		BRONZE_DART(new int[] {806}, 1234, 226, 17, 8, 2),
		IRON_DART(new int[] {807}, 1235, 227, 17, 8, 5),
		STEEL_DART(new int[] {808}, 1236, 228, 17, 8, 8),
		MITHRIL_DART(new int[] {809}, 1238, 229, 17, 8, 10),
		ADAMANT_DART(new int[] {810}, 1239, 230, 17, 8, 15),
		RUNE_DART(new int[] {811}, 1240, 231, 17, 8, 20),
		DRAGON_DART(new int[] {11230}, 1123, 226, 17, 8, 25),

		BRONZE_KNIFE(new int[] {864}, 219, 212, 17, 8, 8),
		IRON_KNIFE(new int[] {863}, 220, 213, 17, 8, 12),
		STEEL_KNIFE(new int[] {865}, 221, 214, 17, 8, 15),
		BLACK_KNIFE(new int[] {869}, 222, 215, 17, 8, 17),
		MITHRIL_KNIFE(new int[] {866}, 223, 215, 17, 8, 19),
		ADAMANT_KNIFE(new int[] {867}, 224, 217, 17, 8, 24),
		RUNE_KNIFE(new int[] {868}, 225, 218, 17, 8, 30),

		BRONZE_THROWNAXE(new int[] {800}, 43, 36, 17, 8, 7),
		IRON_THROWNAXE(new int[] {801}, 42, 35, 17, 8, 9),
		STEEL_THROWNAXE(new int[] {802}, 44, 37, 17, 8, 11),
		MITHRIL_THROWNAXE(new int[] {803}, 45, 38, 17, 8, 13),
		ADAMANT_THROWNAXE(new int[] {804}, 46, 39, 17, 8, 15),
		RUNE_THROWNAXE(new int[] {805}, 48, 41, 17, 8, 17),
		MORRIGANS_THROWNAXE(new int[] {13883, 13957}, 1856, -1, 17, 8, 100),

		BRONZE_JAVELIN(new int[] {825}, 206, 200, 17, 8, 7),
		IRON_JAVELIN(new int[] {826}, 207, 201, 17, 8, 9),
		STEEL_JAVELIN(new int[] {827}, 208, 202, 17, 8, 11),
		MITHRIL_JAVELIN(new int[] {828}, 209, 203, 17, 8, 13),
		ADAMANT_JAVELIN(new int[] {829}, 210, 204, 17, 8, 15),
		RUNE_JAVELIN(new int[] {830}, 211, 205, 17, 8, 17),
		MORRIGANS_JAVELIN(new int[] {13879, 13953}, 1855, -1, 17, 8, 100),

		CHINCHOMPA(new int[] {10033}, -1, -1, 17, 8, 50),
		RED_CHINCHOMPA(new int[] {10034}, -1, -1, 17, 8, 80),

		HAND_CANNON_SHOT(new int[] {15243}, 2138, 2143, 17, 8, 125);

		AmmunitionData(int[] itemIds, int startGfxId, int projectileId, int projectileSpeed, int projectileDelay, int strength) {
			this.itemIds = itemIds;
			this.startGfxId = startGfxId;
			this.projectileId = projectileId;
			this.projectileSpeed = projectileSpeed;
			this.projectileDelay = projectileDelay;
			this.strength = strength;
		}

		private int[] itemIds;
		private int startGfxId;
		private int projectileId;
		private int projectileSpeed;
		private int projectileDelay;
		private int strength;

		public int[] getItemIds() {
			return itemIds;
		}

		public boolean hasSpecialEffect() {
			return getItemIds().length >= 2;
		}

		public int getStartGfxId() {
			return startGfxId;
		}

		public int getProjectileId() {
			return projectileId;
		}

		public int getProjectileSpeed() {
			return projectileSpeed;
		}

		public int getProjectileDelay() {
			return projectileDelay;
		}

		public int getStrength() {
			return strength;
		}
	}

	public enum Type {

		LONGBOW(5, 5),
		SHORTBOW(5, 3),
		CROSSBOW(5, 4),
		THROW(4, 4),
		DARK_BOW(5, 6),
		HAND_CANNON(5, 4);

		Type(int distanceRequired, int attackDelay) {
			this.distanceRequired = distanceRequired;
			this.attackDelay = attackDelay;
		}

		private int distanceRequired;
		private int attackDelay;

		public int getDistanceRequired() {
			return distanceRequired;
		}

		public int getAttackDelay() {
			return attackDelay;
		}
	}

	public static boolean canExecuteAttack(Player p, RangedWeaponData data) {
		if(data == null)
			return false;
		if(p.getLocation() == Location.DUEL_ARENA && Dueling.checkDuel(p, 5)) {
			if(p.getDueling().selectedDuelRules[Dueling.DuelRule.NO_RANGED.ordinal()]) {
				p.getPacketSender().sendMessage("Ranged-based attacks have been disabled in this duel.");
				CombatHandler.resetAttack(p);
				return false;
			}
		}
		if(data.getType() == Type.THROW)
			return true;
		Item ammunition = p.getEquipment().getItems()[data.getType() == Type.THROW ? Equipment.WEAPON_SLOT : Equipment.AMMUNITION_SLOT];
		boolean darkBow = data.getType() == Type.DARK_BOW && ammunition.getAmount() < 2;
		if(ammunition.getAmount() < 1 || darkBow) {
			p.getPacketSender().sendMessage(darkBow ? "You need at least 2 arrows to fire this bow." : "You don't have any ammunition to fire.");
			CombatHandler.resetAttack(p);
			return false;
		}
		boolean properEquipment = false;
		for(AmmunitionData ammo : data.getAmmunitionData()) {
			for(int i : ammo.getItemIds()) {
				if(i == ammunition.getId()) {
					properEquipment = true;
					break;
				}
			}
		}
		if(!properEquipment) {
			String ammoName = ammunition.getDefinition().getName(), weaponName = p.getEquipment().getItems()[Equipment.WEAPON_SLOT].getDefinition().getName(), add = !ammoName.endsWith("s") && !ammoName.endsWith("(e)") ? "s" : "";
			p.getPacketSender().sendMessage("You can not use "+ammoName+""+add+" with "+Misc.anOrA(weaponName)+" "+weaponName+".");
			return false;
		}
		return true;
	}
}