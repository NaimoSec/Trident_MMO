package org.trident.world.content.dialogue.impl;

import org.trident.util.Constants;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.entity.player.Player;

public class HelpbookDialogues {

	
	public static Dialogue firstDialogue(final Player player) {
		
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.STATEMENT;
			}

			@Override
			public int npcId() {
				return -1;
			}
			
			@Override
			public DialogueExpression animation() {
				return null;
			}
			
			@Override
			public String[] dialogue() {
				return new String[] {
						"What would you like to read about?",
				};
			}
			
			@Override
			public void specialAction() { }
			
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.OPTION;
					}

					@Override
					public int npcId() {
						return -1;
					}
					
					@Override
					public DialogueExpression animation() {
						return null;
					}
					
					@Override
					public String[] dialogue() {
						return new String[] {
								"Making money",
								"Navigating",
								"Training",
								"Changing Magic/Prayer book",
								"Nothing, thanks"
						};
					}
					
					@Override
					public void specialAction() {
						player.getAttributes().setDialogueAction(355);
					}
					
					public Dialogue nextDialogue() {
						return optionDialogue(player);
					}
				};
			}
		};
	}
	
	public static Dialogue optionDialogue(final Player player) {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.OPTION;
			}

			@Override
			public int npcId() {
				return -1;
			}
			
			@Override
			public DialogueExpression animation() {
				return null;
			}
			
			@Override
			public String[] dialogue() {
				return new String[] {
						"Making money",
						"Navigating",
						"Training",
						"Changing Magic/Prayer book",
						"Nothing, thanks"
				};
			}
			
			@Override
			public void specialAction() {
				player.getAttributes().setDialogueAction(355);
			}
		};
	}
	
	public static Dialogue secondDialogue(final Player player) {
		return new Dialogue() {
			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public int npcId() {
				return 1;
			}
			
			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}
			
			@Override
			public String[] dialogue() {
				return new String[] {
						"There are many different ways to make money.",
						"If you enjoy figthing, I'd suggest monster slaying.",
						"If you prefer skilling instead, all skills are",
						"profitable and can be used for some quick money.",
						
				};
			}
			
			@Override
			public void specialAction() { }
			
			public Dialogue nextDialogue() {
				return new Dialogue() {
					@Override
					public DialogueType type() {
						return DialogueType.NPC_STATEMENT;
					}

					@Override
					public int npcId() {
						return 1;
					}
					
					@Override
					public DialogueExpression animation() {
						return DialogueExpression.NORMAL;
					}
					
					@Override
					public String[] dialogue() {
						return new String[] {
								"Remember to pick up all items, even the ones you don't",
								"have any use for. You can always sell them to the",
								"General shop for some free coins."
						};
					}
					
					@Override
					public void specialAction() {
					}
					
					public Dialogue nextDialogue() {
						return optionDialogue(player);
					}
				};
			}
		};
	}
	
	public static Dialogue thirdDialogue(final Player player) {
		return new Dialogue() {
			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public int npcId() {
				return 1;
			}
			
			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}
			
			@Override
			public String[] dialogue() {
				return new String[] {
						"You can navigate to different skilling",
						"locations by clicking the skill in the",
						"sidetab interface which I've opened for you.",
						
				};
			}
			
			@Override
			public void specialAction() { 
				player.getPacketSender().sendTab(1);
			}
			
			public Dialogue nextDialogue() {
				return new Dialogue() {
					@Override
					public DialogueType type() {
						return DialogueType.NPC_STATEMENT;
					}

					@Override
					public int npcId() {
						return 1;
					}
					
					@Override
					public DialogueExpression animation() {
						return DialogueExpression.NORMAL;
					}
					
					@Override
					public String[] dialogue() {
						return new String[] {
								"If you want to navigate to other locations",
								"instead, simply use the magic spellbook which",
								"I've opened for you. On top, there are different",
								"kinds of teleports which you can use."
						};
					}
					
					@Override
					public void specialAction() {
						player.getPacketSender().sendTab(Constants.MAGIC_TAB);
					}
					
					public Dialogue nextDialogue() {
						return optionDialogue(player);
					}
				};
			}
		};
	}
	
	public static Dialogue fourthDialogue(final Player player) {
		return new Dialogue() {
			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public int npcId() {
				return 1;
			}
			
			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}
			
			@Override
			public String[] dialogue() {
				return new String[] {
						"To change your Magic book, you need to teleport",
						"to the Magic Guild by clicking the Magic skill",
						"in the interface which I've opened for you.",
						"Once there, simply speak to the Robe store owner."
				};
			}
			
			@Override
			public void specialAction() { 
				player.getPacketSender().sendTab(Constants.SKILLS_TAB);
			}
			
			public Dialogue nextDialogue() {
				return new Dialogue() {
					@Override
					public DialogueType type() {
						return DialogueType.NPC_STATEMENT;
					}

					@Override
					public int npcId() {
						return 1;
					}
					
					@Override
					public DialogueExpression animation() {
						return DialogueExpression.NORMAL;
					}
					
					@Override
					public String[] dialogue() {
						return new String[] {
								"To change your Prayer book, you need to teleport",
								"to the Monastery by clicking the Prayer skill",
								"in the interface which I've opened for you.",
								"Once there, simply speak to Brother Jered."
						};
					}
					
					@Override
					public void specialAction() {
						player.getPacketSender().sendTab(Constants.SKILLS_TAB);
					}
					
					public Dialogue nextDialogue() {
						return optionDialogue(player);
					}
				};
			}
		};
	}
}
