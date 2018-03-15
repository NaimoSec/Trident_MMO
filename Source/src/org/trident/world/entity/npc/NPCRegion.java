package org.trident.world.entity.npc;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a "big" region in the world, which is made
 * up of the surrounding regions.
 *
 * @author relex lawl
 */
public final class NPCRegion {
	
	/**
	 * The Region constructor.
	 * @param position	The {@link RegionPosition} for this {@link NPCRegion}.
	 */
	public NPCRegion(RegionPosition position) {
		this.position = position;
	}
	
	/**
	 * The corresponding {@link RegionPosition}.
	 */
	private final RegionPosition position;
	
	public RegionPosition getPosition() {
		return position;
	}
	
	/**
	 * This list contains local {@link org.niobe.world.Mob}s
	 * in this {@link NPCRegion}.
	 */
	private final List<NPC> npcs = new LinkedList<NPC>();
	
	public List<NPC> getNpcs() {
		return npcs;
	}
	
	/**
	 * Represents a {@link RegionPosition} which holds
	 * the coordinates, which are then shifted and altered
	 * to have the same value for the surrounding regions
	 * in which {@link org.niobe.model.Entity}s will be stored.
	 *
	 * @author relex lawl
	 */
	public static final class RegionPosition {
		
		/**
		 * The RegionPosition constructor.
		 * @param x	The x position coordinate.
		 * @param y	The y position coordinate.
		 * @param z	The height position.
		 */
		public RegionPosition(int x, int y, int z) {
			this.x = ((x >> 3) - 8) / 16;
			this.y = ((y >> 3)) / 16;
			this.z = z;
		}
		
		/**
		 * The region's x-coordinate, will be the same
		 * for the surrounding regions.
		 */
		private final int x;
		
		/**
		 * The region's y-coordinate, will be the same
		 * for the surrounding regions.
		 */
		private final int y;
		
		/**
		 * The region's height-coordinate, will be the same
		 * for the surrounding regions.
		 */
		private final int z;
		
		@Override
		public int hashCode() {
			int hash = x & y | z;
			return hash;
		}
		
		@Override
		public boolean equals(Object object) {
			if (object.getClass() != getClass()) {
				return false;
			}
			RegionPosition other = (RegionPosition) object;
			return other.x == x && other.y == y && other.z == z;
		}
		
		@Override
		public String toString() {
			return "NPCRegion position values: [" + x + "; " + y + "; " + z + "]";
		}
	}
}
