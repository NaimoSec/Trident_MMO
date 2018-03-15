package org.trident.world.entity.npc.custom.impl;

import org.trident.model.Position;
import org.trident.world.World;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCAttributes;

/**
 * 
 * @author Brian
 *
 */
public class Crazy_Archaelogist extends NPC {
	
	public Crazy_Archaelogist() {
		super(NPC_ID, new Position(3089 , 3503));
		setAttributes(new NPCAttributes().setAggressive(true).setAttackable(true).setRespawn(true).setWalkingDistance(3).setAttackLevel(180).setDefenceLevel(240).setStrengthLevel(250).setConstitution(800).setAbsorbMelee(20).setAbsorbRanged(20).setAbsorbMagic(20).setAttackSpeed(6).setMaxHit(250));
		World.register(this);
	}
	
	public static final int NPC_ID = 15312;
	
	private static final String[] ATTACK_MESSAGES = new String[] {
		"I'm Bellock - respect me!",
		"Get off my site!",
		"No-one messes with Bellock's dig!",
		"These ruins are mine!",
		"Taste my knowledge!",
		"You belong in a museum!" };

}
