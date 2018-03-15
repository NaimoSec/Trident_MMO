package org.trident.world.clip.region;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.trident.model.GameObject;
import org.trident.model.Position;
import org.trident.util.Misc;
import org.trident.world.clip.def.CachedGameObjectDefinition;
import org.trident.world.clip.stream.ByteStream;
import org.trident.world.content.Locations.Location;
import org.trident.world.entity.GameCharacter;

/**
 * A highly modified version of the released clipping.
 * @author Relex lawl and Palidino: Gave me (Gabbe) the base.
 * @editor Gabbe: Rewrote the system, now loads regions when they're actually needed and now fetches regions from a hashmap.
 */
public final class RegionClipping {

	private static HashMap<Integer, RegionClipping> regionsList = new HashMap<Integer, RegionClipping>();
	private static final ArrayList<Integer> loadedRegions = new ArrayList<Integer>();

	class RegionData {

		private int mapGround;
		private int mapObject;

		public RegionData(int mapGround, int mapObject) {
			this.mapGround = mapGround;
			this.mapObject = mapObject;			
		}
	}

	private int id;
	private int[][][] clips = new int[4][][];

	
	public GameObject[][][] gameObjects = new GameObject[4][][];

	private RegionData regionData;

	public RegionClipping(int id, int map, int mapObj) {
		this.id = id;
		regionData = new RegionData(map, mapObj);
	}

	public int getId() {
		return id;
	}

	public void removeClip(int x, int y, int height, int shift) {
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;
		if (height < 0 || height >= 4)
			height = 0;
		loadRegion(x, y);
		if (clips[height] == null) {
			clips[height] = new int[64][64];
		}
		clips[height][x - regionAbsX][y - regionAbsY] =/* 16777215 - shift*/0;
	}

	public void addClip(int x, int y, int height, int shift) {		
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;
		if (height < 0 || height >= 4)
			height = 0;
		loadRegion(x, y);
		if (clips[height] == null) {
			clips[height] = new int[64][64];
		}
		clips[height][x - regionAbsX][y - regionAbsY] |= shift;
	}

	public static void init() {
		try {
			CachedGameObjectDefinition.init();
			final File file = new File("./data/clipping/map_index");
			final byte[] buffer = new byte[(int) file.length()];
			final DataInputStream input = new DataInputStream(new FileInputStream(file));
			input.readFully(buffer);
			input.close();
			final ByteStream stream = new ByteStream(buffer);
			int size = stream.getUShort();
			for (int i = 0; i < size; i++) {
				int regionId = stream.getUShort();				
				regionsList.put(regionId, new RegionClipping(regionId, stream.getUShort(), stream.getUShort()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void loadRegion(int x, int y) {
		int regionId = -1;
		try {
			int regionX = x >> 3;
			int regionY = y >> 3;
			regionId = (regionX / 8 << 8) + regionY / 8;
			if (regionsList.get(regionId) == null)
				return;
			if (loadedRegions.contains(regionId))
				return;
			int mapGround = regionsList.get(regionId).regionData.mapGround;
			int mapObjects = regionsList.get(regionId).regionData.mapObject;
			byte[] objectData = Misc.getBuffer(new File("./data/clipping/maps/" + mapObjects + ".gz"));
			byte[] groundData =  Misc.getBuffer(new File("./data/clipping/maps/" + mapGround + ".gz"));
			if (objectData == null || groundData == null) {
				loadedRegions.add(regionId);
				return;
			}
			loadedRegions.add(regionId);
			loadMaps(regionId, new ByteStream(objectData), new ByteStream(groundData));
		} catch (Exception e) {
			loadedRegions.add(regionId);
			e.printStackTrace();
			System.out.println("Error loading "+regionId);
		}
	}

	private static void loadMaps(int regionId, ByteStream objectStream, ByteStream groundStream) {
		int absX = (regionId >> 8) * 64;
		int absY = (regionId & 0xff) * 64;
		byte[][][] heightMap = new byte[4][64][64];
		for (int z = 0; z < 4; z++) {
			for (int tileX = 0; tileX < 64; tileX++) {
				for (int tileY = 0; tileY < 64; tileY++) {
					while (true) {
						int tileType = groundStream.getUByte();
						if (tileType == 0) {
							break;
						} else if (tileType == 1) {
							groundStream.getUByte();
							break;
						} else if (tileType <= 49) {
							groundStream.getUByte();
						} else if (tileType <= 81) {
							heightMap[z][tileX][tileY] = (byte) (tileType - 49);
						}
					}
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			for (int i2 = 0; i2 < 64; i2++) {
				for (int i3 = 0; i3 < 64; i3++) {
					if ((heightMap[i][i2][i3] & 1) == 1) {
						int height = i;
						if ((heightMap[1][i2][i3] & 2) == 2) {
							height--;
						}
						if (height >= 0 && height <= 3) {
							addClipping(absX + i2, absY + i3, height, 0x200000);
						}
					}
				}
			}
		}
		int objectId = -1;
		int incr;
		while ((incr = objectStream.getUSmart()) != 0) {
			objectId += incr;
			int location = 0;
			int incr2;
			while ((incr2 = objectStream.getUSmart()) != 0) {
				location += incr2 - 1;
				int localX = location >> 6 & 0x3f;
						int localY = location & 0x3f;
						int height = location >> 12;
		int objectData = objectStream.getUByte();
		int type = objectData >> 2;
						int direction = objectData & 0x3;
						if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64) {
							continue;
						}
						if ((heightMap[1][localX][localY] & 2) == 2) {
							height--;
						}
						if (height >= 0 && height <= 3)
							addObject(objectId, absX + localX, absY + localY, height, type, direction); //Add object to clipping
			}
		}
	}

	public static void addClipping(int x, int y, int height, int shift) {
		//System.out.println("Added clip at "+x+" and "+y+"");
		RegionClipping.loadRegion(x, y);
		int regionX = x >> 3;
						int regionY = y >> 3;
			int regionId = (regionX / 8 << 8) + regionY / 8;
			if(RegionClipping.regionsList.containsKey(regionId))
				RegionClipping.regionsList.get(regionId).addClip(x, y, height, shift);
	}

	public static void removeClipping(int x, int y, int height, int shift) {
		RegionClipping.loadRegion(x, y);
		int regionX = x >> 3;
			int regionY = y >> 3;
			int regionId = (regionX / 8 << 8) + regionY / 8;
			if(RegionClipping.regionsList.containsKey(regionId))
				RegionClipping.regionsList.get(regionId).removeClip(x, y, height, shift);
	}

	public static RegionClipping forPosition(Position position) {
		int regionX = position.getX() >> 3;
			int regionY = position.getY() >> 3;
			int regionId = ((regionX / 8) << 8) + (regionY / 8);
			loadRegion(position.getX(), position.getY());
			if(RegionClipping.regionsList.containsKey(regionId))
				return RegionClipping.regionsList.get(regionId);
			return null;
	}

	public static int[] getObjectInformation(Position position) {
		final RegionClipping clipping = forPosition(position);
		if (clipping != null) {
			int x = position.getX();
			int y = position.getY();
			int height = position.getZ();
			int regionAbsX = (clipping.id >> 8) * 64;
			int regionAbsY = (clipping.id & 0xff) * 64;
			if (height < 0 || height >= 4)
				height = 0;
			RegionClipping.loadRegion(x, y);
			if (clipping.gameObjects == null
					|| clipping.gameObjects[height] == null
					|| clipping.gameObjects[height][x - regionAbsX] == null
					|| clipping.gameObjects[height][x - regionAbsX][y - regionAbsY] == null) {
				return null;
			}
			return new int[] { 
					clipping.gameObjects[height][x - regionAbsX][y - regionAbsY].getFace(),
					clipping.gameObjects[height][x - regionAbsX][y - regionAbsY].getType(),
					clipping.gameObjects[height][x - regionAbsX][y - regionAbsY].getId(),
			};
		} else {
			return null;
		}
	}

	public static boolean objectExists(GameObject object) {
		Location loc = Location.getLocation(object);
		boolean barrows = object.getPosition().getZ() == -1 && object.getDefinition() != null && (object.getDefinition().getName().toLowerCase().contains("sarcophagus") || object.getDefinition().getName().toLowerCase().contains("staircase")) || loc != null && loc == Location.BARROWS;
		boolean catherbyAquariums = object.getId() == 10091 && object.getPosition().getX() >= 2829 && object.getPosition().getX() <= 2832 && object.getPosition().getY() >= 3441 && object.getPosition().getY() <= 3447;
		boolean freeForAllPortal = object.getId() == 38700 && object.getPosition().getX() == 2814 && object.getPosition().getY() == 5509;
		boolean warriorsGuild = object.getId() == 15653 && object.getPosition().getX() == 2877 && object.getPosition().getY() == 3546 || loc == Location.WARRIORS_GUILD;
		boolean fightPit = object.getId() == 9369 && object.getPosition().getX() == 2399 && object.getPosition().getY() == 5176 || object.getId() == 9368 && object.getPosition().getX() == 2399 && object.getPosition().getY() == 5168;
		boolean barbCourseRopeswing = object.getId() == 2282 && object.getPosition().getX() == 2551 && object.getPosition().getY() == 3550;
		boolean lumbridgeCastle = object.getId() == 12348 && object.getPosition().getX() == 3207 && object.getPosition().getY() == 3217 || object.getId() == 1738 && object.getPosition().getX() == 3204 && object.getPosition().getY() == 3207 || object.getId() == 1739 && object.getPosition().getX() == 3204 && object.getPosition().getY() == 3207 && object.getPosition().getZ() == 1 || object.getId() == 1739 && object.getPosition().getX() == 3204 && object.getPosition().getY() == 3229 && object.getPosition().getZ() == 1;
		boolean rfd = object.getId() == 12356 && (object.getPosition().getX() == 1900 && object.getPosition().getY() == 5345 || object.getPosition().getX() == 1899 && object.getPosition().getY() == 5366 || object.getPosition().getX() == 1910 && object.getPosition().getY() == 5356 || object.getPosition().getX() == 1889 && object.getPosition().getY() == 5355);
		boolean lunar = object.getId() == 29944 && object.getPosition().getX() == 2111 && object.getPosition().getY() == 3917;
		boolean chaosTunnels = object.getId() == 28779; //It checks player coords anyway
		boolean trees = object.getId() == 1306 && object.getPosition().getX() == 2696 && object.getPosition().getY() == 3423 || object.getId() == 1307 && (object.getPosition().getX() == 2727 && object.getPosition().getY() == 3501 || object.getPosition().getX() == 2729 && object.getPosition().getY() == 3481);
		boolean godwars = object.getPosition().getZ() == 2;
		boolean lawAltar = object.getId() == 2485 && object.getPosition().getX() == 2463 && object.getPosition().getY() == 4831;
		boolean mageBankLever = object.getId() == 5959 && object.getPosition().getX() == 3090 && object.getPosition().getY() == 3956;
		if(mageBankLever || lawAltar || trees || chaosTunnels || lunar || barrows || rfd || lumbridgeCastle || barbCourseRopeswing || catherbyAquariums || freeForAllPortal || warriorsGuild || fightPit || godwars || barrows)
			return true;
		int[] info = getObjectInformation(object.getPosition());
		if(info != null) {
			if(info[2] == object.getId()) {
				return true;
			}
		}
		return false;
	}

	public static GameObject getGameObject(Position position) {
		final RegionClipping clipping = forPosition(position);
		if (clipping != null) {
			int x = position.getX();
			int y = position.getY();
			int height = position.getZ();
			int regionAbsX = (clipping.id >> 8) * 64;
			int regionAbsY = (clipping.id & 0xff) * 64;
			if (height < 0 || height >= 4)
				height = 0;
			if (clipping.gameObjects[height] == null) {
				return null;
			}
			return clipping.gameObjects[height][x - regionAbsX][y - regionAbsY];
		} else {
			return null;
		}
	}

	private static void addClippingForVariableObject(int x, int y, int height, int type, int direction, boolean flag) {
		if (type == 0) {
			if (direction == 0) {
				addClipping(x, y, height, 128);
				addClipping(x - 1, y, height, 8);
			} else if (direction == 1) {
				addClipping(x, y, height, 2);
				addClipping(x, y + 1, height, 32);
			} else if (direction == 2) {
				addClipping(x, y, height, 8);
				addClipping(x + 1, y, height, 128);
			} else if (direction == 3) {
				addClipping(x, y, height, 32);
				addClipping(x, y - 1, height, 2);
			}
		} else if (type == 1 || type == 3) {
			if (direction == 0) {
				addClipping(x, y, height, 1);
				addClipping(x - 1, y, height, 16);
			} else if (direction == 1) {
				addClipping(x, y, height, 4);
				addClipping(x + 1, y + 1, height, 64);
			} else if (direction == 2) {
				addClipping(x, y, height, 16);
				addClipping(x + 1, y - 1, height, 1);
			} else if (direction == 3) {
				addClipping(x, y, height, 64);
				addClipping(x - 1, y - 1, height, 4);
			}
		} else if (type == 2) {
			if (direction == 0) {
				addClipping(x, y, height, 130);
				addClipping(x - 1, y, height, 8);
				addClipping(x, y + 1, height, 32);
			} else if (direction == 1) {
				addClipping(x, y, height, 10);
				addClipping(x, y + 1, height, 32);
				addClipping(x + 1, y, height, 128);
			} else if (direction == 2) {
				addClipping(x, y, height, 40);
				addClipping(x + 1, y, height, 128);
				addClipping(x, y - 1, height, 2);
			} else if (direction == 3) {
				addClipping(x, y, height, 160);
				addClipping(x, y - 1, height, 2);
				addClipping(x - 1, y, height, 8);
			}
		}
		if (flag) {
			if (type == 0) {
				if (direction == 0) {
					addClipping(x, y, height, 65536);
					addClipping(x - 1, y, height, 4096);
				} else if (direction == 1) {
					addClipping(x, y, height, 1024);
					addClipping(x, y + 1, height, 16384);
				} else if (direction == 2) {
					addClipping(x, y, height, 4096);
					addClipping(x + 1, y, height, 65536);
				} else if (direction == 3) {
					addClipping(x, y, height, 16384);
					addClipping(x, y - 1, height, 1024);
				}
			}
			if (type == 1 || type == 3) {
				if (direction == 0) {
					addClipping(x, y, height, 512);
					addClipping(x - 1, y + 1, height, 8192);
				} else if (direction == 1) {
					addClipping(x, y, height, 2048);
					addClipping(x + 1, y + 1, height, 32768);
				} else if (direction == 2) {
					addClipping(x, y, height, 8192);
					addClipping(x + 1, y + 1, height, 512);
				} else if (direction == 3) {
					addClipping(x, y, height, 32768);
					addClipping(x - 1, y - 1, height, 2048);
				}
			} else if (type == 2) {
				if (direction == 0) {
					addClipping(x, y, height, 66560);
					addClipping(x - 1, y, height, 4096);
					addClipping(x, y + 1, height, 16384);
				} else if (direction == 1) {
					addClipping(x, y, height, 5120);
					addClipping(x, y + 1, height, 16384);
					addClipping(x + 1, y, height, 65536);
				} else if (direction == 2) {
					addClipping(x, y, height, 20480);
					addClipping(x + 1, y, height, 65536);
					addClipping(x, y - 1, height, 1024);
				} else if (direction == 3) {
					addClipping(x, y, height, 81920);
					addClipping(x, y - 1, height, 1024);
					addClipping(x - 1, y, height, 4096);
				}
			}
		}
	}

	private static void addClippingForSolidObject(int x, int y, int height, int xLength, int yLength, boolean flag) {
		int clipping = 256;
		if (flag) {
			clipping += 0x20000;
		}
		for (int i = x; i < x + xLength; i++) {
			for (int i2 = y; i2 < y + yLength; i2++) {
				addClipping(i, i2, height, clipping);
			}
		}
	}

	public static int[] getLocalPosition(Position position) {
		final RegionClipping clipping = RegionClipping.forPosition(position);
		int absX = position.getX();
		int absY = position.getY();
		int regionAbsX = (clipping.getId() >> 8) * 64;
		int regionAbsY = (clipping.getId() & 0xff) * 64;
		int localX = absX - regionAbsX;
		int localY = absY - regionAbsY;
		return new int[] { localX, localY };
	}

	public static void removeClip(int x, int y, int height)
	{
		int regionX = x >> 3;
		int regionY = y >> 3;
		int regionId = ((regionX / 8) << 8) + (regionY / 8);
		int regionAbsX = (regionId >> 8) * 64;
		int regionAbsY = (regionId & 0xff) * 64;
		loadRegion(x, y);
		if(RegionClipping.regionsList.containsKey(regionId))
			RegionClipping.regionsList.get(regionId).clips[height][x- regionAbsX][y- regionAbsY] = 0;
	}

	public static void addObject(int objectId, int x, int y, int height, int type, int direction) {
		if(CachedGameObjectDefinition.removedObject(objectId))
			return;
		CachedGameObjectDefinition def = CachedGameObjectDefinition.forId(objectId);
		if (def == null) {
			return;
		}
		RegionClipping.loadRegion(x, y);
		switch (objectId) {
		case 14233: //pest control gates
		case 14235: //pest control gates
			return;
		}
		final Position position = new Position(x, y, height);
		final RegionClipping clipping = forPosition(position);
		if (clipping != null) {
			if (clipping.gameObjects[height % 4] == null) {
				clipping.gameObjects[height % 4] = new GameObject[64][64];
			}
			int[] local = getLocalPosition(position);
			clipping.gameObjects[height % 4][local[0]][local[1]] = new GameObject(objectId, new Position(x, y, height), type, direction);
		}
		if (objectId == -1) {
			removeClipping(x, y, height, 0x000000);
			return;
		}
		int xLength;
		int yLength;
		if (direction != 1 && direction != 3) {
			xLength = def.xLength();
			yLength = def.yLength();
		} else {
			yLength = def.xLength();
			xLength = def.yLength();
		}
		if (type == 22) {
			if (def.hasActions() && def.unwalkable) {
				addClipping(x, y, height, 0x200000);
			}
		} else if (type >= 9) {
			if(def.unwalkable)
			{
				addClippingForSolidObject(x, y, height, xLength, yLength, def.aBoolean779);
			}
		} else if (type >= 0 && type <= 3) {
			if(def.unwalkable)
			{
				addClippingForVariableObject(x, y, height, type, direction, def.aBoolean779);
			}
		}
	}

	public static void addObject(GameObject gameObject) {
		if (gameObject.getId() != 65535)
			addObject(gameObject.getId(), gameObject.getPosition().getX(), gameObject.getPosition().getY(), gameObject.getPosition().getZ(),
					gameObject.getType(), gameObject.getFace());
	}

	public static void removeObject(GameObject gameObject) {
		addObject(-1, gameObject.getPosition().getX(), gameObject.getPosition().getY(), gameObject.getPosition().getZ(),
				gameObject.getType(), gameObject.getFace());
	}

	public static int getClipping(int x, int y, int height) {
		loadRegion(x, y);
		int regionX = x >> 3;
		int regionY = y >> 3;
		int regionId = ((regionX / 8) << 8) + (regionY / 8);
		if (height >= 4)
			height = 0;
		else if(height == -1 || Location.inLocation(x, y, Location.PURO_PURO))
			return 0;
		if(RegionClipping.regionsList.containsKey(regionId))
			return RegionClipping.regionsList.get(regionId).getClip(x, y, height);
		return 0;
	}

	private int getClip(int x, int y, int height) {
		//height %= 4;
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;
		loadRegion(x, y);
		if (clips[height] == null) {
			clips[height] = new int[64][64];
		}
		return clips[height][x - regionAbsX][y - regionAbsY];
	}

	public static boolean canMove(int startX, int startY, int endX, int endY,
			int height, int xLength, int yLength) {
		int diffX = endX - startX;
		int diffY = endY - startY;
		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int ii = 0; ii < max; ii++) {
			int currentX = endX - diffX;
			int currentY = endY - diffY;
			for (int i = 0; i < xLength; i++) {
				for (int i2 = 0; i2 < yLength; i2++)
					if (diffX < 0 && diffY < 0) {
						if ((getClipping((currentX + i) - 1,
								(currentY + i2) - 1, height) & 0x128010e) != 0
								|| (getClipping((currentX + i) - 1, currentY
										+ i2, height) & 0x1280108) != 0
										|| (getClipping(currentX + i,
												(currentY + i2) - 1, height) & 0x1280102) != 0)
							return false;
					} else if (diffX > 0 && diffY > 0) {
						if ((getClipping(currentX + i + 1, currentY + i2 + 1,
								height) & 0x12801e0) != 0
								|| (getClipping(currentX + i + 1,
										currentY + i2, height) & 0x1280180) != 0
										|| (getClipping(currentX + i,
												currentY + i2 + 1, height) & 0x1280120) != 0)
							return false;
					} else if (diffX < 0 && diffY > 0) {
						if ((getClipping((currentX + i) - 1, currentY + i2 + 1,
								height) & 0x1280138) != 0
								|| (getClipping((currentX + i) - 1, currentY
										+ i2, height) & 0x1280108) != 0
										|| (getClipping(currentX + i,
												currentY + i2 + 1, height) & 0x1280120) != 0)
							return false;
					} else if (diffX > 0 && diffY < 0) {
						if ((getClipping(currentX + i + 1, (currentY + i2) - 1,
								height) & 0x1280183) != 0
								|| (getClipping(currentX + i + 1,
										currentY + i2, height) & 0x1280180) != 0
										|| (getClipping(currentX + i,
												(currentY + i2) - 1, height) & 0x1280102) != 0)
							return false;
					} else if (diffX > 0 && diffY == 0) {
						if ((getClipping(currentX + i + 1, currentY + i2,
								height) & 0x1280180) != 0)
							return false;
					} else if (diffX < 0 && diffY == 0) {
						if ((getClipping((currentX + i) - 1, currentY + i2,
								height) & 0x1280108) != 0)
							return false;
					} else if (diffX == 0 && diffY > 0) {
						if ((getClipping(currentX + i, currentY + i2 + 1,
								height) & 0x1280120) != 0)
							return false;
					} else if (diffX == 0
							&& diffY < 0
							&& (getClipping(currentX + i, (currentY + i2) - 1,
									height) & 0x1280102) != 0)
						return false;

			}

			if (diffX < 0)
				diffX++;
			else if (diffX > 0)
				diffX--;
			if (diffY < 0)
				diffY++;
			else if (diffY > 0)
				diffY--;
		}

		return true;
	}

	public static boolean canMove(Position start, Position end, int xLength, int yLength) {
		return canMove(start.getX(), start.getY(), end.getX(), end.getY(), start.getZ(), xLength, yLength);
	}

	public static boolean blockedProjectile(Position position) {
		return (getClipping(position.getX(), position.getY(), position.getZ()) & 0x20000) == 0;
	}

	public static boolean blockedNorth(Position pos)
	{
		return (getClipping(pos.getX(), pos.getY() + 1, pos.getZ()) & 0x1280120) != 0;
	}

	public static boolean blockedEast(Position pos)
	{
		return (getClipping(pos.getX() + 1, pos.getY(), pos.getZ()) & 0x1280180) != 0;
	}

	public static boolean blockedSouth(Position pos)
	{
		return (getClipping(pos.getX(), pos.getY() - 1, pos.getZ()) & 0x1280102) != 0;
	}

	public static boolean blockedWest(Position pos)
	{
		return (getClipping(pos.getX() - 1, pos.getY(), pos.getZ()) & 0x1280108) != 0;
	}

	public static boolean blockedNorthEast(Position pos)
	{
		return (getClipping(pos.getX() + 1, pos.getY() + 1, pos.getZ()) & 0x12801e0) != 0;
	}

	public static boolean blockedNorthWest(Position pos)
	{
		return (getClipping(pos.getX() - 1, pos.getY() + 1, pos.getZ()) & 0x1280138) != 0;
	}

	public static boolean blockedSouthEast(Position pos)
	{
		return (getClipping(pos.getX() + 1, pos.getY() - 1, pos.getZ()) & 0x1280183) != 0;
	}

	public static boolean blockedSouthWest(Position pos)
	{
		return (getClipping(pos.getX() - 1, pos.getY() - 1, pos.getZ()) & 0x128010e) != 0;
	}

	public static boolean canProjectileAttack(GameCharacter a, GameCharacter b) {
		if (!a.isPlayer()) {
			if (b.isPlayer()) {
				return canProjectileMove(b.getPosition().getX(), b.getPosition().getY(), a.getPosition().getX(), a.getPosition().getY(), a.getPosition().getZ(), 1, 1);
			}
		}
		return canProjectileMove(a.getPosition().getX(), a.getPosition().getY(), b.getPosition().getX(), b.getPosition().getY(), a.getPosition().getZ(), 1, 1);
	}

	public static boolean canProjectileMove(int startX, int startY, int endX, int endY, int height, int xLength, int yLength) {
		int diffX = endX - startX;
		int diffY = endY - startY;
		//height %= 4;
		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int ii = 0; ii < max; ii++) {
			int currentX = endX - diffX;
			int currentY = endY - diffY;
			for (int i = 0; i < xLength; i++) {
				for (int i2 = 0; i2 < yLength; i2++) {
					if (diffX < 0 && diffY < 0) {
						if ((RegionClipping.getClipping(currentX + i - 1, currentY + i2 - 1, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_EAST_BLOCKED | PROJECTILE_NORTH_EAST_BLOCKED | PROJECTILE_NORTH_BLOCKED)) != 0 || (RegionClipping.getClipping(currentX + i - 1, currentY + i2, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_EAST_BLOCKED)) != 0 || (RegionClipping.getClipping(currentX + i, currentY + i2 - 1, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_NORTH_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX > 0 && diffY > 0) {
						if ((RegionClipping.getClipping(currentX + i + 1, currentY + i2 + 1, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_WEST_BLOCKED | PROJECTILE_SOUTH_WEST_BLOCKED | PROJECTILE_SOUTH_BLOCKED)) != 0 || (RegionClipping.getClipping(currentX + i + 1, currentY + i2, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_WEST_BLOCKED)) != 0 || (RegionClipping.getClipping(currentX + i, currentY + i2 + 1, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_SOUTH_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX < 0 && diffY > 0) {
						if ((RegionClipping.getClipping(currentX + i - 1, currentY + i2 + 1, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_SOUTH_BLOCKED | PROJECTILE_SOUTH_EAST_BLOCKED | PROJECTILE_EAST_BLOCKED)) != 0 || (RegionClipping.getClipping(currentX + i - 1, currentY + i2, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_EAST_BLOCKED)) != 0 || (RegionClipping.getClipping(currentX + i, currentY + i2 + 1, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_SOUTH_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX > 0 && diffY < 0) {
						if ((RegionClipping.getClipping(currentX + i + 1, currentY + i2 - 1, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_WEST_BLOCKED | PROJECTILE_NORTH_BLOCKED | PROJECTILE_NORTH_WEST_BLOCKED)) != 0 || (RegionClipping.getClipping(currentX + i + 1, currentY + i2, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_WEST_BLOCKED)) != 0 || (RegionClipping.getClipping(currentX + i, currentY + i2 - 1, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_NORTH_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX > 0 && diffY == 0) {
						if ((RegionClipping.getClipping(currentX + i + 1, currentY + i2, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_WEST_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX < 0 && diffY == 0) {
						if ((RegionClipping.getClipping(currentX + i - 1, currentY + i2, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_EAST_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX == 0 && diffY > 0) {
						if ((RegionClipping.getClipping(currentX + i, currentY + i2 + 1, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_SOUTH_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX == 0 && diffY < 0) {
						if ((RegionClipping.getClipping(currentX + i, currentY + i2 - 1, height) & (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_NORTH_BLOCKED)) != 0) {
							return false;
						}
					}
				}
			}
			if (diffX < 0) {
				diffX++;
			} else if (diffX > 0) {
				diffX--;
			}
			if (diffY < 0) {
				diffY++;                       //change
			} else if (diffY > 0) {
				diffY--;
			}
		}
		return true;
	}
	
	public final static boolean isInDiagonalBlock(GameCharacter attacked, GameCharacter attacker) {
		return attacked.getPosition().getX() - 1 == attacker.getPosition().getX()
				&& attacked.getPosition().getY() + 1 == attacker.getPosition().getY()
				|| attacker.getPosition().getX() - 1 == attacked.getPosition().getX()
				&& attacker.getPosition().getY() + 1 == attacked.getPosition().getY()
				|| attacked.getPosition().getX() + 1 == attacker.getPosition().getX()
				&& attacked.getPosition().getY() - 1 == attacker.getPosition().getY()
				|| attacker.getPosition().getX() + 1 == attacked.getPosition().getX()
				&& attacker.getPosition().getY() - 1 == attacked.getPosition().getY()
				|| attacked.getPosition().getX() + 1 == attacker.getPosition().getX()
				&& attacked.getPosition().getY() + 1 == attacker.getPosition().getY()
				|| attacker.getPosition().getX() + 1 == attacked.getPosition().getX()
				&& attacker.getPosition().getY() + 1 == attacked.getPosition().getY();
	}

	public static final int PROJECTILE_NORTH_WEST_BLOCKED = 0x200;
	public static final int PROJECTILE_NORTH_BLOCKED = 0x400;
	public static final int PROJECTILE_NORTH_EAST_BLOCKED = 0x800;
	public static final int PROJECTILE_EAST_BLOCKED = 0x1000;
	public static final int PROJECTILE_SOUTH_EAST_BLOCKED = 0x2000;
	public static final int PROJECTILE_SOUTH_BLOCKED = 0x4000;
	public static final int PROJECTILE_SOUTH_WEST_BLOCKED = 0x8000;
	public static final int PROJECTILE_WEST_BLOCKED = 0x10000;
	public static final int PROJECTILE_TILE_BLOCKED = 0x20000;
	public static final int UNKNOWN = 0x80000;
	public static final int BLOCKED_TILE = 0x200000;
	public static final int UNLOADED_TILE = 0x1000000;
	public static final int OCEAN_TILE = 2097152;
}