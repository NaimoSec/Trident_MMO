package org.trident.world.content;

import java.util.HashMap;
import java.util.Map;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.Item;
import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.model.movement.MovementStatus;
import org.trident.util.Misc;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.skills.SkillManager;
import org.trident.world.entity.player.Player;

/**
 * Handles emotes
 * @author Gabbe
 */
public class Emotes {

	public enum EmoteData {
		YES(161, new Animation(855), null),
		NO(162, new Animation(856), null),
		BOW(163, new Animation(858), null),
		ANGRY(164, new Animation(859), null),
		THINK(165, new Animation(857), null),
		WAVE(167, new Animation(863), null),
		SHRUG(168, new Animation(2113), null),
		CHEER(169, new Animation(862), null),
		BECKON(170, new Animation(864), null),
		LAUGH(171, new Animation(861), null),
		JUMP_FOR_JOY(172, new Animation(2109), null),
		YAWN(173, new Animation(2111), null),
		DANCE(19140, new Animation(866), null),
		JIG(175, new Animation(2106), null),
		SPIN(176, new Animation(2107), null),
		HEADBANG(177, new Animation(2108), null),
		CRY(178, new Animation(860), null),
		KISS(179, new Animation(2108), new Graphic(1702)),
		PANIC(180, new Animation(2105), null),
		RASPBERRY(181, new Animation(2110), null),
		CRAP(182, new Animation(865), null),
		SALUTE(19141, new Animation(2112), null),
		GOBLIN_BOW(184, new Animation(2127), null),
		GOBLIN_SALUTE(185, new Animation(2128), null),
		GLASS_BOX(186, new Animation(1131), null),
		CLIMB_ROPE(187, new Animation(1130), null),
		LEAN(666, new Animation(1129), null),
		GLASS_WALL(667, new Animation(1128), null),
		ZOMBIE_WALK(6522, new Animation(3544), null),
		ZOMBIE_DANCE(6532, new Animation(3543), null),
		ZOMBIE_HAND(6533, new Animation(7272), new Graphic(1244)),
		SAFETY_FIRST(6540, new Animation(8770), new Graphic(1553)),
		AIR_GUITAR(11101, new Animation(2414), new Graphic(1537)),
		SNOWMAN_DANCE(11102, new Animation(7531), null),
		FREEZE(11103, new Animation(11044), new Graphic(1973));

		EmoteData(int button, Animation animation, Graphic graphic) {
			this.button = button;
			this.animation = animation;
			this.graphic = graphic;
		}

		private int button;
		public Animation animation;
		public Graphic graphic;

		private static EmoteData forButton(int button) {
			for(EmoteData data : EmoteData.values()) {
				if(data != null && data.button == button)
					return data;
			}
			return null;
		}
		
		public static EmoteData getRandomEmote() {
			int randomEmote = Misc.getRandom(EmoteData.values().length -1);
			return EmoteData.values()[randomEmote];
		}
	}


	public static boolean doEmote(final Player player, int buttonId) {
		EmoteData emoteData = EmoteData.forButton(buttonId);
		//Normal emotes
		if(emoteData != null) {
			animation(player, emoteData.animation, emoteData.graphic);
			return true;
		//Skillcapes
		} else if(buttonId == 6541) {
			if(System.currentTimeMillis() - player.getAttributes().getGraphicDelay() <= 7000)
				return true;
			Item cape = player.getEquipment().getItems()[Equipment.CAPE_SLOT];
			Skillcape_Data data = Skillcape_Data.dataMap.get(cape.getId());
			if (data != null) {
				if (data != Skillcape_Data.QUEST_POINT) {
					Skill skill = Skill.forId(data.ordinal());
					if (data == Skillcape_Data.DUNGEONEERING_MASTER)
						skill = Skill.DUNGEONEERING;
					int level = SkillManager.getMaxAchievingLevel(skill);
					if (player.getSkillManager().getMaxLevel(skill) < level) {
						player.getPacketSender().sendMessage("You need "+Misc.anOrA(skill.getName())+" " + Misc.formatPlayerName2(skill.getName().toLowerCase()) + " level of at least "+ level + " to do this emote.");
						return false;
					}
				}
				player.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
				if (data != Skillcape_Data.DUNGEONEERING && data != Skillcape_Data.DUNGEONEERING_MASTER) {
					player.performAnimation(data.animation);
					player.performGraphic(data.graphic);
					player.getAttributes().setGraphicDelay(System.currentTimeMillis());
					Achievements.handleAchievement(player, Achievements.Tasks.TASK25);
					TaskManager.submit(new Task(data.delay, player, false) {
						@Override
						public void execute() {
							player.getMovementQueue().setMovementStatus(MovementStatus.NONE);
							stop();
						}
					});
				} else {
					//dungeoneeringEmote(player, data);
				}
				return true;
			} else
				player.getPacketSender().sendMessage("You must be wearing a Skillcape in order to use this emote.");
			return true;
		}
		return false;
	}

	public static void animation(Player player, Animation anim, Graphic graphic) {
		if(CombatHandler.inCombat(player)) {
			player.getPacketSender().sendMessage("You cannot do this right now.");
			return;
		}
		if (System.currentTimeMillis() - player.getAttributes().getGraphicDelay() < 3100) {
			player.getPacketSender().sendMessage("You are already performing an emote.");
			return;
		}
		player.getMovementQueue().stopMovement();
		if(anim != null)
			player.performAnimation(anim);
		if(graphic != null)
			player.performGraphic(graphic);
		player.getAttributes().setGraphicDelay(System.currentTimeMillis());
	}

	/**
	 * All Skillcape Configurations and data
	 */
	private enum Skillcape_Data {
		ATTACK(new int[] {9747, 9748, 10639},
				4959, 823, 7),
				DEFENCE(new int[] {9753, 9754, 10641},
						4961, 824, 10),
						STRENGTH(new int[] {9750, 9751, 10640},
								4981, 828, 25),
								CONSTITUTION(new int[] {9768, 9769, 10647},
										14252, 2745, 12),
										RANGED(new int[] {9756, 9757, 10642},
												4973, 832, 12),
												PRAYER(new int[] {9759, 9760, 10643},
														4979, 829, 15),
														MAGIC(new int[] {9762, 9763, 10644},
																4939, 813, 6),
																COOKING(new int[] {9801, 9802, 10658},
																		4955, 821, 36),
																		WOODCUTTING(new int[] {9807, 9808, 10660},
																				4957, 822, 25),
																				FLETCHING(new int[] {9783, 9784, 10652},
																						4937, 812, 20),
																						FISHING(new int[] {9798, 9799, 10657},
																								4951, 819, 19),
																								FIREMAKING(new int[] {9804, 9805, 10659},
																										4975, 831, 14),
																										CRAFTING(new int[] {9780, 9781, 10651},
																												4949, 818, 15),
																												SMITHING(new int[] {9795, 9796, 10656},
																														4943, 815, 23),
																														MINING(new int[] {9792, 9793, 10655},
																																4941, 814, 8),
																																HERBLORE(new int[] {9774, 9775, 10649},
																																		4969, 835, 16),
																																		AGILITY(new int[] {9771, 9772, 10648},
																																				4977, 830, 8),
																																				THIEVING(new int[] {9777, 9778, 10650},
																																						4965, 826, 16),
																																						SLAYER(new int[] {9786, 9787, 10653},
																																								4967, 1656, 8),
																																								FARMING(new int[] {9810, 9811, 10661},
																																										4963, 825, 16),
																																										RUNECRAFTING(new int[] {9765, 9766, 10645},
																																												4947, 817, 10),
																																												CONSTRUCTION(new int[] {9789, 9790, 10654},
																																														4953, 820, 16),
																																												HUNTER(new int[] {9948, 9949, 10646},
																																														5158, 907, 14),
																																																SUMMONING(new int[] {12169, 12170, 12524},
																																																		8525, 1515, 10),
																																																		DUNGEONEERING(new int[] {15706, 18508, 18509},
																																																				-1, -1, -1),
																																																				DUNGEONEERING_MASTER(new int[] {19709, 19710},
																																																						-1, -1, -1),
																																																						QUEST_POINT(new int[] {9813, 9814, 10662},
																																																								4945, 816, 19);

		private Skillcape_Data(int[] itemId, int animationId, int graphicId, int delay) {
			item = new Item[itemId.length];
			for (int i = 0; i < itemId.length; i++) {
				item[i] = new Item(itemId[i]);
			}
			animation = new Animation(animationId);
			graphic = new Graphic(graphicId);
			this.delay = delay;
		}

		private final Item[] item;

		private final Animation animation;

		private final Graphic graphic;

		private final int delay;

		private static Map<Integer, Skillcape_Data> dataMap = new HashMap<Integer, Skillcape_Data>();

		static {
			for (Skillcape_Data data : Skillcape_Data.values()) {
				for (Item item : data.item) {
					dataMap.put(item.getId(), data);
				}
			}
		}
	}
}
