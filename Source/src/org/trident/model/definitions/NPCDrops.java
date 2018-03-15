package org.trident.model.definitions;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.trident.model.Graphic;
import org.trident.model.GroundItem;
import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.util.XStreamLibrary;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.minigames.impl.WarriorsGuild;
import org.trident.world.content.skills.impl.prayer.BonesData;
import org.trident.world.content.treasuretrails.ClueScroll;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

/**
 * Controls the npc drops
 * 
 * @author 2012 <http://www.rune-server.org/members/dexter+morgan/>, Gabbe & Samy
 * 
 */
public class NPCDrops {

	/**
	 * The map containing all the npc drops.
	 */
	private static Map<Integer, NPCDrops> dropControllers;

	@SuppressWarnings("unchecked")
	public static void init()  {
		List<NPCDrops> list  = null;
		try {
			list = (List<NPCDrops>) XStreamLibrary
					.load("./data/config/npcDrops.xml");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(list == null)
			return;
		dropControllers = new HashMap<Integer, NPCDrops>();
		for (NPCDrops npcDrop : list) {
			for (int id : npcDrop.getNpcIds()) {
				dropControllers.put(id, npcDrop);
			}
		}
		//System.out.println("Loaded " + dropControllers.size() + " npc drops.");
	}

	/**
	 * The id's of the NPC's that "owns" this class.
	 */
	private int[] npcIds;

	/**
	 * All the drops that belongs to this class.
	 */
	private NpcDropItem[] drops;

	/**
	 * Gets the NPC drop controller by an id.
	 * 
	 * @return The NPC drops associated with this id.
	 */
	public static NPCDrops forId(int id) {
		return dropControllers.get(id);
	}

	/**
	 * Gets the drop list
	 * 
	 * @return the list
	 */
	public NpcDropItem[] getDropList() {
		return drops;
	}

	/**
	 * Gets the npcIds
	 * 
	 * @return the npcIds
	 */
	public int[] getNpcIds() {
		return npcIds;
	}

	/**
	 * Represents a npc drop item
	 */
	public static class NpcDropItem {

		/**
		 * The id.
		 */
		private final int id;

		/**
		 * Array holding all the amounts of this item.
		 */
		private final int[] count;

		/**
		 * The chance of getting this item.
		 */
		private final int chance;

		/**
		 * New npc drop item
		 * 
		 * @param id
		 *            the item
		 * @param count
		 *            the count
		 * @param chance
		 *            the chance
		 */
		public NpcDropItem(int id, int[] count, int chance) {
			this.id = id;
			this.count = count;
			this.chance = chance;
		}

		/**
		 * Gets the item id.
		 * 
		 * @return The item id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the chance.
		 * 
		 * @return The chance.
		 */
		public int[] getCount() {
			return count;
		}

		/**
		 * Gets the chance.
		 * 
		 * @return The chance.
		 */
		public DropChance getChance() {
			switch (chance) {
			case 1:
				return DropChance.ALMOST_ALWAYS; // 50% <-> 1/2
			case 2:
				return DropChance.VERY_COMMON; // 20% <-> 1/5
			case 3:
				return DropChance.COMMON; // 5% <-> 1/20
			case 4:
				return DropChance.UNCOMMON; // 2% <-> 1/50
			case 5:
				return DropChance.RARE; // 0.5% <-> 1/200
			case 6:
			case 7:
			case 8:
			case 9:
				return DropChance.LEGENDARY; // 0.2% <-> 1/500
			case 10:
				return DropChance.LEGENDARY_2;
			case 12:
				return DropChance.NOTTHATRARE; //1 %
			default:
				return DropChance.ALWAYS;	// 100% <-> 1/1
			}
		}

		/**
		 * Gets the item
		 * 
		 * @return the item
		 */
		public Item getItem() {
			int amount = 0;
			for (int i = 0; i < count.length; i++)
				amount += count[i];
			if (amount > count[0])
				amount = count[0] + Misc.getRandom(count[1]);
			return new Item(id, amount);
		}
	}

	public enum DropChance {
		ALWAYS(0), ALMOST_ALWAYS(3), VERY_COMMON(6), COMMON(20), UNCOMMON(60), NOTTHATRARE(100), RARE(200), LEGENDARY(400), LEGENDARY_2(600), NOPE(800);
		DropChance(int randomModifier) {
			this.random = randomModifier;
		}
		private int random;
		
		public int getRandom() {
			return this.random;
		}
	}

	/**
	 * Drops items for a player after killing an npc. 
	 * A player can max receive one item per drop chance.
	 * @param p	Player to receive drop.	
	 * @param npc NPC to receive drop FROM.
	 */
	public static void dropItems(Player p, NPC npc) {
		if(npc.getLocation() == Location.WARRIORS_GUILD)
			WarriorsGuild.handleDrop(p, npc);
		NPCDrops drops = NPCDrops.forId(npc.getId());
		if(drops == null) 
			return;
		ClueScroll.dropClue(p, npc);
		final boolean goGlobal = p.getPosition().getZ() >= 0 && p.getPosition().getZ() < 4;
		final Position npcPos = npc.getPosition().copy();
		boolean[] dropsReceived = new boolean[13];
		for (int i = 0; i < drops.getDropList().length; i++) {
			if(p.getInventory().contains(18337) && ItemDefinition.forId(drops.getDropList()[i].getItem().getId()).getName().toLowerCase().contains("bones")) {
				p.performGraphic(new Graphic(777));
				p.getSkillManager().addExperience(Skill.PRAYER, BonesData.forId(drops.getDropList()[i].getItem().getId()) != null ? BonesData.forId(drops.getDropList()[i].getItem().getId()).getBuryingXP() + 10 : 0, false);
				continue;
			}
			if(drops.getDropList()[i].getItem().getId() <= 0)
				continue;
			final DropChance dropChance = drops.getDropList()[i].getChance();
			if (dropChance == DropChance.ALWAYS)
				GroundItemManager.spawnGroundItem(p, new GroundItem(drops.getDropList()[i].getItem(), npcPos, p.getUsername(), false, 150, goGlobal, 200));
			else if(dropChance == DropChance.ALMOST_ALWAYS && shouldDrop(dropsReceived, dropChance)) {
				GroundItemManager.spawnGroundItem(p, new GroundItem(drops.getDropList()[i].getItem(), npcPos, p.getUsername(), false, 150, goGlobal, 200));
				dropsReceived[dropChance.ordinal()] = true;
			} else if(dropChance == DropChance.VERY_COMMON && shouldDrop(dropsReceived, dropChance)) {
				GroundItemManager.spawnGroundItem(p, new GroundItem(drops.getDropList()[i].getItem(), npcPos, p.getUsername(), false, 150, goGlobal, 200));
				dropsReceived[dropChance.ordinal()] = true;
			} else if(dropChance == DropChance.COMMON && shouldDrop(dropsReceived, dropChance)) {
				GroundItemManager.spawnGroundItem(p, new GroundItem(drops.getDropList()[i].getItem(), npcPos, p.getUsername(), false, 150, goGlobal, 200));
				dropsReceived[dropChance.ordinal()] = true;
			} else if(dropChance == DropChance.UNCOMMON && shouldDrop(dropsReceived, dropChance)) {
				GroundItemManager.spawnGroundItem(p, new GroundItem(drops.getDropList()[i].getItem(), npcPos, p.getUsername(), false, 150, goGlobal, 200));
				dropsReceived[dropChance.ordinal()] = true;
			} else if(dropChance == DropChance.NOTTHATRARE && shouldDrop(dropsReceived, dropChance)) {
				GroundItemManager.spawnGroundItem(p, new GroundItem(drops.getDropList()[i].getItem(), npcPos, p.getUsername(), false, 150, goGlobal, 200));
				dropsReceived[dropChance.ordinal()] = true;
			} else if(dropChance == DropChance.RARE && shouldDrop(dropsReceived, dropChance)) {
				GroundItemManager.spawnGroundItem(p, new GroundItem(drops.getDropList()[i].getItem(), npcPos, p.getUsername(), false, 150, goGlobal, 200));
				dropsReceived[dropChance.ordinal()] = true;
			} else if(dropChance == DropChance.LEGENDARY && shouldDrop(dropsReceived, dropChance)) {
				GroundItemManager.spawnGroundItem(p, new GroundItem(drops.getDropList()[i].getItem(), npcPos, p.getUsername(), false, 150, goGlobal, 200));
				dropsReceived[dropChance.ordinal()] = true;
			} else if(dropChance == DropChance.NOPE && shouldDrop(dropsReceived, dropChance)) {
				GroundItemManager.spawnGroundItem(p, new GroundItem(drops.getDropList()[i].getItem(), npcPos, p.getUsername(), false, 150, goGlobal, 200));
				dropsReceived[dropChance.ordinal()] = true;
			}
		}
	}
	
	public static boolean shouldDrop(boolean[] b, DropChance chance) {
		return !b[chance.ordinal()] && Misc.getRandom(chance.getRandom()) == 1;
	}
}