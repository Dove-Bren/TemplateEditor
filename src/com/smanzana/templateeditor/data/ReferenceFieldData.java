package com.smanzana.templateeditor.data;

import java.util.LinkedHashMap;
import java.util.Map;

import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.SelectionField;

/**
 * FieldData that represents a 'reference'. In other words, field data
 * that has a reference to another piece of arbitrary data.
 * The field this data produces lets you pick from a list of all available values
 * for some subset.
 * @author Skyler
 *
 */
public final class ReferenceFieldData<T> extends FieldData {
	
	private T selected;
	private Map<String, T> valueMap;
	
	public ReferenceFieldData(Map<String, T> valueMap, T current) {
		this.selected = current;
		this.valueMap = valueMap;
	}

	@Override
	public FieldData clone() {
		T selected = this.selected;
		Map<String, T> valueMap = new LinkedHashMap<>(this.valueMap);
		
		return new ReferenceFieldData<T>(valueMap, selected);
	}

	@Override
	public EditorField<?> constructField() {
		return new SelectionField<T>(valueMap, selected);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fillFromField(EditorField<?> field) {
		selected = (T) field.getObject();
		if (!valueMap.values().contains(selected))
			selected = null;
	}
	
	public T getSelection() {
		return this.selected;
	}
}
