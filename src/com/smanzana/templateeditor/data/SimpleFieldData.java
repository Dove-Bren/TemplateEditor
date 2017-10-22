package com.smanzana.templateeditor.data;

import java.util.List;

import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.editor.fields.BoolField;
import com.smanzana.templateeditor.editor.fields.DoubleField;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.IntField;
import com.smanzana.templateeditor.editor.fields.TextField;

/**
 * FieldData for simple data types.
 * You should not be creating this by yourself. Use the static helper
 * functions in {@link FieldData}
 * @author Skyler
 *
 */
public final class SimpleFieldData extends FieldData {
	
	/**
	 * Enumeration of all basic types.
	 * @author Skyler
	 *
	 */
	public enum FieldType {
		
		/** Basic boolean */
		BOOL,
		
		/** Basic integer */
		INT,
		
		/** Basic double */
		DOUBLE,
		
		/** Regular string */
		STRING,

		/** List of basic integers */
		LIST_INT,
		
		/** List of basic doubles */
		LIST_DOUBLE,
		
		/** List of strings */
		LIST_STRING,
	}
	
	/** Type this data is built on */
	private FieldType type;
	
	/** 
	 * The current value of the field. Updated once the editor passes
	 * back values as finalized.
	 */
	private Object value;
	
	public SimpleFieldData(FieldType type, Object value) {
		super();
		this.type = type;
		setValue(value);
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		if (assertType(value))
			this.value = value;
		else
			throw new RuntimeException(new ClassCastException("Invalid type for SimpleFieldData with type "
					+ type.name() + ": " + value));
	}
	
	public FieldType getType() {
		return type;
	}
	
	// true if correct. False is ERROR
	private boolean assertType(Object value) {
		switch (type) {
		case BOOL:
			return (value instanceof Boolean);
		case DOUBLE:
			return (value instanceof Double);
		case INT:
			return (value instanceof Integer);
		case LIST_DOUBLE:
		case LIST_INT:
		case LIST_STRING:
			return value instanceof List<?>;
		case STRING:
			return (value instanceof String);
		}
		
		return false;
	}

	@Override
	public FieldData clone() {
		return new SimpleFieldData(type, value).name(getName()).desc(getDescription());
	}

	@Override
	public EditorField<?> constructField() {
		EditorField<?> comp = null;
		switch (type) {
		case BOOL:
			comp = new BoolField("DELETE ME", (Boolean) value);
			break;
		case DOUBLE:
			comp = new DoubleField("DELETE ME", (Double) value);
			break;
		case INT:
			comp = new IntField("DELETE ME", (Integer) value);
			break;
		case STRING:
			comp = new TextField("DELETE ME", (String) value);
			break;
		case LIST_DOUBLE:
			break;
		case LIST_INT:
			break;
		case LIST_STRING:
			break;
		}
		
		return comp;
	}
}
