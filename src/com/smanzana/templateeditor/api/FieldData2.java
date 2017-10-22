package com.smanzana.templateeditor.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Bundle of data for a field
 * @author Skyler
 *
 */
public class FieldData2 implements Cloneable {
	
	/** Type this data is built on */
	private FieldType type;
	
	/** If type is UserData, instance to use for field creation */
	private IUserData<?> userDataType;
	
	
	/** Complex type nested values for COMPLEX or LIST_COMPLEX fieldtypes */
	private Map<Integer, FieldData2> nestedTypes;
	
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
	private <U> FieldData2(FieldType type, IUserData<U> userDataType, Map<Integer, FieldData2> nestedTypes,
			IEditorDisplayFormatter<Integer> formatter, Object value) {
		super();
		this.type = type;
		this.userDataType = userDataType;
		this.value = value;
		this.nestedTypes = nestedTypes;
		this.formatter = formatter;
	}
	
	public static FieldData2 simple(FieldType type, Object value) {
		return new FieldData2(type, null, null, null, value);
	}
	
	public static FieldData2 simple(boolean value) {
		return new FieldData2(FieldType.BOOL, null, null, null, value);
	}
	
	public static FieldData2 simple(int value) {
		return new FieldData2(FieldType.INT, null, null, null, value);
	}
	
	public static FieldData2 simple(double value) {
		return new FieldData2(FieldType.DOUBLE, null, null, null, value);
	}
	
	public static FieldData2 simple(String value) {
		return new FieldData2(FieldType.STRING, null, null, null, value);
	}
	
	public static FieldData2 complex(Map<Integer, FieldData2> subfields,
			IEditorDisplayFormatter<Integer> formatter) {
		return new FieldData2(FieldType.COMPLEX, null, subfields, formatter, null);
	}
	
	public static FieldData2 complexList(Map<Integer, FieldData2> subfields,
			IEditorDisplayFormatter<Integer> formatter) {
		return complexList(subfields, formatter, null);
	}
	
	public static FieldData2 complexList(Map<Integer, FieldData2> subfields,
			IEditorDisplayFormatter<Integer> formatter, List<Map<Integer, FieldData2>> startValue) {
		return new FieldData2(FieldType.LIST_COMPLEX, null, subfields, formatter, startValue);
	}
	
	public static <T> FieldData2 user(IUserData<T> template, T value) {
		return new FieldData2(FieldType.USER, template, null, null, value);
	}
	
	public FieldData2 description(String description) {
		if (this.description == null)
			this.description = new LinkedList<>();
		
		if (description != null)
			this.description.add(description);
		return this;
	}
	
	public FieldData2 desc(String description) {
		return description(description);
	}
	
	public FieldData2 description(List<String> descriptions) {
		if (description != null)
			for (String s : descriptions)
				desc(s);
		
		return this;
	}
	
	public FieldData2 desc(List<String> descriptions) {
		return description(descriptions);
	}
	
	public FieldData2 name(String name) {
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

	public Map<Integer, FieldData2> getNestedTypes() {
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
	public FieldData2 clone() {
		Map<Integer, FieldData2> cloneNestedTypes = null;
		if (nestedTypes != null) {
			cloneNestedTypes = new HashMap<>();
			for (Integer key : nestedTypes.keySet()) {
				cloneNestedTypes.put(key, nestedTypes.get(key).clone());
			}
		}
		return new FieldData2(type, userDataType, cloneNestedTypes, formatter, value).name(name).desc(description);
	}
}
