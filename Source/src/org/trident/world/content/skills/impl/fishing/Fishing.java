package org.trident.world.content.skills.impl.fishing;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Direction;
import org.trident.model.Skill;
import org.trident.model.definitions.ItemDefinition;
import org.trident.util.Misc;
import org.trident.world.entity.player.Player;

public class Fishing {

	public enum Spot {
		LURE(2029, new int[]{335, 331}, 309, 314, new int[]{20, 30}, false, new int[]{50, 70}, 623),
		CAGE(2030, new int[]{377}, 301, -1, new int[]{40}, false, new int[]{90}, 619),
		BIGNET(2031, new int[]{353, 341, 363}, 305, -1, new int[]{16, 23, 46}, false, new int[]{20, 45, 100}, 620),
		SMALLNET(2028, new int[]{317, 321}, 303, -1, new int[]{1, 15}, false, new int[]{10, 40}, 621),
		MONK_FISH(2026, new int[]{7944, 389}, 305, -1, new int[]{68, 81}, false, new int[]{197, 457}, 621),
		LURE2(2029, new int[]{349}, 307, 313, new int[]{25}, true, new int[]{60}, 623),
		HARPOON(2030, new int[]{359, 371}, 311, -1, new int[]{35, 50}, true, new int[]{80, 100}, 618),
		HARPOON2(2031, new int[]{383}, 311, -1, new int[]{76}, true, new int[]{110}, 618),
		BAIT(2028, new int[]{327, 345}, 307, 313, new int[]{5, 10}, true, new int[]{20, 30}, 623),
		ROCKTAIL(-1, new int[]{15270}, 309, 25, new int[]{91}, false, new int[]{412}, 623);

		int objectId, equipment, bait, anim;
		int[] rawFish, fishingReqs, xp;
		boolean second;
		private Spot(int objectId, int[] rawFish, int equipment, int bait, int[] fishingReqs, boolean second, int[] xp, int anim) {
			this.objectId = objectId;
			this.rawFish = rawFish;
			this.equipment = equipment;
			this.bait = bait;
			this.fishingReqs = fishingReqs;
			this.second = second;
			this.xp = xp;
			this.anim = anim;
		}

		public int getNPCId() {
			return objectId;
		}

		public int[] getRawFish() {
			return rawFish;
		}

		public int getEquipment() {
			return equipment;
		}

		public int getBait() {
			return bait;
		}

		public int[] getLevelReq() {
			return fishingReqs;
		}

		public boolean getSecond() {
			return second;
		}

		public int[] getXp() {
			return xp;
		}

		public int getAnim() {
			return anim;
		}
	}

	public static Spot forSpot(int npcId, boolean secondClick) {
		for (Spot s : Spot.values()) {
			if (secondClick) {
				if (s.getSecond()) {
					if (s.getNPCId() == npcId) {
						if (s != null)
							return s;
					}
				}
			} else {
				if (s.getNPCId() == npcId) {
					if (s != null)
						return s;
				}
			}
		}
		return null;
	}

	public static void setupFishing(Player p, Spot s) {
		if(p.getInventory().getFreeSlots() <= 0) {
			p.getPacketSender().sendMessage("You do not have any free inventory space.");
			p.getSkillManager().stopSkilling();
			return;
		}
		if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= s.getLevelReq()[0]) {
			if (p.getInventory().contains(s.getEquipment())) {
				if (s.getBait() != -1) {
					if (p.getInventory().contains(s.getBait())) {
						startFishing(p, s);
					} else {
						String baitName = ItemDefinition.forId(s.getBait()).getName();
						if(baitName.contains("Feather") || baitName.contains("worm"))
							baitName += "s";
						p.getPacketSender().sendMessage("You need some "+baitName+" to fish here.");
						p.performAnimation(new Animation(65535));
					}
				} else {
					startFishing(p, s);
				}
			} else {
				String def = ItemDefinition.forId(s.getEquipment()).getName().toLowerCase();
				p.getPacketSender().sendMessage("You need "+Misc.anOrA(def)+" "+def+" to fish here.");
			}
		} else {
			p.getPacketSender().sendMessage("You need a fishing level of at least "+s.getLevelReq()[0]+" to fish here.");
		}
	}

	public static void startFishing(final Player p, final Spot s) {
		p.getSkillManager().stopSkilling();
		final int fishIndex = Misc.getRandom(100) >= 70 ? getMax(p, s.fishingReqs) : (getMax(p, s.fishingReqs) != 0 ? getMax(p, s.fishingReqs) - 1 : 0);
		if(p.getAttributes().getCurrentInteractingObject() != null && p.getAttributes().getCurrentInteractingObject().getId() != 8702)
			p.setDirection(s == Spot.MONK_FISH ? Direction.WEST : Direction.NORTH);
		p.performAnimation(new Animation(s.getAnim()));
		p.getSkillManager().getSkillAttributes().setCurrentTask(new Task(2, p, false) {
			int cycle = 0, reqCycle = Fishing.getDelay(s.getLevelReq()[fishIndex]);
			@Override
			public void execute() {
				if(p.getInventory().getFreeSlots() == 0) {
					p.getPacketSender().sendMessage("You have run out of inventory space.");
					stop();
					return;
				}
				if(!p.getInventory().contains(s.getBait())) {
					stop();
					return;
				}
				cycle++;
				p.performAnimation(new Animation(s.getAnim()));
				if (cycle >= Misc.getRandom(1) + reqCycle) {
					String def = ItemDefinition.forId(s.getRawFish()[fishIndex]).getName();
					if(def.endsWith("s"))
						def = def.substring(0, def.length()-1);
					p.getPacketSender().sendMessage("You catch "+Misc.anOrA(def)+" "+def.toLowerCase().replace("_", " ")+".");
					if (s.getBait() != -1)
						p.getInventory().delete(s.getBait(), 1);
					p.getInventory().add(s.getRawFish()[fishIndex], 1);
					int xp = s.getXp()[fishIndex] * Skill.FISHING.getExperienceMultiplier();
					if(s.getRawFish()[fishIndex] == 383) {
						xp += 400;
						if(p.getAttributes().getCurrentInteractingObject() != null && p.getAttributes().getCurrentInteractingObject().getId() != 8702)
							xp += 500;
					} else if(s.getRawFish()[fishIndex] == 389)
						xp += 650;
					p.getSkillManager().addExperience(Skill.FISHING, xp, false);
					setupFishing(p, s);
					setEventRunning(false);
				}
			}
			@Override
			public void stop() {
				setEventRunning(false);
				p.performAnimation(new Animation(65535));
			}
		});
		
		TaskManager.submit(p.getSkillManager().getSkillAttributes().getCurrentTask());
	}

	public static int getMax(Player p, int[] reqs) {
		int tempInt = -1;
		for (int i : reqs) {
			if (p.getSkillManager().getCurrentLevel(Skill.FISHING) >= i) {
				tempInt++;
			}
		}
		return tempInt;
	}

	private static int getDelay(int req) {
		int timer = 1;
		timer += (int) req * 0.08;
		return timer;
	}

}
