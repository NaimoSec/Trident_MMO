package org.trident.world.entity.npc;

import org.trident.model.Direction;
import org.trident.util.Misc;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.entity.npc.custom.impl.NexMinion;

/**
 * Tooooons of random shit in here
 */
public class NPCData {

	public enum CustomNPCData {
		AGRITH_NA_NA(3493, 1400, 60, 100, 80, 140, 5),
		FLAMBEED(3494, 1880, 150, 160, 130, 140, 5),
		KARAMEL(3495, 2000, 180, 200, 180, 180, 5),
		DESSOURT(3496, 2500, 200, 220, 190, 180, 5),
		GELATINNOTH_MOTHER(3497, 2100, 210, 200, 210, 200, 5),
		CULINAROMANCER(3491, 3700, 220, 250, 220, 360, 5),
		TZHAAR_KIH(2627, 100, 40, 50, 40, 15, 5),
		TZ_KEK(2630, 200, 60, 60, 74, 29, 5),
		TZ_KEK_SMALL(2738, 100, 50, 50, 50, 20, 5),
		TOK_XIL(2631, 400, 100, 100, 110, 150, 5), //Damage Customly handled
		YT_MEJKOT(2741, 900, 200, 190, 150, 220, 5), //Damage Customly handled
		KET_ZEK(2743, 1800, 250, 180, 200, 440, 5), //Damage Customly handled
		TZ_TOK_JAD(2745, 2550, 350, 200, 290, 965, 5), //Damage Customly handled
		YT_HUR_KUT(2746, 300, 150, 150, 150, 120, 5),
		NOMAD(8528, 10000, 400, 400, 400, 400, 5),
		NOMAD_RANGER_HELPER(12050, 200, -1, -1, -1, -1, -1), //Damage Customly handled
		SOULWARS_BARRICADE(SoulWars.BARRICADE_NPC, 200, -1, -1, -1, -1, -1),
		
		ARCHER1(27, 320, -1, 40, -1, 85, 5),
		MAGE1(912, 500, -1, 30, -1, 100, 5),
		SKELETON1(90, 300, 30, 30, 30, 110, 5),
		SCORPION1(108, 320, 35, 30, 30, 90, 5),
		SHADOW_SPIDER1(58, 450, 40, 40, 40, 100, 5),
		BANDIT(1880, 425, 40, 40, 40, 100, 5),
		ZAMORAK_RANGER(6365, 670, -1, 60, -1, 100, 5),
		GREEN_DRAGON1(941, 800, 80, 80, 80, 140, 5),
		
		
		DHAROK_THE_WRETCHED(2026, 1300, 250, 130, 250, 500, 9),
		VERAC_THE_DEFILED(2030, 1300, 230, 120, 250, 140, 7),
		AHRIM_THE_BLIGHTED(2025, 1250, 1, 100, 1, 125, 8),
		TORAG_THE_CORRUPTED(2029, 1250, 120, 120, 90, 140, 7),
		KARIL_THE_TAINTED(2028, 1000, 1, 120, 1, 130, 8),
		GUTHAN_THE_INFESTED(2027, 1100, 120, 120, 90, 130, 7),
		
		BRONZE_ARMOR(4278, 120, 5, 10, 10, 25, 6),
		IRON_ARMOR(4279, 160, 15, 25, 25, 35, 6),
		STEEL_ARMOR(4280, 220, 25, 45, 40, 40, 6),
		BLACK_ARMOR(4281, 260, 30, 50, 50, 50, 6),
		MITHRIL_ARMOR(4282, 312, 40, 60, 56, 60, 6),
		ADAMANT_ARMOR(4283, 400, 50, 70, 70, 80, 6),
		RUNITE_ARMOR(4284, 560, 70, 80, 90, 125, 6);

		CustomNPCData(int npc, int constitution, int attack, int defence, int strength, int maxHit, int attackSpeed) {
			this.npcId = npc;
			this.constitution = constitution;
			this.attack = attack;
			this.defence = defence;
			this.strength = strength;
			this.maxHit = maxHit;
			this.attackSpeed = attackSpeed;
		}
		public int npcId, constitution, attack, defence, strength, maxHit, attackSpeed;
		

		public static CustomNPCData forId(int id) {
			for(CustomNPCData npc : CustomNPCData.values()) {
				if(npc.ordinal() == id) {
					return npc;
				}
			}
			return null;
		}
		
		public static CustomNPCData forNpcId(int id) {
			for(CustomNPCData npc : CustomNPCData.values()) {
				if(npc.npcId == id) {
					return npc;
				}
			}
			return null;
		}
		
		
		public static boolean isUndeadNpc(NPC n) {
			if(n.getDefinition() != null) {
				String name = n.getDefinition().getName().toLowerCase();
				return name.contains("keleton") || name.contains("ombie") || name.contains("ghost") || n.getLocation() != null && n.getLocation() == Location.BARROWS || n.getLocation() == Location.BARROWS;
			}
			return false;
		}
	}
	
	public static boolean godwarsDungeonBoss(int npc) {
		return npc == 13447 || NexMinion.nexMinion(npc) || npc == 6260 || npc == 6261 || npc == 6263 || npc == 6265 || npc == 6222 || npc == 6223 || npc == 6225 || npc == 6227 || npc == 6203 || npc == 6208 || npc == 6204 || npc == 6206 || npc == 6247 || npc == 6248 || npc == 6250 || npc == 6252;
	}
	
	public static boolean isArmadylGodwarsNpc(int npc) {
		return npc == 6246 || npc == 6229 || npc == 6230 || npc == 6231;
	}
	
	public static boolean isBandosGodwarsNpc(int npc) {
		return npc == 102 || npc == 3583 || npc == 115 || npc == 113 || npc == 6273 || npc == 6276 || npc == 6277 || npc == 6288;
	}
	
	public static boolean isSaradominGodwarsNpc(int npc) {
		return npc == 6258 || npc == 6259 || npc == 6254 || npc == 6255 || npc == 6257 || npc == 6256;
	}
	
	public static boolean isZamorakGodwarsNpc(int npc) {
		return npc == 10216 || npc == 6216 || npc == 1220 || npc == 6007 || npc == 6219 ||npc == 6220 || npc == 6221 || npc == 49 || npc == 4418;
	}
	
	public static int getAttackAnimation(int npcId) {
		switch(npcId) {
		case 10127:
			return 13176;
		case 7133: // Bork
			return 8754;
		case 9767:
			return 13703;
		case 2743:
			return 1;
		case 1540:
			return 1;
		case 1813:
			return 1;
		case 1125://dad
			return 284;
		case 10040:
			return 10816;
		case 2779:
			return 426;
		case 3067:
			return 7041; // 2h emote
		case 5529:
			return 5782;
		case 3622:// Zombies!!
			return 5578;
		case 3732:
		case 3734:
		case 3736:
		case 3738:
		case 3740:
			return 3901;
		case 751: // the spirit
			return 6728;
		case 5213:
		case 5214:
		case 5215:
		case 5216:
		case 5217:
		case 5218:
		case 5219: // Penance fighter
			return 5097;

		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237: // Penance ranger
			return 5396;
			// end of barb assault

		case 2589:
		case 2588:
		case 2587:
		case 2586:
			return 1979;
		case 8528:
			return 12696;
		case 6260:
			return 7060;
		case 8597:
			return 11202;// avatar of desc
		case 10530: // forgotten warrior
			return 2661;
		case 5248:
			return 6354 + Misc.getRandom(1);
		case 2892:
		case 2894:
			return 2868;

		case 4477:
			// startAnimation(5556);
			// c.gfx0(369);
			return 5556;
		case 9463:
			return 12791;

		case 9467:
			return 12791;

		case 9465:
			return 12791;
		case 3101: // Melee
			return 10058;
		case 3102: // Range
		case 3103: // Mage
			return 10057;
		case 4278:
		case 4279:
		case 4280:
		case 4281:
		case 4282:
		case 4284:
			return 451;
		case 93:
			return 5499;
		case 4283:
			return 7048;
			// dung npcs
		case 4382: // ankou
			return 422;
		case 5247: // PENANCE QUEEN
			return 5411;
			// GODWARS
		case 3247: // Hobgoblin
			return 6184;

		case 6270: // Cyclops
		case 6269: // Ice cyclops
		case 4291: // Cyclops
		case 4292: // Ice cyclops
			return 4652;

		case 6219: // Spiritual Warrior
		case 6255: // Spiritual Warrior
			return 451;

		case 6229: // Spirtual Warrior arma
			return 6954;

		case 6218: // Gorak
			return 4300;

		case 6212: // Werewolf
			return 6536;

		case 6220: // Spirtual Ranger
		case 6256: // Spirtual Ranger
			return 426;

		case 6257: // Spirtual Mage
		case 6221: // Spirtual Mage
			return 811;

		case 6276: // Spirtual Ranger
		case 6278: // Spirtual Mage
		case 6272: // Ork
		case 6274: // Ork
		case 6277: // Spirtual Warrior bandos
			return 4320;

		case 6230: // Spirtual Ranger
		case 6233: // Aviansie
		case 6239: // Aviansie
		case 6232: // Aviansie
			return 6953;

		case 6254: // Saradomin Priest
			return 440;
		case 6258: // Saradomin Knight
			return 7048;
		case 6231: // Spirtual Mage
			return 6952;

		case 3248: // HellHound
			return 6579;
			// ENDS

		case 6204:
			return 64;
		case 6208:
			return 6947;
		case 6206:
			return 6945;
		case 3494:
			return 1750;
		case 3491:
			return 1979;
		case 2627:
			return 2621;
		case 2630:
			return 2625;
		case 2631:
			return 2633;
		case 2741:
			return 2637;
		case 2746:
			return 2637;
		case 2607:
			return 2611;
		case 50:
		case 53:
		case 54:
		case 742:
		case 55:
		case 941:
		case 4677:
		case 1590:
		case 1591:
		case 1592:
			return 91;
			// bandos gwd
		case 6261:
		case 6263:
		case 6265:
			return 6154;
			// end of gwd
			// arma gwd

		case 6222:
			return 6976;
		case 6225:
			return 6953;
		case 6223:
			return 6952;
		case 6227:
			return 6954;
			// end of arma gwd
			// sara gwd
		case 6247:
			return 6964;
		case 6248:
			return 6376;
		case 6250:
			return 7018;
		case 6252:
			return 7009;
			// end of sara gwd
		case 13: // wizards
			return 711;
		case 103:
			return 5540;
		case 655:
		case 104:
			return 5535;

		case 1624:
			return 1557;

		case 1648:
			return 1590;

		case 2783: // dark beast
			return 2731;

		case 1615: // abby demon
			return 1537;

		case 1613: // nech
			return 1528;

		case 1610:
		case 1611: // garg
			return 1519;

		case 1616: // basilisk
			return 1546;

		case 90: // skele
			return 260;

		case 124: // earth warrior
			return 390;

		case 803: // monk
			return 422;

		case 52: // baby drag
			return 25;

		case 58: // Shadow Spider
		case 59: // Giant Spider
		case 60: // Giant Spider
		case 61: // Spider
		case 62: // Jungle Spider
		case 63: // Deadly Red Spider
		case 64: // Ice Spider
		case 134:
			return 5327;

		case 105: // Bear
		case 106: // Bear
			return 4925;

		case 412:
		case 78:
			return 4915;

		case 2033: // rat
			return 4933;

		case 2031: // bloodworm
			return 2070;

		case 101: // goblin
		case 3663:
			return 6188;

		case 81: // cow
			return 5849;

		case 21: // hero
			return 451;

		case 41: // chicken
			return 55;

		case 9: // guard
		case 32: // guard
		case 20: // paladin
			return 451;

		case 1338: // dagannoth
		case 1340:
		case 1342:
			return 1341;

		case 19: // white knight
			return 406;

		case 110:
		case 111: // ice giant
		case 112:
		case 117:
			return 4672;

		case 2452:
			return 1312;

		case 2889:
			return 2859;

		case 118:
		case 119:
			return 99;

		case 82:// Lesser Demon
		case 83:// Greater Demon
		case 84:// Black Demon
		case 1472:// jungle demon
		case 10039:
			return 64;

		case 1267:
		case 1265:
			return 1312;

		case 125: // ice warrior
		case 178:
			return 451;

		case 1154: // Kalphite Soldier
			return 6225;
		case 1156: // Kalphite worker
		case 1160:// kalphite queen flying one
			return 6236;
		case 1157: // Kalphite guardian
			return 6226;
		case 1153: // Kalphite Worker
		case 1158: // kalphite queen no fly
			// case 1160:// kalphite queen flying one
			return 6223;

		case 123:
		case 122:
			return 164;

		case 2028: // karil
			return 2075;

		case 2025: // ahrim
			return 729;

		case 2026: // dharok
			return 2066;

		case 2027: // guthan
			return 2080;

		case 2029: // torag
			return 0x814;

		case 2030: // verac
			return 2062;

		case 2881: // supreme
			return 2855;

		case 2882: // prime
			return 2854;

		case 2883: // rex
			return 2851;

		case 3200:
			return 3146;

		case 7334:
			return 8172;
		case 7336:
			return 7871;
		case 5228:
			return 5228;

		case 7340:
			return 7879;

		case 7342:
			return 7879;

		case 7344:
			return 8190;

		case 7346:
			return 8048;

		case 7348:
			return 5989;

		case 7350:
			return 7693;

		case 6795:
			return 1010;

		case 10775:
			return 13151;

		case 2037:
			return 5485;

		case 6797:
			return 8104;

		case 6799:
			return 8069;

		case 6801:
			return 7853;

		case 6803:
			return 8159;

		case 6805:
			return 7786;

		case 6807:
			return 8148;

		case 6810:
			return 7970;

		case 6812:
			return 7935;

		case 6814:
			return 7741;

		case 6816:
			return 8288;

		case 6819:
			return 7667;

		case 6821:
			return 7680;

		case 6823:
			return 6376;

		case 6826:
			return 5387;

		case 6828:
			return 8208;

		case 6830:
			return 8292;
		case 6832:
			return 7795;
		case 6834:
			return 8248;
		case 6836:
			return 8275;
		case 6838:
			return 6254;
		case 6856:
			return 4921;
		case 6858:
			return 5327;

		case 6860:
		case 6862:
		case 6864:
			return 7656;

		case 6868:
			return 7896;

		case 6870:
			return 8303;

		case 6872:
			return 7769;

		case 6874:
			return 5782;

		case 6890:
			return 7260;

		case 7330:
			return 8223;

		case 7332:
			return 8032;

		case 7338:
			return 5228;

		case 7352:
			return 8234;

		case 7354:
			return 7755;

		case 7355:
			return 7834;

		case 7358:
			return 7844;

		case 7359:
			return 8183;

		case 7362:
			return 8257;

		case 7364:
		case 7366:
			return 5228;

		case 7368:
		case 7369:

			return 8130;

		case 7371:
			return 8093;

		case 7374:
			return 7994;

		case 7376:
			return 7946;

		default:
			return -1;
		}
	}

	public static int getDeathEmote(int npcId) {
		switch(npcId) {
		case 7133: // Bork
			return 8756;
		case 10127:
			return 13171;
		case 9767:
			return 13702;
		case 2437:
			return 1814;
		case 1813:
			return 1814;
		case 1125://dad
			return 287;
		case 10040:
			return 10815;
		case 3622:// Zombies!!
			return 5575;
		case 5529:
			return 5784;
		case 3732:
		case 3734:
		case 3736:
		case 3738:
		case 3740:
			return 3903;

		case 5229:
		case 5230:
		case 5231:
		case 5232:
		case 5233:
		case 5234:
		case 5235:
		case 5236:
		case 5237: // Penance ranger
			return 5397;

		case 5213:
		case 5214:
		case 5215:
		case 5216:
		case 5217:
		case 5218:
		case 5219: // Penance fighter

			return 5098;

			// end of barbarien assault

		case 2636:
			return 6951;
		case 10530:
			return 0; // forgotten warrior
		case 8596:
			return 11197;
		case 8597:
			return 11199;
		case 3068:
			return 2987;
		case 6142:
		case 6143:
		case 6144:
		case 6145:
			return 6;
			// START OF Dungeoneering
		case 1622:
			return 1597;
		case 1631:
		case 1632:
			return 1568;
		case 1260:
			return 1563;
		case 4382: // ankou
			return 836;
		case 5247: // penance queen
			return 5412;
			// END OF DUNGEONEERING
		case 8528:
			return 12694;
		case 10775:
			return 13153;
		case 9463:
			return 12793;
		case 5248:
			return 6951;
		case 9467:
			return 12793;
		case 9465:
			return 12793;
		case 8349:
		case 8350:
			return 10924;
		case 111: // ice giant
		case 112:
		case 117:
			return 4668;
		case 93:
			return 5503;
		case 5666:
			return 5898;
		case 110:
			return 4673;
		case 8133: // Corporeal beast
		case 3101: // Melee
		case 3102: // Range
		case 3103: // Mage
			return 10059;
			// GODWARS

		case 3247: // Hobgoblin
			return 6182;

		case 6270: // Cyclops
		case 6269: // Ice cyclops
		case 4291: // Cyclops
		case 4292: // Ice cyclops
			return 4653;

		case 6218: // Gorak
			return 4302;

		case 6212: // Werewolf
			return 6537;

		case 6276: // Spirtual Ranger
		case 6278: // Spirtual Mage
		case 6272: // Ork
		case 6274: // Ork
		case 6277: // Spirtual Warrior bandos
			return 4321;

		case 6230: // Spirtual Ranger
		case 6233: // Aviansie
		case 6239: // Aviansie
		case 6232: // Aviansie
		case 6231: // Spirtual Mage
		case 6229: // Spirtual Warrior arma

		case 6223: // Aviansie
		case 6225: // Spirtual Mage
		case 6227: // Spirtual Warrior arma
			return 6956;

		case 3248: // HellHound
			return 6576;

			// ENDS
			// sara gwd
		case 6247:
			return 6965;
		case 1265:
			return 1314;
		case 103:
			return 5542;
		case 655:
		case 104:
			return 5534;
		case 6248:
			return 6377;
		case 6250:
			return 7016;
		case 3491:
			return 3357;
		case 6222:
			return 6975;
		case 6203:
		case 6204:
		case 6206:
		case 6208:
			return 6946;
		case 3493:
			return 3503;
		case 2565:
			return 7011;
		case 3494:
			return 1752;
		case 3496:
			return 3509;
		case 6252:
			return 7011;
			// bandos gwd
		case 6261:
		case 6263:
		case 6265:
			return 6156;
		case 6260:
			return 7062;
		case 2550:
			return 7062;
		case 2892:
		case 2894:
			return 2865;
		case 1612: // banshee
			return 1524;
		case 2559:
		case 2560:
		case 2561:
			return 6956;
		case 2607:
			return 2607;
		case 2627:
			return 2620;
		case 2630:
			return 2627;
		case 2631:
			return 2630;
		case 2738:
			return 2627;
		case 2741:
			return 2638;
		case 2746:
			return 2638;
		case 2743:
			return 2055;	
		case 1540:
			return 2055;		
		case 2745:
			return 9279;

		case 3777:
		case 3778:
		case 3779:
		case 3780:
			return -1;

		case 3200:
			return 3147;

		case 2035: // spider
			return 5329;

		case 2033: // rat
			return 4935;

		case 2031: // bloodvel
			return 2073;

		case 101: // goblin
		case 3663:
			return 6190;

		case 81: // cow
			return 5851;

		case 41: // chicken
			return 5389;

		case 1338: // dagannoth
		case 1340:
		case 1342:
			return 1342;

		case 2881:
		case 2882:
		case 2883:
			return 2856;

		case 125: // ice warrior
			return 843;

		case 751:// Zombies!!
			return 6727;

		case 1626:
		case 1627:
		case 1628:
		case 1629:
		case 1630:
			return 1597;

		case 1616: // basilisk
			return 1548;

		case 1653: // hand
			return 1590;

		case 82:// demons
		case 83:
		case 84:
		case 10039:
			return 67;

		case 1605:// abby spec
			return 1508;

		case 51:// baby drags
		case 52:
		case 1589:
		case 3376:
			return 28;

		case 1610:
		case 1611:
			return 1518;

		case 1618:
		case 1619:
			return 1553;

		case 1620:
		case 1621:
			return 1563;

		case 2783:
			return 2733;

		case 1615:
			return 1538;

		case 1624:
			return 1558;

		case 1613:
			return 1530;

		case 1633:
		case 1634:
		case 1635:
		case 1636:
			return 1580;

		case 1648:
		case 1649:
		case 1650:
		case 1651:
		case 1652:
		case 1654:
		case 1655:
		case 1656:
		case 1657:
			return 1590;

		case 100:
		case 102:
			return 6182;

		case 105:
		case 106:
			return 4929;

		case 412:
		case 78:
			return 4917;

		case 122:
		case 123:
			return 167;

		case 58:
		case 59:
		case 60:
		case 61:
		case 62:
		case 63:
		case 64:
		case 134:
			return 5329;

		case 1153:
		case 1154:
		case 1156:
		case 1157:
			return 6230;

		case 118:
		case 119:
			return 102;

		case 50:// drags
		case 53:
		case 742:
		case 54:
		case 55:
		case 941:
		case 4677:
		case 1590:
		case 1591:
		case 1592:
			return 92;

		default:
			return 2304;
		}
	}

	/**
	 * 	ENUM VALUES:
		NORTH,
		NORTH_EAST,
		EAST,
		SOUTH_EAST,
		SOUTH,
		SOUTH_WEST,
		WEST,
		NORTH_WEST,
	 */
	public static Direction getDirectionToFace(NPC n) {
		switch(n.getId()) {
		case 494:
			if(n.getPosition().getX() >= 2650 && n.getPosition().getY() == 2651)
			return Direction.NORTH;
			if(n.getPosition().getX() == 3187 && n.getPosition().getY() >= 3400)
			return Direction.WEST;
			if(n.getPosition().getX() == 3098 && n.getPosition().getY() == 3492 || n.getPosition().getX() == 3096 && n.getPosition().getY() == 3492)
				return Direction.NORTH;
			if(n.getPosition().getX() == 3096 || n.getPosition().getX() == 2657 && n.getPosition().getY() >= 3280 && n.getPosition().getY() < 3287 || n.getPosition().getX() == 3191 && n.getPosition().getY() >= 3435 && n.getPosition().getY() <= 3443) //Edgeville bankers //Ardougne south bank 
				return Direction.WEST;
			if((n.getPosition().getX() >= 2807 && n.getPosition().getX() <= 2811 && n.getPosition().getY() == 3443) || (n.getPosition().getX() >= 2721 && n.getPosition().getX() <= 2729 && n.getPosition().getY() == 3495) || n.getPosition().getX() == 3208 && n.getPosition().getY() == 3222 && n.getPosition().getZ() == 2) //Lumbridge Castle banker, height 2
				return Direction.SOUTH;
			if(n.getPosition().getX() == 2203 && n.getPosition().getY() == 5345 || n.getPosition().getX() == 3215 && n.getPosition().getY() >= 3251 && n.getPosition().getY() <= 3255 || n.getPosition().getX() == 3090 && n.getPosition().getY() >= 3240 && n.getPosition().getY() < 3247 || n.getPosition().getX() == 2841 && n.getPosition().getY() == 3539 || n.getPosition().getX() == 3019 && n.getPosition().getY() == 9739 || n.getPosition().getX() == 3180 && n.getPosition().getY() >= 3436 && n.getPosition().getY() <= 3444 || n.getLocation() == Location.WARRIORS_GUILD) //Draynor Village bankers, Warriors guild banker and Mining guild banker
				return Direction.EAST;
			if(n.getPosition().getY() == 3330 && n.getPosition().getX() >= 2612 && n.getPosition().getX() < 2622 || n.getPosition().getY() == 3418 && n.getPosition().getX() >= 3251 && n.getPosition().getX() <= 3256) //Ardougne north bank
				return Direction.NORTH;
			break;
		case 6970:
			return Direction.EAST;
		case 6537: //Mandrith
			return Direction.EAST;
		case 2820: //Smith at Lumbridge
		case 570: //Gem merchant at Ardougne market
			return Direction.WEST;
		case 1861: //Ranged instructor
			return Direction.SOUTH;
		case 571: //Baker at Ardougne market
			if(n.getPosition().getX() == 2655 && n.getPosition().getY() == 3311)
				return Direction.EAST;
			else
				return Direction.WEST;
		case 530: //Shopkeeper at Ardougne market
			if(n.getPosition().getX() == 2663 && n.getPosition().getY() == 3316)
				return Direction.SOUTH;
			else
				return Direction.EAST;
		}
		return Direction.SOUTH;
	}


	public static int getMaximumFollow(NPC npc) {
		if(NPCData.CustomNPCData.forNpcId(npc.getId()) != null || npc.getId() == 13447 || NexMinion.nexMinion(npc.getId()))
			return 50; // Custom npcs
		switch(npc.getId()) {
		case 3200:
		case 8133:
			return 13;
		case 6260:
		case 6261:
		case 6263:
		case 6265:
		case 6222:
		case 6225:
		case 6227:
		case 6203:
		case 6223:
		case 6208:
		case 6206:
		case 6204:
		case 6250:
		case 6248:
		case 6247:
			return 16;
		case 14261:
			return 8;
		case 2060:
			return 30;
		default:
			return 6;
		}
	}
}
