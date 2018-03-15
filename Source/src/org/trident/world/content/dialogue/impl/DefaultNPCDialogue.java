package org.trident.world.content.dialogue.impl;

import org.trident.util.MathUtils;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.entity.npc.NPC;

/**
 * Represents a random default npc dialogue.
 * 
 * @author relex lawl
 */

public class DefaultNPCDialogue extends Dialogue {

	/**
	 * The DefaultNPCDialogue constructor.
	 * @param npc	The npc that will dialogue with the associated player.
	 */
	public DefaultNPCDialogue(NPC npc) {
		this.npc = npc;
	}
	
	/**
	 * The npc that will dialogue with the associated player.
	 */
	private final NPC npc;

	@Override
	public DialogueType type() {
		return DialogueType.NPC_STATEMENT;
	}

	@Override
	public DialogueExpression animation() {
		return DialogueExpression.NORMAL;
	}

	@Override
	public String[] dialogue() {
		return new String[] {
			RANDOM_DIALOGUE[MathUtils.random(RANDOM_DIALOGUE.length - 1)]
		};
	}
	
	@Override
	public int npcId() {
		return npc.getId();
	}

	/**
	 * Random dialogues the npc can say.
	 */
	private static final String[] RANDOM_DIALOGUE = {
		"Hello, nice day for an adventure, huh?",
		"Wow! You're one of those adventurers right?",
		"Be careful out there, adventurer!"
	};
}
