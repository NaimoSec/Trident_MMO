package org.trident.world.content;

import java.util.ArrayList;

import org.trident.model.Item;
import org.trident.util.Misc;
import org.trident.world.entity.player.Player;

/***
 * Handles all skill guides
 * @author Gabbe
 */
public class SkillGuides {

	public static boolean handleInterface(Player player, int button) {
		if(player.getAttributes().getSkillGuideInterfaceData() != null) {
			for(SkillGuideInterfaceContent t : player.getAttributes().getSkillGuideInterfaceData().getContent()) {
				if(t.type.childId == button) {
					openInterface(player, player.getAttributes().getSkillGuideInterfaceData(), t.type);
					return true;
				}
			}
		}
		return false;
	}

	public static void openInterface(Player player, SkillGuideInterfaceData interfaceContent, Type type) {
		if(player.getAttributes().getSkillGuideInterfaceData() == null && (interfaceContent == SkillGuideInterfaceData.ATTACK || interfaceContent == SkillGuideInterfaceData.STRENGTH)) {
			player.getPacketSender().sendMessage("@red@Please note that some equipment requires more levels than the one listed above.").sendMessage("@red@Examine an item if you wish to see if you can wield it. If not, you will be prompted.");
		}
		player.getAttributes().setSkillGuideInterfaceData(interfaceContent);
		player.getPacketSender().sendString(8716, "@dre@Skill: "+Misc.formatText(interfaceContent.toString().toLowerCase())).sendString(8719,"").sendString(8718,"").sendString(8846,"").sendString(8823,"").sendString(8824,"").sendString(8827,"").sendString(8837,"").sendString(8840,"").sendString(8843,"").sendString(8859,"").sendString(8862,"").sendString(8865,"").sendString(15303,"").sendString(15306,"").sendString(15309,"").sendString(8718, "Level:").sendString(8719, "Item:");;
		for(int i = 8720; i < 8800; i++)
			player.getPacketSender().sendString(i, "");
		/*
		 * Sending items on interface
		 */
		int lastItemNameIndex = 8760, levelReqChild = 8720, levelReqIndex = 0;
		ArrayList<Item> itemList = new ArrayList<Item>();
		for(SkillGuideInterfaceContent i : interfaceContent.getContent()) {
			if(i.type == type) {
				itemList.add(new Item(i.item));
				player.getPacketSender().sendString(lastItemNameIndex, ""+itemList.get(lastItemNameIndex-8760).getDefinition().getName()).sendString(levelReqChild, ""+interfaceContent.getContent()[levelReqIndex].levelReq);
				lastItemNameIndex++; levelReqChild++;
			}
			levelReqIndex++;
		}
		String s = "@or1@"+type.toSend+"";
		player.getPacketSender().sendInterfaceItems(8847, itemList).sendString(8849, s);
		Type lastType = null;
		for(SkillGuideInterfaceContent con : interfaceContent.getContent()) {
			if(lastType == null || con.type != lastType) {
				s = type != null && con.type == type ? "@or1@" : "";
				player.getPacketSender().sendString(con.type.childId, s + con.type.toSend);
				lastType = con.type;
			}
		}
		if(player.getAttributes().getInterfaceId() != 8714)
			player.getPacketSender().sendInterface(8714);
	}

	public enum Type {
		ATTACK_HALBERDS("Halberds", 8846),
		ATTACK_DAGGERS("Daggers", 8823),
		ATTACK_CLAWS("Claws", 8824),
		ATTACK_SWORDS("Swords", 8827),
		ATTACK_L_SWORDS("L. Swords", 8837),
		ATTACK_SCIMITARS("Scimitars", 8840),
		ATTACK_TWO_HANDED_SWORDS("2H Swords", 8843),
		ATTACK_HATCHETS("Hatchets", 8859),
		ATTACK_PICKAXES("Pickaxes", 8862),
		ATTACK_BARROWS("Barrows", 8865),	
		ATTACK_OBSIDIAN("Obsidian", 15303),
		ATTACK_GODSWORDS("Godswords", 15306),
		ATTACK_MISC("Misc.", 15309),


		STRENGTH_HALBERDS("Halberds", 8846),
		STRENGTH_GRANITE("Granite", 8823),
		STRENGTH_BARROWS("Barrows", 8824),

		DEFENCE_BRONZE("Bronze", 8846),
		DEFENCE_IRON("Iron", 8823),
		DEFENCE_STEEL("Steel", 8824),
		DEFENCE_BLACK("Black", 8827),
		DEFENCE_MITHRIL("Mithril", 8837),
		DEFENCE_INITIATE("Initiate", 8840),
		DEFENCE_ADAMANT("Adamant", 8843),
		DEFENCE_RUNE("Rune", 8859),
		DEFENCE_DRAGON("Dragon", 8862),
		DEFENCE_BARROWS("Barrows", 8865),
		DEFENCE_RANGED("Ranged Eq.", 15303),
		DEFENCE_MAGIC("Magic Eq.", 15306),
		DEFENCE_MISC("Misc.", 15309),

		RANGED_EQUIPMENT("Equipment", 8846),
		RANGED_SHORT_BOWS("S. Bows", 8823),
		RANGED_LONG_BOWS("L. Bows", 8824),
		RANGED_CROSSBOWS("Crossbows", 8827),
		RANGED_JAVELINS("Javelins", 8837),
		RANGED_KNIVES("Knives", 8840),
		RANGED_DARTS("Darts", 8843),
		RANGED_THROWN_AXE("T. Axes", 8859),

		PRAYER_BONES("Bones", 8846);


		Type(String toSend, int childId) {
			this.toSend = toSend;
			this.childId = childId;
		}

		public String toSend;
		public int childId;
	}

	public enum SkillGuideInterfaceData {
		ATTACK(new SkillGuideInterfaceContent[] {

				new SkillGuideInterfaceContent(1, 3190, Type.ATTACK_HALBERDS), 
				new SkillGuideInterfaceContent(1, 3192, Type.ATTACK_HALBERDS), 
				new SkillGuideInterfaceContent(5, 3194, Type.ATTACK_HALBERDS), 
				new SkillGuideInterfaceContent(10, 3196, Type.ATTACK_HALBERDS), 
				new SkillGuideInterfaceContent(20, 3198, Type.ATTACK_HALBERDS), 
				new SkillGuideInterfaceContent(30, 3200, Type.ATTACK_HALBERDS), 
				new SkillGuideInterfaceContent(40, 3202, Type.ATTACK_HALBERDS),
				new SkillGuideInterfaceContent(60, 3204, Type.ATTACK_HALBERDS),

				new SkillGuideInterfaceContent(1, 1205, Type.ATTACK_DAGGERS), 
				new SkillGuideInterfaceContent(1, 1203, Type.ATTACK_DAGGERS), 
				new SkillGuideInterfaceContent(5, 1207, Type.ATTACK_DAGGERS), 
				new SkillGuideInterfaceContent(10, 1217, Type.ATTACK_DAGGERS), 
				new SkillGuideInterfaceContent(20, 1209, Type.ATTACK_DAGGERS), 
				new SkillGuideInterfaceContent(30, 1211, Type.ATTACK_DAGGERS), 
				new SkillGuideInterfaceContent(40, 1213, Type.ATTACK_DAGGERS), 
				new SkillGuideInterfaceContent(60, 1215, Type.ATTACK_DAGGERS), 

				new SkillGuideInterfaceContent(1, 3095, Type.ATTACK_CLAWS), 
				new SkillGuideInterfaceContent(1, 3096, Type.ATTACK_CLAWS), 
				new SkillGuideInterfaceContent(5, 3097, Type.ATTACK_CLAWS),
				new SkillGuideInterfaceContent(10, 3098, Type.ATTACK_CLAWS), 
				new SkillGuideInterfaceContent(20, 3099, Type.ATTACK_CLAWS),
				new SkillGuideInterfaceContent(30, 3100, Type.ATTACK_CLAWS),
				new SkillGuideInterfaceContent(40, 3101, Type.ATTACK_CLAWS),
				new SkillGuideInterfaceContent(60, 14484, Type.ATTACK_CLAWS),

				new SkillGuideInterfaceContent(1, 1277, Type.ATTACK_SWORDS), 
				new SkillGuideInterfaceContent(1, 1279, Type.ATTACK_SWORDS), 
				new SkillGuideInterfaceContent(5, 1281, Type.ATTACK_SWORDS), 
				new SkillGuideInterfaceContent(10, 1283, Type.ATTACK_SWORDS), 
				new SkillGuideInterfaceContent(20, 1285, Type.ATTACK_SWORDS), 
				new SkillGuideInterfaceContent(30, 1287, Type.ATTACK_SWORDS), 
				new SkillGuideInterfaceContent(40, 1289, Type.ATTACK_SWORDS), 

				new SkillGuideInterfaceContent(1, 1291, Type.ATTACK_L_SWORDS), 
				new SkillGuideInterfaceContent(1, 1293, Type.ATTACK_L_SWORDS), 
				new SkillGuideInterfaceContent(5, 1295, Type.ATTACK_L_SWORDS), 
				new SkillGuideInterfaceContent(10, 1297, Type.ATTACK_L_SWORDS), 
				new SkillGuideInterfaceContent(20, 1299, Type.ATTACK_L_SWORDS), 
				new SkillGuideInterfaceContent(30, 1301, Type.ATTACK_L_SWORDS), 
				new SkillGuideInterfaceContent(40, 1303, Type.ATTACK_L_SWORDS), 
				new SkillGuideInterfaceContent(60, 1305, Type.ATTACK_L_SWORDS), 


				new SkillGuideInterfaceContent(1, 1321, Type.ATTACK_SCIMITARS),
				new SkillGuideInterfaceContent(1, 1323, Type.ATTACK_SCIMITARS),
				new SkillGuideInterfaceContent(5, 1325, Type.ATTACK_SCIMITARS),
				new SkillGuideInterfaceContent(10, 1327, Type.ATTACK_SCIMITARS),
				new SkillGuideInterfaceContent(20, 1329, Type.ATTACK_SCIMITARS),
				new SkillGuideInterfaceContent(30, 1331, Type.ATTACK_SCIMITARS),
				new SkillGuideInterfaceContent(40, 1333, Type.ATTACK_SCIMITARS),
				new SkillGuideInterfaceContent(60, 4587, Type.ATTACK_SCIMITARS),

				new SkillGuideInterfaceContent(1, 1307, Type.ATTACK_TWO_HANDED_SWORDS),
				new SkillGuideInterfaceContent(1, 1309, Type.ATTACK_TWO_HANDED_SWORDS),
				new SkillGuideInterfaceContent(5, 1311, Type.ATTACK_TWO_HANDED_SWORDS),
				new SkillGuideInterfaceContent(10, 1313, Type.ATTACK_TWO_HANDED_SWORDS),
				new SkillGuideInterfaceContent(20, 1315, Type.ATTACK_TWO_HANDED_SWORDS),
				new SkillGuideInterfaceContent(30, 1317, Type.ATTACK_TWO_HANDED_SWORDS),
				new SkillGuideInterfaceContent(40, 1319, Type.ATTACK_TWO_HANDED_SWORDS),
				new SkillGuideInterfaceContent(60, 7158, Type.ATTACK_TWO_HANDED_SWORDS),

				new SkillGuideInterfaceContent(1, 1351, Type.ATTACK_HATCHETS),
				new SkillGuideInterfaceContent(1, 1349, Type.ATTACK_HATCHETS),
				new SkillGuideInterfaceContent(10, 1353, Type.ATTACK_HATCHETS),
				new SkillGuideInterfaceContent(10, 1361, Type.ATTACK_HATCHETS),
				new SkillGuideInterfaceContent(20, 1355, Type.ATTACK_HATCHETS),
				new SkillGuideInterfaceContent(30, 1357, Type.ATTACK_HATCHETS),
				new SkillGuideInterfaceContent(40, 1359, Type.ATTACK_HATCHETS),
				new SkillGuideInterfaceContent(60, 6739, Type.ATTACK_HATCHETS),

				new SkillGuideInterfaceContent(1, 1265, Type.ATTACK_PICKAXES),
				new SkillGuideInterfaceContent(1, 1267, Type.ATTACK_PICKAXES),
				new SkillGuideInterfaceContent(10, 1269, Type.ATTACK_PICKAXES),
				new SkillGuideInterfaceContent(20, 1273, Type.ATTACK_PICKAXES),
				new SkillGuideInterfaceContent(30, 1271, Type.ATTACK_PICKAXES),
				new SkillGuideInterfaceContent(40, 1275, Type.ATTACK_PICKAXES),
				new SkillGuideInterfaceContent(60, 15259, Type.ATTACK_PICKAXES),

				new SkillGuideInterfaceContent(70, 4710, Type.ATTACK_BARROWS),
				new SkillGuideInterfaceContent(70, 4726, Type.ATTACK_BARROWS),
				new SkillGuideInterfaceContent(70, 4755, Type.ATTACK_BARROWS),
				new SkillGuideInterfaceContent(70, 4747, Type.ATTACK_BARROWS),
				new SkillGuideInterfaceContent(70, 4718, Type.ATTACK_BARROWS),

				new SkillGuideInterfaceContent(60, 6525, Type.ATTACK_OBSIDIAN),
				new SkillGuideInterfaceContent(60, 6523, Type.ATTACK_OBSIDIAN),

				new SkillGuideInterfaceContent(75, 11694, Type.ATTACK_GODSWORDS),
				new SkillGuideInterfaceContent(75, 11696, Type.ATTACK_GODSWORDS),
				new SkillGuideInterfaceContent(75, 11698, Type.ATTACK_GODSWORDS),
				new SkillGuideInterfaceContent(75, 11700, Type.ATTACK_GODSWORDS),


				new SkillGuideInterfaceContent(50, 4153, Type.ATTACK_MISC),
				new SkillGuideInterfaceContent(70, 4151, Type.ATTACK_MISC),
		}),

		STRENGTH(new SkillGuideInterfaceContent[] {
				new SkillGuideInterfaceContent(1, 3190, Type.STRENGTH_HALBERDS), 
				new SkillGuideInterfaceContent(1, 3192, Type.STRENGTH_HALBERDS), 
				new SkillGuideInterfaceContent(5, 3194, Type.STRENGTH_HALBERDS), 
				new SkillGuideInterfaceContent(10, 3196, Type.STRENGTH_HALBERDS), 
				new SkillGuideInterfaceContent(20, 3198, Type.STRENGTH_HALBERDS), 
				new SkillGuideInterfaceContent(30, 3200, Type.STRENGTH_HALBERDS), 
				new SkillGuideInterfaceContent(40, 3202, Type.STRENGTH_HALBERDS),
				new SkillGuideInterfaceContent(60, 3204, Type.STRENGTH_HALBERDS),

				new SkillGuideInterfaceContent(50, 10589, Type.STRENGTH_GRANITE),
				new SkillGuideInterfaceContent(50, 10564, Type.STRENGTH_GRANITE),
				new SkillGuideInterfaceContent(50, 6809, Type.STRENGTH_GRANITE),
				new SkillGuideInterfaceContent(50, 4153, Type.STRENGTH_GRANITE),


				new SkillGuideInterfaceContent(70, 4718, Type.STRENGTH_BARROWS),
				new SkillGuideInterfaceContent(70, 4747, Type.STRENGTH_BARROWS),
		}),

		DEFENCE(new SkillGuideInterfaceContent[] {

				new SkillGuideInterfaceContent(1, 1139, Type.DEFENCE_BRONZE),
				new SkillGuideInterfaceContent(1, 1155, Type.DEFENCE_BRONZE),
				new SkillGuideInterfaceContent(1, 1103, Type.DEFENCE_BRONZE),
				new SkillGuideInterfaceContent(1, 1117, Type.DEFENCE_BRONZE),
				new SkillGuideInterfaceContent(1, 1087, Type.DEFENCE_BRONZE),
				new SkillGuideInterfaceContent(1, 1075, Type.DEFENCE_BRONZE),
				new SkillGuideInterfaceContent(1, 1173, Type.DEFENCE_BRONZE),
				new SkillGuideInterfaceContent(1, 1189, Type.DEFENCE_BRONZE),
				new SkillGuideInterfaceContent(1, 7454, Type.DEFENCE_BRONZE),
				new SkillGuideInterfaceContent(1, 4119, Type.DEFENCE_BRONZE),
				new SkillGuideInterfaceContent(1, 8844, Type.DEFENCE_BRONZE),

				new SkillGuideInterfaceContent(1, 1137, Type.DEFENCE_IRON),
				new SkillGuideInterfaceContent(1, 1153, Type.DEFENCE_IRON),
				new SkillGuideInterfaceContent(1, 1101, Type.DEFENCE_IRON),
				new SkillGuideInterfaceContent(1, 1115, Type.DEFENCE_IRON),
				new SkillGuideInterfaceContent(1, 1081, Type.DEFENCE_IRON),
				new SkillGuideInterfaceContent(1, 1067, Type.DEFENCE_IRON),
				new SkillGuideInterfaceContent(1, 1175, Type.DEFENCE_IRON),
				new SkillGuideInterfaceContent(1, 1191, Type.DEFENCE_IRON),
				new SkillGuideInterfaceContent(1, 7455, Type.DEFENCE_IRON),
				new SkillGuideInterfaceContent(1, 4121, Type.DEFENCE_IRON),
				new SkillGuideInterfaceContent(1, 8845, Type.DEFENCE_IRON),

				new SkillGuideInterfaceContent(5, 1141, Type.DEFENCE_STEEL),
				new SkillGuideInterfaceContent(5, 1157, Type.DEFENCE_STEEL),
				new SkillGuideInterfaceContent(5, 1105, Type.DEFENCE_STEEL),
				new SkillGuideInterfaceContent(5, 1119, Type.DEFENCE_STEEL),
				new SkillGuideInterfaceContent(5, 1083, Type.DEFENCE_STEEL),
				new SkillGuideInterfaceContent(5, 1069, Type.DEFENCE_STEEL),
				new SkillGuideInterfaceContent(5, 1177, Type.DEFENCE_STEEL),
				new SkillGuideInterfaceContent(5, 1193, Type.DEFENCE_STEEL),
				new SkillGuideInterfaceContent(5, 7456, Type.DEFENCE_STEEL),
				new SkillGuideInterfaceContent(5, 4123, Type.DEFENCE_STEEL),
				new SkillGuideInterfaceContent(5, 8846, Type.DEFENCE_STEEL),

				new SkillGuideInterfaceContent(10, 1151, Type.DEFENCE_BLACK),
				new SkillGuideInterfaceContent(10, 1165, Type.DEFENCE_BLACK),
				new SkillGuideInterfaceContent(10, 1107, Type.DEFENCE_BLACK),
				new SkillGuideInterfaceContent(10, 1125, Type.DEFENCE_BLACK),
				new SkillGuideInterfaceContent(10, 1089, Type.DEFENCE_BLACK),
				new SkillGuideInterfaceContent(10, 1077, Type.DEFENCE_BLACK),
				new SkillGuideInterfaceContent(10, 1179, Type.DEFENCE_BLACK),
				new SkillGuideInterfaceContent(10, 1195, Type.DEFENCE_BLACK),
				new SkillGuideInterfaceContent(10, 7457, Type.DEFENCE_BLACK),
				new SkillGuideInterfaceContent(10, 4125, Type.DEFENCE_BLACK),
				new SkillGuideInterfaceContent(10, 8847, Type.DEFENCE_BLACK),


				new SkillGuideInterfaceContent(20, 1143, Type.DEFENCE_MITHRIL),
				new SkillGuideInterfaceContent(20, 1159, Type.DEFENCE_MITHRIL),
				new SkillGuideInterfaceContent(20, 1109, Type.DEFENCE_MITHRIL),
				new SkillGuideInterfaceContent(20, 1121, Type.DEFENCE_MITHRIL),
				new SkillGuideInterfaceContent(20, 1085, Type.DEFENCE_MITHRIL),
				new SkillGuideInterfaceContent(20, 1071, Type.DEFENCE_MITHRIL),
				new SkillGuideInterfaceContent(20, 1181, Type.DEFENCE_MITHRIL),
				new SkillGuideInterfaceContent(20, 1197, Type.DEFENCE_MITHRIL),
				new SkillGuideInterfaceContent(20, 7458, Type.DEFENCE_MITHRIL),
				new SkillGuideInterfaceContent(20, 4127, Type.DEFENCE_MITHRIL),
				new SkillGuideInterfaceContent(20, 8848, Type.DEFENCE_MITHRIL),

				new SkillGuideInterfaceContent(20, 5574, Type.DEFENCE_INITIATE),
				new SkillGuideInterfaceContent(20, 5575, Type.DEFENCE_INITIATE),
				new SkillGuideInterfaceContent(20, 5576, Type.DEFENCE_INITIATE),

				new SkillGuideInterfaceContent(30, 6895, Type.DEFENCE_ADAMANT),
				new SkillGuideInterfaceContent(30, 1161, Type.DEFENCE_ADAMANT),
				new SkillGuideInterfaceContent(30, 1111, Type.DEFENCE_ADAMANT),
				new SkillGuideInterfaceContent(30, 1123, Type.DEFENCE_ADAMANT),
				new SkillGuideInterfaceContent(30, 1091, Type.DEFENCE_ADAMANT),
				new SkillGuideInterfaceContent(30, 1073, Type.DEFENCE_ADAMANT),
				new SkillGuideInterfaceContent(30, 1183, Type.DEFENCE_ADAMANT),
				new SkillGuideInterfaceContent(30, 1199, Type.DEFENCE_ADAMANT),
				new SkillGuideInterfaceContent(30, 7459, Type.DEFENCE_ADAMANT),
				new SkillGuideInterfaceContent(30, 4129, Type.DEFENCE_ADAMANT),
				new SkillGuideInterfaceContent(30, 8849, Type.DEFENCE_ADAMANT),


				new SkillGuideInterfaceContent(40, 1147, Type.DEFENCE_RUNE),
				new SkillGuideInterfaceContent(40, 1163, Type.DEFENCE_RUNE),
				new SkillGuideInterfaceContent(40, 1113, Type.DEFENCE_RUNE),
				new SkillGuideInterfaceContent(40, 1127, Type.DEFENCE_RUNE),
				new SkillGuideInterfaceContent(40, 1093, Type.DEFENCE_RUNE),
				new SkillGuideInterfaceContent(40, 1079, Type.DEFENCE_RUNE),
				new SkillGuideInterfaceContent(40, 1185, Type.DEFENCE_RUNE),
				new SkillGuideInterfaceContent(40, 1201, Type.DEFENCE_RUNE),
				new SkillGuideInterfaceContent(40, 7460, Type.DEFENCE_RUNE),
				new SkillGuideInterfaceContent(40, 4131, Type.DEFENCE_RUNE),
				new SkillGuideInterfaceContent(40, 8850, Type.DEFENCE_RUNE),

				new SkillGuideInterfaceContent(60, 1149, Type.DEFENCE_DRAGON),
				new SkillGuideInterfaceContent(60, 11335, Type.DEFENCE_DRAGON),
				new SkillGuideInterfaceContent(60, 3140, Type.DEFENCE_DRAGON),
				new SkillGuideInterfaceContent(60, 14479, Type.DEFENCE_DRAGON),
				new SkillGuideInterfaceContent(60, 4585, Type.DEFENCE_DRAGON),
				new SkillGuideInterfaceContent(60, 4087, Type.DEFENCE_DRAGON),
				new SkillGuideInterfaceContent(60, 1187, Type.DEFENCE_DRAGON),
				new SkillGuideInterfaceContent(60, 11732, Type.DEFENCE_DRAGON),
				new SkillGuideInterfaceContent(60, 13262, Type.DEFENCE_DRAGON),

				new SkillGuideInterfaceContent(70, 4745, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4749, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4751, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4716, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4720, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4722, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4708, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4712, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4714, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4753, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4757, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4759, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4724, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4728, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4730, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4732, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4736, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 4738, Type.DEFENCE_BARROWS),
				new SkillGuideInterfaceContent(70, 7462, Type.DEFENCE_BARROWS),

				new SkillGuideInterfaceContent(1, 1167, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(1, 1129, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(1, 1095, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(1, 1063, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(1, 1061, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(1, 6330, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(1, 2581, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(1, 2577, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(20, 1133, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(20, 1097, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(25, 10954, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(25, 10956, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(25, 10958, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(30, 6326, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(30, 6322, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(30, 6324, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(30, 6328, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(40, 6131, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(40, 6133, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(40, 6135, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(40, 1135, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(40, 2499, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(40, 2501, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(40, 2503, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(40, 10336, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(40, 10332, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(42, 11664, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(42, 8839, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(42, 8840, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(42, 8842, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(45, 3749, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(45, 10334, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(45, 10330, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(70, 4732, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(70, 4736, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(70, 4738, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(70, 11718, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(70, 11720, Type.DEFENCE_RANGED),
				new SkillGuideInterfaceContent(70, 11722, Type.DEFENCE_RANGED),

				new SkillGuideInterfaceContent(1, 579, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 577, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 1011, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 1017, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 581, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 1015, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 6109, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 6107, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 6108, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 6110, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 6106, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 6111, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 12964, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 12971, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(1, 12978, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4099, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4101, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4103, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4105, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4107, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4089, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4091, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4093, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4095, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4097, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4109, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4111, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4113, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4115, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 4117, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 7400, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 7399, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(20, 7398, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(40, 14499, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(40, 14497, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(40, 14501, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(42, 11663, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(42, 8839, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(42, 8840, Type.DEFENCE_MAGIC),
				new SkillGuideInterfaceContent(42, 8842, Type.DEFENCE_MAGIC),


				new SkillGuideInterfaceContent(40, 3749, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(40, 3751, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(40, 3753, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(40, 3755, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(40, 6128, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(40, 6129, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(40, 6130, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(42, 11665, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(42, 11664, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(42, 11663, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(42, 8839, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(42, 8840, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(42, 8842, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(50, 10589, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(50, 10564, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(50, 6809, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(65, 11724, Type.DEFENCE_MISC),
				new SkillGuideInterfaceContent(65, 11726, Type.DEFENCE_MISC),
		}),

		RANGED(new SkillGuideInterfaceContent[] {

				new SkillGuideInterfaceContent(1, 1167, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(1, 1129, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(1, 1095, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(1, 1063, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(1, 1061, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(1, 6330, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(1, 2581, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(1, 2577, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(20, 1133, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(20, 1097, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(25, 10954, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(25, 10956, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(25, 10958, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(30, 6326, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(30, 6322, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(30, 6324, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(30, 6328, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(40, 6131, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(40, 6133, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(40, 6135, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(40, 6135, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(40, 1135, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(40, 1099, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(40, 1065, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(42, 11664, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(42, 8839, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(42, 8840, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(42, 8842, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(50, 2499, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(50, 2493, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(50, 2487, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(60, 2501, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(60, 2495, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(60, 2489, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(70, 2503, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(70, 2497, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(70, 2491, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(70, 4732, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(70, 4736, Type.RANGED_EQUIPMENT),
				new SkillGuideInterfaceContent(70, 4738, Type.RANGED_EQUIPMENT),

				new SkillGuideInterfaceContent(1, 841, Type.RANGED_SHORT_BOWS),
				new SkillGuideInterfaceContent(5, 843, Type.RANGED_SHORT_BOWS),
				new SkillGuideInterfaceContent(20, 849, Type.RANGED_SHORT_BOWS),
				new SkillGuideInterfaceContent(30, 853, Type.RANGED_SHORT_BOWS),
				new SkillGuideInterfaceContent(40, 857, Type.RANGED_SHORT_BOWS),
				new SkillGuideInterfaceContent(50, 861, Type.RANGED_SHORT_BOWS),

				new SkillGuideInterfaceContent(1, 839, Type.RANGED_LONG_BOWS),
				new SkillGuideInterfaceContent(5, 845, Type.RANGED_LONG_BOWS),
				new SkillGuideInterfaceContent(20, 847, Type.RANGED_LONG_BOWS),
				new SkillGuideInterfaceContent(30, 851, Type.RANGED_LONG_BOWS),
				new SkillGuideInterfaceContent(40, 855, Type.RANGED_LONG_BOWS),
				new SkillGuideInterfaceContent(50, 859, Type.RANGED_LONG_BOWS),

				new SkillGuideInterfaceContent(1, 9174, Type.RANGED_CROSSBOWS),
				new SkillGuideInterfaceContent(26, 9177, Type.RANGED_CROSSBOWS),
				new SkillGuideInterfaceContent(30, 9179, Type.RANGED_CROSSBOWS),
				new SkillGuideInterfaceContent(40, 9181, Type.RANGED_CROSSBOWS),
				new SkillGuideInterfaceContent(46, 9183, Type.RANGED_CROSSBOWS),
				new SkillGuideInterfaceContent(61, 9185, Type.RANGED_CROSSBOWS),

				new SkillGuideInterfaceContent(1, 825, Type.RANGED_JAVELINS),
				new SkillGuideInterfaceContent(10, 826, Type.RANGED_JAVELINS),
				new SkillGuideInterfaceContent(20, 827, Type.RANGED_JAVELINS),
				new SkillGuideInterfaceContent(30, 828, Type.RANGED_JAVELINS),
				new SkillGuideInterfaceContent(40, 829, Type.RANGED_JAVELINS),
				new SkillGuideInterfaceContent(50, 830, Type.RANGED_JAVELINS),

				new SkillGuideInterfaceContent(1, 864, Type.RANGED_KNIVES),
				new SkillGuideInterfaceContent(10, 863, Type.RANGED_KNIVES),
				new SkillGuideInterfaceContent(20, 865, Type.RANGED_KNIVES),
				new SkillGuideInterfaceContent(30, 866, Type.RANGED_KNIVES),
				new SkillGuideInterfaceContent(40, 867, Type.RANGED_KNIVES),
				new SkillGuideInterfaceContent(50, 868, Type.RANGED_KNIVES),

				new SkillGuideInterfaceContent(1, 806, Type.RANGED_DARTS),
				new SkillGuideInterfaceContent(10, 807, Type.RANGED_DARTS),
				new SkillGuideInterfaceContent(20, 808, Type.RANGED_DARTS),
				new SkillGuideInterfaceContent(30, 809, Type.RANGED_DARTS),
				new SkillGuideInterfaceContent(40, 810, Type.RANGED_DARTS),
				new SkillGuideInterfaceContent(50, 811, Type.RANGED_DARTS),
				new SkillGuideInterfaceContent(60, 11230, Type.RANGED_DARTS),

				new SkillGuideInterfaceContent(1, 800, Type.RANGED_THROWN_AXE),
				new SkillGuideInterfaceContent(10, 801, Type.RANGED_THROWN_AXE),
				new SkillGuideInterfaceContent(20, 802, Type.RANGED_THROWN_AXE),
				new SkillGuideInterfaceContent(30, 803, Type.RANGED_THROWN_AXE),
				new SkillGuideInterfaceContent(40, 804, Type.RANGED_THROWN_AXE),
				new SkillGuideInterfaceContent(50, 805, Type.RANGED_THROWN_AXE),
		}),

		PRAYER(new SkillGuideInterfaceContent[] {
				new SkillGuideInterfaceContent(1, 526, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 2859, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 528, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 530, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 532, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 10977, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 10976, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 3125, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 4812, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 3123, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 534, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 536, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 4830, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 4832, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 6729, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 14793, Type.PRAYER_BONES),
				new SkillGuideInterfaceContent(1, 18830, Type.PRAYER_BONES),
		});


		SkillGuideInterfaceData(SkillGuideInterfaceContent[] content) {
			this.content = content;
		}

		private SkillGuideInterfaceContent[] content;

		public SkillGuideInterfaceContent[] getContent() {
			return content;
		}
	}


	private static class SkillGuideInterfaceContent {

		public SkillGuideInterfaceContent(int levelReq, int item, Type type) {
			this.levelReq = levelReq;
			this.item = item;
			this.type = type;
		}

		public int levelReq, item;
		public Type type;
	}
}
