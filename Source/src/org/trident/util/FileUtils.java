package org.trident.util;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.jboss.netty.buffer.ChannelBuffer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This file contains utilities that may be used to read
 * values from certain files.
 * 
 * @author relex lawl
 */

public class FileUtils {
	
	/*public static void makeCompiler() throws IOException {
		File batch = new File("./compiler.bat");
		String directory = "./src/";
		BufferedWriter writer = new BufferedWriter(new FileWriter(batch));
		
	}*/

	/**
	 * Reads string from a data input stream.
	 * @param inputStream 	The input stream to read string from.
	 * @return 				The String value.
	 */
	public static String readString(DataInputStream inputStream) {
		byte data;
		StringBuilder builder = new StringBuilder();
		try {
			while ((data = inputStream.readByte()) != 0) {
				builder.append((char) data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
	
	/**
	 * Reads an unsigned tri byte from the specified buffer.
	 * @param buffer 	The byte buffer.
	 * @return 			The tri byte value.
	 */
	public static int readTriByte(ByteBuffer buffer) {
		return ((buffer.get() & 0xFF) << 16) | ((buffer.get() & 0xFF) << 8) | (buffer.get() & 0xFF);
	}

	/**
	 * Reads a string from the specified buffer.
	 * @param buffer 	The byte buffer.
	 * @return 			The string value.
	 */
	public static String readString(ByteBuffer buffer) {
		StringBuilder bldr = new StringBuilder();
		char c;
		while ((c = (char) buffer.get()) != 0) {
			bldr.append(c);
		}
		return bldr.toString();
	}
	
	/**
	 * Reads a string from the specified buffer.
	 * @param buffer	The channel buffer.
	 * @return			The string value.
	 */
	public static String readString(ChannelBuffer buffer) {
		StringBuilder builder = null;
		try {
			byte data;
			builder = new StringBuilder();
			while ((data = buffer.readByte()) != 10) {
				builder.append((char) data);
			}
		} catch(IndexOutOfBoundsException e) {
			
		}
		return builder.toString();
	}

	/**
	 * Reads a smart value from a given byte buffer.
	 * @param buffer 	The byte buffer.
	 * @return 			The 'smart' value.
	 */
	public static int readSmart(ByteBuffer buffer) {
		int peek = buffer.get(buffer.position()) & 0xFF;
		if (peek < 128) {
			return buffer.get() & 0xFF;
		}
		return (buffer.getShort() & 0xFFFF) - 32768;
	}
	
	/**
	 * Gets tag value from XML entry.
	 * @param sTag			XML tag String.
	 * @param eElement		XML Element Object.
	 * @return				Integer.parseInt(((Node) lastNodeList.item(0)).getNodeValue()).
	 */
	public static String getXMLEntry(String sTag, Element eElement) {
		NodeList nodeList = eElement.getElementsByTagName(sTag);
	    Element element = (Element) nodeList.item(0);
	    NodeList lastNodeList = element.getChildNodes();
		return ((Node) lastNodeList.item(0)).getNodeValue();
	}
	
	/**
	 * Copies a file to specified destination.
	 * @param file		Original file to copy from.
	 * @param target	File destination to copy to.
	 */
	public static void copy(File file, File target) {
		try {
			@SuppressWarnings("resource")
			FileChannel source = new FileInputStream(file).getChannel();
			@SuppressWarnings("resource")
			FileChannel destination = new FileOutputStream(target).getChannel();
			destination.transferFrom(source, 0, source.size());
			source.close();
			destination.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	/**
	 * Writes said string to specified file.
	 * @param string	String to write to file.
	 */
	public static void write(BufferedWriter writer, String string) {
		try {
			writer.write(string);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}