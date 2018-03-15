

import java.io.BufferedWriter;
import java.io.FileWriter;

@SuppressWarnings("all")
final class ObjectManager {

	public int underlay_floor_texture;
	public int underlay_floor_map_color;
    public ObjectManager(byte abyte0[][][], int ai[][][])
    {
        highestPlane = 99;
        anInt146 = 104;
        anInt147 = 104;
        heightMap = ai;
        tileSettings = abyte0;
        underLay = new byte[4][anInt146][anInt147];
        overLay = new byte[4][anInt146][anInt147];
        overlayClippingPaths = new byte[4][anInt146][anInt147];
        overlayClippingPathRotations = new byte[4][anInt146][anInt147];
        anIntArrayArrayArray135 = new int[4][anInt146 + 1][anInt147 + 1];
        aByteArrayArrayArray134 = new byte[4][anInt146 + 1][anInt147 + 1];
        tileShadowArray = new int[anInt146 + 1][anInt147 + 1];
        anIntArray124 = new int[anInt147];
        anIntArray125 = new int[anInt147];
        anIntArray126 = new int[anInt147];
        anIntArray127 = new int[anInt147];
        anIntArray128 = new int[anInt147];
    }

    private static int method170(int i, int j)
    {
        int k = i + j * 57;
        k = k << 13 ^ k;
        int l = k * (k * k * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
        return l >> 19 & 0xff;
    }
    public final void method171(CollisionDetection aclass11[],
			WorldController worldController) {
		for (int j = 0; j < 4; j++) {
			for (int k = 0; k < 104; k++) {
				for (int i1 = 0; i1 < 104; i1++)
					if ((tileSettings[j][k][i1] & 1) == 1) {
						int k1 = j;
						if ((tileSettings[1][k][i1] & 2) == 2)
							k1--;
						if (k1 >= 0)
							aclass11[k1].appendSolidFlag(i1, k);
					}


			}


		}
		anIntArray124Offset += (int) (Math.random() * 5D) - 2;
		if (anIntArray124Offset < -8)
			anIntArray124Offset = -8;
		if (anIntArray124Offset > 8)
			anIntArray124Offset = 8;
		offsetLightning += (int) (Math.random() * 5D) - 2;
		if (offsetLightning < -16)
			offsetLightning = -16;
		if (offsetLightning > 16)
			offsetLightning = 16;
		for (int plane = 0; plane < 4; plane++) {
			byte abyte0[][] = aByteArrayArrayArray134[plane];
			byte byte0 = 96;
			char c = '\u0300';
			byte byte1 = -50;
			byte byte2 = -10;
			byte byte3 = -50;
			int j3 = (int) Math.sqrt(byte1 * byte1 + byte2 * byte2 + byte3
					* byte3);
			int l3 = c * j3 >> 8;
		for (int j4 = 1; j4 < anInt147 - 1; j4++) {
			for (int j5 = 1; j5 < anInt146 - 1; j5++) {
				int k6 = heightMap[plane][j5 + 1][j4]
				                                            - heightMap[plane][j5 - 1][j4];
				int l7 = heightMap[plane][j5][j4 + 1]
				                                        - heightMap[plane][j5][j4 - 1];
				int j9 = (int) Math.sqrt(k6 * k6 + 0x10000 + l7 * l7);
				int k12 = (k6 << 8) / j9;
				int l13 = 0x10000 / j9;
				int j15 = (l7 << 8) / j9;
				int j16 = byte0 + (byte1 * k12 + byte2 * l13 + byte3 * j15)
				/ l3;
				int j17 = (abyte0[j5 - 1][j4] >> 2)
				+ (abyte0[j5 + 1][j4] >> 3)
				+ (abyte0[j5][j4 - 1] >> 2)
				+ (abyte0[j5][j4 + 1] >> 3) + (abyte0[j5][j4] >> 1);
				tileShadowArray[j5][j4] = j16 - j17;
			}


		}


		for (int k5 = 0; k5 < anInt147; k5++) {
			anIntArray124[k5] = 0;
			anIntArray125[k5] = 0;
			anIntArray126[k5] = 0;
			anIntArray127[k5] = 0;
			anIntArray128[k5] = 0;
		}


		for (int x = -5; x < anInt146 + 5; x++) {
			for (int tileY = 0; tileY < anInt147; tileY++) {
				int tileX_ = x + 5;
				if (tileX_ >= 0 && tileX_ < anInt146) {
					int l12 = underLay[plane][tileX_][tileY] & 0xff;
					if (l12 > 0) {
						if(l12 >= Flo.cache.length)
							l12 = Flo.cache.length;
						Flo flo = Flo.cache[l12 - 1];
						anIntArray124[tileY] += flo.hue2;
						anIntArray125[tileY] += flo.saturation;
						anIntArray126[tileY] += flo.lightness;
						anIntArray127[tileY] += flo.hue_weight;
						anIntArray128[tileY]++;
					}
				}
				int tileX = x - 5;
				if (tileX >= 0 && tileX < anInt146) {
					int i14 = underLay[plane][tileX][tileY] & 0xff;
					if (i14 > 0) {
						if(i14 >= Flo.cache.length)
							i14 = Flo.cache.length;
						Flo flo_1 = Flo.cache[i14 - 1];
						anIntArray124[tileY] -= flo_1.hue2;
						anIntArray125[tileY] -= flo_1.saturation;
						anIntArray126[tileY] -= flo_1.lightness;
						anIntArray127[tileY] -= flo_1.hue_weight;
						anIntArray128[tileY]--;
					}
				}
			}


			if (x >= 1 && x < anInt146 - 1) {
				int blended_anIntArray124 = 0;
				int blended_anIntArray125 = 0;
				int blended_anIntArray126 = 0;
				int blended_anIntArray124_divisor = 0;
				int blend_direction_tracker = 0;
				for (int y = -5; y < anInt147 + 5; y++) {
					int j18 = y + 5;
					if (j18 >= 0 && j18 < anInt147) {
						blended_anIntArray124 += anIntArray124[j18];
						blended_anIntArray125 += anIntArray125[j18];
						blended_anIntArray126 += anIntArray126[j18];
						blended_anIntArray124_divisor += anIntArray127[j18];
						blend_direction_tracker += anIntArray128[j18];
					}
					int k18 = y - 5;
					if (k18 >= 0 && k18 < anInt147) {
						blended_anIntArray124 -= anIntArray124[k18];
						blended_anIntArray125 -= anIntArray125[k18];
						blended_anIntArray126 -= anIntArray126[k18];
						blended_anIntArray124_divisor -= anIntArray127[k18];
						blend_direction_tracker -= anIntArray128[k18];
					}
					if (y >= 1 && y < anInt147 - 1 && (!lowMem
						|| (tileSettings[0][x][y] & 2) != 0 || (tileSettings[plane][x][y] & 0x10) == 0
						&& method182(y, plane, x) == anInt131)) {
						if (plane < highestPlane)
							highestPlane = plane;
						int underlay_floor_id = underLay[plane][x][y] & 0xff;
						int overlay_floor_id = overLay[plane][x][y] & 0xff;
						if (underlay_floor_id > 0 || overlay_floor_id > 0) {
							int tileZA = heightMap[plane][x][y];
							int tileZB = heightMap[plane][x + 1][y];
							int tileZD = heightMap[plane][x + 1][y + 1];
							int tileZC = heightMap[plane][x][y + 1];
							int tileShadowA = tileShadowArray[x][y];
							int tileShadowB = tileShadowArray[x + 1][y];
							int tileShadowD = tileShadowArray[x + 1][y + 1];
							int tileShadowC = tileShadowArray[x][y + 1];
							int hsl_bitset_unmodified = -1;
							int hsl_bitset_randomized = -1;
							/*if (underlay_floor_id > 0) {
								int anIntArray124 = (blended_anIntArray124 * 256) / blended_anIntArray124_divisor;
								int sat = blended_anIntArray125 / blend_direction_tracker;
								int lum = blended_anIntArray126 / blend_direction_tracker;
								hsl_bitset_unmodified = method177(anIntArray124, sat, lum);
								anIntArray124 = anIntArray124 + anIntArray124Offset & 0xff;
								lum += offsetLightning;
								if (lum < 0)
									lum = 0;
								else if (lum > 255)
									lum = 255;
								hsl_bitset_randomized = method177(anIntArray124, sat, lum);
							}*/
							if(underlay_floor_id > 0  || overlay_floor_id != 0) 
							{
								int anIntArray124 = -1;
								int sat = 0;
								int lum = 0;
								if(underlay_floor_id == 0) 
								{
									anIntArray124 = -1;
									sat = 0;
									lum = 0;
								} else
								if (underlay_floor_id > 0) 
								{
									if(blended_anIntArray124_divisor < 1)
										blended_anIntArray124_divisor = 1;
										
									anIntArray124 = (blended_anIntArray124 << 8) / blended_anIntArray124_divisor;
									sat = blended_anIntArray125 / blend_direction_tracker;
									lum = blended_anIntArray126 / blend_direction_tracker;
									hsl_bitset_unmodified = method177(anIntArray124, sat, lum);
										anIntArray124 = anIntArray124 + anIntArray124Offset & 0xff;
										lum += offsetLightning;
										if (lum < 0)
											lum = 0;
											
										else if (lum > 255)
											lum = 255;
									
								} else
								{
									anIntArray124 = underlay_floor_id;
									sat = 0;
									lum = 0;
								}
								if(anIntArray124 != -1 && hsl_bitset_randomized == -1)
									hsl_bitset_randomized = method177(anIntArray124, sat, lum);
									
								if(hsl_bitset_unmodified == -1)
									hsl_bitset_unmodified = hsl_bitset_randomized;
								
							}
							if (plane > 0) {
								boolean flag = true;
								if (underlay_floor_id == 0
										&& overlayClippingPaths[plane][x][y] != 0)
									flag = false;
								if(overlay_floor_id >= OverLayFlo317.overLayFlo317s.length)
										overlay_floor_id = OverLayFlo317.overLayFlo317s.length-1;
								if (overlay_floor_id > 0
										&& !OverLayFlo317.overLayFlo317s[overlay_floor_id - 1].aBoolean393)
									flag = false;
								if (flag && tileZA == tileZB && tileZA == tileZD
										&& tileZA == tileZC)
									anIntArrayArrayArray135[plane][x][y] |= 0x924;
							}
							int rgb_bitset_randomized = 0;
							if (hsl_bitset_unmodified != -1)
								rgb_bitset_randomized = Rasterizer.hsl2rgb[getShadow(hsl_bitset_randomized,
										96)];
							if (overlay_floor_id == 0) {
								/**
								 * Draws shaped tile
								 */
								if(Client.getOption("hd_tex")) {

									if(underlay_floor_id-1 >= Flo.cache.length)
										underlay_floor_id = Flo.cache.length-1;
									Flo floor = Flo.cache[underlay_floor_id - 1];
									int underlay_texture_id = floor.texture;
									if (underlay_texture_id != -1)
										underlay_texture_id = 154; //632, 154
									underlay_floor_texture = underlay_texture_id;
									underlay_floor_map_color = getOverlayShadow(hsl_bitset_unmodified, 96);
									int tile_opcode = overlayClippingPaths[plane][x][y] + 1;
									if (tile_opcode == 1) {
										tile_opcode = 434;
									}
									byte tile_orientation = overlayClippingPathRotations[plane][x][y];
									/**
									 * Adds underlay tile
									 */
									int overlay_hsl = method177(floor.hue, floor.saturation, floor.lightness);
									worldController.addTile(plane, x, y, 
										tile_opcode, tile_orientation, underlay_texture_id, tileZA, tileZB, tileZD, tileZC, 
										getShadow(hsl_bitset_unmodified, tileShadowA), 
										getShadow(hsl_bitset_unmodified, tileShadowB),
										getShadow(hsl_bitset_unmodified, tileShadowD), 
										getShadow(hsl_bitset_unmodified, tileShadowC), 
										getOverlayShadow(overlay_hsl, tileShadowA), 
										getOverlayShadow(overlay_hsl, tileShadowB),
										getOverlayShadow(overlay_hsl, tileShadowD), 
										getOverlayShadow(overlay_hsl, tileShadowC),
										rgb_bitset_randomized, rgb_bitset_randomized, underlay_floor_map_color, 
										underlay_floor_texture, underlay_floor_map_color, false);
								} else {
									worldController.addTile(plane, x, y, 0, 0, -1, tileZA, tileZB, tileZD, tileZC, 
											getShadow(hsl_bitset_unmodified, tileShadowA), 
											getShadow(hsl_bitset_unmodified, tileShadowB), 
											getShadow(hsl_bitset_unmodified, tileShadowD),
											getShadow(hsl_bitset_unmodified, tileShadowC), 
											0, 
											0, 
											0, 
											0,
											rgb_bitset_randomized, rgb_bitset_randomized, -1, 0, 0, true);
								}
							} else {
								int overlay_map_color = 0;
								int overlay_floor_texture_color = -1;
								int tile_opcode = overlayClippingPaths[plane][x][y] + 1;
								byte tile_orientation = overlayClippingPathRotations[plane][x][y];
								if(overlay_floor_id-1 >= OverLayFlo317.overLayFlo317s.length)
									overlay_floor_id = OverLayFlo317.overLayFlo317s.length - 1;
								OverLayFlo317 over = OverLayFlo317.overLayFlo317s[overlay_floor_id - 1];
                                int overlay_texture_id = over.groundTextureOverlay;
                                int overlay_hsl;
                                int overlay_rgb = 0;
								if (overlay_texture_id > 50 && !Client.getOption("hd_tex")) {
									overlay_texture_id = -1;
								}
								if (overlay_texture_id >= 0) {
									if(overlay_texture_id < 51 || !Client.getOption("hd_tex"))
										overlay_rgb = TextureLoader317.getAverageTextureColour(overlay_texture_id);
                                    overlay_hsl = -1;
                                } else if (over.groundColorOverlay == 0xff00ff) {
                                    overlay_rgb = 0;
                                    overlay_hsl = -2;
                                    overlay_texture_id = -1;
                                } else if(over.groundColorOverlay == 0x333333) {
                                    overlay_rgb = Rasterizer.hsl2rgb[getOverlayShadow(over.hslOverlayColor, 96)];                                
                                    overlay_hsl = -2;
                                    overlay_texture_id = -1;
                                } else {
                                    overlay_hsl = method177(over.groundHueOverlay, over.groundSaturationOverlay, over.groundLightnessOverlay);
                                    overlay_rgb = Rasterizer.hsl2rgb[getOverlayShadow(over.hslOverlayColor, 96)];
                                }
								if ((overlay_floor_id-1) == 54) {
				                    overlay_rgb = over.groundColorOverlay = 0x8B8B83;
				                    overlay_hsl = -2;
				                }
								if((overlay_floor_id-1) == 111){
									overlay_rgb = TextureLoader317.getAverageTextureColour(1);
									overlay_hsl = -1;//method177(150,100,100);
                                    overlay_texture_id = 1;
                                } else if (overlay_hsl == 6363) { //river bank (brown shit) 508
									overlay_rgb = 0x483B21;
									overlay_hsl = method177(25,146,24);
                                } else if((overlay_floor_id-1) == 54){
									overlay_rgb = over.groundColorOverlay;
									overlay_hsl = -2;
                                    overlay_texture_id = -1;
                                }
								if(Client.getOption("hd_tex")) {
									if(over.detailedColor != -1) 
									{
										overlay_map_color = (Rasterizer.hsl2rgb[over.detailedColor] != 1) ? Rasterizer.hsl2rgb[over.detailedColor] : 0;
									}
									if ((!Client.getOption("hd_tex") ? (overlay_texture_id >= 0 && overlay_texture_id < 51) : (overlay_texture_id >= 0))) 
									{
										overlay_hsl = -1;
										if (over.groundColorOverlay != 0xff00ff) 
										{
											overlay_hsl = over.groundColorOverlay;
											if(overlay_texture_id > 50)
											overlay_rgb = (overlay_hsl != -1 ? Rasterizer.hsl2rgb[overlay_hsl] : 0);
											overlay_floor_texture_color = getOverlayShadow(over.groundColorOverlay, 96);
										} else 
										{
											if(overlay_texture_id > 50)
												overlay_rgb = over.detailedColor;
											overlay_hsl = -2;
											underlay_floor_map_color = -1;
											overlay_floor_texture_color = -1;
										}
									} else if (over.groundColorOverlay == -1) 
									{
										if(overlay_texture_id > 50)
										overlay_rgb = overlay_map_color;
										overlay_hsl = -2;
										//?
										if(plane > 0)
											underlay_floor_texture = -1;
											
										overlay_texture_id = -1;
									} else 
									{
										overlay_floor_texture_color = getOverlayShadow(over.groundColorOverlay, 96);
										overlay_hsl = over.groundColorOverlay;
										if(overlay_texture_id > 50)
										overlay_rgb = Rasterizer.hsl2rgb[overlay_floor_texture_color];
									}
								}
								if(Client.getOption("hd_tex")) {
									worldController.addTile(plane, x, y, 
										tile_opcode, tile_orientation, overlay_texture_id, tileZA, tileZB, tileZD, tileZC, 
										getShadow(hsl_bitset_unmodified, tileShadowA), getShadow(hsl_bitset_unmodified, tileShadowB),
										getShadow(hsl_bitset_unmodified, tileShadowD),getShadow(hsl_bitset_unmodified, tileShadowC),
										getOverlayShadow(overlay_hsl, tileShadowA), getOverlayShadow(overlay_hsl, tileShadowB),
										getOverlayShadow(overlay_hsl, tileShadowD), getOverlayShadow(overlay_hsl, tileShadowC),
										rgb_bitset_randomized, overlay_rgb, overlay_floor_texture_color, 
										underlay_floor_texture, underlay_floor_map_color, false);
								} else {
	                                worldController.addTile(plane, x, y, tile_opcode,
	                                        tile_orientation, overlay_texture_id, tileZA, tileZB, tileZD, tileZC,
	                                        getShadow(hsl_bitset_unmodified, tileShadowA), getShadow(hsl_bitset_unmodified,
	                                                tileShadowB), getShadow(hsl_bitset_unmodified, tileShadowD),
	                                                getShadow(hsl_bitset_unmodified, tileShadowC), getOverlayShadow(overlay_hsl,
	                                                        tileShadowA), getOverlayShadow(overlay_hsl, tileShadowB),
	                                                        getOverlayShadow(overlay_hsl, tileShadowD), getOverlayShadow(overlay_hsl,
	                                                                tileShadowC), rgb_bitset_randomized, overlay_rgb, -1, 0 ,0, true);
									}
                                }
						}
					}
				}


			}
		}


		for (int j8 = 1; j8 < anInt147 - 1; j8++) {
			for (int i10 = 1; i10 < anInt146 - 1; i10++)
				worldController.setVisiblePlanesFor(plane, i10, j8, method182(j8, plane, i10));


		}


		}


		worldController.shadeModels(-10, -50, -50);
		for (int j1 = 0; j1 < anInt146; j1++) {
			for (int l1 = 0; l1 < anInt147; l1++)
				if ((tileSettings[1][j1][l1] & 2) == 2)
					worldController.applyBridgeMode(l1, j1);


		}


		int i2 = 1;
		int j2 = 2;
		int k2 = 4;
		for (int l2 = 0; l2 < 4; l2++) {
			if (l2 > 0) {
				i2 <<= 3;
				j2 <<= 3;
				k2 <<= 3;
			}
			for (int i3 = 0; i3 <= l2; i3++) {
				for (int k3 = 0; k3 <= anInt147; k3++) {
					for (int i4 = 0; i4 <= anInt146; i4++) {
						if ((anIntArrayArrayArray135[i3][i4][k3] & i2) != 0) {
							int k4 = k3;
							int l5 = k3;
							int i7 = i3;
							int k8 = i3;
							for (; k4 > 0
							&& (anIntArrayArrayArray135[i3][i4][k4 - 1] & i2) != 0; k4--)
								;
							for (; l5 < anInt147
							&& (anIntArrayArrayArray135[i3][i4][l5 + 1] & i2) != 0; l5++)
								;
							label0: for (; i7 > 0; i7--) {
								for (int j10 = k4; j10 <= l5; j10++)
									if ((anIntArrayArrayArray135[i7 - 1][i4][j10] & i2) == 0)
										break label0;


							}


							label1: for (; k8 < l2; k8++) {
								for (int k10 = k4; k10 <= l5; k10++)
									if ((anIntArrayArrayArray135[k8 + 1][i4][k10] & i2) == 0)
										break label1;


							}


							int l10 = ((k8 + 1) - i7) * ((l5 - k4) + 1);
							if (l10 >= 8) {
								char c1 = '\360';
								int k14 = heightMap[k8][i4][k4]
								                                          - c1;
								int l15 = heightMap[i7][i4][k4];
								WorldController.createCullingCluster(l2, i4 * 128, l15,
										i4 * 128, l5 * 128 + 128, k14,
										k4 * 128, 1);
								for (int l16 = i7; l16 <= k8; l16++) {
									for (int l17 = k4; l17 <= l5; l17++)
										anIntArrayArrayArray135[l16][i4][l17] &= ~i2;


								}


							}
						}
						if ((anIntArrayArrayArray135[i3][i4][k3] & j2) != 0) {
							int l4 = i4;
							int i6 = i4;
							int j7 = i3;
							int l8 = i3;
							for (; l4 > 0
							&& (anIntArrayArrayArray135[i3][l4 - 1][k3] & j2) != 0; l4--)
								;
							for (; i6 < anInt146
							&& (anIntArrayArrayArray135[i3][i6 + 1][k3] & j2) != 0; i6++)
								;
							label2: for (; j7 > 0; j7--) {
								for (int i11 = l4; i11 <= i6; i11++)
									if ((anIntArrayArrayArray135[j7 - 1][i11][k3] & j2) == 0)
										break label2;


							}


							label3: for (; l8 < l2; l8++) {
								for (int j11 = l4; j11 <= i6; j11++)
									if ((anIntArrayArrayArray135[l8 + 1][j11][k3] & j2) == 0)
										break label3;


							}


							int k11 = ((l8 + 1) - j7) * ((i6 - l4) + 1);
							if (k11 >= 8) {
								char c2 = '\360';
								int l14 = heightMap[l8][l4][k3]
								                                          - c2;
								int i16 = heightMap[j7][l4][k3];
								WorldController.createCullingCluster(l2, l4 * 128, i16,
										i6 * 128 + 128, k3 * 128, l14,
										k3 * 128, 2);
								for (int i17 = j7; i17 <= l8; i17++) {
									for (int i18 = l4; i18 <= i6; i18++)
										anIntArrayArrayArray135[i17][i18][k3] &= ~j2;


								}


							}
						}
						if ((anIntArrayArrayArray135[i3][i4][k3] & k2) != 0) {
							int i5 = i4;
							int j6 = i4;
							int k7 = k3;
							int i9 = k3;
							for (; k7 > 0
							&& (anIntArrayArrayArray135[i3][i4][k7 - 1] & k2) != 0; k7--)
								;
							for (; i9 < anInt147
							&& (anIntArrayArrayArray135[i3][i4][i9 + 1] & k2) != 0; i9++)
								;
							label4: for (; i5 > 0; i5--) {
								for (int l11 = k7; l11 <= i9; l11++)
									if ((anIntArrayArrayArray135[i3][i5 - 1][l11] & k2) == 0)
										break label4;


							}


							label5: for (; j6 < anInt146; j6++) {
								for (int i12 = k7; i12 <= i9; i12++)
									if ((anIntArrayArrayArray135[i3][j6 + 1][i12] & k2) == 0)
										break label5;


							}


							if (((j6 - i5) + 1) * ((i9 - k7) + 1) >= 4) {
								int j12 = heightMap[i3][i5][k7];
								WorldController.createCullingCluster(l2, i5 * 128, j12,
										j6 * 128 + 128, i9 * 128 + 128, j12,
										k7 * 128, 4);
								for (int k13 = i5; k13 <= j6; k13++) {
									for (int i15 = k7; i15 <= i9; i15++)
										anIntArrayArrayArray135[i3][k13][i15] &= ~k2;


								}
							}
						}
					}
				}
			}
		}
	}
	

    private static int generateMapHeight(int i, int j)
    {
        int k = (method176(i + 45365, j + 0x16713, 4) - 128) + (method176(i + 10294, j + 37821, 2) - 128 >> 1) + (method176(i, j, 1) - 128 >> 2);
        k = (int)((double)k * 0.29999999999999999D) + 35;
        if(k < 10)
            k = 10;
        else
        if(k > 60)
            k = 60;
        return k;
    }

    public static void method173(Stream stream, OnDemandFetcher fetcher)
    {
    	label0:
        {
            int i = -1;
            do
            {
                int j = stream.readUSmart2();
                if(j == 0)
                    break label0;
                i += j;
                System.out.println(i);
                ObjectDef class46 = ObjectDef.forID(i);
                class46.method574(fetcher);
                do
                {
                    int k = stream.method422();
                    if(k == 0)
                        break;
                    stream.readUnsignedByte();
                } while(true);
            } while(true);
        }
    }

    public final void method174(int i, int j, int l, int i1)
    {
        for(int j1 = i; j1 <= i + j; j1++)
        {
            for(int k1 = i1; k1 <= i1 + l; k1++)
                if(k1 >= 0 && k1 < anInt146 && j1 >= 0 && j1 < anInt147)
                {
                    aByteArrayArrayArray134[0][k1][j1] = 127;
                    if(k1 == i1 && k1 > 0)
                        heightMap[0][k1][j1] = heightMap[0][k1 - 1][j1];
                    if(k1 == i1 + l && k1 < anInt146 - 1)
                        heightMap[0][k1][j1] = heightMap[0][k1 + 1][j1];
                    if(j1 == i && j1 > 0)
                        heightMap[0][k1][j1] = heightMap[0][k1][j1 - 1];
                    if(j1 == i + j && j1 < anInt147 - 1)
                        heightMap[0][k1][j1] = heightMap[0][k1][j1 + 1];
                }

        }
    }
	
		public static void write(String text, String file) {
		BufferedWriter bw = null;
		try {
			FileWriter fileWriter = new FileWriter(file + ".txt", true);
			bw = new BufferedWriter(fileWriter);
			bw.write(text);
			bw.newLine();
			bw.flush();
			bw.close();
			fileWriter = null;
			bw = null;
		} catch (Exception exception) {
			System.out.println("Critical error while writing data: " + file);
			exception.printStackTrace();
		}
	}

    public void method175(int y, WorldController worldController, CollisionDetection class11, int j, int z, int x, int i1, int j1) {
		try {
    	if (lowMem && (tileSettings[0][x][y] & 2) == 0) {
			if ((tileSettings[z][x][y] & 0x10) != 0)
				return;
			if (method182(y, z, x) != anInt131)
				return;
		}
		if (z < highestPlane)
			highestPlane = z;
		int k1 = heightMap[z][x][y];
		int l1 = heightMap[z][x + 1][y];
		int i2 = heightMap[z][x + 1][y + 1];
		int j2 = heightMap[z][x][y + 1];
		int k2 = k1 + l1 + i2 + j2 >> 2;
		ObjectDef objectDef = ObjectDef.forID(i1);
		int l2 = x + (y << 7) + ((i1 > 0x7fff ? i1 & 0x7fff : i1) << 14) + 0x40000000;
		if (!objectDef.hasActions)
			l2 += 0x80000000;
		byte byte0 = (byte) ((j1 << 6) + j);
		if (j == 22) {
			if (lowMem && !objectDef.hasActions && !objectDef.aBoolean736)
				return;
			Object obj;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null)
				obj = objectDef.renderObject(22, j1, k1, l1, i2, j2, -1);
			else
				obj = new ObjectOnTile(i1, j1, 22, l1, i2, k1, j2, objectDef.animationID, true);
			worldController.addGroundDecoration(z, k2, y, ((Animable) (obj)), byte0, l2, x, i1);
			if (objectDef.isUnwalkable && objectDef.hasActions && class11 != null)
				class11.appendSolidFlag(y, x);
			return;
		}
		if (j == 10 || j == 11) {
			Object obj1;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null)
				obj1 = objectDef.renderObject(10, j1, k1, l1, i2, j2, -1);
			else
				obj1 = new ObjectOnTile(i1, j1, 10, l1, i2, k1, j2, objectDef.animationID, true);
			if (obj1 != null) {
				int i5 = 0;
				if (j == 11)
					i5 += 256;
				int j4;
				int l4;
				if (j1 == 1 || j1 == 3) {
					j4 = objectDef.sizeY;
					l4 = objectDef.sizeX;
				} else {
					j4 = objectDef.sizeX;
					l4 = objectDef.sizeY;
				}
				if (worldController.addInteractableEntity(l2, byte0, k2, l4, ((Animable) (obj1)), 
						j4, z, i5, y, x, i1) && objectDef.aBoolean779) {
					Model model;
					if (obj1 instanceof Model)
						model = (Model) obj1;
					else
						model = objectDef.renderObject(10, j1, k1, l1, i2, j2, -1);
					if (model != null) {
						for (int j5 = 0; j5 <= j4; j5++) {
							for (int k5 = 0; k5 <= l4; k5++) {
								int l5 = model.anInt1650 / 4;
								if (l5 > 30)
									l5 = 30;
								if (l5 > aByteArrayArrayArray134[z][x + j5][y + k5])
									aByteArrayArrayArray134[z][x + j5][y + k5] = (byte) l5;
							}

						}

					}
				}
			}
			if (objectDef.isUnwalkable && class11 != null)
				class11.method212(objectDef.aBoolean757, objectDef.sizeX, objectDef.sizeY, x, y, j1);
			return;
		}
		if (j >= 12) {
			Object obj2;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null)
				obj2 = objectDef.renderObject(j, j1, k1, l1, i2, j2, -1);
			else
				obj2 = new ObjectOnTile(i1, j1, j, l1, i2, k1, j2, objectDef.animationID, true);
			worldController.addInteractableEntity(l2, byte0, k2, 1, ((Animable) (obj2)), 1, z, 0, y, x, i1);
			if (j >= 12 && j <= 17 && j != 13 && z > 0)
				anIntArrayArrayArray135[z][x][y] |= 0x924;
			if (objectDef.isUnwalkable && class11 != null)
				class11.method212(objectDef.aBoolean757, objectDef.sizeX, objectDef.sizeY, x, y, j1);
			return;
		}
		if (j == 0) {
			Object obj3;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null)
				obj3 = objectDef.renderObject(0, j1, k1, l1, i2, j2, -1);
			else
				obj3 = new ObjectOnTile(i1, j1, 0, l1, i2, k1, j2, objectDef.animationID, true);
			worldController.addWallObject(anIntArray152[j1], ((Animable) (obj3)), l2, y, byte0, x, null, k2, 0, z, i1);
			if (j1 == 0) {
				if (objectDef.aBoolean779) {
					aByteArrayArrayArray134[z][x][y] = 50;
					aByteArrayArrayArray134[z][x][y + 1] = 50;
				}
				if (objectDef.aBoolean764)
					anIntArrayArrayArray135[z][x][y] |= 0x249;
			} else if (j1 == 1) {
				if (objectDef.aBoolean779) {
					aByteArrayArrayArray134[z][x][y + 1] = 50;
					aByteArrayArrayArray134[z][x + 1][y + 1] = 50;
				}
				if (objectDef.aBoolean764)
					anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
			} else if (j1 == 2) {
				if (objectDef.aBoolean779) {
					aByteArrayArrayArray134[z][x + 1][y] = 50;
					aByteArrayArrayArray134[z][x + 1][y + 1] = 50;
				}
				if (objectDef.aBoolean764)
					anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
			} else if (j1 == 3) {
				if (objectDef.aBoolean779) {
					aByteArrayArrayArray134[z][x][y] = 50;
					aByteArrayArrayArray134[z][x + 1][y] = 50;
				}
				if (objectDef.aBoolean764)
					anIntArrayArrayArray135[z][x][y] |= 0x492;
			}
			if (objectDef.isUnwalkable && class11 != null)
				class11.method211(y, j1, x, j, objectDef.aBoolean757);
			if (objectDef.anInt775 != 16)
				worldController.moveWallDec(y, objectDef.anInt775, x, z);
			return;
		}
		if (j == 1) {
			Object obj4;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null)
				obj4 = objectDef.renderObject(1, j1, k1, l1, i2, j2, -1);
			else
				obj4 = new ObjectOnTile(i1, j1, 1, l1, i2, k1, j2, objectDef.animationID, true);
			worldController.addWallObject(anIntArray140[j1], ((Animable) (obj4)), l2, y, byte0, x, null, k2, 0, z, i1);
			if (objectDef.aBoolean779)
				if (j1 == 0)
					aByteArrayArrayArray134[z][x][y + 1] = 50;
				else if (j1 == 1)
					aByteArrayArrayArray134[z][x + 1][y + 1] = 50;
				else if (j1 == 2)
					aByteArrayArrayArray134[z][x + 1][y] = 50;
				else if (j1 == 3)
					aByteArrayArrayArray134[z][x][y] = 50;
			if (objectDef.isUnwalkable && class11 != null)
				class11.method211(y, j1, x, j, objectDef.aBoolean757);
			return;
		}
		if (j == 2) {
			int i3 = j1 + 1 & 3;
			Object obj11;
			Object obj12;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null) {
				obj11 = objectDef.renderObject(2, 4 + j1, k1, l1, i2, j2, -1);
				obj12 = objectDef.renderObject(2, i3, k1, l1, i2, j2, -1);
			} else {
				obj11 = new ObjectOnTile(i1, 4 + j1, 2, l1, i2, k1, j2, objectDef.animationID, true);
				obj12 = new ObjectOnTile(i1, i3, 2, l1, i2, k1, j2, objectDef.animationID, true);
			}
			worldController.addWallObject(anIntArray152[j1], ((Animable) (obj11)), l2, y, byte0, x, ((Animable) (obj12)), k2, anIntArray152[i3], z, i1);
			if (objectDef.aBoolean764)
				if (j1 == 0) {
					anIntArrayArrayArray135[z][x][y] |= 0x249;
					anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
				} else if (j1 == 1) {
					anIntArrayArrayArray135[z][x][y + 1] |= 0x492;
					anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
				} else if (j1 == 2) {
					anIntArrayArrayArray135[z][x + 1][y] |= 0x249;
					anIntArrayArrayArray135[z][x][y] |= 0x492;
				} else if (j1 == 3) {
					anIntArrayArrayArray135[z][x][y] |= 0x492;
					anIntArrayArrayArray135[z][x][y] |= 0x249;
				}
			if (objectDef.isUnwalkable && class11 != null)
				class11.method211(y, j1, x, j, objectDef.aBoolean757);
			if (objectDef.anInt775 != 16)
				worldController.moveWallDec(y, objectDef.anInt775, x, z);
			return;
		}
		if (j == 3) {
			Object obj5;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null)
				obj5 = objectDef.renderObject(3, j1, k1, l1, i2, j2, -1);
			else
				obj5 = new ObjectOnTile(i1, j1, 3, l1, i2, k1, j2, objectDef.animationID, true);
			worldController.addWallObject(anIntArray140[j1], ((Animable) (obj5)), l2, y, byte0, x, null, k2, 0, z, i1);
			if (objectDef.aBoolean779)
				if (j1 == 0)
					aByteArrayArrayArray134[z][x][y + 1] = 50;
				else if (j1 == 1)
					aByteArrayArrayArray134[z][x + 1][y + 1] = 50;
				else if (j1 == 2)
					aByteArrayArrayArray134[z][x + 1][y] = 50;
				else if (j1 == 3)
					aByteArrayArrayArray134[z][x][y] = 50;
			if (objectDef.isUnwalkable && class11 != null)
				class11.method211(y, j1, x, j, objectDef.aBoolean757);
			return;
		}
		if (j == 9) {
			Object obj6;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null)
				obj6 = objectDef.renderObject(j, j1, k1, l1, i2, j2, -1);
			else
				obj6 = new ObjectOnTile(i1, j1, j, l1, i2, k1, j2, objectDef.animationID, true);
			worldController.addInteractableEntity(l2, byte0, k2, 1, ((Animable) (obj6)), 1, z, 0, y, x, i1);
			if (objectDef.isUnwalkable && class11 != null)
				class11.method212(objectDef.aBoolean757, objectDef.sizeX, objectDef.sizeY, x, y, j1);
			return;
		}
		if (objectDef.adjustToTerrain)
			if (j1 == 1) {
				int j3 = j2;
				j2 = i2;
				i2 = l1;
				l1 = k1;
				k1 = j3;
			} else if (j1 == 2) {
				int k3 = j2;
				j2 = l1;
				l1 = k3;
				k3 = i2;
				i2 = k1;
				k1 = k3;
			} else if (j1 == 3) {
				int l3 = j2;
				j2 = k1;
				k1 = l1;
				l1 = i2;
				i2 = l3;
			}
		if (j == 4) {
			Object obj7;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null)
				obj7 = objectDef.renderObject(4, 0, k1, l1, i2, j2, -1);
			else
				obj7 = new ObjectOnTile(i1, 0, 4, l1, i2, k1, j2, objectDef.animationID, true);
			worldController.addWallDecoration(l2, y, j1 * 512, z, 0, k2, ((Animable) (obj7)), x, byte0, 0, anIntArray152[j1], i1);
			return;
		}
		if (j == 5) {
			int i4 = 16;
			int k4 = worldController.getWallObjectUID(z, x, y);
			if (k4 > 0)
				i4 = ObjectDef.forID(k4 >> 14 & 0x7fff).anInt775;
			Object obj13;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null)
				obj13 = objectDef.renderObject(4, 0, k1, l1, i2, j2, -1);
			else
				obj13 = new ObjectOnTile(i1, 0, 4, l1, i2, k1, j2, objectDef.animationID, true);
			worldController.addWallDecoration(l2, y, j1 * 512, z, anIntArray137[j1] * i4, k2, ((Animable) (obj13)), x, byte0, anIntArray144[j1]  * i4, anIntArray152[j1], i1);
			return;
		}
		if (j == 6) {
			Object obj8;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null)
				obj8 = objectDef.renderObject(4, 0, k1, l1, i2, j2, -1);
			else
				obj8 = new ObjectOnTile(i1, 0, 4, l1, i2, k1, j2, objectDef.animationID, true);
			worldController.addWallDecoration(l2, y, j1, z, 0, k2, ((Animable) (obj8)), x, byte0, 0, 256, i1);
			return;
		}
		if (j == 7) {
			Object obj9;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null)
				obj9 = objectDef.renderObject(4, 0, k1, l1, i2, j2, -1);
			else
				obj9 = new ObjectOnTile(i1, 0, 4, l1, i2, k1, j2, objectDef.animationID, true);
			worldController.addWallDecoration(l2, y, j1, z, 0, k2, ((Animable) (obj9)), x, byte0, 0, 512, i1);
			return;
		}
		if (j == 8) {
			Object obj10;
			if (objectDef.animationID == -1 && objectDef.configObjectIDs == null)
				obj10 = objectDef.renderObject(4, 0, k1, l1, i2, j2, -1);
			else
				obj10 = new ObjectOnTile(i1, 0, 4, l1, i2, k1, j2, objectDef.animationID, true);
			worldController.addWallDecoration(l2, y, j1, z, 0, k2, ((Animable) (obj10)), x, byte0, 0, 768, i1);
		}
		} catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Error placing object (out of map boundaries)");
		}
	}

    private static int method176(int i, int j, int k)
    {
        int l = i / k;
        int i1 = i & k - 1;
        int j1 = j / k;
        int k1 = j & k - 1;
        int l1 = method186(l, j1);
        int i2 = method186(l + 1, j1);
        int j2 = method186(l, j1 + 1);
        int k2 = method186(l + 1, j1 + 1);
        int l2 = method184(l1, i2, i1, k);
        int i3 = method184(j2, k2, i1, k);
        return method184(l2, i3, k1, k);
    }

    private int method177(int i, int j, int k)
    {
        if(k > 179)
            j /= 2;
        if(k > 192)
            j /= 2;
        if(k > 217)
            j /= 2;
        if(k > 243)
            j /= 2;
        return (i / 4 << 10) + (j / 32 << 7) + k / 2;
    }

    public static boolean isObjectModelCached(int i, int j)
    {
        ObjectDef class46 = ObjectDef.forID(i);
        if(j == 11)
            j = 10;
        if(j >= 5 && j <= 8)
            j = 4;
        return class46.allModelsFetched(j);
    }

    public final void loadMapChunk(int reqPlane, int rotation, CollisionDetection clips[], int baseX, int baseTileXChunk, byte mapData[],
                                int baseTileYChunk, int myPlane, int baseY)
    {
    	/**
    	 * Apply clipping
    	 */
        for(int xTile = 0; xTile < 8; xTile++)
        {
            for(int yTile = 0; yTile < 8; yTile++)
                if(baseX + xTile > 0 && baseX + xTile < 103 && baseY + yTile > 0 && baseY + yTile < 103)
                    clips[myPlane].clipData[baseX + xTile][baseY + yTile] &= 0xfeffffff;

        }
        
        Stream stream = new Stream(mapData);
        for(int plane = 0; plane < 4; plane++)
        {
            for(int xTile = 0; xTile < 64; xTile++)
            {
                for(int yTile = 0; yTile < 64; yTile++)
                    if(plane == reqPlane && xTile >= baseTileXChunk && xTile < baseTileXChunk + 8 && yTile >= baseTileYChunk && yTile < baseTileYChunk + 8)
                        readTile(baseY + MapUtility.getRotatedMapChunkY(yTile & 7, rotation, xTile & 7), 0, stream, baseX + MapUtility.getRotatedMapChunkX(rotation, yTile & 7, xTile & 7), myPlane, rotation, 0);
                    else
                        readTile(-1, 0, stream, -1, 0, 0, 0);

            }

        }

    }

    public final void method180(byte abyte0[], int i, int j, int k, int l, CollisionDetection aclass11[])
    {
        for(int i1 = 0; i1 < 4; i1++)
        {
            for(int j1 = 0; j1 < 64; j1++)
            {
                for(int k1 = 0; k1 < 64; k1++)
                    if(j + j1 > 0 && j + j1 < 103 && i + k1 > 0 && i + k1 < 103)
                        aclass11[i1].clipData[j + j1][i + k1] &= 0xfeffffff;

            }

        }

        Stream stream = new Stream(abyte0);
        for(int l1 = 0; l1 < 4; l1++)
        {
            for(int i2 = 0; i2 < 64; i2++)
            {
                for(int j2 = 0; j2 < 64; j2++)
                    readTile(j2 + i, l, stream, i2 + j, l1, 0, k);

            }

        }
    }

    private void readTile(int tileY, int j, Stream stream, int tileX, int tilePlane, int rotation,
                                 int k1)
    {
        if(tileX >= 0 && tileX < 104 && tileY >= 0 && tileY < 104)
        {
            tileSettings[tilePlane][tileX][tileY] = 0;
            do
            {
                int tileType = stream.readUnsignedByte();
                if(tileType == 0)
                    if(tilePlane == 0)
                    {
                        heightMap[0][tileX][tileY] = -generateMapHeight(0xe3b7b + tileX + k1, 0x87cce + tileY + j) * 8;
                        return;
                    } else
                    {
                        heightMap[tilePlane][tileX][tileY] = heightMap[tilePlane - 1][tileX][tileY] - 240;
                        return;
                    }
                if(tileType == 1)
                {
                    int j2 = stream.readUnsignedByte();
                    if(j2 == 1)
                        j2 = 0;
                    if(tilePlane == 0)
                    {
                        heightMap[0][tileX][tileY] = -j2 * 8;
                        return;
                    } else
                    {
                        heightMap[tilePlane][tileX][tileY] = heightMap[tilePlane - 1][tileX][tileY] - j2 * 8;
                        return;
                    }
                }
                if(tileType <= 49)
                {
                    overLay[tilePlane][tileX][tileY] = stream.readSignedByte();
                    overlayClippingPaths[tilePlane][tileX][tileY] = (byte)((tileType - 2) / 4);
                    overlayClippingPathRotations[tilePlane][tileX][tileY] = (byte)((tileType - 2) + rotation & 3);
                } else
                if(tileType <= 81)
                    tileSettings[tilePlane][tileX][tileY] = (byte)(tileType - 49);
                else
                    underLay[tilePlane][tileX][tileY] = (byte)(tileType - 81);
            } while(true);
        }
        do
        {
            int i2 = stream.readUnsignedByte();
            if(i2 == 0)
                break;
            if(i2 == 1)
            {
                stream.readUnsignedByte();
                return;
            }
            if(i2 <= 49)
                stream.readUnsignedByte();
        } while(true);
    }

    private int method182(int i, int j, int k)
    {
        if((tileSettings[j][k][i] & 8) != 0)
            return 0;
        if(j > 0 && (tileSettings[1][k][i] & 2) != 0)
            return j - 1;
        else
            return j;
    }

    public final void readObjectMap(CollisionDetection clippingPlanes[], WorldController worldController, int myPlane, int baseXTile, 
    		int baseYTileShifted, int reqPlane, byte mapData[], int baseXTileShifted, int rotation, int baseYTile)
    {
label0:
        {
            Stream stream = new Stream(mapData); //turns the .dat file into a stream.
            int foundObjectId = -1;
            do
            {
                int i2 = stream.readUSmart2(); //reads object id.
                if(i2 == 0)
                    break label0;
                foundObjectId += i2;
                int objectBits = 0;
                do
                {
                    int k2 = stream.method422(); //reads coordiantes x , y , z
                    if(k2 == 0)
                        break;
                    objectBits += k2 - 1;
                    int objectTileY = objectBits & 0x3f;
                    int objectTileX = objectBits >> 6 & 0x3f;
                    int objectPlane = objectBits >> 12;
                    int k3 = stream.readUnsignedByte(); //reads orienetation j & j1 
                    int type = k3 >> 2;
                    int objectRotation = k3 & 3;
                    if(objectPlane == myPlane && objectTileX >= baseXTileShifted && objectTileX < baseXTileShifted + 8 && objectTileY >= baseYTileShifted && objectTileY < baseYTileShifted + 8)
                    {
                        ObjectDef objectDef = ObjectDef.forID(foundObjectId);
                        int finalXTile = baseXTile + MapUtility.getRotatedLandscapeChunkX(rotation, (objectRotation == 0 || objectRotation == 2) ? objectDef.sizeY : objectDef.sizeX, objectTileX & 7, objectTileY & 7, (objectRotation == 0 || objectRotation == 2) ? objectDef.sizeX : objectDef.sizeY);
                        int finalYTile = baseYTile + MapUtility.getRotatedLandscapeChunkY(objectTileY & 7, (objectRotation == 0 || objectRotation == 2) ? objectDef.sizeY : objectDef.sizeX, rotation, (objectRotation == 0 || objectRotation == 2) ? objectDef.sizeX : objectDef.sizeY, objectTileX & 7);
                        if(finalXTile > 0 && finalYTile > 0 && finalXTile < 103 && finalYTile < 103)
                        {
                            int l4 = objectPlane;
                            if((tileSettings[1][finalXTile][finalYTile] & 2) == 2)
                                l4--;
                            CollisionDetection tileSetting = null;
                            if(l4 >= 0)
                                tileSetting = clippingPlanes[l4];
                            method175(finalYTile, worldController, tileSetting, type, reqPlane, finalXTile, foundObjectId, objectRotation + rotation & 3);
                        }
                    }
                } while(true);
            } while(true);
        }
    }

    private static int method184(int i, int j, int k, int l)
    {
        int i1 = 0x10000 - Rasterizer.COSINE[(k * 1024) / l] >> 1;
        return (i * (0x10000 - i1) >> 16) + (j * i1 >> 16);
    }

    private int getOverlayShadow(int i, int j)
    {
        if(i == -2)
            return 0xbc614e;
        if(i == -1)
        {
            if(j < 0)
                j = 0;
            else
            if(j > 127)
                j = 127;
            j = 127 - j;
            return j;
        }
        j = (j * (i & 0x7f)) / 128;
        if(j < 2)
            j = 2;
        else
        if(j > 126)
            j = 126;
        return (i & 0xff80) + j;
    }

    private static int method186(int i, int j)
    {
        int k = method170(i - 1, j - 1) + method170(i + 1, j - 1) + method170(i - 1, j + 1) + method170(i + 1, j + 1);
        int l = method170(i - 1, j) + method170(i + 1, j) + method170(i, j - 1) + method170(i, j + 1);
        int i1 = method170(i, j);
        return k / 16 + l / 8 + i1 / 4;
    }

    private static int getShadow(int i, int j)
    {
        if(i == -1)
            return 0xbc614e;
        j = (j * (i & 0x7f)) / 128;
        if(j < 2)
            j = 2;
        else
        if(j > 126)
            j = 126;
        return (i & 0xff80) + j;
    }

    public static void method188(WorldController worldController, int i, int j, int k, int l, CollisionDetection class11, int ai[][][], int i1,
                                 int j1, int k1)
    {
        int l1 = ai[l][i1][j];
        int i2 = ai[l][i1 + 1][j];
        int j2 = ai[l][i1 + 1][j + 1];
        int k2 = ai[l][i1][j + 1];
        int l2 = l1 + i2 + j2 + k2 >> 2;
        ObjectDef class46 = ObjectDef.forID(j1);
		int i3 = i1 + (j << 7) + ((j1 > 0x7fff ? j1  & 0x7fff : j1) << 14) + 0x40000000;
        if(!class46.hasActions)
            i3 += 0x80000000;
        byte byte1 = (byte)((i << 6) + k);
        if(k == 22)
        {
            Object obj;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
                obj = class46.renderObject(22, i, l1, i2, j2, k2, -1);
            else
                obj = new ObjectOnTile(j1, i, 22, i2, j2, l1, k2, class46.animationID, true);
            worldController.addGroundDecoration(k1, l2, j, ((Animable) (obj)), byte1, i3, i1, j1);
            if(class46.isUnwalkable && class46.hasActions)
                class11.appendSolidFlag(j, i1);
            return;
        }
        if(k == 10 || k == 11)
        {
            Object obj1;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
                obj1 = class46.renderObject(10, i, l1, i2, j2, k2, -1);
            else
                obj1 = new ObjectOnTile(j1, i, 10, i2, j2, l1, k2, class46.animationID, true);
            if(obj1 != null)
            {
                int j5 = 0;
                if(k == 11)
                    j5 += 256;
                int k4;
                int i5;
                if(i == 1 || i == 3)
                {
                    k4 = class46.sizeY;
                    i5 = class46.sizeX;
                } else
                {
                    k4 = class46.sizeX;
                    i5 = class46.sizeY;
                }
                worldController.addInteractableEntity(i3, byte1, l2, i5, ((Animable) (obj1)), k4, k1, j5, j, i1, j1);
            }
            if(class46.isUnwalkable)
                class11.method212(class46.aBoolean757, class46.sizeX, class46.sizeY, i1, j, i);
            return;
        }
        if(k >= 12)
        {
            Object obj2;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
                obj2 = class46.renderObject(k, i, l1, i2, j2, k2, -1);
            else
                obj2 = new ObjectOnTile(j1, i, k, i2, j2, l1, k2, class46.animationID, true);
            worldController.addInteractableEntity(i3, byte1, l2, 1, ((Animable) (obj2)), 1, k1, 0, j, i1, j1);
            if(class46.isUnwalkable)
                class11.method212(class46.aBoolean757, class46.sizeX, class46.sizeY, i1, j, i);
            return;
        }
        if(k == 0)
        {
            Object obj3;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
                obj3 = class46.renderObject(0, i, l1, i2, j2, k2, -1);
            else
                obj3 = new ObjectOnTile(j1, i, 0, i2, j2, l1, k2, class46.animationID, true);
            worldController.addWallObject(anIntArray152[i], ((Animable) (obj3)), i3, j, byte1, i1, null, l2, 0, k1, j1);
            if(class46.isUnwalkable)
                class11.method211(j, i, i1, k, class46.aBoolean757);
            return;
        }
        if(k == 1)
        {
            Object obj4;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
                obj4 = class46.renderObject(1, i, l1, i2, j2, k2, -1);
            else
                obj4 = new ObjectOnTile(j1, i, 1, i2, j2, l1, k2, class46.animationID, true);
            worldController.addWallObject(anIntArray140[i], ((Animable) (obj4)), i3, j, byte1, i1, null, l2, 0, k1, j1);
            if(class46.isUnwalkable)
                class11.method211(j, i, i1, k, class46.aBoolean757);
            return;
        }
        if(k == 2)
        {
            int j3 = i + 1 & 3;
            Object obj11;
            Object obj12;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
            {
                obj11 = class46.renderObject(2, 4 + i, l1, i2, j2, k2, -1);
                obj12 = class46.renderObject(2, j3, l1, i2, j2, k2, -1);
            } else
            {
                obj11 = new ObjectOnTile(j1, 4 + i, 2, i2, j2, l1, k2, class46.animationID, true);
                obj12 = new ObjectOnTile(j1, j3, 2, i2, j2, l1, k2, class46.animationID, true);
            }
            worldController.addWallObject(anIntArray152[i], ((Animable) (obj11)), i3, j, byte1, i1, ((Animable) (obj12)), l2, anIntArray152[j3], k1, j1);
            if(class46.isUnwalkable)
                class11.method211(j, i, i1, k, class46.aBoolean757);
            return;
        }
        if(k == 3)
        {
            Object obj5;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
                obj5 = class46.renderObject(3, i, l1, i2, j2, k2, -1);
            else
                obj5 = new ObjectOnTile(j1, i, 3, i2, j2, l1, k2, class46.animationID, true);
            worldController.addWallObject(anIntArray140[i], ((Animable) (obj5)), i3, j, byte1, i1, null, l2, 0, k1, j1);
            if(class46.isUnwalkable)
                class11.method211(j, i, i1, k, class46.aBoolean757);
            return;
        }
        if(k == 9)
        {
            Object obj6;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
                obj6 = class46.renderObject(k, i, l1, i2, j2, k2, -1);
            else
                obj6 = new ObjectOnTile(j1, i, k, i2, j2, l1, k2, class46.animationID, true);
            worldController.addInteractableEntity(i3, byte1, l2, 1, ((Animable) (obj6)), 1, k1, 0, j, i1, j1);
            if(class46.isUnwalkable)
                class11.method212(class46.aBoolean757, class46.sizeX, class46.sizeY, i1, j, i);
            return;
        }
        if(class46.adjustToTerrain)
            if(i == 1)
            {
                int k3 = k2;
                k2 = j2;
                j2 = i2;
                i2 = l1;
                l1 = k3;
            } else
            if(i == 2)
            {
                int l3 = k2;
                k2 = i2;
                i2 = l3;
                l3 = j2;
                j2 = l1;
                l1 = l3;
            } else
            if(i == 3)
            {
                int i4 = k2;
                k2 = l1;
                l1 = i2;
                i2 = j2;
                j2 = i4;
            }
        if(k == 4)
        {
            Object obj7;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
                obj7 = class46.renderObject(4, 0, l1, i2, j2, k2, -1);
            else
                obj7 = new ObjectOnTile(j1, 0, 4, i2, j2, l1, k2, class46.animationID, true);
            worldController.addWallDecoration(i3, j, i * 512, k1, 0, l2, ((Animable) (obj7)), i1, byte1, 0, anIntArray152[i], j1);
            return;
        }
        if(k == 5)
        {
            int j4 = 16;
            int l4 = worldController.getWallObjectUID(k1, i1, j);
            if(l4 > 0)
                j4 = ObjectDef.forID(l4 >> 14 & 0x7fff).anInt775;
            Object obj13;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
                obj13 = class46.renderObject(4, 0, l1, i2, j2, k2, -1);
            else
                obj13 = new ObjectOnTile(j1, 0, 4, i2, j2, l1, k2, class46.animationID, true);
            worldController.addWallDecoration(i3, j, i * 512, k1, anIntArray137[i] * j4, l2, ((Animable) (obj13)), i1, byte1, anIntArray144[i] * j4, anIntArray152[i], j1);
            return;
        }
        if(k == 6)
        {
            Object obj8;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
                obj8 = class46.renderObject(4, 0, l1, i2, j2, k2, -1);
            else
                obj8 = new ObjectOnTile(j1, 0, 4, i2, j2, l1, k2, class46.animationID, true);
            worldController.addWallDecoration(i3, j, i, k1, 0, l2, ((Animable) (obj8)), i1, byte1, 0, 256, j1);
            return;
        }
        if(k == 7)
        {
            Object obj9;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
                obj9 = class46.renderObject(4, 0, l1, i2, j2, k2, -1);
            else
                obj9 = new ObjectOnTile(j1, 0, 4, i2, j2, l1, k2, class46.animationID, true);
            worldController.addWallDecoration(i3, j, i, k1, 0, l2, ((Animable) (obj9)), i1, byte1, 0, 512, j1);
            return;
        }
        if(k == 8)
        {
            Object obj10;
            if(class46.animationID == -1 && class46.configObjectIDs == null)
                obj10 = class46.renderObject(4, 0, l1, i2, j2, k2, -1);
            else
                obj10 = new ObjectOnTile(j1, 0, 4, i2, j2, l1, k2, class46.animationID, true);
            worldController.addWallDecoration(i3, j, i, k1, 0, l2, ((Animable) (obj10)), i1, byte1, 0, 768, j1);
        }
    }

  public static boolean method189(int i, byte[] is, int i_250_
  ) //xxx bad method, decompiled with JODE
  {
    boolean bool = true;
    Stream stream = new Stream(is);
    int i_252_ = -1;
    for (;;)
      {
	int i_253_ = stream.method422 ();
	if (i_253_ == 0)
	  break;
	i_252_ += i_253_;
	int i_254_ = 0;
	boolean bool_255_ = false;
	for (;;)
	  {
	    if (bool_255_)
	      {
		int i_256_ = stream.method422 ();
		if (i_256_ == 0)
		  break;
		stream.readUnsignedByte();
	      }
	    else
	      {
		int i_257_ = stream.method422 ();
		if (i_257_ == 0)
		  break;
		i_254_ += i_257_ - 1;
		int i_258_ = i_254_ & 0x3f;
		int i_259_ = i_254_ >> 6 & 0x3f;
		int i_260_ = stream.readUnsignedByte() >> 2;
		int i_261_ = i_259_ + i;
		int i_262_ = i_258_ + i_250_;
		if (i_261_ > 0 && i_262_ > 0 && i_261_ < 103 && i_262_ < 103)
		  {
		    ObjectDef class46 = ObjectDef.forID (i_252_);
		    if (i_260_ != 22 || !lowMem || class46.hasActions
                    || class46.aBoolean736)
		      {
			bool &= class46.isAllModelsFetched ();
			bool_255_ = true;
		      }
		  }
	      }
	  }
      }
    return bool;
  }

    public final void method190(int i, CollisionDetection aclass11[], int j, WorldController worldController, byte abyte0[]) {
		label0: {
			ByteBuffer stream = new ByteBuffer(abyte0);
			int l = -1;
			do {
				int i1 = stream.method422();
				if (i1 == 0)
					break label0;
				l += i1;
				int j1 = 0;
				do {
					int k1 = stream.method422();
					if (k1 == 0)
						break;
					j1 += k1 - 1;
					int l1 = j1 & 0x3f;
					int i2 = j1 >> 6 & 0x3f;
					int j2 = j1 >> 12;
					int k2 = stream.readUnsignedByte();
					int l2 = k2 >> 2;
					int i3 = k2 & 3;
					int j3 = i2 + i;
					int k3 = l1 + j;
					if (j3 > 0 && k3 > 0 && j3 < 103 && k3 < 103) {
						int l3 = j2;
						if ((tileSettings[1][j3][k3] & 2) == 2)
							l3--;
						CollisionDetection class11 = null;
						if (l3 >= 0 && l3 < 4)
							class11 = aclass11[l3];
						method175(k3, worldController, class11, l2, j2, j3, l, i3);
					}
				} while (true);
			} while (true);
		}
	}

    private static int anIntArray124Offset = (int)(Math.random() * 17D) - 8;
    private final int[] anIntArray124;
    private final int[] anIntArray125;
    private final int[] anIntArray126;
    private final int[] anIntArray127;
    private final int[] anIntArray128;
    private final int[][][] heightMap;
    public final byte[][][] overLay;
    static int anInt131;
    private static int offsetLightning = (int)(Math.random() * 33D) - 16;
    private final byte[][][] aByteArrayArrayArray134;
    private final int[][][] anIntArrayArrayArray135;
    private final byte[][][] overlayClippingPaths;
    private static final int anIntArray137[] = {
        1, 0, -1, 0
    };
    private static final int anInt138 = 323;
    private final int[][] tileShadowArray;
    private static final int anIntArray140[] = {
        16, 32, 64, 128
    };
    public final byte[][][] underLay;
    private static final int anIntArray144[] = {
        0, -1, 0, 1
    };
    static int highestPlane = 99;
    private final int anInt146;
    private final int anInt147;
    private final byte[][][] overlayClippingPathRotations;
    private final byte[][][] tileSettings;
    static boolean lowMem = true;
    private static final int anIntArray152[] = {
        1, 2, 4, 8
    };

}
