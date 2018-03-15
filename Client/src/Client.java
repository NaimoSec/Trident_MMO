import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.CRC32;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.UnsupportedLookAndFeelException;




@SuppressWarnings("all")
public class Client extends RSApplet {
	
	public static ResourceLoader resourceLoader;

	public static void drawBorder(Sprite backTexture, Sprite[] images, int x, int y, int width, int height, int alpha, int backColour, boolean filled, boolean shadow) {
		final int pieceWidth = images[1].myWidth;
		final int pieceHeight = images[3].myHeight;
		byte remainingWidth = (byte) (width % pieceWidth);
		byte remainingHeight = (byte) (height % pieceHeight);
		if (shadow) {
			int shadowAlpha = alpha - 150;
			if (shadowAlpha < 0) {
				shadowAlpha = 0;
			}
		}
		if (filled) {
			if (backTexture == null) {
				DrawingArea.drawPixelsWithOpacity(backColour, y + pieceHeight, width - pieceWidth - remainingWidth, height - pieceHeight - remainingHeight, alpha, x + pieceWidth);
			} else {
				final byte textureWidth = (byte) backTexture.myWidth;
				final byte textureHeight = (byte) backTexture.myHeight;
				for (int textureWidthIndex = 0; textureWidthIndex < (width / textureWidth); textureWidthIndex++) {
					for (int textureHeightIndex = 0; textureHeightIndex < (height / textureHeight); textureHeightIndex++) {
						backTexture.drawSpriteWithOpacity(x + (textureWidthIndex * textureWidth), y + (textureHeightIndex * textureHeight), alpha);
					}
				}
			}
		}
		images[0].drawAdvancedSprite(x, y);
		images[2].drawAdvancedSprite(x + width - remainingWidth, y);
		images[4].drawAdvancedSprite(x + width - remainingWidth, y + height - remainingHeight);
		images[6].drawAdvancedSprite(x, y + height - remainingHeight);

		for (int horizontalIndex = 1; horizontalIndex < (width / pieceWidth); horizontalIndex++) {
			images[1].drawAdvancedSprite(x + (horizontalIndex * pieceWidth), y);//top
			images[5].drawAdvancedSprite(x + (horizontalIndex * pieceWidth), y + height - remainingHeight);//bottom
		}

		for (int verticalIndex = 2; verticalIndex < (height / pieceHeight); verticalIndex++) {
			images[3].drawAdvancedSprite(x + width - remainingWidth, y + (verticalIndex * pieceHeight));//right
			images[7].drawAdvancedSprite(x, y + (verticalIndex * pieceHeight));//left
		}
	}

	public static int log_view_dist = 9;
	public static HashMap<String, Boolean> options = new HashMap<String, Boolean>();

	public static boolean getOption(String s) {
		return options.get(s).booleanValue();
	}

	private void handleSettings() {
		if(!isWebclient()) {
			if(clientSize != 0) {
				toggleSize(clientSize);
				if(mainFrame != null)
					mainFrame.setClientIcon();
			}
		} else
			clientSize = 0;
		if (getOption("hd_tex")) {
			int l2 = RSInterface.interfaceCache[35565].valueIndexArray[0][1];
			variousSettings[l2] = 1 - variousSettings[l2];
		}
		if (getOption("player_shadow")) {
			int l2 = RSInterface.interfaceCache[35568].valueIndexArray[0][1];
			variousSettings[l2] = 1 - variousSettings[l2];
		}
		if (getOption("tooltip_hover")) {
			int l2 = RSInterface.interfaceCache[35571].valueIndexArray[0][1];
			variousSettings[l2] = 1 - variousSettings[l2];
		}
		if(getOption("old_hits")) {
			int l2 = RSInterface.interfaceCache[35584].valueIndexArray[0][1];
			variousSettings[l2] = 1 - variousSettings[l2];
		}
		if (getOption("constitution")) {
			int l2 = RSInterface.interfaceCache[35587].valueIndexArray[0][1];
			variousSettings[l2] = 1 - variousSettings[l2];
		}
		if (getOption("cursors")) {
			int l2 = RSInterface.interfaceCache[35590].valueIndexArray[0][1];
			variousSettings[l2] = 1 - variousSettings[l2];
		}
		if (getOption("old_frame")) {
			int l2 = RSInterface.interfaceCache[35694].valueIndexArray[0][1];
			variousSettings[l2] = 0 - variousSettings[l2];
		}
		if (getOption("smilies")) {
			int l2 = RSInterface.interfaceCache[35714].valueIndexArray[0][1];
			variousSettings[l2] = 1 - variousSettings[l2];
		}
		if (!musicEnabled)
			stopMidi();
	}

	public void loadSettings() {
		if (!FileOperations.FileExists(signlink.findcachedir()
				+ "res/pref/preferences.set")) {
			clientSize = 0;
			rememberMe = true;
			musicEnabled = false;
			options.put("hd_tex", false);
			options.put("player_shadow", false);
			options.put("tooltip_hover", false);
			options.put("old_hits", false);
			options.put("constitution", true);
			options.put("cursors", true);
			options.put("old_frame", false);
			options.put("smilies", true);
			options.put("censor_active", true);
			options.put("fog_active", true);
			saveSettings();
			return;
		}
		try {
			RandomAccessFile in = new RandomAccessFile(signlink.findcachedir() + "res/pref/preferences.set", "rw");
			String username = in.readUTF();
			if(username.length() > 0) {
				myUsername = username;
				rememberMe = true;
				loginScreenCursorPos = 1;
			}
			int sound = in.readShort();
			options.put("hd_tex", in.readByte() == 1);
			options.put("player_shadow", in.readByte() == 1);
			options.put("tooltip_hover", in.readByte() == 1);
			options.put("old_hits", in.readByte() == 1);
			options.put("constitution", in.readByte() == 1);
			options.put("cursors", in.readByte() == 1);
			options.put("old_frame", in.readByte() == 1);
			options.put("smilies", in.readByte() == 1);
			options.put("censor_active", in.readByte() == 1);
			options.put("fog_active", in.readByte() == 1);
			musicEnabled = false;
			if (sound == -1)
				soundEnabled = false;
			else
				soundEnabled = true;
			totalXP = in.readInt();
			clientSize = in.readInt();
			if(clientSize < 0 || clientSize > 2)
				clientSize = 0;
			/*
			 * Quick prayers / curses
			 */
			String q = in.readUTF();
			for (int i = 0; i < q.length(); i++)
				quickPrayers[i] = Integer.parseInt(q.substring(i, i+1));
			q = in.readUTF();
			for (int i = 0; i < q.length(); i++)
				quickCurses[i] = Integer.parseInt(q.substring(i, i+1));
			in.close();
		} catch (IOException e) {
			clientSize = 0;
			rememberMe = true;
			musicEnabled = false;
			options.put("hd_tex", false);
			options.put("player_shadow", false);
			options.put("tooltip_hover", false);
			options.put("old_hits", false);
			options.put("constitution", true);
			options.put("cursors", true);
			options.put("old_frame", true);
			options.put("smilies", true);
			options.put("censor_active", true);
			options.put("fog_active", true);
			saveSettings();
			loadSettings();
			e.printStackTrace();
		}
	}

	public void saveSettings() {
		try {
			RandomAccessFile out = new RandomAccessFile(signlink.findcachedir()
					+ "res/pref/preferences.set", "rw");
			out.writeUTF(myUsername == null || !rememberMe ? "" : myUsername);
			out.writeShort(soundEnabled ? signlink.wavevol : -1);
			out.write(getOption("hd_tex") ? 1 : 0);
			out.write(getOption("player_shadow") ? 1 : 0);
			out.write(getOption("tooltip_hover") ? 1 : 0);
			out.write(getOption("old_hits") ? 1 : 0);
			out.write(getOption("constitution") ? 1 : 0);
			out.write(getOption("cursors") ? 1 : 0);
			out.write(getOption("old_frame") ? 1 : 0);
			out.write(getOption("smilies") ? 1 : 0);
			out.write(getOption("censor_active") ? 1 : 0);
			out.write(getOption("fog_active") ? 1 : 0);
			out.writeInt(totalXP);
			out.writeInt(clientSize);
			/*
			 * Quick prayers & curses saving
			 */
			String stringSave = "";
			for(int i = 0; i < quickPrayers.length; i++)	{
				stringSave = stringSave + quickPrayers[i];
			}
			out.writeUTF(stringSave);
			stringSave = "";
			for(int i = 0; i < quickCurses.length; i++)	{
				stringSave = stringSave + quickCurses[i];
			}
			out.writeUTF(stringSave);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int screenOpacity = 255;
	/**
	 * 
	 */
	private final static int SHADOW_SPEED = 1;
	private int shadowDestination = 255;
	private boolean shadowProcessing;

	private void processShadow() {
		if (screenOpacity < shadowDestination) {
			screenOpacity += SHADOW_SPEED;
		} else if (screenOpacity > shadowDestination) {
			screenOpacity -= SHADOW_SPEED;
		} else {
			shadowProcessing = false;
		}
	}

	private boolean forcedShadow;
	private double currentShadow;
	private boolean resting = false;
	private final String[][] PLRCOMMANDS = new String[][] {
			{ "Enter character", "hangman" },
			{ "Enter house owner's name", "popE158" } };
	private int playerCommand;

	private static int loginFailures = 0;

	void mouseWheelDragged(int i, int j) {
		if (!mouseWheelDown)
			return;
		this.anInt1186 += i * 3;
		this.anInt1187 += (j << 1);
	}

	public String getPrefix(int rights) {
		String prefix = "cr";
		if (rights > 10) {
			prefix = "c";
		}
		return "@" + prefix + rights + "@";
	}

	public int getPrefixRights(String prefix) {
		int rights = 0;
		int start = 3;
		int end = 4;
		if (!prefix.contains("cr")) {
			start = 2;
		}
		if(end >= prefix.length())
			return 0;
		try {
			rights = Integer.parseInt(prefix.substring(start, end));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rights;
	}

	/**
	 * Draws a black pane, used for quests
	 * 
	 * @param opacity
	 */
	public void drawBlackPane() {
		if (paneOpacity >= 255) {
			minus = true;
		}
		if (paneOpacity <= 0) {
			minus = false;
			timesLooped = true;
		}
		paneOpacity += (minus ? -2 : 2);
		DrawingArea474.drawAlphaFilledPixels(0, 0, getClientWidth(),
				getClientHeight(), 0, paneOpacity);
		if (timesLooped)
			drawPane = false;

	}

	private boolean timesLooped = false;
	private boolean minus = false;
	private int paneOpacity = 0;
	public static int paneClosed = 0;
	public static int clientSize = 0;
	public static int clientWidth = 765, clientWidth2 = 765, clientHeight = 503, clientHeight2 = 503;// 503 - original
	public static int chatAreaHeight = 0;
	private int gameAreaWidth = 512, gameAreaHeight = 334;
	public int appletWidth = 765, appletHeight = 503;
	private static final int resizableWidth = getMaxWidth() - 200;
	private static final int resizableHeight = getMaxHeight() - 200;

	public static int getMaxWidth() {
		return (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	}

	public static int getMaxHeight() {
		return (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	}

	public void toggleSize(int size) {
		// if (clientSize != size) {
		if(isWebclient()) {
			pushMessage("Please download the desktop-client to use this. Link: Voxyde.com/download", 0, "");
			return;
		}
		clientSize = size;
		int width = 765;
		int height = 503;
		if (size == 0) {
			size = 0;
			width = 765;
			height = 503;
			showChat = true;
			showTab = true;
			log_view_dist = 9;
			clientZoom = 0;
		} else if (size == 1) {
			size = 1;
			width = isWebclient() ? getGameComponent().getWidth()
					: resizableWidth;
			height = isWebclient() ? getGameComponent().getHeight()
					: resizableHeight;
			log_view_dist = 10;
			clientZoom = 480;
		} else if (size == 2) {
			size = 2;
			width = getMaxWidth();
			height = getMaxHeight();
			log_view_dist = 10;
			clientZoom = 480;
		} else if (size == 3) {
			clientSize = 0;
			width = 765;
			height = 563;
			log_view_dist = 9;
			clientZoom = 0;
		}
		if(size != 0 && getOption("old_frame"))
			pushMessage("Option 'Old Frame' is not available in a resized mode yet.", 0, "");
		rebuildFrame(size, width, height);
		saveSettings();
		if(!isWebclient() && mainFrame != null)
			mainFrame.setClientIcon();
	}

	public boolean isWebclient() {
		return mainFrame == null && Client.webclient;
	}

	public void checkSize() {
		if (clientSize == 1) {
			boolean requireUpdate = false;
			if (clientWidth != (isWebclient() ? getGameComponent().getWidth()
					: mainFrame.getFrameWidth())) {
				clientWidth = (isWebclient() ? getGameComponent().getWidth()
						: mainFrame.getFrameWidth());
				gameAreaWidth = clientWidth;
				requireUpdate = true;
			}
			if (clientHeight != (isWebclient() ? getGameComponent().getHeight()
					: mainFrame.getFrameHeight())) {
				clientHeight = (isWebclient() ? getGameComponent().getHeight()
						: mainFrame.getFrameHeight());
				gameAreaHeight = clientHeight;
				requireUpdate = true;
			}
			if (requireUpdate)
				updateGameArea();
			clientHeight2 = clientHeight;
			clientWidth2 = clientWidth;
		}
	}

	public void rebuildFrame(int size, int width, int height) {
		try {

			gameAreaWidth = (size == 0) ? 512 : width;
			gameAreaHeight = (size == 0) ? 334 : height;
			clientWidth = width;
			clientHeight = height;
			instance.rebuildFrame(size == 2, width, height, size == 1,
					size == 2);
			updateGameArea();
			checkClientSize(true);
			super.mouseX = super.mouseY = -1;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateGameArea() {
		try {
			Rasterizer.setBounds(clientWidth, clientHeight);
			fullScreenTextureArray = Rasterizer.lineOffsets;
			Rasterizer.setBounds(
					clientSize == 0 ? (chatAreaIP != null ? chatAreaIP.width
							: 519) : clientWidth,
							clientSize == 0 ? (chatAreaIP != null ? chatAreaIP.height
									: 165) : clientHeight);
			anIntArray1180 = Rasterizer.lineOffsets;
			Rasterizer.setBounds(
					clientSize == 0 ? (tabAreaIP != null ? tabAreaIP.width
							: 250) : clientWidth,
							clientSize == 0 ? (tabAreaIP != null ? tabAreaIP.height
									: 335) : clientHeight);
			anIntArray1181 = Rasterizer.lineOffsets;
			Rasterizer.setBounds(!loggedIn ? clientWidth : gameAreaWidth,
					!loggedIn ? clientHeight : gameAreaHeight);
			anIntArray1182 = Rasterizer.lineOffsets;
			int ai[] = new int[9];
			for (int i8 = 0; i8 < 9; i8++) {
				int k8 = 128 + i8 * 32 + 15;
				int l8 = 600 + k8 * 3;
				int i9 = Rasterizer.SINE[k8];
				ai[i8] = l8 * i9 >> 16;
			}
			WorldController.setupViewport(500, 800, gameAreaWidth,
					gameAreaHeight, ai);
			if (loggedIn) {
				gameScreenIP = new RSImageProducer(gameAreaWidth,
						gameAreaHeight, getGameComponent());
			} else {
				titleScreen = new RSImageProducer(clientWidth, clientHeight,
						getGameComponent());
			}
			DrawingArea.width = clientWidth;
			DrawingArea.height = clientHeight;
			DrawingArea.pixels = new int[clientWidth * clientHeight];
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void drawUnfixedGame() {
		if (clientSize != 0) {
			drawChatArea();
			drawTabArea();
			drawMinimap();
		}

	}

	private String inputTitle = null;

	public void drawChatArea() {
		int offsetX = 0;
		int offsetY = clientSize != 0 ? clientHeight - 165 : 0;
		if (clientSize == 0) {
			chatAreaIP.initDrawingArea();
		}
		Rasterizer.lineOffsets = anIntArray1180;
		TextDrawingArea textDrawingArea = normalFont;
		if (showChat) {
			if (clientSize == 0) {
				cacheSprite[0].drawSprite(0 + offsetX, 0 + offsetY);
			} else {
				cacheSprite[88].drawARGBSprite(7 + offsetX, 7 + offsetY);
			}
		}
		drawChannelButtons(offsetX, offsetY);
		if (showInput) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			newBoldFont.drawCenteredString(promptMessage, 259 + offsetX,
					60 + offsetY, 0, -1);
			newBoldFont.drawCenteredString(promptInput + "*", 259 + offsetX,
					80 + offsetY, 128, -1);
		} else if (inputDialogState == 1) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			newBoldFont.drawCenteredString(inputTitle != null ? inputTitle : "Enter amount:", 259 + offsetX, 60 + offsetY, 0, -1);
			newBoldFont.drawCenteredString(amountOrNameInput + "*", 259 + offsetX, 80 + offsetY, 128, -1);
		} else if (inputDialogState == 2) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			newBoldFont.drawCenteredString(inputTitle != null ? inputTitle : "Enter name:", 259 + offsetX, 60 + offsetY, 0, -1);
			newBoldFont.drawCenteredString(amountOrNameInput + "*", 259 + offsetX, 80 + offsetY, 128, -1);
		} else if (inputDialogState == 3) {

			displayItemSearch();
		} else if (inputDialogState == 5) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			String s = "";
			if (playerCommand != 0) {
				s = PLRCOMMANDS[playerCommand - 1][0];
			}
			newBoldFont.drawCenteredString(s, 259 + offsetX, 60 + offsetY, 0,
					-1);
			newBoldFont.drawCenteredString(amountOrNameInput + "*",
					259 + offsetX, 80 + offsetY, 128, -1);
		} else if (aString844 != null) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			newBoldFont.drawCenteredString(aString844, 259 + offsetX,
					60 + offsetY, 0, -1);
			newBoldFont.drawCenteredString("Click to continue", 259 + offsetX,
					80 + offsetY, 128, -1);
		} else if (backDialogID != -1) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			drawInterface(0, 20 + offsetX,
					RSInterface.interfaceCache[backDialogID], 20 + offsetY);
		} else if (dialogID != -1) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			drawInterface(0, 20 + offsetX,
					RSInterface.interfaceCache[dialogID], 20 + offsetY);
		} else if (!quickChat && showChat) {
			int messageY = -3;
			int scrollPosition = 0;
			DrawingArea.setDrawingArea(121 + offsetY, 8 + offsetX,
					512 + offsetX, 7 + offsetY);
			for (int index = 0; index < 500; index++)
				if (chatMessages[index] != null) {
					int chatType = chatTypes[index];
					int positionY = (70 - messageY * 14) + anInt1089 + 6;
					if(chatNames[index] != null && chatNames[index].startsWith("<img=0>"))
						chatNames[index] = chatNames[index].replace("<img=0>", "@cr1@");
					if(chatNames[index] != null && chatNames[index].startsWith("<img=1>"))
						chatNames[index] = chatNames[index].replace("<img=1>", "@cr3@");
					String name = chatNames[index];
					String prefixName = name;
					final String time = "[" + "" + "]";
					int playerRights = 0;
					boolean fixed = false;
					if (name != null && name.indexOf("@") == 0) {
						if(name.contains("@cr10")) {
							playerRights = 10;
							fixed = true;
						} else if(name.contains("@cr11")) {
							playerRights = 11;
							fixed = true;
						}
						name = name.substring(fixed ? 6 : 5);
						if(!fixed)
							playerRights = getPrefixRights(prefixName.substring(0, prefixName.indexOf(name)));
					}
					if(playerRights == 0 && name != null && name.startsWith("<img=0>"))
						playerRights = 1;
					if(playerRights == 0 && name != null && name.startsWith("<img=1>"))
						playerRights = 2;
					boolean glow = false;
					int gloColor = 16722474;

					if (name != null && name.startsWith("@glo")) {
						gloColor = Integer.parseInt(name.substring(4, 6));
						name = name.substring(7);
						glow = true;
					}
					if (chatType == 0) {
						if (chatTypeView == 5 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210) {
								int xPos = 11;
								newRegularFont.drawBasicString(
										chatMessages[index], xPos + offsetX,
										positionY + offsetY,
										clientSize == 0 ? 0 : 0xffffff,
												clientSize == 0 ? -1 : 0);
							}
							scrollPosition++;
							messageY++;
						}
					}
					if ((chatType == 1 || chatType == 2)
							&& (chatType == 1 || publicChatMode == 0 || publicChatMode == 1
							&& isFriendOrSelf(name))) {
						if (chatTypeView == 1 || chatTypeView == 0
								|| (playerRights > 0 && playerRights <= 4)) {
							if (positionY > 0 && positionY < 210) {
								int xPos = 11;
								if (playerRights > 0) {
									playerIcons[playerRights] .drawSprite(xPos + 1 + offsetX, positionY - 11 + offsetY);
									xPos += 14;
								}
								if (gloColor == 10)
									gloColor = 5614335;
								if (gloColor == 11)
									gloColor = 2785279;
								if (gloColor == 12)
									gloColor = 006633;
								if (gloColor == 13)
									gloColor = 65280;
								if (gloColor == 14)
									gloColor = 11206612;
								if (gloColor == 15)
									gloColor = 418308;
								if (gloColor == 16)
									gloColor = 10909941;
								if (gloColor == 17)
									gloColor = 16755285;
								if (gloColor == 18)
									gloColor = 16486436;
								if (gloColor == 19)
									gloColor = 16776960;
								if (gloColor == 20)
									gloColor = 16722474;
								if (gloColor == 21)
									gloColor = 16733652;
								if (gloColor == 22)
									gloColor = 16722687;
								if (gloColor == 23)
									gloColor = 11141375;
								if (gloColor == 24)
									gloColor = 000000;
								if (gloColor == 25)
									gloColor = 16777215;
								if (gloColor == 26)
									gloColor = 13290186;
								if (gloColor == 27)
									gloColor = 5614335;
								if (glow) {
									String toRemove = "";
									try {
										toRemove = name.substring(
												name.indexOf("<col=13132800>"),
												name.indexOf("</col> "));
									} catch (Exception e) {
									}
									int tempX = newRegularFont
											.getTextWidth(toRemove);
									newRegularFont.drawBasicString(
											name.replaceAll(toRemove, ""), xPos
											- 1 + offsetX + tempX,
											positionY + 1 + offsetY, gloColor,
											-1);
								}
								newRegularFont.drawBasicString(name + ":", xPos
										+ offsetX, positionY + offsetY,
										clientSize == 0 ? 0 : 0xffffff,
												clientSize == 0 ? -1 : 0);
								xPos += newRegularFont.getTextWidth(name) + 7;
								newRegularFont.drawBasicString(
										chatMessages[index], xPos + offsetX,
										positionY + offsetY,
										clientSize == 0 ? 255 : 0x7FA9FF,
												clientSize == 0 ? -1 : 0);
							}
							scrollPosition++;
							messageY++;
						}
					}
					if ((chatType == 3 || chatType == 7)
							&& (splitPrivateChat == 0 || chatTypeView == 2)
							&& (chatType == 7 || privateChatMode == 0 || privateChatMode == 1
							&& isFriendOrSelf(name))) {
						if (chatTypeView == 2 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210) {
								int xPos = 11;
								newRegularFont.drawBasicString("From", xPos
										+ offsetX, positionY + offsetY,
										clientSize == 0 ? 0 : 0xffffff,
												clientSize == 0 ? -1 : 0);
								xPos += newRegularFont.getTextWidth("From ");
								if (playerRights > 0) {
									playerIcons[playerRights]
											.drawSprite(xPos + 1 + offsetX,
													positionY - 11 + offsetY);
									xPos += 14;
								}
								newRegularFont.drawBasicString(name + ":", xPos
										+ offsetX, positionY + offsetY,
										clientSize == 0 ? 0 : 0xffffff,
												clientSize == 0 ? -1 : 0);
								xPos += newRegularFont.getTextWidth(name) + 8;
								newRegularFont.drawBasicString(
										chatMessages[index], xPos + offsetX,
										positionY + offsetY,
										clientSize == 0 ? 0x800000 : 0xFF5256,
												clientSize == 0 ? -1 : 0);
							}
							scrollPosition++;
							messageY++;
						}
					}
					if (chatType == 4
							&& (tradeMode == 0 || tradeMode == 1
							&& isFriendOrSelf(name))) {
						if (chatTypeView == 3 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210) {
								newRegularFont.drawBasicString(name + " "
										+ chatMessages[index], 11 + offsetX,
										positionY + offsetY,
										clientSize == 0 ? 0x800080 : 0xFF00D4,
												clientSize == 0 ? -1 : 0);
							}
							scrollPosition++;
							messageY++;
						}
					}
					if (chatType == 5 && splitPrivateChat == 0
							&& privateChatMode < 2) {
						if (chatTypeView == 2 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210)
								newRegularFont.drawBasicString(
										chatMessages[index], 11 + offsetX,
										positionY + offsetY,
										clientSize == 0 ? 0x800000 : 0xFF5256,
												clientSize == 0 ? -1 : 0);
							scrollPosition++;
							messageY++;
						}
					}
					if (chatType == 6
							&& (splitPrivateChat == 0 || chatTypeView == 2)
							&& privateChatMode < 2) {
						if (chatTypeView == 2 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210) {
								newRegularFont.drawBasicString("To " + name
										+ ":", 11 + offsetX, positionY
										+ offsetY, clientSize == 0 ? 0
												: 0xffffff, clientSize == 0 ? -1 : 0);
								newRegularFont.drawBasicString(
										chatMessages[index],
										15
										+ newRegularFont
										.getTextWidth("To :"
												+ name)
												+ offsetX + offsetX, positionY
												+ offsetY,
												clientSize == 0 ? 0x800000 : 0xFF5256,
														clientSize == 0 ? -1 : 0);
							}
							scrollPosition++;
							messageY++;
						}
					}
					if (chatType == 8
							&& (tradeMode == 0 || tradeMode == 1
							&& isFriendOrSelf(name))) {
						if (chatTypeView == 3 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210)
								textDrawingArea.method385(0x7e3200, name + " "
										+ chatMessages[index], positionY
										+ offsetY, 11 + offsetX);
							scrollPosition++;
							messageY++;
						}
					}
					if (chatType == 16) {
						if (chatTypeView == 11 || chatTypeView == 0) {
							if (positionY > 0 && positionY < 210) {
								int positionX = 11;
								String message = (clientSize == 0 ? "<col=800000>"
										: "<col=FF5256>")
										+ chatMessages[index] + "</col>";
								newRegularFont.drawBasicString("" + message,
										positionX, positionY + offsetY,
										clientSize == 0 ? 0 : 0xffffff,
												clientSize == 0 ? -1 : 0);
							}
							scrollPosition++;
							messageY++;
						}
					}
				}
			DrawingArea.defaultDrawingAreaSize();
			anInt1211 = scrollPosition * 14 + 7 + 5;
			if (anInt1211 < 111) {
				anInt1211 = 111;
			}
			drawScrollbar(114, anInt1211 - anInt1089 - 113, 7 + offsetY,
					496 + offsetX, anInt1211, false, clientSize != 0);
			String name;
			if (myPlayer != null && myPlayer.name != null) {
				name = myPlayer.name;
			} else {
				name = TextClass.fixName(myUsername);
			}
			if(myPlayer.playerTitle > 0) {
				textDrawingArea.drawRegularText(clientSize == 0 ? false : true,
						10 + offsetX,
						clientSize == 0 ? 0 : 0xffffff, "@or3@"+getRank(myPlayer.playerTitle)+"", 133 + offsetY);
				offsetX += textDrawingArea.getTextWidth(""+getRank(myPlayer.playerTitle)+"") + 1;
			}
			if (myRights > 0) {
				playerIcons[myRights].drawSprite(12 + offsetX, 122 + offsetY);
				offsetX += 14;
			}

			textDrawingArea.drawRegularText(clientSize == 0 ? false : true,
					11 + offsetX, clientSize == 0 ? 0 : 0xffffff, name,
							133 + offsetY);
			cacheSprite[14].drawSprite(textDrawingArea.getTextWidth(name)
					+ 11 + offsetX, 123 + offsetY);
			textDrawingArea.drawRegularText(clientSize == 0 ? false : true,
					textDrawingArea.getTextWidth(name) + 24 + offsetX,
					clientSize == 0 ? 0 : 0xffffff, ": ", 133 + offsetY);
			// newRegularFont.drawRAString(inputString + "*", 24 +
			// newRegularFont.getTextWidth(s + ": ") + xPosOffset, 133 +
			// yPosOffset, clientSize == 0 ? 255 : 0x7FA9FF, clientSize == 0
			// ? -1 : 0);

			newRegularFont.drawBasicString(inputString + "*", 24
					+ newRegularFont.getTextWidth(name + ": ") + offsetX,
					133 + offsetY, clientSize == 0 ? 255 : 0x7FA9FF,
							clientSize == 0 ? -1 : 0);

			if (clientSize == 0)
				DrawingArea.drawHorizontalLine(121 + offsetY,
						clientSize == 0 ? 0x807660 : 0xffffff, 505, 7);
		} else if (quickChat) {
			cacheSprite[64].drawSprite(0 + offsetX, 0 + offsetY);
			displayQuickChat(offsetX, offsetY);
		}
		if (menuOpen && menuScreenArea == 2) {
			drawMenu();
		}
		if (clientSize == 0) {
			chatAreaIP.drawGraphics(338, super.graphics, 0);
		}
		gameScreenIP.initDrawingArea();
		Rasterizer.lineOffsets = anIntArray1182;
	}

	public int channel;
	public boolean showChat = true;
	public int chatColor = 0;
	public int chatEffect = 0;

	/**
	 * quickChat: is quick chat open? canTalk: can player submit text(type in
	 * the chatbox)? quickHoverY: hover position of the green box.
	 **/
	public boolean quickChat = false, canTalk = true, divideSelections = false,
			divideSelectedSelections = false;
	public int quickSelY = -1, quickSelY2 = -1, quickHoverY = -1,
			quickHoverY2 = -1, shownSelection = -1,
			shownSelectedSelection = -1;
	public String quickChatDir = "Quick Chat";
	public int quickHOffsetX = shownSelection != -1 ? 110 : 0;

	public void openQuickChat() {
		resetQuickChat();
		quickChat = true;
		canTalk = false;
		inputTaken = true;
	}

	public void resetQuickChat() {
		divideSelections = false;
		divideSelectedSelections = false;
		shownSelection = -1;
		shownSelectedSelection = -1;
		quickSelY = -1;
		quickSelY2 = -1;
		quickHoverY = -1;
		quickHoverY2 = -1;
	}

	/**
	 * Draws the quick chat interface.
	 **/
	public void displayQuickChat(int x, int y) {
		String[] shortcutKey = { "G", "T", "S", "E", "C", "M", "Enter" };
		String[] name = { "General", "Trade/Items", "Skills", "Group Events",
				"Clans", "Inter-game", "I'm muted." };
		cacheSprite[65].drawSprite(7 + x, 7 + y);
		if (cButtonHPos == 8) {
			cacheSprite[66].drawSprite(7 + x, 7 + y);
		}
		DrawingArea.drawPixels(2, 23 + y, 7 + x, 0x847963, 506);
		if (divideSelections) {
			DrawingArea.drawPixels(111, 25 + y, 116 + x, 0x847963, 2);
		}
		if (divideSelectedSelections) {
			DrawingArea.drawPixels(111, 25 + y, 116 + 158 + x, 0x847963, 2);
		}
		normalFont.drawRegularText(false, 45 + x, 255, quickChatDir, 20 + y);
		if (quickHoverY != -1 && shownSelection == -1 && quickHOffsetX == 0) {
			DrawingArea.drawPixels(14, quickHoverY + y, 7 + x, 0x577E45, 109);
		} else if (quickHoverY != -1 && shownSelection != -1
				&& quickHOffsetX == 0) {
			DrawingArea.drawPixels(14, quickHoverY + y, 7 + x, 0x969777, 109);
		}
		/**
		 * Hovering over text on selected->selections.
		 **/
		if (quickHoverY2 != -1 && shownSelectedSelection == -1
				&& quickHOffsetX == 0) {
			DrawingArea.drawPixels(14, quickHoverY2 + y, 118 + 159 + x,
					0x577E45, 109);
		} else if (quickHoverY2 != -1 && shownSelectedSelection != -1
				&& quickHOffsetX == 0) {
			DrawingArea.drawPixels(14, quickHoverY2 + y, 118 + 159 + x,
					0x969777, 109);
		}
		if (quickSelY != -1) {
			DrawingArea.drawPixels(14, quickSelY + y, 7 + x, 0x969777, 109);
		}
		if (quickSelY2 != -1) {
			DrawingArea.drawPixels(14, quickSelY2 + y, 118 + x, 0x969777, 156);
		}
		for (int i1 = 0, y2 = 36; i1 < name.length; i1++, y2 += 14) {
			normalFont.drawRegularText(false, 10 + x, 0x555555, shortcutKey[i1]
					+ ".", y + y2);
			if (i1 == name.length - 1)
				normalFont
				.drawRegularText(
						false,
						12
						+ x
						+ normalFont
						.getTextWidth(shortcutKey[i1]
								+ "."), 0, name[i1], y
								+ y2);
			else
				normalFont
				.drawRegularText(
						false,
						12
						+ x
						+ normalFont
						.getTextWidth(shortcutKey[i1]
								+ "."), 0, name[i1]
										+ " ->", y + y2);
		}
		if (shownSelection != -1) {
			showSelections(shownSelection, x, y);
		}
		if (shownSelectedSelection != -1) {
			showSelectedSelections(shownSelectedSelection, x, y);
		}
	}

	public void showSelections(int selections, int x, int y) {
		switch (selections) {
		case 0:
			String[] keys1 = { "R", "H", "G", "C", "S", "M", "B", "P" };
			String[] names1 = { "Responses", "Hello", "Goodbye", "Comments",
					"Smilies", "Mood", "Banter", "Player vs Player" };
			if (quickHoverY != -1 && quickHOffsetX == 110)
				DrawingArea.drawPixels(14, quickHoverY + y, 118 + x, 0x577E45,
						156);
			for (int i1 = 0, y2 = 36; i1 < names1.length; i1++, y2 += 14) {
				normalFont.drawRegularText(false, 118 + x, 0x555555, keys1[i1]
						+ ".", y + y2);
				normalFont.drawRegularText(false,
						120 + x + normalFont.getTextWidth(keys1[i1] + "."), 0,
						names1[i1] + " ->", y + y2);
			}
			break;
		case 1:
			String[] keys2 = { "T", "I" };
			String[] names2 = { "Trade", "Items" };
			if (quickHoverY != -1 && quickHoverY < 53 && quickHOffsetX == 110)
				DrawingArea.drawPixels(14, quickHoverY + y, 118 + x, 0x577E45,
						101);
			for (int i2 = 0, y2 = 36; i2 < names2.length; i2++, y2 += 14) {
				normalFont.drawRegularText(false, 118 + x, 0x555555, keys2[i2]
						+ ".", y + y2);
				normalFont.drawRegularText(false,
						120 + x + normalFont.getTextWidth(keys2[i2] + "."), 0,
						names2[i2] + " ->", y + y2);
			}
			break;

		default:
			break;
		}
	}

	public void showSelectedSelections(int selections, int x, int y) {
		switch (selections) {
		case 0:
			String[] keys1 = { "1", "2", "3", "4", "5" };
			String[] names1 = { "Hi.", "Hey!", "Sup?", "Hello.", "Yo dawg." };
			if (quickHoverY2 != -1 && quickHOffsetX == 269)
				DrawingArea.drawPixels(14, quickHoverY2 + y, 118 + 158 + x,
						0x577E45, 156);
			for (int i1 = 0, y2 = 36; i1 < names1.length; i1++, y2 += 14) {
				normalFont.drawRegularText(false, 118 + 159 + x, 0x555555,
						keys1[i1] + ".", y + y2);
				normalFont.drawRegularText(
						false,
						120 + 159 + x
						+ normalFont.getTextWidth(keys1[i1] + "."), 0,
						names1[i1], y + y2);
			}
			break;

		default:
			break;
		}
	}

	public void processQuickChatArea() {
		int y = clientHeight - 503;
		if (super.mouseX < 117 && super.mouseY > 363) {
			quickHOffsetX = 0;
			quickHoverY2 = -1;
		} else if (super.mouseX > 117 && super.mouseX < 117 + 158
				&& super.mouseY > 363) {
			quickHOffsetX = 110;
			quickHoverY2 = -1;
		} else {
			quickHOffsetX = 269;
			quickHoverY2 = quickHoverY;
		}
		if (super.mouseX >= 7 && super.mouseX <= 23 && super.mouseY >= y + 345
				&& super.mouseY <= y + 361) {
			cButtonHPos = 8;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 364 && super.mouseY <= y + 377) {
			quickHoverY = 25;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 378 && super.mouseY <= y + 391) {
			quickHoverY = 39;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 392 && super.mouseY <= y + 405) {
			quickHoverY = 53;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 406 && super.mouseY <= y + 419) {
			quickHoverY = 67;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 420 && super.mouseY <= y + 433) {
			quickHoverY = 81;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 434 && super.mouseY <= y + 447) {
			quickHoverY = 95;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 448 && super.mouseY <= y + 461) {
			quickHoverY = 109;
			inputTaken = true;
		} else if (super.mouseX >= 8 + quickHOffsetX
				&& super.mouseX <= 117 + quickHOffsetX
				&& super.mouseY >= y + 462 && super.mouseY <= y + 474
				&& shownSelection == 0) {
			quickHoverY = 123;
			inputTaken = true;
		} else {
			quickHoverY = -1;
			quickHoverY2 = -1;
			inputTaken = true;
		}
		if (super.clickMode3 == 1) {
			if (super.saveClickX >= 8 && super.saveClickX <= 117
					&& super.saveClickY >= y + 364
					&& super.saveClickY <= y + 377) {
				setSelection(25, "Quick Chat @bla@-> @blu@General", 0);
			} else if (super.saveClickX >= 8 && super.saveClickX <= 117
					&& super.saveClickY >= y + 378
					&& super.saveClickY <= y + 391) {
				setSelection(39, "Quick Chat @bla@-> @blu@Trade/Items", 1);
			} else if (clickInRegion(118, clientHeight - 126, 118 + 156,
					clientHeight - 113)) {
				if (shownSelection == 0) {
					setSelectedSelection(
							25,
							39,
							"Quick Chat @bla@-> @blu@General @bla@-> @blu@Hello",
							0);
				}
			} else if (clickInRegion(277, clientHeight - 140, 277 + 156,
					clientHeight - 126)) {
				if (shownSelectedSelection == 0) {
					quickSay("Hi.");
					return;
				}
			} else if (clickInRegion(277, clientHeight - 126, 277 + 156,
					clientHeight - 112)) {
				if (shownSelectedSelection == 0) {
					quickSay("Hey!");
					return;
				}
			} else if (clickInRegion(277, clientHeight - 112, 277 + 156,
					clientHeight - 98)) {
				if (shownSelectedSelection == 0) {
					quickSay("Sup?");
					return;
				}
			} else if (clickInRegion(277, clientHeight - 98, 277 + 156,
					clientHeight - 84)) {
				if (shownSelectedSelection == 0) {
					quickSay("Hello.");
					return;
				}
			} else if (clickInRegion(277, clientHeight - 84, 277 + 156,
					clientHeight - 70)) {
				if (shownSelectedSelection == 0) {
					quickSay("Yo dawg.");
					return;
				}
			} else if (clickInRegion(7, clientHeight - 56, 116,
					clientHeight - 42)) {
				quickSay("I'm muted and I can only use quick chat.");
				return;
			} else {
				inputTaken = true;
			}
		}
	}

	public void setSelection(int y, String directory, int selection) {
		quickSelY = y;
		quickSelY2 = -1;
		divideSelections = true;
		divideSelectedSelections = false;
		quickChatDir = directory;
		shownSelection = selection;
		shownSelectedSelection = -1;
		inputTaken = true;
	}

	public void setSelectedSelection(int y1, int y2, String directory,
			int selectedSelection) {
		divideSelections = true;
		divideSelectedSelections = true;
		quickSelY = y1;
		quickSelY2 = y2;
		quickChatDir = directory;
		shownSelectedSelection = selectedSelection;
		inputTaken = true;
	}

	public void quickSay(String text) {
		say(text, true);
		isQuickChat = true;
		resetQuickChat();
		quickChat = false;
		canTalk = true;
		inputTaken = true;
	}

	public boolean isQuickChat = false;

	public void say(String text, boolean isQuickSay) {
		isQuickChat = true;
		stream.createFrame(4);
		stream.writeByte(0);
		int j3 = stream.currentOffset;
		stream.method425(chatEffect);
		stream.method425(chatColor);
		textStream.currentOffset = 0;
		TextInput.appendToStream(text, textStream);
		stream.method441(0, textStream.buffer, textStream.currentOffset);
		stream.writeBytes(stream.currentOffset - j3);
		text = TextInput.processText(text);
		myPlayer.textSpoken = text;
		myPlayer.anInt1513 = chatColor;
		myPlayer.anInt1531 = chatEffect;
		myPlayer.textCycle = 150;
		pushMessage(myPlayer.textSpoken, 2, getPrefix(myRights)
				+ (glowColor != 0 ? "@glo" + glowColor + "@" : "")
				+ myPlayer.name);
		if (publicChatMode == 2) {
			publicChatMode = 3;
			stream.createFrame(95);
			stream.writeByte(publicChatMode);
			stream.writeByte(privateChatMode);
			stream.writeByte(tradeMode);
		}
	}

	public boolean filterMessages = false;
	public String[] filteredMessages = { "You catch a",
			"You successfully cook the", "You accidentally burn the",
			"You manage to get", "You get some" };// add
	// more
	String text[] = { "On", "Friends", "Off", "Filter"/*HIDE*/, "Filter", "All" };

	int[] CHANNELBUTTON_X = new int[] { 5, 62, 119, 176, 233, 290, 347 };
	public void drawChannelButtons(int xPosOffset, int yPosOffset) {
		cacheSprite[5].drawSprite(5 + xPosOffset, 142 + yPosOffset);
		int textColor[] = { 65280, 0xffff00, 0xff0000, 65535, 0xffff00, 65280 };
		switch (cButtonCPos) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			cacheSprite[2].drawSprite(CHANNELBUTTON_X[cButtonCPos] + xPosOffset,
					142 + yPosOffset);
			break;
		}
		if (cButtonHPos == cButtonCPos) {
			switch (cButtonHPos) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				cacheSprite[3].drawSprite(CHANNELBUTTON_X[cButtonHPos] + xPosOffset,
						142 + yPosOffset);
				break;
			case 7:
				cacheSprite[4].drawSprite(CHANNELBUTTON_X[cButtonHPos] + xPosOffset,
						142 + yPosOffset);
				break;
			}
		} else {
			switch (cButtonHPos) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				cacheSprite[1].drawSprite(CHANNELBUTTON_X[cButtonHPos] + xPosOffset,
						142 + yPosOffset);
				break;
			case 7:
				cacheSprite[4].drawSprite(404 + xPosOffset,
						142 + yPosOffset);
				break;
			}
		}
		smallText.drawCenteredText(0xffffff, CHANNELBUTTON_X[0] + 28 + xPosOffset, "All",
				157 + yPosOffset, true);
		smallText.drawCenteredText(0xffffff, CHANNELBUTTON_X[1] + 28 + xPosOffset, "Game",
				152 + yPosOffset, true);
		smallText.drawCenteredText(0xffffff, CHANNELBUTTON_X[2] + 28 + xPosOffset, "Public",
				152 + yPosOffset, true);
		smallText.drawCenteredText(0xffffff, CHANNELBUTTON_X[3] + 28 + xPosOffset, "Private",
				152 + yPosOffset, true);
		smallText.drawCenteredText(0xffffff, CHANNELBUTTON_X[4] + 28 + xPosOffset, "Clan",
				152 + yPosOffset, true);
		smallText.drawCenteredText(0xffffff, CHANNELBUTTON_X[5] + 28 + xPosOffset, "N/A",
				156 + yPosOffset, true);
		smallText.drawCenteredText(0xffffff, CHANNELBUTTON_X[6] + 28 + xPosOffset, "N/A",
				156 + yPosOffset, true);
		smallText.drawCenteredText(0xffffff, 459 + xPosOffset,
				"Report Abuse", 157 + yPosOffset, true);
		smallText.drawCenteredText(textColor[gameChatMode],
				62 + 28 + xPosOffset, "All", 163 + yPosOffset,
				true);
		smallText.drawCenteredText(textColor[publicChatMode], CHANNELBUTTON_X[2] + 28
				+ xPosOffset, text[publicChatMode], 163 + yPosOffset, true);
		smallText.drawCenteredText(textColor[privateChatMode], CHANNELBUTTON_X[3] + 28
				+ xPosOffset, text[privateChatMode], 163 + yPosOffset, true);
		smallText.drawCenteredText(textColor[clanChatMode], CHANNELBUTTON_X[4] + 28
				+ xPosOffset, "All", 163 + yPosOffset, true);
		//	smallText
		//	.drawCenteredText(textColor[tradeMode], CHANNELBUTTON_X[5] + 28 + xPosOffset,
		//			text[tradeMode], 163 + yPosOffset, true);
		// smallText.method382(textColor[yellMode], x[6] + 28 + xPosOffset,
		// text[yellMode], 163 + yPosOffset, true);
	}

	private void processChatModeClick() {
		int[] x = { 5, 62, 119, 176, 233, 290, 347, 404 };
		if (super.mouseX >= x[0] && super.mouseX <= x[0] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 0;
			inputTaken = true;
		} else if (super.mouseX >= x[1] && super.mouseX <= x[1] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 1;
			inputTaken = true;
		} else if (super.mouseX >= x[2] && super.mouseX <= x[2] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 2;
			inputTaken = true;
		} else if (super.mouseX >= x[3] && super.mouseX <= x[3] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 3;
			inputTaken = true;
		} else if (super.mouseX >= x[4] && super.mouseX <= x[4] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 4;
			inputTaken = true;
		} else if (super.mouseX >= x[5] && super.mouseX <= x[5] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 5;
			inputTaken = true;
		} else if (super.mouseX >= x[6] && super.mouseX <= x[6] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 6;
			inputTaken = true;
		} else if (super.mouseX >= x[7] && super.mouseX <= x[7] + 111
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			cButtonHPos = 7;
			inputTaken = true;
		} else {
			cButtonHPos = -1;
			inputTaken = true;
		}
		if (super.clickMode3 == 1) {
			if (super.saveClickX >= x[0] && super.saveClickX <= x[0] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 0) {
						cButtonCPos = 0;
						chatTypeView = 0;
						inputTaken = true;
						channel = 0;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 0;
					chatTypeView = 0;
					inputTaken = true;
					channel = 0;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= x[1]
					&& super.saveClickX <= x[1] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 1 && clientSize != 0) {
						cButtonCPos = 1;
						chatTypeView = 5;
						inputTaken = true;
						channel = 1;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 1;
					chatTypeView = 5;
					inputTaken = true;
					channel = 1;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= x[2]
					&& super.saveClickX <= x[2] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 2 && clientSize != 0) {
						cButtonCPos = 2;
						chatTypeView = 1;
						inputTaken = true;
						channel = 2;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 2;
					chatTypeView = 1;
					inputTaken = true;
					channel = 2;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= x[3]
					&& super.saveClickX <= x[3] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 3 && clientSize != 0) {
						cButtonCPos = 3;
						chatTypeView = 2;
						inputTaken = true;
						channel = 3;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 3;
					chatTypeView = 2;
					inputTaken = true;
					channel = 3;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= x[4]
					&& super.saveClickX <= x[4] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 4 && clientSize != 0) {
						cButtonCPos = 4;
						chatTypeView = 11;
						inputTaken = true;
						channel = 4;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 4;
					chatTypeView = 11;
					inputTaken = true;
					channel = 4;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= x[5]
					&& super.saveClickX <= x[5] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 5 && clientSize != 0) {
						cButtonCPos = 5;
						chatTypeView = 3;
						inputTaken = true;
						channel = 5;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 5;
					chatTypeView = 3;
					inputTaken = true;
					channel = 5;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= x[6]
					&& super.saveClickX <= x[6] + 56
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (clientSize != 0) {
					if (channel != 6 && clientSize != 0) {
						cButtonCPos = 6;
						chatTypeView = 6;
						inputTaken = true;
						channel = 6;
					} else {
						showChat = showChat ? false : true;
					}
				} else {
					cButtonCPos = 6;
					chatTypeView = 6;
					inputTaken = true;
					channel = 6;
				}
				stream.createFrame(95);
				stream.writeByte(publicChatMode);
				stream.writeByte(privateChatMode);
				stream.writeByte(tradeMode);
			} else if (super.saveClickX >= 404 && super.saveClickX <= 515
					&& super.saveClickY >= clientHeight - 23
					&& super.saveClickY <= clientHeight) {
				if (openInterfaceID == -1) {
					clearTopInterfaces();
					reportAbuseInput = "";
					reasonForReport = "";
					playerReporting = "";
					canMute = false;
					sendPacket185(9999);
					reportBox2Selected = false;
				} else {
					pushMessage(
							"Please close the interface you have open before using 'report abuse'",
							0, "");
				}
			} 
			if (!showChat) {
				cButtonCPos = -1;
			}
		}
	}

	private String reasonForReport;
	private boolean reportBox2Selected;
	private String playerReporting;

	private void rightClickChatButtons() {
		int y = clientHeight - 503;
		int[] x = { 5, 62, 119, 176, 233, 290, 347, 404 };
		if (super.mouseX >= 7 && super.mouseX <= 23 && super.mouseY >= y + 345
				&& super.mouseY <= y + 361) {
			if (quickChat) {
				menuActionName[1] = "Close";
				menuActionID[1] = 1004;
				menuActionRow = 2;
			}
		}/* else if (super.mouseX >= 7
				&& super.mouseX <= newRegularFont.getTextWidth(myUsername) + 24
				&& super.mouseY >= clientHeight - 43
				&& super.mouseY <= clientHeight - 31) {
			if (!quickChat) {
				menuActionName[1] = "Open quickchat";
				menuActionID[1] = 1005;
				menuActionRow = 2;
			}
		}*/
		if (super.mouseX >= x[0] && super.mouseX <= x[0] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			menuActionName[1] = "View All";
			menuActionID[1] = 999;
			menuActionRow = 2;
		} else if (super.mouseX >= x[1] && super.mouseX <= x[1] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			/*menuActionName[1] = "Filter Game";
			menuActionID[1] = 798;
			menuActionName[1] = "All Game";
			menuActionID[1] = 797;*/
			menuActionName[1] = "View Game Messages";
			menuActionID[1] = 998;
			menuActionRow = 2;
		} else if (super.mouseX >= x[2] && super.mouseX <= x[2] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			menuActionName[1] = "Filter Public Messages";
			menuActionID[1] = 997;
			menuActionName[2] = "Show No Public Messages";
			menuActionID[2] = 996;
			menuActionName[3] = "Show Public Messages From Friends";
			menuActionID[3] = 995;
			menuActionName[4] = "Show Public Messages From All";
			menuActionID[4] = 994;
			menuActionName[5] = "View Public Messages";
			menuActionID[5] = 993;
			menuActionRow = 6;
		} else if (super.mouseX >= x[3] && super.mouseX <= x[3] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			menuActionName[1] = "Appear Offline To All";
			menuActionID[1] = 992;
			menuActionName[2] = "Appear Online To Friends";
			menuActionID[2] = 991;
			menuActionName[3] = "Appear Online To All";
			menuActionID[3] = 990;
			menuActionName[4] = "View Private Messages";
			menuActionID[4] = 989;
			menuActionRow = 5;
		} else if (super.mouseX >= x[4] && super.mouseX <= x[4] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			/*menuActionName[1] = "Off Clan chat";
			menuActionID[1] = 1003;
			menuActionName[2] = "Friends Clan chat";
			menuActionID[2] = 1002;
			menuActionName[3] = "On Clan chat";
			menuActionID[3] = 1001;*/
			menuActionName[1] = "View Clan Chat Messages";
			menuActionID[1] = 1000;
			menuActionRow = 2;
		}/* else if (super.mouseX >= x[5] && super.mouseX <= x[5] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			menuActionName[1] = "Off Trade";
			menuActionID[1] = 987;
			menuActionName[2] = "Friends Trade";
			menuActionID[2] = 986;
			menuActionName[3] = "On Trade";
			menuActionID[3] = 985;
			menuActionName[4] = "View Trade";
			menuActionID[4] = 984;
			menuActionRow = 5;
		} else if (super.mouseX >= x[6] && super.mouseX <= x[6] + 56
				&& super.mouseY >= clientHeight - 23
				&& super.mouseY <= clientHeight) {
			menuActionName[1] = "Hide Yell";
			menuActionID[1] = 983;
			menuActionName[2] = "Off Yell";
			menuActionID[2] = 982;
			menuActionName[3] = "Friends Yell";
			menuActionID[3] = 981;
			menuActionName[4] = "On Yell";
			menuActionID[4] = 980;
			menuActionName[5] = "View Yell";
			menuActionID[5] = 979;
			menuActionRow = 5;
		}*/
	}

	public boolean canClick() {
		if (mouseInRegion(clientWidth - (clientWidth < smallTabs ? 240 : 480),
				clientHeight - (clientWidth < smallTabs ? 74 : 37),
				clientWidth, clientHeight)) {
			return false;
		}
		if (showChat || backDialogID != -1) {
			if (super.mouseX > 0 && super.mouseX < 519
					&& super.mouseY > clientHeight - 165
					&& super.mouseY < clientHeight
					|| super.mouseX > clientWidth - 220
					&& super.mouseX < clientWidth && super.mouseY > 0
					&& super.mouseY < 165) {
				return false;
			}
		}
		if (mouseInRegion2(clientWidth - 216, clientWidth, 0, 172)) {
			return false;
		}
		if (showTab) {
			if (clientWidth >= smallTabs) {
				if (super.mouseX >= clientWidth - 420
						&& super.mouseX <= clientWidth
						&& super.mouseY >= clientHeight - 37
						&& super.mouseY <= clientHeight
						|| super.mouseX > clientWidth - 204
						&& super.mouseX < clientWidth
						&& super.mouseY > clientHeight - 37 - 274
						&& super.mouseY < clientHeight)
					return false;
			} else {
				if (super.mouseX >= clientWidth - 210
						&& super.mouseX <= clientWidth
						&& super.mouseY >= clientHeight - 74
						&& super.mouseY <= clientHeight
						|| super.mouseX > clientWidth - 204
						&& super.mouseX < clientWidth
						&& super.mouseY > clientHeight - 74 - 274
						&& super.mouseY < clientHeight)
					return false;
			}
		}
		return true;
	}

	public int getOrbX(int orb) {
		switch (orb) {
		case 0:
			return clientSize != 0 ? clientWidth - 212 : 172;
		case 1:
			return clientSize != 0 ? clientWidth - 215 : 188;
		case 2:
			return clientSize != 0 ? clientWidth - 203 : 188;
		case 3:
			return clientSize != 0 ? clientWidth - 180 : 172;
		}
		return 0;
	}

	public int getOrbY(int orb) {
		switch (orb) {
		case 0:
			return clientSize != 0 ? 39 : 15;
		case 1:
			return clientSize != 0 ? 73 : 54;
		case 2:
			return clientSize != 0 ? 107 : 93;
		case 3:
			return clientSize != 0 ? 141 : 128;
		}
		return 0;
	}

	public double fillHP;

	public void drawHPOrb() {
		int currentHp = (currentStats[3] / 10);
		int health = (int) (((double) currentStats[3] / (double) currentMaxStats[3]) * 100D);
		if(!getOption("constitution")) {
			currentHp = (currentHp / 10);
		}
		int x = getOrbX(0);
		int y = getOrbY(0);
		orbs[clientSize == 0 ? 0 : 11].drawSprite(x, y);
		if (health >= 75) {
			newSmallFont.drawCenteredString(Integer.toString(currentHp), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 65280, 0);
		} else if (health <= 74 && health >= 50) {
			newSmallFont.drawCenteredString(Integer.toString(currentHp), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 0xffff00, 0);
		} else if (health <= 49 && health >= 25) {
			newSmallFont.drawCenteredString(Integer.toString(currentHp), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 0xfca607, 0);
		} else if (health <= 24 && health >= 0) {
			newSmallFont.drawCenteredString(Integer.toString(currentHp), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 0xf50d0d, 0);


		}
		orbs[2].drawSprite(x + (clientSize == 0 ? 3 : 27), y + 3);
		double percent = (health / 100D);
		fillHP = 27 * percent;
		int depleteFill = 27 - (int) fillHP;
		orbs[1].myHeight = depleteFill;
		orbs[1].height = depleteFill;
		orbs[1].drawSprite(x + (clientSize == 0 ? 3 : 27), y + 3);
		orbs[3].drawSprite(x + (clientSize == 0 ? 9 : 33), y + 11);
	}

	public double fillPrayer;
	private boolean prayClicked;
	private String prayerBook;

	public void drawPrayerOrb() {
		int currentPray = currentStats[5] / 10;
		if(currentPray <= 0 && quickPrsActive)
			quickPrsActive = false;
		int maxPray = currentMaxStats[5] / 10;
		int prayer = (int) (((double) currentPray / (double) maxPray) * 100D);
		int x = getOrbX(1);
		int y = getOrbY(1);
		orbs[clientSize == 0 ? (hoverPos == 1 ? 12 : 0)
				: (hoverPos == 1 ? 13 : 11)].drawSprite(x, y);
		if (prayer <= 100 && prayer >= 75) {
			newSmallFont.drawCenteredString(Integer.toString(currentPray),
					x + (clientSize == 0 ? 42 : 15), y + 26, 65280, 0);
		} else if (prayer <= 74 && prayer >= 50) {
			newSmallFont.drawCenteredString(Integer.toString(currentPray),
					x + (clientSize == 0 ? 42 : 15), y + 26, 0xffff00, 0);
		} else if (prayer <= 49 && prayer >= 25) {
			newSmallFont.drawCenteredString(Integer.toString(currentPray),
					x + (clientSize == 0 ? 42 : 15), y + 26, 0xfca607, 0);
		} else if (prayer <= 24 && prayer >= 0) {
			newSmallFont.drawCenteredString(Integer.toString(currentPray),
					x + (clientSize == 0 ? 42 : 15), y + 26, 0xf50d0d, 0);

		}
		orbs[quickPrsActive ? 10 : 4].drawSprite(x + (clientSize == 0 ? 3 : 27),
				y + 3);
		double percent = (prayer / 100D);
		fillPrayer = 27 * percent;
		int depleteFill = 27 - (int) fillPrayer;
		orbs[17].myHeight = depleteFill;
		orbs[17].height = depleteFill;
		orbs[17].drawSprite(x + (clientSize == 0 ? 3 : 27), y + 3);
		orbs[5].drawSprite(x + (clientSize == 0 ? 7 : 31), y + 7);
	}

	public double fillRun;
	public boolean running;
	public int currentEnergy;

	public void drawRunOrb() {
		int run = (int) (((double) currentEnergy / (double) 100) * 100D);
		int x = getOrbX(2);
		int y = getOrbY(2);
		orbs[clientSize == 0 ? (hoverPos == 2 ? 12 : 0)
				: (hoverPos == 2 ? 13 : 11)].drawSprite(x, y);
		if (run <= 100 && run >= 75) {
			newSmallFont
			.drawCenteredString(Integer.toString(currentEnergy), x
					+ (clientSize == 0 ? 42 : 15), y + 26, 65280, 0);
		} else if (run <= 74 && run >= 50) {
			newSmallFont.drawCenteredString(
					Integer.toString(currentEnergy), x
					+ (clientSize == 0 ? 42 : 15), y + 26,
					0xffff00, 0);
		} else if (run <= 49 && run >= 25) {
			newSmallFont.drawCenteredString(
					Integer.toString(currentEnergy), x
					+ (clientSize == 0 ? 42 : 15), y + 26,
					0xfca607, 0);
		} else if (run <= 24 && run >= 0) {
			newSmallFont.drawCenteredString(
					Integer.toString(currentEnergy), x
					+ (clientSize == 0 ? 42 : 15), y + 26,
					0xf50d0d, 0);

		}
		orbs[!running ? 6 : 8]
				.drawSprite(x + (clientSize == 0 ? 3 : 27), y + 3);
		double percent = (run / 100D);
		fillRun = 27 * percent;
		int depleteFill = 27 - (int) fillRun;
		orbs[18].myHeight = depleteFill;
		orbs[18].height = depleteFill;
		orbs[18].drawSprite(x + (clientSize == 0 ? 3 : 27), y + 3);
		orbs[!running ? 7 : 9].drawSprite(x + (clientSize == 0 ? 10 : 34),
				y + 7);
	}

	public double fillSummoning;

	public void drawSummoningOrb() {
		int summoning = (int) (((double) currentStats[23] / (double) currentMaxStats[23]) * 100D);
		int x = getOrbX(3);
		int y = getOrbY(3);
		orbs[clientSize == 0 ? (hoverPos == 3 ? 12 : 0)
				: (hoverPos == 3 ? 13 : 11)].drawSprite(x, y);
		if (summoning <= 100 && summoning >= 75) {
			newSmallFont
			.drawCenteredString(Integer.toString(currentStats[23]),
					x + (clientSize == 0 ? 42 : 15), y + 26, 65280,
					0);
		} else if (summoning <= 74 && summoning >= 50) {
			newSmallFont.drawCenteredString(
					Integer.toString(currentStats[23]), x
					+ (clientSize == 0 ? 42 : 15), y + 26,
					0xffff00, 0);
		} else if (summoning <= 49 && summoning >= 25) {
			newSmallFont.drawCenteredString(
					Integer.toString(currentStats[23]), x
					+ (clientSize == 0 ? 42 : 15), y + 26,
					0xfca607, 0);
		} else if (summoning <= 24 && summoning >= 0) {
			newSmallFont.drawCenteredString(
					Integer.toString(currentStats[23]), x
					+ (clientSize == 0 ? 42 : 15), y + 26,
					0xf50d0d, 0);
		}
		orbs[hasFamiliar ? 16 : 14].drawSprite(x
				+ (clientSize == 0 ? 3 : 27), y + 3);
		double percent = (summoning / 100D);
		fillSummoning = 27 * percent;
		int depleteFill = 27 - (int) fillSummoning;
		orbs[19].myHeight = depleteFill;
		orbs[19].height = depleteFill;
		orbs[19].drawSprite(x + (clientSize == 0 ? 3 : 27), y + 3);
		orbs[15].drawSprite(x + (clientSize == 0 ? 9 : 33), y + 9);
	}
	boolean hasFamiliar = false;
	boolean gravestoneInterface = false;

	private NumberFormat format = NumberFormat.getInstance();
	public boolean showXP;
	public boolean showBonus;
	public int gainedExpY = 0;
	public static boolean xpGained = false, canGainXP = true;
	public static int totalXP = 0;

	private LinkedList<XPGain> gains = new LinkedList<XPGain>();

	public void addXP(int skillID, int xp) {
		if (xp != 6667 && xp != 0 && canGainXP) {
			gains.add(new XPGain(skillID, xp));
			totalXP += xp;
		}
	}

	public class XPGain {
		/**
		 * The skill which gained the xp
		 */
		private int skill;

		/**
		 * The XP Gained
		 */
		private int xp;
		private int y;
		private int alpha = 0;

		public XPGain(int skill, int xp) {
			this.skill = skill;
			this.xp = xp;
		}

		public void increaseY() {
			y++;
		}

		public int getSkill() {
			return skill;
		}

		public int getXP() {
			return xp;
		}

		public int getY() {
			return y;
		}

		public int getAlpha() {
			return alpha;
		}

		public void increaseAlpha() {
			alpha += alpha < 256 ? 30 : 0;
			alpha = alpha > 256 ? 256 : alpha;
		}

		public void decreaseAlpha() {
			alpha -= alpha > 0 ? 30 : 0;
			alpha = alpha > 256 ? 256 : alpha;
		}
	}

	public void displayXPCounter() {
		int x = clientSize == 0 ? 419 : clientWidth - 310;
		int y = clientSize == 0 ? 0 : -36;
		int currentIndex = 0;
		int offsetY = 0;
		int stop = 70;
		cacheSprite[40].drawSprite(x, clientSize == 0 ? 50 : 48 + y);
		normalFont.drawRegularText(true, x + 1, 0xffffff,
				"XP:" + String.format("%, d", totalXP), (clientSize == 0 ? 63
						: 61) + y);

		if (!gains.isEmpty()) {
			Iterator<XPGain> it$ = gains.iterator();
			while (it$.hasNext()) {
				XPGain gain = it$.next();
				if (gain.getY() < stop) {
					if (gain.getY() <= 10) {
						gain.increaseAlpha();
					}
					if (gain.getY() >= stop - 10) {
						gain.decreaseAlpha();
					}
					gain.increaseY();
				} else if (gain.getY() == stop) {
					it$.remove();
				}
				if (gain.getY() < stop) {
					if (gains.size() > 1)
						offsetY = (clientSize == 0 ? 0 : -20) + (currentIndex * 28);
					if(gain.getSkill() >= 0) {
						int id = gain.getSkill() + 41;
						if(gain.getSkill() == 23)
							id = 95; //Summon
						if(id == 65)
							id = 88;
						Sprite sprite = SpriteLoader.sprites2[id];
						sprite.drawSprite((x + 15) - (sprite.myWidth / 2),
								gain.getY() + offsetY + 66 - (sprite.myHeight / 2),
								gain.getAlpha());
						newSmallFont.drawBasicString("<trans=" + gain.getAlpha()
								+ ">+" + format.format(gain.getXP()) + "xp",
								x + 30, gain.getY() + offsetY + 70, 0xCC6600, 0);
					} else {
						if(gain.getSkill() == -244 && gain.getXP() == -244)
							newSmallFont.drawBasicString("XP LOCKED!",
									x + 30, gain.getY() + offsetY + 50, 0xCC6600, 0);
					}
				}
				currentIndex++;
			}
		}
	}

	public int hoverPos;


	public Sprite[] oldHitmarks = new Sprite[2];

	public void processMapAreaMouse() {
		if (mouseInRegion(clientWidth - (clientSize == 0 ? 249 : 217),
				clientSize == 0 ? 46 : 3, clientWidth
						- (clientSize == 0 ? 249 : 217) + 34,
						(clientSize == 0 ? 46 : 3) + 34)) {
			hoverPos = 0;// xp counter
		} else if (mouseInRegion(clientSize == 0 ? clientWidth - 58
				: getOrbX(1), getOrbY(1), (clientSize == 0 ? clientWidth - 58
						: getOrbX(1)) + 57, getOrbY(1) + 34)) {
			hoverPos = 1;// prayer
		} else if (mouseInRegion(clientSize == 0 ? clientWidth - 58
				: getOrbX(2), getOrbY(2), (clientSize == 0 ? clientWidth - 58
						: getOrbX(2)) + 57, getOrbY(2) + 34)) {
			hoverPos = 2;// run
		} else if (mouseInRegion(clientSize == 0 ? clientWidth - 74
				: getOrbX(3), getOrbY(3), (clientSize == 0 ? clientWidth - 74
						: getOrbX(3)) + 57, getOrbY(3) + 34)) {
			hoverPos = 3;// summoning
		} else {
			hoverPos = -1;
		}
	}

	public boolean choosingLeftClick;
	public int leftClick;
	public String[] leftClickNames = { "Call Follower", "Dismiss", "Take BoB",
			"Renew Familiar", "Interact", "Attack", "Follower Details", "Cast"
			// "Follower Details", "Attack", "Interact", "Renew Familiar", "Take BoB",
			// "Dismiss", "Call Follower"
	};
	public int[] leftClickActions = { 1018, 1019, 1020, 1021, 1022, 1023, 1024,
			1026 };

	public void rightClickMapArea() {
		if (mouseInRegion(clientWidth - (clientSize == 0 ? 249 : 217),
				clientSize == 0 ? 46 : 3, clientWidth
						- (clientSize == 0 ? 249 : 217) + 34,
						(clientSize == 0 ? 46 : 3) + 34)) {
			menuActionName[1] = "Reset counter";
			menuActionID[1] = 1013;
			menuActionName[2] = showXP ? "Hide counter" : "Show counter";
			menuActionID[2] = 1006;
			menuActionRow = 3;
		}
		if (mouseInRegion(clientSize == 0 ? clientWidth - 58 : getOrbX(1),
				getOrbY(1),
				(clientSize == 0 ? clientWidth - 58 : getOrbX(1)) + 57,
				getOrbY(1) + 34)) {

			String prayerType = (prayerInterfaceType == 5608) ? "prayers" : "curses";
			boolean inProcess = (tabInterfaceIDs[5] == 17200 || tabInterfaceIDs[5] == 17234);
			menuActionName[menuActionRow] = (inProcess ? "Finish" : "Select") + " " + "quick " + prayerType + (inProcess ? " selection" : "");
			menuActionID[menuActionRow] = 1046;
			menuActionRow++;
			menuActionName[menuActionRow] = "Turn quick " + prayerType + " " + (quickPrsActive ? "off" : "on");
			menuActionID[menuActionRow] = 1045;
			menuActionRow++;
		}
		if (mouseInRegion(clientSize == 0 ? clientWidth - 58 : getOrbX(2),
				getOrbY(2),
				(clientSize == 0 ? clientWidth - 58 : getOrbX(2)) + 57,
				getOrbY(2) + 34)) {

			menuActionName[menuActionRow] = "Rest";
			menuActionID[menuActionRow] = 1048;
			menuActionRow++;
			menuActionName[menuActionRow] = "Turn run mode " + (running ? "off" : "on");
			menuActionID[menuActionRow] = 1047;
			menuActionRow++;
		}
		if (mouseInRegion(clientSize == 0 ? 692 : getOrbX(0), getOrbY(0), (clientSize == 0 ? 748 : getOrbX(0)+ 57), getOrbY(0) + 30)) {
			menuActionName[1] = "Cure Poision";
			menuActionID[1] = 10002;
			menuActionName[2] = "Heal";
			menuActionID[2] = 10001;
			menuActionRow = 3;
		}
		int x = super.mouseX; // Face North on compass
		int y = super.mouseY;
		if (x >= 531 && x <= 557 && y >= 7 && y <= 40) {
			menuActionName[1] = "Face North";
			menuActionID[1] = 696;
			menuActionRow = 2;
		}
		if (mouseInRegion(clientSize == 0 ? clientWidth - 74 : getOrbX(3),
				getOrbY(3),
				(clientSize == 0 ? clientWidth - 74 : getOrbX(3)) + 57,
				getOrbY(3) + 34)) {
			menuActionName[4] = "Take BoB";
			menuActionID[4] = 1118;
			menuActionName[3] = "Call Familiar";
			menuActionID[3] = 1119;
			menuActionName[2] = "Renew Familiar";
			menuActionID[2] = 1120;
			menuActionName[1] = "Dismiss Familiar";
			menuActionID[1] = 1121;
			menuActionRow = 5;
		}
	}

	private void drawMinimap() {
		int xPosOffset = clientSize == 0 ? 0 : clientWidth - 246;
		if (clientSize == 0)
			mapAreaIP.initDrawingArea();
		if (minimapStatus == 2) {
			cacheSprite[67].drawSprite((clientSize == 0 ? 32
					: clientWidth - 162), (clientSize == 0 ? 9 : 5));
			if (clientSize == 0) {
				cacheSprite[6].drawSprite(0 + xPosOffset, 0);
			} else {
				cacheSprite[36].drawSprite(clientWidth - 167, 0);
				cacheSprite[37].drawSprite(clientWidth - 172, 0);
			}
			cacheSprite[38].drawSprite(
					clientSize == 0 ? -1 : clientWidth - 188,
							clientSize == 0 ? 46 : 40);
			if (hoverPos == 0) {
				cacheSprite[39].drawSprite(clientSize == 0 ? -1
						: clientWidth - 188, clientSize == 0 ? 46 : 40);
			}
			cacheSprite[30].drawSprite(
					(clientSize == 0 ? 246 : clientWidth) - 21, 0);
			if (tabHover != -1) {
				if (tabHover == 10) {
					cacheSprite[34].drawSprite((clientSize == 0 ? 246
							: clientWidth) - 21, 0);
				}
			}
			if (tabInterfaceIDs[tabID] != -1) {
				if (tabID == 10) {
					cacheSprite[35].drawSprite((clientSize == 0 ? 246
							: clientWidth) - 21, 0);
				}
			}
			drawHPOrb();
			drawPrayerOrb();
			drawRunOrb();
			drawSummoningOrb();
			drawShortcutIcon();
			compass[0].rotate(33, viewRotation, anIntArray1057, 256,
					anIntArray968, 25, (clientSize == 0 ? 8 : 5),
					(clientSize == 0 ? 8 + xPosOffset : clientWidth - 167), 33,
					25);
			gameScreenIP.initDrawingArea();
			return;
		}
		int i = viewRotation + minimapRotation & 0x7ff;
		int j = 48 + myPlayer.x / 32;
		int l2 = 464 - myPlayer.y / 32;
		miniMap.rotate(clientSize == 0 ? 156 : 156, i, minimapYPosArray,
				256 + minimapZoom, minimapXPosArray, l2, (clientSize == 0 ? 8
						: 4/* 5 */),
						(clientSize == 0 ? 32 : clientWidth - 162),
						clientSize == 0 ? 156 : 156, j);
		for (int j5 = 0; j5 < mapFunctionsLoadedAmt; j5++) {
			int k = (mapFunctionTileX[j5] * 4 + 2) - myPlayer.x / 32;
			int i3 = (mapFunctionTileY[j5] * 4 + 2) - myPlayer.y / 32;
			try {
				markMinimap(currentMapFunctionSprites[j5], k, i3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (int k5 = 0; k5 < 104; k5++) {
			for (int l5 = 0; l5 < 104; l5++) {
				Deque class19 = groundArray[plane][k5][l5];
				if (class19 != null) {
					int l = (k5 * 4 + 2) - myPlayer.x / 32;
					int j3 = (l5 * 4 + 2) - myPlayer.y / 32;
					markMinimap(mapDotItem, l, j3);
				}
			}
		}
		for (int i6 = 0; i6 < npcCount; i6++) {
			NPC npc = npcArray[npcIndices[i6]];
			if (npc != null && npc.isVisible()) {
				NPCDef entityDef = npc.desc;// to review
				if (entityDef.childrenIDs != null)
					entityDef = entityDef.getAlteredNPCDef();
				if (entityDef != null && entityDef.drawMinimapDot
						&& entityDef.clickable) {
					int i1 = npc.x / 32 - myPlayer.x / 32;
					int k3 = npc.y / 32 - myPlayer.y / 32;
					markMinimap(mapDotNPC, i1, k3);
				}
			}
		}
		for (int j6 = 0; j6 < playerCount; j6++) {
			Player player = playerArray[playerIndices[j6]];
			if (player != null && player.isVisible()) {
				int j1 = player.x / 32 - myPlayer.x / 32;
				int l3 = player.y / 32 - myPlayer.y / 32;
				boolean flag1 = false;
				long l6 = TextClass.longForName(player.name);
				for (int k6 = 0; k6 < friendsCount; k6++) {
					if (l6 != friendsListAsLongs[k6] || friendsNodeIDs[k6] == 0)
						continue;
					flag1 = true;
					break;
				}
				boolean flag2 = false;
				if (myPlayer.team != 0 && player.team != 0
						&& myPlayer.team == player.team)
					flag2 = true;
				if (flag1)
					markMinimap(mapDotFriend, j1, l3);
				else if (flag2)
					markMinimap(mapDotTeam, j1, l3);
				else
					markMinimap(mapDotPlayer, j1, l3);
			}
		}
		if (anInt855 != 0 && loopCycle % 20 < 10) {
			if (anInt855 == 1 && anInt1222 >= 0 && anInt1222 < npcArray.length) {
				NPC class30_sub2_sub4_sub1_sub1_1 = npcArray[anInt1222];
				if (class30_sub2_sub4_sub1_sub1_1 != null) {
					int k1 = class30_sub2_sub4_sub1_sub1_1.x / 32 - myPlayer.x
							/ 32;
					int i4 = class30_sub2_sub4_sub1_sub1_1.y / 32 - myPlayer.y
							/ 32;
					drawTargetArrow(mapMarker, i4, k1);
				}
			}
			if (anInt855 == 2) {
				int l1 = ((anInt934 - baseX) * 4 + 2) - myPlayer.x / 32;
				int j4 = ((anInt935 - baseY) * 4 + 2) - myPlayer.y / 32;
				drawTargetArrow(mapMarker, j4, l1);
			}
			if (anInt855 == 10 && anInt933 >= 0
					&& anInt933 < playerArray.length) {
				Player class30_sub2_sub4_sub1_sub2_1 = playerArray[anInt933];
				if (class30_sub2_sub4_sub1_sub2_1 != null) {
					int i2 = class30_sub2_sub4_sub1_sub2_1.x / 32 - myPlayer.x
							/ 32;
					int k4 = class30_sub2_sub4_sub1_sub2_1.y / 32 - myPlayer.y
							/ 32;
					drawTargetArrow(mapMarker, k4, i2);
				}
			}
		}
		if (destX != 0) {
			int j2 = (destX * 4 + 2) - myPlayer.x / 32;
			int l4 = (destY * 4 + 2) - myPlayer.y / 32;
			markMinimap(mapFlag, j2, l4);
		}
		DrawingArea.drawPixels(3, (clientSize == 0 ? 84 : 80),
				(clientSize == 0 ? 107 + xPosOffset : clientWidth - 88),
				0xffffff, 3);
		if (clientSize == 0) {
			cacheSprite[6].drawSprite(0 + xPosOffset, 0);
		} else {
			cacheSprite[36].drawSprite(clientWidth - 167, 0);
			cacheSprite[37].drawSprite(clientWidth - 172, 0);
		}
		cacheSprite[38].drawSprite(clientSize == 0 ? -1 : clientWidth - 217,
				clientSize == 0 ? 46 : 3);
		if (hoverPos == 0) {
			cacheSprite[39].drawSprite(
					clientSize == 0 ? -1 : clientWidth - 217,
							clientSize == 0 ? 46 : 3);
		}
		cacheSprite[30].drawSprite((clientSize == 0 ? 246 : clientWidth) - 21,
				0);
		if (tabHover != -1) {
			if (tabHover == 10) {
				cacheSprite[34].drawSprite(
						(clientSize == 0 ? 246 : clientWidth) - 21, 0);
			}
		}
		if (tabInterfaceIDs[tabID] != -1) {
			if (tabID == 10) {
				cacheSprite[35].drawSprite(
						(clientSize == 0 ? 246 : clientWidth) - 21, 0);
			}
		}
		drawHPOrb();
		drawPrayerOrb();
		drawRunOrb();
		drawSummoningOrb();
		drawShortcutIcon();
		drawCoinOrb();
		compass[0].rotate(33, viewRotation, anIntArray1057, 256, anIntArray968,
				25, (clientSize == 0 ? 8 : 5),
				(clientSize == 0 ? 8 + xPosOffset : clientWidth - 167), 33, 25);
		if (menuOpen && menuScreenArea == 3)
			drawMenu();
		gameScreenIP.initDrawingArea();
	}

	public int tabHover = -1;
	public boolean showTab = true;
	private int smallTabs = 1000;
	int[] draw_tab_hover_x = { 0, 30, 60, 120, 150, 180, 210, 90, 30,
			60, -1, 120, 150, 180, 90, 0, 210 };
	int[] draw_Tab_hover_y = { 0, 0, 0, 0, 0, 0, 0, 298, 298, 298, -1,
			298, 298, 298, 0, 298, 298 };
	int[] draw_tab_hover_tab = { 0, 1, 2, 14, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13,
			16 };
	int[] draw_Tab_hover_x = { 0, 30, 60, 90, 120, 150, 180, 210, 0, 30,
			60, 90, 120, 150, 180, 210 };
	public void drawTabHover(boolean fixed) {
		if (fixed) {
			if (tabHover != -1) {
				if (tabInterfaceIDs[tabHover] != -1) {
					if (tabHover != 10) {
						cacheSprite[16].drawSprite(3 + draw_tab_hover_x[tabHover],
								draw_Tab_hover_y[tabHover]);
					}
				}
			}
		} else {
			if (tabHover != -1) {
				int offsetX = 0;
				for (int index = 0; index < draw_tab_hover_tab.length; index++) {
					if (tabInterfaceIDs[draw_tab_hover_tab[index]] != -1) {
						if (tabHover == draw_tab_hover_tab[index]) {
							offsetX = index > 7 && clientWidth >= smallTabs ? 240
									: 0;
							cacheSprite[16]
									.drawARGBSprite(
											(clientWidth - (clientWidth >= smallTabs ? 480
													: 240))
													+ draw_Tab_hover_x[index]
															+ offsetX,
															clientHeight
															- (clientWidth >= smallTabs ? 37
																	: (index < 8 ? 74
																			: 37)));
						}
					}
				}
			}
		}
	}

	Sprite dungeoneeringIcon;

	Sprite[] oldGameframeArea = new Sprite[7];
	public void handleTabArea(boolean fixed) {
		if (fixed) {
			if(!getOption("old_frame"))
				cacheSprite[13].drawSprite(0, 0);
			else
				oldGameframeArea[0].drawSprite(0, 0);
		} else {
			if (clientWidth >= smallTabs) {
				for (int positionX = clientWidth - 480, positionY = clientHeight - 37, index = 0; positionX <= clientWidth - 30
						&& index < 16; positionX += 30, index++) {
					cacheSprite[15].drawSprite(positionX, positionY);
				}
				if (showTab) {
					cacheSprite[18].drawTransparentSprite(clientWidth - 197,
							clientHeight - 37 - 267, 150);
					cacheSprite[19].drawSprite(clientWidth - 204,
							clientHeight - 37 - 274);
				}
			} else {
				for (int positionX = clientWidth - 240, positionY = clientHeight - 74, index = 0; positionX <= clientWidth - 30
						&& index < 8; positionX += 30, index++) {
					cacheSprite[15].drawSprite(positionX, positionY);
				}
				for (int positionX = clientWidth - 240, positionY = clientHeight - 37, index = 0; positionX <= clientWidth - 30
						&& index < 8; positionX += 30, index++) {
					cacheSprite[15].drawSprite(positionX, positionY);
				}
				if (showTab) {
					cacheSprite[18].drawTransparentSprite(clientWidth - 197,
							clientHeight - 74 - 267, 150);
					cacheSprite[19].drawSprite(clientWidth - 204,
							clientHeight - 74 - 274);
				}
			}
		}
		if (invOverlayInterfaceID == -1) {
			if(!getOption("old_frame"))
				drawTabHover(fixed);
			if (showTab) {
				drawTabs(fixed);
			}
			drawSideIcons(fixed);
		}
	}

	public void drawTabs(boolean fixed) {
		if (fixed) {
			int xPos = 2;
			int yPos = 0;
			if (tabID < tabInterfaceIDs.length && tabInterfaceIDs[tabID] != -1) {
				switch (tabID) {
				case 0:
				case 1:
				case 2:
					if(!getOption("old_frame"))
						xPos += tabID * 30;
					else {
						if(tabID == 0)
							xPos = 6;
						else if(tabID == 1)
							xPos = 46;
						else if(tabID == 2)
							xPos = 77;
					}
					yPos = 0;
					break;
				case 3:
				case 4:
				case 5:
				case 6:
					if(!getOption("old_frame"))
						xPos += (tabID + 1) * 30;
					else {
						if(tabID == 3)
							xPos = 111;
						else if(tabID == 4)
							xPos = 145;
						else if(tabID == 5)
							xPos = 178;
						else if(tabID == 6)
							xPos = 211;
					}
					yPos = 0;
					break;
				case 7:
					if(!getOption("old_frame"))
						xPos = 2 + ((tabID - 4) * 30);
					else
						xPos = 111;
					yPos = 299;
					break;
				case 8:
				case 9:
				case 11:
				case 12:
				case 13:
					if(!getOption("old_frame"))
						xPos = 2 + ((tabID - 7) * 30);
					else {
						if(tabID == 8)
							xPos = 45;
						else if(tabID == 9)
							xPos = 78;
						else if(tabID == 11)
							xPos = 144;
						else if(tabID == 12)
							xPos = 177;
					}
					yPos = 299;
					break;
				case 14:
					yPos = 0;
					if(!getOption("old_frame"))
						xPos = 92;
					else {
						xPos = 210;
						yPos = 299;
					}
					break;
				case 15:
					if(!getOption("old_frame"))
						xPos = 2;
					else
						xPos = 7;
					yPos = 299;
					break;
				case 16:
					xPos = 212;
					yPos = 299;
					break;
				}
				if (tabID != 10) {
					if(getOption("old_frame")) {
						if(tabID > 6)
							yPos--;
						if(tabID == 0)
							oldGameframeArea[1].drawSprite(xPos - 4, yPos);
						else if(tabID == 6)
							oldGameframeArea[2].drawSprite(xPos - 4, yPos);
						else if(tabID == 15)
							oldGameframeArea[3].drawSprite(xPos - 4, yPos);
						else if(tabID == 14)
							oldGameframeArea[4].drawSprite(xPos - 4, yPos);
						else
							oldGameframeArea[5].drawSprite(xPos - 4, yPos);
					} else
						cacheSprite[17].drawARGBSprite(xPos - 4, yPos);
				}
			}
		} else {
			for (int index = 0; index < TAB_DRAW.length; index++) {
				int offsetX = clientWidth >= smallTabs ? 481 : 241;
				if (offsetX == 481 && index > 7) {
					offsetX -= 240;
				}
				int offsetY = clientWidth >= smallTabs ? 37 : (index > 7 ? 37
						: 74);
				if (tabID == TAB_DRAW[index] && tabInterfaceIDs[TAB_DRAW[index]] != -1) {
					cacheSprite[17].drawARGBSprite((clientWidth - offsetX - 4)
							+ TAB_POS[index], (clientHeight - offsetY) + 0);
				}
			}
		}
	}
	int[] TAB_DRAW = { 0, 1, 2, 14, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13, 16 };
	int[] TAB_POS = { 0, 30, 60, 90, 120, 150, 180, 210, 0, 30, 60,
			90, 120, 150, 180, 210 };
	int[] SIDEICONS_TAB = { 0, 1, 2, 14, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13, 16 };
	int[] SIDEICONS_POSITION_X = { 8, 37, 67, 97, 127, 159, 187, 217, 7, 38, 69, 97, 127, 157, 187, 217 };
	int[] SIDEICONS_POSITION_Y = { 9, 9, 8, 8, 8, 8, 8, 8, 307, 306, 306, 307,
			306, 306, 306, 308 };
	int[] SIDEICONS_OLD_FRAME_POSITIONX =  new int[] { 13, 46, 80, 113, 112, 148, 179, 212, 9, 46, 81,
			111, 145, 178, 212, 217 };
	int[] SIDEICONS_OLD_FRAME_POSITIONY = new int[] { 9, 9, 8, 8, 8, 8, 8, 8, 304, 306, 306, 307,
			306, 306, 306, 308 };
	int[] id = { 20, 89, 21, 22, 23, 24, 25, 26, 95, 28, 29, 27, 31,
			32, 33, 90, 149 };
	int[] tab = { 0, 1, 2, 14, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, 13, 16 };
	int[] positionX = { 8, 37, 67, 97, 127, 159, 187, 217, 7, 38, 69,
			97, 127, 157, 187, 217 };
	int[] positionY = { 9, 9, 8, 8, 8, 8, 8, 8, /* second row */8, 8,
			8, 9, 8, 8, 8, 9 };

	boolean doingDung = false;

	public void drawSideIcons(boolean fixed) {
		if (fixed) {
			for (int index = 0; index < 15; index++) {
				if(index == 3 && getOption("old_frame")) //Skip achievement tab, it does not fit
					continue;
				if(index == 8 && getOption("old_frame") && tabInterfaceIDs[SIDEICONS_TAB[index]] < 0)
					tabInterfaceIDs[SIDEICONS_TAB[index]] = 54017;
				else if(index == 8 && !getOption("old_frame") && tabInterfaceIDs[SIDEICONS_TAB[index]] > 0)
					tabInterfaceIDs[SIDEICONS_TAB[index]] = -1;
				if(index == 14 && !getOption("old_frame"))
					tabInterfaceIDs[SIDEICONS_TAB[index]] = 54017;
				else if(index == 14 && getOption("old_frame") && tabInterfaceIDs[SIDEICONS_TAB[index]] < 0)
					tabInterfaceIDs[SIDEICONS_TAB[index]] = 15001;
				int l = index == 14 && getOption("old_frame") ? 3 : index;
				if (tabInterfaceIDs[SIDEICONS_TAB[l]] != -1) {
					int x = getOption("old_frame") ? SIDEICONS_OLD_FRAME_POSITIONX[index] : SIDEICONS_POSITION_X[index];
					int y = getOption("old_frame") ? SIDEICONS_OLD_FRAME_POSITIONY[index] : SIDEICONS_POSITION_Y[index];
					int draw = l == 3 && getOption("old_frame") ? 15 : l;
					if(index == 2 && doingDung) {
						x -= 1;
						draw = 16;
					}
					sideIcons[draw].drawSprite(x, y);
				}
			}
		} else {
			for (int index = 0; index < 14; index++) {
				int offsetX = clientWidth >= smallTabs ? 482 : 242;
				if (offsetX == 482 && index > 7) {
					offsetX -= 240;
				}
				int offsetY = clientWidth >= smallTabs ? 37 : (index > 7 ? 37
						: 74);
				if (tabInterfaceIDs[tab[index]] != -1 || index == 8) {
					if(index == 8)
						tabInterfaceIDs[tab[index]] = 54017;
					int i = index;
					if(index == 2 && doingDung)
						i = 16;
					sideIcons[i].drawSprite((clientWidth - offsetX) + positionX[index], (clientHeight - offsetY) + positionY[index]);
				}
			}
		}
	}

	private void drawTabArea() {
		if (clientSize == 0) {
			tabAreaIP.initDrawingArea();
		}
		Rasterizer.lineOffsets = anIntArray1181;
		handleTabArea(clientSize == 0);
		int y = clientWidth >= smallTabs ? 37 : 74;
		if (showTab) {
			if (invOverlayInterfaceID != -1) {
				drawInterface(0, (clientSize == 0 ? 28 : clientWidth - 197),
						RSInterface.interfaceCache[invOverlayInterfaceID],
						(clientSize == 0 ? 37 : clientHeight - y - 267));
			} else if (tabInterfaceIDs[tabID] != -1) {
				drawInterface(0, (clientSize == 0 ? 28 : clientWidth - 197),
						RSInterface.interfaceCache[tabInterfaceIDs[tabID]],
						(clientSize == 0 ? 37 : clientHeight - y - 267));
			}
			if (menuOpen && menuScreenArea == 1)
				drawMenu();
		}
		if (clientSize == 0)
			tabAreaIP.drawGraphics(168, super.graphics, 519);
		gameScreenIP.initDrawingArea();
		Rasterizer.lineOffsets = anIntArray1182;
	}

	private void processTabAreaTooltips(int TabHoverId) {
		if(getOption("old_frame"))
			return;
		boolean fixed = clientSize == 0;
		String[] tooltipString = { "Combat Styles", "Stats", "Player Panel",
				"Inventory", "Worn Equipment", "Prayer List",
				"Magic Spellbook", "Clan Chat", "Friends List", "Ignore List",
				"Ignore List", "Options", "Emotes", fixed ? "Summoning" : "", "Achievements", fixed ? "" : "Summoning", ""};
		if(tooltipString[TabHoverId] == "")
			return;
		menuActionName[1] = tooltipString[TabHoverId];
		menuActionID[1] = 1076;
		menuActionRow = 2;
		tooltipString = null;
	}

	private void processTabAreaHovers() {
		//Moneypouch hover
		int orbX = getCoinOrbX(); int orbY = getCoinOrbY();
		if (clientSize == 0 ?  mouseInRegion(515, 85, 515 + 34, 85 + 34) : mouseInRegion(orbX, orbY, orbX + 34, orbY + 34)) {
			menuActionName[4] = coinToggle ? "Close Money Pouch" : "Open Money Pouch";
			menuActionID[4] = 72667;
			menuActionName[3] = "Withdraw Money Pouch";
			menuActionID[3] = 72666;
			menuActionName[2] = "Open Pricechecker";
			menuActionID[2] = 72668;
			menuActionName[1] = "Examine Money Pouch";
			menuActionID[1] = 72669;
			menuActionRow = 5;
		}
		if (clientSize == 0) {
			if(hoverShortcutIcon()) {
				menuActionName[1] = "Open Shortcuts";
				menuActionID[1] = 72670;
				menuActionRow = 2;
			}
			int positionX = clientWidth - 244;
			int positionY = 169, positionY2 = clientHeight - 36;
			if (mouseInRegion(clientWidth - 21, 0, clientWidth, 21)) {
				tabHover = 10;
				processTabAreaTooltips(tabHover);
			} else if (mouseInRegion(positionX, positionY, positionX + 30,
					positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 0;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 30, positionY, positionX + 60,
					positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 1;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 60, positionY, positionX + 90,
					positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 2;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 90, positionY,
					positionX + 120, positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 14;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 120, positionY,
					positionX + 150, positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 3;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 150, positionY,
					positionX + 180, positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 4;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 180, positionY,
					positionX + 210, positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 5;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 210, positionY,
					positionX + 240, positionY + 36)) {
				needDrawTabArea = true;
				tabHover = 6;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX, positionY2, positionX + 30,
					positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 15;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 30, positionY2,
					positionX + 60, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 8;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 60, positionY2,
					positionX + 90, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 9;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 90, positionY2,
					positionX + 120, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 7;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 120, positionY2,
					positionX + 150, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 11;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 150, positionY2,
					positionX + 180, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 12;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 180, positionY2,
					positionX + 210, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 13;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX + 210, positionY2,
					positionX + 240, positionY2 + 36)) {
				needDrawTabArea = true;
				tabHover = 16;
				processTabAreaTooltips(tabHover);
				tabAreaAltered = true;
			} else {
				needDrawTabArea = true;
				tabHover = -1;
				tabAreaAltered = true;
			}
		} else {
			int offsetX = (clientWidth >= smallTabs ? clientWidth - 480
					: clientWidth - 240);
			int positionY = (clientWidth >= smallTabs ? clientHeight - 37
					: clientHeight - 74);
			int secondPositionY = clientHeight - 37;
			int secondOffsetX = clientWidth >= smallTabs ? 240 : 0;
			if (mouseInRegion(clientWidth - 21, 0, clientWidth, 21)) {
				tabHover = 10;
			} else if (mouseInRegion(positionX[0] + offsetX, positionY,
					positionX[0] + offsetX + 30, positionY + 37)) {
				tabHover = tab[0];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[1] + offsetX, positionY,
					positionX[1] + offsetX + 30, positionY + 37)) {
				tabHover = tab[1];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[2] + offsetX, positionY,
					positionX[2] + offsetX + 30, positionY + 37)) {
				tabHover = tab[2];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[3] + offsetX, positionY,
					positionX[3] + offsetX + 30, positionY + 37)) {
				tabHover = tab[3];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[4] + offsetX, positionY,
					positionX[4] + offsetX + 30, positionY + 37)) {
				tabHover = tab[4];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[5] + offsetX, positionY,
					positionX[5] + offsetX + 30, positionY + 37)) {
				tabHover = tab[5];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[6] + offsetX, positionY,
					positionX[6] + offsetX + 30, positionY + 37)) {
				tabHover = tab[6];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[7] + offsetX, positionY,
					positionX[7] + offsetX + 30, positionY + 37)) {
				tabHover = tab[7];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[8] + offsetX + secondOffsetX,
					secondPositionY, positionX[8] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[8];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[9] + offsetX + secondOffsetX,
					secondPositionY, positionX[9] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[9];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[10] + offsetX + secondOffsetX,
					secondPositionY, positionX[10] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[10];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[11] + offsetX + secondOffsetX,
					secondPositionY, positionX[11] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[11];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[12] + offsetX + secondOffsetX,
					secondPositionY, positionX[12] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[12];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[13] + offsetX + secondOffsetX,
					secondPositionY, positionX[13] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				tabHover = tab[13];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;
			} else if (mouseInRegion(positionX[14] + offsetX + secondOffsetX,
					secondPositionY, positionX[14] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				/*	tabHover = tab[14];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;*/
			} else if (mouseInRegion(positionX[15] + offsetX + secondOffsetX,
					secondPositionY, positionX[15] + offsetX + secondOffsetX
					+ 30, secondPositionY + 37)) {
				/*tabHover = tab[15];
				processTabAreaTooltips(tabHover);
				needDrawTabArea = true;
				tabAreaAltered = true;*/
			} else {
				tabHover = -1;
				needDrawTabArea = true;
				tabAreaAltered = true;
			}
		}
	}

	private void processTabAreaClick() {
		if (clickInRegion(clientWidth - 21, 0, clientWidth, 21)
				&& tabInterfaceIDs[10] != -1) {
			needDrawTabArea = true;
			tabID = 10;
			tabAreaAltered = true;
		}
		if (clientSize == 0) {
			int positionX = clientWidth - 244;
			int positionY = 169;
			if (super.clickMode3 == 1) {
				if(!getOption("old_frame")) {
					if (clickInRegion(positionX, positionY, positionX + 30, positionY + 36) && tabInterfaceIDs[0] != -1) {
						needDrawTabArea = true;
						tabID = 0;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30, positionY + 36) && tabInterfaceIDs[1] != -1) {
						needDrawTabArea = true;
						tabID = 1;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[2] != -1) {
						needDrawTabArea = true;
						tabID = 2;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[14] != -1) {
						needDrawTabArea = true;
						tabID = 14;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[3] != -1) {
						needDrawTabArea = true;
						tabID = 3;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[4] != -1) {
						needDrawTabArea = true;
						tabID = 4;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[5] != -1) {
						needDrawTabArea = true;
						tabID = 5;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[6] != -1) {
						needDrawTabArea = true;
						tabID = 6;
						tabAreaAltered = true;
					}
					positionX = clientWidth - 244;
					positionY = clientHeight - 36; //89
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[15] != -1) {
						needDrawTabArea = true;
						tabID = 15;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[8] != -1) {
						needDrawTabArea = true;
						tabID = 8;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[9] != -1) {
						needDrawTabArea = true;
						tabID = 9;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[7] != -1) {
						needDrawTabArea = true;
						tabID = 7;
						tabAreaAltered = true;
					}
					positionX += 30;

					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[11] != -1) {
						needDrawTabArea = true;
						tabID = 11;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[12] != -1) {
						needDrawTabArea = true;
						tabID = 12;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[13] != -1) {
						needDrawTabArea = true;
						tabID = 13;
						tabAreaAltered = true;
					}
					positionX += 30;
					if (clickInRegion(positionX, positionY, positionX + 30,
							positionY + 36) && tabInterfaceIDs[16] != -1) {
						needDrawTabArea = true;
						tabID = 16;
						tabAreaAltered = true;
					}
				} else {
					if (clickInRegion(positionX, positionY, 559, positionY + 36) && tabInterfaceIDs[0] != -1) {
						needDrawTabArea = true;
						tabID = 0;
						tabAreaAltered = true;
					}
					if (clickInRegion(560, positionY, 593, positionY + 36) && tabInterfaceIDs[1] != -1) {
						needDrawTabArea = true;
						tabID = 1;
						tabAreaAltered = true;
					}
					if (clickInRegion(594, positionY, 625, positionY + 36) && tabInterfaceIDs[2] != -1) {
						needDrawTabArea = true;
						tabID = 2;
						tabAreaAltered = true;
					}
					if (clickInRegion(625, positionY, 660, positionY + 36) && tabInterfaceIDs[3] != -1) {
						needDrawTabArea = true;
						tabID = 3;
						tabAreaAltered = true;
					}
					if (clickInRegion(660, positionY, 694, positionY + 36) && tabInterfaceIDs[4] != -1) {
						needDrawTabArea = true;
						tabID = 4;
						tabAreaAltered = true;
					}
					if (clickInRegion(694, positionY, 725, positionY + 36) && tabInterfaceIDs[5] != -1) {
						needDrawTabArea = true;
						tabID = 5;
						tabAreaAltered = true;
					}
					if (clickInRegion(725, positionY, 763, positionY + 36) && tabInterfaceIDs[6] != -1) {
						needDrawTabArea = true;
						tabID = 6;
						tabAreaAltered = true;
					}
					positionY = 464;
					if (clickInRegion(positionX, positionY, 559, positionY + 36) && tabInterfaceIDs[15] != -1) {
						needDrawTabArea = true;
						tabID = 15;
						tabAreaAltered = true;
					}
					if (clickInRegion(560, positionY, 593, positionY + 36) && tabInterfaceIDs[8] != -1) {
						needDrawTabArea = true;
						tabID = 8;
						tabAreaAltered = true;
					}
					if (clickInRegion(594, positionY, 625, positionY + 36) && tabInterfaceIDs[9] != -1) {
						needDrawTabArea = true;
						tabID = 9;
						tabAreaAltered = true;
					}
					if (clickInRegion(625, positionY, 660, positionY + 36) && tabInterfaceIDs[7] != -1) {
						needDrawTabArea = true;
						tabID = 7;
						tabAreaAltered = true;
					}
					if (clickInRegion(660, positionY, 694, positionY + 36) && tabInterfaceIDs[11] != -1) {
						needDrawTabArea = true;
						tabID = 11;
						tabAreaAltered = true;
					}

					if (clickInRegion(694, positionY, 725, positionY + 36) && tabInterfaceIDs[5] != 12) {
						needDrawTabArea = true;
						tabID = 12;
						tabAreaAltered = true;
					}
					if (clickInRegion(725, positionY, 763, positionY + 36) && tabInterfaceIDs[14] != -1) {
						needDrawTabArea = true;
						tabID = 14;
						tabAreaAltered = true;
					}
				}
			}
		} else {

			int offsetX = (clientWidth >= smallTabs ? clientWidth - 480
					: clientWidth - 240);
			int positionY = (clientWidth >= smallTabs ? clientHeight - 37
					: clientHeight - 74);
			int secondPositionY = clientHeight - 37;
			int secondOffsetX = clientWidth >= smallTabs ? 240 : 0;
			if (super.clickMode3 == 1) {
				if (clickInRegion(positionX[0] + offsetX, positionY,
						positionX[0] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[0]] != -1) {
					if (tabID == tab[0]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[0];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[1] + offsetX, positionY,
						positionX[1] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[1]] != -1) {
					if (tabID == tab[1]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[1];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[2] + offsetX, positionY,
						positionX[2] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[2]] != -1) {
					if (tabID == tab[2]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[2];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[3] + offsetX, positionY,
						positionX[3] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[3]] != -1) {
					if (tabID == tab[3]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[3];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[4] + offsetX, positionY,
						positionX[4] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[4]] != -1) {
					if (tabID == tab[4]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[4];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[5] + offsetX, positionY,
						positionX[5] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[5]] != -1) {
					if (tabID == tab[5]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[5];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[6] + offsetX, positionY,
						positionX[6] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[6]] != -1) {
					if (tabID == tab[6]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[6];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[7] + offsetX, positionY,
						positionX[7] + offsetX + 30, positionY + 37)
						&& tabInterfaceIDs[tab[7]] != -1) {
					if (tabID == tab[7]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[7];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(
						positionX[8] + offsetX + secondOffsetX,
						secondPositionY, positionX[8] + offsetX + secondOffsetX
						+ 30, secondPositionY + 37)) {
					if (tabID == tab[8]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[8];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(
						positionX[9] + offsetX + secondOffsetX,
						secondPositionY, positionX[9] + offsetX + secondOffsetX
						+ 30, secondPositionY + 37)) {
					if (tabID == tab[9]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[9];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[10] + offsetX
						+ secondOffsetX, secondPositionY, positionX[10]
								+ offsetX + secondOffsetX + 30, secondPositionY + 37)) {
					if (tabID == tab[10]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[10];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[11] + offsetX
						+ secondOffsetX, secondPositionY, positionX[11]
								+ offsetX + secondOffsetX + 30, secondPositionY + 37)) {
					if (tabID == tab[11]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[11];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[12] + offsetX
						+ secondOffsetX, secondPositionY, positionX[12]
								+ offsetX + secondOffsetX + 30, secondPositionY + 37)) {
					if (tabID == tab[12]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[12];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[13] + offsetX
						+ secondOffsetX, secondPositionY, positionX[13]
								+ offsetX + secondOffsetX + 30, secondPositionY + 37)) {
					if (tabID == tab[13]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[13];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[14] + offsetX
						+ secondOffsetX, secondPositionY, positionX[14]
								+ offsetX + secondOffsetX + 30, secondPositionY + 37)) {
					if (tabID == tab[14]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[14];
					needDrawTabArea = true;
					tabAreaAltered = true;
				} else if (clickInRegion(positionX[15] + offsetX
						+ secondOffsetX, secondPositionY, positionX[15]
								+ offsetX + secondOffsetX + 30, secondPositionY + 37)) {
					if (tabID == tab[15]) {
						showTab = !showTab;
					} else {
						showTab = true;
					}
					tabID = tab[15];
					needDrawTabArea = true;
					tabAreaAltered = true;
				}
			}
		}
	}

	public int MapX, MapY;
	public static int spellID = 0;
	public static boolean newDamage;
	public Sprite magicAuto;
	public boolean xpLock;
	public int xpCounter;
	public int followPlayer = 0;
	public int followNPC = 0;
	public int followDistance = 1;
	public boolean downloading = false;
	public String clanName = "";
	public boolean buttonclicked = false;
	public String TalkingFix = "";
	/*	public int GEItemId = 0;
	public Sprite Search;
	public Sprite search;*/

	public void magicOnItem(int id) {
		spellSelected = 1;
		spellID = id;
		selectedSpellId = id;
		spellUsableOn = 16;
		itemSelected = 0;
		needDrawTabArea = true;
		spellTooltip = "Cast on";
		needDrawTabArea = true;
		tabID = 3;
		tabAreaAltered = true;
	}

	public final String formatNumberToLetter(int j) {
		if (j >= 0 && j < 10000)
			return String.valueOf(j);
		if (j >= 10000 && j < 10000000)
			return j / 1000 + "K";
		if (j >= 10000000 && j < 999999999)
			return j / 1000000 + "M";
		if (j >= 999999999)
			return "*";
		else
			return "?";
	}

	public static final byte[] ReadFile(String s) {
		try {
			byte abyte0[];
			File file = new File(s);
			int i = (int) file.length();
			abyte0 = new byte[i];
			DataInputStream datainputstream = new DataInputStream(
					new BufferedInputStream(new FileInputStream(s)));
			datainputstream.readFully(abyte0, 0, i);
			datainputstream.close();
			return abyte0;
		} catch (Exception e) {
			System.out.println((new StringBuilder()).append("Read Error: ")
					.append(s).toString());
			return null;
		}
	}

	public static final int MAP_IDX = 4;

	public static final int MODEL_IDX = 1, CONFIG_IDX = 0;

	public static boolean displayScrollbar;

	public void displayItemSearch() {
		/*	int yPosOffset = (clientSize > 0) ? clientHeight - 165 : 0;
		int xPosOffset = 0;
		try {
			quickChat = false;
			if (amountOrNameInput != "") {
				itemSearch(amountOrNameInput);
			}
			cacheSprite[64].drawSprite(0 + xPosOffset, 0 + yPosOffset);
			DrawingArea.setDrawingArea(121 + yPosOffset, 8, 512, 7);
			SpriteCache.spriteCache[678].drawSprite(18, 18 + yPosOffset);
			for (int j = 0; j < totalItemResults; j++) {
				int x = super.mouseX;
				int y = super.mouseY;
				final int yPos = 21 + j * 14 - itemResultScrollPos;
				if (yPos > 0 && yPos < 210) {
					String n = itemResultNames[j];
					for (int i = 0; i <= 20; i++)
						if (n.contains("<img=" + i + ">"))
							n = n.replaceAll("<img=" + i + ">", "");
					chatTextDrawingArea.method591(capitalizeFirstChar(n), 78,
							0xA05A00, yPos + yPosOffset
							+ (totalItemResults < 8 ? 6 : 0));
					if (x > 74
							&& x < 495
							&& y > ((clientSize == 0) ? 338
									: clientHeight - 165)
									+ yPos
									- 13
									+ (totalItemResults < 8 ? 6 : 0)
									&& y < ((clientSize == 0) ? 338
											: clientHeight - 165)
											+ yPos
											+ 2
											+ (totalItemResults < 8 ? 6 : 0)) {
						DrawingArea.fillRectangle(0x807660, yPos - 12
								+ yPosOffset + (totalItemResults < 8 ? 6 : 0),
								424, 15, 60, 75);
						Sprite itemImg = ItemDef.getSprite(itemResultIDs[j], 1,
								0);
						if (itemImg != null)
							itemImg.drawSprite(22, 20 + yPosOffset);
						//GEItemId = itemResultIDs[j];
					}
				}
			}
			DrawingArea.drawPixels(113, 8 + yPosOffset, 74, 0x807660, 2);
			DrawingArea.defaultDrawingAreaSize();
			if (totalItemResults > 8) {
				displayScrollbar = true;
				drawScrollbar(114, itemResultScrollPos, 8 + yPosOffset,
						496 + xPosOffset, totalItemResults * 14, false, false);
				// drawScrollbar(112, itemResultScrollPos, 8, 496,
				// totalItemResults * 14 + 12, 0);
			} else {
				displayScrollbar = false;
			}
			boolean showMatches = true;
			showMatches = true;
			if (amountOrNameInput.length() == 0) {
				chatTextDrawingArea.drawCenteredText(0xA05A00, 259,
						"Grand Exchange Item Search", 30 + yPosOffset, false);
				smallText
				.drawCenteredText(
						0xA05A00,
						259,
						"To search for an item, start by typing part of it's name.",
						80 + yPosOffset, false);
				smallText
				.drawCenteredText(
						0xA05A00,
						259,
						"Then, simply select the item you want from the results on the display.",
						80 + 15 + yPosOffset, false);
				// chatTextDrawingArea.drawText(0xffffff, amountOrNameInput +
				// "*", 32, 133);
				showMatches = false;
			}
			if (totalItemResults == 0 && showMatches) {
				smallText.drawCenteredText(0xA05A00, 259,
						"No matching items found", 80 + yPosOffset, false);
			}
			DrawingArea.fillRectangle(0x807660, 121 + yPosOffset, 506, 15, 120,
					7);// box
			// chatTextDrawingArea.drawText(0, "<img=8>", 133, 12);
			chatTextDrawingArea.method591(amountOrNameInput + "*",
					28 + xPosOffset, 0xffffff, 133 + yPosOffset);
			// chatTextDrawingArea.drawText(0xffffff, amountOrNameInput + "*",
			// 133, 122);
			DrawingArea.drawLine(121 + yPosOffset, 0x807660, 506, 7);// line
			// drawClose(496, 122, 496, 345 + 112, 496 + 19, 361 + 112);
			// drawClose(496, 122, 496, 345 + 112, 496 + 19, 361 + 112);
			// drawClose(496, 122, 496, 345 + 112, 496 + 19, 361 + 112);

			SpriteCache.spriteCache[679].drawSprite(11, 122 + yPosOffset);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	public void drawClose(int x, int y, int x2, int y2, int x3, int y3) {
		cacheSprite[31].drawSprite(x, y);
		if (super.mouseX >= x2 && super.mouseX <= x3 && super.mouseY >= y2
				&& super.mouseY <= y3) {
			cacheSprite[32].drawSprite(x, y);
		}
	}

	public int interfaceButtonAction = 0;

	void sendPacket(int packet) {
		if (packet == 103) {
			stream.createFrame(103);
			stream.writeWordBigEndian(inputString.length() - 1);
			stream.writeString(inputString.substring(2));
			inputString = "";
			promptInput = "";
			interfaceButtonAction = 0;
		}
		if (packet == 1003) {
			stream.createFrame(103);
			inputString = "::" + inputString;
			stream.writeWordBigEndian(inputString.length() - 1);
			stream.writeString(inputString.substring(2));
			inputString = "";
			promptInput = "";
			interfaceButtonAction = 0;
		}
	}

	public void playSong(int id) {
		if (currentSong != id) {
			nextSong = id;
			songChanging = true;
			onDemandFetcher.requestFileData(2, nextSong);
			currentSong = id;
		}
	}

	public void stopMidi() {
		signlink.fadeMidi = 0;
		signlink.midi = "stop";
		try {
			signlink.music.stop();
		} catch (Exception e) {
		}
	}

	private void adjustVolume(boolean updateMidi, int volume) {
		try {
			signlink.setVolume(volume);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		if (updateMidi) {
			signlink.midi = "voladjust";
		}
	}

	private boolean menuHasAddFriend(int j) {
		if (j < 0)
			return false;
		int k = menuActionID[j];
		if (k >= 2000)
			k -= 2000;
		return k == 337;
	}

	private Sprite min;
	private int minY = 0, timer = 0;
	private boolean fin = true, up = false;

	private void resetAnAnim(int w) {
		minY = 0;
		minY -= 140;
		timer = 0;
		fin = false;
		up = false;
	}

	public void processAnAnim() {
		if (!fin) {
			if (minY <= 10 && !up) {
				minY += 2;
			} else {
				up = true;
				timer++;
				if (timer > 30)
					minY -= 2;
			}
			if (minY == -145) {
				fin = true;
			}
			SpriteCache.fetchIfNeeded(676);
			if (min == null)
				min = SpriteCache.spriteCache[676];
			if (min != null)
				min.drawSprite(290, minY);
		}
	}

	public static int getLevelForXP(int exp) {
		int points = 0;
		int output = 0;
		if (exp > 13034430) {
			return 99;
		}
		for (int lvl = 1; lvl <= 99; lvl++) {
			points += Math.floor((double) lvl + 300.0
					* Math.pow(2.0, (double) lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if (output >= exp) {
				return lvl;
			}
		}
		return 0;
	}

	public String[] skillNames = { "Attack", "Hitpoints", "Mining", "Strength",
			"Agility", "Smithing", "Defence", "Herblore", "Fishing", "Range",
			"Thieving", "Cooking", "Prayer", "Crafting", "Firemaking", "Magic",
			"Fletching", "Woodcutting", "Runecrafting", "Slayer", "Farming",
			"Construction", "Hunter", "Summoning", "Dungeoneering" };

	public String setMessage(int level) {
		String[] messages = new String[4];
		String message = "";
		int[] stuff = { 0, 3, 14, 2, 16, 13, 1, 15, 10, 4, 17, 7, 5, 12, 11, 6,
				9, 8, 20, 18, 19, 21, 22, 23, 24 };
		messages[0] = skillNames[level] + ": " + currentStats[stuff[level]]
				+ "/" + currentMaxStats[stuff[level]] + "\\n";
		messages[1] = "Current XP: "
				+ String.format("%, d", currentExp[stuff[level]]) + "\\n";
		messages[2] = "Remainder: "
				+ String.format(
						"%, d",
						(getXPForLevel(currentMaxStats[stuff[level]] + 1) - currentExp[stuff[level]]))
						+ "\\n";
		messages[3] = "Next level: "
				+ String.format("%, d",
						getXPForLevel(currentMaxStats[stuff[level]] + 1));
		message = messages[0] + messages[1] + messages[2] + messages[3];
		if (currentMaxStats[stuff[level]] >= 99) {
			message = messages[0] + messages[1];
		}
		return message;
	}

	public static boolean webclient = false;

	public void init() {
		try {
			webclient = true;
			nodeID = 10;
			portOff = 0;
			setLowMem();
			isMembers = true;
			signlink.storeid = 32;
			signlink.startpriv(InetAddress.getLocalHost());
			initClientFrame(765, 563);
			instance = this;

		} catch (Exception exception) {
			return;
		}
	}

	public void startRunnable(Runnable runnable, int i) {
		if (i > 10)
			i = 10;
		if (signlink.mainapp != null) {
			signlink.startthread(runnable, i);
		} else {
			super.startRunnable(runnable, i);
		}
	}

	public Socket openSocket(int port, String server) throws IOException {
		return new Socket(InetAddress.getByName(server), port);
	}

	public Socket openSocket(int port) throws IOException {
		return new Socket(InetAddress.getByName(Configuration.HOST), port);
	}

	public String indexLocation(int cacheIndex, int index) {
		return signlink.findcachedir() + "index" + cacheIndex + "/"
				+ (index != -1 ? index + ".gz" : "");
	}

	public void repackCacheIndex(int cacheIndex) {
		System.out.println("Started repacking index " + cacheIndex + ".");
		int indexLength = new File(indexLocation(cacheIndex, -1)).listFiles().length;
		File[] file = new File(indexLocation(cacheIndex, -1)).listFiles();
		try {
			for (int index = 0; index < indexLength; index++) {
				int fileIndex = Integer
						.parseInt(getFileNameWithoutExtension(file[index]
								.toString()));
				byte[] data = fileToByteArray(cacheIndex, fileIndex);
				if (data != null && data.length > 0) {
					cacheIndices[cacheIndex].put(data.length, data, fileIndex);
					System.out.println("Repacked " + fileIndex + ".");
				} else {
					System.out.println("Unable to locate index " + fileIndex
							+ ".");
				}
			}
		} catch (Exception e) {
			System.out.println("Error packing cache index " + cacheIndex + ".");
		}
		System.out.println("Finished repacking " + cacheIndex + ".");
	}

	public byte[] fileToByteArray(int cacheIndex, int index) {
		try {
			if (indexLocation(cacheIndex, index).length() <= 0
					|| indexLocation(cacheIndex, index) == null) {
				return null;
			}
			File file = new File(indexLocation(cacheIndex, index));
			byte[] fileData = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			fis.read(fileData);
			fis.close();
			return fileData;
		} catch (Exception e) {
			return null;
		}
	}

	private boolean processMenuClick() {
		if (activeInterfaceType != 0)
			return false;
		int j = super.clickMode3;
		if (spellSelected == 1 && super.saveClickX >= 516
				&& super.saveClickY >= 160 && super.saveClickX <= 765
				&& super.saveClickY <= 205)
			j = 0;
		if (menuOpen) {
			if (j != 1) {
				int k = super.mouseX;
				int j1 = super.mouseY;
				if (menuScreenArea == 0) {
					k -= clientSize == 0 ? 4 : 0;
					j1 -= clientSize == 0 ? 4 : 0;
				}
				if (menuScreenArea == 1) {
					k -= 519;
					j1 -= 168;
				}
				if (menuScreenArea == 2) {
					k -= 17;
					j1 -= 338;
				}
				if (menuScreenArea == 3) {
					k -= 519;
					j1 -= 0;
				}
				if (k < menuOffsetX - 10 || k > menuOffsetX + menuWidth + 10
						|| j1 < menuOffsetY - 10
						|| j1 > menuOffsetY + menuHeight + 10) {
					menuOpen = false;
					if (menuScreenArea == 1)
						needDrawTabArea = true;
					if (menuScreenArea == 2)
						inputTaken = true;
				}
			}
			if (j == 1) {
				int l = menuOffsetX;
				int k1 = menuOffsetY;
				int i2 = menuWidth;
				int k2 = super.saveClickX;
				int l2 = super.saveClickY;
				if (menuScreenArea == 0) {
					k2 -= clientSize == 0 ? 4 : 0;
					l2 -= clientSize == 0 ? 4 : 0;
				}
				if (menuScreenArea == 1) {
					k2 -= 519;
					l2 -= 168;
				}
				if (menuScreenArea == 2) {
					k2 -= 17;
					l2 -= 338;
				}
				if (menuScreenArea == 3) {
					k2 -= 519;
					l2 -= 0;
				}
				int i3 = -1;
				for (int j3 = 0; j3 < menuActionRow; j3++) {
					int k3 = k1 + 31 + (menuActionRow - 1 - j3) * 15;
					if (k2 > l && k2 < l + i2 && l2 > k3 - 13 && l2 < k3 + 3)
						i3 = j3;
				}
				// System.out.println(i3);
				if (i3 != -1)
					doAction(i3);
				menuOpen = false;
				if (menuScreenArea == 1)
					needDrawTabArea = true;
				if (menuScreenArea == 2) {
					inputTaken = true;
				}
			}
			return true;
		} else {
			if (j == 1 && menuActionRow > 0) {
				int i1 = menuActionID[menuActionRow - 1];
				if (i1 == 632 || i1 == 78 || i1 == 867 || i1 == 431 || i1 == 53
						|| i1 == 74 || i1 == 454 || i1 == 539 || i1 == 493
						|| i1 == 847 || i1 == 447 || i1 == 1125) {
					int l1 = menuActionCmd2[menuActionRow - 1];
					int j2 = menuActionCmd3[menuActionRow - 1];
					RSInterface rsi = RSInterface.interfaceCache[j2];
					if (rsi.deleteOnDrag2 || rsi.dragDeletes) {
						aBoolean1242 = false;
						anInt989 = 0;
						anInt1084 = j2;
						anInt1085 = l1;
						activeInterfaceType = 2;
						anInt1087 = super.saveClickX;
						anInt1088 = super.saveClickY;
						if (RSInterface.interfaceCache[j2].parentID == openInterfaceID)
							activeInterfaceType = 1;
						if (RSInterface.interfaceCache[j2].parentID == backDialogID)
							activeInterfaceType = 3;
						return true;
					}
				}
			}
			if (j == 1
					&& (anInt1253 == 1 || menuHasAddFriend(menuActionRow - 1))
					&& menuActionRow > 2)
				j = 2;
			if (j == 1 && menuActionRow > 0)
				doAction(menuActionRow - 1);
			if (j == 2 && menuActionRow > 0)
				determineMenuSize();
			return false;
		}
	}

	public static int totalRead = 0;

	public static String getFileNameWithoutExtension(String fileName) {
		File tmpFile = new File(fileName);
		tmpFile.getName();
		int whereDot = tmpFile.getName().lastIndexOf('.');
		if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2) {
			return tmpFile.getName().substring(0, whereDot);
		}
		return "";
	}

	private void saveMidi(boolean flag, byte abyte0[]) {
		signlink.fadeMidi = flag ? 1 : 0;
		signlink.midisave(abyte0, abyte0.length);
	}

	private void loadRegion() {
		try {
			lastKnownPlane = -1;
			highestAmtToLoad = 0;
			stillGraphicDeque.clear();
			projectileDeque.clear();
			TextureLoader317.clearTextureCache();
			TextureLoader667.clearTextureCache();
			clearMemoryCaches();
			worldController.initToNull();
			System.gc();
			for (int i = 0; i < 4; i++)
				clippingPlanes[i].setDefault();

			for (int l = 0; l < 4; l++) {
				for (int k1 = 0; k1 < 104; k1++) {
					for (int j2 = 0; j2 < 104; j2++)
						byteGroundArray[l][k1][j2] = 0;

				}

			}

			objectManager = new ObjectManager(byteGroundArray, intGroundArray);
			int k2 = terrainData.length;
			if (loggedIn)
				stream.createFrame(0);
			if (!requestMapReconstruct && !requestMapReconstruct2) {
				for (int i3 = 0; i3 < k2; i3++) {
					int i4 = (mapCoordinates[i3] >> 8) * 64 - baseX;
					int k5 = (mapCoordinates[i3] & 0xff) * 64 - baseY;
					byte abyte0[] = terrainData[i3];
					if (abyte0 != null)
						objectManager.method180(abyte0, k5, i4,
								(currentRegionX - 6) * 8,
								(currentRegionY - 6) * 8, clippingPlanes);
				}

				for (int j4 = 0; j4 < k2; j4++) {
					int l5 = (mapCoordinates[j4] >> 8) * 64 - baseX;
					int k7 = (mapCoordinates[j4] & 0xff) * 64 - baseY;
					byte abyte2[] = terrainData[j4];
					if (abyte2 == null && currentRegionY < 800)
						objectManager.method174(k7, 64, 64, l5);
				}

				anInt1097++;
				if (anInt1097 > 160) {
					anInt1097 = 0;
					stream.createFrame(238);
					stream.writeWordBigEndian(96);
				}
				if (loggedIn)
					stream.createFrame(0);
				for (int i6 = 0; i6 < k2; i6++) {
					byte abyte1[] = objectData[i6];
					if (abyte1 != null) {
						// System.out.println("Object maps: "+anIntArray1236[i6]);
						int l8 = (mapCoordinates[i6] >> 8) * 64 - baseX;
						int k9 = (mapCoordinates[i6] & 0xff) * 64 - baseY;
						objectManager.method190(l8, clippingPlanes, k9,
								worldController, abyte1);
					}
				}

			}
			if (requestMapReconstruct2) {
				for (int plane = 0; plane < 4; plane++) {
					for (int x = 0; x < 13; x++) {
						for (int y = 0; y < 13; y++) {
							int chunkBits = constructRegionData[plane][x][y];
							if (chunkBits != -1) {
								int z = chunkBits >> 24 & 3;
						int rotation = chunkBits >> 1 & 3;
				int xCoord = chunkBits >> 14 & 0x3ff;
		int yCoord = chunkBits >> 3 & 0x7ff;
					int mapRegion = (xCoord / 8 << 8) + yCoord / 8;
					for (int idx = 0; idx < mapCoordinates.length; idx++) {
						if (mapCoordinates[idx] != mapRegion
								|| terrainData[idx] == null)
							continue;
						objectManager.loadMapChunk(z, rotation,
								clippingPlanes, x * 8,
								(xCoord & 7) * 8, terrainData[idx],
								(yCoord & 7) * 8, plane, y * 8);
						break;
					}

							}
						}

					}

				}

				for (int xChunk = 0; xChunk < 13; xChunk++) {
					for (int yChunk = 0; yChunk < 13; yChunk++) {
						int tileBits = constructRegionData[0][xChunk][yChunk];
						if (tileBits == -1)
							objectManager.method174(yChunk * 8, 8, 8,
									xChunk * 8);
					}

				}
				if (loggedIn)
					stream.createFrame(0);
				for (int chunkZ = 0; chunkZ < 4; chunkZ++) {
					for (int chunkX = 0; chunkX < 13; chunkX++) {
						for (int chunkY = 0; chunkY < 13; chunkY++) {
							int tileBits = constructRegionData[chunkZ][chunkX][chunkY];
							if (tileBits != -1) {
								int plane = tileBits >> 24 & 3;
						int rotation = tileBits >> 1 & 3;
				int coordX = tileBits >> 14 & 0x3ff;
				int coordY = tileBits >> 3 & 0x7ff;
					int mapRegion = (coordX / 8 << 8) + coordY / 8;
					for (int idx = 0; idx < mapCoordinates.length; idx++) {
						if (mapCoordinates[idx] != mapRegion
								|| objectData[idx] == null)
							continue;
						objectManager.readObjectMap(clippingPlanes,
								worldController, plane, chunkX * 8,
								(coordY & 7) * 8, chunkZ,
								objectData[idx], (coordX & 7) * 8,
								rotation, chunkY * 8);
						break;
					}
							}
						}
					}
				}
				requestMapReconstruct2 = false;
				requestMapReconstruct = false;
			}
			if (loggedIn)
				stream.createFrame(0);
			objectManager.method171(clippingPlanes, worldController);
			if (loggedIn)
				gameScreenIP.initDrawingArea();
			if (loggedIn)
				stream.createFrame(0);
			int k3 = ObjectManager.highestPlane;
			if (k3 > plane)
				k3 = plane;
			if (k3 < plane - 1)
				k3 = plane - 1;
			if (lowMem)
				worldController.initTiles(ObjectManager.highestPlane);
			else
				worldController.initTiles(0);
			for (int i5 = 0; i5 < 104; i5++) {
				for (int i7 = 0; i7 < 104; i7++)
					spawnGroundItem(i5, i7);

			}

			anInt1051++;
			if (anInt1051 > 98) {
				anInt1051 = 0;
				stream.createFrame(150);
			}
			clearObjectSpawnRequests();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		ObjectDef.modelCache.clear();
		ObjectDef.completedModelCache.clear();
		if (super.mainFrame != null && loggedIn) {
			stream.createFrame(210);
			stream.writeDWord(0x3f008edd);
		}
		System.gc();
		TextureLoader317.resetTextures();
		onDemandFetcher.clearExtraFilesList();
		int startRegionX = (currentRegionX - 6) / 8 - 1;
		int endRegionX = (currentRegionX + 6) / 8 + 1;
		int startRegionY = (currentRegionY - 6) / 8 - 1;
		int endRegionY = (currentRegionY + 6) / 8 + 1;
		for (int regionX = startRegionX; regionX <= endRegionX; regionX++) {
			for (int regionY = startRegionY; regionY <= endRegionY; regionY++)
				if (regionX == startRegionX || regionX == endRegionX
				|| regionY == startRegionY || regionY == endRegionY) {
					int floorMapId = onDemandFetcher.getMapIdForRegions(0,
							regionY, regionX);
					if (floorMapId != -1) {
						onDemandFetcher.insertExtraFilesRequest(floorMapId, 3);
					}
					int objectMapId = onDemandFetcher.getMapIdForRegions(1,
							regionY, regionX);
					if (objectMapId != -1) {
						onDemandFetcher.insertExtraFilesRequest(objectMapId, 3);
					}
				}

		}

	}

	public void clearMemoryCaches() {
		ObjectDef.modelCache.clear();
		ObjectDef.completedModelCache.clear();
		NPCDef.modelCache.clear();
		ItemDef.modelCache.clear();
		ItemDef.spriteCache.clear();
		Player.modelCache.clear();
		SpotAnim.modelCache.clear();
	}

	public void renderedMapScene(int i) {
		int ai[] = miniMap.myPixels;
		int j = ai.length;
		for (int k = 0; k < j; k++)
			ai[k] = 0;

		for (int l = 1; l < 103; l++) {
			int i1 = 24628 + (103 - l) * 512 * 4;
			for (int k1 = 1; k1 < 103; k1++) {
				if ((byteGroundArray[i][k1][l] & 0x18) == 0)
					worldController.drawTileMinimap(ai, i1, i, k1, l);
				if (i < 3 && (byteGroundArray[i + 1][k1][l] & 8) != 0)
					worldController.drawTileMinimap(ai, i1, i + 1, k1, l);
				i1 += 4;
			}

		}

		int j1 = ((238 + (int) (Math.random() * 20D)) - 10 << 16)
				+ ((238 + (int) (Math.random() * 20D)) - 10 << 8)
				+ ((238 + (int) (Math.random() * 20D)) - 10);
		int l1 = (238 + (int) (Math.random() * 20D)) - 10 << 16;
		if (loggedIn)
			miniMap.method343();
		for (int i2 = 1; i2 < 103; i2++) {
			for (int j2 = 1; j2 < 103; j2++) {
				if ((byteGroundArray[i][j2][i2] & 0x18) == 0)
					drawMapScenes(i2, j1, j2, l1, i);
				if (i < 3 && (byteGroundArray[i + 1][j2][i2] & 8) != 0)
					drawMapScenes(i2, j1, j2, l1, i + 1);
			}

		}
		if (loggedIn)
			gameScreenIP.initDrawingArea();
		mapFunctionsLoadedAmt = 0;
		for (int xTile = 0; xTile < 104; xTile++) {
			for (int yTIle = 0; yTIle < 104; yTIle++) {
				int uid = worldController.fetchGroundDecorationNewUID(plane,
						xTile, yTIle);
				if (uid != 0) {
					// uid = uid >> 14 & 0x7fff;
					int functionId = ObjectDef.forID(uid).mapFunctionID;
					if (functionId >= 0) {
						int k3 = xTile;
						int l3 = yTIle;
						if (functionId != 22 && functionId != 29
								&& functionId != 34 && functionId != 36
								&& functionId != 46 && functionId != 47
								&& functionId != 48) {
							byte byte0 = 104;
							byte byte1 = 104;
							int ai1[][] = clippingPlanes[plane].clipData;
							for (int i4 = 0; i4 < 10; i4++) {
								int j4 = (int) (Math.random() * 4D);
								if (j4 == 0 && k3 > 0 && k3 > xTile - 3
										&& (ai1[k3 - 1][l3] & 0x1280108) == 0)
									k3--;
								if (j4 == 1 && k3 < byte0 - 1 && k3 < xTile + 3
										&& (ai1[k3 + 1][l3] & 0x1280180) == 0)
									k3++;
								if (j4 == 2 && l3 > 0 && l3 > yTIle - 3
										&& (ai1[k3][l3 - 1] & 0x1280102) == 0)
									l3--;
								if (j4 == 3 && l3 < byte1 - 1 && l3 < yTIle + 3
										&& (ai1[k3][l3 + 1] & 0x1280120) == 0)
									l3++;
							}

						}
						currentMapFunctionSprites[mapFunctionsLoadedAmt] = mapFunctions[functionId];
						mapFunctionTileX[mapFunctionsLoadedAmt] = k3;
						mapFunctionTileY[mapFunctionsLoadedAmt] = l3;
						mapFunctionsLoadedAmt++;
					}
				}
			}

		}

	}

	private void spawnGroundItem(int i, int j) {
		Deque itemDeque = groundArray[plane][i][j];
		if (itemDeque == null) {
			worldController.removeGroundItemFromTIle(plane, i, j);
			return;
		}
		int k = 0xfa0a1f01;
		Object toSpawn = null;
		for (Item item = (Item) itemDeque.getFront(); item != null; item = (Item) itemDeque
				.getNext()) {
			ItemDef itemDef = ItemDef.forID(item.ID);
			int l = itemDef.value;
			if (itemDef.stackable)
				l *= item.amount + 1;
			if (l > k) {
				k = l;
				toSpawn = item;
			}
		}

		itemDeque.insertFront(((Node) (toSpawn)));
		Object firstItem = null;
		Object secondItem = null;
		for (Item item_1 = (Item) itemDeque.getFront(); item_1 != null; item_1 = (Item) itemDeque
				.getNext()) {
			if (item_1.ID != ((Item) (toSpawn)).ID && firstItem == null)
				firstItem = item_1;
			if (item_1.ID != ((Item) (toSpawn)).ID
					&& item_1.ID != ((Item) (firstItem)).ID
					&& secondItem == null)
				secondItem = item_1;
		}

		int i1 = i + (j << 7) + 0x60000000;
		worldController.addGroundItemTile(i, i1, ((Animable) (firstItem)),
				getFloorDrawHeight(plane, j * 128 + 64, i * 128 + 64),
				((Animable) (secondItem)), ((Animable) (toSpawn)), plane, j);
	}

	private void processNpcs(boolean flag) {
		for (int j = 0; j < npcCount; j++) {
			NPC npc = npcArray[npcIndices[j]];
			int k = 0x20000000 + (npcIndices[j] << 14);
			if (npc == null || !npc.isVisible()
					|| npc.desc.hasRenderPriority != flag)
				continue;
			int l = npc.x >> 7;
			int i1 = npc.y >> 7;
		if (l < 0 || l >= 104 || i1 < 0 || i1 >= 104)
			continue;
		if (npc.boundDim == 1 && (npc.x & 0x7f) == 64
				&& (npc.y & 0x7f) == 64) {
			if (anIntArrayArray929[l][i1] == anInt1265)
				continue;
			anIntArrayArray929[l][i1] = anInt1265;
		}
		if (!npc.desc.clickable)
			k += 0x80000000;
		worldController.addMutipleTileEntity(plane, npc.anInt1552,
				getFloorDrawHeight(plane, npc.y, npc.x), k, npc.y,
				(npc.boundDim - 1) * 64 + 60, npc.x, npc, npc.aBoolean1541);
		}
	}

	private void loadError() {
		String s = "ondemand";// was a constant parameter
		System.out.println(s);
		try {
			getAppletContext().showDocument(
					new URL(getCodeBase(), "loaderror_" + s + ".html"));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		do
			try {
				Thread.sleep(1000L);
			} catch (Exception _ex) {
			}
		while (true);
	}

	public void drawHoverBox2(int xPos, int yPos, String text) {
		String[] results = text.split("\n");
		int height = (results.length * 16) + 6;
		int width;
		width = chatTextDrawingArea.getTextWidth(results[0]) + 6;
		for (int i = 1; i < results.length; i++)
			if (width <= chatTextDrawingArea.getTextWidth(results[i]) + 6)
				width = chatTextDrawingArea.getTextWidth(results[i]) + 6;
		DrawingArea.drawPixels(height + 2, yPos - 1, xPos - 1, 0xFFFFFF,
				width + 2);
		DrawingArea.drawPixels(height, yPos, xPos, 0x1E1F1F, width);
		yPos += 14;
		for (int i = 0; i < results.length; i++) {
			drawingArea.drawRegularText(false, xPos + 15, 0xFFFFFF, results[i],
					yPos + 1);
			yPos += 16;
		}
	}

	public void drawHoverBox(int xPos, int yPos, String text) {
		String[] results = text.split("\n");
		int height = (results.length * 16) + 6;
		int width;
		width = chatTextDrawingArea.getTextWidth(results[0]) - 3;
		for (int i = 1; i < results.length; i++) {
			if (width <= chatTextDrawingArea.getTextWidth(results[i]) + 6)
				width = chatTextDrawingArea.getTextWidth(results[i]) + 2;
		}
		DrawingArea.drawPixels(height, yPos, xPos, 0xFFFFA0, width);
		DrawingArea.fillPixels(xPos, width, height, 0, yPos);
		yPos += 14;
		for (int i = 0; i < results.length; i++) {
			drawingArea.drawRegularText(false, xPos + 3, 0, results[i],
					yPos + 1);
			yPos += 16;
		}
	}

	public void drawHoverBox(int xPos, int yPos, int color, int color2,
			String text) {
		String[] results = text.split("\n");
		int height = (results.length * 16) + 6;
		int width;
		width = chatTextDrawingArea.getTextWidth(results[0]) + 6;
		for (int i = 1; i < results.length; i++)
			if (width <= chatTextDrawingArea.getTextWidth(results[i]) + 6)
				width = chatTextDrawingArea.getTextWidth(results[i]) + 6;
		if (xPos + width > clientWidth)
			xPos -= width + 10;
		if (yPos + height > clientHeight)
			yPos -= height + 10;
		DrawingArea.drawPixels(height, yPos, xPos, color, width);
		DrawingArea.fillPixels(xPos, width, height, color2, yPos);
		yPos += 14;
		for (int i = 0; i < results.length; i++) {
			drawingArea.drawRegularText(false, xPos + 5, color2, results[i],
					yPos + 1);
			yPos += 16;
		}
	}

	private int hoverSpriteId = -1;

	private void buildInterfaceMenu(int interfaceX, RSInterface class9,
			int mouseX, int interfaceY, int mouseY, int scrollOffset) {
		if(class9 == null)
			return;
		if (class9.type != 0 || class9.children == null
				|| class9.interfaceShown)
			return;
		if (mouseX < interfaceX || mouseY < interfaceY
				|| mouseX > interfaceX + class9.width
				|| mouseY > interfaceY + class9.height)
			return;
		int totalChildrens = class9.children.length;
		for (int frameID = 0; frameID < totalChildrens; frameID++) {
			int childX = class9.childX[frameID] + interfaceX;
			int childY = (class9.childY[frameID] + interfaceY) - scrollOffset;
			RSInterface child = RSInterface.interfaceCache[class9.children[frameID]];
			childX += child.xOffset;
			childY += child.yOffset;
			if(super.clickMode3 != 0) {
				mouseX = super.clickX;
				mouseY = super.clickY;
			}
			if ((child.hoverType >= 0 || child.disabledMouseOverColor != 0)
					&& mouseX >= childX && mouseY >= childY
					&& mouseX < childX + child.width
					&& mouseY < childY + child.height) {
				if (child.hoverType >= 0) {
					hoveredInterface = child.hoverType;
					hoverSpriteId = hoveredInterface;
				} else {
					hoveredInterface = child.id;
					hoverSpriteId = hoveredInterface;
				}
			}
			if (child.type == 8 || child.type == 9 || child.type == 10
					&& mouseX >= childX && mouseY >= childY
					&& mouseX < childX + child.width
					&& mouseY < childY + child.height) {
				anInt1315 = child.id;
			}
			if (child.type == 0) {
				buildInterfaceMenu(childX, child, mouseX, childY, mouseY,
						child.scrollPosition);
				if (child.scrollMax > child.height)
					scrollInterface(childX + child.width, child.height, mouseX,
							mouseY, child, childY, true, child.scrollMax);
			} else {
				if (child.atActionType == 1 && mouseX >= childX
						&& mouseY >= childY && mouseX < childX + child.width
						&& mouseY < childY + child.height) {
					boolean flag = false;
					boolean flag1 = false;
					if (child.contentType != 0)
						flag = buildFriendsListMenu(child);
					if (child.tooltip.startsWith("[CC]")
							|| child.tooltip.startsWith("[GE]")) {
						flag1 = true;
						if (!child.tooltip.startsWith("[GE]"))
							clanName = RSInterface.interfaceCache[child.id - 800].message;
					}
					if (!flag && !flag1) {
						String tooltip = child.tooltip;
						if (myRights == 4) {
							tooltip += "   DEV: Id: " + child.id + " img1: "
									+ child.disabledSpriteId + " img2:"
									+ child.enabledSpriteId;
						}
						menuActionName[menuActionRow] = tooltip;
						menuActionID[menuActionRow] = 315;
						menuActionCmd3[menuActionRow] = child.id;
						menuActionRow++;
					}
					if (flag1 && !child.tooltip.startsWith("[GE]")) {
						if (RSInterface.interfaceCache[child.id - 800].message != "") {
							menuActionName[menuActionRow] = "General";
							menuActionID[menuActionRow] = 1321;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
							menuActionName[menuActionRow] = "Captain";
							menuActionID[menuActionRow] = 1320;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
							menuActionName[menuActionRow] = "Lieutenant";
							menuActionID[menuActionRow] = 1319;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
							menuActionName[menuActionRow] = "Sergeant";
							menuActionID[menuActionRow] = 1318;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
							menuActionName[menuActionRow] = "Corporal";
							menuActionID[menuActionRow] = 1317;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
							menuActionName[menuActionRow] = "Recruit";
							menuActionID[menuActionRow] = 1316;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
							menuActionName[menuActionRow] = "Not ranked";
							menuActionID[menuActionRow] = 1315;
							menuActionCmd3[menuActionRow] = child.id;
							menuActionRow++;
						}
					}
				}
				if (child.atActionType == 2 && spellSelected == 0
						&& mouseX >= childX && mouseY >= childY
						&& mouseX < childX + child.width
						&& mouseY < childY + child.height) {
					String s = child.selectedActionName;
					if (s.indexOf(" ") != -1)
						s = s.substring(0, s.indexOf(" "));
					if (child.spellName.endsWith("Rush")
							|| child.spellName.endsWith("Burst")
							|| child.spellName.endsWith("Blitz")
							|| child.spellName.endsWith("Barrage")
							|| child.spellName.endsWith("strike")
							|| child.spellName.endsWith("bolt")
							|| child.spellName.equals("Crumble undead")
							|| child.spellName.endsWith("blast")
							|| child.spellName.endsWith("wave")
							|| child.spellName.equals("Claws of Guthix")
							|| child.spellName.equals("Flames of Zamorak")
							|| child.spellName.equals("Magic Dart")) {
						menuActionName[menuActionRow] = "Autocast @gre@"
								+ child.spellName;
						menuActionID[menuActionRow] = 104;
						menuActionCmd3[menuActionRow] = child.id;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = s + " @gre@"
							+ child.spellName;
					menuActionID[menuActionRow] = 626;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.atActionType == 3 && mouseX >= childX
						&& mouseY >= childY && mouseX < childX + child.width
						&& mouseY < childY + child.height) {
					menuActionName[menuActionRow] = "Close";
					menuActionID[menuActionRow] = 200;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.atActionType == 4 && mouseX >= childX
						&& mouseY >= childY && mouseX < childX + child.width
						&& mouseY < childY + child.height) {
					// System.out.println("2"+class9_1.tooltip + ", " +
					// class9_1.interfaceID);
					menuActionName[menuActionRow] = child.tooltip + ", "
							+ child.id;
					menuActionID[menuActionRow] = 169;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
					if (child.hoverText != null) {
						// drawHoverBox(k, l, class9_1.hoverText);
						// System.out.println("DRAWING INTERFACE: " +
						// class9_1.hoverText);
					}
				}
				if (child.atActionType == 5 && mouseX >= childX
						&& mouseY >= childY && mouseX < childX + child.width
						&& mouseY < childY + child.height) {
					menuActionName[menuActionRow] = child.tooltip
							+ ((myRights != 0) ? ", @gre@(@whi@" + child.id
									+ "@gre@)" : "");
					menuActionID[menuActionRow] = 646;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.atActionType == 6 && !aBoolean1149
						&& mouseX >= childX && mouseY >= childY
						&& mouseX < childX + child.width
						&& mouseY < childY + child.height) {
					menuActionName[menuActionRow] = child.tooltip + ", "
							+ child.id;
					menuActionID[menuActionRow] = 679;
					menuActionCmd3[menuActionRow] = child.id;
					menuActionRow++;
				}
				if (child.type == 2) {
					int ptr = 0;
					for (int height = 0; height < child.height; height++) {
						for (int width = 0; width < child.width; width++) {
							int itemX = childX + width
									* (32 + child.invSpritePadX);
							int itemY = childY + height
									* (32 + child.invSpritePadY);
							if (ptr < 20) {
								itemX += child.spritesX[ptr];
								itemY += child.spritesY[ptr];
							}
							if (mouseX >= itemX && mouseY >= itemY
									&& mouseX < itemX + 32
									&& mouseY < itemY + 32) {
								mouseInvInterfaceIndex = ptr;
								lastActiveInvInterface = child.id;
								if (child.inv[ptr] > 0) {
									ItemDef itemDef = ItemDef
											.forID(child.inv[ptr] - 1);
									if (itemSelected == 1
											&& child.isInventoryInterface) {
										if (child.id != lastItemSelectedInterface
												|| ptr != lastItemSelectedSlot) {
											menuActionName[menuActionRow] = "Use "
													+ selectedItemName
													+ " with @lre@"
													+ itemDef.name;
											menuActionID[menuActionRow] = 870;
											menuActionCmd1[menuActionRow] = itemDef.ID;
											menuActionCmd2[menuActionRow] = ptr;
											menuActionCmd3[menuActionRow] = child.id;
											menuActionRow++;
										}
									} else if (spellSelected == 1
											&& child.isInventoryInterface) {
										if ((spellUsableOn & 0x10) == 16) {
											menuActionName[menuActionRow] = spellTooltip
													+ " @lre@" + itemDef.name;
											menuActionID[menuActionRow] = 543;
											menuActionCmd1[menuActionRow] = itemDef.ID;
											menuActionCmd2[menuActionRow] = ptr;
											menuActionCmd3[menuActionRow] = child.id;
											menuActionRow++;
										}
									} else {
										if (child.isInventoryInterface) {
											for (int l3 = 4; l3 >= 3; l3--)
												if (itemDef.actions != null
												&& itemDef.actions[l3] != null) {
													if (openInterfaceID != 24700) {
														menuActionName[menuActionRow] = itemDef.actions[l3]
																+ " @lre@"
																+ itemDef.name;
														if (l3 == 3)
															menuActionID[menuActionRow] = 493;
														if (l3 == 4)
															menuActionID[menuActionRow] = 847;
														menuActionCmd1[menuActionRow] = itemDef.ID;
														menuActionCmd2[menuActionRow] = ptr;
														menuActionCmd3[menuActionRow] = child.id;
														menuActionRow++;
													}
												} else if (l3 == 4) {
													if (openInterfaceID != 24700) {
														menuActionName[menuActionRow] = "Drop @lre@"
																+ itemDef.name;
														menuActionID[menuActionRow] = 847;
														menuActionCmd1[menuActionRow] = itemDef.ID;
														menuActionCmd2[menuActionRow] = ptr;
														menuActionCmd3[menuActionRow] = child.id;
														menuActionRow++;
													}
												}

										}
										if (child.usableItemInterface) {
											if (openInterfaceID == 24700) {
												menuActionName[menuActionRow] = "Offer @lre@"
														+ itemDef.name;
												menuActionID[menuActionRow] = 454;
												menuActionCmd1[menuActionRow] = itemDef.ID;
											} else {
												menuActionName[menuActionRow] = "Use @lre@"
														+ itemDef.name;
												menuActionID[menuActionRow] = 447;
												menuActionCmd1[menuActionRow] = itemDef.ID;
											}
											// k2 = inventory spot
											// System.out.println(k2);
											menuActionCmd2[menuActionRow] = ptr;
											menuActionCmd3[menuActionRow] = child.id;
											menuActionRow++;
										}
										if (child.isInventoryInterface
												&& itemDef.actions != null) {
											for (int i4 = 2; i4 >= 0; i4--)
												if (itemDef.actions[i4] != null) {
													if (openInterfaceID != 24700) {
														menuActionName[menuActionRow] = itemDef.actions[i4]
																+ " @lre@"
																+ itemDef.name;
														if (i4 == 0)
															menuActionID[menuActionRow] = 74;
														if (i4 == 1)
															menuActionID[menuActionRow] = 454;
														if (i4 == 2)
															menuActionID[menuActionRow] = 539;
														menuActionCmd1[menuActionRow] = itemDef.ID;
														menuActionCmd2[menuActionRow] = ptr;
														menuActionCmd3[menuActionRow] = child.id;
														menuActionRow++;
													}
												}

										}
										if(itemDef.actions != null) {
											for(int j4 = 5; j4 >= 0; j4--) {
												if(j4 != 5) {
													/*	if(openInterfaceID == 42000) {
														switch(j4) {
														case 0:
															itemDef.actions[j4] = "Pricecheck 1";
															break;
														case 1:
															itemDef.actions[j4] = "Pricecheck 5";
															break;
														case 2:
															itemDef.actions[j4] = "Pricecheck 10";
															break;
														case 3:
															itemDef.actions[j4] = "Pricecheck All";
															break;
														case 4:
															itemDef.actions[j4] = "Pricecheck X";
															break;
														}
													}*/
												} else if (j4 == 5 && openInterfaceID == 3323 && child.id != 3323 && child.id != 3324) {
													menuActionName[menuActionRow] = "Lend"
															+ " @lre@"
															+ itemDef.name;
													itemToLend = itemDef.ID;
													interfaceButtonAction = 0; 
													menuActionID[menuActionRow] = 76390;
													menuActionCmd1[menuActionRow] = itemDef.ID;
													menuActionCmd2[menuActionRow] = ptr;
													menuActionCmd3[menuActionRow] = child.id;
													menuActionRow++;
												}
											}
										}
										boolean m = false;
										m = false;
										if (child.actions != null) {
											for (int j4 = child.parentID == 5292 && child.actions.length == 6 ? 5 : 4; j4 >= 0; j4--)
												if (child.actions[j4] != null) {
													if (child.actions[j4] != "[GE]") {
														if (child.actions[j4] != "[ITEM]Collect"
																&& child.actions[j4] != "[COINS]Collect") {
															String s = myRights == 4 ? child.actions[j4]
																	+ " @lre@"
																	+ itemDef.name + " " + itemDef.ID : child.actions[j4]
																			+ " @lre@"
																			+ itemDef.name;
															if(child.parentID == 5292 && openInterfaceID == 5292)
																m = true; //Don't show examine option 
															else if(child.parentID == 3321 && openInterfaceID == 42000)
																s = s.replaceAll("Offer", "Pricecheck");
															menuActionName[menuActionRow] = s;
															if (j4 == 0)
																menuActionID[menuActionRow] = 632;
															if (j4 == 1)
																menuActionID[menuActionRow] = 78;
															if (j4 == 2)
																menuActionID[menuActionRow] = 867;
															if (j4 == 3)
																menuActionID[menuActionRow] = 431;
															if (j4 == 4)
																menuActionID[menuActionRow] = 53;
															if (j4 == 5)
																menuActionID[menuActionRow] = 54;
															menuActionCmd1[menuActionRow] = itemDef.ID;
															menuActionCmd2[menuActionRow] = ptr;
															menuActionCmd3[menuActionRow] = child.id;
															menuActionRow++;
														} else {
															menuActionName[menuActionRow] = "Collect @lre@"
																	+ itemDef.name;

															if (openInterfaceID == 54700) {
																if (child.actions[j4]
																		.startsWith("[ITEM]")) {
																	menuActionID[menuActionRow] = 889;
																} else if (child.actions[j4]
																		.startsWith("[COINS]")) {
																	menuActionID[menuActionRow] = 888;
																}
															}
															if (openInterfaceID == 53700) {
																if (child.actions[j4]
																		.startsWith("[ITEM]")) {
																	menuActionID[menuActionRow] = 890;
																} else if (child.actions[j4]
																		.startsWith("[COINS]")) {
																	menuActionID[menuActionRow] = 891;
																}
															}
															menuActionName[menuActionRow] = "Collect @lre@"
																	+ itemDef.name;
															menuActionCmd1[menuActionRow] = itemDef.ID;
															menuActionCmd2[menuActionRow] = ptr;
															menuActionCmd3[menuActionRow] = child.id;
															menuActionRow++;
														}
													} else {
														m = true;
													}
												}

										}
										if (m != true) {
											if (child.id == 38274)
												menuActionName[menuActionRow] = "Build @lre@"
														+ itemDef.name;
											else 
												menuActionName[menuActionRow] = "Examine @lre@" + itemDef.name;
											menuActionID[menuActionRow] = 1125;
											menuActionCmd1[menuActionRow] = itemDef.ID;
											menuActionCmd2[menuActionRow] = ptr;
											menuActionCmd3[menuActionRow] = child.id;
											menuActionRow++;
										}
									}
								}
							}
							ptr++;
						}

					}

				}
			}
		}

	}

	private void drawScrollbar(int barHeight, int scrollPos, int yPos,
			int xPos, int contentHeight, boolean newScroller,
			boolean isTransparent) {
		int backingAmount = (barHeight - 32) / 5;
		int scrollPartHeight = ((barHeight - 32) * barHeight) / contentHeight;
		int scrollerID;
		if (newScroller) {
			scrollerID = 4;
		} else if (isTransparent) {
			scrollerID = 8;
		} else {
			scrollerID = 0;
		}
		if (scrollPartHeight < 10)
			scrollPartHeight = 10;
		int scrollPartAmount = (scrollPartHeight / 5) - 2;
		int scrollPartPos = ((barHeight - 32 - scrollPartHeight) * scrollPos)
				/ (contentHeight - barHeight) + 16 + yPos;
		/* Bar fill */
		for (int i = 0, yyPos = yPos + 16; i <= backingAmount; i++, yyPos += 5) {
			scrollPart[scrollerID + 1].drawSprite(xPos, yyPos);
		}
		/* Top of bar */
		scrollPart[scrollerID + 2].drawSprite(xPos, scrollPartPos);
		scrollPartPos += 5;
		/* Middle of bar */
		for (int i = 0; i <= scrollPartAmount; i++) {
			scrollPart[scrollerID + 3].drawSprite(xPos, scrollPartPos);
			scrollPartPos += 5;
		}
		scrollPartPos = ((barHeight - 32 - scrollPartHeight) * scrollPos)
				/ (contentHeight - barHeight) + 16 + yPos
				+ (scrollPartHeight - 5);
		/* Bottom of bar */
		scrollPart[scrollerID].drawSprite(xPos, scrollPartPos);
		/* Arrows */
		if (newScroller) {
			scrollBar[2].drawSprite(xPos, yPos);
			scrollBar[3].drawSprite(xPos, (yPos + barHeight) - 16);
		} else if (isTransparent) {
			scrollBar[4].drawSprite(xPos, yPos);
			scrollBar[5].drawSprite(xPos, (yPos + barHeight) - 16);
		} else {
			scrollBar[0].drawSprite(xPos, yPos);
			scrollBar[1].drawSprite(xPos, (yPos + barHeight) - 16);
		}
	}

	private void updateNPCs(Stream stream, int i) {
		anInt839 = 0;
		playersToUpdateCount = 0;
		updateNPCAmount(stream);
		updateNPCMovement(i, stream);
		readNPCUpdateMask(stream);
		for (int ptr = 0; ptr < anInt839; ptr++) {
			int l = anIntArray840[ptr];
			if (npcArray[l] == null)
				continue;
			if (npcArray[l].loopCycle != loopCycle) {
				npcArray[l].desc = null;
				npcArray[l] = null;
			}
		}

		if (stream.currentOffset != i) {
			signlink.reporterror(myUsername
					+ " size mismatch in getnpcpos - pos:"
					+ stream.currentOffset + " psize:" + i);
			throw new RuntimeException("eek");
		}
		for (int i1 = 0; i1 < npcCount; i1++)
			if (npcArray[npcIndices[i1]] == null) {
				signlink.reporterror(myUsername
						+ " null entry in npc list - pos:" + i1 + " size:"
						+ npcCount);
				throw new RuntimeException("eek");
			}

	}

	private int cButtonHPos;
	private int cButtonCPos;
	private boolean menuToggle = true;

	private void handleActions(int configID) {
		int action = 0;
		if (configID < Varp.cache.length)
			action = Varp.cache[configID].usage;
		if (action == 0)
			return;
		int config = variousSettings[configID];
		if (action == 1) {
			if (!forcedShadow) {
				if (config == 1) {
					Rasterizer.calculatePalette(0.90000000000000002D, true);
					currentShadow = 0.90000000000000002D;
				}
				if (config == 2) {
					Rasterizer.calculatePalette(0.80000000000000004D, true);
					currentShadow = 0.80000000000000004D;
				}
				if (config == 3) {
					Rasterizer.calculatePalette(0.69999999999999996D, true);
					currentShadow = 0.69999999999999996D;
				}
				if (config == 4) {
					Rasterizer.calculatePalette(0.59999999999999998D, true);
					currentShadow = 0.59999999999999998D;
				}
				ItemDef.spriteCache.clear();
				welcomeScreenRaised = true;
			}
		}
		if (action == 3) {
			boolean music = musicEnabled;
			if (config == 0) {
				adjustVolume(musicEnabled, 500);
				musicEnabled = true;
			}
			if (config == 1) {
				adjustVolume(musicEnabled, 300);
				musicEnabled = true;
			}
			if (config == 2) {
				adjustVolume(musicEnabled, 100);
				musicEnabled = true;
			}
			if (config == 3) {
				adjustVolume(musicEnabled, 0);
				musicEnabled = true;
			}
			if (config == 4) {
				musicEnabled = false;
			}
			if (musicEnabled != music) {
				if (musicEnabled) {
					nextSong = currentSong;
					songChanging = true;
					onDemandFetcher.requestFileData(2, nextSong);
				} else {
					stopMidi();
				}
				prevSong = 0;
			}
		}
		if (action == 4) {
			SoundPlayer.setVolume(config);
			stream.createFrame(6);
			stream.writeDWord(config);
			if (config == 0) {
				soundEnabled = true;
				setWaveVolume(0);
			}
			if (config == 1) {
				soundEnabled = true;
				setWaveVolume(-400);
			}
			if (config == 2) {
				soundEnabled = true;
				setWaveVolume(-800);
			}
			if (config == 3) {
				soundEnabled = true;
				setWaveVolume(-1200);
			}
			if (config == 4) {
				soundEnabled = false;
			}
		}
		if (action == 5)
			anInt1253 = config;
		if (action == 6)
			anInt1249 = config;
		if (action == 7)
			running = !running;
		if (action == 8) {
			splitPrivateChat = config;
			inputTaken = true;
		}
		if (action == 9)
			anInt913 = config;
	}

	private void updateEntities() {
		try {
			int anInt974 = 0;
			for (int j = -1; j < playerCount + npcCount; j++) {
				Object obj;
				if (j == -1)
					obj = myPlayer;
				else if (j < playerCount)
					obj = playerArray[playerIndices[j]];
				else
					obj = npcArray[npcIndices[j - playerCount]];
				if (obj == null || !((Entity) (obj)).isVisible())
					continue;
				if (obj instanceof NPC) {
					NPCDef entityDef = ((NPC) obj).desc;
					if (entityDef.childrenIDs != null)
						entityDef = entityDef.getAlteredNPCDef();
					if (entityDef == null)
						continue;
				}
				if (j < playerCount) {
					int l = 30;
					Player player = (Player)obj;
					if(player.headIcon >= 0) {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if(spriteDrawX > -1) {
							l += 10;
							if (player.skulled) {
								l-= 8;
								skullIcons[0].drawSprite(spriteDrawX - 12, spriteDrawY - l);
								l += 19;
							}
							if (player.bountyHunterIcon > 1 && player.bountyHunterIcon < 7) {
								if(!player.skulled)
									l-=13;
								bountyHunter[player.bountyHunterIcon - 1].drawSprite(spriteDrawX - (player.skulled ? 8 : 10), spriteDrawY - l);
								l += 28;
							}
							if (player.headIcon < 20) {
								headIcons[player.headIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
								l += 18;
							}
						}
					}
					if(j >= 0 && anInt855 == 10 && anInt933 == playerIndices[j]) {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if(spriteDrawX > -1)
							headIconsHint[player.hintIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
					}
				} else {
					NPCDef entityDef_1 = ((NPC) obj).desc;
					if (entityDef_1.headIcon >= 0
							&& entityDef_1.headIcon < headIcons.length) {
						npcScreenPos(((Entity) (obj)),
								((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIcons[entityDef_1.headIcon].drawSprite(
									spriteDrawX - 12, spriteDrawY - 30);
					}
					if (anInt855 == 1
							&& anInt1222 == npcIndices[j - playerCount]
									&& loopCycle % 20 < 10) {
						npcScreenPos(((Entity) (obj)),
								((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIconsHint[0].drawSprite(spriteDrawX - 12,
									spriteDrawY - 28);
					}
				}
				if (((Entity) (obj)).textSpoken != null
						&& (j >= playerCount || publicChatMode == 0
						|| publicChatMode == 3 || publicChatMode == 1
						&& isFriendOrSelf(((Player) obj).name))) {
					npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height);
					if (spriteDrawX > -1 && anInt974 < anInt975) {
						anIntArray979[anInt974] = chatTextDrawingArea
								.method384(((Entity) (obj)).textSpoken) / 2;
						anIntArray978[anInt974] = chatTextDrawingArea.anInt1497;
						anIntArray976[anInt974] = spriteDrawX;
						anIntArray977[anInt974] = spriteDrawY;
						anIntArray980[anInt974] = ((Entity) (obj)).anInt1513;
						anIntArray981[anInt974] = ((Entity) (obj)).anInt1531;
						anIntArray982[anInt974] = ((Entity) (obj)).textCycle;
						aStringArray983[anInt974++] = ((Entity) (obj)).textSpoken;
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 >= 1
								&& ((Entity) (obj)).anInt1531 <= 3) {
							anIntArray978[anInt974] += 10;
							anIntArray977[anInt974] += 5;
						}
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 == 4)
							anIntArray979[anInt974] = 60;
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 == 5)
							anIntArray978[anInt974] += 5;
					}
				}
				if (((Entity) (obj)).loopCycleStatus > loopCycle) {
					try {
						npcScreenPos(((Entity) (obj)),
								((Entity) (obj)).height + 15);
						if (spriteDrawX > -1) {
							int HpPercent;
							if(((Entity) (obj)).currentHealth > 0) {
								int i1 = (((Entity) (obj)).currentHealth * 30)
										/ ((Entity) (obj)).maxHealth;
								if (i1 > 30)
									i1 = 30;
								int hpPercent = (((Entity) (obj)).currentHealth * 90)
										/ ((Entity) (obj)).maxHealth;
								if (hpPercent > 90)
									hpPercent = 90;
								HpPercent = (((Entity) (obj)).currentHealth * 56)
										/ ((Entity) (obj)).maxHealth;
								if (HpPercent > 56)
									HpPercent = 56;
							} else
								HpPercent = 0;
							SpriteCache.spriteCache[34].drawSprite(
									spriteDrawX - 28, spriteDrawY - 5);
							Sprite s = Sprite.getCutted(
									SpriteCache.spriteCache[33], HpPercent,
									SpriteCache.spriteCache[33].myHeight);
							s.drawSprite(spriteDrawX - 28, spriteDrawY - 5);
							/*
							 * if (obj instanceof NPC && ((NPC) obj).maxHealth
							 * >= 2500) {
							 * SpriteCache.spriteCache[35].drawSprite(
							 * spriteDrawX - 44, spriteDrawY - 5);
							 * SpriteCache.spriteCache[33] =
							 * Sprite.getResizedSprite
							 * (SpriteCache.spriteCache[33], HpPercent-2,
							 * SpriteCache.spriteCache[33].myHeight);
							 * SpriteCache
							 * .spriteCache[33].drawSprite(spriteDrawX - 44,
							 * spriteDrawY - 5); }
							 */
						}
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
				for (int j1 = 0; j1 < 4; j1++)
					if (((Entity) (obj)).hitsLoopCycle[j1] > loopCycle) {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height / 2);
						if(!getOption("old_hits")) {
							if (spriteDrawX > -1) {
								Entity e = ((Entity) (obj));
								if (e.moveTimer[j1] == 0) {
									if (e.hitmarkMove[j1] > -14)
										e.hitmarkMove[j1]--;
									e.moveTimer[j1] = 2;
								} else {
									e.moveTimer[j1]--;
								}
								if (e.hitmarkMove[j1] <= -14)
									e.hitmarkTrans[j1] -= 10;
								hitmarkDraw(e, String.valueOf(e.hitArray[j1]).length(), e.hitMarkTypes[j1],
										e.hitIcon[j1], e.hitArray[j1],
										e.soakDamage[j1], e.hitmarkMove[j1],
										e.hitmarkTrans[j1], j1);
							}
						} else {
							if(spriteDrawX > -1)
							{
								if(j1 == 1)
									spriteDrawY -= 20;
								if(j1 == 2)
								{
									spriteDrawX -= 15;
									spriteDrawY -= 10;
								}
								if(j1 == 3)
								{
									spriteDrawX += 15;
									spriteDrawY -= 10;
								}
								Entity e = ((Entity) (obj));
								int dmg = e.hitArray[j1];
								if(dmg > 0) {
									if(!getOption("constitution")) {
										dmg = dmg / 10; 
										if(dmg == 0)
											dmg = 1;
									}
									oldHitmarks[0].drawSprite(spriteDrawX - 11, spriteDrawY - 12);
								} else {
									oldHitmarks[1].drawSprite(spriteDrawX - 12, spriteDrawY - 13);
								}
								smallText.drawText(0, String.valueOf(dmg), spriteDrawY + 4, spriteDrawX);
								smallText.drawText(0xffffff, String.valueOf(dmg), spriteDrawY + 3, spriteDrawX - 1);
							}
						}
					}
			}
			for (int k = 0; k < anInt974; k++) {
				int k1 = anIntArray976[k];
				int l1 = anIntArray977[k];
				int j2 = anIntArray979[k];
				int k2 = anIntArray978[k];
				boolean flag = true;
				while (flag) {
					flag = false;
					for (int l2 = 0; l2 < k; l2++)
						if (l1 + 2 > anIntArray977[l2] - anIntArray978[l2]
								&& l1 - k2 < anIntArray977[l2] + 2
								&& k1 - j2 < anIntArray976[l2]
										+ anIntArray979[l2]
												&& k1 + j2 > anIntArray976[l2]
														- anIntArray979[l2]
																&& anIntArray977[l2] - anIntArray978[l2] < l1) {
							l1 = anIntArray977[l2] - anIntArray978[l2];
							flag = true;
						}

				}
				spriteDrawX = anIntArray976[k];
				spriteDrawY = anIntArray977[k] = l1;
				String s = aStringArray983[k];
				if (anInt1249 == 0) {
					int i3 = 0xffff00;
					if (anIntArray980[k] < 6)
						i3 = anIntArray965[anIntArray980[k]];
					if (anIntArray980[k] == 6)
						i3 = anInt1265 % 20 >= 10 ? 0xffff00 : 0xff0000;
						if (anIntArray980[k] == 7)
							i3 = anInt1265 % 20 >= 10 ? 65535 : 255;
							if (anIntArray980[k] == 8)
								i3 = anInt1265 % 20 >= 10 ? 0x80ff80 : 45056;
								if (anIntArray980[k] == 9) {
									int j3 = 150 - anIntArray982[k];
									if (j3 < 50)
										i3 = 0xff0000 + 1280 * j3;
									else if (j3 < 100)
										i3 = 0xffff00 - 0x50000 * (j3 - 50);
									else if (j3 < 150)
										i3 = 65280 + 5 * (j3 - 100);
								}
								if (anIntArray980[k] == 10) {
									int k3 = 150 - anIntArray982[k];
									if (k3 < 50)
										i3 = 0xff0000 + 5 * k3;
									else if (k3 < 100)
										i3 = 0xff00ff - 0x50000 * (k3 - 50);
									else if (k3 < 150)
										i3 = (255 + 0x50000 * (k3 - 100)) - 5 * (k3 - 100);
								}
								if (anIntArray980[k] == 11) {
									int l3 = 150 - anIntArray982[k];
									if (l3 < 50)
										i3 = 0xffffff - 0x50005 * l3;
									else if (l3 < 100)
										i3 = 65280 + 0x50005 * (l3 - 50);
									else if (l3 < 150)
										i3 = 0xffffff - 0x50000 * (l3 - 100);
								}
								if (anIntArray981[k] == 0) {
									chatTextDrawingArea.drawText(0, s, spriteDrawY + 1,
											spriteDrawX);
									chatTextDrawingArea.drawText(i3, s, spriteDrawY,
											spriteDrawX);
								}
								if (anIntArray981[k] == 1) {
									chatTextDrawingArea.method386(0, s, spriteDrawX,
											anInt1265, spriteDrawY + 1);
									chatTextDrawingArea.method386(i3, s, spriteDrawX,
											anInt1265, spriteDrawY);
								}
								if (anIntArray981[k] == 2) {
									chatTextDrawingArea.method387(spriteDrawX, s,
											anInt1265, spriteDrawY + 1, 0);
									chatTextDrawingArea.method387(spriteDrawX, s,
											anInt1265, spriteDrawY, i3);
								}
								if (anIntArray981[k] == 3) {
									chatTextDrawingArea.method388(150 - anIntArray982[k],
											s, anInt1265, spriteDrawY + 1, spriteDrawX, 0);
									chatTextDrawingArea.method388(150 - anIntArray982[k],
											s, anInt1265, spriteDrawY, spriteDrawX, i3);
								}
								if (anIntArray981[k] == 4) {
									int i4 = chatTextDrawingArea.method384(s);
									int k4 = ((150 - anIntArray982[k]) * (i4 + 100)) / 150;
									DrawingArea.setDrawingArea(334, spriteDrawX - 50,
											spriteDrawX + 50, 0);
									chatTextDrawingArea.method385(0, s, spriteDrawY + 1,
											(spriteDrawX + 50) - k4);
									chatTextDrawingArea.method385(i3, s, spriteDrawY,
											(spriteDrawX + 50) - k4);
									DrawingArea.defaultDrawingAreaSize();
								}
								if (anIntArray981[k] == 5) {
									int j4 = 150 - anIntArray982[k];
									int l4 = 0;
									if (j4 < 25)
										l4 = j4 - 25;
									else if (j4 > 125)
										l4 = j4 - 125;
									DrawingArea
									.setDrawingArea(spriteDrawY + 5, 0, 512,
											spriteDrawY
											- chatTextDrawingArea.anInt1497
											- 1);
									chatTextDrawingArea.drawText(0, s,
											spriteDrawY + 1 + l4, spriteDrawX);
									chatTextDrawingArea.drawText(i3, s, spriteDrawY + l4,
											spriteDrawX);
									DrawingArea.defaultDrawingAreaSize();
								}
				} else {
					chatTextDrawingArea.drawText(0, s, spriteDrawY + 1,
							spriteDrawX);
					chatTextDrawingArea.drawText(0xffff00, s, spriteDrawY,
							spriteDrawX);
				}
			}
		} catch (Exception e) {
		}
	}

	private void delFriend(long l) {
		try {
			if (l == 0L)
				return;
			for (int i = 0; i < friendsCount; i++) {
				if (friendsListAsLongs[i] != l)
					continue;
				friendsCount--;
				needDrawTabArea = true;
				inputString = "[DFR]" + friendsList[i];
				for (int j = i; j < friendsCount; j++) {
					friendsList[j] = friendsList[j + 1];
					friendsNodeIDs[j] = friendsNodeIDs[j + 1];
					friendsListAsLongs[j] = friendsListAsLongs[j + 1];
				}
				sendPacket(1003);
				stream.createFrame(215);
				stream.writeQWord(l);
				break;
			}
		} catch (RuntimeException runtimeexception) {
			signlink.reporterror("18622, " + false + ", " + l + ", "
					+ runtimeexception.toString());
			throw new RuntimeException();
		}
	}

	private void processTextCycles() {
		for (int i = -1; i < playerCount; i++) {
			int j;
			if (i == -1)
				j = myPlayerIndex;
			else
				j = playerIndices[i];
			Player player = playerArray[j];
			if (player != null && player.textCycle > 0) {
				player.textCycle--;
				if (player.textCycle == 0)
					player.textSpoken = null;
			}
		}
		for (int k = 0; k < npcCount; k++) {
			int l = npcIndices[k];
			NPC npc = npcArray[l];
			if (npc != null && npc.textCycle > 0) {
				npc.textCycle--;
				if (npc.textCycle == 0)
					npc.textSpoken = null;
			}
		}
	}

	private void calcCameraPos() {
		int i = anInt1098 * 128 + 64;
		int j = anInt1099 * 128 + 64;
		int k = getFloorDrawHeight(plane, j, i) - anInt1100;
		if (xCameraPos < i) {
			xCameraPos += anInt1101 + ((i - xCameraPos) * anInt1102) / 1000;
			if (xCameraPos > i)
				xCameraPos = i;
		}
		if (xCameraPos > i) {
			xCameraPos -= anInt1101 + ((xCameraPos - i) * anInt1102) / 1000;
			if (xCameraPos < i)
				xCameraPos = i;
		}
		if (zCameraPos < k) {
			zCameraPos += anInt1101 + ((k - zCameraPos) * anInt1102) / 1000;
			if (zCameraPos > k)
				zCameraPos = k;
		}
		if (zCameraPos > k) {
			zCameraPos -= anInt1101 + ((zCameraPos - k) * anInt1102) / 1000;
			if (zCameraPos < k)
				zCameraPos = k;
		}
		if (yCameraPos < j) {
			yCameraPos += anInt1101 + ((j - yCameraPos) * anInt1102) / 1000;
			if (yCameraPos > j)
				yCameraPos = j;
		}
		if (yCameraPos > j) {
			yCameraPos -= anInt1101 + ((yCameraPos - j) * anInt1102) / 1000;
			if (yCameraPos < j)
				yCameraPos = j;
		}
		i = anInt995 * 128 + 64;
		j = anInt996 * 128 + 64;
		k = getFloorDrawHeight(plane, j, i) - anInt997;
		int l = i - xCameraPos;
		int i1 = k - zCameraPos;
		int j1 = j - yCameraPos;
		int k1 = (int) Math.sqrt(l * l + j1 * j1);
		int l1 = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
		int i2 = (int) (Math.atan2(l, j1) * -325.94900000000001D) & 0x7ff;
		if (l1 < 128)
			l1 = 128;
		if (l1 > 383)
			l1 = 383;
		if (yCameraCurve < l1) {
			yCameraCurve += anInt998 + ((l1 - yCameraCurve) * anInt999) / 1000;
			if (yCameraCurve > l1)
				yCameraCurve = l1;
		}
		if (yCameraCurve > l1) {
			yCameraCurve -= anInt998 + ((yCameraCurve - l1) * anInt999) / 1000;
			if (yCameraCurve < l1)
				yCameraCurve = l1;
		}
		int j2 = i2 - xCameraCurve;
		if (j2 > 1024)
			j2 -= 2048;
		if (j2 < -1024)
			j2 += 2048;
		if (j2 > 0) {
			xCameraCurve += anInt998 + (j2 * anInt999) / 1000;
			xCameraCurve &= 0x7ff;
		}
		if (j2 < 0) {
			xCameraCurve -= anInt998 + (-j2 * anInt999) / 1000;
			xCameraCurve &= 0x7ff;
		}
		int k2 = i2 - xCameraCurve;
		if (k2 > 1024)
			k2 -= 2048;
		if (k2 < -1024)
			k2 += 2048;
		if (k2 < 0 && j2 > 0 || k2 > 0 && j2 < 0)
			xCameraCurve = i2;
	}

	private void drawMenu() {
		int i = menuOffsetX;
		int j = menuOffsetY;
		int k = menuWidth;
		int j1 = super.mouseX;
		int k1 = super.mouseY;
		int l = menuHeight + 1;
		int i1 = 0x5d5447;
		if (menuScreenArea == 1 && (clientSize > 0)) {
			i += 519;// +extraWidth;
			j += 168;// +extraHeight;
		}
		if (menuScreenArea == 2 && (clientSize > 0)) {
			j += 338;
		}
		if (menuScreenArea == 3 && (clientSize > 0)) {
			i += 515;
			j += 0;
		}
		if (menuScreenArea == 0) {
			j1 -= 4;
			k1 -= 4;
		}
		if (menuScreenArea == 1) {
			if (!(clientSize > 0)) {
				j1 -= 519;
				k1 -= 168;
			}
		}
		if (menuScreenArea == 2) {
			if (!(clientSize > 0)) {
				j1 -= 17;
				k1 -= 338;
			}
		}
		if (menuScreenArea == 3 && !(clientSize > 0)) {
			j1 -= 515;
			k1 -= 0;
		}
		if (menuToggle == false) {
			DrawingArea.fillRectangle(i1, j, k, l, 150, i);
			DrawingArea.fillRectangle(0, j + 1, k - 2, 16, 150, i + 1);
			DrawingArea.fillPixels(i + 1, k - 2, l - 19, 0, j + 18);
			DrawingArea.drawRectangle(j + 18, l - 19, 150, 0, k - 2, i + 1);
			chatTextDrawingArea.method385(0xc6b895, "Choose Option", j + 14,
					i + 3);
			chatTextDrawingArea.method385(0xc6b895, "Choose Option", j + 14,
					i + 3);
			for (int l1 = 0; l1 < menuActionRow; l1++) {
				int i2 = j + 31 + (menuActionRow - 1 - l1) * 15;
				int j2 = 0xffffff;
				if (j1 > i && j1 < i + k && k1 > i2 - 13 && k1 < i2 + 3)
					j2 = 0xffff00;
				chatTextDrawingArea.drawRegularText(true, i + 3, j2,
						menuActionName[l1], i2);
			}
		} else if (menuToggle == true) {
			// DrawingArea.drawPixels(height, yPos, xPos, color, width);
			// DrawingArea.fillPixels(xPos, width, height, color, yPos);
			DrawingArea.drawPixels(l - 4, j + 2, i, 0x706a5e, k);
			DrawingArea.drawPixels(l - 2, j + 1, i + 1, 0x706a5e, k - 2);
			DrawingArea.drawPixels(l, j, i + 2, 0x706a5e, k - 4);
			DrawingArea.drawPixels(l - 2, j + 1, i + 3, 0x2d2822, k - 6);
			DrawingArea.drawPixels(l - 4, j + 2, i + 2, 0x2d2822, k - 4);
			DrawingArea.drawPixels(l - 6, j + 3, i + 1, 0x2d2822, k - 2);
			DrawingArea.drawPixels(l - 22, j + 19, i + 2, 0x524a3d, k - 4);
			DrawingArea.drawPixels(l - 22, j + 20, i + 3, 0x524a3d, k - 6);
			DrawingArea.drawPixels(l - 23, j + 20, i + 3, 0x2b271c, k - 6);
			DrawingArea.fillPixels(i + 3, k - 6, 1, 0x2a291b, j + 2);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x2a261b, j + 3);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x252116, j + 4);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x211e15, j + 5);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x1e1b12, j + 6);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x1a170e, j + 7);
			DrawingArea.fillPixels(i + 2, k - 4, 2, 0x15120b, j + 8);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x100d08, j + 10);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x090a04, j + 11);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x080703, j + 12);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x090a04, j + 13);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x070802, j + 14);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x090a04, j + 15);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x070802, j + 16);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x090a04, j + 17);
			DrawingArea.fillPixels(i + 2, k - 4, 1, 0x2a291b, j + 18);
			DrawingArea.fillPixels(i + 3, k - 6, 1, 0x564943, j + 19);
			chatTextDrawingArea.method385(0xc6b895, "Choose Option", j + 14,
					i + 3);
			for (int l1 = 0; l1 < menuActionRow; l1++) {
				int i2 = j + 31 + (menuActionRow - 1 - l1) * 15;
				int j2 = 0xc6b895;
				if (j1 > i && j1 < i + k && k1 > i2 - 13 && k1 < i2 + 3) {
					DrawingArea.drawPixels(15, i2 - 11, i + 3, 0x6f695d,
							menuWidth - 6);
					j2 = 0xeee5c6;
				}
				chatTextDrawingArea.drawRegularText(true, i + 4, j2,
						menuActionName[l1], i2 + 1);
			}
		}
	}

	private void addFriend(long l) {
		try {
			if (l == 0L)
				return;
			if (friendsCount >= 100 && anInt1046 != 1) {
				pushMessage(
						"Your friendlist is full. Max of 100 for free users, and 200 for members",
						0, "");
				return;
			}
			if (friendsCount >= 200) {
				pushMessage(
						"Your friendlist is full. Max of 100 for free users, and 200 for members",
						0, "");
				return;
			}
			String s = TextClass.fixName(TextClass.nameForLong(l));
			for (int i = 0; i < friendsCount; i++)
				if (friendsListAsLongs[i] == l) {
					pushMessage(s + " is already on your friend list", 0, "");
					return;
				}
			for (int j = 0; j < ignoreCount; j++)
				if (ignoreListAsLongs[j] == l) {
					pushMessage("Please remove " + s
							+ " from your ignore list first", 0, "");
					return;
				}
			if (s.equals(myPlayer.name))
				return;
			else {
				friendsList[friendsCount] = s;
				friendsListAsLongs[friendsCount] = l;
				friendsNodeIDs[friendsCount] = 0;
				friendsCount++;
				needDrawTabArea = true;
				stream.createFrame(188);
				stream.writeQWord(l);
				inputString = "[FRI]" + s;
				sendPacket(1003);
				int slot = 44001;
				for (int a = 44001; a <= 44200; a++) {
					sendFrame126("", slot);
					slot++;
				}
				slot = 44801;
				for (int d = 44801; d <= 45000; d++) {
					sendFrame126("", slot);
					slot++;
				}
				return;
			}
		} catch (RuntimeException runtimeexception) {
			signlink.reporterror("15283, " + (byte) 68 + ", " + l + ", "
					+ runtimeexception.toString());
		}
		// throw new RuntimeException();
	}

	private int getFloorDrawHeight(int i, int j, int k) {
		int l = k >> 7;
				int i1 = j >> 7;
			if (l < 0 || i1 < 0 || l > 103 || i1 > 103)
				return 0;
			int j1 = i;
			if (j1 < 3 && (byteGroundArray[1][l][i1] & 2) == 2)
				j1++;
			int k1 = k & 0x7f;
			int l1 = j & 0x7f;
			int i2 = intGroundArray[j1][l][i1] * (128 - k1)
					+ intGroundArray[j1][l + 1][i1] * k1 >> 7;
			int j2 = intGroundArray[j1][l][i1 + 1] * (128 - k1)
					+ intGroundArray[j1][l + 1][i1 + 1] * k1 >> 7;
									return i2 * (128 - l1) + j2 * l1 >> 7;
	}

	private static String intToKOrMil(int j) {
		if (j < 0x186a0)
			return String.valueOf(j);
		if (j < 0x989680)
			return j / 1000 + "K";
		else
			return j / 0xf4240 + "M";
	}

	private void resetLogout() {
		saveSettings();
		/*if (!dontRememberMe) {
			Account a_ = null;
			for (Account a : Account.accounts) {
				if (a.getUsername().equals(myUsername)) {
					a_ = a;
					a_.setClientSize(clientSize);
					a_.setIDKHead(myPlayer.equipment[0] < 1164 ? myAppearance[0] == 0 ? myHeadAndJaw[0]
							: myAppearance[0]
									: 0);
					a_.setHelmet(myPlayer.equipment[0] >= 1164 ? myPlayer.equipment[0] - 512
							: 0);
					a_.setJaw(myHeadAndJaw[1]);
					a_.setGender(myPlayer.myGender);
					a_.setPassword(myPassword);
				}
			}
			if (Account.accounts.size() < 4) {
				if (a_ == null) {
					a_ = new Account(
							myUsername,
							myPassword,
							clientSize,
							myPlayer.equipment[0] >= 1164 ? myPlayer.equipment[0] - 512
									: 0,
									myPlayer.equipment[0] < 1164 ? myAppearance[0] == 0 ? myHeadAndJaw[0]
											: myAppearance[0]
													: 0, myAppearance[1] == 0 ? myHeadAndJaw[1]
															: myAppearance[1], myPlayer.myGender);
					Account.accounts.add(a_);
				}
			}
			//if (a_ != null)
			//	a_.save();
			//RSInterface.buildPlayerMenu(Account.accounts);
		}*/
		//	toggleSize(0);
		prayClicked = false;
		followPlayer = 0;
		followNPC = 0;
		followDistance = 1;
		try {
			if (socketStream != null)
				socketStream.close();
		} catch (Exception _ex) {
		}
		socketStream = null;
		loggedIn = false;
		previousScreenState = 0;
		loginScreenState = 0;
		loginCode = 0;
		clearMemoryCaches();
		worldController.initToNull();
		for (int i = 0; i < 4; i++)
			clippingPlanes[i].setDefault();
		System.gc();
		stopMidi();
		currentSong = -1;
		nextSong = -1;
		prevSong = 0;
	}

	private void setMyAppearance() {
		aBoolean1031 = true;
		for (int j = 0; j < 7; j++) {
			myAppearance[j] = -1;
			for (int k = 0; k < IDK.cache.length; k++) {
				if (IDK.cache[k].notSelectable
						|| IDK.cache[k].bodyPartID != j + (isMale ? 0 : 7))
					continue;
				myAppearance[j] = k;
				break;
			}
		}
	}

	private void updateNPCMovement(int i, Stream stream) {
		while (stream.bitPosition + 21 < i * 8) {
			int k = stream.readBits(14);
			if (k == 16383)
				break;
			if (npcArray[k] == null)
				npcArray[k] = new NPC();
			NPC npc = npcArray[k];
			npcIndices[npcCount++] = k;
			npc.loopCycle = loopCycle;
			int l = stream.readBits(5);
			if (l > 15)
				l -= 32;
			int i1 = stream.readBits(5);
			if (i1 > 15)
				i1 -= 32;
			int j1 = stream.readBits(1);
			npc.desc = NPCDef.forID(stream.readBits(Configuration.NPC_BITS));
			int k1 = stream.readBits(1);
			if (k1 == 1)
				playersToUpdate[playersToUpdateCount++] = k;
			npc.boundDim = npc.desc.squaresNeeded;
			npc.anInt1504 = npc.desc.degreesToTurn;
			npc.walkAnimIndex = npc.desc.walkAnim;
			npc.runAnimation = npc.desc.runAnim;
			npc.turn180AnimIndex = npc.desc.turn180AnimIndex;
			npc.turn90CWAnimIndex = npc.desc.turn90CWAnimIndex;
			npc.turn90CCWAnimIndex = npc.desc.turn90CCWAnimIndex;
			npc.standAnimIndex = npc.desc.standAnim;
			npc.setPos(myPlayer.pathX[0] + i1, myPlayer.pathY[0] + l, j1 == 1);
		}
		stream.finishBitAccess();
	}

	public void processGameLoop() {
		if (loadingError || genericLoadingError)
			return;
		loopCycle++;
		checkSize();
		if (!loggedIn){
			try {
				processLoginScreenInput();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				mainGameProcessor();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		}
		processOnDemandQueue();
	}

	private void processPlayers(boolean flag) {
		if (myPlayer.x >> 7 == destX && myPlayer.y >> 7 == destY)
			destX = 0;
		int j = playerCount;
		if (flag)
			j = 1;
		for (int l = 0; l < j; l++) {
			Player player;
			int i1;
			if (flag) {
				player = myPlayer;
				i1 = myPlayerIndex << 14;
			} else {
				player = playerArray[playerIndices[l]];
				i1 = playerIndices[l] << 14;
			}
			if (player == null || !player.isVisible())
				continue;
			player.aBoolean1699 = (lowMem && playerCount > 50 || playerCount > 200)
					&& !flag && player.forcedAnimId == player.standAnimIndex;
			int j1 = player.x >> 7;
				int k1 = player.y >> 7;
		if (j1 < 0 || j1 >= 104 || k1 < 0 || k1 >= 104)
			continue;
		if (player.tranformIntoModel != null
				&& loopCycle >= player.startTimeTransform
				&& loopCycle < player.transformedTimer) {
			player.aBoolean1699 = false;
			player.z = getFloorDrawHeight(plane, player.y, player.x);
			worldController.addSingleTileEntity(plane, player.y, player,
					player.anInt1552, player.extendedYMax, player.x,
					player.z, player.extendedXMin, player.extendedXMax, i1,
					player.extendedYMin);
			continue;
		}
		if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64) {
			if (anIntArrayArray929[j1][k1] == anInt1265)
				continue;
			anIntArrayArray929[j1][k1] = anInt1265;
		}
		player.z = getFloorDrawHeight(plane, player.y, player.x);
		worldController.addMutipleTileEntity(plane, player.anInt1552,
				player.z, i1, player.y, 60, player.x, player,
				player.aBoolean1541);
		}
	}

	private boolean promptUserForInput(RSInterface class9) {
		int j = class9.contentType;
		if (inputTextType == 2) {
			if (j == 201) {
				inputTaken = true;
				inputDialogState = 0;
				showInput = true;
				promptInput = "";
				friendsListAction = 1;
				promptMessage = "Enter name of friend to add to list";
			}
			if (j == 202) {
				inputTaken = true;
				inputDialogState = 0;
				showInput = true;
				promptInput = "";
				friendsListAction = 2;
				promptMessage = "Enter name of friend to delete from list";
			}
		}
		if (j == 7712) {
			doGEAction(721);
		}
		if (j == 7713) {
			doGEAction(722);
		}
		if (j == 7714) {
			doGEAction(723);
		}
		if (j == 7715) {
			doGEAction(724);
		}
		if (j == 22222) {
			inputTaken = true;
			showInput = true;
			amountOrNameInput = "";
			promptInput = "";
			inputDialogState = 0;
			interfaceButtonAction = 6199;
			promptMessage = "Enter a name for the clan chat.";
		}
		if (j == 677) {
			inputTaken = true;
			showInput = true;
			amountOrNameInput = "";
			promptInput = "";
			inputDialogState = 0;
			interfaceButtonAction = 6200;
			promptMessage = "Enter name of the player you would like kicked.";
		}
		if (j == 205) {
			anInt1011 = 250;
			return true;
		}
		if (j == 6650) {
			inputTaken = true;
			showInput = true;
			amountOrNameInput = "";
			inputDialogState = -1;
			interfaceButtonAction = 6651;
			promptMessage = "Enter the note you would like to save";
		}
		if (j == 501) {
			inputTaken = true;
			inputDialogState = 0;
			showInput = true;
			promptInput = "";
			friendsListAction = 4;
			promptMessage = "Enter name of player to add to list";
		}
		if (j == 502) {
			inputTaken = true;
			inputDialogState = 0;
			showInput = true;
			promptInput = "";
			friendsListAction = 5;
			promptMessage = "Enter name of player to delete from list";
		}
		if (j >= 300 && j <= 313) {
			int k = (j - 300) / 2;
			int j1 = j & 1;
			int i2 = myAppearance[k];
			if (i2 != -1) {
				do {
					if (j1 == 0 && --i2 < 0)
						i2 = IDK.cache.length - 1;
					if (j1 == 1 && ++i2 >= IDK.cache.length)
						i2 = 0;
				} while (IDK.cache[i2].notSelectable
						|| IDK.cache[i2].bodyPartID != k + (isMale ? 0 : 7));
				if((j == 310 || j == 311) && i2 >= 90)
					i2 = 36;
				if((j == 306 || j == 307) && i2 >= 588)
					i2 = 26;
				if((j == 305 || j == 304) && i2 >= 443)
					i2 = 18;
				if((j == 308 || j == 309) && i2 >= 364)
					i2 = 33;
				if((j == 312 || j == 313) && i2 > 43)
					i2 = 42;
				myAppearance[k] = i2;
				aBoolean1031 = true;
			}
		}
		if (j >= 314 && j <= 323) {
			int l = (j - 314) / 2;
			int k1 = j & 1;
			int j2 = myAppearanceColors[l];
			if (k1 == 0 && --j2 < 0)
				j2 = anIntArrayArray1003[l].length - 1;
			if (k1 == 1 && ++j2 >= anIntArrayArray1003[l].length)
				j2 = 0;
			myAppearanceColors[l] = j2;
			aBoolean1031 = true;
		}
		if (j == 324 && !isMale) {
			isMale = true;
			setMyAppearance();
		}
		if (j == 325 && isMale) {
			isMale = false;
			setMyAppearance();
		}
		if (j == 326) {
			//Cheaphax lvl 9000
			String s = " "+(isMale ? 0 : 1)+"";
			for (int i1 = 0; i1 < 7; i1++)
				s += " "+(myAppearance[i1]);
			for (int l1 = 0; l1 < 5; l1++)
				s += " "+(myAppearanceColors[l1]);
			stream.createFrame(11);
			stream.writeWordBigEndian(s.substring(1).length() +1);
			stream.writeString(s.substring(1));
			return true;
		}
		if (j == 613)
			canMute = !canMute;
		if (j >= 601 && j <= 612) {
			clearTopInterfaces();
			if (reportAbuseInput.length() > 0) {
				stream.createFrame(218);
				stream.writeQWord(TextClass.longForName(reportAbuseInput));
				stream.writeWordBigEndian(j - 601);
				stream.writeWordBigEndian(canMute ? 1 : 0);
			}
		}
		return false;
	}

	private void processPlayerUpdating(Stream stream) {
		for (int j = 0; j < playersToUpdateCount; j++) {
			int k = playersToUpdate[j];
			Player player = playerArray[k];
			int l = stream.readUnsignedByte();
			if ((l & 0x40) != 0)
				l += stream.readUnsignedByte() << 8;
			readPlayerUpdateMask(l, k, stream, player);
		}
	}

	/*public void drawMapScenes(int y, int primaryColor, int x,
			int secondaryColor, int z) {
		int uid = worldController.getWallObjectUID(z, x, y);
		int newUID = worldController.fetchWallObjectNewUID(z, x, y);
		if ((uid ^ 0xffffffffffffffffL) != -1L || uid != 0) {
			int resourceTag = worldController.getIDTagForXYZ(z, x, y, uid);
			int direction = resourceTag >> 6 & 3;// direction
			int type = resourceTag & 0x1f;// type
			int color = primaryColor;// color
			if (uid > 0)
				color = secondaryColor;
			int mapPixels[] = miniMap.myPixels;
			int pixel = 24624 + x * 4 + (103 - y) * 512 * 4;
			int objectId = worldController.fetchWallDecorationNewUID(z, x, y);
			ObjectDef objDef = ObjectDef.forID(objectId);
			if (objDef.mapSceneID != -1) {
				Background scene = mapScenes[objDef.mapSceneID];
				if (scene != null) {
					int scene_x = (objDef.sizeX * 4 - scene.imgWidth) / 2;
					int scene_y = (objDef.sizeY * 4 - scene.imgHeight) / 2;
					scene.drawBackground(48 + x * 4 + scene_x, 48
							+ (104 - y - objDef.sizeY) * 4 + scene_y);
				}
			} else {
				if ((objDef.mapSceneID ^ 0xffffffff) == 0) {
					if (type == 0 || type == 2)
						if (direction == 0) {
							mapPixels[pixel] = color;
							mapPixels[pixel + 512] = color;
							mapPixels[1024 + pixel] = color;
							mapPixels[1536 + pixel] = color;
						} else if ((direction ^ 0xffffffff) == -2
								|| direction == 1) {
							mapPixels[pixel] = color;
							mapPixels[pixel + 1] = color;
							mapPixels[pixel + 2] = color;
							mapPixels[3 + pixel] = color;
						} else if (direction == 2) {
							mapPixels[pixel - -3] = color;
							mapPixels[3 + (pixel + 512)] = color;
							mapPixels[3 + (pixel + 1024)] = color;
							mapPixels[1536 + (pixel - -3)] = color;
						} else if (direction == 3) {
							mapPixels[pixel + 1536] = color;
							mapPixels[pixel + 1536 + 1] = color;
							mapPixels[2 + pixel + 1536] = color;
							mapPixels[pixel + 1536] = color;
						}
					if (type == 3)
						if (direction == 0)
							mapPixels[pixel] = color;
						else if (direction == 1)
							mapPixels[pixel + 3] = color;
						else if (direction == 2)
							mapPixels[pixel + 3 + 1536] = color;
						else if (direction == 3)
							mapPixels[pixel + 1536] = color;
					if (type == 2)
						if (direction == 3) {
							mapPixels[pixel] = color;
							mapPixels[pixel + 512] = color;
							mapPixels[pixel + 1024] = color;
							mapPixels[pixel + 1536] = color;
						} else if (direction == 0) {
							mapPixels[pixel] = color;
							mapPixels[pixel + 1] = color;
							mapPixels[pixel + 2] = color;
							mapPixels[pixel + 3] = color;
						} else if (direction == 1) {
							mapPixels[pixel + 3] = color;
							mapPixels[pixel + 3 + 512] = color;
							mapPixels[pixel + 3 + 1024] = color;
							mapPixels[pixel + 3 + 1536] = color;
						} else if (direction == 2) {
							mapPixels[pixel + 1536] = color;
							mapPixels[pixel + 1536 + 1] = color;
							mapPixels[pixel + 1536 + 2] = color;
							mapPixels[pixel + 1536 + 3] = color;
						}
				}
			}
		}
		uid = worldController.getInteractableObjectUID(z, x, y);
		newUID = worldController.fetchObjectMeshNewUID(z, x, y);
		if (uid != 0) {
			int resourceTag = worldController.getIDTagForXYZ(z, x, y, uid);
			int direction = resourceTag >> 6 & 3;
			int type = resourceTag & 0x1f;
			int objectId = worldController.fetchObjectMeshNewUID(z, x, y);

			ObjectDef objDef = ObjectDef.forID(objectId);
			if (objDef.mapSceneID != -1) {
				Background scene = mapScenes[objDef.mapSceneID];
				if (scene != null) {
					int sceneX = (objDef.sizeX * 4 - scene.imgWidth) / 2;
					int sceneY = (objDef.sizeY * 4 - scene.imgHeight) / 2;
					scene.drawBackground(48 + x * 4 + sceneX, 48
							+ (104 - y - objDef.sizeY) * 4 + sceneY);
				}
			} else if (type == 9) {
				int color = 0xeeeeee;
				if (uid > 0)
					color = 0xee0000;
				int mapPixels[] = miniMap.myPixels;
				int pixel = 24624 + x * 4 + (103 - y) * 512 * 4;
				if (direction == 0 || direction == 2) {
					mapPixels[pixel + 1536] = color;
					mapPixels[pixel + 1024 + 1] = color;
					mapPixels[pixel + 512 + 2] = color;
					mapPixels[pixel + 3] = color;
				} else {
					mapPixels[pixel] = color;
					mapPixels[pixel + 512 + 1] = color;
					mapPixels[pixel + 1024 + 2] = color;
					mapPixels[pixel + 1536 + 3] = color;
				}
			}
		}
		uid = worldController.fetchGroundDecorationNewUID(z, x, y);
		if (uid > 0 || uid != 0) {
			ObjectDef objDef = ObjectDef.forID(uid);
			if (objDef.mapSceneID != -1) {
				Background scene = mapScenes[objDef.mapSceneID];
				if (scene != null) {
					int sceneX = (objDef.sizeX * 4 - scene.imgWidth) / 2;
					int sceneY = (objDef.sizeY * 4 - scene.imgHeight) / 2;
					scene.drawBackground(48 + x * 4 + sceneX, 48
							+ (104 - y - objDef.sizeY) * 4 + sceneY);
				}
			}
		}
	}*/

	public void drawMapScenes(int y, int primaryColor, int x, int secondaryColor, int z) {
		int uid = worldController.getWallObjectUID(z, x, y);
		if(((uid ^ 0xffffffffffffffffL) != -1L) || uid != 0) 
		{
			int resource_tag = worldController.getIDTagForXYZ(z, x, y, uid);
			int direction = resource_tag >> 6 & 3;
			int type = resource_tag & 0x1f;
			int color = primaryColor;
			if(uid > 0)
				color = secondaryColor;

			int scene_pixels[] = miniMap.myPixels;
			int pixel = 24624 + x * 4 + (103 - y) * 512 * 4;
			int object_id = worldController.fetchWallObjectNewUID(z, x, y);
			ObjectDef def = ObjectDef.forID(object_id);
			if(def.mapSceneID != -1) {
				Background scene = mapScenes[def.mapSceneID];
				if(scene != null) 
				{
					int scene_x = (def.sizeX * 4 - scene.imgWidth) / 2;
					int scene_y = (def.sizeY * 4 - scene.imgHeight) / 2;
					scene.drawBackground(48 + x * 4 + scene_x, 48 + (104 - y - def.sizeY) * 4 + scene_y);
				}
			} else 
			{
				if ((def.mapSceneID ^ 0xffffffff) == 0) 
				{
					if (type == 0 || type == 2)
						if (direction == 0) 
						{
							scene_pixels[pixel] = color;
							scene_pixels[pixel + 512] = color;
							scene_pixels[pixel + 1024] = color;
							scene_pixels[pixel + 1536] = color;
						} else if ((((direction ^ 0xffffffff) == -2) || direction == 1))
						{
							scene_pixels[pixel] = color;
							scene_pixels[pixel + 1] = color;
							scene_pixels[pixel + 2] = color;
							scene_pixels[pixel + 3] = color;
						} else if (direction == 2) 
						{
							scene_pixels[pixel + 3] = color;
							scene_pixels[pixel + 3 + 512] = color;
							scene_pixels[pixel + 3 + 1024] = color;
							scene_pixels[pixel + 3 + 1536] = color;
						} else if (direction == 3) 
						{
							scene_pixels[pixel + 1536] = color;
							scene_pixels[pixel + 1536 + 1] = color;
							scene_pixels[pixel + 1536 + 2] = color;
							scene_pixels[pixel + 1536 + 3] = color;
						}
					if(type == 3)
						if(direction == 0)
							scene_pixels[pixel] = color;

						else if(direction == 1)
							scene_pixels[pixel + 3] = color;

						else if(direction == 2)
							scene_pixels[pixel + 3 + 1536] = color;

						else if(direction == 3)
							scene_pixels[pixel + 1536] = color;

					if(type == 2)
						if(direction == 3) 
						{
							scene_pixels[pixel] = color;
							scene_pixels[pixel + 512] = color;
							scene_pixels[pixel + 1024] = color;
							scene_pixels[pixel + 1536] = color;
						} else if(direction == 0) 
						{
							scene_pixels[pixel] = color;
							scene_pixels[pixel + 1] = color;
							scene_pixels[pixel + 2] = color;
							scene_pixels[pixel + 3] = color;
						} else if(direction == 1) 
						{
							scene_pixels[pixel + 3] = color;
							scene_pixels[pixel + 3 + 512] = color;
							scene_pixels[pixel + 3 + 1024] = color;
							scene_pixels[pixel + 3 + 1536] = color;
						} else if(direction == 2) 
						{
							scene_pixels[pixel + 1536] = color;
							scene_pixels[pixel + 1536 + 1] = color;
							scene_pixels[pixel + 1536 + 2] = color;
							scene_pixels[pixel + 1536 + 3] = color;
						}
				}
			}
		}
		uid = worldController.getInteractableObjectUID(z, x, y);
		if(uid != 0) 
		{
			int resource_tag = worldController.getIDTagForXYZ(z, x, y, uid);
			int direction = resource_tag >> 6 & 3;
			int type = resource_tag & 0x1f;
			int object_id = worldController.fetchObjectMeshNewUID(z, x, y);
			ObjectDef def = ObjectDef.forID(object_id);
			if(def.mapSceneID != -1)
			{
				Background scene = mapScenes[def.mapSceneID];
				if(scene != null) 
				{
					int scene_x = (def.sizeX * 4 - scene.imgWidth) / 2;
					int scene_y = (def.sizeY * 4 - scene.imgHeight) / 2;
					scene.drawBackground(48 + x * 4 + scene_x, 48 + (104 - y - def.sizeY) * 4 + scene_y);
				}
			} else if(type == 9) 
			{
				int color = 0xeeeeee;
				if(uid > 0)
					color = 0xee0000;

				int scene_pixels[] = miniMap.myPixels;
				int pixel = 24624 + x * 4 + (103 - y) * 512 * 4;
				if(direction == 0 || direction == 2) 
				{
					scene_pixels[pixel + 1536] = color;
					scene_pixels[pixel + 1024 + 1] = color;
					scene_pixels[pixel + 512 + 2] = color;
					scene_pixels[pixel + 3] = color;
				} else 
				{
					scene_pixels[pixel] = color;
					scene_pixels[pixel + 512 + 1] = color;
					scene_pixels[pixel + 1024 + 2] = color;
					scene_pixels[pixel + 1536 + 3] = color;
				}
			}
		}
		uid = worldController.fetchGroundDecorationNewUID(z, x, y);
		if(uid > 0 || uid != 0) 
		{
			ObjectDef def = ObjectDef.forID(uid);
			if(def.mapSceneID != -1) 
			{
				Background scene = mapScenes[def.mapSceneID];
				if(scene != null) 
				{
					int scene_x = (def.sizeX * 4 - scene.imgWidth) / 2;
					int scene_y = (def.sizeY * 4 - scene.imgHeight) / 2;
					scene.drawBackground(48 + x * 4 + scene_x, 48 + (104 - y - def.sizeY) * 4 + scene_y);
				}
			}
		}
	}


	private static void setHighMem() {
		WorldController.lowMem = false;
		Rasterizer.lowMem = false;
		lowMem = false;
		ObjectManager.lowMem = false; //Removes roofs
		ObjectDef.lowMem = false;
	}

	public int canWalkDelay = 0;

	public int getDis(int coordX1, int coordY1, int coordX2, int coordY2) {
		int deltaX = coordX2 - coordX1;
		int deltaY = coordY2 - coordY1;
		return ((int) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));
	}

	public int random(int range) {
		return (int) (Math.random() * range);
	}

	public boolean withinDistance(int x1, int y1, int x2, int y2, int dis) {
		for (int i = 0; i <= dis; i++) {
			try {
				if ((x1 + i) == x2
						&& ((y1 + i) == y2 || (y1 - i) == y2 || y1 == y2))
					return true;
				else if ((x1 - i) == x2
						&& ((x1 + i) == y2 || (y1 - i) == y2 || y1 == y2))
					return true;
				else if (x1 == x2
						&& ((x1 + i) == y2 || (y1 - i) == y2 || y1 == y2))
					return true;
			} catch (Exception ex) {
				System.out
				.println("Exception in following, method : WithingDistance");
			}
		}
		return false;
	}

	public static void main(String args[]) {
		try {
			nodeID = 10;
			portOff = 0;
			setHighMem();
			isMembers = true;
			signlink.storeid = 32;
			signlink.startpriv(InetAddress.getLocalHost());
			clientSize = 0;
			instance = new Client();
			instance.createClientFrame(clientWidth, clientHeight);
		} catch (Exception exception) {
		}
	}

	public static Client instance;

	private int highestAmtToLoad = 0;

	boolean sentClientIsLoading = false;
	private void loadingStages() {
		try {
			if (lowMem && loadingStage == 2 && ObjectManager.anInt131 != plane) {
				gameScreenIP.initDrawingArea();
				SpriteCache.spriteCache[31].drawSprite(8, 9);
				int todo = onDemandFetcher.getRemaining();
				normalFont.drawRegularText(false, clientSize == 0 ? 14 : 10,
						0xFFFFFF, "Remaining:" + todo, clientSize == 0 ? 14
								: 10);

				gameScreenIP.drawGraphics(clientSize == 0 ? 4 : 0,
						super.graphics, clientSize == 0 ? 4 : 0);
				loadingStage = 1;
				mapLoadingTime = System.currentTimeMillis();
			}
			if (loadingStage == 1) {
				if(!sentClientIsLoading) {
					stream.createFrame(10);
					stream.writeDWord(1);
				}
				sentClientIsLoading = true;
				SpriteCache.spriteCache[31].drawSprite(8, 9);
				int todo = onDemandFetcher.getRemaining();
				if (todo > highestAmtToLoad)
					highestAmtToLoad = todo;
				double percentage = (((double) todo / (double) highestAmtToLoad) * 100D);
				normalFont.drawRegularText(false, 180 - 36, 0xc8c8c8, "("
						+ (100 - (int) percentage) + "%)", 30);

				gameScreenIP.drawGraphics(clientSize == 0 ? 4 : 0,
						super.graphics, clientSize == 0 ? 4 : 0);
				int j = getMapLoadingState();
				if (j != 0
						&& System.currentTimeMillis() - mapLoadingTime > 0x57e40L) {
					signlink.reporterror(myUsername + " glcfb " + aLong1215
							+ "," + j + "," + lowMem + ","
							+ cacheIndices[CONFIG_IDX] + ","
							+ onDemandFetcher.getRemaining() + "," + plane
							+ "," + currentRegionX + "," + currentRegionY);
					mapLoadingTime = System.currentTimeMillis();
				}
			}
			if (loadingStage == 2 && plane != lastKnownPlane) {
				lastKnownPlane = plane;
				renderedMapScene(plane);
				stream.createFrame(10);
				stream.writeDWord(0);
				sentClientIsLoading = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String objectMaps = "";
	private String floorMaps = "";

	private int getMapLoadingState() {
		if (!floorMaps.equals("") || !objectMaps.equals("")) {
			floorMaps = "";
			objectMaps = "";
		}
		for (int i = 0; i < terrainData.length; i++) {
			floorMaps += "  " + terrainIndices[i];
			objectMaps += "  " + objectIndices[i];
			if (terrainData[i] == null && terrainIndices[i] != -1)
				return -1;
			if (objectData[i] == null && objectIndices[i] != -1)
				return -2;
		}
		boolean flag = true;
		for (int j = 0; j < terrainData.length; j++) {
			byte obData[] = objectData[j];
			if (obData != null) {
				int k = (mapCoordinates[j] >> 8) * 64 - baseX;
				int l = (mapCoordinates[j] & 0xff) * 64 - baseY;
				if (requestMapReconstruct || requestMapReconstruct2) {
					k = 10;
					l = 10;
				}
				flag &= ObjectManager.method189(k, obData, l);
			}
		}
		if (!flag)
			return -3;
		if (loadingMap) {
			return -4;
		} else {
			loadingStage = 2;
			ObjectManager.anInt131 = plane;
			loadRegion();
			if (loggedIn)
				stream.createFrame(121);
			return 0;
		}
	}

	private void processProjectiles() {
		for (Projectile projectile = (Projectile) projectileDeque.getFront(); projectile != null; projectile = (Projectile) projectileDeque
				.getNext())
			if (projectile.plane != plane || loopCycle > projectile.speed)
				projectile.unlink();
			else if (loopCycle >= projectile.startTime) {
				if (projectile.lockOn > 0) {
					NPC npc = npcArray[projectile.lockOn - 1];
					if (npc != null && npc.x >= 0 && npc.x < 13312
							&& npc.y >= 0 && npc.y < 13312)
						projectile.calculateTracking(
								loopCycle,
								npc.y,
								getFloorDrawHeight(projectile.plane, npc.y,
										npc.x) - projectile.endHeight, npc.x);
				}
				if (projectile.lockOn < 0) {
					int j = -projectile.lockOn - 1;
					Player player;
					if (j == playerId)
						player = myPlayer;
					else
						player = playerArray[j];
					if (player != null && player.x >= 0 && player.x < 13312
							&& player.y >= 0 && player.y < 13312)
						projectile.calculateTracking(
								loopCycle,
								player.y,
								getFloorDrawHeight(projectile.plane, player.y,
										player.x) - projectile.endHeight,
										player.x);
				}
				projectile.processMovement(cycleTimer);
				worldController.addMutipleTileEntity(plane,
						projectile.rotationY,
						(int) projectile.currentPositionZ, -1,
						(int) projectile.currentPositionY, 60,
						(int) projectile.currentPositionX, projectile, false);
			}

	}

	public AppletContext getAppletContext() {
		if (signlink.mainapp != null)
			return signlink.mainapp.getAppletContext();
		else
			return super.getAppletContext();
	}

	public static final int ANIM_IDX = 2, AUDIO_IDX = 3, IMAGE_IDX = 5,
			TEXTURE_IDX = 6;

	public synchronized void processOnDemandQueue() {
		do {
			OnDemandRequest onDemandData;
			do {
				onDemandData = onDemandFetcher.getNextNode();
				if (onDemandData == null)
					return;
				/** Models Loading **/
				if (onDemandData.dataType == MODEL_IDX - 1) {
					Model.readFirstModelData(onDemandData.buffer,
							onDemandData.id);
					needDrawTabArea = true;

				}
				/** Animations Loading **/
				if (onDemandData.dataType == ANIM_IDX - 1) {
					FrameReader.load(onDemandData.id, onDemandData.buffer);
				}
				/** Sounds Loading **/
				if (onDemandData.dataType == AUDIO_IDX - 1
						&& onDemandData.id == nextSong
						&& onDemandData.buffer != null)
					saveMidi(songChanging, onDemandData.buffer);
				/** Maps Loading **/

				if (onDemandData.dataType == IMAGE_IDX - 1) {
					/*if (onDemandData.id == 681) {
						setCursor(onDemandData.buffer);
					}*/
					SpriteCache.spriteCache[onDemandData.id] = new Sprite(
							onDemandData.buffer);
				}
				if (onDemandData.dataType == TEXTURE_IDX - 1) {
					Texture.load(onDemandData.id, onDemandData.buffer);
				}
				if (onDemandData.dataType == MAP_IDX - 1 && loadingStage == 1) {

					for (int i = 0; i < terrainData.length; i++) {
						if (terrainIndices[i] == onDemandData.id) {
							terrainData[i] = onDemandData.buffer;
							if (onDemandData.buffer == null)
								terrainIndices[i] = -1;
							break;
						}
						if (objectIndices[i] != onDemandData.id)
							continue;
						objectData[i] = onDemandData.buffer;
						if (onDemandData.buffer == null)
							objectIndices[i] = -1;
						break;
					}

				}
			} while (onDemandData.dataType != 93
					|| !onDemandFetcher.mapIsObjectMap(onDemandData.id));
			ObjectManager.method173(new Stream(onDemandData.buffer),
					onDemandFetcher);
		} while (true);
	}

	private void resetInterfaceAnimation(int i) {
		RSInterface rsInterface = RSInterface.interfaceCache[i];
		if(rsInterface == null || rsInterface.children == null)
			return;
		for (int j = 0; j < rsInterface.children.length; j++) {
			if (rsInterface.children[j] == -1)
				break;
			RSInterface child = RSInterface.interfaceCache[rsInterface.children[j]];
			if (child.type == 1)
				resetInterfaceAnimation(child.id);
			child.currentFrame = 0;
			child.frameTimer = 0;
		}
	}

	private void doGEAction(int l) {
		if (l == 721) {
			inputTaken = true;
			amountOrNameInput = "";
			inputDialogState = 1;
			interfaceButtonAction = 1557;
		}
		if (l == 722) {
			inputTaken = true;
			amountOrNameInput = "";
			inputDialogState = 1;
			interfaceButtonAction = 1557;
		}
		if (l == 723) {
			inputTaken = true;
			amountOrNameInput = "";
			inputDialogState = 1;
			interfaceButtonAction = 1558;
		}
		if (l == 724) {
			inputTaken = true;
			amountOrNameInput = "";
			inputDialogState = 1;
			interfaceButtonAction = 1558;
		}
	}

	private void drawHeadIcon() {
		if (anInt855 != 2)
			return;
		calcEntityScreenPos((anInt934 - baseX << 7) + anInt937, anInt936 * 2,
				(anInt935 - baseY << 7) + anInt938);
		if (spriteDrawX > -1 && loopCycle % 20 < 10)
			headIconsHint[0].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
	}

	public int otherPlayerId = 0, otherPlayerX = 0, otherPlayerY = 0;
	static int lastPercent;

	private void mainGameProcessor() throws ClassNotFoundException,
	InstantiationException, IllegalAccessException,
	UnsupportedLookAndFeelException {
		if (openInterfaceID == 24600 && buttonclicked
				&& interfaceButtonAction != 1558
				&& interfaceButtonAction != 1557) {
			inputDialogState = 3;
		}
		if (openInterfaceID == 24600 && interfaceButtonAction == 1558
				|| openInterfaceID == 24600 && interfaceButtonAction == 1557) {
			inputDialogState = 1;
		}
		if (openInterfaceID == 24600 && !buttonclicked
				&& interfaceButtonAction != 1558
				&& interfaceButtonAction != 1557) {
			inputDialogState = 0;
		}
		if (updateMinutes > 1)
			updateMinutes--;
		if (anInt1011 > 0)
			anInt1011--;
		for (int j = 0; j < 5; j++)
			if (!parsePacket())
				break;

		if (!loggedIn)
			return;
		synchronized (mouseDetection.syncObject) {
			if (loggedIn && otherPlayerId > 0) {
				Player player = playerArray[otherPlayerId];
				int xCOORD = 0;
				int yCOORD = 0;
				boolean doStuff = false;
				if (playerArray[otherPlayerId] != null) {
					xCOORD = player.pathX[0] + (player.x - 6 >> 7);
					yCOORD = player.pathY[0] + (player.y - 6 >> 7);
					if (xCOORD == otherPlayerX && yCOORD == otherPlayerY)
						doStuff = true;
				}
				if (playerArray[otherPlayerId] != null && !doStuff) {
					doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
							player.pathY[0], myPlayer.pathX[0], false,
							player.pathX[0]);
				}
				if (playerArray[otherPlayerId] != null) {
					otherPlayerY = yCOORD;
					otherPlayerX = xCOORD;
				}
				if (playerArray[otherPlayerId] == null) {
					otherPlayerId = 0;
				}
			}
			if (flagged) {
				if (super.clickMode3 != 0 || mouseDetection.coordsIndex >= 40) {

					stream.createFrame(45);
					stream.writeWordBigEndian(0);
					int j2 = stream.currentOffset;
					int j3 = 0;
					for (int j4 = 0; j4 < mouseDetection.coordsIndex; j4++) {
						if (j2 - stream.currentOffset >= 240)
							break;
						j3++;
						int l4 = mouseDetection.coordsY[j4];
						if (l4 < 0)
							l4 = 0;
						else if (l4 > 502)
							l4 = 502;
						int k5 = mouseDetection.coordsX[j4];
						if (k5 < 0)
							k5 = 0;
						else if (k5 > 764)
							k5 = 764;
						int i6 = l4 * 765 + k5;
						if (mouseDetection.coordsY[j4] == -1
								&& mouseDetection.coordsX[j4] == -1) {
							k5 = -1;
							l4 = -1;
							i6 = 0x7ffff;
						}
						if (k5 == anInt1237 && l4 == anInt1238) {
							if (anInt1022 < 2047)
								anInt1022++;
						} else {
							int j6 = k5 - anInt1237;
							anInt1237 = k5;
							int k6 = l4 - anInt1238;
							anInt1238 = l4;
							if (anInt1022 < 8 && j6 >= -32 && j6 <= 31
									&& k6 >= -32 && k6 <= 31) {
								j6 += 32;
								k6 += 32;
								stream.writeWord((anInt1022 << 12) + (j6 << 6)
										+ k6);
								anInt1022 = 0;
							} else if (anInt1022 < 8) {
								stream.writeDWordBigEndian(0x800000
										+ (anInt1022 << 19) + i6);
								anInt1022 = 0;
							} else {
								stream.writeDWord(0xc0000000
										+ (anInt1022 << 19) + i6);
								anInt1022 = 0;
							}
						}
					}

					stream.writeBytes(stream.currentOffset - j2);
					if (j3 >= mouseDetection.coordsIndex) {
						mouseDetection.coordsIndex = 0;
					} else {
						mouseDetection.coordsIndex -= j3;
						for (int i5 = 0; i5 < mouseDetection.coordsIndex; i5++) {
							mouseDetection.coordsX[i5] = mouseDetection.coordsX[i5
							                                                    + j3];
							mouseDetection.coordsY[i5] = mouseDetection.coordsY[i5
							                                                    + j3];
						}

					}
				}
			} else {
				mouseDetection.coordsIndex = 0;
			}
		}
		if (super.clickMode3 != 0) {
			long l = (super.aLong29 - aLong1220) / 50L;
			if (l > 4095L)
				l = 4095L;
			aLong1220 = super.aLong29;
			int k2 = super.saveClickY;
			if (k2 < 0)
				k2 = 0;
			else if (k2 > 502)
				k2 = 502;
			int k3 = super.saveClickX;
			if (k3 < 0)
				k3 = 0;
			else if (k3 > 764)
				k3 = 764;
			int k4 = k2 * 765 + k3;
			int j5 = 0;
			if (super.clickMode3 == 2)
				j5 = 1;
			int l5 = (int) l;
			stream.createFrame(241);
			stream.writeDWord((l5 << 20) + (j5 << 19) + k4);
		}
		processShadow();
		if (anInt1016 > 0)
			anInt1016--;
		if (super.keyArray[1] == 1 || super.keyArray[2] == 1
				|| super.keyArray[3] == 1 || super.keyArray[4] == 1)
			aBoolean1017 = true;
		if (aBoolean1017 && anInt1016 <= 0) {
			anInt1016 = 20;
			aBoolean1017 = false;
			stream.createFrame(86);
			stream.writeWord(anInt1184);
			stream.writeUnsignedWordA(viewRotation);
		}
		if (super.awtFocus && !aBoolean954) {
			aBoolean954 = true;
			stream.createFrame(3);
			stream.writeWordBigEndian(1);
		}
		if (!super.awtFocus && aBoolean954) {
			aBoolean954 = false;
			stream.createFrame(3);
			stream.writeWordBigEndian(0);
		}
		loadingStages();
		updateSpawnedObjects();
		processRequestedAudio();
		anInt1009++;
		if (anInt1009 > 750)
			dropClient();
		updatePlayerInstances();
		readNPCUpdateBlockForced();
		processTextCycles();
		cycleTimer++;
		if (crossType != 0) {
			crossIndex += 20;
			if (crossIndex >= 400)
				crossType = 0;
		}
		if (atInventoryInterfaceType != 0) {
			atInventoryLoopCycle++;
			if (atInventoryLoopCycle >= 15) {
				if (atInventoryInterfaceType == 2)
					needDrawTabArea = true;
				if (atInventoryInterfaceType == 3)
					inputTaken = true;
				atInventoryInterfaceType = 0;
			}
		}
		if (activeInterfaceType != 0) {
			anInt989++;
			if (super.mouseX > anInt1087 + 5 || super.mouseX < anInt1087 - 5
					|| super.mouseY > anInt1088 + 5
					|| super.mouseY < anInt1088 - 5)
				aBoolean1242 = true;
			if (super.clickMode2 == 0) {
				if (activeInterfaceType == 2)
					needDrawTabArea = true;
				if (activeInterfaceType == 3)
					inputTaken = true;
				activeInterfaceType = 0;
				if (aBoolean1242 && anInt989 >= 10) {
					lastActiveInvInterface = -1;
					processRightClick();

					//BANK TAB DRAG
					//int x = clientSize > 0 ? (clientWidth / 2) - 765 : 0;
					//int y = clientSize == 0 ? 40 : (clientHeight / 2) - 503 + 40;
					//int x = 0; int y = clientSize == 0 ? 40 : clientHeight - (211 * 2);

					int x = clientSize == 0 ? 0 : (clientWidth / 2) - 256;
					int y = clientSize == 0 ? 40 : 40 + (clientHeight / 2) - 167;
					if(anInt1084 == 5382 && super.mouseY >= y && super.mouseY <= y+37){//check if bank interface
						if (super.mouseX >= 28+x && super.mouseX <= 74+x){// tab 1
							stream.createFrame(214);
							stream.method433(5);//5 = maintab
							stream.method424(0);
							stream.method433(anInt1085);//Selected item slot
							stream.method431(mouseInvInterfaceIndex);//unused

						}
						if (super.mouseX >= 75+x && super.mouseX <= 121+x){// tab 1
							stream.createFrame(214);
							stream.method433(13);//tab # x 13 (originally movewindow)
							stream.method424(0);
							stream.method433(anInt1085);//Selected item slot
							stream.method431(mouseInvInterfaceIndex);//unused

						}
						if (super.mouseX >= 122+x && super.mouseX <= 168+x){// tab 2
							stream.createFrame(214);
							stream.method433(26);//tab # x 13 (originally movewindow)
							stream.method424(0);
							stream.method433(anInt1085);//Selected item slot
							stream.method431(mouseInvInterfaceIndex);//unused

						}
						if (super.mouseX >= 169+x && super.mouseX <= 215+x){// tab 3
							stream.createFrame(214);
							stream.method433(39);//tab # x 13 (originally movewindow)
							stream.method424(0);
							stream.method433(anInt1085);//Selected item slot
							stream.method431(mouseInvInterfaceIndex);//unused

						}
						if (super.mouseX >= 216+x && super.mouseX <= 262+x){// tab 4
							stream.createFrame(214);
							stream.method433(52);//tab # x 13 (originally movewindow)
							stream.method424(0);
							stream.method433(anInt1085);//Selected item slot
							stream.method431(mouseInvInterfaceIndex);//unused

						}
						if (super.mouseX >= 263+x && super.mouseX <= 309+x){// tab 5
							stream.createFrame(214);
							stream.method433(65);//tab # x 13 (originally movewindow)
							stream.method424(0);
							stream.method433(anInt1085);//Selected item slot
							stream.method431(mouseInvInterfaceIndex);//unused

						}
						if (super.mouseX >= 310+x && super.mouseX <= 356+x){// tab 6
							stream.createFrame(214);
							stream.method433(78);//tab # x 13 (originally movewindow)
							stream.method424(0);
							stream.method433(anInt1085);//Selected item slot
							stream.method431(mouseInvInterfaceIndex);//unused

						}
						if (super.mouseX >= 357+x && super.mouseX <= 403+x){// tab 7
							stream.createFrame(214);
							stream.method433(91);//tab # x 13 (originally movewindow)
							stream.method424(0);
							stream.method433(anInt1085);//Selected item slot
							stream.method431(mouseInvInterfaceIndex);//unused

						}
						if (super.mouseX >= 404+x && super.mouseX <= 450+x){// tab 8
							stream.createFrame(214);
							stream.method433(104);//tab # x 13 (originally movewindow)
							stream.method424(0);
							stream.method433(anInt1085);//Selected item slot
							stream.method431(mouseInvInterfaceIndex);//unused

						}
					}
					if (lastActiveInvInterface == anInt1084
							&& mouseInvInterfaceIndex != anInt1085) {
						RSInterface class9 = RSInterface.interfaceCache[anInt1084];
						int j1 = 0;
						if (anInt913 == 1 && class9.contentType == 206)
							j1 = 1;
						if (class9.inv[mouseInvInterfaceIndex] <= 0)
							j1 = 0;
						if (class9.dragDeletes) {
							int l2 = anInt1085;
							int l3 = mouseInvInterfaceIndex;
							class9.inv[l3] = class9.inv[l2];
							class9.invStackSizes[l3] = class9.invStackSizes[l2];
							class9.inv[l2] = -1;
							class9.invStackSizes[l2] = 0;
						} else if (j1 == 1) {
							int i3 = anInt1085;
							for (int i4 = mouseInvInterfaceIndex; i3 != i4;)
								if (i3 > i4) {
									class9.swapInventoryItems(i3, i3 - 1);
									i3--;
								} else if (i3 < i4) {
									class9.swapInventoryItems(i3, i3 + 1);
									i3++;
								}

						} else {
							class9.swapInventoryItems(anInt1085,
									mouseInvInterfaceIndex);
						}
						stream.createFrame(214);
						stream.writeSignedBigEndian(anInt1084);
						stream.method424(j1);
						stream.writeSignedBigEndian(anInt1085);
						stream.writeUnsignedWordBigEndian(mouseInvInterfaceIndex);
					}
				} else if ((anInt1253 == 1 || menuHasAddFriend(menuActionRow - 1))
						&& menuActionRow > 2)
					determineMenuSize();
				else if (menuActionRow > 0)
					doAction(menuActionRow - 1);
				atInventoryLoopCycle = 10;
				super.clickMode3 = 0;
			}
		}
		if (WorldController.clickedTileX != -1) {
			int k = WorldController.clickedTileX;
			int k1 = WorldController.clickedTileY;

			boolean flag = doWalkTo(0, 0, 0, 0, myPlayer.pathY[0], 0, 0, k1,
					myPlayer.pathX[0], true, k);
			WorldController.clickedTileX = -1;
			if (flag) {
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 1;
				crossIndex = 0;
			}
		}
		if (super.clickMode3 == 1 && aString844 != null) {
			aString844 = null;
			inputTaken = true;
			super.clickMode3 = 0;
		}
		if (!processMenuClick()) {
			processMainScreenClick();
			processTabAreaClick();
			processChatModeClick();
			if (quickChat)
				processQuickChatArea();
			processMapAreaMouse();
		}/*
		if (super.clickMode3 == 1) {
			if (super.saveClickX >= 522 && super.saveClickX <= 558
					&& super.saveClickY >= 124 && super.saveClickY < 161) {
				worldMap[0] = !worldMap[0];
				// WorldMap.clientInstance = this;
				// wm = new WorldMap();

			}
		}*/
		if (super.clickMode2 == 1 || super.clickMode3 == 1)
			anInt1213++;
		if (anInt1500 != 0 || anInt1044 != 0 || anInt1129 != 0) {
			if (anInt1501 < 50 && !menuOpen) {
				anInt1501++;
				if (anInt1501 == 50) {
					if (anInt1500 != 0) {
						inputTaken = true;
					}
					if (anInt1044 != 0) {
						needDrawTabArea = true;
					}
				}
			}
		} else if (anInt1501 > 0) {
			anInt1501--;
		}
		if (loadingStage == 2)
			handleArrowKeys();
		if (loadingStage == 2 && inCutScene)
			calcCameraPos();
		for (int i1 = 0; i1 < 5; i1++)
			anIntArray1030[i1]++;

		manageTextInput();
		super.idleTime++;
		if (super.idleTime > 15000) {
			anInt1011 = 250;
			super.idleTime -= 500;
			stream.createFrame(202);
		}
		anInt988++;
		if (anInt988 > 500) {
			anInt988 = 0;
			int l1 = (int) (Math.random() * 8D);
			if ((l1 & 1) == 1)
				cameraOffsetX += anInt1279;
			if ((l1 & 2) == 2)
				cameraOffsetY += anInt1132;
			if ((l1 & 4) == 4)
				viewRotationOffset += anInt897;
		}
		if (cameraOffsetX < -50)
			anInt1279 = 2;
		if (cameraOffsetX > 50)
			anInt1279 = -2;
		if (cameraOffsetY < -55)
			anInt1132 = 2;
		if (cameraOffsetY > 55)
			anInt1132 = -2;
		if (viewRotationOffset < -40)
			anInt897 = 1;
		if (viewRotationOffset > 40)
			anInt897 = -1;
		anInt1254++;
		if (anInt1254 > 500) {
			anInt1254 = 0;
			int i2 = (int) (Math.random() * 8D);
			if ((i2 & 1) == 1)
				minimapRotation += anInt1210;
			if ((i2 & 2) == 2)
				minimapZoom += anInt1171;
		}
		if (minimapRotation < -60)
			anInt1210 = 2;
		if (minimapRotation > 60)
			anInt1210 = -2;
		if (minimapZoom < -20)
			anInt1171 = 1;
		if (minimapZoom > 10)
			anInt1171 = -1;
		anInt1010++;
		if (anInt1010 > 50)
			stream.createFrame(0);
		try {
			if (socketStream != null && stream.currentOffset > 0) {
				socketStream.queueBytes(stream.currentOffset, stream.buffer);
				stream.currentOffset = 0;
				anInt1010 = 0;
			}
		} catch (IOException _ex) {
			dropClient();
		} catch (Exception exception) {
			resetLogout();
		}
	}

	private void clearObjectSpawnRequests() {
		GameObjectSpawnRequest request = (GameObjectSpawnRequest) objectSpawnDeque
				.getFront();
		for (; request != null; request = (GameObjectSpawnRequest) objectSpawnDeque
				.getNext())
			if (request.removeTime == -1) {
				request.spawnTime = 0;
				assignOldValuesToNewRequest(request);
			} else {
				request.unlink();
			}

	}

	private void resetImageProducers() {
		if (GraphicsBuffer_1107 != null)
			return;
		super.fullGameScreen = null;
		chatAreaIP = null;
		mapAreaIP = null;
		tabAreaIP = null;
		gameScreenIP = null;
		GraphicsBuffer_1125 = null;
		GraphicsBuffer_1107 = new RSImageProducer(509, 171, getGameComponent());
		DrawingArea.resetImage();
		new RSImageProducer(360, 132, getGameComponent());
		DrawingArea.resetImage();
		titleScreen = new RSImageProducer(getClientWidth(), getClientHeight(),
				getGameComponent());
		DrawingArea.resetImage();
		new RSImageProducer(202, 238, getGameComponent());
		DrawingArea.resetImage();
		new RSImageProducer(203, 238, getGameComponent());
		DrawingArea.resetImage();
		new RSImageProducer(74, 94, getGameComponent());
		DrawingArea.resetImage();
		new RSImageProducer(75, 94, getGameComponent());
		DrawingArea.resetImage();
		// System.gc();
		welcomeScreenRaised = true;
	}

	void drawLoadingText(int i, String s) {
		if(!isLoading)
			return;
		try {
			lastPercent = i;
			aString1049 = s;
			resetImageProducers();
			if(titleStreamLoader == null) { //Cache doesn't exist, downloading it..
				super.drawLoadingText(i, s);
				return;
			}
			int addition = 200;
			titleScreen.initDrawingArea();
			/*
			if(normalFont != null)
				normalFont.drawText(0xffffff, "Voxyde V"+Configuration.CLIENT_VERSION+" is loading - please wait...", 200, 200);*/
			int j = 470; // 152
			DrawingArea.fillPixels(-1, 767, 34, 0x302e2c, j);
			DrawingArea.fillPixels(-1, 767, 32, 0, j + 1);
			DrawingArea.drawPixels(30, j + 2, 0, 0x302e2c, 767);
			DrawingArea.drawPixels(30, j + 2, 0 + i, 0, 767);

			if(normalFont != null) {
				if(i != 10)
					s = "Starting Voxyde - "+i/10+"%";
				normalFont.drawText(0xffffff, s, j + 22, 384);
			}
			
			//titleScreen.drawGraphics(171, super.graphics, 202);
			titleScreen.drawGraphics(0, super.graphics, 0);
			if(welcomeScreenRaised)
				welcomeScreenRaised = false;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	public DrawingArea Dinstance = new DrawingArea();

	public DrawingArea getDraw() {
		return Dinstance;
	}

	private void resetImage() {
		DrawingArea.resetImage();
	}

	private void scrollInterface(int maxWidth, int height, int k, int l,
			RSInterface class9, int i1, boolean flag, int j1) {
		int anInt992;
		if (aBoolean972)
			anInt992 = 32;
		else
			anInt992 = 0;
		aBoolean972 = false;
		if (k >= maxWidth && k < maxWidth + 16 && l >= i1 && l < i1 + 16) {
			class9.scrollPosition -= anInt1213 * 4;
			if (flag) {
				needDrawTabArea = true;
			}
		} else if (k >= maxWidth && k < maxWidth + 16
				&& l >= (i1 + height) - 16 && l < i1 + height) {
			class9.scrollPosition += anInt1213 * 4;
			if (flag) {
				needDrawTabArea = true;
			}
		} else if (k >= maxWidth - anInt992 && k < maxWidth + 16 + anInt992
				&& l >= i1 + 16 && l < (i1 + height) - 16 && anInt1213 > 0) {
			int l1 = ((height - 32) * height) / j1;
			if (l1 < 8)
				l1 = 8;
			int i2 = l - i1 - 16 - l1 / 2;
			int j2 = height - 32 - l1;
			class9.scrollPosition = ((j1 - height) * i2) / j2;
			if (flag)
				needDrawTabArea = true;
			aBoolean972 = true;
		}
	}

	private boolean reachedClickedObject(int uid, int tileY, int tileX, int id) {
		int objectBits = worldController.getIDTagForXYZ(plane, tileX, tileY,
				uid);
		if (uid == -1)
			return false;
		int objectType = objectBits & 0x1f;
		int objectRotation = objectBits >> 6 & 3;
			if (objectType == 10 || objectType == 11 || objectType == 22) {
				ObjectDef objectDef = ObjectDef.forID(id);
				int i2;
				int j2;
				if (objectRotation == 0 || objectRotation == 2) {
					i2 = objectDef.sizeX;
					j2 = objectDef.sizeY;
				} else {
					i2 = objectDef.sizeY;
					j2 = objectDef.sizeX;
				}
				int reqPlane = objectDef.plane;
				if (objectRotation != 0)
					reqPlane = (reqPlane << objectRotation & 0xf)
					+ (reqPlane >> 4 - objectRotation);
				doWalkTo(2, 0, j2, 0, myPlayer.pathY[0], i2, reqPlane, tileY,
						myPlayer.pathX[0], false, tileX);
			} else {
				doWalkTo(2, objectRotation, 0, objectType + 1, myPlayer.pathY[0],
						0, 0, tileY, myPlayer.pathX[0], false, tileX);
			}
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			return true;
	}

	public final CRC32 aCRC32_930 = new CRC32();

	private CacheArchive streamLoaderForName(int i, String s, String s1, int j, int k)
	{
		byte abyte0[] = null;
		int l = 5;
		try
		{
			if(cacheIndices[0] != null)
				abyte0 = cacheIndices[0].get(i);
		}
		catch(Exception _ex) { }
		if(abyte0 != null)
		{
			//		aCRC32_930.reset();
			//		aCRC32_930.update(abyte0);
			//		int i1 = (int)aCRC32_930.getValue();
			//		if(i1 != j)
		}
		if(abyte0 != null)
		{
			CacheArchive streamLoader = new CacheArchive(abyte0);
			return streamLoader;
		}
		int j1 = 0;
		while(abyte0 == null)
		{
			String s2 = "Unknown error";
			drawLoadingText(k, "Requesting " + s);
			Object obj = null;
			try
			{
				int k1 = 0;
				DataInputStream datainputstream = openJagGrabInputStream(s1 + j);
				byte abyte1[] = new byte[6];
				datainputstream.readFully(abyte1, 0, 6);
				Stream stream = new Stream(abyte1);
				stream.currentOffset = 3;
				int i2 = stream.read3Bytes() + 6;
				int j2 = 6;
				abyte0 = new byte[i2];
				System.arraycopy(abyte1, 0, abyte0, 0, 6);

				while(j2 < i2) 
				{
					int l2 = i2 - j2;
					if(l2 > 1000)
						l2 = 1000;
					int j3 = datainputstream.read(abyte0, j2, l2);
					if(j3 < 0)
					{
						s2 = "Length error: " + j2 + "/" + i2;
						throw new IOException("EOF");
					}
					j2 += j3;
					int k3 = (j2 * 100) / i2;
					if(k3 != k1)
						drawLoadingText(k, "Loading " + s + " - " + k3 + "%");
					k1 = k3;
				}
				datainputstream.close();
				try
				{
					if(cacheIndices[0] != null)
						cacheIndices[0].put(abyte0.length, abyte0, i);
				}
				catch(Exception _ex)
				{
					cacheIndices[0] = null;
				}
				/*			 if(abyte0 != null)
				{
					aCRC32_930.reset();
					aCRC32_930.update(abyte0);
					int i3 = (int)aCRC32_930.getValue();
					if(i3 != j)
					{
						abyte0 = null;
						j1++;
						s2 = "Checksum error: " + i3;
					}
				}
				 */
			}
			catch(IOException ioexception)
			{
				if(s2.equals("Unknown error"))
					s2 = "Connection error";
				abyte0 = null;
			}
			catch(NullPointerException _ex)
			{
				s2 = "Null error";
				abyte0 = null;
				if(!signlink.reporterror)
					return null;
			}
			catch(ArrayIndexOutOfBoundsException _ex)
			{
				s2 = "Bounds error";
				abyte0 = null;
				if(!signlink.reporterror)
					return null;
			}
			catch(Exception _ex)
			{
				s2 = "Unexpected error";
				abyte0 = null;
				if(!signlink.reporterror)
					return null;
			}
			if(abyte0 == null)
			{
				for(int l1 = l; l1 > 0; l1--)
				{
					if(j1 >= 3)
					{
						drawLoadingText(k, "Game updated - please reload page");
						l1 = 10;
					} else
					{
						drawLoadingText(k, s2 + " - Retrying in " + l1);
					}
					try
					{
						Thread.sleep(1000L);
					}
					catch(Exception _ex) { }
				}

				l *= 2;
				if(l > 60)
					l = 60;
			}

		}

		CacheArchive streamLoader_1 = new CacheArchive(abyte0);
		return streamLoader_1;
	}
	private void dropClient() throws ClassNotFoundException,
	InstantiationException, IllegalAccessException,
	UnsupportedLookAndFeelException {
		if (anInt1011 > 0) {
			resetLogout();
			return;
		}
		gameScreenIP.initDrawingArea();
		SpriteCache.spriteCache[32].drawSprite(7, 4);
		gameScreenIP.drawGraphics(clientSize == 0 ? 4 : 0, super.graphics,
				clientSize == 0 ? 4 : 0);
		minimapStatus = 0;
		destX = 0;
		RSSocket rsSocket = socketStream;
		loggedIn = false;
		loginFailures = 0;
		login(myUsername, myPassword, true);
		if (!loggedIn)
			resetLogout();
		try {
			rsSocket.close();
		} catch (Exception _ex) {
		}
	}

	public boolean autoCast = false;
	public int autocastId = 0;

	private void setAutoCastOff() {
		autoCast = false;
		autocastId = 0;
		sendPacket185(6667);
	}


	boolean dungInvite = false;

	private void doAction(int i) {
		if (i < 0)
			return;
		if (inputDialogState != 0 && inputDialogState != 3) {
			inputDialogState = 0;
			inputTaken = true;
		}
		if (type2 > -1 && clickInRegion(drawX+40, drawY, drawX+80, drawY+106)) {
			type2 = -1;
			return;
		}
		int slot = menuActionCmd2[i];
		int interfaceId = menuActionCmd3[i];
		int cmd4 = menuActionCmd4[i];
		int l = menuActionID[i];
		int nodeId = menuActionCmd1[i];
		int x = slot;
		int y = interfaceId;
		int id = (nodeId > 32767 ? cmd4 : nodeId >> 14 & 0x7fff);
		if(openInterfaceID == 21172 && super.saveClickX > 182 && super.saveClickX < 342 &&
				super.saveClickY > 30 && super.saveClickY < 321) {
			rollingCharacter = !rollingCharacter;
			return;
		}
		if(l == 72667) {
			coinToggle = !coinToggle;
			return;
		} else if(l == 72668) {
			sendPacket185(27653);
			return;
		} else if(l == 72669) {
			String cash = getMoneyInPouch();
			pushMessage("Your money pouch currently contains "+cash+" ("+Integer.parseInt(RSInterface.interfaceCache[8135].message)+") coins.", 0, "");
			return;
		} else if(l == 72670) {
			sendPacket185(12);
			return;
		} else if(l == 72666) { //MP
			if(openInterfaceID > 0 && openInterfaceID != 3323 && openInterfaceID != 6575) {
				pushMessage("Please close the interface you have open before opening another one.", 0, "");
				return;
			}
			inputTitle = "Enter amount of coins to withdraw:";
			interfaceButtonAction = 557;
			showInput = false;
			inputDialogState = 1;
			amountOrNameInput = "";
			inputTaken = true;
			withdrawingMoneyFromPouch = true;
			return;
		} else
			withdrawingMoneyFromPouch = false;
		if(l >= 990 && l <= 992) {
			stream.createFrame(8);
			stream.writeDWord(l);
			inputString = "";
			privateChatMode = l - 990;
			return;
		}
		if(interfaceId == 29329) {
			/*if(openInterfaceID > 0) {
				pushMessage("Please close the interface you have open before opening another one.", 0, "");
				return;
			}
			inputTaken = true;
			inputDialogState = 0;
			showInput = true;
			promptInput = "";
			friendsListAction = 6;
			dungInvite = false;
			promptMessage = "Enter the name of the chat you wish to join";*/
			sendPacket185(29329);
			return;
		}
		if(interfaceId == 26250) {
			if(openInterfaceID > 0) {
				pushMessage("Please close the interface you have open before opening another one.", 0, "");
				return;
			}
			inputTaken = true;
			inputDialogState = 0;
			showInput = true;
			promptInput = "";
			friendsListAction = 6;
			dungInvite = true;
			promptMessage = "Enter the name of the player to invite";
			return;
		}
		if(interfaceId == 10008) {
			if(playerReporting == null || playerReporting.length() < 1 || playerReporting.equalsIgnoreCase(myUsername)) {
				pushMessage("Please enter a valid player to report.", 0, "");
				return;
			}
			if(reasonForReport == null || reasonForReport.length() < 4) {
				pushMessage("Please enter a valid reason for this report.", 0, "");
				return;
			}
			String reportString = new StringBuilder().append(playerReporting).append("''").append(reasonForReport).toString();
			stream.createFrame(13);
			stream.writeWordBigEndian(reportString.length() +1);
			stream.writeString(reportString);
			return;
		} else if(l == 76390) {
			if(openInterfaceID == 3323) {
				if(itemToLend != -1){
					stream.createFrame(9);
					stream.writeDWord(itemToLend);
					itemToLend = -1;
					return;
				}
			}
			return;
		}
		if(l == 1013) {
			totalXP = 0;
			return;
		} else if (l == 1045) {// Toggle quick prayers / curses
			if (openInterfaceID != -1)
				pushMessage("Please close the open interface first.", 0, "");
			else {
				if ((currentStats[5] / 10) > 0)
					handleQuickAidsActive();
				else
					pushMessage(
							"You need to recharge your Prayer points at an altar.", 0,
							"");
				return;
			}
			return;
		}
		else if (l == 1046) {// Select quick prayers / curses
			toggleQuickAidsSelection();
			return;
		}
		if(l == 10001 || l == 10002) {
			sendPacket185(l);
			return;
		}
		if (l >= 2000)
			l -= 2000;
		if (l == 104) {
			RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
			spellID = interfaceId;
			if (!autoCast || (autocastId != spellID)) {
				autoCast = true;
				autocastId = spellID;
				sendPacket185(autocastId, -1, 135);
				//pushMessage("Autocast spell selected.", 0, "");
			} else if (autocastId == spellID) {
				setAutoCastOff();
			}
		}
		if (l == 1047) {// Toggle run mode
			if(currentEnergy <= 0) {
				pushMessage("You don't have any energy left.", 0, "");
				return;
			}
			sendPacket185(152, 173, 169);
			return;
		}
		if (l == 1048) {// Rest
			sendPacket185(1036);
			return;
		}
		if (l == 1121) {// Renew Familiar
			sendPacket185(l - 100);
			return;
		}
		if (l == 1120) {// Tale BoB
			sendPacket185(l - 100);
			return;
		}
		if (l == 1119) {// Dismiss
			sendPacket185(l - 100);
			return;
		}
		if (l == 1118) {// Call Follower
			sendPacket185(l - 100);
			return;
		}
		if (l == 696) {
			viewRotation = 0;
			anInt1184 = 120;

		}
		if (l == 1251) {
			buttonclicked = false;
			//inputString = "[A]" + GEItemId;
			sendPacket(1003);
		}
		if (l == 1007) {
			canGainXP = canGainXP ? false : true;
		}
		if (l == 1006 && !showBonus) {
			if (!gains.isEmpty()) {
				gains.removeAll(gains);
			}
			showXP = showXP ? false : true;
		}
		if (l == 1030 && !showXP) {
			showBonus = showBonus ? false : true;
		}
		if (l == 1005) {
			openQuickChat();
		}
		if (l == 1004) {
			quickChat = false;
			canTalk = true;
			inputTaken = true;
		}
		if (l == 1014) {
			running = !running;
			sendPacket185(19158);
		}
		if (l == 1016) {
			resting = !resting;
			sendPacket185(26500);
		}
		if (l == 1076) {
			if (menuOpen) {
				needDrawTabArea = true;
				tabID = tabHover;
				tabAreaAltered = true;
			}
		}
		if (l == 1026) {// Cast Special Attack
			if (choosingLeftClick) {
				leftClick = 7;
				choosingLeftClick = false;
			} else
				sendPacket185(15660);
		}
		if (l == 1025) {
			if (choosingLeftClick) {
				leftClick = -1;
				choosingLeftClick = false;
			} else {
				leftClick = -1;
				choosingLeftClick = true;
			}
		}
		if (l == 1024) {// Follower Details
			if (choosingLeftClick) {
				leftClick = 6;
				choosingLeftClick = false;
			} else
				sendPacket185(15661);
		}
		if (l == 1023) {// Attack
			if (choosingLeftClick) {
				leftClick = 5;
				choosingLeftClick = false;
			} else
				sendPacket185(15662);
		}
		if (l == 1022) {// Interact
			if (choosingLeftClick) {
				leftClick = 4;
				choosingLeftClick = false;
			} else
				sendPacket185(15663);
		}
		if (l == 1021) {// Renew Familiar
			if (choosingLeftClick) {
				leftClick = 3;
				choosingLeftClick = false;
			} else
				sendPacket185(15664);
		}
		if (l == 1020) {// Tale BoB
			if (choosingLeftClick) {
				leftClick = 2;
				choosingLeftClick = false;
			} else
				sendPacket185(15665);
		}
		if (l == 1019) {// Dismiss
			if (choosingLeftClick) {
				leftClick = 1;
				choosingLeftClick = false;
			} else
				sendPacket185(15666);
		}
		if (l == 1027) {// Dismiss
			leftClick = -1;
			choosingLeftClick = true;
		}
		if (l == 1018) {// Call Follower
			if (choosingLeftClick) {
				leftClick = 0;
				choosingLeftClick = false;
			} else
				sendPacket185(15667);
		}
		if (l == 13003) {
			stream.createFrame(185);
			stream.writeWord(menuActionName[i].contains("Cast") ? 15004 : 15003);
		}
		if (l == 13004) {
			stream.createFrame(185);
			stream.writeWord(15005);
		}
		if (l == 13005) {
			stream.createFrame(185);
			stream.writeWord(15006);
		}
		if (l == 13006) {
			stream.createFrame(185);
			stream.writeWord(15007);
		}
		if (l == 13007) {
			stream.createFrame(185);
			stream.writeWord(15008);
		}
		if (l == 582) {
			NPC npc = npcArray[nodeId];
			if (npc != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0, npc.pathY[0],
						myPlayer.pathX[0], false, npc.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(57);
				stream.writeUnsignedWordA(selectedItemId);
				stream.writeUnsignedWordA(nodeId);
				stream.writeUnsignedWordBigEndian(lastItemSelectedSlot);
				stream.writeUnsignedWordA(lastItemSelectedInterface);
			}
		}
		if (l == 234) {
			boolean flag1 = doWalkTo(2, 0, 0, 0, myPlayer.pathY[0], 0, 0,
					interfaceId, myPlayer.pathX[0], false, slot);
			if (!flag1)
				flag1 = doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						interfaceId, myPlayer.pathX[0], false, slot);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(236);
			stream.writeUnsignedWordBigEndian(interfaceId + baseY);
			stream.writeWord(nodeId);
			stream.writeUnsignedWordBigEndian(slot + baseX);
		}
		if (l == 62 && reachedClickedObject(nodeId, y, x, id)) {
			stream.createFrame(192);
			stream.writeWord(lastItemSelectedInterface);
			stream.writeWord(id);
			stream.writeSignedBigEndian(y + baseY);
			stream.writeUnsignedWordBigEndian(lastItemSelectedSlot);
			stream.writeSignedBigEndian(x + baseX);
			stream.writeWord(selectedItemId);
		}
		if (l == 511) {
			boolean flag2 = doWalkTo(2, 0, 0, 0, myPlayer.pathY[0], 0, 0,
					interfaceId, myPlayer.pathX[0], false, slot);
			if (!flag2)
				flag2 = doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						interfaceId, myPlayer.pathX[0], false, slot);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(25);
			stream.writeUnsignedWordBigEndian(lastItemSelectedInterface);
			stream.writeUnsignedWordA(selectedItemId);
			stream.writeWord(nodeId);
			stream.writeUnsignedWordA(interfaceId + baseY);
			stream.writeSignedBigEndian(lastItemSelectedSlot);
			stream.writeWord(slot + baseX);
		}
		if (l == 74) {
			stream.createFrame(122);
			stream.writeWord(interfaceId);
			stream.writeWord(slot);
			stream.writeWord(nodeId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 315) {
			RSInterface class9 = RSInterface.interfaceCache[interfaceId];
			boolean flag8 = true;
			if (class9.contentType > 0)
				flag8 = promptUserForInput(class9);
			if (flag8) {

				switch (interfaceId) {
				case 17231://Quick prayr confirm
					saveQuickSelection();
					break;
				case 19144:
					sendFrame248(15106, 3213);
					resetInterfaceAnimation(15106);
					inputTaken = true;
					break;
				default:
					if(interfaceId >= 17202 && interfaceId <= 17227) {
						try {
							togglePrayerState(interfaceId);
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						stream.createFrame(185);
						stream.writeWord(interfaceId);
					}
					break;

				}
			}
		}
		switch (l) {
		}
		if (l == 561) {
			Player player = playerArray[nodeId];
			if (player != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0, player.pathY[0],
						myPlayer.pathX[0], false, player.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt1188 += nodeId;
				if (anInt1188 >= 90) {
					stream.createFrame(136);
					anInt1188 = 0;
				}
				stream.createFrame(128);
				stream.writeWord(nodeId);
			}
		}
		if (l == 20) {
			NPC npc = npcArray[nodeId];
			if (npc != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0, npc.pathY[0],
						myPlayer.pathX[0], false, npc.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(155);
				stream.writeUnsignedWordBigEndian(nodeId);
			}
		}
		if (l == 779) {
			Player plr = playerArray[nodeId];
			if (plr != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0, plr.pathY[0],
						myPlayer.pathX[0], false, plr.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(153);
				stream.writeUnsignedWordBigEndian(nodeId);
			}
		}
		if (l == 516) {
			if (!menuOpen)
				worldController.request2DTrace(super.saveClickY - 4,
						super.saveClickX - 4);
			else
				worldController.request2DTrace(interfaceId - 4, slot - 4);
		}
		if (l == 1062) {
			anInt924 += baseX;
			if (anInt924 >= 113) {
				stream.createFrame(183);
				stream.writeDWordBigEndian(0xe63271);
				anInt924 = 0;
			}
			reachedClickedObject(nodeId, y, x, id);
			stream.createFrame(228);
			stream.writeUnsignedWordA(id);
			stream.writeUnsignedWordA(y + baseY);
			stream.writeWord(x + baseX);
		}
		if (l == 679 && !aBoolean1149) {
			stream.createFrame(40);
			stream.writeWord(interfaceId);
			aBoolean1149 = true;
		}
		if (l == 431) {
			stream.createFrame(129);
			stream.writeUnsignedWordA(slot);
			stream.writeWord(interfaceId);
			stream.writeUnsignedWordA(nodeId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 337 || l == 42 || l == 792 || l == 322) {
			String s = menuActionName[i];
			int k1 = s.indexOf("@whi@");
			if (k1 != -1) {
				try {
					String name = s.substring(k1 + 5);
					long l3 = TextClass.longForName(s.substring(
							k1 + 5 + (name.indexOf("@") == 0 ? 5 : 0)).trim());
					if (l == 337)
						addFriend(l3);
					if (l == 42)
						addIgnore(l3);
					if (l == 792)
						delFriend(l3);
					if (l == 322)
						delIgnore(l3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (l == 1315) {
			inputString = "[NOT]" + clanName;
			sendPacket(1003);
		}
		if (l == 1316) {
			inputString = "[REC]" + clanName;
			sendPacket(1003);
		}
		if (l == 1317) {
			inputString = "[COR]" + clanName;
			sendPacket(1003);
		}
		if (l == 1318) {
			inputString = "[SER]" + clanName;
			sendPacket(1003);
		}
		if (l == 1319) {
			inputString = "[LIE]" + clanName;
			sendPacket(1003);
		}
		if (l == 1320) {
			inputString = "[CAP]" + clanName;
			sendPacket(1003);
		}
		if (l == 1321) {
			inputString = "[GEN]" + clanName;
			sendPacket(1003);
		}
		if (l == 53) {
			stream.createFrame(135);
			stream.writeUnsignedWordBigEndian(slot);
			stream.writeUnsignedWordA(interfaceId);
			stream.writeUnsignedWordBigEndian(nodeId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 54) {
			stream.createFrame(135);
			stream.writeUnsignedWordBigEndian(slot);
			stream.writeUnsignedWordA(11);
			stream.writeUnsignedWordBigEndian(nodeId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 539) {
			stream.createFrame(16);
			stream.writeUnsignedWordA(nodeId);
			stream.writeSignedBigEndian(slot);
			stream.writeSignedBigEndian(interfaceId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 484 || l == 6) {
			String s1 = menuActionName[i];
			int l1 = s1.indexOf("@whi@");
			if (l1 != -1) {
				s1 = s1.substring(l1 + 5).trim();
				String s7 = TextClass.fixName(TextClass.nameForLong(TextClass
						.longForName(s1)));
				boolean flag9 = false;
				for (int j3 = 0; j3 < playerCount; j3++) {
					Player class30_sub2_sub4_sub1_sub2_7 = playerArray[playerIndices[j3]];
					if (class30_sub2_sub4_sub1_sub2_7 == null
							|| class30_sub2_sub4_sub1_sub2_7.name == null
							|| !class30_sub2_sub4_sub1_sub2_7.name
							.equalsIgnoreCase(s7))
						continue;
					doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
							class30_sub2_sub4_sub1_sub2_7.pathY[0],
							myPlayer.pathX[0], false,
							class30_sub2_sub4_sub1_sub2_7.pathX[0]);
					if (l == 484) {
						stream.createFrame(139);
						stream.writeUnsignedWordBigEndian(playerIndices[j3]);
					}
					if (l == 6) {
						anInt1188 += nodeId;
						if (anInt1188 >= 90) {
							stream.createFrame(136);
							anInt1188 = 0;
						}
						stream.createFrame(128);
						stream.writeWord(playerIndices[j3]);
					}
					flag9 = true;
					break;
				}

				if (!flag9)
					pushMessage("Unable to find " + s7+".", 0, "");
			}
		}
		if (l == 870) {
			stream.createFrame(53);
			stream.writeWord(slot);
			stream.writeUnsignedWordA(lastItemSelectedSlot);
			stream.writeSignedBigEndian(nodeId);
			stream.writeWord(lastItemSelectedInterface);
			stream.writeUnsignedWordBigEndian(selectedItemId);
			stream.writeWord(interfaceId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		switch (interfaceId) {
		case 40049:
			toggleSize(2);
			break;
		case 40046:
			toggleSize(1);
			break;
		case 40043:
			toggleSize(0);
			break;
		case 40039:
			clearTopInterfaces();
			break;

		}
		if (l == 847) {
			stream.createFrame(87);
			stream.writeUnsignedWordA(nodeId);
			stream.writeWord(interfaceId);
			stream.writeUnsignedWordA(slot);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 626) {
			RSInterface class9_1 = RSInterface.interfaceCache[interfaceId];
			int childId = interfaceId;
			spellSelected = 1;
			spellUsableOn = Integer.parseInt(MagicInterfaces.getSpellData(childId, "spellUsableOn"));
			itemSelected = 0;
			needDrawTabArea = true;
			spellID = childId;
			String s4 = MagicInterfaces.getSpellData(childId, "selectedActionName");
			if (s4.indexOf(" ") != -1)
				s4 = s4.substring(0, s4.indexOf(" "));
			String s8 = MagicInterfaces.getSpellData(childId, "selectedActionName");
			if (s8.indexOf(" ") != -1)
				s8 = s8.substring(s8.indexOf(" ") + 1);
			spellTooltip = s4 + " " + MagicInterfaces.getSpellData(childId, "spellname") + " " + s8;
			if (spellUsableOn == 16) {
				tabID = 3;
				tabAreaAltered = true;
			}
			selectedSpellId = spellID;
			return;
		}
		if (l == 78) {
			stream.createFrame(117);
			stream.writeSignedBigEndian(interfaceId);
			stream.writeSignedBigEndian(nodeId);
			stream.writeUnsignedWordBigEndian(slot);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 27) {
			Player class30_sub2_sub4_sub1_sub2_2 = playerArray[nodeId];
			if (class30_sub2_sub4_sub1_sub2_2 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						class30_sub2_sub4_sub1_sub2_2.pathY[0],
						myPlayer.pathX[0], false,
						class30_sub2_sub4_sub1_sub2_2.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt986 += nodeId;
				if (anInt986 >= 54) {
					stream.createFrame(189);
					stream.writeWordBigEndian(234);
					anInt986 = 0;
				}
				stream.createFrame(73);
				stream.writeUnsignedWordBigEndian(nodeId);
			}
		}
		if (l == 213) {
			boolean flag3 = doWalkTo(2, 0, 0, 0, myPlayer.pathY[0], 0, 0,
					interfaceId, myPlayer.pathX[0], false, slot);
			if (!flag3)
				flag3 = doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						interfaceId, myPlayer.pathX[0], false, slot);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(79);
			stream.writeUnsignedWordBigEndian(interfaceId + baseY);
			stream.writeWord(nodeId);
			stream.writeUnsignedWordA(slot + baseX);
		}
		if (l == 632) {
			stream.createFrame(145);
			stream.writeUnsignedWordA(interfaceId);
			stream.writeUnsignedWordA(slot);
			stream.writeUnsignedWordA(nodeId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 888) {
			inputString = "[BS1]";
			sendPacket(1003);
		}
		if (l == 889) {
			inputString = "[BS2]";
			sendPacket(1003);
		}
		if (l == 890) {
			inputString = "[BB1]";
			sendPacket(1003);
		}
		if (l == 891) {
			inputString = "[BB2]";
			sendPacket(1003);
		}

		if (l == 1004) {
			if (tabInterfaceIDs[10] != -1) {
				needDrawTabArea = true;
				tabID = 10;
				tabAreaAltered = true;
			}
		}
		if (l == 1003) {
			clanChatMode = 2;
			inputTaken = true;
		}
		if (l == 1002) {
			clanChatMode = 1;
			inputTaken = true;
		}
		if (l == 1001) {
			clanChatMode = 0;
			inputTaken = true;
		}
		if (l == 1000) {
			cButtonCPos = 4;
			chatTypeView = 11;
			inputTaken = true;
		}
		if (l == 999) {
			cButtonCPos = 0;
			chatTypeView = 0;
			inputTaken = true;
		}
		if (l == 998) {
			cButtonCPos = 1;
			chatTypeView = 5;
			inputTaken = true;
		}
		if (l == 997) {
			publicChatMode = 3;
			inputTaken = true;
		}
		if (l == 996) {
			publicChatMode = 2;
			inputTaken = true;
		}
		if (l == 995) {
			publicChatMode = 1;
			inputTaken = true;
		}
		if (l == 994) {
			publicChatMode = 0;
			inputTaken = true;
		}
		if (l == 993) {
			cButtonCPos = 2;
			chatTypeView = 1;
			inputTaken = true;
		}
		if (l == 992) {
			privateChatMode = 2;
			inputTaken = true;
		}
		if (l == 991) {
			privateChatMode = 1;
			inputTaken = true;
		}
		if (l == 990) {
			privateChatMode = 0;
			inputTaken = true;
		}
		if (l == 989) {
			cButtonCPos = 3;
			chatTypeView = 2;
			inputTaken = true;
		}
		if (l == 987) {
			tradeMode = 2;
			inputTaken = true;
		}
		if (l == 986) {
			tradeMode = 1;
			inputTaken = true;
		}
		if (l == 985) {
			tradeMode = 0;
			inputTaken = true;
		}
		if (l == 984) {
			cButtonCPos = 5;
			chatTypeView = 3;
			inputTaken = true;
		}
		if (l == 983) {
			duelMode = 2;
			inputTaken = true;
		}
		if (l == 982) {
			duelMode = 1;
			inputTaken = true;
		}
		if (l == 981) {
			duelMode = 0;
			inputTaken = true;
		}
		if (l == 980) {
			cButtonCPos = 6;
			chatTypeView = 4;
			inputTaken = true;
		}
		if (l == 798) {
			gameChatMode = 4;
			inputTaken = true;
			filterMessages = true;
			inputTaken = true;
		}
		if (l == 797) {
			gameChatMode = 5;
			inputTaken = true;
			filterMessages = false;
			inputTaken = true;
		}
		if (l == 493) {
			stream.createFrame(75);
			stream.writeSignedBigEndian(interfaceId);
			stream.writeUnsignedWordBigEndian(slot);
			stream.writeUnsignedWordA(nodeId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 652) {
			boolean flag4 = doWalkTo(2, 0, 0, 0, myPlayer.pathY[0], 0, 0,
					interfaceId, myPlayer.pathX[0], false, slot);
			if (!flag4)
				flag4 = doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						interfaceId, myPlayer.pathX[0], false, slot);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(156);
			stream.writeUnsignedWordA(slot + baseX);
			stream.writeUnsignedWordBigEndian(interfaceId + baseY);
			stream.writeSignedBigEndian(nodeId);
		}
		if (l == 94) {
			boolean flag5 = doWalkTo(2, 0, 0, 0, myPlayer.pathY[0], 0, 0,
					interfaceId, myPlayer.pathX[0], false, slot);
			if (!flag5)
				flag5 = doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						interfaceId, myPlayer.pathX[0], false, slot);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(181);
			stream.writeUnsignedWordBigEndian(interfaceId + baseY);
			stream.writeWord(nodeId);
			stream.writeUnsignedWordBigEndian(slot + baseX);
			stream.writeUnsignedWordA(selectedSpellId);
		}
		if (l == 646) {
			stream.createFrame(185);
			stream.writeWord(interfaceId);
			RSInterface class9_2 = RSInterface.interfaceCache[interfaceId];
			if (class9_2.valueIndexArray != null
					&& class9_2.valueIndexArray[0][0] == 5) {
				int i2 = class9_2.valueIndexArray[0][1];
				if (variousSettings[i2] != class9_2.requiredValues[0]) {
					variousSettings[i2] = class9_2.requiredValues[0];
					handleActions(i2);
					needDrawTabArea = true;
				}
			}
		}
		if (l == 225) {
			NPC class30_sub2_sub4_sub1_sub1_2 = npcArray[nodeId];
			if (class30_sub2_sub4_sub1_sub1_2 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						class30_sub2_sub4_sub1_sub1_2.pathY[0],
						myPlayer.pathX[0], false,
						class30_sub2_sub4_sub1_sub1_2.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt1226 += nodeId;
				stream.createFrame(17);
				stream.writeSignedBigEndian(nodeId);
			}
		}
		if (l == 965) {
			NPC class30_sub2_sub4_sub1_sub1_3 = npcArray[nodeId];
			if (class30_sub2_sub4_sub1_sub1_3 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						class30_sub2_sub4_sub1_sub1_3.pathY[0],
						myPlayer.pathX[0], false,
						class30_sub2_sub4_sub1_sub1_3.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt1134++;
				stream.createFrame(21);
				stream.writeWord(nodeId);
			}
		}
		if (l == 413) {
			NPC class30_sub2_sub4_sub1_sub1_4 = npcArray[nodeId];
			if (class30_sub2_sub4_sub1_sub1_4 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						class30_sub2_sub4_sub1_sub1_4.pathY[0],
						myPlayer.pathX[0], false,
						class30_sub2_sub4_sub1_sub1_4.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(131);
				stream.writeSignedBigEndian(nodeId);
				stream.writeUnsignedWordA(spellID);
			}
		}
		if (l == 200)
			clearTopInterfaces();
		if (l == 1025) {
			NPC class30_sub2_sub4_sub1_sub1_5 = npcArray[nodeId];
			if (class30_sub2_sub4_sub1_sub1_5 != null) {
				NPCDef entityDef = class30_sub2_sub4_sub1_sub1_5.desc;
				if (entityDef.childrenIDs != null)
					entityDef = entityDef.getAlteredNPCDef();
				if (entityDef != null) {
					String s9;
					if (entityDef.description != null)
						s9 = new String(entityDef.description);
					else
						s9 = "You don't find anything interesting about the "
								+ entityDef.name + ".";
					pushMessage(s9, 0, "");
				}
			}
		}
		if (l == 900) {
			reachedClickedObject(nodeId, y, x, id);
			stream.createFrame(252);
			stream.writeSignedBigEndian(id);
			stream.writeUnsignedWordBigEndian(y + baseY);
			stream.writeUnsignedWordA(x + baseX);
		}
		if (l == 412) {
			NPC class30_sub2_sub4_sub1_sub1_6 = npcArray[nodeId];
			if (class30_sub2_sub4_sub1_sub1_6 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						class30_sub2_sub4_sub1_sub1_6.pathY[0],
						myPlayer.pathX[0], false,
						class30_sub2_sub4_sub1_sub1_6.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(72);
				stream.writeUnsignedWordA(nodeId);
			}
		}
		if (l == 365) {
			Player class30_sub2_sub4_sub1_sub2_3 = playerArray[nodeId];
			if (class30_sub2_sub4_sub1_sub2_3 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						class30_sub2_sub4_sub1_sub2_3.pathY[0],
						myPlayer.pathX[0], false,
						class30_sub2_sub4_sub1_sub2_3.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(249);
				stream.writeUnsignedWordA(nodeId);
				stream.writeUnsignedWordBigEndian(selectedSpellId);
			}
		}
		if (l == 729) {
			Player class30_sub2_sub4_sub1_sub2_4 = playerArray[nodeId];
			if (class30_sub2_sub4_sub1_sub2_4 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						class30_sub2_sub4_sub1_sub2_4.pathY[0],
						myPlayer.pathX[0], false,
						class30_sub2_sub4_sub1_sub2_4.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(39);
				stream.writeUnsignedWordBigEndian(nodeId);
			}
		}
		if (l == 577) {
			Player class30_sub2_sub4_sub1_sub2_5 = playerArray[nodeId];
			if (class30_sub2_sub4_sub1_sub2_5 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						class30_sub2_sub4_sub1_sub2_5.pathY[0],
						myPlayer.pathX[0], false,
						class30_sub2_sub4_sub1_sub2_5.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(139);
				stream.writeUnsignedWordBigEndian(nodeId);
			}
		}
		if (l == 956 && reachedClickedObject(nodeId, y, x, id)) {
			stream.createFrame(35);
			stream.writeUnsignedWordBigEndian(x + baseX);
			stream.writeUnsignedWordA(selectedSpellId);
			stream.writeUnsignedWordA(y + baseY);
			stream.writeUnsignedWordBigEndian(id);
		}
		if (l == 567) {
			boolean flag6 = doWalkTo(2, 0, 0, 0, myPlayer.pathY[0], 0, 0,
					interfaceId, myPlayer.pathX[0], false, slot);
			if (!flag6)
				flag6 = doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						interfaceId, myPlayer.pathX[0], false, slot);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(23);
			stream.writeUnsignedWordBigEndian(interfaceId + baseY);
			stream.writeUnsignedWordBigEndian(nodeId);
			stream.writeUnsignedWordBigEndian(slot + baseX);
		}
		if (l == 867) {
			if ((nodeId & 3) == 0)
				anInt1175++;
			if (anInt1175 >= 59) {
				stream.createFrame(200);
				stream.writeWord(25501);
				anInt1175 = 0;
			}
			stream.createFrame(43);
			stream.writeUnsignedWordBigEndian(interfaceId);
			stream.writeUnsignedWordA(nodeId);
			stream.writeUnsignedWordA(slot);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 543) {
			stream.createFrame(237);
			stream.writeWord(slot);
			stream.writeUnsignedWordA(nodeId);
			stream.writeWord(interfaceId);
			stream.writeUnsignedWordA(selectedSpellId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 606) {
			String s2 = menuActionName[i];
			int j2 = s2.indexOf("@whi@");
			if (j2 != -1)
				if (openInterfaceID == -1) {
					clearTopInterfaces();
					reportAbuseInput = s2.substring(j2 + 5).trim();
					canMute = false;
					for (int i3 = 0; i3 < RSInterface.interfaceCache.length; i3++) {
						if (RSInterface.interfaceCache[i3] == null
								|| RSInterface.interfaceCache[i3].contentType != 600)
							continue;
						reportAbuseInterfaceID = openInterfaceID = RSInterface.interfaceCache[i3].parentID;
						break;
					}

				} else {
					pushMessage(
							"Please close the interface you have open before using 'report abuse'",
							0, "");
				}
		}
		if (l == 491) {
			Player class30_sub2_sub4_sub1_sub2_6 = playerArray[nodeId];
			if (class30_sub2_sub4_sub1_sub2_6 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						class30_sub2_sub4_sub1_sub2_6.pathY[0],
						myPlayer.pathX[0], false,
						class30_sub2_sub4_sub1_sub2_6.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(14);
				stream.writeUnsignedWordA(lastItemSelectedInterface);
				stream.writeWord(nodeId);
				stream.writeWord(selectedItemId);
				stream.writeUnsignedWordBigEndian(lastItemSelectedSlot);
			}
		}
		if (l == 639) {
			String s3 = menuActionName[i];
			int k2 = s3.indexOf("@whi@");
			if (k2 != -1) {
				long l4 = TextClass.longForName(s3.substring(k2 + 5).trim());
				int k3 = -1;
				for (int i4 = 0; i4 < friendsCount; i4++) {
					if (friendsListAsLongs[i4] != l4)
						continue;
					k3 = i4;
					break;
				}

				if (k3 != -1 && friendsNodeIDs[k3] > 0) {
					inputTaken = true;
					inputDialogState = 0;
					showInput = true;
					promptInput = "";
					friendsListAction = 3;
					aLong953 = friendsListAsLongs[k3];
					promptMessage = "Enter message to send to "
							+ friendsList[k3];
				}
			}
		}
		if (l == 454) {
			stream.createFrame(41);
			stream.writeWord(nodeId);
			stream.writeUnsignedWordA(slot);
			stream.writeUnsignedWordA(interfaceId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = interfaceId;
			atInventoryIndex = slot;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[interfaceId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[interfaceId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (l == 478) {
			NPC class30_sub2_sub4_sub1_sub1_7 = npcArray[nodeId];
			if (class30_sub2_sub4_sub1_sub1_7 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						class30_sub2_sub4_sub1_sub1_7.pathY[0],
						myPlayer.pathX[0], false,
						class30_sub2_sub4_sub1_sub1_7.pathX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				if ((nodeId & 3) == 0)
					anInt1155++;
				if (anInt1155 >= 53) {
					stream.createFrame(85);
					stream.writeWordBigEndian(66);
					anInt1155 = 0;
				}
				stream.createFrame(18);
				stream.writeUnsignedWordBigEndian(nodeId);
			}
		}
		if (l == 113) {
			reachedClickedObject(nodeId, y, x, id);
			stream.createFrame(70);
			stream.writeUnsignedWordBigEndian(x + baseX);
			stream.writeWord(y + baseY);
			stream.writeSignedBigEndian(id);
		}
		if (l == 872) {
			reachedClickedObject(nodeId, y, x, id);
			stream.createFrame(234);
			stream.writeSignedBigEndian(x + baseX);
			stream.writeUnsignedWordA(id);
			stream.writeSignedBigEndian(y + baseY);
		}
		if (l == 502) {
			reachedClickedObject(nodeId, y, x, id);
			stream.createFrame(132);
			stream.method433(x + baseX);
			stream.writeWord(id);
			stream.method432(y + baseY);
		}
		if (l == 1125) {
			ItemDef itemDef = ItemDef.forID(nodeId);
			RSInterface class9_4 = RSInterface.interfaceCache[interfaceId];
			if (interfaceId == 38274) {
				stream.createFrame(122);
				stream.writeWord(interfaceId);
				stream.writeWord(slot);
				stream.writeWord(nodeId);
			} else {
				stream.createFrame(2); //examine item
				stream.writeWord(itemDef.ID);
			}
		}
		if (l == 169) {
			stream.createFrame(185);
			stream.writeWord(interfaceId);
			RSInterface class9_3 = RSInterface.interfaceCache[interfaceId];
			if (class9_3.valueIndexArray != null
					&& class9_3.valueIndexArray[0][0] == 5) {
				int l2 = class9_3.valueIndexArray[0][1];
				variousSettings[l2] = 1 - variousSettings[l2];
				handleActions(l2);
				needDrawTabArea = true;
			}
			switch (interfaceId) {
			case 35565:// hd tex
				options.put("hd_tex", !getOption("hd_tex"));
				OverLayFlo317.unpackConfig(streamLoaderForName(2, "config",
						"config", expectedCRCs[2], 30));
				loadRegion();
				break;
			case 35568:// player shadow
				options.put("player_shadow", !getOption("player_shadow"));
				Player.modelCache.clear();
				break;
			case 35571:// tooltip shadow
				options.put("tooltip_hover", !getOption("tooltip_hover"));
				break;
			case 35584:// hot keys
				options.put("old_hits", !getOption("old_hits"));
				break;
			case 35587:// constitution
				options.put("constitution", !getOption("constitution"));
				break;
			case 35590:// cursors
				options.put("cursors", !getOption("cursors"));
				setCursor(getOption("cursors") ? 0 : -1);
				break;
			case 35714:
				options.put("smilies", !getOption("smilies"));
				RSFontSystem.SMILIES_TOGGLED = getOption("smilies");
				break;
			case 35694:
				options.put("old_frame", !getOption("old_frame"));
				break;
			}
		}
		if (l == 447) {
			itemSelected = 1;
			lastItemSelectedSlot = slot;
			lastItemSelectedInterface = interfaceId;
			selectedItemId = nodeId;
			selectedItemName = ItemDef.forID(nodeId).name;
			spellSelected = 0;
			needDrawTabArea = true;
			return;
		}
		if (l == 1226) {
			int j1 = nodeId >> 14 & 0x7fff;
					ObjectDef class46 = ObjectDef.forID(j1);
					String s10;
					if (class46.description != null)
						s10 = new String(class46.description);
					else
						s10 = "You don't find anything interesting about the "
								+ class46.name + ".";
					pushMessage(s10, 0, "");
		}
		if (l == 244) {
			boolean flag7 = doWalkTo(2, 0, 0, 0, myPlayer.pathY[0], 0, 0,
					interfaceId, myPlayer.pathX[0], false, slot);
			if (!flag7)
				flag7 = doWalkTo(2, 0, 1, 0, myPlayer.pathY[0], 1, 0,
						interfaceId, myPlayer.pathX[0], false, slot);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(253);
			stream.writeUnsignedWordBigEndian(slot + baseX);
			stream.writeSignedBigEndian(interfaceId + baseY);
			stream.writeUnsignedWordA(nodeId);
		}
		itemSelected = 0;
		spellSelected = 0;
		needDrawTabArea = true;

	}

	private void checkTutorialIsland() {
		isOnTutorialIsland = 0;
		int j = (myPlayer.x >> 7) + baseX;
		int k = (myPlayer.y >> 7) + baseY;
		if (j >= 3053 && j <= 3156 && k >= 3056 && k <= 3136)
			isOnTutorialIsland = 1;
		if (j >= 3072 && j <= 3118 && k >= 9492 && k <= 9535)
			isOnTutorialIsland = 1;
		if (isOnTutorialIsland == 1 && j >= 3139 && j <= 3199 && k >= 3008
				&& k <= 3062)
			isOnTutorialIsland = 0;
	}

	public void run() {
		super.run();
	}

	private void build3dScreenMenu() {
		if (itemSelected == 0 && spellSelected == 0) {
			menuActionName[menuActionRow] = "Walk here";
			menuActionID[menuActionRow] = 516;
			menuActionCmd2[menuActionRow] = super.mouseX;
			menuActionCmd3[menuActionRow] = super.mouseY;
			menuActionRow++;
		}
		int lastUID = -1;
		for (int index = 0; index < Model.objectsRendered; index++) {
			int uid = Model.objectsInCurrentRegion[index];
			int x = uid & 0x7f;
			int y = uid >> 7 & 0x7f;
		int resourceType = uid >> 29 & 3; // k1
		int resourceId = uid >> 14 & 0x7fff;
		if (uid == lastUID)
			continue;
		lastUID = uid;
		if (resourceType == 2
				&& worldController.getIDTagForXYZ(plane, x, y, uid) >= 0) {
			if (resourceId != 1814)
				resourceId = Model.mapObjectIds[index];
			ObjectDef object = ObjectDef.forID(resourceId);
			if (object.configObjectIDs != null)
				object = object.getTransformedObject();
			if (object == null || object.name == null
					|| object.name == "null")
				continue;
			if (itemSelected == 1) {
				menuActionName[menuActionRow] = "Use " + selectedItemName
						+ " -> @cya@" + object.name;
				menuActionID[menuActionRow] = 62;
				menuActionCmd1[menuActionRow] = uid;
				menuActionCmd2[menuActionRow] = x;
				menuActionCmd3[menuActionRow] = y;
				menuActionCmd4[menuActionRow] = resourceId;
				menuActionRow++;
			} else if (spellSelected == 1) {
				if ((spellUsableOn & 4) == 4) {
					menuActionName[menuActionRow] = spellTooltip + " @cya@"
							+ object.name;
					menuActionID[menuActionRow] = 956;
					menuActionCmd1[menuActionRow] = uid;
					menuActionCmd2[menuActionRow] = x;
					menuActionCmd3[menuActionRow] = y;
					menuActionCmd4[menuActionRow] = resourceId;
					menuActionRow++;
				}
			} else {
				if (object.actions != null) {
					for (int i2 = 4; i2 >= 0; i2--)
						if (object.actions[i2] != null) {
							if (variousSettings[8000] == 1) {
								if (object.actions[i2].equals("Remove")
										|| object.actions[i2]
												.equals("Upgrade")
												|| object.actions[i2]
														.equals("Remove-room"))
									continue;
							}
							menuActionName[menuActionRow] = object.actions[i2]
									+ " @cya@" + object.name;
							if (i2 == 0)
								menuActionID[menuActionRow] = 502;
							if (i2 == 1)
								menuActionID[menuActionRow] = 900;
							if (i2 == 2)
								menuActionID[menuActionRow] = 113;
							if (i2 == 3)
								menuActionID[menuActionRow] = 872;
							if (i2 == 4)
								menuActionID[menuActionRow] = 1062;
							menuActionCmd1[menuActionRow] = uid;
							menuActionCmd2[menuActionRow] = x;
							menuActionCmd3[menuActionRow] = y;
							menuActionCmd4[menuActionRow] = resourceId;
							menuActionRow++;
						}
				}
				/*
				 * right click ids
				 */
				String s = myRights == 4 ? "Examine @cya@"
						+ object.name + " @gre@(@whi@" + object.type
						+ "@gre@) (@whi@" + (x + baseX) + "," + (y + baseY)
						+ "@gre@)" :  "Examine @cya@"
						+ object.name;
				menuActionName[menuActionRow] = s;
				menuActionID[menuActionRow] = 1226;
				menuActionCmd1[menuActionRow] = object.type << 14;
				menuActionCmd2[menuActionRow] = x;
				menuActionCmd3[menuActionRow] = y;
				menuActionCmd4[menuActionRow] = resourceId;
				menuActionRow++;
			}
		}
		if (resourceType == 1) {
			NPC npc = npcArray[resourceId];
			if (npc.desc.squaresNeeded == 1 && (npc.x & 0x7f) == 64
					&& (npc.y & 0x7f) == 64) {
				for (int j2 = 0; j2 < npcCount; j2++) {
					NPC npc2 = npcArray[npcIndices[j2]];
					if (npc2 != null && npc2 != npc
							&& npc2.desc.squaresNeeded == 1
							&& npc2.x == npc.x && npc2.y == npc.y)
						buildAtNPCMenu(npc2.desc, npcIndices[j2], y, x);
				}

				for (int l2 = 0; l2 < playerCount; l2++) {
					Player player = playerArray[playerIndices[l2]];
					if (player != null && player.x == npc.x
							&& player.y == npc.y)
						buildAtPlayerMenu(x, playerIndices[l2], player, y);
				}

			}
			buildAtNPCMenu(npc.desc, resourceId, y, x);
		}
		if (resourceType == 0) {
			Player player = playerArray[resourceId];
			if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64) {
				for (int k2 = 0; k2 < npcCount; k2++) {
					NPC class30_sub2_sub4_sub1_sub1_2 = npcArray[npcIndices[k2]];
					try {
						if (class30_sub2_sub4_sub1_sub1_2 != null
								&& class30_sub2_sub4_sub1_sub1_2.desc.squaresNeeded == 1
								&& class30_sub2_sub4_sub1_sub1_2.x == player.x
								&& class30_sub2_sub4_sub1_sub1_2.y == player.y)
							buildAtNPCMenu(
									class30_sub2_sub4_sub1_sub1_2.desc,
									npcIndices[k2], y, x);
					} catch (Exception _ex) {
					}
				}

				for (int i3 = 0; i3 < playerCount; i3++) {
					Player class30_sub2_sub4_sub1_sub2_2 = playerArray[playerIndices[i3]];
					if (class30_sub2_sub4_sub1_sub2_2 != null
							&& class30_sub2_sub4_sub1_sub2_2 != player
							&& class30_sub2_sub4_sub1_sub2_2.x == player.x
							&& class30_sub2_sub4_sub1_sub2_2.y == player.y)
						buildAtPlayerMenu(x, playerIndices[i3],
								class30_sub2_sub4_sub1_sub2_2, y);
				}

			}
			buildAtPlayerMenu(x, resourceId, player, y);
		}
		if (resourceType == 3) {
			Deque class19 = groundArray[plane][x][y];
			if (class19 != null) {
				for (Item item = (Item) class19.getBack(); item != null; item = (Item) class19
						.getPrevious()) {
					ItemDef itemDef = ItemDef.forID(item.ID);
					if (itemSelected == 1) {
						menuActionName[menuActionRow] = "Use "
								+ selectedItemName + " with @lre@"
								+ itemDef.name;
						menuActionID[menuActionRow] = 511;
						menuActionCmd1[menuActionRow] = item.ID;
						menuActionCmd2[menuActionRow] = x;
						menuActionCmd3[menuActionRow] = y;
						menuActionRow++;
					} else if (spellSelected == 1) {
						if ((spellUsableOn & 1) == 1) {
							menuActionName[menuActionRow] = spellTooltip
									+ " @lre@" + itemDef.name;
							menuActionID[menuActionRow] = 94;
							menuActionCmd1[menuActionRow] = item.ID;
							menuActionCmd2[menuActionRow] = x;
							menuActionCmd3[menuActionRow] = y;
							menuActionRow++;
						}
					} else {
						for (int j3 = 4; j3 >= 0; j3--)
							if (itemDef.groundActions != null
							&& itemDef.groundActions[j3] != null) {
								menuActionName[menuActionRow] = itemDef.groundActions[j3]
										+ " @lre@" + itemDef.name;
								if (j3 == 0)
									menuActionID[menuActionRow] = 652;
								if (j3 == 1)
									menuActionID[menuActionRow] = 567;
								if (j3 == 2)
									menuActionID[menuActionRow] = 234;
								if (j3 == 3)
									menuActionID[menuActionRow] = 244;
								if (j3 == 4)
									menuActionID[menuActionRow] = 213;
								menuActionCmd1[menuActionRow] = item.ID;
								menuActionCmd2[menuActionRow] = x;
								menuActionCmd3[menuActionRow] = y;
								menuActionRow++;
							} else if (j3 == 2) {
								menuActionName[menuActionRow] = "Take @lre@"
										+ itemDef.name;
								menuActionID[menuActionRow] = 234;
								menuActionCmd1[menuActionRow] = item.ID;
								menuActionCmd2[menuActionRow] = x;
								menuActionCmd3[menuActionRow] = y;
								menuActionRow++;
							}
						menuActionName[menuActionRow] = "Examine @lre@"
								+ itemDef.name;
						menuActionID[menuActionRow] = 1448;
						menuActionCmd1[menuActionRow] = item.ID;
						menuActionCmd2[menuActionRow] = x;
						menuActionCmd3[menuActionRow] = y;
						menuActionRow++;
					}
				}

			}
		}
		}
	}

	public void cleanUpForQuit() {
		signlink.reporterror = false;
		try {
			if (socketStream != null)
				socketStream.close();
		} catch (Exception _ex) {
		}
		socketStream = null;
		stopMidi();
		if (mouseDetection != null)
			mouseDetection.running = false;
		mouseDetection = null;
		onDemandFetcher.dispose();
		onDemandFetcher = null;
		textStream = null;
		prayClicked = false;
		stream = null;
		aStream_847 = null;
		inStream = null;
		mapCoordinates = null;
		terrainData = null;
		objectData = null;
		terrainIndices = null;
		objectIndices = null;
		intGroundArray = null;
		byteGroundArray = null;
		worldController = null;
		clippingPlanes = null;
		anIntArrayArray901 = null;
		anIntArrayArray825 = null;
		bigX = null;
		bigY = null;
		tabAreaIP = null;
		leftFrame = null;
		topFrame = null;
		rightFrame = null;
		mapAreaIP = null;
		gameScreenIP = null;
		chatAreaIP = null;
		GraphicsBuffer_1125 = null;
		/* Null pointers for custom sprites */
		logo = bg5 = bo = bo2 = b3 = null;
		fixed = resize = fullscreen = null;
		coinOrb = coinOrbHover = coinPart = coinPartHover = coinOrbPart = coinOrbPartHover = null;
		loadingPleaseWait = null;
		newbg3 = null;
		shortcutIcon = shortcutIconHover = null;
		reestablish = null;
		HPBarFull = null;
		HPBarEmpty = null;
		HPBarBigEmpty = null;
		//newMapBack = null;
		orbs = null;
		//LOGOUT = null;
		/**/
		sideIcons = null;
		compass = null;
		headIcons = null;
		skullIcons = null;
		headIconsHint = null;
		crosses = null;
		mapDotItem = null;
		mapDotNPC = null;
		mapDotPlayer = null;
		mapDotFriend = null;
		mapDotTeam = null;
		mapScenes = null;
		mapFunctions = null;
		anIntArrayArray929 = null;
		playerArray = null;
		playerIndices = null;
		playersToUpdate = null;
		aStreamArray895s = null;
		anIntArray840 = null;
		npcArray = null;
		npcIndices = null;
		groundArray = null;
		objectSpawnDeque = null;
		projectileDeque = null;
		stillGraphicDeque = null;
		menuActionCmd2 = null;
		menuActionCmd3 = null;
		menuActionCmd4 = null;
		menuActionID = null;
		menuActionCmd1 = null;
		menuActionName = null;
		variousSettings = null;
		mapFunctionTileX = null;
		mapFunctionTileY = null;
		currentMapFunctionSprites = null;
		miniMap = null;
		friendsList = null;
		friendsListAsLongs = null;
		friendsNodeIDs = null;
		GraphicsBuffer_1107 = null;
		titleScreen = null;
		multiOverlay = null;
		nullLoader();
		ObjectDef.nullLoader();
		NPCDef.nullLoader();
		ItemDef.nullLoader();
		Flo.cache = null;
		IDK.cache = null;
		RSInterface.interfaceCache = null;
		DummyClass.cache = null;
		Animation.anims = null;
		SpotAnim.cache = null;
		SpotAnim.modelCache = null;
		Varp.cache = null;
		super.fullGameScreen = null;
		Player.modelCache = null;
		Rasterizer.clearCache();
		WorldController.nullLoader();
		Model.nullLoader();
		FrameReader.nullLoader();
		System.gc();
	}

	void printDebug() {
		System.out.println("============");
		System.out.println("flame-cycle:" + anInt1208);
		if (onDemandFetcher != null)
			System.out.println("Od-cycle:" + onDemandFetcher.onDemandCycle);
		System.out.println("loop-cycle:" + loopCycle);
		System.out.println("draw-cycle:" + anInt1061);
		System.out.println("ptype:" + opCode);
		System.out.println("psize:" + pktSize);
		if (socketStream != null)
			socketStream.printDebug();
		super.shouldDebug = true;
	}

	Component getGameComponent() {
		if (signlink.mainapp != null)
			return signlink.mainapp;
		if (super.mainFrame != null)
			return super.mainFrame;
		else
			return this;
	}

	private void manageTextInput() {
		do {

			int key = readChar(-796);
			if (key == -1)
				break;
			if(key == 167 || key == 96) {
				if(myRights >= 1 && myRights <= 4)
					consoleOpen = !consoleOpen;
				return;
			}
			if (consoleOpen) {
				if (key == 8 && consoleInput.length() > 0)
					consoleInput = consoleInput.substring(0,
							consoleInput.length() - 1);
				if (key >= 32 && key <= 122
						&& consoleInput.length() < 80)
					consoleInput += (char) key;

				if ((key == 13 || key == 10)
						&& consoleInput.length() >= 1) {
					sendConsoleMessage(consoleInput, true);
					sendConsoleCommand(consoleInput);
					consoleInput = "";
					inputTaken = true;
				}
				return;
			}
			if (openInterfaceID == 10000) {
				if (key == 8) {
					if(!reportBox2Selected) {
						if(playerReporting.length() > 0)
							playerReporting = playerReporting.substring(0, playerReporting.length() - 1);
						else
							playerReporting = "";
					} else {
						if(reasonForReport.length() > 0)
							reasonForReport = reasonForReport.substring(0, reasonForReport.length() - 1);
						else
							reasonForReport = "";
					}
				} else if(key == 10 || key == 9)
					reportBox2Selected = !reportBox2Selected;
				if(!reportBox2Selected) {
					if ((key >= 97 && key <= 122 || key >= 65
							&& key <= 90 || key >= 48
							&& key <= 57 || key == 32)) {
						if(playerReporting.length() <= 12 && key != 65535 && key != 8) {
							playerReporting += (char) key;
							if(playerReporting.length() > 0)
								playerReporting = optimizeText(playerReporting);
							inputString = "";
							return;
						} else {
							pushMessage("A player name can only contain 12 characters.", 0, "");
							break;
						}
					}
				} else {
					if ((key >= 97 && key <= 122 || key >= 65
							&& key <= 90 || key >= 48
							&& key <= 57 || key == 32)) {
						////system.out.println(reason2Report.length());
						if(reasonForReport.length() <= 64 && key != 65535 && key != 8) {
							reasonForReport += (char) key;
							inputString = "";
							return;
						} else {
							pushMessage("A report can only contain 65 characters.", 0, "");
							break;
						}
					}
				}
			} 
			if (backDialogID == 310 || backDialogID == 306
					|| backDialogID == 315 || backDialogID == 321
					|| backDialogID == 4887 || backDialogID == 4900
					|| backDialogID == 6179 || backDialogID == 356
					|| backDialogID == 4882 || backDialogID == 356
					|| backDialogID == 359 || backDialogID == 363
					|| backDialogID == 368 || backDialogID == 374
					|| backDialogID == 4882 || backDialogID == 4887
					|| backDialogID == 4893 || backDialogID == 4900
					|| backDialogID == 968 || backDialogID == 973
					|| backDialogID == 979 || backDialogID == 986) {
				if (key == KeyEvent.VK_SPACE) {
					stream.createFrame(40);
					stream.writeWord(0);
				}
			}
			if (openInterfaceID != -1
					&& openInterfaceID == reportAbuseInterfaceID) {
				if (key == 8 && reportAbuseInput.length() > 0)
					reportAbuseInput = reportAbuseInput.substring(0,
							reportAbuseInput.length() - 1);
				if ((key >= 97 && key <= 122 || key >= 65 && key <= 90
						|| key >= 48 && key <= 57 || key == 32)
						&& reportAbuseInput.length() < 12)
					reportAbuseInput += (char) key;
			} else if (showInput) {
				if (key >= 32 && key <= 122 && promptInput.length() < 80) {
					promptInput += (char) key;
					inputTaken = true;
				}
				if (key == 8 && promptInput.length() > 0) {
					promptInput = promptInput.substring(0,
							promptInput.length() - 1);
					inputTaken = true;
				}
				if (key == 13 || key == 10) {
					showInput = false;
					inputTaken = true;
					if (interfaceButtonAction == 6651
							&& promptInput.length() > 0) {
						stream.createFrame(103);
						stream.writeWordBigEndian(promptInput.length() + 5);
						stream.writeString("note" + promptInput);
						promptInput = "";
						interfaceButtonAction = -1;
					}
					if (friendsListAction == 1) {
						long l = TextClass.longForName(promptInput);
						addFriend(l);
					}
					if (interfaceButtonAction == 6199
							&& promptInput.length() > 0) {
						String inp = "";
						inp = inputString;
						inputString = "::[CN] " + promptInput;
						sendPacket(103);
						inputString = inp;
					}
					if (interfaceButtonAction == 6200
							&& promptInput.length() > 0) {
						String inp = "";
						inp = inputString;
						inputString = "::[NC] " + promptInput;
						sendPacket(103);
						inputString = inp;
					}
					if (friendsListAction == 2 && friendsCount > 0) {
						long l1 = TextClass.longForName(promptInput);
						delFriend(l1);
					}
					if (friendsListAction == 3 && promptInput.length() > 0) {
						stream.createFrame(126);
						stream.writeWordBigEndian(0);
						int k = stream.currentOffset;
						stream.writeQWord(aLong953);
						TextInput.appendToStream(promptInput, stream);
						stream.writeBytes(stream.currentOffset - k);
						promptInput = TextInput.processText(promptInput);
						//promptInput = Censor.doCensor(promptInput);
						pushMessage(promptInput, 6, TextClass.fixName(TextClass
								.nameForLong(aLong953)));
						if (privateChatMode == 2) {
							privateChatMode = 1;
							stream.createFrame(95);
							stream.writeWordBigEndian(publicChatMode);
							stream.writeWordBigEndian(privateChatMode);
							stream.writeWordBigEndian(tradeMode);
						}
					}
					if (friendsListAction == 4 && ignoreCount < 100) {
						long l2 = TextClass.longForName(promptInput);
						addIgnore(l2);
					}
					if (friendsListAction == 5 && ignoreCount > 0) {
						long l3 = TextClass.longForName(promptInput);
						delIgnore(l3);
					}
					if (friendsListAction == 6) {
						stream.createFrame(dungInvite ? 12 : 60);
						stream.writeWordBigEndian(promptInput.length() +1);
						stream.writeString(promptInput);
						dungInvite = false;
					}
				}
			}
			else if (inputDialogState == 1) {
				if (key >= 48 && key <= 57 && amountOrNameInput.length() < 10) {
					amountOrNameInput += (char) key;
					inputTaken = true;
					long l = Long.valueOf(amountOrNameInput);
					if (l == 0) {
						amountOrNameInput = "";
						inputTaken = true;
					}
				}
				if ((amountOrNameInput.length() > 0
						&& !amountOrNameInput.toLowerCase().contains("k")
						&& !amountOrNameInput.toLowerCase().contains("m") && !amountOrNameInput
						.toLowerCase().contains("b"))
						&& (key == 107 || key == 109 || key == 98)) {
					int am = 0;
					boolean addChar = true;
					long l = Long.valueOf(amountOrNameInput);
					if (l > 2147483647) {
						amountOrNameInput = "2147483647";
						inputTaken = true;
						addChar = false;
					} else {
						am = Integer.parseInt(amountOrNameInput);
					}
					if (key == 107 && am > 2147000) {
						addChar = false;
					}
					if (key == 109 && am > 2147) {
						addChar = false;
					}
					if (key == 98 && am > 2) {
						addChar = false;
					}

					if (addChar && am > 0) {
						amountOrNameInput += (char) key;
						inputTaken = true;
					}
				}
				if (key == 8 && amountOrNameInput.length() > 0) {
					amountOrNameInput = amountOrNameInput.substring(0,
							amountOrNameInput.length() - 1);
					inputTaken = true;
				}
				if (key == 13 || key == 10) {
					if (amountOrNameInput.length() > 0) {
						if (amountOrNameInput.toLowerCase().contains("k")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"k", "000");
						} else if (amountOrNameInput.toLowerCase()
								.contains("m")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"m", "000000");
						} else if (amountOrNameInput.toLowerCase()
								.contains("b")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"b", "000000000");
						}
						long l = Long.valueOf(amountOrNameInput);

						if (l > 2147483647) {
							amountOrNameInput = "2147483647";
						}
						int amount = 0;
						amount = Integer.parseInt(amountOrNameInput);
						if(interfaceButtonAction == 557 && withdrawingMoneyFromPouch) {
							stream.createFrame(7);
							stream.writeDWord(amount);
							inputDialogState = 0;
							inputTaken = true;
							withdrawingMoneyFromPouch = false;
							return;
						}
						stream.createFrame(208);
						stream.writeDWord(amount);
						inputTitle = null;
						interfaceButtonAction = 0;
					}
					inputDialogState = 0;
					inputTaken = true;
					withdrawingMoneyFromPouch = false;
				}
				if (key == 13 || key == 10) {
					if (interfaceButtonAction == 1557
							&& amountOrNameInput.length() > 0) {
						if (amountOrNameInput.toLowerCase().contains("k")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"k", "000");
						} else if (amountOrNameInput.toLowerCase()
								.contains("m")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"m", "000000");
						} else if (amountOrNameInput.toLowerCase()
								.contains("b")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"b", "000000000");
						}
						long l = Long.valueOf(amountOrNameInput);

						if (l > 2147483647) {
							amountOrNameInput = "2147483647";
						}
						TalkingFix = inputString;
						inputString = "::[L]" + amountOrNameInput;
						sendPacket(103);
						inputString = TalkingFix;
						inputTaken = true;
					}
					if (interfaceButtonAction == 1558
							&& amountOrNameInput.length() > 0) {
						if (amountOrNameInput.toLowerCase().contains("k")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"k", "000");
						} else if (amountOrNameInput.toLowerCase()
								.contains("m")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"m", "000000");
						} else if (amountOrNameInput.toLowerCase()
								.contains("b")) {
							amountOrNameInput = amountOrNameInput.replaceAll(
									"b", "000000000");
						}
						long l = Long.valueOf(amountOrNameInput);

						if (l > 2147483647) {
							amountOrNameInput = "2147483647";
						}
						TalkingFix = inputString;
						inputString = "::[E]" + amountOrNameInput;
						sendPacket(103);
						inputString = TalkingFix;
						inputTaken = true;
					}
					if (interfaceButtonAction == 1557
							&& amountOrNameInput.length() == 0
							|| interfaceButtonAction == 1558
							&& amountOrNameInput.length() == 0) {
						interfaceButtonAction = 0;
					}
				}
			} else if (inputDialogState == 3) {
				if (key == 10) {
					totalItemResults = 0;
					amountOrNameInput = "";
					inputDialogState = 0;
					inputTaken = true;
				}
				if (key == 13 || key == 10) {
					if (amountOrNameInput.length() == 0) {
						buttonclicked = false;
						interfaceButtonAction = 0;
					}
				}
				if (key >= 32 && key <= 122 && amountOrNameInput.length() < 40) {
					amountOrNameInput += (char) key;
					inputTaken = true;
				}
				if (key == 8 && amountOrNameInput.length() > 0) {
					amountOrNameInput = amountOrNameInput.substring(0,
							amountOrNameInput.length() - 1);
					inputTaken = true;
				}
			} else if (inputDialogState == 2) {
				if (key >= 32 && key <= 122 && amountOrNameInput.length() < 40) {
					amountOrNameInput += (char) key;
					inputTaken = true;
				}
				if (key == 8 && amountOrNameInput.length() > 0) {
					amountOrNameInput = amountOrNameInput.substring(0,
							amountOrNameInput.length() - 1);
					inputTaken = true;
				}
				if (key == 13 || key == 10) {
					if (amountOrNameInput.length() > 0) {

						stream.createFrame(60);
						stream.writeWordBigEndian(amountOrNameInput.length() +1);
						stream.writeString(amountOrNameInput);
						//stream.writeQWord(TextClass
						//		.longForName(amountOrNameInput));

					}
					inputDialogState = 0;
					inputTaken = true;
				}
			} else if (inputDialogState == 5) {
				int max = 12;
				if (playerCommand == 1)
					max = 1;
				if (key >= 32 && key <= 122 && amountOrNameInput.length() < max) {
					amountOrNameInput += (char) key;
					inputTaken = true;
				}
				if (key == 8 && amountOrNameInput.length() > 0) {
					amountOrNameInput = amountOrNameInput.substring(0,
							amountOrNameInput.length() - 1);
					inputTaken = true;
				}
				if (key == 13 || key == 10) {
					if (amountOrNameInput.length() > 0) {
						if (playerCommand != 0) {
							stream.createFrame(103);
							stream.writeWordBigEndian(amountOrNameInput
									.length()
									+ PLRCOMMANDS[playerCommand - 1][1]
											.length() + 1);
							stream.writeString(PLRCOMMANDS[playerCommand - 1][1]
									+ amountOrNameInput);
						}
					}
					inputDialogState = 0;
					inputTaken = true;
				}
			} else if (backDialogID == -1) {
				if (key == 9)
					tabToReplyPm();
				if(key == 124) {
					inputString += "|";
					break;
				}
				if(key == 94) {
					inputString += "^^";
					break;
				}
				if (key >= 32 && key <= 122 && inputString.length() < 80) {
					inputString += (char) key;
					inputTaken = true;
				}
				if (key == 8 && inputString.length() > 0) {
					inputString = inputString.substring(0,
							inputString.length() - 1);
					inputTaken = true;
				}
				if ((key == 13 || key == 10) && inputString.length() > 0) {
					if (inputString.startsWith("/")) {
						stream.createFrame(5);
						stream.writeWordBigEndian(inputString.substring(1).length() +1);
						stream.writeString(inputString.substring(1));
						inputString = "";
						return;
					}
					if (inputString.startsWith("::")
							&& !inputString.startsWith("::[")) {
						if(inputString.equalsIgnoreCase("::censor")) {
							options.put("censor_active", !getOption("censor_active"));
							pushMessage("Censor status toggled.", 0, "");
							inputString = "";
							return;
						}
						if(inputString.equalsIgnoreCase("::fog")) {
							pushMessage("FOG status toggled.", 0, "");
							inputString = "";
							options.put("fog_active", !getOption("fog_active"));
							return;
						}
						stream.createFrame(103);
						stream.writeWordBigEndian(inputString.length() - 1);
						stream.writeString(inputString.substring(2));
					} else {
						String s = inputString.toLowerCase();
						int color = 0;
						if (s.startsWith("yellow:")) {
							color = 0;
							inputString = inputString.substring(7);
						} else if (s.startsWith("red:")) {
							color = 1;
							inputString = inputString.substring(4);
						} else if (s.startsWith("green:")) {
							color = 2;
							inputString = inputString.substring(6);
						} else if (s.startsWith("cyan:")) {
							color = 3;
							inputString = inputString.substring(5);
						} else if (s.startsWith("purple:")) {
							color = 4;
							inputString = inputString.substring(7);
						} else if (s.startsWith("white:")) {
							color = 5;
							inputString = inputString.substring(6);
						} else if (s.startsWith("flash1:")) {
							color = 6;
							inputString = inputString.substring(7);
						} else if (s.startsWith("flash2:")) {
							color = 7;
							inputString = inputString.substring(7);
						} else if (s.startsWith("flash3:")) {
							color = 8;
							inputString = inputString.substring(7);
						} else if (s.startsWith("glow1:")) {
							color = 9;
							inputString = inputString.substring(6);
						} else if (s.startsWith("glow2:")) {
							color = 10;
							inputString = inputString.substring(6);
						} else if (s.startsWith("glow3:")) {
							color = 11;
							inputString = inputString.substring(6);
						}
						s = inputString.toLowerCase();
						int effect = 0;
						if (s.startsWith("wave:")) {
							effect = 1;
							inputString = inputString.substring(5);
						} else if (s.startsWith("wave2:")) {
							effect = 2;
							inputString = inputString.substring(6);
						} else if (s.startsWith("shake:")) {
							effect = 3;
							inputString = inputString.substring(6);
						} else if (s.startsWith("scroll:")) {
							effect = 4;
							inputString = inputString.substring(7);
						} else if (s.startsWith("slide:")) {
							effect = 5;
							inputString = inputString.substring(6);
						}
						if (s.startsWith("opacity")) {
							int nullilify = Integer.parseInt(inputString
									.substring(8));
							RSInterface.interfaceCache[nullilify].type = -1;
							RSInterface rsi = RSInterface.interfaceCache[962];
							int id = 0;
							if (rsi.children != null) {
								for (int ii : rsi.children) {
									RSInterface rsi_ = RSInterface.interfaceCache[ii];
									if (rsi_.type == 5)
										System.out.println(rsi_.id + " "
												+ rsi.childX[id] + " "
												+ rsi.childY[id]);
									id++;
								}
							}
						}
						stream.createFrame(4);
						stream.writeWordBigEndian(0);
						int j3 = stream.currentOffset;
						stream.method425(effect);
						stream.method425(color);
						textStream.currentOffset = 0;
						TextInput.appendToStream(inputString, textStream);
						stream.method441(0, textStream.buffer,
								textStream.currentOffset);
						stream.writeBytes(stream.currentOffset - j3);
						inputString = TextInput.processText(inputString);
						inputString = Censor.doCensor(inputString);
						myPlayer.textSpoken = inputString;
						myPlayer.anInt1513 = color;
						myPlayer.anInt1531 = effect;
						myPlayer.textCycle = 150;
						pushMessage(myPlayer.textSpoken, 2, getPrefix(myRights) + myPlayer.name);
						if (publicChatMode == 2) {
							publicChatMode = 3;
							stream.createFrame(95);
							stream.writeWordBigEndian(publicChatMode);
							stream.writeWordBigEndian(privateChatMode);
							stream.writeWordBigEndian(tradeMode);
						}
					}
					inputString = "";
					inputTaken = true;
				}
			}
		} while (true);
	}

	public static String optimizeText(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)),
						s.substring(1));
			}
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1),
							Character.toUpperCase(s.charAt(i + 1)),
							s.substring(i + 2));
				}
			}
		}
		return s.replace("_", " ");
	}

	private void buildPublicChat(int j) {
		int l = 0;
		for (int index = 0; index < 500; index++) {
			if (chatMessages[index] == null)
				continue;
			if (chatTypeView != 1)
				continue;
			int type = chatTypes[index];
			String name = chatNames[index];
			// String message = chatMessages[index];
			int positionY = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (positionY < -23)
				break;
			if (name != null && name.indexOf("@") == 0) {
				name = name.substring(5);
			}
			if ((type == 1 || type == 2)
					&& (type == 1 || publicChatMode == 0 || publicChatMode == 1
					&& isFriendOrSelf(name))) {
				if (j > positionY - 14 && j <= positionY
						&& !name.equals(myPlayer.name)) {
					if (myRights >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@"
								+ name;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					if (!isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Add ignore @whi@"
								+ name;
						menuActionID[menuActionRow] = 42;
						menuActionRow++;
						menuActionName[menuActionRow] = "Add friend @whi@"
								+ name;
						menuActionID[menuActionRow] = 337;
						menuActionRow++;
					}
					if (isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Message @whi@" + name;
						menuActionID[menuActionRow] = 639;
						menuActionRow++;
					}
				}
				l++;
			}
		}
	}

	private void buildFriendChat(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 2)
				continue;
			int type = chatTypes[i1];
			String name = chatNames[i1];
			// String message = chatMessages[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (k1 < -23)
				break;
			if (name != null && name.indexOf("@") == 0) {
				// name = name.substring(5);
			}
			if ((type == 5 || type == 6)
					&& (splitPrivateChat == 0 || chatTypeView == 2)
					&& (type == 6 || privateChatMode == 0 || privateChatMode == 1
					&& isFriendOrSelf(name)))
				l++;
			if ((type == 3 || type == 7)
					&& (splitPrivateChat == 0 || chatTypeView == 2)
					&& (type == 7 || privateChatMode == 0 || privateChatMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1) {
					if (myRights >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@"
								+ name;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					if (!isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Add ignore @whi@"
								+ name;
						menuActionID[menuActionRow] = 42;
						menuActionRow++;
						menuActionName[menuActionRow] = "Add friend @whi@"
								+ name;
						menuActionID[menuActionRow] = 337;
						menuActionRow++;
					}
					if (isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Message @whi@" + name;
						menuActionID[menuActionRow] = 639;
						menuActionRow++;
					}
				}
				l++;
			}
		}
	}

	private void buildDuelorTrade(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 3 && chatTypeView != 4)
				continue;
			int j1 = chatTypes[i1];
			String name = chatNames[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (k1 < -23)
				break;
			if (name != null && name.indexOf("@") == 0) {
				name = name.substring(5);
			}
			if (chatTypeView == 3
					&& j1 == 4
					&& (tradeMode == 0 || tradeMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept trade @whi@" + name;
					menuActionID[menuActionRow] = 484;
					menuActionRow++;
				}
				l++;
			}
			if (chatTypeView == 4
					&& j1 == 8
					&& (tradeMode == 0 || tradeMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept challenge @whi@"
							+ name;
					menuActionID[menuActionRow] = 6;
					menuActionRow++;
				}
				l++;
			}
			if (j1 == 12) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Go-to @blu@" + name;
					menuActionID[menuActionRow] = 915;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	private void buildChatAreaMenu(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			int j1 = chatTypes[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (k1 < -23)
				break;
			String name = chatNames[i1];
			if (chatTypeView == 1) {
				buildPublicChat(j);
				break;
			}
			if (chatTypeView == 2) {
				buildFriendChat(j);
				break;
			}
			if (chatTypeView == 3 || chatTypeView == 4) {
				buildDuelorTrade(j);
				break;
			}
			if (inputDialogState == 3) {
				buildItemSearch(j);
				break;
			}
			if (chatTypeView == 5) {
				break;
			}
			boolean fixed = false;
			if (name != null && name.indexOf("@") == 0) {
				if(name.contains("@cr10")) {
					rights = 10;
					fixed = true;
				} else if(name.contains("@cr11")) {
					rights = 11;
					fixed = true;
				}
				name = name.substring(fixed ? 6 : 5);
				if(!fixed)
					rights = getPrefixRights(name.substring(0, name.indexOf(name)));
			}
			if(rights == 0 && name != null && name.startsWith("<img=0>")) {
				name = name.substring(7);
				rights = 1;
			}
			if (j1 == 0)
				l++;
			if ((j1 == 1 || j1 == 2)
					&& (j1 == 1 || publicChatMode == 0 || publicChatMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1 && !name.equals(myPlayer.name)) {
					if (myRights >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@"
								+ name;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					if (!isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Add ignore @whi@"
								+ name;
						menuActionID[menuActionRow] = 42;
						menuActionRow++;
						menuActionName[menuActionRow] = "Add friend @whi@"
								+ name;
						menuActionID[menuActionRow] = 337;
						menuActionRow++;
					}
					if (isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Message @whi@" + name;
						menuActionID[menuActionRow] = 639;
						menuActionRow++;
					}
				}
				l++;
			}
			if ((j1 == 3 || j1 == 7)
					&& splitPrivateChat == 0
					&& (j1 == 7 || privateChatMode == 0 || privateChatMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1) {
					if (myRights >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@"
								+ name;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					if (!isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Add ignore @whi@"
								+ name;
						menuActionID[menuActionRow] = 42;
						menuActionRow++;
						menuActionName[menuActionRow] = "Add friend @whi@"
								+ name;
						menuActionID[menuActionRow] = 337;
						menuActionRow++;
					}
					if (isFriendOrSelf(name)) {
						menuActionName[menuActionRow] = "Message @whi@" + name;
						menuActionID[menuActionRow] = 639;
						menuActionRow++;
					}
				}
				l++;
			}
			if (j1 == 4
					&& (tradeMode == 0 || tradeMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept trade @whi@" + name;
					menuActionID[menuActionRow] = 484;
					menuActionRow++;
				}
				l++;
			}
			if ((j1 == 5 || j1 == 6) && splitPrivateChat == 0
					&& privateChatMode < 2)
				l++;
			if (j1 == 8
					&& (tradeMode == 0 || tradeMode == 1
					&& isFriendOrSelf(name))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept challenge @whi@"
							+ name;
					menuActionID[menuActionRow] = 6;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	public static int totalItemResults;
	public String itemResultNames[] = new String[100];
	public int itemResultIDs[] = new int[100];
	public static int itemResultScrollPos;

	public void itemSearch(String n) {
		if (n == null || n.length() == 0) {
			totalItemResults = 0;
			return;
		}
		String searchName = n;
		String searchParts[] = new String[100];
		int totalResults = 0;
		do {
			int k = searchName.indexOf(" ");
			if (k == -1)
				break;
			String part = searchName.substring(0, k).trim();
			if (part.length() > 0)
				searchParts[totalResults++] = part.toLowerCase();
			searchName = searchName.substring(k + 1);
		} while (true);
		searchName = searchName.trim();
		if (searchName.length() > 0)
			searchParts[totalResults++] = searchName.toLowerCase();
		totalItemResults = 0;
		label0: for (int id = 0; id < ItemDef.totalItems; id++) {
			ItemDef item = ItemDef.forID(id);
			if (item.certTemplateID != -1 || item.lentItemID != -1
					|| item.name == null || item.name == "Picture"
					|| item.certID == 18786 || item.name == "Null")
				continue;
			String result = item.name.toLowerCase();
			for (int idx = 0; idx < totalResults; idx++)
				if (result.indexOf(searchParts[idx]) == -1)
					continue label0;

			// int value = getItemShopValue(id);

			if (item.value != 0) {
				itemResultNames[totalItemResults] = result;
				itemResultIDs[totalItemResults] = id;
				totalItemResults++;
			}
			if (totalItemResults >= itemResultNames.length)
				return;
		}
	}

	private void buildItemSearch(int mouseY) {
		int y = 0;
		for (int idx = 0; idx < 100; idx++) {
			if (amountOrNameInput.length() == 0)
				return;
			else if (totalItemResults == 0)
				return;
			if (amountOrNameInput == "")
				return;
			String name = capitalizeFirstChar(itemResultNames[idx]);
			for (int i = 0; i <= 20; i++)
				if (name.contains(" <img=" + i + ">"))
					name = name.replaceAll(" <img=" + i + ">", "");
			int textY = (21 + y * 14) - itemResultScrollPos;
			if (mouseY > textY - 14 && mouseY <= textY && super.mouseX > 74
					&& super.mouseX < 495) {
				menuActionName[menuActionRow] = "" + name;
				menuActionID[menuActionRow] = 1251;
				menuActionRow++;
			}
			y++;
		}
	}

	private boolean rollingCharacter = false;

	private void drawFriendsListOrWelcomeScreen(RSInterface rsi) {
		int j = rsi.contentType;
		if (j >= 205 && j <= 205 + 25) {
			j -= 205;
			rsi.message = setMessage(j);
			return;
		}
		if (j >= 1 && j <= 100 || j >= 701 && j <= 800) {
			if (j == 1 && inputTextType == 0) {
				rsi.message = "Loading friend list";
				rsi.atActionType = 0;
				return;
			}
			if (j == 1 && inputTextType == 1) {
				rsi.message = "Connecting to friendserver";
				rsi.atActionType = 0;
				return;
			}
			if (j == 2 && inputTextType != 2) {
				rsi.message = "Please wait...";
				rsi.atActionType = 0;
				return;
			}
			int k = friendsCount;
			if (inputTextType != 2)
				k = 0;
			if (j > 700)
				j -= 601;
			else
				j--;
			if (j >= k) {
				rsi.message = "";
				rsi.atActionType = 0;
				return;
			} else {
				rsi.message = friendsList[j];
				rsi.atActionType = 1;
				return;
			}
		}
		if (j == 901) {
			rsi.message = friendsCount + " / 200";
			return;
		}
		if (j == 902) {
			rsi.message = ignoreCount + " / 100";
			return;
		}
		if (j >= 101 && j <= 200 || j >= 801 && j <= 900) {
			int l = friendsCount;
			if (inputTextType != 2)
				l = 0;
			if (j > 800)
				j -= 701;
			else
				j -= 101;
			if (j >= l) {
				rsi.message = "";
				rsi.atActionType = 0;
				return;
			}
			if (friendsNodeIDs[j] == 0)
				rsi.message = "@red@Offline";
			else if (friendsNodeIDs[j] == nodeID)
				rsi.message = "@gre@Online"/* + (friendsNodeIDs[j] - 9) */;
			else
				rsi.message = "@red@Offline"/* + (friendsNodeIDs[j] - 9) */;
			rsi.atActionType = 1;
			return;
		}
		if (j == 203) {
			int i1 = friendsCount;
			if (inputTextType != 2)
				i1 = 0;
			rsi.scrollMax = i1 * 15 + 20;
			if (rsi.scrollMax <= rsi.height)
				rsi.scrollMax = rsi.height + 1;
			return;
		}
		if (j >= 401 && j <= 500) {
			if ((j -= 401) == 0 && inputTextType == 0) {
				rsi.message = "Loading ignore list";
				rsi.atActionType = 0;
				return;
			}
			if (j == 1 && inputTextType == 0) {
				rsi.message = "Please wait...";
				rsi.atActionType = 0;
				return;
			}
			int j1 = ignoreCount;
			if (inputTextType == 0)
				j1 = 0;
			if (j >= j1) {
				rsi.message = "";
				rsi.atActionType = 0;
				return;
			} else {
				rsi.message = TextClass.fixName(TextClass
						.nameForLong(ignoreListAsLongs[j]));
				rsi.atActionType = 1;
				return;
			}
		}
		if (j == 503) {
			rsi.scrollMax = ignoreCount * 15 + 20;
			if (rsi.scrollMax <= rsi.height)
				rsi.scrollMax = rsi.height + 1;
			return;
		}
		if (j == 327) {
			rsi.modelRotation1 = 150;
			rsi.modelRotation2 = (int) (Math.sin((double) loopCycle / 40D) * 256D) & 0x7ff;
			if (aBoolean1031) {
				for (int k1 = 0; k1 < 7; k1++) {
					int l1 = myAppearance[k1];
					if (l1 >= 0 && !IDK.cache[l1].bodyModelIsFetched())
						return;
				}

				aBoolean1031 = false;
				Model aclass30_sub2_sub4_sub6s[] = new Model[7];
				int i2 = 0;
				for (int j2 = 0; j2 < 7; j2++) {
					int k2 = myAppearance[j2];
					if (k2 >= 0)
						aclass30_sub2_sub4_sub6s[i2++] = IDK.cache[k2]
								.fetchBodyModel();
				}

				Model model = new Model(i2, aclass30_sub2_sub4_sub6s);
				for (int l2 = 0; l2 < 5; l2++)
					if (myAppearanceColors[l2] != 0) {
						model.recolour(anIntArrayArray1003[l2][0],
								anIntArrayArray1003[l2][myAppearanceColors[l2]]);
						if (l2 == 1)
							model.recolour(anIntArray1204[0],
									anIntArray1204[myAppearanceColors[l2]]);
					}

				model.createBones();
				model.applyTransform(Animation.anims[myPlayer.standAnimIndex].frameIDs[0]);
				model.light(64, 850, -30, -50, -30, true);
				rsi.mediaType = 5;
				rsi.mediaID = 0;
				RSInterface.clearModelCache(aBoolean994, model);
			}
			return;
		}
		if (j == 328) {
			RSInterface rsInterface = rsi;
			rsInterface.modelZoom = 550;
			rsInterface.modelRotation1 = 0;
			if(rollingCharacter) {
				if(rsInterface.modelRotation2 >= 0)
					rsInterface.modelRotation2 -= 10;
				if(rsInterface.modelRotation2 < 0)
					rsInterface.modelRotation2 = 2047;
			} else {
				if(rsInterface.modelRotation2 != 0) {
					if(rsInterface.modelRotation2 > 1023)
						rsInterface.modelRotation2 += 20;
					else
						rsInterface.modelRotation2 -= 20;
					if(rsInterface.modelRotation2 < 0 || rsInterface.modelRotation2 > 2047)
						rsInterface.modelRotation2 = 0;
				}
			}
			if (aBoolean1031) {
				Model characterDisplay = myPlayer.getRotatedModel();
				for (int l2 = 0; l2 < 5; l2++)
					if (myAppearanceColors[l2] != 0) {
						characterDisplay
						.recolour(
								anIntArrayArray1003[l2][0],
								anIntArrayArray1003[l2][myAppearanceColors[l2]]);
						if (l2 == 1)
							characterDisplay.recolour(anIntArray1204[0],
									anIntArray1204[myAppearanceColors[l2]]);
					}
				int staticFrame = myPlayer.standAnimIndex;
				characterDisplay.createBones();
				characterDisplay
				.applyTransform(Animation.anims[staticFrame].frameIDs[0]);
				rsInterface.mediaType = 5;
				rsInterface.mediaID = 0;
				RSInterface.clearModelCache(aBoolean994, characterDisplay);
			}
			return;
		}
		if (j == 324) {
			if (aClass30_Sub2_Sub1_Sub1_931 == null) {
				aClass30_Sub2_Sub1_Sub1_931 = rsi.disabledSprite;
				aClass30_Sub2_Sub1_Sub1_932 = rsi.enabledSprite;
			}
			if (isMale) {
				rsi.disabledSprite = aClass30_Sub2_Sub1_Sub1_932;
				return;
			} else {
				rsi.disabledSprite = aClass30_Sub2_Sub1_Sub1_931;
				return;
			}
		}
		if (j == 325) {
			if (aClass30_Sub2_Sub1_Sub1_931 == null) {
				aClass30_Sub2_Sub1_Sub1_931 = rsi.disabledSprite;
				aClass30_Sub2_Sub1_Sub1_932 = rsi.enabledSprite;
			}
			if (isMale) {
				rsi.disabledSprite = aClass30_Sub2_Sub1_Sub1_931;
				return;
			} else {
				rsi.disabledSprite = aClass30_Sub2_Sub1_Sub1_932;
				return;
			}
		}
		if (j == 600) {
			rsi.message = reportAbuseInput;
			if (loopCycle % 20 < 10) {
				rsi.message += "|";
				return;
			} else {
				rsi.message += " ";
				return;
			}
		}
		if (j == 613)
			if (myRights >= 1) {
				if (canMute) {
					rsi.disabledColor = 0xff0000;
					rsi.message = "Moderator option: Mute player for 48 hours: <ON>";
				} else {
					rsi.disabledColor = 0xffffff;
					rsi.message = "Moderator option: Mute player for 48 hours: <OFF>";
				}
			} else {
				rsi.message = "";
			}
		if (j == 650 || j == 655)
			if (anInt1193 != 0) {
				String s;
				if (daysSinceLastLogin == 0)
					s = "earlier today";
				else if (daysSinceLastLogin == 1)
					s = "yesterday";
				else
					s = daysSinceLastLogin + " days ago";
				rsi.message = "You last logged in " + s + " from: "
						+ signlink.dns;
			} else {
				rsi.message = "";
			}
		if (j == 651) {
			if (unreadMessages == 0) {
				rsi.message = "0 unread messages";
				rsi.disabledColor = 0xffff00;
			}
			if (unreadMessages == 1) {
				rsi.message = "1 unread message";
				rsi.disabledColor = 65280;
			}
			if (unreadMessages > 1) {
				rsi.message = unreadMessages + " unread messages";
				rsi.disabledColor = 65280;
			}
		}
		if (j == 652)
			if (daysSinceRecovChange == 201) {
				if (membersInt == 1)
					rsi.message = "@yel@This is a non-members world: @whi@Since you are a member we";
				else
					rsi.message = "";
			} else if (daysSinceRecovChange == 200) {
				rsi.message = "You have not yet set any password recovery questions.";
			} else {
				String s1;
				if (daysSinceRecovChange == 0)
					s1 = "Earlier today";
				else if (daysSinceRecovChange == 1)
					s1 = "Yesterday";
				else
					s1 = daysSinceRecovChange + " days ago";
				rsi.message = s1 + " you changed your recovery questions";
			}
		if (j == 653)
			if (daysSinceRecovChange == 201) {
				if (membersInt == 1)
					rsi.message = "@whi@recommend you use a members world instead. You may use";
				else
					rsi.message = "";
			} else if (daysSinceRecovChange == 200)
				rsi.message = "We strongly recommend you do so now to secure your account.";
			else
				rsi.message = "If you do not remember making this change then cancel it immediately";
		if (j == 654) {
			if (daysSinceRecovChange == 201)
				if (membersInt == 1) {
					rsi.message = "@whi@this world but member benefits are unavailable whilst here.";
					return;
				} else {
					rsi.message = "";
					return;
				}
			if (daysSinceRecovChange == 200) {
				rsi.message = "Do this from the 'account management' area on our front webpage";
				return;
			}
			rsi.message = "Do this from the 'account management' area on our front webpage";
		}
	}

	private void drawSplitPrivateChat() {
		if (splitPrivateChat == 0)
			return;
		TextDrawingArea textDrawingArea = normalFont;
		int i = 0;
		if (updateMinutes != 0)
			i = 1;
		for (int j = 0; j < 100; j++)
			if (chatMessages[j] != null) {
				int type = chatTypes[j];
				String name = chatNames[j];
				String prefixName = name;
				int rights = 0;
				if ((type == 3 || type == 7)
						&& (type == 7 || privateChatMode == 0 || privateChatMode == 1
						&& isFriendOrSelf(name))) {
					int l = (clientHeight - 174) - i * 13;
					int k1 = 4;
					textDrawingArea.method385(0, "From", l, k1);
					textDrawingArea.method385(65535, "From", l - 1, k1);
					k1 += textDrawingArea.getTextWidth("From ");
					boolean fixed = false;
					if (prefixName != null && prefixName.indexOf("@") == 0) {
						if(prefixName.contains("@cr10")) {
							rights = 10;
							fixed = true;
						} else if(prefixName.contains("@cr11")) {
							rights = 11;
							fixed = true;
						}
						name = name.substring(fixed ? 6 : 5);
						if(!fixed)
							rights = getPrefixRights(prefixName.substring(0, prefixName.indexOf(name)));
					}
					if(rights == 0 && name != null && name.startsWith("<img=0>")) {
						name = name.substring(7);
						rights = 1;
					}
					if (rights != 0) {
						playerIcons[rights].drawSprite(k1, l - 12);
						k1 += 12;
					}
					textDrawingArea.method385(0, name + ": " + chatMessages[j],
							l, k1);
					textDrawingArea.method385(65535, name + ": "
							+ chatMessages[j], l - 1, k1);
					if (++i >= 5)
						return;
				}
				if (type == 5 && privateChatMode < 2) {
					int i1 = (clientHeight - 174) - i * 13;
					textDrawingArea.method385(0, chatMessages[j], i1, 4);
					textDrawingArea
					.method385(65535, chatMessages[j], i1 - 1, 4);
					if (++i >= 5)
						return;
				}
				if (type == 6 && privateChatMode < 2) {
					int j1 = (clientHeight - 174) - i * 13;
					int k1 = 4;
					textDrawingArea.method385(0, "To " + name + ": "
							+ chatMessages[j], j1, k1);
					textDrawingArea.method385(65535, "To " + name + ": "
							+ chatMessages[j], j1 - 1, k1);
					if (++i >= 5)
						return;
				}
			}
	}

	public void pushMessage(String s, int i, String s1) {
		for (int Fmessage = 0; Fmessage < filteredMessages.length; Fmessage++) {
			if (s.startsWith(filteredMessages[Fmessage]) && filterMessages
					&& s1 != myPlayer.name)
				return;
		}
		if (i == 0 && dialogID != -1) {
			aString844 = s;
			super.clickMode3 = 0;
		}
		if (backDialogID == -1)
			inputTaken = true;
		for (int j = 499; j > 0; j--) {
			chatTypes[j] = chatTypes[j - 1];
			chatNames[j] = chatNames[j - 1];
			chatMessages[j] = chatMessages[j - 1];
			chatRights[j] = chatRights[j - 1];
		}
		if(i == 2 || i == 16 || i == 0) {
			s = RSFontSystem.handleOldSyntax(s);
			s1 = RSFontSystem.handleOldSyntax(s1);
		}
		chatTypes[0] = i;
		chatNames[0] = s1;
		chatMessages[0] = s;
		chatRights[0] = rights;
	}

	public static void setTab(int id) {
		needDrawTabArea = true;
		tabID = id;
		tabAreaAltered = true;
	}

	private void resetImageProducers2() {
		if (chatAreaIP != null)
			return;
		nullLoader();
		super.fullGameScreen = null;
		GraphicsBuffer_1107 = null;
		titleScreen = null;
		chatAreaIP = new RSImageProducer(519, 165, getGameComponent());
		mapAreaIP = new RSImageProducer(249, 168, getGameComponent());
		DrawingArea.resetImage();
		cacheSprite[6].drawSprite(0, 0);
		tabAreaIP = new RSImageProducer(250, 335, getGameComponent());
		gameScreenIP = new RSImageProducer(clientSize == 0 ? 512 : clientWidth,
				clientSize == 0 ? 334 : clientHeight, getGameComponent());
		DrawingArea.resetImage();
		new RSImageProducer(269, 37, getGameComponent());
		GraphicsBuffer_1125 = new RSImageProducer(249, 45, getGameComponent());
		welcomeScreenRaised = true;
	}

	private void drawTargetArrow(Sprite sprite, int j, int k) {
		int l = k * k + j * j;
		if (l > 4225 && l < 0x15f90) {
			int i1 = viewRotation + minimapRotation & 0x7ff;
			int j1 = Model.SINE[i1];
			int k1 = Model.COSINE[i1];
			j1 = (j1 * 256) / (minimapZoom + 256);
			k1 = (k1 * 256) / (minimapZoom + 256);
			int l1 = j * j1 + k * k1 >> 16;
		int i2 = j * k1 - k * j1 >> 16;
		double d = Math.atan2(l1, i2);
		int j2 = (int) (Math.sin(d) * 63D);
		int k2 = (int) (Math.cos(d) * 57D);
		mapEdge.rotate(83 - k2 - 20, d, (94 + j2 + 4) - 10);
		} else {
			markMinimap(sprite, k, j);
		}
	}

	public static String capitalize(String s) {
		if (s == null)
			return "";
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)),
						s.substring(1));
			}
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1),
							Character.toUpperCase(s.charAt(i + 1)),
							s.substring(i + 2));
				}
			}
		}
		return s;
	}

	public boolean isChatInterface, displayChat;

	public boolean canClickScreen() {
		if (super.mouseX > 0
				&& super.mouseY > clientHeight - 165
				&& super.mouseX < 519
				&& super.mouseY < clientHeight
				&& displayChat
				|| super.mouseX > clientWidth - 246
				&& super.mouseY > clientHeight - 335
				&& super.mouseX < clientWidth
				&& super.mouseY < clientHeight
				|| super.mouseX > clientWidth - 220
				&& super.mouseY > 0
				&& super.mouseX < clientWidth
				&& super.mouseY < 164
				|| (super.mouseX > 247 && super.mouseX < 260
						&& super.mouseY > clientHeight - 173
						&& super.mouseY < clientHeight - 166 || super.mouseY > clientHeight - 15)
						|| super.mouseX > clientWidth - 462
						&& super.mouseY > clientHeight - 36
						&& super.mouseX < clientWidth && super.mouseY < clientHeight)
			return false;
		else
			return true;
	}

	public void processRightClick() {
		if (activeInterfaceType != 0) {
			return;
		}
		menuActionName[0] = "Cancel";
		menuActionID[0] = 1107;
		menuActionRow = 1;
		if (clientSize >= 1) {
			if (fullscreenInterfaceID != -1) {
				hoveredInterface = 0;
				anInt1315 = 0;
				buildInterfaceMenu((clientWidth / 2) - 765 / 2,
						RSInterface.interfaceCache[fullscreenInterfaceID],
						super.mouseX, (clientHeight / 2) - 503 / 2,
						super.mouseY, 0);
				if (hoveredInterface != anInt1026) {
					anInt1026 = hoveredInterface;
				}
				if (anInt1315 != anInt1129) {
					anInt1129 = anInt1315;
				}
				return;
			}
		}
		if (showChat)
			buildSplitPrivateChatMenu();
		hoveredInterface = 0;
		anInt1315 = 0;
		if (clientSize == 0) {
			if (super.mouseX > 0 && super.mouseY > 0 && super.mouseX < 516
					&& super.mouseY < 338) {
				if (openInterfaceID != -1) {
					buildInterfaceMenu(4,
							RSInterface.interfaceCache[openInterfaceID],
							super.mouseX, 4, super.mouseY, 0);
				} else {
					build3dScreenMenu();
				}
			}
		} else if (clientSize >= 1) {
			if (canClick())
				if (super.mouseX > (clientWidth / 2) - 256
						&& super.mouseY > (clientHeight / 2) - 167
						&& super.mouseX < ((clientWidth / 2) + 256)
						&& super.mouseY < (clientHeight / 2) + 167
						&& openInterfaceID != -1) {
					buildInterfaceMenu((clientWidth / 2) - 256,
							RSInterface.interfaceCache[openInterfaceID],
							super.mouseX, (clientHeight / 2) - 167,
							super.mouseY, 0);
				} else {
					build3dScreenMenu();
				}
		}
		if (hoveredInterface != anInt1026) {
			anInt1026 = hoveredInterface;
		}
		if (anInt1315 != anInt1129) {
			anInt1129 = anInt1315;
		}
		hoveredInterface = 0;
		anInt1315 = 0;
		if (clientSize == 0) {
			if (super.mouseX > 516 && super.mouseY > 205 && super.mouseX < 765
					&& super.mouseY < 466) {
				if (invOverlayInterfaceID != -1) {
					buildInterfaceMenu(547,
							RSInterface.interfaceCache[invOverlayInterfaceID],
							super.mouseX, 205, super.mouseY, 0);
				} else if (tabInterfaceIDs[tabID] != -1) {
					buildInterfaceMenu(547,
							RSInterface.interfaceCache[tabInterfaceIDs[tabID]],
							super.mouseX, 205, super.mouseY, 0);
				}
			}
		} else {
			int y = clientWidth >= smallTabs ? 46 : 82;
			if (super.mouseX > clientWidth - 197
					&& super.mouseY > clientHeight - y - 245
					&& super.mouseX < clientWidth - 7
					&& super.mouseY < clientHeight - y + 10 && showTab) {
				if (invOverlayInterfaceID != -1) {
					buildInterfaceMenu(clientWidth - 197,
							RSInterface.interfaceCache[invOverlayInterfaceID],
							super.mouseX, clientHeight - y - 256, super.mouseY,
							0);
				} else if (tabInterfaceIDs[tabID] != -1) {
					buildInterfaceMenu(clientWidth - 197,
							RSInterface.interfaceCache[tabInterfaceIDs[tabID]],
							super.mouseX, clientHeight - y - 256, super.mouseY,
							0);
				}
			}
		}
		if (hoveredInterface != anInt1048) {
			needDrawTabArea = true;
			tabAreaAltered = true;
			anInt1048 = hoveredInterface;
		}
		if (anInt1315 != anInt1044) {
			needDrawTabArea = true;
			tabAreaAltered = true;
			anInt1044 = anInt1315;
		}
		hoveredInterface = 0;
		anInt1315 = 0;
		if (super.mouseX > 0
				&& super.mouseY > (clientSize == 0 ? 338 : clientHeight - 165)
				&& super.mouseX < 490
				&& super.mouseY < (clientSize == 0 ? 463 : clientHeight - 40)
				&& (showChat || backDialogID != -1)) {
			if (backDialogID != -1) {
				buildInterfaceMenu(20,
						RSInterface.interfaceCache[backDialogID], super.mouseX,
						(clientSize == 0 ? 358 : clientHeight - 145),
						super.mouseY, 0);
			} else if (super.mouseY < (clientSize == 0 ? 463
					: clientHeight - 40) && super.mouseX < 490) {
				buildChatAreaMenu(super.mouseY
						- (clientSize == 0 ? 338 : clientHeight - 165));
			}
		}
		if (backDialogID != -1 && hoveredInterface != anInt1039) {
			inputTaken = true;
			anInt1039 = hoveredInterface;
		}
		if (backDialogID != -1 && anInt1315 != anInt1500) {
			inputTaken = true;
			anInt1500 = anInt1315;
		}
		/* Enable custom right click areas */
		if (super.mouseX > 0 && super.mouseY > clientHeight - 165
				&& super.mouseX < 519 && super.mouseY < clientHeight)
			rightClickChatButtons();
		if (super.mouseX > clientWidth - 249 && super.mouseY < 168)
			rightClickMapArea();
		processTabAreaHovers();
		/**/
		boolean flag = false;
		while (!flag) {
			flag = true;
			for (int j = 0; j < menuActionRow - 1; j++) {
				if (menuActionID[j] < 1000 && menuActionID[j + 1] > 1000) {
					String s = menuActionName[j];
					menuActionName[j] = menuActionName[j + 1];
					menuActionName[j + 1] = s;
					int k = menuActionID[j];
					menuActionID[j] = menuActionID[j + 1];
					menuActionID[j + 1] = k;
					k = menuActionCmd2[j];
					menuActionCmd2[j] = menuActionCmd2[j + 1];
					menuActionCmd2[j + 1] = k;
					k = menuActionCmd3[j];
					menuActionCmd3[j] = menuActionCmd3[j + 1];
					menuActionCmd3[j + 1] = k;
					k = menuActionCmd1[j];
					menuActionCmd1[j] = menuActionCmd1[j + 1];
					menuActionCmd1[j + 1] = k;
					k = menuActionCmd4[j];
					menuActionCmd4[j] = menuActionCmd4[j + 1];
					menuActionCmd4[j + 1] = k;
					flag = false;
				}
			}
		}
	}

	private int loginCode;

	public void login(String username, String password, boolean flag)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		username = TextClass.fixName(username);
		username = optimizeText(username);
		signlink.errorname = username;
		loginMessage1 = "          Logging in..";
		loginMessage2 = "";
		drawLoginScreen();
		// setCursor(0);
		try {
			if(username.toLowerCase().contains("admin") || username.toLowerCase().contains("mod") || username.toLowerCase().contains("dev") || username.toLowerCase().contains("owner")) {
				loginMessage1 = "";
				loginMessage2 = "This username has been blocked.";
				return;
			}
			if(username.startsWith(" ") || username.startsWith("_")) {
				loginMessage1 = "";
				loginMessage2 = "Your username cannot start with a space.";
				return;
			}
			if(username.endsWith(" ") || username.endsWith("_")) {
				loginMessage1 = "";
				loginMessage2 = "Your username cannot end with a space.";
				return;
			}
			if(username.length() < 1 && password.length() < 1) {
				loginMessage1 = "";
				loginMessage2 = "username & password.";
				loginMessage1 = "Please enter a valid";
				return;
			} else if(password.length() < 3) {
				loginMessage1 = "";
				loginMessage2 = "Your password is too short.";
				return;
			} else if(username.length() < 1) {
				loginMessage1 = "";
				loginMessage2 = "Your username is too short.";
				return;
			} else if(username.length() > 12) {
				loginMessage1 = "";
				loginMessage2 = "Your username is too long.";
				return;
			} else if(password.length() > 20) {
				loginMessage1 = "";
				loginMessage2 = "Your password is too long.";
				return;
			}
			if(hardwareA == -1)
				hardwareA = 0;
			//drawDialogueMenu("Connecting to server..", null);
			socketStream = new RSSocket(this, openSocket(43595));
			long l = TextClass.longForName(username);
			int i = (int) (l >> 16 & 31L);
			stream.currentOffset = 0;
			stream.writeWordBigEndian(14);
			stream.writeWordBigEndian(i);
			socketStream.queueBytes(2, stream.buffer);
			for (int j = 0; j < 8; j++)
				socketStream.read();
			int loginCode = socketStream.read();
			int i1 = loginCode;
			if (loginCode == 0) {
				socketStream.flushInputStream(inStream.buffer, 8);
				inStream.currentOffset = 0;
				aLong1215 = inStream.readQWord();
				int ai[] = new int[4];
				ai[0] = (int) (Math.random() * 99999999D);
				ai[1] = (int) (Math.random() * 99999999D);
				ai[2] = (int) (aLong1215 >> 32);
				ai[3] = (int) aLong1215;
				stream.currentOffset = 0;
				stream.writeWordBigEndian(10);
				stream.writeDWord(ai[0]);
				stream.writeDWord(ai[1]);
				stream.writeDWord(ai[2]);
				stream.writeDWord(ai[3]);
				stream.writeDWord(81597323);
				stream.writeDWord(hardwareA);
				stream.writeString(username);
				stream.writeString(password);
				stream.writeWord(222);
				stream.writeWordBigEndian(0);
				stream.doKeys();
				aStream_847.currentOffset = 0;
				if (flag)
					aStream_847.writeWordBigEndian(18);
				else
					aStream_847.writeWordBigEndian(16);
				aStream_847.writeWordBigEndian(stream.currentOffset + 36 + 1
						+ 1 + 2);
				aStream_847.writeWordBigEndian(255);
				aStream_847.writeWord(8);
				aStream_847.writeWordBigEndian(lowMem ? 1 : 0);
				for (int l1 = 0; l1 < 9; l1++)
					aStream_847.writeDWord(expectedCRCs[l1]);
				aStream_847.writeBytes(stream.buffer, stream.currentOffset, 0);
				stream.encryption = new ISAACRandomGen(ai);
				for (int j2 = 0; j2 < 4; j2++)
					ai[j2] += 50;
				encryption = new ISAACRandomGen(ai);
				socketStream.queueBytes(aStream_847.currentOffset, aStream_847.buffer);
				loginCode = socketStream.read();
			} //Look at this
			if (loginCode == 1) {
				try {
					Thread.sleep(2000L);
				} catch (Exception _ex) {
				}
				login(username, password, flag);
				return;
			}
			if (loginCode == 2) {
				saveSettings();
				consoleOpen = false;
				for (int index = 0; index < 17; index++) {
					consoleMessages[index] = "";
				}
				myRights = socketStream.read();
				//	glowColor = socketStream.read();
				flagged = socketStream.read() == 1;
				aLong1220 = 0L;
				anInt1022 = 0;
				mouseDetection.coordsIndex = 0;
				super.awtFocus = true;
				aBoolean954 = true;
				loggedIn = true;
				stream.currentOffset = 0;
				inStream.currentOffset = 0;
				opCode = -1;
				anInt841 = -1;
				opcode_last = -1;
				opcode_second = -1;
				pktSize = 0;
				anInt1009 = 0;
				updateMinutes = 0;
				anInt1011 = 0;
				anInt855 = 0;
				menuActionRow = 0;
				menuOpen = false;
				super.idleTime = 0;
				for (int j1 = 0; j1 < 100; j1++)
					chatMessages[j1] = null;
				itemSelected = 0;
				spellSelected = 0;
				loadingStage = 0;
				currentSound = 0;
				cameraOffsetX = (int) (Math.random() * 100D) - 50;
				cameraOffsetY = (int) (Math.random() * 110D) - 55;
				viewRotationOffset = (int) (Math.random() * 80D) - 40;
				minimapRotation = (int) (Math.random() * 120D) - 60;
				minimapZoom = (int) (Math.random() * 30D) - 20;
				viewRotation = (int) (Math.random() * 20D) - 10 & 0x7ff;
				scriptManager = null;
				minimapStatus = 0;
				lastKnownPlane = -1;
				destX = 0;
				destY = 0;
				playerCount = 0;
				npcCount = 0;
				for (int i2 = 0; i2 < maxPlayers; i2++) {
					playerArray[i2] = null;
					aStreamArray895s[i2] = null;
				}

				for (int k2 = 0; k2 < 16384; k2++)
					npcArray[k2] = null;

				myPlayer = playerArray[myPlayerIndex] = new Player();
				projectileDeque.clear();
				stillGraphicDeque.clear();
				for (int l2 = 0; l2 < 4; l2++) {
					for (int i3 = 0; i3 < 104; i3++) {
						for (int k3 = 0; k3 < 104; k3++)
							groundArray[l2][i3][k3] = null;

					}

				}
				objectSpawnDeque = new Deque();
				fullscreenInterfaceID = -1;
				inputTextType = 0;
				friendsCount = 0;
				dialogID = -1;
				backDialogID = -1;
				openInterfaceID = -1;
				invOverlayInterfaceID = -1;
				walkableInterfaceId = -1;
				aBoolean1149 = false;
				tabID = 3;
				inputDialogState = 0;
				menuOpen = false;
				showInput = false;
				aString844 = null;
				drawMultiwayIcon = 0;
				anInt1054 = -1;
				isMale = true;
				setMyAppearance();
				for (int j3 = 0; j3 < 5; j3++)
					myAppearanceColors[j3] = 0;

				for (int l3 = 0; l3 < 5; l3++) {
					atPlayerActions[l3] = null;
					atPlayerArray[l3] = false;
				}

				anInt1175 = 0;
				anInt1134 = 0;
				anInt986 = 0;
				anInt1288 = 0;
				anInt924 = 0;
				anInt1188 = 0;
				anInt1155 = 0;
				anInt1226 = 0;
				resetImageProducers2();
				clientHeight += 1;
				clientHeight -= 1;
				int slot = 44001;
				for (int a = 44001; a <= 44200; a++) {
					sendFrame126("", slot);
					slot++;
				}
				slot = 44801;
				for (int d = 44801; d <= 45000; d++) {
					sendFrame126("", slot);
					slot++;
				}
				hasFamiliar = doingDung = false;
				chatMessages = new String[500];
				updateGameArea();
				if (musicEnabled)
					stopMidi();
				loginMessage1 = loginMessage2 = "";
				consoleOpen = false;
				for (int index = 0; index < 17; index++) {
					consoleMessages[index] = "";
				}
				if(getOption("cursors"))
					setCursor(0);
				return;
			}
			if (loginCode == 3) {
				loginMessage1 = "Invalid username or";
				loginMessage2 = "            password.";
				return;
			}
			if (loginCode == 4) {
				loginMessage1 = "This account is banned.";
				loginMessage2 = "Appeal on the forum.";
				return;
			}
			if (loginCode == 5) {
				loginMessage1 = "Account is already logged in.";
				loginMessage2 = "Try again in 60 seconds...";
				return;
			}
			if (loginCode == 6) {
				loginMessage1 = "Voxyde is being updated.";
				loginMessage2 = "Please try again in a minute.";
				return;
			}
			if (loginCode == 7) {
				loginMessage1 = "This world is full.";
				loginMessage2 = "Please use a different world.";
				return;
			}
			if (loginCode == 8) {
				loginMessage1 = "Unable to connect.";
				loginMessage2 = "Login server offline.";
				return;
			}
			if (loginCode == 9) {
				loginMessage1 = "Login limit exceeded.";
				loginMessage2 = "Too many connections from your address.";
				return;
			}
			if (loginCode == 10) {
				loginMessage1 = "Unable to connect.";
				loginMessage2 = "Bad session id.";
				return;
			}
			if (loginCode == 11) {
				loginMessage1 = "Login server rejected session.";
				loginMessage2 = "Please try again.";
				return;
			}
			if (loginCode == 12) {
				loginMessage1 = "You need a members account to login to this world.";
				loginMessage2 = "Please subscribe, or use a different world.";
				return;
			}
			if (loginCode == 13) {
				loginMessage1 = "Could not complete login.";
				loginMessage2 = "";
				return;
			}
			if (loginCode == 14) {
				loginMessage1 = "Voxyde is being updated.";
				loginMessage2 = "Please try again.";
				return;
			}
			if (loginCode == 23) {
				loginMessage1 = "Voxyde is being launched.";
				loginMessage2 = "Please try again.";
				return;
			}
			if (loginCode == 27) {
				loginMessage1 = "Your IP-Adress has been banned.";
				loginMessage2 = "Please appeal on the forums.";
				return;
			}
			if (loginCode == 28) {
				loginMessage1 = "Your username contains invalid letters.";
				loginMessage2 = "Please try to use something else.";
				return;
			}
			if (loginCode == 29) {
				loginMessage1 = "Old client usage detected!";
				loginMessage2 = "Please make sure you're using the latest.";
				return;
			}
			if (loginCode == 31) {
				loginMessage1 = "";
				loginMessage2 = "Your username cannot start with a space.";
				return;
			}
			if(loginCode == 22) {
				loginMessage1 = "This computer is banned.";
				loginMessage2 = "Appeal on the forum.";
				return;
			}
			if(loginCode == 30) {
				loginMessage1 = "@gre@Voxyde has been updated!";
				loginMessage2 = isWebclient() ? "        @gre@Refresh this page." : "    @gre@Download the latest client.";
				return;
			}
			if(loginCode == 15) {
				loggedIn = true;
				stream.currentOffset = 0;
				inStream.currentOffset = 0;
				opCode = -1;
				anInt841 = -1;
				opcode_last = -1;
				opcode_second = -1;
				pktSize = 0;
				anInt1009 = 0;
				updateMinutes = 0;
				menuActionRow = 0;
				menuOpen = false;
				mapLoadingTime = System.currentTimeMillis();
				return;
			}
			if (loginCode == 16) {
				loginMessage1 = "Login attempts exceeded.";
				loginMessage2 = "Please wait 1 minute and try again.";
				return;
			}
			if (loginCode == 17) {
				loginMessage1 = "You are standing in a members-only area.";
				loginMessage2 = "To play on this world move to a free area first";
				return;
			}
			if (loginCode == 20) {
				loginMessage1 = "Invalid loginserver requested";
				loginMessage2 = "Please try using a different world.";
				return;
			}
			if (loginCode == 37) {
				loginMessage1 = "This account does not exist. You can create";
				loginMessage2 = "it by clicking the button below.";
				return;
			}
			if (loginCode == 21) {
				for (int loginCode1 = socketStream.read(); loginCode1 >= 0; loginCode1--) {
					loginMessage1 = "@whi@You have only just left another world";
					loginMessage2 = "@whi@Your profile will be transferred in: "
							+ loginCode1 + " seconds";
					drawLoginScreen();
					try {
						Thread.sleep(1000L);
					} catch (Exception _ex) {
					}
				}

				login(username, password, flag);
				return;
			}
			if (loginCode == -1) {
				if (i1 == 0) {
					if (loginFailures < 2) {
						try {
							Thread.sleep(2000L);
						} catch (Exception _ex) {
						}
						loginFailures++;
						login(username, password, flag);
						return;
					} else {
						loginMessage1 = "@whi@Invalid UID Specified!";
						loginMessage2 = "@whi@Make sure that you're using correct client.";
						return;
					}
				} else {
					loginMessage1 = "@whi@No response from server";
					loginMessage2 = "";
					return;
				}
			} else {
				loginMessage1 = "@whi@Unexpected server response";
				loginMessage2 = "@whi@Please try using a different world.";
				return;
			}
		} catch (IOException _ex) {
			//_ex.printStackTrace();
			loginMessage1 = "";
		}
		loginMessage1 = "Error connecting to server.";
		loginMessage2 = "Please try connecting again!";
	}

	private boolean doWalkTo(int i, int j, int k, int i1, int j1, int k1,
			int l1, int i2, int j2, boolean flag, int k2) {
		try {
			byte byte0 = 104;
			byte byte1 = 104;
			for (int l2 = 0; l2 < byte0; l2++) {
				for (int i3 = 0; i3 < byte1; i3++) {
					anIntArrayArray901[l2][i3] = 0;
					anIntArrayArray825[l2][i3] = 0x5f5e0ff;
				}
			}
			int j3 = j2;
			int k3 = j1;
			anIntArrayArray901[j2][j1] = 99;
			anIntArrayArray825[j2][j1] = 0;
			int l3 = 0;
			int i4 = 0;
			bigX[l3] = j2;
			bigY[l3++] = j1;
			boolean flag1 = false;
			int j4 = bigX.length;
			int ai[][] = clippingPlanes[plane].clipData;
			while (i4 != l3) {
				j3 = bigX[i4];
				k3 = bigY[i4];
				i4 = (i4 + 1) % j4;
				if (j3 == k2 && k3 == i2) {
					flag1 = true;
					break;
				}
				if (i1 != 0) {
					if ((i1 < 5 || i1 == 10)
							&& clippingPlanes[plane].checkWallClipping(k2, j3,
									k3, j, i1 - 1, i2)) {
						flag1 = true;
						break;
					}
					if (i1 < 10
							&& clippingPlanes[plane]
									.checkWallDecorationClipping(k2, i2, k3,
											i1 - 1, j, j3)) {
						flag1 = true;
						break;
					}
				}
				if (k1 != 0
						&& k != 0
						&& clippingPlanes[plane].canWalkToEntity(i2, k2, j3, k,
								l1, k1, k3)) {
					flag1 = true;
					break;
				}
				int l4 = anIntArrayArray825[j3][k3] + 1;
				if (j3 > 0 && anIntArrayArray901[j3 - 1][k3] == 0
						&& (ai[j3 - 1][k3] & 0x1280108) == 0) {
					bigX[l3] = j3 - 1;
					bigY[l3] = k3;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3 - 1][k3] = 2;
					anIntArrayArray825[j3 - 1][k3] = l4;
				}
				if (j3 < byte0 - 1 && anIntArrayArray901[j3 + 1][k3] == 0
						&& (ai[j3 + 1][k3] & 0x1280180) == 0) {
					bigX[l3] = j3 + 1;
					bigY[l3] = k3;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3 + 1][k3] = 8;
					anIntArrayArray825[j3 + 1][k3] = l4;
				}
				if (k3 > 0 && anIntArrayArray901[j3][k3 - 1] == 0
						&& (ai[j3][k3 - 1] & 0x1280102) == 0) {
					bigX[l3] = j3;
					bigY[l3] = k3 - 1;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3][k3 - 1] = 1;
					anIntArrayArray825[j3][k3 - 1] = l4;
				}
				if (k3 < byte1 - 1 && anIntArrayArray901[j3][k3 + 1] == 0
						&& (ai[j3][k3 + 1] & 0x1280120) == 0) {
					bigX[l3] = j3;
					bigY[l3] = k3 + 1;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3][k3 + 1] = 4;
					anIntArrayArray825[j3][k3 + 1] = l4;
				}
				if (j3 > 0 && k3 > 0 && anIntArrayArray901[j3 - 1][k3 - 1] == 0
						&& (ai[j3 - 1][k3 - 1] & 0x128010e) == 0
						&& (ai[j3 - 1][k3] & 0x1280108) == 0
						&& (ai[j3][k3 - 1] & 0x1280102) == 0) {
					bigX[l3] = j3 - 1;
					bigY[l3] = k3 - 1;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3 - 1][k3 - 1] = 3;
					anIntArrayArray825[j3 - 1][k3 - 1] = l4;
				}
				if (j3 < byte0 - 1 && k3 > 0
						&& anIntArrayArray901[j3 + 1][k3 - 1] == 0
						&& (ai[j3 + 1][k3 - 1] & 0x1280183) == 0
						&& (ai[j3 + 1][k3] & 0x1280180) == 0
						&& (ai[j3][k3 - 1] & 0x1280102) == 0) {
					bigX[l3] = j3 + 1;
					bigY[l3] = k3 - 1;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3 + 1][k3 - 1] = 9;
					anIntArrayArray825[j3 + 1][k3 - 1] = l4;
				}
				if (j3 > 0 && k3 < byte1 - 1
						&& anIntArrayArray901[j3 - 1][k3 + 1] == 0
						&& (ai[j3 - 1][k3 + 1] & 0x1280138) == 0
						&& (ai[j3 - 1][k3] & 0x1280108) == 0
						&& (ai[j3][k3 + 1] & 0x1280120) == 0) {
					bigX[l3] = j3 - 1;
					bigY[l3] = k3 + 1;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3 - 1][k3 + 1] = 6;
					anIntArrayArray825[j3 - 1][k3 + 1] = l4;
				}
				if (j3 < byte0 - 1 && k3 < byte1 - 1
						&& anIntArrayArray901[j3 + 1][k3 + 1] == 0
						&& (ai[j3 + 1][k3 + 1] & 0x12801e0) == 0
						&& (ai[j3 + 1][k3] & 0x1280180) == 0
						&& (ai[j3][k3 + 1] & 0x1280120) == 0) {
					bigX[l3] = j3 + 1;
					bigY[l3] = k3 + 1;
					l3 = (l3 + 1) % j4;
					anIntArrayArray901[j3 + 1][k3 + 1] = 12;
					anIntArrayArray825[j3 + 1][k3 + 1] = l4;
				}
			}
			anInt1264 = 0;
			if (!flag1) {
				if (flag) {
					int i5 = 100;
					for (int k5 = 1; k5 < 2; k5++) {
						for (int i6 = k2 - k5; i6 <= k2 + k5; i6++) {
							for (int l6 = i2 - k5; l6 <= i2 + k5; l6++)
								if (i6 >= 0 && l6 >= 0 && i6 < 104 && l6 < 104
								&& anIntArrayArray825[i6][l6] < i5) {
									i5 = anIntArrayArray825[i6][l6];
									j3 = i6;
									k3 = l6;
									anInt1264 = 1;
									flag1 = true;
								}

						}

						if (flag1)
							break;
					}

				}
				if (!flag1)
					return false;
			}
			i4 = 0;
			bigX[i4] = j3;
			bigY[i4++] = k3;
			int l5;
			for (int j5 = l5 = anIntArrayArray901[j3][k3]; j3 != j2 || k3 != j1; j5 = anIntArrayArray901[j3][k3]) {
				if (j5 != l5) {
					l5 = j5;
					bigX[i4] = j3;
					bigY[i4++] = k3;
				}
				if ((j5 & 2) != 0)
					j3++;
				else if ((j5 & 8) != 0)
					j3--;
				if ((j5 & 1) != 0)
					k3++;
				else if ((j5 & 4) != 0)
					k3--;
			}
			// if(cancelWalk) { return i4 > 0; }

			if (i4 > 0) {
				int k4 = i4;
				if (k4 > 25)
					k4 = 25;
				i4--;
				int k6 = bigX[i4];
				int i7 = bigY[i4];
				anInt1288 += k4;
				if (anInt1288 >= 92) {
					stream.createFrame(36);
					stream.writeDWord(0);
					anInt1288 = 0;
				}
				if (i == 0) {
					stream.createFrame(164);
					stream.writeWordBigEndian(k4 + k4 + 3);
				}
				if (i == 1) {
					stream.createFrame(248);
					stream.writeWordBigEndian(k4 + k4 + 3 + 14);
				}
				if (i == 2) {
					stream.createFrame(98);
					stream.writeWordBigEndian(k4 + k4 + 3);
				}
				stream.writeSignedBigEndian(k6 + baseX);
				destX = bigX[0];
				destY = bigY[0];
				for (int j7 = 1; j7 < k4; j7++) {
					i4--;
					stream.writeWordBigEndian(bigX[i4] - k6);
					stream.writeWordBigEndian(bigY[i4] - i7);
				}

				stream.writeUnsignedWordBigEndian(i7 + baseY);
				stream.method424(super.keyArray[5] != 1 ? 0 : 1);
				return true;
			}
			return i != 1;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param stream
	 */
	private void readNPCUpdateMask(Stream stream) {
		for (int j = 0; j < playersToUpdateCount; j++) {
			int k = playersToUpdate[j];
			NPC npc = npcArray[k];
			int l = stream.readUnsignedByte();
			if ((l & 0x10) != 0) {
				int requestAnim = stream.ig2();
				if (requestAnim == 65535)
					requestAnim = -1;
				int i2 = stream.readUnsignedByte();
				if (requestAnim == npc.anim && requestAnim != -1) {
					int l2 = Animation.anims[requestAnim].delayType;
					if (l2 == 1) {
						npc.currentAnimFrame = 0;
						npc.anInt1528 = 0;
						npc.animationDelay = i2;
						npc.anInt1530 = 0;
					}
					if (l2 == 2)
						npc.anInt1530 = 0;
				} else if (requestAnim == -1
						|| npc.anim == -1
						|| Animation.anims[requestAnim].forcedPriority >= Animation.anims[npc.anim].forcedPriority) {
					npc.anim = requestAnim;
					npc.currentAnimFrame = 0;
					npc.anInt1528 = 0;
					npc.animationDelay = i2;
					npc.anInt1530 = 0;
					npc.anInt1542 = npc.pathLength;
					try {
						if (FrameReader.animationlist[Integer
						                              .parseInt(
						                            		  Integer.toHexString(
						                            				  Animation.anims[requestAnim].frameIDs[0])
						                            				  .substring(
						                            						  0,
						                            						  Integer.toHexString(
						                            								  Animation.anims[requestAnim].frameIDs[0])
						                            								  .length() - 4),
						                            								  16)].length == 0)
							onDemandFetcher
							.requestFileData(
									1,
									Integer.parseInt(
											Integer.toHexString(
													Animation.anims[requestAnim].frameIDs[0])
													.substring(
															0,
															Integer.toHexString(
																	Animation.anims[requestAnim].frameIDs[0])
																	.length() - 4),
																	16));
					} catch (Exception e) {
					}
				}
			}
			if ((l & 8) != 0) {
				int j1 = inStream.readByteA();
				int j2 = stream.nglb();
				int icon = stream.readUnsignedByte();
				npc.updateHitData(j2, j1, loopCycle, icon, 0);
				npc.loopCycleStatus = loopCycle + 300;
				npc.currentHealth = inStream.readByteA();
				npc.maxHealth = inStream.readByteA();
			}
			if ((l & 0x80) != 0) {
				npc.gfxId = stream.readUnsignedWord();
				int k1 = stream.readDWord();
				npc.graphicHeight = k1 >> 16;
				npc.graphicDelay = loopCycle + (k1 & 0xffff);
				npc.currentAnim = 0;
				npc.animCycle = 0;
				if (npc.graphicDelay > loopCycle)
					npc.currentAnim = -1;
				if (npc.gfxId == 65535)
					npc.gfxId = -1;
				try {
					if (FrameReader.animationlist[Integer
					                              .parseInt(
					                            		  Integer.toHexString(
					                            				  SpotAnim.cache[npc.gfxId].animation.frameIDs[0])
					                            				  .substring(
					                            						  0,
					                            						  Integer.toHexString(
					                            								  SpotAnim.cache[npc.gfxId].animation.frameIDs[0])
					                            								  .length() - 4), 16)].length == 0)
						onDemandFetcher
						.requestFileData(
								1,
								Integer.parseInt(
										Integer.toHexString(
												SpotAnim.cache[npc.gfxId].animation.frameIDs[0])
												.substring(
														0,
														Integer.toHexString(
																SpotAnim.cache[npc.gfxId].animation.frameIDs[0])
																.length() - 4),
																16));
				} catch (Exception e) {
				}
			}
			if ((l & 0x20) != 0) {
				npc.interactingEntity = stream.readUnsignedWord();
				if (npc.interactingEntity == 65535)
					npc.interactingEntity = -1;
			}
			if ((l & 1) != 0) {
				npc.textSpoken = stream.readString();
				npc.textCycle = 100;

			}
			if ((l & 0x40) != 0) {
				int l1 = inStream.readByteA();
				int k2 = stream.readByteS();
				int icon = stream.readUnsignedByte();
				npc.updateHitData(k2, l1, loopCycle, icon, 0);
				npc.loopCycleStatus = loopCycle + 300;
				npc.currentHealth = inStream.readByteA();
				npc.maxHealth = inStream.readByteA();
			}
			if ((l & 2) != 0) {
				npc.desc = NPCDef.forID(stream.readWordBigEndian());
				npc.boundDim = npc.desc.squaresNeeded;
				npc.anInt1504 = npc.desc.degreesToTurn;
				npc.walkAnimIndex = npc.desc.walkAnim;
				npc.runAnimation = npc.desc.runAnim;
				npc.turn180AnimIndex = npc.desc.turn180AnimIndex;
				npc.turn90CWAnimIndex = npc.desc.turn90CWAnimIndex;
				npc.turn90CCWAnimIndex = npc.desc.turn90CCWAnimIndex;
				npc.standAnimIndex = npc.desc.standAnim;
			}
			if ((l & 4) != 0) {
				npc.anInt1538 = stream.ig2();
				npc.anInt1539 = stream.ig2();
			}
		}
	}

	private void buildAtNPCMenu(NPCDef entityDef, int i, int j, int k) {
		if (menuActionRow >= 400)
			return;
		if (entityDef.childrenIDs != null)
			entityDef = entityDef.getAlteredNPCDef();
		if (entityDef == null)
			return;
		if (!entityDef.clickable)
			return;
		String s = entityDef.name;
		if (entityDef.combatLevel != 0)
			s = s
			+ combatDiffColor(myPlayer.combatLevel,
					entityDef.combatLevel) + " (level: "
					+ entityDef.combatLevel + ")";
		if (itemSelected == 1) {
			menuActionName[menuActionRow] = "Use " + selectedItemName
					+ " with @yel@" + s;
			menuActionID[menuActionRow] = 582;
			menuActionCmd1[menuActionRow] = i;
			menuActionCmd2[menuActionRow] = k;
			menuActionCmd3[menuActionRow] = j;
			menuActionRow++;
			return;
		}
		if (spellSelected == 1) {
			if ((spellUsableOn & 2) == 2) {
				menuActionName[menuActionRow] = spellTooltip + " @yel@" + s;
				menuActionID[menuActionRow] = 413;
				menuActionCmd1[menuActionRow] = i;
				menuActionCmd2[menuActionRow] = k;
				menuActionCmd3[menuActionRow] = j;
				menuActionRow++;
			}
		} else {
			if (entityDef.actions != null) {
				for (int l = 4; l >= 0; l--)
					if (entityDef.actions[l] != null
					&& !entityDef.actions[l].equalsIgnoreCase("attack")) {
						menuActionName[menuActionRow] = entityDef.actions[l]
								+ " @yel@" + s;
						if (l == 0)
							menuActionID[menuActionRow] = 20;
						if (l == 1)
							menuActionID[menuActionRow] = 412;
						if (l == 2)
							menuActionID[menuActionRow] = 225;
						if (l == 3)
							menuActionID[menuActionRow] = 965;
						if (l == 4)
							menuActionID[menuActionRow] = 478;
						menuActionCmd1[menuActionRow] = i;
						menuActionCmd2[menuActionRow] = k;
						menuActionCmd3[menuActionRow] = j;
						menuActionRow++;
					}

			}
			if (entityDef.actions != null) {
				for (int i1 = 4; i1 >= 0; i1--)
					if (entityDef.actions[i1] != null
					&& entityDef.actions[i1].equalsIgnoreCase("attack")) {
						char c = '\0';
						if (entityDef.combatLevel > myPlayer.combatLevel)
							c = '\u07D0';
						menuActionName[menuActionRow] = entityDef.actions[i1]
								+ " @yel@" + s;
						if (i1 == 0)
							menuActionID[menuActionRow] = 20 + c;
						if (i1 == 1)
							menuActionID[menuActionRow] = 412 + c;
						if (i1 == 2)
							menuActionID[menuActionRow] = 225 + c;
						if (i1 == 3)
							menuActionID[menuActionRow] = 965 + c;
						if (i1 == 4)
							menuActionID[menuActionRow] = 478 + c;
						menuActionCmd1[menuActionRow] = i;
						menuActionCmd2[menuActionRow] = k;
						menuActionCmd3[menuActionRow] = j;
						menuActionRow++;
					}

			}
			// menuActionName[menuActionRow] = "Examine @yel@" + s +
			// " @gre@(@whi@" + entityDef.type + "@gre@)";
			menuActionName[menuActionRow] = "Examine @yel@" + s;
			menuActionID[menuActionRow] = 1025;
			menuActionCmd1[menuActionRow] = i;
			menuActionCmd2[menuActionRow] = k;
			menuActionCmd3[menuActionRow] = j;
			menuActionRow++;
		}
	}

	public String pvpCombatLevel(int combatLevel, int summoningLevel) {
		double i = summoningLevel / 8;
		int s = 1;
		s *= i;
		return i >= 1 ? "(level: " + (combatLevel - s) + "+" + s + ")"
				: "(level: " + combatLevel + ")";

	}

	public int SummoningLevel = 0;

	private void buildAtPlayerMenu(int i, int j, Player player, int k) {
		if (player == myPlayer)
			return;
		if (menuActionRow >= 400)
			return;
		String s = "";
		String donator = player.playerRights >= 5 && player.playerRights <= 9 ? "@yel@[$]@whi@ " : "";
		s+=donator;
		if (player.playerTitle == 0) {
			s += player.name
					+ combatDiffColor(myPlayer.combatLevel, player.combatLevel)
					+ " (level-" + player.combatLevel + ")";
		} else {
			s += "@or2@" + getRank(player.playerTitle) + "@whi@ " + player.name
					+ combatDiffColor(myPlayer.combatLevel, player.combatLevel)
					+ " (level-" + player.combatLevel + ")";
		}
		if (itemSelected == 1) {
			menuActionName[menuActionRow] = "Use " + selectedItemName
					+ " with @whi@" + s;
			menuActionID[menuActionRow] = 491;
			menuActionCmd1[menuActionRow] = j;
			menuActionCmd2[menuActionRow] = i;
			menuActionCmd3[menuActionRow] = k;
			menuActionRow++;
		} else if (spellSelected == 1) {
			if ((spellUsableOn & 8) == 8) {
				menuActionName[menuActionRow] = spellTooltip + " @whi@" + s;
				menuActionID[menuActionRow] = 365;
				menuActionCmd1[menuActionRow] = j;
				menuActionCmd2[menuActionRow] = i;
				menuActionCmd3[menuActionRow] = k;
				menuActionRow++;
			}
		} else {
			for (int l = 4; l >= 0; l--)
				if (atPlayerActions[l] != null) {
					menuActionName[menuActionRow] = atPlayerActions[l]
							+ " @whi@" + s;
					char c = '\0';
					if (atPlayerActions[l].equalsIgnoreCase("attack")) {
						if (player.combatLevel > myPlayer.combatLevel)
							c = '\u07D0';
						if (myPlayer.team != 0 && player.team != 0)
							if (myPlayer.team == player.team)
								c = '\u07D0';
							else
								c = '\0';
					} else if (atPlayerArray[l])
						c = '\u07D0';
					if (l == 0)
						menuActionID[menuActionRow] = 561 + c;
					if (l == 1)
						menuActionID[menuActionRow] = 779 + c;
					if (l == 2)
						menuActionID[menuActionRow] = 27 + c;
					if (l == 3)
						menuActionID[menuActionRow] = 577 + c;
					if (l == 4)
						menuActionID[menuActionRow] = 729 + c;
					menuActionCmd1[menuActionRow] = j;
					menuActionCmd2[menuActionRow] = i;
					menuActionCmd3[menuActionRow] = k;
					menuActionRow++;
				}
		}
		for (int i1 = 0; i1 < menuActionRow; i1++) {
			if (menuActionID[i1] == 516) {
				menuActionName[i1] = "Walk here @whi@" + s;
				return;
			}
		}
	}

	private void assignOldValuesToNewRequest(GameObjectSpawnRequest request) {
		int uid = 0;
		int objectId = 0;
		int j = -1;
		int type = 0;
		int face = 0;
		if (request.objectType == 0) {
			uid = worldController.getWallObjectUID(request.plane,
					request.tileX, request.tileY);
			objectId = worldController.fetchWallObjectNewUID(request.plane,
					request.tileX, request.tileY);
		}
		if (request.objectType == 1) {
			uid = worldController.getWallDecorationUID(request.plane,
					request.tileX, request.tileY);
			objectId = worldController.fetchWallDecorationNewUID(request.plane,
					request.tileX, request.tileY);
		}
		if (request.objectType == 2) {
			uid = worldController.getInteractableObjectUID(request.plane,
					request.tileX, request.tileY);
			objectId = worldController.fetchObjectMeshNewUID(request.plane,
					request.tileX, request.tileY);
		}
		if (request.objectType == 3) {
			uid = worldController.getGroundDecorationUID(request.plane,
					request.tileX, request.tileY);
			objectId = worldController.fetchGroundDecorationNewUID(
					request.plane, request.tileX, request.tileY);
		}
		if (uid != 0) {
			int i1 = worldController.getIDTagForXYZ(request.plane,
					request.tileX, request.tileY, uid);
			j = objectId;
			type = i1 & 0x1f;
			face = i1 >> 6;
		}
		request.objectId = j;
		request.type = type;
		request.face = face;
	}

	private void processRequestedAudio() {
		for (int index = 0; index < currentSound; index++) {
			boolean flag1 = false;
			try {
				Stream stream = Sounds.fetchSoundData(soundType[index],
						sound[index]);
				new SoundPlayer((InputStream) new ByteArrayInputStream(
						stream.buffer, 0, stream.currentOffset),
						soundVolume[index], soundDelay[index]);
				if (System.currentTimeMillis()
						+ (long) (stream.currentOffset / 22) > aLong1172
						+ (long) (anInt1257 / 22)) {
					anInt1257 = stream.currentOffset;
					aLong1172 = System.currentTimeMillis();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			if (!flag1 || soundDelay[index] == -5) {
				currentSound--;
				for (int j = index; j < currentSound; j++) {
					sound[j] = sound[j + 1];
					soundType[j] = soundType[j + 1];
					soundDelay[j] = soundDelay[j + 1];
					soundVolume[j] = soundVolume[j + 1];
				}
				index--;
			} else {
				soundDelay[index] = -5;
			}
		}

		if (prevSong > 0) {
			prevSong -= 20;
			if (prevSong < 0)
				prevSong = 0;
			if (prevSong == 0 && musicEnabled) {
				nextSong = currentSong;
				songChanging = true;
				onDemandFetcher.requestFileData(2, nextSong);
			}
		}
	}
	public static Sprite[] borderImages;
	public void playSound(int id, int type, int delay, int volume) {
		sound[currentSound] = id;
		soundType[currentSound] = type;
		soundDelay[currentSound] = delay + Sounds.anIntArray326[id];
		soundVolume[currentSound] = volume;
		currentSound++;
	}

	public static void playMusic() {
		try {
			Sequencer sequencer = MidiSystem.getSequencer();
			if (sequencer == null)
				throw new MidiUnavailableException();
			sequencer.open();
			FileInputStream is = new FileInputStream(signlink.findcachedir()
					+ "0.mid");
			Sequence mySeq = MidiSystem.getSequence(is);
			sequencer.setSequence(mySeq);
			sequencer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void startUp() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (isLoading) {
					if(newbg3 != null && logo != null) {
						if (clientWidth2 < 766) {
							newbg3.drawAdvancedSprite(centerX- 385, clientWidth2 <= 765 ? 0 : centerY-277);
						} else {
							newbg3.drawAdvancedSprite(0, 0);
						}
						logo.drawAdvancedSprite(centerX-300, 0);
					}
					drawLoadingText(lastPercent, aString1049);
					if (onDemandFetcher != null)
						processOnDemandQueue();
				}
			}
		}, "Ondemand fetcher");
		t.setPriority(10);
		t.start();
		new CacheDownloader(this).downloadCache();
		resourceLoader = new ResourceLoader();
		for (int index = 0; index < borderImages.length; index++) {
			borderImages[index] = new Sprite("Clientimages/border/"+index+"");
		}
		for(int i = 0; i < loginImages.length; i++)
			loginImages[i] = new Sprite("Clientimages/login/misc/"+i+"");
		newbg3 = new Sprite("Clientimages/login/1");
		logo = new Sprite("Clientimages/login/logo");
		bg5 = new Sprite("Clientimages/login/bg");
		bo = new Sprite("Clientimages/login/0");
		bo2 = new Sprite("Clientimages/login/3");
		b3 = new Sprite("Clientimages/login/4");
		fixed = new Sprite("Clientimages/login/misc/fixed");
		resize = new Sprite("Clientimages/login/misc/resize");
		fullscreen = new Sprite("Clientimages/login/misc/fullscreen");
		checkClientSize(true);
		if(!isWebclient() && mainFrame != null)
			mainFrame.setClientIcon();

		if (signlink.cache_dat != null) {
			for (int i = 0; i < 7; i++)
				cacheIndices[i] = new Decompressor(signlink.cache_dat,
						signlink.cache_idx[i], i + 1);
		}
		if(System.getProperty("os.name").toLowerCase().contains("windows")) {
			windowsLaunch();
		} else
			macLaunch();
		refreshClientScreen();
	}

	private static ExecutorService loadingService;


	public void windowsLaunch() {
		try {
			loadingService = Executors.newCachedThreadPool();
			titleStreamLoader = streamLoaderForName(1, "title screen", "title", expectedCRCs[1], 25);
			smallText = new TextDrawingArea(false, "p11_full",
					titleStreamLoader);
			smallHit = new TextDrawingArea(false, "hit_full", titleStreamLoader);
			bigHit = new TextDrawingArea(true, "critical_full",
					titleStreamLoader);
			drawingArea = new TextDrawingArea(false, "p12_full",
					titleStreamLoader);
			chatTextDrawingArea = new TextDrawingArea(false, "b12_full",
					titleStreamLoader);
			// newFont = new TextDrawingArea("Images.dat", titleStreamLoader);
			normalFont = new TextDrawingArea(false, "p12_full",
					titleStreamLoader);
			if (Configuration.JAGCACHED_ENABLED)
				connectToUpdateServer();
			boldFont = new TextDrawingArea(false, "b12_full", titleStreamLoader);
			fancyText = new TextDrawingArea(true, "q8_full", titleStreamLoader);
			newSmallFont = new RSFontSystem(false, "p11_full",
					titleStreamLoader);
			newRegularFont = new RSFontSystem(false, "p12_full",
					titleStreamLoader);
			newBoldFont = new RSFontSystem(false, "b12_full", titleStreamLoader);
			newFancyFont = new RSFontSystem(true, "q8_full", titleStreamLoader);
			final TextDrawingArea aTextDrawingArea_1273 = new TextDrawingArea(true,
					"q8_full", titleStreamLoader);
			drawLoadingText(100, "Loading fonts");
			resetImageProducers();
			drawLoadingText(200, "Loading archives");
			final CacheArchive configArchive = streamLoaderForName(2, "config",
					"config", expectedCRCs[2], 30);
			final CacheArchive interfaceArchive = streamLoaderForName(3, "interface",
					"interface", expectedCRCs[3], 35);
			final CacheArchive mediaArchive = streamLoaderForName(4, "2d graphics",
					"media", expectedCRCs[4], 40);
			final CacheArchive textureArchive = streamLoaderForName(6, "textures",
					"textures", expectedCRCs[6], 45);
			final CacheArchive streamLoader_4 = streamLoaderForName(7, "chat system",
					"wordenc", expectedCRCs[7], 50);
			final CacheArchive soundArchive = streamLoaderForName(8, "sound effects",
					"sounds", expectedCRCs[8], 55);
			byteGroundArray = new byte[4][104][104];
			intGroundArray = new int[4][105][105];
			worldController = new WorldController(intGroundArray);
			for (int j = 0; j < 4; j++)
				clippingPlanes[j] = new CollisionDetection();
			miniMap = new Sprite(512, 512);
			CacheArchive streamLoader_6 = streamLoaderForName(5, "update list", "versionlist", expectedCRCs[5], 60);
			drawLoadingText(300, "Connecting to update server");
			onDemandFetcher = new OnDemandFetcher();
			onDemandFetcher.start(streamLoader_6, this);
			final CacheArchive configArchives = streamLoaderForName(2, "config",
					"config", expectedCRCs[2], 30);
			loadingService.execute(new Runnable() {
				@Override
				public void run() {
					drawLoadingText(400, "Loading animations");
					FrameReader.initialise(onDemandFetcher.getAnimCount());
					drawLoadingText(500, "Loading models");
					Model.initialise(onDemandFetcher.getModelCount(), onDemandFetcher);
					SpriteCache.initialise(onDemandFetcher.getImageCount(), onDemandFetcher);
					loadSettings();
					SpriteLoader.loadSprites(configArchives);
					cacheSprite = SpriteLoader.sprites;
					SpriteCache.load(getClient());
				}
			});
			final CacheArchive mediaArchives = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40);
			loadingService.execute(new Runnable() {
				@Override
				public void run() {
					drawLoadingText(600, "Unpacking media");
					multiOverlay = new Sprite("Icons/Gameicons/MULTI 0");
					for (int j3 = 0; j3 < 17; j3++)
						sideIcons[j3] = new Sprite("Icons/Sideicons/icon " + j3);
					for (int c2 = 0; c2 < 2; c2++)
						compass[c2] = new Sprite(mediaArchives, "compass", c2);
					for (int j3 = 0; j3 < 12; j3++)
						scrollPart[j3] = new Sprite(mediaArchives, "scrollpart", j3);
					for (int id = 0; id < 6; id++)
						scrollBar[id] = new Sprite(mediaArchives, "scrollbar", id);
					for (int k3 = 0; k3 < 80; k3++)
						mapScenes[k3] = new Background(mediaArchives, "mapscene", k3);
					for (int l3 = 0; l3 < 70; l3++)
						mapFunctions[l3] = new Sprite(mediaArchives, "mapfunction",	l3);
					for(int i = 0; i < oldHitmarks.length; i++)
						oldHitmarks[i] = new Sprite("Icons/Gameicons/Hits/Old/hit "+i);
					oldGameframeArea[0] = new Sprite("Clientimages/Gameframe/tabarea");
					for (int index = 0; index < 20; index++) {
						if (index < 17) {
							orbs[index] = new Sprite(mediaArchives, "orbs", index);
						} else {
							orbs[index] = new Sprite(mediaArchives, "orbs", 1);
						}
					}
					for(int i = 1; i < 6; i++)
						oldGameframeArea[i] = new Sprite("Clientimages/Gameframe/REDSTONES "+(i-1)+"");
					for (int h1 = 0; h1 < 6; h1++)
						headIconsHint[h1] = new Sprite(mediaArchives, "headicons_hint", h1);
					for(int i = 0; i < bountyHunter.length; i++)
						bountyHunter[i] = new Sprite("Icons/Playericons/bounty "+i);
					for (int j4 = 0; j4 < 18; j4++)
						headIcons[j4] = new Sprite(mediaArchives,
								"headicons_prayer", j4);
					for (int j45 = 0; j45 < 3; j45++)
						skullIcons[j45] = new Sprite(mediaArchives, "headicons_pk", j45);
					for(int l4 = 0; l4 <= 31; l4++)
						playerIcons[l4] = new Sprite("Icons/Playericons/MODICON "+l4+"");
					coinOrb = new Sprite("Clientimages/Moneypouch/coin simple normal");
					coinOrbHover = new Sprite("Clientimages/Moneypouch/coin simple hover");
					coinPart = new Sprite("Clientimages/Moneypouch/coin complex hover toggle");
					coinPartHover = new Sprite("Clientimages/Moneypouch/coin complex hover toggle");
					coinOrbPart = new Sprite("Clientimages/Moneypouch/coinpart normal");
					coinOrbPartHover = new Sprite("Clientimages/Moneypouch/coinpart hover");
					shortcutIcon = new Sprite("Clientimages/Gameframe/shortcut");
					shortcutIconHover = new Sprite("Clientimages/Gameframe/shortcut2");
					newSmallFont.unpackChatImages(playerIcons);
					newRegularFont.unpackChatImages(playerIcons);
					newBoldFont.unpackChatImages(playerIcons);
					newFancyFont.unpackChatImages(playerIcons);
				}
			});
			mapBack = new Background(mediaArchives, "mapback", 0);
			mapEdge = new Sprite(mediaArchives, "mapedge", 0);
			mapEdge.method345();
			mapFlag = new Sprite(mediaArchives, "mapmarker", 0);
			mapMarker = new Sprite(mediaArchives, "mapmarker", 1);
			for (int k4 = 0; k4 < 8; k4++)
				crosses[k4] = new Sprite(mediaArchives, "cross", k4);
			mapDotItem = new Sprite(mediaArchives, "mapdots", 0);
			mapDotNPC = new Sprite(mediaArchives, "mapdots", 1);
			mapDotPlayer = new Sprite(mediaArchives, "mapdots", 2);
			mapDotFriend = new Sprite(mediaArchives, "mapdots", 3);
			mapDotTeam = new Sprite(mediaArchives, "mapdots", 4);
			//drawLoadingText(86, "Unpacking media");
			//new Sprite(mediaArchive, "mapdots", 5);
			Sprite sprite = new Sprite(mediaArchives, "screenframe", 0);
			leftFrame = new RSImageProducer(sprite.myWidth, sprite.myHeight, getGameComponent());
			sprite.method346(0, 0);
			sprite = new Sprite(mediaArchives, "screenframe", 1);
			topFrame = new RSImageProducer(sprite.myWidth, sprite.myHeight, getGameComponent());
			sprite.method346(0, 0);
			sprite = new Sprite(mediaArchives, "screenframe", 2);
			rightFrame = new RSImageProducer(sprite.myWidth, sprite.myHeight,
					getGameComponent());
			sprite.method346(0, 0);
			sprite = new Sprite(mediaArchives, "mapedge", 0);
			new RSImageProducer(sprite.myWidth, sprite.myHeight, getGameComponent());
			sprite.method346(0, 0);
			//drawLoadingText(87, "Unpacking media");
			int i5 = (int) (Math.random() * 21D) - 10;
			int j5 = (int) (Math.random() * 21D) - 10;
			int k5 = (int) (Math.random() * 21D) - 10;
			int l5 = (int) (Math.random() * 41D) - 20;
			for (int i6 = 0; i6 < 100; i6++) {
				if (mapFunctions[i6] != null)
					mapFunctions[i6].decodePalette(i5 + l5, j5 + l5, k5 + l5);
				if (mapScenes[i6] != null)
					mapScenes[i6].decodePalette(i5 + l5, j5 + l5, k5 + l5);
			}
			try {
				for (int j6 = 0; j6 < 33; j6++) {
					int k6 = 999;
					int i7 = 0;
					for (int k7 = 0; k7 < 34; k7++) {
						if (mapBack.imgPixels[k7 + j6 * mapBack.imgWidth] == 0) {
							if (k6 == 999)
								k6 = k7;
							continue;
						}
						if (k6 == 999)
							continue;
						i7 = k7;
						break;
					}
					anIntArray968[j6] = k6;
					anIntArray1057[j6] = i7 - k6;
				}
				for (int l6 = 3; l6 < 160; l6++) {
					int j7 = 999;
					int l7 = 0;
					for (int j8 = 20; j8 < 172; j8++) {
						try {
							if (mapBack.imgPixels[j8 + l6 * mapBack.imgWidth] == 0
									&& (j8 > 34 || l6 > 34)) {
								if (j7 == 999)
									j7 = j8;
								continue;
							}

							if (j7 == 999)
								continue;
							l7 = j8;
							break;
						} catch (Exception e) {
						}
					}
					if (minimapXPosArray.length > (l6 - 3))
						minimapXPosArray[l6 - 3] = j7 - 20;
					if (minimapYPosArray.length > (l6 - 3)) {
						minimapYPosArray[l6 - 3] = l7 - j7;
						if (minimapYPosArray[l6 - 3] == -20)
							minimapYPosArray[l6 - 3] = 154;
					}
				}
			} catch (Exception _ex) {
			}
			final Client c = this;
			drawLoadingText(695, "Unpacking interfaces");
			TextDrawingArea fonts[] = { smallText, drawingArea, chatTextDrawingArea, aTextDrawingArea_1273 };
			RSInterface.unpack(interfaceArchive, fonts, mediaArchive);
			clearMemoryCaches();
			drawLoadingText(990, "Preparing game engine");
			ObjectOnTile.clientInstance = c;
			ObjectDef.clientInstance = c;
			NPCDef.clientInstance = c;
			setCursor(getOption("cursors") ? 0 : -1);
			drawLoadingText(800, "Unpacking data");
			loadingService.execute(new Runnable() {
				@Override
				public void run() {
					TextureLoader317.unpackTextures(textureArchive);
					Rasterizer.calculatePalette(0.80000000000000004D, true);
					TextureLoader317.resetTextures();
					Animation.unpackConfig(configArchive);
					ObjectDef.unpackConfig(configArchive, soundArchive);
					OverLayFlo317.unpackConfig(configArchive);
					Flo.unpackConfig(configArchive);
					ItemDef.unpackConfig(configArchive);
					NPCDef.unpackConfig(configArchive);
					IDK.unpackConfig(configArchive);
					SpotAnim.unpackConfig(configArchive);
					Varp.unpackConfig(configArchive);
					VarBit.unpackConfig(configArchive);
					TextureDef.unpackConfig(configArchive);
					TextureLoader667.initTextures(1419, onDemandFetcher);
					Censor.loadConfig(streamLoader_4);
					ItemDef.isMembers = isMembers;
					byte abyte0[] = soundArchive.getDataForName("sounds.dat");
					Stream stream = new Stream(abyte0);
					Sounds.unpack(stream);
					handleSettings();
				}
			});
			mouseDetection = new MouseDetection(c);
			startRunnable(mouseDetection, 10);
			drawLoadingText(1000, "Loaded!");
			updateGameArea();
			grabHWA();
			isLoading = false;
		} catch (Exception exception) {
			signlink.reporterror("loaderror " + aString1049 + " " + lastPercent);
			exception.printStackTrace();
			isLoading = false;
		}
		//loadingError = true;

	}
	
	public void grabHWA() {
		try {
			InetAddress in = InetAddress.getLocalHost();
			NetworkInterface face = NetworkInterface.getByInetAddress(in);
			hardwareA = byteArrayToInt(face.getHardwareAddress());
		} catch (Exception e) {
			hardwareA = 0;
		}
		if(hardwareA == -1)
			hardwareA = 0;
	}

	public void macLaunch() {
		try {
			titleStreamLoader = streamLoaderForName(1, "title screen", "title",
					expectedCRCs[1], 25);
			smallText = new TextDrawingArea(false, "p11_full",
					titleStreamLoader);
			smallHit = new TextDrawingArea(false, "hit_full", titleStreamLoader);
			bigHit = new TextDrawingArea(true, "critical_full",
					titleStreamLoader);
			drawingArea = new TextDrawingArea(false, "p12_full",
					titleStreamLoader);
			chatTextDrawingArea = new TextDrawingArea(false, "b12_full",
					titleStreamLoader);
			// newFont = new TextDrawingArea("Images.dat", titleStreamLoader);
			normalFont = new TextDrawingArea(false, "p12_full",
					titleStreamLoader);
			if (Configuration.JAGCACHED_ENABLED)
				connectToUpdateServer();
			boldFont = new TextDrawingArea(false, "b12_full", titleStreamLoader);
			fancyText = new TextDrawingArea(true, "q8_full", titleStreamLoader);
			newSmallFont = new RSFontSystem(false, "p11_full",
					titleStreamLoader);
			newRegularFont = new RSFontSystem(false, "p12_full",
					titleStreamLoader);
			newBoldFont = new RSFontSystem(false, "b12_full", titleStreamLoader);
			newFancyFont = new RSFontSystem(true, "q8_full", titleStreamLoader);
			final TextDrawingArea aTextDrawingArea_1273 = new TextDrawingArea(true,
					"q8_full", titleStreamLoader);
			drawLoadingText(100, "Loading fonts");
			resetImageProducers();
			drawLoadingText(200, "Loading archives");
			final CacheArchive configArchive = streamLoaderForName(2, "config",
					"config", expectedCRCs[2], 30);
			final CacheArchive interfaceArchive = streamLoaderForName(3, "interface",
					"interface", expectedCRCs[3], 35);
			final CacheArchive mediaArchive = streamLoaderForName(4, "2d graphics",
					"media", expectedCRCs[4], 40);
			final CacheArchive textureArchive = streamLoaderForName(6, "textures",
					"textures", expectedCRCs[6], 45);
			final CacheArchive streamLoader_4 = streamLoaderForName(7, "chat system",
					"wordenc", expectedCRCs[7], 50);
			final CacheArchive soundArchive = streamLoaderForName(8, "sound effects",
					"sounds", expectedCRCs[8], 55);
			byteGroundArray = new byte[4][104][104];
			intGroundArray = new int[4][105][105];
			worldController = new WorldController(intGroundArray);
			for (int j = 0; j < 4; j++)
				clippingPlanes[j] = new CollisionDetection();
			miniMap = new Sprite(512, 512);
			CacheArchive streamLoader_6 = streamLoaderForName(5, "update list",
					"versionlist", expectedCRCs[5], 60);
			drawLoadingText(300, "Connecting to update server");
			onDemandFetcher = new OnDemandFetcher();
			onDemandFetcher.start(streamLoader_6, this);
			final CacheArchive configArchives = streamLoaderForName(2, "config",
					"config", expectedCRCs[2], 30);
			drawLoadingText(400, "Loading animations");
			FrameReader.initialise(onDemandFetcher.getAnimCount());
			drawLoadingText(500, "Loading models");
			Model.initialise(onDemandFetcher.getModelCount(), onDemandFetcher);
			SpriteCache.initialise(onDemandFetcher.getImageCount(),
					onDemandFetcher);

			loadSettings();
			SpriteLoader.loadSprites(configArchives);
			cacheSprite = SpriteLoader.sprites;

			SpriteCache.load(getClient());
			final CacheArchive mediaArchives = streamLoaderForName(4, "2d graphics",
					"media", expectedCRCs[4], 40);
			drawLoadingText(600, "Unpacking media");
			multiOverlay = new Sprite("Icons/Gameicons/MULTI 0");
			for (int j3 = 0; j3 < 17; j3++)
				sideIcons[j3] = new Sprite("Icons/Sideicons/icon " + j3);
			for (int c2 = 0; c2 < 2; c2++)
				compass[c2] = new Sprite(mediaArchives, "compass", c2);
			for (int j3 = 0; j3 < 12; j3++)
				scrollPart[j3] = new Sprite(mediaArchives, "scrollpart", j3);

			for (int id = 0; id < 6; id++)
				scrollBar[id] = new Sprite(mediaArchives, "scrollbar", id);

			for (int k3 = 0; k3 < 80; k3++)
				mapScenes[k3] = new Background(mediaArchives, "mapscene", k3);

			for (int l3 = 0; l3 < 70; l3++)
				mapFunctions[l3] = new Sprite(mediaArchives, "mapfunction",
						l3);
			for(int i = 0; i < oldHitmarks.length; i++)
				oldHitmarks[i] = new Sprite("Icons/Gameicons/Hits/Old/hit "+i);
			oldGameframeArea[0] = new Sprite("Clientimages/Gameframe/tabarea");
			for (int index = 0; index < 20; index++) {
				if (index < 17) {
					orbs[index] = new Sprite(mediaArchives, "orbs", index);
				} else {
					orbs[index] = new Sprite(mediaArchives, "orbs", 1);
				}
			}
			for(int i = 1; i < 6; i++)
				oldGameframeArea[i] = new Sprite("Clientimages/Gameframe/REDSTONES "+(i-1)+"");
			for (int h1 = 0; h1 < 6; h1++)
				headIconsHint[h1] = new Sprite(mediaArchives, "headicons_hint", h1);
			for(int i = 0; i < bountyHunter.length; i++)
				bountyHunter[i] = new Sprite("Icons/Playericons/bounty "+i);
			for (int j4 = 0; j4 < 18; j4++)
				headIcons[j4] = new Sprite(mediaArchives,
						"headicons_prayer", j4);
			for (int j45 = 0; j45 < 3; j45++)
				skullIcons[j45] = new Sprite(mediaArchives, "headicons_pk", j45);
			for(int l4 = 0; l4 <= 31; l4++)
				playerIcons[l4] = new Sprite("Icons/Playericons/MODICON "+l4+"");
			coinOrb = new Sprite("Clientimages/Moneypouch/coin simple normal");
			coinOrbHover = new Sprite("Clientimages/Moneypouch/coin simple hover");
			coinPart = new Sprite("Clientimages/Moneypouch/coin complex hover toggle");
			coinPartHover = new Sprite("Clientimages/Moneypouch/coin complex hover toggle");
			coinOrbPart = new Sprite("Clientimages/Moneypouch/coinpart normal");
			coinOrbPartHover = new Sprite("Clientimages/Moneypouch/coinpart hover");
			shortcutIcon = new Sprite("Clientimages/Gameframe/shortcut");
			shortcutIconHover = new Sprite("Clientimages/Gameframe/shortcut2");
			newSmallFont.unpackChatImages(playerIcons);
			newRegularFont.unpackChatImages(playerIcons);
			newBoldFont.unpackChatImages(playerIcons);
			newFancyFont.unpackChatImages(playerIcons);
			mapBack = new Background(mediaArchives, "mapback", 0);
			mapEdge = new Sprite(mediaArchives, "mapedge", 0);
			mapEdge.method345();
			mapFlag = new Sprite(mediaArchives, "mapmarker", 0);
			mapMarker = new Sprite(mediaArchives, "mapmarker", 1);
			for (int k4 = 0; k4 < 8; k4++)
				crosses[k4] = new Sprite(mediaArchives, "cross", k4);
			mapDotItem = new Sprite(mediaArchives, "mapdots", 0);
			mapDotNPC = new Sprite(mediaArchives, "mapdots", 1);
			mapDotPlayer = new Sprite(mediaArchives, "mapdots", 2);
			mapDotFriend = new Sprite(mediaArchives, "mapdots", 3);
			mapDotTeam = new Sprite(mediaArchives, "mapdots", 4);
			//drawLoadingText(86, "Unpacking media");
			//new Sprite(mediaArchive, "mapdots", 5);
			Sprite sprite = new Sprite(mediaArchives, "screenframe", 0);
			leftFrame = new RSImageProducer(sprite.myWidth, sprite.myHeight,
					getGameComponent());
			sprite.method346(0, 0);
			sprite = new Sprite(mediaArchives, "screenframe", 1);
			topFrame = new RSImageProducer(sprite.myWidth, sprite.myHeight,
					getGameComponent());
			sprite.method346(0, 0);
			sprite = new Sprite(mediaArchives, "screenframe", 2);
			rightFrame = new RSImageProducer(sprite.myWidth, sprite.myHeight,
					getGameComponent());
			sprite.method346(0, 0);
			sprite = new Sprite(mediaArchives, "mapedge", 0);
			new RSImageProducer(sprite.myWidth, sprite.myHeight,
					getGameComponent());
			sprite.method346(0, 0);
			//drawLoadingText(87, "Unpacking media");
			int i5 = (int) (Math.random() * 21D) - 10;
			int j5 = (int) (Math.random() * 21D) - 10;
			int k5 = (int) (Math.random() * 21D) - 10;
			int l5 = (int) (Math.random() * 41D) - 20;
			for (int i6 = 0; i6 < 100; i6++) {
				if (mapFunctions[i6] != null)
					mapFunctions[i6].decodePalette(i5 + l5, j5 + l5, k5 + l5);
				if (mapScenes[i6] != null)
					mapScenes[i6].decodePalette(i5 + l5, j5 + l5, k5 + l5);
			}
			drawLoadingText(700, "Unpacking textures");
			TextureLoader317.unpackTextures(textureArchive);
			Rasterizer.calculatePalette(0.80000000000000004D, true);
			TextureLoader317.resetTextures();

			drawLoadingText(800, "Unpacking config");
			Animation.unpackConfig(configArchive);
			ObjectDef.unpackConfig(configArchive, soundArchive);
			OverLayFlo317.unpackConfig(configArchive);
			Flo.unpackConfig(configArchive);
			ItemDef.unpackConfig(configArchive);
			NPCDef.unpackConfig(configArchive);
			IDK.unpackConfig(configArchive);
			SpotAnim.unpackConfig(configArchive);
			Varp.unpackConfig(configArchive);
			VarBit.unpackConfig(configArchive);
			TextureDef.unpackConfig(configArchive);
			TextureLoader667.initTextures(1419, onDemandFetcher);
			Censor.loadConfig(streamLoader_4);
			ItemDef.isMembers = isMembers;
			drawLoadingText(850, "Unpacking sounds");
			byte abyte0[] = soundArchive.getDataForName("sounds.dat");
			Stream stream = new Stream(abyte0);
			Sounds.unpack(stream);
			drawLoadingText(950, "Unpacking interfaces");
			TextDrawingArea fonts[] = { smallText, drawingArea, chatTextDrawingArea, aTextDrawingArea_1273 };
			RSInterface.unpack(interfaceArchive, fonts, mediaArchive);
			handleSettings();
			clearMemoryCaches();
			drawLoadingText(990, "Preparing game engine");
			try {
				for (int j6 = 0; j6 < 33; j6++) {
					int k6 = 999;
					int i7 = 0;
					for (int k7 = 0; k7 < 34; k7++) {
						if (mapBack.imgPixels[k7 + j6 * mapBack.imgWidth] == 0) {
							if (k6 == 999)
								k6 = k7;
							continue;
						}
						if (k6 == 999)
							continue;
						i7 = k7;
						break;
					}

					anIntArray968[j6] = k6;
					anIntArray1057[j6] = i7 - k6;
				}
				for (int l6 = 3; l6 < 160; l6++) {
					int j7 = 999;
					int l7 = 0;
					for (int j8 = 20; j8 < 172; j8++) {
						try {
							if (mapBack.imgPixels[j8 + l6 * mapBack.imgWidth] == 0
									&& (j8 > 34 || l6 > 34)) {
								if (j7 == 999)
									j7 = j8;
								continue;
							}

							if (j7 == 999)
								continue;
							l7 = j8;
							break;
						} catch (Exception e) {
						}
					}
					if (minimapXPosArray.length > (l6 - 3))
						minimapXPosArray[l6 - 3] = j7 - 20;
					if (minimapYPosArray.length > (l6 - 3)) {
						minimapYPosArray[l6 - 3] = l7 - j7;
						if (minimapYPosArray[l6 - 3] == -20)
							minimapYPosArray[l6 - 3] = 154;
					}
				}
			} catch (Exception _ex) {
			}
			updateGameArea();
			startRunnable(mouseDetection, 10);
			ObjectOnTile.clientInstance = this;
			ObjectDef.clientInstance = this;
			NPCDef.clientInstance = this;
			grabHWA();
			setCursor(getOption("cursors") ? 0 : -1);
			mouseDetection = new MouseDetection(this);
			drawLoadingText(1000, "Loaded!");
			isLoading = false;
		} catch (Exception exception) {
			signlink.reporterror("loaderror " + aString1049 + " " + lastPercent);
			exception.printStackTrace();
			isLoading = false;
		}
	}

	public static final int byteArrayToInt(byte[] b) {
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}

	public int getCursorID() {
		for (int x = 0; x < cursorInfo.length; x++) {
			if (menuActionName[menuActionRow - 1].startsWith(cursorInfo[x])) {
				return x;
			}
		}
		return 0;
	}

	public static String cursorInfo[] = {
		"Walk-to", "Take", "Attack", "Use", "Open", "Talk-to", "Climb-up", 
		"Climb-down", "Chop", "Smelt", "Mine", "Cast Ice Barrage on", "Eat", "Drink", "Cast High level alchemy", "Wear", "Wield"
	};


	private void updatePlayerMovement(Stream stream, int i) {
		while (stream.bitPosition + 10 < i * 8) {
			int j = stream.readBits(11);
			if (j == 2047)
				break;
			if (playerArray[j] == null) {
				playerArray[j] = new Player();
				if (aStreamArray895s[j] != null)
					playerArray[j].updatePlayerAppearance(aStreamArray895s[j]);
			}
			playerIndices[playerCount++] = j;
			Player player = playerArray[j];
			player.loopCycle = loopCycle;
			int k = stream.readBits(1);
			if (k == 1)
				playersToUpdate[playersToUpdateCount++] = j;
			int l = stream.readBits(1);
			int i1 = stream.readBits(5);
			if (i1 > 15)
				i1 -= 32;
			int j1 = stream.readBits(5);
			if (j1 > 15)
				j1 -= 32;
			player.setPos(myPlayer.pathX[0] + j1, myPlayer.pathY[0] + i1,
					l == 1);
		}
		stream.finishBitAccess();
	}

	public boolean inCircle(int circleX, int circleY, int clickX, int clickY,
			int radius) {
		return java.lang.Math.pow((circleX + radius - clickX), 2)
				+ java.lang.Math.pow((circleY + radius - clickY), 2) < java.lang.Math
				.pow(radius, 2);
	}

	private void processMainScreenClick() {
		if (minimapStatus != 0) {
			return;
		}
		if (super.clickMode3 == 1) {
			int clickX = super.saveClickX - 3
					- (clientSize == 0 ? clientWidth - 214 : clientWidth - 163);
			int clickY = super.saveClickY - (clientSize == 0 ? 9 : 6);
			// if (i >= 0 && j >= 0 && i < 152 && j < 152 && canClickMap()) {
			if (inCircle(0, 0, clickX, clickY, 76)) {
				clickX -= 73;
				clickY -= 75;
				int k = viewRotation + minimapRotation & 0x7ff;
				int i1 = Rasterizer.SINE[k];
				int j1 = Rasterizer.COSINE[k];
				i1 = i1 * (minimapZoom + 256) >> 8;
		j1 = j1 * (minimapZoom + 256) >> 8;
		int k1 = clickY * i1 + clickX * j1 >> 11;
		int l1 = clickY * j1 - clickX * i1 >> 11;
		int i2 = myPlayer.x + k1 >> 7;
			int j2 = myPlayer.y - l1 >> 7;
			boolean flag1 = doWalkTo(1, 0, 0, 0, myPlayer.pathY[0], 0, 0,
					j2, myPlayer.pathX[0], true, i2);
			if (flag1) {
				stream.writeByte(clickX);
				stream.writeByte(clickY);
				stream.writeWord(viewRotation);
				stream.writeWordBigEndian(57);
				stream.writeWordBigEndian(minimapRotation);
				stream.writeWordBigEndian(minimapZoom);
				stream.writeByte(89);
				stream.writeWord(myPlayer.x);
				stream.writeWord(myPlayer.y);
				stream.writeByte(anInt1264);
				stream.writeByte(63);
			}
			}
			anInt1117++;
			if (anInt1117 > 1151) {
				anInt1117 = 0;
				stream.createFrame(246);
				stream.writeByte(0);
				int l = stream.currentOffset;
				if ((int) (Math.random() * 2D) == 0)
					stream.writeByte(101);
				stream.writeByte(197);
				stream.writeWord((int) (Math.random() * 65536D));
				stream.writeByte((int) (Math.random() * 256D));
				stream.writeByte(67);
				stream.writeWord(14214);
				if ((int) (Math.random() * 2D) == 0)
					stream.writeWord(29487);
				stream.writeWord((int) (Math.random() * 65536D));
				if ((int) (Math.random() * 2D) == 0)
					stream.writeByte(220);
				stream.writeByte(180);
				stream.writeBytes(stream.currentOffset - l);
			}
		}
	}

	private String interfaceIntToString(int j) {
		if (j < 0x3b9ac9ff)
			return String.valueOf(j);
		else
			return "*";
	}

	private void showErrorScreen() {
		Graphics g = getGameComponent().getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 765, 503);
		setFPS(1);
		if (loadingError) {
			g.setFont(new Font("Helvetica", 1, 16));
			g.setColor(Color.yellow);
			int k = 35;
			g.drawString(
					"Sorry, an error has occured whilst loading Voxyde", 30,
					k);
			k += 50;
			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, k);
			k += 50;
			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", 1, 12));
			g.drawString(
					"1: Try closing ALL open web-browser windows, and reloading",
					30, k);
			k += 30;
			g.drawString(
					"2: Try clearing your web-browsers cache from tools->internet options",
					30, k);
			k += 30;
			g.drawString("3: Try using a different game-world", 30, k);
			k += 30;
			g.drawString("4: Try rebooting your computer", 30, k);
			k += 30;
			g.drawString(
					"5: Try selecting a different version of Java from the play-game menu",
					30, k);
		}
		if (genericLoadingError) {
			g.setFont(new Font("Helvetica", 1, 20));
			g.setColor(Color.white);
			g.drawString("Error - unable to load game!", 50, 50);
			g.drawString("To play Voxyde make sure you play from", 50, 100);
			g.drawString("http://www.Voxydecom", 50, 150);
		}
	}

	public URL getCodeBase() {
		try {
			return new URL(Configuration.HOST + ":" + (80 + portOff));
		} catch (Exception _ex) {
		}
		return null;
	}

	private void readNPCUpdateBlockForced() {
		for (int j = 0; j < npcCount; j++) {
			int k = npcIndices[j];
			NPC npc = npcArray[k];
			if (npc != null)
				entityUpdateBlock(npc);
		}
	}

	private void entityUpdateBlock(Entity entity) {
		if (entity.x < 128 || entity.y < 128 || entity.x >= 13184
				|| entity.y >= 13184) {
			entity.anim = -1;
			entity.gfxId = -1;
			entity.anInt1547 = 0;
			entity.anInt1548 = 0;
			entity.x = entity.pathX[0] * 128 + entity.boundDim * 64;
			entity.y = entity.pathY[0] * 128 + entity.boundDim * 64;
			entity.resetWalk();
		}
		if (entity == myPlayer
				&& (entity.x < 1536 || entity.y < 1536 || entity.x >= 11776 || entity.y >= 11776)) {
			entity.anim = -1;
			entity.gfxId = -1;
			entity.anInt1547 = 0;
			entity.anInt1548 = 0;
			entity.x = entity.pathX[0] * 128 + entity.boundDim * 64;
			entity.y = entity.pathY[0] * 128 + entity.boundDim * 64;
			entity.resetWalk();
		}
		if (entity.anInt1547 > loopCycle)
			updateEntityPos(entity);
		else if (entity.anInt1548 >= loopCycle)
			updateEntityFace(entity);
		else
			processWalkingStep(entity);
		appendFocusDest(entity);
		appendAnimation(entity);
	}

	private void updateEntityPos(Entity entity) {
		int i = entity.anInt1547 - loopCycle;
		int j = entity.anInt1543 * 128 + entity.boundDim * 64;
		int k = entity.anInt1545 * 128 + entity.boundDim * 64;
		entity.x += (j - entity.x) / i;
		entity.y += (k - entity.y) / i;
		entity.anInt1503 = 0;
		if (entity.turnInfo == 0)
			entity.turnDirection = 1024;
		if (entity.turnInfo == 1)
			entity.turnDirection = 1536;
		if (entity.turnInfo == 2)
			entity.turnDirection = 0;
		if (entity.turnInfo == 3)
			entity.turnDirection = 512;
	}

	private void updateEntityFace(Entity entity) {
		if (entity.anInt1548 == loopCycle
				|| entity.anim == -1
				|| entity.animationDelay != 0
				|| entity.anInt1528 + 1 > Animation.anims[entity.anim]
						.getFrameLength(entity.currentAnimFrame)) {
			int i = entity.anInt1548 - entity.anInt1547;
			int j = loopCycle - entity.anInt1547;
			int k = entity.anInt1543 * 128 + entity.boundDim * 64;
			int l = entity.anInt1545 * 128 + entity.boundDim * 64;
			int i1 = entity.anInt1544 * 128 + entity.boundDim * 64;
			int j1 = entity.anInt1546 * 128 + entity.boundDim * 64;
			entity.x = (k * (i - j) + i1 * j) / i;
			entity.y = (l * (i - j) + j1 * j) / i;
		}
		entity.anInt1503 = 0;
		if (entity.turnInfo == 0)
			entity.turnDirection = 1024;
		if (entity.turnInfo == 1)
			entity.turnDirection = 1536;
		if (entity.turnInfo == 2)
			entity.turnDirection = 0;
		if (entity.turnInfo == 3)
			entity.turnDirection = 512;
		entity.anInt1552 = entity.turnDirection;
	}

	private void processWalkingStep(Entity entity) {
		entity.forcedAnimId = entity.standAnimIndex;
		if (entity.pathLength == 0) {
			entity.anInt1503 = 0;
			return;
		}
		if (entity.anim != -1 && entity.animationDelay == 0) {
			Animation animation = Animation.anims[entity.anim];
			if (entity.anInt1542 > 0 && animation.resetWhenWalk == 0) {
				entity.anInt1503++;
				return;
			}
			if (entity.anInt1542 <= 0 && animation.priority == 0) {
				entity.anInt1503++;
				return;
			}
		}
		int currentX = entity.x;
		int currentY = entity.y;
		int nextX = entity.pathX[entity.pathLength - 1] * 128 + entity.boundDim
				* 64;
		int nextY = entity.pathY[entity.pathLength - 1] * 128 + entity.boundDim
				* 64;
		if (nextX - currentX > 256 || nextX - currentX < -256
				|| nextY - currentY > 256 || nextY - currentY < -256) {
			entity.x = nextX;
			entity.y = nextY;
			return;
		}
		if (currentX < nextX) {
			if (currentY < nextY)
				entity.turnDirection = 1280;
			else if (currentY > nextY)
				entity.turnDirection = 1792;
			else
				entity.turnDirection = 1536;
		} else if (currentX > nextX) {
			if (currentY < nextY)
				entity.turnDirection = 768;
			else if (currentY > nextY)
				entity.turnDirection = 256;
			else
				entity.turnDirection = 512;
		} else if (currentY < nextY)
			entity.turnDirection = 1024;
		else
			entity.turnDirection = 0;
		int i1 = entity.turnDirection - entity.anInt1552 & 0x7ff;
		if (i1 > 1024)
			i1 -= 2048;
		int j1 = entity.turn180AnimIndex;
		if (i1 >= -256 && i1 <= 256)
			j1 = entity.walkAnimIndex;
		else if (i1 >= 256 && i1 < 768)
			j1 = entity.turn90CCWAnimIndex;
		else if (i1 >= -768 && i1 <= -256)
			j1 = entity.turn90CWAnimIndex;
		if (j1 == -1)
			j1 = entity.walkAnimIndex;
		entity.forcedAnimId = j1;
		int k1 = 4;
		if (entity.anInt1552 != entity.turnDirection
				&& entity.interactingEntity == -1 && entity.anInt1504 != 0)
			k1 = 2;
		if (entity.pathLength > 2)
			k1 = 6;
		if (entity.pathLength > 3)
			k1 = 8;
		if (entity.anInt1503 > 0 && entity.pathLength > 1) {
			k1 = 8;
			entity.anInt1503--;
		}
		if (entity.aBooleanArray1553[entity.pathLength - 1])
			k1 <<= 1;
		if (k1 >= 8 && entity.forcedAnimId == entity.walkAnimIndex
				&& entity.runAnimation != -1)
			entity.forcedAnimId = entity.runAnimation;
		if (currentX < nextX) {
			entity.x += k1;
			if (entity.x > nextX)
				entity.x = nextX;
		} else if (currentX > nextX) {
			entity.x -= k1;
			if (entity.x < nextX)
				entity.x = nextX;
		}
		if (currentY < nextY) {
			entity.y += k1;
			if (entity.y > nextY)
				entity.y = nextY;
		} else if (currentY > nextY) {
			entity.y -= k1;
			if (entity.y < nextY)
				entity.y = nextY;
		}
		if (entity.x == nextX && entity.y == nextY) {
			entity.pathLength--;
			if (entity.anInt1542 > 0)
				entity.anInt1542--;
		}
	}

	private void appendFocusDest(Entity entity) {
		if (entity.anInt1504 == 0)
			return;
		if (entity.interactingEntity != -1 && entity.interactingEntity < 32768) {
			try {
				NPC npc = npcArray[entity.interactingEntity];
				if (npc != null) {
					int i1 = entity.x - npc.x;
					int k1 = entity.y - npc.y;
					if (i1 != 0 || k1 != 0)
						entity.turnDirection = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
				}
			} catch (Exception ex) {
			}
		}
		if (entity.interactingEntity >= 32768) {
			int j = entity.interactingEntity - 32768;
			if (j == playerId)
				j = myPlayerIndex;
			Player player = playerArray[j];
			if (player != null) {
				int l1 = entity.x - player.x;
				int i2 = entity.y - player.y;
				if (l1 != 0 || i2 != 0)
					entity.turnDirection = (int) (Math.atan2(l1, i2) * 325.94900000000001D) & 0x7ff;
			}
		}
		if ((entity.anInt1538 != 0 || entity.anInt1539 != 0)
				&& (entity.pathLength == 0 || entity.anInt1503 > 0)) {
			int k = entity.x - (entity.anInt1538 - baseX - baseX) * 64;
			int j1 = entity.y - (entity.anInt1539 - baseY - baseY) * 64;
			if (k != 0 || j1 != 0)
				entity.turnDirection = (int) (Math.atan2(k, j1) * 325.94900000000001D) & 0x7ff;
			entity.anInt1538 = 0;
			entity.anInt1539 = 0;
		}
		int l = entity.turnDirection - entity.anInt1552 & 0x7ff;
		if (l != 0) {
			if (l < entity.anInt1504 || l > 2048 - entity.anInt1504)
				entity.anInt1552 = entity.turnDirection;
			else if (l > 1024)
				entity.anInt1552 -= entity.anInt1504;
			else
				entity.anInt1552 += entity.anInt1504;
			entity.anInt1552 &= 0x7ff;
			if (entity.forcedAnimId == entity.standAnimIndex
					&& entity.anInt1552 != entity.turnDirection) {
				if (entity.nextRenderAnim != -1) {
					entity.forcedAnimId = entity.nextRenderAnim;
					return;
				}
				entity.forcedAnimId = entity.walkAnimIndex;
			}
		}
	}

	private void appendAnimation(Entity entity) {
		entity.aBoolean1541 = false;
		if (entity.forcedAnimId != -1) {
			try {
				Animation animation = Animation.anims[entity.forcedAnimId];
				entity.anInt1519++;
				if (entity.currentForcedAnimFrame < animation.frameCount
						&& entity.anInt1519 > animation
						.getFrameLength(entity.currentForcedAnimFrame)) {
					entity.anInt1519 = 1;// this is the frame delay. 0 is what it's
					// normally at. higher number = faster
					// animations.
					entity.currentForcedAnimFrame++;
				}
				if (entity.currentForcedAnimFrame >= animation.frameCount) {
					entity.anInt1519 = 1;
					entity.currentForcedAnimFrame = 0;
				}
			} catch(Exception e) {

			}
		}
		if (entity.gfxId != -1 && loopCycle >= entity.graphicDelay) {
			if (entity.currentAnim < 0)
				entity.currentAnim = 0;
			Animation animation_1 = SpotAnim.cache[entity.gfxId].animation;
			if (animation_1 != null) {
				for (entity.animCycle++; entity.currentAnim < animation_1.frameCount
						&& entity.animCycle > animation_1
						.getFrameLength(entity.currentAnim); entity.currentAnim++)
					entity.animCycle -= animation_1
					.getFrameLength(entity.currentAnim);

				if (entity.currentAnim >= animation_1.frameCount
						&& (entity.currentAnim < 0 || entity.currentAnim >= animation_1.frameCount))
					entity.gfxId = -1;
			}
		}
		if (entity.anim != -1 && entity.animationDelay <= 1) {
			if (Animation.anims.length <= entity.anim) {
				return;
			}
			Animation animation_2 = Animation.anims[entity.anim];
			if (animation_2.resetWhenWalk == 1 && entity.anInt1542 > 0
					&& entity.anInt1547 <= loopCycle
					&& entity.anInt1548 < loopCycle) {
				entity.animationDelay = 1;
				return;
			}
		}
		try {
			if (entity.anim != -1 && entity.animationDelay == 0) {
				Animation animation_3 = Animation.anims[entity.anim];
				for (entity.anInt1528++; entity.currentAnimFrame < animation_3.frameCount
						&& entity.anInt1528 > animation_3
						.getFrameLength(entity.currentAnimFrame); entity.currentAnimFrame++)
					entity.anInt1528 -= animation_3
					.getFrameLength(entity.currentAnimFrame);

				if (entity.currentAnimFrame >= animation_3.frameCount) {
					entity.currentAnimFrame -= animation_3.loopDelay;
					entity.anInt1530++;
					if (entity.anInt1530 >= animation_3.frameStep)
						entity.anim = -1;
					if (entity.currentAnimFrame < 0
							|| entity.currentAnimFrame >= animation_3.frameCount)
						entity.anim = -1;
				}
				entity.aBoolean1541 = animation_3.oneSquareAnimation;
			}
		} catch (Exception e) {

		}
		if (entity.animationDelay > 0)
			entity.animationDelay--;
	}

	private void drawGameScreen() {
		if (fullscreenInterfaceID != -1
				&& (loadingStage == 2 || super.fullGameScreen != null)) {
			if (loadingStage == 2) {

				processInterfaceAnimation(cycleTimer, fullscreenInterfaceID);
				if (openInterfaceID != -1) {
					processInterfaceAnimation(cycleTimer, openInterfaceID);
				}
				cycleTimer = 0;
				resetAllImageProducers();
				super.fullGameScreen.initDrawingArea();
				Rasterizer.lineOffsets = fullScreenTextureArray;
				welcomeScreenRaised = true;
				if (openInterfaceID != -1) {
					RSInterface rsInterface_1 = RSInterface.interfaceCache[openInterfaceID];
					if (rsInterface_1.width == 512
							&& rsInterface_1.height == 334
							&& rsInterface_1.type == 0) {
						rsInterface_1.width = (clientSize == 0 ? 765
								: clientWidth);
						rsInterface_1.height = (clientSize == 0 ? 503
								: clientHeight);
					}
					drawInterface(0, clientSize == 0 ? 0
							: (clientWidth / 2) - 765 / 2, rsInterface_1,
							clientSize == 0 ? 8 : (clientHeight / 2) - 503 / 2);
				}
				RSInterface rsInterface = RSInterface.interfaceCache[fullscreenInterfaceID];
				if (rsInterface.width == 512 && rsInterface.height == 334
						&& rsInterface.type == 0) {
					rsInterface.width = (clientSize == 0 ? 765 : clientWidth);
					rsInterface.height = (clientSize == 0 ? 503 : clientHeight);
				}
				drawInterface(0, clientSize == 0 ? 0
						: (clientWidth / 2) - 765 / 2, rsInterface,
						clientSize == 0 ? 8 : (clientHeight / 2) - 503 / 2);

				if (!menuOpen) {
					processRightClick();
					drawTooltip();
				} else {
					drawMenu();
				}
			}
			drawCount++;
			super.fullGameScreen.drawGraphics(0, super.graphics, 0);

			return;
		} else {
			if (drawCount != 0) {
				resetImageProducers2();
			}
		}
		if (welcomeScreenRaised) {
			welcomeScreenRaised = false;
			if (clientSize == 0) {
				topFrame.drawGraphics(0, super.graphics, 0);
				leftFrame.drawGraphics(4, super.graphics, 0);
				rightFrame.drawGraphics(3, super.graphics, 516);
			}
			needDrawTabArea = true;
			inputTaken = true;
			tabAreaAltered = true;
			if (loadingStage != 2) {
				if (gameScreenIP != null)
					gameScreenIP.drawGraphics(clientSize == 0 ? 4 : 0,
							super.graphics, clientSize == 0 ? 4 : 0);
				if (clientSize == 0)
					if (mapAreaIP != null)
						mapAreaIP.drawGraphics(0, super.graphics, 516);
			}
		}

		if (menuOpen && menuScreenArea == 1)
			needDrawTabArea = true;
		if (invOverlayInterfaceID != -1) {
			boolean flag1 = processInterfaceAnimation(cycleTimer,
					invOverlayInterfaceID);
			if (flag1)
				needDrawTabArea = true;
		}
		if (atInventoryInterfaceType == 2)
			needDrawTabArea = true;
		if (activeInterfaceType == 2)
			needDrawTabArea = true;
		if (needDrawTabArea) {
			if (clientSize == 0) {
				drawTabArea();
			}
			needDrawTabArea = false;
		}
		if (backDialogID == -1 && inputDialogState == 3) {
			int position = totalItemResults * 14 + 7;
			aClass9_1059.scrollPosition = itemResultScrollPos;
			if (super.mouseX > 478 && super.mouseX < 580
					&& super.mouseY > (clientHeight - 161)) {
				scrollInterface(494, 110, super.mouseX - 0, super.mouseY
						- (clientHeight - 155), aClass9_1059, 0, false,
						totalItemResults);
			}
			int scrollPosition = aClass9_1059.scrollPosition;
			if (scrollPosition < 0) {
				scrollPosition = 0;
			}
			if (scrollPosition > position - 110) {
				scrollPosition = position - 110;
			}
			if (itemResultScrollPos != scrollPosition) {
				itemResultScrollPos = scrollPosition;
				inputTaken = true;
			}
		}
		if (backDialogID == -1 && inputDialogState != 3) {
			aClass9_1059.scrollPosition = anInt1211 - anInt1089 - 110;
			if (super.mouseX > 478 && super.mouseX < 580
					&& super.mouseY > (clientHeight - 161))
				scrollInterface(494, 110, super.mouseX - 0, super.mouseY
						- (clientHeight - 155), aClass9_1059, 0, false,
						anInt1211);
			int i = anInt1211 - 110 - aClass9_1059.scrollPosition;
			if (i < 0)
				i = 0;
			if (i > anInt1211 - 110)
				i = anInt1211 - 110;
			if (anInt1089 != i) {
				anInt1089 = i;
				inputTaken = true;
			}
		}
		if (backDialogID != -1) {
			boolean flag2 = processInterfaceAnimation(cycleTimer, backDialogID);
			if (flag2)
				inputTaken = true;
		}
		if (atInventoryInterfaceType == 3)
			inputTaken = true;
		if (activeInterfaceType == 3)
			inputTaken = true;
		if (aString844 != null)
			inputTaken = true;
		if (menuOpen && menuScreenArea == 2)
			inputTaken = true;
		if (inputTaken) {
			if (clientSize == 0) {
				drawChatArea();
				gameScreenIP.initDrawingArea();
			}
			inputTaken = false;
		}
		if (loadingStage == 2)
			try {
				renderWorld();
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (loadingStage == 2) {
			if (clientSize == 0) {
				drawMinimap();
				mapAreaIP.drawGraphics(0, super.graphics, 765 - 246);
			}
		}
		if (anInt1054 != -1)
			tabAreaAltered = true;
		if (tabAreaAltered) {
			if (anInt1054 != -1 && anInt1054 == tabID) {
				anInt1054 = -1;
				stream.createFrame(120);
				stream.writeWordBigEndian(tabID);
			}
			tabAreaAltered = false;
			if (clientSize == 0)
				rightFrame.drawGraphics(3, super.graphics, 516);
			GraphicsBuffer_1125.initDrawingArea();
			gameScreenIP.initDrawingArea();
		}

		cycleTimer = 0;
	}

	private boolean buildFriendsListMenu(RSInterface class9) {
		int i = class9.contentType;
		if (i >= 1 && i <= 200 || i >= 701 && i <= 900) {
			if (i >= 801)
				i -= 701;
			else if (i >= 701)
				i -= 601;
			else if (i >= 101)
				i -= 101;
			else
				i--;
			menuActionName[menuActionRow] = "Remove @whi@" + friendsList[i];
			menuActionID[menuActionRow] = 792;
			menuActionRow++;
			menuActionName[menuActionRow] = "Message @whi@" + friendsList[i];
			menuActionID[menuActionRow] = 639;
			menuActionRow++;
			return true;
		}
		if (i >= 401 && i <= 500) {
			menuActionName[menuActionRow] = "Remove @whi@" + class9.message;
			menuActionID[menuActionRow] = 322;
			menuActionRow++;
			return true;
		} else {
			return false;
		}
	}

	private void processStillGraphic() {
		StillGraphic stillGraphic = (StillGraphic) stillGraphicDeque.getFront();
		for (; stillGraphic != null; stillGraphic = (StillGraphic) stillGraphicDeque
				.getNext())
			if (stillGraphic.plane != plane || stillGraphic.animFinished)
				stillGraphic.unlink();
			else if (loopCycle >= stillGraphic.startTime) {
				stillGraphic.processAnimation(cycleTimer);
				if (stillGraphic.animFinished)
					stillGraphic.unlink();
				else
					worldController.addMutipleTileEntity(stillGraphic.plane, 0,
							stillGraphic.drawHeight, -1, stillGraphic.yTile,
							60, stillGraphic.xTile, stillGraphic, false);
			}

	}

	public void drawBlackBox(int xPos, int yPos) {
		DrawingArea.drawPixels(71, yPos - 1, xPos - 2, 20000, 1);
		DrawingArea.drawPixels(69, yPos, xPos + 174, 20000, 1);
		DrawingArea.drawPixels(1, yPos - 2, xPos - 2, 20000, 178);
		DrawingArea.drawPixels(1, yPos + 68, xPos, 20000, 174);
		DrawingArea.drawPixels(71, yPos - 1, xPos - 1, 20000, 1);
		DrawingArea.drawPixels(71, yPos - 1, xPos + 175, 20000, 1);
		DrawingArea.drawPixels(1, yPos - 1, xPos, 20000, 175);
		DrawingArea.drawPixels(1, yPos + 69, xPos, 20000, 175);
		DrawingArea.fillRectangle(0, yPos, 174, 68, 220, xPos);
	}
	int[] IDs = { 1196, 1199, 1206, 1215, 1224, 1231, 1240, 1249, 1258,
			1267, 1274, 1283, 1573, 1290, 1299, 1308, 1315, 1324, 1333,
			1340, 1349, 1358, 1367, 1374, 1381, 1388, 1397, 1404, 1583,
			12038, 1414, 1421, 1430, 1437, 1446, 1453, 1460, 1469,
			15878, 1602, 1613, 1624, 7456, 1478, 1485, 1494, 1503,
			1512, 1521, 1530, 1544, 1553, 1563, 1593, 1635, 12426,
			12436, 12446, 12456, 6004, 18471,
			/* Ancients */
			12940, 12988, 13036, 12902, 12862, 13046, 12964, 13012,
			13054, 12920, 12882, 13062, 12952, 13000, 13070, 12912,
			12872, 13080, 12976, 13024, 13088, 12930, 12892, 13096 ,
	};
	int[] runeChildren = { 1202, 1203, 1209, 1210, 1211, 1218, 1219,
			1220, 1227, 1228, 1234, 1235, 1236, 1243, 1244, 1245, 1252,
			1253, 1254, 1261, 1262, 1263, 1270, 1271, 1277, 1278, 1279,
			1286, 1287, 1293, 1294, 1295, 1302, 1303, 1304, 1311, 1312,
			1318, 1319, 1320, 1327, 1328, 1329, 1336, 1337, 1343, 1344,
			1345, 1352, 1353, 1354, 1361, 1362, 1363, 1370, 1371, 1377,
			1378, 1384, 1385, 1391, 1392, 1393, 1400, 1401, 1407, 1408,
			1410, 1417, 1418, 1424, 1425, 1426, 1433, 1434, 1440, 1441,
			1442, 1449, 1450, 1456, 1457, 1463, 1464, 1465, 1472, 1473,
			1474, 1481, 1482, 1488, 1489, 1490, 1497, 1498, 1499, 1506,
			1507, 1508, 1515, 1516, 1517, 1524, 1525, 1526, 1533, 1534,
			1535, 1547, 1548, 1549, 1556, 1557, 1558, 1566, 1567, 1568,
			1576, 1577, 1578, 1586, 1587, 1588, 1596, 1597, 1598, 1605,
			1606, 1607, 1616, 1617, 1618, 1627, 1628, 1629, 1638, 1639,
			1640, 6007, 6008, 6011, 8673, 8674, 12041, 12042, 12429,
			12430, 12431, 12439, 12440, 12441, 12449, 12450, 12451,
			12459, 12460, 15881, 15882, 15885, 18474, 18475, 18478 };

	private void drawInterface(int scrollOffset, int interfaceX,
			RSInterface rsInterface, int interfaceY) {
		try {
			if (rsInterface == null || rsInterface.type != 0 || rsInterface.children == null)
				return;
			if(rsInterface.id != 3213)
				if (rsInterface.interfaceShown && anInt1026 != rsInterface.id
				&& anInt1048 != rsInterface.id && anInt1039 != rsInterface.id
				&& rsInterface.id != 35555)
					return;
			int origTopX = DrawingArea.topX;
			int origTopY = DrawingArea.topY;
			int origBottomX = DrawingArea.bottomX;
			int origBottomY = DrawingArea.bottomY;
			DrawingArea.setDrawingArea(interfaceY + rsInterface.height, interfaceX,
					interfaceX + rsInterface.width, interfaceY);

			int totalChildrens = rsInterface.children.length;
			for (int childID = 0; childID < totalChildrens; childID++) {
				int childX = rsInterface.childX[childID] + interfaceX;
				int childY = (rsInterface.childY[childID] + interfaceY)
						- scrollOffset;
				RSInterface child = RSInterface.interfaceCache[rsInterface.children[childID]];
				childX += child.xOffset;
				childY += child.yOffset;
				if (child.contentType > 0)
					drawFriendsListOrWelcomeScreen(child);
				for (int m5 = 0; m5 < IDs.length; m5++) {
					if (child.id == IDs[m5] + 1) {
						if (m5 > 61)
							drawBlackBox(childX + 1, childY);
						else
							drawBlackBox(childX, childY + 1);
					}
				}
				for (int r = 0; r < runeChildren.length; r++)
					if (child.id == runeChildren[r])
						child.modelZoom = 775;
				if(child.id == 1194 || child.id == 12856) //Removes black boxes when not hovering in spellbook
					continue;
				if (child.type == 0) {
					if (child.scrollPosition > child.scrollMax - child.height)
						child.scrollPosition = child.scrollMax - child.height;
					if (child.scrollPosition < 0)
						child.scrollPosition = 0;
					drawInterface(child.scrollPosition, childX, child, childY);
					if (child.scrollMax > child.height)
						drawScrollbar(child.height, child.scrollPosition, childY,
								childX + child.width, child.scrollMax, false, false);
				} else if (child.type != 1)
					if (child.type == 2) {
						int spriteIndex = 0;
						for (int height = 0; height < child.height; height++) {
							for (int width = 0; width < child.width; width++) {
								int itemSpriteX = childX + width
										* (32 + child.invSpritePadX);
								int itemSpriteY = childY + height
										* (32 + child.invSpritePadY);
								if (spriteIndex < 20) {
									itemSpriteX += child.spritesX[spriteIndex];
									itemSpriteY += child.spritesY[spriteIndex];
								}
								if (child.inv[spriteIndex] > 0) {
									int k6 = 0;
									int j7 = 0;
									int j9 = child.inv[spriteIndex] - 1;
									if (itemSpriteX > DrawingArea.topX - 32
											&& itemSpriteX < DrawingArea.bottomX
											&& itemSpriteY > DrawingArea.topY - 32
											&& itemSpriteY < DrawingArea.bottomY
											|| activeInterfaceType != 0
											&& anInt1085 == spriteIndex) {
										int selectedColour = 0;
										if (itemSelected == 1
												&& lastItemSelectedSlot == spriteIndex
												&& lastItemSelectedInterface == child.id)
											selectedColour = 0xffffff;
										Sprite sprite_2 = ItemDef.getSprite(j9,
												child.invStackSizes[spriteIndex],
												selectedColour);
										if (sprite_2 != null) {
											if (activeInterfaceType != 0
													&& anInt1085 == spriteIndex
													&& anInt1084 == child.id) {
												k6 = super.mouseX - anInt1087;
												j7 = super.mouseY - anInt1088;
												if (k6 < 5 && k6 > -5)
													k6 = 0;
												if (j7 < 5 && j7 > -5)
													j7 = 0;
												if (anInt989 < 10) {
													k6 = 0;
													j7 = 0;
												}
												sprite_2.drawSprite1(itemSpriteX
														+ k6, itemSpriteY + j7);
												if (itemSpriteY + j7 < DrawingArea.topY
														&& rsInterface.scrollPosition > 0) {
													int i10 = (cycleTimer * (DrawingArea.topY
															- itemSpriteY - j7)) / 3;
													if (i10 > cycleTimer * 10)
														i10 = cycleTimer * 10;
													if (i10 > rsInterface.scrollPosition)
														i10 = rsInterface.scrollPosition;
													rsInterface.scrollPosition -= i10;
													anInt1088 += i10;
												}
												if (itemSpriteY + j7 + 32 > DrawingArea.bottomY
														&& rsInterface.scrollPosition < rsInterface.scrollMax
														- rsInterface.height) {
													int j10 = (cycleTimer * ((itemSpriteY
															+ j7 + 32) - DrawingArea.bottomY)) / 3;
													if (j10 > cycleTimer * 10)
														j10 = cycleTimer * 10;
													if (j10 > rsInterface.scrollMax
															- rsInterface.height
															- rsInterface.scrollPosition)
														j10 = rsInterface.scrollMax
														- rsInterface.height
														- rsInterface.scrollPosition;
													rsInterface.scrollPosition += j10;
													anInt1088 -= j10;
												}
											} else if (atInventoryInterfaceType != 0
													&& atInventoryIndex == spriteIndex
													&& atInventoryInterface == child.id)
												sprite_2.drawSprite1(itemSpriteX,
														itemSpriteY);
											else
												sprite_2.drawSprite(itemSpriteX,
														itemSpriteY);
											if (sprite_2.maxWidth == 33
													|| child.invStackSizes[spriteIndex] != 1) {
												int k10 = child.invStackSizes[spriteIndex];
												boolean bankTab = child.id >= 22035 && child.id <= 22042;
												if (child.actions[0] != "[GE]"
														&& child.actions[1] != "[GE]"
														&& child.actions[2] != "[GE]"
														&& child.actions[3] != "[GE]"
														&& child.actions[4] != "[GE]" && !bankTab) {
													smallText.method385(0,
															intToKOrMil(k10),
															itemSpriteY + 10 + j7,
															itemSpriteX + 1 + k6);
													if (k10 > 99999
															&& k10 < 10000000)
														smallText.method385(
																0xFFFFFF,
																intToKOrMil(k10),
																itemSpriteY + 9
																+ j7,
																itemSpriteX + k6);
													else if (k10 > 9999999)
														smallText.method385(
																0x00ff80,
																intToKOrMil(k10),
																itemSpriteY + 9
																+ j7,
																itemSpriteX + k6);
													else
														smallText.method385(
																0xFFFF00,
																intToKOrMil(k10),
																itemSpriteY + 9
																+ j7,
																itemSpriteX + k6);
												}
											}
										}
									}
								} else if (child.sprites != null
										&& spriteIndex < 20) {
									Sprite sprite_1 = child.sprites[spriteIndex];
									if (sprite_1 != null)
										sprite_1.drawSprite(itemSpriteX,
												itemSpriteY);
								}
								spriteIndex++;
							}
						}
					} else if (child.type == 3) {
						boolean hover = false;
						if (anInt1039 == child.id || anInt1048 == child.id
								|| anInt1026 == child.id)
							hover = true;
						int color;
						if (interfaceIsSelected(child)) {
							color = child.enabledColor;
							if (hover && child.enabledMouseOverColor != 0)
								color = child.enabledMouseOverColor;
						} else {
							color = child.disabledColor;
							if (hover && child.disabledMouseOverColor != 0)
								color = child.disabledMouseOverColor;
						}
						if (child.opacity == 0) {
							if (child.filled)
								DrawingArea.drawPixels(child.height, childY,
										childX, color, child.width);
							else
								DrawingArea.fillPixels(childX, child.width,
										child.height, color, childY);
						} else if (child.filled)
							DrawingArea.fillRectangle(color, childY, child.width,
									child.height, 256 - (child.opacity & 0xff),
									childX);
						else
							DrawingArea.drawRectangle(childY, child.height,
									256 - (child.opacity & 0xff), color,
									child.width, childX);
					} else if (child.type == 4) {
						TextDrawingArea textDrawingArea = child.textDrawingAreas;
						String s = child.message;
						//s = RSFontSystem.replaceSyntaxes(s);
						if(child.id == 2458)
							s = "Click here to logout";
						boolean hovered = false;
						if (anInt1039 == child.id || anInt1048 == child.id
								|| anInt1026 == child.id)
							hovered = true;
						int color;
						if (interfaceIsSelected(child)) {
							color = child.enabledColor;
							if (hovered && child.enabledMouseOverColor != 0)
								color = child.enabledMouseOverColor;
							if (child.enabledMessage.length() > 0)
								s = child.enabledMessage;
						} else {
							color = child.disabledColor;
							if (hovered && child.disabledMouseOverColor != 0)
								color = child.disabledMouseOverColor;
						}
						if (child.atActionType == 6 && aBoolean1149) {
							s = "Please wait...";
							color = child.disabledColor;
						}
						if (chatAreaIP == null
								|| DrawingArea.width == chatAreaIP.width
								|| rsInterface.id == backDialogID) {
							if (color == 0xffff00)
								color = 255;
							if (color == 49152)
								color = 0xffffff;
						}
						if ((child.parentID == 1151) || (child.parentID == 12855)) {
							switch (color) {
							case 16773120:
								color = 0xFE981F;
								break;
							case 7040819:
								color = 0xAF6A1A;
								break;
							}
						}
						for (int l6 = childY + textDrawingArea.anInt1497; s
								.length() > 0; l6 += textDrawingArea.anInt1497) {
							if (s.indexOf("%") != -1) {
								do {
									int k7 = s.indexOf("%1");
									if (k7 == -1)
										break;
									if (child.id < 4000 || child.id > 5000
											&& child.id != 13921
											&& child.id != 13922
											&& child.id != 12171
											&& child.id != 12172)
										s = s.substring(0, k7)
										+ formatNumberToLetter(extractInterfaceValues(
												child, 0))
												+ s.substring(k7 + 2);
									else
										s = s.substring(0, k7)
										+ interfaceIntToString(extractInterfaceValues(
												child, 0))
												+ s.substring(k7 + 2);
								} while (true);
								do {
									int l7 = s.indexOf("%2");
									if (l7 == -1)
										break;
									s = s.substring(0, l7)
											+ interfaceIntToString(extractInterfaceValues(
													child, 1))
													+ s.substring(l7 + 2);
								} while (true);
								do {
									int i8 = s.indexOf("%3");
									if (i8 == -1)
										break;
									s = s.substring(0, i8)
											+ interfaceIntToString(extractInterfaceValues(
													child, 2))
													+ s.substring(i8 + 2);
								} while (true);
								do {
									int j8 = s.indexOf("%4");
									if (j8 == -1)
										break;
									s = s.substring(0, j8)
											+ interfaceIntToString(extractInterfaceValues(
													child, 3))
													+ s.substring(j8 + 2);
								} while (true);
								do {
									int k8 = s.indexOf("%5");
									if (k8 == -1)
										break;
									s = s.substring(0, k8)
											+ interfaceIntToString(extractInterfaceValues(
													child, 4))
													+ s.substring(k8 + 2);
								} while (true);
							}
							int l8 = s.indexOf("\\n");
							String s1;
							if (l8 != -1) {
								s1 = s.substring(0, l8);
								s = s.substring(l8 + 2);
							} else {
								s1 = s;
								s = "";
							}
							if (child.centerText)
								textDrawingArea.drawCenteredText(color, childX
										+ child.width / 2, s1, l6, child.shadowed);
							else
								textDrawingArea.drawRegularText(child.shadowed,
										childX, color, s1, l6);
						}
					} else if (child.type == 5) {
						Sprite sprite;
						if (child.enabledSpriteId != -1
								&& SpriteCache.spriteCache[child.enabledSpriteId] == null) {
							onDemandFetcher.requestFileData(IMAGE_IDX - 1,
									child.enabledSpriteId);
						}
						if (child.disabledSpriteId != -1
								&& SpriteCache.spriteCache[child.disabledSpriteId] == null) {
							onDemandFetcher.requestFileData(IMAGE_IDX - 1,
									child.disabledSpriteId);
						}
						if (child.itemSpriteId1 != -1
								&& child.disabledSprite == null
								&& child.disabledSpriteId == -1
								&& child.enabledSpriteId == -1) {
							child.disabledSprite = ItemDef.getSprite(
									child.itemSpriteId1, 1,
									(child.itemSpriteZoom1 == -1) ? 0 : -1,
											child.itemSpriteZoom1);
							child.enabledSprite = ItemDef.getSprite(
									child.itemSpriteId2, 1,
									(child.itemSpriteZoom2 == -1) ? 0 : -1,
											child.itemSpriteZoom2);

						}
						if (interfaceIsSelected(child) || hoverSpriteId == child.id)
							if (child.enabledSpriteId != -1
							&& SpriteCache.spriteCache[child.enabledSpriteId] != null)
								sprite = SpriteCache.spriteCache[child.enabledSpriteId];
							else
								sprite = child.enabledSprite;
						else if (child.disabledSpriteId != -1
								&& SpriteCache.spriteCache[child.disabledSpriteId] != null)
							sprite = SpriteCache.spriteCache[child.disabledSpriteId];
						else
							sprite = child.disabledSprite;
						if (child.id == 1164 || child.id == 1167
								|| child.id == 1170 || child.id == 1174
								|| child.id == 1540 || child.id == 1541
								|| child.id == 7455 || child.id == 18470
								|| child.id == 13035 || child.id == 13045
								|| child.id == 13053 || child.id == 13061
								|| child.id == 13069 || child.id == 13079
								|| child.id == 30064 || child.id == 30075
								|| child.id == 30083 || child.id == 30106
								|| child.id == 30114 || child.id == 30106
								|| child.id == 30170 || child.id == 13087
								|| child.id == 30162 || child.id == 13095)
							if (child.enabledSpriteId != -1
							&& SpriteCache.spriteCache[child.enabledSpriteId] != null)
								sprite = SpriteCache.spriteCache[child.enabledSpriteId];
							else
								sprite = child.enabledSprite;
						if (spellSelected == 1 && child.id == spellID
								&& spellID != 0 && sprite != null) {
							sprite.drawSprite(childX, childY, 0xffffff);
						} else {
							if (sprite != null) {
								if (child.type == 5) {
									if (child.advancedSprite) {
										sprite.drawAdvancedSprite(childX, childY);
									} else {
										sprite.drawSprite(childX, childY);
									}
								} else {
									sprite.drawSprite1(childX, childY,
											child.opacity);
								}
							}
						}
						if (autoCast && child.id == autocastId)
							SpriteCache.spriteCache[47].drawSprite(childX - 3,
									childY - 2);
						if(child.summonReq > 0) {
							if(child.summonReq > currentStats[23])
								child.greyScale = true;
							else if(currentStats[23] > child.summonReq)
								child.greyScale = false;
							if(child.greyScale && child.disabledSprite != null)
								child.disabledSprite.greyScale();
						}
					} else if (child.type == 6) {
						int k3 = Rasterizer.center_x;
						int j4 = Rasterizer.center_y;
						Rasterizer.center_x = childX + child.width / 2;
						Rasterizer.center_y = childY + child.height / 2;
						int i5 = Rasterizer.SINE[child.modelRotation1]
								* child.modelZoom >> 16;
					int l5 = Rasterizer.COSINE[child.modelRotation1]
							* child.modelZoom >> 16;
					boolean selected = interfaceIsSelected(child);
					int animId;
					if (selected)
						animId = child.enabledAnimationId;
					else
						animId = child.disabledAnimationId;
					Model model;
					if (animId == -1) {
						model = child.getAnimatedModel(-1, -1, selected);
					} else {
						Animation animation = Animation.anims[animId];
						model = child.getAnimatedModel(
								animation.frameIDs2[child.currentFrame],
								animation.frameIDs[child.currentFrame],
								selected);
					}
					if (model != null)
						model.renderSingle(child.modelRotation2, 0,
								child.modelRotation1, 0, i5, l5);
					// model.reset();
					// model = null;
					Rasterizer.center_x = k3;
					Rasterizer.center_y = j4;
					} else if (child.type == 7) {
						TextDrawingArea textDrawingArea_1 = child.textDrawingAreas;
						int k4 = 0;
						for (int y = 0; y < child.height; y++) {
							for (int x = 0; x < child.width; x++) {
								if (child.inv[k4] > 0) {
									ItemDef itemDef = ItemDef
											.forID(child.inv[k4] - 1);
									String s2 = itemDef.name;
									if (itemDef.stackable
											|| child.invStackSizes[k4] != 1)
										s2 = s2
										+ " x"
										+ intToKOrMilLongName(child.invStackSizes[k4]);
									int i9 = childX + x
											* (115 + child.invSpritePadX);
									int k9 = childY + y
											* (12 + child.invSpritePadY);
									if (child.centerText)
										textDrawingArea_1.drawCenteredText(
												child.disabledColor, i9
												+ child.width / 2, s2, k9,
												child.shadowed);
									else
										textDrawingArea_1.drawRegularText(
												child.shadowed, i9,
												child.disabledColor, s2, k9);
								}
								k4++;
							}
						}
					} else if (child.type == 8) {
						// if (interfaceIsSelected(child)) {
						// } else
						try {
							drawHoverBox(childX, childY, child.popupString);
						} catch (Exception e) {
						}
					} else if (child.type == 9) {
						Sprite sprite;
						if (interfaceIsSelected(child)) {
							sprite = child.enabledSprite;
						} else {
							sprite = child.disabledSprite;
						}
						if (sprite != null) {
							if(child.drawsTransparent)
								sprite.drawTransparentSprite(childX, childY, child.customOpacity);
							else
								sprite.drawSprite(childX, childY);
						}
					} else if (child.type == 10
							&& (anInt1500 == child.id || anInt1044 == child.id || anInt1129 == child.id)
							&& !menuOpen) {
						int boxWidth = 0;
						int boxHeight = 0;
						TextDrawingArea textDrawingArea_2 = drawingArea;
						for (String s1 = child.message; s1.length() > 0;) {
							int l7 = s1.indexOf("\\n");
							String s4;
							if (l7 != -1) {
								s4 = s1.substring(0, l7);
								s1 = s1.substring(l7 + 2);
							} else {
								s4 = s1;
								s1 = "";
							}
							int j10 = textDrawingArea_2.getTextWidth(s4);
							if (j10 > boxWidth) {
								boxWidth = j10;
							}
							boxHeight += textDrawingArea_2.anInt1497 + 1;
						}
						boxWidth += 6;
						boxHeight += 7;
						int xPos = (childX + child.width) - 5 - boxWidth;
						int yPos = childY + child.height + 5;
						if (xPos < childX + 5) {
							xPos = childX + 5;
						}
						if (xPos + boxWidth > interfaceX + rsInterface.width) {
							xPos = (interfaceX + rsInterface.width) - boxWidth;
						}
						if (yPos + boxHeight > interfaceY + rsInterface.height) {
							yPos = (interfaceY + rsInterface.height) - boxHeight;
						}
						if (clientSize != 0) {
							if (childX == clientWidth - 69
									|| childX == clientWidth - 131) {
								xPos -= (childX == clientWidth - 69) ? 100 : 20;
							}
							if (childY == clientHeight
									- (clientWidth <= smallTabs ? 112 : 75)) {
								yPos -= 100;
							}
							// System.out.println("ChildX = "+
							// childX+", childY = "+childY+", clientWidth = "+clientWidth+" , clientHeight = "+clientHeight);
						} else {
							if (child.inventoryHover) {
								if (xPos + boxWidth + interfaceX > 249) {
									xPos = 251 - boxWidth - interfaceX;
								}
								if (yPos + boxHeight + interfaceY > 261) {
									yPos = 245 - boxHeight - interfaceY;
								}
							}
						}
						DrawingArea.drawPixels(boxHeight, yPos, xPos, 0xFFFFA0,
								boxWidth);
						DrawingArea.fillPixels(xPos, boxWidth, boxHeight, 0, yPos);
						String s2 = child.message;
						for (int j11 = yPos + textDrawingArea_2.anInt1497 + 2; s2
								.length() > 0; j11 += textDrawingArea_2.anInt1497 + 1) {
							int l11 = s2.indexOf("\\n");
							String s5;
							if (l11 != -1) {
								s5 = s2.substring(0, l11);
								s2 = s2.substring(l11 + 2);
							} else {
								s5 = s2;
								s2 = "";
							}
							textDrawingArea_2.drawRegularText(false, xPos + 3, 0,
									s5, j11);
						}
					} else if (child.type == 11) {
						DrawingArea.fillRectangle(child.disabledColor, 0,
								clientWidth, clientHeight,
								256 - (child.opacity & 0xff), 0);
					} else if (child.type == 12) {
						try {
							drawHoverBox(childX, childY, child.message);
						} catch (Exception e) {
						}
					}
				if(rsInterface.id == 18128)
					updateClanChatTab();
				if(openInterfaceID == 10000) {
					if(!reportBox2Selected) {
						sendFrame126(reasonForReport, 10006);
						sendFrame126(""+playerReporting+"*", 10004);
					} else {
						sendFrame126(""+reasonForReport+"*", 10006);
						sendFrame126(""+playerReporting, 10004);
					}
				}
			}
			DrawingArea
			.setDrawingArea(origBottomY, origTopX, origBottomX, origTopY);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void readPlayerUpdateMask(int i, int j, Stream stream, Player player) {
		/*
		 * Player updating method
		 */
		if ((i & 0x400) != 0) {
			player.anInt1543 = stream.readByteS();
			player.anInt1545 = stream.readByteS();
			player.anInt1544 = stream.readByteS();
			player.anInt1546 = stream.readByteS();
			player.anInt1547 = stream.readWordBigEndian() + loopCycle;
			player.anInt1548 = stream.readByteA() + loopCycle;
			player.turnInfo = stream.readByteS();
			player.resetWalk();
		}
		if ((i & 0x100) != 0) {
			player.gfxId = stream.ig2();
			int k = stream.readDWord();
			player.graphicHeight = k >> 16;
			player.graphicDelay = loopCycle + (k & 0xffff);
			player.currentAnim = 0;
			player.animCycle = 0;
			if (player.graphicDelay > loopCycle)
				player.currentAnim = -1;
			if (player.gfxId == 65535)
				player.gfxId = -1;
			try {
				if (FrameReader.animationlist[Integer
				                              .parseInt(
				                            		  Integer.toHexString(
				                            				  SpotAnim.cache[player.gfxId].animation.frameIDs[0])
				                            				  .substring(
				                            						  0,
				                            						  Integer.toHexString(
				                            								  SpotAnim.cache[player.gfxId].animation.frameIDs[0])
				                            								  .length() - 4), 16)].length == 0)
					onDemandFetcher
					.requestFileData(
							1,
							Integer.parseInt(
									Integer.toHexString(
											SpotAnim.cache[player.gfxId].animation.frameIDs[0])
											.substring(
													0,
													Integer.toHexString(
															SpotAnim.cache[player.gfxId].animation.frameIDs[0])
															.length() - 4),
															16));
			} catch (Exception e) {
			}
		}
		if ((i & 8) != 0) {
			int requestAnim = stream.ig2();
			if (requestAnim == 65535)
				requestAnim = -1;
			int i2 = stream.nglb();
			if (requestAnim == player.anim && requestAnim != -1) {
				int i3 = Animation.anims[requestAnim].delayType;
				if (i3 == 1) {
					player.currentAnimFrame = 0;
					player.anInt1528 = 0;
					player.animationDelay = i2;
					player.anInt1530 = 0;
				}
				if (i3 == 2)
					player.anInt1530 = 0;
			} else if (requestAnim == -1
					|| player.anim == -1
					|| Animation.anims[requestAnim].forcedPriority >= Animation.anims[player.anim].forcedPriority) {
				player.anim = requestAnim;
				player.currentAnimFrame = 0;
				player.anInt1528 = 0;
				player.animationDelay = i2;
				player.anInt1530 = 0;
				player.anInt1542 = player.pathLength;

				try {
					if (FrameReader.animationlist[Integer
					                              .parseInt(
					                            		  Integer.toHexString(
					                            				  Animation.anims[requestAnim].frameIDs[0])
					                            				  .substring(
					                            						  0,
					                            						  Integer.toHexString(
					                            								  Animation.anims[requestAnim].frameIDs[0])
					                            								  .length() - 4), 16)].length == 0)
						onDemandFetcher
						.requestFileData(
								1,
								Integer.parseInt(
										Integer.toHexString(
												Animation.anims[requestAnim].frameIDs[0])
												.substring(
														0,
														Integer.toHexString(
																Animation.anims[requestAnim].frameIDs[0])
																.length() - 4),
																16));
				} catch (Exception e) {
				}
			}
		}
		if ((i & 4) != 0) {
			player.textSpoken = stream.readString();
			if (player.textSpoken.charAt(0) == '~') {
				player.textSpoken = player.textSpoken.substring(1);
				pushMessage(player.textSpoken, 2, player.name);
			} else if (player == myPlayer) {
				pushMessage(player.textSpoken, 2, player.name);
			}
			player.anInt1513 = 0;
			player.anInt1531 = 0;
			player.textCycle = 150;
		}
		if ((i & 0x80) != 0) {
			int effects = stream.ig2();
			int rights = stream.readUnsignedByte();
			int gloColor = stream.readUnsignedByte();
			int chatTextSize = stream.nglb();
			int currentOffset = stream.currentOffset;
			if (player.name != null && player.visible) {
				long l3 = TextClass.longForName(player.name);
				boolean flag = false;
				if (rights <= 1) {
					for (int i4 = 0; i4 < ignoreCount; i4++) {
						if (ignoreListAsLongs[i4] != l3)
							continue;
						flag = true;
						break;
					}

				}
				if (!flag && isOnTutorialIsland == 0)
					try {
						textStream.currentOffset = 0;
						stream.readBytes_reverse(chatTextSize, 0,
								textStream.buffer);
						textStream.currentOffset = 0;
						String message = TextInput.decodeToString(chatTextSize,
								textStream);
						message = Censor.doCensor(message);
						player.textSpoken = message;
						player.anInt1513 = effects >> 8;
					player.playerRights = rights;
					player.anInt1531 = effects & 0xff;
					player.textCycle = 150;
					pushMessage(message, 2,
							getPrefix(rights)
							+ (gloColor != 0 ? "@glo" + gloColor
									+ "@" : "") + player.name);
					} catch (Exception exception) {
						signlink.reporterror("cde2");
					}
			}
			stream.currentOffset = currentOffset + chatTextSize;
		}
		if ((i & 1) != 0) {
			player.interactingEntity = stream.ig2();
			if (player.interactingEntity == 65535)
				player.interactingEntity = -1;
		}
		if ((i & 0x10) != 0) {
			int j1 = stream.nglb();
			byte abyte0[] = new byte[j1];
			Stream stream_1 = new Stream(abyte0);
			stream.readBytes(j1, 0, abyte0);
			aStreamArray895s[j] = stream_1;
			player.updatePlayerAppearance(stream_1);
		}
		if ((i & 2) != 0) {
			player.anInt1538 = stream.readWordBigEndian();
			player.anInt1539 = stream.ig2();
		}
		if ((i & 0x20) != 0) {
			int k1 = inStream.readByteA();
			int k2 = stream.readUnsignedByte();
			int icon = stream.readUnsignedByte();
			int soakDamage = inStream.readByteA();
			player.updateHitData(k2, k1, loopCycle, icon, soakDamage);
			player.loopCycleStatus = loopCycle + 300;
			player.constitution = player.currentHealth = inStream.readByteA();
			player.maxConstitution = player.maxHealth = inStream.readByteA();
		}
		if ((i & 0x200) != 0) {
			int l1 = inStream.readByteA();
			int l2 = stream.readUnsignedByte();
			int icon = stream.readUnsignedByte();
			int soakDamage = inStream.readByteA();
			player.updateHitData(l2, l1, loopCycle, icon, soakDamage);
			player.loopCycleStatus = loopCycle + 300;
			player.constitution = player.currentHealth = inStream.readByteA();
			player.maxConstitution = player.maxHealth = inStream.readByteA();
		}
	}

	private void handleArrowKeys() {
		try {
			int j = myPlayer.x + cameraOffsetX;
			int k = myPlayer.y + cameraOffsetY;
			if (anInt1014 - j < -500 || anInt1014 - j > 500
					|| anInt1015 - k < -500 || anInt1015 - k > 500) {
				anInt1014 = j;
				anInt1015 = k;
			}
			if (anInt1014 != j)
				anInt1014 += (j - anInt1014) / 16;
			if (anInt1015 != k)
				anInt1015 += (k - anInt1015) / 16;
			if (super.keyArray[1] == 1)
				anInt1186 += (-24 - anInt1186) / 2;
			else if (super.keyArray[2] == 1)
				anInt1186 += (24 - anInt1186) / 2;
			else
				anInt1186 /= 2;
			if (super.keyArray[3] == 1)
				anInt1187 += (12 - anInt1187) / 2;
			else if (super.keyArray[4] == 1)
				anInt1187 += (-12 - anInt1187) / 2;
			else
				anInt1187 /= 2;
			viewRotation = viewRotation + anInt1186 / 2 & 0x7ff;
			anInt1184 += anInt1187 / 2;
			if (anInt1184 < 128)
				anInt1184 = 128;
			if (anInt1184 > 383)
				anInt1184 = 383;
			int l = anInt1014 >> 7;
			int i1 = anInt1015 >> 7;
				int j1 = getFloorDrawHeight(plane, anInt1015, anInt1014);
				int k1 = 0;
				if (l > 3 && i1 > 3 && l < 100 && i1 < 100) {
					for (int l1 = l - 4; l1 <= l + 4; l1++) {
						for (int k2 = i1 - 4; k2 <= i1 + 4; k2++) {
							int l2 = plane;
							if (l2 < 3 && (byteGroundArray[1][l1][k2] & 2) == 2)
								l2++;
							int i3 = j1 - intGroundArray[l2][l1][k2];
							if (i3 > k1)
								k1 = i3;
						}

					}

				}
				anInt1005++;
				if (anInt1005 > 1512) {
					anInt1005 = 0;
					stream.createFrame(77);
					stream.writeWordBigEndian(0);
					int i2 = stream.currentOffset;
					stream.writeWordBigEndian((int) (Math.random() * 256D));
					stream.writeWordBigEndian(101);
					stream.writeWordBigEndian(233);
					stream.writeWord(45092);
					if ((int) (Math.random() * 2D) == 0)
						stream.writeWord(35784);
					stream.writeWordBigEndian((int) (Math.random() * 256D));
					stream.writeWordBigEndian(64);
					stream.writeWordBigEndian(38);
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeBytes(stream.currentOffset - i2);
				}
				int j2 = k1 * 192;
				if (j2 > 0x17f00)
					j2 = 0x17f00;
				if (j2 < 32768)
					j2 = 32768;
				if (j2 > anInt984) {
					anInt984 += (j2 - anInt984) / 24;
					return;
				}
				if (j2 < anInt984) {
					anInt984 += (j2 - anInt984) / 80;
				}
		} catch (Exception _ex) {
			signlink.reporterror("glfc_ex " + myPlayer.x + "," + myPlayer.y
					+ "," + anInt1014 + "," + anInt1015 + "," + currentRegionX
					+ "," + currentRegionY + "," + baseX + "," + baseY);
			throw new RuntimeException("eek");
		}
	}

	public void processDrawing() {
		if (loadingError || genericLoadingError) {
			showErrorScreen();
			return;
		}
		anInt1061++;
		if (!loggedIn)
			drawLoginScreen();
		else {
			drawGameScreen();
		}
		anInt1213 = 0;
	}

	private boolean isFriendOrSelf(String s) {
		if (s == null)
			return false;
		for (int i = 0; i < friendsCount; i++)
			if (s.equalsIgnoreCase(friendsList[i]))
				return true;
		return s.equalsIgnoreCase(myPlayer.name);
	}

	private static String combatDiffColor(int i, int j) {
		int k = i - j;
		if (k < -9)
			return "@red@";
		if (k < -6)
			return "@or3@";
		if (k < -3)
			return "@or2@";
		if (k < 0)
			return "@or1@";
		if (k > 9)
			return "@gre@";
		if (k > 6)
			return "@gr3@";
		if (k > 3)
			return "@gr2@";
		if (k > 0)
			return "@gr1@";
		else
			return "@yel@";
	}

	private void setWaveVolume(int i) {
		signlink.wavevol = i;
	}

	private boolean drawPane = false;

	private void draw3dScreen() {
		if (screenOpacity != 0 && screenOpacity != 255) {
			RSInterface.interfaceCache[35556].width = clientWidth;
			RSInterface.interfaceCache[35556].height = clientHeight;
			RSInterface.interfaceCache[35556].opacity = (byte) screenOpacity;
			drawInterface(0, 0, RSInterface.interfaceCache[35555], 0);
			drawInterface(0, 512, RSInterface.interfaceCache[35555], 0);
			drawInterface(0, 512 * 2, RSInterface.interfaceCache[35555], 0);
			drawInterface(0, 512 * 3, RSInterface.interfaceCache[35555], 0);
			drawInterface(0, 0, RSInterface.interfaceCache[35555], 700);
			drawInterface(0, 512, RSInterface.interfaceCache[35555], 700);
			drawInterface(0, 512 * 2, RSInterface.interfaceCache[35555], 700);
			drawInterface(0, 512 * 3, RSInterface.interfaceCache[35555], 700);
		}
		processAnAnim();
		if (showChat)
			drawSplitPrivateChat();
		if (crossType == 1) {
			crosses[crossIndex / 100]
					.drawSprite(crossX - 8 - 4, crossY - 8 - 4);
			anInt1142++;
			if (anInt1142 > 67) {
				anInt1142 = 0;
				stream.createFrame(78);
			}
		}
		if (crossType == 2)
			crosses[4 + crossIndex / 100].drawSprite(crossX - 8 - 4,
					crossY - 8 - 4);
		if (clientSize != 0 && (walkableInterfaceId == 21119 || walkableInterfaceId == 21100)) {
			processInterfaceAnimation(cycleTimer, walkableInterfaceId);
			drawInterface(0, 0, RSInterface.interfaceCache[walkableInterfaceId], 0);
		} else if (walkableInterfaceId != -1) {
			processInterfaceAnimation(cycleTimer, walkableInterfaceId);
			drawInterface(0, clientSize == 0 ? 0 : (clientWidth / 2) - 256, RSInterface.interfaceCache[walkableInterfaceId], clientSize == 0 ? 0 : (clientHeight / 2) - 167);
		}
		if (openInterfaceID != -1) {
			processInterfaceAnimation(cycleTimer, openInterfaceID);
			drawInterface(0, clientSize == 0 ? 0 : (clientWidth / 2) - 256,
					RSInterface.interfaceCache[openInterfaceID],
					clientSize == 0 ? 0 : (clientHeight / 2) - 167);
		}
		if(gravestoneInterface) {
			drawInterface(0, clientSize == 0 ? 0 : (clientWidth / 2) - 256, RSInterface.interfaceCache[37400], clientSize == 0 ? 0 : (clientHeight / 2) - 167);
		}
		if(openInterfaceID == 5292)
			drawOnBankInterface();
		//checkTutorialIsland();
		if(coinToggle) {
			int x = getCoinOrbX();
			int y = getCoinOrbY();
			coinPart.drawSprite(clientSize == 0 ? 447: x - 67, y+5);
			if(clientSize > 0)
				coinOrbPart.drawSprite(x, y);
			if(RSInterface.interfaceCache[8135].message != null) {
				String cash = getMoneyInPouch();
				newRegularFont.drawBasicString(""+cash, clientSize == 0 ? 464 : x - 47, y+21, getAmountColor((long)Integer.parseInt(RSInterface.interfaceCache[8135].message)), 0);
			}
		}
		drawGrandExchange();
		if (!menuOpen) {
			processRightClick();
			drawTooltip();
		} else if (menuScreenArea == 0)
			drawMenu();
		if (drawMultiwayIcon == 1) {
			int y = clientSize == 0 ? 296 : clientHeight - 500;
			if(clientSize == 0)
				y = gravestoneInterface ? 275 : 296;
			multiOverlay.drawSprite(clientSize == 0 ? 472 : clientWidth - 40, y);
		}
		if (fpsOn) {
			char c = '\u01FB';
			int k = 20;
			int i1 = 0xffff00;
			if (super.fps < 15)
				i1 = 0xff0000;
			drawingArea.method380("Fps:" + super.fps, c, i1, k);
			k += 15;
			Runtime runtime = Runtime.getRuntime();
			int j1 = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
			i1 = 0xffff00;
			if (j1 > 0x2000000 && lowMem)
				i1 = 0xff0000;
			drawingArea.method380("Mem:" + j1 + "k", c, 0xffff00, k);
			k += 15;
			drawingArea.method385(0xffff00, "Mouse X: " + super.mouseX
					+ " , Mouse Y: " + super.mouseY, 314, 5);
		}
		int x = baseX + (myPlayer.x - 6 >> 7);
		int y = baseY + (myPlayer.y - 6 >> 7);
		//updateInterfaces();
		if (clientData) {
			int minus = 45;
			if (super.fps < 15) {

			}
			// memGraph.draw(30, 50);
			// fpsGraph.addValue(super.fps);
			// fpsGraph.draw(30, 350);

			drawingArea
			.method385(0xffff00, "Fps: " + super.fps, 285 - minus, 5);
			Runtime runtime = Runtime.getRuntime();
			int j1 = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);

			drawingArea.method385(0xffff00, "Mem: " + j1 + "k", 299 - minus, 5);
			drawingArea.method385(0xffff00, "Mouse X: " + super.mouseX
					+ " , Mouse Y: " + super.mouseY, 314 - minus, 5);
			drawingArea.method385(0xffff00, "Coords: " + x + ", " + y + " "
					+ myPlayer.x + " " + myPlayer.y, 329 - minus, 5);
			drawingArea.method385(0xffff00, "Client resolution: " + clientWidth
					+ "x" + clientHeight, 344 - minus, 5);
			drawingArea.method385(0xffff00, "Object Maps: " + objectMaps,
					359 - minus, 5);
			drawingArea.method385(0xffff00, "Floor Maps: " + floorMaps,
					374 - minus, 5);

		}
		if (updateMinutes != 0) {
			int j = updateMinutes / 50;
			int l = j / 60;
			j %= 60;
			if (j < 10)
				drawingArea.method385(0xffff00, "System update in: " + l + ":0"
						+ j, 329, 4);
			else
				drawingArea.method385(0xffff00, "System update in: " + l + ":"
						+ j, 329, 4);
			anInt849++;
			if (anInt849 > 75) {
				anInt849 = 0;
				stream.createFrame(148);
			}
		}
	}

	private void addIgnore(long l) {
		try {
			if (l == 0L)
				return;
			if (ignoreCount >= 100) {
				pushMessage("Your ignore list is full. Max of 100 hit", 0, "");
				return;
			}
			String s = TextClass.fixName(TextClass.nameForLong(l));
			for (int j = 0; j < ignoreCount; j++)
				if (ignoreListAsLongs[j] == l) {
					pushMessage(s + " is already on your ignore list", 0, "");
					return;
				}
			for (int k = 0; k < friendsCount; k++)
				if (friendsListAsLongs[k] == l) {
					pushMessage("Please remove " + s
							+ " from your friend list first", 0, "");
					return;
				}

			ignoreListAsLongs[ignoreCount++] = l;
			needDrawTabArea = true;
			stream.createFrame(133);
			stream.writeQWord(l);
			return;
		} catch (RuntimeException runtimeexception) {
			signlink.reporterror("45688, " + l + ", " + 4 + ", "
					+ runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	private void updatePlayerInstances() {
		for (int i = -1; i < playerCount; i++) {
			int j;
			if (i == -1)
				j = myPlayerIndex;
			else
				j = playerIndices[i];
			Player player = playerArray[j];
			if (player != null)
				entityUpdateBlock(player);
		}

	}

	private void updateSpawnedObjects() {
		if (loadingStage == 2) {
			for (GameObjectSpawnRequest request = (GameObjectSpawnRequest) objectSpawnDeque
					.getFront(); request != null; request = (GameObjectSpawnRequest) objectSpawnDeque
					.getNext()) {
				if (request.removeTime > 0)
					request.removeTime--;
				if (request.removeTime == 0) {
					if (request.objectId < 0
							|| ObjectManager.isObjectModelCached(
									request.objectId, request.type)) {
						addRequestedObject(request.tileY, request.plane,
								request.face, request.type, request.tileX,
								request.objectType, request.objectId);
						request.unlink();
					}
				} else {
					if (request.spawnTime > 0)
						request.spawnTime--;
					if (request.spawnTime == 0
							&& request.tileX >= 1
							&& request.tileY >= 1
							&& request.tileX <= 102
							&& request.tileY <= 102
							&& (request.currentIDRequested < 0 || ObjectManager
									.isObjectModelCached(
											request.currentIDRequested,
											request.currentTypeRequested))) {
						addRequestedObject(request.tileY, request.plane,
								request.currentFaceRequested,
								request.currentTypeRequested, request.tileX,
								request.objectType, request.currentIDRequested);
						request.spawnTime = -1;
						if (request.currentIDRequested == request.objectId
								&& request.objectId == -1)
							request.unlink();
						else if (request.currentIDRequested == request.objectId
								&& request.currentFaceRequested == request.face
								&& request.currentTypeRequested == request.type)
							request.unlink();
					}
				}
			}

		}
	}

	private void determineMenuSize() {
		int i = boldFont.getTextWidth("Choose Option");
		for (int j = 0; j < menuActionRow; j++) {
			int k = boldFont.getTextWidth(menuActionName[j]);
			if (k > i)
				i = k;
		}
		i += 8;
		int l = 15 * menuActionRow + 21;
		if (clientSize == 0) {
			if (super.saveClickX > 4 && super.saveClickY > 4
					&& super.saveClickX < 516 && super.saveClickY < 338) {
				int i1 = super.saveClickX - 4 - i / 2;
				if (i1 + i > 512)
					i1 = 512 - i;
				if (i1 < 0)
					i1 = 0;
				int l1 = super.saveClickY - 4;
				if (l1 + l > 334)
					l1 = 334 - l;
				if (l1 < 0)
					l1 = 0;
				menuOpen = true;
				menuScreenArea = 0;
				menuOffsetX = i1;
				menuOffsetY = l1;
				menuWidth = i;
				menuHeight = 15 * menuActionRow + 22;
			}
			if (super.saveClickX > 519 && super.saveClickY > 168
					&& super.saveClickX < 765 && super.saveClickY < 503) {
				int j1 = super.saveClickX - 519 - i / 2;
				if (j1 < 0)
					j1 = 0;
				else if (j1 + i > 245)
					j1 = 245 - i;
				int i2 = super.saveClickY - 168;
				if (i2 < 0)
					i2 = 0;
				else if (i2 + l > 333)
					i2 = 333 - l;
				menuOpen = true;
				menuScreenArea = 1;
				menuOffsetX = j1;
				menuOffsetY = i2;
				menuWidth = i;
				menuHeight = 15 * menuActionRow + 22;
			}
			if (super.saveClickX > 0 && super.saveClickY > 338
					&& super.saveClickX < 516 && super.saveClickY < 503) {
				int k1 = super.saveClickX - 0 - i / 2;
				if (k1 < 0)
					k1 = 0;
				else if (k1 + i > 516)
					k1 = 516 - i;
				int j2 = super.saveClickY - 338;
				if (j2 < 0)
					j2 = 0;
				else if (j2 + l > 165)
					j2 = 165 - l;
				menuOpen = true;
				menuScreenArea = 2;
				menuOffsetX = k1;
				menuOffsetY = j2;
				menuWidth = i;
				menuHeight = 15 * menuActionRow + 22;
			}
			// if(super.saveClickX > 0 && super.saveClickY > 338 &&
			// super.saveClickX < 516 && super.saveClickY < 503) {
			if (super.saveClickX > 519 && super.saveClickY > 0
					&& super.saveClickX < 765 && super.saveClickY < 168) {
				int j1 = super.saveClickX - 519 - i / 2;
				if (j1 < 0)
					j1 = 0;
				else if (j1 + i > 245)
					j1 = 245 - i;
				int i2 = super.saveClickY - 0;
				if (i2 < 0)
					i2 = 0;
				else if (i2 + l > 168)
					i2 = 168 - l;
				menuOpen = true;
				menuScreenArea = 3;
				menuOffsetX = j1;
				menuOffsetY = i2;
				menuWidth = i;
				menuHeight = 15 * menuActionRow + 22;
			}
		} else {
			if (super.saveClickX > 0 && super.saveClickY > 0
					&& super.saveClickX < clientWidth
					&& super.saveClickY < clientHeight) {
				int i1 = super.saveClickX - 0 - i / 2;
				if (i1 + i > clientWidth)
					i1 = clientWidth - i;
				if (i1 < 0)
					i1 = 0;
				int l1 = super.saveClickY - 0;
				if (l1 + l > clientHeight)
					l1 = clientHeight - l;
				if (l1 < 0)
					l1 = 0;
				menuOpen = true;
				menuScreenArea = 0;
				menuOffsetX = i1;
				menuOffsetY = l1;
				menuWidth = i;
				menuHeight = 15 * menuActionRow + 22;
			}
		}
	}

	private void updatePlayerMovement(Stream stream) {
		stream.initBitAccess();
		int j = stream.readBits(1);
		if (j == 0)
			return;
		int k = stream.readBits(2);
		if (k == 0) {
			playersToUpdate[playersToUpdateCount++] = myPlayerIndex;
			return;
		}
		if (k == 1) {
			int l = stream.readBits(3);
			myPlayer.moveInDir(false, l);
			int k1 = stream.readBits(1);
			if (k1 == 1)
				playersToUpdate[playersToUpdateCount++] = myPlayerIndex;
			return;
		}
		if (k == 2) {
			int i1 = stream.readBits(3);
			myPlayer.moveInDir(true, i1);
			int l1 = stream.readBits(3);
			myPlayer.moveInDir(true, l1);
			int j2 = stream.readBits(1);
			if (j2 == 1)
				playersToUpdate[playersToUpdateCount++] = myPlayerIndex;
			return;
		}
		if (k == 3) {
			plane = stream.readBits(2);
			if(lastPlane != plane) 
				loadingStage = 1;
			lastPlane = plane;
			int j1 = stream.readBits(1);
			int i2 = stream.readBits(1);
			if (i2 == 1)
				playersToUpdate[playersToUpdateCount++] = myPlayerIndex;
			int k2 = stream.readBits(7);
			int l2 = stream.readBits(7);
			myPlayer.setPos(l2, k2, j1 == 1);
		}
	}
	
	private int lastPlane;

	private void nullLoader() {
	}

	private boolean processInterfaceAnimation(int i, int interfaceId) {
		boolean flag1 = false;
		RSInterface rsi = RSInterface.interfaceCache[interfaceId];
		if (rsi == null)
			return false;
		if (rsi.children == null)
			return false;
		for (int childId = 0; childId < rsi.children.length; childId++) {
			if (rsi.children[childId] == -1)
				break;
			RSInterface child = RSInterface.interfaceCache[rsi.children[childId]];
			if (child.type == 1)
				flag1 |= processInterfaceAnimation(i, child.id);
			if (child.type == 6
					&& (child.disabledAnimationId != -1 || child.enabledAnimationId != -1)) {
				boolean flag2 = interfaceIsSelected(child);
				int l;
				if (flag2)
					l = child.enabledAnimationId;
				else
					l = child.disabledAnimationId;
				if (l != -1) {
					Animation animation = Animation.anims[l];
					for (child.frameTimer += i; child.frameTimer > animation
							.getFrameLength(child.currentFrame);) {
						child.frameTimer -= animation
								.getFrameLength(child.currentFrame) + 1;
						child.currentFrame++;
						if (child.currentFrame >= animation.frameCount) {
							child.currentFrame -= animation.loopDelay;
							if (child.currentFrame < 0
									|| child.currentFrame >= animation.frameCount)
								child.currentFrame = 0;
						}
						flag1 = true;
					}

				}
			}
		}

		return flag1;
	}

	private int getCameraHeight() {
		int j = 3;
		if (yCameraCurve < 310) {
			int k = xCameraPos >> 7;
		int l = yCameraPos >> 7;
							int i1 = myPlayer.x >> 7;
		int j1 = myPlayer.y >> 7;
				if ((byteGroundArray[plane][k][l] & 4) != 0)
					j = plane;
				int k1;
				if (i1 > k)
					k1 = i1 - k;
				else
					k1 = k - i1;
				int l1;
				if (j1 > l)
					l1 = j1 - l;
				else
					l1 = l - j1;
				if (k1 > l1) {
					int i2 = (l1 * 0x10000) / k1;
					int k2 = 32768;
					while (k != i1) {
						if (k < i1)
							k++;
						else if (k > i1)
							k--;
						if ((byteGroundArray[plane][k][l] & 4) != 0)
							j = plane;
						k2 += i2;
						if (k2 >= 0x10000) {
							k2 -= 0x10000;
							if (l < j1)
								l++;
							else if (l > j1)
								l--;
							if ((byteGroundArray[plane][k][l] & 4) != 0)
								j = plane;
						}
					}
				} else {
					int j2 = (k1 * 0x10000) / l1;
					int l2 = 32768;
					while (l != j1) {
						if (l < j1)
							l++;
						else if (l > j1)
							l--;
						if ((byteGroundArray[plane][k][l] & 4) != 0)
							j = plane;
						l2 += j2;
						if (l2 >= 0x10000) {
							l2 -= 0x10000;
							if (k < i1)
								k++;
							else if (k > i1)
								k--;
							if ((byteGroundArray[plane][k][l] & 4) != 0)
								j = plane;
						}
					}
				}
		}
		if ((byteGroundArray[plane][myPlayer.x >> 7][myPlayer.y >> 7] & 4) != 0)
			j = plane;
		return j;
	}

	private int getCamHeight() {
		int j = getFloorDrawHeight(plane, yCameraPos, xCameraPos);
		if (j - zCameraPos < 800
				&& (byteGroundArray[plane][xCameraPos >> 7][yCameraPos >> 7] & 4) != 0)
			return plane;
		else
			return 3;
	}

	private void delIgnore(long l) {
		try {
			if (l == 0L)
				return;
			for (int j = 0; j < ignoreCount; j++)
				if (ignoreListAsLongs[j] == l) {
					ignoreCount--;
					needDrawTabArea = true;
					System.arraycopy(ignoreListAsLongs, j + 1,
							ignoreListAsLongs, j, ignoreCount - j);
					stream.createFrame(74);
					stream.writeQWord(l);
					return;
				}

			return;
		} catch (RuntimeException runtimeexception) {
			signlink.reporterror("47229, " + 3 + ", " + l + ", "
					+ runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	public String getParameter(String s) {
		if (signlink.mainapp != null)
			return signlink.mainapp.getParameter(s);
		else
			return super.getParameter(s);
	}

	private int extractInterfaceValues(RSInterface rsi, int j) {
		if (rsi.valueIndexArray == null || j >= rsi.valueIndexArray.length)
			return -2;
		try {
			int myValues[] = rsi.valueIndexArray[j];
			int k = 0;
			int valueIdx = 0;
			int i1 = 0;
			do {
				int checkType = myValues[valueIdx++];
				int returnValue = 0;
				byte byte0 = 0;
				if (checkType == 0)
					return k;
				if (checkType == 1)
					returnValue = currentStats[myValues[valueIdx++]];
				if (checkType == 2)
					returnValue = currentMaxStats[myValues[valueIdx++]];
				if (checkType == 3)
					returnValue = currentExp[myValues[valueIdx++]];
				if (checkType == 4) {
					RSInterface interfaceToCheckOn = RSInterface.interfaceCache[myValues[valueIdx++]];
					int itemId = myValues[valueIdx++];
					for (int j3 = 0; j3 < interfaceToCheckOn.inv.length; j3++)
						if (interfaceToCheckOn.inv[j3] == itemId + 1)
							returnValue += interfaceToCheckOn.invStackSizes[j3];
				}
				/**
				 * Returns corresponding varsettings
				 */
				if (checkType == 5)
					returnValue = variousSettings[myValues[valueIdx++]];
				/**
				 * Returns your xp in given skill
				 */
				if (checkType == 6)
					returnValue = levelXPs[currentMaxStats[myValues[valueIdx++]] - 1];
				/**
				 * Checks if something is higher than 46874
				 */
				if (checkType == 7)
					returnValue = (variousSettings[myValues[valueIdx++]] * 100) / 46875;
				/**
				 * Gets combat level
				 */
				if (checkType == 8)
					returnValue = myPlayer.combatLevel;
				/**
				 * Gets total level
				 */
				if (checkType == 9) {
					for (int l1 = 0; l1 < Skills.skillsCount; l1++)
						if (Skills.skillEnabled[l1])
							returnValue += currentMaxStats[l1];

				}
				/**
				 * Checks if the itemid is along the items shown in the
				 * interface
				 */
				if (checkType == 10) {
					RSInterface itemInterface = RSInterface.interfaceCache[myValues[valueIdx++]];
					int itemId = myValues[valueIdx++] + 1;
					for (int k3 = 0; k3 < itemInterface.inv.length; k3++) {
						if (itemInterface.inv[k3] != itemId)
							continue;
						returnValue = 0x3b9ac9ff;
						break;
					}
				}
				/**
				 * Looks up run energy
				 */
				if (checkType == 11)
					returnValue = currentEnergy;
				/**
				 * Looks up weight
				 */
				if (checkType == 12)
					returnValue = weight;
				if (checkType == 13) {
					int required = variousSettings[myValues[valueIdx++]];
					int current = myValues[valueIdx++];
					returnValue = (required & 1 << current) == 0 ? 0 : 1;
				}
				if (checkType == 14) {
					int j2 = myValues[valueIdx++];
					VarBit varBit = VarBit.cache[j2];
					int l3 = varBit.configId;
					int i4 = varBit.leastSignificantBit;
					int j4 = varBit.mostSignificantBit;
					int k4 = anIntArray1232[j4 - i4];
					returnValue = variousSettings[l3] >> i4 & k4;
				}
				if (checkType == 15)
					byte0 = 1;
				if (checkType == 16)
					byte0 = 2;
				if (checkType == 17)
					byte0 = 3;
				if (checkType == 18)
					returnValue = (myPlayer.x >> 7) + baseX;
				if (checkType == 19)
					returnValue = (myPlayer.y >> 7) + baseY;
				if (checkType == 20)
					returnValue = myValues[valueIdx++];
				if (byte0 == 0) {
					if (i1 == 0)
						k += returnValue;
					if (i1 == 1)
						k -= returnValue;
					if (i1 == 2 && returnValue != 0)
						k /= returnValue;
					if (i1 == 3)
						k *= returnValue;
					i1 = 0;
				} else {
					i1 = byte0;
				}
			} while (true);
		} catch (Exception _ex) {
			return -1;
		}
	}

	public void drawTooltip() {
		if(getOption("cursors")) {
			int newCursor = getCursorID();
			if(cursor != newCursor) {
				setCursor(newCursor);
			}
		}
		if (menuActionRow < 2 && itemSelected == 0 && spellSelected == 0) {
			return;
		}
		String s;
		if (itemSelected == 1 && menuActionRow < 2)
			s = "Use " + selectedItemName + " with...";
		else if (spellSelected == 1 && menuActionRow < 2)
			s = spellTooltip + "...";
		else
			s = menuActionName[menuActionRow - 1];
		if (!s.contains("Walk here") && getOption("tooltip_hover")) {
			boolean add = getOption("cursors") && cursor > 0;
			drawHoverBox(super.mouseX + (add ? 20 : 10), super.mouseY - (add ? 20 : 10), 0x101010,
					0xFFFFFF, s);
		}
		if (menuActionRow > 2)
			s = s + "@whi@ / " + (menuActionRow - 2) + " more options";
		chatTextDrawingArea.method390(4, 0xffffff, s, loopCycle / 1000, 15);
	}

	private void npcScreenPos(Entity entity, int i) {
		calcEntityScreenPos(entity.x, i, entity.y);
	}

	private void calcEntityScreenPos(int i, int j, int l) {
		if (i < 128 || l < 128 || i > 13056 || l > 13056) {
			spriteDrawX = -1;
			spriteDrawY = -1;
			return;
		}
		int i1 = getFloorDrawHeight(plane, l, i) - j;
		i -= xCameraPos;
		i1 -= zCameraPos;
		l -= yCameraPos;
		int j1 = Model.SINE[yCameraCurve];
		int k1 = Model.COSINE[yCameraCurve];
		int l1 = Model.SINE[xCameraCurve];
		int i2 = Model.COSINE[xCameraCurve];
		int j2 = l * l1 + i * i2 >> 16;
		l = l * i2 - i * l1 >> 16;
		i = j2;
		j2 = i1 * k1 - l * j1 >> 16;
		l = i1 * j1 + l * k1 >> 16;
		i1 = j2;
		if (l >= 50) {
			spriteDrawX = Rasterizer.center_x + (i << Client.log_view_dist) / l;
			spriteDrawY = Rasterizer.center_y + (i1 << Client.log_view_dist)
					/ l;
		} else {
			spriteDrawX = -1;
			spriteDrawY = -1;
		}
	}

	private void buildSplitPrivateChatMenu() {
		if (splitPrivateChat == 0)
			return;
		int i = 0;
		if (updateMinutes != 0)
			i = 1;
		for (int j = 0; j < 100; j++)
			if (chatMessages[j] != null) {
				int k = chatTypes[j];
				String name = chatNames[j];
				if (name != null && name.indexOf("@") == 0) {
					// name = name.substring(5);
				}
				if ((k == 3 || k == 7)
						&& (k == 7 || privateChatMode == 0 || privateChatMode == 1
						&& isFriendOrSelf(name))) {
					int l = (clientHeight - 174) - i * 13;
					if (super.mouseX > (clientSize == 0 ? 4 : 0)
							&& super.mouseY - (clientSize == 0 ? 4 : 0) > l - 10
							&& super.mouseY - (clientSize == 0 ? 4 : 0) <= l + 3) {
						int i1 = normalFont.getTextWidth("From:  " + name
								+ chatMessages[j]) + 25;
						if (i1 > 450)
							i1 = 450;
						if (super.mouseX < (clientSize == 0 ? 4 : 0) + i1) {
							if (myRights >= 1) {
								menuActionName[menuActionRow] = "Report abuse @whi@"
										+ name;
								menuActionID[menuActionRow] = 2606;
								menuActionRow++;
							}
							if (!isFriendOrSelf(name)) {
								menuActionName[menuActionRow] = "Add ignore @whi@"
										+ name;
								menuActionID[menuActionRow] = 2042;
								menuActionRow++;
								menuActionName[menuActionRow] = "Add friend @whi@"
										+ name;
								menuActionID[menuActionRow] = 2337;
								menuActionRow++;
							}
							if (isFriendOrSelf(name)) {
								menuActionName[menuActionRow] = "Message @whi@"
										+ name;
								menuActionID[menuActionRow] = 2639;
								menuActionRow++;
							}
						}
					}
					if (++i >= 5)
						return;
				}
				if ((k == 5 || k == 6) && privateChatMode < 2 && ++i >= 5)
					return;
			}

	}

	private void createObjectSpawnRequest(int j, int objectId, int l, int i1,
			int j1, int k1, int l1, int i2, int j2) {
		GameObjectSpawnRequest newRequest = null;
		for (GameObjectSpawnRequest request = (GameObjectSpawnRequest) objectSpawnDeque
				.getFront(); request != null; request = (GameObjectSpawnRequest) objectSpawnDeque
				.getNext()) {
			if (request.plane != l1 || request.tileX != i2
					|| request.tileY != j1 || request.objectType != i1)
				continue;
			newRequest = request;
			break;
		}

		if (newRequest == null) {
			newRequest = new GameObjectSpawnRequest();
			newRequest.plane = l1;
			newRequest.objectType = i1;
			newRequest.tileX = i2;
			newRequest.tileY = j1;
			assignOldValuesToNewRequest(newRequest);
			objectSpawnDeque.insertBack(newRequest);
		}
		newRequest.currentIDRequested = objectId;
		newRequest.currentTypeRequested = k1;
		newRequest.currentFaceRequested = l;
		newRequest.spawnTime = j2;
		newRequest.removeTime = j;
	}

	private boolean interfaceIsSelected(RSInterface rsInterface) {
		if (rsInterface.valueCompareType == null)
			return false;
		for (int i = 0; i < rsInterface.valueCompareType.length; i++) {
			int myValue = extractInterfaceValues(rsInterface, i);
			int minValue = rsInterface.requiredValues[i];
			if (rsInterface.valueCompareType[i] == 2) {
				if (myValue >= minValue)
					return false;
			} else if (rsInterface.valueCompareType[i] == 3) {
				if (myValue <= minValue)
					return false;
			} else if (rsInterface.valueCompareType[i] == 4) {
				if (myValue == minValue)
					return false;
			} else if (rsInterface.valueCompareType[i] == 10) {
				if (myValue < minValue)
					return false;
			} else if (myValue != minValue)
				return false;
		}

		return true;
	}

	private void connectToUpdateServer() {
		int j = 5;
		expectedCRCs[8] = 0;
		int k = 0;
		while (expectedCRCs[8] == 0) {
			String s = "Unknown problem";
			drawLoadingText(10, "Connecting to Voxyde...");
			try {
				DataInputStream datainputstream = openJagGrabInputStream("crc"
						+ (int) (Math.random() * 99999999D) + "-" + 317);
				Stream stream = new Stream(new byte[40]);
				datainputstream.readFully(stream.buffer, 0, 40);
				datainputstream.close();
				for (int i1 = 0; i1 < 9; i1++)
					expectedCRCs[i1] = stream.readInt();

				int j1 = stream.readInt();
				int k1 = 1234;
				for (int l1 = 0; l1 < 9; l1++)
					k1 = (k1 << 1) + expectedCRCs[l1];

				if (j1 != k1) {
					s = "checksum problem";
					expectedCRCs[8] = 0;
				}
			} catch (EOFException _ex) {
				s = "EOF problem";
				expectedCRCs[8] = 0;
			} catch (IOException _ex) {
				s = "Connection problem";
				expectedCRCs[8] = 0;
			} catch (Exception _ex) {
				s = "logic problem";
				expectedCRCs[8] = 0;
				if (!signlink.reporterror)
					return;
			}
			if (expectedCRCs[8] == 0) {
				k++;
				for (int l = j; l > 0; l--) {
					if (k >= 10) {
						drawLoadingText(10, "Game updated - please reload page");
						l = 10;
					} else {
						drawLoadingText(10, s + " - Will retry in " + l
								+ " secs.");
					}
					try {
						Thread.sleep(1000L);
					} catch (Exception _ex) {
					}
				}

				j *= 2;
				if (j > 60)
					j = 60;
			}
		}
	}

	private DataInputStream openJagGrabInputStream(String s) throws IOException {
		// if(!aBoolean872)
		// if(signlink.mainapp != null)
		// return signlink.openurl(s);
		// else
		// return new DataInputStream((new URL(getCodeBase(), s)).openStream());
		if (jagGrabSocket != null) {
			try {
				jagGrabSocket.close();
			} catch (Exception _ex) {
			}
			jagGrabSocket = null;
		}
		jagGrabSocket = openSocket(43596, Configuration.JAGGRAB_HOST);
		jagGrabSocket.setSoTimeout(10000);
		java.io.InputStream inputstream = jagGrabSocket.getInputStream();
		OutputStream outputstream = jagGrabSocket.getOutputStream();
		outputstream.write(("JAGGRAB /" + s + "\n\n").getBytes());
		return new DataInputStream(inputstream);
	}

	private void updatePlayer(Stream stream) {
		int j = stream.readBits(8);
		if (j < playerCount) {
			for (int k = j; k < playerCount; k++)
				anIntArray840[anInt839++] = playerIndices[k];

		}
		if (j > playerCount) {
			//signlink.reporterror(myUsername + " Too many players");
			throw new RuntimeException("eek");
		}
		playerCount = 0;
		for (int l = 0; l < j; l++) {
			int i1 = playerIndices[l];
			Player player = playerArray[i1];
			int j1 = stream.readBits(1);
			if (j1 == 0) {
				playerIndices[playerCount++] = i1;
				player.loopCycle = loopCycle;
			} else {
				int k1 = stream.readBits(2);
				if (k1 == 0) {
					playerIndices[playerCount++] = i1;
					player.loopCycle = loopCycle;
					playersToUpdate[playersToUpdateCount++] = i1;
				} else if (k1 == 1) {
					playerIndices[playerCount++] = i1;
					player.loopCycle = loopCycle;
					int l1 = stream.readBits(3);
					player.moveInDir(false, l1);
					int j2 = stream.readBits(1);
					if (j2 == 1)
						playersToUpdate[playersToUpdateCount++] = i1;
				} else if (k1 == 2) {
					playerIndices[playerCount++] = i1;
					player.loopCycle = loopCycle;
					int i2 = stream.readBits(3);
					player.moveInDir(true, i2);
					int k2 = stream.readBits(3);
					player.moveInDir(true, k2);
					int l2 = stream.readBits(1);
					if (l2 == 1)
						playersToUpdate[playersToUpdateCount++] = i1;
				} else if (k1 == 3)
					anIntArray840[anInt839++] = i1;
			}
		}
	}

	public int terrainRegionX;
	public int terrainRegionY;
	public int titleWidth = -1;
	public int titleHeight = -1;
	public ScriptManager scriptManager;

	public void generateWorld(int x, int y) {
		terrainRegionX = x;
		terrainRegionY = y;
		requestMapReconstruct = requestMapReconstruct2 = false;
		if (currentRegionX == x && currentRegionY == y && loadingStage == 2) {
			return;
		}
		currentRegionX = x;
		currentRegionY = y;
		baseX = (currentRegionX - 6) * 8;
		baseY = (currentRegionY - 6) * 8;
		inTutorialIsland = (currentRegionX / 8 == 48 || currentRegionX / 8 == 49)
				&& currentRegionY / 8 == 48;
		if (currentRegionX / 8 == 48 && currentRegionY / 8 == 148)
			inTutorialIsland = true;
		loadingStage = 1;
		mapLoadingTime = System.currentTimeMillis();
		int k16 = 0;
		for (int i21 = (currentRegionX - 6) / 8; i21 <= (currentRegionX + 6) / 8; i21++) {
			for (int k23 = (currentRegionY - 6) / 8; k23 <= (currentRegionY + 6) / 8; k23++)
				k16++;
		}
		terrainData = new byte[k16][];
		objectData = new byte[k16][];
		mapCoordinates = new int[k16];
		terrainIndices = new int[k16];
		objectIndices = new int[k16];
		k16 = 0;
		for (int l23 = (currentRegionX - 6) / 8; l23 <= (currentRegionX + 6) / 8; l23++) {
			for (int j26 = (currentRegionY - 6) / 8; j26 <= (currentRegionY + 6) / 8; j26++) {
				mapCoordinates[k16] = (l23 << 8) + j26;
				if (inTutorialIsland
						&& (j26 == 49 || j26 == 149 || j26 == 147 || l23 == 50 || l23 == 49
						&& j26 == 47)) {
					terrainIndices[k16] = -1;
					objectIndices[k16] = -1;
					k16++;
				} else {
					int k28 = terrainIndices[k16] = onDemandFetcher
							.getMapIdForRegions(0, j26, l23);
					if (k28 != -1)
						onDemandFetcher.requestFileData(3, k28);
					int j30 = objectIndices[k16] = onDemandFetcher
							.getMapIdForRegions(1, j26, l23);
					if (j30 != -1)
						onDemandFetcher.requestFileData(3, j30);
					k16++;
				}
			}
		}
		int i17 = baseX - anInt1036;
		int j21 = baseY - anInt1037;
		anInt1036 = baseX;
		anInt1037 = baseY;
		for (int j24 = 0; j24 < 16384; j24++) {
			NPC npc = npcArray[j24];
			if (npc != null) {
				for (int j29 = 0; j29 < 10; j29++) {
					npc.pathX[j29] -= i17;
					npc.pathY[j29] -= j21;
				}
				npc.x -= i17 * 128;
				npc.y -= j21 * 128;
			}
		}
		for (int i27 = 0; i27 < maxPlayers; i27++) {
			Player player = playerArray[i27];
			if (player != null) {
				for (int i31 = 0; i31 < 10; i31++) {
					player.pathX[i31] -= i17;
					player.pathY[i31] -= j21;
				}
				player.x -= i17 * 128;
				player.y -= j21 * 128;
			}
		}
		loadingMap = true;
		byte byte1 = 0;
		byte byte2 = 104;
		byte byte3 = 1;
		if (i17 < 0) {
			byte1 = 103;
			byte2 = -1;
			byte3 = -1;
		}
		byte byte4 = 0;
		byte byte5 = 104;
		byte byte6 = 1;
		if (j21 < 0) {
			byte4 = 103;
			byte5 = -1;
			byte6 = -1;
		}
		for (int k33 = byte1; k33 != byte2; k33 += byte3) {
			for (int l33 = byte4; l33 != byte5; l33 += byte6) {
				int i34 = k33 + i17;
				int j34 = l33 + j21;
				for (int k34 = 0; k34 < 4; k34++)
					if (i34 >= 0 && j34 >= 0 && i34 < 104 && j34 < 104)
						groundArray[k34][k33][l33] = groundArray[k34][i34][j34];
					else
						groundArray[k34][k33][l33] = null;
			}
		}
		for (GameObjectSpawnRequest spawnReq = (GameObjectSpawnRequest) objectSpawnDeque
				.getFront(); spawnReq != null; spawnReq = (GameObjectSpawnRequest) objectSpawnDeque
				.getNext()) {
			spawnReq.tileX -= i17;
			spawnReq.tileY -= j21;
			if (spawnReq.tileX < 0 || spawnReq.tileY < 0
					|| spawnReq.tileX >= 104 || spawnReq.tileY >= 104)
				spawnReq.unlink();
		}
		if (destX != 0) {
			destX -= i17;
			destY -= j21;
		}
		inCutScene = false;
	}

	public void resetWorld(int stage) {
		if (stage == 0) {
			currentSound = 0;
			cameraOffsetX = (int) (Math.random() * 100D) - 50;
			cameraOffsetY = (int) (Math.random() * 110D) - 55;
			viewRotationOffset = (int) (Math.random() * 80D) - 40;
			minimapRotation = (int) (Math.random() * 120D) - 60;
			minimapZoom = (int) (Math.random() * 30D) - 20;
			viewRotation = (int) (Math.random() * 20D) - 10 & 0x7ff;
			minimapStatus = 0;
			loadingStage = 1;
		} else if (stage == 1) {
			loadingMap = false;
		}
	}

	void loginScreenBG(boolean b) {
		xCameraPos = 6100;
		yCameraPos = 6867;
		zCameraPos = -750;
		xCameraCurve = 2040;
		yCameraCurve = 383;
		resetWorld(0);
		if (b || scriptManager == null) {
			scriptManager = new ScriptManager(this);
		} else {
			scriptManager.update();
		}
		plane = scriptManager.regionPlane;
		generateWorld(scriptManager.terrainRegionX,
				scriptManager.terrainRegionY);
		resetWorld(1);
	}

	private int opacity = 256;

	private void loopLayerTransparency(int xPos, int YPos, int width,
			int height, int speed) {
		if (opacity >= 0)
			opacity -= speed;
		else
			opacity = 0;
		DrawingArea474.drawAlphaFilledPixels(xPos, YPos, width, height, 0,
				opacity);
	}

	private boolean bgCheck = true;

	public RealFont[] arial = { new RealFont(this, "Arial", 0, 10, true),
			new RealFont(this, "Arial", 0, 12, true),
			new RealFont(this, "Arial", 0, 14, true) };

	private int candleStatus, candleCycle;
	private boolean candleDir;

	private void candleCycle() {
		if (candleCycle >= 8) {
			candleCycle = 0;
			if (!candleDir) {
				if (candleStatus == 2) {
					candleDir = true;
				} else
					candleStatus++;
			} else {
				if (candleStatus == 0) {
					candleDir = false;
				} else {
					candleStatus--;
				}
			}
		} else {
			candleCycle++;
		}
	}


	private boolean rememberMe;

	public boolean onInputBox(int t) {
		boolean x = super.mouseX >= centerX-118 && super.mouseX <= centerX+117;
		boolean y = false;
		switch(t) {
		case 1:
			y = super.mouseY >= centerY-114 && super.mouseY <= centerY-84;
			break;
		case 2:
			y = super.mouseY >= centerY-45 && super.mouseY <= centerY+12;
			break;
		}
		return x && y;
	}
	
	private int[] alpha = new int[9];
	Sprite logo;
	Sprite bg5;
	Sprite bo;
	Sprite bo2;
	Sprite b3;
	private boolean settingsToggled;
	
	public void drawLoginScreen() {
		resetImageProducers();
		titleScreen.initDrawingArea();
		TextDrawingArea.drawPixels(0, 0, getWidth(), getHeight(), 0x010101);
		//refreshClientScreen();
		checkClientSize(false);
		/*
		 * Drawing sprites
		 */
		if (clientWidth2 < 766) {
			newbg3.drawAdvancedSprite(centerX- 385, clientWidth2 <= 765 ? 0 : centerY-277);
		} else {
			newbg3.drawAdvancedSprite(0, 0);
		}
		logo.drawAdvancedSprite(centerX-300, 0);
		drawBorder(null, borderImages, -25, centerY-150, 110, 200, 75, 0, true, false);


		loginImages[0].drawAdvancedSprite(17, centerY-10);
		loginImages[1].drawAdvancedSprite(17, centerY-10, alpha[0]);
		loginImages[2].drawAdvancedSprite(17, centerY-70);
		loginImages[3].drawAdvancedSprite(17, centerY-70, alpha[2]);
		handleAlphaHovering();

		loginImages[4].drawAdvancedSprite(17, centerY-135);
		loginImages[10].drawAdvancedSprite(17, centerY-135, alpha[1]);
		int clientH = clientHeight2;
		if(clientSize == 0)
			clientH -= 52;
		else if(clientSize == 2)
			clientH -= 20;
		drawBorder(null, borderImages, 2, clientH - 60, 130, 50, 150, 0, true, false);
		chatTextDrawingArea.drawRegularText(true, 32, 16777215, "Version: "+Configuration.CLIENT_VERSION, clientH-26);
		if (!settingsToggled) {
			
			drawBorder(null, borderImages, centerX-145, centerY-150, 280, 220, 150, 0, true, false);

			bo.drawAdvancedSprite(centerX-55, centerY+25);
			bo2.drawAdvancedSprite(centerX-55, centerY+25, alpha[3]);
			b3.drawAdvancedSprite(centerX-125, centerY-120);
			b3.drawAdvancedSprite(centerX-125, centerY-50);
			loginImages[5].drawAdvancedSprite(centerX+95, centerY-35);
			//loginImages[15].drawSprite(centerX+95, centerY+5);
			loginImages[6].drawAdvancedSprite(centerX+95, centerY-105);
			newRegularFont.drawBasicString("Username:", centerX-130, centerY-125, 0xffffff, 0x000000);
			newRegularFont.drawBasicString("Password:", centerX-130, centerY-55, 0xffffff, 0x000000);

			newRegularFont.drawBasicString("Recover Password", centerX+16, centerY+8, 0xffffff, 0x000000);
			DrawingArea474.drawAlphaHorizontalLine(centerX+16, centerY+8, 103, 0xffffff, alpha[8]);

			if(myUsername.length() > 0) 
				myUsername = optimizeText(myUsername);
			String stars = getStars(myPassword);

			chatTextDrawingArea.drawRegularText(true, centerX-115, 16777215, myUsername, centerY-90);
			chatTextDrawingArea.drawRegularText(true, centerX-115, 16777215, stars, centerY-20);
			if (loopCycle % 45 < 10) {
				if(loginScreenCursorPos == 0)
					chatTextDrawingArea.drawRegularText(true, centerX-115, 16777215, myUsername+"|", centerY-90);
				else if(loginScreenCursorPos == 1)
					chatTextDrawingArea.drawRegularText(true, centerX-115, 16777215, stars+"|", centerY-20);
			}
			if (loginMessage1.length() > 0 || loginMessage2.length() > 0) {
				drawBorder(null, borderImages, centerX-115, centerY+80, 225, 75, 150, 0, true, false);
				chatTextDrawingArea.drawRegularText(true, centerX-55 - loginMessage1.length(), 16777215, loginMessage1, centerY+105);
				chatTextDrawingArea.drawRegularText(true, centerX-60 - loginMessage2.length(), 16777215, loginMessage2, centerY+120);
				newBoldFont.drawBasicString("Click here to dismiss", centerX-65, centerY+144, 0xFF0000, -1);
				DrawingArea474.drawAlphaHorizontalLine(centerX-65, centerY+144, 135, 0xFF0000, 255);
			}
		} else {
			drawBorder(null, borderImages, centerX-100, centerY-160, 200, 300, 150, 0, true, false);

			//Remember me
			newBoldFont.drawBasicString("Save username:", centerX-85, centerY-135, 0xFFFF00, -1);
			int index = rememberMe ? 9 : 8;
			loginImages[index].drawAdvancedSprite(centerX+72, centerY-148);

			//Constitution
			newBoldFont.drawBasicString("Constitution:", centerX-85, centerY-115, 0xFFFF00, -1);
			index = getOption("constitution") ? 9 : 8;
			loginImages[index].drawAdvancedSprite(centerX+72, centerY-128);

			//HD textures
			newBoldFont.drawBasicString("HD textures:", centerX-85, centerY-95, 0xFFFF00, -1);
			index = getOption("hd_tex") ? 9 : 8;
			loginImages[index].drawAdvancedSprite(centerX+72, centerY-108);

			//Old frame
			newBoldFont.drawBasicString("Old frame:", centerX-85, centerY-75, 0xFFFF00, -1);
			index = getOption("old_frame") ? 9 : 8;
			loginImages[index].drawAdvancedSprite(centerX+72, centerY-88);

			//Old hits
			newBoldFont.drawBasicString("Old hits:", centerX-85, centerY-55, 0xFFFF00, -1);
			index = getOption("old_hits") ? 9 : 8;
			loginImages[index].drawAdvancedSprite(centerX+72, centerY-68);

			//Tooltips
			newBoldFont.drawBasicString("Tooltips:", centerX-85, centerY-35, 0xFFFF00, -1);
			index = getOption("tooltip_hover") ? 9 : 8;
			loginImages[index].drawAdvancedSprite(centerX+72, centerY-48);

			//Cursors
			newBoldFont.drawBasicString("Cursors:", centerX-85, centerY-15, 0xFFFF00, -1);
			index = getOption("cursors") ? 9 : 8;
			loginImages[index].drawAdvancedSprite(centerX+72, centerY-28);

			//Smilies
			newBoldFont.drawBasicString("Smilies:", centerX-85, centerY+5, 0xFFFF00, -1);
			index = getOption("smilies") ? 9 : 8;
			loginImages[index].drawAdvancedSprite(centerX+72, centerY-8);

			//Censor
			newBoldFont.drawBasicString("Censor:", centerX-85, centerY+25, 0xFFFF00, -1);
			index = getOption("censor_active") ? 9 : 8;
			loginImages[index].drawAdvancedSprite(centerX+72, centerY+12);

			//FOG
			newBoldFont.drawBasicString("FOG:", centerX-85, centerY+45, 0xFFFF00, -1);
			index = getOption("fog_active") ? 9 : 8;
			loginImages[index].drawAdvancedSprite(centerX+72, centerY+32);


			//Client size
			String s = clientSize == 0 ? "small" : clientSize == 1 ? "resized" : clientSize == 2 ? "full" : "small";
			newBoldFont.drawBasicString("Client screen size: "+s+"", centerX-85, centerY+80, 0xFFFF00, -1);

			if(clientSize != 0) {
				fixed.drawAdvancedSprite(centerX-82, centerY+90);
				loginImages[12].drawAdvancedSprite(centerX-82, centerY+90, alpha[5]);
			} else
				loginImages[12].drawAdvancedSprite(centerX-82, centerY+90);


			if(clientSize != 1) {
				resize.drawAdvancedSprite(centerX-23, centerY+90);
				loginImages[13].drawAdvancedSprite(centerX-23, centerY+90, alpha[6]);
			} else
				loginImages[13].drawAdvancedSprite(centerX-23, centerY+90);

			if(clientSize != 2) {
				fullscreen.drawAdvancedSprite(centerX + 36, centerY+90);
				loginImages[14].drawAdvancedSprite(centerX + 36, centerY+90, alpha[7]);
			} else
				loginImages[14].drawAdvancedSprite(centerX + 36, centerY+90);

		}
		loginImages[settingsToggled ? 11 : 7].drawAdvancedSprite(clientSize == 0 ? clientWidth2 - 59 : clientWidth2 - 52, clientH - 54);
		loginImages[11].drawAdvancedSprite(clientSize == 0 ? clientWidth2 - 59 : clientWidth2 - 52, clientH - 54, alpha[4]);


		titleScreen.drawGraphics(0, super.graphics, 0);
	}

	boolean handCursor;

	void handleAlphaHovering() {
		handCursor = false;
		if (!mouseInRegion(17, centerY-70, 17+loginImages[0].myWidth, centerY-70+loginImages[0].myHeight)) {
			alpha[2] -= (alpha[2] > 25 ? 25 : 0);
		} else {
			alpha[2] += (alpha[2] < 225 ? 25 : 0);
			handCursor = true;
		}

		if (!mouseInRegion(centerX-52, centerY+27, centerX+59, centerY+57)) {
			alpha[3] -= (alpha[3] > 25 ? 25 : 0);
		} else {
			alpha[3] += (alpha[3] < 225 ? 25 : 0);
			handCursor = true;
		}

		if (!mouseInRegion(17, centerY-10, 17+loginImages[0].myWidth, centerY-10+loginImages[0].myHeight)) {
			alpha[0] -= (alpha[0] > 25 ? 25 : 0);
		} else {
			alpha[0] += (alpha[0] < 225 ? 25 : 0);
			handCursor = true;
		}

		if (!mouseInRegion(clientWidth2 - 52, clientHeight2 - (clientSize == 0 ? 108 : 58), clientWidth2 - 4, clientHeight2 - 4)) {
			alpha[4] -= (alpha[4] > 25 ? 25 : 0);
		} else {
			alpha[4] += (alpha[4] < 225 ? 25 : 0);
			handCursor = true;
		}

		if (!mouseInRegion(23, centerY-135, 68, centerY-80)) {
			alpha[1] -= (alpha[1] > 25 ? 25 : 0);
		} else {
			alpha[1] += (alpha[1] < 225 ? 25 : 0);
			handCursor = true;
		}

		if (!mouseInRegion(centerX+14, centerY - 5, centerX+125, centerY + 7)) {
			alpha[8] = 150;
		} else {
			alpha[8] = 225;
			handCursor = true;
		}

		if(mouseInRegion(centerX-70, centerY+130, centerX+65, centerY+142) && (loginMessage1.length() > 0 || loginMessage2.length() > 0)) {
			handCursor = true;
		}

		if (settingsToggled) {
			if (!mouseInRegion(centerX-82, centerY + 88, centerX-32, centerY + 123)) {
				alpha[5] -= (alpha[5] > 25 ? 25 : 0);
			} else {
				alpha[5] += (alpha[5] < 225 ? 25 : 0);
				handCursor = true;
			}
			if (!mouseInRegion(centerX-24, centerY + 88, centerX+27, centerY + 123)) {
				alpha[6] -= (alpha[6] > 25 ? 25 : 0);
			} else {
				alpha[6] += (alpha[6] < 225 ? 25 : 0);
				handCursor = true;
			}
			if (!mouseInRegion(centerX+40, centerY + 88, centerX+84, centerY + 123)) {
				alpha[7] -= (alpha[7] > 25 ? 25 : 0);
			} else {
				alpha[7] += (alpha[7] < 225 ? 25 : 0);
				handCursor = true;
			}
			if(super.mouseY >= centerY - 150 && super.mouseY <= centerY - 136) {
				if(super.mouseX >= centerX+72 && super.mouseX <= centerX+87) {
					handCursor = true;
				}
			} 
			else if(super.mouseY >= centerY - 130 && super.mouseY <= centerY - 116) {
				if(super.mouseX >= centerX+72 && super.mouseX <= centerX+87) {
					handCursor = true;
				}
			} 
			else if(super.mouseY >= centerY - 110 && super.mouseY <= centerY - 96) {
				if(super.mouseX >= centerX+72 && super.mouseX <= centerX+87) {
					handCursor = true;
				}
			} 
			else if(super.mouseY >= centerY - 90 && super.mouseY <= centerY - 76) {
				if(super.mouseX >= centerX+72 && super.mouseX <= centerX+87) {
					handCursor = true;
				}
			}
			else if(super.mouseY >= centerY - 70 && super.mouseY <= centerY - 56) {
				if(super.mouseX >= centerX+72 && super.mouseX <= centerX+87) {
					handCursor = true;
				}
			}	
			else if(super.mouseY >= centerY - 50 && super.mouseY <= centerY - 36) {
				if(super.mouseX >= centerX+72 && super.mouseX <= centerX+87) {
					handCursor = true;
				}
			}	
			else if(super.mouseY >= centerY - 30 && super.mouseY <= centerY - 16) {
				if(super.mouseX >= centerX+72 && super.mouseX <= centerX+87) {
					handCursor = true;
				}
			}	else if(super.mouseY >= centerY - 10 && super.mouseY <= centerY +4) {
				if(super.mouseX >= centerX+72 && super.mouseX <= centerX+87) {
					handCursor = true;
				}
			}
			else if(super.mouseY >= centerY +10 && super.mouseY <= centerY +24) {
				if(super.mouseX >= centerX+72 && super.mouseX <= centerX+87) {
					handCursor = true;
				}
			}
			else if(super.mouseY >= centerY +30 && super.mouseY <= centerY +44) {
				if(super.mouseX >= centerX+72 && super.mouseX <= centerX+87) {
					handCursor = true;
				}
			} else if(super.mouseY >= centerY + 88 && super.mouseY <= centerY + 123) {
				if(super.mouseX >= centerX-82 && super.mouseX <= centerX-32) {
					handCursor = true;
				} else if(mouseInRegion(centerX-24, centerY + 88, centerX+27, centerY + 123)) {
					handCursor = true;
				} else if(super.mouseX >= centerX+40 && super.mouseX <= centerX+84) {
					handCursor = true;
				}
			}
		}
		if(handCursor) {
			setCursor(-2);
		} else if(cursor != 0 && cursor != -1) {
			setCursor(getOption("cursors") ? 0 : -1);
		}
	}

	Sprite fixed, resize, fullscreen;

	/**
	 * Creates a new String containing stars only.
	 * @return
	 */
	public static String getStars(String s) {
		StringBuffer stars = new StringBuffer();
		for(int i = 0; i < s.length(); i++)
			stars.append("*");
		return stars.toString();
	}


	private void processLoginScreenInput() {
		try {
			if(isLoading)
				return;
			if (super.clickMode3 == 1 && settingsToggled) {
				if(super.saveClickY >= centerY - 150 && super.saveClickY <= centerY - 136) {
					if(super.saveClickX >= centerX+72 && super.saveClickX <= centerX+87) {
						rememberMe = !rememberMe;
						saveSettings();
					}
				} 
				else if(super.saveClickY >= centerY - 130 && super.saveClickY <= centerY - 116) {
					if(super.saveClickX >= centerX+72 && super.saveClickX <= centerX+87) {
						options.put("constitution", !getOption("constitution"));
						saveSettings();
					}
				} 
				else if(super.saveClickY >= centerY - 110 && super.saveClickY <= centerY - 96) {
					if(super.saveClickX >= centerX+72 && super.saveClickX <= centerX+87) {
						options.put("hd_tex", !getOption("hd_tex"));
						OverLayFlo317.unpackConfig(streamLoaderForName(2, "config",
								"config", expectedCRCs[2], 30));
						saveSettings();
					}
				} 
				else if(super.saveClickY >= centerY - 90 && super.saveClickY <= centerY - 76) {
					if(super.saveClickX >= centerX+72 && super.saveClickX <= centerX+87) {
						options.put("old_frame", !getOption("old_frame"));
						saveSettings();
					}
				}
				else if(super.saveClickY >= centerY - 70 && super.saveClickY <= centerY - 56) {
					if(super.saveClickX >= centerX+72 && super.saveClickX <= centerX+87) {
						options.put("old_hits", !getOption("old_hits"));
						saveSettings();
					}
				}	
				else if(super.saveClickY >= centerY - 50 && super.saveClickY <= centerY - 36) {
					if(super.saveClickX >= centerX+72 && super.saveClickX <= centerX+87) {
						options.put("tooltip_hover", !getOption("tooltip_hover"));
						saveSettings();
					}
				}	
				else if(super.saveClickY >= centerY - 30 && super.saveClickY <= centerY - 16) {
					if(super.saveClickX >= centerX+72 && super.saveClickX <= centerX+87) {
						options.put("cursors", !getOption("cursors"));
						setCursor(getOption("cursors") ? 0 : -1);
						saveSettings();
					}
				}	else if(super.saveClickY >= centerY - 10 && super.saveClickY <= centerY +4) {
					if(super.saveClickX >= centerX+72 && super.saveClickX <= centerX+87) {
						options.put("smilies", !getOption("smilies"));
						saveSettings();
					}
				}
				else if(super.saveClickY >= centerY +10 && super.saveClickY <= centerY +24) {
					if(super.saveClickX >= centerX+72 && super.saveClickX <= centerX+87) {
						options.put("censor_active", !getOption("censor_active"));
						saveSettings();
					}
				}
				else if(super.saveClickY >= centerY +30 && super.saveClickY <= centerY +44) {
					if(super.saveClickX >= centerX+72 && super.saveClickX <= centerX+87) {
						options.put("fog_active", !getOption("fog_active"));
						saveSettings();
					}
				} else if(super.saveClickY >= centerY + 88 && super.saveClickY <= centerY + 123) {
					if(super.saveClickX >= centerX-82 && super.saveClickX <= centerX-32) {
						if(isWebclient()) {
							loginMessage1 = "To change client size, please ";
							loginMessage2 = "download the desktop client.";
							settingsToggled = false;
							return;
						}
						if(clientSize != 0) {
							toggleSize(0);
							alpha[5] = 0;
						}
					} else if(mouseInRegion(centerX-24, centerY + 88, centerX+27, centerY + 123)) {
						if(isWebclient()) {
							loginMessage1 = "To change client size, please ";
							loginMessage2 = "download the desktop client.";
							settingsToggled = false;
							return;
						}
						if(clientSize != 1)
							toggleSize(1);
					} else if(super.saveClickX >= centerX+40 && super.saveClickX <= centerX+84) {
						if(isWebclient()) {
							loginMessage1 = "To change client size, please ";
							loginMessage2 = "download the desktop client.";
							settingsToggled = false;
							return;
						}
						if(clientSize != 2)
							toggleSize(2);
					}
				}
			}
			if (super.clickMode3 == 1 && super.saveClickX >= clientWidth - 52 && super.saveClickX <= clientWidth - 4 && super.saveClickY >= clientHeight - (clientSize == 0 ? 108 : 58) && super.saveClickY <= clientHeight - 4) {
				settingsToggled = !settingsToggled;
				alpha[4] = 0;
			}
			if (!settingsToggled) {
				if (super.clickMode3 == 1 && onInputBox(1)) {
					loginScreenCursorPos = 0;
				}
				if (super.clickMode3 == 1 && onInputBox(2)) {
					loginScreenCursorPos = 1;
				}
				if (super.clickMode3 == 1 &&  mouseInRegion(centerX-70, centerY+130, centerX+65, centerY+142)) {
					loginMessage1 = loginMessage2 = "";
				}
				if (super.clickMode3 == 1 && mouseInRegion(23, centerY-135, 68, centerY-80)) {
					launchURL("http://Voxyde.com");
				} else if(super.clickMode3 == 1 && mouseInRegion(centerX+14, centerY - 5, centerX+125, centerY + 7)) {
					launchURL("http://www.Voxyde.com/community");
					alpha[8] = 150;
				}
				if (super.clickMode3 == 1 && mouseInRegion(17, centerY-70, 17+loginImages[0].myWidth, centerY-70+loginImages[0].myHeight)) {
					launchURL("https://www.youtube.com/");
				}
				if (super.clickMode3 == 1 && mouseInRegion(17, centerY-10, 17+loginImages[0].myWidth, centerY-10+loginImages[0].myHeight)) {
					launchURL("http://Voxyde.com");
				}
				if (super.clickMode3 == 1 && super.saveClickX >= centerX-53 && super.saveClickY >= centerY+28 && super.saveClickX <= centerX+60 && super.saveClickY <= centerY+57) {
					//Sprite bo4 = new Sprite("2");
					//bo4.drawAdvancedSprite(centerX-55, centerY+25);
					loginFailures = 0;
					login(myUsername, myPassword, false);
					if(loggedIn)
						return;
				}
			}
			/*if(super.clickMode3 == 1 && onCreateFreeAccountButton()) {
				///launchURL("http://www.Voxyde.com/forums/index.php?app=core&module=global&section=register");
				loginMessage1 = "Enter a username and password";
				loginMessage2 = "         and then attempt to login.";
			}*/
			if(settingsToggled)
				return;
			do {
				int key = readChar(-796);
				if(key == -1) {
					break;
				}
				boolean validKey = false;
				for(int index = 0; index < validUserPassChars.length(); index++) {
					if(key != validUserPassChars.charAt(index)) {
						continue;
					}
					validKey = true;
					break;
				}
				if(loginScreenCursorPos == 0) {
					if(key == 8 && myUsername.length() > 0) {
						myUsername = myUsername.substring(0, myUsername.length() - 1);
					}
					if(key == 9 || key == 10 || key == 13) {
						loginScreenCursorPos = 1;
					}
					if(validKey) {
						myUsername = myUsername + (char)key;
					}
					if(myUsername.length() > 12) {
						myUsername = myUsername.substring(0, 12);
					}
				} else if(loginScreenCursorPos == 1) {
					if(key == 8 && myPassword.length() > 0) {
						myPassword = myPassword.substring(0, myPassword.length() - 1);
					}
					if (key == 9) {
						loginScreenCursorPos = 0;
					}
					if(key == 10 || key == 13) {
						loginFailures = 0;
						login(myUsername, myPassword, false);
						if(loggedIn)
							return;
					}
					if(validKey) {
						myPassword += (char)key;
					}
					if(myPassword.length() > 20) {
						myPassword = myPassword.substring(0, 20);
					}
				}
			} while(true);
			return;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static String capitalizeFirstChar(String s) {
		try {
			if (s != "")
				return (s.substring(0, 1).toUpperCase() + s.substring(1)
						.toLowerCase()).trim();
		} catch (Exception e) {
		}
		return s;
	}

	private int anInt1275;

	private int anInt1040;
	private int anInt1041;

	public void raiseWelcomeScreen() {
		welcomeScreenRaised = true;
	}

	private void parseEntityPacket(Stream stream, int j) {
		if (j == 84) {
			int k = stream.readUnsignedByte();
			int j3 = bigRegionX + (k >> 4 & 7);
			int i6 = bigRegionY + (k & 7);
			int l8 = stream.readUnsignedWord();
			int k11 = stream.readUnsignedWord();
			int l13 = stream.readUnsignedWord();
			if (j3 >= 0 && i6 >= 0 && j3 < 104 && i6 < 104) {
				Deque class19_1 = groundArray[plane][j3][i6];
				if (class19_1 != null) {
					for (Item class30_sub2_sub4_sub2_3 = (Item) class19_1
							.getFront(); class30_sub2_sub4_sub2_3 != null; class30_sub2_sub4_sub2_3 = (Item) class19_1
							.getNext()) {
						if (class30_sub2_sub4_sub2_3.ID != (l8 & 0x7fff)
								|| class30_sub2_sub4_sub2_3.amount != k11)
							continue;
						class30_sub2_sub4_sub2_3.amount = l13;
						break;
					}

					spawnGroundItem(j3, i6);
				}
			}
			return;
		}
		if (j == 105) {
			int l = stream.readUnsignedByte();
			int k3 = bigRegionX + (l >> 4 & 7);
			int j6 = bigRegionY + (l & 7);
			int i9 = stream.readUnsignedWord();
			int l11 = stream.readUnsignedByte();
			int i14 = l11 >> 4 & 0xf;
		int i16 = l11 & 7;
		if (myPlayer.pathX[0] >= k3 - i14 && myPlayer.pathX[0] <= k3 + i14
				&& myPlayer.pathY[0] >= j6 - i14
				&& myPlayer.pathY[0] <= j6 + i14 && soundEnabled && !lowMem
				&& currentSound < 50) {
			sound[currentSound] = i9;
			soundType[currentSound] = i16;
			soundDelay[currentSound] = Sounds.anIntArray326[i9];
			currentSound++;
		}
		}
		if (j == 215) {
			int i1 = stream.readByteA();
			int l3 = stream.readByteS();
			int k6 = bigRegionX + (l3 >> 4 & 7);
			int j9 = bigRegionY + (l3 & 7);
			int i12 = stream.readByteA();
			int j14 = stream.readUnsignedWord();
			if (k6 >= 0 && j9 >= 0 && k6 < 104 && j9 < 104 && i12 != playerId) {
				Item class30_sub2_sub4_sub2_2 = new Item();
				class30_sub2_sub4_sub2_2.ID = i1;
				class30_sub2_sub4_sub2_2.amount = j14;
				if (groundArray[plane][k6][j9] == null)
					groundArray[plane][k6][j9] = new Deque();
				groundArray[plane][k6][j9].insertBack(class30_sub2_sub4_sub2_2);
				spawnGroundItem(k6, j9);
			}
			return;
		}
		if (j == 156) {
			int j1 = stream.method426();
			int i4 = bigRegionX + (j1 >> 4 & 7);
			int l6 = bigRegionY + (j1 & 7);
			int k9 = stream.readUnsignedWord();
			if (i4 >= 0 && l6 >= 0 && i4 < 104 && l6 < 104) {
				Deque deque = groundArray[plane][i4][l6];
				if (deque != null) {
					for (Item item = (Item) deque.getFront(); item != null; item = (Item) deque
							.getNext()) {
						if (item.ID != (k9 & 0x7fff))
							continue;
						item.unlink();
						break;
					}

					if (deque.getFront() == null)
						groundArray[plane][i4][l6] = null;
					spawnGroundItem(i4, l6);
				}
			}
			return;
		}
		if (j == 160) {
			int k1 = stream.readByteS();
			int j4 = bigRegionX + (k1 >> 4 & 7);
			int i7 = bigRegionY + (k1 & 7);
			int l9 = stream.readByteS();
			int j12 = l9 >> 2;
				int k14 = l9 & 3;
				int objectType = anIntArray1177[j12];
				int animId = stream.readByteA();
				if(animId == 12703)
					System.out.println("k1: "+k1+", j4: "+j4+", i7: "+i7+", l9: "+l9+", objectType: "+objectType+", animId: "+animId);
				if (j4 >= 0 && i7 >= 0 && j4 < 103) {
					int j18 = intGroundArray[plane][j4][i7];
					int i19 = intGroundArray[plane][j4 + 1][i7];
					int l19 = intGroundArray[plane][j4 + 1][i7 + 1];
					int k20 = intGroundArray[plane][j4][i7 + 1];
					if (objectType == 0) {
						WallObject class10 = worldController.getWallObject(plane,
								j4, i7);
						if (class10 != null) {
							int k21 = class10.wallObjUID;
							if (j12 == 2) {
								class10.node1 = new ObjectOnTile(k21, 4 + k14, 2,
										i19, l19, j18, k20, animId, false);
								class10.node2 = new ObjectOnTile(k21, k14 + 1 & 3,
										2, i19, l19, j18, k20, animId, false);
							} else {
								class10.node1 = new ObjectOnTile(k21, k14, j12,
										i19, l19, j18, k20, animId, false);
							}
						}
					}
					if (objectType == 1) {
						WallDecoration class26 = worldController.getWallDecoration(
								j4, i7, plane);
						if (class26 != null)
							class26.node = new ObjectOnTile(class26.wallDecorUID,
									0, 4, i19, l19, j18, k20, animId, false);
					}
					if (objectType == 2) {
						InteractableObject class28 = worldController.getInteractableObject(j4, i7, plane);
						if (j12 == 11)
							j12 = 10;
						if (class28 != null)
							class28.node = new ObjectOnTile(class28.interactiveObjUID, k14, j12, i19, l19, j18, k20, animId, false);
					}
					if (objectType == 3) {
						GroundDecoration class49 = worldController
								.getGroundDecoration(i7, j4, plane);
						if (class49 != null)
							class49.node = new ObjectOnTile(class49.groundDecorUID,
									k14, 22, i19, l19, j18, k20, animId, false);
					}
				}
				return;
		}
		if (j == 147) {
			int tileBits = stream.readByteS();
			int x = bigRegionX + (tileBits >> 4 & 7);
			int y = bigRegionY + (tileBits & 7);
			int plrId = stream.readUnsignedWord();
			byte xMax = stream.method430();
			int startTime = stream.ig2();
			byte yMax = stream.method429();
			int endTime = stream.readUnsignedWord();
			int objectBits = stream.readByteS();
			int objectType = objectBits >> 2;
			int objectFace = objectBits & 3;
			int typeDecoded = anIntArray1177[objectType];
			byte xMin = stream.readSignedByte();
			int objectId = stream.readUnsignedWord();
			byte yMin = stream.method429();
			Player player;
			if (plrId == playerId)
				player = myPlayer;
			else
				player = playerArray[plrId];
			if (player != null) {
				ObjectDef objectDef = ObjectDef.forID(objectId);
				int mine = intGroundArray[plane][x][y];
				int right = intGroundArray[plane][x + 1][y];
				int upperRight = intGroundArray[plane][x + 1][y + 1];
				int up = intGroundArray[plane][x][y + 1];
				Model model = objectDef.renderObject(objectType, objectFace,
						mine, right, upperRight, up, -1);
				if (model != null) {
					createObjectSpawnRequest(endTime + 1, -1, 0, typeDecoded,
							y, 0, plane, x, startTime + 1);
					player.startTimeTransform = startTime + loopCycle;
					player.transformedTimer = endTime + loopCycle;
					player.tranformIntoModel = model;
					int addedSizeY = objectDef.sizeX;
					int addedSizeX = objectDef.sizeY;
					if (objectFace == 1 || objectFace == 3) {
						addedSizeY = objectDef.sizeY;
						addedSizeX = objectDef.sizeX;
					}
					player.resizeX = x * 128 + addedSizeY * 64;
					player.resizeY = y * 128 + addedSizeX * 64;
					player.resizeZ = getFloorDrawHeight(plane, player.resizeY,
							player.resizeX);
					if (xMin > xMax) {
						byte oldMin = xMin;
						xMin = xMax;
						xMax = oldMin;
					}
					if (yMin > yMax) {
						byte oldMin = yMin;
						yMin = yMax;
						yMax = oldMin;
					}
					player.extendedXMin = x + xMin;
					player.extendedXMax = x + xMax;
					player.extendedYMin = y + yMin;
					player.extendedYMax = y + yMax;
				}
			}
		}
		if (j == 151) {
			int tileData = stream.method426();
			int l4 = bigRegionX + (tileData >> 4 & 7);
			int k7 = bigRegionY + (tileData & 7);
			int objID = stream.ig2();
			int typeAndFaceBits = stream.readByteS();
			int obType = typeAndFaceBits >> 2;
			int obFace = typeAndFaceBits & 3;
			int l17 = anIntArray1177[obType];
			if (l4 >= 0 && k7 >= 0 && l4 < 104 && k7 < 104)
				createObjectSpawnRequest(-1, objID, obFace, l17, k7, obType,
						plane, l4, 0);
			return;
		}
		if (j == 152) {
			int tileData = stream.method426();
			int tileX = bigRegionX + (tileData >> 4 & 7);
			int tileY = bigRegionY + (tileData & 7);
			int objID = stream.ig2();
			int typeAndFaceBits = stream.readByteS();
			int obType = typeAndFaceBits >> 2;
			int obFace = typeAndFaceBits & 3;
			int toPlane = stream.readByte();
			int l17 = anIntArray1177[obType];
			if (tileX >= 0 && tileY >= 0 && tileX < 104 && tileY < 104)
				createObjectSpawnRequest(-1, objID, obFace, l17, tileY, obType,
						toPlane, tileX, 0);
			return;
		}

		if (j == 153) {
			int chunkX = stream.readUnsignedByte();
			int chunkY = stream.readUnsignedByte();
			int toPlane = stream.readByte();
			if (toPlane == 10) {
				clearObjectSpawnRequests();
				return;
			}
			GameObjectSpawnRequest request = (GameObjectSpawnRequest) objectSpawnDeque
					.getFront();
			for (; request != null; request = (GameObjectSpawnRequest) objectSpawnDeque
					.getNext()) {
				if (request.tileX >= chunkX * 8
						&& request.tileX <= (chunkX * 8) + 7
						&& request.tileY >= chunkY * 8
						&& request.tileY <= (chunkY * 8) + 7
						&& request.plane == toPlane)
					request.unlink();
			}

			return;
		}
		if (j == 4) {
			int tileBits = stream.readUnsignedByte();
			int x = bigRegionX + (tileBits >> 4 & 7);
			int y = bigRegionY + (tileBits & 7);
			int id = stream.readUnsignedWord();
			int height = stream.readUnsignedByte();
			int time = stream.readUnsignedWord();
			if (x >= 0 && y >= 0 && x < 104 && y < 104) {
				x = x * 128 + 64;
				y = y * 128 + 64;
				StillGraphic stillGraphi = new StillGraphic(plane, loopCycle,
						time, id, getFloorDrawHeight(plane, y, x) - height, y,
						x);
				stillGraphicDeque.insertBack(stillGraphi);
			}
			return;
		}
		if (j == 44) {
			int k2 = stream.readWordBigEndian();
			int j5 = stream.readUnsignedWord();
			int i8 = stream.readUnsignedByte();
			int l10 = bigRegionX + (i8 >> 4 & 7);
			int i13 = bigRegionY + (i8 & 7);
			if (l10 >= 0 && i13 >= 0 && l10 < 104 && i13 < 104) {
				Item item = new Item();
				item.ID = k2;
				item.amount = j5;
				if (groundArray[plane][l10][i13] == null)
					groundArray[plane][l10][i13] = new Deque();
				groundArray[plane][l10][i13].insertBack(item);
				spawnGroundItem(l10, i13);
			}
			return;
		}
		if (j == 101) {
			int l2 = stream.nglb();
			int k5 = l2 >> 2;
			int j8 = l2 & 3;
			if (k5 >= anIntArray1177.length)
				return;
			int i11 = anIntArray1177[k5];
			int j13 = stream.readUnsignedByte();
			int k15 = bigRegionX + (j13 >> 4 & 7);
			int l16 = bigRegionY + (j13 & 7);
			if (k15 >= 0 && l16 >= 0 && k15 < 104 && l16 < 104)
				createObjectSpawnRequest(-1, -1, j8, i11, l16, k5, plane, k15,
						0);
			return;
		}
		if (j == 117) {
			int tileBits = stream.readUnsignedByte();
			int xTile = bigRegionX + (tileBits >> 4 & 7);
			int yTile = bigRegionY + (tileBits & 7);
			int xToGo = xTile + stream.readSignedByte();
			int yToGo = yTile + stream.readSignedByte();
			int lockOn = stream.readSignedWord();
			int gfxId = stream.readUnsignedWord();
			int startHeight = stream.readUnsignedByte() * 4;
			int endHeight = stream.readUnsignedByte() * 4;
			int time = stream.readUnsignedWord();
			int speed = stream.readUnsignedWord();
			int slopeHeight = stream.readUnsignedByte();
			int radius = stream.readUnsignedByte();
			if (xTile >= 0 && yTile >= 0 && xTile < 104 && yTile < 104
					&& xToGo >= 0 && yToGo >= 0 && xToGo < 104 && yToGo < 104
					&& gfxId != 65535) {
				xTile = xTile * 128 + 64;
				yTile = yTile * 128 + 64;
				xToGo = xToGo * 128 + 64;
				yToGo = yToGo * 128 + 64;
				Projectile projectile = new Projectile(slopeHeight, endHeight,
						time + loopCycle, speed + loopCycle, radius, plane,
						getFloorDrawHeight(plane, yTile, xTile) - startHeight,
						yTile, xTile, lockOn, gfxId);
				projectile.calculateTracking(time + loopCycle, yToGo,
						getFloorDrawHeight(plane, yToGo, xToGo) - endHeight,
						xToGo);
				projectileDeque.insertBack(projectile);
			}
		}
	}

	private static void setLowMem() {
		WorldController.lowMem = true;
		lowMem = true;
		ObjectManager.lowMem = true;
		ObjectDef.lowMem = true;

		Rasterizer.lowMem = false;
		//		Rasterizer.lowMem = true;
	}

	private void updateNPCAmount(Stream stream) {
		stream.initBitAccess();
		int npcAmt = stream.readBits(8);
		if (npcAmt < npcCount) {
			for (int l = npcAmt; l < npcCount; l++)
				anIntArray840[anInt839++] = npcIndices[l];

		}
		if (npcAmt > npcCount) {
			signlink.reporterror(myUsername + " Too many npcs");
			throw new RuntimeException("eek");
		}
		npcCount = 0;
		for (int i1 = 0; i1 < npcAmt; i1++) {
			int j1 = npcIndices[i1];
			NPC npc = npcArray[j1];
			int k1 = stream.readBits(1);
			if (k1 == 0) {
				npcIndices[npcCount++] = j1;
				npc.loopCycle = loopCycle;
			} else {
				int l1 = stream.readBits(2);
				if (l1 == 0) {
					npcIndices[npcCount++] = j1;
					npc.loopCycle = loopCycle;
					playersToUpdate[playersToUpdateCount++] = j1;
				} else if (l1 == 1) {
					npcIndices[npcCount++] = j1;
					npc.loopCycle = loopCycle;
					int i2 = stream.readBits(3);
					npc.moveInDir(false, i2);
					int k2 = stream.readBits(1);
					if (k2 == 1)
						playersToUpdate[playersToUpdateCount++] = j1;
				} else if (l1 == 2) {
					npcIndices[npcCount++] = j1;
					npc.loopCycle = loopCycle;
					int j2 = stream.readBits(3);
					npc.moveInDir(true, j2);
					int l2 = stream.readBits(3);
					npc.moveInDir(true, l2);
					if (npc.desc.type == 13447) {
						npc.moveInDir(true, l2);
						npc.moveInDir(true, l2);
						npc.moveInDir(true, l2);
					}
					int i3 = stream.readBits(1);
					if (i3 == 1)
						playersToUpdate[playersToUpdateCount++] = j1;
				} else if (l1 == 3)
					anIntArray840[anInt839++] = j1;
			}
		}

	}

	public static int getRandom(int number, boolean greaterThan0) {
		Random random = new Random();
		int randomNr = random.nextInt(number) + (greaterThan0 ? 1 : 0);
		return randomNr;
	}

	private void markMinimap(Sprite sprite, int x, int y) {
		if (sprite == null)
			return;
		try {
			int offX = clientSize == 0 ? 0 : clientWidth - 249;
			int k = viewRotation + minimapRotation & 0x7ff;
			int l = x * x + y * y;
			if (l > 6400) {
				return;
			}
			int i1 = Model.SINE[k];
			int j1 = Model.COSINE[k];
			i1 = (i1 * 256) / (minimapZoom + 256);
			j1 = (j1 * 256) / (minimapZoom + 256);
			int k1 = y * i1 + x * j1 >> 16;
		int l1 = y * j1 - x * i1 >> 16;
					if (clientSize == 0)
						sprite.drawSprite(
								((105 + k1) - sprite.maxWidth / 2) + 4 + offX, 88 - l1
								- sprite.maxHeight / 2 - 4);
					else
						sprite.drawSprite(((77 + k1) - sprite.maxWidth / 2) + 4
								+ (clientWidth - 167), 85 - l1 - sprite.maxHeight / 2
								- 4);
		} catch (Exception e) {

		}
	}

	private void addRequestedObject(int yTile, int z, int objectFace,
			int requestType, int xTile, int objectType, int objectId) {
		if (xTile >= 1 && yTile >= 1 && xTile <= 102 && yTile <= 102) {
			if (lowMem && z != plane)
				return;
			int uid = 0;
			int obId = 0;
			if (objectType == 0) {
				uid = worldController.getWallObjectUID(z, xTile, yTile);
				obId = worldController.fetchWallObjectNewUID(z, xTile, yTile);
			}
			if (objectType == 1) {
				uid = worldController.getWallDecorationUID(z, xTile, yTile);
				obId = worldController.fetchWallDecorationNewUID(z, xTile,
						yTile);
			}
			if (objectType == 2) {
				uid = worldController.getInteractableObjectUID(z, xTile, yTile);
				obId = worldController.fetchObjectMeshNewUID(z, xTile, yTile);
			}
			if (objectType == 3) {
				obId = worldController.fetchGroundDecorationNewUID(z, xTile,
						yTile);
				uid = worldController.getGroundDecorationUID(z, xTile, yTile);
			}
			if (uid != 0) {
				int uidTag = worldController.getIDTagForXYZ(z, xTile, yTile,
						uid);
				int objectId_1 = obId;
				int k2 = uidTag & 0x1f;
				int l2 = uidTag >> 6;
		if (objectType == 0) {
			worldController.removeWallObject(xTile, z, yTile);
			ObjectDef objectDef = ObjectDef.forID(objectId_1);
			if (objectDef.isUnwalkable)
				clippingPlanes[z].addClip(l2, k2,
						objectDef.aBoolean757, xTile, yTile);
		}
		if (objectType == 1)
			worldController.removeWallDecoration(yTile, z, xTile);
		if (objectType == 2) {
			worldController.removeInteractableObject(z, xTile, yTile);
			ObjectDef objectDef_1 = ObjectDef.forID(objectId_1);
			if (xTile + objectDef_1.sizeX > 103
					|| yTile + objectDef_1.sizeX > 103
					|| xTile + objectDef_1.sizeY > 103
					|| yTile + objectDef_1.sizeY > 103)
				return;
			if (objectDef_1.isUnwalkable)
				clippingPlanes[z].addInteractableObjectClip(l2,
						objectDef_1.sizeX, xTile, yTile,
						objectDef_1.sizeY, objectDef_1.aBoolean757);
		}
		if (objectType == 3) {
			worldController.removeGroundDecoration(z, yTile, xTile);
			ObjectDef objectDef_2 = ObjectDef.forID(objectId_1);
			if (objectDef_2.isUnwalkable && objectDef_2.hasActions)
				clippingPlanes[z].addGroundDecClip(yTile, xTile);
		}
			}
			if (objectId >= 0) {
				int j3 = z;
				if (j3 < 3 && (byteGroundArray[1][xTile][yTile] & 2) == 2)
					j3++;
				ObjectManager.method188(worldController, objectFace, yTile,
						requestType, j3, clippingPlanes[z], intGroundArray,
						xTile, objectId, z);
			}
		}
	}

	private void updatePlayers(int i, Stream stream) {
		anInt839 = 0;
		playersToUpdateCount = 0;
		updatePlayerMovement(stream);
		updatePlayer(stream);
		updatePlayerMovement(stream, i);
		processPlayerUpdating(stream);
		for (int k = 0; k < anInt839; k++) {
			int l = anIntArray840[k];
			if (playerArray[l].loopCycle != loopCycle)
				playerArray[l] = null;
		}

		if (stream.currentOffset != i) {
			signlink.reporterror("Error packet size mismatch in getplayer pos:"
					+ stream.currentOffset + " psize:" + i);
			throw new RuntimeException("eek");
		}
		for (int i1 = 0; i1 < playerCount; i1++)
			if (playerArray[playerIndices[i1]] == null) {
				signlink.reporterror(myUsername
						+ " null entry in pl list - pos:" + i1 + " size:"
						+ playerCount);
				throw new RuntimeException("eek");
			}

	}

	private void setCameraPos(int j, int k, int l, int i1, int j1, int k1) {
		int l1 = 2048 - k & 0x7ff;
		int i2 = 2048 - j1 & 0x7ff;
		int j2 = 0;
		int k2 = 0;
		int l2 = j;
		if (l1 != 0) {
			int i3 = Model.SINE[l1];
			int k3 = Model.COSINE[l1];
			int i4 = k2 * k3 - l2 * i3 >> 16;
		l2 = k2 * i3 + l2 * k3 >> 16;
				k2 = i4;
		}
		if (i2 != 0) {
			/*
			 * xxx if(cameratoggle){ if(zoom == 0) zoom = k2; if(lftrit == 0)
			 * lftrit = j2; if(fwdbwd == 0) fwdbwd = l2; k2 = zoom; j2 = lftrit;
			 * l2 = fwdbwd; }
			 */
			int j3 = Model.SINE[i2];
			int l3 = Model.COSINE[i2];
			int j4 = l2 * j3 + j2 * l3 >> 16;
			l2 = l2 * l3 - j2 * j3 >> 16;
			j2 = j4;
		}
		xCameraPos = l - j2;
		zCameraPos = i1 - k2;
		yCameraPos = k1 - l2;
		yCameraCurve = k;
		xCameraCurve = j1;
	}

	public void updateStrings(String str, int i) {
		switch (i) {
		case 1675:
			sendFrame126(str, 17508);
			break;// Stab
		case 1676:
			sendFrame126(str, 17509);
			break;// Slash
		case 1677:
			sendFrame126(str, 17510);
			break;// Cursh
		case 1678:
			sendFrame126(str, 17511);
			break;// Magic
		case 1679:
			sendFrame126(str, 17512);
			break;// Range
		case 1680:
			sendFrame126(str, 17513);
			break;// Stab
		case 1681:
			sendFrame126(str, 17514);
			break;// Slash
		case 1682:
			sendFrame126(str, 17515);
			break;// Crush
		case 1683:
			sendFrame126(str, 17516);
			break;// Magic
		case 1684:
			sendFrame126(str, 17517);
			break;// Range
		case 1686:
			sendFrame126(str, 17518);
			break;// Strength
		case 1687:
			sendFrame126(str, 17519);
			break;// Prayer
		}
	}

	public void sendFrame126(String str, int i) {
		if (i < RSInterface.interfaceCache.length && RSInterface.interfaceCache[i] == null)
			return;
		try {
			RSInterface.interfaceCache[i].message = str;
			if (RSInterface.interfaceCache[i].parentID == tabInterfaceIDs[tabID])
				needDrawTabArea = true;
		} catch(Exception e) {

		}
	}

	public void sendPacket185(int buttonID) {
		stream.createFrame(185);
		stream.writeWord(buttonID);
		RSInterface rsi = RSInterface.interfaceCache[buttonID];
		if (rsi == null)
			return;
		if (rsi.valueIndexArray != null && rsi.valueIndexArray[0][0] == 5) {
			int configID = rsi.valueIndexArray[0][1];
			variousSettings[configID] = 1 - variousSettings[configID];
			handleActions(configID);
			needDrawTabArea = true;
		}
	}

	public void sendPacket185(int button, int toggle, int type) {
		switch (type) {
		case 135:
			RSInterface class9 = RSInterface.interfaceCache[button];
			boolean flag8 = true;
			if (class9.contentType > 0)
				flag8 = promptUserForInput(class9);
			if (flag8) {
				stream.createFrame(185);
				stream.writeWord(button);
			}
			break;
		case 646:
			stream.createFrame(185);
			stream.writeWord(button);
			RSInterface class9_2 = RSInterface.interfaceCache[button];
			if (class9_2.valueIndexArray != null
					&& class9_2.valueIndexArray[0][0] == 5) {
				if (variousSettings[toggle] != class9_2.requiredValues[0]) {
					variousSettings[toggle] = class9_2.requiredValues[0];
					handleActions(toggle);
					needDrawTabArea = true;
				}
			}
			break;
		case 169:
			if(tabInterfaceIDs[5] == 17200 || tabInterfaceIDs[5] == 17234) {
				return;
			}
			stream.createFrame(185);
			stream.writeWord(button);
			RSInterface clickedInterface = RSInterface.interfaceCache[button];
			if (clickedInterface.valueIndexArray != null
					&& clickedInterface.valueIndexArray[0][0] == 5) {
				variousSettings[toggle] = 1 - variousSettings[toggle];
				handleActions(toggle);
				needDrawTabArea = true;
			}
			switch (button) {
			case 19136:
				if (toggle == 0)
					sendFrame36(173, toggle);
				if (toggle == 1)
					sendPacket185(153, 173, 646);
				break;
			}
			break;
		}
	}

	public void sendFrame36(int id, int state) {
		varbitConfigs[id] = state;
		if (variousSettings[id] != state) {
			variousSettings[id] = state;
			handleActions(id);
			needDrawTabArea = true;
			if (dialogID != -1)
				inputTaken = true;
		}
	}

	public void sendFrame219() {
		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			needDrawTabArea = true;
			tabAreaAltered = true;
		}
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}
		openInterfaceID = -1;
		aBoolean1149 = false;
	}

	public void sendFrame248(int interfaceID, int sideInterfaceID) {
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}
		openInterfaceID = interfaceID;
		invOverlayInterfaceID = sideInterfaceID;
		needDrawTabArea = true;
		tabAreaAltered = true;
		aBoolean1149 = false;
	}

	private boolean parsePacket() {
		if(socketStream == null)
			return false;
		try {
			int i = socketStream.available();
			if(i == 0)
				return false;
			if(opCode == -1) {
				socketStream.flushInputStream(inStream.buffer, 1);
				opCode = inStream.buffer[0] & 0xff;
				if(encryption != null)
					opCode = opCode - encryption.getNextKey() & 0xff;
				pktSize = SizeConstants.packetSizes[opCode];
				i--;
			}
			if(pktSize == -1)
				if(i > 0) {
					socketStream.flushInputStream(inStream.buffer, 1);
					pktSize = inStream.buffer[0] & 0xff;
					i--;
				} else {
					return false;
				}
			if(pktSize == -2)
				if(i > 1) {
					socketStream.flushInputStream(inStream.buffer, 2);
					inStream.currentOffset = 0;
					pktSize = inStream.readUnsignedWord();
					i -= 2;
				} else {
					return false;
				}
			if(i < pktSize)
				return false;
			inStream.currentOffset = 0;
			socketStream.flushInputStream(inStream.buffer, pktSize);
			anInt1009 = 0;
			opcode_second = opcode_last;
			opcode_last = anInt841;
			anInt841 = opCode;
			switch (opCode) {
			case 81:
				updatePlayers(pktSize, inStream);
				loadingMap = false;
				opCode = -1;
				return true;

			case 123:
				sendConsoleMessage(inStream.readString(), false);
				opCode = -1;
				return true;

			case 179:
				title = inStream.readString();
				information = inStream.readString();
				drawX = inStream.readUnsignedWord();
				drawY = inStream.readUnsignedWord();
				speed = inStream.readUnsignedByte();
				pause = inStream.readUnsignedByte();
				type2 = inStream.readSignedByte();
				opCode = -1;
				return true;

			case 172:
				try {
					boolean active = inStream.readByte() == 1;
					String special = "";
					if (active)
						special = inStream.readString();
				} catch (Exception e) {
					e.printStackTrace();
				}
				opCode = -1;
				return true;
			case 161:
				int frameId = inStream.readUnsignedWord();
				RSInterface rsi = RSInterface.interfaceCache[frameId];
				int color = inStream.readUnsignedByte();
				int height = inStream.readUnsignedWord();
				if (color == 0) {
					color = 0xFF2323;
				}
				if (color == 1) {
					color = 0x3366FF;
				}
				if (color == 2) {
					color = 0xF0F0F0;
				}
				if (color != 10)
					rsi.disabledColor = color;
				rsi.height = height;
				opCode = -1;
				return true;

			case 176:
				daysSinceRecovChange = inStream.nglb();
				unreadMessages = inStream.readByteA();
				membersInt = inStream.readUnsignedByte();
				anInt1193 = inStream.method440();
				daysSinceLastLogin = inStream.readUnsignedWord();
				if (anInt1193 != 0 && openInterfaceID == -1) {
					signlink.dnslookup(TextClass.method586(anInt1193));
					clearTopInterfaces();
					char c = '\u028A';
					if (daysSinceRecovChange != 201 || membersInt == 1)
						c = '\u028F';
					reportAbuseInput = "";
					canMute = false;
					for (int k9 = 0; k9 < RSInterface.interfaceCache.length; k9++) {
						if (RSInterface.interfaceCache[k9] == null
								|| RSInterface.interfaceCache[k9].contentType != c)
							continue;
						openInterfaceID = RSInterface.interfaceCache[k9].parentID;

					}
				}
				opCode = -1;
				return true;

			case 64:
				bigRegionX = inStream.nglb();
				bigRegionY = inStream.readByteS();
				for (int j = bigRegionX; j < bigRegionX + 8; j++) {
					for (int l9 = bigRegionY; l9 < bigRegionY + 8; l9++)
						if (groundArray[plane][j][l9] != null) {
							groundArray[plane][j][l9] = null;
							spawnGroundItem(j, l9);
						}
				}
				for (GameObjectSpawnRequest class30_sub1 = (GameObjectSpawnRequest) objectSpawnDeque
						.getFront(); class30_sub1 != null; class30_sub1 = (GameObjectSpawnRequest) objectSpawnDeque
						.getNext())
					if (class30_sub1.tileX >= bigRegionX
					&& class30_sub1.tileX < bigRegionX + 8
					&& class30_sub1.tileY >= bigRegionY
					&& class30_sub1.tileY < bigRegionY + 8
					&& class30_sub1.plane == plane)
						class30_sub1.removeTime = 0;
				opCode = -1;
				return true;

			case 185:
				int k = inStream.readWordBigEndian();
				RSInterface.interfaceCache[k].mediaType = 3;
				if (myPlayer.desc == null)
					RSInterface.interfaceCache[k].mediaID = (myPlayer.anIntArray1700[0] << 25)
					+ (myPlayer.anIntArray1700[4] << 20)
					+ (myPlayer.equipment[0] << 15)
					+ (myPlayer.equipment[8] << 10)
					+ (myPlayer.equipment[11] << 5)
					+ myPlayer.equipment[1];
				else
					RSInterface.interfaceCache[k].mediaID = (int) (0x12345678L + myPlayer.desc.type);
				opCode = -1;
				return true;

				/* Clan chat packet */
			case 217:
				try {
					name = inStream.readString();
					message = inStream.readString();
					clanname = inStream.readString();
					rights = inStream.readUnsignedWord();
					message = TextInput.processText(message);
					message = Censor.doCensor(message);
					// System.out.println(clanname);
					pushMessage(message, 16, name);
				} catch (Exception e) {
					e.printStackTrace();
				}
				opCode = -1;
				return true;

			case 107:
				inCutScene = false;
				for (int l = 0; l < 5; l++)
					aBooleanArray876[l] = false;
				xpCounter = 0;
				opCode = -1;
				return true;

			case 72:
				int i1 = inStream.ig2();
				RSInterface class9 = RSInterface.interfaceCache[i1];
				if (class9.inv == null) {
					opCode = -1;
					return true;
				}
				for (int k15 = 0; k15 < class9.inv.length; k15++) {
					class9.inv[k15] = -1;
					class9.inv[k15] = 0;
				}
				opCode = -1;
				return true;

			case 124:
				int skillID = inStream.readUnsignedByte();
				int gainedXP = inStream.readDWord();
				int totalEXP = inStream.readDWord();
				addXP(skillID, gainedXP);
				totalXP = totalEXP;
				opCode = -1;
				return true;

			case 214: //Reset the ignore list
				//Cheap hax level over 9000 lulululul
				ignoreListAsLongs = new long[100];
				ignoreCount = 0;
				int amount = Integer.parseInt(inStream.readString());
				for(int i2 = 0; i2 < amount; i2++) {
					ignoreListAsLongs[i2] = Long.parseLong(inStream.readString());
					ignoreCount++;
				}
				needDrawTabArea = true;
				opCode = -1;
				return true;

			case 166:
				inStream.readUnsignedByte();
				int type = inStream.readUnsignedByte();
				int slot = inStream.readUnsignedByte();
				if (type == 1) {
					slotColor[slot] = inStream.readUnsignedByte();
				} else if (type == 2) {
					slotColorPercent[slot] = inStream.readUnsignedByte();
				} else if (type == 3) {
					int lololol = inStream.readUnsignedByte();
					if (lololol == 1) {
						slotAborted[slot] = true;
					} else {
						slotAborted[slot] = false;
					}
				} else if (type == 4) {
					int thing = inStream.readUnsignedByte();
					if (thing == 1) {
						buttonclicked = false;
						interfaceButtonAction = 0;
					} else if (thing == 2) {
						slotSelected = slot;
					} else if (thing == 3) {
						slots[slot] = "";
						Slots[slot] = 0;
					}
				} else if (type == 5) {
					int thing1 = inStream.readUnsignedByte();
					if (thing1 == 1) {
						slotUsing = slot;
						slots[slot] = "Sell";
						Slots[slot] = 1;
					} else if (thing1 == 2) {
						slotUsing = slot;
						slots[slot] = "Buy";
						Slots[slot] = 1;
					} else if (thing1 == 3) {
						Slots[slot] = 2;
						slots[slot] = "Sell";
					} else if (thing1 == 4) {
						Slots[slot] = 2;
						slots[slot] = "Buy";
					} else if (thing1 == 5) {
						Slots[slot] = 3;
						slots[slot] = "Sell";
					} else if (thing1 == 6) {
						Slots[slot] = 3;
						slots[slot] = "Buy";
					}
				} else if (type == 6) {
					inStream.readUnsignedByte();
					buttonclicked = true;
					amountOrNameInput = "";
					totalItemResults = 0;
				} else if (type == 7) {
					int anInt1308 = inStream.readUnsignedByte();
					resetAnAnim(anInt1308);
				} else {
					inStream.readUnsignedByte();
				}
				inStream.readUnsignedByte();
				inStream.readUnsignedByte();
				opCode = -1;
				return true;

			case 134:
				needDrawTabArea = true;
				int id = inStream.readUnsignedByte();
				int exp = inStream.method439();
				int level = inStream.readUnsignedWord();
				int maxLevel = inStream.readUnsignedWord();
				if(id == 5 || id == 3) {
					level *= 10;
					maxLevel *= 10;
				}
				currentExp[id] = exp;
				currentStats[id] = level;
				currentMaxStats[id] = maxLevel;
				int gainedExperience = inStream.readUnsignedWord();
				addXP(id, gainedExperience);
				updateSkilltab();
				opCode = -1;
				return true;

			case 175:
				int soundId = inStream.readWordBigEndian();
				int volume = inStream.readSignedByte();
				type = volume;
				int delay = inStream.readShort();
				try {
					sound[currentSound] = soundId;
					soundType[currentSound] = type;
					soundDelay[currentSound] = delay + Sounds.anIntArray326[soundId];
					soundVolume[currentSound] = volume;
					currentSound++;
				} catch (Exception e) {
					e.printStackTrace();
				}
				opCode = -1;
				return true;

			case 71:
				int l1 = inStream.readUnsignedWord();
				int j10 = inStream.method426();
				if (l1 == 65535)
					l1 = -1;
				tabInterfaceIDs[j10] = l1;
				needDrawTabArea = true;
				tabAreaAltered = true;
				prayerInterfaceType = tabInterfaceIDs[5];
				opCode = -1;
				return true;

			case 74:
				int songID = inStream.ig2();
				if (songID == 65535) {
					songID = -1;
				}
				if (songID != currentSong && musicEnabled && !lowMem
						&& prevSong == 0) {
					nextSong = songID;
					songChanging = true;
					onDemandFetcher.requestFileData(2, nextSong);
				}
				currentSong = songID;
				opCode = -1;
				return true;

			case 121:
				int j2 = inStream.readWordBigEndian();
				int k10 = inStream.readByteA();
				if (musicEnabled && !lowMem) {
					nextSong = j2;
					songChanging = false;
					onDemandFetcher.requestFileData(2, nextSong);
					prevSong = k10;
				}
				opCode = -1;
				return true;

			case 109:
				resetLogout();
				opCode = -1;
				return false;

			case 70:
				int x_off = inStream.readSignedWord();
				int y_off = inStream.method437();
				int interface_id = inStream.ig2();
				RSInterface rsi_select = RSInterface.interfaceCache[interface_id];
				rsi_select.xOffset = x_off;
				rsi_select.yOffset = y_off;
				opCode = -1;
				return true;

			case 73:
			case 241:
				int regionX = currentRegionX;
				int regionY = currentRegionY;
				if (opCode == 73) {
					regionX = inStream.readByteA();
					regionY = inStream.readUnsignedWord();
					requestMapReconstruct = false;
				}
				if (opCode == 241) {
					regionY = inStream.readByteA();
					for (int z = 0; z < 4; z++) {
						for (int x = 0; x < 13; x++) {
							for (int y = 0; y < 13; y++) {
								int i26 = inStream.readByte();
								if (i26 == 5)
									constructRegionData[z][x][y] = Integer.parseInt(inStream.readString());
								else
									constructRegionData[z][x][y] = -1;

								//	System.out.println(constructRegionData[z][x][y]);
							}
						}
					}
					regionX = inStream.readUnsignedWord();
					requestMapReconstruct2 = true;
				}
				if (opCode != 241 && currentRegionX == regionX
						&& currentRegionY == regionY && loadingStage == 2) {
					opCode = -1;
					return true;
				}
				currentRegionX = regionX;
				currentRegionY = regionY;
				baseX = (currentRegionX - 6) * 8;
				baseY = (currentRegionY - 6) * 8;
				loadingStage = 1;
				mapLoadingTime = System.currentTimeMillis();
				gameScreenIP.initDrawingArea();
				if (opCode == 73) {
					int k16 = 0;
					for (int i21 = (currentRegionX - 6) / 8; i21 <= (currentRegionX + 6) / 8; i21++) {
						for (int k23 = (currentRegionY - 6) / 8; k23 <= (currentRegionY + 6) / 8; k23++)
							k16++;
					}
					terrainData = new byte[k16][];
					objectData = new byte[k16][];
					mapCoordinates = new int[k16];
					terrainIndices = new int[k16];
					objectIndices = new int[k16];
					k16 = 0;
					for (int l23 = (currentRegionX - 6) / 8; l23 <= (currentRegionX + 6) / 8; l23++) {
						for (int j26 = (currentRegionY - 6) / 8; j26 <= (currentRegionY + 6) / 8; j26++) {
							mapCoordinates[k16] = (l23 << 8) + j26;
							if (inTutorialIsland
									&& (j26 == 49 || j26 == 149 || j26 == 147
									|| l23 == 50 || l23 == 49
									&& j26 == 47)) {
								terrainIndices[k16] = -1;
								objectIndices[k16] = -1;
								k16++;
							} else {
								int k28 = terrainIndices[k16] = onDemandFetcher
										.getMapIdForRegions(0, j26, l23);
								if (k28 != -1)
									onDemandFetcher.requestFileData(3, k28);
								int j30 = objectIndices[k16] = onDemandFetcher
										.getMapIdForRegions(1, j26, l23);
								if (j30 != -1)
									onDemandFetcher.requestFileData(3, j30);
								k16++;
							}
						}
					}
				}
				if (opCode == 241) {
					int totalLegitChunks = 0;
					int totalChunks[] = new int[676];
					for (int z = 0; z < 4; z++) {
						for (int x = 0; x < 13; x++) {
							for (int y = 0; y < 13; y++) {
								int tileBits = constructRegionData[z][x][y];
								if (tileBits != -1) {
									int xCoord = tileBits >> 14 & 0x3ff;
							int yCoord = tileBits >> 3 & 0x7ff;
					int mapRegion = (xCoord / 8 << 8) + yCoord
							/ 8;
					for (int idx = 0; idx < totalLegitChunks; idx++) {
						if (totalChunks[idx] != mapRegion)
							continue;
						mapRegion = -1;

					}
					if (mapRegion != -1)
						totalChunks[totalLegitChunks++] = mapRegion;
								}
							}
						}
					}
					terrainData = new byte[totalLegitChunks][];
					objectData = new byte[totalLegitChunks][];
					mapCoordinates = new int[totalLegitChunks];
					terrainIndices = new int[totalLegitChunks];
					objectIndices = new int[totalLegitChunks];
					for (int idx = 0; idx < totalLegitChunks; idx++) {
						int region = mapCoordinates[idx] = totalChunks[idx];
						int l30 = region >> 8 & 0xff;
					int l31 = region & 0xff;
					int terrainMapId = terrainIndices[idx] = onDemandFetcher
							.getMapIdForRegions(0, l31, l30);
					if (terrainMapId != -1)
						onDemandFetcher.requestFileData(3, terrainMapId);
					int objectMapId = objectIndices[idx] = onDemandFetcher
							.getMapIdForRegions(1, l31, l30);
					if (objectMapId != -1)
						onDemandFetcher.requestFileData(3, objectMapId);
					}
				}
				int i17 = baseX - anInt1036;
				int j21 = baseY - anInt1037;
				anInt1036 = baseX;
				anInt1037 = baseY;
				for (int npcIdx = 0; npcIdx < 16384; npcIdx++) {
					NPC npc = npcArray[npcIdx];
					if (npc != null) {
						for (int j29 = 0; j29 < 10; j29++) {
							npc.pathX[j29] -= i17;
							npc.pathY[j29] -= j21;
						}
						npc.x -= i17 * 128;
						npc.y -= j21 * 128;
					}
				}
				for (int plrIdx = 0; plrIdx < maxPlayers; plrIdx++) {
					Player player = playerArray[plrIdx];
					if (player != null) {
						for (int i31 = 0; i31 < 10; i31++) {
							player.pathX[i31] -= i17;
							player.pathY[i31] -= j21;
						}
						player.x -= i17 * 128;
						player.y -= j21 * 128;
					}
				}
				loadingMap = true;
				byte minX = 0;
				byte endX = 104;
				byte incrementX = 1;
				if (i17 < 0) {
					minX = 103;
					endX = -1;
					incrementX = -1;
				}
				byte minY = 0;
				byte endY = 104;
				byte incrementY = 1;
				if (j21 < 0) {
					minY = 103;
					endY = -1;
					incrementY = -1;
				}
				for (int x = minX; x != endX; x += incrementX) {
					for (int y = minY; y != endY; y += incrementY) {
						int xTile = x + i17;
						int yTile = y + j21;
						for (int plane = 0; plane < 4; plane++)
							if (xTile >= 0 && yTile >= 0 && xTile < 104
							&& yTile < 104)
								groundArray[plane][x][y] = groundArray[plane][xTile][yTile];
							else
								groundArray[plane][x][y] = null;
					}
				}
				for (GameObjectSpawnRequest request = (GameObjectSpawnRequest) objectSpawnDeque
						.getFront(); request != null; request = (GameObjectSpawnRequest) objectSpawnDeque
						.getNext()) {
					request.tileX -= i17;
					request.tileY -= j21;
					if (request.tileX < 0 || request.tileY < 0
							|| request.tileX >= 104 || request.tileY >= 104)
						request.unlink();
				}
				if (destX != 0) {
					destX -= i17;
					destY -= j21;
				}
				inCutScene = false;
				opCode = -1;
				return true;

			case 208:
				int i3 = inStream.readUnsignedWord();
				if(i3 == 65535)
					i3 = -1;
				if (i3 >= 0)
					resetInterfaceAnimation(i3);
				if(i3 == 37400)
					gravestoneInterface = true;
				else {
					walkableInterfaceId = i3;
					if(i3 == -1 && gravestoneInterface)
						gravestoneInterface = false;
				}
				opCode = -1;
				return true;

			case 99:
				minimapStatus = inStream.readUnsignedByte();
				opCode = -1;
				return true;

			case 75:
				int mediaId = inStream.readWordBigEndian();
				int j11 = inStream.readWordBigEndian();
				RSInterface.interfaceCache[j11].mediaType = 2;
				RSInterface.interfaceCache[j11].mediaID = mediaId;
				opCode = -1;
				return true;

			case 114:
				updateMinutes = inStream.ig2() * 30;
				opCode = -1;
				return true;

			case 60:
				bigRegionY = inStream.readUnsignedByte();
				bigRegionX = inStream.nglb();
				while (inStream.currentOffset < pktSize) {
					int k3 = inStream.readUnsignedByte();
					parseEntityPacket(inStream, k3);
				}
				opCode = -1;
				return true;

			case 35:
				int screenStateIdx = inStream.readUnsignedByte();
				int k11 = inStream.readUnsignedByte();
				int j17 = inStream.readUnsignedByte();
				int k21 = inStream.readUnsignedByte();
				aBooleanArray876[screenStateIdx] = true;
				anIntArray873[screenStateIdx] = k11;
				anIntArray1203[screenStateIdx] = j17;
				anIntArray928[screenStateIdx] = k21;
				anIntArray1030[screenStateIdx] = 0;
				opCode = -1;
				return true;

			case 174:
				followPlayer = 0;
				followNPC = 0;
				int l11z = inStream.readUnsignedWord();
				int iq = inStream.readUnsignedByte();
				followDistance = inStream.readUnsignedWord();
				if (iq == 0) {
					followNPC = l11z;
				} else if (iq == 1) {
					followPlayer = l11z;
				}
				opCode = -1;
				return true;
			case 178:
				boolean active = inStream.readByte() == 1;
				drawPane = active;
				opCode = -1;
				return true;

			case 104:
				int optionId = inStream.nglb();
				int i12 = inStream.method426();
				String ptionContent = inStream.readString();
				if (optionId >= 1 && optionId <= 5) {
					if (ptionContent.equalsIgnoreCase("null"))
						ptionContent = null;
					atPlayerActions[optionId - 1] = ptionContent;
					atPlayerArray[optionId - 1] = i12 == 0;
				}
				opCode = -1;
				return true;

			case 78:
				destX = 0;
				opCode = -1;
				return true;

			case 253:
				String s = inStream.readString();
				if(s.equals("@CANTADDXP:LOCKED@")) {
					gains.add(new XPGain(-244, -244));
					opCode = -1;
					return true;
				} else if(s.startsWith("@SETVOLUME:")) {
					final int vol = Integer.parseInt(s.substring(12));
					SoundPlayer.setVolume(vol);
					opCode = -1;
					return true;
				}
				if (s.endsWith(":tradereq:")) {
					String s3 = s.substring(0, s.indexOf(":"));
					long l17 = TextClass.longForName(s3);
					boolean flag2 = false;
					for (int j27 = 0; j27 < ignoreCount; j27++) {
						if (ignoreListAsLongs[j27] != l17)
							continue;
						flag2 = true;

					}
					if (!flag2 && s3.length() >= 2)
						pushMessage("wishes to trade with you. Click here to accept the invitation.", 4, s3);
				} else if (s.endsWith("::")) {
					String s4 = s.substring(0, s.indexOf(":"));
					TextClass.longForName(s4);
					pushMessage("Clan: ", 8, s4);
				} else if(s.equalsIgnoreCase("@autocastoff")) {
					setAutoCastOff();
					//SpriteCache.spriteCache[47].drawSprite(1000, 1000);
					opCode = -1;
					return true;
				} else if (s.endsWith(":duelreq:")) {
					String s4 = s.substring(0, s.indexOf(":"));
					long l18 = TextClass.longForName(s4);
					boolean flag3 = false;
					for (int k27 = 0; k27 < ignoreCount; k27++) {
						if (ignoreListAsLongs[k27] != l18)
							continue;
						flag3 = true;

					}
					if (!flag3 && s4.length() >= 2)
						pushMessage("wishes to duel with you. Click here to accept the invitation.", 8, s4);
				} else if (s.endsWith(":chalreq:")) {
					String s5 = s.substring(0, s.indexOf(":"));
					long l19 = TextClass.longForName(s5);
					boolean flag4 = false;
					for (int l27 = 0; l27 < ignoreCount; l27++) {
						if (ignoreListAsLongs[l27] != l19)
							continue;
						flag4 = true;

					}
					if (!flag4 ) {
						String s8 = s.substring(s.indexOf(":") + 1,
								s.length() - 9);
						pushMessage(s8, 8, s5);
					}
				} else {
					boolean clan = s.startsWith("@clan:A@");
					if(clan)
						s = s.substring(8, s.length());
					pushMessage(s, clan ? 16 : 0, "");
				}
				if(s.equalsIgnoreCase("oh dear, you are dead!"))
					quickPrsActive = false;
				opCode = -1;
				return true;

			case 1:
				for (int k4 = 0; k4 < playerArray.length; k4++)
					if (playerArray[k4] != null)
						playerArray[k4].anim = -1;
				for (int j12 = 0; j12 < npcArray.length; j12++)
					if (npcArray[j12] != null)
						npcArray[j12].anim = -1;
				opCode = -1;
				return true;

			case 50:
				long l4 = inStream.readQWord();
				int i18 = inStream.readUnsignedByte();
				String s7 = TextClass.fixName(TextClass.nameForLong(l4));
				for (int k24 = 0; k24 < friendsCount; k24++) {
					if (l4 != friendsListAsLongs[k24])
						continue;
					if (friendsNodeIDs[k24] != i18) {
						friendsNodeIDs[k24] = i18;
						needDrawTabArea = true;
						/*if (i18 >= 2) {
							pushMessage(s7 + " has logged in.", 5, "");
						}
						if (i18 <= 1) {
							pushMessage(s7 + " has logged out.", 5, "");
						}*/
					}
					s7 = null;

				}
				if (s7 != null && friendsCount < 200) {
					friendsListAsLongs[friendsCount] = l4;
					friendsList[friendsCount] = s7;
					friendsNodeIDs[friendsCount] = i18;
					friendsCount++;
					needDrawTabArea = true;
				}
				for (boolean flag6 = false; !flag6;) {
					flag6 = true;
					for (int k29 = 0; k29 < friendsCount - 1; k29++)
						if (friendsNodeIDs[k29] != nodeID
						&& friendsNodeIDs[k29 + 1] == nodeID
						|| friendsNodeIDs[k29] == 0
						&& friendsNodeIDs[k29 + 1] != 0) {
							int j31 = friendsNodeIDs[k29];
							friendsNodeIDs[k29] = friendsNodeIDs[k29 + 1];
							friendsNodeIDs[k29 + 1] = j31;
							String s10 = friendsList[k29];
							friendsList[k29] = friendsList[k29 + 1];
							friendsList[k29 + 1] = s10;
							long l32 = friendsListAsLongs[k29];
							friendsListAsLongs[k29] = friendsListAsLongs[k29 + 1];
							friendsListAsLongs[k29 + 1] = l32;
							needDrawTabArea = true;
							flag6 = false;
						}
				}
				opCode = -1;
				return true;

			case 110:
				if (tabID == 12)
					needDrawTabArea = true;
				currentEnergy = inStream.readUByte();
				opCode = -1;
				return true;

			case 254:
				anInt855 = inStream.readUnsignedByte();
				if (anInt855 == 1)
					anInt1222 = inStream.readUnsignedWord();
				if (anInt855 >= 2 && anInt855 <= 6) {
					if (anInt855 == 2) {
						anInt937 = 64;
						anInt938 = 64;
					}
					if (anInt855 == 3) {
						anInt937 = 0;
						anInt938 = 64;
					}
					if (anInt855 == 4) {
						anInt937 = 128;
						anInt938 = 64;
					}
					if (anInt855 == 5) {
						anInt937 = 64;
						anInt938 = 0;
					}
					if (anInt855 == 6) {
						anInt937 = 64;
						anInt938 = 128;
					}
					anInt855 = 2;
					anInt934 = inStream.readUnsignedWord();
					anInt935 = inStream.readUnsignedWord();
					anInt936 = inStream.readUnsignedByte();
				}
				if (anInt855 == 10)
					anInt933 = inStream.readUnsignedWord();
				opCode = -1;
				return true;

			case 248:
				int i5 = inStream.readByteA();
				int k12 = inStream.readUnsignedWord();
				if (backDialogID != -1) {
					backDialogID = -1;
					inputTaken = true;
				}
				if (inputDialogState != 0) {
					inputDialogState = 0;
					inputTaken = true;
				}
				openInterfaceID = i5;
				invOverlayInterfaceID = k12;
				needDrawTabArea = true;
				tabAreaAltered = true;
				aBoolean1149 = false;
				opCode = -1;
				return true;

			case 79:
				int j5 = inStream.ig2();
				int l12 = inStream.readByteA();
				RSInterface class9_3 = RSInterface.interfaceCache[j5];
				if (class9_3 != null && class9_3.type == 0) {
					if (l12 < 0)
						l12 = 0;
					if (l12 > class9_3.scrollMax - class9_3.height)
						l12 = class9_3.scrollMax - class9_3.height;
					class9_3.scrollPosition = l12;
				}
				opCode = -1;
				return true;

			case 68:
				for (int k5 = 0; k5 < variousSettings.length; k5++)
					if (variousSettings[k5] != varbitConfigs[k5]) {
						variousSettings[k5] = varbitConfigs[k5];
						handleActions(k5);
						needDrawTabArea = true;
					}
				opCode = -1;
				return true;

			case 196:
				long l5 = inStream.readQWord();
				inStream.readDWord();
				int playerRights = inStream.readUnsignedByte();
				boolean flag5 = false;
				if (playerRights <= 1) {
					for (int l29 = 0; l29 < ignoreCount; l29++) {
						if (ignoreListAsLongs[l29] != l5)
							continue;
						flag5 = true;

					}
				}
				if (!flag5)
					try {
						String message = TextInput.decodeToString(pktSize - 13,
								inStream);
						if (playerRights != 0) {
							pushMessage(
									message,
									7,
									getPrefix(playerRights)
									+ TextClass.fixName(TextClass
											.nameForLong(l5)));
						} else {
							pushMessage(message, 3, TextClass.fixName(TextClass
									.nameForLong(l5)));
						}
					} catch (Exception exception1) {
						signlink.reporterror("cde1");
					}
				opCode = -1;
				return true;
			case 85:
				bigRegionY = inStream.nglb();
				bigRegionX = inStream.nglb();
				opCode = -1;
				return true;

			case 86:
				int l = inStream.readUByte();
				if(l >= 0 && l < 4) {
					plane = l;/*
					ObjectManager.anInt131 = l;
					lastKnownPlane = l;
					renderedMapScene(l);
					loadingStage = 2;
					loadRegion();
					stream.createFrame(121);*/
				}
				opCode = -1;
				return true;
				
			case 88:
				opCode = -1;
				return true;

			case 24:
				anInt1054 = inStream.readByteS();
				if (anInt1054 == tabID) {
					if (anInt1054 == 3)
						tabID = 1;
					else
						tabID = 3;
					needDrawTabArea = true;
				}
				opCode = -1;
				return true;

			case 28:
				playerCommand = inStream.readUnsignedByte();
				showInput = false;
				inputDialogState = 5;
				amountOrNameInput = "";
				inputTaken = true;
				opCode = -1;
				return true;
			case 29:
				int value = inStream.readUnsignedByte();
				shadowProcessing = true;
				if (value == 250 || value == 0)
					value = 255;
				shadowDestination = value;
				opCode = -1;
				return true;
			case 246:
				int i6 = inStream.ig2();
				int i13 = inStream.readUnsignedWord();
				int k18 = inStream.readUnsignedWord();
				if (k18 == 65535) {
					RSInterface.interfaceCache[i6].mediaType = 0;
					opCode = -1;
					return true;
				} else {
					ItemDef itemDef = ItemDef.forID(k18);
					RSInterface.interfaceCache[i6].mediaType = 4;
					RSInterface.interfaceCache[i6].mediaID = k18;
					RSInterface.interfaceCache[i6].modelRotation1 = itemDef.rotationY;
					RSInterface.interfaceCache[i6].modelRotation2 = itemDef.rotationX;
					RSInterface.interfaceCache[i6].modelZoom = (itemDef.modelZoom * 100)
							/ i13;
					opCode = -1;
					return true;
				}

			case 171:
				boolean flag1 = inStream.readUnsignedByte() == 1;
				int j13 = inStream.readUnsignedWord();
				RSInterface.interfaceCache[j13].interfaceShown = flag1;
				opCode = -1;
				return true;

			case 142:
				int j6 = inStream.ig2();
				resetInterfaceAnimation(j6);
				if (backDialogID != -1) {
					backDialogID = -1;
					inputTaken = true;
				}
				if (inputDialogState != 0) {
					inputDialogState = 0;
					inputTaken = true;
				}
				invOverlayInterfaceID = j6;
				needDrawTabArea = true;
				tabAreaAltered = true;
				openInterfaceID = -1;
				aBoolean1149 = false;
				opCode = -1;
				return true;

			case 113:
				running = inStream.readUnsignedByte() > 0;
				opCode = -1;
				return true;

			case 127:
				myRights = inStream.readUnsignedByte();
				opCode = -1;
				return true;

			case 126:
				String text = inStream.readString();
				int frame = inStream.readByteA();
				if (text.startsWith("[SUMMO")) {
					hasFamiliar = Boolean.parseBoolean(text.substring(6));
					opCode = -1;
					return true;
				} else if(text.startsWith("[DUNG]")) {
					doingDung = Boolean.parseBoolean(text.substring(6));
					opCode = -1;
					return true;
				}
				if (text.startsWith("[UPDATEEREPORT")) {
					reportBox2Selected = text.equalsIgnoreCase("[UPDATEEREPORT 2]");
					opCode = -1;
					return true;
				} else if(text.equals("[BUGREPORT]")) {
					clearTopInterfaces();
					reportAbuseInput = "";
					reasonForReport = "";
					playerReporting = "Bug";
					canMute = false;
					sendPacket185(9999);
					reportBox2Selected = true;
					opCode = -1;
					return true;
				} else if(text.equals("[CLOSEMENU]") && frame == 0) {
					menuOpen = false;
					opCode = -1;
					return true;
				}
				if (frame == 0 && text.equals("resting"))
					resting = false;
				if (text.startsWith("http://") || text.startsWith("www.")) {
					launchURL(text);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[REG]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].disabledSprite = RSInterface.interfaceCache[frame + 20000].savedSprite[0];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[OWN]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].disabledSprite = RSInterface.interfaceCache[frame + 20000].savedSprite[1];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[MOD]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].disabledSprite = RSInterface.interfaceCache[frame + 20000].savedSprite[2];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[REC]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].disabledSprite = RSInterface.interfaceCache[frame + 20000].savedSprite[3];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[COR]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].disabledSprite = RSInterface.interfaceCache[frame + 20000].savedSprite[4];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[SER]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].disabledSprite = RSInterface.interfaceCache[frame + 20000].savedSprite[5];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[LIE]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].disabledSprite = RSInterface.interfaceCache[frame + 20000].savedSprite[6];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[BER]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].disabledSprite = RSInterface.interfaceCache[frame + 20000].savedSprite[7];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[VR]")) {
					text = text.substring(3);
					RSInterface.interfaceCache[frame + 20000].disabledSprite = RSInterface.interfaceCache[frame + 20000].savedSprite[8];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (text.startsWith("[FRI]")) {
					text = text.substring(4);
					RSInterface.interfaceCache[frame + 20000].disabledSprite = RSInterface.interfaceCache[frame + 20000].savedSprite[9];
					updateStrings(text, frame);
					sendFrame126(text, frame);
					opCode = -1;
					return true;
				}
				if (frame == 47997) {
					Recruits = "";
					Recruits = text;
					opCode = -1;
					return true;
				}
				if (frame == 47996) {
					Corporals = "";
					Corporals = text;
					opCode = -1;
					return true;
				}
				if (frame == 47995) {
					Sergeants = "";
					Sergeants = text;
					opCode = -1;
					return true;
				}
				if (frame == 47994) {
					Lieutenants = "";
					Lieutenants = text;
					opCode = -1;
					return true;
				}
				if (frame == 47993) {
					Captains = "";
					Captains = text;
					opCode = -1;
					return true;
				}
				if (frame == 47992) {
					Generals = "";
					Generals = text;
					opCode = -1;
					return true;
				}
				if (text.startsWith("[UPDATE]")) {
					slot = 44001;
					for (int a = 0; a < friendsCount; a++) {
						if (isRecruit("" + friendsList[a] + ""))
							sendFrame126("Recruit", slot + 800);
						else if (isCorporal("" + friendsList[a] + ""))
							sendFrame126("Corporal", slot + 800);
						else if (isSergeant("" + friendsList[a] + ""))
							sendFrame126("Sergeant", slot + 800);
						else if (isLieutenant("" + friendsList[a] + ""))
							sendFrame126("Lieutenant", slot + 800);
						else if (isCaptain("" + friendsList[a] + ""))
							sendFrame126("Captain", slot + 800);
						else if (isGeneral("" + friendsList[a] + ""))
							sendFrame126("General", slot + 800);
						else
							sendFrame126("Not ranked", slot + 800);
						sendFrame126(friendsList[a], slot);
						slot++;
					}
					opCode = -1;
					return true;
				}
				if (text.startsWith("[FI]")) {
					text = text.substring(4);
					otherPlayerId = Integer.parseInt(text);
					opCode = -1;
					return true;
				}
				updateStrings(text, frame);
				sendFrame126(text, frame);
				if (frame >= 18144 && frame <= 18244) {
					clanList[frame - 18144] = text;
				}
				opCode = -1;
				return true;

			case 206:
				publicChatMode = inStream.readUnsignedByte();
				privateChatMode = inStream.readUnsignedByte();
				tradeMode = inStream.readUnsignedByte();
				inputTaken = true;
				opCode = -1;
				return true;

			case 240:
				if (tabID == 12)
					needDrawTabArea = true;
				weight = inStream.readSignedWord();
				opCode = -1;
				return true;

			case 8:
				int k6 = inStream.readWordBigEndian();
				int l13 = inStream.readUnsignedWord();
				RSInterface.interfaceCache[k6].mediaType = 1;
				RSInterface.interfaceCache[k6].mediaID = l13;
				opCode = -1;
				return true;

			case 122:
				int rsi_id = inStream.readWordBigEndian();
				int i14 = inStream.readWordBigEndian();
				int i19 = i14 >> 10 & 0x1f;
				int i22 = i14 >> 5 & 0x1f;
				int l24 = i14 & 0x1f;
				RSInterface.interfaceCache[rsi_id].disabledColor = (i19 << 19)
						+ (i22 << 11) + (l24 << 3);
				opCode = -1;
				return true;

				case 53:
					needDrawTabArea = true;
					try {
						int rsi_frame = inStream.readUnsignedWord();
						RSInterface rsi_1 = RSInterface.interfaceCache[rsi_frame];
						int totalItems = inStream.readUnsignedWord();
						for (int idx = 0; idx < totalItems; idx++) {
							int itemAmt = inStream.readUnsignedByte();
							if (itemAmt == 255)
								itemAmt = inStream.method440();
							rsi_1.inv[idx] = inStream.readWordBigEndian();
							rsi_1.invStackSizes[idx] = itemAmt;
						}
						for (int idx = totalItems; idx < rsi_1.inv.length && idx < rsi_1.invStackSizes.length; idx++) {
							rsi_1.inv[idx] = 0;
							rsi_1.invStackSizes[idx] = 0;
						}
					} catch(Exception e) {

					}
					opCode = -1;
					return true;

				case 230:
					int j7 = inStream.readByteA();
					int j14 = inStream.readUnsignedWord();
					int k19 = inStream.readUnsignedWord();
					int k22 = inStream.readWordBigEndian();
					RSInterface.interfaceCache[j14].modelRotation1 = k19;
					RSInterface.interfaceCache[j14].modelRotation2 = k22;
					RSInterface.interfaceCache[j14].modelZoom = j7;
					opCode = -1;
					return true;

				case 221:
					inputTextType = inStream.readUnsignedByte();
					needDrawTabArea = true;
					opCode = -1;
					return true;

				case 177:
					inCutScene = true;
					anInt995 = inStream.readUnsignedByte();
					anInt996 = inStream.readUnsignedByte();
					anInt997 = inStream.readUnsignedWord();
					anInt998 = inStream.readUnsignedByte();
					anInt999 = inStream.readUnsignedByte();
					if (anInt999 >= 100) {
						int k7 = anInt995 * 128 + 64;
						int k14 = anInt996 * 128 + 64;
						int i20 = getFloorDrawHeight(plane, k14, k7) - anInt997;
						int l22 = k7 - xCameraPos;
						int k25 = i20 - zCameraPos;
						int j28 = k14 - yCameraPos;
						int i30 = (int) Math.sqrt(l22 * l22 + j28 * j28);
						yCameraCurve = (int) (Math.atan2(k25, i30) * 325.94900000000001D) & 0x7ff;
						xCameraCurve = (int) (Math.atan2(l22, j28) * -325.94900000000001D) & 0x7ff;
						if (yCameraCurve < 128)
							yCameraCurve = 128;
						if (yCameraCurve > 383)
							yCameraCurve = 383;
					}
					opCode = -1;
					return true;

				case 249:
					anInt1046 = inStream.method426();
					playerId = inStream.readWordBigEndian();
					opCode = -1;
					return true;

				case 65:
					updateNPCs(inStream, pktSize);
					opCode = -1;
					return true;

				case 27:
					inputTitle = new String(inStream.readString());
					showInput = false;
					inputDialogState = 1;
					amountOrNameInput = "";
					inputTaken = true;
					opCode = -1;
					return true;

				case 31:
					inputDialogState = 1;
					amountOrNameInput = "";
					inputTaken = true;
					opCode = -1;
					return true;

				case 187:
					inputTitle = new String(inStream.readString());
					showInput = false;
					inputDialogState = 2;
					amountOrNameInput = "";
					inputTaken = true;
					opCode = -1;
					return true;

				case 97:
					try {
						int interfaceID = inStream.readUnsignedWord();
						resetInterfaceAnimation(interfaceID);
						if (invOverlayInterfaceID != -1) {
							invOverlayInterfaceID = -1;
							needDrawTabArea = true;
							tabAreaAltered = true;
						}
						if (backDialogID != -1) {
							backDialogID = -1;
							inputTaken = true;
						}
						if (inputDialogState != 0) {
							inputDialogState = 0;
							inputTaken = true;
						}
						if (interfaceID == 24600) {
							buttonclicked = false;
							interfaceButtonAction = 0;
						}
						openInterfaceID = interfaceID;
						aBoolean1149 = false;
					} catch (Exception e) {
						e.printStackTrace();
					}
					withdrawingMoneyFromPouch = false;
					opCode = -1;
					return true;

				case 218:
					int i8 = inStream.method438();
					dialogID = i8;
					inputTaken = true;
					opCode = -1;
					return true;

				case 87:
					int settingIdx = inStream.ig2();
					int settingValue = inStream.method439();
					varbitConfigs[settingIdx] = settingValue;
					if (variousSettings[settingIdx] != settingValue) {
						variousSettings[settingIdx] = settingValue;
						handleActions(settingIdx);
						needDrawTabArea = true;
						if (dialogID != -1)
							inputTaken = true;
					}
					opCode = -1;
					return true;

				case 36:
					int k8 = inStream.ig2();
					byte byte0 = inStream.readSignedByte();
					varbitConfigs[k8] = byte0;
					if (variousSettings[k8] != byte0) {
						variousSettings[k8] = byte0;
						handleActions(k8);
						needDrawTabArea = true;
						if (dialogID != -1)
							inputTaken = true;
					}
					opCode = -1;
					return true;

				case 61:
					drawMultiwayIcon = inStream.readUnsignedByte();
					opCode = -1;
					return true;

				case 200:
					int l8 = inStream.readUnsignedWord();
					int i15 = inStream.readSignedWord();
					RSInterface class9_4 = RSInterface.interfaceCache[l8];
					class9_4.disabledAnimationId = i15;
					if (i15 == -1) {
						class9_4.currentFrame = 0;
						class9_4.frameTimer = 0;
					}
					class9_4.modelZoom = 1600;
					opCode = -1;
					return true;

				case 219:
					if (invOverlayInterfaceID != -1) {
						invOverlayInterfaceID = -1;
						needDrawTabArea = true;
						tabAreaAltered = true;
					}
					if (backDialogID != -1) {
						backDialogID = -1;
						inputTaken = true;
					}
					if (inputDialogState != 0) {
						inputDialogState = 0;
						inputTaken = true;
					}
					openInterfaceID = -1;
					aBoolean1149 = false;
					withdrawingMoneyFromPouch = false;
					opCode = -1;
					return true;

				case 34:
					needDrawTabArea = true;
					int i9 = inStream.readUnsignedWord();
					RSInterface class9_2 = RSInterface.interfaceCache[i9];
					while (inStream.currentOffset < pktSize) {
						int j20 = inStream.method422();
						int i23 = inStream.readUnsignedWord();
						int l25 = inStream.readUnsignedByte();
						if (l25 == 255)
							l25 = inStream.readDWord();
						if (j20 >= 0 && j20 < class9_2.inv.length) {
							class9_2.inv[j20] = i23;
							class9_2.invStackSizes[j20] = l25;
						}
					}
					opCode = -1;
					return true;

				case 4:
				case 44:
				case 84:
				case 101:
				case 105:
				case 117:
				case 147:
				case 151:
				case 152:
				case 153:
				case 156:
				case 160:
				case 215:
					parseEntityPacket(inStream, opCode);
					opCode = -1;
					return true;

				case 106:
					tabID = inStream.nglb();
					needDrawTabArea = true;
					tabAreaAltered = true;
					opCode = -1;
					return true;

				case 164:
					int j9 = inStream.ig2();
					resetInterfaceAnimation(j9);
					if (invOverlayInterfaceID != -1) {
						invOverlayInterfaceID = -1;
						needDrawTabArea = true;
						tabAreaAltered = true;
					}
					backDialogID = j9;
					inputTaken = true;
					openInterfaceID = -1;
					aBoolean1149 = false;
					withdrawingMoneyFromPouch = false;
					opCode = -1;
					return true;

			}
			signlink.reporterror("Packet not handled: " + opCode + ","
					+ pktSize + " - " + opcode_last + "," + opcode_second);
			// resetLogout();
		} catch (IOException _ex) {
			try {
				System.out.println("Kicked player out, reasson :"+_ex);
				dropClient();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
		} catch (Exception exception) {
			String s2 = "Packet received but exception occurred: " + opCode
					+ "," + opcode_last + "," + opcode_second + " - " + pktSize
					+ "," + (baseX + myPlayer.pathX[0]) + ","
					+ (baseY + myPlayer.pathY[0]) + " - ";
			for (int j15 = 0; j15 < pktSize && j15 < 50; j15++)
				s2 = s2 + inStream.buffer[j15] + ",";
			exception.printStackTrace();
			// resetLogout();
		}
		opCode = -1;
		return true;
	}

	public static int clientZoom = 0;

	private void renderWorld() {
		anInt1265++;
		int j = 0;
		int l = xCameraPos;
		int i1 = zCameraPos;
		int j1 = yCameraPos;
		int k1 = yCameraCurve;
		int l1 = xCameraCurve;
		if (loggedIn) {
			processPlayers(true);
			processNpcs(true);
			processPlayers(false);
			processNpcs(false);
			processProjectiles();
			processStillGraphic();
			if (!inCutScene) {
				int i = anInt1184;
				if (anInt984 / 256 > i)
					i = anInt984 / 256;
				if (aBooleanArray876[4] && anIntArray1203[4] + 128 > i)
					i = anIntArray1203[4] + 128;
				int k = viewRotation + viewRotationOffset & 0x7ff;
				int zoom = (600 + (i * clientHeight / 400) + clientZoom);
				setCameraPos(clientSize == 0 ? (600 + i * 3) + clientZoom
						: zoom, i, anInt1014,
						getFloorDrawHeight(plane, myPlayer.y, myPlayer.x) - 50,
						k, anInt1015);
			}
			if (!inCutScene)
				j = getCameraHeight();
			else
				j = getCamHeight();
			for (int i2 = 0; i2 < 5; i2++)
				if (aBooleanArray876[i2]) {
					int j2 = (int) ((Math.random()
							* (double) (anIntArray873[i2] * 2 + 1) - (double) anIntArray873[i2]) + Math
							.sin((double) anIntArray1030[i2]
									* ((double) anIntArray928[i2] / 100D))
									* (double) anIntArray1203[i2]);
					if (i2 == 0)
						xCameraPos += j2;
					if (i2 == 1)
						zCameraPos += j2;
					if (i2 == 2)
						yCameraPos += j2;
					if (i2 == 3)
						xCameraCurve = xCameraCurve + j2 & 0x7ff;
					if (i2 == 4) {
						yCameraCurve += j2;
						if (yCameraCurve < 128)
							yCameraCurve = 128;
						if (yCameraCurve > 383)
							yCameraCurve = 383;
					}
				}
		}
		Model.objectExists = true;
		Model.objectsRendered = 0;
		Model.currentCursorX = super.mouseX - 4;
		Model.currentCursorY = super.mouseY - 4;
		DrawingArea.resetImage();
		if (loggedIn) {
			//pl not ej
			worldController.render(xCameraPos, yCameraPos, xCameraCurve,
					zCameraPos, plane, yCameraCurve);
			worldController.clearInteractableObjects();
		}
		updateEntities();
		drawHeadIcon();
		TextureAnimating.animateTexture();
		if (drawPane)
			drawBlackPane();
		if (loggedIn) {
			if (type2 > -1)
				drawArrow(title, information, drawX, drawY, speed, pause, type2);
			drawUnfixedGame();
			draw3dScreen();

		}
		if (showXP && loggedIn) {
			displayXPCounter();
		}
		if(consoleOpen && loggedIn) {
			drawConsole();
			drawConsoleArea();
		}
		if (loggedIn) {
			gameScreenIP.drawGraphics(clientSize == 0 ? 4 : 0, super.graphics,
					clientSize == 0 ? 4 : 0);
			xCameraPos = l;
			zCameraPos = i1;
			yCameraPos = j1;
			yCameraCurve = k1;
			xCameraCurve = l1;
		}

	}


	public int getAmountColor(long amount) {
		if (amount >= 10000000000L)
			return 0x00FFFF;
		if (amount >= 10000000)
			return 0x00FF80;
		if (amount >= 100000)
			return 0xFFFFFF;
		if (amount >= 1)
			return 0xFFFF00;
		return 0xFFFFFF;
	}

	public final String formatAmount(long amount) {
		String format = "Too high!";
		if (amount >= 0 && amount < 100000) {
			format = String.valueOf(amount);
		} else if (amount >= 100000 && amount < 1000000) {
			format = amount / 1000 + "K";
		} else if (amount >= 1000000 && amount < 1000000000L) {
			format = amount / 1000000 + "M";
		} else if (amount >= 1000000000L && amount < 1000000000000L) {
			format = amount / 1000000000 + "B";
		} else if (amount >= 10000000000000L && amount < 10000000000000000L) {
			format = amount / 1000000000000L + "T";
		} else if (amount >= 10000000000000000L && amount < 1000000000000000000L) {
			format = amount / 1000000000000000L + "QD";
		} else if (amount >= 1000000000000000000L && amount < Long.MAX_VALUE) {
			format = amount / 1000000000000000000L + "QT";
		}
		return format;
	}
	public void setNorth() {
		cameraOffsetX = 0;
		cameraOffsetY = 0;
		viewRotationOffset = 0;
		viewRotation = 0;
		minimapRotation = 0;
		minimapZoom = 0;
	}

	public void clearTopInterfaces() {
		stream.createFrame(130);
		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			needDrawTabArea = true;
			aBoolean1149 = false;
			tabAreaAltered = true;
		}
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
			aBoolean1149 = false;
		}
		openInterfaceID = -1;
		fullscreenInterfaceID = -1;
	}

	public Sprite[] loginImages;

	public Client() {
		hitMark = new Sprite[50];
		borderImages = new Sprite[8];
		hitIcon = new Sprite[20];
		bountyHunter = new Sprite[6];
		loginImages = new Sprite[16];
		consoleInput = "";
		consoleOpen = false;
		consoleMessages = new String[17];
		xpLock = false;
		xpCounter = 0;
		choosingLeftClick = false;
		leftClick = -1;
		fullscreenInterfaceID = -1;
		chatRights = new int[500];
		displayChat = true;
		chatTypeView = 0;
		clanChatMode = 0;
		cButtonHPos = -1;
		cButtonCPos = 0;
		anIntArrayArray825 = new int[104][104];
		friendsNodeIDs = new int[200];
		groundArray = new Deque[4][104][104];
		textStream = new Stream(new byte[5000]);
		npcArray = new NPC[50000];
		npcIndices = new int[50000];
		anIntArray840 = new int[1000];
		aStream_847 = Stream.create();
		soundEnabled = true;
		openInterfaceID = -1;
		currentExp = new int[Skills.skillsCount];

		anIntArray873 = new int[5];
		aBooleanArray876 = new boolean[5];
		reportAbuseInput = "";
		playerId = -1;
		menuOpen = false;
		inputString = "";
		maxPlayers = 2048;
		myPlayerIndex = 2047;
		playerArray = new Player[maxPlayers];
		playerIndices = new int[maxPlayers];
		playersToUpdate = new int[maxPlayers];
		aStreamArray895s = new Stream[maxPlayers];
		anInt897 = 1;
		anIntArrayArray901 = new int[104][104];
		currentStats = new int[Skills.skillsCount];
		ignoreListAsLongs = new long[100];
		loadingError = false;
		anIntArray928 = new int[5];
		anIntArrayArray929 = new int[104][104];
		chatTypes = new int[500];
		chatNames = new String[500];
		chatMessages = new String[500];
		sideIcons = new Sprite[17];
		aBoolean954 = true;
		friendsListAsLongs = new long[200];
		currentSong = -1;
		spriteDrawX = -1;
		spriteDrawY = -1;
		anIntArray968 = new int[33];
		cacheIndices = new Decompressor[8];
		variousSettings = new int[9000];
		aBoolean972 = false;
		anInt975 = 50;
		anIntArray976 = new int[anInt975];
		anIntArray977 = new int[anInt975];
		anIntArray978 = new int[anInt975];
		anIntArray979 = new int[anInt975];
		anIntArray980 = new int[anInt975];
		anIntArray981 = new int[anInt975];
		anIntArray982 = new int[anInt975];
		aStringArray983 = new String[anInt975];
		lastKnownPlane = -1;
		compass = new Sprite[2];
		//hitMark = new Sprite[50];
		//hitIcon = new Sprite[20];
		myAppearanceColors = new int[5];
		scrollPart = new Sprite[12];
		scrollBar = new Sprite[6];
		aBoolean994 = false;
		amountOrNameInput = "";
		projectileDeque = new Deque();
		aBoolean1017 = false;
		walkableInterfaceId = -1;
		anIntArray1030 = new int[5];
		aBoolean1031 = false;
		mapFunctions = new Sprite[100];
		dialogID = -1;
		currentMaxStats = new int[Skills.skillsCount];
		varbitConfigs = new int[9000];
		isMale = true;
		minimapXPosArray = new int[158];
		anInt1054 = -1;
		stillGraphicDeque = new Deque();
		anIntArray1057 = new int[33];
		aClass9_1059 = new RSInterface();
		mapScenes = new Background[100];
		myAppearance = new int[7];
		mapFunctionTileX = new int[1000];
		mapFunctionTileY = new int[1000];
		loadingMap = false;
		friendsList = new String[200];
		inStream = Stream.create();
		expectedCRCs = new int[9];
		menuActionCmd2 = new int[500];
		menuActionCmd3 = new int[500];
		menuActionCmd4 = new int[500];
		menuActionID = new int[500];
		menuActionCmd1 = new int[500];
		headIcons = new Sprite[20];
		skullIcons = new Sprite[20];
		headIconsHint = new Sprite[20];
		tabAreaAltered = false;
		promptMessage = "";
		atPlayerActions = new String[5];
		atPlayerArray = new boolean[5];
		constructRegionData = new int[4][13][13];
		anInt1132 = 2;
		currentMapFunctionSprites = new Sprite[1000];
		inTutorialIsland = false;
		aBoolean1149 = false;
		crosses = new Sprite[8];
		musicEnabled = true;
		needDrawTabArea = false;
		loggedIn = false;
		canMute = false;
		requestMapReconstruct = requestMapReconstruct2 = false;
		inCutScene = false;
		anInt1171 = 1;
		myUsername = "";
		myPassword = "";
		genericLoadingError = false;
		reportAbuseInterfaceID = -1;
		objectSpawnDeque = new Deque();
		anInt1184 = 128;
		invOverlayInterfaceID = -1;
		stream = Stream.create();
		menuActionName = new String[500];
		anIntArray1203 = new int[5];
		sound = new int[50];
		anInt1210 = 2;
		anInt1211 = 78;
		promptInput = "";
		playerIcons = new Sprite[32];
		tabID = 3;
		inputTaken = false;
		songChanging = true;
		minimapYPosArray = new int[158];
		clippingPlanes = new CollisionDetection[4];
		soundType = new int[50];
		aBoolean1242 = false;
		soundDelay = new int[50];
		soundVolume = new int[50];
		welcomeScreenRaised = false;
		showInput = false; inputTitle = null;
		loginMessage1 = "";
		loginMessage2 = "";
		backDialogID = -1;
		anInt1279 = 2;
		bigX = new int[4000];
		bigY = new int[4000];
	}

	public int rights;
	public String name;
	public String message;
	public String clanname;
	private final int[] chatRights;
	public int chatTypeView;
	public int clanChatMode;
	public int duelMode;
	/* Declare custom sprites */
	public Sprite loadingPleaseWait;
	public Sprite reestablish;
	public Sprite HPBarFull, HPBarEmpty, HPBarBigEmpty;
	public Sprite shortcutIcon, shortcutIconHover;
	/**/
	private RSImageProducer leftFrame;
	private RSImageProducer topFrame;
	private RSImageProducer rightFrame;
	private int ignoreCount;
	private long mapLoadingTime;
	private int[][] anIntArrayArray825;
	private int[] friendsNodeIDs;
	private Deque[][][] groundArray;
	private Socket jagGrabSocket;
	public int loginScreenState = 5;
	public int previousScreenState;
	private Stream textStream;
	private NPC[] npcArray;
	private int npcCount;
	private int[] npcIndices;
	private int anInt839;
	private int[] anIntArray840;
	private int anInt841;
	private int opcode_last;
	private int opcode_second;
	private String aString844;
	private int privateChatMode;
	private int gameChatMode;
	private Stream aStream_847;
	private boolean soundEnabled;
	private static int anInt849;
	private int anInt855;
	public static int openInterfaceID;
	public int xCameraPos;
	public int zCameraPos;
	public int yCameraPos;
	public int yCameraCurve;
	public int xCameraCurve;
	private int myRights;
	private int glowColor;
	final int[] currentExp;
	private Sprite mapFlag;
	private Sprite mapMarker;
	private final int[] anIntArray873;
	private final boolean[] aBooleanArray876;
	private int weight;
	private MouseDetection mouseDetection;
	private String reportAbuseInput;
	private int playerId;
	private boolean menuOpen;
	private int hoveredInterface;
	private String inputString;
	private final int maxPlayers;
	private final int myPlayerIndex;
	private Player[] playerArray;
	private int playerCount;
	private int[] playerIndices;
	private int playersToUpdateCount;
	private int[] playersToUpdate;
	private Stream[] aStreamArray895s;
	private int viewRotationOffset;
	private int anInt897;
	private int friendsCount;
	private int inputTextType;
	private int[][] anIntArrayArray901;
	private int anInt913;
	private int crossX;
	private int crossY;
	private int crossIndex;
	private int crossType;
	public int plane;
	private final int[] currentStats;
	private static int anInt924;
	private long[] ignoreListAsLongs;
	private boolean loadingError;
	private final int[] anIntArray928;
	private int[][] anIntArrayArray929;
	private Sprite aClass30_Sub2_Sub1_Sub1_931;
	private Sprite aClass30_Sub2_Sub1_Sub1_932;
	private int anInt933;
	private int anInt934;
	private int anInt935;
	private int anInt936;
	private int anInt937;
	private int anInt938;
	private final int[] chatTypes;
	private final String[] chatNames;
	private String[] chatMessages;
	public int cycleTimer;
	public WorldController worldController;
	private Sprite[] sideIcons;
	private int menuScreenArea;
	private int menuOffsetX;
	private int menuOffsetY;
	private int menuWidth;
	private int menuHeight;
	private long aLong953;
	private boolean aBoolean954;
	private long[] friendsListAsLongs;
	private String[] clanList = new String[100];
	private int currentSong;
	private static int nodeID = 10;
	static int portOff;
	public static boolean clientData;
	private static boolean isMembers = true;
	private static boolean lowMem;
	private int spriteDrawX;
	private int spriteDrawY;
	private final int[] anIntArray965 = { 0xffff00, 0xff0000, 65280, 65535,
			0xff00ff, 0xffffff };
	private final int[] anIntArray968;
	final Decompressor[] cacheIndices;
	public int variousSettings[];
	private boolean aBoolean972;
	private final int anInt975;
	private final int[] anIntArray976;
	private final int[] anIntArray977;
	private final int[] anIntArray978;
	private final int[] anIntArray979;
	private final int[] anIntArray980;
	private final int[] anIntArray981;
	private final int[] anIntArray982;
	private final String[] aStringArray983;
	private int anInt984;
	private int lastKnownPlane;
	private static int anInt986;
	//public Sprite[] hitMark;
	//public Sprite[] hitIcon;
	private Sprite[] scrollBar;
	public Sprite[] scrollPart;
	private int anInt988;
	private int anInt989;
	private final int[] myAppearanceColors;
	private final boolean aBoolean994;
	private int anInt995;
	public static int[] myHeadAndJaw = new int[2];
	private int anInt996;
	private int anInt997;
	private ObjectManager objectManager;
	private int anInt998;
	private int anInt999;
	private ISAACRandomGen encryption;
	private Sprite mapEdge;
	private Sprite multiOverlay;
	static final int[][] anIntArrayArray1003 = {
		{ 6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983,
			54193 },
			{ 8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153,
				56621, 4783, 1341, 16578, 35003, 25239 },
				{ 25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094,
					10153, 56621, 4783, 1341, 16578, 35003 },
					{ 4626, 11146, 6439, 12, 4758, 10270 },
					{ 4550, 20165, 43678, 16895, 28416, 12231, 947, 60359, 32433 } };
	private String amountOrNameInput;
	private static int anInt1005;
	private int daysSinceLastLogin;
	private int pktSize;
	private int opCode;
	private int anInt1009;
	private int anInt1010;
	private int anInt1011;
	private Deque projectileDeque;
	private int anInt1014;
	private int anInt1015;
	private int anInt1016;
	private boolean aBoolean1017;
	private int walkableInterfaceId;
	private static final int[] levelXPs;
	private int minimapStatus;
	private int anInt1022;
	private int loadingStage;
	private int anInt1026;
	private final int[] anIntArray1030;
	private boolean aBoolean1031;
	public Sprite[] mapFunctions;
	private static int baseX;
	private static int baseY;
	public static int getBaseX() {
		return baseX;
	}
	public static int getBaseY() {
		return baseY;
	}

	private int anInt1036;
	private int anInt1037;
	private int anInt1039;
	private int dialogID;
	private final int[] currentMaxStats;
	private final int[] varbitConfigs;
	private int anInt1046;
	private boolean isMale;
	private int anInt1048;
	static String aString1049;
	private static int anInt1051;
	private final int[] minimapXPosArray;
	private CacheArchive titleStreamLoader;
	private int anInt1054;
	private int drawMultiwayIcon;
	private Deque stillGraphicDeque;
	private final int[] anIntArray1057;
	public final RSInterface aClass9_1059;
	public Background[] mapScenes;
	private static int anInt1061;
	private int currentSound;
	private int friendsListAction;
	private final int[] myAppearance;
	private int mouseInvInterfaceIndex;
	private int lastActiveInvInterface;
	public OnDemandFetcher onDemandFetcher;
	private int currentRegionX;
	private int currentRegionY;
	private int mapFunctionsLoadedAmt;
	private int[] mapFunctionTileX;
	private int[] mapFunctionTileY;
	private Sprite mapDotItem;
	private Sprite mapDotNPC;
	private Sprite mapDotPlayer;
	private Sprite mapDotFriend;
	private Sprite mapDotTeam;
	//private Sprite mapDotClan;
	private boolean loadingMap;
	private String[] friendsList;
	private Stream inStream;
	private int anInt1084;
	private int anInt1085;
	private int activeInterfaceType;
	private int anInt1087;
	private int anInt1088;
	public static int anInt1089;
	private final int[] expectedCRCs;
	private int[] menuActionCmd2;
	private int[] menuActionCmd3;
	public int[] menuActionCmd4;
	private int[] menuActionID;
	private int[] menuActionCmd1;
	private Sprite[] headIcons;
	public Sprite[] bountyHunter;
	private Sprite[] skullIcons;
	private Sprite[] headIconsHint;
	private static int anInt1097;
	private int anInt1098;
	private int anInt1099;
	private int anInt1100;
	private int anInt1101;
	private int anInt1102;
	public static boolean tabAreaAltered;
	private int updateMinutes;
	private RSImageProducer GraphicsBuffer_1107;
	private RSImageProducer titleScreen;
	private static int anInt1117;
	private int membersInt;
	private String promptMessage;
	private Sprite[] compass;
	private RSImageProducer GraphicsBuffer_1125;
	public static Player myPlayer;
	private final String[] atPlayerActions;
	private final boolean[] atPlayerArray;
	private final int[][][] constructRegionData;
	public final int[] tabInterfaceIDs = { -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1 };
	private int cameraOffsetY;
	private int anInt1132;
	private int menuActionRow;
	private static int anInt1134;
	private int spellSelected;
	private int selectedSpellId;
	private int spellUsableOn;
	private String spellTooltip;
	private Sprite[] currentMapFunctionSprites;
	private boolean inTutorialIsland;
	private static int anInt1142;
	private boolean aBoolean1149;
	private Sprite[] crosses;
	private boolean musicEnabled;
	private Background[] aBackgroundArray1152s;
	public static boolean needDrawTabArea;
	private int unreadMessages;
	private static int anInt1155;
	public static boolean fpsOn;
	public static boolean loggedIn;
	private boolean canMute;
	private boolean requestMapReconstruct, requestMapReconstruct2;
	private boolean inCutScene;
	public static int loopCycle;
	public static final String validUserPassChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	private RSImageProducer tabAreaIP;
	private RSImageProducer mapAreaIP;
	private RSImageProducer gameScreenIP;
	private RSImageProducer chatAreaIP;
	private int daysSinceRecovChange;
	private RSSocket socketStream;
	private int minimapZoom;
	private int anInt1171;
	private long aLong1172;
	public String myUsername;
	public String myPassword;
	private static int anInt1175;
	private boolean genericLoadingError;
	private final int[] anIntArray1177 = { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2,
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };
	private int reportAbuseInterfaceID;
	private Deque objectSpawnDeque;
	private int[] anIntArray1180;
	private int[] anIntArray1181;
	private int[] anIntArray1182;
	public Sprite[] cacheSprite;
	private byte[][] terrainData;
	private int anInt1184;
	private int viewRotation;
	private int anInt1186;
	private int anInt1187;
	private static int anInt1188;
	private int invOverlayInterfaceID;
	static Stream stream;
	private int anInt1193;
	private int splitPrivateChat;
	private Background mapBack;
	/* Gameframe update */
	//public Sprite newMapBack;
	private String[] menuActionName;
	private final int[] anIntArray1203;
	static final int[] anIntArray1204 = { 9104, 10275, 7595, 3610, 7975, 8526,
		918, 38802, 24466, 10145, 58654, 5027, 1457, 16565, 34991, 25486 };
	private static boolean flagged;
	private final int[] sound;
	private int anInt1208;
	private int minimapRotation;
	private int anInt1210;
	public static int anInt1211;
	private String promptInput;
	private int anInt1213;
	private int[][][] intGroundArray;
	private long aLong1215;
	public int loginScreenCursorPos;
	public final Sprite[] playerIcons;
	private long aLong1220;
	public static int tabID;
	private int anInt1222;
	public static boolean inputTaken;
	public boolean withdrawingMoneyFromPouch = false;
	private int inputDialogState;
	private static int anInt1226;
	private int nextSong;
	private boolean songChanging;
	private final int[] minimapYPosArray;
	static CollisionDetection[] clippingPlanes;
	public static int anIntArray1232[];
	private int[] mapCoordinates;
	private int[] terrainIndices;
	private int[] objectIndices;
	private int anInt1237;
	private int anInt1238;
	public final int anInt1239 = 100;
	private final int[] soundType;
	private boolean aBoolean1242;
	private int atInventoryLoopCycle;
	private int atInventoryInterface;
	private int atInventoryIndex;
	private int atInventoryInterfaceType;
	private byte[][] objectData;
	private int tradeMode;
	private int anInt1249;
	private final int[] soundDelay;
	private final int[] soundVolume;
	private int isOnTutorialIsland;
	private int anInt1253;
	private int anInt1254;
	private boolean welcomeScreenRaised;
	private boolean showInput;
	private int anInt1257;
	private byte[][][] byteGroundArray;
	private int prevSong;
	private int destX;
	private int destY;
	private Sprite miniMap;
	private int anInt1264;
	private int anInt1265;
	private String loginMessage1;
	private String loginMessage2;
	private int bigRegionX;
	private int bigRegionY;
	public RSFontSystem newSmallFont, newRegularFont, newBoldFont,
	newFancyFont, regularHitFont, bigHitFont;
	public static TextDrawingArea normalFont;
	public static TextDrawingArea boldFont;
	public static TextDrawingArea fancyText;
	public TextDrawingArea smallText;
	private TextDrawingArea smallHit;
	private TextDrawingArea bigHit;
	public TextDrawingArea drawingArea;
	private TextDrawingArea chatTextDrawingArea;
	private int backDialogID;
	private int cameraOffsetX;
	private int anInt1279;
	private int[] bigX;
	private int[] bigY;
	private int itemSelected;
	private int lastItemSelectedSlot;
	private int lastItemSelectedInterface;
	private int selectedItemId;
	private String selectedItemName;
	private int publicChatMode;
	private static int anInt1288;
	public static int anInt1290;
	public int drawCount;
	public int fullscreenInterfaceID;
	public int anInt1044;
	public int anInt1129;
	public int anInt1315;
	public int anInt1500;
	public int anInt1501;
	public int[] fullScreenTextureArray;
	public String Recruits = "";
	public String Corporals = "";
	public String Sergeants = "";
	public String Lieutenants = "";
	public String Captains = "";
	public String Generals = "";
	public int Slots[] = new int[7];
	public String slots[] = new String[7];
	public int slotItemId[] = new int[7];
	public int slotColor[] = new int[7];
	public int slotColorPercent[] = new int[7];
	public boolean slotAborted[] = new boolean[7];
	public int slotUsing = 0;
	public int slotSelected;
	public Sprite Sell;
	public Sprite Buy;
	public Sprite SubmitBuy;
	public Sprite SubmitSell;

	public boolean isRecruit(String name) {
		name = name.toLowerCase();
		if (Recruits.contains(" " + name + ",")) {
			return true;
		}
		if (Recruits.contains(", " + name + "]")) {
			return true;
		}
		if (Recruits.contains("[" + name + ",")) {
			return true;
		}
		if (Recruits.contains("[" + name + "]")) {
			return true;
		}
		return false;
	}

	public boolean isCorporal(String name) {
		name = name.toLowerCase();
		if (Corporals.contains(" " + name + ",")) {
			return true;
		}
		if (Corporals.contains(", " + name + "]")) {
			return true;
		}
		if (Corporals.contains("[" + name + ",")) {
			return true;
		}
		if (Corporals.contains("[" + name + "]")) {
			return true;
		}
		return false;
	}

	public boolean isSergeant(String name) {
		name = name.toLowerCase();
		if (Sergeants.contains(" " + name + ",")) {
			return true;
		}
		if (Sergeants.contains(", " + name + "]")) {
			return true;
		}
		if (Sergeants.contains("[" + name + ",")) {
			return true;
		}
		if (Sergeants.contains("[" + name + "]")) {
			return true;
		}
		return false;
	}

	public boolean isLieutenant(String name) {
		name = name.toLowerCase();
		if (Lieutenants.contains(" " + name + ",")) {
			return true;
		}
		if (Lieutenants.contains(", " + name + "]")) {
			return true;
		}
		if (Lieutenants.contains("[" + name + ",")) {
			return true;
		}
		if (Lieutenants.contains("[" + name + "]")) {
			return true;
		}
		return false;
	}

	public boolean isCaptain(String name) {
		name = name.toLowerCase();
		if (Captains.contains(" " + name + ",")) {
			return true;
		}
		if (Captains.contains(", " + name + "]")) {
			return true;
		}
		if (Captains.contains("[" + name + ",")) {
			return true;
		}
		if (Captains.contains("[" + name + "]")) {
			return true;
		}
		return false;
	}

	public boolean isGeneral(String name) {
		name = name.toLowerCase();
		if (Generals.contains(" " + name + ",")) {
			return true;
		}
		if (Generals.contains(", " + name + "]")) {
			return true;
		}
		if (Generals.contains("[" + name + ",")) {
			return true;
		}
		if (Generals.contains("[" + name + "]")) {
			return true;
		}
		return false;
	}

	public void resetAllImageProducers() {
		if (super.fullGameScreen != null) {
			return;
		}
		chatAreaIP = null;
		mapAreaIP = null;
		tabAreaIP = null;
		gameScreenIP = null;
		GraphicsBuffer_1125 = null;
		GraphicsBuffer_1107 = null;
		titleScreen = null;
		super.fullGameScreen = new RSImageProducer(765, 503, getGameComponent());
		welcomeScreenRaised = true;
	}

	public void drawGrandExchange() {
		if (openInterfaceID != 24500 && openInterfaceID != 54700
				&& openInterfaceID != 53700) {
			return;
		}
		if (openInterfaceID == 24500) {
			for (int i = 1; i < Slots.length; i++) {
				if (Slots[i] == 0) {
					drawUpdate(i, "Regular");
				}
				if (Slots[i] == 1 && slots[i] == "Sell") {
					drawUpdate(i, "Submit Sell");
				}
				if (Slots[i] == 1 && slots[i] == "Buy") {
					drawUpdate(i, "Submit Buy");
				}
				if (Slots[i] == 2 && slots[i] == "Sell") {
					drawUpdate(i, "Sell");
				}
				if (Slots[i] == 2 && slots[i] == "Buy") {
					drawUpdate(i, "Buy");
				}
				if (Slots[i] == 3 && slots[i] == "Sell") {
					drawUpdate(i, "Finished Selling");
				}
				if (Slots[i] == 3 && slots[i] == "Buy") {
					drawUpdate(i, "Finished Buying");
				}
			}
		}
		int x = 0;
		int y = 0;
		x = (clientSize == 0) ? 71 : (71 + (clientWidth / 2 - 256));
		y = (clientSize == 0) ? 303 : (clientHeight / 2 + 136);
		if (openInterfaceID == 54700) {
			SpriteCache.fetchIfNeeded(647);
			SpriteCache.fetchIfNeeded(648);
			SpriteCache.fetchIfNeeded(649);
			SpriteCache.fetchIfNeeded(651);
			per4 = SpriteCache.spriteCache[647];
			per5 = SpriteCache.spriteCache[648];
			per6 = SpriteCache.spriteCache[649];
			abort2 = SpriteCache.spriteCache[651];
			if (slotColorPercent[slotSelected] == 100
					|| slotAborted[slotSelected]) {
				RSInterface.interfaceCache[54800].tooltip = "[GE]";
			} else {
				RSInterface.interfaceCache[54800].tooltip = "Abort offer";
			}
			if (slotSelected <= 6) {
				if (!slotAborted[slotSelected]) {
					for (int k9 = 1; k9 < slotColorPercent[slotSelected]; k9++) {
						if (slotColorPercent[slotSelected] > 0) {
							if (k9 == 1) {
								if (per4 != null)
									per4.drawSprite(x, y);
								x += 3;
							} else if (k9 == 99) {
								if (per6 != null)
									per6.drawSprite(x, y);
								x += 4;
							} else {
								if (per5 != null)
									per5.drawSprite(x, y);
								x += 3;
							}
						}
					}
				} else {
					if (abort2 != null)
						abort2.drawSprite(x, y);
				}
			}
		}
		x = (clientSize == 0) ? 71 : (71 + (clientWidth / 2 - 256));
		y = (clientSize == 0) ? 303 : (clientHeight / 2 + 136);
		if (openInterfaceID == 53700) {
			SpriteCache.fetchIfNeeded(647);
			SpriteCache.fetchIfNeeded(648);
			SpriteCache.fetchIfNeeded(649);
			per4 = SpriteCache.spriteCache[647];
			per5 = SpriteCache.spriteCache[648];
			per6 = SpriteCache.spriteCache[649];
			if (slotColorPercent[slotSelected] == 100
					|| slotAborted[slotSelected]) {
				RSInterface.interfaceCache[53800].tooltip = "[GE]";
			} else {
				RSInterface.interfaceCache[53800].tooltip = "Abort offer";
			}
			if (slotSelected <= 6) {
				if (!slotAborted[slotSelected]) {
					for (int k9 = 1; k9 < slotColorPercent[slotSelected]; k9++) {
						if (slotColorPercent[slotSelected] > 0) {
							if (k9 == 1) {
								if (per4 != null)
									per4.drawSprite(x, y);
								x += 3;
							} else if (k9 == 99) {
								if (per6 != null)
									per6.drawSprite(x, y);
								x += 4;
							} else {
								if (per5 != null)
									per5.drawSprite(x, y);
								x += 3;
							}
						}
					}
				} else {
					if (abort2 != null)
						abort2.drawSprite(x, y);
				}
			}
		}
	}

	public void drawUpdate(int id, String type) {
		int x = 0;
		int y = 0;
		int x2 = 0;
		int y2 = 0;
		int x3 = 0;
		int y3 = 0;
		boolean fixed = (clientSize == 0);
		switch (id) {
		case 1:
			x = fixed ? 30 : (clientWidth / 2 - 226);
			x2 = fixed ? 80 : (clientWidth / 2 - 226 + 50);
			x3 = fixed ? 40 : (clientWidth / 2 - 226 + 10);
			y = fixed ? 74 : (clientHeight / 2 - 93);
			y2 = fixed ? 136 : (clientHeight / 2 - 93 + 62);
			y3 = fixed ? 115 : (clientHeight / 2 - 93 + 41);
			break;
		case 2:
			x = fixed ? 186 : (clientWidth / 2 - 70);
			x2 = fixed ? 80 + 156 : (clientWidth / 2 - 70 + 50);
			x3 = fixed ? 40 + 156 : (clientWidth / 2 - 70 + 10);
			y = fixed ? 74 : (clientHeight / 2 - 93);
			y2 = fixed ? 136 : (clientHeight / 2 - 93 + 62);
			y3 = fixed ? 115 : (clientHeight / 2 - 93 + 41);
			break;
		case 3:
			x = fixed ? 342 : (clientWidth / 2 + 86);
			x2 = fixed ? 80 + 156 + 156 : (clientWidth / 2 + 86 + 50);
			x3 = fixed ? 40 + 156 + 156 : (clientWidth / 2 + 86 + 10);
			y = fixed ? 74 : (clientHeight / 2 - 93);
			y2 = fixed ? 136 : (clientHeight / 2 - 93 + 62);
			y3 = fixed ? 115 : (clientHeight / 2 - 93 + 41);
			break;
		case 4:
			x = fixed ? 30 : (clientWidth / 2 - 226);
			x2 = fixed ? 80 : (clientWidth / 2 - 226 + 50);
			x3 = fixed ? 40 : (clientWidth / 2 - 226 + 10);
			y = fixed ? 194 : (clientHeight / 2 + 27);
			y2 = fixed ? 256 : (clientHeight / 2 + 27 + 62);
			y3 = fixed ? 235 : (clientHeight / 2 + 27 + 41);
			break;
		case 5:
			x = fixed ? 186 : (clientWidth / 2 - 70);
			x2 = fixed ? 80 + 156 : (clientWidth / 2 - 70 + 50);
			x3 = fixed ? 40 + 156 : (clientWidth / 2 - 70 + 10);
			y = fixed ? 194 : (clientHeight / 2 + 27);
			y2 = fixed ? 256 : (clientHeight / 2 + 27 + 62);
			y3 = fixed ? 235 : (clientHeight / 2 + 27 + 41);
			break;
		case 6:
			x = fixed ? 342 : (clientWidth / 2 + 86);
			x2 = fixed ? 80 + 156 + 156 : (clientWidth / 2 + 86 + 50);
			x3 = fixed ? 40 + 156 + 156 : (clientWidth / 2 + 86 + 10);
			y = fixed ? 194 : (clientHeight / 2 + 27);
			y2 = fixed ? 256 : (clientHeight / 2 + 27 + 62);
			y3 = fixed ? 235 : (clientHeight / 2 + 27 + 41);
			break;
		}
		x -= 2;
		x2 -= 2;
		x3 -= 2;
		int minus = 20;
		if (type == "Sell") {
			if (super.mouseX >= x && super.mouseX <= x + 140
					&& super.mouseY >= y && super.mouseY <= y + 110
					&& !menuOpen) {
				SpriteCache.fetchIfNeeded(655);
				SellHover = SpriteCache.spriteCache[655];
				if (SellHover != null)
					SellHover.drawSprite(x, y);
			} else {
				SpriteCache.spriteCache[30].drawSprite(x, y);
			}
			setGrandExchange(id, false);
			if (slotAborted[id] || slotColorPercent[id] == 100) {
				changeSet(id, true, false);
			} else {
				changeSet(id, true, true);
			}
			drawPercentage(id);
			smallText.method592(0xCC9900, x2,
					RSInterface.interfaceCache[32000 + id].message, y2 - minus,
					true);
			smallText.method592(0xBDBB5B, x2,
					RSInterface.interfaceCache[33000 + id].message, y2, true);
			smallText.method592(0xFFFF00, x3,
					RSInterface.interfaceCache[33100 + id].message, y3, true);
			setHovers(id, false);
		} else if (type == "Buy") {
			if (super.mouseX >= x && super.mouseX <= x + 140
					&& super.mouseY >= y && super.mouseY <= y + 110
					&& !menuOpen) {
				SpriteCache.fetchIfNeeded(654);
				BuyHover = SpriteCache.spriteCache[654];
				if (BuyHover != null)
					BuyHover.drawSprite(x, y);
			} else {
				SpriteCache.spriteCache[29].drawSprite(x, y);
			}
			setGrandExchange(id, false);
			if (slotAborted[id] || slotColorPercent[id] == 100) {
				changeSet(id, true, false);
			} else {
				changeSet(id, true, true);
			}
			drawPercentage(id);
			smallText.method592(0xCC9900, x2,
					RSInterface.interfaceCache[32000 + id].message, y2 - minus,
					true);
			smallText.method592(0xBDBB5B, x2,
					RSInterface.interfaceCache[33000 + id].message, y2, true);
			smallText.method592(0xFFFF00, x3,
					RSInterface.interfaceCache[33100 + id].message, y3, true);
			setHovers(id, false);
		} else if (type == "Submit Buy") {
			if (super.mouseX >= x && super.mouseX <= x + 140
					&& super.mouseY >= y && super.mouseY <= y + 110
					&& !menuOpen) {
				SpriteCache.fetchIfNeeded(656);
				buySubmitHover = SpriteCache.spriteCache[656];
				if (buySubmitHover != null)
					buySubmitHover.drawSprite(x, y);
			} else {
				SpriteCache.spriteCache[27].drawSprite(x, y);
			}
			setGrandExchange(id, false);
			changeSet(id, false, false);
			smallText.method592(0xCC9900, x2,
					RSInterface.interfaceCache[32000 + id].message, y2 - minus,
					true);
			smallText.method592(0xBDBB5B, x2,
					RSInterface.interfaceCache[33000 + id].message, y2, true);
			smallText.method592(0xFFFF00, x3,
					RSInterface.interfaceCache[33100 + id].message, y3, true);
			setHovers(id, false);
		} else if (type == "Submit Sell") {
			if (super.mouseX >= x && super.mouseX <= x + 140
					&& super.mouseY >= y && super.mouseY <= y + 110
					&& !menuOpen) {
				SpriteCache.fetchIfNeeded(657);
				sellSubmitHover = SpriteCache.spriteCache[657];
				if (sellSubmitHover != null)
					sellSubmitHover.drawSprite(x, y);
			} else {
				SpriteCache.spriteCache[28].drawSprite(x, y);
			}
			setGrandExchange(id, false);
			changeSet(id, false, false);
			smallText.method592(0xCC9900, x2,
					RSInterface.interfaceCache[32000 + id].message, y2 - minus,
					true);
			smallText.method592(0xBDBB5B, x2,
					RSInterface.interfaceCache[33000 + id].message, y2, true);
			smallText.method592(0xFFFF00, x3,
					RSInterface.interfaceCache[33100 + id].message, y3, true);
			setHovers(id, false);
		} else if (type == "Regular") {
			setGrandExchange(id, true);
			setHovers(id, true);
		} else if (type == "Finished Selling") {
			if (super.mouseX >= x && super.mouseX <= x + 140
					&& super.mouseY >= y && super.mouseY <= y + 110
					&& !menuOpen) {
				SpriteCache.fetchIfNeeded(655);
				SellHover = SpriteCache.spriteCache[655];
				if (SellHover != null)
					SellHover.drawSprite(x, y);
			} else {
				SpriteCache.spriteCache[30].drawSprite(x, y);
			}
			setGrandExchange(id, false);
			changeSet(id, true, false);
			drawPercentage(id);
			smallText.method592(0xCC9900, x2,
					RSInterface.interfaceCache[32000 + id].message, y2 - minus,
					true);
			smallText.method592(0xBDBB5B, x2,
					RSInterface.interfaceCache[33000 + id].message, y2, true);
			smallText.method592(0xFFFF00, x3,
					RSInterface.interfaceCache[33100 + id].message, y3, true);
			setHovers(id, false);
		} else if (type == "Finished Buying") {
			if (super.mouseX >= x && super.mouseX <= x + 140
					&& super.mouseY >= y && super.mouseY <= y + 110
					&& !menuOpen) {
				SpriteCache.fetchIfNeeded(656);
				BuyHover = SpriteCache.spriteCache[656];
				if (BuyHover != null)
					BuyHover.drawSprite(x, y);
			} else {
				SpriteCache.spriteCache[29].drawSprite(x, y);
			}
			setGrandExchange(id, false);
			changeSet(id, true, false);
			drawPercentage(id);
			smallText.method592(0xCC9900, x2,
					RSInterface.interfaceCache[32000 + id].message, y2 - minus,
					true);
			smallText.method592(0xBDBB5B, x2,
					RSInterface.interfaceCache[33000 + id].message, y2, true);
			smallText.method592(0xFFFF00, x3,
					RSInterface.interfaceCache[33100 + id].message, y3, true);
			setHovers(id, false);
		}
	}

	public Sprite per0;
	public Sprite per1;
	public Sprite per2;
	public Sprite per3;
	public Sprite per4;
	public Sprite per5;
	public Sprite per6;
	public Sprite abort1;
	public Sprite abort2;
	public Sprite SellHover;
	public Sprite BuyHover;
	public Sprite sellSubmitHover;
	public Sprite buySubmitHover;

	public void drawPercentage(int id) {
		SpriteCache.fetchIfNeeded(643);
		SpriteCache.fetchIfNeeded(644);
		SpriteCache.fetchIfNeeded(645);
		SpriteCache.fetchIfNeeded(646);
		SpriteCache.fetchIfNeeded(650);
		per0 = SpriteCache.spriteCache[643];
		per1 = SpriteCache.spriteCache[644];
		per2 = SpriteCache.spriteCache[645];
		per3 = SpriteCache.spriteCache[646];
		abort1 = SpriteCache.spriteCache[650];
		int x = 0;
		int y = 0;
		boolean fixed = (clientSize == 0);
		switch (id) {
		case 1:
			x = fixed ? 30 + 8 : (clientWidth / 2 - 226 + 8);
			y = fixed ? 74 + 81 : (clientHeight / 2 - 93 + 81);
			break;
		case 2:
			x = fixed ? 186 + 8 : (clientWidth / 2 - 70 + 8);
			y = fixed ? 74 + 81 : (clientHeight / 2 - 93 + 81);
			break;
		case 3:
			x = fixed ? 342 + 8 : (clientWidth / 2 + 86 + 8);
			y = fixed ? 74 + 81 : (clientHeight / 2 - 93 + 81);
			break;
		case 4:
			x = fixed ? 30 + 8 : (clientWidth / 2 - 226 + 8);
			y = fixed ? 194 + 81 : (clientHeight / 2 + 27 + 81);
			break;
		case 5:
			x = fixed ? 186 + 8 : (clientWidth / 2 - 70 + 8);
			y = fixed ? 194 + 81 : (clientHeight / 2 + 27 + 81);
			break;
		case 6:
			x = fixed ? 342 + 8 : (clientWidth / 2 + 86 + 8);
			y = fixed ? 194 + 81 : (clientHeight / 2 + 27 + 81);
			break;
		}
		x -= 2;
		if (slotColorPercent[id] > 100) {
			slotColorPercent[id] = 100;
		}
		int s = 0;
		if (!slotAborted[id]) {
			for (int k9 = 1; k9 < slotColorPercent[id]; k9++) {
				if (slotColorPercent[id] > 0) {
					if (k9 == 1) {
						if (per0 != null)
							per0.drawSprite(x, y);
						x += 2;
					} else if (k9 == 2) {
						if (per1 != null)
							per1.drawSprite(x, y);
						x += 2;
					} else if (k9 >= 6 && k9 <= 14) {
						if (per3 != null)
							per3.drawSprite(x, y);
						x += 1;
					} else if (k9 >= 56 && k9 <= 65) {
						if (per3 != null)
							per3.drawSprite(x, y);
						x += 1;
					} else if (k9 >= 76 && k9 <= 82) {
						if (per3 != null)
							per3.drawSprite(x, y);
						x += 1;
					} else {
						if (s == 0) {
							if (per2 != null)
								per2.drawSprite(x, y);
							x += 2;
							s += 1;
						} else if (s == 1) {
							if (per3 != null)
								per3.drawSprite(x, y);
							x += 1;
							s += 1;
						} else if (s == 2) {
							if (per3 != null)
								per3.drawSprite(x, y);
							x += 1;
							s = 0;
						} else if (s == 4) {
							if (per3 != null)
								per3.drawSprite(x, y);
							x += 1;
							s = 0;
						}
					}
				}
			}
		} else {
			if (abort1 != null)
				abort1.drawSprite(x, y);
		}
	}

	public void setGrandExchange(int id, boolean on) {
		switch (id) {
		case 1:
			if (on) {
				RSInterface.interfaceCache[24505].tooltip = "Buy";
				RSInterface.interfaceCache[24511].tooltip = "Sell";
				changeSet(id, false, false);
			} else {
				RSInterface.interfaceCache[24505].tooltip = "[GE]";
				RSInterface.interfaceCache[24511].tooltip = "[GE]";
			}
			break;
		case 2:
			if (on) {
				RSInterface.interfaceCache[24523].tooltip = "Buy";
				RSInterface.interfaceCache[24526].tooltip = "Sell";
				changeSet(id, false, false);
			} else {
				RSInterface.interfaceCache[24523].tooltip = "[GE]";
				RSInterface.interfaceCache[24526].tooltip = "[GE]";
			}
			break;
		case 3:
			if (on) {
				RSInterface.interfaceCache[24514].tooltip = "Buy";
				RSInterface.interfaceCache[24529].tooltip = "Sell";
				changeSet(id, false, false);
			} else {
				RSInterface.interfaceCache[24514].tooltip = "[GE]";
				RSInterface.interfaceCache[24529].tooltip = "[GE]";
			}
			break;
		case 4:
			if (on) {
				RSInterface.interfaceCache[24508].tooltip = "Buy";
				RSInterface.interfaceCache[24532].tooltip = "Sell";
				changeSet(id, false, false);
			} else {
				RSInterface.interfaceCache[24508].tooltip = "[GE]";
				RSInterface.interfaceCache[24532].tooltip = "[GE]";
			}
			break;
		case 5:
			if (on) {
				RSInterface.interfaceCache[24517].tooltip = "Buy";
				RSInterface.interfaceCache[24535].tooltip = "Sell";
				changeSet(id, false, false);
			} else {
				RSInterface.interfaceCache[24517].tooltip = "[GE]";
				RSInterface.interfaceCache[24535].tooltip = "[GE]";
			}
			break;
		case 6:
			if (on) {
				RSInterface.interfaceCache[24520].tooltip = "Buy";
				RSInterface.interfaceCache[24538].tooltip = "Sell";
				changeSet(id, false, false);
			} else {
				RSInterface.interfaceCache[24520].tooltip = "[GE]";
				RSInterface.interfaceCache[24538].tooltip = "[GE]";
			}
			break;
		}
	}

	public void changeSet(int id, boolean view, boolean abort) {
		switch (id) {
		case 1:
			if (view) {
				RSInterface.interfaceCache[24543].tooltip = "View offer";
			} else {
				RSInterface.interfaceCache[24543].tooltip = "[GE]";
			}
			if (abort) {
				RSInterface.interfaceCache[24541].tooltip = "Abort offer";
			} else {
				RSInterface.interfaceCache[24541].tooltip = "[GE]";
			}
			break;
		case 2:
			if (view) {
				RSInterface.interfaceCache[24547].tooltip = "View offer";
			} else {
				RSInterface.interfaceCache[24547].tooltip = "[GE]";
			}
			if (abort) {
				RSInterface.interfaceCache[24545].tooltip = "Abort offer";
			} else {
				RSInterface.interfaceCache[24545].tooltip = "[GE]";
			}
			break;
		case 3:
			if (view) {
				RSInterface.interfaceCache[24551].tooltip = "View offer";
			} else {
				RSInterface.interfaceCache[24551].tooltip = "[GE]";
			}
			if (abort) {
				RSInterface.interfaceCache[24549].tooltip = "Abort offer";
			} else {
				RSInterface.interfaceCache[24549].tooltip = "[GE]";
			}
			break;
		case 4:
			if (view) {
				RSInterface.interfaceCache[24555].tooltip = "View offer";
			} else {
				RSInterface.interfaceCache[24555].tooltip = "[GE]";
			}
			if (abort) {
				RSInterface.interfaceCache[24553].tooltip = "Abort offer";
			} else {
				RSInterface.interfaceCache[24553].tooltip = "[GE]";
			}
			break;
		case 5:
			if (view) {
				RSInterface.interfaceCache[24559].tooltip = "View offer";
			} else {
				RSInterface.interfaceCache[24559].tooltip = "[GE]";
			}
			if (abort) {
				RSInterface.interfaceCache[24557].tooltip = "Abort offer";
			} else {
				RSInterface.interfaceCache[24557].tooltip = "[GE]";
			}
			break;
		case 6:
			if (view) {
				RSInterface.interfaceCache[24563].tooltip = "View offer";
			} else {
				RSInterface.interfaceCache[24563].tooltip = "[GE]";
			}
			if (abort) {
				RSInterface.interfaceCache[24561].tooltip = "Abort offer";
			} else {
				RSInterface.interfaceCache[24561].tooltip = "[GE]";
			}
			break;
		}
	}

	public void setHovers(int id, boolean on) {
		switch (id) {
		case 1:
			if (!on) {
			} else {
			}
			break;
		case 2:
			if (!on) {
			} else {
			}
			break;
		case 3:
			if (!on) {
			} else {
			}
			break;
		case 4:
			if (!on) {
			} else {
			}
			break;
		case 5:
			if (!on) {
			} else {
			}
			break;
		case 6:
			if (!on) {
			} else {
			}
			break;
		}
	}

	public void tabToReplyPm() {
		String name = null;
		for (int j = 0; j < 100; j++)
			if (chatMessages[j] != null) {
				int chatType = chatTypes[j];
				if (chatType == 3 || chatType == 7) {
					name = chatNames[j];
					break;
				}
			}
		if (name != null) {
			if(name.startsWith("@cr0@") || name.startsWith("@cr1@") || name.startsWith("@cr2@") || name.startsWith("@cr3@") || name.startsWith("@cr4@") || name.startsWith("@cr5@") || name.startsWith("@cr6@") || name.startsWith("@cr7@") || name.startsWith("@cr8@") || name.startsWith("@cr9@") || name.startsWith("@cr10@") || name.startsWith("@cr11@"))
				name = name.substring(5);
		}
		if (name == null) {
			pushMessage("You have not recieved any messages.", 0, "");
			return;
		}
		try {
			if (name != null) {
				long namel = TextClass.longForName(name.trim());
				int node = -1;
				for (int count = 0; count < friendsCount; count++) {
					if (friendsListAsLongs[count] != namel)
						continue;
					node = count;
					break;
				}
				if (node != -1 && friendsNodeIDs[node] > 0) {
					inputTaken = true;
					inputDialogState = 0;
					showInput = true;
					promptInput = "";
					friendsListAction = 3;
					aLong953 = friendsListAsLongs[node];
					promptMessage = "Enter message to send to "
							+ friendsList[node];
				} else {
					if(node == -1)
						pushMessage(capitalize(name) + " is not in your friendlist.", 0, "");
					else if(!(friendsNodeIDs[node] > 0))
						pushMessage(capitalize(name) + " is not online.", 0, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getClientWidth() {
		return clientWidth;
	}

	public int centerX = clientWidth / 2;
	public int centerY = clientHeight / 2;
	public int lastW = 765;
	public int lastH = 503;
	public Sprite newbg3;

	public void checkClientSize(boolean ignore) {
		if (!ignore && lastW == getGameComponent().getWidth() || lastH == getGameComponent().getHeight()) {
			//System.out.println("same so stopped");
			return;
		} else if (ignore || lastW != getGameComponent().getWidth() || lastH != getGameComponent().getHeight()) {
			clientWidth2 = getGameComponent().getWidth();
			clientHeight2 = getGameComponent().getHeight() + 23;
			lastW = clientWidth2;
			lastH = clientHeight2;
			centerX = clientWidth2 / 2;
			centerY = clientHeight2 / 2;
			newbg3 = new Sprite("Clientimages/login/1",  clientWidth2, clientHeight2, true);
		}
	}

	public void refreshClientScreen() {
		titleScreen = new RSImageProducer(clientWidth, clientHeight, getGameComponent());
		DrawingArea.resetImage();
	}

	public int getClientHeight() {
		return clientHeight;
	}

	public static Client getClient() {
		return instance;
	}

	public void launchURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (osName.startsWith("Windows"))
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);
			else {
				String[] browsers = { "firefox", "opera", "konqueror",
						"epiphany", "mozilla", "netscape", "safari" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime()
							.exec(new String[] { "which", browsers[count] })
							.waitFor() == 0)
						browser = browsers[count];
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else
					Runtime.getRuntime().exec(new String[] { browser, url });
			}
		} catch (Exception e) {
			pushMessage("Failed to open URL.", 0, "");
		}
	}

	static {
		levelXPs = new int[99];
		int i = 0;
		for (int j = 0; j < 99; j++) {
			int l = j + 1;
			int i1 = (int) ((double) l + 300D * Math.pow(2D, (double) l / 7D));
			i += i1;
			levelXPs[j] = i / 4;
		}
		anIntArray1232 = new int[32];
		i = 2;
		for (int k = 0; k < 32; k++) {
			anIntArray1232[k] = i - 1;
			i += i;
		}
	}

	//public boolean[] worldMap = new boolean[2];

	public void loadOrbs() {
		drawAdv();
		//drawWorldMap();
	}

	public void drawAdv() {
		ADVISOR[!advisorHover ? 0 : 1].drawSprite(207, 0);
		if (super.clickMode2 == 1 && super.mouseX > 724 && super.mouseX < 743
				&& super.mouseY > 1 && super.mouseY < 20) {
			ADVISOR[2].drawSprite(207, 0);
		}
	}

	public void drawShortcutIcon() {
		if (clientSize == 0) {
			(hoverShortcutIcon() ? shortcutIconHover: shortcutIcon).drawSprite(4, 120);
		}
	}

	public boolean hoverShortcutIcon() {
		return super.mouseX >= 522 && super.mouseX <= 558 && super.mouseY >= 124 && super.mouseY < 161;
	}

	public boolean mouseInRegion2(int x1, int x2, int y1, int y2) {
		if (super.mouseX >= x1 && super.mouseX <= x2 && super.mouseY >= y1
				&& super.mouseY <= y2) {
			return true;
		}
		return false;
	}

	public boolean clickInRegion2(int x1, int x2, int y1, int y2) {
		if (super.saveClickX >= x1 && super.saveClickX <= x2
				&& super.saveClickY >= y1 && super.saveClickY <= y2) {
			return true;
		}
		return false;
	}

	public boolean mouseInRegion(int x1, int y1, int x2, int y2) {
		if (super.mouseX >= x1 && super.mouseX <= x2 && super.mouseY >= y1
				&& super.mouseY <= y2)
			return true;
		return false;
	}

	public boolean clickInRegion(int x1, int y1, int x2, int y2) {
		if (super.saveClickX >= x1 && super.saveClickX <= x2
				&& super.saveClickY >= y1 && super.saveClickY <= y2)
			return true;
		return false;
	}

	public boolean logHover = false;
	public boolean advisorHover = false;
	public Sprite[] ADVISOR = new Sprite[5];
	public Sprite[] orbs = new Sprite[20];
	//public Sprite[] LOGOUT = new Sprite[5];

	private static String intToKOrMilLongName(int i) {
		String s = String.valueOf(i);
		for (int k = s.length() - 3; k > 0; k -= 3)
			s = s.substring(0, k) + "," + s.substring(k);
		if (s.length() > 8)
			s = "@gre@" + s.substring(0, s.length() - 8) + " million @whi@("
					+ s + ")";
		else if (s.length() > 4)
			s = "@cya@" + s.substring(0, s.length() - 4) + "K @whi@(" + s + ")";
		return " " + s;
	}

	public void hitmarkDraw(Entity e, int hitLength, int type, int icon,
			int damage, int soak, int move, int opacity, int mask) {
		int drawPos = 0;
		if (mask == 0) {
			e.hitMarkPos[0] = spriteDrawY + move;
			drawPos = e.hitMarkPos[0];
		}
		if (mask != 0) {
			e.hitMarkPos[mask] = e.hitMarkPos[0] + (19 * mask);
			drawPos = e.hitMarkPos[mask];
		}
		if (damage > 0) {
			Sprite end1 = null, middle = null, end2 = null;
			int x = 0;
			if(!getOption("constitution")) {
				damage = (damage / 10);
				if(damage == 0)
					damage = 1;
			}
			switch (hitLength) {
			/* Trial and error shit, terrible hardcoding :( */
			case 1:
				x = 8;
				break;
			case 2:
				x = 4;
				break;
			case 3:
				x = 1;
				break;
			}
			if (soak > 0)
				x -= 16;
			end1 = SpriteCache.spriteCache[81 + (type * 3)];
			middle = SpriteCache.spriteCache[81 + (type * 3) + 1];
			end2 = SpriteCache.spriteCache[81 + (type * 3) + 2];
			if (icon != 255) {
				SpriteCache.spriteCache[114 + icon].drawSprite3(spriteDrawX
						- 34 + x, drawPos - 14, opacity);
			}
			end1.drawSprite3(spriteDrawX - 12 + x, drawPos - 12, opacity);
			x += 4;
			for (int i = 0; i < hitLength * 2; i++) {
				middle.drawSprite3(spriteDrawX - 12 + x, drawPos - 12, opacity);
				x += 4;
			}
			end2.drawSprite3(spriteDrawX - 12 + x, drawPos - 12, opacity);
			(type == 1 ? bigHit : smallHit).drawOpacityText(0xffffff,
					String.valueOf(damage), drawPos + (type == 1 ? 2 : 32),
					spriteDrawX + 4 + (soak > 0 ? -16 : 0), opacity);
			if (soak > 0)
				drawSoak(soak, opacity, drawPos, x);
		} else {
			SpriteCache.fetchIfNeeded(673);
			Sprite block = SpriteCache.spriteCache[673];
			if (block != null)
				block.drawSprite3(spriteDrawX - 12, drawPos - 14, opacity);
		}
	}


	public Sprite[] hitMark;
	public Sprite[] hitIcon;
	//private Sprite block;

	public int dayTimeShades() {
		Calendar calendar = new GregorianCalendar();
		int min = calendar.get(Calendar.MINUTE);
		int sec = calendar.get(Calendar.SECOND);
		long time = min + (sec / 60);
		if (time > 30 && time <= 57.5)
			return 0x000000;
		else if (time > 57.5 && time < 61)
			return 0x669999;
		else if (time > 0 && time <= 2.5)
			return 0x99CCCC;
		else if (time > 2.5 && time <= 5)
			return 0x66CCCC;
		else if (time > 5 && time <= 7.5)
			return 0x99FFFF;
		else if (time > 7.5 && time <= 10)
			return 0xCCFFCC;
		else if (time > 10 && time <= 17.5)
			return 0xFFFF66;
		else if (time > 17.5 && time <= 25)
			return 0xFFCC33;
		else if (time > 25 && time <= 27.5)
			return 0xFF9900;
		else if (time > 27.5 && time <= 30)
			return 0xCC3300;
		return 0xCC3300;
	}

	public void drawSoak(int damage, int opacity, int drawPos, int x) {
		x -= 12;
		int soakLength = String.valueOf(damage).length();
		SpriteCache.spriteCache[114 + 5].drawSprite3(spriteDrawX + x,
				drawPos - 12, opacity);
		x += 20;
		SpriteCache.spriteCache[81 + 30].drawSprite3(spriteDrawX + x,
				drawPos - 12, opacity);
		x += 4;
		for (int i = 0; i < soakLength * 2; i++) {
			SpriteCache.spriteCache[81 + 31].drawSprite3(spriteDrawX + x,
					drawPos - 12, opacity);
			x += 4;
		}
		SpriteCache.spriteCache[81 + 32].drawSprite3(spriteDrawX + x,
				drawPos - 10, opacity);
		smallHit.drawOpacityText(0xffffff, String.valueOf(damage),
				drawPos + 32, spriteDrawX - 8 + x + (soakLength == 1 ? 5 : 0),
				opacity);
	}
	/**
	 * Console
	 */
	public static int consoleAlpha = 0;
	public static boolean consoleOpen;
	public String consoleInput;
	public final String[] consoleMessages;

	public void drawConsole() {
		int height = 334;
		if (consoleOpen) {
			consoleAlpha += consoleAlpha < 100 ? 10 : 0;
			TextDrawingArea.drawAlphaFilledPixels(0, 0, clientWidth, height, 5320850, consoleAlpha < 101 ? consoleAlpha : 100);
			DrawingArea.drawPixels(1, height - 19, 0, 16777215, clientWidth);
			newBoldFont.drawBasicString("-->", 3, height - 6, 16777215, 0);
			if (loopCycle % 20 < 10) {
				newBoldFont.drawBasicString(consoleInput + "|", 32, height - 6, 16777215, 0);
			} else {
				//smallText.method380(consoleInput, 38, height - 6, 0);
				newBoldFont.drawBasicString(consoleInput, 32, height - 6, 16777215, 0);
			}
		} else {
			consoleAlpha -= consoleAlpha > 0 ? 10 : 0;
			consoleAlpha = consoleAlpha < 0 ? 0 : consoleAlpha;
			if (consoleAlpha > 0) {
				TextDrawingArea.drawAlphaFilledPixels(0, 0, clientWidth, height,
						5320850, consoleAlpha);
			}
		}
	}

	public void drawConsoleArea() {
		if (consoleOpen) {
			for (int index = 0, positionY = 326 - 26; index < 17; index++, positionY -= 18) {
				if (consoleMessages[index] != null) {
					newSmallFont.drawBasicString(consoleMessages[index], 9, positionY, 16777215, 0);
				}
			}
			if (consoleMessages[1].length() <= 0) {
				sendConsoleMessage("Type 'commands' for a list of commands, 'cls' to clear the console, and 'info' for more",false);
				sendConsoleMessage("information. Use the 'close'command or hit the '~' button to close the console.", false);
			}
		}
	}

	public void sendConsoleMessage(String s, boolean response) {
		if (backDialogID == -1)
			inputTaken = true;
		for (int index = 16; index > 0; index--) {
			consoleMessages[index] = consoleMessages[index - 1];
		}
		if (response) {
			consoleMessages[0] = "--> " + s;
		} else {
			consoleMessages[0] = s;
		}
	}

	public void getConsoleCommands() {
		sendConsoleMessage("'cls' - clear the console.", false);
		sendConsoleMessage("'data' - toggles data display (fps, memory usage, etc).", false);
		sendConsoleMessage("'teleto #player' - Teleports to Player player.", false);
		sendConsoleMessage("'teletome #player' - Teleports Player player to you.", false);
		sendConsoleMessage("'globalmsg #message' - Sends a global message to everyone.", false);
		if (myRights == 2 || myRights == 3 || myRights == 4) {
			sendConsoleMessage("'config #' - displays the configs for specified child id.", false);
			sendConsoleMessage("'unpack_interfaces' - unpacks all interfaces.", false);
			sendConsoleMessage("'pos' - prints out your position values.", false);
			sendConsoleMessage("'reset' - resets your current skill levels.", false);
			sendConsoleMessage("'master' - raises all your levels to their maximum level.", false);
			sendConsoleMessage("'setlevel $ #' - raises your skill ($) to said amount (#).", false);
			sendConsoleMessage("'anim #' - performs the specified animation (#).", false);
			sendConsoleMessage("'gfx #' - performs the specified graphic (#).", false);
			sendConsoleMessage("'rights (name) (rank)' - gives (rank) to player with the username as (name).", false);
			sendConsoleMessage("'interface #' - displays specified interface id (#).", false);
			sendConsoleMessage("'update #' - sets a system update for said seconds (#).", false);
			sendConsoleMessage("'gear' - equips you with a default equipment array.", false);
			sendConsoleMessage("'item $' or 'item $ #' - spawns an item id ($) by a certain amount (# or 1 by default).", false);
			sendConsoleMessage("'npc #' - spawns an npc (#).", false);
			sendConsoleMessage("'object #' - spawns an object (#).", false);
			sendConsoleMessage("'find #' - finds the id for said item name.", false);
		}
	}

	public void sendConsoleCommand(String command) {
		String[] args = command.split(" ");
		if(args == null || args.length == 0)
			return;
		String commandStart = args[0].toLowerCase();
		if(commandStart == null)
			return;
		switch (commandStart) {
		case "ttt":
			//drawLoading = !drawLoading;
			loadingStage = 2;
			ObjectManager.anInt131 = plane;
			loadRegion();
			stream.createFrame(121);
			break;
		case "noclip":
			if(myRights >= 1 && myRights <= 4) {
				for (int k1 = 0; k1 < 4; k1++) {
					for (int i2 = 1; i2 < 103; i2++) {
						for (int k2 = 1; k2 < 103; k2++)
							clippingPlanes[k1].clipData[i2][k2] = 0;

					}
				}
			}
			break;
		case "cls":
			for (int index = 0; index < 17; index++) {
				consoleMessages[index] = null;
			}
			sendConsoleMessage("Type 'commands' for a list of commands, 'cls' to clear the console, and 'info' for more", false);
			sendConsoleMessage("information. Use the 'close'command or hit the '~' button to close the console.", false);
			break;
		case "close":
			consoleOpen = false;
			break;
		case "gc":
			System.gc();
			break;
		case "commands":
			getConsoleCommands();
			break;
		case "data":
			clientData = !clientData;
			break;
		case "sd":
			setLowMem();
			break;
		case "hd":
			setHighMem();
			break;
		case "child":
			if(myRights >= 1 && myRights <= 4) {
				for(int i = 0; i <= 30000; i++) {
					sendFrame126(""+i, i);
					sendConsoleMessage(""+i, false);
				}
			}
			break;
		case "lag":
			printDebug();
			break;
		case "fps":
			fpsOn = !fpsOn;
			break;
		default:
			stream.createFrame(103);
			stream.writeWordBigEndian(command.length() +1);
			stream.writeString(command);
			break;
		}
	}

	public void updateSkilltab() {
		//Update skillstab
		try {
			int totalLevel = 0, totalExp = 0;
			int[] currentPlayerStats = currentStats.clone();
			int[] maxPlayerLevels = currentMaxStats.clone();
			int[] currentPlayerExp = currentExp.clone();
			maxPlayerLevels[5] = maxPlayerLevels[5] / 100; maxPlayerLevels[3] = maxPlayerLevels[3] / 100;
			currentPlayerStats[5] = currentPlayerStats[5] / 100; currentPlayerStats[3] =  currentPlayerStats[3] / 100;
			for(int h = 0; h < maxPlayerLevels.length; h++) {
				if(currentPlayerStats[h] < 0)
					currentPlayerStats[h] = 0;
				if(maxPlayerLevels[h] <= 0)
					maxPlayerLevels[h] = 1;
				totalLevel += maxPlayerLevels[h];
				totalExp += currentPlayerExp[h];
			}
			for(int x = 0; x < maxPlayerLevels.length; x++) {
				Skill skill = Skill.forId(x);
				sendFrame126(Integer.toString(currentPlayerStats[x]), skill.updateStrings[0]);
				sendFrame126(Integer.toString(maxPlayerLevels[x]), skill.updateStrings[1]);
				String toSend = maxPlayerLevels[x] >= 99 && x != 3 || x == 3 && maxPlayerLevels[x] >= 99 ? ""+optimizeText(skill.toString().toLowerCase())+": "+currentPlayerStats[x]+"/"+maxPlayerLevels[x]+"\nCurrent XP: "+currentPlayerExp[x] : ""+optimizeText(skill.toString().toLowerCase())+": "+currentPlayerStats[x]+"/"+maxPlayerLevels[x]+"\nCurrent XP: "+currentPlayerExp[x]+"\nNext Level: "+(getXPForLevel(maxPlayerLevels[x]+1))+"\nRemainder: "+(getXPForLevel(maxPlayerLevels[x]+1)- currentPlayerExp[x])+"";
				sendFrame126(toSend, skill.updateStrings[2]);
			}
			sendFrame126(""+totalLevel+"", 31200); 
			sendFrame126("Total Level: "+totalLevel+"\nTotal XP: "+totalExp+"", 31198);
			//summLevel = currentPlayerStats[23];
			maxPlayerLevels = currentPlayerStats = currentPlayerExp = null;
		} catch(Exception e) {
			e.printStackTrace();

		}
	}

	private void updateClanChatTab() {
		for(int childIndex = 0; childIndex < 100; childIndex++) {
			RSInterface childInterface = RSInterface.interfaceCache[18144+childIndex];
			if(childInterface.message != null && childInterface.message.length() > 0 && childInterface.message.contains("<clan")) {
				String image = childInterface.message.substring(childInterface.message.indexOf("=")+1, childInterface.message.indexOf(">"));
				int imageId = Integer.valueOf(image);
				Sprite icon = SpriteLoader.sprites2[imageId];
				icon.drawSprite(30, drawClanChatY);
			}
			drawClanChatY+= 15;
		}
		drawClanChatY = 101;
	}
	private int drawClanChatY = 101;

	private void saveQuickSelection() {
		saveSettings();
		tabInterfaceIDs[5] = prayerInterfaceType;
	}

	private boolean quickPrsActive = false;

	private void handleQuickAidsActive() {
		if(prayerInterfaceType == 5608) {
			if(getQuickPrayersSet() > 0) {
				if(!quickPrsActive)
					for(int i = 0; i < quickPrayers.length; i++) {
						if(quickPrayers[i] == 1) {
							stream.createFrame(185);
							stream.writeWord((i * 2) + 25000);
						}
					}
				else
					turnOffPrayers();
				quickPrsActive = !quickPrsActive;
			} else {
				if(quickPrsActive) {
					turnOffPrayers();
					quickPrsActive = false;
				} else {
					pushMessage("You don't have any quick prayers selected.", 0, "");
					pushMessage("Right-click the Prayer button next to the minimap to select some.", 0, "");
				}
			}
		} else if(prayerInterfaceType == 32500) {
			if(getQuickCursesSet() > 0) {
				if(!quickPrsActive)
					for(int i = 0; i < quickCurses.length; i++) {
						if(quickCurses[i] == 1) {
							stream.createFrame(185);
							stream.writeWord((i * 2) + 32500 + 3);
						}
					}
				else
					turnOffCurses();
				quickPrsActive = !quickPrsActive;
			} else {
				if(quickPrsActive) {
					turnOffCurses();
					quickPrsActive = false;
				} else {
					pushMessage("You don't have any quick curses selected.", 0, "");
					pushMessage("Right-click the Curses button next to the minimap to select some.", 0, "");
				}
			}
		}
	}

	private int getQuickPrayersSet() {
		int amount = 0;
		for(int i = 0; i < quickPrayers.length; i++)
			if(quickPrayers[i] == 1) {
				amount++;
			}
		return amount;
	}

	private int getQuickCursesSet() {
		int amount = 0;
		for(int i = 0; i < quickCurses.length; i++)
			if(quickCurses[i] == 1) {
				amount++;
			}
		return amount;
	}

	private int[] quickPrayers = new int[26];
	private int[] quickCurses = new int[20];

	private int[] getCurseTypeForIndex(int index) {
		int[] types = null;
		for(int g = 0; g < leechCurse.length; g++) {
			if(index == leechCurse[g])
				types = sapCurse;
		}
		for(int g = 0; g < sapCurse.length; g++) {
			if(index == sapCurse[g])
				types = leechCurse;
		}
		for(int g = 0; g < deflectCurse.length; g++) {
			if(index == deflectCurse[g])
				types = deflectCurse;
		}
		if(index == 6) {
			int[] type = {17,18};
			types = type;
		}
		if(index == 17 || index == 18) {
			int[] type = {6,7,8,9,17,18};
			types = type;
		}
		if(index == 19)  {
			int[] turmoilCurseOff = {1,2,3,4,10,11,12,13,14,15,16,19};
			types = turmoilCurseOff;
		}
		return types;
	}

	public void togglePrayerState(int button) {
		int index = button - 17202;
		if(prayerInterfaceType == 5608) {
			if((currentMaxStats[5] / 10) >= prayerLevelRequirements[index]) {
				int[] types = getPrayerTypeForIndex(index);
				if(types != null) {
					for(int g = 0; g < rangeAndMagePray.length; g++) {
						if(index == rangeAndMagePray[g]) {
							types = rangeAndMagePrayOff;
						}
					}
					for(int i = 0; i < types.length; i++) {
						if(index != types[i]) {
							if(index == 24 || index == 25) {
								types = superiorPray;
							}
							quickPrayers[types[i]] = 0;
							variousSettings[quickConfigIDs[types[i]]] = 0;
							handleActions(quickConfigIDs[types[i]]);
							if(dialogID != -1)
								inputTaken = true;
						} else {
							quickPrayers[index] = (quickPrayers[index] + 1) % 2;
							variousSettings[quickConfigIDs[index]] = quickPrayers[index];
							handleActions(quickConfigIDs[index]);
							if(dialogID != -1)
								inputTaken = true;
						}
					}
				} else {
					quickPrayers[index] = (quickPrayers[index] + 1) % 2;
					variousSettings[quickConfigIDs[index]] = quickPrayers[index];
					handleActions(quickConfigIDs[index]);
					if(dialogID != -1)
						inputTaken = true;
				}
			} else {
				pushMessage("You need a Prayer level of atleast " + prayerLevelRequirements[index] + " to use " + prayerName[index] + ".", 0, "");
			}
		} else if(prayerInterfaceType == 32500) {
			if((currentMaxStats[5] / 10) >= curseLevelRequirements[index]) {
				int[] types = getCurseTypeForIndex(index);
				if(types != null) {
					for(int i = 0; i < types.length; i++) {
						if(index != types[i]) {
							quickCurses[types[i]] = 0;
							variousSettings[quickConfigIDs[types[i]]] = 0;
							handleActions(quickConfigIDs[types[i]]);
							if(dialogID != -1)
								inputTaken = true;
						}
					}
				}
				quickCurses[index] = (quickCurses[index] + 1) % 2;
				variousSettings[quickConfigIDs[index]] = quickCurses[index];
				handleActions(quickConfigIDs[index]);
				if(dialogID != -1)
					inputTaken = true;
			} else {
				pushMessage("You need a Prayer level of atleast " + curseLevelRequirements[index] + " to use " + curseName[index] + ".", 0, "");
			}
		}
	}

	private void turnOffPrayers() {
		int toggle = -1;
		for(int i = 0; i < quickPrayers.length; i++) {
			RSInterface rsInterface = RSInterface.interfaceCache[(i * 2) + 25000];
			if(rsInterface.valueIndexArray != null && rsInterface.valueIndexArray[0][0] == 5) {
				toggle = rsInterface.valueIndexArray[0][1];
				if(variousSettings[toggle] == 1) {
					stream.createFrame(185);
					stream.writeWord((i * 2) + 25000); 
				}	
			}
		}
	}

	private void turnOffCurses() {
		int toggle = -1;
		for(int i = 0; i < quickCurses.length; i++) {
			RSInterface rsInterface = RSInterface.interfaceCache[(i * 2) + 32503];
			if(rsInterface.valueIndexArray != null && rsInterface.valueIndexArray[0][0] == 5) {
				toggle = rsInterface.valueIndexArray[0][1];
				if(variousSettings[toggle] == 1) {
					stream.createFrame(185);
					stream.writeWord((i * 2) + 32503);
				}
			}
		}
	}

	public int prayerInterfaceType;
	public final int[] curseLevelRequirements = {50,50,52,54,56,59,62,65,68,71,74,76,78,80,82,84,86,89,92,95};
	public final String[] curseName = {
			"Protect Item", "Sap Warrior", "Sap Ranger", "Sap Mage", "Sap Spirit",
			"Berserker", "Deflect Summoning", "Deflect Magic","Deflect Missiles", "Deflect Melee",
			"Leech Attack", "Leech Ranged", "Leech Magic", "Leech Defence", "Leech Strength",
			"Leech Energy", "Leech Special Attack", "Wrath", "Soul Split", "Turmoil"
	};
	public int[] quickConfigIDs = {630,631,632,633,634,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,655,656};
	public final int[] prayerLevelRequirements = {1,4,7,8,9,10,13,16,19,22,25,26,27,28,31,34,37,40,43,44,45,46,49,52,60,70};
	public final String[] prayerName = {
			"Thick Skin", "Burst of Strength", "Clarity of Thought", "Sharp Eye", "Mystic Will", "Rock Skin", "Superhuman Strength", "Improved Reflexes","Rapid Restore", 
			"Rapid Heal", "Protect Item", "Hawk Eye", "Mystic Lore", "Steel Skin", "Ultimate Strength", "Incredible Reflexes","Protect from Magic", "Protect from Missiles",
			"Protect from Melee","Eagle Eye", "Mystic Might", "Retribution", "Redemption", "Smite", "Chivalry", "Piety"
	};
	private int[] defPray = {0,5,13,24,25};
	private int[] strPray = {1,3,4,6,11,12,14,19,20,24,25};
	private int[] atkPray = {2,3,4,7,11,12,15,19,20,24,25};
	private int[] rangeAndMagePray = {3,4,11,12,19,20,24,25};
	private int[] rangeAndMagePrayOff = {1,2,3,4,6,7,11,12,14,15,19,20,24,25};
	private int[] headPray = {16,17,18,21,22,23};
	private int[] superiorPray = {0,1,2,3,4,5,6,7,11,12,13,14,15,19,20,24,25};
	private int[][] prayer = {defPray,strPray,atkPray,rangeAndMagePray,headPray};	

	private int[] getPrayerTypeForIndex(int index) {
		int[] type = null;
		for(int i = 0; i < prayer.length; i++) {
			for(int il = 0; il < prayer[i].length; il++) {
				if(index == prayer[i][il]) {
					type =  prayer[i];
				}
			}
		}
		return type;
	}

	private int[] sapCurse = {1,2,3,4,19};
	private int[] leechCurse = {10,11,12,13,14,15,16,19};
	private int[] deflectCurse = {7,8,9,17,18};


	public void toggleQuickAidsSelection() {
		boolean inProcess = (tabInterfaceIDs[5] == 17200 || tabInterfaceIDs[5] == 17234);
		if(inProcess)
			saveQuickSelection();
		else {
			if(prayerInterfaceType == 5608) {
				if(!inProcess) {
					tabInterfaceIDs[5] = 17200;
					tabAreaAltered = true;
				} else {
					tabInterfaceIDs[5] = 5608;
					tabAreaAltered = true;
				}
			} else if(prayerInterfaceType == 32500) {
				if(!inProcess) {
					tabInterfaceIDs[5] = 17234;
					tabAreaAltered = true;
				} else {
					tabInterfaceIDs[5] = 32500;
					tabAreaAltered = true;
				}
			}
			tabID = 5;
		}
	}

	/*
	 * Money orb
	 */
	public Sprite coinOrb;
	public Sprite coinOrbHover;
	public Sprite coinPart;
	public Sprite coinPartHover;
	public Sprite coinOrbPart;
	public Sprite coinOrbPartHover;
	public boolean coinToggle;

	public void drawOnBankInterface()
	{
		if(openInterfaceID == 5292 && RSInterface.interfaceCache[27000].message.equals("1"))
		{
			int i = Integer.parseInt(RSInterface.interfaceCache[27001].message);
			int j = Integer.parseInt(RSInterface.interfaceCache[27002].message);
			for(int k = 0; k <= i; k++)
			{
				RSInterface.interfaceCache[27014 + k].disabledSprite = new Sprite("Interfaces/Bank/TAB 3");
				RSInterface.interfaceCache[27014 + k].message = (new StringBuilder()).append("Click here to select tab ").append(k + 1).toString();
			}

			for(int l = i + 1; l <= 8; l++)
			{
				RSInterface.interfaceCache[27014+ l].disabledSprite = new Sprite("");
				RSInterface.interfaceCache[27014 + l].message = "";
			}

			if(i != 8)
			{
				RSInterface.interfaceCache[27015 + i].disabledSprite = new Sprite("Interfaces/Bank/TAB 4");
				RSInterface.interfaceCache[27015 + i].message = "Drag an item here to create a new tab";
			}
			if(j == -1)
			{
				RSInterface.interfaceCache[27013].disabledSprite = new Sprite("Interfaces/Bank/TAB 1");
			} else
				if(j > 0)
				{
					RSInterface.interfaceCache[27014 + j].disabledSprite = new Sprite("Interfaces/Bank/TAB 2");
					RSInterface.interfaceCache[27014].disabledSprite = new Sprite("Interfaces/Bank/TAB 1");
				} else
				{
					RSInterface.interfaceCache[27014].disabledSprite = new Sprite("Interfaces/Bank/TAB 0");
				}
			RSInterface.interfaceCache[27000].message = "0";
		}
	}

	public int getCoinOrbX() {
		return clientSize == 0 ? -2 : clientWidth - 40;
	}

	public int getCoinOrbY() {
		if(clientSize == 0)
			return 84;
		return 175;
	}

	public void drawCoinOrb() {
		int x = getCoinOrbX();
		int y = getCoinOrbY();
		boolean hover = clientSize == 0 ?  mouseInRegion(515, 85, 515 + 34, 85 + 34) : mouseInRegion(x, y, x + 34, y + 34);
		if(!coinToggle) {
			if(hover)
				coinOrbPartHover.drawSprite(x, y);
		}
		if(hover)
			coinOrbHover.drawSprite(x, y);
		else
			coinOrb.drawSprite(x, y);
		if(clientSize == 0)
			coinOrbPart.drawSprite(-1, y);
	}

	public String getMoneyInPouch() {
		String Cash = RSInterface.interfaceCache[8135].message;
		int convertedMoney = Integer.parseInt(Cash);
		Cash = formatAmount(convertedMoney);
		if(Cash.equals("0"))
			return "    0";
		return Cash;
	}

	public int getXPForLevel(int level) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level) {
				return output;
			}
			output = (int)Math.floor(points / 4);
		}
		return 0;
	}

	public String getRank(int i){
		switch(i){
		case 1:
			return "Sir";
		case 2:
			return "Lady";
		case 3:
			return "King";
		case 4:
			return "Queen";
		case 5:
			return "Gangster";
		case 6:
			return "Peedo";
		case 7:
			return "Demon";
		case 8:
			return "Angel";
		case 9:
			return "Unstoppable";
		case 10:
			return "Nerd";
		case 11:
			return "Psychopath";
		case 12:
			return "Immortal";
		case 13:
			return "Helper";
		case 14:
			return "Hell Raiser";
		case 15:
			return "Baron";
		case 16:
			return "Duke";
		case 17:
			return "Lady";
		case 18:
			return "Dame";
		case 19:
			return "Dudette";
		case 20:
			return "Baroness";
		case 21:
			return "Countess";
		case 22:
			return "Overlordess";
		case 23:
			return "Duchess";
		case 24:
			return "Queen";		
		case 25:
			return "Donator";		
		case 26:
			return "Extreme Donator";		
		case 27:
			return "Veteran";		
		case 28:
			return "Owner";		
		case 29:
			return "Co-Owner";
		case 30:
			return "Trusted-Dice";
		case 31:
			return "Trusted";
		case 32:
			return "Dicer";
		case 33:
			return "AdminLikeBaws";		
		case 34:
			return "The best";		
		case 35:
			return "Gladiator";	
		case 36:
			return "Survivor";	
		case 37:
			return "Dominator";
		case 38:
			return "The Boss";
		}
		return "";
	}


	private int arrowO = 0;
	private int arrowP = 0;
	private byte arrowS = 0;
	public void drawArrow(String title, String information, int x, int y, int speed, int pause, int type) {
		int length = 4;
		int max = 255;
		int low = 0;
		int rt = type;
		Sprite[] arrows = new Sprite[length];
		for (int amt = 0; amt < length; amt++) {
			arrows[amt] = new Sprite("Icons/Arrows/"+amt);
		}
		Sprite arrowinfo = new Sprite("Icons/Arrows/arrowinfo");
		arrows[rt].drawAdvancedSprite2(x,y,arrowO);
		if (mouseInRegion(x+40, y, x+80, y+106)) {
			//arrowinfo.drawAdvancedSprite(x-80, y-120);
			int titleLength = title.length();
			int X = drawX - 107 - (titleLength * 5);
			int Y = drawY + 3;
			drawBorder(null, borderImages, X, Y, 130 + titleLength * 5, 110, 75, 0, true, false);
			
			String[] results = information.split("---");
			int height = (results.length * 16) + 6;
			int width;
			width =  newRegularFont.getTextWidth(results[0]) + 6;
			int x2 = X, y2 = Y;
			x2 += 80;
			y2 += 38;
			for(int i = 1; i < results.length; i++)
				if(width <=  newRegularFont.getTextWidth(results[i]) + 6)
					width =  newRegularFont.getTextWidth(results[i]) + 6;
			x2 += 14;

			for(int i = 0; i < results.length; i++) {
				newRegularFont.drawCenteredString(results[i], x2, y2, 0xffffff, -1);
				y2 += 16;
			}
			newBoldFont.drawBasicString(title, X + 25 + (newBoldFont.getTextWidth(title) / 2), Y + 20);
		}
		if (arrowS == 0) {
			arrowO += speed;
			if (arrowO == max) {
				arrowS = 1;
			}
		} else if (arrowS == 1) {
			if (arrowP != pause) {
				arrowP++;
			} else {
				arrowP = 0;
				arrowS = 2;
			}
		} else if (arrowS == 2) {
			arrowO -= speed;
			if (arrowO == low) {
				arrowS = 0;
			}
		}

	}

	private int cursor;

	public void setCursor(int id) {
		if(isLoading || resourceLoader == null)
			return;
		if(id == -1 || id == -2) {
			getMainFrame().setCursor(new Cursor(id == -1 ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
			cursor = id;
		} else {
			try {
				cursor = id;
				Image image = resourceLoader.getImage("Clientimages/Cursors/Cursor "+id+"");
				if(image == null)
					return;
				getMainFrame().setCursor(getMainFrame().getToolkit().createCustomCursor(image, new Point(0, 0), null));
			} catch(Exception e) {
				cursor = -1;
				getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	private String title = "";
	private String information = "";
	private int drawX = 0;
	private int drawY = 0;
	private int speed = 0;
	private int pause = 0;
	private int type2 = -1;
	private int itemToLend;
	private int hardwareA = -1;

}