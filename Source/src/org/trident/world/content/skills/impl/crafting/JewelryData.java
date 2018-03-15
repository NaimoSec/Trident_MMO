package org.trident.world.content.skills.impl.crafting;

public class JewelryData {
	public static enum jewelryData {

		RINGS(new int[][] {{2357, 1635, 5, 15}, {1607, 1637, 20, 40}, {1605, 1639, 27, 55}, {1603, 1641, 34, 70}, {1601, 1643, 43, 85}, {1615, 1645, 55, 100}, {6573, 6575, 67, 115}}),
		NECKLACE(new int[][] {{2357, 1654, 6, 20}, {1607, 1656, 22, 55}, {1605, 1658, 29, 60}, {1603, 1660, 40, 75}, {1601, 1662, 56, 90}, {1615, 1664, 72, 105}, {6573, 6577, 82, 120}}),
		AMULETS(new int[][] {{2357, 1673, 8, 30}, {1607, 1675, 24, 65}, {1605, 1677, 31, 70}, {1603, 1679, 50, 85}, {1601, 1681, 70, 100}, {1615, 1683, 80, 150}, {6573, 6579, 90, 165}});

		public int[][] item;

		private jewelryData(final int[][] item) {
			this.item = item;
		}	
	}

	public static enum amuletData {
		GOLD(1673, 1692),
		SAPPHIRE(1675, 1694),
		EMERALD(1677, 1696),
		RUBY(1679, 1698),
		DIAMOND(1681, 1700),
		DRAGONSTONE(1683, 1702),
		ONYX(6579, 6581);

		private int amuletId, product;

		private amuletData(final int amuletId, final int product) {
			this.amuletId = amuletId;
			this.product = product;
		}

		public int getAmuletId() {
			return amuletId;
		}

		public int getProduct() {
			return product;
		}
	}
}
