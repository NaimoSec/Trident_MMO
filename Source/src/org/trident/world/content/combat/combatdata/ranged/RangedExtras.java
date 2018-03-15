package org.trident.world.content.combat.combatdata.ranged;

import java.util.ArrayList;

import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Flag;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.GroundItem;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.util.Misc;
import org.trident.world.content.Locations;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatExtras;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.combat.combatdata.ranged.RangedData.RangedWeaponData;
import org.trident.world.content.combat.combatdata.ranged.RangedData.Type;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

public class RangedExtras {

	public static void dropArrows(Player player, Position pos) {
		RangedWeaponData data = player.getPlayerCombatAttributes().getRangedWeaponData();
		if(data != null) {
			int arrowId = data.getType() == Type.THROW ? player.getEquipment().getItems()[3].getId() : player.getEquipment().getItems()[13].getId();
			int arrowAmount = data.getType() == Type.THROW ?  player.getEquipment().getItems()[3].getAmount() : player.getEquipment().getItems()[13].getAmount();
			boolean chinchompa = arrowId == 10033 || arrowId == 10034;
			boolean dropItem = (player.getEquipment().getItems()[1].getId() != 10499 || Misc.getRandom(20) == 1) || arrowId == 15243 || chinchompa;
			if(dropItem) {
				player.getEquipment().delete(new Item(arrowId));
				player.getEquipment().refreshItems();
				if(Misc.getRandom(20) <= 15 && arrowId != 15243 && !chinchompa && arrowId != 13879 && arrowId != 13883) {
					if(!Dungeoneering.doingDungeoneering(player))
						GroundItemManager.spawnGroundItem(player, new GroundItem(new Item(arrowId), pos, player.getUsername(), player.getHostAdress(), false, 80, player.getPosition().getZ() >= 0 && player.getPosition().getZ() < 4 ? true : false, 80));
					else
						player.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().createGroundItem(new Item(arrowId), pos);
				}
				WeaponHandler.update(player);
				if(arrowAmount <= 1) {
					player.getPacketSender().sendMessage("You have run out of ammunition.");
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					CombatHandler.resetAttack(player);
				}
			}
		}
	}

	private static void createCombatGFX(GameCharacter target, int gfx, GraphicHeight height) {
		target.performGraphic(new Graphic(gfx, height));
	}

	public static int getBoltSpecialDamage(Player plr, GameCharacter target, int arrow, int dmg) {
		if(plr == null || target == null || target.getConstitution() <= 0 || plr.getConstitution() <= 0 || dmg <= 0)
			return 0;
		double multiplier = 1;
		Player targ = null;
		if(target instanceof Player)
			targ = (Player) target;
		switch (arrow) {
		case 9236: // Lucky Lightning
			createCombatGFX(target, 749, GraphicHeight.LOW);
			multiplier = 1.3;
			break;
		case 9237: // Earth's Fury
			createCombatGFX(target, 755, GraphicHeight.LOW);
			break;
		case 9238: // Sea Curse
			createCombatGFX(target, 750, GraphicHeight.LOW);
			multiplier = 1.1;
			break;
		case 9239: // Down to Earth
			createCombatGFX(target, 757, GraphicHeight.LOW);
			if(targ != null && CombatHandler.inCombat(plr) && plr.getCombatAttributes().getCurrentEnemy().getIndex() == targ.getIndex()) {
				targ.getSkillManager().setCurrentLevel(Skill.MAGIC, targ.getSkillManager().getCurrentLevel(Skill.MAGIC) - 3);
				targ.getPacketSender().sendMessage("Your Magic level has been reduced.");
			}
			break;
		case 9240: // Clear Mind
			createCombatGFX(target, 751, GraphicHeight.LOW);
			if(targ != null && CombatHandler.inCombat(plr) && plr.getCombatAttributes().getCurrentEnemy().getIndex() == targ.getIndex()) {
				targ.getSkillManager().setCurrentLevel(Skill.PRAYER, targ.getSkillManager().getCurrentLevel(Skill.PRAYER) - 40);
				if(targ.getSkillManager().getCurrentLevel(Skill.PRAYER) < 0)
					targ.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
				targ.getPacketSender().sendMessage("Your Prayer level has been leeched.");
				plr.getSkillManager().setCurrentLevel(Skill.PRAYER, targ.getSkillManager().getCurrentLevel(Skill.PRAYER) + 40);
				if(plr.getSkillManager().getCurrentLevel(Skill.PRAYER) > plr.getSkillManager().getMaxLevel(Skill.PRAYER))
					plr.getSkillManager().setCurrentLevel(Skill.PRAYER, plr.getSkillManager().getMaxLevel(Skill.PRAYER));
				else
					plr.getPacketSender().sendMessage("You manage to leech some Prayer points from your opponent.");
			}
			break;
		case 9241: // Magical Posion
			createCombatGFX(target, 752, GraphicHeight.LOW);
			CombatExtras.poison(target, 7, false);
			break;
		case 9242: // Blood Forfiet
			createCombatGFX(target, 754, GraphicHeight.LOW);
			if(plr.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) - plr.getSkillManager().getCurrentLevel(Skill.CONSTITUTION)/200 < 10)
				break;
			int priceDamage = (int) (plr.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) * 0.09);
			if(priceDamage < 0)
				break;
			Damage damage = new Damage(new Hit(priceDamage, CombatIcon.NONE, Hitmask.RED));
			if (damage.getHits()[0].getDamage() > plr.getConstitution())
				damage.getHits()[0].setDamage(plr.getConstitution() - 10);
			int dmg2 = (int) ((int) target.getConstitution() * 0.15);
			if(dmg2 > 1000)
				dmg2 = 990 + Misc.getRandom(50);
			if(dmg2 <= 0)
				return dmg;
			plr.setDamage(damage);
			Damage damage2 = new Damage(new Hit(dmg2, CombatIcon.RANGED, Hitmask.CRITICAL));
			if (damage.getHits()[0].getDamage() > target.getConstitution())
				damage.getHits()[0].setDamage(target.getConstitution());
			target.setDamage(damage2);
			multiplier = 0;
			break;
		case 9243: // Armour Piercing
			createCombatGFX(target, 758, GraphicHeight.MIDDLE);
			multiplier = 1.15;
			target.setDamage(new Damage(new Hit((int) (dmg*multiplier), CombatIcon.RANGED, Hitmask.RED)));
			multiplier = 0;
			break;
		case 9244: // Dragon's Breath
			createCombatGFX(target, 756, GraphicHeight.LOW);
			if(targ != null && targ.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() != 1540 && 
					targ.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() != 11283 && targ.getAttributes().getFireImmunity() <= 0)
			break;
		case 9245: // Life Leech
			createCombatGFX(target, 753, GraphicHeight.LOW);
			multiplier = 1.17;
			int heal = (int) (dmg * 0.25) + 10;
			plr.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, plr.getSkillManager().getCurrentLevel(Skill.CONSTITUTION)+heal);
			if(plr.getSkillManager().getCurrentLevel(Skill.CONSTITUTION) >= 1120)
				plr.getSkillManager().setCurrentLevel(Skill.CONSTITUTION, 1120);
			plr.getSkillManager().updateSkill(Skill.CONSTITUTION);
			break;
		}
		dmg *= multiplier;
		return dmg;
	}

	public static void handleChinchompaExplosion(Player player, GameCharacter mainTarget) {
		ArrayList<GameCharacter> targets = new ArrayList<GameCharacter>();
		targets.add(mainTarget);
		for(Player players : player.getAttributes().getLocalPlayers()) {
			if(players == null || targets.contains(players))
				continue;
			if(!Locations.goodDistance(mainTarget.getPosition(), players.getPosition(), 2))
				continue;
			if(CombatHandler.checkRequirements(player, players) && CombatHandler.checkSecondaryRequirements(player, players))
				targets.add(players);
		}
		for(NPC npcs : player.getAttributes().getLocalNpcs()) {
			if(npcs == null)
				continue;
			if(!Locations.goodDistance(npcs.getPosition().copy(), mainTarget.getPosition().copy(), 2))
				continue;
			if(npcs.getAttributes().isAttackable() && CombatHandler.checkRequirements(player, npcs))
				targets.add(npcs);
		}
		TeleportHandler.cancelCurrentActions(player);
		for(GameCharacter targ : targets) {
			if(targ != null && targ.getConstitution() > 0) {
				Damage dmgToSet = DamageHandler.getDamage(player, targ);
				if(!targ.isTeleporting()) {
					CombatHandler.addExperience(player, dmgToSet, player.getPlayerCombatAttributes().getAttackStyle().getExperienceReward());
					player.getPlayerCombatAttributes().setThrewChinchompa(true);
					DamageHandler.handleAttack(player, targ, dmgToSet, AttackType.RANGED, false, false);
					player.getPlayerCombatAttributes().setThrewChinchompa(false);
					targ.performGraphic(new Graphic(346, GraphicHeight.MIDDLE));
				}
			}
		}
	}
}
