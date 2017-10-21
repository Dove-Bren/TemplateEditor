package com.smanzana.templateeditor.editor;

import java.util.Map;

import javax.swing.JComponent;

import com.smanzana.templateeditor.FieldData;
import com.smanzana.templateeditor.editor.fields.EditorField;

public interface IEditor<T> {
	
	public static class DataPair<T> {
		private FieldData<T> data;
		private EditorField<?> field;
		
		public DataPair(FieldData<T> data, EditorField<?> field) {
			this.data = data;
			this.field = field;
		}

		public FieldData<T> getData() {
			return data;
		}

		public EditorField<?> getField() {
			return field;
		}
	}
	
	/**
	 * Returns JSwing component that will represent this editor and all nested fields
	 * @return
	 */
	public JComponent getComponent();
	
	/**
	 * Returns finalized copy of map which maps input to a piece of FieldData 
	 * @return
	 */
	public Map<T, FieldData<T>> fetchData();

}
