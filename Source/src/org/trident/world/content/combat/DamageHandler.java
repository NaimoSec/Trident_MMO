package org.trident.world.content.combat;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Position;
import org.trident.model.Projectile;
import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.audio.SoundEffects.SoundData;
import org.trident.world.content.combat.combatdata.bonusdata.EquipmentBonus;
import org.trident.world.content.combat.combatdata.bonusdata.EquipmentEffects;
import org.trident.world.content.combat.combatdata.magic.CombatSpell;
import org.trident.world.content.combat.combatdata.melee.MeleeData;
import org.trident.world.content.combat.combatdata.ranged.RangedData;
import org.trident.world.content.combat.combatdata.ranged.RangedExtras;
import org.trident.world.content.combat.combatdata.ranged.RangedData.AmmunitionData;
import org.trident.world.content.combat.combatdata.ranged.RangedData.RangedWeaponData;
import org.trident.world.content.combat.combatdata.ranged.RangedData.Type;
import org.trident.world.content.combat.weapons.WeaponHandler.ExperienceStyle;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.content.skills.SkillManager;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.content.skills.impl.slayer.SlayerTasks;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCData.CustomNPCData;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

/**
 * @author Gabbe
 * Note: I didn't make the formulas.
 * Note2: This is a shitty way of doing combat but it does the job well.
 */
public class DamageHandler {

	/**
	 * Handles set damage and damage-related effects such as Prayers/Curses and items.
	 * @param attacker	The attacker who's going to set damage on target.
	 * @param target The target who's going to take damage from the attacker.
	 * @param damage	The damage which attacker is going to deal on target.
	 */
	public static void handleAttack(final GameCharacter attacker, final GameCharacter target, final Damage damage, final AttackType attackType, final boolean specialAttack, final boolean usingCannon) {
		if(!usingCannon)
			attacker.setEntityInteraction(target);
		/*
		 * Damage reduction
		 */
		final int dmg = damage.getHits()[0].getDamage();
		double damageReductionMultiplier = 1;
		boolean PvP = attacker.isPlayer() && target.isPlayer();
		if(dmg > 0) {
			switch(attackType) {
			case MELEE:
				if(target.getCurseActive()[CurseHandler.DEFLECT_MELEE]) {
					if(Misc.getRandom(10) <= 4)
						CombatExtras.handleDeflectPrayers(attacker, target, 2, damage.getHits()[0].getDamage());
					damageReductionMultiplier -= PvP ? 0.20 : 0.96;
				} else if(target.getPrayerActive()[PrayerHandler.PROTECT_FROM_MELEE])
					damageReductionMultiplier -= PvP ? 0.20 : 0.96;
				if(target.getCombatAttributes().hasStaffOfLightEffect()) {
					damageReductionMultiplier -= 0.5;
					target.performGraphic(new Graphic(2319));
				}
				break;
			case MAGIC:
				if(target.getCurseActive()[CurseHandler.DEFLECT_MAGIC]) {
					if(Misc.getRandom(10) <= 4)
						CombatExtras.handleDeflectPrayers(attacker, target, 0, dmg);
					damageReductionMultiplier -= PvP ? 0.20 : 0.96;
				} else if(target.getPrayerActive()[PrayerHandler.PROTECT_FROM_MAGIC])
					damageReductionMultiplier -= PvP ? 0.20 : 0.96;
				break;
			case RANGED:
				if(target.getCurseActive()[CurseHandler.DEFLECT_MISSILES]) {
					if(Misc.getRandom(10) <= 4)
						CombatExtras.handleDeflectPrayers(attacker, target, 1, dmg);
					damageReductionMultiplier -= PvP ? 0.20 : 0.96;
				} else if(target.getPrayerActive()[PrayerHandler.PROTECT_FROM_MISSILES])
					damageReductionMultiplier -= PvP ? 0.20 : 0.96;
				break;
			}
			if(target.isPlayer() && dmg != 0)
				damageReductionMultiplier = EquipmentEffects.getSpiritShieldEffects((Player)target, dmg, damageReductionMultiplier);
			boolean ignoreReduction = attacker.isNpc() && ((NPC)attacker).getId() == 2030; //Verac hits through prayers
			if(!ignoreReduction)
				for(int i = 0; i < damage.getHits().length; i++) {
					damage.getHits()[i].setDamage((int) (damage.getHits()[i].getDamage() * damageReductionMultiplier));
					if(damage.getHits()[i].getDamage() < 0) 
						damage.getHits()[i].setDamage(0);
					else if(damage.getHits()[i].getDamage() > target.getConstitution())
						damage.getHits()[i].setDamage(target.getConstitution());
					if(i == 1) { //double hit, the second hit damage should be 0 if the first hit killed it
						if((target.getConstitution() - damage.getHits()[0].getDamage()) <= 0)
							damage.getHits()[i].setDamage(0);
					}
				}
		}
		//Experience reward for player
		final ExperienceStyle experienceReward = attacker.isPlayer() ? attackType == AttackType.MAGIC ? ExperienceStyle.MAGIC : ((Player)attacker).getPlayerCombatAttributes().getAttackStyle().getExperienceReward() : null;
		/*
		 * Setting the actual damage
		 */
		if(!usingCannon) {
			final Player plr = attacker.isPlayer() ? (Player) attacker : null;
			final Position startingPos =  attacker.isPlayer() ? new Position(plr.getPosition().getX(), plr.getPosition().getY(), 43) : null;
			final Position endingPos = attacker.isPlayer() ? new Position(target.getPosition().getX(), target.getPosition().getY(), 31) : null;
			switch(attackType) {
			case MELEE:
				TaskManager.submit(new Task(1, false) {
					@Override
					public void execute() {
						target.setDamage(damage);
						target.getCombatAttributes().setLastDamageReceived(System.currentTimeMillis());
						if(target.getCombatAttributes().getAttackDelay() <= 2 && !(target.isNpc() && target.getLocation() == Location.PEST_CONTROL_GAME))
							target.performAnimation(target.getBlockAnimation());
						CombatHandler.handleAutoRetaliate(attacker, target);
						stop();
					}
				});
				break;
			case MAGIC:
				if(plr != null) {
					plr.setEntityInteraction(target);
					if(!specialAttack) { // Korasi's
						if(plr.getPlayerCombatAttributes().getMagic().getCurrentSpell() == null)
							return;
						plr.getPlayerCombatAttributes().getMagic().getCurrentSpell().castSpell(plr, target, damage);
					} else if(specialAttack && plr != null) { //Korasi's sword				
						target.setDamage(damage);
						CombatHandler.handleAutoRetaliate(attacker, target);
						CombatHandler.addExperience(plr, damage, ExperienceStyle.MAGIC);
					}
				} else {
					target.setDamage(damage);
					CombatHandler.handleAutoRetaliate(attacker, target);
				}
				break;
			case RANGED:
				if(plr != null && !plr.getPlayerCombatAttributes().threwChinchompa()) {
					final boolean usingDbow = plr.getPlayerCombatAttributes().getRangedWeaponData().getType() == RangedData.Type.DARK_BOW;
					final boolean magicBow = plr.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 861;
					final boolean handCannon = plr.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 15241;
					final boolean chinchompa = plr.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 10033 || plr.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 10034;
					final AmmunitionData rangedAmmunitionData = RangedWeaponData.getAmmunitionData(plr);
					if(!specialAttack && rangedAmmunitionData.getStartGfxId() > 0)
						plr.performGraphic(new Graphic(rangedAmmunitionData.getStartGfxId(), handCannon ? GraphicHeight.MIDDLE : GraphicHeight.HIGH));
					/*
					 * Dropping ammo
					 */
					final int ammunition = plr.getEquipment().getItems()[plr.getPlayerCombatAttributes().getRangedWeaponData().getType() == Type.THROW ? Equipment.WEAPON_SLOT : Equipment.AMMUNITION_SLOT].getId();
					RangedExtras.dropArrows(plr, target.getPosition());
					if(usingDbow) {
						RangedExtras.dropArrows(plr, target.getPosition());
						if(specialAttack)
							target.performGraphic(new Graphic(1100, GraphicHeight.HIGH));
					}
					/*
					 * Setting damage
					 */
					TaskManager.submit(new Task(chinchompa ? 1 : 2, false) {
						int gfx = rangedAmmunitionData.getProjectileId();
						@Override
						public void execute() {
							if(specialAttack) {
								if(magicBow)
									gfx = 256;
								else if(usingDbow)
									gfx = 1099;
							}
							if(gfx > 0)
								plr.getPacketSender().sendGlobalProjectile(new Projectile(startingPos, endingPos, new Graphic(gfx, GraphicHeight.HIGH), rangedAmmunitionData.getProjectileDelay(), 50, rangedAmmunitionData.getProjectileSpeed()), target);		
							if(chinchompa) {
								RangedExtras.handleChinchompaExplosion(plr, target);
								stop();
								return;
							}
							if(usingDbow || magicBow && specialAttack) {
								if(target.isPlayer()) {
									startingPos.setZ(53);
									plr.getPacketSender().sendGlobalProjectile(new Projectile(startingPos, endingPos, new Graphic(gfx), rangedAmmunitionData.getProjectileDelay(), 50, rangedAmmunitionData.getProjectileSpeed()), target);	
								} else if(target.isNpc()) {
									startingPos.setZ(60);
									plr.getPacketSender().sendGlobalProjectile(new Projectile(startingPos, endingPos, new Graphic(gfx), rangedAmmunitionData.getProjectileDelay(), 50, rangedAmmunitionData.getProjectileSpeed()), target);	
								}
								if(specialAttack && usingDbow) {
									for(int i = 0; i < damage.getHits().length; i++)
										if(damage.getHits()[i].getDamage() < 80)
											damage.getHits()[i].setDamage(80);
								}
							}
							if (!specialAttack && !usingCannon && Misc.getRandom(14) <= 2) {
								if(plr.getPlayerCombatAttributes().getRangedWeaponData() != null && plr.getPlayerCombatAttributes().getRangedWeaponData().getType() == RangedData.Type.CROSSBOW && RangedWeaponData.getAmmunitionData(plr).hasSpecialEffect()) 
									damage.getHits()[0].setDamage(RangedExtras.getBoltSpecialDamage(plr, target, ammunition, damage.getHits()[0].getDamage()));
							}
							if(target.getCombatAttributes().getAttackDelay() <= 2 && !(target.isNpc() && target.getLocation() == Location.PEST_CONTROL_GAME))
								target.performAnimation(target.getBlockAnimation());
							CombatHandler.handleAutoRetaliate(attacker, target);
							target.setDamage(damage);
							target.getCombatAttributes().setLastDamageReceived(System.currentTimeMillis());
							stop();
						}
					});
				} else {
					target.setDamage(damage);
					CombatHandler.handleAutoRetaliate(attacker, target);
				}
				break;
			}
		} else if(usingCannon && attacker.isPlayer() && target.isNpc()) {
			((Player)attacker).getSkillManager().addExperience(Skill.RANGED, (int) (((damage.getHits()[0].getDamage() * .20) * Skill.RANGED.getExperienceMultiplier())), false);
			TaskManager.submit(new Task(1, false) {
				@Override
				public void execute() {
					target.setDamage(damage);
					SoundEffects.sendSoundEffect(((Player)attacker), SoundData.FIRING_CANNON, 9, 0);
					if(target.getCombatAttributes().getAttackDelay() <= 2 && !(target.isNpc() && target.getLocation() == Location.PEST_CONTROL_GAME))
						target.performAnimation(target.getBlockAnimation());
					CombatHandler.handleAutoRetaliate(attacker, target);
					stop();
				}
			});
		} 
		if(attacker.isPlayer()) {
			Player plr = (Player)attacker;
			CombatHandler.addExperience((Player) attacker, damage, experienceReward);
			SoundEffects.handleCombatAttack(attacker, target);
			SoulWars.handleActivity(plr, 3, true, true);
			if(plr.getLocation() == Location.PEST_CONTROL_GAME)
				plr.getAttributes().getMinigameAttributes().getPestControlAttributes().setDamageDealt(plr.getAttributes().getMinigameAttributes().getPestControlAttributes().getDamageDealt() + damage.getHits()[0].getDamage());
			for(Hit hit : damage.getHits()) {
				if(hit != null) 
					addDamage(plr, target, hit);
			}
			if(target.isNpc() && ((NPC)target).getId() == 13447)
				CustomNPC.getNex().takeDamage(plr, damage.getHits()[0].getDamage());
			plr = null;
		} else if(target.isPlayer() && attacker.isNpc() && ((NPC)attacker).getId() == 13447)
			CustomNPC.getNex().dealtDamage((Player)target, damage.getHits()[0].getDamage());
		if(!usingCannon)
			CombatExtras.handleEffects(attacker, target, damage);
		target.getCombatAttributes().setLastAttacker(attacker).setLastDamageReceived(System.currentTimeMillis());
	}

	/**
	 * Get's the damage an entity will deal to another.
	 * @param attacker		The attacker to use to calculate the damage
	 * @param target		The target which will be receving this damage
	 * @return				The damage which the attacker will deal to the target
	 * 
	 * NOTE: These formulas don't currently differ much for PVM/PVP etc. Need to change that.
	 */
	public static Damage getDamage(GameCharacter attacker, GameCharacter target) {
		int damage = 0, damage2 = 0, maxHit = 0;
		boolean crit = false;
		boolean darkBow = attacker.isPlayer() && ((Player)attacker).getPlayerCombatAttributes().getRangedWeaponData() != null && ((Player)attacker).getPlayerCombatAttributes().getRangedWeaponData().getType() == RangedData.Type.DARK_BOW;
		/*
		 * Player vs Player
		 */
		if(attacker.isPlayer() && target.isPlayer()) {
			Player player = (Player)attacker;
			Player opponent = (Player)target;
			switch(attacker.getCombatAttributes().getAttackType()) {
			case MELEE:
				maxHit = getBaseMeleeDamage(player);
				damage = Misc.getRandom(maxHit);
				if(Misc.getRandom(1 + getMeleeDefence(opponent)) > Misc.getRandom(1 + getMeleeAttack(player)))
					damage = 0;
				else if(damage >= maxHit * 0.94)
					crit = true;
				break;
			case RANGED:
				maxHit = getRangedMaxHit(player);
				damage = Misc.getRandom(maxHit);
				if(Misc.getRandom(10 + getRangedDefence(opponent)) > Misc.getRandom(10 + getRangedAttack(player)))
					damage = 0;
				else if(damage >= maxHit * 0.94)
					crit = true;
				if(darkBow)
					damage2 = Misc.getRandom(maxHit);
				break;
			case MAGIC:
				maxHit = getMagicMaxhit(player);
				damage = Misc.getRandom(maxHit);
				if(Misc.getRandom(1 + getMagicDefence(opponent)) > Misc.getRandom(12 + getMagicAttack(player)))
					damage = 0;
				else if(damage >= maxHit * 0.94)
					crit = true;
				break;
			}
		} else
			/*
			 * Player vs NPC
			 * TODO: Improve it and make it much more advanced than this rofl, kinda feels like pvp rip off
			 */
			if(attacker.isPlayer() && target.isNpc()) {
				Player player = (Player)attacker;
				NPC opponent = (NPC)target;
				switch(attacker.getCombatAttributes().getAttackType()) {
				case MELEE:
					maxHit = getBaseMeleeDamage(player);
					damage = Misc.getRandom(maxHit);
					if(Misc.getRandom(opponent.getAttributes().getDefenceLevel() + opponent.getAttributes().getAbsorbMelee()) > 10 + Misc.getRandom(getMeleeAttack(player)))
						damage = 0;
					else if(damage >= maxHit * 0.94)
						crit = true;
					break;
				case RANGED:
					maxHit = getRangedMaxHit(player);
					damage = Misc.getRandom(maxHit);
					if(Misc.getRandom(opponent.getAttributes().getDefenceLevel() + opponent.getAttributes().getAbsorbRanged()) > 5 + Misc.getRandom(getRangedAttack(player)))
						damage = 0;
					else if(damage >= maxHit * 0.94)
						crit = true;
					if(darkBow)
						damage2 = Misc.getRandom(maxHit);
					break;
				case MAGIC:
					maxHit = getMagicMaxhit(player);
					damage = Misc.getRandom(maxHit);
					if(Misc.getRandom(opponent.getAttributes().getDefenceLevel() + opponent.getAttributes().getAbsorbMagic()) > 16 + Misc.getRandom(getMagicAttack(player)))
						damage = 0;
					else if(damage >= maxHit * 0.94)
						crit = true;
					break;
				}
			} else 
				/*
				 * NPC vs Player
				 * TODO: Improve it and make it much more advanced than this rofl, add support for other attack types
				 */
				if(attacker.isNpc() && target.isPlayer()) {
					NPC npc = (NPC)attacker;
					Player player = (Player)target;
					if(attacker.getCombatAttributes().getAttackType().equals(AttackType.MELEE)) {
						double absorbMelee = player.getAttributes().getBonusManager().getDefenceBonus()[6];
						int i = (int) ((int) player.getAttributes().getBonusManager().getDefenceBonus()[MeleeData.bestMeleeDef(player)] + absorbMelee);
						int protect = Misc.getRandom((int)((player.getSkillManager().getCurrentLevel(Skill.DEFENCE) * 0.40) + (i * 0.25))) + 8;
						if(protect < npc.getAttributes().getAttackLevel()) { //Player should not take any damage
							int randomStrength = Misc.getRandom(npc.getAttributes().getStrengthLevel());
							damage = (int) (randomStrength + (Misc.getRandom(npc.getAttributes().getAttackLevel()) * 0.05) - (protect * 0.3));
							int max = npc.getAttributes().getMaxHit();
							if(damage > max)
								damage = max;
						}
					} else
						return null; //only support for melee/range atm
				}
		if(damage < 0)
			damage = 0;
		if(darkBow) {
			if(damage < 80)
				damage = 80;
			if(damage2 < 80)
				damage2 = 80;
		}
		return darkBow ? new Damage(new Hit(damage, CombatIcon.forId(attacker.getCombatAttributes().getAttackType().ordinal()), crit ? Hitmask.CRITICAL : Hitmask.RED), new Hit(damage2, CombatIcon.forId(attacker.getCombatAttributes().getAttackType().ordinal()), crit ? Hitmask.CRITICAL : Hitmask.RED)) : new Damage(new Hit(damage, CombatIcon.forId(attacker.getCombatAttributes().getAttackType().ordinal()), crit ? Hitmask.CRITICAL : Hitmask.RED));
	}

	/**
	 * Adds damage to the target's hit map, used for finding the killer of target later on.
	 * @param attacker
	 * @param target
	 * @param hit
	 */
	public static void addDamage(Player attacker, GameCharacter target, Hit hit) {
		if (hit.getDamage() < 1)
			return;
		Player player = (Player) attacker;
		if (target.getCombatAttributes().getHitMap().containsKey(player)) {
			target.getCombatAttributes().getHitMap().get(player).incrementDamage(hit.getDamage());
			return;
		}
		target.getCombatAttributes().getHitMap().put(player, hit);
	}
	
	/*==============================================================================*/
	/*===================================MELEE=====================================*/

	/**
	 * Calculates a player's Melee attack level (how likely that they're going to hit through defence)
	 * @param plr	The player's Meelee attack level
	 * @return		The player's Melee attack level
	 */
	private static int getMeleeAttack(Player plr) {
		int attackLevel = plr.getSkillManager().getCurrentLevel(Skill.ATTACK);
		if(plr.getCombatAttributes().getAttackStyle().getExperienceReward() == ExperienceStyle.ATTACK)
			attackLevel += 1;
		boolean hasVoid = EquipmentBonus.wearingVoid(plr, AttackType.MELEE);
		if (plr.getPrayerActive()[2]) {
			attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.05;
		} else if (plr.getPrayerActive()[7]) {
			attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.1;
		} else if (plr.getPrayerActive()[15]) {
			attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.15;
		} else if (plr.getPrayerActive()[24]) {
			attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.15;
		} else if (plr.getPrayerActive()[25]) {
			attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.2;
		}
		else if (plr.getCurseActive()[CurseHandler.TURMOIL]) { // turmoil
			attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK)
					* 0.5 + plr.getCombatAttributes().getLeechedBonuses()[2];
		}
		if (hasVoid) {
			attackLevel += plr.getSkillManager().getMaxLevel(Skill.ATTACK) * 0.1;
		}
		attackLevel *= plr.getPlayerCombatAttributes().getSpecialAttack() != null ? plr.getPlayerCombatAttributes().getSpecialAttack().getAccuracy() : 1;
		int i = (int) plr.getAttributes().getBonusManager().getAttackBonus()[MeleeData.bestMeleeAtk(plr)];
		if (MeleeData.hasObsidianEffect(plr) || hasVoid)
			i *= 1.30;
		/*
		 * Slay helm
		 */
		if(plr.getAdvancedSkills().getSlayer().getSlayerTask() != null && (plr.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 13263 || plr.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 15492)) {
			if(CombatHandler.inCombat(plr) && plr.getCombatAttributes().getCurrentEnemy().isNpc()) {
				NPC n = (NPC)plr.getCombatAttributes().getCurrentEnemy();
				if(n != null && n.getId() == plr.getAdvancedSkills().getSlayer().getSlayerTask().getNpcId())
					i *= 1.12;
			}
		}
		return (int)(attackLevel + (attackLevel * 0.15) + (i + i * 0.05));
	}

	/**
	 * Calculates a player's Melee Defence level
	 * @param plr		The player to calculate Melee defence for
	 * @return		The player's Melee defence level
	 */
	public static int getMeleeDefence(Player plr) {
		int defenceLevel = plr.getSkillManager().getCurrentLevel(Skill.DEFENCE);
		int i = (int) plr.getAttributes().getBonusManager().getDefenceBonus()[MeleeData.bestMeleeDef(plr)];
		if (plr.getPrayerActive()[0]) {
			defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.05;
		} else if (plr.getPrayerActive()[5]) {
			defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.1;
		} else if (plr.getPrayerActive()[13]) {
			defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.15;
		} else if (plr.getPrayerActive()[24]) {
			defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.2;
		} else if (plr.getPrayerActive()[25]) {
			defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.25;
		} else if (plr.getCurseActive()[CurseHandler.TURMOIL]) {
			defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.15;
		}
		return (int)(defenceLevel + (defenceLevel * 0.15) + (i + i * 0.05));
	}

	/**
	 * Calculates a player's base damage
	 * @param plr		The player to calculate base damage for
	 * @return			The player's base damage
	 */
	public static int getBaseMeleeDamage(Player plr) {
		double base = 0;
		double effective = MeleeData.getEffectiveStr(plr);
		double specialBonus = plr.getPlayerCombatAttributes().getSpecialAttack() != null ? plr.getPlayerCombatAttributes().getSpecialAttack().getMultiplier() : 1;
		double strengthBonus = plr.getAttributes().getBonusManager().getOtherBonus()[0];
		base = (13 + effective + (strengthBonus / 8) + ((effective * strengthBonus) / 64)) / 10;
		if(plr.getEquipment().getItems()[3].getId() == 4718 && plr.getEquipment().getItems()[0].getId() == 4716 && plr.getEquipment().getItems()[4].getId() == 4720 && plr.getEquipment().getItems()[7].getId() == 4722)
			base += ((plr.getSkillManager().getMaxLevel(Skill.CONSTITUTION) - plr.getConstitution()) * .06) + 1;
		if (plr.getPlayerCombatAttributes().isUsingSpecialAttack())
			base = (base * specialBonus);
		if (MeleeData.hasObsidianEffect(plr) || EquipmentBonus.wearingVoid(plr, AttackType.MELEE))
			base = (base * 1.2);

		/*
		 * Salve amulet against undead creatures
		 */
		if(plr.getEquipment().getItems()[Equipment.AMULET_SLOT].getId() == 4081) {
			if(plr.getCombatAttributes().getCurrentEnemy() != null && plr.getCombatAttributes().getCurrentEnemy().isNpc()) {
				NPC n = (NPC)plr.getCombatAttributes().getCurrentEnemy();
				if(n != null && CustomNPCData.isUndeadNpc(n))
					base = (base * 1.15);
			}
		}

		base *= 10;
		return (int) Math.floor(base);
	}

	/*==============================================================================*/
	/*===================================RANGED=====================================*/

	/**
	 * Calculates a player's Ranged attack (level).
	 * Credits: Dexter Morgan
	 * @param plr	The player to calculate Ranged attack level for
	 * @return		The player's Ranged attack level
	 */
	private static int getRangedAttack(Player plr) {
		int rangeLevel = plr.getSkillManager().getCurrentLevel(Skill.RANGED);
		boolean hasVoid = EquipmentBonus.wearingVoid(plr, AttackType.RANGED);
		double accuracy = plr.getPlayerCombatAttributes().getSpecialAttack() != null ? plr.getPlayerCombatAttributes().getSpecialAttack().getAccuracy() : 1;
		rangeLevel *= accuracy;
		if (hasVoid){
			rangeLevel += SkillManager.getLevelForExperience(plr.getSkillManager().getExperience(Skill.RANGED)) * 0.1;
		}
		if (plr.getCurseActive()[PrayerHandler.SHARP_EYE] || plr.getCurseActive()[CurseHandler.SAP_RANGER]) {
			rangeLevel *= 1.05;
		} else if (plr.getPrayerActive()[PrayerHandler.HAWK_EYE]) {
			rangeLevel *= 1.10;
		} else if (plr.getPrayerActive()[PrayerHandler.EAGLE_EYE]) {
			rangeLevel *= 1.15;
		} else if(plr.getCurseActive()[CurseHandler.LEECH_RANGED]) {
			rangeLevel *= 1.10;
		}
		if (hasVoid && accuracy > 1.15)
			rangeLevel *= 1.65;
		/*
		 * Slay helm
		 */
		if(plr.getAdvancedSkills().getSlayer().getSlayerTask() != null && plr.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 15492) {
			if(plr.getCombatAttributes().getCurrentEnemy() != null && plr.getCombatAttributes().getCurrentEnemy().isNpc()) {
				NPC n = (NPC)plr.getCombatAttributes().getCurrentEnemy();
				if(n != null && n.getId() == plr.getAdvancedSkills().getSlayer().getSlayerTask().getNpcId())
					rangeLevel *= 1.12;
			}
		}
		return (int) (rangeLevel + (plr.getAttributes().getBonusManager().getAttackBonus()[4] * 1.95));
	}

	/**
	 * Calculates a player's Ranged defence level.
	 * @param plr		The player to calculate the Ranged defence level for
	 * @return			The player's Ranged defence level
	 */
	public static int getRangedDefence(Player plr) {
		int defenceLevel = plr.getSkillManager().getCurrentLevel(Skill.DEFENCE);
		if (plr.getPrayerActive()[PrayerHandler.THICK_SKIN]) {
			defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.05;
		} else if (plr.getPrayerActive()[PrayerHandler.ROCK_SKIN]) {
			defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.1;
		} else if (plr.getPrayerActive()[PrayerHandler.STEEL_SKIN]) {
			defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.15;
		} else if (plr.getPrayerActive()[PrayerHandler.CHIVALRY]) {
			defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.2;
		} else if (plr.getPrayerActive()[PrayerHandler.PIETY]) {
			defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.25;
		} else if (plr.getCurseActive()[CurseHandler.TURMOIL]) { // turmoil
			defenceLevel += plr.getSkillManager().getMaxLevel(Skill.DEFENCE)
					* 0.20 + plr.getCombatAttributes().getLeechedBonuses()[0];
		}
		return (int) (defenceLevel + plr.getAttributes().getBonusManager().getDefenceBonus()[4] + (plr.getAttributes().getBonusManager().getDefenceBonus()[4] / 2));
	}

	/**
	 * Calculates a player's max hit with ranged.
	 * Credits: Dexter Morgan
	 * @param plr	The player to calculate Ranged max hit for
	 * @return		The player's Ranged max hit
	 */
	public static int getRangedMaxHit(Player plr) {
		int a = plr.getSkillManager().getCurrentLevel(Skill.RANGED);
		boolean hasVoid = EquipmentBonus.wearingVoid(plr, AttackType.RANGED);
		AmmunitionData ammunitionData = RangedWeaponData.getAmmunitionData(plr);
		int d = 0;
		if(ammunitionData != null)
			d = ammunitionData.getStrength();
		double b = 1.00;
		if (plr.getPrayerActive()[PrayerHandler.SHARP_EYE])
			b *= 1.05;
		else if (plr.getPrayerActive()[PrayerHandler.HAWK_EYE])
			b *= 1.10;
		else if (plr.getPrayerActive()[PrayerHandler.EAGLE_EYE])
			b *= 1.15;
		else if (plr.getCurseActive()[CurseHandler.LEECH_RANGED])
			b *= 1.15;
		if (hasVoid)
			b *= 1.20;
		double e = Math.floor(a * b);
		if(plr.getCombatAttributes().getAttackStyle().getIndex() == 0) //Accurate
			e = (e + 3.0);
		/*
		 * Broad bolts/arrows
		 */
		if(plr.getEquipment().getItems()[Equipment.AMMUNITION_SLOT].getId() == 4160 || plr.getEquipment().getItems()[Equipment.AMMUNITION_SLOT].getId() == 13280) {
			if(plr.getAdvancedSkills().getSlayer().getSlayerTask() != SlayerTasks.NO_TASK && plr.getCombatAttributes().getCurrentEnemy() != null && plr.getCombatAttributes().getCurrentEnemy().isNpc()) {
				NPC enemy = World.getNpcs().get(plr.getCombatAttributes().getCurrentEnemy().getIndex());
				if(enemy != null && plr.getAdvancedSkills().getSlayer().getSlayerTask().getNpcId() == enemy.getId())
					e = plr.getEquipment().getItems()[Equipment.AMMUNITION_SLOT].getId() == 4160 ? (e + 5.0) : (e + 8.5);
			}
		}
		double darkbow = 1.0;
		if(plr.getPlayerCombatAttributes().isUsingSpecialAttack()) {
			int weapon = plr.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId();
			if(weapon == 11235 || weapon == 15701 || weapon == 15702 || weapon == 15703 || weapon == 15704) {
				if(plr.getEquipment().getItems()[Equipment.AMMUNITION_SLOT].getId() == 11212) {
					darkbow = 1.5;
				} else {
					darkbow = 1.3;
				}
			}
		}
		double max = (1.3 + e/10 + d/80 + e*d/640) * darkbow;
		int dmg = (int) max * 9;
		/*
		 * Slay helm
		 */
		if(plr.getAdvancedSkills().getSlayer().getSlayerTask() != null && plr.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 15492) {
			if(plr.getCombatAttributes().getCurrentEnemy() != null && plr.getCombatAttributes().getCurrentEnemy().isNpc()) {
				NPC n = (NPC)plr.getCombatAttributes().getCurrentEnemy();
				if(n != null && n.getId() == plr.getAdvancedSkills().getSlayer().getSlayerTask().getNpcId())
					dmg *= 1.12;
			}
		}
		return dmg;
	}

	/*==============================================================================*/
	/*===================================MAGIC=====================================*/

	/**
	 * Calculates a player's magic attack level
	 * @param plr				The player to calculate mage attack for
	 * @return				The player's magic attack level
	 */
	public static int getMagicAttack(Player plr) {
		boolean voidEquipment = EquipmentBonus.wearingVoid(plr, AttackType.MAGIC);
		int attackLevel = plr.getSkillManager().getCurrentLevel(Skill.MAGIC);
		if (voidEquipment)
			attackLevel += plr.getSkillManager().getCurrentLevel(Skill.MAGIC) * 0.2;
		if (plr.getPrayerActive()[PrayerHandler.MYSTIC_WILL] || plr.getCurseActive()[CurseHandler.SAP_MAGE]) {
			attackLevel *= 1.05;
		} else if (plr.getPrayerActive()[PrayerHandler.MYSTIC_LORE]) {
			attackLevel *= 1.10;
		} else if (plr.getPrayerActive()[PrayerHandler.MYSTIC_MIGHT]) {
			attackLevel *= 1.15;
		} else if (plr.getCurseActive()[CurseHandler.LEECH_MAGIC]) {
			attackLevel *= 1.18;
		}
		/*
		 * Slay helm
		 */
		if(plr.getAdvancedSkills().getSlayer().getSlayerTask() != null && plr.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 15492) {
			if(plr.getCombatAttributes().getCurrentEnemy() != null && plr.getCombatAttributes().getCurrentEnemy().isNpc()) {
				NPC n = (NPC)plr.getCombatAttributes().getCurrentEnemy();
				if(n != null && n.getId() == plr.getAdvancedSkills().getSlayer().getSlayerTask().getNpcId())
					attackLevel *= 1.12;
			}
		}
		return (int) (attackLevel + (plr.getAttributes().getBonusManager().getAttackBonus()[3] * 2));
	}

	/**
	 * Calculates a player's magic defence level
	 * @param p			The player to calculate magic defence level for
	 * @return			The player's magic defence level
	 */
	public static int getMagicDefence(Player p) {
		int defenceLevel = p.getSkillManager().getCurrentLevel(Skill.DEFENCE)/2 + p.getSkillManager().getCurrentLevel(Skill.MAGIC)/2;
		if (p.getPrayerActive()[PrayerHandler.THICK_SKIN]) {
			defenceLevel += p.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.05;
		} else if (p.getPrayerActive()[PrayerHandler.ROCK_SKIN]) {
			defenceLevel += p.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.1;
		} else if (p.getPrayerActive()[PrayerHandler.STEEL_SKIN]) {
			defenceLevel += p.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.15;
		} else if (p.getPrayerActive()[PrayerHandler.CHIVALRY]) {
			defenceLevel += p.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.2;
		} else if (p.getPrayerActive()[PrayerHandler.PIETY]) {
			defenceLevel += p.getSkillManager().getMaxLevel(Skill.DEFENCE) * 0.25;
		} else if (p.getCurseActive()[CurseHandler.TURMOIL]) { // turmoil
			defenceLevel += p.getSkillManager().getMaxLevel(Skill.DEFENCE)
					* 0.20 + p.getCombatAttributes().getLeechedBonuses()[0];
		}
		return (int) (defenceLevel + p.getAttributes().getBonusManager().getDefenceBonus()[3] + (p.getAttributes().getBonusManager().getDefenceBonus()[3] / 3));
	}

	/**
	 * Calculates a player's magic max hit
	 * @param p			The player to calculate magic max hit for
	 * @return			The player's magic max hit damage
	 */
	public static int getMagicMaxhit(Player p) {
		double damage = 0;
		CombatSpell spell = p.getPlayerCombatAttributes().getMagic().getCurrentSpell();
		if(spell != null) {
			if(spell.damagingSpell())
				damage += spell.maximumStrength();
			else 
				return 1;
		}
		double damageMultiplier = 1.12;
		switch (p.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId()) {
		case 4675: 
		case 6914:
			damageMultiplier += .10;
			break;
		}
		damage *= damageMultiplier;
		/*
		 * Slay helm
		 */
		if(p.getAdvancedSkills().getSlayer().getSlayerTask() != null && p.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 15492) {
			if(p.getCombatAttributes().getCurrentEnemy() != null && p.getCombatAttributes().getCurrentEnemy().isNpc()) {
				NPC n = (NPC)p.getCombatAttributes().getCurrentEnemy();
				if(n != null && n.getId() == p.getAdvancedSkills().getSlayer().getSlayerTask().getNpcId())
					damage *= 1.12;
			}
		}
		return (int)damage;
	}
}
