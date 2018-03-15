package org.trident.world.entity.npc.custom.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Archer extends CustomNPC {

	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		attacker.getCombatAttributes().setAttackType(AttackType.RANGED);
		attacker.performAnimation(attacker.getAttackAnimation());
		TaskManager.submit(new Task(1, target, false) {
			int delay = 0, damage = getBaseDamage(attacker.getId()) - Misc.getRandom(DamageHandler.getRangedDefence(target));
			@Override
			public void execute() {
				switch(delay) {
				case 1:
					CustomNPC.fireGlobalProjectile(target, attacker, new Graphic(getGfx(attacker.getId())));
					break;
				case 2:
					if(damage > 84 && Dungeoneering.doingDungeoneering(target) && target.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getDifficulty() == 0)
						damage = 84;
					DamageHandler.handleAttack(attacker, target, new Damage(new Hit(damage, CombatIcon.RANGED, Hitmask.RED)), AttackType.RANGED, false, false);
					stop();
					break;
				}
				delay++;
			}
		});
	}
	
	public static int getGfx(int npcId) {
		switch(npcId) {
		case 6225:
			return 1837;
		case 6276:
			return 37;
		}
		return 1120;
	}
	
	public static int getBaseDamage(int npcId) {
		switch(npcId) {
		case 27:
			return 120;
		case 6225:
		case 6252:
			return 210;
		case 6276:
			return 150;
		}
		return 90;
	}

}
