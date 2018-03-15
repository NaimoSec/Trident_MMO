
public final class NPCDef {

	public int frontLight = 68;
	public int backLight = 820;
	public int rightLight = 0;
	public int middleLight = -1; // Cannot be 0
	public int leftLight = 0;

	public static NPCDef forID(int i) {
		for (int j = 0; j < 20; j++)
			if (cache[j].type == (long) i)
				return cache[j];
		cacheIndex = (cacheIndex + 1) % 20;
		NPCDef npc = cache[cacheIndex] = new NPCDef();
		if (i >= streamIndices.length)
			return null;
		stream.currentOffset = streamIndices[i];
		npc.type = i;
		npc.readValues(stream);
		if(npc.name != null && npc.name.toLowerCase().contains("bank")) {
			if(npc.actions != null) {
				for(int l = 0; l < npc.actions.length; l++) {
					if(npc.actions[l] != null && npc.actions[l].equalsIgnoreCase("Collect"))
						npc.actions[l] = null;
				}
			}
		}
		switch (i) {
		case 8032:
			npc.actions = new String[] {"Talk-to", null, "Trade", null, null};
			break;
		case 8022:
		case 8028:
			String color = i == 8022 ? "Yellow" : "Green";
			npc.name = ""+color+" energy source";
			npc.actions = new String[] {"Siphon", null, null, null, null};
			break;
		case 534:
			npc.name = "Fairy";
			npc.actions = new String[] {"Talk-to", null, "Trade", "Change difficulty", null};
			break;
		case 1265:
		case 1267:
		case 8459:
			npc.drawMinimapDot = true;
			break;
		case 1382:
			npc.name = "Glacor";
			npc.npcModels = new int[]{58940};
			npc.squaresNeeded = 3;
			npc.sizeY = 200;
			npc.actions = new String[]{null, "Attack", null, null, null};
			npc.combatLevel = 475;
			npc.sizeXZ = 123;
			npc.hasRenderPriority = true;
			break;
		case 6715:
			npc.combatLevel = 20;
			break;
		case 6716:
			npc.combatLevel = 40;
			break;
		case 6727:
			npc.combatLevel = 64;
			break;
		case 6689:
			npc.combatLevel = 120;
			break;
		case 2579:
			npc.name = "Max";
			npc.description = "He has mastered every skill on Voxyde..";
			npc.combatLevel = 138;
			npc.actions = new String[5];
			npc.actions[0] = "Talk-to";
			npc.npcModels = new int[7];
			npc.npcModels[0] = 65291;
			npc.npcModels[1] = 62746;
			npc.npcModels[2] = 62743;
			npc.npcModels[3] = 65322;
			npc.npcModels[4] = 13319;
			npc.npcModels[5] = 27738;
			npc.npcModels[6] = 20147;
			npc.standAnim = 808;
			npc.walkAnim = 819;
			npc.npcHeadModels = NPCDef.forID(517).npcHeadModels;
			break;
		case 1383:
			npc.name = "Unstable glacyte";
			npc.npcModels = new int[]{58942};
			npc.actions = new String[]{null, "Attack", null, null, null};
			npc.combatLevel = 101;
			npc.hasRenderPriority = false;
			break;
		case 1384:
			npc.name = "Sapping glacyte";
			npc.npcModels = new int[]{58939};
			npc.actions = new String[]{null, "Attack", null, null, null};
			npc.combatLevel = 101;
			npc.hasRenderPriority = true;
			break;
		case 1385:
			npc.name = "Enduring glacyte";
			npc.npcModels = new int[]{58937};
			npc.actions = new String[]{null, "Attack", null, null, null};
			npc.combatLevel = 101;
			npc.hasRenderPriority = true;
			break;
		case 705:
			npc.actions = new String[] {"Talk-to", null, "Buy Armour", "Buy Weapons", "Buy Jewelries"};
			npc.name = "Warrior";
			break;
		case 1861:
			npc.actions = new String[] {"Talk-to", null, "Buy Equipment", "Buy Ammunition", null};
			npc.name = "Archer";
			break;
		case 946:
			npc.actions = new String[] {"Talk-to", null, "Buy Equipment", "Buy Runes", null};
			npc.name = "Mage";
			break;
		case 548:
			npc.actions = new String[] {"Talk-to", null, "Buy Clothes", null, null};
			break;
		case 7969:
			npc.actions = new String[] {"Talk-to", null, "Trade", "Achievement Rewards", null};
			break;
		case 316:
		case 309:
		case 312:
		case 313:
			npc.npcModels = new int[]{36844};
			break;
		case 547:
		case 970:
			npc.actions = new String[] {"Talk-to", null, "Trade", null, null};
			break;
		case 961:
			npc.actions = new String[] {"Talk-to", null, "Buy Consumables", "Restore Stats", null};
			npc.name = "Healer";
			break;
		case 4289:
			npc.name = "Kamfreena";
			npc.actions = new String[] {"Talk-to", null, null, null, null};
			npc.squaresNeeded = 1;
			npc.combatLevel = 0;
			npc.standAnim = 2065;
			npc.walkAnim = 2064;
			npc.npcModels = new int[] {391, 414, 468, 481, 344, 353, 546, 15413, 5034, 3331};
			break;
		case 650:
		case 8591:
		case 4247:
			npc.actions = new String[] {"Talk-to", null, "Trade", null, null};
			break;
		case 2192:
			npc.actions = new String[] {"Talk-to", null, "Conquest Shop", null, null};
			break;
		case 13447:
			npc.runAnim = 6321;
			break;
		case 13448:
			npc.headIcon = 9;
			break;
		case 13449:
			npc.headIcon = 16;
			break;
		case 13450:
			npc.headIcon = 17;
			break;
		case 6203:
			npc.npcModels = new int[] {27768, 27773, 27764, 27765, 27770};
			break;
		case 6222:
			npc.sizeXZ = 110;
			npc.sizeY = 110;
			break;
		}
		return npc;
	}

	public Model getHeadModel() {
		if (childrenIDs != null) {
			NPCDef altered = getAlteredNPCDef();
			if (altered == null)
				return null;
			else
				return altered.getHeadModel();
		}
		if (npcHeadModels == null)
			return null;
		boolean everyFetched = false;
		for (int i = 0; i < npcHeadModels.length; i++)
			if (!Model.modelIsFetched(npcHeadModels[i]))
				everyFetched = true;
		if (everyFetched)
			return null;
		Model parts[] = new Model[npcHeadModels.length];
		for (int j = 0; j < npcHeadModels.length; j++)
			parts[j] = Model.fetchModel(npcHeadModels[j]);
		Model completeModel;
		if (parts.length == 1)
			completeModel = parts[0];
		else
			completeModel = new Model(parts.length, parts);
		if (originalColours != null) {
			for (int k = 0; k < originalColours.length; k++)
				completeModel.recolour(originalColours[k], destColours[k]);
		}
		return completeModel;
	}

	public NPCDef getAlteredNPCDef() {
		try {
			int j = -1;
			if (varbitId != -1) {
				VarBit varBit = VarBit.cache[varbitId];
				int k = varBit.configId;
				int l = varBit.leastSignificantBit;
				int i1 = varBit.mostSignificantBit;
				int j1 = Client.anIntArray1232[i1 - l];
				j = clientInstance.variousSettings[k] >> l & j1;
			} else if (varSettingsId != -1) {
				j = clientInstance.variousSettings[varSettingsId];
			}
			if (j < 0 || j >= childrenIDs.length || childrenIDs[j] == -1) {
				return null;
			} else {
				return forID(childrenIDs[j]);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static int NPCAMOUNT = 11599;

	public static void unpackConfig(CacheArchive streamLoader) {
		stream = new Stream(streamLoader.getDataForName("npc.dat"));
		Stream stream2 = new Stream(streamLoader.getDataForName("npc.idx"));
		int totalNPCs = stream2.readUnsignedWord();
		streamIndices = new int[totalNPCs];
		int i = 2;
		for (int j = 0; j < totalNPCs; j++) {
			streamIndices[j] = i;
			i += stream2.readUnsignedWord();
		}
		cache = new NPCDef[20];
		for (int k = 0; k < 20; k++)
			cache[k] = new NPCDef();
		//NPCDefThing2.initialize();
	}

	public static void nullLoader() {
		modelCache = null;
		streamIndices = null;
		cache = null;
		stream = null;
	}

	public Model getAnimatedModel(int j, int k, int ai[]) {
		if (childrenIDs != null) {
			NPCDef entityDef = getAlteredNPCDef();
			if (entityDef == null)
				return null;
			else
				return entityDef.getAnimatedModel(j, k, ai);
		}
		Model completedModel = (Model) modelCache.get(type);
		if (completedModel == null) {
			boolean everyModelFetched = false;
			for (int ptr = 0; ptr < npcModels.length; ptr++)
				if (!Model.modelIsFetched(npcModels[ptr]))
					everyModelFetched = true;

			if (everyModelFetched)
				return null;
			Model parts[] = new Model[npcModels.length];
			for (int j1 = 0; j1 < npcModels.length; j1++)
				parts[j1] = Model.fetchModel(npcModels[j1]);
			if (parts.length == 1)
				completedModel = parts[0];
			else
				completedModel = new Model(parts.length, parts);
			if (originalColours != null) {
				for (int k1 = 0; k1 < originalColours.length; k1++)
					completedModel.recolour(originalColours[k1], destColours[k1]);
			}
			completedModel.createBones();
			completedModel.light(frontLight, backLight, rightLight, middleLight, leftLight, true);
			modelCache.put(completedModel, type);
		}
		Model animatedModel = Model.entityModelDesc;
		animatedModel.method464(completedModel, FrameReader.isNullFrame(k) & FrameReader.isNullFrame(j));
		if (k != -1 && j != -1)
			animatedModel.method471(ai, j, k);
		else if (k != -1)
			animatedModel.applyTransform(k);
		if (sizeXZ != 128 || sizeY != 128)
			animatedModel.scaleT(sizeXZ, sizeXZ, sizeY);
		animatedModel.calculateDiagonals();
		animatedModel.triangleSkin = null;
		animatedModel.vertexSkin = null;
		if (squaresNeeded == 1)
			animatedModel.rendersWithinOneTile = true;
		return animatedModel;
	}

	public void readValues(Stream stream) {
		do {
			int i = stream.readUnsignedByte();
			if (i == 0)
				return;
			if (i == 1) {
				int j = stream.readUnsignedByte();
				npcModels = new int[j];
				for (int j1 = 0; j1 < j; j1++)
					npcModels[j1] = stream.readUnsignedWord();
			} else if (i == 2)
				name = stream.readNewString();
			else if (i == 3) {
				description = stream.readNewString();
			} else if (i == 12)
				squaresNeeded = stream.readSignedByte();
			else if (i == 13)
				standAnim = stream.readUnsignedWord();
			else if (i == 14) {
				walkAnim = stream.readUnsignedWord();
				runAnim = walkAnim;
			} else if (i == 17) {
				walkAnim = stream.readUnsignedWord();
				turn180AnimIndex = stream.readUnsignedWord();
				turn90CWAnimIndex = stream.readUnsignedWord();
				turn90CCWAnimIndex = stream.readUnsignedWord();
				if (walkAnim == 65535)
					walkAnim = -1;
				if (turn180AnimIndex == 65535)
					turn180AnimIndex = -1;
				if (turn90CWAnimIndex == 65535)
					turn90CWAnimIndex = -1;
				if (turn90CCWAnimIndex == 65535)
					turn90CCWAnimIndex = -1;
			} else if (i >= 30 && i < 40) {
				if (actions == null)
					actions = new String[5];
				actions[i - 30] = stream.readNewString();
				if (actions[i - 30].equalsIgnoreCase("hidden"))
					actions[i - 30] = null;
			} else if (i == 40) {
				int k = stream.readUnsignedByte();
				destColours = new int[k];
				originalColours = new int[k];
				for (int k1 = 0; k1 < k; k1++) {
					originalColours[k1] = stream.readUnsignedWord();
					destColours[k1] = stream.readUnsignedWord();
				}
			} else if (i == 60) {
				int l = stream.readUnsignedByte();
				npcHeadModels = new int[l];
				for (int l1 = 0; l1 < l; l1++)
					npcHeadModels[l1] = stream.readUnsignedWord();
			} else if (i == 90)
				stream.readUnsignedWord();
			else if (i == 91)
				stream.readUnsignedWord();
			else if (i == 92)
				stream.readUnsignedWord();
			else if (i == 93)
				drawMinimapDot = false;
			else if (i == 95)
				combatLevel = stream.readUnsignedWord();
			else if (i == 97)
				sizeXZ = stream.readUnsignedWord();
			else if (i == 98)
				sizeY = stream.readUnsignedWord();
			else if (i == 99)
				hasRenderPriority = true;
			else if (i == 100)
				lightning = stream.readSignedByte();
			else if (i == 101)
				shadow = stream.readSignedByte() * 5;
			else if (i == 102)
				headIcon = stream.readUnsignedWord();
			else if (i == 103)
				degreesToTurn = stream.readUnsignedWord();
			else if (i == 106) {
				varbitId = stream.readUnsignedWord();
				if (varbitId == 65535)
					varbitId = -1;
				varSettingsId = stream.readUnsignedWord();
				if (varSettingsId == 65535)
					varSettingsId = -1;
				int i1 = stream.readUnsignedByte();
				childrenIDs = new int[i1 + 1];
				for (int i2 = 0; i2 <= i1; i2++) {
					childrenIDs[i2] = stream.readUnsignedWord();
					if (childrenIDs[i2] == 65535)
						childrenIDs[i2] = -1;
				}
			} else if (i == 107)
				clickable = false;
		} while (true);
	}

	public NPCDef() {
		turn90CCWAnimIndex = -1;
		varbitId = -1;
		turn180AnimIndex = -1;
		varSettingsId = -1;
		combatLevel = -1;
		walkAnim = -1;
		squaresNeeded = 1;
		headIcon = -1;
		standAnim = -1;
		type = -1L;
		degreesToTurn = 32;
		turn90CWAnimIndex = -1;
		clickable = true;
		sizeY = 128;
		drawMinimapDot = true;
		sizeXZ = 128;
		hasRenderPriority = false;
	}

	public int turn90CCWAnimIndex;
	public static int cacheIndex;
	public int varbitId;
	public int turn180AnimIndex;
	public int varSettingsId;
	public static Stream stream;
	public int combatLevel;
	public String name;
	public String actions[];
	public int walkAnim;
	public int runAnim;
	public byte squaresNeeded;
	public int[] destColours;
	public static int[] streamIndices;
	public int[] npcHeadModels;
	public int headIcon;
	public int[] originalColours;
	public int standAnim;
	public long type;
	public int degreesToTurn;
	public static NPCDef[] cache;
	public static Client clientInstance;
	public int turn90CWAnimIndex;
	public boolean clickable;
	public int lightning;
	public int sizeY;
	public boolean drawMinimapDot;
	public int childrenIDs[];
	public String description;
	public int sizeXZ;
	public int shadow;
	public boolean hasRenderPriority;
	public int[] npcModels;
	public static MemCache modelCache = new MemCache(30);
}