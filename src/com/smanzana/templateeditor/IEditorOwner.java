package com.smanzana.templateeditor;

public interface IEditorOwner {

	/**
	 * Called from the editor to signal that the current piece being worked on has changed and,
	 * as such, should be committed before exiting.
	 */
	public void dirty();
	
}
