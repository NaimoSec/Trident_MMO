

public class Configuration {
	
	public final static String HOST = "144.217.95.33"; // gs1.voxyde.com
	public final static String JAGGRAB_HOST = "0.0.0.0"; // gs2.voxyde.com /202.68.164.172
	public final static int CHARACTERS_SEPARATOR_WIDTH = 110;
	public final static double CLIENT_VERSION = 2.0;
	
	public final static String CLIENT_NAME = "Trident";
	
	/**
	 * The NPC bits.
	 * 12 = 317/377
	 * 14 = 474+
	 * 16 = 600+
	 */
	public final static int NPC_BITS = 18;

	/*
	 * Update-server enabled?
	 * Redone by Gabbe
	 */
	public static final boolean JAGCACHED_ENABLED = false;
	
	public static int[] ITEMS_WITH_BLACK = {
		1277, 560, 559, 1077, 1089, 1125, 1149, 1153, 1155, 1157, 1159, 1161, 1163,
		1165, 1279, 1313, 1327, 2349, 2351, 2353, 2355, 2357, 2359,
		2361, 2363, 2619, 2627, 2657, 2665, 2673, 3140, 3486, 6568, 11335, 12158, 1261, 
		12162, 12163, 12164, 12165, 12166, 12167, 12168, 15332, 15333, 15334, 15335,
		13675, 14479, 18510, 19337, 19342, 19347, 19407, 19437
	};

	public static int FOG_BEGIN_DEPTH = 2300;
	public static int FOG_END_DEPTH = 2900;
}
