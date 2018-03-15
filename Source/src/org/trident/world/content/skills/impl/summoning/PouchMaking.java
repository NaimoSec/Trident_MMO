package org.trident.world.content.skills.impl.summoning;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.Item;
import org.trident.model.Skill;
import org.trident.model.inputhandling.impl.EnterAmountToInfuse;
import org.trident.world.content.teleporting.TeleportHandler;
import org.trident.world.entity.player.Player;

public class PouchMaking {

	private static final int SHARD_ID = 18016;
	private static final int POUCH_ID = 12155;

	public static void open(Player player) {
		player.getPacketSender().sendInterface(63471);
		Summoning.sendSummoningLevel(player);
	}

	public static boolean pouchInterface(Player c, int buttonId) {
		final Pouch pouch = Pouch.get(buttonId);
		if(pouch == null)
			return false;
		c.getAdvancedSkills().getSummoning().setPouch(pouch);
		c.getAttributes().setInputHandling(new EnterAmountToInfuse());
		c.getPacketSender().sendEnterAmountPrompt("Enter amount to infuse:");
		return true;
	}

	private static boolean hasRequirements(final Player player, final Pouch pouch) {
		if(pouch == null)
			return false;
		if (player.getSkillManager().getMaxLevel(Skill.SUMMONING) >= pouch.getLevelRequired()) {
			if (player.getInventory().contains(POUCH_ID))  {
				if (player.getInventory().getAmount(SHARD_ID) >= pouch.getShardsRequired()) {
					if (player.getInventory().contains(pouch.getCharmId())) {
						if (player.getInventory().contains(pouch.getsecondIngredientId())) {
							return true;
						} else {
							String msg = new Item(pouch.getsecondIngredientId()).getDefinition().getName().endsWith("s") ? "some" : "a";
							player.getPacketSender().sendMessage("You need "+msg+" "+ new Item(pouch.getsecondIngredientId()).getDefinition().getName() + " for this pouch.");
							return false;
						}
					} else {
						player.getPacketSender().sendMessage("You need a " + new Item(pouch.getCharmId()).getDefinition().getName() + " for this pouch.");
						return false;
					}
				} else {
					player.getPacketSender().sendMessage("You need " + pouch.getShardsRequired() + " Spirit shards to create this pouch.");
					return false;
				}
			} else {
				player.getPacketSender().sendMessage("You need to have an empty pouch to do this.");
				return false;
			}
		} else {
			player.getPacketSender().sendMessage("You need a Summoning level of at least "+ pouch.getLevelRequired() + " to create this pouch");
			return false;
		}
	}

	public static void infusePouches(final Player player, final int amount) {
		final Pouch pouch = player.getAdvancedSkills().getSummoning().getPouch();
		if(pouch == null)
			return;
		if(!hasRequirements(player, pouch))
			return;
		TeleportHandler.cancelCurrentActions(player);
		player.performAnimation(new Animation(725));
		player.performGraphic(new Graphic(1207));
		TaskManager.submit(new Task(2, player, false) {
			@Override
			public void execute() {
				int x = amount;
				while(x > 0) {
					if(!hasRequirements(player, pouch))
						break;
					else {
						player.getInventory().delete(POUCH_ID, 1);
						player.getInventory().delete(SHARD_ID, pouch.getShardsRequired());
						player.getInventory().delete(pouch.getCharmId(), 1);
						player.getInventory().delete(pouch.getsecondIngredientId(), 1);
						player.getSkillManager().addExperience(Skill.SUMMONING, pouch.getExp() * 19, false);
						player.getInventory().add(pouch.getPouchId(), 1);
						x--;
					}
				}
				stop();
			}
		});
	}
}
