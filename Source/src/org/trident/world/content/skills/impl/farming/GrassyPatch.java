package org.trident.world.content.skills.impl.farming;

import java.util.Calendar;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.entity.player.Player;

public class GrassyPatch {
	public byte stage = 0;
	public int minute;
	public int hour;
	public int day;
	public int year;

	public void setTime() {
		minute = Calendar.getInstance().get(12);
		hour = Calendar.getInstance().get(11);
		day = Calendar.getInstance().get(6);
		year = Calendar.getInstance().get(1);
	}

	public boolean isRaked() {
		return stage == 3;
	}

	public void process(Player player, int index) {
		if (stage == 0)
			return;
		int elapsed = Misc.getMinutesElapsed(minute, hour, day, year);
		int grow = 4;

		if (elapsed >= grow) {
			for (int i = 0; i < elapsed / grow; i++) {
				if (stage == 0) {
					return;
				}

				stage = ((byte) (stage - 1));
			}
			player.getAdvancedSkills().getFarming().doConfig();
			setTime();
		}
	}

	public void click(Player player, int option, int index) {
		if (option == 1)
			rake(player, index);
	}

	boolean raking = false;
	public void rake(final Player p, final int index) {
		if(raking)
			return;
		if (isRaked()) {
			p.getPacketSender().sendMessage("This plot is fully raked. Try planting a seed.");
			return;
		}
		if (!p.getInventory().contains(5341)) {
			p.getPacketSender().sendMessage("This patch needs to be raked before anything can grow in it.");
			p.getPacketSender().sendMessage("You do not have a rake in your inventory.");
			return;
		}
		raking = true;
		p.getSkillManager().stopSkilling();
		p.performAnimation(new Animation(2273));
		p.getSkillManager().getSkillAttributes().setCurrentTask(new Task(1, p, true) {
			int delay = 0;
			@Override
			public void execute() {
				if (!p.getInventory().contains(5341)) {
					p.getPacketSender().sendMessage("This patch needs to be raked before anything can grow in it.");
					p.getPacketSender().sendMessage("You do not have a rake in your inventory.");
					stop();
					return;
				}
				if(p.getInventory().getFreeSlots() == 0) {
					p.getInventory().full();
					stop();
					return;
				}
				p.performAnimation(new Animation(2273));
				if(delay >= 2 + Misc.getRandom(2)) {
					setTime();
					GrassyPatch grassyPatch = GrassyPatch.this;
					grassyPatch.stage = ((byte) (grassyPatch.stage + 1));
					p.getAttributes().setShouldProcessFarming(true);
					grassyPatch.doConfig(p, index);
					p.getSkillManager().addExperience(Skill.FARMING, Misc.getRandom(2), false);
					p.getInventory().add(6055, 1);
					if (isRaked()) {
						p.getPacketSender().sendMessage("The plot is now fully raked.");
						stop();
					}
					delay = 0;
				}
				delay++;
			}
			
			@Override
			public void stop() {
				raking = false;
				setEventRunning(false);
				p.performAnimation(new Animation(65535));
			}
		});
		TaskManager.submit(p.getSkillManager().getSkillAttributes().getCurrentTask());
	}

	public void doConfig(Player p, int index) {
		p.getAdvancedSkills().getFarming().doConfig();
	}

	public int getConfig(int index) {
		return stage * FarmingPatches.values()[index].mod;
	}
}
