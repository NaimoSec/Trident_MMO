package org.trident.world.entity.npc.custom.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Position;
import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class ChaosElemental extends CustomNPC {

	private static final Graphic teleGraphic = new Graphic(661);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		attacker.getMovementQueue().stopMovement();
		attacker.setEntityInteraction(target);
		final int attackStyle = Misc.getRandom(2); //0 = melee, 1 = range, 2 =mage
		final elementalData data = elementalData.forId(attackStyle);
		attacker.getCombatAttributes().setAttackType(AttackType.forId(attackStyle));
		if(data.startGraphic != null)
			attacker.performGraphic(data.startGraphic);
		attacker.performAnimation(attacker.getAttackAnimation());
		if(data.projectileGraphic != null)
			CustomNPC.fireGlobalProjectile(target, attacker, data.projectileGraphic);
		TaskManager.submit(new Task(1) {
			@Override
			public void execute() {
				int dmg = Misc.getRandom(data.maxhit);
				Damage damage = new Damage(new Hit(dmg, CombatIcon.forId(attackStyle), Hitmask.RED));
				DamageHandler.handleAttack(attacker, target, damage, AttackType.forId(attackStyle), false, false);
				if(data.endGraphic != null)
					target.performGraphic(data.endGraphic);
				if(Misc.getRandom(15) <= 1) {
					attacker.performGraphic(teleGraphic);
					target.performGraphic(teleGraphic);
					if(target.getMovementQueue().canWalk(4, 4))
						target.moveTo(new Position(target.getPosition().getX() + 4, target.getPosition().getY() + 4));
				}
				stop();
			}
		});

	}

	enum elementalData {
		MELEE(new Graphic(553, GraphicHeight.HIGH), new Graphic(554,GraphicHeight.MIDDLE), null, 320 ),
		RANGED(new Graphic(665, GraphicHeight.HIGH), null, new Graphic(552, GraphicHeight.HIGH), 320),
		MAGIC(new Graphic(550, GraphicHeight.HIGH), new Graphic(551,GraphicHeight.MIDDLE), new Graphic(555, GraphicHeight.HIGH), 400);

		elementalData(Graphic startGfx, Graphic projectile, Graphic endGraphic, int maxhit) {
			startGraphic = startGfx;
			projectileGraphic = projectile;
			this.endGraphic = endGraphic;
			this.maxhit = maxhit;
		}

		public Graphic startGraphic;
		public Graphic projectileGraphic;
		public Graphic endGraphic;
		public int maxhit;

		static elementalData forId(int id) {
			for(elementalData data : elementalData.values()) {
				if(data.ordinal() == id)
					return data;
			}
			return null;
		}
	}

}
