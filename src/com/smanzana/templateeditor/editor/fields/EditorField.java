package com.smanzana.templateeditor.editor.fields;

import javax.swing.JComponent;

import com.smanzana.templateeditor.IEditorOwner;

/**
 * Subfield of an editor responsible for managing and editing an object.
 * @author Skyler
 *
 * @param <T> Type this editor edits
 */
public interface EditorField<T> {

	/**
	 * Returns a JComponent that is responsible for displaying the required fields and prompts
	 * to the user to get the data needed to assemble the edited object.
	 * @return Component to be nested in editor panel for this object
	 */
	public JComponent getComponent();
	
	/**
	 * Returns the current object with all current modifications.
	 * @return
	 */
	public T getObject();
	
	/**
	 * Grants the provided object to the field for editing.<br />
	 * The field is responsible for modifying the object as edits are made.
	 * The field is ALSO responsible for being able to return the original
	 * object if asked.
	 */
	public void setObject(T obj);
	
	/**
	 * Returns the object as it was provided by {@link #setObject(Object)}. This is
	 * basically what's called when the edit operation is 'cancel'ed.
	 * @return
	 */
	public T getOriginal();
	
	/**
	 * Sets the current owner.<br />
	 * The owner should be notified as soon as data is changed (mark it dirty).
	 * @param owner
	 */
	public void setOwner(IEditorOwner owner);
	
}
