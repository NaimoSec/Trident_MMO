package org.trident.world.content.combat.weapons.specials;

import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.PlayerSpecialAmountTask;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.Misc;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

public abstract class SpecialAttack {
	
	public abstract Animation getAnimation();

	public abstract Graphic getGraphic();

	public abstract double getSpecialAmount();

	public double getMultiplier() {
		return 1;
	}

	public double getAccuracy() {
		return 1;
	}

	public int getExtraDamage(Player player, GameCharacter victim) {
		return 0;
	}

	public boolean isDoubleHit() {
		return false;
	}

	public boolean selfGraphic() {
		return false;
	}

	public boolean isImmediate() {
		return false;
	}

	public boolean modifyDamage() {
		return false;
	}

	public void specialAction(Player player, GameCharacter victim, Damage damage) {

	}

	public void execute(Player player, GameCharacter victim) {
		if (player.getPlayerCombatAttributes().getSpecialAttackAmount() < getSpecialAmount()) {
			player.getPacketSender().sendMessage("You don't have the required special energy to use this attack.");
			player.getPlayerCombatAttributes().setUsingSpecialAttack(false);
			CombatHandler.resetAttack(player);
			return;
		}
		player.getPlayerCombatAttributes().setSpecialAttackAmount(player.getPlayerCombatAttributes().getSpecialAttackAmount() - getSpecialAmount());
		if (!player.getPlayerCombatAttributes().isRecoveringSpecialAttack()) {
			TaskManager.submit(new PlayerSpecialAmountTask(player));
		}
		if(victim != null)
			player.setEntityInteraction(victim);
		if(getAnimation() != null)
			player.performAnimation(getAnimation());
		if(getGraphic() != null) {
			if (!selfGraphic() && victim != null)
				victim.performGraphic(getGraphic());
			else
				player.performGraphic(getGraphic());
		}
		Damage damage = null;
		if (victim != null && !modifyDamage()) {
			if (isDoubleHit()) {
				damage = new Damage(new Hit(Misc.getRandom((int)DamageHandler.getBaseMeleeDamage(player)), CombatIcon.MELEE, Hitmask.RED),
						new Hit(Misc.getRandom((int)DamageHandler.getBaseMeleeDamage(player)), CombatIcon.MELEE, Hitmask.RED));
			} else {
				damage = new Damage(new Hit(Misc.getRandom((int)DamageHandler.getBaseMeleeDamage(player)), CombatIcon.MELEE, Hitmask.RED));
			}
		}
		specialAction(player, victim, damage);
		if (damage != null)
			DamageHandler.handleAttack(player, victim, damage, player.getCombatAttributes().getAttackType(), true, false);
		/*if(victim != null)
			victim.performAnimation(victim.getBlockAnimation());*/
		player.getPlayerCombatAttributes().setUsingSpecialAttack(false);
		WeaponHandler.update(player);
		player.getPlayerCombatAttributes().setUsingSpecialAttack(false);
		player.getPlayerCombatAttributes().setSpecialAttack(null);
	}
}
