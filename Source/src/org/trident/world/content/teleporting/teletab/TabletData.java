package org.trident.world.content.teleporting.teletab;

import java.util.HashMap;
import java.util.Map;

/**
 * All relevant data for teleport tabs.
 * @author Jacob Ford
 *
 */
public enum TabletData {

	VARROCK(8007, 3213, 3424), 
	LUMBRIDGE(8008, 3222, 3218), 
	FALADOR(8009,2964, 3379), 
	CAMELOT(8010, 2757, 3477), 
	ARDOUGNE(8011, 2661, 3305), 
	WATCHTOWER(8012, 2549, 3112),
	ASTRAL(13611, 2154, 3868);

	/**
	 * The tab id.
	 */
	private int tabletId;

	/**
	 * The x-axis.
	 */
	private int posX;

	/**
	 * The y-axis.
	 */
	private int posY;
	
	/**
	 * A map of tab ids.
	 */
	private static Map<Integer, TabletData> tablets = new HashMap<Integer, TabletData>();

	/**
	 * Populates the tablet map.
	 */
	static {
		for (TabletData tab : TabletData.values()) {
			tablets.put(tab.getTabletId(), tab);
		}
	}
	
	/**
	 * Gets a tab by an item id.
	 * @param item The item id.
	 */
	public static TabletData forId(int item) {
		return tablets.get(item);
		
		
	}
	
	/**
	 * Teleports
	 * @param tabletId The tab.
	 * @param posX The x-axis.
	 * @param posY The y-axis.
	 */
	private TabletData(int tabletId, int posX, int posY) {
		this.tabletId = tabletId;
		this.posX = posX;
		this.posY = posY;
	}

	/**
	 * Gets the tab id
	 * 
	 * @return The tab.
	 */
	int getTabletId() {
		return tabletId;
	}

	/**
	 * Gets the x-axis.
	 * 
	 * @return The x-axis.
	 */
	int getPostionX() {
		return posX;
	}

	/**
	 * Gets the y-axis.
	 * 
	 * @return The y-axis.
	 */
	int getPostionY() {
		return posY;
	}

}