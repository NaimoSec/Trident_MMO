package org.trident.util;

import java.util.HashMap;
import java.util.Map;

public class FrameUpdater {

	/**
	 * System to optimize sendFrame126 performance.
	 * @author MikeRSPS
	 * UltimateScape
	 * http://ultimatescape2.com
	 */
	public Map<Integer, TinterfaceText> interfaceTextMap = new HashMap<Integer, TinterfaceText>();
	
	public class TinterfaceText {
		public int id;
		public String currentState;
		
		public TinterfaceText(String s, int id) {
			this.currentState = s;
			this.id = id;
		}
		
	}

	public boolean shouldUpdate(String text, int id) {
		if(text.equalsIgnoreCase("[CLOSEMENU]") || id == 27000 || id == 27001 || id == 27002 || id == 1)
			return true;
		if(!interfaceTextMap.containsKey(id)) {
			interfaceTextMap.put(id, new TinterfaceText(text, id));
		} else {
			TinterfaceText t = interfaceTextMap.get(id);
			if(text.equals(t.currentState)) {
				return false;
			}
			t.currentState = text;
		}
		return true;
	}
}
