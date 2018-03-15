package org.trident.world.content;

import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.GameObject;
import org.trident.model.Position;
import org.trident.world.World;
import org.trident.world.clip.region.RegionClipping;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.skills.impl.dungeoneering.Dungeoneering;
import org.trident.world.entity.player.Player;

/**
 * Handles Custom Objects
 * @author Gabbe
 */

public class CustomObjects {
	/**
	 * Contains
	 * @param ObjectId - The object ID to spawn
	 * @param absX - The X position of the object to spawn
	 * @param absY - The Y position of the object to spawn
	 * @param Z - The Z position of the object to spawn
	 * @param face - The position the object will face
	 */
	public static final int[][] CUSTOM_OBJECTS = {
		{6403, 2732, 5074, 0, 0},//Strykewyrm tunnel blocking
		{6403, 2732, 5073, 0, 0},
		{6403, 2729, 5098, 0, 0},
		{6403, 2728, 5097, 0, 0},
		{6403, 2744, 5106, 0, 0},
		{6403, 2744, 5105, 0, 0},
		/**New Member Zone (Not G.E)*/
        {2344, 3421, 2908, 0, 0}, //Rock blocking
        {2345, 3438, 2909, 0, 0},
        {2344, 3435, 2909, 0, 0},
        {2344, 3432, 2909, 0, 0},
        {2345, 3431, 2909, 0, 0},
        {2343, 3434, 2925, 0, 0},
        {2343, 3435, 2924, 0, 0},
        {2343, 3436, 2923, 0, 0},
        {2343, 3437, 2922, 0, 0},
        {2344, 3417, 2913, 0, 1},
        {2344, 3417, 2916, 0, 1},
        {2344, 3417, 2919, 0, 1},
        {2344, 3417, 2922, 0, 1},
        {2345, 3417, 2912, 0, 0},
        {2346, 3418, 2925, 0, 0},
        {61, 3426, 2930, 0, 0}, //Altar
        {-1, 3420, 2909, 0, 10}, //Remove crate by mining
        {-1, 3420, 2923, 0, 10}, //Remove Rockslide by Woodcutting
        {14859, 3421, 2909, 0, 0}, //Mining
        {14859, 3419, 2909, 0, 0},
        {14859, 3418, 2910, 0, 0},
        {14859, 3418, 2911, 0, 0},
        {14859, 3422, 2909, 0, 0},
        {1306, 3418, 2921, 0, 0}, //Woodcutting
        {1306, 3421, 2924, 0, 0},
        {1306, 3420, 2924, 0, 0},
        {1306, 3419, 2923, 0, 0},
        {1306, 3418, 2922, 0, 0},
        { 39515, 3430, 2928, 0, 1}, //Portals
        { 11356, 3424, 2931, 0, 0},
        { 47180, 3423, 2928, 0, 0},
        { 8702, 3424, 2911, 0, 0}, //Fishing Barrel
		{ 20331, 3428, 2921, 0, 2}, //Daggers stall
        { 2465, 2834, 9521, 0, 0}, //Zone Objects
        { 548, 2844, 9516, 0, 0},
        { 548, 2846, 9520, 0, 0},
        { 436, 2846, 9519, 0, 0},
        { 438, 2846, 9518, 0, 1},
        { 549, 2834, 9505, 0, 0},
        { 551, 2838, 9503, 0, 0},
        { 551, 2836, 9505, 0, 1},
        { 436, 2837, 9504, 0, 3},
        { 32307, 2860, 9742, 0, 0},
        { 32307, 2859, 9742, 0, 0},
        { 2465, 2861, 9735, 0, 0},
        { -1, 2860, 9734, 0, 0},
        /**New Member Zone end*/
		{ 1754, 3209, 3216, 0, 1}, //Lumbridge kitchen ladder
		{ 23093, 2912, 5299, 2, 0},
		{ 23093, 2914, 5300, 1, 0},
		{ 23093, 2920, 5276, 1, 0},
		{ 23093, 2920, 5274, 0, 0},
		{ -1, 3095, 3498, 0, 0},
		{ -1, 3095, 3499, 0, 0},
		{ 1749, 2607, 9666, 0, 2},
		{ 1749, 2607, 9680, 0, 2},
		{ -1, 2606, 9668, 4, 2},
		{ -1, 2606, 9668, 0, 2},
		{ -1, 3212, 3256, 0, 2},
		{ 38700, 3085, 3509, 0, 1},
		{ 4483, 2331, 3686, 0, 2},
		{ 4483, 2330, 3686, 0, 2},
		{ 4483, 2676, 3163, 0, 2},
		{ 1171, 2523, 9463, 0, 0}, //Slash bash anti safing
		{ 1171, 2524, 9462, 0, 0}, //Slash bash anti safing
		{ 1171, 2521, 9465, 0, 0}, //Slash bash anti safing
		{ 1171, 2519, 9467, 0, 0}, //Slash bash anti safing
		{ 172, 3100, 3506, 0, 2},
		{ 2783, 3229, 3254, 0, 1},
		{ 172, 3202, 3240, 0, 1},
		{ 2783, 3229, 3254, 0, 1},
		{ 2466, 2841, 4828, 0, 0}, //Fixed cheat teleport to dzone
		{ 1171, 2526, 9458, 0, 1},
		{ 14859, 3054, 9746, 0, 1},
		{ 14859, 3048, 9754, 0, 1},
		{ -1, 3033, 9740, 0, 1},
		{ 1, 2656, 2646, 0, 1},
		{ 29946, 3090, 3507, 0, 0},
		{ 409, 2093, 3918, 0, 1},
		{ 411, 2096, 3919, 0, 1},
		{ 6552, 3341, 2961, 0, 0},
		//{ 8748, 2736, 3448, 0, 0}, //spin wheel
		{ -1, 2673, 3421, 0, 1},
		{ 11758, 3019, 9740, 0, 0},
		{ 11758, 3020, 9739, 0, 1},
		{ 11758, 3019, 9738, 0, 2},
		{ 11758, 3018, 9739, 0, 3},
		{ 11933, 3028, 9739, 0, 1},
		{ 11933, 3032, 9737, 0, 2},
		{ 11933, 3032, 9735, 0, 0},
		{ 11933, 3035, 9742, 0, 3},
		{ 11933, 3034, 9739, 0, 0},
		{ 11936, 3028, 9737, 0, 1},
		{ 11936, 3029, 9734, 0, 2},
		{ 11936, 3031, 9739, 0, 0},
		{ 11936, 3032, 9741, 0, 3},
		{ 11936, 3035, 9734, 0, 0},
		{ 11954, 3037, 9739, 0, 1},
		{ 11954, 3037, 9735, 0, 2},
		{ 11954, 3037, 9733, 0, 0},
		{ 11954, 3039, 9741, 0, 3},
		{ 11954, 3039, 9738, 0, 0},
		{ 11963, 3039, 9733, 0, 1},
		{ 11964, 3040, 9732, 0, 2},
		{ 11965, 3042, 9734, 0, 0},
		{ 11965, 3044, 9737, 0, 3},
		{ 11963, 3042, 9739, 0, 0},
		{ 11963, 3045, 9740, 0, 1},
		{ 11965, 3043, 9742, 0, 2},
		{ 11964, 3045, 9744, 0, 0},
		{ 11965, 3048, 9747, 0, 3},
		{ 11951, 3048, 9743, 0, 0},
		{ 11951, 3049, 9740, 0, 1},
		{ 11951, 3047, 9737, 0, 2},
		{ 11951, 3050, 9738, 0, 0},
		{ 11951, 3052, 9739, 0, 3},
		{ 11951, 3051, 9735, 0, 0},
		{ 11947, 3049, 9735, 0, 1},
		{ 11947, 3049, 9734, 0, 2},
		{ 11947, 3047, 9733, 0, 0},
		{ 11947, 3046, 9733, 0, 3},
		{ 11947, 3046, 9735, 0, 0},
		{ 11941, 3053, 9737, 0, 1},
		{ 11939, 3054, 9739, 0, 2},
		{ 11941, 3053, 9742, 0, 0},
		{ 14859, 3038, 9748, 0, 3},
		{ 14859, 3044, 9753, 0, 0},
		{ 14859, 3048, 9754, 0, 1},
		{ 14859, 3054, 9746, 0, 2},
		{ 2182, 3219, 9623, 0, 3},
		{ 12356, 1863, 5317, 0, 3},
		{ 30821, 3050, 3499, 1, 3},
		{ 26814, 3185, 3425, 0, 0},
		{ 26814, 3227, 3440, 0, 0},
		{ 20987, 2927, 3444, 0, 0},
		{ 11758, 3449, 3722, 0, 0},
		{ 11758, 3450, 3722, 0, 0},
		{ 50547, 3445, 3717, 0, 3},
        //Armadyl altar
        { 47120, 2460, 4770, 0, 0},
        //Edge
        { 6448, 3098, 3488, 0, 3},
        { -1, 3098, 3496, 0, 0},
        { -1, 3090, 3503, 0, 0},
        //Farming
        { -1, 3056, 3312, 0, 0},
        { -1, 3058, 3308, 0, 0},
        { -1, 3058, 3307, 0, 0},
        { -1, 3058, 3306, 0, 0},
        { -1, 3058, 3305, 0, 0},
        { -1, 3058, 3304, 0, 0},
        { -1, 3058, 3303, 0, 0},
        { -1, 3055, 3304, 0, 0},
        { -1, 3054, 3304, 0, 0},
        { -1, 3053, 3304, 0, 0},
        { -1, 3056, 3304, 0, 0},
        { -1, 3057, 3304, 0, 0},
        { -1, 3055, 3303, 0, 0},
        { -1, 3056, 3303, 0, 0},
        { -1, 3057, 3303, 0, 0},
        { -1, 3059, 3308, 0, 0},
        { -1, 3059, 3307, 0, 0},
        { -1, 3059, 3306, 0, 0},
        { -1, 3059, 3305, 0, 0},
        { -1, 3059, 3304, 0, 0},
        { -1, 3059, 3303, 0, 0},
        //Thieving
        { 13290, 2581, 3309, 0, 0},
        { 1, 2578, 3306, 0, 0},
        { 1, 2578, 3305, 0, 0},
        //Agility
        { -1, 2542, 3553, 0, 0},
        { 9326, 3001, 3960, 0, 0},
        { 2467, 3003, 3931, 0, 0},
        //Desospan
        { -1, 2601, 4774, 0, 0},
        { -1, 2611, 4776, 0, 0},
        //Fishing
        { 2028, 2343, 3702, 0, 0},
        { 2028, 2342, 3702, 0, 0},
        { 2028, 2341, 3702, 0, 0},
        { 2028, 2348, 3702, 0, 0},
        { 2028, 2349, 3702, 0, 0},
        { 2029, 2336, 3703, 0, 0},
        { 2029, 2335, 3703, 0, 0},
        { 2029, 2334, 3703, 0, 0},
        { 2030, 2329, 3701, 0, 0},
        { 2030, 2328, 3700, 0, 0},
        { 2030, 2327, 3700, 0, 0},
        { 2030, 2319, 3702, 0, 0},
        { 2031, 2318, 3702, 0, 0},
        { -1, 2311, 3696, 0, 0},
        { 2026, 2307, 3700, 0, 0},
        { 2026, 2307, 3701, 0, 0},
	};

	/**
	 * Inserts custom objects into the array list
	 */
	public static void initalizeObjects() {
		for(int i = 0; i < CUSTOM_OBJECTS.length; i++) {
			int id = CUSTOM_OBJECTS[i][0];
			int x = CUSTOM_OBJECTS[i][1];
			int y = CUSTOM_OBJECTS[i][2];
			int z = CUSTOM_OBJECTS[i][3];
			int face = CUSTOM_OBJECTS[i][4];
			GameObject object = new GameObject(id, new Position(x, y, z));
			object.setFace(face);
			customObjects.add(object);
			World.register(object);
			object = null;
		}
	}

	/**
	 * Checks if a object exists in the specified position.	
	 * @param pos	Specified position.
	 * @return		true if object exists or false if it does not.
	 */
	public static boolean objectExists(Position pos) {
		for(GameObject objects : customObjects) {
			if(objects != null && objects.getPosition().equals(pos)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a object exists in the specified position and returns it.	
	 * @param pos	Specified position.
	 * @return		the gameobject at a specific position
	 */
	public static GameObject getGameObject(Position pos) {
		for(GameObject objects : customObjects) {
			if(objects != null && objects.getPosition().equals(pos)) {
				return objects;
			}
		}
		return null;
	}

	/**
	 * Spawns custom objects that are inside the array upon entering a new region
	 * @param c
	 */
	public static void handleRegionChange(Player c) {
		for(GameObject obj: customObjects) {
			if(obj == null)
				continue;
			if(Locations.goodDistance(c.getPosition().copy(), obj.getPosition().copy(), 62))
				spawnObject(c, obj);
		}
	}

	/**
	 * Deletes an object
	 * @param c - The player to delete the object for
	 * @param object - The object to delete
	 */
	public static void deleteObject(Player c, GameObject object) {
		c.getPacketSender().sendObjectRemoval(object);
	}

	/**
	 * Spawns an object
	 * @param c - The player to spawn the object for
	 * @param object - The object to spawn
	 */

	public static void spawnObject(Player c, GameObject object) {
		if(object.getId() == -1) {
			deleteObject(c, object);
			if(RegionClipping.objectExists(object))
				RegionClipping.removeObject(object);
		} else {
			c.getPacketSender().sendObject(object);
			if(!RegionClipping.objectExists(object))
				RegionClipping.addObject(object);
		}
	}

	/**
	 * Spawns an object for every player on the server within 25 distance to set position
	 * @param object - The object to spawn
	 */
	public static void spawnGlobalObjectWithinDistance(GameObject object) {
		for(Player player : World.getPlayers()) {
			if(player == null)
				continue;
			if(Locations.goodDistance(object.getPosition().getX(), object.getPosition().getY(), player.getPosition().getX(), player.getPosition().getY(), player.getLocation() == Location.SOULWARS || Dungeoneering.doingDungeoneering(player) ? 400 : 60)) {
				spawnObject(player, object);
			}
		}
	}

	/**
	 * Deletes a global Object
	 * @param object
	 */

	public static void deleteGlobalObject(GameObject object) {
		World.deregister(object);
		handleList(object, "delete");
	}

	/**
	 * Spawns a Global object that can be seen by everyone.
	 * @param Object The object that will be spawned globally.
	 */

	public static void spawnGlobalObject(GameObject object) {
		handleList(object, "add");
		if(object.getId() == -1) //Remove the object 
			World.deregister(object);
		else
			World.register(object);
	}

	/**
	 * Spawns a global object which will turn into another object after specified time.
	 * @param i 
	 */
	public static void globalObjectRespawnTask(final GameObject tempObject, final GameObject mainObject, final int cycles) {
		spawnGlobalObject(tempObject);
		TaskManager.submit(new Task(cycles) {
			@Override
			public void execute() {
				deleteGlobalObject(tempObject);
				spawnGlobalObjectWithinDistance(mainObject);
				this.stop();
			}
		});
	}

	/**
	 * Spawns a object which will turn into another object after specified time.
	 * @param i 
	 */
	public static void objectRespawnTask(final Player player, final GameObject tempObject, final GameObject mainObject, final int cycles) {
		spawnObject(player, tempObject);
		TaskManager.submit(new Task(cycles) {
			@Override
			public void execute() {
				if(!tempObject.getPosition().copy().equals(mainObject.getPosition().copy()))
					deleteObject(player, tempObject);
				spawnObject(player, mainObject);
				this.stop();
			}
		});
	}

	/**
	 * Spawns a global object which will be deleted after some time
	 * @param i 
	 */
	public static void globalObjectRemovalTask(final GameObject object, final int cycles) {
		spawnGlobalObject(object);
		TaskManager.submit(new Task(cycles) {
			@Override
			public void execute() {
				deleteGlobalObject(object);
				this.stop();
			}
		});
	}

	/**
	 * Spawns a global object which will turn into another global object after specified time.
	 * @param i 
	 */
	public static void globalFiremakingTask(final GameObject fire, final Player player, final int cycles) {
		spawnGlobalObject(fire);
		TaskManager.submit(new Task(cycles) {
			@Override
			public void execute() {
				//World.createGroundItem(player, new Item(592), fire.getPosition().copy(), GroundItem.HIDE_TICKS);
				deleteGlobalObject(fire);
				if(player.getAttributes().getCurrentInteractingObject() != null && player.getAttributes().getCurrentInteractingObject().getId() == 2732)
					player.getAttributes().setCurrentInteractingObject(null);
				this.stop();
			}
		});
	}

	public static void handleList(GameObject object, String handleType) {
		switch(handleType.toUpperCase()) {
		case "DELETE":
			for(GameObject objects : customObjects) {
				if(objects.getId() == object.getId() && objects.getPosition().getX() == object.getPosition().getX() && object.getPosition().getY() == objects.getPosition().getY()) {
					customObjects.remove(objects);
				}
			}
			break;
		case "ADD":
			if(!customObjects.contains(object))
				customObjects.add(object);
			break;
		case "EMPTY":
			customObjects.clear();
			break;
		}
	}

	public static CopyOnWriteArrayList<GameObject> customObjects = new CopyOnWriteArrayList<GameObject>();

	/**
	 * Flowers
	 */
	public static final int FLOWER_IDS[] = {2980, 2981, 2982, 2983, 2984, 2985, 2986, 2987, 2988};
	public static boolean isFlower(GameObject object) {
		for(int i = 0; i < FLOWER_IDS.length; i++) {
			if(FLOWER_IDS[i] == object.getId())
				return true;
		}
		return false;
	}
}
