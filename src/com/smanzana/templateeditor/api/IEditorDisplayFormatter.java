package com.smanzana.templateeditor.api;

import java.util.Map;

/**
 * Can take a datamap (some key to a FieldData) and produce a meaningful
 * name and (optionally) a description
 * @author Skyler
 *
 * @param <T> Key expected from the datamap.
 */
public interface IEditorDisplayFormatter<T> {

	/**
	 * Returns a name for the editor to display for this object
	 * @return
	 */
	public String getEditorName(Map<T, FieldData> dataMap);
	
	/**
	 * Returns a longer description that can be used as a tooltip for this object
	 * @return
	 */
	public String getEditorTooltip(Map<T, FieldData> dataMap);
	
}
