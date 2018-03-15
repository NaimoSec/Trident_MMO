package org.trident.world.content.skills.impl.runecrafting;

import org.trident.world.entity.player.Player;

public class RunecraftingPouches {

	private static final int RUNE_ESS = 1436, PURE_ESS = 7936;

	public enum Pouch {
		SMALL(7, 7);

		Pouch(int maxRuneEss, int maxPureEss) {
			this.maxRuneEss = maxRuneEss;
			this.maxPureEss = maxPureEss;
		}

		public int maxRuneEss, maxPureEss;
	}

	public static void fill(Player p, Pouch pouch) {
		if(p.getAttributes().getInterfaceId() > 0) {
			p.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			return;
		}
		int rEss = p.getInventory().getAmount(RUNE_ESS);
		int pEss = p.getInventory().getAmount(PURE_ESS);
		if(rEss == 0 && pEss == 0) {
			p.getPacketSender().sendMessage("You do not have any essence in your inventory.");
			return;
		}
		rEss = rEss > pouch.maxRuneEss ? pouch.maxRuneEss : rEss;
		pEss = pEss > pouch.maxPureEss ? pouch.maxPureEss : pEss;
		int stored = 0;
		if(p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredRuneEssence() >= pouch.maxRuneEss)
			p.getPacketSender().sendMessage("Your pouch can not hold any more Rune essence.");
		if(p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredPureEssence() >= pouch.maxPureEss)
			p.getPacketSender().sendMessage("Your pouch can not hold any more Pure essence.");
		while(rEss > 0 && p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredRuneEssence() < pouch.maxRuneEss && p.getInventory().contains(RUNE_ESS)) {
			p.getInventory().delete(RUNE_ESS, 1);
			p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().setStoredRuneEssence(p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredRuneEssence()+1);
			stored++;
		}
		while(pEss > 0 && p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredPureEssence() < pouch.maxPureEss && p.getInventory().contains(PURE_ESS)) {
			p.getInventory().delete(PURE_ESS, 1);
			p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().setStoredPureEssence(p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredPureEssence()+1);
			stored++;
		}
		if(stored > 0)
			p.getPacketSender().sendMessage("You fill your pouch with "+stored+" essence..");
	}
	
	public static void empty(Player p, Pouch pouch) {
		if(p.getAttributes().getInterfaceId() > 0) {
			p.getPacketSender().sendMessage("Please close the interface you have open before doing this.");
			return;
		}
		while(p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredRuneEssence() > 0 && p.getInventory().getFreeSlots() > 0) {
			p.getInventory().add(RUNE_ESS, 1);
			p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().setStoredRuneEssence(p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredRuneEssence()-1);
		}
		while(p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredPureEssence() > 0 && p.getInventory().getFreeSlots() > 0) {
			p.getInventory().add(PURE_ESS, 1);
			p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().setStoredPureEssence(p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredPureEssence()-1);
		}
	}
	
	public static void check(Player p, Pouch pouch) {
		p.getPacketSender().sendMessage("Your pouch currently contains "+p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredRuneEssence()+"/"+pouch.maxRuneEss+" Rune essence and "+p.getSkillManager().getSkillAttributes().getRunecraftingAttributes().getStoredPureEssence()+"/"+pouch.maxPureEss+" Pure essence.");
	}
}
