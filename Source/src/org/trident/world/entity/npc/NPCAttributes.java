package org.trident.world.entity.npc;


/**
 * Holds an NPC's properties such as levels and other fields
 * @author Gabbe
 */
public class NPCAttributes {

	private int constitution = 100;
	private int attackLevel = 10;
	private int strengthLevel = 10;
	private int defenceLevel = 10;
	private int absorbMelee;
	private int absorbRanged;
	private int absorbMagic;
	private int maxHit = 10;
	private boolean attackable, aggressive, respawn;
	private int walkingDistance;
	private int attackSpeed = 5;
	private boolean summoningNpc;
	private boolean walkingHome;
	private boolean isDying, isDead;
	private boolean hasHealed; //Mainly used for bosses such as Nomad
	private int transformationId = -1;
	private boolean visible = true;
	
	public NPCAttributes() {
		
	}
	
	public NPCAttributes setConstitution(int constitution) {
		this.constitution = constitution;
		return this;
	}
	
	public int getConstitution() {
		return constitution;
	}
	
	public NPCAttributes setAttackLevel(int attackLevel) {
		this.attackLevel = attackLevel;
		return this;
	}
	
	public int getAttackLevel() {
		return attackLevel;
	}
	
	public NPCAttributes setStrengthLevel(int strengthLevel) {
		this.strengthLevel = strengthLevel;
		return this;
	}

	public int getStrengthLevel() {
		return strengthLevel;
	}
	
	public NPCAttributes setDefenceLevel(int defenceLevel) {
		this.defenceLevel = defenceLevel;
		return this;
	}
	
	public int getDefenceLevel() {
		return defenceLevel;
	}

	public NPCAttributes setAbsorbMelee(int absorbMelee) {
		this.absorbMelee = absorbMelee;
		return this;
	}
	
	public int getAbsorbMelee() {
		return absorbMelee;
	}
	
	public NPCAttributes setAbsorbRanged(int absorbRanged) {
		this.absorbRanged = absorbRanged;
		return this;
	}

	public int getAbsorbRanged() {
		return absorbRanged;
	}

	public NPCAttributes setAbsorbMagic(int absorbMagic) {
		this.absorbMagic = absorbMagic;
		return this;
	}
	
	public int getAbsorbMagic() {
		return absorbMagic;
	}

	public NPCAttributes setMaxHit(int maxHit) {
		this.maxHit = maxHit;
		return this;
	}
	
	public int getMaxHit() {
		return maxHit;
	}
	
	public NPCAttributes setAttackable(boolean attackable) {
		this.attackable = attackable;
		return this;
	}
	
	public boolean isAttackable() {
		return attackable;
	}
	
	public NPCAttributes setAggressive(boolean aggressive) {
		this.aggressive = aggressive;
		return this;
	}
	
	public boolean isAggressive() {
		return aggressive;
	}
	
	public NPCAttributes setRespawn(boolean respawn) {
		this.respawn = respawn;
		return this;
	}
	
	public boolean shouldRespawn() {
		return respawn;
	}
	
	public NPCAttributes setWalkingDistance(int walkingDistance) {
		this.walkingDistance = walkingDistance;
		return this;
	}
	
	public int getWalkingDistance() {
		return walkingDistance;
	}
	
	public NPCAttributes setAttackSpeed(int attackSpeed) {
		this.attackSpeed = attackSpeed;
		return this;
	}
	
	public int getAttackSpeed() {
		return attackSpeed;
	}
	
	public NPCAttributes setSummoningNpc(boolean summoningNpc) {
		this.summoningNpc = summoningNpc;
		return this;
	}
	
	public boolean isSummoningNpc() {
		return this.summoningNpc;
	}

	public NPCAttributes setWalkingHome(boolean walkingHome) {
		this.walkingHome = walkingHome;
		return this;
	}
	
	public boolean isWalkingHome() {
		return this.walkingHome;
	}
	
	public NPCAttributes setDying(boolean dying) {
		this.isDying = dying;
		return this;
	}
	
	public boolean isDying() {
		return this.isDying;
	}
	
	public NPCAttributes setDead(boolean isDead) {
		this.isDead = isDead;
		return this;
	}
	
	public NPCAttributes setHealed(boolean hasHealed) {
		this.hasHealed = hasHealed;
		return this;
	}
	
	public boolean isDead() {
		return isDead;
	}
	
	public boolean hasHealed() {
		return hasHealed;
	}
	
	public int getTransformationId() {
		return transformationId;
	}

	public NPCAttributes setTransformationId(int transformationId) {
		this.transformationId = transformationId;
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	public NPCAttributes setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
	
	public NPCAttributes copy() {
		return new NPCAttributes().setConstitution(constitution).setAttackLevel(attackLevel).setDefenceLevel(defenceLevel).setStrengthLevel(strengthLevel).setAbsorbMelee(absorbMelee).setAbsorbRanged(absorbRanged).setAbsorbMagic(absorbMagic).setMaxHit(maxHit).setAttackable(attackable).setAggressive(aggressive).setRespawn(respawn).setSummoningNpc(summoningNpc).setDying(isDying).setHealed(hasHealed).setWalkingHome(walkingHome).setWalkingDistance(walkingDistance).setAttackSpeed(attackSpeed).setDead(isDead).setVisible(visible).setTransformationId(transformationId);
	}
}
