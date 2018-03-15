package org.trident.world.content.combat;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.PoisonTask;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Flag;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Position;
import org.trident.model.Projectile;
import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.util.Constants;
import org.trident.util.Misc;
import org.trident.world.content.BonusManager;
import org.trident.world.content.ItemDegrading;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.skills.impl.herblore.WeaponPoison;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.content.teleporting.TeleportType;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * Handles Misc Combat tools, such as equipment and bonus effects.
 * 
 * @author Gabbe
 */

public class CombatExtras {

	public static void poison(GameCharacter target, int amount, boolean login) {
		int currentPoisonDamage = target.getCombatAttributes().getCurrentPoisonDamage();
		boolean startEvent = currentPoisonDamage == 0 || login;
		currentPoisonDamage = currentPoisonDamage + amount;
		if(currentPoisonDamage > 11)
			currentPoisonDamage = 11;
		target.getCombatAttributes().setCurrentPoisonDamage(currentPoisonDamage);
		if(!login && target instanceof Player)
			((Player)target).getPacketSender().sendMessage("You have been poisoned!");
		if(startEvent)
			TaskManager.submit(new PoisonTask(target, 35));
	}

	public static void chargeDragonFireShield(Player player) {
		if(player.getAttributes().getDragonFireCharges() >= 30) {
			player.getPacketSender().sendMessage("Your Dragonfire shield is fully charged and can be operated.");
			return;
		}
		player.performAnimation(new Animation(6695));
		player.performGraphic(new Graphic(1164));
		player.getAttributes().setDragonFireCharges(1, true);
		BonusManager.update(player);
		player.getPacketSender().sendMessage("Your shield absorbs some of the Dragon's fire..");
	}

	public static void handleDragonFireShield(final Player player, final GameCharacter target) {
		if(player == null || target == null || target.getConstitution() <= 0 || player.getConstitution() <= 0)
			return;
		CombatHandler.resetAttack(player);
		player.setEntityInteraction(target);
		player.performAnimation(new Animation(6696));
		player.performGraphic(new Graphic(1165));
		TaskManager.submit(new Task(1, player, false) {
			int ticks = 0;
			@Override
			public void execute() {
				switch(ticks) {
				case 3:
					player.getPacketSender().sendGlobalProjectile(new Projectile(new Position(player.getPosition().getX(), player.getPosition().getY(), 43), new Position(target.getPosition().getX(), target.getPosition().getY(), 31), new Graphic(1166, GraphicHeight.HIGH), 0, 50, 50), target);
					break;
				case 4:
					Damage damage = new Damage(new Hit(50 + Misc.getRandom(150), CombatIcon.MAGIC, Hitmask.RED));
					target.setDamage(damage);
					target.performGraphic(new Graphic(1167, GraphicHeight.HIGH));
					stop();
					break;
				}
				ticks++;
			}
		});
		player.getAttributes().setDragonFireCharges(0, false);
		BonusManager.update(player);
	}

	public static void handleRecoil(final Player c, final int dmg, final GameCharacter target) {
		final boolean hasRecoil = c.getEquipment().getItems()[Equipment.RING_SLOT].getId() == 2550;
		if (!hasRecoil || dmg <= 0 )
			return;
		TaskManager.submit(new Task(1, c, false) {
			@Override
			public void execute() {
				int recDamage = (int) (dmg * 0.10);
				if (!hasRecoil || dmg <= 0 || recDamage <= 0) {
					stop();
					return;
				}
				if (recDamage > target.getConstitution())
					recDamage = target.getConstitution();
				Damage recoilDamage = new Damage(new Hit(recDamage, CombatIcon.DEFLECT,
						Hitmask.RED));
				target.setDamage(recoilDamage);
				stop();
			}

		});
	}

	public static void handleDeflectPrayers(final GameCharacter attacker, final GameCharacter target, final int index, final int damage) {
		if (attacker.getConstitution() <= 0 || target == null || target.getConstitution() <= 0)
			return;
		final int[][] deflectData = {
				// prayerActive[id], gfx, anim
				{ 2228, 12573 },// Deflect Mage
				{ 2229, 12573 },// Deflect Range
				{ 2230, 12573 },// Deflect Melee
		};
		target.performGraphic(new Graphic(deflectData[index][0], GraphicHeight.MIDDLE));
		target.performAnimation(new Animation(deflectData[index][1]));
		TaskManager.submit(new Task(1, false) {
			@Override
			public void execute() {
				int deflectDamage = (int) (damage * 0.15);
				if (deflectDamage <= 0)
					return;
				if (deflectDamage > target.getConstitution())
					deflectDamage = target.getConstitution();
				Damage damageToDeflect = new Damage(new Hit(deflectDamage, CombatIcon.DEFLECT, Hitmask.RED));
				attacker.setDamage(damageToDeflect);
				stop();
			}
		});
	}

	public static void handleRedemption(Player c) {
		if (c.getConstitution() <= 0)
			return;
		int amountToHeal = (int) (c.getSkillManager().getMaxLevel(Skill.PRAYER) * .25);
		if (c.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) <= c
				.getSkillManager().getMaxLevel(Skill.CONSTITUTION) * .1) {
			c.performGraphic(new Graphic(436));
			c.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
			c.getSkillManager().updateSkill(Skill.PRAYER);
			c.getSkillManager().setCurrentLevel(Skill.CONSTITUTION,
					c.getConstitution() + amountToHeal);
			c.getSkillManager().updateSkill(Skill.CONSTITUTION);
			PrayerHandler.deactivatePrayers(c);
			CurseHandler.deactivateCurses(c);
		}
	}

	public static void handleRingofLife(Player c) {
		if (c.getConstitution() <= 0)
			return;
		if(c.getLocation() == Location.DUEL_ARENA)
			return;
		if(!TeleportHandler.checkReqs(c, null))
			return;
		if (c.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) <= c
				.getSkillManager().getMaxLevel(Skill.CONSTITUTION) * .1) {
			c.getEquipment().delete(
					c.getEquipment().getItems()[Equipment.RING_SLOT]);
			TeleportHandler.teleportPlayer(c, Constants.DEFAULT_POSITION.copy(),
					TeleportType.RING_TELE);
			c.getPacketSender()
			.sendMessage(
					"Your Ring of Life teleported you away, but was destroyed in the process.");
		}
	}

	public static void handlePhoenixNecklace(Player c) {
		if(c.getLocation() == Location.DUEL_ARENA)
			return;
		int restore = (int) (c.getSkillManager()
				.getMaxLevel(Skill.CONSTITUTION) * .3);
		if (c.getConstitution() <= 0)
			return;
		if (c.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) <= c
				.getSkillManager().getMaxLevel(Skill.CONSTITUTION) * .2) {
			c.performGraphic(new Graphic(1690, 0));
			c.getEquipment().delete(
					c.getEquipment().getItems()[Equipment.AMULET_SLOT]);
			c.getSkillManager().setCurrentLevel(
					Skill.CONSTITUTION,
					c.getSkillManager().getCurrentLevel(Skill.CONSTITUTION)
					+ restore);
			c.getPacketSender()
			.sendMessage(
					"Your Phoenix Necklace restored your Constitution, but was destroyed in the process.");
			c.getUpdateFlag().flag(Flag.APPEARANCE);
		}
	}

	public static void handleSoulSplit(final Player c, int damage, final GameCharacter target) {
		final int form = damage / 4;
		if (form < 0
				|| target == null
				|| target.getConstitution() <= 0)
			return;
		c.getPacketSender().sendGlobalProjectile(new Projectile(c.getPosition(), target.getPosition(), new Graphic(2263, GraphicHeight.HIGH), 5, 50, 40), target);
		TaskManager.submit(new Task(3) {
			@Override
			public void execute() {
				target.performGraphic(new Graphic(2264, GraphicHeight.LOW));
				c.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, c.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) + form);
				if(c.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) > c.getSkillManager().getMaxLevel(Skill.CONSTITUTION))
					c.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, c.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
				if(target instanceof Player) {
					Player plr = (Player) target;
					if(plr != null) {
						plr.getSkillManager().setCurrentLevel(Skill.PRAYER, plr.getSkillManager().getCurrentLevel(Skill.PRAYER) - form);
						if (plr.getSkillManager().getCurrentLevel(Skill.PRAYER) < 0) {
							plr.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
							CurseHandler.deactivateCurses(plr);
							PrayerHandler.deactivatePrayers(plr);
						}
						plr.getSkillManager().updateSkill(Skill.PRAYER);
					}
				}
				stop();
			}
		});
	}

	public static void handleSmite(Player c, int damage, Player target) {
		int form = damage / 4;
		if (form < 0 || damage < 0 || target.getConstitution() <= 0
				|| target == null)
			return;
		target.getSkillManager().setCurrentLevel(Skill.PRAYER,
				target.getSkillManager().getCurrentLevel(Skill.PRAYER) - form);
		if (target.getSkillManager().getCurrentLevel(Skill.PRAYER) < 0) {
			target.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
			CurseHandler.deactivateCurses(target);
			PrayerHandler.deactivatePrayers(target);
		}
		target.getSkillManager().updateSkill(Skill.PRAYER);
	}

	public static void handleWrath(Player c, GameCharacter target) {
		if(c.getAttributes().prayerIsDealingDamage())
			return;
		int wrathDamage = (c.getSkillManager().getCurrentLevel(Skill.PRAYER) / 100) * 26;
		c.performGraphic(new Graphic(2259));
		c.performAnimation(new Animation(12583));
		if (target != null) {
			target.performGraphic(new Graphic(2260));
			target.setDamage(new Damage(new Hit(wrathDamage, CombatIcon.DEFLECT, Hitmask.RED)));
		}
		c.getAttributes().setPrayerIsDealingDamage(true);
	}

	public static void handleRetribution(Player c, GameCharacter target) {
		if(c.getAttributes().prayerIsDealingDamage())
			return;
		int retDamage = (c.getSkillManager().getCurrentLevel(Skill.PRAYER) / 100) * 16;
		c.performGraphic(new Graphic(437));
		if (target != null) {
			target.setDamage(new Damage(new Hit(retDamage, CombatIcon.DEFLECT,
					Hitmask.RED)));
		}
		c.getAttributes().setPrayerIsDealingDamage(true);
	}

	public static void applyLeeches(Player attacker, GameCharacter target) {
		if(target == null || target.getConstitution() <= 0 || attacker.getConstitution() <= 0)
			return;
		if(!attacker.getCurseActive()[CurseHandler.LEECH_ATTACK] && !attacker.getCurseActive()[CurseHandler.LEECH_DEFENCE] &&!attacker.getCurseActive()[CurseHandler.LEECH_STRENGTH] && !attacker.getCurseActive()[CurseHandler.LEECH_MAGIC] && !attacker.getCurseActive()[CurseHandler.LEECH_RANGED]) 
			return;
		int i, gfx, projectileGfx; i = gfx = projectileGfx = -1;
		if(Misc.getRandom(10) >= 7 && attacker.getCurseActive()[CurseHandler.LEECH_ATTACK]) {
			i = 0;
			projectileGfx = 2252;
			gfx = 2253;
		} else if(Misc.getRandom(15) >= 11 && attacker.getCurseActive()[CurseHandler.LEECH_DEFENCE]) {
			i = 1;
			projectileGfx = 2248;
			gfx = 2250;
		} else if(Misc.getRandom(10) <= 3 && attacker.getCurseActive()[CurseHandler.LEECH_STRENGTH]) {
			i = 2;
			projectileGfx = 2236;
			gfx = 2238;
		} else if(Misc.getRandom(20) >= 16 && attacker.getCurseActive()[CurseHandler.LEECH_RANGED]) {
			i = 4;
			projectileGfx = 2236;
			gfx = 2238;
		} else  if(Misc.getRandom(30) >= 26 && attacker.getCurseActive()[CurseHandler.LEECH_MAGIC]) {
			i = 6;
			projectileGfx = 2244;
			gfx = 2242;
		}
		if(i != -1) {
			if(target.getCombatAttributes().getLeechedBonuses()[i] > -25)
				target.getCombatAttributes().getLeechedBonuses()[i] -= 2;
			if(attacker.getCombatAttributes().getLeechedBonuses()[i] < 2)
				attacker.getCombatAttributes().getLeechedBonuses()[i] += Misc.getRandom(2);
			sendProjectile(attacker, target, projectileGfx);
			target.performGraphic(new Graphic(gfx));
			if(attacker.getCombatAttributes().getAttackDelay() <= 2)
				attacker.performAnimation(new Animation(12575));
			BonusManager.sendCurseBonuses(attacker);
			if(target instanceof Player)
				BonusManager.sendCurseBonuses((Player) target);
			attacker.getPacketSender().sendMessage("You manage to drain your target's "+Misc.formatText(Skill.forId(i).toString().toLowerCase())+".");
		}
	}

	public static void applySaps(Player attacker, GameCharacter target) {
		if(target == null || target.getConstitution() <= 0 || attacker.getConstitution() <= 0)
			return;
		boolean sapWarrior = attacker.getCurseActive()[CurseHandler.SAP_WARRIOR];
		boolean sapRanger = attacker.getCurseActive()[CurseHandler.SAP_RANGER];
		boolean sapMage = attacker.getCurseActive()[CurseHandler.SAP_MAGE];
		if(!sapWarrior && !sapRanger && !sapMage)
			return;
		if(sapWarrior && Misc.getRandom(8) <= 2) {
			if(target.getCombatAttributes().getLeechedBonuses()[0] > -10)
				target.getCombatAttributes().getLeechedBonuses()[0] -= Misc.getRandom(2);
			if(target.getCombatAttributes().getLeechedBonuses()[1] > -10)
				target.getCombatAttributes().getLeechedBonuses()[1] -= Misc.getRandom(2);
			if(target.getCombatAttributes().getLeechedBonuses()[2] > -10)
				target.getCombatAttributes().getLeechedBonuses()[2] -= Misc.getRandom(2);
			attacker.performGraphic(new Graphic(2214));
			sendProjectile(attacker, target, 2215);
			attacker.getPacketSender().sendMessage("You decrease the your Attack, Strength and Defence level..");
		} else if(sapRanger && Misc.getRandom(16) >= 9) {
			if(target.getCombatAttributes().getLeechedBonuses()[4] > -10)
				target.getCombatAttributes().getLeechedBonuses()[4] -= Misc.getRandom(2);
			if(target.getCombatAttributes().getLeechedBonuses()[1] > -10)
				target.getCombatAttributes().getLeechedBonuses()[1] -= Misc.getRandom(2);
			attacker.performGraphic(new Graphic(2217));
			sendProjectile(attacker, target, 2218);
			attacker.getPacketSender().sendMessage("You decrease your target's Ranged level..");
		} else if(sapMage && Misc.getRandom(15) >= 10) {
			if(target.getCombatAttributes().getLeechedBonuses()[6] > -10)
				target.getCombatAttributes().getLeechedBonuses()[6] -= Misc.getRandom(2);
			if(target.getCombatAttributes().getLeechedBonuses()[1] > -10)
				target.getCombatAttributes().getLeechedBonuses()[1] -= Misc.getRandom(2);
			attacker.performGraphic(new Graphic(2220));
			sendProjectile(attacker, target, 2221);
			attacker.getPacketSender().sendMessage("You decrease your target's Magic stat..");
		}
		BonusManager.sendCurseBonuses(attacker);
		if(target instanceof Player)
			BonusManager.sendCurseBonuses((Player) target);
		if(attacker.getCombatAttributes().getAttackDelay() <= 2)
			attacker.performAnimation(new Animation(12575));
	}

	public static void applyTurmoil(Player attacker, GameCharacter target) {
		if(target == null || target.getConstitution() <= 0 || attacker.getConstitution() <= 0)
			return;
		if (Misc.getRandom(5) >= 3) {
			int increase = Misc.getRandom(2);
			attacker.getCombatAttributes().getLeechedBonuses()[increase] += 1;
			BonusManager.sendCurseBonuses(attacker);
		}
	}

	public static void leechSpecialAttack(Player attacker, Player target) {
		if(!(target instanceof Player) || target.getPlayerCombatAttributes().getSpecialAttackAmount() <= 0)
			return;
		int failed = Misc.getRandom(7);
		if (failed == 5) {
			if(target.getPlayerCombatAttributes().getSpecialAttackAmount() > 0) {
				double drain = 0;
				if(attacker.getCurseActive()[CurseHandler.LEECH_SPECIAL_ATTACK])
					drain = 0.6;
				else if(attacker.getCurseActive()[CurseHandler.SAP_SPIRIT])
					drain = 0.4;
				if(drain <= 0.0)
					return;
				attacker.getPlayerCombatAttributes().setSpecialAttackAmount(attacker.getPlayerCombatAttributes().getSpecialAttackAmount() + drain);
				target.getPlayerCombatAttributes().setSpecialAttackAmount(target.getPlayerCombatAttributes().getSpecialAttackAmount() - drain);
				if(attacker.getPlayerCombatAttributes().getSpecialAttackAmount() > 10)
					attacker.getPlayerCombatAttributes().setSpecialAttackAmount(10.00);
				if(target.getPlayerCombatAttributes().getSpecialAttackAmount() < 0)
					target.getPlayerCombatAttributes().setSpecialAttackAmount(0);
				((Player) target).getPacketSender().sendMessage("Your opponent has leeched some of your Special attack energy!");
				attacker.getPacketSender().sendMessage("You drain some Special attack energy from your opponent.");
				if(attacker.getCombatAttributes().getAttackDelay() <= 2)
					attacker.performAnimation(new Animation(12575));
				WeaponHandler.updateSpecialBar(((Player)target));
			}
		}
	}

	public static void leechEnergy(Player attacker, GameCharacter target) {
		if(!(target instanceof Player))
			return;
		int failed = Misc.getRandom(7);
		if (failed == 5) {
			Player p = (Player)target;
			if(p.getAttributes().getRunEnergy() > 0) {
				int drain = 6;
				attacker.getAttributes().setRunEnergy(attacker.getAttributes().getRunEnergy() + drain);
				p.getAttributes().setRunEnergy(p.getAttributes().getRunEnergy() - drain);
				if(attacker.getAttributes().getRunEnergy() > 100)
					attacker.getAttributes().setRunEnergy(100);
				if(p.getAttributes().getRunEnergy() < 0)
					p.getAttributes().setRunEnergy(0);
				((Player) target).getPacketSender().sendMessage("Your opponent has leeched some of your Run energy!");
				attacker.getPacketSender().sendMessage("You drain some Run energy from your opponent.");
				if(attacker.getCombatAttributes().getAttackDelay() <= 2)
					attacker.performAnimation(new Animation(12575));
			}
		}
	}

	public static void sendProjectile(Player attacker, GameCharacter target, int gfxId) {
		attacker.getPacketSender().sendProjectile(new Projectile(attacker.getPosition(), target.getPosition(), new Graphic(gfxId)), target);
	}

	/**
	 * Handles effects such as prayers, items and damage effects.
	 * @param attacker	Attacker who's attacking
	 * @param target	Target who's being attacked
	 * @param damage	Damage that the Attacker will deal
	 */
	public static void handleEffects(final GameCharacter attacker, final GameCharacter target, final Damage damage) {
		Player player;
		final int dmg = damage.getHits()[0].getDamage();
		Player trg = null;
		if(target.isPlayer())
			trg = (Player) target;
		if(attacker.isPlayer()) {
			player = (Player) attacker;
			player.getPacketSender().sendInterfaceRemoval();
			WeaponPoison.handleWeaponPoison(player, target);
			CombatExtras.applyLeeches(player, target);
			CombatExtras.applySaps(player, target);
			if(attacker.getCurseActive()[CurseHandler.LEECH_SPECIAL_ATTACK] || attacker.getCurseActive()[CurseHandler.SAP_SPIRIT] && trg != null)
				CombatExtras.leechSpecialAttack(player, trg);
			if(attacker.getCurseActive()[CurseHandler.LEECH_ENERGY])
				CombatExtras.leechEnergy(player, target);
			if(attacker.getCurseActive()[CurseHandler.TURMOIL])
				CombatExtras.applyTurmoil(player, target);
			if (player.getCurseActive()[CurseHandler.SOUL_SPLIT] && dmg > 0)
				CombatExtras.handleSoulSplit(player, dmg, target);
			if (attacker.getPrayerActive()[PrayerHandler.SMITE] && dmg > 0 && trg != null)
					CombatExtras.handleSmite(player, dmg, (Player) trg);
			//Guthan's
			if(player.getConstitution() < player.getSkillManager().getMaxLevel(Skill.CONSTITUTION) && Misc.getRandom(13) <= 2 && player.getEquipment().getItems()[3].getId() == 4726 && player.getEquipment().getItems()[0].getId() == 4724 && player.getEquipment().getItems()[4].getId() == 4728 && player.getEquipment().getItems()[7].getId() == 4730) {
				player.setConstitution((int) (player.getConstitution() + dmg));
				target.performGraphic(new Graphic(398));
				attacker.performGraphic(new Graphic(2044, GraphicHeight.LOW));
			} else
				//Ahrim's
				if(Misc.getRandom(13) <= 2 && player.getEquipment().getItems()[3].getId() == 4710 && player.getEquipment().getItems()[0].getId() == 4708 && player.getEquipment().getItems()[4].getId() == 4712 && player.getEquipment().getItems()[7].getId() == 4714) {
					if(target.isPlayer()) {
						trg.getSkillManager().setCurrentLevel(Skill.STRENGTH, trg.getSkillManager().getCurrentLevel(Skill.STRENGTH) - Misc.getRandom(5));
						if(trg.getSkillManager().getCurrentLevel(Skill.STRENGTH) <= 0)
							trg.getSkillManager().setCurrentLevel(Skill.STRENGTH, 1);
					}/* else
						((NPC)target).strengthLevel -= Misc.random(2);*/
					target.performGraphic(new Graphic(400));
					player.getPacketSender().sendMessage("You have drained your target's Strength level!");
				} else if(trg != null) {
					//Karil's
					if(Misc.getRandom(13) <= 2 && player.getEquipment().getItems()[3].getId() == 4734 && player.getEquipment().getItems()[0].getId() == 4732 && player.getEquipment().getItems()[4].getId() == 4736 && player.getEquipment().getItems()[7].getId() == 4738) {
						trg.getSkillManager().setCurrentLevel(Skill.AGILITY, (int) (trg.getSkillManager().getCurrentLevel(Skill.AGILITY) - Math.floor(dmg / 10)));
						if(trg.getSkillManager().getCurrentLevel(Skill.AGILITY) <= 0)
							trg.getSkillManager().setCurrentLevel(Skill.AGILITY, 0);
						player.getPacketSender().sendMessage("Your attacks have successfully drained the opponent's Agility level.");
						trg.performGraphic(new Graphic(401));
					} else
						//Torags
						if(Misc.getRandom(13) <= 2 && player.getEquipment().getItems()[3].getId() == 4747 && player.getEquipment().getItems()[0].getId() == 4745 && player.getEquipment().getItems()[4].getId() == 4749 && player.getEquipment().getItems()[7].getId() == 4751) {
							trg.getAttributes().setRunEnergy(trg.getAttributes().getRunEnergy() - 20);
							if(trg.getAttributes().getRunEnergy() <= 0)
								trg.getAttributes().setRunEnergy(0);
							player.getPacketSender().sendMessage("Your attacks have successfully drained the opponent's energy.");
							trg.performGraphic(new Graphic(399));
						}
				}
		}
		if(trg != null) {
			if(trg.getConstitution() > 0 && trg.getEquipment().getItems()[Equipment.AMULET_SLOT].getId() == 11090 && damage.getHits()[0].getDamage() > 0) 
				CombatExtras.handlePhoenixNecklace(trg);
			if(trg.getConstitution() > 0 && trg.getEquipment().getItems()[Equipment.RING_SLOT].getId() == 2570 && damage.getHits()[0].getDamage() > 0 && !target.isTeleporting()) 
				CombatExtras.handleRingofLife(trg);
			if(trg.getConstitution() > 0 && trg.getPrayerActive()[PrayerHandler.REDEMPTION])
				CombatExtras.handleRedemption(trg);
			if(trg.getConstitution() > 0 && trg.getEquipment().getItems()[Equipment.RING_SLOT].getId() == 2550 && damage.getHits()[0].getDamage() > 0) 
				CombatExtras.handleRecoil(trg, dmg, attacker);
			if(trg.getConstitution() <= 0 && trg.getCurseActive()[CurseHandler.WRATH])
				CombatExtras.handleWrath(trg, attacker);
			if(trg.getConstitution() <= 0 && trg.getPrayerActive()[PrayerHandler.RETRIBUTION])
				CombatExtras.handleRetribution(trg, attacker);
			if(trg.getPlayerCombatAttributes().hasVengeance() && dmg > 0 && attacker.getConstitution() > 0) {
				int vengDamage = (int)(dmg * 0.75);
				if (vengDamage > attacker.getConstitution())
					vengDamage = attacker.getConstitution();
				attacker.setDamage(new Damage(new Hit(vengDamage, CombatIcon.DEFLECT, Hitmask.RED)));
				trg.forceChat("Taste Vengeance!");
				trg.getPlayerCombatAttributes().setVengeance(false);
				trg.getCombatAttributes().setLastDamageReceived(System.currentTimeMillis());
			}
			ItemDegrading.handleItemDegrading(trg, true);
			TeleportHandler.cancelCurrentActions(trg);
		}
		player = trg = null;
	}
}
