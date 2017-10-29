package com.smanzana.templateeditor.api;

import com.smanzana.templateeditor.editor.fields.EditorField;

/**
 * Type of data that cannot be represented by any types described in
 * {@link FieldType}.<br />
 * T should match the object the provided field produces
 * @author Skyler
 *
 */
public interface ICustomData {
	
	/**
	 * Construct an editor field to represent this field.
	 * The editor field should be initialized to the current state of this piece of data.
	 * @return An editor to be displayed to represent and edit this field
	 */
	public EditorField<?> getField();
	
	/**
	 * Pull data out from the given field to update this piece of data.
	 * The passed field will be the same as produced with a corresponding call to {@link #getField()}.
	 * @param field
	 */
	public void fillFromField(EditorField<?> field);
	
}
