package org.trident.world.content.quests;

import org.trident.engine.task.Task;
import org.trident.engine.task.TaskManager;
import org.trident.model.Position;
import org.trident.model.RegionInstance;
import org.trident.model.Skill;
import org.trident.model.RegionInstance.RegionInstanceType;
import org.trident.model.definitions.NPCSpawns;
import org.trident.world.World;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.dialogue.DialogueManager;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.npc.NPCData.CustomNPCData;
import org.trident.world.entity.player.Player;

/**
 * @author Gabbe
 */
public class Nomad {

	public static void startFight(final Player p) {
		if(p.getAttributes().getMinigameAttributes().getNomadAttributes().hasFinishedPart(2))
			return;
		p.getPacketSender().sendInterfaceRemoval();
		p.moveTo(new Position(3361, 5856, p.getIndex() * 4));
		p.getAttributes().setRegionInstance(new RegionInstance(p, RegionInstanceType.NOMAD));
		TaskManager.submit(new Task(1, p, false) {
			int tick = 0;
			@Override
			public void execute() {
				if(tick >= 4) {
					NPC n = NPCSpawns.createCustomNPC(CustomNPCData.NOMAD, new Position(3360, 5860, p.getPosition().getZ()));
					n.getCombatAttributes().setSpawnedFor(p);
					CombatHandler.setAttack(n, p);
					World.register(n);
					p.getAttributes().getRegionInstance().getNpcsList().add(n);
					stop();
				}
				tick++;
			}
		});
	}

	public static void endFight(Player p, boolean killed) {
		if(p.getAttributes().getRegionInstance() != null)
			p.getAttributes().getRegionInstance().destruct();
		if(killed) {
			p.setConstitution(p.getSkillManager().getMaxLevel(Skill.CONSTITUTION));
			p.getAttributes().getMinigameAttributes().getNomadAttributes().setPartFinished(2, true);
			DialogueManager.start(p, 324);
		}
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
			if(!p.getAttributes().getMinigameAttributes().getNomadAttributes().hasFinishedPart(questGuideIndex))
				p.getPacketSender().sendString(i, ""+questGuide[questGuideIndex]);
			else
				p.getPacketSender().sendString(i, "@str@"+questGuide[questGuideIndex]+"");
			questGuideIndex++;
		}
		if(p.getAttributes().getMinigameAttributes().getNomadAttributes().hasFinishedPart(2))
			p.getPacketSender().sendString(8147+questIntro.length+questGuide.length, "@dre@Quest complete!");
	}


	private static final String questTitle = "Nomad's Requiem";
	private static final String[] questIntro ={
		"Nomad is searching for a worthy opponent.", 
		"Are you eligible for the job?",
		"",
	};
	private static final String[] questGuide ={
		"Begin the quest by speaking to Nomad.", 
		"Accept his challenge to a fight.",
		"Defeat Nomad."
	};
}
