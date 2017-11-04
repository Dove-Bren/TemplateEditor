package com.smanzana.templateeditor.editor.fields;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;

import com.smanzana.templateeditor.IEditorOwner;
import com.smanzana.templateeditor.api.FieldData;
import com.smanzana.templateeditor.editor.TemplateEditor;
import com.smanzana.templateeditor.uiutils.TextUtil;
import com.smanzana.templateeditor.uiutils.UIColor;

/**
 * Like a list, but all entries are known at time of creation. Also, each
 * entry has a key and a value.
 * MapFields do not support adding new mappings.
 * @author Skyler
 */
public class MapField<K> extends AEditorField<Map<K, FieldData>> implements IEditorOwner {
	
	private JPanel wrapper;
	//private JPanel dataList; // components are STRICTLY EntryItem
	//private Map<K, EditorField<?>> fieldMap;
	//private Map<K, EntryItem> listMap;
	private TemplateEditor<K> editor;
	
	public MapField(Map<K, FieldData> fields) {
		
		wrapper = new JPanel();
		wrapper.setLayout(new BorderLayout());
		UIColor.setColors(wrapper, UIColor.Key.EDITOR_MAIN_PANE_FOREGROUND, UIColor.Key.EDITOR_MAIN_PANE_BACKGROUND);
		
		setObject(fields);
		
		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}
	
//	private void breakdown(Map<K, V> fields) {
//		for (Entry<K, V> row : fields.entrySet()) {
//			EditorField<?> editor = row.getValue().constructField();
//			EntryItem item = new EntryItem(new BorderLayout());
//			item.add(editor.getComponent(), BorderLayout.CENTER);
//			Dimension pref = item.getPreferredSize();
//			item.setMaximumSize(new Dimension(Short.MAX_VALUE, pref.height));
//			item.setMinimumSize(new Dimension(20, pref.height));
//			editor.setOwner(this.getOwner());
//			fieldMap.put(row.getKey(), editor);
//			listMap.put(row.getKey(), item);
//			wrapper.add(item, wrapper.getComponentCount() - 1);
//		}
//
//		dataMap = fields;
//	}
	
	@Override
	public Map<K,FieldData> getObject() {
		return editor.fetchData();
	}

	@Override
	protected void setCurrentObject(Map<K,FieldData> map) {
		if (editor != null) {
			wrapper.remove(editor.getComponent());
		}
		
		// Just for you <3 jk just for me <3
		
		for (K key : map.keySet()) {
			FieldData data = map.get(key);
			if (data.getName() == null || data.getName().trim().isEmpty())
				data.name(TextUtil.pretty(key.toString()));
		}
		
		editor = new TemplateEditor<K>(this, map);
		wrapper.add(editor.getComponent());
	}
	
	@Override
	public void dirty() {
		this.markDirty();
	}
	
}
