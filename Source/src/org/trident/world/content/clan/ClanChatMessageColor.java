package org.trident.world.content.clan;

import org.trident.world.entity.player.Player;

public enum ClanChatMessageColor {

	BLUE("000080", "0000FF", "0000FF"),
	RED("8B0000", "FF0000", "FF0000"),
	ORANGE("FFA500", "FFA500", "D2691E"),
	GREEN("00D807", "00FF09", "006400"),
	PURPLE("25512780"),
	PINK("D10099", "FF69b4", "FFc0cb"),
	YELLOW("F2EA00", "F7FF00", "F7FF00"),
	TEAL("00FFEF", "00C3B6", "00C3B6"),
	WHITE("FFFFFF", "FFFFFF", "FFFFFF"),
	BLACK("000000", "000000", "000000"),
	GREY("708090", "778899", "778899");

	private ClanChatMessageColor(String... rgb) {
		if (rgb.length > 3)
			throw new IllegalArgumentException("Clan chat message colors can only hold 3 decimal values.");
		this.rgb = rgb;
	}

	private final String[] rgb;

	public String[] getRGB() {
		return rgb;
	}
	
	private int rgbIndex;
	
	public void setRgbIndex(int rgbIndex) {
		this.rgbIndex = rgbIndex;
	}
	
	public int getRgbIndex() {
		return rgbIndex;
	}

	public static ClanChatMessageColor forId(int id) {
		for (ClanChatMessageColor color : ClanChatMessageColor.values()) {
			if (color.ordinal() == id) {
				return color;
			}
		}
		return null;
	}

	public static boolean setColor(Player player, int button) {
		if(player.getAttributes().getInterfaceId() < 0) {
			ClanChatMessageColor color = null;
			int index = 0;
			switch(button) {
			case -26534:
				color = ClanChatMessageColor.TEAL;
				break;
			case -26531:
				color = ClanChatMessageColor.TEAL;
				index = 1;
				break;
			case -26528:
				color = ClanChatMessageColor.BLUE;
				break;
			case -26525:
				color = ClanChatMessageColor.GREEN;
				break;
			case -26522:
				color = ClanChatMessageColor.GREEN;
				index = 1;
				break;
			case -26519:
				color = ClanChatMessageColor.GREEN;
				index = 2;
				break;
			case -26516:
				color = ClanChatMessageColor.PINK;
				index = 2;
				break;
			case -26513:
				color = ClanChatMessageColor.ORANGE;
				break;
			case -26510:
				color = ClanChatMessageColor.ORANGE;
				index = 2;
				 break;
			case -26507:
				color = ClanChatMessageColor.YELLOW;
				break;
			case -26504:
				color = ClanChatMessageColor.RED;
				index = 1;
				break;
			case -26501:
				color = ClanChatMessageColor.PINK;
				break;
			case -26498:
				color = ClanChatMessageColor.PINK;
				index = 1;
				break;
			case -26495:
				color = ClanChatMessageColor.PURPLE;		
				break;
			case -26492:
				color = ClanChatMessageColor.BLACK;
				break;
			}
			if(color != null) {
				player.getAttributes().setClanChatMessageColor(color);
				player.getAttributes().getClanChatMessageColor().setRgbIndex(index);
				player.getPacketSender().sendMessage("<col=" + player.getAttributes().getClanChatMessageColor().getRGB()[player.getAttributes().getClanChatMessageColor().getRgbIndex()] + ">[EXAMPLE]: Hello!");
				color = null;
				return true;
			}
		}
		return false;
	}
}
