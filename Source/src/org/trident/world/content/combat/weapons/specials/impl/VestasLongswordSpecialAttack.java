package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;

public class VestasLongswordSpecialAttack extends SpecialAttack {

	@Override
	public Animation getAnimation() {
		return ANIMATION;
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}
	
	@Override
	public boolean selfGraphic() {
		return true;
	}

	@Override
	public double getSpecialAmount() {
		return 2.5;
	}
	
	@Override
	public double getAccuracy() {
		return 1.2;
	}
	
	@Override
	public double getMultiplier() {
		return 1.20;
	}
	
	private static final Animation ANIMATION = new Animation(10502);
}
