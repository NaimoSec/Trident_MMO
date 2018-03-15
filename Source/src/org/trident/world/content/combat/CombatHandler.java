package org.trident.world.content.combat;

import java.util.Map.Entry;

import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.SkullTask;
import org.trident.model.Damage;
import org.trident.model.Flag;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Position;
import org.trident.model.Projectile;
import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.model.definitions.NPCDefinition;
import org.trident.model.movement.MovementQueue;
import org.trident.model.movement.PathFinder;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.clip.region.RegionClipping;
import org.trident.world.content.Following;
import org.trident.world.content.Locations;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.combat.combatdata.ranged.RangedData;
import org.trident.world.content.combat.combatdata.ranged.RangedData.RangedWeaponData;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.combat.weapons.WeaponHandler.ExperienceStyle;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.content.minigames.impl.Dueling;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCAggression;
import org.trident.world.entity.npc.NPCData;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.npc.custom.impl.NexMinion;
import org.trident.world.entity.player.Player;


/**
 * Handles combat, based on GameCharacter's (Entities)
 * Last updated: 2013, 10th July
 * This crappy system isn't advanced at all, but it does the job very well.
 * TODO: Make everything abstract and more advanced.
 * @author Gabbe
 * Credits to Lare for his Magic system from Asteria, was too lazy to make my own
 */

public class CombatHandler {

	/**
	 * The main method for the combat system. It is called by process.
	 * @param attacker	The Entity who's attacking the target.
	 * @param target	The target being attacked.
	 */
	public static void attack(GameCharacter attacker, GameCharacter target) {
		setAttack(attacker, target);
		if(!checkRequirements(attacker, target) || !checkSecondaryRequirements(attacker, target))
			return;
		attacker.getCombatAttributes().setAttackDelay(attacker.getAttackDelay());
		if(handleSpecialAttack(attacker, target))
			return;
		final Damage damage = DamageHandler.getDamage(attacker, target);
		boolean customNPC = attacker.isNpc() && target.isPlayer() && CustomNPC.isCustomNPC(((NPC)attacker).getId());
		if(damage == null && !customNPC)
			return;
		if(customNPC) {
			CustomNPC.handleAttack((Player)target, (NPC)attacker);
		} else {
			attacker.performAnimation(attacker.getAttackAnimation());
			DamageHandler.handleAttack(attacker, target, damage, attacker.getCombatAttributes().getAttackType(), false, false);
		}
	}

	/**
	 * Checks if the attacker has the requirements to attack the target.
	 * @param attacker	The GameCharacter whose trying to attack target
	 * @param target	The GameCharacter who's being attacked
	 * @return
	 */
	public static boolean checkRequirements(GameCharacter attacker, GameCharacter target) {
		if(attacker == null || target == null || attacker.getConstitution() <= 0 || target.getConstitution() <= 0) {
			try {
				if(attacker.isPlayer()) {
					if(attacker.getCombatAttributes().getCurrentEnemy() != null && attacker.getCombatAttributes().getCurrentEnemy() == target)
						attacker.getCombatAttributes().setCurrentEnemy(null);
				} else
					resetAttack(attacker);
				if(target.isPlayer()) {
					if(target.getCombatAttributes().getCurrentEnemy() != null && target.getCombatAttributes().getCurrentEnemy() == attacker)
						target.getCombatAttributes().setCurrentEnemy(null);
				} else
					resetAttack(target);
				if(attacker.getInteractingEntity() != null && attacker.getInteractingEntity() == target)
					attacker.setEntityInteraction(null);
				if(target.getInteractingEntity() != null && target.getInteractingEntity() == attacker)
					target.setEntityInteraction(null);
			} catch(Exception e) {}
			return false;
		} else if(attacker != null && target != null) {
			if(attacker == target)
				return false;
			//Double checking null..
			if(target.isPlayer()) {
				if(World.getPlayers().get(target.getIndex()) == null) {
					resetAttack(attacker);
					return false;
				}
			} else if(target.isNpc()) {
				if(World.getNpcs().get(target.getIndex()) == null) {
					resetAttack(attacker);
					return false;
				}
			}
		}
		Player plr = attacker.isPlayer() ? (Player) attacker : null;
		NPC n = target.isNpc() ? (NPC)target : null;
		if(attacker.getCombatAttributes().isStunned())
			return false;
		if(target.getCombatAttributes().getSpawnedFor() != null && target.getCombatAttributes().getSpawnedFor() != attacker)
			return false;
		if(attacker.getCombatAttributes().getSpawnedFor() != null && attacker.getCombatAttributes().getSpawnedFor() != target)
			return false;
		if(target.isPlayer()) {
			Player plr2 = (Player)target;
			if(plr2.getAttributes().clientIsLoading() || plr != null && plr.getAttributes().clientIsLoading()) {
				if(plr2 != null && plr != null)
					plr.getPacketSender().sendMessage("You cannot attack this player yet because they are loading the region.");
				resetAttack(attacker);
				return false;
			}
			if(plr != null && plr2 != null) {
				if(plr.getAttributes().getNewPlayerDelay() > 0 && plr.getRights().ordinal() == 0) {
					plr.getPacketSender().sendMessage("You must wait another "+plr.getAttributes().getNewPlayerDelay() / 60+" minutes before being able to attack other players.");
					resetAttack(attacker);
					return false;
				}
				if(plr.getCombatAttributes().getAttackType() == AttackType.MELEE && !plr.getPlayerCombatAttributes().isUsingSpecialAttack()) {
					if(plr.getLocation() == Location.DUEL_ARENA && Dueling.checkDuel(plr, 5)) {
						if(plr.getDueling().selectedDuelRules[Dueling.DuelRule.NO_MELEE.ordinal()]) {
							plr.getPacketSender().sendMessage("Melee-based attacks have been disabled in this duel.");
							CombatHandler.resetAttack(plr);
							return false;
						}
					}
				}
				if(plr2.getAttributes().getNewPlayerDelay() > 0 && plr2.getRights().ordinal() == 0) {
					plr.getPacketSender().sendMessage("You must wait another "+plr2.getAttributes().getNewPlayerDelay() / 60+" minutes before being able to attack this player.");
					resetAttack(attacker);
					return false;
				}
				boolean plrCanAttack = plr.getLocation() == Location.FREE_FOR_ALL_ARENA || plr.getLocation() == Location.WILDERNESS || (plr.getDueling().duelingStatus == 5 && plr.getLocation() == Location.DUEL_ARENA) || plr.getLocation() == Location.FIGHT_PITS || plr.getLocation() == Location.SOULWARS;
				boolean targetCanBeAttacked = plr2.getLocation() == Location.FREE_FOR_ALL_ARENA || plr2.getLocation() == Location.WILDERNESS || (plr2.getDueling().duelingStatus == 5 && plr2.getLocation() == Location.DUEL_ARENA) || plr2.getLocation() == Location.FIGHT_PITS || plr2.getLocation() == Location.SOULWARS;
				if(!plrCanAttack) {
					plr.getPacketSender().sendMessage("You are not in the Wilderness.");
					resetAttack(attacker);
					plr.getMovementQueue().stopMovement();
					return false;
				} else if(!targetCanBeAttacked) {
					plr.getPacketSender().sendMessage("Your target is not in the Wilderness.");
					plr.getMovementQueue().stopMovement();
					resetAttack(attacker);
					return false;
				}
				if(plr.getDueling().duelingStatus == 5) {
					if(plr2.getIndex() != plr.getDueling().duelingWith || plr.getIndex() != plr2.getDueling().duelingWith) {
						plr.getPacketSender().sendMessage("This is not your target!");
						resetAttack(attacker);
						return false;
					}
				}
				if(plr.getLocation() == Location.WILDERNESS) {
					int combatDif1 = getCombatDifference(plr.getSkillManager().getCombatLevel(), plr2.getSkillManager().getCombatLevel());
					if(combatDif1 > plr.getPlayerCombatAttributes().getWildernessLevel() || combatDif1 > plr2.getPlayerCombatAttributes().getWildernessLevel()) {
						plr.getPacketSender().sendMessage("Your combat level difference is too great to attack that player here.");
						resetAttack(attacker);
						return false;
					}
					if(!plr.getEquipment().properEquipmentForWilderness()) {
						plr.getPacketSender().sendMessage("You need to be wearing more than 3 tradeable items to attack this player.");
						resetAttack(attacker);
						return false;
					}
				} else if(plr.getLocation() == Location.SOULWARS && plr2.getLocation() == Location.SOULWARS) {
					if(SoulWars.blueTeam.contains(plr) && SoulWars.blueTeam.contains(plr2) || SoulWars.redTeam.contains(plr) && SoulWars.redTeam.contains(plr2)) {
						plr.getPacketSender().sendMessage("You cannot attack your team member!");
						resetAttack(attacker);
						return false;
					}
				}
			}
		}
		if(target.getConstitution() > 0 && attacker.getConstitution() > 0) {
			boolean inCombat = attacker.getCombatAttributes().getLastAttacker()!= null && attacker.getCombatAttributes().getLastAttacker().getConstitution() > 0 && System.currentTimeMillis() - attacker.getCombatAttributes().getLastDamageReceived() < 4500;
			if (inCombat && !Location.inMulti(attacker)) {
				if(attacker.getCombatAttributes().getLastAttacker() != null && attacker.getCombatAttributes().getLastAttacker() != target) {
					if(plr != null)
						plr.getPacketSender().sendMessage("You are already under attack.");
					resetAttack(attacker);
					return false;
				}
			}		
			inCombat = target.getCombatAttributes().getLastAttacker() != null && target.getCombatAttributes().getLastAttacker().getConstitution() > 0 && System.currentTimeMillis() - target.getCombatAttributes().getLastDamageReceived() < 4500;
			if (inCombat && !Location.inMulti(target)) {
				if(target.getCombatAttributes().getLastAttacker() != null && target.getCombatAttributes().getLastAttacker() != attacker) {
					if(plr != null)
						plr.getPacketSender().sendMessage("That target is already under attack.");
					resetAttack(attacker);
					return false;
				}
			}
		}
		if(n != null && plr != null) {
			if(plr.getLocation() == Location.SOULWARS && (n.getId() == SoulWars.RED_AVATAR || n.getId() == SoulWars.BLUE_AVATAR)) {
				if(n.getId() == SoulWars.RED_AVATAR) {
					if(SoulWars.redTeam.contains(plr)) {
						plr.getPacketSender().sendMessage("You can not attack your own Avatar!");
						resetAttack(attacker);
						return false;
					} else if(plr.getSkillManager().getCurrentLevel(Skill.SLAYER) < SoulWars.redSlayerLevel) {
						plr.getPacketSender().sendMessage("");
						plr.getPacketSender().sendMessage("You need a Slayer level of at least "+SoulWars.redSlayerLevel+" to attack the avatar.");
						plr.getPacketSender().sendMessage("You can reduce the requirement by using Soul Fragments on the obelisk.");
						resetAttack(attacker);
						return false;
					}
				} else if(n.getId() == SoulWars.BLUE_AVATAR) {
					if(SoulWars.blueTeam.contains(plr)) {
						plr.getPacketSender().sendMessage("You can not attack your own Avatar!");
						resetAttack(attacker);
						return false;
					} else if(plr.getSkillManager().getCurrentLevel(Skill.SLAYER) < SoulWars.blueSlayerLevel) {
						plr.getPacketSender().sendMessage("");
						plr.getPacketSender().sendMessage("You need a Slayer level of at least "+SoulWars.redSlayerLevel+" to attack the avatar.");
						plr.getPacketSender().sendMessage("You can reduce the requirement by using Soul Fragments on the obelisk.");
						resetAttack(attacker);
						return false;
					}
				}
				SoulWars.handleActivity(plr, 2, true, false);
			} else {
				NPCDefinition def = n.getDefinition();
				if(!CustomNPC.isCustomNPC(n.getId()) && def == null || !n.getAttributes().isAttackable()) {
					resetAttack(attacker);
					return false;
				}
				if (plr != null && def != null && def.getSlayerLevelRequirement() > plr.getSkillManager().getCurrentLevel(Skill.SLAYER)) {
					plr.getPacketSender().sendMessage("You need a Slayer level of at least "+ def.getSlayerLevelRequirement() +" to attack this NPC.");
					resetAttack(attacker);
					return false;
				}
				if(n.getId() == 6222 && plr.getCombatAttributes().getAttackType() == AttackType.MELEE && !plr.getEquipment().wearingHalberd()) {
					plr.getPacketSender().sendMessage("Kree'arra's skin is too strong for short-distance Melee attacks.");
					resetAttack(attacker);
					return false;
				}
				if(NexMinion.nexMinion(n.getId()) && !NexMinion.attackable(n.getId())) {
					plr.getPacketSender().sendMessage(""+NPCDefinition.forId(n.getId()).getName()+" is currently being protected by Nex.");
					resetAttack(attacker);
					return false;
				}
			}
		}
		if(plr != null && target.isPlayer()) {
			if(plr.getLocation() == Location.WILDERNESS && target.getLocation() == Location.WILDERNESS) {
				if(!plr.getPlayerCombatAttributes().isSkulled() && System.currentTimeMillis() - plr.getCombatAttributes().getLastDamageReceived() > 15000)
					skull(plr, true);
			}
		}
		return true;
	}

	/**
	 * Checks if attacker has the requirements to perform an attack on target
	 * @param attacker	The attacker to check requirements for.
	 * @param target	The target that attacker is going to attack.
	 * @return
	 */
	public static boolean checkSecondaryRequirements(GameCharacter attacker, GameCharacter target) {
		attacker.setPositionToFace(target.getPosition());
		if(!closeDistance(attacker, target)) {
			setAttack(attacker, target);
			if(attacker.isNpc() && (((NPC)attacker).getId() == 6247 || ((NPC)attacker).getId() == 13447) && !Locations.goodDistance(attacker.getPosition(), target.getPosition(), 2)) {
				if(target.isPlayer()) {
					Player newPlayerToAttack = Misc.getCloseRandomPlayer(((Player)target).getAttributes().getLocalPlayers());
					if(newPlayerToAttack != null)
						NPCAggression.processFor(newPlayerToAttack);
				}
			}
			return false;
		}
		if(attacker.getCombatAttributes().getAttackDelay() > 0) {
			setAttack(attacker, target);
			closeDistance(attacker, target);
			return false;
		}
		Player plr = null;
		if(attacker.isPlayer())
			plr = (Player) attacker;
		attacker.setEntityInteraction(attacker.getConstitution() <= 0 ? null : target);
		if(plr != null) {
			if(plr.getCombatAttributes().getAttackType() == AttackType.RANGED) {
				if(!RangedData.canExecuteAttack(plr, plr.getPlayerCombatAttributes().getRangedWeaponData())) {
					resetAttack(attacker);
					return false;
				}
			} else if(plr.getCombatAttributes().getAttackType() == AttackType.MAGIC && plr.getPlayerCombatAttributes().getMagic().getCurrentSpell() != null) {
				if(!plr.getPlayerCombatAttributes().getMagic().getCurrentSpell().prepareCast(plr, target, true)) {
					resetAttack(attacker);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if a special attack should be executed
	 * @param attacker
	 * @param target
	 * @return
	 */
	public static boolean handleSpecialAttack(GameCharacter attacker, GameCharacter target) {
		if(attacker.isPlayer()) {
			Player p = (Player)attacker;
			if(p.getPlayerCombatAttributes().isUsingSpecialAttack() && p.getPlayerCombatAttributes().getSpecialAttack() != null) {
				if(!attacker.getCombatAttributes().getAttackType().equals(AttackType.MAGIC) || attacker.getCombatAttributes().getAttackType().equals(AttackType.MAGIC) && attacker.isPlayer() && ((Player)attacker).getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 19780 && ((Player)attacker).getPlayerCombatAttributes().getMagic().getCurrentSpell() == null) {
					CombatHandler.setAttack(attacker, target);
					specialAttack((Player) attacker, target);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if a GameCharacter is already in combat.
	 * @param gc The GameCharacter to check if in combat 
	 * @return	If the gamecharacter is in combat
	 */
	public static boolean inCombat(GameCharacter gc) {
		return gc.getCombatAttributes().getCurrentEnemy() != null && gc.getCombatAttributes().isAttacking();
	}

	/**
	 * Resets the combat-related variables for a GameCharacter
	 * @param gc The GameCharacter to reset combat for.
	 */
	public static void resetAttack(GameCharacter gc) {
		gc.getCombatAttributes().setCurrentEnemy(null).setAttacking(false);
		gc.setEntityInteraction(null);
	}

	/**
	 * Sets the combat-related variables for a GameCharacter
	 * @param attacker The GameCharacter to set combat for.
	 * @param target	The Target to assign as enemy for the attacker
	 */
	public static void setAttack(GameCharacter attacker, GameCharacter target) {
		attacker.getCombatAttributes().setCurrentEnemy(target).setAttacking(true);
		attacker.setEntityInteraction(target);
	}

	/**
	 * Handles a player's wepaon's special attack
	 * @param player	The player who's the owner of the weapon
	 * @param target	The target who's going to get attacked by the special attack.
	 */
	public static void specialAttack(Player player, GameCharacter target) {
		if(player.getLocation() == Location.DUEL_ARENA && Dueling.checkDuel(player, 5)) {
			if(player.getDueling().selectedDuelRules[Dueling.DuelRule.NO_SPECIAL_ATTACKS.ordinal()]) {
				player.getPacketSender().sendMessage("Special attacks have been disabled in this duel.");
				return;
			}
		}
		SpecialAttack special = player.getPlayerCombatAttributes().getSpecialAttack();
		if (special != null) {
			special.execute(player, target);
			int specialAttackSound = SoundEffects.specialSounds(player.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId());
			if(specialAttackSound > 0) {
				SoundEffects.sendSoundEffect(player, specialAttackSound, 10, 1);
				if(target != null && target.isPlayer()) {
					SoundEffects.sendSoundEffect(((Player)target), specialAttackSound, 10, 1);
					SoundEffects.sendSoundEffect(player, SoundEffects.getPlayerBlockSounds((Player)target), 10, 2);
					SoundEffects.sendSoundEffect(((Player)target), SoundEffects.getPlayerBlockSounds((Player)target), 10, 2);
				}
			}
			WeaponHandler.update(player);
		}
	}

	/**
	 * Adds experience in a combat skill, how much depends on the amount of damage the player made.
	 * @param player	The player who dealt the damage.
	 * @param damage	The damage that the player dealt, and is going to be rewarded for.
	 */
	public static void addExperience(Player player, Damage damage, ExperienceStyle experience) {
		Skill[] skills = null;
		switch(experience) {
		case STRENGTH:
			skills = new Skill[] {Skill.STRENGTH};
			break;
		case ATTACK:
			skills = new Skill[] {Skill.ATTACK};
			break;
		case DEFENCE:
			skills = new Skill[] {Skill.DEFENCE};
			break;
		case MELEE_SHARED:
			skills = new Skill[] {Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE};
			break;
		case RANGED:
			skills = new Skill[] {Skill.RANGED};
			break;
		case RANGED_SHARED:
			skills = new Skill[] {Skill.RANGED, Skill.DEFENCE};
			break;
		case MAGIC:
			skills = new Skill[] {Skill.MAGIC};
			break;
		case MAGIC_SHARED:
			skills = new Skill[] {Skill.MAGIC, Skill.DEFENCE};
			break;
		}
		if(skills == null)
			return;
		boolean sharedXP = skills.length >= 2;
		int hit = damage.getHits()[0].getDamage();
		if (damage.getHits().length >= 2)
			hit += damage.getHits()[1].getDamage();
		if(sharedXP) {
			for (Skill skill : skills)
				player.getSkillManager().addExperience(skill, (int) (((hit * .60) * skill.getExperienceMultiplier()) / skills.length), false);
		} else 
			player.getSkillManager().addExperience(skills[0], (int) (((hit * .60) * skills[0].getExperienceMultiplier())), false);
		player.getSkillManager().addExperience(Skill.CONSTITUTION, (int) (hit * .45) * Skill.CONSTITUTION.getExperienceMultiplier(), false);
	}

	/**
	 * Checks if attacker has the required distance to perform an attack on target
	 * @param attacker	The GameCharacter to check distance for.
	 * @param target The GameCharacter who's going to receive an attack.
	 * @return	false or true, depending on if the GameCharacter attacker has the distance required.
	 */
	public static boolean closeDistance(GameCharacter attacker, GameCharacter target) {
		if(attacker == null || target == null || attacker.getConstitution() <= 0 || target.getConstitution() <= 0)
			return false;
		NPC n = null;
		if(attacker.isNpc()) {
			n = (NPC) attacker;
			if(target.isPlayer())
				if(NPCData.godwarsDungeonBoss(n.getId()) && !((Player)target).getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()) {
					resetAttack(attacker);
					return false;
				}
		} else if(target.isNpc()) {
			n = (NPC) target;
			/*if(n.walkingHome && !Locations.inPcGame(n)) {
				if(attacker.isPlayer())
					((Player)attacker).getPacketSender().sendMessage("This NPC is currently not attackable.");
				resetAttack(attacker);
				return false;
			}*/
			if(NPCData.godwarsDungeonBoss(n.getId()) && !((Player)attacker).getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom()) {
				((Player)attacker).getPacketSender().sendMessage("You cannot reach that.");
				resetAttack(attacker);
				return false;
			}
		}
		if(n != null && n.getLocation() != Location.FIGHT_CAVES && n.getLocation() != Location.RECIPE_FOR_DISASTER) { //Stops combat for npcs if too far away
			if(!Locations.goodDistance(n.getPosition().copy(), attacker.getIndex() != n.getIndex() ? attacker.getPosition().copy() : target.getPosition().copy(), NPCData.getMaximumFollow(n)) || !Locations.goodDistance(n.getDefaultPosition(), n.getPosition(), NPCData.getMaximumFollow(n) + n.getAttributes().getWalkingDistance())) {
				if(Locations.goodDistance(n.getPosition().copy(), attacker.getIndex() != n.getIndex() ? attacker.getPosition().copy() : target.getPosition().copy(), 1))
					return true;
				resetAttack(attacker);
				resetAttack(target);
				n.setEntityInteraction(null);
				n.getAttributes().setWalkingHome(true);
				MovementQueue.walkHome(n);
				if(attacker.isNpc())
					return false;
			}
		}
		int distanceReq = target.getSize();
		switch(attacker.getCombatAttributes().getAttackType()) {
		case MELEE:
			if(attacker.isPlayer()) {
				if(((Player)attacker).getEquipment().wearingHalberd())
					distanceReq += 2;
				if(distanceReq > 2)
					distanceReq = 2;
			} else {
				if(attacker.getSize() >= 3)
					distanceReq++;
			}
			break;
		case MAGIC:
			distanceReq = 5;
			break;
		case RANGED:
			if(attacker.isPlayer() && ((Player)attacker).getPlayerCombatAttributes().getRangedWeaponData() != null)
				distanceReq = ((Player)attacker).getPlayerCombatAttributes().getRangedWeaponData().getType().getDistanceRequired();
			else
				distanceReq = 4;
			break;
		}
		if(attacker.isNpc()) {
			int distance = CustomNPC.getDistance((NPC)attacker);
			if(distanceReq < distance)
				distanceReq = distance;
		}
		boolean sameSpot = attacker.getPosition().equals(target.getPosition()) && !attacker.getMovementQueue().isMoving() && !target.getMovementQueue().isMoving();
		boolean goodDistance = !sameSpot && Locations.goodDistance(attacker.getPosition().getX(), attacker.getPosition().getY(), target.getPosition().getX(), target.getPosition().getY(), distanceReq);
		boolean projectilePathBlocked = false;
		if(attacker.isPlayer() && (attacker.getCombatAttributes().getAttackType() == AttackType.RANGED || attacker.getCombatAttributes().getAttackType() == AttackType.MAGIC) || attacker.isNpc() && attacker.getCombatAttributes().getAttackType() == AttackType.MELEE) {
			if(!RegionClipping.canProjectileAttack(attacker, target))
				projectilePathBlocked = true;
		}
		attacker.setEntityInteraction(attacker.getConstitution() <= 0 ? null : target);
		if(!projectilePathBlocked && goodDistance) {
			if(attacker.getCombatAttributes().getAttackType() == AttackType.MELEE && RegionClipping.isInDiagonalBlock(target, attacker)) {
				PathFinder.findPath(attacker, target.getPosition().getX() - 1, target.getPosition().getY(), true, 1, 1);
				CombatHandler.setAttack(attacker, target);
				return false;
			} else
				attacker.getMovementQueue().stopMovement();
			return true;
		} else if(projectilePathBlocked || !goodDistance) {
			Following.followGameCharacter(attacker, target);
			return false;
		}
		return true;
	}


	/**
	 * Handles a target's auto retaliation against the attacker.
	 * @param attacker	The gamecharacter who's attacked the gamecharacter target
	 * @param target	The gamecharacter who's attacked by the gamecharacter attacker
	 */
	public static void handleAutoRetaliate(GameCharacter attacker, GameCharacter target) {
		if(target.isNpc() && (target.getLocation() == null || target.getLocation() != Location.PEST_CONTROL_GAME) && ((NPC)target).getId() != SoulWars.BARRICADE_NPC) {
			if(!((NPC)target).getAttributes().isWalkingHome() && (target.getCombatAttributes().getCurrentEnemy() == null || System.currentTimeMillis() - target.getCombatAttributes().getLastDamageReceived() > 4000))
				setAttack(target, attacker);
		} else if(target.isPlayer() && target.getCombatAttributes().isAutoRetaliation() && !target.getMovementQueue().isMoving()) {
			if(target.getLocation() == null || target.getLocation() != null && !Location.inMulti(target))
				setAttack(target, attacker);
			else {
				setProperAttackType((Player)target);
				if(target.getCombatAttributes().getCurrentEnemy() != null) {
					if(!target.getCombatAttributes().isAttacking() && target.getCombatAttributes().getCurrentEnemy() != attacker) {
						setAttack(target, target.getCombatAttributes().getLastAttacker());
						return;
					}
				} else if(target.getCombatAttributes().getCurrentEnemy() == null || target.getCombatAttributes().getCurrentEnemy() != null && target.getCombatAttributes().getLastAttacker().getConstitution() <= 0)
					setAttack(target, attacker);
			}
		}
	}

	/**
	 * Gets the killer of a character (the one who dealt the most dmg)
	 * @param charKilled	The character which was killed
	 * @return				The player who has killed the character
	 */
	public static Player getKiller(GameCharacter charKilled) {
		if(charKilled == null)
			return null;
		Player killer = null;
		int damage = 0;
		for (Entry<Player, Hit> entry : charKilled.getCombatAttributes().getHitMap().entrySet()) {
			if (entry == null)
				continue;
			long timeElapsed = entry.getValue().getStopwatch().elapsed();
			if (timeElapsed > 60000)
				continue;
			if(entry.getValue().getDamage() > damage) {
				damage = entry.getValue().getDamage();
				killer = entry.getKey();
			}
		}
		return killer;
	}

	/**
	 * Calculates the lowest and highest level of a target an enemy can attack
	 * @param player The player to check who whom can attack
	 * @param up if you want to calculate up or down
	 * @return level difference
	 */
	public static int getLevelDifference(Player player, boolean up) {
		int difference = 0;
		if (up)
			difference = (int)player.getSkillManager().getCombatLevel() + player.getPlayerCombatAttributes().getWildernessLevel(); 
		else 
			difference =(int)player.getSkillManager().getCombatLevel() - player.getPlayerCombatAttributes().getWildernessLevel();
		return difference < 3 ? 3 : difference > 138 && up ? 138 : difference;
	}

	/**
	 * Calculates combat difference
	 * @param combat1	The difference 1 to calculate
	 * @param combat2	The difference 2 to calculate
	 * @return
	 */
	public static int getCombatDifference(int combat1, int combat2) {
		if(combat1 > combat2) {
			return (combat1 - combat2);
		}
		if(combat2 > combat1) {
			return (combat2 - combat1);
		}	
		return 0;
	}

	/**
	 * Fires a projectile
	 * @param attacker
	 * @param target
	 * @param gfx
	 */
	public static void fireProjectile(Player attacker, GameCharacter target, Graphic gfx, boolean magic) {
		attacker.getPacketSender().sendGlobalProjectile(new Projectile(new Position(attacker.getPosition().getX(), attacker.getPosition().getY(), 43), new Position(target.getPosition().getX(), target.getPosition().getY(), 31), gfx, 0, 50, 50), target);
	}

	public static void skull(Player p, boolean skull) {
		if(skull) {
			if(!p.getPlayerCombatAttributes().isSkulled()) {
				p.getPlayerCombatAttributes().setSkulled(true);
				p.getUpdateFlag().flag(Flag.APPEARANCE);
				TaskManager.submit(new SkullTask(p));
				p.getPacketSender().sendMessage("@red@Warning! You have been skulled! You will keep less items on death!");

			}
		} else {
			if(p.getPlayerCombatAttributes().isSkulled()) {
				p.getPlayerCombatAttributes().setSkulled(false);
				p.getUpdateFlag().flag(Flag.APPEARANCE);
				p.getPacketSender().sendMessage("@red@You are no longer skulled.");
			}
		}
	}

	/**
	 * Sets the correct attack type for a player
	 * @param p
	 */
	public static void setProperAttackType(Player p) {
		AttackType attackType = AttackType.MELEE;
		RangedWeaponData data = RangedData.RangedWeaponData.getData(p);
		if(data != null) {
			p.getPlayerCombatAttributes().setRangedWeaponData(data);
			attackType = AttackType.RANGED;
		} else
			p.getPlayerCombatAttributes().setRangedWeaponData(null);
		//boolean armadylStaff = p.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 21010;
		if(p.getPlayerCombatAttributes().getMagic().getAutocastSpell() != null) {
			attackType = AttackType.MAGIC;
			/*if(armadylStaff)
				p.spellId = MagicData.ARMADYL_STORM_SPELL_ID;*/
		} else {
			p.getPlayerCombatAttributes().getMagic().setCurrentSpell(null);
		}
		p.getCombatAttributes().setAttackType(attackType);
	}
}
