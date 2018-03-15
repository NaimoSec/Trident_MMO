

public final class IDK {

	public static void unpackConfig(CacheArchive streamLoader) {
		Stream stream = new Stream(streamLoader.getDataForName("idk.dat"));
		int length = stream.readUnsignedWord();
		if (cache == null)
			cache = new IDK[length];
		for (int j = 0; j < length; j++) {
			if (cache[j] == null)
				cache[j] = new IDK();
			cache[j].readValues(stream);
		}
	}

	private void readValues(Stream stream) {
		do {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				return;
			if (opcode == 1)
				bodyPartID = stream.readUnsignedByte();
			else if (opcode == 2) {
				int modelCount = stream.readUnsignedByte();
				bodyModelIDs = new int[modelCount];
				for (int k = 0; k < modelCount; k++)
					bodyModelIDs[k] = stream.readUnsignedWord();

			} else if (opcode == 3)
				notSelectable = true;
			else if (opcode >= 40 && opcode < 50)
				recolourOriginal[opcode - 40] = stream.readUnsignedWord();
			else if (opcode >= 50 && opcode < 60)
				recolourTarget[opcode - 50] = stream.readUnsignedWord();
			else if (opcode >= 60 && opcode < 70)
				headModelIDs[opcode - 60] = stream.readUnsignedWord();
			//else
				//System.out.println("Error unrecognised config code: " + opcode);
		} while (true);
	}

	public int MALE_HEAD = 0, MALE_JAW = 1, MALE_TORSO = 2, MALE_ARMS = 3,
			MALE_HANDS = 4, MALE_LEGS = 5, MALE_FEET = 6, FEMALE_HEAD = 7,
			FEMALE_JAW = 8, FEMALE_TORSO = 9, FEMALE_ARMS = 10,
			FEMALE_HANFS = 11, FEMALE_LEGS = 12, FEMALE_FEET = 13;

	public boolean bodyModelIsFetched() {
		if (bodyModelIDs == null)
			return true;
		boolean flag = true;
		for (int j = 0; j < bodyModelIDs.length; j++)
			if (!Model.modelIsFetched(bodyModelIDs[j]))
				flag = false;
		return flag;
	}

	public Model fetchBodyModel() {
		if (bodyModelIDs == null)
			return null;
		Model models[] = new Model[bodyModelIDs.length];
		for (int i = 0; i < bodyModelIDs.length; i++)
			models[i] = Model.fetchModel(bodyModelIDs[i]);
		Model model;
		if (models.length == 1)
			model = models[0];
		else
			model = new Model(models.length, models);
		if(recolourTarget != null || recolourOriginal != null)
		for (int j = 0; j < 6; j++) {
			model.recolour(recolourOriginal[j], recolourTarget[j]);
		}
		model.recolour(55232, 6798);
		return model;
	}

	public boolean headModelFetched() {
		boolean flag1 = true;
		for (int i = 0; i < 5; i++)
			if (headModelIDs[i] != -1 && !Model.modelIsFetched(headModelIDs[i]))
				flag1 = false;
		return flag1;
	}

	public Model fetchHeadModel() {
		Model models[] = new Model[5];
		int j = 0;
		for (int k = 0; k < 5; k++)
			if (headModelIDs[k] != -1)
				models[j++] = Model.fetchModel(headModelIDs[k]);
		Model model = new Model(j, models);
		for (int l = 0; l < 6; l++) {
			model.recolour(recolourOriginal[l], recolourTarget[l]);
		}
		model.recolour(55232, 6798);
		return model;
	}

	private IDK() {
		bodyPartID = -1;
		recolourOriginal = new int[6];
		recolourTarget = new int[6];
		notSelectable = false;
	}
	public static IDK cache[];
	public int bodyPartID;
	private int[] bodyModelIDs;
	private int[] recolourOriginal;
	private int[] recolourTarget;
	private final int[] headModelIDs = {
	-1, -1, -1, -1, -1 };
	public boolean notSelectable;

}