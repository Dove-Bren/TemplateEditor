package com.smanzana.templateeditor;

public interface IEditorDisplayable {

	/**
	 * Returns a name for the editor to display for this object
	 * @return
	 */
	public String getEditorName();
	
	/**
	 * Returns a longer description that can be used as a tooltip for this object
	 * @return
	 */
	public String getEditorTooltip();
	
}
