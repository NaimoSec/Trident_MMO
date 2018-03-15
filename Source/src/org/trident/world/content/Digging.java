package org.trident.world.content;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Position;
import org.trident.world.content.treasuretrails.CoordinateScrolls;
import org.trident.world.content.treasuretrails.DiggingScrolls;
import org.trident.world.content.treasuretrails.MapScrolls;
import org.trident.world.entity.player.Player;

public class Digging {
	
	public static void dig(final Player player) {
		if(System.currentTimeMillis() - player.getAttributes().getClickDelay() <= 2000)
			return;
		player.getMovementQueue().stopMovement();
		player.getPacketSender().sendMessage("You start digging..");
		player.performAnimation(new Animation(830));
		TaskManager.submit(new Task(2, player, false) {
			@Override
			public void execute() {
				/**
				 * Clue scrolls
				 */
				if (/*ClueScrolls.digSpot(player)*/DiggingScrolls.digClue(player) || MapScrolls.digClue(player) || CoordinateScrolls.digClue(player)) {
					stop();
					return;
				}
				Position targetPosition = null;
				/**
				 * Barrows
				 */
				if (inArea(player.getPosition(), 3553, 3301, 3561, 3294))
					targetPosition = new Position(3578, 9706, -1);
				else if (inArea(player.getPosition(), 3550, 3287, 3557, 3278))
					targetPosition = new Position(3568, 9683, -1);
				else if (inArea(player.getPosition(), 3561, 3292, 3568, 3285))
					targetPosition = new Position(3557, 9703, -1);
				else if (inArea(player.getPosition(), 3570, 3302, 3579, 3293))
					targetPosition = new Position(3556, 9718, -1);
				else if (inArea(player.getPosition(), 3571, 3285, 3582, 3278))
					targetPosition = new Position(3534, 9704, -1);
				else if (inArea(player.getPosition(), 3562, 3279, 3569, 3273))
					targetPosition = new Position(3546, 9684, -1);
				else if (inArea(player.getPosition(), 2986, 3370, 3013, 3388))
					targetPosition = new Position(3546, 9684, -1);
				if(targetPosition != null)
					player.moveTo(targetPosition);
				else
					player.getPacketSender().sendMessage("You find nothing of interest.");
				targetPosition = null;
				stop();
			}
		});
		player.getAttributes().setClickDelay(System.currentTimeMillis());
	}

	private static boolean inArea(Position pos, int x, int y, int x1, int y1) {
		return pos.getX() > x && pos.getX() < x1 && pos.getY() < y && pos.getY() > y1;
	}
}
