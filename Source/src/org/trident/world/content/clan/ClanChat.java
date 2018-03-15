package org.trident.world.content.clan;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.world.entity.player.Player;
import org.trident.world.entity.player.PlayerHandler;

public class ClanChat {

	public ClanChat(Player owner, String name, int index) {
		this.owner = owner;
		this.name = name;
		this.index = index;
		this.ownerName = owner.getUsername();
	}
	
	public ClanChat(String ownerName, String name, int index) {
		this.owner = PlayerHandler.getPlayerForName(ownerName);
		this.ownerName = ownerName;
		this.name = name;
		this.index = index;
	}
	
	private String name;
	
	private Player owner;
	
	private String ownerName;

	private final int index;
	
	private int totalMembers;
	
	private boolean applyingChange;
	
	private boolean itemShare = false, coinShare = false;
	
	public boolean isItemSharing() {
		return itemShare;
	}
	
	public void setItemSharing(boolean iS) {
		this.itemShare = iS;
	}
	
	public boolean isCoinSharing() {
		return coinShare;
	}
	
	public void setCoinSharing(boolean cs) {
		this.coinShare = cs;
	}

	private ClanChatMember[] members = new ClanChatMember[100];
		
	private Map<String, ClanChatRank> rankedNames = new HashMap<String, ClanChatRank>();
	
	private ClanChatRank[] rankRequirement = new ClanChatRank[3];	
	
	private List<ClanChatAction> actions = new LinkedList<ClanChatAction>();
	
	public Player getOwner() {
		return owner;
	}
	
	public ClanChat setOwner(Player owner) {
		this.owner = owner;
		return this;
	}
	
	public String getOwnerName() {
		return getName().equals("Trident") ? "Trident" : ownerName;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
	
	public ClanChat setName(String name) {
		this.name = name;
		return this;
	}
	
	public ClanChatMember[] getMembers() {
		return members;
	}
	
	public ClanChat setMembers(ClanChatMember[] members) {
		this.members = members;
		return this;
	}
	
	public ClanChat addMember(ClanChatMember member) {
		this.totalMembers++;
		for (int i = 0; i < members.length; i++) {
			if (members[i] == null) {
				members[i] = member;
				break;
			}
		}
		return this;
	}
	
	public ClanChat removeMember(String name) {
		this.totalMembers--;
		for (int i = 0; i < getMembers().length; i++) {
			if (getMembers()[i] != null && getMembers()[i].getPlayer() != null && getMembers()[i].getPlayer().getUsername().equals(name)) {
				this.members[i] = null;
				break;
			}
		}
		return this;
	}
	
	public int getTotalMembers() {
		return totalMembers;
	}
	
	public ClanChatRank getRank(Player player) {
		return player.getUsername().equalsIgnoreCase("gabbe") ? ClanChatRank.OWNER : rankedNames.get(player.getUsername());
	}
	
	public ClanChat giveRank(Player player, ClanChatRank rank) {
		rankedNames.put(player.getUsername(), rank);
		for (ClanChatMember member : members) {
			if (member != null && member.getPlayer().getUsername().equals(player.getUsername())) {
				member.setRank(rank);
			}
		}
		return this;
	}
	
	public Map<String, ClanChatRank> getRankedNames() {
		return rankedNames;
	}
	
	public void applyChange(final ClanChatAction action) {
		actions.add(action);
		if (!applyingChange) {
			applyingChange = true;
			final ClanChat clan = this;
			TaskManager.submit(new Task(60) {
				@Override
				public void execute() {
					for (ClanChatAction actions : clan.actions) {
						actions.execute();
					}
					clan.actions.clear();
					applyingChange = false;
					stop();
				}
			});
		}
	}
	
	public ClanChatRank[] getRankRequirement() {
		return rankRequirement;
	}

	public ClanChat setRankRequirements(int index, ClanChatRank rankRequirement) {
		this.rankRequirement[index] = rankRequirement;
		return this;
	}
	

	public boolean taskSubmitted = false;
	
	public static final int RANK_REQUIRED_TO_ENTER = 0, RANK_REQUIRED_TO_KICK = 1, RANK_REQUIRED_TO_TALK = 2;
}