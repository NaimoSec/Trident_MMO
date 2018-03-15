package org.trident.world.content.skills.impl.agility;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Flag;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.entity.player.Player;
/**
 * Messy as fuck, what ever
 * @author Gabbe
 */
public enum ObstacleData {

	/* GNOME COURSE */
	LOG(2295, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			player.moveTo(new Position(2474, 3436));
			TaskManager.submit(new Task(1, player, false) {
				int tick = 7;
				@Override
				public void execute() {
					tick--;
					player.getMovementQueue().walkStep(0, -1);
					if(tick <= 0)
						stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(0, true).setCrossingObstacle(false).setAnimation(-1);
					player.getSkillManager().addExperience(Skill.AGILITY, 60, false);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					if(wasRunning && !player.getAttributes().isRunning()) {
						player.getAttributes().setRunning(true);
						player.getPacketSender().sendRunStatus();
					}
				}
			});
		}
	},
	NET(2285, false) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.performAnimation(new Animation(828));
			TaskManager.submit(new Task(2, player, false) {
				@Override
				public void execute() {
					player.moveTo(new Position(2473, 3423, 1));
					player.getSkillManager().addExperience(Skill.AGILITY, 40, false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(1, true).setCrossingObstacle(false);
					stop();
				}
			});
		}
	},
	BRANCH(2313, false) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.performAnimation(new Animation(828));
			TaskManager.submit(new Task(2, player, false) {
				@Override
				public void execute() {
					player.moveTo(new Position(2473, 3420, 2));
					player.getSkillManager().addExperience(Skill.AGILITY, 42, false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(2, true).setCrossingObstacle(false);
					stop();
				}
			});
		}
	},
	ROPE(2312, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			TaskManager.submit(new Task(1, player, false) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(1, 0);
					if(tick >= 6)
						stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(3, true).setCrossingObstacle(false).setAnimation(-1);
					player.getSkillManager().addExperience(Skill.AGILITY, 25, false);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					if(wasRunning && !player.getAttributes().isRunning()) {
						player.getAttributes().setRunning(true);
						player.getPacketSender().sendRunStatus();
					}
				}
			});
		}
	},
	BRANCH_2(2314, false) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.performAnimation(new Animation(828));
			TaskManager.submit(new Task(2, player, false) {
				@Override
				public void execute() {
					player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY(), 0));
					player.getSkillManager().addExperience(Skill.AGILITY, 42, false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(4, true).setCrossingObstacle(false);
					stop();
				}
			});
		}
	},
	NETS_2(2286, false) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			if(player.getPosition().getY() != 3425) {
				player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false);
				return;
			}
			player.performAnimation(new Animation(828));
			TaskManager.submit(new Task(2, player, false) {
				@Override
				public void execute() {
					player.moveTo(new Position(player.getPosition().getX(), player.getPosition().getY() + 2, 0));
					player.getSkillManager().addExperience(Skill.AGILITY, 15, false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(5, true).setCrossingObstacle(false);
					stop();
				}
			});
		}
	},
	PIPE_1(4058, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.moveTo(new Position(2487, 3430));
			TaskManager.submit(new Task(1, player, false) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick < 3 || tick >= 4) {
						if(player.getSkillManager().getSkillAttributes().getAgilityAttributes().getAnimation() != 844) {
							player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(844);
							player.getUpdateFlag().flag(Flag.APPEARANCE);
						}
					} else {
						if(player.getSkillManager().getSkillAttributes().getAgilityAttributes().getAnimation() != -1) {
							player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(-1);
							player.getUpdateFlag().flag(Flag.APPEARANCE);
						}
					}
					player.getAttributes().setRunning(false);player.getPacketSender().sendRunStatus();
					tick++;
					player.getMovementQueue().walkStep(0, 1);
					if(tick >= 4)
						stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.moveTo(new Position(2487, 3437));
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(6, true).setCrossingObstacle(false).setAnimation(-1);
					player.getAttributes().setClickDelay(System.currentTimeMillis());
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					if(Agility.passedAllObstacles(player)) {
						DialogueManager.start(player, DialogueManager.getDialogues().get(95 + Misc.getRandom(2)));
						player.getInventory().add(2996, 2);
						player.getSkillManager().addExperience(Skill.AGILITY, 1200, false);
					} else {
						DialogueManager.start(player, DialogueManager.getDialogues().get(94));
					}
					Agility.reset(player);
					if(wasRunning && !player.getAttributes().isRunning()) {
						player.getAttributes().setRunning(true);
						player.getPacketSender().sendRunStatus();
					}
				}
			});
		}
	},
	PIPE_2(154, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.moveTo(new Position(2484, 3430));
			TaskManager.submit(new Task(1, player, false) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick < 3 || tick >= 4) {
						if(player.getSkillManager().getSkillAttributes().getAgilityAttributes().getAnimation() != 844) {
							player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(844);
							player.getUpdateFlag().flag(Flag.APPEARANCE);
						}
					} else {
						if(player.getSkillManager().getSkillAttributes().getAgilityAttributes().getAnimation() != -1) {
							player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(-1);
							player.getUpdateFlag().flag(Flag.APPEARANCE);
						}
					}
					player.getAttributes().setRunning(false);player.getPacketSender().sendRunStatus();
					tick++;
					player.getMovementQueue().walkStep(0, 1);
					if(tick >= 4)
						stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.moveTo(new Position(2483, 3437));
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(6, true).setCrossingObstacle(false).setAnimation(-1);
					player.getAttributes().setClickDelay(System.currentTimeMillis());
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					if(Agility.passedAllObstacles(player)) {
						DialogueManager.start(player, DialogueManager.getDialogues().get(95 + Misc.getRandom(2)));
						player.getInventory().add(2996, 2);
						player.getSkillManager().addExperience(Skill.AGILITY, 1200, false);
					} else {
						DialogueManager.start(player, DialogueManager.getDialogues().get(94));
					}
					Agility.reset(player);
					if(wasRunning && !player.getAttributes().isRunning()) {
						player.getAttributes().setRunning(true);
						player.getPacketSender().sendRunStatus();
					}
				}
			});
		}

	},



	/* BARBARIAN OUTPOST COURSE */
	ROPE_SWING(2282, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			Agility.reset(player);
			boolean infront = player.getPosition().getY() == 3554 && player.getPosition().getX() >= 2549 && player.getPosition().getX() <= 2553;
			if(!infront) {
				player.getPacketSender().sendMessage("You must be positioned infront of the Ropeswing to do that.");
				if(wasRunning && !player.getAttributes().isRunning()) {
					player.getAttributes().setRunning(true);
					player.getPacketSender().sendRunStatus();
				}
				player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false);
				return;
			}
			if(player.getPosition().getX() == 2553)
				player.moveTo(new Position(2552, 3554));
			if(player.getPosition().getX() == 2550)
				player.moveTo(new Position(2551, 3554));
			player.performAnimation(new Animation(751));
			final boolean success = Agility.isSucessive(player);
			TaskManager.submit(new Task(1, player, false) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					if(tick == 1)
						player.moveTo(new Position(player.getPosition().getX(), 3553, 0));
					if(!success) {
						player.moveTo(new Position(2550, 9950, 0));
						player.getSkillManager().addExperience(Skill.AGILITY, 6 * 3, false);
						player.setDamage(new Damage(new Hit(Misc.getRandom(50), CombatIcon.BLOCK, Hitmask.RED)));
						stop();
						return;
					}
					if(tick >= 3) {
						player.moveTo(new Position(player.getPosition().getX(), 3549, 0));
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(0, success ? true : false).setCrossingObstacle(false);
					player.getSkillManager().addExperience(Skill.AGILITY, 75 * 3, false);
					if(wasRunning && !player.getAttributes().isRunning()) {
						player.getAttributes().setRunning(true);
						player.getPacketSender().sendRunStatus();
					}
				}
			});
		}
	},
	LOG_2(2294, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			final boolean fail = !Agility.isSucessive(player);
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(762);
			player.moveTo(new Position(2550, 3546, 0));
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(-1, 0);
					if(tick >= 9 || player == null)
						stop();
					if(tick == 5 && fail) {
						stop();
						tick = 0;
						player.getMovementQueue().stopMovement();
						player.performAnimation(new Animation(764));
						TaskManager.submit(new Task(1, player, true) {
							int tick2 = 0;
							public void execute() {
								if(tick2 == 0) {
									player.moveTo(new Position(2546, 3547));
									player.setDamage(new Damage(new Hit(Misc.getRandom(50), CombatIcon.BLOCK, Hitmask.RED)));
								}
								tick2++;
								player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(772);
								player.getUpdateFlag().flag(Flag.APPEARANCE);
								player.getAttributes().setRunning(false);
								player.getMovementQueue().walkStep(0, 1);
								if(tick2 >= 3) {
									player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(1, false).setCrossingObstacle(false).setAnimation(-1);;
									player.getSkillManager().addExperience(Skill.AGILITY, 5, false);
									player.getUpdateFlag().flag(Flag.APPEARANCE);
									if(wasRunning && !player.getAttributes().isRunning()) {
										player.getAttributes().setRunning(wasRunning);
										player.getPacketSender().sendRunStatus();
									}
									stop();
									return;
								}
							}
						});
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					if(!fail) {
						player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(1, true).setCrossingObstacle(false).setAnimation(-1);
						player.getSkillManager().addExperience(Skill.AGILITY, fail ? 5 * 3 : 60 * 3, false);
						player.getUpdateFlag().flag(Flag.APPEARANCE);
						if(wasRunning && !player.getAttributes().isRunning()) {
							player.getAttributes().setRunning(wasRunning);
							player.getPacketSender().sendRunStatus();
						}
					}
				}
			});
		}
	},
	NET_3(2284, false) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.performAnimation(new Animation(828));
			TaskManager.submit(new Task(2, player, false) {
				@Override
				public void execute() {
					player.moveTo(new Position(2537 + Misc.getRandom(1), 3546 + Misc.getRandom(1), 1));
					player.getSkillManager().addExperience(Skill.AGILITY, 30 * 3, false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(2, true).setAnimation(-1).setCrossingObstacle(false);
					stop();
				}
			});
		}
	},
	BALANCE_LEDGE(2302, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			if(player.getPosition().getX() != 2536) {
				if(wasRunning && !player.getAttributes().isRunning()) {
					player.getAttributes().setRunning(true);
					player.getPacketSender().sendRunStatus();
				}
				player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false);
				return;
			}
			player.getAttributes().setResetPosition(player.getPosition().copy());
			final boolean fallDown = !Agility.isSucessive(player);
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(true);
			player.moveTo(new Position(2536, 3547, 1));
			TaskManager.submit(new Task(1, player, false) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(756);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					player.getMovementQueue().walkStep(-1, 0);
					if(tick == 3 && fallDown) {
						player.performAnimation(new Animation(761));
						stop();
						TaskManager.submit(new Task(1) {
							@Override
							public void execute() {
								player.moveTo(new Position(2535, 3546, 0));
								player.setDamage(new Damage(new Hit(Misc.getRandom(50), CombatIcon.BLOCK, Hitmask.RED)));
								player.getMovementQueue().walkStep(0, -1);
								player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(3, false).setAnimation(-1);
								player.getUpdateFlag().flag(Flag.APPEARANCE);
								player.getSkillManager().addExperience(Skill.AGILITY, 6 * 3, false);
								TaskManager.submit(new Task(1) {
									@Override
									public void execute() {
										player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false);
										stop();
									}
								});
								stop();
							}
						});
					} else if(tick == 4) {
						player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(3, true).setAnimation(-1).setCrossingObstacle(false);
						player.getUpdateFlag().flag(Flag.APPEARANCE);
						player.getSkillManager().addExperience(Skill.AGILITY, 40 * 3, false);
						player.getAttributes().setResetPosition(null);
						stop();
					}
				}
			});
		}
	},
	LADDER(3205, false) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.performAnimation(new Animation(827));
			TaskManager.submit(new Task(1, player, false) {
				@Override
				public void execute() {
					player.moveTo(new Position(2532, 3546, 0));
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(4, true).setCrossingObstacle(false);
					stop();
				}
			});
		}
	},
	RAMP(1948, false) {

		@Override
		public void cross(final Player player, final boolean wasRunning) {
			if(player.getPosition().getX() != 2535 && player.getPosition().getX() != 2538 && player.getPosition().getX() != 2542 && player.getPosition().getX() != 2541) {
				if(wasRunning && !player.getAttributes().isRunning()) {
					player.getAttributes().setRunning(true);
					player.getPacketSender().sendRunStatus();
				}
				player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false);
				return;
			}
			final boolean first = player.getPosition().getX() == 2535;
			final boolean oneStep = player.getPosition().getX() == 2537 || player.getPosition().getX() == 2542;
			player.setPositionToFace(player.getAttributes().getCurrentInteractingObject().getPosition().copy());
			player.performAnimation(new Animation(1115));
			TaskManager.submit(new Task(1, player, false) {
				@Override
				public void execute() {
					player.getPacketSender().sendClientRightClickRemoval();
					player.moveTo(new Position(player.getPosition().getX() + (oneStep ? 1 : 2), 3553));
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false).setCrossedObstacle(first ? 5 : 6, true);
					if(!first) {
						if(Agility.passedAllObstacles(player)) {
							DialogueManager.start(player, 95);
							player.getInventory().add(2996, 4);
							player.getSkillManager().addExperience(Skill.AGILITY, 2000, false);
						} else {
							DialogueManager.start(player, 94);
						}
						Agility.reset(player);
						player.getSkillManager().addExperience(Skill.AGILITY, 50 * 3, false);
					}
					stop();
				}
			});
		}
	},
	ROPESWING_UNDERGROUND(1755, false) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.performAnimation(new Animation(828));
			TaskManager.submit(new Task(1) {
				@Override
				public void execute() {
					if(player.getLocation() != null && player.getLocation() == Location.WILDERNESS)
						player.moveTo(new Position(3005, 3962, 0));
					else {
						if(player.getPosition().getY() < 9630) //Lumbridge kitchen
							player.moveTo(new Position(3208, 3216, 0));
						else
							player.moveTo(new Position(2546, 3551, 0));
					}
					player.setEntityInteraction(null);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false);
					stop();
				}
			});
		}
	},
	ROPESWING_LADDER(1759, false) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.performAnimation(new Animation(827));
			TaskManager.submit(new Task(1) {
				@Override
				public void execute() {
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false);
					player.moveTo(new Position(player.getPosition().getX() > 2610 ? 2209: 2546, player.getPosition().getY() < 3550 ? 5348: 9951, 0));
					stop();
				}
			});
		}
	},

	/* WILD COURSE */

	GATE_1(2309, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.moveTo(new Position(2998, 3917));
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(0, 1);
					if(player.getPosition().getY() == 3930 || tick >= 15) {
						player.moveTo(new Position(2998, 3931, 0));
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false).setAnimation(-1);
					player.getSkillManager().addExperience(Skill.AGILITY, 15, false);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					Agility.reset(player);
					if(wasRunning && !player.getAttributes().isRunning()) {
						player.getAttributes().setRunning(true);
						player.getPacketSender().sendRunStatus();
					}
				}
			});
		}
	},
	GATE_2(2308, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.moveTo(new Position(2998, 3930));
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(0, -1);
					if(player.getPosition().getY() == 3917 || tick >= 15) {
						player.moveTo(new Position(2998, 3916));
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false).setAnimation(-1);
					player.getSkillManager().addExperience(Skill.AGILITY, 15, false);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					Agility.reset(player);
					if(wasRunning && !player.getAttributes().isRunning()) {
						player.getAttributes().setRunning(true);
						player.getPacketSender().sendRunStatus();
					}
				}
			});
		}
	},
	GATE_3(2308, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.moveTo(new Position(2998, 3930));
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(0, -1);
					if(player.getPosition().getY() == 3917 || tick >= 15) {
						player.moveTo(new Position(2998, 3916));
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false).setAnimation(-1);
					player.getSkillManager().addExperience(Skill.AGILITY, 15, false);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					Agility.reset(player);
					if(wasRunning && !player.getAttributes().isRunning()) {
						player.getAttributes().setRunning(true);
						player.getPacketSender().sendRunStatus();
					}
				}
			});
		}
	},
	PIPE_3(2288, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			if(player.getPosition().getY() != 3937) {
				if(wasRunning && !player.getAttributes().isRunning()) {
					player.getAttributes().setRunning(true);
					player.getPacketSender().sendRunStatus();
				}
				player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false);
				player.getPacketSender().sendMessage("You must be positioned infront of the pipe to do this.");
				return;
			}
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(844);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			player.getAttributes().setResetPosition(player.getPosition().copy());
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(0, 1);
					if(tick == 4)
						player.moveTo(new Position(3004, 3947));
					else if (tick == 7)
						stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(0, true).setCrossedObstacle(1, true).setCrossedObstacle(2, true).setCrossingObstacle(false).setAnimation(-1);
					player.getSkillManager().addExperience(Skill.AGILITY, 175, false);
					player.getAttributes().setRunning(wasRunning);player.getPacketSender().sendRunStatus();
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					if(wasRunning && !player.getAttributes().isRunning()) {
						player.getAttributes().setRunning(true);
						player.getPacketSender().sendRunStatus();
					}
					player.getAttributes().setResetPosition(null);
				}
			});
		}
	},
	ROPE_SWING_2(2283, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			if(player.getPosition().getY() != 3953) {
				player.getPacketSender().sendMessage("You must be positioned infront of the Ropeswing to do that.");
				if(wasRunning && !player.getAttributes().isRunning()) {
					player.getAttributes().setRunning(true);
					player.getPacketSender().sendRunStatus();
				}
				player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false);
				return;
			}
			if(player.getPosition().getX() == 3006 || player.getPosition().getX() == 3004)
				if(player.getPosition().getY() == 3953)
					player.moveTo(new Position(3005, 3953));
			player.performAnimation(new Animation(751));
			if(player.getAttributes().getCurrentInteractingObject() != null)
				player.getAttributes().getCurrentInteractingObject().performAnimation(new Animation(497));
			player.setPositionToFace(new Position(3005, 3960, 0));
			final boolean fail = !Agility.isSucessive(player);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					if(tick == 1) {
						if(fail) {
							player.moveTo(new Position(3004, 10356));
							player.setDamage(new Damage(new Hit(Misc.getRandom(60), CombatIcon.BLOCK, Hitmask.RED)));
							player.getSkillManager().addExperience(Skill.AGILITY, 40, false);
							stop();
							return;
						} else {
							player.setPositionToFace(new Position(3005, 3960, 0));
							player.moveTo(new Position(player.getPosition().getX(), 3954, 0));
						}
					}
					if(tick >= 3) {
						player.moveTo(new Position(player.getPosition().getX(), 3958, 0));
						player.setPositionToFace(new Position(3005, 3960, 0));
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(3, fail ? false : true).setCrossingObstacle(false);
					if(wasRunning && !player.getAttributes().isRunning()) {
						player.getAttributes().setRunning(true);
						player.getPacketSender().sendRunStatus();
					}
				}
			});
		}
	},
	STEPPING_STONES(9326, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.getAttributes().setRunning(false);player.getPacketSender().sendRunStatus();
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(769);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 1;
				@Override
				public void execute() {
					tick++;
					if(tick == 2)
						player.getMovementQueue().walkStep(-1, 0);
					else if(tick == 4)
						player.getMovementQueue().walkStep(-1, 0);
					else if(tick == 6)
						player.getMovementQueue().walkStep(-1, 0);
					else if(tick == 8)
						player.getMovementQueue().walkStep(-1, 0);
					else if(tick == 10)
						player.getMovementQueue().walkStep(-1, 0);
					if(tick >= 12) {
						player.moveTo(new Position(2996, 3960, 0));
						player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(-1);
						player.getUpdateFlag().flag(Flag.APPEARANCE);
						player.getSkillManager().addExperience(Skill.AGILITY, 250, false);
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.getAttributes().setResetPosition(null);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(4, true).setCrossingObstacle(false);
					player.getSkillManager().addExperience(Skill.AGILITY, 100, false);
					if(wasRunning && !player.getAttributes().isRunning()) {
						player.getAttributes().setRunning(true);
						player.getPacketSender().sendRunStatus();
					}
				}
			});
		}
	},
	BALANCE_LEDGE_2(2297, true) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.moveTo(new Position(3001, 3945, 0));
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			final boolean fail = !Agility.isSucessive(player);
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					tick++;
					player.getMovementQueue().walkStep(-1, 0);
					if(tick >= 7)
						stop();
					else if(fail && tick >= 3) {
						player.moveTo(new Position(3000, 10346));
						player.setDamage(new Damage(new Hit(Misc.getRandom(60), CombatIcon.BLOCK, Hitmask.RED)));
						stop();
					}
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(5, fail ? false : true).setCrossingObstacle(false).setAnimation(-1);
					player.getSkillManager().addExperience(Skill.AGILITY, fail ? 10 : 250, false);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					if(wasRunning && !player.getAttributes().isRunning()) {
						player.getAttributes().setRunning(true);
						player.getPacketSender().sendRunStatus();
					}
				}
			});
		}
	},
	CLIMB_WALL(2994, false) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.performAnimation(new Animation(828));
			TaskManager.submit(new Task(3, player, true) {
				@Override
	        	public void execute() {
					player.getPacketSender().sendClientRightClickRemoval();
					player.moveTo(new Position(2996, 3933, 0));
					stop();
				}
				@Override
				public void stop() {
					setEventRunning(false);
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossedObstacle(6, true).setCrossingObstacle(false);
					player.getSkillManager().addExperience(Skill.AGILITY, 100, false);
					if(Agility.passedAllObstacles(player)) {
						DialogueManager.start(player, 95);
						player.getInventory().add(2996, 6);
						player.getSkillManager().addExperience(Skill.AGILITY, 8157, false);
					} else {
						DialogueManager.start(player, 94);
					}
					Agility.reset(player);
					player.getSkillManager().addExperience(Skill.AGILITY, 300, false);
					player.getAttributes().setRunning(wasRunning);player.getPacketSender().sendRunStatus();
				}
			});
		}
	},
	LADDER_2(14758, false) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.performAnimation(new Animation(827));
			player.setEntityInteraction(null);
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false);
			TaskManager.submit(new Task(1) {
				@Override
				public void execute() {
					player.moveTo(new Position(3005, 10362, 0));
					stop();
				}
			});
		}
	},
	
	/**MISC**/
	RED_DRAGON_LOG_BALANCE(5088, false) {
		@Override
		public void cross(final Player player, final boolean wasRunning) {
			player.getAttributes().setResetPosition(player.getPosition().copy());
			player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(true).setAnimation(762);
			player.getUpdateFlag().flag(Flag.APPEARANCE);
			final int moveX = player.getPosition().getX() > 2683 ? 2686 : 2683;
			player.moveTo(new Position(moveX, 9506));
			TaskManager.submit(new Task(1, player, true) {
				int tick = 0;
				@Override
				public void execute() {
					if(tick < 4)
						player.getMovementQueue().walkStep(moveX == 2683 ? +1 : -1, 0);
					else if(tick == 4) {
						player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(-1).setCrossingObstacle(false);
						player.getUpdateFlag().flag(Flag.APPEARANCE);
						player.getSkillManager().addExperience(Skill.AGILITY, 32, false);
						player.getAttributes().setResetPosition(null);
						stop();
					}
					tick++;
				}
			});
		}
	},
	;

	ObstacleData(int object, boolean mustWalk) {
		this.object = object;
		this.mustWalk = mustWalk;
	}

	private int object;
	private boolean mustWalk;

	public int getObject() {
		return object;
	}

	public boolean mustWalk() {
		return mustWalk;
	}

	public void cross(final Player player, final boolean wasRunning) {

	}

	public static ObstacleData forId(int object) {
		if(object == 2993 || object == 2328 || object == 2995 || object == 2994)
			return CLIMB_WALL;
		else if(object == 2307)
			return GATE_2;
		else if(object == 5088 || object == 5090)
			return RED_DRAGON_LOG_BALANCE;
		for(ObstacleData obstacleData : ObstacleData.values()) {
			if(obstacleData.getObject() == object)
				return obstacleData;
		}
		return null;
	}
}