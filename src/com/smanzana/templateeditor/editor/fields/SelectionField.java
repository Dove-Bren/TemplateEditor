package com.smanzana.templateeditor.editor.fields;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.smanzana.templateeditor.uiutils.TextUtil;

/**
 * Field that allows you to select a value from an available pool
 * of options. Like a combo box, but object-backed.
 * SelectionFields support a 'non-valid' selection; no auto-swapping of values
 * occurs if the passed in 'startSelection' is not one of the potential values.
 * Instead, the selection field rolls with it and even potentially returns it.
 * It's up to data viewers to perform the check.
 * @author Skyler
 *
 * @param <T> Type selected. If no list of names is passed, toString is used
 * to display.
 */
public class SelectionField<T> extends AEditorField<T> implements ItemListener {

	private JComboBox<String> combo;
	private JPanel wrapper;
	private Map<T, String> prettyMap;
	private boolean hasInvalid;
	private T invalid;

	private SelectionField() {
		prettyMap = new HashMap<>();
		hasInvalid = false;
		
		wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.LINE_AXIS));
		wrapper.add(Box.createRigidArea(new Dimension(10, 0)));
		
		combo = new JComboBox<>();
		combo.setMinimumSize(new Dimension(50, 20));
		combo.setMaximumSize(new Dimension(300, 20));
		combo.setPreferredSize(new Dimension(200, 20));
		
		combo.addItemListener(this);
		wrapper.add(combo);
		wrapper.add(Box.createRigidArea(new Dimension(20, 0)));
		wrapper.add(Box.createHorizontalGlue());
		
		wrapper.validate();
	}
	
	public SelectionField(Map<String, T> namesToValues, T startSelection) {
		this();
		
		setup(namesToValues, startSelection);
	}
	
	public SelectionField(Collection<T> values, T startSelection) {
		this();
		
		Map<String, T> dump = new HashMap<>();
		int i = 0;
		for (T val : values)
			dump.put("" + i++, val);
		
		setup(dump, startSelection);
	}
	
	private void setup(Map<String, T> namesToValues, T startSelection) {
		
		if (!namesToValues.values().contains(startSelection)) {
			String pretty = invalidateName(TextUtil.pretty(startSelection.toString()));
			prettyMap.put(startSelection, pretty);
			combo.addItem(pretty);
			hasInvalid = true;
			invalid = startSelection;
		}
		
		for (Entry<String, T> row : namesToValues.entrySet()) {
			String pretty = (row.getKey() == null ? 
					TextUtil.pretty(row.getValue().toString()) : row.getKey());
			prettyMap.put(row.getValue(), pretty);
			combo.addItem(pretty);
		}
		
		this.setObject(startSelection);
		
//		for (Entry<String, T> row : namesToValues.entrySet()) {
//			prettyMap.put(row.getValue(), row.getKey());
//		}
		
		wrapper.validate();
	}
	
	public JPanel getComponent() {
		return wrapper;
	}

	private T lookup(String pretty) {
		for (T t : prettyMap.keySet())
			if (prettyMap.get(t).equals(pretty))
				return t;
		
		return null;
	}

	@Override
	public T getObject() {
		return lookup((String) combo.getSelectedItem());
	}

	@Override
	protected void setCurrentObject(T obj) {
		if (obj == null) {
			combo.setSelectedIndex(0);
		} else {
			T current = lookup((String) combo.getSelectedItem());
			if (obj == current)
				return;
			
			if (hasInvalid) {
				combo.remove(0);
				prettyMap.remove(invalid);
				hasInvalid = false;
				if (combo.getSelectedIndex() > 0)
					combo.setSelectedIndex(combo.getSelectedIndex() - 1);
			}
			
			if (prettyMap.keySet().contains(obj)) {
				// not an invalid value
				String name = prettyMap.get(obj);
				combo.setSelectedItem(name);
			} else {
				hasInvalid = true;
				String name = invalidateName(TextUtil.pretty(obj.toString()));
				prettyMap.put(obj, name);
				combo.insertItemAt(name, 0);
			}
		}
	}
	
	private String invalidateName(String name) {
		return "X> " + name + " <X";
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() != ItemEvent.SELECTED)
			return;
		
		if (hasInvalid) {
			int i = combo.getSelectedIndex();
			if (i > 0) {
				i--;
				combo.removeItemAt(0);
				combo.setSelectedIndex(i);
				hasInvalid = false;
			}
		}
		markDirty();
	}
}
