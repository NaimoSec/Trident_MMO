package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Damage;
import org.trident.model.Flag;
import org.trident.model.Graphic;
import org.trident.model.Prayerbook;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

public class DragonScimitarSpecialAttack extends SpecialAttack {

	@Override
	public Animation getAnimation() {
		return ANIMATION;
	}

	@Override
	public Graphic getGraphic() {
		return GRAPHIC;
	}

	@Override
	public double getSpecialAmount() {
		return 5.5;
	}

	@Override
	public void specialAction(Player player, final GameCharacter victim, Damage damage) {
		if(!victim.isPlayer())
			return;
		final Player player2 = (Player) victim;
		final boolean[] active = player2.getPrayerActive();
		if (player2.getAttributes().getPrayerbook() == Prayerbook.NORMAL) {
			if (active[PrayerHandler.PROTECT_FROM_MAGIC] ||
					active[PrayerHandler.PROTECT_FROM_MELEE] || active[PrayerHandler.PROTECT_FROM_MISSILES]) {
				PrayerHandler.resetPrayers(player2, PrayerHandler.OVERHEAD_PRAYERS, -1);
			}
		} else if (player2.getAttributes().getPrayerbook() == Prayerbook.CURSES) {
			if (active[CurseHandler.DEFLECT_MISSILES])
				CurseHandler.deactivateCurse(player2, CurseHandler.DEFLECT_MISSILES);
			if (active[CurseHandler.DEFLECT_MELEE])
				CurseHandler.deactivateCurse(player2, CurseHandler.DEFLECT_MELEE);
			if (active[CurseHandler.DEFLECT_MAGIC])
				CurseHandler.deactivateCurse(player2, CurseHandler.DEFLECT_MAGIC);
			if (active[CurseHandler.DEFLECT_SUMMONING])
				CurseHandler.deactivateCurse(player2, CurseHandler.DEFLECT_SUMMONING);
		}
		player2.getAttributes().setPrayerInjured(true);
		player2.getPacketSender().sendMessage("You have been injured.");
		player2.getAppearance().setHeadHint(-1);
		player2.getUpdateFlag().flag(Flag.APPEARANCE);
		TaskManager.submit(new Task(8, false) {
			@Override
			public void execute() {
				player2.getAttributes().setPrayerInjured(false);
				stop();
			}
		});
	}

	private static final Animation ANIMATION = new Animation(12031);

	private static final Graphic GRAPHIC = new Graphic(2118);
}
