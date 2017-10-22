package com.smanzana.templateeditor.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.api.IEditorDisplayFormatter;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.NestedEditorField;
import com.smanzana.templateeditor.editor.fields.NestedEditorListField;

/**
 * Bundle of data for a field
 * @author Skyler
 *
 */
public final class ComplexFieldData extends FieldData {
	
	/** Complex type nested values for COMPLEX or LIST_COMPLEX fieldtypes */
	private Map<Integer, FieldData> nestedTypes;
	
	/** Formatter for complex data types */
	private IEditorDisplayFormatter<Integer> formatter;
	
	private List<Map<Integer, FieldData>> listValue;
	
	public ComplexFieldData(Map<Integer, FieldData> submap, IEditorDisplayFormatter<Integer> formatter) {
		this(submap, formatter, null);
	}
	
	public ComplexFieldData(Map<Integer, FieldData> submap, IEditorDisplayFormatter<Integer> formatter,
			List<Map<Integer, FieldData>> listValue) {
		this.nestedTypes = submap;
		this.formatter = formatter;
		this.listValue = listValue;
	}

	public Map<Integer, FieldData> getNestedTypes() {
		return nestedTypes;
	}
	
	public IEditorDisplayFormatter<Integer> getFormatter() {
		return formatter;
	}
	
	@Override
	public FieldData clone() {
		Map<Integer, FieldData> cloneNestedTypes = null;
		if (nestedTypes != null) {
			cloneNestedTypes = new HashMap<>();
			for (Integer key : nestedTypes.keySet()) {
				cloneNestedTypes.put(key, nestedTypes.get(key).clone());
			}
		}
		return new ComplexFieldData(cloneNestedTypes, formatter, listValue).name(getName()).desc(getDescription());
	}

	@Override
	public EditorField<?> constructField() {
		if (listValue == null) {
			// Single complex.
			EditorField<Map<Integer, FieldData>> comp = new NestedEditorField("DELETE ME", nestedTypes, formatter);
			comp.setObject(nestedTypes);
			return comp;
		}
		else {
			return new NestedEditorListField("DELETE ME", nestedTypes, listValue, formatter);
		}
	}
}
