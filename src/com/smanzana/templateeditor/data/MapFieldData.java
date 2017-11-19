package com.smanzana.templateeditor.data;

import java.util.LinkedHashMap;
import java.util.Map;

import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.MapField;

/**
 * Bundle of map data for editting! Yay!
 * @author Skyler
 *
 */
public final class MapFieldData<K> extends FieldData {
	
	private Map<K,FieldData> map;
	private FieldData newTemplate;
	
	public MapFieldData(Map<K,FieldData> map, FieldData newTemplate) {
		this.map = map;
		this.newTemplate = newTemplate;
	}
	
	@Override
	public FieldData clone() {
		Map<K, FieldData> cloneNestedTypes = new LinkedHashMap<>();
		for (K key : map.keySet()) {
			cloneNestedTypes.put(key, map.get(key) == null ? null : map.get(key).clone());
		}
		return new MapFieldData<K>(cloneNestedTypes, newTemplate.clone());
	}

	@Override
	public EditorField<?> constructField() {
		return new MapField<K>(map, newTemplate);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void fillFromField(EditorField<?> field) {
		map = ((MapField<K>) field).getObject();
	}
	
	public Map<K, FieldData> getMapping() {
		return map;
	}
}
