package com.smanzana.templateeditor;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.ImageIcon;

public final class EditorIconRegistry {
	
	public static enum Key {
		ARROW_RIGHT("arrow_right.png", "right arrow"),
		ARROW_DOWN("arrow_down.png", "down arrow");
		
		private String defaultPath;
		private String shortDesc;
		
		private Key(String defaultPath, String shortDesc) {
			this.defaultPath = defaultPath;
			this.shortDesc = shortDesc;
		}

		public String getDefaultPath() {
			return defaultPath;
		}

		public String getShortDesc() {
			return shortDesc;
		}
	}
	
	private static final String PATH_ICON_DEFAULT = "icons/";
	private static Map<Key, ImageIcon> icons = null;
	
	private static void lazyInit() {
		if (icons == null) {
			icons = new EnumMap<>(Key.class);
			for (Key k : Key.values()) {
				URL imgURL = EditorIconRegistry.class.getResource(PATH_ICON_DEFAULT + k.getDefaultPath());
		        if (imgURL != null) {
		            icons.put(k,  new ImageIcon(imgURL, k.getShortDesc()));
		        } else {
		            System.err.println("Couldn't find file: " + PATH_ICON_DEFAULT + k.getDefaultPath());
		        }
			}
		}
	}
	
	public static ImageIcon get(Key key) {
		lazyInit();
		return icons.get(key);
	}
	
	public static void register(Key key, ImageIcon icon) {
		lazyInit();
		icons.put(key, icon);
	}
	
}
