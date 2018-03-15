package org.trident.model;

/**
 * Represents a player's privilege rights.
 * @author Gabbe
 */

public enum PlayerRights {

	/*
	 * A regular member of the server.
	 */
	PLAYER(-1, null),
	/*
	 * A moderator who has more privilege than other regular members and donators.
	 */
	MODERATOR(-1, "<col=20B2AA><shad=0>"),

	/*
	 * The second-highest-privileged member of the server.
	 */
	ADMINISTRATOR(-1, "<col=FFFF64><shad=0>"),

	/*
	 * The highest-privileged member of the server
	 */
	OWNER(-1, "<col=B40404>"),

	/*
	 * The Developer of the server, has same rights as the owner.
	 */
	DEVELOPER(-1, "<shad=B40404>"),


	/*
	 * A member who has donated to the server. 
	 *Bronze // donator
	 *Silzer // super
	 *Gold // extreme
	 *Platium // legendary
	 *Diamond // uber
	 */
	DONATOR(60, "<shad=FF7F00>"),
	SUPER_DONATOR(40, "<col=E6E8FA>"),
 	EXTREME_DONATOR(20, "<col=D9D919><shad=0>"),
	LEGENDARY_DONATOR(20, "<shad=697998>"),
	UBER_DONATOR(10, "<shad=0EBFE9>"),

	/*
	 * A member who has the ability to help people better.
	 */
	SUPPORT(-1, "<col=FF0000><shad=0>"),

	/*
	 * A member who has been with the server for a long time.
	 */
	VETERAN(30, "<col=CD661D>");

	PlayerRights(int yellDelaySeconds, String yellHexColorPrefix) {
		this.yellDelay = yellDelaySeconds;
		this.yellHexColorPrefix = yellHexColorPrefix;
	}
	
	/*
	 * The yell delay for the rank
	 * The amount of seconds the player with the specified rank must wait before sending another yell message.
	 */
	private int yellDelay;
	
	public int getYellDelay() {
		return yellDelay;
	}
	
	/*
	 * The player's yell message prefix.
	 * Color and shadowing.
	 */
	private String yellHexColorPrefix;
	
	public String getYellPrefix() {
		return yellHexColorPrefix;
	}
	
	/**
	 * Gets the rank for a certain id.
	 * 
	 * @param id	The id (ordinal()) of the rank.
	 * @return		rights.
	 */
	public static PlayerRights forId(int id) {
		for (PlayerRights rights : PlayerRights.values()) {
			if (rights.ordinal() == id) {
				return rights;
			}
		}
		return null;
	}

}
