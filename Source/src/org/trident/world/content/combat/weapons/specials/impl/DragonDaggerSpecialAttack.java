package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.content.skills.impl.herblore.WeaponPoison;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

public class DragonDaggerSpecialAttack extends SpecialAttack {

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
		return 1.01;
	}
	
	@Override
	public double getMultiplier() {
		return 1.01;
	}
	
	@Override
	public boolean isDoubleHit() {
		return true;
	}
	
	@Override
	public void specialAction(Player p, GameCharacter t, Damage dmg) {
		WeaponPoison.handleWeaponPoison(p, t);
	}
	
	private static final Animation ANIMATION = new Animation(1062);
	
	private static final Graphic GRAPHIC = new Graphic(252, GraphicHeight.HIGH);
}
