package org.trident.world.content.dialogue.impl;

import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueType;

public class GiveSkillcapeDialogue {

	public static Dialogue gaveSkillCape(final int npcId) {
		return new Dialogue() {	
			
			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public int npcId() {
				return npcId;
			}
			
			@Override
			public int id() {
				return 105;
			}
			
			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}
			
			@Override
			public String[] dialogue() {

				return new String[] {
						"Wear the cape with pride, my friend.",
				};
			}
			
			
			@Override
			public void specialAction() {
				
			}
		};
	}
}
