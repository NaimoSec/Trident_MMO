package org.trident.world.content.combat.combatdata.bonusdata;

import org.trident.model.Skill;
import org.trident.model.container.impl.Equipment;
import org.trident.world.entity.player.Player;

public class EquipmentEffects {

	public static double getSpiritShieldEffects(Player player, int dmg, double damageReductionMultiplier) {
		if(player.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 13738 || player.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 13740 || player.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 13742) {
			if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) > 0) {
					damageReductionMultiplier -= 0.25;
					int prayerLost = (int) (dmg * 0.18);
					if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) >= prayerLost) {
						player.getSkillManager().setCurrentLevel(Skill.PRAYER, player.getSkillManager().getCurrentLevel(Skill.PRAYER) - prayerLost);
						if(player.getSkillManager().getCurrentLevel(Skill.PRAYER) < 0)
							player.getSkillManager().setCurrentLevel(Skill.PRAYER, 0);
					}
			}
		}
		return damageReductionMultiplier;
	}
}
