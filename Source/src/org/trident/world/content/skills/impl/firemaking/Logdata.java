package org.trident.world.content.skills.impl.firemaking;

import org.trident.world.entity.player.Player;


public class Logdata {

	public static enum logData {
		
		LOG(1511, 1, 437, 50),
		ACHEY(2862, 1, 438, 60),
		OAK(1521, 15, 590, 70),
		WILLOW(1519, 30, 980, 80),
		TEAK(6333, 35, 1437, 90),
		ARCTIC_PINE(10810, 42, 1725, 100),
		MAPLE(1517, 45, 1900, 115),
		MAHOGANY(6332, 50, 2300, 120),
		EUCALYPTUS(12581, 58, 3230, 130),
		YEW(1515, 60, 4700, 150),
		MAGIC(1513, 75, 9337, 180);
		
		private int logId, level, burnTime;
		private int xp;
		
		private logData(int logId, int level, int xp, int burnTime) {
			this.logId = logId;
			this.level = level;
			this.xp = xp;
			this.burnTime = burnTime;
		}
		
		public int getLogId() {
			return logId;
		}
		
		public int getLevel() {
			return level;
		}
		
		public int getXp() {
			return xp;
		}		
		
		public int getBurnTime() {
			return this.burnTime;
		}
	}

	public static logData getLogData(Player p, int log) {
		for (final Logdata.logData l : Logdata.logData.values()) {
			if(log == l.getLogId() || log == -1 && p.getInventory().contains(l.getLogId())) {
				return l;
			}
		}
		return null;
	}

}