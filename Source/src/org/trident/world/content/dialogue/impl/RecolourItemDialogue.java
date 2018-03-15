package org.trident.world.content.dialogue.impl;

import org.trident.model.Item;
import org.trident.model.definitions.ItemDefinition;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.dialogue.DialogueExpression;
import org.trident.world.content.dialogue.DialogueType;

/**
 * Represents a recolor item dialogue, used for barbarian assault
 * for items such as abyssal whip, dark bows and staves of light.
 * 
 * @author relex lawl
 */

public class RecolourItemDialogue extends Dialogue {
	
	/**
	 * The RecolourItemDialogue constructor.
	 * @param item		The item to show in the dialogue.
	 * @param zoom		The zoom of the item.
	 * @param title		The title of the dialogue.
	 */
	public RecolourItemDialogue(Item item, int zoom, String title) {
		this.item = item;
		this.zoom = zoom;
		this.title = title;
	}
	
	/**
	 * The item to show in the dialogue.
	 */
	private final Item item;
	
	/**
	 * The zoom of the item.
	 */
	private final int zoom;
	
	/**
	 * The title of the dialogue.
	 */
	private final String title;

	@Override
	public DialogueType type() {
		return DialogueType.ITEM_STATEMENT;
	}

	@Override
	public DialogueExpression animation() {
		return null;
	}

	@Override
	public String[] dialogue() {
		return new String[] {
			"You have recoloured your " + ItemDefinition.forId(item.getId()).getName() + "."
		};
	}
	
	@Override
	public String[] item() {
		return new String[] {
			String.valueOf(item.getId()),
			String.valueOf(zoom),
			title
		};
	}
}
