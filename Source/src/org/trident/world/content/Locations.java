package org.trident.world.content;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Direction;
import org.trident.model.Flag;
import org.trident.model.GameObject;
import org.trident.model.Graphic;
import org.trident.model.GroundItem;
import org.trident.model.Item;
import org.trident.model.PlayerInteractingOption;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.model.movement.MovementStatus;
import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.pvp.BountyHunter;
import org.trident.world.content.minigames.impl.Barrows;
import org.trident.world.content.minigames.impl.Conquest;
import org.trident.world.content.minigames.impl.FightCave;
import org.trident.world.content.minigames.impl.FightPit;
import org.trident.world.content.minigames.impl.FishingTrawler;
import org.trident.world.content.minigames.impl.PestControl;
import org.trident.world.content.minigames.impl.SoulWars;
import org.trident.world.content.quests.Nomad;
import org.trident.world.content.quests.RecipeForDisaster;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.content.skills.impl.dungeoneering.DungeoneeringHandler;
import org.trident.world.entity.Entity;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCData;
import org.trident.world.entity.player.Player;

public class Locations {

	public static void login(Player player) {
		player.setLocation(Location.getLocation(player));
		player.getLocation().login(player);
	}

	public static void logout(Player player) {
		player.getLocation().logout(player);
		if(player.getAttributes().getRegionInstance() != null)
			player.getAttributes().getRegionInstance().destruct();
	}

	public enum Location {
		EDGEVILLE(new int[]{3073, 3134}, new int[]{3457, 3518}, false, true, true, true, true, true) {
		},
		LUMBRIDGE(new int[]{3175, 3238}, new int[]{3179, 3302}, false, true, true, true, true, true) {
		},
		KING_BLACK_DRAGON(new int[]{2251, 2292}, new int[]{4673, 4717}, true, true, true, true, true, true) {
		},
		SLASH_BASH(new int[]{2499, 2534}, new int[]{9450, 9471}, true, true, true, true, true, true) {
		},
		BANDIT_CAMP(new int[]{3020, 3150, 3055, 3195}, new int[]{3684, 3711, 2958, 3003}, true, true, true, true, true, true) {
		},
		ROCK_CRABS(new int[]{2689, 2727}, new int[]{3691, 3730}, true, true, true, true, true, true) {
		},
		CORPOREAL_BEAST(new int[]{2879, 2962}, new int[]{4368, 4406}, true, true, true, false, true, true) {
		},
		WILDERNESS(new int[]{2941, 3392, 2986, 3012}, new int[]{3523, 3968, 10338, 10366}, false, true, true, true, true, true) {
			@Override
			public void process(Player player) {
				int modY = player.getPosition().getY() > 6400 ? player.getPosition().getY() - 6400 : player.getPosition().getY();
				player.getPlayerCombatAttributes().setWildernessLevel((((modY - 3520) / 8)+1));
				player.getPacketSender().sendString(25352, ""+player.getPlayerCombatAttributes().getWildernessLevel());
				player.getPacketSender().sendString(25355, "Levels: "+CombatHandler.getLevelDifference(player, false) +" - "+CombatHandler.getLevelDifference(player, true));
				if(player.getAttributes().getWalkableInterfaceId() != 25347)
					player.getPacketSender().sendWalkableInterface(25347);
				if(!player.getPlayerCombatAttributes().getBountyHunterAttributes().isEventRunning(1))
					BountyHunter.processBountyHunter(player);
				if(player.getAttributes().getPlayerInteractingOption() != PlayerInteractingOption.ATTACK)
					player.getPacketSender().sendInteractionOption("Attack", 2, true);
			}

			@Override
			public void leave(Player player) {
				player.getUpdateFlag().flag(Flag.APPEARANCE); //Gets rid of bh skull
			}

			@Override
			public boolean canTeleport(Player player) {
				if(player.getPlayerCombatAttributes().getWildernessLevel() >= 20) {
					boolean staff = player.getRights() == PlayerRights.MODERATOR || player.getRights() == PlayerRights.ADMINISTRATOR || player.getRights() == PlayerRights.OWNER || player.getRights() == PlayerRights.DEVELOPER;
					if(staff) {
						player.getPacketSender().sendMessage("@red@You've teleported out of deep Wilderness, logs have been written.");
						Logger.log(player.getUsername(), "Player teleported out of "+player.getPlayerCombatAttributes().getWildernessLevel()+" Wilderness.");
						return true;
					}
					player.getPacketSender().sendMessage("Teleport spells are blocked in this level of Wilderness.");
					player.getPacketSender().sendMessage("You must be below level 20 of Wilderness to use teleportation spells.");
					return false;
				}
				return true;
			}

			@Override
			public void login(Player player) {
				player.performGraphic(new Graphic(2000, 8));
			}
		},
		BARROWS(new int[] {3520, 3598, 3543, 3584, 3543, 3560}, new int[] {9653, 9750, 3265, 3314, 9685, 9702}, false, true, true, true, true, true) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getWalkableInterfaceId() != 37200)
					player.getPacketSender().sendWalkableInterface(37200);
			}

			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void logout(Player player) {
				leave(player);
			}

			@Override
			public void leave(Player player) {
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				Barrows.killBarrowsNpc(killer, npc, true);
				return true;
			}
		},
		PEST_CONTROL_GAME(new int[]{2624, 2690}, new int[]{2550, 2619}, true, true, true, false, true, true) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getWalkableInterfaceId() != 21100)
					player.getPacketSender().sendWalkableInterface(21100);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked on this island. Wait for the game to finish!");
				return false;
			}

			@Override
			public void logout(Player player) {
				PestControl.leave(player);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC n) {
				return true;
			}
		},
		PEST_CONTROL_BOAT(new int[]{2660, 2663}, new int[]{2638, 2643}, false, false, false, false, false, true) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getWalkableInterfaceId() != 21005)
					player.getPacketSender().sendWalkableInterface(21005);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("You must leave the boat before teleporting.");
				return false;
			}

			@Override
			public void logout(Player player) {
				PestControl.leave(player);
			}
		},
		TRAWLER_BOAT(new int[]{2668, 2674}, new int[]{3165, 3185}, false, false, false, false, false, true) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getWalkableInterfaceId() != 21005)
					player.getPacketSender().sendWalkableInterface(21005);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("You must leave the boat before teleporting.");
				return false;
			}

			@Override
			public void logout(Player player) {
				FishingTrawler.leave(player);
			}
		},
		TRAWLER_GAME(new int[]{-1, -1}, new int[]{-1, -1}, false, false, false, false, true, true) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getWalkableInterfaceId() != 11908)
					player.getPacketSender().sendWalkableInterface(11908);
			}

			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void logout(Player player) {
				FishingTrawler.leave(player);
			}
		},
		SOULWARS(new int[]{-1, -1}, new int[]{-1, -1}, true, true, true, false, true, true) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getWalkableInterfaceId() != 28510)
					player.getPacketSender().sendWalkableInterface(28510);
				if(player.getAttributes().getPlayerInteractingOption() != PlayerInteractingOption.ATTACK)
					player.getPacketSender().sendInteractionOption("Attack", 2, true);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("If you wish to leave, you must use the portal in your team's lobby.");
				return false;
			}

			@Override
			public void logout(Player player) {
				SoulWars.leaveSoulWars(player);
			}

			@Override
			public void onDeath(Player player) {
				SoulWars.handleDeath(player);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				switch(npc.getId()) {
				case SoulWars.BARRICADE_NPC:
					SoulWars.redBarricades.remove(npc);
					SoulWars.blueBarricades.remove(npc);
					return true;
				case SoulWars.BLUE_AVATAR:
				case SoulWars.RED_AVATAR:
					SoulWars.handleAvatarDeath(npc.getId());
					return true;
				case SoulWars.PYREFIEND:
					GroundItemManager.spawnGroundItem(killer, new GroundItem(new Item(SoulWars.SOUL_FRAGMENT, 2), npc.getPosition(), killer.getUsername(), false, 20, true, 80));
					return true;
				case SoulWars.JELLY:
					GroundItemManager.spawnGroundItem(killer, new GroundItem(new Item(SoulWars.SOUL_FRAGMENT, 2), npc.getPosition(), killer.getUsername(), false, 20, true, 80));
					return true;
				}
				return false;
			}

		},
		SOULWARS_WAIT(new int[]{-1, -1}, new int[]{-1, -1}, false, false, false, false, false, true) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getWalkableInterfaceId() != 28500)
					player.getPacketSender().sendWalkableInterface(28500);
				player.getPacketSender().sendString(28504, "New game: " + (25 - SoulWars.getMinutesPassed()) + " mins");
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("You must leave the waiting room before being able to teleport.");
				return false;
			}

			@Override
			public void logout(Player player) {
				SoulWars.leaveSoulWars(player);
			}
		},
		FIGHT_CAVES(new int[]{2360, 2445}, new int[]{5045, 5125}, true, true, false, false, false, false) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getWalkableInterfaceId() != 37300)
					player.getPacketSender().sendWalkableInterface(37300);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here. If you'd like to leave, use the north-east exit.");
				return false;
			}

			@Override
			public void login(Player player) {
				FightCave.enterCave(player, true);
			}

			@Override
			public void onDeath(Player player) {
				FightCave.leaveCave(player, true);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				FightCave.handleCaveDeath(killer, npc);
				return true;
			}
		},
		DAEMONHEIM(new int[]{3430, 3462}, new int[]{3705, 3727}, true, true, true, false, true, true) {
			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void leave(Player player) {

			}
		},
		DUNGEONEERING(new int[]{2421, 2499}, new int[]{4915, 4990}, true, false, true, false, false, false) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getWalkableInterfaceId() != 37500)
					player.getPacketSender().sendWalkableInterface(37500);
			}

			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void logout(Player player) {
				if(Dungeoneering.doingDungeoneering(player))
					Dungeoneering.leave(player);
			}

			@Override
			public void onDeath(Player player) {
				if(Dungeoneering.doingDungeoneering(player))
					DungeoneeringHandler.handlePlayerDeath(player);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				if(Dungeoneering.doingDungeoneering(killer)) {
					DungeoneeringHandler.handleNpcDeath(killer, npc);
					return true;
				}
				return false;
			}
		},
		CONQUEST(new int[]{2598, 2618}, new int[]{9662, 9689}, true, true, false, true, false, false) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getWalkableInterfaceId() != 37300)
					player.getPacketSender().sendWalkableInterface(37300);
				player.getPacketSender().sendString(37308, "@yel@Points: "+player.getPointsHandler().getConquestPoints());
			}

			@Override
			public boolean canTeleport(Player player) {
				if(player.getAttributes().getMinigameAttributes().getConquestArenaAttributes().isInArena()) {
					player.getPacketSender().sendMessage("Teleport spells are blocked here. Please use the ladder to leave the arena.");
					return false;
				}
				return true;
			}

			@Override
			public void logout(Player player) {
				if(player.getAttributes().getMinigameAttributes().getConquestArenaAttributes().isInArena())
					Conquest.leaveArena(player);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				Conquest.handleNPCDeath(killer, npc);
				return true;
			}
		},
		FIGHT_PITS(new int[]{2370, 2425}, new int[]{5133, 5165}, true, true, true, false, false, true) {
			@Override
			public void process(Player player) {
				if(FightPit.inFightPits(player)) {
					FightPit.updateGame(player);
					if(player.getAttributes().getPlayerInteractingOption() != PlayerInteractingOption.ATTACK)
						player.getPacketSender().sendInteractionOption("Attack", 2, true);
				}
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here. If you'd like to leave, use the northern exit.");
				return false;
			}

			@Override
			public void logout(Player player) {
				FightPit.removePlayer(player, "leave game");
			}

			@Override
			public void onDeath(Player player) {
				if(FightPit.getState(player) != null)
					FightPit.removePlayer(player, "death");
			}
		},
		FIGHT_PITS_WAIT_ROOM(new int[]{2393, 2404}, new int[]{5168, 5177}, false, false, false, false, false, true) {
			@Override
			public void process(Player player) {
				FightPit.updateWaitingRoom(player);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here. If you'd like to leave, use the northern exit.");
				return false;
			}

			@Override
			public void logout(Player player) {
				FightPit.removePlayer(player, "leave room");
			}

		},
		DUEL_ARENA(new int[]{3322, 3394, 3311, 3323, 3331, 3391}, new int[]{3195, 3291, 3223, 3248, 3242, 3260}, false, false, false, false, false, false) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getWalkableInterfaceId() != 201)
					player.getPacketSender().sendWalkableInterface(201);
				if(player.getDueling().duelingStatus == 0) {
					if(player.getAttributes().getPlayerInteractingOption() != PlayerInteractingOption.CHALLENGE)
						player.getPacketSender().sendInteractionOption("Challenge", 2, false);
				} else if(player.getAttributes().getPlayerInteractingOption() != PlayerInteractingOption.ATTACK)
					player.getPacketSender().sendInteractionOption("Attack", 2, true);
			}

			@Override
			public boolean canTeleport(Player player) {
				if(player.getDueling().duelingStatus == 5) {
					player.getPacketSender().sendMessage("To forfiet a duel, run to the west and use the trapdoor.");
					return false;
				}
				return true;
			}

			@Override
			public void logout(Player player) {
				if(player.getDueling().inDuelScreen && player.getDueling().duelingStatus != 5)
					player.getDueling().declineDuel(player.getDueling().duelingWith > 0 ? true : false);
				else if(player.getDueling().duelingStatus == 5) {
					if(player.getDueling().duelingWith > -1) {
						Player duelEnemy = World.getPlayers().get(player.getDueling().duelingWith);
						duelEnemy.getDueling().duelVictory();
					}
				}
			}

			@Override
			public void onDeath(Player player) {
				if(player.getDueling().duelingStatus == 5) {
					if(player.getDueling().duelingWith > -1) {
						Player duelEnemy = World.getPlayers().get(player.getDueling().duelingWith);
						duelEnemy.getDueling().duelVictory();
						duelEnemy.getPacketSender().sendMessage("You won the duel! Congratulations!");
					}
					player.getPacketSender().sendMessage("You've lost the duel.");
					player.getDueling().arenaStats[1]++; player.getDueling().reset();
				}
				player.moveTo(new Position(3368 + Misc.getRandom(5), 3267+ Misc.getRandom(3), 0));
				player.getDueling().reset();
			}
		},
		GODWARS_DUNGEON(new int[]{2800, 2950, 2858, 2943}, new int[]{5200, 5400, 5180, 5230}, true, true, true, false, true, true) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getWalkableInterfaceId() != 16210)
					player.getPacketSender().sendWalkableInterface(16210);
			}

			@Override
			public boolean canTeleport(Player player) {
				return true;
			}

			@Override
			public void onDeath(Player player) {
				leave(player);
			}

			@Override
			public void leave(Player player) {
				GodWarsDungeon.resetProgress(player);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				int index = -1;
				if(NPCData.isArmadylGodwarsNpc(npc.getId()))
					index = 0;
				else if(NPCData.isBandosGodwarsNpc(npc.getId()))
					index = 1;
				else if(NPCData.isSaradominGodwarsNpc(npc.getId()))
					index = 2;
				else if(NPCData.isZamorakGodwarsNpc(npc.getId()))
					index = 3;
				if(index != -1) {
					killer.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[index]++;
					killer.getPacketSender().sendString((16216+index), ""+killer.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[index]);
				}
				return false;
			}
		},
		NOMAD(new int[]{3342, 3377}, new int[]{5839, 5877}, true, true, false, true, false, true) {
			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here. If you'd like to leave, use the southern exit.");
				return false;
			}

			@Override
			public void leave(Player player) {
				Nomad.endFight(player, false);
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				if(npc.getId() == 8528) {
					Nomad.endFight(killer, true);
					return true;
				}
				return false;
			}
		},
		RECIPE_FOR_DISASTER(new int[]{1885, 1913}, new int[]{5340, 5369}, true, true, false, false, false, false) {

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here. If you'd like to leave, use a portal.");
				return false;
			}

			@Override
			public boolean handleKilledNPC(Player killer, NPC npc) {
				RecipeForDisaster.handleNPCDeath(killer, npc);
				return true;
			}

			@Override
			public void logout(Player player) {
				if(player.getAttributes().getRegionInstance() != null)
					player.getAttributes().getRegionInstance().destruct();
				player.moveTo(new Position(3207, 3215, 0));
			}
		},
		FREE_FOR_ALL_ARENA(new int[]{2755, 2876}, new int[]{5511, 5627}, true, true, true, false, false, true) {
			@Override
			public void process(Player player) {
				if(player.getAttributes().getPlayerInteractingOption() != PlayerInteractingOption.ATTACK)
					player.getPacketSender().sendInteractionOption("Attack", 2, true);
			}

			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here, if you wish to teleport, use the portal.");
				return false;
			}
			@Override
			public void onDeath(Player player) {
				player.moveTo(new Position(2815, 5511));
			}
		},
		FREE_FOR_ALL_WAIT(new int[]{2755, 2876}, new int[]{5507, 5627}, false, false, true, false, false, true) {
			@Override
			public boolean canTeleport(Player player) {
				player.getPacketSender().sendMessage("Teleport spells are blocked here, if you wish to teleport, use the portal.");
				return false;
			}

			@Override
			public void onDeath(Player player) {
				player.moveTo(new Position(2815, 5511));
			}
		},
		WARRIORS_GUILD(new int[]{2833, 2879}, new int[]{3531, 3559}, false, true, true, false, false, true) {
		},
		PURO_PURO(new int[]{2556, 2630}, new int[]{4281, 4354}, false, true, true, false, false, true) {
		},
		FLESH_CRAWLERS(new int[]{2033, 2049}, new int[]{5178, 5197}, false, true, true, false, true, true) {
		},
		BANK(new int[]{3090, 3099, 3089, 3090, 3248, 3258, 3179, 3191, 2944, 2948, 2942, 2948, 2944, 2950, 3008, 3019, 3017, 3022, 3203, 3213, 3212, 3215, 3215, 3220, 3220, 3227, 3227, 3230, 3226, 3228, 3227, 3229}, new int[]{3487, 3500, 3492, 3498, 3413, 3428, 3432, 3448, 3365, 3374, 3367, 3374, 3365, 3370, 3352, 3359, 3352, 3357, 3200, 3237, 3200, 3235, 3202, 3235, 3202, 3229, 3208, 3226, 3230, 3211, 3208, 3226}, false, true, true, false, false, true) {
		},
		DEFAULT(null, null, false, true, true, true, true, true) {
		};

		Location(int[] x, int[] y, boolean multi, boolean summonAllowed, boolean followingAllowed, boolean cannonAllowed, boolean firemakingAllowed, boolean aidingAllowed) {
			this.x = x;
			this.y = y;
			this.multi = multi;
			this.summonAllowed = summonAllowed;
			this.followingAllowed = followingAllowed;
			this.cannonAllowed = cannonAllowed;
			this.firemakingAllowed = firemakingAllowed;
			this.aidingAllowed = aidingAllowed;
		}

		private int[] x, y;
		private boolean multi;
		private boolean summonAllowed;
		private boolean followingAllowed;
		private boolean cannonAllowed;
		private boolean firemakingAllowed;
		private boolean aidingAllowed;

		public int[] getX() {
			return x;
		}

		public int[] getY() {
			return y;
		}

		public static boolean inMulti(GameCharacter gc) {
			if(gc.getLocation() == WILDERNESS) {
				int x = gc.getPosition().getX(), y = gc.getPosition().getY();
				if(x >= 3250 && x <= 3302 && y >= 3905 && y <= 3925 || x >= 3020 && x <= 3055 && y >= 3684 && y <= 3711 || x >= 3150 && x <= 3195 && y >= 2958 && y <= 3003) //Bandit camp
					return true;
			}
			return gc.getLocation().multi;
		}

		public boolean isSummoningAllowed() {
			return summonAllowed;
		}

		public boolean isFollowingAllowed() {
			return followingAllowed;
		}

		public boolean isCannonAllowed() {
			return cannonAllowed;
		}

		public boolean isFiremakingAllowed() {
			return firemakingAllowed;
		}

		public boolean isAidingAllowed() {
			return aidingAllowed;
		}

		public static Location getLocation(Entity gc) {
			for(Location location : Location.values()) {
				if(location != Location.DEFAULT)
					if(inLocation(gc, location))
						return location;
			}
			return Location.DEFAULT;
		}

		public static boolean inLocation(Entity gc, Location location) {
			if(location == Location.DEFAULT) {
				if(getLocation(gc) == Location.DEFAULT)
					return true;
				else
					return false;
			}
			if(gc instanceof Player) {
				Player p = (Player)gc;
				if(location == Location.TRAWLER_GAME) {
					String state = FishingTrawler.getState(p);
					return (state != null && state.equals("PLAYING"));
				} else if(location == FIGHT_PITS_WAIT_ROOM || location == FIGHT_PITS) {
					String state = FightPit.getState(p), needed = (location == FIGHT_PITS_WAIT_ROOM) ? "WAITING" : "PLAYING";
					return (state != null && state.equals(needed));
				} else if(location == Location.SOULWARS) {
					return (SoulWars.redTeam.contains(p) || SoulWars.blueTeam.contains(p) && SoulWars.gameRunning);
				} else if(location == Location.SOULWARS_WAIT) {
					return SoulWars.isWithin(SoulWars.BLUE_LOBBY, p) || SoulWars.isWithin(SoulWars.RED_LOBBY, p);
				}
			}
			return inLocation(gc.getPosition().getX(), gc.getPosition().getY(), location);
		}

		public static boolean inLocation(int absX, int absY, Location location) {
			int checks = location.getX().length / 2;
			for(int i = 0; i <= checks; i+=2) {
				if(absX >= location.getX()[i] && absX <= location.getX()[i + 1]) {
					if(absY >= location.getY()[i] && absY <= location.getY()[i + 1]) {
						return true;
					}
				}
			}
			return false;
		}

		public void process(Player player) {

		}

		public boolean canTeleport(Player player) {
			return true;
		}

		public void login(Player player) {

		}

		public void leave(Player player) {

		}

		public void logout(Player player) {

		}

		public void onDeath(Player player) {

		}

		public boolean handleKilledNPC(Player killer, NPC npc) {
			return false;
		}
	}

	public static void process(GameCharacter gc) {
		if(Location.inLocation(gc, gc.getLocation())) {
			if(gc.isPlayer()) {
				Player player = (Player) gc;
				gc.getLocation().process(player);
				if(Location.inMulti(player)) {
					if(player.getAttributes().getMultiIcon() != 1)
						player.getPacketSender().sendMultiIcon(1);
				} else if(player.getAttributes().getMultiIcon() == 1)
					player.getPacketSender().sendMultiIcon(0);
			}
		} else {
			if(gc.isPlayer()) {
				Player player = (Player) gc;
				gc.getLocation().leave(player);
				if(player.getAttributes().getMultiIcon() > 0)
					player.getPacketSender().sendMultiIcon(0);
				if(player.getAttributes().getWalkableInterfaceId() > 0 && player.getAttributes().getWalkableInterfaceId() != 37400 && player.getAttributes().getWalkableInterfaceId() != 50000)
					player.getPacketSender().sendWalkableInterface(-1);
				if(player.getAttributes().getPlayerInteractingOption() != PlayerInteractingOption.NONE)
					player.getPacketSender().sendInteractionOption("null", 2, true);
			}
			gc.setLocation(Location.getLocation(gc));
		}
	}

	public static boolean goodDistance(int objectX, int objectY, int playerX,
			int playerY, int distance) {
		if (playerX == objectX && playerY == objectY)
			return true;
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == playerX
						&& ((objectY + j) == playerY
						|| (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if ((objectX - i) == playerX
						&& ((objectY + j) == playerY
						|| (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if (objectX == playerX
						&& ((objectY + j) == playerY
						|| (objectY - j) == playerY || objectY == playerY)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean goodDistance(Position pos1, Position pos2, int distanceReq) {
		if(pos1.getZ() != pos2.getZ())
			return false;
		return goodDistance(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY(), distanceReq);
	}

	public static int distanceTo(Position position, Position destination,
			int size) {
		final int x = position.getX();
		final int y = position.getY();
		final int otherX = destination.getX();
		final int otherY = destination.getY();
		int distX, distY;
		if (x < otherX)
			distX = otherX - x;
		else if (x > otherX + size)
			distX = x - (otherX + size);
		else
			distX = 0;
		if (y < otherY)
			distY = otherY - y;
		else if (y > otherY + size)
			distY = y - (otherY + size);
		else
			distY = 0;
		if (distX == distY)
			return distX + 1;
		return distX > distY ? distX : distY;
	}

	public static class GodWarsDungeon {
		/**
		 * @author Gabbe
		 */
		/*
		 * Boss rooms
		 */
		private static final int ARMADYL_ALTAR = 26288;
		private static final int BANDOS_ALTAR = 26289;
		private static final int SARADOMIN_ALTAR = 26287;
		private static final int ZAMORAK_ALTAR = 26286;

		/*
		 * Boss rooms entrances
		 */
		private static final int ARMADYL_DOOR = 26426;
		private static final int BANDOS_DOOR = 26425;
		private static final int SARADOMIN_DOOR = 26427;
		private static final int ZAMORAK_DOOR = 26428;

		/*
		 * Entrances to the sideparts of the map
		 */
		private static final int ARMADYL_ENTRANCE = 26303;
		private static final int BANDOS_ENTRANCE = 26384;
		private static final int SARADOMIN_ENTRANCE = 23093;
		private static final int ZAMORAK_BRIDGE = 26439;

		public static boolean handleObject(final Player player, final GameObject obj, final int objectClickType) {
			int objectId = obj.getId();
			if(objectClickType == 1) { //First click object
				switch(objectId) {
				case ZAMORAK_DOOR:
				case ARMADYL_DOOR:
				case BANDOS_DOOR:
				case SARADOMIN_DOOR:
					String bossRoom = "Armadyl";
					boolean leaveRoom = player.getPosition().getY() > 5295;
					int index = 0;
					Position movePos = new Position(2839, !leaveRoom ? 5296 : 5295, 2);
					if(objectId == BANDOS_DOOR) {
						bossRoom = "Bandos";
						leaveRoom = player.getPosition().getX() > 2863;
						index = 1;
						movePos = new Position(!leaveRoom ? 2864 : 2863, 5354, 2);
					} else if(objectId == SARADOMIN_DOOR) {
						bossRoom = "Saradomin";
						leaveRoom = player.getPosition().getX() < 2908;
						index = 2;
						movePos = new Position(leaveRoom ? 2908 : 2907, 5265);
					} else if(objectId == ZAMORAK_DOOR) {
						bossRoom = "Zamorak";
						leaveRoom = player.getPosition().getY() <= 5331;
						index = 3;
						movePos = new Position(2925, leaveRoom ? 5332 : 5331, 2);
					}
					if(!leaveRoom && (player.getRights() != PlayerRights.ADMINISTRATOR && player.getRights() != PlayerRights.OWNER && player.getRights() != PlayerRights.DEVELOPER && player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[index] < 20)) {
						player.getPacketSender().sendMessage("You need "+Misc.anOrA(bossRoom)+" "+bossRoom+" killcount of at least 20 to enter this room.");
						return true;
					}
					player.moveTo(movePos);
					player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().setHasEnteredRoom(leaveRoom ? false : true);
					player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[index] = 0;
					player.getPacketSender().sendString(16216+index, "0");
					break;
				case BANDOS_ALTAR:
				case ZAMORAK_ALTAR:
				case ARMADYL_ALTAR:
				case SARADOMIN_ALTAR:
					if(System.currentTimeMillis() - player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getAltarDelay() < 600000) {
						player.getPacketSender().sendMessage("");
						player.getPacketSender().sendMessage("You can only pray at a God's altar once every 10 minutes.");
						player.getPacketSender().sendMessage("You must wait another "+(int)((600 - (System.currentTimeMillis() - player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getAltarDelay()) * 0.001))+" seconds before being able to do this again.");
						return true;
					}
					int itemCount = objectId == BANDOS_ALTAR ? Equipment.getItemCount(player, "Bandos", false) : objectId == ZAMORAK_ALTAR ? Equipment.getItemCount(player, "Zamorak", false) : objectId == ARMADYL_ALTAR ? Equipment.getItemCount(player, "Armadyl", false) : objectId == SARADOMIN_ALTAR ? Equipment.getItemCount(player, "Saradomin", false) : 0;
					int toRestore = player.getSkillManager().getMaxLevel(Skill.PRAYER) + (itemCount * 10);
					if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) >= toRestore) {
						player.getPacketSender().sendMessage("You do not need to recharge your Prayer points at the moment.");
						return true;
					}
					player.performAnimation(new Animation(645));
					player.getSkillManager().setCurrentLevel(Skill.PRAYER, toRestore);
					player.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().setAltarDelay(System.currentTimeMillis());
					return true;
				case SARADOMIN_ENTRANCE:
					if(player.getSkillManager().getCurrentLevel(Skill.AGILITY) < 70) {
						player.getPacketSender().sendMessage("You need an Agility level of at least 70 to go through this portal.");
						return true;
					}
					if(System.currentTimeMillis() - player.getAttributes().getClickDelay() <= 2000) 
						return true;
					int plrHeight = player.getPosition().getZ();
					if(plrHeight == 2)
						player.moveTo(new Position(2914, 5300, 1));
					else if(plrHeight == 1) {
						int x = obj.getPosition().getX();
						int y = obj.getPosition().getY();
						if(x == 2914 && y == 5300)
							player.moveTo(new Position(2912, 5299, 2));
						else if(x == 2920 && y == 5276)
							player.moveTo(new Position(2920, 5274, 0));
					} else if(plrHeight == 0)
						player.moveTo(new Position(2920, 5276, 1));
					player.getAttributes().setClickDelay(System.currentTimeMillis());
					return true;
				case ZAMORAK_BRIDGE:
					if(player.getSkillManager().getMaxLevel(Skill.CONSTITUTION) <= 700) {
						player.getPacketSender().sendMessage("You need a Constitution level of at least 70 to swim across.");
						return true;
					}
					if(System.currentTimeMillis() - player.getAttributes().getClickDelay() < 1000)
						return true;
					if(player.getSkillManager().getSkillAttributes().getAgilityAttributes().isCrossingObstacle())
						return true;
					final String startMessage = "You jump into the icy cold water..";
					final String endMessage = "You climb out of the water safely.";
					final int jumpGFX = 68;
					final int jumpAnimation = 772;
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(773).setCrossingObstacle(true);
					player.getUpdateFlag().flag(Flag.APPEARANCE);
					player.performAnimation(new Animation(3067));
					final boolean goBack2 = player.getPosition().getY() >= 5344;
					player.getPacketSender().sendMessage(startMessage);  
					player.moveTo(new Position(2885, !goBack2 ? 5335 : 5342, 2));
					player.setDirection(goBack2 ? Direction.SOUTH : Direction.NORTH);
					player.performGraphic(new Graphic (jumpGFX));
					player.performAnimation(new Animation(jumpAnimation));
					player.getAttributes().setResetPosition(player.getPosition().copy());
					TaskManager.submit(new Task(1, player, false) {
						int ticks = 0;
						@Override
						public void execute() {
							ticks++;
							player.getMovementQueue().walkStep(0, goBack2 ? -1 : 1);
							if(ticks >= 10)
								stop();
						}
						@Override
						public void stop() {
							player.getSkillManager().getSkillAttributes().getAgilityAttributes().setAnimation(-1).setCrossingObstacle(false);
							player.getUpdateFlag().flag(Flag.APPEARANCE);
							player.getPacketSender().sendMessage(endMessage);
							player.setTeleportPosition(new Position(2885, player.getPosition().getY() < 5340 ? 5333 : 5345, 2));
							player.getMovementQueue().setMovementStatus(MovementStatus.NONE);
							player.getAttributes().setResetPosition(null);
							setEventRunning(false);
						}
					});
					player.getAttributes().setClickDelay(System.currentTimeMillis()+9000);
					break;
				case BANDOS_ENTRANCE:
					if(player.getSkillManager().getSkillAttributes().getAgilityAttributes().isCrossingObstacle())
						return true;
					if(!player.getInventory().contains(2347)) {
						player.getPacketSender().sendMessage("You need to have a hammer to bang on the door with.");
						return true;
					}
					player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(true);
					final boolean goBack = player.getPosition().getX() <= 2850;
					player.performAnimation(new Animation(377));
					TaskManager.submit(new Task(2, player, false) {
						@Override
						public void execute() {
							player.moveTo(new Position(goBack ? 2851 : 2850, 5333, 2));
							player.getSkillManager().getSkillAttributes().getAgilityAttributes().setCrossingObstacle(false);
							stop();
						}
					});
					break;
				case ARMADYL_ENTRANCE:
					if(System.currentTimeMillis() - player.getAttributes().getClickDelay() <= 1200)
						return true;
					if (player.getSkillManager().getCurrentLevel(Skill.RANGED) < 70)
						player.getPacketSender().sendMessage("You need a Ranged level of at least 70 to swing across here.");
					else if (!player.getInventory().contains(9418)) {
						player.getPacketSender().sendMessage("You need a Mithril grapple to swing across here.");
						return true;
					} else {
						player.performAnimation(new Animation(789));
						TaskManager.submit(new Task(2, player, false) {
							@Override
							public void execute() {
								player.getPacketSender().sendMessage("You throw your Mithril grapple over the pillar and move across.");
								player.moveTo(new Position(2871, player.getPosition().getY() <= 5270 ? 5279 : 5269, 2));
								stop();
							}
						});
						player.getAttributes().setClickDelay(System.currentTimeMillis());
					}
					break;
				}
			}
			return false;
		}

		/**
		 * Resets a player's godwars progress
		 * @param p		The player to reset progress for
		 */
		public static void resetProgress(Player p) {
			if(!(p.getPosition().getX() != 1 && p.getPosition().getY() != 1))
				return;
			for(int i = 0; i < p.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount().length; i++) {
				p.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().getKillcount()[i] = 0;
				p.getPacketSender().sendString((16216+i), "0");
			}
			p.getAttributes().getMinigameAttributes().getGodwarsDungeonAttributes().setAltarDelay(0).setHasEnteredRoom(false);
			p.getPacketSender().sendMessage("Your Godwars dungeon progress has been reset.");
		}
	}
}
