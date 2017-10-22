package com.smanzana.templateeditor.api;

import com.smanzana.templateeditor.editor.fields.EditorField;

/**
 * Type of data that cannot be represented by any types described in
 * {@link FieldType}.<br />
 * T should match the object the provided field produces
 * @author Skyler
 *
 */
public interface IUserData<T> {
	
	public EditorField<T> getField();
	
}
