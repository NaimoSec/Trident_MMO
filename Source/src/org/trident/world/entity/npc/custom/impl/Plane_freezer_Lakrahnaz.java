package org.trident.world.entity.npc.custom.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.Misc;
import org.trident.world.content.Locations;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.combat.combatdata.magic.MagicExtras;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Plane_freezer_Lakrahnaz extends CustomNPC 
{
	
	private static final Animation anim1 = new Animation(13770);
	private static final Graphic gfx1 = new Graphic(474);
	private static final Graphic gfx2 = new Graphic(615);

	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		final AttackType attkType = Misc.getRandom(5) <= 2 ? AttackType.RANGED : AttackType.MAGIC;
		attacker.performAnimation(anim1);
		TaskManager.submit(new Task(1, target, false) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 2:
					CustomNPC.fireGlobalProjectile(target, attacker, new Graphic(attkType == AttackType.RANGED ? 605 : 473, GraphicHeight.LOW));
					break;
				case 3:
					int damage = attkType == AttackType.RANGED ? Misc.getRandom(380) - DamageHandler.getRangedDefence(target) : Misc.getRandom(390) - DamageHandler.getMagicDefence(target);
					DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(damage), attkType == AttackType.RANGED ? CombatIcon.RANGED : CombatIcon.MAGIC, Hitmask.RED)), attkType, false, false);
					if(Misc.getRandom(26) <= 8 && attkType == AttackType.MAGIC)
						MagicExtras.freezeTarget(target, 18, gfx1);
					if(Dungeoneering.doingDungeoneering(target) && target.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getDifficulty() >= 2)
						if(Misc.getRandom(300) <= (target.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getDifficulty() >= 3 ? 95 : 80)) {
							for(Player plr : target.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty().getPlayers()) {
								if(plr != null && Locations.goodDistance(attacker.getPosition(), plr.getPosition(), 8)) {
									plr.performGraphic(gfx2);
									plr.setDamage(new Damage(new Hit(140 + Misc.getRandom(200), CombatIcon.NONE, Hitmask.RED)));
								}
							}
						}
					stop();
					break;
				}
				tick++;
			}
		});
	}

}
