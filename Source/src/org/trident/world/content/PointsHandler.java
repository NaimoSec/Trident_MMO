package org.trident.world.content;

import org.trident.world.entity.player.Player;

public class PointsHandler {

	private Player p;
	public PointsHandler(Player p) {
		this.p = p;
	}

	public void refreshPanel() {
		p.getPacketSender().sendString(39359, "@or2@Dungeoneering Tokens: @yel@ "+dungTokens);
		p.getPacketSender().sendString(39360, "@or2@Achievement Points: @yel@ "+achievementPoints);
		p.getPacketSender().sendString(39361, "@or2@Conquest Points: @yel@ "+conquestPoints);
		p.getPacketSender().sendString(39362, "@or2@Loyalty Points: @yel@"+loyaltyProgrammePoints);
		p.getPacketSender().sendString(39363, "@or2@Commendations: @yel@ "+commendations);
		p.getPacketSender().sendString(39364, "@or2@Member Points: @yel@ "+donatorPoints);
		p.getPacketSender().sendString(39365, "@or2@Slayer Points: @yel@"+slayerPoints);
		p.getPacketSender().sendString(39366, "@or2@Pk Points: @yel@"+pkPoints);
		p.getPacketSender().sendString(39367, "@or2@Zeals: @yel@"+zeals);

		p.getPacketSender().sendString(39369, "@or2@Wilderness Kills: @yel@"+p.getPlayerCombatAttributes().getKills());
		p.getPacketSender().sendString(39370, "@or2@Wilderness Deaths: @yel@"+p.getPlayerCombatAttributes().getDeaths());
		p.getPacketSender().sendString(39371, "@or2@Arena Victories: @yel@"+p.getDueling().arenaStats[0]);
		p.getPacketSender().sendString(39372, "@or2@Arena Losses: @yel@"+p.getDueling().arenaStats[1]);
	}

	/**
	 * Donator/member points
	 */
	private int donatorPoints;

	public int getDonatorPoints() {
		return this.donatorPoints;
	}

	public void setDonatorPoints(int points, boolean add) {
		if(add)
			this.donatorPoints += points;
		else
			this.donatorPoints = points;
	}

	/**
	 * Slayer points
	 */
	private int slayerPoints = 0;

	public int getSlayerPoints() {
		return this.slayerPoints;
	}

	public void setSlayerPoints(int slayerPoints, boolean add) {
		if(add)
			this.slayerPoints += slayerPoints;
		else
			this.slayerPoints = slayerPoints;
	}
	/**
	 * Commendations
	 */
	private int commendations = 0;

	public int getCommendations() {
		return this.commendations;
	}

	public void setCommendations(int commendations, boolean add) {
		if(add)
			this.commendations += commendations;
		else
			this.commendations = commendations;
	}
	/**
	 * Achievement points
	 */
	private int achievementPoints = 0; 

	public int getAchievementPoints() {
		return this.achievementPoints;
	}

	public void setAchievementPoints(int points, boolean add) {
		if(add)
			this.achievementPoints += points;
		else
			this.achievementPoints = points;
	}

	/**
	 * Assault points
	 */
	private int conquestPoints = 0;

	public int getConquestPoints() {
		return this.conquestPoints;
	}

	public void setConquestPoints(int points, boolean add) {
		if(add)
			this.conquestPoints += points;
		else
			this.conquestPoints = points;
	}

	/**
	 * Loyalty points
	 */
	private int loyaltyProgrammePoints = 0;

	public int getLoyaltyProgrammePoints() {
		return this.loyaltyProgrammePoints;
	}

	public void setLoyaltyProgrammePoints(int points, boolean add) {
		if(add)
			this.loyaltyProgrammePoints += points;
		else
			this.loyaltyProgrammePoints = points;
	}

	/**
	 * PK points
	 */
	private int pkPoints = 0;

	public int getPkPoints() {
		return this.pkPoints;
	}

	public void setPkPoints(int points, boolean add) {
		if(add)
			this.pkPoints += points;
		else
			this.pkPoints = points;
	}

	/**
	 * Zeals
	 */
	private int zeals = 0;

	public int getZeals() {
		return this.zeals;
	}

	public void setZeals(int points, boolean add) {
		if(add)
			this.zeals += points;
		else
			this.zeals = points;
	}

	/*
	 * Dungeoneering
	 */
	private int dungTokens = 0;

	public int getDungeoneeringTokens() {
		return dungTokens;
	}

	public void setDungeoneeringTokens(boolean add, int dungTokens) {
		if(add)
			this.dungTokens += dungTokens;
		else
			this.dungTokens = dungTokens;
	}
}
