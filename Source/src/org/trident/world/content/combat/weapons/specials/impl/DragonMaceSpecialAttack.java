package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;

public class DragonMaceSpecialAttack extends SpecialAttack {

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
		return 1.17;
	}
	
	@Override
	public double getMultiplier() {
		return 1.30;
	}
	
	@Override
	public boolean isDoubleHit() {
		return false;
	}
	
	private static final Animation ANIMATION = new Animation(1060);
	
	private static final Graphic GRAPHIC = new Graphic(251, GraphicHeight.HIGH);
}
