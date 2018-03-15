package org.trident.world.content.skills;

import org.trident.world.content.skills.impl.farming.Farming;
import org.trident.world.content.skills.impl.slayer.Slayer;
import org.trident.world.content.skills.impl.summoning.Summoning;
import org.trident.world.entity.player.Player;

public class AdvancedSkills {

	public AdvancedSkills(Player p) {
		farming = new Farming(p);
		summoning = new Summoning(p);
		slayer = new Slayer(p);
	}
	
	private final Farming farming;
	private final Summoning summoning;
	private final Slayer slayer;
	
	public Farming getFarming() {
		return farming;
	}
	
	public Summoning getSummoning() {
		return summoning;
	}
	
	public Slayer getSlayer() {
		return slayer;
	}
}
