package com.smanzana.templateeditor.editor.fields;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.smanzana.templateeditor.EditorIconRegistry;
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
	
	// Wrapper that allows nulling
	private class ValueButtonWrapper extends FieldData implements EditorField<FieldData> {

		private FieldData nested;
		private JPanel panel;
		private EditorField<?> lastEditor;
		private JComponent last;
		private JButton delButton;
		private JButton addButton;
		
		private IEditorOwner owner;
		private FieldData original;
		
		public ValueButtonWrapper(FieldData nested) {
			last = null;
			lastEditor = null;
			panel = new JPanel(new BorderLayout());
			delButton = new JButton(EditorIconRegistry.get(
					EditorIconRegistry.Key.X));
			
			delButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setObject(null);
				}
			});
			
			addButton = new JButton(EditorIconRegistry.get(
					EditorIconRegistry.Key.ADD));
			
			addButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setObject(newTemplate.clone());
				}
			});
			
			this.setObject(nested);
		}
		
		private void update() {
			if (nested != null) {
				panel.remove(addButton);
				addButton.setVisible(false);
				panel.add(delButton, BorderLayout.EAST);
				delButton.setVisible(true);
			} else {
				panel.remove(delButton);
				delButton.setVisible(false);
				panel.add(addButton, BorderLayout.EAST);
				addButton.setVisible(true);
			}
			panel.validate();
			panel.repaint();
			if (owner != null)
				owner.dirty();
		}
		
//		public boolean isNull() {
//			return this.nested == null;
//		}
		
		@Override
		public FieldData clone() {
			return new ValueButtonWrapper(nested.clone());
		}

		@Override
		public EditorField<?> constructField() {
			return this;
		}

		@Override
		public void fillFromField(EditorField<?> field) {
			System.out.println("Wrapper fill from field"); // We are the field
			//nested.fillFromField(last);
		}

		@Override
		public JComponent getComponent() {
			return panel;
		}

		@Override
		public FieldData getObject() {
			if (nested != null) 
				nested.fillFromField(lastEditor);
			return this.nested;
		}

		@Override
		public void setObject(FieldData obj) {
			if (this.last != null)
				panel.remove(this.last);
			this.nested = obj;
			this.original = obj == null ? null : obj.clone();
			
			if (obj != null) {
				lastEditor = obj.constructField();
				last = lastEditor.getComponent();
				panel.add(last, BorderLayout.CENTER);
			} else {
				last = null;
				lastEditor = null;
			}
			
			update();
		}

		@Override
		public FieldData getOriginal() {
			return this.original;
		}

		@Override
		public void setOwner(IEditorOwner owner) {
			this.owner = owner;
		}
	}
	
	private JPanel wrapper;
	private Map<K, ValueButtonWrapper> buttons;
	private TemplateEditor<K> editor;
	private FieldData newTemplate;
	
	public MapField(Map<K, FieldData> fields, FieldData newTemplate) {
		this.newTemplate = newTemplate;
		buttons = new HashMap<>();
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
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<K,FieldData> getObject() {
		Map<K, FieldData> wrappedMap = editor.fetchData();
		
		// Map is wrapped in ValueButtonWrappers. Need to unwrap
		Map<K, FieldData> map = new LinkedHashMap<>();
		for (K k : wrappedMap.keySet()) {
			ValueButtonWrapper wrapper = (ValueButtonWrapper) wrappedMap.get(k);
//			if (wrapper.isNull())
//				continue;
			map.put(k, wrapper.getObject());
		}
		
		return map;
	}

	@Override
	protected void setCurrentObject(Map<K,FieldData> map) {
		if (editor != null) {
			wrapper.remove(editor.getComponent());
		}
		
		Map<K, FieldData> newmap = new LinkedHashMap<>(map);
		for (K key : newmap.keySet()) {
			ValueButtonWrapper button = new ValueButtonWrapper(map.get(key));
			buttons.put(key, button);
			newmap.put(key, button);
		}
		
		// Just for you <3 jk just for me <3
		
		for (K key : newmap.keySet()) {
			FieldData data = newmap.get(key);
			if (data.getName() == null || data.getName().trim().isEmpty())
				data.name(TextUtil.pretty(key.toString()));
		}
		
		editor = new TemplateEditor<K>(this, newmap);
		wrapper.add(editor.getComponent());
	}
	
	@Override
	public void dirty() {
		this.markDirty();
	}
	
}
