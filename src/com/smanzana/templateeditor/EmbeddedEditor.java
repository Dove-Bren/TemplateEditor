package com.smanzana.templateeditor;

import javax.swing.JComponent;

import com.smanzana.templateeditor.api.ObjectDataLoader;
import com.smanzana.templateeditor.editor.IEditor;

public class EmbeddedEditor<T> {
	
	private IEditor<Integer> editor;
	private ObjectDataLoader<T> loader;
	
	public EmbeddedEditor(T object, IEditorOwner owner) {
		loader = new ObjectDataLoader<>(object);
		editor = IEditor.createTemplateEditor(owner, loader);
	}
	
	public JComponent getEditorPanel() {
		return editor.getComponent();
	}
	
	public T commit() {
		loader.updateData(editor.fetchData());
		return loader.fetchEdittedValue();
	}
	
}
