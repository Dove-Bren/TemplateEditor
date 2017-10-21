package com.smanzana.templateeditor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Bundle of data for a field
 * @author Skyler
 *
 */
public class FieldData implements Cloneable {
	
	/** Type this data is built on */
	private FieldType type;
	
	/** If type is UserData, instance to use for field creation */
	private IUserData<?> userDataType;
	
	
	/** Complex type nested values for COMPLEX or LIST_COMPLEX fieldtypes */
	private Map<Integer, FieldData> nestedTypes;
	
	/** Formatter for complex data types */
	private IEditorDisplayFormatter<Integer> formatter;
	
	/** 
	 * The current value of the field. Updated once the editor passes
	 * back values as finalized.
	 */
	private Object value;
	
	/** 
	 * Optional name to be displayed in the editor.
	 * This doesn't mean anything on USER types
	 */
	private String name;
	
	/**
	 * Optional description. Usually displayed as a tooltip.
	 */
	private List<String> description;
	
	/**
	 * When in doubt, use the static helper constructors:
	 * <ul>
	 * <li>{@link #simple(FieldType, Object)}</li>
	 * <li>{@link #complex(Map, Map)}</li>
	 * <li>{@link #complexList(Map, List)}</li>
	 * <li>{@link #user(IUserData, Object)}</li>
	 * </ul>
	 * @param type
	 * @param userDataType
	 * @param nestedTypes
	 * @param value
	 */
	private <U> FieldData(FieldType type, IUserData<U> userDataType, Map<Integer, FieldData> nestedTypes,
			IEditorDisplayFormatter<Integer> formatter, Object value) {
		super();
		this.type = type;
		this.userDataType = userDataType;
		this.value = value;
		this.nestedTypes = nestedTypes;
		this.formatter = formatter;
	}
	
	public static FieldData simple(FieldType type, Object value) {
		return new FieldData(type, null, null, null, value);
	}
	
	public static FieldData simple(boolean value) {
		return new FieldData(FieldType.BOOL, null, null, null, value);
	}
	
	public static FieldData simple(int value) {
		return new FieldData(FieldType.INT, null, null, null, value);
	}
	
	public static FieldData simple(double value) {
		return new FieldData(FieldType.DOUBLE, null, null, null, value);
	}
	
	public static FieldData simple(String value) {
		return new FieldData(FieldType.STRING, null, null, null, value);
	}
	
	public static FieldData complex(Map<Integer, FieldData> subfields,
			IEditorDisplayFormatter<Integer> formatter) {
		return new FieldData(FieldType.COMPLEX, null, subfields, formatter, null);
	}
	
	public static FieldData complexList(Map<Integer, FieldData> subfields,
			IEditorDisplayFormatter<Integer> formatter) {
		return complexList(subfields, formatter, null);
	}
	
	public static FieldData complexList(Map<Integer, FieldData> subfields,
			IEditorDisplayFormatter<Integer> formatter, List<Map<Integer, FieldData>> startValue) {
		return new FieldData(FieldType.LIST_COMPLEX, null, subfields, formatter, startValue);
	}
	
	public static <T> FieldData user(IUserData<T> template, T value) {
		return new FieldData(FieldType.USER, template, null, null, value);
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

	public FieldType getType() {
		return type;
	}

	public IUserData<?> getUserDataType() {
		return userDataType;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Map<Integer, FieldData> getNestedTypes() {
		return nestedTypes;
	}
	
	public IEditorDisplayFormatter<Integer> getFormatter() {
		return formatter;
	}

	public String getName() {
		return name;
	}

	public List<String> getDescription() {
		return description;
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
		return new FieldData(type, userDataType, cloneNestedTypes, formatter, value).name(name).desc(description);
	}
}
