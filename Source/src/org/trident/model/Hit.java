package org.trident.model;

import org.trident.util.Stopwatch;

public class Hit {

	public Hit(int damage, CombatIcon combatIcon, Hitmask hitmask) {
		this.damage = damage;
		this.combatIcon = combatIcon;
		this.hitmask = hitmask;
		this.absorption = 0;
		this.stopwatch = new Stopwatch().reset();
	}

	public Hit(int damage, int absorption, CombatIcon combatIcon, Hitmask hitmask) {
		this.damage = damage;
		this.absorption = absorption;
		this.combatIcon = combatIcon;
		this.hitmask = hitmask;
		this.stopwatch = new Stopwatch().reset();
	}

	private int damage;

	private final Stopwatch stopwatch;

	private final CombatIcon combatIcon;

	private Hitmask hitmask;

	private final int absorption;

	public int getDamage() {
		return damage;
	}

	public Hit setDamage(int damage) {
		this.damage = damage;
		this.stopwatch.reset();
		return this;
	}

	public void incrementDamage(int damage) {
		this.damage += damage;
		this.stopwatch.reset();
	}
	
	public Stopwatch getStopwatch() {
		return stopwatch;
	}

	public CombatIcon getCombatIcon() {
		return combatIcon;
	}

	public Hitmask getHitmask() {
		return hitmask;
	}

	public Hitmask setHitmask(Hitmask mask) {
		return this.hitmask = mask;
	}

	public int getAbsorption() {
		return absorption;
	}

}
