package org.trident.world.content.quests;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Item;
import org.trident.model.Position;
import org.trident.model.RegionInstance;
import org.trident.model.RegionInstance.RegionInstanceType;
import org.trident.model.container.impl.Shop;
import org.trident.model.definitions.NPCSpawns;
import org.trident.model.inputhandling.impl.EnterAmountToBuyFromShop;
import org.trident.model.inputhandling.impl.EnterAmountToSellToShop;
import org.trident.model.movement.MovementStatus;
import org.trident.world.World;
import org.trident.world.content.Locations.Location;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCData.CustomNPCData;
import org.trident.world.entity.player.Player;

/**
 * @author Gabbe
 * Wrote this quickly!!
 * Handles the RFD quest
 */
public class RecipeForDisaster {

	public static void enterRFD(Player p) {
		if(p.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() == 6)
			return;
		p.moveTo(new Position(1900, 5346, p.getIndex() * 4 + 2));
		p.getAttributes().setRegionInstance(new RegionInstance(p, RegionInstanceType.RECIPE_FOR_DISASTER));
		spawnWave(p, p.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted());
		CurseHandler.deactivateAll(p); PrayerHandler.deactivateAll(p);
	}

	public static void spawnWave(final Player p, final int wave) {
		if(wave > 5 || p.getAttributes().getRegionInstance() == null)
			return;
		TaskManager.submit(new Task(1, p, false) {
			@Override
			public void execute() {
				NPC n = NPCSpawns.createCustomNPC(CustomNPCData.forId(wave), new Position(spawnPos.getX(), spawnPos.getY(), p.getPosition().getZ()));
				n.getCombatAttributes().setSpawnedFor(p);
				CombatHandler.setAttack(n, p);
				World.register(n);
				p.getAttributes().getRegionInstance().getNpcsList().add(n);	
				stop();
			}
		});
	}

	public static void handleNPCDeath(final Player player, NPC n) {
		if(player.getAttributes().getRegionInstance() == null)
			return;
		player.getAttributes().getRegionInstance().getNpcsList().remove(n);
		player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().setWavesCompleted(player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() + 1);
		switch(n.getId()) {
		case 3493:
		case 3494:
		case 3495:
		case 3496:
		case 3497:
			int index = n.getId() - 3490;
			player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().setPartFinished(index, true);
			break;
		case 3491:
			player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().setPartFinished(8, true);
			player.moveTo(new Position(3207, 3215, 0));
			player.getMovementQueue().setMovementStatus(MovementStatus.NONE);
			player.restart();
			DialogueManager.start(player, 273);
			break;
		}
		if(player.getLocation() != Location.RECIPE_FOR_DISASTER || player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() == 6)
			return;
		TaskManager.submit(new Task(3, player, false) {
			@Override
			public void execute() {
				stop();
				if(player.getLocation() != Location.RECIPE_FOR_DISASTER || player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() == 6)
					return;
				spawnWave(player, player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted());
			}
		});
	}

	public static void openQuestLog(Player p) {
		for(int i = 8145; i < 8196; i++)
			p.getPacketSender().sendString(i, "");
		p.getPacketSender().sendInterface(8134);
		p.getPacketSender().sendString(8136, "Close window");
		p.getPacketSender().sendString(8144, ""+questTitle);
		p.getPacketSender().sendString(8145, "");
		int questIntroIndex = 0;
		for(int i = 8147; i < 8147+questIntro.length; i++) {
			p.getPacketSender().sendString(i, "@dre@"+questIntro[questIntroIndex]);
			questIntroIndex++;
		}
		int questGuideIndex = 0;
		for(int i = 8147+questIntro.length; i < 8147+questIntro.length+questGuide.length; i++) {
			if(!p.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(questGuideIndex))
				p.getPacketSender().sendString(i, ""+questGuide[questGuideIndex]);
			else
				p.getPacketSender().sendString(i, "@str@"+questGuide[questGuideIndex]+"");
			if(questGuideIndex == 2) {
				if(p.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() > 0 && !p.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().hasFinishedPart(questGuideIndex))
					p.getPacketSender().sendString(i, "@yel@"+questGuide[questGuideIndex]);
				if(p.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() == 6)
					p.getPacketSender().sendString(i, "@str@"+questGuide[questGuideIndex]+"");
			}
			questGuideIndex++;
		}
		if(p.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted() == 6)
			p.getPacketSender().sendString(8147+questIntro.length+questGuide.length, "@dre@Quest complete!");
	}

	public static void openRFDShop(final Player player) {
		int[] stock = new int[10];
		int[] stockAmount = new int[10];
		for(int i = 0; i < stock.length; i++) {
			stock[i] = -1;
			stockAmount[i] = 1;
		}
		for(int i = 0; i <= player.getAttributes().getMinigameAttributes().getRecipeForDisasterAttributes().getWavesCompleted(); i++) {
			switch(i) {
			case 0:
				stock[0] = 7453;
				break;
			case 1:
				stock[1] = 7454;
				stock[2] = 7455;
				break;
			case 2:
				stock[3] = 7456;
				stock[4] = 7457;
				break;
			case 3:
				stock[5] = 7458;
				break;
			case 4:
				stock[6] = 7459;
				stock[7] = 7460;
				break;
			case 5:
				stock[8] = 7461;
				stock[9] = 7462;
				break;
			}
		}
		Item[] stockItems = new Item[stock.length];
		for(int i = 0; i < stock.length; i++)
			stockItems[i] = new Item(stock[i], stockAmount[i]);
		Shop shop = new Shop(player, 21, "Culinaromancer's chest", new Item(995), stockItems);
		stock = stockAmount = null;
		stockItems = null;
		shop.setPlayer(player);
		player.getPacketSender().sendItemContainer(player.getInventory(), Shop.INVENTORY_INTERFACE_ID);
		player.getPacketSender().sendItemContainer(shop, Shop.ITEM_CHILD_ID);
		player.getPacketSender().sendString(Shop.NAME_INTERFACE_CHILD_ID, "Culinaromancer's chest");
		if(player.getAttributes().getInputHandling() == null || !(player.getAttributes().getInputHandling() instanceof EnterAmountToSellToShop || player.getAttributes().getInputHandling() instanceof EnterAmountToBuyFromShop))
			player.getPacketSender().sendInterfaceSet(Shop.INTERFACE_ID, Shop.INVENTORY_INTERFACE_ID - 1);
		player.getAttributes().setShop(shop).setInterfaceId(Shop.INTERFACE_ID).setShopping(true);
	}


	private static final Position spawnPos = new Position(1900, 5354);
	private static final String questTitle = "Recipe for Disaster";
	private static final String[] questIntro ={
		"The Culinaromancer has returned. He froze time", 
		"in the Lumbridge dining room. Only you can stop him!",
		"",
	};
	private static final String[] questGuide ={
		"Talk to Lumbridge's Cook and agree to help him.", 
		"Enter the room north of the kitchen and investigate.",
		"Enter the portal and defeat the following defenders:",
		"* Agrith-Na-Na",
		"* Flambeed",
		"* Karamel",
		"* Dessourt",
		"* Gelatinnoth mother",
		"And finally.. Defeat the Culinaromancer!"
	};
}
