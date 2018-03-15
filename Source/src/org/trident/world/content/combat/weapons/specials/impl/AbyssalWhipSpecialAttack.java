package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * Represents an abyssal whip special attack.
 * 
 * @author relex lawl
 */

public class AbyssalWhipSpecialAttack extends SpecialAttack {

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
	public void specialAction(Player player, GameCharacter victim, Damage damage) {
		if(victim.isPlayer()) {
			Player p = (Player)victim;
			int totalRunEnergy = p.getAttributes().getRunEnergy() - 25;
			if (totalRunEnergy < 0)
				totalRunEnergy = 0;
			p.getAttributes().setRunEnergy(totalRunEnergy);
		}
	}

	private static final Animation ANIMATION = new Animation(11971);

	private static final Graphic GRAPHIC = new Graphic(2108);
}
