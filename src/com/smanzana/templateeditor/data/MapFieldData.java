package com.smanzana.templateeditor.data;

import java.util.HashMap;
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
	
	public MapFieldData(Map<K,FieldData> map) {
		this.map = map;
	}
	
	@Override
	public FieldData clone() {
		Map<K, FieldData> cloneNestedTypes = new HashMap<>();
		for (K key : map.keySet()) {
			cloneNestedTypes.put(key, map.get(key).clone());
		}
		return new MapFieldData<K>(cloneNestedTypes);
	}

	@Override
	public EditorField<?> constructField() {
		return new MapField<K>(map);
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
