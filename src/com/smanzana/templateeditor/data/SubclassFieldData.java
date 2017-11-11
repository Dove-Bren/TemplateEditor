package com.smanzana.templateeditor.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ListCellRenderer;

import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.editor.fields.ChildEditorField;
import com.smanzana.templateeditor.editor.fields.EditorField;

/**
 * Field data that defers to a
 * {@link com.smanzana.templateeditor.editor.fields.ChildEditorField ChildEditorField}.
 * Look there for a better explaination.
 * @author Skyler
 *
 */
public final class SubclassFieldData<T, O> extends FieldData {
	
	private Map<T, Map<Integer, FieldData>> dataMaps;
	private List<T> typeList;
	private ChildEditorField.GenericFactory<T, O> factory;
	
	// Optional
	private ChildEditorField.TypeResolver<T, O> resolver;
	private ListCellRenderer<T> formatter;
	
	private O lastObject;
	
	public SubclassFieldData(List<T> typeList,
			Map<T,Map<Integer, FieldData>> dataMaps,
			ChildEditorField.GenericFactory<T, O> factory) {
		this(typeList, dataMaps, factory, null);
	}
	
	public SubclassFieldData(List<T> typeList,
			Map<T,Map<Integer, FieldData>> dataMaps,
			ChildEditorField.GenericFactory<T, O> factory,
			ChildEditorField.TypeResolver<T, O> resolver) {
		this(typeList, dataMaps, factory, resolver, null);
	}
	
	public SubclassFieldData(List<T> typeList,
			Map<T,Map<Integer, FieldData>> dataMaps,
			ChildEditorField.GenericFactory<T, O> factory,
			ChildEditorField.TypeResolver<T, O> resolver,
			O current) {
		this(typeList, dataMaps, factory, resolver, null, current);
	}
	
	public SubclassFieldData(List<T> typeList,
			Map<T,Map<Integer, FieldData>> dataMaps,
			ChildEditorField.GenericFactory<T, O> factory,
			ChildEditorField.TypeResolver<T, O> resolver,
			ListCellRenderer<T> formatter,
			O current) {
		this.formatter = formatter;
		this.factory = factory;
		this.dataMaps = dataMaps;
		this.typeList = typeList;
		this.resolver = resolver;
		lastObject = current;
	}

	@Override
	public FieldData clone() {
		Map<T, Map<Integer, FieldData>> cloneMap;
		if (dataMaps.keySet().iterator().next() instanceof Comparable)
			cloneMap = new TreeMap<>();
		else
			cloneMap = new HashMap<>();
		
		for (T key : dataMaps.keySet()) {
			Map<Integer, FieldData> submap = new HashMap<>();
			
			Map<Integer, FieldData> nested = dataMaps.get(key);
			for (Integer i : nested.keySet()) {
				submap.put(i, nested.get(i).clone());
			}
			
			cloneMap.put(key, submap);
		}
		
		return new SubclassFieldData<T, O>(typeList, cloneMap,
				factory, resolver, formatter, factory.constructClone(lastObject));
	}

	@Override
	public EditorField<?> constructField() {
		return new ChildEditorField<T, O>(typeList, dataMaps, factory,
				lastObject, resolver, formatter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fillFromField(EditorField<?> field) {
		lastObject = ((ChildEditorField<T, O>) field).getObject();
	}
	
	public O getValue() {
		return lastObject;
	}
	
	// Don't do it.
	public void setValue(O obj) {
		this.lastObject = obj;
	}
}
