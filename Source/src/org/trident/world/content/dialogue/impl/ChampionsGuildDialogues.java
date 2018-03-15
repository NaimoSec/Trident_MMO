package org.trident.world.content.dialogue.impl;

import org.trident.model.Skill;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.entity.player.Player;

public class ChampionsGuildDialogues {

	/**
	 * The Champions' Guild Master's NPC Id
	 */
	public static final int CHAMPION_GUILD_MASTER = 4288;
	
	
	/**
	 * Attack Skillcape Dialogues
	 * @return
	 */
	
	public static Dialogue skillcapeOfAttack(final Player player) {
		
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public int npcId() {
				return CHAMPION_GUILD_MASTER;
			}
			
			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}
			
			@Override
			public String[] dialogue() {
				return new String[] {
						"Ah" +
						", the cape that I'm wearing.",
						"It proves that I've mastered the skill Attack.",
				};
			}
			
			@Override
			public void specialAction() {
				
			}
			
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.NPC_STATEMENT;
					}

					@Override
					public int npcId() {
						return CHAMPION_GUILD_MASTER;
					}
					
					@Override
					public DialogueExpression animation() {
						return DialogueExpression.NORMAL;
					}
					
					@Override
					public String[] dialogue() {
						return new String[] {
								""
						};
					}
					
					@Override
					public void specialAction() {
						player.getPacketSender().sendInterfaceRemoval();
						if(player.getSkillManager().getMaxLevel(Skill.ATTACK) >= 99) {
							DialogueManager.start(player, hasEnoughLevel((player), "Attack", 45));
							player.getAttributes().setDialogueAction(45);
						} else {
							DialogueManager.start(player, notEnoughLevel("Attack"));
						}
					}
			
				};
			}
	
		};
	}
	
	/**
	 * Skillcape of Strength
	 */
	
		public static Dialogue skillcapeOfStrength(final Player player) {
		
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public int npcId() {
				return CHAMPION_GUILD_MASTER;
			}
			
			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}
			
			@Override
			public String[] dialogue() {
				return new String[] {
						"Ah, the Skillcape of Strength.",
						"Beautifully designed by the best crafters.",
				};
			}
			
			@Override
			public void specialAction() {
				
			}
			
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.NPC_STATEMENT;
					}

					@Override
					public int npcId() {
						return CHAMPION_GUILD_MASTER;
					}
					
					@Override
					public DialogueExpression animation() {
						return DialogueExpression.NORMAL;
					}
					
					@Override
					public String[] dialogue() {
						return new String[] {
								"h"
						};
					}
					
					@Override
					public void specialAction() {
						player.getPacketSender().sendInterfaceRemoval();
						if(player.getSkillManager().getMaxLevel(Skill.STRENGTH) >= 99) {
							DialogueManager.start(player, hasEnoughLevel((player), "Strength", 46));
							player.getAttributes().setDialogueAction(47);
						} else {
							DialogueManager.start(player, notEnoughLevel("Strength"));
						}
					}
			
				};
			}
	
		};
	}
	
		/**
		 * Defence Skillcape Dialogues
		 * @return
		 */
		
		public static Dialogue skillcapeOfDefence(final Player player) {
			
			return new Dialogue() {

				@Override
				public DialogueType type() {
					return DialogueType.NPC_STATEMENT;
				}

				@Override
				public int npcId() {
					return CHAMPION_GUILD_MASTER;
				}
				
				@Override
				public DialogueExpression animation() {
					return DialogueExpression.NORMAL;
				}
				
				@Override
				public String[] dialogue() {
					return new String[] {
							"Ah, the Defence Skillcape. A powerful cape that",
							"proves that a player can take very much damage.",
					};
				}
				
				@Override
				public void specialAction() {
					
				}
				
				public Dialogue nextDialogue() {
					return new Dialogue() {

						@Override
						public DialogueType type() {
							return DialogueType.NPC_STATEMENT;
						}

						@Override
						public int npcId() {
							return CHAMPION_GUILD_MASTER;
						}
						
						@Override
						public DialogueExpression animation() {
							return DialogueExpression.NORMAL;
						}
						
						@Override
						public String[] dialogue() {
							return new String[] {
									""
							};
						}
						
						@Override
						public void specialAction() {
							player.getPacketSender().sendInterfaceRemoval();
							if(player.getSkillManager().getMaxLevel(Skill.DEFENCE) >= 99) {
								DialogueManager.start(player, hasEnoughLevel((player), "Defence", 47));
							} else {
								DialogueManager.start(player, notEnoughLevel("Defence"));
							}
						}
				
					};
				}
		
			};
		}
		
		

	/**
	 * Misc
	 */
	
	public static Dialogue notEnoughLevel(final String Skill) {
		return new Dialogue() {	
			
			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public int npcId() {
				return CHAMPION_GUILD_MASTER;
			}
			
			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}
			
			@Override
			public String[] dialogue() {

				return new String[] {
						"You could use some training. Make sure you're gaining",
						""+Skill+" Experience when ever you're in combat",
						"by selecting the correct fight mode for your weapon."
				};
			}
			
			
			@Override
			public void specialAction() {
				
			}
			
			public Dialogue nextDialogue() {
				return new Dialogue() {	
					
					@Override
					public DialogueType type() {
						return DialogueType.NPC_STATEMENT;
					}

					@Override
					public int npcId() {
						return CHAMPION_GUILD_MASTER;
					}
					
					@Override
					public DialogueExpression animation() {
						return DialogueExpression.NORMAL;
					}
					
					@Override
					public String[] dialogue() {

						return new String[] {
							"Once you've trained enough and mastered the skill,",
							"come talk to me! I have some spare Skillcapes for sale.",
							"I'll be here waiting for you!"
						};
					}
					
					
					@Override
					public void specialAction() {
						
					}
				};
			}
		};
	}
	
	public static Dialogue hasEnoughLevel(final Player player, final String skill, final int dialogueAction) {
		return new Dialogue() {	
			
			@Override
			public DialogueType type() {
				return DialogueType.NPC_STATEMENT;
			}

			@Override
			public int npcId() {
				return CHAMPION_GUILD_MASTER;
			}
			
			@Override
			public DialogueExpression animation() {
				return DialogueExpression.NORMAL;
			}
			
			@Override
			public String[] dialogue() {

				return new String[] {
						"I can see that you've mastered the skill.",
						"Would you like to purchase a Skillcape of "+skill+"?",
						"I'm able to give you one in exchange for 99,000 Coins."
				};
			}
			
			@Override
			public void specialAction() {
				
			}
			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.OPTION;
					}

					
					@Override
					public DialogueExpression animation() {
						return null;
					}
					
					@Override
					public String[] dialogue() {
						return new String[] {
							"Yes",
							"No"
					
						};
					}
					
					@Override
					public void specialAction() {
						player.getAttributes().setDialogueAction(dialogueAction);
					}
				};
			}
		};
	}
}
