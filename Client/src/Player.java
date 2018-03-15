
@SuppressWarnings("all")
public final class Player extends Entity {

	public int frontLight = 68;
	public int backLight = 820;
	public int rightLight = 0;
	public int middleLight = -1; // Cannot be 0
	public int leftLight = 0;

	public Model getRotatedModel() {
		if (!visible)
			return null;
		Model model = method452();
		if (model == null)
			return null;
		super.height = model.modelHeight;
		model.rendersWithinOneTile = true;
		if (aBoolean1699)
			return model;
		if (super.gfxId != -1 && super.currentAnim != -1) {
			SpotAnim spotAnim = SpotAnim.cache[super.gfxId];
			Model model_2 = spotAnim.getModel();
			if(spotAnim.animation != null) {
				if (FrameReader.animationlist[Integer.parseInt(Integer.toHexString(spotAnim.animation.frameIDs[0]).substring(0, Integer.toHexString(spotAnim.animation.frameIDs[0])
															.length() - 4), 16)].length == 0){
					model_2 = null;
					
				}
			}
			
			if (model_2 != null) {
				Model model_3 = new Model(true, FrameReader.isNullFrame(super.currentAnim), false, model_2);
				model_3.translate(0, -super.graphicHeight, 0);
				model_3.createBones();
				model_3.scaleT(132, 132, 132);
				model_3.applyTransform(spotAnim.animation.frameIDs[super.currentAnim]);
				model_3.triangleSkin = null;
				model_3.vertexSkin = null;
				if (spotAnim.sizeXY != 128 || spotAnim.sizeZ != 128)
					model_3.scaleT(spotAnim.sizeXY, spotAnim.sizeXY, spotAnim.sizeZ);
				model_3.light(64 + spotAnim.shadow, 850 + spotAnim.lightness, -30, -50, -30, true);
				Model aclass30_sub2_sub4_sub6_1s[] = { model, model_3 };
				model = new Model(aclass30_sub2_sub4_sub6_1s);
			}
		}
		if (tranformIntoModel != null) {
			if (Client.loopCycle >= transformedTimer)
				tranformIntoModel = null;
			if (Client.loopCycle >= startTimeTransform && Client.loopCycle < transformedTimer) {
				Model model_1 = tranformIntoModel;
				model_1.light(frontLight, backLight, rightLight, middleLight, leftLight, true);
				if (super.turnDirection == 512) {
					model_1.rotateBy90();
					model_1.rotateBy90();
					model_1.rotateBy90();
				} else if (super.turnDirection == 1024) {
					model_1.rotateBy90();
					model_1.rotateBy90();
				} else if (super.turnDirection == 1536)
					model_1.rotateBy90();
				Model models[] = { model, model_1 };
				model = new Model(models);
				if (super.turnDirection == 512)
					model_1.rotateBy90();
				else if (super.turnDirection == 1024) {
					model_1.rotateBy90();
					model_1.rotateBy90();
				} else if (super.turnDirection == 1536) {
					model_1.rotateBy90();
					model_1.rotateBy90();
					model_1.rotateBy90();
				}
				model_1.translate(super.x - resizeX, z - resizeZ, super.y - resizeY);
			}
		}
		model.rendersWithinOneTile = true;
		return model;
	}

	public int constitution, maxConstitution;

	public void updatePlayerAppearance(Stream stream) {
		stream.currentOffset = 0;
		myGender = stream.readUnsignedByte();
		headIcon = stream.readUnsignedByte();
		bountyHunterIcon = stream.readUnsignedByte();
		skulled = stream.readUnsignedWord() == 1;
		desc = null;
		team = 0;
		for (int partId = 0; partId < 12; partId++) {
			int firstByte = stream.readUnsignedByte();
			if (firstByte == 0) {
				equipment[partId] = 0;
				continue;
			}
			int secondByte = stream.readUnsignedByte();
			equipment[partId] = (firstByte << 8) + secondByte;
			if (partId == 0 && equipment[0] == 65535) {
				desc = NPCDef.forID(stream.readUnsignedWord());
				break;
			}
			if(partId == 8)
				Client.myHeadAndJaw[0] = equipment[partId]-256;
			if(partId == 11)
				Client.myHeadAndJaw[1] = equipment[partId]-256;
			if (equipment[partId] >= 512 && equipment[partId] - 512 < ItemDef.totalItems) {
				int l1 = ItemDef.forID(equipment[partId] - 512).team;
				if (l1 != 0)
					team = l1;
			}
		}

		for (int l = 0; l < 5; l++) {
			int j1 = stream.readUnsignedByte();
			if (j1 < 0 || j1 >= Client.anIntArrayArray1003[l].length)
				j1 = 0;
			anIntArray1700[l] = j1;
		}

		super.standAnimIndex = stream.readUnsignedWord();
		if (super.standAnimIndex == 65535)
			super.standAnimIndex = -1;
		super.nextRenderAnim = stream.readUnsignedWord();
		if (super.nextRenderAnim == 65535)
			super.nextRenderAnim = -1;
		super.walkAnimIndex = stream.readUnsignedWord();
		if (super.walkAnimIndex == 65535)
			super.walkAnimIndex = -1;
		super.turn180AnimIndex = stream.readUnsignedWord();
		if (super.turn180AnimIndex == 65535)
			super.turn180AnimIndex = -1;
		super.turn90CWAnimIndex = stream.readUnsignedWord();
		if (super.turn90CWAnimIndex == 65535)
			super.turn90CWAnimIndex = -1;
		super.turn90CCWAnimIndex = stream.readUnsignedWord();
		if (super.turn90CCWAnimIndex == 65535)
			super.turn90CCWAnimIndex = -1;
		super.runAnimation = stream.readUnsignedWord();
		if (super.runAnimation == 65535)
			super.runAnimation = -1;
		name = TextClass.fixName(TextClass.nameForLong(stream.readQWord()));
		combatLevel = stream.readUnsignedByte();
		//skill = stream.readUnsignedWord();
		playerRights = stream.readUnsignedWord();
		playerTitle = stream.readUnsignedWord();
		visible = true;
		aLong1718 = 0L;
		
		if (desc != null) {
			combatLevel = desc.combatLevel;
			super.standAnimIndex = desc.standAnim;
			super.nextRenderAnim = desc.standAnim;
			super.walkAnimIndex = desc.walkAnim;
			super.turn180AnimIndex = desc.standAnim;
			super.turn90CWAnimIndex = desc.walkAnim;
			super.turn90CCWAnimIndex = desc.walkAnim;
			super.runAnimation = desc.walkAnim;
		}
		
		
		for (int k1 = 0; k1 < 12; k1++) {
			aLong1718 <<= 4;
			if (equipment[k1] >= 256)
				aLong1718 += equipment[k1] - 256;
		}

		if (equipment[0] >= 256)
			aLong1718 += equipment[0] - 256 >> 4;
		if (equipment[1] >= 256)
			aLong1718 += equipment[1] - 256 >> 8;
		for (int i2 = 0; i2 < 5; i2++) {
			aLong1718 <<= 3;
			aLong1718 += anIntArray1700[i2];
		}

		aLong1718 <<= 1;
		aLong1718 += myGender;
	}

	public Model method452() {
		if (desc != null) {
			int j = -1;
			if (super.anim >= 0 && super.animationDelay == 0) {
				j = Animation.anims[super.anim].frameIDs[super.currentAnimFrame];
			} else if (super.forcedAnimId >= 0) {
				j = Animation.anims[super.forcedAnimId].frameIDs[super.currentForcedAnimFrame];
			}
			Model model = desc.getAnimatedModel(-1, j, null);
			return model;
		}
		long l = aLong1718;
		int k = -1;
		int i1 = -1;
		int j1 = -1;
		int k1 = -1;
		int nextFrame = -1;
		if (super.anim >= 0 && super.animationDelay == 0) {
			Animation animation = Animation.anims[super.anim];
			k = animation.frameIDs[super.currentAnimFrame];
			nextFrame = animation.frameIDs[super.currentAnimFrame >= animation.frameIDs.length-1 ? 0 : super.currentAnimFrame+1];
			if (super.forcedAnimId >= 0 && super.forcedAnimId != super.standAnimIndex) {
				i1 = Animation.anims[super.forcedAnimId].frameIDs[super.currentForcedAnimFrame];
				nextFrame = animation.frameIDs[super.forcedAnimId >= animation.frameIDs.length-1 ? 0 : super.forcedAnimId+1];
			}
			if (animation.leftHandItem >= 0) {
				j1 = animation.leftHandItem;
				l += j1 - equipment[5] << 40;
			}
			if (animation.rightHandItem >= 0) {
				k1 = animation.rightHandItem;
				l += k1 - equipment[3] << 48;
			}
		} else if (super.forcedAnimId >= 0) {
			k = Animation.anims[super.forcedAnimId].frameIDs[super.currentForcedAnimFrame];
			nextFrame = Animation.anims[super.forcedAnimId].frameIDs[super.currentForcedAnimFrame >= Animation.anims[super.forcedAnimId].frameIDs.length-1 ? 0 : super.currentForcedAnimFrame+1];
		}
		Model model_1 = (Model) modelCache.get(l);
		if (model_1 == null) {
			boolean fetchModels = false;
			for (int bodyPartId = 0; bodyPartId < 12; bodyPartId++) {
				int partId = equipment[bodyPartId];
				if (k1 >= 0 && bodyPartId == 3)
					partId = k1;
				if (j1 >= 0 && bodyPartId == 5)
					partId = j1;
				if((partId - 256) != 846) {
					
					if (partId >= 256 && partId < 512 && !IDK.cache[partId - 256].bodyModelIsFetched())
						fetchModels = true;
					if (partId >= 512 && !ItemDef.forID(partId - 512).equipModelFetched(myGender))
						fetchModels = true;
			
				}
			}

			if (fetchModels) {
				if (aLong1697 != -1L)
					model_1 = (Model) modelCache.get(aLong1697);
				if (model_1 == null)
					return null;
			}
		}
		if (model_1 == null) {
			Model bodyPartModels[] = new Model[13];
			int j2 = 0;
			for (int currentPart = 0; currentPart < 12; currentPart++) {
				int i3 = equipment[currentPart];
				if (k1 >= 0 && currentPart == 3)
					i3 = k1;
				if (j1 >= 0 && currentPart == 5)
					i3 = j1;
				if (i3 >= 256 && i3 < 512) {
					Model model_3 = null;
					if((i3 - 256) < IDK.cache.length)
						 model_3 = IDK.cache[i3 - 256].fetchBodyModel();
					if (model_3 != null)
						bodyPartModels[j2++] = model_3;
				}
				if (i3 >= 512) {
					Model model_4 = ItemDef.forID(i3 - 512).getEquipModel(myGender);
					if (model_4 != null)
						bodyPartModels[j2++] = model_4;
				}
			}
			if (Client.getOption("player_shadow"))
				bodyPartModels[j2++] = Model.fetchModel(38715);
			model_1 = new Model(j2, bodyPartModels);
			for (int j3 = 0; j3 < 5; j3++)
				if (anIntArray1700[j3] != 0) {
					model_1.recolour(Client.anIntArrayArray1003[j3][0], Client.anIntArrayArray1003[j3][anIntArray1700[j3]]);
					if (j3 == 1)
						model_1.recolour(Client.anIntArray1204[0], Client.anIntArray1204[anIntArray1700[j3]]);
				}

			model_1.createBones();
			model_1.light(frontLight, backLight, rightLight, middleLight, leftLight, true);
			modelCache.put(model_1, l);
			aLong1697 = l;
		}
		if (aBoolean1699)
			return model_1;
		Model model_2 = Model.entityModelDesc;
		model_2.method464(model_1, FrameReader.isNullFrame(k) & FrameReader.isNullFrame(i1));
		if (k != -1 && i1 != -1) {
			model_2.method471(Animation.anims[super.anim].animationFlowControl, i1, k);
		} else if (k != -1) {
				model_2.applyTransform(k);
		}
		tween = !tween;
		model_2.calculateDiagonals();
		model_2.triangleSkin = null;
		model_2.vertexSkin = null;
		return model_2;
	}

	public boolean isVisible() {
		return visible;
	}

	public int playerRights, playerTitle;

	public Model getPlayerModel() {
		if (!visible)
			return null;
		if (desc != null)
			return desc.getHeadModel();
		boolean flag = false;
		for (int i = 0; i < 12; i++) {
			int j = equipment[i];
			try {
				if (j >= 256 && j < 512 && !IDK.cache[j - 256].headModelFetched())
					flag = true;
				if (j >= 512 && !ItemDef.forID(j - 512).dialogueModelFetched(myGender)) {
					flag = true;
				}
			} catch(Exception e) {
				flag = true;
			}
		}

		if (flag)
			return null;
		Model models[] = new Model[12];
		int k = 0;
		for (int l = 0; l < 12; l++) {
			int i1 = equipment[l];
			if (i1 >= 256 && i1 < 512) {
				Model model_1 = IDK.cache[i1 - 256].fetchHeadModel();
				if (model_1 != null)
					models[k++] = model_1;
			}
			if (i1 >= 512) {
				Model model_2 = ItemDef.forID(i1 - 512).getDialogueModel(myGender);
				if (model_2 != null)
					models[k++] = model_2;
			}
		}

		Model model = new Model(k, models);
		for (int j1 = 0; j1 < 5; j1++)
			if (anIntArray1700[j1] != 0) {
				model.recolour(Client.anIntArrayArray1003[j1][0], Client.anIntArrayArray1003[j1][anIntArray1700[j1]]);
				if (j1 == 1)
					model.recolour(Client.anIntArray1204[0], Client.anIntArray1204[anIntArray1700[j1]]);
			}

		return model;
	}

	Player() {
		aLong1697 = -1L;
		aBoolean1699 = false;
		anIntArray1700 = new int[5];
		visible = false;
		anInt1715 = 9;
		equipment = new int[12];
	}

	private long aLong1697;
	public NPCDef desc;
	boolean aBoolean1699;
	final int[] anIntArray1700;
	public int team;
	public int myGender;
	public String name;
	static MemCache modelCache = new MemCache(260);
	public int combatLevel;
	public int headIcon;
	public int bountyHunterIcon;
	public int hintIcon;
	public boolean skulled;
	public int summonLevel;
	public int SummonLevel;
	public int startTimeTransform;
	int transformedTimer;
	int z;
	boolean visible;
	int resizeX;
	int resizeZ;
	int resizeY;
	Model tranformIntoModel;
	private int anInt1715;
	public final int[] equipment;
	private long aLong1718;
	int extendedXMin;
	int extendedYMin;
	int extendedXMax;
	int extendedYMax;
	boolean tween;

}
