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
import org.trident.model.Position;
import org.trident.model.definitions.NPCSpawns;
import org.trident.model.movement.MovementStatus;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.combat.combatdata.magic.MagicExtras;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.content.teleporting.TeleportType;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCData.CustomNPCData;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Nomad extends CustomNPC {

	private static final Animation anim1 = new Animation(1074);
	private static final Animation anim2 = new Animation(12696);
	private static final Animation anim3 = new Animation(12698);
	private static final Graphic gfx1 = new Graphic(99, GraphicHeight.HIGH);
	private static final Graphic gfx2 = new Graphic(2281, GraphicHeight.LOW);
	private static final Graphic gfx3 = new Graphic(369, GraphicHeight.LOW);
	
	@Override
	public void executeAttack(final NPC attacker, final Player target) {
		int randomNomad = Misc.getRandom(30);
		if (randomNomad >= 0 && randomNomad <= 26) {
			boolean heal = !attacker.getAttributes().hasHealed() && attacker.getConstitution() < 4000;
			if (attacker.getConstitution() > 0 && !heal) {
				attacker.performAnimation(anim2);
				DamageHandler.handleAttack(attacker, target, new Damage(new Hit(100 + Misc.getRandom(120), CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
			} else {
				attacker.getAttributes().setHealed(true);
				attacker.performGraphic(gfx2);
				attacker.performAnimation(anim3);
				attacker.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
				attacker.forceChat("Zamorak.. Aid me..");
				attacker.getCombatAttributes().setAttackDelay(30);
				TaskManager.submit(new Task(1, target, false) {
					int ticks = 0;
					@Override
					public void execute() {
						attacker.setConstitution(attacker.getConstitution() + 600);
						ticks++;
						if(ticks >= 5) {
							attacker.forceChat("Zamorak, I am in your favor.");
							attacker.getMovementQueue().setMovementStatus(MovementStatus.NONE);
							attacker.getCombatAttributes().setAttackDelay(5);
							stop();
						}
					}
				});
			}
		}
		else if (randomNomad == 27) {
			attacker.getCombatAttributes().setAttackDelay(30);
			TaskManager.submit(new Task(1, target, false) {
				int ticks = 0;
				@Override
				public void execute() {
					if (ticks == 0) {
						attacker.performGraphic(gfx2);
						attacker.performAnimation(anim3);
						attacker.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
					}
					if(ticks == 5)
						attacker.performGraphic(gfx2);
					if(ticks == 7)
						attacker.forceChat("Almost.. Almost there..");
					if(ticks == 9)
						attacker.performAnimation(new Animation(12697));
					if (ticks == 10) {
						fireGlobalProjectile(target, attacker, new Graphic(2283, GraphicHeight.MIDDLE));
						DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(750), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
						attacker.getMovementQueue().setMovementStatus(MovementStatus.NONE);
						attacker.getCombatAttributes().setAttackDelay(5);
						this.stop();
					}
					ticks++;
				}
			});

		}
		else if (randomNomad == 29) {
			attacker.getCombatAttributes().setAttackDelay(30);
			TaskManager.submit(new Task(1, target, false) {
				int ticks = 0;
				@Override
				public void execute() {
					if (ticks == 0) {
						handleNomadTeleport(attacker, 3360, 5858, target.getPosition().getZ(), 8939, 8941, 1576, 1577);
						TeleportHandler.teleportPlayer(target, new Position(3360, 5856, target.getPosition().getZ()), TeleportType.NORMAL);
						MagicExtras.freezeTarget(target, 15, gfx3);
						DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(200), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
						attacker.getMovementQueue().stopMovement(); target.getMovementQueue().stopMovement();
						attacker.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
						attacker.performGraphic(gfx2);
						attacker.performAnimation(anim3);
						attacker.forceChat("Reunite!");
					}
					if(ticks == 5)
						attacker.forceChat("Zamorak, please! Allow me to me channel your power!");
					if(ticks == 10)
						attacker.forceChat("Adventurer, prepare to be blown away!");
					if(ticks == 5 || ticks == 10 || ticks == 15) {
						attacker.performGraphic(gfx2);
						attacker.performAnimation(anim3);
					}
					if(ticks == 18)
						attacker.forceChat("I call upon you, Zamorak!");
					if(ticks == 20)
						attacker.performAnimation(new Animation(12697));
					if(ticks == 23) 
						fireGlobalProjectile(target, attacker, new Graphic(2001, GraphicHeight.HIGH));
					if(ticks == 24)
						target.performGraphic(new Graphic(2004));
					if (ticks == 25) {
						DamageHandler.handleAttack(attacker, target, new Damage(new Hit(target.getConstitution() - Misc.getRandom(150), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
						target.getCombatAttributes().setFreezeDelay(0);
						attacker.getCombatAttributes().setAttackDelay(8);
						attacker.getMovementQueue().setMovementStatus(MovementStatus.NONE);
						stop();
					}
					ticks++;
				}
			});
		}else if (randomNomad == 28) {
			attacker.getCombatAttributes().setAttackDelay(30);
			TaskManager.submit(new Task(1, target, false) {
				int ticks = 0;
				@Override
				public void execute() {
					if (ticks == 0) {
						handleNomadTeleport(attacker, 3360, 5858, target.getPosition().getZ(), 8939, 8941, 1576, 1577);
						TeleportHandler.teleportPlayer(target, new Position(3360, 5856, target.getPosition().getZ()), TeleportType.NORMAL);
						MagicExtras.freezeTarget(target, 15, gfx3);
						DamageHandler.handleAttack(attacker, target, new Damage(new Hit(Misc.getRandom(200), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
						attacker.getMovementQueue().stopMovement(); target.getMovementQueue().stopMovement();
						attacker.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
						attacker.performGraphic(gfx2);
						attacker.performAnimation(anim3);
						attacker.forceChat("Reunite!");
					}
					if(ticks == 5 || ticks == 10 || ticks == 15) {
						attacker.performGraphic(gfx2);
						attacker.performAnimation(anim3);
					}
					if(ticks == 20)
						spawnRanger(target, attacker);
					if (ticks == 22) {
						target.getCombatAttributes().setFreezeDelay(0);
						attacker.getCombatAttributes().setAttackDelay(1);
						attacker.getMovementQueue().setMovementStatus(MovementStatus.NONE);
						stop();
					}
					ticks++;
				}
			});
		} else if (randomNomad == 26) {
			attacker.getCombatAttributes().setAttackDelay(30);
			attacker.performAnimation(anim2);
			attacker.forceChat("You shall fall!");
			DamageHandler.handleAttack(attacker, target, new Damage(new Hit(100 + Misc.getRandom(120), CombatIcon.MELEE, Hitmask.RED), new Hit(120 + Misc.getRandom(120), CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
		}
	}

	public static void spawnRanger(final Player target, final NPC attacker) {
		final NPC ranger = NPCSpawns.createCustomNPC(CustomNPCData.NOMAD_RANGER_HELPER, new Position(3360, 5854, target.getPosition().getZ()));
		ranger.getCombatAttributes().setSpawnedFor(target);
		TaskManager.submit(new Task(1, target, true) {
			int ticks = 0;
			@Override
			public void execute() {
				if(ticks == 0)
					ranger.setEntityInteraction(target);
				else if(ticks == 2)
					ranger.performAnimation(anim1);
				else if(ticks == 4) {
					fireGlobalProjectile(target, ranger, gfx1);
					fireGlobalProjectile(target, ranger, gfx1);
				} else if(ticks == 5)
					DamageHandler.handleAttack(ranger, target, new Damage(new Hit(Misc.getRandom(300), CombatIcon.RANGED, Hitmask.RED), new Hit(Misc.getRandom(250), CombatIcon.RANGED, Hitmask.RED)), AttackType.RANGED, false, false);
				if(target.getCombatAttributes().getCurrentEnemy() != null && target.getCombatAttributes().getCurrentEnemy() == ranger)
					target.getCombatAttributes().setCurrentEnemy(attacker);
				if(ticks >= 6) {
					World.deregister(ranger);
					stop();
				}
				ticks++;
			}
		});
	}
	
	public static void handleNomadTeleport(final NPC n, final int x, final int y, final int height
			, final int beginAnim, final int endAnim, final int beginGfx, final int endGfx)
	{
		if(beginAnim != -1)
			n.performAnimation(new Animation(beginAnim));
		if(beginGfx != -1)
			n.performGraphic(new Graphic(beginGfx, GraphicHeight.LOW));
		TaskManager.submit(new Task(1) {
			int ticks = 0;
			@Override
			public void execute() {
				if(ticks == 1)
					n.moveTo(new Position(x, y, height));
				else if(ticks >= 2) {
					if(endAnim != -1)
						n.performAnimation(new Animation(endAnim));
					if(endGfx != -1)
						n.performGraphic(new Graphic(endGfx, GraphicHeight.LOW));
					this.stop();
				}
				ticks++;

			}
		});
	}
}
