

/**
 * This enum contains data used as constants for skill configurations
 * such as experience rates, string id's for interface updating.
 */
public enum Skill {
	
	ATTACK(75, 
			new int[] {31114, 31115, 31113, 6247}),
	DEFENCE(70, 
			new int[] {31124, 31125, 31123, 6253}),
	STRENGTH(75, 
			new int[] {31119, 31120, 31118, 6206}),
	CONSTITUTION(15, 
			new int[] {31159, 31160, 31158, 6216}),
	RANGED(60, 
			new int[] {31129, 31130, 31128, 4443}),
	PRAYER(50, 
			new int[] {31134, 31135, 31133, 6242}),
	MAGIC(75, 
			new int[] {31139, 31140, 31138, 6211}),
	COOKING(150, 
			new int[] {31219, 31220, 31218, 6226}),
	WOODCUTTING(150, 
			new int[] {31229, 31230, 31228, 4272}),
	FLETCHING(175, 
			new int[] {31184, 31185, 31183, 6231}),
	FISHING(150, 
			new int[] {31214, 31215, 31213, 6258}),
	FIREMAKING(200, 
			new int[] {31224, 31225, 31223, 4282}),
	CRAFTING(250, 
			new int[] {31179, 31180, 31178, 6263}),
	SMITHING(300, 
			new int[] {31209, 31210, 31208, 6221}),
	MINING(150, 
			new int[] {31204, 31205, 31203, 4416}),
	HERBLORE(250, 
			new int[] {31169, 31170, 31168, 6237}),
	AGILITY(300,
			new int[] {31164, 31165, 31163, 4277}),
	THIEVING(300, 
			new int[] {31174, 31175, 31173, 4261}),
	SLAYER(400,
			new int[] {31189, 31190, 31188, 12122}),
	FARMING(400, 
			new int[] {31234, 31235, 31233, 5267}),
	RUNECRAFTING(400,
			new int[] {31144, 31145, 31143, 4267}),
	CONSTRUCTION(175, 
			new int[] {31149, 31150, 31148, 7267}),
	HUNTER(150, 
			new int[] {31194, 31195, 31193, 8267}),
	SUMMONING(200, 
			new int[] {31239, 31240, 31238, 9267}),
	DUNGEONEERING(200, 
			new int[] {31154, 31155, 31153, 10267});
	
	private Skill(int multiplier, int[] updateStrings) {
		this.multiplier = multiplier;
		this.updateStrings = updateStrings;
	}
	
	/**
	 * The amount the experience will be multiplied by
	 * when being added to the player's skills.
	 */
	private int multiplier;
	
	/**
	 * The string child id's on the skills tab to update
	 * upon login and level up.
	 */
	int[] updateStrings;
	
	/**
	 * Gets the Skills experience multiplier.
	 * @return multiplier.
	 */
	public int getExperienceMultiplier() {
		return multiplier;
	}

	/**
	 * Gets the Skill's name.
	 * @return	The skill's name in a lower case format.
	 */
	public String getName() {
		return toString().toLowerCase();
	}
	
	/**
	 * Gets the Skill value which ordinal() matches {@code id}.
	 * @param id	The index of the skill to fetch Skill instance for.
	 * @return		The Skill instance.
	 */
	public static Skill forId(int id) {
		for (Skill skill : Skill.values()) {
			if (skill.ordinal() == id) {
				return skill;
			}
		}
		return null;
	}
}