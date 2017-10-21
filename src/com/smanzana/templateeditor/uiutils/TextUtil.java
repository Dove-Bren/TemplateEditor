package com.smanzana.templateeditor.uiutils;

public class TextUtil {
	/**
	 * Removes _'s and camel-cases stuff
	 * @param raw
	 * @return
	 */
	public static String pretty(String raw) {
		String buf = "";
			
		buf += raw.substring(0, 1);
		raw = raw.substring(1);
		raw = raw.toLowerCase();
		
		int pos;
		while (-1 != (pos = raw.indexOf('_'))) {
			// pos is position of first underscore.
			// copy up to pos into buf. Then copy char after pos as uppercase.
			// then set raw past capital char
			buf += raw.substring(0, pos);
			buf += raw.substring(pos + 1, pos + 2).toUpperCase();
			raw = raw.substring(pos + 2);
		}
		buf += raw;
		
		return buf;
	}
}
