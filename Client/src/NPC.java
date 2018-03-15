

public final class NPC extends Entity
{

	private Model getAnimatedModel()
	{
		if(super.anim >= 0 && super.animationDelay == 0)
		{
			int k = Animation.anims[super.anim].frameIDs[super.currentAnimFrame];
			int i1 = -1;
			if(super.forcedAnimId >= 0 && super.forcedAnimId != super.standAnimIndex)
				i1 = Animation.anims[super.forcedAnimId].frameIDs[super.currentForcedAnimFrame];
			return desc.getAnimatedModel(i1, k, Animation.anims[super.anim].animationFlowControl);
		}
		int l = -1;
		if(super.forcedAnimId >= 0)
			l = Animation.anims[super.forcedAnimId].frameIDs[super.currentForcedAnimFrame];
		return desc.getAnimatedModel(-1, l, null);
	}

	public Model getRotatedModel()
	{
		if(desc == null)
			return null;
		Model model = getAnimatedModel();
		if(model == null)
			return null;
		super.height = model.modelHeight;
		if(super.gfxId != -1 && super.currentAnim != -1)
		{
			SpotAnim spotAnim = SpotAnim.cache[super.gfxId];
			Model model_1 = spotAnim.getModel();
			if(model_1 != null)
			{
				int j = spotAnim.animation.frameIDs[super.currentAnim];
				Model model_2 = new Model(true, FrameReader.isNullFrame(j), false, model_1);
				model_2.translate(0, -super.graphicHeight, 0);
				model_2.createBones();
				model_2.applyTransform(j);
				model_2.triangleSkin = null;
				model_2.vertexSkin = null;
				if(spotAnim.sizeXY != 128 || spotAnim.sizeZ != 128)
					model_2.scaleT(spotAnim.sizeXY, spotAnim.sizeXY, spotAnim.sizeZ);
				model_2.light(64 + spotAnim.shadow, 850 + spotAnim.lightness, -30, -50, -30, true);
				Model aModel[] = {
						model, model_2
				};
				model = new Model(aModel);
			}
		}
		if(desc.squaresNeeded == 1)
			model.rendersWithinOneTile = true;
		return model;
	}

	public boolean isVisible()
	{
		return desc != null;
	}

	NPC()
	{
	}

	public NPCDef desc;
}
