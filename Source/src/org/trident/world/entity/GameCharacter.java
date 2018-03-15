package org.trident.world.entity;

import org.trident.model.Animation;
import org.trident.model.Damage;
import org.trident.model.Direction;
import org.trident.model.Flag;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Position;
import org.trident.model.UpdateFlag;
import org.trident.model.movement.MovementQueue;
import org.trident.world.content.Following;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.CombatAttributes;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.entity.npc.NPC;

/**
 * A player or NPC
 * @author Gabbe
 */

public abstract class GameCharacter extends Entity {

	public GameCharacter(Position position) {
		super(position);
		location = Location.getLocation(this);
	}
	
	private final CombatAttributes combatAttributes = new CombatAttributes();
	private Location location;
	private UpdateFlag updateFlag = new UpdateFlag();
	private MovementQueue movementQueue = new MovementQueue(this);
	private Damage damage;
	private Position positionToFace;
	private Animation animation;
	private Graphic graphic;
	private Entity interactingEntity;
	private Direction primaryDirection = Direction.NONE, secondaryDirection = Direction.NONE;
	private boolean[] prayerActive = new boolean[30], curseActive = new boolean[20];
	private boolean teleporting;
	private int npcTransformationId;
	private String forcedChat;
	private Position teleportPosition;
	private GameCharacter characterToFollow;
	private Direction direction;

	/*
	 * Getters and setters
	 */

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Graphic getGraphic() {
		return graphic;
	}

	public GameCharacter setGraphic(Graphic graphic) {
		this.graphic = graphic;
		getUpdateFlag().flag(Flag.GRAPHIC);
		return this;
	}

	public Animation getAnimation() {
		return animation;
	}

	public GameCharacter setAnimation(Animation animation) {
		this.animation = animation;
		getUpdateFlag().flag(Flag.ANIMATION);
		return this;
	}

	public Damage getDamage() {
		return damage;
	}

	public GameCharacter setDamage(Damage damage) {
		if (getConstitution() <= 0)
			return this;
		for(int i = 0; i < damage.getHits().length; i++) {
			if(damage.getHits()[i].getDamage() != 9001)
				if(damage.getHits()[i].getDamage() > getConstitution())
					damage.getHits()[i].setDamage(getConstitution());
			if(damage.getHits()[i].getDamage() < 0)
				damage.getHits()[i].setDamage(0);
		}
		this.damage = damage;
		if(this.damage.getHits()[0] != null) {
			Hit hit = damage.getHits()[0];
			if(hit.getDamage() < 0)
				hit.setDamage(0);
			int outcome = getConstitution() - hit.getDamage();
			if (outcome < 0)
				outcome = 0;
			setConstitution(outcome);
			getUpdateFlag().flag(Flag.SINGLE_HIT);
		}
		if(this.damage.getHits().length == 2 && this.damage.getHits()[1] != null) {
			Hit hit = damage.getHits()[1];
			if(hit.getDamage() < 0)
				hit.setDamage(0);
			int outcome = getConstitution() - hit.getDamage();
			if (outcome < 0)
				outcome = 0;
			setConstitution(outcome);
			getUpdateFlag().flag(Flag.DOUBLE_HIT);
		}
		return this;
	}

	public GameCharacter heal(Damage damage) {
		if (getConstitution() <= 0)
			return this;
		for(int i = 0; i < damage.getHits().length; i++) {
			if(damage.getHits()[i].getDamage() < 0)
				return this;
		}
		this.damage = damage;
		if(this.damage.getHits()[0] != null) {
			Hit hit = damage.getHits()[0];
			if(hit.getDamage() < 0)
				hit.setDamage(0);
			int outcome = getConstitution() + hit.getDamage();
			if (outcome < 0)
				outcome = 0;
			setConstitution(outcome);
			getUpdateFlag().flag(Flag.SINGLE_HIT);
		}
		if(this.damage.getHits().length == 2 && this.damage.getHits()[1] != null) {
			Hit hit = damage.getHits()[1];
			if(hit.getDamage() < 0)
				hit.setDamage(0);
			int outcome = getConstitution() + hit.getDamage();
			if (outcome < 0)
				outcome = 0;
			setConstitution(outcome);
			getUpdateFlag().flag(Flag.DOUBLE_HIT);
		}
		return this;
	}

	public Position getPositionToFace() {
		return positionToFace;
	}

	public GameCharacter setPositionToFace(Position positionToFace) {
		this.positionToFace = positionToFace;
		getUpdateFlag().flag(Flag.FACE_POSITION);
		return this;
	}

	public GameCharacter moveTo(Position teleportTarget) {
		getMovementQueue().stopMovement();
		setTeleportPosition(teleportTarget);
		setPosition(teleportTarget);
		return this;
	}

	public UpdateFlag getUpdateFlag() {
		return updateFlag;
	}

	public GameCharacter setMovementQueue(MovementQueue movementQueue) {
		this.movementQueue = movementQueue;
		return this;
	}

	public MovementQueue getMovementQueue() {
		return movementQueue;
	}

	public GameCharacter forceChat(String message) {
		setForcedChat(message);
		getUpdateFlag().flag(Flag.FORCED_CHAT);
		return this;
	}

	public GameCharacter setEntityInteraction(Entity entity) {
		this.interactingEntity = entity;
		getUpdateFlag().flag(Flag.ENTITY_INTERACTION);
		return this;
	}

	public Entity getInteractingEntity() {
		return interactingEntity;
	}

	/**
	 * Appends a character's death.
	 */
	public abstract void appendDeath();

	/**
	 * Gets the character's current constitution.
	 */
	public abstract int getConstitution();

	/**
	 * Gets the character's attack animation.
	 */
	public abstract Animation getAttackAnimation();

	/**
	 * Gets the character's block animation.
	 */
	public abstract Animation getBlockAnimation();

	public abstract int getAttackDelay();

	public abstract GameCharacter setConstitution(int constitution);

	@Override
	public void performAnimation(Animation animation) {
		if(animation == null || isNpc() && ((NPC)this).getId() == SoulWars.BARRICADE_NPC)
			return;
		setAnimation(animation);
		getUpdateFlag().flag(Flag.ANIMATION);
	}

	@Override
	public void performGraphic(Graphic graphic) {
		setGraphic(graphic);
		getUpdateFlag().flag(Flag.GRAPHIC);
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
		int[] directionDeltas = direction.getDirectionDelta();
		setPositionToFace(getPosition().copy().add(directionDeltas[0], directionDeltas[1]));
	}

	public boolean isTeleporting() {
		return this.teleporting;
	}

	public GameCharacter setTeleporting(boolean teleporting) {
		this.teleporting = teleporting;
		return this;
	}

	public String getForcedChat() {
		return forcedChat;
	}

	public GameCharacter setForcedChat(String forcedChat) {
		this.forcedChat = forcedChat;
		return this;
	}

	public Position getTeleportPosition() {
		return teleportPosition;
	}

	public GameCharacter setTeleportPosition(Position teleportPosition) {
		this.teleportPosition = teleportPosition;
		return this;
	}
	public boolean[] getPrayerActive() {
		return prayerActive;
	}

	public boolean[] getCurseActive() {
		return curseActive;
	}

	public GameCharacter setPrayerActive(boolean[] prayerActive) {
		this.prayerActive = prayerActive;
		return this;
	}

	public GameCharacter setPrayerActive(int id, boolean prayerActive) {
		this.prayerActive[id] = prayerActive;
		return this;
	}

	public GameCharacter setCurseActive(boolean[] curseActive) {
		this.curseActive = curseActive;
		return this;
	}

	public GameCharacter setCurseActive(int id, boolean curseActive) {
		this.curseActive[id] = curseActive;
		return this;
	}

	public CombatAttributes getCombatAttributes() {
		return combatAttributes;
	}

	public int getNpcTransformationId() {
		return npcTransformationId;
	}

	public GameCharacter setNpcTransformationId(int npcTransformationId) {
		this.npcTransformationId = npcTransformationId;
		return this;
	}

	/*
	 * Movement queue
	 */

	public void setDirections(Direction primaryDirection,
			Direction secondaryDirection) {
		this.primaryDirection = primaryDirection;
		this.secondaryDirection = secondaryDirection;
	}

	public Direction getPrimaryDirection() {
		return primaryDirection;
	}

	public Direction getSecondaryDirection() {
		return secondaryDirection;
	}

	public void followCharacter(GameCharacter gc) {
		this.characterToFollow = gc;
	}

	public GameCharacter getCharacterToFollow() {
		return characterToFollow;
	}
	
	public void processGameCharacterAttributes() {
		if(combatAttributes.getFreezeDelay() > 0)
			combatAttributes.setFreezeDelay(combatAttributes.getFreezeDelay() - 1);
		if(combatAttributes.getAttackDelay() > 0)
			combatAttributes.setAttackDelay(combatAttributes.getAttackDelay() - 1);
		boolean inCb = CombatHandler.inCombat(this);
		if(inCb) {
			if(combatAttributes.getAttackDelay() <= 0)
				CombatHandler.attack(this, combatAttributes.getCurrentEnemy());
			CombatHandler.closeDistance(this, combatAttributes.getCurrentEnemy());
		} else if(getCharacterToFollow() != null)
			Following.followGameCharacter(this, getCharacterToFollow());
	}
}