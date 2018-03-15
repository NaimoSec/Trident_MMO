package org.trident.world.content.dialogue.impl;

import org.trident.model.Position;
import org.trident.model.movement.MovementStatus;
import org.trident.util.Constants;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.entity.player.Player;

public class TutorialDialogues {

	public static Dialogue startingDialogue(final Player player) {
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
				return DialogueExpression.TALK_SWING;
			}

			@Override
			public String[] dialogue() {
				return new String[] {
						"Welcome to Trident",
						""+player.getUsername()+", would you like to start the tutorial?",
						"If you choose to complete it all, you will be rewarded."
				};
			}

			@Override
			public void specialAction() {
				player.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.OPTION;
					}

					@Override
					public int npcId() {
						return 945;
					}

					@Override
					public DialogueExpression animation() {
						return DialogueExpression.TALK_SWING;
					}

					@Override
					public String[] dialogue() {
						return new String[] {
								"Start tutorial (reward)",
								"Skip tutorial",
						};
					}

					@Override
					public void specialAction() {
						player.getAttributes().setDialogueAction(1);
					}
				};
			}
		};
	}

	public static Dialogue tutorialDialogues(final Player player) {
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
						"How do I make money?",
						"Why and how do I level up skills?",
						"How do I navigate to other locations?",
						"Where can I find the main stores?",
						"I'm ready to play Trident!"
				};
			}
			@Override
			public void specialAction() {
				player.getAttributes().setDialogueAction(294);
				player.moveTo(Constants.DEFAULT_POSITION.copy());
			}
		};
	}

	public static Dialogue makingMoney(final Player player) {
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
				return DialogueExpression.PLAIN_TALKING;
			}

			@Override
			public String[] dialogue() {
				return new String[] {
						"There are many different ways to make money.",
						"The more popular ways are through skilling",
						"and killing mobs or players for their items.",
				};
			}

			@Override
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
						return DialogueExpression.TALK_SWING;
					}

					@Override
					public String[] dialogue() {
						return new String[] {
								"You can sell items to the General store.",
								"You can also trade them with other players."
						};
					}

					@Override
					public void specialAction() {
						player.moveTo(new Position(3077, 3510));
						if(player.getAttributes().getResetPosition() != null)
							player.getAttributes().setResetPosition(Constants.DEFAULT_POSITION.copy());

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
								return DialogueExpression.PLAIN_TALKING;
							}

							@Override
							public String[] dialogue() {
								return new String[] {
										"A good way to make money is skilling!",
										"Mining for example, simply mine some ores and sell them",
										"to the General store or other players."
								};
							}

							@Override
							public void specialAction() {
								player.moveTo(new Position(3023, 9740));
								if(player.getAttributes().getResetPosition() != null)
									player.getAttributes().setResetPosition(Constants.DEFAULT_POSITION.copy());
							}

							@Override
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
										return DialogueExpression.TALK_SWING;
									}

									@Override
									public String[] dialogue() {
										return new String[] {
												"If you prefer a more dangerous way, you can",
												"start off by killing some low-level mobs",
												"such as Chaos Druids in the Edgeville dungeon.",
												"They drop valuable herbs."
										};
									}

									@Override
									public void specialAction() {
										if(player.getAttributes().getResetPosition() != null)
											player.getAttributes().setResetPosition(Constants.DEFAULT_POSITION.copy());
										player.moveTo(new Position(3113, 9928));
										player.getAttributes().getTutorialFinished()[0] = true;
									}

									public Dialogue nextDialogue() {
										return tutorialDialogues(player);
									}
								};
							}
						};
					}
				};
			}
		};
	}

	public static Dialogue whyLevelUp(final Player player) {
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
				return DialogueExpression.PLAIN_TALKING;
			}

			@Override
			public String[] dialogue() {
				return new String[] {
						"By leveling up a skill, you unlock new features and",
						"possibilities. Once you achieve level 99 in a skill,",
						"you can buy a Skillcape for the skill which has",
						"a rare emote."
				};
			}

			@Override
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
						return DialogueExpression.TALK_SWING;
					}

					@Override
					public String[] dialogue() {
						return new String[] {
								"There is also a Highscores page on the Trident",
								"website, which you can use to track your own or",
								"other player's progress in the game.",
								"The higher level, the higher rank."
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
								return DialogueType.NPC_STATEMENT;
							}

							@Override
							public int npcId() {
								return 1;
							}

							@Override
							public DialogueExpression animation() {
								return DialogueExpression.PLAIN_TALKING;
							}

							@Override
							public String[] dialogue() {
								return new String[] {
										"In order to train a skill, you have to gain experience.",
										"For example, if you would want to level up in Ranged,",
										"you would need to fight using a Ranged weapon,",
										"such as a bow with arrows or a crossbow with bolts."
								};
							}

							@Override
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
										return DialogueExpression.TALK_SWING;
									}

									@Override
									public String[] dialogue() {
										return new String[] {
												"Another example would be the Firemaking skill.", 
												"To gain Firemaking experience, you have to set logs",
												"on fire. You can obtain logs through the Woodcutting",
												"skill."

										};
									}

									@Override
									public void specialAction() {
										player.getAttributes().getTutorialFinished()[1] = true;
									}

									@Override
									public Dialogue nextDialogue() {
										return tutorialDialogues(player);
									}
								};
							}
						};
					}
				};
			}
		};
	}



	public static Dialogue navigation(final Player player) {
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
				return DialogueExpression.PLAIN_TALKING;
			}

			@Override
			public String[] dialogue() {
				return new String[] {
						"I've opened your Magic spellbook for you.",
						"On the top of the interface, you can find different",
						"teleports which you can use to navigate around",
						"in the world of Trident."
				};
			}

			@Override
			public void specialAction() {
				player.getPacketSender().sendTab(Constants.MAGIC_TAB);
			}

			@Override
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
						return DialogueExpression.TALK_SWING;
					}

					@Override
					public String[] dialogue() {
						return new String[] {
								"You can navigate to different skilling locations",
								"by clicking on the skill in the skill tab which",
								"I've opened for you."
						};
					}

					@Override
					public void specialAction() {
						player.getPacketSender().sendTab(1);
						player.getAttributes().getTutorialFinished()[2] = true;
					}

					@Override
					public Dialogue nextDialogue() {
						return tutorialDialogues(player);
					}
				};
			}
		};
	}

	public static Dialogue mainShops(final Player player) {
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
				return DialogueExpression.PLAIN_TALKING;
			}

			@Override
			public String[] dialogue() {
				return new String[] {
						"You can find the main stores here in Edgeville.",
						"You can find other stores around the world of Trident.",
						"For example: you can buy skilling items from masters",
						"found at their proper locations."

				};
			}

			@Override
			public void specialAction() {
				player.getAttributes().getTutorialFinished()[3] = true;
			}

			@Override
			public Dialogue nextDialogue() {
				return tutorialDialogues(player);
			}
		};
	}

	public static Dialogue finishedTutorial(final Player player) {
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
				return DialogueExpression.PLAIN_TALKING;
			}

			@Override
			public String[] dialogue() {
				return new String[] {
						"I've given you a Magical Orb.",
						"You can use it whenever you want to re-do",
						"this tutorial. If you need help with anything,",
						"feel free to ask other players and staff members.",

				};
			}

			@Override
			public void specialAction() {
				player.getInventory().add(6950, 1);
			}

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
						player.getPacketSender().sendInterface(3559);
						player.getAppearance().setCanChangeAppearance(true);
						if(player.getAttributes().hasStarted() && player.getAttributes().getTutorialFinished()[4] && !player.getAttributes().getTutorialFinished()[5]) {
							player.getPacketSender().sendMessage("You completed the tutorial and have been rewarded with 100.000 coins!");
							player.getInventory().add(995, 100000);
							player.getAttributes().getTutorialFinished()[5] = true;
						}
					}
				};
			}
		};
	}

	public static Dialogue confirmFinishTut(final Player player) {
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
				return DialogueExpression.PLAIN_TALKING;
			}

			@Override
			public String[] dialogue() {
				return new String[] {
						"You haven't finished the whole tutorial.",
						"Are you sure you wish to skip it?",
						"You will not receive any reward."

				};
			}

			@Override
			public void specialAction() {

			}

			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.OPTION;
					}

					@Override
					public DialogueExpression animation() {
						return DialogueExpression.NO_EXPRESSION;
					}

					@Override
					public String[] dialogue() {
						return new String[]{"Yes", "No"};
					}

					@Override
					public void specialAction() {
						player.getAttributes().setDialogueAction(390);
					}

				};
			}
		};
	}

	public static Dialogue pickReward(final Player player) {
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
				return DialogueExpression.TALK_SWING;
			}

			@Override
			public String[] dialogue() {
				return new String[] {
						""+player.getUsername()+", since you finished the tutorial,",
						"I'll lend you an item of your choice for 24 hours.",
						"Which item would you like to borrow?"
				};
			}

			@Override
			public void specialAction() {
				player.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.OPTION;
					}

					@Override
					public int npcId() {
						return 1;
					}

					@Override
					public DialogueExpression animation() {
						return DialogueExpression.TALK_SWING;
					}

					@Override
					public String[] dialogue() {
						return new String[] {
								"[Warrior] Abyssal Whip",
								"[Archer] Dark Bow",
								"[Mage] Staff of Light",
								"[Skiller] Inferno Adze",
								"None"
						};
					}

					@Override
					public void specialAction() {
						player.getAttributes().setDialogueAction(378);
					}
				};
			}
		};
	}
}
