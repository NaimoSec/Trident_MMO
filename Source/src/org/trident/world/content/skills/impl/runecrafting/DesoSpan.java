package org.trident.world.content.skills.impl.runecrafting;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Animation;
import org.trident.model.CombatIcon;
import org.trident.model.Damage;
import org.trident.model.Graphic;
import org.trident.model.Hit;
import org.trident.model.Hitmask;
import org.trident.model.Position;
import org.trident.model.Skill;
import org.trident.model.definitions.NPCSpawns;
import org.trident.util.Misc;
import org.trident.world.World;
import org.trident.world.content.Following;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCAttributes;
import org.trident.world.entity.npc.custom.CustomNPC;
import org.trident.world.entity.player.Player;

/**
 * Handles the custom DesoSpan area
 * @author Gabbe
 */
public class DesoSpan {

	private static final Animation SIPHONING_ANIMATION = new Animation(9368);
	private static final int ENERGY_FRAGMENT = 13653;

	enum Energy {
		GREEN_ENERGY(8028, 40, 1800, new Graphic(127), new Graphic(551), new Graphic(999)),
		YELLOW_ENERGY(8022, 72, 3244, new Graphic(94), new Graphic(135), new Graphic(1006));

		Energy(int npcId, int levelReq, int experience, Graphic playerGraphic, Graphic projectileGraphic, Graphic npcGraphic) {
			this.npcId = npcId;
			this.levelReq = levelReq;
			this.experience = experience;
			this.playerGraphic = playerGraphic;
			this.projectileGraphic = projectileGraphic;
			this.npcGraphic = npcGraphic;
		}

		public int npcId, levelReq, experience;
		public Graphic playerGraphic, projectileGraphic, npcGraphic;

		static Energy forId(int npc) {
			for(Energy e : Energy.values()) {
				if(e.npcId == npc) 
					return e;
			}
			return null;
		}
	}

	public static void spawnEnergySources() {
		int lastX = 0;
		NPCAttributes attributes;
		for(int i = 0; i < 6; i++) {
			int randomX = 2595 + Misc.getRandom(12);
			if(randomX == lastX || randomX == lastX + 1 || randomX == lastX - 1)
				randomX++;
			int randomY = 4772 + Misc.getRandom(8);
			lastX = randomX;
			attributes = new NPCAttributes().setAttackable(false).setAggressive(false).setRespawn(true).setConstitution(5000);
			World.register(NPCSpawns.createNPC(i <= 3 ? 8028 : 8022, new Position(randomX, randomY), attributes, attributes.copy()));
		}
	}

	public static void siphon(final Player player, final NPC n) {
		final Energy energyType = Energy.forId(n.getId());
		if(energyType != null) {
			player.getSkillManager().stopSkilling();
			if(player.getPosition().equals(n.getPosition()))
				Following.stepAway(player);
			player.setEntityInteraction(n);
			if(player.getSkillManager().getCurrentLevel(Skill.RUNECRAFTING) < energyType.levelReq) {
				player.getPacketSender().sendMessage("You need a Runecrafting level of at least "+energyType.levelReq+" to siphon this energy source.");
				return;
			}
			if(!player.getInventory().contains(ENERGY_FRAGMENT) && player.getInventory().getFreeSlots() == 0) {
				player.getPacketSender().sendMessage("You need some free inventory space to do this.");
				return;
			}
			player.performAnimation(SIPHONING_ANIMATION);
			CustomNPC.fireGlobalProjectile(player, n, energyType.projectileGraphic);
			int cycle = 2 + Misc.getRandom(2);
			player.getSkillManager().getSkillAttributes().setCurrentTask(new Task(cycle, player, false) {
				@Override
				public void execute() {
					if(n.getConstitution() <= 0) {
						player.getPacketSender().sendMessage("This energy source has died out.");
						stop();
						return;
					}
					player.getSkillManager().addExperience(Skill.RUNECRAFTING, energyType.experience - Misc.getRandom(1000), false);
					player.performGraphic(energyType.playerGraphic);
					n.performGraphic(energyType.npcGraphic);
					n.setDamage(new Damage(new Hit(Misc.getRandom(12), CombatIcon.DEFLECT, Hitmask.DARK_RED)));
					if(Misc.getRandom(40) <= 10) {
						player.setDamage(new Damage(new Hit(1 + Misc.getRandom(60), CombatIcon.MAGIC, Hitmask.DARK_RED)));
						player.getPacketSender().sendMessage("You accidently attempt to siphon too much energy, and get hurt.");
					} else {
						player.getPacketSender().sendMessage("You siphon some energy ..");
						player.getInventory().add(ENERGY_FRAGMENT, 1);
					}
					if(n.getConstitution() > 0 && player.getConstitution() > 0)
						siphon(player, n);
					stop();
				}
			});
			TaskManager.submit(player.getSkillManager().getSkillAttributes().getCurrentTask());
		}
	}

}
