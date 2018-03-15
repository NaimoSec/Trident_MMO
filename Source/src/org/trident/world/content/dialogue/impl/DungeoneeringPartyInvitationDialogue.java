package org.trident.world.content.dialogue.impl;

import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueType;
import org.trident.world.entity.player.Player;

/**
 * Represents a random default npc dialogue.
 * 
 * @author relex lawl
 */

public class DungeoneeringPartyInvitationDialogue extends Dialogue {

	public DungeoneeringPartyInvitationDialogue(Player inviter, Player p) {
		this.inviter = inviter;
		this.p = p;
	}
	
	private Player inviter, p;
	
	@Override
	public DialogueType type() {
		return DialogueType.STATEMENT;
	}

	@Override
	public DialogueExpression animation() {
		return null;
	}

	@Override
	public String[] dialogue() {
		return new String[] {
			""+inviter.getUsername()+" has invited you to join their Dungeoneering party."
		};
	}
	
	@Override
	public int npcId() {
		return -1;
	}
	
	@Override
	public Dialogue nextDialogue() {
		return new Dialogue() {

			@Override
			public DialogueType type() {
				return DialogueType.OPTION;
			}

			@Override
			public DialogueExpression animation() {
				return null;
			}

			@Override
			public String[] dialogue() {
				return new String[] {"Join "+inviter.getUsername()+"'s party", "Don't join "+inviter.getUsername()+"'s party."};
			}
			
			@Override
			public void specialAction() {
				p.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().setPartyInvitation(inviter.getSkillManager().getSkillAttributes().getDungeoneeringAttributes().getDungeoneeringFloor().getParty());
				p.getAttributes().setDialogueAction(360);
			}
			
		};
	}
}