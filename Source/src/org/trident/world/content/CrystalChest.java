package org.trident.world.content;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.GameObject;
import org.trident.model.Item;
import org.trident.util.Logger;
import org.trident.util.Misc;
import org.trident.world.entity.player.Player;

public class CrystalChest {

	public static void handleChest(final Player p, final GameObject chest) {
		if(System.currentTimeMillis() - p.getAttributes().getClickDelay() <= 3000) 
			return;
		if(!p.getInventory().contains(989)) {
			p.getPacketSender().sendMessage("This chest can only be opened with a Crystal key.");
			return;
		}
		p.performAnimation(new Animation(827));
		p.getInventory().delete(989, 1);
		p.getPacketSender().sendMessage("You open the chest..");
		TaskManager.submit(new Task(1, p, false) {
			int tick = 0;
			@Override
			public void execute() {
				switch(tick) {
				case 2:
					Item[] loot = itemRewards[Misc.getRandom(itemRewards.length - 1)];
					for(Item item : loot) {
						p.getInventory().add(item);
						Logger.log(p.getUsername(), "Player looted crystal chest and received "+item.getDefinition().getName()+" x"+item.getAmount()+", noted: "+item.getDefinition().isNoted());
					}
					p.getPacketSender().sendMessage("and find some items!");
					CustomObjects.spawnObject(p, new GameObject(173, chest.getPosition().copy(), 10, 2));
					break;
				case 14:
					CustomObjects.spawnObject(p, new GameObject(chest.getId(), chest.getPosition().copy(), 10, 2));
					stop();
					break;
				}
				tick++;
			}
		});
		p.getAttributes().setClickDelay(System.currentTimeMillis());
	}

	private static final Item[][] itemRewards =  {
		{new Item(1631, 1), new Item(1969, 1), new Item(995, 20000)}, //set 1
		{new Item(1631, 1)}, //set 2
		{new Item(1631, 1), new Item(995, 10000), new Item(373, 1)}, //set 3
		{new Item(1631, 1), new Item(554, 50), new Item(555, 50), new Item(556, 50), new Item(557, 50), new Item(558, 50), new Item(559, 50), new Item(560, 10), new Item(561, 10), new Item(562, 10), new Item(563, 10), new Item(564, 10)}, //set 4
		{new Item(1631, 1), new Item(454, 100)}, //set 5
		{new Item(1631, 1), new Item(1601, 1), new Item(1603, 1)}, //set 6
		{new Item(1631, 1), new Item(985, 1), new Item(995, 7500)}, //set 7
		{new Item(1631, 1), new Item(2363, 1)}, //set 8
		{new Item(1631, 1), new Item(987, 1), new Item(995, 7500)}, //set 9
		{new Item(1631, 1), new Item(441, 150)}, //set 10
		{new Item(1631, 1), new Item(1185, 1)}, //set 11
		{new Item(1631, 1), new Item(1079, 1)}, //set 12
		{new Item(1631, 1), new Item(1093, 1)}, //set 13
	};
}
