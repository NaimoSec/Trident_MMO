package org.trident.world.content.clan;

import org.trident.world.entity.player.Player;

public class ClanChatMember {

	public ClanChatMember(Player player, ClanChat clan, ClanChatRank rank) {
		this.player = player;
		this.clan = clan;
		this.rank = rank;
	}

	private ClanChatRank rank;
	
	private final ClanChat clan;
	
	private final Player player;
	
	public ClanChatRank getRank() {
		return rank;
	}

	public ClanChatMember setRank(ClanChatRank rank) {
		this.rank = rank;
		return this;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public ClanChat getClan() {
		return clan;
	}
}
