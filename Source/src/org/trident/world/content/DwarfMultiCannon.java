package org.trident.world.content;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.DwarfCannon;
import org.trident.model.GameObject;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Position;
import org.trident.model.Projectile;
import org.trident.model.movement.MovementStatus;
import org.trident.util.Misc;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.audio.SoundEffects;
import org.trident.world.content.audio.SoundEffects.SoundData;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

/**
 * Handles the Dwarf multi Cannon
 * @author Gabbe
 *
 */
public class DwarfMultiCannon {

	private static final int 
	/**Start of cannon object ids**/
	CANNON_BASE = 7, CANNON_STAND = 8,
	CANNON_BARRELS = 9, CANNON = 6,
	/**Start of cannon item ids**/ 
	CANNONBALL = 2, CANNON_BASE_ID = 6,
	CANNON_STAND_ID = 8, CANNON_BARRELS_ID = 10,
	CANNON_FURNACE_ID = 12;

	public static void setupCannon(final Player c) {
		if(!canSetupCannon(c))
			return;
		c.getMovementQueue().stopMovement();
		c.getAttributes().setSettingUpCannon(true);
		c.getSkillManager().stopSkilling();
		c.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
		final GameObject object = new GameObject(CANNON_BASE, c.getPosition().copy());
		final GameObject object2 = new GameObject(CANNON_STAND, c.getPosition().copy());
		final GameObject object3 = new GameObject(CANNON_BARRELS, c.getPosition().copy());
		final GameObject object4 = new GameObject(CANNON, c.getPosition().copy());
		c.getInventory().delete(CANNON_FURNACE_ID, 1);
		c.getInventory().delete(CANNON_BARRELS_ID, 1);
		c.getInventory().delete(CANNON_STAND_ID, 1);
		c.getInventory().delete(CANNON_BASE_ID, 1);
		TaskManager.submit(new Task(2, c, true) {
			int setupTicks = 5;
			@Override
			public void execute() {
				switch(setupTicks) {

				case 5:
					c.performAnimation(new Animation(827));
					CustomObjects.spawnGlobalObject(object);
					break;

				case 4:
					c.performAnimation(new Animation(827));
					CustomObjects.customObjects.remove(object);
					CustomObjects.spawnGlobalObject(object2);
					break;

				case 3:
					c.performAnimation(new Animation(827));
					CustomObjects.customObjects.remove(object2);
					CustomObjects.spawnGlobalObject(object3);
					break;

				case 2:
					c.performAnimation(new Animation(827));
					CustomObjects.customObjects.remove(object3);
					DwarfCannon cannon = new DwarfCannon(c.getIndex(), c.getPosition().copy());
					c.getAttributes().setCannon(cannon).setCannonObject(object4);
					CustomObjects.spawnGlobalObject(c.getAttributes().getCannonObject());
					break;

				case 1:
					setupTicks = 5;
					c.getAttributes().setSettingUpCannon(false);
					stop();
					break;
				}
				setupTicks--;
			}

			@Override
			public void stop() {
				c.getMovementQueue().setMovementStatus(MovementStatus.NONE);
				setEventRunning(false);
			}
		});
	}

	public static boolean canSetupCannon(Player c) {
		if (!c.getInventory().contains(CANNON_BASE_ID)
				|| !c.getInventory().contains(CANNON_STAND_ID)
				|| !c.getInventory().contains(CANNON_BARRELS_ID)
				|| !c.getInventory().contains(CANNON_FURNACE_ID)) {
			c.getPacketSender().sendMessage("You don't have the required items to setup the dwarf-cannon.");
			return false;
		}
		if(c.getAttributes().getCannon() != null) {
			c.getPacketSender().sendMessage("You can only have one cannon setup at once.");
			return false;
		}
		if(!c.getMovementQueue().canWalk(3, 3) || CustomObjects.objectExists(c.getPosition().copy()) || !c.getLocation().isCannonAllowed()) {
			c.getPacketSender().sendMessage("The cannon cannot be setup here.");
			return false;
		}
		if(c.getAttributes().isSettingUpCannon() || c.getConstitution() <= 0 || c.getConstitution() <= 0)
			return false;
		return true;
	}

	public static void pickupCannon(Player c, DwarfCannon cannon, boolean force) {
		if(c.getAttributes().isSettingUpCannon())
			return;
		boolean deleted = false;
		boolean addCannonballs = cannon.getCannonballs() > 0;
		if(force) { //Logout
			if(c.getInventory().getFreeSlots() >= 5) {
				c.getInventory().add(CANNON_BASE_ID, 1);
				c.getInventory().add(CANNON_STAND_ID, 1);
				c.getInventory().add(CANNON_BARRELS_ID, 1);
				c.getInventory().add(CANNON_FURNACE_ID, 1);
				if(addCannonballs)
					c.getInventory().add(CANNONBALL, cannon.getCannonballs());
				deleted = true;
			} else {
				c.getBank(c.getAttributes().getCurrentBankTab()).add(CANNON_BASE_ID, 1);
				c.getBank(c.getAttributes().getCurrentBankTab()).add(CANNON_STAND_ID, 1);
				c.getBank(c.getAttributes().getCurrentBankTab()).add(CANNON_BARRELS_ID, 1);
				c.getBank(c.getAttributes().getCurrentBankTab()).add(CANNON_FURNACE_ID, 1);
				if(addCannonballs)
					c.getBank(c.getAttributes().getCurrentBankTab()).add(CANNONBALL, cannon.getCannonballs());
				deleted = true;
			}
		} else {
			if(c.getInventory().getFreeSlots() >= 5) {
				c.getInventory().add(CANNON_BASE_ID, 1);
				c.getInventory().add(CANNON_STAND_ID, 1);
				c.getInventory().add(CANNON_BARRELS_ID, 1);
				c.getInventory().add(CANNON_FURNACE_ID, 1);
				if(addCannonballs)
					c.getInventory().add(CANNONBALL, cannon.getCannonballs());
				deleted = true;
			} else {
				c.getPacketSender().sendMessage("You don't have enough free inventory space.");
				deleted = false;
			}
		}
		if(deleted) {
			cannon.setCannonballs(0);
			cannon.setCannonFiring(false);
			cannon.setRotations(0);
			CustomObjects.deleteGlobalObject(c.getAttributes().getCannonObject());
			c.getAttributes().setCannon(null).setSettingUpCannon(false).setCannonObject(null);
		}
	}

	public static void startFiringCannon(Player c, DwarfCannon cannon) {
		if(cannon.cannonFiring() && cannon.getCannonballs() > 15) {
			c.getPacketSender().sendMessage("Your cannon is already firing.");
			return;
		}
		if(cannon.getCannonballs() <= 15) {
			int playerCannonballs = c.getInventory().getAmount(2) > 30 ? 30: c.getInventory().getAmount(2);
			int cannonballsToAdd = playerCannonballs - cannon.getCannonballs();
			if(playerCannonballs < 1) {
				c.getPacketSender().sendMessage("You do not have any cannonballs in your inventory to fire the cannon with.");
				return;
			}
			c.getInventory().delete(CANNONBALL, cannonballsToAdd);
			cannon.setCannonballs(cannonballsToAdd);
			if(!cannon.cannonFiring())
				fireCannon(c, cannon);
		}
	}

	public static void fireCannon(final Player c, final DwarfCannon cannon) {
		if(cannon.cannonFiring())
			return;
		TaskManager.submit(new Task(1) {
			@Override
			public void execute() {
				if(c.getAttributes().getCannon() == null) {
					this.stop();
					return;
				}
				if(cannon.getCannonballs() > 0) {
					rotateCannon(c, cannon);
					cannon.setCannonFiring(true);
					attack(c);
				} else {
					c.getPacketSender().sendMessage("Your cannon has run out of cannonballs.");
					cannon.setCannonballs(0);
					cannon.setCannonFiring(false);
					cannon.setRotations(0);
					this.stop();
				}
			}

		});
	}

	private static void rotateCannon(final Player c, DwarfCannon cannon) {
		final GameObject object = c.getAttributes().getCannonObject();
		cannon.addRotation(1);
		if(Locations.goodDistance(object.getPosition(), c.getPosition(), 12))
			SoundEffects.sendSoundEffect(c, SoundData.ROTATING_CANNON, 9, 0);
		switch(cannon.getRotations()) {
		case 1: // north 
			object.performAnimation(new Animation(516));
			break;
		case 2: // north-east
			object.performAnimation(new Animation(517));
			break;
		case 3: // east
			object.performAnimation(new Animation(518));
			break;
		case 4: // south-east
			object.performAnimation(new Animation(519));
			break;
		case 5: // south
			object.performAnimation(new Animation(520));
			break;
		case 6: // south-west
			object.performAnimation(new Animation(521));
			break;
		case 7: // west
			object.performAnimation(new Animation(514));
			break;
		case 8: // north-west
			object.performAnimation(new Animation(515));
			cannon.setRotations(0);
			break;
		}
	}

	public static void attack(Player player) {
		DwarfCannon cannon = player.getAttributes().getCannon();
		if(cannon == null)
			return;
		NPC n = getTarget(player, cannon);
		if(n == null)
			return;
		int damage = Misc.getRandom(300) - Misc.getRandom((n.getAttributes().getAbsorbRanged() + n.getAttributes().getDefenceLevel() / 2));
		Damage dmg = new Damage(new Hit(damage, CombatIcon.CANNON, Hitmask.RED));
		final Projectile projectile = new Projectile(new Position(cannon.getPosition().getX(), cannon.getPosition().getY(), 43), new Position(n.getPosition().getX(), n.getPosition().getY(), 31), new Graphic(53), 55, 50, 78);
		if (Location.inMulti(player) && Location.inMulti(n)) {
			player.getPacketSender().sendGlobalProjectile(projectile, n);
			DamageHandler.handleAttack(player, n, dmg, AttackType.RANGED, false, true);
		} else {
			boolean inCombat = n.getCombatAttributes().getLastAttacker() != null && n.getCombatAttributes().getLastAttacker().getConstitution() > 0 && System.currentTimeMillis() - n.getCombatAttributes().getLastDamageReceived() < 4500;
			if (inCombat && !Location.inMulti(n)) {
				if(n.getCombatAttributes().getLastAttacker() != null && n.getCombatAttributes().getLastAttacker() != player)
					return;
			}
			player.getPacketSender().sendGlobalProjectile(projectile, n);
			DamageHandler.handleAttack(player, n, dmg, AttackType.RANGED, false, true);
		}
		cannon.setCannonballs(cannon.getCannonballs() - 1);
	}

	private static NPC getTarget(Player p, DwarfCannon cannon) {
		for(NPC n : p.getAttributes().getLocalNpcs()) {
			if(n == null)
				continue;
			int myX = cannon.getPosition().getX();
			int myY = cannon.getPosition().getY();
			int theirX = n.getPosition().getX(); 
			int theirY = n.getPosition().getY();
			if (n.getAttributes().isAttackable() && n.getConstitution() > 0 && Locations.goodDistance(cannon.getPosition(), n.getPosition(), 14) && CombatHandler.checkRequirements(p, n)) {
				switch (cannon.getRotations()) {
				case 1: // north
					if (theirY > myY && theirX >= myX - 1
					&& theirX <= myX + 1)
						return n;
					break;
				case 2: // north-east
					if (theirX >= myX + 1 && theirY >= myY + 1)
						return n;
					break;
				case 3: // east
					if (theirX > myX && theirY >= myY - 1
					&& theirY <= myY + 1)
						return n;
					break;
				case 4: // south-east
					if (theirY <= myY - 1 && theirX >= myX + 1)
						return n;
					break;
				case 5: // south
					if (theirY < myY && theirX >= myX - 1
					&& theirX <= myX + 1)
						return n;
					break;
				case 6: // south-west
					if (theirX <= myX - 1 && theirY <= myY - 1)
						return n;
					break;
				case 7: // west
					if (theirX < myX && theirY >= myY - 1
					&& theirY <= myY + 1)
						return n;
					break;
				case 8: // north-west
					if (theirX <= myX - 1 && theirY >= myY + 1)
						return n;
					break;
				}
			}
		}
		return null;
	}
}
