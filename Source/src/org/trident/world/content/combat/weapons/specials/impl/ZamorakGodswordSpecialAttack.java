package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.model.Animation;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * Represents a powerful and accurate armadyl godsword special attack.
 * 
 * @author relex lawl
 */

public class ZamorakGodswordSpecialAttack extends SpecialAttack {

	@Override
	public Animation getAnimation() {
		return ANIMATION;
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public double getSpecialAmount() {
		return 5;
	}

	@Override
	public double getAccuracy() {
		return 1.5;
	}

	@Override
	public double getMultiplier() {
		return 1.10;
	}
	
	@Override
	public boolean modifyDamage() {
		return false;
	}
	
	@Override
	public void specialAction(Player player, GameCharacter victim, Damage damage) {
		victim.performGraphic(new Graphic(1221));
		if(victim.getCombatAttributes().getFreezeDelay() <= 0) { //TODO: Make large npcs unfreezeable
			victim.getCombatAttributes().setFreezeDelay(20);
			if(victim.isPlayer())
				((Player)victim).getPacketSender().sendMessage("You've been frozen!");
		}
	}

	private static final Animation ANIMATION = new Animation(7070);
}
