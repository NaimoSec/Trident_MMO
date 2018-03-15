package org.trident.world.entity.npc;

import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.NPCDeathTask;
import org.trident.model.Animation;
import org.trident.model.Position;
import org.trident.model.definitions.NPCDefinition;
import org.trident.world.entity.GameCharacter;

/**
 * Represents a non-playable character, which players can interact with.
 * @author Gabbe
 */

public class NPC extends GameCharacter {

	private final int id;
	private final Position defaultPosition;
	private NPCAttributes attributes = new NPCAttributes(), defaultAttributes = new NPCAttributes();
	private NPCRegion region;

	public NPC(int id, Position position) {
		super(position);
		this.id = id;
		this.defaultPosition = position;
	}

	@Override
	public void appendDeath() {
		if(!attributes.isDying() && !attributes.isDead()) {
			TaskManager.submit(new NPCDeathTask(this));
			attributes.setDying(true).setDead(true);
		}
	}

	@Override
	public int getConstitution() {
		return attributes.getConstitution();
	}

	@Override
	public NPC setConstitution(int constitution) {
		attributes.setConstitution(constitution);
		if(getConstitution() <= 0)
			appendDeath();
		return this;
	}

	@Override
	public boolean isNpc() {
		return true;
	}

	@Override
	public Animation getAttackAnimation() {
		return getDefinition() != null && getDefinition().getAttackAnimation() != null && getDefinition().getAttackAnimation().getId() > 0 ? getDefinition().getAttackAnimation() : new Animation(NPCData.getAttackAnimation(getId()));
	}

	@Override
	public Animation getBlockAnimation() {
		return getDefinition() != null && getDefinition().getDefenceAnimation() != null && getDefinition().getDefenceAnimation().getId() > 0 ? getDefinition().getDefenceAnimation() : new Animation(getDefinition() == null || getDefinition().getAttackAnimation() == null ? -1 : getDefinition().getAttackAnimation().getId() + 2);
	}

	@Override
	public int getAttackDelay() {
		return attributes.getAttackSpeed();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof NPC && ((NPC)other).getIndex() == getIndex();
	}

	@Override
	public int getSize() {
		return getDefinition().getSize();
	}

	public void process() {
		processGameCharacterAttributes();
		if(attributes.isAttackable() && !attributes.isDead() && !attributes.isDying() && attributes.getConstitution() < getDefaultAttributes().getConstitution()) {
			if(System.currentTimeMillis() - getCombatAttributes().getLastDamageReceived() >= (getId() == 13447 ? 45000 : 15000)) {
				int defaultConstitution = getDefaultAttributes().getConstitution();
				setConstitution(getConstitution() + (int)(defaultConstitution * 0.1));
				if(getConstitution() > defaultConstitution)
					setConstitution(defaultConstitution);
			}
		}
	}

	public int getId() {
		return id;
	}

	public NPC setAttributes(NPCAttributes properties) {
		this.attributes = properties;
		return this;
	}

	public NPCAttributes getAttributes() {
		return attributes;
	}

	public NPC setDefaultAttributes(NPCAttributes defaultProperties) {
		this.defaultAttributes = defaultProperties;
		return this;
	}

	public NPCAttributes getDefaultAttributes() {
		return defaultAttributes;
	}

	public Position getDefaultPosition() {
		return defaultPosition;
	}

	public NPCDefinition getDefinition() {
		return NPCDefinition.forId(id);
	}
	
	public NPCRegion getRegion() {
		return region;
	}
	
	public void setRegion(NPCRegion region) {
		this.region = region;
	}
}
