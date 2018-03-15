package org.trident.world.content;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.definitions.ItemDefinition;
import org.trident.util.Misc;
import org.trident.world.entity.player.Player;

/**
 * 
 * @author Jason MacKeigan (http://www.rune-server.org/members/jason)
 * @since August 16th, 2013
 *
 */
public class MysteryBox {
    
    /**
     * Index 0 = Item Id
     * Index 1 = Amount
     * Index 2 = Rarity
     */
    public static final int MAXIMUM_RARITY = 10;
    public static final int MINIMUM_RARITY = 1;
    public static final int MAXIMUM_ITEMS = 100;
    public static final int CYCLE_TIME = 4;
    public static final int EVENT_ID = 245193413;
    public static final int[][] ITEM_DATA = {
        {4151, 1, 5},
        {11694, 1, 10},
        {4153, 1, 3},
        {11696, 1, 9},
        {11698, 1, 9},
        {11700, 1, 9},
        {5699, 10, 3},
        {3440, 1, 1}
    };
    
    public static void addItem(Player player) {
        int rarity = getRarityFormula();
        int[][] items = new int[MAXIMUM_ITEMS][2];
        int itemsIndex = 0;
        for(int i = 0; i < ITEM_DATA.length; i++) {
            if(ITEM_DATA[i][2] <= rarity) {
                items[itemsIndex][0] = ITEM_DATA[i][0];
                items[itemsIndex][1] = ITEM_DATA[i][1];
                itemsIndex++;
            }
        }
        int index = Misc.getRandom(itemsIndex - 1);
        int itemId = items[index][0];
        int amount = items[index][1];
        player.getInventory().delete(6199, 1);
        player.getAttributes().setClickDelay(System.currentTimeMillis());
        execute(player, itemId, amount);
    }
    
    public static void execute(final Player player, final int itemId, final int amount) {
		TaskManager.submit(new Task(3, player, false) {
			@Override
			public void execute() {
				player.getInventory().add(itemId, amount);
                player.getPacketSender().sendMessage("You have received "+ItemDefinition.forId(itemId).getName().toLowerCase()+"x"+amount+" from the mystery box.");
                stop();
            }
            
            @Override
            public void stop() {
                setEventRunning(false);
            }
            
        });
    }
    
    public static int getRarityFormula() {
        int rarity = MINIMUM_RARITY + Misc.getRandom(MysteryBox.MAXIMUM_RARITY - 1);
        for(int i = 0; i < 3; i++) {
            if(rarity >= MAXIMUM_RARITY - 1)
                rarity = MINIMUM_RARITY + Misc.getRandom(MysteryBox.MAXIMUM_RARITY - 1);
        }
        return rarity;
    }


}