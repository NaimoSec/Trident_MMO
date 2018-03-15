package org.trident.world.content.dialogue.impl;

import org.trident.util.Misc;
import org.trident.world.content.Lottery;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.entity.player.Player;

/**
 * Represents the lottery dialogues
 * @author Gabbe
 */

public class LotteryDialogues{
	
	public static Dialogue getCurrentPot(Player p) {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}
			
			@Override
			public int npcId() {
				return 3001;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"The pot is currently at:", ""+Misc.insertCommasToNumber(""+Lottery.getPot())+" coins."};
			}
			
			@Override
			public Dialogue nextDialogue() {
				return DialogueManager.getDialogues().get(422);
			}
		};
	}
	
	public static Dialogue getLastWinner(Player p) {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}
			
			@Override
			public int npcId() {
				return 3001;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"Last week's winner was "+Lottery.getLastWinner()+"."};
			}
			
			@Override
			public Dialogue nextDialogue() {
				return DialogueManager.getDialogues().get(422);
			}
		};
	}
}
