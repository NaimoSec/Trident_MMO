package org.trident.world.content.combat.combatdata.magic;


import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.Projectile;
import org.trident.model.Skill;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * @author lare96
 */
public abstract class CombatSpell extends Spell {

	@Override
	public void castSpell(final Player p, final GameCharacter castOn, final Damage damage) {
		if(castAnimation() != null)
			p.performAnimation(castAnimation());
		if (startGfx() != null)
			p.performGraphic(startGfx());
		final CombatSpell spell = this;
		TaskManager.submit(new Task(1, p, spell instanceof CombatAncientSpell ? true : false) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 2:
					if (castOn.getConstitution() <= 0) {
						this.stop();
						return;
					}
					if (castProjectile(p, castOn) != null)
						CombatHandler.fireProjectile(p, castOn, castProjectile(p, castOn).getGraphic(), true);
					break;
				case 3:
					this.stop();
					if(!(spell instanceof CombatAncientSpell) || ((CombatAncientSpell)spell).spellRadius() == 0) { //This is all done in the ancient spells class aswell, but for multiple targets
						if(castOn.getCombatAttributes().getAttackDelay() <= 2 && !(castOn.isNpc() && castOn.getLocation() == Location.PEST_CONTROL_GAME))
							castOn.performAnimation(castOn.getBlockAnimation());
						CombatHandler.handleAutoRetaliate(p, castOn);
						if(damage == null || damage.getHits()[0].getDamage() <= 0) {
							castOn.performGraphic(new Graphic(85, GraphicHeight.MIDDLE));
							p.getSkillManager().addExperience(Skill.MAGIC, baseExperience(), false);
							return;
						}
						if(endGfx() != null)
							castOn.performGraphic(endGfx());
						if(damagingSpell())
							castOn.setDamage(damage);
						if(spell instanceof CombatAncientSpell) //Single target spells, need to call it for ice blitz etc to freeze target
							((CombatAncientSpell)spell).spellEffect(p, castOn, damage.getHits()[0].getDamage());
					}
					endCast(p, castOn, damage.getHits()[0].getDamage());
					break;
				}
				tick++;
			}
		});
		if(p.getPlayerCombatAttributes().getMagic().getAutocastSpell() == null) {
			p.getPlayerCombatAttributes().getMagic().setCurrentSpell(null);
			CombatHandler.setProperAttackType(p);
			CombatHandler.resetAttack(p);
		} else {
			p.getPlayerCombatAttributes().getMagic().setCurrentSpell(p.getPlayerCombatAttributes().getMagic().getAutocastSpell());
			p.getCombatAttributes().setAttackType(AttackType.MAGIC);
		}
	}

	/**
	 * Determines the maximum strength of this spell.
	 *
	 * @return the maximum strength of this spell.
	 */
	public abstract int maximumStrength();

	/**
	 * The animation played when the spell is cast.
	 *
	 * @return the animation played when the spell is cast.
	 */
	public abstract Animation castAnimation();

	/**
	 * The starting graphic for this spell.
	 *
	 * @return the starting graphic for this spell.
	 */
	public abstract Graphic startGfx();

	/**
	 * The mid-cast projectile for this spell.
	 *
	 * @param cast
	 * the person casting the spell.
	 * @param castOnt
	 * the castOn of the spell.
	 *
	 * @return the projectile for this spell.
	 */
	public abstract Projectile castProjectile(GameCharacter cast, GameCharacter castOn);

	/**
	 * The ending graphic for this spell.
	 *
	 * @return the ending graphic for this spell.
	 */
	public abstract Graphic endGfx();

	/**
	 * Invoked when the spell hits the castOn.
	 *
	 * @param cast
	 * the person casting the spell.
	 * @param castOn
	 * the target of the spell.
	 * @param spellAccurate
	 * if the spell actually hit the player.
	 * @param damageInflicted
	 * the amount of damage that was inflicted.
	 */
	public abstract void endCast(Player cast, GameCharacter castOn, int damageInflicted);

	/**
	 * Is this combat spell a damaging spell or more of a weakening spell?
	 * @return
	 */
	public abstract boolean damagingSpell();
}