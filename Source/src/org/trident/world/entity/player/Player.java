package org.trident.world.entity.player;

import org.jboss.netty.channel.Channel;
import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.PlayerDeathTask;
import org.trident.model.Animation;
import org.trident.model.Appearance;
import org.trident.model.ChatMessage;
import org.trident.model.Difficulty;
import org.trident.model.Item;
import org.trident.model.PlayerRelations;
import org.trident.model.PlayerRights;
import org.trident.model.Prayerbook;
import org.trident.model.Skill;
import org.trident.model.container.impl.Bank;
import org.trident.model.container.impl.Equipment;
import org.trident.model.container.impl.Inventory;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketSender;
import org.trident.util.Constants;
import org.trident.util.FrameUpdater;
import org.trident.world.content.BonusManager;
import org.trident.world.content.LoyaltyProgrammeHandler;
import org.trident.world.content.PointsHandler;
import org.trident.world.content.Trading;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.combatdata.magic.Autocasting;
import org.trident.world.content.combat.weapons.WeaponHandler;
import org.trident.world.content.minigames.impl.Dueling;
import org.trident.world.content.skills.AdvancedSkills;
import org.trident.world.content.skills.SkillManager;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.npc.NPC;


/**
 * Represents a world's playable character, which can interact
 * with different aspects of the game.
 * @author relex lawl
 */

public class Player extends GameCharacter {

	public Player(Channel channel) {
		super(Constants.DEFAULT_POSITION.copy());
		this.channel = channel;
	}

	@Override
	public void appendDeath() {
		getAttributes().setDying(true);
		TaskManager.submit(new PlayerDeathTask(this));
	}

	@Override
	public int getConstitution() {
		return getSkillManager().getCurrentLevel(Skill.CONSTITUTION);
	}

	@Override
	public GameCharacter setConstitution(int constitution) {
		getSkillManager().setCurrentLevel(Skill.CONSTITUTION, constitution);
		getPacketSender().sendSkill(Skill.CONSTITUTION);
		if(getConstitution() <= 0 && !getAttributes().isDying())
			appendDeath();
		return this;
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Override
	public int getAttackDelay() {
		return (WeaponHandler.getAttackDelay(this) + getPlayerCombatAttributes().getAttackStyle().getExtraAttackDelay());
	}

	@Override
	public Animation getAttackAnimation() {
		if(getCombatAttributes().getAttackType() == AttackType.MAGIC && getPlayerCombatAttributes().getMagic().getCurrentSpell() != null)
			return getPlayerCombatAttributes().getMagic().getCurrentSpell().castAnimation();
		return new Animation(WeaponHandler.getWepAnim(this));
	}

	@Override
	public Animation getBlockAnimation() {
		return new Animation(WeaponHandler.getBlockEmote(this));
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof Long)
			return ((Long)object).equals(getLongUsername());
		else if(object instanceof String)
			return ((String)object).equalsIgnoreCase(getUsername());
		else if(object instanceof NPC)
			return false;
		Player other = (Player) object;
		return other.getUsername().equalsIgnoreCase(getUsername());
	}

	@Override
	public int getSize() {
		return 1;
	}

	/*
	 * Processing
	 */
	public void process() {
		processGameCharacterAttributes();
		if(attributes.shouldProcessFarming())
			getAdvancedSkills().getFarming().process();
		attributes.setLoyaltyTick(1, true);
		if(attributes.getLoyaltyTick() >= 900) {
			LoyaltyProgrammeHandler.givePoints(this);
			attributes.setLoyaltyTick(0, false);
		}
	}

	/*
	 * Fields, most stuff are in the PlayerAttributes class.
	 */

	private String username;
	private Long longUsername;
	private String password;
	private String emailAdress;
	private int hardwareNumber;
	private Difficulty gameDifficulty;
	private PlayerAttributes attributes = new PlayerAttributes();
	private PlayerCombatAttributes playerCombatAttributes = new PlayerCombatAttributes(this);
	private PacketSender packetSender = new PacketSender(this);
	private Appearance appearance = new Appearance(this);
	private Inventory inventory = new Inventory(this);
	private Equipment equipment = new Equipment(this);
	private FrameUpdater frameUpdater = new FrameUpdater();
	private Bank[] bank = new Bank[9];
	private PlayerRights rights = PlayerRights.PLAYER;
	private SkillManager skillManager = new SkillManager(this);
	private PlayerRelations relations = new PlayerRelations(this);
	private ChatMessage chatMessages = new ChatMessage();
	private PointsHandler pointsHandler = new PointsHandler(this);
	private AdvancedSkills advancedSkills = new AdvancedSkills(this);
	private final Trading trading = new Trading(this);
	private final Dueling dueling = new Dueling(this);

	/*
	 * Getters and setters
	 */

	public String getUsername() {
		return username;
	}

	public Player setUsername(String username) {
		this.username = username;
		return this;
	}

	public Long getLongUsername() {
		return longUsername;
	}

	public Player setLongUsername(Long longUsername) {
		this.longUsername = longUsername;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public String getEmailAdress() {
		return this.emailAdress;
	}

	public void setEmailAdress(String adress) {
		this.emailAdress = adress;
	}

	public Player setPassword(String password) {
		this.password = password;
		return this;
	}

	public Difficulty getDifficulty() {
		return gameDifficulty;
	}

	public void setDifficulty(Difficulty gameDifficulty) {
		this.gameDifficulty = gameDifficulty;
	}

	private String hostAdress;

	public String getHostAdress() {
		return hostAdress;
	}

	public Player setHostAdress(String hostAdress) {
		this.hostAdress = hostAdress;
		return this;
	}

	public int getHardwareNumber() {
		return hardwareNumber;
	}

	public Player setHardwareNumber(int hardwareNumber) {
		this.hardwareNumber = hardwareNumber;
		return this;
	}

	public FrameUpdater getFrameUpdater() {
		return this.frameUpdater;
	}

	public PlayerRights getRights() {
		return rights;
	}

	public Player setRights(PlayerRights rights) {
		this.rights = rights;
		return this;
	}

	public ChatMessage getChatMessages() {
		return chatMessages;
	}

	public PacketSender getPacketSender() {
		return packetSender;
	}

	public SkillManager getSkillManager() {
		return skillManager;
	}


	public Appearance getAppearance() {
		return appearance;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public Bank getBank(int tab) {
		return bank[tab];
	}

	public void setBank(int tab, Bank bank) {
		this.bank[tab] = bank;
	}

	public Bank[] getBanks() {
		return this.bank;
	}

	public PlayerRelations getRelations() {
		return relations;
	}

	public PointsHandler getPointsHandler() {
		return pointsHandler;
	}

	public AdvancedSkills getAdvancedSkills() {
		return advancedSkills;
	}

	public Trading getTrading() {
		return trading;
	}

	public Dueling getDueling() {
		return dueling;
	}
	
	public PlayerAttributes getAttributes() {
		return attributes;
	}

	public PlayerCombatAttributes getPlayerCombatAttributes() {
		return playerCombatAttributes;
	}

	public int clue1Amount;
	public int clue2Amount;
	public int clue3Amount;
	public int clueLevel;
	public Item[] puzzleStoredItems;
	public int sextantGlobalPiece;
	public double sextantBarDegree;
	public int rotationFactor;
	public int sextantLandScapeCoords;
	public int sextantSunCoords;

	/*
	 * 'Restarts' a player - sets it to the default properties!
	 * This method is called upon death etc.
	 */
	public void restart() {
		getAttributes().setDying(false);
		TeleportHandler.cancelCurrentActions(this);
		getAttributes().setRunEnergy(100);
		getPlayerCombatAttributes().setSpecialAttackAmount(10.0).setUsingSpecialAttack(false).setVengeance(false).setLastVengeanceCast(0);
		getCombatAttributes().setCurrentPoisonDamage(0).setCurrentEnemy(null).setLastAttacker(null).setStunned(false).setTargeted(false);
		if(getAttributes().getPrayerbook() == Prayerbook.CURSES)
			CurseHandler.deactivateAll(this);
		else
			PrayerHandler.deactivateAll(this);
		for (Skill skill : Skill.values())
			getSkillManager().setCurrentLevel(skill, getSkillManager().getMaxLevel(skill));
		setAnimation(new Animation(65535));
		CombatHandler.skull(this, false);
		CombatHandler.resetAttack(this);
		for(int i = 0; i < getCombatAttributes().getLeechedBonuses().length; i++)
			getCombatAttributes().getLeechedBonuses()[i] = 0;
		BonusManager.sendCurseBonuses(this);
		BonusManager.update(this);
		Autocasting.resetAutocast(this, true);
		WeaponHandler.update(this);
	}

	/*
	 * Logs out the player
	 */
	public boolean logout() {
		if (System.currentTimeMillis() - getCombatAttributes().getLastDamageReceived() < 8000) {
			getPacketSender().sendMessage("You must wait a few seconds after being out of combat before doing this.");
			return false;
		}
		if(getConstitution() <= 0 || getAttributes().isSettingUpCannon() || getSkillManager().getSkillAttributes().getAgilityAttributes().isCrossingObstacle()) {
			getPacketSender().sendMessage("You cannot log out at the moment.");
			return false;
		}
		return true;
	}

	private Channel channel;
	
	public Player write(Packet packet) {
		if (channel.isConnected()) {
			channel.write(packet);
		}
		return this;
	}
	
	public Channel getChannel() {
		return channel;
	}
}
