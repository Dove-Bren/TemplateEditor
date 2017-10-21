package com.smanzana.templateeditor.uiutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumMap;
import java.util.Map;

public class UIConfState {
	
	public static enum Key {
		LASTTEMPLATE,
		LASTSESSION,
	}

	private static UIConfState instance;
	
	public static UIConfState instance() {
		if (instance == null)
			instance = new UIConfState();
		
		return instance;
	}
	
	private Map<Key, String> values;
	
	private UIConfState() {
		values = new EnumMap<>(Key.class);
		for (Key key : Key.values())
			values.put(key, null);
	}
	
	public String get(Key key) {
		return values.get(key);
	}
	
	public void set(Key key, String value) {
		values.put(key, value);
	}
	
	public static void saveToFile(File outFile) throws FileNotFoundException {
		String buf = "";
		
		for (Key k : Key.values()) {
			buf += k.name() + ":" + instance().values.get(k) + ",";
		}
		
		PrintWriter writer = new PrintWriter(outFile);
		writer.println(buf);
		writer.close();
	}
	
	public static void loadFromFile(File inFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inFile));
		String raw = reader.readLine();
		reader.close();
		
		for (String sub : raw.split(",")) {
			int pos = sub.indexOf(':');
			if (pos == -1)
				continue;
			
			String key = sub.substring(0, pos);
			Key real;
			try {
				real = Key.valueOf(key);
			} catch (Exception e) {
				System.err.println("Failed to find key for read value: " + key);
				continue;
			}
			
			String val = sub.substring(pos + 1);
			if (val == null || val.trim().isEmpty() || val.trim().equalsIgnoreCase("null"))
				val = null;
			instance().values.put(real, val);
		}
		
	}
	
}
