package org.trident.net.packet.impl;

import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.Skill;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketListener;
import org.trident.world.World;
import org.trident.world.content.combat.AttackType;
import org.trident.world.content.combat.CombatHandler;
import org.trident.world.content.combat.combatdata.magic.CombatMagicSpells;
import org.trident.world.content.combat.combatdata.magic.CombatSpell;
import org.trident.world.content.combat.combatdata.magic.MagicSpells;
import org.trident.world.entity.player.Player;

public class MagicOnPlayerPacketListener implements PacketListener {

	@SuppressWarnings("incomplete-switch")
	@Override
	public void execute(Player player, Packet packet) {
		int playerIndex = packet.readShortA();
		if(playerIndex < 0 || playerIndex > World.getPlayers().size())
			return;
		int spellId = packet.readLEShort();
		Player target = World.getPlayers().get(playerIndex);
		if(target == null || target.getConstitution() <= 0) {
			player.getMovementQueue().stopMovement();
			return;
		}
		player.setPositionToFace(target.getPosition().copy());
		CombatMagicSpells cbSpell = CombatMagicSpells.getSpell(spellId);
		if(cbSpell == null) {
			player.getMovementQueue().stopMovement();
			MagicSpells nonCbSpell = MagicSpells.forSpellId(spellId);
			if(nonCbSpell != null) {
				if(target.getLocation().isAidingAllowed()) {
					if(!target.getAttributes().isAcceptingAid()) {
						player.getPacketSender().sendMessage("This player is not accepting aid from other players right now.");
						return;
					}
					if(System.currentTimeMillis() - player.getCombatAttributes().getLastAid() < 30000) {
						player.getPacketSender().sendMessage("You can only aid other players every 30 seconds.");
						return;
					}
					switch(nonCbSpell) {
					case VENGEANCE_OTHER:
						if (System.currentTimeMillis() - player.getPlayerCombatAttributes().getLastVengeanceCast() < 30000) {
							player.getPacketSender().sendMessage("You can only cast Vengeance every 30 seconds.");
							return;
						}
						player.getSkillManager().addExperience(Skill.MAGIC, nonCbSpell.getSpell().baseExperience(), false);
						player.performAnimation(new Animation(4411));
						player.getPlayerCombatAttributes().setLastVengeanceCast(System.currentTimeMillis());
						player.getCombatAttributes().setLastAid(System.currentTimeMillis());
						target.performGraphic(new Graphic(725, GraphicHeight.HIGH));
						target.getPlayerCombatAttributes().setVengeance(true);
						break;
					}
				} else
					player.getPacketSender().sendMessage("You cannot aid other players here.");
			}
			return;
		}
		CombatSpell spell = cbSpell.getSpell();
		player.getPlayerCombatAttributes().getMagic().setCurrentSpell(spell);
		player.getCombatAttributes().setAttackType(AttackType.MAGIC);
		CombatHandler.attack(player, target);
	}

}
