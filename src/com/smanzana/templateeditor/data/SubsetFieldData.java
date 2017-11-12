package com.smanzana.templateeditor.data;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.GrabListField;

/**
 * FieldData that represents a subset selection
 * @author Skyler
 *
 */
public final class SubsetFieldData<T> extends FieldData {
	
	private List<T> options;
	private List<T> selected;
	private GrabListField.DisplayFormatter<T> formatter;
	
	public SubsetFieldData(List<T> options, GrabListField.DisplayFormatter<T> formatter) {
		this(options, formatter, null);
	}
	
	public SubsetFieldData(List<T> allOptions, GrabListField.DisplayFormatter<T> formatter,
			List<T> currentSubset) {
		this.options = allOptions;
		this.formatter = formatter;
		this.selected = currentSubset;
	}

	public GrabListField.DisplayFormatter<T> getFormatter() {
		return formatter;
	}
	
	@Override
	public FieldData clone() {
		List<T> allOptions = new LinkedList<>();
		for (T t : options)
			allOptions.add(t);
		List<T> selected = new LinkedList<>();
		for (T t : selected)
			selected.add(t);
		
		return new SubsetFieldData<T>(allOptions, formatter, selected);
	}

	@Override
	public EditorField<?> constructField() {
		return new GrabListField<T>(options, formatter, selected);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fillFromField(EditorField<?> field) {
		selected = (List<T>) field.getObject();
	}
	
	public List<T> getSelection() {
		return selected;
	}
}
