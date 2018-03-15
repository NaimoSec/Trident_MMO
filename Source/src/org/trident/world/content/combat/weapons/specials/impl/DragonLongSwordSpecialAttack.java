package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;

public class DragonLongSwordSpecialAttack extends SpecialAttack {

	@Override
	public Animation getAnimation() {
		return ANIMATION;
	}

	@Override
	public Graphic getGraphic() {
		return GRAPHIC;
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
		return 1.1;
	}
	
	@Override
	public double getMultiplier() {
		return 1.20;
	}
	
	@Override
	public boolean isDoubleHit() {
		return false;
	}
	
	private static final Animation ANIMATION = new Animation(12033);
	
	private static final Graphic GRAPHIC = new Graphic(2117, GraphicHeight.HIGH);
}
