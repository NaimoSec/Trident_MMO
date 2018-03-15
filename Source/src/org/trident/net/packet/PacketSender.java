package org.trident.net.packet;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.GameObject;
import org.trident.model.Graphic;
import org.trident.model.Item;
import org.trident.model.PlayerInteractingOption;
import org.trident.model.PlayerRights;
import org.trident.model.Position;
import org.trident.model.Projectile;
import org.trident.model.Skill;
import org.trident.model.container.ItemContainer;
import org.trident.model.container.impl.Shop;
import org.trident.model.container.impl.Bank.BankSearchAttributes;
import org.trident.net.packet.Packet.PacketType;
import org.trident.util.Constants;
import org.trident.util.Misc;
import org.trident.util.NameUtils;
import org.trident.world.clip.region.Palette;
import org.trident.world.clip.region.Palette.PaletteTile;
import org.trident.world.content.CustomObjects;
import org.trident.world.content.Locations;
import org.trident.world.content.PriceChecker;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.entity.Entity;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;

/**
 * This class manages making the packets that will be sent (when called upon) onto
 * the associated player's client.
 * 
 * @author relex lawl & Gabbe
 */

public class PacketSender {

	/**
	 * Sends the removal of every player attribute that should
	 * be taken out when they are in movement.
	 * @return	The PacketSender instance.
	 */
	public PacketSender sendNonWalkableAttributeRemoval() {
		player.getPacketSender().sendInterfaceRemoval();
		player.setEntityInteraction(null);
		player.followCharacter(null);
		CombatHandler.resetAttack(player);
		player.setTeleporting(false);
		player.getAttributes().setInactiveTimer(0).setWalkToTask(null);
		player.getSkillManager().stopSkilling();
		return this;
	}

	/**
	 * Sends the map region a player is located in and also
	 * sets the player's first step position of said region as their
	 * {@code lastKnownRegion}.
	 * @return	The PacketSender instance.
	 */
	public PacketSender sendMapRegion() {
		player.setLastKnownRegion(player.getPosition().copy());
		player.write(new PacketBuilder(73).writeShortA(player.getPosition().getRegionX() + 6).writeShort(player.getPosition().getRegionY() + 6).toPacket());
		return this;
	}

	/**
	 * Fuck this took me 3 days to fix /Gabbe
	 * Sends the construction map region packet for construction
	 * @return	The PacketSender instance.
	 */
	public PacketSender constructMapRegionForConstruction(Palette palette) {
		PacketBuilder bldr = new PacketBuilder(241, PacketType.SHORT);
		bldr.writeShortA(player.getPosition().getRegionY() + 6);
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					PaletteTile tile = palette.getTile(x, y, z);
					boolean b = false;
					if (x < 2 || x > 10 || y < 2 || y > 10)
						b = true;
					int toWrite = !b && tile != null ? 5 : 0;
					bldr.writeByte(toWrite);
					if(toWrite == 5) {
						int val = tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 | tile.getRotation() << 1;
						bldr.writeString(""+val+"");
					}
				}
			}
		}
		bldr.writeShort(player.getPosition().getRegionX() + 6);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends the logout packet for the player.
	 * @return	The PacketSender instance.
	 */
	public PacketSender sendLogout() {
		player.write(new PacketBuilder(109).toPacket());
		return this;
	}

	/**
	 * Sets the world's system update time, once timer is 0, everyone will be disconnected.
	 * @param time	The amount of seconds in which world will be updated in.
	 * @return		The PacketSender instance.
	 */
	public PacketSender sendSystemUpdate(int time) {
		player.write(new PacketBuilder(114).writeLEShort(time).toPacket());
		return this;
	}

	/**
	 * Sends a game message to a player in the server.
	 * @param message	The message they will receive in chat box.
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendMessage(String message) {
		player.write(new PacketBuilder(253, PacketType.BYTE).writeString(message).toPacket());
		return this;
	}

	/**
	 * Sends skill information onto the client, to calculate things such as
	 * constitution, prayer and summoning orb and other configurations.
	 * @param skill		The skill being sent.
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendSkill(Skill skill) {
		PacketBuilder bldr = new PacketBuilder(134);
		bldr.writeByte(skill.ordinal());
		bldr.writeSingleInt(player.getSkillManager().getExperience(skill));
		bldr.writeShort(player.getSkillManager().getCurrentLevel(skill));
		bldr.writeShort(player.getSkillManager().getMaxLevel(skill));
		bldr.writeShort(6667);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends skill information onto the client, to calculate things such as
	 * constitution, prayer and summoning orb and other configurations.
	 * @param skill		The skill being sent.
	 * @param experience	The amount of experience gained.
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendSkill(Skill skill, int experience) {
		PacketBuilder bldr = new PacketBuilder(134);
		bldr.writeByte(skill.ordinal());
		bldr.writeSingleInt(player.getSkillManager().getExperience(skill));
		bldr.writeShort(player.getSkillManager().getCurrentLevel(skill));
		bldr.writeShort(player.getSkillManager().getMaxLevel(skill));
		bldr.writeShort(experience);
		player.write(bldr.toPacket());
		return this;
	}

	public PacketSender sendExpOrbUpdate(Skill skill, int experience) {
		PacketBuilder bldr = new PacketBuilder(256);
		bldr.writeByte(skill.ordinal());
		bldr.writeShort(experience);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends a configuration button's state.
	 * @param configId	The id of the configuration button.
	 * @param state		The state to set it to.
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendConfig(int configId, int state) {
		PacketBuilder builder = new PacketBuilder(36);
		builder.writeLEShort(configId);
		builder.writeByte(state);
		player.write(builder.toPacket());
		return this;
	}

	/**
	 * Sends a interface child's toggle. 
	 * @param id		The id of the child.
	 * @param state		The state to set it to.
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendToggle(int id, int state) {
		player.write(new PacketBuilder(87).writeLEShort(id).writeSingleInt(state).toPacket());
		return this;
	}

	/**
	 * Sends the state in which the player has their chat options, such as public, private, friends only.
	 * @param publicChat	The state of their public chat.
	 * @param privateChat	The state of their private chat.
	 * @param tradeChat		The state of their trade chat.
	 * @return				The PacketSender instance.
	 */
	public PacketSender sendChatOptions(int publicChat, int privateChat, int tradeChat) {
		player.write(new PacketBuilder(206).writeByte(publicChat).writeByte(privateChat).writeByte(tradeChat).toPacket());
		return this;
	}

	public PacketSender sendSong(int id) {
		player.write(new PacketBuilder(74).writeLEShort(id).toPacket());
		return this;
	}

	public PacketSender sendPacket161(int frameId, int color, int height) {
		player.write(new PacketBuilder(161).writeShort(frameId).writeByte(color).writeShort(height).toPacket());
		return this;
	}

	public PacketSender sendSongConfigurations(int nextSong, int previousSong) {
		player.write(new PacketBuilder(121).writeLEShortA(nextSong).writeShortA(previousSong).toPacket());
		return this;
	}

	public PacketSender sendSound(int soundId, int volume, int delay) {
		player.write(new PacketBuilder(175).writeLEShortA(soundId).writeByte(volume).writeShort(delay).toPacket());
		return this;
	}

	public PacketSender createFrame(int frame) {
		if(player.getAttributes().getInterfaceId() <= 0)
			player.getAttributes().setInterfaceId(50); //50 means that it will reset on walk cuz an interface is open
		player.write(new PacketBuilder(164).writeLEShort(frame).toPacket());
		return this;
	}

	public PacketSender sendRunEnergy(int energy) {
		player.write(new PacketBuilder(110).writeByte(energy).toPacket());
		return this;
	}

	public PacketSender sendRunStatus() {
		sendConfig(173, player.getAttributes().isRunning() ? 1 : 0);
		player.write(new PacketBuilder(113).writeByte(player.getAttributes().isRunning() ? 1 : 0).toPacket());
		return this;
	}

	public PacketSender sendWeight(int weight) {
		player.write(new PacketBuilder(240).writeShort(weight).toPacket());
		return this;
	}

	public PacketSender commandFrame(int i) {
		player.write(new PacketBuilder(28).writeByte(i).toPacket());
		return this;
	}

	public PacketSender sendInterface(int id) {
		player.write(new PacketBuilder(97).writeShort(id).toPacket());
		player.getAttributes().setInterfaceId(id);
		return this;
	}

	public PacketSender sendWalkableInterface(int interfaceId) {
		player.getAttributes().setWalkableInterfaceId(interfaceId);
		player.write(new PacketBuilder(208).writeShort(interfaceId).toPacket());
		return this;
	}

	public PacketSender sendInterfaceDisplayState(int interfaceId, boolean hide) {
		player.write(new PacketBuilder(171).writeByte(hide ? 1 : 0).writeShort(interfaceId).toPacket());
		return this;
	}

	public PacketSender sendInterfaceDisplayState(int state, int interfaceId) {
		player.write(new PacketBuilder(171).writeByte(state).writeShort(interfaceId).toPacket());
		return this;
	}

	public PacketSender sendInterfaceMediaState(int interfaceId, int state) {
		player.write(new PacketBuilder(8).writeLEShortA(interfaceId).writeShort(state).toPacket());
		return this;
	}

	public PacketSender sendInventoryOverlayInterface(int interfaceId) {
		player.write(new PacketBuilder(142).writeLEShort(interfaceId).toPacket());
		return this;
	}

	public PacketSender sendStringColour(int stringId, int colour) {
		//player.write(new PacketBuilder(122).writeLEShortA(stringId).writeLEShortA(colour).toPacket());
		return this;
	}

	public PacketSender sendPlayerHeadOnInterface(int id) {
		player.write(new PacketBuilder(185).writeLEShortA(id).toPacket());
		return this;
	}

	public PacketSender sendNpcHeadOnInterface(int id, int interfaceId) {
		player.write(new PacketBuilder(75).writeLEShortA(id).writeLEShortA(interfaceId).toPacket());
		return this;
	}

	public PacketSender sendEnterAmountPrompt(String title) {
		player.write(new PacketBuilder(27, PacketType.BYTE).writeString(title).toPacket());
		return this;
	}

	public PacketSender sendEnterInputPrompt(String title) {
		player.write(new PacketBuilder(187, PacketType.BYTE).writeString(title).toPacket());
		return this;
	}

	public PacketSender sendInterfaceReset() {
		player.write(new PacketBuilder(68).toPacket());
		return this;
	}

	public PacketSender sendInterfaceComponentMoval(int x, int y, int id) {
		player.write(new PacketBuilder(70).writeShort(x).writeShort(y).writeLEShort(id).toPacket());
		return this;
	}

	public PacketSender sendBlinkingHint(String title, String information, int x, int y, int speed, int pause, int type, final int time) {
		player.write(new PacketBuilder(179, PacketType.SHORT).writeString(title).writeString(information).writeShort(x).writeShort(y).writeByte(speed).writeByte(pause).writeByte(type).toPacket());
		if(type > 0) {
			TaskManager.submit(new Task(time, player, false) {
				@Override
				public void execute() {
					player.getPacketSender().sendBlinkingHint("", "", 0, 0, 0, 0, -1, 0);
					stop();
				}
			});
		}
		return this;
	}

	public PacketSender sendInterfaceColor(int id, String color) {
		long longColor = NameUtils.stringToLong(color);
		player.write(new PacketBuilder(141).writeShort(id).writeLong(longColor).toPacket());
		return this;
	}

	public PacketSender sendInterfaceEdit(int zoom, int id, int rotationX, int rotationY) {
		player.write(new PacketBuilder(230).writeShortA(zoom).writeShort(id).writeShort(rotationX).writeLEShortA(rotationY).toPacket());
		return this;
	}

	public PacketSender sendInterfaceAnimation(int interfaceId, Animation animation) {
		player.write(new PacketBuilder(200).writeShort(interfaceId).writeShort(animation.getId()).toPacket());
		return this;
	}

	public PacketSender sendInterfaceModel(int interfaceId, int itemId, int zoom) {
		player.write(new PacketBuilder(246).writeLEShort(interfaceId).writeShort(zoom).writeShort(itemId).toPacket());
		return this;
	}

	public PacketSender sendModelOnComponent(int interfaceId, int modelId) {
		player.write(new PacketBuilder(8).writeLEShortA(interfaceId).writeShort(modelId).toPacket());
		return this;
	}

	public PacketSender sendScrollbar(int childId, int location) {
		player.write(new PacketBuilder(79).writeLEShort(childId).writeShortA(location).toPacket());
		return this;
	}

	public PacketSender sendTabInterface(int tabId, int interfaceId) {
		player.write(new PacketBuilder(71).writeShort(interfaceId).writeByteA(tabId).toPacket());
		return this;
	}

	public PacketSender sendTabs() {
		for (int i = 0; i < Constants.SIDEBAR_INTERFACES.length; i++)
			sendTabInterface(i, Constants.SIDEBAR_INTERFACES[i]);
		return this;
	}

	public PacketSender removeTabs() {
		for (int i = 0; i < Constants.SIDEBAR_INTERFACES.length; i++)
			sendTabInterface(i, -1);
		return this;
	}

	public PacketSender sendTab(int id) {
		player.write(new PacketBuilder(106).writeByteC(id).toPacket());
		return this;
	}

	public PacketSender sendFlashingSidebar(int id) {
		player.write(new PacketBuilder(24).writeByteS(id).toPacket());
		return this;
	}

	public PacketSender sendChatInterface(int id) {
		if(player.getAttributes().getInterfaceId() <= 0)
			player.getAttributes().setInterfaceId(55);
		player.write(new PacketBuilder(218).writeLEShort(id).toPacket());
		return this;
	}

	public PacketSender sendChatboxInterface(int id) {
		if(player.getAttributes().getInterfaceId() <= 0)
			player.getAttributes().setInterfaceId(55);
		player.write(new PacketBuilder(164).writeLEShort(id).toPacket());
		return this;
	}

	public PacketSender sendMapState(int state) {
		player.write(new PacketBuilder(99).writeByte(state).toPacket());
		return this;
	}

	public PacketSender sendCameraAngle(Position position, int speed, int angle) {
		player.write(new PacketBuilder(177).writeByte(position.getX()).writeByte(position.getY()).writeShort(position.getZ()).writeByte(speed).writeByte(angle).toPacket());
		return this;
	}

	public PacketSender sendCameraShake(int verticalAmount, int verticalSpeed, int horizontalAmount, int horizontalSpeed) {
		player.write(new PacketBuilder(35).writeByte(verticalAmount).writeByte(verticalSpeed).writeByte(horizontalAmount).writeByte(horizontalSpeed).toPacket());
		return this;
	}

	public PacketSender sendCameraSpin(Position position, int speed, int angle) {
		player.write(new PacketBuilder(166).writeByte(position.getX()).writeByte(position.getY()).writeShort(position.getZ()).writeByte(speed).writeByte(angle).toPacket());
		return this;
	}

	public PacketSender sendCameraNeutrality() {
		player.write(new PacketBuilder(107).toPacket());
		return this;
	}

	public PacketSender sendInterfaceRemoval() {
		if(player.getAttributes().isShopping()) {
			sendClientRightClickRemoval().sendItemsOnInterface(Shop.INTERFACE_ID, new Item[]{new Item(-1)});
			player.getAttributes().setShopping(false);
		}
		if(player.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().isViewingInterface()) {
			sendClientRightClickRemoval().sendItemsOnInterface(Shop.INTERFACE_ID, new Item[]{new Item(-1)});
			player.getAttributes().getMinigameAttributes().getFishingTrawlerAttributes().setViewingInterface(false).getRewards().clear();
		}
		if(player.getAdvancedSkills().getSummoning().isStoring()) {
			sendClientRightClickRemoval();
			player.getAdvancedSkills().getSummoning().setStoring(false);
		}
		if(player.getDueling().inDuelScreen && player.getDueling().duelingStatus != 5) {
			sendClientRightClickRemoval();
			player.getDueling().declineDuel(player.getDueling().duelingWith > 0 ? true : false);
		}
		if(player.getTrading().inTrade()) {
			sendClientRightClickRemoval();
			player.getTrading().declineTrade(true);
		}
		if(player.getAttributes().isPriceChecking()) {
			sendClientRightClickRemoval();
			PriceChecker.closePriceChecker(player);
		}
		if(player.getAttributes().getBankSearchingAttribtues().isSearchingBank())
			BankSearchAttributes.stopSearch(player, false);
		if(player.getAttributes().isBanking()) {
			sendClientRightClickRemoval();
			player.getAttributes().setBanking(false);
		}
		player.getAttributes().setInterfaceId(-1).setShop(null).setProductChosen(-1);
		player.getAppearance().setCanChangeAppearance(false);
		player.write(new PacketBuilder(219).toPacket());
		return this;
	}

	public PacketSender sendInterfaceSet(int interfaceId, int sidebarInterfaceId) {
		PacketBuilder out = new PacketBuilder(248);
		out.writeShortA(interfaceId);
		out.writeShort(sidebarInterfaceId);
		player.write(out.toPacket());
		player.getAttributes().setInterfaceId(interfaceId);
		return this;
	}

	public PacketSender sendItemContainer(ItemContainer container, int interfaceId) {
		PacketBuilder builder = new PacketBuilder(53, PacketType.SHORT);
		builder.writeShort(interfaceId);
		builder.writeShort(container.capacity());
		for (Item item : container.getItems()) {
			if (item.getAmount() > 254) {
				builder.writeByte(255);
				builder.writeDoubleInt(item.getAmount());
			} else {
				builder.writeByte(item.getAmount());
			}
			builder.writeLEShortA(item.getId() + 1);
		}
		player.write(builder.toPacket());
		return this;
	}

	public PacketSender sendItemOnInterface(int frame, int item, int slot, int amount) {
		PacketBuilder builder = new PacketBuilder(34, PacketType.SHORT);
		builder.writeShort(frame);
		builder.writeByte(slot);
		builder.writeShort(item + 1); 
		builder.writeByte(255);
		builder.writeInt(amount);
		player.write(builder.toPacket());
		return this;
	}

	public PacketSender sendDuelEquipment() {
		for (int i = 0; i < player.getEquipment().getItems().length; i++) {
			PacketBuilder builder = new PacketBuilder(34, PacketType.SHORT);
			builder.writeShort(13824);
			builder.writeByte(i);
			builder.writeShort(player.getEquipment().getItems()[i].getId()+1); 
			builder.writeByte(255);
			builder.writeInt(player.getEquipment().getItems()[i].getAmount());
			player.write(builder.toPacket());
		}
		return this;
	}

	public PacketSender sendSmithingData(int id, int slot, int column, int amount) {
		PacketBuilder builder = new PacketBuilder(34, PacketType.SHORT);
		builder.writeShort(column);
		builder.writeByte(4);
		builder.writeInt(slot);
		builder.writeShort(id + 1); 
		builder.writeByte(amount);
		player.write(builder.toPacket());
		return this;
	}

	public PacketSender sendInterfaceItems(int interfaceId, CopyOnWriteArrayList<Item> items) {
		PacketBuilder builder = new PacketBuilder(53, PacketType.SHORT);
		builder.writeShort(interfaceId);
		builder.writeShort(items.size());
		int current = 0;
		for (Item item : items) {
			if (item.getAmount() > 254) {
				builder.writeByte(255);
				builder.writeDoubleInt(item.getAmount());
			} else {
				builder.writeByte(item.getAmount());
			}
			builder.writeLEShortA(item.getId() + 1);
			current++;
		}
		if (current < 27) {
			for (int i = current; i < 28; i++) {
				builder.writeByte(1);
				builder.writeLEShortA(-1);
			}
		}
		player.write(builder.toPacket());
		return this;
	}

	public PacketSender sendItemOnInterface(int interfaceId, int item, int amount) {
		PacketBuilder builder = new PacketBuilder(53, PacketType.SHORT);
		if(item <= 0)
			item = -1;
		if(amount <= 0)
			amount = 1;
		if(interfaceId <= 0)
			return this;
		builder.writeShort(interfaceId);
		builder.writeShort(1);
		if (amount > 254) {
			builder.writeByte(255);
			builder.writeDoubleInt(amount);
		} else {
			builder.writeByte(amount);
		}
		builder.writeLEShortA(item + 1);

		player.write(builder.toPacket());
		return this;
	}

	public PacketSender sendItemsOnInterface(int interfaceId, Item[] items) {
		PacketBuilder builder = new PacketBuilder(53, PacketType.SHORT);
		for(int i = 0; i < items.length; i++) {
			if(items[i] != null && items[i].getId() > 0 && items[i].getAmount() > 0) {
				builder.writeShort(interfaceId);
				builder.writeShort(items.length);

				if (items[i].getAmount() > 254) {
					builder.writeByte(255);
					builder.writeDoubleInt(items[i].getAmount());
				} else {
					builder.writeByte(items[i].getAmount());
				}

				builder.writeLEShortA(items[i].getId() + 1);
			} else
				continue;
		}
		player.write(builder.toPacket());
		return this;
	}

	public PacketSender sendItemsOnInterface(int interfaceId, int[] items) {
		PacketBuilder builder = new PacketBuilder(53, PacketType.SHORT);
		for(int i = 0; i < items.length; i++) {
			builder.writeShort(interfaceId);
			builder.writeShort(items.length);
			builder.writeByte(1);
			builder.writeLEShortA(items[i] + 1);
		}
		player.write(builder.toPacket());
		return this;
	}

	public PacketSender sendInterfaceItems(int interfaceId, Collection<Item> items) {
		PacketBuilder builder = new PacketBuilder(53, PacketType.SHORT);
		builder.writeShort(interfaceId);
		builder.writeShort(items.size());
		for (Item item : items) {
			if (item.getAmount() > 254) {
				builder.writeByte(255);
				builder.writeDoubleInt(item.getAmount());
			} else {
				builder.writeByte(item.getAmount());
			}
			builder.writeLEShortA(item.getId() + 1);
			if (interfaceId == 1203) {
				builder.writeInt(item.getDefinition().getValue());
			}
		}
		player.write(builder.toPacket());
		return this;
	}

	/*public PacketSender sendConstructionInterfaceItems(ArrayList<Furniture> items) {
		PacketBuilder builder = new PacketBuilder(53, PacketType.SHORT);
		builder.writeShort(38274);
		builder.writeShort(items.size());
		for (int i = 0; i < items.size(); i++) {
			builder.writeByte(1);
			builder.writeLEShortA(items.get(i).getItemId() + 1);
		}
		player.write(builder.toPacket());
		return this;
	}*/

	public PacketSender sendInterfaceItems(int interfaceId, Item[] items) {
		PacketBuilder builder = new PacketBuilder(53, PacketType.SHORT);
		builder.writeShort(interfaceId);
		builder.writeShort(items.length);
		for (Item item : items) {
			if (item.getAmount() > 254) {
				builder.writeByte(255);
				builder.writeDoubleInt(item.getAmount());
			} else {
				builder.writeByte(item.getAmount());
			}
			builder.writeLEShortA(item.getId() + 1);
			if (interfaceId == 1203) {
				builder.writeInt(item.getDefinition().getValue());
			}
		}
		player.write(builder.toPacket());
		return this;
	}


	public PacketSender sendShopContainer(ItemContainer container, int interfaceId) {
		PacketBuilder builder = new PacketBuilder(53, PacketType.SHORT);
		builder.writeShort(interfaceId);
		builder.writeShort(container.capacity());
		for (int i = 0; i < container.getItems().length; i++) {
			Item item = container.getItems()[i];
			if (item.getAmount() > 254) {
				builder.writeByte(255);
				builder.writeDoubleInt(item.getAmount());
			} else {
				builder.writeByte(item.getAmount());
			}
			builder.writeLEShortA(item.getId() + 1);
			builder.writeInt(item.getDefinition().getValue());
		}
		player.write(builder.toPacket());
		return this;
	}

	public PacketSender sendInteractionOption(String option, int slot, boolean top) {
		PacketBuilder bldr = new PacketBuilder(104, PacketType.BYTE);
		bldr.writeByteC(slot);
		bldr.writeByteA(top ? 1 : 0);
		bldr.writeString(option);
		player.write(bldr.toPacket());
		PlayerInteractingOption interactingOption = PlayerInteractingOption.forName(option);
		if(option != null)
			player.getAttributes().setPlayerInteractingOption(interactingOption);
		return this;
	}

	public PacketSender sendString(int id, String string) {
		if(id == 18250 && string.length() < 2)
			return this;
		if(!player.getFrameUpdater().shouldUpdate(string, id))
			return this;
		PacketBuilder bldr = new PacketBuilder(126, PacketType.SHORT);
		bldr.writeString(string);
		bldr.writeShortA(id);
		player.write(bldr.toPacket());
		return this;
	}

	public PacketSender sendClientRightClickRemoval() {
		sendString(0, "[CLOSEMENU]");
		return this;
	}


	public PacketSender sendShadow() {
		PacketBuilder bldr = new PacketBuilder(29);
		bldr.writeByte(player.getAttributes().getShadowState());
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends the players rights ordinal to the client.
	 * @return	The packetsender instance.
	 */
	public PacketSender sendRights() {
		player.write(new PacketBuilder(127).writeByte(player.getRights().ordinal()).toPacket());
		return this;
	}

	/**
	 * Sends a hint to specified position.
	 * @param position			The position to create the hint.
	 * @param tilePosition		The position on the square (middle = 2; west = 3; east = 4; south = 5; north = 6)
	 * @return					The Packet Sender instance.
	 */
	public PacketSender sendPositionalHint(Position position, int tilePosition) {
		player.write(new PacketBuilder(254).writeByte(tilePosition).writeShort(position.getX()).writeShort(position.getY()).writeByte(position.getZ()).toPacket());
		return this;
	}

	/**
	 * Sends a hint above an entity's head.
	 * @param entity	The target entity to draw hint for.
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendEntityHint(Entity entity) {
		int type = entity instanceof Player ? 10 : 1;
		player.write(new PacketBuilder(254).writeByte(type).writeShort(entity.getIndex()).writeTripleInt(0).toPacket());
		return this;
	}

	/**
	 * Sends a hint removal above an entity's head.
	 * @param playerHintRemoval	Remove hint from a player or an NPC?
	 * @return			The PacketSender instance.
	 */
	public PacketSender sendEntityHintRemoval(boolean playerHintRemoval) {
		int type = playerHintRemoval ? 10 : 1;
		player.write(new PacketBuilder(254).writeByte(type).writeShort(-1).writeTripleInt(0).toPacket());
		return this;
	}

	public PacketSender sendMultiIcon(int value) {
		player.getAttributes().setMultiIcon(value);
		player.write(new PacketBuilder(61).writeByte(value).toPacket());
		return this;
	}

	public PacketSender sendPrivateMessage(long name, PlayerRights rights, byte[] message, int size) {
		player.write(new PacketBuilder(196, PacketType.BYTE).writeLong(name).writeInt(player.getRelations().getPrivateMessageId()).writeByte(rights.ordinal()).writeByteArray(message, 0, size).toPacket());
		return this;
	}

	public PacketSender sendFriendStatus(int status) {
		player.write(new PacketBuilder(221).writeByte(status).toPacket());
		return this;
	}

	public PacketSender sendFriend(long name, int world) {
		world = world != 0 ? world + 9 : world;
		player.write(new PacketBuilder(50).writeLong(name).writeByte(world).toPacket());
		return this;
	}

	public PacketSender sendIgnoreList() {
		PacketBuilder pkt = new PacketBuilder(214, PacketType.BYTE);
		int amount = player.getRelations().getIgnoreList().size();
		pkt.writeString(""+amount);
		for(int i = 0; i < amount; i++)
			pkt.writeString(""+player.getRelations().getIgnoreList().get(i));
		player.write(pkt.toPacket());
		return this;
	}

	public PacketSender sendObjectTransformation(GameObject object) {
		/**
		 * This packet turns a player into an object,
		 * could be used for things such as, ring of stone transformation, fight pits orb viewing, etc.
		 * Some information can be found here: http://www.rune-server.org/runescape-development/rs2-client/help/379637-whats-do-packet-147-a.html
		 */
		PacketBuilder builder = new PacketBuilder(147);
		int x = (player.getPosition().getLocalX() & 0x7) << 4;
		int offset = x + (player.getPosition().getLocalY() & 0x7);
		builder.writeByteS(offset)
		.writeShort(player.getIndex())
		.writeByteS(0)
		.writeLEShort(object.getId())
		.writeByteC(0)
		.writeShort(object.getId() + 1)
		.writeByteS((object.getType() << 2) + (object.getFace() & 3))
		.writeByte(0)
		.writeShort(object.getId())
		.writeByteC(0);
		player.write(builder.toPacket());
		return this;
	}

	public PacketSender sendProjectile(Projectile projectile, Entity lockon) {
		if(lockon != null) {
			if(lockon instanceof Player && ((Player)lockon).getConstitution() <= 0 || lockon instanceof NPC && ((NPC)lockon).getConstitution() <= 0)
				return this;
		}
		final Position position = projectile.getPosition();
		final Position target = projectile.getDestination();
		int offX = (projectile.getPosition().getY() - projectile.getDestination().getY())* -1;
		int offY = (projectile.getPosition().getX() - projectile.getDestination().getX())* -1;		
		player.write(new PacketBuilder(85).writeByteC((position.getY() - (player.getLastKnownRegion().getRegionY() * 8)) - 2).writeByteC((position.getX() - (player.getLastKnownRegion().getRegionX() * 8)) - 3).toPacket());
		player.write(new PacketBuilder(117).
				writeByte(projectile.getAngle()).
				writeByte(offY).
				writeByte(offX).
				writeShort(lockon != null ? (lockon instanceof Player ? -(lockon.getIndex() - 1) : lockon.getIndex() + 1) : 0).
				writeShort(projectile.getGraphic().getId()).
				writeByte(position.getZ()).
				writeByte(target.getZ()).
				writeShort(projectile.getDelay()).
				writeShort(projectile.getSpeed()).
				writeByte(projectile.getSlope()).
				writeByte(64).toPacket());
		return this;
	}

	public PacketSender sendGlobalProjectile(Projectile projectile, Entity lockon) {
		final Position start = player.getPosition();
		final Projectile projectile2 = projectile;
		final Entity lock = lockon;
		for(Player p : Misc.getCombinedPlayerList(player)) {
			if(p == null)
				continue;
			if(Locations.goodDistance(start, p.getPosition(), 15))
				p.getPacketSender().sendProjectile(projectile2, lock);
		}
		return this;
	}

	public PacketSender sendAnimationReset() {
		player.write(new PacketBuilder(1).toPacket());
		return this;
	}

	public PacketSender sendGraphic(Graphic graphic, Position position) {
		sendPosition(position);
		player.write(new PacketBuilder(4).writeByte(0).writeShort(graphic.getId()).writeByte(position.getZ()).writeShort(graphic.getDelay()).toPacket());
		return this;
	}

	public PacketSender sendGlobalGraphic(Graphic graphic, Position position) {
		sendGraphic(graphic, position);
		for(Player p : player.getAttributes().getLocalPlayers()) {
			if(p.getPosition().distanceToPoint(player.getPosition().getX(), player.getPosition().getY()) > 20)
				continue;
			p.getPacketSender().sendGraphic(graphic, position);
		}
		return this;
	}

	public PacketSender sendObject(GameObject object) {
		sendPosition(object.getPosition());
		player.write(new PacketBuilder(151).writeByteA(0).writeLEShort(object.getDefinition().getId()).writeByteS((byte) ((object.getType() << 2) + (object.getFace() & 3))).toPacket());
		return this;
	}

	public void sendObject_cons(int objectX, int objectY, int objectId, int face, int objectType, int height) {
		sendPosition(new Position(objectX, objectY));
		PacketBuilder packet = new PacketBuilder(152);
		if (objectId != -1) // removing
			player.write(packet.writeByteS(0).writeLEShort(objectId).writeByteS((objectType << 2) + (face & 3)).writeByte(height).toPacket());
		if (objectId == -1 || objectId == 0 || objectId == 6951)
			CustomObjects.spawnObject(player, new GameObject(objectId, new Position(objectX, objectY)));

	}

	public PacketSender sendObjectRemoval(GameObject object) {
		sendPosition(object.getPosition());
		player.write(new PacketBuilder(101).writeByteC((object.getType() << 2) + (object.getFace() & 3)).writeByte(object.getPosition().getZ()).toPacket());
		return this;
	}

	public PacketSender sendObjectsRemoval(int chunkX, int chunkY, int height) {
		player.write(new PacketBuilder(153).writeByte(chunkX).writeByte(chunkY).writeByte(height).toPacket());
		return this;
	}

	public PacketSender sendObjectAnimation(GameObject object, Animation anim) {
		sendPosition(object.getPosition());
		player.write(new PacketBuilder(160).writeByteS(0).writeByteS((object.getType() << 2) + (object.getFace() & 3)).writeShortA(anim.getId()).toPacket());
		return this;
	}

	public PacketSender sendGroundItemAmount(Position position, Item item, int amount) {
		sendPosition(position);
		player.write(new PacketBuilder(84).writeByte(0).writeShort(item.getId()).writeShort(item.getAmount()).writeShort(amount).toPacket());
		return this;
	}

	public PacketSender createGroundItem(int itemID, int itemX, int itemY, int itemAmount) {
		sendPosition(new Position(itemX, itemY));
		player.write(new PacketBuilder(44).writeLEShortA(itemID).writeShort(itemAmount).writeByte(0).toPacket());
		return this;
	}

	public PacketSender removeGroundItem(int itemID, int itemX, int itemY, int Amount) {
		sendPosition(new Position(itemX, itemY));
		player.write(new PacketBuilder(156).writeByteA(0).writeShort(itemID).toPacket());
		return this;
	}

	public PacketSender sendRegion(Position position) {
		player.write(new PacketBuilder(64).writeByteC(position.getRegionY()).writeByteS(position.getRegionX()).toPacket());
		return this;
	}

	public PacketSender sendPosition(final Position position) {
		final Position other = player.getLastKnownRegion();
		player.write(new PacketBuilder(85).writeByteC(position.getY() - 8 * other.getRegionY()).writeByteC(position.getX() - 8 * other.getRegionX()).toPacket());
		return this;
	}

	public PacketSender setDrawLoading(boolean drawLoading) {
		player.write(new PacketBuilder(88).writeByte(drawLoading ? 1 : 0).toPacket());
		return this;
	}

	public PacketSender sendConsoleMessage(String message) {
		player.write(new PacketBuilder(123, PacketType.BYTE).writeString(message).toPacket());
		return this;
	}

	public PacketSender sendInterfaceSpriteChange(int childId, int firstSprite, int secondSprite) {
		//	player.write(new PacketBuilder(140).writeShort(childId).writeByte((firstSprite << 0) + (secondSprite & 0x0)).toPacket());
		return this;
	}

	public PacketSender sendNote(String note) {
		player.write(new PacketBuilder(130, PacketType.BYTE).writeString(note).toPacket());
		return this;
	}

	public int getRegionOffset(Position position) {
		int x = position.getX() - (position.getRegionX() << 4);
		int y = position.getY() - (position.getRegionY() & 0x7);
		int offset = ((x & 0x7)) << 4 + (y & 0x7);
		return offset;
	}

	public PacketSender(Player player) {
		this.player = player;
	}

	private Player player;


}
