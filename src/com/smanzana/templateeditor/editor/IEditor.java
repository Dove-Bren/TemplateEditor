package com.smanzana.templateeditor.editor;

import java.util.Map;

import javax.swing.JComponent;

import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.api.ObjectDataLoader;
import com.smanzana.templateeditor.editor.fields.EditorField;

public interface IEditor<T> {
	
	public static class DataPair<T> {
		private FieldData data;
		private EditorField<?> field;
		
		public DataPair(FieldData data, EditorField<?> field) {
			this.data = data;
			this.field = field;
		}

		public FieldData getData() {
			return data;
		}

		public EditorField<?> getField() {
			return field;
		}
	}
	
	public static <T> TemplateEditor<Integer> createTemplateEditor(IEditorOwner owner, ObjectDataLoader<T> loader) {
		return new TemplateEditor<>(owner, loader.getFieldMap());
	}
	
//	public static <T> TemplateEditor<Integer> createTemplateEditor(IEditorOwner owner, T obj) {
//		return new TemplateEditor<>(owner, 
//				new ObjectDataLoader<>(obj).getFieldMap());
//	}
	
	/**
	 * Returns JSwing component that will represent this editor and all nested fields
	 * @return
	 */
	public JComponent getComponent();
	
	/**
	 * Returns finalized copy of map which maps input to a piece of FieldData.
	 * @return
	 */
	public Map<T, FieldData> fetchData();

}
