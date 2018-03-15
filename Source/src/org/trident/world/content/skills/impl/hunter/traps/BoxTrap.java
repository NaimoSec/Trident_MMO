package org.trident.world.content.skills.impl.hunter.traps;

import org.trident.model.GameObject;
import org.trident.world.content.skills.impl.hunter.Trap;
import org.trident.world.entity.player.Player;

/**
 * 
 * @author Faris
 */
public class BoxTrap extends Trap {

	private TrapState state;

	public BoxTrap(GameObject obj, TrapState state, int ticks, Player p) {
		super(obj, state, ticks, p);
	}

	/**
	 * @return the state
	 */
	public TrapState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(TrapState state) {
		this.state = state;
	}

}
