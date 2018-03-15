package org.trident.world.content.skills;

import org.trident.engine.task.Task;
import org.trident.world.content.skills.impl.dungeoneering.DungeoneeringFloor;
import org.trident.world.content.skills.impl.dungeoneering.DungeoneeringParty;

public class SkillAttributes {
	
	public SkillAttributes(){}
	
	/*
	 * Fields
	 */
	
	private Task currentTask;
	private int selectedItem;
	private final AgilityAttributes agilityAttributes = new AgilityAttributes();
	private final HerbloreAttributes herbloreAttributes = new HerbloreAttributes();
	private final HunterAttributes hunterAttributes = new HunterAttributes();
	private final FletchingAttributes fletchingAttributes = new FletchingAttributes();
	private final DungeoneeringAttributes dungeoneeringAttributes = new DungeoneeringAttributes();
	private final RunecraftingAttributes runecraftingAttributes = new RunecraftingAttributes();
	
	public AgilityAttributes getAgilityAttributes() {
		return agilityAttributes;
	}
	
	public HerbloreAttributes getHerbloreAttributes() {
		return herbloreAttributes;
	}
	
	public HunterAttributes getHunterAttributes() {
		return hunterAttributes;
	}
	
	public FletchingAttributes getFletchingAttributes() {
		return fletchingAttributes;
	}
	
	public DungeoneeringAttributes getDungeoneeringAttributes() {
		return dungeoneeringAttributes;
	}
	
	public RunecraftingAttributes getRunecraftingAttributes() {
		return runecraftingAttributes;
	}
	
	public int getSelectedItem() {
		return selectedItem;
	}
	
	public SkillAttributes setSelectedItem(int selectedItem) {
		this.selectedItem = selectedItem;
		return this;
	}
	
	public Task getCurrentTask() {
		return currentTask;
	}
	
	public SkillAttributes setCurrentTask(Task currentTask) {
		this.currentTask = currentTask;
		return this;
	}
	
	/*
	 * Attribute classes
	 */
	
	
	public class AgilityAttributes {
		
		public AgilityAttributes() {}
		
		private boolean crossingObstacle;
		private boolean[] crossedObstacles = new boolean[7];
		private int animation;
		
		public boolean isCrossingObstacle() {
			return crossingObstacle;
		}
		
		public AgilityAttributes setCrossingObstacle(boolean crossingObstacle) {
			this.crossingObstacle = crossingObstacle;
			return this;
		}
		

		public boolean[] getCrossedObstacles() {
			return crossedObstacles;
		}
		
		public boolean getCrossedObstacle(int i) {
			return crossedObstacles[i];
		}
		
		public AgilityAttributes setCrossedObstacle(int i, boolean completed) {
			crossedObstacles[i] = completed;
			return this;
		}
		
		public int getAnimation() {
			return animation;
		}
		
		public AgilityAttributes setAnimation(int animation) {
			this.animation = animation;
			return this;
		}
	}
	
	public class HerbloreAttributes {
		public HerbloreAttributes() {
			
		}
		
		private int currentBookPage;
		
		public int getCurrentBookPage() {
			return currentBookPage;
		}
		
		public void setCurrentBookPage(int currentBookPage) {
			this.currentBookPage = currentBookPage;
		}
	}
	
	public class HunterAttributes {
		public HunterAttributes() {
			
		}
		
		private int trapsLaid;
		
		public int getTrapsLaid() {
			return trapsLaid;
		}
		
		public void setTrapsLaid(int trapsLaid) {
			this.trapsLaid = trapsLaid;
		}
	}
	
	public class FletchingAttributes {
		public FletchingAttributes() {
			
		}
		
		private int[] products;
		
		public int[] getProduct() {
			return products;
		}
		
		public void setProducts(int[] products) {
			this.products = products;
		}
	}
	
	public class DungeoneeringAttributes {
		public DungeoneeringAttributes(){}
		
		private DungeoneeringFloor dungeoneeringFloor;
		private int experienceReceived, tokensReceived;
		private long lastInvitation;
		private DungeoneeringParty partyInvitation;
		
		public DungeoneeringFloor getDungeoneeringFloor() {
			return dungeoneeringFloor;
		}

		public void setDungeoneeringFloor(DungeoneeringFloor dungeoneeringFloor) {
			this.dungeoneeringFloor = dungeoneeringFloor;
		}

		public int getExperienceReceived() {
			return experienceReceived;
		}
		
		public DungeoneeringAttributes setExperienceReceived(int experienceReceived) {
			this.experienceReceived = experienceReceived;
			return this;
		}
		
		public int getTokensReceived() {
			return tokensReceived;
		}
		
		public void setTokensReceived(int tokensReceived) {
			this.tokensReceived = tokensReceived;
		}
		
		private int[] boundItems = new int[5];

		public int[] getBoundItems() {
			return boundItems;
		}
		
		public long getLastInvitation() {
			return lastInvitation;
		}
		
		public void setLastInvitation(long lastInvitation) {
			this.lastInvitation = lastInvitation;
		}
		
		public DungeoneeringParty getPartyInvitation() {
			return partyInvitation;
		}
		
		public void setPartyInvitation(DungeoneeringParty partyInvitation) {
			this.partyInvitation = partyInvitation;
		}
	}
	
	public class RunecraftingAttributes {

		public RunecraftingAttributes() {}
		
		private int storedRuneEssence, storedPureEssence;
		
		public int getStoredRuneEssence() {
			return storedRuneEssence;
		}
		
		public void setStoredRuneEssence(int storedRuneEssence) {
			this.storedRuneEssence = storedRuneEssence;
		}
		
		public int getStoredPureEssence() {
			return storedPureEssence;
		}
		
		public void setStoredPureEssence(int storedPureEssence) {
			this.storedPureEssence = storedPureEssence;
		}
	}
}
