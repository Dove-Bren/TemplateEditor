package com.smanzana.templateeditor.data;

import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.EnumField;

/**
 * FieldData that represents a subset selection
 * @author Skyler
 *
 */
public final class EnumFieldData<T extends Enum<T>> extends FieldData {
	
	private T selected;
	
	public EnumFieldData(T current) {
		this.selected = current;
	}

	@Override
	public FieldData clone() {
		T selected = this.selected;
		
		return new EnumFieldData<T>(selected);
	}

	@Override
	public EditorField<?> constructField() {
		return new EnumField<T>(selected);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fillFromField(EditorField<?> field) {
		selected = (T) field.getObject();
	}
	
	public T getSelection() {
		return this.selected;
	}
}
