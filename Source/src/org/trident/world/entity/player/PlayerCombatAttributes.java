package org.trident.world.entity.player;
import org.trident.world.content.combat.combatdata.magic.Magic;
import org.trident.world.content.combat.combatdata.ranged.RangedData.RangedWeaponData;
import org.trident.world.content.combat.pvp.PkRewardSystem;
import org.trident.world.content.combat.pvp.BountyHunter.BountyHunterAttributes;
import org.trident.world.content.combat.weapons.WeaponHandler.AttackStyle;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;

public class PlayerCombatAttributes {
	
	public PlayerCombatAttributes(Player p) { 
		pkRewardSystem = new PkRewardSystem(p);
		bountyHunterAttributes = new BountyHunterAttributes();
	}

	private BountyHunterAttributes bountyHunterAttributes;
	private PkRewardSystem pkRewardSystem;
	private AttackStyle attackStyle = AttackStyle.PUNCH;
	private RangedWeaponData rangedWeaponData;
	private Magic magic = new Magic();
	private SpecialAttack specialAttack;
	private boolean usingSpecialAttack;
	private boolean recoveringSpecialAttack;
	private double specialAttackAmount = 10;
	private boolean skulled;
	private int kills;
	private int deaths;
	private int wildernessLevel;
	private boolean hasVengeance;
	private long lastVengeanceCast;
	private int specialAttackBarId;
	private boolean threwChinchompa;
	
	public BountyHunterAttributes getBountyHunterAttributes() {
		return bountyHunterAttributes;
	}
	
	public PkRewardSystem getPkRewardSystem() {
		return pkRewardSystem;
	}
	
	public AttackStyle getAttackStyle() {
		return this.attackStyle;
	}

	public void setAttackStyle(AttackStyle attackStyle) {
		this.attackStyle = attackStyle;
	}
	
	public PlayerCombatAttributes setRangedWeaponData(RangedWeaponData data) {
		this.rangedWeaponData = data;
		return this;
	}

	public RangedWeaponData getRangedWeaponData() {
		return rangedWeaponData;
	}

	public Magic getMagic() {
		return magic;
	}
	
	public boolean isUsingSpecialAttack() {
		return usingSpecialAttack;
	}

	public PlayerCombatAttributes setUsingSpecialAttack(boolean usingSpecialAttack) {
		this.usingSpecialAttack = usingSpecialAttack;
		return this;
	}

	public boolean isRecoveringSpecialAttack() {
		return recoveringSpecialAttack;
	}

	public PlayerCombatAttributes setRecoveringSpecialAttack(boolean recoveringSpecialAttack) {
		this.recoveringSpecialAttack = recoveringSpecialAttack;
		return this;
	}
	
	public double getSpecialAttackAmount() {
		return specialAttackAmount;
	}

	public PlayerCombatAttributes setSpecialAttackAmount(double specialAttackAmount) {
		this.specialAttackAmount = specialAttackAmount;
		return this;
	}
	
	public boolean isSkulled() {
		return skulled;
	}
	
	public void setSkulled(boolean skulled) {
		this.skulled = skulled;
	}
	
	public int getKills() {
		return kills;
	}
	
	public void setKills(int kills) {
		this.kills = kills;
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	
	public int getWildernessLevel() {
		return wildernessLevel;
	}
	
	public void setWildernessLevel(int wildernessLevel) {
		this.wildernessLevel = wildernessLevel;
	}
	
	public boolean hasVengeance() {
		return hasVengeance;
	}
	
	public PlayerCombatAttributes setVengeance(boolean hasVengeance) {
		this.hasVengeance = hasVengeance;
		return this;
	}
	
	public long getLastVengeanceCast() {
		return lastVengeanceCast;
	}
	
	public PlayerCombatAttributes setLastVengeanceCast(long lastVengeanceCast) {
		this.lastVengeanceCast = lastVengeanceCast;
		return this;
	}
	
	public int getSpecialAttackBarId() {
		return specialAttackBarId;
	}
	
	public void setSpecialAttackBarId(int specialAttackBarId) {
		this.specialAttackBarId = specialAttackBarId;
	}
	
	public SpecialAttack getSpecialAttack() {
		return specialAttack;
	}
	
	public void setSpecialAttack(SpecialAttack specialAttack) {
		this.specialAttack = specialAttack;
	}
	
	public boolean threwChinchompa() {
		return threwChinchompa;
	}
	
	public void setThrewChinchompa(boolean threwChinchompa) {
		this.threwChinchompa = threwChinchompa;
	}
}
