package org.trident.world.content.dialogue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.trident.model.definitions.NPCDefinition;
import org.trident.util.XStreamLibrary;
import org.trident.world.entity.player.Player;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Manages the loading and start of dialogues.
 * 
 * @author relex lawl
 */

public class DialogueManager {

	/**
	 * The directory where the dialogues file is located.
	 */
	private static final String FILE_DIRECTORY = "./data/config/dialogues.xml";
	
	/**
	 * Contains all dialogues loaded from said file.
	 */
	public static Map<Integer, Dialogue> dialogues = new HashMap<Integer, Dialogue>();

	/**
	 * Parses the information from the dialogue file.
	 */
	public static void init() {
		try {
			File fXmlFile = new File(FILE_DIRECTORY);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("dialogue");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) nNode;
					try {
						final int id = Integer.valueOf(XStreamLibrary.getEntry("id", element));
						final DialogueType type = DialogueType.valueOf(XStreamLibrary.getEntry("type", element));
						DialogueExpression dialogueExpression = null;
						if (type == DialogueType.NPC_STATEMENT || type == DialogueType.PLAYER_STATEMENT)
							dialogueExpression = DialogueExpression.valueOf(XStreamLibrary.getEntry("animation", element));
						final DialogueExpression animation = dialogueExpression;
						final int totalLines = Integer.valueOf(XStreamLibrary.getEntry("lines", element));
						String[] lines = new String[totalLines];
						for (int i = 0; i < lines.length; i++) {
							lines[i] = XStreamLibrary.getEntry("line" + (i + 1), element);
						}
						int npc = -1, item = -1, zoom = -1;
						String header = null;
						if (type == DialogueType.NPC_STATEMENT) {
							npc = Integer.valueOf(XStreamLibrary.getEntry("npcId", element));
						} else if (type == DialogueType.ITEM_STATEMENT) {
							item = Integer.valueOf(XStreamLibrary.getEntry("itemId", element));
							zoom = Integer.valueOf(XStreamLibrary.getEntry("itemZoom", element));
							header = XStreamLibrary.getEntry("header", element);
						}
						final int nextDialogue = Integer.valueOf(XStreamLibrary.getEntry("next", element));
						final int npcId = npc, itemId = item, itemZoom = zoom;
						final String dialogueHeader = header;
						final String[] finalLines = lines;
						Dialogue dialogue = new Dialogue() {
							@Override
							public int id() {
								return id;
							}
							
							@Override
							public DialogueType type() {
								return type;
							}
							
							@Override
							public DialogueExpression animation() {
								return animation;
							}
							
							@Override
							public String[] dialogue() {
								return finalLines;
							}

							@Override
							public int nextDialogueId() {
								return nextDialogue;
							}

							@Override
							public int npcId() {
								return npcId;
							}
							
							@Override
							public String[] item() {
								return new String[] {
									String.valueOf(itemId),
									String.valueOf(itemZoom),
									dialogueHeader
								};
							}
						};
						dialogues.put(id, dialogue);
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts a dialogue gotten from the dialogues map.
	 * @param player	The player to dialogue with.
	 * @param id		The id of the dialogue to retrieve from dialogues map.
	 */
	public static void start(Player player, int id) {
		Dialogue dialogue = dialogues.get(id);
		start(player, dialogue);
	}
	
	/**
	 * Starts a dialogue.
	 * @param player	The player to dialogue with.	
	 * @param dialogue	The dialogue to show the player.
	 */
	public static void start(Player player, Dialogue dialogue) {
		player.getAttributes().setDialogue(dialogue);
		if(player.getAttributes().isBanking() || player.getTrading().inTrade() || player.getAttributes().isPriceChecking() || player.getAttributes().isShopping() || player.getAdvancedSkills().getSummoning().isStoring() || player.getAttributes().getInterfaceId() > 0 && player.getAttributes().getInterfaceId() != 50)
			player.getPacketSender().sendInterfaceRemoval();
		if (dialogue == null || dialogue.id() < 0) {
			player.getPacketSender().sendInterfaceRemoval();
		} else {
			showDialogue(player, dialogue);
			dialogue.specialAction();
		}
		if(player.getAttributes().getInterfaceId() != 50)
			player.getAttributes().setInterfaceId(50);
	}
	
	/**
	 * Handles the clicking of 'click here to continue', option1, option2 and so on.
	 * @param player	The player who will continue the dialogue.
	 */
	public static void next(Player player) {
		if (player.getAttributes().getDialogue() == null) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		Dialogue next = player.getAttributes().getDialogue().nextDialogue();
		if (next == null)
			next = dialogues.get(player.getAttributes().getDialogue().nextDialogueId());
		if (next == null || next.id() < 0) {
			player.getPacketSender().sendInterfaceRemoval();
			return;
		}
		start(player, next);
	}

	/**
	 * Configures the dialogue's type and shows the dialogue interface
	 * and sets its child id's.
	 * @param player		The player to show dialogue for.
	 * @param dialogue		The dialogue to show.
	 */
	private static void showDialogue(Player player, Dialogue dialogue) {
		String[] lines = dialogue.dialogue();
		switch (dialogue.type()) {
		case NPC_STATEMENT:
			int startDialogueChildId = NPC_DIALOGUE_ID[lines.length - 1];
			int headChildId = startDialogueChildId - 2;
			int npcId = dialogue.npcId() > 0 ? dialogue.npcId() : player.getAttributes().getCurrentInteractingNPC() != null && !player.getAttributes().getCurrentInteractingNPC().getAttributes().isAttackable() ? player.getAttributes().getCurrentInteractingNPC().getId() : -1;
			player.getPacketSender().sendNpcHeadOnInterface(npcId, headChildId);
			player.getPacketSender().sendInterfaceAnimation(headChildId, dialogue.animation().getAnimation());
			player.getPacketSender().sendString(startDialogueChildId - 1, NPCDefinition.forId(npcId) != null ? NPCDefinition.forId(npcId).getName().replaceAll("_", " ") : "");
			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString(startDialogueChildId + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
			break;
		case PLAYER_STATEMENT:
			startDialogueChildId = PLAYER_DIALOGUE_ID[lines.length - 1];
			headChildId = startDialogueChildId - 2;
			player.getPacketSender().sendPlayerHeadOnInterface(headChildId);
			player.getPacketSender().sendInterfaceAnimation(headChildId, dialogue.animation().getAnimation());
			player.getPacketSender().sendString(startDialogueChildId - 1, player.getUsername());
			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString(startDialogueChildId + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
			break;
		case ITEM_STATEMENT:
			startDialogueChildId = NPC_DIALOGUE_ID[lines.length - 1];
			headChildId = startDialogueChildId - 2;
			player.getPacketSender().sendInterfaceModel(headChildId, Integer.valueOf(dialogue.item()[0]), Integer.valueOf(dialogue.item()[1]));
			player.getPacketSender().sendString(startDialogueChildId - 1, dialogue.item()[2]);
			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString(startDialogueChildId + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(startDialogueChildId - 3);
			break;
		case STATEMENT:
			sendStatement(player, dialogue.dialogue()[0]);
			break;
		case OPTION:
			int firstChildId = OPTION_DIALOGUE_ID[lines.length - 1];
			player.getPacketSender().sendString(firstChildId - 1, "Choose an option");
			for (int i = 0; i < lines.length; i++) {
				player.getPacketSender().sendString(firstChildId + i, lines[i]);
			}
			player.getPacketSender().sendChatboxInterface(firstChildId - 2);
			break;
		}
		if(player.getAttributes().getInterfaceId() <= 0)
			player.getAttributes().setInterfaceId(100);
	}
	
	public static void sendStatement(Player p, String statement) {
		p.getPacketSender().sendString(357, statement);
		p.getPacketSender().sendString(358, "Click here to continue");
		p.getPacketSender().sendChatboxInterface(356);
	}
	
	/**
	 * Gets an empty id for a dialogue.
	 * @return	An empty index from the map or the map's size itself.
	 */
	public static int getDefaultId() {
		int id = dialogues.size();
		for (int i = 0; i < dialogues.size(); i++) {
			if (dialogues.get(i) == null) {
				id = i;
				break;
			}
		}
		return id;
	}
	
	/**
	 * Retrieves the dialogues map.
	 * @return	dialogues.
	 */
	public static Map<Integer, Dialogue> getDialogues() {
		return dialogues;
	}
	
	/**
	 * This array contains the child id where the dialogue
	 * statement starts for npc and item dialogues.
	 */
	private static final int[] NPC_DIALOGUE_ID = {
		4885,
		4890,
		4896,
		4903
	};
	
	/**
	 * This array contains the child id where the dialogue
	 * statement starts for player dialogues.
	 */
	private static final int[] PLAYER_DIALOGUE_ID = {
		971,
		976,
		982,
		989
	};
	
	/**
	 * This array contains the child id where the dialogue
	 * statement starts for option dialogues.
	 */
	private static final int[] OPTION_DIALOGUE_ID = {
		13760,
		2461,
		2471,
		2482,
		2494,
	};
}
