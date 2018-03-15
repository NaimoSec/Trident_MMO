package org.trident.world.content.combat.combatdata.magic;

import java.util.ArrayList;

import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.Item;
import org.trident.util.Misc;
import org.trident.world.content.Locations;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatExtras;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.combat.weapons.WeaponHandler.ExperienceStyle;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

/**
 * A {@link CombatSpell} implementation that is primarily used for spells that
 * are a part of the ancients spellbook.
 *
 * @author lare96
 */
public abstract class CombatAncientSpell extends CombatSpell {

	@Override
	public void endCast(Player cast, GameCharacter castOn, int damageInflicted) {
		if (damageInflicted > 0) {
			if (this.spellRadius() == 0)
				return;
			if(!cast.isPlayer())
				return;
			Player player = (Player)cast;
			ArrayList<GameCharacter> targets = new ArrayList<GameCharacter>();
			targets.add(castOn);
			if(Location.inMulti(cast)) {
				for(Player targ : player.getAttributes().getLocalPlayers()) {
					if(targ == null)
						continue;
					if(targets.contains(targ) || !Locations.goodDistance(targ.getPosition(), castOn.getPosition(), this.spellRadius()))
						continue;
					if(targ.getIndex() != player.getIndex()) {
						if(CombatHandler.checkRequirements(player, targ) && CombatHandler.checkSecondaryRequirements(player, targ))
							targets.add(targ);
					}
				}
				for(NPC targ : player.getAttributes().getLocalNpcs()) {
					if(targ == null)
						continue;
					if(!Locations.goodDistance(targ.getPosition(), castOn.getPosition(), this.spellRadius()))
						continue;
					if(!targets.contains(targ) && targ.getAttributes().isAttackable()) {
						if(CombatHandler.checkRequirements(player, targ))
							targets.add(targ);
					}
				}
			}
			TeleportHandler.cancelCurrentActions(player);
			boolean resetNeeded = player.getCombatAttributes().getAttackType() != AttackType.MAGIC;
			if(resetNeeded)
				player.getCombatAttributes().setAttackType(AttackType.MAGIC);
			final boolean resetSpellCast = player.getPlayerCombatAttributes().getMagic().getCurrentSpell() == null;
			if(resetSpellCast) //For calculating damage
				player.getPlayerCombatAttributes().getMagic().setCurrentSpell(this);
			for(GameCharacter toAttack : targets) {
				if(toAttack != null) {
					Damage damage = DamageHandler.getDamage(player, toAttack);
					int dmg = damage.getHits()[0].getDamage();
					if(dmg > 0) {
						if(toAttack.getCurseActive()[CurseHandler.DEFLECT_MAGIC]) {
							if(Misc.getRandom(10) <= 6)
								CombatExtras.handleDeflectPrayers(player, toAttack, 0, dmg);
							dmg = (int)(dmg * 0.7);
						}
					}
					if (dmg > 0) {
						toAttack.setDamage(damage);
						spellEffect(cast, toAttack, damage.getHits()[0].getDamage());
						CombatHandler.addExperience(player, damage, ExperienceStyle.MAGIC);
						CombatExtras.handleEffects(player, toAttack, damage);
					} else
						toAttack.performGraphic(new Graphic(85, GraphicHeight.MIDDLE));
					if(toAttack.isPlayer())
						TeleportHandler.cancelCurrentActions((Player)toAttack);
					toAttack.getCombatAttributes().setLastAttacker(player).setLastDamageReceived(System.currentTimeMillis());
					if(endGfx() != null)
						toAttack.performGraphic(endGfx());
					CombatHandler.handleAutoRetaliate(player, toAttack);
				}
			}
			if(resetNeeded)
				CombatHandler.setProperAttackType(player);
			if(resetSpellCast)
				player.getPlayerCombatAttributes().getMagic().setCurrentSpell(null);
		}
	}

	@Override
	public Item[] equipmentRequired(Player player) {

		/** Ancient spells never require any equipment. */
		return null;
	}

	/**
	 * The effect this spell has on the target(s).
	 *
	 * @param cast
	 * the GameCharacter casting this spell.
	 * @param castOn
	 * the person being hit by this spell.
	 * @param damageInflicted
	 * the damage inflicted.
	 */
	public abstract void spellEffect(GameCharacter cast, GameCharacter castOn,
			int damageInflicted);

	/**
	 * The radius of this spell.
	 *
	 * @return how far from the target this spell can hit.
	 */
	public abstract int spellRadius();
}