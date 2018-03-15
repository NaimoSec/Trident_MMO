package org.trident.world.content.combat.combatdata.magic;

import org.trident.model.Animation;
import org.trident.model.Graphic;
import org.trident.model.GraphicHeight;
import org.trident.model.Item;
import org.trident.model.Projectile;
import org.trident.model.Skill;
import org.trident.util.Misc;
import org.trident.world.content.skills.impl.prayer.CurseHandler;
import org.trident.world.content.skills.impl.prayer.PrayerHandler;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.npc.NPC;
import org.trident.world.entity.player.Player;


/**
 * Holds data for all of the {@link CombatSpell}s that can be cast in game.
 * 
 * @author lare96
 */
public enum CombatMagicSpells {

	/** Normal spellbook spells. */
	WIND_STRIKE(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14221);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2699));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2700, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 29;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 22;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556), new Item(558) };
		}

		@Override
		public int levelRequired() {
			return 1;
		}

		@Override
		public int spellId() {
			return 1152;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	CONFUSE(new CombatEffectSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(716);
		}

		@Override
		public void spellEffect(Player cast, GameCharacter castOn) {
			MagicExtras.weakenSkill(cast, castOn, Skill.ATTACK, 0.95);
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(104, GraphicHeight.MIDDLE);
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(102, GraphicHeight.HIGH);
		}

		@Override
		public int baseExperience() {
			return 37;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(555, 3), new Item(557, 2), new Item(559) };
		}

		@Override
		public int levelRequired() {
			return 3;
		}

		@Override
		public int spellId() {
			return 1153;
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(103));
		}

		@Override
		public boolean damagingSpell() {
			return false;
		}
	}),
	WATER_STRIKE(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14220);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2703));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2708, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 49;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(2701);
		}

		@Override
		public int baseExperience() {
			return 45;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(555), new Item(556), new Item(558) };
		}

		@Override
		public int levelRequired() {
			return 5;
		}

		@Override
		public int spellId() {
			return 1154;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	EARTH_STRIKE(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14222);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2718));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2723, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 69;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(2713);
		}

		@Override
		public int baseExperience() {
			return 67;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 1), new Item(558, 1),
					new Item(557, 2) };
		}

		@Override
		public int levelRequired() {
			return 9;
		}

		@Override
		public int spellId() {
			return 1156;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	WEAKEN(new CombatEffectSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(716);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(106));
		}

		@Override
		public void spellEffect(Player cast, GameCharacter castOn) {
			MagicExtras.weakenSkill(cast, castOn, Skill.STRENGTH, 0.95);
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(107, GraphicHeight.MIDDLE);
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(105);
		}

		@Override
		public int baseExperience() {
			return 80;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(555, 3), new Item(557, 2),
					new Item(559, 1) };
		}

		@Override
		public int levelRequired() {
			return 11;
		}

		@Override
		public int spellId() {
			return 1157;
		}

		@Override
		public boolean damagingSpell() {
			return false;
		}
	}),
	FIRE_STRIKE(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14223);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2729));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2737, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 89;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(2728);
		}

		@Override
		public int baseExperience() {
			return 111;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 1), new Item(558, 1),
					new Item(554, 3) };
		}

		@Override
		public int levelRequired() {
			return 13;
		}

		@Override
		public int spellId() {
			return 1158;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	WIND_BOLT(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14221);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2699));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2700, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 99;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 134;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 2), new Item(562, 1) };
		}

		@Override
		public int levelRequired() {
			return 17;
		}

		@Override
		public int spellId() {
			return 1160;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	CURSE(new CombatEffectSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(710);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(109));
		}

		@Override
		public void spellEffect(Player cast, GameCharacter castOn) {
			MagicExtras.weakenSkill(cast, castOn, Skill.DEFENCE, 0.95);
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(110, GraphicHeight.MIDDLE);
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(108, GraphicHeight.HIGH);
		}

		@Override
		public int baseExperience() {
			return 168;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(555, 2), new Item(557, 3),
					new Item(559, 1) };
		}

		@Override
		public int levelRequired() {
			return 19;
		}

		@Override
		public int spellId() {
			return 1161;
		}

		@Override
		public boolean damagingSpell() {
			return false;
		}
	}),
	BIND(new CombatEffectSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(710);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(178));
		}

		@Override
		public void spellEffect(Player cast, GameCharacter castOn) {
			MagicExtras.freezeTarget(castOn, 5, new Graphic(181, GraphicHeight.HIGH));
		}

		@Override
		public Graphic endGfx() {
			return null;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(177, GraphicHeight.HIGH);
		}

		@Override
		public int baseExperience() {
			return 197;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(555, 3), new Item(557, 3),
					new Item(561, 2) };
		}

		@Override
		public int levelRequired() {
			return 20;
		}

		@Override
		public int spellId() {
			return 1572;
		}

		@Override
		public boolean damagingSpell() {
			return false;
		}
	}),
	WATER_BOLT(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14220);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2704));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2709, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 109;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(2702);
		}

		@Override
		public int baseExperience() {
			return 239;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 2), new Item(562, 1),
					new Item(555, 2) };
		}

		@Override
		public int levelRequired() {
			return 23;
		}

		@Override
		public int spellId() {
			return 1163;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	EARTH_BOLT(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14222);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2719));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2724, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 119;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(2714);
		}

		@Override
		public int baseExperience() {
			return 273;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 2), new Item(562, 1),
					new Item(557, 3) };
		}

		@Override
		public int levelRequired() {
			return 29;
		}

		@Override
		public int spellId() {
			return 1166;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	FIRE_BOLT(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14223);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2731));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2738, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 129;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(2728);
		}

		@Override
		public int baseExperience() {
			return 321;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 3), new Item(562, 1),
					new Item(554, 4) };
		}

		@Override
		public int levelRequired() {
			return 35;
		}

		@Override
		public int spellId() {
			return 1169;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	CRUMBLE_UNDEAD(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(724);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(146));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(147, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 159;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(145, GraphicHeight.HIGH);
		}

		@Override
		public int baseExperience() {
			return 377;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 2), new Item(562, 1),
					new Item(557, 2) };
		}

		@Override
		public int levelRequired() {
			return 39;
		}

		@Override
		public int spellId() {
			return 1171;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	WIND_BLAST(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14221);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2699));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2700, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 139;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 412;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 3), new Item(560, 1) };
		}

		@Override
		public int levelRequired() {
			return 41;
		}

		@Override
		public int spellId() {
			return 1172;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	WATER_BLAST(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14220);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2705));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2710, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 149;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(2702);
		}

		@Override
		public int baseExperience() {
			return 482;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(555, 3), new Item(556, 3),
					new Item(560, 1) };
		}

		@Override
		public int levelRequired() {
			return 47;
		}

		@Override
		public int spellId() {
			return 1175;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	IBAN_BLAST(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(708);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(88));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(89, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 259;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(87);
		}

		@Override
		public int baseExperience() {
			return 562;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return new Item[] { new Item(1409) };
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(560, 1), new Item(554, 5) };
		}

		@Override
		public int levelRequired() {
			return 50;
		}

		@Override
		public int spellId() {
			return 1539;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	SNARE(new CombatEffectSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(710);
		}
		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(178));
		}

		@Override
		public void spellEffect(Player cast, GameCharacter castOn) {
			MagicExtras.freezeTarget(castOn, 10, new Graphic(181, GraphicHeight.HIGH));
		}

		@Override
		public Graphic endGfx() {
			return null;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(177, GraphicHeight.HIGH);
		}

		@Override
		public int baseExperience() {
			return 612;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(555, 3), new Item(557, 4),
					new Item(561, 3) };
		}

		@Override
		public int levelRequired() {
			return 50;
		}

		@Override
		public int spellId() {
			return 1582;
		}
		@Override
		public boolean damagingSpell() {
			return false;
		}
	}),
	MAGIC_DART(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(1575);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(328));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(329, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 199;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(327, GraphicHeight.HIGH);
		}

		@Override
		public int baseExperience() {
			return 677;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return new Item[] { new Item(4170) };
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(558, 4), new Item(560, 1) };
		}

		@Override
		public int levelRequired() {
			return 50;
		}

		@Override
		public int spellId() {
			return 12037;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	EARTH_BLAST(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14222);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2720));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2725, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 159;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(2715, GraphicHeight.MIDDLE);
		}

		@Override
		public int baseExperience() {
			return 712;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 3), new Item(560, 1),
					new Item(557, 4) };
		}

		@Override
		public int levelRequired() {
			return 53;
		}

		@Override
		public int spellId() {
			return 1177;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	FIRE_BLAST(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14223);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2733));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2739, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 169;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(2728);
		}

		@Override
		public int baseExperience() {
			return 803;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 4), new Item(560, 1),
					new Item(554, 5) };
		}

		@Override
		public int levelRequired() {
			return 59;
		}

		@Override
		public int spellId() {
			return 1181;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	SARADOMIN_STRIKE(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(811);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return null;
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(76);
		}

		@Override
		public int maximumStrength() {
			return 209;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 787;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return new Item[] { new Item(2415) };
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 4), new Item(565, 2),
					new Item(554, 2) };
		}

		@Override
		public int levelRequired() {
			return 60;
		}

		@Override
		public int spellId() {
			return 1190;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	CLAWS_OF_GUTHIX(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(811);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return null;
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(77, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 209;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 787;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return new Item[] { new Item(2416) };
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 4), new Item(565, 2),
					new Item(554, 2) };
		}

		@Override
		public int levelRequired() {
			return 60;
		}

		@Override
		public int spellId() {
			return 1191;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	FLAMES_OF_ZAMORAK(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(811);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return null;
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(78);
		}

		@Override
		public int maximumStrength() {
			return 209;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 787;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return new Item[] { new Item(2417) };
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 4), new Item(565, 2),
					new Item(554, 2) };
		}

		@Override
		public int levelRequired() {
			return 60;
		}

		@Override
		public int spellId() {
			return 1192;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	WIND_WAVE(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14221);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2699));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2700, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 179;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 954;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 5), new Item(565, 1) };
		}

		@Override
		public int levelRequired() {
			return 62;
		}

		@Override
		public int spellId() {
			return 1183;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	WATER_WAVE(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14220);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2706));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2711, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 189;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(2702);
		}

		@Override
		public int baseExperience() {
			return 1101;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 5), new Item(565, 1),
					new Item(555, 7) };
		}

		@Override
		public int levelRequired() {
			return 65;
		}

		@Override
		public int spellId() {
			return 1185;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	VULNERABILITY(new CombatEffectSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(729);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(168));
		}

		@Override
		public void spellEffect(Player cast, GameCharacter castOn) {
			MagicExtras.weakenSkill(cast, castOn, Skill.DEFENCE, 0.90);
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(169, GraphicHeight.MIDDLE);
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(167, GraphicHeight.HIGH);
		}

		@Override
		public int baseExperience() {
			return 1234;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(557, 5), new Item(555, 5),
					new Item(566, 1) };
		}

		@Override
		public int levelRequired() {
			return 66;
		}

		@Override
		public int spellId() {
			return 1542;
		}

		@Override
		public boolean damagingSpell() {
			return false;
		}
	}),
	EARTH_WAVE(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14222);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2721));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2726, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 199;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(2715);
		}

		@Override
		public int baseExperience() {
			return 1345;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 5), new Item(565, 1),
					new Item(557, 7) };
		}

		@Override
		public int levelRequired() {
			return 70;
		}

		@Override
		public int spellId() {
			return 1188;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	ENFEEBLE(new CombatEffectSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(729);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(171));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(172, GraphicHeight.MIDDLE);
		}

		@Override
		public void spellEffect(Player cast, GameCharacter castOn) {
			MagicExtras.weakenSkill(cast, castOn, Skill.STRENGTH, 0.90);
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(170, GraphicHeight.MIDDLE);
		}

		@Override
		public int baseExperience() {
			return 1456;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(557, 8), new Item(555, 8),
					new Item(566, 1) };
		}

		@Override
		public int levelRequired() {
			return 73;
		}

		@Override
		public int spellId() {
			return 1543;
		}
		@Override
		public boolean damagingSpell() {
			return false;
		}
	}),
	FIRE_WAVE(new CombatFightSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(14223);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(2735));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(2740, GraphicHeight.MIDDLE);
		}

		@Override
		public int maximumStrength() {
			return 209;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(2728);
		}

		@Override
		public int baseExperience() {
			return 1450;
		}

		@Override
		public Item[] equipmentRequired(Player player) {
			return null;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 5), new Item(565, 1),
					new Item(554, 7) };
		}

		@Override
		public int levelRequired() {
			return 75;
		}

		@Override
		public int spellId() {
			return 1189;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	ENTANGLE(new CombatEffectSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(710);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(178));
		}

		@Override
		public void spellEffect(Player cast, GameCharacter castOn) {
			MagicExtras.freezeTarget(castOn, 15, null);
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(179, GraphicHeight.HIGH);
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(177, GraphicHeight.HIGH);
		}

		@Override
		public int baseExperience() {
			return 1517;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(555, 5), new Item(557, 5),
					new Item(561, 4) };
		}

		@Override
		public int levelRequired() {
			return 79;
		}

		@Override
		public int spellId() {
			return 1592;
		}

		@Override
		public boolean damagingSpell() {
			return false;
		}
	}),
	STUN(new CombatEffectSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(729);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(174));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(107, GraphicHeight.MIDDLE);
		}

		@Override
		public void spellEffect(Player cast, GameCharacter castOn) {
			MagicExtras.weakenSkill(cast, castOn, Skill.ATTACK, 0.90);
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(173, GraphicHeight.MIDDLE);
		}

		@Override
		public int baseExperience() {
			return 1627;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(557, 12), new Item(555, 12),
					new Item(556, 1) };
		}

		@Override
		public int levelRequired() {
			return 80;
		}

		@Override
		public int spellId() {
			return 1562;
		}

		@Override
		public boolean damagingSpell() {
			return false;
		}
	}),
	TELEBLOCK(new CombatEffectSpell() {
		@Override
		public Animation castAnimation() {
			return new Animation(10503);
		}

		@Override
		public void spellEffect(Player cast, GameCharacter castOn) {
			boolean hasPrayers = castOn.getPrayerActive()[PrayerHandler.PROTECT_FROM_MAGIC] || castOn.getCurseActive()[CurseHandler.DEFLECT_MAGIC];
			final int delay = !hasPrayers ? 180 : 100 + Misc.getRandom(50);
			if(castOn.getCombatAttributes().getTeleportBlockDelay() > 0) {
				cast.getPacketSender().sendMessage("That target is already teleport blocked!");
				return;
			}
			castOn.getCombatAttributes().setTeleportBlockDelay(delay);
			if(castOn.isPlayer())
				((Player)castOn).getPacketSender().sendMessage("@red@A teleport block spell has been cast on you!");
			MagicExtras.teleportBlockDecrease(castOn);
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(1843);
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(1841);
		}

		@Override
		public int baseExperience() {
			return 1850;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(563, 1), new Item(562, 1),
					new Item(560, 1) };
		}

		@Override
		public int levelRequired() {
			return 85;
		}

		@Override
		public int spellId() {
			return 12445;
		}

		@Override
		public Projectile castProjectile(GameCharacter cast,
				GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(1842));
		}

		@Override
		public boolean damagingSpell() {
			return false;
		}
	}),
	SMOKE_RUSH(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {

		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1978);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast,
				GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(384));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(385);
		}

		@Override
		public int maximumStrength() {
			return 139;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 565;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 1), new Item(554, 1),
					new Item(562, 2), new Item(560, 2) };
		}

		@Override
		public int levelRequired() {
			return 50;
		}

		@Override
		public int spellId() {
			return 12939;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	SHADOW_RUSH(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {

		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1978);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast,
				GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(378));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(379);
		}

		@Override
		public int maximumStrength() {
			return 149;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 624;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 1), new Item(566, 1),
					new Item(562, 2), new Item(560, 2) };
		}

		@Override
		public int levelRequired() {
			return 52;
		}

		@Override
		public int spellId() {
			return 12987;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	BLOOD_RUSH(new CombatAncientSpell() {

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1978);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(372));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(373);
		}

		@Override
		public int maximumStrength() {
			return 159;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 702;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(565, 1), new Item(562, 2),
					new Item(560, 2) };
		}

		@Override
		public int levelRequired() {
			return 56;
		}

		@Override
		public int spellId() {
			return 12901;
		}

		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn,
				int damageInflicted) {
			if (damageInflicted <= 0)
				return;
			int maxHealth = cast.isPlayer() ? ((Player)cast).getSkillManager().getMaxLevel(Skill.CONSTITUTION) : ((NPC)cast).getDefaultAttributes().getConstitution();
			int toHeal = cast.getConstitution() + (int) (damageInflicted * 0.25);
			if(toHeal > maxHealth)
				toHeal = maxHealth;
			cast.setConstitution(toHeal);
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	ICE_RUSH(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {
			if(Misc.getRandom(10) <= 6) {
				if(castOn.isPlayer())
					MagicExtras.freezeTarget(castOn, 5, null);
				else
					MagicExtras.freezeTarget(castOn, 10, null);
			}
		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1978);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(360));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(361);
		}

		@Override
		public int maximumStrength() {
			return 189;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 766;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(555, 2), new Item(562, 2),
					new Item(560, 2) };
		}

		@Override
		public int levelRequired() {
			return 58;
		}

		@Override
		public int spellId() {
			return 12861;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	SMOKE_BURST(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {

		}

		@Override
		public int spellRadius() {
			return 1;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1979);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return null;
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(389);
		}

		@Override
		public int maximumStrength() {
			return 139;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 854;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 2), new Item(554, 2),
					new Item(562, 4), new Item(560, 2) };
		}

		@Override
		public int levelRequired() {
			return 62;
		}

		@Override
		public int spellId() {
			return 12963;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	SHADOW_BURST(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;
				int currentAttackLevel = player.getSkillManager().getCurrentLevel(Skill.ATTACK);
				if (currentAttackLevel < player.getSkillManager().getMaxLevel(Skill.ATTACK))
					return;
				player.getSkillManager().setCurrentLevel(Skill.ATTACK, currentAttackLevel - ((int) (0.1 * currentAttackLevel)));
			}
		}

		@Override
		public int spellRadius() {
			return 1;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1979);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return null;
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(382);
		}

		@Override
		public int maximumStrength() {
			return 189;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 955;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 1), new Item(566, 2),
					new Item(562, 4), new Item(560, 2) };
		}

		@Override
		public int levelRequired() {
			return 64;
		}

		@Override
		public int spellId() {
			return 13011;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	BLOOD_BURST(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {
			if (damageInflicted < 1) {
				return;
			}
			int maxHealth = cast.isPlayer() ? ((Player)cast).getSkillManager().getMaxLevel(Skill.CONSTITUTION) : ((NPC)cast).getDefaultAttributes().getConstitution();
			int toHeal = cast.getConstitution() + (int) (damageInflicted * 0.25);
			if(toHeal > maxHealth)
				toHeal = maxHealth;
			cast.setConstitution(toHeal);
		}

		@Override
		public int spellRadius() {
			return 1;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1979);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return null;
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(376);
		}

		@Override
		public int maximumStrength() {
			return 219;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 1011;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(565, 2), new Item(562, 4),
					new Item(560, 2) };
		}

		@Override
		public int levelRequired() {
			return 68;
		}

		@Override
		public int spellId() {
			return 12919;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	ICE_BURST(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {
			MagicExtras.freezeTarget(castOn, 10, new Graphic(181, GraphicHeight.HIGH));
		}

		@Override
		public int spellRadius() {
			return 2;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1979);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return null;
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(363);
		}

		@Override
		public int maximumStrength() {
			return 229;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 1237;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(555, 4), new Item(562, 4),
					new Item(560, 2) };
		}

		@Override
		public int levelRequired() {
			return 70;
		}

		@Override
		public int spellId() {
			return 12881;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	SMOKE_BLITZ(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {

		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1978);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(386));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(387);
		}

		@Override
		public int maximumStrength() {
			return 239;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 1345;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 2), new Item(554, 2),
					new Item(565, 2), new Item(560, 2) };
		}

		@Override
		public int levelRequired() {
			return 74;
		}

		@Override
		public int spellId() {
			return 12951;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	SHADOW_BLITZ(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;
				int currentAttackLevel = player.getSkillManager().getCurrentLevel(Skill.ATTACK);
				if (currentAttackLevel < player.getSkillManager().getMaxLevel(Skill.ATTACK))
					return;
				player.getSkillManager().setCurrentLevel(Skill.ATTACK, currentAttackLevel - ((int) (0.15 * currentAttackLevel)));
			}
		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1978);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(380));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(381);
		}

		@Override
		public int maximumStrength() {
			return 249;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 1456;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 2), new Item(566, 2),
					new Item(565, 2), new Item(560, 2) };
		}

		@Override
		public int levelRequired() {
			return 76;
		}

		@Override
		public int spellId() {
			return 12999;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	BLOOD_BLITZ(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {
			if (damageInflicted <= 0)
				return;
			int maxHealth = cast.isPlayer() ? ((Player)cast).getSkillManager().getMaxLevel(Skill.CONSTITUTION) : ((NPC)cast).getDefaultAttributes().getConstitution();
			int toHeal = cast.getConstitution() + (int) (damageInflicted * 0.25);
			if(toHeal > maxHealth)
				toHeal = maxHealth;
			cast.setConstitution(toHeal);
		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1978);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return new Projectile(cast.getPosition(), castOn.getPosition(), new Graphic(374));
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(375);
		}

		@Override
		public int maximumStrength() {
			return 259;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 1652;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(565, 4), new Item(560, 2) };
		}

		@Override
		public int levelRequired() {
			return 80;
		}

		@Override
		public int spellId() {
			return 12911;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	ICE_BLITZ(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {
			MagicExtras.freezeTarget(castOn, 15, null);
		}

		@Override
		public int spellRadius() {
			return 0;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1978);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return null;
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(367);
		}

		@Override
		public int maximumStrength() {
			return 269;
		}

		@Override
		public Graphic startGfx() {
			return new Graphic(366, 6553600);
		}

		@Override
		public int baseExperience() {
			return 1782;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(555, 3), new Item(565, 2),
					new Item(560, 2) };
		}

		@Override
		public int levelRequired() {
			return 82;
		}

		@Override
		public int spellId() {
			return 12871;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	SMOKE_BARRAGE(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {
		}

		@Override
		public int spellRadius() {
			return 2;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1979);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return null;
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(391);
		}

		@Override
		public int maximumStrength() {
			return 279;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 2103;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 4), new Item(554, 4),
					new Item(565, 2), new Item(560, 4) };
		}

		@Override
		public int levelRequired() {
			return 86;
		}

		@Override
		public int spellId() {
			return 12975;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	SHADOW_BARRAGE(new CombatAncientSpell() {
		@Override
		public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {
			if (castOn.isPlayer()) {
				Player player = (Player) castOn;
				int currentAttackLevel = player.getSkillManager().getCurrentLevel(Skill.ATTACK);
				if (currentAttackLevel < player.getSkillManager().getMaxLevel(Skill.ATTACK))
					return;
				player.getSkillManager().setCurrentLevel(Skill.ATTACK, currentAttackLevel - ((int) (0.16 * currentAttackLevel)));
			}
		}

		@Override
		public int spellRadius() {
			return 2;
		}

		@Override
		public Animation castAnimation() {
			return new Animation(1979);
		}

		@Override
		public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
			return null;
		}

		@Override
		public Graphic endGfx() {
			return new Graphic(383);
		}

		@Override
		public int maximumStrength() {
			return 289;
		}

		@Override
		public Graphic startGfx() {
			return null;
		}

		@Override
		public int baseExperience() {
			return 2624;
		}

		@Override
		public Item[] itemsRequired(Player player) {
			return new Item[] { new Item(556, 4), new Item(566, 3),
					new Item(565, 2), new Item(560, 4) };
		}

		@Override
		public int levelRequired() {
			return 88;
		}

		@Override
		public int spellId() {
			return 13023;
		}

		@Override
		public boolean damagingSpell() {
			return true;
		}
	}),
	BLOOD_BARRAGE(new CombatAncientSpell() {
        @Override
        public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {
			if (damageInflicted <= 0)
				return;
			int maxHealth = cast.isPlayer() ? ((Player)cast).getSkillManager().getMaxLevel(Skill.CONSTITUTION) : ((NPC)cast).getDefaultAttributes().getConstitution();
			int toHeal = cast.getConstitution() + (int) (damageInflicted * 0.25);
			if(toHeal > maxHealth)
				toHeal = maxHealth;
			cast.setConstitution(toHeal);
        }

        @Override
        public int spellRadius() {
            return 2;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1979);
        }

        @Override
        public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
            return null;
        }

        @Override
        public Graphic endGfx() {
            return new Graphic(377);
        }

        @Override
        public int maximumStrength() {
            return 299;
        }

        @Override
        public Graphic startGfx() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 3100;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(560, 4), new Item(566, 1),
                    new Item(565, 4) };
        }

        @Override
        public int levelRequired() {
            return 92;
        }

        @Override
        public int spellId() {
            return 12929;
        }

		@Override
		public boolean damagingSpell() {
			return true;
		}
    }),
    ICE_BARRAGE(new CombatAncientSpell() {
        @Override
        public void spellEffect(GameCharacter cast, GameCharacter castOn, int damageInflicted) {
        	boolean createOrb = castOn.getCombatAttributes().getFreezeDelay() > 0;
        	MagicExtras.freezeTarget(castOn, 20, null);
        	if(createOrb)
        		castOn.performGraphic(new Graphic(1677, GraphicHeight.MIDDLE));
        	else
        		castOn.performGraphic(new Graphic(369));
        }

        @Override
        public int spellRadius() {
            return 2;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1979);
        }

        @Override
        public Projectile castProjectile(GameCharacter cast, GameCharacter castOn) {
            return null;
        }

        @Override
        public Graphic endGfx() {
            return null;
        }

        @Override
        public int maximumStrength() {
            return 329;
        }

        @Override
        public Graphic startGfx() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 52;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 6), new Item(565, 2),
                    new Item(560, 4) };
        }

        @Override
        public int levelRequired() {
            return 94;
        }

        @Override
        public int spellId() {
            return 12891;
        }

		@Override
		public boolean damagingSpell() {
			return true;
		}
    });

	/** The combat spell that can be casted. */
	private CombatSpell spell;

	/** 
	 * Create a new {@link CombatMagicSpells}.
	 * 
	 * @param spell
	 *            the combat spell that can be casted.
	 */
	private CombatMagicSpells(CombatSpell spell) {
		this.spell = spell;
	}

	/**
	 * Gets the combat spell that can be casted.
	 * 
	 * @return the combat spell that can be casted.
	 */
	public CombatSpell getSpell() {
		return spell;
	}

	/**
	 * Gets the spell constant by its spell id.
	 * 
	 * @param spellId
	 *            the spell to retrieve.
	 * @return the spell constant with that spell id.
	 */
	public static CombatMagicSpells getSpell(int spellId) {
		for (CombatMagicSpells spell : CombatMagicSpells.values()) {
			if (spell.getSpell() == null)
				continue;
			if (spell.getSpell().spellId() == spellId)
				return spell;
		}
		return null;
	}
}
