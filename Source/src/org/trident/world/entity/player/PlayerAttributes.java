package org.trident.world.entity.player;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.engine.task.impl.WalkToAction;
import org.trident.model.Animation;
import org.trident.model.DwarfCannon;
import org.trident.model.GameObject;
import org.trident.model.Item;
import org.trident.model.MagicSpellbook;
import org.trident.model.PlayerInteractingOption;
import org.trident.model.Position;
import org.trident.model.Prayerbook;
import org.trident.model.RegionInstance;
import org.trident.model.container.impl.Shop;
import org.trident.model.container.impl.Bank.BankSearchAttributes;
import org.trident.model.inputhandling.Input;
import org.trident.world.content.Achievements;
import org.trident.world.content.BonusManager;
import org.trident.world.content.BankPin.BankPinAttributes;
import org.trident.world.content.Gravestones.GravestoneAttributes;
import org.trident.world.content.SkillGuides.SkillGuideInterfaceData;
import org.trident.world.content.clan.ClanChat;
import org.trident.world.content.clan.ClanChatMessageColor;
import org.trident.world.content.dialogue.Dialogue;
import org.trident.world.content.minigames.MinigameAttributes;
import org.trident.world.entity.npc.NPC;

/**
 * A class which holds a player's properties
 * Note: This class holds properties that are only for the player and not for other entites.
 * @author Gabbe
 */
public class PlayerAttributes {

	/*
	 * Junk
	 */
	
	private final BonusManager bonusManager = new BonusManager();
	private MinigameAttributes minigameAttributes = new MinigameAttributes();
	private BankPinAttributes bankPinAttributes = new BankPinAttributes();
	private GravestoneAttributes gravestoneAttributes = new GravestoneAttributes();
	private BankSearchAttributes bankSearchAttributes = new BankSearchAttributes();
	private ClanChatMessageColor clanChatMessageColor = ClanChatMessageColor.BLUE;
	private Prayerbook prayerbook = Prayerbook.NORMAL;
	private MagicSpellbook spellbook = MagicSpellbook.NORMAL;
	private PlayerInteractingOption playerInteractingOption = PlayerInteractingOption.NONE;
	private SkillGuideInterfaceData skillGuideInterfaceData;
	private CopyOnWriteArrayList<Item> priceCheckedItems = new CopyOnWriteArrayList<Item>();
	private RegionInstance regionInstance;
	private Animation[] playerAnimations = new Animation[7];
	private Input inputHandling;
	private DwarfCannon cannon;
	private GameObject cannonObject;
	private ClanChat clanChat;
	private GameObject currentInteractingObject;
	private Item currentInteractingItem;
	private NPC currentInteractingNPC;
	private Position resetPosition;
	private Item itemToDrop;
	private Shop shop;
	private Dialogue dialogue;
	private WalkToAction walkToAction;
	private Object[] loyaltyProductSelected;
	private boolean isBanking;
	private String clanChatName;
	private boolean hasClan;
	private int runEnergy = 100;
	private int interfaceId = -1, walkableInterfaceId = -1;
	private int dialogueAction;
	private boolean hasStarted;
	private boolean regionLoading;
	private boolean experienceLocked;
	private boolean isShopping;
	private boolean settingUpCannon;
	private long callForHelp;
	private boolean isDying;
	private boolean prayerInjured;
	private boolean changingRegion;
	private int logoutAttempts;
	private int[] forceMovement = new int[7];
	private int currentBankTab;
	private int moneyPouchCash;
	private int productChosen;
	private int noClueReward;
	private boolean prayerDealingDamage;
	private boolean isResting, isRunning;
	private long lastRunRecovery;
	private int multiIcon, shadowState;
	private boolean drainingPrayer;
	private long ardougneChestLootingDelay;
	private boolean priceChecking;
	private boolean acceptingAid = true;
	private boolean fireImmunityEventRunning;
	private int volume = 2;
	private int newPlayerDelay;
	private int overloadPotionTimer, prayerRenewalPotionTimer;
	private int fireImmunity, fireDamageModifier;
	private long specialRestoreTimer;
	private long lastYell;
	private long summonSpawnDelay;
	private long foodDelay;
	private long potionDelay;
	private long graphicDelay;
	private long clickDelay;
	private long lastVote;
	private long lastItemPickup;
	private long lastReport;
	private boolean[] achievements = new boolean[Achievements.Tasks.values().length + 1];
	private int effigy;
	private int dragonFireCharges;
	private int loyaltyTitle;
	private int loyaltyTick;
	private int inactiveTimer;
	private boolean[] tutorialFinished = new boolean[6];
	private boolean shouldProcessFarming;
	private boolean noteWithdrawal;
	private boolean clientIsLoading;
	private boolean coughing;
	private long sqlDelay;
	private boolean updateHighscores;
	private boolean clientExitTaskActive;
	private boolean forceLogout, loggedOut;
	private final List<Player> localPlayers = new LinkedList<Player>();
	private List<NPC> localNpcs = new LinkedList<NPC>();
	
	public PlayerAttributes() {}

	/*
	 * Getters and setters
	 */
	
	public PlayerAttributes setClanChat(ClanChat clanChat) {
		this.clanChat = clanChat;
		return this;
	}

	public PlayerAttributes setClanChatMessageColor(ClanChatMessageColor clanChatColorDecimal) {
		this.clanChatMessageColor = clanChatColorDecimal;
		return this;
	}

	public ClanChat getClanChat() {
		return clanChat;
	}

	public ClanChatMessageColor getClanChatMessageColor() {
		return clanChatMessageColor;
	}

	public BonusManager getBonusManager() {
		return bonusManager;
	}

	public PlayerAttributes setSpellbook(MagicSpellbook spellbook) {
		this.spellbook = spellbook;
		return this;
	}


	public MagicSpellbook getSpellbook() {
		return spellbook;
	}

	public PlayerAttributes setPrayerbook(Prayerbook prayerbook) {
		this.prayerbook = prayerbook;
		return this;
	}

	public Prayerbook getPrayerbook() {
		return prayerbook;
	}

	public PlayerAttributes setShop(Shop shop) {
		this.shop = shop;
		return this;
	}

	public Shop getShop() {
		return shop;
	}

	public PlayerAttributes setDialogue(Dialogue dialogue) {
		this.dialogue = dialogue;
		return this;
	}

	public Dialogue getDialogue() {
		return dialogue;
	}

	public PlayerAttributes setBanking(boolean isBanking) {
		this.isBanking = isBanking;
		return this;
	}

	public boolean isBanking() {
		return isBanking;
	}

	public PlayerAttributes setRunEnergy(int runEnergy) {
		this.runEnergy = runEnergy;
		return this;
	}

	public int getRunEnergy() {
		return runEnergy;
	}

	public PlayerAttributes setInterfaceId(int interfaceId) {
		this.interfaceId = interfaceId;
		return this;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public PlayerAttributes setWalkableInterfaceId(int walkableInterfaceId) {
		this.walkableInterfaceId = walkableInterfaceId;
		return this;
	}

	public int getWalkableInterfaceId() {
		return walkableInterfaceId;
	}

	public PlayerAttributes setDialogueAction(int dialogueAction) {
		this.dialogueAction = dialogueAction;
		return this;
	}

	public int getDialogueAction() {
		return dialogueAction;
	}

	public PlayerAttributes setStarted(boolean hasStarted) {
		this.hasStarted = hasStarted;
		return this;
	}

	public boolean hasStarted() {
		return hasStarted;
	}

	public PlayerAttributes setClickDelay(long clickDelay) {
		this.clickDelay = clickDelay;
		return this;
	}

	public long getClickDelay() {
		return clickDelay;
	}

	public PlayerAttributes setRegionLoading(boolean regionLoading) {
		this.regionLoading = regionLoading;
		return this;
	}

	public boolean isLoadingRegion() {
		return regionLoading;
	}

	public PlayerAttributes setFoodDelay(long foodDelay) {
		this.foodDelay = foodDelay;
		return this;
	}

	public PlayerAttributes setPotionDelay(long potionDelay) {
		this.potionDelay = potionDelay;
		return this;
	}

	public long getFoodDelay() {
		return foodDelay;
	}

	public long getPotionDelay() {
		return potionDelay;
	}

	public PlayerAttributes setGraphicDelay(long graphicDelay) {
		this.graphicDelay = graphicDelay;
		return this;
	}

	public long getGraphicDelay() {
		return graphicDelay;
	}

	public PlayerAttributes setExperienceLocked(boolean experienceLocked) {
		this.experienceLocked = experienceLocked;
		return this;
	}

	public boolean experienceLocked() {
		return experienceLocked;
	}

	public boolean isShopping() {
		return isShopping;
	}

	public void setShopping(boolean isShopping) {
		this.isShopping = isShopping;
	}

	public PlayerAttributes setClan(boolean hasClan) {
		this.hasClan = hasClan;
		return this;
	}

	public boolean hasClan() {
		return hasClan;
	}

	public PlayerAttributes setDying(boolean isDying) {
		this.isDying = isDying;
		return this;
	}

	public boolean isDying() {
		return isDying;
	}

	public String getClanChatName() {
		return clanChatName;
	}

	public PlayerAttributes setClanChatName(String clanChatName) {
		this.clanChatName = clanChatName;
		return this;
	}

	public long getCallForHelpDelay() {
		return callForHelp;
	}	

	public PlayerAttributes setCallForHelpDelay(long callForHelp) {
		this.callForHelp = callForHelp;
		return this;
	}

	public PlayerAttributes setCannon(DwarfCannon cannon) {
		this.cannon = cannon;
		return this;
	}

	public DwarfCannon getCannon() {
		return cannon;
	}

	public PlayerAttributes setCannonObject(GameObject cannonObject) {
		this.cannonObject = cannonObject;
		return this;
	}

	public GameObject getCannonObject() {
		return cannonObject;
	}

	public PlayerAttributes setPrayerInjured(boolean prayerInjured) {
		this.prayerInjured = prayerInjured;
		return this;
	}

	public boolean isPrayerInjured() {
		return prayerInjured;
	}

	public PlayerAttributes setRegionChange(boolean changingRegion) {
		this.changingRegion = changingRegion;
		return this;
	}

	public boolean isChangingRegion() {
		return changingRegion;
	}

	public PlayerAttributes setSettingUpCannon(boolean settingUpCannon) {
		this.settingUpCannon = settingUpCannon;
		return this;
	}

	public boolean isSettingUpCannon() {
		return settingUpCannon;
	}

	public PlayerAttributes setLogoutAttempts(int logoutAttempts) {
		this.logoutAttempts = logoutAttempts;
		return this;
	}

	public int getLogoutAttempts() {
		return logoutAttempts;
	}

	public int[] getForceMovement() {
		return forceMovement;
	}

	public PlayerAttributes setForceMovement(int[] forceMovement) {
		this.forceMovement = forceMovement;
		return this;
	}

	public int getCurrentBankTab() {
		return currentBankTab;
	}

	public PlayerAttributes setCurrentBankTab(int currentBankTab) {
		this.currentBankTab = currentBankTab;
		return this;
	}

	public int getMoneyInPouch() {
		return moneyPouchCash;
	}

	public PlayerAttributes setMoneyInPouch(int moneyPouchCash) {
		this.moneyPouchCash = moneyPouchCash;
		return this;
	}

	public MinigameAttributes getMinigameAttributes() {
		return minigameAttributes;
	}

	public BankPinAttributes getBankPinAttributes() {
		return bankPinAttributes;
	}
	
	public GravestoneAttributes getGravestoneAttributes() {
		return gravestoneAttributes;
	}

	public void setInputHandling(Input inputHandling) {
		this.inputHandling = inputHandling;
	}

	public Input getInputHandling() {
		return inputHandling;
	}

	public int getProductChosen() {
		return productChosen;
	}

	public PlayerAttributes setProductChosen(int productChosen) {
		this.productChosen = productChosen;
		return this;
	}

	public long getSummoningSpawnDelay() {
		return summonSpawnDelay;
	}

	public PlayerAttributes setSummoningSpawnDelay(long summonSpawnDelay) {
		this.summonSpawnDelay = summonSpawnDelay;
		return this;
	}

	public int getNewPlayerDelay() {
		return newPlayerDelay;
	}

	public PlayerAttributes setNewPlayerDelay(int newPlayerDelay) {
		this.newPlayerDelay = newPlayerDelay;
		return this;
	}

	public GameObject getCurrentInteractingObject() {
		return currentInteractingObject;
	}

	public Item getCurrentInteractingItem() {
		return currentInteractingItem;
	}

	public NPC getCurrentInteractingNPC() {
		return currentInteractingNPC;
	}

	public PlayerAttributes setCurrentInteractingObject(GameObject currentInteractingObject) {
		this.currentInteractingObject = currentInteractingObject;
		return this;
	}

	public PlayerAttributes setCurrentInteractingItem(Item currentInteractingItem) {
		this.currentInteractingItem = currentInteractingItem;
		return this;
	}

	public PlayerAttributes setCurrentInteractingNPC(NPC currentInteractingNPC) {
		this.currentInteractingNPC = currentInteractingNPC;
		return this;
	}

	public boolean prayerIsDealingDamage() {
		return prayerDealingDamage;
	}

	public PlayerAttributes setPrayerIsDealingDamage(boolean prayerDealingDamage) {
		this.prayerDealingDamage = prayerDealingDamage;
		return this;
	}

	public boolean isResting() {
		return isResting;
	}

	public PlayerAttributes setResting(boolean isResting) {
		this.isResting = isResting;
		return this;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public PlayerAttributes setRunning(boolean isRunning) {
		this.isRunning = isRunning;
		return this;
	}

	public long getLastRunRecovery() {
		return lastRunRecovery;
	}

	public PlayerAttributes setLastRunRecovery(long lastRunRecovery) {
		this.lastRunRecovery = lastRunRecovery;
		return this;
	}

	public int getMultiIcon() {
		return multiIcon;
	}

	public PlayerAttributes setMultiIcon(int multiIcon) {
		this.multiIcon = multiIcon;
		return this;
	}
	
	public PlayerInteractingOption getPlayerInteractingOption() {
		return playerInteractingOption;
	}
	
	public PlayerAttributes setPlayerInteractingOption(PlayerInteractingOption playerInteractingOption) {
		this.playerInteractingOption = playerInteractingOption;
		return this;
	}
	
	public SkillGuideInterfaceData getSkillGuideInterfaceData() {
		return skillGuideInterfaceData;
	}
	
	public void setSkillGuideInterfaceData(SkillGuideInterfaceData skillGuideInterfaceData) {
		this.skillGuideInterfaceData = skillGuideInterfaceData;
	}
	
	public boolean isDrainingPrayer() {
		return drainingPrayer;
	}
	
	public PlayerAttributes setDrainingPrayer(boolean drainingPrayer) {
		this.drainingPrayer = drainingPrayer;
		return this;
	}
	
	public long getArdougneChestLootingDelay() {
		return ardougneChestLootingDelay;
	}

	public PlayerAttributes setArdougneChestLootingDelay(long ardougneChestLootingDelay) {
		this.ardougneChestLootingDelay = ardougneChestLootingDelay;
		return this;
	}
	
	public void setPriceChecking(boolean priceChecking) {
		this.priceChecking = priceChecking;
	}

	public boolean isPriceChecking() {
		return priceChecking;
	}
	
	public CopyOnWriteArrayList<Item> getPriceCheckedItems() {
		return priceCheckedItems;
	}
	
	public boolean isAcceptingAid() {
		return acceptingAid;
	}
	
	public void setAcceptingAid(boolean acceptingAid) {
		this.acceptingAid = acceptingAid;
	}
	
	public int getVolume() {
		return volume;
	}
	
	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	public int getOverloadPotionTimer() {
		return overloadPotionTimer;
	}
	
	public void setOverloadPotionTimer(int overloadPotionTimer) {
		this.overloadPotionTimer = overloadPotionTimer;
	}
	
	public int getPrayerRenewalPotionTimer() {
		return prayerRenewalPotionTimer;
	}
	
	public void setPrayerRenewalPotionTimer(int prayerRenewalPotionTimer) {
		this.prayerRenewalPotionTimer = prayerRenewalPotionTimer;
	}
	
	public long getLastYell() {
		return lastYell;
	}
	
	public void setLastYell(long lastYell) {
		this.lastYell = lastYell;
	}
	
	public long getLastVote() {
		return lastVote;
	}
	
	public void setLastVote(long lastVote) {
		this.lastVote = lastVote;
	}
	
	public long getLastItemPickup() {
		return lastItemPickup;
	}
	
	public void setLastItemPickup(long lastItemPickup) {
		this.lastItemPickup = lastItemPickup;
	}
	
	public long getLastReport() {
		return lastReport;
	}
	
	public void setLastReport(long lastReport) {
		this.lastReport = lastReport;
	}

	public boolean[] getAchievements() {
		return achievements;
	}
	
	public int getNoClueReward() {
		return noClueReward;
	}
	
	public void setNoClueReward(int noClueReward) {
		this.noClueReward = noClueReward;
	}
	
	public int getEffigy() {
		return effigy;
	}
	
	public void setEffigy(int effigy) {
		this.effigy = effigy;
	}
	
	public Position getResetPosition() {
		return resetPosition;
	}
	
	public void setResetPosition(Position resetPosition) {
		this.resetPosition = resetPosition;
	}
	
	public Item getItemToDrop() {
		return itemToDrop;
	}
	
	public void setItemToDrop(Item itemToDrop) {
		this.itemToDrop = itemToDrop;
	}
	
	public Animation[] getPlayerAnimations() {
		return playerAnimations;
	}
	
	public PlayerAttributes setPlayerAnimation(int index, Animation animation) {
		playerAnimations[index] = animation;
		return this;
	}
	
	public int getDragonFireCharges() {
		return dragonFireCharges;
	}

	public void setDragonFireCharges(int charges, boolean add) {
		this.dragonFireCharges = add ? this.dragonFireCharges+charges : charges;
	}
	
	public int getLoyaltyTitle() {
		return loyaltyTitle;
	}
	
	public void setLoyaltyTitle(int loyaltyTitle) {
		this.loyaltyTitle = loyaltyTitle;
	}
	
	public Object[] getLoyaltyProductSelected() {
		return loyaltyProductSelected;
	}
	
	public void setLoyaltyProductSelected(Object[] loyaltyProductSelected) {
		this.loyaltyProductSelected = loyaltyProductSelected;
	}
	
	public int getInactiveTimer() {
		return inactiveTimer;
	}
	
	public PlayerAttributes setInactiveTimer(int inactiveTimer) {
		this.inactiveTimer = inactiveTimer;
		return this;
	}
	
	public boolean[] getTutorialFinished() {
		return tutorialFinished;
	}
	
	public boolean shouldProcessFarming() {
		return shouldProcessFarming;
	}
	
	public void setShouldProcessFarming(boolean shouldProcessFarming) {
		this.shouldProcessFarming = shouldProcessFarming;
	}
	
	public int getLoyaltyTick() {
		return loyaltyTick;
	}
	
	public void setLoyaltyTick(int loyaltyTick, boolean increase) {
		this.loyaltyTick = increase ? (this.loyaltyTick + loyaltyTick) : loyaltyTick;
	}
	
	public void setNoteWithdrawal(boolean noteWithdrawal) {
		this.noteWithdrawal = noteWithdrawal;
	}
	
	public boolean withdrawAsNote() {
		return noteWithdrawal;
	}
	
	public boolean clientIsLoading() {
		return clientIsLoading;
	}
	
	public void setClientIsLoading(boolean clientIsLoading) {
		this.clientIsLoading = clientIsLoading;
	}
	
	public WalkToAction getWalkToAction() {
		return walkToAction;
	}

	public PlayerAttributes setWalkToTask(WalkToAction walkToTask) { 
		this.walkToAction = walkToTask;
		return this;
	}
	
	public long getSpecialRestoreTimer() {
		return specialRestoreTimer;
	}
	
	public void setSpecialRestoreTimer(long specialRestoreTimer) {
		this.specialRestoreTimer = specialRestoreTimer;
	}
	
	public boolean isFireImmuneEventRunning() {
		return fireImmunityEventRunning;
	}
	
	public void setFireImmunityEventRunning(boolean fireImmunityEventRunning) {
		this.fireImmunityEventRunning = fireImmunityEventRunning;
	}
	
	public int getFireImmunity() {
		return fireImmunity;
	}
	
	public PlayerAttributes setFireImmunity(int fireImmunity) {
		this.fireImmunity = fireImmunity;
		return this;
	}
	
	public int getFireDamagemodifier() {
		return fireDamageModifier;
	}
	
	public PlayerAttributes setFireDamageModifier(int fireDamageModifier) {
		this.fireDamageModifier = fireDamageModifier;
		return this;
	}
	
	public boolean isCoughing() {
		return coughing;
	}
	
	public void setCoughing(boolean coughing) {
		this.coughing = coughing;
	}
	
	public int getShadowState() {
		return shadowState;
	}
	
	public void setShadowState(int shadowState) {
		this.shadowState = shadowState;
	}
	
	public BankSearchAttributes getBankSearchingAttribtues() {
		return bankSearchAttributes;
	}
	
	public long getSqlDelay() {
		return sqlDelay;
	}
	
	public void setSqlDelay(long sqlDelay) {
		this.sqlDelay = sqlDelay;
	}
	
	public boolean updateHighscores() {
		return updateHighscores;
	}
	
	public void setUpdateHighscores(boolean updateHighscores) {
		this.updateHighscores = updateHighscores;
	}
	
	public RegionInstance getRegionInstance() {
		return regionInstance;
	}
	
	public void setRegionInstance(RegionInstance regionInstance) {
		this.regionInstance = regionInstance;
	}

	public boolean isClientExitTaskActive() {
		return clientExitTaskActive;
	}

	public void setClientExitTaskActive(boolean clientExitTaskActive) {
		this.clientExitTaskActive = clientExitTaskActive;
	}
	
	public boolean forceLogout() {
		return forceLogout;
	}
	
	public void setForceLogout(boolean forceLogout) {
		this.forceLogout = forceLogout;
	}
	
	public boolean loggedOut() {
		return loggedOut;
	}
	
	public void setLoggedOut(boolean loggedOut) {
		this.loggedOut = loggedOut;
	}
	
	/**
	 * The player's local players list.
	 */
	public List<Player> getLocalPlayers() {
		return localPlayers;
	}
	
	/**
	 * The player's local npcs list getter
	 */
	public List<NPC> getLocalNpcs() {
		return localNpcs;
	}

}