package org.trident.world.entity.npc.custom.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Flag;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Position;
import org.trident.model.Projectile;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.DamageHandler;
import org.trident.world.content.combat.combatdata.magic.MagicExtras;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCAggression;
import org.trident.world.entity.npc.NPCAttributes;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class Nex extends NPC {

	NexMinion FUMUS, UMBRA, CRUOR, GLACIES;

	public Nex() {
		super(13447, new Position(2925, 5203));
		setAttributes(new NPCAttributes().setAggressive(true).setAttackable(true).setRespawn(true).setWalkingDistance(3).setAttackLevel(250).setDefenceLevel(500).setStrengthLevel(250).setConstitution(20000).setAbsorbMelee(20).setAbsorbRanged(20).setAbsorbMagic(20).setAttackSpeed(6).setMaxHit(600)).setDefaultAttributes(new NPCAttributes().setAggressive(true).setAttackable(true).setRespawn(true).setWalkingDistance(3).setAttackLevel(250).setDefenceLevel(500).setStrengthLevel(250).setConstitution(20000).setAbsorbMelee(20).setAbsorbRanged(20).setAbsorbMagic(20).setAttackSpeed(6).setMaxHit(600));
		World.register(this);
		spawnMinions();
	}

	public void reset() {
		CustomNPC.setNex(new Nex());
	}

	public void spawnMinions() {
		if(FUMUS != null)
			World.deregister(FUMUS);
		if(UMBRA != null)
			World.deregister(UMBRA);
		if(CRUOR != null)
			World.deregister(CRUOR);
		if(GLACIES != null)
			World.deregister(GLACIES);
		FUMUS = new NexMinion(13451, new Position(2916, 5213));
		UMBRA = new NexMinion(13452, new Position(2934, 5213));
		CRUOR = new NexMinion(13453, new Position(2915, 5193));
		GLACIES = new NexMinion(13454, new Position(2935, 5193));
	}

	private boolean[] magesKilled = new boolean[4];
	private boolean[] magesAttackable = new boolean[4];
	private int phase;
	private int prayerType;
	private int prayerTimer;
	private boolean[] attacks = new boolean[18];

	public void attack(final NPC attacker, final Player p) {
		if(!p.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().hasEnteredRoom() || Misc.getRandom(20) <= 2) {
			Player plr2 = Misc.getCloseRandomPlayer(p.getAttributes().getLocalPlayers());
			if(plr2 != null) {
				CombatHandler.resetAttack(attacker);
				NPCAggression.processFor(plr2);
				return;
			}
		}
		if(phase == 0)
		{
			int rnd = Misc.getRandom(10);
			if(rnd <= 2)
			{
				attacker.forceChat("Let the virus flow through you!");
				cough(p);
				attacker.performAnimation(new Animation(6986));
				DamageHandler.handleAttack(attacker, p, new Damage(new Hit(200 + Misc.getRandom(100), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
				return;
			}
			/*if(rnd == 4 || p.getUsername().equals("Gabbe")) FUCKING FLY U IDIOT OMG
			{
				attacker.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
				attacker.performAnimation(new Animation(6321));
				attacker.forceChat("There is... NO ESCAPE!");
				final int rndAtt = Misc.getRandom(coords.length-1);
				GameServer.getTaskScheduler().schedule(new Task(1) {
					int tick = 0;
					@Override
					protected void execute() {
						switch(tick) {
						case 1:
							attacker.moveTo(new Position(coords[rndAtt][0], coords[rndAtt][1]));
							attacker.setPositionToFace(p.getPosition().copy());
							break;
						case 2:
						case 3:
						case 4:
						case 5:
							attacker.moveTo(new Position(attacker.getPosition().getX() + coords[rndAtt][2], attacker.getPosition().getY() + coords[rndAtt][3]));
							for(Player p2 : Misc.getCombinedPlayerList(p))
							{
								if(p2 == null)
									continue;
								if(p2.distanceToPoint(attacker.getPosition().getX(), attacker.getPosition().getY()) < 3)
									p2.setDamage(new Damage(new Hit(200 + Misc.getRandom(200), CombatIcon.NONE, Hitmask.RED)));
							}
							break;
						case 6:
							attacker.getMovementQueue().setMovementStatus(MovementStatus.NONE);
							stop();
							break;
						}
						tick++;
					}
				});
				return;
			} else {*/
			if(p.getPosition().distanceToPoint(this.getPosition().getX(), this.getPosition().getY()) <= 3 && Misc.getRandom(1) == 0)
			{
				attacker.performAnimation(new Animation(6354));
				DamageHandler.handleAttack(attacker, p, new Damage(new Hit(200 + Misc.getRandom(200), CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
				return;
			} else {
				attacker.performAnimation(new Animation(6326));
				p.performGraphic(new Graphic(383));
				DamageHandler.handleAttack(attacker, p, new Damage(new Hit(200+Misc.getRandom(170), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
				return;
			}
		}
		//}
		if(phase == 1)
		{
			int rnd = Misc.getRandom(20);
			if(rnd < 2 && !attacks[3])
			{
				attacker.forceChat("Fear the shadow!");
				attacks[3] = true;
				for(final Player p_ : Misc.getCombinedPlayerList(p))
				{
					TaskManager.submit(new Task(1) {
						int origX, origY;
						int ticks;
						@Override
						public void execute()
						{
							if(ticks == 0)
							{
								origX = p_.getPosition().getX();
								origY = p_.getPosition().getY();
							}
							if(ticks == 5)
							{
								if(origX == p_.getPosition().getX() && origY == p_.getPosition().getY())
								{
									p.setDamage(new Damage(new Hit(300 + Misc.getRandom(100), CombatIcon.NONE, Hitmask.RED)));
									this.stop();
									attacks[3] = false;
								}
							}
							ticks++;
						}
					});
				}
			}else if(rnd >= 5 && rnd <= 7 && !attacks[4])
			{
				attacker.forceChat("Embrace darkness!");
				attacks[4] = true;
				TaskManager.submit(new Task(1) {
					int ticks = 0;
					@Override
					public void execute()
					{
						for(Player p_ : Misc.getCombinedPlayerList(p))
						{
							if(ticks == 10)
								setShadow(p_, 250);
							else {
								double dist = p_.getPosition().distanceToPoint(getPosition().getX(), getPosition().getY());
								if(dist < 3)
								{
									p_.getPacketSender().sendMessage("The shadows start to consume you!");
									p.setDamage(new Damage(new Hit(10, CombatIcon.NONE, Hitmask.RED)));
									setShadow(p_, 20);
								}
								if(dist >= 3 && dist < 5)
									setShadow(p_, 40);
								if(dist > 5)
									setShadow(p_, 90);
							}
						}
						if(ticks == 10)
						{
							this.stop();
							attacks[4] = false;
						}
						ticks++;
					}
				});
				attacker.performAnimation(new Animation(6984));
				DamageHandler.handleAttack(attacker, p, new Damage(new Hit(Misc.getRandom(300), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
				return;
			} else {
				if(p.getPosition().distanceToPoint(this.getPosition().getX(), this.getPosition().getY()) <= 2 && Misc.getRandom(1) == 0)
				{
					attacker.performAnimation(new Animation(6354));
					DamageHandler.handleAttack(attacker, p, new Damage(new Hit(200 + Misc.getRandom(200), CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
					return;
				} else {
					attacker.performAnimation(new Animation(6326));
					attacker.performGraphic(new Graphic(378));
					DamageHandler.handleAttack(attacker, p, new Damage(new Hit(200 + Misc.getRandom(200), CombatIcon.RANGED, Hitmask.RED)), AttackType.RANGED, false, false);
					return;
				}
			}
		}
		if(phase == 4)
		{
			prayerTimer++;
			if(prayerTimer == 4)
			{
				if(prayerType == 0) {
					prayerType = 1;
					attacker.getAttributes().setTransformationId(13448);
					attacker.getUpdateFlag().flag(Flag.TRANSFORM);
				} else {
					prayerType = 0; 
					attacker.getAttributes().setTransformationId(13450);
					attacker.getUpdateFlag().flag(Flag.TRANSFORM);
				}
				prayerTimer = 0;
			}
			if(p.getPosition().distanceToPoint(this.getPosition().getX(), this.getPosition().getY()) <= 2 && Misc.getRandom(1) == 0)
			{
				attacker.performAnimation(new Animation(6354));
				DamageHandler.handleAttack(attacker, p, new Damage(new Hit(Misc.getRandom(400), CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
				return;
			} else {
				attacker.performAnimation(new Animation(6326));
				attacker.performGraphic(new Graphic(373));
				DamageHandler.handleAttack(attacker, p, new Damage(new Hit(Misc.getRandom(300), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
				return;
			}
		}
		if(phase == 3)
		{
			int rnd = Misc.getRandom(15);
			if(rnd >= 0 && rnd <= 3 && !attacks[0])
			{
				attacks[0] = true;
				attacker.forceChat("Die now, in a prison of ice!");
				final int origX = p.getPosition().getX();
				final int origY = p.getPosition().getY();
				p.getMovementQueue().stopMovement();
				for(int x = origX-1; x < origX+1; x++)
				{
					for(int y = origY-1; y < origY+1; y++)
					{
						//if(Region3.getClipping(x, y, 0) == 0)
						//	CustomObjects.spawnGlobalObjectWithinDistance(new GameObject(57263, new Position(x, y)));
					}
				}
				TaskManager.submit(new Task(10) {
					@Override
					public void execute()
					{
						if(p.getPosition().getX() == origX && p.getPosition().getY() == origY)
						{
							p.setDamage(new Damage(new Hit(300 + Misc.getRandom(150), CombatIcon.NONE, Hitmask.RED)));
						}
						for(int x = origX-1; x < origX+1; x++)
						{
							for(int y = origY-1; y < origY+1; y++)
							{
								//if(Region3.getClipping(x, y, 0) == 0)
								//	CustomObjects.spawnGlobalObjectWithinDistance(new GameObject(6951, new Position(x, y)));
							}
						}
						attacks[0] = false;
						this.stop();

					}
				});
			} else if(rnd > 3 && rnd <= 5 && !attacks[1])
			{
				attacker.forceChat("Contain this!");
				attacks[1] = true;
				final int origX = this.getPosition().getX(), origY = this.getPosition().getY();
				for(int x = origX-2; x < origX+2; x++)
				{
					for(int y = origY-2; y < origY+2; y++)
					{
						if(x == origX-2 || x == origX+2 || y == origY-2 || y == origY+2) {
							//	if(Region3.getClipping(x, y, 0) == 0)
							//	CustomObjects.spawnGlobalObjectWithinDistance(new GameObject(57262, new Position(x, y)));
						}
					}
				}
				TaskManager.submit(new Task(1) {
					int ticks = 0;
					@Override
					public void execute()
					{
						for(int x = origX-ticks; x < origX+ticks; x++)
						{
							for(int y = origY-ticks; y < origY+ticks; y++)
							{
								if(x == origX-ticks || y == origY-ticks || x == origX+ticks || y == origY+ticks)
								{
									p.getPacketSender().sendGraphic(new Graphic(366), new Position(x, y));
									for(Player p_ : Misc.getCombinedPlayerList(p))
									{
										if(p_ == null)
											continue;
										if(p_.getPosition().getX() == x && p_.getPosition().getY() == y)
											p.setDamage(new Damage(new Hit(200 + Misc.getRandom(110), CombatIcon.NONE, Hitmask.RED)));
									}
								}
							}
						}
						if(ticks == 6) {
							attacks[1] = false;
							this.stop();
						}
						ticks++;
					}
				});
			} else {
				if(p.getPosition().distanceToPoint(this.getPosition().getX(), this.getPosition().getY()) <= 2 && Misc.getRandom(1) == 0)
				{
					attacker.performAnimation(new Animation(6354));
					DamageHandler.handleAttack(attacker, p, new Damage(new Hit(Misc.getRandom(400), CombatIcon.MELEE, Hitmask.RED)), AttackType.MELEE, false, false);
				} else {
					MagicExtras.freezeTarget(p, 10, new Graphic(366));
					attacker.performAnimation(new Animation(6326));
					DamageHandler.handleAttack(attacker, p, new Damage(new Hit(Misc.getRandom(300), CombatIcon.MAGIC, Hitmask.RED)), AttackType.MAGIC, false, false);
				}
			}
		}
	}

	public void dealtDamage(final Player p, final int damage) {
		if(phase == 4)
		{
			if(prayerType == 0 && damage != 0)
			{
				p.getPacketSender().sendProjectile(new Projectile(this.getPosition(), p.getPosition(), new Graphic(2263)), p);
				TaskManager.submit(new Task(2) {
					@Override
					public void execute()
					{
						setConstitution(getConstitution() + (damage/5));
						p.getSkillManager().setCurrentLevel(Skill.PRAYER, p.getSkillManager().getCurrentLevel(Skill.PRAYER) - (damage/85));
						if(p.getSkillManager().getCurrentLevel(Skill.PRAYER) < 0)
							p.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
						p.performGraphic(new Graphic(2264));
						p.getPacketSender().sendProjectile(new Projectile(p.getPosition(), getPosition(), new Graphic(2263)), p);
						this.stop();
					}
				});
			}
		}
	}

	public void handleDeath() {
		phase = 0;
		this.forceChat("Taste my wrath!");
		final int x = this.getPosition().getX(), y = this.getPosition().getY();
		TaskManager.submit(new Task(4) {
			@Override
			public void execute() {
				for(Player p : World.getPlayers())
				{
					if(p == null)
						continue;
					if(p.getPosition().distanceToPoint(x, y) <= 3)
					{
						p.setDamage(new Damage(new Hit(150, CombatIcon.NONE, Hitmask.RED)));
					}	
					if(p.getPosition().distanceToPoint(x, y) <= 20)
					{

						for(int x_ = x-2; x_ < x+2; x_++)
						{
							for(int y_ = y-2; y_ < y+2; y_++)
							{
								p.getPacketSender().sendGraphic(new Graphic(2259), new Position(x_, y));
							}
						}
					}
				}
				this.stop();
			}
		});
		for(int i = 0; i < magesAttackable.length; i++)
			magesAttackable[i] = true;
		NexMinion[] nexMinions = {FUMUS, UMBRA, CRUOR, GLACIES};
		for(NexMinion npc : nexMinions) {
			if(npc != null && npc.getConstitution() > 0)
				World.deregister(npc);
		}
	}

	public void sendGlobalMsg(Player original, String message) {
		for(Player p : Misc.getCombinedPlayerList(original)) {
			if(p != null) {
				p.getPacketSender().sendMessage(message);
			}
		}
	}

	public void takeDamage(Player from, int damage) {
		if(phase == 4)
		{
			if(prayerType == 0)
			{
				setConstitution(getConstitution() + (damage/2));
			}
		}
		if(phase == 3)
		{
			if(getConstitution() <= 4000)
			{
				if(magesKilled[3]) {
					phase = 4;
					forceChat("NOW, THE POWER OF ZAROS!");
					getAttributes().setDefenceLevel(getAttributes().getDefenceLevel() * 2);
				}
				if(!magesAttackable[3]) {
					forceChat("Don't fail me, Glacies!");
					sendGlobalMsg(from, "@red@Glacies is now attackable! You need to defeat him to weaken Nex!");
					setConstitution(4000);
					magesAttackable[3] = true;
				}
				if(magesAttackable[3] && !magesKilled[3])
					setConstitution(4000);
			}
		}
		if(phase == 2)
		{
			/*	if(siphoning)
			{
				setConstitution(getConstitution() + (damage*2));
			}*/
			if(getConstitution() <= 8000)
			{
				if(magesKilled[2])
					phase = 3;
				if(!magesAttackable[2]) {
					forceChat("Don't fail me, Cruor!");
					sendGlobalMsg(from, "@red@Cruor is now attackable! You need to defeat him to weaken Nex!");
					setConstitution(8000);
					magesAttackable[2] = true;
				}
				if(magesAttackable[2] && !magesKilled[2])
					setConstitution(8000);
			}
		}
		if(phase == 1)
		{
			if(getConstitution() <= 12000)
			{
				if(magesKilled[1])
					phase = 2;
				if(!magesAttackable[1]) {
					forceChat("Don't fail me, Umbra!");
					sendGlobalMsg(from, "@red@Umbra is now attackable! You need to defeat him to weaken Nex!");
					magesAttackable[1] = true;
				}
				if(magesAttackable[1] && !magesKilled[1])
					setConstitution(12000);
			}
		}
		if(phase == 0)
		{
			if(getConstitution() <= 16000)
			{
				if(magesKilled[0])
					phase = 1;
				if(!magesAttackable[0]) {
					forceChat("Don't fail me, Fumus!");
					sendGlobalMsg(from, "@red@Fumus is now attackable! You need to defeat her to weaken Nex!");
					magesAttackable[0] = true;
				}
				if(magesAttackable[0] && !magesKilled[0])
					setConstitution(16000);
			}
		}
	}

	public static void cough(final Player p) {
		if (p.getAttributes().isCoughing())
			return;
		p.getAttributes().setCoughing(true);
		TaskManager.submit(new Task(1) {
			int ticks = 0;
			@Override
			public void execute() {
				if (ticks == 5 || p.getConstitution() <= 0)
					this.stop();
				p.forceChat("Cough..");
				for(Skill skill : Skill.values()) {
					if(skill != Skill.CONSTITUTION && skill != Skill.PRAYER) {
						p.getSkillManager().setCurrentLevel(skill, p.getSkillManager().getCurrentLevel(skill)-1);
						if(p.getSkillManager().getCurrentLevel(skill) < 1)
							p.getSkillManager().setCurrentLevel(skill, 1);
					}
				}
				for (Player p2 : Misc.getCombinedPlayerList(p)) {
					if (p2 == null || p2 == p)
						continue;
					if (p2.getPosition().distanceToPoint(p.getPosition().getX(), p.getPosition().getY()) == 1)
						cough(p2);
				}
				ticks++;
			}

			@Override
			public void stop() {
				setEventRunning(false);
				p.getAttributes().setCoughing(false);
			}
		});
	}

	public boolean[] getMagesKilled()
	{
		return magesKilled;
	}
	public boolean[] getMagesAttackable()
	{
		return magesAttackable;
	}

	public static void setShadow(Player p, int shadow) {
		if(p.getAttributes().getShadowState() == shadow)
			return;
		p.getAttributes().setShadowState(shadow);
		p.getPacketSender().sendShadow();
	}
}
