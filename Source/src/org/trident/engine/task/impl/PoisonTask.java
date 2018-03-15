package org.trident.engine.task.impl;

import org.trident.engine.task.Task;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.world.entity.GameCharacter;

/**
 * Handles poison
 * 
 * @author Gabbe
 */

public class PoisonTask extends Task {

	public PoisonTask(GameCharacter c, int timer) {
		super(timer, c, false);
		this.character = c;
	}

	private GameCharacter character;

	@Override
	public void execute() {
		if(character == null || character.getConstitution() <= 0 || character.getCombatAttributes().getPoisonImmunity() > 0 || character.getCombatAttributes().getCurrentPoisonDamage() <= 0) {
			if(character != null)
				character.getCombatAttributes().setCurrentPoisonDamage(0);
			stop();
			return;
		}
		int currentPoisonDamage = character.getCombatAttributes().getCurrentPoisonDamage();
		character.setDamage(new Damage(new Hit((currentPoisonDamage * 10), CombatIcon.NONE, Hitmask.DARK_GREEN)));
		character.getCombatAttributes().setLastDamageReceived(System.currentTimeMillis()).setCurrentPoisonDamage(currentPoisonDamage-1);
	}
	
	@Override
	public void stop() {
		setEventRunning(false);
	}
}
