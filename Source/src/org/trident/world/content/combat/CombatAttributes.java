package org.trident.world.content.combat;

import java.util.HashMap;
import java.util.Map;

import org.trident.model.Hit;
import org.trident.world.content.combat.weapons.WeaponHandler.AttackStyle;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * FOR GAMECHARACTERS (npcs and players)
 * Player's combat attributes are located in 'PlayerCombatAttributes'
 * Edited by Gabbe
 */
public class CombatAttributes {

	private GameCharacter currentEnemy;

	private GameCharacter lastAttacker;
	
    private Map<Player, Hit> hitMap = new HashMap<>();

	private AttackType attackType = AttackType.MELEE;

	private boolean isAttacking;

	private boolean autoRetaliation;

	private int attackDelay;

	private boolean staffOfLightEffect;

	private boolean stunned;

	private long lastDamageReceived;

	private int currentPoisonDmg;

	private int poisonImmunity;

	private GameCharacter spawnedFor;

	private double[] leechedBonuses = new double[7];
	
	private int teleportBlockDelay;
	
	private int freezeDelay;
	
	private boolean targeted;
	
	
	public boolean isAutoRetaliation() {
		return autoRetaliation;
	}

	public CombatAttributes setAutoRetaliation(boolean autoRetaliation) {
		this.autoRetaliation = autoRetaliation;
		return this;
	}

	public int getAttackDelay() {
		return attackDelay;
	}

	public CombatAttributes setAttackDelay(int attackDelay) {
		this.attackDelay = attackDelay;
		return this;
	}

	public boolean hasStaffOfLightEffect() {
		return staffOfLightEffect;
	}

	public CombatAttributes setStaffOfLightEffect(boolean staffOfLightEffect) {
		this.staffOfLightEffect = staffOfLightEffect;
		return this;
	}

	public AttackType getAttackType() {
		return attackType;
	}

	public CombatAttributes setAttackType(AttackType attackType) {
		this.attackType = attackType;
		return this;
	}

	private AttackStyle attackStyle = AttackStyle.PUNCH;

	public AttackStyle getAttackStyle() {
		return this.attackStyle;
	}

	public CombatAttributes setAttackStyle(AttackStyle attackStyle) {
		this.attackStyle = attackStyle;
		return this;
	}

	public long lastAid;

	public long getLastAid() {
		return this.lastAid;
	}

	public void setLastAid(long aid) {
		this.lastAid = aid;
	}

	public GameCharacter getCurrentEnemy() {
		return currentEnemy;
	}

	public CombatAttributes setCurrentEnemy(GameCharacter currentEnemy) {
		this.currentEnemy = currentEnemy;
		return this;
	}

	public GameCharacter getLastAttacker() {
		return lastAttacker;
	}

	public CombatAttributes setLastAttacker(GameCharacter lastAttacker) {
		this.lastAttacker = lastAttacker;
		return this;
	}

	public CombatAttributes setAttacking(boolean isAttacking) {
		this.isAttacking = isAttacking;
		return this;
	}

	public boolean isAttacking() {
		return isAttacking;
	}

	public CombatAttributes setStunned(boolean stunned) {
		this.stunned = stunned;
		return this;
	}

	public boolean isStunned() {
		return stunned;
	}

	public CombatAttributes setLastDamageReceived(long lastDamageReceived) {
		this.lastDamageReceived = lastDamageReceived;
		return this;
	}

	public long getLastDamageReceived() {
		return lastDamageReceived;
	}

	public CombatAttributes setCurrentPoisonDamage(int currentPoisonDmg) {
		this.currentPoisonDmg = currentPoisonDmg;
		return this;
	}

	public int getCurrentPoisonDamage() {
		return currentPoisonDmg;
	}

	public CombatAttributes setPoisonImmunity(int seconds) {
		 this.poisonImmunity = seconds;
		 return this;
	}

	public int getPoisonImmunity() {
		return poisonImmunity;
	}
	
	public CombatAttributes setSpawnedFor(GameCharacter spawnedFor) {
		 this.spawnedFor = spawnedFor;
		 return this;
	}
	
	public GameCharacter getSpawnedFor() {
		return spawnedFor;
	}
	
	public double[] getLeechedBonuses() {
		return leechedBonuses;
	}
	
	public int getTeleportBlockDelay() {
		return this.teleportBlockDelay;
	}
	
	public CombatAttributes setTeleportBlockDelay(int delay) {
		this.teleportBlockDelay = delay;
		return this;
	}

	public int getFreezeDelay() {
		return this.freezeDelay;
	}
	
	public CombatAttributes setFreezeDelay(int freezeDelay) {
		this.freezeDelay = freezeDelay;
		return this;
	}
	
	public boolean isTargeted() {
		return targeted;
	}
	
	public CombatAttributes setTargeted(boolean targeted) {
		this.targeted = targeted;
		return this;
	}
	
	public Map<Player, Hit> getHitMap() {
		return hitMap;
	}
}
