package org.trident.world.content.combat.weapons;

import org.trident.model.Animation;
import org.trident.model.Item;
import org.trident.model.container.impl.Equipment;
import org.trident.model.definitions.ItemDefinition;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.combatdata.ranged.RangedData;
import org.trident.world.content.combat.combatdata.ranged.RangedData.RangedWeaponData;
import org.trident.world.content.combat.combatdata.ranged.RangedData.Type;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.AbyssalWhipSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.ArmadylGodswordSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.BandosGodswordSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.DarkBowSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.DragonBattleAxeSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.DragonClawSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.DragonDaggerSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.DragonHalberdSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.DragonLongSwordSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.DragonMaceSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.DragonScimitarSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.DragonSpearSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.GraniteMaulSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.HandCannonSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.KorasiSwordSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.MagicBowSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.MorrigansJavelinSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.MorrigansThrowingAxeSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.SaradominGodswordSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.StaffOfLightSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.StatiusWarhammerSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.VestasLongswordSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.ZamorakGodswordSpecialAttack;
import org.trident.world.content.combat.weapons.specials.impl.ZaniksCrossbowSpecialAttack;
import org.trident.world.content.minigames.impl.Dueling;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.entity.player.Player;

/**
 * Ripped shit from PI, too lazy to make my own
 * @author Gabbe
 */
public class WeaponHandler {

	public static boolean handleButton(Player player, int buttonId) {
		int weapon = player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
		final AttackStyle attackStyle = AttackStyle.forButtonId(buttonId);
		if(attackStyle != null) {
			player.getPlayerCombatAttributes().setAttackStyle(attackStyle);
			return true;
		} else
			if(buttonId != 7562 && buttonId != 12311 && buttonId != 7487 && buttonId != 7788 && buttonId != 8481 && buttonId != 7612 && buttonId != 7587 && buttonId != 7662 && buttonId != 7462 && buttonId != 7537)
				return false;
		if(weapon == 15486 || weapon == 21000 || weapon == 21001 || weapon == 21002 || weapon == 21003 || weapon == 15502)
			activateSpecialAttack(player, new StaffOfLightSpecialAttack());
		else if(weapon == 1305)
			activateSpecialAttack(player, new DragonLongSwordSpecialAttack());
		else if(weapon == 11694 || weapon == 13450)
			activateSpecialAttack(player, new ArmadylGodswordSpecialAttack());
		else if(weapon == 11696)
			activateSpecialAttack(player, new BandosGodswordSpecialAttack());
		else if(weapon == 11698)
			activateSpecialAttack(player, new SaradominGodswordSpecialAttack());
		else if(weapon == 11700)
			activateSpecialAttack(player, new ZamorakGodswordSpecialAttack());
		else if(ItemDefinition.forId(weapon).getName().toLowerCase().contains("dragon dagger")) 
			activateSpecialAttack(player, new DragonDaggerSpecialAttack());
		else if(weapon == 19780)
			activateSpecialAttack(player, new KorasiSwordSpecialAttack());
		else if(weapon == 4151 || weapon == 15441 || weapon == 15442 || weapon == 15443 || weapon == 15444 || weapon == 13444)
			activateSpecialAttack(player, new AbyssalWhipSpecialAttack());
		else if(weapon == 1377)
			activateSpecialAttack(player, new DragonBattleAxeSpecialAttack());
		else if(weapon == 14484)
			activateSpecialAttack(player, new DragonClawSpecialAttack());
		else if(weapon == 3204)
			activateSpecialAttack(player, new DragonHalberdSpecialAttack());
		else if(weapon == 1434)
			activateSpecialAttack(player, new DragonMaceSpecialAttack());
		else if(weapon == 4587)
			activateSpecialAttack(player, new DragonScimitarSpecialAttack());
		else if(weapon == 1249)
			activateSpecialAttack(player, new DragonSpearSpecialAttack());
		else if(weapon == 4153) { //Granite maul
			if(player.getCombatAttributes().getCurrentEnemy() == null)
				return true;
			activateSpecialAttack(player, new GraniteMaulSpecialAttack());
		}
		else if(weapon == 861)
			activateSpecialAttack(player, new MagicBowSpecialAttack());
		else if(weapon == 11235 || weapon == 15701 || weapon == 15702 || weapon == 15703 || weapon == 15704 || weapon == 13405)
			activateSpecialAttack(player, new DarkBowSpecialAttack());
		else if(weapon == 15241)
			activateSpecialAttack(player, new HandCannonSpecialAttack());
		else if(weapon == 13899 || weapon == 13901 || weapon == 13923 || weapon == 13925)
			activateSpecialAttack(player, new VestasLongswordSpecialAttack());
		else if(weapon == 13902 || weapon == 13904 || weapon == 13926 || weapon == 13928)
			activateSpecialAttack(player, new StatiusWarhammerSpecialAttack());
		else if(weapon == 13883)
			activateSpecialAttack(player, new MorrigansThrowingAxeSpecialAttack());
		else if(weapon == 13879)
			activateSpecialAttack(player, new MorrigansJavelinSpecialAttack());
		else if(weapon == 14684)
			activateSpecialAttack(player, new ZaniksCrossbowSpecialAttack());
		return true;
	}

	public static void activateSpecialAttack(Player player, SpecialAttack specialAttack) {
		if(player.getConstitution() <= 0)
			return;
		if(player.getLocation() == Location.DUEL_ARENA && player.getDueling().duelingStatus == 5) {
			if(player.getDueling().selectedDuelRules[Dueling.DuelRule.NO_SPECIAL_ATTACKS.ordinal()]) {
				player.getPacketSender().sendMessage("Special Attacks have been disabled in this duel.");	
				player.getPlayerCombatAttributes().setUsingSpecialAttack(false);
				WeaponHandler.update(player);
				return;
			}
		}
		try {
			if(specialAttack.isImmediate()) {
				specialAttack.execute(player, player.getCombatAttributes().getCurrentEnemy());
				return;
			}
			player.getPlayerCombatAttributes().setSpecialAttack(specialAttack);
			player.getPlayerCombatAttributes().setUsingSpecialAttack(!player.getPlayerCombatAttributes().isUsingSpecialAttack());
			WeaponHandler.update(player);
			specialAttack = null;
		} catch(Exception e) {
			//Mostly nullpointers should occur because of enemy is null or something
		}
	}

	public static void update(Player player) {
		final Item weapon = player.getEquipment().getItems()[Equipment.WEAPON_SLOT];
		sendSpecialAttackBar(player, weapon.getId());
		sendWeapon(player, weapon.getId());
		setWeaponAnimationIndex(player);
	}

	/**
	 * Weapons special bar, adds the spec bars to weapons that require them and
	 * removes the spec bars from weapons which don't require them
	 **/
	public static void sendSpecialAttackBar(Player player, int weapon) {
		switch (weapon) {

		case 4151: // whip
		case 13444:
		case 15441: // whip
		case 15442: // whip
		case 15443: // whip
		case 15444: // whip
			player.getPacketSender().sendInterfaceDisplayState(0, 12323);
			specialAmount(player, weapon, 12335);
			break;

		case 19780:// Korasi's Sword
		case 19784:
			player.getPacketSender().sendInterfaceDisplayState(0, 7574);
			specialAmount(player, weapon, 7586);
			break;

		case 15241:
		case 859: // magic bows
		case 861:
		case 11235:
		case 13405:
		case 15701: // dark bow
		case 15702: // dark bow
		case 15703: // dark bow
		case 15704: // dark bow
		case 13879:
		case 14684:
		case 13883:
			player.getPacketSender().sendInterfaceDisplayState(0, 7549);
			specialAmount(player, weapon, 7561);
			break;

		case 4587:// dscimmy
			player.getPacketSender().sendInterfaceDisplayState(0, 7599);
			specialAmount(player, weapon, 7611);
			break;

		case 3204: // d hally
			player.getPacketSender().sendInterfaceDisplayState(0, 8493);
			specialAmount(player, weapon, 8505);
			break;

		case 1377: // d battleaxe
			player.getPacketSender().sendInterfaceDisplayState(0, 7499);
			specialAmount(player, weapon, 7511);
			break;

		case 4153: // gmaul
			player.getPacketSender().sendInterfaceDisplayState(0, 7474);
			specialAmount(player, weapon, 7486);
			break;

		case 1249: // dspear
			player.getPacketSender().sendInterfaceDisplayState(0, 7674);
			specialAmount(player, weapon, 7686);
			break;

		case 14484: // dragon claws
			player.getPacketSender().sendInterfaceDisplayState(0, 7800);
			specialAmount(player, weapon, 7812);
			break;
		case 13902: // Statius War
			player.getPacketSender().sendInterfaceDisplayState(0, 7474);
			specialAmount(player, weapon, 7486);
			break;
		case 13904: // Statius War (deg)
			player.getPacketSender().sendInterfaceDisplayState(0, 7474);
			specialAmount(player, weapon, 7486);
			break;
			
		case 15486: // SOL
		case 21000:
		case 15502:
		case 21001:
		case 21002:
		case 21003:
		case 1215:// dragon dagger
		case 1231:
		case 13899:
		case 13901:
		case 10887:
		case 5680:
		case 13905:
		case 13907:
		case 5698:
		case 1305: // dragon long
		case 20000:
		case 20001:
		case 20002:
		case 20003:
		case 11694:
		case 13450:
		case 11698:
		case 11700:
		case 11730:
		case 11696:
			player.getPacketSender().sendInterfaceDisplayState(0, 7574);
			specialAmount(player, weapon, 7586);
			break;

		case 1434: // dragon mace
			player.getPacketSender().sendInterfaceDisplayState(0, 7624);
			specialAmount(player, weapon, 7636);
			break;

		default:
			player.getPacketSender().sendInterfaceDisplayState(1, 7624); // mace interface
			player.getPacketSender().sendInterfaceDisplayState(1, 7474); // hammer, gmaul
			player.getPacketSender().sendInterfaceDisplayState(1, 7499); // axe
			player.getPacketSender().sendInterfaceDisplayState(1, 7549); // bow interface
			player.getPacketSender().sendInterfaceDisplayState(1, 7574); // sword interface
			player.getPacketSender().sendInterfaceDisplayState(1, 7599); // scimmy sword interface, for most
			// swords
			player.getPacketSender().sendInterfaceDisplayState(1, 8493);
			player.getPacketSender().sendInterfaceDisplayState(1, 12323); // whip interface
			break;
		}
	}

	public static void sendWeapon(Player player, int Weapon) {
		String WeaponName = ItemDefinition.forId(Weapon).getName();
		String WeaponName2 = WeaponName.replaceAll("Bronze", "");
		WeaponName2 = WeaponName2.replaceAll("Iron", "");
		WeaponName2 = WeaponName2.replaceAll("Steel", "");
		WeaponName2 = WeaponName2.replaceAll("Black", "");
		WeaponName2 = WeaponName2.replaceAll("Mithril", "");
		WeaponName2 = WeaponName2.replaceAll("Adamant", "");
		WeaponName2 = WeaponName2.replaceAll("Rune", "");
		WeaponName2 = WeaponName2.replaceAll("Granite", "");
		WeaponName2 = WeaponName2.replaceAll("Dragon", "");
		WeaponName2 = WeaponName2.replaceAll("Drag", "");
		WeaponName2 = WeaponName2.replaceAll("Crystal", "");
		WeaponName2 = WeaponName2.trim();
		int uniqueIndex = 0;
		if (WeaponName.equals("None")) {
			player.getPacketSender().sendTabInterface(0, 5855); // punch, kick, block
			player.getPacketSender().sendString(5857, WeaponName);
		} else if (WeaponName.endsWith("whip")) {
			uniqueIndex = 3;
			player.getPacketSender().sendTabInterface(0, 12290); // flick, lash, deflect
			player.getPacketSender().sendInterfaceModel(12291, Weapon, 200);
			player.getPacketSender().sendString(12293, WeaponName);
		} else if (WeaponName2.toLowerCase().contains("maul")
				|| WeaponName.endsWith("warhammer")) {
			uniqueIndex = 5;
			player.getPacketSender().sendTabInterface(0, 425); // war hamer equip.
			player.getPacketSender().sendInterfaceModel(426, Weapon, 200);
			player.getPacketSender().sendString(428, WeaponName);
		} else if (WeaponName.endsWith("bow") || WeaponName.endsWith("10")
				|| Weapon == 13879
				|| Weapon == 15241
				|| Weapon == 18357
				|| Weapon == 13880
				|| Weapon == 13881
				|| Weapon == 13882
				|| Weapon == 13883
				|| WeaponName.endsWith("full")
				|| WeaponName.startsWith("seercull")) {
			uniqueIndex = 1;
			player.getPacketSender().sendTabInterface(0, 1764); // accurate, rapid, longrange
			player.getPacketSender().sendInterfaceModel(1765, Weapon, 200);
			player.getPacketSender().sendString(1767, WeaponName);
		} else if (WeaponName2.startsWith("dagger")
				|| Weapon == 13905
				|| Weapon == 13899
				|| WeaponName2.contains("Staff of light")
				|| Weapon == 18349
				|| Weapon == 10887
				|| WeaponName2.contains("sword")  || Weapon == 20671) {
			uniqueIndex = 8;
			player.getPacketSender().sendTabInterface(0, 2276); // stab, lunge, slash, block
			player.getPacketSender().sendInterfaceModel(2277, Weapon, 200);
			player.getPacketSender().sendString(2279, WeaponName);
		} else if (WeaponName.startsWith("Staff")
				|| WeaponName.endsWith("staff") || WeaponName.endsWith("wand") || Weapon == 21005 || Weapon == 21010) {
			player.getPacketSender().sendTabInterface(0, 328); // spike, impale, smash, block
			player.getPacketSender().sendInterfaceModel(329, Weapon, 200);
			player.getPacketSender().sendString(331, WeaponName);
		} else if (WeaponName2.startsWith("dart")
				|| WeaponName2.startsWith("knife")
				|| WeaponName2.startsWith("javelin")
				|| WeaponName.equalsIgnoreCase("toktz-xil-ul") || WeaponName.toLowerCase().contains("chinchompa")) {
			uniqueIndex = 2;
			player.getPacketSender().sendTabInterface(0, 4446); // accurate, rapid, longrange
			player.getPacketSender().sendInterfaceModel(4447, Weapon, 200);
			player.getPacketSender().sendString(4449, WeaponName);
		} else if (WeaponName2.startsWith("pickaxe")) {
			uniqueIndex = 6;
			player.getPacketSender().sendTabInterface(0, 5570); // spike, impale, smash, block
			player.getPacketSender().sendInterfaceModel(5571, Weapon, 200);
			player.getPacketSender().sendString(5573, WeaponName);
		} else if (WeaponName2.startsWith("axe")
				|| WeaponName2.startsWith("battleaxe")) {
			uniqueIndex = 7;
			player.getPacketSender().sendTabInterface(0, 1698); // chop, hack, smash, block
			player.getPacketSender().sendInterfaceModel(1699, Weapon, 200);
			player.getPacketSender().sendString(1701, WeaponName);
		} else if (WeaponName2.startsWith("halberd")) {
			uniqueIndex = 9;
			player.getPacketSender().sendTabInterface(0, 8460); // jab, swipe, fend
			player.getPacketSender().sendInterfaceModel(8461, Weapon, 200);
			player.getPacketSender().sendString(8463, WeaponName);
		} else if (Weapon == 14484) {
			uniqueIndex = 11;
			player.getPacketSender().sendTabInterface(0, 7762); // claws
			player.getPacketSender().sendInterfaceModel(7763, Weapon, 200);
			player.getPacketSender().sendString(7765, WeaponName);
		} else if (WeaponName2.startsWith("scythe")) {
			uniqueIndex = 9;
			player.getPacketSender().sendTabInterface(0, 8460); // jab, swipe, fend
			player.getPacketSender().sendInterfaceModel(8461, Weapon, 200);
			player.getPacketSender().sendString(8463, WeaponName);
		} else if (WeaponName2.startsWith("spear")) {
			uniqueIndex = 10;
			player.getPacketSender().sendTabInterface(0, 4679); // lunge, swipe, pound, block
			player.getPacketSender().sendInterfaceModel(4680, Weapon, 200);
			player.getPacketSender().sendString(4682, WeaponName);
		} else if (WeaponName2.toLowerCase().contains("mace")
				|| Weapon == 13902) {
			uniqueIndex = 12;
			player.getPacketSender().sendTabInterface(0, 3796);
			player.getPacketSender().sendInterfaceModel(3797, Weapon, 200);
			player.getPacketSender().sendString(3799, WeaponName);
		} else if (Weapon == 4153) {
			uniqueIndex = 5;
			player.getPacketSender().sendTabInterface(0, 425); // war hamer equip.
			player.getPacketSender().sendInterfaceModel(426, Weapon, 200);
			player.getPacketSender().sendString(428, WeaponName);
		} else if (Weapon == 18351) {
			uniqueIndex = 5;
			player.getPacketSender().sendTabInterface(0, 2423); // war hamer equip.
			player.getPacketSender().sendInterfaceModel(426, Weapon, 200);
			player.getPacketSender().sendString(428, WeaponName);
		} else {
			uniqueIndex = 4;
			player.getPacketSender().sendTabInterface(0, 2423); // chop, slash, lunge, block
			player.getPacketSender().sendInterfaceModel(2424, Weapon, 200);
			player.getPacketSender().sendString(2426, WeaponName);
		}
		player.getPlayerCombatAttributes().setAttackStyle(AttackStyle.getAttackStyle(uniqueIndex, player.getPlayerCombatAttributes().getAttackStyle().getIndex()));
		player.getPacketSender().sendConfig(43, player.getPlayerCombatAttributes().getAttackStyle().getConfigId());
		player.getPacketSender().sendConfig(172, player.getCombatAttributes().isAutoRetaliation() ? 1 : 0);
		player.getPacketSender().sendString(19000, "Combat level: " + player.getSkillManager().getCombatLevel());
	}

	/**
	 * Checks if an item is wielded in two hands
	 * @param weapon	The weapon's name
	 * @param itemId	The weapon's itemId
	 * @return
	 */
	public static boolean twoHandedWeapon(String weapon, int itemId) {
		weapon = weapon.toLowerCase();
		if (weapon.contains("ahrim") || weapon.contains("karil")
				|| weapon.contains("verac") || weapon.contains("guthan")
				|| weapon.contains("dharok") || weapon.contains("torag")) {
			return true;
		}
		if (weapon.contains("longbow") || weapon.contains("shortbow")
				|| weapon.contains("ark bow")) {
			return true;
		}
		if (weapon.contains("crystal") || weapon.contains("maul")
				|| weapon.contains("cannon")) {
			return true;
		}
		if (weapon.contains("godsword") || weapon.contains("claw")
				|| weapon.contains("aradomin sword")
				|| weapon.contains("2h") || weapon.contains("spear")) {
			return true;
		}
		if (weapon.contains("zaryte")) {
			return true;
		}
		switch (itemId) {
		case 6724: // seercull
		case 11730:
		case 4153:
		case 6528:
		case 10887:
		case 14484:
			return true;
		}
		return false;
	}

	/**
	 * Specials bar filling amount
	 **/

	public static void specialAmount(Player p, int weapon, int barId) {
		p.getPlayerCombatAttributes().setSpecialAttackBarId(barId);
		double specAmount = p.getPlayerCombatAttributes().getSpecialAttackAmount();
		p.getPacketSender().sendInterfaceComponentMoval(specAmount >= 10 ? 500 : 0, 0, (--barId));
		p.getPacketSender().sendInterfaceComponentMoval(specAmount >= 9 ? 500 : 0, 0, (--barId));
		p.getPacketSender().sendInterfaceComponentMoval(specAmount >= 8 ? 500 : 0, 0, (--barId));
		p.getPacketSender().sendInterfaceComponentMoval(specAmount >= 7 ? 500 : 0, 0, (--barId));
		p.getPacketSender().sendInterfaceComponentMoval(specAmount >= 6 ? 500 : 0, 0, (--barId));
		p.getPacketSender().sendInterfaceComponentMoval(specAmount >= 5 ? 500 : 0, 0, (--barId));
		p.getPacketSender().sendInterfaceComponentMoval(specAmount >= 4 ? 500 : 0, 0, (--barId));
		p.getPacketSender().sendInterfaceComponentMoval(specAmount >= 3 ? 500 : 0, 0, (--barId));
		p.getPacketSender().sendInterfaceComponentMoval(specAmount >= 2 ? 500 : 0, 0, (--barId));
		p.getPacketSender().sendInterfaceComponentMoval(specAmount >= 1 ? 500 : 0, 0, (--barId));
		updateSpecialBar(p);
	}

	/**
	 * Special attack text and what to highlight or blackout
	 **/

	public static void updateSpecialBar(Player p) {
		int weapon = p.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
		if (p.getPlayerCombatAttributes().isUsingSpecialAttack() && weapon != 15502 && weapon != 15486 && weapon != 22209 && weapon != 22207 && weapon != 22211 && weapon != 22213)
			p.getPacketSender().sendString(p.getPlayerCombatAttributes().getSpecialAttackBarId(), "@yel@ Special Attack (" + (int) p.getPlayerCombatAttributes().getSpecialAttackAmount() * 10 + "%)");
		else 
			p.getPacketSender().sendString(p.getPlayerCombatAttributes().getSpecialAttackBarId(), "@bla@ Special Attack (" + (int) p.getPlayerCombatAttributes().getSpecialAttackAmount() * 10 + "%)");
	}

	/**
	 * Anims
	 */
	public static int getWepAnim(Player c) {
		int weaponId = c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
		String weaponName = ItemDefinition.forId(weaponId).getName().toLowerCase();
		if (weaponId <= 0) {
			switch (c.getPlayerCombatAttributes().getAttackStyle().toString()) {
			case "PUNCH":
				return 422;
			case "KICK":
				return 423;
			case "BLOCK":
				return 451;
			}
		}
		if(weaponId == 18373)
			return 1074;
		if(weaponId == 10033 || weaponId == 10034)
			return 2779;
		RangedWeaponData rangeProp = c.getPlayerCombatAttributes().getRangedWeaponData();
		if(rangeProp != null && (rangeProp.getType() == RangedData.Type.DARK_BOW || rangeProp.getType() == Type.LONGBOW || rangeProp.getType() == Type.SHORTBOW ) | weaponId == 19143)
			return /*10848*/426;
		if(rangeProp != null && rangeProp.getType() == Type.THROW) {
			if(rangeProp.toString().toLowerCase().contains("dart")) {
				if(c.getPlayerCombatAttributes().getAttackStyle().toString().toLowerCase().contains("long"))
					return 6600;
				return 582;
			} else if(rangeProp.toString().toLowerCase().contains("knife") || rangeProp.toString().toLowerCase().contains("thrown")) {
				return 929;
			}
		}
		if (weaponName.contains("javelin") || weaponName.contains("thrownaxe")) {
			return 806;
		}
		if (weaponName.contains("halberd")) {
			return 440;
		}
		if (weaponName.startsWith("dragon dagger")) {
			if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("SLASH"))
				return 377;
			return 376;
		}
		if (weaponName.endsWith("dagger")) {
			if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("SLASH"))
				return 13048;
			return 13049;
		}
		if(weaponName.equals("staff of light") || weaponId == 21005 || weaponId == 21010) {
			if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("STAB"))
				return 13044;
			else if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("LUNGE"))
				return 13047;
			else if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("SLASH"))
				return 13048;
			else if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("BLOCK"))
				return 13049;
		} else if(weaponName.startsWith("staff") || weaponName.endsWith("staff")) {
			//bash = 400
			return 401;
		}
		if(weaponName.endsWith("warhammer") || weaponName.endsWith("battleaxe"))
			return 401;
		if (weaponName.contains("2h sword") || weaponName.contains("godsword")
				|| weaponName.contains("saradomin sword")) {
			if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("STAB"))
				return 11981;
			else if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("SLASH"))
				return 11980;
			return 11979;
		}
		if(weaponName.contains("brackish")) {
			if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("LUNGE") || c.getPlayerCombatAttributes().getAttackStyle().toString().contains("SLASH"))
				return 12029;
			return 12028;
		}
		if (weaponName.contains("scimitar") || weaponName.contains("longsword")
				|| weaponName.contains("korasi's") || weaponName.contains("katana")) {
			if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("LUNGE"))
				return 15072;
			return 15071;
		}
		if(weaponName.contains("spear")) {
			if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("LUNGE"))
				return 13045;
			else if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("SLASH"))
				return 13047;
			return 13044;
		}
		if (weaponName.contains("rapier")) {
			if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("SLASH"))
				return 12029;
			return 386;
		}
		if(weaponName.contains("claws"))
			return 393;
		if(weaponName.contains("maul") && !weaponName.contains("granite"))
			return 13055;
		if (weaponName.contains("dharok")) {
			if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("BLOCK"))
				return 2067;
			return 2066;
		}
		if (weaponName.contains("sword")) {
			return c.getPlayerCombatAttributes().getAttackStyle().toString().contains("SLASH") ? 12311 : 12310;
		}
		if (weaponName.contains("karil"))
			return 2075;
		else if (weaponName.contains("'bow") || weaponName.contains("crossbow"))
			return 4230;
		if (weaponName.contains("bow") && !weaponName.contains("'bow"))
			return 426;
		if (weaponName.contains("pickaxe")) {
			if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("SMASH"))
				return 401;
			return 400;
		}
		if (weaponName.contains("mace")) {
			if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("SPIKE"))
				return 13036;
			return 13035;
		}
		switch (weaponId) { // if you don't want
		// to use strings
		case 20000:
		case 20001:
		case 20002:
		case 20003:
			return 7041;									
		case 6522: //Obsidian throw
			return 2614;
		case 4153: // granite maul
			return 1665;
		case 13879:
		case 13883:
			return 806;
		case 16184:
			return 2661;
		case 16425:
			return 2661;
		case 15241:
			return 12153;
		case 4747: // torag
			return 0x814;
		case 4710: // ahrim
			return 406;
		case 18353:
			return 13055;
		case 18349:
			return 386;
		case 19146:
			return 386;
		case 4755: // verac
			return 2062;
		case 4734: // karil
			return 2075;
		case 10887:
			return 5865;
		case 4151:
		case 13444:
		case 15441: // whip
		case 15442: // whip
		case 15443: // whip
		case 15444: // whip
			if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("FLICK"))
				return 11968;
			else if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("LASH"))
				return 11969;
			else if(c.getPlayerCombatAttributes().getAttackStyle().toString().contains("DEFLECT"))
				return 11970;
		case 6528:
			return 2661;
		default:
			return 451;
		}
	}

	public static int getBlockEmote(Player c) {
		int weaponId = c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
		String shield = ItemDefinition.forId(c.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId()).getName().toLowerCase();
		String weapon = ItemDefinition.forId(weaponId).getName().toLowerCase();
		if (shield.contains("defender"))
			return 4177;
		if (shield.contains("2h"))
			return 7050;
		if (shield.contains("book") && (weapon.contains("wand")))
			return 420;
		if (shield.contains("shield"))
			return 1156;
		if(weapon.contains("scimitar") || weapon.contains("longsword") || weapon.contains("katana") || weapon.contains("korasi"))
			return 15074;
		switch (weaponId) {
		case 4755:
			return 2063;
		case 15241:
			return 12156;
		case 13899:
			return 13042;
		case 18355:
			return 13046;
		case 14484:
			return 397;
		case 11716:
			return 12008;
		case 4153:
			return 1666;
		case 4151:
		case 13444:
		case 15441: // whip
		case 15442: // whip
		case 15443: // whip
		case 15444: // whip
			return 11974;
		case 15486:
		case 15502:
		case 22209:
		case 22211:
		case 22207:
		case 22213:
		case 21005:
		case 21010:
			return 12806;
		case 18349:
			return 12030;
		case 18353:
			return 13054;
		case 18351:
			return 13042;
		case 20000:
		case 20001:
		case 20002:
		case 20003:
		case 11694:
		case 11698:
		case 11700:
		case 11696:
		case 11730:
			return 7050;
		case -1:
			return 424;
		default:
			return 424;
		}
	}

	public static void setWeaponAnimationIndex(Player player) {
		if(player.getNpcTransformationId() == SoulWars.GHOST_ID)
			return;
		String weaponName = ItemDefinition.forId(player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId()).getName().toLowerCase();
		int item = player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
		int playerStandIndex = 0x328;
		int playerTurnIndex = 0x337;
		int playerWalkIndex = 0x333;
		int playerTurn180Index = 0x334;
		int playerTurn90CWIndex = 0x335;
		int playerTurn90CCWIndex = 0x336;
		int playerRunIndex = 0x338;
		if (weaponName.contains("halberd") || weaponName.contains("guthan")) {
			playerStandIndex = 809;
			playerWalkIndex = 1146;
			playerRunIndex = 1210;
		}
		else if (weaponName.startsWith("basket")) {
			playerWalkIndex = 1836;
			playerRunIndex = 1836;
		}
		else if (weaponName.contains("dharok")) {
			playerStandIndex = 0x811;
			playerWalkIndex = 0x67F;
			playerRunIndex = 0x680;
		}
		else if (weaponName.contains("sled")) {
			playerStandIndex = 1461;
			playerWalkIndex = 1468;
			playerRunIndex = 1467;
		}
		else if (weaponName.contains("ahrim")) {
			playerStandIndex = 809;
			playerWalkIndex = 1146;
			playerRunIndex = 1210;
		}
		else if (weaponName.contains("verac")) {
			playerStandIndex = 0x328;
			playerWalkIndex = 0x333;
			playerRunIndex = 824;
		}
		else if (weaponName.contains("longsword") || weaponName.contains("scimitar")) {
			playerStandIndex = 15069;//12021;
			playerRunIndex = 15070;//12023;
			playerWalkIndex = 15073; //12024;
		} else if(weaponName.contains("silverlight")
				|| weaponName.contains("korasi's") || weaponName.contains("katana")) {
			playerStandIndex = 12021;
			playerRunIndex = 12023;
			playerWalkIndex = 12024;
		}
		else if (weaponName.contains("wand") || weaponName.contains("staff")
				|| weaponName.contains("staff") || weaponName.contains("spear") || item == 21005 || item == 21010) {
			playerStandIndex = 8980;
			playerRunIndex = 1210;
			playerWalkIndex = 1146;
		}
		else if (weaponName.contains("karil")) {
			playerStandIndex = 2074;
			playerWalkIndex = 2076;
			playerRunIndex = 2077;
		}
		else if (weaponName.contains("2h sword") || weaponName.contains("godsword")
				|| weaponName.contains("saradomin sw")) {
			playerStandIndex = 7047;
			playerWalkIndex = 7046;
			playerRunIndex = 7039;
		}
		else if (weaponName.contains("bow")) {
			playerStandIndex = 808;
			playerWalkIndex = 819;
			playerRunIndex = 824;
		}
		if(weaponName.toLowerCase().contains("rapier")) {
			playerStandIndex = 12021;
			playerWalkIndex = 12024;
			playerRunIndex = 12023;
		}
		switch (player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId()) {
		case 18353: // maul chaotic
			playerStandIndex = 13217;
			playerWalkIndex = 13218;
			playerRunIndex = 13220;
			break;
		case 16184:
			playerStandIndex = 13217;
			playerWalkIndex = 13218;
			playerRunIndex = 13220;
			break;
		case 16425:
			playerStandIndex = 13217;
			playerWalkIndex = 13218;
			playerRunIndex = 13220;
			break;
		case 4151:
		case 13444:
		case 15441: // whip
		case 15442: // whip
		case 15443: // whip
		case 15444: // whip
			playerStandIndex = 11973;
			playerWalkIndex = 11975;
			playerRunIndex = 1661;
			break;
		case 15039:
			playerStandIndex = 12000;
			playerWalkIndex = 1663;
			playerRunIndex = 1664;
			break;
		case 10887:
			playerStandIndex = 5869;
			playerWalkIndex = 5867;
			playerRunIndex = 5868;
			break;
		case 6528:
			playerStandIndex = 0x811;
			playerWalkIndex = 2064;
			playerRunIndex = 1664;
			break;
		case 4153:
			playerStandIndex = 1662;
			playerWalkIndex = 1663;
			playerRunIndex = 1664;
			break;
		case 15241:
			playerStandIndex = 12155;
			playerWalkIndex = 12154;
			playerRunIndex = 12154;
			break;
		case 20000:
		case 20001:
		case 20002:
		case 20003:
		case 11694:
		case 11696:
		case 11730:
		case 11698:
		case 11700:
			break;
		case 1305:
			playerStandIndex = 809;
			break;
		}
		player.getAttributes().setPlayerAnimation(0, new Animation(playerStandIndex)).
		setPlayerAnimation(1, new Animation(playerTurnIndex)).
		setPlayerAnimation(2, new Animation(playerWalkIndex)).
		setPlayerAnimation(3, new Animation(playerTurn180Index)).
		setPlayerAnimation(4, new Animation(playerTurn90CCWIndex)).
		setPlayerAnimation(5, new Animation(playerTurn90CWIndex)).
		setPlayerAnimation(6, new Animation(playerRunIndex));
	}

	public static int getAttackDelay(Player plr) {
		int id = plr.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
		String s = ItemDefinition.forId(id).getName().toLowerCase();
		if(plr.getCombatAttributes().getAttackType() == AttackType.MAGIC)
			return 5;
		if (id == -1)
			return 4;// unarmed
		if(id == 18357 || id == 14684)
			return 3;
		RangedWeaponData rangedData = plr.getPlayerCombatAttributes().getRangedWeaponData();
		if(rangedData != null) {
			if(rangedData.toString().toLowerCase().contains("knife") || rangedData.toString().toLowerCase().contains("dart"))
				return 2;
			return rangedData.getType().getAttackDelay();
		}
		if(id == 18365)
			return 3;
		else if(id == 18349) //CCbow and rapier
			return 4;
		if (id == 18353) // cmaul
			return 7;// chaotic maul

		if (id == 20000)
			return 4;// gs	
		if (id == 20001)
			return 4;// gs	
		if (id == 20002)
			return 4;// gs	
		if (id == 20003)
			return 4;// gs	
		if (id == 18349)
			return 5;// chaotic rapier				
		if (id == 18353) // cmaul
			return 7;// chaotic maul
		if (id == 16877)
			return 4;// dung 16877 shortbow
		if (id == 19143)
			return 3;// sara shortbow
		if (id == 19146)
			return 4;// guthix shortbow
		if (id == 19149)
			return 3;// zammy shortbow

		switch (id) {
		case 11235:
		case 13405: //dbow
		case 15701: // dark bow
		case 15702: // dark bow
		case 15703: // dark bow
		case 15704: // dark bow
		case 19146: // guthix bow
			return 9;
		case 13879:
			return 8;
		case 15241: // hand cannon
			return 8;
		case 11730:
			return 4;
		case 14484:
			return 5;
		case 13883:
			return 6;
		case 10887:
		case 6528:
		case 15039:
			return 7;
		case 13905:
			return 5;
		case 13907:
			return 5;
		case 18353:
			return 7;
		case 18349:
			return 4;
		case 20000:
		case 20001:
		case 20002:
		case 20003:
			return 4;

		case 16403: //long primal
			return 5;
		}

		if (s.endsWith("greataxe"))
			return 7;
		else if (s.equals("torags hammers"))
			return 5;
		else if (s.equals("guthans warspear"))
			return 5;
		else if (s.equals("veracs flail"))
			return 5;
		else if (s.equals("ahrims staff"))
			return 6;
		else if (s.equals("chaotic crossbow"))
			return 4;
		else if (s.contains("staff")) {
			if (s.contains("zamarok") || s.contains("guthix")
					|| s.contains("saradomian") || s.contains("slayer")
					|| s.contains("ancient"))
				return 4;
			else
				return 5;
		} else if (s.contains("aril")) {
			if (s.contains("composite") || s.equals("seercull"))
				return 5;
			else if (s.contains("Ogre"))
				return 8;
			else if (s.contains("short") || s.contains("hunt")
					|| s.contains("sword"))
				return 4;
			else if (s.contains("long") || s.contains("crystal"))
				return 6;
			else if (s.contains("'bow"))
				return 7;

			return 5;
		} else if (s.contains("dagger"))
			return 4;
		else if (s.contains("godsword") || s.contains("2h"))
			return 6;
		else if (s.contains("longsword"))
			return 5;
		else if (s.contains("sword"))
			return 4;
		else if (s.contains("scimitar") || s.contains("katana"))
			return 4;
		else if (s.contains("mace"))
			return 5;
		else if (s.contains("battleaxe"))
			return 6;
		else if (s.contains("pickaxe"))
			return 5;
		else if (s.contains("thrownaxe"))
			return 5;
		else if (s.contains("axe"))
			return 5;
		else if (s.contains("warhammer"))
			return 6;
		else if (s.contains("2h"))
			return 7;
		else if (s.contains("spear"))
			return 5;
		else if (s.contains("claw"))
			return 4;
		else if (s.contains("halberd"))
			return 7;

		// sara sword, 2400ms
		else if (s.equals("granite maul"))
			return 7;
		else if (s.equals("toktz-xil-ak"))// sword
			return 4;
		else if (s.equals("tzhaar-ket-em"))// mace
			return 5;
		else if (s.equals("tzhaar-ket-om"))// maul
			return 7;
		else if (s.equals("chaotic maul"))// maul
			return 7;
		else if (s.equals("toktz-xil-ek"))// knife
			return 4;
		else if (s.equals("toktz-xil-ul"))// rings
			return 4;
		else if (s.equals("toktz-mej-tal"))// staff
			return 6;
		else if (s.contains("whip"))
			return 4;
		else if (s.contains("dart"))
			return 3;
		else if (s.contains("knife"))
			return 3;
		else if (s.contains("javelin"))
			return 6;
		return 5;
	}

	public enum ExperienceStyle {
		/**
		 * Earns experience in the strength skill.
		 */
		STRENGTH,

		/**
		 * Earns experience in the attack skill.
		 */
		ATTACK,

		/**
		 * Earns experience in the defense skill.
		 */
		DEFENCE,

		/**
		 * Earns experience in the strength, attack and defense skill.
		 */
		MELEE_SHARED,

		/**
		 * Earns experience in the ranged skill.
		 */
		RANGED,

		/**
		 * Earns experience in the ranged and defense skill.
		 */
		RANGED_SHARED,

		/**
		 * Earns experience in the magic skill.
		 */
		MAGIC,

		/**
		 * Earns experience in the magic and defense skill.
		 */
		MAGIC_SHARED;
	}

	public enum AttackStyle {
		//Normal, default
		PUNCH(0, 0, 5862, 1, 0, ExperienceStyle.ATTACK),
		KICK(0, 1, 5861, 2, 0, ExperienceStyle.STRENGTH),
		BLOCK(0, 2, 5860, 0, 0, ExperienceStyle.DEFENCE),

		//Range
		ACCURATE(1, 0, 1772, 0, 1, ExperienceStyle.RANGED),
		RAPID(1, 1, 1771, 1, 0, ExperienceStyle.RANGED),
		LONGRANGE(1, 2, 1770, 2, 2, ExperienceStyle.RANGED_SHARED),

		//Range 2
		ACCURATE2(2, 0, 4454, 0, 1, ExperienceStyle.RANGED),
		RAPID2(2, 1, 4453, 1, 0, ExperienceStyle.RANGED),
		LONGRANGE2(2, 2, 4452, 2, 2, ExperienceStyle.RANGED_SHARED),

		//Whip
		FLICK(3, 0, 12298, 0, 0, ExperienceStyle.ATTACK),
		LASH(3, 1, 12297, 1, 1, ExperienceStyle.MELEE_SHARED),
		DEFLECT(3, 2, 12296, 2, 0, ExperienceStyle.DEFENCE),

		//Scimitar
		CHOP(4, 0, 2429, 0, 0, ExperienceStyle.ATTACK),
		SLASH(4, 1, 2432, 1, 0, ExperienceStyle.STRENGTH),
		LUNGE(4, 2, 2431, 2, 1, ExperienceStyle.MELEE_SHARED),
		BLOCK2(4, 3, 2430, 3, 0, ExperienceStyle.DEFENCE),

		//Warhammer
		POUND(5, 0, 433, 0, 0, ExperienceStyle.ATTACK),
		PUMMEL(5, 1, 432, 1, 0, ExperienceStyle.STRENGTH),
		BLOCK3(5, 2, 431, 2, 0, ExperienceStyle.DEFENCE),

		//Pickaxe
		SPIKE(6, 0, 5576, 0, 0, ExperienceStyle.ATTACK),
		IMPALE(6, 1, 5579, 1, 0, ExperienceStyle.STRENGTH),
		SMASH(6, 2, 5578, 2, 1, ExperienceStyle.STRENGTH),
		BLOCK4(6, 3, 5577, 3, 0, ExperienceStyle.DEFENCE),

		//Battleaxe/axe
		CHOP2(7, 0, 1704, 0, 0, ExperienceStyle.ATTACK),
		HACK(7, 1, 1707, 1, 0, ExperienceStyle.STRENGTH),
		SMASH2(7, 2, 1706, 2, 1, ExperienceStyle.STRENGTH),
		BLOCK5(7, 3, 1705, 3, 0, ExperienceStyle.DEFENCE),

		//Longsword
		STAB(8, 0, 2282, 0, 1, ExperienceStyle.ATTACK),
		LUNGE2(8, 1, 2285, 1, 0, ExperienceStyle.STRENGTH),
		SLASH2(8, 2, 2284, 2, 0, ExperienceStyle.STRENGTH),
		BLOCK6(8, 3, 2283, 3, 0, ExperienceStyle.DEFENCE),

		//Halberd
		JAB(9, 0, 8468, 1, 0, ExperienceStyle.MELEE_SHARED),
		SWIPE(9, 1, 8467, 2, 0, ExperienceStyle.STRENGTH),
		FEND(9, 2, 8466, 0, 0, ExperienceStyle.DEFENCE),

		//Spear
		LUNGE3(10, 0, 4685, 0, 1, ExperienceStyle.MELEE_SHARED),
		SWIPE2(10, 1, 4688, 1, 1, ExperienceStyle.MELEE_SHARED),
		POUND2(10, 2, 4687, 2, 1, ExperienceStyle.MELEE_SHARED),
		BLOCK7(10, 3, 4686, 3, 1, ExperienceStyle.DEFENCE),

		//Claws
		CHOP3(11, 0, 7768, 0, 0, ExperienceStyle.ATTACK),
		SLASH3(11, 1, 7771, 1, 0, ExperienceStyle.STRENGTH),
		LUNGE4(11, 2, 7770, 2, 1, ExperienceStyle.MELEE_SHARED),
		BLOCK8(11, 3, 7769, 3, 0, ExperienceStyle.DEFENCE),

		//Mace
		POUND3(12, 0, 3802, 0, 0, ExperienceStyle.ATTACK),
		PUMMEL2(12, 1, 3805, 1, 0, ExperienceStyle.STRENGTH),
		SPIKE2(12, 2, 3804, 2, 1, ExperienceStyle.MELEE_SHARED),
		BLOCK9(12, 3, 3803, 3, 0, ExperienceStyle.DEFENCE),

		//Staff
		BASH(13, 0, 336, 1, 0, ExperienceStyle.ATTACK),
		POUND4(13, 0, 335, 2, 0, ExperienceStyle.STRENGTH),
		FOCUS(13, 0, 334, 0, 0, ExperienceStyle.DEFENCE);

		AttackStyle(int uniqueId, int index, int buttonId, int configId, int extraAttackDelay, ExperienceStyle experienceReward) {
			this.uniqueId = uniqueId;
			this.index = index;
			this.buttonId = buttonId;
			this.configId = configId;
			this.extraAttackDelay = extraAttackDelay;
			this.experienceReward = experienceReward;
		}

		private int uniqueId;

		public int getUniqueId() {
			return this.uniqueId;
		}

		private int index;

		public int getIndex() {
			return index;
		}

		private int buttonId;

		public int getButtonId() {
			return this.buttonId;
		}

		private int configId;

		public int getConfigId() {
			return this.configId;
		}

		private int extraAttackDelay;

		public int getExtraAttackDelay() {
			return this.extraAttackDelay;
		}

		private ExperienceStyle experienceReward;

		public ExperienceStyle getExperienceReward() {
			return experienceReward;
		}

		public static AttackStyle forId(int id) {
			for(AttackStyle attackStyles : AttackStyle.values()) {
				if(attackStyles != null && attackStyles.ordinal() == id)
					return attackStyles;
			}
			return null;
		}

		public static AttackStyle forButtonId(int id) {
			for(AttackStyle attackStyles : AttackStyle.values()) {
				if(attackStyles.getButtonId() == id)
					return attackStyles;
			}
			return null;
		}

		public static AttackStyle getAttackStyle(int uniqueId, int index) {
			for(AttackStyle attackStyles : AttackStyle.values()) {
				if(attackStyles != null && attackStyles.getUniqueId() == uniqueId && attackStyles.getIndex() == index) {
					return attackStyles;
				}
			}
			for(AttackStyle attackStyles : AttackStyle.values()) { //Index is 4 but only index 3 exists
				if(attackStyles != null && attackStyles.getUniqueId() == uniqueId && attackStyles.getIndex() == index - 1) {
					return attackStyles;
				}
			}
			for(AttackStyle attackStyles : AttackStyle.values()) { //Return first index
				if(attackStyles != null && attackStyles.getUniqueId() == uniqueId) {
					return attackStyles;
				}
			}
			return null;
		}
	}
}