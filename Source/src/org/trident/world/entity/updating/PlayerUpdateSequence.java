package org.trident.world.entity.updating;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;

import org.trident.world.World;
import org.trident.world.WorldUpdateSequence;
import org.trident.world.entity.player.Player;

public class PlayerUpdateSequence implements WorldUpdateSequence<Player> {

	/** Used to block the game thread until updating is completed. */
	private final Phaser synchronizer;
	/** The thread pool that will update players in parallel. */
	private final ExecutorService updateExecutor;

	/**
	 * Create a new {@link PlayerUpdateSequence}.
	 *
	 * @param synchronizer
	 * used to block the game thread until updating is completed.
	 * @param updateExecutor
	 * the thread pool that will update players in parallel.
	 */
	public PlayerUpdateSequence(Phaser synchronizer,
			ExecutorService updateExecutor) {
		this.synchronizer = synchronizer;
		this.updateExecutor = updateExecutor;
	}

	@Override
	public void executePreUpdate(Player t) {
		try {
			if(t.getAttributes().getWalkToAction() != null)
				t.getAttributes().getWalkToAction().tick();
			t.getMovementQueue().pulse();
			t.process();
		} catch (Exception e) {
			e.printStackTrace();
			World.deregister(t);
		}
	}

	@Override
	public void executeUpdate(Player t) {
		updateExecutor.execute(() -> {
			try {
				//synchronized (t) {
					PlayerUpdating.update(t);
					NPCUpdating.update(t);
				//}
			} catch (Exception e) {
				e.printStackTrace();
				World.deregister(t);
			} finally {
				synchronizer.arriveAndDeregister();
			}
		});
	}

	@Override
	public void executePostUpdate(Player t) {
		try {
			PlayerUpdating.resetFlags(t);
		} catch (Exception e) {
			e.printStackTrace();
			World.deregister(t);
		}
	}
}
