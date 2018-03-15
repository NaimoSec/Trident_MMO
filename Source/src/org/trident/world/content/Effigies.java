package org.trident.world.content;

import org.trident.model.Animation;
import org.trident.model.Skill;
import org.trident.model.definitions.ItemDefinition;
import org.trident.util.Misc;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.entity.player.Player;

public class Effigies {

	public static void handleEffigy(Player player, int effigy) {
		if(player == null)
			return;
		if(player.getAttributes().getInterfaceId() > 0) {
			player.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			return;
		} else
			DialogueManager.start(player, cleanEffigy(player, effigy));
	}

	public static Dialogue cleanEffigy(final Player player, final int effigy) {
		return new Dialogue() {
			final String name = Misc.formatText(ItemDefinition.forId(effigy).getName());

			@Override
			public DialogueType type() {
				return DialogueType.ITEM_STATEMENT;
			}

			@Override
			public DialogueExpression animation() {
				return null;
			}

			@Override
			public String[] dialogue() {
				return new String[] {
						"You clean off the dust off of the Ancient effigy.",
						"The relic begins to make some sort of weird noises.",
						"I think there may be something inside here."
				};
			}

			@Override
			public String[] item() {
				return new String[] {
						""+effigy+"",
						"180",
						""+name+""
				};
			}

			@Override
			public Dialogue nextDialogue() {
				return new Dialogue() {

					@Override
					public DialogueType type() {
						return DialogueType.ITEM_STATEMENT;
					}

					@Override
					public DialogueExpression animation() {
						return null;
					}

					@Override
					public String[] dialogue() {
						switch(effigy) {
						case 18778:
							return new String[] {"This will require at least a level of 91 in one of the two skills", "to investigate. After investigation it becomes nourished,", "rewarding 15,000 experience in the skill used."};
						case 18779:
							return new String[] {"This will require at least a level of 93 in one of the two skills", "to investigate. After investigation it becomes sated,", "rewarding 30,000 experience in the skill used."};
						case 18780:
							return new String[] {"This will require at least a level of 95 in one of the two skills", "to investigate. After investigation it becomes gordged,", "rewarding 45,000 experience in the skill used."};
						case 18781:
							return new String[] {"This will require at least a level of 97 in one of the two skills", "to investigate. After investigation it provides 60,000 ", "experience in the skill used and, then crumbles to dust,", "leaving behind a dragonkin lamp."};
						}
						return new String[1];
					}

					@Override
					public String[] item() {
						return new String[] {
								""+effigy+"",
								"180",
								""+name+""
						};
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
								String[] pairs = new String[2];
								int r = 1 + Misc.getRandom(6);
								boolean newEffigy = player.getAttributes().getEffigy() == 0;
								if(!newEffigy)
									r = player.getAttributes().getEffigy();
								else
									player.getAttributes().setEffigy(r);
								switch(r) {
								case 1:
									pairs = new String[] {"Crafting", "Agility"};
									player.getAttributes().setDialogueAction(435);
									break;
								case 2:
									pairs = new String[] {"Runecrafting", "Thieving"};
									player.getAttributes().setDialogueAction(436);
									break;
								case 3:
									pairs = new String[] {"Cooking", "Firemaking"};
									player.getAttributes().setDialogueAction(437);
									break;
								case 4:
									pairs = new String[] {"Farming", "Fishing"};
									player.getAttributes().setDialogueAction(438);
									break;
								case 5:
									pairs = new String[] {"Fletching", "Woodcutting"};
									player.getAttributes().setDialogueAction(439);
									break;
								case 6:
									pairs = new String[] {"Herblore", "Prayer"};
									player.getAttributes().setDialogueAction(440);
									break;
								case 7:
									pairs = new String[] {"Smithing", "Mining"};
									player.getAttributes().setDialogueAction(441);
									break;
								}
								return pairs;
							}

							@Override
							public String[] item() {
								return new String[] {
										""+effigy+"",
										"180",
										""+name+""
								};
							}
						};
					}
				};
			}
		};
	}

	public static boolean handleEffigyAction(Player player, int action) {
		if(player.getAttributes().getCurrentInteractingItem() == null || player.getAttributes().getCurrentInteractingItem() != null && !isEffigy(player.getAttributes().getCurrentInteractingItem().getId())) {
			return false;
		}
		switch(action) {
		case 2461:
			if (player.getAttributes().getDialogueAction() == 435) {
				openEffigy(player, 12);
				return true;
			} else if (player.getAttributes().getDialogueAction() == 436) {
				openEffigy(player, 20);
				return true;
			} else if (player.getAttributes().getDialogueAction() == 437) {
				openEffigy(player, 7);
				return true;
			} else if (player.getAttributes().getDialogueAction() == 438) {
				openEffigy(player, 19);
				return true;
			} else if (player.getAttributes().getDialogueAction() == 439) {
				openEffigy(player, 9);
				return true;
			} else if (player.getAttributes().getDialogueAction() == 440) {
				openEffigy(player, 15);
				return true;
			} else if (player.getAttributes().getDialogueAction() == 441) {
				openEffigy(player, 13);
				return true;
			}
			break;
		case 2462:
			if (player.getAttributes().getDialogueAction() == 435) {
				openEffigy(player, 16);
				return true;
			} else if (player.getAttributes().getDialogueAction() == 436) {
				openEffigy(player, 17);
				return true;
			} else if (player.getAttributes().getDialogueAction() == 437) {
				openEffigy(player, 11);
				return true;
			} else if (player.getAttributes().getDialogueAction() == 438) {
				openEffigy(player, 10);
				return true;
			} else if (player.getAttributes().getDialogueAction() == 439) {
				openEffigy(player, 8);
				return true;
			} else if (player.getAttributes().getDialogueAction() == 440) {
				openEffigy(player, 5);
				return true;
			} else if (player.getAttributes().getDialogueAction() == 441) {
				openEffigy(player, 14);
				return true;
			}
			break;
		}
		return false;
	}

	public static void openEffigy(Player player, int skillId) {
		int[] levelReq = {91, 93, 95, 97};
		if(player.getAttributes().getCurrentInteractingItem() == null)
			return;
		if (System.currentTimeMillis() - player.getAttributes().getClickDelay() < 4000) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		if(player.getAttributes().getCurrentInteractingItem().getId() == 18778 && player.getSkillManager().getCurrentLevel(Skill.forId(skillId)) >= levelReq[0]) {
			if(player.getInventory().contains(18778)) {
				player.getInventory().delete(18778, 1);
				player.getInventory().add(18779, 1);	
				player.getSkillManager().addExperience(Skill.forId(skillId), 15000, true);
				player.getAttributes().setClickDelay(System.currentTimeMillis());
				player.performAnimation(new Animation(7368));
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setEffigy(0);
				return;
			}
		}
		if (player.getAttributes().getCurrentInteractingItem().getId() == 18779 && player.getSkillManager().getCurrentLevel(Skill.forId(skillId)) >= levelReq[1]) {
			if(player.getInventory().contains(18779)) {
				player.getInventory().delete(18779, 1);
				player.getInventory().add(18780, 1);
				player.getSkillManager().addExperience(Skill.forId(skillId), 30000, true);
				player.getAttributes().setClickDelay(System.currentTimeMillis());
				player.performAnimation(new Animation(7368));
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setEffigy(0);
				return;
			}
		}
		if (player.getAttributes().getCurrentInteractingItem().getId() == 18780 && player.getSkillManager().getCurrentLevel(Skill.forId(skillId)) >= levelReq[2]) {
			if(player.getInventory().contains(18780)) {
				player.getInventory().delete(18780, 1);
				player.getInventory().add(18781, 1);
				player.getSkillManager().addExperience(Skill.forId(skillId), 45000, true);
				player.getAttributes().setClickDelay(System.currentTimeMillis());
				player.performAnimation(new Animation(7368));
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setEffigy(0);
				return;
			}
		}
		if (player.getAttributes().getCurrentInteractingItem().getId() == 18781 && player.getSkillManager().getCurrentLevel(Skill.forId(skillId)) >= levelReq[3]) {
			if(player.getInventory().contains(18781)) {
				player.getInventory().delete(18781, 1);
				player.getInventory().add(18782, 1);
				player.getSkillManager().addExperience(Skill.forId(skillId), 60000, true);
				player.getAttributes().setClickDelay(System.currentTimeMillis());
				player.performAnimation(new Animation(7368));
				player.getPacketSender().sendInterfaceRemoval();
				player.getAttributes().setEffigy(0);
				return;
			}
		}
	}

	public static boolean isEffigy(int item) {
		return item >= 18778 && item <= 18781;
	}
}
