package org.trident.model;

/**
 * @author Gabbe
 */

public class DwarfCannon {

	public DwarfCannon(int ownerIndex, Position pos) {
		this.ownerIndex = ownerIndex;
		this.pos = pos;
	}
	
	private int ownerIndex;
	private Position pos;
	private int cannonballs = 0;
	private boolean cannonFiring = false;
	private int rotations = 0;
	
	public int getOwnerIndex() {
		return this.ownerIndex;
	}

	public Position getPosition() {
		return this.pos;
	}
	
	public int getCannonballs() {
		return this.cannonballs;
	}
	
	public void setCannonballs(int cannonballs) {
		this.cannonballs = cannonballs;
	}
	
	public boolean cannonFiring() {
		return this.cannonFiring;
	}
	
	public void setCannonFiring(boolean firing) {
		this.cannonFiring = firing;
	}
	
	public int getRotations() {
		return this.rotations;
	}
	
	public void setRotations(int rotations) {
		this.rotations = rotations;
	}
	
	public void addRotation(int amount) {
		this.rotations += amount;
	}
	
}
