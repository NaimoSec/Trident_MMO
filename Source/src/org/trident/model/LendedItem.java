package org.trident.model;


public class LendedItem extends Item {

	public LendedItem(int id) {
		super(id);
	}

	public String getItemOwner() {
		return itemOwner;
	}
	public void setItemOwner(String itemOwner) {
		this.itemOwner = itemOwner;
	}

	public String getItemLoaner() {
		return itemLoaner;
	}

	public void setItemLoaner(String itemLoaner) {
		this.itemLoaner = itemLoaner;
	}

	public long getStartMilliS() {
		return startMilliS;
	}

	public void setStartMilliS(long startMilliS) {
		this.startMilliS = startMilliS;
	}

	public long getReturnMilliS() {
		return returnMilliS;
	}

	public void setReturnMilliS(long returnMilliS) {
		this.returnMilliS = returnMilliS;
	}
	
	public boolean itemHasReturned() {
		return this.itemReturnedToOwner;
	}
	
	public void setItemReturned(boolean s) {
		this.itemReturnedToOwner = s;
	}

	private String itemOwner = null;
	private String itemLoaner = null;
	private long startMilliS;
	private long returnMilliS;
	private boolean itemReturnedToOwner;
}
