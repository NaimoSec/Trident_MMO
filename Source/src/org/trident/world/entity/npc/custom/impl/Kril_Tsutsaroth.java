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
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.Locations;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Kril_Tsutsaroth extends CustomNPC {
	
	private static final Animation anim1 = new Animation(6947);
	private static final Graphic graphic1 = new Graphic(1211, GraphicHeight.MIDDLE);
	private static final Graphic graphic2 = new Graphic(390);

	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		AttackType style = Misc.getRandom(8) >= 6 && Locations.goodDistance(attacker.getPosition(), target.getPosition(), 2) ? AttackType.MELEE : AttackType.MAGIC;
		switch(style) {
		case MELEE:
			attacker.performAnimation(attacker.getAttackAnimation());
			Damage damage = getBaseDamage(attacker, target);
			int specialAttack = Misc.getRandom(4);
			if (specialAttack == 2) {
				int dmgToSet = Misc.getRandom(510);
				int amountToDrain = (int) (dmgToSet * 0.4);
				dmgToSet = (int) (dmgToSet * 1);
				target.getPacketSender().sendMessage("K'ril Tsutsaroth slams through your defence and steals some Prayer points..");
				damage.getHits()[0].setDamage(dmgToSet);
				target.setDamage(damage);
				target.getSkillManager().setCurrentLevel(Skill.PRAYER, target.getSkillManager().getCurrentLevel(Skill.PRAYER) - amountToDrain);
				return;
			}
			DamageHandler.handleAttack(attacker, target, damage, AttackType.MELEE, false, false);
			break;
		default:
			attacker.performAnimation(anim1);
			TaskManager.submit(new Task(2, false) {
				int tick = 0;
				@Override
				public void execute() {
					switch(tick) {
					case 0:
						for (final Player near : Misc.getCombinedPlayerList(target)) {
							if(near == null || near.getLocation() != Location.GODWARS_DUNGEON || near.isTeleporting())
								continue;
							if(near.getPosition().distanceToPoint(attacker.getPosition().getX(), attacker.getPosition().getY()) > 20)
								continue;
							CustomNPC.fireGlobalProjectile(target, attacker, graphic1);
						}
						break;
					case 1:
						for (final Player near : Misc.getCombinedPlayerList(target)) {
							if(near == null || near.getLocation() != Location.GODWARS_DUNGEON || near.isTeleporting() || !near.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom())
								continue;
							if(near.getPosition().distanceToPoint(attacker.getPosition().getX(), attacker.getPosition().getY()) > 20)
								continue;
							near.performGraphic(graphic2);
							int magicDamage = 300 + Misc.getRandom(150);
							magicDamage -= Misc.getRandom(DamageHandler.getMagicDefence(near));
							DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(magicDamage), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
						}
						stop();
						break;
					}
					tick++;
				}
			});
			break;
		}
	}

}
