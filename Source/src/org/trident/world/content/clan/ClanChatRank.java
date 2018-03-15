package org.trident.world.content.clan;

public enum ClanChatRank {

	FRIEND,
	RECRUIT,
	CORPORAL,
	SERGEANT,
	LIEUTENANT,
	CAPTAIN,
	GENERAL,
	OWNER,
	SERVER_ADMINISTRATOR;
	
	public static ClanChatRank forId(int id) {
		for (ClanChatRank rank : ClanChatRank.values()) {
			if (rank.ordinal() == id) {
				return rank;
			}
		}
		return null;
	}
	
	public static ClanChatRank forString(String rank) {
		for (ClanChatRank ranks : ClanChatRank.values()) {
			if (ranks.toString().equalsIgnoreCase(rank)) {
				return ranks;
			}
		}
		return null;
	}
}
