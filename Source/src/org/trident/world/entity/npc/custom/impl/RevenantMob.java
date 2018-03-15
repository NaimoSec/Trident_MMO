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

public class RevenantMob extends CustomNPC 
{

	private static final Graphic gfx = new Graphic(281);
	private static final Graphic gfx2 = new Graphic(1186);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		final AttackType attkType = Misc.getRandom(5) <= 2 && Locations.goodDistance(attacker.getPosition(), target.getPosition(), 2) ? AttackType.MELEE : Misc.getRandom(10) <= 5 ? AttackType.MAGIC : AttackType.RANGED;
		attacker.getCombatAttributes().setAttackType(attkType);
		switch(attkType) {
		case MELEE:
			attacker.performAnimation(attacker.getAttackAnimation());
			DamageHandler.handleAttack(attacker, target, CustomNPC.getBaseDamage(attacker, target), AttackType.MELEE, false, false);
			break;
		case MAGIC:
		case RANGED:
			final REVENANT_DATA revData = REVENANT_DATA.getData(attacker.getId());
			attacker.performAnimation(attkType == AttackType.MAGIC ? revData.magicAttack : revData.rangedAttack);
			TaskManager.submit(new Task(1, target, false) {
				int tick = 0;
				@Override
				public void execute() {
					switch(tick) {
					case 1:
						CustomNPC.fireGlobalProjectile(target, attacker, new Graphic(attkType == AttackType.RANGED ? 970 : 280, GraphicHeight.LOW));
						break;
					case 3:
						DamageHandler.handleAttack(attacker, target, getDamage(target, revData, attkType), attkType, false, false);
						if(Misc.getRandom(26) <= 8 && attkType == AttackType.MAGIC) {
							MagicExtras.freezeTarget(target, 15, gfx);
							attacker.performGraphic(gfx2);
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

	enum REVENANT_DATA {

		REVENANT_IMP(6715, new Animation(7500), new Animation(7501), 170, 170),
		REVENANT_GOBLIN(6716, new Animation(7499), new Animation(7513), 240, 240),
		REVENANT_HOBGOBLIN(6727, new Animation(7503), new Animation(7516), 290, 290),
		REVENANT_DEMON(6689, new Animation(7498), new Animation(7512), 350, 350);

		REVENANT_DATA(int npc, Animation magicAttack, Animation rangedAttack, int maxMagicDamage, int maxRangedDamage) {
			this.npc = npc;
			this.magicAttack = magicAttack;
			this.rangedAttack = rangedAttack;
			this.maxMagicDamage = maxMagicDamage;
			this.maxRangedDamage = maxRangedDamage;
		}

		int npc;
		Animation magicAttack, rangedAttack;
		int maxMagicDamage, maxRangedDamage;

		public static REVENANT_DATA getData(int npc) {
			for(REVENANT_DATA data : REVENANT_DATA.values()) {
				if(data != null && data.npc == npc) {
					return data;
				}
			}
			return null;
		}
	}
	
	public static Damage getDamage(Player p, REVENANT_DATA data, AttackType type) {
		Hitmask mask = Hitmask.RED;
		CombatIcon icon = type == AttackType.RANGED ? CombatIcon.RANGED : CombatIcon.MAGIC;
		int damage = Misc.getRandom(type == AttackType.RANGED ? data.maxRangedDamage : data.maxMagicDamage);
		if(type == AttackType.RANGED)
			damage -= Misc.getRandom(DamageHandler.getRangedDefence(p));
		else if(type == AttackType.MAGIC)
			damage -= Misc.getRandom(DamageHandler.getMagicDefence(p));
		if(data == REVENANT_DATA.REVENANT_HOBGOBLIN && Dungeoneering.doingDungeoneering(p)) {
			if(damage > 150 && p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getDifficulty() <= 1)
				damage = 150;
		}
		return new Damage(new Hit(damage, icon, mask));
	}
}
