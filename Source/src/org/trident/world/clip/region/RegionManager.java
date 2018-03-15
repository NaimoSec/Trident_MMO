package org.trident.world.clip.region;

import java.util.HashMap;
import java.util.Map;

import org.trident.model.Position;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCRegion;
import org.trident.world.entity.npc.NPCRegion.RegionPosition;

/**
 * This class handles all the {@link NPCRegion}s in the
 * world.
 *
 * @author relex lawl
 */
public final class RegionManager {

	/**
	 * This map contains all the active {@link NPCRegion}s
	 * in the world.
	 */
	private static final Map<RegionPosition, NPCRegion> regions = new HashMap<RegionPosition, NPCRegion>();

	/**
	 * Registers an {@link org.niobe.model.Entity} to their
	 * corresponding {@link NPCRegion}.
	 * @param entity	The {@link org.niobe.model.Entity} to register.
	 * @return			The {@link NPCRegion} the entity is being registered to.
	 */
	public static void register(NPC n) {
		if (n.getRegion() != null) {
			unregister(n);
		}
		NPCRegion region = forPosition(n.getPosition());
		n.setRegion(region);
		n.getRegion().getNpcs().add(n);
	}

	/**
	 * Unregisters the {@link org.niobe.model.Entity} from their
	 * current {@link NPCRegion}.
	 * @param entity	The {@link org.niobe.model.Entity} to unregister.
	 */
	public static void unregister(NPC n) {
		if(n.getRegion() != null)
			n.getRegion().getNpcs().remove(n);
	}

	/**
	 * Gets the corresponding {@link NPCRegion} for said
	 * position and checks if the {@link #regions} map contains
	 * the {@link NPCRegion} to avoid making new instances.
	 * @param position	The {@link org.niobe.model.Position} to get {@link NPCRegion} for.
	 * @return			The corresponding {@link NPCRegion}.
	 */
	public static NPCRegion forPosition(Position position) {
		RegionPosition regionPosition = new RegionPosition(position.getX(), position.getY(), position.getZ());
		NPCRegion region = regions.get(regionPosition);
		if (region != null) {
			return region;
		}
		region = new NPCRegion(regionPosition);
		regions.put(regionPosition, region);
		return region;
	}
}
