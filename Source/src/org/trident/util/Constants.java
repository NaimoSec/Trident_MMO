package org.trident.util;

import org.trident.model.Position;

/**
 * Contains all constants that will be used throughout the server.
 * 
 * @author Gabbe
 */

public class Constants {
	
	/**
	 * The current game version
	 */
	public static final int GAME_VERSION = 8;

	/**
	 * The maximum amount of ground items allowed on the server.
	 */
	public static final int MAX_GROUND_ITEMS = 2000;

	/**
	 * The maximum amount of game objects allowed on the server.
	 */
	public static final int MAX_GAME_OBJECTS = 2000;

	/**
	 * The opcode sent when a game object is spawned.
	 */
	public static final int GAME_OBJECT_OPCODE = 151;

	/**
	 * The opcode sent when a ground item is spawned.
	 */
	public static final int GROUND_ITEM_OPCODE = 44;

	/**
	 * The default position an entity will be set to upon first login.
	 */
	public static final Position DEFAULT_POSITION = new Position(3094, 3503);

	/**
	 * The tab indexes.
	 */
	public static final int ATTACK_TAB = 0, SKILLS_TAB = 1, QUESTS_TAB = 2, ACHIEVEMENT_TAB = 14,
			INVENTORY_TAB = 3, EQUIPMENT_TAB = 4, PRAYER_TAB = 5, MAGIC_TAB = 6,
			SUMMONING_TAB = 13, FRIEND_TAB = 8, IGNORE_TAB = 9, CLAN_CHAT_TAB = 7,
			OPTIONS_TAB = 11, EMOTES_TAB = 12;

	/**
	 * The side bar interface id's in their respective order.
	 */
	public static final int[] SIDEBAR_INTERFACES = {
		2423, //attack
		31110, //Skills
		639, //Quest
		3213, //inventory
		27650, //equipment
		5608, //prayer
		1151, //magic spellbook
		29328, //clan chat
		5065, //friends
		5715, //ignore
		2449, //log out button
		904, //options
		147, //emote
		54017,
		173,//summoning
		15001, //achievements
	};

	/**
	 * Items that cannot be traded, sold or dopped.
	 */	
	public static final int[] untradeableItems = 
		{13661, 13262,
		6529, 6950, 1464, 2996, 2677, 2678, 2679, 2680, 2682, 
		2683, 2684, 2685, 2686, 2687, 2688, 2689, 2690, 
		6570, 12158, 12159, 12160, 12163, 12161, 12162,
		19143, 19149, 19146, 19157, 19162, 19152, 4155,
		8850, 10551, 8839, 8840, 8842, 11663, 11664, 
		11665, 3842, 3844, 3840, 8844, 8845, 8846, 8847, 
		8848, 8849, 8850, 10551, 6570, 7462, 7461, 7460, 
		7459, 7458, 7457, 7456, 7455, 7454, 7453, 8839, 
		8840, 8842, 11663, 11664, 11665, 10499, 9748, 
		9754, 9751, 9769, 9757, 9760, 9763, 9802, 9808,
		9784, 9799, 9805, 9781, 9796, 9793, 9775, 9772,
		9778, 9787, 9811, 9766, 9749, 9755, 9752, 9770, 
		9758, 9761, 9764, 9803, 9809, 9785, 9800, 9806, 
		9782, 9797, 9794, 9776, 9773, 9779, 9788, 9812, 
		9767, 9747, 9753, 9750, 9768, 9756, 9759, 9762,
		9801, 9807, 9783, 9798, 9804, 9780, 9795, 9792, 
		9774, 9771, 9777, 9786, 9810, 9765, 9948, 9949,
		9950, 12169, 12170, 12171, 20671, 14641, 14642,
		6188, 10954, 10956, 10958,
		3057, 3058, 3059, 3060, 3061,
		7594, 7592, 7593, 7595, 7596,
		14076, 14077, 14081,
		10840, 10836, 6858, 6859, 10837, 10838, 10839,
		9925, 9924, 9923, 9922, 9921,
		4084, 4565, 20046, 20044, 20045,
		1050, 14595, 14603, 14602, 14605, 	11789,
		19708, 19706, 19707,
		4860, 4866, 4872, 4878, 4884, 4896, 4890, 4896, 4902,
		4932, 4938, 4944, 4950, 4908, 4914, 4920, 4926, 4956,
		4926, 4968, 4994, 4980, 4986, 4992, 4998,
		18778, 18779, 18780, 18781,
		13450, 13444, 13405, 15502, 
		10548, 10549, 10550, 10551, 10555, 10552, 10553, 2412, 2413, 2414,
		20747, 
		18365, 18373, 18371, 15246, 12964, 12971, 12978,
		18351, 18349, 18353, 18355, 18357, 18359,//Chaotic
		757,
		13855, 13848, 13857, 13856, 13854, 13853, 13852, 13851, 13850, 5509, 13653, 19111
		};

	/*
	 * Items that should be allowed to trade but not sell.
	 */
	public static int unsellable[] = new int[] {
		1038, 1040,	1042, 1044, 1046, 1048, //Phats
		1053, 1055, 1057, //Hween
		1050, //Santa
		19780, //Korasi's
		20671, //Brackish
		20135, 20139, 20143, 20147, 20151, 20155, 20159, 20163, 20167, //Nex armors
		6570, //Fire cape
		19143, 19146, 19149, //God bows
		8844, 8845, 8846, 8847, 8848, 8849, 8850, 13262, //Defenders
		20135, 20139, 20143, 20147, 20151, 20155, 20159, 20163, 20167, // Torva, pernix, virtus
		13746, 13748, 13750, 13752, 13738, 13740, 13742, 13744, //Spirit shields & Sigil
		11694, 11696, 11698, 11700, 11702, 11704, 11706, 11708, 11686, 11688, 11690, 11692, 11710, 11712, 11714, //Godswords, hilts, pieces
		15486, //sol
		11730, //ss
		11718, 11720, 11722, //armadyl
		11724, 11726, 11728, //bandos
		11286, 11283, //dfs & visage
		14472, 14474, 14476, 14479, //dragon pieces and plate
		14484, //dragon claws
		13887, 13888, 13893, 13895, 13899, 13901, 13905, 13907, 13911, 13913, 13917, 13919, 13923, 13925, 13929, 13931, //Vesta's																																														
		13884, 13886, 13890, 13892, 13896, 13898, 13902, 13904, 13908, 13910, 13914, 13916, 13920, 13922, 13926, 13928, //Statius's
		13870, 13872, 13873, 13875, 13876, 13878, 13879, 13880, 13881, 13882, 13883, 13944, 13946, 13947, 13949, 13950, 13952, 13953, 13954, 13955, 13956, 13957, //Morrigan's
		13858, 13860, 13861, 13863, 13864, 13866, 13867, 13869, 13932, 13934, 13935, 13937, 13938, 13940, 13941, 13943, //Zuriel's
		20147, 20149, 20151, 20153, 20155, 20157, //Pernix
		20159, 20161, 20163, 20165, 20167, 20169, //Virtus
		20135, 20137, 20139, 20141, 20143, 20145, //Torva
		25, //Red vine worm
		11335, //D full helm
		6731, 6733, 6735, //warrior ring, seers ring, archer ring
		962, //Christmas Cracker
		21787, 21790, 21793, //Steadfast, glaiven, ragefire
		20674,//Something something
		13958,13961,13964,13967,13970,13973,13976,13979,13982,13985,13988,13908,13914,13926,13911,13917,13923,13929,13932,13935,13938,13941,13944,13947,13950,13953,13957,13845,13846,13847,13848,13849,13850,13851,13852,13853,13854,13855,13856,13857, //Le corrupted items




	};
}
