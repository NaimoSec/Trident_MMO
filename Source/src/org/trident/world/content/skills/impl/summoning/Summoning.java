package org.trident.world.content.skills.impl.summoning;

import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.FamiliarDeathTimerTask;
import org.trident.engine.task.impl.FamiliarSpawnTask;
import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.GroundItem;
import org.trident.model.Item;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.container.impl.Bank;
import org.trident.model.definitions.NPCSpawns;
import org.trident.util.Logger;
import org.trident.world.World;
import org.trident.world.content.Achievements;
import org.trident.world.content.ItemLending;
import org.trident.world.entity.grounditem.GroundItemManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCAttributes;
import org.trident.world.entity.player.Player;

/**
 * The summoning skill is based upon creating pouches that contain
 * certain 'familiars', which you can then summon and use their abilities
 * as a form of 'assistance'.
 * 
 * @author Gabbe
 */

public class Summoning {

	Player player;

	public Summoning(Player p) {
		this.player = p;
	}

	public void summon(final FamiliarData familiar, boolean renew, boolean login) {
		if(familiar == null)
			return;
		if(!player.getLocation().isSummoningAllowed()) {
			player.getPacketSender().sendMessage("You cannot summon familiars here.");
			return;
		}
		if(!login && System.currentTimeMillis() - player.getAttributes().getSummoningSpawnDelay() < 1000)
			return;
		if(getFamiliar() != null && !renew && !login) {
			player.getPacketSender().sendMessage("You already have a familiar.");
			return;
		}
		if (login || player.getSkillManager().getMaxLevel(Skill.SUMMONING) >= familiar.levelRequired) {
			if(!login) {
				if (player.getSkillManager().getCurrentLevel(Skill.SUMMONING) >= familiar.summoningPointsRequired) {
					player.getSkillManager().setCurrentLevel(Skill.SUMMONING, player.getSkillManager().getCurrentLevel(Skill.SUMMONING) - familiar.summoningPointsRequired);
					player.getInventory().delete(familiar.getPouchId(), 1);
					if(renew && getFamiliar() != null) {
						player.getPacketSender().sendMessage("You have renewed your familiar.");
					}
				} else {
					player.getPacketSender().sendMessage("You do not have enough Summoning points to summon this familiar.");
					player.getPacketSender().sendMessage("You can recharge your Summoning points at an obelisk.");
					return;
				}
			}
			if(getFamiliar() != null && getFamiliar().getSummonNpc() != null)
				World.deregister(getFamiliar().getSummonNpc());


			NPCAttributes attributes = new NPCAttributes().setAttackable(false).setAggressive(false).setRespawn(true).setWalkingDistance(0);
			NPC foll = NPCSpawns.createNPC(familiar.npcId, new Position(player.getPosition().getX(), player.getPosition().getY() + 1, player.getPosition().getZ()), attributes, attributes.copy());
			foll.performGraphic(new Graphic(1315));
			foll.setPositionToFace(player.getPosition());
			foll.getAttributes().setSummoningNpc(true);
			foll.setEntityInteraction(player);
			foll.followCharacter(player);
			World.register(foll);
			setFamiliar(new Familiar(player, foll, login && getFamiliar() != null && getFamiliar().getDeathTimer() > 0 ? getFamiliar().getDeathTimer() : SummoningData.getFollowerTimer(familiar.npcId)));
			player.getPacketSender().sendString(54028, ""+familiar.name().replaceAll("_", " "));
			player.getPacketSender().sendString(54045, " "+player.getSkillManager().getCurrentLevel(Skill.SUMMONING)+"/"+player.getSkillManager().getMaxLevel(Skill.SUMMONING));
			if(!summoningEvent) {
				FamiliarDeathTimerTask.start(player);
				player.getPacketSender().sendNpcHeadOnInterface(player.getAdvancedSkills().getSummoning().getFamiliar().getSummonNpc().getId(), 54021); // 60 = invisable head to remove it
			}
			if(!login && !renew)
				player.getAdvancedSkills().getSummoning().storedItems.clear();
			for(int i = 0; i < SummoningData.frames.length; i++)
				player.getPacketSender().sendItemOnInterface(SummoningData.getFrameID(i), -1, 1);
			if(familiar.getPouchId() == 12043) //Dreadfowl
				Achievements.handleAchievement(player, Achievements.Tasks.TASK29);
			player.getPacketSender().sendString(1, "[SUMMOtrue");
			player.getAttributes().setSummoningSpawnDelay(System.currentTimeMillis());
		} else {
			player.getPacketSender().sendMessage("You need a Summoning level of at least " + familiar.levelRequired + " to summon this familiar.");
		}
	}

	public boolean summoningEvent = false;

	public void toBank() {
		if(player.getAttributes().isBanking()) {
			if(getFamiliar() == null || !SummoningData.beastOfBurden(getFamiliar().getSummonNpc().getId())) {
				player.getPacketSender().sendMessage("You do not have a familiar which can hold items.");
				return;
			}
			if(storedItems.size() == 0)
				return;
			for(Item storedItem: storedItems) {
				if (storedItem == null || storedItem.getId() < 0 || storedItem.getAmount() <= 0)
					continue;
				int tab = Bank.getTabForItem(player, storedItem.getId());
				if(player.getBank(tab).getFreeSlots() == 0 && !player.getBank(tab).contains(storedItem.getId()))
					break;
				player.getAttributes().setCurrentBankTab(tab);
				player.getBank(tab).add(storedItem);
			}
			storedItems.clear();
			for(int i = 0; i < SummoningData.frames.length; i++)
				player.getPacketSender().sendItemOnInterface(SummoningData.getFrameID(i), -1, 1);
			player.performAnimation(new Animation(827));
		}
	}

	public void toInventory() {
		if(player == null)
			return;
		if(player.getInventory().getFreeSlots() <= 0) {
			player.getPacketSender().sendMessage("You do not have any free space in your inventory.");
			return;
		}
		if(!player.getAttributes().isBanking() && !player.getTrading().inTrade() && player.getLocation().isSummoningAllowed() && !player.getDueling().inDuelScreen) {
			if(getFamiliar() == null || !SummoningData.beastOfBurden(getFamiliar().getSummonNpc().getId())) {
				player.getPacketSender().sendMessage("You do not have a Beast of Burden.");
				return;
			}
			if(player.getAdvancedSkills().getSummoning().storedItems.size() == 0) {
				player.getPacketSender().sendMessage("Your familiar is not currently holding any items for you.");
				return;
			}
			int familiarId = getFamiliar().getSummonNpc().getId();
			for(Item storedItem: player.getAdvancedSkills().getSummoning().storedItems) {
				if(player.getInventory().getFreeSlots() > 0) {
					player.getInventory().add(storedItem);
					player.getAdvancedSkills().getSummoning().storedItems.remove(storedItem);
					Logger.log(player.getUsername(), "Remove stored item: "+storedItem.getDefinition().getName()+" from Summoning familiar: "+familiarId+", using BoB to inventory.");
				} else
					break;
			}
			sendBoBItems();
			if(isStoring()) {
				player.getPacketSender().sendInterfaceSet(2700, 3321);
				player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
			}
			player.performAnimation(new Animation(827));
		} else
			player.getPacketSender().sendMessage("You cannot do this right now.");
	}

	public void store() {
		if(player == null)
			return;
		if(player.getConstitution() <= 0)
			return;
		if(player.getAttributes().isBanking())
			return;
		if(player.getAttributes().isShopping())
			return;
		if(player.getTrading().inTrade())
			return;
		if(player.getAttributes().isPriceChecking())
			return;
		if(player.getAttributes().getInterfaceId() != -1) {
			player.getPacketSender().sendMessage("Please close the interface you have open before opening another one.");
			return;
		}
		if(getFamiliar() != null) {
			if(SummoningData.beastOfBurden(getFamiliar().getSummonNpc().getId())) {
				player.getAdvancedSkills().getSummoning().setStoring(true);
				player.getPacketSender().sendInterfaceSet(2700, 3321);
				player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
				player.getAdvancedSkills().getSummoning().sendBoBItems();
			} else {
				player.getPacketSender().sendMessage("You don't have a beast of burden.");
			}
		} else {
			player.getPacketSender().sendMessage("You don't have a familiar.");
		}
	}

	public void storeItem(int item, int amount, int slot) {
		if(player.getAdvancedSkills().getSummoning().getFamiliar() == null || getFamiliar() == null)
			return;
		if(player.getAttributes().isBanking() || player.getAttributes().isShopping() || player.getTrading().inTrade() || player.getAttributes().isPriceChecking())
			return;
		if(!player.getInventory().contains(item) || slot < 0)
			return;
		Item itemToStore = player.getInventory().getItems()[slot];
		if(itemToStore.getId() != item || itemToStore.getAmount() <= 0)
			return;
		if(player.getAdvancedSkills().getSummoning().isStoring()) {
			if(ItemLending.borrowedItem(player, itemToStore.getId())) {
				player.getPacketSender().sendMessage("This item cannot be stored.");
				return;
			}
			/**
			 * Check if the player has reached max
			 */
			int maxStore = SummoningData.getStoreAmount(getFamiliar().getSummonNpc().getId());
			if(player.getAdvancedSkills().getSummoning().storedItems.size() == maxStore) {
				player.getPacketSender().sendMessage("Your familiar can only store "+maxStore+" items.");
				return;
			}
			if(amount + player.getAdvancedSkills().getSummoning().storedItems.size() >= maxStore)
				amount = maxStore-player.getAdvancedSkills().getSummoning().storedItems.size();
			if(amount == 0)
				return;
			if (player.getInventory().getAmount(item) < amount) {
				amount = player.getInventory().getAmount(item);
				if (amount == 0 || player.getInventory().getAmount(item) < amount)
					return;
			}
			if (!itemToStore.getDefinition().isStackable()) {
				for (int a = 0; a < amount && a < 28; a++) {
					if (player.getInventory().contains(item)) {
						player.getAdvancedSkills().getSummoning().storedItems.add(new Item(item, 1));
						player.getInventory().delete(item, 1);
						Logger.log(player.getUsername(), "Stored item "+itemToStore.getDefinition().getName()+" in Summoning familiar. ");
					} else
						break;
				}
			} else {
				player.getPacketSender().sendMessage("You cannot store stackable items.");
				return;
			}
			sendBoBItems();
			player.getPacketSender().sendInterfaceSet(2700, 3321);
			player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
		}
	}

	public static String getTimer(int seconds) {
		int minuteCounter = 0;
		int secondCounter = 0;
		for(int j = seconds; j > 0; j--) {
			if(secondCounter >= 59) {
				minuteCounter++;
				secondCounter = 0;
			} else
				secondCounter++;
		}
		if(minuteCounter == 0 && secondCounter == 0)
			return "";
		return ""+minuteCounter+"."+secondCounter+"";
	}

	public void removeStoredItem(int itemId, int amount) {
		if(player.getAttributes().isBanking() || player.getAttributes().isShopping() || player.getTrading().inTrade() || player.getAttributes().isPriceChecking())
			return;
		Item itemToRem = null;
		for (Item item : storedItems) {
			if (item.getId() == itemId) {
				itemToRem = item;
			}
		}
		if(itemToRem == null)
			return;
		if(player.getAdvancedSkills().getSummoning().isStoring()) {
			if (!itemToRem.getDefinition().isStackable()) {
				if (amount > 28)
					amount = 28;
				int familiarId = getFamiliar().getSummonNpc().getId();
				for (int a = 0; a < amount; a++) {
					for (Item item : player.getAdvancedSkills().getSummoning().storedItems) {
						if (item.getId() == itemToRem.getId()) {
							if (player.getInventory().getFreeSlots() > 0) {
								player.getAdvancedSkills().getSummoning().storedItems.remove(item);
								player.getInventory().add(itemToRem.getId(), 1);
								Logger.log(player.getUsername(), "Removed stored item "+itemToRem.getDefinition().getName()+" from Summoning familiar: "+familiarId);
							} else if(player.getInventory().getFreeSlots() <= 0) {
								player.getPacketSender().sendMessage("You do not have enough free space in your inventory.");
								return;
							}
							break;
						}
					}
				}
			}
			sendBoBItems();
			player.getPacketSender().sendInterfaceSet(2700, 3321);
			player.getPacketSender().sendItemContainer(player.getInventory(), 3322);
		}
	}

	public void sendBoBItems() {
		if(player.getAdvancedSkills().getSummoning().isStoring()) {
			for(int i = 0; i < SummoningData.frames.length; i++) {
				player.getPacketSender().sendItemOnInterface(SummoningData.getFrameID(i), -1, 1);
			}
			if(player.getAdvancedSkills().getSummoning().storedItems.size() > 0) {
				for(int i = 0; i < player.getAdvancedSkills().getSummoning().storedItems.size(); i++)
					player.getPacketSender().sendItemOnInterface(SummoningData.getFrameID(i), player.getAdvancedSkills().getSummoning().storedItems.get(i).getId(), 1);
			}
		}
	}

	public void resetFollower(boolean drop) {
		if(getFamiliar() != null) {
			player.getPacketSender().sendMessage("Your familiar has died.");
			player.getPacketSender().sendString(1, "[SUMMOfalse");
			if(SummoningData.getStoreAmount(getFamiliar().getSummonNpc().getId()) > 0 && drop) {
				player.getAdvancedSkills().getSummoning().resetBoB(true);
				player.getPacketSender().sendMessage("Your familiar has dropped all your items on the floor.");
			}
			World.deregister(getFamiliar().getSummonNpc());
			setFamiliar(null);
			summoningEvent = false;
		}
		clearInterface();
	}

	public void resetBoB(boolean drop) {
		if(player.getRights() == PlayerRights.ADMINISTRATOR)
			return;
		if(player.getAdvancedSkills().getSummoning().storedItems.size() > 0 && getFamiliar() != null && drop) {
			int familiarId = getFamiliar().getSummonNpc().getId();
			for(Item item: storedItems) {
				if(item != null) {
					GroundItemManager.spawnGroundItem(player, new GroundItem(item, getFamiliar().getSummonNpc().getPosition().copy(), player.getUsername(), player.getHostAdress(), false, 120, true, 80));
					Logger.log(player.getUsername(), "Reset familiar, dropping stored item: "+item.getDefinition().getName()+" from Summoning familiar: "+familiarId);
				}
			}
		}
		player.getAdvancedSkills().getSummoning().storedItems.clear();
		for(int i = 0; i < SummoningData.frames.length; i++)
			player.getPacketSender().sendItemOnInterface(SummoningData.getFrameID(i), -1, 1);
	}

	public enum FamiliarData {
		SPIRIT_WOLF(12047, 1, 4.8, 6830, 0.1, 1, 12425, new Item(12158),new Item(
				12155),new Item(2859),new Item(12183, 7)),
				DREADFOWL(12043, 4, 9.3, 6825, 0.1, 1, 12445, new Item(12158),new Item(
						12155),new Item(2138),new Item(12183, 8)),
						SPIRIT_SPIDER(12059, 10, 12.6, 6841, 0.2, 2, 12428, new Item(12158),
								new Item(12155),new Item(6291),new Item(12183, 8)),
								THORNY_SNAIL(12019, 13, 12.6, 6806, 0.2, 2, 12459, new Item(12158),
										new Item(12155),new Item(3363),new Item(12183, 9)),
										GRANITE_CRAB(12009, 16, 31.6, 6796, 0.2, 2, 1, new Item(12158),
												new Item(12155),new Item(440),new Item(12183, 7)),
												SPIRIT_MOSQUITO(12778, 17, 46.5, 7331, 0.5, 2, 1, new Item(12158),
														new Item(12155),new Item(6319),new Item(12183)),
														DESERT_WYRM(12049, 18, 31.2, 6831, 0.4, 1, 1, new Item(12159),new Item(
																12155),new Item(1783),new Item(12183, 45)),
																SPIRIT_SCORPION(12055, 19, 83.2, 6837, 0.9, 2, 1, new Item(12160),
																		new Item(12155),new Item(3095),new Item(12183, 57)),
																		SPIRIT_TZ_KIH(12808, 22, 96.8, 7361, 1.1, 3, 1, new Item(12160),
																				new Item(12168),new Item(12155),new Item(12183, 64)),
																				ALBINO_RAT(12067, 23, 202.4, 6847, 2.3, 1, 1, new Item(12163),new Item(
																						12155),new Item(2134),new Item(12183, 75)),
																						SPIRIT_KALPHITE(12063, 25, 220, 6994, 2.5, 3, 1, new Item(12163),
																								new Item(12155),new Item(3138),new Item(12183, 51)),
																								COMPOST_MOUND(12091, 28, 49.8, 6872, 0.6, 6, 1, new Item(12159),
																										new Item(12155),new Item(6032),new Item(12183, 47)),
																										GIANT_CHINCHOMPA(12800, 29, 255.2, 7353, 2.9, 1, 1, new Item(12163),
																												new Item(12155),new Item(10033),new Item(12183, 84)),
																												VAMPIRE_BAT(12053, 31, 136, 6835, 1.5, 4, 1, new Item(12160),new Item(
																														12155),new Item(3325),new Item(12183, 81)),
																														HONEY_BADGER(12065, 32, 140.8, 6845, 1.6, 4, 1, new Item(12160),
																																new Item(12155),new Item(12156),new Item(12183, 84)),
																																BEAVER(12021, 33, 57.6, 6808, 0.7, 4, 1, new Item(12159),
																																		new Item(12155),new Item(1519),new Item(12183, 72)),
																																		VOID_RAVAGER(12818, 34, 59.6, 7370, 0.7, 4, 1, new Item(12159),
																																				new Item(12164),new Item(12155),new Item(12183, 74)),
																																				VOID_SPINNER(12781, 34, 59.6, 7333, 0.7, 4, 1, new Item(12163),
																																						new Item(12166),new Item(12155),new Item(12183, 74)),
																																						VOID_TORCHER(12798, 34, 59.6, 7351, 0.7, 4, 1, new Item(12163),
																																								new Item(12167),new Item(12155),new Item(12183, 74)),
																																								VOID_SHIFTER(12814, 34, 59.6, 7367, 0.7, 4, 1, new Item(12163),
																																										new Item(12165),new Item(12155),new Item(12183, 74)),
																																										BRONZE_MINOTAUR(12073, 36, 316.8, 6853, 3.6, 3, 1, new Item(12163),
																																												new Item(12155),new Item(2349),new Item(12183, 102)),
																																												IRON_MINOTAUR(12075, 46, 404.8, 6855, 4.6, 9, 1, new Item(12163),
																																														new Item(12155),new Item(2351),new Item(12183, 125)),
																																														STEEL_MINOTAUR(12077, 56, 492.8, 6857, 5.6, 9, 1, new Item(12163),
																																																new Item(12155),new Item(2353),new Item(12183, 141)),
																																																MITHRIL_MINOTAUR(12079, 66, 580.8, 6859, 6.6, 9, 1, new Item(12163),
																																																		new Item(12155),new Item(2359),new Item(12183, 152)),
																																																		ADAMANT_MINOTAUR(12081, 76, 668.8, 6861, 7.6, 9, 1, new Item(12163),
																																																				new Item(12155),new Item(2361),new Item(12183, 144)),
																																																				RUNE_MINOTAUR(12083, 86, 756.8, 6863, 8.6, 9, 1, new Item(12163),
																																																						new Item(12155),new Item(2363),new Item(12183)),
																																																						BULL_ANT(12087, 40, 52.8, 6867, 0.6, 5, 1, new Item(12158),new Item(
																																																								12155),new Item(6010),new Item(12183, 11)),
																																																								MACAW(12071, 41, 72.4, 6851, 0.8, 5, 1, new Item(12159),
																																																										new Item(12155),new Item(249),new Item(12183, 78)),
																																																										EVIL_TURNIP(12051, 42, 184.8, 6833, 2.1, 5, 1, new Item(12160),
																																																												new Item(12155),new Item(12153),new Item(12183, 104)),
																																																												SPIRIT_COCKATRICE(12095, 43, 75.2, 6875, 0.9, 5, 1, new Item(12159),
																																																														new Item(12155),new Item(12109),new Item(12183, 88)),
																																																														SPIRIT_GUTHATRICE(12097, 43, 75.2, 6877, 0.9, 5, 1, new Item(12159),
																																																																new Item(12155),new Item(12111),new Item(12183, 88)),
																																																																SPIRIT_SARATRICE(12099, 43, 75.2, 6879, 0.9, 5, 1, new Item(12159),
																																																																		new Item(12155),new Item(12113),new Item(12183, 88)),
																																																																		SPIRIT_ZAMATRICE(12101, 43, 75.2, 6881, 0.9, 5, 1, new Item(12159),
																																																																				new Item(12155),new Item(12115),new Item(12183, 88)),
																																																																				SPIRIT_PENGATRICE(12103, 43, 75.2, 6883, 0.9, 5, 1, new Item(12159),
																																																																						new Item(12155),new Item(12117),new Item(12183, 88)),
																																																																						SPIRIT_CORAXATRICE(12105, 43, 75.2, 6885, 0.9, 5, 1, new Item(12159),
																																																																								new Item(12155),new Item(12119),new Item(12183, 88)),
																																																																								SPIRIT_VULATRICE(12107, 43, 75.2, 6887, 0.9, 5, 1, new Item(12159),new Item(
																																																																										12155),new Item(12121),new Item(12183, 88)),
																																																																										PYRELORD(12816, 46, 202.4, 7377, 2.3, 5, 1, new Item(12160),new Item(
																																																																												12155),new Item(590),new Item(12183, 111)),
																																																																												MAGPIE(12041, 47, 83.2, 6823, 0.9, 5, 1, new Item(12159),
																																																																														new Item(12155),new Item(1635),new Item(12183, 88)),
																																																																														BLOATED_LEECH(12061, 49, 215.2, 6843, 2.4, 5, 1, new Item(12160),
																																																																																new Item(12155),new Item(2132),new Item(12183, 117)),
																																																																																SPIRIT_TERRORBIRD(12007, 52, 68.4, 6794, 0.8, 6, 12441, new Item(12158),
																																																																																		new Item(12155),new Item(9978),new Item(12183, 12)),
																																																																																		ABYSSAL_PARASITE(12035, 54, 94.8, 6818, 1.1, 6, 1, new Item(12159),
																																																																																				new Item(12155),new Item(12161),new Item(12183, 106)),
																																																																																				SPIRIT_JELLY(12027, 55, 484, 6992, 5.5, 6, 1, new Item(12163),new Item(
																																																																																						12155),new Item(1937),new Item(12183, 151)),
																																																																																						IBIS(12531, 56, 98.8, 6991, 1.1, 6, 1, new Item(12159),new Item(12155),
																																																																																								new Item(311),new Item(12183, 109)),
																																																																																								SPIRIT_KYATT(12812, 57, 501.6, 7365, 5.7, 6, 1, new Item(12163),
																																																																																										new Item(12155),new Item(10103),new Item(12183, 153)),
																																																																																										SPIRIT_LARUPIA(12784, 57, 501.6, 7337, 5.7, 6, 1, new Item(12163),
																																																																																												new Item(12155),new Item(10095),new Item(12183, 155)),
																																																																																												SPIRIT_GRAAHK(12710, 57, 501.6, 7363, 5.7, 6, 1, new Item(12163),
																																																																																														new Item(12155),new Item(10099),new Item(12183, 154)),
																																																																																														KARAMTHULHU(12023, 58, 510.4, 6809, 5.8, 6, 1, new Item(12163),
																																																																																																new Item(12155),new Item(6667),new Item(12183, 144)),
																																																																																																SMOKE_DEVIL(12085, 61, 268, 6865, 3, 7, 1, new Item(12160),new Item(
																																																																																																		12155),new Item(9736),new Item(12183, 141)),
																																																																																																		ABYSSAL_LUKRER(12037, 62, 109.6, 6820, 1.9, 9, 1, new Item(12159),new Item(
																																																																																																				12155),new Item(12161),new Item(12183, 119)),
																																																																																																				SPIRIT_COBRA(12015, 63, 276.8, 6802, 3.1, 6, 1, new Item(12160),
																																																																																																						new Item(12155),new Item(6287),new Item(12183, 116)),
																																																																																																						STRANGER_PLANT(12045, 64, 281.6, 6827, 3.2, 6, 1, new Item(12160),
																																																																																																								new Item(12155),new Item(8431),new Item(12183, 128)),
																																																																																																								BARKER_TOAD(12123, 66, 87, 6889, 1, 7, 1, new Item(12158),new Item(
																																																																																																										12155),new Item(2150),new Item(12183, 11)),
																																																																																																										WAR_TORTOISE(12031, 67, 58.6, 6815, 0.7, 7, 12439, new Item(12158),
																																																																																																												new Item(12155),new Item(7939),new Item(12183)),
																																																																																																												BUNYIP(12029, 68, 119.2, 6813, 1.4, 7, 1, new Item(12159),new Item(
																																																																																																														12155),new Item(383),new Item(12183, 110)),
																																																																																																														FRUIT_BAT(12033, 69, 121.2, 6817, 1.4, 8, 1, new Item(12159),new Item(
																																																																																																																12155),new Item(1963),new Item(12183, 130)),
																																																																																																																RAVENOUS_LOCUST(12820, 70, 132, 7372, 1.5, 4, 1, new Item(12160),
																																																																																																																		new Item(12155),new Item(1933),new Item(12183, 79)),
																																																																																																																		ARCTIC_BEAR(12057, 71, 93.2, 6839, 1.1, 8, 1, new Item(12158),new Item(
																																																																																																																				12155),new Item(10117),new Item(12183, 14)),
																																																																																																																				PHOENIX(14623, 72, 302, 8575, 1.1, 8, 1, new Item(12160),
																																																																																																																						new Item(12155),new Item(14616),new Item(12183, 165)),
																																																																																																																						OBSIDIAN_GOLEM(12792, 73, 642.4, 7345, 7.3, 8, 1, new Item(12163),
																																																																																																																								new Item(12155),new Item(12168),new Item(12183, 195)),
																																																																																																																								GRANITE_LOBSTER(12069, 74, 325.6, 6849, 3.7, 8, 1, new Item(12160),
																																																																																																																										new Item(12155),new Item(6983),new Item(12183, 166)),
																																																																																																																										PRAYING_MANTIS(12011, 75, 329.6, 6798, 3.6, 8, 1, new Item(12160),
																																																																																																																												new Item(12155),new Item(2460),new Item(12183, 168)),
																																																																																																																												FORGE_REGENT_BEAST(12782, 76, 134, 7335, 1.5, 9, 1, new Item(12159),new Item(
																																																																																																																														12155),new Item(10020),new Item(12183, 141)),
																																																																																																																														TALON_BEAST(12794, 77, 1015.2, 7347, 3.8, 9, 1, new Item(12160),
																																																																																																																																new Item(12155),new Item(12162),new Item(12183, 174)),
																																																																																																																																GIANT_ENT(12013, 78, 136.8, 6800, 1.6, 8, 1, new Item(12159),new Item(
																																																																																																																																		5933),new Item(12155),new Item(12183, 124)),
																																																																																																																																		HYDRA(12025, 80, 140.8, 9488, 1.6, 9, 1, new Item(12159),new Item(571),
																																																																																																																																				new Item(12115),new Item(12183, 128)),
																																																																																																																																				SPIRIT_DAGANNOTH(12017, 83, 364.8, 6804, 4.1, 9, 1, new Item(12160),
																																																																																																																																						new Item(6155),new Item(12115),new Item(12183)),
																																																																																																																																						UNICORN_STALLION(12039, 89, 154.4, 6822, 1.8, 9, 1, new Item(12159),
																																																																																																																																								new Item(237),new Item(12115),new Item(12183, 203)),
																																																																																																																																								WOLPERTINGER(12089, 92, 404.8, 6869, 4.5, 10, 1, new Item(12160),
																																																																																																																																										new Item(2859),new Item(3226),new Item(12115),new Item(12183,203)),
																																																																																																																																										PACK_YAK(12093, 96, 422.4, 6873, 4.8, 10, 1, new Item(12160),new Item(
																																																																																																																																												10818),new Item(12183, 211)),
																																																																																																																																												FIRE_TITAN(12802, 79, 695.2, 7355, 7.9, 9, 1, new Item(12163),new Item(
																																																																																																																																														1442),new Item(12155),new Item(12183, 198)),
																																																																																																																																														MOSS_TITAN(12804, 79, 695.2, 7357, 7.9, 9, 1, new Item(12163),new Item(
																																																																																																																																																1440),new Item(12155),new Item(12183, 198)),
																																																																																																																																																ICE_TITAN(12806, 79, 695.2, 7359, 7.9, 9, 1, new Item(12163),new Item(
																																																																																																																																																		1438),new Item(1444),new Item(12155),new Item(12183, 198)),
																																																																																																																																																		LAVA_TITAN(12788, 83, 730.4, 7341, 8.3, 9, 1, new Item(12163),new Item(
																																																																																																																																																				12168),new Item(12155),new Item(12183, 219)),
																																																																																																																																																				SWAMP_TITAN(12776, 85, 373.6, 7329, 4.2, 9, 1, new Item(12160),
																																																																																																																																																						new Item(10149),new Item(12155),new Item(12183, 150)),
																																																																																																																																																						GEYSER_TITAN(12786, 89, 783.2, 7339, 8.9, 9, 1, new Item(12163),
																																																																																																																																																								new Item(1444),new Item(12155),new Item(12183, 222)),
																																																																																																																																																								ABYSSAL_TITAN(12796, 93, 163.2, 7349, 1.9, 10, 1, new Item(12159),
																																																																																																																																																										new Item(12161),new Item(12155),new Item(12183, 113)),
																																																																																																																																																										IRON_TITAN(12822, 95, 417.6, 7375, 4.7, 10, 1, new Item(12160),
																																																																																																																																																												new Item(1115),new Item(12155),new Item(12183, 198)),
																																																																																																																																																												STEEL_TITAN(12790, 99, 435.2, 7343, 4.9, 10, 12825, new Item(12160),
																																																																																																																																																														new Item(1119), new Item(12155), new Item(12183, 178));

		private FamiliarData(int pouchId, int levelRequired, double createExperience,
				int npcId, double summonExperience, int summoningPointsRequired, int scroll, Item... recipe) {
			this.pouchId = pouchId;
			this.levelRequired = levelRequired;
			this.createExperience = createExperience;
			this.npcId = npcId;
			this.summonExperience = summonExperience;
			this.summoningPointsRequired = summoningPointsRequired;
			this.scroll = scroll;
			this.recipe = recipe;
		}

		public final int pouchId;

		private final int levelRequired;

		@SuppressWarnings("unused")
		private final double createExperience;

		private final int npcId;

		@SuppressWarnings("unused")
		private final double summonExperience;

		private final int summoningPointsRequired;

		@SuppressWarnings("unused")
		private final Item[] recipe;

		public final int scroll;

		public static FamiliarData forId(int pouchId) {
			for (FamiliarData familiar : FamiliarData.values()) {
				if (familiar.getPouchId() == pouchId) {
					return familiar;
				}
			}
			return null;
		}

		public static FamiliarData forNPCId(int npcId) {
			for (FamiliarData familiar : FamiliarData.values()) {
				if (familiar.npcId == npcId)
					return familiar;
			}
			return null;
		}

		public int getPouchId() {
			return pouchId;
		}
	}


	public void moveFollower() { //For some reason moveTo isn't working for npcs.. have to deregister and register again..
		if(getFamiliar() != null && getFamiliar().getSummonNpc() != null) {
			int npc = getFamiliar().getSummonNpc().getId();
			int time = getFamiliar().getDeathTimer();
			World.deregister(getFamiliar().getSummonNpc());
			TaskManager.submit(new FamiliarSpawnTask(player).setFamiliarId(npc).setDeathTimer(time));
		}
	}

	public void login() {
		clearInterface();
		if(spawnTask != null)
			TaskManager.submit(spawnTask);
		spawnTask = null;
	}

	public void clearInterface() {
		player.getPacketSender().sendString(54045, "");
		player.getPacketSender().sendString(54043, "");
		player.getPacketSender().sendString(54028, "");
		player.getPacketSender().sendString(54024, "0");
		player.getPacketSender().sendNpcHeadOnInterface(60, 54021); // 60 = invisable head to remove it
		player.getPacketSender().sendString(18045, player.getSkillManager().getMaxLevel(Skill.SUMMONING) < 10 ? "   "+player.getSkillManager().getCurrentLevel(Skill.SUMMONING)+"/"+player.getSkillManager().getMaxLevel(Skill.SUMMONING) :  " "+player.getSkillManager().getCurrentLevel(Skill.SUMMONING)+"/"+player.getSkillManager().getMaxLevel(Skill.SUMMONING));
	}

	private FamiliarSpawnTask spawnTask;

	public FamiliarSpawnTask getSpawnTask() {
		return spawnTask;
	}

	public FamiliarSpawnTask setFamiliarSpawnTask(FamiliarSpawnTask spawnTask) {
		this.spawnTask = spawnTask;
		return this.spawnTask;
	}

	/**
	 * Summoning
	 */
	private Familiar familiar = null;

	public Familiar getFamiliar() {
		return this.familiar;
	}

	public void setFamiliar(Familiar familiar) {
		this.familiar = familiar;
	}

	/*
	 * STORING
	 */
	public CopyOnWriteArrayList<Item> storedItems = new CopyOnWriteArrayList<Item>();
	private boolean storing;

	public boolean isStoring() {
		return storing;
	}

	public void setStoring(boolean store) {
		storing = store;
	}

	/*
	 * Pouch infusion
	 */
	private Pouch pouch;

	public void setPouch(Pouch pouch) {
		this.pouch = pouch;
	}

	public Pouch getPouch() {
		return pouch;
	}

	public static void sendSummoningLevel(Player player) {
		player.getPacketSender().sendString(1, "set summ "+player.getSkillManager().getMaxLevel(Skill.SUMMONING)+"");
	}
}
