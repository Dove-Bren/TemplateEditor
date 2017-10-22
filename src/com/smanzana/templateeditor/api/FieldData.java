package com.smanzana.templateeditor.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.smanzana.templateeditor.data.ComplexFieldData;
import com.smanzana.templateeditor.data.SimpleFieldData;
import com.smanzana.templateeditor.data.SimpleFieldData.FieldType;
import com.smanzana.templateeditor.editor.fields.EditorField;

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
	
//	/**
//	 * When in doubt, use the static helper constructors:
//	 * <ul>
//	 * <li>{@link #simple(FieldType, Object)}</li>
//	 * <li>{@link #complex(Map, Map)}</li>
//	 * <li>{@link #complexList(Map, List)}</li>
//	 * <li>{@link #user(IUserData, Object)}</li>
//	 * </ul>
//	 * @param type
//	 * @param userDataType
//	 * @param nestedTypes
//	 * @param value
//	 */
//	private <U> FieldData(FieldType type, IUserData<U> userDataType, Map<Integer, FieldData> nestedTypes,
//			IEditorDisplayFormatter<Integer> formatter, Object value) {
//		super();
//		this.type = type;
//		this.userDataType = userDataType;
//		this.value = value;
//		this.nestedTypes = nestedTypes;
//		this.formatter = formatter;
//	}
//	
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
	
	public static ComplexFieldData complex(Map<Integer, FieldData> subfields,
			IEditorDisplayFormatter<Integer> formatter) {
		return new ComplexFieldData(subfields, formatter);
	}
	
	public static ComplexFieldData complexList(Map<Integer, FieldData> subfields,
			IEditorDisplayFormatter<Integer> formatter) {
		return complexList(subfields, formatter, new LinkedList<Map<Integer, FieldData>>());
	}
	
	public static ComplexFieldData complexList(Map<Integer, FieldData> subfields,
			IEditorDisplayFormatter<Integer> formatter, List<Map<Integer, FieldData>> startValue) {
		return new ComplexFieldData(subfields, formatter, startValue);
	}
	
	public FieldData description(String description) {
		if (this.description == null)
			this.description = new LinkedList<>();
		
		if (description != null)
			this.description.add(description);
		return this;
	}
	
	public FieldData desc(String description) {
		return description(description);
	}
	
	public FieldData description(List<String> descriptions) {
		if (description != null)
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
