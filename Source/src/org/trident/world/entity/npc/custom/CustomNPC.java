package org.trident.world.entity.npc.custom;

import java.util.HashMap;
import java.util.Map;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Position;
import org.trident.model.Projectile;
import org.trident.model.container.impl.Equipment;
import org.trident.util.Misc;
import org.trident.world.content.Locations;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatExtras;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.impl.Agrith_Na_Na;
import org.trident.world.entity.npc.custom.impl.Ahrim_The_Blighted;
import org.trident.world.entity.npc.custom.impl.Archer;
import org.trident.world.entity.npc.custom.impl.AviansieRaceNpc;
import org.trident.world.entity.npc.custom.impl.Balfrug_Kreeyath;
import org.trident.world.entity.npc.custom.impl.ChaosElemental;
import org.trident.world.entity.npc.custom.impl.Commander_Zilyana;
import org.trident.world.entity.npc.custom.impl.CorporealBeast;
import org.trident.world.entity.npc.custom.impl.Culinaromancer;
import org.trident.world.entity.npc.custom.impl.Dessourt;
import org.trident.world.entity.npc.custom.impl.Dharok_The_Wretched;
import org.trident.world.entity.npc.custom.impl.Dragon;
import org.trident.world.entity.npc.custom.impl.General_Graardor;
import org.trident.world.entity.npc.custom.impl.Growler;
import org.trident.world.entity.npc.custom.impl.Guthan_The_Infested;
import org.trident.world.entity.npc.custom.impl.Karamel;
import org.trident.world.entity.npc.custom.impl.Karil_The_Tainted;
import org.trident.world.entity.npc.custom.impl.Ket_Zek;
import org.trident.world.entity.npc.custom.impl.KingBlackDragon;
import org.trident.world.entity.npc.custom.impl.Kree_Arra;
import org.trident.world.entity.npc.custom.impl.Kril_Tsutsaroth;
import org.trident.world.entity.npc.custom.impl.Mage;
import org.trident.world.entity.npc.custom.impl.Nex;
import org.trident.world.entity.npc.custom.impl.NexMinion;
import org.trident.world.entity.npc.custom.impl.Nomad;
import org.trident.world.entity.npc.custom.impl.Plane_freezer_Lakrahnaz;
import org.trident.world.entity.npc.custom.impl.RevenantMob;
import org.trident.world.entity.npc.custom.impl.Sergeant_Grimspike;
import org.trident.world.entity.npc.custom.impl.Sergeant_Steelwill;
import org.trident.world.entity.npc.custom.impl.Strykewyrm;
import org.trident.world.entity.npc.custom.impl.Tok_Zil;
import org.trident.world.entity.npc.custom.impl.Torag_The_Corrupted;
import org.trident.world.entity.npc.custom.impl.TormentedDemon;
import org.trident.world.entity.npc.custom.impl.TzTok_Jad;
import org.trident.world.entity.npc.custom.impl.Tzhaar_Kih;
import org.trident.world.entity.npc.custom.impl.Wingman_Skree;
import org.trident.world.entity.npc.custom.impl.Zakln_Gritch;
import org.trident.world.entity.player.Player;

public abstract class CustomNPC {

	public abstract void executeAttack(NPC attacker, Player target);

	private static Map<Integer, CustomNPC> customNpcs = new HashMap<Integer, CustomNPC>();

	public static void init() {
		customNpcs.put(50, new KingBlackDragon());
		customNpcs.put(941, new Dragon());
		customNpcs.put(55, new Dragon());
		customNpcs.put(53, new Dragon());
		customNpcs.put(5363, new Dragon());
		customNpcs.put(5362, new Dragon());
		customNpcs.put(51, new Dragon());
		customNpcs.put(8133, new CorporealBeast());
		customNpcs.put(3200, new ChaosElemental());
		customNpcs.put(8349, new TormentedDemon());
		customNpcs.put(2627, new Tzhaar_Kih());
		customNpcs.put(2631, new Tok_Zil());
		customNpcs.put(2743, new Ket_Zek());
		customNpcs.put(2745, new TzTok_Jad());
		customNpcs.put(2029, new Torag_The_Corrupted());
		customNpcs.put(2028, new Karil_The_Tainted());
		customNpcs.put(2025, new Ahrim_The_Blighted());
		customNpcs.put(2027, new Guthan_The_Infested());
		customNpcs.put(2026, new Dharok_The_Wretched());
		customNpcs.put(3493, new Agrith_Na_Na());
		customNpcs.put(3495, new Karamel());
		customNpcs.put(3496, new Dessourt());
		customNpcs.put(3491, new Culinaromancer());
		customNpcs.put(8528, new Nomad());
		customNpcs.put(27, new Archer());
		customNpcs.put(6365, new Archer());
		customNpcs.put(912, new Mage());
		customNpcs.put(13459, new Mage());
		customNpcs.put(13457, new Archer());
		customNpcs.put(6220, new Archer());
		customNpcs.put(6256, new Archer());
		customNpcs.put(6276, new Archer());
		customNpcs.put(6221, new Mage());
		customNpcs.put(6257, new Mage());
		customNpcs.put(6278, new Mage());
		customNpcs.put(6254, new Mage());
		customNpcs.put(6260, new General_Graardor());
		customNpcs.put(6263, new Sergeant_Steelwill());
		customNpcs.put(6265, new Sergeant_Grimspike());
		customNpcs.put(6222, new Kree_Arra());
		customNpcs.put(6223, new Wingman_Skree());
		customNpcs.put(6225, new Archer());
		customNpcs.put(6203, new Kril_Tsutsaroth());
		customNpcs.put(6208, new Balfrug_Kreeyath());
		customNpcs.put(6206, new Zakln_Gritch());
		customNpcs.put(6247, new Commander_Zilyana());
		customNpcs.put(6250, new Growler());
		customNpcs.put(6252, new Archer());
		customNpcs.put(6246, new AviansieRaceNpc());
		customNpcs.put(6230, new AviansieRaceNpc());
		customNpcs.put(6231, new AviansieRaceNpc());
		customNpcs.put(6715, new RevenantMob());
		customNpcs.put(6716, new RevenantMob());
		customNpcs.put(6727, new RevenantMob());
		customNpcs.put(6689, new RevenantMob());
		customNpcs.put(9939, new Plane_freezer_Lakrahnaz());
		getNex();
		customNpcs.put(9467, new Strykewyrm());
		customNpcs.put(9465, new Strykewyrm());
		customNpcs.put(9463, new Strykewyrm());
	}

	private static Nex nex;

	public static Nex getNex() {
		if(nex == null)
			nex = new Nex();
		return nex;
	}
	
	public static void setNex(Nex nex) {
		CustomNPC.nex = nex;
	}

	public static boolean isCustomNPC(int npcId) {
		return npcId == nex.getId() || NexMinion.nexMinion(npcId) || customNpcs.containsKey(npcId);
	}

	public static void handleAttack(Player p, NPC n) {
		if(n.getId() == nex.getId())
			nex.attack(n, p);
		else if(NexMinion.nexMinion(n.getId())) 
			NexMinion.attack(n, p);
		else
			customNpcs.get(n.getId()).executeAttack(n, p);
	}

	public static int getDistance(NPC n) {
		switch(n.getId()) {
		case 2060:
			return 4;
		case 13451:
		case 13452:
		case 13453:
		case 13454:
			return 40;
		case 9939:
			return 7;
		case 8528:
		case 6248:
		case 6229:
			return 2;
		case 6246:
		case 6230:
		case 6276:
		case 6278:
		case 6254:
		case 6220:
		case 6221:
			return 4;
		case 50:
		case 8349:
			return 9;
		case 51:
		case 941:
		case 1591:
		case 1592:
		case 54:
		case 5363:
			return 7;
		case 2028:
		case 3491:
		case 6263:
		case 6265:
		case 6222:
		case 6225:
		case 6223:
		case 6206:
		case 6250:
		case 6715:
		case 6716:
		case 6727:
		case 6689:
			return 10;
		case 27:
			return 7;
		case 6203:
			return 2;
		case 6365:
		case 6231:
		case 6256:
			return 5;
		case 2631:
		case 2025:
		case 912:
		case 6208:
		case 6252:
			return 7;
		case 8133:
		case 2743:
			return 12;
		case 3200:
			return 13;
		case 2745:
			return 14;
		case 3493:
			return 4;
		case 13447:
			return 4;
		default:
			return 1;
		}
	}

	/*
	 * Custom NPC methods
	 */

	/**
	 * Sends fire onto the player
	 */
	public static void handleDragonFireBreath(final NPC attacker, final Player target) {
		attacker.getCombatAttributes().setAttackType(AttackType.MAGIC);
		final boolean kbd = attacker.getId() == 50;
		boolean frost = attacker.getId() == 51;
		final boolean highLvlDrag = attacker.getId() == 5363 || attacker.getId() == 5362;
		int anim = kbd ? 81 : 12259;
		if(highLvlDrag)
			anim = 14246;
		else if(frost)
			anim = 13152;
		attacker.performAnimation(new Animation(anim));
		if(kbd)
			TaskManager.submit(new Task(2, target, false) {
				@Override
				public void execute() {
					target.getPacketSender().sendGlobalProjectile(new Projectile(new Position(attacker.getPosition().getX(), attacker.getPosition().getY(), 43), new Position(target.getPosition().getX(), target.getPosition().getY(), 31), new Graphic(393 + Misc.getRandom(3)), 0, 50, 78), target);
					stop();
				}
			});
		TaskManager.submit(new Task(4, target, false) {
			@Override
			public void execute() {
				if(!kbd)
					target.performGraphic(new Graphic(5));
				boolean protectFromFire = target.getAttributes().getFireImmunity() > 0 || target.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 1540 || target.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 11283 || target.getPrayerActive()[PrayerHandler.PROTECT_FROM_MELEE] || target.getCurseActive()[CurseHandler.DEFLECT_MAGIC];
				int magicDamage = protectFromFire ? Misc.getRandom(kbd ? 240 : 100) : 300 + Misc.getRandom(150); magicDamage -= Misc.getRandom(DamageHandler.getMagicDefence(target)); 	if(magicDamage < 0)	magicDamage = 0;
				if(target.getAttributes().getFireDamagemodifier() == 50)
					magicDamage = magicDamage/2;  
				else if(target.getAttributes().getFireDamagemodifier() == 100)
					magicDamage = 0;
				Damage damage = new Damage(new Hit(magicDamage, CombatIcon.MAGIC, Hitmask.RED));
				if(!protectFromFire)
					target.getPacketSender().sendMessage("You are badly burnt by the dragon's fire!");
				if(Locations.goodDistance(attacker.getPosition().copy(), target.getPosition().copy(), 20))
					DamageHandler.handleAttack(attacker, target, damage, AttackType.MAGIC, false, false);
				attacker.getCombatAttributes().setAttackDelay(3);
				if(Misc.getRandom(20) <= 15 && target.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 11283) {
					target.setPositionToFace(attacker.getPosition().copy());
					CombatExtras.chargeDragonFireShield(target);
				}
				stop();
			}
		});
	}

	/**
	 * Calculates an NPC's normal damage
	 */
	public static Damage getBaseDamage(NPC attacker, Player target) {
		if(attacker.getId() == 3496) //Double damage
			return new Damage(DamageHandler.getDamage(attacker, target).getHits()[0], DamageHandler.getDamage(attacker, target).getHits()[0]);
		return DamageHandler.getDamage(attacker, target);
	}

	/**
	 * Fires a global projectile
	 * @param target
	 * @param n
	 */
	public static void fireGlobalProjectile(Player target, GameCharacter n, Graphic gfx) {
		target.getPacketSender().sendGlobalProjectile(new Projectile(new Position(n.getPosition().getX(), n.getPosition().getY(), 43), new Position(target.getPosition().getX(), target.getPosition().getY(), 31), gfx, 0, 50, 50), target);
	}

	/**
	 * Fires a projectile
	 * @param target
	 * @param n
	 */
	public static void fireProjectile(Player target, GameCharacter n, Graphic gfx) {
		target.getPacketSender().sendProjectile(new Projectile(new Position(n.getPosition().getX(), n.getPosition().getY(), 43), new Position(target.getPosition().getX(), target.getPosition().getY(), 31), gfx, 0, 50, 50), target);
	}
}