package org.trident.world.content.audio;

import org.trident.model.container.impl.Equipment;
import org.trident.model.definitions.ItemDefinition;
import org.trident.model.definitions.NPCDefinition;
import org.trident.util.Misc;
import org.trident.world.content.combat.AttackType;
import org.trident.world.entity.GameCharacter;
import org.trident.world.entity.player.Player;

/**
 * Handles sound effects
 * @author Gabbe
 * Credits to squxx for some sound ids
 */
public class SoundEffects {

	/**
	 * Some normal sound effects that occur ingame.
	 */
	public enum SoundData {
		ROTATING_CANNON(new int[] {941}),
		FIRING_CANNON(new int[] {341}),
		LEVELUP(new int[] {51}),
		DRINK_POTION(new int[] {334}),
		EAT_FOOD(new int[] {317}),
		EQUIP_ITEM(new int[] {319, 320}),
		DROP_ITEM(new int[] {376}),
		PICKUP_ITEM(new int[] {358, 359}),
		SMITH_ITEM(new int[] {464, 468}),
		SMELT_ITEM(new int[] {352}),
		MINE_ITEM(new int[] {429, 431, 432}),
		FLETCH_ITEM(new int[] {375}),
		WOODCUT(new int[] {471, 472, 473}),
		LIGHT_FIRE(new int[] {811}),
		TELEPORT(new int[] {202, 201}),
		ACTIVATE_PRAYER_OR_CURSE(new int[] {433}),
		DEACTIVATE_PRAYER_OR_CURSE(new int[] {435}),
		RUN_OUT_OF_PRAYER_POINTS(new int[] {438}),
		BURY_BONE(new int[] {380});

		SoundData(int[] sounds) {
			this.sounds = sounds;
		}

		private int[] sounds;

		public int[] getSounds() {
			return sounds;
		}

		public int getSound() {
			return sounds[Misc.getRandom(getSounds().length - 1)];
		}
	}

	/**
	 * Sends a sound to the player's Player
	 * @param player	Player to receive the sound
	 * @param data		SoundData containing soundId to send etc.
	 * @param volume	Volume of the sound to send.
	 * @param delay		Delay of the sound before being played after received.
	 */
	public static void sendSoundEffect(Player player, SoundData data, int volume, int delay) {
		player.getPacketSender().sendSound(data.getSound(), volume, delay);
	}

	/**
	 * Sends a sound to the player's Player
	 * @param player	Player to receive the sound
	 * @param soundId		SoundId to send to Player
	 * @param volume	Volume of the sound to send.
	 * @param delay		Delay of the sound before being played after received.
	 */
	public static void sendSoundEffect(Player player, int soundId, int volume, int delay) {
		player.getPacketSender().sendSound(soundId, volume, delay);
	}


	public static void handleLogin(Player player) {
		final int plrVolume = player.getAttributes().getVolume();
		player.getPacketSender().sendMessage("@SETVOLUME: "+plrVolume);
		player.getPacketSender().sendConfig(169, plrVolume);
	}

	/**
	 * COMBAT SOUNDS.
	 */

	public static void handleCombatAttack(GameCharacter attacker, GameCharacter target) {
		if(attacker.isPlayer()) {
			@SuppressWarnings("unused")
			AttackType type = ((Player) attacker).getCombatAttributes().getAttackType();
			SoundEffects.sendSoundEffect(((Player) attacker), SoundEffects.getPlayerAttackSound(((Player) attacker)), 10, 1);
			if(target.isPlayer()) {
				SoundEffects.sendSoundEffect(((Player) attacker), SoundEffects.getPlayerBlockSounds((Player)target), 10, 2);
				SoundEffects.sendSoundEffect(((Player) target), SoundEffects.getPlayerAttackSound((Player)attacker), 10, 1);
				SoundEffects.sendSoundEffect(((Player) target), SoundEffects.getPlayerBlockSounds((Player)target), 10, 2);
			}
		} else if(attacker.isNpc() && target.isPlayer())
			SoundEffects.sendSoundEffect(((Player)target), SoundEffects.getPlayerBlockSounds(((Player)target)), 10, 2);
	}

	public static int getNpcAttackSounds(int NPCID)
	{
		String npc = NPCDefinition.forId(NPCID) == null ? "" : NPCDefinition.forId(NPCID).getName().toLowerCase();
		if (npc.contains("bat")) {
			return 1;
		}
		if (npc.contains("cow")) {
			return 4;
		}
		if (npc.contains("imp"))
		{
			return 11;
		}
		if (npc.contains("rat"))
		{
			return 17;
		}
		if (npc.contains("duck"))
		{
			return 26;
		}
		if (npc.contains("wolf") || npc.contains("bear"))
		{
			return 28;
		}
		if (npc.contains("dragon"))
		{
			return 47;
		}
		if (npc.contains("ghost"))
		{
			return 57;
		}
		if (npc.contains("goblin"))
		{
			return 88;
		}
		if (npc.contains("skeleton") || npc.contains("demon") || npc.contains("ogre") || npc.contains("giant") || npc.contains("tz-") || npc.contains("jad"))
		{
			return 48;
		}
		if (npc.contains("zombie"))
		{
			return 1155;
		}
		if (npc.contains("man") || npc.contains("woman") || npc.contains("monk"))
		{
			return 417;
		}
		return Misc.getRandom(6) > 3 ? 398 : 394;
	}


	public static int getNpcBlockSound(int NPCID)
	{
		String npc = NPCDefinition.forId(NPCID) == null ? "" : NPCDefinition.forId(NPCID).getName().toLowerCase();
		if (npc.contains("bat")) {
			return 7;
		}
		if (npc.contains("cow")) {
			return 5;
		}
		if (npc.contains("imp"))
		{
			return 11;
		}
		if (npc.contains("rat"))
		{
			return 16;
		}
		if (npc.contains("duck"))
		{
			return 24;
		}
		if (npc.contains("wolf") || npc.contains("bear"))
		{
			return 34;
		}
		if (npc.contains("dragon"))
		{
			return 45;
		}
		if (npc.contains("ghost"))
		{
			return 53;
		}
		if (npc.contains("goblin"))
		{
			return 87;
		}
		if (npc.contains("skeleton") || npc.contains("demon") || npc.contains("ogre") || npc.contains("giant") || npc.contains("tz-") || npc.contains("jad"))
		{
			return 1154;
		}
		if (npc.contains("zombie"))
		{
			return 1151;
		}
		if (npc.contains("man") && !npc.contains("woman"))
		{
			return 816;
		}
		if (npc.contains("monk"))
		{
			return 816;
		}

		if (!npc.contains("man") && npc.contains("woman"))
		{
			return 818;
		}
		return 791;
	}

	public static int getNpcDeathSounds(int NPCID)
	{
		String npc = NPCDefinition.forId(NPCID) == null ? "" : NPCDefinition.forId(NPCID).getName().toLowerCase();
		if (npc.contains("bat")) {
			return 7;
		}
		if (npc.contains("cow")) {
			return 3;
		}
		if (npc.contains("imp"))
		{
			return 9;
		}
		if (npc.contains("rat"))
		{
			return 15;
		}
		if (npc.contains("duck"))
		{
			return 25;
		}
		if (npc.contains("wolf") || npc.contains("bear"))
		{
			return 35;
		}
		if (npc.contains("dragon"))
		{
			return 44;
		}
		if (npc.contains("ghost"))
		{
			return 60;
		}
		if (npc.contains("goblin"))
		{
			return 125;
		}
		if (npc.contains("skeleton") || npc.contains("demon") || npc.contains("ogre") || npc.contains("giant") || npc.contains("tz-") || npc.contains("jad"))
		{
			return 70;
		}
		if (npc.contains("zombie"))
		{
			return 1140;
		}
		return 70;

	}


	public static int getPlayerBlockSounds(Player c) {

		int blockSound = 511;

		if (c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 2499 ||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 2501 ||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 2503 ||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 4746 ||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 4757 ||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 10330) {//Dragonhide sound
			blockSound = 24;
		}
		else if (c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 10551 ||//Torso
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 10438) {//3rd age
			blockSound = 32;//Weird sound
		}
		else if (c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 10338 ||//3rd age
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 7399 ||//Enchanted
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 6107 ||//Ghostly
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 4091 ||//Mystic
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 4101 ||//Mystic
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 4111 ||//Mystic
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1035 ||//Zamorak
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 12971) {//Combat
			blockSound = 14;//Robe sound
		}
		else if (c.getEquipment().getItems()[Equipment.SHIELD_SLOT].getId() == 4224) {//Crystal Shield
			blockSound = 30;//Crystal sound
		}
		else if (c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1101 ||//Chains
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1103||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1105||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1107||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1109||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1111||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1113||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1115|| //Plates
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1117||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1119||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1121||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1123||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1125||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1127||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 4720|| //Barrows armour
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 4728||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 4749||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 4712||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 11720||//Godwars armour
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 11724||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 3140||//Dragon
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 2615||//Fancy
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 2653||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 2661||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 2669||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 2623||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 3841||
				c.getEquipment().getItems()[Equipment.BODY_SLOT].getId() == 1127) {//Metal armour sound
			blockSound = 511;
		}
		return blockSound;
	}

	public static int getPlayerAttackSound(Player c)	{

		String wep = ItemDefinition.forId(c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId()).getName().toLowerCase();
		if(wep.contains("bow")) {
			return 370;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4718) {//Dharok's Greataxe
			return 1320;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4734) {//Karil's Crossbow
			return 1081;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4747) {//Torag's Hammers
			return 1330;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4710) {//Ahrim's Staff
			return 2555;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4755) {//Verac's Flail
			return 1323;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4726) {//Guthan's Warspear
			return 2562;
		}

		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 772
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1379
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1381
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1383
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1385
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1387
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1389
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1391
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1393
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1395
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1397
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1399
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1401
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1403
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1405
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1407
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1409
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 9100) { //Staff wack
			return 394;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 839
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 841
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 843
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 845
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 847
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 849
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 851
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 853
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 855
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 857
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 859
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 861
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4734
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 2023 //RuneC'Bow
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4212
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4214
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4934
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 9104
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 9107) { //Bows/Crossbows
			return 370;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1363
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1365
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1367
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1369
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1371
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1373
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1375
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1377
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1349
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1351
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1353
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1355
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1357
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1359
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1361
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 9109) { //BattleAxes/Axes
			return 399;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4718 || c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 7808)
		{ //Dharok GreatAxe
			return 400;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 6609
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1307
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1309
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1311
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1313
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1315
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1317
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1319) { //2h
			return 425;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1321
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1323
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1325
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1327
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1329
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1331
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 1333
				|| c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4587) { //Scimitars
			return 396;
		}
		if (wep.contains("halberd"))
		{
			return 420;
		}
		if (wep.contains("long"))
		{
			return 396;
		}
		if (wep.contains("knife"))
		{
			return 368;
		}
		if (wep.contains("javelin"))
		{
			return 364;
		}

		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 9110) {
			return 401;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4755) {
			return 1059;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4153) {
			return 1079;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 9103) {
			return 385;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == -1) { // fists
			return 417;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 2745 || c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 2746 || c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 2747 || c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 2748) { // Godswords
			return 390;
		}
		if (c.getEquipment().getItems()[Equipment.WEAPON_SLOT].getId() == 4151) {
			return 1080;
		} else {
			return 398; //Daggers(this is enything that isn't added)
		}
	}


	public static int specialSounds(int id)
	{
		if (id == 4151) //whip
		{
			return 1081;
		}
		if (id == 5698) //dds
		{
			return 793;
		}
		if (id == 1434)//Mace
		{
			return 387;
		}
		if (id == 3204) //halberd
		{
			return 420;
		}
		if (id == 4153) //gmaul
		{
			return 1082;
		}
		if (id == 7158) //d2h
		{
			return 426;
		}
		if (id == 4587) //dscim
		{
			return 1305;
		}
		if (id == 1215) //Dragon dag
		{
			return 793;
		}
		if (id == 1305) //D Long
		{
			return 390;
		}
		if (id == 861) //MSB
		{
			return 386;
		}
		if (id == 11235) //DBow
		{
			return 386;
		}
		if (id == 6739) //D Axe
		{
		}
		if (id == 1377) //DBAxe
		{
			return 389;
		}
		return -1;
	}
}
