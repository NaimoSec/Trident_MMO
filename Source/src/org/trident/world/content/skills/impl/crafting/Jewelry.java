package org.trident.world.content.skills.impl.crafting;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Skill;
import org.trident.model.definitions.ItemDefinition;
import org.trident.util.Misc;
import org.trident.world.content.skills.impl.crafting.JewelryData.amuletData;
import org.trident.world.content.skills.impl.crafting.JewelryData.jewelryData;
import org.trident.world.entity.player.Player;

public class Jewelry {

	public static void jewelryMaking(final Player p, final String type, final int itemId, final int amount) {
		if(p.getAttributes().getInterfaceId() != 4161)
			return;
		switch (type) {
		case "RING":
			for (int i = 0; i < jewelryData.RINGS.item.length; i++) {
				if (itemId == jewelryData.RINGS.item[i][1]) {
					mouldJewelry(p, jewelryData.RINGS.item[i][0], itemId, amount, jewelryData.RINGS.item[i][2], jewelryData.RINGS.item[i][3]);
				}
			}
			break;
		case "NECKLACE":
			for (int i = 0; i < jewelryData.NECKLACE.item.length; i++) {
				if (itemId == jewelryData.NECKLACE.item[i][1]) {
					mouldJewelry(p, jewelryData.NECKLACE.item[i][0], itemId, amount, jewelryData.NECKLACE.item[i][2], jewelryData.NECKLACE.item[i][3]);
				}
			}
			break;
		case "AMULET":
			for (int i = 0; i < jewelryData.AMULETS.item.length; i++) {
				if (itemId == jewelryData.AMULETS.item[i][1]) {
					mouldJewelry(p, jewelryData.AMULETS.item[i][0], itemId, amount, jewelryData.AMULETS.item[i][2], jewelryData.AMULETS.item[i][3]);
				}
			}
			break;
		}
	}

	private static void mouldJewelry(final Player player, final int required, final int itemId, final int amount, final int level, final int xp) {
		player.getSkillManager().stopSkilling();
		if (player.getSkillManager().getCurrentLevel(Skill.CRAFTING) < level) {
			player.getPacketSender().sendMessage("You need a Crafting level of at least "+ level +" to mould this item.");
			return;
		}
		if (!player.getInventory().contains(2357)) {
			player.getPacketSender().sendMessage("You need a Gold bar to mould this item.");
			return;
		}
		if (!player.getInventory().contains(required)) {
			String name = ItemDefinition.forId(required).getName();
			player.getPacketSender().sendMessage("You need "+Misc.anOrA(name)+" "+name+" to mould this item.");
			return;
		}
		player.getPacketSender().sendInterfaceRemoval();
		final String name = ItemDefinition.forId(itemId).getName();
		player.performAnimation(new Animation(899));
		player.getSkillManager().getSkillAttributes().setCurrentTask(new Task(4, player, false) {
			int done = 0;
			@Override
			public void execute() {
				if (player.getInventory().contains(2357) && player.getInventory().contains(required) && done <= amount) {
					player.getInventory().delete(2357, 1).delete(required, 1).add(itemId, 1);
					player.performAnimation(new Animation(899));
					player.getSkillManager().addExperience(Skill.CRAFTING, xp * 23, false);
					done++;
					player.getPacketSender().sendMessage("You make "+Misc.anOrA(name)+" "+name.toLowerCase()+".");
				} else
					stop();
			}
		});
		TaskManager.submit(player.getSkillManager().getSkillAttributes().getCurrentTask());
	}

	public static void showInterface(Player player) {
		player.getPacketSender().sendInterfaceRemoval();
		player.getSkillManager().stopSkilling();
		for (final jewelryData i : jewelryData.values()) {
			/*
			 * Rings
			 */
			if (player.getInventory().contains(1592))
				for (int j = i.item.length - 1; j >= 0; j--) {
					int ite = player.getInventory().contains(jewelryData.RINGS.item[j][0]) ? jewelryData.RINGS.item[j][1] : -1;
					player.getPacketSender().sendItemOnInterface(4233, ite, 1).sendString(4230, "Choose an item to make.").sendInterfaceModel(4229, -1, 0);
					if(ite > 0)
						break;
				}
			else
				player.getPacketSender().sendInterfaceModel(4229, 1592, 120).sendString(4230, "You need a Ring mould to craft rings.").sendItemOnInterface(4233, -1, 1);

			/*
			 * Necklaces
			 */
			if (player.getInventory().contains(1597))
				for (int j = i.item.length - 1; j >= 0; j--) {
					int ite = player.getInventory().contains(jewelryData.NECKLACE.item[j][0]) ? jewelryData.NECKLACE.item[j][1] : -1;
					player.getPacketSender().sendItemOnInterface(4239, ite, 1).sendString(4236, "Choose an item to make.").sendInterfaceModel(4235, -1, 0);
					if(ite > 0)
						break;
				}
			else
				player.getPacketSender().sendInterfaceModel(4235, 1597, 120).sendString(4236, "You need a Necklace mould to craft necklaces.").sendItemOnInterface(4239, -1, 1);

			/*
			 * Amulets
			 */
			if (player.getInventory().contains(1595))
				for (int j = i.item.length - 1; j >= 0; j--) {
					int ite = player.getInventory().contains(jewelryData.AMULETS.item[j][0]) ? jewelryData.AMULETS.item[j][1] : -1;
					player.getPacketSender().sendItemOnInterface(4245, ite, 1).sendString(4242, "Choose an item to make.").sendInterfaceModel(4241, -1, 0);
					if(ite > 0)
						break;
				}
			else 
				player.getPacketSender().sendInterfaceModel(4241, 1595, 120).sendString(4242, "You need an Amulet mould to craft rings.").sendItemOnInterface(4245, -1, 1);
		}
		player.getPacketSender().sendInterface(4161);
	}
	
	public static void stringAmulet(Player p, int amuletId) {
		if(p.getAttributes().getInterfaceId() > 0) {
			p.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			return;
		}
		for (final amuletData a : amuletData.values()) {
			if (amuletId == a.getAmuletId()) {
				p.performAnimation(new Animation(897));
				p.getInventory().delete(1759, 1).delete(amuletId, 1).add(a.getProduct(), 1);
				p.getSkillManager().addExperience(Skill.CRAFTING, 5, false);
				p.getPacketSender().sendMessage("You attach the Ball of Wool to the amulet..");
				break;
			}
		}
	}
}
