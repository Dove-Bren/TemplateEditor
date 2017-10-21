package com.smanzana.templateeditor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Bundle of data for a field
 * @author Skyler
 *
 */
public class FieldData<T> {
	
	/** Structure for nested generic type information */
	private static class ComplexData<T> {
		/** Complex type nested values for COMPLEX or LIST_COMPLEX fieldtypes */
		public Map<T, FieldData<T>> nestedTypes;
		
		/** Formatter for complex data types */
		public IEditorDisplayFormatter<T> formatter;
		
		public ComplexData(Map<T, FieldData<T>> nestedTypes, IEditorDisplayFormatter<T> formatter) {
			this.nestedTypes = nestedTypes;
			this.formatter = formatter;
		}
	}
	
	/** Type this data is built on */
	private FieldType type;
	
	/** If type is UserData, instance to use for field creation */
	private IUserData<?> userDataType;
	
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
	
	private ComplexData<T> complexData;

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
	private <U> FieldData(FieldType type, IUserData<U> userDataType, Map<T, FieldData<T>> nestedTypes,
			IEditorDisplayFormatter<T> formatter, Object value) {
		super();
		this.type = type;
		this.userDataType = userDataType;
		this.value = value;
		
		this.complexData = new ComplexData<T>(nestedTypes, formatter);
	}
	
	public static FieldData<?> simple(FieldType type, Object value) {
		return new FieldData<Object>(type, null, null, null, value);
	}
	
	public static <T> FieldData<T> complex(Map<T, FieldData<T>> subfields, Map<T, FieldData<T>> value,
			IEditorDisplayFormatter<T> formatter) {
		return new FieldData<T>(FieldType.COMPLEX, null, subfields, formatter, value);
	}
	
	public static <T> FieldData<T> complexList(Map<T, FieldData<T>> subfields, List<Map<T, FieldData<T>>> value,
			IEditorDisplayFormatter<T> formatter) {
		return new FieldData<T>(FieldType.LIST_COMPLEX, null, subfields, formatter, value);
	}
	
	public static <T> FieldData<?> user(IUserData<T> template, T value) {
		return new FieldData<Object>(FieldType.USER, template, null, null, value);
	}
	
	public static <T> FieldData<T> description(FieldData<T> data, String description) {
		if (data.description == null)
			data.description = new LinkedList<>();
		
		data.description.add(description);
		return data;
	}
	
	public static <T> FieldData<T> desc(FieldData<T> data, String description) {
		return description(data, description);
	}
	
	public static <T> FieldData<T> description(FieldData<T> data, List<String> descriptions) {
		for (String s : descriptions)
			desc(data, s);
		
		return data;
	}
	
	public static <T> FieldData<T> desc(FieldData<T> data, List<String> descriptions) {
		return description(data, descriptions);
	}
	
	public static <T> FieldData<T> name(FieldData<T> data, String name) {
		data.name = name;
		return data;
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

	public Map<T, FieldData<T>> getNestedTypes() {
		return complexData.nestedTypes;
	}
	
	public IEditorDisplayFormatter<T> getFormatter() {
		return complexData.formatter;
	}

	public String getName() {
		return name;
	}

	public List<String> getDescription() {
		return description;
	}
}
