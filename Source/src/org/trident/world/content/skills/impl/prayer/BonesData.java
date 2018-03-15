package org.trident.world.content.skills.impl.prayer;

public enum BonesData {

	 IMPIOUS_ASHES(20264, 100, 200),
	 BONES(526, 200, 400),
	 WOLF_BONES(2859, 200, 400),
	 BURNT_BONES(528, 200, 400),
	 BAT_BONES(530, 200, 400),
	 ACCURSED_ASHES(20266, 600, 1200),
	 BIG_BONES(532, 800, 1600),
	 CURVED_BONE(10977, 1500, 3000),
	 LONG_BONES(10976, 1500, 3000),
	 JOGRE_BONE(3125, 1200, 2400),
	 ZOGRE_BONES(4812, 1500, 3000),
	 SHAIKAHAN_BONES(3123, 1200, 2400),
	 BABYDRAGON_BONES(534, 1600, 3200),
	 INFERNAL_ASHES(20269, 1000, 2000),
	 DRAGON_BONES(536, 2500, 5000),
	 FAYRG_BONES(4830, 2700, 5400),
	 RAURG_BONES(4832, 2800, 5600),
	 DAGANNOTH_BONES(6729, 3000, 6000),
	 OURG_BONES(14793, 3400, 6800),
	 FROSTDRAGON_BONES(18830, 4200, 8400);
	
	BonesData(int boneId, int buryXP, int altarXP) {
		this.boneId = boneId;
		this.buryXP = buryXP;
		this.altarXP = altarXP;
	}

	private int boneId;
	private int buryXP;
	private int altarXP;
	
	public int getBoneID() {
		return this.boneId;
	}
	
	public int getBuryingXP() {
		return this.buryXP;
	}
	
	public int getAltarXP() {
		return altarXP;
	}
	
	public static BonesData forId(int bone) {
		for(BonesData prayerData : BonesData.values()) {
			if(prayerData.getBoneID() == bone) {
				return prayerData;
			}
		}
		return null;
	}
	
}
