package org.trident.model.definitions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.trident.model.Item;
import org.trident.model.container.impl.Shop;



public class ShopManager {
	
	private static Map<Integer, Shop> shops = new HashMap<Integer, Shop>();
	
	@SuppressWarnings("resource")
	public static boolean init() {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[(101 * 2)];
		String FileName = "shops.cfg";
		boolean EndOfFile = false;
		BufferedReader shopConfig = null;
		try {
			shopConfig = new BufferedReader(new FileReader("data/config/"
					+ FileName));
		} catch (FileNotFoundException fileex) {
			System.out.println(FileName + ": file not found.");
			return false;
		}
		try {
			line = shopConfig.readLine();
		} catch (IOException ioexception) {
			System.out.println(FileName + ": error loading file.");
			return false;
		}
		while (EndOfFile == false && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token3 = token2_2.split("\t");
				int index = 0;
				if (token.equals("shop")) {
					int shopId = Integer.parseInt(token3[0]);
					String shopName= token3[1].replaceAll("_", " ");
					Item[] stock = new Item[((token3.length - 2) / 2)];
					for(int i = 0; i < stock.length; i++)
						stock[i] = new Item(-1);
					for (int i = 0; i < ((token3.length - 2) / 2); i++) {
						if (token3[(2 + (i * 2))] != null) {
							int item = (Integer.parseInt(token3[(2 + (i * 2))]));
							int itemAmount = (Integer.parseInt(token3[(3 + (i * 2))]));
							stock[index] = new Item(item, itemAmount);
							index++;
						} else {
							break;
						}
					}
					int currency = 995;
					if(shopId == 33)
						currency = 18201;
					else if(shopId == 10)
						currency = 1464;
					else if(shopId == 12)
						currency = 2996;
					else if(shopId == 60)
						currency = 13653;
					shops.put(shopId, new Shop(null, shopId, shopName, new Item(currency), stock));
				}
			} else {
				if (line.equals("[ENDOFSHOPLIST]")) {
					try {
						shopConfig.close();
					} catch (IOException ioexception) {
					}
					return true;
				}
			}
			try {
				line = shopConfig.readLine();
			} catch (IOException ioexception1) {
				EndOfFile = true;
			}
		}
		try {
			shopConfig.close();
		} catch (IOException ioexception) {
		}
		return false;
	}
	
	public static Map<Integer, Shop> getShops() {
		return shops;
	}
}
