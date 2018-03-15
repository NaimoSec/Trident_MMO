package org.trident.world.content.minigames;

import java.util.ArrayList;

import org.trident.model.Item;

/**
 * Holds different minigame attributes for a player
 * @author Gabbe
 */
public class MinigameAttributes {

	private final ArcheryCompetitionAttributes archeryCompetition = new ArcheryCompetitionAttributes();
	private final BarrowsMinigameAttributes barrowsMinigameAttributes = new BarrowsMinigameAttributes();
	private final ConquestArenaAttributes conquestArenaAttributes = new ConquestArenaAttributes();
	private final FightCaveAttributes fightCaveAttributes = new FightCaveAttributes();
	private final MageArenaAttributes mageArenaAttributes = new MageArenaAttributes();

	private final FishingTrawlerAttributes fishingTrawlerAttributes = new FishingTrawlerAttributes();
	private final WarriorsGuildAttributes warriorsGuildAttributes = new WarriorsGuildAttributes();
	private final PestControlAttributes pestControlAttributes = new PestControlAttributes();
	private final RecipeForDisasterAttributes rfdAttributes = new RecipeForDisasterAttributes();
	private final NomadAttributes nomadAttributes = new NomadAttributes();
	private final SoulWarsAttributes soulWarsAttributes = new SoulWarsAttributes();
	private final GodwarsDungeonAttributes godwarsDungeonAttributes = new GodwarsDungeonAttributes();
	
	public class PestControlAttributes {
		
		public PestControlAttributes() {
			
		}
		
		private int damageDealt;
		
		public int getDamageDealt() {
			return damageDealt;
		}
		
		public void setDamageDealt(int damageDealt) {
			this.damageDealt = damageDealt;
		}
	}
	
	public class WarriorsGuildAttributes {
		
		public WarriorsGuildAttributes() {
			
		}
		
		private boolean[] data = new boolean[4];
		
		public boolean[] getData() {
			return data;
		}
	}
	
	public class FishingTrawlerAttributes {
		
		public FishingTrawlerAttributes() {
			
		}
		
		private ArrayList<Item> rewards;
		private boolean viewingInterface;
		private int amountOfActions;
		
		public ArrayList<Item> getRewards() {
			if(rewards == null)
				setRewards(new ArrayList<Item>());
			return rewards;
		}
		
		public void setRewards(ArrayList<Item> rewards) {
			this.rewards = rewards;
		}
		
		public boolean isViewingInterface() {
			return viewingInterface;
		}
		
		public FishingTrawlerAttributes setViewingInterface(boolean viewingInterface) {
			this.viewingInterface = viewingInterface;
			return this;
		}
		
		public int getAmountOfActions() {
			return amountOfActions;
		}
		
		public void setAmountOfActions(int amountOfActions) {
			this.amountOfActions = amountOfActions;
		}
	}
	
	public class FightCaveAttributes {
		
		public FightCaveAttributes() {}
		
		private int amountToKill;
		private int wave;
		
		public int getAmountToKill() {
			return amountToKill;
		}
		
		public FightCaveAttributes setAmountToKill(int amountToKill) {
			this.amountToKill = amountToKill;
			return this;
		}
		
		public int getWave() {
			return wave;
		}
		
		public FightCaveAttributes setWave(int wave) {
			this.wave = wave;
			return this;
		}
	}
	
	public class MageArenaAttributes {
		
		public MageArenaAttributes() {}
		
		private int amountToKill;
		private int wave;
		
		public int getAmountToKill() {
			return amountToKill;
		}
		
		public MageArenaAttributes setAmountToKill(int amountToKill) {
			this.amountToKill = amountToKill;
			return this;
		}
		
		public int getWave() {
			return wave;
		}
		
		public MageArenaAttributes setWave(int wave) {
			this.wave = wave;
			return this;
		}
	}
	
	public class ConquestArenaAttributes {
		
		public ConquestArenaAttributes() {
			
		}
		
		private boolean inArena;
		private int wave;
		private int amountToKill;
		
		public boolean isInArena() {
			return inArena;
		}
		
		public void setInArena(boolean inArena) {
			this.inArena = inArena;
		}
		
		public int getWave() {
			return wave;
		}
		
		public void setWave(int wave) {
			this.wave = wave;
		}
		
		public int getAmountToKill() {
			return amountToKill;
		}
		
		public void setAmountToKill(int amountToKill) {
			this.amountToKill = amountToKill;
		}
	}
	
	public class ArcheryCompetitionAttributes {
		
		public ArcheryCompetitionAttributes() {
			
		}
		
		private int targetsHit;
		
		public int getTargetsHit() {
			return targetsHit;
		}
		
		public ArcheryCompetitionAttributes setTargetsHit(int targetsHit) {
			this.targetsHit = targetsHit;
			return this;
		}
		private boolean eventRunning;
		
		public boolean eventIsRunning() {
			return eventRunning;
		}
		
		public ArcheryCompetitionAttributes setEventRunning(boolean eventRunning) {
			this.eventRunning = eventRunning;
			return this;
		}
		
		private boolean participating;
		
		public boolean isParticipating() {
			return participating;
		}
		
		public ArcheryCompetitionAttributes setParticipating(boolean participating) {
			this.participating = participating;
			return this;
		}
		
		private int[] score = new int[5];
		
		public int[] getScore() {
			return score;
		}
	}
	
	public class BarrowsMinigameAttributes {

		public BarrowsMinigameAttributes() {

		}
		
		private int killcount, randomCoffin, riddleAnswer = -1;

		public int getKillcount() {
			return killcount;
		}

		public void setKillcount(int killcount) {
			this.killcount = killcount;
		}

		public int getRandomCoffin() {
			return randomCoffin;
		}

		public void setRandomCoffin(int randomCoffin) {
			this.randomCoffin = randomCoffin;
		}

		public int getRiddleAnswer() {
			return riddleAnswer;
		}

		public void setRiddleAnswer(int riddleAnswer) {
			this.riddleAnswer = riddleAnswer;
		}

		private int[][] barrowsData = { //NPCID, state
				{ 2030, 0}, // verac
				{ 2029, 0 }, // toarg
				{ 2028, 0 }, // karil
				{ 2027, 0 }, // guthan
				{ 2026, 0 }, // dharok
				{ 2025, 0 } // ahrim
		};

		public int[][] getBarrowsData() {
			return barrowsData;
		}
	}
	
	public class RecipeForDisasterAttributes {
		public RecipeForDisasterAttributes() {}
		
		private int wavesCompleted;
		private boolean[] questParts = new boolean[9];
		
		public int getWavesCompleted() {
			return wavesCompleted;
		}
		
		public void setWavesCompleted(int wavesCompleted) {
			this.wavesCompleted = wavesCompleted;
		}
		
		public boolean hasFinishedPart(int index) {
			return questParts[index];
		}
		
		public void setPartFinished(int index, boolean finished) {
			questParts[index] = finished;
		}
	}
	
	public class NomadAttributes {
		public NomadAttributes() {}
		
		private boolean[] questParts = new boolean[3];
		
		public boolean hasFinishedPart(int index) {
			return questParts[index];
		}
		
		public void setPartFinished(int index, boolean finished) {
			questParts[index] = finished;
		}
	}
	
	public class SoulWarsAttributes {
		public SoulWarsAttributes(){}
		
		private int activity = 30;
		private int productChosen = -1;
		private int team = -1;
		
		public int getActivity() {
			return activity;
		}
		
		public void setActivity(int activity) {
			this.activity = activity;
		}
		
		public int getProductChosen() {
			return productChosen;
		}
		
		public void setProductChosen(int prodouctChosen) {
			this.productChosen = prodouctChosen;
		}
		
		public int getTeam() {
			return team;
		}
		
		public void setTeam(int team) {
			this.team = team;
		}
	}
	
	public class GodwarsDungeonAttributes {
		public GodwarsDungeonAttributes() {}
		
		private int[] killcount = new int[4]; // 0 = armadyl, 1 = bandos, 2 = saradomin, 3 = zamorak
		private boolean enteredRoom;
		private long altarDelay;
		
		public int[] getKillcount() {
			return killcount;
		}
		
		public boolean hasEnteredRoom() {
			return enteredRoom;
		}
		
		public void setHasEnteredRoom(boolean enteredRoom) {
			this.enteredRoom = enteredRoom;
		}
		
		public long getAltarDelay() {
			return altarDelay;
		}
		
		public GodwarsDungeonAttributes setAltarDelay(long altarDelay) {
			this.altarDelay = altarDelay;
			return this;
		}
	}
	
	public MinigameAttributes() {}
	
	public BarrowsMinigameAttributes getBarrowsMinigameAttributes() {
		return barrowsMinigameAttributes;
	}
	
	public ArcheryCompetitionAttributes getArcheryCompetitionAttributes() {
		return archeryCompetition;
	}
	
	public ConquestArenaAttributes getConquestArenaAttributes() {
		return conquestArenaAttributes;
	}
	
	public FightCaveAttributes getFightCaveAttributes() {
		return fightCaveAttributes;
	}
	
	public MageArenaAttributes getMageArenaAttributes() {
		return mageArenaAttributes;
	}
	
	public FishingTrawlerAttributes getFishingTrawlerAttributes() {
		return fishingTrawlerAttributes;
	}
	
	public WarriorsGuildAttributes getWarriorsGuildAttributes() {
		return warriorsGuildAttributes;
	}
	
	public PestControlAttributes getPestControlAttributes() {
		return pestControlAttributes;
	}
	
	public RecipeForDisasterAttributes getRecipeForDisasterAttributes() {
		return rfdAttributes;
	}
	
	public NomadAttributes getNomadAttributes() {
		return nomadAttributes;
	}
	
	public SoulWarsAttributes getSoulWarsAttributes() {
		return soulWarsAttributes;
	}
	
	public GodwarsDungeonAttributes getGodwarsDungeonAttributes() {
		return godwarsDungeonAttributes;
	}
	
}
