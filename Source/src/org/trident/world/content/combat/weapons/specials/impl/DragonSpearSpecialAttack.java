package org.trident.world.content.combat.weapons.specials.impl;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.movement.MovementStatus;
import org.trident.world.content.combat.weapons.specials.SpecialAttack;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

public class DragonSpearSpecialAttack extends SpecialAttack {

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
		return 2.5;
	}

	@Override
	public boolean modifyDamage() {
		return true;
	}

	@Override
	public void specialAction(final Player player, final GameCharacter victim, Damage damage) {
		if(victim.getSize() > 1 || victim.isNpc() && CustomNPC.isCustomNPC(((NPC)victim).getId())) {
			player.getPacketSender().sendMessage("Your Special attack had no effect on this creature.");
			return;
		}
		int moveX = victim.getPosition().getX() - player.getPosition().getX();
		int moveY = victim.getPosition().getY() - player.getPosition().getY();
		if (moveX > 0)
			moveX = 1;
		else if (moveX < 0)
			moveX = -1;
		if (moveY > 0)
			moveY = 1;
		else if (moveY < 0)
			moveY = -1;
		victim.performGraphic(new Graphic(254, GraphicHeight.HIGH));
		if(victim.getMovementQueue().canWalk(moveX, moveY)) {
			victim.setEntityInteraction(player);
			victim.getMovementQueue().stopMovement();
			victim.getMovementQueue().walkStep(moveX, moveY);
			victim.getMovementQueue().setMovementStatus(MovementStatus.CANNOT_MOVE);
		}
		victim.getCombatAttributes().setStunned(true);
		if(victim.isPlayer())
			((Player)victim).getPacketSender().sendMessage("You've been stunned!");
		//CombatHandler.closeDistance(player, victim);
		TaskManager.submit(new Task(4, false) {
			@Override
			public void execute() {
				victim.getCombatAttributes().setStunned(false);
				victim.getMovementQueue().setMovementStatus(MovementStatus.NONE).stopMovement();;
				stop();
			}
		});
	}

	private static final Animation ANIMATION = new Animation(13045);

	private static final Graphic GRAPHIC = null;
}
