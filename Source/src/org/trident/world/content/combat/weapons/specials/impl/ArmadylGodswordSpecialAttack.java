package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;

/**
 * Represents a powerful and accurate armadyl godsword special attack.
 * 
 * @author relex lawl
 */

public class ArmadylGodswordSpecialAttack extends SpecialAttack {

	@Override
	public Animation getAnimation() {
		return ANIMATION;
	}

	@Override
	public Graphic getGraphic() {
		return GRAPHIC;
	}

	@Override
	public double getSpecialAmount() {
		return 5;
	}
	
	@Override
	public double getAccuracy() {
		return 1.73;
	}
	
	@Override
	public double getMultiplier() {
		return 1.375;
	}
	
	
	private static final Animation ANIMATION = new Animation(11989);
	
	private static final Graphic GRAPHIC = new Graphic(2113);
}
