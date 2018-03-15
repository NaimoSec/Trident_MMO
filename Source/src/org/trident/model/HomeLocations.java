package org.trident.model;


	/**
	 * Represents a player's home location
	 * 
	 * @author Gabbe
	 */
	public enum HomeLocations {
		LUMBRIDGE,
		VARROCK,
		FALADOR,
		CAMELOT,
		ARDOUGNE,
		NEITZNOT;
			
		/**
		 * Gets the location of an ordinal()
		 * 
		 * @param id	The id (ordinal()) of the rank.
		 * @return		Location.
		 */
		public static HomeLocations forId(int id) {
			for (HomeLocations locations : HomeLocations.values()) {
				if (locations.ordinal() == id) {
					return locations;
				}
			}
			return null;
		}
}
