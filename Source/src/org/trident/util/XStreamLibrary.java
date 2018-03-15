package org.trident.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import org.trident.model.definitions.NPCDrops;
import org.trident.model.definitions.NPCDrops.NpcDropItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Class handling all XStream
 * 
 * @author Ultimate1
 */

public class XStreamLibrary {

	private static XStream xStream = new XStream();

	static {
		xStream.alias("dropController", NPCDrops.class);
		xStream.alias("npcDrop", NpcDropItem.class);
	}

	/**
	 * Gets tag value from XML entry.
	 * @param tag			XML tag String.
	 * @param xmlElement		XML Element Object.
	 * @return				Integer.parseInt(((Node) lastNodeList.item(0)).getNodeValue()).
	 */
	public static String getEntry(String tag, Element xmlElement) {
		NodeList nodeList = xmlElement.getElementsByTagName(tag);
	    Element element = (Element) nodeList.item(0);
	    NodeList lastNodeList = element.getChildNodes();
		return ((Node) lastNodeList.item(0)).getNodeValue();
	}
	
	public static NodeList getNodesByName(Document doc, String nodeName) {
		Element docEle = doc.getDocumentElement();
		NodeList list = docEle.getElementsByTagName(nodeName);
		return list;
	}
	
	public static Object load(String file) throws FileNotFoundException {
		FileInputStream in = new FileInputStream(file);
		try {
			return xStream.fromXML(in);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void save(String file, Object data)
			throws FileNotFoundException {
		FileOutputStream out = new FileOutputStream(file);
		try {
			xStream.toXML(data, out);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}