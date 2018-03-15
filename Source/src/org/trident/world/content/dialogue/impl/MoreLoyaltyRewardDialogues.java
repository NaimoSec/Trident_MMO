package org.trident.world.content.dialogue.impl;

import org.trident.model.definitions.ItemDefinition;
import org.trident.model.inputhandling.impl.EnterAmountOfLoyaltyItemsToBuy;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.entity.player.Player;

public class MoreLoyaltyRewardDialogues {

	
	public static Dialogue buyItem(final Player p, final int product, final int cost) {
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
				return 945;
			}

			@Override
			public String[] dialogue() { 
				return new String[] {"@bla@One "+ItemDefinition.forId(product).getName()+" costs @red@"+cost+"@bla@ Loyalty points.", "@bla@How many would you like to buy?"};
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.STATEMENT;
					}

					@Override
					public DialogueExpression animation() {
						return DialogueExpression.NO_EXPRESSION;
					}

					@Override
					public String[] dialogue() {
						return new String[]{""};
					}

					@Override
					public void specialAction() {
						p.getPacketSender().sendEnterAmountPrompt("How many "+ItemDefinition.forId(product).getName()+"s would you like to buy?");
						p.getAttributes().setInputHandling(new EnterAmountOfLoyaltyItemsToBuy(product, cost));
					}
				};
			}
		};
	}
}
