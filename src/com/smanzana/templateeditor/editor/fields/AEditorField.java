package com.smanzana.templateeditor.editor.fields;

import com.smanzana.templateeditor.IEditorOwner;

public abstract class AEditorField<T> implements EditorField<T> {

	private IEditorOwner owner;
	private T original;
	
	/**
	 * Set the current object to edit.
	 * Does all the work of setting the original (for default returning, etc)
	 * @param obj
	 */
	protected abstract void setCurrentObject(T obj);
	
	@Override
	public void setObject(T obj) {
		this.original = obj;
		this.setCurrentObject(obj);
	}
	
	@Override
	public T getOriginal() {
		return this.original;
	}
	
	@Override
	public void setOwner(IEditorOwner owner) {
		this.owner = owner;
	}
	
	protected IEditorOwner getOwner() {
		return this.owner;
	}
	
	/**
	 * Notify current owner that data has changed.
	 */
	protected void dirty() {
		if (owner != null)
			owner.dirty();
	}
	
}
