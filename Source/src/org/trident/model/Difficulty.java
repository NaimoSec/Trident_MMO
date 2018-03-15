package org.trident.model;

public enum Difficulty {

	EASY,
	NORMAL,
	HARD,
	IRONMAN,
	HARDCORE_IRONMAN;
	
	public static Difficulty forId(int id) {
		for(Difficulty difficulty : Difficulty.values()) {
			if(difficulty != null && difficulty.ordinal() == id)
				return difficulty;
		}
		return null;
	}
}
