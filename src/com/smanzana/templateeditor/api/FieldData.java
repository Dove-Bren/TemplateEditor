package com.smanzana.templateeditor.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.templateeditor.data.ComplexFieldData;
import com.smanzana.templateeditor.data.CustomFieldData;
import com.smanzana.templateeditor.data.EnumFieldData;
import com.smanzana.templateeditor.data.SimpleFieldData;
import com.smanzana.templateeditor.data.SimpleFieldData.FieldType;
import com.smanzana.templateeditor.data.SubsetFieldData;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.GrabListField;

/**
 * Bundle of data for a field
 * @author Skyler
 *
 */
public abstract class FieldData implements Cloneable {
	
	/** 
	 * Optional name to be displayed in the editor.
	 * This doesn't mean anything on USER types
	 */
	private String name;
	
	/**
	 * Optional description. Usually displayed as a tooltip.
	 */
	private List<String> description;
	
	public static SimpleFieldData simple(FieldType type, Object value) {
		return new SimpleFieldData(type, value);
	}
	
	public static SimpleFieldData simple(boolean value) {
		return new SimpleFieldData(FieldType.BOOL, value);
	}
	
	public static SimpleFieldData simple(int value) {
		return new SimpleFieldData(FieldType.INT, value);
	}
	
	public static SimpleFieldData simple(double value) {
		return new SimpleFieldData(FieldType.DOUBLE, value);
	}
	
	public static SimpleFieldData simple(String value) {
		return new SimpleFieldData(FieldType.STRING, value);
	}
	
	public static SimpleFieldData listInt(List<Integer> value) {
		return new SimpleFieldData(FieldType.LIST_INT, value);
	}
	
	public static SimpleFieldData listDouble(List<Double> value) {
		return new SimpleFieldData(FieldType.LIST_DOUBLE, value);
	}
	
	public static SimpleFieldData listString(List<String> value) {
		return new SimpleFieldData(FieldType.LIST_STRING, value);
	}
	
	public static <T extends Enum<T>> EnumFieldData<T> enumSelection(T value) {
		return new EnumFieldData<>(value);
	}
	
	public static ComplexFieldData complex(Map<Integer, FieldData> subfields,
			IEditorDisplayFormatter<Integer> formatter) {
		return new ComplexFieldData(subfields, formatter);
	}
	
	public static <T> ComplexFieldData complexObject(ObjectDataLoader<T> loader) {
		return new ComplexFieldData(loader);
	}
	
	public static <T> ComplexFieldData complexObject(T object) {
		return complexObject(new ObjectDataLoader<T>(object));
	}
	
	public static ComplexFieldData complexList(Map<Integer, FieldData> subfields,
			IEditorDisplayFormatter<Integer> formatter) {
		return complexList(subfields, formatter, new LinkedList<Map<Integer, FieldData>>());
	}
	
	public static ComplexFieldData complexList(Map<Integer, FieldData> subfields,
			IEditorDisplayFormatter<Integer> formatter, List<Map<Integer, FieldData>> startValue) {
		return new ComplexFieldData(subfields, formatter, startValue);
	}
	
	public static <T> SubsetFieldData<T> subset(List<T> options, List<T> selected,
			GrabListField.DisplayFormatter<T> formatter) {
		return new SubsetFieldData<T>(options, formatter, selected);
	}
	
	public static CustomFieldData custom(ICustomData data) {
		return new CustomFieldData(data);
	}
	
	public static CustomFieldData customList(ICustomData base, List<ICustomData> activeList) {
		return new CustomFieldData(base, activeList);
	}
	
	public FieldData description(String description) {
		if (description == null)
			return this;
		
		if (this.description == null)
			this.description = new LinkedList<>();
		
		this.description.add(description);
		return this;
	}
	
	public FieldData desc(String description) {
		return description(description);
	}
	
	public FieldData description(List<String> descriptions) {
		if (descriptions != null)
			for (String s : descriptions)
				desc(s);
		
		return this;
	}
	
	public FieldData desc(List<String> descriptions) {
		return description(descriptions);
	}
	
	public FieldData name(String name) {
		this.name = name;
		return this;
	}

	public String getName() {
		return name;
	}

	public List<String> getDescription() {
		return description;
	}
	
	public String getFormattedDescription() {
		if (description == null || description.isEmpty())
			return null;
		
		String buf = "<html>" + description.get(0);
		for (int i = 1; i < description.size(); i++) {
			buf += "<br />" + description.get(i);
		}
		
		return buf;
	}
	
	@Override
	public abstract FieldData clone();
	
	/**
	 * Create an {@link EditorField} to be used to edit this field.
	 * @return
	 */
	public abstract EditorField<?> constructField();
	
	/**
	 * Takes a field (should be same produced with {@link #constructField()}
	 * and modifies internal data to match. That is, takes state of the field
	 * and mirrors it in internal data.<br />
	 * This is called, for example, right before returning finalized data from the editor.
	 * @param field
	 */
	public abstract void fillFromField(EditorField<?> field);
}
