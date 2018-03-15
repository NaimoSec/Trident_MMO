
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class SpriteLoader {

	/**
	 * Loads the sprite data and index files from the cache location. This can
	 * be edited to use an archive such as config or media to load from the
	 * cache.
	 * 
	 * @param archive
	 */
	public static void loadSprites(CacheArchive CacheArchive) {
		try {
			Stream index = new Stream(CacheArchive.getDataForName("sprites.idx"));
			Stream data = new Stream(CacheArchive.getDataForName("sprites.dat"));
			DataInputStream indexFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(index.buffer)));
			DataInputStream dataFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(data.buffer)));
			int totalSprites = indexFile.readInt();
			if (cache == null) {
				cache = new SpriteLoader[totalSprites];
				sprites = new Sprite[totalSprites];
			}
			for (int i = 0; i < totalSprites; i++) {
				int id = indexFile.readInt();
				if (cache[id] == null) {
					cache[id] = new SpriteLoader();
				}
				cache[id].readValues(indexFile, dataFile);
				createSprite(cache[id], false);
				//if(id == 246)
				//	sprites[id] = RSInterface.imageLoader(0, "Interfaces/skill_tab/DIVINATION");
			}
			indexFile.close();
			dataFile.close();
			loadSprites2();
			sprites2[88] = new Sprite("Icons/Sideicons/icon 16");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadSprites2() {
		try {
			//int amount = 0;
			Stream index = new Stream(FileOperations.readFile(signlink.findcachedir() + "/res/ig/sprites.idx"));
			Stream data = new Stream(FileOperations.readFile(signlink.findcachedir() + "/res/ig/sprites.dat"));
			//JagexBuffer index = new JagexBuffer(archive.grabData("sprite.idx"));
			//JagexBuffer data = new JagexBuffer(archive.grabData("sprite.dat"));
			DataInputStream indexFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(index.buffer)));
			DataInputStream dataFile = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(data.buffer)));
			int totalSprites = indexFile.readInt();
			if (cache2 == null) {
				cache2 = new SpriteLoader[totalSprites];
				sprites2 = new Sprite[totalSprites];
			}
			for (int i = 0; i < totalSprites; i++) {
				int id = indexFile.readInt();
				if (cache2[id] == null) {
					cache2[id] = new SpriteLoader();
				}
				cache2[id].readValues(indexFile, dataFile);
				createSprite(cache2[id], true);
				//if(id == 246)
				//	sprites2[id] = new Sprite("Interfaces/skill_tab/DIVINATION 0");
				//amount++;
			}
			indexFile.close();
			dataFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the information from the index and data files.
	 * 
	 * @param index
	 *            holds the sprite indices
	 * @param data
	 *            holds the sprite data per index
	 * @throws IOException
	 */
	public void readValues(DataInputStream index, DataInputStream data) throws IOException {
		do {
			int opCode = data.readByte();
			if (opCode == 0) {
				break;
			}
			if (opCode == 1) {
				id = data.readShort();
			} else if (opCode == 2) {
				name = data.readUTF();
			} else if (opCode == 3) {
				drawOffsetX = data.readShort();
			} else if (opCode == 4) {
				drawOffsetY = data.readShort();
			} else if (opCode == 5) {
				int indexLength = index.readInt();
				byte[] dataread = new byte[indexLength];
				data.readFully(dataread);
				spriteData = dataread;
			}
		} while (true);
	}

	/**
	 * Creates a sprite out of the spriteData.
	 * 
	 * @param sprite
	 */
	public static void createSprite(SpriteLoader sprite, boolean second) {
		
		 /*File directory = new File(signlink.findcachedir() + "dump"); if
		 (!directory.exists()) { directory.mkdir(); } FileOperations.WriteFile(directory.getAbsolutePath() +
		 System.getProperty("file.separator") + sprite.id + ".png",
		 sprite.spriteData);
		 */
		if(!second) {
			sprites[sprite.id] = new Sprite(sprite.spriteData);
			sprites[sprite.id].drawOffsetX = sprite.drawOffsetX;
			sprites[sprite.id].drawOffsetY = sprite.drawOffsetY;
		} else {
			sprites2[sprite.id] = new Sprite(sprite.spriteData);
			sprites2[sprite.id].drawOffsetX = sprite.drawOffsetX;
			sprites2[sprite.id].drawOffsetY = sprite.drawOffsetY;
		}
	}

	/**
	 * Gets the name of a specified sprite index.
	 * 
	 * @param index
	 * @return
	 */
	public static String getName(int index) {
		if (cache[index].name != null) {
			return cache[index].name;
		} else {
			return "null";
		}
	}

	/**
	 * Gets the drawOffsetX of a specified sprite index.
	 * 
	 * @param index
	 * @return
	 */
	public static int getOffsetX(int index) {
		return cache[index].drawOffsetX;
	}

	/**
	 * Gets the drawOffsetY of a specified sprite index.
	 * 
	 * @param index
	 * @return
	 */
	public static int getOffsetY(int index) {
		return cache[index].drawOffsetY;
	}

	/**
	 * Sets the default values.
	 */
	public SpriteLoader() {
		name = "name";
		id = -1;
		drawOffsetX = 0;
		drawOffsetY = 0;
		spriteData = null;
	}

	public static SpriteLoader[] cache;
	public static Sprite[] sprites = null;
	
	public static SpriteLoader[] cache2;
	public static Sprite[] sprites2 = null;
	
	public static int totalSprites;
	public String name;
	public int id;
	public int drawOffsetX;
	public int drawOffsetY;
	public byte[] spriteData;
}