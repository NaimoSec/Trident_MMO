
public final class Animation {
	
    public static void unpackConfig(CacheArchive streamLoader)
    {
       Stream stream = new Stream(streamLoader.getDataForName("seq.dat"));
        int length = stream.readUnsignedWord();
        if(anims == null)
            anims = new Animation[length];
        for(int j = 0; j < length; j++)
        {
            if(anims[j] == null)
                anims[j] = new Animation();
            anims[j].readValues(stream);
            /*
             * Glacor anims
             */
        	if(j == 10867) {
        		anims[j].frameCount = 19;
        		anims[j].delays = new int[]{5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5};
        		anims[j].frameIDs = new int[]{244252686, 244252714, 244252760, 244252736, 244252678, 244252780, 244252817, 244252756, 244252700, 244252774, 244252834, 244252715, 244252732, 244252836, 244252776, 244252701, 244252751, 244252743, 244252685};
        	}
        	if(j == 10901) {
        		anims[j].frameCount = 19;
        		anims[j].delays = new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
        		anims[j].frameIDs = new int[]{244252826, 244252833, 244252674, 244252724, 244252793, 244252696, 244252787, 244252753, 244252703, 244252800, 244252752, 244252744, 244252680, 244252815, 244252829, 244252769, 244252699, 244252757, 244252695};
        	}
        }
    }

    public int getFrameLength(int i)
    {
    	if(i > delays.length)
    		return 1;
        int j = delays[i];
        if(j == 0)
        {
            FrameReader reader = FrameReader.forID(frameIDs[i]);
            if(reader != null)
                j = delays[i] = reader.displayLength;
        }
        if(j == 0)
            j = 1;
        return j;
    }

	public void readValues(Stream stream)
	{
		do {
			int i = stream.readUnsignedByte();
			if(i == 0)
				break;
			if(i == 1) {
				frameCount = stream.readUnsignedWord();
				frameIDs = new int[frameCount];
				frameIDs2 = new int[frameCount];
				delays = new int[frameCount];
				for(int i_ = 0; i_ < frameCount; i_++){
					frameIDs[i_] = stream.readDWord();
					frameIDs2[i_] = -1;
				}
				for(int i_ = 0; i_ < frameCount; i_++)
					delays[i_] = stream.readUnsignedByte();
			}
			else if(i == 2)
				loopDelay = stream.readUnsignedWord();
			else if(i == 3) {
				int k = stream.readUnsignedByte();
				animationFlowControl = new int[k + 1];
				for(int l = 0; l < k; l++)
					animationFlowControl[l] = stream.readUnsignedByte();
				animationFlowControl[k] = 0x98967f;
			}
			else if(i == 4)
				oneSquareAnimation = true;
			else if(i == 5)
				forcedPriority = stream.readUnsignedByte();
			else if(i == 6)
				leftHandItem = stream.readUnsignedWord();
			else if(i == 7)
				rightHandItem = stream.readUnsignedWord();
			else if(i == 8)
				frameStep = stream.readUnsignedByte();
			else if(i == 9)
				resetWhenWalk = stream.readUnsignedByte();
			else if(i == 10)
				priority = stream.readUnsignedByte();
			else if(i == 11)
				delayType = stream.readUnsignedByte();
			else 
				System.out.println("Unrecognized seq.dat config code: "+i);
		} while(true);
		if(frameCount == 0)
		{
			frameCount = 1;
			frameIDs = new int[1];
			frameIDs[0] = -1;
			frameIDs2 = new int[1];
			frameIDs2[0] = -1;
			delays = new int[1];
			delays[0] = -1;
		}
		if(resetWhenWalk == -1)
			if(animationFlowControl != null)
				resetWhenWalk = 2;
			else
				resetWhenWalk = 0;
		if(priority == -1)
		{
			if(animationFlowControl != null)
			{
				priority = 2;
				return;
			}
			priority = 0;
		}
	}

    private Animation()
    {
        loopDelay = -1;
        oneSquareAnimation = false;
        forcedPriority = 5;
        leftHandItem = -1;
        rightHandItem = -1;
        frameStep = 99;
        resetWhenWalk = -1;
        priority = -1;
        delayType = 2;
    }
    public static Animation anims[];
    public int frameCount;
    public int frameIDs[];
    public int frameIDs2[];
    private int[] delays;
    public int loopDelay;
    public int animationFlowControl[];
    public boolean oneSquareAnimation;
    public int forcedPriority;
    public int leftHandItem;
    public int rightHandItem;
    public int frameStep;
    public int resetWhenWalk;
    public int priority;
    public int delayType;
    public static int anInt367;
}