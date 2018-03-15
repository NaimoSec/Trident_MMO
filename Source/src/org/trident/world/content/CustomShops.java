package org.trident.world.content;

import org.trident.model.Item;
import org.trident.model.container.impl.Shop;
import org.trident.model.definitions.ItemDefinition;
import org.trident.world.content.minigames.impl.ArcheryCompetition;
import org.trident.world.content.quests.RecipeForDisaster;
import org.trident.world.content.skills.impl.agility.Agility;
import org.trident.world.entity.player.Player;

/**
 * Handles custom shops
 * @author Gabbe
 *
 */
public class CustomShops {

	public static boolean isCustomShop(int shopId) { 
		return shopId == 10 || shopId == 12 || shopId == 21 || shopId == 29 || shopId == 36 || shopId == 49 || shopId == 55 || shopId == 53 || shopId == 33 || shopId == 57 || shopId == 60;
	}

	public static void checkValue(Player player, int shopId, Item itemToValue) {
		switch(shopId) {
		case 10:
			player.getPacketSender().sendMessage(""+itemToValue.getDefinition().getName()+": currently costs "+ArcheryCompetition.getItemPrice(itemToValue.getId()) +" Archery tickets.");
			break;
		case 12:
			player.getPacketSender().sendMessage(""+itemToValue.getDefinition().getName()+": currently costs "+Agility.getPriceForItem(itemToValue.getId()) +" Agility tickets.");
			break;
		case 21:
			player.getPacketSender().sendMessage(""+itemToValue.getDefinition().getName()+": currently costs "+itemToValue.getDefinition().getValue()+" coins.");
			break;
		case 29:
			player.getPacketSender().sendMessage(""+itemToValue.getDefinition().getName()+": currently costs "+getPkShopValue(itemToValue.getId())+" Pk points.");
			break;
		case 36:
			player.getPacketSender().sendMessage(""+itemToValue.getDefinition().getName()+": currently costs "+getTokkulValue(itemToValue.getId())+" Tokkul.");
			break;
		case 49:
		case 55:
			player.getPacketSender().sendMessage(""+itemToValue.getDefinition().getName()+": currently costs "+getDonatorPointsValue(itemToValue.getId())+" Donator points.");
			break;
		case 53:
			player.getPacketSender().sendMessage(""+itemToValue.getDefinition().getName()+": currently costs "+getConquestPointsValue(itemToValue.getId())+" Conquest points.");
			break;
		case 33:
			player.getPacketSender().sendMessage(""+itemToValue.getDefinition().getName()+": currently costs "+getDungeoneeringItemValue(itemToValue.getId())+" coins.");
			break;
		case 57:
			if(itemToValue.getId() == 15707) {
				player.getPacketSender().sendMessage("@red@Ring of Kinship is currently for free.");
				return;
			}
			player.getPacketSender().sendMessage(""+itemToValue.getDefinition().getName()+": currently costs "+getDungeoneeringTokensCost(itemToValue.getId())+" Dungeoneering tokens.");
			break;
		case 60:
			player.getPacketSender().sendMessage(""+itemToValue.getDefinition().getName()+": currently costs "+getEnergyFragmentCost(itemToValue.getId())+" Energy fragments.");
			break;
		}
	}

	public static void buyItem(Player player, int shopId, Item itemToBuy, int amountToBuy) {
		if(player.getAttributes().getShop() == null)
			return;
		if(!player.getAttributes().getShop().contains(itemToBuy.getId()))
			return;
		if(amountToBuy <= 0)
			return;
		if(shopId == 10) { //Archery tickets
			int price = ArcheryCompetition.getItemPrice(itemToBuy.getId());
			int playerCurrencyAmount = player.getInventory().getAmount(1464);
			if(playerCurrencyAmount < price) {
				player.getPacketSender().sendMessage("You do not have enough Archery tickets to purchase this item.");
				return;
			}
			if(player.getInventory().getFreeSlots() < 1) {
				player.getPacketSender().sendMessage("Please get some more inventory space.");
				return;
			}
			for (int i = amountToBuy; i > 0; i--) {
				playerCurrencyAmount = player.getInventory().getAmount(1464);
				if(!itemToBuy.getDefinition().isStackable()) {
					if(playerCurrencyAmount >= price && player.getInventory().getFreeSlots() >= 1) {
						player.getInventory().delete(1464, price, true);
						player.getInventory().add(itemToBuy.getId(), 1);
						deleteFromShop(player.getAttributes().getShop(), itemToBuy, 1);
						player.getInventory().refreshItems();
						playerCurrencyAmount -= price;
					} else {
						break;
					}
				} else {
					if(player.getInventory().getFreeSlots() >= 1 || player.getInventory().contains(itemToBuy.getId())) {
						int moneyAmount = player.getInventory().getAmount(1464);
						if(moneyAmount == 0)
							break; //lol gj here gabbe ty
						int canBeBought = moneyAmount / (price); //How many noted items can be bought for the player's money
						if(canBeBought == 0)
							break;
						if(canBeBought >= amountToBuy)
							canBeBought = amountToBuy;
						player.getInventory().delete(1464, price * canBeBought, true);
						player.getInventory().add(itemToBuy.getId(), canBeBought);
						deleteFromShop(player.getAttributes().getShop(), itemToBuy, canBeBought);
						player.getInventory().refreshItems();
						playerCurrencyAmount -= price;
						break;
					} else {
						break;
					}
				}
				amountToBuy--;
			}
		} else if(shopId == 12) { //Agility shop
			int price = Agility.getPriceForItem(itemToBuy.getId());
			int playerCurrencyAmount = player.getInventory().getAmount(2996);
			if(playerCurrencyAmount < price) {
				player.getPacketSender().sendMessage("You do not have enough Agility tickets to purchase this item.");
				return;
			}
			if(player.getInventory().getFreeSlots() < 1) {
				player.getPacketSender().sendMessage("Please get some more inventory space.");
				return;
			}
			for (int i = amountToBuy; i > 0; i--) {
				if(playerCurrencyAmount >= price && player.getInventory().getFreeSlots() >= 1) {
					player.getInventory().delete(2996, price, true);
					player.getInventory().add(itemToBuy.getId(), 1);
					deleteFromShop(player.getAttributes().getShop(), itemToBuy, 1);
					player.getInventory().refreshItems();
					playerCurrencyAmount -= price;
				} else {
					break;
				}
				amountToBuy--;
			}
		} else if(shopId == 21) { //RFD shop
			int price = ItemDefinition.forId(itemToBuy.getId()).getValue();
			int playerCurrencyAmount = player.getInventory().getAmount(995);
			if(playerCurrencyAmount < price) {
				player.getPacketSender().sendMessage("You do not have enough coins to purchase this item.");
				return;
			}
			if(player.getInventory().getFreeSlots() < 1) {
				player.getPacketSender().sendMessage("Please get some more inventory space.");
				return;
			}
			for (int i = amountToBuy; i > 0; i--) {
				if(playerCurrencyAmount >= price && player.getInventory().getFreeSlots() >= 1) {
					player.getInventory().delete(995, price, true);
					player.getInventory().add(itemToBuy.getId(), 1);
					playerCurrencyAmount -= price;
					player.getInventory().refreshItems();
					RecipeForDisaster.openRFDShop(player);
				} else {
					break;
				}
				amountToBuy--;
			}
		} else if(shopId == 29) { //Pk shop
			int price = getPkShopValue(itemToBuy.getId());
			int playerCurrencyAmount = player.getPointsHandler().getPkPoints();
			if(playerCurrencyAmount < price) {
				player.getPacketSender().sendMessage("You do not have enough Pk points to purchase this item.");
				return;
			}
			if(player.getInventory().getFreeSlots() < 1) {
				player.getPacketSender().sendMessage("Please get some more inventory space.");
				return;
			}
			amountToBuy = 1;
			if(playerCurrencyAmount >= price && player.getInventory().getFreeSlots() >= 1) {
				player.getPointsHandler().setPkPoints(-price, true);
				player.getInventory().add(itemToBuy.getId(), 1);
				player.getInventory().refreshItems();
				deleteFromShop(player.getAttributes().getShop(), itemToBuy, 1);
			}
		} else if(shopId == 36) { //Tokkul shop
			int price = getTokkulValue(itemToBuy.getId());
			int playerCurrencyAmount = player.getInventory().getAmount(6529);
			if(playerCurrencyAmount < price) {
				player.getPacketSender().sendMessage("You do not have enough Tokkul points to purchase this item.");
				return;
			}
			if(player.getInventory().getFreeSlots() < 1) {
				player.getPacketSender().sendMessage("Please get some more inventory space.");
				return;
			}
			for (int i = amountToBuy; i > 0; i--) {
				if(playerCurrencyAmount >= price && player.getInventory().getFreeSlots() >= 1) {
					player.getInventory().delete(6529, price);
					player.getInventory().add(itemToBuy.getId(), 1);
					player.getInventory().refreshItems();
					deleteFromShop(player.getAttributes().getShop(), itemToBuy, 1);
					playerCurrencyAmount -= price;
				} else {
					break;
				}
				amountToBuy--;
			}
		} else if(shopId == 49 || shopId == 55) {
			amountToBuy = 1;
			int price = getDonatorPointsValue(itemToBuy.getId());
			int playerCurrencyAmount = player.getPointsHandler().getDonatorPoints();
			if(playerCurrencyAmount < price) {
				player.getPacketSender().sendMessage("You do not have enough Donator points to purchase this item.");
				return;
			}
			if(player.getInventory().getFreeSlots() < 1) {
				player.getPacketSender().sendMessage("Please get some more inventory space.");
				return;
			}
			player.getPointsHandler().setDonatorPoints(playerCurrencyAmount - price, false);
			player.getPointsHandler().refreshPanel();
			player.getInventory().add(itemToBuy.getId(), 1).refreshItems();
			deleteFromShop(player.getAttributes().getShop(), itemToBuy, 1);
		} else if(shopId == 53) {
			amountToBuy = 1;
			int price = getConquestPointsValue(itemToBuy.getId());
			int playerCurrencyAmount = player.getPointsHandler().getConquestPoints();
			if(playerCurrencyAmount < price) {
				player.getPacketSender().sendMessage("You do not have enough Conquest points to purchase this item.");
				return;
			}
			if(player.getInventory().getFreeSlots() < 1) {
				player.getPacketSender().sendMessage("Please get some more inventory space.");
				return;
			}
			player.getPointsHandler().setConquestPoints(playerCurrencyAmount - price, false);
			player.getPointsHandler().refreshPanel();
			player.getInventory().add(itemToBuy.getId(), 1).refreshItems();
			deleteFromShop(player.getAttributes().getShop(), itemToBuy, 1);
		} else if(shopId == 33) {
			int price = getDungeoneeringItemValue(itemToBuy.getId());
			int playerCurrencyAmount = player.getInventory().getAmount(18201);
			if(playerCurrencyAmount < price) {
				player.getPacketSender().sendMessage("You do not have enough coins to purchase this item.");
				return;
			}
			if(player.getInventory().getFreeSlots() == 0) {
				player.getPacketSender().sendMessage("Please get some more inventory space.");
				return;
			}
			for (int i = amountToBuy; i > 0; i--) {
				if(playerCurrencyAmount >= price && player.getInventory().getFreeSlots() >= 1) {
					player.getInventory().delete(18201, price);
					player.getInventory().add(itemToBuy.getId(), 1);
					player.getInventory().refreshItems();
					deleteFromShop(player.getAttributes().getShop(), itemToBuy, 1);
					playerCurrencyAmount -= price;
				} else {
					break;
				}
				amountToBuy--;
			}
		} else if(shopId == 57) {
			amountToBuy = 1;
			int price = getDungeoneeringTokensCost(itemToBuy.getId());
			int playerCurrencyAmount = player.getPointsHandler().getDungeoneeringTokens();
			if(playerCurrencyAmount < price) {
				player.getPacketSender().sendMessage("You do not have enough Dungeoneering tokens to purchase this item.");
				return;
			}
			if(player.getInventory().getFreeSlots() == 0) {
				player.getPacketSender().sendMessage("Please get some more inventory space before buying this.");
				return;
			}
			player.getPointsHandler().setDungeoneeringTokens(false, playerCurrencyAmount - price);
			player.getPointsHandler().refreshPanel();
			player.getInventory().add(itemToBuy.getId(), 1).refreshItems();
			deleteFromShop(player.getAttributes().getShop(), itemToBuy, 1);
		} else if(shopId == 60) {
			amountToBuy = 1;
			int price = getEnergyFragmentCost(itemToBuy.getId());
			int playerCurrencyAmount = player.getInventory().getAmount(13653);
			if(playerCurrencyAmount < price) {
				player.getPacketSender().sendMessage("You do not have enough Energy fragments to purchase this item.");
				return;
			}
			if(player.getInventory().getFreeSlots() == 0) {
				player.getPacketSender().sendMessage("Please get some more inventory space before buying this.");
				return;
			}
			player.getInventory().delete(13653, price).add(itemToBuy.getId(), 1);
			deleteFromShop(player.getAttributes().getShop(), itemToBuy, 1);
		}
	}

	public static void deleteFromShop(Shop shop, Item itemToDelete, int amount) {
		if(shop == null)
			return;
		if(shop.getAmount(itemToDelete.getId()) - amount < 1) {
			shop.delete(itemToDelete.getId(), amount);
			shop.add(itemToDelete.getId(), 1);
		} else {
			shop.delete(itemToDelete.getId(), amount);
		}
		shop.refreshItems();
	}

	public static int getPkShopValue(int item) {
		switch(item) {
		case 6585:
			return 18;
		case 15486:
			return 60;
		case 11716:
			return 40;
		case 13262:
			return 25;
		case 13864:
			return 90;
		case 13861:
			return 80;
		case 13858:
			return 100;
		case 13867:
			return 95;
		case 13887:
			return 100;
		case 13893:
			return 80;
		case 13899:
			return 105;
		case 13905:
			return 90;
		case 13884:
			return 100;
		case 13890:
			return 80;
		case 13896:
			return 90;
		case 13902:
			return 100;
		case 13870:
			return 100;
		case 13873:
			return 80;
		case 13876:
			return 90;
		case 13883:
			return 1;
		case 19780:
			return 120;
		}
		return Integer.MAX_VALUE;
	}

	public static int getTokkulValue(int item) {
		switch(item) {
		case 436:
		case 438:
			return 5;
		case 440:
			return 12;
		case 453:
			return 25;
		case 442:
			return 30;
		case 444:
			return 50;
		case 447:
			return 110;
		case 449:
			return 280;
		case 451:
			return 800;
		case 9194:
			return 2400;
		case 1623:
			return 100;
		case 1621:
			return 190;
		case 1619:
			return 340;
		case 1617:
			return 1400;
		case 1631:
			return 9600;
		case 6571:
			return 80000;
		}
		return Integer.MAX_VALUE;
	}

	public static int getDonatorPointsValue(int item) {
		switch(item) {
		case 962:
			return 40;
		case 1038:
		case 1040:
		case 1042:
		case 1044:
		case 1046:
		case 1048:
			return 50;
		case 1053:
		case 1055:
		case 1057:
			return 25;
		case 14484:
			return 20;
		case 13744:
			return 20;
		case 13738:
			return 22;
		case 13742:
			return 25;
		case 13740:
			return 35;
		case 19780:
			return 25;
		case 11694:
			return 30;
		case 20671:
			return 5;
		case 11696:
		case 11700:
		case 11698:
			return 25;
		case 20135:
			return 25;
		case 20147:
		case 20159:
			return 20;
		case 20143:
			return 30;
		case 20674:
			return 30;
		case 20155:
		case 20167:
			return 25;
		case 20151:
		case 20163:
			return 30;
		case 20139:
			return 35;
		case 15486:
			return 15;
		case 21005:
			return 10;
		case 21010:
			return 25;
		case 19143:
		case 19149:
		case 19146:
			return 10;
		case 11730:
			return 15;
		case 11718:
		case 11728:
			return 12;
		case 11720:
		case 11724:
			return 18;
		case 11722:
		case 11726:
			return 15;
		case 6570:
			return 10;
		case 21787:
		case 21790:
		case 21793:
			return 20;
		case 6731:
		case 6733:
		case 6735:
			return 15;
		}
		if(ItemDefinition.forId(item).getName().toLowerCase().contains("chaotic"))
			return 25;
		return Integer.MAX_VALUE;
	}

	public static int getConquestPointsValue(int item) {
		switch(item) {
		case 10548:
		case 10549:
		case 10550:
			return 200;
		case 10551:
			return 400;
		case 10555:
			return 210;
		case 10552:
		case 10553:
			return 150;
		case 2412:
		case 2413:
			return 200;
		}
		return Integer.MAX_VALUE;
	}

	public static int getDungeoneeringItemValue(int item) {
		switch(item) {
		case 18159:
			return 113;
		case 18161:
			return 540;
		case 18169:
			return 3112;
		case 18173:
			return 5125;
		case 157:
		case 145:
		case 163:
		case 169:
		case 3042:
			return 6123;
		case 139:
			return 12300;
		case 1331:
		case 857:
			return 9500;
		case 1383: 
			return 5135;
		case 890:
		case 558:
		case 556:
			return 367;
		}
		return 0;
	}

	public static int getDungeoneeringTokensCost(int item) {
		switch(item) {
		case 11137:
			return 4387;
		case 18782:
			return 5523;
		case 18357:
		case 18359:
		case 18355:
		case 18353:
		case 18351:
		case 18349:
			return 39121;
		case 18335:
			return 14233;
		case 18363:
			return 12721;
		}
		return 0;
	}

	public static int getEnergyFragmentCost(int item) {
		switch(item) {
		case 5509:
			return 3500;
		}
		return 0;
	}
}
