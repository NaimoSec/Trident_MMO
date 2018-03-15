package org.trident.world.content.skills.impl.hunter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.HunterTrapsTask;
import org.trident.engine.task.impl.NPCRespawnTask;
import org.trident.model.Animation;
import org.trident.model.GameObject;
import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.model.definitions.NPCSpawns;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.Locations;
import org.trident.world.content.skills.impl.firemaking.Firemaking;
import org.trident.world.content.skills.impl.hunter.Trap.TrapState;
import org.trident.world.content.skills.impl.hunter.traps.BoxTrap;
import org.trident.world.content.skills.impl.hunter.traps.SnareTrap;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCAttributes;
import org.trident.world.entity.player.Player;

public class Hunter {

	/**
	 * Spawns all implings onto the Puro Puro map
	 */
	public static void spawnImplings() {
		int[][] implings = {
				/**
				 * Baby imps
				 */
				{6055, 2612, 4318},
				{6055, 2602, 4314},
				{6055, 2610, 4338},
				{6055, 2582, 4344},
				{6055, 2578, 4344},
				{6055, 2568, 4311},
				{6055, 2583, 4295},
				{6055, 2582, 4330},
				{6055, 2600, 4303},
				{6055, 2611, 4301},
				{6055, 2618, 4329},

				/**
				 * Young imps
				 */
				{6056, 2591, 4332},
				{6056, 2600, 4338},
				{6056, 2595, 4345},
				{6056, 2610, 4327},
				{6056, 2617, 4314},
				{6056, 2619, 4294},
				{6056, 2599, 4294},
				{6056, 2575, 4303},
				{6056, 2570, 4299},

				/**
				 * Gourment imps
				 */
				{6057, 2573, 4339},
				{6057, 2567, 4328},
				{6057, 2593, 4297},
				{6057, 2618, 4305},
				{6057, 2605, 4316},
				{6057, 2596, 4333},

				/**
				 * Earth imps
				 */
				{6058, 2592, 4338},
				{6058, 2611, 4345},
				{6058, 2617, 4339},
				{6058, 2614, 4301},
				{6058, 2606, 4295},
				{6058, 2581, 4299},

				/**
				 * Essence imps
				 */
				{6059, 2602, 4328},
				{6059, 2608, 4333},
				{6059, 2609, 4296},
				{6059, 2581, 4304},
				{6059, 2570, 4318},

				/**
				 * Eclectic imps
				 */
				{6060, 2611, 4310},
				{6060, 2617, 4319},
				{6060, 2600, 4347},
				{6060, 2570, 4326},
				{6060, 2579, 4310},

				/**
				 * Spirit imps
				 */

				/**
				 * Nature imps
				 */
				{6061, 2581, 4310},
				{6061, 2581, 4310},
				{6061, 2603, 4333},
				{6061, 2576, 4335},
				{6061, 2588, 4345},

				/**
				 * Magpie imps
				 */
				{6062, 2612, 4324},
				{6062, 2602, 4323},
				{6062, 2587, 4348},
				{6062, 2564, 4320},
				{6062, 2566, 4295},

				/**
				 * Ninja imps
				 */
				{6063, 2570, 4347},
				{6063, 2572, 4327},
				{6063, 2578, 4318},
				{6063, 2610, 4312},
				{6063, 2594, 4341},

				/**
				 * Dragon imps
				 */
				{6064, 2613, 4341},
				{6064, 2585, 4337},
				{6064, 2576, 4319},
				{6064, 2576, 4294},
				{6064, 2592, 4305},

		};

		NPCAttributes attributes;
		for(int i = 0; i < implings.length; i++) {
			attributes = new NPCAttributes().setAttackable(false).setAggressive(false).setRespawn(true).setWalkingDistance(4);
			World.register(NPCSpawns.createNPC(implings[i][0], new Position(implings[i][1], implings[i][2]), attributes, attributes.copy()));
		}

		/**
		 * Kingly imps
		 * Randomly spawned
		 */
		int random = Misc.getRandom(6);
		Position pos = new Position(2596, 4351);
		switch(random) {
		case 1:
			pos = new Position(2620, 4348);
			break;
		case 2:
			pos = new Position(2607, 4321);
			break;
		case 3:
			pos = new Position(2588, 4289);
			break;
		case 4:
			pos = new Position(2576, 4305);
			break;
		}
		attributes = new NPCAttributes().setAttackable(false).setAggressive(false).setRespawn(true).setWalkingDistance(4);
		World.register(NPCSpawns.createNPC(7903, pos, attributes, attributes.copy()));

	}

	/**
	 * Catches an Impling
	 * @param player	The player catching an Imp
	 * @param npc	The NPC (Impling) to catch
	 */
	public static void catchImpling(Player player, final NPC imp) {
		ImpData implingData = ImpData.forId(imp.getId());
		if(player.getAttributes().getInterfaceId() > 0 || player == null || imp == null || implingData == null || imp.getConstitution() <= 0 || imp.getAttributes().isDying() || System.currentTimeMillis() - player.getAttributes().getClickDelay() <= 2000)
			return;
		if(player.getSkillManager().getCurrentLevel(Skill.HUNTER) < implingData.levelReq) {
			player.getPacketSender().sendMessage("You need a Hunter level of at least "+implingData.levelReq+" to catch this impling.");
			return;
		}
		if(!player.getInventory().contains(10010) && !player.getEquipment().contains(10010)) {
			player.getPacketSender().sendMessage("You do not have any net to catch this impling with.");
			return;
		}
		if(!player.getInventory().contains(11260)) {
			player.getPacketSender().sendMessage("You do not have any empty jars to hold this impling with.");
			return;
		}
		player.performAnimation(new Animation(6605));
		boolean sucess = player.getSkillManager().getCurrentLevel(Skill.HUNTER) > 8 ? Misc.getRandom(player.getSkillManager().getCurrentLevel(Skill.HUNTER) / 2) > 1 : true;
		if(sucess) {
			if(!imp.getAttributes().isDying()) {
				World.deregister(imp);
				TaskManager.submit(new NPCRespawnTask(imp, imp.getDefinition().getRespawnTime()));
				player.getInventory().delete(11260, 1).add(implingData.impJar, 1);
				player.getPacketSender().sendMessage("You successfully catch the impling.");
				player.getSkillManager().addExperience(Skill.HUNTER, implingData.XPReward, false);
			}
		} else
			player.getPacketSender().sendMessage("You failed to catch the impling.");
		player.getAttributes().setClickDelay(System.currentTimeMillis());
	}
	/**
	 * Handles pushing through walls in Puro puro
	 * @param player	The player pushing a wall
	 */
	public static void pushThroughWall(final Player player) {
		if(player == null)
			return;
		int x = player.getPosition().getX();
		int y = player.getPosition().getY();

		/**
		 * Wall 1 - West
		 */
		if(x == 2584) {
			player.moveTo(new Position(2582, y));
		} else if(x == 2582) {
			player.moveTo(new Position(2584, y));
		}

		/**
		 * Wall 2 - South
		 */
		if(y== 4312) {
			player.moveTo(new Position(x, 4310));
		} else if(y == 4310) {
			player.moveTo(new Position(x, 4312));
		}

		/**
		 * Wall 3 - East
		 */
		if(x == 2599) {
			player.moveTo(new Position(2601, y));
		} else if(x == 2601) {
			player.moveTo(new Position(2599, y));
		}

		/**
		 * Wall 4 - North
		 */
		if(y == 4327) {
			player.moveTo(new Position(x, 4329));
		} else if(y == 4329) {
			player.moveTo(new Position(x, 4327));
		}
	}

	/**
	 * Handles Impling Jars looting
	 * @param player	The player looting the jar
	 * @param itemId	The jar the player is looting
	 */
	public static void lootJar(final Player player, Item jar, JarData jarData) {
		if(player == null || jar == null || jarData == null || System.currentTimeMillis() - player.getAttributes().getClickDelay() <= 2000)
			return;
		if(player.getInventory().getFreeSlots() < 2) {
			player.getPacketSender().sendMessage("You need at least 2 free inventory space to loot this.");
			return;
		}
		player.getInventory().delete(jar);
		player.getInventory().add(11260, 1);
		int randomCommonItem = Misc.getRandom(JarData.getLootRarity(jarData, 0));
		int randomUncommonItem = JarData.getLootRarity(jarData, 0)+Misc.getRandom(JarData.getLootRarity(jarData, 1));
		int randomRareItem = JarData.getLootRarity(jarData, 2);
		int randomVeryRareItem = JarData.getLootRarity(jarData, 3);
		Item reward = null;
		switch(JarData.getRar()) {
		case 0:
			reward = jarData.loot[randomCommonItem];
			if(reward != null)
				player.getInventory().add(reward);
			break;
		case 1:
			reward = jarData.loot[randomUncommonItem];
			if(reward != null)
				player.getInventory().add(reward);
			break;
		case 2:
			reward = jarData.loot[randomRareItem];
			if(reward != null)
				player.getInventory().add(reward);
			break;
		case 3:
			reward = jarData.loot[randomVeryRareItem];
			if(reward != null)
				player.getInventory().add(reward);
			break;
		}
		String rewardName = reward.getDefinition().getName();
		String s = Misc.anOrA(rewardName);
		if(reward.getAmount() > 1) {
			s = ""+reward.getAmount()+"";
			if(!rewardName.endsWith("s")) {
				if(rewardName.contains("potion")) {
					String l = rewardName.substring(0, rewardName.indexOf(" potion"));
					String l2 = rewardName.substring(rewardName.indexOf("potion"), 8);
					l2 += rewardName.contains("(3)") ? "(3)" : "(4)";
					rewardName = ""+l+" potions "+l2+"";
				} else
					rewardName = rewardName + "s";
			}
		}
		player.getPacketSender().sendMessage("You loot the "+jar.getDefinition().getName()+" and find "+s+" "+rewardName+".");
		player.getAttributes().setClickDelay(System.currentTimeMillis());
	}

	/*
	 * Other stuff like traps etc
	 */

	/**
	 * Registers a new Trap
	 * 
	 * @param trap
	 */
	public static void register(final Trap trap) {
		CustomObjects.spawnGlobalObject(trap.getGameObject());
		traps.add(trap);
		if (trap.getOwner() != null)
			trap.getOwner().getSkillManager().getSkillAttributes().getHunterAttributes().setTrapsLaid(trap.getOwner().getSkillManager().getSkillAttributes().getHunterAttributes().getTrapsLaid() + 1);
	}

	/**
	 * Unregisters a trap
	 * 
	 * @param trap
	 */
	public static void deregister(Trap trap) {
		CustomObjects.deleteGlobalObject(trap.getGameObject());
		traps.remove(trap); // Remove the Trap
		if (trap.getOwner() != null)
			trap.getOwner().getSkillManager().getSkillAttributes().getHunterAttributes().setTrapsLaid(trap.getOwner().getSkillManager().getSkillAttributes().getHunterAttributes().getTrapsLaid() - 1);
	}

	/**
	 * The list which contains all Traps
	 */
	public static List<Trap> traps = new CopyOnWriteArrayList<Trap>();

	/**
	 * The Hash map which contains all Hunting NPCS
	 */
	public static List<NPC> hunterNpcs = new CopyOnWriteArrayList<NPC>();

	private static final int[] exps = { 1322, 1623, 2135, 3657, 4332, 4780, 5311, 6667 };


	/**
	 * Can this client lay a trap here?
	 * 
	 * @param client
	 */
	public static boolean canLay(Player client) {
		if (!goodArea(client)) {
			client.getPacketSender().sendMessage(
					"You need to be in a hunting area to lay a trap.");
			return false;
		}
		if (System.currentTimeMillis() - client.getAttributes().getClickDelay() < 2000)
			return false;
		for (final Trap trap : traps) {
			if (trap == null)
				continue;
			if (trap.getGameObject().getPosition().getX() == client.getPosition().getX()
					&& trap.getGameObject().getPosition().getY() == client
					.getPosition().getY()) {
				client.getPacketSender()
				.sendMessage(
						"There is already a trap here, please place yours somewhere else.");
				return false;
			}
		}
		int x = client.getPosition().getX();
		int y = client.getPosition().getY();
		for (final NPC npc : hunterNpcs) {
			if (npc == null)
				continue;
			if (npc.getAttributes().isDying() || !npc.getAttributes().isVisible())
				continue;
			if (x == npc.getPosition().getX() && y == npc.getPosition().getY() || x == npc.getDefaultPosition().getX() && y == npc.getDefaultPosition().getY()) {
				client.getPacketSender().sendMessage(
						"You cannot place your trap here.");

				return false;
			}
		}
		if (client.getSkillManager().getSkillAttributes().getHunterAttributes().getTrapsLaid() >= getMaximumTraps(client)) {
			client.getPacketSender().sendMessage(
					"You can only have a max of " + getMaximumTraps(client)
					+ " traps setup at once.");
			return false;
		}
		return true;
	}


	public static void handleRegionChange(Player client) {
		if(client.getSkillManager().getSkillAttributes().getHunterAttributes().getTrapsLaid() > 0) {
			for (final Trap trap : traps) {
				if (trap == null)
					continue;
				if (trap.getOwner() != null && trap.getOwner().getUsername().equals(client.getUsername()) && !Locations.goodDistance(trap.getGameObject().getPosition(), client.getPosition(), 50)) {
					deregister(trap);
					client.getPacketSender().sendMessage("You didn't watch over your trap well enough, it has collapsed.");
				}
			}
		}
	}

	/**
	 * Checks if the user is in the area where you can lay boxes.
	 * 
	 * @param client
	 * @return
	 */
	public static boolean goodArea(Player client) {
		int x = client.getPosition().getX();
		int y = client.getPosition().getY();
		return x >= 2758 && x <= 2965 && y >= 2880 && y <= 2954;
	}

	/**
	 * Returns the maximum amount of traps this player can have
	 * 
	 * @param client
	 * @return
	 */
	public static int getMaximumTraps(Player client) {
		return client.getSkillManager().getCurrentLevel(Skill.HUNTER) / 20 + 1;
	}

	/**
	 * Gets the ObjectID required by NPC ID
	 * 
	 * @param npcId
	 */
	public static int getObjectIDByNPCID(int npcId) {
		switch (npcId) {
		case 5073:
			return 19180;
		case 5079:
			return 19191;
		case 5080:
			return 19189;
		case 5075:
			return 19184;
		case 5076:
			return 19186;
		case 5074:
			return 19182;
		case 5072:
			return 19178;
		}
		return 0;
	}

	/**
	 * Searches the specific Trap that belongs to this WorldObject
	 * 
	 * @param object
	 */
	public static Trap getTrapForGameObject(final GameObject object) {
		for (final Trap trap : traps) {
			if (trap == null)
				continue;
			if (trap.getGameObject().getPosition().equals(object.getPosition()))
				return trap;
		}
		return null;
	}

	/**
	 * Dismantles a Trap
	 * 
	 * @param client
	 */
	public static void dismantle(Player client, GameObject trap) {
		if (trap == null)
			return;
		final Trap theTrap = getTrapForGameObject(trap);
		if (theTrap != null && theTrap.getOwner() == client) {
			deregister(theTrap);
			if (theTrap instanceof SnareTrap)
				client.getInventory().add(10006, 1);
			else if (theTrap instanceof BoxTrap) {
				client.getInventory().add(10008, 1);
				client.performAnimation(new Animation(827));
			}
		} else
			client.getPacketSender().sendMessage(
					"You cannot dismantle someone else's trap.");
	}

	/**
	 * Sets up a trap
	 * 
	 * @param client
	 * @param trap
	 */
	public static void layTrap(Player client, Trap trap) {
		int id = 10006;
		if (trap instanceof BoxTrap) {
			id = 10008;
			if(client.getSkillManager().getCurrentLevel(Skill.HUNTER) < 60) {
				client.getPacketSender().sendMessage("You need a Hunter level of at least 60 to lay this trap.");
				return;
			}
		}
		if (!client.getInventory().contains(id))
			return;
		if (canLay(client)) {
			register(trap);
			client.getAttributes().setClickDelay(System.currentTimeMillis());
			client.getMovementQueue().stopMovement();
			Firemaking.forceWalk(client);
			client.setPositionToFace(trap.getGameObject().getPosition());
			client.performAnimation(new Animation(827));
			if (trap instanceof SnareTrap) {
				client.getPacketSender().sendMessage("You set up a bird snare..");
				client.getInventory().delete(10006, 1);
			} else if (trap instanceof BoxTrap) {
				if (client.getSkillManager().getCurrentLevel(Skill.HUNTER) < 27) {
					client.getPacketSender().sendMessage("You need a Hunter level of at least 27 to do this.");
					return;
				}
				client.getPacketSender().sendMessage("You set up a box trap..");
				client.getInventory().delete(10008, 1);
			}
			HunterTrapsTask.fireTask();
		}
	}

	/**
	 * Gets the required level for the NPC.
	 * 
	 * @param npcType
	 */
	public static int requiredLevel(int npcType) {
		int levelToReturn = 1;
		if (npcType == 5072)
			levelToReturn = 19;
		else if (npcType == 5073)
			levelToReturn = 1;
		else if (npcType == 5074)
			levelToReturn = 11;
		else if (npcType == 5075)
			levelToReturn = 5;
		else if (npcType == 5076)
			levelToReturn = 9;
		else if (npcType == 5079)
			levelToReturn = 53;
		else if (npcType == 5080)
			levelToReturn = 63;
		return levelToReturn;
	}

	public static boolean isHunterNPC(int npc) {
		return npc >= 5072 && npc <= 5080;
	}

	public static void lootTrap(Player client, GameObject trap) {
		if (trap != null) {
			client.setPositionToFace(trap.getPosition());
			final Trap theTrap = getTrapForGameObject(trap);
			if(theTrap != null) {
				if (theTrap.getOwner() != null)
					if (theTrap.getOwner() == client) {
						if (theTrap instanceof SnareTrap) {
							client.getInventory().add(10006, 1);
							client.getInventory().add(526, 1);
							if (theTrap.getGameObject().getId() == 19180) {
								client.getInventory().add(10088,  20 + Misc.getRandom(30));
								client.getInventory().add(9978, 1);
								client.getPacketSender()
								.sendMessage(
										"You've succesfully caught a crimson swift.");
								client.getSkillManager().addExperience(Skill.HUNTER, exps[0], false);
							} else if (theTrap.getGameObject().getId() == 19184) {
								client.getInventory().add(10090,
										20 + Misc.getRandom(30));
								client.getInventory().add(9978, 1);
								client.getPacketSender()
								.sendMessage(
										"You've succesfully caught a Golden Warbler.");
								client.getSkillManager().addExperience(Skill.HUNTER, exps[1], false);
							} else if (theTrap.getGameObject().getId() == 19186) {
								client.getInventory().add(10091,
										20 + Misc.getRandom(50));
								client.getInventory().add(9978, 1);
								client.getPacketSender()
								.sendMessage(
										"You've succesfully caught a Copper Longtail.");
								client.getSkillManager().addExperience(Skill.HUNTER, exps[2], false);
							} else if (theTrap.getGameObject().getId() == 19182) {
								client.getInventory().add(10089,
										20 + Misc.getRandom(30));
								client.getInventory().add(9978, 1);
								client.getPacketSender()
								.sendMessage(
										"You've succesfully caught a Cerulean Twitch.");
								client.getSkillManager().addExperience(Skill.HUNTER, exps[3], false);
							} else if (theTrap.getGameObject().getId() == 19178) {
								client.getInventory().add(10087,
										20 + Misc.getRandom(30));
								client.getInventory().add(9978, 1);
								client.getPacketSender()
								.sendMessage(
										"You've succesfully caught a Tropical Wagtail.");
								client.getSkillManager().addExperience(Skill.HUNTER, exps[4], false);
							}
						} else if (theTrap instanceof BoxTrap) {
							client.getInventory().add(10008, 1);
							if (theTrap.getGameObject().getId() == 19191) {
								client.getInventory().add(10033, 1);
								client.getSkillManager().addExperience(Skill.HUNTER, exps[6], false);
								client.getPacketSender().sendMessage(
										"You've succesfully caught a chinchompa!");
							} else if (theTrap.getGameObject().getId() == 19189) {
								client.getInventory().add(10034, 1);
								client.getSkillManager().addExperience(Skill.HUNTER, exps[7], false);
								client.getPacketSender()
								.sendMessage(
										"You've succesfully caught a red chinchompa!");
							}
						}
						deregister(theTrap);
						client.performAnimation(new Animation(827));
					} else
						client.getPacketSender().sendMessage(
								"This is not your trap.");
			}
		}

	}

	/**
	 * Try to catch an NPC
	 * 
	 * @param trap
	 * @param npc
	 */
	public static void catchNPC(Trap trap, NPC npc) {
		if (trap.getTrapState().equals(TrapState.CAUGHT))
			return;
		if (trap.getOwner() != null) {
			if (trap.getOwner().getSkillManager().getCurrentLevel(Skill.HUNTER) < requiredLevel(npc.getId())) {
				trap
				.getOwner()
				.getPacketSender()
				.sendMessage(
						"You failed to catch the animal because your hunter level is to low.");
				trap
				.getOwner()
				.getPacketSender()
				.sendMessage(
						"You need atleast "
								+ requiredLevel(npc.getId())
								+ " hunter to catch this animal");
				return;
			}
			deregister(trap);
			if (trap instanceof SnareTrap)
				register(new SnareTrap(new GameObject(getObjectIDByNPCID(npc.getId()), new Position(trap
						.getGameObject().getPosition().getX(), trap
						.getGameObject().getPosition().getY())),
						Trap.TrapState.CAUGHT, 100, trap.getOwner()));
			else		
				register(new BoxTrap(new GameObject(getObjectIDByNPCID(npc.getId()), new Position(trap
						.getGameObject().getPosition().getX(), trap
						.getGameObject().getPosition().getY())),
						Trap.TrapState.CAUGHT, 100, trap.getOwner()));
			npc.appendDeath();
		}
	}

	public static boolean hasLarupia(Player client) {
		return client.getEquipment().getItems()[Equipment.HEAD_SLOT].getId() == 10045 && client.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 10043	&& client.getEquipment().getItems()[Equipment.LEG_SLOT].getId() == 10041;
	}

	public static void handleLogout(Player p) {
		for(Trap trap : traps) {
			if(trap != null) {
				if(trap.getOwner() != null && trap.getOwner().getUsername().equals(p.getUsername())) {
					deregister(trap);
					if (trap instanceof SnareTrap)
						p.getInventory().add(10006, 1);
					else if (trap instanceof BoxTrap) {
						p.getInventory().add(10008, 1);
						p.performAnimation(new Animation(827));
					}
				}
			}
		}
	}
}
