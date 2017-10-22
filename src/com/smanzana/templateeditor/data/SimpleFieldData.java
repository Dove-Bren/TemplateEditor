package com.smanzana.templateeditor.data;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.editor.fields.BoolField;
import com.smanzana.templateeditor.editor.fields.DoubleField;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.GenericListField;
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
		SimpleFieldData base = null;
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
			base = new SimpleFieldData(FieldType.DOUBLE, 0.0);
			// Intentional fallthrough
		case LIST_INT:
			base = new SimpleFieldData(FieldType.INT, 0);
		case LIST_STRING:
			base = new SimpleFieldData(FieldType.STRING, "");
			
			//Fallthrough for lists:
			comp = new GenericListField<SimpleFieldData>("DELETE ME", base,
					(List<SimpleFieldData>) toList());
		}
		
		return comp;
	}
	
	@SuppressWarnings("unchecked")
	private List<SimpleFieldData> toList() {
		// Assumes value is list of primitive type
		List<SimpleFieldData> list = new LinkedList<>();
		
		switch (type) {
		case LIST_DOUBLE:
			for (Double i : (List<Double>) value) {
				list.add(FieldData.simple(i));
			}
			break;
		case LIST_INT:
			for (Integer i : (List<Integer>) value) {
				list.add(FieldData.simple(i));
			}
			break;
		case LIST_STRING:
			for (String i : (List<String>) value) {
				list.add(FieldData.simple(i));
			}
			break;
		default:
			break;
		}
		
		return list;
	}

	@Override
	public void fillFromField(EditorField<?> field) {
		setValue(field.getObject());
	}
}
